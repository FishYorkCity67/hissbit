package app.pyquest.js

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import app.pyquest.python.CodeRunner
import app.pyquest.python.ConsoleChunk
import app.pyquest.python.InputChannel
import app.pyquest.python.RunResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.ByteArrayInputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@Serializable
private data class RawJsResult(
    val stdout: String = "",
    val stderr: String = "",
    val aborted: String = "",
    val exitCode: Double? = 0.0,
    val error: RawJsError? = null,
    val ms: Long = 0,
)

/**
 * Führt JavaScript in einer unsichtbaren WebView aus (V8 – dieselbe Engine wie Chrome).
 * Jeder Lauf bekommt ein frisches iframe; Ausgabe, prompt(), Pausen und Abbruch laufen über
 * die Brücke HB. Hängt die Seite trotzdem fest (z. B. endlose Promise-Kette), wird der
 * Renderer-Prozess beendet und beim nächsten Lauf neu gestartet.
 */
class JsRunner(context: Context) : CodeRunner {
    private val appContext = context.applicationContext
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
    private val mutex = Mutex()
    private val runIds = AtomicInteger(0)

    private val _version = MutableStateFlow<String?>(null)
    override val version: StateFlow<String?> = _version.asStateFlow()
    override val languageName: String = "JavaScript"

    /** Sprache für Fehlerhinweise ("de" oder "en"). */
    @Volatile
    var hintLanguage: String = "de"

    @Volatile
    private var active: ActiveRun? = null

    @Volatile
    private var interactiveRunning = false

    /** Nur auf dem Main-Thread anfassen. */
    private var engine: Engine? = null

    private class ActiveRun(val id: Int, val input: InputChannel?, val onOutput: ((ConsoleChunk) -> Unit)?) {
        val result = CompletableDeferred<String?>()
        val cancelLatch = CountDownLatch(1)

        @Volatile
        var cancelled = false

        @Volatile
        var cancelAt = 0L
    }

    private class Engine(val webView: WebView) {
        val ready = CompletableDeferred<Unit>()

        @Volatile
        var dead = false
    }

    /** Wird aus JavaScript aufgerufen (Thread "JavaBridge"). Methodennamen nicht ändern – siehe host.js. */
    private inner class Bridge(private val engine: Engine) {
        @JavascriptInterface
        fun ready() {
            engine.ready.complete(Unit)
        }

        @JavascriptInterface
        fun out(id: Int, kind: Int, text: String) {
            val run = active ?: return
            if (run.id == id && !run.cancelled) run.onOutput?.invoke(ConsoleChunk(text, kind))
        }

        @JavascriptInterface
        fun readLine(id: Int, prompt: String): String? {
            val run = active ?: return null
            if (run.id != id || run.cancelled) return null
            return run.input?.read(prompt)
        }

        @JavascriptInterface
        fun sleep(id: Int, ms: Int): Boolean {
            val run = active ?: return true
            if (run.id != id) return true
            return run.cancelLatch.await(ms.toLong().coerceAtLeast(0), TimeUnit.MILLISECONDS) || run.cancelled
        }

        @JavascriptInterface
        fun poll(id: Int): Boolean {
            val run = active ?: return true
            return run.id != id || run.cancelled
        }

        @JavascriptInterface
        fun done(id: Int, result: String) {
            val run = active ?: return
            if (run.id == id) run.result.complete(result)
        }
    }

    override fun warmUp() {
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            runCatching { if (engine == null || engine?.dead == true) engine = createEngine() }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createEngine(): Engine {
        val wv = WebView(appContext)
        val e = Engine(wv)
        wv.settings.apply {
            javaScriptEnabled = true
            allowFileAccess = false
            allowContentAccess = false
            domStorageEnabled = false
            cacheMode = WebSettings.LOAD_NO_CACHE
            blockNetworkImage = true
            runCatching { blockNetworkLoads = true }
        }
        wv.addJavascriptInterface(Bridge(e), "HB")
        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean = true

            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                // Die Gastgeber-Seite selbst durchlassen, alles andere (Bilder, Skripte, fetch …) sperren.
                if (request.isForMainFrame) return null
                val scheme = request.url.scheme.orEmpty()
                if (scheme == "data" || scheme == "about" || scheme == "blob") return null
                return WebResourceResponse("text/plain", "utf-8", 403, "Blocked", emptyMap(), ByteArrayInputStream(ByteArray(0)))
            }

            override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
                engineDied(e)
                return true
            }

        }
        wv.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(message: ConsoleMessage): Boolean {
                Log.d("HissbitJS", "${message.message()} (${message.sourceId()}:${message.lineNumber()})")
                return true
            }
        }
        // Lokale Basisadresse – die App hat (gewollt) keine Internet-Berechtigung.
        wv.loadDataWithBaseURL("file:///android_asset/js/", hostPage(), "text/html", "utf-8", null)
        _version.value = "(V8)"
        return e
    }

    private fun hostPage(): String {
        val assets = appContext.assets
        val prelude = assets.open("js/prelude.js").bufferedReader().use { it.readText() }
        val host = assets.open("js/host.js").bufferedReader().use { it.readText() }
        val preludeLiteral = JsonPrimitive(prelude).toString().replace("</", "<\\/")
        return "<!doctype html><html><head><meta charset=\"utf-8\"></head><body>" +
            "<script>window.__HB_PRELUDE = $preludeLiteral;</script>" +
            "<script>${host.replace("</script", "<\\/script")}</script></body></html>"
    }

    private fun engineDied(e: Engine) {
        lastResetAt = SystemClock.elapsedRealtime()
        e.dead = true
        if (engine === e) engine = null
        runCatching { e.webView.destroy() }
        active?.result?.complete(CRASHED)
    }

    /** Zeitpunkt des letzten erzwungenen Neustarts (der alte Renderer braucht kurz zum Sterben). */
    @Volatile
    private var lastResetAt = 0L

    private suspend fun readyEngine(): Engine {
        repeat(2) { attempt ->
            val sinceReset = SystemClock.elapsedRealtime() - lastResetAt
            if (sinceReset < 1500) kotlinx.coroutines.delay(1500 - sinceReset)
            val e = withContext(Dispatchers.Main.immediate) {
                engine?.takeIf { !it.dead } ?: createEngine().also { engine = it }
            }
            val ok = withTimeoutOrNull(if (attempt == 0) 10_000L else 15_000L) { e.ready.await() } != null
            if (ok && !e.dead) return e
            hardReset()
        }
        throw IllegalStateException("WebView antwortet nicht")
    }

    private suspend fun hardReset() = withContext(Dispatchers.Main.immediate) {
        lastResetAt = SystemClock.elapsedRealtime()
        val e = engine ?: return@withContext
        engine = null
        e.dead = true
        runCatching { if (Build.VERSION.SDK_INT >= 29) e.webView.webViewRenderProcess?.terminate() }
        runCatching { e.webView.destroy() }
    }

    override suspend fun run(
        code: String,
        stdin: List<String>,
        timeoutSeconds: Double,
        input: InputChannel?,
        onOutput: ((ConsoleChunk) -> Unit)?,
    ): RunResult {
        // Ein Spielplatz-Programm (ohne Zeitlimit) darf Prüfungen in Lektionen nicht blockieren.
        if (input == null && interactiveRunning) cancel()
        return mutex.withLock { runLocked(code, stdin, timeoutSeconds, input, onOutput) }
    }

    private suspend fun runLocked(
        code: String,
        stdin: List<String>,
        timeoutSeconds: Double,
        input: InputChannel?,
        onOutput: ((ConsoleChunk) -> Unit)?,
    ): RunResult {
        val en = hintLanguage == "en"
        val e = try {
            readyEngine()
        } catch (t: Throwable) {
            hardReset()
            return RunResult.failure(if (en) "JavaScript could not be started: ${t.message}" else "JavaScript konnte nicht gestartet werden: ${t.message}")
        }
        val instr = JsInstrumenter.instrument(code)
        val run = ActiveRun(runIds.incrementAndGet(), input, onOutput)
        input?.reset()
        active = run
        interactiveRunning = input != null
        val timeoutMs = if (input != null) 0L else (timeoutSeconds * 1000).toLong()
        try {
            val payload = buildJsonObject {
                put("code", instr.code)
                put("stdin", JsonArray(stdin.map { JsonPrimitive(it) }))
                put("interactive", input != null)
                put("realtime", input != null || onOutput != null)
                put("stream", input != null || onOutput != null)
                put("timeoutMs", timeoutMs)
                put("lang", hintLanguage)
            }
            withContext(Dispatchers.Main.immediate) {
                e.webView.evaluateJavascript("__hbRun(${run.id}, $payload)", null)
            }
            val started = SystemClock.elapsedRealtime()
            var raw: String? = null
            var hung = false
            while (true) {
                val r = withTimeoutOrNull(200) { run.result.await() }
                if (r != null) {
                    raw = r
                    break
                }
                val now = SystemClock.elapsedRealtime()
                if (run.cancelled && now - run.cancelAt > 2500) {
                    hung = true
                    break
                }
                if (timeoutMs > 0 && now - started > timeoutMs + 3000) {
                    hung = true
                    break
                }
            }
            if (hung) {
                hardReset()
                return RunResult("", "", null, null, null, null, timedOut = !run.cancelled, cancelled = run.cancelled, durationMs = SystemClock.elapsedRealtime() - started)
            }
            if (raw == CRASHED) {
                return RunResult(
                    "", "",
                    if (en) "The program was stopped: it used too much memory or got stuck." else "Das Programm wurde beendet: Es hat zu viel Speicher gebraucht oder hing fest.",
                    "Crash", null, null, false, run.cancelled, SystemClock.elapsedRealtime() - started,
                )
            }
            return toResult(raw!!, code, instr, en)
        } catch (t: Throwable) {
            if (t is kotlinx.coroutines.CancellationException) {
                cancelRun(run)
                throw t
            }
            return RunResult.failure(if (en) "Internal error while running: ${t.message}" else "Interner Fehler beim Ausführen: ${t.message}")
        } finally {
            if (active === run) active = null
            interactiveRunning = false
        }
    }

    private fun toResult(raw: String, code: String, instr: JsInstrumenter.Result, en: Boolean): RunResult {
        val r = json.decodeFromString(RawJsResult.serializer(), raw)
        when (r.aborted) {
            "timeout" -> return RunResult(r.stdout, r.stderr, null, null, null, null, true, false, r.ms)
            "cancel" -> return RunResult(r.stdout, r.stderr, null, null, null, null, false, true, r.ms)
            "output" -> return RunResult(
                r.stdout, r.stderr,
                if (en) "Stopped: your program printed a huge amount of text (infinite loop?)." else "Abgebrochen: Dein Programm hat sehr viel Text ausgegeben (Endlosschleife?).",
                "OutputLimit", null, null, false, false, r.ms,
            )
            "exit" -> {
                val exit = r.exitCode
                if (exit != null && exit != 0.0) {
                    val shown = if (exit == Math.floor(exit)) exit.toLong().toString() else exit.toString()
                    return RunResult(r.stdout, r.stderr, "process.exit($shown)", "Exit", null, null, false, false, r.ms)
                }
                return RunResult(r.stdout, r.stderr, null, null, null, null, false, false, r.ms)
            }
        }
        val err = r.error ?: return RunResult(r.stdout, r.stderr, null, null, null, null, false, false, r.ms)
        val f = JsErrors.format(err, code, instr, if (en) "en" else "de")
        return RunResult(r.stdout, r.stderr, f.text, f.type, f.hint, f.line, false, false, r.ms)
    }

    private fun cancelRun(run: ActiveRun) {
        if (!run.cancelled) {
            run.cancelled = true
            run.cancelAt = SystemClock.elapsedRealtime()
        }
        run.cancelLatch.countDown()
        run.input?.cancel()
    }

    /** Bricht das laufende Programm ab (auch während prompt() wartet). */
    override fun cancel() {
        active?.let { cancelRun(it) }
    }

    private companion object {
        const val CRASHED = "\u0000crashed"
    }
}

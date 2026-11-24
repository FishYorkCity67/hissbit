package app.pyquest.python

import android.content.Context
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicLong

/** Wird von Python aus aufgerufen (siehe pyquest_runner.py). Nicht umbenennen! */
class PythonBridge(
    private val sink: (String, Int) -> Unit,
    private val reader: (String) -> String?,
) {
    fun write(text: String, kind: Int) = sink(text, kind)
    fun readLine(prompt: String): String? = reader(prompt)
}

/** Verbindet input() im Python-Programm mit einem Eingabefeld in der App. */
class InputChannel {
    private object Cancel

    private val queue = LinkedBlockingQueue<Any>()
    private val _waiting = MutableStateFlow<String?>(null)

    /** Prompt, solange das Programm auf eine Eingabe wartet, sonst null. */
    val waiting: StateFlow<String?> = _waiting.asStateFlow()

    internal fun read(prompt: String): String? {
        _waiting.value = prompt
        val value = queue.take()
        _waiting.value = null
        return value as? String
    }

    fun submit(line: String) {
        queue.offer(line)
    }

    fun cancel() {
        queue.offer(Cancel)
    }

    fun reset() {
        queue.clear()
        _waiting.value = null
    }
}

@Serializable
private data class RawResult(
    val stdout: String = "",
    val stderr: String = "",
    val error: String? = null,
    val errorType: String? = null,
    val hint: String? = null,
    val line: Int? = null,
    val timedOut: Boolean = false,
    val cancelled: Boolean = false,
    val ms: Long = 0,
)

data class RunResult(
    val stdout: String,
    val stderr: String,
    val error: String?,
    val errorType: String?,
    val hint: String?,
    val errorLine: Int?,
    val timedOut: Boolean,
    val cancelled: Boolean,
    val durationMs: Long,
) {
    val ok: Boolean get() = error == null && !timedOut && !cancelled

    companion object {
        fun failure(message: String) = RunResult("", "", message, "AppError", null, null, false, false, 0)
    }
}

/** Eine Ausgabe-Zeile der Konsole: 0 = stdout, 1 = stderr, 2 = Eingabe-Echo, 3 = Systemmeldung. */
data class ConsoleChunk(val text: String, val kind: Int)

class PythonRunner(context: Context) : CodeRunner {
    private val appContext = context.applicationContext
    private val executor = Executors.newSingleThreadExecutor { r ->
        Thread(null, r, "pyquest-python", 64L * 1024 * 1024).apply { isDaemon = true }
    }
    private val dispatcher = executor.asCoroutineDispatcher()
    private val json = Json { ignoreUnknownKeys = true }

    @Volatile
    private var module: PyObject? = null

    @Volatile
    private var activeInput: InputChannel? = null

    /** Kennung des laufenden Programms – damit ein Abbruch nur genau dieses trifft. */
    private val tokens = AtomicLong(0)

    @Volatile
    private var activeToken = 0L

    /** Läuft gerade ein interaktives Programm (Spielplatz, ohne Zeitlimit)? */
    @Volatile
    private var interactiveRunning = false

    private val _version = MutableStateFlow<String?>(null)
    val pythonVersion: StateFlow<String?> = _version.asStateFlow()
    override val version: StateFlow<String?> get() = pythonVersion
    override val languageName: String = "Python"

    /** Sprache für Fehlerhinweise aus Python ("de" oder "en"). */
    @Volatile
    var hintLanguage: String = "de"

    private fun module(): PyObject {
        module?.let { return it }
        synchronized(this) {
            module?.let { return it }
            if (!Python.isStarted()) Python.start(AndroidPlatform(appContext))
            val m = Python.getInstance().getModule("pyquest_runner")
            val workdir = File(appContext.filesDir, "python_files").absolutePath
            _version.value = m.callAttr("init", workdir).toString()
            module = m
            return m
        }
    }

    /** Startet Python im Hintergrund vor, damit der erste Lauf schnell ist. */
    override fun warmUp() {
        executor.execute { runCatching { module() } }
    }

    override suspend fun run(
        code: String,
        stdin: List<String>,
        timeoutSeconds: Double,
        input: InputChannel?,
        onOutput: ((ConsoleChunk) -> Unit)?,
    ): RunResult {
        // Ein Spielplatz-Programm ohne Zeitlimit (z.B. eine Endlosschleife oder ein wartendes
        // input()) darf Prüfungen in Lektionen nicht blockieren – es wird dann gestoppt.
        if (input == null && interactiveRunning) cancel()
        return withContext(dispatcher) {
            val m = try {
                module()
            } catch (t: Throwable) {
                return@withContext RunResult.failure(if (hintLanguage == "en") "Python could not be started: ${t.message}" else "Python konnte nicht gestartet werden: ${t.message}")
            }
            val token = tokens.incrementAndGet()
            activeToken = token
            interactiveRunning = input != null
            input?.reset()
            activeInput = input
            val bridge = PythonBridge(
                sink = { text, kind -> onOutput?.invoke(ConsoleChunk(text, kind)) },
                reader = { prompt -> input?.read(prompt) },
            )
            try {
                val stdinJson = json.encodeToString(ListSerializer(String.serializer()), stdin)
                val raw = m.callAttr(
                    "run",
                    code,
                    stdinJson,
                    input != null,
                    bridge,
                    if (input != null) 0.0 else timeoutSeconds,
                    token,
                    hintLanguage,
                ).toString()
                val r = json.decodeFromString(RawResult.serializer(), raw)
                RunResult(r.stdout, r.stderr, r.error, r.errorType, r.hint, r.line, r.timedOut, r.cancelled, r.ms)
            } catch (t: Throwable) {
                RunResult.failure(if (hintLanguage == "en") "Internal error while running: ${t.message}" else "Interner Fehler beim Ausführen: ${t.message}")
            } finally {
                activeInput = null
                interactiveRunning = false
            }
        }
    }

    /** Bricht das laufende Programm ab (auch während es auf input() wartet). */
    override fun cancel() {
        val token = activeToken
        activeInput?.cancel()
        Thread { runCatching { module?.callAttr("cancel", token) } }.start()
    }
}

/** Vergleicht Programmausgaben tolerant gegenüber Leerzeichen am Zeilenende. */
fun normalizeOutput(s: String): String =
    s.replace("\r\n", "\n").lines().joinToString("\n") { it.trimEnd() }.trim('\n')

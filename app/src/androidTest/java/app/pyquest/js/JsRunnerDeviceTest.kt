package app.pyquest.js

import android.os.SystemClock
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import app.pyquest.python.ConsoleChunk
import app.pyquest.python.InputChannel
import app.pyquest.python.RunResult
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prüft die JavaScript-Laufzeit in einer echten Android-WebView.
 * Start: adb shell am instrument -w -e class app.pyquest.js.JsRunnerDeviceTest app.pyquest.test/androidx.test.runner.AndroidJUnitRunner
 */
@RunWith(AndroidJUnit4::class)
class JsRunnerDeviceTest {
    private val runner = JsRunner(InstrumentationRegistry.getInstrumentation().targetContext)

    private fun run(code: String, stdin: List<String> = emptyList(), timeout: Double = 6.0): RunResult {
        val t0 = SystemClock.elapsedRealtime()
        val r = runBlocking { runner.run(code, stdin, timeout) }
        Log.i("JsRunnerTest", "${SystemClock.elapsedRealtime() - t0} ms: ${code.lineSequence().first()} -> out=${r.stdout.take(120)} err=${r.error?.take(300)} timedOut=${r.timedOut}")
        return r
    }

    @Test
    fun basics() {
        val r = run("console.log('Hallo', 42, [1, 2, 3], { a: 'x' });")
        assertTrue(r.ok)
        assertEquals("Hallo 42 [ 1, 2, 3 ] { a: 'x' }\n", r.stdout)
    }

    @Test
    fun freshRealmEachRun() {
        assertTrue(run("const x = 1; globalThis.y = 2; console.log(x);").ok)
        val r = run("const x = 5; console.log(x, typeof y);")
        assertEquals("5 undefined\n", r.stdout)
    }

    @Test
    fun timersAndAsync() {
        val r = run(
            """
            const warte = (ms) => new Promise((r) => setTimeout(r, ms));
            async function main() {
              console.log('A');
              await warte(1500);
              console.log('C');
            }
            setTimeout(() => console.log('B'), 500);
            main();
            """.trimIndent(),
        )
        assertEquals("A\nB\nC\n", r.stdout)
        assertTrue("virtuelle Zeit: schnell", r.durationMs < 1000)
    }

    @Test
    fun syntaxErrorWithLocation() {
        val r = run("console.log('a');\nlet x = ;")
        assertEquals("SyntaxError", r.errorType)
        assertEquals(2, r.errorLine)
        assertTrue(r.error!!, r.error!!.startsWith("main.js:2\nlet x = ;\n        ^"))
        assertNotNull(r.hint)
        assertEquals("", r.stdout)
    }

    @Test
    fun runtimeErrorWithFrames() {
        val r = run("function f(o) {\n  return o.name.length;\n}\nconsole.log('vorher');\nf({});")
        assertEquals("vorher\n", r.stdout)
        assertEquals("TypeError", r.errorType)
        assertEquals(2, r.errorLine)
        assertTrue(r.error!!, r.error!!.contains("TypeError: Cannot read properties of undefined (reading 'length')"))
        assertTrue(r.error!!, r.error!!.contains("at f (main.js:2:17)"))
        assertTrue(r.error!!, r.error!!.contains("at main.js:5:1"))
    }

    @Test
    fun columnsAreMappedBackAfterInstrumentation() {
        val r = run("let i = 0;\nwhile (i < 3) { i++; nichtDa(); }")
        assertEquals(2, r.errorLine)
        assertTrue(r.error!!, r.error!!.contains("main.js:2\nwhile (i < 3) { i++; nichtDa(); }\n                     ^"))
    }

    @Test
    fun infiniteLoopTimesOut() {
        val t0 = SystemClock.elapsedRealtime()
        val r = run("let n = 0;\nwhile (true) { n++; }", timeout = 2.0)
        assertTrue(r.timedOut)
        assertTrue(SystemClock.elapsedRealtime() - t0 < 4500)
        // danach geht es normal weiter
        assertEquals("ok\n", run("console.log('ok')").stdout)
    }

    @Test
    fun forLoopsAndForOfAreGuarded() {
        assertTrue(run("for (;;) {}", timeout = 1.5).timedOut)
        assertTrue(run("function* g() { while (true) yield 1; }\nfor (const x of g()) {}", timeout = 1.5).timedOut)
        val r = run("const o = { a: 1 };\nfor (const x of o) {}")
        assertEquals("TypeError", r.errorType)
        assertTrue(r.error!!, r.error!!.contains("o is not iterable"))
        assertEquals(2, r.errorLine)
    }

    @Test
    fun microtaskLoopIsKilledAndEngineRecovers() {
        val t0 = SystemClock.elapsedRealtime()
        val r = run("function f() { Promise.resolve().then(f); }\nf();", timeout = 1.5)
        assertTrue(r.timedOut)
        assertTrue(SystemClock.elapsedRealtime() - t0 < 8000)
        assertEquals("wieder da\n", run("console.log('wieder da')").stdout)
    }

    @Test
    fun promptWithGivenInput() {
        val r = run("const n = prompt('Name?');\nconst z = Number(prompt('Zahl:'));\nconsole.log(n, z * 2);", stdin = listOf("Ada", "21"))
        assertEquals("Ada 42\n", r.stdout)
    }

    @Test
    fun outputLimit() {
        run("console.log('warm')")
        val t0 = SystemClock.elapsedRealtime()
        val r = run("while (true) console.log('spam spam spam spam spam');")
        assertEquals("OutputLimit", r.errorType)
        assertTrue("Ausgabelimit schnell erreicht", SystemClock.elapsedRealtime() - t0 < 3000)
    }

    @Test
    fun unhandledRejectionIsAnError() {
        val r = run("async function f() { throw new Error('kaputt'); }\nf();\nconsole.log('weiter');")
        assertEquals("weiter\n", r.stdout)
        assertEquals("Error", r.errorType)
        assertTrue(r.error!!, r.error!!.contains("Error: kaputt"))
    }

    @Test
    fun errorInTimerStopsProgram() {
        val r = run("setTimeout(() => { null.x; }, 10);\nsetTimeout(() => console.log('nie'), 50);\nconsole.log('vorher');")
        assertEquals("vorher\n", r.stdout)
        assertEquals("TypeError", r.errorType)
        assertEquals(1, r.errorLine)
    }

    @Test
    fun consoleTableLikeNode() {
        val r = run("console.table([{ a: 1, b: 'x' }, { a: 2, b: 'y' }]);")
        val expected = """
            ┌─────────┬───┬─────┐
            │ (index) │ a │ b   │
            ├─────────┼───┼─────┤
            │ 0       │ 1 │ 'x' │
            │ 1       │ 2 │ 'y' │
            └─────────┴───┴─────┘
        """.trimIndent() + "\n"
        assertEquals(expected, r.stdout)
    }

    @Test
    fun realtimeStreamingAndCancel() = runBlocking {
        run("console.log('warm')")
        val chunks = mutableListOf<ConsoleChunk>()
        val input = InputChannel()
        val job = async {
            runner.run("let i = 0;\nsetInterval(() => console.log('tick', ++i), 100);", input = input, onOutput = { synchronized(chunks) { chunks += it } })
        }
        delay(1200)
        runner.cancel()
        val r = job.await()
        assertTrue(r.cancelled)
        val ticks = synchronized(chunks) { chunks.joinToString("") { it.text } }.lines().count { it.startsWith("tick") }
        Log.i("JsRunnerTest", "ticks in 1.2 s: $ticks")
        assertTrue("echte Zeit: $ticks Ticks", ticks in 5..14)
    }

    @Test
    fun interactivePrompt() = runBlocking {
        val input = InputChannel()
        val job = async {
            runner.run("const n = prompt('Wie heißt du?');\nconsole.log('Hallo ' + n);", input = input, onOutput = {})
        }
        var waited = 0
        while (input.waiting.value == null && waited < 5000) {
            delay(50)
            waited += 50
        }
        assertEquals("Wie heißt du? ", input.waiting.value)
        input.submit("Ada")
        val r = job.await()
        assertEquals("Wie heißt du? Hallo Ada\n", r.stdout)
        assertNull(r.error)
    }

    @Test
    fun formattingMatchesNode() {
        val r = run("console.log([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]);\nconsole.log(new Map([['a', 1]]), new Set([1]), -0, 10n, 'x');")
        assertEquals("[\n  1, 2, 3, 4,  5,\n  6, 7, 8, 9, 10\n]\nMap(1) { 'a' => 1 } Set(1) { 1 } -0 10n x\n", r.stdout)
    }
}

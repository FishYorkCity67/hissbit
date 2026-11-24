package app.pyquest.js

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import app.pyquest.python.ConsoleChunk
import app.pyquest.python.RunResult
import app.pyquest.python.normalizeOutput
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Führt den kompletten JavaScript-Kurs (Export von ContentExportTest) in der echten
 * WebView-Laufzeit der App aus – mit Schleifen-Wächtern, genau wie beim Lernen.
 *
 * Vorher: ./gradlew :app:testDebugUnitTest --tests *ContentExportTest
 * Start:  adb shell am instrument -w -e class app.pyquest.js.JsContentDeviceTest app.pyquest.test/androidx.test.runner.AndroidJUnitRunner
 */
@RunWith(AndroidJUnit4::class)
class JsContentDeviceTest {
    private val runner = JsRunner(InstrumentationRegistry.getInstrumentation().targetContext)

    private fun str(o: JsonObject, key: String): String? = o[key]?.jsonPrimitive?.content
    private fun list(o: JsonObject, key: String): List<String> = (o[key] as? JsonArray)?.map { it.jsonPrimitive.content } ?: emptyList()

    /** Lauf wie in einer Lektion (virtuelle Zeit) – oder mit Live-Ausgabe wie in der Theorie. */
    private fun run(code: String, stdin: List<String> = emptyList(), shown: Boolean = false): Pair<RunResult, String> = runBlocking {
        if (!shown) {
            val r = runner.run(code, stdin, 6.0)
            r to r.stdout
        } else {
            val sb = StringBuilder()
            val r = runner.run(code, stdin, 8.0, onOutput = { c: ConsoleChunk -> if (c.kind == 0 || c.kind == 2) synchronized(sb) { sb.append(c.text) } })
            r to synchronized(sb) { sb.toString() }
        }
    }

    @Test
    fun wholeCourseRunsInWebView() = runCourse("content-javascript-de.json")

    @Test
    fun wholeEnglishCourseRunsInWebView() = runCourse("content-javascript-en.json")

    private fun runCourse(assetName: String) {
        val ctx = InstrumentationRegistry.getInstrumentation().context
        val text = ctx.assets.open(assetName).bufferedReader().use { it.readText() }
        val items = Json.parseToJsonElement(text).jsonObject["items"]!!.jsonArray.map { it.jsonObject }
        val problems = mutableListOf<String>()
        var checked = 0
        for (item in items) {
            val kind = str(item, "kind")!!
            val where = str(item, "where")!!
            fun problem(msg: String) {
                problems += "$where: $msg"
                Log.w("JsContentTest", "$where: $msg")
            }
            fun expect(r: RunResult, actual: String, expected: String?) {
                if (!r.ok) problem("Fehler: ${r.error?.lines()?.lastOrNull { it.isNotBlank() } ?: if (r.timedOut) "Zeitlimit" else "?"}")
                else if (expected != null && normalizeOutput(actual) != normalizeOutput(expected)) {
                    problem("Ausgabe weicht ab:\n--- erwartet\n$expected\n--- WebView\n$actual")
                }
            }
            when (kind) {
                "theory" -> if (item["runnable"]?.jsonPrimitive?.content == "true") {
                    val (r, shown) = run(str(item, "code")!!, list(item, "stdin"), shown = true)
                    val fails = item["fails"]?.jsonPrimitive?.content == "true"
                    if (fails) {
                        if (r.ok) problem("soll einen Fehler zeigen, läuft aber fehlerfrei")
                    } else {
                        expect(r, shown, str(item, "output"))
                    }
                    checked++
                }
                "output", "typeOutput" -> {
                    val expected = if (kind == "output") list(item, "options").first() else str(item, "expected")
                    val (r, out) = run(str(item, "code")!!)
                    expect(r, out, expected)
                    checked++
                }
                "fill" -> str(item, "expected")?.let { expected ->
                    val (r, out) = run(str(item, "solved")!!)
                    expect(r, out, expected)
                    checked++
                }
                "arrange" -> str(item, "expected")?.let { expected ->
                    val (r, out) = run(list(item, "lines").joinToString("\n"))
                    expect(r, out, expected)
                    checked++
                }
                "code" -> {
                    val program = str(item, "solution")!! + (str(item, "test")?.let { "\n\n$it" } ?: "")
                    val (r, out) = run(program, list(item, "stdin"))
                    expect(r, out, str(item, "expected"))
                    checked++
                }
                "example", "reference" -> {
                    val (r, out) = run(str(item, "code")!!, list(item, "stdin"))
                    expect(r, out, null)
                    checked++
                }
            }
        }
        Log.i("JsContentTest", "$assetName – geprüft: $checked Programme, ${problems.size} Probleme")
        assertTrue("${problems.size} Probleme:\n" + problems.joinToString("\n\n"), problems.isEmpty())
    }
}

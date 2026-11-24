package app.pyquest.content

import app.pyquest.data.ArrangeExercise
import app.pyquest.data.ChoiceExercise
import app.pyquest.data.CodeExercise
import app.pyquest.data.CourseId
import app.pyquest.data.FillBlankExercise
import app.pyquest.data.LessonKind
import app.pyquest.data.MatchExercise
import app.pyquest.data.TheoryBlock
import app.pyquest.data.TrueFalseExercise
import app.pyquest.data.TypeExercise
import app.pyquest.i18n.AppLanguage
import app.pyquest.js.JsInstrumenter
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Baut alle Kurse in allen vorhandenen Sprachen (dabei greifen die require-Prüfungen der DSL),
 * prüft strukturelle Regeln und exportiert alles Ausführbare als JSON:
 * content-<kurs>-<sprache>.json (Python-Deutsch zusätzlich als content.json).
 *
 * Geprüft wird der Export mit echtem Python (tools/verify_content.py) bzw. mit der
 * Hissbit-JavaScript-Laufzeit in Node (tools/verify_js.mjs).
 */
class ContentExportTest {

    private fun strings(list: List<String>) = JsonArray(list.map { JsonPrimitive(it) })

    @Test
    fun exportContent() {
        val dir = File(System.getProperty("pyquest.exportDir") ?: "build/content-export")
        dir.mkdirs()
        val allProblems = mutableListOf<String>()
        for (id in CourseId.entries) {
            for (lang in AppLanguage.entries) {
                // Auch unfertige Übersetzungen prüfen – so fallen Fehler früh auf.
                val bundle = Catalog.exactBundle(id, lang) ?: Catalog.draftBundle(id, lang) ?: continue
                val (json, problems) = export(bundle, id, lang)
                val name = "content-${id.key}-${lang.code}.json"
                File(dir, name).writeText(json.toString())
                if (id == CourseId.PYTHON && lang == AppLanguage.DE) File(dir, "content.json").writeText(json.toString())
                println("Export $name: ${bundle.course.units.size} Einheiten, ${bundle.course.lessons.size} Lektionen, " +
                    "${bundle.course.exercises.size} Aufgaben, ${(json["items"] as JsonArray).size} Prüfpunkte")
                problems.forEach { println("PROBLEM [$name] $it") }
                allProblems += problems.map { "[$name] $it" }
            }
        }
        assertTrue("Strukturprobleme:\n" + allProblems.joinToString("\n"), allProblems.isEmpty())
    }

    private fun export(bundle: CourseBundle, id: CourseId, lang: AppLanguage): Pair<JsonObject, List<String>> {
        val course = bundle.course
        val js = id == CourseId.JAVASCRIPT
        val problems = mutableListOf<String>()
        fun problem(where: String, what: String) {
            problems += "$where: $what"
        }
        val items = mutableListOf<JsonObject>()

        fun item(kind: String, where: String, block: JsonObjectBuilder.() -> Unit) {
            items += buildJsonObject {
                put("kind", kind)
                put("where", where)
                block()
            }
        }

        /** Für JavaScript zusätzlich die Variante mit Schleifen-Wächtern, wie die App sie ausführt. */
        fun JsonObjectBuilder.code(key: String, code: String) {
            put(key, code)
            if (js) put(key + "I", JsInstrumenter.instrument(code).code)
        }

        val ids = mutableSetOf<String>()
        for (unit in course.units) {
            if (unit.lessons.none { it.kind == LessonKind.CHECKPOINT }) problem(unit.id, "keine Checkpoint-Lektion")
            for (lesson in unit.lessons) {
                val lw = "${lesson.id} „${lesson.title}“"
                if (lesson.kind != LessonKind.CHECKPOINT && lesson.exercises.size < 4) {
                    problem(lw, "nur ${lesson.exercises.size} Aufgaben")
                }
                lesson.theory.forEachIndexed { i, block ->
                    if (block is TheoryBlock.Code) {
                        item("theory", "$lw / Theorie-Code ${i + 1}") {
                            put("lesson", lesson.id)
                            code("code", block.code)
                            block.output?.let { put("output", it) }
                            put("stdin", strings(block.stdin))
                            put("runnable", block.runnable)
                            put("fails", block.fails)
                        }
                    }
                }
                for (e in lesson.exercises) {
                    if (!ids.add(e.id)) problem(e.id, "doppelte ID")
                    val w = "${e.id} ($lw)"
                    when (e) {
                        is ChoiceExercise -> {
                            if (e.options.toSet().size != e.options.size) problem(w, "doppelte Optionen ${e.options}")
                            if (e.options.any { it.isBlank() }) problem(w, "leere Option")
                            val visible = e.options.map { o -> o.lines().joinToString("\n") { it.trimEnd() }.trim('\n') }
                            if (visible.toSet().size != visible.size) problem(w, "Optionen sehen gleich aus (Leerzeichen am Ende): ${e.options}")
                            if (e.options.any { o -> o.lines().any { it != it.trimEnd() } }) problem(w, "Leerzeichen am Zeilenende sind unsichtbar: ${e.options}")
                            if (e.isOutput) item("output", w) {
                                put("lesson", lesson.id)
                                code("code", e.code.orEmpty())
                                put("options", strings(e.options))
                            } else if (e.code != null) item("compile", w) {
                                put("lesson", lesson.id)
                                put("code", e.code)
                            }
                        }
                        is TrueFalseExercise -> if (e.code != null) item("compile", w) {
                            put("lesson", lesson.id)
                            put("code", e.code)
                        }
                        is FillBlankExercise -> {
                            val tiles = e.answers + e.distractors
                            if (e.distractors.any { it in e.answers }) problem(w, "Distraktor ist auch eine Antwort")
                            if (e.distractors.toSet().size != e.distractors.size) problem(w, "doppelte Distraktoren")
                            if (tiles.any { it.isBlank() }) problem(w, "leere Kachel")
                            item("fill", w) {
                                put("lesson", lesson.id)
                                put("template", e.template)
                                put("answers", strings(e.answers))
                                put("distractors", strings(e.distractors))
                                code("solved", e.solvedCode)
                                e.expectedOutput?.let { put("expected", it) }
                            }
                        }
                        is ArrangeExercise -> {
                            if (e.distractors.any { it in e.lines }) problem(w, "Distraktor ist auch eine Zeile")
                            item("arrange", w) {
                                put("lesson", lesson.id)
                                put("lines", strings(e.lines))
                                put("distractors", strings(e.distractors))
                                if (js) put("codeI", JsInstrumenter.instrument(e.lines.joinToString("\n")).code)
                                e.expectedOutput?.let { put("expected", it) }
                            }
                        }
                        is TypeExercise -> {
                            if (e.accepted.isEmpty() || e.accepted.any { it.isBlank() }) problem(w, "leere Antwort")
                            if (e.isOutput) item("typeOutput", w) {
                                put("lesson", lesson.id)
                                code("code", e.code.orEmpty())
                                put("expected", e.accepted.first())
                            } else if (e.code != null) item("compile", w) {
                                put("lesson", lesson.id)
                                put("code", e.code)
                            }
                        }
                        is CodeExercise -> codeItem(::item, w, lesson.id, e, js)
                        is MatchExercise -> {
                            val left = e.pairs.map { it.first }
                            val right = e.pairs.map { it.second }
                            if (left.toSet().size != left.size || right.toSet().size != right.size) problem(w, "doppelte Paare")
                        }
                    }
                }
            }
        }

        bundle.challenges.forEachIndexed { i, c ->
            codeItem(::item, "Challenge ${i + 1}: ${c.prompt.substringBefore(":**").removePrefix("**")}", "challenge", c, js)
            if (!c.prompt.startsWith("**") || ":**" !in c.prompt) problem("Challenge ${i + 1}", "Prompt beginnt nicht mit **Titel:**")
        }
        bundle.examples.forEach { ex ->
            item("example", "Beispiel „${ex.title}“") {
                put("lesson", "examples")
                code("code", ex.code)
                put("stdin", strings(ex.testInput))
            }
        }
        bundle.reference.forEach { cat ->
            cat.entries.forEach { r ->
                if (r.code != null) item("reference", "Nachschlagen ${cat.title} / ${r.title}") {
                    put("lesson", "reference")
                    code("code", r.code)
                }
            }
        }
        bundle.errors.forEach { r ->
            if (r.code != null) item("reference", "Fehlerlexikon ${r.title}") {
                put("lesson", "errors")
                code("code", r.code)
            }
        }
        bundle.tips.forEach { t ->
            item("compile", "Tipp „${t.title}“") {
                put("lesson", "tips")
                put("code", t.code)
            }
        }

        val json = buildJsonObject {
            put("course", id.key)
            put("language", lang.code)
            put("units", course.units.size)
            put("lessons", course.lessons.size)
            put("exercises", course.exercises.size)
            put("items", JsonArray(items))
            put("problems", strings(problems))
        }
        return json to problems
    }

    private fun codeItem(
        item: (String, String, JsonObjectBuilder.() -> Unit) -> Unit,
        where: String,
        lesson: String,
        c: CodeExercise,
        js: Boolean,
    ) = item("code", where) {
        put("lesson", lesson)
        put("starter", c.starter)
        put("solution", c.solution)
        c.testCode?.let { put("test", it) }
        if (js) {
            val program = if (c.testCode != null) c.solution + "\n\n" + c.testCode else c.solution
            put("programI", JsInstrumenter.instrument(program).code)
        }
        c.expectedOutput?.let { put("expected", it) }
        put("stdin", strings(c.stdin))
        put("mustContain", strings(c.mustContain))
    }
}

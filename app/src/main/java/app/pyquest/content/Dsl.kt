package app.pyquest.content

import app.pyquest.data.ArrangeExercise
import app.pyquest.data.ChoiceExercise
import app.pyquest.data.CodeExercise
import app.pyquest.data.Course
import app.pyquest.data.CourseId
import app.pyquest.data.CourseUnit
import app.pyquest.data.Exercise
import app.pyquest.data.FillBlankExercise
import app.pyquest.data.Lesson
import app.pyquest.data.LessonIcon
import app.pyquest.data.LessonKind
import app.pyquest.data.MatchExercise
import app.pyquest.data.TheoryBlock
import app.pyquest.data.TrueFalseExercise
import app.pyquest.data.TypeExercise
import app.pyquest.i18n.AppLanguage

/** Drei Anführungszeichen für Docstrings innerhalb von Kotlin-Raw-Strings. */
const val TQ = "\"\"\""

@DslMarker
annotation class CourseDsl

fun buildCourse(id: CourseId = CourseId.PYTHON, lang: AppLanguage = AppLanguage.DE, block: CourseBuilder.() -> Unit): Course =
    CourseBuilder(id, lang).apply(block).build()

@CourseDsl
class CourseBuilder(private val courseId: CourseId = CourseId.PYTHON, private val lang: AppLanguage = AppLanguage.DE) {
    private val units = mutableListOf<CourseUnit>()

    fun unit(title: String, description: String, block: UnitBuilder.() -> Unit) {
        val number = units.size + 1
        // Python-IDs bleiben ohne Präfix ("u01"), damit gespeicherter Fortschritt passt.
        val prefix = if (courseId.idPrefix.isEmpty()) "" else courseId.idPrefix + "-"
        val id = prefix + "u%02d".format(number)
        val builder = UnitBuilder(id, lang).apply(block)
        units += CourseUnit(
            id = id,
            number = number,
            title = title,
            description = description,
            colorIndex = (number - 1),
            lessons = builder.build(),
        )
    }

    fun build() = Course(units.toList(), courseId)
}

@CourseDsl
class UnitBuilder(private val unitId: String, private val lang: AppLanguage = AppLanguage.DE) {
    private val lessons = mutableListOf<Lesson>()

    fun lesson(
        title: String,
        subtitle: String = "",
        icon: LessonIcon = LessonIcon.STAR,
        kind: LessonKind = LessonKind.LESSON,
        block: LessonBuilder.() -> Unit,
    ) {
        val id = "$unitId-l${lessons.size + 1}"
        lessons += LessonBuilder(id, lang).apply(block).build(id, unitId, title, subtitle, kind, icon)
    }

    fun project(title: String, subtitle: String = "", block: LessonBuilder.() -> Unit) =
        lesson(title, subtitle, LessonIcon.ROCKET, LessonKind.PROJECT, block)

    /** Checkpoint: zieht zur Laufzeit Aufgaben aus allen Lektionen der Einheit. */
    fun checkpoint(
        title: String = "Checkpoint",
        subtitle: String = lang.pick("Zeig, was du kannst!", "Show what you can do!"),
        block: LessonBuilder.() -> Unit = {},
    ) {
        val id = "$unitId-cp"
        lessons += LessonBuilder(id, lang).apply(block).build(id, unitId, title, subtitle, LessonKind.CHECKPOINT, LessonIcon.TROPHY)
    }

    fun build() = lessons.toList()
}

@CourseDsl
class LessonBuilder(private val lessonId: String, private val lang: AppLanguage = AppLanguage.DE) {
    private val theory = mutableListOf<TheoryBlock>()
    private val exercises = mutableListOf<Exercise>()

    private fun nextId() = "$lessonId-e${exercises.size + 1}"

    // ---------- Theorie ----------

    fun heading(text: String) {
        theory += TheoryBlock.Heading(text)
    }

    fun text(markdown: String) {
        theory += TheoryBlock.Text(markdown.trimIndent().trim())
    }

    fun code(code: String, output: String? = null, stdin: List<String> = emptyList(), runnable: Boolean = true, fails: Boolean = false) {
        theory += TheoryBlock.Code(code.trimIndent().trimEnd(), output?.trimIndent()?.trimEnd('\n'), stdin, runnable, fails)
    }

    fun tip(markdown: String) {
        theory += TheoryBlock.Tip(markdown.trimIndent().trim())
    }

    fun warn(markdown: String) {
        theory += TheoryBlock.Warning(markdown.trimIndent().trim())
    }

    // ---------- Aufgaben ----------

    /** Multiple Choice, die erste Option ist richtig. */
    fun choice(prompt: String, vararg options: String, code: String? = null, explain: String? = null, mono: Boolean = false) {
        require(options.size >= 2) { "$lessonId: choice braucht mindestens 2 Optionen" }
        exercises += ChoiceExercise(nextId(), prompt, code?.trimIndent()?.trimEnd(), options.toList(), explain, isOutput = false, monoOptions = mono)
    }

    /** "Was gibt dieser Code aus?" – erste Option ist die echte Ausgabe. */
    fun output(code: String, vararg options: String, prompt: String = lang.pick("Was gibt dieser Code aus?", "What does this code print?"), explain: String? = null) {
        require(options.size >= 2) { "$lessonId: output braucht mindestens 2 Optionen" }
        exercises += ChoiceExercise(nextId(), prompt, code.trimIndent().trimEnd(), options.toList(), explain, isOutput = true, monoOptions = true)
    }

    fun truth(statement: String, answer: Boolean, code: String? = null, explain: String? = null) {
        exercises += TrueFalseExercise(nextId(), statement, code?.trimIndent()?.trimEnd(), answer, explain)
    }

    /** Lückentext. Im Template steht ?? für jede Lücke. */
    fun fill(
        prompt: String,
        template: String,
        answers: List<String>,
        distractors: List<String> = emptyList(),
        output: String? = null,
        explain: String? = null,
    ) {
        val t = template.trimIndent().trimEnd()
        val blanks = t.split(FillBlankExercise.BLANK).size - 1
        require(blanks == answers.size) { "$lessonId: Template hat $blanks Lücken, aber ${answers.size} Antworten" }
        exercises += FillBlankExercise(nextId(), prompt, t, answers, distractors, explain, output?.trimIndent()?.trimEnd('\n'))
    }

    /** Code-Puzzle: [code] ist die richtige Lösung, jede Zeile wird zur Kachel. */
    fun arrange(
        prompt: String,
        code: String,
        distractors: List<String> = emptyList(),
        output: String? = null,
        explain: String? = null,
    ) {
        val lines = code.trimIndent().trimEnd().lines().filter { it.isNotBlank() }
        require(lines.size >= 2) { "$lessonId: arrange braucht mindestens 2 Zeilen" }
        exercises += ArrangeExercise(nextId(), prompt, lines, distractors, explain, output?.trimIndent()?.trimEnd('\n'))
    }

    /** Freitext-Antwort. */
    fun type(prompt: String, vararg accepted: String, code: String? = null, explain: String? = null, placeholder: String? = null) {
        exercises += TypeExercise(nextId(), prompt, code?.trimIndent()?.trimEnd(), accepted.toList(), explain, isOutput = false, placeholder = placeholder)
    }

    /** Ausgabe eintippen – wird beim Build mit echtem Python geprüft. */
    fun typeOutput(
        code: String,
        expected: String,
        prompt: String = lang.pick("Was gibt dieser Code aus? Tippe die Ausgabe ein.", "What does this code print? Type the output."),
        explain: String? = null,
    ) {
        exercises += TypeExercise(nextId(), prompt, code.trimIndent().trimEnd(), listOf(expected), explain, isOutput = true, placeholder = lang.pick("Ausgabe", "Output"))
    }

    /** Programmieraufgabe. Geprüft wird über [output] (erwartete Ausgabe) und/oder [test] (asserts). */
    fun write(
        prompt: String,
        solution: String,
        starter: String = "",
        output: String? = null,
        test: String? = null,
        stdin: List<String> = emptyList(),
        mustContain: List<String> = emptyList(),
        hint: String? = null,
        explain: String? = null,
    ) {
        require(output != null || test != null) { "$lessonId: write braucht output oder test" }
        exercises += CodeExercise(
            id = nextId(),
            prompt = prompt,
            starter = starter.trimIndent().trimEnd(),
            solution = solution.trimIndent().trimEnd(),
            expectedOutput = output?.trimIndent()?.trimEnd('\n'),
            testCode = test?.trimIndent()?.trimEnd(),
            stdin = stdin,
            mustContain = mustContain,
            hint = hint,
            explanation = explain,
        )
    }

    fun match(vararg pairs: Pair<String, String>, prompt: String = lang.pick("Finde die passenden Paare", "Find the matching pairs"), explain: String? = null) {
        require(pairs.size in 3..6) { "$lessonId: match braucht 3 bis 6 Paare" }
        exercises += MatchExercise(nextId(), prompt, pairs.toList(), explain)
    }

    fun build(id: String, unitId: String, title: String, subtitle: String, kind: LessonKind, icon: LessonIcon) = Lesson(
        id = id,
        unitId = unitId,
        title = title,
        subtitle = subtitle,
        kind = kind,
        icon = icon,
        theory = theory.toList(),
        exercises = exercises.toList(),
    )
}

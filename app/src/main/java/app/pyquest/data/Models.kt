package app.pyquest.data

/*
 * Inhaltsmodell des Kurses. Bewusst ohne Android-Abhängigkeiten, damit die
 * Inhalte auch in JVM-Unit-Tests geprüft werden können.
 */

enum class LessonKind { LESSON, CHECKPOINT, PROJECT }

enum class LessonIcon { STAR, CODE, BOOK, PUZZLE, ROCKET, BRAIN, TROPHY, BUG, BOX, LOOP, BRANCH, CLASS, FILE, MAGIC, CHAT, CALC }

data class Course(val units: List<CourseUnit>, val id: CourseId = CourseId.PYTHON) {
    val lessons: List<Lesson> = units.flatMap { it.lessons }
    private val lessonIndex: Map<String, Int> = lessons.withIndex().associate { it.value.id to it.index }
    private val unitById: Map<String, CourseUnit> = units.associateBy { it.id }
    val exercises: Map<String, Exercise> = lessons.flatMap { it.exercises }.associateBy { it.id }
    private val lessonOfExercise: Map<String, Lesson> =
        lessons.flatMap { l -> l.exercises.map { it.id to l } }.toMap()

    fun lesson(id: String): Lesson? = lessonIndex[id]?.let { lessons[it] }
    fun indexOf(lessonId: String): Int = lessonIndex[lessonId] ?: -1
    fun unit(id: String): CourseUnit? = unitById[id]
    fun unitOf(lesson: Lesson): CourseUnit = unitById.getValue(lesson.unitId)
    fun lessonOf(exerciseId: String): Lesson? = lessonOfExercise[exerciseId]
}

data class CourseUnit(
    val id: String,
    val number: Int,
    val title: String,
    val description: String,
    val colorIndex: Int,
    val lessons: List<Lesson>,
)

data class Lesson(
    val id: String,
    val unitId: String,
    val title: String,
    val subtitle: String,
    val kind: LessonKind,
    val icon: LessonIcon,
    val theory: List<TheoryBlock>,
    val exercises: List<Exercise>,
)

sealed interface TheoryBlock {
    data class Heading(val text: String) : TheoryBlock
    data class Text(val markdown: String) : TheoryBlock
    data class Code(
        val code: String,
        val output: String? = null,
        val stdin: List<String> = emptyList(),
        val runnable: Boolean = true,
        /** Der Code zeigt absichtlich einen Fehler. */
        val fails: Boolean = false,
    ) : TheoryBlock
    data class Tip(val markdown: String) : TheoryBlock
    data class Warning(val markdown: String) : TheoryBlock
}

sealed interface Exercise {
    val id: String
    val prompt: String
    val explanation: String?
}

/** Multiple Choice. Die erste Option ist immer die richtige, angezeigt wird gemischt. */
data class ChoiceExercise(
    override val id: String,
    override val prompt: String,
    val code: String?,
    val options: List<String>,
    override val explanation: String?,
    /** true: Optionen sind die Ausgabe von [code] (wird beim Build geprüft). */
    val isOutput: Boolean,
    /** Optionen als Code (Monospace) darstellen. */
    val monoOptions: Boolean,
) : Exercise

data class TrueFalseExercise(
    override val id: String,
    override val prompt: String,
    val code: String?,
    val answer: Boolean,
    override val explanation: String?,
) : Exercise

/** Lückentext: [template] enthält [BLANK] für jede Lücke, gefüllt wird mit Kacheln. */
data class FillBlankExercise(
    override val id: String,
    override val prompt: String,
    val template: String,
    val answers: List<String>,
    val distractors: List<String>,
    override val explanation: String?,
    val expectedOutput: String?,
) : Exercise {
    val solvedCode: String
        get() {
            var result = template
            answers.forEach { result = result.replaceFirst(BLANK, it) }
            return result
        }

    companion object {
        const val BLANK = "??"
    }
}

/** Code-Puzzle: Zeilen in die richtige Reihenfolge bringen. */
data class ArrangeExercise(
    override val id: String,
    override val prompt: String,
    val lines: List<String>,
    val distractors: List<String>,
    override val explanation: String?,
    val expectedOutput: String?,
) : Exercise

/** Freie Texteingabe (z.B. Ausgabe vorhersagen). */
data class TypeExercise(
    override val id: String,
    override val prompt: String,
    val code: String?,
    val accepted: List<String>,
    override val explanation: String?,
    val isOutput: Boolean,
    val placeholder: String?,
) : Exercise

/** Echte Programmieraufgabe, die mit Python ausgeführt und geprüft wird. */
data class CodeExercise(
    override val id: String,
    override val prompt: String,
    val starter: String,
    val solution: String,
    val expectedOutput: String?,
    val testCode: String?,
    val stdin: List<String>,
    val mustContain: List<String>,
    val hint: String?,
    override val explanation: String?,
) : Exercise

data class MatchExercise(
    override val id: String,
    override val prompt: String,
    val pairs: List<Pair<String, String>>,
    override val explanation: String?,
) : Exercise

/** Eintrag im Nachschlagewerk. */
data class RefEntry(
    val id: String,
    val title: String,
    val summary: String,
    val body: String,
    val code: String?,
    val keywords: List<String> = emptyList(),
)

data class RefCategory(val id: String, val title: String, val icon: LessonIcon, val entries: List<RefEntry>)

data class GlossaryTerm(val term: String, val meaning: String)

data class ExampleProgram(
    val title: String,
    val description: String,
    val code: String,
    val category: String,
    /** Eingaben für die automatische Prüfung beim Build (input()). */
    val testInput: List<String> = emptyList(),
)

data class PythonTip(val title: String, val code: String)

package app.pyquest.ui.lesson

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.pyquest.PyQuestApp
import app.pyquest.content.Catalog
import app.pyquest.data.CourseId
import app.pyquest.data.ArrangeExercise
import app.pyquest.data.ChoiceExercise
import app.pyquest.data.CodeExercise
import app.pyquest.data.Exercise
import app.pyquest.data.FillBlankExercise
import app.pyquest.data.Game
import app.pyquest.data.Lesson
import app.pyquest.data.LessonKind
import app.pyquest.data.MatchExercise
import app.pyquest.data.PurchaseResult
import app.pyquest.data.SessionBuilder
import app.pyquest.data.SessionMode
import app.pyquest.data.SessionResult
import app.pyquest.data.ShopItem
import app.pyquest.data.TrueFalseExercise
import app.pyquest.data.TypeExercise
import app.pyquest.data.applySession
import app.pyquest.data.isCompleted
import app.pyquest.data.loseHeart
import app.pyquest.data.purchase
import app.pyquest.data.settled
import app.pyquest.i18n.AppLanguage
import app.pyquest.python.RunResult
import app.pyquest.python.normalizeOutput
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class LessonPhase { LOADING, THEORY, EXERCISE, OUT_OF_HEARTS, FINISHING, FINISHED, EMPTY }

sealed interface Answer {
    data class Choice(val optionIndex: Int) : Answer
    data class Bool(val value: Boolean) : Answer
    data class Tiles(val tiles: List<String>) : Answer
    data class Text(val text: String) : Answer
    data class Code(val code: String) : Answer
    data object Matched : Answer
}

/** Eine Aufgabe in der Warteschlange – mit fester Mischreihenfolge. */
data class ExerciseInstance(
    val key: Int,
    val exercise: Exercise,
    val optionOrder: List<Int>,
    val bank: List<String>,
    val retry: Boolean,
)

data class FeedbackState(
    val correct: Boolean,
    val title: String,
    val solution: String?,
    val solutionIsCode: Boolean,
    val explanation: String?,
    val run: RunResult? = null,
    val expectedOutput: String? = null,
    val canRetry: Boolean = false,
    val revealed: Boolean = false,
)

class LessonViewModel(
    private val container: PyQuestApp,
    val mode: SessionMode,
    val lessonId: String?,
    private val lang: AppLanguage,
    val courseId: CourseId,
) : ViewModel() {
    private val repository = container.repository
    private val bundle = Catalog.bundle(courseId, lang)
    val course = bundle.course
    val lesson: Lesson? = lessonId?.let { course.lesson(it) }
    val unit = lesson?.let { course.unitOf(it) }

    private val rnd = Random(System.nanoTime())
    private val startedAt = System.currentTimeMillis()
    private var keyCounter = 0

    var phase by mutableStateOf(LessonPhase.LOADING)
        private set
    val queue = mutableStateListOf<ExerciseInstance>()
    var index by mutableIntStateOf(0)
        private set
    var total by mutableIntStateOf(0)
        private set
    var solved by mutableIntStateOf(0)
        private set
    var mistakes by mutableIntStateOf(0)
        private set
    var combo by mutableIntStateOf(0)
        private set
    var maxCombo by mutableIntStateOf(0)
        private set
    var hearts by mutableIntStateOf(Game.MAX_HEARTS)
        private set
    var heartsEnabled by mutableStateOf(true)
        private set
    var feedback by mutableStateOf<FeedbackState?>(null)
        private set
    var checking by mutableStateOf(false)
        private set
    var answer by mutableStateOf<Answer?>(null)
    var gems by mutableIntStateOf(0)
        private set

    /** Zählt hoch bei falschen Antworten – für die Wackel-Animation. */
    var wrongPulse by mutableIntStateOf(0)
        private set

    private var codeSolved = 0
    private val wrongIds = mutableSetOf<String>()
    private val fixedIds = mutableSetOf<String>()

    val costsHearts: Boolean
        get() = heartsEnabled && (mode == SessionMode.LESSON || mode == SessionMode.CHECKPOINT || mode == SessionMode.CHALLENGE)

    val current: ExerciseInstance? get() = queue.getOrNull(index)

    val progressFraction: Float get() = if (total == 0) 0f else solved.toFloat() / total

    val title: String
        get() = when (mode) {
            SessionMode.LESSON -> lesson?.title ?: lang.pick("Lektion", "Lesson")
            SessionMode.CHECKPOINT -> "Checkpoint: ${unit?.title ?: ""}"
            SessionMode.PRACTICE -> lang.pick("Schnelltraining", "Quick practice")
            SessionMode.MISTAKES -> lang.pick("Fehler wiederholen", "Fix your mistakes")
            SessionMode.CODE_PRACTICE -> lang.pick("Code-Werkstatt", "Code workshop")
            SessionMode.CHALLENGE -> lang.pick("Tages-Challenge", "Daily challenge")
        }

    init {
        viewModelScope.launch {
            val settings = repository.currentSettings()
            val progress = repository.updateProgress { it.settled() }
            heartsEnabled = settings.heartsEnabled
            hearts = progress.hearts
            gems = progress.gems
            val exercises = SessionBuilder.build(course, progress, mode, lessonId, bundle.challenges, rnd.nextLong())
            if (exercises.isEmpty()) {
                phase = LessonPhase.EMPTY
                return@launch
            }
            exercises.forEach { queue += instance(it, retry = false) }
            total = exercises.size
            val firstTime = lessonId == null || !progress.isCompleted(lessonId)
            val hasTheory = lesson != null && lesson.kind != LessonKind.CHECKPOINT && lesson.theory.isNotEmpty()
            phase = when {
                costsHearts && hearts <= 0 -> LessonPhase.OUT_OF_HEARTS
                mode == SessionMode.LESSON && hasTheory && settings.showTheory && firstTime -> LessonPhase.THEORY
                else -> LessonPhase.EXERCISE
            }
        }
    }

    private fun instance(e: Exercise, retry: Boolean): ExerciseInstance {
        val order: List<Int>
        val bank: List<String>
        when (e) {
            is ChoiceExercise -> {
                order = e.options.indices.shuffled(rnd)
                bank = emptyList()
            }
            is FillBlankExercise -> {
                order = emptyList()
                bank = (e.answers + e.distractors).shuffled(rnd)
            }
            is ArrangeExercise -> {
                order = emptyList()
                var b = (e.lines + e.distractors).shuffled(rnd)
                // Nicht schon in der richtigen Reihenfolge anbieten
                var guard = 0
                while (b == e.lines && guard++ < 5) b = b.shuffled(rnd)
                bank = b
            }
            else -> {
                order = emptyList()
                bank = emptyList()
            }
        }
        return ExerciseInstance(keyCounter++, e, order, bank, retry)
    }

    fun startExercises() {
        phase = if (costsHearts && hearts <= 0) LessonPhase.OUT_OF_HEARTS else LessonPhase.EXERCISE
    }

    fun showTheoryAgain() {
        if (lesson?.theory?.isNotEmpty() == true) phase = LessonPhase.THEORY
    }

    /** Prüft die aktuelle Antwort. [run] führt Code mit Python aus. */
    fun check(run: suspend (code: String, stdin: List<String>) -> RunResult) {
        val inst = current ?: return
        val a = answer ?: return
        if (checking || feedback != null) return
        val e = inst.exercise
        when {
            e is CodeExercise && a is Answer.Code -> {
                checking = true
                viewModelScope.launch {
                    val program = if (e.testCode != null) a.code + "\n\n" + e.testCode else a.code
                    val result = run(program, e.stdin)
                    checking = false
                    val missing = e.mustContain.filterNot { a.code.contains(it) }
                    val outputOk = e.expectedOutput == null || normalizeOutput(result.stdout) == normalizeOutput(e.expectedOutput)
                    val ok = result.ok && outputOk && missing.isEmpty()
                    if (ok) {
                        codeSolved++
                        onCorrect(inst, FeedbackState(true, praise(), null, false, e.explanation, run = result))
                    } else {
                        val reason = when {
                            result.timedOut -> lang.pick("Zeitlimit überschritten – hast du eine Endlosschleife?", "Time limit exceeded – is there an infinite loop?")
                            !result.ok -> lang.pick("Dein Programm hat einen Fehler.", "Your program has an error.")
                            missing.isNotEmpty() -> lang.pick("Benutze in deiner Lösung: ", "Use this in your solution: ") + missing.joinToString(", ") { "`$it`" }
                            else -> lang.pick("Die Ausgabe stimmt noch nicht.", "The output isn't right yet.")
                        }
                        feedback = FeedbackState(
                            correct = false,
                            title = reason,
                            solution = e.solution,
                            solutionIsCode = true,
                            explanation = e.hint,
                            run = result,
                            expectedOutput = e.expectedOutput,
                            canRetry = true,
                        )
                        wrongPulse++
                    }
                }
            }
            e is ArrangeExercise && a is Answer.Tiles && a.tiles != e.lines && e.expectedOutput != null -> {
                // Alternative Reihenfolgen akzeptieren, wenn die Ausgabe stimmt
                checking = true
                viewModelScope.launch {
                    val result = run(a.tiles.joinToString("\n"), emptyList())
                    checking = false
                    val ok = result.ok && normalizeOutput(result.stdout) == normalizeOutput(e.expectedOutput) &&
                        a.tiles.size == e.lines.size
                    if (ok) onCorrect(inst, FeedbackState(true, praise(), null, false, e.explanation))
                    else onWrong(inst, e.lines.joinToString("\n"), true)
                }
            }
            else -> {
                val (ok, solution, isCode) = evaluate(e, a)
                if (ok) onCorrect(inst, FeedbackState(true, praise(), null, false, e.explanation))
                else onWrong(inst, solution, isCode)
            }
        }
    }

    private fun evaluate(e: Exercise, a: Answer): Triple<Boolean, String?, Boolean> = when (e) {
        is ChoiceExercise -> Triple((a as? Answer.Choice)?.optionIndex == 0, e.options[0], e.monoOptions)
        is TrueFalseExercise -> Triple(
            (a as? Answer.Bool)?.value == e.answer,
            if (e.answer) lang.pick("Richtig", "True") else lang.pick("Falsch", "False"),
            false,
        )
        is FillBlankExercise -> Triple((a as? Answer.Tiles)?.tiles == e.answers, e.solvedCode, true)
        is ArrangeExercise -> Triple((a as? Answer.Tiles)?.tiles == e.lines, e.lines.joinToString("\n"), true)
        is TypeExercise -> {
            val given = (a as? Answer.Text)?.text.orEmpty()
            Triple(e.accepted.any { matchesTyped(given, it) }, e.accepted.first(), e.isOutput || e.code != null)
        }
        is MatchExercise -> Triple(true, null, false)
        is CodeExercise -> Triple(false, e.solution, true)
    }

    private fun matchesTyped(given: String, expected: String): Boolean {
        val g = normalizeOutput(given.trim())
        val x = normalizeOutput(expected.trim())
        if (g == x) return true
        val strip = { s: String -> s.filterNot { it.isWhitespace() } }
        if (strip(g) == strip(x)) return true
        // Typografische Anführungszeichen der Tastatur tolerieren
        val quotes = { s: String -> s.replace('„', '"').replace('“', '"').replace('”', '"').replace('‘', '\'').replace('’', '\'') }
        return strip(quotes(g)) == strip(quotes(x))
    }

    private fun onCorrect(inst: ExerciseInstance, state: FeedbackState) {
        solved++
        combo++
        if (combo > maxCombo) maxCombo = combo
        if (mode == SessionMode.MISTAKES && inst.exercise.id !in wrongIds) fixedIds += inst.exercise.id
        feedback = state
    }

    private fun onWrong(inst: ExerciseInstance, solution: String?, isCode: Boolean) {
        registerMistake(inst)
        feedback = FeedbackState(
            correct = false,
            title = if (lang == AppLanguage.EN) listOf("Not quite …", "Sorry, that's wrong", "Almost!", "So close").random(rnd)
            else listOf("Nicht ganz …", "Leider falsch", "Fast!", "Knapp daneben").random(rnd),
            solution = solution,
            solutionIsCode = isCode,
            explanation = inst.exercise.explanation,
        )
        // Später nochmal üben (höchstens zweimal pro Aufgabe)
        val repeats = queue.count { it.exercise.id == inst.exercise.id }
        if (repeats < 3) queue += instance(inst.exercise, retry = true)
    }

    private fun registerMistake(inst: ExerciseInstance) {
        mistakes++
        combo = 0
        wrongPulse++
        wrongIds += inst.exercise.id
        if (costsHearts) {
            hearts = (hearts - 1).coerceAtLeast(0)
            viewModelScope.launch { repository.updateProgress { it.loseHeart(System.currentTimeMillis()) } }
        }
    }

    /** Code-Aufgabe: nochmal probieren (Feedback schließen). */
    fun retry() {
        feedback = null
    }

    /** Code-Aufgabe: Lösung aufdecken – zählt als Fehler. */
    fun reveal() {
        val inst = current ?: return
        val fb = feedback ?: return
        registerMistake(inst)
        feedback = fb.copy(canRetry = false, revealed = true, title = lang.pick("Hier ist eine mögliche Lösung", "Here's one possible solution"))
        solved++ // die Aufgabe ist damit erledigt
    }

    fun next() {
        feedback = null
        answer = null
        if (index + 1 >= queue.size) {
            finish()
            return
        }
        index++
        if (costsHearts && hearts <= 0) phase = LessonPhase.OUT_OF_HEARTS
    }

    fun refillWithGems(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            var ok = false
            val p = repository.updateProgress { p ->
                when (val r = p.purchase(ShopItem.REFILL)) {
                    is PurchaseResult.Ok -> {
                        ok = true
                        r.progress
                    }
                    is PurchaseResult.Failed -> p
                }
            }
            hearts = p.hearts
            gems = p.gems
            if (ok) phase = LessonPhase.EXERCISE
            onResult(ok)
        }
    }

    private fun finish() {
        if (phase == LessonPhase.FINISHING || phase == LessonPhase.FINISHED) return
        phase = LessonPhase.FINISHING
        val result = SessionResult(
            mode = mode,
            lessonId = lessonId,
            total = total,
            mistakes = mistakes,
            durationMs = System.currentTimeMillis() - startedAt,
            maxCombo = maxCombo,
            codeSolved = codeSolved,
            fixedMistakes = fixedIds.toList(),
            newMistakes = wrongIds.toList(),
        )
        viewModelScope.launch {
            val settings = repository.currentSettings()
            val reward = repository.updateProgressWith { p -> p.applySession(result, course, Catalog.allCourses(lang), settings) }
            container.lastReward = reward
            phase = LessonPhase.FINISHED
        }
    }

    private fun praise(): String {
        val list = if (lang == AppLanguage.EN) {
            listOf("Great!", "Correct!", "Nice!", "Awesome!", "Exactly!", "Perfect!", "Keep it up!", "Excellent!")
        } else {
            listOf("Super!", "Richtig!", "Klasse!", "Stark!", "Genau so!", "Perfekt!", "Weiter so!", "Ausgezeichnet!")
        }
        val n = combo + 1
        return when {
            n >= 10 -> lang.pick("Unglaublich – $n in Folge!", "Incredible – $n in a row!")
            n >= 5 -> lang.pick("$n in Folge!", "$n in a row!")
            else -> list.random(rnd)
        }
    }
}

package app.pyquest.data

import java.time.LocalDate
import kotlin.random.Random

/** Stellt die Aufgaben für eine Sitzung zusammen. */
object SessionBuilder {

    fun build(
        course: Course,
        progress: UserProgress,
        mode: SessionMode,
        lessonId: String?,
        challenges: List<CodeExercise>,
        seed: Long = System.nanoTime(),
    ): List<Exercise> {
        val rnd = Random(seed)
        return when (mode) {
            SessionMode.LESSON -> course.lesson(lessonId ?: "")?.exercises.orEmpty()
            SessionMode.CHECKPOINT -> checkpoint(course, lessonId, rnd)
            SessionMode.PRACTICE -> practice(course, progress, rnd)
            SessionMode.MISTAKES -> progress.mistakes.mapNotNull { course.exercises[it] }.takeLast(12).shuffled(rnd)
            SessionMode.CODE_PRACTICE -> completedExercises(course, progress)
                .filterIsInstance<CodeExercise>().shuffled(rnd).take(3)
            SessionMode.CHALLENGE -> listOfNotNull(dailyChallenge(challenges))
        }
    }

    fun dailyChallenge(challenges: List<CodeExercise>, date: LocalDate = LocalDate.now()): CodeExercise? {
        if (challenges.isEmpty()) return null
        val idx = Math.floorMod(date.toEpochDay(), challenges.size.toLong()).toInt()
        return challenges[idx]
    }

    private fun completedExercises(course: Course, p: UserProgress): List<Exercise> =
        course.lessons.filter { it.kind != LessonKind.CHECKPOINT && p.isCompleted(it.id) }.flatMap { it.exercises }

    private fun practice(course: Course, p: UserProgress, rnd: Random): List<Exercise> {
        val pool = completedExercises(course, p).shuffled(rnd)
        val code = pool.filterIsInstance<CodeExercise>().take(2)
        val rest = pool.filter { it !is CodeExercise && it !is MatchExercise }.take(10 - code.size)
        val match = pool.filterIsInstance<MatchExercise>().take(1)
        return (rest + match + code).shuffled(rnd).take(11)
    }

    private fun checkpoint(course: Course, lessonId: String?, rnd: Random): List<Exercise> {
        val cp = course.lesson(lessonId ?: "") ?: return emptyList()
        val unit = course.unitOf(cp)
        val sources = unit.lessons.filter { it.kind != LessonKind.CHECKPOINT }
        val picked = mutableListOf<Exercise>()
        sources.forEach { lesson ->
            picked += lesson.exercises.filter { it !is CodeExercise && it !is MatchExercise }.shuffled(rnd).take(2)
        }
        val code = sources.flatMap { it.exercises }.filterIsInstance<CodeExercise>().shuffled(rnd).take(2)
        val body = (picked.shuffled(rnd).take(12) + code).shuffled(rnd)
        return cp.exercises + body
    }
}

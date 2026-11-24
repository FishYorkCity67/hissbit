package app.pyquest.content

import app.pyquest.content.en.EnCourseContent
import app.pyquest.content.js.JsContent
import app.pyquest.content.js.en.JsEnContent
import app.pyquest.data.CodeExercise
import app.pyquest.data.Course
import app.pyquest.data.CourseId
import app.pyquest.data.ExampleProgram
import app.pyquest.data.GlossaryTerm
import app.pyquest.data.PythonTip
import app.pyquest.data.RefCategory
import app.pyquest.data.RefEntry
import app.pyquest.i18n.AppLanguage

/** Alle Inhalte eines Kurses in einer Sprache. */
class CourseBundle(
    val course: Course,
    val challenges: List<CodeExercise>,
    val examples: List<ExampleProgram>,
    val reference: List<RefCategory>,
    val glossary: List<GlossaryTerm>,
    val errors: List<RefEntry>,
    val tips: List<PythonTip>,
)

/**
 * Verzeichnis aller Kurse. Jeden Kurs gibt es auf Deutsch; fehlt eine Übersetzung,
 * wird der deutsche Inhalt verwendet. Lektions-IDs sind in allen Sprachen gleich,
 * damit beim Sprachwechsel der Fortschritt erhalten bleibt.
 */
object Catalog {
    private val bundles = mutableMapOf<Pair<CourseId, AppLanguage>, CourseBundle>()

    private fun build(id: CourseId, lang: AppLanguage): CourseBundle? =
        if (lang == AppLanguage.DE || isComplete(id, lang)) draftBundle(id, lang) else null

    private fun isComplete(id: CourseId, lang: AppLanguage): Boolean = when (id to lang) {
        CourseId.PYTHON to AppLanguage.EN -> EnCourseContent.complete
        CourseId.JAVASCRIPT to AppLanguage.EN -> JsEnContent.complete
        else -> false
    }

    /**
     * Inhalte einer Sprache – auch wenn die Übersetzung noch unvollständig ist (nur für Prüfungen
     * wie ContentExportTest; in der App erscheinen nur vollständige Übersetzungen).
     */
    fun draftBundle(id: CourseId, lang: AppLanguage): CourseBundle? = when (id to lang) {
        CourseId.PYTHON to AppLanguage.EN -> CourseBundle(
            course = EnCourseContent.course,
            challenges = EnCourseContent.challenges,
            examples = EnCourseContent.examples,
            reference = EnCourseContent.reference,
            glossary = EnCourseContent.glossary,
            errors = EnCourseContent.errors,
            tips = EnCourseContent.tips,
        )
        CourseId.PYTHON to AppLanguage.DE -> CourseBundle(
            course = CourseContent.course,
            challenges = CourseContent.challenges,
            examples = CourseContent.examples,
            reference = CourseContent.reference,
            glossary = CourseContent.glossary,
            errors = CourseContent.errors,
            tips = CourseContent.tips,
        )
        CourseId.JAVASCRIPT to AppLanguage.DE -> CourseBundle(
            course = JsContent.course,
            challenges = JsContent.challenges,
            examples = JsContent.examples,
            reference = JsContent.reference,
            glossary = JsContent.glossary,
            errors = JsContent.errors,
            tips = JsContent.tips,
        )
        CourseId.JAVASCRIPT to AppLanguage.EN -> CourseBundle(
            course = JsEnContent.course,
            challenges = JsEnContent.challenges,
            examples = JsEnContent.examples,
            reference = JsEnContent.reference,
            glossary = JsEnContent.glossary,
            errors = JsEnContent.errors,
            tips = JsEnContent.tips,
        )
        else -> null
    }

    /** Kurse, die man in der App wählen kann. */
    val courses: List<CourseId> by lazy {
        listOf(CourseId.PYTHON) + (if (JsContent.ready) listOf(CourseId.JAVASCRIPT) else emptyList())
    }

    /** Das Bündel genau dieser Sprache – oder null, wenn es (noch) keine Übersetzung gibt. */
    fun exactBundle(id: CourseId, lang: AppLanguage): CourseBundle? =
        synchronized(bundles) { bundles[id to lang] ?: build(id, lang)?.also { bundles[id to lang] = it } }

    fun bundle(id: CourseId, lang: AppLanguage): CourseBundle =
        exactBundle(id, lang) ?: exactBundle(id, AppLanguage.DE)!!

    fun course(id: CourseId, lang: AppLanguage): Course = bundle(id, lang).course

    /** Alle Kurse in einer Sprache (für Erfolge über alle Kurse hinweg). */
    fun allCourses(lang: AppLanguage = AppLanguage.DE): List<Course> = courses.map { course(it, lang) }
}

package app.pyquest.content.en

import app.pyquest.content.CourseContent
import app.pyquest.content.buildCourse
import app.pyquest.data.CodeExercise
import app.pyquest.data.Course
import app.pyquest.data.CourseId
import app.pyquest.data.ExampleProgram
import app.pyquest.data.GlossaryTerm
import app.pyquest.data.PythonTip
import app.pyquest.data.RefCategory
import app.pyquest.data.RefEntry
import app.pyquest.i18n.AppLanguage

/**
 * Der Python-Kurs auf Englisch. Aufbau und IDs entsprechen exakt dem deutschen Kurs
 * (geprüft von ContentIdsStableTest), damit beim Sprachwechsel der Fortschritt passt.
 */
object EnCourseContent {
    val course: Course by lazy {
        buildCourse(CourseId.PYTHON, AppLanguage.EN) {
            enUnit01()
            enUnit02()
            enUnit03()
            enUnit04()
            enUnit05()
            enUnit06()
            enUnit07()
            enUnit08()
            enUnit09()
            enUnit10()
            enUnit11()
            enUnit12()
            enUnit13()
            enUnit14()
            enUnit15()
            enUnit16()
        }
    }

    val challenges: List<CodeExercise> by lazy { buildEnChallenges() }
    val examples: List<ExampleProgram> get() = EnExamplePrograms
    val reference: List<RefCategory> get() = EnReferenceCategories
    val glossary: List<GlossaryTerm> get() = EnGlossary
    val errors: List<RefEntry> get() = EnErrorLexicon
    val tips: List<PythonTip> get() = EnPythonTips

    /** Erst wenn alles übersetzt ist, wird der englische Kurs angeboten. */
    val complete: Boolean
        get() = course.units.size == CourseContent.course.units.size &&
            challenges.size == CourseContent.challenges.size &&
            examples.isNotEmpty() && reference.isNotEmpty() && glossary.isNotEmpty() && tips.isNotEmpty()
}

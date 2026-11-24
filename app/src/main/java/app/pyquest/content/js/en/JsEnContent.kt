package app.pyquest.content.js.en

import app.pyquest.content.buildCourse
import app.pyquest.content.js.JsContent
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
 * The JavaScript course in English. Structure and IDs match the German course exactly
 * (checked by ContentIdsStableTest), so progress carries over when the language is switched.
 */
object JsEnContent {
    val course: Course by lazy {
        buildCourse(CourseId.JAVASCRIPT, AppLanguage.EN) {
            jsEnUnit01()
            jsEnUnit02()
            jsEnUnit03()
            jsEnUnit04()
            jsEnUnit05()
            jsEnUnit06()
            jsEnUnit07()
            jsEnUnit08()
            jsEnUnit09()
            jsEnUnit10()
            jsEnUnit11()
            jsEnUnit12()
            jsEnUnit13()
            jsEnUnit14()
        }
    }

    val challenges: List<CodeExercise> by lazy { buildJsEnChallenges() }
    val examples: List<ExampleProgram> get() = JsEnExamplePrograms
    val reference: List<RefCategory> get() = JsEnReferenceCategories
    val glossary: List<GlossaryTerm> get() = JsEnGlossary
    val errors: List<RefEntry> get() = JsEnErrorLexicon
    val tips: List<PythonTip> get() = JsEnTips

    /** The English course is only offered once everything has been translated. */
    val complete: Boolean
        get() = course.units.size == JsContent.course.units.size &&
            challenges.size == JsContent.challenges.size &&
            examples.isNotEmpty() && reference.isNotEmpty() && glossary.isNotEmpty() && tips.isNotEmpty()
}

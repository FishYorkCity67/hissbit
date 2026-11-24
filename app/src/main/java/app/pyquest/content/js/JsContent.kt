package app.pyquest.content.js

import app.pyquest.content.buildCourse
import app.pyquest.data.CodeExercise
import app.pyquest.data.Course
import app.pyquest.data.CourseId
import app.pyquest.data.ExampleProgram
import app.pyquest.data.GlossaryTerm
import app.pyquest.data.PythonTip
import app.pyquest.data.RefCategory
import app.pyquest.data.RefEntry

/**
 * Der JavaScript-Kurs (Deutsch). Alle IDs beginnen mit "js-" – sie dürfen sich nach der
 * Veröffentlichung nie mehr ändern, sonst passt gespeicherter Fortschritt nicht mehr.
 */
object JsContent {
    val course: Course by lazy {
        buildCourse(CourseId.JAVASCRIPT) {
            jsUnit01()
            jsUnit02()
            jsUnit03()
            jsUnit04()
            jsUnit05()
            jsUnit06()
            jsUnit07()
            jsUnit08()
            jsUnit09()
            jsUnit10()
            jsUnit11()
            jsUnit12()
            jsUnit13()
            jsUnit14()
        }
    }

    val challenges: List<CodeExercise> by lazy { buildJsChallenges() }
    val examples: List<ExampleProgram> get() = JsExamplePrograms
    val reference: List<RefCategory> get() = JsReferenceCategories
    val glossary: List<GlossaryTerm> get() = JsGlossary
    val errors: List<RefEntry> get() = JsErrorLexicon
    val tips: List<PythonTip> get() = JsTips

    /** Erst wenn der Kurs Inhalte hat, taucht er in der Kursauswahl auf. */
    val ready: Boolean get() = course.units.isNotEmpty()
}

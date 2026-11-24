package app.pyquest.content

import app.pyquest.data.CodeExercise
import app.pyquest.data.Course
import app.pyquest.data.ExampleProgram
import app.pyquest.data.GlossaryTerm
import app.pyquest.data.PythonTip
import app.pyquest.data.RefCategory
import app.pyquest.data.RefEntry

object CourseContent {
    val course: Course by lazy {
        buildCourse {
            unit01()
            unit02()
            unit03()
            unit04()
            unit05()
            unit06()
            unit07()
            unit08()
            unit09()
            unit10()
            unit11()
            unit12()
            unit13()
            unit14()
            unit15()
            unit16()
        }
    }

    val challenges: List<CodeExercise> by lazy { buildChallenges() }
    val examples: List<ExampleProgram> get() = ExamplePrograms
    val reference: List<RefCategory> get() = ReferenceCategories
    val glossary: List<GlossaryTerm> get() = Glossary
    val errors: List<RefEntry> get() = ErrorLexicon
    val tips: List<PythonTip> get() = PythonTips
}

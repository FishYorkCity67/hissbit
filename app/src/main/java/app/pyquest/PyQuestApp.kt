package app.pyquest

import android.app.Application
import android.content.Context
import app.pyquest.audio.SoundFx
import app.pyquest.content.Catalog
import app.pyquest.data.CourseId
import app.pyquest.data.Repository
import app.pyquest.data.Reward
import app.pyquest.js.JsRunner
import app.pyquest.notify.Reminders
import app.pyquest.python.CodeRunner
import app.pyquest.python.PythonRunner
import app.pyquest.widget.MidnightRefresh
import app.pyquest.widget.WidgetUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** App-Container (der Klassenname bleibt aus PyQuest-Zeiten – er steht im Manifest). */
class PyQuestApp : Application() {
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    lateinit var repository: Repository
        private set
    lateinit var python: PythonRunner
        private set
    lateinit var js: JsRunner
        private set

    /** Belohnung der zuletzt beendeten Sitzung – wird von den Ergebnis-Screens gelesen. */
    @Volatile
    var lastReward: Reward? = null

    /** Laufzeit für eine Kurssprache. */
    fun runnerFor(id: CourseId): CodeRunner = when (id) {
        CourseId.PYTHON -> python
        CourseId.JAVASCRIPT -> js
    }

    /** Sprache für Fehlerhinweise in allen Laufzeiten. */
    fun setHintLanguage(code: String) {
        python.hintLanguage = code
        js.hintLanguage = code
    }

    /** Sprachen, die der Spielplatz anbietet – JavaScript auch ohne gewählten Kurs. */
    val playgroundLanguages: List<CourseId> get() = listOf(CourseId.PYTHON, CourseId.JAVASCRIPT)

    override fun onCreate() {
        super.onCreate()
        repository = Repository(this, appScope) { WidgetUpdater.requestUpdate(this) }
        python = PythonRunner(this)
        python.warmUp()
        js = JsRunner(this)
        Reminders.createChannel(this)
        appScope.launch {
            val settings = repository.settings.filterNotNull().first()
            val course = CourseId.byKey(repository.currentProgress().course)
            // Kurs vorab aufbauen, damit der erste Bildschirm nicht wartet
            Catalog.course(course, settings.language.resolve()).lessons.size
            if (course == CourseId.JAVASCRIPT) js.warmUp()
            SoundFx.enabled = settings.sounds
            if (settings.sounds) SoundFx.preload()
            setHintLanguage(settings.language.resolve().code)
            Reminders.createChannel(this@PyQuestApp, settings.language.resolve())
            Reminders.schedule(this@PyQuestApp, settings, replace = false)
            MidnightRefresh.schedule(this@PyQuestApp, replace = false)
        }
    }
}

val Context.pyApp: PyQuestApp get() = applicationContext as PyQuestApp

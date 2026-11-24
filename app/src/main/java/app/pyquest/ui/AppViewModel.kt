package app.pyquest.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.pyquest.PyQuestApp
import app.pyquest.audio.SoundFx
import app.pyquest.content.Catalog
import app.pyquest.content.CourseBundle
import app.pyquest.data.AppSettings
import app.pyquest.data.BackupFile
import app.pyquest.data.Course
import app.pyquest.data.CourseId
import app.pyquest.data.DEFAULT_PLAYGROUND_CODE
import app.pyquest.data.DEFAULT_PLAYGROUND_CODE_EN
import app.pyquest.data.DEFAULT_PLAYGROUND_JS
import app.pyquest.data.DEFAULT_PLAYGROUND_JS_EN
import app.pyquest.data.ExampleProgram
import app.pyquest.data.PurchaseResult
import app.pyquest.data.SETTINGS_SCHEMA
import app.pyquest.data.ShopItem
import app.pyquest.data.Snippet
import app.pyquest.data.UserProgress
import app.pyquest.data.purchase
import app.pyquest.data.repairStreak
import app.pyquest.data.settled
import app.pyquest.data.unlockAchievements
import app.pyquest.i18n.AppLanguage
import app.pyquest.i18n.Txt
import app.pyquest.notify.Reminders
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class MainTab { LEARN, PRACTICE, CODE, KNOWLEDGE, PROFILE }

class AppViewModel(application: Application) : AndroidViewModel(application) {
    val container = application as PyQuestApp
    private val repository = container.repository
    val python get() = container.python

    val progress: StateFlow<UserProgress?> = repository.progress
    val settings: StateFlow<AppSettings?> = repository.settings

    /** Sprache der Oberfläche und der Kurse. */
    val language: AppLanguage get() = (settings.value ?: AppSettings()).language.resolve()

    /** Der Kurs, der gerade auf dem Lernpfad gezeigt wird. */
    val courseId: CourseId
        get() = CourseId.byKey(progress.value?.course).takeIf { it in Catalog.courses } ?: CourseId.PYTHON
    val bundle: CourseBundle get() = Catalog.bundle(courseId, language)
    val course: Course get() = bundle.course
    val allCourses: List<Course> get() = Catalog.allCourses(language)
    val availableCourses: List<CourseId> get() = Catalog.courses

    /** Tickt regelmäßig, damit Herzen-Countdown und Tageswechsel aktuell bleiben. */
    val now: StateFlow<Long> = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(10_000)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), System.currentTimeMillis())

    var selectedTab by mutableIntStateOf(MainTab.LEARN.ordinal)

    /** Code (mit Sprache), der im Spielplatz geöffnet werden soll (z.B. aus dem Nachschlagewerk). */
    val playgroundRequest = MutableStateFlow<Pair<String, CourseId>?>(null)

    private var lastSettledDay: LocalDate? = null
    private var saveJob: Job? = null

    fun settle() {
        val today = LocalDate.now()
        if (lastSettledDay == today && progress.value != null) return
        lastSettledDay = today
        val courses = allCourses
        viewModelScope.launch {
            repository.updateProgress { p -> p.settled().unlockAchievements(courses).first }
        }
    }

    fun refreshHearts() {
        viewModelScope.launch { repository.updateProgress { it.settled() } }
    }

    fun updateSettings(transform: (AppSettings) -> AppSettings) {
        viewModelScope.launch {
            val old = repository.currentSettings()
            val next = repository.updateSettings(transform)
            SoundFx.enabled = next.sounds
            if (old.reminderEnabled != next.reminderEnabled || old.reminderHour != next.reminderHour ||
                old.reminderMinute != next.reminderMinute || old.language != next.language
            ) {
                Reminders.schedule(container, next, replace = true)
            }
        }
    }

    fun updateProgress(transform: (UserProgress) -> UserProgress) {
        viewModelScope.launch { repository.updateProgress(transform) }
    }

    suspend fun buy(item: ShopItem): PurchaseResult {
        var outcome: PurchaseResult = PurchaseResult.Failed(Txt("Unbekannter Fehler", "Unknown error"))
        val courses = allCourses
        repository.updateProgress { p ->
            when (val r = p.purchase(item)) {
                is PurchaseResult.Ok -> {
                    outcome = r
                    r.progress.unlockAchievements(courses).first
                }
                is PurchaseResult.Failed -> {
                    outcome = r
                    p
                }
            }
        }
        return outcome
    }

    fun equip(itemId: String?) = updateProgress { it.copy(equipped = itemId) }

    fun repairStreak(): Boolean {
        val p = progress.value ?: return false
        if (p.repairStreak(LocalDate.now()) == null) return false
        updateProgress { cur -> cur.repairStreak(LocalDate.now()) ?: cur }
        return true
    }

    fun dismissLostStreak() = updateProgress { it.copy(lostStreak = 0) }

    /** Wechselt den Kurs auf dem Lernpfad (Fortschritt aller Kurse bleibt erhalten). */
    fun switchCourse(id: CourseId) {
        // Laufzeit vorstarten, damit die erste Code-Aufgabe nicht warten muss
        container.runnerFor(id).warmUp()
        updateProgress { it.copy(course = id.key) }
    }

    fun completeOnboarding(name: String, course: CourseId, startUnit: Int, settings: AppSettings) {
        viewModelScope.launch {
            val stored = repository.updateSettings { settings.copy(schema = SETTINGS_SCHEMA) }
            val en = stored.language.resolve() == AppLanguage.EN
            repository.updateProgress {
                it.copy(
                    onboarded = true,
                    name = name.trim(),
                    course = course.key,
                    startUnit = if (course == CourseId.PYTHON) startUnit else it.startUnit,
                    startUnits = if (course == CourseId.PYTHON) it.startUnits else it.startUnits + (course.key to startUnit),
                    createdAt = if (it.createdAt == 0L) System.currentTimeMillis() else it.createdAt,
                    playgroundCode = if (en) DEFAULT_PLAYGROUND_CODE_EN else DEFAULT_PLAYGROUND_CODE,
                    playgroundCodeJs = if (en) DEFAULT_PLAYGROUND_JS_EN else DEFAULT_PLAYGROUND_JS,
                    playgroundLanguage = course.key,
                )
            }
            Reminders.schedule(container, stored, replace = true)
        }
    }

    // ------------------------------------------------------------ Spielplatz

    val playgroundLanguages: List<CourseId> get() = container.playgroundLanguages

    fun playgroundCodeFor(p: UserProgress, lang: CourseId): String =
        if (lang == CourseId.JAVASCRIPT) p.playgroundCodeJs else p.playgroundCode

    fun savePlaygroundCode(lang: CourseId, code: String) {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(800)
            repository.updateProgress {
                if (lang == CourseId.JAVASCRIPT) it.copy(playgroundCodeJs = code) else it.copy(playgroundCode = code)
            }
        }
    }

    fun setPlaygroundLanguage(lang: CourseId) = updateProgress { it.copy(playgroundLanguage = lang.key) }

    fun examplesFor(lang: CourseId): List<ExampleProgram> = Catalog.bundle(lang, language).examples

    fun recordCodeRun() = viewModelScope.launch {
        val courses = allCourses
        repository.updateProgress { p -> p.copy(codeRuns = p.codeRuns + 1).unlockAchievements(courses).first }
    }

    fun saveSnippet(title: String, code: String, lang: CourseId) = updateProgress { p ->
        val now = System.currentTimeMillis()
        val existing = p.snippets.firstOrNull { it.title.equals(title, ignoreCase = true) && it.language == lang.key }
        val list = if (existing != null) {
            p.snippets.map { if (it.id == existing.id) it.copy(code = code, updated = now) else it }
        } else {
            listOf(Snippet(now, title, code, now, lang.key)) + p.snippets
        }
        p.copy(snippets = list.take(100))
    }

    fun deleteSnippet(id: Long) = updateProgress { p -> p.copy(snippets = p.snippets.filterNot { it.id == id }) }

    fun openInPlayground(code: String, lang: CourseId) {
        playgroundRequest.value = code to lang
        selectedTab = MainTab.CODE.ordinal
    }

    // ------------------------------------------------------------ Daten

    fun resetProgress() {
        viewModelScope.launch {
            val keep = repository.currentProgress()
            repository.resetProgress(keepSettings = true)
            repository.updateProgress {
                it.copy(onboarded = true, name = keep.name, course = keep.course, createdAt = System.currentTimeMillis())
            }
        }
    }

    fun restoreBackup(backup: BackupFile) {
        viewModelScope.launch {
            repository.restoreBackup(backup)
            Reminders.schedule(container, repository.currentSettings(), replace = true)
        }
    }
}

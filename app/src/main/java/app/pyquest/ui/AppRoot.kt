package app.pyquest.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.pyquest.BuildConfig
import app.pyquest.MainActivity
import app.pyquest.data.AmbientBackground
import app.pyquest.data.CourseId
import app.pyquest.data.Lesson
import app.pyquest.data.LessonKind
import app.pyquest.data.SessionMode
import app.pyquest.i18n.LocalLanguage
import app.pyquest.ui.ambient.AmbientBackdrop
import app.pyquest.ui.components.LocalCourseId
import app.pyquest.ui.components.LocalOpenInPlayground
import app.pyquest.ui.components.LocalRunner
import app.pyquest.ui.home.HomeScreen
import app.pyquest.ui.home.LessonTheoryScreen
import app.pyquest.ui.home.UnitNotesScreen
import app.pyquest.ui.lesson.LessonScreen
import app.pyquest.ui.lesson.LessonViewModel
import app.pyquest.ui.lesson.ResultFlow
import app.pyquest.ui.onboarding.OnboardingScreen
import app.pyquest.ui.playground.PlaygroundViewModel
import app.pyquest.ui.settings.BackupActions
import app.pyquest.ui.settings.SettingsScreen
import app.pyquest.ui.shop.ShopScreen
import app.pyquest.ui.theme.PyQuestTheme
import app.pyquest.ui.theme.PyTheme
import kotlinx.coroutines.flow.MutableStateFlow

/** Nur für Entwicklungs-Builds (siehe AppRoot): erzwingt einen Hintergrund, ohne die Einstellungen zu ändern. */
object DebugOverrides {
    var ambient by mutableStateOf<AmbientBackground?>(null)
}

object Routes {
    const val HOME = "home"
    const val RESULT = "result"
    const val SETTINGS = "settings"
    const val SHOP = "shop"

    fun lesson(lesson: Lesson) =
        session(if (lesson.kind == LessonKind.CHECKPOINT) SessionMode.CHECKPOINT else SessionMode.LESSON, lesson.id)

    fun session(mode: SessionMode, lessonId: String? = null) = "lesson/${mode.name}/${lessonId ?: "-"}"
    fun theory(lessonId: String) = "theory/$lessonId"
    fun notes(unitId: String) = "notes/$unitId"
}

@Composable
fun AppRoot(openRequest: MutableStateFlow<String?>) {
    val app: AppViewModel = viewModel()
    val playground: PlaygroundViewModel = viewModel()
    val settingsState by app.settings.collectAsStateWithLifecycle()
    val progressState by app.progress.collectAsStateWithLifecycle()
    val storedSettings = settingsState ?: return
    // Nur in Entwicklungs-Builds: ein per Intent erzwungener Hintergrund (für Screenshots), wird nie gespeichert.
    val settings = if (BuildConfig.DEBUG) DebugOverrides.ambient?.let { storedSettings.copy(ambient = it) } ?: storedSettings else storedSettings
    val progress = progressState ?: return

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        app.settle()
        app.refreshHearts()
    }

    PyQuestTheme(settings, accessory = progress.equipped) {
        val language = LocalLanguage.current
        // Fehlerhinweise der Laufzeiten in der App-Sprache
        SideEffect { app.container.setHintLanguage(language.code) }

        if (!progress.onboarded) {
            OnboardingScreen(settings, app.availableCourses) { name, course, startUnit, newSettings ->
                app.completeOnboarding(name, course, startUnit, newSettings)
            }
            return@PyQuestTheme
        }

        val nav = rememberNavController()
        val openInPlayground: (String, CourseId) -> Unit = remember(nav) {
            { code, lang ->
                app.openInPlayground(code, lang)
                nav.popBackStack(Routes.HOME, inclusive = false)
            }
        }
        val open by openRequest.collectAsStateWithLifecycle()
        LaunchedEffect(open) {
            val tab = when (open) {
                MainActivity.OPEN_LESSON -> MainTab.LEARN
                MainActivity.OPEN_PRACTICE -> MainTab.PRACTICE
                MainActivity.OPEN_PLAYGROUND -> MainTab.CODE
                MainActivity.OPEN_KNOWLEDGE -> MainTab.KNOWLEDGE
                else -> null
            }
            if (tab != null) {
                nav.popBackStack(Routes.HOME, inclusive = false)
                app.selectedTab = tab.ordinal
            }
            openRequest.value = null
        }

        // Der Hintergrund erscheint nur auf den Hauptseiten (nicht in Lektionen, Notizen und im Code-Editor):
        // dort liegt das Lernen im Vordergrund, und es spart Akku.
        val backStack by nav.currentBackStackEntryAsState()
        val route = backStack?.destination?.route
        val onMainTab = route == Routes.HOME && app.selectedTab != MainTab.CODE.ordinal
        val ambient = if (onMainTab || route == Routes.SETTINGS || route == Routes.SHOP) settings.ambient else AmbientBackground.NONE

        val courseId = app.courseId
        CompositionLocalProvider(
            LocalCourseId provides courseId,
            LocalRunner provides app.container.runnerFor(courseId),
            LocalOpenInPlayground provides openInPlayground,
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(PyTheme.colors.background)
            ) {
                Crossfade(targetState = ambient, animationSpec = tween(500), label = "ambient") { kind ->
                    if (kind != AmbientBackground.NONE) AmbientBackdrop(kind, Modifier.fillMaxSize())
                }
                Box(
                    Modifier
                        .fillMaxSize()
                        // Kamera-Aussparungen an der Seite (Querformat) freihalten
                        .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                ) {
                    AppNavHost(nav, app, playground)
                }
            }
        }
    }
}

@Composable
private fun AppNavHost(nav: NavHostController, app: AppViewModel, playground: PlaygroundViewModel) {
    val settings by app.settings.collectAsStateWithLifecycle()
    val progress by app.progress.collectAsStateWithLifecycle()
    val now by app.now.collectAsStateWithLifecycle()
    val pyVersion by app.python.pythonVersion.collectAsStateWithLifecycle()

    NavHost(
        navController = nav,
        startDestination = Routes.HOME,
        enterTransition = { fadeIn(tween(220)) + slideInHorizontally(tween(260)) { it / 6 } },
        exitTransition = { fadeOut(tween(160)) },
        popEnterTransition = { fadeIn(tween(220)) },
        popExitTransition = { fadeOut(tween(160)) + slideOutHorizontally(tween(220)) { it / 6 } },
    ) {
        composable(Routes.HOME) {
            val p = progress ?: return@composable
            val s = settings ?: return@composable
            HomeScreen(
                app = app,
                playground = playground,
                progress = p,
                settings = s,
                now = now,
                onStartLesson = { nav.navigate(Routes.lesson(it)) },
                onStartSession = { nav.navigate(Routes.session(it)) },
                onOpenTheory = { nav.navigate(Routes.theory(it.id)) },
                onOpenNotes = { nav.navigate(Routes.notes(it.id)) },
                onOpenShop = { nav.navigate(Routes.SHOP) },
                onOpenSettings = { nav.navigate(Routes.SETTINGS) },
            )
        }
        composable(
            "lesson/{mode}/{lessonId}",
            enterTransition = { slideInVertically(tween(340)) { it } + fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(160)) },
            popExitTransition = { slideOutVertically(tween(300)) { it } + fadeOut(tween(250)) },
        ) { entry ->
            val mode = runCatching { SessionMode.valueOf(entry.arguments?.getString("mode") ?: "") }.getOrDefault(SessionMode.LESSON)
            val lessonId = entry.arguments?.getString("lessonId")?.takeIf { it != "-" }
            val lang = LocalLanguage.current
            val courseId = app.courseId
            val vm: LessonViewModel = viewModel(key = "session-${courseId.key}-${mode.name}-$lessonId") {
                LessonViewModel(app.container, mode, lessonId, lang, courseId)
            }
            LessonScreen(
                vm = vm,
                onClose = { nav.popBackStack() },
                onFinished = {
                    nav.navigate(Routes.RESULT) { popUpTo(Routes.HOME) }
                },
                onGoPractice = {
                    nav.navigate(Routes.session(SessionMode.PRACTICE)) { popUpTo(Routes.HOME) }
                },
            )
        }
        composable(
            Routes.RESULT,
            enterTransition = { fadeIn(tween(300)) + scaleIn(tween(320), initialScale = 0.96f) },
            popExitTransition = { fadeOut(tween(250)) + scaleOut(tween(250), targetScale = 0.98f) },
        ) {
            val reward = remember { app.container.lastReward }
            if (reward == null) {
                LaunchedEffect(Unit) { nav.popBackStack() }
            } else {
                ResultFlow(reward, progress) { nav.popBackStack(Routes.HOME, inclusive = false) }
            }
        }
        composable("theory/{lessonId}") { entry ->
            val lesson = app.course.lesson(entry.arguments?.getString("lessonId") ?: "")
            if (lesson == null) {
                LaunchedEffect(Unit) { nav.popBackStack() }
            } else {
                LessonTheoryScreen(
                    lesson = lesson,
                    unit = app.course.unitOf(lesson),
                    onBack = { nav.popBackStack() },
                    onStart = { nav.navigate(Routes.lesson(lesson)) { popUpTo(Routes.HOME) } },
                    extension = app.courseId.extension,
                )
            }
        }
        composable("notes/{unitId}") { entry ->
            val unit = app.course.unit(entry.arguments?.getString("unitId") ?: "")
            if (unit == null) {
                LaunchedEffect(Unit) { nav.popBackStack() }
            } else {
                UnitNotesScreen(unit, onBack = { nav.popBackStack() }, extension = app.courseId.extension)
            }
        }
        composable(Routes.SETTINGS) {
            val s = settings ?: return@composable
            val repository = app.container.repository
            SettingsScreen(
                settings = s,
                pythonVersion = pyVersion,
                onChange = { app.updateSettings(it) },
                onResetProgress = {
                    app.resetProgress()
                    nav.popBackStack(Routes.HOME, inclusive = false)
                },
                onBack = { nav.popBackStack() },
                backup = remember(repository) {
                    BackupActions(
                        export = { repository.exportBackup(BuildConfig.VERSION_NAME) },
                        parse = { repository.parseBackup(it) },
                        restore = { app.restoreBackup(it) },
                    )
                },
            )
        }
        composable(Routes.SHOP) {
            val p = progress ?: return@composable
            ShopScreen(
                progress = p,
                now = now,
                onBuy = { app.buy(it) },
                onEquip = { app.equip(it) },
                onBack = { nav.popBackStack() },
            )
        }
    }
}

package app.pyquest

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import app.pyquest.data.AmbientBackground
import app.pyquest.ui.AppRoot
import app.pyquest.ui.DebugOverrides
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    private val openRequest = MutableStateFlow<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        applyOrientationPolicy(resources.configuration)
        val repository = pyApp.repository
        splash.setKeepOnScreenCondition {
            repository.progress.value == null || repository.settings.value == null
        }
        if (savedInstanceState == null) openRequest.value = intent?.getStringExtra(EXTRA_OPEN)
        applyDebugExtras(intent)
        setContent { AppRoot(openRequest) }
    }

    /**
     * Handys und schmale Displays (z.B. das Außendisplay des Z Fold) bleiben im Hochformat.
     * Große Displays (Innendisplay, Tablets) dürfen frei drehen. Durch configChanges im
     * Manifest wird die Activity beim Auf- und Zuklappen nicht neu gestartet.
     */
    private fun applyOrientationPolicy(config: Configuration) {
        val wanted = if (config.smallestScreenWidthDp >= 600) {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        if (requestedOrientation != wanted) requestedOrientation = wanted
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        applyOrientationPolicy(newConfig)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.getStringExtra(EXTRA_OPEN)?.let { openRequest.value = it }
        applyDebugExtras(intent)
    }

    /** Nur Entwicklungs-Builds: `--es debug_ambient BLOSSOM` erzwingt einen Hintergrund (für Screenshots), `NONE` hebt es auf. */
    private fun applyDebugExtras(intent: Intent?) {
        if (!BuildConfig.DEBUG) return
        val name = intent?.getStringExtra("debug_ambient") ?: return
        DebugOverrides.ambient = runCatching { AmbientBackground.valueOf(name) }.getOrNull()
    }

    companion object {
        const val EXTRA_OPEN = "open"
        const val OPEN_LESSON = "lesson"
        const val OPEN_PRACTICE = "practice"
        const val OPEN_PLAYGROUND = "playground"
        const val OPEN_KNOWLEDGE = "knowledge"
    }
}

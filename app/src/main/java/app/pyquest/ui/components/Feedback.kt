package app.pyquest.ui.components

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import app.pyquest.audio.Sfx
import app.pyquest.audio.SoundFx
import app.pyquest.ui.theme.FeedbackPrefs
import app.pyquest.ui.theme.LocalFeedbackPrefs

/** Bündelt Haptik und Sounds, jeweils abhängig von den Einstellungen. */
@Stable
class Feedback(private val view: View, private val prefs: FeedbackPrefs) {
    private fun haptic(constant: Int) {
        if (prefs.haptics) view.performHapticFeedback(constant)
    }

    private fun sound(sfx: Sfx) {
        if (prefs.sounds) SoundFx.play(sfx)
    }

    fun tap() = haptic(HapticFeedbackConstants.KEYBOARD_TAP)

    fun tick() = haptic(HapticFeedbackConstants.CLOCK_TICK)

    fun select() {
        haptic(HapticFeedbackConstants.KEYBOARD_TAP)
        sound(Sfx.TAP)
    }

    fun correct() {
        haptic(if (Build.VERSION.SDK_INT >= 30) HapticFeedbackConstants.CONFIRM else HapticFeedbackConstants.VIRTUAL_KEY)
        sound(Sfx.CORRECT)
    }

    fun wrong() {
        haptic(if (Build.VERSION.SDK_INT >= 30) HapticFeedbackConstants.REJECT else HapticFeedbackConstants.LONG_PRESS)
        sound(Sfx.WRONG)
    }

    fun combo() = sound(Sfx.COMBO)

    fun complete() {
        haptic(HapticFeedbackConstants.LONG_PRESS)
        sound(Sfx.COMPLETE)
    }

    fun streak() {
        haptic(HapticFeedbackConstants.LONG_PRESS)
        sound(Sfx.STREAK)
    }

    fun unlock() {
        haptic(HapticFeedbackConstants.LONG_PRESS)
        sound(Sfx.UNLOCK)
    }

    fun coin() = sound(Sfx.COIN)

    /** Monty zischt, wenn man ihn antippt. */
    fun hiss() {
        haptic(HapticFeedbackConstants.CLOCK_TICK)
        sound(Sfx.HISS)
    }

    /** Kurzes "Los!" beim Ausführen von Code. */
    fun run() {
        haptic(HapticFeedbackConstants.KEYBOARD_TAP)
        sound(Sfx.RUN)
    }
}

@Composable
fun rememberFeedback(): Feedback {
    val view = LocalView.current
    val prefs = LocalFeedbackPrefs.current
    return remember(view, prefs) { Feedback(view, prefs) }
}

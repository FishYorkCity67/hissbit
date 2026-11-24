package app.pyquest.ui.theme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WidthClass { COMPACT, MEDIUM, EXPANDED }

/**
 * Größe des App-Fensters. Beim Z Fold 7 ist das Außendisplay ca. 411 dp breit (COMPACT),
 * das Innendisplay ca. 750 dp (MEDIUM) – quer ca. 830 dp.
 */
@Immutable
data class WindowInfo(val widthDp: Int, val heightDp: Int) {
    val widthClass: WidthClass
        get() = when {
            widthDp >= 840 -> WidthClass.EXPANDED
            widthDp >= 600 -> WidthClass.MEDIUM
            else -> WidthClass.COMPACT
        }

    /** Seitenleiste statt Navigationsleiste unten. */
    val useNavRail: Boolean get() = widthDp >= 600

    /** Zusätzliche Info-Spalte neben dem Lernpfad. */
    val showSidePanel: Boolean get() = widthDp >= 640

    /** Editor und Konsole nebeneinander. */
    val splitPlayground: Boolean get() = widthDp >= 700

    /** Zwei Spalten z.B. im Profil. */
    val twoColumns: Boolean get() = widthDp >= 700
}

val LocalWindowInfo = staticCompositionLocalOf { WindowInfo(411, 900) }

@Composable
fun rememberWindowInfo(): WindowInfo {
    val config = LocalConfiguration.current
    return remember(config.screenWidthDp, config.screenHeightDp) {
        WindowInfo(config.screenWidthDp, config.screenHeightDp)
    }
}

object Adaptive {
    val window: WindowInfo
        @Composable @ReadOnlyComposable get() = LocalWindowInfo.current
}

/** Begrenzt die Breite und zentriert den Inhalt – damit auf großen Displays nichts auseinandergezogen wird. */
fun Modifier.centeredMaxWidth(max: Dp): Modifier = this
    .fillMaxWidth()
    .wrapContentWidth(Alignment.CenterHorizontally)
    .widthIn(max = max)
    .fillMaxWidth()

val ReadingWidth = 680.dp
val FormWidth = 600.dp

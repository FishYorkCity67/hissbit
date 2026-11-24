package app.pyquest.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import app.pyquest.data.Accent
import app.pyquest.data.AmbientBackground
import app.pyquest.data.AnimationLevel
import app.pyquest.data.AppSettings
import app.pyquest.data.CornerStyle
import app.pyquest.data.PathStyle
import app.pyquest.data.ThemeMode
import app.pyquest.i18n.LocalLanguage

@Immutable
data class PyShapes(val small: Dp, val medium: Dp, val large: Dp) {
    val smallShape get() = RoundedCornerShape(small)
    val mediumShape get() = RoundedCornerShape(medium)
    val largeShape get() = RoundedCornerShape(large)
}

@Immutable
data class CodeStyle(
    val family: FontFamily,
    val fontSize: TextUnit,
    val theme: EditorTheme,
    val lineNumbers: Boolean,
    val autoIndent: Boolean,
    /** Nur Fira Code zeigt Ligaturen wie ≠ – sonst sehen Anfänger genau das, was sie tippen. */
    val ligatures: Boolean = false,
) {
    val fontFeatures: String? get() = if (ligatures) null else "calt 0, liga 0"
}

@Immutable
data class FeedbackPrefs(val sounds: Boolean, val haptics: Boolean, val confetti: Boolean)

@Immutable
data class MascotPrefs(val show: Boolean, val palette: MascotPalette, val accessory: String?)

@Immutable
data class LayoutPrefs(val pathStyle: PathStyle)

val LocalPyColors = staticCompositionLocalOf { buildColors(false, false, accentColors(Accent.PYTHON)) }
val LocalPyShapes = staticCompositionLocalOf { PyShapes(12.dp, 16.dp, 22.dp) }
val LocalCodeStyle = staticCompositionLocalOf {
    CodeStyle(FontFamily.Monospace, 14.sp, EditorThemes.MontyLight, lineNumbers = true, autoIndent = true)
}
val LocalAnimLevel = staticCompositionLocalOf { AnimationLevel.FULL }
/** Gewählter Hintergrund: Wenn aktiv, zeichnen die Hauptseiten keine deckende Fläche (siehe screenBackground). */
val LocalAmbient = staticCompositionLocalOf { AmbientBackground.NONE }
val LocalFeedbackPrefs = staticCompositionLocalOf { FeedbackPrefs(sounds = true, haptics = true, confetti = true) }
val LocalMascotPrefs = staticCompositionLocalOf { MascotPrefs(true, mascotPalette(app.pyquest.data.MascotColor.BLUE), null) }
val LocalLayoutPrefs = staticCompositionLocalOf { LayoutPrefs(PathStyle.WINDING) }

object PyTheme {
    val colors: PyColors
        @Composable @ReadOnlyComposable get() = LocalPyColors.current
    val shapes: PyShapes
        @Composable @ReadOnlyComposable get() = LocalPyShapes.current
    val code: CodeStyle
        @Composable @ReadOnlyComposable get() = LocalCodeStyle.current
    val anim: AnimationLevel
        @Composable @ReadOnlyComposable get() = LocalAnimLevel.current
    val animate: Boolean
        @Composable @ReadOnlyComposable get() = LocalAnimLevel.current == AnimationLevel.FULL
}

fun CornerStyle.toShapes(): PyShapes = when (this) {
    CornerStyle.ROUND -> PyShapes(10.dp, 14.dp, 20.dp)
    CornerStyle.SOFT -> PyShapes(8.dp, 11.dp, 15.dp)
    CornerStyle.SHARP -> PyShapes(4.dp, 6.dp, 8.dp)
}

private fun resolveAccent(accent: Accent, dark: Boolean, context: Context): AccentColors {
    if (accent == Accent.DYNAMIC && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val scheme = if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        val primary = if (dark) dynamicLightColorScheme(context).primary.lighten(0.25f) else scheme.primary
        return AccentColors(primary, primary.darken(0.75f), scheme.tertiary)
    }
    return accentColors(accent, dark)
}

private fun PyColors.toMaterial(): ColorScheme {
    val scheme = if (isDark) darkColorScheme() else lightColorScheme()
    return scheme.copy(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primarySoft,
        onPrimaryContainer = text,
        secondary = secondary,
        onSecondary = Color.Black,
        tertiary = gem,
        surfaceTint = Color.Transparent,
        background = background,
        onBackground = text,
        surface = surface,
        onSurface = text,
        surfaceVariant = surfaceAlt,
        onSurfaceVariant = textMuted,
        surfaceContainerLowest = background,
        surfaceContainerLow = surface,
        surfaceContainer = surface,
        surfaceContainerHigh = surfaceAlt,
        surfaceContainerHighest = surfaceAlt,
        outline = border,
        outlineVariant = border,
        error = error,
        onError = Color.White,
        inverseSurface = text,
        inverseOnSurface = background,
    )
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun PyQuestTheme(
    settings: AppSettings,
    accessory: String? = null,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val dark = when (settings.themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.AMOLED -> true
    }
    val amoled = settings.themeMode == ThemeMode.AMOLED
    val context = LocalContext.current
    val accent = remember(settings.accent, dark) { resolveAccent(settings.accent, dark, context) }
    val colors = remember(dark, amoled, accent) { buildColors(dark, amoled, accent) }
    val scheme = remember(colors) { colors.toMaterial() }
    val typography = remember(settings.uiFont) {
        pyTypography(Fonts.ui(settings.uiFont), settings.uiFont, Fonts.display(settings.uiFont))
    }
    val language = remember(settings.language) { settings.language.resolve() }
    val shapes = remember(settings.corners) { settings.corners.toShapes() }
    val codeStyle = remember(settings.codeFont, settings.codeFontSize, settings.editorTheme, dark, settings.lineNumbers, settings.autoIndent) {
        CodeStyle(
            family = Fonts.code(settings.codeFont),
            fontSize = settings.codeFontSize.sp,
            theme = EditorThemes.resolve(settings.editorTheme, dark),
            lineNumbers = settings.lineNumbers,
            autoIndent = settings.autoIndent,
            ligatures = settings.codeFont == app.pyquest.data.CodeFont.FIRA,
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity()?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !dark
                isAppearanceLightNavigationBars = !dark
            }
        }
    }

    val density = LocalDensity.current
    val scaled = remember(density, settings.textScale) {
        Density(density.density, density.fontScale * settings.textScale.coerceIn(0.8f, 1.4f))
    }
    val window = rememberWindowInfo()

    CompositionLocalProvider(
        LocalLanguage provides language,
        LocalWindowInfo provides window,
        LocalPyColors provides colors,
        LocalPyShapes provides shapes,
        LocalCodeStyle provides codeStyle,
        LocalAnimLevel provides settings.animations,
        LocalAmbient provides settings.ambient,
        LocalFeedbackPrefs provides FeedbackPrefs(settings.sounds, settings.haptics, settings.confetti),
        LocalMascotPrefs provides MascotPrefs(settings.showMascot, mascotPalette(settings.mascotColor), accessory),
        LocalLayoutPrefs provides LayoutPrefs(settings.pathStyle),
        LocalDensity provides scaled,
    ) {
        MaterialTheme(
            colorScheme = scheme,
            typography = typography,
            shapes = Shapes(
                extraSmall = RoundedCornerShape(shapes.small / 2),
                small = shapes.smallShape,
                medium = shapes.mediumShape,
                large = shapes.largeShape,
                extraLarge = RoundedCornerShape(shapes.large + 4.dp),
            ),
            content = content,
        )
    }
}

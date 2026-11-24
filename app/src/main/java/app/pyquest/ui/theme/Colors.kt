package app.pyquest.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import app.pyquest.data.Accent
import app.pyquest.data.MascotColor

/** Dunkle Schrift für helle Farbflächen (warmes Fast-Schwarz). */
val Ink = Color(0xFF1D1B19)

@Immutable
data class PyColors(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceAlt: Color,
    val border: Color,
    val text: Color,
    val textMuted: Color,
    val primary: Color,
    val primaryDark: Color,
    val onPrimary: Color,
    val primarySoft: Color,
    /** Primärfarbe als Textfarbe (im hellen Design dunkler, damit sie gut lesbar ist). */
    val primaryText: Color,
    val secondary: Color,
    val success: Color,
    val successDark: Color,
    val successSoft: Color,
    val onSuccessSoft: Color,
    val error: Color,
    val errorDark: Color,
    val errorSoft: Color,
    val onErrorSoft: Color,
    val locked: Color,
    val lockedDark: Color,
    val lockedIcon: Color,
    val gold: Color,
    val goldDark: Color,
    val streak: Color,
    val streakDark: Color,
    /** "Bits" – die Währung der App (Feldname noch aus PyQuest-Zeiten). */
    val gem: Color,
    val gemDark: Color,
    val heart: Color,
    val heartDark: Color,
    val freeze: Color,
    val xp: Color,
    /** Früher für Leuchteffekte – das schlichte Design kommt ohne aus. */
    val glow: Color,
)

@Immutable
data class AccentColors(val primary: Color, val dark: Color, val secondary: Color)

/**
 * Akzentfarben: gedeckt und edel – je eine Fassung für helles und dunkles Design
 * (im dunklen etwas heller, damit sie auf dunklem Grund nicht absaufen).
 * Das Hissbit-Grün ist seit 2.3 ein tiefes Jade, dazu ein mattes Antik-Gold.
 */
fun accentColors(accent: Accent, dark: Boolean = false): AccentColors {
    fun pick(light: AccentColors, night: AccentColors) = if (dark) night else light
    return when (accent) {
        Accent.HISSBIT, Accent.DYNAMIC -> pick(
            AccentColors(Color(0xFF1D7357), Color(0xFF165A45), Color(0xFFC39A3C)),
            AccentColors(Color(0xFF52B891), Color(0xFF3B9674), Color(0xFFD9B055)),
        )
        Accent.PYTHON -> pick(
            AccentColors(Color(0xFF3672A8), Color(0xFF2A5A86), Color(0xFFD9AE2C)),
            AccentColors(Color(0xFF5C9BD6), Color(0xFF4280BC), Color(0xFFF2CB52)),
        )
        Accent.GREEN -> pick(
            AccentColors(Color(0xFF5A9A2A), Color(0xFF477A21), Color(0xFFC39A3C)),
            AccentColors(Color(0xFF8CC653), Color(0xFF6FA53D), Color(0xFFD9B055)),
        )
        Accent.OCEAN -> pick(
            AccentColors(Color(0xFF1F7BD1), Color(0xFF1862A8), Color(0xFF14918A)),
            AccentColors(Color(0xFF52A3EE), Color(0xFF3486D2), Color(0xFF3BBDB4)),
        )
        Accent.VIOLET -> pick(
            AccentColors(Color(0xFF6E4FC2), Color(0xFF573DA0), Color(0xFFC03A6D)),
            AccentColors(Color(0xFF9D86E3), Color(0xFF8069C9), Color(0xFFE8709D)),
        )
        Accent.SUNSET -> pick(
            AccentColors(Color(0xFFD5611B), Color(0xFFB34C0C), Color(0xFFC63F5A)),
            AccentColors(Color(0xFFF58E4C), Color(0xFFE2712A), Color(0xFFEC6F85)),
        )
        Accent.CHERRY -> pick(
            AccentColors(Color(0xFFBF3F6C), Color(0xFFA02F57), Color(0xFFC39A3C)),
            AccentColors(Color(0xFFE9709F), Color(0xFFD15282), Color(0xFFD9B055)),
        )
        Accent.MINT -> pick(
            AccentColors(Color(0xFF0E8A80), Color(0xFF0A6D65), Color(0xFFC39A3C)),
            AccentColors(Color(0xFF33B9AE), Color(0xFF23978D), Color(0xFFD9B055)),
        )
        Accent.INDIGO -> pick(
            AccentColors(Color(0xFF4150BF), Color(0xFF33409C), Color(0xFFC39A3C)),
            AccentColors(Color(0xFF8090EE), Color(0xFF6474D4), Color(0xFFD9B055)),
        )
    }
}

/**
 * Farben der Einheiten auf dem Lernpfad: wenige, gedeckte Edelsteintöne mit gleicher Helligkeit,
 * damit der Pfad abwechslungsreich, aber nicht bunt wirkt.
 */
val UnitPalette: List<Pair<Color, Color>> = listOf(
    Color(0xFF2A8566) to Color(0xFF1F6A51), // Jade
    Color(0xFF4A78B0) to Color(0xFF385E8E), // Schieferblau
    Color(0xFFC47048) to Color(0xFF9E5738), // Terrakotta
    Color(0xFF2A8A8A) to Color(0xFF206C6C), // Petrol
    Color(0xFFBC5C7C) to Color(0xFF964863), // Altrosa
    Color(0xFFB88C2E) to Color(0xFF936F22), // Antik-Gold
)

fun unitColors(index: Int): Pair<Color, Color> = UnitPalette[Math.floorMod(index, UnitPalette.size)]

/** Gut lesbare Schrift-/Iconfarbe auf einer Farbfläche. */
fun Color.onColor(): Color = if (luminance() > 0.35f) Ink else Color.White

/**
 * Warme Neutraltöne statt kühlem Grau: ein Hauch Elfenbein im hellen, warmes Anthrazit im dunklen Design.
 * Karten sind im hellen Design reinweiß auf dem Elfenbein-Grund und werden nur durch eine feine Linie abgesetzt.
 */
fun buildColors(dark: Boolean, amoled: Boolean, accent: AccentColors): PyColors {
    val onPrimary = accent.primary.onColor()
    return when {
        amoled -> PyColors(
            isDark = true,
            background = Color(0xFF000000),
            surface = Color(0xFF0F0E0D),
            surfaceAlt = Color(0xFF181715),
            border = Color(0xFF262421),
            text = Color(0xFFF1EDE7),
            textMuted = Color(0xFFA29C93),
            primary = accent.primary, primaryDark = accent.dark, onPrimary = onPrimary,
            primarySoft = accent.primary.copy(alpha = 0.14f),
            primaryText = accent.primary,
            secondary = accent.secondary,
            success = Color(0xFF52B891), successDark = Color(0xFF3B9674),
            successSoft = Color(0xFF0F1F18), onSuccessSoft = Color(0xFF86D6B5),
            error = Color(0xFFEB6A66), errorDark = Color(0xFFC94A46),
            errorSoft = Color(0xFF241413), onErrorSoft = Color(0xFFFFA3A0),
            locked = Color(0xFF211F1D), lockedDark = Color(0xFF1A1816), lockedIcon = Color(0xFF625D56),
            gold = Color(0xFFD9B055), goldDark = Color(0xFFB58F3A),
            streak = Color(0xFFF0914D), streakDark = Color(0xFFD06F2A),
            gem = Color(0xFF6AA2F0), gemDark = Color(0xFF4A85D8),
            heart = Color(0xFFEB6A66), heartDark = Color(0xFFC94A46),
            freeze = Color(0xFF62B9EE), xp = Color(0xFFD9B055),
            glow = Color.Transparent,
        )
        dark -> PyColors(
            isDark = true,
            background = Color(0xFF131211),
            surface = Color(0xFF1B1A18),
            surfaceAlt = Color(0xFF252321),
            border = Color(0xFF34312D),
            text = Color(0xFFF2EEE8),
            textMuted = Color(0xFFA59F96),
            primary = accent.primary, primaryDark = accent.dark, onPrimary = onPrimary,
            primarySoft = accent.primary.copy(alpha = 0.15f),
            primaryText = accent.primary,
            secondary = accent.secondary,
            success = Color(0xFF52B891), successDark = Color(0xFF3B9674),
            successSoft = Color(0xFF16261F), onSuccessSoft = Color(0xFF86D6B5),
            error = Color(0xFFEB6A66), errorDark = Color(0xFFC94A46),
            errorSoft = Color(0xFF2E1A19), onErrorSoft = Color(0xFFFFA3A0),
            locked = Color(0xFF2C2A27), lockedDark = Color(0xFF242220), lockedIcon = Color(0xFF6A655D),
            gold = Color(0xFFD9B055), goldDark = Color(0xFFB58F3A),
            streak = Color(0xFFF0914D), streakDark = Color(0xFFD06F2A),
            gem = Color(0xFF6AA2F0), gemDark = Color(0xFF4A85D8),
            heart = Color(0xFFEB6A66), heartDark = Color(0xFFC94A46),
            freeze = Color(0xFF62B9EE), xp = Color(0xFFD9B055),
            glow = Color.Transparent,
        )
        else -> PyColors(
            isDark = false,
            background = Color(0xFFFBFAF7),
            surface = Color(0xFFFFFFFF),
            surfaceAlt = Color(0xFFF5F2ED),
            border = Color(0xFFE9E4DC),
            text = Color(0xFF1D1B19),
            textMuted = Color(0xFF6E6860),
            primary = accent.primary, primaryDark = accent.dark, onPrimary = onPrimary,
            primarySoft = accent.primary.copy(alpha = 0.10f),
            primaryText = if (accent.primary.luminance() > 0.35f) accent.primary.darken(0.62f) else accent.primary,
            secondary = accent.secondary,
            success = Color(0xFF1D7357), successDark = Color(0xFF165A45),
            successSoft = Color(0xFFE8F2EC), onSuccessSoft = Color(0xFF17624A),
            error = Color(0xFFCC4540), errorDark = Color(0xFFA93733),
            errorSoft = Color(0xFFFBEDEA), onErrorSoft = Color(0xFFA8322C),
            locked = Color(0xFFEEEAE4), lockedDark = Color(0xFFE0DBD3), lockedIcon = Color(0xFFA39C92),
            gold = Color(0xFFC39A3C), goldDark = Color(0xFF9A7425),
            streak = Color(0xFFE27B3A), streakDark = Color(0xFFBF6226),
            gem = Color(0xFF3F7BD0), gemDark = Color(0xFF2F62AB),
            heart = Color(0xFFD2504C), heartDark = Color(0xFFAE3E3A),
            freeze = Color(0xFF3B8FCB), xp = Color(0xFFC39A3C),
            glow = Color.Transparent,
        )
    }
}

@Immutable
data class MascotPalette(val body: Color, val dark: Color, val belly: Color)

fun mascotPalette(color: MascotColor): MascotPalette = when (color) {
    MascotColor.BLUE -> MascotPalette(Color(0xFF3F80C4), Color(0xFF285A8F), Color(0xFFFFE25C))
    MascotColor.GREEN -> MascotPalette(Color(0xFF4CAF5A), Color(0xFF2F8A40), Color(0xFFD5F2B8))
    MascotColor.YELLOW -> MascotPalette(Color(0xFFF2BE2E), Color(0xFFC79410), Color(0xFFFFF4C2))
    MascotColor.PURPLE -> MascotPalette(Color(0xFF8F6AD6), Color(0xFF6A49AE), Color(0xFFEADCFF))
    MascotColor.PINK -> MascotPalette(Color(0xFFF07AAE), Color(0xFFC95688), Color(0xFFFFE0EE))
    MascotColor.RED -> MascotPalette(Color(0xFFE65A52), Color(0xFFBD3D35), Color(0xFFFFD6D2))
    MascotColor.TEAL -> MascotPalette(Color(0xFF36B37E), Color(0xFF22885C), Color(0xFFFFE27A))
}

/** Farbe abdunkeln (z.B. für gedrückte Knöpfe). */
fun Color.darken(factor: Float = 0.78f): Color =
    Color(red * factor, green * factor, blue * factor, alpha)

/** Farbe aufhellen. */
fun Color.lighten(amount: Float = 0.12f): Color =
    Color(red + (1f - red) * amount, green + (1f - green) * amount, blue + (1f - blue) * amount, alpha)

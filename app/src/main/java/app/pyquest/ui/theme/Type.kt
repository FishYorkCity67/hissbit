package app.pyquest.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import app.pyquest.R
import app.pyquest.data.CodeFont
import app.pyquest.data.UiFont

@OptIn(ExperimentalTextApi::class)
private fun variableFamily(res: Int, vararg weights: Int): FontFamily = FontFamily(
    weights.map { w ->
        Font(
            res,
            weight = FontWeight(w),
            variationSettings = FontVariation.Settings(FontVariation.weight(w)),
        )
    }
)

object Fonts {
    val nunito: FontFamily by lazy { variableFamily(R.font.nunito, 400, 500, 600, 700, 800, 900) }
    val lexend: FontFamily by lazy { variableFamily(R.font.lexend, 300, 400, 500, 600, 700, 800, 900) }
    val jetbrains: FontFamily by lazy { variableFamily(R.font.jetbrains_mono, 400, 500, 600, 700, 800) }
    val fira: FontFamily by lazy { variableFamily(R.font.fira_code, 400, 500, 600, 700) }
    val sourceCode: FontFamily by lazy { variableFamily(R.font.source_code_pro, 400, 500, 600, 700, 800) }

    /** "Elegant": Überschriften in einer Serifenschrift, Text in einer ruhigen Grotesk. */
    val luxeDisplay: FontFamily get() = FontFamily.Serif
    val luxeText: FontFamily get() = FontFamily.SansSerif

    /** Schrift für Text, Beschriftungen und Knöpfe. */
    fun ui(font: UiFont): FontFamily = when (font) {
        UiFont.LUXE -> luxeText
        UiFont.NUNITO -> nunito
        UiFont.LEXEND -> lexend
        UiFont.SYSTEM -> FontFamily.SansSerif
    }

    /** Schrift für große Überschriften – nur "Elegant" nutzt hier eine andere als für den Text. */
    fun display(font: UiFont): FontFamily = if (font == UiFont.LUXE) luxeDisplay else ui(font)

    fun code(font: CodeFont): FontFamily = when (font) {
        CodeFont.JETBRAINS -> jetbrains
        CodeFont.FIRA -> fira
        CodeFont.SOURCE -> sourceCode
        CodeFont.SYSTEM -> FontFamily.Monospace
    }
}

/**
 * Standard ist seit 2.3 "Elegant": große Überschriften in einer Serifenschrift mit mittlerem Gewicht,
 * alles andere in einer ruhigen, hellen Grotesk – wenige, feine Schriftschnitte statt kräftiger Fettdrucke.
 * Nunito (rund, freundlich) und Lexend (breit) bleiben als Alternativen wählbar.
 */
fun pyTypography(family: FontFamily, font: UiFont = UiFont.LUXE, display: FontFamily = family): Typography {
    val b = Typography()
    val rounded = font == UiFont.NUNITO
    val luxe = font == UiFont.LUXE
    fun w(lexend: Int, nunito: Int, elegant: Int) = FontWeight(
        when {
            luxe -> elegant
            rounded -> nunito
            else -> lexend
        }
    )
    fun TextStyle.f(
        weight: FontWeight,
        size: Int? = null,
        line: Int? = null,
        spacing: Double? = null,
        serif: Boolean = false,
    ) = copy(
        fontFamily = if (serif) display else family,
        fontWeight = weight,
        fontSize = size?.sp ?: fontSize,
        lineHeight = line?.sp ?: lineHeight,
        letterSpacing = spacing?.sp ?: letterSpacing,
    )
    return Typography(
        displayLarge = b.displayLarge.f(w(700, 900, 500), spacing = if (luxe) -0.8 else -1.0, serif = true),
        displayMedium = b.displayMedium.f(w(700, 900, 500), spacing = if (luxe) -0.5 else -0.6, serif = true),
        displaySmall = b.displaySmall.f(w(700, 900, 500), spacing = if (luxe) -0.3 else -0.4, serif = true),
        headlineLarge = b.headlineLarge.f(w(700, 800, 500), spacing = if (luxe) -0.3 else -0.4, serif = true),
        headlineMedium = b.headlineMedium.f(w(700, 800, 500), spacing = if (luxe) -0.2 else -0.3, serif = true),
        headlineSmall = b.headlineSmall.f(w(600, 800, 600), spacing = if (luxe) -0.1 else -0.2, serif = true),
        titleLarge = b.titleLarge.f(w(600, 800, 600), spacing = if (luxe) 0.0 else -0.1, serif = true),
        titleMedium = b.titleMedium.f(w(600, 800, 600), size = 17),
        titleSmall = b.titleSmall.f(w(600, 700, 600)),
        bodyLarge = b.bodyLarge.f(w(400, 600, 400), size = 17, line = 26),
        bodyMedium = b.bodyMedium.f(w(400, 600, 400), size = 15, line = 22),
        bodySmall = b.bodySmall.f(w(400, 600, 400)),
        labelLarge = b.labelLarge.f(w(600, 800, 600), size = 15, spacing = if (luxe) 0.4 else 0.2),
        labelMedium = b.labelMedium.f(w(500, 700, 600), spacing = if (luxe) 0.5 else 0.3),
        labelSmall = b.labelSmall.f(w(500, 700, 500), spacing = if (luxe) 0.5 else 0.3),
    )
}
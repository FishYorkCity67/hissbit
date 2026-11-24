package app.pyquest.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.pyquest.data.CourseId
import app.pyquest.ui.theme.PyTheme

/** Markenfarbe von Hissbit – unabhängig von der gewählten Akzentfarbe. */
val HissbitGreen = Color(0xFF1D7357)
private val HissbitGold = Color(0xFFF2B640)

/**
 * Das Hissbit-Zeichen: eine Schlange, die ein "S" bildet, auf einer grünen Fläche –
 * daneben das "Bit", das sie gleich schnappt (wie im Spiel "Snake").
 */
@Composable
fun HissbitLogo(modifier: Modifier = Modifier, size: Dp = 36.dp) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        drawRoundRect(HissbitGreen, size = Size(w, w), cornerRadius = CornerRadius(w * 0.28f))
        // Schlangen-S
        val s = Path().apply {
            moveTo(w * 0.68f, w * 0.30f)
            cubicTo(w * 0.60f, w * 0.20f, w * 0.30f, w * 0.22f, w * 0.32f, w * 0.38f)
            cubicTo(w * 0.34f, w * 0.52f, w * 0.68f, w * 0.48f, w * 0.68f, w * 0.64f)
            cubicTo(w * 0.68f, w * 0.80f, w * 0.38f, w * 0.80f, w * 0.30f, w * 0.70f)
        }
        drawPath(s, Color.White, style = Stroke(width = w * 0.13f, cap = StrokeCap.Round))
        // Auge am Kopf
        drawCircle(HissbitGreen, radius = w * 0.028f, center = Offset(w * 0.635f, w * 0.285f))
        // das Bit
        drawRoundRect(
            HissbitGold,
            topLeft = Offset(w * 0.745f, w * 0.385f),
            size = Size(w * 0.09f, w * 0.09f),
            cornerRadius = CornerRadius(w * 0.018f),
        )
    }
}

/** Logo mit Schriftzug „hissbit“. */
@Composable
fun HissbitWordmark(modifier: Modifier = Modifier, logoSize: Dp = 34.dp) {
    val c = PyTheme.colors
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        HissbitLogo(size = logoSize)
        Spacer(Modifier.width(10.dp))
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = c.text)) { append("hiss") }
                withStyle(SpanStyle(color = c.primaryText)) { append("bit") }
            },
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

/** Kurzname einer Programmiersprache für das Sprach-Abzeichen. */
val CourseId.shortLabel: String
    get() = when (this) {
        CourseId.PYTHON -> "Py"
        CourseId.JAVASCRIPT -> "JS"
    }

/** Farben des Sprach-Abzeichens (Fläche, Schrift) – angelehnt an die bekannten Sprachfarben. */
fun CourseId.badgeColors(): Pair<Color, Color> = when (this) {
    CourseId.PYTHON -> Color(0xFF3672A8) to Color.White
    CourseId.JAVASCRIPT -> Color(0xFFF0C419) to Color(0xFF1B1C1E)
}

/** Kleines Abzeichen mit dem Kürzel der Sprache – statt eines Emojis. */
@Composable
fun LanguageBadge(course: CourseId, modifier: Modifier = Modifier, size: Dp = 22.dp) {
    val (bg, fg) = course.badgeColors()
    // feste Größe passend zum Kästchen – unabhängig von der eingestellten Schriftgröße
    val fontSize = with(LocalDensity.current) { (size * 0.42f).toSp() }
    Box(
        modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.26f))
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            course.shortLabel,
            color = fg,
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
        )
    }
}

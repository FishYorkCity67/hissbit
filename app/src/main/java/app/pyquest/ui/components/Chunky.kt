package app.pyquest.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pyquest.ui.theme.PyTheme
import app.pyquest.ui.theme.darken
import app.pyquest.ui.theme.onColor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Wie stark ein gedrücktes Element abdunkelt bzw. schrumpft. */
private const val PressDarken = 0.88f

/**
 * Hissbits Knopf: eine flache, einfarbige Fläche mit klarer Beschriftung.
 * Beim Drücken wird er minimal kleiner und etwas dunkler – ohne Verläufe oder Leuchten.
 */
@Composable
fun ChunkyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = PyTheme.colors.primary,
    @Suppress("UNUSED_PARAMETER") shadow: Color = PyTheme.colors.primaryDark,
    contentColor: Color = color.onColor(),
    outlined: Boolean = false,
    icon: ImageVector? = null,
    loading: Boolean = false,
    height: Dp = 52.dp,
    uppercase: Boolean = false,
    sound: Boolean = true,
) {
    val c = PyTheme.colors
    val feedback = rememberFeedback()
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val active = enabled && !loading
    val press by animateFloatAsState(
        targetValue = if (pressed && active) 1f else 0f,
        animationSpec = tween(90),
        label = "press",
    )
    // Kapselform wirkt edler; wer "Eckig" gewählt hat, behält die eckigen Knöpfe.
    val shape = if (PyTheme.shapes.medium <= 6.dp) PyTheme.shapes.mediumShape else RoundedCornerShape(50)
    val fg = when {
        !enabled -> c.lockedIcon
        outlined -> if (color == c.primary) c.primaryText else color
        else -> contentColor
    }
    val surface = when {
        !enabled -> Modifier.background(c.locked)
        outlined -> Modifier
            .background(lerp(c.surface, c.surfaceAlt, press))
            .border(1.dp, c.border, shape)
        else -> Modifier.background(lerp(color, color.darken(PressDarken), press))
    }
    Box(
        modifier
            .heightIn(min = height)
            .graphicsLayer {
                val s = 1f - 0.02f * press
                scaleX = s
                scaleY = s
            }
            .clip(shape)
            .then(surface)
            .clickable(interactionSource = interaction, indication = null, enabled = active) {
                if (sound) feedback.select() else feedback.tap()
                onClick()
            }
            .semantics { role = Role.Button }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = fg, strokeWidth = 2.5.dp)
            } else {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(20.dp))
                    if (text.isNotEmpty()) Spacer(Modifier.width(8.dp))
                }
                if (text.isNotEmpty()) {
                    Text(
                        text = if (uppercase) text.uppercase() else text,
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp),
                        color = fg,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

/**
 * Grundfläche für Karten, Kacheln und Knoten: einfarbig und flach. Mit Rahmen
 * ([borderWidth] > 0) eine ruhige Karte, ohne Rahmen eine farbige Fläche.
 * [edge] und [depth] stammen aus dem alten 3D-Stil und werden nur noch für den
 * Rahmen bzw. gar nicht mehr gebraucht.
 */
@Composable
fun ChunkySurface(
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    face: Color = PyTheme.colors.surface,
    edge: Color = PyTheme.colors.border,
    borderColor: Color = edge,
    borderWidth: Dp = 1.dp,
    @Suppress("UNUSED_PARAMETER") depth: Dp = 0.dp,
    shape: Shape = PyTheme.shapes.mediumShape,
    minHeight: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val clickable = onClick != null && enabled
    val press by animateFloatAsState(
        targetValue = if (pressed && clickable) 1f else 0f,
        animationSpec = tween(90),
        label = "press",
    )
    val c = PyTheme.colors
    // Halbtransparente Flächen brauchen einen deckenden Untergrund.
    val underlay = c.background
    val pressedFace = if (face.alpha < 1f || borderWidth > 0.dp) lerp(face, c.text.copy(alpha = face.alpha), 0.05f) else face.darken(PressDarken)
    val clickModifier = if (onClick != null) {
        Modifier
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, onClick = onClick)
            .semantics { role = Role.Button }
    } else Modifier

    Box(
        modifier
            .graphicsLayer {
                val s = 1f - 0.015f * press
                scaleX = s
                scaleY = s
            }
            .heightIn(min = minHeight)
            .clip(shape)
            .then(if (face.alpha < 1f) Modifier.background(underlay) else Modifier)
            .background(lerp(face, pressedFace, press))
            .then(if (borderWidth > 0.dp) Modifier.border(borderWidth, borderColor, shape) else Modifier)
            .then(clickModifier)
            .padding(contentPadding),
        contentAlignment = contentAlignment,
        content = content,
    )
}

/** Schlichter Fortschrittsbalken mit sanfter Animation. */
@Composable
fun PyProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = PyTheme.colors.success,
    track: Color = PyTheme.colors.locked,
    height: Dp = 10.dp,
) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = 0.9f, stiffness = 300f),
        label = "progress",
    )
    Box(
        modifier
            .height(height)
            .clip(CircleShape)
            .background(track)
    ) {
        if (animated > 0.001f) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animated.coerceAtLeast(0.03f))
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

/** Fortschritt in einer Lektion – ein durchgehender, ruhiger Balken. */
@Composable
fun BitProgress(
    done: Int,
    total: Int,
    modifier: Modifier = Modifier,
    color: Color = PyTheme.colors.success,
    track: Color = PyTheme.colors.locked,
    height: Dp = 10.dp,
) {
    PyProgressBar(if (total <= 0) 0f else done.toFloat() / total, modifier, color, track, height)
}

/** Skaliert ein Element beim Drücken leicht – für Karten ohne eigene Animation. */
fun Modifier.pressScale(pressed: Boolean): Modifier = this.graphicsLayer {
    val s = if (pressed) 0.98f else 1f
    scaleX = s
    scaleY = s
}

@Composable
fun Pill(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = PyTheme.colors.primaryText,
    background: Color = color.copy(alpha = 0.12f),
    icon: ImageVector? = null,
) {
    Row(
        modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
        }
        Text(text, color = color, style = MaterialTheme.typography.labelMedium, fontSize = 12.sp)
    }
}

/** Die Währung "Bits": ein flaches Sechseck mit einer "1". */
@Composable
fun BitsIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, color: Color = PyTheme.colors.gem) {
    Canvas(modifier.size(size)) {
        val r = this.size.minDimension / 2f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val hex = Path().apply {
            for (i in 0 until 6) {
                val a = PI / 3 * i - PI / 2
                val p = Offset(center.x + r * 0.96f * cos(a).toFloat(), center.y + r * 0.96f * sin(a).toFloat())
                if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }
        drawPath(hex, color)
        drawLine(
            Color.White,
            Offset(center.x, center.y - r * 0.36f),
            Offset(center.x, center.y + r * 0.36f),
            strokeWidth = r * 0.2f,
            cap = StrokeCap.Round,
        )
        drawLine(
            Color.White,
            Offset(center.x, center.y - r * 0.36f),
            Offset(center.x - r * 0.18f, center.y - r * 0.2f),
            strokeWidth = r * 0.18f,
            cap = StrokeCap.Round,
        )
    }
}

package app.pyquest.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.pyquest.data.AnimationLevel
import app.pyquest.ui.theme.LocalFeedbackPrefs
import app.pyquest.ui.theme.PyTheme
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private class Particle(
    val x0: Float, val y0: Float, val vx: Float, val vy: Float,
    val rot: Float, val vr: Float, val w: Float, val h: Float,
    val color: Color, val flip: Float, val circle: Boolean,
)

private val ConfettiColors = listOf(
    Color(0xFF22A060), Color(0xFF2F7DD6), Color(0xFFE07A1F),
    Color(0xFF15958F), Color(0xFFD64D6F), Color(0xFFD69E12),
)

/**
 * Konfetti aus kleinen Chips und Punkten in den Farben des Lernpfads.
 * Startet neu, sobald sich [trigger] ändert. [originY] = Startpunkt (0 oben, 1 unten).
 */
@Composable
fun ConfettiBurst(
    trigger: Any,
    modifier: Modifier = Modifier,
    count: Int = 150,
    durationMs: Int = 2800,
    originY: Float = 0.42f,
    spread: Float = 1f,
) {
    val enabled = LocalFeedbackPrefs.current.confetti && PyTheme.anim != AnimationLevel.OFF
    if (!enabled) return
    val particles = remember(trigger) {
        val rnd = Random(trigger.hashCode())
        List(count) {
            val angle = rnd.nextDouble(-Math.PI * 0.95, -Math.PI * 0.05).toFloat()
            val speed = rnd.nextDouble(0.55, 1.45).toFloat() * spread
            Particle(
                x0 = 0.5f + rnd.nextDouble(-0.08, 0.08).toFloat(),
                y0 = originY,
                vx = cos(angle) * speed * 0.62f,
                vy = sin(angle) * speed,
                rot = rnd.nextDouble(0.0, 360.0).toFloat(),
                vr = rnd.nextDouble(-540.0, 540.0).toFloat(),
                w = rnd.nextDouble(6.0, 10.0).toFloat(),
                h = rnd.nextDouble(6.0, 10.0).toFloat(),
                color = ConfettiColors[rnd.nextInt(ConfettiColors.size)],
                flip = rnd.nextDouble(4.0, 11.0).toFloat(),
                circle = rnd.nextInt(4) == 0,
            )
        }
    }
    val progress = remember(trigger) { Animatable(0f) }
    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(durationMs, easing = LinearEasing))
    }
    Canvas(modifier.fillMaxSize()) {
        val p = progress.value
        if (p >= 1f) return@Canvas
        val t = p * durationMs / 1000f
        val gravity = 1.25f
        val drag = 1f / (1f + t * 0.9f)
        val alpha = if (p > 0.7f) ((1f - p) / 0.3f).coerceIn(0f, 1f) else 1f
        particles.forEach { q ->
            val x = (q.x0 + q.vx * t * drag) * size.width
            val y = (q.y0 + q.vy * t * drag + 0.5f * gravity * t * t) * size.height
            if (y > size.height + 40f) return@forEach
            val squash = abs(cos(q.flip * t))
            val dpScale = density
            rotate(q.rot + q.vr * t, pivot = Offset(x, y)) {
                if (q.circle) {
                    drawCircle(q.color.copy(alpha = alpha), radius = q.h * 0.45f * dpScale, center = Offset(x, y))
                } else {
                    // kleiner abgerundeter Chip ("Bit"), der beim Drehen flach wird
                    val w = q.w * dpScale
                    val h = q.h * dpScale * (0.3f + 0.7f * squash)
                    drawRoundRect(
                        q.color.copy(alpha = alpha),
                        topLeft = Offset(x - w / 2, y - h / 2),
                        size = Size(w, h),
                        cornerRadius = CornerRadius(w * 0.28f),
                    )
                }
            }
        }
    }
}

/** Animierte Streak-Flamme. [active] = heute schon gelernt. */
@Composable
fun StreakFlame(modifier: Modifier = Modifier, size: Dp = 64.dp, active: Boolean = true, frozen: Boolean = false) {
    val c = PyTheme.colors
    val animate = PyTheme.anim != AnimationLevel.OFF && active
    val flicker: Float
    if (animate) {
        val t = rememberInfiniteTransition(label = "flame")
        flicker = t.animateFloat(0f, 1f, infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse), label = "flicker").value
    } else {
        flicker = 0.5f
    }
    val outerColor = when {
        frozen -> Color(0xFF4FB3E8)
        active -> Color(0xFFF2801F)
        else -> c.locked
    }
    val inner = when {
        frozen -> Color(0xFFDDF3FF)
        active -> Color(0xFFFFD25A)
        else -> c.lockedIcon.copy(alpha = 0.45f)
    }
    Canvas(modifier.size(width = size, height = size * 1.2f)) {
        val u = this.size.width / 100f
        scale(u, pivot = Offset.Zero) {
            val sy = 0.96f + flicker * 0.06f
            scale(1f + (0.5f - flicker) * 0.03f, sy, pivot = Offset(50f, 116f)) {
                val outer = Path().apply {
                    moveTo(50f, 4f)
                    cubicTo(60f, 24f, 88f, 40f, 88f, 74f)
                    cubicTo(88f, 99f, 70f, 117f, 50f, 117f)
                    cubicTo(30f, 117f, 12f, 99f, 12f, 74f)
                    cubicTo(12f, 54f, 24f, 42f, 30f, 30f)
                    cubicTo(33f, 42f, 37f, 49f, 43f, 52f)
                    cubicTo(40f, 34f, 43f, 18f, 50f, 4f)
                    close()
                }
                drawPath(outer, outerColor)
                val wob = (flicker - 0.5f) * 4f
                val core = Path().apply {
                    moveTo(50f + wob * 0.5f, 52f)
                    cubicTo(58f + wob, 66f, 70f, 76f, 70f, 92f)
                    cubicTo(70f, 105f, 61f, 113f, 50f, 113f)
                    cubicTo(39f, 113f, 30f, 105f, 30f, 92f)
                    cubicTo(30f, 78f, 42f, 68f, 50f + wob * 0.5f, 52f)
                    close()
                }
                drawPath(core, inner)
            }
        }
    }
}

/** Lässt ein Element kurz wackeln, sobald [trigger] hochzählt (z.B. bei falscher Antwort). */
fun Modifier.shake(trigger: Int): Modifier = composed {
    val offset = remember { Animatable(0f) }
    LaunchedEffect(trigger) {
        if (trigger > 0) {
            for (v in listOf(14f, -12f, 9f, -6f, 3f, 0f)) offset.animateTo(v, tween(45))
        }
    }
    this.graphicsLayer { translationX = offset.value * density }
}

/** Sanftes Auf und Ab, z.B. für die "Start"-Blase. */
@Composable
fun rememberBob(enabled: Boolean = true, amplitude: Float = 6f, periodMs: Int = 1100): Float {
    if (!enabled || PyTheme.anim != AnimationLevel.FULL) return 0f
    val t = rememberInfiniteTransition(label = "bob")
    val v by t.animateFloat(-1f, 1f, infiniteRepeatable(tween(periodMs / 2), RepeatMode.Reverse), label = "bobv")
    return v * amplitude
}

/** Pulsierender Wert zwischen 0 und 1. */
@Composable
fun rememberPulse(enabled: Boolean = true, periodMs: Int = 1400): Float {
    if (!enabled || PyTheme.anim != AnimationLevel.FULL) return 0f
    val t = rememberInfiniteTransition(label = "pulse")
    val v by t.animateFloat(0f, 1f, infiniteRepeatable(tween(periodMs, easing = LinearEasing)), label = "pulsev")
    return v
}

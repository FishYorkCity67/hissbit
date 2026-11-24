package app.pyquest.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.pyquest.data.AnimationLevel
import app.pyquest.ui.theme.LocalMascotPrefs
import app.pyquest.ui.theme.MascotPalette
import app.pyquest.ui.theme.PyTheme
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

enum class MontyMood { HAPPY, CELEBRATE, SAD, THINKING, SURPRISED, TALKING }

private val Ink = Color(0xFF1F2333)
private val MouthRed = Color(0xFF8C1F3D)
private val TonguePink = Color(0xFFFF6F8E)
private val CheekPink = Color(0xFFFF8FA3)
private val HatRed = Color(0xFFFF5C7A)
private val HatDark = Color(0xFF232A45)
private val Gold = Color(0xFFFFC23D)
private val GoldDark = Color(0xFFE09A1A)
private val GemMint = Color(0xFF2CE2AA)
private val GemViolet = Color(0xFF8E7CFF)

/** Ein Punkt auf dem Körper (vorberechnet). */
private class BodySample(val pos: Offset, val radius: Float, val spot: Boolean)

private fun buildBody(): List<BodySample> {
    val path = Path().apply {
        moveTo(90f, 76f)
        cubicTo(92f, 96f, 64f, 104f, 44f, 100f)
        cubicTo(24f, 96f, 18f, 80f, 34f, 74f)
        cubicTo(46f, 69f, 58f, 72f, 58f, 60f)
    }
    val measure = PathMeasure()
    measure.setPath(path, false)
    val length = measure.length
    val steps = 90
    var nextSpot = 9f
    return (0..steps).map { i ->
        val d = length * i / steps
        val f = d / length
        val r = 2.8f + (9.6f - 2.8f) * f.toDouble().pow(0.65).toFloat()
        val spot = d >= nextSpot && f in 0.12f..0.92f
        if (spot) nextSpot += 9.5f
        BodySample(measure.getPosition(d), r, spot)
    }
}

/**
 * Monty, das Maskottchen. Vollständig mit Canvas gezeichnet (Koordinaten 100 × 110),
 * damit es in jeder Größe gestochen scharf ist und sich frei animieren lässt.
 */
@Composable
fun Monty(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    mood: MontyMood = MontyMood.HAPPY,
    accessory: String? = LocalMascotPrefs.current.accessory,
    palette: MascotPalette = LocalMascotPrefs.current.palette,
    animated: Boolean = PyTheme.anim != AnimationLevel.OFF,
    lookX: Float = 0f,
    lookY: Float = 0f,
    /** Antippen lässt Monty zischen und wackeln. */
    interactive: Boolean = animated,
) {
    val body = remember { buildBody() }
    val feedback = rememberFeedback()
    val scope = rememberCoroutineScope()
    // 1 = Ruhe; beim Antippen läuft der Wert von 0 nach 1.
    val poke = remember { Animatable(1f) }
    val tapModifier = if (interactive) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
        ) {
            feedback.hiss()
            scope.launch {
                poke.snapTo(0f)
                poke.animateTo(1f, tween(950, easing = LinearEasing))
            }
        }
    } else Modifier
    val phase: Float
    val blink: Float
    val tongue: Float
    val talk: Float
    if (animated) {
        val t = rememberInfiniteTransition(label = "monty")
        phase = t.animateFloat(
            0f, 1f,
            infiniteRepeatable(tween(if (mood == MontyMood.CELEBRATE) 760 else 2400, easing = LinearEasing)),
            label = "phase",
        ).value
        blink = t.animateFloat(
            1f, 1f,
            infiniteRepeatable(keyframes {
                durationMillis = 4200
                1f at 0
                1f at 3700
                0.06f at 3800
                1f at 3930
            }),
            label = "blink",
        ).value
        tongue = t.animateFloat(
            0f, 0f,
            infiniteRepeatable(keyframes {
                durationMillis = 5600
                0f at 0
                0f at 4400
                1f at 4560
                0.7f at 4700
                1f at 4840
                0f at 5000
            }),
            label = "tongue",
        ).value
        talk = t.animateFloat(
            0f, 1f,
            infiniteRepeatable(tween(260, easing = LinearEasing), repeatMode = androidx.compose.animation.core.RepeatMode.Reverse),
            label = "talk",
        ).value
    } else {
        phase = 0f
        blink = 1f
        tongue = 0f
        talk = 0.5f
    }

    val p = poke.value
    val poking = p < 1f
    // gedämpftes Hin-und-Her-Wackeln
    val wiggle = if (poking) sin(p * 7 * PI).toFloat() * 10f * (1f - p).pow(1.4f) else 0f
    // die Zunge zuckt schnell vor und zurück
    val hissTongue = if (poking && p < 0.82f) 0.7f + 0.3f * sin(p * 46f) else 0f

    Canvas(modifier.size(width = size, height = size * 1.1f).then(tapModifier)) {
        val unit = this.size.width / 100f
        scale(unit, pivot = Offset.Zero) {
            val wave = sin(phase * 2 * PI).toFloat()
            val (dy, tilt) = when (mood) {
                MontyMood.CELEBRATE -> -abs(sin(phase * PI).toFloat()) * 9f to wave * 4f
                MontyMood.SAD -> 2f + wave * 0.4f to -3f
                else -> wave * 1.3f to wave * 1.2f
            }
            val hop = if (poking) -sin(min(1f, p * 2.2f) * PI).toFloat() * 4f else 0f
            translate(top = dy + hop) {
                rotate(tilt + wiggle, pivot = Offset(55f, 80f)) {
                    drawBody(body, palette)
                    drawHead(palette, mood, blink, max(tongue, hissTongue), talk, lookX, lookY, forceTongue = hissTongue > 0f)
                    drawAccessory(accessory)
                }
                if (poking) drawHissWaves(p, palette)
            }
        }
    }
}

/** Kleine Schallwellen neben dem Maul, solange Monty zischt. */
private fun DrawScope.drawHissWaves(p: Float, palette: MascotPalette) {
    val fade = (1f - p).coerceIn(0f, 1f)
    for (i in 0 until 3) {
        val local = (p * 1.6f - i * 0.18f).coerceIn(0f, 1f)
        if (local <= 0f || local >= 1f) continue
        val r = 5f + local * 9f
        val alpha = (1f - local) * fade
        drawArc(
            palette.body.copy(alpha = alpha),
            startAngle = -40f, sweepAngle = 80f, useCenter = false,
            topLeft = Offset(70f - r, 50f - r), size = Size(r * 2, r * 2),
            style = Stroke(width = 2.2f, cap = StrokeCap.Round),
        )
    }
}

private fun DrawScope.drawBody(body: List<BodySample>, p: MascotPalette) {
    body.forEach { drawCircle(p.dark, it.radius + 1.7f, it.pos) }
    body.forEach { drawCircle(p.body, it.radius, it.pos) }
    body.forEach { if (it.spot) drawCircle(p.belly, it.radius * 0.38f, it.pos) }
}

private fun DrawScope.drawHead(
    p: MascotPalette,
    mood: MontyMood,
    blink: Float,
    tongue: Float,
    talk: Float,
    lookX: Float,
    lookY: Float,
    forceTongue: Boolean = false,
) {
    val c = Offset(55f, 44f)
    drawCircle(p.dark, 21.7f, c)
    drawCircle(p.body, 20f, c)
    drawCircle(Color.White.copy(alpha = 0.16f), 7.5f, Offset(46f, 34f))
    // Schnauze
    drawOval(p.belly, topLeft = Offset(42f, 46.5f), size = Size(26f, 13.5f))
    // Bäckchen
    drawCircle(CheekPink.copy(alpha = 0.55f), 3.3f, Offset(40.5f, 50f))
    drawCircle(CheekPink.copy(alpha = 0.55f), 3.3f, Offset(69.5f, 50f))

    // Augen
    val eyes = listOf(Offset(47.5f, 40f), Offset(62.5f, 40f))
    if (mood == MontyMood.CELEBRATE) {
        eyes.forEach { e ->
            drawArc(
                Ink, startAngle = 200f, sweepAngle = 140f, useCenter = false,
                topLeft = Offset(e.x - 5.5f, e.y - 3f), size = Size(11f, 9f),
                style = Stroke(width = 2.6f, cap = StrokeCap.Round),
            )
        }
    } else {
        val (lx, ly) = when (mood) {
            MontyMood.SAD -> 0f to 1.6f
            MontyMood.THINKING -> 1.4f to -1.8f
            else -> lookX.coerceIn(-1f, 1f) * 1.6f to lookY.coerceIn(-1f, 1f) * 1.6f
        }
        val openness = if (mood == MontyMood.SAD) blink * 0.8f else blink
        val eyeR = if (mood == MontyMood.SURPRISED) 7.4f else 6.6f
        eyes.forEach { e ->
            scale(1f, openness.coerceIn(0.05f, 1f), pivot = e) {
                drawCircle(Ink, eyeR + 0.9f, e)
                drawCircle(Color.White, eyeR, e)
                val pupil = Offset(e.x + lx, e.y + ly + 0.5f)
                drawCircle(Ink, if (mood == MontyMood.SURPRISED) 3.2f else 3.7f, pupil)
                drawCircle(Color.White, 1.35f, Offset(pupil.x - 1.3f, pupil.y - 1.5f))
            }
        }
        if (mood == MontyMood.SAD) {
            drawLine(Ink, Offset(42.5f, 33.5f), Offset(50f, 31f), 1.9f, StrokeCap.Round)
            drawLine(Ink, Offset(67.5f, 33.5f), Offset(60f, 31f), 1.9f, StrokeCap.Round)
        }
    }

    // Mund
    when (mood) {
        MontyMood.HAPPY, MontyMood.THINKING -> drawArc(
            Ink, startAngle = 20f, sweepAngle = 140f, useCenter = false,
            topLeft = Offset(49f, 44.5f), size = Size(12f, 9f),
            style = Stroke(width = 2.4f, cap = StrokeCap.Round),
        )
        MontyMood.CELEBRATE -> {
            drawArc(MouthRed, 0f, 180f, useCenter = true, topLeft = Offset(47.5f, 45f), size = Size(15f, 13f))
            drawArc(TonguePink, 0f, 180f, useCenter = true, topLeft = Offset(51f, 51f), size = Size(8f, 6f))
        }
        MontyMood.SAD -> drawArc(
            Ink, startAngle = 200f, sweepAngle = 140f, useCenter = false,
            topLeft = Offset(50f, 51f), size = Size(10f, 7f),
            style = Stroke(width = 2.3f, cap = StrokeCap.Round),
        )
        MontyMood.SURPRISED -> {
            drawOval(MouthRed, topLeft = Offset(51.5f, 48f), size = Size(7f, 8f))
        }
        MontyMood.TALKING -> {
            val h = 3f + 6f * talk
            drawArc(MouthRed, 0f, 180f, useCenter = true, topLeft = Offset(49f, 49f - h / 2f), size = Size(12f, h + 2f))
        }
    }

    // Züngeln
    if (tongue > 0.01f && (forceTongue || mood == MontyMood.HAPPY || mood == MontyMood.THINKING)) {
        val base = Offset(55f, 52f)
        val tip = Offset(55f, 52f + 7.5f * tongue)
        val stroke = 1.7f
        drawLine(TonguePink, base, tip, stroke, StrokeCap.Round)
        drawLine(TonguePink, tip, Offset(tip.x - 2.3f * tongue, tip.y + 2.6f * tongue), stroke, StrokeCap.Round)
        drawLine(TonguePink, tip, Offset(tip.x + 2.3f * tongue, tip.y + 2.6f * tongue), stroke, StrokeCap.Round)
    }
}

private fun DrawScope.drawAccessory(id: String?) {
    when (id) {
        "hat_party" -> {
            val hat = Path().apply {
                moveTo(42f, 30f); lineTo(66f, 27f); lineTo(57f, 2.5f); close()
            }
            drawPath(hat, GemViolet)
            drawPath(hat, Color(0xFF6A58E0), style = Stroke(1.2f, join = StrokeJoin.Round))
            drawCircle(Gold, 1.9f, Offset(52f, 22f))
            drawCircle(GemMint, 1.6f, Offset(58.5f, 15f))
            drawCircle(Gold, 1.5f, Offset(60.5f, 24f))
            drawCircle(HatRed, 1.4f, Offset(55f, 9.5f))
            drawCircle(GemMint, 3.6f, Offset(57f, 2.8f))
        }
        "hat_top" -> {
            drawRoundRect(HatDark, topLeft = Offset(42f, 6f), size = Size(26f, 23f), cornerRadius = CornerRadius(2.5f))
            drawRect(HatRed, topLeft = Offset(42f, 20.5f), size = Size(26f, 5f))
            drawRoundRect(HatDark, topLeft = Offset(35f, 27.5f), size = Size(40f, 5f), cornerRadius = CornerRadius(2.5f))
            drawLine(Color.White.copy(alpha = 0.18f), Offset(45f, 9f), Offset(45f, 19f), 1.6f, StrokeCap.Round)
        }
        "hat_cap" -> {
            drawArc(HatRed, 180f, 180f, useCenter = true, topLeft = Offset(38.5f, 17f), size = Size(33f, 26f))
            drawOval(Color(0xFFC02E43), topLeft = Offset(55f, 26.5f), size = Size(26f, 7f))
            drawCircle(Color(0xFFC02E43), 2.1f, Offset(55f, 17.5f))
            drawLine(Color.White.copy(alpha = 0.25f), Offset(46f, 25f), Offset(52f, 19.5f), 1.4f, StrokeCap.Round)
        }
        "hat_grad" -> {
            drawRoundRect(HatDark, topLeft = Offset(44.5f, 22f), size = Size(21f, 10f), cornerRadius = CornerRadius(3f))
            val board = Path().apply { moveTo(32f, 21f); lineTo(55f, 13f); lineTo(78f, 21f); lineTo(55f, 29f); close() }
            drawPath(board, HatDark)
            drawPath(
                Path().apply { moveTo(55f, 21f); lineTo(71f, 23.5f); lineTo(71f, 35f) },
                Color(0xFFFFD43B), style = Stroke(1.5f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
            drawCircle(Color(0xFFFFD43B), 2f, Offset(71f, 35.5f))
            drawCircle(Color(0xFFFFD43B), 1.6f, Offset(55f, 21f))
        }
        "hat_wizard" -> {
            val cone = Path().apply { moveTo(38f, 30f); lineTo(72f, 30f); lineTo(60f, 0.5f); close() }
            drawPath(cone, Color(0xFF6A4BC4))
            drawOval(Color(0xFF5A3DB0), topLeft = Offset(32f, 25.5f), size = Size(46f, 9f))
            drawCircle(Color(0xFFFFD43B), 1.9f, Offset(54f, 19f))
            drawCircle(Color(0xFFFFD43B), 1.3f, Offset(60f, 10f))
            drawCircle(Color(0xFFFFD43B), 1.2f, Offset(49f, 25f))
            drawCircle(Color(0xFFFFFFFF), 0.9f, Offset(63f, 20f))
        }
        "hat_crown" -> {
            val crown = Path().apply {
                moveTo(41f, 31f); lineTo(41f, 15f); lineTo(48f, 22f); lineTo(55f, 10f)
                lineTo(62f, 22f); lineTo(69f, 15f); lineTo(69f, 31f); close()
            }
            drawPath(crown, Gold)
            drawPath(crown, GoldDark, style = Stroke(1.3f, join = StrokeJoin.Round))
            drawCircle(Gold, 1.9f, Offset(41f, 15f))
            drawCircle(Gold, 1.9f, Offset(55f, 10f))
            drawCircle(Gold, 1.9f, Offset(69f, 15f))
            drawCircle(HatRed, 2.3f, Offset(55f, 25f))
            drawCircle(GemViolet, 1.6f, Offset(46.5f, 27f))
            drawCircle(GemMint, 1.6f, Offset(63.5f, 27f))
        }
        "glasses" -> {
            listOf(Offset(47.5f, 40f), Offset(62.5f, 40f)).forEach {
                drawCircle(Ink, 7.7f, it, style = Stroke(1.9f))
            }
            drawLine(Ink, Offset(39.8f, 39f), Offset(35.5f, 37f), 1.7f, StrokeCap.Round)
            drawLine(Ink, Offset(70.2f, 39f), Offset(74.5f, 37f), 1.7f, StrokeCap.Round)
        }
        "shades" -> {
            drawRoundRect(Ink, topLeft = Offset(39f, 35f), size = Size(15.5f, 10f), cornerRadius = CornerRadius(4f))
            drawRoundRect(Ink, topLeft = Offset(55.5f, 35f), size = Size(15.5f, 10f), cornerRadius = CornerRadius(4f))
            drawLine(Ink, Offset(53.5f, 37.5f), Offset(56.5f, 37.5f), 1.8f)
            drawLine(Color.White.copy(alpha = 0.55f), Offset(42f, 37.5f), Offset(46f, 37.5f), 1.3f, StrokeCap.Round)
            drawLine(Color.White.copy(alpha = 0.55f), Offset(58.5f, 37.5f), Offset(62.5f, 37.5f), 1.3f, StrokeCap.Round)
            drawLine(Ink, Offset(39f, 38f), Offset(35f, 36.5f), 1.7f, StrokeCap.Round)
            drawLine(Ink, Offset(71f, 38f), Offset(75f, 36.5f), 1.7f, StrokeCap.Round)
        }
        "headphones" -> {
            drawArc(HatDark, 180f, 180f, useCenter = false, topLeft = Offset(32.5f, 21.5f), size = Size(45f, 45f), style = Stroke(3.4f, cap = StrokeCap.Round))
            drawRoundRect(HatRed, topLeft = Offset(28.5f, 38f), size = Size(8.5f, 15f), cornerRadius = CornerRadius(3.5f))
            drawRoundRect(HatRed, topLeft = Offset(73f, 38f), size = Size(8.5f, 15f), cornerRadius = CornerRadius(3.5f))
        }
        "bowtie" -> {
            val left = Path().apply { moveTo(57f, 67f); lineTo(48.5f, 61.5f); lineTo(48.5f, 72.5f); close() }
            val right = Path().apply { moveTo(57f, 67f); lineTo(65.5f, 61.5f); lineTo(65.5f, 72.5f); close() }
            drawPath(left, HatRed)
            drawPath(right, HatRed)
            drawCircle(Color(0xFFC02E43), 2.4f, Offset(57f, 67f))
        }
    }
}

package app.pyquest.ui.ambient

import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp as lerpColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.util.lerp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import app.pyquest.data.AmbientBackground
import app.pyquest.data.AnimationLevel
import app.pyquest.ui.theme.LocalAmbient
import app.pyquest.ui.theme.PyTheme
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

/*
 * Ruhige, animierte Hintergründe für die Hauptseiten: vier Jahreszeiten mit einer flachen Szenerie
 * (Fuji, Berge, Hügel, Bäume) und leise fallenden oder schwebenden Teilchen. Bewusst ohne Glühen,
 * ohne Neonfarben und mit niedrigem Kontrast, damit Text und Karten davor gut lesbar bleiben.
 *
 * Aufbau: Die Szenerie wird einmal in eine eigene Ebene gezeichnet (graphicsLayer) und danach nicht
 * mehr neu berechnet. Nur die Teilchen werden pro Bild neu gemalt – ihre Position ergibt sich
 * rein aus der Zeit, es gibt also keinen Zustand pro Teilchen und keine Speicherzuweisungen im Zeichnen.
 */

private const val TAU = 6.2831855f
private const val STILL_TIME = 14.0
/** Pause zwischen zwei Bildern der Teilchen: ergibt ca. 25–30 Bilder pro Sekunde. */
private const val FRAME_DELAY_MS = 28L

/** Auf Bildschirmen mit aktivem Hintergrund zeichnen die Seiten nichts Deckendes mehr, sonst sähe man ihn nicht. */
@Composable
fun Modifier.screenBackground(): Modifier =
    if (LocalAmbient.current == AmbientBackground.NONE) background(PyTheme.colors.background) else this

@Composable
private fun systemAnimationsOff(): Boolean {
    val resolver = LocalContext.current.contentResolver
    return remember(resolver) {
        runCatching { Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f }.getOrDefault(false)
    }
}

/**
 * Szenerie und Teilchen einer Jahreszeit. Füllt den ihm gegebenen Platz; dahinter liegt immer die
 * normale Hintergrundfarbe der App.
 *
 * @param animated false = stehendes Bild (Vorschau-Karten)
 * @param scale Größe der Teilchen relativ zum Bildschirm (Vorschau-Karten sind kleiner)
 * @param sceneHeight Anteil der Höhe, den die Szenerie unten einnimmt
 * @param lift Anteil der Szenenhöhe, um den die Szenerie nach oben rückt (Platz für die untere Leiste)
 */
@Composable
fun AmbientBackdrop(
    kind: AmbientBackground,
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    scale: Float = 1f,
    sceneHeight: Float = 0.46f,
    lift: Float = 0.16f,
) {
    if (kind == AmbientBackground.NONE) return
    val c = PyTheme.colors
    val dark = c.isDark
    val bg = c.background
    val level = PyTheme.anim
    val reduced = level == AnimationLevel.REDUCED
    val motion = animated && level != AnimationLevel.OFF && !systemAnimationsOff()

    val clock = remember { mutableDoubleStateOf(STILL_TIME) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(motion, lifecycle) {
        if (!motion) return@LaunchedEffect
        // Nur solange die App sichtbar ist – im Hintergrund läuft nichts.
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            var last = 0L
            while (true) {
                withFrameNanos { now ->
                    if (last != 0L) clock.doubleValue += min((now - last) / 1_000_000_000.0, 0.1)
                    last = now
                }
                // Zwischen den Bildern nicht auf jeden Takt warten: spart Akku, bei so langsamer Bewegung unsichtbar.
                delay(FRAME_DELAY_MS)
            }
        }
    }

    BoxWithConstraints(modifier.clipToBounds().clearAndSetSemantics { }) {
        val areaDp = maxWidth.value * maxHeight.value
        val wanted = (areaDp / (scale * scale) / kind.areaPerParticle).takeIf { it.isFinite() } ?: 14f
        val count = (if (reduced) wanted * 0.6f else wanted).roundToInt().coerceIn(6, 44)
        val particles = remember(kind, count) { makeParticles(kind, count) }
        val tones = remember(kind, dark) { toneList(kind, dark) }
        val speed = if (reduced) 0.55f else 1f

        // Szenerie: eigene Ebene, wird nur bei Größen- oder Designwechsel neu gezeichnet.
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                .drawBehind { drawScene(kind, dark, bg, sceneHeight, lift) }
        )
        // Teilchen: nur diese Ebene wird in jedem Bild neu gemalt.
        Box(Modifier.fillMaxSize().drawBehind { drawParticles(kind, clock.doubleValue, particles, tones, scale, speed) })
    }
}

private val AmbientBackground.areaPerParticle: Float
    get() = when (this) {
        AmbientBackground.BLOSSOM -> 14000f
        AmbientBackground.SNOW -> 9000f
        AmbientBackground.LEAVES -> 26000f
        AmbientBackground.FIREFLIES -> 21000f
        AmbientBackground.NONE -> 1e9f
    }

// ------------------------------------------------------------------------------------------ Farben

private class Crown(val main: Color, val light: Color, val shade: Color)

private class Pal(
    val disc: Color,
    val discAlpha: Float,
    val peak: Color,
    val snow: Color,
    val shade: Color,
    val hillFar: Color,
    val hillMid: Color,
    val hillNear: Color,
    val crowns: List<Crown>,
    val trunk: Color,
    val grass: Color,
)

private fun argb(value: Long) = Color(value)

private fun palette(kind: AmbientBackground, dark: Boolean): Pal = when (kind) {
    AmbientBackground.BLOSSOM -> if (!dark) Pal(
        disc = argb(0xFFF4C6D0), discAlpha = 0.65f,
        peak = argb(0xFFB9C8DC), snow = argb(0xFFEBF1F8), shade = argb(0xFF5B7599).copy(alpha = 0.16f),
        hillFar = argb(0xFFF5E7EB), hillMid = argb(0xFFEFD8E0), hillNear = argb(0xFFEBCAD5),
        crowns = listOf(Crown(argb(0xFFF7CFDB), argb(0xFFFCE6EC), argb(0xFFEFB6C7)), Crown(argb(0xFFF4BFD0), argb(0xFFFADCE5), argb(0xFFE8A5BA))),
        trunk = argb(0xFF9B7B78), grass = argb(0xFFEBCAD5),
    ) else Pal(
        disc = argb(0xFFF0E6D0), discAlpha = 0.22f,
        peak = argb(0xFF1F2938), snow = argb(0xFF566680), shade = Color.Black.copy(alpha = 0.25f),
        hillFar = argb(0xFF221D22), hillMid = argb(0xFF2A2227), hillNear = argb(0xFF33282E),
        crowns = listOf(Crown(argb(0xFF4A3140), argb(0xFF5B3C4E), argb(0xFF3B2630)), Crown(argb(0xFF533748), argb(0xFF66445A), argb(0xFF422B38))),
        trunk = argb(0xFF463B3D), grass = argb(0xFF33282E),
    )
    AmbientBackground.SNOW -> if (!dark) Pal(
        disc = argb(0xFFDCE8F4), discAlpha = 0.6f,
        peak = argb(0xFFC3D1E0), snow = argb(0xFFF1F5FA), shade = argb(0xFF5B7599).copy(alpha = 0.14f),
        hillFar = argb(0xFFEEF3F8), hillMid = argb(0xFFE3EBF3), hillNear = argb(0xFFD8E3EE),
        crowns = listOf(Crown(argb(0xFFA7BFB7), argb(0xFFA7BFB7), argb(0xFFA7BFB7)), Crown(argb(0xFF8FAAA1), argb(0xFF8FAAA1), argb(0xFF8FAAA1))),
        trunk = argb(0xFF9B8B80), grass = argb(0xFFD8E3EE),
    ) else Pal(
        disc = argb(0xFFC9D6E8), discAlpha = 0.22f,
        peak = argb(0xFF202A37), snow = argb(0xFF4D5C74), shade = Color.Black.copy(alpha = 0.25f),
        hillFar = argb(0xFF1A2028), hillMid = argb(0xFF202833), hillNear = argb(0xFF27313D),
        crowns = listOf(Crown(argb(0xFF14221F), argb(0xFF14221F), argb(0xFF14221F)), Crown(argb(0xFF1A2E29), argb(0xFF1A2E29), argb(0xFF1A2E29))),
        trunk = argb(0xFF3A332F), grass = argb(0xFF27313D),
    )
    AmbientBackground.LEAVES -> if (!dark) Pal(
        disc = argb(0xFFF3D9A8), discAlpha = 0.6f,
        peak = argb(0xFFECD0AE), snow = argb(0xFFECD0AE), shade = Color.Transparent,
        hillFar = argb(0xFFF7EEE1), hillMid = argb(0xFFF2E0C8), hillNear = argb(0xFFECD0AE),
        crowns = listOf(
            Crown(argb(0xFFE58B4F), argb(0xFFF2B27A), argb(0xFFC9602F)),
            Crown(argb(0xFFE8B04C), argb(0xFFF5D58A), argb(0xFFD18F2E)),
            Crown(argb(0xFFC9602F), argb(0xFFE58B4F), argb(0xFFA94822)),
        ),
        trunk = argb(0xFF8D6A52), grass = argb(0xFFECD0AE),
    ) else Pal(
        disc = argb(0xFFE8D9B8), discAlpha = 0.24f,
        peak = argb(0xFF372C23), snow = argb(0xFF372C23), shade = Color.Transparent,
        hillFar = argb(0xFF241F1A), hillMid = argb(0xFF2D251E), hillNear = argb(0xFF372C23),
        crowns = listOf(
            Crown(argb(0xFF8F5530), argb(0xFFA56A41), argb(0xFF74411F)),
            Crown(argb(0xFF8A6B2A), argb(0xFFA08340), argb(0xFF6F541F)),
            Crown(argb(0xFF7C3F26), argb(0xFF94533A), argb(0xFF62301C)),
        ),
        trunk = argb(0xFF4C3E34), grass = argb(0xFF372C23),
    )
    AmbientBackground.FIREFLIES -> if (!dark) Pal(
        disc = argb(0xFFFBE5A0), discAlpha = 0.6f,
        peak = argb(0xFFD0E4C7), snow = argb(0xFFD0E4C7), shade = Color.Transparent,
        hillFar = argb(0xFFEDF4E8), hillMid = argb(0xFFDFEDD8), hillNear = argb(0xFFD0E4C7),
        crowns = listOf(Crown(argb(0xFFB5D2AB), argb(0xFFB5D2AB), argb(0xFFB5D2AB))),
        trunk = argb(0xFF8DA486), grass = argb(0xFFB5D3AC),
    ) else Pal(
        disc = argb(0xFFE7E3CF), discAlpha = 0.65f,
        peak = argb(0xFF1C2E26), snow = argb(0xFF1C2E26), shade = Color.Transparent,
        hillFar = argb(0xFF15201D), hillMid = argb(0xFF17251F), hillNear = argb(0xFF1C2E26),
        crowns = listOf(Crown(argb(0xFF1C2E26), argb(0xFF1C2E26), argb(0xFF1C2E26))),
        trunk = argb(0xFF24402F), grass = argb(0xFF24402F),
    )
    AmbientBackground.NONE -> Pal(
        Color.Transparent, 0f, Color.Transparent, Color.Transparent, Color.Transparent,
        Color.Transparent, Color.Transparent, Color.Transparent, listOf(Crown(Color.Transparent, Color.Transparent, Color.Transparent)),
        Color.Transparent, Color.Transparent,
    )
}

private fun toneList(kind: AmbientBackground, dark: Boolean): List<Color> = when (kind) {
    AmbientBackground.BLOSSOM ->
        if (!dark) listOf(argb(0xFFF7B9CA), argb(0xFFF2A0B8), argb(0xFFFBD3DF), argb(0xFFEE93AE))
        else listOf(argb(0xFFF4B6C8), argb(0xFFE895AD), argb(0xFFF7CFDA), argb(0xFFDD7F9C))
    AmbientBackground.SNOW ->
        if (!dark) listOf(argb(0xFFB7C7DA), argb(0xFFC9D6E5), argb(0xFFA9BBD0))
        else listOf(argb(0xFFFFFFFF), argb(0xFFE6EEF8), argb(0xFFCFE0F2))
    AmbientBackground.LEAVES ->
        if (!dark) listOf(argb(0xFFD9763A), argb(0xFFC2552B), argb(0xFFE0A23A), argb(0xFFA9A03F), argb(0xFF9C4A2B))
        else listOf(argb(0xFFE08A4B), argb(0xFFCF6A3B), argb(0xFFE5AE4A), argb(0xFFB3A94B), argb(0xFFB05A38))
    AmbientBackground.FIREFLIES ->
        if (!dark) listOf(argb(0xFFE8A000), argb(0xFFF0B429))
        else listOf(argb(0xFFE2F27A), argb(0xFFF4E58A))
    AmbientBackground.NONE -> listOf(Color.Transparent)
}

// ------------------------------------------------------------------------------------------ Szenerie

private class SceneCtx(val p: Pal, val bg: Color, val dark: Boolean, val w: Float, val h: Float, val u: Float, val lift: Float)

private fun DrawScope.drawScene(kind: AmbientBackground, dark: Boolean, bg: Color, sceneHeight: Float, lift: Float) {
    val w = size.width
    val h = size.height
    if (w <= 0f || h <= 0f) return
    // u = Höhe des Szenenkastens; auf breiten Bildschirmen wächst die Szenerie nicht mit der Breite mit.
    val u = min(h * sceneHeight, w * 1.08f)
    val s = SceneCtx(palette(kind, dark), bg, dark, w, h, u, lift * u)
    when (kind) {
        AmbientBackground.BLOSSOM -> blossomScene(s)
        AmbientBackground.SNOW -> winterScene(s)
        AmbientBackground.LEAVES -> autumnScene(s)
        AmbientBackground.FIREFLIES -> summerScene(s)
        AmbientBackground.NONE -> Unit
    }
}

private fun frac(v: Float) = v - floor(v)
private fun hash(i: Int, salt: Int = 0): Float = frac(sin(i * 12.9898f + salt * 78.233f) * 43758.547f)

private fun ridgeY(x: Float, baseY: Float, amp: Float, wavelength: Float, phase: Float): Float {
    val a = x / wavelength * TAU + phase
    return baseY - amp * (0.62f * sin(a) + 0.38f * sin(a * 2.3f + phase * 1.7f + 1.2f))
}

private fun DrawScope.ridgePath(baseY: Float, amp: Float, wavelength: Float, phase: Float): Path {
    val step = max(4f, size.width / 140f)
    val path = Path()
    path.moveTo(-step, size.height + 4f)
    var x = -step
    while (x < size.width + step) {
        path.lineTo(x, ridgeY(x, baseY, amp, wavelength, phase))
        x += step
    }
    val endX = size.width + step
    path.lineTo(endX, ridgeY(endX, baseY, amp, wavelength, phase))
    path.lineTo(endX, size.height + 4f)
    path.close()
    return path
}

/** Fuji: flacher Gipfel, nach unten auslaufende (konkave) Flanken. */
private fun fujiPath(cx: Float, peakY: Float, baseY: Float, halfW: Float): Path {
    val hF = baseY - peakY
    val rim = halfW * 0.045f
    return Path().apply {
        moveTo(cx - rim, peakY)
        lineTo(cx + rim, peakY)
        cubicTo(cx + halfW * 0.20f, peakY + hF * 0.30f, cx + halfW * 0.52f, peakY + hF * 0.80f, cx + halfW, baseY)
        lineTo(cx + halfW, baseY + 0.02f * hF)
        lineTo(cx - halfW, baseY + 0.02f * hF)
        lineTo(cx - halfW, baseY)
        cubicTo(cx - halfW * 0.52f, peakY + hF * 0.80f, cx - halfW * 0.20f, peakY + hF * 0.30f, cx - rim, peakY)
        close()
    }
}

private fun fujiSnow(cx: Float, peakY: Float, halfW: Float, hF: Float): Path = Path().apply {
    moveTo(cx - halfW, peakY - 4f)
    lineTo(cx + halfW, peakY - 4f)
    val teeth = listOf(
        0.50f to 0.30f, 0.41f to 0.20f, 0.31f to 0.31f, 0.22f to 0.19f, 0.12f to 0.30f, 0.03f to 0.18f,
        -0.06f to 0.30f, -0.16f to 0.17f, -0.25f to 0.30f, -0.34f to 0.20f, -0.46f to 0.32f,
    )
    teeth.forEach { (dx, depth) -> lineTo(cx + dx * halfW, peakY + depth * hF) }
    close()
}

/** Schattenseite rechts: leichter Schleier von der Spitze nach unten – gibt dem Berg Volumen. */
private fun fujiShade(cx: Float, peakY: Float, halfW: Float, hF: Float): Path = Path().apply {
    moveTo(cx, peakY - 4f)
    lineTo(cx + halfW * 1.1f, peakY - 4f)
    lineTo(cx + halfW * 1.1f, peakY + hF * 1.1f)
    lineTo(cx + halfW * 0.24f, peakY + hF * 1.1f)
    close()
}

private fun DrawScope.drawTree(cx: Float, baseY: Float, r: Float, trunk: Color, crown: Crown) {
    drawLine(trunk, Offset(cx, baseY + r * 0.12f), Offset(cx + r * 0.05f, baseY - r * 0.8f), strokeWidth = max(2f, r * 0.15f), cap = StrokeCap.Round)
    drawCircle(crown.shade, r * 0.80f, Offset(cx + r * 0.42f, baseY - r * 1.05f))
    drawCircle(crown.main, r * 0.95f, Offset(cx, baseY - r * 1.35f))
    drawCircle(crown.main, r * 0.72f, Offset(cx - r * 0.80f, baseY - r * 1.05f))
    drawCircle(crown.main, r * 0.70f, Offset(cx + r * 0.80f, baseY - r * 1.10f))
    drawCircle(crown.light, r * 0.52f, Offset(cx - r * 0.28f, baseY - r * 1.62f))
    drawCircle(crown.light, r * 0.34f, Offset(cx - r * 0.98f, baseY - r * 1.24f))
}

private fun DrawScope.drawPine(cx: Float, baseY: Float, height: Float, color: Color) {
    val w = height * 0.40f
    val path = Path().apply {
        moveTo(cx, baseY - height)
        lineTo(cx + w * 0.55f, baseY - height * 0.62f)
        lineTo(cx + w * 0.30f, baseY - height * 0.62f)
        lineTo(cx + w * 0.78f, baseY - height * 0.30f)
        lineTo(cx + w * 0.40f, baseY - height * 0.30f)
        lineTo(cx + w, baseY)
        lineTo(cx - w, baseY)
        lineTo(cx - w * 0.40f, baseY - height * 0.30f)
        lineTo(cx - w * 0.78f, baseY - height * 0.30f)
        lineTo(cx - w * 0.30f, baseY - height * 0.62f)
        lineTo(cx - w * 0.55f, baseY - height * 0.62f)
        close()
    }
    drawPath(path, color)
}

private fun DrawScope.mist(s: SceneCtx, fromY: Float, toY: Float) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color.Transparent, s.bg.copy(alpha = 0.94f)),
            startY = fromY,
            endY = toY,
        ),
        topLeft = Offset(0f, fromY),
        size = Size(s.w, toY - fromY),
    )
}

private fun DrawScope.blossomScene(s: SceneCtx) {
    val p = s.p
    val w = s.w
    val h = s.h
    val u = s.u
    val baseY = h - s.lift - 0.22f * u
    val halfW = min(0.95f * w, 1.15f * u)
    val hF = min(0.52f * halfW, 0.62f * u)
    val cx = w * 0.60f
    val peakY = baseY - hF

    // Sonne bzw. Mond rechts über dem Berg (links liegen meist Monty und der Lernpfad)
    val discX = (cx + halfW * 0.44f).coerceAtMost(0.86f * w)
    drawCircle(p.disc, radius = hF * 0.10f, center = Offset(discX, peakY - hF * 0.34f), alpha = p.discAlpha)

    val fuji = fujiPath(cx, peakY, baseY, halfW)
    drawPath(fuji, p.peak)
    clipPath(fuji) {
        drawPath(fujiSnow(cx, peakY, halfW, hF), p.snow)
        drawPath(fujiShade(cx, peakY, halfW, hF), p.shade)
    }
    mist(s, baseY - 0.20f * u, baseY + 0.025f * u)

    drawPath(ridgePath(baseY + 0.030f * u, 0.045f * u, 0.90f * u, 0.8f), p.hillFar)
    val midBase = baseY + 0.090f * u
    drawPath(ridgePath(midBase, 0.040f * u, 0.70f * u, 2.1f), p.hillMid)
    var x = 0.10f * u
    var i = 0
    while (x < w + 0.2f * u) {
        val r = u * (0.027f + 0.014f * hash(i, 3))
        drawTree(x, ridgeY(x, midBase, 0.040f * u, 0.70f * u, 2.1f) + r * 0.15f, r, p.trunk, p.crowns[i % p.crowns.size])
        x += u * (0.26f + 0.18f * hash(i, 5))
        i++
    }
    val nearBase = baseY + 0.165f * u
    drawPath(ridgePath(nearBase, 0.030f * u, 0.55f * u, 4.0f), p.hillNear)
    // ein größerer Baum rahmt die Szene am linken Rand
    drawTree(w * 0.05f, ridgeY(w * 0.05f, nearBase, 0.030f * u, 0.55f * u, 4.0f) + 0.01f * u, u * 0.062f, p.trunk, p.crowns[0])
    drawTree(w * 0.96f, ridgeY(w * 0.96f, nearBase, 0.030f * u, 0.55f * u, 4.0f) + 0.01f * u, u * 0.048f, p.trunk, p.crowns[1])
}

private fun mountainPath(cx: Float, peakY: Float, halfW: Float, baseY: Float, bottom: Float): Path {
    val hgt = baseY - peakY
    return Path().apply {
        moveTo(cx - halfW, bottom)
        lineTo(cx - halfW, baseY)
        lineTo(cx - halfW * 0.50f, peakY + hgt * 0.46f)
        lineTo(cx - halfW * 0.30f, peakY + hgt * 0.38f)
        lineTo(cx - halfW * 0.06f, peakY + hgt * 0.10f)
        lineTo(cx, peakY)
        lineTo(cx + halfW * 0.16f, peakY + hgt * 0.24f)
        lineTo(cx + halfW * 0.34f, peakY + hgt * 0.34f)
        lineTo(cx + halfW * 0.52f, peakY + hgt * 0.52f)
        lineTo(cx + halfW, baseY)
        lineTo(cx + halfW, bottom)
        close()
    }
}

private fun mountainCap(cx: Float, peakY: Float, halfW: Float, hgt: Float): Path = Path().apply {
    moveTo(cx - halfW, peakY - 4f)
    lineTo(cx + halfW, peakY - 4f)
    val teeth = listOf(
        0.52f to 0.50f, 0.40f to 0.36f, 0.30f to 0.46f, 0.18f to 0.30f, 0.08f to 0.40f,
        -0.02f to 0.28f, -0.12f to 0.42f, -0.24f to 0.34f, -0.34f to 0.50f, -0.46f to 0.46f,
    )
    teeth.forEach { (dx, depth) -> lineTo(cx + dx * halfW, peakY + depth * hgt) }
    close()
}

private fun DrawScope.drawMountain(s: SceneCtx, cx: Float, peakY: Float, halfW: Float, baseY: Float, body: Color, snow: Color) {
    val shape = mountainPath(cx, peakY, halfW, baseY, baseY + 0.05f * s.u)
    drawPath(shape, body)
    clipPath(shape) {
        drawPath(mountainCap(cx, peakY, halfW, baseY - peakY), snow)
        drawPath(fujiShade(cx, peakY, halfW, baseY - peakY), s.p.shade)
    }
}

private fun DrawScope.winterScene(s: SceneCtx) {
    val p = s.p
    val w = s.w
    val h = s.h
    val u = s.u
    val baseY = h - s.lift - 0.20f * u
    drawCircle(p.disc, 0.06f * u, Offset(w * 0.50f, h - s.lift - 0.84f * u), alpha = p.discAlpha)
    // hintere Berge heller (Dunst), vordere kräftiger
    drawMountain(s, w * 0.76f, baseY - 0.62f * u, 0.82f * u, baseY, lerpColor(p.peak, s.bg, 0.40f), lerpColor(p.snow, s.bg, 0.25f))
    drawMountain(s, w * 0.24f, baseY - 0.48f * u, 0.68f * u, baseY, p.peak, p.snow)
    mist(s, baseY - 0.16f * u, baseY + 0.02f * u)

    val farBase = baseY + 0.03f * u
    drawPath(ridgePath(farBase, 0.035f * u, 0.85f * u, 1.1f), p.hillFar)
    var x = 0.05f * u
    var i = 0
    while (x < w + 0.1f * u) {
        val ph = 0.05f + 0.045f * hash(i, 7)
        drawPine(x, ridgeY(x, farBase, 0.035f * u, 0.85f * u, 1.1f) + 0.01f * u, u * ph, p.crowns[0].main)
        x += u * (0.05f + 0.07f * hash(i, 9))
        i++
    }
    val midBase = baseY + 0.10f * u
    drawPath(ridgePath(midBase, 0.032f * u, 0.65f * u, 3.0f), p.hillMid)
    x = 0.02f * u
    i = 20
    while (x < w + 0.1f * u) {
        if (hash(i, 11) > 0.35f) {
            val ph = 0.075f + 0.06f * hash(i, 13)
            drawPine(x, ridgeY(x, midBase, 0.032f * u, 0.65f * u, 3.0f) + 0.012f * u, u * ph, p.crowns[1].main)
        }
        x += u * (0.10f + 0.10f * hash(i, 15))
        i++
    }
    drawPath(ridgePath(baseY + 0.165f * u, 0.026f * u, 0.5f * u, 5.0f), p.hillNear)
}

private fun DrawScope.autumnScene(s: SceneCtx) {
    val p = s.p
    val w = s.w
    val h = s.h
    val u = s.u
    val baseY = h - s.lift - 0.20f * u
    drawCircle(p.disc, 0.065f * u, Offset(w * 0.70f, baseY - 0.30f * u), alpha = p.discAlpha)
    drawPath(ridgePath(baseY - 0.02f * u, 0.050f * u, 1.10f * u, 0.4f), p.hillFar)
    mist(s, baseY - 0.14f * u, baseY + 0.04f * u)
    val midBase = baseY + 0.07f * u
    drawPath(ridgePath(midBase, 0.045f * u, 0.80f * u, 2.3f), p.hillMid)
    var x = 0.08f * u
    var i = 0
    while (x < w + 0.2f * u) {
        val r = u * (0.028f + 0.016f * hash(i, 3))
        drawTree(x, ridgeY(x, midBase, 0.045f * u, 0.80f * u, 2.3f) + r * 0.15f, r, p.trunk, p.crowns[i % p.crowns.size])
        x += u * (0.24f + 0.20f * hash(i, 5))
        i++
    }
    val nearBase = baseY + 0.15f * u
    drawPath(ridgePath(nearBase, 0.032f * u, 0.60f * u, 4.4f), p.hillNear)
    drawTree(w * 0.08f, ridgeY(w * 0.08f, nearBase, 0.032f * u, 0.60f * u, 4.4f) + 0.01f * u, u * 0.062f, p.trunk, p.crowns[0])
    drawTree(w * 0.94f, ridgeY(w * 0.94f, nearBase, 0.032f * u, 0.60f * u, 4.4f) + 0.01f * u, u * 0.052f, p.trunk, p.crowns[2])
}

private fun DrawScope.summerScene(s: SceneCtx) {
    val p = s.p
    val w = s.w
    val h = s.h
    val u = s.u
    val baseY = h - s.lift - 0.20f * u
    if (s.dark) {
        val moon = Offset(w * 0.78f, h - s.lift - 0.82f * u)
        val r = 0.05f * u
        drawCircle(p.disc, r, moon, alpha = p.discAlpha)
        drawCircle(s.bg, r * 0.86f, Offset(moon.x + r * 0.45f, moon.y - r * 0.20f)) // Sichel
        for (i in 0 until 28) {
            val sx = hash(i, 1) * w
            val sy = 0.05f * h + hash(i, 2) * (h - u * 1.0f - 0.05f * h)
            if (sy > 0f) drawCircle(Color.White, 0.9f + 1.2f * hash(i, 3), Offset(sx, sy), alpha = 0.18f + 0.32f * hash(i, 4))
        }
    } else {
        drawCircle(p.disc, 0.055f * u, Offset(w * 0.78f, h - s.lift - 0.80f * u), alpha = p.discAlpha)
    }
    drawPath(ridgePath(baseY, 0.045f * u, 1.0f * u, 0.9f), p.hillFar)
    mist(s, baseY - 0.14f * u, baseY + 0.04f * u)
    drawPath(ridgePath(baseY + 0.08f * u, 0.040f * u, 0.75f * u, 2.6f), p.hillMid)
    val nearBase = baseY + 0.15f * u
    drawPath(ridgePath(nearBase, 0.030f * u, 0.58f * u, 4.7f), p.hillNear)
    // Gräser am unteren Rand
    var x = 0f
    var i = 0
    while (x < w) {
        val len = u * (0.035f + 0.075f * hash(i, 21))
        val lean = u * 0.03f * (hash(i, 23) - 0.5f)
        val y0 = ridgeY(x, nearBase, 0.030f * u, 0.58f * u, 4.7f) + 0.02f * u
        val blade = Path().apply {
            moveTo(x, y0)
            quadraticTo(x + lean * 0.3f, y0 - len * 0.55f, x + lean, y0 - len)
        }
        drawPath(blade, p.grass, style = Stroke(width = max(2f, u * 0.006f), cap = StrokeCap.Round))
        x += u * (0.022f + 0.020f * hash(i, 25))
        i++
    }
}

// ------------------------------------------------------------------------------------------ Teilchen

private class Particle(
    val x: Float,
    val y: Float,
    val speed: Float,
    val size: Float,
    val sway: Float,
    val swayF: Float,
    val phase: Float,
    val rot: Float,
    val spin: Float,
    val flutter: Float,
    val alpha: Float,
    val tone: Int,
    val depth: Float,
    val drift: Float,
)

private fun makeParticles(kind: AmbientBackground, n: Int): List<Particle> {
    val rnd = java.util.Random(1000L + kind.ordinal * 31L)
    fun r(lo: Float, hi: Float) = lo + (hi - lo) * rnd.nextFloat()
    val list = List(n) {
        val depth = rnd.nextFloat()
        when (kind) {
            AmbientBackground.SNOW -> Particle(
                x = rnd.nextFloat(), y = rnd.nextFloat(),
                speed = lerp(14f, 34f, depth) * r(0.85f, 1.15f), size = lerp(2.6f, 6.0f, depth) * r(0.85f, 1.15f),
                sway = lerp(5f, 20f, depth), swayF = r(0.4f, 1.0f), phase = r(0f, TAU),
                rot = 0f, spin = 0f, flutter = 0f, alpha = lerp(0.45f, 0.85f, depth), tone = rnd.nextInt(3), depth = depth,
                drift = r(-3f, 5f),
            )
            AmbientBackground.LEAVES -> Particle(
                x = rnd.nextFloat(), y = rnd.nextFloat(),
                speed = lerp(20f, 44f, depth) * r(0.85f, 1.15f), size = lerp(10f, 19f, depth) * r(0.85f, 1.15f),
                sway = lerp(16f, 42f, depth), swayF = r(0.5f, 1.1f), phase = r(0f, TAU),
                rot = r(0f, TAU), spin = r(-1.6f, 1.6f), flutter = r(1.0f, 2.4f),
                alpha = lerp(0.55f, 0.92f, depth), tone = rnd.nextInt(5), depth = depth, drift = r(-4f, 8f),
            )
            AmbientBackground.FIREFLIES -> Particle(
                x = rnd.nextFloat(), y = rnd.nextFloat(),
                speed = 0f, size = lerp(2.2f, 3.8f, depth) * r(0.9f, 1.15f),
                sway = r(26f, 80f), swayF = r(0.12f, 0.32f), phase = r(0f, TAU),
                rot = 0f, spin = 0f, flutter = r(0.5f, 1.3f), alpha = lerp(0.6f, 1f, depth), tone = rnd.nextInt(2), depth = depth,
                drift = 0f,
            )
            else -> Particle( // Kirschblüten
                x = rnd.nextFloat(), y = rnd.nextFloat(),
                speed = lerp(16f, 44f, depth) * r(0.85f, 1.15f), size = lerp(6f, 15f, depth) * r(0.85f, 1.15f),
                sway = lerp(10f, 30f, depth), swayF = r(0.5f, 1.2f), phase = r(0f, TAU),
                rot = r(0f, TAU), spin = r(-1.2f, 1.2f), flutter = r(1.4f, 3.0f),
                alpha = lerp(0.50f, 0.92f, depth), tone = rnd.nextInt(4), depth = depth, drift = r(-2f, 9f),
            )
        }
    }
    return list.sortedBy { it.depth } // ferne zuerst, nahe zuletzt
}

/** Blütenblatt mit der typischen Einkerbung an der Spitze (Einheitsform um den Nullpunkt). */
private val PetalPath: Path by lazy {
    Path().apply {
        moveTo(0f, 0.60f)
        cubicTo(-0.55f, 0.35f, -0.62f, -0.35f, -0.28f, -0.60f)
        quadraticTo(-0.12f, -0.72f, 0f, -0.44f)
        quadraticTo(0.12f, -0.72f, 0.28f, -0.60f)
        cubicTo(0.62f, -0.35f, 0.55f, 0.35f, 0f, 0.60f)
        close()
    }
}

/** Spitz zulaufendes Blatt. */
private val LeafPath: Path by lazy {
    Path().apply {
        moveTo(0f, -0.72f)
        cubicTo(0.58f, -0.36f, 0.52f, 0.38f, 0f, 0.72f)
        cubicTo(-0.52f, 0.38f, -0.58f, -0.36f, 0f, -0.72f)
        close()
    }
}

private fun DrawScope.drawParticles(kind: AmbientBackground, t: Double, ps: List<Particle>, tones: List<Color>, scale: Float, speedMul: Float) {
    val w = size.width
    val h = size.height
    if (w <= 0f || h <= 0f) return
    val d = density * scale
    // Gemeinsamer, langsamer „Wind“ – bewegt alle Teilchen sanft in dieselbe Richtung.
    val wind = (30.0 * sin(0.17 * t) + 14.0 * sin(0.41 * t + 1.3)).toFloat()
    for (index in ps.indices) {
        val p = ps[index]
        val sizePx = p.size * d
        if (kind == AmbientBackground.FIREFLIES) {
            drawFirefly(p, t, tones, w, h, d, sizePx)
            continue
        }
        val margin = sizePx * 2.2f
        val spanY = h + 2f * margin
        val travel = (p.y * spanY + p.speed * speedMul * d * t) % spanY
        val y = (travel - margin).toFloat()
        val spanX = w + 2f * margin
        val sway = p.sway * d * sin(p.swayF * t + p.phase).toFloat()
        val xRaw = (p.x * spanX + p.drift * d * speedMul * t + sway + wind * d * (0.35f + 0.65f * p.depth)) % spanX
        val x = (if (xRaw < 0) xRaw + spanX else xRaw).toFloat() - margin
        val color = tones[p.tone % tones.size]
        when (kind) {
            AmbientBackground.SNOW -> drawCircle(color, sizePx * 0.5f, Offset(x, y), alpha = p.alpha)
            AmbientBackground.LEAVES -> {
                val tumble = 0.30f + 0.70f * abs(cos(p.flutter * t + p.phase)).toFloat()
                val deg = ((p.rot + p.spin * speedMul * t) * 180.0 / PI).toFloat()
                withTransform({
                    translate(x, y)
                    rotate(deg, Offset.Zero)
                    scale(sizePx, sizePx * tumble, Offset.Zero)
                }) {
                    drawPath(LeafPath, color, alpha = p.alpha)
                    drawLine(Color.Black, Offset(0f, -0.60f), Offset(0f, 0.78f), strokeWidth = 0.05f, alpha = 0.16f)
                }
            }
            else -> {
                val flutter = 0.28f + 0.72f * (0.5f + 0.5f * cos(p.flutter * t + p.phase).toFloat())
                val deg = ((p.rot + p.spin * speedMul * t) * 180.0 / PI).toFloat()
                withTransform({
                    translate(x, y)
                    rotate(deg, Offset.Zero)
                    scale(sizePx * flutter, sizePx, Offset.Zero)
                }) {
                    drawPath(PetalPath, color, alpha = p.alpha * (0.78f + 0.22f * flutter))
                }
            }
        }
    }
}

private fun DrawScope.drawFirefly(p: Particle, t: Double, tones: List<Color>, w: Float, h: Float, d: Float, sizePx: Float) {
    // Langsame, ineinander verschlungene Bahnen im unteren Bildschirmbereich
    val px = p.x * w + (p.sway * d * sin(p.swayF * t + p.phase) + 0.5 * p.sway * d * sin(p.swayF * 0.63 * t + p.phase * 2.0)).toFloat()
    val py = h * (0.30f + 0.62f * p.y) + (0.7 * p.sway * d * cos(p.swayF * 0.81 * t + p.phase * 1.3)).toFloat()
    val pulse = (0.5 + 0.5 * sin(p.flutter * t * 2.0 + p.phase * 3.0)).coerceAtLeast(0.0).pow(2.2).toFloat()
    val color = tones[p.tone % tones.size]
    val a = p.alpha * (0.12f + 0.88f * pulse)
    val center = Offset(px, py)
    drawCircle(color, sizePx * 3.4f, center, alpha = a * 0.20f)
    drawCircle(color, sizePx, center, alpha = a)
}

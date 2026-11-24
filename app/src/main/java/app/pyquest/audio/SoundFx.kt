package app.pyquest.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tanh
import kotlin.random.Random

enum class Sfx { CORRECT, WRONG, COMPLETE, TAP, COMBO, UNLOCK, STREAK, COIN, HISS, RUN }

/**
 * Hissbits Klänge – alle beim ersten Gebrauch selbst synthetisiert (keine Audiodateien):
 * FM-Glocken für Erfolge, Zupfklänge (Karplus-Strong) für Arpeggien, ein 8-Bit-"Bit"
 * für die Währung, gefiltertes Rauschen für Montys Zischen und ein kleiner Hall.
 */
object SoundFx {
    private const val RATE = 44100
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val tracks = mutableMapOf<Sfx, AudioTrack>()

    @Volatile
    var enabled = true

    fun play(sfx: Sfx) {
        if (!enabled) return
        scope.launch {
            runCatching {
                val track = synchronized(tracks) { tracks.getOrPut(sfx) { buildTrack(render(sfx)) } }
                synchronized(track) {
                    if (track.playState == AudioTrack.PLAYSTATE_PLAYING) track.stop()
                    track.reloadStaticData()
                    track.play()
                }
            }
        }
    }

    /** Erzeugt die Sounds vorab, damit der erste Klick nicht verzögert. */
    fun preload() {
        scope.launch {
            Sfx.entries.forEach { sfx ->
                runCatching { synchronized(tracks) { tracks.getOrPut(sfx) { buildTrack(render(sfx)) } } }
            }
        }
    }

    private fun buildTrack(pcm: ShortArray): AudioTrack {
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(pcm.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        track.write(pcm, 0, pcm.size)
        return track
    }

    // ------------------------------------------------------------------ Bausteine

    private fun note(name: Int): Double = 440.0 * 2.0.pow((name - 69) / 12.0)

    private class Buf(seconds: Double) {
        val data = DoubleArray((seconds * RATE).toInt())
        fun add(i: Int, v: Double) {
            if (i in data.indices) data[i] += v
        }
    }

    /** Hüllkurve: kurzer Anstieg, exponentielles Ausklingen. */
    private fun env(t: Double, attack: Double, decay: Double): Double =
        if (t < attack) t / attack else exp(-(t - attack) / decay)

    /** FM-Glocke: Träger mit abklingender Modulation – klar und freundlich. */
    private fun bell(b: Buf, start: Double, freq: Double, dur: Double, vol: Double, ratio: Double = 3.5, index: Double = 2.2) {
        val s0 = (start * RATE).toInt()
        val n = (dur * RATE).toInt()
        for (k in 0 until n) {
            val t = k.toDouble() / RATE
            val i = index * exp(-t * 7.0)
            val mod = sin(2 * PI * freq * ratio * t) * i
            val v = sin(2 * PI * freq * t + mod) * env(t, 0.003, dur / 4.2)
            b.add(s0 + k, v * vol)
        }
    }

    /** Weicher Sinus mit Oberton und Tonhöhen-Gleiten von [f0] nach [f1]. */
    private fun tone(b: Buf, start: Double, f0: Double, f1: Double, dur: Double, vol: Double, attack: Double = 0.004, bright: Double = 0.2) {
        val s0 = (start * RATE).toInt()
        val n = (dur * RATE).toInt()
        var phase = 0.0
        for (k in 0 until n) {
            val t = k.toDouble() / RATE
            val f = f0 + (f1 - f0) * min(1.0, t / dur)
            phase += 2 * PI * f / RATE
            val v = (sin(phase) + bright * sin(2 * phase)) * env(t, attack, dur / 3.2)
            b.add(s0 + k, v * vol)
        }
    }

    /** Zupfklang (Karplus-Strong) – wie eine kleine Harfe oder Kalimba. */
    private fun pluck(b: Buf, start: Double, freq: Double, dur: Double, vol: Double, rnd: Random) {
        val period = max(2, (RATE / freq).toInt())
        val line = DoubleArray(period) { rnd.nextDouble(-1.0, 1.0) }
        // Anschlag weicher machen
        for (i in 1 until period) line[i] = 0.5 * (line[i] + line[i - 1])
        val s0 = (start * RATE).toInt()
        val n = (dur * RATE).toInt()
        var idx = 0
        val damp = 0.996
        for (k in 0 until n) {
            val next = (idx + 1) % period
            val v = line[idx]
            line[idx] = damp * 0.5 * (line[idx] + line[next])
            idx = next
            val t = k.toDouble() / RATE
            val fade = if (t > dur - 0.05) max(0.0, (dur - t) / 0.05) else 1.0
            b.add(s0 + k, v * vol * fade)
        }
    }

    /** 8-Bit-Ton: bandbegrenztes Rechteck (ungerade Obertöne). */
    private fun chip(b: Buf, start: Double, freq: Double, dur: Double, vol: Double) {
        val s0 = (start * RATE).toInt()
        val n = (dur * RATE).toInt()
        for (k in 0 until n) {
            val t = k.toDouble() / RATE
            var v = 0.0
            var h = 1
            while (h <= 9 && freq * h < 12000) {
                v += sin(2 * PI * freq * h * t) / h
                h += 2
            }
            b.add(s0 + k, v * 0.8 * env(t, 0.002, dur / 3.0) * vol)
        }
    }

    /** Gefiltertes Rauschen (einfacher Bandpass) – für Zischen und Rauschen. */
    private fun noise(
        b: Buf, start: Double, dur: Double, vol: Double, rnd: Random,
        lowCut: Double, highCut: Double, attack: Double, release: Double, sweepTo: Double? = null,
    ) {
        val s0 = (start * RATE).toInt()
        val n = (dur * RATE).toInt()
        var lp = 0.0
        var hp = 0.0
        var prev = 0.0
        for (k in 0 until n) {
            val t = k.toDouble() / RATE
            val hi = if (sweepTo != null) highCut + (sweepTo - highCut) * (t / dur) else highCut
            val aLp = 1 - exp(-2 * PI * hi / RATE)
            val aHp = exp(-2 * PI * lowCut / RATE)
            val x = rnd.nextDouble(-1.0, 1.0)
            lp += aLp * (x - lp)
            hp = aHp * (hp + lp - prev)
            prev = lp
            val e = when {
                t < attack -> t / attack
                t > dur - release -> max(0.0, (dur - t) / release)
                else -> 1.0
            }
            b.add(s0 + k, hp * e * vol)
        }
    }

    /** Kleiner Raumhall (Schroeder: 4 Kammfilter + 2 Allpässe). */
    private fun reverb(src: DoubleArray, mix: Double): DoubleArray {
        val combs = intArrayOf(1116, 1188, 1277, 1356)
        val out = DoubleArray(src.size)
        for (d in combs) {
            val buf = DoubleArray(d)
            var i = 0
            for (k in src.indices) {
                val y = buf[i]
                buf[i] = src[k] + y * 0.74
                out[k] += y / combs.size
                i = (i + 1) % d
            }
        }
        for (d in intArrayOf(556, 441)) {
            val buf = DoubleArray(d)
            var i = 0
            for (k in out.indices) {
                val bufOut = buf[i]
                val x = out[k]
                val y = -x + bufOut
                buf[i] = x + bufOut * 0.5
                out[k] = y
                i = (i + 1) % d
            }
        }
        return DoubleArray(src.size) { k -> src[k] + out[k] * mix }
    }

    private fun finish(b: Buf, reverbMix: Double, gain: Double): ShortArray {
        val wet = if (reverbMix > 0) reverb(b.data, reverbMix) else b.data
        val peak = wet.maxOf { abs(it) }.coerceAtLeast(1e-6)
        val norm = 0.92 / peak
        // sanft ein- und ausblenden, damit nichts knackt
        val fade = (0.004 * RATE).toInt()
        return ShortArray(wet.size) { i ->
            var v = tanh(wet[i] * norm * 1.1) * gain
            if (i < fade) v *= i.toDouble() / fade
            if (i > wet.size - fade) v *= (wet.size - i).toDouble() / fade
            (v * Short.MAX_VALUE).toInt().coerceIn(-32768, 32767).toShort()
        }
    }

    // ------------------------------------------------------------------ Die Klänge

    private fun render(sfx: Sfx): ShortArray {
        val rnd = Random(sfx.ordinal * 7919 + 13)
        return when (sfx) {
            Sfx.TAP -> Buf(0.09).let { b ->
                tone(b, 0.0, 1180.0, 760.0, 0.06, 0.6, attack = 0.002, bright = 0.1)
                noise(b, 0.0, 0.02, 0.18, rnd, lowCut = 2500.0, highCut = 9000.0, attack = 0.001, release = 0.015)
                finish(b, 0.0, 0.32)
            }
            Sfx.CORRECT -> Buf(0.85).let { b ->
                bell(b, 0.0, note(84), 0.55, 0.55)            // C6
                bell(b, 0.075, note(88), 0.65, 0.6)           // E6
                bell(b, 0.075, note(91), 0.5, 0.18, ratio = 2.0, index = 1.2) // G6 leise darüber
                tone(b, 0.12, note(103), note(103), 0.25, 0.06, bright = 0.0) // Glitzer
                finish(b, 0.22, 0.62)
            }
            Sfx.WRONG -> Buf(0.5).let { b ->
                tone(b, 0.0, note(55), note(54), 0.18, 0.55, attack = 0.006, bright = 0.45)  // G3
                tone(b, 0.13, note(52), note(50) * 0.985, 0.32, 0.6, attack = 0.006, bright = 0.45) // E3 sinkt
                finish(b, 0.12, 0.5)
            }
            Sfx.COMBO -> Buf(0.9).let { b ->
                listOf(84, 86, 88, 91, 93, 96).forEachIndexed { i, m -> pluck(b, i * 0.045, note(m), 0.45, 0.42, rnd) }
                bell(b, 0.27, note(96), 0.55, 0.38)
                finish(b, 0.25, 0.6)
            }
            Sfx.COMPLETE -> Buf(1.8).let { b ->
                listOf(72, 76, 79, 84).forEachIndexed { i, m ->
                    pluck(b, i * 0.085, note(m), 0.6, 0.45, rnd)
                    bell(b, i * 0.085, note(m + 12), 0.45, 0.16)
                }
                // warmer Schlussakkord
                listOf(72, 76, 79, 84).forEach { m -> tone(b, 0.36, note(m), note(m), 1.2, 0.17, attack = 0.03, bright = 0.3) }
                bell(b, 0.36, note(96), 1.0, 0.28)
                bell(b, 0.5, note(100), 0.8, 0.14)
                finish(b, 0.3, 0.7)
            }
            Sfx.STREAK -> Buf(1.4).let { b ->
                noise(b, 0.0, 0.42, 0.55, rnd, lowCut = 200.0, highCut = 600.0, attack = 0.18, release = 0.15, sweepTo = 4200.0)
                // Knistern
                repeat(9) { i -> noise(b, 0.08 + i * 0.035 + rnd.nextDouble(0.0, 0.02), 0.012, 0.35, rnd, 1500.0, 7000.0, 0.001, 0.008) }
                listOf(79, 83, 86).forEach { m -> bell(b, 0.34, note(m), 0.9, 0.3) }
                bell(b, 0.42, note(91), 0.8, 0.24)
                finish(b, 0.28, 0.66)
            }
            Sfx.UNLOCK -> Buf(1.6).let { b ->
                listOf(72, 74, 76, 79, 81, 84, 86, 88).forEachIndexed { i, m -> pluck(b, i * 0.038, note(m), 0.7, 0.34, rnd) }
                listOf(84, 88, 91).forEach { m -> bell(b, 0.32, note(m), 1.1, 0.26) }
                tone(b, 0.32, note(103), note(103), 0.5, 0.05, bright = 0.0)
                finish(b, 0.32, 0.66)
            }
            Sfx.COIN -> Buf(0.42).let { b ->
                chip(b, 0.0, note(83), 0.07, 0.6)   // B5
                chip(b, 0.065, note(88), 0.32, 0.6) // E6
                finish(b, 0.08, 0.5)
            }
            Sfx.HISS -> Buf(0.55).let { b ->
                noise(b, 0.0, 0.5, 0.8, rnd, lowCut = 3800.0, highCut = 9500.0, attack = 0.04, release = 0.22)
                finish(b, 0.0, 0.34)
            }
            Sfx.RUN -> Buf(0.22).let { b ->
                tone(b, 0.0, 420.0, 1250.0, 0.13, 0.5, attack = 0.003, bright = 0.15)
                chip(b, 0.11, note(91), 0.09, 0.25)
                finish(b, 0.05, 0.36)
            }
        }
    }
}

package com.treblenote.scout.data.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.treblenote.scout.core.config.GameConfig
import com.treblenote.scout.domain.repository.TonePlayer
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin

class AudioTrackTonePlayer : TonePlayer {

    private var track: AudioTrack? = null
    private val cache = mutableMapOf<Int, ShortArray>()

    override fun play(midi: Int) {
        stop()
        val samples = cache.getOrPut(midi) { synthesize(midi) }
        val created = runCatching { build(samples) }.getOrNull() ?: return
        runCatching {
            created.write(samples, 0, samples.size)
            created.play()
        }
        track = created
    }

    override fun stop() {
        track?.let { active ->
            runCatching { active.stop() }
            runCatching { active.release() }
        }
        track = null
    }

    private fun build(samples: ShortArray): AudioTrack =
        AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(samples.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

    private fun synthesize(midi: Int): ShortArray {
        val frames = SAMPLE_RATE * GameConfig.TONE_MS / 1000
        val fade = SAMPLE_RATE * FADE_MS / 1000
        val frequency = BASE_FREQUENCY * 2.0.pow((midi - BASE_MIDI) / 12.0)
        val samples = ShortArray(frames)
        for (index in 0 until frames) {
            val envelope = when {
                index < fade -> index.toDouble() / fade
                index > frames - fade -> (frames - index).toDouble() / fade
                else -> 1.0
            }
            val value = sin(2.0 * PI * frequency * index / SAMPLE_RATE) * envelope * AMPLITUDE
            samples[index] = value.toInt().toShort()
        }
        return samples
    }

    private companion object {
        const val SAMPLE_RATE = 44100
        const val FADE_MS = 12
        const val BASE_FREQUENCY = 440.0
        const val BASE_MIDI = 69
        const val AMPLITUDE = 8200.0
    }
}

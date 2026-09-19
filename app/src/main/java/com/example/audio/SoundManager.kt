package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Supported game sound effects for in-game events and interactive previews.
 */
enum class GameSoundEffect(
    val titleEn: String,
    val titleAr: String,
    val icon: String,
    val descriptionEn: String,
    val descriptionAr: String
) {
    MOVE_X("Player X Move", "حركة اللاعب X", "✖️", "Bright, crisp pop with ascending harmonic", "فرقعة نقية صاعدة سريعة"),
    MOVE_O("Player O Move", "حركة اللاعب O", "⭕", "Warm resonant bell chime", "رنين جرس دافئ ومرن"),
    MOVE_AI("AI Move", "حركة الذكاء الاصطناعي", "🤖", "Cybernetic digital synth chirp", "نغمة إلكترونية رقمية ذكية"),
    WIN("Victory Fanfare", "نغمة الفوز والانتصار", "🏆", "Triumphant 4-note celebration fanfare", "معزوفة انتصار متصاعدة متناغمة"),
    LOSS("Defeat Melancholy", "نغمة الهزيمة والتراجع", "💔", "Solemn descending minor cadence", "نغمة هبوط كلاسيكية حزينة"),
    DRAW("Tie / Stalemate", "نغمة التعادل", "🤝", "Neutral mellow two-tone resolution", "نغمة حيادية ناعمة ومتوازنة"),
    GAME_START("Game Start", "بدء المباراة", "▶️", "Energetic ascending launch call", "نغمة انطلاق حيوية وسريعة"),
    TIMER_WARNING("Timer Warning", "تنبيه الوقت", "⏱️", "Urgent high alert tick", "تكة تنبيه سريعة للوقت"),
    ACHIEVEMENT("Achievement Unlocked", "إنجاز مفتوح", "⭐", "Sparkling golden flourish", "زغردة ذهبية متألقة للإنجازات"),
    REWARD_CLAIM("Daily Reward", "مكافأة الحضور", "🪙", "Cascading sparkling coin arpeggio", "شلال كوينز متلألئ")
}

data class SynthTone(
    val startFrequency: Double,
    val endFrequency: Double = startFrequency,
    val durationMs: Int,
    val harmonics: List<Pair<Double, Double>> = emptyList()
)

class SoundManager(private val context: Context) {
    var soundEnabled: Boolean = true
    var hapticsEnabled: Boolean = true
    var soundVolume: Float = 0.85f

    private val sampleRate = 22050
    private val scope = CoroutineScope(Dispatchers.Default)

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    // ==================== UNIQUE MOVE CLIPS ====================

    fun playMove(isX: Boolean, isAi: Boolean = false) {
        if (isAi) {
            playAIMove()
        } else if (isX) {
            playMoveX()
        } else {
            playMoveO()
        }
    }

    fun playMoveX() {
        vibrate(25)
        if (!soundEnabled) return
        scope.launch {
            // Bright, crisp pop with upward slide: 720Hz -> 960Hz with 2nd harmonic
            val tones = listOf(
                SynthTone(
                    startFrequency = 720.0,
                    endFrequency = 960.0,
                    durationMs = 70,
                    harmonics = listOf(2.0 to 0.22)
                )
            )
            playBuffer(synthesizeTones(tones))
        }
    }

    fun playMoveO() {
        vibrate(35)
        if (!soundEnabled) return
        scope.launch {
            // Warm resonant acoustic bell chime: 460Hz dropping to 390Hz with warm harmonics
            val tones = listOf(
                SynthTone(
                    startFrequency = 460.0,
                    endFrequency = 390.0,
                    durationMs = 85,
                    harmonics = listOf(2.0 to 0.28, 3.0 to 0.12)
                )
            )
            playBuffer(synthesizeTones(tones))
        }
    }

    fun playAIMove() {
        vibrate(20)
        if (!soundEnabled) return
        scope.launch {
            // Cybernetic double chirp
            val tones = listOf(
                SynthTone(startFrequency = 520.0, endFrequency = 640.0, durationMs = 30),
                SynthTone(startFrequency = 680.0, endFrequency = 780.0, durationMs = 45)
            )
            playBuffer(synthesizeTones(tones))
        }
    }

    // ==================== OUTCOME CLIPS: WIN, LOSS, DRAW ====================

    fun playWin() {
        vibratePattern(longArrayOf(0, 60, 40, 100, 50, 140))
        if (!soundEnabled) return
        scope.launch {
            // Triumphant 4-note ascending fanfare: C5 -> E5 -> G5 -> C6 with rich harmonics
            val tones = listOf(
                SynthTone(523.25, 523.25, 80, listOf(2.0 to 0.2)), // C5
                SynthTone(659.25, 659.25, 80, listOf(2.0 to 0.2)), // E5
                SynthTone(783.99, 783.99, 90, listOf(2.0 to 0.25)), // G5
                SynthTone(1046.50, 1046.50, 260, listOf(2.0 to 0.3, 0.5 to 0.35)) // C6 with lingering octave resonance
            )
            playBuffer(synthesizeTones(tones))
        }
    }

    fun playLoss() {
        vibratePattern(longArrayOf(0, 100, 60, 180))
        if (!soundEnabled) return
        scope.launch {
            // Solemn descending minor cadence: Eb4 -> D4 -> C4 -> G3 with soft melancholic fade
            val tones = listOf(
                SynthTone(311.13, 311.13, 95, listOf(0.5 to 0.2)), // Eb4
                SynthTone(293.66, 293.66, 95, listOf(0.5 to 0.2)), // D4
                SynthTone(261.63, 261.63, 110, listOf(0.5 to 0.25)), // C4
                SynthTone(196.00, 196.00, 300, listOf(1.189 to 0.25, 0.5 to 0.3)) // G3 + minor 3rd undertone fading smoothly
            )
            playBuffer(synthesizeTones(tones))
        }
    }

    fun playDraw() {
        vibratePattern(longArrayOf(0, 45, 40, 45))
        if (!soundEnabled) return
        scope.launch {
            // Neutral gentle resolution: A4 -> Ab4
            val tones = listOf(
                SynthTone(440.0, 440.0, 90),
                SynthTone(415.3, 415.3, 140, listOf(2.0 to 0.15))
            )
            playBuffer(synthesizeTones(tones))
        }
    }

    // ==================== ATMOSPHERE & EVENT CLIPS ====================

    fun playGameStart() {
        vibrate(30)
        if (!soundEnabled) return
        scope.launch {
            // Ascending power-up: C5 -> G5
            val tones = listOf(
                SynthTone(523.25, 523.25, 65),
                SynthTone(783.99, 783.99, 120, listOf(2.0 to 0.2))
            )
            playBuffer(synthesizeTones(tones))
        }
    }

    fun playTimerWarning() {
        vibrate(20)
        if (!soundEnabled) return
        scope.launch {
            // Urgent high tick
            val tones = listOf(
                SynthTone(940.0, 940.0, 35)
            )
            playBuffer(synthesizeTones(tones))
        }
    }

    fun playAchievementUnlocked() {
        vibratePattern(longArrayOf(0, 40, 30, 60, 30, 90))
        if (!soundEnabled) return
        scope.launch {
            // Golden shimmer: E5 -> G5 -> B5 -> E6
            val tones = listOf(
                SynthTone(659.25, 659.25, 65),
                SynthTone(783.99, 783.99, 65),
                SynthTone(987.77, 987.77, 75),
                SynthTone(1318.51, 1318.51, 240, listOf(2.0 to 0.35, 0.5 to 0.2))
            )
            playBuffer(synthesizeTones(tones))
        }
    }

    fun playRewardClaim() {
        vibratePattern(longArrayOf(0, 40, 30, 60, 30, 80))
        if (!soundEnabled) return
        scope.launch {
            // Coin cascade: D5 -> F#5 -> A5 -> D6
            val tones = listOf(
                SynthTone(587.33, 587.33, 60),
                SynthTone(739.99, 739.99, 60),
                SynthTone(880.00, 880.00, 70),
                SynthTone(1174.66, 1174.66, 190, listOf(2.0 to 0.25))
            )
            playBuffer(synthesizeTones(tones))
        }
    }

    fun playUndo() {
        vibrate(20)
        if (!soundEnabled) return
        scope.launch {
            // Reverse slide
            val tones = listOf(
                SynthTone(startFrequency = 720.0, endFrequency = 340.0, durationMs = 80)
            )
            playBuffer(synthesizeTones(tones))
        }
    }

    fun playClick() {
        vibrate(15)
        if (!soundEnabled) return
        scope.launch {
            val tones = listOf(
                SynthTone(1000.0, 1000.0, 20)
            )
            playBuffer(synthesizeTones(tones))
        }
    }

    fun playEffect(effect: GameSoundEffect) {
        when (effect) {
            GameSoundEffect.MOVE_X -> playMoveX()
            GameSoundEffect.MOVE_O -> playMoveO()
            GameSoundEffect.MOVE_AI -> playAIMove()
            GameSoundEffect.WIN -> playWin()
            GameSoundEffect.LOSS -> playLoss()
            GameSoundEffect.DRAW -> playDraw()
            GameSoundEffect.GAME_START -> playGameStart()
            GameSoundEffect.TIMER_WARNING -> playTimerWarning()
            GameSoundEffect.ACHIEVEMENT -> playAchievementUnlocked()
            GameSoundEffect.REWARD_CLAIM -> playRewardClaim()
        }
    }

    // ==================== HAPTIC & SYNTHESIZER ENGINE ====================

    private fun vibrate(durationMs: Long) {
        if (!hapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (ignored: Exception) {}
    }

    private fun vibratePattern(pattern: LongArray) {
        if (!hapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, -1)
            }
        } catch (ignored: Exception) {}
    }

    private fun synthesizeTones(tones: List<SynthTone>): ShortArray {
        val totalSamples = tones.sumOf { (it.durationMs * sampleRate) / 1000 }
        val buffer = ShortArray(totalSamples)
        var bufferOffset = 0
        val volFactor = soundVolume.coerceIn(0f, 1f)

        for (tone in tones) {
            val numSamples = (tone.durationMs * sampleRate) / 1000
            var phase = 0.0

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val currentFreq = tone.startFrequency + (tone.endFrequency - tone.startFrequency) * progress
                val phaseIncrement = (2.0 * PI * currentFreq) / sampleRate
                phase += phaseIncrement
                if (phase > 2.0 * PI) phase -= 2.0 * PI

                // Smooth ADSR envelope: fast attack (5%), steady, soft decay (25%)
                val envelope = when {
                    i < numSamples * 0.05 -> i / (numSamples * 0.05)
                    i > numSamples * 0.75 -> (numSamples - i) / (numSamples * 0.25)
                    else -> 1.0
                }

                var sampleVal = sin(phase)
                for ((multiplier, amp) in tone.harmonics) {
                    sampleVal += sin(phase * multiplier) * amp
                }

                // Normalize and apply volume
                val finalSample = (sampleVal * 32767 * envelope * volFactor * 0.65).toInt().coerceIn(-32768, 32767)
                buffer[bufferOffset + i] = finalSample.toShort()
            }
            bufferOffset += numSamples
        }
        return buffer
    }

    private fun playBuffer(buffer: ShortArray) {
        if (buffer.isEmpty()) return

        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val trackBufferSize = maxOf(buffer.size * 2, minBufferSize)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val audioFormat = AudioFormat.Builder()
            .setSampleRate(sampleRate)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()

        var track: AudioTrack? = null
        try {
            track = AudioTrack(
                audioAttributes,
                audioFormat,
                trackBufferSize,
                AudioTrack.MODE_STATIC,
                android.media.AudioManager.AUDIO_SESSION_ID_GENERATE
            )
            track.write(buffer, 0, buffer.size)
            track.play()
            val durationMs = (buffer.size * 1000L) / sampleRate
            Thread.sleep(durationMs + 15)
            track.stop()
        } catch (ignored: Exception) {
        } finally {
            try {
                track?.release()
            } catch (ignored: Exception) {}
        }
    }
}


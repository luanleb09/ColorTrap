package com.colortrap.game.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log

/**
 * Quản lý sound effects
 */
class SoundManager(private val context: Context) {

    companion object {
        private const val TAG = "SoundManager"
        private const val MAX_STREAMS = 5
    }

    // Sound IDs
    private var tapCorrectSoundId: Int = -1
    private var tapWrongSoundId: Int = -1
    private var gameOverSoundId: Int = -1
    private var levelUpSoundId: Int = -1
    private var buttonClickSoundId: Int = -1

    private var soundPool: SoundPool? = null
    private var isSoundEnabled = true
    private var volume = 0.7f

    init {
        initSoundPool()
    }

    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(MAX_STREAMS)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load sounds from res/raw (nếu có)
        // tapCorrectSoundId = soundPool?.load(context, R.raw.tap_correct, 1) ?: -1
        // tapWrongSoundId = soundPool?.load(context, R.raw.tap_wrong, 1) ?: -1
        // gameOverSoundId = soundPool?.load(context, R.raw.game_over, 1) ?: -1
        // levelUpSoundId = soundPool?.load(context, R.raw.level_up, 1) ?: -1
        // buttonClickSoundId = soundPool?.load(context, R.raw.button_click, 1) ?: -1

        Log.d(TAG, "✅ SoundPool initialized")
    }

    /**
     * Play sound effects
     */
    fun playTapCorrect() = playSound(tapCorrectSoundId)
    fun playTapWrong() = playSound(tapWrongSoundId)
    fun playGameOver() = playSound(gameOverSoundId)
    fun playLevelUp() = playSound(levelUpSoundId)
    fun playButtonClick() = playSound(buttonClickSoundId)

    private fun playSound(soundId: Int) {
        if (!isSoundEnabled || soundId == -1) return

        soundPool?.play(soundId, volume, volume, 1, 0, 1f)
    }

    /**
     * Enable/Disable sound
     */
    fun setSoundEnabled(enabled: Boolean) {
        isSoundEnabled = enabled
        Log.d(TAG, "Sound ${if (enabled) "enabled" else "disabled"}")
    }

    /**
     * Set volume (0.0 - 1.0)
     */
    fun setVolume(newVolume: Float) {
        volume = newVolume.coerceIn(0f, 1f)
    }

    /**
     * Release resources
     */
    fun release() {
        soundPool?.release()
        soundPool = null
        Log.d(TAG, "🗑️ SoundPool released")
    }
}
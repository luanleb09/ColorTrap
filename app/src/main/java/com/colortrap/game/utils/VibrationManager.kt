package com.colortrap.game.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

/**
 * Quản lý haptic feedback
 */
class VibrationManager(private val context: Context) {

    companion object {
        private const val TAG = "VibrationManager"

        // Vibration patterns (ms)
        private const val TAP_CORRECT_DURATION = 50L
        private const val TAP_WRONG_DURATION = 200L
        private const val GAME_OVER_DURATION = 500L
    }

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var isVibrationEnabled = true

    /**
     * Vibrate patterns
     */
    fun vibrateTapCorrect() = vibrate(TAP_CORRECT_DURATION)
    fun vibrateTapWrong() = vibrate(TAP_WRONG_DURATION)
    fun vibrateGameOver() = vibrate(GAME_OVER_DURATION)

    /**
     * Vibrate với duration
     */
    private fun vibrate(durationMs: Long) {
        if (!isVibrationEnabled || vibrator == null) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createOneShot(
                    durationMs,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Vibration error: ${e.message}")
        }
    }

    /**
     * Vibrate với pattern (long[] = [delay, vibrate, delay, vibrate...])
     */
    fun vibratePattern(pattern: LongArray, repeat: Int = -1) {
        if (!isVibrationEnabled || vibrator == null) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(pattern, repeat)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, repeat)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Pattern vibration error: ${e.message}")
        }
    }

    /**
     * Enable/Disable vibration
     */
    fun setVibrationEnabled(enabled: Boolean) {
        isVibrationEnabled = enabled
        Log.d(TAG, "Vibration ${if (enabled) "enabled" else "disabled"}")
    }

    /**
     * Check if device supports vibration
     */
    fun hasVibrator(): Boolean = vibrator?.hasVibrator() ?: false
}
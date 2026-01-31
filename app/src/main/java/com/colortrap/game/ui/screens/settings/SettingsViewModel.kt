package com.colortrap.game.ui.screens.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.colortrap.game.data.local.PreferencesManager
import com.colortrap.game.utils.SoundManager
import com.colortrap.game.utils.VibrationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * SettingsViewModel
 * - Load/Save settings từ SharedPreferences
 * - Update SoundManager & VibrationManager
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "SettingsViewModel"
    }

    private val context = application.applicationContext
    private val prefsManager = PreferencesManager(context)
    private val soundManager = SoundManager(context)
    private val vibrationManager = VibrationManager(context)

    // Settings State
    private val _settings = MutableStateFlow(Settings())
    val settings: StateFlow<Settings> = _settings.asStateFlow()

    init {
        loadSettings()
    }

    /**
     * Load settings từ SharedPreferences
     */
    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val soundEnabled = prefsManager.getSoundEnabled()
                val vibrationEnabled = prefsManager.getVibrationEnabled()
                val musicEnabled = prefsManager.getMusicEnabled()
                val volume = prefsManager.getVolume()

                _settings.value = Settings(
                    soundEnabled = soundEnabled,
                    vibrationEnabled = vibrationEnabled,
                    musicEnabled = musicEnabled,
                    volume = volume
                )

                // Apply to managers
                soundManager.setSoundEnabled(soundEnabled)
                soundManager.setVolume(volume)
                vibrationManager.setVibrationEnabled(vibrationEnabled)

                Log.d(TAG, "✅ Settings loaded: Sound=$soundEnabled, Vibration=$vibrationEnabled, Volume=$volume")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Settings loading failed: ${e.message}")
            }
        }
    }

    /**
     * Set sound enabled
     */
    fun setSoundEnabled(enabled: Boolean) {
        _settings.update { it.copy(soundEnabled = enabled) }
        prefsManager.setSoundEnabled(enabled)
        soundManager.setSoundEnabled(enabled)

        // Play test sound
        if (enabled) {
            soundManager.playButtonClick()
        }

        Log.d(TAG, "Sound: $enabled")
    }

    /**
     * Set vibration enabled
     */
    fun setVibrationEnabled(enabled: Boolean) {
        _settings.update { it.copy(vibrationEnabled = enabled) }
        prefsManager.setVibrationEnabled(enabled)
        vibrationManager.setVibrationEnabled(enabled)

        // Test vibration
        if (enabled) {
            vibrationManager.vibrateTapCorrect()
        }

        Log.d(TAG, "Vibration: $enabled")
    }

    /**
     * Set music enabled
     */
    fun setMusicEnabled(enabled: Boolean) {
        _settings.update { it.copy(musicEnabled = enabled) }
        prefsManager.setMusicEnabled(enabled)

        // TODO: Start/stop background music

        Log.d(TAG, "Music: $enabled")
    }

    /**
     * Set volume
     */
    fun setVolume(volume: Float) {
        val clampedVolume = volume.coerceIn(0f, 1f)
        _settings.update { it.copy(volume = clampedVolume) }
        prefsManager.setVolume(clampedVolume)
        soundManager.setVolume(clampedVolume)

        Log.d(TAG, "Volume: ${(clampedVolume * 100).toInt()}%")
    }

    /**
     * Reset all progress
     */
    fun resetProgress() {
        viewModelScope.launch {
            try {
                prefsManager.resetAllData()
                Log.d(TAG, "✅ Progress reset successfully")

                // Play confirmation sound
                soundManager.playButtonClick()

            } catch (e: Exception) {
                Log.e(TAG, "❌ Reset failed: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }

    /**
     * Settings data class
     */
    data class Settings(
        val soundEnabled: Boolean = true,
        val vibrationEnabled: Boolean = true,
        val musicEnabled: Boolean = false,
        val volume: Float = 0.7f
    )
}
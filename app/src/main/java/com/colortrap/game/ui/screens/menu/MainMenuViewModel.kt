package com.colortrap.game.ui.screens.menu

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.colortrap.game.data.local.PreferencesManager
import com.colortrap.game.data.models.GameMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * MainMenuViewModel
 * - Load high scores từ SharedPreferences
 * - Load coins
 * - Update khi player quay lại từ game
 * - Manage tutorial display (NEW!)
 */
class MainMenuViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "MainMenuViewModel"
    }

    private val prefsManager = PreferencesManager(application.applicationContext)

    // High scores cho mỗi mode
    private val _highScores = MutableStateFlow<Map<GameMode, Int>>(emptyMap())
    val highScores: StateFlow<Map<GameMode, Int>> = _highScores.asStateFlow()

    // Coins
    private val _coins = MutableStateFlow(0)
    val coins: StateFlow<Int> = _coins.asStateFlow()

    // Stats (optional - để hiển thị total games played, etc.)
    private val _totalGames = MutableStateFlow(0)
    val totalGames: StateFlow<Int> = _totalGames.asStateFlow()

    init {
        loadPlayerData()
    }

    /**
     * Load tất cả player data từ SharedPreferences
     */
    private fun loadPlayerData() {
        viewModelScope.launch {
            try {
                // Load high scores
                val scores = mutableMapOf<GameMode, Int>()
                scores[GameMode.NORMAL] = prefsManager.getHighScore(GameMode.NORMAL)
                scores[GameMode.HARD] = prefsManager.getHighScore(GameMode.HARD)
                scores[GameMode.SUPER_HARD] = prefsManager.getHighScore(GameMode.SUPER_HARD)
                scores[GameMode.RELAX] = prefsManager.getHighScore(GameMode.RELAX)

                _highScores.value = scores

                // Load coins
                _coins.value = prefsManager.getCoins()

                // Load stats
                _totalGames.value = prefsManager.getTotalGames()

                Log.d(TAG, "✅ Player data loaded:")
                Log.d(TAG, "  High Scores: $scores")
                Log.d(TAG, "  Coins: ${_coins.value}")
                Log.d(TAG, "  Total Games: ${_totalGames.value}")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error loading player data: ${e.message}")
            }
        }
    }

    /**
     * Refresh data (gọi khi player quay lại từ GameOver)
     */
    fun refreshData() {
        loadPlayerData()
    }

    /**
     * Get specific high score
     */
    fun getHighScore(mode: GameMode): Int {
        return _highScores.value[mode] ?: 0
    }

    /**
     * Check if player has played before
     */
    fun hasPlayedBefore(): Boolean {
        return _totalGames.value > 0
    }

    /**
     * Get total score across all modes (for stats)
     */
    fun getTotalScore(): Int {
        return _highScores.value.values.sum()
    }

    // ==================== TUTORIAL FUNCTIONS (NEW!) ====================

    /**
     * Check if tutorial should be shown
     */
    fun shouldShowTutorial(): Boolean {
        return prefsManager.shouldShowTutorial()
    }

    /**
     * Get checkbox state for tutorial
     */
    fun getTutorialCheckboxState(): Boolean {
        return prefsManager.getTutorialCheckboxState()
    }

    /**
     * Save tutorial preference when dialog is dismissed
     */
    fun saveTutorialPreference(dontShowToday: Boolean) {
        prefsManager.saveTutorialPreference(dontShowToday)
        Log.d(TAG, "📖 Tutorial preference updated: dontShowToday=$dontShowToday")
    }

    /**
     * Force reset tutorial (for testing or settings)
     */
    fun resetTutorial() {
        prefsManager.resetTutorial()
        Log.d(TAG, "🔄 Tutorial reset")
    }
}

// ==================== VIEWMODEL FACTORY ====================

class MainMenuViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainMenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainMenuViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
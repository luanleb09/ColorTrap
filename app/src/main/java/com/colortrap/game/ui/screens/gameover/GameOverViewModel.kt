package com.colortrap.game.ui.screens.gameover

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.colortrap.game.data.local.PreferencesManager
import com.colortrap.game.data.models.GameMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * GameOverViewModel
 * - Check if new high score
 * - Calculate stats (streak, accuracy)
 * - Share functionality
 */
class GameOverViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "GameOverViewModel"
    }

    private val context = application.applicationContext
    private val prefsManager = PreferencesManager(context)

    // State
    private val _isNewHighScore = MutableStateFlow(false)
    val isNewHighScore: StateFlow<Boolean> = _isNewHighScore.asStateFlow()

    private val _stats = MutableStateFlow(GameStats())
    val stats: StateFlow<GameStats> = _stats.asStateFlow()

    /**
     * Initialize with game result
     */
    fun initialize(mode: GameMode, score: Int, level: Int) {
        viewModelScope.launch {
            try {
                // Check if new high score
                val previousHighScore = prefsManager.getHighScore(mode)
                _isNewHighScore.value = score > previousHighScore

                // Calculate stats
                val coinsEarned = calculateCoinsEarned(level)
                val bestStreak = level // Approximation (level = số correct taps liên tiếp)
                val accuracy = calculateAccuracy(level)

                _stats.value = GameStats(
                    coinsEarned = coinsEarned,
                    bestStreak = bestStreak,
                    accuracy = accuracy,
                    previousHighScore = previousHighScore
                )

                Log.d(TAG, "✅ GameOver initialized:")
                Log.d(TAG, "  Score: $score (High: $previousHighScore)")
                Log.d(TAG, "  Level: $level")
                Log.d(TAG, "  New Record: ${_isNewHighScore.value}")
                Log.d(TAG, "  Coins: +$coinsEarned")

            } catch (e: Exception) {
                Log.e(TAG, "❌ GameOver initialization failed: ${e.message}")
            }
        }
    }

    /**
     * Calculate coins earned
     */
    private fun calculateCoinsEarned(level: Int): Int {
        return (level * 10).coerceAtLeast(10)
    }

    /**
     * Calculate accuracy (simplified)
     */
    private fun calculateAccuracy(level: Int): Int {
        // Assumption: Player reached level X with minimal mistakes
        // Accuracy = (correct taps / total taps) * 100
        // Simplified: 100% for reaching level, -2% per estimated mistake
        val estimatedMistakes = (level / 10).coerceAtMost(5)
        return (100 - estimatedMistakes * 2).coerceAtLeast(85)
    }

    /**
     * Share score to social media
     */
    fun shareScore(score: Int, level: Int, mode: GameMode) {
        try {
            val shareText = buildShareText(score, level, mode)
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            val chooserIntent = Intent.createChooser(shareIntent, "Share your score")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(chooserIntent)

            Log.d(TAG, "📤 Share intent launched")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Share failed: ${e.message}")
        }
    }

    /**
     * Build share message
     */
    private fun buildShareText(score: Int, level: Int, mode: GameMode): String {
        return """
            🎮 ColorTrap - ${mode.name.replace("_", " ")} Mode
            
            🏆 Score: ${score}
            📊 Level: $level
            
            Can you beat my score? 🔥
            
            #ColorTrap #MobileGame #PuzzleGame
        """.trimIndent()
    }

    /**
     * Game Stats data class
     */
    data class GameStats(
        val coinsEarned: Int = 0,
        val bestStreak: Int = 0,
        val accuracy: Int = 100,
        val previousHighScore: Int = 0
    )
}
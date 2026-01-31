package com.colortrap.game.ui.screens.game

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.colortrap.game.data.local.PreferencesManager
import com.colortrap.game.data.models.*
import com.colortrap.game.domain.ConfigManager
import com.colortrap.game.domain.DynamicLevelGenerator
import com.colortrap.game.domain.DynamicSkinManager
import com.colortrap.game.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * DynamicGameViewModel
 * CORE GAME LOGIC:
 * - Generate levels dynamically
 * - Timer countdown
 * - Validate tile clicks
 * - Calculate scores
 * - Handle power-ups
 * - Detect game over
 * - Reward ads & Interstitial ads
 */
class DynamicGameViewModel(
    application: Application,
    private val gameMode: GameMode
) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "DynamicGameViewModel"
        private const val TIMER_INTERVAL_MS = 100L // Update every 100ms
        private const val SLOW_TIME_MULTIPLIER = 0.5f // 50% slower
        private const val SLOW_TIME_DURATION = 5000L // 5 seconds

        // Ad constants
        private const val LOSSES_BEFORE_INTERSTITIAL = 3 // Show interstitial after 3 losses
    }

    // Managers
    private val context = application.applicationContext
    private val prefsManager = PreferencesManager(context)
    private val configManager = ConfigManager(context)
    private val skinManager = DynamicSkinManager(context)
    private val levelGenerator = DynamicLevelGenerator(context)
    private val soundManager = SoundManager(context)
    private val vibrationManager = VibrationManager(context)
    private val adManager = AdManager(context)

    // Game State
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // Timer Job
    private var timerJob: Job? = null
    private var slowTimeJob: Job? = null

    // Activity reference for showing ads
    private var activityRef: Activity? = null

    init {
        initializeGame()

        // Initialize ads
        adManager.initialize()
        adManager.loadRewardAd()
        adManager.loadInterstitialAd()
    }

    /**
     * Set activity reference for ad display
     * MUST be called from Screen before game starts
     */
    fun setActivity(activity: Activity) {
        activityRef = activity
        Log.d(TAG, "✅ Activity reference set for ads")
    }

    /**
     * Initialize game with first level
     */
    private fun initializeGame() {
        viewModelScope.launch {
            try {
                // Load configs
                val balanceConfig = configManager.loadBalanceConfig()

                // Initialize state với game mode
                _gameState.update { it.copy(mode = gameMode) }

                // Start first level
                startNewLevel()

                Log.d(TAG, "✅ Game initialized: Mode=$gameMode")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Game initialization failed: ${e.message}")
                _gameState.update { it.copy(isGameOver = true) }
            }
        }
    }

    /**
     * Start new level
     */
    private suspend fun startNewLevel() {
        try {
            val currentLevel = _gameState.value.level + 1

            // Generate level
            val dynamicLevel = levelGenerator.generateLevel(
                levelNumber = currentLevel,
                mode = gameMode
            )

            // Load bitmaps - FIXED WITH withContext
            val tiles = withContext(Dispatchers.IO) {
                dynamicLevel.tiles.map { tileData ->
                    val bitmap = skinManager.loadTileBitmap(
                        tileData.colorGroup,
                        tileData.variantIndex
                    )
                    tileData.copy(bitmap = bitmap)
                }
            }

            val forbiddenTiles = withContext(Dispatchers.IO) {
                dynamicLevel.forbiddenColors.map { tileData ->
                    val bitmap = skinManager.loadTileBitmap(
                        tileData.colorGroup,
                        tileData.variantIndex
                    )
                    tileData.copy(bitmap = bitmap)
                }
            }

            // Update state
            _gameState.update { state ->
                state.copy(
                    level = currentLevel,
                    tiles = tiles,
                    forbiddenTiles = forbiddenTiles,
                    timeRemaining = dynamicLevel.timeLimit,
                    maxTime = dynamicLevel.timeLimit,
                    showCountdown = currentLevel == 1, // Show countdown only at level 1
                    isPlaying = currentLevel > 1, // Don't start timer if showing countdown
                    isPaused = false
                )
            }

            // Start timer (only if not showing countdown)
            if (currentLevel > 1) {
                startTimer()
            }

            // Play level up sound
            if (currentLevel > 1) {
                soundManager.playLevelUp()
            }

            Log.d(TAG, "✅ Level $currentLevel started")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Level generation failed: ${e.message}")
            endGame()
        }
    }

    /**
     * Called when countdown finishes (level 1 only)
     */
    fun onCountdownComplete() {
        _gameState.update {
            it.copy(
                showCountdown = false,
                isPlaying = true
            )
        }
        startTimer()
        Log.d(TAG, "🎬 Countdown complete - game started!")
    }

    /**
     * Timer countdown
     */
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_gameState.value.timeRemaining > 0 && _gameState.value.isPlaying) {
                delay(TIMER_INTERVAL_MS)

                val decrement = if (_gameState.value.isSlowTimeActive) {
                    (TIMER_INTERVAL_MS / 1000f) * SLOW_TIME_MULTIPLIER
                } else {
                    TIMER_INTERVAL_MS / 1000f
                }

                _gameState.update { state ->
                    state.copy(
                        timeRemaining = (state.timeRemaining - decrement).coerceAtLeast(0f)
                    )
                }
            }

            // Time's up!
            if (_gameState.value.timeRemaining <= 0f) {
                onTimeUp()
            }
        }
    }

    /**
     * Handle tile click
     */
    fun onTileClick(tile: DynamicTile) {
        if (!_gameState.value.isPlaying || _gameState.value.isPaused) return

        val state = _gameState.value
        val isForbidden = state.forbiddenTiles.any { it.colorGroup == tile.colorGroup }

        if (isForbidden) {
            // WRONG CLICK
            onWrongClick()
        } else {
            // CORRECT CLICK
            onCorrectClick()
        }
    }

    /**
     * Correct click handler
     */
    private fun onCorrectClick() {
        soundManager.playTapCorrect()
        vibrationManager.vibrateTapCorrect()

        val state = _gameState.value

        // Calculate score
        val baseScore = Constants.SCORE_BASE
        val timeBonus = (state.timeRemaining * Constants.SCORE_TIME_BONUS_MULTIPLIER).toInt()
        val streakBonus = state.streak * Constants.SCORE_STREAK_MULTIPLIER
        val totalScore = baseScore + timeBonus + streakBonus

        _gameState.update {
            it.copy(
                score = it.score + totalScore,
                streak = it.streak + 1
            )
        }

        Log.d(TAG, "✅ Correct! Score: +$totalScore (base=$baseScore, time=$timeBonus, streak=$streakBonus)")

        // Next level
        viewModelScope.launch {
            delay(300) // Brief pause before next level
            startNewLevel()
        }
    }

    /**
     * Wrong click handler - UPDATED with Ads
     */
    private fun onWrongClick() {
        soundManager.playTapWrong()
        vibrationManager.vibrateTapWrong()

        val state = _gameState.value

        // Reset streak
        _gameState.update { it.copy(streak = 0) }

        if (state.mode == GameMode.RELAX) {
            // RELAX mode: Lose 1 life
            val newLives = state.lives - 1
            _gameState.update { it.copy(lives = newLives) }

            if (newLives <= 0) {
                // Lost all lives - check for reward ad
                onPlayerLoss()
            } else {
                // Continue with same level
                Log.d(TAG, "⚠️ Wrong click! Lives remaining: $newLives")
            }
        } else {
            // Other modes: Check for reward ad or end game
            onPlayerLoss()
        }
    }

    /**
     * Time up handler - UPDATED with Ads
     */
    private fun onTimeUp() {
        Log.d(TAG, "⏰ Time's up!")

        val state = _gameState.value

        if (state.mode == GameMode.RELAX) {
            // RELAX mode: Lose 1 life
            val newLives = state.lives - 1
            _gameState.update { it.copy(lives = newLives) }

            if (newLives <= 0) {
                onPlayerLoss()
            } else {
                // Restart same level
                viewModelScope.launch {
                    startNewLevel()
                }
            }
        } else {
            // Other modes: Check for reward ad or end game
            onPlayerLoss()
        }
    }

    // ==================== AD FUNCTIONS ====================

    /**
     * Called when player loses
     * Shows reward ad dialog if available
     */
    private fun onPlayerLoss() {
        val state = _gameState.value

        // Stop timer
        timerJob?.cancel()

        // Increment consecutive losses
        val newLossCount = state.consecutiveLosses + 1

        // Check if should show interstitial ad (after 3 losses)
        if (newLossCount >= LOSSES_BEFORE_INTERSTITIAL) {
            showInterstitialAd()
            // Reset loss counter after showing ad
            _gameState.update { it.copy(consecutiveLosses = 0) }
        } else {
            _gameState.update { it.copy(consecutiveLosses = newLossCount) }
        }

        // Check if can show reward ad dialog
        if (!state.hasUsedRewardAd && adManager.isRewardAdReady()) {
            // Show dialog offering reward ad
            _gameState.update {
                it.copy(
                    showRewardAdDialog = true,
                    isPlaying = false
                )
            }
            Log.d(TAG, "🎬 Showing reward ad dialog")
        } else {
            // No reward ad available - end game
            endGame()
        }
    }

    /**
     * User chooses to watch reward ad
     */
    fun watchRewardAd() {
        val activity = activityRef
        if (activity == null) {
            Log.e(TAG, "❌ Activity reference not set - cannot show ad")
            declineRewardAd()
            return
        }

        _gameState.update {
            it.copy(
                showRewardAdDialog = false,
                hasUsedRewardAd = true
            )
        }

        Log.d(TAG, "🎬 Showing reward ad...")

        // Show reward ad
        adManager.showRewardAd(
            activity = activity,
            onAdWatched = {
                // Reward: Skip current level
                onRewardAdComplete()
            },
            onAdFailed = {
                // Ad failed - end game
                Log.e(TAG, "❌ Reward ad failed")
                endGame()
            }
        )
    }

    /**
     * User declines reward ad
     */
    fun declineRewardAd() {
        _gameState.update { it.copy(showRewardAdDialog = false) }
        endGame()
        Log.d(TAG, "❌ User declined reward ad")
    }

    /**
     * Reward ad watched successfully - skip level
     */
    private fun onRewardAdComplete() {
        Log.d(TAG, "✅ Reward ad complete - skipping level")

        // Award coins for watching ad
        prefsManager.addCoins(50)

        // Reset consecutive losses (got a second chance)
        _gameState.update { it.copy(consecutiveLosses = 0) }

        // Continue to next level
        viewModelScope.launch {
            startNewLevel()
        }
    }

    /**
     * Show interstitial ad after 3 losses
     */
    private fun showInterstitialAd() {
        val activity = activityRef
        if (activity == null) {
            Log.e(TAG, "❌ Activity reference not set - cannot show interstitial ad")
            return
        }

        Log.d(TAG, "📺 Showing interstitial ad (3 consecutive losses)")

        adManager.showInterstitialAd(
            activity = activity,
            onAdClosed = {
                Log.d(TAG, "✅ Interstitial ad closed")
                // Ad shown, continue normal flow
            }
        )
    }

    /**
     * Check if reward ad is available
     */
    fun isRewardAdAvailable(): Boolean {
        val state = _gameState.value
        return !state.hasUsedRewardAd && adManager.isRewardAdReady()
    }

    // ==================== POWER-UPS ====================

    /**
     * Use Slow Time power-up
     */
    fun useSlowTime() {
        val state = _gameState.value
        if (state.slowTimeCount <= 0 || state.isSlowTimeActive) return

        _gameState.update {
            it.copy(
                slowTimeCount = it.slowTimeCount - 1,
                isSlowTimeActive = true
            )
        }

        // Cancel after duration
        slowTimeJob?.cancel()
        slowTimeJob = viewModelScope.launch {
            delay(SLOW_TIME_DURATION)
            _gameState.update { it.copy(isSlowTimeActive = false) }
        }

        Log.d(TAG, "🐌 Slow Time activated! (${SLOW_TIME_DURATION}ms)")
    }

    /**
     * Use Skip Level power-up
     */
    fun useSkipLevel() {
        val state = _gameState.value
        if (state.skipLevelCount <= 0) return

        _gameState.update {
            it.copy(skipLevelCount = it.skipLevelCount - 1)
        }

        // Skip to next level
        viewModelScope.launch {
            startNewLevel()
        }

        Log.d(TAG, "⏭️ Level skipped!")
    }

    /**
     * Pause game
     */
    fun pauseGame() {
        timerJob?.cancel()
        _gameState.update { it.copy(isPaused = true, isPlaying = false) }
        Log.d(TAG, "⏸️ Game paused")
    }

    /**
     * Resume game
     */
    fun resumeGame() {
        _gameState.update { it.copy(isPaused = false, isPlaying = true) }
        startTimer()
        Log.d(TAG, "▶️ Game resumed")
    }

    /**
     * End game
     */
    private fun endGame() {
        timerJob?.cancel()
        slowTimeJob?.cancel()

        soundManager.playGameOver()
        vibrationManager.vibrateGameOver()

        val state = _gameState.value

        // Save high score
        prefsManager.saveHighScore(state.mode, state.score)

        // Award coins
        val coinsEarned = (state.level * 10).coerceAtLeast(10)
        prefsManager.addCoins(coinsEarned)

        // Update stats
        prefsManager.incrementTotalGames()

        _gameState.update {
            it.copy(
                isGameOver = true,
                isPlaying = false
            )
        }

        Log.d(TAG, "🎮 Game Over! Score: ${state.score}, Level: ${state.level}, Coins: +$coinsEarned")
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        slowTimeJob?.cancel()
        soundManager.release()
        adManager.release()
        activityRef = null
    }

    /**
     * Game State data class
     */
    data class GameState(
        val mode: GameMode = GameMode.NORMAL,
        val level: Int = 0,
        val score: Int = 0,
        val streak: Int = 0,
        val lives: Int = 3, // For RELAX mode

        val tiles: List<DynamicTile> = emptyList(),
        val forbiddenTiles: List<DynamicTile> = emptyList(),

        val timeRemaining: Float = 5f,
        val maxTime: Float = 5f,

        val slowTimeCount: Int = 3,
        val skipLevelCount: Int = 3,

        val isSlowTimeActive: Boolean = false,
        val isPlaying: Boolean = false,
        val isPaused: Boolean = false,
        val isGameOver: Boolean = false,

        // Countdown overlay (level 1 only)
        val showCountdown: Boolean = false,

        // Ad-related states
        val showRewardAdDialog: Boolean = false,
        val hasUsedRewardAd: Boolean = false, // Only 1 per game session
        val consecutiveLosses: Int = 0 // Track for interstitial ad (show at 3)
    )
}


// ==================== ViewModel Factory ====================
class DynamicGameViewModelFactory(
    private val application: Application,
    private val gameMode: GameMode
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DynamicGameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DynamicGameViewModel(application, gameMode) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
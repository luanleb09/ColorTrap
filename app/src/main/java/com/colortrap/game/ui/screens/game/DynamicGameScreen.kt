package com.colortrap.game.ui.screens.game

import android.app.Activity
import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.colortrap.game.data.local.PreferencesManager
import com.colortrap.game.data.models.GameMode
import com.colortrap.game.data.models.SkinType
import com.colortrap.game.ui.components.*
import com.colortrap.game.ui.theme.SkinBackgroundColors

/**
 * DynamicGameScreen
 * Main game screen with:
 * - Countdown overlay (level 1 only)
 * - Top bar (score, level, timer, lives)
 * - Forbidden colors display
 * - Tile grid
 * - Power-ups bar
 * - Reward ad dialog
 *
 * UPDATED: Background changes based on current skin
 */
@Composable
fun DynamicGameScreen(
    gameMode: String,
    onNavigateToGameOver: (score: Int, level: Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: DynamicGameViewModel = viewModel(
        factory = DynamicGameViewModelFactory(
            LocalContext.current.applicationContext as Application,
            GameMode.valueOf(gameMode)
        )
    )
) {
    val gameState by viewModel.gameState.collectAsState()
    val context = LocalContext.current

    // Get current skin from preferences
    val prefsManager = remember { PreferencesManager(context) }
    val currentSkinId = remember { prefsManager.getCurrentSkin() }
    val currentSkin = remember { SkinType.fromId(currentSkinId) }

    // Get background gradient for current skin
    val backgroundGradient = remember(currentSkin) {
        SkinBackgroundColors.getBackgroundGradient(currentSkin)
    }

    // Set activity reference for ads
    LaunchedEffect(Unit) {
        (context as? Activity)?.let { activity ->
            viewModel.setActivity(activity)
        }
    }

    // Handle game over
    LaunchedEffect(gameState.isGameOver) {
        if (gameState.isGameOver) {
            onNavigateToGameOver(gameState.score, gameState.level)
        }
    }

    // Handle back press
    BackHandler {
        if (!gameState.showCountdown) { // Don't allow back during countdown
            viewModel.pauseGame()
            onNavigateBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundGradient.topColor,
                        backgroundGradient.bottomColor
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Stats Bar
            TopBar(
                score = gameState.score,
                level = gameState.level,
                timeRemaining = gameState.timeRemaining,
                lives = if (gameState.mode == GameMode.RELAX) gameState.lives else null,
                isTimeCritical = gameState.timeRemaining < 2f
            )

            // Forbidden Colors Display
            DynamicColorDisplayBar(
                forbiddenTiles = gameState.forbiddenTiles.map { tile ->
                    Pair(tile.bitmap!!, tile.colorGroup)
                }
            )

            // Main Game Grid
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                DynamicTileGrid(
                    tiles = gameState.tiles,
                    onTileClick = { tile ->
                        viewModel.onTileClick(tile)
                    },
                    isEnabled = gameState.isPlaying && !gameState.isPaused && !gameState.showCountdown
                )
            }

            // Item Bar (Power-ups)
            ItemBar(
                slowTimeCount = gameState.slowTimeCount,
                skipLevelCount = gameState.skipLevelCount,
                onSlowTimeClick = {
                    viewModel.useSlowTime()
                },
                onSkipLevelClick = {
                    viewModel.useSkipLevel()
                },
                isEnabled = gameState.isPlaying && !gameState.isPaused && !gameState.showCountdown
            )
        }

        // ==================== COUNTDOWN OVERLAY (Level 1 Only) ====================
        if (gameState.showCountdown) {
            CountdownOverlay(
                onCountdownComplete = {
                    viewModel.onCountdownComplete()
                }
            )
        }

        // ==================== REWARD AD DIALOG ====================
        if (gameState.showRewardAdDialog) {
            RewardAdDialog(
                onWatchAd = {
                    viewModel.watchRewardAd()
                },
                onDecline = {
                    viewModel.declineRewardAd()
                },
                isAdAvailable = viewModel.isRewardAdAvailable()
            )
        }
    }
}
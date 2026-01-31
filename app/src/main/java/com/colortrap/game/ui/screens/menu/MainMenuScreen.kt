package com.colortrap.game.ui.screens.menu

import android.app.Application
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.colortrap.game.data.models.GameMode
import com.colortrap.game.ui.components.AnimatedGradientText
import com.colortrap.game.ui.components.GradientStyle
import com.colortrap.game.ui.components.TutorialDialog
import com.colortrap.game.utils.formatScore
import com.colortrap.game.ui.components.CurvedRainbowTitle
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

/**
 * Main Menu Screen
 * - 4 Game Mode buttons (NORMAL, HARD, SUPER HARD, RELAX)
 * - Settings & Shop buttons
 * - High scores display
 * - Tutorial dialog
 */
@Composable
fun MainMenuScreen(
    onPlayGame: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToShop: () -> Unit,
    viewModel: MainMenuViewModel = viewModel(
        factory = MainMenuViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val highScores by viewModel.highScores.collectAsState()
    val coins by viewModel.coins.collectAsState()

    // Tutorial Dialog State
    var showTutorial by remember { mutableStateOf(false) }

    // Show tutorial on first composition
    LaunchedEffect(Unit) {
        if (viewModel.shouldShowTutorial()) {
            showTutorial = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Title + Coins
            TopSection(coins = coins)

            // Middle Section: Game Mode Buttons
            GameModeSection(
                highScores = highScores,
                onPlayGame = onPlayGame
            )

            // Bottom Section: How to Play + Settings + Shop
            BottomSection(
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToShop = onNavigateToShop,
                onShowTutorial = { showTutorial = true },
                highScores = highScores,
                coins = coins
            )
        }

        // Tutorial Dialog Overlay
        if (showTutorial) {
            TutorialDialog(
                initialCheckboxState = viewModel.getTutorialCheckboxState(),
                onDismiss = { dontShowToday ->
                    viewModel.saveTutorialPreference(dontShowToday)
                    showTutorial = false
                }
            )
        }
    }
}

@Composable
private fun TopSection(coins: Int) {
    // Lấy kích thước màn hình
    val configuration = LocalContext.current.resources.configuration
    val screenWidth = configuration.screenWidthDp.dp

    // Tính toán responsive
    val horizontalPadding = screenWidth * 0.1f // 10% mỗi bên = 20% total
    val titleContainerWidth = screenWidth * 0.8f // 80% screen width
    val fontSize = (screenWidth.value * 0.08f).sp // 8% của screen width

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Curved Rainbow Title with Animation
        val infiniteTransition = rememberInfiniteTransition(label = "title")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )

        Box(
            modifier = Modifier
                .width(titleContainerWidth) // Giới hạn width theo tỷ lệ màn hình
                .height(fontSize.value.dp * 2.5f) // Height tỷ lệ với font size
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            // Dùng AnimatedGradientText thay vì Curved để tránh overflow
            AnimatedGradientText(
                text = "COLORTRAP",
                fontSize = fontSize,
                style = GradientStyle.RAINBOW,
                animated = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Composable
private fun GameModeSection(
    highScores: Map<GameMode, Int>,
    onPlayGame: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // NORMAL Mode
        GameModeButton(
            mode = GameMode.NORMAL,
            highScore = highScores[GameMode.NORMAL] ?: 0,
            gradient = listOf(Color(0xFF4CAF50), Color(0xFF45A049)),
            onClick = { onPlayGame(GameMode.NORMAL.name) }
        )

        // HARD Mode
        GameModeButton(
            mode = GameMode.HARD,
            highScore = highScores[GameMode.HARD] ?: 0,
            gradient = listOf(Color(0xFFFF9800), Color(0xFFF57C00)),
            onClick = { onPlayGame(GameMode.HARD.name) }
        )

        // SUPER HARD Mode
        GameModeButton(
            mode = GameMode.SUPER_HARD,
            highScore = highScores[GameMode.SUPER_HARD] ?: 0,
            gradient = listOf(Color(0xFFF44336), Color(0xFFD32F2F)),
            onClick = { onPlayGame(GameMode.SUPER_HARD.name) }
        )

        // RELAX Mode
        GameModeButton(
            mode = GameMode.RELAX,
            highScore = highScores[GameMode.RELAX] ?: 0,
            gradient = listOf(Color(0xFF2196F3), Color(0xFF1976D2)),
            onClick = { onPlayGame(GameMode.RELAX.name) }
        )
    }
}

@Composable
private fun GameModeButton(
    mode: GameMode,
    highScore: Int,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .scale(scale)
            .clickable {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPressed) 2.dp else 6.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(colors = gradient)
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(
                    text = getModeDisplayName(mode),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = getModeDescription(mode),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            // High Score Badge
            if (highScore > 0) {
                Card(
                    modifier = Modifier.align(Alignment.TopEnd),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "BEST",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontSize = 10.sp
                        )
                        Text(
                            text = highScore.formatScore(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomSection(
    onNavigateToSettings: () -> Unit,
    onNavigateToShop: () -> Unit,
    onShowTutorial: () -> Unit,
    highScores: Map<GameMode, Int>,
    coins: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp)) // Space from RELAX mode (NEW!)

        // Button Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // How to Play Button
            IconButtonWithLabel(
                icon = Icons.Default.Info,
                label = "How to Play",
                onClick = onShowTutorial
            )

            // Settings Button
            IconButtonWithLabel(
                icon = Icons.Default.Settings,
                label = "Settings",
                onClick = onNavigateToSettings
            )

            // Shop Button
            IconButtonWithLabel(
                icon = Icons.Default.ShoppingCart,
                label = "Shop",
                onClick = onNavigateToShop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ad Banner Placeholder (NEW!)
        AdBannerPlaceholder()
    }
}

@Composable
private fun IconButtonWithLabel(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clickable {
                isPressed = true
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isPressed) 2.dp else 4.dp
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier
                    .padding(20.dp)
                    .size(32.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// Helper functions
private fun getModeDisplayName(mode: GameMode): String = when (mode) {
    GameMode.NORMAL -> "NORMAL"
    GameMode.HARD -> "HARD"
    GameMode.SUPER_HARD -> "SUPER HARD"
    GameMode.RELAX -> "RELAX"
}

private fun getModeDescription(mode: GameMode): String = when (mode) {
    GameMode.NORMAL -> "Balanced challenge"
    GameMode.HARD -> "Fast & tricky"
    GameMode.SUPER_HARD -> "Ultimate test"
    GameMode.RELAX -> "Casual with 3 lives"
}

// Ad Banner Placeholder (NEW!)
@Composable
private fun AdBannerPlaceholder() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📢 Ad Banner Here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}
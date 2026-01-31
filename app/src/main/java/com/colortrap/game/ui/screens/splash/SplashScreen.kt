package com.colortrap.game.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.colortrap.game.R

/**
 * SplashScreen
 * Shows loading animation while assets are being loaded
 *
 * UPDATED:
 * ✅ Changed from CircularProgressIndicator to horizontal LinearProgressIndicator
 * ✅ Progress bar positioned at bottom of screen
 * ✅ Added animated progress tracking
 */
@Composable
fun SplashScreen(
    onNavigateToMenu: () -> Unit,
    viewModel: SplashViewModel = viewModel()
) {
    val loadingState by viewModel.loadingState.collectAsState()

    // Calculate progress based on loading state
    val progress = remember(loadingState) {
        when (loadingState) {
            is SplashViewModel.LoadingState.Loading -> {
                when ((loadingState as SplashViewModel.LoadingState.Loading).message) {
                    "Initializing..." -> 0.0f
                    "Loading configs..." -> 0.25f
                    "Scanning color groups..." -> 0.5f
                    "Initializing skins..." -> 0.75f
                    "Preparing game..." -> 0.9f
                    else -> 0.0f
                }
            }
            is SplashViewModel.LoadingState.Success -> 1.0f
            is SplashViewModel.LoadingState.Error -> 0.0f
        }
    }

    // Animate progress smoothly
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "progress"
    )

    // Navigate when loading is complete
    LaunchedEffect(loadingState) {
        if (loadingState is SplashViewModel.LoadingState.Success) {
            kotlinx.coroutines.delay(500) // Brief pause before transition
            onNavigateToMenu()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Full Screen Background Image
        Image(
            painter = painterResource(id = R.drawable.colortrap_loading),
            contentDescription = "ColorTrap Loading",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay with loading content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)) // Semi-transparent overlay
        ) {
            // Loading Message - Centered
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                // Empty space - message moved below
            }

            // Progress Bar - Bottom of screen
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 48.dp, start = 32.dp, end = 32.dp)
            ) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Progress percentage
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Loading message - MOVED HERE (below progress bar)
                when (val state = loadingState) {
                    is SplashViewModel.LoadingState.Loading -> {
                        Text(
                            text = state.message,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    is SplashViewModel.LoadingState.Error -> {
                        Text(
                            text = state.message,
                            color = Color.Red,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}
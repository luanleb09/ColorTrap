package com.colortrap.game.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.dp

/**
 * Countdown Overlay
 * Shows "3...2...1...GO!" countdown at start of level 1
 *
 * Features:
 * - 3 second countdown
 * - Scale animation for each number
 * - Semi-transparent black overlay
 * - Auto-dismiss after countdown
 */
@Composable
fun CountdownOverlay(
    onCountdownComplete: () -> Unit
) {
    var countdownValue by remember { mutableStateOf(3) }
    var showGo by remember { mutableStateOf(false) }

    // Countdown logic
    LaunchedEffect(Unit) {
        // Count from 3 to 1
        for (i in 3 downTo 1) {
            countdownValue = i
            delay(1000) // 1 second per number
        }

        // Show "GO!"
        showGo = true
        delay(500) // Show "GO!" for 0.5 seconds

        // Complete
        onCountdownComplete()
    }

    // Scale animation for each number
    val infiniteTransition = rememberInfiniteTransition(label = "scale")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        if (showGo) {
            // "GO!" text
            Text(
                text = "GO!",
                fontSize = 120.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF4CAF50), // Green
                modifier = Modifier.scale(scale)
            )
        } else {
            // Countdown number
            Text(
                text = countdownValue.toString(),
                fontSize = 180.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.scale(scale)
            )
        }

        // Ready text at top
        if (!showGo) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "GET READY!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tap tiles that DON'T match the forbidden colors",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
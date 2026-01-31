package com.colortrap.game.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * TopBar hiển thị màu cấm (Forbidden Colors)
 * Động: Pulse animation, glow effect
 */
@Composable
fun DynamicColorDisplayBar(
    forbiddenTiles: List<Pair<Bitmap, String>>, // List<Bitmap, GroupName>
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "DON'T TAP",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Forbidden Colors Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            forbiddenTiles.forEachIndexed { index, (bitmap, groupName) ->
                ForbiddenColorItem(
                    bitmap = bitmap,
                    groupName = groupName,
                    animationDelay = index * 100 // Stagger animation
                )

                if (index < forbiddenTiles.size - 1) {
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
    }
}

@Composable
private fun ForbiddenColorItem(
    bitmap: Bitmap,
    groupName: String,
    animationDelay: Int = 0
) {
    // Pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 800,
                delayMillis = animationDelay,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Glow animation
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                delayMillis = animationDelay,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .size(64.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect (outer border)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.error.copy(alpha = glowAlpha),
                    shape = RoundedCornerShape(12.dp)
                )
        )

        // Image
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Forbidden: $groupName",
            modifier = Modifier
                .fillMaxSize(0.85f)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}
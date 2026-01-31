package com.colortrap.game.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Thanh items (power-ups): Slow Time, Skip Level, Revive
 */
@Composable
fun ItemBar(
    slowTimeCount: Int,
    skipLevelCount: Int,
    onSlowTimeClick: () -> Unit,
    onSkipLevelClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Slow Time Item
        ItemButton(
            icon = Icons.Default.Refresh, // Hoặc custom icon
            label = "Slow",
            count = slowTimeCount,
            onClick = onSlowTimeClick,
            isEnabled = isEnabled && slowTimeCount > 0,
            color = Color(0xFF4CAF50) // Green
        )

        // Skip Level Item
        ItemButton(
            icon = Icons.Default.KeyboardArrowRight, // Hoặc skip icon
            label = "Skip",
            count = skipLevelCount,
            onClick = onSkipLevelClick,
            isEnabled = isEnabled && skipLevelCount > 0,
            color = Color(0xFF2196F3) // Blue
        )
    }
}

@Composable
private fun ItemButton(
    icon: ImageVector,
    label: String,
    count: Int,
    onClick: () -> Unit,
    isEnabled: Boolean,
    color: Color
) {
    // Click animation
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "click"
    )

    // Color animation when disabled
    val buttonColor by animateColorAsState(
        targetValue = if (isEnabled) color else Color.Gray,
        animationSpec = tween(300),
        label = "color"
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clickable(enabled = isEnabled) {
                isPressed = true
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Button với badge
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            // Icon button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = buttonColor.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = buttonColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = buttonColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Count badge
            if (count > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            color = MaterialTheme.colorScheme.error,
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = count.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isEnabled) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}


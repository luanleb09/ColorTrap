package com.colortrap.game.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Rainbow Gradient Text
 * Creates colorful gradient text with animation
 *
 * FIXED: Using TextStyle.brush instead of BlendMode for better compatibility
 */

// ==================== GRADIENT PRESETS ====================

enum class GradientStyle {
    RAINBOW,    // 🌈 Rainbow colors
    SUNSET,     // 🌅 Warm colors
    OCEAN,      // 🌊 Cool colors
    NEON,       // 💡 Bright neon
    FIRE,       // 🔥 Hot colors
    CANDY       // 🍬 Sweet colors
}

fun getGradientColors(style: GradientStyle): List<Color> {
    return when (style) {
        GradientStyle.RAINBOW -> listOf(
            Color(0xFFFF0080), // Pink
            Color(0xFFFF0000), // Red
            Color(0xFFFF7F00), // Orange
            Color(0xFFFFFF00), // Yellow
            Color(0xFF00FF00), // Green
            Color(0xFF0000FF), // Blue
            Color(0xFF4B0082), // Indigo
            Color(0xFF9400D3)  // Violet
        )
        GradientStyle.SUNSET -> listOf(
            Color(0xFFFF6B6B), // Coral
            Color(0xFFFF8E53), // Orange
            Color(0xFFFFC837), // Yellow
            Color(0xFFFF6B6B)  // Back to coral
        )
        GradientStyle.OCEAN -> listOf(
            Color(0xFF00D4FF), // Cyan
            Color(0xFF0099FF), // Sky blue
            Color(0xFF0066FF), // Blue
            Color(0xFF3366FF)  // Deep blue
        )
        GradientStyle.NEON -> listOf(
            Color(0xFFFF00FF), // Magenta
            Color(0xFF00FFFF), // Cyan
            Color(0xFFFFFF00), // Yellow
            Color(0xFFFF00FF)  // Magenta
        )
        GradientStyle.FIRE -> listOf(
            Color(0xFFFF0000), // Red
            Color(0xFFFF4500), // Orange red
            Color(0xFFFF8C00), // Dark orange
            Color(0xFFFFD700)  // Gold
        )
        GradientStyle.CANDY -> listOf(
            Color(0xFFFF69B4), // Hot pink
            Color(0xFFFFB6C1), // Light pink
            Color(0xFF87CEEB), // Sky blue
            Color(0xFF98FB98)  // Pale green
        )
    }
}

// ==================== ANIMATED GRADIENT TEXT ====================

/**
 * Main function - Uses TextStyle.brush for gradient
 *
 * FIXED: Better compatibility across devices
 */
@Composable
fun AnimatedGradientText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 56.sp,
    fontWeight: FontWeight = FontWeight.ExtraBold,
    style: GradientStyle = GradientStyle.RAINBOW,
    animated: Boolean = true
) {
    // Animation offset
    var animationOffset by remember { mutableStateOf(0f) }

    // Animate gradient
    if (animated) {
        LaunchedEffect(Unit) {
            while (true) {
                animationOffset += 0.005f
                if (animationOffset > 1f) animationOffset = 0f
                delay(16) // ~60 FPS
            }
        }
    }

    val colors = getGradientColors(style)

    // Create animated gradient colors
    val gradientColors = remember(animationOffset, style) {
        if (!animated) {
            colors
        } else {
            val expandedColors = mutableListOf<Color>()
            val offset = animationOffset * colors.size

            for (i in 0 until 20) {
                val position = (i / 20f * colors.size + offset) % colors.size
                val colorIndex = position.toInt()
                val fraction = position - colorIndex

                val color1 = colors[colorIndex % colors.size]
                val color2 = colors[(colorIndex + 1) % colors.size]

                expandedColors.add(
                    Color(
                        red = lerp(color1.red, color2.red, fraction),
                        green = lerp(color1.green, color2.green, fraction),
                        blue = lerp(color1.blue, color2.blue, fraction),
                        alpha = 1f
                    )
                )
            }
            expandedColors
        }
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                brush = Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(1500f, 100f)
                ),
                fontSize = fontSize,
                fontWeight = fontWeight,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.4f),
                    offset = Offset(4f, 4f),
                    blurRadius = 10f
                ),
                textAlign = TextAlign.Center,
                letterSpacing = 4.sp
            )
        )
    }
}

// ==================== HELPER FUNCTION ====================

/**
 * Linear interpolation
 */
private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}

// ==================== CURVED RAINBOW TEXT ====================

/**
 * Curved Rainbow Title - Chữ cong như cầu vồng
 * Perfect for game title!
 */
@Composable
fun CurvedRainbowTitle(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 56.sp,
    style: GradientStyle = GradientStyle.RAINBOW,
    curveIntensity: Float = 3f // Độ cong (càng lớn càng cong)
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        text.forEachIndexed { index, char ->
            val totalChars = text.length
            val centerIndex = totalChars / 2f
            val distance = index - centerIndex

            // Parabola curve (arc shape)
            val curveOffset = -(distance * distance) * curveIntensity

            // Slight rotation for each character
            val rotation = distance * 3f

            Box(
                modifier = Modifier
                    .offset(y = curveOffset.dp)
                    .graphicsLayer {
                        rotationZ = rotation
                    }
            ) {
                AnimatedGradientText(
                    text = char.toString(),
                    fontSize = fontSize,
                    style = style,
                    animated = true
                )
            }
        }
    }
}

// ==================== STATIC GRADIENT ====================

@Composable
fun StaticGradientText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 56.sp,
    fontWeight: FontWeight = FontWeight.ExtraBold,
    style: GradientStyle = GradientStyle.RAINBOW
) {
    val colors = getGradientColors(style)

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                brush = Brush.linearGradient(colors = colors),
                fontSize = fontSize,
                fontWeight = fontWeight,
                textAlign = TextAlign.Center,
                letterSpacing = 4.sp
            )
        )
    }
}
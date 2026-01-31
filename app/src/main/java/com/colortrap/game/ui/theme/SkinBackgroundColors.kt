package com.colortrap.game.ui.theme

import androidx.compose.ui.graphics.Color
import com.colortrap.game.data.models.SkinType

/**
 * Skin Background Colors
 * Pastel, gentle colors for each skin theme
 * Designed to not interfere with game object colors
 */

data class SkinBackgroundGradient(
    val topColor: Color,
    val bottomColor: Color
)

object SkinBackgroundColors {

    /**
     * Get background gradient for a skin
     */
    fun getBackgroundGradient(skinType: SkinType): SkinBackgroundGradient {
        return when (skinType) {
            SkinType.GEOMETRIC -> SkinBackgroundGradient(
                topColor = Color(0xFFE3F2FD),    // Very light blue (more visible)
                bottomColor = Color(0xFFBBDEFB)   // Soft sky blue
            )

            SkinType.ANIMALS -> SkinBackgroundGradient(
                topColor = Color(0xFFFFF8E1),    // Light cream
                bottomColor = Color(0xFFFFECB3)   // Pale yellow
            )

            SkinType.EMOJI -> SkinBackgroundGradient(
                topColor = Color(0xFFFFF3E0),    // Peachy cream
                bottomColor = Color(0xFFFFE0B2)   // Light peach
            )

            SkinType.GEMS -> SkinBackgroundGradient(
                topColor = Color(0xFFE8EAF6),    // Very light indigo
                bottomColor = Color(0xFFC5CAE9)   // Light lavender
            )

            SkinType.FOOD -> SkinBackgroundGradient(
                topColor = Color(0xFFFCE4EC),    // Light pink
                bottomColor = Color(0xFFF8BBD0)   // Soft pink
            )

            SkinType.SPORTS -> SkinBackgroundGradient(
                topColor = Color(0xFFE0F2F1),    // Very light teal
                bottomColor = Color(0xFFB2DFDB)   // Soft cyan
            )

            SkinType.NATURE -> SkinBackgroundGradient(
                topColor = Color(0xFFF1F8E9),    // Very light green
                bottomColor = Color(0xFFDCEDC8)   // Pale green
            )

            SkinType.SPACE -> SkinBackgroundGradient(
                topColor = Color(0xFFE3F2FD),    // Very light blue
                bottomColor = Color(0xFFBBDEFB)   // Soft sky blue
            )

            SkinType.HALLOWEEN -> SkinBackgroundGradient(
                topColor = Color(0xFFFBE9E7),    // Very light orange
                bottomColor = Color(0xFFFFCCBC)   // Soft orange
            )

            SkinType.CHRISTMAS -> SkinBackgroundGradient(
                topColor = Color(0xFFFFEBEE),    // Very light red
                bottomColor = Color(0xFFFFCDD2)   // Soft pink-red
            )
        }
    }

    /**
     * Get single background color (for simpler backgrounds)
     */
    fun getSolidBackground(skinType: SkinType): Color {
        return when (skinType) {
            SkinType.GEOMETRIC -> Color(0xFFF5F5F5)  // Light gray
            SkinType.ANIMALS -> Color(0xFFFFF8E1)    // Cream
            SkinType.EMOJI -> Color(0xFFFFF3E0)      // Peach
            SkinType.GEMS -> Color(0xFFE8EAF6)       // Lavender
            SkinType.FOOD -> Color(0xFFFCE4EC)       // Pink
            SkinType.SPORTS -> Color(0xFFE0F2F1)     // Teal
            SkinType.NATURE -> Color(0xFFF1F8E9)     // Green
            SkinType.SPACE -> Color(0xFFE3F2FD)      // Blue
            SkinType.HALLOWEEN -> Color(0xFFFBE9E7)  // Orange
            SkinType.CHRISTMAS -> Color(0xFFFFEBEE)  // Red
        }
    }

    /**
     * Get all background gradients (for preview)
     */
    fun getAllGradients(): Map<SkinType, SkinBackgroundGradient> {
        return SkinType.values().associateWith { getBackgroundGradient(it) }
    }
}
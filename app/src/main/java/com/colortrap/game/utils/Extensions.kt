package com.colortrap.game.utils

import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

/**
 * Kotlin Extensions cho ColorTrap
 */

// ==================== String Extensions ====================

/**
 * Capitalize first letter
 */
fun String.capitalizeFirst(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

/**
 * Remove file extension
 */
fun String.withoutExtension(): String {
    return this.substringBeforeLast('.')
}

/**
 * Get file extension
 */
fun String.getExtension(): String {
    return this.substringAfterLast('.', "")
}


// ==================== Number Extensions ====================

/**
 * Format score with commas (1000 → 1,000)
 */
fun Int.formatScore(): String {
    return "%,d".format(this)
}

/**
 * Format time with 1 decimal (3.45 → "3.4s")
 */
fun Float.formatTime(): String {
    return "%.1fs".format(this)
}

/**
 * Clamp value between min-max
 */
fun Int.clamp(min: Int, max: Int): Int {
    return this.coerceIn(min, max)
}

fun Float.clamp(min: Float, max: Float): Float {
    return this.coerceIn(min, max)
}

/**
 * Convert percentage to float (50 → 0.5)
 */
fun Int.toPercentage(): Float = this / 100f


// ==================== Color Extensions ====================

/**
 * Convert Color to hex string (#RRGGBB)
 */
fun Color.toHexString(): String {
    val red = (this.red * 255).roundToInt()
    val green = (this.green * 255).roundToInt()
    val blue = (this.blue * 255).roundToInt()
    return "#%02X%02X%02X".format(red, green, blue)
}

/**
 * Lighten color by factor (0.0 - 1.0)
 */
fun Color.lighten(factor: Float): Color {
    val f = factor.clamp(0f, 1f)
    return Color(
        red = (red + (1f - red) * f).clamp(0f, 1f),
        green = (green + (1f - green) * f).clamp(0f, 1f),
        blue = (blue + (1f - blue) * f).clamp(0f, 1f),
        alpha = alpha
    )
}

/**
 * Darken color by factor (0.0 - 1.0)
 */
fun Color.darken(factor: Float): Color {
    val f = factor.clamp(0f, 1f)
    return Color(
        red = (red * (1f - f)).clamp(0f, 1f),
        green = (green * (1f - f)).clamp(0f, 1f),
        blue = (blue * (1f - f)).clamp(0f, 1f),
        alpha = alpha
    )
}


// ==================== List Extensions ====================

/**
 * Shuffle and return new list (immutable)
 */
fun <T> List<T>.shuffled(): List<T> {
    return this.shuffled()
}

/**
 * Pick N random items
 */
fun <T> List<T>.pickRandom(count: Int): List<T> {
    return if (count >= this.size) {
        this.shuffled()
    } else {
        this.shuffled().take(count)
    }
}

/**
 * Pick 1 random item
 */
fun <T> List<T>.pickRandomOne(): T? {
    return this.randomOrNull()
}

/**
 * Safe get (return null if out of bounds)
 */
fun <T> List<T>.getOrNull(index: Int): T? {
    return if (index in indices) this[index] else null
}


// ==================== Boolean Extensions ====================

/**
 * Convert Boolean to Int (true = 1, false = 0)
 */
fun Boolean.toInt(): Int = if (this) 1 else 0


// ==================== Time Extensions ====================

/**
 * Convert milliseconds to seconds
 */
fun Long.toSeconds(): Float = this / 1000f

/**
 * Convert seconds to milliseconds
 */
fun Float.toMillis(): Long = (this * 1000).toLong()


// ==================== Grid Extensions ====================

/**
 * Calculate grid columns from size
 * 4 tiles → 2x2, 6 → 2x3, 9 → 3x3, etc.
 */
fun Int.toGridColumns(): Int {
    return when {
        this <= 4 -> 2
        this <= 6 -> 3
        this <= 9 -> 3
        else -> 4
    }
}

/**
 * Calculate grid rows from size
 */
fun Int.toGridRows(): Int {
    val cols = this.toGridColumns()
    return (this + cols - 1) / cols // Ceiling division
}
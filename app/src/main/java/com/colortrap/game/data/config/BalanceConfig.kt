package com.colortrap.game.data.config

/**
 * GAME BALANCE CONFIGURATION
 * Level progression, difficulty curves, etc.
 * Loaded from JSON but with fallback defaults
 */
object BalanceConfig {

    data class LevelRange(
        val minLevel: Int,
        val maxLevel: Int?,
        val gridSize: Int,
        val forbiddenCount: Int,
        val timeLimit: Float
    )

    // ==================== NORMAL MODE ====================

    val NORMAL_LEVELS = listOf(
        LevelRange(1, 5, 4, 1, 5.0f),
        LevelRange(6, 10, 4, 1, 4.5f),
        LevelRange(11, 20, 4, 2, 4.0f),
        LevelRange(21, 30, 5, 2, 3.8f),
        LevelRange(31, 40, 5, 3, 3.5f),
        LevelRange(41, 50, 6, 4, 3.2f),
        LevelRange(51, 60, 6, 5, 3.0f),
        LevelRange(61, 80, 8, 7, 2.5f),
        LevelRange(81, null, 10, 9, 2.0f)
    )

    // ==================== HARD MODE ====================

    val HARD_LEVELS = listOf(
        LevelRange(1, 10, 4, 2, 4.0f),
        LevelRange(11, 20, 5, 3, 3.5f),
        LevelRange(21, 30, 6, 4, 3.0f),
        LevelRange(31, 50, 6, 5, 2.8f),
        LevelRange(51, null, 8, 7, 2.0f)
    )

    // ==================== SUPER HARD MODE ====================

    val SUPER_HARD_LEVELS = listOf(
        LevelRange(1, 10, 5, 4, 3.0f),
        LevelRange(11, 20, 6, 5, 2.5f),
        LevelRange(21, 40, 8, 7, 2.0f),
        LevelRange(41, null, 10, 9, 1.5f)
    )

    // ==================== RELAX MODE ====================

    val RELAX_LEVELS = listOf(
        LevelRange(1, 10, 4, 1, 8.0f),
        LevelRange(11, 20, 4, 2, 7.0f),
        LevelRange(21, 40, 5, 2, 6.5f),
        LevelRange(41, 60, 6, 2, 6.0f),
        LevelRange(61, 100, 6, 3, 5.5f),
        LevelRange(101, null, 6, 2, 5.0f)
    )

    // ==================== HELPER FUNCTIONS ====================

    fun getLevelConfig(level: Int, levels: List<LevelRange>): LevelRange {
        return levels.find { range ->
            level >= range.minLevel && (range.maxLevel == null || level <= range.maxLevel)
        } ?: levels.last()
    }
}
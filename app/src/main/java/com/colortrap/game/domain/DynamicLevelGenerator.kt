package com.colortrap.game.domain

import android.content.Context
import android.util.Log
import com.colortrap.game.data.models.*

/**
 * DynamicLevelGenerator
 * Generates levels dynamically based on:
 * - Level number
 * - Game mode
 * - Difficulty progression
 *
 * UPDATED LOGIC:
 * ✅ Tile Grid: Always 4 colors (fixed)
 * ✅ Forbidden: 3 colors from grid (N-1) + 0-3 extra colors (not the safe one)
 * ✅ Total forbidden: Min 3, Max 6 objects
 */
class DynamicLevelGenerator(private val context: Context) {

    companion object {
        private const val TAG = "LevelGenerator"
        private const val GRID_COLOR_COUNT = 4 // Always 4 colors in grid
    }

    private val skinManager = DynamicSkinManager(context)

    /**
     * Generate a complete level
     */
    fun generateLevel(levelNumber: Int, mode: GameMode): DynamicLevel {
        // Determine difficulty
        val difficulty = determineDifficulty(levelNumber, mode)

        // Get level parameters
        val gridSize = getGridSize(levelNumber, mode)
        val extraForbiddenCount = getExtraForbiddenCount(levelNumber, mode) // 0-3
        val timeLimit = getTimeLimit(levelNumber, mode)

        // Select color groups based on difficulty
        val availableGroups = skinManager.getAvailableColorGroups()

        // Step 1: Select 4 colors for grid
        val gridColorGroups = selectGridColorGroups(availableGroups, difficulty)

        // Step 2: Generate tiles with those 4 colors
        val tiles = generateTiles(gridSize, gridColorGroups, difficulty)

        // Step 3: Select forbidden colors
        // - 3 colors from grid (N-1, where N=4)
        // - Plus 0-3 extra colors (not the safe one)
        val forbiddenTiles = selectForbiddenTiles(
            tiles,
            gridColorGroups,
            availableGroups,
            extraForbiddenCount
        )

        Log.d(TAG, "✅ Level $levelNumber generated: " +
                "difficulty=$difficulty, " +
                "gridSize=$gridSize, " +
                "gridColors=${gridColorGroups.size}, " +
                "forbidden=${forbiddenTiles.size} (3 from grid + $extraForbiddenCount extra), " +
                "time=${timeLimit}s")

        return DynamicLevel(
            levelNumber = levelNumber,
            gridSize = gridSize,
            tiles = tiles,
            forbiddenColors = forbiddenTiles,
            timeLimit = timeLimit,
            difficulty = difficulty
        )
    }

    /**
     * Determine difficulty based on level and mode
     */
    private fun determineDifficulty(level: Int, mode: GameMode): DifficultyLevel {
        return when (mode) {
            GameMode.NORMAL -> when {
                level <= 20 -> DifficultyLevel.EASY
                level <= 40 -> DifficultyLevel.MEDIUM
                level <= 60 -> DifficultyLevel.HARD
                else -> DifficultyLevel.SUPER_HARD
            }

            GameMode.HARD -> when {
                level <= 10 -> DifficultyLevel.MEDIUM
                level <= 30 -> DifficultyLevel.HARD
                else -> DifficultyLevel.SUPER_HARD
            }

            GameMode.SUPER_HARD -> when {
                level <= 10 -> DifficultyLevel.HARD
                else -> DifficultyLevel.SUPER_HARD
            }

            GameMode.RELAX -> DifficultyLevel.EASY
        }
    }

    /**
     * Get grid size based on level and mode
     */
    private fun getGridSize(level: Int, mode: GameMode): Int {
        return when (mode) {
            GameMode.NORMAL -> when {
                level <= 20 -> (8..12).random()  // More tiles
                level <= 40 -> (12..16).random()
                else -> (16..20).random()
            }

            GameMode.HARD -> when {
                level <= 10 -> (12..16).random()
                level <= 30 -> (16..20).random()
                else -> (20..24).random()
            }

            GameMode.SUPER_HARD -> when {
                level <= 10 -> (16..20).random()
                else -> (20..24).random()
            }

            GameMode.RELAX -> (8..12).random()
        }
    }

    /**
     * Get extra forbidden count (0-3) based on level and mode
     * This adds to the base 3 from grid
     */
    private fun getExtraForbiddenCount(level: Int, mode: GameMode): Int {
        return when (mode) {
            GameMode.NORMAL -> when {
                level <= 20 -> 0  // Total: 3 forbidden
                level <= 40 -> (0..1).random()  // Total: 3-4
                else -> (1..2).random()  // Total: 4-5
            }

            GameMode.HARD -> when {
                level <= 10 -> (0..1).random()  // Total: 3-4
                level <= 30 -> (1..2).random()  // Total: 4-5
                else -> (2..3).random()  // Total: 5-6
            }

            GameMode.SUPER_HARD -> when {
                level <= 10 -> (1..2).random()  // Total: 4-5
                else -> (2..3).random()  // Total: 5-6
            }

            GameMode.RELAX -> 0  // Total: 3 forbidden only
        }
    }

    /**
     * Get time limit based on level and mode
     */
    private fun getTimeLimit(level: Int, mode: GameMode): Float {
        return when (mode) {
            GameMode.NORMAL -> when {
                level <= 20 -> (4.0f..5.0f).random()
                level <= 40 -> (3.0f..4.0f).random()
                else -> (2.0f..3.0f).random()
            }

            GameMode.HARD -> when {
                level <= 10 -> (3.0f..4.0f).random()
                level <= 30 -> (2.0f..3.0f).random()
                else -> (1.5f..2.5f).random()
            }

            GameMode.SUPER_HARD -> when {
                level <= 10 -> (2.0f..3.0f).random()
                else -> (1.0f..2.0f).random()
            }

            GameMode.RELAX -> (8.0f..10.0f).random()
        }
    }

    /**
     * Select 4 color groups for grid based on difficulty
     */
    private fun selectGridColorGroups(
        availableGroups: List<String>,
        difficulty: DifficultyLevel
    ): List<String> {
        return when (difficulty) {
            DifficultyLevel.EASY -> {
                // 4 different colors (easy to distinguish)
                availableGroups.shuffled().take(GRID_COLOR_COUNT)
            }

            DifficultyLevel.MEDIUM -> {
                // 2 same + 2 different (moderate challenge)
                val primaryGroup = availableGroups.random()
                val otherGroups = availableGroups.filter { it != primaryGroup }.shuffled()
                listOf(primaryGroup, primaryGroup) + otherGroups.take(2)
            }

            DifficultyLevel.HARD -> {
                // 3 same + 1 different (harder)
                val primaryGroup = availableGroups.random()
                val otherGroup = availableGroups.filter { it != primaryGroup }.random()
                listOf(primaryGroup, primaryGroup, primaryGroup, otherGroup)
            }

            DifficultyLevel.SUPER_HARD -> {
                // All 4 same color group (very hard)
                val primaryGroup = availableGroups.random()
                List(GRID_COLOR_COUNT) { primaryGroup }
            }
        }
    }

    /**
     * Generate tiles with 4 colors
     */
    private fun generateTiles(
        gridSize: Int,
        colorGroups: List<String>,
        difficulty: DifficultyLevel
    ): List<DynamicTile> {
        val tiles = mutableListOf<DynamicTile>()

        repeat(gridSize) { index ->
            // Distribute 4 colors evenly across tiles
            val colorGroup = colorGroups[index % GRID_COLOR_COUNT]

            // Select variant index (random variant from that color group)
            val variantIndex = (0..2).random() // Assume 3 variants per color

            tiles.add(
                DynamicTile(
                    id = index,
                    colorGroup = colorGroup,
                    variantIndex = variantIndex,
                    bitmap = null // Will be loaded by ViewModel
                )
            )
        }

        return tiles.shuffled()
    }

    /**
     * Select forbidden tiles
     * - 3 colors from grid (N-1, where N=4)
     * - Plus extraCount (0-3) colors from other available groups (not the safe color)
     */
    private fun selectForbiddenTiles(
        tiles: List<DynamicTile>,
        gridColorGroups: List<String>,
        allAvailableGroups: List<String>,
        extraCount: Int
    ): List<DynamicTile> {
        val forbiddenTiles = mutableListOf<DynamicTile>()

        // Step 1: Pick 3 colors from grid (N-1, where N=4)
        val gridForbiddenGroups = gridColorGroups.distinct().shuffled().take(3)
        val safeColorGroup = gridColorGroups.distinct().firstOrNull {
            it !in gridForbiddenGroups
        }

        // Add 3 forbidden colors from grid
        gridForbiddenGroups.forEach { group ->
            val exampleTile = tiles.first { it.colorGroup == group }
            forbiddenTiles.add(
                DynamicTile(
                    id = -forbiddenTiles.size - 1,
                    colorGroup = group,
                    variantIndex = exampleTile.variantIndex,
                    bitmap = null
                )
            )
        }

        // Step 2: Add 0-3 extra colors (not in grid, not the safe color)
        if (extraCount > 0) {
            val extraGroups = allAvailableGroups
                .filter { it !in gridColorGroups && it != safeColorGroup }
                .shuffled()
                .take(extraCount)

            extraGroups.forEach { group ->
                forbiddenTiles.add(
                    DynamicTile(
                        id = -forbiddenTiles.size - 1,
                        colorGroup = group,
                        variantIndex = (0..2).random(),
                        bitmap = null
                    )
                )
            }
        }

        Log.d(TAG, "Forbidden: ${gridForbiddenGroups.size} from grid + ${extraCount} extra = ${forbiddenTiles.size} total")
        Log.d(TAG, "Safe color: $safeColorGroup")

        return forbiddenTiles
    }

    /**
     * Extension function for Float range random
     */
    private fun ClosedFloatingPointRange<Float>.random(): Float {
        return start + Math.random().toFloat() * (endInclusive - start)
    }
}
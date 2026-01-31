package com.colortrap.game.data.models

data class DynamicLevel(
    val levelNumber: Int,
    val gridSize: Int,
    val tiles: List<DynamicTile>,           // ← CHANGED from allVariants
    val forbiddenColors: List<DynamicTile>, // ← CHANGED from forbiddenVariants
    val timeLimit: Float,
    val difficulty: DifficultyLevel,
    val modifiers: List<LevelModifier> = emptyList()
)
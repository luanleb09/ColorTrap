package com.colortrap.game.data.models

data class DynamicGameState(
    val mode: GameMode = GameMode.NORMAL,
    val currentLevel: Int = 1,
    val score: Int = 0,
    val coins: Int = 0,
    val lives: Int = 3,
    val timeRemaining: Float = 5f,
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val isGameOver: Boolean = false,

    val level: DynamicLevel? = null,
    val tiles: List<DynamicTile> = emptyList(),

    val items: Map<ItemType, Int> = emptyMap(),
    val activeShield: Boolean = false,
    val slowTimeActive: Boolean = false,

    val consecutiveCorrect: Int = 0
)
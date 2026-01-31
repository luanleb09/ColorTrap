package com.colortrap.game.data.config

/**
 * CENTRALIZED GAME CONFIGURATION
 * Single source of truth for all game constants
 */
object GameConfig {

    // ==================== GAME INFO ====================
    const val GAME_NAME = "ColorTrap"
    const val GAME_VERSION = "1.0.0"
    const val MIN_ANDROID_VERSION = 24

    // ==================== GAMEPLAY CONSTANTS ====================

    object Gameplay {
        // Scoring
        const val BASE_SCORE = 10
        const val COMBO_MULTIPLIER = 5
        const val PERFECT_BONUS = 50
        const val LEVEL_COMPLETE_BONUS = 20

        // Lives (Relax mode)
        const val RELAX_MODE_LIVES = 3
        const val MAX_LIVES = 5

        // Revive
        const val MAX_REVIVE_PER_RUN = 1
        const val REVIVE_TIME_BONUS = 1.5f // seconds

        // Timer warning
        const val TIME_WARNING_THRESHOLD = 2.0f // seconds
        const val TIME_CRITICAL_THRESHOLD = 1.0f

        // Combos
        const val COMBO_THRESHOLD = 3 // consecutive correct taps
        const val MAX_COMBO = 10
    }

    // ==================== DIFFICULTY THRESHOLDS ====================

    object Difficulty {
        // When to suggest Relax mode
        const val CONSECUTIVE_FAILS_FOR_RELAX = 3

        // Fail rate targets (for analytics)
        const val NORMAL_FAIL_RATE = 0.40f
        const val HARD_FAIL_RATE = 0.70f
        const val SUPER_HARD_FAIL_RATE = 0.85f
        const val RELAX_FAIL_RATE = 0.15f

        // Grid size limits
        const val MIN_GRID_SIZE = 4
        const val MAX_GRID_SIZE = 10

        // Time limits
        const val MIN_TIME_LIMIT = 1.5f
        const val MAX_TIME_LIMIT = 8.0f
    }

    // ==================== ADS CONFIGURATION ====================

    object Ads {
        // Ad IDs (Test IDs - replace in production)
        const val INTERSTITIAL_AD_ID = "ca-app-pub-3940256099942544/1033173712"
        const val REWARDED_AD_ID = "ca-app-pub-3940256099942544/5224354917"
        const val BANNER_AD_ID = "ca-app-pub-3940256099942544/6300978111"
        const val APP_ID = "ca-app-pub-3940256099942544~3347511713"

        // Ad frequency
        const val INTERSTITIAL_FREQUENCY = 5 // Every X levels
        const val MAX_REWARD_ADS_PER_RUN = 5
        const val MAX_REWARD_ADS_PER_DAY = 10

        // Cooldowns (milliseconds)
        const val INTERSTITIAL_COOLDOWN = 60000L // 1 minute
        const val REWARDED_COOLDOWN = 30000L // 30 seconds
    }

    // ==================== ASSET PATHS ====================

    object Assets {
        const val SKINS_BASE_PATH = "skins"
        const val CONFIG_PATH = "config"
        const val AUDIO_PATH = "audio"
        const val EFFECTS_PATH = "effects"

        // Default skin
        const val DEFAULT_SKIN = "color"

        // Supported formats
        val IMAGE_FORMATS = listOf("webp", "png", "jpg", "jpeg")
        val AUDIO_FORMATS = listOf("ogg", "wav", "mp3")
    }

    // ==================== UI CONSTANTS ====================

    object UI {
        // Animation durations (milliseconds)
        const val TILE_TAP_DURATION = 100L
        const val HINT_DURATION = 300L
        const val FADE_DURATION = 200L
        const val SLIDE_DURATION = 300L

        // Grid layout
        const val GRID_PADDING_DP = 8
        const val TILE_CORNER_RADIUS_DP = 16
        const val TILE_ELEVATION_DP = 4

        // Colors
        const val BACKGROUND_DARK = 0xFF1A1A1A
        const val TOPBAR_BG = 0xFF2C2C2C
        const val CORRECT_COLOR = 0xFF4CAF50
        const val ERROR_COLOR = 0xFFD32F2F
        const val WARNING_COLOR = 0xFFFF9800
    }

    // ==================== AUDIO SETTINGS ====================

    object Audio {
        const val DEFAULT_VOLUME = 1.0f
        const val MAX_STREAMS = 5

        // Sound file names
        const val SOUND_TAP = "tap"
        const val SOUND_CORRECT = "correct"
        const val SOUND_WRONG = "wrong"
        const val SOUND_GAME_OVER = "game_over"
        const val SOUND_COUNTDOWN = "countdown"
        const val SOUND_LEVEL_UP = "level_up"
        const val SOUND_ITEM_USE = "item_use"
    }

    // ==================== SESSION LIMITS ====================

    object Session {
        const val AUTO_SAVE_INTERVAL = 30000L // 30 seconds
        const val SESSION_TIMEOUT = 300000L // 5 minutes
        const val MAX_SESSION_LENGTH = 3600000L // 1 hour
    }

    // ==================== ANALYTICS EVENTS ====================

    object Analytics {
        const val EVENT_GAME_START = "game_start"
        const val EVENT_GAME_OVER = "game_over"
        const val EVENT_LEVEL_COMPLETE = "level_complete"
        const val EVENT_ITEM_USED = "item_used"
        const val EVENT_AD_WATCHED = "ad_watched"
        const val EVENT_PURCHASE = "purchase"
        const val EVENT_REVIVE = "revive_used"
    }
}

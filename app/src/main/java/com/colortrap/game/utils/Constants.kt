package com.colortrap.game.utils

/**
 * Game constants
 */
object Constants {

    // ==================== GAME CONFIG ====================
    const val MIN_SDK = 24
    const val TARGET_SDK = 34

    // ==================== PATHS ====================
    const val ASSETS_CONFIG_PATH = "config"
    const val ASSETS_SKINS_PATH = "skins/color"

    // Config file names
    const val GAME_CONFIG_FILE = "game_config.json"
    const val BALANCE_CONFIG_FILE = "balance_config.json"
    const val SHOP_CONFIG_FILE = "shop_config.json"
    const val TEXT_CONFIG_FILE = "text_config.json"

    // ==================== GAME SETTINGS ====================

    // Grid sizes (min-max)
    const val MIN_GRID_SIZE = 4
    const val MAX_GRID_SIZE = 10

    // Forbidden colors (min-max)
    const val MIN_FORBIDDEN = 1
    const val MAX_FORBIDDEN = 9

    // Time limits (seconds)
    const val MIN_TIME = 1.5f
    const val MAX_TIME = 8f
    const val DEFAULT_TIME = 5f

    // Lives (RELAX mode)
    const val RELAX_LIVES = 3

    // Score multipliers
    const val SCORE_BASE = 100
    const val SCORE_TIME_BONUS_MULTIPLIER = 10 // Mỗi 0.1s còn lại = +10 điểm
    const val SCORE_STREAK_MULTIPLIER = 50 // Mỗi combo = +50

    // ==================== UI CONFIG ====================

    // Animation durations (ms)
    const val ANIM_TILE_CLICK = 100L
    const val ANIM_TILE_SPAWN = 300L
    const val ANIM_LEVEL_TRANSITION = 500L
    const val ANIM_GAME_OVER = 800L

    // Grid spacing
    const val GRID_PADDING_DP = 16
    const val TILE_SPACING_DP = 8

    // Top bar heights
    const val TOP_BAR_HEIGHT_DP = 80
    const val ITEM_BAR_HEIGHT_DP = 64

    // ==================== SHARED PREFERENCES KEYS ====================
    object Prefs {
        const val PREFS_NAME = "ColorTrapPrefs"

        // High scores
        const val HIGH_SCORE_NORMAL = "high_score_normal"
        const val HIGH_SCORE_HARD = "high_score_hard"
        const val HIGH_SCORE_SUPER_HARD = "high_score_super_hard"
        const val HIGH_SCORE_RELAX = "high_score_relax"

        // Settings
        const val SOUND_ENABLED = "sound_enabled"
        const val VIBRATION_ENABLED = "vibration_enabled"
        const val MUSIC_ENABLED = "music_enabled"
        const val VOLUME = "volume"

        // Player data
        const val TOTAL_GAMES = "total_games"
        const val TOTAL_SCORE = "total_score"
        const val COINS = "coins"

        // Shop items (owned)
        const val OWNED_SKINS = "owned_skins" // JSON array
        const val OWNED_ITEMS = "owned_items" // JSON array
        const val CURRENT_SKIN = "current_skin"

        // Ads
        const val ADS_REMOVED = "ads_removed"
        const val LAST_AD_TIMESTAMP = "last_ad_timestamp"
    }

    // ==================== AD CONFIG ====================
    const val AD_COOLDOWN_MS = 30_000L // 30 seconds between ads
    const val AD_SHOW_AFTER_GAMES = 3 // Show ad after every 3 games

    // ==================== DIFFICULTY THRESHOLDS ====================
    object Difficulty {
        // Level ranges for each difficulty
        const val EASY_MAX_LEVEL = 20
        const val MEDIUM_MAX_LEVEL = 40
        const val HARD_MAX_LEVEL = 60
        // 61+ = Super Hard

        // Percentage thresholds (for color distribution)
        const val EASY_SAME_GROUP_PERCENT = 0 // 0% same group as forbidden
        const val MEDIUM_SAME_GROUP_PERCENT = 50 // 50%
        const val HARD_SAME_GROUP_PERCENT = 80 // 80%
        const val SUPER_HARD_SAME_GROUP_PERCENT = 100 // 100% (except 1 safe)
    }

    // ==================== SHOP CONFIG ====================
    const val STARTING_COINS = 0
    const val COIN_REWARD_PER_GAME = 10
    const val COIN_REWARD_HIGH_SCORE = 50

    // Item prices (default)
    const val ITEM_SLOW_TIME_PRICE = 50
    const val ITEM_SKIP_LEVEL_PRICE = 30
    const val ITEM_REVIVE_PRICE = 100

    // ==================== DEBUG ====================
    const val DEBUG_MODE = true
    const val LOG_ASSET_SCANNING = true
    const val LOG_LEVEL_GENERATION = true
}
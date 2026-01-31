package com.colortrap.game.data.config

/**
 * CENTRALIZED TEXT CONFIGURATION
 * All user-facing text and localization strings
 */
object TextConfig {

    // ==================== GAME MODES ====================

    object GameModes {
        const val NORMAL = "Normal"
        const val HARD = "Hard"
        const val SUPER_HARD = "Super Hard"
        const val RELAX = "Relax"
        const val ENDLESS = "Endless"
        const val DAILY_CHALLENGE = "Daily Challenge"

        // Descriptions
        const val NORMAL_DESC = "Balanced difficulty for most players"
        const val HARD_DESC = "Challenging gameplay for experienced players"
        const val SUPER_HARD_DESC = "Extreme difficulty - Are you ready?"
        const val RELAX_DESC = "Take it easy - More time, multiple lives"
        const val ENDLESS_DESC = "How far can you go?"
        const val DAILY_DESC = "Complete today's unique challenge"
    }

    // ==================== UI LABELS ====================

    object Labels {
        const val SCORE = "Score"
        const val LEVEL = "Level"
        const val TIME = "Time"
        const val LIVES = "Lives"
        const val COINS = "Coins"
        const val BEST = "Best"
        const val COMBO = "Combo"

        // Buttons
        const val PLAY = "Play"
        const val RETRY = "Retry"
        const val CONTINUE = "Continue"
        const val QUIT = "Quit"
        const val SHOP = "Shop"
        const val SETTINGS = "Settings"
        const val BACK = "Back"
        const val CLOSE = "Close"
        const val BUY = "Buy"
        const val CLAIM = "Claim"
        const val WATCH_AD = "Watch Ad"
        const val NO_THANKS = "No Thanks"
    }

    // ==================== INSTRUCTIONS ====================

    object Instructions {
        const val MAIN = "❌ DON'T TAP THESE"
        const val TAP_DIFFERENT = "Tap the different color!"
        const val TAP_SAFE = "Tap a safe color"
        const val HURRY = "Hurry up!"
        const val GOOD_JOB = "Good job!"
        const val PERFECT = "Perfect!"
        const val ALMOST = "So close!"
    }

    // ==================== MESSAGES ====================

    object Messages {
        const val GAME_OVER = "GAME OVER"
        const val LEVEL_COMPLETE = "Level Complete!"
        const val NEW_HIGH_SCORE = "🎉 New High Score!"
        const val COMBO_STARTED = "Combo x"
        const val TIME_RUNNING_OUT = "⏰ Time Running Out!"

        // Relax mode
        const val LOST_LIFE = "Lost 1 life!"
        const val LAST_LIFE = "⚠️ Last life!"

        // Revive
        const val REVIVE_TITLE = "Continue Playing?"
        const val REVIVE_MESSAGE = "Watch an ad to get +1.5 seconds"

        // Items
        const val ITEM_USED = "Item activated!"
        const val NO_ITEMS = "No items available"
        const val ITEM_PURCHASED = "Purchase successful!"

        // Shop
        const val NOT_ENOUGH_COINS = "Not enough coins!"
        const val PURCHASE_SUCCESS = "Purchase successful!"
        const val DAILY_CLAIMED = "Daily reward claimed!"
        const val DAILY_ALREADY_CLAIMED = "Come back tomorrow!"

        // Errors
        const val ERROR_GENERIC = "Something went wrong"
        const val ERROR_NO_CONNECTION = "No internet connection"
        const val ERROR_AD_NOT_READY = "Ad not ready, try again later"
    }

    // ==================== TIPS ====================

    object Tips {
        val ALL_TIPS = listOf(
            "💡 Look for the most different color",
            "⏱️ Don't rush - accuracy over speed",
            "🎯 Use hints on harder levels",
            "🛡️ Save shields for Super Hard mode",
            "🔄 Shuffle when colors are too similar",
            "👀 Focus on the forbidden colors first",
            "🎮 Practice in Relax mode",
            "⭐ Combos give bonus points!",
            "📊 Check your progress in stats",
            "🎁 Don't forget daily rewards!"
        )

        fun getRandomTip(): String = ALL_TIPS.random()
    }

    // ==================== ACHIEVEMENTS ====================

    object Achievements {
        const val FIRST_WIN = "First Victory"
        const val FIRST_WIN_DESC = "Complete your first level"

        const val SPEED_DEMON = "Speed Demon"
        const val SPEED_DEMON_DESC = "Complete a level in under 2 seconds"

        const val COMBO_MASTER = "Combo Master"
        const val COMBO_MASTER_DESC = "Reach a 10x combo"

        const val SURVIVOR = "Survivor"
        const val SURVIVOR_DESC = "Reach level 50"

        const val PERFECTIONIST = "Perfectionist"
        const val PERFECTIONIST_DESC = "Complete 10 levels without mistakes"

        const val COIN_COLLECTOR = "Coin Collector"
        const val COIN_COLLECTOR_DESC = "Earn 10,000 coins"
    }

    // ==================== SETTINGS ====================

    object Settings {
        const val TITLE = "Settings"

        const val SOUND = "Sound"
        const val SOUND_DESC = "Game sound effects"

        const val VIBRATION = "Vibration"
        const val VIBRATION_DESC = "Haptic feedback"

        const val NOTIFICATIONS = "Notifications"
        const val NOTIFICATIONS_DESC = "Daily reminders"

        const val ABOUT = "About"
        const val PRIVACY = "Privacy Policy"
        const val TERMS = "Terms of Service"
        const val RATE = "Rate this app"
        const val SHARE = "Share with friends"

        const val RESET_PROGRESS = "Reset Progress"
        const val RESET_CONFIRM = "Are you sure? This cannot be undone!"
    }
}
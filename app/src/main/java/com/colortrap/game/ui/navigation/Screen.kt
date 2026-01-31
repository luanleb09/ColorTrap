package com.colortrap.game.ui.navigation

/**
 * Sealed class định nghĩa tất cả screens trong app
 * Dùng cho Navigation routes
 */
sealed class Screen(val route: String) {

    /**
     * Splash Screen
     * - Hiển thị logo, loading assets
     * - Auto navigate sang Menu sau 2s
     */
    object Splash : Screen("splash")

    /**
     * Main Menu Screen
     * - Chọn game mode (NORMAL, HARD, SUPER HARD, RELAX)
     * - Settings, Shop buttons
     */
    object MainMenu : Screen("main_menu")

    /**
     * Game Screen
     * - Main gameplay
     * - Route có parameter: gameMode
     */
    object Game : Screen("game/{gameMode}") {
        fun createRoute(gameMode: String) = "game/$gameMode"
    }

    /**
     * Game Over Screen
     * - Hiển thị score, level đạt được
     * - Buttons: Replay, Main Menu, Share
     * - Route có parameters: score, level, gameMode
     */
    object GameOver : Screen("game_over/{score}/{level}/{gameMode}") {
        fun createRoute(score: Int, level: Int, gameMode: String) =
            "game_over/$score/$level/$gameMode"
    }

    /**
     * Settings Screen
     * - Sound, Vibration, Music toggles
     * - Volume slider
     * - Reset progress
     */
    object Settings : Screen("settings")

    /**
     * Shop Screen
     * - Mua skins
     * - Mua items (slow time, skip, revive)
     * - Hiển thị coins
     */
    object Shop : Screen("shop")

    companion object {
        /**
         * Get all screen routes (for navigation graph)
         */
        fun getAllRoutes(): List<String> = listOf(
            Splash.route,
            MainMenu.route,
            Game.route,
            GameOver.route,
            Settings.route,
            Shop.route
        )
    }
}
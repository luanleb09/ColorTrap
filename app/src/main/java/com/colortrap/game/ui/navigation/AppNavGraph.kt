package com.colortrap.game.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.colortrap.game.ui.screens.game.DynamicGameScreen
import com.colortrap.game.ui.screens.gameover.GameOverScreen
import com.colortrap.game.ui.screens.menu.MainMenuScreen
import com.colortrap.game.ui.screens.settings.SettingsScreen
import com.colortrap.game.ui.screens.shop.ShopScreen
import com.colortrap.game.ui.screens.splash.SplashScreen

/**
 * App Navigation Graph
 * Định nghĩa tất cả routes và transitions
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier,
        enterTransition = { defaultEnterTransition() },
        exitTransition = { defaultExitTransition() },
        popEnterTransition = { defaultPopEnterTransition() },
        popExitTransition = { defaultPopExitTransition() }
    ) {

        // ==================== SPLASH SCREEN ====================
        composable(
            route = Screen.Splash.route,
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(300)) }
        ) {
            SplashScreen(
                onNavigateToMenu = {
                    navController.navigate(Screen.MainMenu.route) {
                        // Remove splash from back stack
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // ==================== MAIN MENU SCREEN ====================
        composable(
            route = Screen.MainMenu.route,
            enterTransition = { slideInHorizontally(tween(400)) { it } + fadeIn(tween(400)) },
            exitTransition = { slideOutHorizontally(tween(400)) { -it } + fadeOut(tween(400)) }
        ) {
            MainMenuScreen(
                onPlayGame = { gameMode ->
                    navController.navigate(Screen.Game.createRoute(gameMode))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToShop = {
                    navController.navigate(Screen.Shop.route)
                }
            )
        }

        // ==================== GAME SCREEN ====================
        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument("gameMode") {
                    type = NavType.StringType
                }
            ),
            enterTransition = {
                slideInVertically(tween(400)) { it } + fadeIn(tween(400))
            },
            exitTransition = {
                slideOutVertically(tween(400)) { it } + fadeOut(tween(400))
            }
        ) { backStackEntry ->
            val gameMode = backStackEntry.arguments?.getString("gameMode") ?: "NORMAL"

            DynamicGameScreen(
                gameMode = gameMode,
                onNavigateToGameOver = { score, level ->
                    navController.navigate(
                        Screen.GameOver.createRoute(score, level, gameMode)
                    ) {
                        // Remove game screen from back stack
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ==================== GAME OVER SCREEN ====================
        composable(
            route = Screen.GameOver.route,
            arguments = listOf(
                navArgument("score") {
                    type = NavType.IntType
                },
                navArgument("level") {
                    type = NavType.IntType
                },
                navArgument("gameMode") {
                    type = NavType.StringType
                }
            ),
            enterTransition = {
                scaleIn(tween(400), initialScale = 0.8f) + fadeIn(tween(400))
            },
            exitTransition = {
                scaleOut(tween(400), targetScale = 0.8f) + fadeOut(tween(400))
            }
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val level = backStackEntry.arguments?.getInt("level") ?: 0
            val gameMode = backStackEntry.arguments?.getString("gameMode") ?: "NORMAL"

            GameOverScreen(
                score = score,
                level = level,
                gameMode = gameMode,
                onReplay = {
                    // Navigate back to game with same mode
                    navController.navigate(Screen.Game.createRoute(gameMode)) {
                        popUpTo(Screen.MainMenu.route)
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Screen.MainMenu.route) {
                        // Clear back stack to menu
                        popUpTo(Screen.MainMenu.route) { inclusive = true }
                    }
                }
            )
        }

        // ==================== SETTINGS SCREEN ====================
        composable(
            route = Screen.Settings.route,
            enterTransition = {
                slideInHorizontally(tween(400)) { it } + fadeIn(tween(400))
            },
            exitTransition = {
                slideOutHorizontally(tween(400)) { it } + fadeOut(tween(400))
            },
            popEnterTransition = {
                slideInHorizontally(tween(400)) { -it } + fadeIn(tween(400))
            },
            popExitTransition = {
                slideOutHorizontally(tween(400)) { it } + fadeOut(tween(400))
            }
        ) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ==================== SHOP SCREEN ====================
        composable(
            route = Screen.Shop.route,
            enterTransition = {
                slideInHorizontally(tween(400)) { it } + fadeIn(tween(400))
            },
            exitTransition = {
                slideOutHorizontally(tween(400)) { it } + fadeOut(tween(400))
            },
            popEnterTransition = {
                slideInHorizontally(tween(400)) { -it } + fadeIn(tween(400))
            },
            popExitTransition = {
                slideOutHorizontally(tween(400)) { it } + fadeOut(tween(400))
            }
        ) {
            ShopScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

// ==================== DEFAULT TRANSITIONS ====================

/**
 * Default enter transition (slide from right + fade in)
 */
private fun defaultEnterTransition(): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(400),
        initialOffsetX = { it }
    ) + fadeIn(tween(400))
}

/**
 * Default exit transition (slide to left + fade out)
 */
private fun defaultExitTransition(): ExitTransition {
    return slideOutHorizontally(
        animationSpec = tween(400),
        targetOffsetX = { -it / 3 }
    ) + fadeOut(tween(400))
}

/**
 * Default pop enter transition (slide from left + fade in)
 */
private fun defaultPopEnterTransition(): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(400),
        initialOffsetX = { -it / 3 }
    ) + fadeIn(tween(400))
}

/**
 * Default pop exit transition (slide to right + fade out)
 */
private fun defaultPopExitTransition(): ExitTransition {
    return slideOutHorizontally(
        animationSpec = tween(400),
        targetOffsetX = { it }
    ) + fadeOut(tween(400))
}


// ==================== NAVIGATION HELPER EXTENSIONS ====================

/**
 * Extension functions cho NavController
 * Giúp navigate dễ dàng hơn
 */

/**
 * Navigate to Game với gameMode
 */
fun NavHostController.navigateToGame(gameMode: String) {
    navigate(Screen.Game.createRoute(gameMode))
}

/**
 * Navigate to GameOver với score, level, mode
 */
fun NavHostController.navigateToGameOver(score: Int, level: Int, gameMode: String) {
    navigate(Screen.GameOver.createRoute(score, level, gameMode)) {
        // Remove game from back stack
        popUpTo(Screen.Game.route) { inclusive = true }
    }
}

/**
 * Navigate to Menu (clear back stack)
 */
fun NavHostController.navigateToMenuClearBackStack() {
    navigate(Screen.MainMenu.route) {
        popUpTo(0) { inclusive = true }
    }
}

/**
 * Navigate to Settings
 */
fun NavHostController.navigateToSettings() {
    navigate(Screen.Settings.route)
}

/**
 * Navigate to Shop
 */
fun NavHostController.navigateToShop() {
    navigate(Screen.Shop.route)
}
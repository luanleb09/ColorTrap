package com.colortrap.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.colortrap.game.ui.navigation.AppNavGraph
import com.colortrap.game.ui.theme.ColorTrapTheme

/**
 * MainActivity - Entry point của ColorTrap
 * - Setup Compose
 * - Setup Navigation
 * - Apply Theme
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ColorTrapApp()
        }
    }
}

/**
 * Main App Composable
 */
@Composable
fun ColorTrapApp() {
    ColorTrapTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()

            AppNavGraph(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
package com.colortrap.game

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay

/**
 * SplashActivity
 * Hiển thị ziny_studio.png full screen với background đen
 * Tự động chuyển sang MainActivity sau 1.5 giây
 *
 * Đây là màn hình đầu tiên khi mở app (LAUNCHER activity)
 * Sau đó sẽ chuyển sang MainActivity -> LoadingScreen -> Game
 *
 * UPDATED: Modern back press handling with OnBackPressedDispatcher
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Modern way to handle back press - disable back button
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing - prevent going back to splash
            }
        })

        setContent {
            // Sử dụng SplashTheme với background đen
            com.colortrap.game.ui.theme.SplashTheme {
                SplashContent(
                    onTimeout = {
                        // Chuyển sang MainActivity
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Đóng SplashActivity để không quay lại được
                    }
                )
            }
        }
    }
}

/**
 * Splash Screen Content
 * - Background đen full screen
 * - Ảnh ziny_studio.png scale vừa màn hình (ContentScale.Fit)
 * - Tự động chuyển màn sau 1.5 giây
 */
@Composable
private fun SplashContent(onTimeout: () -> Unit) {

    // Tự động chuyển màn sau 1.5 giây
    LaunchedEffect(Unit) {
        delay(1500L) // 1.5 giây
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black), // Background đen
        contentAlignment = Alignment.Center
    ) {
        // Hiển thị ảnh ziny_studio.png
        Image(
            painter = painterResource(id = R.drawable.ziny_studio),
            contentDescription = "Ziny Studio Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit // Scale ảnh vừa màn hình, giữ tỷ lệ gốc
            // Các options khác:
            // ContentScale.Crop - Fill màn hình, crop phần thừa
            // ContentScale.FillHeight - Fill theo chiều cao
            // ContentScale.FillWidth - Fill theo chiều rộng
        )
    }
}
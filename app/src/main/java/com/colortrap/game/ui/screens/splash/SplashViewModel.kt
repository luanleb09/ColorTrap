package com.colortrap.game.ui.screens.splash

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.colortrap.game.domain.ConfigManager
import com.colortrap.game.domain.DynamicSkinManager
import com.colortrap.game.utils.DynamicAssetScanner
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * SplashViewModel
 * - Load configs từ assets
 * - Load skins (scan color groups)
 * - Preload assets nếu cần
 *
 * FIXED:
 * ✅ Removed gameConfig.modes reference
 * ✅ Simplified config loading
 */
class SplashViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "SplashViewModel"
        private const val MIN_SPLASH_DURATION = 2000L // 2 seconds
    }

    // State
    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Loading("Initializing..."))
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    // Managers
    private val context = application.applicationContext
    private val configManager = ConfigManager(context)
    private val assetScanner = DynamicAssetScanner(context)
    private lateinit var skinManager: DynamicSkinManager

    init {
        loadAssets()
    }

    /**
     * Load tất cả assets cần thiết
     */
    private fun loadAssets() {
        viewModelScope.launch {
            try {
                val startTime = System.currentTimeMillis()

                // Step 1: Load Configs (stub for now)
                _loadingState.value = LoadingState.Loading("Loading configs...")
                delay(300)
                loadConfigs()

                // Step 2: Scan Color Groups
                _loadingState.value = LoadingState.Loading("Scanning color groups...")
                delay(300)
                scanColorGroups()

                // Step 3: Initialize SkinManager
                _loadingState.value = LoadingState.Loading("Initializing skins...")
                delay(300)
                initializeSkinManager()

                // Step 4: Preload essential assets (optional)
                _loadingState.value = LoadingState.Loading("Preparing game...")
                delay(300)
                preloadEssentialAssets()

                // Ensure minimum splash duration
                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed < MIN_SPLASH_DURATION) {
                    delay(MIN_SPLASH_DURATION - elapsed)
                }

                // Success!
                _loadingState.value = LoadingState.Success
                Log.d(TAG, "✅ All assets loaded successfully!")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error loading assets: ${e.message}")
                _loadingState.value = LoadingState.Error("Failed to load game assets")
            }
        }
    }

    /**
     * Load game configs
     * NOTE: Config files are currently stubs (not implemented)
     */
    private suspend fun loadConfigs() {
        try {
            // ConfigManager methods return stub data for now
            // No actual JSON parsing happens
            val gameConfig = configManager.loadGameConfig()
            val balanceConfig = configManager.loadBalanceConfig()

            // gameConfig is a stub object, no .modes property
            Log.d(TAG, "✅ Loaded configs (stub implementation)")

        } catch (e: Exception) {
            Log.e(TAG, "⚠️ Config loading skipped (stub): ${e.message}")
            // Don't throw - configs are optional stubs
        }
    }

    /**
     * Scan color groups từ assets/skins/color/
     */
    private suspend fun scanColorGroups() {
        try {
            val colorGroups = assetScanner.scanColorGroups()

            if (colorGroups.isEmpty()) {
                throw Exception("No color groups found in assets!")
            }

            Log.d(TAG, "✅ Found ${colorGroups.size} color groups: $colorGroups")

            // Scan variants cho mỗi group
            colorGroups.forEach { groupName ->
                val variants = assetScanner.scanColorVariants(groupName)
                Log.d(TAG, "  - $groupName: ${variants.size} variants")
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Color group scanning failed: ${e.message}")
            throw e
        }
    }

    /**
     * Initialize DynamicSkinManager
     */
    private suspend fun initializeSkinManager() {
        try {
            skinManager = DynamicSkinManager(context)
            val colorGroups = skinManager.getAvailableColorGroups()

            Log.d(TAG, "✅ SkinManager initialized with ${colorGroups.size} groups")

        } catch (e: Exception) {
            Log.e(TAG, "❌ SkinManager initialization failed: ${e.message}")
            throw e
        }
    }

    /**
     * Preload essential assets (menu backgrounds, sounds...)
     * Optional: Để tăng tốc menu screen
     */
    private suspend fun preloadEssentialAssets() {
        try {
            // TODO: Preload menu background, button images, etc.
            // val assetLoader = AssetLoader(context)
            // assetLoader.preloadBitmaps(listOf(...))

            Log.d(TAG, "✅ Essential assets preloaded (stub)")

        } catch (e: Exception) {
            Log.e(TAG, "⚠️ Preload failed (non-critical): ${e.message}")
            // Don't throw - this is optional
        }
    }

    /**
     * Loading State sealed class
     */
    sealed class LoadingState {
        data class Loading(val message: String) : LoadingState()
        object Success : LoadingState()
        data class Error(val message: String) : LoadingState()
    }
}
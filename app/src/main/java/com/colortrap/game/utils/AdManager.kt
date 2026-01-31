package com.colortrap.game.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.colortrap.game.data.local.PreferencesManager

/**
 * AdManager
 * Manages all ad types:
 * - Reward Ads (watch to skip level)
 * - Interstitial Ads (after 3 losses)
 * - Banner Ads (menu screen)
 *
 * NOTE: This is a STUB implementation for development
 * Replace with real AdMob integration before production
 */
class AdManager(private val context: Context) {

    companion object {
        private const val TAG = "AdManager"

        // Test Ad Unit IDs (replace with real ones)
        private const val REWARD_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917" // Test
        private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712" // Test
        private const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111" // Test
    }

    private val prefsManager = PreferencesManager(context)

    // Ad instances (stub - will be real AdMob objects)
    private var rewardAd: Any? = null
    private var interstitialAd: Any? = null

    /**
     * Initialize ads
     */
    fun initialize() {
        // TODO: Initialize AdMob SDK
        // MobileAds.initialize(context) {}

        Log.d(TAG, "✅ AdManager initialized (STUB)")
    }

    // ==================== REWARD AD ====================

    /**
     * Load reward ad
     */
    fun loadRewardAd() {
        if (prefsManager.getAdsRemoved()) {
            Log.d(TAG, "⚠️ Ads removed - skipping reward ad load")
            return
        }

        // TODO: Load real reward ad
        // val adRequest = AdRequest.Builder().build()
        // RewardedAd.load(context, REWARD_AD_UNIT_ID, adRequest, object : RewardedAdLoadCallback() { ... })

        Log.d(TAG, "📺 Loading reward ad (STUB)")
        rewardAd = "STUB_REWARD_AD" // Simulate loaded ad
    }

    /**
     * Check if reward ad is ready
     */
    fun isRewardAdReady(): Boolean {
        if (prefsManager.getAdsRemoved()) return false

        // TODO: Check real ad status
        // return rewardAd != null

        val isReady = rewardAd != null
        Log.d(TAG, "🎬 Reward ad ready: $isReady")
        return isReady
    }

    /**
     * Show reward ad
     * @param activity - Activity context for showing ad
     * @param onAdWatched - Callback when user watches full ad
     * @param onAdFailed - Callback when ad fails or is dismissed early
     */
    fun showRewardAd(
        activity: Activity,
        onAdWatched: () -> Unit,
        onAdFailed: () -> Unit
    ) {
        if (prefsManager.getAdsRemoved()) {
            Log.d(TAG, "⚠️ Ads removed - skipping reward ad")
            onAdFailed()
            return
        }

        if (!isRewardAdReady()) {
            Log.e(TAG, "❌ Reward ad not ready")
            onAdFailed()
            return
        }

        // TODO: Show real reward ad
        // (rewardAd as? RewardedAd)?.show(activity) { rewardItem ->
        //     Log.d(TAG, "✅ User earned reward: ${rewardItem.amount}")
        //     onAdWatched()
        //     rewardAd = null
        //     loadRewardAd() // Preload next ad
        // }

        // STUB: Simulate watching ad
        Log.d(TAG, "🎬 Showing reward ad (STUB - auto-complete in 2s)")

        // Simulate ad completion after 2 seconds
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            Log.d(TAG, "✅ Reward ad watched (STUB)")
            onAdWatched()
            rewardAd = null
            loadRewardAd() // Preload next
        }, 2000)
    }

    // ==================== INTERSTITIAL AD ====================

    /**
     * Load interstitial ad
     */
    fun loadInterstitialAd() {
        if (prefsManager.getAdsRemoved()) {
            Log.d(TAG, "⚠️ Ads removed - skipping interstitial ad load")
            return
        }

        // TODO: Load real interstitial ad
        // val adRequest = AdRequest.Builder().build()
        // InterstitialAd.load(context, INTERSTITIAL_AD_UNIT_ID, adRequest, object : InterstitialAdLoadCallback() { ... })

        Log.d(TAG, "📺 Loading interstitial ad (STUB)")
        interstitialAd = "STUB_INTERSTITIAL_AD"
    }

    /**
     * Check if interstitial ad is ready
     */
    fun isInterstitialAdReady(): Boolean {
        if (prefsManager.getAdsRemoved()) return false

        val isReady = interstitialAd != null
        Log.d(TAG, "📺 Interstitial ad ready: $isReady")
        return isReady
    }

    /**
     * Show interstitial ad (after 3 losses)
     * @param activity - Activity context
     * @param onAdClosed - Callback when ad is dismissed
     */
    fun showInterstitialAd(
        activity: Activity,
        onAdClosed: () -> Unit
    ) {
        if (prefsManager.getAdsRemoved()) {
            Log.d(TAG, "⚠️ Ads removed - skipping interstitial ad")
            onAdClosed()
            return
        }

        if (!isInterstitialAdReady()) {
            Log.e(TAG, "❌ Interstitial ad not ready")
            onAdClosed()
            return
        }

        // TODO: Show real interstitial ad
        // (interstitialAd as? InterstitialAd)?.show(activity)
        // interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
        //     override fun onAdDismissedFullScreenContent() {
        //         interstitialAd = null
        //         loadInterstitialAd()
        //         onAdClosed()
        //     }
        // }

        // STUB: Simulate showing ad
        Log.d(TAG, "📺 Showing interstitial ad (STUB - auto-close in 3s)")

        // Simulate ad close after 3 seconds
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            Log.d(TAG, "✅ Interstitial ad closed (STUB)")
            interstitialAd = null
            loadInterstitialAd()
            onAdClosed()
        }, 3000)
    }

    // ==================== BANNER AD ====================

    /**
     * Check if banner ads should be shown
     */
    fun shouldShowBannerAd(): Boolean {
        return !prefsManager.getAdsRemoved()
    }

    // ==================== CLEANUP ====================

    /**
     * Release ad resources
     */
    fun release() {
        rewardAd = null
        interstitialAd = null
        Log.d(TAG, "🗑️ AdManager released")
    }
}

// ==================== AD REWARD CALLBACK ====================

/**
 * Callback interface for reward ad results
 */
interface RewardAdCallback {
    fun onAdWatched()
    fun onAdFailed()
    fun onAdNotAvailable()
}

/**
 * Callback interface for interstitial ad results
 */
interface InterstitialAdCallback {
    fun onAdClosed()
    fun onAdFailed()
}
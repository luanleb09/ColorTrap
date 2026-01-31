package com.colortrap.game.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.colortrap.game.data.models.GameMode
import com.colortrap.game.utils.Constants
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

/**
 * PreferencesManager - Quản lý tất cả SharedPreferences
 *
 * QUẢN LÝ:
 * - High scores cho 4 game modes
 * - Coins & items
 * - Settings (sound, vibration, music, volume)
 * - Shop data (owned skins, current skin)
 * - Stats (total games, total score)
 * - Tutorial preferences (NEW!)
 */
class PreferencesManager(context: Context) {

    companion object {
        private const val TAG = "PreferencesManager"

        // Tutorial constants (NEW!)
        private const val KEY_TUTORIAL_DONT_SHOW_DATE = "tutorial_dont_show_date"
        private const val KEY_TUTORIAL_CHECKBOX_STATE = "tutorial_checkbox_state"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.Prefs.PREFS_NAME,
        Context.MODE_PRIVATE
    )

    // ==================== HIGH SCORES ====================

    fun getHighScore(mode: GameMode): Int {
        val key = when (mode) {
            GameMode.NORMAL -> Constants.Prefs.HIGH_SCORE_NORMAL
            GameMode.HARD -> Constants.Prefs.HIGH_SCORE_HARD
            GameMode.SUPER_HARD -> Constants.Prefs.HIGH_SCORE_SUPER_HARD
            GameMode.RELAX -> Constants.Prefs.HIGH_SCORE_RELAX
        }
        return prefs.getInt(key, 0)
    }

    fun saveHighScore(mode: GameMode, score: Int) {
        val key = when (mode) {
            GameMode.NORMAL -> Constants.Prefs.HIGH_SCORE_NORMAL
            GameMode.HARD -> Constants.Prefs.HIGH_SCORE_HARD
            GameMode.SUPER_HARD -> Constants.Prefs.HIGH_SCORE_SUPER_HARD
            GameMode.RELAX -> Constants.Prefs.HIGH_SCORE_RELAX
        }

        val currentHighScore = prefs.getInt(key, 0)
        if (score > currentHighScore) {
            prefs.edit().putInt(key, score).apply()
            Log.d(TAG, "✅ New high score for $mode: $score (previous: $currentHighScore)")
        } else {
            Log.d(TAG, "Score $score not higher than current high score $currentHighScore for $mode")
        }
    }

    // ==================== COINS ====================

    fun getCoins(): Int {
        return prefs.getInt(Constants.Prefs.COINS, Constants.STARTING_COINS)
    }

    fun addCoins(amount: Int) {
        val current = getCoins()
        val newAmount = (current + amount).coerceAtLeast(0)
        prefs.edit().putInt(Constants.Prefs.COINS, newAmount).apply()
        Log.d(TAG, "💰 Coins: $current → $newAmount (+$amount)")
    }

    fun spendCoins(amount: Int): Boolean {
        val current = getCoins()
        return if (current >= amount) {
            prefs.edit().putInt(Constants.Prefs.COINS, current - amount).apply()
            Log.d(TAG, "💸 Spent $amount coins: $current → ${current - amount}")
            true
        } else {
            Log.d(TAG, "⚠️ Not enough coins! Have: $current, Need: $amount")
            false
        }
    }

    fun setCoins(amount: Int) {
        prefs.edit().putInt(Constants.Prefs.COINS, amount.coerceAtLeast(0)).apply()
        Log.d(TAG, "💰 Coins set to: $amount")
    }

    // ==================== ITEMS (POWER-UPS) ====================

    fun getItemCount(itemType: String): Int {
        return prefs.getInt("item_$itemType", 0)
    }

    fun addItem(itemType: String, count: Int = 1) {
        val current = getItemCount(itemType)
        val newCount = current + count
        prefs.edit().putInt("item_$itemType", newCount).apply()
        Log.d(TAG, "🎁 Item '$itemType': $current → $newCount (+$count)")
    }

    fun useItem(itemType: String): Boolean {
        val current = getItemCount(itemType)
        return if (current > 0) {
            prefs.edit().putInt("item_$itemType", current - 1).apply()
            Log.d(TAG, "✅ Used item '$itemType': $current → ${current - 1}")
            true
        } else {
            Log.d(TAG, "⚠️ No '$itemType' items available")
            false
        }
    }

    fun setItemCount(itemType: String, count: Int) {
        prefs.edit().putInt("item_$itemType", count.coerceAtLeast(0)).apply()
        Log.d(TAG, "🎁 Item '$itemType' set to: $count")
    }

    // ==================== SETTINGS ====================

    fun getSoundEnabled(): Boolean {
        return prefs.getBoolean(Constants.Prefs.SOUND_ENABLED, true)
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(Constants.Prefs.SOUND_ENABLED, enabled).apply()
        Log.d(TAG, "🔊 Sound: $enabled")
    }

    fun getVibrationEnabled(): Boolean {
        return prefs.getBoolean(Constants.Prefs.VIBRATION_ENABLED, true)
    }

    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(Constants.Prefs.VIBRATION_ENABLED, enabled).apply()
        Log.d(TAG, "📳 Vibration: $enabled")
    }

    fun getMusicEnabled(): Boolean {
        return prefs.getBoolean(Constants.Prefs.MUSIC_ENABLED, false)
    }

    fun setMusicEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(Constants.Prefs.MUSIC_ENABLED, enabled).apply()
        Log.d(TAG, "🎵 Music: $enabled")
    }

    fun getVolume(): Float {
        return prefs.getFloat(Constants.Prefs.VOLUME, 0.7f)
    }

    fun setVolume(volume: Float) {
        val clampedVolume = volume.coerceIn(0f, 1f)
        prefs.edit().putFloat(Constants.Prefs.VOLUME, clampedVolume).apply()
        Log.d(TAG, "🔈 Volume: ${(clampedVolume * 100).toInt()}%")
    }

    // ==================== SHOP - SKINS ====================

    fun getCurrentSkin(): String {
        return prefs.getString(Constants.Prefs.CURRENT_SKIN, "default") ?: "default"
    }

    fun setCurrentSkin(skinId: String) {
        prefs.edit().putString(Constants.Prefs.CURRENT_SKIN, skinId).apply()
        Log.d(TAG, "🎨 Current skin: $skinId")
    }

    fun getOwnedSkins(): List<String> {
        val json = prefs.getString(Constants.Prefs.OWNED_SKINS, "[]") ?: "[]"
        return try {
            JSONArray(json).let { array ->
                List(array.length()) { i -> array.getString(i) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error parsing owned skins: ${e.message}")
            emptyList()
        }
    }

    fun addOwnedSkin(skinId: String) {
        val current = getOwnedSkins().toMutableList()
        if (!current.contains(skinId)) {
            current.add(skinId)
            val json = JSONArray(current).toString()
            prefs.edit().putString(Constants.Prefs.OWNED_SKINS, json).apply()
            Log.d(TAG, "✅ Added owned skin: $skinId")
        } else {
            Log.d(TAG, "⚠️ Skin already owned: $skinId")
        }
    }

    fun isSkinOwned(skinId: String): Boolean {
        return getOwnedSkins().contains(skinId)
    }

    // ==================== SHOP - ITEMS ====================

    fun getOwnedItems(): List<String> {
        val json = prefs.getString(Constants.Prefs.OWNED_ITEMS, "[]") ?: "[]"
        return try {
            JSONArray(json).let { array ->
                List(array.length()) { i -> array.getString(i) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error parsing owned items: ${e.message}")
            emptyList()
        }
    }

    // ==================== STATS ====================

    fun getTotalGames(): Int {
        return prefs.getInt(Constants.Prefs.TOTAL_GAMES, 0)
    }

    fun incrementTotalGames() {
        val current = getTotalGames()
        prefs.edit().putInt(Constants.Prefs.TOTAL_GAMES, current + 1).apply()
        Log.d(TAG, "🎮 Total games: ${current + 1}")
    }

    fun getTotalScore(): Int {
        return prefs.getInt(Constants.Prefs.TOTAL_SCORE, 0)
    }

    fun addToTotalScore(score: Int) {
        val current = getTotalScore()
        prefs.edit().putInt(Constants.Prefs.TOTAL_SCORE, current + score).apply()
        Log.d(TAG, "📊 Total score: $current → ${current + score}")
    }

    // ==================== ADS ====================

    fun getAdsRemoved(): Boolean {
        return prefs.getBoolean(Constants.Prefs.ADS_REMOVED, false)
    }

    fun setAdsRemoved(removed: Boolean) {
        prefs.edit().putBoolean(Constants.Prefs.ADS_REMOVED, removed).apply()
        Log.d(TAG, "📢 Ads removed: $removed")
    }

    fun getLastAdTimestamp(): Long {
        return prefs.getLong(Constants.Prefs.LAST_AD_TIMESTAMP, 0L)
    }

    fun setLastAdTimestamp(timestamp: Long) {
        prefs.edit().putLong(Constants.Prefs.LAST_AD_TIMESTAMP, timestamp).apply()
    }

    fun canShowAd(): Boolean {
        if (getAdsRemoved()) return false

        val lastAdTime = getLastAdTimestamp()
        val currentTime = System.currentTimeMillis()
        val timeSinceLastAd = currentTime - lastAdTime

        return timeSinceLastAd >= Constants.AD_COOLDOWN_MS
    }

    // ==================== TUTORIAL (NEW!) ====================

    /**
     * Check if tutorial should be shown
     * Returns true if:
     * - User never checked "don't show today"
     * - OR it's a new day since last check
     */
    fun shouldShowTutorial(): Boolean {
        val lastDate = prefs.getString(KEY_TUTORIAL_DONT_SHOW_DATE, null)
        val today = getCurrentDate()

        // Never hidden before → show
        if (lastDate == null) return true

        // Different day → show
        return lastDate != today
    }

    /**
     * Get checkbox state
     * Returns true if user previously checked "don't show today"
     */
    fun getTutorialCheckboxState(): Boolean {
        return prefs.getBoolean(KEY_TUTORIAL_CHECKBOX_STATE, false)
    }

    /**
     * Save tutorial preferences when dialog is dismissed
     * @param dontShowToday - checkbox state
     */
    fun saveTutorialPreference(dontShowToday: Boolean) {
        prefs.edit().apply {
            putBoolean(KEY_TUTORIAL_CHECKBOX_STATE, dontShowToday)

            if (dontShowToday) {
                // Save current date to hide for today
                putString(KEY_TUTORIAL_DONT_SHOW_DATE, getCurrentDate())
            } else {
                // Remove date restriction if unchecked
                remove(KEY_TUTORIAL_DONT_SHOW_DATE)
            }

            apply()
        }

        Log.d(TAG, "📖 Tutorial preference saved: dontShowToday=$dontShowToday")
    }

    /**
     * Force show tutorial (for testing or settings)
     */
    fun resetTutorial() {
        prefs.edit().apply {
            remove(KEY_TUTORIAL_DONT_SHOW_DATE)
            remove(KEY_TUTORIAL_CHECKBOX_STATE)
            apply()
        }
        Log.d(TAG, "🔄 Tutorial reset - will show on next launch")
    }

    /**
     * Get current date in YYYY-MM-DD format
     */
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    // ==================== UTILITY ====================

    fun resetAllData() {
        prefs.edit().clear().apply()
        Log.d(TAG, "🗑️ All data cleared!")
    }

    fun resetGameProgress() {
        prefs.edit().apply {
            // Remove high scores
            remove(Constants.Prefs.HIGH_SCORE_NORMAL)
            remove(Constants.Prefs.HIGH_SCORE_HARD)
            remove(Constants.Prefs.HIGH_SCORE_SUPER_HARD)
            remove(Constants.Prefs.HIGH_SCORE_RELAX)

            // Reset coins
            putInt(Constants.Prefs.COINS, Constants.STARTING_COINS)

            // Reset items
            remove("item_slow_time")
            remove("item_skip_level")

            // Reset stats
            remove(Constants.Prefs.TOTAL_GAMES)
            remove(Constants.Prefs.TOTAL_SCORE)

            // Reset shop
            remove(Constants.Prefs.OWNED_SKINS)
            putString(Constants.Prefs.CURRENT_SKIN, "default")

            apply()
        }
        Log.d(TAG, "🗑️ Game progress reset (settings preserved)")
    }

    fun exportData(): String {
        val allData = prefs.all
        return org.json.JSONObject(allData as Map<*, *>).toString(2)
    }

    fun getDataSummary(): String {
        return buildString {
            appendLine("=== PREFERENCES SUMMARY ===")
            appendLine("High Scores:")
            appendLine("  NORMAL: ${getHighScore(GameMode.NORMAL)}")
            appendLine("  HARD: ${getHighScore(GameMode.HARD)}")
            appendLine("  SUPER_HARD: ${getHighScore(GameMode.SUPER_HARD)}")
            appendLine("  RELAX: ${getHighScore(GameMode.RELAX)}")
            appendLine()
            appendLine("Coins: ${getCoins()}")
            appendLine()
            appendLine("Items:")
            appendLine("  Slow Time: ${getItemCount("slow_time")}")
            appendLine("  Skip Level: ${getItemCount("skip_level")}")
            appendLine()
            appendLine("Settings:")
            appendLine("  Sound: ${getSoundEnabled()}")
            appendLine("  Vibration: ${getVibrationEnabled()}")
            appendLine("  Music: ${getMusicEnabled()}")
            appendLine("  Volume: ${(getVolume() * 100).toInt()}%")
            appendLine()
            appendLine("Shop:")
            appendLine("  Current Skin: ${getCurrentSkin()}")
            appendLine("  Owned Skins: ${getOwnedSkins()}")
            appendLine()
            appendLine("Stats:")
            appendLine("  Total Games: ${getTotalGames()}")
            appendLine("  Total Score: ${getTotalScore()}")
            appendLine()
            appendLine("Tutorial:")
            appendLine("  Should Show: ${shouldShowTutorial()}")
            appendLine("  Checkbox State: ${getTutorialCheckboxState()}")
            appendLine()
            appendLine("Ads:")
            appendLine("  Removed: ${getAdsRemoved()}")
            appendLine("===========================")
        }
    }
}

// ==================== EXTENSION FUNCTIONS ====================

fun SharedPreferences.getIntOrDefault(key: String, default: Int = 0): Int {
    return getInt(key, default)
}

fun SharedPreferences.getFloatOrDefault(key: String, default: Float = 0f): Float {
    return getFloat(key, default)
}

fun SharedPreferences.getBooleanOrDefault(key: String, default: Boolean = false): Boolean {
    return getBoolean(key, default)
}

fun SharedPreferences.getStringOrDefault(key: String, default: String = ""): String {
    return getString(key, default) ?: default
}

fun SharedPreferences.Editor.putAll(vararg pairs: Pair<String, Any>): SharedPreferences.Editor {
    pairs.forEach { (key, value) ->
        when (value) {
            is Int -> putInt(key, value)
            is Float -> putFloat(key, value)
            is Boolean -> putBoolean(key, value)
            is String -> putString(key, value)
            is Long -> putLong(key, value)
        }
    }
    return this
}
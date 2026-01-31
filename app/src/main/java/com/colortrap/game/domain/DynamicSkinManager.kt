package com.colortrap.game.domain

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.colortrap.game.data.local.PreferencesManager
import com.colortrap.game.data.models.DynamicColorGroup
import com.colortrap.game.data.models.SkinType
import com.colortrap.game.data.models.TileVariant
import com.colortrap.game.utils.AssetLoader
import com.colortrap.game.utils.DynamicAssetScanner

/**
 * DynamicSkinManager - Quản lý skins động
 *
 * CHỨC NĂNG:
 * - Scan tất cả color groups từ assets/skins/{current_skin}/
 * - Load bitmaps cho tiles based on equipped skin
 * - Cache bitmaps để tối ưu performance
 * - Cung cấp random groups/variants cho level generation
 *
 * CRITICAL FIX:
 * ✅ NEVER recycle bitmaps (Compose manages memory)
 * ✅ Use LRU cache with size limit
 * ✅ Let garbage collector handle cleanup
 * ✅ Support multiple skin themes
 */
class DynamicSkinManager(private val context: Context) {

    companion object {
        private const val TAG = "DynamicSkinManager"
        private const val MAX_CACHE_SIZE = 50 // Limit cache to prevent memory issues
    }

    private val assetScanner = DynamicAssetScanner(context)
    private val assetLoader = AssetLoader(context)
    private val prefsManager = PreferencesManager(context)

    // Get current skin folder
    private fun getCurrentSkinFolder(): String {
        val currentSkinId = prefsManager.getCurrentSkin()
        val skinType = SkinType.fromId(currentSkinId)
        return skinType.folderName
    }

    // LRU Cache with automatic size management
    private val bitmapCache = object : LinkedHashMap<String, Bitmap>(
        MAX_CACHE_SIZE,
        0.75f,
        true // Access order (LRU)
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Bitmap>?): Boolean {
            val shouldRemove = size > MAX_CACHE_SIZE
            if (shouldRemove && eldest != null) {
                // ✅ CRITICAL: DON'T RECYCLE! Compose may still be using it
                // Let garbage collector handle cleanup
                Log.d(TAG, "⚠️ Cache full, removing oldest: ${eldest.key} (size: $size)")
            }
            return shouldRemove
        }
    }

    // Cached color groups
    private var cachedColorGroups: List<DynamicColorGroup>? = null

    /**
     * Scan và lấy tất cả color groups
     */
    fun getAvailableColorGroups(): List<String> {
        return assetScanner.scanColorGroups()
    }

    /**
     * Load bitmap cho một tile cụ thể
     *
     * @param colorGroup - Tên folder (vd: "blue", "red")
     * @param variantIndex - Index của variant (0, 1, 2...)
     * @return Bitmap hoặc throw exception nếu fail
     */
    suspend fun loadTileBitmap(colorGroup: String, variantIndex: Int): Bitmap {
        // Get variants cho color group
        val variants = assetScanner.scanColorVariants(colorGroup)

        if (variants.isEmpty()) {
            throw Exception("No variants found for color group: $colorGroup")
        }

        // Get file name
        val fileName = variants.getOrNull(variantIndex)
            ?: variants.firstOrNull()
            ?: throw Exception("No variants available for $colorGroup")

        // Build asset path
        val assetPath = assetScanner.getAssetPath(colorGroup, fileName)

        // Check cache first
        bitmapCache[assetPath]?.let { cachedBitmap ->
            // ✅ Verify bitmap is still valid
            if (!cachedBitmap.isRecycled) {
                Log.d(TAG, "✅ Cache hit: $assetPath")
                return cachedBitmap
            } else {
                // Remove invalid bitmap
                bitmapCache.remove(assetPath)
                Log.w(TAG, "⚠️ Removed recycled bitmap from cache: $assetPath")
            }
        }

        // Load bitmap
        val bitmap = assetLoader.loadBitmap(assetPath)
            ?: throw Exception("Failed to load bitmap: $assetPath")

        // Cache it (LRU will auto-remove oldest if needed)
        bitmapCache[assetPath] = bitmap

        Log.d(TAG, "✅ Loaded & cached: $colorGroup/$fileName (cache size: ${bitmapCache.size})")
        return bitmap
    }

    /**
     * Load bitmap synchronously (for preview/thumbnail)
     */
    fun loadTileBitmapSync(colorGroup: String, variantIndex: Int): Bitmap? {
        return try {
            val variants = assetScanner.scanColorVariants(colorGroup)
            val fileName = variants.getOrNull(variantIndex) ?: variants.firstOrNull() ?: return null
            val assetPath = assetScanner.getAssetPath(colorGroup, fileName)

            // Check cache
            bitmapCache[assetPath]?.let { cachedBitmap ->
                if (!cachedBitmap.isRecycled) {
                    return cachedBitmap
                } else {
                    bitmapCache.remove(assetPath)
                }
            }

            // Load sync
            val bitmap = assetLoader.loadBitmapSync(assetPath)
            bitmap?.let { bitmapCache[assetPath] = it }

            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error loading bitmap sync: ${e.message}")
            null
        }
    }

    /**
     * Get random color groups (for level generation)
     */
    fun getRandomColorGroups(count: Int): List<String> {
        val allGroups = getAvailableColorGroups()
        return allGroups.shuffled().take(count.coerceAtMost(allGroups.size))
    }

    /**
     * Get random variant index cho một color group
     */
    fun getRandomVariantIndex(colorGroup: String): Int {
        val variants = assetScanner.scanColorVariants(colorGroup)
        return if (variants.isNotEmpty()) {
            variants.indices.random()
        } else {
            0
        }
    }

    /**
     * Get số lượng variants cho một color group
     */
    fun getVariantCount(colorGroup: String): Int {
        return assetScanner.scanColorVariants(colorGroup).size
    }

    /**
     * Check if color group exists
     */
    fun hasColorGroup(colorGroup: String): Boolean {
        return getAvailableColorGroups().contains(colorGroup)
    }

    /**
     * Clear bitmap cache
     * ✅ FIXED: DON'T recycle bitmaps!
     */
    fun clearCache() {
        // ✅ CRITICAL FIX: Don't recycle! Just clear references
        // Compose may still be rendering these bitmaps
        // Let garbage collector handle cleanup
        val cacheSize = bitmapCache.size
        bitmapCache.clear()
        Log.d(TAG, "🗑️ Cache cleared ($cacheSize bitmaps removed, NOT recycled)")
    }

    /**
     * Get cache size
     */
    fun getCacheSize(): Int = bitmapCache.size

    /**
     * Get cache stats for debugging
     */
    fun getCacheStats(): String {
        return buildString {
            appendLine("=== Bitmap Cache Stats ===")
            appendLine("Size: ${bitmapCache.size}/$MAX_CACHE_SIZE")

            var totalBytes = 0L
            var recycledCount = 0
            bitmapCache.values.forEach { bitmap ->
                if (bitmap.isRecycled) {
                    recycledCount++
                } else {
                    totalBytes += bitmap.byteCount
                }
            }

            appendLine("Valid: ${bitmapCache.size - recycledCount}")
            appendLine("Recycled: $recycledCount")
            appendLine("Memory: ${totalBytes / 1024}KB")
            appendLine("=========================")
        }
    }

    /**
     * Preload bitmaps cho một level (optional optimization)
     */
    suspend fun preloadLevel(colorGroups: List<Pair<String, Int>>) {
        colorGroups.forEach { (colorGroup, variantIndex) ->
            try {
                loadTileBitmap(colorGroup, variantIndex)
            } catch (e: Exception) {
                Log.e(TAG, "⚠️ Failed to preload: $colorGroup/$variantIndex")
            }
        }
        Log.d(TAG, "✅ Preloaded ${colorGroups.size} tiles (cache: ${bitmapCache.size})")
    }
}
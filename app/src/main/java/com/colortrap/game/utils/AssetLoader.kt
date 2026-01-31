package com.colortrap.game.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Load và cache images từ assets
 */
class AssetLoader(private val context: Context) {

    companion object {
        private const val TAG = "AssetLoader"
        private const val MAX_CACHE_SIZE = 50 // Giới hạn số ảnh trong cache
    }

    // LRU Cache cho Bitmaps
    private val bitmapCache = LinkedHashMap<String, Bitmap>(
        MAX_CACHE_SIZE,
        0.75f,
        true // accessOrder = true (LRU)
    )

    /**
     * Load bitmap từ assets (async)
     * @param assetPath - Path đầy đủ (vd: "skins/color/blue/01.png")
     * @return Bitmap hoặc null nếu lỗi
     */
    suspend fun loadBitmap(assetPath: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            // Check cache trước
            bitmapCache[assetPath]?.let {
                Log.d(TAG, "✅ Cache hit: $assetPath")
                return@withContext it
            }

            // Load từ assets
            context.assets.open(assetPath).use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)

                if (bitmap != null) {
                    // Add vào cache
                    addToCache(assetPath, bitmap)
                    Log.d(TAG, "✅ Loaded: $assetPath (${bitmap.width}x${bitmap.height})")
                } else {
                    Log.e(TAG, "❌ Failed to decode: $assetPath")
                }

                bitmap
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error loading $assetPath: ${e.message}")
            null
        }
    }

    /**
     * Load bitmap synchronously (chỉ dùng cho preview/thumbnail)
     */
    fun loadBitmapSync(assetPath: String): Bitmap? {
        return try {
            bitmapCache[assetPath] ?: run {
                context.assets.open(assetPath).use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)?.also {
                        addToCache(assetPath, it)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error loading sync: ${e.message}")
            null
        }
    }

    /**
     * Preload nhiều bitmaps (cho 1 level)
     */
    suspend fun preloadBitmaps(assetPaths: List<String>) = withContext(Dispatchers.IO) {
        assetPaths.forEach { path ->
            loadBitmap(path)
        }
        Log.d(TAG, "✅ Preloaded ${assetPaths.size} bitmaps")
    }

    /**
     * Add vào cache với LRU eviction
     */
    private fun addToCache(key: String, bitmap: Bitmap) {
        if (bitmapCache.size >= MAX_CACHE_SIZE) {
            // Remove oldest entry
            val oldest = bitmapCache.entries.first()
            oldest.value.recycle()
            bitmapCache.remove(oldest.key)
            Log.d(TAG, "♻️ Evicted from cache: ${oldest.key}")
        }
        bitmapCache[key] = bitmap
    }

    /**
     * Clear toàn bộ cache
     */
    fun clearCache() {
        bitmapCache.values.forEach { it.recycle() }
        bitmapCache.clear()
        Log.d(TAG, "🗑️ Cache cleared")
    }

    /**
     * Get cache size
     */
    fun getCacheSize(): Int = bitmapCache.size
}
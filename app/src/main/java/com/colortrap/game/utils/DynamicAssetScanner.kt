package com.colortrap.game.utils

import android.content.Context
import android.util.Log

/**
 * Tự động scan tất cả color groups trong assets/skins/color/
 * Không cần hardcode trong JSON!
 */
class DynamicAssetScanner(private val context: Context) {

    companion object {
        private const val TAG = "DynamicAssetScanner"
        private const val SKINS_PATH = "skins/color"
    }

    /**
     * Scan tất cả folders trong assets/skins/color/
     * @return List<String> - Danh sách tên folders (blue, red, green...)
     */
    fun scanColorGroups(): List<String> {
        return try {
            val colorGroups = context.assets.list(SKINS_PATH)?.toList() ?: emptyList()

            // Filter chỉ lấy folders (loại bỏ files)
            val validGroups = colorGroups.filter { groupName ->
                try {
                    val files = context.assets.list("$SKINS_PATH/$groupName")
                    !files.isNullOrEmpty()
                } catch (e: Exception) {
                    false
                }
            }

            Log.d(TAG, "✅ Scanned ${validGroups.size} color groups: $validGroups")
            validGroups

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error scanning color groups: ${e.message}")
            emptyList()
        }
    }

    /**
     * Scan tất cả variants (images) trong 1 color group
     * @param groupName - Tên folder (vd: "blue")
     * @return List<String> - Danh sách tên files (01.png, 02.webp...)
     */
    fun scanColorVariants(groupName: String): List<String> {
        return try {
            val path = "$SKINS_PATH/$groupName"
            val files = context.assets.list(path)?.toList() ?: emptyList()

            // Filter chỉ lấy image files
            val validFiles = files.filter { fileName ->
                fileName.endsWith(".png", ignoreCase = true) ||
                        fileName.endsWith(".jpg", ignoreCase = true) ||
                        fileName.endsWith(".webp", ignoreCase = true)
            }

            Log.d(TAG, "✅ Group '$groupName': ${validFiles.size} variants")
            validFiles

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error scanning variants for '$groupName': ${e.message}")
            emptyList()
        }
    }

    /**
     * Get full asset path
     */
    fun getAssetPath(groupName: String, fileName: String): String {
        return "$SKINS_PATH/$groupName/$fileName"
    }

    /**
     * Validate asset exists
     */
    fun assetExists(path: String): Boolean {
        return try {
            context.assets.open(path).use { true }
        } catch (e: Exception) {
            false
        }
    }
}
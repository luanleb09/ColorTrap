package com.colortrap.game.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import java.io.File
import java.io.FileOutputStream

/**
 * SocialShareManager
 * Manages social sharing functionality
 * - Facebook Share
 * - Generic Share (Twitter, Instagram, etc.)
 * - Screenshot sharing
 *
 * FIXED: Type mismatch in ShareDialog.canShow()
 */
class SocialShareManager(private val activity: Activity) {

    companion object {
        private const val TAG = "SocialShareManager"
    }

    // ==================== FACEBOOK SHARE ====================

    /**
     * Share score to Facebook with screenshot
     */
    fun shareToFacebook(
        score: Int,
        level: Int,
        mode: String,
        screenshot: Bitmap? = null
    ) {
        try {
            val shareDialog = ShareDialog(activity)

            if (screenshot != null) {
                // Share with photo
                val photo = SharePhoto.Builder()
                    .setBitmap(screenshot)
                    .build()

                val content = SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build()

                // FIXED: Remove ::class.java, just pass content
                if (shareDialog.canShow(content)) {
                    shareDialog.show(content)
                    Log.d(TAG, "✅ Sharing to Facebook with photo")
                } else {
                    // Fallback to text share
                    shareScoreText(score, level, mode)
                }
            } else {
                // Share text only (Facebook doesn't support link posts from apps anymore)
                shareScoreText(score, level, mode)
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Facebook share failed: ${e.message}")
            // Fallback to generic share
            shareScoreText(score, level, mode)
        }
    }

    /**
     * Share to Facebook Story
     */
    fun shareToFacebookStory(
        score: Int,
        level: Int,
        mode: String,
        screenshot: Bitmap
    ) {
        try {
            // Save screenshot to temp file
            val file = saveBitmapToCache(screenshot, "colortrap_story.jpg")
            val uri = FileProvider.getUriForFile(
                activity,
                "${activity.packageName}.fileprovider",
                file
            )

            // Create story share intent
            val intent = Intent("com.facebook.stories.ADD_TO_STORY").apply {
                type = "image/*"
                putExtra("interactive_asset_uri", uri)
                putExtra(
                    "top_background_color",
                    "#4267B2" // Facebook blue
                )
                putExtra(
                    "bottom_background_color",
                    "#2196F3" // Light blue
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Check if Facebook app is installed
            if (intent.resolveActivity(activity.packageManager) != null) {
                activity.startActivity(intent)
                Log.d(TAG, "✅ Sharing to Facebook Story")
            } else {
                Log.w(TAG, "⚠️ Facebook app not installed")
                shareScoreText(score, level, mode)
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Facebook Story share failed: ${e.message}")
        }
    }

    // ==================== GENERIC SHARE ====================

    /**
     * Share score via any app (Twitter, Instagram, WhatsApp, etc.)
     */
    fun shareScoreText(
        score: Int,
        level: Int,
        mode: String
    ) {
        val text = buildString {
            appendLine("🎮 I just scored $score in ColorTrap!")
            appendLine("📊 Level: $level")
            appendLine("🎯 Mode: $mode")
            appendLine()
            appendLine("Can you beat my score? 🏆")
            appendLine()
            appendLine("#ColorTrap #MobileGaming")
            appendLine("Download: [Your Play Store Link]")
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "ColorTrap - My Score!")
        }

        val chooser = Intent.createChooser(intent, "Share your score")
        activity.startActivity(chooser)

        Log.d(TAG, "✅ Opening share chooser")
    }

    /**
     * Share with screenshot
     */
    fun shareWithScreenshot(
        score: Int,
        level: Int,
        mode: String,
        screenshot: Bitmap
    ) {
        try {
            // Save screenshot to cache
            val file = saveBitmapToCache(screenshot, "colortrap_score.jpg")
            val uri = FileProvider.getUriForFile(
                activity,
                "${activity.packageName}.fileprovider",
                file
            )

            val text = buildString {
                appendLine("🎮 ColorTrap Score: $score")
                appendLine("📊 Level: $level | Mode: $mode")
                appendLine("#ColorTrap")
            }

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, text)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(intent, "Share your achievement")
            activity.startActivity(chooser)

            Log.d(TAG, "✅ Sharing with screenshot")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Screenshot share failed: ${e.message}")
            // Fallback to text only
            shareScoreText(score, level, mode)
        }
    }

    // ==================== UTILITIES ====================

    /**
     * Save bitmap to cache directory
     */
    private fun saveBitmapToCache(bitmap: Bitmap, filename: String): File {
        val cacheDir = File(activity.cacheDir, "images")
        cacheDir.mkdirs()

        val file = File(cacheDir, filename)

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }

        return file
    }

    /**
     * Check if Facebook app is installed
     */
    fun isFacebookInstalled(): Boolean {
        return try {
            activity.packageManager.getPackageInfo("com.facebook.katana", 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Open ColorTrap page on Facebook
     */
    fun openFacebookPage(pageId: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)

            // Try to open in Facebook app
            intent.data = Uri.parse("fb://page/$pageId")

            if (intent.resolveActivity(activity.packageManager) == null) {
                // Fallback to browser
                intent.data = Uri.parse("https://www.facebook.com/$pageId")
            }

            activity.startActivity(intent)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to open Facebook page: ${e.message}")
        }
    }
}
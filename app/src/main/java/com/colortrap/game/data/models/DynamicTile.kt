package com.colortrap.game.data.models

import android.graphics.Bitmap

data class DynamicTile(
    val id: Int,                    // ← CHANGED from String
    val colorGroup: String,
    val variantIndex: Int,
    val bitmap: Bitmap? = null      // ← CHANGED from Bitmap (nullable)
)
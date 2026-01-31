//package com.colortrap.game.data.models
//
//import android.os.Parcelable
//import kotlinx.parcelize.Parcelize
//
//@Parcelize
//data class TileVariant(
//    val groupId: String,
//    val variantIndex: Int,
//    val imagePath: String,
//    val displayName: String
//) : Parcelable

package com.colortrap.game.data.models

/**
 * Tile variant
 */
data class TileVariant(
    val groupName: String,
    val fileName: String,
    val assetPath: String
)

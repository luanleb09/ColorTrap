//package com.colortrap.game.data.models
//
//import android.os.Parcelable
//import kotlinx.parcelize.Parcelize
//
//@Parcelize
//data class DynamicColorGroup(
//    val id: String,
//    val name: String,
//    val variantPaths: List<String>,
//    val difficulty: DifficultyLevel = DifficultyLevel.EASY
//) : Parcelable {
//
//    fun getVariantCount(): Int = variantPaths.size
//
//    fun getVariantPath(index: Int): String? {
//        return variantPaths.getOrNull(index)
//    }
//
//    fun getRandomVariantPath(): String {
//        return variantPaths.random()
//    }
//}

package com.colortrap.game.data.models

/**
 * Dynamic color group
 */
data class DynamicColorGroup(
    val groupName: String,
    val variants: List<String>,
    val difficulty: DifficultyLevel = DifficultyLevel.EASY
)
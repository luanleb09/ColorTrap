package com.colortrap.game.data.models

/**
 * Skin Type Definitions
 * Each skin has different visual themes for tiles
 */
enum class SkinType(
    val id: String,
    val displayName: String,
    val description: String,
    val folderName: String,
    val price: Int,
    val isDefault: Boolean = false,
    val emoji: String = "🎨"
) {
    // Default skin (free)
    GEOMETRIC(
        id = "geometric",
        displayName = "Geometric",
        description = "Simple colorful shapes",
        folderName = "color", // Current default
        price = 0,
        isDefault = true,
        emoji = "🔷"
    ),

    // Premium skins
    ANIMALS(
        id = "animals",
        displayName = "Animals",
        description = "Cute animal faces",
        folderName = "animals",
        price = 500,
        emoji = "🐶"
    ),

    EMOJI(
        id = "emoji",
        displayName = "Emoji",
        description = "Popular emoji icons",
        folderName = "emoji",
        price = 300,
        emoji = "😀"
    ),

    GEMS(
        id = "gems",
        displayName = "Gems",
        description = "Shiny precious stones",
        folderName = "gems",
        price = 800,
        emoji = "💎"
    ),

    FOOD(
        id = "food",
        displayName = "Food",
        description = "Delicious treats",
        folderName = "food",
        price = 400,
        emoji = "🍕"
    ),

    SPORTS(
        id = "sports",
        displayName = "Sports",
        description = "Sports equipment",
        folderName = "sports",
        price = 600,
        emoji = "⚽"
    ),

    NATURE(
        id = "nature",
        displayName = "Nature",
        description = "Flowers and plants",
        folderName = "nature",
        price = 450,
        emoji = "🌸"
    ),

    SPACE(
        id = "space",
        displayName = "Space",
        description = "Planets and stars",
        folderName = "space",
        price = 700,
        emoji = "🚀"
    ),

    HALLOWEEN(
        id = "halloween",
        displayName = "Halloween",
        description = "Spooky season vibes",
        folderName = "halloween",
        price = 999,
        emoji = "🎃"
    ),

    CHRISTMAS(
        id = "christmas",
        displayName = "Christmas",
        description = "Holiday cheer",
        folderName = "christmas",
        price = 999,
        emoji = "🎄"
    );

    companion object {
        /**
         * Get skin by ID
         */
        fun fromId(id: String): SkinType {
            return values().find { it.id == id } ?: GEOMETRIC
        }

        /**
         * Get all available skins
         */
        fun getAllSkins(): List<SkinType> {
            return values().toList()
        }

        /**
         * Get free skins
         */
        fun getFreeSkins(): List<SkinType> {
            return values().filter { it.price == 0 }
        }

        /**
         * Get premium skins
         */
        fun getPremiumSkins(): List<SkinType> {
            return values().filter { it.price > 0 }
        }

        /**
         * Get seasonal skins
         */
        fun getSeasonalSkins(): List<SkinType> {
            return listOf(HALLOWEEN, CHRISTMAS)
        }
    }
}

/**
 * Skin ownership data
 */
data class SkinOwnership(
    val skinType: SkinType,
    val isOwned: Boolean,
    val isEquipped: Boolean
)
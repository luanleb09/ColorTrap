//package com.colortrap.game.data.config
//
//import com.colortrap.game.data.models.ItemType
//
///**
// * SHOP & MONETIZATION CONFIGURATION
// * All prices, packages, and IAP configs
// */
//object ShopConfig {
//
//    // ==================== CURRENCY ====================
//
//    object Currency {
//        const val COIN_SYMBOL = "💰"
//        const val COIN_NAME = "Coins"
//
//        // Earning rates
//        const val COINS_PER_LEVEL = 10
//        const val COINS_PER_AD = 50
//        const val DAILY_REWARD_COINS = 100
//        const val ACHIEVEMENT_COINS = 500
//
//        // Starting balance
//        const val STARTING_COINS = 500
//        const val MAX_COINS = 999999
//    }
//
//    // ==================== ITEM PRICES ====================
//
//    object ItemPrices {
//        const val ADD_TIME = 100
//        const val HINT = 150
//        const val SHIELD = 300
//        const val REMOVE_TRAP = 250
//        const val SLOW_TIME = 200
//        const val SHUFFLE = 150
//
//        fun getPrice(itemType: ItemType): Int = when (itemType) {
////            ItemType.ADD_TIME -> ADD_TIME
//            ItemType.HINT -> HINT
////            ItemType.SHIELD -> SHIELD
////            ItemType.REMOVE_TRAP -> REMOVE_TRAP
//            ItemType.SLOW_TIME -> SLOW_TIME
////            ItemType.SHUFFLE -> SHUFFLE
//        }
//    }
//
//    // ==================== ITEM LIMITS PER RUN ====================
//
//    object ItemLimits {
//        const val ADD_TIME_MAX = 2
//        const val HINT_MAX = 2
//        const val SHIELD_MAX = 1
//        const val REMOVE_TRAP_MAX = 1
//        const val SLOW_TIME_MAX = 1
//        const val SHUFFLE_MAX = 1
//
//        fun getMaxPerRun(itemType: ItemType): Int = when (itemType) {
//            ItemType.ADD_TIME -> ADD_TIME_MAX
//            ItemType.HINT -> HINT_MAX
//            ItemType.SHIELD -> SHIELD_MAX
//            ItemType.REMOVE_TRAP -> REMOVE_TRAP_MAX
//            ItemType.SLOW_TIME -> SLOW_TIME_MAX
//            ItemType.SHUFFLE -> SHUFFLE_MAX
//        }
//    }
//
//    // ==================== PACKAGES ====================
//
//    data class ItemPackage(
//        val id: String,
//        val name: String,
//        val description: String,
//        val items: Map<ItemType, Int>,
//        val coinPrice: Int,
//        val discount: Int = 0, // percentage
//        val iapSku: String? = null // For In-App Purchase
//    )
//
//    object Packages {
//        val STARTER_PACK = ItemPackage(
//            id = "starter_pack",
//            name = "Starter Pack",
//            description = "Perfect for beginners!",
//            items = mapOf(
//                ItemType.ADD_TIME to 3,
//                ItemType.HINT to 3,
//                ItemType.SHIELD to 1
//            ),
//            coinPrice = 500,
//            discount = 20 // 20% off
//        )
//
//        val POWER_PACK = ItemPackage(
//            id = "power_pack",
//            name = "Power Pack",
//            description = "For advanced players",
//            items = mapOf(
//                ItemType.ADD_TIME to 5,
//                ItemType.HINT to 5,
//                ItemType.SHIELD to 3,
//                ItemType.REMOVE_TRAP to 2,
//                ItemType.SLOW_TIME to 2
//            ),
//            coinPrice = 1200,
//            discount = 30
//        )
//
//        val ULTIMATE_PACK = ItemPackage(
//            id = "ultimate_pack",
//            name = "Ultimate Pack",
//            description = "Everything you need!",
//            items = mapOf(
//                ItemType.ADD_TIME to 10,
//                ItemType.HINT to 10,
//                ItemType.SHIELD to 5,
//                ItemType.REMOVE_TRAP to 5,
//                ItemType.SLOW_TIME to 5,
//                ItemType.SHUFFLE to 5
//            ),
//            coinPrice = 2500,
//            discount = 40
//        )
//
//        val DAILY_FREE_PACK = ItemPackage(
//            id = "daily_free",
//            name = "Daily Free Pack",
//            description = "Claim once per day!",
//            items = mapOf(
//                ItemType.ADD_TIME to 1,
//                ItemType.HINT to 1
//            ),
//            coinPrice = 0, // Free
//            discount = 100
//        )
//
//        val ALL_PACKAGES = listOf(
//            STARTER_PACK,
//            POWER_PACK,
//            ULTIMATE_PACK,
//            DAILY_FREE_PACK
//        )
//
//        fun getPackageById(id: String): ItemPackage? {
//            return ALL_PACKAGES.find { it.id == id }
//        }
//    }
//    // ==================== COIN PACKAGES (IAP) ====================
//
//    data class CoinPackage(
//        val id: String,
//        val name: String,
//        val coins: Int,
//        val bonusCoins: Int = 0,
//        val priceUsd: Double,
//        val iapSku: String
//    )
//
//    object CoinPackages {
//        val SMALL = CoinPackage(
//            id = "coins_small",
//            name = "Coin Pouch",
//            coins = 1000,
//            bonusCoins = 0,
//            priceUsd = 0.99,
//            iapSku = "com.colortrap.coins.small"
//        )
//
//        val MEDIUM = CoinPackage(
//            id = "coins_medium",
//            name = "Coin Bag",
//            coins = 5000,
//            bonusCoins = 500, // +10%
//            priceUsd = 4.99,
//            iapSku = "com.colortrap.coins.medium"
//        )
//
//        val LARGE = CoinPackage(
//            id = "coins_large",
//            name = "Coin Chest",
//            coins = 15000,
//            bonusCoins = 3000, // +20%
//            priceUsd = 9.99,
//            iapSku = "com.colortrap.coins.large"
//        )
//
//        val MEGA = CoinPackage(
//            id = "coins_mega",
//            name = "Coin Vault",
//            coins = 50000,
//            bonusCoins = 15000, // +30%
//            priceUsd = 24.99,
//            iapSku = "com.colortrap.coins.mega"
//        )
//
//        val ALL_COIN_PACKAGES = listOf(SMALL, MEDIUM, LARGE, MEGA)
//
//        fun getPackageById(id: String): CoinPackage? {
//            return ALL_COIN_PACKAGES.find { it.id == id }
//        }
//    }
//
//    // ==================== SPECIAL OFFERS ====================
//
//    data class SpecialOffer(
//        val id: String,
//        val title: String,
//        val description: String,
//        val originalPrice: Int,
//        val discountedPrice: Int,
//        val discountPercentage: Int,
//        val items: Map<ItemType, Int>,
//        val duration: Long, // milliseconds
//        val iapSku: String? = null
//    )
//
//    object SpecialOffers {
//        val WEEKEND_SPECIAL = SpecialOffer(
//            id = "weekend_special",
//            title = "Weekend Special!",
//            description = "50% OFF - Limited Time",
//            originalPrice = 1000,
//            discountedPrice = 500,
//            discountPercentage = 50,
//            items = mapOf(
//                ItemType.ADD_TIME to 5,
//                ItemType.HINT to 5,
//                ItemType.SHIELD to 2
//            ),
//            duration = 172800000L // 48 hours
//        )
//
//        val FIRST_PURCHASE = SpecialOffer(
//            id = "first_purchase",
//            title = "First Time Offer!",
//            description = "Special deal for new players",
//            originalPrice = 1500,
//            discountedPrice = 299,
//            discountPercentage = 80,
//            items = mapOf(
//                ItemType.ADD_TIME to 10,
//                ItemType.HINT to 10,
//                ItemType.SHIELD to 5,
//                ItemType.REMOVE_TRAP to 3
//            ),
//            duration = 0L // One-time only
//        )
//    }
//
//    // ==================== IAP PRODUCTS ====================
//
//    object IAP {
//        const val REMOVE_ADS_SKU = "com.colortrap.removeads"
//        const val REMOVE_ADS_PRICE_USD = 2.99
//
//        const val VIP_MEMBERSHIP_SKU = "com.colortrap.vip.monthly"
//        const val VIP_PRICE_USD = 4.99 // per month
//
//        // VIP Benefits
//        object VIPBenefits {
//            const val NO_ADS = true
//            const val DOUBLE_COINS = true
//            const val EXCLUSIVE_SKINS = true
//            const val DAILY_BONUS_ITEMS = 3
//        }
//    }
//}
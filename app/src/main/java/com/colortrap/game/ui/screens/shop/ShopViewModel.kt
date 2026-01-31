package com.colortrap.game.ui.screens.shop

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.colortrap.game.data.local.PreferencesManager
import com.colortrap.game.domain.DynamicSkinManager
import com.colortrap.game.utils.AssetLoader
import com.colortrap.game.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ShopViewModel
 * - Load available skins & items
 * - Handle purchases
 * - Manage coins
 */
class ShopViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "ShopViewModel"
    }

    private val context = application.applicationContext
    private val prefsManager = PreferencesManager(context)
    private val skinManager = DynamicSkinManager(context)
    private val assetLoader = AssetLoader(context)

    // State
    private val _coins = MutableStateFlow(0)
    val coins: StateFlow<Int> = _coins.asStateFlow()

    private val _skins = MutableStateFlow<List<SkinItem>>(emptyList())
    val skins: StateFlow<List<SkinItem>> = _skins.asStateFlow()

    private val _items = MutableStateFlow<List<ShopItem>>(emptyList())
    val items: StateFlow<List<ShopItem>> = _items.asStateFlow()

    private val _currentSkin = MutableStateFlow("default")
    val currentSkin: StateFlow<String> = _currentSkin.asStateFlow()

    init {
        loadShopData()
    }

    /**
     * Load all shop data
     */
    private fun loadShopData() {
        viewModelScope.launch {
            try {
                // Load coins
                _coins.value = prefsManager.getCoins()

                // Load current skin
                _currentSkin.value = prefsManager.getCurrentSkin()

                // Load skins
                loadSkins()

                // Load items
                loadItems()

                Log.d(TAG, "✅ Shop data loaded: ${_skins.value.size} skins, ${_items.value.size} items")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Shop loading failed: ${e.message}")
            }
        }
    }

    /**
     * Load available skins
     */
    private suspend fun loadSkins() {
        val colorGroups = skinManager.getAvailableColorGroups()
        val ownedSkins = prefsManager.getOwnedSkins()

        val skinItems = colorGroups.mapIndexed { index, groupName ->
            val isFree = index == 0 // First skin is free
            val isOwned = isFree || ownedSkins.contains(groupName)
            val price = if (isFree) 0 else (index + 1) * 100

            // Load preview bitmap
            val previewBitmap = try {
                skinManager.loadTileBitmap(groupName, 0)
            } catch (e: Exception) {
                null
            }

            SkinItem(
                id = groupName,
                name = groupName.capitalize(),
                price = price,
                isOwned = isOwned,
                isFree = isFree,
                previewBitmap = previewBitmap
            )
        }

        _skins.value = skinItems
    }

    /**
     * Load items
     */
    private fun loadItems() {
        _items.value = listOf(
            ShopItem(
                type = "slow_time",
                name = "Slow Time",
                description = "Slows down timer by 50% for 5 seconds",
                price = Constants.ITEM_SLOW_TIME_PRICE,
                count = prefsManager.getItemCount("slow_time"),
                emoji = "🐌",
                color = Color(0xFF4CAF50)
            ),
            ShopItem(
                type = "skip_level",
                name = "Skip Level",
                description = "Skip current level instantly",
                price = Constants.ITEM_SKIP_LEVEL_PRICE,
                count = prefsManager.getItemCount("skip_level"),
                emoji = "⏭️",
                color = Color(0xFF2196F3)
            )
        )
    }

    /**
     * Purchase skin
     */
    fun purchaseSkin(skinId: String) {
        viewModelScope.launch {
            try {
                val skin = _skins.value.find { it.id == skinId } ?: return@launch

                if (skin.isOwned) {
                    Log.d(TAG, "⚠️ Skin already owned: $skinId")
                    return@launch
                }

                if (_coins.value < skin.price) {
                    Log.d(TAG, "⚠️ Not enough coins for $skinId")
                    return@launch
                }

                // Deduct coins
                val success = prefsManager.spendCoins(skin.price)
                if (!success) return@launch

                // Add to owned skins
                prefsManager.addOwnedSkin(skinId)

                // Equip automatically
                prefsManager.setCurrentSkin(skinId)

                // Refresh data
                _coins.value = prefsManager.getCoins()
                _currentSkin.value = skinId
                loadSkins()

                Log.d(TAG, "✅ Purchased and equipped skin: $skinId")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Purchase failed: ${e.message}")
            }
        }
    }

    /**
     * Equip skin
     */
    fun equipSkin(skinId: String) {
        viewModelScope.launch {
            try {
                val skin = _skins.value.find { it.id == skinId } ?: return@launch

                if (!skin.isOwned) {
                    Log.d(TAG, "⚠️ Cannot equip unowned skin: $skinId")
                    return@launch
                }

                prefsManager.setCurrentSkin(skinId)
                _currentSkin.value = skinId

                Log.d(TAG, "✅ Equipped skin: $skinId")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Equip failed: ${e.message}")
            }
        }
    }

    /**
     * Purchase item
     */
    fun purchaseItem(itemType: String) {
        viewModelScope.launch {
            try {
                val item = _items.value.find { it.type == itemType } ?: return@launch

                if (_coins.value < item.price) {
                    Log.d(TAG, "⚠️ Not enough coins for $itemType")
                    return@launch
                }

                // Deduct coins
                val success = prefsManager.spendCoins(item.price)
                if (!success) return@launch

                // Add item
                prefsManager.addItem(itemType)

                // Refresh data
                _coins.value = prefsManager.getCoins()
                loadItems()

                Log.d(TAG, "✅ Purchased item: $itemType")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Purchase failed: ${e.message}")
            }
        }
    }

    /**
     * Skin Item data class
     */
    data class SkinItem(
        val id: String,
        val name: String,
        val price: Int,
        val isOwned: Boolean,
        val isFree: Boolean,
        val previewBitmap: Bitmap?
    )

    /**
     * Shop Item data class
     */
    data class ShopItem(
        val type: String,
        val name: String,
        val description: String,
        val price: Int,
        val count: Int,
        val emoji: String,
        val color: Color
    )
}
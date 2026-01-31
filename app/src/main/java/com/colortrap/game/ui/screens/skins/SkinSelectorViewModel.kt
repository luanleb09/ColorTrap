package com.colortrap.game.ui.screens.skins

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.colortrap.game.data.local.PreferencesManager
import com.colortrap.game.data.models.SkinType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * SkinSelectorViewModel
 * Manages skin selection, purchase, and equipment
 *
 * FIXED: Removed prefsManager.purchaseSkin() call (method doesn't exist)
 */
class SkinSelectorViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "SkinSelectorViewModel"
    }

    private val prefsManager = PreferencesManager(application.applicationContext)

    private val _uiState = MutableStateFlow(SkinSelectorUiState())
    val uiState: StateFlow<SkinSelectorUiState> = _uiState.asStateFlow()

    init {
        loadSkinData()
    }

    /**
     * Load current skin data
     */
    private fun loadSkinData() {
        viewModelScope.launch {
            val currentSkin = prefsManager.getCurrentSkin()
            val ownedSkins = prefsManager.getOwnedSkins()
            val coins = prefsManager.getCoins()

            _uiState.update {
                it.copy(
                    currentSkin = currentSkin,
                    ownedSkins = ownedSkins,
                    coins = coins
                )
            }

            Log.d(TAG, "✅ Skin data loaded: current=$currentSkin, owned=$ownedSkins, coins=$coins")
        }
    }

    /**
     * Purchase a skin
     * FIXED: Manual implementation of purchase logic
     */
    fun purchaseSkin(skinType: SkinType) {
        val currentCoins = _uiState.value.coins

        // Check if already owned
        if (_uiState.value.ownedSkins.contains(skinType.id)) {
            Log.d(TAG, "⚠️ Skin already owned: ${skinType.id}")
            return
        }

        // Check if can afford
        if (currentCoins < skinType.price) {
            Log.d(TAG, "⚠️ Not enough coins: need ${skinType.price}, have $currentCoins")
            _uiState.update {
                it.copy(
                    showMessage = "Not enough coins! Need ${skinType.price}"
                )
            }
            return
        }

        // FIXED: Manual purchase implementation
        // Deduct coins
        val newCoins = currentCoins - skinType.price
        prefsManager.setCoins(newCoins)

        // Add skin to owned list
        prefsManager.addOwnedSkin(skinType.id)
        val newOwnedSkins = prefsManager.getOwnedSkins()

        // Update UI state
        _uiState.update {
            it.copy(
                ownedSkins = newOwnedSkins,
                coins = newCoins,
                showMessage = "Purchased ${skinType.displayName}!"
            )
        }

        // Auto-equip purchased skin
        equipSkin(skinType)

        Log.d(TAG, "✅ Skin purchased: ${skinType.displayName}, coins: $currentCoins -> $newCoins")
    }

    /**
     * Equip a skin
     */
    fun equipSkin(skinType: SkinType) {
        // Check if owned
        if (!_uiState.value.ownedSkins.contains(skinType.id)) {
            Log.d(TAG, "⚠️ Cannot equip unowned skin: ${skinType.id}")
            return
        }

        // Equip skin
        prefsManager.setCurrentSkin(skinType.id)

        _uiState.update {
            it.copy(
                currentSkin = skinType.id,
                showMessage = "${skinType.displayName} equipped!"
            )
        }

        Log.d(TAG, "✅ Skin equipped: ${skinType.displayName}")
    }

    /**
     * Clear message
     */
    fun clearMessage() {
        _uiState.update { it.copy(showMessage = null) }
    }

    /**
     * UI State
     */
    data class SkinSelectorUiState(
        val currentSkin: String = "geometric",
        val ownedSkins: List<String> = listOf("geometric"),
        val coins: Int = 0,
        val showMessage: String? = null
    )
}

// ==================== VIEWMODEL FACTORY ====================

class SkinSelectorViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SkinSelectorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SkinSelectorViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
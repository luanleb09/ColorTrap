package com.colortrap.game.ui.screens.leaderboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.colortrap.game.data.firebase.FirebaseManager
import com.colortrap.game.data.firebase.LeaderboardEntry
import com.colortrap.game.data.models.GameMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * LeaderboardViewModel
 * Manages leaderboard data for different game modes
 *
 * FIXED:
 * - Pass context to FirebaseManager
 * - Handle Result<> return type
 * - Convert GameMode to String
 * - Factory in separate file
 */
class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "LeaderboardViewModel"
    }

    // Pass application context to FirebaseManager
    private val firebaseManager = FirebaseManager(application.applicationContext)

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        // Load NORMAL mode by default
        loadLeaderboard(GameMode.NORMAL)
    }

    /**
     * Load leaderboard for a specific game mode
     * PUBLIC method - can be called from UI
     */
    fun loadLeaderboard(mode: GameMode) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                Log.d(TAG, "Loading leaderboard for mode: $mode")

                // Convert GameMode to String
                val modeString = mode.name.lowercase()

                // Fetch from Firebase (returns Result<List<>>)
                val result = firebaseManager.getLeaderboard(modeString, limit = 50)

                result.onSuccess { entries ->
                    // Get current user ID
                    val currentUserId = firebaseManager.getCurrentUser()?.uid

                    _uiState.update {
                        it.copy(
                            entries = entries,
                            currentUserId = currentUserId,
                            isLoading = false,
                            errorMessage = null
                        )
                    }

                    Log.d(TAG, "✅ Loaded ${entries.size} entries for $mode")
                }.onFailure { exception ->
                    Log.e(TAG, "❌ Error loading leaderboard: ${exception.message}")
                    _uiState.update {
                        it.copy(
                            entries = emptyList(),
                            isLoading = false,
                            errorMessage = exception.message ?: "Failed to load leaderboard"
                        )
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error loading leaderboard: ${e.message}")
                _uiState.update {
                    it.copy(
                        entries = emptyList(),
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load leaderboard"
                    )
                }
            }
        }
    }

    /**
     * Refresh current leaderboard
     */
    fun refresh(mode: GameMode) {
        loadLeaderboard(mode)
    }

    /**
     * UI State
     */
    data class LeaderboardUiState(
        val entries: List<LeaderboardEntry> = emptyList(),
        val currentUserId: String? = null,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )
}
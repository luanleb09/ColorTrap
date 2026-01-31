package com.colortrap.game.ui.screens.login

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.colortrap.game.data.firebase.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * LoginViewModel
 * Manages authentication state and login flow
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val firebaseManager = FirebaseManager(application.applicationContext)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Get Google Sign-In intent
     */
    fun getGoogleSignInIntent(): Intent {
        return firebaseManager.getGoogleSignInClient().signInIntent
    }

    /**
     * Sign in with Google credential
     */
    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = firebaseManager.signInWithGoogle(idToken)

            result.fold(
                onSuccess = { user ->
                    Log.d(TAG, "✅ Google sign-in successful: ${user.email}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSignedIn = true
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    Log.e(TAG, "❌ Google sign-in failed: ${error.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Sign-in failed: ${error.message}"
                        )
                    }
                }
            )
        }
    }

    /**
     * Setup Facebook login callback
     */
    fun setupFacebookCallback(activity: Activity, onSuccess: () -> Unit) {
        firebaseManager.setupFacebookLogin(
            activity = activity,
            onSuccess = { user ->
                Log.d(TAG, "✅ Facebook sign-in successful: ${user.email}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSignedIn = true
                    )
                }
                onSuccess()
            },
            onError = { error ->
                Log.e(TAG, "❌ Facebook sign-in failed: $error")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Facebook sign-in failed: $error"
                    )
                }
            }
        )
    }

    /**
     * Set error message
     */
    fun setError(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = message
            )
        }
    }

    /**
     * Check if user is already signed in
     */
    fun checkSignInStatus(): Boolean {
        return firebaseManager.isSignedIn()
    }

    /**
     * UI State
     */
    data class LoginUiState(
        val isLoading: Boolean = false,
        val isSignedIn: Boolean = false,
        val errorMessage: String? = null
    )
}

// ==================== VIEWMODEL FACTORY ====================

class LoginViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
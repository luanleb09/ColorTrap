package com.colortrap.game.data.firebase

import android.app.Activity
import android.content.Context
import android.util.Log
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await

/**
 * FirebaseManager
 * Manages all Firebase operations:
 * - Google Sign-In
 * - Facebook Login
 * - User Management
 * - Leaderboard
 */
class FirebaseManager(private val context: Context) {

    companion object {
        private const val TAG = "FirebaseManager"
        const val WEB_CLIENT_ID = "YOUR_WEB_CLIENT_ID" // From google-services.json
    }

    // Firebase instances
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val leaderboardRef: DatabaseReference = database.getReference("leaderboards")

    // Google Sign-In
    private val googleSignInClient: GoogleSignInClient

    // Facebook Login
    private val facebookCallbackManager: CallbackManager = CallbackManager.Factory.create()

    init {
        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    // ==================== AUTHENTICATION ====================

    /**
     * Get current user
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Check if user is signed in
     */
    fun isSignedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Get Google Sign-In client
     */
    fun getGoogleSignInClient(): GoogleSignInClient {
        return googleSignInClient
    }

    /**
     * Sign in with Google credential
     */
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()

            result.user?.let { user ->
                // Create user profile in database
                createUserProfile(user)
                Log.d(TAG, "✅ Google sign-in successful: ${user.email}")
                Result.success(user)
            } ?: Result.failure(Exception("User is null"))

        } catch (e: Exception) {
            Log.e(TAG, "❌ Google sign-in failed: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Setup Facebook Login
     */
    fun setupFacebookLogin(
        activity: Activity,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        LoginManager.getInstance().registerCallback(
            facebookCallbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    Log.d(TAG, "📘 Facebook login success")
                    handleFacebookAccessToken(result.accessToken, onSuccess, onError)
                }

                override fun onCancel() {
                    Log.d(TAG, "📘 Facebook login cancelled")
                    onError("Login cancelled")
                }

                override fun onError(error: FacebookException) {
                    Log.e(TAG, "❌ Facebook login error: ${error.message}")
                    onError(error.message ?: "Unknown error")
                }
            }
        )
    }

    /**
     * Get Facebook callback manager
     */
    fun getFacebookCallbackManager(): CallbackManager {
        return facebookCallbackManager
    }

    /**
     * Handle Facebook access token
     */
    private fun handleFacebookAccessToken(
        token: com.facebook.AccessToken,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        val credential = FacebookAuthProvider.getCredential(token.token)

        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                result.user?.let { user ->
                    createUserProfile(user)
                    Log.d(TAG, "✅ Facebook sign-in successful: ${user.email}")
                    onSuccess(user)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Facebook sign-in failed: ${e.message}")
                onError(e.message ?: "Sign-in failed")
            }
    }

    /**
     * Sign out
     */
    fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
        LoginManager.getInstance().logOut()
        Log.d(TAG, "✅ User signed out")
    }

    // ==================== USER PROFILE ====================

    /**
     * Create/update user profile in database
     */
    private fun createUserProfile(user: FirebaseUser) {
        val userRef = database.getReference("users").child(user.uid)

        val userProfile = mapOf(
            "uid" to user.uid,
            "displayName" to (user.displayName ?: "Anonymous"),
            "email" to (user.email ?: ""),
            "photoUrl" to (user.photoUrl?.toString() ?: ""),
            "lastLogin" to ServerValue.TIMESTAMP,
            "createdAt" to ServerValue.TIMESTAMP
        )

        userRef.updateChildren(userProfile)
            .addOnSuccessListener {
                Log.d(TAG, "✅ User profile created/updated")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Failed to create user profile: ${e.message}")
            }
    }

    // ==================== LEADERBOARD ====================

    /**
     * Submit score to leaderboard
     */
    suspend fun submitScore(
        mode: String,
        score: Int,
        level: Int
    ): Result<Unit> {
        val user = getCurrentUser() ?: return Result.failure(Exception("Not signed in"))

        return try {
            val scoreEntry = mapOf(
                "uid" to user.uid,
                "displayName" to (user.displayName ?: "Anonymous"),
                "photoUrl" to (user.photoUrl?.toString() ?: ""),
                "score" to score,
                "level" to level,
                "timestamp" to ServerValue.TIMESTAMP
            )

            // Save to mode-specific leaderboard
            val leaderboardPath = "leaderboards/$mode/${user.uid}"
            database.getReference(leaderboardPath).setValue(scoreEntry).await()

            Log.d(TAG, "✅ Score submitted: $mode - $score")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to submit score: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Get leaderboard for a mode
     */
    suspend fun getLeaderboard(
        mode: String,
        limit: Int = 100
    ): Result<List<LeaderboardEntry>> {
        return try {
            val snapshot = leaderboardRef
                .child(mode)
                .orderByChild("score")
                .limitToLast(limit)
                .get()
                .await()

            val entries = mutableListOf<LeaderboardEntry>()

            snapshot.children.forEach { child ->
                val entry = child.getValue(LeaderboardEntry::class.java)
                entry?.let { entries.add(it) }
            }

            // Sort by score descending
            entries.sortByDescending { it.score }

            Log.d(TAG, "✅ Leaderboard loaded: $mode - ${entries.size} entries")
            Result.success(entries)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to load leaderboard: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Get user's rank in leaderboard
     */
    suspend fun getUserRank(mode: String): Result<Int> {
        val user = getCurrentUser() ?: return Result.failure(Exception("Not signed in"))

        return try {
            val leaderboard = getLeaderboard(mode).getOrThrow()
            val rank = leaderboard.indexOfFirst { it.uid == user.uid } + 1

            if (rank > 0) {
                Result.success(rank)
            } else {
                Result.failure(Exception("User not in leaderboard"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// ==================== DATA MODELS ====================

data class LeaderboardEntry(
    val uid: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val score: Int = 0,
    val level: Int = 0,
    val timestamp: Long = 0
)
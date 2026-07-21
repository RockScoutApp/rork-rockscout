package com.rork.rockscout.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Local self-contained auth — no Supabase backend.
 *
 * Email + password accounts are stored in [LocalDataStore] under the
 * [LocalDataStore.KEY_USERS] table. Sessions are persisted via two keys
 * ([LocalDataStore.KEY_AUTH_EMAIL] / [LocalDataStore.KEY_AUTH_USER_ID]) so
 * the session survives app restarts without any server round-trip.
 *
 * Exposes a [SessionStatus] flow (the local sealed class, not Supabase's)
 * so UI code that checks `sessionStatus is SessionStatus.Authenticated` and
 * accesses `.session?.user?.email` continues to work unchanged.
 */
class AuthRepository private constructor() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _sessionStatus = MutableStateFlow<SessionStatus>(SessionStatus.NotAuthenticated())
    val sessionStatus: StateFlow<SessionStatus> = _sessionStatus.asStateFlow()

    /** Currently signed-in user's id (null when not authenticated). */
    val currentUserId: String?
        get() = (sessionStatus.value as? SessionStatus.Authenticated)?.session?.user?.id

    /** Currently signed-in user's email (null when not authenticated). */
    val currentUserEmail: String?
        get() = (sessionStatus.value as? SessionStatus.Authenticated)?.session?.user?.email

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** Restore the saved session from local storage, if any. Called once
     *  from Application.onCreate. */
    fun initialize() {
        val savedEmail = LocalDataStore.getString(LocalDataStore.KEY_AUTH_EMAIL)
        val savedUserId = LocalDataStore.getString(LocalDataStore.KEY_AUTH_USER_ID)
        if (!savedEmail.isNullOrBlank() && !savedUserId.isNullOrBlank()) {
            _sessionStatus.value = SessionStatus.Authenticated(
                Session(user = UserInfo(id = savedUserId, email = savedEmail))
            )
            // Re-link RevenueCat to the restored session.
            scope.launch { PurchaseManager.instance.linkRevenueCatUser(savedUserId) }
        }
    }

    /** Sign up with email + password. Creates a local account and signs in. */
    suspend fun signUp(email: String, password: String): Result<Unit> {
        _isLoading.value = true
        _error.value = null
        return runCatching {
            val trimmed = email.trim()
            if (trimmed.isBlank() || !trimmed.contains('@')) {
                error("Please enter a valid email address.")
            }
            if (password.length < 6) {
                error("Password must be at least 6 characters.")
            }
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
            if (users.any { it.email.equals(trimmed, ignoreCase = true) }) {
                error("An account with that email already exists.")
            }
            val userId = "local-" + UUID.randomUUID().toString()
            // Generate a unique default display name. "Rock Scout" is the base;
            // if it's taken, append an incrementing number until we find one
            // that no other user has (case-insensitive).
            val baseName = "Rock Scout"
            val usersList = users
            var defaultName = baseName
            var suffix = 2
            while (usersList.any {
                    it.display_name.trim().equals(defaultName, ignoreCase = true)
                }) {
                defaultName = "$baseName $suffix"
                suffix++
            }
            val newUser = LocalUser(
                id = userId,
                email = trimmed,
                password = password,
                display_name = defaultName,
                avatar_emoji = "\uD83E\uDD20",
                status = "off",
                club_enabled = false,
            )
            LocalDataStore.setTable(LocalDataStore.KEY_USERS, users + newUser)
            saveSession(userId, trimmed)
            // Social toggles default off — users enable them in Social Settings.
            // Link RevenueCat to the user's account so purchases carry over.
            scope.launch { PurchaseManager.instance.linkRevenueCatUser(userId) }
            // Send personalized welcome email (fire-and-forget).
            scope.launch { WelcomeEmailApi.sendWelcomeEmail(trimmed, defaultName) }
            Unit
        }.onFailure {
            _error.value = it.message ?: "Sign-up failed"
            Log.e("AuthRepository", "signUp failed", it)
        }.also {
            _isLoading.value = false
        }
    }

    /** Sign in with email + password. Validates against local accounts. */
    suspend fun signIn(email: String, password: String): Result<Unit> {
        _isLoading.value = true
        _error.value = null
        return runCatching {
            val trimmed = email.trim()
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
            val user = users.firstOrNull { it.email.equals(trimmed, ignoreCase = true) }
                ?: error("No account found for that email. Try creating one.")
            if (user.password != password) {
                error("Incorrect password. Please try again.")
            }
            saveSession(user.id, user.email)
            // Social toggles default off — users enable them in Social Settings.
            // Link RevenueCat to the user's account so purchases carry over.
            scope.launch { PurchaseManager.instance.linkRevenueCatUser(user.id) }
            Unit
        }.onFailure {
            _error.value = it.message ?: "Sign-in failed"
            Log.e("AuthRepository", "signIn failed", it)
        }.also {
            _isLoading.value = false
        }
    }

    /** Sign out and clear the local session. */
    suspend fun signOut(): Result<Unit> {
        _isLoading.value = true
        return runCatching {
            // Log out RevenueCat so the next user starts fresh.
            PurchaseManager.instance.logoutRevenueCatUser()
            LocalDataStore.setString(LocalDataStore.KEY_AUTH_EMAIL, "")
            LocalDataStore.setString(LocalDataStore.KEY_AUTH_USER_ID, "")
            _sessionStatus.value = SessionStatus.NotAuthenticated()
            Unit
        }.onFailure {
            Log.e("AuthRepository", "signOut failed", it)
        }.also {
            _isLoading.value = false
        }
    }

    /**
     * Delete the currently signed-in account and all associated device data.
     * Removes the user from the local users table, wipes every persisted
     * app-state and local-social table, logs out RevenueCat, and clears the
     * session. This satisfies the Play Store in-app account-deletion requirement
     * and mirrors the privacy policy promise that users can delete their account
     * from within the app.
     */
    suspend fun deleteAccount(): Result<Unit> {
        _isLoading.value = true
        return runCatching {
            val userId = currentUserId ?: error("No signed-in account to delete.")
            val userEmail = currentUserEmail
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
            LocalDataStore.setTable(LocalDataStore.KEY_USERS, users.filter { it.id != userId })
            // Wipe all user-generated app state and local social tables.
            PersistenceManager.clearAll()
            LocalDataStore.clearAll()
            // Log out RevenueCat and clear the active session.
            PurchaseManager.instance.logoutRevenueCatUser()
            _sessionStatus.value = SessionStatus.NotAuthenticated()
            // Notify the backend so there is a server-side deletion record.
            if (!userEmail.isNullOrBlank()) {
                scope.launch { DeleteAccountApi.notifyDeletion(userEmail) }
            }
            Unit
        }.onFailure {
            Log.e("AuthRepository", "deleteAccount failed", it)
            _error.value = it.message ?: "Account deletion failed"
        }.also {
            _isLoading.value = false
        }
    }

    /** True when the user is currently authenticated. */
    val isAuthenticated: Boolean
        get() = sessionStatus.value is SessionStatus.Authenticated

    /** Persist the session locally + update the flow. */
    private fun saveSession(userId: String, email: String) {
        LocalDataStore.setString(LocalDataStore.KEY_AUTH_EMAIL, email)
        LocalDataStore.setString(LocalDataStore.KEY_AUTH_USER_ID, userId)
        _sessionStatus.value = SessionStatus.Authenticated(
            Session(user = UserInfo(id = userId, email = email))
        )
    }

    companion object {
        val instance: AuthRepository by lazy { AuthRepository() }
    }
}

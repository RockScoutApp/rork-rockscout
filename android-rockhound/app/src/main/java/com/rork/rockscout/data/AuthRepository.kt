package com.rork.rockscout.data

import android.util.Log
import com.rork.rockscout.data.MockDataSeeder.LocalDeletedAccountLog
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

    /** Email address pending verification (null when not in PendingVerification state). */
    val pendingVerificationEmail: String?
        get() = (sessionStatus.value as? SessionStatus.PendingVerification)?.email

    /** True when the current session user's email has been verified. */
    val isEmailVerified: Boolean
        get() {
            val status = sessionStatus.value
            return when (status) {
                is SessionStatus.Authenticated -> {
                    val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
                    users.firstOrNull { it.id == status.session.user.id }?.email_verified ?: true
                }
                else -> false
            }
        }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** Restore the saved session from local storage, if any. Called once
     *  from Application.onCreate. If the saved user's email is not verified,
     *  the session is not restored — the user must sign in and verify.
     *  If the saved user's account has been admin-deleted, the session is
     *  set to [SessionStatus.AccountDeleted] so the blocking popup shows. */
    fun initialize() {
        val savedEmail = LocalDataStore.getString(LocalDataStore.KEY_AUTH_EMAIL)
        val savedUserId = LocalDataStore.getString(LocalDataStore.KEY_AUTH_USER_ID)
        if (!savedEmail.isNullOrBlank() && !savedUserId.isNullOrBlank()) {
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
            val user = users.firstOrNull { it.id == savedUserId }
            if (user != null && user.account_deleted) {
                // Admin-deleted account — show the blocking popup.
                _sessionStatus.value = SessionStatus.AccountDeleted(
                    email = savedEmail,
                    userId = savedUserId,
                    reason = user.deletion_reason ?: "Your account has been deleted by an administrator.",
                )
                return
            }
            if (user != null && !user.email_verified) {
                // Unverified account — don't restore the session.
                // Clear the saved session so the auth gate shows.
                LocalDataStore.setString(LocalDataStore.KEY_AUTH_EMAIL, "")
                LocalDataStore.setString(LocalDataStore.KEY_AUTH_USER_ID, "")
                _sessionStatus.value = SessionStatus.NotAuthenticated()
                return
            }
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
                email_verified = false,
            )
            LocalDataStore.setTable(LocalDataStore.KEY_USERS, users + newUser)
            // Do NOT save the session yet — the user must verify their email first.
            // Set the session to PendingVerification so the UI shows the code entry panel.
            _sessionStatus.value = SessionStatus.PendingVerification(
                email = trimmed,
                userId = userId,
            )
            // Send the 6-digit verification code via the backend.
            val sendResult = EmailVerificationApi.sendCode(trimmed)
            if (sendResult is EmailVerificationApi.VerificationResult.Failed) {
                // The account was created but we couldn't send the code.
                // The user can resend from the verification panel.
                Log.w("AuthRepository", "Initial verification code send failed: ${sendResult.message}")
            } else if (sendResult is EmailVerificationApi.VerificationResult.NetworkError) {
                Log.w("AuthRepository", "Verification code send network error — user can resend")
            }
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

    /** Complete email verification by submitting the 6-digit [code].
     *  On success, marks the user's email as verified, saves the session,
     *  and transitions to Authenticated. */
    suspend fun completeVerification(code: String): Result<Unit> {
        val pending = sessionStatus.value as? SessionStatus.PendingVerification
            ?: return Result.failure(IllegalStateException("No pending verification."))
        _isLoading.value = true
        _error.value = null
        return runCatching {
            val trimmedCode = code.trim()
            if (trimmedCode.length != 6 || !trimmedCode.all { it.isDigit() }) {
                error("Please enter the 6-digit code.")
            }
            val result = EmailVerificationApi.verifyCode(pending.email, trimmedCode)
            when (result) {
                is EmailVerificationApi.VerificationResult.Success -> {
                    // Mark the user as email_verified = true in the local DB.
                    LocalDataStore.updateTable<LocalUser>(LocalDataStore.KEY_USERS) { users ->
                        users.map { if (it.id == pending.userId) it.copy(email_verified = true) else it }
                    }
                    // Now save the session and transition to Authenticated.
                    saveSession(pending.userId, pending.email)
                    // Link RevenueCat to the user's account.
                    scope.launch { PurchaseManager.instance.linkRevenueCatUser(pending.userId) }
                    Unit
                }
                is EmailVerificationApi.VerificationResult.Failed -> {
                    error(result.message)
                }
                is EmailVerificationApi.VerificationResult.NetworkError -> {
                    error("Network error. Check your connection and try again.")
                }
            }
        }.onFailure {
            _error.value = it.message ?: "Verification failed"
            Log.e("AuthRepository", "completeVerification failed", it)
        }.also {
            _isLoading.value = false
        }
    }

    /** Resend the verification code to the pending email address. */
    suspend fun resendVerificationCode(): Result<Unit> {
        val pending = sessionStatus.value as? SessionStatus.PendingVerification
            ?: return Result.failure(IllegalStateException("No pending verification."))
        _isLoading.value = true
        _error.value = null
        return runCatching {
            val result = EmailVerificationApi.sendCode(pending.email)
            when (result) {
                is EmailVerificationApi.VerificationResult.Success -> Unit
                is EmailVerificationApi.VerificationResult.Failed -> error(result.message)
                is EmailVerificationApi.VerificationResult.NetworkError -> error("Network error. Check your connection and try again.")
            }
        }.onFailure {
            _error.value = it.message ?: "Failed to resend code"
            Log.e("AuthRepository", "resendVerificationCode failed", it)
        }.also {
            _isLoading.value = false
        }
    }

    /** Cancel the pending verification and go back to the sign-up form.
     *  Removes the unverified account so the email can be reused. */
    suspend fun cancelPendingVerification(): Result<Unit> {
        return runCatching {
            val pending = sessionStatus.value as? SessionStatus.PendingVerification
            if (pending != null) {
                // Remove the unverified user from the local DB.
                LocalDataStore.updateTable<LocalUser>(LocalDataStore.KEY_USERS) { users ->
                    users.filter { it.id != pending.userId }
                }
            }
            _sessionStatus.value = SessionStatus.NotAuthenticated()
            _error.value = null
            Unit
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
            if (!user.email_verified) {
                // Unverified account — send a new code and show verification panel.
                _sessionStatus.value = SessionStatus.PendingVerification(
                    email = user.email,
                    userId = user.id,
                )
                val sendResult = EmailVerificationApi.sendCode(user.email)
                if (sendResult is EmailVerificationApi.VerificationResult.Failed) {
                    Log.w("AuthRepository", "Sign-in verification code send failed: ${sendResult.message}")
                }
            } else if (user.account_deleted) {
                // Admin-deleted account — show the blocking popup, do NOT restore session.
                _sessionStatus.value = SessionStatus.AccountDeleted(
                    email = user.email,
                    userId = user.id,
                    reason = user.deletion_reason ?: "Your account has been deleted by an administrator.",
                )
            } else {
                saveSession(user.id, user.email)
                // Link RevenueCat to the user's account so purchases carry over.
                scope.launch { PurchaseManager.instance.linkRevenueCatUser(user.id) }
                // Cloud-restore settings if this is a fresh install (e.g. after a
                // signing-conflict uninstall + reinstall). If local data already
                // exists, the restore is skipped — the user already has their data.
                if (PersistenceManager.isLocalDataEmpty()) {
                    scope.launch {
                        SettingsBackupApi.restoreSettings(user.id)
                            .onSuccess { settingsJson ->
                                if (settingsJson != null) {
                                    val restored = PersistenceManager.restoreAllSettingsFromJson(settingsJson)
                                    if (restored) {
                                        Log.d("AuthRepository", "Restored settings from cloud backup for user ${user.id}")
                                        // Reload the restored data into AppRepository
                                        PersistenceManager.reloadIntoRepository()
                                    }
                                }
                            }
                            .onFailure { Log.w("AuthRepository", "Settings restore failed: ${it.message}") }
                    }
                }
            }
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

    /** The reason for the current AccountDeleted state (null when not in that state). */
    val deletionReason: String?
        get() = (sessionStatus.value as? SessionStatus.AccountDeleted)?.reason

    /** The user ID for the current AccountDeleted state (null when not in that state). */
    val deletedUserId: String?
        get() = (sessionStatus.value as? SessionStatus.AccountDeleted)?.userId

    /** Admin: delete a user's account by [userId] with the given [reason].
     *  Marks the user as deleted (does NOT remove from the DB so they can be
     *  identified on sign-in). Logs the deletion to the deleted-accounts log.
     *  If the user is currently signed in, their session is set to AccountDeleted. */
    suspend fun adminDeleteAccount(userId: String, reason: String): Result<Unit> {
        return runCatching {
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
            val user = users.firstOrNull { it.id == userId }
                ?: error("User not found.")
            val now = System.currentTimeMillis()
            // Mark the user as deleted in the users table.
            LocalDataStore.updateTable<LocalUser>(LocalDataStore.KEY_USERS) { rows ->
                rows.map { if (it.id == userId) it.copy(account_deleted = true, deletion_reason = reason, deleted_at = now, restored_at = null) else it }
            }
            // Log the deletion.
            val logEntry = LocalDeletedAccountLog(
                id = "dellog-" + UUID.randomUUID(),
                user_id = userId,
                username = user.display_name,
                email = user.email,
                reason = reason,
                deleted_at = now,
            )
            LocalDataStore.updateTable<LocalDeletedAccountLog>(LocalDataStore.KEY_DELETED_ACCOUNT_LOGS) { rows ->
                listOf(logEntry) + rows
            }
            // If the deleted user is currently signed in, set their session to AccountDeleted.
            if (currentUserId == userId) {
                _sessionStatus.value = SessionStatus.AccountDeleted(
                    email = user.email,
                    userId = userId,
                    reason = reason,
                )
            }
            Unit
        }.onFailure {
            Log.e("AuthRepository", "adminDeleteAccount failed", it)
        }
    }

    /** Admin: restore a previously deleted user's account by [userId].
     *  Clears the deletion flags and logs the restoration timestamp.
     *  If the user is currently in AccountDeleted session state, they are
     *  transitioned to Authenticated. */
    suspend fun adminRestoreAccount(userId: String): Result<Unit> {
        return runCatching {
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
            val user = users.firstOrNull { it.id == userId }
                ?: error("User not found.")
            val now = System.currentTimeMillis()
            // Clear the deletion flags.
            LocalDataStore.updateTable<LocalUser>(LocalDataStore.KEY_USERS) { rows ->
                rows.map { if (it.id == userId) it.copy(account_deleted = false, deletion_reason = null, restored_at = now) else it }
            }
            // Update the deletion log entry with the restoration timestamp.
            LocalDataStore.updateTable<LocalDeletedAccountLog>(LocalDataStore.KEY_DELETED_ACCOUNT_LOGS) { rows ->
                rows.map { if (it.user_id == userId && it.restored_at == null) it.copy(restored_at = now) else it }
            }
            // If the user is currently in AccountDeleted session state, restore their session.
            val deletedStatus = sessionStatus.value as? SessionStatus.AccountDeleted
            if (deletedStatus != null && deletedStatus.userId == userId) {
                saveSession(userId, user.email)
            }
            Unit
        }.onFailure {
            Log.e("AuthRepository", "adminRestoreAccount failed", it)
        }
    }

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

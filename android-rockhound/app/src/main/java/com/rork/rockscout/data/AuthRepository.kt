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
import java.util.UUID

/**
 * Supabase-backed authentication repository.
 *
 * Replaces the former local-only auth. Sign-up and sign-in now use the same
 * Supabase project as the web PWA, so accounts are shared across Android,
 * web, and iOS. Sessions are persisted via Supabase access/refresh tokens
 * stored in [LocalDataStore].
 *
 * Existing pre-migration users (userId starting with "local-") are silently
 * upgraded on the next app launch: their stored email + password are used to
 * create a Supabase account (or sign in if one already exists from the web).
 * If the silent migration fails (e.g. password mismatch with an existing web
 * account), [needsMigration] is set to true and the local session remains
 * active until the user completes the migration manually.
 *
 * Email verification uses our custom 6-digit code sent via the backend
 * ([EmailVerificationApi]). When the code is verified, the backend also
 * confirms the Supabase email via the admin API, so the user doesn't need
 * to click a separate Supabase confirmation link.
 *
 * Exposes the same [SessionStatus] flow and public API as the previous
 * local-only implementation, so UI code requires no changes.
 */
class AuthRepository private constructor() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _sessionStatus = MutableStateFlow<SessionStatus>(SessionStatus.Initializing)
    val sessionStatus: StateFlow<SessionStatus> = _sessionStatus.asStateFlow()

    private val _needsMigration = MutableStateFlow(false)
    /** True when a pre-migration local account exists but couldn't be auto-upgraded to Supabase. */
    val needsMigration: StateFlow<Boolean> = _needsMigration.asStateFlow()

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
        get() = sessionStatus.value is SessionStatus.Authenticated

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** Pending Supabase user ID from sign-up (used to confirm email via backend). */
    private var pendingSupabaseUserId: String? = null

    /** Pending password from sign-up (used to sign in after email confirmation). */
    private var pendingPassword: String? = null

    /**
     * Restore the saved session. Called once from Application.onCreate.
     *
     * Priority:
     * 1. Supabase access token → try getUser, fall back to refresh.
     * 2. Local pre-migration session → attempt silent migration to Supabase.
     * 3. Nothing → NotAuthenticated.
     */
    fun initialize() {
        val accessToken = LocalDataStore.getString(LocalDataStore.KEY_SUPABASE_ACCESS_TOKEN)
        val refreshToken = LocalDataStore.getString(LocalDataStore.KEY_SUPABASE_REFRESH_TOKEN)

        if (!accessToken.isNullOrBlank()) {
            scope.launch { restoreSupabaseSession(accessToken, refreshToken) }
            return
        }

        // No Supabase tokens — check for pre-migration local session.
        val savedEmail = LocalDataStore.getString(LocalDataStore.KEY_AUTH_EMAIL)
        val savedUserId = LocalDataStore.getString(LocalDataStore.KEY_AUTH_USER_ID)
        val migrated = LocalDataStore.getBoolean(LocalDataStore.KEY_LOCAL_AUTH_MIGRATED, false)

        if (!savedEmail.isNullOrBlank() && !savedUserId.isNullOrBlank()
            && savedUserId.startsWith("local-") && !migrated
        ) {
            scope.launch { attemptMigration(savedEmail, savedUserId) }
            return
        }

        // No Supabase session — check for a pending email verification.
        // The user may have signed up, received the verification email, then
        // the app was killed before they clicked the verify button. Restore
        // the PendingVerification state so the deep link can auto-complete.
        val savedPendingEmail = LocalDataStore.getString(LocalDataStore.KEY_PENDING_VERIFY_EMAIL)
        val savedPendingPassword = LocalDataStore.getString(LocalDataStore.KEY_PENDING_VERIFY_PASSWORD)
        val savedPendingUserId = LocalDataStore.getString(LocalDataStore.KEY_PENDING_VERIFY_USER_ID)
        if (!savedPendingEmail.isNullOrBlank() && !savedPendingPassword.isNullOrBlank()) {
            pendingSupabaseUserId = savedPendingUserId
            pendingPassword = savedPendingPassword
            _sessionStatus.value = SessionStatus.PendingVerification(
                email = savedPendingEmail,
                userId = savedPendingUserId ?: "",
            )
            Log.i("AuthRepository", "Restored pending verification for $savedPendingEmail")
            return
        }

        // No session to restore.
        _sessionStatus.value = SessionStatus.NotAuthenticated()
    }

    /** Restore a Supabase session from stored tokens. */
    private suspend fun restoreSupabaseSession(accessToken: String, refreshToken: String?) {
        // Try getUser with the access token.
        val userResult = SupabaseAuthClient.getUser(accessToken)
        if (userResult.isSuccess) {
            val user = userResult.getOrThrow()
            _sessionStatus.value = SessionStatus.Authenticated(
                Session(user = UserInfo(id = user.id, email = user.email))
            )
            ErrorReporter.setUserId(user.id)
            scope.launch { PurchaseManager.instance.linkRevenueCatUser(user.id) }
            SupabaseDataSync.syncInBackground()
            Log.i("AuthRepository", "Session restored for user=${user.id}")
            // Cloud-restore settings if this is a fresh install (re-install on
            // a new device, or after the signing-conflict uninstall flow).
            restoreSettingsFromCloudIfNeeded(user.id)
            return
        }

        // Access token expired — try refresh.
        if (!refreshToken.isNullOrBlank()) {
            val refreshResult = SupabaseAuthClient.refreshSession(refreshToken)
            if (refreshResult.isSuccess) {
                val auth = refreshResult.getOrThrow()
                saveSupabaseTokens(auth.access_token, auth.refresh_token)
                _sessionStatus.value = SessionStatus.Authenticated(
                    Session(user = UserInfo(
                        id = auth.user?.id ?: "",
                        email = auth.user?.email ?: "",
                    ))
                )
                scope.launch { PurchaseManager.instance.linkRevenueCatUser(auth.user?.id ?: "") }
                SupabaseDataSync.syncInBackground()
                Log.i("AuthRepository", "Session refreshed for user=${auth.user?.id}")
                // Cloud-restore settings if this is a fresh install.
                restoreSettingsFromCloudIfNeeded(auth.user?.id ?: "")
                return
            }
        }

        // Both failed — clear tokens and require sign-in.
        clearSupabaseTokens()
        ErrorReporter.setUserId(null)
        _sessionStatus.value = SessionStatus.NotAuthenticated()
        Log.w("AuthRepository", "Session restore failed — tokens cleared")
    }

    /**
     * Attempt to silently migrate a pre-migration local account to Supabase.
     * Tries sign-up first (new account), then sign-in (existing web account).
     * On success, updates the stored userId and tokens. On failure, keeps the
     * local session and sets [needsMigration] so the UI can prompt the user.
     */
    private suspend fun attemptMigration(email: String, oldUserId: String) {
        val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
        val user = users.firstOrNull { it.id == oldUserId }

        if (user == null || user.password.isBlank()) {
            _sessionStatus.value = SessionStatus.NotAuthenticated()
            return
        }

        // Try sign-up (creates a new Supabase account with the same credentials).
        val signUpResult = SupabaseAuthClient.signUp(email, user.password)
        if (signUpResult.isSuccess) {
            val auth = signUpResult.getOrThrow()
            if (auth.access_token.isNotBlank()) {
                // Session returned (email confirmation disabled) — migration complete.
                completeMigration(auth, user.display_name, user.avatar_emoji)
                Log.i("AuthRepository", "Silent migration via sign-up for $email")
                return
            }
            // No session (email confirmation enabled) — need to verify email.
            // Fall through to sign-in attempt (account exists but unconfirmed).
        }

        // Sign-up failed or no session — try sign-in (account may exist from web).
        val signInResult = SupabaseAuthClient.signInWithPassword(email, user.password)
        if (signInResult.isSuccess) {
            val auth = signInResult.getOrThrow()
            completeMigration(auth, user.display_name, user.avatar_emoji)
            Log.i("AuthRepository", "Silent migration via sign-in for $email")
            return
        }

        // Both failed — keep local session, flag for manual migration.
        val signUpErr = signUpResult.exceptionOrNull()?.message
        val signInErr = signInResult.exceptionOrNull()?.message
        Log.w("AuthRepository", "Silent migration failed for $email: signUp=$signUpErr, signIn=$signInErr")

        // Check if the account was admin-deleted locally.
        if (user.account_deleted) {
            _sessionStatus.value = SessionStatus.AccountDeleted(
                email = email,
                userId = oldUserId,
                reason = user.deletion_reason ?: "Your account has been deleted by an administrator.",
            )
            return
        }

        _needsMigration.value = true
        _sessionStatus.value = SessionStatus.Authenticated(
            Session(user = UserInfo(id = oldUserId, email = email))
        )
        scope.launch { PurchaseManager.instance.linkRevenueCatUser(oldUserId) }
    }

    /** Finalize migration: save tokens, update userId, upsert profile, mark migrated. */
    private suspend fun completeMigration(
        auth: SupabaseAuthClient.AuthResponse,
        displayName: String,
        avatarEmoji: String,
    ) {
        saveSupabaseTokens(auth.access_token, auth.refresh_token)
        val userId = auth.user?.id ?: ""
        val email = auth.user?.email ?: ""
        LocalDataStore.setString(LocalDataStore.KEY_AUTH_EMAIL, email)
        LocalDataStore.setString(LocalDataStore.KEY_AUTH_USER_ID, userId)
        LocalDataStore.setBoolean(LocalDataStore.KEY_LOCAL_AUTH_MIGRATED, true)
        _needsMigration.value = false

        // Upsert the profile so the web PWA can see this user.
        scope.launch {
            SupabaseAuthClient.upsertProfile(auth.access_token, userId, displayName, avatarEmoji)
        }

        _sessionStatus.value = SessionStatus.Authenticated(
            Session(user = UserInfo(id = userId, email = email))
        )
        scope.launch { PurchaseManager.instance.linkRevenueCatUser(userId) }
        SupabaseDataSync.syncInBackground()

        // Cloud-restore settings if this is a fresh install.
        restoreSettingsFromCloudIfNeeded(userId)
    }

    /** Sign up with email + password via Supabase. */
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

            val result = SupabaseAuthClient.signUp(trimmed, password)
            if (result.isFailure) {
                error(result.exceptionOrNull()?.message ?: "Sign-up failed")
            }

            val auth = result.getOrThrow()
            val userId = auth.user?.id ?: ""

            if (auth.access_token.isNotBlank()) {
                // Session returned (email confirmation disabled) — but we still
                // use our 6-digit code for UX consistency. Store the session
                // temporarily and transition to PendingVerification.
                saveSupabaseTokens(auth.access_token, auth.refresh_token)
            }

            // Store pending info for verification + sign-in after confirmation.
            pendingSupabaseUserId = userId
            pendingPassword = password
            // Persist so we can recover if the app is killed between sending
            // the email and the user clicking the verify link.
            LocalDataStore.setString(LocalDataStore.KEY_PENDING_VERIFY_EMAIL, trimmed)
            LocalDataStore.setString(LocalDataStore.KEY_PENDING_VERIFY_PASSWORD, password)
            LocalDataStore.setString(LocalDataStore.KEY_PENDING_VERIFY_USER_ID, userId)

            // Generate a default display name.
            val baseName = "Rock Scout"
            val defaultName = baseName // Supabase profile upsert will handle uniqueness.

            // Transition to PendingVerification — user must enter the 6-digit code
            // or click the verify button in the email.
            _sessionStatus.value = SessionStatus.PendingVerification(
                email = trimmed,
                userId = userId,
            )

            // Send the 6-digit verification code via the backend. If delivery
            // fails we stay on the verification screen (the user can retry with
            // Resend) but surface the reason instead of leaving them staring at
            // a code entry box for an email that will never arrive.
            when (val sendResult = EmailVerificationApi.sendCode(trimmed)) {
                is EmailVerificationApi.VerificationResult.Success -> Unit
                is EmailVerificationApi.VerificationResult.Failed -> {
                    Log.w("AuthRepository", "Initial verification code send failed: ${sendResult.message}")
                    _error.value = sendResult.message
                }
                is EmailVerificationApi.VerificationResult.NetworkError -> {
                    Log.w("AuthRepository", "Verification code send network error — user can resend")
                    _error.value = "Couldn't send the code. Check your connection and tap Resend."
                }
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

    /** Complete email verification by submitting the 6-digit [code]. */
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

            // Verify the code via backend. The backend also confirms the
            // Supabase email via admin API when the code matches.
            val supabaseUserId = pendingSupabaseUserId
            val result = EmailVerificationApi.verifyCodeWithEmailConfirm(
                email = pending.email,
                code = trimmedCode,
                supabaseUserId = supabaseUserId,
            )

            when (result) {
                is EmailVerificationApi.VerificationResult.Success -> {
                    if (!result.emailConfirmed) {
                        Log.w("AuthRepository", "Backend could not confirm the Supabase email")
                    }
                    // Sign in with Supabase to get a fresh session. The admin
                    // confirmation we just triggered can take a moment to become
                    // visible to the auth API, so retry briefly instead of
                    // failing the user on the first attempt.
                    val password = pendingPassword ?: error("Session expired. Please sign in again.")
                    val signInResult = signInWithRetry(pending.email, password)
                    if (signInResult.isSuccess) {
                        val auth = signInResult.getOrThrow()
                        saveSupabaseTokens(auth.access_token, auth.refresh_token)
                        val userId = auth.user?.id ?: pending.userId
                        val email = auth.user?.email ?: pending.email
                        LocalDataStore.setString(LocalDataStore.KEY_AUTH_EMAIL, email)
                        LocalDataStore.setString(LocalDataStore.KEY_AUTH_USER_ID, userId)
                        LocalDataStore.setBoolean(LocalDataStore.KEY_LOCAL_AUTH_MIGRATED, true)
                        _needsMigration.value = false

                        // Upsert profile.
                        scope.launch {
                            SupabaseAuthClient.upsertProfile(
                                auth.access_token, userId, "Rock Scout", "\uD83E\uDD20"
                            )
                        }

                        _sessionStatus.value = SessionStatus.Authenticated(
                            Session(user = UserInfo(id = userId, email = email))
                        )
                        scope.launch { PurchaseManager.instance.linkRevenueCatUser(userId) }
                        SupabaseDataSync.syncInBackground()
                        // Cloud-restore settings if this is a fresh install.
                        restoreSettingsFromCloudIfNeeded(userId)
                    } else {
                        val cause = signInResult.exceptionOrNull()?.message.orEmpty()
                        Log.w("AuthRepository", "Post-verification sign-in failed: $cause")
                        if (cause.contains("not confirmed", ignoreCase = true)) {
                            error("Your code was correct, but the account couldn't be activated. Please tap Resend and try once more.")
                        } else {
                            error("Email verified, but couldn't sign in. Please try signing in manually.")
                        }
                    }
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

    /**
     * Sign in, retrying briefly while Supabase propagates the admin email
     * confirmation that was just applied by the verification endpoint.
     */
    private suspend fun signInWithRetry(
        email: String,
        password: String,
    ): Result<SupabaseAuthClient.AuthResponse> {
        var last: Result<SupabaseAuthClient.AuthResponse> =
            SupabaseAuthClient.signInWithPassword(email, password)
        var delayMs = 400L
        repeat(3) {
            if (last.isSuccess) return last
            val message = last.exceptionOrNull()?.message.orEmpty()
            if (!message.contains("not confirmed", ignoreCase = true)) return last
            kotlinx.coroutines.delay(delayMs)
            delayMs *= 2
            last = SupabaseAuthClient.signInWithPassword(email, password)
        }
        return last
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

    /** Cancel the pending verification and go back to the sign-up form. */
    suspend fun cancelPendingVerification(): Result<Unit> {
        return runCatching {
            pendingSupabaseUserId = null
            pendingPassword = null
            clearPendingVerifyPersistence()
            // Clear any partial Supabase session from sign-up.
            clearSupabaseTokens()
            _sessionStatus.value = SessionStatus.NotAuthenticated()
            _error.value = null
            Unit
        }
    }

    /**
     * Complete email verification from the click-to-verify deep link.
     *
     * Called when the app receives a `rockscout://verify_email?email=…&verified=true`
     * deep link. The backend has already confirmed the Supabase email — we just
     * need to sign in with the stored credentials to get a fresh session.
     *
     * If the app was killed between sending the email and clicking the link,
     * the pending email/password/userId are restored from [LocalDataStore].
     *
     * Returns true on success, false on failure (with error set).
     */
    suspend fun completeVerificationFromLink(linkEmail: String): Boolean {
        val email = linkEmail.trim().lowercase()

        // Restore pending state from memory or persistence.
        val pending = sessionStatus.value as? SessionStatus.PendingVerification
        val storedEmail = pending?.email
            ?: LocalDataStore.getString(LocalDataStore.KEY_PENDING_VERIFY_EMAIL)
        val storedPassword = pending?.let { pendingPassword }
            ?: LocalDataStore.getString(LocalDataStore.KEY_PENDING_VERIFY_PASSWORD)
        val storedUserId = pending?.userId?.takeIf { it.isNotBlank() }
            ?: LocalDataStore.getString(LocalDataStore.KEY_PENDING_VERIFY_USER_ID)

        // If the deep link email doesn't match the pending email, something is
        // wrong — don't sign in as the wrong user.
        if (storedEmail != null && storedEmail != email) {
            Log.w("AuthRepository", "verify-email link email mismatch: $email vs $storedEmail")
            _error.value = "This verification link doesn't match your account. Please use the link from your own email."
            return false
        }

        if (storedEmail == null || storedPassword.isNullOrBlank()) {
            Log.w("AuthRepository", "verify-email link but no pending credentials")
            // Transition to PendingVerification so the user can at least see
            // the verification screen and resend.
            _sessionStatus.value = SessionStatus.PendingVerification(
                email = email,
                userId = storedUserId ?: "",
            )
            _error.value = "Your email was verified, but we need your password to sign in. Please sign in manually."
            return false
        }

        _isLoading.value = true
        _error.value = null

        // Make sure we're in PendingVerification so the UI shows the right state.
        if (pending == null) {
            pendingSupabaseUserId = storedUserId
            pendingPassword = storedPassword
            _sessionStatus.value = SessionStatus.PendingVerification(
                email = storedEmail,
                userId = storedUserId ?: "",
            )
        }

        return runCatching {
            // The backend already confirmed the email via the admin API, so
            // sign-in should work immediately. Retry briefly in case Supabase
            // hasn't propagated the confirmation yet.
            val signInResult = signInWithRetry(storedEmail, storedPassword)
            if (signInResult.isSuccess) {
                val auth = signInResult.getOrThrow()
                saveSupabaseTokens(auth.access_token, auth.refresh_token)
                val userId = auth.user?.id ?: storedUserId ?: ""
                val userEmail = auth.user?.email ?: storedEmail
                LocalDataStore.setString(LocalDataStore.KEY_AUTH_EMAIL, userEmail)
                LocalDataStore.setString(LocalDataStore.KEY_AUTH_USER_ID, userId)
                LocalDataStore.setBoolean(LocalDataStore.KEY_LOCAL_AUTH_MIGRATED, true)
                _needsMigration.value = false

                // Upsert profile.
                scope.launch {
                    SupabaseAuthClient.upsertProfile(
                        auth.access_token, userId, "Rock Scout", "\uD83E\uDD20"
                    )
                }

                _sessionStatus.value = SessionStatus.Authenticated(
                    Session(user = UserInfo(id = userId, email = userEmail))
                )
                scope.launch { PurchaseManager.instance.linkRevenueCatUser(userId) }
                SupabaseDataSync.syncInBackground()
                // Cloud-restore settings if this is a fresh install.
                restoreSettingsFromCloudIfNeeded(userId)
                clearPendingVerifyPersistence()
                true
            } else {
                val cause = signInResult.exceptionOrNull()?.message.orEmpty()
                Log.w("AuthRepository", "verify-email sign-in failed: $cause")
                _error.value = "Your email is verified, but we couldn't sign you in automatically. Please sign in with your email and password."
                false
            }
        }.onFailure {
            _error.value = it.message ?: "Verification failed"
            Log.e("AuthRepository", "completeVerificationFromLink failed", it)
        }.getOrDefault(false).also {
            _isLoading.value = false
        }
    }

    /** Clear persisted pending-verification state. */
    private fun clearPendingVerifyPersistence() {
        LocalDataStore.setString(LocalDataStore.KEY_PENDING_VERIFY_EMAIL, "")
        LocalDataStore.setString(LocalDataStore.KEY_PENDING_VERIFY_PASSWORD, "")
        LocalDataStore.setString(LocalDataStore.KEY_PENDING_VERIFY_USER_ID, "")
    }

    /** Sign in with email + password via Supabase. */
    suspend fun signIn(email: String, password: String): Result<Unit> {
        _isLoading.value = true
        _error.value = null
        return runCatching {
            val trimmed = email.trim()
            val result = SupabaseAuthClient.signInWithPassword(trimmed, password)

            if (result.isFailure) {
                val errMsg = result.exceptionOrNull()?.message ?: "Sign-in failed"
                // Check if the error is "email not confirmed" — if so, transition
                // to PendingVerification so the user can verify via our 6-digit code.
                if (errMsg.contains("not confirmed", ignoreCase = true) ||
                    errMsg.contains("email_not_confirmed", ignoreCase = true)
                ) {
                    pendingPassword = password
                    _sessionStatus.value = SessionStatus.PendingVerification(
                        email = trimmed,
                        userId = "",
                    )
                    val sendResult = EmailVerificationApi.sendCode(trimmed)
                    if (sendResult is EmailVerificationApi.VerificationResult.Failed) {
                        Log.w("AuthRepository", "Sign-in verification code send failed: ${sendResult.message}")
                    }
                    return@runCatching Unit
                }
                error(errMsg)
            }

            val auth = result.getOrThrow()
            saveSupabaseTokens(auth.access_token, auth.refresh_token)
            val userId = auth.user?.id ?: ""
            val userEmail = auth.user?.email ?: trimmed

            LocalDataStore.setString(LocalDataStore.KEY_AUTH_EMAIL, userEmail)
            LocalDataStore.setString(LocalDataStore.KEY_AUTH_USER_ID, userId)
            LocalDataStore.setBoolean(LocalDataStore.KEY_LOCAL_AUTH_MIGRATED, true)
            _needsMigration.value = false

            // Check local blocked list (admin-deleted accounts).
            val blockedEmails = getBlockedEmails()
            if (userEmail.lowercase() in blockedEmails) {
                _sessionStatus.value = SessionStatus.AccountDeleted(
                    email = userEmail,
                    userId = userId,
                    reason = "Your account has been deleted by an administrator.",
                )
                return@runCatching Unit
            }

            // Upsert profile (ensures the user has a row for web/other platforms).
            scope.launch {
                SupabaseAuthClient.upsertProfile(auth.access_token, userId, "Rock Scout", "\uD83E\uDD20")
            }

            _sessionStatus.value = SessionStatus.Authenticated(
                Session(user = UserInfo(id = userId, email = userEmail))
            )
            scope.launch { PurchaseManager.instance.linkRevenueCatUser(userId) }
            SupabaseDataSync.syncInBackground()

            // Cloud-restore settings on fresh install.
            restoreSettingsFromCloudIfNeeded(userId)
            Unit
        }.onFailure {
            _error.value = it.message ?: "Sign-in failed"
            Log.e("AuthRepository", "signIn failed", it)
        }.also {
            _isLoading.value = false
        }
    }

    /** Sign out and clear the Supabase session. */
    suspend fun signOut(): Result<Unit> {
        _isLoading.value = true
        return runCatching {
            val accessToken = LocalDataStore.getString(LocalDataStore.KEY_SUPABASE_ACCESS_TOKEN)
            if (!accessToken.isNullOrBlank()) {
                SupabaseAuthClient.signOut(accessToken)
            }
            PurchaseManager.instance.logoutRevenueCatUser()
            clearSupabaseTokens()
            LocalDataStore.setString(LocalDataStore.KEY_AUTH_EMAIL, "")
            LocalDataStore.setString(LocalDataStore.KEY_AUTH_USER_ID, "")
            ErrorReporter.setUserId(null)
            _sessionStatus.value = SessionStatus.NotAuthenticated()
            Unit
        }.onFailure {
            Log.e("AuthRepository", "signOut failed", it)
        }.also {
            _isLoading.value = false
        }
    }

    /**
     * Delete the currently signed-in account and all associated data.
     * Notifies the backend to delete the Supabase auth user (which cascades
     * to all Supabase tables via foreign keys), wipes local data, logs out
     * RevenueCat, and clears the session.
     */
    suspend fun deleteAccount(): Result<Unit> {
        _isLoading.value = true
        return runCatching {
            val userId = currentUserId ?: error("No signed-in account to delete.")
            val userEmail = currentUserEmail
            val accessToken = LocalDataStore.getString(LocalDataStore.KEY_SUPABASE_ACCESS_TOKEN)

            // Notify the backend to delete the Supabase user.
            if (!userEmail.isNullOrBlank()) {
                scope.launch { DeleteAccountApi.notifyDeletionWithEmail(userEmail, userId, accessToken) }
            }

            // Wipe all user-generated app state and local social tables.
            PersistenceManager.clearAll()
            LocalDataStore.clearAll()
            PurchaseManager.instance.logoutRevenueCatUser()
            clearSupabaseTokens()
            _sessionStatus.value = SessionStatus.NotAuthenticated()
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

    /**
     * Admin: block a user's account by [userId] with the given [reason].
     * Adds the user's email to the local blocked list and notifies the backend
     * to disable the Supabase user. If the blocked user is currently signed in,
     * their session is set to AccountDeleted.
     */
    suspend fun adminDeleteAccount(userId: String, reason: String): Result<Unit> {
        return runCatching {
            // Look up the user in the local users table (mock community members).
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
            val user = users.firstOrNull { it.id == userId }
            val now = System.currentTimeMillis()

            // Log the deletion.
            val logEntry = LocalDeletedAccountLog(
                id = "dellog-" + UUID.randomUUID(),
                user_id = userId,
                username = user?.display_name ?: "Unknown",
                email = user?.email ?: "",
                reason = reason,
                deleted_at = now,
            )
            LocalDataStore.updateTable<LocalDeletedAccountLog>(LocalDataStore.KEY_DELETED_ACCOUNT_LOGS) { rows ->
                listOf(logEntry) + rows
            }

            // Add to local blocked list.
            if (user != null && user.email.isNotBlank()) {
                addBlockedEmail(user.email)
            }

            // If the blocked user is currently signed in, show the blocking popup.
            if (currentUserId == userId) {
                _sessionStatus.value = SessionStatus.AccountDeleted(
                    email = user?.email ?: currentUserEmail ?: "",
                    userId = userId,
                    reason = reason,
                )
            }
            Unit
        }.onFailure {
            Log.e("AuthRepository", "adminDeleteAccount failed", it)
        }
    }

    /** Admin: restore a previously blocked user's account by [userId]. */
    suspend fun adminRestoreAccount(userId: String): Result<Unit> {
        return runCatching {
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
            val user = users.firstOrNull { it.id == userId }
            val now = System.currentTimeMillis()

            // Remove from local blocked list.
            if (user != null && user.email.isNotBlank()) {
                removeBlockedEmail(user.email)
            }

            // Update the deletion log.
            LocalDataStore.updateTable<LocalDeletedAccountLog>(LocalDataStore.KEY_DELETED_ACCOUNT_LOGS) { rows ->
                rows.map { if (it.user_id == userId && it.restored_at == null) it.copy(restored_at = now) else it }
            }

            // If the user is currently in AccountDeleted state, restore their session.
            val deletedStatus = sessionStatus.value as? SessionStatus.AccountDeleted
            if (deletedStatus != null && deletedStatus.userId == userId) {
                _sessionStatus.value = SessionStatus.NotAuthenticated()
            }
            Unit
        }.onFailure {
            Log.e("AuthRepository", "adminRestoreAccount failed", it)
        }
    }

    // ─── Supabase token management ─────────────────────────────────────────

    private fun saveSupabaseTokens(accessToken: String, refreshToken: String) {
        LocalDataStore.setString(LocalDataStore.KEY_SUPABASE_ACCESS_TOKEN, accessToken)
        LocalDataStore.setString(LocalDataStore.KEY_SUPABASE_REFRESH_TOKEN, refreshToken)
    }

    private fun clearSupabaseTokens() {
        LocalDataStore.setString(LocalDataStore.KEY_SUPABASE_ACCESS_TOKEN, "")
        LocalDataStore.setString(LocalDataStore.KEY_SUPABASE_REFRESH_TOKEN, "")
    }

    // ─── Local blocked-emails list (admin deletions) ───────────────────────

    private fun getBlockedEmails(): Set<String> {
        val raw = LocalDataStore.getString("blocked_emails") ?: return emptySet()
        return try {
            LocalDataStore.json.decodeFromString<List<String>>(raw).toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    private fun addBlockedEmail(email: String) {
        val emails = getBlockedEmails().toMutableSet()
        emails.add(email.lowercase())
        LocalDataStore.setString(
            "blocked_emails",
            LocalDataStore.json.encodeToString(emails.toList()),
        )
    }

    private fun removeBlockedEmail(email: String) {
        val emails = getBlockedEmails().toMutableSet()
        emails.remove(email.lowercase())
        LocalDataStore.setString(
            "blocked_emails",
            LocalDataStore.json.encodeToString(emails.toList()),
        )
    }

    // ─── Cloud settings restore (re-install / new device) ──────────────────

    /**
     * Fetches the user's backed-up settings from the backend and restores them
     * to local SharedPreferences, but ONLY when the local data appears empty
     * (fresh install, re-install on a new device, or after the signing-conflict
     * uninstall flow).
     *
     * This is called from every path that establishes an authenticated session:
     * - [restoreSupabaseSession] (auto-login from stored tokens)
     * - [signIn] (manual sign-in)
     * - [completeMigration] (local-to-Supabase migration)
     * - [completeVerification] (6-digit code verification)
     * - [completeVerificationFromLink] (click-to-verify deep link)
     *
     * If local data already exists (returning user on the same device), the
     * restore is skipped — the user already has their data.
     *
     * Fire-and-forget: runs on a background coroutine so it never blocks the
     * auth flow. Failures are logged but never surface to the user.
     */
    private fun restoreSettingsFromCloudIfNeeded(userId: String) {
        if (userId.isBlank()) return
        if (!PersistenceManager.isLocalDataEmpty()) {
            Log.d("AuthRepository", "Skipping cloud restore — local data already exists")
            return
        }
        // Need the user's Supabase access token for RLS-authenticated reads.
        val accessToken = LocalDataStore.getString(LocalDataStore.KEY_SUPABASE_ACCESS_TOKEN)
        if (accessToken.isNullOrBlank()) {
            Log.d("AuthRepository", "Skipping cloud restore — no access token available")
            return
        }
        scope.launch {
            SettingsBackupApi.restoreSettings(userId, accessToken)
                .onSuccess { settingsJson ->
                    if (settingsJson != null) {
                        val restored = PersistenceManager.restoreAllSettingsFromJson(settingsJson)
                        if (restored) {
                            Log.d("AuthRepository", "Restored settings from cloud backup for user $userId")
                            PersistenceManager.reloadIntoRepository()
                        } else {
                            Log.d("AuthRepository", "Cloud backup was empty or invalid for user $userId")
                        }
                    } else {
                        Log.d("AuthRepository", "No cloud backup found for user $userId")
                    }
                }
                .onFailure { Log.w("AuthRepository", "Settings restore failed: ${it.message}") }
        }
    }

    companion object {
        val instance: AuthRepository by lazy { AuthRepository() }
    }
}

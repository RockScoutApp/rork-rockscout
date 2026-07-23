package com.rork.rockscout.data

/**
 * Local replacement for Supabase's SessionStatus sealed class.
 *
 * The app is fully self-contained — no Supabase backend. Auth is local
 * (email + password stored in [LocalDataStore]). This sealed class mirrors
 * the shape of the Supabase SDK's SessionStatus so UI code that checks
 * `sessionStatus is SessionStatus.Authenticated` and accesses
 * `.session?.user?.email` continues to work unchanged.
 */
sealed class SessionStatus {
    /** User is signed in with a valid local session. */
    data class Authenticated(val session: Session) : SessionStatus()
    /** User is not signed in. */
    data class NotAuthenticated(val message: String? = null) : SessionStatus()
    /** Auth state is being restored from local storage. */
    data object Initializing : SessionStatus()
    /** Session is being refreshed (no-op in local mode). */
    data class Refreshing(val session: Session) : SessionStatus()
    /** Account created but email not yet verified. The user must enter the
     *  6-digit code sent to [email] before gaining access. */
    data class PendingVerification(val email: String, val userId: String) : SessionStatus()
    /** Account has been admin-deleted. The user sees a blocking popup they
     *  cannot get past. [reason] is shown to the user. */
    data class AccountDeleted(val email: String, val userId: String, val reason: String) : SessionStatus()
}

/** Local session containing the signed-in user's info. */
data class Session(
    val user: UserInfo,
    val accessToken: String = "local-access-token",
    val refreshToken: String = "local-refresh-token",
    val expiresIn: Long = Long.MAX_VALUE,
    val expiresAt: Long? = null,
)

/** Signed-in user's identity. */
data class UserInfo(
    val id: String,
    val email: String,
)

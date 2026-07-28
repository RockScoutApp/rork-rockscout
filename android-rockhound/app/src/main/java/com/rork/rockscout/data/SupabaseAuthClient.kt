package com.rork.rockscout.data

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Supabase auth REST API client.
 *
 * Handles sign-up, sign-in (password grant), session refresh, user retrieval,
 * sign-out, and profile upsert via Supabase's REST auth endpoints. Used by
 * [AuthRepository] to provide cross-platform shared accounts (Android + web + iOS)
 * backed by the same Supabase project.
 *
 * Uses the shared [NetworkClient] Ktor client for connection pooling and retry.
 * The Supabase URL and anon key are resolved from [BuildSecrets].
 */
object SupabaseAuthClient {

    private const val TAG = "SupabaseAuthClient"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val client = NetworkClient.client

    private fun baseUrl(): String =
        BuildSecrets.resolve("EXPO_PUBLIC_SUPABASE_URL", BuildSecrets.SUPABASE_URL)

    private fun anonKey(): String =
        BuildSecrets.resolve("EXPO_PUBLIC_SUPABASE_ANON_KEY", BuildSecrets.SUPABASE_ANON_KEY)

    @Serializable
    data class SupabaseUser(
        val id: String = "",
        val email: String = "",
        val email_confirmed_at: String? = null,
        val created_at: String? = null,
    )

    @Serializable
    data class AuthResponse(
        val access_token: String = "",
        val refresh_token: String = "",
        val expires_in: Long = 0,
        val token_type: String = "bearer",
        val user: SupabaseUser? = null,
    )

    @Serializable
    private data class ErrorResponse(
        val error: String? = null,
        val error_description: String? = null,
        val msg: String? = null,
        val message: String? = null,
        val code: String? = null,
    )

    @Serializable
    private data class SignupBody(val email: String, val password: String)

    @Serializable
    private data class RefreshBody(val refresh_token: String)

    @Serializable
    private data class ProfileUpsert(
        val id: String,
        val display_name: String,
        val avatar_emoji: String,
    )

    /**
     * Sign up with email + password.
     * Returns an [AuthResponse] that may or may not include a session
     * (depends on whether email confirmation is enabled in Supabase).
     */
    suspend fun signUp(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = client.post("${baseUrl()}/auth/v1/signup") {
                header("apikey", anonKey())
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(SignupBody.serializer(), SignupBody(email, password)))
            }
            if (response.status.isSuccess()) {
                val body = response.body<String>()
                val parsed = json.decodeFromString(AuthResponse.serializer(), body)
                Log.i(TAG, "Sign-up successful for $email, user=${parsed.user?.id}")
                Result.success(parsed)
            } else {
                val err = parseError(response)
                Log.w(TAG, "Sign-up failed: $err")
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Sign-up network error: ${e.message}")
            Result.failure(Exception("Network error. Check your connection and try again."))
        }
    }

    /**
     * Sign in with email + password (password grant).
     * Returns an [AuthResponse] with access + refresh tokens.
     */
    suspend fun signInWithPassword(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = client.post("${baseUrl()}/auth/v1/token?grant_type=password") {
                header("apikey", anonKey())
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(SignupBody.serializer(), SignupBody(email, password)))
            }
            if (response.status.isSuccess()) {
                val body = response.body<String>()
                val parsed = json.decodeFromString(AuthResponse.serializer(), body)
                Log.i(TAG, "Sign-in successful for $email, user=${parsed.user?.id}")
                Result.success(parsed)
            } else {
                val err = parseError(response)
                Log.w(TAG, "Sign-in failed: $err")
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Sign-in network error: ${e.message}")
            Result.failure(Exception("Network error. Check your connection and try again."))
        }
    }

    /**
     * Get the current user from an access token.
     * Used during session restoration on app cold-start.
     */
    suspend fun getUser(accessToken: String): Result<SupabaseUser> {
        return try {
            val response = client.get("${baseUrl()}/auth/v1/user") {
                header("apikey", anonKey())
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) {
                val body = response.body<String>()
                val parsed = json.decodeFromString(SupabaseUser.serializer(), body)
                Result.success(parsed)
            } else {
                Result.failure(Exception("Session expired"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error"))
        }
    }

    /**
     * Refresh the session using a refresh token.
     * Returns a new [AuthResponse] with fresh tokens.
     */
    suspend fun refreshSession(refreshToken: String): Result<AuthResponse> {
        return try {
            val response = client.post("${baseUrl()}/auth/v1/token?grant_type=refresh_token") {
                header("apikey", anonKey())
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(RefreshBody.serializer(), RefreshBody(refreshToken)))
            }
            if (response.status.isSuccess()) {
                val body = response.body<String>()
                val parsed = json.decodeFromString(AuthResponse.serializer(), body)
                Log.i(TAG, "Session refreshed for user=${parsed.user?.id}")
                Result.success(parsed)
            } else {
                Result.failure(Exception("Refresh failed"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error"))
        }
    }

    /**
     * Sign out (revoke the session). Best-effort — does not throw.
     */
    suspend fun signOut(accessToken: String) {
        try {
            client.post("${baseUrl()}/auth/v1/logout") {
                header("apikey", anonKey())
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            Log.i(TAG, "Sign-out successful")
        } catch (e: Exception) {
            Log.w(TAG, "Sign-out error: ${e.message}")
        }
    }

    /**
     * Upsert a profile row in rockscout_profiles.
     * Creates the row if missing (first sign-in), does nothing if it already exists
     * (merge-duplicates resolution). Ensures the user has a profile for the web PWA
     * and other platforms to read.
     */
    suspend fun upsertProfile(accessToken: String, userId: String, displayName: String, avatarEmoji: String): Result<Unit> {
        return try {
            val response = client.post("${baseUrl()}/rest/v1/rockscout_profiles") {
                header("apikey", anonKey())
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                contentType(ContentType.Application.Json)
                header("Prefer", "resolution=merge-duplicates,return=minimal")
                setBody(json.encodeToString(ProfileUpsert.serializer(), ProfileUpsert(
                    id = userId,
                    display_name = displayName,
                    avatar_emoji = avatarEmoji,
                )))
            }
            if (response.status.isSuccess()) {
                Log.i(TAG, "Profile upserted for user=$userId")
                Result.success(Unit)
            } else {
                val err = parseError(response)
                Log.w(TAG, "Profile upsert failed: $err")
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Profile upsert error: ${e.message}")
            Result.failure(e)
        }
    }

    private suspend fun parseError(response: HttpResponse): String {
        return try {
            val body = response.body<String>()
            val parsed = json.decodeFromString(ErrorResponse.serializer(), body)
            parsed.error_description ?: parsed.error ?: parsed.msg ?: parsed.message
                ?: "Request failed (${response.status.value})"
        } catch (e: Exception) {
            "Request failed (${response.status.value})"
        }
    }
}

package com.rork.rockscout.data

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Calls the backend `/entitlement` endpoint to sync the user's RevenueCat
 * Premium entitlement to the Supabase profile.
 *
 * The backend asks RevenueCat whether the user has an active Premium or
 * legacy Pro entitlement and writes `is_pro` back to the Supabase
 * `rockscout_profiles` row. This keeps the web PWA in sync when a user
 * buys Premium on Android.
 *
 * Fire-and-forget — failures are logged but never block the caller.
 */
object EntitlementApi {

    private const val TAG = "EntitlementApi"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val client = NetworkClient.client

    @Serializable
    private data class EntitlementRequest(val userId: String)

    @Serializable
    private data class EntitlementResponse(
        val ok: Boolean = false,
        val isPremium: Boolean = false,
        val supabaseUpdated: Boolean = false,
    )

    /**
     * Sync the user's RevenueCat entitlement to Supabase.
     * Silently fails on network errors — this is a best-effort sync.
     * Returns true if the sync succeeded, false otherwise.
     */
    suspend fun syncEntitlement(userId: String): Boolean {
        if (userId.isBlank()) return false
        return try {
            val baseUrl = BuildSecrets.resolve("EXPO_PUBLIC_RORK_FUNCTIONS_URL", BuildSecrets.RORK_FUNCTIONS_URL)
                .ifBlank { null } ?: return false
            val appKey = BuildSecrets.resolve("EXPO_PUBLIC_RORK_APP_KEY", BuildSecrets.RORK_APP_KEY)

            val response = client.post("$baseUrl/entitlement") {
                contentType(ContentType.Application.Json)
                if (appKey.isNotBlank()) header("X-App-Key", appKey)
                setBody(json.encodeToString(EntitlementRequest.serializer(), EntitlementRequest(userId)))
            }

            val body = response.body<String>()
            val parsed = json.decodeFromString(EntitlementResponse.serializer(), body)
            if (parsed.ok) {
                Log.i(TAG, "Entitlement synced for user=$userId: isPremium=${parsed.isPremium}, supabaseUpdated=${parsed.supabaseUpdated}")
                true
            } else {
                Log.w(TAG, "Entitlement sync returned ok=false for user=$userId")
                false
            }
        } catch (e: Exception) {
            Log.w(TAG, "Entitlement sync failed: ${e.message}")
            false
        }
    }
}

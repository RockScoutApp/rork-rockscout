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
 * Fire-and-forget backend receipt for an account deletion.
 *
 * The real deletion happens on the device via [AuthRepository.deleteAccount] —
 * this call just records the request on the server so there is a server-side
 * audit trail and any future server-side copies can be purged from the same
 * endpoint.
 */
object DeleteAccountApi {

    private const val TAG = "DeleteAccountApi"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val client = NetworkClient.client

    private val APP_KEY: String =
        BuildSecrets.resolve("EXPO_PUBLIC_RORK_APP_KEY", BuildSecrets.RORK_APP_KEY)

    @Serializable
    private data class DeleteAccountRequest(
        val email: String,
        val userId: String? = null,
        val accessToken: String? = null,
    )

    @Serializable
    private data class DeleteAccountResponse(val success: Boolean = false)

    /**
     * Records the deletion request for [email]. Silently fails on network errors
     * so the in-app deletion path is never blocked by the backend.
     */
    suspend fun notifyDeletion(email: String) {
        notifyDeletionWithEmail(email, null, null)
    }

    /**
     * Records the deletion request for [email], optionally passing the Supabase
     * [userId] and [accessToken] so the backend can delete the Supabase auth user
     * (which cascades to all Supabase tables via foreign keys).
     * Silently fails on network errors so the in-app deletion path is never blocked.
     */
    suspend fun notifyDeletionWithEmail(email: String, userId: String?, accessToken: String?) {
        try {
            val baseUrl = BuildSecrets.resolve("EXPO_PUBLIC_RORK_FUNCTIONS_URL", BuildSecrets.RORK_FUNCTIONS_URL)
                .ifBlank { null } ?: return
            val response = client.post("$baseUrl/delete-account") {
                contentType(ContentType.Application.Json)
                if (APP_KEY.isNotBlank()) header("X-App-Key", APP_KEY)
                setBody(
                    json.encodeToString(
                        DeleteAccountRequest.serializer(),
                        DeleteAccountRequest(
                            email = email,
                            userId = userId,
                            accessToken = accessToken,
                        ),
                    ),
                )
            }
            val body = response.body<String>()
            val parsed = json.decodeFromString(DeleteAccountResponse.serializer(), body)
            if (parsed.success) {
                Log.i(TAG, "Delete-account request recorded for $email")
            } else {
                Log.w(TAG, "Delete-account backend did not record success")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Delete-account notification failed: ${e.message}")
        }
    }
}

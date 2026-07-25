package com.rork.rockscout.data

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Backend call to send a personalized welcome email to a newly signed-up user
 * via the Cloudflare Worker `/welcome-email` endpoint (which uses Resend).
 *
 * Fire-and-forget — failures are logged but never surfaced to the user.
 */
object WelcomeEmailApi {

    private const val TAG = "WelcomeEmailApi"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val client = NetworkClient.client

    @Serializable
    private data class WelcomeEmailRequest(
        val email: String,
        val displayName: String? = null,
    )

    @Serializable
    private data class WelcomeEmailResponse(val ok: Boolean = false)

    /**
     * Sends a welcome email to [email] (optionally addressed to [displayName]).
     * Silently fails on network errors — the user never sees an error from this.
     */
    suspend fun sendWelcomeEmail(email: String, displayName: String? = null) {
        try {
            val baseUrl = BuildSecrets.resolve("EXPO_PUBLIC_RORK_FUNCTIONS_URL", BuildSecrets.RORK_FUNCTIONS_URL)
                .ifBlank { null } ?: return
            val response = client.post("$baseUrl/welcome-email") {
                contentType(ContentType.Application.Json)
                setBody(
                    json.encodeToString(
                        WelcomeEmailRequest.serializer(),
                        WelcomeEmailRequest(email = email, displayName = displayName),
                    ),
                )
            }
            val body = response.body<String>()
            val parsed = json.decodeFromString(WelcomeEmailResponse.serializer(), body)
            if (parsed.ok) {
                Log.i(TAG, "Welcome email sent to $email")
            } else {
                Log.w(TAG, "Welcome email not sent (backend returned ok=false)")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Welcome email failed: ${e.message}")
        }
    }
}

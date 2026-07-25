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
 * Backend calls to the Cloudflare Worker `/email-verification` endpoint
 * for sending and verifying 6-digit email confirmation codes during sign-up.
 *
 * Uses the same Resend email infrastructure as the welcome email.
 */
object EmailVerificationApi {

    private const val TAG = "EmailVerificationApi"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val client = NetworkClient.client

    @Serializable
    private data class SendCodeRequest(
        val action: String,
        val email: String,
    )

    @Serializable
    private data class VerifyCodeRequest(
        val action: String,
        val email: String,
        val code: String,
    )

    @Serializable
    private data class VerificationResponse(
        val ok: Boolean = false,
        val verified: Boolean = false,
        val error: String? = null,
    )

    /** Result of a verification API call. */
    sealed class VerificationResult {
        data object Success : VerificationResult()
        data class Failed(val message: String) : VerificationResult()
        data object NetworkError : VerificationResult()
    }

    private fun baseUrl(): String? =
        BuildSecrets.resolve("EXPO_PUBLIC_RORK_FUNCTIONS_URL", BuildSecrets.RORK_FUNCTIONS_URL)
            .ifBlank { null }

    /**
     * Sends a 6-digit verification code to [email] via the backend.
     * Returns true if the email was sent successfully.
     */
    suspend fun sendCode(email: String): VerificationResult {
        val url = baseUrl() ?: return VerificationResult.NetworkError
        return try {
            val response = client.post("$url/email-verification") {
                contentType(ContentType.Application.Json)
                setBody(
                    json.encodeToString(
                        SendCodeRequest.serializer(),
                        SendCodeRequest(action = "send", email = email),
                    ),
                )
            }
            val body = response.body<String>()
            val parsed = json.decodeFromString(VerificationResponse.serializer(), body)
            if (parsed.ok) {
                Log.i(TAG, "Verification code sent to $email")
                VerificationResult.Success
            } else {
                Log.w(TAG, "Send code failed: ${parsed.error}")
                VerificationResult.Failed(parsed.error ?: "Failed to send code")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Send code network error: ${e.message}")
            VerificationResult.NetworkError
        }
    }

    /**
     * Verifies the [code] for [email] against the backend.
     * Returns true if the code matched.
     */
    suspend fun verifyCode(email: String, code: String): VerificationResult {
        val url = baseUrl() ?: return VerificationResult.NetworkError
        return try {
            val response = client.post("$url/email-verification") {
                contentType(ContentType.Application.Json)
                setBody(
                    json.encodeToString(
                        VerifyCodeRequest.serializer(),
                        VerifyCodeRequest(action = "verify", email = email, code = code),
                    ),
                )
            }
            val body = response.body<String>()
            val parsed = json.decodeFromString(VerificationResponse.serializer(), body)
            if (parsed.ok && parsed.verified) {
                Log.i(TAG, "Email verified: $email")
                VerificationResult.Success
            } else if (!parsed.ok && parsed.error?.contains("expired") == true) {
                VerificationResult.Failed(parsed.error)
            } else if (!parsed.ok) {
                VerificationResult.Failed(parsed.error ?: "Invalid code")
            } else {
                VerificationResult.Failed("Verification failed")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Verify code network error: ${e.message}")
            VerificationResult.NetworkError
        }
    }
}

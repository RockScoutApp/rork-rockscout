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
        val supabaseUserId: String? = null,
    )

    @Serializable
    private data class VerificationResponse(
        val ok: Boolean = false,
        val verified: Boolean = false,
        val emailConfirmed: Boolean = false,
        val confirmReason: String? = null,
        val reason: String? = null,
        val error: String? = null,
    )

    /** Result of a verification API call. */
    sealed class VerificationResult {
        /**
         * The call succeeded. [emailConfirmed] is true when the backend also
         * marked the Supabase email as confirmed, which is what allows the
         * immediate sign-in that follows verification.
         */
        data class Success(val emailConfirmed: Boolean = true) : VerificationResult()
        data class Failed(val message: String) : VerificationResult()
        data object NetworkError : VerificationResult()
    }

    /** Turns a backend response into a message that is safe and useful to show. */
    private fun messageFor(parsed: VerificationResponse): String = when {
        !parsed.error.isNullOrBlank() -> parsed.error
        parsed.reason == "email_verification_not_configured" ->
            "Verification email service is temporarily unavailable. Please try again shortly."
        else -> "Verification failed. Please try again."
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
                Log.i(TAG, "Verification code sent")
                VerificationResult.Success()
            } else {
                Log.w(TAG, "Send code failed: ${parsed.reason ?: parsed.error}")
                VerificationResult.Failed(messageFor(parsed))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Send code network error: ${e.message}")
            VerificationResult.NetworkError
        }
    }

    /**
     * Verifies the [code] for [email] against the backend, and if a [supabaseUserId]
     * is provided, asks the backend to also confirm the Supabase email via admin API.
     * Returns true if the code matched.
     */
    suspend fun verifyCodeWithEmailConfirm(
        email: String,
        code: String,
        supabaseUserId: String? = null,
    ): VerificationResult {
        val url = baseUrl() ?: return VerificationResult.NetworkError
        return try {
            val response = client.post("$url/email-verification") {
                contentType(ContentType.Application.Json)
                setBody(
                    json.encodeToString(
                        VerifyCodeRequest.serializer(),
                        VerifyCodeRequest(
                            action = "verify",
                            email = email,
                            code = code,
                            supabaseUserId = supabaseUserId,
                        ),
                    ),
                )
            }
            val body = response.body<String>()
            val parsed = json.decodeFromString(VerificationResponse.serializer(), body)
            if (parsed.ok && parsed.verified) {
                if (!parsed.emailConfirmed) {
                    Log.w(TAG, "Code correct but Supabase confirm skipped: ${parsed.confirmReason}")
                }
                VerificationResult.Success(emailConfirmed = parsed.emailConfirmed)
            } else {
                VerificationResult.Failed(messageFor(parsed))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Verify code network error: ${e.message}")
            VerificationResult.NetworkError
        }
    }

    /** Verifies the [code] for [email] without touching the Supabase account. */
    suspend fun verifyCode(email: String, code: String): VerificationResult =
        verifyCodeWithEmailConfirm(email = email, code = code, supabaseUserId = null)
}

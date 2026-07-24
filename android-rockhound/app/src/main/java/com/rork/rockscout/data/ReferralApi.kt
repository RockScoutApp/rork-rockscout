package com.rork.rockscout.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Cross-device referral backend calls — stores the referral code -> sender
 * mapping and sends the invitation email, then verifies a code when a new user
 * signs up and enters it.
 *
 * The backend is the deployed Cloudflare Worker at the same domain used by the
 * identify endpoint. If the backend is unreachable or unconfigured, the app
 * falls back to local-only behavior (e.g. the Android email chooser for sending).
 */
object ReferralApi {

    private const val BASE_URL = "https://rockscout-finder-backend.rork.app"
    private const val TAG = "ReferralApi"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val client = NetworkClient.client

    @Serializable
    private data class SendReferralRequest(
        val code: String,
        val senderEmail: String,
        val recipientEmail: String = "",
        val senderName: String = "",
    )

    @Serializable
    private data class RegisterCodeRequest(
        val code: String,
        val senderEmail: String,
        val senderName: String = "",
    )

    @Serializable
    private data class RegisterCodeResponse(
        val ok: Boolean = false,
        val error: String? = null,
    )

    @Serializable
    private data class SendReferralResponse(
        val ok: Boolean = false,
        val error: String? = null,
    )

    @Serializable
    private data class VerifyReferralRequest(
        val code: String,
        val recipientEmail: String,
    )

    @Serializable
    private data class VerifyReferralResponse(
        val ok: Boolean = false,
        val senderEmail: String? = null,
        val error: String? = null,
    )

    @Serializable
    private data class CompleteReferralRequest(
        val code: String,
        val recipientEmail: String,
    )

    @Serializable
    private data class CompleteReferralResponse(
        val ok: Boolean = false,
        val senderEmail: String? = null,
        val error: String? = null,
    )

    @Serializable
    private data class CheckCompletionsRequest(
        val senderEmail: String,
    )

    @Serializable
    data class CompletionEntry(
        val code: String = "",
        val senderEmail: String = "",
        val recipientEmail: String = "",
        val completedAt: String = "",
    )

    @Serializable
    private data class CheckCompletionsResponse(
        val ok: Boolean = false,
        val completions: List<CompletionEntry> = emptyList(),
    )

    @Serializable
    private data class AcknowledgeRequest(
        val senderEmail: String,
        val code: String,
        val recipientEmail: String,
    )

    @Serializable
    private data class AcknowledgeResponse(
        val ok: Boolean = false,
    )

    /**
     * Register the referral code -> sender email mapping on the backend without
     * sending an email. This ensures the code is verifiable when a recipient
     * enters it, even if the sender shared it via the system share sheet
     * instead of the in-app email flow.
     *
     * Returns true on success, false on failure.
     */
    suspend fun registerCode(
        code: String,
        senderEmail: String,
        senderName: String = "",
    ): Boolean {
        return try {
            val response = client.post("$BASE_URL/referral/send") {
                contentType(ContentType.Application.Json)
                setBody(
                    json.encodeToString(
                        SendReferralRequest.serializer(),
                        SendReferralRequest(
                            code = code.uppercase(),
                            senderEmail = senderEmail,
                            recipientEmail = "",
                            senderName = senderName,
                        ),
                    ),
                )
            }
            val body = response.body<String>()
            val parsed = json.decodeFromString(SendReferralResponse.serializer(), body)
            parsed.ok
        } catch (e: Exception) {
            Log.w(TAG, "registerCode failed: ${e.message}")
            false
        }
    }

    /**
     * Upload the referral code -> sender email mapping and send the invitation
     * email to [recipientEmail]. Returns true on success, false on failure.
     */
    suspend fun sendReferralEmail(
        code: String,
        senderEmail: String,
        recipientEmail: String,
        senderName: String = "",
    ): Boolean {
        return try {
            val response = client.post("$BASE_URL/referral/send") {
                contentType(ContentType.Application.Json)
                setBody(
                    json.encodeToString(
                        SendReferralRequest.serializer(),
                        SendReferralRequest(
                            code = code.uppercase(),
                            senderEmail = senderEmail,
                            recipientEmail = recipientEmail,
                            senderName = senderName,
                        ),
                    ),
                )
            }
            val body = response.body<String>()
            val parsed = json.decodeFromString(SendReferralResponse.serializer(), body)
            parsed.ok
        } catch (e: Exception) {
            Log.w(TAG, "sendReferralEmail failed: ${e.message}")
            false
        }
    }

    /**
     * Verify a referral code entered by a new user. Returns the sender's email
     * if the code is valid and known by the backend, or null otherwise.
     */
    suspend fun verifyReferralCode(code: String, recipientEmail: String): String? {
        return try {
            val response = client.post("$BASE_URL/referral/verify") {
                contentType(ContentType.Application.Json)
                setBody(
                    json.encodeToString(
                        VerifyReferralRequest.serializer(),
                        VerifyReferralRequest(
                            code = code.uppercase(),
                            recipientEmail = recipientEmail,
                        ),
                    ),
                )
            }
            val body = response.body<String>()
            val parsed = json.decodeFromString(VerifyReferralResponse.serializer(), body)
            if (parsed.ok) parsed.senderEmail else null
        } catch (e: Exception) {
            Log.w(TAG, "verifyReferralCode failed: ${e.message}")
            null
        }
    }

    /**
     * Notify the backend that a referral has been completed — the new user has
     * signed in AND verified the code. This records the completion so the
     * sender can be credited when they next check for completions.
     *
     * Returns the sender's email on success, null on failure.
     */
    suspend fun completeReferral(code: String, recipientEmail: String): String? {
        return try {
            val response = client.post("$BASE_URL/referral/complete") {
                contentType(ContentType.Application.Json)
                setBody(
                    json.encodeToString(
                        CompleteReferralRequest.serializer(),
                        CompleteReferralRequest(
                            code = code.uppercase(),
                            recipientEmail = recipientEmail,
                        ),
                    ),
                )
            }
            val body = response.body<String>()
            val parsed = json.decodeFromString(CompleteReferralResponse.serializer(), body)
            if (parsed.ok) parsed.senderEmail else null
        } catch (e: Exception) {
            Log.w(TAG, "completeReferral failed: ${e.message}")
            null
        }
    }

    /**
     * Check for unacknowledged referral completions for this sender. Called by
     * the sender's app on sign-in to credit pending rewards. Returns the list
     * of completions, or an empty list on failure.
     */
    suspend fun checkCompletions(senderEmail: String): List<CompletionEntry> {
        return try {
            val response = client.post("$BASE_URL/referral/check-completions") {
                contentType(ContentType.Application.Json)
                setBody(
                    json.encodeToString(
                        CheckCompletionsRequest.serializer(),
                        CheckCompletionsRequest(senderEmail = senderEmail),
                    ),
                )
            }
            val body = response.body<String>()
            val parsed = json.decodeFromString(CheckCompletionsResponse.serializer(), body)
            if (parsed.ok) parsed.completions else emptyList()
        } catch (e: Exception) {
            Log.w(TAG, "checkCompletions failed: ${e.message}")
            emptyList()
        }
    }

    /**
     * Acknowledge that the sender's app has credited the reward for a completion.
     * Removes the completion from the backend so it won't be returned again.
     */
    suspend fun acknowledgeCompletion(
        senderEmail: String,
        code: String,
        recipientEmail: String,
    ): Boolean {
        return try {
            val response = client.post("$BASE_URL/referral/acknowledge") {
                contentType(ContentType.Application.Json)
                setBody(
                    json.encodeToString(
                        AcknowledgeRequest.serializer(),
                        AcknowledgeRequest(
                            senderEmail = senderEmail,
                            code = code.uppercase(),
                            recipientEmail = recipientEmail,
                        ),
                    ),
                )
            }
            val body = response.body<String>()
            val parsed = json.decodeFromString(AcknowledgeResponse.serializer(), body)
            parsed.ok
        } catch (e: Exception) {
            Log.w(TAG, "acknowledgeCompletion failed: ${e.message}")
            false
        }
    }
}

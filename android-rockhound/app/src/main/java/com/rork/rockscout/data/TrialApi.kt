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
 * Backend calls for the one-trial-per-device system.
 *
 * Checks whether a device has already used its free trial and claims it
 * so a reinstall on the same device cannot get a second trial.
 *
 * Falls back gracefully — if the backend is unreachable, the trial is
 * granted locally (optimistic) and the claim is retried on the next launch.
 */
object TrialApi {

    private const val TAG = "TrialApi"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val client = NetworkClient.client

    @Serializable
    private data class CheckTrialRequest(val deviceId: String)

    @Serializable
    private data class CheckTrialResponse(val trialUsed: Boolean = false)

    @Serializable
    private data class ClaimTrialRequest(val deviceId: String)

    @Serializable
    private data class ClaimTrialResponse(val ok: Boolean = false)

    /**
     * Check whether [deviceId] has already claimed a trial.
     * Returns true if the trial has been used, false if available or on network error.
     */
    suspend fun checkTrial(deviceId: String): Boolean {
        return try {
            val baseUrl = com.rork.rockscout.Config.allValues["EXPO_PUBLIC_RORK_FUNCTIONS_URL"]
                ?: return false
            val response = client.post("$baseUrl/trial/check") {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(CheckTrialRequest.serializer(), CheckTrialRequest(deviceId)))
            }
            val body = response.body<String>()
            val parsed = json.decodeFromString(CheckTrialResponse.serializer(), body)
            parsed.trialUsed
        } catch (e: Exception) {
            Log.w(TAG, "checkTrial failed: ${e.message}")
            false
        }
    }

    /**
     * Claim the trial for [deviceId] so a reinstall cannot get a second trial.
     * Fire-and-forget — failure is retried on the next launch.
     */
    suspend fun claimTrial(deviceId: String): Boolean {
        return try {
            val baseUrl = com.rork.rockscout.Config.allValues["EXPO_PUBLIC_RORK_FUNCTIONS_URL"]
                ?: return false
            val response = client.post("$baseUrl/trial/claim") {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(ClaimTrialRequest.serializer(), ClaimTrialRequest(deviceId)))
            }
            val body = response.body<String>()
            val parsed = json.decodeFromString(ClaimTrialResponse.serializer(), body)
            parsed.ok
        } catch (e: Exception) {
            Log.w(TAG, "claimTrial failed: ${e.message}")
            false
        }
    }
}

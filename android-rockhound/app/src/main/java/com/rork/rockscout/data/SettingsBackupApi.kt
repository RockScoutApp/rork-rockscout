package com.rork.rockscout.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Calls the /settings/backup and /settings/restore backend endpoints
 * to preserve user data across re-installs and new-device setups.
 *
 * Before the uninstall: [backupSettings] pushes the full SharedPreferences
 * JSON blob to Supabase keyed by the user's ID.
 *
 * After reinstall + sign-in: [restoreSettings] fetches the blob so
 * [PersistenceManager] can write it back to SharedPreferences.
 *
 * Both endpoints require the user's Supabase access token (Bearer header)
 * so RLS can enforce that users only read/write their own backup row.
 * No service-role key is needed — this is more secure and doesn't break
 * when the service key rotates.
 */
object SettingsBackupApi {

    private val BASE_URL: String =
        BuildSecrets.resolve("EXPO_PUBLIC_RORK_FUNCTIONS_URL", BuildSecrets.RORK_FUNCTIONS_URL)
            .removeSuffix("/")
    private val APP_KEY: String =
        BuildSecrets.resolve("EXPO_PUBLIC_RORK_APP_KEY", BuildSecrets.RORK_APP_KEY)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    @Serializable
    private data class RestoreResponse(val settingsJson: String? = null)

    private val client = HttpClient {
        install(HttpTimeout) {
            connectTimeoutMillis = 15_000
            requestTimeoutMillis = 30_000
            socketTimeoutMillis = 30_000
        }
    }

    /**
     * Push the settings JSON blob to the backend for cloud backup.
     *
     * @param userId The Supabase user ID (must match the token's auth.uid()).
     * @param settingsJson The full SharedPreferences export.
     * @param accessToken The user's Supabase access token (Bearer).
     */
    suspend fun backupSettings(
        userId: String,
        settingsJson: String,
        accessToken: String,
    ): Result<Unit> {
        return try {
            val requestBody = buildJsonObject {
                put("userId", userId)
                put("settingsJson", settingsJson)
            }.toString()

            val response = client.put("$BASE_URL/settings/backup") {
                contentType(ContentType.Application.Json)
                if (APP_KEY.isNotBlank()) header("X-App-Key", APP_KEY)
                header("Authorization", "Bearer $accessToken")
                setBody(requestBody)
            }

            if (response.status.value.toString().startsWith("2")) {
                Result.success(Unit)
            } else {
                val errorBody = response.body<String>()
                Result.failure(Exception("Backup failed: ${response.status} — $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch the settings JSON blob from the backend, or null if no backup exists.
     *
     * @param userId The Supabase user ID (must match the token's auth.uid()).
     * @param accessToken The user's Supabase access token (Bearer).
     */
    suspend fun restoreSettings(
        userId: String,
        accessToken: String,
    ): Result<String?> {
        return try {
            val response = client.get("$BASE_URL/settings/restore?userId=$userId") {
                if (APP_KEY.isNotBlank()) header("X-App-Key", APP_KEY)
                header("Authorization", "Bearer $accessToken")
            }

            if (response.status.value.toString().startsWith("2")) {
                val body = response.body<String>()
                val parsed = json.decodeFromString(RestoreResponse.serializer(), body)
                Result.success(parsed.settingsJson)
            } else {
                Result.failure(Exception("Restore failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

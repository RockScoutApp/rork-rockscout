package com.rork.rockscout.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Calls the /museums backend endpoint to find artifact-relevant museums
 * near the user's location. The backend queries OpenStreetMap Overpass
 * API and filters to natural history, anthropology, archaeology, tribal
 * cultural centers, and similar types.
 *
 * Used by the "Ask an Expert" feature on the artifact uncertainty card.
 */
object MuseumRepository {

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

    private val client = HttpClient {
        install(HttpTimeout) {
            connectTimeoutMillis = 15_000
            requestTimeoutMillis = 30_000
            socketTimeoutMillis = 30_000
        }
    }

    /**
     * Fetch museums near [lat]/[lon]. Returns the museum list and whether
     * the radius was expanded (no museums within the default 50 miles).
     */
    suspend fun fetchMuseums(
        lat: Double,
        lon: Double,
        radiusMiles: Int = 50,
    ): Result<MuseumResponse> {
        return try {
            val requestBody = buildJsonObject {
                put("lat", lat)
                put("lon", lon)
                put("radius", radiusMiles)
            }.toString()

            val response = client.post("$BASE_URL/museums") {
                contentType(ContentType.Application.Json)
                if (APP_KEY.isNotBlank()) header("X-App-Key", APP_KEY)
                setBody(requestBody)
            }

            if (!response.status.value.toString().startsWith("2")) {
                val errorBody = response.body<String>()
                Result.failure(Exception("Museum search failed: ${response.status} — $errorBody"))
            } else {
                val body = response.body<String>()
                val parsed = json.decodeFromString(MuseumResponse.serializer(), body)
                Result.success(parsed)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

package com.rork.rockscout.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Geocoding repository — converts a free-text location query into real-world
 * coordinates via the backend /geocode endpoint (Nominatim/OpenStreetMap).
 * Results include a display name, a formatted address, and lat/lng so the
 * submitted location can be used for directions.
 */
object GeocodeRepository {

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
            connectTimeoutMillis = 10_000
            requestTimeoutMillis = 20_000
            socketTimeoutMillis = 20_000
        }
    }

    @Serializable
    data class GeocodedLocation(
        val displayName: String,
        val address: String,
        val latitude: Double,
        val longitude: Double,
    )

    @Serializable
    private data class GeocodeResponse(
        val results: List<GeocodedLocation> = emptyList(),
    )

    /**
     * Search Nominatim for up to 5 matching places for the user's free-text query.
     */
    suspend fun search(query: String): Result<List<GeocodedLocation>> {
        return try {
            val requestBody = buildJsonObject {
                put("query", query.trim())
            }.toString()

            val response = client.post("$BASE_URL/geocode") {
                contentType(ContentType.Application.Json)
                if (APP_KEY.isNotBlank()) header("X-App-Key", APP_KEY)
                setBody(requestBody)
            }

            if (!response.status.value.toString().startsWith("2")) {
                val errorBody = response.body<String>()
                Result.failure(Exception("Geocoding failed: ${response.status} — $errorBody"))
            } else {
                val body = response.body<String>()
                val parsed = json.decodeFromString(GeocodeResponse.serializer(), body)
                Result.success(parsed.results)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

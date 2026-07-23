package com.rork.rockscout.data

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * Web verification service for user-submitted gem & mineral shows.
 *
 * Uses the Rork proxy's Exa search endpoint ([/v2/exa/search]) so the
 * server-side Exa key and toolkit billing path are used.
 *
 * Follows the [DigSiteSearchService] pattern exactly.
 */
object GemShowSearchService {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = NetworkClient.client

    @Serializable
    private data class ExaResult(
        val title: String? = null,
        val url: String? = null,
        val text: String? = null,
        val highlights: List<String>? = null,
    )

    @Serializable
    private data class ExaResponse(
        val results: List<ExaResult> = emptyList(),
    )

    /**
     * Web-verify a user-submitted gem show by searching for the show name
     * with gem/mineral/show keywords and the city/state. Returns true if
     * the search finds a result whose title or snippet mentions gem/mineral/
     * rock/fossil/show keywords AND matches the show name or city.
     *
     * Also returns the snippet and URL of the matching result via [onResult].
     */
    suspend fun verifyShow(
        name: String,
        city: String,
        state: String,
        website: String,
        onResult: ((snippet: String, url: String) -> Unit)? = null,
    ): Boolean = withContext(Dispatchers.IO) {
        val allVals = runCatching { com.rork.rockscout.Config.allValues }.getOrDefault(emptyMap())
        val toolkitUrl = allVals["EXPO_PUBLIC_TOOLKIT_URL"] ?: ""
        val secret = allVals["EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY"] ?: ""
        if (toolkitUrl.isBlank() || secret.isBlank()) return@withContext false

        val query = "$name gem mineral rock show $city $state"

        val requestBody = buildJsonObject {
            put("query", query)
            put("type", "auto")
            put("numResults", 5)
            put("contents", buildJsonObject {
                put("highlights", true)
                put("text", true)
            })
        }.toString()

        try {
            val proxyUrl = "$toolkitUrl/v2/exa/search"
            val response = client.post(proxyUrl) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
                headers.append("Authorization", "Bearer $secret")
            }
            val raw: String = response.body()
            val parsed = json.decodeFromString(ExaResponse.serializer(), raw)

            val showKeywords = listOf("gem", "mineral", "rock", "fossil", "show", "lapidary", "jewelry", "bead", "crystal", "geode", "meteorite")
            val matchingResult = parsed.results.firstOrNull { result ->
                val text = "${result.title ?: ""} ${result.highlights?.joinToString(" ") ?: ""} ${result.text ?: ""}".lowercase()
                showKeywords.any { it in text } &&
                    (result.title?.contains(name, ignoreCase = true) == true ||
                        text.contains(name.lowercase()) ||
                        text.contains(city.lowercase()))
            }
            if (matchingResult != null) {
                val snippet = (matchingResult.highlights?.firstOrNull() ?: matchingResult.text ?: "").take(300)
                val url = matchingResult.url ?: website
                onResult?.invoke(snippet, url)
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Search for gem & mineral shows in a given state. Returns discovered
     * show names and URLs for potential future auto-discovery.
     */
    suspend fun searchForShows(state: String): List<Pair<String, String>> =
        withContext(Dispatchers.IO) {
            val allVals = runCatching { com.rork.rockscout.Config.allValues }.getOrDefault(emptyMap())
            val toolkitUrl = allVals["EXPO_PUBLIC_TOOLKIT_URL"] ?: ""
            val secret = allVals["EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY"] ?: ""
            if (toolkitUrl.isBlank() || secret.isBlank()) return@withContext emptyList()

            val query = "gem mineral rock fossil show $state 2025 2026"

            val requestBody = buildJsonObject {
                put("query", query)
                put("type", "auto")
                put("numResults", 10)
                put("contents", buildJsonObject {
                    put("highlights", true)
                    put("text", true)
                })
            }.toString()

            try {
                val proxyUrl = "$toolkitUrl/v2/exa/search"
                val response = client.post(proxyUrl) {
                    contentType(ContentType.Application.Json)
                    setBody(requestBody)
                    headers.append("Authorization", "Bearer $secret")
                }
                val raw: String = response.body()
                val parsed = json.decodeFromString(ExaResponse.serializer(), raw)

                parsed.results.mapNotNull { result ->
                    val title = result.title?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                    val url = result.url?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                    Pair(title, url)
                }
            } catch (_: Exception) {
                emptyList()
            }
        }
}

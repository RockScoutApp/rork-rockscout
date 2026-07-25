package com.rork.rockscout.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Searches the web for rock hunting locations, dig sites, and rock shops
 * when the user's location filter returns fewer than 3 built-in dig sites.
 *
 * Uses the Rork proxy's Exa search endpoint ([/v2/exa/search]) so the
 * server-side Exa key and toolkit billing path are used.
 *
 * Results are stored in [DigSiteDiscoveryStore] for manual curation later.
 */
object DigSiteSearchService {

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
     * Search for rock hunting locations, dig sites, and rock shops in [searchArea].
     *
     * @param searchArea a human-readable location string, e.g. "Oregon, USA" or "Canada".
     * @return list of discovered sites, or empty list on error.
     */
    suspend fun searchRockLocations(searchArea: String): List<DigSiteDiscoveryStore.DiscoveredSite> =
        withContext(Dispatchers.IO) {
            val toolkitUrl = BuildSecrets.resolve("EXPO_PUBLIC_TOOLKIT_URL", BuildSecrets.TOOLKIT_URL)
            val secret = BuildSecrets.resolve("EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY", BuildSecrets.RORK_TOOLKIT_SECRET_KEY)
            if (toolkitUrl.isBlank() || secret.isBlank()) {
                return@withContext emptyList()
            }

            val query = "rock hunting dig sites rock shops mineral collecting locations in $searchArea"

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
                    val name = result.title?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                    val url = result.url?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                    val description = (result.highlights?.firstOrNull() ?: result.text ?: "")
                        .take(300)
                    val type = classifySite(name, description)

                    DigSiteDiscoveryStore.DiscoveredSite(
                        id = "disc-${System.currentTimeMillis()}-${name.hashCode()}",
                        name = name,
                        type = type,
                        region = searchArea,
                        url = url,
                        description = description,
                        searchArea = searchArea,
                        discoveredAt = System.currentTimeMillis(),
                    )
                }
            } catch (_: Exception) {
                emptyList()
            }
        }

    /**
     * Web-verify a user-submitted location by searching for the location name
     * near the given coordinates. Returns true if the search finds a result
     * whose title or snippet mentions rock/mineral/dig/collecting keywords,
     * confirming the location is a known rock-related site.
     *
     * Also returns the snippet and URL of the matching result via [onResult].
     */
    suspend fun verifyLocation(
        name: String,
        lat: Double,
        lng: Double,
        onResult: ((snippet: String, url: String) -> Unit)? = null,
    ): Boolean = withContext(Dispatchers.IO) {
        val toolkitUrl = BuildSecrets.resolve("EXPO_PUBLIC_TOOLKIT_URL", BuildSecrets.TOOLKIT_URL)
        val secret = BuildSecrets.resolve("EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY", BuildSecrets.RORK_TOOLKIT_SECRET_KEY)
        if (toolkitUrl.isBlank() || secret.isBlank()) return@withContext false

        val query = "$name rock hunting dig site mineral collecting near ${"%.2f, %.2f".format(lat, lng)}"

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

            val rockKeywords = listOf("rock", "mineral", "dig", "collect", "mine", "quarry", "gem", "crystal", "fossil", "geode", "lapidary", "rockhound")
            val matchingResult = parsed.results.firstOrNull { result ->
                val text = "${result.title ?: ""} ${result.highlights?.joinToString(" ") ?: ""} ${result.text ?: ""}".lowercase()
                rockKeywords.any { it in text } && (result.title?.contains(name, ignoreCase = true) == true || text.contains(name.lowercase()))
            }
            if (matchingResult != null) {
                val snippet = (matchingResult.highlights?.firstOrNull() ?: matchingResult.text ?: "").take(300)
                val url = matchingResult.url ?: ""
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
     * Web-verify a user-submitted campground by searching for the name near the
     * given coordinates. Returns true if the search finds a result mentioning
     * campground/camping/RV keywords.
     */
    suspend fun verifyCampground(
        name: String,
        lat: Double,
        lng: Double,
        onResult: ((snippet: String, url: String) -> Unit)? = null,
    ): Boolean = withContext(Dispatchers.IO) {
        val toolkitUrl = BuildSecrets.resolve("EXPO_PUBLIC_TOOLKIT_URL", BuildSecrets.TOOLKIT_URL)
        val secret = BuildSecrets.resolve("EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY", BuildSecrets.RORK_TOOLKIT_SECRET_KEY)
        if (toolkitUrl.isBlank() || secret.isBlank()) return@withContext false

        val query = "$name campground camping RV near ${"%.2f, %.2f".format(lat, lng)}"

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

            val campKeywords = listOf("campground", "camp", "camping", "rv", "recreation", "dispersed", "tent", "hookup", "state park", "national forest")
            val matchingResult = parsed.results.firstOrNull { result ->
                val text = "${result.title ?: ""} ${result.highlights?.joinToString(" ") ?: ""} ${result.text ?: ""}".lowercase()
                campKeywords.any { it in text } && (result.title?.contains(name, ignoreCase = true) == true || text.contains(name.lowercase()))
            }
            if (matchingResult != null) {
                val snippet = (matchingResult.highlights?.firstOrNull() ?: matchingResult.text ?: "").take(300)
                val url = matchingResult.url ?: ""
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
     * Web-verify a user-submitted trailhead by searching for the name near the
     * given coordinates. Returns true if the search finds a result mentioning
     * trailhead/hiking/trail/access keywords.
     */
    suspend fun verifyTrailhead(
        name: String,
        lat: Double,
        lng: Double,
        onResult: ((snippet: String, url: String) -> Unit)? = null,
    ): Boolean = withContext(Dispatchers.IO) {
        val toolkitUrl = BuildSecrets.resolve("EXPO_PUBLIC_TOOLKIT_URL", BuildSecrets.TOOLKIT_URL)
        val secret = BuildSecrets.resolve("EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY", BuildSecrets.RORK_TOOLKIT_SECRET_KEY)
        if (toolkitUrl.isBlank() || secret.isBlank()) return@withContext false

        val query = "$name trailhead hiking trail access near ${"%.2f, %.2f".format(lat, lng)}"

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

            val trailKeywords = listOf("trailhead", "trail", "hiking", "access", "parking", "forest service", "blm", "wilderness", "recreation")
            val matchingResult = parsed.results.firstOrNull { result ->
                val text = "${result.title ?: ""} ${result.highlights?.joinToString(" ") ?: ""} ${result.text ?: ""}".lowercase()
                trailKeywords.any { it in text } && (result.title?.contains(name, ignoreCase = true) == true || text.contains(name.lowercase()))
            }
            if (matchingResult != null) {
                val snippet = (matchingResult.highlights?.firstOrNull() ?: matchingResult.text ?: "").take(300)
                val url = matchingResult.url ?: ""
                onResult?.invoke(snippet, url)
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    /** Classify a discovered site as dig site, rock shop, or general. */
    private fun classifySite(name: String, description: String): String {
        val text = "$name $description".lowercase()
        return when {
            "shop" in text || "store" in text || "gem shop" in text -> "Rock Shop"
            "mine" in text || "quarry" in text || "dig" in text || "collect" in text -> "Dig Site"
            "fee" in text || "pay dig" in text -> "Pay Dig Site"
            "club" in text || "lapidary" in text -> "Lapidary Club"
            else -> "Rock Hunting Location"
        }
    }
}

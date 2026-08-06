package com.rork.rockscout.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

/**
 * Repository that resolves hero photos for dig sites and museums from the
 * backend /commons-photo endpoint (Wikimedia Commons search).
 *
 * Three-tier cache to minimise network usage:
 * 1. In-memory - ConcurrentHashMap keyed by location name. Survives
 *    navigation within the same app session. Checked first, never blocks.
 * 2. Persisted - PersistenceManager (SharedPreferences). Survives app
 *    restarts. Stores the resolved URL + a timestamp so we can re-validate
 *    stale entries after 30 days.
 * 3. Network - calls the backend endpoint only on a cache miss. The
 *    backend itself hits Wikimedia Commons, which has its own CDN cache.
 *
 * The resolved URL is then loaded by Coil's PersistentImage composable,
 * which adds a fourth tier: Coil's disk + memory bitmap cache. So a photo
 * that was once displayed loads instantly from disk on subsequent visits,
 * even with no network.
 *
 * A sentinel value NO_PHOTO is cached for locations where Commons returned
 * zero results, so we don't re-query the endpoint every time the card scrolls
 * into view.
 */
object LocationImageRepository {

    private val BASE_URL: String =
        BuildSecrets.resolve("EXPO_PUBLIC_RORK_FUNCTIONS_URL", BuildSecrets.RORK_FUNCTIONS_URL)
            .removeSuffix("/")
    private val APP_KEY: String =
        BuildSecrets.resolve("EXPO_PUBLIC_RORK_APP_KEY", BuildSecrets.RORK_APP_KEY)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient {
        install(HttpTimeout) {
            connectTimeoutMillis = 8_000
            requestTimeoutMillis = 15_000
            socketTimeoutMillis = 15_000
        }
    }

    private const val NO_PHOTO = "__NO_PHOTO__"
    private const val STALE_MS = 30L * 24 * 60 * 60 * 1000

    private val memoryCache = ConcurrentHashMap<String, CachedUrl>()
    private val fetchLocks = ConcurrentHashMap<String, Mutex>()

    @Serializable
    private data class CachedUrl(
        val url: String,
        val fetchedAtMs: Long,
    )

    private data class PersistedEntry(
        val url: String,
        val timestamp: Long,
    )

    @Serializable
    private data class CommonsResponse(
        val results: List<CommonsResult> = emptyList(),
    )

    @Serializable
    private data class CommonsResult(
        val url: String,
        val license: String = "",
    )

    /**
     * Returns a hero image URL for [name], or null if no photo was found.
     * Resolution order: in-memory, persisted, network.
     */
    suspend fun resolveImageUrl(name: String, region: String? = null): String? {
        val cacheKey = buildCacheKey(name, region)

        // Tier 1: in-memory
        memoryCache[cacheKey]?.let { entry ->
            if (entry.url == NO_PHOTO) return null
            if (System.currentTimeMillis() - entry.fetchedAtMs < STALE_MS) return entry.url
        }

        // Tier 2: persisted (SharedPreferences)
        val persisted = readPersisted(cacheKey)
        if (persisted != null) {
            if (System.currentTimeMillis() - persisted.timestamp < STALE_MS) {
                val url = persisted.url
                memoryCache[cacheKey] = CachedUrl(url, persisted.timestamp)
                return if (url == NO_PHOTO) null else url
            }
        }

        // Tier 3: network - deduplicate concurrent calls for the same key
        val mutex = fetchLocks.computeIfAbsent(cacheKey) { Mutex() }
        return mutex.withLock {
            // Re-check memory after acquiring the lock
            memoryCache[cacheKey]?.let { entry ->
                if (entry.url == NO_PHOTO) return null
                return entry.url
            }

            val url = fetchFromCommons(name, region)
            val now = System.currentTimeMillis()
            val stored = if (url != null) url else NO_PHOTO
            memoryCache[cacheKey] = CachedUrl(stored, now)
            writePersisted(cacheKey, PersistedEntry(stored, now))
            fetchLocks.remove(cacheKey)

            url
        }
    }

    /**
     * Pre-warm the cache for a list of locations without blocking the caller.
     */
    suspend fun prefetch(names: List<Pair<String, String?>>) {
        withContext(Dispatchers.IO) {
            names.forEach { (name, region) ->
                runCatching { resolveImageUrl(name, region) }
            }
        }
    }

    /**
     * Clear the in-memory and persisted cache.
     */
    fun clearCache() {
        memoryCache.clear()
        runCatching {
            PersistenceManager.clearLocationImageCache()
        }
    }

    private fun buildCacheKey(name: String, region: String?): String {
        val cleanName = name.trim().lowercase()
        val cleanRegion = region?.trim()?.lowercase().orEmpty()
        return if (cleanRegion.isNotEmpty()) "$cleanName|$cleanRegion" else cleanName
    }

    private suspend fun fetchFromCommons(name: String, region: String?): String? {
        return withContext(Dispatchers.IO) {
            runCatching {
                val query = if (!region.isNullOrBlank()) "$name $region" else name
                val url = "$BASE_URL/commons-photo?q=${java.net.URLEncoder.encode(query, "UTF-8")}&limit=3"
                val response: HttpResponse = client.get(url) {
                    if (APP_KEY.isNotBlank()) header("X-App-Key", APP_KEY)
                }
                if (!response.status.value.toString().startsWith("2")) return@withContext null

                val body: String = response.body()
                val parsed = json.decodeFromString(CommonsResponse.serializer(), body)
                parsed.results.firstOrNull()?.url
            }.getOrNull()
        }
    }

    private fun readPersisted(key: String): PersistedEntry? {
        return runCatching {
            val raw = PersistenceManager.readLocationImageCache(key)
            if (raw.isNullOrBlank()) return null
            val parts = raw.split("|", limit = 2)
            if (parts.size != 2) return null
            val ts = parts[0].toLongOrNull() ?: return null
            PersistedEntry(parts[1], ts)
        }.getOrNull()
    }

    private fun writePersisted(key: String, entry: PersistedEntry) {
        runCatching {
            PersistenceManager.writeLocationImageCache(key, "${entry.timestamp}|${entry.url}")
        }
    }
}

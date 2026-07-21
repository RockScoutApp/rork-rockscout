package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.MessageDigest

/**
 * On-device exact-image cache for AI identification results.
 *
 * Accuracy-protected by design: the cache key is the SHA-256 hash of the
 * **exact normalized image bytes** (resized + JPEG-recompressed using the same
 * parameters the identify pipeline uses). A cache hit means the user is
 * re-identifying the *same* photo (accidental re-submit, re-upload after
 * closing the result, etc.), so returning the stored result is guaranteed to
 * be identical to a fresh AI call — zero accuracy loss, zero AI cost.
 *
 * **No fuzzy / perceptual / "similar image" matching.** Two photos of two
 * different pyrite specimens produce different hashes and each get their own
 * fresh vision call. The AI always sees each new photo. This is the hard rule
 * from the plan: a cache hit must be guaranteed to return the exact same
 * result a fresh call would.
 *
 * Stored entries: full [IdentifyResponse] (matches, summary, web references,
 * clarification questions) keyed by hash, persisted to SharedPreferences as
 * JSON so the cache survives app restarts. LRU-evicted at [MAX_ENTRIES];
 * entries expire after [TTL_MS] (30 days) to keep the store from growing
 * unbounded and to let the underlying model improve over time.
 *
 * Only the **first-pass** identification result is cached. Clarification
 * re-rank calls (which depend on user answers) are never cached — those are
 * per-identification input and must always run fresh.
 */
object IdentifyCache {

    private const val PREFS_NAME = "rockscout_identify_cache"
    private const val TAG = "IdentifyCache"
    private const val MAX_ENTRIES = 50
    private const val TTL_MS = 30L * 24 * 60 * 60 * 1000L  // 30 days

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    private lateinit var prefs: android.content.SharedPreferences

    /** Must be called once from Application.onCreate() before any access. */
    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        pruneExpired()
        Log.d(TAG, "IdentifyCache initialized (${count()} entries)")
    }

    /**
     * Compute the SHA-256 hash of [jpegBytes] (the normalized image bytes —
     * resized + JPEG-recompressed using the same parameters the identify
     * pipeline uses). Returns a hex string. Two identical source bitmaps
     * produce identical hashes; two different specimens produce different
     * hashes.
     */
    fun hash(jpegBytes: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(jpegBytes).joinToString("") { "%02x".format(it) }
    }

    /**
     * Look up a cached [IdentifyResponse] for [hashKey]. Returns null on miss,
     * on expired entry (and prunes it), or if the store isn't initialized.
     * A hit means the user is re-identifying the exact same photo — the
     * returned response is guaranteed identical to a fresh AI call.
     */
    fun get(hashKey: String): IdentifyResponse? {
        if (!::prefs.isInitialized) return null
        val raw = prefs.getString(entryKey(hashKey), null) ?: return null
        val entry = runCatching { json.decodeFromString(CacheEntry.serializer(), raw) }.getOrNull()
            ?: run { prefs.edit().remove(entryKey(hashKey)).apply(); return null }
        if (System.currentTimeMillis() - entry.cachedAtMs > TTL_MS) {
            prefs.edit().remove(entryKey(hashKey)).remove(metaKey(hashKey)).apply()
            return null
        }
        // Touch the access-order meta so LRU eviction picks the oldest.
        prefs.edit().putLong(metaKey(hashKey), System.currentTimeMillis()).apply()
        return entry.response
    }

    /**
     * Store [response] under [hashKey]. Evicts the oldest entry if the cache
     * is at [MAX_ENTRIES] so the store stays bounded.
     */
    fun put(hashKey: String, response: IdentifyResponse) {
        if (!::prefs.isInitialized) return
        val now = System.currentTimeMillis()
        val entry = CacheEntry(response = response, cachedAtMs = now)
        runCatching {
            prefs.edit()
                .putString(entryKey(hashKey), json.encodeToString(CacheEntry.serializer(), entry))
                .putLong(metaKey(hashKey), now)
                .apply()
        }.onFailure { Log.w(TAG, "Failed to cache identify result: ${it.message}") }
        evictIfNeeded()
    }

    /** Number of cached entries (for diagnostics / a future "cache size" UI). */
    fun count(): Int {
        if (!::prefs.isInitialized) return 0
        return prefs.all.keys.count { it.startsWith(PREFIX_ENTRY) }
    }

    /** Wipe the entire cache (e.g. on sign-out). */
    fun clear() {
        if (!::prefs.isInitialized) return
        prefs.edit().clear().apply()
    }

    // ---------------------------------------------------------------- helpers
    private fun entryKey(hash: String) = "$PREFIX_ENTRY$hash"
    private fun metaKey(hash: String) = "$PREFIX_META$hash"
    private fun pruneExpired() {
        if (!::prefs.isInitialized) return
        val now = System.currentTimeMillis()
        val toRemove = mutableListOf<String>()
        prefs.all.forEach { (k, v) ->
            if (k.startsWith(PREFIX_ENTRY) && v is String) {
                val entry = runCatching { json.decodeFromString(CacheEntry.serializer(), v) }.getOrNull()
                if (entry != null && now - entry.cachedAtMs > TTL_MS) {
                    val hash = k.removePrefix(PREFIX_ENTRY)
                    toRemove.add(k); toRemove.add(metaKey(hash))
                }
            }
        }
        if (toRemove.isNotEmpty()) prefs.edit().apply { toRemove.forEach { remove(it) } }.apply()
    }

    private fun evictIfNeeded() {
        val keys = prefs.all.keys.filter { it.startsWith(PREFIX_ENTRY) }
        if (keys.size <= MAX_ENTRIES) return
        // Evict the least-recently-accessed entries until we're under the cap.
        val accessTimes = keys.associateWith { k ->
            val hash = k.removePrefix(PREFIX_ENTRY)
            prefs.getLong(metaKey(hash), 0L)
        }.toList().sortedBy { it.second }
        val evictCount = keys.size - MAX_ENTRIES
        val editor = prefs.edit()
        accessTimes.take(evictCount).forEach { (k, _) ->
            val hash = k.removePrefix(PREFIX_ENTRY)
            editor.remove(k).remove(metaKey(hash))
        }
        editor.apply()
        Log.d(TAG, "Evicted $evictCount cached identify results (LRU)")
    }

    @kotlinx.serialization.Serializable
    private data class CacheEntry(
        val response: IdentifyResponse,
        val cachedAtMs: Long,
    )

    private const val PREFIX_ENTRY = "e_"
    private const val PREFIX_META = "m_"
}

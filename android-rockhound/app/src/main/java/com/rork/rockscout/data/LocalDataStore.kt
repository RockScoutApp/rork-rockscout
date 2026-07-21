package com.rork.rockscout.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Self-contained local database backed by SharedPreferences.
 *
 * Replaces Supabase Postgrest entirely. All social tables (users, connections,
 * message requests, friend requests, blocks, threads, messages, posts, likes,
 * comments, notifications, trade interests, reports, pings, public trade
 * listings) are stored as JSON strings and loaded into memory on init.
 *
 * Generic [getTable] / [setTable] methods handle (de)serialization for any
 * @Serializable type. Each repository owns its table key and row type.
 */
object LocalDataStore {

    private const val PREFS_NAME = "rockscout_local_db"

    val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    private lateinit var prefs: SharedPreferences

    /** Synchronization lock for atomic read-modify-write on table operations. */
    @PublishedApi
    internal val tableLock = Any()

    /**
     * Bounded LRU cache of raw JSON strings keyed by table key.
     * Avoids repeated SharedPreferences disk reads + JSON deserialization
     * on every getTable() call — a major CPU win on low-end devices and
     * low-signal scenarios where the app needs to feel responsive.
     *
     * Capped at [MAX_CACHE_ENTRIES] to prevent unbounded memory growth.
     * When the cap is exceeded, the least-recently-accessed entry is evicted.
     *
     * Invalidated automatically on setTable / updateTable / setString.
     */
    private val rawCache =
        object : java.util.LinkedHashMap<String, String>(16, 0.75f, true) {
            override fun removeEldestEntry(eldest: Map.Entry<String, String>): Boolean {
                return size > MAX_CACHE_ENTRIES
            }
        }

    private val rawCacheLock = Any()

    private const val MAX_CACHE_ENTRIES = 32

    // ---- Table keys --------------------------------------------------------
    const val KEY_USERS = "users"
    const val KEY_CONNECTIONS = "connections"
    const val KEY_MESSAGE_REQUESTS = "message_requests"
    const val KEY_FRIEND_REQUESTS = "friend_requests"
    const val KEY_BLOCKS = "blocks"
    const val KEY_THREADS = "threads"
    const val KEY_MESSAGES = "messages"
    const val KEY_POSTS = "posts"
    const val KEY_POST_LIKES = "post_likes"
    const val KEY_POST_COMMENTS = "post_comments"
    const val KEY_POST_COMMENT_LIKES = "post_comment_likes"
    const val KEY_COMMUNITY_POSTS = "community_posts"
    const val KEY_COMMUNITY_POST_LIKES = "community_post_likes"
    const val KEY_COMMUNITY_POST_COMMENTS = "community_post_comments"
    const val KEY_COMMUNITY_POST_COMMENT_LIKES = "community_post_comment_likes"
    const val KEY_NOTIFICATIONS = "notifications"
    const val KEY_TRADE_INTERESTS = "trade_interests"
    const val KEY_USER_REPORTS = "user_reports"
    const val KEY_PINGS = "pings"
    const val KEY_PUBLIC_TRADE_LISTINGS = "public_trade_listings"
    const val KEY_IMAGE_REVIEWS = "image_reviews"
    const val KEY_LIST_LIKES = "list_likes"
    const val KEY_APPEALS = "appeals"
    const val KEY_MOCK_SEEDED = "mock_data_seeded"

    // ---- Local-auth account keys ------------------------------------------
    const val KEY_AUTH_EMAIL = "auth_current_email"
    const val KEY_AUTH_USER_ID = "auth_current_user_id"

    /** Must be called once from Application.onCreate before any access. */
    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // ---- Raw access --------------------------------------------------------
    fun getString(key: String): String? {
        // Check in-memory LRU cache first to avoid disk read on hot paths.
        synchronized(rawCacheLock) {
            rawCache[key]?.let { return it }
        }
        val raw = prefs.getString(key, null)
        if (raw != null) {
            synchronized(rawCacheLock) { rawCache[key] = raw }
        }
        return raw
    }

    fun setString(key: String, value: String) {
        synchronized(rawCacheLock) { rawCache[key] = value }
        prefs.edit().putString(key, value).apply()
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        prefs.getBoolean(key, default)

    fun setBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    // ---- Typed table access ------------------------------------------------
    /** Load a typed list from a table. Returns empty list if the table is
     *  empty or deserialization fails. */
    inline fun <reified T> getTable(key: String): List<T> = synchronized(tableLock) {
        val raw = getString(key) ?: return emptyList()
        return runCatching {
            json.decodeFromString<List<T>>(raw)
        }.getOrDefault(emptyList())
    }

    /** Save a typed list to a table. */
    inline fun <reified T> setTable(key: String, data: List<T>) = synchronized(tableLock) {
        setString(key, json.encodeToString(data))
    }

    /** Atomically read-modify-write a table under [tableLock]. */
    inline fun <reified T> updateTable(key: String, transform: (List<T>) -> List<T>) = synchronized(tableLock) {
        val current = runCatching {
            getString(key)?.let { json.decodeFromString<List<T>>(it) } ?: emptyList()
        }.getOrDefault(emptyList())
        val updated = transform(current)
        setString(key, json.encodeToString(updated))
    }

    /** Check if the store has been initialized. */
    fun isInitialized(): Boolean = ::prefs.isInitialized

    /** Wipe every locally persisted table and key. Used during account deletion
     *  so the user's device copy of their account, social graph, posts, messages,
     *  and trade data is fully removed. */
    fun clearAll() {
        synchronized(tableLock) {
            synchronized(rawCacheLock) { rawCache.clear() }
            prefs.edit().clear().apply()
        }
    }
}

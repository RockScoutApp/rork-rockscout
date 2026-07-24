package com.rork.rockscout.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Persists user-generated app state (profile, collection, wishlist, favorite
 * spots, captures, current location, location-monitoring flag) to
 * SharedPreferences as JSON so it survives app closes and device restarts.
 *
 * Read-only reference data (specimens, dig sites, guides, periods, gem shows)
 * ships in the binary and is not persisted here.
 *
 * Also exposes a tiny key-value API for the location-monitoring flag so the
 * [ProximityCheckWorker] can read it from a background process without
 * touching the in-memory [AppRepository] (which is only alive while the app
 * process is running).
 */
object PersistenceManager {

    private const val PREFS_NAME = "rockscout_app_state"
    private const val TAG = "PersistenceManager"

    // Keys that mirror AppRepository's StateFlows
    private const val KEY_PROFILE = "profile_json"
    private const val KEY_COLLECTION = "collection_json"
    private const val KEY_WISHLIST = "wishlist_json"
    private const val KEY_FAVORITE_SPOTS = "favorite_spots_json"
    private const val KEY_CAPTURES = "captures_json"
    private const val KEY_TRIPS = "trips_json"
    private const val KEY_JOURNAL_ENTRIES = "journal_entries_json"
    private const val KEY_TRADE_LISTINGS = "trade_listings_json"
    private const val KEY_SAVED_IMAGES = "saved_images_json"
    private const val KEY_LIKED_SPECIMENS = "liked_specimens_json"
    private const val KEY_CURRENT_LOCATION = "current_location_json"

    // Standalone key for the location-monitoring flag, used by background workers
    private const val KEY_LOCATION_MONITORING = "location_monitoring_enabled"
    private const val KEY_NEARBY_PLACES = "nearby_places_enabled"
    private const val KEY_NEARBY_ALERTS = "nearby_alerts_enabled"
    private const val KEY_CLUB_ENABLED = "club_enabled"
    private const val KEY_NEARBY_FRIENDS = "nearby_friends_enabled"
    private const val KEY_NEARBY_FRIENDS_ALERTS = "nearby_friends_alerts_enabled"
    private const val KEY_WEATHER_ALERTS = "weather_alerts_enabled"
    private const val KEY_AURORA_ALERTS = "aurora_alerts_enabled"
    private const val KEY_NOTIF_FRIEND_REQUESTS = "notif_friend_requests_enabled"
    private const val KEY_NOTIF_MESSAGES = "notif_messages_enabled"
    private const val KEY_NOTIF_NEW_POSTS = "notif_new_posts_enabled"
    private const val KEY_NOTIF_TRADE_INTEREST = "notif_trade_interest_enabled"
    private const val KEY_NOTIF_MARKED_TRADED = "notif_marked_traded_enabled"
    private const val KEY_NOTIF_LOCATION_APPROVED = "notif_location_approved_enabled"
    private const val KEY_NOTIF_ENGAGEMENT_SUMMARY = "notif_engagement_summary_enabled"
    private const val KEY_HUNTER_STATUS = "hunter_status"
    private const val KEY_SCAN_RADIUS_MILES = "scan_radius_miles"
    private const val KEY_LAST_SPONSORED_PROMPT = "last_sponsored_prompt_at"
    private const val KEY_CACHE_SIZE_MODE = "cache_size_mode"
    private const val KEY_PARKING_SPOT = "parking_spot_json"
    // Set of trip IDs whose route + specimen marker map tiles have been cached for offline use.
    private const val KEY_CACHED_TRIP_IDS = "cached_trip_ids_json"
    // Map of trip ID -> epoch millis when its tiles were last cached. Used by the
    // map sync-status indicator to show "Cached 3 days ago" and to suggest a
    // refresh when tiles are older than the staleness threshold.
    private const val KEY_CACHED_TRIP_TIMESTAMPS = "cached_trip_timestamps_json"
    // Last time the user's current-area (ping map / dig sites map) tiles were
    // refreshed. Keyed by a coarse lat/lng grid cell so moving to a new area
    // is treated as a fresh cache.
    private const val KEY_CACHED_AREA_TIMESTAMPS = "cached_area_timestamps_json"
    // Last time a single-point (location detail / parking / pin drop) tile cache
    // was refreshed, keyed by coarse grid cell.
    private const val KEY_CACHED_POINT_TIMESTAMPS = "cached_point_timestamps_json"
    // Version of the mandatory legal disclaimer the user has accepted. Null or a
    // version older than [DISCLAIMER_CURRENT_VERSION] triggers the disclaimer flow.
    private const val KEY_DISCLAIMER_ACCEPTED_VERSION = "disclaimer_accepted_version"

    // Bulk-download state for the optional "Download all specimen images for offline"
    // action. Stores a small JSON record with total/finished/bytes/done/cancelled
    // so the UI can resume across screen changes and app restarts.
    private const val KEY_BULK_DOWNLOAD_STATE = "bulk_download_state_json"

    // Nightly offline-sync toggle + last-run timestamp. When the toggle is on,
    // WorkScheduler schedules a periodic NightlySyncWorker that re-runs the
    // bulk prefetch (charging + idle + WiFi only) so the local cache stays
    // current without the user having to remember.
    private const val KEY_NIGHTLY_SYNC_ENABLED = "nightly_sync_enabled"
    private const val KEY_NIGHTLY_SYNC_LAST_RUN = "nightly_sync_last_run_ms"
    private const val KEY_NIGHT_MODE_ENABLED = "night_mode_enabled"

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    private lateinit var prefs: SharedPreferences

    /**
     * Must be called once from [com.rork.rockscout.RockScoutApplication.onCreate]
     * before any access. Loads persisted values into [AppRepository].
     */
    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadInto(AppRepository.instance)
    }

    // ---------------------------------------------------------------- saveers
    fun saveProfile(profile: UserProfile) =
        putJson(KEY_PROFILE, profile)

    fun saveCollection(entries: List<CollectionEntry>) =
        putJson(KEY_COLLECTION, entries)

    fun saveWishlist(ids: List<String>) =
        putJson(KEY_WISHLIST, ids)

    fun saveFavoriteSpots(ids: List<String>) =
        putJson(KEY_FAVORITE_SPOTS, ids)

    fun saveCaptures(photos: List<CapturedPhoto>) =
        putJson(KEY_CAPTURES, photos)

    fun saveTrips(trips: List<Trip>) =
        putJson(KEY_TRIPS, trips)

    fun saveJournalEntries(entries: List<JournalEntry>) =
        putJson(KEY_JOURNAL_ENTRIES, entries)

    fun saveTradeListings(listings: List<TradeListing>) =
        putJson(KEY_TRADE_LISTINGS, listings)

    fun saveSavedImages(images: List<SavedImage>) =
        putJson(KEY_SAVED_IMAGES, images)

    fun saveLikedSpecimens(ids: List<String>) =
        putJson(KEY_LIKED_SPECIMENS, ids)

    fun saveCurrentLocation(lat: Double, lng: Double) =
        putJson(KEY_CURRENT_LOCATION, Pair(lat, lng))

    /**
     * Persist the location-monitoring flag on its own key so background workers
     * can read it even when the app process is not running.
     */
    fun saveLocationMonitoringEnabled(enabled: Boolean) {
        ensureInitialized()
        prefs.edit().putBoolean(KEY_LOCATION_MONITORING, enabled).apply()
    }

    /** Background-safe read of the location-monitoring flag. */
    fun isLocationMonitoringEnabled(): Boolean {
        ensureInitialized()
        return prefs.getBoolean(KEY_LOCATION_MONITORING, false)
    }

    /** Persist the nearby-places flag on its own key so it survives app restarts. */
    fun saveNearbyPlacesEnabled(enabled: Boolean) {
        ensureInitialized()
        prefs.edit().putBoolean(KEY_NEARBY_PLACES, enabled).apply()
    }

    fun isNearbyPlacesEnabled(): Boolean {
        ensureInitialized()
        return prefs.getBoolean(KEY_NEARBY_PLACES, false)
    }

    fun saveNearbyAlertsEnabled(enabled: Boolean) {
        ensureInitialized()
        prefs.edit().putBoolean(KEY_NEARBY_ALERTS, enabled).apply()
    }

    fun isNearbyAlertsEnabled(): Boolean {
        ensureInitialized()
        return prefs.getBoolean(KEY_NEARBY_ALERTS, false)
    }

    fun saveClubEnabled(enabled: Boolean) {
        ensureInitialized()
        prefs.edit().putBoolean(KEY_CLUB_ENABLED, enabled).apply()
    }

    fun isClubEnabled(): Boolean {
        ensureInitialized()
        return prefs.getBoolean(KEY_CLUB_ENABLED, false)
    }

    fun saveNearbyFriendsEnabled(enabled: Boolean) {
        ensureInitialized()
        prefs.edit().putBoolean(KEY_NEARBY_FRIENDS, enabled).apply()
    }

    fun isNearbyFriendsEnabled(): Boolean {
        ensureInitialized()
        return prefs.getBoolean(KEY_NEARBY_FRIENDS, false)
    }

    fun saveNearbyFriendsAlertsEnabled(enabled: Boolean) {
        ensureInitialized()
        prefs.edit().putBoolean(KEY_NEARBY_FRIENDS_ALERTS, enabled).apply()
    }

    fun isNearbyFriendsAlertsEnabled(): Boolean {
        ensureInitialized()
        return prefs.getBoolean(KEY_NEARBY_FRIENDS_ALERTS, false)
    }

    fun saveWeatherAlertsEnabled(enabled: Boolean) {
        ensureInitialized()
        prefs.edit().putBoolean(KEY_WEATHER_ALERTS, enabled).apply()
    }

    fun isWeatherAlertsEnabled(): Boolean {
        ensureInitialized()
        return prefs.getBoolean(KEY_WEATHER_ALERTS, false)
    }

    fun saveAuroraAlertsEnabled(enabled: Boolean) {
        ensureInitialized()
        prefs.edit().putBoolean(KEY_AURORA_ALERTS, enabled).apply()
    }

    fun isAuroraAlertsEnabled(): Boolean {
        ensureInitialized()
        return prefs.getBoolean(KEY_AURORA_ALERTS, false)
    }

    fun saveNotifFriendRequestsEnabled(enabled: Boolean) {
        ensureInitialized()
        prefs.edit().putBoolean(KEY_NOTIF_FRIEND_REQUESTS, enabled).apply()
    }

    fun isNotifFriendRequestsEnabled(): Boolean {
        ensureInitialized()
        return prefs.getBoolean(KEY_NOTIF_FRIEND_REQUESTS, true)
    }

    fun saveNotifMessagesEnabled(enabled: Boolean) {
        ensureInitialized()
        prefs.edit().putBoolean(KEY_NOTIF_MESSAGES, enabled).apply()
    }

    fun isNotifMessagesEnabled(): Boolean {
        ensureInitialized()
        return prefs.getBoolean(KEY_NOTIF_MESSAGES, true)
    }

    fun saveNotifNewPostsEnabled(enabled: Boolean) {
        ensureInitialized()
        prefs.edit().putBoolean(KEY_NOTIF_NEW_POSTS, enabled).apply()
    }

    fun isNotifNewPostsEnabled(): Boolean {
        ensureInitialized()
        return prefs.getBoolean(KEY_NOTIF_NEW_POSTS, true)
    }

    fun saveNotifTradeInterestEnabled(enabled: Boolean) {
        ensureInitialized()
        prefs.edit().putBoolean(KEY_NOTIF_TRADE_INTEREST, enabled).apply()
    }

    fun isNotifTradeInterestEnabled(): Boolean {
        ensureInitialized()
        return prefs.getBoolean(KEY_NOTIF_TRADE_INTEREST, true)
    }

    fun saveNotifMarkedTradedEnabled(enabled: Boolean) {
        ensureInitialized()
        prefs.edit().putBoolean(KEY_NOTIF_MARKED_TRADED, enabled).apply()
    }

    fun isNotifMarkedTradedEnabled(): Boolean {
        ensureInitialized()
        return prefs.getBoolean(KEY_NOTIF_MARKED_TRADED, true)
    }

    fun saveNotifLocationApprovedEnabled(enabled: Boolean) {
        ensureInitialized()
        prefs.edit().putBoolean(KEY_NOTIF_LOCATION_APPROVED, enabled).apply()
    }

    fun isNotifLocationApprovedEnabled(): Boolean {
        ensureInitialized()
        return prefs.getBoolean(KEY_NOTIF_LOCATION_APPROVED, true)
    }

    fun saveNotifEngagementSummaryEnabled(enabled: Boolean) {
        ensureInitialized()
        prefs.edit().putBoolean(KEY_NOTIF_ENGAGEMENT_SUMMARY, enabled).apply()
    }

    fun isNotifEngagementSummaryEnabled(): Boolean {
        ensureInitialized()
        return prefs.getBoolean(KEY_NOTIF_ENGAGEMENT_SUMMARY, true)
    }

    fun saveHunterStatus(status: HunterStatus) {
        ensureInitialized()
        prefs.edit().putString(KEY_HUNTER_STATUS, status.name).apply()
    }

    fun loadHunterStatus(): HunterStatus {
        ensureInitialized()
        val raw = prefs.getString(KEY_HUNTER_STATUS, null) ?: return HunterStatus.OFF_GRID
        return runCatching { HunterStatus.valueOf(raw) }.getOrDefault(HunterStatus.OFF_GRID)
    }

    fun saveScanRadiusMiles(miles: Int) {
        ensureInitialized()
        prefs.edit().putInt(KEY_SCAN_RADIUS_MILES, miles.coerceIn(5, 250)).apply()
    }

    fun loadScanRadiusMiles(): Int {
        ensureInitialized()
        return prefs.getInt(KEY_SCAN_RADIUS_MILES, 50)
    }

    /**
     * Mandatory legal disclaimer acceptance. Bump [DISCLAIMER_CURRENT_VERSION] when
     * clauses change materially; users with an older (or null) accepted version will
     * be re-prompted with the disclaimer flow on next launch / sign-in.
     */
    fun saveDisclaimerAcceptedVersion(version: String) {
        ensureInitialized()
        prefs.edit().putString(KEY_DISCLAIMER_ACCEPTED_VERSION, version).apply()
    }

    fun loadDisclaimerAcceptedVersion(): String? {
        ensureInitialized()
        return prefs.getString(KEY_DISCLAIMER_ACCEPTED_VERSION, null)
    }

    /** 72-hour cooldown for the paying-user sponsored specimen prompt. */
    fun saveLastSponsoredPromptAt(timestampMs: Long) {
        ensureInitialized()
        prefs.edit().putLong(KEY_LAST_SPONSORED_PROMPT, timestampMs).apply()
    }

    fun loadLastSponsoredPromptAt(): Long {
        ensureInitialized()
        return prefs.getLong(KEY_LAST_SPONSORED_PROMPT, 0L)
    }

    // ---------------------------------------------------------- cache size
    /**
     * Persist the user's cache size preference: "standard" (150MB) or "max" (2GB).
     * Read at app startup by RockHoundApplication to size Coil + osmdroid caches.
     */
    fun saveCacheSizeMode(mode: String) {
        ensureInitialized()
        prefs.edit().putString(KEY_CACHE_SIZE_MODE, mode).apply()
    }

    fun loadCacheSizeMode(): String {
        ensureInitialized()
        return prefs.getString(KEY_CACHE_SIZE_MODE, "standard") ?: "standard"
    }

    // ------------------------------------------------------ bulk download
    /**
     * Snapshot of the optional "Download all specimen images for offline" job.
     * Persists across screen changes and app restarts so the user can resume a
     * partial download instead of starting over.
     *
     * @property total       Total number of unique image URLs to fetch.
     * @property finished    How many have been successfully cached so far.
     * @property bytes       Approximate bytes downloaded (sum of cached file sizes).
     * @property done        True once [finished] has reached [total].
     * @property cancelled   True if the user cancelled the in-flight run.
     * @property running     True if a run is currently active (not yet done/cancelled).
     * @property startedAt   Epoch millis when the current run was started.
     * @property finishedAt  Epoch millis when the run reached done/cancelled.
     */
    @Serializable
    data class BulkDownloadState(
        @SerialName("total") val total: Int = 0,
        @SerialName("finished") val finished: Int = 0,
        @SerialName("bytes") val bytes: Long = 0L,
        @SerialName("done") val done: Boolean = false,
        @SerialName("cancelled") val cancelled: Boolean = false,
        @SerialName("running") val running: Boolean = false,
        @SerialName("startedAt") val startedAt: Long = 0L,
        @SerialName("finishedAt") val finishedAt: Long = 0L,
    )

    /** Save the current bulk-download state. */
    fun saveBulkDownloadState(state: BulkDownloadState) {
        putJson(KEY_BULK_DOWNLOAD_STATE, state)
    }

    /** Load the current bulk-download state, or null if never started. */
    fun loadBulkDownloadState(): BulkDownloadState? {
        ensureInitialized()
        return runCatching {
            prefs.getString(KEY_BULK_DOWNLOAD_STATE, null)
                ?.let { json.decodeFromString<BulkDownloadState>(it) }
        }.getOrNull()
    }

    /** Clear the bulk-download state (e.g. after the user dismisses the completion card). */
    fun clearBulkDownloadState() {
        ensureInitialized()
        prefs.edit().remove(KEY_BULK_DOWNLOAD_STATE).apply()
    }

    // ----------------------------------------------------------- nightly sync
    /**
     * Whether the optional "Auto-sync offline database while charging at night"
     * toggle is on. When true, [com.rork.rockscout.data.WorkScheduler] keeps a
     * periodic NightlySyncWorker scheduled that re-runs the bulk prefetch
     * (charging + device idle + WiFi only) so the local cache stays current
     * without the user having to remember.
     */
    fun saveNightlySyncEnabled(enabled: Boolean) {
        ensureInitialized()
        prefs.edit().putBoolean(KEY_NIGHTLY_SYNC_ENABLED, enabled).apply()
    }

    fun isNightlySyncEnabled(): Boolean {
        ensureInitialized()
        return prefs.getBoolean(KEY_NIGHTLY_SYNC_ENABLED, false)
    }

    /**
     * Records the epoch millis of the last successful nightly sync run. Used by
     * the settings UI to show "Last synced N days ago" next to the toggle.
     */
    fun saveNightlySyncLastRun(timestampMs: Long) {
        ensureInitialized()
        prefs.edit().putLong(KEY_NIGHTLY_SYNC_LAST_RUN, timestampMs).apply()
    }

    fun loadNightlySyncLastRun(): Long {
        ensureInitialized()
        return prefs.getLong(KEY_NIGHTLY_SYNC_LAST_RUN, 0L)
    }

    // ----------------------------------------------------------- night mode
    /** Red-light / night-vision mode for UV collecting. Persists across launches. */
    fun saveNightModeEnabled(enabled: Boolean) {
        ensureInitialized()
        prefs.edit().putBoolean(KEY_NIGHT_MODE_ENABLED, enabled).apply()
    }

    fun isNightModeEnabled(): Boolean {
        ensureInitialized()
        return prefs.getBoolean(KEY_NIGHT_MODE_ENABLED, false)
    }

    // ---------------------------------------------------------- parking spot
    /** Persisted parking spot saved via the Park Here feature on the dig sites map. */
    @Serializable
    data class ParkingSpot(
        @SerialName("lat") val latitude: Double,
        @SerialName("lng") val longitude: Double,
        @SerialName("ts") val timestamp: Long,
    )

    fun saveParkingSpot(lat: Double, lng: Double) {
        putJson(KEY_PARKING_SPOT, ParkingSpot(lat, lng, System.currentTimeMillis()))
    }

    fun loadParkingSpot(): ParkingSpot? {
        ensureInitialized()
        return runCatching {
            prefs.getString(KEY_PARKING_SPOT, null)?.let {
                json.decodeFromString<ParkingSpot>(it)
            }
        }.getOrNull()
    }

    fun clearParkingSpot() {
        ensureInitialized()
        prefs.edit().remove(KEY_PARKING_SPOT).apply()
    }

    // ------------------------------------------------------- cached trip areas
    /**
     * Persist the set of trip IDs whose route corridor + specimen marker map
     * tiles have been pre-downloaded for offline use. Read back at startup so
     * the Cache Trip Area button reflects the real on-disk state and users
     * don't re-download tiles they already have.
     */
    fun saveCachedTripIds(ids: Set<String>) {
        putJson(KEY_CACHED_TRIP_IDS, ids)
    }

    fun loadCachedTripIds(): Set<String> {
        ensureInitialized()
        return runCatching {
            prefs.getString(KEY_CACHED_TRIP_IDS, null)
                ?.let { json.decodeFromString<Set<String>>(it) }
        }.getOrNull() ?: emptySet()
    }

    fun addCachedTripId(tripId: String) {
        saveCachedTripIds(loadCachedTripIds() + tripId)
        recordTripCacheTime(tripId, System.currentTimeMillis())
    }

    fun removeCachedTripId(tripId: String) {
        saveCachedTripIds(loadCachedTripIds() - tripId)
        // Also drop its timestamp so a stale entry doesn't linger.
        runCatching {
            val stamps = loadCachedTripTimestamps().toMutableMap()
            stamps.remove(tripId)
            putJson(KEY_CACHED_TRIP_TIMESTAMPS, stamps)
        }
    }

    /** Map of trip ID -> epoch millis when the trip's tiles were last refreshed. */
    fun loadCachedTripTimestamps(): Map<String, Long> {
        ensureInitialized()
        return runCatching {
            prefs.getString(KEY_CACHED_TRIP_TIMESTAMPS, null)
                ?.let { json.decodeFromString<Map<String, Long>>(it) }
        }.getOrNull() ?: emptyMap()
    }

    /** Update the cached timestamp for a trip without re-writing the ID set. */
    fun recordTripCacheTime(tripId: String, timestampMillis: Long) {
        runCatching {
            val stamps = loadCachedTripTimestamps().toMutableMap()
            stamps[tripId] = timestampMillis
            putJson(KEY_CACHED_TRIP_TIMESTAMPS, stamps)
        }
    }

    /**
     * Records a cache refresh for a geographic area (user-area prefetch on the
     * dig sites or ping map). Coarse-grained 0.1° grid (~6 mi) so a new area is
     * treated as freshly cached while the same area updates the existing stamp.
     */
    fun recordAreaCacheTime(lat: Double, lng: Double) {
        runCatching {
            val key = areaKey(lat, lng)
            val stamps = loadAreaCacheTimestamps().toMutableMap()
            stamps[key] = System.currentTimeMillis()
            putJson(KEY_CACHED_AREA_TIMESTAMPS, stamps)
        }
    }

    /** Returns the last cache time for the area containing [lat]/[lng], or null. */
    fun areaCacheTime(lat: Double, lng: Double): Long? {
        ensureInitialized()
        return runCatching {
            prefs.getString(KEY_CACHED_AREA_TIMESTAMPS, null)
                ?.let { json.decodeFromString<Map<String, Long>>(it) }
                ?.get(areaKey(lat, lng))
        }.getOrNull()
    }

    /** Map of coarse area key -> epoch millis when tiles were last refreshed. */
    fun loadAreaCacheTimestamps(): Map<String, Long> {
        ensureInitialized()
        return runCatching {
            prefs.getString(KEY_CACHED_AREA_TIMESTAMPS, null)
                ?.let { json.decodeFromString<Map<String, Long>>(it) }
        }.getOrNull() ?: emptyMap()
    }

    /**
     * Records a cache refresh for a single point (location detail, parking
     * spot, or a manually-dropped pin). Uses a 0.01° grid (~0.6 mi) so re-caching
     * the same dig site updates its stamp while a different site gets its own.
     */
    fun recordPointCacheTime(lat: Double, lng: Double) {
        runCatching {
            val key = pointKey(lat, lng)
            val stamps = loadPointCacheTimestamps().toMutableMap()
            stamps[key] = System.currentTimeMillis()
            putJson(KEY_CACHED_POINT_TIMESTAMPS, stamps)
        }
    }

    /** Returns the last cache time for the point containing [lat]/[lng], or null. */
    fun pointCacheTime(lat: Double, lng: Double): Long? {
        ensureInitialized()
        return runCatching {
            prefs.getString(KEY_CACHED_POINT_TIMESTAMPS, null)
                ?.let { json.decodeFromString<Map<String, Long>>(it) }
                ?.get(pointKey(lat, lng))
        }.getOrNull()
    }

    /** Map of coarse point key -> epoch millis when tiles were last refreshed. */
    fun loadPointCacheTimestamps(): Map<String, Long> {
        ensureInitialized()
        return runCatching {
            prefs.getString(KEY_CACHED_POINT_TIMESTAMPS, null)
                ?.let { json.decodeFromString<Map<String, Long>>(it) }
        }.getOrNull() ?: emptyMap()
    }

    /** Coarse 0.1° grid key (~6 mi cells) for area-level cache tracking. */
    private fun areaKey(lat: Double, lng: Double): String {
        val gridLat = (lat * 10.0).toInt() / 10.0
        val gridLng = (lng * 10.0).toInt() / 10.0
        return "%.1f,%.1f".format(gridLat, gridLng)
    }

    /** Finer 0.01° grid key (~0.6 mi cells) for point-level cache tracking. */
    private fun pointKey(lat: Double, lng: Double): String {
        val gridLat = (lat * 100.0).toInt() / 100.0
        val gridLng = (lng * 100.0).toInt() / 100.0
        return "%.2f,%.2f".format(gridLat, gridLng)
    }

    /** Clears every cached timestamp entry (called when the user wipes all caches). */
    fun clearAllCacheTimestamps() {
        ensureInitialized()
        prefs.edit()
            .remove(KEY_CACHED_TRIP_TIMESTAMPS)
            .remove(KEY_CACHED_AREA_TIMESTAMPS)
            .remove(KEY_CACHED_POINT_TIMESTAMPS)
            .apply()
    }

    /** Also drop bulk-download state when the user wipes the cache, since the
     *  underlying image files have been deleted and any progress is no longer valid. */
    fun clearAllCacheState() {
        ensureInitialized()
        prefs.edit()
            .remove(KEY_CACHED_TRIP_TIMESTAMPS)
            .remove(KEY_CACHED_AREA_TIMESTAMPS)
            .remove(KEY_CACHED_POINT_TIMESTAMPS)
            .remove(KEY_BULK_DOWNLOAD_STATE)
            .apply()
    }

    // ---------------------------------------------------------------- loading
    private fun loadInto(repo: AppRepository) {
        ensureInitialized()
        runCatching {
            prefs.getString(KEY_PROFILE, null)?.let {
                repo.loadProfile(json.decodeFromString(it))
            }
        }
        runCatching {
            prefs.getString(KEY_COLLECTION, null)?.let {
                repo.loadCollection(json.decodeFromString(it))
            }
        }
        runCatching {
            prefs.getString(KEY_WISHLIST, null)?.let {
                repo.loadWishlist(json.decodeFromString(it))
            }
        }
        runCatching {
            prefs.getString(KEY_FAVORITE_SPOTS, null)?.let {
                repo.loadFavoriteSpots(json.decodeFromString(it))
            }
        }
        runCatching {
            prefs.getString(KEY_CAPTURES, null)?.let {
                repo.loadCaptures(json.decodeFromString(it))
            }
        }
        runCatching {
            prefs.getString(KEY_TRIPS, null)?.let {
                repo.loadTrips(json.decodeFromString(it))
            }
        }
        runCatching {
            prefs.getString(KEY_JOURNAL_ENTRIES, null)?.let {
                repo.loadJournalEntries(json.decodeFromString(it))
            }
        }
        runCatching {
            prefs.getString(KEY_TRADE_LISTINGS, null)?.let {
                repo.loadTradeListings(json.decodeFromString(it))
            }
        }
        runCatching {
            prefs.getString(KEY_SAVED_IMAGES, null)?.let {
                repo.loadSavedImages(json.decodeFromString(it))
            }
        }
        runCatching {
            prefs.getString(KEY_LIKED_SPECIMENS, null)?.let {
                repo.loadLikedSpecimens(json.decodeFromString(it))
            }
        }
        runCatching {
            prefs.getString(KEY_CURRENT_LOCATION, null)?.let {
                val (lat, lng) = json.decodeFromString<Pair<Double, Double>>(it)
                repo.setCurrentLocation(lat, lng)
            }
        }
        // Sync the standalone location-monitoring flag with the profile value
        // so background workers see the latest setting.
        prefs.edit()
            .putBoolean(KEY_LOCATION_MONITORING, repo.profile.value.locationMonitoring)
            .putBoolean(KEY_NEARBY_PLACES, repo.profile.value.nearbyPlacesEnabled)
            .putBoolean(KEY_NEARBY_ALERTS, repo.profile.value.nearbyAlertsEnabled)
            .putBoolean(KEY_CLUB_ENABLED, repo.profile.value.clubEnabled)
            .putBoolean(KEY_NEARBY_FRIENDS, repo.profile.value.nearbyFriendsEnabled)
            .putBoolean(KEY_NEARBY_FRIENDS_ALERTS, repo.profile.value.nearbyFriendsAlertsEnabled)
            .putBoolean(KEY_WEATHER_ALERTS, repo.profile.value.weatherAlertsEnabled)
            .putBoolean(KEY_AURORA_ALERTS, repo.profile.value.auroraAlertsEnabled)
            .putBoolean(KEY_NOTIF_FRIEND_REQUESTS, repo.profile.value.notifFriendRequestsEnabled)
            .putBoolean(KEY_NOTIF_MESSAGES, repo.profile.value.notifMessagesEnabled)
            .putBoolean(KEY_NOTIF_NEW_POSTS, repo.profile.value.notifNewPostsEnabled)
            .putBoolean(KEY_NOTIF_TRADE_INTEREST, repo.profile.value.notifTradeInterestEnabled)
            .putBoolean(KEY_NOTIF_MARKED_TRADED, repo.profile.value.notifMarkedTradedEnabled)
            .putBoolean(KEY_NOTIF_LOCATION_APPROVED, repo.profile.value.notifLocationApprovedEnabled)
            .putBoolean(KEY_NOTIF_ENGAGEMENT_SUMMARY, repo.profile.value.notifEngagementSummaryEnabled)
            .putString(KEY_HUNTER_STATUS, repo.profile.value.hunterStatus.name)
            .putInt(KEY_SCAN_RADIUS_MILES, repo.profile.value.scanRadiusMiles)
            .apply()
        Log.d(TAG, "Loaded persisted app state into AppRepository")
    }

    // ---------------------------------------------------------------- helpers
    private inline fun <reified T> putJson(key: String, value: T) {
        ensureInitialized()
        runCatching {
            prefs.edit().putString(key, json.encodeToString(value)).apply()
        }.onFailure { Log.w(TAG, "Failed to persist $key: ${it.message}") }
    }

    private fun ensureInitialized() {
        check(::prefs.isInitialized) {
            "PersistenceManager not initialized — call initialize(context) from Application.onCreate()"
        }
    }

    /**
     * Wipes every user-generated app state key. Used during account deletion so
     * the device copy of the collection, wishlist, captures, trips, journal,
     * trade listings, saved images, and settings is removed along with the
     * account record.
     */
    fun clearAll() {
        ensureInitialized()
        prefs.edit().clear().apply()
    }
}

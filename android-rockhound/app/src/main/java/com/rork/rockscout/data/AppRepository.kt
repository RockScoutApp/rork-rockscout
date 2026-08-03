package com.rork.rockscout.data

import kotlinx.serialization.Serializable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import com.rork.rockscout.data.ProfanityFilter
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import java.util.UUID

/** A user's collected specimen with personal notes.
 *  [addedAt] is epoch millis (UTC) recording when the specimen was added to
 *  the collection; defaults to 0 for entries created before this field existed
 *  so existing JSON deserialization stays backward compatible. */
@Serializable
data class CollectionEntry(
    val specimenId: String,
    val note: String = "",
    val foundAt: String = "",
    val addedAt: Long = 0L,
)

/** RockScout Friends availability status shown to other hunters in scans/pings. */
enum class HunterStatus(val label: String, val emoji: String) {
    OFF_GRID("Off the grid", "\uD83D\uDD12"),
    ON_THE_HUNT("I'm on the hunt!", "\u26CF\uFE0F"),
    WISHING("Thinkin' Bout Rocks", "\uD83D\uDD2E"),
    LOOKING_FOR_TRADES("Looking for trades", "\uD83D\uDD04"),
    ;
    /** Whether this status makes the user visible in other users' scans. */
    val isHuntVisible: Boolean get() = this != OFF_GRID
}

@Serializable
data class UserProfile(
    val name: String = "Rock Scout",
    val bio: String = "Field collector & weekend prospector.",
    val homeRegion: String = "Arkansas, USA",
    val avatarEmoji: String = "\uD83E\uDD20",
    val locationMonitoring: Boolean = false,
    val nearbyPlacesEnabled: Boolean = false,
    val nearbyAlertsEnabled: Boolean = false,
    /** RockScout Friends master toggle — off by default. When off the user cannot
     *  scan, be scanned, ping, or be pinged. */
    val clubEnabled: Boolean = false,
    /** Nearby Friends toggle — when on, the app monitors for connected friends
     *  within the 50-mile radius and can surface them in scan results. */
    val nearbyFriendsEnabled: Boolean = false,
    /** Nearby Friends push-notification toggle — when on, the user receives a
     *  push notification when a connected friend comes within 50 miles.
     *  Independent of [nearbyFriendsEnabled] so users can monitor without alerts. */
    val nearbyFriendsAlertsEnabled: Boolean = false,
    /** Severe Weather Alerts toggle — when on, a self-rescheduling worker polls
     *  the NWS API every 3 minutes and posts a notification for active severe-
     *  weather warnings (thunderstorm, tornado, hurricane, tsunami, extreme heat,
     *  marine, high wind) in the user's area. Operates independently of
     *  [locationMonitoring] — the worker fetches the device location on its own
     *  specifically for weather alerts. */
    val weatherAlertsEnabled: Boolean = false,
    /** Aurora Alerts toggle — when on, a self-rescheduling worker checks Kp every
     *  3 minutes and posts a notification when aurora is likely visible from
     *  the user's latitude. Only fires at night. Independent of NWS weather alerts. */
    val auroraAlertsEnabled: Boolean = false,
    /** Current hunter status shown in scans and on pings. Defaults to off-grid. */
    val hunterStatus: HunterStatus = HunterStatus.OFF_GRID,
    /** Saved scan radius in miles (5/25/50/100/250). Default 50. */
    val scanRadiusMiles: Int = 50,
    /** Friend-request push-notification toggle (default ON). When off, instant
     *  friend-request push notifications are suppressed; the in-app notification
     *  row is still created so the user sees it on open. */
    val notifFriendRequestsEnabled: Boolean = true,
    /** Private-message push-notification toggle (default ON). When off, instant
     *  private-message push notifications are suppressed; the in-app notification
     *  row is still created. */
    val notifMessagesEnabled: Boolean = true,
    /** New-posts summary push-notification toggle (default ON). When off, the
     *  1-hour periodic new-posts summary notification is suppressed. */
    val notifNewPostsEnabled: Boolean = true,
    /** Trade-interest push-notification toggle (default ON). Instant push when
     *  someone expresses interest in your listing. */
    val notifTradeInterestEnabled: Boolean = true,
    /** Marked-as-traded push-notification toggle (default ON). Instant push when
     *  a listing you're interested in is marked traded. */
    val notifMarkedTradedEnabled: Boolean = true,
    /** Location-approved push-notification toggle (default ON). Instant push when
     *  your submitted location is auto-verified or Dev-verified. */
    val notifLocationApprovedEnabled: Boolean = true,
    /** Engagement-summary push-notification toggle (default ON). The 1-hour
     *  summary push for likes/comments/replies on your posts. */
    val notifEngagementSummaryEnabled: Boolean = true,
    /** Active profile background image URI (content:// or file://). Null = default gradient. */
    val backgroundImagePath: String? = null,
    /** Pending background image awaiting developer review (questionable content). */
    val pendingBackgroundPath: String? = null,
    /** Custom aurora notification Kp threshold. Null = use latitude-based default.
     *  When set, the AuroraAlertWorker fires notifications when Kp reaches this value
     *  instead of the computed latitude threshold. Range 0.0–9.0 in 0.5 steps. */
    val auroraKpThreshold: Float? = null,
    /** User's gender: "male", "female", or "rather_not_say". Shown on public profile. */
    val gender: String = "rather_not_say",
    /** Birthday as epoch millis (00:00 UTC of the birth date). Null = not set / rather not say. */
    val birthdayMillis: Long? = null,
    /** Whether the birthday is shown publicly. When false, public profiles show "Private". */
    val birthdayPublic: Boolean = false,
)

/**
 * In-memory app state for profile, collection, wishlist and favorite spots.
 * Single source of truth shared across screens via [AppRepository.instance].
 *
 * All mutations are persisted to disk via [PersistenceManager] so user data
 * survives app closes and device restarts. The location-monitoring flag is
 * also stored on a standalone key so background workers can read it even
 * when the app process is not running.
 */
class AppRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private val _collection = MutableStateFlow<List<CollectionEntry>>(
        listOf(
            CollectionEntry("quartz", "Cluster of clear points.", "Mount Ida, AR"),
            CollectionEntry("amethyst", "Deep purple geode slice.", "Brazil (purchased)"),
            CollectionEntry("pyrite", "Perfect natural cube.", "Spain (trade)"),
        )
    )
    val collection: StateFlow<List<CollectionEntry>> = _collection.asStateFlow()

    private val _wishlist = MutableStateFlow<List<String>>(
        listOf("diamond", "opal", "malachite")
    )
    val wishlist: StateFlow<List<String>> = _wishlist.asStateFlow()

    /** Specimens the user has "liked" — a lightweight personal favorite, separate
     *  from collection and wishlist. Toggled by the heart on specimen cards. */
    private val _likedSpecimens = MutableStateFlow<List<String>>(emptyList())
    val likedSpecimens: StateFlow<List<String>> = _likedSpecimens.asStateFlow()

    private val _favoriteSpots = MutableStateFlow<List<String>>(
        listOf("crater-of-diamonds", "wegner-quartz")
    )
    val favoriteSpots: StateFlow<List<String>> = _favoriteSpots.asStateFlow()

    private val _captures = MutableStateFlow<List<CapturedPhoto>>(emptyList())
    val captures: StateFlow<List<CapturedPhoto>> = _captures.asStateFlow()

    private val _allTrips = MutableStateFlow<List<Trip>>(emptyList())
    /** Active (non-archived) trips, sorted by date descending. */
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()
    /** Archived trips, sorted by completedAt descending. */
    private val _archivedTrips = MutableStateFlow<List<Trip>>(emptyList())
    val archivedTrips: StateFlow<List<Trip>> = _archivedTrips.asStateFlow()

    private val _journalEntries = MutableStateFlow<List<JournalEntry>>(emptyList())
    val journalEntries: StateFlow<List<JournalEntry>> = _journalEntries.asStateFlow()

    private val _tradeListings = MutableStateFlow<List<TradeListing>>(emptyList())
    val tradeListings: StateFlow<List<TradeListing>> = _tradeListings.asStateFlow()

    private val _savedImages = MutableStateFlow<List<SavedImage>>(emptyList())
    val savedImages: StateFlow<List<SavedImage>> = _savedImages.asStateFlow()

    /** Aurora Saved Spots — lat/lng bookmarks for tracking aurora visibility. */
    private val _auroraSavedSpots = MutableStateFlow<List<AuroraSavedSpot>>(emptyList())
    val auroraSavedSpots: StateFlow<List<AuroraSavedSpot>> = _auroraSavedSpots.asStateFlow()

    /** Simulated current GPS position (default: central Arkansas crystal country). */
    private val _currentLocation = MutableStateFlow(Pair(34.5037, -93.6321))
    val currentLocation: StateFlow<Pair<Double, Double>> = _currentLocation.asStateFlow()

    /** Triggered when profile changes require a refresh of location-based data. */
    private val _locationRefreshTrigger = MutableStateFlow(0)
    val locationRefreshTrigger: StateFlow<Int> = _locationRefreshTrigger.asStateFlow()

    /** Whether persisted state has been loaded into this repository. */
    private var loaded = false

    // ---- Bulk-load helpers called once from PersistenceManager.initialize ----
    fun loadProfile(p: UserProfile) { _profile.value = p }
    fun loadCollection(entries: List<CollectionEntry>) {
        // Always set — an empty list means the user intentionally cleared
        // their collection and we must not reseed defaults on restart.
        _collection.value = entries
    }
    fun loadWishlist(ids: List<String>) {
        _wishlist.value = ids
    }
    fun loadLikedSpecimens(ids: List<String>) {
        _likedSpecimens.value = ids
    }
    fun loadFavoriteSpots(ids: List<String>) {
        _favoriteSpots.value = ids
    }
    fun loadCaptures(photos: List<CapturedPhoto>) {
        _captures.value = photos
    }
    fun loadTrips(trips: List<Trip>) {
        _allTrips.value = trips
        _trips.value = trips.filter { !it.isArchived }.sortedByDescending { it.date }
        _archivedTrips.value = trips.filter { it.isArchived }.sortedByDescending { it.completedAt ?: 0L }
    }
    fun loadJournalEntries(entries: List<JournalEntry>) {
        _journalEntries.value = entries.sortedByDescending { it.date }
    }
    fun loadTradeListings(listings: List<TradeListing>) {
        _tradeListings.value = listings.sortedByDescending { it.createdAt }
    }

    fun loadSavedImages(images: List<SavedImage>) {
        _savedImages.value = images.sortedByDescending { it.savedAt }
    }

    fun loadAuroraSavedSpots(spots: List<AuroraSavedSpot>) {
        _auroraSavedSpots.value = spots.sortedByDescending { it.createdAt }
    }

    /** Marks persistence loaded; subsequent mutations will persist to disk. */
    fun markLoaded() { loaded = true }

    private fun persistProfile() {
        if (loaded) PersistenceManager.saveProfile(_profile.value)
    }
    private fun persistCollection() {
        if (loaded) PersistenceManager.saveCollection(_collection.value)
    }
    private fun persistWishlist() {
        if (loaded) PersistenceManager.saveWishlist(_wishlist.value)
    }
    private fun persistLikedSpecimens() {
        if (loaded) PersistenceManager.saveLikedSpecimens(_likedSpecimens.value)
    }
    private fun persistFavoriteSpots() {
        if (loaded) PersistenceManager.saveFavoriteSpots(_favoriteSpots.value)
    }
    private fun persistCaptures() {
        if (loaded) PersistenceManager.saveCaptures(_captures.value)
    }
    private fun persistTrips() {
        if (loaded) PersistenceManager.saveTrips(_allTrips.value)
    }

    /** Re-derives active/archived lists from _allTrips after a mutation. */
    private fun refreshTripFlows() {
        _trips.value = _allTrips.value.filter { !it.isArchived }.sortedByDescending { it.date }
        _archivedTrips.value = _allTrips.value.filter { it.isArchived }.sortedByDescending { it.completedAt ?: 0L }
    }
    private fun persistJournalEntries() {
        if (loaded) PersistenceManager.saveJournalEntries(_journalEntries.value)
    }
    private fun persistTradeListings() {
        if (loaded) PersistenceManager.saveTradeListings(_tradeListings.value)
    }
    private fun persistSavedImages() {
        if (loaded) PersistenceManager.saveSavedImages(_savedImages.value)
    }
    private fun persistAuroraSavedSpots() {
        if (loaded) PersistenceManager.saveAuroraSavedSpots(_auroraSavedSpots.value)
    }
    private fun persistCurrentLocation() {
        if (loaded) {
            val (lat, lng) = _currentLocation.value
            PersistenceManager.saveCurrentLocation(lat, lng)
        }
    }

    fun updateProfile(transform: (UserProfile) -> UserProfile) {
        val updated = transform(_profile.value)
        _profile.value = updated.copy(
            name = ProfanityFilter.filter(updated.name),
            bio = ProfanityFilter.filter(updated.bio),
            homeRegion = ProfanityFilter.filter(updated.homeRegion),
        )
        persistProfile()
        PersistenceManager.saveLocationMonitoringEnabled(_profile.value.locationMonitoring)
    }

    fun setLocationMonitoring(enabled: Boolean) {
        // When location monitoring is turned off, automatically disable nearby
        // spots and nearby friends — they rely on location to surface nearby dig
        // sites and friends. Weather alerts are intentionally NOT disabled here
        // because they operate independently: the WeatherAlertWorker fetches the
        // device location on its own specifically for weather alerts.
        if (!enabled && (_profile.value.nearbyPlacesEnabled || _profile.value.nearbyAlertsEnabled ||
                _profile.value.nearbyFriendsEnabled || _profile.value.nearbyFriendsAlertsEnabled)) {
            _profile.value = _profile.value.copy(
                locationMonitoring = false,
                nearbyPlacesEnabled = false,
                nearbyAlertsEnabled = false,
                nearbyFriendsEnabled = false,
                nearbyFriendsAlertsEnabled = false,
            )
            persistProfile()
            PersistenceManager.saveLocationMonitoringEnabled(false)
            PersistenceManager.saveNearbyPlacesEnabled(false)
            PersistenceManager.saveNearbyAlertsEnabled(false)
            PersistenceManager.saveNearbyFriendsEnabled(false)
            PersistenceManager.saveNearbyFriendsAlertsEnabled(false)
        } else {
            _profile.value = _profile.value.copy(
                locationMonitoring = enabled,
            )
            persistProfile()
            PersistenceManager.saveLocationMonitoringEnabled(enabled)
        }
    }

    fun setNearbyPlacesEnabled(enabled: Boolean) {
        _profile.value = _profile.value.copy(nearbyPlacesEnabled = enabled)
        persistProfile()
        PersistenceManager.saveNearbyPlacesEnabled(enabled)
    }

    fun setNearbyAlertsEnabled(enabled: Boolean) {
        _profile.value = _profile.value.copy(nearbyAlertsEnabled = enabled)
        persistProfile()
        PersistenceManager.saveNearbyAlertsEnabled(enabled)
    }

    fun setNearbyFriendsEnabled(enabled: Boolean) {
        _profile.value = _profile.value.copy(nearbyFriendsEnabled = enabled)
        persistProfile()
        PersistenceManager.saveNearbyFriendsEnabled(enabled)
    }

    fun setNearbyFriendsAlertsEnabled(enabled: Boolean) {
        _profile.value = _profile.value.copy(nearbyFriendsAlertsEnabled = enabled)
        persistProfile()
        PersistenceManager.saveNearbyFriendsAlertsEnabled(enabled)
    }

    fun setWeatherAlertsEnabled(enabled: Boolean) {
        _profile.value = _profile.value.copy(weatherAlertsEnabled = enabled)
        persistProfile()
        PersistenceManager.saveWeatherAlertsEnabled(enabled)
    }

    fun setAuroraAlertsEnabled(enabled: Boolean) {
        _profile.value = _profile.value.copy(auroraAlertsEnabled = enabled)
        persistProfile()
        PersistenceManager.saveAuroraAlertsEnabled(enabled)
    }

    fun setClubEnabled(enabled: Boolean) {
        _profile.value = _profile.value.copy(clubEnabled = enabled)
        persistProfile()
        PersistenceManager.saveClubEnabled(enabled)
    }

    fun setNotifFriendRequestsEnabled(enabled: Boolean) {
        _profile.value = _profile.value.copy(notifFriendRequestsEnabled = enabled)
        persistProfile()
        PersistenceManager.saveNotifFriendRequestsEnabled(enabled)
    }

    fun setNotifMessagesEnabled(enabled: Boolean) {
        _profile.value = _profile.value.copy(notifMessagesEnabled = enabled)
        persistProfile()
        PersistenceManager.saveNotifMessagesEnabled(enabled)
    }

    fun setNotifNewPostsEnabled(enabled: Boolean) {
        _profile.value = _profile.value.copy(notifNewPostsEnabled = enabled)
        persistProfile()
        PersistenceManager.saveNotifNewPostsEnabled(enabled)
    }

    fun setNotifTradeInterestEnabled(enabled: Boolean) {
        _profile.value = _profile.value.copy(notifTradeInterestEnabled = enabled)
        persistProfile()
        PersistenceManager.saveNotifTradeInterestEnabled(enabled)
    }

    fun setNotifMarkedTradedEnabled(enabled: Boolean) {
        _profile.value = _profile.value.copy(notifMarkedTradedEnabled = enabled)
        persistProfile()
        PersistenceManager.saveNotifMarkedTradedEnabled(enabled)
    }

    fun setNotifLocationApprovedEnabled(enabled: Boolean) {
        _profile.value = _profile.value.copy(notifLocationApprovedEnabled = enabled)
        persistProfile()
        PersistenceManager.saveNotifLocationApprovedEnabled(enabled)
    }

    fun setNotifEngagementSummaryEnabled(enabled: Boolean) {
        _profile.value = _profile.value.copy(notifEngagementSummaryEnabled = enabled)
        persistProfile()
        PersistenceManager.saveNotifEngagementSummaryEnabled(enabled)
    }

    /** Epoch millis of the last status change, or 0 if never changed. */
    private val _lastStatusChangeAt = MutableStateFlow(0L)
    val lastStatusChangeAt: StateFlow<Long> = _lastStatusChangeAt.asStateFlow()

    /** Whether the user can change status right now, given their tier.
     *  Free users are rate-limited to one change per 10 minutes; Premium is waived. */
    fun canChangeStatus(isPremium: Boolean): Boolean = true

    /** Minutes remaining until the user can change status again (always 0 — no cooldown). */
    fun statusCooldownMinutesRemaining(isPremium: Boolean): Int = 0

    fun setHunterStatus(status: HunterStatus) {
        _profile.value = _profile.value.copy(hunterStatus = status)
        _lastStatusChangeAt.value = System.currentTimeMillis()
        persistProfile()
        PersistenceManager.saveHunterStatus(status)
    }

    fun setScanRadiusMiles(miles: Int) {
        _profile.value = _profile.value.copy(scanRadiusMiles = miles)
        persistProfile()
        PersistenceManager.saveScanRadiusMiles(miles)
    }

    /** Sets the profile background image (already passed moderation). */
    fun setBackgroundImagePath(path: String?) {
        _profile.value = _profile.value.copy(
            backgroundImagePath = path,
            pendingBackgroundPath = null,
        )
        persistProfile()
    }

    /** Sets a pending background image awaiting developer review. */
    fun setPendingBackgroundPath(path: String?) {
        _profile.value = _profile.value.copy(pendingBackgroundPath = path)
        persistProfile()
    }

    /** Adds a pending image URI to a field capture awaiting review. */
    fun setCapturePendingImage(captureId: String, imageUri: String?) {
        _captures.value = _captures.value.map {
            if (it.id == captureId) it.copy(pendingImageUri = imageUri) else it
        }
        persistCaptures()
    }

    fun setCurrentLocation(lat: Double, lng: Double) {
        _currentLocation.value = Pair(lat, lng)
        _locationRefreshTrigger.value++
        persistCurrentLocation()
    }

    /** Updates the user's home region and triggers a refresh of location-based data. */
    fun setHomeRegion(region: String) {
        _profile.value = _profile.value.copy(homeRegion = region)
        _locationRefreshTrigger.value++
        // Re-resolve the user's timezone from the new home region.
        UserTimezoneProvider.onHomeRegionChanged()
        persistProfile()
    }

    /** Saves all profile changes and triggers a refresh of location-based data. */
    fun saveProfileChanges() {
        _locationRefreshTrigger.value++
        persistProfile()
        PersistenceManager.saveLocationMonitoringEnabled(_profile.value.locationMonitoring)
    }

    // Collection -----------------------------------------------------------
    fun isCollected(specimenId: String): Boolean =
        _collection.value.any { it.specimenId == specimenId }

    fun toggleCollection(specimenId: String) {
        val current = _collection.value
        _collection.value = if (current.any { it.specimenId == specimenId }) {
            current.filterNot { it.specimenId == specimenId }
        } else {
            current + CollectionEntry(specimenId, addedAt = System.currentTimeMillis())
        }
        persistCollection()
        AchievementsRepository.checkAchievements()
    }

    /** Batch-removes collection entries by specimen id. Used by the mass-delete
     *  selection flow on the My Rocks screen. */
    fun removeCollectionEntries(specimenIds: Set<String>) {
        if (specimenIds.isEmpty()) return
        _collection.value = _collection.value.filterNot { it.specimenId in specimenIds }
        persistCollection()
    }

    fun updateCollectionNote(specimenId: String, note: String, foundAt: String) {
        _collection.value = _collection.value.map {
            if (it.specimenId == specimenId) it.copy(
                note = ProfanityFilter.filter(note),
                foundAt = ProfanityFilter.filter(foundAt),
            ) else it
        }
        persistCollection()
    }

    // Wishlist -------------------------------------------------------------
    fun isWishlisted(specimenId: String): Boolean = _wishlist.value.contains(specimenId)

    fun toggleWishlist(specimenId: String) {
        val current = _wishlist.value
        _wishlist.value = if (current.contains(specimenId)) {
            current - specimenId
        } else {
            current + specimenId
        }
        persistWishlist()
        AchievementsRepository.checkAchievements()
    }

    // Liked specimens ------------------------------------------------------
    fun isLiked(specimenId: String): Boolean = _likedSpecimens.value.contains(specimenId)

    /**
     * Deterministic pseudo-random base like count for a specimen.
     * Generates a stable number from the specimen ID hash so each specimen
     * always shows the same count across sessions. Range: 47–18,499.
     */
    fun specimenBaseLikeCount(specimenId: String): Int {
        val hash = specimenId.hashCode()
        val positive = if (hash < 0) -hash else hash
        return 47 + (positive % 18_453)
    }

    /**
     * Full like count for a specimen: base count + 1 if the current user
     * has liked it. This makes the counter increment when the user taps
     * the heart, giving the "running counter" effect.
     */
    fun specimenLikeCount(specimenId: String): Int {
        val base = specimenBaseLikeCount(specimenId)
        return if (isLiked(specimenId)) base + 1 else base
    }

    /**
     * Deterministic pseudo-random base like count for a saved image.
     * Generates a stable number from the image ID hash. Range: 12–849.
     */    fun savedImageBaseLikeCount(imageId: String): Int {
        val hash = imageId.hashCode()
        val positive = if (hash < 0) -hash else hash
        return 12 + (positive % 838)
    }

    /**
     * Full like count for a saved image: base count + 1 if the user has liked it.
     */
    fun savedImageLikeCount(imageId: String): Int {
        val base = savedImageBaseLikeCount(imageId)
        val isLiked = _savedImages.value.firstOrNull { it.id == imageId }?.liked == true
        return if (isLiked) base + 1 else base
    }

    fun toggleLike(specimenId: String) {
        val current = _likedSpecimens.value
        _likedSpecimens.value = if (current.contains(specimenId)) {
            current - specimenId
        } else {
            current + specimenId
        }
        persistLikedSpecimens()
    }

    // Favorite spots -------------------------------------------------------
    fun isFavoriteSpot(locationId: String): Boolean = _favoriteSpots.value.contains(locationId)

    fun toggleFavoriteSpot(locationId: String) {
        val current = _favoriteSpots.value
        _favoriteSpots.value = if (current.contains(locationId)) {
            current - locationId
        } else {
            current + locationId
        }
        persistFavoriteSpots()
        AchievementsRepository.checkAchievements()
    }

    // Captured photos -------------------------------------------------------
    fun addCapture(photo: CapturedPhoto) {
        _captures.value = listOf(photo.copy(
            customName = ProfanityFilter.filter(photo.customName),
            customLocation = ProfanityFilter.filter(photo.customLocation),
            generalInfo = ProfanityFilter.filter(photo.generalInfo),
            note = ProfanityFilter.filter(photo.note),
        )) + _captures.value
        persistCaptures()
        AchievementsRepository.checkAchievements()
        SyncQueueManager.enqueue(SyncQueueManager.SyncTable.CAPTURES, photo.id)
    }

    fun updateCaptureNote(captureId: String, note: String) {
        _captures.value = _captures.value.map {
            if (it.id == captureId) it.copy(note = ProfanityFilter.filter(note)) else it
        }
        persistCaptures()
        SyncQueueManager.enqueue(SyncQueueManager.SyncTable.CAPTURES, captureId)
    }

    /** Updates the editable user-defined fields on a field capture. */
    fun updateCaptureFields(
        captureId: String,
        customName: String,
        customLocation: String,
        generalInfo: String,
        latitude: Double? = null,
        longitude: Double? = null,
    ) {
        _captures.value = _captures.value.map {
            if (it.id == captureId) {
                it.copy(
                    customName = ProfanityFilter.filter(customName),
                    customLocation = ProfanityFilter.filter(customLocation),
                    generalInfo = ProfanityFilter.filter(generalInfo),
                    latitude = latitude,
                    longitude = longitude,
                )
            } else it
        }
        persistCaptures()
        SyncQueueManager.enqueue(SyncQueueManager.SyncTable.CAPTURES, captureId)
    }

    /** Adds an image URI to an existing field capture card. */
    fun addImageToCapture(captureId: String, imageUri: String) {
        _captures.value = _captures.value.map {
            if (it.id == captureId) {
                it.copy(imageUris = it.imageUris + imageUri)
            } else it
        }
        persistCaptures()
        SyncQueueManager.enqueue(SyncQueueManager.SyncTable.CAPTURES, captureId)
    }

    /** Removes an image from a field capture card by index. */
    fun removeImageFromCapture(captureId: String, imageIndex: Int) {
        _captures.value = _captures.value.map {
            if (it.id == captureId && imageIndex in it.imageUris.indices) {
                it.copy(imageUris = it.imageUris.toMutableList().also { list -> list.removeAt(imageIndex) })
            } else it
        }
        persistCaptures()
        SyncQueueManager.enqueue(SyncQueueManager.SyncTable.CAPTURES, captureId)
    }

    /** Toggles a field capture's membership in My Rocks. */
    fun toggleCaptureInCollection(captureId: String) {
        _captures.value = _captures.value.map {
            if (it.id == captureId) it.copy(inCollection = !it.inCollection) else it
        }
        persistCaptures()
        SyncQueueManager.enqueue(SyncQueueManager.SyncTable.CAPTURES, captureId)
    }

    /** Toggles a field capture's membership in the Wishlist. */
    fun toggleCaptureInWishlist(captureId: String) {
        _captures.value = _captures.value.map {
            if (it.id == captureId) it.copy(inWishlist = !it.inWishlist) else it
        }
        persistCaptures()
        SyncQueueManager.enqueue(SyncQueueManager.SyncTable.CAPTURES, captureId)
    }

    /**
     * Merges another field capture [otherId] into [targetId].
     * - Combines all image URIs (target first, then other's, deduplicated).
     * - Keeps the target's editable fields if set, otherwise adopts the other's.
     * - The merged capture inherits the union of inCollection/inWishlist flags.
     * - The other capture is removed from the list.
     */
    fun mergeCaptures(targetId: String, otherId: String) {
        val current = _captures.value
        val target = current.firstOrNull { it.id == targetId } ?: return
        val other = current.firstOrNull { it.id == otherId } ?: return
        if (target.id == other.id) return

        val merged = target.copy(
            imageUris = (target.imageUris + other.imageUris).distinct(),
            customName = ProfanityFilter.filter(target.customName.ifBlank { other.customName }),
            customLocation = ProfanityFilter.filter(target.customLocation.ifBlank { other.customLocation }),
            generalInfo = ProfanityFilter.filter(target.generalInfo.ifBlank { other.generalInfo }),
            note = ProfanityFilter.filter(target.note.ifBlank { other.note }),
            inCollection = target.inCollection || other.inCollection,
            inWishlist = target.inWishlist || other.inWishlist,
        )
        _captures.value = current
            .filterNot { it.id == otherId || it.id == targetId }
            .let { listOf(merged) + it }
        persistCaptures()
        SyncQueueManager.enqueue(SyncQueueManager.SyncTable.CAPTURES, targetId)
        SyncQueueManager.enqueueDelete(SyncQueueManager.SyncTable.CAPTURES, otherId)
    }

    /** Updates the coordinates of a field capture (for the specimen marker map). */
    fun updateCaptureCoordinates(captureId: String, latitude: Double?, longitude: Double?) {
        _captures.value = _captures.value.map {
            if (it.id == captureId) it.copy(latitude = latitude, longitude = longitude) else it
        }
        persistCaptures()
        SyncQueueManager.enqueue(SyncQueueManager.SyncTable.CAPTURES, captureId)
    }

    fun removeCapture(captureId: String) {
        _captures.value = _captures.value.filterNot { it.id == captureId }
        persistCaptures()
        SyncQueueManager.enqueueDelete(SyncQueueManager.SyncTable.CAPTURES, captureId)
    }

    /** Batch-removes all captures whose id is in [ids]. */
    fun removeCaptures(ids: Set<String>) {
        if (ids.isEmpty()) return
        _captures.value = _captures.value.filterNot { it.id in ids }
        persistCaptures()
        ids.forEach { SyncQueueManager.enqueueDelete(SyncQueueManager.SyncTable.CAPTURES, it) }
    }

    /** Batch-moves captures into My Rocks (sets inCollection = true). */
    fun moveCapturesToCollection(ids: Set<String>) {
        if (ids.isEmpty()) return
        _captures.value = _captures.value.map {
            if (it.id in ids) it.copy(inCollection = true) else it
        }
        persistCaptures()
        ids.forEach { SyncQueueManager.enqueue(SyncQueueManager.SyncTable.CAPTURES, it) }
    }

    /** Batch-moves captures into the Wishlist (sets inWishlist = true). */
    fun moveCapturesToWishlist(ids: Set<String>) {
        if (ids.isEmpty()) return
        _captures.value = _captures.value.map {
            if (it.id in ids) it.copy(inWishlist = true) else it
        }
        persistCaptures()
        ids.forEach { SyncQueueManager.enqueue(SyncQueueManager.SyncTable.CAPTURES, it) }
    }

    // Trips -----------------------------------------------------------------
    fun saveTrip(trip: Trip) {
        val filtered = trip.copy(
            name = ProfanityFilter.filter(trip.name),
            notes = ProfanityFilter.filter(trip.notes),
            targetSpecimens = trip.targetSpecimens.map { ProfanityFilter.filter(it) },
            gearChecklist = trip.gearChecklist.map { ProfanityFilter.filter(it) },
            stops = trip.stops.map { stop ->
                stop.copy(locationName = ProfanityFilter.filter(stop.locationName))
            },
        )
        val existing = _allTrips.value.firstOrNull { it.id == filtered.id }
        _allTrips.value = (if (existing == null) {
            listOf(filtered) + _allTrips.value
        } else {
            _allTrips.value.map { if (it.id == filtered.id) filtered else it }
        })
        refreshTripFlows()
        persistTrips()
        if (existing == null) {
            AchievementsRepository.checkAchievements()
        }
        SyncQueueManager.enqueue(SyncQueueManager.SyncTable.TRIPS, filtered.id)
    }

    fun deleteTrip(tripId: String) {
        _allTrips.value = _allTrips.value.filterNot { it.id == tripId }
        refreshTripFlows()
        persistTrips()
        SyncQueueManager.enqueueDelete(SyncQueueManager.SyncTable.TRIPS, tripId)
    }

    /** Adds a stop to an existing trip and persists it. */
    fun addTripStop(tripId: String, stop: TripStop) {
        _allTrips.value = _allTrips.value.map { trip ->
            if (trip.id == tripId) {
                trip.copy(stops = trip.stops + stop.copy(order = trip.stops.size))
            } else trip
        }
        refreshTripFlows()
        persistTrips()
        SyncQueueManager.enqueue(SyncQueueManager.SyncTable.TRIPS, tripId)
    }

    /** Updates the specimen markers on a trip and persists it. */
    fun updateTripSpecimenMarkers(tripId: String, markers: List<SpecimenMarker>) {
        val filtered = markers.map { marker ->
            marker.copy(
                name = ProfanityFilter.filter(marker.name),
                description = ProfanityFilter.filter(marker.description),
            )
        }
        _allTrips.value = _allTrips.value.map { trip ->
            if (trip.id == tripId) trip.copy(specimenMarkers = filtered) else trip
        }
        refreshTripFlows()
        persistTrips()
        SyncQueueManager.enqueue(SyncQueueManager.SyncTable.TRIPS, tripId)
    }

    fun getTrip(tripId: String): Trip? = _allTrips.value.firstOrNull { it.id == tripId }

    /** Archives a trip: sets isArchived = true, completedAt = now. */
    fun archiveTrip(tripId: String) {
        _allTrips.value = _allTrips.value.map {
            if (it.id == tripId) it.copy(isArchived = true, completedAt = System.currentTimeMillis()) else it
        }
        refreshTripFlows()
        persistTrips()
        AchievementsRepository.checkAchievements()
        SyncQueueManager.enqueue(SyncQueueManager.SyncTable.TRIPS, tripId)
    }

    /** Restores an archived trip back to active. */
    fun restoreTrip(tripId: String) {
        _allTrips.value = _allTrips.value.map {
            if (it.id == tripId) it.copy(isArchived = false, completedAt = null) else it
        }
        refreshTripFlows()
        persistTrips()
        AchievementsRepository.checkAchievements()
        SyncQueueManager.enqueue(SyncQueueManager.SyncTable.TRIPS, tripId)
    }

    /** Permanently deletes an archived trip. */
    fun deleteArchivedTrip(tripId: String) {
        _allTrips.value = _allTrips.value.filterNot { it.id == tripId }
        refreshTripFlows()
        persistTrips()
        SyncQueueManager.enqueueDelete(SyncQueueManager.SyncTable.TRIPS, tripId)
    }

    // Journal entries -------------------------------------------------------
    fun saveJournalEntry(entry: JournalEntry) {
        val filtered = entry.copy(
            location = ProfanityFilter.filter(entry.location),
            weatherSummary = ProfanityFilter.filter(entry.weatherSummary),
            notes = ProfanityFilter.filter(entry.notes),
        )
        val existing = _journalEntries.value.firstOrNull { it.id == filtered.id }
        _journalEntries.value = (if (existing == null) {
            listOf(filtered) + _journalEntries.value
        } else {
            _journalEntries.value.map { if (it.id == filtered.id) filtered else it }
        }).sortedByDescending { it.date }
        persistJournalEntries()
        SyncQueueManager.enqueue(SyncQueueManager.SyncTable.FIELD_JOURNAL, filtered.id)
    }

    fun deleteJournalEntry(entryId: String) {
        _journalEntries.value = _journalEntries.value.filterNot { it.id == entryId }
        persistJournalEntries()
        SyncQueueManager.enqueueDelete(SyncQueueManager.SyncTable.FIELD_JOURNAL, entryId)
    }

    fun getJournalEntry(entryId: String): JournalEntry? =
        _journalEntries.value.firstOrNull { it.id == entryId }

    // Trade listings -------------------------------------------------------
    fun saveTradeListing(listing: TradeListing) {
        // Stamp the owner user id from the current session so backend sync +
        // the trade-interest flow can route notifications to the right user.
        val ownerId = AuthRepository.instance.currentUserId
        val ownerName = listing.ownerUsername
            ?: _profile.value.name.takeIf { it.isNotBlank() }
            ?: AuthRepository.instance.currentUserEmail?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
        val filtered = listing.copy(
            specimenName = ProfanityFilter.filter(listing.specimenName),
            condition = ProfanityFilter.filter(listing.condition),
            description = ProfanityFilter.filter(listing.description),
            wantInReturn = ProfanityFilter.filter(listing.wantInReturn),
            tags = listing.tags.map { ProfanityFilter.filter(it) },
            ownerUserId = listing.ownerUserId ?: ownerId,
            ownerUsername = ownerName,
        )
        val existing = _tradeListings.value.firstOrNull { it.id == filtered.id }
        _tradeListings.value = (if (existing == null) {
            listOf(filtered) + _tradeListings.value
        } else {
            _tradeListings.value.map { if (it.id == filtered.id) filtered else it }
        }).sortedByDescending { it.createdAt }
        persistTradeListings()
    }

    fun deleteTradeListing(listingId: String) {
        _tradeListings.value = _tradeListings.value.filterNot { it.id == listingId }
        persistTradeListings()
    }

    fun markTradeListingTraded(listingId: String) {
        val me = AuthRepository.instance.currentUserId
        val myName = _profile.value.name.takeIf { it.isNotBlank() }
            ?: AuthRepository.instance.currentUserEmail?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
        val tradedListing = _tradeListings.value.firstOrNull { it.id == listingId }
        _tradeListings.value = _tradeListings.value.map {
            if (it.id == listingId) {
                // If a user marks a listing traded, treat it as their completed trade.
                // Pre-loaded/demo listings often carry a mock owner id, so we transfer
                // ownership to the current user so it shows up in My Trades → Completed.
                it.copy(
                    status = "traded",
                    ownerUserId = me ?: it.ownerUserId,
                    ownerUsername = myName ?: it.ownerUsername,
                )
            } else {
                it
            }
        }
        persistTradeListings()

        // Notify all users who expressed interest in this listing that it was traded.
        if (tradedListing != null) {
            scope.launch {
                val interests = LocalDataStore.getTable<MockDataSeeder.LocalTradeInterest>(
                    LocalDataStore.KEY_TRADE_INTERESTS
                ).filter { it.listing_id == listingId }
                for (interest in interests) {
                    if (interest.interested_user_id == me) continue
                    NotificationRepository.instance.createNotification(
                        userId = interest.interested_user_id,
                        type = NotificationRepository.TYPE_MARKED_TRADED,
                        actorId = me,
                        refId = listingId,
                        body = "${tradedListing.specimenName} was marked as traded!",
                        deepLinkTarget = "my_trades",
                    )
                }
            }
        }
    }

    // Saved images ----------------------------------------------------------
    fun addSavedImage(url: String, localUri: String? = null): Boolean {
        if (url.isBlank()) return false
        val current = _savedImages.value
        if (current.any { it.url == url }) return false
        val id = "${System.currentTimeMillis()}_${url.hashCode().toUInt()}"
        _savedImages.value = listOf(SavedImage(id = id, url = url, localUri = localUri)) + current
        persistSavedImages()
        SyncQueueManager.enqueue(SyncQueueManager.SyncTable.SAVED_IMAGES, id)
        return true
    }

    fun removeSavedImage(id: String) {
        _savedImages.value = _savedImages.value.filterNot { it.id == id }
        persistSavedImages()
        SyncQueueManager.enqueueDelete(SyncQueueManager.SyncTable.SAVED_IMAGES, id)
    }

    fun isSavedImage(url: String): Boolean = _savedImages.value.any { it.url == url }

    fun toggleSavedImageLike(id: String) {
        _savedImages.value = _savedImages.value.map {
            if (it.id == id) it.copy(liked = !it.liked) else it
        }
        persistSavedImages()
        SyncQueueManager.enqueue(SyncQueueManager.SyncTable.SAVED_IMAGES, id)
    }

    /** Updates the image URLs on a field capture with remote (uploaded) URLs. Called by SyncQueueManager after photo upload. */
    fun updateCaptureImageUrls(captureId: String, imageUris: List<String>) {
        _captures.value = _captures.value.map {
            if (it.id == captureId) it.copy(imageUris = imageUris) else it
        }
        persistCaptures()
    }

    /** Updates the URL on a saved image with the remote (uploaded) URL. Called by SyncQueueManager after photo upload. */
    fun updateSavedImageUrl(imageId: String, remoteUrl: String) {
        _savedImages.value = _savedImages.value.map {
            if (it.id == imageId) it.copy(url = remoteUrl) else it
        }
        persistSavedImages()
    }

    /** Updates the photo URLs on a journal entry with remote (uploaded) URLs. Called by SyncQueueManager after photo upload. */
    fun updateJournalPhotoUrls(entryId: String, photoUris: List<String>) {
        _journalEntries.value = _journalEntries.value.map {
            if (it.id == entryId) it.copy(photoUris = photoUris) else it
        }
        persistJournalEntries()
    }

    // Aurora Saved Spots -------------------------------------------------
    fun addAuroraSavedSpot(name: String, latitude: Double, longitude: Double) {
        val spot = AuroraSavedSpot(
            id = UUID.randomUUID().toString(),
            name = ProfanityFilter.filter(name),
            latitude = latitude,
            longitude = longitude,
        )
        _auroraSavedSpots.value = listOf(spot) + _auroraSavedSpots.value
        persistAuroraSavedSpots()
    }

    fun removeAuroraSavedSpot(spotId: String) {
        _auroraSavedSpots.value = _auroraSavedSpots.value.filterNot { it.id == spotId }
        persistAuroraSavedSpots()
    }

    // Aurora Kp Threshold -------------------------------------------------
    fun setAuroraKpThreshold(threshold: Float?) {
        _profile.value = _profile.value.copy(auroraKpThreshold = threshold)
        persistProfile()
        if (threshold != null) {
            PersistenceManager.saveAuroraKpThreshold(threshold)
        } else {
            PersistenceManager.saveAuroraKpThreshold(-1f)
        }
    }

    fun getAuroraKpThreshold(): Float? {
        val profileVal = _profile.value.auroraKpThreshold
        if (profileVal != null) return profileVal
        val stored = PersistenceManager.loadAuroraKpThreshold()
        return if (stored >= 0f) stored else null
    }

    fun getTradeListing(listingId: String): TradeListing? =
        _tradeListings.value.firstOrNull { it.id == listingId }

    /** Auto-expires any listing whose [expiresAt] has passed and is still "active".
     *  Called on screen load so both the Trade Board and My Trades see consistent data.
     *  Returns true if any listings were expired. */
    fun expireStaleListings(): Boolean {
        val now = System.currentTimeMillis()
        val current = _tradeListings.value
        var changed = false
        _tradeListings.value = current.map { listing ->
            if (listing.status == "active" && listing.expiresAt <= now) {
                changed = true
                listing.copy(status = "expired")
            } else {
                listing
            }
        }
        if (changed) persistTradeListings()
        return changed
    }

    /** Creates a fresh listing from an expired one, with a new 14-day clock.
     *  Copies the specimen name, photo, description, tags, and type. */
    fun relistListing(expiredListing: TradeListing): String {
        val newId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val fresh = expiredListing.copy(
            id = newId,
            status = "active",
            createdAt = now,
            expiresAt = now + 14L * 24 * 60 * 60 * 1000,
        )
        saveTradeListing(fresh)
        return newId
    }

    companion object {
        val instance: AppRepository by lazy { AppRepository() }

        /** Cooldown disabled — all users can change status freely. */
        const val STATUS_COOLDOWN_MS = 0L

        /** Great-circle distance in miles between two lat/lng points. */
        fun distanceMiles(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val r = 3958.8
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return r * c
        }
    }
}

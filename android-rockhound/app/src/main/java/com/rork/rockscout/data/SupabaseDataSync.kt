package com.rork.rockscout.data

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Supabase data sync manager — local-first with background push/pull.
 *
 * Keeps [PersistenceManager] as the primary local store for offline capability.
 * On sign-in, pulls all user data from Supabase and merges with local.
 * On mutations, pushes changes to Supabase (debounced via the persist hooks).
 *
 * Uses the Supabase REST API (PostgREST) via the shared [NetworkClient].
 * All tables are per-user with RLS on auth.uid(), so the access token from
 * [AuthRepository] gates all operations.
 *
 * Tables synced:
 * - rockscout_collection ← CollectionEntry
 * - rockscout_wishlist ← wishlist IDs
 * - rockscout_liked_specimens ← liked specimen IDs
 * - rockscout_favorite_spots ← favorite spot IDs
 * - rockscout_captures ← CapturedPhoto
 * - rockscout_saved_images ← SavedImage
 * - rockscout_field_journal ← JournalEntry
 * - rockscout_trips ← Trip
 * - rockscout_trade_listings ← TradeListing
 * - rockscout_aurora_saved_spots ← AuroraSavedSpot
 * - rockscout_profiles ← UserProfile (display name, avatar, status, etc.)
 */
object SupabaseDataSync {

    private const val TAG = "SupabaseDataSync"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val syncMutex = Mutex()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val client = NetworkClient.client

    private fun baseUrl(): String =
        BuildSecrets.resolve("EXPO_PUBLIC_SUPABASE_URL", BuildSecrets.SUPABASE_URL)

    private fun anonKey(): String =
        BuildSecrets.resolve("EXPO_PUBLIC_SUPABASE_ANON_KEY", BuildSecrets.SUPABASE_ANON_KEY)

    private fun accessToken(): String? =
        LocalDataStore.getString(LocalDataStore.KEY_SUPABASE_ACCESS_TOKEN)

    private fun userId(): String? =
        AuthRepository.instance.currentUserId

    /** True when sync is available (user is signed in with Supabase tokens). */
    val isEnabled: Boolean
        get() = !accessToken().isNullOrBlank() && !userId().isNullOrBlank()

    // ─── Pull: download all user data from Supabase ──────────────────────

    /**
     * Pull all user data from Supabase and merge into local storage.
     * Called on sign-in. Remote data wins on conflict (last-write-wins by
     * updated_at / created_at). Local-only data is pushed up after pull.
     */
    suspend fun pullAll(): Result<Unit> = syncMutex.withLock {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        val uid = userId() ?: return Result.failure(Exception("No user ID"))
        val url = baseUrl()
        val key = anonKey()

        return try {
            Log.i(TAG, "Starting full pull for user=$uid")

            // Pull collection
            pullCollection(url, key, token, uid)
            // Pull wishlist
            pullWishlist(url, key, token, uid)
            // Pull liked specimens
            pullLikedSpecimens(url, key, token, uid)
            // Pull favorite spots
            pullFavoriteSpots(url, key, token, uid)
            // Pull captures
            pullCaptures(url, key, token, uid)
            // Pull saved images
            pullSavedImages(url, key, token, uid)
            // Pull journal entries
            pullJournalEntries(url, key, token, uid)
            // Pull trips
            pullTrips(url, key, token, uid)
            // Pull trade listings
            pullTradeListings(url, key, token, uid)
            // Pull aurora saved spots
            pullAuroraSavedSpots(url, key, token, uid)
            // Pull profile
            pullProfile(url, key, token, uid)

            Log.i(TAG, "Full pull complete for user=$uid")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Pull failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun fetchTable(url: String, key: String, token: String, table: String, userId: String): JsonArray {
        val response = client.get("$url/rest/v1/$table?user_id=eq.$userId") {
            header("apikey", key)
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        if (!response.status.isSuccess()) {
            Log.w(TAG, "Fetch $table failed: ${response.status}")
            return JsonArray(emptyList())
        }
        val body = response.body<String>()
        return json.parseToJsonElement(body).jsonArray
    }

    private suspend fun pullCollection(url: String, key: String, token: String, uid: String) {
        val rows = fetchTable(url, key, token, "rockscout_collection", uid)
        if (rows.isEmpty()) return
        val entries = rows.mapNotNull { row ->
            try {
                val obj = row.jsonObject
                CollectionEntry(
                    specimenId = obj["specimen_id"]?.jsonPrimitive?.content ?: return@mapNotNull null,
                    note = obj["note"]?.jsonPrimitive?.content ?: "",
                    foundAt = obj["found_at"]?.jsonPrimitive?.content ?: "",
                    addedAt = obj["added_at"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0L,
                )
            } catch (e: Exception) { null }
        }
        if (entries.isNotEmpty()) {
            AppRepository.instance.loadCollection(entries)
            PersistenceManager.saveCollection(entries)
            Log.d(TAG, "Pulled ${entries.size} collection entries")
        }
    }

    private suspend fun pullWishlist(url: String, key: String, token: String, uid: String) {
        val rows = fetchTable(url, key, token, "rockscout_wishlist", uid)
        if (rows.isEmpty()) return
        val ids = rows.mapNotNull { it.jsonObject["specimen_id"]?.jsonPrimitive?.content }
        if (ids.isNotEmpty()) {
            AppRepository.instance.loadWishlist(ids)
            PersistenceManager.saveWishlist(ids)
            Log.d(TAG, "Pulled ${ids.size} wishlist items")
        }
    }

    private suspend fun pullLikedSpecimens(url: String, key: String, token: String, uid: String) {
        val rows = fetchTable(url, key, token, "rockscout_liked_specimens", uid)
        if (rows.isEmpty()) return
        val ids = rows.mapNotNull { it.jsonObject["specimen_id"]?.jsonPrimitive?.content }
        if (ids.isNotEmpty()) {
            AppRepository.instance.loadLikedSpecimens(ids)
            PersistenceManager.saveLikedSpecimens(ids)
            Log.d(TAG, "Pulled ${ids.size} liked specimens")
        }
    }

    private suspend fun pullFavoriteSpots(url: String, key: String, token: String, uid: String) {
        val rows = fetchTable(url, key, token, "rockscout_favorite_spots", uid)
        if (rows.isEmpty()) return
        val ids = rows.mapNotNull { it.jsonObject["spot_id"]?.jsonPrimitive?.content }
        if (ids.isNotEmpty()) {
            AppRepository.instance.loadFavoriteSpots(ids)
            PersistenceManager.saveFavoriteSpots(ids)
            Log.d(TAG, "Pulled ${ids.size} favorite spots")
        }
    }

    private suspend fun pullCaptures(url: String, key: String, token: String, uid: String) {
        val rows = fetchTable(url, key, token, "rockscout_captures", uid)
        if (rows.isEmpty()) return
        val captures = rows.mapNotNull { row ->
            try {
                val obj = row.jsonObject
                CapturedPhoto(
                    id = obj["id"]?.jsonPrimitive?.content ?: return@mapNotNull null,
                    specimenId = obj["specimen_id"]?.jsonPrimitive?.content ?: "field-camera",
                    specimenEmoji = obj["specimen_emoji"]?.jsonPrimitive?.content ?: "📷",
                    confidence = 0,
                    timestamp = obj["created_at"]?.jsonPrimitive?.content?.let { parseIsoToMillis(it) } ?: 0L,
                    customName = obj["custom_name"]?.jsonPrimitive?.content ?: "",
                    customLocation = obj["custom_location"]?.jsonPrimitive?.content ?: "",
                    generalInfo = obj["general_info"]?.jsonPrimitive?.content ?: "",
                    imageUris = obj["image_urls"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList(),
                    inCollection = obj["in_collection"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false,
                    inWishlist = obj["in_wishlist"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false,
                    latitude = obj["latitude"]?.jsonPrimitive?.content?.toDoubleOrNull(),
                    longitude = obj["longitude"]?.jsonPrimitive?.content?.toDoubleOrNull(),
                )
            } catch (e: Exception) { null }
        }
        if (captures.isNotEmpty()) {
            AppRepository.instance.loadCaptures(captures)
            PersistenceManager.saveCaptures(captures)
            Log.d(TAG, "Pulled ${captures.size} captures")
        }
    }

    private suspend fun pullSavedImages(url: String, key: String, token: String, uid: String) {
        val rows = fetchTable(url, key, token, "rockscout_saved_images", uid)
        if (rows.isEmpty()) return
        val images = rows.mapNotNull { row ->
            try {
                val obj = row.jsonObject
                SavedImage(
                    id = obj["id"]?.jsonPrimitive?.content ?: return@mapNotNull null,
                    url = obj["image_url"]?.jsonPrimitive?.content ?: return@mapNotNull null,
                    savedAt = obj["created_at"]?.jsonPrimitive?.content?.let { parseIsoToMillis(it) } ?: 0L,
                )
            } catch (e: Exception) { null }
        }
        if (images.isNotEmpty()) {
            AppRepository.instance.loadSavedImages(images)
            PersistenceManager.saveSavedImages(images)
            Log.d(TAG, "Pulled ${images.size} saved images")
        }
    }

    private suspend fun pullJournalEntries(url: String, key: String, token: String, uid: String) {
        val rows = fetchTable(url, key, token, "rockscout_field_journal", uid)
        if (rows.isEmpty()) return
        val entries = rows.mapNotNull { row ->
            try {
                val obj = row.jsonObject
                JournalEntry(
                    id = obj["id"]?.jsonPrimitive?.content ?: return@mapNotNull null,
                    date = obj["entry_date"]?.jsonPrimitive?.content?.let { parseDateToMillis(it) } ?: 0L,
                    location = obj["location"]?.jsonPrimitive?.content ?: "",
                    digSiteId = obj["dig_site_id"]?.jsonPrimitive?.content,
                    weatherSummary = obj["weather_summary"]?.jsonPrimitive?.content ?: "",
                    notes = obj["notes"]?.jsonPrimitive?.content ?: "",
                    photoUris = obj["photo_urls"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList(),
                    createdAt = obj["created_at"]?.jsonPrimitive?.content?.let { parseIsoToMillis(it) } ?: 0L,
                )
            } catch (e: Exception) { null }
        }
        if (entries.isNotEmpty()) {
            AppRepository.instance.loadJournalEntries(entries)
            PersistenceManager.saveJournalEntries(entries)
            Log.d(TAG, "Pulled ${entries.size} journal entries")
        }
    }

    private suspend fun pullTrips(url: String, key: String, token: String, uid: String) {
        val rows = fetchTable(url, key, token, "rockscout_trips", uid)
        if (rows.isEmpty()) return
        val trips = rows.mapNotNull { row ->
            try {
                val obj = row.jsonObject
                Trip(
                    id = obj["id"]?.jsonPrimitive?.content ?: return@mapNotNull null,
                    name = obj["name"]?.jsonPrimitive?.content ?: "Trip",
                    date = obj["trip_date"]?.jsonPrimitive?.content?.let { parseDateToMillis(it) } ?: 0L,
                    stops = obj["stops"]?.jsonArray?.mapNotNull { stopEl ->
                        try {
                            val s = stopEl.jsonObject
                            TripStop(
                                locationId = s["locationId"]?.jsonPrimitive?.content ?: "",
                                locationName = s["locationName"]?.jsonPrimitive?.content ?: "",
                                order = s["order"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0,
                                latitude = s["latitude"]?.jsonPrimitive?.content?.toDoubleOrNull(),
                                longitude = s["longitude"]?.jsonPrimitive?.content?.toDoubleOrNull(),
                                isCustomPin = s["isCustomPin"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false,
                                stopType = s["stopType"]?.jsonPrimitive?.content ?: "dig_site",
                            )
                        } catch (e: Exception) { null }
                    } ?: emptyList(),
                    targetSpecimens = obj["target_specimens"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList(),
                    gearChecklist = obj["gear_checklist"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList(),
                    notes = obj["notes"]?.jsonPrimitive?.content ?: "",
                    isArchived = obj["is_archived"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false,
                    completedAt = obj["completed_at"]?.jsonPrimitive?.content?.let { parseIsoToMillis(it) },
                    createdAt = obj["created_at"]?.jsonPrimitive?.content?.let { parseIsoToMillis(it) } ?: 0L,
                )
            } catch (e: Exception) { null }
        }
        if (trips.isNotEmpty()) {
            AppRepository.instance.loadTrips(trips)
            PersistenceManager.saveTrips(trips)
            Log.d(TAG, "Pulled ${trips.size} trips")
        }
    }

    private suspend fun pullTradeListings(url: String, key: String, token: String, uid: String) {
        val rows = fetchTable(url, key, token, "rockscout_trade_listings", uid)
        if (rows.isEmpty()) return
        val listings = rows.mapNotNull { row ->
            try {
                val obj = row.jsonObject
                TradeListing(
                    id = obj["listing_id"]?.jsonPrimitive?.content ?: return@mapNotNull null,
                    type = ListingType.valueOf(obj["type"]?.jsonPrimitive?.content ?: "HAVE"),
                    listingMode = ListingMode.valueOf(obj["listing_mode"]?.jsonPrimitive?.content ?: "SWAP"),
                    price = obj["price"]?.jsonPrimitive?.content ?: "",
                    specimenName = obj["specimen_name"]?.jsonPrimitive?.content ?: "",
                    condition = obj["condition"]?.jsonPrimitive?.content ?: "",
                    description = obj["description"]?.jsonPrimitive?.content ?: "",
                    wantInReturn = obj["want_in_return"]?.jsonPrimitive?.content ?: "",
                    photoUri = obj["photo_uri"]?.jsonPrimitive?.content,
                    tags = obj["tags"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList(),
                    status = obj["status"]?.jsonPrimitive?.content ?: "active",
                    ownerUsername = obj["owner_username"]?.jsonPrimitive?.content,
                    createdAt = obj["created_at"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0L,
                    expiresAt = obj["expires_at"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0L,
                )
            } catch (e: Exception) { null }
        }
        if (listings.isNotEmpty()) {
            AppRepository.instance.loadTradeListings(listings)
            PersistenceManager.saveTradeListings(listings)
            Log.d(TAG, "Pulled ${listings.size} trade listings")
        }
    }

    private suspend fun pullAuroraSavedSpots(url: String, key: String, token: String, uid: String) {
        val rows = fetchTable(url, key, token, "rockscout_aurora_saved_spots", uid)
        if (rows.isEmpty()) return
        val spots = rows.mapNotNull { row ->
            try {
                val obj = row.jsonObject
                AuroraSavedSpot(
                    id = obj["spot_id"]?.jsonPrimitive?.content ?: return@mapNotNull null,
                    name = obj["name"]?.jsonPrimitive?.content ?: "",
                    latitude = obj["latitude"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
                    longitude = obj["longitude"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
                    createdAt = obj["created_at"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0L,
                )
            } catch (e: Exception) { null }
        }
        if (spots.isNotEmpty()) {
            AppRepository.instance.loadAuroraSavedSpots(spots)
            PersistenceManager.saveAuroraSavedSpots(spots)
            Log.d(TAG, "Pulled ${spots.size} aurora spots")
        }
    }

    private suspend fun pullProfile(url: String, key: String, token: String, uid: String) {
        val response = client.get("$url/rest/v1/rockscout_profiles?id=eq.$uid&select=display_name,avatar_emoji,status,club_enabled,scan_radius_miles") {
            header("apikey", key)
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        if (!response.status.isSuccess()) return
        val body = response.body<String>()
        val rows = json.parseToJsonElement(body).jsonArray
        if (rows.isEmpty()) return
        val obj = rows[0].jsonObject
        val displayName = obj["display_name"]?.jsonPrimitive?.content ?: ""
        val avatarEmoji = obj["avatar_emoji"]?.jsonPrimitive?.content ?: "\uD83E\uDD20"
        // Update the local profile with the Supabase display name + avatar if they differ.
        val current = AppRepository.instance.profile.value
        if (displayName.isNotBlank() && displayName != current.name) {
            AppRepository.instance.updateProfile { it.copy(name = displayName, avatarEmoji = avatarEmoji) }
            Log.d(TAG, "Pulled profile: name=$displayName")
        }
    }

    // ─── Push: upload local data to Supabase ─────────────────────────────

    /**
     * Push all local data to Supabase. Called after pullAll on first sign-in
     * (to upload pre-existing local data) and periodically by the sync worker.
     */
    suspend fun pushAll(): Result<Unit> = syncMutex.withLock {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        val uid = userId() ?: return Result.failure(Exception("No user ID"))
        val url = baseUrl()
        val key = anonKey()

        return try {
            Log.i(TAG, "Starting full push for user=$uid")

            pushCollection(url, key, token, uid)
            pushWishlist(url, key, token, uid)
            pushLikedSpecimens(url, key, token, uid)
            pushFavoriteSpots(url, key, token, uid)
            pushProfile(url, key, token, uid)

            Log.i(TAG, "Full push complete for user=$uid")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Push failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun pushCollection(url: String, key: String, token: String, uid: String) {
        val entries = AppRepository.instance.collection.value
        // Upsert each entry (merge-duplicates to handle re-syncs).
        for (entry in entries) {
            try {
                client.post("$url/rest/v1/rockscout_collection") {
                    header("apikey", key)
                    header(HttpHeaders.Authorization, "Bearer $token")
                    contentType(ContentType.Application.Json)
                    header("Prefer", "resolution=merge-duplicates,return=minimal")
                    setBody(makeJson(
                        "user_id" to uid,
                        "specimen_id" to entry.specimenId,
                        "note" to entry.note,
                        "found_at" to entry.foundAt,
                        "added_at" to entry.addedAt.toString(),
                    ))
                }
            } catch (e: Exception) {
                Log.w(TAG, "Push collection entry failed: ${e.message}")
            }
        }
        // Delete entries that are no longer in the local collection.
        val remoteRows = fetchTable(url, key, token, "rockscout_collection", uid)
        val localIds = entries.map { it.specimenId }.toSet()
        for (row in remoteRows) {
            val remoteId = row.jsonObject["specimen_id"]?.jsonPrimitive?.content ?: continue
            if (remoteId !in localIds) {
                try {
                    client.delete("$url/rest/v1/rockscout_collection?user_id=eq.$uid&specimen_id=eq.$remoteId") {
                        header("apikey", key)
                        header(HttpHeaders.Authorization, "Bearer $token")
                    }
                } catch (e: Exception) { /* best-effort */ }
            }
        }
    }

    private suspend fun pushWishlist(url: String, key: String, token: String, uid: String) {
        val ids = AppRepository.instance.wishlist.value
        // Upsert all.
        for (id in ids) {
            try {
                client.post("$url/rest/v1/rockscout_wishlist") {
                    header("apikey", key)
                    header(HttpHeaders.Authorization, "Bearer $token")
                    contentType(ContentType.Application.Json)
                    header("Prefer", "resolution=merge-duplicates,return=minimal")
                    setBody(makeJson(
                        "user_id" to uid,
                        "specimen_id" to id,
                    ))
                }
            } catch (e: Exception) { /* best-effort */ }
        }
        // Delete removed entries.
        val remoteRows = fetchTable(url, key, token, "rockscout_wishlist", uid)
        for (row in remoteRows) {
            val remoteId = row.jsonObject["specimen_id"]?.jsonPrimitive?.content ?: continue
            if (remoteId !in ids) {
                try {
                    client.delete("$url/rest/v1/rockscout_wishlist?user_id=eq.$uid&specimen_id=eq.$remoteId") {
                        header("apikey", key)
                        header(HttpHeaders.Authorization, "Bearer $token")
                    }
                } catch (e: Exception) { /* best-effort */ }
            }
        }
    }

    private suspend fun pushLikedSpecimens(url: String, key: String, token: String, uid: String) {
        val ids = AppRepository.instance.likedSpecimens.value
        for (id in ids) {
            try {
                client.post("$url/rest/v1/rockscout_liked_specimens") {
                    header("apikey", key)
                    header(HttpHeaders.Authorization, "Bearer $token")
                    contentType(ContentType.Application.Json)
                    header("Prefer", "resolution=merge-duplicates,return=minimal")
                    setBody(makeJson(
                        "user_id" to uid,
                        "specimen_id" to id,
                    ))
                }
            } catch (e: Exception) { /* best-effort */ }
        }
        val remoteRows = fetchTable(url, key, token, "rockscout_liked_specimens", uid)
        for (row in remoteRows) {
            val remoteId = row.jsonObject["specimen_id"]?.jsonPrimitive?.content ?: continue
            if (remoteId !in ids) {
                try {
                    client.delete("$url/rest/v1/rockscout_liked_specimens?user_id=eq.$uid&specimen_id=eq.$remoteId") {
                        header("apikey", key)
                        header(HttpHeaders.Authorization, "Bearer $token")
                    }
                } catch (e: Exception) { /* best-effort */ }
            }
        }
    }

    private suspend fun pushFavoriteSpots(url: String, key: String, token: String, uid: String) {
        val ids = AppRepository.instance.favoriteSpots.value
        for (id in ids) {
            try {
                client.post("$url/rest/v1/rockscout_favorite_spots") {
                    header("apikey", key)
                    header(HttpHeaders.Authorization, "Bearer $token")
                    contentType(ContentType.Application.Json)
                    header("Prefer", "resolution=merge-duplicates,return=minimal")
                    setBody(makeJson(
                        "user_id" to uid,
                        "spot_id" to id,
                        "spot_type" to "dig_site",
                        "name" to id,
                    ))
                }
            } catch (e: Exception) { /* best-effort */ }
        }
        val remoteRows = fetchTable(url, key, token, "rockscout_favorite_spots", uid)
        for (row in remoteRows) {
            val remoteId = row.jsonObject["spot_id"]?.jsonPrimitive?.content ?: continue
            if (remoteId !in ids) {
                try {
                    client.delete("$url/rest/v1/rockscout_favorite_spots?user_id=eq.$uid&spot_id=eq.$remoteId") {
                        header("apikey", key)
                        header(HttpHeaders.Authorization, "Bearer $token")
                    }
                } catch (e: Exception) { /* best-effort */ }
            }
        }
    }

    private suspend fun pushProfile(url: String, key: String, token: String, uid: String) {
        val profile = AppRepository.instance.profile.value
        try {
            client.post("$url/rest/v1/rockscout_profiles") {
                header("apikey", key)
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                header("Prefer", "resolution=merge-duplicates,return=minimal")
                setBody(makeJson(
                    "id" to uid,
                    "display_name" to profile.name,
                    "avatar_emoji" to profile.avatarEmoji,
                    "status" to profile.hunterStatus.name.lowercase(),
                    "club_enabled" to profile.clubEnabled.toString(),
                    "scan_radius_miles" to profile.scanRadiusMiles.toString(),
                ))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Push profile failed: ${e.message}")
        }
    }

    // ─── Sync: pull then push ────────────────────────────────────────────

    /**
     * Full sync: pull remote data, then push local data.
     * Called on sign-in and periodically by the sync worker.
     */
    fun syncInBackground() {
        if (!isEnabled) return
        scope.launch {
            pullAll().onSuccess {
                pushAll()
            }
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────

    /** Build a JSON object string from key-value pairs. */
    private fun makeJson(vararg pairs: Pair<String, String>): String {
        val map = pairs.associate { it.first to JsonPrimitive(it.second) }
        return json.encodeToString(JsonObject.serializer(), JsonObject(map))
    }

    /** Parse an ISO 8601 timestamp (e.g. "2024-01-15T12:30:00Z") to epoch millis. */
    private fun parseIsoToMillis(iso: String): Long {
        return try {
            java.time.OffsetDateTime.parse(iso).toInstant().toEpochMilli()
        } catch (e: Exception) {
            try {
                java.time.LocalDateTime.parse(iso).toInstant(java.time.ZoneOffset.UTC).toEpochMilli()
            } catch (e2: Exception) { 0L }
        }
    }

    /** Parse a date string (e.g. "2024-01-15") to epoch millis. */
    private fun parseDateToMillis(date: String): Long {
        return try {
            java.time.LocalDate.parse(date).atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli()
        } catch (e: Exception) { 0L }
    }
}

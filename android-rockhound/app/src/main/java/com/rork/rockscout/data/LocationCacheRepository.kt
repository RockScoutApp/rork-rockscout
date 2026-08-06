package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import com.rork.rockscout.data.db.DigLocationEntity
import com.rork.rockscout.data.db.MuseumEntity
import com.rork.rockscout.data.db.RockScoutDatabase
import com.rork.rockscout.data.db.toEntity
import com.rork.rockscout.data.db.toDigLocation
import com.rork.rockscout.data.db.toMuseumEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Repository that provides Room-backed access to dig locations and museums.
 *
 * On first launch (or when the database is empty), seeds all data from the
 * hardcoded [SeedData] and [UsMuseums] singletons into the Room database.
 * Subsequent reads come from SQLite, which is instant and works fully offline.
 *
 * The Room layer provides:
 * - Persistent SQLite storage instead of in-memory singletons
 * - Efficient query support (search, filter by type/region)
 * - A single source of truth that can be updated without an app release
 * - Foundation for future remote data synchronisation
 *
 * User-submitted dig locations ([CustomDigLocationStore]) and user-added
 * museums ([UserMuseumStore]) continue to be managed by their respective
 * stores. This repository seeds their current state into Room at launch so
 * they appear in queries. Screens merge any newly added entries at the UI
 * layer for immediate visibility.
 */
object LocationCacheRepository {

    private const val TAG = "LocationCacheRepo"
    private const val SEED_VERSION_KEY = "room_seed_version"

    private lateinit var database: RockScoutDatabase

    private val _isSeeded = MutableStateFlow(false)
    val isSeeded: StateFlow<Boolean> = _isSeeded.asStateFlow()

    // ── Flows (reactive, always up-to-date) ──────────────────────────────────

    /** All dig locations (US + international), sorted alphabetically by name. */
    val allDigLocations: Flow<List<DigLocationEntity>>
        get() = database.digLocationDao().getAll()

    /** US-only dig locations, sorted alphabetically. */
    val usDigLocations: Flow<List<DigLocationEntity>>
        get() = database.digLocationDao().getUsLocations()

    /** International-only dig locations, sorted alphabetically. */
    val internationalDigLocations: Flow<List<DigLocationEntity>>
        get() = database.digLocationDao().getInternationalLocations()

    /** All curated (non-user-added) museums, sorted by state then name. */
    val curatedMuseums: Flow<List<MuseumEntity>>
        get() = database.museumDao().getCuratedMuseums()

    // ── Initialization ─────────────────────────────────────────────────────────

    /**
     * Must be called from [com.rork.rockscout.RockScoutApplication] during
     * startup, before any screen accesses the flows.
     */
    fun initialize(context: Context) {
        if (::database.isInitialized) return
        database = RockScoutDatabase.getInstance(context)
    }

    /**
     * Seeds the database from hardcoded data if it is empty or if the app
     * version has changed since the last seed (so new entries in app updates
     * appear without a full app reinstall).
     *
     * Safe to call on every launch — runs on [Dispatchers.IO] and only
     * does work when seeding is actually needed.
     */
    suspend fun seedIfNeeded(currentVersionCode: Int) {
        if (!::database.isInitialized) {
            Log.w(TAG, "seedIfNeeded called before initialize — skipping")
            return
        }
        withContext(Dispatchers.IO) {
            runCatching {
                val lastSeedVersion = LocalDataStore.getString(SEED_VERSION_KEY)?.toIntOrNull() ?: 0
                val locationCount = database.digLocationDao().count()
                val museumCount = database.museumDao().curatedCount()
                val needsSeed = lastSeedVersion != currentVersionCode ||
                    locationCount == 0 ||
                    museumCount == 0

                if (needsSeed) {
                    Log.i(TAG, "Seeding Room database (version=$currentVersionCode, lastSeed=$lastSeedVersion, locs=$locationCount, museums=$museumCount)")
                    seedDigLocations()
                    seedMuseums()
                    LocalDataStore.setString(SEED_VERSION_KEY, currentVersionCode.toString())
                } else {
                    Log.d(TAG, "Room database already seeded ($locationCount locations, $museumCount museums)")
                }
                _isSeeded.value = true
            }.onFailure { e ->
                Log.e(TAG, "Failed to seed Room database", e)
                _isSeeded.value = true
            }
        }
    }

    // ── One-shot queries ───────────────────────────────────────────────────────

    suspend fun digLocationById(id: String): DigLocationEntity? =
        withContext(Dispatchers.IO) { database.digLocationDao().getById(id) }

    fun searchDigLocations(query: String): Flow<List<DigLocationEntity>> =
        database.digLocationDao().search(query)

    // ── Seeding internals ──────────────────────────────────────────────────────

    private suspend fun seedDigLocations() {
        // SeedData.allLocations includes CustomDigLocationStore entries.
        val usLocations = SeedData.allLocations.map { it.toEntity(isInternational = false) }
        val intlLocations = InternationalLocations.internationalLocations
            .map { it.toEntity(isInternational = true) }

        // Clear and re-insert so app updates with new/changed entries are picked up.
        database.digLocationDao().deleteAll()
        database.digLocationDao().insertAll(usLocations + intlLocations)
        Log.i(TAG, "Seeded ${usLocations.size} US + ${intlLocations.size} international locations")
    }

    private suspend fun seedMuseums() {
        val museums = UsMuseums.allMuseums.map { it.toEntity() }
        database.museumDao().deleteCurated()
        database.museumDao().insertAll(museums)
        Log.i(TAG, "Seeded ${museums.size} curated museums")
    }
}

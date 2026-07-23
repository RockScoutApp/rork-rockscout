package com.rork.rockscout.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Persistently stores user-submitted [GemShow] entries that were approved
 * (either auto-approved via web verification or manually approved in the
 * Developer Console).
 *
 * Approved shows are merged into [GemShowData.allShows] so they appear
 * permanently in the Gem Shows screen, search, and proximity notifications.
 *
 * Follows the [CustomDigLocationStore] pattern.
 */
object CustomGemShowStore {

    private const val PREFS_KEY = "custom_gem_shows"

    private val _shows = MutableStateFlow<List<GemShow>>(emptyList())
    val shows: StateFlow<List<GemShow>> = _shows.asStateFlow()

    private var initialized = false

    fun initialize() {
        if (initialized) return
        initialized = true
        loadFromDisk()
    }

    fun addAll(newShows: List<GemShow>) {
        val existingIds = _shows.value.map { it.id }.toSet()
        val unique = newShows.filter { it.id !in existingIds }
        if (unique.isEmpty()) return
        _shows.value = (_shows.value + unique)
        persist()
    }

    fun remove(id: String) {
        _shows.value = _shows.value.filterNot { it.id == id }
        persist()
    }

    fun clear() {
        _shows.value = emptyList()
        persist()
    }

    private fun loadFromDisk() {
        runCatching {
            val raw = LocalDataStore.getString(PREFS_KEY) ?: return
            val list = LocalDataStore.json.decodeFromString<List<GemShow>>(raw)
            _shows.value = list
        }
    }

    private fun persist() {
        runCatching {
            LocalDataStore.setString(
                PREFS_KEY,
                LocalDataStore.json.encodeToString(_shows.value),
            )
        }
    }

    /**
     * Convert an approved [GemShowSubmissionStore.GemShowSubmission] into a
     * permanent [GemShow] so it appears in the Gem Shows screen.
     */
    fun fromSubmission(sub: GemShowSubmissionStore.GemShowSubmission): GemShow {
        val monthIndexMap = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December",
        ).mapIndexed { i, name -> name to (i + 1) }.toMap()

        return GemShow(
            id = "user-show-${sub.id}",
            name = sub.name,
            city = sub.city,
            state = sub.state,
            venue = "See website for details",
            monthLabel = sub.month.ifBlank { "Various" },
            dateRange = sub.dateRange.ifBlank { "See website" },
            description = sub.description.ifBlank {
                if (sub.webVerified) "Web-verified show submitted by ${sub.submitterName}."
                else "User-submitted show. Verify details on the website before traveling."
            },
            entryFee = "See website",
            isAnnual = false,
            website = sub.website.ifBlank { sub.webUrl },
            monthIndex = monthIndexMap[sub.month] ?: 0,
        )
    }

    /**
     * Add an auto-verified or admin-approved show submission as a permanent
     * [GemShow] so it appears in the Gem Shows screen.
     */
    fun addApprovedShow(sub: GemShowSubmissionStore.GemShowSubmission) {
        val show = fromSubmission(sub)
        addAll(listOf(show))
    }
}

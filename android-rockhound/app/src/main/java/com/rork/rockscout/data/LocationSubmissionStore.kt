package com.rork.rockscout.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

/**
 * Persistently stores full location submissions from users — dig sites, mines,
 * quarries, rock shops, and other collecting locations submitted via the
 * "Upload New Location" form with photos, type, address, and comments.
 *
 * Submissions are either auto-approved (when web verification confirms the
 * location is a known dig site) or sent to the Developer Console for manual
 * review. Approved submissions are converted to permanent [DigLocation] entries
 * via [CustomDigLocationStore].
 */
object LocationSubmissionStore {

    private const val PREFS_KEY = "location_submissions"

    @Serializable
    data class LocationSubmission(
        val id: String,
        val name: String,
        val type: String,
        val address: String,
        val comments: String,
        val latitude: Double,
        val longitude: Double,
        val photoUris: List<String>,
        val submitterName: String,
        val submitterId: String?,
        val submittedAt: Long,
        val status: String = "pending",
        val webVerified: Boolean = false,
        val webSnippet: String = "",
        val webUrl: String = "",
        /** Category so dev tools can distinguish dig site, campground, and trailhead submissions. */
        val locationCategory: String = "dig_site",
    )

    private val _submissions = MutableStateFlow<List<LocationSubmission>>(emptyList())
    val submissions: StateFlow<List<LocationSubmission>> = _submissions.asStateFlow()

    private var initialized = false

    fun initialize() {
        if (initialized) return
        initialized = true
        loadFromDisk()
    }

    val pendingSubmissions: List<LocationSubmission>
        get() = _submissions.value.filter { it.status == "pending" }

    fun add(submission: LocationSubmission) {
        val existingIds = _submissions.value.map { it.id }.toSet()
        if (submission.id in existingIds) return
        _submissions.value = (_submissions.value + submission)
        persist()
    }

    fun approve(id: String) {
        _submissions.value = _submissions.value.map {
            if (it.id == id) it.copy(status = "approved") else it
        }
        persist()
    }

    fun deny(id: String) {
        _submissions.value = _submissions.value.map {
            if (it.id == id) it.copy(status = "denied") else it
        }
        persist()
    }

    fun remove(id: String) {
        _submissions.value = _submissions.value.filterNot { it.id == id }
        persist()
    }

    fun clear() {
        _submissions.value = emptyList()
        persist()
    }

    private fun loadFromDisk() {
        runCatching {
            val raw = LocalDataStore.getString(PREFS_KEY) ?: return
            val list = LocalDataStore.json.decodeFromString<List<LocationSubmission>>(raw)
            _submissions.value = list
        }
    }

    private fun persist() {
        runCatching {
            LocalDataStore.setString(
                PREFS_KEY,
                LocalDataStore.json.encodeToString(_submissions.value),
            )
        }
    }
}

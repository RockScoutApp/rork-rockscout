package com.rork.rockscout.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import com.rork.rockscout.data.ProfanityFilter

/**
 * Persistently stores custom pin submissions from users that may be rock-related
 * dig sites not yet in the official [SeedData] database.
 *
 * When a user drops a custom trip pin, the app runs a background web search
 * using [DigSiteSearchService]. If the search confirms the pin is rock-related,
 * the pin is auto-submitted here for admin review in the Developer Console.
 * Users can also manually flag a pin as a rock location via a toggle.
 *
 * Approving a submission converts it to a permanent [DigLocation] via
 * [CustomDigLocationStore].
 */
object UserPinSubmissionStore {

    private const val PREFS_KEY = "user_pin_submissions"

    @Serializable
    data class PinSubmission(
        val id: String,
        val name: String,
        val latitude: Double,
        val longitude: Double,
        val submitterName: String,
        val submitterId: String?,
        val submittedAt: Long,
        /** Web search snippet confirming rock-related status, or empty if manually flagged. */
        val webSnippet: String,
        /** URL of the web source, if any. */
        val webUrl: String,
        /** Whether the user manually flagged this as a rock location. */
        val manuallyFlagged: Boolean,
        /** Approval status: "pending", "approved", "denied". */
        val status: String = "pending",
    )

    private val _submissions = MutableStateFlow<List<PinSubmission>>(emptyList())
    val submissions: StateFlow<List<PinSubmission>> = _submissions.asStateFlow()

    private var initialized = false

    fun initialize() {
        if (initialized) return
        initialized = true
        loadFromDisk()
    }

    val pendingSubmissions: List<PinSubmission>
        get() = _submissions.value.filter { it.status == "pending" }

    fun add(submission: PinSubmission) {
        val existingIds = _submissions.value.map { it.id }.toSet()
        if (submission.id in existingIds) return
        val filtered = submission.copy(name = ProfanityFilter.filter(submission.name))
        _submissions.value = (_submissions.value + filtered)
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
            val list = LocalDataStore.json.decodeFromString<List<PinSubmission>>(raw)
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

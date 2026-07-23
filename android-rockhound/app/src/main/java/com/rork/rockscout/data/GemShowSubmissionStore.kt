package com.rork.rockscout.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

/**
 * Persistently stores user-submitted gem & mineral show entries.
 *
 * Submissions are either auto-approved (when web verification confirms the
 * show is a known gem/mineral event) or sent to the Developer Console for
 * manual review. Approved submissions are converted to permanent [GemShow]
 * entries via [CustomGemShowStore].
 *
 * Follows the [LocationSubmissionStore] pattern exactly.
 */
object GemShowSubmissionStore {

    private const val PREFS_KEY = "gem_show_submissions"

    @Serializable
    data class GemShowSubmission(
        val id: String,
        val name: String,
        val city: String,
        val state: String,
        val month: String,
        val website: String,
        val dateRange: String = "",
        val description: String = "",
        val submitterName: String,
        val submitterId: String?,
        val submittedAt: Long,
        val status: String = "pending",
        val webVerified: Boolean = false,
        val webSnippet: String = "",
        val webUrl: String = "",
    )

    private val _submissions = MutableStateFlow<List<GemShowSubmission>>(emptyList())
    val submissions: StateFlow<List<GemShowSubmission>> = _submissions.asStateFlow()

    private var initialized = false

    fun initialize() {
        if (initialized) return
        initialized = true
        loadFromDisk()
    }

    val pendingSubmissions: List<GemShowSubmission>
        get() = _submissions.value.filter { it.status == "pending" }

    fun add(submission: GemShowSubmission) {
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
            val list = LocalDataStore.json.decodeFromString<List<GemShowSubmission>>(raw)
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

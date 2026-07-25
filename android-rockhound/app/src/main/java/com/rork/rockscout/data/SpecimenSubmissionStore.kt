package com.rork.rockscout.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

/**
 * Persistently stores user-submitted specimens awaiting developer review.
 *
 * Users can submit specimens from the Specimen Database or Rocks Are Amazing
 * screens. Each submission includes up to 4 images, info text, and a location.
 * The developer reviews submissions in the Developer Console and can approve
 * them to the specimen database or RAA, or deny them.
 */
object SpecimenSubmissionStore {

    private const val PREFS_KEY = "specimen_submissions"

    @Serializable
    data class SpecimenSubmission(
        val id: String,
        val submitterName: String,
        val submitterId: String?,
        val submitterAvatar: String,
        val imageUris: List<String>,
        /** User-provided specimen name (used for duplicate detection and review). */
        val name: String = "",
        val infoText: String,
        /** User-provided date the specimen was found, e.g. "Jul 25, 2026". */
        val dateFound: String = "",
        val location: String,
        val submittedAt: Long,
        val status: String = "pending",
        /** Set by dev on approval: "database" or "raa". */
        val targetDatabase: String? = null,
    )

    private val _submissions = MutableStateFlow<List<SpecimenSubmission>>(emptyList())
    val submissions: StateFlow<List<SpecimenSubmission>> = _submissions.asStateFlow()

    private var initialized = false

    fun initialize() {
        if (initialized) return
        initialized = true
        loadFromDisk()
    }

    val pendingSubmissions: List<SpecimenSubmission>
        get() = _submissions.value.filter { it.status == "pending" }

    fun add(submission: SpecimenSubmission): Result<Unit> = runCatching {
        val existingIds = _submissions.value.map { it.id }.toSet()
        if (submission.id in existingIds) return@runCatching
        _submissions.value = (_submissions.value + submission)
        persist()
    }.onFailure {
        android.util.Log.w("SpecimenSubmissionStore", "add failed", it)
    }

    fun approve(id: String, targetDatabase: String) {
        _submissions.value = _submissions.value.map {
            if (it.id == id) it.copy(status = "approved", targetDatabase = targetDatabase) else it
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

    fun updateInfoText(id: String, newInfoText: String) {
        _submissions.value = _submissions.value.map {
            if (it.id == id) it.copy(infoText = newInfoText) else it
        }
        persist()
    }

    fun clear() {
        _submissions.value = emptyList()
        persist()
    }

    private fun loadFromDisk() {
        runCatching {
            val raw = LocalDataStore.getString(PREFS_KEY) ?: return
            val list = LocalDataStore.json.decodeFromString<List<SpecimenSubmission>>(raw)
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

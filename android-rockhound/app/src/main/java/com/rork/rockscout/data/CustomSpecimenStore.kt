package com.rork.rockscout.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Persistently stores user-submitted specimens that were approved by the
 * developer in the Developer Console.
 *
 * Approved specimens are merged into [SeedData.allSpecimens] so they appear
 * permanently in the Specimen Database and search results. Specimens approved
 * to RAA are also merged into [RocksAreAmazingSpecimens].
 */
object CustomSpecimenStore {

    private const val PREFS_KEY = "custom_specimens"
    private const val PREFS_KEY_RAA = "custom_raa_specimens"

    private val _specimens = MutableStateFlow<List<Specimen>>(emptyList())
    val specimens: StateFlow<List<Specimen>> = _specimens.asStateFlow()

    private val _raaSpecimens = MutableStateFlow<List<Specimen>>(emptyList())
    val raaSpecimens: StateFlow<List<Specimen>> = _raaSpecimens.asStateFlow()

    private var initialized = false

    fun initialize() {
        if (initialized) return
        initialized = true
        loadFromDisk()
    }

    fun add(specimen: Specimen) {
        val existingIds = _specimens.value.map { it.id }.toSet()
        if (specimen.id in existingIds) return
        _specimens.value = (_specimens.value + specimen)
        persist()
    }

    fun addToRaa(specimen: Specimen) {
        val existingIds = _raaSpecimens.value.map { it.id }.toSet()
        if (specimen.id in existingIds) return
        _raaSpecimens.value = (_raaSpecimens.value + specimen)
        // Also add to the main database
        add(specimen)
        persistRaa()
    }

    fun remove(id: String) {
        _specimens.value = _specimens.value.filterNot { it.id == id }
        _raaSpecimens.value = _raaSpecimens.value.filterNot { it.id == id }
        persist()
        persistRaa()
    }

    fun clear() {
        _specimens.value = emptyList()
        _raaSpecimens.value = emptyList()
        persist()
        persistRaa()
    }

    private fun loadFromDisk() {
        runCatching {
            val raw = LocalDataStore.getString(PREFS_KEY)
            if (raw != null) {
                _specimens.value = LocalDataStore.json.decodeFromString<List<Specimen>>(raw)
            }
            val raaRaw = LocalDataStore.getString(PREFS_KEY_RAA)
            if (raaRaw != null) {
                _raaSpecimens.value = LocalDataStore.json.decodeFromString<List<Specimen>>(raaRaw)
            }
        }
    }

    private fun persist() {
        runCatching {
            LocalDataStore.setString(
                PREFS_KEY,
                LocalDataStore.json.encodeToString(_specimens.value),
            )
        }
    }

    private fun persistRaa() {
        runCatching {
            LocalDataStore.setString(
                PREFS_KEY_RAA,
                LocalDataStore.json.encodeToString(_raaSpecimens.value),
            )
        }
    }

    /**
     * Constructs a [Specimen] from a [SpecimenSubmissionStore.SpecimenSubmission]
     * that the developer has reviewed and approved.
     *
     * The info text is used to derive the name (first line or first sentence),
     * tagline (excerpt), and description (full text). The location populates
     * whereFound. All images become the specimen's imageUrls.
     */
    fun fromSubmission(
        submission: SpecimenSubmissionStore.SpecimenSubmission,
        targetDatabase: String,
    ): Specimen {
        val infoText = submission.infoText.trim()
        // Prefer the user-provided name; fall back to deriving from the description.
        val name = submission.name.trim().ifBlank {
            infoText.lineSequence().firstOrNull()?.takeIf { it.isNotBlank() }
                ?: infoText.substringBefore(".").takeIf { it.isNotBlank() }
                ?: "User Submitted Specimen"
        }
        val tagline = if (infoText.length > 80) infoText.take(80) + "…" else infoText.ifBlank { "Submitted by ${submission.submitterName}" }
        val description = buildString {
            append(infoText.ifBlank { "No description provided." })
            if (submission.dateFound.isNotBlank()) {
                append("\n\nDate found: ${submission.dateFound}")
            }
        }

        return Specimen(
            id = "custom-${submission.id}",
            name = name,
            rockClass = RockClass.MINERAL,
            category = "User Submitted",
            tagline = tagline,
            emoji = "\uD83E\uDEA8",
            colorHex = 0xFF6FA8C7,
            description = description,
            formation = "—",
            hardness = "—",
            luster = "—",
            streak = "—",
            crystalSystem = "—",
            chemicalFormula = "—",
            commonColors = emptyList(),
            whereFound = if (submission.location.isNotBlank()) listOf(submission.location) else emptyList(),
            funFacts = emptyList(),
            uses = "—",
            rarity = "Uncommon",
            imageUrls = submission.imageUris,
        )
    }
}

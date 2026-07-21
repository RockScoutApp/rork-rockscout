package com.rork.rockscout.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

/**
 * Persistently stores rock hunting locations discovered via web search
 * when the user's location filter returns fewer than 3 built-in dig sites.
 *
 * Results are viewable in the Developer Console → Bugs tab under a
 * "Discovered Locations" section so they can be manually curated into
 * [SeedData] later.
 */
object DigSiteDiscoveryStore {

    private const val PREFS_KEY = "discovered_dig_sites"

    @Serializable
    data class DiscoveredSite(
        val id: String,
        val name: String,
        val type: String,
        val region: String,
        val url: String,
        val description: String,
        val searchArea: String,
        val discoveredAt: Long,
    )

    private val _sites = MutableStateFlow<List<DiscoveredSite>>(emptyList())
    val sites: StateFlow<List<DiscoveredSite>> = _sites.asStateFlow()

    private var initialized = false

    fun initialize() {
        if (initialized) return
        initialized = true
        loadFromDisk()
    }

    fun addAll(newSites: List<DiscoveredSite>) {
        val existingIds = _sites.value.map { it.id }.toSet()
        val unique = newSites.filter { it.id !in existingIds }
        if (unique.isEmpty()) return
        _sites.value = (_sites.value + unique)
        persist()
    }

    fun clear() {
        _sites.value = emptyList()
        persist()
    }

    fun remove(id: String) {
        _sites.value = _sites.value.filterNot { it.id == id }
        persist()
    }

    private fun loadFromDisk() {
        runCatching {
            val raw = LocalDataStore.getString(PREFS_KEY) ?: return
            val list = LocalDataStore.json.decodeFromString<List<DiscoveredSite>>(raw)
            _sites.value = list
        }
    }

    private fun persist() {
        runCatching {
            LocalDataStore.setString(
                PREFS_KEY,
                LocalDataStore.json.encodeToString(_sites.value),
            )
        }
    }
}

package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Stores user-submitted museums on the Museums tab.
 *
 * Users can add their own museums via the "Add a Museum" pill button.
 * These are stored locally (SharedPreferences JSON) and appear in the
 * museum list under a "User-Added" section.
 */

@Serializable
data class UserMuseum(
    val id: String,
    val name: String,
    val city: String = "",
    val state: String = "",
    val website: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val photoPath: String? = null,
    val addedAtMs: Long = 0L,
)

@Serializable
private data class UserMuseumStoreData(
    val museums: List<UserMuseum> = emptyList(),
)

object UserMuseumStore {

    private const val DRAFT_FILE = "user_museums.json"
    private val json = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }

    private lateinit var appContext: Context

    private val _museums = MutableStateFlow<List<UserMuseum>>(emptyList())
    val museums: StateFlow<List<UserMuseum>> = _museums.asStateFlow()

    fun initialize(context: Context) {
        appContext = context.applicationContext
        load()
    }

    fun addMuseum(museum: UserMuseum) {
        if (!::appContext.isInitialized) return
        val current = _museums.value.toMutableList()
        current.add(museum)
        _museums.value = current
        persist(current)
    }

    fun deleteMuseum(id: String) {
        val current = _museums.value.filterNot { it.id == id }
        _museums.value = current
        persist(current)
    }

    private fun load() {
        if (!::appContext.isInitialized) return
        try {
            val file = File(appContext.filesDir, DRAFT_FILE)
            if (!file.exists()) return
            val data = json.decodeFromString(UserMuseumStoreData.serializer(), file.readText())
            _museums.value = data.museums
        } catch (e: Exception) {
            Log.w("UserMuseumStore", "load failed", e)
        }
    }

    private fun persist(museums: List<UserMuseum>) {
        if (!::appContext.isInitialized) return
        try {
            val file = File(appContext.filesDir, DRAFT_FILE)
            val data = UserMuseumStoreData(museums = museums)
            file.writeText(json.encodeToString(UserMuseumStoreData.serializer(), data))
        } catch (e: Exception) {
            Log.w("UserMuseumStore", "persist failed", e)
        }
    }
}

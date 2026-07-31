package com.rork.rockscout.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Randomizes which gear items get the "Top Pick" badge once every 24 hours
 * for each user. The selection is persisted locally so the same set of top
 * picks stays stable throughout the day — it only re-rolls at midnight
 * (or on the first access of a new calendar day).
 *
 * Roughly 20% of the full catalog (min 8, max 15) is marked as a top pick
 * each cycle, drawn from all available item IDs.
 */
object TopPickManager {

    private const val PREFS_KEY = "top_pick_state"
    private const val MIN_PICKS = 8
    private const val MAX_PICKS = 15
    private const val PICK_RATIO = 0.20f

    private val _topPickIds = MutableStateFlow<Set<String>>(emptySet())
    val topPickIds: StateFlow<Set<String>> = _topPickIds.asStateFlow()

    private var initialized = false

    /**
     * Initialize from persisted state. Call once from [RockScoutApplication.onCreate].
     * If the persisted date is stale (not today), re-rolls immediately.
     */
    fun initialize() {
        if (initialized) return
        initialized = true
        rollIfNeeded()
    }

    /**
     * Returns true if the given item ID is a top pick for today.
     */
    fun isTopPick(itemId: String): Boolean = _topPickIds.value.contains(itemId)

    /**
     * Checks if the persisted selection is for today; if not, re-rolls.
     * Called on app launch and safe to call at any time.
     */
    fun rollIfNeeded() {
        val today = todayString()
        val raw = LocalDataStore.getString(PREFS_KEY) ?: ""
        if (raw.isBlank()) {
            roll(today)
            return
        }
        val parts = raw.split("|", limit = 2)
        val savedDate = parts.getOrNull(0) ?: ""
        if (savedDate != today) {
            roll(today)
        } else {
            val ids = parts.getOrNull(1)?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
            _topPickIds.value = ids
        }
    }

    /**
     * Force a new random selection for the given date and persist it.
     */
    private fun roll(date: String) {
        val allIds = GearGuide.allItems.map { it.id }
        val count = (allIds.size * PICK_RATIO).toInt().coerceIn(MIN_PICKS, MAX_PICKS)
        val shuffled = allIds.shuffled()
        val picked = shuffled.take(count).toSet()
        _topPickIds.value = picked
        LocalDataStore.setString(PREFS_KEY, "$date|${picked.joinToString(",")}")
    }

    /** Force a re-roll now (useful for testing / dev console). */
    fun forceReroll() {
        roll(todayString())
    }

    private fun todayString(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        return sdf.format(java.util.Date())
    }
}

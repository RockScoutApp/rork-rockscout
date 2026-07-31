package com.rork.rockscout.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

/**
 * Tracks Amazon affiliate link click-throughs from the Gear Guide.
 *
 * Every tap on a gear item that opens an Amazon affiliate URL is logged
 * here — both as a per-item tally and as a daily time series. Stats persist
 * locally across launches and are surfaced in the Developer Console
 * Analytics tab with bar graphs.
 */
object AffiliateClickTracker {

    private const val PREFS_KEY = "affiliate_click_state"

    @Serializable
    data class ItemClick(val itemId: String, val name: String, val clicks: Int = 0)

    @Serializable
    data class DailyClick(val date: String, val clicks: Int = 0)

    @Serializable
    data class AffiliateState(
        val totalClicks: Int = 0,
        val perItem: List<ItemClick> = emptyList(),
        val perDay: List<DailyClick> = emptyList(),
    )

    private val _state = MutableStateFlow(AffiliateState())
    val state: StateFlow<AffiliateState> = _state.asStateFlow()

    private var initialized = false

    fun initialize(context: Context) {
        if (initialized) return
        initialized = true
        loadFromDisk(context)
    }

    /**
     * Record a single click on a gear item.
     * @param itemId stable gear item ID (e.g. "loupe_10x")
     * @param name display name for the dev console
     */
    fun recordClick(context: Context?, itemId: String, name: String) {
        val s = _state.value
        val today = todayString()

        val updatedItems = s.perItem.toMutableList()
        val idx = updatedItems.indexOfFirst { it.itemId == itemId }
        if (idx >= 0) {
            updatedItems[idx] = updatedItems[idx].copy(clicks = updatedItems[idx].clicks + 1)
        } else {
            updatedItems.add(ItemClick(itemId, name, 1))
        }

        val updatedDaily = s.perDay.toMutableList()
        val dayIdx = updatedDaily.indexOfFirst { it.date == today }
        if (dayIdx >= 0) {
            updatedDaily[dayIdx] = updatedDaily[dayIdx].copy(clicks = updatedDaily[dayIdx].clicks + 1)
        } else {
            updatedDaily.add(DailyClick(today, 1))
            // Keep only the last 30 days
            while (updatedDaily.size > 30) updatedDaily.removeAt(0)
        }

        _state.value = s.copy(
            totalClicks = s.totalClicks + 1,
            perItem = updatedItems.sortedByDescending { it.clicks },
            perDay = updatedDaily,
        )
        persist(context)
    }

    fun reset(context: Context) {
        _state.value = AffiliateState()
        persist(context)
    }

    /** Formatted estimated affiliate revenue (assumes ~4% commission, avg $50 order). */
    fun estimatedRevenueFormatted(): String {
        // Conservative estimate: 4% commission on $50 average order, 1 click ≈ 3% conversion
        val estRevenue = _state.value.totalClicks * 50.0 * 0.04 * 0.03
        return "$%,.2f".format(estRevenue)
    }

    // ---- Persistence -----------------------------------------------------

    private fun loadFromDisk(context: Context) {
        runCatching {
            val raw = LocalDataStore.getString(PREFS_KEY) ?: return
            _state.value = LocalDataStore.json.decodeFromString<AffiliateState>(raw)
        }
    }

    private fun persist(context: Context?) {
        context ?: return
        runCatching {
            LocalDataStore.setString(
                PREFS_KEY,
                LocalDataStore.json.encodeToString(_state.value),
            )
        }
    }

    private fun todayString(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        return sdf.format(java.util.Date())
    }
}

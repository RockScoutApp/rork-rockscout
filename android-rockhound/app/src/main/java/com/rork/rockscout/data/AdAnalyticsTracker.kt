package com.rork.rockscout.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

/**
 * Tracks real ad impressions, clicks, and estimated revenue for the
 * Developer Console analytics dashboard.
 *
 * Every banner/interstitial impression and click is logged here. Estimated
 * revenue uses a configurable eCPM (revenue per 1000 impressions). Stats
 * persist locally across launches.
 *
 * The [adsEnabled] flag and [interstitialCooldownMs] are also owned here so
 * the Developer Console can toggle them at runtime.
 */
object AdAnalyticsTracker {

    private const val PREFS_KEY = "ad_analytics_state"

    @Serializable
    data class AnalyticsState(
        val bannerImpressions: Int = 0,
        val bannerClicks: Int = 0,
        val interstitialImpressions: Int = 0,
        val interstitialClicks: Int = 0,
        val estimatedRevenueMicros: Long = 0L, // in micro-dollars ($1 = 1_000_000)
        val adsEnabled: Boolean = true,
        val interstitialCooldownMs: Long = 45_000L,
        val bannerEcpmMicros: Long = 200_000L,    // $0.20 CPM default
        val interstitialEcpmMicros: Long = 2_000_000L, // $2.00 CPM default
    )

    private val _state = MutableStateFlow(AnalyticsState())
    val state: StateFlow<AnalyticsState> = _state.asStateFlow()

    private var initialized = false

    fun initialize(context: Context) {
        if (initialized) return
        initialized = true
        loadFromDisk(context)
    }

    val adsEnabled: Boolean get() = _state.value.adsEnabled
    val interstitialCooldownMs: Long get() = _state.value.interstitialCooldownMs

    fun setAdsEnabled(context: Context, enabled: Boolean) {
        _state.value = _state.value.copy(adsEnabled = enabled)
        persist(context)
    }

    fun setInterstitialCooldown(context: Context, ms: Long) {
        _state.value = _state.value.copy(interstitialCooldownMs = ms.coerceIn(15_000L, 300_000L))
        persist(context)
    }

    fun setBannerEcpm(context: Context, micros: Long) {
        _state.value = _state.value.copy(bannerEcpmMicros = micros.coerceAtLeast(0L))
        persist(context)
    }

    fun setInterstitialEcpm(context: Context, micros: Long) {
        _state.value = _state.value.copy(interstitialEcpmMicros = micros.coerceAtLeast(0L))
        persist(context)
    }

    fun recordBannerImpression(context: Context?) {
        if (!_state.value.adsEnabled) return
        val s = _state.value
        val revenue = s.bannerEcpmMicros / 1000L
        _state.value = s.copy(
            bannerImpressions = s.bannerImpressions + 1,
            estimatedRevenueMicros = s.estimatedRevenueMicros + revenue,
        )
        persist(context)
    }

    fun recordBannerClick(context: Context?) {
        if (!_state.value.adsEnabled) return
        _state.value = _state.value.copy(bannerClicks = _state.value.bannerClicks + 1)
        persist(context)
    }

    fun recordInterstitialImpression(context: Context?) {
        if (!_state.value.adsEnabled) return
        val s = _state.value
        val revenue = s.interstitialEcpmMicros / 1000L
        _state.value = s.copy(
            interstitialImpressions = s.interstitialImpressions + 1,
            estimatedRevenueMicros = s.estimatedRevenueMicros + revenue,
        )
        persist(context)
    }

    fun recordInterstitialClick(context: Context?) {
        if (!_state.value.adsEnabled) return
        _state.value = _state.value.copy(interstitialClicks = _state.value.interstitialClicks + 1)
        persist(context)
    }

    fun reset(context: Context) {
        _state.value = AnalyticsState(
            adsEnabled = _state.value.adsEnabled,
            interstitialCooldownMs = _state.value.interstitialCooldownMs,
            bannerEcpmMicros = _state.value.bannerEcpmMicros,
            interstitialEcpmMicros = _state.value.interstitialEcpmMicros,
        )
        persist(context)
    }

    /** Formatted estimated revenue in dollars. */
    fun estimatedRevenueFormatted(): String {
        val dollars = _state.value.estimatedRevenueMicros / 1_000_000.0
        return "$%,.4f".format(dollars)
    }

    /** eCPM across all impressions combined. */
    fun combinedEcpmFormatted(): String {
        val s = _state.value
        val totalImpressions = s.bannerImpressions + s.interstitialImpressions
        if (totalImpressions == 0) return "$0.00"
        val ecpm = s.estimatedRevenueMicros.toDouble() / totalImpressions / 1000.0
        return "$%,.2f".format(ecpm)
    }

    // ---- Persistence -----------------------------------------------------

    private fun loadFromDisk(context: Context) {
        runCatching {
            val raw = LocalDataStore.getString(PREFS_KEY) ?: return
            _state.value = LocalDataStore.json.decodeFromString<AnalyticsState>(raw)
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
}

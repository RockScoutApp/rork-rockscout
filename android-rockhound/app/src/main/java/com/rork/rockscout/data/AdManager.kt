package com.rork.rockscout.data

/**
 * Manages interstitial ad display logic — per-screen cooldown, session
 * rotation index for ad variety, and dismiss tracking.
 *
 * Premium users never see ads; callers check PurchaseManager independently.
 *
 * Cooldown strategy: each screen key shows its ad at most once per session
 * until dismissed. A global minimum interval between any two interstitials
 * prevents ad fatigue.
 */
class AdManager private constructor() {

    private val dismissedScreens = mutableSetOf<String>()
    private var lastShownTimestamp: Long = 0L
    private var _rotationIndex: Int = 0
    val rotationIndex: Int get() = _rotationIndex

    companion object {
        const val GLOBAL_COOLDOWN_MS = 45_000L
        val instance: AdManager = AdManager()
    }

    /**
     * Whether the interstitial should be shown for [screenKey] right now.
     * Returns false if already dismissed this session or if the global
     * cooldown hasn't elapsed (cooldown is configurable via AdAnalyticsTracker).
     */
    fun shouldShowAd(screenKey: String): Boolean {
        if (dismissedScreens.contains(screenKey)) return false
        val cooldown = AdAnalyticsTracker.interstitialCooldownMs
        if (lastShownTimestamp != 0L &&
            System.currentTimeMillis() - lastShownTimestamp < cooldown
        ) {
            return false
        }
        return true
    }

    /**
     * Mark the ad as shown for a screen (called when the dialog appears).
     */
    fun markShown(screenKey: String) {
        lastShownTimestamp = System.currentTimeMillis()
        _rotationIndex++
    }

    /**
     * Dismiss the ad for this screen — prevents re-showing until [resetScreen].
     */
    fun dismissAd(screenKey: String) {
        dismissedScreens.add(screenKey)
    }

    /**
     * Reset cooldown for a screen so the ad can show again on next visit.
     * Call this when the user navigates to the screen (before the ad trigger).
     */
    fun resetScreen(screenKey: String) {
        dismissedScreens.remove(screenKey)
    }
}

package com.rork.rockscout.data

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

/** Type of credit consumed by an identify call, used for refund tracking. */
enum class ConsumedCredit {
    TRIAL,
    TOKEN,
    PREMIUM,
    NONE,
}

/**
 * Manages free-trial identify quotas and purchased single-use identifier tokens.
 *
 * Free trial rules (Phase 8 — shortened from the prior 2-week/5-per-week model):
 *  - 1-week trial only (first-install + 6 days).
 *  - Flat 5-token grant at first install — spend them at any pace over the week.
 *  - No weekly reset — once the 5 tokens are gone or the week elapses, the trial is over.
 *  - After the trial expires, free identifies are gone for good (rewarded video or donated tokens only).
 *
 * Tokens:
 *  - Single-use identifier tokens purchased via RevenueCat or granted by donations / rewarded video.
 *  - Each identify consumes one token when the trial is over / exhausted.
 *  - Tokens persist across app installs for the same RevenueCat user (best-effort local cache).
 *
 * Premium and Pro subscribers bypass all quotas — unlimited identifies.
 */
class IdentifyAccessManager private constructor() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _tokenBalance = MutableStateFlow(0)
    val tokenBalance: StateFlow<Int> = _tokenBalance.asStateFlow()

    private val _trialUsesRemaining = MutableStateFlow(0)
    val trialUsesRemaining: StateFlow<Int> = _trialUsesRemaining.asStateFlow()

    private val _trialActive = MutableStateFlow(false)
    val trialActive: StateFlow<Boolean> = _trialActive.asStateFlow()

    private val _trialExpired = MutableStateFlow(false)
    val trialExpired: StateFlow<Boolean> = _trialExpired.asStateFlow()

    /** True if the first-launch trial info popup should be shown. */
    private val _shouldShowTrialInfo = MutableStateFlow(false)
    val shouldShowTrialInfo: StateFlow<Boolean> = _shouldShowTrialInfo.asStateFlow()

    private val _weekNumber = MutableStateFlow(0)
    val weekNumber: StateFlow<Int> = _weekNumber.asStateFlow()

    /** Epoch millis when a donated location-monitoring unlock expires, or 0 if none active. */
    private val _locationUnlockExpiry = MutableStateFlow(0L)
    val locationUnlockExpiry: StateFlow<Long> = _locationUnlockExpiry.asStateFlow()

    /** Whether the user currently has a donated location-monitoring unlock active. */
    private val _hasLocationUnlock = MutableStateFlow(false)
    val hasLocationUnlock: StateFlow<Boolean> = _hasLocationUnlock.asStateFlow()

    /** Epoch millis when a donated ad-free unlock expires, or 0 if none active. */
    private val _adFreeUnlockExpiry = MutableStateFlow(0L)
    val adFreeUnlockExpiry: StateFlow<Long> = _adFreeUnlockExpiry.asStateFlow()

    /** Whether the user currently has a donated ad-free unlock active. */
    private val _hasAdFreeUnlock = MutableStateFlow(false)
    val hasAdFreeUnlock: StateFlow<Boolean> = _hasAdFreeUnlock.asStateFlow()

    /**
     * Mirror of the active Premium entitlement, pushed in by [PurchaseManager]
     * whenever the entitlement changes. Used so trial state can be presented as
     * "ended" for Premium subscribers without destroying the persisted trial
     * counters (a user who cancels later keeps whatever trial they had left).
     */
    private val _premiumActive = MutableStateFlow(false)

    private lateinit var prefs: SharedPreferences

    /**
     * Push the current Premium entitlement into the access manager.
     * Premium subscribers get a true premium experience: the free trial is
     * reported as inactive/ended, the first-launch trial popup never shows,
     * and ads are suppressed.
     */
    fun setPremiumActive(active: Boolean) {
        if (_premiumActive.value == active) return
        _premiumActive.value = active
        if (active) {
            _shouldShowTrialInfo.value = false
        }
        recomputeState()
    }

    /**
     * Initialize from Application context. Reads persisted trial/token state,
     * rolls over the weekly quota if a new week has started, and enforces
     * one-trial-per-device via the backend.
     */
    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // First-ever launch — check backend for prior trial claim on this device.
        val installWeekStart = prefs.getLong(KEY_INSTALL_WEEK_START, 0L)
        if (installWeekStart == 0L) {
            // Show the trial info popup on first launch.
            if (!prefs.getBoolean(KEY_TRIAL_INFO_SHOWN, false)) {
                _shouldShowTrialInfo.value = true
            }

            // Skip FORCE_PREMIUM builds — they get everything unlocked.
            if (com.rork.rockscout.BuildConfig.FORCE_PREMIUM) {
                recomputeState()
                return
            }

            // Check backend for prior trial claim on this device.
            val deviceId = getDeviceId(context)
            scope.launch {
                val trialUsed = TrialApi.checkTrial(deviceId)
                if (trialUsed) {
 // Trial already used on this device — deny.
                    prefs.edit()
                        .putLong(KEY_INSTALL_WEEK_START, 1L) // non-zero so we never re-grant
                        .putInt(KEY_WEEK_INDEX, 99)
                        .putInt(KEY_TRIAL_USES_USED, TRIAL_TOKEN_GRANT)
                        .putBoolean(KEY_TRIAL_DENIED, true)
                        .apply()
                    recomputeState()
                } else {
                    // Trial available — grant locally and claim on backend.
                    recomputeState()
                    val claimed = TrialApi.claimTrial(deviceId)
                    if (claimed) {
                        prefs.edit().putBoolean(KEY_TRIAL_CLAIM_PENDING, false).apply()
                    } else {
                        prefs.edit().putBoolean(KEY_TRIAL_CLAIM_PENDING, true).apply()
                    }
                }
            }
        } else {
            // Retry pending trial claim from a previous launch.
            if (prefs.getBoolean(KEY_TRIAL_CLAIM_PENDING, false)) {
                val deviceId = getDeviceId(context)
                scope.launch {
                    val claimed = TrialApi.claimTrial(deviceId)
                    if (claimed) {
                        prefs.edit().putBoolean(KEY_TRIAL_CLAIM_PENDING, false).apply()
                    }
                }
            }
            recomputeState()
        }
    }

    /** Mark the trial info popup as shown so it never appears again. */
    fun markTrialInfoShown() {
        prefs.edit().putBoolean(KEY_TRIAL_INFO_SHOWN, true).apply()
        _shouldShowTrialInfo.value = false
    }

    /** Get a stable device fingerprint using ANDROID_ID. */
    private fun getDeviceId(context: Context): String {
        return try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }

    /**
     * Recompute trial/expiry/remaining state from persisted prefs.
     * Called on init and after any state mutation.
     *
     * Phase 8 model: 1-week trial, flat 5-token grant, no weekly reset.
     */
    private fun recomputeState() {
        if (!::prefs.isInitialized) return
        val now = System.currentTimeMillis()
        val installWeekStart = prefs.getLong(KEY_INSTALL_WEEK_START, 0L)
        val currentWeekStart = currentWeekStartMillis(now)

        // First-ever launch — record the install week and grant the flat 5-token trial allowance.
        // (Unless the backend denied the trial — KEY_TRIAL_DENIED is set by initialize().)
        if (installWeekStart == 0L) {
            val denied = prefs.getBoolean(KEY_TRIAL_DENIED, false)
            if (!denied) {
                prefs.edit()
                    .putLong(KEY_INSTALL_WEEK_START, currentWeekStart)
                    .putInt(KEY_WEEK_INDEX, 0)
                    .putInt(KEY_TRIAL_USES_USED, 0)
                    .apply()
                _weekNumber.value = 0
                _trialUsesRemaining.value = TRIAL_TOKEN_GRANT
                _trialActive.value = true
                _trialExpired.value = false
            } else {
                _weekNumber.value = 99
                _trialUsesRemaining.value = 0
                _trialActive.value = false
                _trialExpired.value = true
            }
            _tokenBalance.value = prefs.getInt(KEY_TOKEN_BALANCE, 0)
            applyPremiumOverride()
            return
        }

        // Determine the week index relative to install (0 = the only trial week).
        val weeksSinceInstall = ((currentWeekStart - installWeekStart) / MILLIS_PER_WEEK).toInt()
        val persistedWeekIndex = prefs.getInt(KEY_WEEK_INDEX, 0)

        // If a new week has started, bump the persisted index. We do NOT reset the
        // uses-used counter on rollover — the trial grant is flat and non-renewable,
        // so once the 5 tokens are spent they stay spent.
        if (weeksSinceInstall != persistedWeekIndex) {
            prefs.edit()
                .putInt(KEY_WEEK_INDEX, weeksSinceInstall)
                .apply()
        }

        // Trial covers exactly 1 week (index 0). After that, trial is expired.
        val trialActiveNow = weeksSinceInstall == 0
        val trialExpiredNow = weeksSinceInstall > 0

        val usesUsed = prefs.getInt(KEY_TRIAL_USES_USED, 0)
        val remaining = if (trialActiveNow) (TRIAL_TOKEN_GRANT - usesUsed).coerceAtLeast(0) else 0

        _weekNumber.value = weeksSinceInstall
        _trialUsesRemaining.value = remaining
        _trialActive.value = trialActiveNow
        _trialExpired.value = trialExpiredNow
        _tokenBalance.value = prefs.getInt(KEY_TOKEN_BALANCE, 0)

        // Recompute donated location-monitoring unlock.
        val expiry = prefs.getLong(KEY_LOCATION_UNLOCK_EXPIRY, 0L)
        _locationUnlockExpiry.value = expiry
        _hasLocationUnlock.value = expiry > now

        // Recompute donated ad-free unlock.
        val adFreeExpiry = prefs.getLong(KEY_ADFREE_UNLOCK_EXPIRY, 0L)
        _adFreeUnlockExpiry.value = adFreeExpiry
        _hasAdFreeUnlock.value = adFreeExpiry > now

        applyPremiumOverride()
    }

    /**
     * For Premium subscribers, collapse all trial state so no trial banners,
     * counters, or "trial expired" upsells can ever surface. Persisted prefs are
     * left untouched so the real trial state returns if the subscription lapses.
     */
    private fun applyPremiumOverride() {
        if (!_premiumActive.value) return
        _trialActive.value = false
        _trialExpired.value = false
        _trialUsesRemaining.value = 0
        _shouldShowTrialInfo.value = false
    }

    /**
     * Whether the user can perform an identify right now, and via which mechanism.
     * Premium users always get [IdentifyAccess.UNLIMITED].
     */
    fun accessState(isPremium: Boolean): IdentifyAccess {
        if (isPremium) return IdentifyAccess.UNLIMITED
        if (_trialActive.value && _trialUsesRemaining.value > 0) {
            return IdentifyAccess.TRIAL_AVAILABLE
        }
        if (_tokenBalance.value > 0) return IdentifyAccess.TOKEN_AVAILABLE
        return IdentifyAccess.LOCKED
    }

    /**
     * Consume a single identify credit. Premium users consume nothing.
     * During the active trial, consumes a trial use first; otherwise consumes a token.
     * Returns the [ConsumedCredit] type so callers can refund on failure.
     */
    fun consumeIdentify(isPremium: Boolean): ConsumedCredit {
        if (isPremium) return ConsumedCredit.PREMIUM
        if (!::prefs.isInitialized) return ConsumedCredit.NONE

        return if (_trialActive.value && _trialUsesRemaining.value > 0) {
            val used = prefs.getInt(KEY_TRIAL_USES_USED, 0) + 1
            prefs.edit().putInt(KEY_TRIAL_USES_USED, used).apply()
            recomputeState()
            ConsumedCredit.TRIAL
        } else if (_tokenBalance.value > 0) {
            val newBalance = _tokenBalance.value - 1
            prefs.edit().putInt(KEY_TOKEN_BALANCE, newBalance).apply()
            _tokenBalance.value = newBalance
            ConsumedCredit.TOKEN
        } else {
            ConsumedCredit.NONE
        }
    }

    /**
     * Refund a previously consumed credit. Called when an identification fails
     * (timeout, backend error, no matches) so the user is not charged for a
     * failed attempt. Premium and NONE are no-ops.
     */
    fun refundIdentify(credit: ConsumedCredit) {
        if (!::prefs.isInitialized) return
        when (credit) {
            ConsumedCredit.TRIAL -> {
                val used = prefs.getInt(KEY_TRIAL_USES_USED, 0)
                if (used > 0) {
                    prefs.edit().putInt(KEY_TRIAL_USES_USED, used - 1).apply()
                    recomputeState()
                    Log.i("IdentifyAccess", "Refunded 1 trial use — uses used: ${used - 1}")
                }
            }
            ConsumedCredit.TOKEN -> {
                val newBalance = _tokenBalance.value + 1
                prefs.edit().putInt(KEY_TOKEN_BALANCE, newBalance).apply()
                _tokenBalance.value = newBalance
                Log.i("IdentifyAccess", "Refunded 1 token — new balance: $newBalance")
            }
            ConsumedCredit.PREMIUM, ConsumedCredit.NONE -> { /* no-op */ }
        }
    }

    /**
     * Add purchased tokens to the user's bank.
     */
    fun addTokens(count: Int) {
        if (count <= 0 || !::prefs.isInitialized) return
        val newBalance = _tokenBalance.value + count
        prefs.edit().putInt(KEY_TOKEN_BALANCE, newBalance).apply()
        _tokenBalance.value = newBalance
        Log.i("IdentifyAccess", "Added $count tokens — new balance: $newBalance")
    }

    /**
     * Whether location monitoring should be locked for this free user.
     * Locked once the 1-week trial expires AND no donated unlock is active.
     * Premium users are never locked.
     */
    fun isLocationLocked(isPremium: Boolean): Boolean {
        if (isPremium) return false
        if (_hasLocationUnlock.value) return false
        return _trialExpired.value
    }

    /**
     * Unified feature gate for the strict-lockout set (Phase 8).
     *
     * After the 1-week trial expires, every create/edit feature locks except
     * educational browsing (geology guides + specimen database view) and
     * field-capture share-only mode. The donated full-feature unlock
     * (the same expiry timestamp used for location monitoring) temporarily
     * re-opens the full set — field captures (full, not just sharing),
     * wishlist, my rocks, favorite spots, and RockScout Friends — for the
     * granted duration. Premium subscribers are never locked.
     *
     * This single check drives every locked surface so the rules stay
     * consistent across the app.
     */
    fun isFeatureLocked(isPremium: Boolean): Boolean {
        if (isPremium) return false
        if (_hasLocationUnlock.value) return false
        return _trialExpired.value
    }

    /**
     * Gate for social/communal features (Friends, Messenger, Trade Board,
     * Trading Floor, Community, RockScouts Map, Discover Hunters, Trip
     * Planner, My Trades, Scan/Profile sharing).
     *
     * Social features are open during the 1-week trial and during donated
     * unlocks, then lock for non-premium users. Premium is never locked.
     */
    fun isSocialLocked(isPremium: Boolean): Boolean {
        if (isPremium) return false
        if (_hasLocationUnlock.value) return false
        return _trialExpired.value
    }

    /**
     * Gate for personal collection tools (My Rocks, Wishlist, Field
     * Captures, Favorite Spots, Field Journal, Saved Images).
     *
     * Personal tools are always free — never locked regardless of trial
     * status or premium. This keeps the app useful for free users and
     * keeps it rated G by limiting social access for non-subscribers.
     */
    fun isPersonalLocked(isPremium: Boolean): Boolean {
        return false
    }

    /**
     * Grant a temporary location-monitoring unlock for the given number of days.
     * Stacks/extends from the later of now or the current expiry.
     */
    fun grantLocationUnlock(days: Int) {
        if (days <= 0 || !::prefs.isInitialized) return
        val now = System.currentTimeMillis()
        val base = maxOf(now, _locationUnlockExpiry.value)
        val newExpiry = base + days.toLong() * 24L * 60L * 60L * 1000L
        prefs.edit().putLong(KEY_LOCATION_UNLOCK_EXPIRY, newExpiry).apply()
        _locationUnlockExpiry.value = newExpiry
        _hasLocationUnlock.value = newExpiry > now
        Log.i("IdentifyAccess", "Granted $days days location unlock — expiry: $newExpiry")
    }

    /**
     * Grant a temporary ad-free unlock for the given number of days.
     * Stacks/extends from the later of now or the current expiry.
     */
    fun grantAdFreeUnlock(days: Int) {
        if (days <= 0 || !::prefs.isInitialized) return
        val now = System.currentTimeMillis()
        val base = maxOf(now, _adFreeUnlockExpiry.value)
        val newExpiry = base + days.toLong() * 24L * 60L * 60L * 1000L
        prefs.edit().putLong(KEY_ADFREE_UNLOCK_EXPIRY, newExpiry).apply()
        _adFreeUnlockExpiry.value = newExpiry
        _hasAdFreeUnlock.value = newExpiry > now
        Log.i("IdentifyAccess", "Granted $days days ad-free unlock — expiry: $newExpiry")
    }

    /**
     * Whether ads should be hidden for this user.
     * True for Premium subscribers or while a donated ad-free unlock is active.
     */
    fun isAdFree(isPremium: Boolean): Boolean {
        return isPremium || _hasAdFreeUnlock.value
    }

    companion object {
        private const val PREFS_NAME = "identify_access_prefs"
        private const val KEY_INSTALL_WEEK_START = "install_week_start"
        private const val KEY_WEEK_INDEX = "week_index"
        private const val KEY_TRIAL_USES_USED = "trial_uses_used"
        private const val KEY_TOKEN_BALANCE = "token_balance"
        private const val KEY_LOCATION_UNLOCK_EXPIRY = "location_unlock_expiry"
        private const val KEY_ADFREE_UNLOCK_EXPIRY = "adfree_unlock_expiry"
        private const val KEY_TRIAL_INFO_SHOWN = "trial_info_shown"
        private const val KEY_TRIAL_CLAIM_PENDING = "trial_claim_pending"
        private const val KEY_TRIAL_DENIED = "trial_denied"

        private const val TRIAL_TOKEN_GRANT = 5
        private const val MILLIS_PER_WEEK = 7L * 24L * 60L * 60L * 1000L

        /** Public version for UI display — the flat 1-week trial token grant. */
        const val TRIAL_TOKEN_GRANT_PUBLIC = 5
        /** Legacy alias kept so older callers compile during the transition. */
        const val MAX_TRIAL_USES_PER_WEEK_PUBLIC = TRIAL_TOKEN_GRANT_PUBLIC

        /**
         * Returns the epoch millis of the most recent Monday 12:01 AM EST for the given time.
         * Weeks roll over at this boundary so the 5 free uses reset on Monday 12:01 AM EST.
         */
        private fun currentWeekStartMillis(nowMillis: Long): Long {
            val est = TimeZone.getTimeZone("America/New_York")
            val cal = Calendar.getInstance(est)
            cal.timeInMillis = nowMillis
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 1)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            // If now is before this Monday 12:01 AM, roll back to the previous Monday.
            return if (cal.timeInMillis > nowMillis) {
                cal.add(Calendar.WEEK_OF_YEAR, -1)
                cal.timeInMillis
            } else {
                cal.timeInMillis
            }
        }

        val instance: IdentifyAccessManager by lazy { IdentifyAccessManager() }
    }
}

/** How an identify request may proceed. */
enum class IdentifyAccess {
    /** Premium subscriber — unlimited. */
    UNLIMITED,
    /** Free trial — uses remaining this week. */
    TRIAL_AVAILABLE,
    /** No trial uses left, but purchased tokens are available. */
    TOKEN_AVAILABLE,
    /** No access — show the lock screen / purchase options. */
    LOCKED,
}

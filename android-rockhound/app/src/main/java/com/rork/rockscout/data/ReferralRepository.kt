package com.rork.rockscout.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.rork.rockscout.data.PurchaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.TimeZone
import kotlin.random.Random

/**
 * Client-side referral program state — "Enlist a RockScout".
 *
 * Generates a stable personal referral code at first launch, persists it, and
 * exposes share-ready text. Tracks pending/completed enlistment counts and
 * credits rewards when a referred user completes their first sign-in with the
 * sender's code.
 *
 * Reward rules:
 *  - Free users: 4 ID tokens + 1-day full-feature unlock per completed referral,
 *    capped at 3 rewarded referrals per week.
 *  - Premium users: 500 XP per completed referral, no weekly cap.
 *
 * The referral code is also uploaded to the Cloudflare backend so it can be
 * verified by a new user on a different device. When a new user signs in and
 * enters a valid code, the sender is linked by email, free gifts are granted to
 * the new user, and a reciprocal RockScout Friends connection is created.
 */
object ReferralRepository {

    private const val PREFS_NAME = "rockscout_referrals"
    private const val KEY_REFERRAL_CODE = "referral_code"
    private const val KEY_PENDING_COUNT = "pending_count"
    private const val KEY_COMPLETED_COUNT = "completed_count"
    private const val KEY_REWARDS_CREDITED = "rewards_credited"
    private const val KEY_REFERRAL_HISTORY = "referral_history"
    private const val KEY_REFERRAL_CODE_APPLIED = "referral_code_applied"
    private const val KEY_APPLIED_REFERRER_EMAIL = "applied_referrer_email"
    private const val KEY_REFERRAL_PROMPT_SHOWN = "referral_prompt_shown"
    private const val KEY_PENDING_REFERRAL_CODE = "pending_referral_code"
    private const val KEY_PENDING_NEW_USER_REWARD = "pending_new_user_reward"
    private const val KEY_CREDITED_COMPLETIONS = "credited_completions"
    private const val KEY_PENDING_COMPLETION = "pending_completion"
    private const val TAG = "ReferralRepository"

    /** Maximum number of rewarded referrals a free user can earn per week. */
    const val FREE_USER_WEEKLY_CAP = 3

    /** Tokens credited per completed referral for free users. */
    const val TOKEN_REWARD = 4

    /** Full-feature unlock days credited per completed referral for free users. */
    const val UNLOCK_DAYS_REWARD = 1

    /** XP credited per completed referral for premium users. */
    const val PREMIUM_XP_REWARD = 500

    private lateinit var prefs: SharedPreferences

    private val _referralCode = MutableStateFlow("")
    val referralCode: StateFlow<String> = _referralCode.asStateFlow()

    private val _pendingCount = MutableStateFlow(0)
    val pendingCount: StateFlow<Int> = _pendingCount.asStateFlow()

    private val _completedCount = MutableStateFlow(0)
    val completedCount: StateFlow<Int> = _completedCount.asStateFlow()

    private val _rewardsCredited = MutableStateFlow(0)
    val rewardsCredited: StateFlow<Int> = _rewardsCredited.asStateFlow()

    private val _referralCodeApplied = MutableStateFlow(false)
    val referralCodeApplied: StateFlow<Boolean> = _referralCodeApplied.asStateFlow()

    private val _appliedReferrerEmail = MutableStateFlow<String?>(null)
    val appliedReferrerEmail: StateFlow<String?> = _appliedReferrerEmail.asStateFlow()

    private val _referralPromptShown = MutableStateFlow(false)
    val referralPromptShown: StateFlow<Boolean> = _referralPromptShown.asStateFlow()

    /** A referral code entered on the sign-in screen before the user has signed in. */
    private val _pendingReferralCode = MutableStateFlow<String?>(null)
    val pendingReferralCode: StateFlow<String?> = _pendingReferralCode.asStateFlow()

    /**
     * Set when a new user's referral code has been verified and their free gifts
     * have been applied. The home screen shows a confirmation popup and clears it.
     */
    private val _pendingNewUserReward = MutableStateFlow<NewUserRewardData?>(null)
    val pendingNewUserReward: StateFlow<NewUserRewardData?> = _pendingNewUserReward.asStateFlow()

    /**
     * Set when the sender has pending referral completions credited. The home
     * screen shows a celebration popup and clears it.
     */
    private val _pendingSenderReward = MutableStateFlow<SenderRewardData?>(null)
    val pendingSenderReward: StateFlow<SenderRewardData?> = _pendingSenderReward.asStateFlow()

    /** Must be called once from Application.onCreate() before any access. */
    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_REFERRAL_CODE, null)
        val code = existing ?: generateCode()
        if (existing == null) {
            prefs.edit().putString(KEY_REFERRAL_CODE, code).apply()
        }
        _referralCode.value = code
        _pendingCount.value = prefs.getInt(KEY_PENDING_COUNT, 0)
        _completedCount.value = prefs.getInt(KEY_COMPLETED_COUNT, 0)
        _rewardsCredited.value = prefs.getInt(KEY_REWARDS_CREDITED, 0)
        _referralCodeApplied.value = prefs.getBoolean(KEY_REFERRAL_CODE_APPLIED, false)
        _appliedReferrerEmail.value = prefs.getString(KEY_APPLIED_REFERRER_EMAIL, null)
        _referralPromptShown.value = prefs.getBoolean(KEY_REFERRAL_PROMPT_SHOWN, false)
        _pendingReferralCode.value = prefs.getString(KEY_PENDING_REFERRAL_CODE, null)
        // Restore a pending new-user reward if the app was killed before the
        // popup was dismissed.
        prefs.getString(KEY_PENDING_NEW_USER_REWARD, null)?.let { raw ->
            if (raw.isNotBlank()) {
                val parts = raw.split("|", limit = 2)
                if (parts.size == 2) {
                    _pendingNewUserReward.value = NewUserRewardData(
                        referrerEmail = parts[0],
                        code = parts[1],
                    )
                }
            }
        }
        Log.d(
            TAG,
            "Initialized: code=$code, pending=${_pendingCount.value}, completed=${_completedCount.value}, " +
                "rewardsCredited=${_rewardsCredited.value}, codeApplied=${_referralCodeApplied.value}",
        )
    }

    /**
     * The share-ready text for the system share sheet.
     * Includes the referral code and a call-to-action.
     */
    fun shareText(): String {
        val code = _referralCode.value
        return "Join me on RockScout — I'm a rockhounder. " +
            "Identify rocks, find dig sites, and trade specimens. " +
            "Use my code $code to get started!"
    }

    /**
     * Increment the pending count (a referral link was shared / a new user
     * signed up with this code but hasn't identified yet). The backend will
     * call this when a referred user signs up.
     */
    fun incrementPending() {
        if (!::prefs.isInitialized) return
        val newPending = _pendingCount.value + 1
        prefs.edit().putInt(KEY_PENDING_COUNT, newPending).apply()
        _pendingCount.value = newPending
    }

    /**
     * Credit the referrer's reward when a referred user completes their first
     * sign-in with this user's code.
     *
     * Free users: +4 tokens + 1-day unlock, capped at 3 per week.
     * Premium users: +500 XP, no cap.
     *
     * Returns true if a reward was credited, false if the free-user weekly cap
     * was already hit.
     */
    fun creditReferralReward(): Boolean {
        if (!::prefs.isInitialized) return false

        val isPremium = PurchaseManager.instance.hasPaidAccess.value
        val history = loadReferralHistory()
        val now = System.currentTimeMillis()

        if (!isPremium) {
            val weekStart = currentWeekStartMillis(now)
            val weekCount = history.count { it >= weekStart }
            if (weekCount >= FREE_USER_WEEKLY_CAP) {
                Log.i(TAG, "Free user weekly referral cap ($FREE_USER_WEEKLY_CAP) reached — not crediting")
                return false
            }
        }

        val newHistory = history + now
        prefs.edit()
            .putString(KEY_REFERRAL_HISTORY, newHistory.joinToString(","))
            .apply()

        if (isPremium) {
            // Premium users earn 500 XP per referral (unlimited, no popup).
            AchievementsRepository.awardPremiumReferralXp()
        } else {
            // Free users earn tokens + a temporary unlock.
            IdentifyAccessManager.instance.addTokens(TOKEN_REWARD)
            IdentifyAccessManager.instance.grantLocationUnlock(UNLOCK_DAYS_REWARD)
            IdentifyAccessManager.instance.grantAdFreeUnlock(UNLOCK_DAYS_REWARD)
        }

        // Award XP — drives the Recruiter badge and level progression for all users.
        AchievementsRepository.award(XpSource.REFERRAL)

        // Update counts.
        val newCompleted = _completedCount.value + 1
        val newRewarded = _rewardsCredited.value + 1
        val newPending = (_pendingCount.value - 1).coerceAtLeast(0)

        prefs.edit()
            .putInt(KEY_COMPLETED_COUNT, newCompleted)
            .putInt(KEY_REWARDS_CREDITED, newRewarded)
            .putInt(KEY_PENDING_COUNT, newPending)
            .apply()

        _completedCount.value = newCompleted
        _rewardsCredited.value = newRewarded
        _pendingCount.value = newPending

        Log.i(
            TAG,
            "Credited referral reward #$newRewarded: " +
                if (isPremium) "+$PREMIUM_XP_REWARD XP" else "+$TOKEN_REWARD tokens, +$UNLOCK_DAYS_REWARD day unlock",
        )
        return true
    }

    /**
     * Check the backend for unacknowledged referral completions (friends who
     * signed in with this user's code) and credit the sender's reward for each
     * one. Called on sign-in so the sender gets their rewards as soon as they
     * open the app.
     *
     * A referral is only "completed" when the new user has both verified the
     * code AND signed in — the backend won't return it until both steps are
     * done. This prevents fake accounts from farming rewards.
     *
     * Returns the number of completions credited.
     */
    suspend fun checkAndCreditCompletions(): Int {
        val senderEmail = AuthRepository.instance.currentUserEmail
        if (senderEmail.isNullOrBlank()) return 0

        val completions = ReferralApi.checkCompletions(senderEmail)
        if (completions.isEmpty()) return 0

        // Load the set of completions we've already credited locally. This
        // prevents double-crediting when acknowledgeCompletion fails (network
        // timeout, 5xx, app killed before POST completes) and the backend
        // returns the same completion again on the next sign-in.
        val creditedSet = loadCreditedCompletions()

        var credited = 0
        for (entry in completions) {
            val completionKey = "${entry.code}|${entry.recipientEmail}"
            if (completionKey in creditedSet) {
                // Already credited locally — re-acknowledge in case the previous
                // ack failed, then skip the reward.
                ReferralApi.acknowledgeCompletion(
                    senderEmail = senderEmail,
                    code = entry.code,
                    recipientEmail = entry.recipientEmail,
                )
                continue
            }
            // creditReferralReward handles the weekly cap for free users —
            // if the cap is hit it returns false and we skip acknowledging
            // so the completion stays on the backend for next week.
            val rewarded = creditReferralReward()
            if (rewarded) {
                credited++
                // Record the credit BEFORE acking so a crash between the two
                // doesn't result in a re-credit next time.
                creditedSet.add(completionKey)
                saveCreditedCompletions(creditedSet)
                // Acknowledge so the backend doesn't return it again.
                ReferralApi.acknowledgeCompletion(
                    senderEmail = senderEmail,
                    code = entry.code,
                    recipientEmail = entry.recipientEmail,
                )
                // Also create the RockScout Friends connection so the sender
                // and the new user can find each other.
                runCatching {
                    SocialRepository.instance.createConnectionFromReferral(entry.recipientEmail)
                }
            } else {
                // Free-user weekly cap reached — stop processing. Remaining
                // completions will be credited next week when the cap resets.
                break
            }
        }

        if (credited > 0) {
            Log.i(TAG, "Credited $credited referral completion(s) from backend")
            // Queue a sender reward popup so the home screen can celebrate.
            val isPremium = PurchaseManager.instance.hasPaidAccess.value
            _pendingSenderReward.value = SenderRewardData(
                count = credited,
                isPremium = isPremium,
            )
        }
        return credited
    }

    /**
     * Apply a referral code as a newly signed-up user. Called once after the
     * code is verified against the backend. Marks the code as used, stores the
     * referrer's email, and grants the new-user free gifts.
     *
     * Free gifts for the new user: 4 ID tokens + 1-day full-feature unlock.
     */
    fun applyReferralCode(referrerEmail: String): Boolean {
        if (!::prefs.isInitialized) return false
        if (_referralCodeApplied.value) {
            Log.i(TAG, "Referral code already applied for this user")
            return false
        }

        IdentifyAccessManager.instance.addTokens(TOKEN_REWARD)
        IdentifyAccessManager.instance.grantLocationUnlock(UNLOCK_DAYS_REWARD)
        IdentifyAccessManager.instance.grantAdFreeUnlock(UNLOCK_DAYS_REWARD)

        prefs.edit()
            .putBoolean(KEY_REFERRAL_CODE_APPLIED, true)
            .putString(KEY_APPLIED_REFERRER_EMAIL, referrerEmail)
            .apply()

        _referralCodeApplied.value = true
        _appliedReferrerEmail.value = referrerEmail

        Log.i(TAG, "Applied referral code from $referrerEmail: +$TOKEN_REWARD tokens, +$UNLOCK_DAYS_REWARD day unlock")
        return true
    }

    /**
     * Store a referral code entered on the sign-in screen before the user has
     * signed in. The code is persisted so it survives an app kill and is
     * verified after sign-in completes.
     */
    fun setPendingReferralCode(code: String) {
        if (!::prefs.isInitialized) return
        val trimmed = code.trim().uppercase()
        prefs.edit().putString(KEY_PENDING_REFERRAL_CODE, trimmed).apply()
        _pendingReferralCode.value = trimmed
        Log.i(TAG, "Stored pending referral code: $trimmed")
    }

    /** Clear the pending referral code after it has been processed or skipped. */
    fun clearPendingReferralCode() {
        if (!::prefs.isInitialized) return
        prefs.edit().remove(KEY_PENDING_REFERRAL_CODE).apply()
        _pendingReferralCode.value = null
    }

    /**
     * Process a pending referral code after sign-in. Verifies the code against
     * the backend, applies the new-user's free gifts, creates the RockScout
     * Friends connection, notifies the backend that the referral is complete,
     * and sets [pendingNewUserReward] so the home screen can show a popup.
     *
     * Returns true if the code was valid and rewards were applied.
     */
    suspend fun processPendingReferralCode(): Boolean {
        val code = _pendingReferralCode.value ?: return false
        val email = AuthRepository.instance.currentUserEmail
        if (email.isNullOrBlank()) return false
        if (_referralCodeApplied.value) {
            clearPendingReferralCode()
            return false
        }

        val senderEmail = ReferralApi.verifyReferralCode(code, email)
        if (senderEmail == null) {
            Log.w(TAG, "Pending referral code $code was not found on backend")
            clearPendingReferralCode()
            return false
        }
        // Guard against self-referral — users cannot use their own code
        if (senderEmail.equals(email, ignoreCase = true)) {
            Log.w(TAG, "Self-referral detected — blocking")
            clearPendingReferralCode()
            return false
        }

        applyReferralCode(senderEmail)
        runCatching {
            SocialRepository.instance.createConnectionFromReferral(senderEmail)
        }
        val completed = ReferralApi.completeReferral(code, email)
        markReferralPromptShown()
        if (completed == null) {
            // Backend completion failed (network error / backend down). Keep
            // the pending code so we retry on the next sign-in; the new user
            // keeps their welcome gifts either way, but the sender won't be
            // credited until the completion is recorded.
            Log.w(TAG, "completeReferral failed for $code — will retry next sign-in")
            // Still surface the new-user reward popup since applyReferralCode
            // already granted the welcome gifts.
            val reward = NewUserRewardData(referrerEmail = senderEmail, code = code)
            _pendingNewUserReward.value = reward
            prefs.edit().putString(KEY_PENDING_NEW_USER_REWARD, "$senderEmail|$code").apply()
            return false
        }
        clearPendingReferralCode()

        // Set the pending reward data so the home screen shows a popup.
        val reward = NewUserRewardData(referrerEmail = senderEmail, code = code)
        _pendingNewUserReward.value = reward
        prefs.edit().putString(KEY_PENDING_NEW_USER_REWARD, "$senderEmail|$code").apply()

        Log.i(TAG, "Processed pending referral code: rewards applied, popup queued")
        return true
    }

    /** Clear the pending new-user reward after the popup has been dismissed. */
    fun clearPendingNewUserReward() {
        if (!::prefs.isInitialized) return
        prefs.edit().remove(KEY_PENDING_NEW_USER_REWARD).apply()
        _pendingNewUserReward.value = null
    }

    /** Clear the pending sender reward after the popup has been dismissed. */
    fun clearPendingSenderReward() {
        _pendingSenderReward.value = null
    }

    /** Returns true if the current user has already applied a referral code. */
    fun hasAppliedReferralCode(): Boolean = _referralCodeApplied.value

    /** Returns true if the first-sign-in referral prompt has already been shown. */
    fun hasReferralPromptBeenShown(): Boolean = _referralPromptShown.value

    /** Marks the first-sign-in referral prompt as shown so it only appears once. */
    fun markReferralPromptShown() {
        if (!::prefs.isInitialized) return
        prefs.edit().putBoolean(KEY_REFERRAL_PROMPT_SHOWN, true).apply()
        _referralPromptShown.value = true
    }

    /** Number of rewarded referrals credited to the current user this week. */
    fun weeklyRewardedCount(): Int {
        if (!::prefs.isInitialized) return 0
        val weekStart = currentWeekStartMillis(System.currentTimeMillis())
        return loadReferralHistory().count { it >= weekStart }
    }

    /** Whether the current free user has hit the weekly reward cap. */
    fun isFreeWeeklyCapReached(): Boolean =
        !PurchaseManager.instance.hasPaidAccess.value && weeklyRewardedCount() >= FREE_USER_WEEKLY_CAP

    /** Generates a human-readable referral code like "ROCK-A4F92K". */
    private fun generateCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val suffix = (1..6).map { chars[Random.nextInt(chars.length)] }.joinToString("")
        return "ROCK-$suffix"
    }

    private fun loadReferralHistory(): List<Long> {
        val raw = prefs.getString(KEY_REFERRAL_HISTORY, "") ?: ""
        return raw.split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { it.toLongOrNull() }
    }

    /**
     * Locally persisted set of completed referrals we have already credited.
     * Keyed by "<code>|<recipientEmail>" so a completion is never double-credited
     * even if the backend acknowledgment fails and the completion is returned
     * again on the next sign-in.
     */
    private fun loadCreditedCompletions(): MutableSet<String> {
        if (!::prefs.isInitialized) return mutableSetOf()
        val raw = prefs.getString(KEY_CREDITED_COMPLETIONS, "") ?: ""
        return raw.split("\n")
            .filter { it.isNotBlank() }
            .toMutableSet()
    }

    private fun saveCreditedCompletions(set: Set<String>) {
        if (!::prefs.isInitialized) return
        prefs.edit().putString(KEY_CREDITED_COMPLETIONS, set.joinToString("\n")).apply()
    }

    /**
     * Data for a new-user referral reward popup (the referred user's welcome gift).
     */
    data class NewUserRewardData(
        val referrerEmail: String,
        val code: String,
    )

    /**
     * Data for a sender referral reward popup (when a friend completes a referral).
     */
    data class SenderRewardData(
        val count: Int,
        val isPremium: Boolean,
    )

    /**
     * Returns the epoch millis of the most recent Monday 12:01 AM EST for the given time.
     * Weeks roll over at this boundary so the weekly cap resets on Monday 12:01 AM EST.
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
        return if (cal.timeInMillis > nowMillis) {
            cal.add(Calendar.WEEK_OF_YEAR, -1)
            cal.timeInMillis
        } else {
            cal.timeInMillis
        }
    }
}

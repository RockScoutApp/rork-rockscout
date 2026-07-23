package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * XP source — the action that earned the points. Drives the badge catalog and
 * the consecutive-day streak counter (only [IDENTIFY] and [CAPTURE] count
 * toward the daily streak).
 */
enum class XpSource(val xp: Int, val label: String) {
    IDENTIFY(10, "Rock identified"),
    CAPTURE(25, "Field capture logged"),
    WISHLIST_HIT(25, "Wishlist specimen found"),
    DIG_SITE_CHECKIN(5, "Dig site check-in"),
    JOURNAL_ENTRY(10, "Journal entry written"),
    TRADE(30, "Specimen traded"),
    REFERRAL(50, "RockScout enlisted"),
}

/**
 * Level tier with rockhound-themed names. The top three carry the RockScout
 * name so the most advanced users literally become RockScouts on their scan
 * cards and map pins.
 *
 * Progression: Pocket Pebble (L1–3) → Shelf Specimen (L4–7) → Collector
 * (L8–12) → Field Hunter (L13–18) → Senior RockScout (L19–25) → Expert
 * RockScout (L26–33) → Master RockScout (L34+).
 */
enum class LevelTier(val displayName: String, val levelRange: IntRange, val emoji: String) {
    POCKET_PEBBLE("Pocket Pebble", 1..3, "\uD83E\uDEA3"),
    SHELF_SPECIMEN("Shelf Specimen", 4..7, "\uD83D\uDC8E"),
    COLLECTOR("Collector", 8..12, "\uD83E\uDEA8"),
    FIELD_HUNTER("Field Hunter", 13..18, "⛰\uFE0F"),
    SENIOR_ROCKSCOUT("Senior RockScout", 19..25, "\uD83D\uDD6E"),
    EXPERT_ROCKSCOUT("Expert RockScout", 26..33, "\uD83D\uDD6F\uFE0F"),
    MASTER_ROCKSCOUT("Master RockScout", 34..Int.MAX_VALUE, "\uD83C\uDF1F");

    companion object {
        /** Returns the tier that contains [level], or the highest tier if above. */
        fun forLevel(level: Int): LevelTier =
            values().first { level in it.levelRange }
    }
}

/**
 * A badge in the catalog. Earned badges appear in a grid on the Profile tab;
 * tapping an unearned badge shows its requirement.
 */
@Serializable
data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String,
    /** Predicate-style requirement, evaluated by [AchievementsRepository.checkBadges]. */
    val requirement: Requirement,
) {
    @Serializable
    data class Requirement(
        val type: String,           // "identify_count", "streak_days", "family_complete", etc.
        val threshold: Int = 0,
        val familyTag: String = "", // for family-completion badges
    )
}

/**
 * Persisted XP/level/badge/streak state. Stored as JSON in SharedPreferences
 * so it survives app restarts. The full event log isn't kept on-device — only
 * the aggregate counters needed to evaluate badges and the level curve.
 */
@Serializable
data class AchievementsState(
    val totalXp: Int = 0,
    val identifyCount: Int = 0,
    val captureCount: Int = 0,
    val wishlistHitCount: Int = 0,
    val digSiteCheckInCount: Int = 0,
    val journalEntryCount: Int = 0,
    val tradeCount: Int = 0,
    val referralCount: Int = 0,
    /** Epoch day (UTC) of the last qualifying action for the streak. */
    val lastStreakDay: Long = 0L,
    /** Current consecutive-day streak count. */
    val currentStreak: Int = 0,
    /** Longest streak ever achieved (for display). */
    val longestStreak: Int = 0,
    /** IDs of earned badges. */
    val earnedBadgeIds: Set<String> = emptySet(),
    /** Epoch ms of last badge-earn / level-up event (drives the celebratory overlay). */
    val lastCelebrationMs: Long = 0L,
    val lastCelebrationType: String = "",   // "level_up" | "badge_earned"
    val lastCelebrationDetail: String = "", // e.g. "Level 8 — Collector" or "Agate Hunter"
    /** Total completed premium referrals (500 XP each, unlimited). */
    val premiumReferralCount: Int = 0,
    /** IDs of completed achievements (from the 100-achievement catalog). */
    val completedAchievementIds: Set<String> = emptySet(),
    /** Epoch ms of last achievement-completion event (drives achievement popup). */
    val lastAchievementCelebrationMs: Long = 0L,
    val lastAchievementCelebrationId: String = "",
    /** Epoch ms of last badge-earn event (drives badge popup, independent channel). */
    val lastBadgeCelebrationMs: Long = 0L,
    val lastBadgeCelebrationId: String = "",
)

/**
 * Client-side achievements, XP, leveling, badges, and daily streaks.
 *
 * XP is awarded on each qualifying action via [award]. The award methods are
 * called from the existing action points (IdentifyScreen on identification,
 * CapturesScreen on field capture, etc.). State persists to SharedPreferences
 * via a dedicated prefs file so it survives app restarts.
 *
 * Server-side sync is a later phase (the backend will mirror the XP event log
 * and compute badge awards server-side for cross-device consistency); for now
 * the client is the source of truth and the system works standalone.
 */
object AchievementsRepository {

    private const val PREFS_NAME = "rockscout_achievements"
    private const val KEY_STATE = "state_json"
    private const val TAG = "AchievementsRepository"

    /**
     * XP required to reach a given level. Progressive RPG curve: each level
     * requires 200 more XP than the previous one.
     * L2=150, L3=500, L5=1800, L10=8550, L20=37050, L30=85550, L50=242550.
     */
    fun xpForLevel(level: Int): Int {
        if (level <= 1) return 0
        val n = level - 1
        return 100 * n * n + 50 * n
    }

    /** The level a user with [totalXp] has reached. */
    fun levelForXp(totalXp: Int): Int {
        var level = 1
        while (xpForLevel(level + 1) <= totalXp) level++
        return level
    }

    /** Progress (0f..1f) toward the next level from [totalXp]. */
    fun levelProgress(totalXp: Int): Float {
        val level = levelForXp(totalXp)
        val cur = xpForLevel(level)
        val next = xpForLevel(level + 1)
        if (next == cur) return 1f
        return (totalXp - cur).toFloat() / (next - cur).toFloat()
    }

    /** Full badge catalog (31 badges). Stable ordering — UI renders in this order. */
    val badgeCatalog: List<Badge> = listOf(
        // ── Identification milestones ──
        Badge("first_10", "First 10", "Identify 10 rocks.", "\uD83C\uDFC6", Badge.Requirement("identify_count", 10)),
        Badge("century_club", "Century Club", "Identify 100 rocks.", "\uD83C\uDFC5", Badge.Requirement("identify_count", 100)),
        Badge("rock_sage", "Rock Sage", "Identify 500 rocks.", "\uD83D\uDCD0", Badge.Requirement("identify_count", 500)),
        Badge("grandmaster_hound", "Grandmaster Hound", "Identify 1,000 rocks.", "\uD83C\uDF1F", Badge.Requirement("identify_count", 1000)),
        // ── Field capture milestones ──
        Badge("shutterbug", "Shutterbug", "Log 10 field captures.", "\uD83D\uDCF7", Badge.Requirement("capture_count", 10)),
        Badge("field_season", "Field Season", "Log 50 field captures.", "\uD83C\uDFD5\uFE0F", Badge.Requirement("capture_count", 50)),
        Badge("seasoned_hunter", "Seasoned Field Hunter", "Log 200 field captures.", "\u26F0\uFE0F", Badge.Requirement("capture_count", 200)),
        // ── Wishlist milestones ──
        Badge("wishlist_rookie", "Wishlist Rookie", "Find 5 wishlist specimens.", "\uD83D\uDC9D", Badge.Requirement("wishlist_count", 5)),
        Badge("wishlist_master", "Wishlist Master", "Find 25 wishlist specimens.", "\uD83D\uDC8D", Badge.Requirement("wishlist_count", 25)),
        // ── Streak milestones ──
        Badge("streak_7", "Streak: 7 Days", "Identify or capture a rock 7 days in a row.", "\uD83D\uDD25", Badge.Requirement("streak_days", 7)),
        Badge("streak_30", "Streak: 30 Days", "Identify or capture a rock 30 days in a row.", "\u26A1", Badge.Requirement("streak_days", 30)),
        Badge("streak_100", "Century Streak", "Keep a 100-day streak alive.", "\uD83D\uDCAF", Badge.Requirement("streak_days", 100)),
        // ── Dig site check-ins ──
        Badge("site_hopper", "Site Hopper", "Check in at 10 dig sites.", "\uD83D\uDDFA\uFE0F", Badge.Requirement("digsite_count", 10)),
        Badge("dig_veteran", "Dig Veteran", "Check in at 50 dig sites.", "\u26CF\uFE0F", Badge.Requirement("digsite_count", 50)),
        // ── Journal entries ──
        Badge("journal_keeper", "Journal Keeper", "Write 10 journal entries.", "\uD83D\uDCD8", Badge.Requirement("journal_count", 10)),
        Badge("chronicler", "Chronicler", "Write 50 journal entries.", "\u270D\uFE0F", Badge.Requirement("journal_count", 50)),
        // ── Trades ──
        Badge("trader", "Trader", "Complete your first specimen swap.", "\uD83D\uDD04", Badge.Requirement("trade_count", 1)),
        Badge("swap_meet", "Swap Meet", "Complete 5 specimen swaps.", "\uD83E\uDD1D", Badge.Requirement("trade_count", 5)),
        Badge("baron_barter", "Baron of Barter", "Complete 25 specimen swaps.", "\uD83D\uDCB0", Badge.Requirement("trade_count", 25)),
        // ── Referrals ──
        Badge("recruiter", "Recruiter", "Enlist your first RockScout.", "\uD83D\uDCE2", Badge.Requirement("referral_count", 1)),
        Badge("squad_leader", "Squad Leader", "Enlist 5 RockScouts.", "\uD83D\uDC65", Badge.Requirement("referral_count", 5)),
        Badge("rockscout_legion", "RockScout Legion", "Enlist 25 RockScouts.", "\uD83D\uDC51", Badge.Requirement("referral_count", 25)),
        // ── Collection size ──
        Badge("curator", "Curator", "Collect 25 distinct specimens.", "\uD83C\uDFEC", Badge.Requirement("collection_size", 25)),
        Badge("museum_keeper", "Museum Keeper", "Collect 100 distinct specimens.", "\uD83C\uDFDB\uFE0F", Badge.Requirement("collection_size", 100)),
        // ── Level milestones ──
        Badge("rising_hunter", "Rising Hunter", "Reach level 10.", "\uD83C\uDFC3", Badge.Requirement("level_reached", 10)),
        Badge("senior_scout", "Senior Scout", "Reach level 25.", "\uD83E\uDEA8", Badge.Requirement("level_reached", 25)),
        // ── Fossil & family badges ──
        Badge("first_fossil", "First Fossil", "Identify your first fossil.", "\uD83E\uDDA0", Badge.Requirement("first_fossil")),
        Badge("quartz_family", "Quartz Family Complete", "Collect every quartz-family specimen.", "\uD83D\uDC8E", Badge.Requirement("family_complete", familyTag = "quartz")),
        Badge("agate_hunter", "Agate Hunter", "Collect 10 agate species.", "\uD83C\uDFAF", Badge.Requirement("family_complete", 10, "agate")),
        Badge("completionist", "Completionist", "Complete one full specimen family.", "\uD83C\uDFC9", Badge.Requirement("any_family_complete")),
    )

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    private lateinit var prefs: android.content.SharedPreferences

    private val _state = MutableStateFlow(AchievementsState())
    val state: StateFlow<AchievementsState> = _state.asStateFlow()

    /** Convenience: current level derived from total XP. */
    val level: StateFlow<Int> = MutableStateFlow(1)
    /** Convenience: current tier derived from level. */
    val tier: StateFlow<LevelTier> = MutableStateFlow(LevelTier.POCKET_PEBBLE)

    /** Must be called once from Application.onCreate() before any access. */
    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        runCatching {
            prefs.getString(KEY_STATE, null)?.let {
                _state.value = json.decodeFromString(AchievementsState.serializer(), it)
            }
        }.onFailure { Log.w(TAG, "Failed to load achievements: ${it.message}") }
        recomputeDerived()
        // On init, check for streak reset (missed day) without awarding XP.
        applyStreakResetOnMissedDay()
        Log.d(TAG, "Initialized: level ${level.value} (${tier.value.displayName}), ${_state.value.totalXp} XP, ${_state.value.earnedBadgeIds.size} badges")
    }

    /**
     * Award XP for [source]. Updates the aggregate counter, the streak, and
     * the derived level/tier. Returns the event result (level-ups, new badges)
     * so the caller can fire the celebratory overlay if anything leveled up.
     *
     * For [XpSource.WISHLIST_HIT], pass [familyTag] so family-completion
     * badges can be evaluated (e.g. "quartz", "agate").
     */
    fun award(source: XpSource, familyTag: String = ""): AwardResult {
        val prev = _state.value
        val now = System.currentTimeMillis()
        val today = epochDay(now)

        // Streak handling — only IDENTIFY and CAPTURE count toward the daily streak.
        val countsForStreak = source == XpSource.IDENTIFY || source == XpSource.CAPTURE
        var newStreak = prev.currentStreak
        var longest = prev.longestStreak
        if (countsForStreak) {
            newStreak = when {
                prev.lastStreakDay == 0L -> 1                  // first ever qualifying action
                prev.lastStreakDay == today -> newStreak       // already counted today (no double)
                prev.lastStreakDay == today - 1 -> newStreak + 1 // consecutive day
                else -> 1                                       // missed day(s) — reset to 1
            }
            longest = maxOf(longest, newStreak)
        }

        // Streak bonus: +5 XP per consecutive day (applied on streak-qualifying actions).
        val streakBonus = if (countsForStreak && newStreak > 1) 5 * (newStreak - 1) else 0
        val xpGain = source.xp + streakBonus

        val next = prev.copy(
            totalXp = prev.totalXp + xpGain,
            identifyCount = prev.identifyCount + if (source == XpSource.IDENTIFY) 1 else 0,
            captureCount = prev.captureCount + if (source == XpSource.CAPTURE) 1 else 0,
            wishlistHitCount = prev.wishlistHitCount + if (source == XpSource.WISHLIST_HIT) 1 else 0,
            digSiteCheckInCount = prev.digSiteCheckInCount + if (source == XpSource.DIG_SITE_CHECKIN) 1 else 0,
            journalEntryCount = prev.journalEntryCount + if (source == XpSource.JOURNAL_ENTRY) 1 else 0,
            tradeCount = prev.tradeCount + if (source == XpSource.TRADE) 1 else 0,
            referralCount = prev.referralCount + if (source == XpSource.REFERRAL) 1 else 0,
            lastStreakDay = if (countsForStreak) today else prev.lastStreakDay,
            currentStreak = newStreak,
            longestStreak = longest,
        )
        return commitAndEvaluate(next, prev, source, familyTag, xpGain)
    }

    /**
     * Evaluate the badge catalog + achievement catalog + level changes after a
     * state update, persist, and return an [AwardResult] describing what changed
     * (for the overlay).
     */
    private fun commitAndEvaluate(
        next: AchievementsState,
        prev: AchievementsState,
        source: XpSource,
        familyTag: String,
        xpGain: Int,
    ): AwardResult {
        val prevLevel = levelForXp(prev.totalXp)

        // ── Detect newly completed achievements from the 100-achievement catalog ──
        val prevCompleted = prev.completedAchievementIds
        val newlyCompletedAchievements = mutableListOf<com.rork.rockscout.data.Achievement>()
        AchievementCatalog.all.forEach { ach ->
            if (ach.id !in prevCompleted &&
                AchievementCatalog.isEarned(ach, next, levelForXp(next.totalXp))
            ) {
                newlyCompletedAchievements += ach
            }
        }
        // Credit one-time reward XP for newly completed achievements.
        val rewardXp = newlyCompletedAchievements.sumOf { it.rewardXp }
        val afterAchievements = if (newlyCompletedAchievements.isNotEmpty()) {
            next.copy(
                totalXp = next.totalXp + rewardXp,
                completedAchievementIds = prevCompleted + newlyCompletedAchievements.map { it.id },
            )
        } else {
            next
        }

        val newLevel = levelForXp(afterAchievements.totalXp)
        val leveledUp = newLevel > prevLevel

        // Evaluate badges against the new state.
        val newlyEarned = mutableListOf<Badge>()
        badgeCatalog.forEach { badge ->
            if (badge.id !in afterAchievements.earnedBadgeIds && badgeEarned(badge, afterAchievements, familyTag)) {
                newlyEarned += badge
            }
        }
        val finalState = if (newlyEarned.isNotEmpty()) {
            afterAchievements.copy(earnedBadgeIds = afterAchievements.earnedBadgeIds + newlyEarned.map { it.id })
        } else {
            afterAchievements
        }

        // Level-up celebration takes priority over badge/achievement for the main overlay.
        val celebration = if (leveledUp) {
            Celebration(
                type = "level_up",
                detail = "Level $newLevel — ${LevelTier.forLevel(newLevel).displayName}",
                emoji = LevelTier.forLevel(newLevel).emoji,
            )
        } else null

        // Achievement celebration — fires independently so both popups can show.
        val achievementCelebration = if (newlyCompletedAchievements.isNotEmpty()) {
            AchievementCelebrationData(
                achievementId = newlyCompletedAchievements.first().id,
                name = newlyCompletedAchievements.first().name,
                emoji = newlyCompletedAchievements.first().emoji,
                rewardXp = newlyCompletedAchievements.first().rewardXp,
                description = newlyCompletedAchievements.first().description,
            )
        } else null

        // Badge celebration — fires independently (its own channel) so badge-earn
        // popups show even when a level-up or achievement also fired the same action.
        val badgeCelebration = if (newlyEarned.isNotEmpty()) {
            BadgeCelebrationData(
                badgeId = newlyEarned.first().id,
                name = newlyEarned.first().name,
                emoji = newlyEarned.first().emoji,
                description = newlyEarned.first().description,
            )
        } else null

        val persisted = finalState.copy(
            lastCelebrationMs = if (celebration != null) System.currentTimeMillis() else finalState.lastCelebrationMs,
            lastCelebrationType = if (celebration != null) celebration.type else finalState.lastCelebrationType,
            lastCelebrationDetail = if (celebration != null) celebration.detail else finalState.lastCelebrationDetail,
            lastAchievementCelebrationMs = if (achievementCelebration != null) System.currentTimeMillis() else finalState.lastAchievementCelebrationMs,
            lastAchievementCelebrationId = if (achievementCelebration != null) achievementCelebration.achievementId else finalState.lastAchievementCelebrationId,
            lastBadgeCelebrationMs = if (badgeCelebration != null) System.currentTimeMillis() else finalState.lastBadgeCelebrationMs,
            lastBadgeCelebrationId = if (badgeCelebration != null) badgeCelebration.badgeId else finalState.lastBadgeCelebrationId,
        )

        _state.value = persisted
        persist(persisted)
        recomputeDerived()
        return AwardResult(
            xpGained = xpGain + rewardXp,
            newTotalXp = persisted.totalXp,
            newLevel = newLevel,
            leveledUp = leveledUp,
            newBadges = newlyEarned,
            celebration = celebration,
            achievementCelebration = achievementCelebration,
            badgeCelebration = badgeCelebration,
            newAchievements = newlyCompletedAchievements,
        )
    }

    /** Predicate: is [badge] earned given [s]? */
    private fun badgeEarned(badge: Badge, s: AchievementsState, actionFamilyTag: String): Boolean = when (badge.requirement.type) {
        "identify_count" -> s.identifyCount >= badge.requirement.threshold
        "capture_count" -> s.captureCount >= badge.requirement.threshold
        "trade_count" -> s.tradeCount >= badge.requirement.threshold
        "referral_count" -> s.referralCount >= badge.requirement.threshold
        "streak_days" -> s.longestStreak >= badge.requirement.threshold
        "first_fossil" -> {
            // First-fossil badge: awarded when a fossil-class specimen has been identified.
            // The identify counter doesn't track class, so we approximate by checking
            // the captures log for any fossil-class specimen. This keeps it client-side.
            AppRepository.instance.captures.value.any { cap ->
                SeedData.specimenById(cap.specimenId)?.rockClass == RockClass.FOSSIL
            }
        }
        "family_complete" -> {
            val tag = badge.requirement.familyTag
            val collected = AppRepository.instance.collection.value.mapNotNull {
                SeedData.specimenById(it.specimenId)
            }
            if (badge.requirement.threshold > 0) {
                // Threshold-style (e.g. Agate Hunter = 10 agate species)
                collected.count { spec -> specMatchesFamily(spec, tag) } >= badge.requirement.threshold
            } else {
                // Full-family completion (e.g. Quartz Family Complete)
                val familyMembers = SeedData.allSpecimens.filter { specMatchesFamily(it, tag) }
                familyMembers.isNotEmpty() && familyMembers.all { member ->
                    collected.any { it.id == member.id }
                }
            }
        }
        "any_family_complete" -> {
            // One full family completed. Check each distinct family tag in the catalog.
            val families = badgeCatalog.mapNotNull {
                if (it.requirement.type == "family_complete" && it.requirement.threshold == 0) it.requirement.familyTag else null
            }.distinct()
            val collected = AppRepository.instance.collection.value.mapNotNull { SeedData.specimenById(it.specimenId) }
            families.any { tag ->
                val members = SeedData.allSpecimens.filter { spec -> specMatchesFamily(spec, tag) }
                members.isNotEmpty() && members.all { m -> collected.any { it.id == m.id } }
            }
        }
        "wishlist_count" -> s.wishlistHitCount >= badge.requirement.threshold
        "digsite_count" -> s.digSiteCheckInCount >= badge.requirement.threshold
        "journal_count" -> s.journalEntryCount >= badge.requirement.threshold
        "collection_size" -> AppRepository.instance.collection.value.size >= badge.requirement.threshold
        "level_reached" -> levelForXp(s.totalXp) >= badge.requirement.threshold
        else -> false
    }

    /** Loose family matcher — a specimen belongs to a family if its id/name contains the tag. */
    private fun specMatchesFamily(spec: Specimen, tag: String): Boolean {
        if (tag.isBlank()) return false
        val id = spec.id.lowercase()
        val name = spec.name.lowercase()
        return id.contains(tag) || name.contains(tag) ||
            (tag == "quartz" && (id.contains("quartz") || id.contains("agate") || id.contains("jasper") || id.contains("chalcedony") || id.contains("citrine") || id.contains("amethyst") || id.contains("smoky"))) ||
            (tag == "agate" && (id.contains("agate") || name.contains("agate")))
    }

    /** Recompute the derived level/tier StateFlows from the current XP. */
    private fun recomputeDerived() {
        val lvl = levelForXp(_state.value.totalXp)
        (level as MutableStateFlow).value = lvl
        (tier as MutableStateFlow).value = LevelTier.forLevel(lvl)
    }

    /** If the user missed a day, reset the streak counter (without awarding XP). */
    private fun applyStreakResetOnMissedDay() {
        val s = _state.value
        if (s.lastStreakDay == 0L) return
        val today = epochDay(System.currentTimeMillis())
        if (s.lastStreakDay < today - 1) {
            // Missed at least one full day — reset streak to 0.
            val reset = s.copy(currentStreak = 0)
            _state.value = reset
            persist(reset)
        }
    }

    private fun persist(s: AchievementsState) {
        if (!::prefs.isInitialized) return
        runCatching {
            prefs.edit().putString(KEY_STATE, json.encodeToString(AchievementsState.serializer(), s)).apply()
        }.onFailure { Log.w(TAG, "Failed to persist achievements: ${it.message}") }
    }

    /**
     * Add raw XP without incrementing any action counters or streaks. Used for
     * one-off XP bonuses that should not affect the daily streak or other counters.
     */
    fun addXp(amount: Int, source: XpSource): AwardResult {
        val prev = _state.value
        val next = prev.copy(totalXp = prev.totalXp + amount)
        return commitAndEvaluate(next, prev, source, "", amount)
    }

    /**
     * Award 500 XP for a completed premium referral. Increments the
     * [premiumReferralCount] counter (used by the unlimited referral XP bar and
     * premium-referral achievements). Does NOT trigger a celebratory popup since
     * these can happen many times in a row.
     */
    fun awardPremiumReferralXp(): AwardResult {
        val prev = _state.value
        val next = prev.copy(
            totalXp = prev.totalXp + 500,
            premiumReferralCount = prev.premiumReferralCount + 1,
        )
        val result = commitAndEvaluate(next, prev, XpSource.REFERRAL, "", 500)
        // Suppress the achievement/badge celebrations for this call —
        // premium referral XP can fire many times in a row and popups would spam.
        return result.copy(
            achievementCelebration = null,
            badgeCelebration = null,
        )
    }

    /** Clear all achievements (e.g. on sign-out). */
    fun clear() {
        if (!::prefs.isInitialized) return
        prefs.edit().clear().apply()
        _state.value = AchievementsState()
        recomputeDerived()
    }

    /** Consume the pending celebration (after the overlay shows it). */
    fun clearCelebration() {
        val s = _state.value
        if (s.lastCelebrationMs == 0L) return
        _state.value = s.copy(lastCelebrationMs = 0L, lastCelebrationType = "", lastCelebrationDetail = "")
        persist(_state.value)
    }

    /** Consume the pending achievement celebration (after the overlay shows it). */
    fun clearAchievementCelebration() {
        val s = _state.value
        if (s.lastAchievementCelebrationMs == 0L) return
        _state.value = s.copy(lastAchievementCelebrationMs = 0L, lastAchievementCelebrationId = "")
        persist(_state.value)
    }

    /** Consume the pending badge celebration (after the overlay shows it). */
    fun clearBadgeCelebration() {
        val s = _state.value
        if (s.lastBadgeCelebrationMs == 0L) return
        _state.value = s.copy(lastBadgeCelebrationMs = 0L, lastBadgeCelebrationId = "")
        persist(_state.value)
    }

    /** Get the Badge for the pending badge celebration, if any. */
    fun pendingBadgeCelebration(): BadgeCelebrationData? {
        val s = _state.value
        if (s.lastBadgeCelebrationMs == 0L || s.lastBadgeCelebrationId.isBlank()) return null
        val badge = badgeCatalog.firstOrNull { it.id == s.lastBadgeCelebrationId } ?: return null
        return BadgeCelebrationData(
            badgeId = badge.id,
            name = badge.name,
            emoji = badge.emoji,
            description = badge.description,
        )
    }

    /** Get the Achievement for the pending achievement celebration, if any. */
    fun pendingAchievementCelebration(): AchievementCelebrationData? {
        val s = _state.value
        if (s.lastAchievementCelebrationMs == 0L || s.lastAchievementCelebrationId.isBlank()) return null
        val ach = AchievementCatalog.byId(s.lastAchievementCelebrationId) ?: return null
        return AchievementCelebrationData(
            achievementId = ach.id,
            name = ach.name,
            emoji = ach.emoji,
            rewardXp = ach.rewardXp,
            description = ach.description,
        )
    }

    private fun epochDay(ms: Long): Long = TimeUnit.MILLISECONDS.toDays(ms)

    /**
     * Synthesize a read-only [AchievementsState] for another user based on their
     * level and total XP. Used by the UserAchievementsScreen to display a
     * read-only view of another hunter's level, tier, and badge catalog.
     *
     * Badge earned status is approximated: since the server only stores level
     * + XP (not individual counters), we can't know exactly which badges a
     * user has earned. We mark badges as "earned" if their threshold is
     * derivable from level/XP alone (identify_count badges use a rough
     * estimate based on XP). For non-derivable badges, we leave them as
     * "locked" — the viewer sees the full catalog but only approximate state.
     */
    fun synthesizeStateForUser(level: Int, xp: Int): AchievementsState {
        // Rough estimate: assume ~60% of XP comes from identifications at 10 XP each.
        val estimatedIdentifyCount = (xp * 0.6 / 10).toInt()
        val estimatedCaptureCount = (xp * 0.25 / XpSource.CAPTURE.xp).toInt()
        val estimatedBadges = estimatedIdentifyCount / 20  // rough: 1 badge per 20 IDs
        return AchievementsState(
            totalXp = xp,
            identifyCount = estimatedIdentifyCount,
            captureCount = estimatedCaptureCount,
            earnedBadgeIds = badgeCatalog.filter { badge ->
                when (badge.requirement.type) {
                    "identify_count" -> estimatedIdentifyCount >= badge.requirement.threshold
                    "capture_count" -> estimatedCaptureCount >= badge.requirement.threshold
                    else -> false  // Can't derive from XP alone — show as locked
                }
            }.map { it.id }.toSet(),
        )
    }
}

/** Result of an [AchievementsRepository.award] call — drives the celebratory overlay. */
data class AwardResult(
    val xpGained: Int,
    val newTotalXp: Int,
    val newLevel: Int,
    val leveledUp: Boolean,
    val newBadges: List<Badge>,
    val celebration: Celebration?,
    val achievementCelebration: AchievementCelebrationData? = null,
    val badgeCelebration: BadgeCelebrationData? = null,
    val newAchievements: List<com.rork.rockscout.data.Achievement> = emptyList(),
)

/** Data for the badge-earn celebratory overlay (its own channel, parallel to achievement popups). */
data class BadgeCelebrationData(
    val badgeId: String,
    val name: String,
    val emoji: String,
    val description: String,
)

/** Data for the achievement-completion celebratory overlay. */
data class AchievementCelebrationData(
    val achievementId: String,
    val name: String,
    val emoji: String,
    val rewardXp: Int,
    val description: String,
)

data class Celebration(
    val type: String,        // "level_up" | "badge_earned"
    val detail: String,      // e.g. "Level 8 — Collector" or "Agate Hunter"
    val emoji: String,
)

package com.rork.rockscout.data

import kotlinx.serialization.Serializable

/**
 * A trackable achievement with a numeric progress meter.
 *
 * Each achievement has a [threshold] (the goal count) and a [rewardXp] bonus
 * granted when the player reaches the threshold. The UI renders a progress bar
 * for every achievement inside the level card, and a small XP meter showing
 * how much XP that achievement is worth.
 *
 * Progress is derived from the current [AchievementsState] counters by
 * [progressFor] — the value returned is clamped to 0..threshold.
 */
@Serializable
data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String,
    /** Goal count — the action count needed to complete this achievement. */
    val threshold: Int,
    /** XP bonus granted on completion (separate from per-action XP). */
    val rewardXp: Int,
    /** Which aggregate counter drives this achievement's progress. */
    val metric: AchievementMetric,
    /** Optional family tag for family-completion achievements. */
    val familyTag: String = "",
)

/** The aggregate counter that an [Achievement] tracks. */
enum class AchievementMetric {
    IDENTIFY,
    CAPTURE,
    WISHLIST_HIT,
    DIG_SITE_CHECKIN,
    JOURNAL_ENTRY,
    TRADE,
    REFERRAL,
    PREMIUM_REFERRAL,
    STREAK,
    COLLECTION_SIZE,
    WISHLIST_SIZE,
    FAVORITE_SPOTS,
    LEVEL,
    FAMILY_COMPLETE,
    TRIPS,
}

/**
 * The full 100-achievement catalog. Ordered by tier (easier first) so the list
 * inside the level card reads as a natural progression path.
 *
 * Each achievement is independently trackable: progress is the current value of
 * its [AchievementMetric] clamped to [Achievement.threshold]. When progress
 * reaches the threshold the achievement is "earned" and the reward XP is
 * credited (one-time) by [AchievementsRepository].
 */
object AchievementCatalog {

    val all: List<Achievement> = listOf(
        // ── Tier 1: First steps (identify) ──
        Achievement("id_1", "First Rock", "Identify your first rock.", "\uD83E\uDEA3", 1, 10, AchievementMetric.IDENTIFY),
        Achievement("id_5", "Quick Learner", "Identify 5 rocks.", "\uD83D\uDD2C", 5, 25, AchievementMetric.IDENTIFY),
        Achievement("id_10", "First 10", "Identify 10 rocks.", "\uD83C\uDFC6", 10, 50, AchievementMetric.IDENTIFY),
        Achievement("id_25", "Rock Spotter", "Identify 25 rocks.", "\uD83D\uDC8E", 25, 75, AchievementMetric.IDENTIFY),
        Achievement("id_50", "Half Century", "Identify 50 rocks.", "\uD83C\uDFC5", 50, 100, AchievementMetric.IDENTIFY),
        Achievement("id_100", "Century Club", "Identify 100 rocks.", "\uD83C\uDF89", 100, 200, AchievementMetric.IDENTIFY),
        Achievement("id_250", "Rock Scholar", "Identify 250 rocks.", "\uD83D\uDCDA", 250, 400, AchievementMetric.IDENTIFY),
        Achievement("id_500", "Master Identifier", "Identify 500 rocks.", "\uD83C\uDF1F", 500, 800, AchievementMetric.IDENTIFY),
        Achievement("id_1000", "Rock Legend", "Identify 1,000 rocks.", "\uD83D\uDC51", 1000, 1500, AchievementMetric.IDENTIFY),

        // ── Tier 2: Field captures ──
        Achievement("cap_1", "First Capture", "Log your first field capture.", "\uD83D\uDCF7", 1, 15, AchievementMetric.CAPTURE),
        Achievement("cap_5", "Snap Happy", "Log 5 field captures.", "\uD83D\uDCF8", 5, 30, AchievementMetric.CAPTURE),
        Achievement("cap_10", "Field Photographer", "Log 10 field captures.", "\uD83D\uDCDE", 10, 50, AchievementMetric.CAPTURE),
        Achievement("cap_25", "Shutter Bug", "Log 25 field captures.", "\uD83D\uDCF0", 25, 75, AchievementMetric.CAPTURE),
        Achievement("cap_50", "Field Season", "Log 50 field captures.", "\uD83C\uDFD5\uFE0F", 50, 150, AchievementMetric.CAPTURE),
        Achievement("cap_100", "Seasoned Pro", "Log 100 field captures.", "\uD83C\uDFC3", 100, 300, AchievementMetric.CAPTURE),
        Achievement("cap_250", "Archive Keeper", "Log 250 field captures.", "\uD83D\uDCDD", 250, 600, AchievementMetric.CAPTURE),

        // ── Tier 3: Streaks ──
        Achievement("stk_3", "Getting Warmed Up", "3-day identify/capture streak.", "\uD83D\uDD25", 3, 30, AchievementMetric.STREAK),
        Achievement("stk_7", "Week Warrior", "7-day identify/capture streak.", "\uD83D\uDD25", 7, 75, AchievementMetric.STREAK),
        Achievement("stk_14", "Fortnight Hunter", "14-day identify/capture streak.", "\uD83D\uDD25", 14, 150, AchievementMetric.STREAK),
        Achievement("stk_30", "Monthly Grinder", "30-day identify/capture streak.", "\uD83D\uDD25", 30, 300, AchievementMetric.STREAK),
        Achievement("stk_60", "Iron Will", "60-day identify/capture streak.", "\u2693\uFE0F", 60, 500, AchievementMetric.STREAK),
        Achievement("stk_100", "Centurion", "100-day identify/capture streak.", "\uD83C\uDFC6", 100, 1000, AchievementMetric.STREAK),

        // ── Tier 4: Wishlist ──
        Achievement("wl_1", "Wishlist Builder", "Add 1 specimen to your wishlist.", "\uD83D\uDCBC", 1, 10, AchievementMetric.WISHLIST_SIZE),
        Achievement("wl_5", "Dreaming Big", "Add 5 specimens to your wishlist.", "\uD83D\uDCBC", 5, 25, AchievementMetric.WISHLIST_SIZE),
        Achievement("wl_10", "Wishlist Hunter", "Add 10 specimens to your wishlist.", "\uD83D\uDCBC", 10, 50, AchievementMetric.WISHLIST_SIZE),
        Achievement("wl_25", "Serious Collector", "Add 25 specimens to your wishlist.", "\uD83D\uDCBC", 25, 75, AchievementMetric.WISHLIST_SIZE),
        Achievement("wl_50", "Wishlist Master", "Add 50 specimens to your wishlist.", "\uD83D\uDCBC", 50, 150, AchievementMetric.WISHLIST_SIZE),

        // ── Tier 5: Wishlist hits (specimen found) ──
        Achievement("wh_1", "First Find", "Check 1 specimen off your wishlist.", "\u2705", 1, 25, AchievementMetric.WISHLIST_HIT),
        Achievement("wh_3", "Treasure Hunter", "Check 3 specimens off your wishlist.", "\uD83D\uDDC2\uFE0F", 3, 50, AchievementMetric.WISHLIST_HIT),
        Achievement("wh_5", "Bucket List Buster", "Check 5 specimens off your wishlist.", "\uD83C\uDFAB", 5, 100, AchievementMetric.WISHLIST_HIT),
        Achievement("wh_10", "Dream Chaser", "Check 10 specimens off your wishlist.", "\u2B50", 10, 200, AchievementMetric.WISHLIST_HIT),
        Achievement("wh_25", "Wishlist Conqueror", "Check 25 specimens off your wishlist.", "\uD83C\uDF1F", 25, 500, AchievementMetric.WISHLIST_HIT),

        // ── Tier 6: Collection size ──
        Achievement("col_1", "First Specimen", "Collect 1 specimen.", "\uD83D\uDC8E", 1, 10, AchievementMetric.COLLECTION_SIZE),
        Achievement("col_5", "Small Collection", "Collect 5 specimens.", "\uD83D\uDC8E", 5, 25, AchievementMetric.COLLECTION_SIZE),
        Achievement("col_10", "Double Digits", "Collect 10 specimens.", "\uD83D\uDC8E", 10, 50, AchievementMetric.COLLECTION_SIZE),
        Achievement("col_25", "Rock Collector", "Collect 25 specimens.", "\uD83D\uDC8E", 25, 75, AchievementMetric.COLLECTION_SIZE),
        Achievement("col_50", "Serious Hound", "Collect 50 specimens.", "\uD83D\uDC8E", 50, 150, AchievementMetric.COLLECTION_SIZE),
        Achievement("col_100", "Centurion Collector", "Collect 100 specimens.", "\uD83C\uDFC5", 100, 300, AchievementMetric.COLLECTION_SIZE),
        Achievement("col_250", "Museum Curator", "Collect 250 specimens.", "\uD83C\uDFDB\uFE0F", 250, 600, AchievementMetric.COLLECTION_SIZE),

        // ── Tier 7: Dig site check-ins ──
        Achievement("dig_1", "First Dig", "Check in at 1 dig site.", "\u26CF\uFE0F", 1, 10, AchievementMetric.DIG_SITE_CHECKIN),
        Achievement("dig_5", "Site Hopper", "Check in at 5 dig sites.", "\u26CF\uFE0F", 5, 25, AchievementMetric.DIG_SITE_CHECKIN),
        Achievement("dig_10", "Road Tripper", "Check in at 10 dig sites.", "\u26CF\uFE0F", 10, 50, AchievementMetric.DIG_SITE_CHECKIN),
        Achievement("dig_25", "Globe Trotter", "Check in at 25 dig sites.", "\uD83C\uDF0D", 25, 100, AchievementMetric.DIG_SITE_CHECKIN),
        Achievement("dig_50", "Site Veteran", "Check in at 50 dig sites.", "\uD83C\uDFDE\uFE0F", 50, 250, AchievementMetric.DIG_SITE_CHECKIN),

        // ── Tier 8: Journal entries ──
        Achievement("jr_1", "First Entry", "Write your first journal entry.", "\uD83D\uDCD6", 1, 10, AchievementMetric.JOURNAL_ENTRY),
        Achievement("jr_5", "Storyteller", "Write 5 journal entries.", "\uD83D\uDCD6", 5, 25, AchievementMetric.JOURNAL_ENTRY),
        Achievement("jr_10", "Field Chronicler", "Write 10 journal entries.", "\u270D\uFE0F", 10, 50, AchievementMetric.JOURNAL_ENTRY),
        Achievement("jr_25", "Rock Diarist", "Write 25 journal entries.", "\uD83D\uDCD8", 25, 100, AchievementMetric.JOURNAL_ENTRY),
        Achievement("jr_50", "Memoirist", "Write 50 journal entries.", "\uD83D\uDCD8", 50, 200, AchievementMetric.JOURNAL_ENTRY),

        // ── Tier 9: Trades ──
        Achievement("tr_1", "First Trade", "Complete your first specimen trade.", "\uD83D\uDD04", 1, 30, AchievementMetric.TRADE),
        Achievement("tr_3", "Trading Up", "Complete 3 specimen trades.", "\uD83D\uDD04", 3, 75, AchievementMetric.TRADE),
        Achievement("tr_5", "Wheel & Deal", "Complete 5 specimen trades.", "\uD83D\uDD04", 5, 125, AchievementMetric.TRADE),
        Achievement("tr_10", "Trade Baron", "Complete 10 specimen trades.", "\uD83C\uDFB4", 10, 250, AchievementMetric.TRADE),
        Achievement("tr_25", "Rock Exchange", "Complete 25 specimen trades.", "\uD83C\uDFB4", 25, 500, AchievementMetric.TRADE),

        // ── Tier 10: Referrals ──
        Achievement("ref_1", "Recruiter", "Enlist your first RockScout.", "\uD83C\uDF1F", 1, 50, AchievementMetric.REFERRAL),
        Achievement("ref_3", "Squad Leader", "Enlist 3 RockScouts.", "\uD83C\uDF1F", 3, 100, AchievementMetric.REFERRAL),
        Achievement("ref_5", "Community Builder", "Enlist 5 RockScouts.", "\uD83D\uDC65", 5, 200, AchievementMetric.REFERRAL),
        Achievement("ref_10", "RockScout Ambassador", "Enlist 10 RockScouts.", "\uD83C\uDF1F", 10, 400, AchievementMetric.REFERRAL),
        Achievement("ref_25", "Movement Starter", "Enlist 25 RockScouts.", "\uD83C\uDF1F", 25, 1000, AchievementMetric.REFERRAL),

        // ── Tier 11: Favorite spots ──
        Achievement("fav_1", "First Spot", "Save 1 favorite dig spot.", "\uD83D\uDCCC", 1, 10, AchievementMetric.FAVORITE_SPOTS),
        Achievement("fav_5", "Spot Saver", "Save 5 favorite dig spots.", "\uD83D\uDCCC", 5, 25, AchievementMetric.FAVORITE_SPOTS),
        Achievement("fav_10", "Map Maker", "Save 10 favorite dig spots.", "\uD83D\uDDFA\uFE0F", 10, 50, AchievementMetric.FAVORITE_SPOTS),
        Achievement("fav_25", "Atlas Builder", "Save 25 favorite dig spots.", "\uD83D\uDDFA\uFE0F", 25, 100, AchievementMetric.FAVORITE_SPOTS),
        Achievement("fav_50", "Cartographer", "Save 50 favorite dig spots.", "\uD83D\uDDFA\uFE0F", 50, 200, AchievementMetric.FAVORITE_SPOTS),

        // ── Tier 12: Trip planner ──
        Achievement("trip_1", "First Trip", "Plan 1 hunting trip.", "\uD83D\uDDFA\uFE0F", 1, 15, AchievementMetric.TRIPS),
        Achievement("trip_5", "Trip Planner", "Plan 5 hunting trips.", "\uD83D\uDDFA\uFE0F", 5, 40, AchievementMetric.TRIPS),
        Achievement("trip_10", "Expedition Leader", "Plan 10 hunting trips.", "\uD83C\uDFD5\uFE0F", 10, 80, AchievementMetric.TRIPS),
        Achievement("trip_25", "World Explorer", "Plan 25 hunting trips.", "\uD83C\uDFD5\uFE0F", 25, 200, AchievementMetric.TRIPS),

        // ── Tier 13: Level milestones ──
        Achievement("lvl_5", "Rising Rockhound", "Reach level 5.", "\uD83D\uDD6E", 5, 50, AchievementMetric.LEVEL),
        Achievement("lvl_10", "Collector", "Reach level 10.", "\uD83D\uDD6E", 10, 100, AchievementMetric.LEVEL),
        Achievement("lvl_15", "Field Hunter", "Reach level 15.", "\u2692\uFE0F", 15, 150, AchievementMetric.LEVEL),
        Achievement("lvl_20", "Senior RockScout", "Reach level 20.", "\uD83D\uDD6E", 20, 200, AchievementMetric.LEVEL),
        Achievement("lvl_25", "Expert RockScout", "Reach level 25.", "\uD83D\uDD6F\uFE0F", 25, 300, AchievementMetric.LEVEL),
        Achievement("lvl_30", "Master RockScout", "Reach level 30.", "\uD83C\uDF1F", 30, 500, AchievementMetric.LEVEL),
        Achievement("lvl_40", "Grandmaster", "Reach level 40.", "\uD83C\uDF1F", 40, 800, AchievementMetric.LEVEL),

        // ── Tier 14: Extended identify milestones ──
        Achievement("id_150", "Rock Connoisseur", "Identify 150 rocks.", "\uD83D\uDCD0", 150, 250, AchievementMetric.IDENTIFY),
        Achievement("id_750", "Rock Sage", "Identify 750 rocks.", "\uD83D\uDD6E\uFE0F", 750, 1200, AchievementMetric.IDENTIFY),
        Achievement("id_2000", "Rock Oracle", "Identify 2,000 rocks.", "\uD83D\uDC51", 2000, 2500, AchievementMetric.IDENTIFY),

        // ── Tier 15: Extended field capture milestones ──
        Achievement("cap_150", "Field Documenter", "Log 150 field captures.", "\uD83D\uDCF0", 150, 400, AchievementMetric.CAPTURE),
        Achievement("cap_500", "Field Archive Master", "Log 500 field captures.", "\uD83D\uDCDE", 500, 1000, AchievementMetric.CAPTURE),

        // ── Tier 16: Extended streak milestones ──
        Achievement("stk_150", "Dedicated Hunter", "150-day identify/capture streak.", "\uD83D\uDD25", 150, 800, AchievementMetric.STREAK),
        Achievement("stk_200", "Unbreakable", "200-day identify/capture streak.", "\uD83D\uDD25", 200, 1500, AchievementMetric.STREAK),

        // ── Tier 17: Extended collection milestones ──
        Achievement("col_150", "Grand Collector", "Collect 150 specimens.", "\uD83D\uDC8E", 150, 400, AchievementMetric.COLLECTION_SIZE),
        Achievement("col_350", "Exhibition Curator", "Collect 350 specimens.", "\uD83C\uDFEC", 350, 1000, AchievementMetric.COLLECTION_SIZE),
        Achievement("col_500", "Museum Founder", "Collect 500 specimens.", "\uD83C\uDFDB\uFE0F", 500, 1500, AchievementMetric.COLLECTION_SIZE),

        // ── Tier 18: Extended dig site milestones ──
        Achievement("dig_75", "Dig Master", "Check in at 75 dig sites.", "\u26CF\uFE0F", 75, 400, AchievementMetric.DIG_SITE_CHECKIN),
        Achievement("dig_100", "Pilgrim", "Check in at 100 dig sites.", "\uD83C\uDFDE\uFE0F", 100, 600, AchievementMetric.DIG_SITE_CHECKIN),

        // ── Tier 19: Extended journal milestones ──
        Achievement("jr_75", "Field Historian", "Write 75 journal entries.", "\uD83D\uDCD8", 75, 300, AchievementMetric.JOURNAL_ENTRY),
        Achievement("jr_100", "Rock Librarian", "Write 100 journal entries.", "\uD83D\uDCD8", 100, 400, AchievementMetric.JOURNAL_ENTRY),

        // ── Tier 20: Extended trade milestones ──
        Achievement("tr_50", "Trade Magnate", "Complete 50 specimen trades.", "\uD83C\uDFB4", 50, 800, AchievementMetric.TRADE),

        // ── Tier 21: Extended referral milestones ──
        Achievement("ref_50", "RockScout Pioneer", "Enlist 50 RockScouts.", "\uD83C\uDF1F", 50, 1500, AchievementMetric.REFERRAL),

        // ── Tier 22: Premium referral milestones ──
        Achievement("pre_ref_1", "Premium Recruiter", "Complete 1 premium referral (500 XP).", "\uD83D\uDCB0", 1, 100, AchievementMetric.PREMIUM_REFERRAL),
        Achievement("pre_ref_5", "Premium Squad", "Complete 5 premium referrals.", "\uD83D\uDCB0", 5, 300, AchievementMetric.PREMIUM_REFERRAL),
        Achievement("pre_ref_10", "Premium Ambassador", "Complete 10 premium referrals.", "\uD83D\uDCB0", 10, 600, AchievementMetric.PREMIUM_REFERRAL),

        // ── Tier 23: Extended favorite spots milestones ──
        Achievement("fav_75", "Trail Blazer", "Save 75 favorite dig spots.", "\uD83D\uDDFA\uFE0F", 75, 300, AchievementMetric.FAVORITE_SPOTS),
        Achievement("fav_100", "Master Cartographer", "Save 100 favorite dig spots.", "\uD83D\uDDFA\uFE0F", 100, 500, AchievementMetric.FAVORITE_SPOTS),

        // ── Tier 24: Extended trip milestones ──
        Achievement("trip_50", "Globetrotter", "Plan 50 hunting trips.", "\uD83C\uDFD5\uFE0F", 50, 400, AchievementMetric.TRIPS),

        // ── Tier 25: Extended wishlist milestones ──
        Achievement("wl_100", "Dream Collector", "Add 100 specimens to your wishlist.", "\uD83D\uDCBC", 100, 250, AchievementMetric.WISHLIST_SIZE),
        Achievement("wh_50", "Wishlist Legend", "Check 50 specimens off your wishlist.", "\u2B50", 50, 800, AchievementMetric.WISHLIST_HIT),

        // ── Tier 26: Extended level milestones ──
        Achievement("lvl_35", "Crystal Hunter", "Reach level 35.", "\uD83D\uDD6F\uFE0F", 35, 600, AchievementMetric.LEVEL),
        Achievement("lvl_50", "Diamond Hound", "Reach level 50.", "\uD83D\uDD6E", 50, 800, AchievementMetric.LEVEL),
    )

    /** Total count (101 achievements). */
    val size: Int get() = all.size

    /** Lookup by id. */
    fun byId(id: String): Achievement? = all.firstOrNull { it.id == id }

    /**
     * Current progress value for [achievement] given the current [state] and
     * derived [level]. Clamped to 0..threshold.
     */
    fun progressFor(achievement: Achievement, state: AchievementsState, level: Int): Int {
        val raw = when (achievement.metric) {
            AchievementMetric.IDENTIFY -> state.identifyCount
            AchievementMetric.CAPTURE -> state.captureCount
            AchievementMetric.WISHLIST_HIT -> state.wishlistHitCount
            AchievementMetric.DIG_SITE_CHECKIN -> state.digSiteCheckInCount
            AchievementMetric.JOURNAL_ENTRY -> state.journalEntryCount
            AchievementMetric.TRADE -> state.tradeCount
            AchievementMetric.REFERRAL -> state.referralCount
            AchievementMetric.PREMIUM_REFERRAL -> state.premiumReferralCount
            AchievementMetric.STREAK -> state.longestStreak
            AchievementMetric.COLLECTION_SIZE -> AppRepository.instance.collection.value.size
            AchievementMetric.WISHLIST_SIZE -> AppRepository.instance.wishlist.value.size
            AchievementMetric.FAVORITE_SPOTS -> AppRepository.instance.favoriteSpots.value.size
            AchievementMetric.LEVEL -> level
            AchievementMetric.TRIPS -> AppRepository.instance.trips.value.size
            AchievementMetric.FAMILY_COMPLETE -> 0 // not tracked as a single counter
        }
        return raw.coerceIn(0, achievement.threshold)
    }

    /** True when the achievement's threshold has been met. */
    fun isEarned(achievement: Achievement, state: AchievementsState, level: Int): Boolean =
        progressFor(achievement, state, level) >= achievement.threshold
}

package com.rork.rockscout.data

import android.util.Log
import kotlinx.serialization.Serializable

/**
 * Seeds the local database with a believable community of mock RockScout users
 * so the social features (scan, friends, messages, posts, trade board) work
 * end-to-end without a backend.
 *
 * Seeded once on first launch (guarded by [LocalDataStore.KEY_MOCK_SEEDED]).
 * The signed-in user is NOT created here — that happens via [AuthRepository]
 * sign-up/sign-in. Instead, these are the "other users" the signed-in user
 * can discover, befriend, message, and trade with.
 *
 * Mock users have realistic rockhounding profiles, statuses, coarse
 * locations (spread across the USA), and a few have posts + active trade
 * listings so the feeds feel populated.
 */
object MockDataSeeder {

    private const val TAG = "MockDataSeeder"

    @Serializable
    data class LocalConnection(
        val id: String,
        val user_a: String,
        val user_b: String,
        val created_at: String,
    )

    @Serializable
    data class LocalMessageRequest(
        val id: String,
        val sender_id: String,
        val recipient_id: String,
        val body: String,
        val status: String,
        val created_at: String,
        val responded_at: String? = null,
    )

    @Serializable
    data class LocalFriendRequest(
        val id: String,
        val sender_id: String,
        val recipient_id: String,
        val status: String,
        val created_at: String,
        val responded_at: String? = null,
    )

    @Serializable
    data class LocalBlock(
        val id: String,
        val blocker_id: String,
        val blocked_id: String,
        val created_at: String,
    )

    @Serializable
    data class LocalThread(
        val id: String,
        val user_a: String,
        val user_b: String,
        val last_message_at: String,
        val created_at: String,
    )

    @Serializable
    data class LocalMessage(
        val id: String,
        val thread_id: String,
        val sender_id: String,
        val body: String,
        val image_uri: String? = null,
        val read_at: String? = null,
        val created_at: String,
    )

    @Serializable
    data class LocalPost(
        val id: String,
        val user_id: String,
        val source_type: String,
        val source_ref_id: String? = null,
        val title: String,
        val tagline: String = "",
        val image_uri: String? = null,
        val caption: String = "",
        val location_text: String = "",
        val created_at: String,
        val archived: Boolean = false,
    )

    @Serializable
    data class LocalPostLike(
        val post_id: String,
        val user_id: String,
        val created_at: String,
    )

    @Serializable
    data class LocalPostComment(
        val id: String,
        val post_id: String,
        val user_id: String,
        val body: String,
        val parent_comment_id: String? = null,
        val created_at: String,
        val image_uri: String? = null,
    )

    @Serializable
    data class LocalPostCommentLike(
        val comment_id: String,
        val user_id: String,
        val created_at: String,
    )

    @Serializable
    data class LocalCommunityPost(
        val id: String,
        val user_id: String,
        val title: String,
        val tagline: String = "",
        val image_uri: String? = null,
        val description: String = "",
        val location_text: String = "",
        val created_at: String,
        val expires_at: String,
        val expired: Boolean = false,
        /** Category: "identification", "location_tips", or "general". Defaults to general for legacy rows. */
        val category: String = "general",
    )

    @Serializable
    data class LocalCommunityPostLike(
        val post_id: String,
        val user_id: String,
        val created_at: String,
    )

    @Serializable
    data class LocalCommunityPostComment(
        val id: String,
        val post_id: String,
        val user_id: String,
        val body: String,
        val parent_comment_id: String? = null,
        val created_at: String,
        val image_uri: String? = null,
    )

    @Serializable
    data class LocalCommunityPostCommentLike(
        val comment_id: String,
        val user_id: String,
        val created_at: String,
    )

    @Serializable
    data class LocalNotification(
        val id: String,
        val user_id: String,
        val type: String,
        val actor_id: String? = null,
        val ref_id: String? = null,
        val body: String,
        val read_at: String? = null,
        val created_at: String,
        val deep_link_target: String? = null,
    )

    @Serializable
    data class LocalTradeInterest(
        val id: String,
        val listing_id: String,
        val listing_owner_id: String,
        val interested_user_id: String,
        val message: String = "",
        val created_at: String,
    )

    @Serializable
    data class LocalUserReport(
        val id: String,
        val reporter_id: String,
        val reported_user_id: String,
        val reason: String? = null,
        val status: String,
        val created_at: Long,
        val screenshotPath: String? = null,
        val reporter_name: String? = null,
        val reported_name: String? = null,
        val reported_avatar: String? = null,
    )

    @Serializable
    data class LocalListLike(
        val target_user_id: String,   // the user whose collection/wishlist entry is liked
        val specimen_id: String,      // the specimen that was liked
        val list_type: String,        // "collection" or "wishlist"
        val liker_id: String,         // the user who liked it
        val created_at: String,
    )

    @Serializable
    data class LocalImageReview(
        val id: String,
        val user_id: String,
        val user_name: String?,
        val user_avatar: String?,
        val image_uri: String,
        val type: String,          // "profile_background" | "field_capture"
        val capture_id: String?,   // only for field_capture type
        val reason: String,        // AI-flagged reason
        val status: String,        // "pending" | "approved" | "deleted"
        val created_at: Long,
    )

    @Serializable
    data class LocalAppeal(
        val id: String,
        val user_id: String,
        val type: String,          // "image_rejected" | "report_ban"
        val ref_id: String? = null,
        val reason: String,
        val image_uri: String? = null,
        val status: String,        // "pending" | "approved" | "denied"
        val created_at: Long,
    )

    @Serializable
    data class LocalPing(
        val id: String,
        val user_id: String,
        val lat: Double,
        val lng: Double,
        val label: String,
        val expires_at: String,
        val created_at: String,
    )

    @Serializable
    data class LocalPublicTradeListing(
        val id: String,
        val type: String,
        val listingMode: String = "swap",
        val price: String = "",
        val specimenName: String,
        val condition: String = "",
        val description: String = "",
        val wantInReturn: String = "",
        val photoUri: String? = null,
        val tags: List<String> = emptyList(),
        val status: String = "active",
        val ownerUserId: String,
        val ownerName: String,
        val ownerAvatar: String,
        val createdAt: Long,
        val expiresAt: Long,
    )

    /** Seed mock data if not already done. Safe to call on every app start. */
    fun seedIfNeeded() {
        if (!LocalDataStore.isInitialized()) return
        if (LocalDataStore.getBoolean(LocalDataStore.KEY_MOCK_SEEDED)) {
            // Still ensure empty tables exist so repositories don't NPE.
            ensureTablesExist()
            return
        }
        seed()
        LocalDataStore.setBoolean(LocalDataStore.KEY_MOCK_SEEDED, true)
    }

    /** Ensure all tables have at least an empty list stored, so getTable
     *  never returns unexpected nulls and repository logic is consistent. */
    private fun ensureTablesExist() {
        ensureTable(LocalDataStore.KEY_USERS, emptyList<LocalUser>())
        ensureTable(LocalDataStore.KEY_CONNECTIONS, emptyList<LocalConnection>())
        ensureTable(LocalDataStore.KEY_MESSAGE_REQUESTS, emptyList<LocalMessageRequest>())
        ensureTable(LocalDataStore.KEY_FRIEND_REQUESTS, emptyList<LocalFriendRequest>())
        ensureTable(LocalDataStore.KEY_BLOCKS, emptyList<LocalBlock>())
        ensureTable(LocalDataStore.KEY_THREADS, emptyList<LocalThread>())
        ensureTable(LocalDataStore.KEY_MESSAGES, emptyList<LocalMessage>())
        ensureTable(LocalDataStore.KEY_POSTS, emptyList<LocalPost>())
        ensureTable(LocalDataStore.KEY_POST_LIKES, emptyList<LocalPostLike>())
        ensureTable(LocalDataStore.KEY_POST_COMMENTS, emptyList<LocalPostComment>())
        ensureTable(LocalDataStore.KEY_POST_COMMENT_LIKES, emptyList<LocalPostCommentLike>())
        ensureTable(LocalDataStore.KEY_COMMUNITY_POSTS, emptyList<LocalCommunityPost>())
        ensureTable(LocalDataStore.KEY_COMMUNITY_POST_LIKES, emptyList<LocalCommunityPostLike>())
        ensureTable(LocalDataStore.KEY_COMMUNITY_POST_COMMENTS, emptyList<LocalCommunityPostComment>())
        ensureTable(LocalDataStore.KEY_COMMUNITY_POST_COMMENT_LIKES, emptyList<LocalCommunityPostCommentLike>())
        ensureTable(LocalDataStore.KEY_NOTIFICATIONS, emptyList<LocalNotification>())
        ensureTable(LocalDataStore.KEY_TRADE_INTERESTS, emptyList<LocalTradeInterest>())
        ensureTable(LocalDataStore.KEY_USER_REPORTS, emptyList<LocalUserReport>())
        ensureTable(LocalDataStore.KEY_PINGS, emptyList<LocalPing>())
        ensureTable(LocalDataStore.KEY_PUBLIC_TRADE_LISTINGS, emptyList<LocalPublicTradeListing>())
        ensureTable(LocalDataStore.KEY_LIST_LIKES, emptyList<LocalListLike>())
        ensureTable(LocalDataStore.KEY_IMAGE_REVIEWS, emptyList<LocalImageReview>())
        ensureTable(LocalDataStore.KEY_APPEALS, emptyList<LocalAppeal>())
    }

    private inline fun <reified T> ensureTable(key: String, default: List<T>) {
        if (LocalDataStore.getString(key) == null) {
            LocalDataStore.setTable(key, default)
        }
    }

    private fun seed() {
        Log.d(TAG, "Seeding mock community data…")

        // ---- Mock users (spread across US rockhounding hotspots) -----------
        val users = listOf(
            LocalUser(
                id = "mock-amber-1",
                email = "amber@example.com",
                display_name = "Amber Quartz",
                avatar_emoji = "\uD83D\uDC8E",
                status = "on-the-hunt",
                level = 12,
                xp = 3400,
                is_premium = true,
                premium_badge = true,
                coarse_lat = 34.51,
                coarse_lng = -93.63,
                bio = "Crystal collector based in Arkansas. Always hunting for clear quartz points.",
                home_region = "Arkansas, USA",
                club_enabled = true,
                scan_radius_miles = 50,
            ),
            LocalUser(
                id = "mock-granite-2",
                email = "granite@example.com",
                display_name = "Granite Pete",
                avatar_emoji = "\u26CF\uFE0F",
                status = "looking-for-trades",
                level = 8,
                xp = 2100,
                is_premium = false,
                coarse_lat = 44.98,
                coarse_lng = -93.27,
                bio = "Minnesota agate hunter. Trade-friendly!",
                home_region = "Minnesota, USA",
                club_enabled = true,
                scan_radius_miles = 100,
            ),
            LocalUser(
                id = "mock-jade-3",
                email = "jade@example.com",
                display_name = "Jade River",
                avatar_emoji = "\uD83D\uDC8E",
                status = "on-the-hunt",
                level = 15,
                xp = 5200,
                is_premium = true,
                premium_badge = true,
                coarse_lat = 47.61,
                coarse_lng = -122.33,
                bio = "Pacific NW jade and fossils.",
                home_region = "Washington, USA",
                club_enabled = true,
            ),
            LocalUser(
                id = "mock-copper-4",
                email = "copper@example.com",
                display_name = "Copper Canyon",
                avatar_emoji = "\uD83D\uDD04",
                status = "wishing",
                level = 5,
                xp = 900,
                is_premium = false,
                coarse_lat = 46.79,
                coarse_lng = -92.10,
                bio = "Lake Superior agates and copper.",
                home_region = "Michigan, USA",
                club_enabled = true,
            ),
            LocalUser(
                id = "mock-garnet-5",
                email = "garnet@example.com",
                display_name = "Garnet Gale",
                avatar_emoji = "\uD83D\uDC53",
                status = "on-the-hunt",
                level = 20,
                xp = 8100,
                is_premium = true,
                premium_badge = true,
                coarse_lat = 43.07,
                coarse_lng = -109.08,
                bio = "Wyoming jade & garnet prospector.",
                home_region = "Wyoming, USA",
                club_enabled = true,
            ),
            LocalUser(
                id = "mock-topaz-6",
                email = "topaz@example.com",
                display_name = "Topaz Tess",
                avatar_emoji = "\uD83D\uDC8E",
                status = "looking-for-trades",
                level = 10,
                xp = 2800,
                is_premium = false,
                coarse_lat = 39.75,
                coarse_lng = -104.87,
                bio = "Colorado topaz and aquamarine.",
                home_region = "Colorado, USA",
                club_enabled = true,
            ),
            LocalUser(
                id = "mock-obsidian-7",
                email = "obsidian@example.com",
                display_name = "Obsidian Ollie",
                avatar_emoji = "\uD83D\uDDFF",
                status = "off",
                level = 3,
                xp = 300,
                is_premium = false,
                coarse_lat = 38.90,
                coarse_lng = -119.80,
                bio = "Volcanic glass fan. California deserts.",
                home_region = "California, USA",
                club_enabled = false,
            ),
            LocalUser(
                id = "mock-fossil-8",
                email = "fossil@example.com",
                display_name = "Fossil Finder",
                avatar_emoji = "\uD83E\uDDA5",
                status = "on-the-hunt",
                level = 18,
                xp = 6500,
                is_premium = true,
                premium_badge = true,
                coarse_lat = 36.17,
                coarse_lng = -86.78,
                bio = "Tennessee creek fossils — crinoids & trilobites.",
                home_region = "Tennessee, USA",
                club_enabled = true,
            ),
            LocalUser(
                id = "mock-sapphire-9",
                email = "sapphire@example.com",
                display_name = "Sapphire Sam",
                avatar_emoji = "\uD83D\uDC8E",
                status = "wishing",
                level = 7,
                xp = 1500,
                is_premium = false,
                coarse_lat = 45.80,
                coarse_lng = -108.50,
                bio = "Montana sapphire gravel miner.",
                home_region = "Montana, USA",
                club_enabled = true,
            ),
            LocalUser(
                id = "mock-turquoise-10",
                email = "turquoise@example.com",
                display_name = "Turquoise Tara",
                avatar_emoji = "\uD83D\uDC8E",
                status = "looking-for-trades",
                level = 14,
                xp = 4900,
                is_premium = true,
                premium_badge = true,
                coarse_lat = 35.31,
                coarse_lng = -108.67,
                bio = "New Mexico turquoise & geodes.",
                home_region = "New Mexico, USA",
                club_enabled = true,
            ),
        )
        // Assign each mock user a small public collection, wishlist, and favorite
        // spots so other hunters can see their rock counts and tap to view them.
        val collectionWishlist = mapOf(
            "mock-amber-1" to (listOf("quartz", "amethyst", "pyrite") to listOf("diamond", "opal", "malachite")),
            "mock-granite-2" to (listOf("agate-brazilian", "agate-thunderegg") to listOf("quartz", "sapphire", "opal")),
            "mock-jade-3" to (listOf("jade", "labradorite", "quartz") to listOf("nephrite", "amazonite", "agate-brazilian")),
            "mock-copper-4" to (listOf("copper", "chalcopyrite") to listOf("silver", "gold", "pallasite")),
            "mock-garnet-5" to (listOf("garnet", "garnet-almandine") to listOf("ruby", "sapphire", "emerald")),
            "mock-topaz-6" to (listOf("topaz", "aquamarine") to listOf("turquoise", "opal-mexican-fire")),
            "mock-obsidian-7" to (listOf("obsidian") to listOf("opal", "fire-agate")),
            "mock-fossil-8" to (listOf("trilobite", "crinoid", "ammonite") to listOf("dinosaur-bone", "petrified-wood")),
            "mock-sapphire-9" to (listOf("sapphire") to listOf("garnet", "spinel")),
            "mock-turquoise-10" to (listOf("turquoise", "turquoise-kingman") to listOf("chrysocolla", "azurite")),
        )
        val favoriteSpots = mapOf(
            "mock-amber-1" to listOf("crater-of-diamonds", "wegner-quartz"),
            "mock-granite-2" to listOf("wegner-quartz", "crater-of-diamonds"),
            "mock-jade-3" to listOf("wegner-quartz", "crater-of-diamonds"),
            "mock-copper-4" to listOf("crater-of-diamonds", "wegner-quartz"),
            "mock-garnet-5" to listOf("wegner-quartz", "crater-of-diamonds"),
            "mock-topaz-6" to listOf("crater-of-diamonds", "wegner-quartz"),
            "mock-obsidian-7" to listOf("wegner-quartz", "crater-of-diamonds"),
            "mock-fossil-8" to listOf("crater-of-diamonds", "wegner-quartz"),
            "mock-sapphire-9" to listOf("wegner-quartz", "crater-of-diamonds"),
            "mock-turquoise-10" to listOf("crater-of-diamonds", "wegner-quartz"),
        )
        val usersWithCollections = users.map { user ->
            val (c, w) = collectionWishlist[user.id] ?: (emptyList<String>() to emptyList<String>())
            val f = favoriteSpots[user.id] ?: emptyList()
            user.copy(collection_ids = c, wishlist_ids = w, favorite_spot_ids = f)
        }
        LocalDataStore.setTable(LocalDataStore.KEY_USERS, usersWithCollections)

        val now = System.currentTimeMillis()
        val isoNow = java.time.OffsetDateTime.now().toString()

        // ---- Connections (some mock users are already friends with each other) ----
        val connections = listOf(
            LocalConnection("conn-1", "mock-amber-1", "mock-granite-2", isoNow),
            LocalConnection("conn-2", "mock-jade-3", "mock-amber-1", isoNow),
            LocalConnection("conn-3", "mock-garnet-5", "mock-topaz-6", isoNow),
            LocalConnection("conn-4", "mock-fossil-8", "mock-jade-3", isoNow),
        )
        LocalDataStore.setTable(LocalDataStore.KEY_CONNECTIONS, connections)

        // ---- Threads + messages (between connected mock users) ------------
        val threads = listOf(
            LocalThread("thread-1", "mock-amber-1", "mock-granite-2", isoNow, isoNow),
            LocalThread("thread-2", "mock-jade-3", "mock-amber-1", isoNow, isoNow),
            LocalThread("thread-3", "mock-garnet-5", "mock-topaz-6", isoNow, isoNow),
        )
        LocalDataStore.setTable(LocalDataStore.KEY_THREADS, threads)

        val messages = listOf(
            LocalMessage("msg-1", "thread-1", "mock-amber-1", "Hey Granite Pete! Got any good Lake Superior agates this season?", read_at = isoNow, created_at = isoNow),
            LocalMessage("msg-2", "thread-1", "mock-granite-2", "Found a few nice ones last weekend! Want to trade for some quartz points?", read_at = isoNow, created_at = isoNow),
            LocalMessage("msg-3", "thread-2", "mock-jade-3", "Amber, that geode you posted is stunning!", read_at = null, created_at = isoNow),
            LocalMessage("msg-4", "thread-3", "mock-topaz-6", "Garnet, are you heading to the Denver gem show?", read_at = null, created_at = isoNow),
        )
        LocalDataStore.setTable(LocalDataStore.KEY_MESSAGES, messages)

        // ---- Posts (some mock users have profile posts) -------------------
        val posts = listOf(
            LocalPost("post-1", "mock-amber-1", "capture", null, "Clear Quartz Cluster", "Mount Ida, Arkansas", null, "Six perfectly clear points in a cluster!", "Mount Ida, AR", isoNow),
            LocalPost("post-2", "mock-amber-1", "collection", null, "Pyrite Cube", "Spain", null, "Natural perfect cube from Spain.", "Spain (trade)", isoNow),
            LocalPost("post-3", "mock-granite-2", "capture", null, "Lake Superior Agate", "Minnesota North Shore", null, "Beautiful banded agate, classic MN find.", "North Shore, MN", isoNow),
            LocalPost("post-4", "mock-jade-3", "capture", null, "Nephrite Jade Boulder", "Washington Coast", null, "Beach-washed jade from the Pacific.", "Washington Coast", isoNow),
            LocalPost("post-5", "mock-garnet-5", "collection", null, "Wyoming Jade", "Wyoming", null, "Dark green nephrite slab.", "Wyoming", isoNow),
            LocalPost("post-6", "mock-fossil-8", "capture", null, "Crinoid Stem", "Tennessee Creek", null, "Fossilized crinoid stems in limestone.", "Tennessee", isoNow),
            LocalPost("post-7", "mock-topaz-6", "capture", null, "Colorado Topaz", "Mount Antero, CO", null, "Small but gemmy topaz crystal!", "Mount Antero, CO", isoNow),
            LocalPost("post-8", "mock-turquoise-10", "collection", null, "Cerrillos Turquoise", "New Mexico", null, "Blue turquoise nugget from Cerrillos.", "Cerrillos, NM", isoNow),
        )
        LocalDataStore.setTable(LocalDataStore.KEY_POSTS, posts)

        val likes = listOf(
            LocalPostLike("post-1", "mock-granite-2", isoNow),
            LocalPostLike("post-1", "mock-jade-3", isoNow),
            LocalPostLike("post-3", "mock-amber-1", isoNow),
            LocalPostLike("post-4", "mock-amber-1", isoNow),
            LocalPostLike("post-4", "mock-granite-2", isoNow),
            LocalPostLike("post-6", "mock-jade-3", isoNow),
        )
        LocalDataStore.setTable(LocalDataStore.KEY_POST_LIKES, likes)

        val comments = listOf(
            LocalPostComment("cmt-1", "post-1", "mock-granite-2", "Those points are incredible! What dig site?", null, isoNow),
            LocalPostComment("cmt-2", "post-1", "mock-amber-1", "Thanks! Collected at Wegner Quartz mine.", "cmt-1", isoNow),
            LocalPostComment("cmt-3", "post-4", "mock-granite-2", "I've always wanted to find beach jade!", null, isoNow),
        )
        LocalDataStore.setTable(LocalDataStore.KEY_POST_COMMENTS, comments)

        // ---- Community posts (sample Q&A / finds from mock users) -----------
        val dayMsComm = 24L * 60 * 60 * 1000
        val nowComm = System.currentTimeMillis()
        val expiryMs = nowComm + 14 * dayMsComm
        val isoExpiry = java.time.OffsetDateTime
            .now().plusDays(14).toString()
        val isoTenDaysAgo = java.time.OffsetDateTime
            .now().minusDays(10).toString()
        val isoFiveDaysAgo = java.time.OffsetDateTime
            .now().minusDays(5).toString()
        val isoTwoDaysAgo = java.time.OffsetDateTime
            .now().minusDays(2).toString()
        val isoOneDayAgo = java.time.OffsetDateTime
            .now().minusDays(1).toString()
        val isoSixHoursAgo = java.time.OffsetDateTime
            .now().minusHours(6).toString()
        val communityPosts = listOf(
            LocalCommunityPost(
                id = "cpost-1",
                user_id = "mock-granite-2",
                title = "How do you clean iron-stained quartz?",
                tagline = "Tried oxalic acid but the stains won't budge",
                image_uri = null,
                description = "I've soaked these Arkansas quartz points in oxalic acid for two days and the iron staining is still there. Am I doing something wrong? Should I try a stronger concentration or a different method entirely?",
                location_text = "Mount Ida, AR",
                created_at = isoSixHoursAgo,
                expires_at = isoExpiry,
                category = "general",
            ),
            LocalCommunityPost(
                id = "cpost-2",
                user_id = "mock-amber-1",
                title = "Found this heart-shaped rock on the beach!",
                tagline = "Nature makes the best shapes",
                image_uri = null,
                description = "Came across this naturally heart-shaped stone while walking the shore this morning. No carving — it's entirely natural erosion. Has anyone else found heart-shaped rocks? I've been collecting them for years.",
                location_text = "Lake Superior, MN",
                created_at = isoOneDayAgo,
                expires_at = java.time.OffsetDateTime.now().plusDays(13).toString(),
                category = "general",
            ),
            LocalCommunityPost(
                id = "cpost-3",
                user_id = "mock-jade-3",
                title = "Best public dig sites in the Pacific Northwest?",
                tagline = "Planning a rockhounding road trip",
                image_uri = null,
                description = "Heading to Oregon and Washington next month. Looking for public-access spots where I can collect — agates, jasper, thunder eggs, anything. I know about Richardson's Ranch but want to find some lesser-known spots too.",
                location_text = "Oregon & Washington",
                created_at = isoTwoDaysAgo,
                expires_at = java.time.OffsetDateTime.now().plusDays(12).toString(),
                category = "location_tips",
            ),
            LocalCommunityPost(
                id = "cpost-4",
                user_id = "mock-fossil-8",
                title = "Is this a crinoid or a bryozoan?",
                tagline = "Need help ID-ing this fossil",
                image_uri = null,
                description = "Found this in a limestone creek bed in Tennessee. It has segmented column-like structures but I'm not sure if it's a crinoid stem or bryozoan. The segments seem round and stacked like poker chips. Any paleontology folks able to help?",
                location_text = "Tennessee",
                created_at = isoFiveDaysAgo,
                expires_at = java.time.OffsetDateTime.now().plusDays(9).toString(),
                category = "identification",
            ),
            LocalCommunityPost(
                id = "cpost-5",
                user_id = "mock-garnet-5",
                title = "Wyoming jade — how to tell nephrite from jadeite in the field",
                tagline = "Field ID tips appreciated",
                image_uri = null,
                description = "I've been hunting jade in central Wyoming and keep finding green stones I can't confidently ID. I know nephrite is more common here but want to be sure. What field tests do you use — hardness, streak, specific gravity? Any reliable visual cues?",
                location_text = "Wyoming",
                created_at = isoTenDaysAgo,
                expires_at = java.time.OffsetDateTime.now().plusDays(4).toString(),
                category = "identification",
            ),
        )
        LocalDataStore.setTable(LocalDataStore.KEY_COMMUNITY_POSTS, communityPosts)

        val communityLikes = listOf(
            LocalCommunityPostLike("cpost-1", "mock-amber-1", isoNow),
            LocalCommunityPostLike("cpost-1", "mock-jade-3", isoNow),
            LocalCommunityPostLike("cpost-2", "mock-granite-2", isoNow),
            LocalCommunityPostLike("cpost-2", "mock-jade-3", isoNow),
            LocalCommunityPostLike("cpost-2", "mock-fossil-8", isoNow),
            LocalCommunityPostLike("cpost-3", "mock-amber-1", isoNow),
            LocalCommunityPostLike("cpost-4", "mock-garnet-5", isoNow),
            LocalCommunityPostLike("cpost-5", "mock-topaz-6", isoNow),
        )
        LocalDataStore.setTable(LocalDataStore.KEY_COMMUNITY_POST_LIKES, communityLikes)

        val communityComments = listOf(
            LocalCommunityPostComment("ccmt-1", "cpost-1", "mock-amber-1", "Have you tried heating the acid slightly? Warm (not boiling) oxalic works much faster on stubborn stains.", null, isoNow),
            LocalCommunityPostComment("ccmt-2", "cpost-1", "mock-jade-3", "Iron Buddy from Iron Out is my go-to. Much stronger than oxalic for deep staining.", null, isoNow),
            LocalCommunityPostComment("ccmt-3", "cpost-1", "mock-granite-2", "Thanks! I'll try warming the acid first and pick up some Iron Out.", "ccmt-1", isoNow),
            LocalCommunityPostComment("ccmt-4", "cpost-2", "mock-jade-3", "Those are the best finds! Nature is amazing.", null, isoNow),
            LocalCommunityPostComment("ccmt-5", "cpost-3", "mock-amber-1", "Prineville area has great thundereggs. Also check the Hampton area for agates.", null, isoNow),
            LocalCommunityPostComment("ccmt-6", "cpost-4", "mock-garnet-5", "Sounds like a crinoid — the stacked round segments are classic. Bryozoans tend to have branching or fan-like patterns.", null, isoNow),
        )
        LocalDataStore.setTable(LocalDataStore.KEY_COMMUNITY_POST_COMMENTS, communityComments)

        // ---- Public trade listings (so the board has community listings) ----
        val dayMs = 24L * 60 * 60 * 1000
        val publicListings = listOf(
            LocalPublicTradeListing(
                id = "pub-listing-1",
                type = "HAVE",
                listingMode = "swap",
                specimenName = "Clear Quartz Cluster",
                condition = "Natural cluster",
                description = "Six clear points, Mount Ida Arkansas. About 4 inches tall.",
                wantInReturn = "Looking for Lake Superior agates or pyrite",
                photoUri = null,
                tags = listOf("quartz", "crystal", "arkansas"),
                status = "active",
                ownerUserId = "mock-amber-1",
                ownerName = "Amber Quartz",
                ownerAvatar = "\uD83D\uDC8E",
                createdAt = now - 2 * dayMs,
                expiresAt = now + 12 * dayMs,
            ),
            LocalPublicTradeListing(
                id = "pub-listing-2",
                type = "HAVE",
                listingMode = "sell",
                price = "$40",
                specimenName = "Lake Superior Agate",
                condition = "Tumbled",
                description = "Classic banded agate, great color. ~2 inch.",
                wantInReturn = "",
                photoUri = null,
                tags = listOf("agate", "tumbled", "minnesota"),
                status = "active",
                ownerUserId = "mock-granite-2",
                ownerName = "Granite Pete",
                ownerAvatar = "\u26CF\uFE0F",
                createdAt = now - 5 * dayMs,
                expiresAt = now + 10 * dayMs,
            ),
            LocalPublicTradeListing(
                id = "pub-listing-3",
                type = "HAVE",
                listingMode = "swap",
                specimenName = "Nephrite Jade Boulder",
                condition = "Rough",
                description = "Beach-washed jade boulder from Washington coast. ~3 lbs.",
                wantInReturn = "Looking for good quality garnets or sapphires",
                photoUri = null,
                tags = listOf("jade", "rough", "washington"),
                status = "active",
                ownerUserId = "mock-jade-3",
                ownerName = "Jade River",
                ownerAvatar = "\uD83D\uDC8E",
                createdAt = now - 1 * dayMs,
                expiresAt = now + 13 * dayMs,
            ),
            LocalPublicTradeListing(
                id = "pub-listing-4",
                type = "WANT",
                listingMode = "swap",
                specimenName = "Montana Sapphire",
                condition = "",
                description = "Looking for a nice facetable Montana sapphire, any blue or teal.",
                wantInReturn = "",
                photoUri = null,
                tags = listOf("sapphire", "montana", "want"),
                status = "active",
                ownerUserId = "mock-sapphire-9",
                ownerName = "Sapphire Sam",
                ownerAvatar = "\uD83D\uDC8E",
                createdAt = now - 3 * dayMs,
                expiresAt = now + 11 * dayMs,
            ),
            LocalPublicTradeListing(
                id = "pub-listing-5",
                type = "HAVE",
                listingMode = "sell",
                price = "$75",
                specimenName = "Cerrillos Turquoise Nugget",
                condition = "Rough",
                description = "Natural blue turquoise from Cerrillos, NM. ~1 inch.",
                wantInReturn = "",
                photoUri = null,
                tags = listOf("turquoise", "rough", "newmexico"),
                status = "active",
                ownerUserId = "mock-turquoise-10",
                ownerName = "Turquoise Tara",
                ownerAvatar = "\uD83D\uDC8E",
                createdAt = now - 6 * dayMs,
                expiresAt = now + 8 * dayMs,
            ),
            LocalPublicTradeListing(
                id = "pub-listing-6",
                type = "HAVE",
                listingMode = "swap",
                specimenName = "Crinoid Fossil Plate",
                condition = "As found",
                description = "Limestone plate with multiple crinoid stems. Tennessee.",
                wantInReturn = "Looking for trilobites or other fossils",
                photoUri = null,
                tags = listOf("fossil", "crinoid", "tennessee"),
                status = "active",
                ownerUserId = "mock-fossil-8",
                ownerName = "Fossil Finder",
                ownerAvatar = "\uD83E\uDDA5",
                createdAt = now - 4 * dayMs,
                expiresAt = now + 9 * dayMs,
            ),
        )
        LocalDataStore.setTable(LocalDataStore.KEY_PUBLIC_TRADE_LISTINGS, publicListings)

        // ---- Empty tables (will be populated by user actions) -------------
        LocalDataStore.setTable(LocalDataStore.KEY_MESSAGE_REQUESTS, emptyList<LocalMessageRequest>())
        LocalDataStore.setTable(LocalDataStore.KEY_FRIEND_REQUESTS, emptyList<LocalFriendRequest>())
        LocalDataStore.setTable(LocalDataStore.KEY_BLOCKS, emptyList<LocalBlock>())
        LocalDataStore.setTable(LocalDataStore.KEY_NOTIFICATIONS, emptyList<LocalNotification>())
        LocalDataStore.setTable(LocalDataStore.KEY_TRADE_INTERESTS, emptyList<LocalTradeInterest>())
        LocalDataStore.setTable(LocalDataStore.KEY_USER_REPORTS, emptyList<LocalUserReport>())
        LocalDataStore.setTable(LocalDataStore.KEY_PINGS, emptyList<LocalPing>())

        Log.d(TAG, "Mock community seeded: ${users.size} users, ${connections.size} connections, ${threads.size} threads, ${posts.size} posts, ${publicListings.size} public listings")
    }
}

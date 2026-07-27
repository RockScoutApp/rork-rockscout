package com.rork.rockscout.ui.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rork.rockscout.ui.screens.AchievementsScreen
import com.rork.rockscout.ui.screens.AllAchievementsScreen
import com.rork.rockscout.ui.screens.CapturesScreen
import com.rork.rockscout.ui.screens.CollectionScreen
import com.rork.rockscout.ui.screens.FavoriteSpotsScreen
import com.rork.rockscout.ui.screens.HomeScreen
import com.rork.rockscout.ui.screens.IdentifyScreen
import com.rork.rockscout.ui.screens.LocationDetailScreen
import com.rork.rockscout.ui.screens.LocationsScreen
import com.rork.rockscout.ui.screens.PaleontologyScreen
import com.rork.rockscout.ui.screens.PrehistoricOrganismsScreen
import com.rork.rockscout.ui.screens.PeriodDetailScreen
import com.rork.rockscout.ui.screens.ProfileScreen
import com.rork.rockscout.ui.screens.RockGuideDetailScreen
import com.rork.rockscout.ui.screens.RockInfoScreen
import com.rork.rockscout.ui.screens.SearchScreen
import com.rork.rockscout.ui.screens.ElementDetailScreen
import com.rork.rockscout.ui.screens.PeriodicTableScreen
import com.rork.rockscout.ui.screens.SpecimenDetailScreen
import com.rork.rockscout.ui.screens.SharedSpotScreen
import com.rork.rockscout.ui.screens.SpecimenListScreen
import com.rork.rockscout.ui.screens.RocksAreAmazingScreen
import com.rork.rockscout.ui.screens.ArtifactsScreen
import com.rork.rockscout.ui.screens.ArtifactDetailScreen
import com.rork.rockscout.ui.screens.SavedImagesScreen
import com.rork.rockscout.ui.screens.MeteoriteHuntingScreen
import com.rork.rockscout.ui.screens.ContactUsScreen
import com.rork.rockscout.ui.screens.PaywallScreen
import com.rork.rockscout.ui.screens.ResourceLinksScreen
import com.rork.rockscout.ui.screens.TectonicVolcanicScreen
import com.rork.rockscout.ui.screens.GemShowsScreen
import com.rork.rockscout.ui.screens.GemShowDetailScreen
import com.rork.rockscout.ui.screens.CommunityPostDetailScreen
import com.rork.rockscout.ui.screens.GearGuideScreen
import com.rork.rockscout.ui.screens.GlossaryScreen
import com.rork.rockscout.ui.screens.NaturalWondersScreen
import com.rork.rockscout.ui.screens.TokenInfoScreen
import com.rork.rockscout.ui.screens.WishlistScreen
import com.rork.rockscout.ui.screens.TripPlannerScreen
import com.rork.rockscout.ui.screens.TripJournalScreen
import com.rork.rockscout.ui.screens.ArchivedTripsScreen
import com.rork.rockscout.ui.screens.FieldJournalScreen
import com.rork.rockscout.ui.screens.TradeBoardScreen
import com.rork.rockscout.ui.screens.ReferralScreen
import com.rork.rockscout.ui.screens.ScanScreen
import com.rork.rockscout.ui.screens.MessengerScreen
import com.rork.rockscout.ui.screens.FriendsScreen
import com.rork.rockscout.ui.screens.UserProfileScreen
import com.rork.rockscout.ui.screens.MyTradesScreen
import com.rork.rockscout.ui.screens.TradingFloorScreen
import com.rork.rockscout.ui.screens.UserAchievementsScreen
import com.rork.rockscout.ui.screens.UserCollectionScreen
import com.rork.rockscout.ui.screens.UserCollectionMode
import com.rork.rockscout.ui.screens.RockScoutsMapScreen
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.ReferralRepository
import com.rork.rockscout.data.ReportRepository
import com.rork.rockscout.ui.components.AccountDeletedPopup
import com.rork.rockscout.ui.components.NightModeOverlay
import com.rork.rockscout.ui.components.ReportWarningDialog
import com.rork.rockscout.ui.screens.DeveloperConsoleScreen
import com.rork.rockscout.ui.screens.DiscoverHuntersScreen
import com.rork.rockscout.ui.screens.SignInScreen
import com.rork.rockscout.ui.screens.BlmGuideScreen
import com.rork.rockscout.ui.screens.BlmStateDetailScreen
import com.rork.rockscout.ui.screens.BlmTrailheadDetailScreen
import com.rork.rockscout.ui.screens.BlmCampgroundDetailScreen
import com.rork.rockscout.ui.screens.SocialSettingsScreen
import com.rork.rockscout.ui.screens.NotificationsScreen
import com.rork.rockscout.ui.screens.HowToUseScreen
import com.rork.rockscout.ui.screens.ThankYouScreen
import com.rork.rockscout.ui.screens.DisclaimerScreen
import com.rork.rockscout.ui.screens.MineralCareGuideScreen
import com.rork.rockscout.ui.screens.FluorescenceUvReferenceScreen
import com.rork.rockscout.ui.screens.CrystalSystemReferenceScreen
import com.rork.rockscout.ui.screens.LapidaryBasicsGuideScreen
import com.rork.rockscout.ui.screens.AuroraScreen
import com.rork.rockscout.ui.screens.SevereWeatherScreen
import com.rork.rockscout.ui.screens.StarsLandingScreen
import com.rork.rockscout.ui.screens.ConstellationsScreen
import com.rork.rockscout.ui.screens.ImportantStarsScreen
import com.rork.rockscout.ui.screens.PlanetsScreen
import com.rork.rockscout.ui.screens.DeepSkyObjectsScreen
import com.rork.rockscout.ui.screens.TripCalendarScreen
import com.rork.rockscout.ui.screens.BlmLandScreen
import com.rork.rockscout.ui.screens.StateParksScreen
import com.rork.rockscout.ui.screens.BlmTrailheadsScreen
import com.rork.rockscout.ui.screens.BlmCampgroundsScreen
import com.rork.rockscout.ui.screens.StateParkDetailScreen
import com.rork.rockscout.ui.screens.RockTypesScreen
import com.rork.rockscout.ui.screens.MineralIdScreen
import com.rork.rockscout.ui.screens.CrystalHardnessScreen
import com.rork.rockscout.ui.screens.RockCycleToolsScreen
import com.rork.rockscout.ui.screens.GeoTimeScaleScreen
import com.rork.rockscout.ui.screens.MassExtinctionsScreen
import com.rork.rockscout.ui.screens.FossilTypesScreen
import com.rork.rockscout.ui.screens.GeologicPeriodsScreen
import com.rork.rockscout.ui.screens.isDisclaimerAccepted

object Routes {
    const val HOME = "home"
    const val IDENTIFY = "identify"
    const val LOCATIONS = "locations"
    const val GEM_SHOWS = "gem_shows"
    const val GEM_SHOW_DETAIL = "gem_show/{showId}"
    const val LOCATION_DETAIL = "location/{locationId}"
    const val PROFILE = "profile"
    const val PROFILE_FRIENDS = "profile_friends"
    const val COLLECTION = "collection"
    const val WISHLIST = "wishlist"
    const val FAVORITES = "favorites"
    const val CAPTURES = "captures"
    const val ROCK_INFO = "rockinfo"
    const val PALEONTOLOGY = "paleontology"
    const val PREHISTORIC_ORGANISMS = "prehistoric_organisms"
    const val ROCKS_ARE_AMAZING = "rocks_are_amazing"
    const val GEAR_GUIDE = "gear_guide"
    const val SAVED_IMAGES = "saved_images"
    const val METEORITE_HUNTING = "meteorite_hunting"
    const val CONTACT_US = "contact_us"
    const val PAYWALL = "paywall"
    const val TOKEN_INFO = "token_info"
    const val ACHIEVEMENTS = "achievements?scrollToBadges={scrollToBadges}"
    const val ALL_ACHIEVEMENTS = "all_achievements"
    const val TRIP_PLANNER = "trip_planner"
    const val ARCHIVED_TRIPS = "archived_trips"
    const val FIELD_JOURNAL = "field_journal"
    const val TRIP_JOURNAL = "trip_journal?initialTab={initialTab}"
    fun tripJournal(initialTab: Int = 0): String = "trip_journal?initialTab=$initialTab"
    const val TRADE_BOARD = "trade_board"
    const val COMMUNITY = "community"
    const val COMMUNITY_POST_DETAIL = "community_post/{postId}"
    const val REFERRAL = "referral"
    const val SIGN_IN = "sign_in"
    const val SCAN = "scan"
    // MESSENGER route removed — unified into FriendsScreen
    const val FRIENDS = "friends?initialTab={initialTab}&showFR={showFR}"
    const val USER_PROFILE = "user_profile/{userId}"
    const val USER_ACHIEVEMENTS = "user_achievements/{userId}"
    const val USER_COLLECTION = "user_collection/{userId}"
    const val USER_WISHLIST = "user_wishlist/{userId}"
    const val MESSENGER_THREAD = "messenger/{otherUserId}"
    const val MESSENGER_REQUEST = "messenger_request/{requestId}"
    const val ROCKSCOUTS_MAP = "rockscouts_map"
    const val SEARCH = "search"
    const val SPECIMEN_LIST = "specimenlist"
    const val GUIDE_DETAIL = "guide/{guideId}"
    const val PERIOD_DETAIL = "period/{periodId}"
    const val SPECIMEN_DETAIL = "specimen/{specimenId}?lat={lat}&lng={lng}"
    const val SHARED_SPOT = "shared_spot/{lat}/{lng}?name={name}"
    const val PERIODIC_TABLE = "periodic_table"
    const val ELEMENT_DETAIL = "element/{atomicNumber}"
    const val DISCOVER_HUNTERS = "discover_hunters"
    const val DEV_CONSOLE = "dev_console"
    const val BLM_GUIDE = "blm_guide"
    const val BLM_STATE_DETAIL = "blm_state/{stateCode}"
    const val BLM_TRAILHEAD_DETAIL = "blm_trailhead/{name}"
    const val BLM_CAMPGROUND_DETAIL = "blm_campground/{name}"
    const val TECTONIC_VOLCANIC = "tectonic_volcanic"
    const val RESOURCE_LINKS = "resource_links"
    const val MY_TRADES = "my_trades"
    const val TRADING_FLOOR = "trading_floor"
    const val SOCIAL_SETTINGS = "social_settings"
    const val NOTIFICATIONS = "notifications"
    const val HOW_TO_USE = "how_to_use"
    const val THANK_YOU = "thank_you/{tokens}/{days}"
    const val DISCLAIMER = "disclaimer?isGate={isGate}"
    const val MINERAL_CARE = "mineral_care"
    const val FLUORESCENCE_UV = "fluorescence_uv"
    const val CRYSTAL_SYSTEMS = "crystal_systems"
    const val LAPIDARY_BASICS = "lapidary_basics"
    const val AURORA = "aurora"
    const val SEVERE_WEATHER = "severe_weather"
    const val GLOSSARY = "glossary"
    const val ARTIFACTS = "artifacts"
    const val ARTIFACT_DETAIL = "artifact/{artifactId}"
    const val NATURAL_WONDERS = "natural_wonders"

    // Stars & Constellations sub-screens
    const val STARS_LANDING = "stars_landing"
    const val CONSTELLATIONS = "constellations"
    const val IMPORTANT_STARS = "important_stars"
    const val PLANETS = "planets"
    const val DEEP_SKY_OBJECTS = "deep_sky_objects"

    // Trip Calendar
    const val TRIP_CALENDAR = "trip_calendar"

    // BLM guide sub-screens
    const val BLM_LAND = "blm_land"
    const val STATE_PARKS = "state_parks"
    const val BLM_TRAILHEADS = "blm_trailheads"
    const val BLM_CAMPGROUNDS = "blm_campgrounds"
    const val STATE_PARK_DETAIL = "state_park/{parkId}"

    // Geology sub-screens
    const val ROCK_TYPES = "rock_types"
    const val MINERAL_ID = "mineral_id"
    const val CRYSTAL_HARDNESS = "crystal_hardness"
    const val ROCK_CYCLE_TOOLS = "rock_cycle_tools"

    // Paleontology sub-screens
    const val GEO_TIME_SCALE = "geo_time_scale"
    const val MASS_EXTINCTIONS = "mass_extinctions"
    const val FOSSIL_TYPES_SCREEN = "fossil_types"
    const val GEOLOGIC_PERIODS = "geologic_periods"

    fun gemShowDetail(id: String) = "gem_show/$id"
    fun communityPostDetail(id: String) = "community_post/$id"
    fun location(id: String) = "location/$id"
    fun element(atomicNumber: Int) = "element/$atomicNumber"
    fun guide(id: String) = "guide/$id"
    fun period(id: String) = "period/$id"
    fun specimen(id: String, lat: Double? = null, lng: Double? = null): String {
        val base = "specimen/$id"
        if (lat == null || lng == null) return base
        return "$base?lat=$lat&lng=$lng"
    }
    fun sharedSpot(lat: Double, lng: Double, name: String = ""): String {
        val encoded = Uri.encode(name)
        return "shared_spot/$lat/$lng?name=$encoded"
    }
    fun messengerThread(otherUserId: String) = "messenger/$otherUserId"
    fun messengerRequest(requestId: String) = "messenger_request/$requestId"
    fun userProfile(userId: String) = "user_profile/$userId"
    fun achievements(scrollToBadges: Boolean = false) = "achievements?scrollToBadges=$scrollToBadges"
    fun allAchievements() = "all_achievements"
    fun friends(initialTab: Int = 0, showFR: Boolean = true) = "friends?initialTab=$initialTab&showFR=$showFR"
    fun userAchievements(userId: String) = "user_achievements/$userId"
    fun userCollection(userId: String) = "user_collection/$userId"
    fun userWishlist(userId: String) = "user_wishlist/$userId"
    fun blmState(stateCode: String) = "blm_state/$stateCode"
    fun blmTrailhead(name: String) = "blm_trailhead/${Uri.encode(name)}"
    fun blmCampground(name: String) = "blm_campground/${Uri.encode(name)}"
    fun stateParkDetail(parkId: String) = "state_park/$parkId"
    fun thankYou(tokens: Int, days: Int) = "thank_you/$tokens/$days"
    fun disclaimer(isGate: Boolean) = "disclaimer?isGate=$isGate"
    fun artifactDetail(id: String) = "artifact/$id"
}

/**
 * Parse a rockscout:// deep-link URI and navigate to the matching screen.
 * Supported schemes:
 *   rockscout://location/{locationId}
 *   rockscout://specimen/{specimenId}
 *   rockscout://period/{periodId}
 *   rockscout://friends
 *   rockscout://messenger
 *   rockscout://trade_board
 *   rockscout://scan
 *   rockscout://locations
 *   rockscout://sign_in
 *
 * Note: Auth is now mandatory — the SignInScreen gate blocks all navigation
 * until the user is authenticated, so deep links are always handled post-auth.
 */
private fun handleDeepLink(uri: Uri, navController: NavController) {
    val segments = uri.pathSegments
    val isSignedIn = AuthRepository.instance.currentUserId != null
    when (uri.host) {
        "location" -> {
            val id = segments.firstOrNull()
            if (id != null) {
                navController.navigate(Routes.location(id))
            } else {
                navController.navigate(Routes.LOCATIONS)
            }
        }
        "locations" -> {
            navController.navigate(Routes.LOCATIONS)
        }
        "sign_in" -> {
            navController.navigate(Routes.SIGN_IN)
        }
        "friends" -> {
            if (isSignedIn) {
                navController.navigate(Routes.friends())
            } else {
                navController.navigate(Routes.SIGN_IN)
            }
        }
        "messenger" -> {
            if (isSignedIn) {
                navController.navigate(Routes.friends(initialTab = 1))
            } else {
                navController.navigate(Routes.SIGN_IN)
            }
        }
        "message_requests" -> {
            if (isSignedIn) navController.navigate(Routes.friends(initialTab = 1, showFR = false))
            else navController.navigate(Routes.SIGN_IN)
        }
        "trade_board" -> {
            if (isSignedIn) {
                navController.navigate(Routes.TRADE_BOARD)
            } else {
                navController.navigate(Routes.SIGN_IN)
            }
        }
        "scan" -> {
            if (isSignedIn) {
                navController.navigate(Routes.SCAN)
            } else {
                navController.navigate(Routes.SIGN_IN)
            }
        }
        "specimen" -> {
            val id = segments.firstOrNull()
            if (id != null) {
                navController.navigate(Routes.specimen(id))
            }
        }
        "spot" -> {
            // Format: rockscout://spot/<lat>,<lng>?name=<encoded+name>
            // The first path segment is "<lat>,<lng>"; name is a query param.
            val coordSegment = segments.firstOrNull()
            if (coordSegment != null) {
                val parts = coordSegment.split(',')
                if (parts.size == 2) {
                    val lat = parts[0].toDoubleOrNull()
                    val lng = parts[1].toDoubleOrNull()
                    if (lat != null && lng != null) {
                        val name = uri.getQueryParameter("name") ?: ""
                        navController.navigate(Routes.sharedSpot(lat, lng, name))
                    }
                }
            }
        }
        "period" -> {
            val id = segments.firstOrNull()
            if (id != null) {
                navController.navigate(Routes.period(id))
            }
        }
        "home" -> {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.HOME) { inclusive = true }
            }
        }
        "my_trades" -> {
            if (isSignedIn) navController.navigate(Routes.MY_TRADES)
            else navController.navigate(Routes.SIGN_IN)
        }
        "profile" -> {
            if (isSignedIn) navController.navigate(Routes.PROFILE)
            else navController.navigate(Routes.SIGN_IN)
        }
        "contact_us" -> {
            navController.navigate(Routes.CONTACT_US)
        }
        "notifications" -> {
            if (isSignedIn) navController.navigate(Routes.NOTIFICATIONS)
            else navController.navigate(Routes.SIGN_IN)
        }
        "user_profile" -> {
            val id = segments.firstOrNull()
            if (id != null && isSignedIn) {
                navController.navigate(Routes.userProfile(id))
            } else {
                navController.navigate(Routes.SIGN_IN)
            }
        }
        else -> { /* unknown — stay on home */ }
    }
}

@Composable
fun AppNavigation(
    deepLinkUri: Uri? = null,
    onDeepLinkConsumed: () -> Unit = {},
) {
    val navController = rememberNavController()

    // Family-friendly moderation: load the current user's report status on
    // app open and show the escalating warning pop-up if they have reports.
    val reportRepo = ReportRepository.instance
    val reportStatus by reportRepo.myStatus.collectAsState()
    var warningAcknowledged by remember { mutableStateOf(false) }
    // Recomposition trigger for the mandatory disclaimer gate. Accept is
    // persisted to PersistenceManager; flipping this state afterwards makes
    // the gate overlay disappear and reveal the NavHost (HOME as start).
    var disclaimerAcceptedVersion by remember { mutableStateOf(0) }
    val auth = AuthRepository.instance
    val sessionStatus by auth.sessionStatus.collectAsState()
    val isSignedIn = sessionStatus is com.rork.rockscout.data.SessionStatus.Authenticated
    val isAccountDeleted = sessionStatus is com.rork.rockscout.data.SessionStatus.AccountDeleted
    val pendingReferralCode by ReferralRepository.pendingReferralCode.collectAsState()
    val referralCodeApplied by ReferralRepository.referralCodeApplied.collectAsState()

    // Mandatory auth gate — every user must create an account to use the app.
    // The SignInScreen is shown as a full-screen blocking overlay until the
    // user signs in. Once authenticated, the overlay disappears and the
    // NavHost (with all screens) is accessible.
    if (!isSignedIn) {
        // Account-deleted users are NOT signed in (Authenticated), but they
        // have a session status of AccountDeleted. Show the blocking popup
        // instead of the sign-in gate.
        if (isAccountDeleted) {
            val deletedStatus = sessionStatus as com.rork.rockscout.data.SessionStatus.AccountDeleted
            AccountDeletedPopup(
                reason = deletedStatus.reason,
                onAppeal = { /* the popup handles showing the AppealComposer internally */ },
            )
            return
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            SignInScreen(
                isGate = true,
                onSignedIn = { /* overlay auto-dismisses when isSignedIn flips */ },
                onBack = { /* no-op in gate mode — there's nothing to go back to */ },
            )
        }
        return
    }

    // Mandatory legal disclaimer gate — every user must accept the current
    // version of the Privacy / Terms / Community / Trade / Safety notice before
    // reaching Home. Re-prompts when [DISCLAIMER_CURRENT_VERSION] is bumped.
    if (!isDisclaimerAccepted() && disclaimerAcceptedVersion == 0) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            DisclaimerScreen(
                navController = navController,
                isGate = true,
                onAccepted = { disclaimerAcceptedVersion = 1 },
            )
        }
        return
    }

    LaunchedEffect(isSignedIn) {
        if (isSignedIn) {
            reportRepo.loadMyStatus()
            // If the user entered a referral code on the sign-in screen,
            // process it now: verify against the backend, apply free gifts,
            // create the RockScout Friends connection, and queue a
            // confirmation popup for the home screen.
            if (!referralCodeApplied && pendingReferralCode != null) {
                ReferralRepository.processPendingReferralCode()
            }
            // Check the backend for unacknowledged referral completions
            // (friends who signed in with this user's code) and credit the
            // sender's rewards. A referral is only complete after the new
            // user has both verified the code AND signed in.
            ReferralRepository.checkAndCreditCompletions()
        }
    }

    if (isSignedIn && reportStatus.reportCount > 0 && !warningAcknowledged) {
        ReportWarningDialog(
            reportCount = reportStatus.reportCount,
            onAcknowledge = { warningAcknowledged = true },
            onAppeal = {
                warningAcknowledged = true
                navController.navigate(Routes.CONTACT_US)
            },
        )
    }

    // Handle deep-link from proximity notifications
    LaunchedEffect(deepLinkUri) {
        if (deepLinkUri != null) {
            handleDeepLink(deepLinkUri, navController)
            onDeepLinkConsumed()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            enterTransition = {
                slideInHorizontally(tween(300)) { it / 4 } + fadeIn(tween(300))
            },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(150)) },
            popExitTransition = { fadeOut(tween(150)) },
        ) {
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.IDENTIFY) { IdentifyScreen(navController) }
        composable(Routes.LOCATIONS) { LocationsScreen(navController) }
        composable(Routes.GEM_SHOWS) { GemShowsScreen(navController) }
        composable(
            Routes.GEM_SHOW_DETAIL,
            arguments = listOf(navArgument("showId") { type = NavType.StringType }),
        ) { entry ->
            GemShowDetailScreen(
                navController = navController,
                showId = entry.arguments?.getString("showId").orEmpty(),
            )
        }
        composable(Routes.PROFILE) { ProfileScreen(navController) }
        composable(Routes.PROFILE_FRIENDS) { ProfileScreen(navController, openToFriends = true) }
        composable(Routes.COLLECTION) { CollectionScreen(navController) }
        composable(Routes.WISHLIST) { WishlistScreen(navController) }
        composable(Routes.FAVORITES) { FavoriteSpotsScreen(navController) }
        composable(Routes.CAPTURES) { CapturesScreen(navController) }
        composable(Routes.ROCK_INFO) { RockInfoScreen(navController) }
        composable(Routes.PALEONTOLOGY) { PaleontologyScreen(navController) }
        composable(Routes.PREHISTORIC_ORGANISMS) { PrehistoricOrganismsScreen(navController) }
        composable(Routes.ROCKS_ARE_AMAZING) { RocksAreAmazingScreen(navController) }
        composable(Routes.ARTIFACTS) { ArtifactsScreen(navController) }
        composable(
            Routes.ARTIFACT_DETAIL,
            arguments = listOf(navArgument("artifactId") { type = NavType.StringType }),
        ) { entry ->
            ArtifactDetailScreen(
                navController = navController,
                artifactId = entry.arguments?.getString("artifactId").orEmpty(),
            )
        }
        composable(Routes.GEAR_GUIDE) { GearGuideScreen(navController) }
        composable(Routes.SAVED_IMAGES) { SavedImagesScreen(navController) }
        composable(Routes.METEORITE_HUNTING) { MeteoriteHuntingScreen(navController) }
        composable(Routes.SEARCH) { SearchScreen(navController) }
        composable(Routes.PERIODIC_TABLE) { PeriodicTableScreen(navController) }
        composable(
            Routes.ELEMENT_DETAIL,
            arguments = listOf(navArgument("atomicNumber") { type = NavType.IntType }),
        ) { entry ->
            ElementDetailScreen(
                navController = navController,
                atomicNumber = entry.arguments?.getInt("atomicNumber") ?: 0,
            )
        }
        composable(Routes.SPECIMEN_LIST) { SpecimenListScreen(navController) }
        composable(Routes.BLM_GUIDE) { BlmGuideScreen(navController) }
        composable(
            Routes.BLM_STATE_DETAIL,
            arguments = listOf(navArgument("stateCode") { type = NavType.StringType }),
        ) { entry ->
            BlmStateDetailScreen(
                navController = navController,
                stateCode = entry.arguments?.getString("stateCode").orEmpty(),
            )
        }
        composable(
            Routes.BLM_TRAILHEAD_DETAIL,
            arguments = listOf(navArgument("name") { type = NavType.StringType }),
        ) { entry ->
            BlmTrailheadDetailScreen(
                navController = navController,
                trailheadName = entry.arguments?.getString("name").orEmpty(),
            )
        }
        composable(
            Routes.BLM_CAMPGROUND_DETAIL,
            arguments = listOf(navArgument("name") { type = NavType.StringType }),
        ) { entry ->
            BlmCampgroundDetailScreen(
                navController = navController,
                campgroundName = entry.arguments?.getString("name").orEmpty(),
            )
        }
        composable(Routes.TECTONIC_VOLCANIC) { TectonicVolcanicScreen(navController) }
        composable(Routes.RESOURCE_LINKS) { ResourceLinksScreen(navController) }
        composable(Routes.MY_TRADES) { MyTradesScreen(navController) }
        composable(Routes.TRADING_FLOOR) { TradingFloorScreen(navController) }
        composable(Routes.SOCIAL_SETTINGS) { SocialSettingsScreen(navController) }
        composable(Routes.NOTIFICATIONS) { NotificationsScreen(navController) }
        composable(Routes.HOW_TO_USE) { HowToUseScreen(navController) }
        composable(
            Routes.DISCLAIMER,
            arguments = listOf(navArgument("isGate") { type = NavType.BoolType }),
        ) { entry ->
            DisclaimerScreen(
                navController = navController,
                isGate = entry.arguments?.getBoolean("isGate") ?: false,
            )
        }
        composable(
            Routes.THANK_YOU,
            arguments = listOf(
                navArgument("tokens") { type = NavType.IntType },
                navArgument("days") { type = NavType.IntType },
            ),
        ) { entry ->
            ThankYouScreen(
                navController = navController,
                tokensGranted = entry.arguments?.getInt("tokens") ?: 0,
                unlockDays = entry.arguments?.getInt("days") ?: 0,
            )
        }
        composable(Routes.DEV_CONSOLE) { DeveloperConsoleScreen(navController) }
        composable(Routes.CONTACT_US) { ContactUsScreen(navController) }
        composable(Routes.PAYWALL) { PaywallScreen(navController) }
        composable(Routes.TOKEN_INFO) { TokenInfoScreen(navController) }
        composable(
            Routes.ACHIEVEMENTS,
            arguments = listOf(navArgument("scrollToBadges") { type = NavType.BoolType; defaultValue = false }),
        ) { entry ->
            AchievementsScreen(
                navController = navController,
                scrollToBadges = entry.arguments?.getBoolean("scrollToBadges") ?: false,
            )
        }
        composable(Routes.ALL_ACHIEVEMENTS) { AllAchievementsScreen(navController) }
        composable(Routes.TRIP_PLANNER) { TripPlannerScreen(navController) }
        composable(Routes.ARCHIVED_TRIPS) { ArchivedTripsScreen(navController) }
        composable(Routes.FIELD_JOURNAL) { FieldJournalScreen(navController) }
        composable(
            Routes.TRIP_JOURNAL,
            arguments = listOf(navArgument("initialTab") { type = NavType.IntType; defaultValue = 0 }),
        ) { entry ->
            val initialTab = entry.arguments?.getInt("initialTab") ?: 0
            TripJournalScreen(navController, initialTabIndex = initialTab)
        }
        composable(Routes.TRADE_BOARD) { TradeBoardScreen(navController) }
        composable(Routes.COMMUNITY) { com.rork.rockscout.ui.screens.CommunityScreen(navController) }
        composable(
            Routes.COMMUNITY_POST_DETAIL,
            arguments = listOf(navArgument("postId") { type = NavType.StringType }),
        ) { entry ->
            CommunityPostDetailScreen(
                navController = navController,
                postId = entry.arguments?.getString("postId").orEmpty(),
            )
        }
        composable(Routes.REFERRAL) { ReferralScreen(navController) }
        composable(Routes.SIGN_IN) {
            SignInScreen(
                onSignedIn = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.DISCOVER_HUNTERS) { DiscoverHuntersScreen(navController) }
        composable(Routes.SCAN) { ScanScreen(navController) }
        composable(Routes.ROCKSCOUTS_MAP) { RockScoutsMapScreen(navController) }
        composable(
            Routes.FRIENDS,
            arguments = listOf(
                navArgument("initialTab") { type = NavType.IntType; defaultValue = 0 },
                navArgument("showFR") { type = NavType.BoolType; defaultValue = true },
            ),
        ) { entry ->
            FriendsScreen(
                navController = navController,
                initialTabIndex = entry.arguments?.getInt("initialTab") ?: 0,
                showFriendRequests = entry.arguments?.getBoolean("showFR") ?: true,
            )
        }
        composable(
            Routes.USER_PROFILE,
            arguments = listOf(navArgument("userId") { type = NavType.StringType }),
        ) { entry ->
            UserProfileScreen(
                navController = navController,
                userId = entry.arguments?.getString("userId").orEmpty(),
            )
        }
        composable(
            Routes.MESSENGER_THREAD,
            arguments = listOf(navArgument("otherUserId") { type = NavType.StringType }),
        ) { entry ->
            MessengerScreen(
                navController = navController,
                openThreadWith = entry.arguments?.getString("otherUserId"),
            )
        }
        composable(
            Routes.MESSENGER_REQUEST,
            arguments = listOf(navArgument("requestId") { type = NavType.StringType }),
        ) { entry ->
            MessengerScreen(
                navController = navController,
                openRequestId = entry.arguments?.getString("requestId"),
            )
        }
        composable(
            Routes.USER_ACHIEVEMENTS,
            arguments = listOf(navArgument("userId") { type = NavType.StringType }),
        ) { entry ->
            UserAchievementsScreen(
                navController = navController,
                userId = entry.arguments?.getString("userId").orEmpty(),
            )
        }
        composable(
            Routes.USER_COLLECTION,
            arguments = listOf(navArgument("userId") { type = NavType.StringType }),
        ) { entry ->
            UserCollectionScreen(
                navController = navController,
                userId = entry.arguments?.getString("userId").orEmpty(),
                mode = UserCollectionMode.COLLECTION,
            )
        }
        composable(
            Routes.USER_WISHLIST,
            arguments = listOf(navArgument("userId") { type = NavType.StringType }),
        ) { entry ->
            UserCollectionScreen(
                navController = navController,
                userId = entry.arguments?.getString("userId").orEmpty(),
                mode = UserCollectionMode.WISHLIST,
            )
        }

        composable(
            Routes.LOCATION_DETAIL,
            arguments = listOf(navArgument("locationId") { type = NavType.StringType }),
        ) { entry ->
            LocationDetailScreen(
                navController = navController,
                locationId = entry.arguments?.getString("locationId").orEmpty(),
            )
        }
        composable(
            Routes.GUIDE_DETAIL,
            arguments = listOf(navArgument("guideId") { type = NavType.StringType }),
        ) { entry ->
            RockGuideDetailScreen(
                navController = navController,
                guideId = entry.arguments?.getString("guideId").orEmpty(),
            )
        }
        composable(
            Routes.PERIOD_DETAIL,
            arguments = listOf(navArgument("periodId") { type = NavType.StringType }),
        ) { entry ->
            PeriodDetailScreen(
                navController = navController,
                periodId = entry.arguments?.getString("periodId").orEmpty(),
            )
        }
        composable(
            Routes.SPECIMEN_DETAIL,
            arguments = listOf(
                navArgument("specimenId") { type = NavType.StringType },
                navArgument("lat") { type = NavType.StringType; defaultValue = "" },
                navArgument("lng") { type = NavType.StringType; defaultValue = "" },
            ),
        ) { entry ->
            val lat = entry.arguments?.getString("lat")?.toDoubleOrNull()
            val lng = entry.arguments?.getString("lng")?.toDoubleOrNull()
            SpecimenDetailScreen(
                navController = navController,
                specimenId = entry.arguments?.getString("specimenId").orEmpty(),
                spotLatitude = lat,
                spotLongitude = lng,
            )
        }
        composable(
            Routes.SHARED_SPOT,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lng") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType; defaultValue = "" },
            ),
        ) { entry ->
            val lat = entry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
            val lng = entry.arguments?.getString("lng")?.toDoubleOrNull() ?: 0.0
            val name = entry.arguments?.getString("name").orEmpty()
            SharedSpotScreen(
                navController = navController,
                latitude = lat,
                longitude = lng,
                spotName = name,
            )
        }
        composable(Routes.MINERAL_CARE) { MineralCareGuideScreen(navController) }
        composable(Routes.FLUORESCENCE_UV) { FluorescenceUvReferenceScreen(navController) }
        composable(Routes.CRYSTAL_SYSTEMS) { CrystalSystemReferenceScreen(navController) }
        composable(Routes.LAPIDARY_BASICS) { LapidaryBasicsGuideScreen(navController) }
        composable(Routes.AURORA) { AuroraScreen(navController) }
        composable(Routes.SEVERE_WEATHER) { SevereWeatherScreen(navController) }
        composable(Routes.GLOSSARY) { GlossaryScreen(navController) }
        composable(Routes.NATURAL_WONDERS) { NaturalWondersScreen(navController) }
        composable(Routes.STARS_LANDING) { StarsLandingScreen(navController) }
        composable(Routes.CONSTELLATIONS) { ConstellationsScreen(navController) }
        composable(Routes.IMPORTANT_STARS) { ImportantStarsScreen(navController) }
        composable(Routes.PLANETS) { PlanetsScreen(navController) }
        composable(Routes.DEEP_SKY_OBJECTS) { DeepSkyObjectsScreen(navController) }
        composable(Routes.TRIP_CALENDAR) { TripCalendarScreen(navController) }
        // BLM guide sub-screens
        composable(Routes.BLM_LAND) { BlmLandScreen(navController) }
        composable(Routes.STATE_PARKS) { StateParksScreen(navController) }
        composable(Routes.BLM_TRAILHEADS) { BlmTrailheadsScreen(navController) }
        composable(Routes.BLM_CAMPGROUNDS) { BlmCampgroundsScreen(navController) }
        composable(
            Routes.STATE_PARK_DETAIL,
            arguments = listOf(navArgument("parkId") { type = NavType.StringType }),
        ) { entry ->
            StateParkDetailScreen(
                navController = navController,
                parkId = entry.arguments?.getString("parkId").orEmpty(),
            )
        }
        // Geology sub-screens
        composable(Routes.ROCK_TYPES) { RockTypesScreen(navController) }
        composable(Routes.MINERAL_ID) { MineralIdScreen(navController) }
        composable(Routes.CRYSTAL_HARDNESS) { CrystalHardnessScreen(navController) }
        composable(Routes.ROCK_CYCLE_TOOLS) { RockCycleToolsScreen(navController) }
        // Paleontology sub-screens
        composable(Routes.GEO_TIME_SCALE) { GeoTimeScaleScreen(navController) }
        composable(Routes.MASS_EXTINCTIONS) { MassExtinctionsScreen(navController) }
        composable(Routes.FOSSIL_TYPES_SCREEN) { FossilTypesScreen(navController) }
        composable(Routes.GEOLOGIC_PERIODS) { GeologicPeriodsScreen(navController) }
    }
        // Night-mode red overlay sits on top of the entire NavHost so it
        // covers every screen, sheet, and dialog when the toggle is on.
        NightModeOverlay()
    }
}

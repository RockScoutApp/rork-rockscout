package com.rork.rockscout.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.HolidayVillage
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.rork.rockscout.data.AchievementsRepository
import com.rork.rockscout.data.AdditionalSpecimens
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.NightModeManager
import com.rork.rockscout.data.AssemblageSpecimens
import com.rork.rockscout.data.ExpandedSpecimens
import com.rork.rockscout.data.ExpandedVarieties
import com.rork.rockscout.data.ImpactGlassSpecimens
import com.rork.rockscout.data.FossilSpecimens
import com.rork.rockscout.data.ExpansionGarnets
import com.rork.rockscout.data.ExpansionGemstones
import com.rork.rockscout.data.ExpansionMinerals
import com.rork.rockscout.data.ExpansionSilicates
import com.rork.rockscout.data.ExpansionTourmalines
import com.rork.rockscout.data.LocationRefresher
import com.rork.rockscout.data.JasperSpecimens
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.data.MassiveExpansion
import com.rork.rockscout.data.MeteoriteSpecimens
import com.rork.rockscout.data.RocksAreAmazingSpecimens
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.GearGuide
import com.rork.rockscout.data.GearItem
import android.location.Geocoder
import android.location.Address
import com.rork.rockscout.data.AffiliateClickTracker
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.data.DigSiteSearchService
import com.rork.rockscout.data.DigSiteDiscoveryStore
import com.rork.rockscout.data.LocationFetcher
import com.rork.rockscout.data.Specimen
import com.rork.rockscout.data.SpecimenImages
import com.rork.rockscout.data.DinoImageMap
import com.rork.rockscout.R
import com.rork.rockscout.ui.components.AdBanner
import com.rork.rockscout.ui.components.AnimatedAvatarIcon
import com.rork.rockscout.ui.components.BadgeIconButton
import com.rork.rockscout.ui.components.CelebrationLevel
import com.rork.rockscout.ui.components.PinPadOverlay
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.FullScreenImageViewer
import com.rork.rockscout.ui.components.BLM_HOME_TILE_NATURE
import com.rork.rockscout.ui.components.FIELD_KIT_TILE_PARKS
import com.rork.rockscout.ui.components.FIELD_KIT_TILE_TRAIL_CAMP
import com.rork.rockscout.ui.components.NATURAL_WONDERS_TILE_WAVE
import com.rork.rockscout.ui.components.GEM_MINERAL_HERO_URL
import com.rork.rockscout.ui.components.HunterStatusDropdown
import com.rork.rockscout.ui.components.HunterStatusIcon
import com.rork.rockscout.ui.components.LongPressableImage
import com.rork.rockscout.ui.components.RandomReviewPopup
import com.rork.rockscout.ui.components.ReviewUsCard
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SpecimenGlyph
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.SearchNearMeResultRow
import com.rork.rockscout.ui.components.ThankYouCelebration
import com.rork.rockscout.ui.components.LevelUpCelebration
import com.rork.rockscout.ui.components.AchievementCelebration
import com.rork.rockscout.ui.components.BadgeCelebration
import com.rork.rockscout.ui.components.ReferralRewardCelebration
import com.rork.rockscout.ui.components.ReferralSenderRewardCelebration
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.FieldCameraDialog
import com.rork.rockscout.data.AchievementCelebrationData
import com.rork.rockscout.data.BadgeCelebrationData
import com.rork.rockscout.data.Celebration
import com.rork.rockscout.ui.components.rockClassColor
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.sculpted
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.ReferralRepository
import com.rork.rockscout.data.HunterStatus
import com.rork.rockscout.data.ReviewManager
import com.rork.rockscout.data.PurchaseManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.rork.rockscout.data.PurchaseResult
import com.rork.rockscout.data.IdentifyAccessManager
import com.rork.rockscout.data.UpdateManager
import com.rork.rockscout.data.ApkInstaller
import com.rork.rockscout.data.PlayUpdateManager
import com.rork.rockscout.ui.components.TokenBank
import com.rork.rockscout.data.DeviceManager
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.navigation.safePopBackStack
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.AquaDeep
import com.rork.rockscout.ui.theme.Cyan
import com.rork.rockscout.ui.theme.CyanDeep
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.CitrineSoft
import com.rork.rockscout.ui.theme.CitrineDeep
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.components.profileBorderColor
import com.rork.rockscout.ui.components.statusAccent
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.screens.AURORA_TILE_BG_URL
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

/** Home screen Identify hero background URL. Public so the offline bulk-download registry can include it. */
const val IDENTIFY_HERO_BACKGROUND_URL = "https://r2-pub.rork.com/attachments/t5vh4q8xpxmg46mq3j955.jpg"

private data class HomeTile(
    val label: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color,
    val route: String,
    val imageUrl: String? = null,
)

@Composable
fun HomeScreen(navController: NavController) {
    val repo = AppRepository.instance
    val profile by repo.profile.collectAsStateWithLifecycle()
    val collection by repo.collection.collectAsStateWithLifecycle()
    val wishlist by repo.wishlist.collectAsStateWithLifecycle()
    val favorites by repo.favoriteSpots.collectAsStateWithLifecycle()
    val captures by repo.captures.collectAsStateWithLifecycle()
    val current by repo.currentLocation.collectAsStateWithLifecycle()
    val locationRefresh by repo.locationRefreshTrigger.collectAsStateWithLifecycle()
    val achievementsState by AchievementsRepository.state.collectAsStateWithLifecycle()
    val level by AchievementsRepository.level.collectAsStateWithLifecycle()
    val tier by AchievementsRepository.tier.collectAsStateWithLifecycle()

    val trips by repo.trips.collectAsStateWithLifecycle()
    val journalEntries by repo.journalEntries.collectAsStateWithLifecycle()
    val tradeListings by repo.tradeListings.collectAsStateWithLifecycle()
    val listingsCount = tradeListings.count { it.status == "active" }
    val savedImages by repo.savedImages.collectAsStateWithLifecycle()
    val auroraSavedSpots by repo.auroraSavedSpots.collectAsStateWithLifecycle()

    var viewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerInitialPage by remember { mutableIntStateOf(0) }

    // Search Near Me state
    var isSearchingNearMe by remember { mutableStateOf(false) }
    var nearMeResults by remember { mutableStateOf<List<DigSiteDiscoveryStore.DiscoveredSite>>(emptyList()) }
    var nearMeSearchRadius by remember { mutableIntStateOf(50) }
    var nearMeSearchArea by remember { mutableStateOf("") }
    var nearMeError by remember { mutableStateOf(false) }
    var nearMeProgress by remember { mutableFloatStateOf(0f) }
    var nearMeStage by remember { mutableStateOf("") }
    var showNearMePermissionDialog by remember { mutableStateOf(false) }

    // Developer Console entry — 5 quick taps on the version text reveal the PIN pad
    var versionTapCount by remember { mutableIntStateOf(0) }
    var versionFirstTapMs by remember { mutableStateOf(0L) }
    var showPinPad by remember { mutableStateOf(false) }
    var showSmsVerify by remember { mutableStateOf(false) }
    var smsVerifying by remember { mutableStateOf(false) }
    var isResending by remember { mutableStateOf(false) }
    var resendSentAtMillis by remember { mutableStateOf(0L) }
    var resendJustSent by remember { mutableStateOf(false) }
    var smsError by remember { mutableStateOf<String?>(null) }
    // Code returned directly by the backend when SMS delivery isn't available.
    // Shown inline only when notifications are blocked, so the developer is
    // never locked out waiting on a text that can't arrive.
    var devHintCode by remember { mutableStateOf<String?>(null) }
    var showFellowRockScoutsNote by remember { mutableStateOf(false) }
    var showFieldCamera by remember { mutableStateOf(false) }

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { _ -> /* Results handled implicitly */ }

    val purchaseManager = PurchaseManager.instance
    val isPurchasing by purchaseManager.isPurchasing.collectAsState()
    val isPremium by purchaseManager.effectiveIsPremium.collectAsState()
    val rawIsPremium by purchaseManager.isPremium.collectAsState()
    val deviceOverLimit by DeviceManager.deviceOverLimit.collectAsState()
    val auth = AuthRepository.instance
    val sessionStatus by auth.sessionStatus.collectAsStateWithLifecycle()
    val isSignedIn = sessionStatus is com.rork.rockscout.data.SessionStatus.Authenticated
    val justVerifiedFromLink by auth.justVerifiedFromLink.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val reviewManager = remember { ReviewManager(context) }

    val accessManager = IdentifyAccessManager.instance
    val tokenBalance by accessManager.tokenBalance.collectAsStateWithLifecycle()
    val trialUsesRemaining by accessManager.trialUsesRemaining.collectAsStateWithLifecycle()
    val trialActive by accessManager.trialActive.collectAsStateWithLifecycle()
    val trialExpired by accessManager.trialExpired.collectAsStateWithLifecycle()
    val shouldShowTrialInfo by accessManager.shouldShowTrialInfo.collectAsStateWithLifecycle()
    var trialBannerDismissed by remember { mutableStateOf(false) }

    // Update state — collected here so the update-available banner at the top
    // of the home screen can react instantly when a new version is detected.
    val updateInfo by UpdateManager.updateInfo.collectAsStateWithLifecycle()
    val apkStatus by ApkInstaller.status.collectAsStateWithLifecycle()

    // TokenBank shows total identifies available: trial uses (when active) + purchased tokens.
    val effectiveTokenBalance = (if (trialActive) trialUsesRemaining else 0) + tokenBalance
    val hasLocationUnlock by accessManager.hasLocationUnlock.collectAsStateWithLifecycle()
    val hasAdFreeUnlock by accessManager.hasAdFreeUnlock.collectAsStateWithLifecycle()
    val isTokenUnlocked = isPremium || hasLocationUnlock || hasAdFreeUnlock
    val locationLocked = remember(isPremium, trialExpired, hasLocationUnlock) {
        accessManager.isLocationLocked(isPremium)
    }
    val socialLocked = remember(isPremium, trialExpired, hasLocationUnlock) {
        accessManager.isSocialLocked(isPremium)
    }

    // Referral reward popups — shown when a new user's code is confirmed or
    // when the sender has pending referral completions credited.
    val pendingNewUserReward by ReferralRepository.pendingNewUserReward.collectAsStateWithLifecycle()
    val pendingSenderReward by ReferralRepository.pendingSenderReward.collectAsStateWithLifecycle()

    // Celebration overlay — shown after a successful donation
    var celebrationLevel by remember { mutableStateOf<CelebrationLevel?>(null) }

    // Achievement celebration — fires when the achievements state has a pending
    // level-up or badge-earn event (set by AchievementsRepository.award()).
    var achievementCelebration by remember { mutableStateOf<Celebration?>(null) }
    var completedAchievement by remember { mutableStateOf<AchievementCelebrationData?>(null) }
    var earnedBadge by remember { mutableStateOf<BadgeCelebrationData?>(null) }
    var shareToProfileCelebration by remember { mutableStateOf<Pair<String, String>?>(null) }
    var shareToProfileAchievement by remember { mutableStateOf<AchievementCelebrationData?>(null) }
    var shareToProfileBadge by remember { mutableStateOf<BadgeCelebrationData?>(null) }
    var shareToProfileFieldCapture by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    // Consolidated back handler — dismisses the topmost overlay in priority
    // order before falling through to NavController pop. Replaces ~14 separate
    // BackHandler registrations to cut dispatch overhead and recompositions.
    BackHandler {
        when {
            showFellowRockScoutsNote -> showFellowRockScoutsNote = false
            showFieldCamera -> showFieldCamera = false
            showPinPad -> showPinPad = false
            shouldShowTrialInfo -> { /* block — must tap Confirm */ }
            showSmsVerify -> showSmsVerify = false
            celebrationLevel != null -> celebrationLevel = null
            achievementCelebration != null -> {
                achievementCelebration = null
                com.rork.rockscout.data.AchievementsRepository.clearCelebration()
            }
            completedAchievement != null -> {
                completedAchievement = null
                com.rork.rockscout.data.AchievementsRepository.clearAchievementCelebration()
            }
            earnedBadge != null -> {
                earnedBadge = null
                com.rork.rockscout.data.AchievementsRepository.clearBadgeCelebration()
            }
            viewerUrls.isNotEmpty() -> viewerUrls = emptyList()
            shareToProfileCelebration != null -> shareToProfileCelebration = null
            shareToProfileAchievement != null -> shareToProfileAchievement = null
            shareToProfileBadge != null -> shareToProfileBadge = null
            shareToProfileFieldCapture != null -> shareToProfileFieldCapture = null
            pendingNewUserReward != null -> ReferralRepository.clearPendingNewUserReward()
            pendingSenderReward != null -> ReferralRepository.clearPendingSenderReward()
            else -> {
                // Never exit the app via back button — only pop if
                // there's a previous screen. At root, consume and stay.
                navController.safePopBackStack()
            }
        }
    }

    androidx.compose.runtime.LaunchedEffect(achievementsState.lastCelebrationMs) {
        if (achievementsState.lastCelebrationMs > 0L && achievementCelebration == null) {
            achievementCelebration = Celebration(
                type = achievementsState.lastCelebrationType,
                detail = achievementsState.lastCelebrationDetail,
                emoji = if (achievementsState.lastCelebrationType == "level_up")
                    com.rork.rockscout.data.LevelTier.forLevel(
                        com.rork.rockscout.data.AchievementsRepository.levelForXp(achievementsState.totalXp)
                    ).emoji
                else "🏆",
            )
        }
    }
    androidx.compose.runtime.LaunchedEffect(achievementsState.lastAchievementCelebrationMs) {
        if (achievementsState.lastAchievementCelebrationMs > 0L && completedAchievement == null) {
            completedAchievement = com.rork.rockscout.data.AchievementsRepository.pendingAchievementCelebration()
        }
    }
    androidx.compose.runtime.LaunchedEffect(achievementsState.lastBadgeCelebrationMs) {
        if (achievementsState.lastBadgeCelebrationMs > 0L && earnedBadge == null) {
            earnedBadge = com.rork.rockscout.data.AchievementsRepository.pendingBadgeCelebration()
        }
    }

    val nearby = remember(current, profile.locationMonitoring, locationRefresh) {
        SeedData.allLocations
            .map { it to AppRepository.distanceMiles(current.first, current.second, it.latitude, it.longitude) }
            .let { list ->
                if (profile.locationMonitoring) {
                    list.filter { it.second <= 100.0 }
                } else {
                    list
                }
            }
            .sortedBy { it.second }
            .take(3)
    }

    val tiles = listOf(
        // Row 1: My Rocks / Wishlist
        HomeTile("My Rocks", "${collection.size} specimens · Your collected specimens", Icons.Filled.Inventory2, Aqua, Routes.COLLECTION,
            SpecimenImages.urls["amethyst"]?.firstOrNull()),
        HomeTile("Wishlist", "${wishlist.size} wanted · Specimens you're hunting for", Icons.Filled.PlaylistAdd, Color(0xFF9B7BD8), Routes.WISHLIST,
            SpecimenImages.urls["diamond"]?.firstOrNull()),
        // Row 2: Field Captures / Field Camera
        HomeTile("Field Captures", "${captures.size} photos · Rock photos logged in the field", Icons.Filled.PhotoLibrary, Color(0xFF5CC98C), Routes.CAPTURES,
            SpecimenImages.urls["quartz"]?.firstOrNull()),
        HomeTile("Field Camera", "Snap a photo without the ID tool · Save anywhere", Icons.Filled.CameraAlt, Citrine, "field_camera",
            SpecimenImages.urls["citrine"]?.firstOrNull()),
        // Remaining tiles
        HomeTile("My Favorite Spots", "${favorites.size} saved · Dig sites you've saved for quick access", Icons.Filled.FavoriteBorder, Color(0xFFE2574C), Routes.FAVORITES,
            SpecimenImages.urls["sedimentary-geode"]?.firstOrNull()),
        HomeTile("Dig Sites & Rock Shops", "Mines, shops, metaphysical & digs to visit", Icons.Filled.LocationOn, Success, Routes.LOCATIONS,
            SpecimenImages.urls["fluorite"]?.firstOrNull()),
        HomeTile("Specimen Database", "${SeedData.allSpecimens.size} entries · Browse every specimen in the app", Icons.Filled.CollectionsBookmark, Color(0xFF6FA8C7), Routes.SPECIMEN_LIST,
            SpecimenImages.urls["pyrite"]?.firstOrNull()),
        HomeTile("My Saved Images", "${savedImages.size} saved · Photos you've saved from the app", Icons.Filled.Download, Color(0xFF44AACC), Routes.SAVED_IMAGES,
            SpecimenImages.urls["amazonite-smoky-quartz-assemblage"]?.firstOrNull()),
        HomeTile("Aurora Forecaster", "Northern lights forecast & saved aurora spots", Icons.Filled.NightsStay, Color(0xFF00E5C9), Routes.AURORA,
            AURORA_TILE_BG_URL),
        HomeTile("Severe Weather", "NWS alerts + live storm chaser streams", Icons.Filled.Warning, Color(0xFFFF6B3D), Routes.SEVERE_WEATHER,
            "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/e2803cb6-56f0-4506-84cb-0a36be573f7e.png"),
    )

    val infoTiles = listOf(
        // 1. Rocks Are Amazing (RAA)
        HomeTile("Rocks Are Amazing", "${RocksAreAmazingSpecimens.allAmazing.size} wonders · Earth's most stunning formations", Icons.Filled.CollectionsBookmark, Color(0xFF44AACC), Routes.ROCKS_ARE_AMAZING,
            SpecimenImages.urls["bismuth-crystal"]?.firstOrNull()),
        // 2. Artifacts
        HomeTile("Artifacts", "Arrowheads, hand axes, beads & stone tools", Icons.Filled.AccountBalance, Color(0xFFB87333), Routes.ARTIFACTS,
            "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/ca44cafb-2e4f-4d3b-9334-174ceedf713b.png"),
        // 3. Natural Wonders
        HomeTile("Natural Wonders", "World-famous geological sites & what to find there", Icons.Filled.Public, Color(0xFF1B3A4B), Routes.NATURAL_WONDERS,
            NATURAL_WONDERS_TILE_WAVE),
        // 4. Crystal Systems
        HomeTile("Crystal Systems", "The 7 crystal shapes with visual examples", Icons.Filled.Diamond, Color(0xFF6FA8C7), Routes.CRYSTAL_SYSTEMS,
            SpecimenImages.urls["herkimer"]?.firstOrNull()),
        // 5. Fluorescence & UV
        HomeTile("Fluorescence & UV", "Which minerals glow under UV light & what colors", Icons.Filled.Lightbulb, Color(0xFF9B7BD8), Routes.FLUORESCENCE_UV,
            SpecimenImages.urls["uv-tile"]?.firstOrNull()),
        // 6. Dino Dictionary
        HomeTile("Dinosaur Dictionary", "200+ dinosaurs & Ice Age animals with silhouettes", Icons.Filled.Pets, Color(0xFF6FBF8A), Routes.DINOSAUR_DICTIONARY,
            DinoImageMap.imageUri("tyrannosaurus")),
        // 7. Explore Organisms (Exploring Prehistoric Organisms)
        HomeTile("Exploring Prehistoric Organisms", "Dinosaurs, birds, ancient flora & more", Icons.Filled.Nature, Color(0xFF8BBF6A), Routes.PREHISTORIC_ORGANISMS,
            SpecimenImages.urls["dinosaur-bone"]?.firstOrNull()),
        // 8. Exploring Paleontology
        HomeTile("Exploring Paleontology", "Fossils, eras & deep-time history", Icons.Filled.Nature, Color(0xFFC9A87C), Routes.PALEONTOLOGY,
            SpecimenImages.urls["ammonite"]?.firstOrNull()),
        // 9. Exploring Geology
        HomeTile("Exploring Geology", "Learn how rocks, minerals & gems form", Icons.Filled.MenuBook, Color(0xFFD9B26A), Routes.ROCK_INFO,
            SpecimenImages.urls["granite"]?.firstOrNull()),
        // 10. Tectonics & Volcanoes
        HomeTile("Tectonics & Volcanoes", "Plate movement, magma & where rocks form", Icons.Filled.LocalFireDepartment, Color(0xFFE2574C), Routes.TECTONIC_VOLCANIC,
            "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/13d72c22-f574-47c4-a23c-a6a9ae6b65bb.png"),
        // 11. Explore the Stars
        HomeTile("Explore the Stars", "88 constellations, stars, planets & deep sky objects", Icons.Filled.Star, Color(0xFF00E5C9), Routes.STARS_LANDING,
            "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/923312d6-4c0c-4855-8e00-827426991a2f.png"),
        // 12. Periodic Table
        HomeTile("Periodic Table", "118 elements · Where each element appears in rocks & gems", Icons.Filled.Science, Color(0xFF7CB5EC), Routes.PERIODIC_TABLE,
            "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/040be3bf-71ab-46d0-b6be-4598df22a18b.png"),
        // 13. Mineral Care & Cleaning
        HomeTile("Mineral Care & Cleaning", "Don't ruin your finds · Safe cleaning for every mineral type", Icons.Filled.CleaningServices, Color(0xFF5CC98C), Routes.MINERAL_CARE,
            "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/263c5833-c668-4d2a-a4b5-e45cb5148679.png"),
        // 14. Lapidary Basics
        HomeTile("Lapidary Basics", "Cut, polish & cab your finds into jewelry", Icons.Filled.Construction, Color(0xFFE8A33D), Routes.LAPIDARY_BASICS,
            SpecimenImages.urls["lapidary-tile"]?.firstOrNull()),
    )

    // Field-guide tiles live in the field kit grid (same square format as the
    // kit tiles) instead of the Explore & learn carousel.
    val fieldGuideTiles = listOf(
        HomeTile("BLM Public Lands", "State rules, dig sites & info", Icons.Filled.Terrain, Color(0xFFC97B4A), Routes.BLM_GUIDE,
            BLM_HOME_TILE_NATURE),
        HomeTile("National / State Parks", "Parks with geological interest", Icons.Filled.Park, Color(0xFF7BA868), Routes.STATE_PARKS,
            FIELD_KIT_TILE_PARKS),
        HomeTile("Campgrounds & Trailheads", "Camp & hike near dig sites", Icons.Filled.HolidayVillage, Color(0xFFE8A33D), Routes.CAMPGROUNDS_TRAILHEADS,
            FIELD_KIT_TILE_TRAIL_CAMP),
        HomeTile("Finding Meteorites", "How to hunt and identify space rocks", Icons.Filled.Public, Color(0xFFC0C0C0), Routes.METEORITE_HUNTING,
            SpecimenImages.urls["amazing-meteorite-hunting"]?.firstOrNull()),
        HomeTile("Rock & Gem Resources", "Trusted geology, gem & fossil websites", Icons.Filled.Public, Color(0xFF7CB5EC), Routes.RESOURCE_LINKS,
            GEM_MINERAL_HERO_URL),
        HomeTile("Glossary", "Every rock, mineral & space term explained", Icons.Filled.MenuBook, Color(0xFF6FA8C7), Routes.GLOSSARY,
            SpecimenImages.urls["lapis-lazuli"]?.firstOrNull()),
    )

    val featuredSpecimens = remember {
        SeedData.allSpecimens.filter { it.rarity != "Common" }.shuffled().take(6)
    }

    // Prefetch featured specimen + tile images into Coil cache so they
    // load instantly even on a weak 4G connection. Runs once on first composition.
    androidx.compose.runtime.LaunchedEffect(featuredSpecimens) {
        val prefetchUrls = buildList {
            featuredSpecimens.forEach { spec ->
                // Prefetch the first 2 images per featured specimen so the
                // detail screen hero + gallery start instantly too.
                SpecimenImages.urls[spec.id]?.take(2)?.forEach { add(it) }
            }
            tiles.mapNotNull { it.imageUrl }.take(4).forEach { add(it) }
        }
        com.rork.rockscout.data.ImagePrefetcher.prefetch(context, prefetchUrls)
    }

    var isRefreshing by remember { mutableStateOf(false) }

    RockBackground {
        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                scope.launch {
                    runCatching {
                        LocationRefresher.refresh(context)
                        repo.saveProfileChanges()
                        kotlinx.coroutines.delay(600)
                    }
                    isRefreshing = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp, top = 64.dp, bottom = 40.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
            // Update available banner — shown at the very top when a newer
            // version is available. Tapping it goes to Settings → Update Now.
            if (updateInfo != null && apkStatus == ApkInstaller.Status.IDLE) {
                item {
                    UpdateAvailableBanner(
                        versionName = updateInfo?.latestVersionName ?: "",
                        changelog = updateInfo?.changelog ?: "",
                        onClick = { navController.navigate(Routes.SOCIAL_SETTINGS) },
                    )
                }
            }

            // Email verification success banner — shown once when the user
            // arrives from a click-to-verify email link. Auto-dismisses after 6s.
            if (justVerifiedFromLink) {
                item {
                    EmailVerifiedBanner(
                        onDismiss = { auth.consumeJustVerifiedFromLink() },
                    )
                }
            }

            // Auto-dismiss the verification banner after 6 seconds.
            if (justVerifiedFromLink) {
                item {
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(6000)
                        auth.consumeJustVerifiedFromLink()
                    }
                }
            }

            // Device limit banner — shown when premium is paused due to 3-device limit
            if (rawIsPremium && deviceOverLimit) {
                item {
                    DeviceLimitBanner(
                        onManageDevices = { navController.navigate(Routes.MANAGE_DEVICES) },
                    )
                }
            }

            // Trial expired upsell banner — dismissible but reappears on next launch
            if (trialExpired && !isPremium && !trialBannerDismissed) {
                item {
                    TrialExpiredBanner(
                        onGoPremium = { navController.navigate(Routes.PAYWALL) },
                        onDonate = { navController.navigate(Routes.TOKEN_INFO) },
                        onDismiss = { trialBannerDismissed = true },
                    )
                }
            }

            // Ad banner at top for non-premium users
            item {
                AdBanner(
                    onRemoveAds = { navController.navigate(Routes.PAYWALL) },
                )
            }
            item {
                HomeTagline(
                    onNoteClick = { showFellowRockScoutsNote = true },
                    onHowToUseClick = { navController.navigate(Routes.HOW_TO_USE) },
                    onContactUsClick = { navController.navigate(Routes.CONTACT_US) },
                )
            }
            item {
                HomeHeader(
                    avatar = profile.avatarEmoji,
                    avatarImagePath = profile.avatarImagePath,
                    onSearchClick = { navController.navigate(Routes.SEARCH) },
                    onProfileClick = { navController.navigate(Routes.PROFILE) },
                    tokenBalance = effectiveTokenBalance,
                    isPremium = isPremium,
                    onTokenBankClick = { navController.navigate(Routes.TOKEN_INFO) },
                    isSignedIn = isSignedIn,
                    hunterStatus = profile.hunterStatus,
                    isTokenUnlocked = isTokenUnlocked,
                    onStatusChange = { status -> repo.setHunterStatus(status) },
                    onSocialClick = {
                        if (isSignedIn) navController.navigate(Routes.PROFILE_FRIENDS)
                        else navController.navigate(Routes.SIGN_IN)
                    },
                    onNotificationsClick = { navController.navigate(Routes.NOTIFICATIONS) },
                    onMessageRequestsClick = { navController.navigate(Routes.friends(initialTab = 1)) },
                )
            }
            item { HomeGreeting(name = profile.name) }
            item { IdentifyHero { navController.navigate(Routes.IDENTIFY) } }
            item {
                StreakLevelStrip(
                    level = level,
                    tierName = tier.displayName,
                    tierEmoji = tier.emoji,
                    totalXp = achievementsState.totalXp,
                    streak = achievementsState.currentStreak,
                    progress = AchievementsRepository.levelProgress(achievementsState.totalXp),
                    onOpenAchievements = { navController.navigate(Routes.allAchievements()) },
                    onTileClick = { navController.navigate(Routes.achievements()) },
                )
            }
            // Full-width banner tiles (same width/height as Location Monitoring)
            // Shared accent for the four banner tiles (Gear Guide, Trade Board,
            // Community, Trip Planner, Field Journal) so their borders match.
            val bannerAccent = Color(0xFFE8A33D)
            item {
                HomeGearGuideTile(navController = navController)
            }
            item {
                FullWidthBannerTile(
                    label = "Trade Board",
                    subtitle = if (socialLocked) "Subscribe or donate to post and browse trades" else "$listingsCount listings · Post a specimen to swap, sell, or browse nearby",
                    icon = Icons.Filled.SwapHoriz,
                    accent = bannerAccent,
                    imageRes = R.drawable.trade_board_floor,
                    locked = socialLocked,
                    onClick = { if (socialLocked) navController.navigate(Routes.PAYWALL) else navController.navigate(Routes.TRADE_BOARD) },
                )
            }
            item {
                FullWidthBannerTile(
                    label = "Community",
                    subtitle = if (socialLocked) "Subscribe or donate to join the community" else "Ask questions, share finds & discuss with fellow RockScouts",
                    icon = Icons.Filled.Forum,
                    accent = bannerAccent,
                    imageUrl = "https://r2-pub.rork.com/attachments/r6r3hon86cegy20yrqaxy.jpg",
                    locked = socialLocked,
                    onClick = { if (socialLocked) navController.navigate(Routes.PAYWALL) else navController.navigate(Routes.COMMUNITY) },
                )
            }
            item {
                FullWidthBannerTile(
                    label = "Trip Planner & Field Journal",
                    subtitle = if (socialLocked) "Subscribe or donate to plan and share trips" else "${trips.size} trips · ${journalEntries.size} journal entries · Plan hunts & log your days in the field",
                    icon = Icons.Filled.Map,
                    accent = bannerAccent,
                    imageUrl = "https://r2-pub.rork.com/attachments/78k8yy4tgahby3o9opb6j.png",
                    locked = socialLocked,
                    onClick = { if (socialLocked) navController.navigate(Routes.PAYWALL) else navController.navigate(Routes.tripJournal(0)) },
                )
            }
            item {
                FullWidthBannerTile(
                    label = "Calendar",
                    subtitle = "${trips.size} planned trips · View, edit & plan all your trips on a calendar",
                    icon = Icons.Filled.CalendarMonth,
                    accent = Color(0xFF7CB5EC),
                    imageUrl = "https://r2-pub.rork.com/attachments/78k8yy4tgahby3o9opb6j.png",
                    onClick = { navController.navigate(Routes.TRIP_CALENDAR) },
                )
            }
            item { SectionLabel("Your field kit") }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    tiles.chunked(2).forEach { rowTiles ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            rowTiles.forEach { tile ->
                                DashboardTile(
                                    tile = tile,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        if (tile.route == "field_camera") {
                                            showFieldCamera = true
                                        } else {
                                            navController.navigate(tile.route)
                                        }
                                    },
                                )
                            }
                            if (rowTiles.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
            // Field-guide tiles render in the same square grid as the kit
            // tiles so they match the rest of the field kit section.
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    fieldGuideTiles.chunked(2).forEach { rowTiles ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            rowTiles.forEach { tile ->
                                DashboardTile(
                                    tile = tile,
                                    modifier = Modifier.weight(1f),
                                    onClick = { navController.navigate(tile.route) },
                                )
                            }
                            if (rowTiles.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                val hasNearbyWithinRadius = remember(current, profile.locationMonitoring) {
                    profile.locationMonitoring && SeedData.allLocations.any {
                        AppRepository.distanceMiles(current.first, current.second, it.latitude, it.longitude) <= 100.0
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SectionLabel(
                        when {
                            isSearchingNearMe -> "Searching within ${nearMeSearchRadius} miles…"
                            nearMeResults.isNotEmpty() -> "Found ${nearMeResults.size} spot(s) near you (${nearMeSearchArea})"
                            nearMeError -> "Search failed"
                            hasNearbyWithinRadius -> "Nearby hot spots (within 100 mi)"
                            else -> "Featured locations across the US"
                        }
                    )
                    // Search Near Me pill button — aligned right in the header row
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSearchingNearMe) Citrine.copy(alpha = 0.08f) else Citrine.copy(alpha = 0.15f))
                            .clickable {
                                if (!profile.nearbyPlacesEnabled || !LocationFetcher.hasPermission(context)) {
                                    showNearMePermissionDialog = true
                                } else {
                                    isSearchingNearMe = true
                                    nearMeError = false
                                    nearMeResults = emptyList()
                                    nearMeSearchRadius = 50
                                    nearMeProgress = 0f
                                    nearMeStage = "Locating your position…"
                                    scope.launch {
                                        val lat = current.first
                                        val lng = current.second
                                        nearMeProgress = 0.20f
                                        // Reverse geocode: try Android Geocoder, fall back to profile homeRegion
                                        nearMeStage = "Searching for nearby dig sites…"
                                        val searchArea = reverseGeocode(context, lat, lng, profile.homeRegion)
                                        nearMeSearchArea = searchArea
                                        nearMeProgress = 0.40f
                                        // 50-mile search
                                        var results = DigSiteSearchService.searchNearLocation(lat, lng, searchArea, 50)
                                        if (results.isEmpty()) {
                                            // 100-mile retry
                                            nearMeSearchRadius = 100
                                            nearMeStage = "Widening search to 100 miles…"
                                            nearMeProgress = 0.65f
                                            val broaderArea = searchArea.substringAfter(", ").ifBlank { searchArea }
                                            results = DigSiteSearchService.searchNearLocation(lat, lng, broaderArea, 100)
                                        }
                                        nearMeProgress = 0.90f
                                        if (results.isEmpty() && nearMeSearchRadius == 100) {
                                            // Both searches returned nothing — check if it was an error vs genuinely empty
                                            // For now treat empty as success (show empty state)
                                        }
                                        nearMeResults = results
                                        if (results.isNotEmpty()) {
                                            DigSiteDiscoveryStore.addAll(results)
                                        }
                                        nearMeProgress = 1f
                                        nearMeStage = if (results.isNotEmpty()) "Found ${results.size} spot(s) near you!" else "No dig sites found nearby."
                                        kotlinx.coroutines.delay(300)
                                        isSearchingNearMe = false
                                        nearMeStage = ""
                                    }
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        if (isSearchingNearMe) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Citrine,
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.NearMe,
                                    contentDescription = "Search near me",
                                    tint = Citrine,
                                    modifier = Modifier.size(14.dp),
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "Search Near Me",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Citrine,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
            // Inline search results — replace the nearby list when searching or results are available
            if (isSearchingNearMe || nearMeResults.isNotEmpty() || nearMeError) {
                if (isSearchingNearMe) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                            ) {
                                Text(
                                    text = nearMeStage.ifBlank { "Searching nearby…" },
                                    style = MaterialTheme.typography.titleSmall,
                                    color = DarkTextHigh,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                )
                                Spacer(Modifier.height(12.dp))
                                LinearProgressIndicator(
                                    progress = { nearMeProgress },
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = Citrine,
                                    trackColor = Citrine.copy(alpha = 0.2f),
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Searching within ${nearMeSearchRadius} miles of ${nearMeSearchArea}…",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkTextMid,
                                )
                            }
                        }
                    }
                } else if (nearMeError) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "Search failed. Check your connection and try again.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextMid,
                            )
                            Spacer(Modifier.height(12.dp))
                            SculptedTextButton(
                                text = "Retry",
                                onClick = {
                                    nearMeError = false
                                    isSearchingNearMe = true
                                    nearMeResults = emptyList()
                                    nearMeSearchRadius = 50
                                    nearMeProgress = 0f
                                    nearMeStage = "Locating your position…"
                                    scope.launch {
                                        val lat = current.first
                                        val lng = current.second
                                        nearMeProgress = 0.20f
                                        nearMeStage = "Searching for nearby dig sites…"
                                        val searchArea = reverseGeocode(context, lat, lng, profile.homeRegion)
                                        nearMeSearchArea = searchArea
                                        nearMeProgress = 0.40f
                                        var results = DigSiteSearchService.searchNearLocation(lat, lng, searchArea, 50)
                                        if (results.isEmpty()) {
                                            nearMeSearchRadius = 100
                                            nearMeStage = "Widening search to 100 miles…"
                                            nearMeProgress = 0.65f
                                            val broaderArea = searchArea.substringAfter(", ").ifBlank { searchArea }
                                            results = DigSiteSearchService.searchNearLocation(lat, lng, broaderArea, 100)
                                        }
                                        nearMeProgress = 0.90f
                                        nearMeResults = results
                                        if (results.isNotEmpty()) {
                                            DigSiteDiscoveryStore.addAll(results)
                                        }
                                        nearMeProgress = 1f
                                        nearMeStage = if (results.isNotEmpty()) "Found ${results.size} spot(s) near you!" else "No dig sites found nearby."
                                        kotlinx.coroutines.delay(300)
                                        isSearchingNearMe = false
                                        nearMeStage = ""
                                    }
                                },
                                accent = Citrine,
                            )
                        }
                    }
                } else if (nearMeResults.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "No rock-related places found within 100 miles of your location. Try browsing the full Dig Sites list.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextMid,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            )
                            Spacer(Modifier.height(12.dp))
                            SculptedTextButton(
                                text = "Browse Dig Sites",
                                onClick = { navController.navigate(Routes.LOCATIONS) },
                                accent = Citrine,
                            )
                        }
                    }
                } else {
                    items(nearMeResults) { site ->
                        SearchNearMeResultRow(
                            site = site,
                            onOpenUrl = { url ->
                                SafeLinkOpener.openUrl(context, url)
                            },
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }
                    item {
                        Text(
                            text = "These results have been saved for review — approved spots will appear on the Dig Sites map in a future update.",
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTextMid,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }
                }
            } else {
                items(nearby) { (loc, miles) ->
                    NearbyRow(
                        name = loc.name,
                        region = loc.region,
                        summary = loc.summary,
                        miles = miles,
                        emoji = loc.type.emoji,
                        onClick = { navController.navigate(Routes.location(loc.id)) },
                    )
                }
            }
            item { SectionLabel("Explore & learn") }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(infoTiles) { tile ->
                        InfoTileCard(
                            tile = tile,
                            onClick = { navController.navigate(tile.route) },
                        )
                    }
                }
            }
            item { SectionLabel("Featured specimens") }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(featuredSpecimens) { spec ->
                        FeaturedSpecimenCard(
                            specimen = spec,
                            onClick = { navController.navigate(Routes.specimen(spec.id)) },
                            onPhotoClick = { urls: List<String>, page: Int ->
                                viewerUrls = urls
                                viewerInitialPage = page
                            },
                        )
                    }
                }
            }
            item { SectionLabel("Mohs hardness scale") }
            item {
                MohsHardnessCard(
                    onPhotoClick = { urls: List<String>, page: Int ->
                        viewerUrls = urls
                        viewerInitialPage = page
                    },
                )
            }
            // Donation card with dropdown — hidden for Premium subscribers
            if (!isPremium) {
                item {
                    DonationCard(
                        purchaseManager = purchaseManager,
                        isPurchasing = isPurchasing,
                        onDonate = { packageId ->
                            val activity = context as? android.app.Activity
                            if (activity != null) {
                                scope.launch {
                                    runCatching {
                                        val result = purchaseManager.purchaseDonation(activity, packageId)
                                        if (result is PurchaseResult.Success) {
                                            val amount = com.rork.rockscout.data.IapConfig.PRESET_DONATIONS.firstOrNull {
                                                it.packageId == packageId
                                            }?.displayAmount?.toDouble() ?: 1.0
                                            celebrationLevel = CelebrationLevel.forAmount(amount)
                                            val tokens = com.rork.rockscout.data.IapConfig.DONATION_TOKEN_GRANT[packageId] ?: 0
                                            val days = com.rork.rockscout.data.IapConfig.donationLocationDays(packageId)
                                            kotlinx.coroutines.delay(2000)
                                            celebrationLevel = null
                                            navController.navigate(Routes.thankYou(tokens, days))
                                        }
                                    }
                                }
                            }
                        },
                    )
                }
            }
            // Review Us card — always visible, below donation
            item {
                ReviewUsCard(
                    onClick = {
                        val activity = context as? android.app.Activity
                        if (activity != null) {
                            scope.launch {
                                runCatching { reviewManager.requestReview(activity) }
                            }
                        }
                    },
                )
            }
            // Version text — 5 quick taps reveal the Developer Console PIN pad
            item {
                val versionName = remember {
                    runCatching {
                        context.packageManager
                            .getPackageInfo(context.packageName, 0)
                            .versionName
                    }.getOrDefault("1.1")
                }
                Text(
                    text = "RockScout v${versionName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextLow.copy(alpha = 0.45f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val now = System.currentTimeMillis()
                            if (now - versionFirstTapMs > 3000L) {
                                // Reset window
                                versionTapCount = 1
                                versionFirstTapMs = now
                            } else {
                                versionTapCount++
                                if (versionTapCount >= 5) {
                                    showPinPad = true
                                    versionTapCount = 0
                                    versionFirstTapMs = 0L
                                }
                            }
                        }
                        .padding(vertical = 16.dp),
                )
            }
        }

            if (viewerUrls.isNotEmpty()) {
                FullScreenImageViewer(
                    imageUrls = viewerUrls,
                    initialPage = viewerInitialPage,
                    onDismiss = { viewerUrls = emptyList() },
                )
            }
        }

        // Random review popup — only for non-premium users
        RandomReviewPopup(
            isPremium = isPremium,
            sessionKey = "home",
        )

        // Note to fellow RockScouts dialog
        if (showFellowRockScoutsNote) {
            FellowRockScoutsNoteDialog(
                onDismiss = { showFellowRockScoutsNote = false }
            )
        }

        // Search Near Me — location permission dialog
        if (showNearMePermissionDialog) {
            AlertDialog(
                onDismissRequest = { showNearMePermissionDialog = false },
                title = { Text("Location needed") },
                text = { Text("Turn on Nearby Places and grant location permission to search for rock hunting spots near you.") },
                confirmButton = {
                    SculptedTextButton(
                        text = "Go to Settings",
                        onClick = {
                            showNearMePermissionDialog = false
                            navController.navigate(Routes.SOCIAL_SETTINGS)
                        },
                        accent = Citrine,
                    )
                },
                dismissButton = {
                    SculptedTextButton(
                        text = "Cancel",
                        onClick = { showNearMePermissionDialog = false },
                        accent = DarkTextMid,
                    )
                },
            )
        }

        // Field Camera dialog — launched from the Field Camera tile
        if (showFieldCamera) {
            FieldCameraDialog(
                onDismiss = { showFieldCamera = false },
                onShareToProfile = { imageUri, name, location ->
                    showFieldCamera = false
                    shareToProfileFieldCapture = Triple(imageUri, name, location)
                },
                onSubmitSpecimen = { _ ->
                    showFieldCamera = false
                    navController.navigate(Routes.SPECIMEN_LIST)
                },
                onOpenFieldJournal = {
                    showFieldCamera = false
                    navController.navigate(Routes.FIELD_JOURNAL)
                },
            )
        }

        // First-launch trial info popup — blocking, must tap Confirm
        if (shouldShowTrialInfo) {
            TrialInfoPopup(
                onConfirm = { accessManager.markTrialInfoShown() },
            )
        }
    }

        // Thank-you celebration overlay (after a successful donation)
        celebrationLevel?.let { level ->
            ThankYouCelebration(
                level = level,
                onDismiss = { celebrationLevel = null },
            )
        }

        // Achievement celebration overlay (level-up / badge-earn)
        achievementCelebration?.let { celeb ->
            LevelUpCelebration(
                celebration = celeb,
                onShare = {
                    // Reuse the share-card flow with a clean level-up / badge card.
                    scope.launch {
                        runCatching {
                            com.rork.rockscout.ui.components.ShareCardImage.share(
                                context = context,
                                title = if (celeb.type == "level_up") "LEVEL UP!" else "BADGE EARNED!",
                                subtitle = celeb.detail,
                                body = "Posted from RockScout",
                                accentHex = 0xFFE8A33D,
                                fileName = "rockscout_${celeb.type}_${System.currentTimeMillis()}",
                            )
                        }
                    }
                },
                onShareToProfile = {
                    shareToProfileCelebration = (if (celeb.type == "level_up") "LEVEL UP!" else "BADGE EARNED!") to celeb.detail
                },
                onDismiss = {
                    achievementCelebration = null
                    com.rork.rockscout.data.AchievementsRepository.clearCelebration()
                },
            )
        }

        // Achievement-completion celebration overlay (75-achievement catalog)
        completedAchievement?.let { achData ->
            AchievementCelebration(
                data = achData,
                onShare = {
                    scope.launch {
                        runCatching {
                            com.rork.rockscout.ui.components.ShareCardImage.share(
                                context = context,
                                title = "ACHIEVEMENT UNLOCKED!",
                                subtitle = achData.name,
                                body = "${achData.description} (+${achData.rewardXp} XP) — Posted from RockScout",
                                accentHex = 0xFF9B7BD8,
                                fileName = "rockscout_achievement_${System.currentTimeMillis()}",
                            )
                        }
                    }
                },
                onShareToProfile = {
                    shareToProfileAchievement = achData
                },
                onDismiss = {
                    completedAchievement = null
                    com.rork.rockscout.data.AchievementsRepository.clearAchievementCelebration()
                },
            )
        }

        // Badge-earn celebration overlay (31-badge catalog)
        earnedBadge?.let { badgeData ->
            BadgeCelebration(
                data = badgeData,
                onShare = {
                    scope.launch {
                        runCatching {
                            com.rork.rockscout.ui.components.ShareCardImage.share(
                                context = context,
                                title = "BADGE EARNED!",
                                subtitle = badgeData.name,
                                body = "${badgeData.description} — Posted from RockScout",
                                accentHex = 0xFFE8A33D,
                                fileName = "rockscout_badge_${System.currentTimeMillis()}",
                            )
                        }
                    }
                },
                onShareToProfile = {
                    shareToProfileBadge = badgeData
                },
                onDismiss = {
                    earnedBadge = null
                    com.rork.rockscout.data.AchievementsRepository.clearBadgeCelebration()
                },
            )
        }

        // Referral reward celebration — new user's welcome gift confirmed
        pendingNewUserReward?.let { reward ->
            ReferralRewardCelebration(
                data = reward,
                onDismiss = {
                    ReferralRepository.clearPendingNewUserReward()
                },
            )
        }

        // Referral sender reward celebration — friends completed referrals
        pendingSenderReward?.let { reward ->
            ReferralSenderRewardCelebration(
                data = reward,
                onDismiss = {
                    ReferralRepository.clearPendingSenderReward()
                },
            )
        }

        // Developer Console PIN pad — shown after 5 taps on version text
        if (showPinPad) {
            PinPadOverlay(
                correctPin = "081311",
                onUnlock = {
                    showPinPad = false
                    // After correct PIN, email the verification code
                    smsVerifying = true
                    smsError = null
                    scope.launch {
                        devHintCode = runCatching { sendDevVerificationCode(context) }.getOrNull()
                        smsVerifying = false
                        showSmsVerify = true
                    }
                },
                onDismiss = { showPinPad = false },
            )
        }

        if (showSmsVerify) {
            DevSmsVerifyOverlay(
                isVerifying = smsVerifying,
                isResending = isResending,
                resendSentAtMillis = resendSentAtMillis,
                resendJustSent = resendJustSent,
                error = smsError,
                hintCode = if (com.rork.rockscout.data.NotificationHelper.hasNotificationPermission(context)) null else devHintCode,
                onVerify = { code ->
                    smsVerifying = true
                    smsError = null
                    scope.launch {
                        val verified = runCatching { verifyDevSmsCode(code) }.getOrDefault(false)
                        smsVerifying = false
                        if (verified) {
                            showSmsVerify = false
                            navController.navigate(Routes.DEV_CONSOLE)
                        } else {
                            smsError = "Invalid or expired code. Try again."
                        }
                    }
                },
                onResend = {
                    if (isResending) return@DevSmsVerifyOverlay
                    isResending = true
                    smsError = null
                    resendJustSent = false
                    scope.launch {
                        devHintCode = runCatching { sendDevVerificationCode(context) }.getOrNull()
                        isResending = false
                        resendSentAtMillis = System.currentTimeMillis()
                        resendJustSent = true
                    }
                },
                onDismiss = { showSmsVerify = false },
            )
        }

        // Share-to-profile composers for celebration events
        shareToProfileCelebration?.let { (title, detail) ->
            ShareToProfileComposer(
                sourceType = "levelup",
                title = title,
                tagline = detail,
                imageUri = null,
                locationText = "",
                onDismiss = { shareToProfileCelebration = null },
            )
        }
        shareToProfileAchievement?.let { ach ->
            ShareToProfileComposer(
                sourceType = "achievement",
                title = "Achievement: ${ach.name}",
                tagline = ach.description,
                imageUri = null,
                locationText = "+${ach.rewardXp} XP",
                onDismiss = { shareToProfileAchievement = null },
            )
        }
        shareToProfileBadge?.let { badge ->
            ShareToProfileComposer(
                sourceType = "badge",
                title = "Badge: ${badge.name}",
                tagline = badge.description,
                imageUri = null,
                locationText = "",
                onDismiss = { shareToProfileBadge = null },
            )
        }
        shareToProfileFieldCapture?.let { (imageUri, name, location) ->
            ShareToProfileComposer(
                sourceType = "field_capture",
                title = name.ifBlank { "Field Find" },
                tagline = location.ifBlank { "Posted from the field" },
                imageUri = imageUri,
                locationText = location,
                onDismiss = { shareToProfileFieldCapture = null },
            )
        }
}

@Composable
private fun HomeTagline(
    onNoteClick: () -> Unit,
    onHowToUseClick: () -> Unit = {},
    onContactUsClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Top row: Contact Us, How to Use, and Night Vision toggle.
        // The Legal button lives on the Profile / Social Settings screen.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ContactUsPillButton(
                onClick = onContactUsClick,
                modifier = Modifier.weight(1f),
            )
            HowToUsePillButton(
                onClick = onHowToUseClick,
                modifier = Modifier.weight(1f),
            )
            NightVisionPillButton()
        }
        // Note to Fellow RockScouts button below the pills
        NoteToFellowRockScoutsButton(
            onClick = onNoteClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun HomeHeader(
    avatar: String,
    avatarImagePath: String? = null,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    tokenBalance: Int = 0,
    isPremium: Boolean = false,
    onTokenBankClick: () -> Unit = {},
    isSignedIn: Boolean = false,
    hunterStatus: HunterStatus = HunterStatus.OFF_GRID,
    isTokenUnlocked: Boolean = false,
    onStatusChange: (HunterStatus) -> Unit = {},
    onSocialClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onMessageRequestsClick: () -> Unit = {},
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repo = remember { AppRepository.instance }
    val notificationRepo = remember { com.rork.rockscout.data.NotificationRepository.instance }
    val unreadCount by notificationRepo.unreadCount.collectAsStateWithLifecycle()
    val socialRepo = remember { SocialRepository.instance }
    val messagingCount by socialRepo.totalMessagingCount.collectAsStateWithLifecycle()
    val purchaseManager = remember { PurchaseManager.instance }
    val syncStatus by purchaseManager.syncStatus.collectAsStateWithLifecycle()
    val entitlementSynced by purchaseManager.entitlementSynced.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        notificationRepo.loadNotifications()
        socialRepo.loadRequests()
        socialRepo.loadThreads()
    }

    // Entitlement sync confirmation toast — shown once per app session. The flag
    // lives in PurchaseManager so it survives LazyColumn scroll-driven
    // recompositions of the header item.
    androidx.compose.runtime.LaunchedEffect(entitlementSynced) {
        if (entitlementSynced && !purchaseManager.entitlementSyncToastShown) {
            purchaseManager.entitlementSyncToastShown = true
            Toast.makeText(context, "Premium entitlement synced across devices", Toast.LENGTH_SHORT).show()
        }
    }
    var statusCooldownToast by remember { mutableStateOf<String?>(null) }
    androidx.compose.runtime.LaunchedEffect(statusCooldownToast) {
        if (statusCooldownToast != null) {
            kotlinx.coroutines.delay(2500)
            statusCooldownToast = null
        }
    }
    val transition = rememberInfiniteTransition(label = "avatarGlow")
    val glow by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(tween(2800), RepeatMode.Reverse),
        label = "avatarGlowAlpha",
    )
    var statusMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            // 1. Profile avatar with status pill directly underneath.
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .sculpted(
                            shape = CircleShape,
                            accent = Citrine,
                            shadowElevation = 8.dp,
                            circular = true,
                            onClick = onProfileClick,
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Citrine.copy(alpha = glow), Aqua.copy(alpha = 0.2f), Color.Transparent)
                            )
                        )
                        .glowingBorder(5.dp, Cyan, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(Citrine.copy(alpha = 0.55f), Aqua.copy(alpha = 0.4f)))
                            )
                            .glowingBorder(4.dp, Cyan.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (!avatarImagePath.isNullOrBlank()) {
                            AsyncImage(
                                model = avatarImagePath,
                                contentDescription = "Profile picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            AnimatedAvatarIcon(
                                emoji = avatar,
                                size = 50.dp,
                                style = MaterialTheme.typography.headlineSmall,
                            )
                        }
                    }
                }

            }

            Spacer(Modifier.width(4.dp))

            // 2. Notification bell with unread badge.
            BadgeIconButton(
                icon = Icons.Filled.Notifications,
                contentDescription = "Notifications",
                count = unreadCount,
                onClick = onNotificationsClick,
            )

            Spacer(Modifier.width(4.dp))

            // 3. Message icon with total unread messages + requests count.
            BadgeIconButton(
                icon = Icons.Filled.Mail,
                contentDescription = "Messages",
                count = messagingCount,
                onClick = onMessageRequestsClick,
            )

            Spacer(Modifier.width(4.dp))

            // 4. Compact search bar with magnifying glass — fills the remaining space.
            MiniSearchBar(
                onClick = onSearchClick,
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
            )

            Spacer(Modifier.width(4.dp))

            // 5. Token bank (all users — shows ∞ when unlocked).
            TokenBank(
                tokenBalance = tokenBalance,
                onClick = onTokenBankClick,
                isUnlocked = isTokenUnlocked,
            )

            // 6. Sync-status pill — shows entitlement sync state for premium users.
            if (isPremium) {
                Spacer(Modifier.width(4.dp))
                SyncStatusPill(syncStatus = syncStatus)
            }
        }

        // RockScout Social button + full status dropdown — only for signed-in users.
        // The full text dropdown sits in the left "blue box" area, aligned to the same
        // height as the social button so the row feels balanced.
        if (isSignedIn) {
            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HunterStatusDropdown(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    isCompact = false,
                    hunterStatus = hunterStatus,
                    isPremium = isPremium,
                    onCooldown = { mins ->
                        statusCooldownToast = "Wait ${mins}m before changing status again (Premium waives this)"
                    },
                )
                RockScoutSocialButton(
                    onClick = onSocialClick,
                    modifier = Modifier.fillMaxHeight(),
                )
            }
        }
    }
}

@Composable
private fun RockScoutSocialButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
            .sculpted(
                shape = RoundedCornerShape(12.dp),
                accent = Cyan,
                shadowElevation = 6.dp,
                onClick = onClick,
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Cyan,
                        CyanDeep,
                    )
                )
            )
            .glowingBorder(3.dp, Cyan.copy(alpha = 0.95f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Icon(
            Icons.Filled.Group,
            contentDescription = "RockScout Social",
            tint = Ink,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = "RockScout\nSocial",
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = Ink,
                lineHeight = androidx.compose.ui.unit.TextUnit(13f, androidx.compose.ui.unit.TextUnitType.Sp),
            ),
            maxLines = 2,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MiniSearchBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .sculpted(
                shape = RoundedCornerShape(23.dp),
                accent = Citrine,
                shadowElevation = 6.dp,
                onClick = onClick,
            )
            .clip(RoundedCornerShape(23.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Obsidian.copy(alpha = 0.85f),
                        Slate900.copy(alpha = 0.9f),
                    )
                )
            )
            .glowingBorder(3.dp, Cyan.copy(alpha = 0.9f), RoundedCornerShape(23.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Icon(
            Icons.Filled.Search,
            contentDescription = "Search",
            tint = Aqua,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = "Search",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = TextMid,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SyncStatusPill(syncStatus: PurchaseManager.SyncStatus) {
    val (color, label, icon) = when (syncStatus) {
        PurchaseManager.SyncStatus.SYNCED -> Triple(Color(0xFF5CC98C), "Synced", Icons.Filled.CloudDone)
        PurchaseManager.SyncStatus.SYNCING -> Triple(Citrine, "Syncing…", Icons.Filled.CloudUpload)
        PurchaseManager.SyncStatus.FAILED -> Triple(Color(0xFFE2574C), "Not synced", Icons.Filled.CloudOff)
        PurchaseManager.SyncStatus.IDLE -> Triple(Color(0xFF8A8270), "—", Icons.Filled.Cloud)
    }
    Box(
        modifier = Modifier
            .size(44.dp)
            .sculpted(
                shape = CircleShape,
                accent = color,
                shadowElevation = 4.dp,
                circular = true,
            )
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f))
            .glowingBorder(2.dp, color.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Entitlement sync: $label",
            tint = color,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun HomeGreeting(name: String) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val updateInfo by UpdateManager.updateInfo.collectAsStateWithLifecycle()
    val apkStatus by ApkInstaller.status.collectAsStateWithLifecycle()
    val apkProgress by ApkInstaller.progress.collectAsStateWithLifecycle()
    val apkSize by ApkInstaller.downloadSize.collectAsStateWithLifecycle()
    val apkError by ApkInstaller.error.collectAsStateWithLifecycle()
    val playReady by PlayUpdateManager.readyToInstall.collectAsStateWithLifecycle()

    when (apkStatus) {
        // Android 8+ requires explicit consent before an app can install another
        // APK. Without this prompt the update silently never installs — so send
        // the user straight to the right Settings screen; MainActivity.onResume
        // resumes the download automatically once it's granted.
        ApkInstaller.Status.NEEDS_INSTALL_PERMISSION -> InstallPermissionDialog(
            onAllow = { ApkInstaller.openInstallPermissionSettings(context) },
            onDismiss = { ApkInstaller.reset() },
        )

        ApkInstaller.Status.DOWNLOADING,
        ApkInstaller.Status.VERIFYING,
        ApkInstaller.Status.INSTALLING,
        -> UpdateProgressDialog(
            status = apkStatus,
            progress = apkProgress,
            sizeLabel = apkSize,
        )

        // Friendly signing-conflict dialog — shown when a downloaded update APK
        // is signed with a different key than the installed build. Instead of the
        // system "App not installed" dialog, we apologize and offer to uninstall
        // the old version so the new one can install cleanly.
        ApkInstaller.Status.SIGNING_CONFLICT -> SigningConflictDialog(
            onUninstall = {
                ApkInstaller.launchUninstall(context)
                ApkInstaller.reset()
            },
            onDismiss = {
                ApkInstaller.reset()
                UpdateManager.openStore(context)
            },
        )

        ApkInstaller.Status.FAILED -> UpdateFailedDialog(
            message = apkError ?: "The update couldn't be installed.",
            onRetry = { ApkInstaller.retry(context) },
            onDismiss = { ApkInstaller.reset() },
        )

        ApkInstaller.Status.DONE, ApkInstaller.Status.IDLE -> Unit
    }

    // Google Play finished downloading an update in the background — one tap
    // restarts the app into the new version.
    if (playReady) {
        UpdateReadyDialog(
            onRestart = { PlayUpdateManager.completeUpdate() },
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = "Welcome back,",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextHigh,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = name,
                modifier = Modifier.weight(1f, fill = false),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = TextHigh,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.45f),
                        offset = Offset(0f, 2f),
                        blurRadius = 4f,
                    ),
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            // Inline "Update Now" button — tries an in-app self-update when a
            // direct APK URL is available, otherwise opens the Play Store listing.
            if (updateInfo != null && apkStatus == ApkInstaller.Status.IDLE) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .sculpted(
                            shape = RoundedCornerShape(8.dp),
                            accent = Success,
                            shadowElevation = 4.dp,
                            onClick = { UpdateManager.downloadAndInstall(context) },
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .background(Success)
                        .glowingBorder(2.dp, Cyan.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Update Now",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Ink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

/**
 * Friendly, apologetic dialog shown when a downloaded update APK is signed
 * with a different key than the installed build. Android refuses to install
 * an APK over an existing app with a different signing certificate; instead
 * of letting the system show its generic "App not installed" dialog, we
 * explain what happened and offer to uninstall the old version so the new
 * one can install cleanly. A secondary action dismisses the dialog and
 * falls back to the Play Store listing.
 */
@Composable
private fun SigningConflictDialog(
    onUninstall: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Update can't be installed",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextHigh,
            )
        },
        text = {
            Text(
                "We're sorry — this update is signed with a different key than the " +
                    "version currently installed on your device, so Android won't let " +
                    "it install over the existing app.\n\n" +
                    "To update, uninstall the old version first, then install the new " +
                    "one. Your account, collection, and saved data are stored online and " +
                    "will be restored when you sign back in.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
            )
        },
        confirmButton = {
            SculptedTextButton(
                text = "Uninstall old version",
                onClick = onUninstall,
                accent = Danger,
            )
        },
        dismissButton = {
            SculptedTextButton(
                text = "Open Play Store",
                onClick = onDismiss,
                accent = Citrine,
            )
        },
    )
}

/**
 * Shown when the app hasn't been granted permission to install packages.
 * Android silently blocks the install without this, which is the single most
 * common reason a sideloaded update "just doesn't install".
 */
@Composable
private fun InstallPermissionDialog(
    onAllow: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "One quick permission",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextHigh,
            )
        },
        text = {
            Text(
                "To install this update, Android needs your permission for RockScout " +
                    "to install app updates.\n\n" +
                    "Tap Open Settings, switch on \u201CAllow from this source\u201D, then come " +
                    "back \u2014 the update will continue on its own.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
            )
        },
        confirmButton = {
            SculptedTextButton(
                text = "Open Settings",
                onClick = onAllow,
                accent = Success,
            )
        },
        dismissButton = {
            SculptedTextButton(
                text = "Not now",
                onClick = onDismiss,
                accent = Citrine,
            )
        },
    )
}

/** Live download / verify / install progress for the in-app update. */
@Composable
private fun UpdateProgressDialog(
    status: ApkInstaller.Status,
    progress: Int,
    sizeLabel: String,
) {
    val heading = when (status) {
        ApkInstaller.Status.DOWNLOADING -> "Downloading update"
        ApkInstaller.Status.VERIFYING -> "Checking the update"
        else -> "Installing update"
    }
    val detail = when (status) {
        ApkInstaller.Status.DOWNLOADING ->
            if (sizeLabel.isNotEmpty()) "$progress% of $sizeLabel" else "$progress%"
        ApkInstaller.Status.VERIFYING -> "Making sure the download is complete and genuine\u2026"
        else -> "Confirm the install when Android asks \u2014 your data stays exactly where it is."
    }
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                heading,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextHigh,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (status == ApkInstaller.Status.DOWNLOADING) {
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.fillMaxWidth(),
                        color = Success,
                    )
                } else {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Success,
                    )
                }
                Text(
                    detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMid,
                )
            }
        },
        confirmButton = {},
    )
}

/** Update failed for a recoverable reason — offer a one-tap retry. */
@Composable
private fun UpdateFailedDialog(
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Update didn't finish",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextHigh,
            )
        },
        text = {
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
            )
        },
        confirmButton = {
            SculptedTextButton(
                text = "Try again",
                onClick = onRetry,
                accent = Success,
            )
        },
        dismissButton = {
            SculptedTextButton(
                text = "Later",
                onClick = onDismiss,
                accent = Citrine,
            )
        },
    )
}

/** Google Play downloaded an update in the background — restart to apply it. */
@Composable
private fun UpdateReadyDialog(onRestart: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                "Update ready",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextHigh,
            )
        },
        text = {
            Text(
                "A new version of RockScout has finished downloading. Restart now to " +
                    "start using it \u2014 your collection and settings are untouched.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
            )
        },
        confirmButton = {
            SculptedTextButton(
                text = "Restart & install",
                onClick = onRestart,
                accent = Success,
            )
        },
    )
}

@Composable
private fun IdentifyHero(onClick: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "glow")
    val glow by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Reverse),
        label = "glowAlpha",
    )
    val note = "Try to take pictures in daylight and catch as much clear detail as possible for the most accurate IDs. Although this search engine is extremely thorough, blurriness or certain lighting can cause inaccuracy or false IDs."
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(248.dp)
            .sculpted(
                shape = RoundedCornerShape(26.dp),
                accent = Citrine,
                shadowElevation = 10.dp,
                onClick = onClick,
            )
            .clip(RoundedCornerShape(26.dp))
            .glowingBorder(3.dp, Cyan.copy(alpha = 0.55f), RoundedCornerShape(26.dp)),
    ) {
        // Mineral specimen background image
        AsyncImage(
            model = IDENTIFY_HERO_BACKGROUND_URL,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        // Dark scrim so the title, icon, and note stay legible — lightened so
        // the mineral specimen background photo is much more visible.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.25f),
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.52f),
                        )
                    )
                )
        )
        // Accent glow overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Citrine.copy(alpha = 0.22f + glow), Citrine.copy(alpha = 0.04f), Color.Transparent)
                    )
                ),
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .size(150.dp)
                .background(
                    Brush.radialGradient(
                        listOf(Citrine.copy(alpha = glow), Color.Transparent)
                    )
                ),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    TagChip("AI IMAGE SEARCH", color = Aqua)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Identify a rock",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.65f),
                                offset = Offset(0f, 2f),
                                blurRadius = 5f,
                            ),
                        ),
                        color = Aqua,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "Point your camera. Get an instant match.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.55f),
                                offset = Offset(0f, 1f),
                                blurRadius = 3f,
                            ),
                        ),
                        color = Color(0xFFE8E0D0),
                    )
                }
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Citrine.copy(alpha = 0.35f), Citrine.copy(alpha = 0.10f))
                            )
                        )
                        .glowingBorder(2.dp, Cyan.copy(alpha = 0.60f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.CameraAlt,
                        contentDescription = "Identify",
                        tint = Citrine,
                        modifier = Modifier.size(30.dp),
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF000000).copy(alpha = 0.5f))
                    .glowingBorder(1.dp, Cyan.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE8E0D0),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun LocationMonitorBanner(
    enabled: Boolean,
    nearbyEnabled: Boolean,
    nearestName: String?,
    nearestMiles: Double?,
    onToggle: (Boolean) -> Unit,
    onToggleNearby: (Boolean) -> Unit,
    onOpen: () -> Unit,
    locked: Boolean = false,
) {
    val locationAccent = if (enabled && !locked) Success else if (locked) Citrine else TextLow
    val nearbyAccent = if (nearbyEnabled) Aqua else TextLow
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = RoundedCornerShape(20.dp), accent = locationAccent, shadowElevation = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .glowingBorder(3.dp, Cyan.copy(alpha = 0.50f), RoundedCornerShape(20.dp))
            .padding(16.dp),
    ) {
        // Accent glow overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(100.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(locationAccent.copy(alpha = 0.18f), locationAccent.copy(alpha = 0.03f), Color.Transparent)
                    )
                ),
        )
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(locationAccent.copy(alpha = 0.35f), locationAccent.copy(alpha = 0.10f))
                            )
                        )
                        .glowingBorder(2.dp, Cyan.copy(alpha = 0.60f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = locationAccent,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (locked) "Location monitoring locked"
                        else if (enabled) "Location monitoring ON" else "Location monitoring OFF",
                        style = MaterialTheme.typography.titleMedium,
                        color = Aqua,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (locked)
                            "Free trial ended — go Premium or donate to unlock live nearby alerts."
                        else if (enabled && nearestName != null && nearestMiles != null)
                            "Nearest: $nearestName · ${nearestMiles.toInt()} mi"
                        else "Turn on to enable RockScout social, dig site tracking, trip planner routing, and more!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                    )
                }
                if (locked) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Citrine.copy(alpha = 0.18f))
                            .glowingBorder(2.dp, Cyan.copy(alpha = 0.45f), CircleShape)
                            .clickable { onToggle(true) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Lock,
                            contentDescription = "Locked",
                            tint = Citrine,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                } else {
                    androidx.compose.material3.Switch(
                        checked = enabled,
                        onCheckedChange = onToggle,
                    )
                }
            }
            // Nearby spots toggle — same box, independent of monitoring
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(nearbyAccent.copy(alpha = 0.35f), nearbyAccent.copy(alpha = 0.10f))
                            )
                        )
                        .glowingBorder(2.dp, Cyan.copy(alpha = 0.60f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Map,
                        contentDescription = null,
                        tint = nearbyAccent,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (nearbyEnabled) "Nearby spots ON" else "Nearby spots OFF",
                        style = MaterialTheme.typography.titleMedium,
                        color = Aqua,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (nearbyEnabled)
                            "Show dig sites closest to you on the locations screen."
                        else "Turn on to surface the nearest mines, shops, and digs.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                    )
                }
                androidx.compose.material3.Switch(
                    checked = nearbyEnabled,
                    onCheckedChange = onToggleNearby,
                )
            }
            if (enabled && !locked) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Monitoring stays on in the background until you turn it off.",
                    style = MaterialTheme.typography.labelMedium,
                    color = DarkTextMid,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun RockScoutSocialBanner(
    enabled: Boolean,
    isSignedIn: Boolean,
    onToggle: (Boolean) -> Unit,
    onFriends: () -> Unit,
    onScan: () -> Unit,
    onMap: () -> Unit,
) {
    val accent = if (enabled) Citrine else TextLow
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = RoundedCornerShape(20.dp), accent = accent, shadowElevation = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .glowingBorder(3.dp, Cyan.copy(alpha = 0.50f), RoundedCornerShape(20.dp))
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(100.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(accent.copy(alpha = 0.18f), accent.copy(alpha = 0.03f), Color.Transparent)
                    )
                ),
        )
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(accent.copy(alpha = 0.35f), accent.copy(alpha = 0.10f))
                            )
                        )
                        .glowingBorder(2.dp, Cyan.copy(alpha = 0.60f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Group, contentDescription = null, tint = accent)
                }
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF0D0C08).copy(alpha = 0.72f),
                                    Color(0xFF0D0C08).copy(alpha = 0.52f),
                                    Color(0xFF0D0C08).copy(alpha = 0.28f),
                                )
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = if (enabled) "RockScout Social ON" else "RockScout Social OFF",
                        style = MaterialTheme.typography.titleMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.8f),
                                offset = Offset(0f, 1f),
                                blurRadius = 4f,
                            ),
                        ),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (enabled) "Discoverable to nearby hunters \u2014 scan, message & ping."
                        else "Turn on to scan for nearby RockScouts, message, and ping.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.7f),
                                offset = Offset(0f, 1f),
                                blurRadius = 3f,
                            ),
                        ),
                        color = Color(0xFFF5F0E6),
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "* Does not reveal your location \u2014 only populates a list of connected hunters. Pings are private \u2014 only you see yours. Share via Messenger.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.7f),
                                offset = Offset(0f, 1f),
                                blurRadius = 3f,
                            ),
                        ),
                        color = Color(0xFFE8E0D0),
                        fontWeight = FontWeight.Medium,
                    )
                }
                Spacer(Modifier.width(8.dp))
                Switch(checked = enabled, onCheckedChange = onToggle)
            }
            if (enabled) {
                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    SocialSubTile(
                        icon = Icons.Filled.Group,
                        label = "Friends & messages",
                        subtitle = null,
                        accent = Color(0xFF4CF0E8),
                        iconTint = Color(0xFFEAFFFE),
                        modifier = Modifier.weight(1f),
                        backgroundImage = R.drawable.social_tile_bg,
                        onClick = onFriends,
                    )
                    SocialSubTile(
                        icon = Icons.Filled.LocationOn,
                        label = "Scan",
                        subtitle = "Find nearby hunters",
                        accent = Color(0xFF7BEFFF),
                        iconTint = Color(0xFFEAFFFE),
                        modifier = Modifier.weight(1f),
                        backgroundImage = R.drawable.social_background,
                        onClick = onScan,
                    )
                    SocialSubTile(
                        icon = Icons.Filled.Map,
                        label = "Ping maps",
                        subtitle = null,
                        accent = Color(0xFF4CF0E8),
                        iconTint = Color(0xFFEAFFFE),
                        modifier = Modifier.weight(1f),
                        backgroundImage = R.drawable.trip_planner_background,
                        onClick = onMap,
                    )
                }
            } else if (!isSignedIn) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "Sign in under the profile tab to be discoverable to other users, scan for connected RockScouts, use the ping/meetup option, and more!",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.7f),
                            offset = Offset(0f, 1f),
                            blurRadius = 3f,
                        ),
                    ),
                    color = Color(0xFFF5F0E6),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
fun SocialSubTile(
    icon: ImageVector,
    label: String,
    subtitle: String?,
    accent: Color,
    iconTint: Color = Color.White,
    modifier: Modifier = Modifier,
    backgroundImage: Int = R.drawable.social_background,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    val iconGlow = Color(0xFF4CF0E8).copy(alpha = 0.35f)
    Box(
        modifier = modifier
            .height(118.dp)
            .sculpted(
                shape = shape,
                accent = accent,
                shadowElevation = 7.dp,
                onClick = onClick,
            )
            .clip(shape)
            .glowingBorder(3.dp, Cyan.copy(alpha = 0.55f), shape),
    ) {
        // Themed background image — each tab can use the same background as its home-page counterpart
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        // Dark scrim so icons and text read clearly over the photo background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.50f),
                            Color.Black.copy(alpha = 0.68f),
                        )
                    )
                )
        )
        // Cool top glow behind the icon
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(64.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(accent.copy(alpha = 0.22f), Color.Transparent)
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = iconGlow,
                        spotColor = iconGlow,
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(
                                iconTint.copy(alpha = 0.35f),
                                accent.copy(alpha = 0.25f),
                                Color(0xFF0A1A1F).copy(alpha = 0.85f),
                            )
                        )
                    )
                    .glowingBorder(3.dp, Cyan.copy(alpha = 0.85f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEAFFFE),
                    lineHeight = 17.sp,
                ),
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFD6EAE8),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            } else {
                // Reserve the subtitle row so the title has room to use two lines and stays centered vertically.
                Spacer(Modifier.height(14.dp))
            }
        }
    }
}

/** Card with the yooperlite beach-at-night background used for the social section.
 *  A dark scrim is applied so text and controls stay legible over the photo. */
@Composable
fun SocialCard(
    modifier: Modifier = Modifier,
    accent: Color = Citrine,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .sculpted(shape = shape, accent = accent, shadowElevation = 6.dp)
            .clip(shape)
            .glowingBorder(3.dp, Cyan.copy(alpha = 0.50f), shape),
    ) {
        Image(
            painter = painterResource(id = R.drawable.social_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.42f),
                            Color.Black.copy(alpha = 0.56f),
                            Color.Black.copy(alpha = 0.70f),
                        )
                    )
                )
        )
        // Accent glow at the top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(100.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(accent.copy(alpha = 0.18f), Color.Transparent)
                    )
                )
        )
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun LocationAndSocialBox(
    socialEnabled: Boolean,
    isSignedIn: Boolean,
    onToggleSocial: (Boolean) -> Unit,
    onFriends: () -> Unit,
    onBrowse: () -> Unit,
    onMap: () -> Unit,
    locationEnabled: Boolean,
    nearbyEnabled: Boolean,
    alertsEnabled: Boolean,
    nearbyFriendsEnabled: Boolean,
    nearbyFriendsAlertsEnabled: Boolean,
    weatherAlertsEnabled: Boolean,
    nearestName: String?,
    nearestMiles: Double?,
    locationLocked: Boolean,
    onToggleLocation: (Boolean) -> Unit,
    onToggleNearby: (Boolean) -> Unit,
    onToggleAlerts: (Boolean) -> Unit,
    onToggleNearbyFriends: (Boolean) -> Unit,
    onToggleNearbyFriendsAlerts: (Boolean) -> Unit,
    onToggleWeatherAlerts: (Boolean) -> Unit,
) {
    val accent = if (socialEnabled) Citrine else TextLow
    val locationAccent = if (locationEnabled && !locationLocked) Success else if (locationLocked) Citrine else TextLow
    val nearbyAccent = if (nearbyEnabled) Aqua else TextLow
    val alertsAccent = if (alertsEnabled) Citrine else TextLow
    val friendsAccent = if (nearbyFriendsEnabled) Aqua else TextLow
    val friendsAlertsAccent = if (nearbyFriendsAlertsEnabled) Citrine else TextLow
    val weatherAccent = if (weatherAlertsEnabled) Color(0xFFFF6B3D) else TextLow
    SocialCard(
        modifier = Modifier.fillMaxWidth(),
        accent = accent,
    ) {
        Column {
            // RockScout Social toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(accent.copy(alpha = 0.35f), accent.copy(alpha = 0.10f))
                            )
                        )
                        .glowingBorder(2.dp, Cyan.copy(alpha = 0.60f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Group, contentDescription = null, tint = accent)
                }
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF0D0C08).copy(alpha = 0.72f),
                                    Color(0xFF0D0C08).copy(alpha = 0.52f),
                                    Color(0xFF0D0C08).copy(alpha = 0.28f),
                                )
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = if (socialEnabled) "RockScout Social ON" else "RockScout Social OFF",
                        style = MaterialTheme.typography.titleMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.8f),
                                offset = Offset(0f, 1f),
                                blurRadius = 4f,
                            ),
                        ),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (socialEnabled) "Discoverable to nearby hunters \u2014 scan, message & ping."
                        else "Turn on to scan for nearby RockScouts, message, and ping.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.7f),
                                offset = Offset(0f, 1f),
                                blurRadius = 3f,
                            ),
                        ),
                        color = Color(0xFFF5F0E6),
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "* Does not reveal your location \u2014 only populates a list of connected hunters. Pings are private \u2014 only you see yours. Share via Messenger.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.7f),
                                offset = Offset(0f, 1f),
                                blurRadius = 3f,
                            ),
                        ),
                        color = Color(0xFFE8E0D0),
                        fontWeight = FontWeight.Medium,
                    )
                }
                Spacer(Modifier.width(8.dp))
                Switch(checked = socialEnabled, onCheckedChange = onToggleSocial)
            }
            if (socialEnabled) {
                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    SocialSubTile(
                        icon = Icons.Filled.Group,
                        label = "Friends & messages",
                        subtitle = null,
                        accent = Color(0xFF4CF0E8),
                        iconTint = Color(0xFFEAFFFE),
                        modifier = Modifier.weight(1f),
                        backgroundImage = R.drawable.social_tile_bg,
                        onClick = onFriends,
                    )
                    SocialSubTile(
                        icon = Icons.Filled.PersonSearch,
                        label = "Browse users",
                        subtitle = null,
                        accent = Color(0xFF7BEFFF),
                        iconTint = Color(0xFFEAFFFE),
                        modifier = Modifier.weight(1f),
                        backgroundImage = R.drawable.social_background,
                        onClick = onBrowse,
                    )
                    SocialSubTile(
                        icon = Icons.Filled.Map,
                        label = "Ping maps",
                        subtitle = null,
                        accent = Color(0xFF4CF0E8),
                        iconTint = Color(0xFFEAFFFE),
                        modifier = Modifier.weight(1f),
                        backgroundImage = R.drawable.trip_planner_background,
                        onClick = onMap,
                    )
                }
            } else if (!isSignedIn) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "Sign in under the profile tab to be discoverable to other users, scan for connected RockScouts, use the ping/meetup option, and more!",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.7f),
                            offset = Offset(0f, 1f),
                            blurRadius = 3f,
                        ),
                    ),
                    color = Color(0xFFF5F0E6),
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)
            Spacer(Modifier.height(14.dp))

            // Location monitoring toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(locationAccent.copy(alpha = 0.35f), locationAccent.copy(alpha = 0.10f))
                            )
                        )
                        .glowingBorder(2.dp, Cyan.copy(alpha = 0.60f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = locationAccent,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF0D0C08).copy(alpha = 0.72f),
                                    Color(0xFF0D0C08).copy(alpha = 0.52f),
                                    Color(0xFF0D0C08).copy(alpha = 0.28f),
                                )
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = if (locationLocked) "Location monitoring locked"
                        else if (locationEnabled) "Location monitoring ON" else "Location monitoring OFF",
                        style = MaterialTheme.typography.titleMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.8f),
                                offset = Offset(0f, 1f),
                                blurRadius = 4f,
                            ),
                        ),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (locationLocked)
                            "Free trial ended \u2014 go Premium or donate to unlock live nearby alerts."
                        else if (locationEnabled && nearestName != null && nearestMiles != null)
                            "Nearest: $nearestName \u00b7 ${nearestMiles.toInt()} mi"
                        else "Turn on to enable RockScout social, dig site tracking, trip planner routing, and more!",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.7f),
                                offset = Offset(0f, 1f),
                                blurRadius = 3f,
                            ),
                        ),
                        color = Color(0xFFF5F0E6),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.width(8.dp))
                if (locationLocked) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Citrine.copy(alpha = 0.18f))
                            .glowingBorder(2.dp, Cyan.copy(alpha = 0.45f), CircleShape)
                            .clickable { onToggleLocation(true) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Lock,
                            contentDescription = "Locked",
                            tint = Citrine,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                } else {
                    androidx.compose.material3.Switch(
                        checked = locationEnabled,
                        onCheckedChange = onToggleLocation,
                    )
                }
            }

            // Nearby spots toggle
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(nearbyAccent.copy(alpha = 0.35f), nearbyAccent.copy(alpha = 0.10f))
                            )
                        )
                        .glowingBorder(2.dp, Cyan.copy(alpha = 0.60f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Map,
                        contentDescription = null,
                        tint = nearbyAccent,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF0D0C08).copy(alpha = 0.72f),
                                    Color(0xFF0D0C08).copy(alpha = 0.52f),
                                    Color(0xFF0D0C08).copy(alpha = 0.28f),
                                )
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = if (nearbyEnabled) "Nearby spots ON" else "Nearby spots OFF",
                        style = MaterialTheme.typography.titleMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.8f),
                                offset = Offset(0f, 1f),
                                blurRadius = 4f,
                            ),
                        ),
                        color = Aqua,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (nearbyEnabled)
                            "Show dig sites closest to you on the locations screen."
                        else "Turn on to surface the nearest mines, shops, and digs.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.7f),
                                offset = Offset(0f, 1f),
                                blurRadius = 3f,
                            ),
                        ),
                        color = Color(0xFFF5F0E6),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.width(8.dp))
                androidx.compose.material3.Switch(
                    checked = nearbyEnabled,
                    onCheckedChange = onToggleNearby,
                )
            }

            // Nearby alerts toggle
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(alertsAccent.copy(alpha = 0.35f), alertsAccent.copy(alpha = 0.10f))
                            )
                        )
                        .glowingBorder(2.dp, Cyan.copy(alpha = 0.60f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Notifications,
                        contentDescription = null,
                        tint = alertsAccent,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF0D0C08).copy(alpha = 0.72f),
                                    Color(0xFF0D0C08).copy(alpha = 0.52f),
                                    Color(0xFF0D0C08).copy(alpha = 0.28f),
                                )
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = if (alertsEnabled) "Nearby alerts ON" else "Nearby alerts OFF",
                        style = MaterialTheme.typography.titleMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.8f),
                                offset = Offset(0f, 1f),
                                blurRadius = 4f,
                            ),
                        ),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (alertsEnabled)
                            "You'll get a push notification when you're near a dig site, mine, or shop."
                        else "Turn on to get notified when you come near a dig site, mine, or shop.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.7f),
                                offset = Offset(0f, 1f),
                                blurRadius = 3f,
                            ),
                        ),
                        color = Color(0xFFF5F0E6),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.width(8.dp))
                androidx.compose.material3.Switch(
                    checked = alertsEnabled,
                    onCheckedChange = onToggleAlerts,
                )
            }

            // Nearby friends toggle — appears under the nearby alerts toggle
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(friendsAccent.copy(alpha = 0.35f), friendsAccent.copy(alpha = 0.10f))
                            )
                        )
                        .glowingBorder(2.dp, Cyan.copy(alpha = 0.60f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        tint = friendsAccent,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF0D0C08).copy(alpha = 0.72f),
                                    Color(0xFF0D0C08).copy(alpha = 0.52f),
                                    Color(0xFF0D0C08).copy(alpha = 0.28f),
                                )
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = if (nearbyFriendsEnabled) "Nearby friends ON" else "Nearby friends OFF",
                        style = MaterialTheme.typography.titleMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.8f),
                                offset = Offset(0f, 1f),
                                blurRadius = 4f,
                            ),
                        ),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (nearbyFriendsEnabled)
                            "Scan for connected friends within 50 miles of your location."
                        else "Turn on to monitor for nearby RockScout friends within 50 miles.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.7f),
                                offset = Offset(0f, 1f),
                                blurRadius = 3f,
                            ),
                        ),
                        color = Color(0xFFF5F0E6),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.width(8.dp))
                androidx.compose.material3.Switch(
                    checked = nearbyFriendsEnabled,
                    onCheckedChange = onToggleNearbyFriends,
                )
            }

            // Get notified when friends are nearby toggle — under the nearby friends toggle
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(friendsAlertsAccent.copy(alpha = 0.35f), friendsAlertsAccent.copy(alpha = 0.10f))
                            )
                        )
                        .glowingBorder(2.dp, Cyan.copy(alpha = 0.60f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Notifications,
                        contentDescription = null,
                        tint = friendsAlertsAccent,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF0D0C08).copy(alpha = 0.72f),
                                    Color(0xFF0D0C08).copy(alpha = 0.52f),
                                    Color(0xFF0D0C08).copy(alpha = 0.28f),
                                )
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = if (nearbyFriendsAlertsEnabled)
                            "Get notified when friends are nearby ON"
                        else "Get notified when friends are nearby OFF",
                        style = MaterialTheme.typography.titleMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.8f),
                                offset = Offset(0f, 1f),
                                blurRadius = 4f,
                            ),
                        ),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (nearbyFriendsAlertsEnabled)
                            "You'll get a push notification when a friend is within 50 miles."
                        else "Turn on to get a push notification when a friend comes within 50 miles.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.7f),
                                offset = Offset(0f, 1f),
                                blurRadius = 3f,
                            ),
                        ),
                        color = Color(0xFFF5F0E6),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.width(8.dp))
                androidx.compose.material3.Switch(
                    checked = nearbyFriendsAlertsEnabled,
                    onCheckedChange = onToggleNearbyFriendsAlerts,
                )
            }

            if (locationEnabled && !locationLocked) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Monitoring stays on in the background until you turn it off.",
                    style = MaterialTheme.typography.labelMedium.copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.7f),
                            offset = Offset(0f, 1f),
                            blurRadius = 3f,
                        ),
                    ),
                    color = Color(0xFFF5F0E6),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun FullWidthBannerTile(
    label: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    imageUrl: String? = null,
    imageRes: Int = 0,
    locked: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .sculpted(
                shape = RoundedCornerShape(20.dp),
                accent = accent,
                shadowElevation = 8.dp,
                onClick = onClick,
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .glowingBorder(3.dp, Cyan.copy(alpha = 0.50f), RoundedCornerShape(20.dp)),
    ) {
        if (locked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
            )
        }
        // Background image
        if (imageRes != 0) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF0D0C08).copy(alpha = 0.60f), Color(0xFF0D0C08).copy(alpha = 0.35f), Color(0xFF0D0C08).copy(alpha = 0.20f))
                        )
                    ),
            )
        } else if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF0D0C08).copy(alpha = 0.55f), Color(0xFF0D0C08).copy(alpha = 0.32f), Color(0xFF0D0C08).copy(alpha = 0.18f))
                        )
                    ),
            )
        }
        // Accent glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(70.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(accent.copy(alpha = 0.22f), Color.Transparent)
                    )
                ),
        )
        // Localized dark scrim behind the text row — keeps text legible without
        // dimming the entire background image.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF0A0906).copy(alpha = 0.55f), Color(0xFF0A0906).copy(alpha = 0.30f), Color.Transparent)
                    )
                ),
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(14.dp),
                        ambientColor = accent.copy(alpha = 0.40f),
                        spotColor = accent,
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(accent.copy(alpha = 0.45f), accent.copy(alpha = 0.15f))
                        )
                    )
                    .glowingBorder(3.dp, Cyan.copy(alpha = 0.75f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium.copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.7f),
                            offset = Offset(0f, 1f),
                            blurRadius = 4f,
                        ),
                    ),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.6f),
                            offset = Offset(0f, 1f),
                            blurRadius = 3f,
                        ),
                    ),
                    color = Color(0xFFE8E0D0),
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = accent.copy(alpha = 0.85f), modifier = Modifier.size(28.dp))
        }
        if (locked) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Lock, contentDescription = "Locked", tint = Color(0xFFE8A33D), modifier = Modifier.size(16.dp))
            }
        }
    }
}

/**
 * Tall home-screen Gear Guide tile that shows a scrollable preview of gear items.
 *
 * The card is 200dp tall (double the normal banner height). The header and footer
 * are clickable and open the full Gear Guide screen; the scrollable list itself
 * consumes taps so scrolling does not accidentally launch the full-screen view,
 * while individual gear rows inside the list still open their Amazon links.
 */
@Composable
private fun HomeGearGuideTile(navController: NavController) {
    val context = LocalContext.current
    val gearItems = remember { GearGuide.allItems }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .sculpted(
                shape = RoundedCornerShape(20.dp),
                accent = Aqua,
                shadowElevation = 8.dp,
                onClick = { navController.navigate(Routes.GEAR_GUIDE) },
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .glowingBorder(3.dp, Cyan.copy(alpha = 0.50f), RoundedCornerShape(20.dp)),
    ) {
        // Background image.
        AsyncImage(
            model = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/10ba26f0-a7d8-4ea7-964b-864eb46744d6.png",
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF0D0C08).copy(alpha = 0.55f), Color(0xFF0D0C08).copy(alpha = 0.32f), Color(0xFF0D0C08).copy(alpha = 0.18f))
                    )
                ),
        )
        // Accent glow.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(70.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Aqua.copy(alpha = 0.22f), Color.Transparent)
                    )
                ),
        )
        // Localized dark scrim behind the text row.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF0A0906).copy(alpha = 0.55f), Color(0xFF0A0906).copy(alpha = 0.30f), Color.Transparent)
                    )
                ),
        )
        // Header.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(14.dp),
                        ambientColor = Aqua.copy(alpha = 0.40f),
                        spotColor = Aqua,
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(Aqua.copy(alpha = 0.45f), Aqua.copy(alpha = 0.15f))
                        )
                    )
                    .glowingBorder(3.dp, Cyan.copy(alpha = 0.75f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Diamond, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Gear Guide",
                    style = MaterialTheme.typography.titleMedium.copy(
                        shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), offset = Offset(0f, 1f), blurRadius = 4f),
                    ),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Curated kits for every level — from first hunt to advanced field collecting",
                    style = MaterialTheme.typography.bodySmall.copy(
                        shadow = Shadow(color = Color.Black.copy(alpha = 0.6f), offset = Offset(0f, 1f), blurRadius = 3f),
                    ),
                    color = Color(0xFFE8E0D0),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Aqua.copy(alpha = 0.85f), modifier = Modifier.size(28.dp))
        }
        // Scrollable gear preview. pointerInput consumes taps in the list area so the
        // card's clickable does not fire while the user is scrolling.
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 68.dp, bottom = 28.dp, start = 12.dp, end = 12.dp)
                .pointerInput(Unit) { detectTapGestures { } },
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(gearItems, key = { it.id }) { item ->
                HomeGearItemRow(
                    item = item,
                    onClick = {
                        AffiliateClickTracker.recordClick(context, item.id, item.name)
                        SafeLinkOpener.openUrl(context, item.url)
                    },
                )
            }
        }
        // Footer hint.
        Text(
            text = "Tap to see the full gear guide",
            style = MaterialTheme.typography.labelSmall,
            color = Aqua.copy(alpha = 0.85f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
                .background(Color(0xFF0A0906).copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun HomeGearItemRow(item: GearItem, onClick: () -> Unit) {
    val rowShape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(rowShape)
            .background(Color(0xFF3A3830).copy(alpha = 0.92f))
            .glowingBorder(1.dp, Aqua.copy(alpha = 0.35f), rowShape)
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Aqua.copy(alpha = 0.22f))
                .glowingBorder(1.dp, Aqua.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(item.emoji, style = MaterialTheme.typography.titleSmall)
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.name,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                item.priceBand,
                style = MaterialTheme.typography.labelSmall,
                color = Aqua,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Icon(Icons.Filled.OpenInNew, contentDescription = "Open ${item.name}", tint = Aqua, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = Aqua,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 4.dp),
    )
}

/** Compact level + streak strip on the home dashboard. Has a bottom-left
 *  'View all achievements' button that opens the full Achievements screen. */
@Composable
private fun StreakLevelStrip(
    level: Int,
    tierName: String,
    tierEmoji: String,
    totalXp: Int,
    streak: Int,
    progress: Float,
    onOpenAchievements: () -> Unit,
    onTileClick: () -> Unit,
) {
    val nextLevelXp = AchievementsRepository.xpForLevel(level + 1)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .sculpted(
                shape = RoundedCornerShape(20.dp),
                accent = Citrine,
                shadowElevation = 8.dp,
            )
            .clip(RoundedCornerShape(20.dp))
            .glowingBorder(3.dp, Cyan.copy(alpha = 0.55f), RoundedCornerShape(20.dp))
            .clickable(onClick = onTileClick),
    ) {
        // Same colorful agate-slice background as the Profile level tile.
        Image(
            painter = painterResource(id = R.drawable.level_tile_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        // Dark scrim so the level text and icons read clearly over the stone background.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.Black.copy(alpha = 0.58f),
                            Color.Black.copy(alpha = 0.72f),
                            Color.Black.copy(alpha = 0.82f),
                        )
                    )
                ),
        )
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Citrine.copy(alpha = 0.9f), Aqua.copy(alpha = 0.7f))))
                        .glowingBorder(4.dp, Cyan.copy(alpha = 0.85f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        tierEmoji,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(2.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xCC1E1C16))
                        .glowingBorder(1.dp, Cyan.copy(alpha = 0.20f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text("LEVEL $level", style = MaterialTheme.typography.labelMedium, color = Citrine, fontWeight = FontWeight.Bold)
                    Text(tierName, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xE61E1C16))
                            .glowingBorder(1.5.dp, Cyan.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                    ) {
                        Text(
                            "$totalXp XP / ${nextLevelXp - totalXp} XP to level ${level + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Aqua,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
                if (streak > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xCC1E1C16))
                            .glowingBorder(1.dp, Cyan.copy(alpha = 0.20f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = Aqua, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("$streak", style = MaterialTheme.typography.titleLarge, color = Aqua, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0x55FFFFFF))
                    .glowingBorder(2.dp, Cyan.copy(alpha = 0.45f), RoundedCornerShape(4.dp)),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFFFD54F))
                        .glowingBorder(1.dp, Cyan.copy(alpha = 0.55f), RoundedCornerShape(4.dp)),
                )
            }
            Spacer(Modifier.height(10.dp))
            // Bottom-left 'View all achievements' button — the ONLY element on the
            // tile that opens the full Achievements page.
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xE61E1C16))
                    .glowingBorder(2.dp, Cyan.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                    .clickable(onClick = onOpenAchievements)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text(
                    "View all achievements",
                    style = MaterialTheme.typography.labelMedium,
                    color = Citrine,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}

@Composable
private fun DashboardTile(tile: HomeTile, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "tileGlow")
    val glow by transition.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(tween(2400 + (tile.label.hashCode() % 1000), delayMillis = 200), RepeatMode.Reverse),
        label = "tileGlowAlpha",
    )
    Box(
        modifier = modifier
            .height(175.dp)
            .sculpted(
                shape = RoundedCornerShape(20.dp),
                accent = tile.accent,
                shadowElevation = 8.dp,
                onClick = onClick,
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .glowingBorder(3.dp, Cyan.copy(alpha = 0.55f), RoundedCornerShape(20.dp)),
    ) {
        // Accent glow overlay — uses uniform aqua for the border glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(100.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF6FA8C7).copy(alpha = 0.18f + glow), Color(0xFF6FA8C7).copy(alpha = 0.04f), Color.Transparent)
                    )
                ),
        )
        // Specimen photo as full-tile background
        if (tile.imageUrl != null) {
            AsyncImage(
                model = tile.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(118.dp)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                contentScale = ContentScale.Crop,
            )
            // Light scrim over the image so the photo stays vivid and readable.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(118.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.05f), Color.Black.copy(alpha = 0.15f), Color(0xFF16140F).copy(alpha = 0.72f))
                        )
                    ),
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xFF16140F).copy(alpha = 0.78f), Color(0xFF16140F).copy(alpha = 0.96f))
                    )
                )
                .padding(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(tile.accent.copy(alpha = 0.35f), tile.accent.copy(alpha = 0.10f))
                        )
                    )
                    .glowingBorder(2.dp, Cyan.copy(alpha = 0.60f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(tile.icon, contentDescription = null, tint = tile.accent, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = tile.label,
                style = MaterialTheme.typography.titleSmall,
                color = Aqua,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
            Text(
                text = tile.subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.6f),
                        offset = Offset(0f, 1f),
                        blurRadius = 2f,
                    ),
                ),
                color = Color(0xFFE8E0D0),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
        }
    }
}

/** Reverse geocode lat/lng to a human-readable "City, State, Country" string.
 *  Uses Android Geocoder on API 33+ (async), falls back to the profile
 *  homeRegion, then to a coordinate string. Must be called from a coroutine. */
private fun reverseGeocode(context: android.content.Context, lat: Double, lng: Double, homeRegion: String): String {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val geocoder = Geocoder(context)
            var resolved = ""
            geocoder.getFromLocation(lat, lng, 1) { addresses ->
                val addr = addresses.firstOrNull()
                if (addr != null) {
                    val parts = mutableListOf<String>()
                    addr.locality?.let { parts.add(it) }
                    addr.adminArea?.let { parts.add(it) }
                    addr.countryName?.let { parts.add(it) }
                    resolved = parts.joinToString(", ")
                }
            }
            // Geocoder async may not complete synchronously, wait briefly
            Thread.sleep(500)
            if (resolved.isNotBlank()) resolved else homeRegion.ifBlank { "%.2f, %.2f".format(lat, lng) }
        } else {
            @Suppress("DEPRECATION")
            val addresses = Geocoder(context).getFromLocation(lat, lng, 1)
            val addr = addresses?.firstOrNull()
            if (addr != null) {
                val parts = mutableListOf<String>()
                addr.locality?.let { parts.add(it) }
                addr.adminArea?.let { parts.add(it) }
                addr.countryName?.let { parts.add(it) }
                parts.joinToString(", ").ifBlank { homeRegion.ifBlank { "%.2f, %.2f".format(lat, lng) } }
            } else {
                homeRegion.ifBlank { "%.2f, %.2f".format(lat, lng) }
            }
        }
    } catch (_: Throwable) {
        homeRegion.ifBlank { "%.2f, %.2f".format(lat, lng) }
    }
}

@Composable
private fun NearbyRow(
    name: String,
    region: String,
    summary: String,
    miles: Double,
    emoji: String,
    onClick: () -> Unit,
) {
    val locAccent = Aqua
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(
                shape = RoundedCornerShape(20.dp),
                accent = locAccent,
                shadowElevation = 7.dp,
                onClick = onClick,
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .glowingBorder(3.dp, Cyan.copy(alpha = 0.75f), RoundedCornerShape(20.dp))
            .padding(14.dp),
    ) {
        // Accent glow overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(80.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(locAccent.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SpecimenGlyph(emoji = emoji, accent = locAccent, size = 46)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = region,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFB5AE9C),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${miles.toInt()} mi",
                        style = MaterialTheme.typography.titleMedium,
                        color = Citrine,
                    )
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = DarkTextMid)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun MohsHardnessCard(onPhotoClick: (List<String>, Int) -> Unit = { _, _ -> }) {
    data class MohsInfographic(val label: String, val url: String)

    val infographics = listOf(
        MohsInfographic("Scratch Test Chart", SpecimenImages.IMG_MOHS_SCRATCH_TEST_CHART),
        MohsInfographic("Absolute Hardness", SpecimenImages.IMG_MOHS_ABSOLUTE_HARDNESS),
        MohsInfographic("Field Test Kit", SpecimenImages.IMG_MOHS_FIELD_TEST_KIT),
        MohsInfographic("Scratch Test Steps", SpecimenImages.IMG_MOHS_SCRATCH_TEST_STEPS),
        MohsInfographic("Common Items & Hardness", SpecimenImages.IMG_MOHS_COMMON_ITEMS),
        MohsInfographic("All 10 Minerals Grid", SpecimenImages.IMG_MOHS_ALL_MINERALS_GRID),
    )

    val allUrls = infographics.map { it.url }

    val mohsAccent = Citrine
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = RoundedCornerShape(20.dp), accent = mohsAccent, shadowElevation = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .glowingBorder(3.dp, Cyan.copy(alpha = 0.45f), RoundedCornerShape(20.dp)),
    ) {
        // Accent glow overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(100.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(mohsAccent.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )
        Column {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "From Talc (1) to Diamond (10)",
                    style = MaterialTheme.typography.titleMedium,
                    color = Aqua,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "The Mohs scale classifies scratch resistance through ten reference minerals. A harder material scratches a softer one.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TagChip("1–10 scale", color = CitrineSoft)
                    TagChip("Friedrich Mohs, 1812", color = Aqua)
                }
            }

            // Swipeable pager — main infographic + reference guides/charts only
            val pagerImages = listOf(SpecimenImages.IMG_MOHS_SCALE_INFOGRAPHIC) +
                infographics.map { it.url }
            val pagerLabels = listOf("Mohs Hardness Scale") +
                infographics.map { it.label }
            // Map pager index → allUrls index for the full-screen viewer
            // allUrls = infographics(0..5)
            // pagerImages = [mainInfographic] + infographics
            fun pagerToViewerIndex(pagerIdx: Int): Int {
                return if (pagerIdx == 0) 0 // main infographic → first infographic slot
                else pagerIdx - 1 // infographics
            }
            val pagerState = rememberPagerState(pageCount = { pagerImages.size })
            val scope = rememberCoroutineScope()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp)),
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp)),
                ) { page ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onPhotoClick(allUrls, pagerToViewerIndex(page)) },
                    ) {
                        AsyncImage(
                            model = pagerImages[page],
                            contentDescription = pagerLabels[page],
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.FillWidth,
                        )
                        // Label badge at bottom-left
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                        ) {
                            Text(
                                text = pagerLabels[page],
                                style = MaterialTheme.typography.labelSmall,
                                color = Citrine,
                            )
                        }
                    }
                }

                // Left arrow — show when not on first page
                if (pagerState.currentPage > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 4.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .glowingBorder(1.dp, Cyan.copy(alpha = 0.15f), CircleShape)
                            .clickable {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChevronLeft,
                            contentDescription = "Previous",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }

                // Right arrow — show when not on last page
                if (pagerState.currentPage < pagerImages.size - 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 4.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .glowingBorder(1.dp, Cyan.copy(alpha = 0.15f), CircleShape)
                            .clickable {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = "Next",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }

            // Page indicator dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(pagerImages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(if (isSelected) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Citrine else Citrine.copy(alpha = 0.3f)
                            )
                            .glowingBorder(1.dp, Cyan.copy(alpha = 0.35f), CircleShape),
                    )
                }
            }
            Text(
                text = "Swipe or tap arrows to browse · Tap image to enlarge",
                style = MaterialTheme.typography.labelSmall,
                color = TextLow,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )
        }
    }
}

@Composable
private fun MohsMineralCard(
    number: Int,
    name: String,
    imageUrl: String,
    onClick: () -> Unit,
) {
    val numAccent = if (number >= 7) Citrine else Aqua
    Column(
        modifier = Modifier
            .width(110.dp)
            .sculpted(shape = RoundedCornerShape(14.dp), accent = numAccent, shadowElevation = 4.dp, onClick = onClick)
            .clip(RoundedCornerShape(14.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .width(110.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF1E1C16))
                .glowingBorder(1.dp, Cyan.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Mohs $number — $name",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop,
            )
            // Hardness number badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    text = number.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = if (number >= 7) Citrine else Color.White,
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            color = DarkTextMid,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun MohsInfographicCard(
    label: String,
    imageUrl: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(200.dp)
            .sculpted(shape = RoundedCornerShape(14.dp), accent = Citrine, shadowElevation = 4.dp, onClick = onClick)
            .clip(RoundedCornerShape(14.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(140.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF1E1C16))
                .glowingBorder(1.dp, Cyan.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = label,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop,
            )
            // Label badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Citrine,
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Tap to enlarge",
            style = MaterialTheme.typography.labelSmall,
            color = TextLow,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun InfoTileCard(
    tile: HomeTile,
    onClick: () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "infoTileGlow")
    val glow by transition.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            tween(2400 + (tile.label.hashCode() % 1000), delayMillis = 200),
            RepeatMode.Reverse,
        ),
        label = "infoTileGlowAlpha",
    )
    Column(
        modifier = Modifier
            .width(160.dp)
            .height(230.dp)
            .sculpted(
                shape = RoundedCornerShape(20.dp),
                accent = tile.accent,
                shadowElevation = 7.dp,
                onClick = onClick,
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F)))
            )
            .glowingBorder(3.dp, Cyan.copy(alpha = 0.75f), RoundedCornerShape(20.dp))
            .padding(12.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF1A1812))
                .glowingBorder(1.dp, Cyan.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (tile.imageUrl != null) {
                AsyncImage(
                    model = tile.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop,
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .align(Alignment.Center)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.05f), Color.Black.copy(alpha = 0.25f), Color(0xFF16140F).copy(alpha = 0.75f))
                            )
                        ),
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(tile.accent.copy(alpha = 0.40f), tile.accent.copy(alpha = 0.12f))
                        )
                    )
                    .glowingBorder(2.dp, Cyan.copy(alpha = 0.60f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(tile.icon, contentDescription = null, tint = tile.accent, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = tile.label,
            style = MaterialTheme.typography.titleSmall,
            color = Aqua,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
        )
        Text(
            text = tile.subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = DarkTextMid,
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun FeaturedSpecimenCard(
    specimen: Specimen,
    onClick: () -> Unit,
    onPhotoClick: (List<String>, Int) -> Unit = { _, _ -> },
) {
    val accent = rockClassColor(specimen.rockClass)
    val imageUrls = SpecimenImages.urls[specimen.id] ?: specimen.imageUrls

    Column(
        modifier = Modifier
            .width(140.dp)
            .height(235.dp)
            .sculpted(
                shape = RoundedCornerShape(20.dp),
                accent = accent,
                shadowElevation = 7.dp,
                onClick = onClick,
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(listOf(Color(0xFF2A2820), Color(0xFF16140F)))
            )
            .glowingBorder(3.dp, Cyan.copy(alpha = 0.75f), RoundedCornerShape(20.dp))
            .padding(12.dp),
    ) {
        Box(
            modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.radialGradient(listOf(accent.copy(alpha = 0.28f), Color(0xFF1A1812)))
            ),
        contentAlignment = Alignment.Center,
        ) {
            if (imageUrls.isNotEmpty()) {
                LongPressableImage(
                    model = imageUrls.first(),
                    contentDescription = "Photo of ${specimen.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop,
                    onClick = { onPhotoClick(imageUrls, 0) },
                    overlay = {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.45f))
                                .glowingBorder(1.dp, Cyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 5.dp, vertical = 2.dp),
                        ) {
                            Text("🔍", style = MaterialTheme.typography.labelSmall)
                        }
                        if (imageUrls.size > 1) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(alpha = 0.55f))
                                    .glowingBorder(1.dp, Cyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    "${imageUrls.size}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                )
                            }
                        }
                    },
                )
            } else {
                Text(text = specimen.emoji, style = MaterialTheme.typography.displaySmall)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = specimen.name,
            style = MaterialTheme.typography.titleSmall,
            color = Aqua,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
        )
        Text(
            text = specimen.category,
            style = MaterialTheme.typography.labelSmall,
            color = DarkTextMid,
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun NoteToFellowRockScoutsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .sculpted(
                shape = RoundedCornerShape(16.dp),
                accent = Aqua,
                shadowElevation = 5.dp,
                onClick = onClick,
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Slate900.copy(alpha = 0.75f))
            .glowingBorder(2.dp, Cyan, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.MailOutline,
            contentDescription = null,
            tint = Aqua,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = "A Note to My Fellow RockScouts",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Aqua,
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun HowToUsePillButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .sculpted(
                shape = RoundedCornerShape(16.dp),
                accent = Citrine,
                shadowElevation = 5.dp,
                onClick = onClick,
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Slate900.copy(alpha = 0.75f))
            .glowingBorder(2.dp, Cyan, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.MenuBook,
            contentDescription = null,
            tint = Citrine,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = "How to Use",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Citrine,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ContactUsPillButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .sculpted(
                shape = RoundedCornerShape(16.dp),
                accent = Color(0xFF7CB5EC),
                shadowElevation = 5.dp,
                onClick = onClick,
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Slate900.copy(alpha = 0.75f))
            .glowingBorder(2.dp, Cyan, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.MailOutline,
            contentDescription = null,
            tint = Color(0xFF7CB5EC),
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = "Contact Us",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7CB5EC),
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Compact night-vision / red-light toggle pill for the home tagline row. */
@Composable
private fun NightVisionPillButton(
    modifier: Modifier = Modifier,
) {
    val nightModeEnabled by NightModeManager.enabled.collectAsStateWithLifecycle()
    val accent = if (nightModeEnabled) Color(0xFFE2574C) else DarkTextMid
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .sculpted(
                shape = RoundedCornerShape(16.dp),
                accent = accent,
                shadowElevation = 5.dp,
                onClick = { NightModeManager.toggle() },
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Slate900.copy(alpha = 0.75f))
            .glowingBorder(2.dp, Cyan, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Icon(
            imageVector = if (nightModeEnabled) Icons.Filled.NightsStay else Icons.Filled.Brightness3,
            contentDescription = if (nightModeEnabled) "Night mode on — tap to turn off" else "Night mode off — tap to turn on",
            tint = accent,
            modifier = Modifier.size(22.dp),
        )
    }
}

/** Feature entry for the Note to Fellow RockScouts dialog. */
private data class FeatureEntry(
    val number: Int,
    val name: String,
    val description: String,
)

/** The 18 grouped feature entries shown in the dialog. */
private val fellowRockScoutsFeatures: List<FeatureEntry> = listOf(
    FeatureEntry(
        1,
        "AI Rock Identification — 3 AI Models!",
        "the biggest advantage RockScout has over every other rock app: THREE different AI models (Claude Haiku, Claude Sonnet, AND Gemini 2.5 Pro) tag-team every single identification. Most other rock apps only use 1! It performs a visual reference comparison — the AI actually SEES the database reference images alongside your photo and ranks the top matches visually (not just from text descriptions). If the visual match is highly confident (92%+), results return instantly. Otherwise it runs a web search cross-check, asks you clarifying questions (hardness, streak, environment) to narrow down tough cases, AND auto-detects assemblages — if your rock has multiple minerals, it identifies the host rock AND breaks down each component. Basically, it does everything in its power to give you the highest accuracy possible",
    ),
    FeatureEntry(
        2,
        "My Collection",
        "build your ultimate personal museum! Catalog every specimen in My Rocks, log epic field captures with the dedicated Field Camera tile (snap a photo without running the ID tool, then save it anywhere — Field Captures, Saved Images, My Rocks, Wishlist, a Field Journal entry, your Profile feed, your Profile background, or even Submit a Specimen — all from one dropdown), curate a wishlist of dream specimens you're chasing, and bookmark your favorite dig spots for instant access — now with a favorite button on every national and state park, BLM location, campground, trailhead, and dig site detail screen. Field Captures even has a second swipeable page — a full-page Specimen Map showing a pin for every capture with coordinates, so you can see every find spot on one map. Every specimen card features a glowing Yooperlite heart to like or wishlist at a glance, plus a quick-action dropdown to add to your collection, add to your wishlist, share to your profile feed, or share straight to social media",
    ),
    FeatureEntry(
        3,
        "Dig Sites, Shops & Shows",
        "your treasure map to free, public, and pay-to-dig sites, rock shops, metaphysical shops across the US, upcoming gem & mineral shows grouped by month so you never miss a hunt, AND a dedicated Gem & Mineral Shows screen browsable from the Dig Sites map. Plus a Park Here button that caches satellite tiles around your parking spot so you can navigate back to your vehicle with zero signal, tap-to-drop-pin offline map downloads on every map screen, a 3-state satellite zoom (zoom 16, zoom 19, restore), and Download Maps for All Stops on trip routes for backcountry adventures",
    ),
    FeatureEntry(
        4,
        "Specimen Database",
        "a massive encyclopedia of over 900 specimens with stunning photos, detailed properties, and where-to-find locations — your pocket field guide to the mineral kingdom. The AI identification pipeline actually sees these reference images alongside your photo for visual comparison. And it keeps growing: users can submit their own specimens for review and addition to the database!",
    ),
    FeatureEntry(
        5,
        "Submit Specimens & Add Locations",
        "found something special that isn't in the database? Use the Submit Specimen button on specimen detail pages or the database screen to send up to 10 photos plus any info you have, and after review it gets added to the Specimen Database or Rocks Are Amazing collection for every RockScout user to discover. Found a dig site, rock shop, or show that isn't on the map? Use the Add Location form on the map screens to add it — after review it appears on the Dig Sites map for every RockScout user to find. Help build the most thorough rock database and the most complete hunting map on the app market!",
    ),
    FeatureEntry(
        6,
        "Trading Community",
        "wheel and deal! Post specimens to swap or sell on the Trade Board, browse HAVE/WANT listings on the Trading Floor, and chat with interested traders to seal the deal",
    ),
    FeatureEntry(
        7,
        "Trip Planner, Calendar & Field Journal",
        "plan epic multi-stop hunt routes with gear checklists and custom map pins for any stop along the way — drop a pin for gas, food, or that unnamed creek crossing, and flag interesting pins as potential rock locations for the developer to review and add to the database. When editing a trip, drop specimen marker pins on the specimen marker map to mark exactly where you found each rock — they persist with the trip and show on the route map. Expand the route map to fullscreen and tap anywhere to drop a pin with Add to Route or Upload New Location pill buttons. Long-press and drag stop cards to reorder them in real-time, with a dashed polyline connecting all stops and estimated travel time between each one. A standalone Calendar screen on the home screen shows all planned trips in a month grid — drag and drop trip cards to reschedule them, create new trips, edit existing ones, and export from the trip planner. Completed trips can be archived to a dedicated Archived tab for safekeeping. Then log your daily adventures with auto-weather, photos, and field notes — your personal hunting chronicle",
    ),
    FeatureEntry(
        8,
        "RockScout Social",
        "join the community! Share live pings on the map, send friend requests and messages, discover fellow hunters up to 250 miles away with Premium, browse and search ALL discoverable RockScout hunters worldwide from the Discover Hunters screen on your Profile, post to the app-wide Community Q&A board (posts, comments, threaded replies, loves, reposts, image attachments, and a 14-day auto-expire with an Archived Posts popup so nothing gets lost), show off your collections and achievements on public profiles with custom background images, set your hunter status (Off Grid, Hunting, Digging, Trading) with color-coded profile borders so everyone knows if you're available or off the grid, share captures and milestones to your profile or straight to social media, and invite friends with referral links that earn tokens and XP. The unified RockScout Friends screen puts everything in one place — friend requests and message requests at the top under a Requests section, active conversations in the middle, and your connected friends below — no more jumping between tabs. Swipe left on any conversation or friend card to delete or unfriend with a confirmation popup. Color-coded status dots on conversation tiles show you who's hunting, digging, trading, or off the grid at a glance. Use checkbox selection mode to bulk-delete conversations, friends, or requests. Preview messages without triggering a read receipt, send images through the messenger, and use the Report button to flag anything inappropriate",
    ),
    FeatureEntry(
        9,
        "Achievements, Badges, XP & Leveling",
        "level up your rockhound game! Unlock over 100 achievements and over 30 badges, earn XP for every action, celebrate level-ups with confetti explosions, share brag-worthy level-up and badge cards, and watch milestone animations for referrals and donations",
    ),
    FeatureEntry(
        10,
        "Aurora Forecaster & Explore the Stars",
        "your personal space weather station and night sky guide! The Aurora Forecaster shows real-time Kp index, Bz value, solar wind speed, and visibility status with colorful northern lights theming. View 24-hour Kp and 7-day F10.7 trend charts, a 3-day forecast, and active sunspot regions with tappable detail views showing magnetic evolution history and educational content. Save custom coordinates as aurora watching spots and track visibility at each one on a pin-drop map. Set a custom Kp notification threshold and get push alerts when aurora may be visible — then share your Kp status straight to social media. The Explore the Stars tile in the Explore & Learn section is a standalone night sky guide with clickable tiles for all 88 constellations (with programmatic star charts), 30+ important stars, all 8 planets plus dwarf planets, and 40+ deep sky objects — each with hero images, detailed data, and animated twinkling white stars in every page background",
    ),
    FeatureEntry(
        11,
        "Search, Location, Wildlife & Weather Alerts",
        "find anything fast with global search across specimens, locations, guides, and now favorite spots (national and state parks, BLM locations, campgrounds, trailheads, and dig sites); discover nearby hot spots within 100 miles (or up to 250 miles with Premium); get proximity pings when you're close to a dig site; see Common Wildlife tiles on every BLM state guide, dig site, trailhead, campground, national and state park, and beach detail screen showing the animals you might encounter in that area; receive instant NWS severe weather alerts for your area (severe thunderstorm, tornado, flash flood, hurricane, tropical storm, tsunami, blizzard, winter storm, ice storm, extreme heat/cold, high wind, dust storm, dense fog, fire weather, red flag, smoke/air quality, dense smoke advisory); and tap deep links from notifications to jump straight to a specimen or location",
    ),
    FeatureEntry(
        12,
        "Notification Center & Message Icon",
        "two separate icons keep your RockScout world organized: the notification bell shows a summary tile for pending friend requests at the top, plus regular notifications like trade interests, submission approvals, post likes, and comments — each with a tappable deep link. Swipe left on any notification to delete it with a 5-second Undo pill in case you change your mind, or use checkbox selection mode to bulk-delete. The message icon (envelope, next to the bell) is your exclusive home for all message activity — it shows a count badge for unread messages and pending message requests, and tapping it opens the unified RockScout Friends screen where conversations and message requests live together",
    ),
    FeatureEntry(
        13,
        "Maps & Offline Navigation",
        "hunt with confidence even off the grid! Download cached satellite and street tiles for no-signal adventures by tapping the floating Download button on any map and dropping a pin, export high-resolution satellite map images straight to your gallery for offline reference, use the Park Here button to cache satellite tiles around your parking spot and navigate back to your vehicle with zero signal, cycle the satellite button through 3 zoom levels (16, 19, restore), and get one-tap Google Maps directions to any dig site",
    ),
    FeatureEntry(
        14,
        "Image Interactions & Saved Images",
        "tap any photo in the app to view it full-screen, long-press to save it to your personal Saved Images folder, and share saved images to your profile feed or straight to social media. Every specimen photo, field capture, and guide image is interactive — your own personal rock reference library",
    ),
    FeatureEntry(
        15,
        "Tokens & Subscriptions",
        "power your hunts with a token bank for identifications, try everything free for 7 days, unlock unlimited IDs and every feature with $5.99/mo Premium, and support the app with one-time donations that grant tokens and bonus access. Plus a Storage setting in Social Settings lets you choose Standard (150MB) or Maximum (2GB) cache size — Maximum stores all over 3,500 specimen images and every dig site map for offline use",
    ),
    FeatureEntry(
        16,
        "Gear Guide",
        "curated beginner, intermediate, and advanced gear kits plus over 45 curated rockhounding tools — loupes, rock hammers, hardness kits, UV flashlights, field notebooks, and more — with direct links to Amazon. Contextual gear recommendations appear on every specimen detail, dig site, and identification result so you always know what to bring",
    ),
    FeatureEntry(
        17,
        "Immersive 3D Design",
        "every button, card, and tile features a sculpted 3D look with layered shadows that give the app real depth and tactile feel — no flat, boring interfaces here. It looks and feels like a premium app should",
    ),
    FeatureEntry(
        18,
        "Community & Support",
        "a family-friendly community with a profanity filter and screenshot reporting to keep things clean, rate & review to help fellow rockhounders discover RockScout, and a direct line to contact us for help or feedback anytime",
    ),
    FeatureEntry(
        19,
        "Trip Calendar",
        "a standalone calendar screen accessible right from the home screen — view all planned trips in a month grid, navigate between months, create and edit trips, share trips, drag and drop trip cards to reschedule them to different dates, and see an agenda of upcoming trips. The trip planner links to it via the View in Calendar button, but the calendar is always accessible on its own for full editing and planning",
    ),
    FeatureEntry(
        20,
        "Archived Trips",
        "mark completed trips with a checkmark and archive them to a dedicated Archived tab in the Trip Planner — they stay safe without cluttering your active list, and can be restored or permanently deleted anytime",
    ),
)

/** The creator's personal note shown on an ancient-scroll parchment background. */
private val creatorNoteParagraphs: List<String> = listOf(
    "First of all, I'd like to say this is the actual creator typing this. Although I did use AI in the creation of this app, this is not AI writing this note. I assure you, I'm a real, live boy.",
    "I've been a rockhounder for 30+ years, and as every single rockhounder knows, identification can be a bit of a struggle from time to time. If you're picking up your first rock that you're going to keep forever and ever, or you've got 132,649 specimens because you keep every rock you get your hands on (like me), there's always a use for a phenomenal rock ID app. Normally, I wouldn't say phenomenal when describing much of anything (except maybe food). But if you're a rock hound, or even just the outdoorsy type, considering how much hiking and camping info there is in here as well, then that's really the only appropriate word for this. I packed as much accurate ID power and info into this thing as I could. No joke. It uses 3 different AI models, when most other rock apps only use 1. It also cross-checks with the app database images (which I put quite a bit of time into), runs a web search cross-check, and even asks you clarifying questions when a rock is tough to get a solid identification on. Basically, it does everything in its power to provide the highest accuracy possible. Plus I included a whole ton of other informational sections, and created a whole social network just for rockhounds. You can build a friends list, chat on an in-app Messenger, post questions and photos to a community-wide Q&A board, show off specimens in a postable Profile feed, haggle on a Trade Board (a marketplace for trading, selling, or buying rocks), snap quick field photos with the field kit camera, and loads more. and yes, there's image moderation, and a fairly forgiving profanity filter. The free version does not have access to any of the social aspects, so it keeps it rated G for the littles, but still gives them a ton of content to explore. And just to be clear — you don't necessarily need a subscription to get you 'money's worth' out of this thing. The free version alone gives you access to the (ad supported) rock identification engine, the entire mineral and specimen database with detailed info pages, the interactive map with dig sites and rock shops, the Field Camera, the glossary, the daily streak challenges, and plenty more to dig in to. (See what I did there?)? The free tier is ad supported, but all the ads are the most family-friendly rating I can make them. Free users can watch a couple short videos to receive a free identification token as well. No limit. The free version is still useful, but the premium version completely unlocks the highest levels of ID and everything else in the app. Literally something for all ages and levels of expertise. I do however, have to recommend 18+ due to the social aspect (which users can toggle on or off in their social settings). Gotta keep the kids safe. The adults too, for that matter, so be smart and use your own discretion if you plan on meeting up with other rockhounders. Safety always comes first.",
    "Seeing as how rockhounding is a mostly social hobby, I've added both a \"Submit Specimen\" button and an \"Add Location\" option so users can send in special or unique specimens they've found (that aren't in the current database) and dig sites, rock shops, or shows that aren't on the map yet. After review, both get added to the database and map for every RockScout user to discover. Also, if you find something in RockScout that needs to be added, fixed, made more legible, etc, please take a screenshot and email me with it (through the Contact Us tab) so I can promptly fix the issue. The OCD in me wants this app perfect, so don't hesitate to reach out with issues or suggestions.",
    "So in closing, I'd like to give a huge thank you for helping to support this app. I'll do my best to keep adding things, and tweaking other things, to make this app every rockhounders best friend. Every subscription and donation helps make this one of the most, if not THE most, accurate and thorough rock app you can find. And don't forget, the more people that join, the larger RockScouts social network becomes, so tell all your rockhounding friends to get the app!",
    "Now I'll go ahead and let AI take back over and break down everything this bad boy can do. I know it's a bit of a long read, but bear with it. You'll be happy you did. It's pretty awesome. Happy Hunting!",
)

/** Parchment scroll background with solid black text — ancient-scroll aesthetic. */
@Composable
private fun ParchmentScrollText(
    paragraphs: List<String>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .drawBehind {
                // Base parchment gradient — darker at edges, warmer in center
                drawRect(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0xFFC4B090),
                            0.08f to Color(0xFFE0D0B0),
                            0.25f to Color(0xFFEBE0C8),
                            0.5f to Color(0xFFF2E8D4),
                            0.75f to Color(0xFFEBE0C8),
                            0.92f to Color(0xFFE0D0B0),
                            1.0f to Color(0xFFC4B090),
                        ),
                    ),
                )
                // Aged vignette — darker corners
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x15000000),
                            Color(0x2A000000),
                        ),
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = size.maxDimension * 0.75f,
                    ),
                )
                // Subtle aged spots for parchment texture
                drawCircle(
                    color = Color(0x18A08050),
                    radius = size.minDimension * 0.09f,
                    center = Offset(size.width * 0.18f, size.height * 0.12f),
                )
                drawCircle(
                    color = Color(0x14A08050),
                    radius = size.minDimension * 0.07f,
                    center = Offset(size.width * 0.82f, size.height * 0.28f),
                )
                drawCircle(
                    color = Color(0x12A08050),
                    radius = size.minDimension * 0.06f,
                    center = Offset(size.width * 0.72f, size.height * 0.78f),
                )
                drawCircle(
                    color = Color(0x10A08050),
                    radius = size.minDimension * 0.05f,
                    center = Offset(size.width * 0.25f, size.height * 0.65f),
                )
            }
            .glowingBorder(
                width = 1.dp,
                color = Color(0xFFB09870).copy(alpha = 0.35f),
                shape = RoundedCornerShape(3.dp),
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Column {
            paragraphs.forEachIndexed { index, paragraph ->
                if (index > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(
                    text = paragraph,
                    color = Color(0xFF1A1A1A),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                    ),
                )
            }
        }
    }
}

@Composable
private fun FellowRockScoutsNoteDialog(
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .navigationBarsPadding(),
            shape = RoundedCornerShape(28.dp),
            color = Slate900,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "A Note to My Fellow RockScouts",
                        color = Aqua,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    SculptedIconButton(
                        icon = Icons.Filled.Close,
                        contentDescription = "Close",
                        onClick = onDismiss,
                        accent = Aqua,
                        iconTint = Aqua,
                        size = 40.dp,
                        shadowElevation = 4.dp,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                ParchmentScrollText(
                    paragraphs = creatorNoteParagraphs,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Welcome, fellow rockhounder! Here's everything RockScout brings to your adventures:",
                    color = Aqua,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(12.dp))
                fellowRockScoutsFeatures.forEach { feature ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                    ) {
                        Text(
                            text = "${feature.number}.",
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.width(28.dp),
                        )
                        Column {
                            Text(
                                text = feature.name,
                                color = TextHigh,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                            )
                            Text(
                                text = feature.description,
                                color = TextMid,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                SculptedButton(
                    text = "Close",
                    onClick = onDismiss,
                    accent = Aqua,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

/** SMS verification overlay — shown after correct dev PIN entry.
 *  Reuses the PinPadOverlay visual style with a 6-digit code entry pad. */
@Composable
private fun DevSmsVerifyOverlay(
    isVerifying: Boolean,
    isResending: Boolean,
    resendSentAtMillis: Long,
    resendJustSent: Boolean,
    error: String?,
    hintCode: String?,
    onVerify: (String) -> Unit,
    onResend: () -> Unit,
    onDismiss: () -> Unit,
) {
    var entered by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf(false) }

    // 30-second cooldown after a resend so the user can't spam the button.
    val cooldownSeconds = 30
    var nowMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(resendSentAtMillis) {
        if (resendSentAtMillis > 0L) {
            while (true) {
                nowMillis = System.currentTimeMillis()
                if (nowMillis - resendSentAtMillis >= cooldownSeconds * 1000L) break
                delay(1000L)
            }
        }
    }
    val cooldownRemaining = remember(resendSentAtMillis, nowMillis) {
        if (resendSentAtMillis == 0L) 0
        else ((cooldownSeconds * 1000L - (nowMillis - resendSentAtMillis)) / 1000).coerceAtLeast(0).toInt()
    }
    val resendDisabled = isResending || cooldownRemaining > 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.92f))
            .clickable(enabled = false, onClick = {}),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .sculpted(
                            shape = CircleShape,
                            accent = Aqua,
                            shadowElevation = 4.dp,
                            circular = true,
                            onClick = { onDismiss() },
                        )
                        .clip(CircleShape)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(Aqua.copy(alpha = 0.30f), Aqua.copy(alpha = 0.08f))
                        )
                    )
                    .glowingBorder(2.dp, Cyan.copy(alpha = 0.55f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = Aqua, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Verify Your Identity",
                style = MaterialTheme.typography.titleLarge,
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "A 6-digit code was emailed to Aaron_James_Martin@yahoo.com.",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                for (i in 0 until 6) {
                    val filled = i < entered.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (filled) {
                                    if (localError || error != null) Color(0xFFE2574C) else Aqua
                                } else {
                                    Color.White.copy(alpha = 0.15f)
                                }
                            )
                            .glowingBorder(
                                1.dp,
                                if (localError || error != null) Color(0xFFE2574C) else Aqua.copy(alpha = 0.5f),
                                CircleShape,
                            ),
                    )
                }
            }
            if (error != null) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFE2574C),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
            if (isVerifying) {
                Text(
                    text = "Verifying...",
                    style = MaterialTheme.typography.labelMedium,
                    color = Aqua,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
            Spacer(Modifier.height(32.dp))
            val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "⌫")
            keys.chunked(3).forEach { rowKeys ->
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    rowKeys.forEach { key ->
                        when (key) {
                            "" -> Box(modifier = Modifier.size(72.dp))
                            "⌫" -> Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .sculpted(
                                        shape = CircleShape,
                                        accent = Aqua,
                                        shadowElevation = 4.dp,
                                        circular = true,
                                        onClick = { if (entered.isNotEmpty()) { entered = entered.dropLast(1); localError = false } },
                                    )
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .glowingBorder(2.dp, Cyan.copy(alpha = 0.18f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Filled.Backspace, contentDescription = "Backspace", tint = DarkTextMid, modifier = Modifier.size(26.dp))
                            }
                            else -> Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .sculpted(
                                        shape = CircleShape,
                                        accent = Aqua,
                                        shadowElevation = 5.dp,
                                        circular = true,
                                        onClick = {
                                            if (entered.length < 6) {
                                                entered += key
                                                localError = false
                                                if (entered.length == 6) {
                                                    onVerify(entered)
                                                }
                                            }
                                        },
                                    )
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.10f))
                                    .glowingBorder(2.dp, Cyan.copy(alpha = 0.22f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(text = key, style = MaterialTheme.typography.headlineSmall, color = DarkTextHigh, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
            // Resend button with cooldown + success confirmation
            val resendLabel = when {
                isResending -> "Sending…"
                cooldownRemaining > 0 -> "Resend in ${cooldownRemaining}s"
                resendJustSent -> "Resend code"
                else -> "Resend code"
            }
            SculptedTextButton(
                text = resendLabel,
                onClick = {
                    if (resendDisabled) return@SculptedTextButton
                    entered = ""
                    localError = false
                    onResend()
                },
                accent = Aqua,
                textColor = Aqua,
                enabled = !resendDisabled,
            )
            if (resendJustSent && !isResending && cooldownRemaining > 0) {
                Text(
                    text = "A new code was emailed.",
                    style = MaterialTheme.typography.labelMedium,
                    color = Aqua,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

/**
 * Requests a developer verification code.
 *
 * The backend emails the code to the developer address. If email delivery isn't
 * possible it returns the code directly (`devCode`) so the developer is never
 * locked out — we then post it as a high-priority local notification and hand
 * it back to the caller so the verify overlay can show it inline when
 * notification permission hasn't been granted.
 *
 * @return the 6-digit code when the backend returned one, or null when the code
 *         was delivered by email only / the request failed.
 */
private suspend fun sendDevVerificationCode(context: android.content.Context): String? {
    return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        runCatching {
            val functionsUrl = com.rork.rockscout.data.BuildSecrets.resolve("EXPO_PUBLIC_RORK_FUNCTIONS_URL", com.rork.rockscout.data.BuildSecrets.RORK_FUNCTIONS_URL)
            if (functionsUrl.isBlank()) {
                // No backend — cannot send verification code
                return@withContext null
            }
            val payload = """{"action":"send"}"""
            val url = java.net.URL("$functionsUrl/dev-sms-verify")
            val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 10_000
                readTimeout = 15_000
                setRequestProperty("Content-Type", "application/json")
                // Required — the endpoint is app-key guarded and rejects the
                // request with 401 without this header.
                setRequestProperty(
                    "X-App-Key",
                    com.rork.rockscout.data.BuildSecrets.resolve(
                        "EXPO_PUBLIC_RORK_APP_KEY",
                        com.rork.rockscout.data.BuildSecrets.RORK_APP_KEY,
                    ),
                )
                doOutput = true
            }
            conn.outputStream.use { it.write(payload.toByteArray()) }
            val ok = conn.responseCode in 200..299
            val code = if (ok) {
                val body = conn.inputStream.bufferedReader().use { it.readText() }
                Regex("\"devCode\":\"([0-9]{6})\"").find(body)?.groupValues?.get(1)
            } else {
                android.util.Log.w("DevVerify", "Code request failed: ${conn.responseCode}")
                null
            }
            conn.disconnect()
            if (code != null) {
                com.rork.rockscout.data.NotificationHelper.showDeveloperPinNotification(context, code)
            }
            code
        }.getOrNull()
    }
}

/** Verifies the developer 2-step code by calling the Cloudflare function. */
private suspend fun verifyDevSmsCode(code: String): Boolean {
    return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        runCatching {
            val functionsUrl = com.rork.rockscout.data.BuildSecrets.resolve("EXPO_PUBLIC_RORK_FUNCTIONS_URL", com.rork.rockscout.data.BuildSecrets.RORK_FUNCTIONS_URL)
            if (functionsUrl.isBlank()) {
                // No backend — cannot verify the code
                return@withContext false
            }
            val payload = """{"action":"verify","code":"$code"}"""
            val url = java.net.URL("$functionsUrl/dev-sms-verify")
            val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 10_000
                readTimeout = 15_000
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty(
                    "X-App-Key",
                    com.rork.rockscout.data.BuildSecrets.resolve(
                        "EXPO_PUBLIC_RORK_APP_KEY",
                        com.rork.rockscout.data.BuildSecrets.RORK_APP_KEY,
                    ),
                )
                doOutput = true
            }
            conn.outputStream.use { it.write(payload.toByteArray()) }
            val ok = conn.responseCode in 200..299
            if (ok) {
                val body = conn.inputStream.bufferedReader().use { it.readText() }
                conn.disconnect()
                body.contains("\"verified\":true")
            } else {
                conn.disconnect()
                false
            }
        }.getOrDefault(false)
    }
}

/**
 * First-launch trial info popup — blocking dialog shown once after install.
 * Cannot be dismissed by back press or tapping outside — must tap Confirm.
 */
@Composable
private fun TrialInfoPopup(
    onConfirm: () -> Unit,
) {
    Dialog(
        onDismissRequest = { /* block dismiss — must tap Confirm */ },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .navigationBarsPadding(),
            shape = RoundedCornerShape(28.dp),
            color = Slate900,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Citrine.copy(alpha = 0.18f))
                        .glowingBorder(3.dp, Cyan.copy(alpha = 0.45f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Diamond,
                        contentDescription = null,
                        tint = Citrine,
                        modifier = Modifier.size(36.dp),
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Welcome to RockScout!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Aqua,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "You get a 1-week free trial with 5 identification tokens. Full access — AI rock identifier, dig sites, field captures, wishlist, favorite spots, and RockScout Friends. After 7 days, donate any amount to keep going, or go Premium for $5.99/mo. One trial per device — make it count!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMid,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(20.dp))
                SculptedButton(
                    text = "Confirm",
                    onClick = onConfirm,
                    accent = Citrine,
                    containerColor = Citrine,
                    textColor = Ink,
                    icon = Icons.Filled.Check,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                )
            }
        }
    }
}

/**
 * Dismissible trial-expired upsell banner shown at the top of the home screen.
 * Reappears on next app launch if still expired and not Premium.
 */
@Composable
private fun TrialExpiredBanner(
    onGoPremium: () -> Unit,
    onDonate: () -> Unit,
    onDismiss: () -> Unit,
) {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFFE2574C)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = null,
                        tint = Color(0xFFE2574C),
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Your free trial has ended",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onDismiss)
                        .background(Color.White.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Dismiss",
                        tint = TextMid,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Subscribe to Premium for $5.99/mo or donate any amount to keep identifying rocks and using all features. Your account, collections, and captures are saved.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SculptedButton(
                    text = "Go Premium",
                    onClick = onGoPremium,
                    accent = Citrine,
                    containerColor = Citrine,
                    textColor = Ink,
                    icon = Icons.Filled.Star,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                )
                SculptedOutlinedButton(
                    text = "Donate",
                    onClick = onDonate,
                    accent = Color(0xFFE2574C),
                    textColor = Color(0xFFE2574C),
                    icon = Icons.Filled.FavoriteBorder,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                )
            }
        }
    }
}

/**
 * Banner shown when the user's device is over the 3-device Premium limit.
 * Premium features are paused — tapping navigates to Manage Devices.
 */
@Composable
private fun DeviceLimitBanner(
    onManageDevices: () -> Unit,
) {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFFE8A33D)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onManageDevices)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                Icons.Filled.Warning,
                contentDescription = null,
                tint = Color(0xFFE8A33D),
                modifier = Modifier.size(22.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "3-device limit reached",
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "Premium is paused on this device. Tap to manage your devices.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = DarkTextMid,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/**
 * Compact banner shown at the very top of the home screen when a newer app
 * version is available. Tapping it navigates to Settings where the "Update
 * Now" button lives. Auto-dismisses when the download starts (handled by the
 * caller's visibility condition — banner only shows when apkStatus is IDLE).
 */
@Composable
private fun UpdateAvailableBanner(
    versionName: String,
    changelog: String,
    onClick: () -> Unit,
) {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Success) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Pulsing download icon to draw the eye.
            val transition = rememberInfiniteTransition(label = "update-pulse")
            val pulse by transition.animateFloat(
                initialValue = 0.7f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(900, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "pulse-alpha",
            )
            Icon(
                Icons.Filled.Download,
                contentDescription = null,
                tint = Success.copy(alpha = pulse),
                modifier = Modifier.size(22.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (versionName.isNotBlank()) "Update available — v${versionName}" else "Update available",
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (changelog.isNotBlank()) {
                    Text(
                        text = changelog,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMid,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "Go to update",
                tint = Success,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/**
 * Success banner shown on the home dashboard when the user arrives from a
 * click-to-verify email link. Displays a checkmark icon, confirmation message,
 * and auto-dismisses after 6 seconds (handled by the caller's LaunchedEffect).
 */
@Composable
private fun EmailVerifiedBanner(
    onDismiss: () -> Unit,
) {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Success) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Success.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.MarkEmailRead,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Email verified!",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Your RockScout account is now active. Welcome aboard!",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMid,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onDismiss)
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Dismiss",
                    tint = TextMid,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

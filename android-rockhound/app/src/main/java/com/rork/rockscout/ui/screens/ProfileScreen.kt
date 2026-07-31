package com.rork.rockscout.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.items
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import android.net.Uri
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rork.rockscout.data.PurchaseManager
import androidx.navigation.NavController
import com.rork.rockscout.data.AchievementsRepository
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AchievementCatalog
import com.rork.rockscout.data.Achievement
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.IdentifyAccessManager
import com.rork.rockscout.data.NotificationRepository
import com.rork.rockscout.data.PostRepository
import com.rork.rockscout.data.ReferralRepository
import com.rork.rockscout.ui.components.AnimatedAvatarIcon
import com.rork.rockscout.ui.components.BadgeIconButton
import com.rork.rockscout.ui.components.postImageNavigation
import com.rork.rockscout.data.SeedData
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import com.rork.rockscout.data.RegionData
import com.rork.rockscout.data.LevelTier
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.data.WorkScheduler
import com.rork.rockscout.data.SettingsBackupWorker
import com.rork.rockscout.data.SettingsBackupApi
import com.rork.rockscout.data.PersistenceManager
import com.rork.rockscout.data.UserDateFormatter
import androidx.compose.material3.LinearProgressIndicator
import com.rork.rockscout.data.ProfanityFilter
import com.rork.rockscout.data.ImageModerator
import com.rork.rockscout.data.ModerationResult
import com.rork.rockscout.data.ModerationTriState
import com.rork.rockscout.data.ImageUtils
import com.rork.rockscout.data.ImageReviewRepository
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.components.CreatePostButton
import com.rork.rockscout.ui.components.CreatePostSheet
import com.rork.rockscout.ui.components.DeleteConfirmDialog
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.EmptyPostBox
import com.rork.rockscout.ui.components.SavedImagesPickerDialog
import com.rork.rockscout.ui.components.HunterStatusDropdown
import com.rork.rockscout.ui.components.HunterStatusIcon
import com.rork.rockscout.data.HunterStatus
import com.rork.rockscout.ui.components.PostCard
import com.rork.rockscout.ui.components.ProfileStatBar
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.ArchivedPostsPopup
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.StatTile
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.R
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Cyan
import com.rork.rockscout.ui.theme.CyanDeep
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.StoneLine
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.components.profileBorderColor
import com.rork.rockscout.ui.components.statusAccent
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.glowingBorder

private val avatarOptions = listOf(
    "\uD83E\uDD20", "\u26CF\uFE0F", "\uD83D\uDC8E", "\uD83E\uDEA8", "\uD83D\uDD2E",
    "\uD83C\uDF0B", "\uD83E\uDDD7", "\uD83D\uDC68\u200D\uD83D\uDD2C", "\uD83C\uDF0D",
    "\u26F0\uFE0F", "\uD83C\uDFD4\uFE0F", "\uD83D\uDCA0", "\uD83D\uDD2C",
    "\u2692\uFE0F", "\uD83E\uDDED", "\uD83D\uDDFA\uFE0F", "\uD83C\uDFDC\uFE0F",
    "\uD83E\uDDB4", "\uD83D\uDC1A", "\uD83E\uDD88", "\uD83E\uDD95",
    "\uD83E\uDD96", "\uD83D\uDC0A", "\uD83E\uDEB8", "\uD83D\uDC1F",
    "\uD83E\uDD9E", "\uD83D\uDC19", "\u2604\uFE0F", "\u2728", "\uD83C\uDF1F",
    "\uD83D\uDD25", "\u2744\uFE0F", "\uD83E\uDDCA", "\uD83E\uDD7D", "\uD83D\uDDFF",
)

@Composable
fun ProfileScreen(
    navController: NavController,
    openToFriends: Boolean = false,
) {
    val repo = AppRepository.instance
    val profile by repo.profile.collectAsStateWithLifecycle()
    val collection by repo.collection.collectAsStateWithLifecycle()
    val wishlist by repo.wishlist.collectAsStateWithLifecycle()
    val favorites by repo.favoriteSpots.collectAsStateWithLifecycle()
    val achievementsState by AchievementsRepository.state.collectAsStateWithLifecycle()
    val level by AchievementsRepository.level.collectAsStateWithLifecycle()
    val tier by AchievementsRepository.tier.collectAsStateWithLifecycle()
    val auth = AuthRepository.instance
    val social = SocialRepository.instance
    val purchaseManager = PurchaseManager.instance
    val isPremium by purchaseManager.isPremium.collectAsStateWithLifecycle()
    val sessionStatus by auth.sessionStatus.collectAsStateWithLifecycle()
    val isSignedIn = sessionStatus is com.rork.rockscout.data.SessionStatus.Authenticated
    val signedInEmail = (sessionStatus as? com.rork.rockscout.data.SessionStatus.Authenticated)?.session?.user?.email
    val connections by social.connections.collectAsStateWithLifecycle()
    val incomingMessageRequests by social.incomingRequests.collectAsStateWithLifecycle()
    val messagingCount by social.totalMessagingCount.collectAsStateWithLifecycle()
    val notificationRepo = NotificationRepository.instance
    val unreadCount by notificationRepo.unreadCount.collectAsStateWithLifecycle()
    val postRepo = PostRepository.instance
    val myPosts by postRepo.myPosts.collectAsStateWithLifecycle()
    val postLikes by postRepo.postLikes.collectAsStateWithLifecycle()
    val postComments by postRepo.postComments.collectAsStateWithLifecycle()
    val commentLikes by postRepo.commentLikes.collectAsStateWithLifecycle()
    val likedPostIds by postRepo.likedPostIds.collectAsStateWithLifecycle()
    val likedCommentIds by postRepo.likedCommentIds.collectAsStateWithLifecycle()
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    var showCreatePost by remember { mutableStateOf(false) }
    var commentBodies by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var replyBodies by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var replyingTo by remember { mutableStateOf<String?>(null) }
    var commentImageUris by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }
    var replyImageUris by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }
    var commentImageErrors by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }
    var replyImageErrors by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }
    var pendingDeletePostId by remember { mutableStateOf<String?>(null) }
    var showRemoveBgConfirm by remember { mutableStateOf(false) }
    var pendingRemoveMemberId by remember { mutableStateOf<String?>(null) }
    var showArchivedPosts by remember { mutableStateOf(false) }
    val archivedPosts by postRepo.archivedPosts.collectAsStateWithLifecycle()

    // ─── Settings backup state (Phase 4) ───
    var isBackingUp by remember { mutableStateOf(false) }
    var backupSuccess by remember { mutableStateOf<Boolean?>(null) }
    var lastBackupMs by remember { mutableStateOf(0L) }
    val backupScope = androidx.compose.runtime.rememberCoroutineScope()

    // Sync my collection, wishlist, and favorite spots to the local users table so other
    // hunters can see my public rock counts and lists.
    androidx.compose.runtime.LaunchedEffect(collection, wishlist, favorites) {
        social.syncMyPublicLists()
    }

    // Load my profile posts whenever the screen is shown or after a new post is created.
    androidx.compose.runtime.LaunchedEffect(Unit) {
        postRepo.loadMyPosts()
        val me = auth.currentUserId
        if (me != null) postRepo.loadArchivedPosts(me)
    }

    val current by repo.currentLocation.collectAsStateWithLifecycle()
    val locationRefresh by repo.locationRefreshTrigger.collectAsStateWithLifecycle()

    val accessManager = IdentifyAccessManager.instance
    val trialExpired by accessManager.trialExpired.collectAsStateWithLifecycle()
    val locationLocked = remember(isPremium, trialExpired) {
        accessManager.isLocationLocked(isPremium)
    }
    val socialLocked = remember(isPremium) { accessManager.isSocialLocked(isPremium) }

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

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { _ -> /* Results handled implicitly */ }

    val listState = rememberLazyListState()

    var clubMembers by remember { mutableStateOf<List<SocialRepository.HunterProfile>>(emptyList()) }
    var howThisWorksExpanded by remember { mutableStateOf(false) }
    androidx.activity.compose.BackHandler(enabled = howThisWorksExpanded) { howThisWorksExpanded = false }

    // Load club members (connected RockScout Friends) whenever the connection list changes.
    androidx.compose.runtime.LaunchedEffect(connections) {
        clubMembers = if (connections.isNotEmpty()) social.fetchProfiles(connections) else emptyList()
    }

    // Load connections, message requests, and notifications when signed in.
    androidx.compose.runtime.LaunchedEffect(isSignedIn, profile.clubEnabled) {
        if (isSignedIn && profile.clubEnabled) {
            social.loadConnections()
            social.loadRequests()
        }
        if (isSignedIn) {
            notificationRepo.loadNotifications()
        }
    }

    var showEditSheet by remember { mutableStateOf(false) }
    var statusCooldownToast by remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    // Load last backup timestamp on screen entry (Phase 4)
    LaunchedEffect(isSignedIn) {
        if (isSignedIn) {
            lastBackupMs = SettingsBackupWorker.lastBackupAt(context)
        }
    }
    androidx.compose.runtime.LaunchedEffect(statusCooldownToast) {
        if (statusCooldownToast != null) {
            android.widget.Toast.makeText(context, statusCooldownToast, android.widget.Toast.LENGTH_SHORT).show()
            kotlinx.coroutines.delay(2500)
            statusCooldownToast = null
        }
    }

    ScreenScaffold(title = "Profile", onBack = { navController.popBackStack() }) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 20.dp, end = 20.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ─── Profile header card (tappable to edit) ───
            // Floating Create Post button sits above the card in the top-right.
            item {
                // Create Post button sits above the profile card in the top-right, outside the card box.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (!socialLocked) {
                        CreatePostButton(
                            onClick = {
                                if (isSignedIn) showCreatePost = true
                                else navController.navigate(Routes.SIGN_IN)
                            },
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }
                }
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Two-zone profile card: background image on top, solid gradient below.
                    val cardShape = RoundedCornerShape(20.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(cardShape)
                            .glowingBorder(3.dp, Citrine.copy(alpha = 0.55f), cardShape)
                            .clickable { showEditSheet = true },
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Top zone: background image (no gradient overlay) — ~190dp tall.
                            if (!profile.backgroundImagePath.isNullOrBlank()) {
                                AsyncImage(
                                    model = profile.backgroundImagePath,
                                    contentDescription = "Profile background",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(190.dp),
                                    contentScale = ContentScale.Crop,
                                )
                            } else {
                                // Fallback gradient band when no background image is set.
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Citrine.copy(alpha = 0.25f), Aqua.copy(alpha = 0.15f), Color(0xFF1A1812))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = Citrine.copy(alpha = 0.35f),
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(16.dp)
                                            .size(20.dp),
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = Aqua.copy(alpha = 0.30f),
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(16.dp)
                                            .size(16.dp),
                                    )
                                    Text(
                                        "\u26CF\uFE0F",
                                        style = MaterialTheme.typography.displayMedium,
                                    )
                                }
                            }
                            // Bottom zone: solid gradient with all text & icons.
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF2A2820), Color(0xFF1E1C16))
                                        )
                                    )
                                    .padding(16.dp),
                            ) {
                                // Top row: avatar, notification/message-request icons, Social Settings pill.
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(Brush.linearGradient(listOf(Citrine.copy(alpha = 0.5f), Aqua.copy(alpha = 0.3f))))
                                            .glowingBorder(3.dp, profileBorderColor(profile.hunterStatus), CircleShape),
                                        contentAlignment = Alignment.BottomCenter,
                                    ) {
                                        AnimatedAvatarIcon(
                                            emoji = profile.avatarEmoji,
                                            size = 64.dp,
                                            style = MaterialTheme.typography.displaySmall,
                                            contentAlignment = Alignment.BottomCenter,
                                        )
                                    }
                                    BadgeIconButton(
                                        icon = Icons.Filled.Notifications,
                                        contentDescription = "Notifications",
                                        count = unreadCount,
                                        onClick = { navController.navigate(Routes.NOTIFICATIONS) },
                                        size = 48.dp,
                                    )
                                    BadgeIconButton(
                                        icon = Icons.Filled.Mail,
                                        contentDescription = "Messages",
                                        count = messagingCount,
                                        onClick = { navController.navigate(Routes.friends(initialTab = 1)) },
                                        size = 48.dp,
                                    )
                                    Spacer(Modifier.weight(1f))
                                    // Right-side column: Social Settings pill + compact Status Update dropdown.
                                    // Both are constrained to the same height and the total column never exceeds
                                    // the 64.dp avatar, so the status dropdown never drops below the profile icon.
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        modifier = Modifier
                                            .height(64.dp)
                                            .width(IntrinsicSize.Max),
                                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                                    ) {
                                        // Cyan "Social Settings" pill — stands out on the dark card
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                                .clip(RoundedCornerShape(50.dp))
                                                .background(Brush.horizontalGradient(listOf(Cyan, CyanDeep)))
                                                .glowingBorder(2.dp, CyanDeep, RoundedCornerShape(50.dp))
                                                .clickable { navController.navigate(Routes.SOCIAL_SETTINGS) },
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Filled.Settings,
                                                    contentDescription = "Social Settings",
                                                    tint = Ink,
                                                    modifier = Modifier.size(16.dp),
                                                )
                                                Spacer(Modifier.width(6.dp))
                                                Text(
                                                    "Social Settings",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Ink,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            }
                                        }
                                        // Compact status dropdown — same height as the Social Settings pill
                                        if (isSignedIn && !socialLocked) {
                                            HunterStatusDropdown(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f),
                                                isCompact = true,
                                                hunterStatus = profile.hunterStatus,
                                                isPremium = isPremium,
                                                onCooldown = { mins ->
                                                    statusCooldownToast = "Wait ${mins}m before changing status again (Premium waives this)"
                                                },
                                            )
                                        }
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                Text(profile.bio, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid)
                                Spacer(Modifier.height(8.dp))
                                // Username + location only — the View all achievements button lives on the level card.
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Text(
                                        profile.name,
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.LocationOn,
                                            contentDescription = null,
                                            tint = Citrine,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            profile.homeRegion,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Citrine,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                                // Pending review hint
                                if (!profile.pendingBackgroundPath.isNullOrBlank()) {
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        "\u23F3 Background image pending review",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Aqua,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ─── Level / XP / streak card (directly under profile card) ───
            item {
                LevelXpCard(
                    level = level,
                    tier = tier,
                    totalXp = achievementsState.totalXp,
                    streak = achievementsState.currentStreak,
                    progress = AchievementsRepository.levelProgress(achievementsState.totalXp),
                    onTileClick = { navController.navigate(Routes.achievements()) },
                    onViewAllAchievements = { navController.navigate(Routes.allAchievements()) },
                    onOpenBadges = { navController.navigate(Routes.achievements(scrollToBadges = true)) },
                )
            }

            // ─── Stat cards (collected / wishlist / spots — directly under level card) ───
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatTile("Collected", collection.size.toString(), Icons.Filled.Inventory2, Aqua, Modifier.weight(1f)) { navController.navigate(Routes.COLLECTION) }
                    StatTile("Wishlist", wishlist.size.toString(), Icons.Filled.PlaylistAdd, Color(0xFF9B7BD8), Modifier.weight(1f)) { navController.navigate(Routes.WISHLIST) }
                    StatTile("Spots", favorites.size.toString(), Icons.Filled.Place, Color(0xFFE2574C), Modifier.weight(1f)) { navController.navigate(Routes.FAVORITES) }
                }
            }

            // ─── RockScout Friends section (directly above Profile Posts) ───
            item { SectionLabel("RockScout Friends") }
            item {
                RockScoutFriendsSection(
                    isSignedIn = isSignedIn,
                    signedInEmail = signedInEmail,
                    clubEnabled = profile.clubEnabled,
                    clubMembers = clubMembers,
                    scanRadiusMiles = profile.scanRadiusMiles,
                    isPremium = isPremium,
                    socialLocked = socialLocked,
                    howThisWorksExpanded = howThisWorksExpanded,
                    onHowThisWorksToggle = { howThisWorksExpanded = !howThisWorksExpanded },
                    onSignIn = { navController.navigate(Routes.SIGN_IN) },
                    onSignOut = { coroutineScope.launch { auth.signOut() } },
                    onEnableFriends = { navController.navigate(Routes.SOCIAL_SETTINGS) },
                    onScan = { navController.navigate(Routes.SCAN) },
                    onFriends = { navController.navigate(Routes.friends()) },
                    onBrowse = { navController.navigate(Routes.DISCOVER_HUNTERS) },
                    onPingMap = { navController.navigate(Routes.ROCKSCOUTS_MAP) },
                    onSetScanRadius = { miles -> repo.setScanRadiusMiles(miles) },
                    onPaywall = { navController.navigate(Routes.PAYWALL) },
                    onMemberTap = { memberId -> navController.navigate(Routes.userProfile(memberId)) },
                    onMemberRemove = { memberId ->
                        pendingRemoveMemberId = memberId
                    },
                    onMemberCollection = { memberId -> navController.navigate(Routes.userCollection(memberId)) },
                    onMemberWishlist = { memberId -> navController.navigate(Routes.userWishlist(memberId)) },
                )
            }

            // ─── Post feed (5 most recent posts, empty box if none) ───
            // ─── Profile Posts ───────────────────────────────────────────
            // Your public post feed. Only shown on your own profile.
            item { SectionLabel("Profile Posts") }
            if (myPosts.isEmpty()) {
                item {
                    if (!socialLocked) {
                        EmptyPostBox(
                            isMe = true,
                            onCreatePost = {
                                if (isSignedIn) showCreatePost = true
                                else navController.navigate(Routes.SIGN_IN)
                            },
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF1E1C16))
                                .glowingBorder(1.dp, Color(0xFF3A3830), RoundedCornerShape(16.dp))
                                .clickable { navController.navigate(Routes.PAYWALL) }
                                .padding(24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("\uD83D\uDD12", style = MaterialTheme.typography.headlineMedium)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Social features are locked",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = DarkTextHigh,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Subscribe or donate to create posts and share with the community.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMid,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            } else {
                items(myPosts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        isFriend = false,
                        isMe = true,
                        isLiked = likedPostIds.contains(post.id),
                        likeCount = postLikes[post.id]?.size ?: 0,
                        comments = postComments[post.id] ?: emptyList(),
                        commentLikes = commentLikes,
                        likedCommentIds = likedCommentIds,
                        myUserId = auth.currentUserId,
                        commentBody = commentBodies[post.id] ?: "",
                        replyingToCommentId = replyingTo,
                        replyBody = if (replyingTo != null) replyBodies[replyingTo] ?: "" else "",
                        commentImageUri = commentImageUris[post.id],
                        replyImageUri = replyingTo?.let { replyImageUris[it] },
                        commentImageModerating = commentImageUris[post.id] == "__loading__",
                        replyImageModerating = replyingTo != null && replyImageUris[replyingTo] == "__loading__",
                        commentImageError = commentImageErrors[post.id],
                        replyImageError = replyingTo?.let { replyImageErrors[it] },
                        onLike = {
                            coroutineScope.launch { postRepo.toggleLike(post.id) }
                        },
                        onCommentChange = { body ->
                            commentBodies = commentBodies + (post.id to body)
                        },
                        onComment = {
                            val body = commentBodies[post.id] ?: ""
                            val imgUri = commentImageUris[post.id]
                            val cleanImg = if (imgUri != null && !imgUri.startsWith("__") && imgUri != "__loading__") imgUri else null
                            if (body.isNotBlank() || cleanImg != null) {
                                coroutineScope.launch {
                                    postRepo.addComment(post.id, body, null, cleanImg)
                                    commentBodies = commentBodies + (post.id to "")
                                    commentImageUris = commentImageUris - post.id
                                    commentImageErrors = commentImageErrors - post.id
                                }
                            }
                        },
                        onReplyStart = { commentId ->
                            replyingTo = if (replyingTo == commentId) null else commentId
                        },
                        onReplyBodyChange = { body ->
                            replyingTo?.let { replyBodies = replyBodies + (it to body) }
                        },
                        onReplySubmit = {
                            val parentId = replyingTo
                            val body = parentId?.let { replyBodies[it] } ?: ""
                            val imgUri = parentId?.let { replyImageUris[it] }
                            val cleanImg = if (imgUri != null && !imgUri.startsWith("__") && imgUri != "__loading__") imgUri else null
                            if (parentId != null && (body.isNotBlank() || cleanImg != null)) {
                                coroutineScope.launch {
                                    postRepo.addComment(post.id, body, parentId, cleanImg)
                                    replyBodies = replyBodies + (parentId to "")
                                    replyImageUris = replyImageUris - parentId
                                    replyImageErrors = replyImageErrors - parentId
                                    replyingTo = null
                                }
                            }
                        },
                        onCommentLike = { commentId ->
                            coroutineScope.launch { postRepo.toggleCommentLike(commentId) }
                        },
                        onCommentImagePicked = { uri ->
                            if (uri == null) {
                                commentImageUris = commentImageUris - post.id
                                commentImageErrors = commentImageErrors - post.id
                            } else if (uri.startsWith("__error:")) {
                                commentImageErrors = commentImageErrors + (post.id to uri.substring(7))
                                commentImageUris = commentImageUris - post.id
                            } else {
                                commentImageUris = commentImageUris + (post.id to uri)
                                commentImageErrors = commentImageErrors - post.id
                            }
                        },
                        onReplyImagePicked = { uri ->
                            val key = replyingTo ?: return@PostCard
                            if (uri == null) {
                                replyImageUris = replyImageUris - key
                                replyImageErrors = replyImageErrors - key
                            } else if (uri.startsWith("__error:")) {
                                replyImageErrors = replyImageErrors + (key to uri.substring(7))
                                replyImageUris = replyImageUris - key
                            } else {
                                replyImageUris = replyImageUris + (key to uri)
                                replyImageErrors = replyImageErrors - key
                            }
                        },
                        onCommentImageRemove = {
                            commentImageUris = commentImageUris - post.id
                            commentImageErrors = commentImageErrors - post.id
                        },
                        onReplyImageRemove = {
                            replyingTo?.let {
                                replyImageUris = replyImageUris - it
                                replyImageErrors = replyImageErrors - it
                            }
                        },
                        onImageClick = postImageNavigation(post, navController),
                        onDelete = {
                            pendingDeletePostId = post.id
                        },
                        onDeleteComment = { commentId ->
                            coroutineScope.launch { postRepo.deleteComment(commentId) }
                        },
                        onEditComment = { commentId, newBody ->
                            coroutineScope.launch { postRepo.editComment(commentId, newBody) }
                        },
                        onEditPost = { newCaption ->
                            coroutineScope.launch { postRepo.editPostCaption(post.id, newCaption) }
                        },
                    )
                }
            }

            // ─── Archived Posts button (only if archived posts exist) ───
            if (archivedPosts.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(16.dp))
                    SculptedOutlinedButton(
                        text = "Archived Posts (${archivedPosts.size})",
                        onClick = { showArchivedPosts = true },
                        modifier = Modifier.fillMaxWidth(),
                        accent = Citrine,
                        icon = Icons.Filled.Archive,
                    )
                }
            }


            // ─── Referral card ───
            item { SectionLabel("Enlist a RockScout") }
            item {
                DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .shadow(12.dp, CircleShape, spotColor = Citrine, ambientColor = Citrine, clip = false)
                                .glowingBorder(2.dp, Citrine, CircleShape)
                                .clip(CircleShape)
                                .background(Color.Transparent)
                                .glowingBorder(1.dp, Citrine.copy(alpha = 0.55f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    "+${ReferralRepository.PREMIUM_XP_REWARD}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    "XP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Citrine,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    "Premium users",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    lineHeight = 10.sp,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Share your code, earn rewards", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            Text(
                                if (isPremium)
                                    "Premium users: 500 XP per referred RockScout. No referral limit."
                                else
                                    "Free users: 4 ID tokens + 1-day unlock per referred RockScout. Max 3 per week.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextMid,
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "${achievementsState.referralCount} enlisted",
                            style = MaterialTheme.typography.labelMedium,
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                        )
                        Text("View ›", style = MaterialTheme.typography.labelMedium, color = Citrine, fontWeight = FontWeight.Bold)
                    }
                }.let {
                    // Make the whole card tappable to open the referral screen.
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).clickable {
                        navController.navigate(Routes.REFERRAL)
                    }) { it }
                }
            }


            // ─── Settings cloud backup section (Phase 4) ───
            if (isSignedIn) {
                item { SectionLabel("Data & Sync") }
                item {
                    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
                        Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Citrine.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CloudUpload,
                                        contentDescription = null,
                                        tint = Citrine,
                                        modifier = Modifier.size(22.dp),
                                )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Cloud Backup",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = if (lastBackupMs > 0L) {
                                            "Last backed up ${UserDateFormatter.formatDateTime(lastBackupMs)}"
                                        } else {
                                            "Your settings back up automatically every 12 hours."
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DarkTextMid,
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            // Progress bar while backing up
                            if (isBackingUp) {
                                LinearProgressIndicator(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Citrine,
                                    trackColor = Citrine.copy(alpha = 0.2f),
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Backing up your data to the cloud…",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Citrine,
                                )
                            } else {
                                // Success / failure message
                                backupSuccess?.let { success ->
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = if (success) {
                                            "✓ Backup complete! Your data is synced to the cloud."
                                        } else {
                                            "✗ Backup failed. Check your connection and try again."
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (success) Success else Danger,
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (!isBackingUp) {
                                        backupSuccess = null
                                        isBackingUp = true
                                        backupScope.launch {
                                            val userId = auth.currentUserId
                                            if (userId.isNullOrBlank()) {
                                                isBackingUp = false
                                                backupSuccess = false
                                                return@launch
                                            }
                                            try {
                                                val settingsJson = PersistenceManager.exportAllSettingsAsJson()
                                                val result = SettingsBackupApi.backupSettings(userId, settingsJson)
                                                isBackingUp = false
                                                if (result.isSuccess) {
                                                    backupSuccess = true
                                                    lastBackupMs = System.currentTimeMillis()
                                                } else {
                                                    backupSuccess = false
                                                }
                                            } catch (e: Exception) {
                                                isBackingUp = false
                                                backupSuccess = false
                                            }
                                        }
                                    }
                                },
                                enabled = !isBackingUp,
                                shape = RoundedCornerShape(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Citrine,
                                    contentColor = Ink,
                                    disabledContainerColor = Citrine.copy(alpha = 0.4f),
                                    disabledContentColor = Ink.copy(alpha = 0.5f),
                                ),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Save,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Back Up Data Now",
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }
                }
            }

        }
    }

    // Post delete confirmation
    pendingDeletePostId?.let { postId ->
        DeleteConfirmDialog(
            title = "Delete post?",
            message = "Delete this post? This action cannot be undone.",
            onConfirm = {
                coroutineScope.launch {
                    postRepo.deletePost(postId)
                    postRepo.loadMyPosts()
                }
                pendingDeletePostId = null
            },
            onDismiss = { pendingDeletePostId = null },
        )
    }

    // ─── Archived Posts full-screen popup ───
    if (showArchivedPosts) {
        ArchivedPostsPopup(
            posts = archivedPosts,
            isMe = true,
            isFriend = false,
            postLikes = postLikes,
            postComments = postComments,
            commentLikes = commentLikes,
            likedPostIds = likedPostIds,
            likedCommentIds = likedCommentIds,
            myUserId = auth.currentUserId,
            commentBodies = commentBodies,
            replyBodies = replyBodies,
            replyingTo = replyingTo,
            commentImageUris = commentImageUris,
            replyImageUris = replyImageUris,
            commentImageErrors = commentImageErrors,
            replyImageErrors = replyImageErrors,
            onLike = { postId -> coroutineScope.launch { postRepo.toggleLike(postId) } },
            onCommentChange = { postId, body -> commentBodies = commentBodies + (postId to body) },
            onComment = { postId ->
                val body = commentBodies[postId] ?: ""
                val imgUri = commentImageUris[postId]
                val cleanImg = if (imgUri != null && !imgUri.startsWith("__") && imgUri != "__loading__") imgUri else null
                if (body.isNotBlank() || cleanImg != null) {
                    coroutineScope.launch {
                        postRepo.addComment(postId, body, null, cleanImg)
                        commentBodies = commentBodies + (postId to "")
                        commentImageUris = commentImageUris - postId
                        commentImageErrors = commentImageErrors - postId
                    }
                }
            },
            onReplyStart = { commentId -> replyingTo = if (replyingTo == commentId) null else commentId },
            onReplyBodyChange = { body -> replyingTo?.let { replyBodies = replyBodies + (it to body) } },
            onReplySubmit = {
                val parentId = replyingTo
                val body = parentId?.let { replyBodies[it] } ?: ""
                val imgUri = parentId?.let { replyImageUris[it] }
                val cleanImg = if (imgUri != null && !imgUri.startsWith("__") && imgUri != "__loading__") imgUri else null
                if (parentId != null && (body.isNotBlank() || cleanImg != null)) {
                    coroutineScope.launch {
                        val postId = archivedPosts.firstOrNull { post ->
                            postComments[post.id]?.any { it.id == parentId } == true
                        }?.id
                        if (postId != null) {
                            postRepo.addComment(postId, body, parentId, cleanImg)
                            replyBodies = replyBodies + (parentId to "")
                            replyImageUris = replyImageUris - parentId
                            replyImageErrors = replyImageErrors - parentId
                            replyingTo = null
                        }
                    }
                }
            },
            onCommentImagePicked = { postId, uri ->
                if (uri == null) {
                    commentImageUris = commentImageUris - postId
                    commentImageErrors = commentImageErrors - postId
                } else if (uri.startsWith("__error:")) {
                    commentImageErrors = commentImageErrors + (postId to uri.substring(7))
                    commentImageUris = commentImageUris - postId
                } else {
                    commentImageUris = commentImageUris + (postId to uri)
                    commentImageErrors = commentImageErrors - postId
                }
            },
            onReplyImagePicked = { uri ->
                val key = replyingTo
                if (key != null) {
                    if (uri == null) {
                        replyImageUris = replyImageUris - key
                        replyImageErrors = replyImageErrors - key
                    } else if (uri.startsWith("__error:")) {
                        replyImageErrors = replyImageErrors + (key to uri.substring(7))
                        replyImageUris = replyImageUris - key
                    } else {
                        replyImageUris = replyImageUris + (key to uri)
                        replyImageErrors = replyImageErrors - key
                    }
                }
            },
            onCommentImageRemove = { postId ->
                commentImageUris = commentImageUris - postId
                commentImageErrors = commentImageErrors - postId
            },
            onReplyImageRemove = {
                replyingTo?.let {
                    replyImageUris = replyImageUris - it
                    replyImageErrors = replyImageErrors - it
                }
            },
            onCommentLike = { commentId -> coroutineScope.launch { postRepo.toggleCommentLike(commentId) } },
            onRestore = { postId ->
                coroutineScope.launch {
                    postRepo.restorePost(postId)
                }
            },
            onDeleteComment = { commentId ->
                coroutineScope.launch { postRepo.deleteComment(commentId) }
            },
            onEditComment = { commentId, newBody ->
                coroutineScope.launch { postRepo.editComment(commentId, newBody) }
            },
            onEditPost = { postId, newCaption ->
                coroutineScope.launch { postRepo.editPostCaption(postId, newCaption) }
            },
            onDismiss = { showArchivedPosts = false },
        )
    }

    // Create post sheet
    if (showCreatePost) {
        CreatePostSheet(
            onDismiss = { showCreatePost = false },
            onPosted = {
                coroutineScope.launch { postRepo.loadMyPosts() }
            },
        )
    }

    // Edit profile bottom sheet
    if (showEditSheet) {
        val currentUid = auth.currentUserId
        val context = LocalContext.current
        val coroutineScope2 = rememberCoroutineScope()
        EditProfileSheetContainer(
            name = profile.name,
            homeRegion = profile.homeRegion,
            bio = profile.bio,
            avatarEmoji = profile.avatarEmoji,
            backgroundImagePath = profile.backgroundImagePath,
            pendingBackgroundPath = profile.pendingBackgroundPath,
            currentUserId = currentUid,
            onDismiss = { showEditSheet = false },
            onSave = { newName, newRegion, newBio, newAvatar, newBgPath ->
                // Block save if the display name is already taken by another user.
                if (SocialRepository.instance.isDisplayNameTaken(newName, excludeUserId = currentUid)) {
                    return@EditProfileSheetContainer
                }
                repo.updateProfile {
                    it.copy(
                        name = ProfanityFilter.filter(newName),
                        homeRegion = newRegion,
                        bio = ProfanityFilter.filter(newBio),
                        avatarEmoji = newAvatar,
                    )
                }
                if (newBgPath != profile.backgroundImagePath) {
                    if (newBgPath == null) {
                        repo.setBackgroundImagePath(null)
                    }
                }
                repo.saveProfileChanges()
                if (profile.locationMonitoring) {
                    WorkScheduler.runProximityCheckNow(
                        navController.context.applicationContext
                    )
                }
                showEditSheet = false
            },
            onBackgroundSelected = { uri ->
                coroutineScope2.launch {
                    val base64 = ImageUtils.uriToModerationBase64(context, uri)
                    if (base64 == null) {
                        return@launch
                    }
                    val verdict = ImageModerator.scan(base64, "image/jpeg")
                    when (verdict.triState) {
                        ModerationTriState.CLEAN -> {
                            val persistentPath = ImageUtils.copyUriToInternalStorage(context, uri, "backgrounds")
                            repo.setBackgroundImagePath(persistentPath ?: uri.toString())
                        }
                        ModerationTriState.EXPLICIT -> {
                            // Rejected — show feedback via a toast
                            android.widget.Toast.makeText(
                                context,
                                "Image rejected: inappropriate content detected.",
                                android.widget.Toast.LENGTH_LONG,
                            ).show()
                        }
                        ModerationTriState.QUESTIONABLE -> {
                            val persistentPath = ImageUtils.copyUriToInternalStorage(context, uri, "backgrounds")
                            repo.setPendingBackgroundPath(persistentPath ?: uri.toString())
                            val userName = profile.name
                            ImageReviewRepository.instance.submitReview(
                                userId = currentUid ?: "unknown",
                                userName = userName,
                                userAvatar = profile.avatarEmoji,
                                imageUri = persistentPath ?: uri.toString(),
                                type = "profile_background",
                                reason = verdict.reason,
                            )
                            android.widget.Toast.makeText(
                                context,
                                "Background submitted for review. We'll apply it once approved.",
                                android.widget.Toast.LENGTH_LONG,
                            ).show()
                        }
                    }
                }
            },
            onRemoveBackground = {
                showRemoveBgConfirm = true
            },
        )
    }

    // Remove background image confirmation
    if (showRemoveBgConfirm) {
        DeleteConfirmDialog(
            title = "Remove background?",
            message = "Remove your profile background image? You can set a new one anytime.",
            onConfirm = {
                repo.setBackgroundImagePath(null)
                showRemoveBgConfirm = false
            },
            onDismiss = { showRemoveBgConfirm = false },
        )
    }

    // Club member removal confirmation
    pendingRemoveMemberId?.let { memberId ->
        DeleteConfirmDialog(
            title = "Remove connection?",
            message = "Remove this RockScout from your club? You can re-add them later.",
            onConfirm = {
                coroutineScope.launch {
                    social.removeConnection(memberId)
                    social.loadConnections()
                }
                pendingRemoveMemberId = null
            },
            onDismiss = { pendingRemoveMemberId = null },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileSheetContainer(
    name: String,
    homeRegion: String,
    bio: String,
    avatarEmoji: String,
    backgroundImagePath: String?,
    pendingBackgroundPath: String?,
    currentUserId: String?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String?) -> Unit,
    onBackgroundSelected: (Uri) -> Unit,
    onRemoveBackground: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1E1C16),
    ) {
        EditProfileSheet(
            name = name,
            homeRegion = homeRegion,
            bio = bio,
            avatarEmoji = avatarEmoji,
            backgroundImagePath = backgroundImagePath,
            pendingBackgroundPath = pendingBackgroundPath,
            currentUserId = currentUserId,
            onSave = onSave,
            onBackgroundSelected = onBackgroundSelected,
            onRemoveBackground = onRemoveBackground,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileSheet(
    name: String,
    homeRegion: String,
    bio: String,
    avatarEmoji: String,
    backgroundImagePath: String?,
    pendingBackgroundPath: String?,
    currentUserId: String?,
    onSave: (String, String, String, String, String?) -> Unit,
    onBackgroundSelected: (Uri) -> Unit,
    onRemoveBackground: () -> Unit,
) {
    var editName by remember { mutableStateOf(name) }
    var editBio by remember { mutableStateOf(bio) }
    var editAvatar by remember { mutableStateOf(avatarEmoji) }
    var editRegion by remember { mutableStateOf(homeRegion) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var bgModerating by remember { mutableStateOf(false) }
    var bgRejected by remember { mutableStateOf<String?>(null) }
    var showSavedImagePicker by remember { mutableStateOf(false) }
    // Track the original name so we know whether the user actually changed it
    // (needed to avoid blocking the save when they kept their own name).
    val originalName = remember { name.trim() }
    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    val bgGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri != null) {
            // Reject files larger than 5 MB before moderation to prevent
            // base64-encoding OOMs and failed uploads.
            if (ImageUtils.isOverUploadLimit(context, uri)) {
                bgRejected = "That image is over 5 MB. Please choose a smaller photo."
                return@rememberLauncherForActivityResult
            }
            bgModerating = true
            bgRejected = null
            onBackgroundSelected(uri)
        }
    }

    // Live duplicate check — re-validate whenever the name changes.
    LaunchedEffect(editName) {
        val trimmed = editName.trim()
        nameError = if (trimmed.isBlank()) {
            "Display name cannot be empty."
        } else if (trimmed.equals(originalName, ignoreCase = true)) {
            null // User kept their own name — always allowed.
        } else if (SocialRepository.instance.isDisplayNameTaken(trimmed, excludeUserId = currentUserId)) {
            "That display name is already taken. Try another."
        } else {
            null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            "Edit your RockScout card",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        // ── Add Background pill ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Citrine.copy(alpha = 0.15f))
                    .glowingBorder(2.dp, Citrine.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .clickable { bgGalleryLauncher.launch("image/*") }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "\uD83D\uDDBC\uFE0F",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (backgroundImagePath.isNullOrBlank()) "Add Background" else "Change Background",
                        style = MaterialTheme.typography.labelLarge,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Citrine.copy(alpha = 0.15f))
                    .glowingBorder(2.dp, Citrine.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .clickable { showSavedImagePicker = true }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Download,
                        contentDescription = "Saved images",
                        tint = Citrine,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Saved Images",
                        style = MaterialTheme.typography.labelLarge,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            if (!backgroundImagePath.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Danger.copy(alpha = 0.12f))
                        .glowingBorder(2.dp, Danger.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                        .clickable { onRemoveBackground() }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "Remove",
                        style = MaterialTheme.typography.labelLarge,
                        color = Danger,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        // Pending review hint
        if (!pendingBackgroundPath.isNullOrBlank()) {
            Text(
                "\u23F3 Your background image is pending review. We'll apply it once it's approved.",
                style = MaterialTheme.typography.labelSmall,
                color = Aqua,
                fontWeight = FontWeight.SemiBold,
            )
        }
        // Display name field with inline error
        Column {
            Text("Display name", style = MaterialTheme.typography.labelMedium, color = Aqua, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = editName,
                onValueChange = { editName = it },
                modifier = Modifier.fillMaxWidth().noAutoFocus(),
                singleLine = true,
                isError = nameError != null,
                supportingText = {
                    if (nameError != null) {
                        Text(
                            nameError!!,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                keyboardOptions = KeyboardOptions.Default,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Slate800,
                    unfocusedContainerColor = Slate800,
                    focusedIndicatorColor = if (nameError != null) MaterialTheme.colorScheme.error else Citrine,
                    unfocusedIndicatorColor = StoneLine,
                    focusedTextColor = TextHigh,
                    unfocusedTextColor = TextHigh,
                    cursorColor = Citrine,
                ),
                shape = RoundedCornerShape(12.dp),
            )
        }
        HomeRegionPicker(editRegion) { v -> editRegion = v }
        ProfileField("Bio", editBio) { v -> editBio = v }
        Text("Choose an avatar", style = MaterialTheme.typography.labelMedium, color = Aqua, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            avatarOptions.forEach { emoji ->
                val selected = emoji == editAvatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (selected) Citrine.copy(alpha = 0.3f) else Color(0xFF3A3830))
                        .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape)
                        .clickable { editAvatar = emoji },
                    contentAlignment = Alignment.Center,
                ) { Text(emoji, style = MaterialTheme.typography.titleLarge) }
            }
        }
        Button(
            onClick = { onSave(editName, editRegion, editBio, editAvatar, backgroundImagePath) },
            modifier = Modifier.fillMaxWidth(),
            enabled = nameError == null && editName.trim().isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Citrine,
                contentColor = Ink,
                disabledContainerColor = Citrine.copy(alpha = 0.35f),
                disabledContentColor = Ink.copy(alpha = 0.5f),
            ),
            shape = RoundedCornerShape(14.dp),
        ) {
            Icon(
                androidx.compose.material.icons.Icons.Filled.Save,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text("Save Changes", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(16.dp))
    }

    if (showSavedImagePicker) {
        SavedImagesPickerDialog(
            onDismiss = { showSavedImagePicker = false },
            onImageSelected = { image ->
                showSavedImagePicker = false
                val uri = image.localUri?.let { android.net.Uri.parse(it) } ?: android.net.Uri.parse(image.url)
                onBackgroundSelected(uri)
            },
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    val isPosts = text.equals("Posts", ignoreCase = true)
    Text(
        text.uppercase(),
        style = if (isPosts) MaterialTheme.typography.titleMedium else MaterialTheme.typography.labelMedium,
        color = if (isPosts) Color.White else TextMid,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(top = if (isPosts) 8.dp else 4.dp, bottom = if (isPosts) 4.dp else 0.dp),
    )
}


@Composable
private fun RockScoutFriendsSection(
    isSignedIn: Boolean,
    signedInEmail: String?,
    clubEnabled: Boolean,
    clubMembers: List<SocialRepository.HunterProfile>,
    scanRadiusMiles: Int,
    isPremium: Boolean,
    socialLocked: Boolean,
    howThisWorksExpanded: Boolean,
    onHowThisWorksToggle: () -> Unit,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onEnableFriends: () -> Unit,
    onScan: () -> Unit,
    onFriends: () -> Unit,
    onBrowse: () -> Unit,
    onPingMap: () -> Unit,
    onSetScanRadius: (Int) -> Unit,
    onPaywall: () -> Unit,
    onMemberTap: (String) -> Unit,
    onMemberRemove: (String) -> Unit,
    onMemberCollection: (String) -> Unit,
    onMemberWishlist: (String) -> Unit,
) {
    var showSignOutConfirm by remember { mutableStateOf(false) }
    SocialCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Sign-in / account tile
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background((if (isSignedIn) Success else TextLow).copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Login,
                        contentDescription = null,
                        tint = if (isSignedIn) Success else TextLow,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isSignedIn) "Signed in" else "Sign in to RockScout",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = signedInEmail ?: "Required to use RockScout.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (isSignedIn) {
                    OutlinedButton(
                        onClick = { showSignOutConfirm = true },
                        shape = RoundedCornerShape(50.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    ) { Text("Sign out", style = MaterialTheme.typography.labelMedium) }
                } else {
                    Button(
                        onClick = onSignIn,
                        shape = RoundedCornerShape(50.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Citrine, contentColor = Ink),
                    ) { Text("Sign in", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold) }
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)
            Spacer(Modifier.height(8.dp))

            // Single wider Scan tile
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (socialLocked) {
                    SocialSubTile(
                        icon = Icons.Filled.Lock,
                        label = "Scan (locked)",
                        subtitle = "Subscribe to scan",
                        accent = TextLow,
                        iconTint = TextLow,
                        modifier = Modifier.fillMaxWidth(0.66f),
                        onClick = onPaywall,
                    )
                } else {
                    SocialSubTile(
                        icon = Icons.Filled.LocationOn,
                        label = "Scan",
                        subtitle = "Find nearby",
                        accent = Aqua,
                        iconTint = Aqua,
                        modifier = Modifier.fillMaxWidth(0.66f),
                        onClick = onScan,
                    )
                }
            }

            // When social is ON, show sub-tiles + scan radius + status + friends list
            if (clubEnabled) {
                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)
                Spacer(Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SocialSubTile(
                        icon = if (socialLocked) Icons.Filled.Lock else Icons.Filled.Group,
                        label = if (socialLocked) "Locked" else "Friends & Messages",
                        subtitle = null,
                        accent = if (socialLocked) TextLow else Color(0xFF4CF0E8),
                        iconTint = if (socialLocked) TextLow else Color(0xFFEAFFFE),
                        modifier = Modifier.weight(1f),
                        onClick = if (socialLocked) onPaywall else onFriends,
                    )
                    SocialSubTile(
                        icon = if (socialLocked) Icons.Filled.Lock else Icons.Filled.PersonSearch,
                        label = if (socialLocked) "Locked" else "Browse Users",
                        subtitle = null,
                        accent = if (socialLocked) TextLow else Color(0xFF4CF0E8),
                        iconTint = if (socialLocked) TextLow else Color(0xFFEAFFFE),
                        modifier = Modifier.weight(1f),
                        onClick = if (socialLocked) onPaywall else onBrowse,
                    )
                    SocialSubTile(
                        icon = if (socialLocked) Icons.Filled.Lock else Icons.Filled.Map,
                        label = if (socialLocked) "Locked" else "Ping Maps",
                        subtitle = null,
                        accent = if (socialLocked) TextLow else Color(0xFF4CF0E8),
                        iconTint = if (socialLocked) TextLow else Color(0xFFEAFFFE),
                        modifier = Modifier.weight(1f),
                        onClick = if (socialLocked) onPaywall else onPingMap,
                    )
                }

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)
                Spacer(Modifier.height(14.dp))
                Text("Scan radius", style = MaterialTheme.typography.labelMedium, color = DarkTextLow, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(5, 25, 50, 100, 250).forEach { miles ->
                        val selected = scanRadiusMiles == miles
                        val proLocked = miles == 250 && !isPremium
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) Citrine.copy(alpha = 0.30f) else Color(0xFF3A3830))
                                .border(2.dp, Citrine.copy(alpha = 0.75f), RoundedCornerShape(10.dp))
                                .clickable { if (proLocked) onPaywall() else onSetScanRadius(miles) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = if (proLocked) "$miles mi\nPREMIUM" else "$miles mi",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selected) Ink else TextMid,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onHowThisWorksToggle)
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("How this works", style = MaterialTheme.typography.labelMedium, color = Citrine, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(6.dp))
                    Text(if (howThisWorksExpanded) "\u25B4" else "\u25BE", style = MaterialTheme.typography.labelMedium, color = Citrine)
                }
                if (howThisWorksExpanded) {
                    Spacer(Modifier.height(8.dp))
                    HowThisWorksStep("1.", "Scan", "Tap Scan for RockScouts to see other hunters near you who are on the hunt right now. You'll see their name and a rough distance — never their exact spot.")
                    Spacer(Modifier.height(6.dp))
                    HowThisWorksStep("2.", "Message", "Tap Send message on someone's card to send a message request. If they accept, you two become connected and can chat in the app.")
                    Spacer(Modifier.height(6.dp))
                    HowThisWorksStep(
                        "3.", "Ping", "Once you're connected, open the RockScouts Map and tap Ping my location. Move the pin to a safe meet-up spot, then tap Set Ping. Your connected friend sees it on their map for 12 hours. Nobody else does.",
                        emphasis = "Only connected RockScout Friends can see each other's pings.",
                    )
                    Spacer(Modifier.height(6.dp))
                    HowThisWorksStep("4.", "Safety", "Your exact location is never shared with strangers. Only your connected RockScout Friends see your pings, and only the ones you set yourself.")
                    Spacer(Modifier.height(10.dp))
                }

                Spacer(Modifier.height(6.dp))
                Text(
                    "Your RockScout Friends (${clubMembers.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = Aqua,
                    fontWeight = FontWeight.Bold,
                )
                if (clubMembers.isEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "No connections yet. Scan for RockScouts and send a message request to start building your Friends list.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                    )
                } else {
                    Spacer(Modifier.height(8.dp))
                    clubMembers.forEach { member ->
                        ClubMemberRow(
                            name = member.display_name,
                            emoji = member.avatar_emoji,
                            level = member.level,
                            isPremium = member.premium_badge,
                            collectionCount = member.collection_count,
                            wishlistCount = member.wishlist_count,
                            status = when (member.status) {
                                "on-the-hunt" -> HunterStatus.ON_THE_HUNT
                                "wishing" -> HunterStatus.WISHING
                                "looking-for-trades" -> HunterStatus.LOOKING_FOR_TRADES
                                else -> HunterStatus.OFF_GRID
                            },
                            onTap = { onMemberTap(member.id) },
                            onRemove = { onMemberRemove(member.id) },
                            onCollectionClick = { onMemberCollection(member.id) },
                            onWishlistClick = { onMemberWishlist(member.id) },
                        )
                        if (member != clubMembers.last()) {
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    // Sign-out confirmation dialog — prevents accidental sign-outs by requiring
    // an explicit confirm before [onSignOut] is invoked.
    if (showSignOutConfirm) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirm = false },
            title = { Text("Sign out?", style = MaterialTheme.typography.headlineSmall) },
            text = {
                Text(
                    "You'll be signed out of RockScout on this device. Your collection, captures, and posts stay safely stored — you can sign back in anytime to pick up where you left off.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        showSignOutConfirm = false
                        onSignOut()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Citrine),
                ) {
                    Text("Sign out", color = Ink, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showSignOutConfirm = false }) {
                    Text("Cancel", color = TextLow)
                }
            },
        )
    }
}

@Composable
private fun HowThisWorksStep(
    number: String,
    title: String,
    body: String,
    emphasis: String = "",
) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            number,
            style = MaterialTheme.typography.labelMedium,
            color = Citrine,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 6.dp),
        )
        Column {
            Text(title, style = MaterialTheme.typography.labelMedium, color = DarkTextHigh, fontWeight = FontWeight.Bold)
            Text(body, style = MaterialTheme.typography.bodySmall, color = DarkTextMid, maxLines = 3, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            if (emphasis.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(emphasis, style = MaterialTheme.typography.bodySmall, color = DarkTextHigh, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ClubMemberRow(
    name: String,
    emoji: String,
    level: Int,
    isPremium: Boolean,
    collectionCount: Int,
    wishlistCount: Int,
    status: HunterStatus,
    onTap: () -> Unit,
    onRemove: () -> Unit,
    onCollectionClick: () -> Unit,
    onWishlistClick: () -> Unit,
) {
    val statusAccent = statusAccent(status)
    val statusLabel = status.label
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF3A3830))
            .clickable(onClick = onTap)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(statusAccent.copy(alpha = 0.40f), Aqua.copy(alpha = 0.20f)))),
            contentAlignment = Alignment.Center,
        ) { Text(emoji, style = MaterialTheme.typography.titleMedium) }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    name,
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
                if (isPremium) {
                    Spacer(Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Citrine.copy(alpha = 0.30f))
                            .padding(horizontal = 4.dp, vertical = 1.dp),
                    ) { Text("PREMIUM", style = MaterialTheme.typography.labelSmall, color = Citrine, fontWeight = FontWeight.Bold) }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                HunterStatusIcon(status = status, size = 16.dp)
                Spacer(Modifier.width(4.dp))
                Text(
                    statusLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Aqua,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
            }
            Text("Lvl $level", style = MaterialTheme.typography.labelSmall, color = Aqua)
            Spacer(Modifier.height(6.dp))
            ProfileStatBar(
                collectionCount = collectionCount,
                wishlistCount = wishlistCount,
                onCollectionClick = onCollectionClick,
                onWishlistClick = onWishlistClick,
            )
        }
        Box(
            modifier = Modifier
                .size(32.dp)
                .sculpted(shape = CircleShape, accent = Citrine, shadowElevation = 3.dp, circular = true, onClick = onRemove)
                .clip(CircleShape)
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = "Remove connection", tint = Color.White, modifier = Modifier.size(18.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeRegionPicker(currentValue: String, onChange: (String) -> Unit) {
    val (existingSub, existingCountryShort) = remember(currentValue) { RegionData.parse(currentValue) }
    var selectedCountryShort by remember(currentValue) {
        mutableStateOf(
            RegionData.countries.firstOrNull { it.shortName == existingCountryShort }?.shortName
                ?: RegionData.countries.first().shortName,
        )
    }
    var selectedSub by remember(currentValue) { mutableStateOf(existingSub ?: "") }
    var countryMenuExpanded by remember { mutableStateOf(false) }
    var subMenuExpanded by remember { mutableStateOf(false) }

    val country = remember(selectedCountryShort) {
        RegionData.countries.first { it.shortName == selectedCountryShort }
    }

    fun commit() {
        val stored = RegionData.format(selectedSub.takeIf { it.isNotBlank() }, selectedCountryShort)
        onChange(stored)
    }

    Column {
        Text(
            "Home region (state/province + country only)",
            style = MaterialTheme.typography.labelMedium,
            color = Aqua,
            fontWeight = FontWeight.Bold,
        )
        Text(
            "Cities/towns aren\u2019t shared here \u2014 keep it to your state or province. To disclose a more specific spot, use private messages. Safety is always first.",
            style = MaterialTheme.typography.labelSmall,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(6.dp))
        // Country picker
        ExposedDropdownMenuBox(expanded = countryMenuExpanded, onExpandedChange = { countryMenuExpanded = it }) {
            OutlinedTextField(
                value = country.displayName,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth().menuAnchor().noAutoFocus(),
                singleLine = true,
                label = { Text("Country") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryMenuExpanded) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Slate800,
                    unfocusedContainerColor = Slate800,
                    focusedIndicatorColor = Citrine,
                    unfocusedIndicatorColor = Color(0xFF3A4350),
                    focusedTextColor = TextHigh,
                    unfocusedTextColor = TextHigh,
                    cursorColor = Citrine,
                    focusedLabelColor = Aqua,
                    unfocusedLabelColor = Aqua,
                ),
                shape = RoundedCornerShape(12.dp),
            )
            ExposedDropdownMenu(expanded = countryMenuExpanded, onDismissRequest = { countryMenuExpanded = false }) {
                RegionData.countries.forEach { c ->
                    DropdownMenuItem(
                        text = { Text(c.displayName) },
                        onClick = {
                            selectedCountryShort = c.shortName
                            selectedSub = ""
                            countryMenuExpanded = false
                            commit()
                        },
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        // State/province picker (disabled if country has no subdivisions)
        if (country.subdivisions.isNotEmpty()) {
            ExposedDropdownMenuBox(expanded = subMenuExpanded, onExpandedChange = { subMenuExpanded = it }) {
                OutlinedTextField(
                    value = selectedSub.ifBlank { "" },
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor().noAutoFocus(),
                    singleLine = true,
                    label = { Text("State / Province") },
                    placeholder = { Text("Select\u2026") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subMenuExpanded) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Slate800,
                        unfocusedContainerColor = Slate800,
                        focusedIndicatorColor = Citrine,
                        unfocusedIndicatorColor = StoneLine,
                        focusedTextColor = TextHigh,
                        unfocusedTextColor = TextHigh,
                        cursorColor = Citrine,
                        focusedLabelColor = Aqua,
                        unfocusedLabelColor = Aqua,
                    ),
                    shape = RoundedCornerShape(12.dp),
                )
                ExposedDropdownMenu(expanded = subMenuExpanded, onDismissRequest = { subMenuExpanded = false }) {
                    country.subdivisions.forEach { sub ->
                        DropdownMenuItem(
                            text = { Text(sub) },
                            onClick = {
                                selectedSub = sub
                                subMenuExpanded = false
                                commit()
                            },
                        )
                    }
                }
            }
        } else {
            // No subdivisions listed (e.g. "Other / Not listed") — country-only is fine.
            Text(
                "No state/province list for this country — your profile will show the country only.",
                style = MaterialTheme.typography.labelSmall,
                color = DarkTextLow,
            )
        }
    }
}

@Composable
private fun ProfileField(label: String, value: String, onChange: (String) -> Unit) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Aqua, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth().noAutoFocus(),
            singleLine = label != "Bio",
            keyboardOptions = KeyboardOptions.Default,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Slate800,
                unfocusedContainerColor = Slate800,
                focusedIndicatorColor = Citrine,
                unfocusedIndicatorColor = StoneLine,
                focusedTextColor = TextHigh,
                unfocusedTextColor = TextHigh,
                cursorColor = Citrine,
            ),
            shape = RoundedCornerShape(12.dp),
        )
    }
}

/** Level + XP + streak card shown on the Profile, tappable to open the full
 *  Achievements screen. Contains the 75-achievement list with progress bars
 *  and XP meters. Reward badges are compact and labeled with their source
 *  achievement name. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LevelXpCard(
    level: Int,
    tier: LevelTier,
    totalXp: Int,
    streak: Int,
    progress: Float,
    onTileClick: () -> Unit,
    onViewAllAchievements: () -> Unit,
    onOpenBadges: () -> Unit,
) {
    val achievementsState by AchievementsRepository.state.collectAsStateWithLifecycle()
    val nextLevelXp = AchievementsRepository.xpForLevel(level + 1)

    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = shape, accent = Citrine, shadowElevation = 8.dp)
            .clip(shape)
            .glowingBorder(3.dp, Citrine.copy(alpha = 0.55f), shape)
            .clickable(onClick = onTileClick)
    ) {
        // High-res colorful agate slice background — fills the tile, cropped to the card shape
        Image(
            painter = painterResource(id = R.drawable.level_tile_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        // Heavy dark scrim so the white text, Citrine/Aqua badges, and progress bars read
        // clearly over the colorful stone background. The gradient darkens toward the bottom
        // where the badges and achievement list live, while still letting the agate colors
        // show through at the edges.
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
                )
        )
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
        val earnedCount = AchievementCatalog.all.count { AchievementCatalog.isEarned(it, achievementsState, level) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Citrine.copy(alpha = 0.9f), Aqua.copy(alpha = 0.7f))))
                    .glowingBorder(4.dp, Color.White.copy(alpha = 0.85f), CircleShape)
                    .shadow(8.dp, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    tier.emoji,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(2.dp),
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xCC1E1C16))
                    .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    "LEVEL $level",
                    style = MaterialTheme.typography.titleMedium,
                    color = Citrine,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .glowingBorder(2.dp, Citrine.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                )
                Text(tier.displayName, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                // Streak mini-card with rock texture, glow, and highlight — same treatment as the badge tiles.
                if (streak > 0) {
                    val streakShape = RoundedCornerShape(12.dp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .sculpted(shape = streakShape, accent = Aqua, shadowElevation = 4.dp)
                            .clip(streakShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16))
                                )
                            )
                            .glowingBorder(2.dp, Aqua.copy(alpha = 0.85f), streakShape)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(Aqua.copy(alpha = 0.45f), Aqua.copy(alpha = 0.10f), Color.Transparent)
                                    )
                                )
                                .glowingBorder(1.5.dp, Aqua.copy(alpha = 0.85f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = Aqua, modifier = Modifier.size(16.dp))
                        }
                        Spacer(Modifier.width(6.dp))
                        Text("$streak", style = MaterialTheme.typography.titleMedium, color = Aqua, fontWeight = FontWeight.Bold)
                    }
                }
        }
        }
        // Compact earned badges strip sits right below the level header and above the XP bar.
        if (earnedCount > 0) {
            Spacer(Modifier.height(10.dp))
            Text(
                "EARNED BADGES",
                style = MaterialTheme.typography.labelMedium,
                color = Citrine,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xCC1E1C16))
                    .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            )
            Spacer(Modifier.height(8.dp))
            val earnedAchievements = remember(achievementsState) {
                AchievementCatalog.all
                    .filter { AchievementCatalog.isEarned(it, achievementsState, level) }
                    .shuffled()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(12.dp)),
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    earnedAchievements.forEach { ach ->
                        CompactRewardBadge(achievement = ach)
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0x55FFFFFF))
                .glowingBorder(4.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                .shadow(4.dp, RoundedCornerShape(6.dp)),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFFD54F))
                    .glowingBorder(1.dp, Color(0xFFFFD54F).copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                    .shadow(4.dp, RoundedCornerShape(6.dp)),
            )
        }
        // XP remaining badge tucked under the right end of the progress bar.
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                text = "${nextLevelXp - totalXp} XP to Lvl ${level + 1}",
                style = MaterialTheme.typography.labelSmall,
                color = Aqua,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xE61E1C16))
                    .glowingBorder(1.5.dp, Aqua.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "$earnedCount / ${AchievementCatalog.size} achievements",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xE61E1C16))
                    .glowingBorder(2.dp, Citrine.copy(alpha = 0.70f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
        Spacer(Modifier.height(10.dp))
        // Bottom-left 'View all achievements' button — opens the full achievement list overlay.
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xE61E1C16))
                .glowingBorder(2.dp, Citrine.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                .clickable(onClick = onViewAllAchievements)
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

/** Compact reward badge — smaller than the old badge grid tiles, shows the
 *  source achievement name so users know what each badge was awarded for. */
@Composable
private fun CompactRewardBadge(achievement: Achievement) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xE61E1C16))
            .glowingBorder(3.dp, Citrine.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp),
    ) {
        Text(
            achievement.emoji,
            style = MaterialTheme.typography.labelMedium,
        )
        Spacer(Modifier.width(4.dp))
        Text(
            achievement.name,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
        )
    }
}

/** Single achievement row with progress bar and XP meter.
 *  Each row sits on a dark semi-transparent slab with a colored accent border so
 *  the icon, title, description, and progress information stay legible over the
 *  busy agate/rock background behind the card. */
@Composable
private fun AchievementRow(
    achievement: Achievement,
    progress: Int,
    earned: Boolean,
) {
    val progressFraction = if (achievement.threshold > 0) {
        (progress.toFloat() / achievement.threshold.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val accent = if (earned) Citrine else if (progressFraction > 0f) Aqua else StoneLine
    val rowShape = RoundedCornerShape(12.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(rowShape)
            .background(Color(0xDD1E1C16))
            .glowingBorder(2.dp, accent.copy(alpha = if (earned) 0.90f else 0.55f), rowShape)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Emoji icon with a bright palette backing so it pops off the dark row.
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                accent.copy(alpha = if (earned) 0.45f else 0.22f),
                                accent.copy(alpha = if (earned) 0.15f else 0.06f),
                                Color.Transparent,
                            )
                        )
                    )
                    .glowingBorder(2.dp, accent.copy(alpha = if (earned) 0.85f else 0.45f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    achievement.emoji,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (earned) Color.White else Color.White.copy(alpha = 0.75f),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    achievement.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
                Text(
                    achievement.description,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (earned) DarkTextHigh else Color.White.copy(alpha = 0.75f),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
            }
            // XP meter — shows the reward XP value on a dark pill so it never fades.
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xCC000000))
                    .glowingBorder(1.5.dp, accent.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    "+${achievement.rewardXp} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        // Progress bar + counter — thicker bar and high-contrast labels.
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0x33FFFFFF))
                    .glowingBorder(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(4.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (earned) {
                                Brush.horizontalGradient(listOf(Citrine, Color(0xFFFFE082), Aqua))
                            } else {
                                Brush.horizontalGradient(listOf(accent, accent.copy(alpha = 0.65f)))
                            }
                        )
                        .shadow(2.dp, RoundedCornerShape(4.dp)),
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                "$progress / ${achievement.threshold}",
                style = MaterialTheme.typography.labelMedium,
                color = if (earned) Citrine else Color.White,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.width(64.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End,
            )
        }
    }
}

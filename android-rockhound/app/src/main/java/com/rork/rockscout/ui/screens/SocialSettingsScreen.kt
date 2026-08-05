package com.rork.rockscout.ui.screens

import android.Manifest
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.ChevronRight
import com.rork.rockscout.ui.theme.DarkTextHigh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedButton
import com.rork.rockscout.ui.theme.DarkTextMid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.RockScoutApplication
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.IdentifyAccessManager
import com.rork.rockscout.data.PersistenceManager
import com.rork.rockscout.data.PurchaseManager
import com.rork.rockscout.data.StorageUsageCalculator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import kotlin.math.roundToInt
import com.rork.rockscout.data.AuroraRepository
import com.rork.rockscout.data.WorkScheduler
import com.rork.rockscout.data.SyncQueueManager
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.UpdateManager
import com.rork.rockscout.data.DeviceManager
import com.rork.rockscout.data.ApkInstaller
import com.rork.rockscout.data.PlayUpdateManager
import com.rork.rockscout.ui.components.BulkDownloadCard
import com.rork.rockscout.ui.components.LegalPillButton
import com.rork.rockscout.ui.components.DeleteConfirmDialog
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Cyan
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid

/**
 * Full-screen social settings page showing every discovery, location, and
 * notification toggle in one place. Reached from the cyan "Social Settings"
 * pill on the profile header card.
 *
 * Two labeled sections:
 *  - Discovery & Location (7 existing toggles)
 *  - Notifications (3 new toggles for friend requests, messages, new posts)
 */
@Composable
fun SocialSettingsScreen(
    navController: NavController,
) {
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val repo = AppRepository.instance
    val profile by repo.profile.collectAsStateWithLifecycle()
    val purchaseManager = PurchaseManager.instance
    val isPremium by purchaseManager.effectiveIsPremium.collectAsStateWithLifecycle()
    val auth = AuthRepository.instance
    val sessionStatus by auth.sessionStatus.collectAsStateWithLifecycle()
    val isSignedIn = sessionStatus is com.rork.rockscout.data.SessionStatus.Authenticated

    val accessManager = IdentifyAccessManager.instance
    val trialExpired by accessManager.trialExpired.collectAsStateWithLifecycle()
    val hasLocationUnlock by accessManager.hasLocationUnlock.collectAsStateWithLifecycle()
    val locationLocked = remember(isPremium, trialExpired, hasLocationUnlock) {
        accessManager.isLocationLocked(isPremium)
    }

    val current by repo.currentLocation.collectAsStateWithLifecycle()
    val locationRefresh by repo.locationRefreshTrigger.collectAsStateWithLifecycle()
    val nearby = remember(current, profile.locationMonitoring, locationRefresh) {
        SeedData.allLocations
            .map { it to AppRepository.distanceMiles(current.first, current.second, it.latitude, it.longitude) }
            .let { list ->
                if (profile.locationMonitoring) list.filter { it.second <= 100.0 } else list
            }
            .sortedBy { it.second }
            .take(3)
    }

    // Tracks which setting requested notification permission so the launcher
    // callback only flips the matching toggle on when the user actually grants
    // POST_NOTIFICATIONS. Pre-TIRAMISU devices are always treated as granted.
    var pendingNotifToggle by remember { mutableStateOf<String?>(null) }
    var showDeleteAccountConfirm by remember { mutableStateOf(false) }
    var showStopSharingConfirm by remember { mutableStateOf(false) }
    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        val notifGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            results[Manifest.permission.POST_NOTIFICATIONS] == true
        when (pendingNotifToggle) {
            "nearby_alerts" -> if (notifGranted) repo.setNearbyAlertsEnabled(true)
            "nearby_friends" -> {
                if (notifGranted) {
                    repo.setLocationMonitoring(true)
                    repo.setNearbyFriendsEnabled(true)
                }
            }
            "nearby_friends_alerts" -> if (notifGranted) repo.setNearbyFriendsAlertsEnabled(true)
            "weather_alerts" -> {
                if (notifGranted) {
                    repo.setWeatherAlertsEnabled(true)
                    WorkScheduler.scheduleWeatherChain(
                        navController.context.applicationContext,
                    )
                    WorkScheduler.runWeatherCheckNow(
                        navController.context.applicationContext,
                    )
                }
            }
        }
        pendingNotifToggle = null
    }

    ScreenScaffold(
        title = "Social Settings",
        onBack = { navController.popBackStack() },
        actions = {
            LegalPillButton(
                onClick = { navController.navigate(Routes.disclaimer(isGate = false)) },
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ── Section 1: Content Filter ──
            SectionHeader("Content Filter")
            Text(
                "Choose how much profanity is filtered from text in posts, messages, and other user-generated content. Sexually explicit words, racial slurs, and severe terms are always filtered. Image moderation is always on.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
            )

            val profanityLevel = remember(profile.profanityFilterLevel) {
                com.rork.rockscout.data.ProfanityFilter.ProfanityLevel.fromValue(profile.profanityFilterLevel)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1A1812).copy(alpha = 0.6f))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    com.rork.rockscout.data.ProfanityFilter.ProfanityLevel.entries.forEach { level ->
                        val isSelected = profanityLevel == level
                        val label = when (level) {
                            com.rork.rockscout.data.ProfanityFilter.ProfanityLevel.OFF -> "Off"
                            com.rork.rockscout.data.ProfanityFilter.ProfanityLevel.LOW -> "Low (default)"
                            com.rork.rockscout.data.ProfanityFilter.ProfanityLevel.STRICT -> "Strict"
                        }
                        val desc = when (level) {
                            com.rork.rockscout.data.ProfanityFilter.ProfanityLevel.OFF ->
                                "Only sexually explicit words, racial slurs, and severe terms (retard, rape) are asterisked. All other profanity is shown."
                            com.rork.rockscout.data.ProfanityFilter.ProfanityLevel.LOW ->
                                "Same as Off, plus \u201cfuck\u201d variants are silently asterisked. Mild profanity (shit, bitch, ass, etc.) is shown."
                            com.rork.rockscout.data.ProfanityFilter.ProfanityLevel.STRICT ->
                                "Everything except \u201chell\u201d and \u201cdamn\u201d is asterisked."
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) Citrine.copy(alpha = 0.12f)
                                    else Color.Transparent
                                )
                                .clickable { repo.setProfanityFilterLevel(level) }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) Citrine else Color(0xFF2A2820)
                                    )
                                    .border(
                                        1.5.dp,
                                        if (isSelected) Citrine else TextLow,
                                        RoundedCornerShape(10.dp),
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF1A1812),
                                        modifier = Modifier.size(14.dp),
                                    )
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Citrine else DarkTextMid,
                                )
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMid,
                                )
                            }
                        }
                        if (level != com.rork.rockscout.data.ProfanityFilter.ProfanityLevel.STRICT) {
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
            }

            // ── Section 2: Discovery & Location ──
            SectionHeader("Discovery & Location")

            ToggleRow(
                icon = Icons.Filled.Group,
                title = if (profile.clubEnabled) "RockScout Social ON" else "RockScout Social OFF",
                helper = if (profile.clubEnabled)
                    "Discoverable to connected RockScouts — scan, message & ping."
                else "Turn on to scan for connected RockScouts, message, and ping.",
                accent = if (profile.clubEnabled) Citrine else TextLow,
                checked = profile.clubEnabled,
                onCheckedChange = { enabled ->
                    if (enabled && !isSignedIn) {
                        navController.navigate(Routes.SIGN_IN)
                    } else {
                        repo.setClubEnabled(enabled)
                    }
                },
            )

            ToggleDivider()

            ToggleRow(
                icon = Icons.Filled.LocationOn,
                title = if (locationLocked) "Location monitoring locked"
                    else if (profile.locationMonitoring) "Location monitoring ON"
                    else "Location monitoring OFF",
                helper = if (locationLocked)
                    "Free trial ended — go Premium or donate to unlock live nearby alerts."
                else if (profile.locationMonitoring && nearby.firstOrNull()?.first?.name != null && nearby.firstOrNull()?.second != null) {
                    val first = nearby.first()
                    "Nearest: ${first.first.name} · ${first.second.toInt()} mi"
                }
                else "Turn on to enable RockScout social, dig site tracking, trip planner routing, and more!",
                accent = if (profile.locationMonitoring && !locationLocked) Color(0xFF5CC98C)
                    else if (locationLocked) Citrine else TextLow,
                checked = profile.locationMonitoring && !locationLocked,
                locked = locationLocked,
                onCheckedChange = { enabled ->
                    if (locationLocked) {
                        navController.navigate(Routes.PAYWALL)
                    } else if (enabled) {
                        val perms = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            perms.add(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        permissionsLauncher.launch(perms.toTypedArray())
                        repo.setLocationMonitoring(enabled)
                    } else {
                        showStopSharingConfirm = true
                    }
                },
            )

            ToggleDivider()

            ToggleRow(
                icon = Icons.Filled.Map,
                title = if (profile.nearbyPlacesEnabled) "Nearby spots ON" else "Nearby spots OFF",
                helper = if (profile.nearbyPlacesEnabled)
                    "Show dig sites closest to you on the locations screen."
                else "Turn on to surface the nearest mines, shops, and digs.",
                accent = if (profile.nearbyPlacesEnabled) Aqua else TextLow,
                checked = profile.nearbyPlacesEnabled,
                onCheckedChange = { enabled ->
                    repo.setNearbyPlacesEnabled(enabled)
                },
            )

            ToggleDivider()

            ToggleRow(
                icon = Icons.Filled.Notifications,
                title = if (profile.nearbyAlertsEnabled) "Nearby alerts ON" else "Nearby alerts OFF",
                helper = if (profile.nearbyAlertsEnabled)
                    "You'll get a push notification when you're near a dig site, mine, or shop."
                else "Turn on to get notified when you come near a dig site, mine, or shop.",
                accent = if (profile.nearbyAlertsEnabled) Citrine else TextLow,
                checked = profile.nearbyAlertsEnabled,
                onCheckedChange = { enabled ->
                    if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        pendingNotifToggle = "nearby_alerts"
                        permissionsLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
                    } else {
                        repo.setNearbyAlertsEnabled(enabled)
                    }
                },
            )

            ToggleDivider()

            ToggleRow(
                icon = Icons.Filled.Person,
                title = if (profile.nearbyFriendsEnabled) "Nearby friends ON" else "Nearby friends OFF",
                helper = if (profile.nearbyFriendsEnabled)
                    "Scan for connected friends within 50 miles of your location."
                else "Turn on to monitor for nearby RockScout friends within 50 miles.",
                accent = if (profile.nearbyFriendsEnabled) Aqua else TextLow,
                checked = profile.nearbyFriendsEnabled,
                onCheckedChange = { enabled ->
                    if (enabled && !isSignedIn) {
                        navController.navigate(Routes.SIGN_IN)
                    } else if (enabled && !profile.locationMonitoring) {
                        val perms = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            perms.add(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        pendingNotifToggle = "nearby_friends"
                        permissionsLauncher.launch(perms.toTypedArray())
                    } else {
                        repo.setNearbyFriendsEnabled(enabled)
                    }
                },
            )

            ToggleDivider()

            ToggleRow(
                icon = Icons.Filled.Notifications,
                title = if (profile.nearbyFriendsAlertsEnabled)
                    "Get notified when friends are nearby ON"
                else "Get notified when friends are nearby OFF",
                helper = if (profile.nearbyFriendsAlertsEnabled)
                    "You'll get a push notification when a friend is within 50 miles."
                else "Turn on to get a push notification when a friend comes within 50 miles.",
                accent = if (profile.nearbyFriendsAlertsEnabled) Citrine else TextLow,
                checked = profile.nearbyFriendsAlertsEnabled,
                onCheckedChange = { enabled ->
                    if (enabled && !isSignedIn) {
                        navController.navigate(Routes.SIGN_IN)
                    } else if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        pendingNotifToggle = "nearby_friends_alerts"
                        permissionsLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
                    } else {
                        repo.setNearbyFriendsAlertsEnabled(enabled)
                    }
                },
            )

            // ── Section 3: Notifications ──
            Spacer(Modifier.height(8.dp))
            SectionHeader("Notifications")
            Text(
                "Control which push notifications you receive. " +
                    "In-app notifications are always shown when you open the app.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
            )

            ToggleRow(
                icon = Icons.Filled.Group,
                title = "Friend requests",
                helper = "Get an instant push notification when someone sends you a friend request.",
                accent = if (profile.notifFriendRequestsEnabled) Citrine else TextLow,
                checked = profile.notifFriendRequestsEnabled,
                onCheckedChange = { enabled ->
                    repo.setNotifFriendRequestsEnabled(enabled)
                },
            )

            ToggleDivider()

            ToggleRow(
                icon = Icons.Filled.Notifications,
                title = "New messages",
                helper = "Get an instant push notification when you receive a private message.",
                accent = if (profile.notifMessagesEnabled) Citrine else TextLow,
                checked = profile.notifMessagesEnabled,
                onCheckedChange = { enabled ->
                    repo.setNotifMessagesEnabled(enabled)
                },
            )

            ToggleDivider()

            ToggleRow(
                icon = Icons.Filled.Notifications,
                title = "New posts",
                helper = "Get a periodic summary of new posts from your RockScout friends (every 1 hour).",
                accent = if (profile.notifNewPostsEnabled) Citrine else TextLow,
                checked = profile.notifNewPostsEnabled,
                onCheckedChange = { enabled ->
                    repo.setNotifNewPostsEnabled(enabled)
                },
            )

            ToggleDivider()

            ToggleRow(
                icon = Icons.Filled.SwapHoriz,
                title = "Trade interest",
                helper = "Get an instant push when someone is interested in your listing.",
                accent = if (profile.notifTradeInterestEnabled) Citrine else TextLow,
                checked = profile.notifTradeInterestEnabled,
                onCheckedChange = { enabled ->
                    repo.setNotifTradeInterestEnabled(enabled)
                },
            )

            ToggleDivider()

            ToggleRow(
                icon = Icons.Filled.CheckCircle,
                title = "Marked as traded",
                helper = "Get an instant push when a listing you're interested in is marked traded.",
                accent = if (profile.notifMarkedTradedEnabled) Citrine else TextLow,
                checked = profile.notifMarkedTradedEnabled,
                onCheckedChange = { enabled ->
                    repo.setNotifMarkedTradedEnabled(enabled)
                },
            )

            ToggleDivider()

            ToggleRow(
                icon = Icons.Filled.LocationOn,
                title = "Location/specimen approved",
                helper = "Get an instant push when your submitted location or specimen is auto-verified or Dev-verified.",
                accent = if (profile.notifLocationApprovedEnabled) Citrine else TextLow,
                checked = profile.notifLocationApprovedEnabled,
                onCheckedChange = { enabled ->
                    repo.setNotifLocationApprovedEnabled(enabled)
                },
            )

            ToggleDivider()

            ToggleRow(
                icon = Icons.Filled.Favorite,
                title = "Likes, comments & replies summary",
                helper = "Get a 1-hour summary push of engagement on your profile posts.",
                accent = if (profile.notifEngagementSummaryEnabled) Citrine else TextLow,
                checked = profile.notifEngagementSummaryEnabled,
                onCheckedChange = { enabled ->
                    repo.setNotifEngagementSummaryEnabled(enabled)
                },
            )

            // Note about always-on moderation notifications
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1A1812).copy(alpha = 0.6f))
                    .glowingBorder(1.dp, Color(0xFFFF6B3D).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF6B3D),
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Image rejections and report/ban alerts are always on — they cannot be turned off.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMid,
                )
            }

            // ── Section 4: Storage ──
            Spacer(Modifier.height(8.dp))
            SectionHeader("Storage")
            Text(
                "Choose how much device storage RockScout can use for offline images and satellite map tiles.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
            )

            // ── Sync Now button ──
            // Manually forces an immediate upload of all pending local changes
            // (captures, saved images, field journal entries, trips) to Supabase.
            val syncQueuePending by SyncQueueManager.pendingCount.collectAsStateWithLifecycle()
            var isSyncingNow by remember { mutableStateOf(false) }
            var syncResultMsg by remember { mutableStateOf<String?>(null) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .glowingBorder(1.dp, Aqua.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF0A1410).copy(alpha = 0.85f),
                                Color(0xFF08100C).copy(alpha = 0.65f),
                            )
                        )
                    )
                    .clickable(enabled = !isSyncingNow && syncQueuePending > 0) {
                        isSyncingNow = true
                        syncResultMsg = null
                        scope.launch(Dispatchers.IO) {
                            val synced = SyncQueueManager.drain()
                            isSyncingNow = false
                            syncResultMsg = if (synced > 0) {
                                "Synced $synced item${if (synced == 1) "" else "s"} successfully"
                            } else if (syncQueuePending == 0) {
                                "Nothing to sync — all caught up"
                            } else {
                                "Sync failed — will retry automatically"
                            }
                        }
                    }
                    .padding(horizontal = 14.dp, vertical = 14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Aqua.copy(alpha = if (isSyncingNow) 0.08f else 0.18f))
                            .glowingBorder(1.dp, Aqua.copy(alpha = if (isSyncingNow) 0.25f else 0.45f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            if (isSyncingNow) Icons.Filled.CloudUpload else Icons.Filled.Sync,
                            contentDescription = null,
                            tint = Aqua,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isSyncingNow) "Syncing…" else "Sync Now",
                            style = MaterialTheme.typography.titleMedium.copy(
                                shadow = Shadow(color = Color.Black.copy(alpha = 0.8f), offset = Offset(0f, 1f), blurRadius = 4f),
                            ),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = when {
                                isSyncingNow -> "Uploading pending captures, photos, and journal entries…"
                                syncResultMsg != null -> syncResultMsg!!
                                syncQueuePending > 0 -> "$syncQueuePending pending item${if (syncQueuePending == 1) "" else "s"} waiting to sync"
                                else -> "All changes synced — nothing pending"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), offset = Offset(0f, 1f), blurRadius = 3f),
                            ),
                            color = if (syncResultMsg != null && syncResultMsg!!.contains("failed")) Color(0xFFFF6B3D) else TextMid,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            // Live storage usage card: shows cached map tiles + uploaded rock
            // images currently on-device, recomputed whenever the user enters
            // the screen or finishes clearing the cache.
            var usage by remember { mutableStateOf<StorageUsageCalculator.Usage?>(null) }
            var usageRefreshKey by remember { mutableStateOf(0) }
            LaunchedEffect(usageRefreshKey) {
                val context = navController.context.applicationContext
                usage = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    StorageUsageCalculator.compute(context)
                }
            }

            // Per-category clear state. Each clear button shows a confirm
            // dialog, runs the delete on a background thread, then refreshes
            // the usage card so the freed space is reflected immediately.
            var showClearTilesConfirm by remember { mutableStateOf(false) }
            var isClearingTiles by remember { mutableStateOf(false) }
            var lastTilesFreed by remember { mutableStateOf(0L) }
            var showTilesResult by remember { mutableStateOf(false) }

            StorageUsageCard(
                usage = usage,
                isClearingTiles = isClearingTiles,
                showTilesResult = showTilesResult,
                lastTilesFreed = lastTilesFreed,
                onClearTiles = { showClearTilesConfirm = true },
            )

            if (showClearTilesConfirm) {
                AlertDialog(
                    onDismissRequest = { if (!isClearingTiles) showClearTilesConfirm = false },
                    title = { Text("Clear cached map tiles?", style = MaterialTheme.typography.headlineSmall) },
                    text = {
                        Text(
                            "This deletes every downloaded satellite / topo map tile stored on your device. " +
                                "Saved trips, specimen markers, and offline trip caches are NOT affected — only the tile files. " +
                                "Tiles will re-download as needed the next time you view a map with a signal.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    confirmButton = {
                        Button(
                            enabled = !isClearingTiles,
                            onClick = {
                                isClearingTiles = true
                                val context = navController.context.applicationContext
                                scope.launch(Dispatchers.IO) {
                                    val freed = StorageUsageCalculator.clearMapTiles(context)
                                    lastTilesFreed = freed
                                    isClearingTiles = false
                                    showClearTilesConfirm = false
                                    showTilesResult = true
                                    usageRefreshKey++
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B3D)),
                        ) {
                            Text("Clear tiles", color = Ink, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            enabled = !isClearingTiles,
                            onClick = { showClearTilesConfirm = false },
                        ) {
                            Text("Cancel", color = TextLow)
                        }
                    },
                )
            }


            var cacheMode by remember { mutableStateOf(PersistenceManager.loadCacheSizeMode()) }
            val isMaxCache = cacheMode == "max"
            var showCacheConfirm by remember { mutableStateOf(false) }

            ToggleRow(
                icon = Icons.Filled.Storage,
                title = if (isMaxCache) "Maximum cache (2GB)" else "Standard cache (150MB)",
                helper = if (isMaxCache)
                    "Stores up to 2GB of recently viewed specimen photos, satellite tiles, trip route caches, and location downloads. Older items are cleared automatically when the cap is reached to make room for new ones."
                else "Stores up to 150MB of recently viewed images and map tiles. Automatically manages itself — older items are removed as new ones come in.",
                accent = if (isMaxCache) Citrine else TextLow,
                checked = isMaxCache,
                onCheckedChange = { enabled ->
                    if (enabled) {
                        showCacheConfirm = true
                    } else {
                        cacheMode = "standard"
                        PersistenceManager.saveCacheSizeMode("standard")
                        RockScoutApplication.reconfigureCacheSize(
                            navController.context.applicationContext,
                            "standard",
                        )
                    }
                },
            )

            Spacer(Modifier.height(8.dp))

            // Bulk-download action — mirrors the pill in the Specimen Database
            // header so users can kick off the same download from Storage
            // settings. Gated by the 2 GB cache toggle above.
            BulkDownloadCard(
                isMaxCache = isMaxCache,
                onEnableMaxCache = { showCacheConfirm = true },
            )

            // Auto-sync toggle — when on, schedules a nightly WorkManager run
            // that refreshes the offline image cache while the device is
            // charging + idle + on WiFi, so new specimens / guide images land
            // on-device without the user having to remember. Gated behind the
            // 2 GB Maximum cache toggle (a 150 MB cache would evict images as
            // fast as the sync writes them).
            var nightlySync by remember { mutableStateOf(PersistenceManager.isNightlySyncEnabled()) }
            val lastSyncMs = remember(nightlySync) { PersistenceManager.loadNightlySyncLastRun() }
            val lastSyncLabel = remember(lastSyncMs) { formatLastSync(lastSyncMs) }

            ToggleRow(
                icon = Icons.Filled.Bedtime,
                title = "Auto-sync offline database at night",
                helper = if (!isMaxCache) {
                    "Requires Maximum (2 GB) cache. Enable it above so the nightly sync has room to store refreshed images."
                } else if (nightlySync) {
                    "While your device is charging and idle overnight (around 2:30 AM, WiFi only), RockScout refreshes the offline specimen + guide cache so new images land automatically. $lastSyncLabel"
                } else {
                    "Turn on to have RockScout refresh the offline specimen + guide cache automatically while your device is charging and idle overnight (WiFi only, ~2:30 AM). No cellular data used."
                },
                accent = if (nightlySync && isMaxCache) Citrine else TextLow,
                checked = nightlySync && isMaxCache,
                locked = !isMaxCache,
                onCheckedChange = { enabled ->
                    if (!isMaxCache) {
                        showCacheConfirm = true
                        return@ToggleRow
                    }
                    nightlySync = enabled
                    PersistenceManager.saveNightlySyncEnabled(enabled)
                    val appContext = navController.context.applicationContext
                    if (enabled) {
                        WorkScheduler.scheduleNightlySync(appContext)
                    } else {
                        WorkScheduler.cancelNightlySync(appContext)
                    }
                },
            )

            Spacer(Modifier.height(4.dp))

            // Explanatory comparison text
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1A1812).copy(alpha = 0.6f))
                    .glowingBorder(1.dp, Aqua.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
            ) {
                Text(
                    "What's the difference?",
                    style = MaterialTheme.typography.titleSmall,
                    color = Aqua,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Standard (150MB):",
                    style = MaterialTheme.typography.labelMedium,
                    color = Citrine,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "Caches the most recently viewed specimen photos and map tiles. If you scroll through hundreds of specimens, earlier ones may need to re-download when you go back to them. Good for most users.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMid,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Maximum (2GB):",
                    style = MaterialTheme.typography.labelMedium,
                    color = Citrine,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "Caches up to 2GB of specimen photos, satellite maps, trip route areas, and location downloads. Older items are cleared automatically when the cap is reached, so it does not keep every image permanently. Good for heavier off-grid use without downloading the full 3.5GB library.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMid,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Both modes automatically manage storage — older items are removed first when the limit is reached. You can switch anytime.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextLow,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            // ── Clear Offline Data (specimen + guide images only) ──
            // Distinct from "Clear all offline cache" below — this wipes only
            // the bulk-downloaded specimen photos and educational / guide
            // illustrations (Coil disk image cache), leaving map tiles intact
            // so offline maps still work after the wipe.
            Spacer(Modifier.height(10.dp))

            var showClearOfflineDataConfirm by remember { mutableStateOf(false) }
            var isClearingOfflineData by remember { mutableStateOf(false) }
            var lastOfflineDataFreed by remember { mutableStateOf(0L) }
            var showOfflineDataResult by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .glowingBorder(1.dp, Color(0xFFE0A23A).copy(alpha = 0.55f), RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF1A1408).copy(alpha = 0.85f),
                                Color(0xFF120E06).copy(alpha = 0.65f),
                            )
                        )
                    )
                    .clickable(enabled = !isClearingOfflineData) { showClearOfflineDataConfirm = true }
                    .padding(horizontal = 14.dp, vertical = 14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE0A23A).copy(alpha = 0.18f))
                            .glowingBorder(1.dp, Color(0xFFE0A23A).copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.PhotoLibrary,
                            contentDescription = null,
                            tint = Color(0xFFE0A23A),
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isClearingOfflineData) "Clearing offline data…" else "Clear Offline Data",
                            style = MaterialTheme.typography.titleMedium.copy(
                                shadow = Shadow(color = Color.Black.copy(alpha = 0.8f), offset = Offset(0f, 1f), blurRadius = 4f),
                            ),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = if (isClearingOfflineData)
                                "Deleting downloaded specimen photos and guide images — please wait."
                            else if (showOfflineDataResult && lastOfflineDataFreed > 0)
                                "Freed ${formatBytes(lastOfflineDataFreed)}. Images will re-download as you browse."
                            else
                                "Removes all downloaded specimen photos and educational / guide illustrations from this device. Offline map tiles are NOT affected.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), offset = Offset(0f, 1f), blurRadius = 3f),
                            ),
                            color = TextMid,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            if (showClearOfflineDataConfirm) {
                AlertDialog(
                    onDismissRequest = { if (!isClearingOfflineData) showClearOfflineDataConfirm = false },
                    title = { Text("Clear Offline Data?", style = MaterialTheme.typography.headlineSmall) },
                    text = {
                        Text(
                            "This permanently deletes every downloaded specimen photo and educational / guide illustration stored on your device. " +
                                "You'll immediately regain that storage space. " +
                                "Offline map tiles are NOT affected — your cached satellite / topo maps will still work. " +
                                "Any saved specimens, collection entries, and trips are unaffected — only the image files. " +
                                "Images will re-download as needed the next time you view them with a signal, or you can bulk-download them again anytime.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    confirmButton = {
                        Button(
                            enabled = !isClearingOfflineData,
                            onClick = {
                                isClearingOfflineData = true
                                val context = navController.context.applicationContext
                                scope.launch(Dispatchers.IO) {
                                    val freed = RockScoutApplication.clearOfflineImageData(context)
                                    // Drop the bulk-download progress state too, since
                                    // the underlying image files are gone and any
                                    // "X% complete" indicator is no longer valid.
                                    PersistenceManager.clearBulkDownloadState()
                                    lastOfflineDataFreed = freed
                                    isClearingOfflineData = false
                                    showClearOfflineDataConfirm = false
                                    showOfflineDataResult = true
                                    // Refresh the usage card so the freed space is reflected.
                                    usageRefreshKey++
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0A23A)),
                        ) {
                            Text("Clear now", color = Ink, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            enabled = !isClearingOfflineData,
                            onClick = { showClearOfflineDataConfirm = false },
                        ) {
                            Text("Cancel", color = TextLow)
                        }
                    },
                )
            }

            // ── Clear all offline caches ──
            Spacer(Modifier.height(12.dp))

            var showClearConfirm by remember { mutableStateOf(false) }
            var isClearing by remember { mutableStateOf(false) }
            var lastClearedBytes by remember { mutableStateOf(0L) }
            var showClearResult by remember { mutableStateOf(false) }

            // Full-width "Clear offline cache" button — frees space used by
            // all downloaded map tiles and cached specimen/field-capture images.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .glowingBorder(1.dp, Color(0xFFFF6B3D).copy(alpha = 0.55f), RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF1A1208).copy(alpha = 0.85f),
                                Color(0xFF120C06).copy(alpha = 0.65f),
                            )
                        )
                    )
                    .clickable(enabled = !isClearing) { showClearConfirm = true }
                    .padding(horizontal = 14.dp, vertical = 14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFF6B3D).copy(alpha = 0.18f))
                            .glowingBorder(1.dp, Color(0xFFFF6B3D).copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.DeleteSweep,
                            contentDescription = null,
                            tint = Color(0xFFFF6B3D),
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isClearing) "Clearing cache…" else "Clear all offline cache",
                            style = MaterialTheme.typography.titleMedium.copy(
                                shadow = Shadow(color = Color.Black.copy(alpha = 0.8f), offset = Offset(0f, 1f), blurRadius = 4f),
                            ),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = if (isClearing)
                                "Deleting map tiles and cached images — please wait."
                            else if (showClearResult && lastClearedBytes > 0)
                                "Freed ${formatBytes(lastClearedBytes)}. Cache will refill as you browse."
                            else
                                "Removes all downloaded satellite map tiles and cached specimen / field-capture images to free up storage.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), offset = Offset(0f, 1f), blurRadius = 3f),
                            ),
                            color = TextMid,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            if (showClearConfirm) {
                AlertDialog(
                    onDismissRequest = { if (!isClearing) showClearConfirm = false },
                    title = { Text("Clear all offline cache?", style = MaterialTheme.typography.headlineSmall) },
                    text = {
                        Text(
                            "This permanently deletes every downloaded map tile and every cached specimen, field-capture, and community image stored on your device. " +
                                "You'll immediately regain that storage space. " +
                                "Offline trip caches and saved specimen markers are NOT affected — only the tile and image files used to display them. " +
                                "Images and tiles will re-download as needed the next time you view them with a signal.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    confirmButton = {
                        Button(
                            enabled = !isClearing,
                            onClick = {
                                isClearing = true
                                val context = navController.context.applicationContext
                                scope.launch(Dispatchers.IO) {
                                    val freed = RockScoutApplication.clearAllOfflineCaches(context)
                                    // Also wipe the persisted cache timestamps so the
                                    // sync-status indicators reset to "Not cached".
                                    com.rork.rockscout.data.PersistenceManager.clearAllCacheTimestamps()
                                    lastClearedBytes = freed
                                    isClearing = false
                                    showClearConfirm = false
                                    showClearResult = true
                                    // Refresh the usage card so the freed space is reflected.
                                    usageRefreshKey++
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B3D)),
                        ) {
                            Text("Clear now", color = Ink, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            enabled = !isClearing,
                            onClick = { showClearConfirm = false },
                        ) {
                            Text("Cancel", color = TextLow)
                        }
                    },
                )
            }

            if (showCacheConfirm) {
                AlertDialog(
                    onDismissRequest = { showCacheConfirm = false },
                    title = { Text("Enable Maximum cache?", style = MaterialTheme.typography.headlineSmall) },
                    text = {
                        Text(
                            "This will allow RockScout to use up to 2GB of storage on your device for offline specimen photos, satellite map tiles, cached trip routes, and location downloads. That's enough for all 3,500+ specimen images, every dig site map, and trip area caches with room to spare. Tap OK to enable.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                cacheMode = "max"
                                PersistenceManager.saveCacheSizeMode("max")
                                RockScoutApplication.reconfigureCacheSize(
                                    navController.context.applicationContext,
                                    "max",
                                )
                                showCacheConfirm = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Citrine),
                        ) {
                            Text("OK", color = Ink, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCacheConfirm = false }) {
                            Text("Cancel", color = TextLow)
                        }
                    },
                )
            }

            // ── Manage Devices ──
            Spacer(Modifier.height(8.dp))
            SectionHeader("Devices")
            val deviceOverLimit by DeviceManager.deviceOverLimit.collectAsStateWithLifecycle()
            val devices by DeviceManager.devices.collectAsStateWithLifecycle()
            val rawIsPremium by purchaseManager.isPremium.collectAsStateWithLifecycle()
            if (isSignedIn && rawIsPremium) {
                DarkCard(modifier = Modifier.fillMaxWidth(), accent = if (deviceOverLimit) Danger else Aqua) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Routes.MANAGE_DEVICES) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            Icons.Filled.Devices,
                            contentDescription = null,
                            tint = if (deviceOverLimit) Danger else Aqua,
                            modifier = Modifier.size(22.dp),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Manage Devices",
                                style = MaterialTheme.typography.titleSmall,
                                color = DarkTextHigh,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                if (deviceOverLimit) "Over limit — premium paused"
                                else "${devices.size} of 3 devices used",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (deviceOverLimit) Danger else DarkTextMid,
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

            // ── Update App ──
            // Lets the user manually check for and install app updates
            // directly from settings, without needing to go to the Play Store.
            Spacer(Modifier.height(8.dp))
            SectionHeader("App Updates")

            val updateInfo by UpdateManager.updateInfo.collectAsStateWithLifecycle()
            val apkStatus by ApkInstaller.status.collectAsStateWithLifecycle()
            val apkProgress by ApkInstaller.progress.collectAsStateWithLifecycle()
            val playReady by PlayUpdateManager.readyToInstall.collectAsStateWithLifecycle()
            var isCheckingForUpdate by remember { mutableStateOf(false) }
            var updateCheckMsg by remember { mutableStateOf<String?>(null) }
            val context = navController.context.applicationContext

            // Trigger a manual update check when this section first appears
            LaunchedEffect(Unit) {
                if (updateInfo == null && !isCheckingForUpdate) {
                    isCheckingForUpdate = true
                    UpdateManager.checkForUpdate(context)
                    // Give the check a moment, then clear the spinner
                    kotlinx.coroutines.delay(3000L)
                    isCheckingForUpdate = false
                }
            }

            // Auto-clear the "up to date" message after 4 seconds
            LaunchedEffect(updateCheckMsg) {
                if (updateCheckMsg != null) {
                    kotlinx.coroutines.delay(4000L)
                    updateCheckMsg = null
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .glowingBorder(
                        1.dp,
                        if (updateInfo != null) Success.copy(alpha = 0.55f) else Citrine.copy(alpha = 0.35f),
                        RoundedCornerShape(14.dp),
                    )
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF0A1410).copy(alpha = 0.85f),
                                Color(0xFF08100C).copy(alpha = 0.65f),
                            )
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (updateInfo != null) Success.copy(alpha = 0.18f)
                                else Citrine.copy(alpha = 0.18f)
                            )
                            .glowingBorder(
                                1.dp,
                                if (updateInfo != null) Success.copy(alpha = 0.45f)
                                else Citrine.copy(alpha = 0.45f),
                                RoundedCornerShape(12.dp),
                        ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            if (updateInfo != null) Icons.Filled.CloudUpload else Icons.Filled.Sync,
                            contentDescription = null,
                            tint = if (updateInfo != null) Success else Citrine,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when {
                                apkStatus == ApkInstaller.Status.DOWNLOADING -> "Downloading update…"
                                apkStatus == ApkInstaller.Status.VERIFYING -> "Verifying update…"
                                apkStatus == ApkInstaller.Status.INSTALLING -> "Installing update…"
                                updateInfo != null -> "Update available: v${updateInfo?.latestVersionName}"
                                isCheckingForUpdate -> "Checking for updates…"
                                else -> "Check for updates"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                shadow = Shadow(color = Color.Black.copy(alpha = 0.8f), offset = Offset(0f, 1f), blurRadius = 4f),
                            ),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = when {
                                apkStatus == ApkInstaller.Status.DOWNLOADING ->
                                    if (apkProgress > 0) "$apkProgress% downloaded" else "Starting download…"
                                apkStatus == ApkInstaller.Status.VERIFYING ->
                                    "Making sure the download is complete and genuine…"
                                apkStatus == ApkInstaller.Status.INSTALLING ->
                                    "Confirm the install when Android asks."
                                updateInfo != null ->
                                    updateInfo?.changelog ?: "Tap Update Now to install."
                                isCheckingForUpdate -> "Contacting the update server…"
                                updateCheckMsg != null -> updateCheckMsg!!
                                else -> "Tap to check if a new version is available."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), offset = Offset(0f, 1f), blurRadius = 3f),
                            ),
                            color = TextMid,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    // Update Now button — shown when an update is available
                    if (updateInfo != null && apkStatus == ApkInstaller.Status.IDLE) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Success)
                                .glowingBorder(2.dp, Cyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .clickable { UpdateManager.downloadAndInstall(context) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Update Now",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Ink,
                            )
                        }
                    } else if (!isCheckingForUpdate && apkStatus == ApkInstaller.Status.IDLE && updateInfo == null) {
                        // Check for updates button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Citrine.copy(alpha = 0.2f))
                                .glowingBorder(1.dp, Citrine.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .clickable {
                                    isCheckingForUpdate = true
                                    updateCheckMsg = null
                                    UpdateManager.checkForUpdate(context)
                                    scope.launch {
                                        kotlinx.coroutines.delay(3000L)
                                        isCheckingForUpdate = false
                                        // Read the result after the check
                                        updateCheckMsg = if (UpdateManager.updateInfo.value != null) {
                                            "Update found! Tap Update Now."
                                        } else {
                                            "You're up to date!"
                                        }
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Check",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Citrine,
                            )
                        }
                    }
                }
            }

            // Play update ready to install
            if (playReady) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .glowingBorder(1.dp, Success.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .background(Success.copy(alpha = 0.1f))
                        .clickable { PlayUpdateManager.completeUpdate() }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Restart & install update",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Success,
                    )
                }
            }

            // ── Account deletion ──
            // Placed at the very bottom of social settings so it is out of the
            // way but still reachable. Only visible while signed in.
            if (isSignedIn) {
                Spacer(Modifier.height(24.dp))
                SectionHeader("Account")
                DarkCard(modifier = Modifier.fillMaxWidth(), accent = Danger) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Delete account",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Remove your account and all stored data from this device. This cannot be undone.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMid,
                        )
                        Spacer(Modifier.height(10.dp))
                        SculptedOutlinedButton(
                            text = "Delete my account",
                            onClick = { showDeleteAccountConfirm = true },
                            modifier = Modifier.fillMaxWidth(),
                            accent = Danger,
                            icon = Icons.Filled.Close,
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }

    // Stop sharing confirmation
    if (showStopSharingConfirm) {
        AlertDialog(
            onDismissRequest = { showStopSharingConfirm = false },
            containerColor = Color(0xFF1E1C16),
            titleContentColor = Color.White,
            textContentColor = DarkTextMid,
            title = { Text("Stop sharing your location?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Other hunters will no longer be able to find you nearby. You can turn it back on anytime.",
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showStopSharingConfirm = false
                        repo.setLocationMonitoring(false)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE2574C),
                        contentColor = Color.White,
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) { Text("Stop sharing", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showStopSharingConfirm = false },
                    shape = RoundedCornerShape(12.dp),
                ) { Text("Cancel", color = DarkTextMid) }
            },
        )
    }

    // Account deletion confirmation
    if (showDeleteAccountConfirm) {
        DeleteConfirmDialog(
            title = "Delete your account?",
            message = "This will permanently delete your RockScout account, collection, captures, posts, messages, and all other data stored on this device. This action cannot be undone.",
            onConfirm = {
                showDeleteAccountConfirm = false
                scope.launch {
                    auth.deleteAccount()
                    navController.navigate(Routes.SIGN_IN) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            },
            onDismiss = { showDeleteAccountConfirm = false },
        )
    }
}

/**
 * Card showing current on-device storage used by cached map tiles and
 * user-uploaded rock / specimen / field-capture images. Recomputed every
 * time [usage] changes (on screen entry and after clearing the cache).
 */
@Composable
private fun StorageUsageCard(
    usage: StorageUsageCalculator.Usage?,
    isClearingTiles: Boolean = false,
    showTilesResult: Boolean = false,
    lastTilesFreed: Long = 0L,
    onClearTiles: () -> Unit = {},
) {
    val tilesLabel = usage?.let { StorageUsageCalculator.formatBytes(it.mapTilesBytes) } ?: "…"
    val totalLabel = usage?.let { StorageUsageCalculator.formatBytes(it.totalBytes) } ?: "…"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF1A1812).copy(alpha = 0.7f),
                        Color(0xFF120F08).copy(alpha = 0.5f),
                    )
                )
            )
            .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Citrine.copy(alpha = 0.18f))
                    .glowingBorder(1.dp, Citrine.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Storage,
                    contentDescription = null,
                    tint = Citrine,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Storage in use",
                    style = MaterialTheme.typography.titleMedium.copy(
                        shadow = Shadow(color = Color.Black.copy(alpha = 0.8f), offset = Offset(0f, 1f), blurRadius = 4f),
                    ),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "Total: $totalLabel on this device",
                    style = MaterialTheme.typography.bodySmall.copy(
                        shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), offset = Offset(0f, 1f), blurRadius = 3f),
                    ),
                    color = TextMid,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)
        Spacer(Modifier.height(12.dp))

        StorageUsageRow(
            icon = Icons.Filled.Map,
            label = "Cached map tiles",
            value = tilesLabel,
            accent = Aqua,
            isClearing = isClearingTiles,
            showResult = showTilesResult,
            lastFreed = lastTilesFreed,
            onClear = onClearTiles,
        )
    }
}

@Composable
private fun StorageUsageRow(
    icon: ImageVector,
    label: String,
    value: String,
    accent: Color,
    isClearing: Boolean = false,
    showResult: Boolean = false,
    lastFreed: Long = 0L,
    onClear: () -> Unit = {},
) {
    val clearAccent = Color(0xFFFF6B3D)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
                fontWeight = FontWeight.SemiBold,
            )
            if (isClearing) {
                Text(
                    "Clearing…",
                    style = MaterialTheme.typography.labelSmall,
                    color = clearAccent,
                    fontWeight = FontWeight.SemiBold,
                )
            } else if (showResult && lastFreed > 0) {
                Text(
                    "Freed ${StorageUsageCalculator.formatBytes(lastFreed)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF5CC98C),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Text(
            value,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 10.dp),
        )
        // Individual clear button — small, bordered, tinted with the warning
        // accent. Disabled (and shows a spinner-like label) while a clear is
        // running for this category.
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .glowingBorder(1.dp, clearAccent.copy(alpha = if (isClearing) 0.25f else 0.55f), RoundedCornerShape(10.dp))
                .background(clearAccent.copy(alpha = if (isClearing) 0.08f else 0.16f))
                .clickable(enabled = !isClearing) { onClear() }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.DeleteOutline,
                    contentDescription = "Clear $label",
                    tint = clearAccent,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    if (isClearing) "…" else "Clear",
                    style = MaterialTheme.typography.labelMedium,
                    color = clearAccent,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

/** Formats a byte count as a human-readable string (e.g. "24.3 MB"). */
private fun formatBytes(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    return when {
        gb >= 1.0 -> String.format("%.2f GB", gb)
        mb >= 1.0 -> String.format("%.1f MB", mb)
        kb >= 1.0 -> String.format("%.0f KB", kb)
        else -> "$bytes B"
    }
}

/**
 * Formats the last nightly-sync timestamp as a human-readable relative string
 * for the auto-sync toggle helper text. Returns an empty string when the sync
 * has never run.
 */
private fun formatLastSync(timestampMs: Long): String {
    if (timestampMs <= 0L) return ""
    val elapsedMs = System.currentTimeMillis() - timestampMs
    val minutes = elapsedMs / 60_000L
    return when {
        minutes < 1L -> "Last synced just now."
        minutes < 60L -> "Last synced ${minutes} min ago."
        minutes < 24 * 60L -> {
            val hours = minutes / 60L
            "Last synced $hours hour${if (hours == 1L) "" else "s"} ago."
        }
        else -> {
            val days = minutes / (24 * 60L)
            "Last synced $days day${if (days == 1L) "" else "s"} ago."
        }
    }
}

@Composable
private fun SevereWeatherAlertList() {
    val leftColumn = listOf(
        "Severe Thunderstorm",
        "Tornado",
        "Flash Flood",
        "Hurricane",
        "Tropical Storm",
        "Tsunami",
        "Blizzard",
        "Winter Storm",
    )
    val rightColumn = listOf(
        "Ice Storm",
        "Extreme Heat/Cold",
        "High Wind",
        "Dust Storm",
        "Fire Weather",
        "Red Flag",
        "Smoke & Air Quality",
    )
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                leftColumn.forEach { alert ->
                    Text(
                        text = "\u2022 $alert",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFF5F0E6),
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                rightColumn.forEach { alert ->
                    Text(
                        text = "\u2022 $alert",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFF5F0E6),
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Monitors your location independently.",
            style = MaterialTheme.typography.labelSmall,
            color = TextLow,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.titleMedium,
        color = Citrine,
        fontWeight = FontWeight.ExtraBold,
    )
}

@Composable
private fun ToggleDivider() {
    HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)
}

@Composable
private fun ToggleRow(
    icon: ImageVector,
    title: String,
    helper: String,
    accent: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    locked: Boolean = false,
    helperContent: @Composable (() -> Unit)? = null,
) {
    val textShadow = Shadow(
        color = Color.Black.copy(alpha = 0.8f),
        offset = Offset(0f, 1f),
        blurRadius = 4f,
    )
    val helperShadow = Shadow(
        color = Color.Black.copy(alpha = 0.7f),
        offset = Offset(0f, 1f),
        blurRadius = 3f,
    )
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
                .glowingBorder(2.dp, accent.copy(alpha = 0.60f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = accent,
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
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(shadow = textShadow),
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            if (helperContent != null) {
                helperContent()
            } else {
                Text(
                    text = helper,
                    style = MaterialTheme.typography.bodyMedium.copy(shadow = helperShadow),
                    color = Color(0xFFF5F0E6),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        if (locked) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Citrine.copy(alpha = 0.18f))
                    .glowingBorder(2.dp, Citrine.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                    .clickable { onCheckedChange(true) },
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
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}

package com.rork.rockscout.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.HunterStatus
import com.rork.rockscout.data.FriendRepository
import com.rork.rockscout.data.ReportRepository
import com.rork.rockscout.data.ReportScreenshotHelper
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.HunterStatusIcon
import com.rork.rockscout.ui.components.profileBorderColor
import com.rork.rockscout.ui.components.ReportSubmittedDialog
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.data.SessionStatus
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.glowingBorder

/**
 * RockScout Scan screen — discovery list of nearby hunters who are on the
 * hunt. Cards match the trip planner tile style, vertically stacked, showing
 * user name and distance/direction text. A sort pill toggles between Closest,
 * Furthest, and Most Recent. Clicking a card opens the user's profile.
 *
 * Users are automatically included in each other's search radius when their
 * location monitoring and searchable toggles are ON.
 */
enum class ScanSortMode { CLOSEST, FURTHEST, MOST_RECENT }

@Composable
fun ScanScreen(navController: NavController) {
    val repo = AppRepository.instance
    val profile by repo.profile.collectAsStateWithLifecycle()
    val current by repo.currentLocation.collectAsStateWithLifecycle()
    val auth = AuthRepository.instance
    val social = SocialRepository.instance
    val sessionStatus by auth.sessionStatus.collectAsStateWithLifecycle()
    val isSignedIn = sessionStatus is SessionStatus.Authenticated

    // Phase 8 soft-lockout: RockScout Friends (the social aspect,
    // toggleable in social settings) requires an active trial, donated
    // unlock, or subscription. Free users whose trial has expired see a
    // subscribe/donate prompt instead of the scan list.
    val accessManager = com.rork.rockscout.data.IdentifyAccessManager.instance
    val purchaseManager = com.rork.rockscout.data.PurchaseManager.instance
    val isPremium by purchaseManager.isPremium.collectAsStateWithLifecycle()
    val trialExpired by accessManager.trialExpired.collectAsStateWithLifecycle()
    val hasLocationUnlock by accessManager.hasLocationUnlock.collectAsStateWithLifecycle()
    val clubLocked = remember(isPremium, trialExpired, hasLocationUnlock) {
        accessManager.isSocialLocked(isPremium)
    }

    val scanResults by social.scanResults.collectAsStateWithLifecycle()
    val isScanning by social.isScanning.collectAsStateWithLifecycle()
    val scanError by social.scanError.collectAsStateWithLifecycle()
    val connections by social.connections.collectAsStateWithLifecycle()

    // Sort mode for the scan results list.
    var sortMode by remember { mutableStateOf(ScanSortMode.CLOSEST) }

    // Re-sort scan results when sort mode or results change.
    val sortedResults = remember(scanResults, sortMode) {
        when (sortMode) {
            ScanSortMode.CLOSEST -> scanResults.sortedBy { it.distanceMiles }
            ScanSortMode.FURTHEST -> scanResults.sortedByDescending { it.distanceMiles }
            ScanSortMode.MOST_RECENT -> scanResults.sortedByDescending { it.hunter.last_location_update }
        }
    }

    // Determinate staged progress for the scan operation — mirrors the cloud
    // backup bar: an invisible progress track that fills up with contextual
    // stage text, then populates the results list on completion.
    var scanProgress by remember { mutableStateOf(0f) }
    var scanStage by remember { mutableStateOf("") }
    var foundCount by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val rootView = androidx.compose.ui.platform.LocalView.current
    val myProfile by repo.profile.collectAsStateWithLifecycle()
    var requestTarget by remember { mutableStateOf<SocialRepository.HunterProfile?>(null) }
    var requestBody by remember { mutableStateOf("") }
    var requestSent by remember { mutableStateOf<String?>(null) }
    var reportTarget by remember { mutableStateOf<SocialRepository.HunterProfile?>(null) }
    var showReportConfirm by remember { mutableStateOf(false) }
    var reportSubmitted by remember { mutableStateOf(false) }
    var reportError by remember { mutableStateOf<String?>(null) }

    val radiusOptions = remember { listOf(5, 25, 50, 100, 250) }

    val permsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { _ -> /* handled implicitly */ }

    ScreenScaffold(title = "RockScout Scan", onBack = { navController.popBackStack() }) {
        if (!isSignedIn) {
            ScanLockedState(
                emoji = "\uD83D\uDD11",
                title = "Sign in to scan",
                message = "You need a RockScout account so other hunters know who's reaching out.",
                buttonLabel = "Sign in",
                onButton = { navController.navigate(Routes.SIGN_IN) },
            )
            return@ScreenScaffold
        }
        if (clubLocked) {
            ScanLockedState(
                emoji = "\uD83D\uDD12",
                title = "Unlock RockScout Friends",
                message = "Your 1-week free trial has ended. Subscribe or donate to keep scanning for nearby hunters, messaging, and pinging.",
                buttonLabel = "Subscribe or donate",
                onButton = { navController.navigate(Routes.PAYWALL) },
            )
            return@ScreenScaffold
        }
        if (!profile.clubEnabled) {
            ScanLockedState(
                emoji = "\uD83E\uDD1D",
                title = "Turn on RockScout Friends",
                message = "Enable the RockScout Friends toggle in your Profile to appear in scans and reach out to nearby hunters.",
                buttonLabel = "Open Profile",
                onButton = { navController.navigate(Routes.PROFILE) },
            )
            return@ScreenScaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Scan radius stepper
            item {
                DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
                    Text(
                        "Scan radius",
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkTextMid,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        radiusOptions.forEach { miles ->
                            val selected = profile.scanRadiusMiles == miles
                            val premiumLocked = miles == 250 && !isPremium
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) Citrine.copy(alpha = 0.30f) else Color(0xFF3A3830))
                                    .glowingBorder(2.dp, if (selected) Citrine else Color.Transparent, RoundedCornerShape(10.dp))
                                    .clickable {
                                        if (premiumLocked) {
                                            navController.navigate(Routes.PAYWALL)
                                        } else {
                                            repo.setScanRadiusMiles(miles)
                                        }
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = if (premiumLocked) "$miles\nPREMIUM" else "$miles mi",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selected) Ink else TextMid,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }

            // Scan button
            item {
                ScanButton(
                    isScanning = isScanning,
                    radius = profile.scanRadiusMiles,
                    scanProgress = scanProgress,
                    scanStage = scanStage,
                    foundCount = foundCount,
                    onScan = {
                        if (profile.locationMonitoring) {
                            val perms = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                perms.add(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            permsLauncher.launch(perms.toTypedArray())
                        }
                        scanProgress = 0f
                        scanStage = "Locating nearby hunters…"
                        foundCount = 0
                        scope.launch {
                            // Stage 1: locate
                            scanStage = "Locating nearby hunters…"
                            kotlinx.coroutines.delay(200)
                            scanProgress = 0.25f
                            // Stage 2: query (actual scan runs here)
                            scanStage = "Searching the field…"
                            val result = social.scan(
                                myLat = current.first,
                                myLng = current.second,
                                radiusMiles = profile.scanRadiusMiles,
                            )
                            scanProgress = 0.75f
                            // Stage 3: tally results
                            val count = result.getOrNull()?.size ?: 0
                            foundCount = count
                            scanStage = if (count > 0) "Found $count ${if (count == 1) "RockScout" else "RockScouts"} nearby!" else "No RockScouts found in range."
                            kotlinx.coroutines.delay(200)
                            scanProgress = 1f
                            scanStage = ""
                        }
                    },
                )
            }

            scanError?.let { err ->
                item {
                    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFFE2574C)) {
                        Text(
                            "Scan couldn't complete: $err",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextHigh,
                        )
                    }
                }
            }

            if (scanResults.isEmpty() && !isScanning && scanError == null) {
                item {
                    EmptyScanState(radius = profile.scanRadiusMiles)
                }
            }

            // Sorting pill — Closest / Furthest / Most Recent
            if (scanResults.isNotEmpty()) {
                item {
                    SortPillRow(
                        sortMode = sortMode,
                        onSortMode = { sortMode = it },
                    )
                }
            }

            items(sortedResults, key = { it.hunter.id }) { result ->
                HunterCard(
                    result = result,
                    myLat = current.first,
                    myLng = current.second,
                    isConnected = connections.contains(result.hunter.id),
                    onRequest = { requestTarget = result.hunter },
                    onOpenThread = {
                        navController.navigate(Routes.messengerThread(result.hunter.id))
                    },
                    onFriendRequest = {
                        scope.launch {
                            FriendRepository.instance.sendFriendRequest(result.hunter.id)
                        }
                    },
                    onReport = { showReportConfirm = true; reportTarget = result.hunter },
                    onViewProfile = { navController.navigate(Routes.userProfile(result.hunter.id)) },
                    onViewCollection = { navController.navigate(Routes.userCollection(result.hunter.id)) },
                    onViewWishlist = { navController.navigate(Routes.userWishlist(result.hunter.id)) },
                )
            }
        }

        // Report-confirmation dialog
        if (reportSubmitted) {
            ReportSubmittedDialog(onDismiss = {
                reportSubmitted = false
                reportTarget = null
            })
        }
        reportError?.let { err ->
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { reportError = null },
                title = { Text("Report failed", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
                text = { Text(err, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid) },
                confirmButton = {
                    SculptedTextButton(text = "OK", onClick = { reportError = null }, accent = Citrine, textColor = Citrine)
                },
                containerColor = Color(0xFF1E1C16),
                titleContentColor = DarkTextHigh,
                textContentColor = DarkTextMid,
            )
        }
        // Report confirmation popup — prevents accidental taps from triggering
        // screenshot capture + email composer.
        if (showReportConfirm && reportTarget != null) {
            val target = reportTarget
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showReportConfirm = false; reportTarget = null },
                title = { Text("Report this user?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "A screenshot will be captured and you'll be asked to send a report email. Only continue if you believe this user violated our community guidelines.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                    )
                },
                confirmButton = {
                    SculptedButton(
                        text = "Continue",
                        onClick = { showReportConfirm = false },
                        accent = Danger,
                        containerColor = Danger,
                        textColor = Color.White,
                    )
                },
                dismissButton = {
                    SculptedTextButton(text = "Cancel", onClick = { showReportConfirm = false; reportTarget = null }, accent = DarkTextMid, textColor = DarkTextMid)
                },
                containerColor = Color(0xFF1E1C16),
                titleContentColor = DarkTextHigh,
                textContentColor = DarkTextMid,
            )
        }
        reportTarget?.let { target ->
            if (!showReportConfirm) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { reportTarget = null },
                title = { Text("Report ${target.display_name}?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Help keep RockScout safe and family-friendly. Report this user for inappropriate behavior, profanity, or content that violates our community guidelines. Our moderation team will review the report and issue an escalating warning if warranted.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                    )
                },
                confirmButton = {
                    SculptedButton(
                        text = "Report",
                        onClick = {
                            scope.launch {
                                val reportId = "report-${System.currentTimeMillis()}"
                                val screenshotPath = ReportScreenshotHelper.captureScreenshot(
                                    context, rootView, reportId,
                                )
                                ReportRepository.instance.reportUser(
                                    reportedUserId = target.id,
                                    reason = "Inappropriate behavior",
                                    screenshotPath = screenshotPath,
                                    reporterName = myProfile.name,
                                    reportedName = target.display_name,
                                    reportedAvatar = target.avatar_emoji,
                                ).onSuccess {
                                    reportSubmitted = true
                                    // Launch email composer to RockScoutApp2026@yahoo.com
                                    val intent = ReportScreenshotHelper.buildEmailIntent(
                                        context = context,
                                        reportedUserName = target.display_name,
                                        reporterUserName = myProfile.name,
                                        reason = "Inappropriate behavior",
                                        timestamp = System.currentTimeMillis(),
                                        screenshotPath = screenshotPath,
                                    )
                                    ReportScreenshotHelper.launchEmailComposer(context, intent)
                                }.onFailure { reportError = it.message ?: "Could not submit report. Try again later." }
                            }
                        },
                        accent = Danger,
                        containerColor = Danger,
                        textColor = Color.White,
                    )
                },
                dismissButton = {
                    SculptedTextButton(text = "Cancel", onClick = { reportTarget = null }, accent = DarkTextMid, textColor = DarkTextMid)
                },
                containerColor = Color(0xFF1E1C16),
                titleContentColor = DarkTextHigh,
                textContentColor = DarkTextMid,
            )
            }
        }
        // Message-request dialog
        requestTarget?.let { target ->
            MessageRequestDialog(
                target = target,
                body = requestBody,
                onBodyChange = { requestBody = it },
                sentFlag = requestSent,
                onSend = {
                    scope.launch {
                        social.sendRequest(target.id, requestBody.ifBlank { "Hi! Fellow RockScout here — want to connect?" })
                            .onSuccess {
                                requestSent = target.id
                            }
                    }
                },
                onDismiss = {
                    requestTarget = null
                    requestBody = ""
                    requestSent = null
                },
            )
        }
    }
}

@Composable
private fun ScanButton(
    isScanning: Boolean,
    radius: Int,
    scanProgress: Float,
    scanStage: String,
    foundCount: Int,
    onScan: () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "scanGlow")
    val glow by transition.animateFloat(
        initialValue = 0.30f,
        targetValue = 0.70f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "scanGlowAlpha",
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .sculpted(shape = RoundedCornerShape(22.dp), accent = Citrine, shadowElevation = 7.dp, enabled = !isScanning, onClick = onScan)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .glowingBorder(3.dp, Citrine.copy(alpha = 0.55f), RoundedCornerShape(22.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(80.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Citrine.copy(alpha = 0.20f + glow), Color.Transparent)
                    )
                ),
        )
        if (isScanning) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {
                Text("Scanning the area", style = MaterialTheme.typography.titleLarge, color = DarkTextHigh, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                // Identification-scan-style circular scanning indicator around the target icon.
                val scanRing = rememberInfiniteTransition(label = "scanRing")
                val pulse by scanRing.animateFloat(
                    initialValue = 0.6f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
                    label = "scanRingPulse",
                )
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(56.dp),
                        color = Citrine,
                        strokeWidth = 4.dp,
                        trackColor = Color(0x33FFFFFF),
                    )
                    // Pulsing glow behind the icon.
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Citrine.copy(alpha = 0.35f * pulse), Citrine.copy(alpha = 0.10f * pulse), Color.Transparent)
                                )
                            ),
                    )
                    // Center target icon.
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2A2820))
                            .glowingBorder(2.dp, Citrine.copy(alpha = 0.85f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = Citrine,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                // Determinate progress bar — fills up with the scan stages.
                LinearProgressIndicator(
                    progress = { scanProgress },
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Citrine,
                    trackColor = Color(0x33FFFFFF),
                )
                Spacer(Modifier.height(8.dp))
                // Contextual stage text — shows what's happening or the found count.
                Text(
                    text = scanStage.ifBlank { "Searching for nearby RockScouts…" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (foundCount > 0 && scanProgress >= 1f) Success else DarkTextMid,
                    fontWeight = if (foundCount > 0 && scanProgress >= 1f) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Citrine.copy(alpha = 0.40f), Citrine.copy(alpha = 0.10f))))
                        .glowingBorder(2.dp, Citrine.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Citrine, modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "Scan for RockScouts",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "Find hunters on the hunt within $radius mi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            }
        }
    }
}

@Composable
private fun SortPillRow(
    sortMode: ScanSortMode,
    onSortMode: (ScanSortMode) -> Unit,
) {
    val pillShape = RoundedCornerShape(50.dp)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ScanSortMode.entries.forEach { mode ->
            val label = when (mode) {
                ScanSortMode.CLOSEST -> "Closest"
                ScanSortMode.FURTHEST -> "Furthest"
                ScanSortMode.MOST_RECENT -> "Most Recent"
            }
            val selected = sortMode == mode
            val accent = when (mode) {
                ScanSortMode.CLOSEST -> Citrine
                ScanSortMode.FURTHEST -> Aqua
                ScanSortMode.MOST_RECENT -> Color(0xFFB08BFF)
            }
            Box(
                modifier = Modifier
                    .sculpted(
                        shape = pillShape,
                        accent = accent,
                        shadowElevation = if (selected) 6.dp else 3.dp,
                        onClick = { onSortMode(mode) },
                    )
                    .clip(pillShape)
                    .background(if (selected) accent.copy(alpha = 0.25f) else Slate800)
                    .glowingBorder(
                        2.dp,
                        if (selected) accent else accent.copy(alpha = 0.3f),
                        pillShape,
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (selected) accent else DarkTextMid,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun HunterCard(
    result: SocialRepository.ScanResult,
    myLat: Double,
    myLng: Double,
    isConnected: Boolean,
    onRequest: () -> Unit,
    onOpenThread: () -> Unit,
    onFriendRequest: () -> Unit,
    onReport: () -> Unit,
    onViewProfile: () -> Unit,
    onViewCollection: () -> Unit,
    onViewWishlist: () -> Unit,
) {
    val h = result.hunter
    val statusAccent = when (h.status) {
        "on-the-hunt" -> Success
        "wishing" -> Citrine
        "looking-for-trades" -> Aqua
        else -> TextMid
    }
    val status = when (h.status) {
        "on-the-hunt" -> HunterStatus.ON_THE_HUNT
        "wishing" -> HunterStatus.WISHING
        "looking-for-trades" -> HunterStatus.LOOKING_FOR_TRADES
        else -> HunterStatus.OFF_GRID
    }
    val statusLabel = when (h.status) {
        "on-the-hunt" -> "On the hunt!"
        "wishing" -> "Thinkin' Bout Rocks"
        "looking-for-trades" -> "Looking for trades"
        else -> "Off the grid"
    }

    // Compute the distance/direction text.
    val distDirText = remember(result, myLat, myLng) {
        val theirLat = h.coarse_lat
        val theirLng = h.coarse_lng
        if (theirLat != null && theirLng != null) {
            SocialRepository.instance.distanceDirectionText(
                result.distanceMiles, myLat, myLng, theirLat, theirLng,
            )
        } else {
            "about ${result.distanceMiles.roundToInt()} miles from you"
        }
    }

    // Match the trip planner tile style: full-width, rounded card with gradient
    // background, accent glow at top, and dark overlay.
    val cardShape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = cardShape, accent = statusAccent, shadowElevation = 6.dp, onClick = onViewProfile)
            .clip(cardShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.62f),
                        Color.Black.copy(alpha = 0.72f),
                        Color.Black.copy(alpha = 0.82f),
                    )
                )
            )
            .glowingBorder(3.dp, statusAccent.copy(alpha = 0.50f), cardShape),
    ) {
        // Accent glow overlay at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(80.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(statusAccent.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Identity row — avatar + name/distance on the left, report on the right
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(statusAccent.copy(alpha = 0.45f), Aqua.copy(alpha = 0.25f))))
                        .glowingBorder(2.dp, profileBorderColor(status), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(h.avatar_emoji, style = MaterialTheme.typography.headlineSmall)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            h.display_name,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (h.premium_badge) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Citrine.copy(alpha = 0.30f))
                                    .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Text("PREMIUM", style = MaterialTheme.typography.labelSmall, color = Citrine, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        HunterStatusIcon(status = status, size = 18.dp)
                        Text(
                            statusLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Aqua,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    // Distance/direction text — the key info line
                    Text(
                        "$distDirText",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        "Lvl ${h.level}",
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkTextMid,
                        maxLines = 1,
                    )
                    // Last-active indicator
                    if (h.last_location_update > 0L) {
                        val activeText = remember(h.last_location_update) {
                            val elapsedMs = System.currentTimeMillis() - h.last_location_update
                            val mins = elapsedMs / 60_000L
                            when {
                                mins < 1L -> "Active just now"
                                mins < 60L -> "Active ${mins}m ago"
                                mins < 24 * 60L -> "Active ${mins / 60L}h ago"
                                else -> "Active ${mins / (24 * 60L)}d ago"
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (h.last_location_update > System.currentTimeMillis() - 300_000L) Success
                                        else if (h.last_location_update > System.currentTimeMillis() - 3_600_000L) Citrine
                                        else TextLow
                                    ),
                            )
                            Text(
                                activeText,
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextMid,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                            )
                        }
                    }
                }
                SculptedIconButton(
                    icon = Icons.Filled.Flag,
                    contentDescription = "Report",
                    onClick = onReport,
                    accent = Danger,
                    iconTint = TextLow,
                    size = 40.dp,
                    shadowElevation = 3.dp,
                )
            }

            Spacer(Modifier.height(12.dp))

            // Action buttons — full row so they never squeeze the text above
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isConnected) {
                    SculptedButton(
                        text = "Message",
                        onClick = onOpenThread,
                        accent = Aqua,
                        containerColor = Aqua,
                        textColor = Ink,
                        icon = Icons.AutoMirrored.Filled.Send,
                        modifier = Modifier.height(40.dp),
                    )
                } else {
                    SculptedButton(
                        text = "Message",
                        onClick = onRequest,
                        accent = Aqua,
                        containerColor = Aqua,
                        textColor = Ink,
                        icon = Icons.AutoMirrored.Filled.Send,
                        modifier = Modifier.height(40.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    SculptedOutlinedButton(
                        text = "RockScout",
                        onClick = onFriendRequest,
                        accent = Success,
                        textColor = Success,
                        modifier = Modifier.height(40.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyScanState(radius: Int) {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = TextMid) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("\uD83D\uDD0D", style = MaterialTheme.typography.displayMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                "No RockScouts on the hunt near you right now.",
                style = MaterialTheme.typography.titleMedium,
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Try widening your scan radius or check back later — the field is always changing.",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ScanLockedState(
    emoji: String,
    title: String,
    message: String,
    buttonLabel: String,
    onButton: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(emoji, style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium, color = TextMid, textAlign = TextAlign.Center)
            Spacer(Modifier.height(20.dp))
            SculptedButton(
                text = buttonLabel,
                onClick = onButton,
                accent = Citrine,
                containerColor = Citrine,
                textColor = Ink,
            )
        }
    }
}

@Composable
private fun MessageRequestDialog(
    target: SocialRepository.HunterProfile,
    body: String,
    onBodyChange: (String) -> Unit,
    sentFlag: String?,
    onSend: () -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        DarkCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clickable(enabled = false) {},
            accent = Citrine,
        ) {
            Column(modifier = Modifier.fillMaxWidth().imePadding()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(target.avatar_emoji, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Message ${target.display_name}", style = MaterialTheme.typography.titleMedium, color = DarkTextHigh, fontWeight = FontWeight.Bold)
                        Text("They'll get a request to connect. If they accept, you two can chat and ping each other.", style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .sculpted(
                                shape = CircleShape,
                                accent = Citrine,
                                shadowElevation = 3.dp,
                                circular = true,
                                onClick = onDismiss,
                            )
                            .clip(CircleShape)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.height(14.dp))
                if (sentFlag == target.id) {
                    Text(
                        "Request sent! You'll see them in your RockScout Friends list if they accept.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Success,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(12.dp))
                    SculptedButton(
                        text = "Done",
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        accent = Citrine,
                        containerColor = Citrine,
                        textColor = Ink,
                    )
                } else {
                    OutlinedTextField(
                        value = body,
                        onValueChange = onBodyChange,
                        modifier = Modifier.fillMaxWidth().noAutoFocus(),
                        placeholder = { Text("Hi! Fellow RockScout here — want to connect?") },
                        minLines = 3,
                        maxLines = 5,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF3A3830),
                            unfocusedContainerColor = Color(0xFF3A3830),
                            focusedTextColor = DarkTextHigh,
                            unfocusedTextColor = DarkTextHigh,
                            focusedIndicatorColor = Citrine,
                            unfocusedIndicatorColor = Color(0xFF5A554A),
                            cursorColor = Citrine,
                        ),
                        shape = RoundedCornerShape(12.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    SculptedButton(
                        text = "Send message request",
                        onClick = onSend,
                        modifier = Modifier.fillMaxWidth(),
                        accent = Aqua,
                        containerColor = Aqua,
                        textColor = Ink,
                        icon = Icons.AutoMirrored.Filled.Send,
                    )
                }
            }
        }
    }
}

package com.rork.rockscout.ui.screens

import androidx.activity.compose.BackHandler
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close

import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.PersistenceManager
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.ui.components.PingConversationPicker
import com.rork.rockscout.ui.components.PingToShare
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.Warning
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Directions
import com.rork.rockscout.data.SessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.rork.rockscout.ui.components.MapDownloadSheet
import com.rork.rockscout.ui.components.applyHybridTiles
import com.rork.rockscout.ui.components.MapOfflineNotice
import com.rork.rockscout.ui.components.MapTileCacheManager
import com.rork.rockscout.ui.components.MapViewLifecycleEffect
import com.rork.rockscout.ui.components.MapZoomControls
import com.rork.rockscout.ui.components.createRockScoutMapView
import com.rork.rockscout.ui.components.toggleSatelliteView
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.glowingBorder
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import com.rork.rockscout.data.LocationFetcher

/**
 * RockScouts Map — shows YOUR ping only. Pings are private — nobody else's
 * ping appears here. Use the Share button to send your ping location to
 * someone via Messenger, SMS, or any app.
 *
 * Scan for nearby hunters (the Scan screen) only populates a card list of
 * connected RockScout Friends within your search radius — it does NOT show
 * anyone on this map.
 *
 * Requires: RockScout Friends ON (auth is mandatory for all users).
 * Auto-switches to satellite-style tiles at close zoom (street level) so
 * users can see streets, parking lots, and trailheads clearly.
 */
@Composable
fun RockScoutsMapScreen(navController: NavController) {
    val repo = AppRepository.instance
    val profile by repo.profile.collectAsStateWithLifecycle()
    val current by repo.currentLocation.collectAsStateWithLifecycle()
    val auth = AuthRepository.instance
    val social = SocialRepository.instance
    val sessionStatus by auth.sessionStatus.collectAsStateWithLifecycle()
    val isSignedIn = sessionStatus is SessionStatus.Authenticated

    // Phase 8 soft-lockout: RockScout Friends requires an active trial,
    // donated unlock, or subscription.
    val accessManager = com.rork.rockscout.data.IdentifyAccessManager.instance
    val purchaseManager = com.rork.rockscout.data.PurchaseManager.instance
    val isPremium by purchaseManager.effectiveIsPremium.collectAsStateWithLifecycle()
    val clubLocked = remember(isPremium) {
        accessManager.isSocialLocked(isPremium)
    }

    val pings by social.pings.collectAsStateWithLifecycle()
    val sharedPings by social.sharedPings.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var myPingId by remember { mutableStateOf<String?>(null) }
    var showSafetyNote by remember { mutableStateOf(true) }
    var showPingConfirmDialog by remember { mutableStateOf(false) }
    var showDownloadSheet by remember { mutableStateOf(false) }
    var showConversationPicker by remember { mutableStateOf(false) }
    var selectedSharedPing by remember { mutableStateOf<SocialRepository.SharedPingRow?>(null) }
    var showRemovePingConfirm by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var mapCenterLat by remember { mutableStateOf(current.first) }
    var mapCenterLng by remember { mutableStateOf(current.second) }

    // Safety banner persistence: load dismissed state on entry, clear on dispose
    // so the banner reappears next time the user opens the screen.
    DisposableEffect(Unit) {
        showSafetyNote = !com.rork.rockscout.data.LocalDataStore.getBoolean("safety_banner_dismissed")
        onDispose {
            com.rork.rockscout.data.LocalDataStore.setBoolean("safety_banner_dismissed", false)
        }
    }

    // Ping marker color — stored locally, used for the user's ping marker.
    var pingColorArgb by remember {
        mutableStateOf(
            com.rork.rockscout.data.LocalDataStore.getString("ping_marker_color")
                ?.toLongOrNull()?.let { Color(it.toULong()) }
                ?: Citrine
        )
    }

    // Live map-center coordinate tracking — polls the map view's center
    // every 300ms so the coordinate chip updates as the user drags the map.
    LaunchedEffect(mapView) {
        while (true) {
            val mv = mapView
            if (mv != null) {
                val c = mv.mapCenter
                mapCenterLat = c.latitude
                mapCenterLng = c.longitude
            }
            kotlinx.coroutines.delay(300)
        }
    }

    BackHandler(enabled = showSafetyNote) { showSafetyNote = false }

    // Load my pings and shared pings on open. Pings are private — only mine show here.
    // Shared pings are ones others sent me via Messenger deep links.
    LaunchedEffect(isSignedIn, profile.clubEnabled) {
        if (isSignedIn && profile.clubEnabled) {
            social.loadVisiblePings()
            social.loadSharedPings()
        }
    }

    // Refresh pings periodically (~30s) while the screen is open.
    LaunchedEffect(profile.clubEnabled) {
        while (profile.clubEnabled) {
            kotlinx.coroutines.delay(30_000)
            social.loadVisiblePings()
            social.loadSharedPings()
        }
    }


    // Track which ping is mine.
    LaunchedEffect(pings, auth.currentUserId) {
        myPingId = pings.firstOrNull { it.user_id == auth.currentUserId }?.id
    }

    // Render shared pings only on the map. The user's own ping is NOT shown
    // as a marker — instead the map centers on the user's GPS location (or
    // their existing ping location) so they can position a new ping at the
    // map center using the crosshair.
    LaunchedEffect(sharedPings, mapView) {
        val mv = mapView ?: return@LaunchedEffect
        try {
            withContext(Dispatchers.IO) {
                if (!isActive) return@withContext
                mv.overlays.removeAll { it is Marker && it.id?.startsWith("ping_") == true }
                // Shared pings — Aqua color, tappable to show directions
                sharedPings.forEach { sp ->
                    if (!isActive) return@withContext
                    val marker = PingMarker(
                        mv,
                        sp.lat,
                        sp.lng,
                        sp.label,
                        isMine = false,
                        displayName = sp.senderName,
                        avatarEmoji = "\uD83D\uDC65",
                        onOpenThread = {
                            selectedSharedPing = sp
                        },
                    )
                    marker.id = "ping_shared_${sp.id}"
                    mv.overlays.add(marker)
                }
                if (!isActive) return@withContext
                withContext(Dispatchers.Main) {
                    runCatching { mv.invalidate() }
                }
            }
        } catch (_: kotlinx.coroutines.CancellationException) {
            // Expected when navigating away.
        } catch (_: Throwable) {
            // MapView may have been detached concurrently.
        }
    }

    // Resolve the user's own ping from the pings list.
    val myPing = pings.firstOrNull { it.user_id == auth.currentUserId }

    // Center the map on the user's existing ping location, or their GPS
    // location if no ping is set yet. This runs once after the map is ready.
    LaunchedEffect(mapView, myPing) {
        val mv = mapView ?: return@LaunchedEffect
        val targetPoint = if (myPing != null) {
            GeoPoint(myPing.lat, myPing.lng)
        } else {
            // Fetch GPS location and center there
            val loc = withContext(Dispatchers.IO) {
                runCatching { LocationFetcher.fetch(context) }.getOrNull()
            }
            if (loc != null) GeoPoint(loc.latitude, loc.longitude)
            else GeoPoint(current.first, current.second)
        }
        withContext(Dispatchers.Main) {
            runCatching {
                mv.controller.animateTo(targetPoint)
                mv.controller.setZoom(10.0)
            }
        }
    }

    // Locked states.
    if (!isSignedIn) {
        MapLockedState(
            emoji = "\uD83D\uDD11",
            title = "Sign in to use the map",
            message = "You need a RockScout account so you can ping and be pinged by your connected hunters.",
            buttonLabel = "Sign in",
            onButton = { navController.navigate(Routes.SIGN_IN) },
        )
        return
    }
    if (clubLocked) {
        MapLockedState(
            emoji = "\uD83D\uDD12",
            title = "Unlock RockScout Friends",
            message = "Your 1-week free trial has ended. Subscribe or donate to keep pinging your connected hunters on the map.",
            buttonLabel = "Subscribe or donate",
            onButton = { navController.navigate(Routes.PAYWALL) },
        )
        return
    }
    if (!profile.clubEnabled) {
        MapLockedState(
            emoji = "\uD83E\uDD1D",
            title = "Turn on RockScout Friends",
            message = "Enable the RockScout Friends toggle in your Profile to set a ping and share your location.",
            buttonLabel = "Open Profile",
            onButton = { navController.navigate(Routes.PROFILE) },
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Map
        Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp))) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    createRockScoutMapView(ctx).apply {
                        controller.setZoom(10.0)
                        controller.setCenter(GeoPoint(current.first, current.second))
                        overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
                        overlays.add(CompassOverlay(ctx, this).apply { enableCompass() })

                        mapView = this
                    }
                },
                update = { /* No-op — tile switching handled by overlay listener in factory. */ },
            )

            MapOfflineNotice(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 76.dp),
            )


        }

        // Centered crosshair — stays fixed at screen center while the map pans
        // underneath. Marks the exact geo-point at the map center where the ping
        // will be set.
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            // Glow circle at the exact target point
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Success.copy(alpha = 0.25f))
                    .glowingBorder(2.dp, Success, CircleShape),
            )
            // Crosshair ring
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .glowingBorder(1.5.dp, Success.copy(alpha = 0.6f), CircleShape),
            )
            // Live coordinate chip — floats below the crosshair, updates as map pans
            Box(
                modifier = Modifier
                    .offset(y = 44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xDD1C1A14))
                    .glowingBorder(1.dp, Success.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    "%.5f, %.5f".format(mapCenterLat, mapCenterLng),
                    style = MaterialTheme.typography.labelSmall,
                    color = Success,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1C1A14).copy(alpha = 0.92f))
                .padding(start = 8.dp, end = 16.dp, top = 52.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DarkTextHigh,
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                "RockScouts Map",
                style = MaterialTheme.typography.headlineMedium,
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            // Private badge — pings are private
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Warning.copy(alpha = 0.20f))
                    .glowingBorder(2.dp, Warning.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    "Private",
                    style = MaterialTheme.typography.labelMedium,
                    color = Warning,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(8.dp))
            // Ping color picker button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(pingColorArgb.copy(alpha = 0.25f))
                    .glowingBorder(2.dp, pingColorArgb, CircleShape)
                    .clickable { showColorPicker = true },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(pingColorArgb),
                )
            }
        }

        // Zoom controls — compact shared component, raised well above the bottom
        // action bar so it never overlaps the compact button rows.
        MapZoomControls(
            onZoomIn = { mapView?.let { it.controller.zoomIn() } },
            onZoomOut = { mapView?.let { it.controller.zoomOut() } },
            onRecenter = {},
            showUser = true,
            onSatellite = { toggleSatelliteView(mapView) },
            compact = true,
            mapView = mapView,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 156.dp)
                .navigationBarsPadding(),
        )

        // Safety / privacy note (dismissible)
        AnimatedVisibility(
            visible = showSafetyNote,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 110.dp, start = 16.dp, end = 16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2A1410).copy(alpha = 0.96f))
                    .glowingBorder(2.dp, Danger.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("\u26A0\uFE0F", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Always meet in a public area. Keep your own safety in mind when sharing your location with anyone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Danger,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .sculpted(
                                shape = CircleShape,
                                accent = Citrine,
                                shadowElevation = 3.dp,
                                circular = true,
                                onClick = {
                                    showSafetyNote = false
                                    com.rork.rockscout.data.LocalDataStore.setBoolean("safety_banner_dismissed", true)
                                },
                            )
                            .clip(CircleShape)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Dismiss",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Your ping is private — only you see it. Use Share to send it to someone via Messenger. Move it to a safe meet-up spot before sharing.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                )
            }
        }

        // Bottom action bar — compact, two-column rows so all buttons fit above
        // the system navigation bar. Half-width buttons keep text visible while
        // preserving tappable size.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xFF1C1A14).copy(alpha = 0.95f))
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .navigationBarsPadding(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (myPing != null) {
                    // Compact live ping info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Success.copy(alpha = 0.30f))
                                .glowingBorder(1.dp, Success.copy(alpha = 0.35f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Success, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Your ping is live",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextHigh,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                "Private — expires in ${if (isPremium) "24h" else "12h"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextMid,
                            )
                        }
                    }
                    // Share + Remove row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SculptedButton(
                            text = "Share ping",
                            onClick = { showConversationPicker = true },
                            modifier = Modifier.weight(1f),
                            accent = Citrine,
                            containerColor = Citrine,
                            textColor = Ink,
                            icon = Icons.Filled.Share,
                            shape = RoundedCornerShape(18.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                            textStyle = MaterialTheme.typography.labelMedium,
                        )
                        SculptedButton(
                            text = "Remove ...",
                            onClick = { showRemovePingConfirm = true },
                            modifier = Modifier.weight(1f),
                            accent = Aqua,
                            containerColor = Color(0xFF3A3830),
                            textColor = DarkTextHigh,
                            shape = RoundedCornerShape(18.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                            textStyle = MaterialTheme.typography.labelMedium,
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "Drag the map to position your pin, then tap Set ping.",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextHigh,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Lat: %.5f  \u00B7  Lng: %.5f".format(mapCenterLat, mapCenterLng),
                            style = MaterialTheme.typography.labelSmall,
                            color = Aqua,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                // Download maps + Set/Drop ping row — always visible
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SculptedOutlinedButton(
                        text = "Download offline maps",
                        onClick = { showDownloadSheet = true },
                        accent = Aqua,
                        textColor = Aqua,
                        icon = Icons.Filled.Download,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                        textStyle = MaterialTheme.typography.labelMedium,
                    )
                    SculptedButton(
                        text = if (myPing != null) "Drop ping" else "Set ping",
                        onClick = { showPingConfirmDialog = true },
                        modifier = Modifier.weight(1f),
                        accent = Success,
                        containerColor = Success,
                        textColor = Ink,
                        icon = Icons.Filled.LocationOn,
                        shape = RoundedCornerShape(18.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                        textStyle = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }

        // Download sheet — uses the centered pin's coordinates (map center)
        if (showDownloadSheet) {
            val center = mapView?.mapCenter
            val lat = center?.latitude ?: current.first
            val lng = center?.longitude ?: current.second
            MapDownloadSheet(
                lat = lat,
                lng = lng,
                onDismiss = { showDownloadSheet = false },
            )
        }

        // In-app conversation picker — share the user's ping to a selected chat.
        if (showConversationPicker && myPing != null) {
            val ping = myPing
            PingConversationPicker(
                navController = navController,
                ping = PingToShare(
                    lat = ping.lat,
                    lng = ping.lng,
                    label = ping.label,
                    senderId = auth.currentUserId ?: "",
                    senderName = profile.name,
                ),
                onDismiss = { showConversationPicker = false },
            )
        }

        // Shared ping directions dialog — shows when tapping a shared ping marker
        if (selectedSharedPing != null) {
            val sp = selectedSharedPing!!
            AlertDialog(
                onDismissRequest = { selectedSharedPing = null },
                containerColor = Color(0xFF1C1A14),
                titleContentColor = DarkTextHigh,
                textContentColor = DarkTextHigh,
                title = { Text("${sp.senderName}'s ping") },
                text = {
                    Column {
                        Text(sp.label)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Lat: %.5f  ·  Lng: %.5f".format(sp.lat, sp.lng),
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextMid,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Received ${sp.receivedAt.substringBefore("T")} · expires in 24h",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextMid,
                        )
                    }
                },
                confirmButton = {
                    SculptedButton(
                        text = "Get directions",
                        onClick = {
                            val origin = current
                            SafeLinkOpener.openPointToPointDirections(
                                context,
                                origin = Pair(origin.first, origin.second),
                                destination = Pair(sp.lat, sp.lng),
                            )
                        },
                        accent = Aqua,
                        containerColor = Aqua,
                        textColor = Ink,
                        icon = Icons.Filled.Directions,
                    )
                },
                dismissButton = {
                    Row {
                        SculptedOutlinedButton(
                            text = "Remove",
                            onClick = {
                                val id = sp.id
                                selectedSharedPing = null
                                scope.launch { social.removeSharedPing(id) }
                            },
                            accent = Warning,
                            textColor = DarkTextHigh,
                        )
                        Spacer(Modifier.width(8.dp))
                        SculptedOutlinedButton(
                            text = "Close",
                            onClick = { selectedSharedPing = null },
                            accent = Aqua,
                            textColor = DarkTextHigh,
                        )
                    }
                },
            )
        }

        // Remove ping confirmation dialog
        if (showRemovePingConfirm) {
            AlertDialog(
                onDismissRequest = { showRemovePingConfirm = false },
                containerColor = Color(0xFF1C1A14),
                titleContentColor = DarkTextHigh,
                textContentColor = DarkTextHigh,
                title = { Text("Stop sharing your location?") },
                text = {
                    Text("Other hunters will no longer be able to find you nearby. You can set a new ping anytime.")
                },
                confirmButton = {
                    SculptedButton(
                        text = "Stop sharing",
                        onClick = {
                            showRemovePingConfirm = false
                            scope.launch {
                                runCatching { social.clearMyPing() }
                                runCatching { social.loadVisiblePings() }
                            }
                        },
                        accent = Danger,
                        containerColor = Danger,
                        textColor = Color.White,
                    )
                },
                dismissButton = {
                    SculptedOutlinedButton(
                        text = "Cancel",
                        onClick = { showRemovePingConfirm = false },
                        accent = Aqua,
                        textColor = DarkTextHigh,
                    )
                },
            )
        }

        // Ping color picker dialog
        if (showColorPicker) {
            PingColorPickerDialog(
                currentColor = pingColorArgb,
                onSelect = { color ->
                    pingColorArgb = color
                    com.rork.rockscout.data.LocalDataStore.setString("ping_marker_color", color.value.toString())
                    showColorPicker = false
                    scope.launch { social.loadVisiblePings() }
                },
                onDismiss = { showColorPicker = false },
            )
        }

        // Ping confirmation dialog
        if (showPingConfirmDialog) {
            val center = mapView?.mapCenter
            AlertDialog(
                onDismissRequest = { showPingConfirmDialog = false },
                containerColor = Color(0xFF1C1A14),
                titleContentColor = DarkTextHigh,
                textContentColor = DarkTextHigh,
                title = { Text("Confirm your ping location") },
                text = {
                    Column {
                        Text(
                            "Set your ping at the current map center? This ping is private — only you can see it. Share it with someone via Messenger after it's set. It stays live for ${if (isPremium) "24 hours" else "12 hours"}.",
                        )
                        if (center != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Lat: %.5f  ·  Lng: %.5f".format(center.latitude, center.longitude),
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextMid,
                            )
                        }
                    }
                },
                confirmButton = {
                    SculptedButton(
                        text = "Confirm",
                        onClick = {
                            val mv = mapView
                            val center = mv?.mapCenter
                            if (center != null) {
                                showPingConfirmDialog = false
                                scope.launch {
                                    runCatching {
                                        val ttl = if (isPremium) 24 else 12
                                        social.setPing(center.latitude, center.longitude, "I'm here!", ttlHours = ttl)
                                        social.loadVisiblePings()
                                    }
                                }
                            }
                        },
                        accent = Success,
                        containerColor = Success,
                        textColor = Ink,
                        icon = Icons.Filled.Check,
                    )
                },
                dismissButton = {
                    SculptedOutlinedButton(
                        text = "Cancel",
                        onClick = { showPingConfirmDialog = false },
                        accent = Aqua,
                        textColor = DarkTextHigh,
                    )
                },
            )
        }
    }

    MapViewLifecycleEffect(mapView)
}

@Composable
private fun MapLockedState(
    emoji: String,
    title: String,
    message: String,
    buttonLabel: String,
    onButton: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(emoji, style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleLarge, color = DarkTextHigh, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid, textAlign = TextAlign.Center)
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

/** A live broadcast ping marker. Mine is citrine, others' are aqua. Tappable
 *  to open the thread (others) or no-op (mine). */
private class PingMarker(
    mapView: MapView,
    lat: Double,
    lng: Double,
    label: String,
    private val isMine: Boolean,
    private val displayName: String,
    private val avatarEmoji: String,
    private val onOpenThread: () -> Unit,
    markerColor: Color = Citrine,
) : Marker(mapView) {
    init {
        position = GeoPoint(lat, lng)
        title = if (isMine) "Your ping" else displayName
        snippet = label
        setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM)
        icon = createPingIcon(mapView.context, markerColor, avatarEmoji)
        // Note: the caller's chosen color is applied at the call site by passing
        // it through the PingMarker constructor — see the LaunchedEffect that
        // creates markers for the actual color used.
        setOnMarkerClickListener { _, _ ->
            if (!isMine) onOpenThread()
            true
        }
    }
}

/** Build a colored teardrop pin with an emoji glyph in the center. */
private fun createPingIcon(
    context: android.content.Context,
    color: Color,
    emoji: String,
): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val sizePx = (32 * density).toInt().coerceAtLeast(32)
    val bmp = android.graphics.Bitmap.createBitmap(sizePx, sizePx + (8 * density).toInt(), android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bmp)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
    val argb = color.toArgb()
    val cx = sizePx / 2f
    val cy = sizePx / 2f
    val r = sizePx / 3f
    // Teardrop tail
    paint.color = argb
    val path = android.graphics.Path().apply {
        moveTo(cx, bmp.height.toFloat())
        lineTo(cx - r * 0.9f, cy + r * 0.4f)
        lineTo(cx + r * 0.9f, cy + r * 0.4f)
        close()
    }
    canvas.drawPath(path, paint)
    canvas.drawCircle(cx, cy, r, paint)
    // White ring
    paint.color = android.graphics.Color.WHITE
    paint.style = android.graphics.Paint.Style.STROKE
    paint.strokeWidth = 2.5f * density
    canvas.drawCircle(cx, cy, r, paint)
    // Emoji glyph (best-effort — if it fails, fall back to a white dot)
    paint.style = android.graphics.Paint.Style.FILL
    paint.textSize = r * 1.1f
    paint.textAlign = android.graphics.Paint.Align.CENTER
    val fm = paint.fontMetrics
    val textY = cy - (fm.ascent + fm.descent) / 2
    runCatching {
        canvas.drawText(emoji, cx, textY, paint)
    }.onFailure {
        paint.color = android.graphics.Color.WHITE
        canvas.drawCircle(cx, cy, r * 0.35f, paint)
    }
    return android.graphics.drawable.BitmapDrawable(context.resources, bmp)
}

/** 30 easily-distinguishable colors for the ping marker color picker. */
private val PING_COLORS = listOf(
    Color(0xFFFF3B30), Color(0xFFFF9500), Color(0xFFFFCC00), Color(0xFFFF2D55), Color(0xFFE8A33D), Color(0xFFD9B26A),
    Color(0xFF34C759), Color(0xFF5CC98C), Color(0xFF00C7BE), Color(0xFF30B0C7), Color(0xFF32ADE6), Color(0xFF007AFF),
    Color(0xFF5856D6), Color(0xFF9B7BD8), Color(0xFFAF52DE), Color(0xFFB08BFF), Color(0xFFFF6B3D), Color(0xFFFF5E3A),
    Color(0xFF00E5C9), Color(0xFF4FC3F7), Color(0xFF6FA8C7), Color(0xFF7CB5EC), Color(0xFF8BBF6A), Color(0xFF6FBF8A),
    Color(0xFFB87333), Color(0xFFC97B4A), Color(0xFFE2574C), Color(0xFF1B3A4B), Color(0xFF44AACC), Color(0xFFC0C0C0),
)

@Composable
private fun PingColorPickerDialog(
    currentColor: Color,
    onSelect: (Color) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1C1A14),
        titleContentColor = DarkTextHigh,
        textContentColor = DarkTextHigh,
        title = { Text("Ping marker color", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Choose the color of your ping marker on other hunters' maps.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                )
                Spacer(Modifier.height(12.dp))
                PING_COLORS.chunked(6).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                    ) {
                        row.forEach { color ->
                            val isSelected = color == currentColor
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .glowingBorder(
                                        if (isSelected) 3.dp else 1.dp,
                                        if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                                        CircleShape,
                                    )
                                    .clickable { onSelect(color) },
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        },
        confirmButton = {
            SculptedOutlinedButton(
                text = "Close",
                onClick = onDismiss,
                accent = Aqua,
                textColor = DarkTextHigh,
            )
        },
    )
}

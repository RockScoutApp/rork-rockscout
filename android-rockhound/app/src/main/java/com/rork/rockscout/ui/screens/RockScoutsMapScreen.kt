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
import androidx.compose.material.icons.filled.Directions
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
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.Warning
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
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * RockScouts Map — shows live pings from you, your connected RockScout Friends,
 * and nearby hunters in your scan radius. Strangers outside the radius never
 * appear here. Supports a draggable preview pin + "Set Ping" confirmation before
 * broadcasting your location.
 *
 * Requires: RockScout Friends ON (auth is mandatory for all users).
 * Auto-switches to satellite-style
 * tiles at close zoom (street level) so users can see streets, parking lots,
 * and trailheads clearly.
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
    val isPremium by purchaseManager.isPremium.collectAsStateWithLifecycle()
    val clubLocked = remember(isPremium) {
        accessManager.isSocialLocked(isPremium)
    }

    val connections by social.connections.collectAsStateWithLifecycle()
    val pings by social.pings.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var myPingId by remember { mutableStateOf<String?>(null) }
    var hunterCache by remember { mutableStateOf<Map<String, SocialRepository.HunterProfile>>(emptyMap()) }
    var showSafetyNote by remember { mutableStateOf(true) }
    var showPingConfirmDialog by remember { mutableStateOf(false) }
    var showDownloadSheet by remember { mutableStateOf(false) }

    BackHandler(enabled = showSafetyNote) { showSafetyNote = false }

    // Load connections + pings on open.
    LaunchedEffect(isSignedIn, profile.clubEnabled) {
        if (isSignedIn && profile.clubEnabled) {
            social.loadConnections()
            social.loadVisiblePings(current.first, current.second, profile.scanRadiusMiles)
        }
    }

    // Fetch hunter profiles for connection partners (for pin pop-ups).
    LaunchedEffect(connections) {
        if (connections.isNotEmpty()) {
            val fetched = mutableMapOf<String, SocialRepository.HunterProfile>()
            connections.chunked(50).forEach { chunk ->
                social.fetchProfiles(chunk).forEach { fetched[it.id] = it }
            }
            hunterCache = fetched
        }
    }

    // Refresh pings periodically (~30s) while the screen is open.
    LaunchedEffect(profile.clubEnabled, current) {
        while (profile.clubEnabled) {
            kotlinx.coroutines.delay(30_000)
            social.loadVisiblePings(current.first, current.second, profile.scanRadiusMiles)
        }
    }


    // Track which ping is mine.
    LaunchedEffect(pings, auth.currentUserId) {
        myPingId = pings.firstOrNull { it.user_id == auth.currentUserId }?.id
    }

    // Render ping markers when the ping list or map changes.
    // Uses withContext (tied to this LaunchedEffect's coroutine) instead of
    // scope.launch so the work is properly cancelled before the MapView is
    // detached during disposal — prevents the race-condition crash that
    // occurred when navigating away (e.g. tapping "Open Profile").
    LaunchedEffect(pings, hunterCache, mapView) {
        val mv = mapView ?: return@LaunchedEffect
        try {
            withContext(Dispatchers.IO) {
                if (!isActive) return@withContext
                // Remove old ping markers (keep the preview + overlays).
                mv.overlays.removeAll { it is Marker && it.id?.startsWith("ping_") == true }
                pings.forEach { ping ->
                    if (!isActive) return@withContext
                    val isMine = ping.user_id == auth.currentUserId
                    val hunter = if (!isMine) hunterCache[ping.user_id] else null
                    val marker = PingMarker(
                        mv,
                        ping.lat,
                        ping.lng,
                        ping.label,
                        isMine,
                        hunter?.display_name ?: "You",
                        hunter?.avatar_emoji ?: "\u26CF\uFE0F",
                        onOpenThread = if (!isMine) {
                            { navController.navigate(Routes.messengerThread(ping.user_id)) }
                        } else ({ {} }),
                    )
                    marker.id = "ping_${ping.id}"
                    mv.overlays.add(marker)
                }
                if (!isActive) return@withContext
                withContext(Dispatchers.Main) {
                    runCatching { mv.invalidate() }
                }
            }
        } catch (_: kotlinx.coroutines.CancellationException) {
            // Expected when navigating away — don't touch the MapView.
        } catch (_: Throwable) {
            // MapView may have been detached concurrently; swallow to avoid crash.
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
            message = "Enable the RockScout Friends toggle in your Profile to use the RockScouts Map and ping your connections.",
            buttonLabel = "Open Profile",
            onButton = { navController.navigate(Routes.PROFILE) },
        )
        return
    }

    val myPing = pings.firstOrNull { it.user_id == auth.currentUserId }
    // Show directions pill whenever my ping is live alongside any other ping.
    val showPingDirections = myPing != null && pings.any { it.user_id != auth.currentUserId }

    Box(modifier = Modifier.fillMaxSize()) {
        // Map
        Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp))) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    createRockScoutMapView(ctx).apply {
                        controller.setZoom(8.0)
                        controller.setCenter(GeoPoint(current.first, current.second))
                        overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
                        overlays.add(CompassOverlay(ctx, this).apply { enableCompass() })
                        // User location dot
                        val locOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                        locOverlay.enableMyLocation()
                        overlays.add(locOverlay)

                        mapView = this
                    }
                },
                update = { /* No-op — tile switching handled by overlay listener in factory. */ },
            )

            MapOfflineNotice(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 76.dp),
            )


        }

        // Centered ping pin — stays fixed at screen center while the map pans
        // underneath. The tip of the pin marks the exact geo-point at the map center.
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            // Glow circle at the exact target point
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(Warning.copy(alpha = 0.35f))
                    .glowingBorder(2.dp, Warning, CircleShape),
            )
            // Pin above center — tip touches the center point
            Icon(
                Icons.Filled.LocationOn,
                contentDescription = "Drag the map to set your ping",
                tint = Warning,
                modifier = Modifier
                    .size(44.dp)
                    .offset(y = (-22).dp),
            )
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
            // Connections count badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Aqua.copy(alpha = 0.20f))
                    .glowingBorder(2.dp, Aqua.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    "${connections.size} connected",
                    style = MaterialTheme.typography.labelMedium,
                    color = Aqua,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // Zoom controls — compact shared component, raised well above the bottom
        // action bar so it never overlaps the Drop ping / Remove ping controls.
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
                .padding(end = 16.dp, bottom = 192.dp)
                .navigationBarsPadding(),
        )

        // Safety note (dismissible)
        AnimatedVisibility(
            visible = showSafetyNote,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 110.dp, start = 16.dp, end = 16.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1C1A14).copy(alpha = 0.95f))
                    .glowingBorder(2.dp, Warning.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("\uD83D\uDEE1\uFE0F", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Feel free to move your ping to a safe meet-up spot. Safety first, always.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextHigh,
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
                                onClick = { showSafetyNote = false },
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
            }
        }

        // Bottom action bar — Set Ping sits in the same bottom row as the profile
        // button (full-width when no live ping; Set Ping + Remove side-by-side
        // when a ping is already live). Lifted above the system navigation bar.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xFF1C1A14).copy(alpha = 0.95f))
                .padding(16.dp)
                .navigationBarsPadding(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Directions pill (above the ping controls) — shown when 2 pings are visible
                if (showPingDirections) {
                    SculptedOutlinedButton(
                        text = "Directions to other ping",
                        onClick = {
                            val otherPing = pings.firstOrNull { it.user_id != auth.currentUserId }
                            if (otherPing != null && myPing != null) {
                                SafeLinkOpener.openPointToPointDirections(
                                    context,
                                    Pair(myPing.lat, myPing.lng),
                                    Pair(otherPing.lat, otherPing.lng),
                                )
                            }
                        },
                        accent = Aqua,
                        textColor = Aqua,
                        icon = Icons.Filled.Directions,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(10.dp))
                }
                // Download Map button — uses the centered pin's coordinates (map center)
                SculptedOutlinedButton(
                    text = "Download offline maps",
                    onClick = { showDownloadSheet = true },
                    accent = Aqua,
                    textColor = Aqua,
                    icon = Icons.Filled.Download,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(10.dp))
                if (myPing != null) {
                    // Live ping — show info + Set Ping (update) / Remove
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Success.copy(alpha = 0.30f))
                                .glowingBorder(1.dp, Success.copy(alpha = 0.35f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Success, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Your ping is live", style = MaterialTheme.typography.titleMedium, color = DarkTextHigh, fontWeight = FontWeight.Bold)
                            Text("Visible to your friends and nearby hunters · expires in ${if (isPremium) "24h" else "12h"}", style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        SculptedButton(
                            text = "Drop ping",
                            onClick = { showPingConfirmDialog = true },
                            modifier = Modifier.weight(2f),
                            accent = Success,
                            containerColor = Success,
                            textColor = Ink,
                            icon = Icons.Filled.LocationOn,
                            shape = RoundedCornerShape(24.dp),
                        )
                        SculptedButton(
                            text = "Remove ping",
                            onClick = {
                                scope.launch {
                                    runCatching { social.clearMyPing() }
                                    runCatching { social.loadVisiblePings() }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            accent = Aqua,
                            containerColor = Color(0xFF3A3830),
                            textColor = DarkTextHigh,
                            shape = RoundedCornerShape(24.dp),
                        )
                    }
                } else {
                    Text(
                        "Drag the map to position your pin, then tap Set ping.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(10.dp))
                    SculptedButton(
                        text = "Set ping",
                        onClick = { showPingConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        accent = Success,
                        containerColor = Success,
                        textColor = Ink,
                        icon = Icons.Filled.LocationOn,
                        shape = RoundedCornerShape(24.dp),
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
                            "Set your ping at the current map center? Your RockScout friends and nearby hunters will see this location for ${if (isPremium) "24 hours" else "12 hours"}.",
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
                            val mv = mapView ?: return@SculptedButton
                            val c = mv.mapCenter
                            showPingConfirmDialog = false
                            scope.launch {
                                val ttl = if (isPremium) 24 else 12
                                social.setPing(c.latitude, c.longitude, "I'm here!", ttlHours = ttl)
                                social.loadVisiblePings()
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
) : Marker(mapView) {
    init {
        position = GeoPoint(lat, lng)
        title = if (isMine) "Your ping" else displayName
        snippet = label
        setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM)
        icon = createPingIcon(mapView.context, if (isMine) Citrine else Aqua, avatarEmoji)
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

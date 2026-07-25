package com.rork.rockscout.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.DigLocation
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.WildlifeData
import com.rork.rockscout.ui.components.WildlifeCard
import com.rork.rockscout.data.SpecimenImages
import com.rork.rockscout.data.WeatherRepository
import com.rork.rockscout.data.WeatherSnapshot
import com.rork.rockscout.data.HourlyTrend
import com.rork.rockscout.data.GearGuide
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.GearLinksCard
import com.rork.rockscout.ui.components.MapTileCacheManager
import com.rork.rockscout.data.OfflineMapExporter
import com.rork.rockscout.ui.components.MapViewLifecycleEffect
import com.rork.rockscout.ui.components.MapCacheStatusIndicator
import com.rork.rockscout.ui.components.MapZoomControls
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.ShareCardImage
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.applyHybridTiles
import com.rork.rockscout.ui.components.MapOfflineNotice
import com.rork.rockscout.ui.components.createRockScoutMapView
import com.rork.rockscout.ui.components.MapExpandButton
import com.rork.rockscout.ui.components.FullscreenMapOverlay
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.SpecimenListItem
import com.rork.rockscout.ui.components.rockClassColor
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.navigation.Routes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.components.glowingBorder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LocationDetailScreen(navController: NavController, locationId: String) {
    val loc = SeedData.locationById(locationId)
    val repo = AppRepository.instance
    val current by repo.currentLocation.collectAsStateWithLifecycle()
    val favorites by repo.favoriteSpots.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (loc == null) {
        RockBackground { Box(Modifier.fillMaxSize().padding(40.dp)) { Text("Location not found.", color = TextMid) } }
        return
    }

    val miles = AppRepository.distanceMiles(current.first, current.second, loc.latitude, loc.longitude)
    val isFav = favorites.contains(loc.id)
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var shareToProfileLocation by remember { mutableStateOf<DigLocation?>(null) }

    // Weather snapshot for this dig site (Open-Meteo + locally-computed sunrise/sunset).
    // Auto-fetches on first composition; cached 30 min per site by WeatherRepository.
    var weather by remember(loc.id) { mutableStateOf<WeatherSnapshot?>(WeatherRepository.cached(loc.id)) }
    var weatherLoading by remember(loc.id) { mutableStateOf(weather == null) }
    LaunchedEffect(loc.id) {
        if (weather == null || weather?.isStale == true) {
            weatherLoading = true
            val snap = WeatherRepository.fetch(loc.id, loc.latitude, loc.longitude)
            if (snap != null) weather = snap
            weatherLoading = false
        }
    }

    RockBackground {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                scope.launch {
                    delay(800)
                    isRefreshing = false
                }
            },
            modifier = Modifier.fillMaxSize(),
        ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Back button at the top — separated from the map so the map
            // can be positioned further down without losing navigation.
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .sculpted(
                            shape = CircleShape,
                            accent = Citrine,
                            shadowElevation = 5.dp,
                            circular = true,
                            onClick = { navController.popBackStack() },
                        )
                        .clip(CircleShape)
                        .background(Color(0xCC0C0F14))
                        .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TagChip(loc.type.label.uppercase(), color = Aqua, filled = true)
                    Spacer(Modifier.width(8.dp))
                    TagChip(loc.difficulty.uppercase(), color = difficultyColor(loc.difficulty))
                }
                Spacer(Modifier.height(10.dp))
                Text(loc.name, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
                Text(loc.region, style = MaterialTheme.typography.bodyLarge, color = TextMid)
                if (loc.submitterName != null) {
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.PanTool,
                            contentDescription = null,
                            tint = Citrine,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Submitted by ${loc.submitterName}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Citrine,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SculptedButton(
                    text = "Directions",
                    onClick = {
                        val googleMapsUri = "google.navigation:q=${loc.latitude},${loc.longitude}"
                        val fallbackGeoUri = "geo:${loc.latitude},${loc.longitude}?q=${loc.latitude},${loc.longitude}(${Uri.encode(loc.name)})"
                        SafeLinkOpener.openMaps(context, googleMapsUri, fallbackGeoUri)
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    accent = Citrine,
                    containerColor = Citrine,
                    textColor = Ink,
                    icon = Icons.Filled.Directions,
                )
                SculptedIconButton(
                    icon = if (isFav) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = "Favorite",
                    onClick = { repo.toggleFavoriteSpot(loc.id) },
                    accent = Citrine,
                    iconTint = if (isFav) Citrine else TextMid,
                    size = 50.dp,
                )
                SculptedIconButton(
                    icon = Icons.Filled.Share,
                    contentDescription = "Share spot",
                    onClick = {
                        scope.launch {
                            ShareCardImage.share(
                                context = context,
                                title = loc.name,
                                subtitle = loc.region + "  •  " + loc.type.label,
                                body = loc.summary,
                                accentHex = 0xFF2C6F9B,
                                fileName = "rockscout_site_${loc.id}",
                            )
                        }
                    },
                    accent = Citrine,
                    iconTint = TextMid,
                    size = 50.dp,
                )
                SculptedIconButton(
                    icon = Icons.Filled.PersonAdd,
                    contentDescription = "Share to profile",
                    onClick = { shareToProfileLocation = loc },
                    accent = Citrine,
                    iconTint = Citrine,
                    size = 50.dp,
                )
            }
            // Map moved here — between the directions buttons and the field conditions card.
            RouteMap(loc, miles, showBackButton = false) { navController.popBackStack() }
            WeatherCard(
                snapshot = weather,
                loading = weatherLoading,
                latitude = loc.latitude,
                longitude = loc.longitude,
            )

            // Common Wildlife (beach/coastal locations only)
            if (loc.type == com.rork.rockscout.data.LocationType.BEACH) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    WildlifeCard(
                        wildlife = WildlifeData.forCoastalLocation(loc.region, loc.latitude, loc.longitude),
                    )
                }
            }
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                // Pre-arrival tip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Citrine.copy(alpha = 0.08f))
                        .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                ) {
                    Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = Citrine, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Download before you lose signal! Cache this area ahead of time so your map and satellite imagery are ready when you arrive on-site with no cell service.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                    )
                }
                Spacer(Modifier.height(12.dp))
                var isDownloadingMaps by remember { mutableStateOf(false) }
                var mapsDownloaded by remember { mutableStateOf(false) }
                // Two-row button: action on row 1, "at pin location" on row 2
                SculptedOutlinedButton(
                    text = if (isDownloadingMaps) "Downloading maps…" else if (mapsDownloaded) "Offline maps ready ✓" else "Download offline map tiles",
                    onClick = {
                        if (isDownloadingMaps) return@SculptedOutlinedButton
                        isDownloadingMaps = true
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                runCatching {
                                    MapTileCacheManager.prefetchLocation(context, loc)
                                }
                            }
                            isDownloadingMaps = false
                            mapsDownloaded = true
                        }
                    },
                    accent = Aqua,
                    textColor = Aqua,
                    icon = Icons.Filled.Download,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isDownloadingMaps,
                )
                Text(
                    "at this location",
                    style = MaterialTheme.typography.labelSmall,
                    color = Aqua,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Caches satellite, street, and label tiles at zoom 10–19 so the map works with zero signal. Tap to zoom to full detail on-site.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
                Spacer(Modifier.height(10.dp))
                var isGeneratingMapImage by remember { mutableStateOf(false) }
                SculptedOutlinedButton(
                    text = if (isGeneratingMapImage) "Generating map image…" else "Save ultra high-res map image",
                    onClick = {
                        if (isGeneratingMapImage) return@SculptedOutlinedButton
                        isGeneratingMapImage = true
                        scope.launch {
                            OfflineMapExporter.saveOfflineMapImage(context, loc.latitude, loc.longitude)
                            isGeneratingMapImage = false
                        }
                    },
                    accent = Citrine,
                    textColor = Citrine,
                    icon = Icons.Filled.PhotoCamera,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isGeneratingMapImage,
                )
                Text(
                    "at this location",
                    style = MaterialTheme.typography.labelSmall,
                    color = Citrine,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Stitches a single ultra high-resolution satellite image (3-mile radius, zoom 15) and saves it to your gallery's Offline Maps folder. Pinch to zoom in deep — trails, terrain, and landmarks stay crisp even with no signal. Perfect for finding your way back to your vehicle when you're off-grid.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
            Section("About this spot") {
                Text(loc.summary, style = MaterialTheme.typography.bodyLarge, color = DarkTextMid)
            }
            Section("Visitor info") {
                InfoLine(Icons.Filled.ConfirmationNumber, "Prices & fees", loc.feeInfo)
                Spacer(Modifier.height(12.dp))
                InfoLine(Icons.Filled.AccessTime, "Operating hours", loc.hours)
                loc.phone?.let {
                    Spacer(Modifier.height(12.dp))
                    InfoLine(Icons.Filled.Phone, "Phone", it)
                }
                Spacer(Modifier.height(12.dp))
                InfoLine(Icons.Filled.Terrain, "Difficulty", loc.difficulty)
            }
            loc.website?.let { site ->
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    DarkCard(
                        accent = Aqua,
                        modifier = Modifier.fillMaxWidth().clickable {
                            SafeLinkOpener.openUrl(context, site)
                        },
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(42.dp).clip(CircleShape).background(Aqua.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center,
                            ) { Icon(Icons.Filled.Language, contentDescription = null, tint = Aqua) }
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Visit official website", style = MaterialTheme.typography.titleMedium, color = Color.White)
                                Text(site.removePrefix("https://").removePrefix("http://"), style = MaterialTheme.typography.bodyMedium, color = Aqua, maxLines = 1)
                            }
                        }
                    }
                }
            }
            Section("What you can find") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    loc.mineralTags.forEach { TagChip(it, color = Citrine) }
                }
                val linked = loc.knownFor.mapNotNull { SeedData.specimenById(it) }
                if (linked.isNotEmpty()) {
                    Spacer(Modifier.height(14.dp))
                    Text("In your field guide", style = MaterialTheme.typography.labelMedium, color = DarkTextLow, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        linked.forEach { spec ->
                            val accent = rockClassColor(spec.rockClass)
                            SpecimenListItem(
                                specimen = spec,
                                accent = accent,
                                onClick = { navController.navigate(Routes.specimen(spec.id)) },
                                showCategory = true,
                                imageSize = 92.dp,
                                trailing = {
                                    Text("View ›", color = accent, style = MaterialTheme.typography.labelLarge)
                                },
                            )
                        }
                    }
                }
            }
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
                    Row {
                        Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = Citrine, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Field tip", style = MaterialTheme.typography.titleMedium, color = Citrine)
                            Spacer(Modifier.height(4.dp))
                            Text(loc.tips, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid)
                        }
                    }
                }
            }
            GearLinksCard(
                sectionTitle = "Recommended gear for this trip",
                items = GearGuide.gearForLocationType(loc.type),
                accent = Aqua,
            )
        }
        }
    }

    shareToProfileLocation?.let { shareLoc ->
        ShareToProfileComposer(
            sourceType = "digsite",
            title = shareLoc.name,
            tagline = shareLoc.region + "  •  " + shareLoc.type.label,
            imageUri = SpecimenImages.urls[shareLoc.id]?.firstOrNull(),
            locationText = shareLoc.region,
            onDismiss = { shareToProfileLocation = null },
        )
    }
}

@Composable
private fun RouteMap(loc: DigLocation, miles: Double, showBackButton: Boolean = true, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mapView by remember { mutableStateOf<org.osmdroid.views.MapView?>(null) }
    var isFullscreen by remember { mutableStateOf(false) }
    // Live cache timestamp for this location's tiles — drives the sync-status
    // pill on the route map. Re-read after a prefetch / refresh.
    var pointCacheTimestamp by remember(loc.id) {
        mutableStateOf(com.rork.rockscout.data.PersistenceManager.pointCacheTime(loc.latitude, loc.longitude))
    }
    val repo = AppRepository.instance
    val current by repo.currentLocation.collectAsStateWithLifecycle()

    // Drop a destination marker and route line once the map is ready.
    // Captures the current location snapshot once — does NOT re-run on every
    // GPS update, which would reset the user's pan/zoom and cause jank.
    LaunchedEffect(mapView) {
        val mv = mapView ?: return@LaunchedEffect
        val destPoint = org.osmdroid.util.GeoPoint(loc.latitude, loc.longitude)
        val currentPoint = org.osmdroid.util.GeoPoint(current.first, current.second)
        val routePoints = listOf(currentPoint, destPoint)

        // Route line
        val polyline = org.osmdroid.views.overlay.Polyline().apply {
            id = "route_to_site"
            setPoints(routePoints)
            outlinePaint.strokeWidth = 6f * context.resources.displayMetrics.density
            outlinePaint.color = Citrine.toArgb()
            outlinePaint.isAntiAlias = true
        }
        mv.overlays.removeAll { it is org.osmdroid.views.overlay.Polyline && it.id == "route_to_site" }
        mv.overlays.add(0, polyline)

        // Destination pin
        val marker = org.osmdroid.views.overlay.Marker(mv).apply {
            position = destPoint
            title = loc.name
            snippet = loc.region
            setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM)
            icon = createLocationPinIcon(context, Citrine)
        }
        mv.overlays.removeAll { it is org.osmdroid.views.overlay.Marker && it.id == "site_dest" }
        marker.id = "site_dest"
        mv.overlays.add(marker)

        // Fit the route in view.
        val box = org.osmdroid.util.BoundingBox.fromGeoPoints(routePoints)
        mv.zoomToBoundingBox(box, false, 48)
        mv.invalidate()

        // NOTE: Automatic tile prefetching removed — it was causing the app to
        // freeze on entry. The prefetch created multiple SqlTileWriter instances
        // that contended for the same SQLite database, and the bounding-box tile
        // download for the route corridor at zoom 12-18 queued thousands of tiles
        // across 3 tile sources on every screen load.
        // The map loads tiles on demand (lazy loading) for normal viewing.
        // Users can manually download offline tiles via the "Download offline maps" button.
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(275.dp)
            .clip(RoundedCornerShape(20.dp)),
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                createRockScoutMapView(ctx, isEmbedded = true).apply {
                    controller.setZoom(12.0)
                    controller.setCenter(org.osmdroid.util.GeoPoint(loc.latitude, loc.longitude))
                    overlays.add(org.osmdroid.views.overlay.gestures.RotationGestureOverlay(this).apply { isEnabled = true })
                    overlays.add(org.osmdroid.views.overlay.compass.CompassOverlay(ctx, this).apply { enableCompass() })
                    mapView = this
                }
            },
            update = { /* no-op */ },
        )

        if (showBackButton) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 48.dp)
                    .size(44.dp)
                    .sculpted(
                        shape = CircleShape,
                        accent = Citrine,
                        shadowElevation = 5.dp,
                        circular = true,
                        onClick = onBack,
                    )
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xCC0C0F14))
                .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Directions, contentDescription = null, tint = Citrine, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("${miles.toInt()} mi from your location", style = MaterialTheme.typography.labelLarge, color = Color.White)
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
        ) {
            TagChip("SATELLITE READY", color = Citrine)
        }

        MapZoomControls(
            onZoomIn = { mapView?.controller?.zoomIn() },
            onZoomOut = { mapView?.controller?.zoomOut() },
            onRecenter = {},
            showUser = false,
            onSatellite = { com.rork.rockscout.ui.components.toggleSatelliteView(mapView) },
            mapView = mapView,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        )

        MapExpandButton(
            onClick = {
                mapView?.let {
                    isFullscreen = true
                }
            },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
        )

        MapOfflineNotice(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 60.dp),
        )

        MapCacheStatusIndicator(
            cachedAtMillis = pointCacheTimestamp,
            onRefresh = {
                val mv = mapView ?: return@MapCacheStatusIndicator
                scope.launch {
                    withContext(kotlinx.coroutines.Dispatchers.IO) {
                        runCatching {
                            com.rork.rockscout.ui.components.MapTileCacheManager.prefetchLocation(
                                context = context,
                                loc = loc,
                            )
                        }
                    }
                    pointCacheTimestamp = com.rork.rockscout.data.PersistenceManager.pointCacheTime(loc.latitude, loc.longitude)
                    android.widget.Toast.makeText(
                        context,
                        "Offline tiles refreshed.",
                        android.widget.Toast.LENGTH_SHORT,
                    ).show()
                }
            },
            label = "Location tiles",
            modifier = Modifier.align(Alignment.TopStart).padding(start = 12.dp, top = 60.dp),
        )
    }

    if (isFullscreen) {
        FullscreenMapOverlay(
            onDismiss = { isFullscreen = false },
            initialCenter = org.osmdroid.util.GeoPoint(loc.latitude, loc.longitude),
            initialZoom = mapView?.zoomLevelDouble ?: 12.0,
            onMapReady = { fsMv ->
                // Build the route overlay and marker on a background thread, then
                // add them to the MapView and zoom on the main thread to avoid the
                // ANR that happened when expanding the map.
                withContext(kotlinx.coroutines.Dispatchers.IO) {
                    val destPoint = org.osmdroid.util.GeoPoint(loc.latitude, loc.longitude)
                    val currentPoint = org.osmdroid.util.GeoPoint(current.first, current.second)
                    val routePoints = listOf(currentPoint, destPoint)
                    val polyline = org.osmdroid.views.overlay.Polyline().apply {
                        id = "route_to_site_fs"
                        setPoints(routePoints)
                        outlinePaint.strokeWidth = 6f * context.resources.displayMetrics.density
                        outlinePaint.color = Citrine.toArgb()
                        outlinePaint.isAntiAlias = true
                    }
                    val marker = org.osmdroid.views.overlay.Marker(fsMv).apply {
                        position = destPoint
                        title = loc.name
                        snippet = loc.region
                        setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM)
                        icon = createLocationPinIcon(context, Citrine)
                    }
                    marker.id = "site_dest_fs"
                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                        fsMv.overlays.add(0, polyline)
                        fsMv.overlays.add(marker)
                        val box = org.osmdroid.util.BoundingBox.fromGeoPoints(routePoints)
                        fsMv.zoomToBoundingBox(box, false, 48)
                        fsMv.invalidate()
                    }
                }
            },
        )
    }

    MapViewLifecycleEffect(mapView)
}

/** Simple teardrop pin for the destination marker on the location detail map. */
private fun createLocationPinIcon(context: android.content.Context, color: Color): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val sizePx = (32 * density).toInt().coerceAtLeast(32)
    val bmp = android.graphics.Bitmap.createBitmap(sizePx, sizePx, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bmp)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
    val cx = sizePx / 2f
    val cy = sizePx / 2f
    val r = sizePx / 3f

    paint.color = color.toArgb()
    val path = android.graphics.Path().apply {
        moveTo(cx, sizePx.toFloat())
        lineTo(cx - r * 0.9f, cy + r * 0.4f)
        lineTo(cx + r * 0.9f, cy + r * 0.4f)
        close()
    }
    canvas.drawPath(path, paint)
    canvas.drawCircle(cx, cy, r, paint)

    paint.color = android.graphics.Color.WHITE
    paint.style = android.graphics.Paint.Style.STROKE
    paint.strokeWidth = 2.5f * density
    canvas.drawCircle(cx, cy, r, paint)
    paint.style = android.graphics.Paint.Style.FILL
    canvas.drawCircle(cx, cy, r * 0.35f, paint)

    return android.graphics.drawable.BitmapDrawable(context.resources, bmp)
}

@Composable
private fun InfoLine(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = Citrine, modifier = Modifier.size(20.dp).padding(top = 2.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = DarkTextLow, fontWeight = FontWeight.Bold)
            Text(value, style = MaterialTheme.typography.bodyLarge, color = DarkTextHigh)
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = TextMid,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
        )
        DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) { content() }
    }
}

private fun difficultyColor(difficulty: String): Color = when (difficulty) {
    "Easy" -> Success
    "Moderate" -> Citrine
    else -> Color(0xFFE2574C)
}

/**
 * Weather + daylight card shown on the dig site detail screen.
 * Shows current temperature/conditions/precip/wind, today's sunrise/sunset window,
 * and a 3-hour forecast strip. Falls back to a loading state and an offline
 * state (last cached + offline-computed daylight) per the plan.
 */
@Composable
private fun WeatherCard(
    snapshot: WeatherSnapshot?,
    loading: Boolean,
    latitude: Double,
    longitude: Double,
) {
    Box(modifier = Modifier.padding(horizontal = 20.dp)) {
        DarkCard(modifier = Modifier.fillMaxWidth(), accent = Aqua) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(Aqua.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        snapshot?.conditionEmoji ?: "--",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "FIELD CONDITIONS",
                        style = MaterialTheme.typography.labelMedium,
                        color = Aqua,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(2.dp))
                    if (loading && snapshot == null) {
                        Text(
                            "Loading weather…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextMid,
                        )
                    } else if (snapshot == null) {
                        Text(
                            "Weather unavailable — offline.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextMid,
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "${snapshot.temperatureF}°F",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                snapshot.conditionLabel,
                                style = MaterialTheme.typography.bodyLarge,
                                color = DarkTextHigh,
                            )
                        }
                    }
                }
            }

            if (snapshot != null) {
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MetricPill(Icons.Filled.Umbrella, "${snapshot.precipProbability}%", "Rain")
                    MetricPill(Icons.Filled.Air, "${snapshot.windMph}", "Wind mph")
                }

                Spacer(Modifier.height(14.dp))
                DaylightRow(snapshot)

                if (snapshot.hourly.isNotEmpty()) {
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "NEXT 3 HOURS",
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkTextLow,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        snapshot.hourly.forEach { h ->
                            HourlyTrendCell(h, modifier = Modifier.weight(1f))
                        }
                    }
                }

                if (snapshot.isStale && !loading) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Cached ${snapshot.cachedMinutesAgo} min ago",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextLow,
                    )
                }
            } else if (!loading) {
                // Offline + never-cached: still show the daylight window.
                Spacer(Modifier.height(14.dp))
                OfflineDaylightRow(latitude, longitude)
            }
        }
    }
}

@Composable
private fun RowScope.MetricPill(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    Row(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x14FFFFFF))
            .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = Aqua, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(value, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = DarkTextLow)
        }
    }
}

@Composable
private fun DaylightRow(snapshot: WeatherSnapshot) {
    val nowSec = System.currentTimeMillis() / 1000L
    val daylightLabel = WeatherRepository.daylightLabel(nowSec, snapshot.sunriseEpochSec, snapshot.sunsetEpochSec)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.WbSunny, contentDescription = null, tint = Citrine, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            "Sunrise ${WeatherRepository.formatLocalTime(snapshot.sunriseEpochSec)}",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextHigh,
        )
        Spacer(Modifier.width(12.dp))
        Icon(Icons.Filled.NightsStay, contentDescription = null, tint = Aqua, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            "Sunset ${WeatherRepository.formatLocalTime(snapshot.sunsetEpochSec)}",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextHigh,
        )
    }
    Spacer(Modifier.height(4.dp))
    Text(
        daylightLabel,
        style = MaterialTheme.typography.labelMedium,
        color = Citrine,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun OfflineDaylightRow(latitude: Double, longitude: Double) {
    val nowSec = System.currentTimeMillis() / 1000L
    val (sunrise, sunset) = remember(latitude, longitude) {
        WeatherRepository.computeSunriseSunset(latitude, longitude, nowSec)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.WbSunny, contentDescription = null, tint = Citrine, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            "Sunrise ${WeatherRepository.formatLocalTime(sunrise)}",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextHigh,
        )
        Spacer(Modifier.width(12.dp))
        Icon(Icons.Filled.NightsStay, contentDescription = null, tint = Aqua, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            "Sunset ${WeatherRepository.formatLocalTime(sunset)}",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextHigh,
        )
    }
}

@Composable
private fun HourlyTrendCell(hour: HourlyTrend, modifier: Modifier = Modifier) {
    val cal = java.util.GregorianCalendar(java.util.TimeZone.getDefault())
    cal.timeInMillis = hour.hourEpochSec * 1000L
    val hourLabel = java.text.SimpleDateFormat("ha", java.util.Locale.US).apply {
        timeZone = java.util.TimeZone.getDefault()
    }.format(cal.time)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x14FFFFFF))
            .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(hourLabel, style = MaterialTheme.typography.labelSmall, color = DarkTextLow)
        Spacer(Modifier.height(4.dp))
        Text(WeatherRepository.weatherCodeEmoji(hour.weatherCode), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        Text("${hour.temperatureF}°", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Umbrella, contentDescription = null, tint = Aqua, modifier = Modifier.size(12.dp))
            Spacer(Modifier.width(3.dp))
            Text("${hour.precipProbability}%", style = MaterialTheme.typography.labelSmall, color = DarkTextMid)
        }
    }
}

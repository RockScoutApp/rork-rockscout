package com.rork.rockscout.ui.components

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.DirectionsCar
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.rork.rockscout.data.DigLocation
import com.rork.rockscout.data.GemShow
import com.rork.rockscout.data.GemShowData
import com.rork.rockscout.data.LocationType
import com.rork.rockscout.data.PersistenceManager
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.Warning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.rork.rockscout.ui.components.MapTileCacheManager
import com.rork.rockscout.ui.components.MapZoomControls
import com.rork.rockscout.ui.components.MapExpandButton
import com.rork.rockscout.ui.components.FullscreenMapOverlay
import com.rork.rockscout.ui.components.MapDownloadSheet
import com.rork.rockscout.ui.components.ParkingButtonState
import com.rork.rockscout.ui.components.applyHybridTiles
import com.rork.rockscout.ui.components.createRockScoutMapView
import com.rork.rockscout.ui.components.toggleSatelliteView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * Real, interactive OpenStreetMap map for the Dig Sites tab.
 * Smooth pinch-zoom, pan, rotation, and detailed street/terrain tiles — Google Maps style.
 * No API key required (uses OSM public tile servers).
 */
@Composable
fun DigSitesMapView(
    locations: List<DigLocation>,
    userLat: Double,
    userLng: Double,
    showUser: Boolean,
    onLocationTap: (DigLocation) -> Unit,
    modifier: Modifier = Modifier,
    showGemShows: Boolean = true,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var isFullscreen by remember { mutableStateOf(false) }
    var pinLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var showDownloadSheet by remember { mutableStateOf(false) }
    var parkingSpot by remember { mutableStateOf<PersistenceManager.ParkingSpot?>(null) }
    var showClearParkingConfirm by remember { mutableStateOf(false) }
    // Live cache timestamp for the user's current area — drives the sync-status
    // pill on the dig sites map. Re-read after a prefetch / refresh.
    // Load persisted parking spot on first composition.
    LaunchedEffect(Unit) {
        parkingSpot = PersistenceManager.loadParkingSpot()
    }

    // Gem shows with valid coordinates.
    val gemShows = remember(showGemShows) {
        if (showGemShows) GemShowData.enrichedShows.filter { it.latitude != 0.0 && it.longitude != 0.0 } else emptyList()
    }

    // Markers must be recreated when the location list changes.
    LaunchedEffect(locations, onLocationTap, gemShows) {
        val mv = mapView ?: return@LaunchedEffect
        scope.launch(Dispatchers.IO) {
            mv.overlays.removeAll { it is Marker && (it.id?.startsWith("dig_") == true || it.id?.startsWith("gemshow_") == true) }
            locations.filter { it.latitude != 0.0 || it.longitude != 0.0 }.forEach { loc ->
                val marker = DigMarker(mv, loc, onLocationTap)
                marker.id = "dig_${loc.id}"
                mv.overlays.add(marker)
            }
            gemShows.forEach { show ->
                val marker = GemShowMarker(mv, show)
                marker.id = "gemshow_${show.id}"
                mv.overlays.add(marker)
            }
            withContext(Dispatchers.Main) { mv.invalidate() }
        }
    }

    // Update the parking marker whenever the parking spot changes.
    LaunchedEffect(parkingSpot, mapView) {
        val mv = mapView ?: return@LaunchedEffect
        scope.launch(Dispatchers.IO) {
            mv.overlays.removeAll { it is Marker && it.id == "parking_spot" }
            parkingSpot?.let { spot ->
                val marker = Marker(mv).apply {
                    id = "parking_spot"
                    position = GeoPoint(spot.latitude, spot.longitude)
                    title = "Your Parking Spot"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = createParkingIcon(mv.context)
                }
                mv.overlays.add(marker)
            }
            withContext(Dispatchers.Main) { mv.invalidate() }
        }
    }

    // Toggle the user location dot when the setting changes.
    LaunchedEffect(showUser, mapView) {
        val mv = mapView ?: return@LaunchedEffect
        var locOverlay = mv.overlays.firstOrNull { it is MyLocationNewOverlay } as? MyLocationNewOverlay
        if (showUser && locOverlay == null) {
            locOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mv)
            locOverlay.enableMyLocation()
            locOverlay.enableFollowLocation()
            mv.overlays.add(locOverlay)
        } else if (!showUser && locOverlay != null) {
            locOverlay.disableMyLocation()
            locOverlay.disableFollowLocation()
            mv.overlays.remove(locOverlay)
        }
        mv.invalidate()
    }

    // Center on user or the US **only once** when the map first becomes available.
    // Re-centering on every location update would reset the user's pinch-zoom.
    // The recenter button (MapZoomControls) handles manual re-centering.
    LaunchedEffect(mapView) {
        val mv = mapView ?: return@LaunchedEffect
        if (showUser && (userLat != 0.0 || userLng != 0.0)) {
            mv.controller.animateTo(GeoPoint(userLat, userLng), 5.0, 600)
        } else {
            mv.controller.animateTo(GeoPoint(39.5, -98.0), 4.0, 800)
        }
    }

    // NOTE: Automatic tile prefetching removed — it was causing the same
    // entry-freeze bug that affected LocationDetailScreen. prefetchUserArea
    // downloads hundreds of tiles across 3 tile sources × zoom 12-19 on screen
    // load, contending with the visible map's own tile loads on the shared
    // SqlTileWriter lock. The map loads tiles on demand for visible areas;
    // users can manually download offline tiles from each location's detail
    // screen via the "Download offline maps" button.

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(20.dp)),
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                createRockScoutMapView(ctx).apply {
                    // Start somewhere reasonable until the LaunchedEffect centers us.
                    controller.setZoom(3.0)
                    controller.setCenter(GeoPoint(39.5, -98.0))

                    overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
                    // Compass repositioned to top-right so it doesn't overlap
                    // the legend and cache indicator at top-left.
                    val embedCompass = CompassOverlay(ctx, this).apply { enableCompass() }
                    overlays.add(embedCompass)
                    post {
                        val d = ctx.resources.displayMetrics.density
                        embedCompass.setCompassCenter(width - 56f * d, 40f * d)
                    }

                    // Tap-to-drop-pin overlay — tapping the map drops a pin
                    // that the user can then download offline maps for.
                    overlays.add(object : org.osmdroid.views.overlay.Overlay() {
                        override fun onSingleTapConfirmed(e: android.view.MotionEvent?, view: MapView?): Boolean {
                            if (e == null || view == null) return false
                            val proj = view.projection
                            val point = proj.fromPixels(e.x.toInt(), e.y.toInt())
                            pinLocation = Pair(point.latitude, point.longitude)
                            view.overlays.removeAll { it is Marker && it.id == "dig_pin_preview" }
                            val marker = Marker(view).apply {
                                id = "dig_pin_preview"
                                position = GeoPoint(point.latitude, point.longitude)
                                title = "Pin location"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            }
                            view.overlays.add(marker)
                            view.invalidate()
                            return true
                        }
                    })

                    mapView = this
                }
            },
            update = { /* handled by LaunchedEffects above */ },
        )

        MapZoomControls(
            onZoomIn = { mapView?.let { it.controller.zoomIn() } },
            onZoomOut = { mapView?.let { it.controller.zoomOut() } },
            onRecenter = {},
            showUser = showUser,
            onSatellite = { toggleSatelliteView(mapView) },
            mapView = mapView,
            showRemovePin = pinLocation != null,
            onRemovePin = {
                pinLocation = null
                mapView?.overlays?.removeAll { it is Marker && it.id == "dig_pin_preview" }
                mapView?.invalidate()
            },
            parkingState = if (parkingSpot != null) ParkingButtonState.HasSpot else ParkingButtonState.NoSpot,
            onParkHere = {
                if (userLat == 0.0 && userLng == 0.0) {
                    android.widget.Toast.makeText(
                        context,
                        "Location not available — enable location monitoring to save your parking spot.",
                        android.widget.Toast.LENGTH_LONG,
                    ).show()
                    return@MapZoomControls
                }
                PersistenceManager.saveParkingSpot(userLat, userLng)
                parkingSpot = PersistenceManager.loadParkingSpot()
                scope.launch(Dispatchers.IO) {
                    MapTileCacheManager.prefetchParkingSpot(context, userLat, userLng)
                }
                android.widget.Toast.makeText(
                    context,
                    "Parking spot saved. Satellite tiles cached — you can find your way back even without signal.",
                    android.widget.Toast.LENGTH_LONG,
                ).show()
            },
            onClearParking = { showClearParkingConfirm = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        )

        MapExpandButton(
            onClick = { isFullscreen = true },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
        )

        MapLegend(
            showGemShows = showGemShows,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
        )

        MapOfflineNotice(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
        )

        // Bottom-left: offline download button only; the pin-drop hint lives below the map.
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SculptedIconButton(
                icon = Icons.Filled.Download,
                contentDescription = "Download offline maps",
                onClick = {
                    val coords = pinLocation ?: mapView?.let {
                        Pair(it.mapCenter.latitude, it.mapCenter.longitude)
                    } ?: Pair(39.5, -98.0)
                    pinLocation = coords
                    showDownloadSheet = true
                },
                accent = Citrine,
                iconTint = Aqua,
                backgroundColor = Slate800,
                size = 44.dp,
                shadowElevation = 5.dp,
            )
        }
    }

    // Pin-drop hint placed below the map so it never overlays the tile area.
    if (pinLocation == null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xE6000000))
                .glowingBorder(2.dp, Warning.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Filled.LocationOn,
                contentDescription = null,
                tint = Warning,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Tap the map to drop a pin, then download offline maps for that area.",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
            )
        }
    }
}

    pinLocation?.let { loc ->
        if (showDownloadSheet) {
            MapDownloadSheet(
                lat = loc.first,
                lng = loc.second,
                onDismiss = { showDownloadSheet = false },
            )
        }
    }

    if (isFullscreen) {
        FullscreenMapOverlay(
            onDismiss = { isFullscreen = false },
            initialCenter = mapView?.let {
                GeoPoint(it.mapCenter.latitude, it.mapCenter.longitude)
            } ?: GeoPoint(39.5, -98.0),
            initialZoom = mapView?.zoomLevelDouble ?: 4.0,
            showUserLocation = showUser,
            onMapReady = { fsMv ->
                // Build markers off the main thread, then mutate fsMv.overlays on
                // the main thread. osmdroid's overlay list is a non-synchronized
                // ArrayList, so mutating it from a background coroutine while the
                // MapView is being detached (when the fullscreen dialog is
                // dismissed) races and crashes the app with
                // ConcurrentModificationException / NPE.
                scope.launch(Dispatchers.IO) {
                    val digMarkers = locations.filter { it.latitude != 0.0 || it.longitude != 0.0 }
                        .map { dloc ->
                            DigMarker(fsMv, dloc, onLocationTap).apply { id = "dig_fs_${dloc.id}" }
                        }
                    val gemMarkers = gemShows.map { show ->
                        GemShowMarker(fsMv, show).apply { id = "gemshow_fs_${show.id}" }
                    }
                    withContext(Dispatchers.Main) {
                        digMarkers.forEach { fsMv.overlays.add(it) }
                        gemMarkers.forEach { fsMv.overlays.add(it) }
                        fsMv.invalidate()
                    }
                }
            },
        )
    }

    if (showClearParkingConfirm) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showClearParkingConfirm = false },
            title = { androidx.compose.material3.Text("Clear parking spot?", style = MaterialTheme.typography.headlineSmall) },
            text = { androidx.compose.material3.Text("Remove your saved parking spot and its cached map tiles?") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    PersistenceManager.clearParkingSpot()
                    parkingSpot = null
                    showClearParkingConfirm = false
                    android.widget.Toast.makeText(
                        context,
                        "Parking spot cleared.",
                        android.widget.Toast.LENGTH_SHORT,
                    ).show()
                }) { androidx.compose.material3.Text("Clear", color = Warning, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showClearParkingConfirm = false }) {
                    androidx.compose.material3.Text("Cancel", color = DarkTextMid)
                }
            },
        )
    }

    MapViewLifecycleEffect(mapView)
}

private val legendEntries: List<Pair<String, Color>>
    get() = LocationType.entries.map { "${it.emoji} ${it.label}" to it.mapColor } + ("\uD83D\uDDC3\uFE0F Gem Shows" to Amethyst)

@Composable
private fun MapLegend(showGemShows: Boolean, modifier: Modifier = Modifier) {
    val entries = remember(showGemShows) {
        if (showGemShows) legendEntries else legendEntries.filter { !it.first.contains("Gem Shows") }
    }

    Column(
        modifier = modifier
            .sculpted(shape = RoundedCornerShape(12.dp), accent = Color.White, shadowElevation = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1C1A14).copy(alpha = 0.95f))
            .glowingBorder(2.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            "Location Types",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold,
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(entries) { (label, color) ->
                LegendChip(label = label, color = color)
            }
        }
    }
}

@Composable
private fun LegendChip(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .sculpted(shape = RoundedCornerShape(8.dp), accent = color, shadowElevation = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .glowingBorder(2.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color)
                .glowingBorder(2.dp, Color.White.copy(alpha = 0.4f), CircleShape),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/**
 * Process-wide cache of marker icon bitmaps. Marker icons only depend on the
 * location type (or are constant, for gem shows and parking), so a small
 * cache keyed by density + type avoids re-allocating a fresh bitmap for every
 * pin on every map refresh — each DigMarker previously called [createPinIcon]
 * in its init block, producing hundreds of short-lived bitmaps across the Dig
 * Sites map, fullscreen map, and other osmdroid map screens. Cached bitmaps
 * are wrapped in [BitmapDrawable]s (which don't mutate the underlying bitmap)
 * so the same bitmap can back many markers safely.
 */
private val markerIconCache = java.util.concurrent.ConcurrentHashMap<String, android.graphics.Bitmap>()

/** Build a cache key that includes density so a fresh process on a new display
 *  metric doesn't reuse an undersized cached bitmap. */
private fun iconCacheKey(kind: String, density: Float, extra: String = ""): String =
    "${kind}_${density}_$extra"

/**
 * A marker pin for a DigLocation. Uses osmdroid's built-in anchor handling so taps land cleanly
 * on the pin, not the area around it.
 */
private class DigMarker(
    mapView: MapView,
    private val location: DigLocation,
    private val onTap: (DigLocation) -> Unit,
) : Marker(mapView) {
    init {
        position = GeoPoint(location.latitude, location.longitude)
        title = location.name
        snippet = location.region
        setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM)
        icon = createPinIcon(mapView.context, location.type)
        // Route taps to the app's detail screen.
        setOnMarkerClickListener { _, _ ->
            onTap(location)
            true
        }
    }
}

/**
 * A marker pin for a GemShow. Uses a distinct diamond/star shape in Amethyst
 * purple so gem shows stand out from dig-site teardrops. Tapping opens the
 * show's website in the system browser.
 */
private class GemShowMarker(
    mapView: MapView,
    private val show: GemShow,
) : Marker(mapView) {
    init {
        position = GeoPoint(show.latitude, show.longitude)
        title = show.name
        snippet = "${show.city}, ${show.state} • ${show.monthLabel}"
        setAnchor(ANCHOR_CENTER, ANCHOR_CENTER)
        icon = createGemShowIcon(mapView.context)
        setOnMarkerClickListener { _, _ ->
            SafeLinkOpener.openUrl(mapView.context, show.website)
            true
        }
    }
}

/**
 * Build a purple diamond-shaped icon for gem show markers with a white star center.
 */
private fun createGemShowIcon(
    context: android.content.Context,
): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val key = iconCacheKey("gemshow", density)
    val bmp = markerIconCache.getOrPut(key) {
        buildGemShowBitmap(density)
    }
    return android.graphics.drawable.BitmapDrawable(context.resources, bmp)
}

/** Builds the gem-show diamond bitmap. Split out so the cached path never
 *  re-allocates — the cache stores the raw [android.graphics.Bitmap]. */
private fun buildGemShowBitmap(density: Float): android.graphics.Bitmap {
    val sizePx = (30 * density).toInt().coerceAtLeast(30)
    val bmp = android.graphics.Bitmap.createBitmap(sizePx, sizePx, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bmp)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
    val cx = sizePx / 2f
    val cy = sizePx / 2f
    val half = sizePx / 3f

    // Diamond shape.
    paint.color = Amethyst.toArgb()
    val path = android.graphics.Path().apply {
        moveTo(cx, cy - half)
        lineTo(cx + half, cy)
        lineTo(cx, cy + half)
        lineTo(cx - half, cy)
        close()
    }
    canvas.drawPath(path, paint)

    // White border.
    paint.color = android.graphics.Color.WHITE
    paint.style = android.graphics.Paint.Style.STROKE
    paint.strokeWidth = 2.5f * density
    canvas.drawPath(path, paint)

    // White center dot.
    paint.style = android.graphics.Paint.Style.FILL
    canvas.drawCircle(cx, cy, half * 0.35f, paint)

    return bmp
}

/**
 * Build a car silhouette icon for the parking spot marker — Citrine yellow
 * with white outline, visually distinct from dig site pins and gem show markers.
 */
private fun createParkingIcon(
    context: android.content.Context,
): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val key = iconCacheKey("parking", density)
    val bmp = markerIconCache.getOrPut(key) {
        buildParkingBitmap(density)
    }
    return android.graphics.drawable.BitmapDrawable(context.resources, bmp)
}

/** Builds the parking-car bitmap. Split out so the cached path never
 *  re-allocates. */
private fun buildParkingBitmap(density: Float): android.graphics.Bitmap {
    val sizePx = (32 * density).toInt().coerceAtLeast(32)
    val bmp = android.graphics.Bitmap.createBitmap(sizePx, sizePx, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bmp)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
    val cx = sizePx / 2f
    val cy = sizePx / 2f

    // Car body — rounded rectangle in Citrine yellow.
    paint.color = Citrine.toArgb()
    val bodyW = sizePx * 0.62f
    val bodyH = sizePx * 0.40f
    val left = cx - bodyW / 2f
    val top = cy - bodyH / 2f
    val rect = android.graphics.RectF(left, top, left + bodyW, top + bodyH)
    canvas.drawRoundRect(rect, 6f * density, 6f * density, paint)

    // Car roof — smaller rounded rect on top.
    val roofW = bodyW * 0.55f
    val roofH = bodyH * 0.50f
    val roofLeft = cx - roofW / 2f
    val roofTop = top - roofH * 0.45f
    val roofRect = android.graphics.RectF(roofLeft, roofTop, roofLeft + roofW, roofTop + roofH)
    canvas.drawRoundRect(roofRect, 4f * density, 4f * density, paint)

    // White outline on body.
    paint.color = android.graphics.Color.WHITE
    paint.style = android.graphics.Paint.Style.STROKE
    paint.strokeWidth = 2f * density
    canvas.drawRoundRect(rect, 6f * density, 6f * density, paint)
    canvas.drawRoundRect(roofRect, 4f * density, 4f * density, paint)

    // Wheels — two black circles at the bottom.
    paint.style = android.graphics.Paint.Style.FILL
    paint.color = android.graphics.Color.BLACK
    val wheelR = sizePx * 0.08f
    canvas.drawCircle(left + bodyW * 0.22f, top + bodyH + wheelR * 0.3f, wheelR, paint)
    canvas.drawCircle(left + bodyW * 0.78f, top + bodyH + wheelR * 0.3f, wheelR, paint)

    // White hubcaps.
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(left + bodyW * 0.22f, top + bodyH + wheelR * 0.3f, wheelR * 0.45f, paint)
    canvas.drawCircle(left + bodyW * 0.78f, top + bodyH + wheelR * 0.3f, wheelR * 0.45f, paint)

    return bmp
}

/**
 * Build a colored teardrop pin Drawable with the location-type color.
 */
private fun createPinIcon(
    context: android.content.Context,
    type: LocationType,
): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val key = iconCacheKey("pin", density, type.name)
    val bmp = markerIconCache.getOrPut(key) {
        buildPinBitmap(density, type)
    }
    return android.graphics.drawable.BitmapDrawable(context.resources, bmp)
}

/** Builds the teardrop pin bitmap. Split out so the cached path never
 *  re-allocates — pin icons only vary by [LocationType] (9 types), so the
 *  cache holds at most 9 bitmaps per density for the whole process. */
private fun buildPinBitmap(density: Float, type: LocationType): android.graphics.Bitmap {
    val sizePx = (28 * density).toInt().coerceAtLeast(28)
    val bmp = android.graphics.Bitmap.createBitmap(sizePx, sizePx, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bmp)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
    val color = type.mapColor.toArgb()

    // Teardrop: circle + triangle tail.
    val cx = sizePx / 2f
    val cy = sizePx / 2f
    val r = sizePx / 3f
    paint.color = color
    val path = android.graphics.Path().apply {
        moveTo(cx, sizePx.toFloat())
        lineTo(cx - r * 0.9f, cy + r * 0.4f)
        lineTo(cx + r * 0.9f, cy + r * 0.4f)
        close()
    }
    canvas.drawPath(path, paint)
    canvas.drawCircle(cx, cy, r, paint)

    // White inner ring + dot for contrast.
    paint.color = android.graphics.Color.WHITE
    paint.style = android.graphics.Paint.Style.STROKE
    paint.strokeWidth = 2.5f * density
    canvas.drawCircle(cx, cy, r, paint)
    paint.style = android.graphics.Paint.Style.FILL
    canvas.drawCircle(cx, cy, r * 0.35f, paint)

    return bmp
}

/** Pin marker color per location type. */
private val LocationType.mapColor: Color
    get() = when (this) {
        LocationType.PUBLIC_DIG -> Citrine
        LocationType.MINE -> Color(0xFFE2574C)
        LocationType.QUARRY -> Color(0xFF8D6E63)
        LocationType.BEACH -> Aqua
        LocationType.RIVER -> Color(0xFF42A5F5)
        LocationType.DESERT -> Warning
        LocationType.ROCK_SHOP -> Amethyst
        LocationType.METAPHYSICAL -> Color(0xFFF48FB1)
        LocationType.LAPIDARY_CLUB -> Color(0xFF26A69A)
    }

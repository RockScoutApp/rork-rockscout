package com.rork.rockscout.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.activity.compose.BackHandler
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AuroraRepository
import com.rork.rockscout.data.AuroraSavedSpot
import com.rork.rockscout.data.LocationFetcher
import com.rork.rockscout.ui.components.MapExpandButton
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay

private val AuroraGreen = Color(0xFF00E5C9)
private val AuroraPurple = Color(0xFF9B7BD8)
private val AuroraDarkBg = Color(0xFF0D0C08)

/** Aurora borealis background image for the Saved Spots section header. */
const val AURORA_SAVED_SPOTS_BG_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/30fcfec0-d83d-4c93-a550-12f73fa7de62.png"

/**
 * Aurora-themed pin-drop map for tracking aurora visibility at bookmarked coordinates.
 * Modeled on SpecimenMarkerMap but with aurora markers, popup, and add-spot form.
 *
 * @param spots List of saved aurora spots to display as pins.
 * @param onAddSpot Called when the user submits a new spot (name, lat, lng).
 * @param onRemoveSpot Called when the user deletes a saved spot.
 * @param currentKp Current Kp index for computing visibility at each spot.
 * @param modifier Layout modifier.
 */
@Composable
fun AuroraSavedSpotsMap(
    spots: List<AuroraSavedSpot>,
    onAddSpot: (String, Double, Double) -> Unit,
    onRemoveSpot: (String) -> Unit,
    currentKp: Double,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var spotName by remember { mutableStateOf("") }
    var spotLat by remember { mutableStateOf("") }
    var spotLng by remember { mutableStateOf("") }
    var isAddingSpot by remember { mutableStateOf(false) }
    var selectedSpot by remember { mutableStateOf<AuroraSavedSpot?>(null) }
    var isFullscreen by remember { mutableStateOf(false) }
    var fullscreenCenter by remember { mutableStateOf(GeoPoint(60.0, -100.0)) }
    var fullscreenZoom by remember { mutableStateOf(3.0) }

    // Back button dismisses the add-spot form or selected-spot popup instead
    // of exiting the entire screen.
    BackHandler(enabled = isAddingSpot || selectedSpot != null) {
        if (isAddingSpot) {
            isAddingSpot = false
            spotName = ""
            spotLat = ""
            spotLng = ""
        } else {
            selectedSpot = null
        }
    }

    // Update markers when spots change.
    // Marker creation + zoomToBoundingBox are deferred to after the map has
    // been laid out and run on a background dispatcher to avoid blocking the
    // main thread (which caused ANRs/freezes when adding a spot).
    LaunchedEffect(mapView, spots) {
        val mv = mapView ?: return@LaunchedEffect

        // Wait for the map view to complete its current layout pass before
        // manipulating it — especially important when the spot count changes
        // and the map height animates from 200dp to 300dp.
        val layoutReady = CompletableDeferred<Unit>()
        mv.post { layoutReady.complete(Unit) }
        layoutReady.await()

        withContext(Dispatchers.IO) {
            runCatching {
                mv.overlays.removeAll { it is Marker && it.id?.startsWith("aurora_pin_") == true }

                if (spots.isEmpty()) return@runCatching

                val points = mutableListOf<GeoPoint>()
                spots.forEach { spot ->
                    val point = GeoPoint(spot.latitude, spot.longitude)
                    points.add(point)
                    val threshold = AuroraRepository.kpThresholdForLatitude(spot.latitude)
                    val isVisible = currentKp >= threshold

                    val marker = object : Marker(mv) {
                        init {
                            position = point
                            title = spot.name
                            snippet = if (isVisible) "Aurora VISIBLE (Kp $currentKp ≥ $threshold)" else "Aurora unlikely (Kp $currentKp < $threshold)"
                            id = "aurora_pin_${spot.id}"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            icon = createAuroraPinDrawable(mv.context, isVisible)
                            setOnMarkerClickListener { _, _ ->
                                selectedSpot = spot
                                true
                            }
                        }
                    }
                    mv.overlays.add(marker)
                }

                if (points.isNotEmpty()) {
                    if (points.size == 1) {
                        // Single point — degenerate bounding box crashes osmdroid.
                        // Just animate to the point at a reasonable zoom instead.
                        mv.controller.animateTo(points.first())
                        mv.controller.setZoom(8.0)
                    } else {
                        val box = org.osmdroid.util.BoundingBox.fromGeoPoints(points)
                        // Pad the box slightly so it's never zero-area even for
                        // very close points.
                        val padded = box.increaseByScale(1.2f)
                        mv.zoomToBoundingBox(padded, false, 48)
                    }
                }
                mv.invalidate()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding(),
    ) {
        // Section header with aurora background image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp)),
        ) {
            AsyncImage(
                model = AURORA_SAVED_SPOTS_BG_URL,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color.Transparent, AuroraDarkBg.copy(alpha = 0.7f))
                        )
                    ),
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
            ) {
                Text(
                    text = "Saved Spots",
                    style = MaterialTheme.typography.titleLarge,
                    color = AuroraGreen,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Track aurora visibility at your favorite locations",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Map view
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (spots.isEmpty()) 200.dp else 300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(AuroraDarkBg),
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    createRockScoutMapView(ctx, readOnly = false).apply {
                        controller.setZoom(3.0)
                        controller.setCenter(GeoPoint(60.0, -100.0))
                        overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
                        overlays.add(CompassOverlay(ctx, this).apply { enableCompass() })
                        // Tap-to-drop-pin: tapping the map pre-fills lat/lng form fields
                        overlays.add(object : org.osmdroid.views.overlay.Overlay() {
                            override fun onSingleTapConfirmed(e: android.view.MotionEvent, mv: MapView): Boolean {
                                val proj = mv.projection
                                val point = proj.fromPixels(e.x.toInt(), e.y.toInt())
                                spotLat = String.format("%.4f", point.latitude)
                                spotLng = String.format("%.4f", point.longitude)
                                if (spotName.isBlank()) spotName = "Dropped Pin"
                                isAddingSpot = true
                                return true
                            }
                        })
                        mapView = this
                    }
                },
                update = { /* no-op */ },
            )

            MapZoomControls(
                onZoomIn = { mapView?.controller?.zoomIn() },
                onZoomOut = { mapView?.controller?.zoomOut() },
                onRecenter = {},
                showUser = false,
                onSatellite = { toggleSatelliteView(mapView) },
                compact = true,
                mapView = mapView,
                modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
            )

            // Expand-to-fullscreen button — top-right corner.
            MapExpandButton(
                onClick = {
                    mapView?.let {
                        fullscreenCenter = GeoPoint(it.mapCenter.latitude, it.mapCenter.longitude)
                        fullscreenZoom = it.zoomLevelDouble
                    }
                    isFullscreen = true
                },
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
            )
        }

        // Empty-state hint (below the map, not overlapping it)
        if (spots.isEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "No saved spots yet — tap the map to drop a pin or enter coordinates below to track aurora visibility",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )
        }

        Spacer(Modifier.height(12.dp))

        // Add spot form
        if (isAddingSpot) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AuroraDarkBg.copy(alpha = 0.9f))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Add Aurora Spot",
                    style = MaterialTheme.typography.titleMedium,
                    color = AuroraGreen,
                    fontWeight = FontWeight.Bold,
                )
                OutlinedTextField(
                    value = spotName,
                    onValueChange = { spotName = it },
                    label = { Text("Spot name", color = AuroraGreen.copy(alpha = 0.7f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AuroraGreen,
                        unfocusedTextColor = AuroraGreen,
                        cursorColor = AuroraGreen,
                        focusedBorderColor = AuroraGreen,
                        unfocusedBorderColor = AuroraGreen.copy(alpha = 0.4f),
                        focusedLabelColor = AuroraGreen,
                        unfocusedLabelColor = AuroraGreen.copy(alpha = 0.5f),
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = spotLat,
                        onValueChange = { spotLat = it },
                        label = { Text("Latitude", color = AuroraGreen.copy(alpha = 0.7f)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AuroraGreen,
                            unfocusedTextColor = AuroraGreen,
                            cursorColor = AuroraGreen,
                            focusedBorderColor = AuroraGreen,
                            unfocusedBorderColor = AuroraGreen.copy(alpha = 0.4f),
                            focusedLabelColor = AuroraGreen,
                            unfocusedLabelColor = AuroraGreen.copy(alpha = 0.5f),
                        ),
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = spotLng,
                        onValueChange = { spotLng = it },
                        label = { Text("Longitude", color = AuroraGreen.copy(alpha = 0.7f)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AuroraGreen,
                            unfocusedTextColor = AuroraGreen,
                            cursorColor = AuroraGreen,
                            focusedBorderColor = AuroraGreen,
                            unfocusedBorderColor = AuroraGreen.copy(alpha = 0.4f),
                            focusedLabelColor = AuroraGreen,
                            unfocusedLabelColor = AuroraGreen.copy(alpha = 0.5f),
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = {
                            val lat = spotLat.toDoubleOrNull()
                            val lng = spotLng.toDoubleOrNull()
                            val name = spotName.ifBlank { "Spot ${spots.size + 1}" }
                            if (lat != null && lng != null && lat in -90.0..90.0 && lng in -180.0..180.0) {
                                onAddSpot(name, lat, lng)
                                spotName = ""
                                spotLat = ""
                                spotLng = ""
                                isAddingSpot = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AuroraGreen, contentColor = AuroraDarkBg),
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Save Spot")
                    }
                    Button(
                        onClick = {
                            isAddingSpot = false
                            spotName = ""
                            spotLat = ""
                            spotLng = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuroraDarkBg,
                            contentColor = AuroraGreen,
                        ),
                    ) {
                        Text("Cancel")
                    }
                }
                // Mark My Location button
                Button(
                    onClick = {
                        scope.launch {
                            val loc = withContext(Dispatchers.IO) { LocationFetcher.fetch(context) }
                            if (loc != null) {
                                spotLat = String.format("%.4f", loc.latitude)
                                spotLng = String.format("%.4f", loc.longitude)
                                if (spotName.isBlank()) spotName = "My Location"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AuroraPurple.copy(alpha = 0.3f),
                        contentColor = AuroraPurple,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Filled.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Mark My Location (GPS)")
                }
            }
        } else {
            Button(
                onClick = { isAddingSpot = true },
                colors = ButtonDefaults.buttonColors(containerColor = AuroraGreen.copy(alpha = 0.2f), contentColor = AuroraGreen),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Add New Spot")
            }
        }

        Spacer(Modifier.height(12.dp))

        // Spot list
        spots.forEach { spot ->
            val threshold = AuroraRepository.kpThresholdForLatitude(spot.latitude)
            val isVisible = currentKp >= threshold
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AuroraDarkBg.copy(alpha = 0.9f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = if (isVisible) AuroraGreen else AuroraPurple,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = spot.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = AuroraGreen,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = String.format("%.4f, %.4f", spot.latitude, spot.longitude),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f),
                    )
                    Text(
                        text = if (isVisible) "Aurora visible (Kp $currentKp ≥ ${String.format("%.1f", threshold)})" else "Aurora unlikely (Kp $currentKp < ${String.format("%.1f", threshold)})",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isVisible) AuroraGreen else Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onRemoveSpot(spot.id) },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color(0xFFFF3B30), modifier = Modifier.size(18.dp))
                }
            }
            Spacer(Modifier.height(6.dp))
        }
    }

    // Manage the MapView lifecycle — without this, onResume/onPause/onDetach
    // are never called, which causes freezes and crashes when interacting with
    // the map (especially when adding spots triggers marker updates).
    MapViewLifecycleEffect(mapView)

    if (isFullscreen) {
        FullscreenAuroraSpotsOverlay(
            spots = spots,
            onDismiss = { isFullscreen = false },
            initialCenter = fullscreenCenter,
            initialZoom = fullscreenZoom,
            currentKp = currentKp,
            onAddSpot = onAddSpot,
            onRemoveSpot = onRemoveSpot,
        )
    }
}

/**
 * Full-screen overlay for the Aurora Saved Spots map. Renders all saved spot
 * pins with the same tap-to-add-spot flow, a close button in the top-left, and
 * the compass repositioned to the top-right so it never overlaps the close
 * button. Tapping the map pre-fills the add-spot form fields.
 */
@Composable
private fun FullscreenAuroraSpotsOverlay(
    spots: List<AuroraSavedSpot>,
    onDismiss: () -> Unit,
    initialCenter: GeoPoint,
    initialZoom: Double,
    currentKp: Double,
    onAddSpot: (String, Double, Double) -> Unit,
    onRemoveSpot: (String) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var fsMapView by remember { mutableStateOf<MapView?>(null) }
    var spotName by remember { mutableStateOf("") }
    var spotLat by remember { mutableStateOf("") }
    var spotLng by remember { mutableStateOf("") }
    var isAddingSpot by remember { mutableStateOf(false) }
    var selectedSpot by remember { mutableStateOf<AuroraSavedSpot?>(null) }

    BackHandler(enabled = isAddingSpot || selectedSpot != null) {
        if (isAddingSpot) {
            isAddingSpot = false
            spotName = ""
            spotLat = ""
            spotLng = ""
        } else {
            selectedSpot = null
        }
    }

    // Update markers when spots change.
    LaunchedEffect(fsMapView, spots) {
        val mv = fsMapView ?: return@LaunchedEffect
        val layoutReady = CompletableDeferred<Unit>()
        mv.post { layoutReady.complete(Unit) }
        layoutReady.await()
        withContext(Dispatchers.IO) {
            runCatching {
                mv.overlays.removeAll { it is Marker && it.id?.startsWith("aurora_pin_fs_") == true }
                if (spots.isEmpty()) return@runCatching
                val points = mutableListOf<GeoPoint>()
                spots.forEach { spot ->
                    val point = GeoPoint(spot.latitude, spot.longitude)
                    points.add(point)
                    val threshold = AuroraRepository.kpThresholdForLatitude(spot.latitude)
                    val isVisible = currentKp >= threshold
                    val marker = object : Marker(mv) {
                        init {
                            position = point
                            title = spot.name
                            snippet = if (isVisible) "Aurora VISIBLE (Kp $currentKp ≥ $threshold)" else "Aurora unlikely (Kp $currentKp < $threshold)"
                            id = "aurora_pin_fs_${spot.id}"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            icon = createAuroraPinDrawable(mv.context, isVisible)
                            setOnMarkerClickListener { _, _ ->
                                selectedSpot = spot
                                true
                            }
                        }
                    }
                    mv.overlays.add(marker)
                }
                if (points.isNotEmpty()) {
                    if (points.size == 1) {
                        mv.controller.animateTo(points.first())
                        mv.controller.setZoom(8.0)
                    } else {
                        val box = org.osmdroid.util.BoundingBox.fromGeoPoints(points)
                        val padded = box.increaseByScale(1.2f)
                        mv.zoomToBoundingBox(padded, false, 48)
                    }
                }
                mv.invalidate()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    createRockScoutMapView(ctx, readOnly = false).apply {
                        controller.setCenter(initialCenter)
                        controller.setZoom(initialZoom)
                        overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
                        // Compass moved to the top-right so the close button
                        // (top-left) doesn't overlap it.
                        val fsCompass = CompassOverlay(ctx, this).apply { enableCompass() }
                        overlays.add(fsCompass)
                        post {
                            val d = ctx.resources.displayMetrics.density
                            fsCompass.setCompassCenter(width - 56f * d, 40f * d)
                        }
                        // Tap-to-drop-pin: tapping the map pre-fills lat/lng form fields.
                        overlays.add(object : org.osmdroid.views.overlay.Overlay() {
                            override fun onSingleTapConfirmed(e: android.view.MotionEvent, mv: MapView): Boolean {
                                val proj = mv.projection
                                val point = proj.fromPixels(e.x.toInt(), e.y.toInt())
                                spotLat = String.format("%.4f", point.latitude)
                                spotLng = String.format("%.4f", point.longitude)
                                if (spotName.isBlank()) spotName = "Dropped Pin"
                                isAddingSpot = true
                                return true
                            }
                        })
                        fsMapView = this
                    }
                },
                update = { /* no-op */ },
            )

            // Close button — top-left corner.
            androidx.compose.material3.IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
            ) {
                Icon(Icons.Filled.Close, contentDescription = "Close full screen map", tint = Color.White)
            }

            // Zoom controls — bottom-right.
            MapZoomControls(
                onZoomIn = { fsMapView?.controller?.zoomIn() },
                onZoomOut = { fsMapView?.controller?.zoomOut() },
                onRecenter = {},
                showUser = false,
                onSatellite = { toggleSatelliteView(fsMapView) },
                compact = false,
                mapView = fsMapView,
                modifier = Modifier.align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .navigationBarsPadding(),
            )

            MapOfflineNotice(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 64.dp),
            )

            // Add-spot form — slides in from the bottom when the user taps the map.
            if (isAddingSpot) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(AuroraDarkBg.copy(alpha = 0.95f))
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "Add Aurora Spot",
                        style = MaterialTheme.typography.titleMedium,
                        color = AuroraGreen,
                        fontWeight = FontWeight.Bold,
                    )
                    OutlinedTextField(
                        value = spotName,
                        onValueChange = { spotName = it },
                        label = { Text("Spot name", color = AuroraGreen.copy(alpha = 0.7f)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AuroraGreen,
                            unfocusedTextColor = AuroraGreen,
                            cursorColor = AuroraGreen,
                            focusedBorderColor = AuroraGreen,
                            unfocusedBorderColor = AuroraGreen.copy(alpha = 0.4f),
                            focusedLabelColor = AuroraGreen,
                            unfocusedLabelColor = AuroraGreen.copy(alpha = 0.5f),
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = spotLat,
                            onValueChange = { spotLat = it },
                            label = { Text("Latitude", color = AuroraGreen.copy(alpha = 0.7f)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = AuroraGreen,
                                unfocusedTextColor = AuroraGreen,
                                cursorColor = AuroraGreen,
                                focusedBorderColor = AuroraGreen,
                                unfocusedBorderColor = AuroraGreen.copy(alpha = 0.4f),
                                focusedLabelColor = AuroraGreen,
                                unfocusedLabelColor = AuroraGreen.copy(alpha = 0.5f),
                            ),
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedTextField(
                            value = spotLng,
                            onValueChange = { spotLng = it },
                            label = { Text("Longitude", color = AuroraGreen.copy(alpha = 0.7f)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = AuroraGreen,
                                unfocusedTextColor = AuroraGreen,
                                cursorColor = AuroraGreen,
                                focusedBorderColor = AuroraGreen,
                                unfocusedBorderColor = AuroraGreen.copy(alpha = 0.4f),
                                focusedLabelColor = AuroraGreen,
                                unfocusedLabelColor = AuroraGreen.copy(alpha = 0.5f),
                            ),
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Button(
                            onClick = {
                                val lat = spotLat.toDoubleOrNull()
                                val lng = spotLng.toDoubleOrNull()
                                val name = spotName.ifBlank { "Spot ${spots.size + 1}" }
                                if (lat != null && lng != null && lat in -90.0..90.0 && lng in -180.0..180.0) {
                                    onAddSpot(name, lat, lng)
                                    spotName = ""
                                    spotLat = ""
                                    spotLng = ""
                                    isAddingSpot = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AuroraGreen, contentColor = AuroraDarkBg),
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Save Spot")
                        }
                        Button(
                            onClick = {
                                isAddingSpot = false
                                spotName = ""
                                spotLat = ""
                                spotLng = ""
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AuroraDarkBg,
                                contentColor = AuroraGreen,
                            ),
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }

        MapViewLifecycleEffect(fsMapView)
    }
}

/** Create an aurora-themed teardrop pin drawable. */
private fun createAuroraPinDrawable(context: android.content.Context, isVisible: Boolean): android.graphics.drawable.Drawable {
    val color = if (isVisible) AuroraGreen.toArgb() else AuroraPurple.toArgb()
    val size = 80
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Draw teardrop shape
    val path = Path().apply {
        moveTo(size / 2f, size * 0.9f)
        cubicTo(size * 0.1f, size * 0.5f, size * 0.1f, size * 0.15f, size / 2f, size * 0.15f)
        cubicTo(size * 0.9f, size * 0.15f, size * 0.9f, size * 0.5f, size / 2f, size * 0.9f)
        close()
    }
    paint.color = color
    canvas.drawPath(path, paint)

    // Draw center circle (moon/star)
    paint.color = android.graphics.Color.WHITE
    paint.textSize = 32f
    paint.textAlign = Paint.Align.CENTER
    val emoji = if (isVisible) "✨" else "🌙"
    canvas.drawText(emoji, size / 2f, size * 0.42f, paint)

    return BitmapDrawable(context.resources, bitmap)
}

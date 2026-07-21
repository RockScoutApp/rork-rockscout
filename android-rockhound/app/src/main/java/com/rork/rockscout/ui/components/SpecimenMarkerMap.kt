package com.rork.rockscout.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.AsyncImage
import com.rork.rockscout.data.CapturedPhoto
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.Slate800
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * View-only full-page map showing specimen pins for every field capture that has
 * lat/lng coordinates. Tapping a pin opens a read-only popup showing the field
 * capture card (photo, name, location, description, date).
 *
 * This map does NOT offer Set Pin / Remove Pin controls — it is strictly view-only.
 * Uses the same RockScoutMap base (zoom controls, satellite toggle, offline notice).
 *
 * @param captures list of captures with coordinates; only those with non-null
 *                 latitude/longitude are shown as pins.
 * @param onPinTapped optional callback when a pin is tapped (in addition to the popup).
 * @param modifier layout modifier.
 */
@Composable
fun SpecimenMarkerMap(
    captures: List<CapturedPhoto>,
    modifier: Modifier = Modifier,
    onPinTapped: ((CapturedPhoto) -> Unit)? = null,
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var clusterOverlay by remember { mutableStateOf<SpecimenClusterOverlay?>(null) }
    var isFullscreen by remember { mutableStateOf(false) }
    var fullscreenCenter by remember { mutableStateOf(GeoPoint(39.5, -98.0)) }
    var fullscreenZoom by remember { mutableStateOf(4.0) }
    var selectedCapture by remember { mutableStateOf<CapturedPhoto?>(null) }

    val capturesWithCoords = remember(captures) {
        captures.filter { it.hasCoordinates }
    }

    // Build clustered markers whenever the capture list or map changes.
    // Nearby pins are grouped into count badges so overlapping discoveries in the
    // same region no longer stack on top of each other.
    LaunchedEffect(mapView, capturesWithCoords) {
        val mv = mapView ?: return@LaunchedEffect
        val overlay = clusterOverlay ?: return@LaunchedEffect

        // Drop the previous specimen markers + any legacy individual pin overlays.
        mv.overlays.removeAll { it is Marker && it.id?.startsWith("specimen_pin_") == true }
        overlay.clear()

        if (capturesWithCoords.isEmpty()) return@LaunchedEffect

        val points = mutableListOf<GeoPoint>()
        val markers = ArrayList<Marker>(capturesWithCoords.size)
        capturesWithCoords.forEachIndexed { index, capture ->
            val lat = capture.latitude ?: return@forEachIndexed
            val lng = capture.longitude ?: return@forEachIndexed
            val point = GeoPoint(lat, lng)
            points.add(point)

            val spec = SeedData.specimenById(capture.specimenId)
            val accent = if (spec != null) rockClassColor(spec.rockClass) else Citrine
            val displayName = capture.displayName(spec?.name ?: "Unknown specimen")

            val marker = object : Marker(mv) {
                init {
                    position = point
                    title = displayName
                    snippet = capture.customLocation.ifBlank { "" }
                    id = "specimen_pin_${capture.id}"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = createNumberedPinDrawable(mv.context, index + 1, accent)
                    setOnMarkerClickListener { _, _ ->
                        selectedCapture = capture
                        onPinTapped?.invoke(capture)
                        true
                    }
                }
            }
            markers.add(marker)
        }
        overlay.setItems(markers)

        if (points.isNotEmpty()) {
            val box = BoundingBox.fromGeoPoints(points)
            mv.zoomToBoundingBox(box, false, 48)
        }
        mv.invalidate()
    }

    Box(modifier = modifier.clip(RoundedCornerShape(16.dp))) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                createRockScoutMapView(ctx, readOnly = true).apply {
                    controller.setZoom(4.0)
                    controller.setCenter(GeoPoint(39.5, -98.0))
                    overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
                    overlays.add(CompassOverlay(ctx, this).apply { enableCompass() })
                    // Clustering overlay handles all specimen pins — added before
                    // the recenter/zoom button overlays so taps route through it.
                    val clusterer = SpecimenClusterOverlay(this)
                    overlays.add(clusterer)
                    clusterOverlay = clusterer
                    mapView = this
                }
            },
            update = { /* no-op */ },
        )

        MapZoomControls(
            onZoomIn = { mapView?.controller?.zoomIn() },
            onZoomOut = { mapView?.controller?.zoomOut() },
            onRecenter = {
                mapView?.let { mv ->
                    val pts = capturesWithCoords.mapNotNull { c ->
                        if (c.latitude != null && c.longitude != null) GeoPoint(c.latitude, c.longitude) else null
                    }
                    if (pts.isNotEmpty()) {
                        val box = BoundingBox.fromGeoPoints(pts)
                        mv.controller.animateTo(GeoPoint(box.centerLatitude, box.centerLongitude))
                    }
                }
            },
            showUser = false,
            onSatellite = { toggleSatelliteView(mapView) },
            compact = true,
            modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
        )

        MapOfflineNotice(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
        )

        MapExpandButton(
            onClick = {
                mapView?.let {
                    fullscreenCenter = GeoPoint(it.mapCenter.latitude, it.mapCenter.longitude)
                    fullscreenZoom = it.zoomLevelDouble
                }
                isFullscreen = true
            },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
        )

        // Empty state overlay when no captures have coordinates
        if (capturesWithCoords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("\uD83D\uDDCC", style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "No specimen pins yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Drop a pin on the Field Camera save form or the Trip Planner editor to see your specimen markers here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                    )
                }
            }
        }
    }

    // Fullscreen overlay — view-only specimen map
    if (isFullscreen) {
        FullscreenSpecimenMapOverlay(
            captures = capturesWithCoords,
            onDismiss = { isFullscreen = false },
            initialCenter = fullscreenCenter,
            initialZoom = fullscreenZoom,
            onPinTapped = { capture -> selectedCapture = capture },
        )
    }

    // Read-only capture card popup
    selectedCapture?.let { capture ->
        ReadOnlyCapturePopup(
            capture = capture,
            onDismiss = { selectedCapture = null },
        )
    }

    MapViewLifecycleEffect(mapView)
}

/**
 * Fullscreen view-only specimen map overlay.
 * Shows all specimen pins, tapping one opens the read-only popup.
 */
@Composable
private fun FullscreenSpecimenMapOverlay(
    captures: List<CapturedPhoto>,
    onDismiss: () -> Unit,
    initialCenter: GeoPoint,
    initialZoom: Double,
    onPinTapped: (CapturedPhoto) -> Unit,
) {
    var fsMapView by remember { mutableStateOf<MapView?>(null) }
    var fsClusterOverlay by remember { mutableStateOf<SpecimenClusterOverlay?>(null) }

    LaunchedEffect(fsMapView, captures) {
        val mv = fsMapView ?: return@LaunchedEffect
        val overlay = fsClusterOverlay ?: return@LaunchedEffect
        mv.overlays.removeAll { it is Marker && it.id?.startsWith("specimen_pin_fs_") == true }
        overlay.clear()
        if (captures.isEmpty()) return@LaunchedEffect

        val points = mutableListOf<GeoPoint>()
        val markers = ArrayList<Marker>(captures.size)
        captures.forEachIndexed { index, capture ->
            val lat = capture.latitude ?: return@forEachIndexed
            val lng = capture.longitude ?: return@forEachIndexed
            val point = GeoPoint(lat, lng)
            points.add(point)
            val spec = SeedData.specimenById(capture.specimenId)
            val accent = if (spec != null) rockClassColor(spec.rockClass) else Citrine
            val displayName = capture.displayName(spec?.name ?: "Unknown specimen")
            val marker = object : Marker(mv) {
                init {
                    position = point
                    title = displayName
                    id = "specimen_pin_fs_${capture.id}"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = createNumberedPinDrawable(mv.context, index + 1, accent)
                    setOnMarkerClickListener { _, _ ->
                        onPinTapped(capture)
                        true
                    }
                }
            }
            markers.add(marker)
        }
        overlay.setItems(markers)
        if (points.isNotEmpty()) {
            val box = BoundingBox.fromGeoPoints(points)
            mv.zoomToBoundingBox(box, false, 48)
        }
        mv.invalidate()
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    createRockScoutMapView(ctx, readOnly = true).apply {
                        controller.setCenter(initialCenter)
                        controller.setZoom(initialZoom)
                        overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
                        overlays.add(CompassOverlay(ctx, this).apply { enableCompass() })
                        val clusterer = SpecimenClusterOverlay(this)
                        overlays.add(clusterer)
                        fsClusterOverlay = clusterer
                        fsMapView = this
                    }
                },
                update = { /* no-op */ },
            )

            SculptedIconButton(
                icon = Icons.Filled.Close,
                contentDescription = "Close full screen map",
                onClick = onDismiss,
                accent = Citrine,
                iconTint = Color.White,
                backgroundColor = Slate800,
                size = 44.dp,
                shadowElevation = 5.dp,
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
            )

            MapZoomControls(
                onZoomIn = { fsMapView?.controller?.zoomIn() },
                onZoomOut = { fsMapView?.controller?.zoomOut() },
                onRecenter = {
                    fsMapView?.let { mv ->
                        val pts = captures.mapNotNull { c ->
                            if (c.latitude != null && c.longitude != null) GeoPoint(c.latitude, c.longitude) else null
                        }
                        if (pts.isNotEmpty()) {
                            val box = BoundingBox.fromGeoPoints(pts)
                            mv.controller.animateTo(GeoPoint(box.centerLatitude, box.centerLongitude))
                        }
                    }
                },
                showUser = false,
                onSatellite = { toggleSatelliteView(fsMapView) },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            )

            MapOfflineNotice(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 64.dp),
            )
        }
        MapViewLifecycleEffect(fsMapView)
    }
}

/**
 * Read-only popup showing a field capture card (photo, name, location, description, date).
 * No edit/delete buttons — view only.
 */
@Composable
private fun ReadOnlyCapturePopup(
    capture: CapturedPhoto,
    onDismiss: () -> Unit,
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault()) }
    val spec = SeedData.specimenById(capture.specimenId)
    val accent = if (spec != null) rockClassColor(spec.rockClass) else Citrine
    val displayName = capture.displayName(spec?.name ?: "Unknown specimen")

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = Color(0xFF1E1C16),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = accent, modifier = Modifier.padding(end = 8.dp))
                Text(displayName, color = DarkTextHigh, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                // Photo
                if (capture.imageUris.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1A1812)),
                    ) {
                        AsyncImage(
                            model = capture.imageUris.first(),
                            contentDescription = displayName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Location
                if (capture.customLocation.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Aqua, modifier = Modifier.padding(end = 6.dp))
                        Text(
                            capture.customLocation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Aqua,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }

                // Description
                if (capture.generalInfo.isNotBlank()) {
                    Text(
                        capture.generalInfo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextHigh,
                    )
                    Spacer(Modifier.height(8.dp))
                }

                // Date
                Text(
                    dateFormat.format(Date(capture.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTextMid,
                )

                // Coordinates
                if (capture.hasCoordinates) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Lat: %.4f  \u00b7  Lng: %.4f".format(capture.latitude ?: 0.0, capture.longitude ?: 0.0),
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextMid,
                    )
                }
            }
        },
        confirmButton = {
            SculptedTextButton(
                text = "Close",
                onClick = onDismiss,
                accent = Citrine,
                textColor = Citrine,
            )
        },
    )
}

/** Creates a numbered teardrop pin Drawable in the given accent color. */
private fun createNumberedPinDrawable(context: android.content.Context, number: Int, color: Color): Drawable {
    val density = context.resources.displayMetrics.density
    val sizePx = (32 * density).toInt().coerceAtLeast(32)
    val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val cx = sizePx / 2f
    val cy = sizePx / 2f
    val r = sizePx / 3f

    paint.color = color.toArgb()
    val path = Path().apply {
        moveTo(cx, sizePx.toFloat())
        lineTo(cx - r * 0.9f, cy + r * 0.4f)
        lineTo(cx + r * 0.9f, cy + r * 0.4f)
        close()
    }
    canvas.drawPath(path, paint)
    canvas.drawCircle(cx, cy, r, paint)

    paint.color = android.graphics.Color.WHITE
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 2.5f * density
    canvas.drawCircle(cx, cy, r, paint)

    paint.style = Paint.Style.FILL
    paint.textSize = r * 1.2f
    paint.textAlign = Paint.Align.CENTER
    val fm = paint.fontMetrics
    val textY = cy - (fm.ascent + fm.descent) / 2
    canvas.drawText(number.toString(), cx, textY, paint)

    return BitmapDrawable(context.resources, bmp)
}

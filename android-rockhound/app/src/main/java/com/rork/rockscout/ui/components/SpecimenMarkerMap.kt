package com.rork.rockscout.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.rork.rockscout.data.CapturedPhoto
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate800
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import java.util.Locale

/**
 * Full-page map showing specimen pins for every field capture that has
 * lat/lng coordinates. Tapping a pin opens the matching specimen detail page
 * for viewing or editing.
 *
 * This map does NOT offer Set Pin / Remove Pin controls — it is strictly view-only.
 * Uses the same RockScoutMap base (zoom controls, satellite toggle, offline notice).
 *
 * @param captures list of captures with coordinates; only those with non-null
 *                 latitude/longitude are shown as pins.
 * @param onPinTapped callback when a pin is tapped. Caller typically navigates to
 *                     the specimen detail page for the pinned specimen.
 * @param modifier layout modifier.
 */
@Composable
fun SpecimenMarkerMap(
    captures: List<CapturedPhoto>,
    modifier: Modifier = Modifier,
    onPinTapped: (CapturedPhoto) -> Unit,
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var clusterOverlay by remember { mutableStateOf<SpecimenClusterOverlay?>(null) }
    var isFullscreen by remember { mutableStateOf(false) }
    var fullscreenCenter by remember { mutableStateOf(GeoPoint(39.5, -98.0)) }
    var fullscreenZoom by remember { mutableStateOf(4.0) }

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

        if (capturesWithCoords.isEmpty()) {
            mv.invalidate()
            return@LaunchedEffect
        }

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

            val marker = Marker(mv).apply {
                position = point
                title = displayName
                snippet = capture.customLocation.ifBlank { "" }
                id = "specimen_pin_${capture.id}"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = createCapturePinDrawable(mv.context, index + 1, accent)
                setOnMarkerClickListener { _, _ ->
                    onPinTapped(capture)
                    true
                }
            }
            markers.add(marker)
        }
        overlay.setItems(markers)

        if (points.isNotEmpty()) {
            if (points.size == 1) {
                mv.controller.animateTo(points.first())
                mv.controller.setZoom(8.0)
            } else {
                val box = BoundingBox.fromGeoPoints(points)
                mv.zoomToBoundingBox(box, false, 48)
            }
        }
        mv.invalidate()
    }

    Column(modifier = modifier) {
        // Empty-state helper text sits above the map instead of overlaying it.
        if (capturesWithCoords.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Slate800.copy(alpha = 0.72f))
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
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
                    textAlign = TextAlign.Center,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp)),
        ) {
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
                        fullscreenCenter = safeGeoPoint(it.mapCenter.latitude, it.mapCenter.longitude) ?: GeoPoint(39.5, -98.0)
                        fullscreenZoom = it.zoomLevelDouble.coerceIn(it.minZoomLevel, it.maxZoomLevel)
                    }
                    isFullscreen = true
                },
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
            )
        }
    }

    // Fullscreen overlay — view-only specimen map
    if (isFullscreen) {
        FullscreenSpecimenMapOverlay(
            captures = capturesWithCoords,
            onDismiss = { isFullscreen = false },
            initialCenter = fullscreenCenter,
            initialZoom = fullscreenZoom,
            onPinTapped = onPinTapped,
        )
    }

    MapViewLifecycleEffect(mapView)
}

/**
 * Fullscreen view-only specimen map overlay.
 * Shows all specimen pins, tapping one navigates to the specimen detail page.
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
        if (captures.isEmpty()) {
            mv.invalidate()
            return@LaunchedEffect
        }

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
            val marker = Marker(mv).apply {
                position = point
                title = displayName
                id = "specimen_pin_fs_${capture.id}"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = createCapturePinDrawable(mv.context, index + 1, accent)
                setOnMarkerClickListener { _, _ ->
                    onPinTapped(capture)
                    true
                }
            }
            markers.add(marker)
        }
        overlay.setItems(markers)
        if (points.isNotEmpty()) {
            if (points.size == 1) {
                mv.controller.animateTo(points.first())
                mv.controller.setZoom(8.0)
            } else {
                val box = BoundingBox.fromGeoPoints(points)
                mv.zoomToBoundingBox(box, false, 48)
            }
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
                        // Compass moved to the top-right so it never overlaps the
                        // close button in the top-left.
                        val fsCompass = CompassOverlay(ctx, this).apply { enableCompass() }
                        overlays.add(fsCompass)
                        post {
                            val d = ctx.resources.displayMetrics.density
                            fsCompass.setCompassCenter(width - 56f * d, 40f * d)
                        }
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

/** Creates a numbered map pin Drawable in the given accent color. */
private fun createCapturePinDrawable(context: android.content.Context, number: Int, accent: Color): Drawable {
    val density = context.resources.displayMetrics.density
    val widthPx = (40 * density).toInt().coerceAtLeast(40)
    val heightPx = (52 * density).toInt().coerceAtLeast(52)
    val bmp = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val cx = widthPx / 2f
    val headCy = heightPx * 0.38f
    val headR = widthPx * 0.28f
    val pointY = heightPx.toFloat() - 2f * density

    // Soft shadow behind the pin so it reads on any map background.
    val shadow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF000000.toInt()
        alpha = 80
        style = Paint.Style.FILL
    }
    canvas.drawCircle(cx + 2f * density, headCy + 3f * density, headR * 1.05f, shadow)

    // Teardrop pin path: rounded head tapering to a point at the bottom anchor.
    paint.color = accent.toArgb()
    paint.style = Paint.Style.FILL
    val pinPath = Path().apply {
        moveTo(cx, pointY)
        lineTo(cx - headR * 0.85f, headCy + headR * 0.55f)
        // Top-left arc of the head.
        arcTo(RectF(cx - headR, headCy - headR, cx + headR, headCy + headR), 200f, 140f, false)
        lineTo(cx, pointY)
        close()
    }
    canvas.drawPath(pinPath, paint)

    // White border stroke for crisp definition.
    paint.color = android.graphics.Color.WHITE
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 2.5f * density
    canvas.drawPath(pinPath, paint)

    // White circular badge in the head for the number.
    paint.style = Paint.Style.FILL
    canvas.drawCircle(cx, headCy, headR * 0.62f, paint)

    // Number text, sized to fit inside the badge.
    paint.color = 0xFF1A1812.toInt()
    paint.textSize = headR * 0.95f
    paint.textAlign = Paint.Align.CENTER
    paint.isFakeBoldText = true
    val fm = paint.fontMetrics
    val textY = headCy - (fm.ascent + fm.descent) / 2f
    canvas.drawText(number.toString(), cx, textY, paint)

    return BitmapDrawable(context.resources, bmp)
}

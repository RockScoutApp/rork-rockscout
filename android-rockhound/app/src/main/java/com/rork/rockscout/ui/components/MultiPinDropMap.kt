package com.rork.rockscout.ui.components

import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.rork.rockscout.data.SpecimenMarker
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.TextLow
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

/**
 * Reusable map for dropping and managing multiple specimen-marker pins at once.
 *
 * Flow:
 * 1. Existing pins are rendered in the accent color.
 * 2. User taps the map → an orange tentative pin appears and a floating "Add Pin" button is shown.
 * 3. User taps "Add Pin" → the pin is reported via [onPinSet] and the tentative pin is cleared so the
 *    caller can add it to the list and pass it back as part of [pins].
 * 4. User taps an existing pin → it highlights (red) and a "Remove Pin" button appears.
 * 5. User taps "Remove Pin" → confirmation dialog, then [onPinRemoved] is called with the pin ID.
 *
 * The map auto-zooms to fit all existing pins on load and whenever the pin list changes.
 * Uses the same RockScoutMap base (CartoDB Voyager + auto-hybrid satellite at zoom 16+, zoom controls,
 * satellite toggle, offline notice, layer toggle).
 *
 * @param pins existing pins to display.
 * @param onPinSet called when the user confirms a newly-dropped pin.
 * @param onPinRemoved called when the user confirms removal of an existing pin.
 * @param onExistingPinTapped optional callback when an existing pin is tapped.
 * @param modifier layout modifier.
 * @param initialCenter optional lat/lng to center on when no pins exist.
 * @param initialZoom starting zoom when no pins exist.
 * @param accent accent color for pins.
 * @param pinLabel label used in the removal dialog.
 * @param showLayerToggle whether to show the street/satellite toggle in the zoom controls.
 */
@Composable
fun MultiPinDropMap(
    pins: List<SpecimenMarker>,
    modifier: Modifier = Modifier,
    initialCenter: Pair<Double, Double>? = null,
    initialZoom: Double = 10.0,
    accent: Color = Citrine,
    onPinSet: (Double, Double) -> Unit = { _, _ -> },
    onPinRemoved: (String) -> Unit = {},
    onExistingPinTapped: (SpecimenMarker) -> Unit = {},
    pinLabel: String = "Specimen pin",
    showLayerToggle: Boolean = false,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var currentLayer by remember { mutableStateOf(MapLayerStyle.STREET) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var tentativePin by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var selectedPinId by remember { mutableStateOf<String?>(null) }
    var showRemoveDialog by remember { mutableStateOf(false) }

    // Render pins, highlight selection, and fit the map whenever the data changes.
    LaunchedEffect(mapView, pins, tentativePin, selectedPinId) {
        val mv = mapView ?: return@LaunchedEffect

        // Drop all MultiPinDropMap markers so we can redraw them cleanly.
        mv.overlays.removeAll { it is Marker && it.id?.startsWith("multipin_") == true }

        // Existing pins.
        pins.forEach { pin ->
            val isSelected = pin.id == selectedPinId
            val marker = object : Marker(mv) {
                init {
                    position = GeoPoint(pin.latitude, pin.longitude)
                    title = pin.name
                    snippet = pin.description
                    id = "multipin_${pin.id}"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = createPinDrawable(
                        mv.context,
                        if (isSelected) Color(0xFFFF4444) else accent,
                        sizePx = if (isSelected) 40 else 32,
                    )
                    setOnMarkerClickListener { _, _ ->
                        selectedPinId = if (selectedPinId == pin.id) null else pin.id
                        onExistingPinTapped(pin)
                        true
                    }
                }
            }
            mv.overlays.add(marker)
        }

        // Tentative pin (orange, smaller) — shown while the user is deciding whether to add it.
        tentativePin?.let { (lat, lng) ->
            val marker = Marker(mv).apply {
                id = "multipin_tentative"
                position = GeoPoint(lat, lng)
                title = "Tentative $pinLabel"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = createPinDrawable(mv.context, Color(0xFFE8A33D), sizePx = 28)
            }
            mv.overlays.add(marker)
        }

        // Fit all pins (existing + tentative). Only auto-fit when pins change, not every selection tick.
        val allPoints = pins.map { GeoPoint(it.latitude, it.longitude) } +
            tentativePin?.let { GeoPoint(it.first, it.second) }
        if (allPoints.isNotEmpty()) {
            val box = BoundingBox.fromGeoPoints(allPoints)
            mv.zoomToBoundingBox(box, false, 48)
        }

        mv.invalidate()
    }

    Box(modifier = modifier.clip(RoundedCornerShape(16.dp))) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                createRockScoutMapView(ctx).apply {
                    val center = initialCenter?.let { GeoPoint(it.first, it.second) }
                        ?: if (pins.isNotEmpty()) {
                            val box = BoundingBox.fromGeoPoints(pins.map { GeoPoint(it.latitude, it.longitude) })
                            GeoPoint(box.centerLatitude, box.centerLongitude)
                        } else {
                            GeoPoint(39.5, -98.0)
                        }
                    controller.setCenter(center)
                    controller.setZoom(if (pins.size > 1) 10.0 else initialZoom)
                    overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
                    overlays.add(CompassOverlay(ctx, this).apply { enableCompass() })

                    // Tap-to-drop-pin overlay. Tapping the map always creates a new tentative pin;
                    // existing-pin taps are handled by each marker's own click listener above.
                    overlays.add(object : Overlay() {
                        override fun onSingleTapConfirmed(e: MotionEvent?, view: MapView?): Boolean {
                            if (e == null || view == null) return false
                            val proj = view.projection
                            val point = proj.fromPixels(e.x.toInt(), e.y.toInt())
                            tentativePin = Pair(point.latitude, point.longitude)
                            selectedPinId = null
                            view.invalidate()
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
            onRecenter = {
                val points = pins.map { GeoPoint(it.latitude, it.longitude) } +
                    tentativePin?.let { GeoPoint(it.first, it.second) }
                val target = if (points.isNotEmpty()) {
                    val box = BoundingBox.fromGeoPoints(points)
                    GeoPoint(box.centerLatitude, box.centerLongitude)
                } else {
                    initialCenter?.let { GeoPoint(it.first, it.second) } ?: GeoPoint(39.5, -98.0)
                }
                mapView?.controller?.animateTo(target)
            },
            showUser = false,
            onSatellite = { toggleSatelliteView(mapView) },
            compact = true,
            showLayerToggle = showLayerToggle,
            currentLayer = currentLayer,
            onLayerToggle = {
                currentLayer = toggleMapLayer(mapView, currentLayer)
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
        )

        MapOfflineNotice(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp),
        )

        // Pin controls at top-left.
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Add Pin button — enabled when there's a tentative pin.
            if (tentativePin != null) {
                PinControlPill(
                    label = "Add Pin",
                    icon = Icons.Filled.Add,
                    accent = Citrine,
                    onClick = {
                        tentativePin?.let { (lat, lng) ->
                            onPinSet(lat, lng)
                            tentativePin = null
                            selectedPinId = null
                        }
                    },
                )
            }

            // Remove Pin button — shown when an existing pin is highlighted.
            if (selectedPinId != null) {
                PinControlPill(
                    label = "Remove Pin",
                    icon = Icons.Filled.Remove,
                    accent = Danger,
                    onClick = { showRemoveDialog = true },
                )
            }

            // Hint when no action is active.
            if (tentativePin == null && selectedPinId == null) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xE6000000))
                        .glowingBorder(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = accent, modifier = Modifier.padding(end = 6.dp))
                    Text(
                        "Tap the map to drop a pin",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                    )
                }
            }
        }
    }

    // Confirmation dialog for removing an existing pin.
    if (showRemoveDialog) {
        val selectedName = pins.firstOrNull { it.id == selectedPinId }?.name ?: pinLabel
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = { Text("Remove this $pinLabel?", color = DarkTextMid, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to remove \"$selectedName\"? This cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextLow,
                )
            },
            confirmButton = {
                SculptedTextButton(
                    text = "Remove",
                    onClick = {
                        selectedPinId?.let { id -> onPinRemoved(id) }
                        showRemoveDialog = false
                        selectedPinId = null
                    },
                    accent = Danger,
                    textColor = Danger,
                    fontWeight = FontWeight.Bold,
                )
            },
            dismissButton = {
                SculptedTextButton(
                    text = "Cancel",
                    onClick = { showRemoveDialog = false },
                    accent = Aqua,
                    textColor = TextLow,
                )
            },
            containerColor = Color(0xFF1E1C16),
            titleContentColor = DarkTextMid,
            textContentColor = TextLow,
        )
    }

    MapViewLifecycleEffect(mapView)
}

/** Creates a simple teardrop pin Drawable in the given color and size. */
private fun createPinDrawable(context: Context, color: Color, sizePx: Int = 32): Drawable {
    val density = context.resources.displayMetrics.density
    val px = (sizePx * density).toInt().coerceAtLeast(32)
    val bmp = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val cx = px / 2f
    val cy = px / 2f
    val r = px / 3f

    paint.color = color.toArgb()
    val path = Path().apply {
        moveTo(cx, px.toFloat())
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

    return BitmapDrawable(context.resources, bmp)
}

@Composable
private fun PinControlPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .sculpted(
                shape = RoundedCornerShape(20.dp),
                accent = accent,
                shadowElevation = 4.dp,
                onClick = onClick,
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Slate800)
            .glowingBorder(2.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.padding(end = 4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = accent,
            fontWeight = FontWeight.Bold,
        )
    }
}

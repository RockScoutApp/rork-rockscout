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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.TextLow
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import androidx.compose.ui.graphics.toArgb

/**
 * Reusable map for dropping a single pin with Set Pin / Remove Pin controls.
 *
 * Flow:
 * 1. User taps the map → tentative pin appears (orange).
 * 2. User taps "Set Pin" → pin is confirmed (green/citrine).
 * 3. User taps the confirmed pin → it highlights (enlarged, accent ring) and
 *    a "Remove Pin" button appears.
 * 4. User taps "Remove Pin" → confirmation dialog before removal.
 *
 * Uses the same RockScoutMap base (CartoDB Voyager + auto-hybrid satellite at
 * zoom 16+, zoom controls, satellite toggle, offline notice).
 *
 * @param initialCenter optional lat/lng to center the map on.
 * @param initialZoom starting zoom level.
 * @param onPinSet called when the user confirms a pin with [Set Pin].
 * @param onPinRemoved called when the user confirms removal.
 * @param modifier layout modifier.
 * @param accent accent color for the confirmed pin.
 */
@Composable
fun PinDropMap(
    modifier: Modifier = Modifier,
    initialCenter: Pair<Double, Double>? = null,
    initialZoom: Double = 10.0,
    accent: Color = Citrine,
    onPinSet: (Double, Double) -> Unit = { _, _ -> },
    onPinRemoved: () -> Unit = {},
    pinLabel: String = "Specimen pin",
    showLayerToggle: Boolean = false,
) {
    var currentLayer by remember { mutableStateOf(com.rork.rockscout.ui.components.MapLayerStyle.STREET) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var tentativePin by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var confirmedPin by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var isHighlighted by remember { mutableStateOf(false) }
    var showRemoveDialog by remember { mutableStateOf(false) }

    // Refresh markers whenever pin state changes
    LaunchedEffect(tentativePin, confirmedPin, isHighlighted, mapView) {
        val mv = mapView ?: return@LaunchedEffect
        // Remove all existing PinDropMap markers
        mv.overlays.removeAll { it is Marker && it.id?.startsWith("pindrop_") == true }

        // Tentative pin (orange, smaller)
        tentativePin?.let { (lat, lng) ->
            val marker = Marker(mv).apply {
                id = "pindrop_tentative"
                position = GeoPoint(lat, lng)
                title = "Tentative $pinLabel"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = createPinDrawable(mv.context, Color(0xFFE8A33D), sizePx = 28)
            }
            mv.overlays.add(marker)
        }

        // Confirmed pin (accent color, larger when highlighted)
        confirmedPin?.let { (lat, lng) ->
            val marker = object : Marker(mv) {
                init {
                    position = GeoPoint(lat, lng)
                    title = pinLabel
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    id = "pindrop_confirmed"
                    icon = createPinDrawable(
                        mv.context,
                        if (isHighlighted) Color(0xFFFF4444) else accent,
                        sizePx = if (isHighlighted) 40 else 32,
                    )
                    setOnMarkerClickListener { _, _ ->
                        isHighlighted = !isHighlighted
                        false
                    }
                }
            }
            mv.overlays.add(marker)
        }

        mv.invalidate()
    }

    Box(modifier = modifier.clip(RoundedCornerShape(16.dp))) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                createRockScoutMapView(ctx).apply {
                    val center = initialCenter?.let { GeoPoint(it.first, it.second) }
                        ?: GeoPoint(39.5, -98.0)
                    controller.setCenter(center)
                    controller.setZoom(initialZoom)
                    overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
                    overlays.add(CompassOverlay(ctx, this).apply { enableCompass() })

                    // Tap-to-drop-pin overlay
                    overlays.add(object : Overlay() {
                        override fun onSingleTapConfirmed(e: MotionEvent?, view: MapView?): Boolean {
                            if (e == null || view == null) return false
                            val proj = view.projection
                            val point = proj.fromPixels(e.x.toInt(), e.y.toInt())
                            tentativePin = Pair(point.latitude, point.longitude)
                            isHighlighted = false
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
            onRecenter = {},
            showUser = false,
            onSatellite = { toggleSatelliteView(mapView) },
            compact = true,
            showLayerToggle = showLayerToggle,
            currentLayer = currentLayer,
            onLayerToggle = {
                currentLayer = com.rork.rockscout.ui.components.toggleMapLayer(mapView, currentLayer)
            },
            mapView = mapView,
            showRemovePin = tentativePin != null,
            onRemovePin = {
                tentativePin = null
                isHighlighted = false
                mapView?.invalidate()
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
        )

        MapOfflineNotice(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp),
        )

        // Pin controls at top-left
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Set Pin button — enabled when there's a tentative pin
            if (tentativePin != null && confirmedPin == null) {
                PinControlPill(
                    label = "Set Pin",
                    icon = Icons.Filled.Check,
                    accent = Citrine,
                    onClick = {
                        tentativePin?.let { (lat, lng) ->
                            confirmedPin = Pair(lat, lng)
                            tentativePin = null
                            isHighlighted = false
                            onPinSet(lat, lng)
                        }
                    },
                )
                PinControlPill(
                    label = "Cancel",
                    icon = Icons.Filled.Close,
                    accent = Danger,
                    onClick = {
                        tentativePin = null
                        isHighlighted = false
                    },
                )
            }
            // Remove Pin button — shown when the confirmed pin is highlighted
            if (confirmedPin != null && isHighlighted) {
                PinControlPill(
                    label = "Remove Pin",
                    icon = Icons.Filled.Remove,
                    accent = Danger,
                    onClick = { showRemoveDialog = true },
                )
            }
            // Hint
            if (tentativePin == null && confirmedPin == null) {
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

    // Confirmation dialog for pin removal
    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = { Text("Remove this $pinLabel?", color = DarkTextMid, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to remove this specimen pin? This cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextLow,
                )
            },
            confirmButton = {
                SculptedTextButton(
                    text = "Remove",
                    onClick = {
                        showRemoveDialog = false
                        confirmedPin = null
                        tentativePin = null
                        isHighlighted = false
                        onPinRemoved()
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

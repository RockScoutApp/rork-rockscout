package com.rork.rockscout.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.ui.components.MapExpandButton
import com.rork.rockscout.ui.components.MapOfflineNotice
import com.rork.rockscout.ui.components.MapViewLifecycleEffect
import com.rork.rockscout.ui.components.MapZoomControls
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.createRockScoutMapView
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.toggleSatelliteView
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextMid
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import java.util.Locale

/**
 * Full-screen "Shared Spot" screen reached via a `rockscout://spot` deep link.
 *
 * Shows a single rock-hunting spot pinned on a map with its name and
 * coordinates. A header card explains that a friend shared the spot, and a
 * "Get Directions" button opens the system maps app so the recipient can
 * navigate there. A close button in the top corner dismisses the screen.
 *
 * The deep-link URI format is:
 *   rockscout://spot/<lat>,<lng>?name=<encoded+name>
 *
 * Coordinates are required; the name is optional and falls back to
 * "Shared Rock Hunting Spot" when absent.
 */
@Composable
fun SharedSpotScreen(
    navController: NavController,
    latitude: Double,
    longitude: Double,
    spotName: String,
) {
    val context = LocalContext.current
    val safeName = spotName.ifBlank { "Shared Rock Hunting Spot" }
    var mapView by remember { mutableStateOf<MapView?>(null) }

    BackHandler { navController.popBackStack() }

    // Drop a single pin on the map once the view is ready, then center on it.
    LaunchedEffect(mapView) {
        val mv = mapView ?: return@LaunchedEffect
        mv.overlays.removeAll { it is Marker && it.id?.startsWith("shared_spot_") == true }
        val point = GeoPoint(latitude, longitude)
        val marker = Marker(mv).apply {
            id = "shared_spot_pin"
            position = point
            title = safeName
            snippet = String.format(Locale.US, "%.5f, %.5f", latitude, longitude)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        mv.overlays.add(marker)
        mv.controller.animateTo(point)
        mv.controller.setZoom(13.0)
        mv.invalidate()
    }

    RockBackground {
        Box(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Hero header card with the spot name and a "shared by a friend" note.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.verticalGradient(listOf(Citrine.copy(alpha = 0.38f), Slate900))
                        ),
                ) {
                    // Close button — top left
                    SculptedIconButton(
                        icon = Icons.Filled.Close,
                        contentDescription = "Close shared spot",
                        onClick = { navController.popBackStack() },
                        accent = Citrine,
                        iconTint = Color.White,
                        backgroundColor = Slate800,
                        size = 44.dp,
                        shadowElevation = 5.dp,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 12.dp, top = 16.dp),
                    )

                    // Share button — top right (re-share the spot)
                    SculptedIconButton(
                        icon = Icons.Filled.Share,
                        contentDescription = "Share this spot",
                        onClick = {
                            val deepLink = buildSpotDeepLink(latitude, longitude, safeName)
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Rock hunting spot: $safeName")
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "$safeName\n\nA rock hunting spot shared with you via RockScout.\n" +
                                        "Open in RockScout: $deepLink\n\n" +
                                        "Coordinates: ${String.format(Locale.US, "%.5f, %.5f", latitude, longitude)}",
                                )
                            }
                            SafeLinkOpener.openShareChooser(
                                context = context,
                                shareIntent = shareIntent,
                                title = "Share this rock hunting spot",
                            )
                        },
                        accent = Aqua,
                        iconTint = Color.White,
                        backgroundColor = Slate800,
                        size = 44.dp,
                        shadowElevation = 5.dp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 12.dp, top = 16.dp),
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = Citrine,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "SHARED ROCK HUNTING SPOT",
                                style = MaterialTheme.typography.labelMedium,
                                color = Citrine,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            safeName,
                            style = MaterialTheme.typography.displaySmall,
                            color = TextHigh,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            String.format(Locale.US, "%.5f, %.5f", latitude, longitude),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMid,
                        )
                    }
                }

                // Map fills the remaining space below the header.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(20.dp)),
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            createRockScoutMapView(ctx, readOnly = true).apply {
                                controller.setCenter(GeoPoint(latitude, longitude))
                                controller.setZoom(13.0)
                                overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
                                overlays.add(CompassOverlay(ctx, this).apply { enableCompass() })
                                mapView = this
                            }
                        },
                        update = { /* no-op */ },
                    )

                    MapZoomControls(
                        onZoomIn = { mapView?.controller?.zoomIn() },
                        onZoomOut = { mapView?.controller?.zoomOut() },
                        onRecenter = {
                            mapView?.controller?.animateTo(GeoPoint(latitude, longitude))
                        },
                        showUser = false,
                        onSatellite = { toggleSatelliteView(mapView) },
                        compact = true,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                    )

                    MapOfflineNotice(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 12.dp),
                    )

                    MapExpandButton(
                        onClick = {
                            // Re-share is the primary action here; expanding isn't needed
                            // because the map already fills the screen. Keep the button
                            // as a no-op-free affordance by routing to directions.
                            SafeLinkOpener.openGeo(
                                context = context,
                                geoUri = "geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(safeName)})",
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp),
                    )
                }

                // Action bar — "Get Directions" + "Re-Share".
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SculptedTextButton(
                        text = "Get Directions",
                        onClick = {
                            SafeLinkOpener.openMaps(
                                context = context,
                                googleMapsUri = "google.navigation:q=$latitude,$longitude",
                                fallbackGeoUri = "geo:$latitude,$longitude?q=$latitude,$longitude",
                            )
                        },
                        accent = Citrine,
                        textColor = Ink,
                        modifier = Modifier.weight(1f),
                    )
                    SculptedTextButton(
                        text = "Share Spot",
                        onClick = {
                            val deepLink = buildSpotDeepLink(latitude, longitude, safeName)
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Rock hunting spot: $safeName")
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "$safeName\n\nA rock hunting spot shared with you via RockScout.\n" +
                                        "Open in RockScout: $deepLink\n\n" +
                                        "Coordinates: ${String.format(Locale.US, "%.5f, %.5f", latitude, longitude)}",
                                )
                            }
                            SafeLinkOpener.openShareChooser(
                                context = context,
                                shareIntent = shareIntent,
                                title = "Share this rock hunting spot",
                            )
                        },
                        accent = Aqua,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            MapViewLifecycleEffect(mapView)
        }
    }
}

/**
 * Builds a `rockscout://spot/<lat>,<lng>?name=<encoded+name>` deep link that
 * opens this spot directly in RockScout when tapped by a friend.
 */
fun buildSpotDeepLink(latitude: Double, longitude: Double, name: String): String {
    val coords = String.format(Locale.US, "%.6f,%.6f", latitude, longitude)
    val nameParam = if (name.isBlank()) "" else "?name=${Uri.encode(name)}"
    return "rockscout://spot/$coords$nameParam"
}

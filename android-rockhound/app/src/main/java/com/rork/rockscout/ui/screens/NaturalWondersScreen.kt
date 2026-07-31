package com.rork.rockscout.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.FavoriteSpotResolver
import com.rork.rockscout.data.NaturalWonder
import com.rork.rockscout.data.NaturalWondersData
import com.rork.rockscout.data.UsRegion
import com.rork.rockscout.data.WonderType
import com.rork.rockscout.data.usRegion
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.FullScreenImageViewer
import com.rork.rockscout.ui.components.FullscreenMapOverlay
import com.rork.rockscout.ui.components.MapExpandButton
import com.rork.rockscout.ui.components.MapOfflineNotice
import com.rork.rockscout.ui.components.MapViewLifecycleEffect
import com.rork.rockscout.ui.components.MapZoomControls
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.createRockScoutMapView
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.toggleSatelliteView
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NaturalWondersScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<WonderType?>(null) }
    var selectedRegion by remember { mutableStateOf<UsRegion?>(null) }
    var viewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerInitialPage by remember { mutableIntStateOf(0) }
    var shareWonder by remember { mutableStateOf<NaturalWonder?>(null) }
    var isMapView by remember { mutableStateOf(false) }
    var isFullscreenMap by remember { mutableStateOf(false) }
    val repo = AppRepository.instance
    val favorites by repo.favoriteSpots.collectAsStateWithLifecycle()

    val allWonders = remember { NaturalWondersData.allWonders }
    val filteredWonders = remember(allWonders, searchQuery, selectedType, selectedRegion) {
        var list = allWonders
        if (selectedType != null) {
            list = list.filter { it.type == selectedType }
        }
        if (selectedRegion != null) {
            list = list.filter { it.usRegion() == selectedRegion }
        }
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.lowercase().trim()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                    it.location.lowercase().contains(q) ||
                    it.description.lowercase().contains(q) ||
                    it.rocksToFind.any { rock -> rock.lowercase().contains(q) }
            }
        }
        list
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Natural Wonders",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                },
                navigationIcon = {
                    SculptedIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = { navController.popBackStack() },
                        accent = Citrine,
                        iconTint = Citrine,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
        containerColor = Color.Transparent,
    ) { innerPadding ->
        RockBackground {
            if (isMapView) {
                // ── Map view: all filtered wonders as pins ──
                WonderMapView(
                    wonders = filteredWonders,
                    isFullscreen = false,
                    onExpand = { isFullscreenMap = true },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = innerPadding.calculateTopPadding())
                        .navigationBarsPadding(),
                )
            } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp,
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = 40.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Hero
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(26.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF1B3A4B),
                                        Color(0xFF0D1F2D),
                                        Color(0xFF162A38),
                                    )
                                )
                            )
                            .padding(24.dp),
                    ) {
                        Column {
                            Text(
                                "🌍 Natural Wonders of the Earth",
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextHigh,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "World-famous geological sites — how they formed, and what rocks & minerals you can find there. " +
                                    "From the Grand Canyon to Giant's Causeway, these are the places where geology comes alive.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMid,
                            )
                        }
                    }
                }

                // Search bar
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E1C16))
                            .glowingBorder(1.dp, Color(0xFF1E1C16).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = TextLow, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        androidx.compose.material3.OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search wonders, locations, rocks…", style = MaterialTheme.typography.bodyMedium, color = TextLow) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextHigh,
                                unfocusedTextColor = TextHigh,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                // Type filter chips
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        WonderFilterChip("All Types", selectedType == null) { selectedType = null }
                        WonderType.entries.forEach { type ->
                            WonderFilterChip(type.label, selectedType == type) { selectedType = type }
                        }
                    }
                }

                // US region filter chips
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        WonderFilterChip("All Regions", selectedRegion == null, selectedColor = Citrine) { selectedRegion = null }
                        UsRegion.entries.forEach { region ->
                            WonderFilterChip(region.label, selectedRegion == region, selectedColor = Citrine) { selectedRegion = region }
                        }
                    }
                }

                // Result count
                item {
                    Text(
                        "${filteredWonders.size} wonder${if (filteredWonders.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextLow,
                    )
                }

                // Wonder cards
                items(filteredWonders, key = { it.id }) { wonder ->
                    val favId = FavoriteSpotResolver.wonderId(wonder.id)
                    WonderCard(
                        wonder = wonder,
                        isFavorited = favorites.contains(favId),
                        onToggleFavorite = { repo.toggleFavoriteSpot(favId) },
                        onPhotoClick = { urls, page ->
                            viewerUrls = urls
                            viewerInitialPage = page
                        },
                        onShare = { shareWonder = wonder },
                    )
                }

                if (filteredWonders.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(40.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "No wonders found. Try a different search or filter.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextLow,
                            )
                        }
                    }
                }
            }
            } // end else (list view)

            if (viewerUrls.isNotEmpty()) {
                FullScreenImageViewer(
                    imageUrls = viewerUrls,
                    initialPage = viewerInitialPage,
                    onDismiss = { viewerUrls = emptyList() },
                )
            }
        }
    }

    // Full-screen map overlay
    if (isFullscreenMap) {
        FullscreenMapOverlay(
            onDismiss = { isFullscreenMap = false },
            initialCenter = GeoPoint(20.0, 0.0),
            initialZoom = 2.0,
            onMapReady = { fsMv ->
                val points = filteredWonders.map { GeoPoint(it.latitude, it.longitude) }
                if (points.isNotEmpty()) {
                    val box = BoundingBox.fromGeoPoints(points)
                    fsMv.zoomToBoundingBox(box, false, 48)
                }
                filteredWonders.forEach { wonder ->
                    val marker = WonderMarker(fsMv, wonder)
                    marker.id = "wonder_fs_${wonder.id}"
                    fsMv.overlays.add(marker)
                }
                withContext(Dispatchers.Main) { fsMv.invalidate() }
            },
        )
    }

    shareWonder?.let { wonder ->
        ShareToProfileComposer(
            sourceType = "natural_wonder",
            title = wonder.name,
            tagline = wonder.location + "  •  " + wonder.type.label,
            imageUri = wonder.imageUrl,
            locationText = wonder.location,
            onDismiss = { shareWonder = null },
        )
    }
}

@Composable
private fun WonderFilterChip(
    label: String,
    isSelected: Boolean,
    selectedColor: Color = Aqua,
    onClick: () -> Unit,
) {
    val accent = if (isSelected) selectedColor else Color(0xFF2A2820)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(accent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) Color.Black else TextMid,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

@Composable
private fun WonderCard(
    wonder: NaturalWonder,
    isFavorited: Boolean,
    onToggleFavorite: () -> Unit,
    onPhotoClick: (List<String>, Int) -> Unit,
    onShare: () -> Unit,
) {
    val typeColor = wonder.type.accentColor()
    DarkCard(
        accent = typeColor,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
    ) {
        // Hero image — uses 16:9 aspect ratio + Crop so the photo fills the frame
        // without letterboxing, while still showing the full scenic composition.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF1A1812))
                .clickable { onPhotoClick(listOf(wonder.imageUrl), 0) },
        ) {
            AsyncImage(
                model = wonder.imageUrl,
                contentDescription = wonder.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            // Gradient overlay for text legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    ),
            )
            // Type badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(typeColor.copy(alpha = 0.85f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    wonder.type.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                )
            }
            // Location
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = typeColor, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    wonder.location,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            // Favorite + Share buttons
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .sculpted(
                            shape = RoundedCornerShape(8.dp),
                            accent = if (isFavorited) Citrine else typeColor,
                            shadowElevation = 2.dp,
                            onClick = onToggleFavorite,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        if (isFavorited) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = if (isFavorited) "Remove from Favorite Spots" else "Add to Favorite Spots",
                        tint = if (isFavorited) Citrine else Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .sculpted(
                            shape = RoundedCornerShape(8.dp),
                            accent = typeColor,
                            shadowElevation = 2.dp,
                            onClick = onShare,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Share, contentDescription = "Share", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Title
        Text(
            wonder.name,
            style = MaterialTheme.typography.titleLarge,
            color = DarkTextHigh,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(8.dp))

        // Description
        Text(
            wonder.description,
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )

        Spacer(Modifier.height(10.dp))

        // Formation section
        Text(
            "How It Formed",
            style = MaterialTheme.typography.titleSmall,
            color = typeColor,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            wonder.formation,
            style = MaterialTheme.typography.bodySmall,
            color = TextMid,
        )

        Spacer(Modifier.height(10.dp))

        // Rocks to find
        Text(
            "What to Find There",
            style = MaterialTheme.typography.titleSmall,
            color = Citrine,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            wonder.rocksToFind.forEach { rock ->
                TagChip(rock, color = Citrine)
            }
        }

        Spacer(Modifier.height(10.dp))

        // Fun facts
        Text(
            "Did You Know?",
            style = MaterialTheme.typography.titleSmall,
            color = Aqua,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        wonder.funFacts.forEach { fact ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                Text("•", style = MaterialTheme.typography.bodySmall, color = Aqua, modifier = Modifier.padding(end = 6.dp))
                Text(fact, style = MaterialTheme.typography.bodySmall, color = TextMid, maxLines = 4, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

/** Accent color per wonder type for visual variety. */
private fun WonderType.accentColor(): Color = when (this) {
    WonderType.VOLCANIC -> Color(0xFFE2574C)
    WonderType.SEDIMENTARY -> Color(0xFFD9B26A)
    WonderType.METAMORPHIC -> Color(0xFF9B7BD8)
    WonderType.IMPACT -> Color(0xFFC0C0C0)
    WonderType.KARST -> Color(0xFF5CC98C)
    WonderType.COASTAL -> Color(0xFF44AACC)
    WonderType.DESERT -> Color(0xFFE8A33D)
    WonderType.MOUNTAIN -> Color(0xFF6FA8C7)
}

// ───────────────────────────────────────────────────────────────────────────
// Map view for Natural Wonders
// ───────────────────────────────────────────────────────────────────────────

/**
 * Interactive osmdroid map showing every wonder in [wonders] as a colored pin.
 * Pins are tinted by [WonderType] so volcanic, sedimentary, karst, etc. are
 * visually distinct. The map auto-fits to show all pins on load, with zoom
 * controls, satellite toggle, compass, and an expand-to-fullscreen button.
 * Tapping a pin shows an info bubble with the wonder name and location.
 */
@Composable
private fun WonderMapView(
    wonders: List<NaturalWonder>,
    isFullscreen: Boolean,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }

    // Render / refresh pins whenever the filtered list changes.
    LaunchedEffect(mapView, wonders) {
        val mv = mapView ?: return@LaunchedEffect
        mv.overlays.removeAll { it is Marker && it.id?.startsWith("wonder_pin_") == true }
        wonders.forEach { wonder ->
            val marker = WonderMarker(mv, wonder)
            marker.id = "wonder_pin_${wonder.id}"
            mv.overlays.add(marker)
        }
        // Auto-fit to all pins
        val points = wonders.map { GeoPoint(it.latitude, it.longitude) }
        if (points.isNotEmpty()) {
            if (points.size == 1) {
                mv.controller.animateTo(points.first())
                mv.controller.setZoom(8.0)
            } else {
                val box = BoundingBox.fromGeoPoints(points)
                mv.zoomToBoundingBox(box, false, 64)
            }
        }
        mv.invalidate()
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp)),
            factory = { ctx ->
                createRockScoutMapView(ctx).apply {
                    controller.setZoom(2.0)
                    controller.setCenter(GeoPoint(20.0, 0.0))
                    overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
                    val compass = CompassOverlay(ctx, this).apply { enableCompass() }
                    overlays.add(compass)
                    post {
                        val d = ctx.resources.displayMetrics.density
                        compass.setCompassCenter(width - 56f * d, 40f * d)
                    }
                    mapView = this
                }
            },
            update = { /* handled by LaunchedEffect */ },
        )

        // Zoom controls (bottom-right)
        MapZoomControls(
            onZoomIn = { mapView?.controller?.zoomIn() },
            onZoomOut = { mapView?.controller?.zoomOut() },
            onRecenter = {},
            showUser = false,
            onSatellite = { toggleSatelliteView(mapView) },
            mapView = mapView,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        )

        // Expand to fullscreen (top-right)
        MapExpandButton(
            onClick = onExpand,
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
        )

        // Offline notice (top-center)
        MapOfflineNotice(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
        )

        // Pin count badge (top-left)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Slate800.copy(alpha = 0.92f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Text(
                "${wonders.size} wonder${if (wonders.size != 1) "s" else ""} on map",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }

    MapViewLifecycleEffect(mapView)
}

/**
 * A map marker for a [NaturalWonder]. The pin is tinted with the wonder's
 * type accent color. Tapping shows an info bubble with name + location.
 */
private class WonderMarker(
    mapView: MapView,
    private val wonder: NaturalWonder,
) : Marker(mapView) {
    init {
        position = GeoPoint(wonder.latitude, wonder.longitude)
        title = wonder.name
        snippet = wonder.location
        setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM)
        icon = createWonderPinDrawable(mapView.context, wonder.type.accentColor())
    }
}

/** Process-wide cache of wonder pin bitmaps keyed by color + density. */
private val wonderPinCache = java.util.concurrent.ConcurrentHashMap<String, Bitmap>()

/**
 * Builds (or retrieves from cache) a teardrop pin [Drawable] in the given
 * [color]. Cached so 73 pins don't allocate 73 separate bitmaps.
 */
private fun createWonderPinDrawable(
    context: android.content.Context,
    color: Color,
): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val key = "wonder_pin_${density}_${color.toArgb()}"
    val bmp = wonderPinCache.getOrPut(key) {
        buildWonderPinBitmap(density, color)
    }
    return BitmapDrawable(context.resources, bmp)
}

/** Builds the teardrop pin bitmap with a white inner ring. */
private fun buildWonderPinBitmap(density: Float, color: Color): Bitmap {
    val sizePx = (28 * density).toInt().coerceAtLeast(28)
    val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bmp)
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
    canvas.drawCircle(cx, cy, r * 0.35f, paint)

    return bmp
}

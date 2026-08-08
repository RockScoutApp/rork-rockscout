package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.ArtifactRarity
import com.rork.rockscout.data.ArtifactSpecimens
import com.rork.rockscout.data.RelicRegions
import com.rork.rockscout.data.WarRelicSpecimens
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.RarityIndicator
import com.rork.rockscout.ui.components.MapViewLifecycleEffect
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.StandaloneZoomableImageViewer
import com.rork.rockscout.ui.components.StatRow
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.addOverlaySafe
import com.rork.rockscout.ui.components.createRockScoutMapView
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.components.isValidCoordinate
import com.rork.rockscout.ui.components.removeOverlaysSafe
import com.rork.rockscout.ui.components.runMapSafe
import com.rork.rockscout.ui.components.safeGeoPoint
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextMid
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/**
 * Standalone detail screen for artifacts — modeled on [SpecimenDetailScreen]
 * (same layout, section ordering, heart/add/share buttons) but reads from
 * the [Artifact] model. [SpecimenDetailScreen] itself is NOT modified.
 */
@Composable
fun ArtifactDetailScreen(
    navController: NavController,
    artifactId: String,
) {
    val artifact = ArtifactSpecimens.byId(artifactId) ?: WarRelicSpecimens.byId(artifactId)
    val repo = AppRepository.instance
    val context = LocalContext.current

    if (artifact == null) {
        RockBackground {
            Box(Modifier.fillMaxWidth().padding(40.dp)) {
                Text("Artifact not found.", color = TextMid)
            }
        }
        return
    }

    val accent = Color(artifact.accentHex)
    val collection by repo.collection.collectAsStateWithLifecycle()
    val wishlist by repo.wishlist.collectAsStateWithLifecycle()
    val likedSpecimens by repo.likedSpecimens.collectAsStateWithLifecycle()
    val collected = collection.any { it.specimenId == artifact.id }
    val wishlisted = wishlist.contains(artifact.id)
    val isLiked = likedSpecimens.contains(artifact.id)

    // Pre-compute relic region coordinates for the map (only for war relics)
    val relicRegions = remember(artifact.id) {
        if (artifact.domain == "war_relic") {
            RelicRegions.getRegionCoords(artifact.whereFound)
        } else {
            emptyList()
        }
    }

    var viewerOpen by remember { mutableStateOf(false) }

    if (viewerOpen) {
        StandaloneZoomableImageViewer(
            imageUrl = artifact.imageUrl,
            onDismiss = { viewerOpen = false },
        )
    }

    RockBackground {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Header
            item {
                ArtifactHeader(
                    artifact = artifact,
                    accent = accent,
                    isLiked = isLiked,
                    collected = collected,
                    wishlisted = wishlisted,
                    onLikeToggle = { repo.toggleLike(artifact.id) },
                    onCollectionToggle = { repo.toggleCollection(artifact.id) },
                    onWishlistToggle = { repo.toggleWishlist(artifact.id) },
                    onBack = { navController.popBackStack() },
                )
            }

            // Image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(280.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF1A1812))
                        .clickable { viewerOpen = true },
                ) {
                    AsyncImage(
                        model = artifact.imageUrl,
                        contentDescription = artifact.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }

            // Overview
            item {
                SectionCard("Overview", accent = accent) {
                    Text(
                        artifact.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = com.rork.rockscout.ui.theme.DarkTextMid,
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TagChip(artifact.family, color = accent)
                        RarityIndicator(ArtifactRarity.forId(artifact.id), accent = accent)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TagChip(artifact.subFamily, color = Aqua)
                    }
                }
            }

            // Culture & Era / Origin & Era
            item {
                val cultureLabel = if (artifact.domain == "war_relic") "Origin / Side" else "Culture / Tradition"
                SectionCard(if (artifact.domain == "war_relic") "Origin & Era" else "Culture & Era", accent = accent) {
                    StatRow(cultureLabel, artifact.tribe, accent = accent)
                    StatRow("Time Period", artifact.timePeriod, accent = accent, showDivider = false)
                }
            }

            // How it was made
            item {
                SectionCard("How it was made", accent = accent) {
                    Text(
                        artifact.howMade,
                        style = MaterialTheme.typography.bodyLarge,
                        color = com.rork.rockscout.ui.theme.DarkTextMid,
                    )
                }
            }

            // Where it's found
            item {
                SectionCard("Where it's found", accent = accent) {
                    artifact.whereFound.forEachIndexed { index, place ->
                        LocationRow(place, accent, isLast = index == artifact.whereFound.lastIndex)
                    }
                }
            }

            // Visual region map — only for war relics
            if (artifact.domain == "war_relic") {
                item {
                    RelicRegionMap(
                        regions = relicRegions,
                        accent = accent,
                    )
                }
            }

            // Good to know
            item {
                SectionCard("Good to know", accent = accent) {
                    artifact.funFacts.forEachIndexed { index, fact ->
                        FactRow(fact, accent, isLast = index == artifact.funFacts.lastIndex)
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtifactHeader(
    accent: Color,
    isLiked: Boolean,
    collected: Boolean,
    wishlisted: Boolean,
    onLikeToggle: () -> Unit,
    onCollectionToggle: () -> Unit,
    onWishlistToggle: () -> Unit,
    onBack: () -> Unit,
    artifact: com.rork.rockscout.data.Artifact,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(
                Brush.verticalGradient(listOf(accent.copy(alpha = 0.40f), Slate900))
            ),
    ) {
        // Back button
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 48.dp)
                .size(44.dp)
                .sculpted(
                    shape = RoundedCornerShape(22.dp),
                    accent = accent,
                    shadowElevation = 5.dp,
                    circular = true,
                    onClick = onBack,
                )
                .clip(RoundedCornerShape(22.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        // Like / Bookmark / Share — top right
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 12.dp, top = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Heart
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .sculpted(
                        shape = CircleShape,
                        accent = accent,
                        shadowElevation = 5.dp,
                        circular = true,
                        onClick = onLikeToggle,
                    )
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    androidx.compose.material.icons.Icons.Filled.Favorite,
                    contentDescription = "Like",
                    tint = if (isLiked) accent else Color.White,
                )
            }
            // Collection
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .sculpted(
                        shape = CircleShape,
                        accent = accent,
                        shadowElevation = 5.dp,
                        circular = true,
                        onClick = onCollectionToggle,
                    )
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (collected) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = "Collect",
                    tint = if (collected) accent else Color.White,
                )
            }
            // Wishlist
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .sculpted(
                        shape = CircleShape,
                        accent = Aqua,
                        shadowElevation = 5.dp,
                        circular = true,
                        onClick = onWishlistToggle,
                    )
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (wishlisted) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = "Wishlist",
                    tint = if (wishlisted) Aqua else Color.White,
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp),
        ) {
            TagChip(artifact.family.uppercase(), color = accent, filled = true)
            Spacer(Modifier.height(8.dp))
            Text(
                artifact.name,
                style = MaterialTheme.typography.displayMedium,
                color = TextHigh,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                artifact.tagline,
                style = MaterialTheme.typography.bodyLarge,
                color = com.rork.rockscout.ui.components.brightenForText(accent, amount = 0.5f),
                fontStyle = FontStyle.Italic,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * Compact osmdroid map showing discovery region markers for war relics.
 * Uses the same [createRockScoutMapView] infrastructure as the Dig Sites map,
 * configured as a read-only embedded preview (scroll passes through to the
 * parent LazyColumn, taps still work for marker popups).
 */
@Composable
private fun RelicRegionMap(
    regions: List<RelicRegions.RegionCoord>,
    accent: Color,
) {
    var mapView by remember { mutableStateOf<MapView?>(null) }

    LaunchedEffect(mapView, regions) {
        val mv = mapView ?: return@LaunchedEffect
        runMapSafe("RelicRegionMap markers") {
            mv.removeOverlaysSafe { it is Marker && it.id?.startsWith("relic_region_") == true }
            val points = mutableListOf<GeoPoint>()
            for (region in regions) {
                if (!isValidCoordinate(region.lat, region.lng)) continue
                val geo = safeGeoPoint(region.lat, region.lng) ?: continue
                points.add(geo)
                val marker = Marker(mv).apply {
                    position = geo
                    title = region.label
                    id = "relic_region_${region.name}"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                mv.addOverlaySafe(marker)
            }
            // Fit bounds to show all markers
            if (points.size > 1) {
                val box = BoundingBox.fromGeoPoints(points)
                mv.zoomToBoundingBox(box, false, 48)
            } else if (points.size == 1) {
                mv.controller.animateTo(points.first())
                mv.controller.setZoom(6.0)
            }
            mv.invalidate()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF1A1812))
            .glowingBorder(1.dp, accent.copy(alpha = 0.25f), RoundedCornerShape(18.dp)),
    ) {
        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Filled.MilitaryTech,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Discovery Regions",
                style = MaterialTheme.typography.labelMedium,
                color = TextHigh,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.weight(1f))
            Text(
                "${regions.size} region${if (regions.size != 1) "s" else ""}",
                style = MaterialTheme.typography.labelSmall,
                color = TextMid,
            )
        }

        // Map view
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(14.dp)),
            factory = { ctx ->
                createRockScoutMapView(ctx, readOnly = true, isEmbedded = true).apply {
                    controller.setZoom(5.0)
                    controller.setCenter(GeoPoint(38.9, -77.35))
                    mapView = this
                }
            },
            update = { /* handled by LaunchedEffect above */ },
        )

        // Region chips below the map
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            regions.take(3).forEach { region ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(accent.copy(alpha = 0.12f))
                        .glowingBorder(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        region.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (regions.size > 3) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(accent.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        "+${regions.size - 3} more",
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                    )
                }
            }
        }
    }

    MapViewLifecycleEffect(mapView)
}

@Composable
private fun SectionCard(title: String, accent: Color = Citrine, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, 18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Aqua,
                fontWeight = FontWeight.Bold,
            )
        }
        DarkCard(modifier = Modifier.fillMaxWidth(), accent = accent) { content() }
    }
}

@Composable
private fun LocationRow(place: String, accent: Color, isLast: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.85f))
                    .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape),
            )
            Spacer(Modifier.width(12.dp))
            Text(
                place,
                style = MaterialTheme.typography.bodyLarge,
                color = com.rork.rockscout.ui.theme.DarkTextMid,
                modifier = Modifier.weight(1f),
            )
        }
        if (!isLast) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.06f)),
            )
        }
    }
}

@Composable
private fun FactRow(fact: String, accent: Color, isLast: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accent.copy(alpha = 0.15f))
                    .glowingBorder(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.History, contentDescription = null, tint = accent, modifier = Modifier.size(14.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text(
                fact,
                style = MaterialTheme.typography.bodyLarge,
                color = com.rork.rockscout.ui.theme.DarkTextMid,
                modifier = Modifier.weight(1f),
            )
        }
        if (!isLast) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.06f)),
            )
        }
    }
}

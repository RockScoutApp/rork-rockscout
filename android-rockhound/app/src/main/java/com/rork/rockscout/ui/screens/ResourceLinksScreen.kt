package com.rork.rockscout.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import com.rork.rockscout.data.ProfanityFilter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiNature
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.FavoriteSpotResolver
import com.rork.rockscout.data.LocationCacheRepository
import com.rork.rockscout.data.MuseumEntry
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.data.UsMuseums
import com.rork.rockscout.data.UserMuseum
import com.rork.rockscout.data.UserMuseumStore
import com.rork.rockscout.data.db.MuseumEntity
import com.rork.rockscout.data.db.toMuseumEntry
import com.rork.rockscout.ui.components.CompactSearchPill
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.LocationImage
import com.rork.rockscout.ui.components.addOverlaySafe
import com.rork.rockscout.ui.components.removeOverlaysSafe
import com.rork.rockscout.ui.components.runMapSafe
import com.rork.rockscout.ui.components.safeGeoPoint
import com.rork.rockscout.ui.components.isValidCoordinate
import com.rork.rockscout.ui.components.FullscreenMapOverlay
import com.rork.rockscout.ui.components.GEM_MINERAL_HERO_URL
import com.rork.rockscout.ui.components.GEM_IMG_CUT_GEMS
import com.rork.rockscout.ui.components.GEM_IMG_MUSEUM
import com.rork.rockscout.ui.components.GEM_IMG_LAPIDARY
import com.rork.rockscout.ui.components.GEM_IMG_AMMONITE
import com.rork.rockscout.ui.components.GEM_IMG_PEGMATITE
import com.rork.rockscout.ui.components.InlineContentImage
import com.rork.rockscout.ui.components.BookStyleImage
import com.rork.rockscout.ui.components.MapExpandButton
import com.rork.rockscout.ui.components.MapOfflineNotice
import com.rork.rockscout.ui.components.MapViewLifecycleEffect
import com.rork.rockscout.ui.components.MapZoomControls
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.createRockScoutMapView
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.components.toggleSatelliteView
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.TextHigh
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

private data class ResourceCategory(
    val title: String,
    val icon: ImageVector,
    val accent: Color,
    val links: List<ResourceLink>,
)

private data class ResourceLink(
    val name: String,
    val description: String,
    val url: String,
)

@Composable
fun ResourceLinksScreen(navController: NavController) {
    val pagerState = rememberPagerState(initialPage = 0) { 2 }
    val pagerScope = rememberCoroutineScope()

    val screenTitle = when (pagerState.currentPage) {
        1 -> "Museum Directory"
        else -> "Rock & Gem Resources"
    }

    ScreenScaffold(title = screenTitle, onBack = { navController.popBackStack() }) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Pill switcher ──
            ResourcePillSwitcher(
                currentPage = pagerState.currentPage,
                onPageSelected = { page ->
                    pagerScope.launch { pagerState.animateScrollToPage(page) }
                },
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 0.dp,
            ) { page ->
                when (page) {
                    0 -> WebsitesPage()
                    1 -> MuseumsPage()
                }
            }
        }
    }
}

/* ── Pill switcher ────────────────────────────────────────────────────────── */

@Composable
private fun ResourcePillSwitcher(
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val pills = listOf("Websites" to 0, "Museums" to 1)
        pills.forEach { (label, page) ->
            val isActive = currentPage == page
            val accent = if (page == 0) Citrine else Aqua
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (isActive) accent.copy(alpha = 0.18f) else Color.Transparent
                    )
                    .glowingBorder(
                        1.5.dp,
                        if (isActive) accent else Color(0x33FFFFFF),
                        RoundedCornerShape(24.dp),
                    )
                    .clickable { onPageSelected(page) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (page == 0) Icons.Filled.Language else Icons.Filled.Museum,
                        contentDescription = null,
                        tint = if (isActive) accent else DarkTextMid,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        label,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isActive) accent else DarkTextMid,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                    )
                }
            }
            if (page < 1) {
                Spacer(Modifier.width(12.dp))
            }
        }
    }
}

/* ── Page 1: Websites ─────────────────────────────────────────────────────── */

@Composable
private fun WebsitesPage() {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Hero banner image
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1A1812))
                    .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(20.dp)),
            ) {
                AsyncImage(
                    model = GEM_MINERAL_HERO_URL,
                    contentDescription = "Collection of colorful gemstones and minerals",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f), Color.Black.copy(alpha = 0.7f))
                            )
                        ),
                    contentAlignment = Alignment.BottomStart,
                ) {
                    Text(
                        "A curated collection of trusted websites for rock, gem, mineral, and fossil research. Tap any card to open it in your browser.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }

        item {
            InlineContentImage(
                imageUrl = GEM_IMG_CUT_GEMS,
                contentDescription = "Collection of cut and polished gemstones on dark velvet",
                caption = "Cut and faceted gemstones — the end result of lapidary art",
            )
        }
        item {
            ResourceCategoryCard(
                category = resourceCategories[0],
                onLinkClick = { link -> SafeLinkOpener.openUrl(context, link.url) },
            )
        }
        item {
            InlineContentImage(
                imageUrl = GEM_IMG_MUSEUM,
                contentDescription = "Museum mineral display case with crystal specimens",
                caption = "Museum mineral collections — a wealth of reference data",
            )
        }
        item {
            ResourceCategoryCard(
                category = resourceCategories[1],
                onLinkClick = { link -> SafeLinkOpener.openUrl(context, link.url) },
            )
        }
        item {
            InlineContentImage(
                imageUrl = GEM_IMG_LAPIDARY,
                contentDescription = "Lapidary faceting machine cutting a gemstone",
                caption = "Faceting machine — where rough stones become gems",
            )
        }
        item {
            ResourceCategoryCard(
                category = resourceCategories[2],
                onLinkClick = { link -> SafeLinkOpener.openUrl(context, link.url) },
            )
        }
        item {
            InlineContentImage(
                imageUrl = GEM_IMG_AMMONITE,
                contentDescription = "Fossil ammonite in sedimentary rock",
                caption = "Ammonite fossil — paleontology meets rockhounding",
            )
        }
        item {
            ResourceCategoryCard(
                category = resourceCategories[3],
                onLinkClick = { link -> SafeLinkOpener.openUrl(context, link.url) },
            )
        }
        item {
            InlineContentImage(
                imageUrl = GEM_IMG_PEGMATITE,
                contentDescription = "Pegmatite pocket with large tourmaline and quartz crystals",
                caption = "Pegmatite pocket — where giant crystals grow",
            )
        }
        item {
            ResourceCategoryCard(
                category = resourceCategories[4],
                onLinkClick = { link -> SafeLinkOpener.openUrl(context, link.url) },
            )
        }
        item {
            ResourceCategoryCard(
                category = resourceCategories[5],
                onLinkClick = { link -> SafeLinkOpener.openUrl(context, link.url) },
            )
        }
        item {
            ResourceCategoryCard(
                category = resourceCategories[6],
                onLinkClick = { link -> SafeLinkOpener.openUrl(context, link.url) },
            )
        }
    }
}

/* ── Page 2: Museums ──────────────────────────────────────────────────────── */

@Composable
private fun MuseumsPage() {
    val context = LocalContext.current
    val repo = AppRepository.instance
    val favorites by repo.favoriteSpots.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var expandedState by remember { mutableStateOf("") }
    var isMapView by remember { mutableStateOf(false) }
    var isFullscreenMap by remember { mutableStateOf(false) }
    var showAddMuseumDialog by remember { mutableStateOf(false) }
    val userMuseums by UserMuseumStore.museums.collectAsStateWithLifecycle()

    // Collect curated museums from Room (offline SQLite cache). Falls back to
    // UsMuseums singleton on first launch before seeding completes.
    val roomMuseums by LocationCacheRepository.curatedMuseums
        .collectAsStateWithLifecycle(emptyList())

    // Build state-grouped list from Room data (or fallback to hardcoded).
    val allStates: List<UsMuseums.StateGroup> = remember(roomMuseums) {
        if (roomMuseums.isNotEmpty()) {
            roomMuseums
                .map { it.toMuseumEntry() }
                .groupBy { it.state }
                .toSortedMap(compareBy { it })
                .map { (state, museums) ->
                    UsMuseums.StateGroup(state, museums.sortedBy { it.name })
                }
        } else {
            UsMuseums.states
        }
    }

    val filteredStates = remember(allStates, searchQuery) {
        if (searchQuery.isBlank()) {
            allStates
        } else {
            allStates.map { sg ->
                sg.copy(museums = sg.museums.filter { m ->
                    m.name.contains(searchQuery, ignoreCase = true) ||
                        m.city.contains(searchQuery, ignoreCase = true) ||
                        sg.state.contains(searchQuery, ignoreCase = true) ||
                        (m.address?.contains(searchQuery, ignoreCase = true) ?: false)
                })
            }.filter { it.museums.isNotEmpty() }
        }
    }

    val filteredUserMuseums = remember(searchQuery, userMuseums) {
        if (searchQuery.isBlank()) userMuseums
        else userMuseums.filter { m ->
            m.name.contains(searchQuery, ignoreCase = true) ||
                m.city.contains(searchQuery, ignoreCase = true) ||
                m.state.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredMuseums = remember(filteredStates, filteredUserMuseums) {
        filteredStates.flatMap { it.museums } + filteredUserMuseums.map { um ->
            MuseumEntry(name = um.name, city = um.city, state = um.state, website = um.website, lat = um.lat, lng = um.lng)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // ── Search bar — single row at top ──
        CompactSearchPill(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Search by museum name or location…",
            accent = Aqua,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
        )
        // ── Add a Museum pill + map toggle ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Add a Museum pill button (Museums tab only)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(23.dp))
                    .background(Aqua.copy(alpha = 0.15f))
                    .glowingBorder(1.5.dp, Aqua.copy(alpha = 0.7f), RoundedCornerShape(23.dp))
                    .clickable { showAddMuseumDialog = true }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Museum", tint = Aqua, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Add", style = MaterialTheme.typography.labelMedium, color = Aqua, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.weight(1f))
            SculptedIconButton(
                icon = if (isMapView) Icons.Filled.ViewList else Icons.Filled.Map,
                contentDescription = if (isMapView) "Switch to list view" else "Switch to map view",
                onClick = { isMapView = !isMapView },
                accent = Aqua,
                iconTint = Aqua,
                size = 48.dp,
                shadowElevation = 3.dp,
            )
        }

        if (isMapView) {
            MuseumMapView(
                museums = filteredMuseums,
                isFullscreen = false,
                onExpand = { isFullscreenMap = true },
                onOpenWebsite = { url -> SafeLinkOpener.openUrl(context, url) },
                modifier = Modifier.fillMaxSize().navigationBarsPadding(),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (filteredStates.isEmpty()) {
                    item {
                        DarkCard(modifier = Modifier.fillMaxWidth(), accent = TextMid) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text("\uD83C\uDFDB\uFE0F", style = MaterialTheme.typography.displayMedium)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "No museums found.",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextHigh,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Try a different search term.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DarkTextMid,
                                )
                            }
                        }
                    }
                } else {
                    // User-added museums section
                    if (filteredUserMuseums.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, tint = Aqua, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "Your Added Museums",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Aqua,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(Modifier.weight(1f))
                                Text(
                                    "${filteredUserMuseums.size}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = DarkTextMid,
                                )
                            }
                        }
                        items(filteredUserMuseums, key = { it.id }) { um ->
                            val umFavId = FavoriteSpotResolver.museumId(um.name, um.city, um.state)
                            val isUmFav = favorites.contains(umFavId)
                            DarkCard(
                                modifier = Modifier.fillMaxWidth(),
                                accent = Aqua,
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            um.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = DarkTextHigh,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        if (um.city.isNotBlank() || um.state.isNotBlank()) {
                                            Text(
                                                listOfNotNull(um.city.takeIf { it.isNotBlank() }, um.state.takeIf { it.isNotBlank() }).joinToString(", "),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = DarkTextMid,
                                            )
                                        }
                                        if (um.website.isNotBlank()) {
                                            Text(
                                                um.website,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Aqua,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF2A2820))
                                            .glowingBorder(1.dp, Citrine.copy(alpha = 0.3f), CircleShape)
                                            .clickable { repo.toggleFavoriteSpot(umFavId) },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            if (isUmFav) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                            contentDescription = if (isUmFav) "Remove from favorites" else "Add to favorites",
                                            tint = if (isUmFav) Citrine else Color.White,
                                            modifier = Modifier.size(18.dp),
                                        )
                                    }
                                    Spacer(Modifier.width(6.dp))
                                    if (um.website.isNotBlank()) {
                                        SculptedIconButton(
                                            icon = Icons.AutoMirrored.Filled.Launch,
                                            contentDescription = "Open website",
                                            onClick = { SafeLinkOpener.openUrl(context, um.website) },
                                            accent = Aqua,
                                            iconTint = Aqua,
                                            size = 36.dp,
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF2A2820))
                                            .glowingBorder(1.dp, Aqua.copy(alpha = 0.3f), CircleShape)
                                            .clickable { UserMuseumStore.deleteMuseum(um.id) },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(Icons.Filled.Close, contentDescription = "Remove", tint = DarkTextMid, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                    // Standard museum sections by state
                    items(filteredStates, key = { it.state }) { stateGroup ->
                        MuseumStateSection(
                            stateGroup = stateGroup,
                            isExpanded = expandedState == stateGroup.state,
                            onToggle = {
                                expandedState = if (expandedState == stateGroup.state) "" else stateGroup.state
                            },
                            onOpenWebsite = { url -> SafeLinkOpener.openUrl(context, url) },
                            favorites = favorites,
                            onToggleFavorite = { favId -> repo.toggleFavoriteSpot(favId) },
                        )
                    }
                }
            }
        }
    }

    // Add a Museum dialog
    if (showAddMuseumDialog) {
        AddMuseumDialog(
            onDismiss = { showAddMuseumDialog = false },
            onAdd = { name, city, state, website, lat, lng ->
                UserMuseumStore.addMuseum(
                    UserMuseum(
                        id = "um-" + System.currentTimeMillis(),
                        name = name,
                        city = city,
                        state = state,
                        website = website,
                        lat = lat,
                        lng = lng,
                        addedAtMs = System.currentTimeMillis(),
                    )
                )
                showAddMuseumDialog = false
            },
        )
    }

    // Full-screen map overlay
    if (isFullscreenMap) {
        FullscreenMapOverlay(
            onDismiss = { isFullscreenMap = false },
            initialCenter = GeoPoint(39.5, -98.35),
            initialZoom = 3.5,
            onMapReady = { fsMv ->
                val points = filteredMuseums.map { GeoPoint(it.lat, it.lng) }
                if (points.isNotEmpty()) {
                    val box = BoundingBox.fromGeoPoints(points)
                    fsMv.zoomToBoundingBox(box, false, 48)
                }
                filteredMuseums.forEach { museum ->
                    val marker = MuseumMarker(fsMv, museum)
                    marker.id = "museum_fs_${museum.name}"
                    fsMv.overlays.add(marker)
                }
                withContext(Dispatchers.Main) { fsMv.invalidate() }
            },
        )
    }
}

/* ── Museum map view ───────────────────────────────────────────────────────── */

/**
 * Interactive osmdroid map showing every museum in [museums] as a pin.
 * The map auto-fits to show all pins on load, with zoom controls,
 * satellite toggle, compass, and an expand-to-fullscreen button.
 * Tapping a pin shows an info bubble with the museum name and city.
 */
@Composable
private fun MuseumMapView(
    museums: List<MuseumEntry>,
    isFullscreen: Boolean,
    onExpand: () -> Unit,
    onOpenWebsite: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var mapView by remember { mutableStateOf<MapView?>(null) }

    // Render / refresh pins whenever the filtered list changes.
    LaunchedEffect(mapView, museums) {
        val mv = mapView ?: return@LaunchedEffect
        runMapSafe("MuseumMapView pins") {
            mv.removeOverlaysSafe { it is Marker && it.id?.startsWith("museum_pin_") == true }
            val validMuseums = museums.filter { isValidCoordinate(it.lat, it.lng) }
            validMuseums.forEach { museum ->
                val marker = MuseumMarker(mv, museum)
                marker.id = "museum_pin_${museum.name}"
                mv.addOverlaySafe(marker)
            }
            // Auto-fit to all pins
            val points = validMuseums.map { GeoPoint(it.lat, it.lng) }
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
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp)),
            factory = { ctx ->
                createRockScoutMapView(ctx).apply {
                    controller.setZoom(3.5)
                    controller.setCenter(GeoPoint(39.5, -98.35))
                    addOverlaySafe(RotationGestureOverlay(this).apply { isEnabled = true })
                    val compass = CompassOverlay(ctx, this).apply { enableCompass() }
                    addOverlaySafe(compass)
                    post {
                        if (!isAttachedToWindow) return@post
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
        if (museums.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Slate800.copy(alpha = 0.92f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    "${museums.size} museum${if (museums.size != 1) "s" else ""} on map",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }

    MapViewLifecycleEffect(mapView)
}

/**
 * A map marker for a [MuseumEntry]. The pin is tinted with the Aqua accent.
 * Tapping shows an info bubble with name + city, state.
 */
private class MuseumMarker(
    mapView: MapView,
    private val museum: MuseumEntry,
) : Marker(mapView) {
    init {
        position = GeoPoint(museum.lat, museum.lng)
        title = museum.name
        snippet = "${museum.city}, ${museum.state}"
        setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM)
        icon = createMuseumPinDrawable(mapView.context)
    }
}

/** Process-wide cache of museum pin bitmaps keyed by density. */
private val museumPinCache = java.util.concurrent.ConcurrentHashMap<String, Bitmap>()

/**
 * Builds (or retrieves from cache) a teardrop pin [Drawable] in Aqua.
 * Cached so 250+ pins don't allocate separate bitmaps.
 */
private fun createMuseumPinDrawable(
    context: android.content.Context,
): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val key = "museum_pin_${density}"
    val bmp = museumPinCache.getOrPut(key) {
        buildMuseumPinBitmap(density)
    }
    return BitmapDrawable(context.resources, bmp)
}

/** Builds the teardrop pin bitmap in Aqua with a white inner ring. */
private fun buildMuseumPinBitmap(density: Float): Bitmap {
    val sizePx = (28 * density).toInt().coerceAtLeast(28)
    val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bmp)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val cx = sizePx / 2f
    val cy = sizePx / 2f
    val r = sizePx / 3f
    val pinColor = 0xFF38B6FF.toInt() // Aqua

    paint.color = pinColor
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

/* ── Museum list components ────────────────────────────────────────────────── */

@Composable
private fun MuseumStateSection(
    stateGroup: UsMuseums.StateGroup,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onOpenWebsite: (String) -> Unit,
    favorites: List<String>,
    onToggleFavorite: (String) -> Unit,
) {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Aqua) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header row — tappable to expand/collapse
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onToggle)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Aqua.copy(alpha = 0.18f))
                        .glowingBorder(1.dp, Aqua.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Aqua, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    stateGroup.state,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextHigh,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "${stateGroup.museums.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = DarkTextMid,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.width(6.dp))
                Icon(
                    Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = DarkTextMid,
                    modifier = Modifier.size(24.dp),
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Spacer(Modifier.height(4.dp))
                    stateGroup.museums.forEach { museum ->
                        val favId = FavoriteSpotResolver.museumId(museum.name, museum.city, museum.state)
                        MuseumCard(
                            museum = museum,
                            isFavorited = favorites.contains(favId),
                            onToggleFavorite = { onToggleFavorite(favId) },
                            onOpenWebsite = { onOpenWebsite(museum.website) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MuseumCard(
    museum: MuseumEntry,
    isFavorited: Boolean,
    onToggleFavorite: () -> Unit,
    onOpenWebsite: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF15130E))
            .glowingBorder(1.dp, Color(0xFF15130E).copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .clickable(onClick = onOpenWebsite)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Citrine.copy(alpha = 0.15f))
                .glowingBorder(1.dp, Citrine.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            LocationImage(
                name = museum.name,
                region = "${museum.city} ${museum.state}",
                modifier = Modifier.fillMaxSize(),
                fallback = {
                    Icon(Icons.Filled.Museum, contentDescription = null, tint = Citrine, modifier = Modifier.size(22.dp))
                },
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                museum.name,
                style = MaterialTheme.typography.titleSmall,
                color = TextHigh,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "${museum.city}, ${museum.state}",
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextMid,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF2A2820))
                .glowingBorder(1.dp, Citrine.copy(alpha = 0.3f), CircleShape)
                .clickable(onClick = onToggleFavorite),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (isFavorited) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                contentDescription = if (isFavorited) "Remove from favorites" else "Add to favorites",
                tint = if (isFavorited) Citrine else Color.White,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        Icon(
            Icons.AutoMirrored.Filled.Launch,
            contentDescription = "Open website",
            tint = Aqua,
            modifier = Modifier.size(20.dp),
        )
    }
}

/* ── Shared components ────────────────────────────────────────────────────── */

@Composable
private fun ResourceCategoryCard(
    category: ResourceCategory,
    onLinkClick: (ResourceLink) -> Unit,
) {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = category.accent) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(category.accent.copy(alpha = 0.22f))
                    .glowingBorder(1.dp, category.accent.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(category.icon, contentDescription = null, tint = category.accent, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(14.dp))
            Text(
                category.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(14.dp))
        if (category.title.contains("Gem", ignoreCase = true)) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    "\u2192 These trusted resources cover identification, valuation, and lapidary techniques.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                    modifier = Modifier.weight(1f).padding(end = 10.dp),
                )
                BookStyleImage(
                    imageUrl = GEM_IMG_CUT_GEMS,
                    contentDescription = "Cut and polished gemstones",
                )
            }
            Spacer(Modifier.height(10.dp))
        } else if (category.title.contains("Museum", ignoreCase = true) || category.title.contains("Reference", ignoreCase = true)) {
            Row(verticalAlignment = Alignment.Top) {
                BookStyleImage(
                    imageUrl = GEM_IMG_MUSEUM,
                    contentDescription = "Museum mineral display case",
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "\u2192 Museum collections are the gold standard for comparing your finds.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(10.dp))
        } else if (category.title.contains("Fossil", ignoreCase = true) || category.title.contains("Paleo", ignoreCase = true)) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    "\u2192 Fossil resources help you identify specimens from every geologic period.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                    modifier = Modifier.weight(1f).padding(end = 10.dp),
                )
                BookStyleImage(
                    imageUrl = GEM_IMG_AMMONITE,
                    contentDescription = "Ammonite fossil specimen",
                )
            }
            Spacer(Modifier.height(10.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            category.links.forEach { link ->
                ResourceLinkRow(
                    link = link,
                    accent = category.accent,
                    onClick = { onLinkClick(link) },
                )
            }
        }
    }
}

@Composable
private fun ResourceLinkRow(
    link: ResourceLink,
    accent: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF15130E))
            .glowingBorder(1.dp, Color(0xFF15130E).copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                link.name,
                style = MaterialTheme.typography.titleMedium,
                color = TextHigh,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                link.description,
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextMid,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(12.dp))
        Icon(
            Icons.AutoMirrored.Filled.Launch,
            contentDescription = "Open link",
            tint = accent,
            modifier = Modifier.size(22.dp),
        )
    }
}

private val resourceCategories = listOf(
    ResourceCategory(
        title = "Mineral & Gem Databases",
        icon = Icons.Filled.Diamond,
        accent = Citrine,
        links = listOf(
            ResourceLink(
                "Mindat.org",
                "The largest mineral database on Earth. Detailed locality, chemistry, and photo records for thousands of minerals.",
                "https://www.mindat.org",
            ),
            ResourceLink(
                "Minerals.net",
                "The Mineral & Gemstone Kingdom — a visual guide to mineral species, rock types, and gem classifications.",
                "https://www.minerals.net",
            ),
            ResourceLink(
                "Webmineral",
                "Crystallography, chemical formulas, physical properties, and locality data for minerals.",
                "https://www.webmineral.com",
            ),
            ResourceLink(
                "Smithsonian NMNH Mineral Sciences",
                "The National Museum of Natural History's mineral collections and research resources.",
                "https://naturalhistory.si.edu/research/mineral-sciences",
            ),
        ),
    ),
    ResourceCategory(
        title = "Gemology & Faceting",
        icon = Icons.Filled.Science,
        accent = Color(0xFF9B7BD8),
        links = listOf(
            ResourceLink(
                "International Gem Society (IGS)",
                "Gem identification, encyclopedia articles, and a retail gem price guide.",
                "https://www.gemsociety.org",
            ),
            ResourceLink(
                "GIA Gem Encyclopedia",
                "The Gemological Institute of America's authoritative encyclopedia on gemstone history, lore, and properties.",
                "https://www.gia.edu/gem-encyclopedia",
            ),
            ResourceLink(
                "Gemology Online",
                "Community forum and reference materials for gemologists and lapidary artists.",
                "https://www.gemonline.com",
            ),
        ),
    ),
    ResourceCategory(
        title = "General Geology & Earth Science",
        icon = Icons.Filled.Public,
        accent = Aqua,
        links = listOf(
            ResourceLink(
                "Geology.com",
                "Educational hub for earth science, rock identification, and news about the rock cycle.",
                "https://geology.com",
            ),
            ResourceLink(
                "USGS Geology",
                "Maps, data, and research from the United States Geological Survey.",
                "https://www.usgs.gov/programs/geology-minerals-energy-and-geophysics-science-center",
            ),
            ResourceLink(
                "USGS Mineral Resources Program",
                "Interactive maps and databases for mineral deposits and resource assessments.",
                "https://www.usgs.gov/programs/mineral-resources-program",
            ),
            ResourceLink(
                "Geological Society of America",
                "Professional geology organization with publications, field guides, and meeting resources.",
                "https://www.geosociety.org",
            ),
            ResourceLink(
                "British Geological Survey",
                "UK-focused geology maps, data, and research with global relevance.",
                "https://www.bgs.ac.uk",
            ),
        ),
    ),
    ResourceCategory(
        title = "Paleontology & Fossils",
        icon = Icons.Filled.EmojiNature,
        accent = Color(0xFF8BBF6A),
        links = listOf(
            ResourceLink(
                "Smithsonian Paleobiology",
                "Fossil collections, deep-time research, and the history of life from the Smithsonian.",
                "https://paleobiology.si.edu",
            ),
            ResourceLink(
                "Paleontology Portal (Paleoportal)",
                "Explore fossils by region, time period, and taxonomy with museum-quality data.",
                "https://paleoportal.org",
            ),
            ResourceLink(
                "Fossilworks",
                "Gateway to the Paleobiology Database — search fossil occurrences, taxa, and time ranges.",
                "https://www.fossilworks.org",
            ),
            ResourceLink(
                "The Paleontological Society",
                "Professional paleontology organization with resources, publications, and education.",
                "https://www.paleosoc.org",
            ),
            ResourceLink(
                "iDigBio",
                "Integrated Digitized Biocollections — millions of fossil and specimen records from museums.",
                "https://www.idigbio.org",
            ),
            ResourceLink(
                "Fossil Forum",
                "Active community for fossil identification, collecting ethics, and trip reports.",
                "https://www.thefossilforum.com",
            ),
        ),
    ),
    ResourceCategory(
        title = "Government & Public Land Resources",
        icon = Icons.Filled.Gavel,
        accent = Color(0xFFE2574C),
        links = listOf(
            ResourceLink(
                "Bureau of Land Management",
                "US public land rules, permits, and recreational rock collecting guidelines.",
                "https://www.blm.gov",
            ),
            ResourceLink(
                "US Forest Service",
                "National forest and grassland information, including recreation and collection rules.",
                "https://www.fs.usda.gov",
            ),
            ResourceLink(
                "National Park Service",
                "Park-specific regulations — collecting rocks, fossils, or minerals is prohibited in most national parks.",
                "https://www.nps.gov",
            ),
        ),
    ),
    ResourceCategory(
        title = "Publications & Rockhounding",
        icon = Icons.Filled.Book,
        accent = Color(0xFFD9B26A),
        links = listOf(
            ResourceLink(
                "Rock & Gem Magazine",
                "The premier publication for lapidary, mineral hobbyists, and the rockhounding community.",
                "https://www.rockngem.com",
            ),
            ResourceLink(
                "Mindat Localities",
                "Detailed locality reports and photos from rockhounds around the world.",
                "https://www.mindat.org/localities/",
            ),
            ResourceLink(
                "American Federation of Mineralogical Societies",
                "Umbrella organization for local gem and mineral clubs across the US.",
                "https://www.amfed.org",
            ),
        ),
    ),
    ResourceCategory(
        title = "War Relics",
        icon = Icons.Filled.MilitaryTech,
        accent = Color(0xFF4A6B7B),
        links = listOf(
            ResourceLink(
                "American Civil War Museum",
                "Online collections from the premier museum of Civil War artifacts, uniforms, and ordnance.",
                "https://www.acwm.org",
            ),
            ResourceLink(
                "Encyclopedia of Civil War Artillery",
                "Comprehensive reference for Civil War artillery projectiles, fuses, and ordnance.",
                "https://www.civilwarartillery.com",
            ),
            ResourceLink(
                "Ridgeway Reference Library",
                "Ridgeway's extensive photo reference for Civil War plates, buckles, and buttons.",
                "https://www.relicman.com",
            ),
            ResourceLink(
                "Museum of the American Revolution",
                "Online exhibits and artifact collections from the American Revolutionary War.",
                "https://www.amrevmuseum.org",
            ),
            ResourceLink(
                "NPS Archaeology",
                "National Park Service archaeology articles on Civil War and Revolutionary War sites.",
                "https://www.nps.gov/subjects/archeology/index.htm",
            ),
            ResourceLink(
                "Colonial Williamsburg Archaeology",
                "Archaeological research and collections from Colonial Williamsburg, Virginia.",
                "https://www.colonialwilliamsburg.org",
            ),
            ResourceLink(
                "Historic Jamestowne Collections",
                "Artifact collections and archaeological findings from Historic Jamestowne.",
                "https://www.historicjamestowne.org",
            ),
            ResourceLink(
                "Smithsonian National Museum of American History",
                "The Smithsonian's collections of Civil War and Revolutionary War military artifacts.",
                "https://americanhistory.si.edu",
            ),
            ResourceLink(
                "American Revolution Institute",
                "Reference library and collections on Revolutionary War artifacts and military material culture.",
                "https://www.americanrevolutioninstitute.org",
            ),
            ResourceLink(
                "Civil War Bullet Forum",
                "Community forum dedicated to the identification and study of Civil War bullets and projectiles.",
                "https://www.cwbullet.org",
            ),
            ResourceLink(
                "Columbia River Arsenal",
                "Detailed photo reference for Civil War small arms ammunition and cartridge types.",
                "https://www.columbiariverarsenal.com",
            ),
            ResourceLink(
                "Mount Vernon Archaeology",
                "Archaeological findings and artifact collections from George Washington's Mount Vernon estate.",
                "https://www.mountvernon.org",
            ),
        ),
    ),
)

/* ── Add a Museum dialog ───────────────────────────────────────────────────── */

@Composable
private fun AddMuseumDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, city: String, state: String, website: String, lat: Double, lng: Double) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var latStr by remember { mutableStateOf("") }
    var lngStr by remember { mutableStateOf("") }
    val canSubmit = name.isNotBlank() && latStr.isNotBlank() && lngStr.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1C16),
        titleContentColor = TextHigh,
        textContentColor = DarkTextMid,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Museum, contentDescription = null, tint = Aqua, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("Add a Museum", color = TextHigh, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = ProfanityFilter.filter(it) },
                    label = { Text("Museum name *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2A2820),
                        unfocusedContainerColor = Color(0xFF2A2820),
                        focusedBorderColor = Aqua,
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedTextColor = TextHigh,
                        unfocusedTextColor = TextHigh,
                        cursorColor = Aqua,
                    ),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = ProfanityFilter.filter(it) },
                        label = { Text("City") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF2A2820),
                            unfocusedContainerColor = Color(0xFF2A2820),
                            focusedBorderColor = Aqua,
                            unfocusedBorderColor = Color(0x33FFFFFF),
                            focusedTextColor = TextHigh,
                            unfocusedTextColor = TextHigh,
                            cursorColor = Aqua,
                        ),
                    )
                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = ProfanityFilter.filter(it) },
                        label = { Text("State") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF2A2820),
                            unfocusedContainerColor = Color(0xFF2A2820),
                            focusedBorderColor = Aqua,
                            unfocusedBorderColor = Color(0x33FFFFFF),
                            focusedTextColor = TextHigh,
                            unfocusedTextColor = TextHigh,
                            cursorColor = Aqua,
                        ),
                    )
                }
                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Website URL") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2A2820),
                        unfocusedContainerColor = Color(0xFF2A2820),
                        focusedBorderColor = Aqua,
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedTextColor = TextHigh,
                        unfocusedTextColor = TextHigh,
                        cursorColor = Aqua,
                    ),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = latStr,
                        onValueChange = { latStr = it.filter { c -> c.isDigit() || c == '.' || c == '-' } },
                        label = { Text("Latitude *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF2A2820),
                            unfocusedContainerColor = Color(0xFF2A2820),
                            focusedBorderColor = Aqua,
                            unfocusedBorderColor = Color(0x33FFFFFF),
                            focusedTextColor = TextHigh,
                            unfocusedTextColor = TextHigh,
                            cursorColor = Aqua,
                        ),
                    )
                    OutlinedTextField(
                        value = lngStr,
                        onValueChange = { lngStr = it.filter { c -> c.isDigit() || c == '.' || c == '-' } },
                        label = { Text("Longitude *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF2A2820),
                            unfocusedContainerColor = Color(0xFF2A2820),
                            focusedBorderColor = Aqua,
                            unfocusedBorderColor = Color(0x33FFFFFF),
                            focusedTextColor = TextHigh,
                            unfocusedTextColor = TextHigh,
                            cursorColor = Aqua,
                        ),
                    )
                }
                Text(
                    "You can find lat/lng on Google Maps by right-clicking a location.",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTextMid,
                )
            }
        },
        confirmButton = {
            com.rork.rockscout.ui.components.SculptedButton(
                text = "Add Museum",
                onClick = {
                    val lat = latStr.toDoubleOrNull() ?: 0.0
                    val lng = lngStr.toDoubleOrNull() ?: 0.0
                    onAdd(name.trim(), city.trim(), state.trim(), website.trim(), lat, lng)
                },
                accent = Aqua,
                containerColor = Aqua,
                textColor = com.rork.rockscout.ui.theme.Ink,
            )
        },
        dismissButton = {
            com.rork.rockscout.ui.components.SculptedTextButton(
                text = "Cancel",
                onClick = onDismiss,
                accent = DarkTextMid,
                textColor = DarkTextMid,
            )
        },
    )
}

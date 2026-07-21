package com.rork.rockscout.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.navigation.NavController
import com.rork.rockscout.data.BlmCampground
import com.rork.rockscout.data.BlmData
import com.rork.rockscout.data.BlmDigSite
import com.rork.rockscout.data.BlmInfoSection
import com.rork.rockscout.data.BlmState
import com.rork.rockscout.data.BlmTrailhead
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.ui.components.GlobalSearchSection
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.StateSilhouette
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.BLM_HERO_URL
import com.rork.rockscout.ui.components.BLM_IMG_ROCKHOUND
import com.rork.rockscout.ui.components.BLM_IMG_CANYON
import com.rork.rockscout.ui.components.BLM_IMG_CAMPING
import com.rork.rockscout.ui.components.BLM_IMG_TRAILHEAD
import com.rork.rockscout.ui.components.BLM_IMG_PANORAMIC
import com.rork.rockscout.ui.components.InlineContentImage
import com.rork.rockscout.ui.components.BookStyleImage
import com.rork.rockscout.ui.components.BLM_IMG_ROCK_HAMMER
import com.rork.rockscout.ui.components.BLM_IMG_DESERT_VARNISH
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid

private enum class BlmTab(val label: String) {
    STATES("States"), TRAILHEADS("Trailheads"), CAMPGROUNDS("Campgrounds"), INFO("Info")
}

private const val BLM_CAMPFIRE_BACKGROUND_URL = BLM_HERO_URL

/** Translucent dark card used on the BLM guide so the campfire background
 *  remains visible while the white text stays legible. */
@Composable
private fun BlmCard(
    modifier: Modifier = Modifier,
    accent: Color = Citrine,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .sculpted(shape = shape, accent = accent, shadowElevation = 6.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.58f),
                        Color.Black.copy(alpha = 0.68f),
                        Color.Black.copy(alpha = 0.78f),
                    )
                )
            )
            .glowingBorder(BorderStroke(3.dp, accent.copy(alpha = 0.55f)), shape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(100.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(accent.copy(alpha = 0.16f), Color.Transparent)
                    )
                )
        )
        Column(
            modifier = Modifier.padding(contentPadding)
        ) {
            content()
        }
    }
}

@Composable
fun BlmGuideScreen(navController: NavController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val tabs = BlmTab.entries

    ScreenScaffold(
        title = "BLM Public Lands",
        onBack = { navController.popBackStack() },
        background = { innerContent ->
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = BLM_CAMPFIRE_BACKGROUND_URL,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.45f),
                                    Color.Black.copy(alpha = 0.55f),
                                    Color.Black.copy(alpha = 0.65f),
                                    Color.Black.copy(alpha = 0.75f),
                                )
                            )
                        )
                )
                innerContent()
            }
        },
    ) {
        Column(Modifier.fillMaxSize()) {
            GlobalSearchSection(
                navController = navController,
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search BLM states, trailheads, camps, specimens, rocks…",
            )
            // Intro text
            Text(
                "Rockhounding on Bureau of Land Management public lands. Rules vary by state and site — always verify with the local BLM office before collecting.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 8.dp),
            )
            // Intro image
            InlineContentImage(
                imageUrl = BLM_IMG_PANORAMIC,
                contentDescription = "Panoramic view of BLM public lands in the American West",
                caption = "BLM public lands — millions of acres open to rockhounding",
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
            )
            // Tab bar — scrollable so long labels like "Campgrounds" aren't truncated
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF1E1C16).copy(alpha = 0.82f),
                contentColor = Citrine,
                edgePadding = 0.dp,
                divider = {},
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .height(3.dp)
                                .padding(horizontal = 8.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Citrine),
                        )
                    }
                },
            ) {
                tabs.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab.ordinal,
                        onClick = { selectedTab = tab.ordinal },
                        text = {
                            Text(
                                tab.label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (selectedTab == tab.ordinal) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == tab.ordinal) Citrine else TextMid,
                                maxLines = 1,
                            )
                        },
                    )
                }
            }
            // Tab content
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    BlmTab.STATES.ordinal -> StatesTab(navController)
                    BlmTab.TRAILHEADS.ordinal -> TrailheadsTab(navController)
                    BlmTab.CAMPGROUNDS.ordinal -> CampgroundsTab(navController)
                    BlmTab.INFO.ordinal -> InfoTab()
                }
            }
        }
    }
}

// ── States tab ──────────────────────────────────────────────────────────

@Composable
private fun StatesTab(navController: NavController) {
    val states = remember { BlmData.allStates }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(states, key = { it.code }) { state ->
            StateCard(state = state) {
                navController.navigate(Routes.blmState(state.code))
            }
        }
    }
}

@Composable
private fun StateCard(state: BlmState, onClick: () -> Unit) {
    val accent = Color(state.accentHex)
    BlmCard(
        accent = accent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Colored state silhouette badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent.copy(alpha = 0.18f))
                    .glowingBorder(2.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                StateSilhouette(stateCode = state.code, accent = accent, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    state.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    state.blmAcreage,
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                    maxLines = 2,
                    softWrap = true,
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = DarkTextMid,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

// ── Trailheads tab ──────────────────────────────────────────────────────

@Composable
private fun TrailheadsTab(navController: NavController) {
    val trailheads = remember { BlmData.trailheads }
    val grouped = remember(trailheads) {
        trailheads.groupBy { it.state }
    }
    val allStates = remember { BlmData.allStates }
    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        allStates.forEach { state ->
            val trails = grouped[state.code].orEmpty()
            item(key = "header_${state.code}") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Hiking, contentDescription = null, tint = Success, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        state.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "(${trails.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextLow,
                    )
                }
            }
            if (trails.isEmpty()) {
                item(key = "empty_${state.code}") {
                    Text(
                        "No popular BLM trailheads documented for ${state.name}. Check with the local BLM field office for access points.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextLow,
                        modifier = Modifier.padding(start = 26.dp, bottom = 4.dp),
                    )
                }
            } else {
                items(trails, key = { "${it.state}_${it.name}" }) { trail ->
                    TrailheadCard(trailhead = trail) {
                        navController.navigate(Routes.blmTrailhead(trail.name))
                    }
                }
            }
        }
    }
}

@Composable
private fun TrailheadCard(trailhead: BlmTrailhead, onClick: () -> Unit) {
    BlmCard(
        accent = Success,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Success.copy(alpha = 0.16f))
                    .glowingBorder(1.dp, Success.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Explore, contentDescription = null, tint = Success, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(trailhead.name, style = MaterialTheme.typography.titleMedium, color = DarkTextHigh, fontWeight = FontWeight.Bold)
                Text(trailhead.region, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                Spacer(Modifier.height(6.dp))
                Text(trailhead.description, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
            }
        }
    }
}

// ── Campgrounds tab ─────────────────────────────────────────────────────

@Composable
private fun CampgroundsTab(navController: NavController) {
    val campgrounds = remember { BlmData.campgrounds }
    val grouped = remember(campgrounds) {
        campgrounds.groupBy { it.state }
    }
    val allStates = remember { BlmData.allStates }
    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        allStates.forEach { state ->
            val camps = grouped[state.code].orEmpty()
            item(key = "camp_header_${state.code}") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Terrain, contentDescription = null, tint = Citrine, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        state.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "(${camps.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextLow,
                    )
                }
            }
            if (camps.isEmpty()) {
                item(key = "camp_empty_${state.code}") {
                    Text(
                        "No popular BLM campgrounds documented for ${state.name}. Dispersed camping may still be allowed on BLM land — check with the local field office.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextLow,
                        modifier = Modifier.padding(start = 26.dp, bottom = 4.dp),
                    )
                }
            } else {
                items(camps, key = { "${it.state}_${it.name}" }) { camp ->
                    CampgroundCard(campground = camp) {
                        navController.navigate(Routes.blmCampground(camp.name))
                    }
                }
            }
        }
    }
}

@Composable
private fun CampgroundCard(campground: BlmCampground, onClick: () -> Unit) {
    BlmCard(
        accent = Citrine,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Citrine.copy(alpha = 0.16f))
                    .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Terrain, contentDescription = null, tint = Citrine, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(campground.name, style = MaterialTheme.typography.titleMedium, color = DarkTextHigh, fontWeight = FontWeight.Bold)
                Text(campground.region, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                Spacer(Modifier.height(6.dp))
                Text(campground.description, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                Spacer(Modifier.height(8.dp))
                TagChip(text = campground.feeInfo, color = Citrine)
            }
        }
    }
}

// ── Important Info tab ──────────────────────────────────────────────────

@Composable
private fun InfoTab() {
    val sections = remember { BlmData.infoSections }
    val context = LocalContext.current
    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(sections, key = { it.title }) { section ->
            InfoSectionCard(section = section)
        }
        // Inline content images interspersed after info sections
        item {
            InlineContentImage(
                imageUrl = BLM_IMG_ROCKHOUND,
                contentDescription = "Rockhound collecting rocks and minerals on BLM desert land",
                caption = "Collecting on BLM land — always verify local rules first",
            )
        }
        item {
            InlineContentImage(
                imageUrl = BLM_IMG_CANYON,
                contentDescription = "Desert canyon with exposed colorful rock layers on BLM land",
                caption = "Exposed rock layers — reading the geology helps you find specimens",
            )
        }
        item {
            InlineContentImage(
                imageUrl = BLM_IMG_TRAILHEAD,
                contentDescription = "BLM trailhead information sign in the desert",
                caption = "Trailhead signs mark access points to BLM collecting areas",
            )
        }
        item {
            InlineContentImage(
                imageUrl = BLM_IMG_CAMPING,
                contentDescription = "Dispersed camping on BLM land with tent and truck",
                caption = "Dispersed camping — free on most BLM land, up to 14 days",
            )
        }
        // Disclaimer + Visit BLM.gov link
        item {
            BlmCard(accent = Citrine, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        "Rules can change",
                        style = MaterialTheme.typography.titleMedium,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "The information in this guide is curated from BLM.gov and is for general reference only. Rules, limits, and area closures can change at any time. Always verify current regulations with the local BLM field office before collecting.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Citrine.copy(alpha = 0.15f))
                            .glowingBorder(2.dp, Citrine.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable {
                                SafeLinkOpener.openUrl(context, "https://www.blm.gov/programs/recreation/rockhounding")
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = Citrine, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Visit BLM.gov Rockhounding",
                            style = MaterialTheme.typography.titleSmall,
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoSectionCard(section: BlmInfoSection) {
    val accent = Color(section.accentHex)
    BlmCard(accent = accent, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(section.icon, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.width(10.dp))
            Text(
                section.title,
                style = MaterialTheme.typography.titleMedium,
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(10.dp))
        section.points.forEachIndexed { index, point ->
            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accent)
                        .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape)
                        .padding(top = 8.dp),
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    point,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                    modifier = Modifier.weight(1f),
                )
            }
            // Book-style illustration: rock hammer after first point in collecting section
            if (index == 1 && section.title.contains("Collecting", ignoreCase = true)) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Top) {
                    BookStyleImage(
                        imageUrl = BLM_IMG_ROCK_HAMMER,
                        contentDescription = "Rock hammer chipping at a mineral vein in desert rock",
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "\u2192 A good rock hammer is your most important BLM tool.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextLow,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            // Book-style illustration: desert varnish after second point in rules section
            if (index == 2 && section.title.contains("Rules", ignoreCase = true)) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        "\u2192 Desert varnish coats many BLM rocks \u2014 look underneath for the true color.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextLow,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        modifier = Modifier.weight(1f).padding(end = 10.dp),
                    )
                    BookStyleImage(
                        imageUrl = BLM_IMG_DESERT_VARNISH,
                        contentDescription = "Desert varnish on a boulder in the American Southwest",
                    )
                }
            }
        }
    }
}

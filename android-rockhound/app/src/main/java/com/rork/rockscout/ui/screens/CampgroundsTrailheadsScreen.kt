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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.BlmCampground
import com.rork.rockscout.data.BlmData
import com.rork.rockscout.data.BlmTrailhead
import com.rork.rockscout.ui.components.NewBadge
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.StatePickerPill
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import kotlinx.coroutines.launch

@Composable
fun CampgroundsTrailheadsScreen(navController: NavController) {
    val pagerState = rememberPagerState(initialPage = 0) { 2 }
    val scope = rememberCoroutineScope()

    val campgrounds = remember { BlmData.campgrounds }
    val trailheads = remember { BlmData.trailheads }
    val allStates = remember { BlmData.allStates }

    val campgroundsGrouped = remember(campgrounds) { campgrounds.groupBy { it.state } }
    val trailheadsGrouped = remember(trailheads) { trailheads.groupBy { it.state } }

    val statesWithCampgrounds = remember(allStates, campgroundsGrouped) {
        allStates.filter { state -> campgroundsGrouped[state.code]?.isNotEmpty() == true }
    }
    val statesWithTrailheads = remember(allStates, trailheadsGrouped) {
        allStates.filter { state -> trailheadsGrouped[state.code]?.isNotEmpty() == true }
    }

    val screenTitle = when (pagerState.currentPage) {
        1 -> "Trailheads"
        else -> "Campgrounds"
    }

    val campListState = rememberLazyListState()
    val trailListState = rememberLazyListState()

    val campStateToIndex = remember(statesWithCampgrounds, campgroundsGrouped) {
        var index = 0
        val map = mutableMapOf<String, Int>()
        statesWithCampgrounds.forEach { state ->
            val camps = campgroundsGrouped[state.code].orEmpty()
            map[state.code] = index
            index += 1 + if (camps.isEmpty()) 1 else camps.size
        }
        map
    }
    val trailStateToIndex = remember(statesWithTrailheads, trailheadsGrouped) {
        var index = 0
        val map = mutableMapOf<String, Int>()
        statesWithTrailheads.forEach { state ->
            val trails = trailheadsGrouped[state.code].orEmpty()
            map[state.code] = index
            index += 1 + if (trails.isEmpty()) 1 else trails.size
        }
        map
    }

    ScreenScaffold(
        title = screenTitle,
        onBack = { navController.popBackStack() },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CampgroundTrailPillSwitcher(
                currentPage = pagerState.currentPage,
                onPageSelected = { page ->
                    scope.launch { pagerState.animateScrollToPage(page) }
                },
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 0.dp,
            ) { page ->
                when (page) {
                    0 -> CampgroundsPage(
                        navController = navController,
                        campgroundsGrouped = campgroundsGrouped,
                        statesWithCampgrounds = statesWithCampgrounds,
                        listState = campListState,
                        stateToIndex = campStateToIndex,
                        onStateSelected = { code ->
                            campStateToIndex[code]?.let { targetIndex ->
                                scope.launch { campListState.animateScrollToItem(targetIndex) }
                            }
                        },
                    )
                    1 -> TrailheadsPage(
                        navController = navController,
                        trailheadsGrouped = trailheadsGrouped,
                        statesWithTrailheads = statesWithTrailheads,
                        listState = trailListState,
                        stateToIndex = trailStateToIndex,
                        onStateSelected = { code ->
                            trailStateToIndex[code]?.let { targetIndex ->
                                scope.launch { trailListState.animateScrollToItem(targetIndex) }
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun CampgroundTrailPillSwitcher(
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
        val pills = listOf("Campgrounds" to 0, "Trailheads" to 1)
        pills.forEach { (label, page) ->
            val isActive = currentPage == page
            val accent = if (page == 0) Citrine else Success
            Box(
                modifier = Modifier
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
                    .padding(horizontal = 28.dp, vertical = 8.dp),
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isActive) accent else TextLow,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun CampgroundsPage(
    navController: NavController,
    campgroundsGrouped: Map<String, List<BlmCampground>>,
    statesWithCampgrounds: List<com.rork.rockscout.data.BlmState>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    stateToIndex: Map<String, Int>,
    onStateSelected: (String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            statesWithCampgrounds.forEach { state ->
                val camps = campgroundsGrouped[state.code].orEmpty()
                item(key = "camp_header_${state.code}") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Terrain, contentDescription = null, tint = Citrine, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(state.name, style = MaterialTheme.typography.titleMedium, color = TextHigh, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Text("(${camps.size})", style = MaterialTheme.typography.labelSmall, color = TextLow)
                    }
                }
                if (camps.isEmpty()) {
                    item(key = "camp_empty_${state.code}") {
                        Text(
                            "No campgrounds documented for ${state.name}. Dispersed camping may be allowed — check with the local field office.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextLow,
                            modifier = Modifier.padding(start = 26.dp, bottom = 4.dp),
                        )
                    }
                } else {
                    items(camps, key = { "${it.state}_${it.name}" }) { camp ->
                        CampgroundCard(campground = camp) { navController.navigate(Routes.blmCampground(camp.name)) }
                    }
                }
            }
        }
        StatePickerPill(
            states = statesWithCampgrounds.map { it.code to it.name },
            onStateSelected = onStateSelected,
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 20.dp, top = 4.dp),
        )
    }
}

@Composable
private fun TrailheadsPage(
    navController: NavController,
    trailheadsGrouped: Map<String, List<BlmTrailhead>>,
    statesWithTrailheads: List<com.rork.rockscout.data.BlmState>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    stateToIndex: Map<String, Int>,
    onStateSelected: (String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            statesWithTrailheads.forEach { state ->
                val trails = trailheadsGrouped[state.code].orEmpty()
                item(key = "trail_header_${state.code}") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Hiking, contentDescription = null, tint = Success, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(state.name, style = MaterialTheme.typography.titleMedium, color = TextHigh, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Text("(${trails.size})", style = MaterialTheme.typography.labelSmall, color = TextLow)
                    }
                }
                if (trails.isEmpty()) {
                    item(key = "trail_empty_${state.code}") {
                        Text(
                            "No trailheads documented for ${state.name}. Check with the local field office for access points.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextLow,
                            modifier = Modifier.padding(start = 26.dp, bottom = 4.dp),
                        )
                    }
                } else {
                    items(trails, key = { "${it.state}_${it.name}" }) { trail ->
                        TrailheadCard(trailhead = trail) { navController.navigate(Routes.blmTrailhead(trail.name)) }
                    }
                }
            }
        }
        StatePickerPill(
            states = statesWithTrailheads.map { it.code to it.name },
            onStateSelected = onStateSelected,
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 20.dp, top = 4.dp),
        )
    }
}

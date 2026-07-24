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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.BlmData
import com.rork.rockscout.data.BlmTrailhead
import com.rork.rockscout.data.BlmCampground
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.StatePickerPill
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.launch

@Composable
fun BlmTrailheadsScreen(navController: NavController) {
    val trailheads = remember { BlmData.trailheads }
    val grouped = remember(trailheads) { trailheads.groupBy { it.state } }
    val allStates = remember { BlmData.allStates }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Only states that have trailheads, in display order
    val statesWithTrailheads = remember(allStates, grouped) {
        allStates.filter { state -> grouped[state.code]?.isNotEmpty() == true }
    }

    // Map state code → item index in the LazyColumn
    val stateToItemIndex = remember(statesWithTrailheads, grouped) {
        var index = 0
        val map = mutableMapOf<String, Int>()
        statesWithTrailheads.forEach { state ->
            val trails = grouped[state.code].orEmpty()
            map[state.code] = index
            // header + empty msg or trail items
            index += 1 + if (trails.isEmpty()) 1 else trails.size
        }
        map
    }

    ScreenScaffold(
        title = "BLM Trailheads",
        onBack = { navController.popBackStack() },
        actions = {
            StatePickerPill(
                states = statesWithTrailheads.map { it.code to it.name },
                onStateSelected = { code ->
                    stateToItemIndex[code]?.let { targetIndex ->
                        scope.launch { listState.animateScrollToItem(targetIndex) }
                    }
                },
            )
        },
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            statesWithTrailheads.forEach { state ->
                val trails = grouped[state.code].orEmpty()
                item(key = "header_${state.code}") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Hiking, contentDescription = null, tint = Success, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(state.name, style = MaterialTheme.typography.titleMedium, color = TextHigh, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Text("(${trails.size})", style = MaterialTheme.typography.labelSmall, color = TextLow)
                    }
                }
                if (trails.isEmpty()) {
                    item(key = "empty_${state.code}") {
                        Text("No popular BLM trailheads documented for ${state.name}. Check with the local BLM field office for access points.", style = MaterialTheme.typography.bodySmall, color = TextLow, modifier = Modifier.padding(start = 26.dp, bottom = 4.dp))
                    }
                } else {
                    items(trails, key = { "${it.state}_${it.name}" }) { trail ->
                        TrailheadCard(trailhead = trail) { navController.navigate(Routes.blmTrailhead(trail.name)) }
                    }
                }
            }
        }
    }
}

@Composable
fun BlmCampgroundsScreen(navController: NavController) {
    val campgrounds = remember { BlmData.campgrounds }
    val grouped = remember(campgrounds) { campgrounds.groupBy { it.state } }
    val allStates = remember { BlmData.allStates }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val statesWithCampgrounds = remember(allStates, grouped) {
        allStates.filter { state -> grouped[state.code]?.isNotEmpty() == true }
    }

    val stateToItemIndex = remember(statesWithCampgrounds, grouped) {
        var index = 0
        val map = mutableMapOf<String, Int>()
        statesWithCampgrounds.forEach { state ->
            val camps = grouped[state.code].orEmpty()
            map[state.code] = index
            index += 1 + if (camps.isEmpty()) 1 else camps.size
        }
        map
    }

    ScreenScaffold(
        title = "BLM Campgrounds",
        onBack = { navController.popBackStack() },
        actions = {
            StatePickerPill(
                states = statesWithCampgrounds.map { it.code to it.name },
                onStateSelected = { code ->
                    stateToItemIndex[code]?.let { targetIndex ->
                        scope.launch { listState.animateScrollToItem(targetIndex) }
                    }
                },
            )
        },
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            statesWithCampgrounds.forEach { state ->
                val camps = grouped[state.code].orEmpty()
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
                        Text("No popular BLM campgrounds documented for ${state.name}. Dispersed camping may still be allowed on BLM land — check with the local field office.", style = MaterialTheme.typography.bodySmall, color = TextLow, modifier = Modifier.padding(start = 26.dp, bottom = 4.dp))
                    }
                } else {
                    items(camps, key = { "${it.state}_${it.name}" }) { camp ->
                        CampgroundCard(campground = camp) { navController.navigate(Routes.blmCampground(camp.name)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrailheadCard(trailhead: BlmTrailhead, onClick: () -> Unit) {
    val accent = Success
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier.fillMaxWidth().sculpted(shape = shape, accent = accent, shadowElevation = 6.dp, onClick = onClick).clip(shape)
            .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.58f), Color.Black.copy(alpha = 0.68f), Color.Black.copy(alpha = 0.78f))))
            .glowingBorder(3.dp, accent.copy(alpha = 0.55f), shape),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(accent.copy(alpha = 0.16f)).glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Explore, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
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
}

@Composable
private fun CampgroundCard(campground: BlmCampground, onClick: () -> Unit) {
    val accent = Citrine
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier.fillMaxWidth().sculpted(shape = shape, accent = accent, shadowElevation = 6.dp, onClick = onClick).clip(shape)
            .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.58f), Color.Black.copy(alpha = 0.68f), Color.Black.copy(alpha = 0.78f))))
            .glowingBorder(3.dp, accent.copy(alpha = 0.55f), shape),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(accent.copy(alpha = 0.16f)).glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Terrain, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(campground.name, style = MaterialTheme.typography.titleMedium, color = DarkTextHigh, fontWeight = FontWeight.Bold)
                    Text(campground.region, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                    Spacer(Modifier.height(6.dp))
                    Text(campground.description, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                    Spacer(Modifier.height(8.dp))
                    TagChip(text = campground.feeInfo, color = accent)
                }
            }
        }
    }
}

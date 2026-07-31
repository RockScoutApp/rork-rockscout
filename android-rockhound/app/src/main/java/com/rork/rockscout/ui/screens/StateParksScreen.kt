package com.rork.rockscout.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.BlmData
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.data.StatePark
import com.rork.rockscout.data.StateParkData
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.StatePickerPill
import com.rork.rockscout.ui.components.NewBadge
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import kotlinx.coroutines.launch

@Composable
fun StateParksScreen(navController: NavController) {
    val parks = remember { StateParkData.allParks }
    val grouped = remember(parks) {
        parks.groupBy { it.state }.mapValues { (_, stateParks) ->
            // National parks first, then state parks — preserve existing order within each group
            val (national, state) = stateParks.partition { it.isNationalPark }
            national + state
        }
    }
    val allStates = remember { BlmData.allStates }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var showAddLocation by remember { mutableStateOf(false) }
    var addLocationMessage by remember { mutableStateOf<String?>(null) }

    // Build list of states that actually have parks, in the order they appear
    val statesWithParks = remember(allStates, grouped) {
        allStates.filter { state -> grouped[state.code]?.isNotEmpty() == true }
    }

    // Map state code to item index in the LazyColumn for scrolling
    val stateToItemIndex = remember(statesWithParks, grouped) {
        var index = 0
        val map = mutableMapOf<String, Int>()
        statesWithParks.forEach { state ->
            val stateParks = grouped[state.code].orEmpty()
            map[state.code] = index
            index += 1 + stateParks.size
        }
        map
    }

    ScreenScaffold(
        title = "National / State Parks",
        onBack = { navController.popBackStack() },
        actions = {
            StatePickerPill(
                states = statesWithParks.map { it.code to it.name },
                onStateSelected = { code ->
                    stateToItemIndex[code]?.let { targetIndex ->
                        scope.launch { listState.animateScrollToItem(targetIndex) }
                    }
                },
            )
            Spacer(Modifier.width(8.dp))
            SculptedIconButton(
                icon = Icons.Filled.AddLocation,
                contentDescription = "Upload New Park",
                onClick = { showAddLocation = true },
                accent = Success,
                iconTint = Success,
                size = 40.dp,
                shadowElevation = 4.dp,
            )
        },
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            statesWithParks.forEach { state ->
                val stateParks = grouped[state.code].orEmpty()
                val nationalCount = stateParks.count { it.isNationalPark }
                item(key = "park_header_${state.code}") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Park, contentDescription = null, tint = Success, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(state.name, style = MaterialTheme.typography.titleMedium, color = TextHigh, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Text("(${stateParks.size})", style = MaterialTheme.typography.labelSmall, color = TextLow)
                        if (nationalCount > 0) {
                            Spacer(Modifier.width(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Stars, contentDescription = null, tint = Amethyst, modifier = Modifier.size(12.dp))
                                Spacer(Modifier.width(2.dp))
                                Text("$nationalCount National Park${if (nationalCount == 1) "" else "s"}", style = MaterialTheme.typography.labelSmall, color = Amethyst, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
                items(stateParks, key = { it.id }) { park ->
                    StateParkCard(park = park) {
                        navController.navigate(Routes.stateParkDetail(park.id))
                    }
                }
            }
        }
    }

    if (showAddLocation) {
        AddLocationDialog(
            onDismiss = { showAddLocation = false },
            onSubmitted = { approved ->
                addLocationMessage = if (approved) {
                    "Park web-verified and submitted!"
                } else {
                    "Park submitted for review!"
                }
                showAddLocation = false
            },
            submissionMode = "park",
        )
    }

    addLocationMessage?.let { msg ->
        androidx.compose.material3.SnackbarHost(
            hostState = remember { androidx.compose.material3.SnackbarHostState() }.also {
                androidx.compose.runtime.LaunchedEffect(msg) {
                    it.showSnackbar(msg)
                    addLocationMessage = null
                }
            },
        )
    }
}

@Composable
private fun StateParkCard(park: StatePark, onClick: () -> Unit) {
    val accent = if (park.isNationalPark) Amethyst else Success
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier.fillMaxWidth().sculpted(shape = shape, accent = accent, shadowElevation = 6.dp, onClick = onClick).clip(shape)
            .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.58f), Color.Black.copy(alpha = 0.68f), Color.Black.copy(alpha = 0.78f))))
            .glowingBorder(3.dp, accent.copy(alpha = 0.55f), shape),
    ) {
        if (park.isNew()) {
            NewBadge(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp))
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(accent.copy(alpha = 0.16f)).glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(
                        if (park.isNationalPark) Icons.Filled.Stars else Icons.Filled.Park,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(park.name, style = MaterialTheme.typography.titleMedium, color = DarkTextHigh, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    Text(park.region, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                    Spacer(Modifier.height(6.dp))
                    Text(park.description, style = MaterialTheme.typography.bodySmall, color = DarkTextMid, maxLines = 3, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (park.isNationalPark) {
                            TagChip(text = "National Park", color = accent)
                        }
                        if (park.hasCamping) TagChip(text = "Camping", color = accent)
                        TagChip(text = park.feeInfo, color = accent)
                    }
                }
            }
        }
    }
}

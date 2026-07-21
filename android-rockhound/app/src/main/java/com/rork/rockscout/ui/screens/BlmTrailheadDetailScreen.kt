package com.rork.rockscout.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.BlmCampground
import com.rork.rockscout.data.BlmData
import com.rork.rockscout.data.BlmTrailhead
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid

// ── Trailhead Detail ──────────────────────────────────────────────────────

@Composable
fun BlmTrailheadDetailScreen(
    navController: NavController,
    trailheadName: String,
) {
    val trailhead = remember(trailheadName) {
        BlmData.trailheads.firstOrNull { it.name == trailheadName }
    }

    if (trailhead == null) {
        ScreenScaffold(title = "Trailhead", onBack = { navController.popBackStack() }) {
            Box(Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
                Text("Trailhead information not found.", color = TextMid)
            }
        }
        return
    }

    val state = remember(trailhead.state) {
        BlmData.allStates.firstOrNull { it.code == trailhead.state }
    }
    val accent = Success
    val context = LocalContext.current

    ScreenScaffold(
        title = trailhead.name,
        onBack = { navController.popBackStack() },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp, end = 20.dp, bottom = 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header card
            item {
                DarkCard(accent = accent, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(accent.copy(alpha = 0.18f))
                                .glowingBorder(2.dp, accent.copy(alpha = 0.45f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Explore, contentDescription = null, tint = accent, modifier = Modifier.size(26.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                trailhead.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = DarkTextHigh,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                trailhead.region,
                                style = MaterialTheme.typography.bodyMedium,
                                color = accent,
                            )
                        }
                    }
                }
            }

            // Description
            item {
                InfoSectionCard(
                    title = "About This Trailhead",
                    accent = accent,
                    content = trailhead.description,
                )
            }

            // State info link
            if (state != null) {
                item {
                    DarkCard(accent = accent, modifier = Modifier.fillMaxWidth().clickable {
                        navController.navigate(com.rork.rockscout.ui.navigation.Routes.blmState(state.code))
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${state.name} BLM Guide",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = DarkTextHigh,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    "Tap to view collecting rules, limits, and permits for ${state.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkTextMid,
                                )
                            }
                        }
                    }
                }
            }

            // Coordinates card
            item {
                DarkCard(accent = accent, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Map, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Coordinates",
                                style = MaterialTheme.typography.titleSmall,
                                color = DarkTextHigh,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                "%.4f, %.4f".format(trailhead.latitude, trailhead.longitude),
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextMid,
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(accent.copy(alpha = 0.15f))
                            .glowingBorder(2.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .clickable {
                                val geoUri = "geo:${trailhead.latitude},${trailhead.longitude}?q=${trailhead.latitude},${trailhead.longitude}(${Uri.encode(trailhead.name)})"
                                SafeLinkOpener.openGeo(context, geoUri)
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Open in Maps",
                            style = MaterialTheme.typography.labelLarge,
                            color = accent,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            // Safety reminder
            item {
                DarkCard(accent = Citrine, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Before You Go",
                        style = MaterialTheme.typography.titleSmall,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Always verify trailhead access and road conditions with the local BLM field office. Many BLM roads are unimproved and may be impassable after rain. Bring plenty of water, a first aid kit, and tell someone your plans.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                    )
                }
            }
        }
    }
}

// ── Campground Detail ─────────────────────────────────────────────────────

@Composable
fun BlmCampgroundDetailScreen(
    navController: NavController,
    campgroundName: String,
) {
    val campground = remember(campgroundName) {
        BlmData.campgrounds.firstOrNull { it.name == campgroundName }
    }

    if (campground == null) {
        ScreenScaffold(title = "Campground", onBack = { navController.popBackStack() }) {
            Box(Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
                Text("Campground information not found.", color = TextMid)
            }
        }
        return
    }

    val state = remember(campground.state) {
        BlmData.allStates.firstOrNull { it.code == campground.state }
    }
    val accent = Citrine
    val context = LocalContext.current

    ScreenScaffold(
        title = campground.name,
        onBack = { navController.popBackStack() },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp, end = 20.dp, bottom = 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header card
            item {
                DarkCard(accent = accent, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(accent.copy(alpha = 0.18f))
                                .glowingBorder(2.dp, accent.copy(alpha = 0.45f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Terrain, contentDescription = null, tint = accent, modifier = Modifier.size(26.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                campground.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = DarkTextHigh,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                campground.region,
                                style = MaterialTheme.typography.bodyMedium,
                                color = accent,
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    TagChip(text = campground.feeInfo, color = accent)
                }
            }

            // Description
            item {
                InfoSectionCard(
                    title = "About This Campground",
                    accent = accent,
                    content = campground.description,
                )
            }

            // State info link
            if (state != null) {
                item {
                    DarkCard(accent = accent, modifier = Modifier.fillMaxWidth().clickable {
                        navController.navigate(com.rork.rockscout.ui.navigation.Routes.blmState(state.code))
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${state.name} BLM Guide",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = DarkTextHigh,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    "Tap to view collecting rules, limits, and permits for ${state.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkTextMid,
                                )
                            }
                        }
                    }
                }
            }

            // Coordinates card
            item {
                DarkCard(accent = accent, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Map, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Coordinates",
                                style = MaterialTheme.typography.titleSmall,
                                color = DarkTextHigh,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                "%.4f, %.4f".format(campground.latitude, campground.longitude),
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextMid,
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(accent.copy(alpha = 0.15f))
                            .glowingBorder(2.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .clickable {
                                val geoUri = "geo:${campground.latitude},${campground.longitude}?q=${campground.latitude},${campground.longitude}(${Uri.encode(campground.name)})"
                                SafeLinkOpener.openGeo(context, geoUri)
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Open in Maps",
                            style = MaterialTheme.typography.labelLarge,
                            color = accent,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            // Camping tips
            item {
                DarkCard(accent = Citrine, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Camping Tips",
                        style = MaterialTheme.typography.titleSmall,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Dispersed camping on BLM land is generally allowed up to 14 days. Always check with the local BLM field office for current conditions and fire restrictions. Bring all necessary supplies — many BLM campgrounds have no water or hookups. Pack out all trash and practice Leave No Trace.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                    )
                }
            }
        }
    }
}

// ── Shared component ───────────────────────────────────────────────────────

@Composable
private fun InfoSectionCard(
    title: String,
    accent: Color,
    content: String,
) {
    DarkCard(accent = accent, modifier = Modifier.fillMaxWidth()) {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            color = DarkTextHigh,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            content,
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
    }
}

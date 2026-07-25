package com.rork.rockscout.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.BlmData
import com.rork.rockscout.data.BlmInfoSection
import com.rork.rockscout.data.BlmState
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.ui.components.InlineContentImage
import com.rork.rockscout.ui.components.BookStyleImage
import com.rork.rockscout.ui.components.BLM_IMG_ROCKHOUND
import com.rork.rockscout.ui.components.BLM_IMG_CANYON
import com.rork.rockscout.ui.components.BLM_IMG_TRAILHEAD
import com.rork.rockscout.ui.components.BLM_IMG_CAMPING
import com.rork.rockscout.ui.components.BLM_IMG_ROCK_HAMMER
import com.rork.rockscout.ui.components.BLM_IMG_DESERT_VARNISH
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.StateSilhouette
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow

@Composable
fun BlmLandScreen(navController: NavController) {
    ScreenScaffold(title = "BLM Land", onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp),
        ) {
            // State grid header
            item {
                Text(
                    "State-by-State Collecting Rules",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextHigh,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 4.dp),
                )
                Text(
                    "Tap a state to see collecting rules, limits, dig sites, and local BLM contacts.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 4.dp),
                )
            }
            // State grid — rendered as a non-scrolling Column of Rows so the grid
            // occupies only the exact space it needs. The "Important Info" header is
            // placed inside the same LazyColumn item so there is zero inter-item gap
            // between the grid and the info cards below it.
            item {
                val states = remember { BlmData.allStates }
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    states.chunked(2).forEach { rowStates ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            rowStates.forEach { state ->
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.TopStart,
                                ) {
                                    StateCard(state = state) {
                                        navController.navigate(Routes.blmState(state.code))
                                    }
                                }
                            }
                            if (rowStates.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                // Important Info header immediately follows the state grid with no
                // extra spacing, eliminating the perceived dead space on the BLM screen.
                Text(
                    "Important Info",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextHigh,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 8.dp),
                )
            }
            items(BlmData.infoSections, key = { it.title }) { section ->
                InfoSectionCard(section = section)
            }
            // Inline content images
            item {
                InlineContentImage(
                    imageUrl = BLM_IMG_ROCKHOUND,
                    contentDescription = "Rockhound collecting rocks and minerals on BLM desert land",
                    caption = "Collecting on BLM land — always verify local rules first",
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
                )
            }
            item {
                InlineContentImage(
                    imageUrl = BLM_IMG_CANYON,
                    contentDescription = "Desert canyon with exposed colorful rock layers on BLM land",
                    caption = "Exposed rock layers — reading the geology helps you find specimens",
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
                )
            }
            item {
                InlineContentImage(
                    imageUrl = BLM_IMG_TRAILHEAD,
                    contentDescription = "BLM trailhead information sign in the desert",
                    caption = "Trailhead signs mark access points to BLM collecting areas",
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
                )
            }
            item {
                InlineContentImage(
                    imageUrl = BLM_IMG_CAMPING,
                    contentDescription = "Dispersed camping on BLM land with tent and truck",
                    caption = "Dispersed camping — free on most BLM land, up to 14 days",
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
                )
            }
            // Disclaimer + BLM.gov link
            item {
                BlmDisclaimerCard()
            }
        }
    }
}

@Composable
private fun StateCard(state: BlmState, onClick: () -> Unit) {
    val accent = Color(state.accentHex)
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = shape, accent = accent, shadowElevation = 6.dp, onClick = onClick)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.58f), Color.Black.copy(alpha = 0.68f), Color.Black.copy(alpha = 0.78f))
                )
            )
            .glowingBorder(3.dp, accent.copy(alpha = 0.55f), shape),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Text(state.name, style = MaterialTheme.typography.titleMedium, color = DarkTextHigh, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(state.blmAcreage, style = MaterialTheme.typography.labelSmall, color = accent, maxLines = 2)
                }
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = DarkTextMid, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun InfoSectionCard(section: BlmInfoSection) {
    val accent = Color(section.accentHex)
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
            .sculpted(shape = shape, accent = accent, shadowElevation = 6.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.58f), Color.Black.copy(alpha = 0.68f), Color.Black.copy(alpha = 0.78f))
                )
            )
            .glowingBorder(3.dp, accent.copy(alpha = 0.55f), shape),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(section.icon, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.width(10.dp))
                Text(section.title, style = MaterialTheme.typography.titleMedium, color = DarkTextHigh, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            section.points.forEachIndexed { index, point ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(accent).glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape).padding(top = 8.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(point, style = MaterialTheme.typography.bodySmall, color = DarkTextMid, modifier = Modifier.weight(1f))
                }
                if (index == 1 && section.title.contains("Collecting", ignoreCase = true)) {
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        BookStyleImage(imageUrl = BLM_IMG_ROCK_HAMMER, contentDescription = "Rock hammer chipping at a mineral vein in desert rock")
                        Spacer(Modifier.width(10.dp))
                        Text("\u2192 A good rock hammer is your most important BLM tool.", style = MaterialTheme.typography.bodySmall, color = TextLow, fontStyle = FontStyle.Italic, modifier = Modifier.weight(1f))
                    }
                }
                if (index == 2 && section.title.contains("Rules", ignoreCase = true)) {
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Text("\u2192 Desert varnish coats many BLM rocks \u2014 look underneath for the true color.", style = MaterialTheme.typography.bodySmall, color = TextLow, fontStyle = FontStyle.Italic, modifier = Modifier.weight(1f).padding(end = 10.dp))
                        BookStyleImage(imageUrl = BLM_IMG_DESERT_VARNISH, contentDescription = "Desert varnish on a boulder in the American Southwest")
                    }
                }
            }
        }
    }
}

@Composable
private fun BlmDisclaimerCard() {
    val context = LocalContext.current
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp)
            .sculpted(shape = shape, accent = Citrine, shadowElevation = 6.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.58f), Color.Black.copy(alpha = 0.68f), Color.Black.copy(alpha = 0.78f))
                )
            )
            .glowingBorder(3.dp, Citrine.copy(alpha = 0.55f), shape),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Rules can change", style = MaterialTheme.typography.titleMedium, color = Citrine, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("The information in this guide is curated from BLM.gov and is for general reference only. Rules, limits, and area closures can change at any time. Always verify current regulations with the local BLM field office before collecting.", style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Citrine.copy(alpha = 0.15f))
                    .glowingBorder(2.dp, Citrine.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable { SafeLinkOpener.openUrl(context, "https://www.blm.gov/programs/recreation/rockhounding") }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = Citrine, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text("Visit BLM.gov Rockhounding", style = MaterialTheme.typography.titleSmall, color = Citrine, fontWeight = FontWeight.Bold)
            }
        }
    }
}

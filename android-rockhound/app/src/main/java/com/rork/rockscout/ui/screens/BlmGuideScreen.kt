package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.HolidayVillage
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.navigation.NavController
import com.rork.rockscout.ui.components.GlobalSearchSection
import com.rork.rockscout.ui.components.InlineContentImage
import com.rork.rockscout.ui.components.LandingTile
import com.rork.rockscout.ui.components.LandingTileData
import com.rork.rockscout.ui.components.BLM_HERO_URL
import com.rork.rockscout.ui.components.BLM_IMG_PANORAMIC
import com.rork.rockscout.ui.components.BLM_TILE_LAND
import com.rork.rockscout.ui.components.BLM_TILE_PARKS
import com.rork.rockscout.ui.components.BLM_TILE_TRAILHEADS
import com.rork.rockscout.ui.components.BLM_TILE_CAMPGROUNDS
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextMid

private const val BLM_CAMPFIRE_BACKGROUND_URL = BLM_HERO_URL

@Composable
fun BlmGuideScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 40.dp),
        ) {
            // Search
            item {
                GlobalSearchSection(
                    navController = navController,
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search BLM states, trailheads, camps, specimens, rocks…",
                )
            }
            // Intro text
            item {
                Text(
                    "Rockhounding on Bureau of Land Management public lands. Rules vary by state and site — always verify with the local BLM office before collecting.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMid,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 8.dp),
                )
            }
            // Intro image
            item {
                InlineContentImage(
                    imageUrl = BLM_IMG_PANORAMIC,
                    contentDescription = "Panoramic view of BLM public lands in the American West",
                    caption = "BLM public lands — millions of acres open to rockhounding",
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
                )
            }
            // 2x2 tile grid
            item {
                Column(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        LandingTile(
                            tile = LandingTileData(
                                label = "BLM Land",
                                subtitle = "State rules, dig sites & info",
                                icon = Icons.Filled.Terrain,
                                accent = Color(0xFFC97B4A),
                                route = Routes.BLM_LAND,
                                imageUrl = BLM_TILE_LAND,
                            ),
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Routes.BLM_LAND) },
                        )
                        LandingTile(
                            tile = LandingTileData(
                                label = "State Parks",
                                subtitle = "Parks with geological interest",
                                icon = Icons.Filled.Park,
                                accent = Color(0xFF7BA868),
                                route = Routes.STATE_PARKS,
                                imageUrl = BLM_TILE_PARKS,
                            ),
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Routes.STATE_PARKS) },
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        LandingTile(
                            tile = LandingTileData(
                                label = "Trailheads",
                                subtitle = "Access points to collecting areas",
                                icon = Icons.Filled.Hiking,
                                accent = Success,
                                route = Routes.BLM_TRAILHEADS,
                                imageUrl = BLM_TILE_TRAILHEADS,
                            ),
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Routes.BLM_TRAILHEADS) },
                        )
                        LandingTile(
                            tile = LandingTileData(
                                label = "Campgrounds",
                                subtitle = "Camp near rockhounding sites",
                                icon = Icons.Filled.HolidayVillage,
                                accent = Citrine,
                                route = Routes.BLM_CAMPGROUNDS,
                                imageUrl = BLM_TILE_CAMPGROUNDS,
                            ),
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Routes.BLM_CAMPGROUNDS) },
                        )
                    }
                }
            }
        }
    }
}

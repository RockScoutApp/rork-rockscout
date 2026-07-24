package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Science
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.navigation.NavController
import com.rork.rockscout.ui.components.InterstitialAdTrigger
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.LandingTile
import com.rork.rockscout.ui.components.LandingTileData
import com.rork.rockscout.ui.components.GEO_TILE_ROCK_TYPES
import com.rork.rockscout.ui.components.GEO_TILE_MINERAL_ID
import com.rork.rockscout.ui.components.GEO_TILE_CRYSTALS
import com.rork.rockscout.ui.components.GEO_TILE_ROCK_CYCLE
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.TextMid

@Composable
fun RockInfoScreen(navController: NavController) {
    InterstitialAdTrigger(screenKey = "rockinfo") {
        navController.navigate(Routes.PAYWALL)
    }
    ScreenScaffold(title = "Exploring Geology", onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                            )
                        )
                        .padding(20.dp),
                ) {
                    Text(
                        "From molten magma to polished gem, every rock and mineral tells a story. Explore the five great categories of geology below — plus the tools and techniques geologists use to identify what you find in the field.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextMid,
                    )
                }
            }

            // 2x2 grid of landing tiles
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        LandingTile(
                            tile = LandingTileData(
                                label = "Rock Types",
                                subtitle = "Igneous, sedimentary & metamorphic",
                                icon = Icons.Filled.Category,
                                accent = Color(0xFFE5683C),
                                route = Routes.ROCK_TYPES,
                                imageUrl = GEO_TILE_ROCK_TYPES,
                            ),
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Routes.ROCK_TYPES) },
                        )
                        LandingTile(
                            tile = LandingTileData(
                                label = "Mineral ID",
                                subtitle = "8-step field identification process",
                                icon = Icons.Filled.Science,
                                accent = Color(0xFFD9B26A),
                                route = Routes.MINERAL_ID,
                                imageUrl = GEO_TILE_MINERAL_ID,
                            ),
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Routes.MINERAL_ID) },
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        LandingTile(
                            tile = LandingTileData(
                                label = "Crystal Systems",
                                subtitle = "7 systems & Mohs hardness scale",
                                icon = Icons.Filled.Diamond,
                                accent = Color(0xFF9B7BD8),
                                route = Routes.CRYSTAL_HARDNESS,
                                imageUrl = GEO_TILE_CRYSTALS,
                            ),
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Routes.CRYSTAL_HARDNESS) },
                        )
                        LandingTile(
                            tile = LandingTileData(
                                label = "Rock Cycle & Tools",
                                subtitle = "The cycle & essential field kit",
                                icon = Icons.Filled.Loop,
                                accent = Color(0xFF8BBF6A),
                                route = Routes.ROCK_CYCLE_TOOLS,
                                imageUrl = GEO_TILE_ROCK_CYCLE,
                            ),
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Routes.ROCK_CYCLE_TOOLS) },
                        )
                    }
                }
            }
        }
    }
}

package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.navigation.NavController
import com.rork.rockscout.ui.components.InterstitialAdTrigger
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.LandingTile
import com.rork.rockscout.ui.components.LandingTileData
import com.rork.rockscout.ui.components.PALEO_TILE_TIME_SCALE
import com.rork.rockscout.ui.components.PALEO_TILE_EXTINCTIONS
import com.rork.rockscout.ui.components.PALEO_TILE_FOSSIL_TYPES
import com.rork.rockscout.ui.components.PALEO_TILE_PERIODS
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Fossil
import com.rork.rockscout.ui.theme.TextMid

@Composable
fun PaleontologyScreen(navController: NavController) {
    InterstitialAdTrigger(screenKey = "paleontology") {
        navController.navigate(Routes.PAYWALL)
    }

    ScreenScaffold(title = "Exploring Paleontology", onBack = { navController.popBackStack() }) {
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
                        "Explore the history of life on Earth, from the Cambrian explosion to the last Ice Age. Each period tells the story of life evolving through deep time — over 3.7 billion years of evolution, extinction, and renewal.",
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
                                label = "Geologic Time Scale",
                                subtitle = "4.6 billion years of Earth history",
                                icon = Icons.Filled.Public,
                                accent = Fossil,
                                route = Routes.GEO_TIME_SCALE,
                                imageUrl = PALEO_TILE_TIME_SCALE,
                            ),
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Routes.GEO_TIME_SCALE) },
                        )
                        LandingTile(
                            tile = LandingTileData(
                                label = "Mass Extinctions",
                                subtitle = "The Big Five + the Sixth",
                                icon = Icons.Filled.Thunderstorm,
                                accent = Color(0xFFE2574C),
                                route = Routes.MASS_EXTINCTIONS,
                                imageUrl = PALEO_TILE_EXTINCTIONS,
                            ),
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Routes.MASS_EXTINCTIONS) },
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        LandingTile(
                            tile = LandingTileData(
                                label = "Fossilization & Types",
                                subtitle = "How fossils form & 7 types",
                                icon = Icons.Filled.Pets,
                                accent = Color(0xFF9B7BD8),
                                route = Routes.FOSSIL_TYPES_SCREEN,
                                imageUrl = PALEO_TILE_FOSSIL_TYPES,
                            ),
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Routes.FOSSIL_TYPES_SCREEN) },
                        )
                        LandingTile(
                            tile = LandingTileData(
                                label = "Geologic Periods",
                                subtitle = "Paleozoic, Mesozoic & Cenozoic",
                                icon = Icons.Filled.HistoryEdu,
                                accent = Color(0xFF8BBF6A),
                                route = Routes.GEOLOGIC_PERIODS,
                                imageUrl = PALEO_TILE_PERIODS,
                            ),
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Routes.GEOLOGIC_PERIODS) },
                        )
                    }
                }
            }
        }
    }
}

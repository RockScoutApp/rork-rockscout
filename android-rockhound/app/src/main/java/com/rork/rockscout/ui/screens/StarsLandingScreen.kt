package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.navigation.NavController
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.LandingTile
import com.rork.rockscout.ui.components.LandingTileData
import com.rork.rockscout.ui.components.TwinklingStars
import com.rork.rockscout.ui.navigation.Routes

/** Stars landing tile image URLs. */
const val STARS_TILE_CONSTELLATIONS = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/a8000b2e-2f29-4f34-8556-f34677e225bb.png"
const val STARS_TILE_STARS = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/4b813c02-8b09-4730-8ed9-1a47c309c452.png"
const val STARS_TILE_PLANETS = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/d9a10489-d9d4-411d-b562-688018fd91c0.png"
const val STARS_TILE_DSO = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/507979b3-327f-488c-a93a-90ff6423bad8.png"

private val AuroraGreen = Color(0xFF00E5C9)
private val AuroraPurple = Color(0xFF9B7BD8)
private val AuroraBlue = Color(0xFF5CC98C)
private val CitrineColor = Color(0xFFD4C84A)

@Composable
fun StarsLandingScreen(navController: NavController) {
    ScreenScaffold(title = "Stars & Constellations", onBack = { navController.popBackStack() }) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Twinkling stars background
            TwinklingStars(
                modifier = Modifier.fillMaxSize(),
                starCount = 50,
            )

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
                                    listOf(Color(0xFF0D0C08), Color(0xFF1A1812))
                                )
                            )
                            .padding(20.dp),
                    ) {
                        Column {
                            Text(
                                text = "Night Sky Guide",
                                style = MaterialTheme.typography.headlineMedium,
                                color = AuroraGreen,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "Explore the wonders above — constellations, stars, planets, and deep sky objects.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f),
                            )
                        }
                    }
                }
                item {
                    val tiles = listOf(
                        LandingTileData(
                            label = "Constellations",
                            subtitle = "88 constellations · Star patterns of the night sky",
                            icon = Icons.Filled.Star,
                            accent = AuroraGreen,
                            route = Routes.CONSTELLATIONS,
                            imageUrl = STARS_TILE_CONSTELLATIONS,
                        ),
                        LandingTileData(
                            label = "Important Stars",
                            subtitle = "30 brightest stars · Navigation & spectral guides",
                            icon = Icons.Filled.Lightbulb,
                            accent = AuroraPurple,
                            route = Routes.IMPORTANT_STARS,
                            imageUrl = STARS_TILE_STARS,
                        ),
                        LandingTileData(
                            label = "Planets",
                            subtitle = "10 solar system bodies · What's visible tonight",
                            icon = Icons.Filled.Public,
                            accent = AuroraBlue,
                            route = Routes.PLANETS,
                            imageUrl = STARS_TILE_PLANETS,
                        ),
                        LandingTileData(
                            label = "Deep Sky Objects",
                            subtitle = "Galaxies, nebulae & star clusters",
                            icon = Icons.Filled.NightsStay,
                            accent = CitrineColor,
                            route = Routes.DEEP_SKY_OBJECTS,
                            imageUrl = STARS_TILE_DSO,
                        ),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        tiles.chunked(2).forEach { rowTiles ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                rowTiles.forEach { tile ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        LandingTile(
                                            tile = tile,
                                            onClick = { navController.navigate(tile.route) },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

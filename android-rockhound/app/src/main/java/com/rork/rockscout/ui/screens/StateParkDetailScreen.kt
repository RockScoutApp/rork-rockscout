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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.FavoriteSpotResolver
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.data.StatePark
import com.rork.rockscout.data.StateParkData
import com.rork.rockscout.data.WildlifeData
import com.rork.rockscout.ui.components.WildlifeCard
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid

@Composable
fun StateParkDetailScreen(
    navController: NavController,
    parkId: String,
) {
    val park = remember(parkId) {
        StateParkData.allParks.firstOrNull { it.id == parkId }
    }

    if (park == null) {
        ScreenScaffold(title = "State Park", onBack = { navController.popBackStack() }) {
            Box(Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
                Text("Park information not found.", color = TextMid)
            }
        }
        return
    }

    val accent = if (park.isNationalPark) Amethyst else Success
    val context = LocalContext.current
    val repo = AppRepository.instance
    val favorites by repo.favoriteSpots.collectAsStateWithLifecycle()
    val favId = FavoriteSpotResolver.parkId(park.id)
    val isFav = favorites.contains(favId)

    ScreenScaffold(title = park.name, onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header card with icon and name
            item {
                val shape = RoundedCornerShape(20.dp)
                Box(
                    modifier = Modifier.fillMaxWidth().sculpted(shape = shape, accent = accent, shadowElevation = 6.dp).clip(shape)
                        .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.58f), Color.Black.copy(alpha = 0.68f), Color.Black.copy(alpha = 0.78f))))
                        .glowingBorder(3.dp, accent.copy(alpha = 0.55f), shape),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(accent.copy(alpha = 0.16f)).glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(
                                    if (park.isNationalPark) Icons.Filled.Stars else Icons.Filled.Park,
                                    contentDescription = null,
                                    tint = accent,
                                    modifier = Modifier.size(28.dp),
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text(park.name, style = MaterialTheme.typography.titleLarge, color = DarkTextHigh, fontWeight = FontWeight.Bold)
                                Text(park.region, style = MaterialTheme.typography.labelMedium, color = accent, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(park.description, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid)
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (park.isNationalPark) {
                                TagChip(text = "National Park", color = accent)
                            }
                            TagChip(text = if (park.hasCamping) "Camping Available" else "Day Use", color = accent)
                            TagChip(text = park.feeInfo, color = accent)
                        }
                    }
                }
            }

            // Common Wildlife
            item {
                WildlifeCard(wildlife = WildlifeData.forState(park.state))
            }

            // Get directions button
            item {
                val shape = RoundedCornerShape(20.dp)
                Box(
                    modifier = Modifier.fillMaxWidth().sculpted(shape = shape, accent = Citrine, shadowElevation = 6.dp).clip(shape)
                        .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.58f), Color.Black.copy(alpha = 0.68f), Color.Black.copy(alpha = 0.78f))))
                        .glowingBorder(3.dp, Citrine.copy(alpha = 0.55f), shape),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Coordinates", style = MaterialTheme.typography.titleMedium, color = Citrine, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text("${String.format("%.4f", park.latitude)}, ${String.format("%.4f", park.longitude)}", style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Citrine.copy(alpha = 0.15f))
                                .glowingBorder(2.dp, Citrine.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable {
                                    SafeLinkOpener.openGeo(
                                        context,
                                        "geo:${park.latitude},${park.longitude}?q=${park.latitude},${park.longitude}(${park.name})",
                                    )
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Citrine, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("Get Directions", style = MaterialTheme.typography.titleSmall, color = Citrine, fontWeight = FontWeight.Bold)
                        }
                        // Favorite button
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Citrine.copy(alpha = 0.15f))
                                .glowingBorder(2.dp, Citrine.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable { repo.toggleFavoriteSpot(favId) }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                if (isFav) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = if (isFav) "Remove from favorites" else "Add to favorites",
                                tint = if (isFav) Citrine else TextLow,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                if (isFav) "Saved to Favorite Spots" else "Add to Favorite Spots",
                                style = MaterialTheme.typography.titleSmall,
                                color = if (isFav) Citrine else TextLow,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        // Website link
                        if (park.website != null) {
                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Success.copy(alpha = 0.15f))
                                    .glowingBorder(2.dp, Success.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .clickable { SafeLinkOpener.openUrl(context, park.website) }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = Success, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("Visit Park Website", style = MaterialTheme.typography.titleSmall, color = Success, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

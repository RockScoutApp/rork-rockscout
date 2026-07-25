package com.rork.rockscout.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.data.AchievementsRepository
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.FavoriteSpotEntry
import com.rork.rockscout.data.FavoriteSpotResolver
import com.rork.rockscout.data.IdentifyAccessManager
import com.rork.rockscout.data.PurchaseManager
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.XpSource
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.DeleteConfirmDialog
import com.rork.rockscout.ui.components.GlobalSearchSection
import com.rork.rockscout.ui.components.LockedFeatureBanner
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.ShareCardImage
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.SponsoredSpecimenPrompt
import com.rork.rockscout.ui.components.SpecimenGlyph
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextLow
import kotlinx.coroutines.launch

@Composable
fun FavoriteSpotsScreen(navController: NavController) {
    val repo = AppRepository.instance
    val favorites by repo.favoriteSpots.collectAsStateWithLifecycle()
    val current by repo.currentLocation.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val accessManager = IdentifyAccessManager.instance
    val purchaseManager = PurchaseManager.instance
    val isPremium by purchaseManager.isPremium.collectAsStateWithLifecycle()
    val isPayingUser = isPremium
    val wishlistIds by repo.wishlist.collectAsStateWithLifecycle()
    var shareToProfileLocId by remember { mutableStateOf<String?>(null) }
    var pendingUnbookmarkLocId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    ScreenScaffold(title = "My Favorite Spots", onBack = { navController.popBackStack() }) {
        if (favorites.isEmpty()) {
            EmptyState(
                emoji = "\uD83D\uDCCD",
                title = "No saved spots yet",
                message = "Bookmark dig sites, state parks, BLM areas, trailheads, and campgrounds from their detail pages to keep them one tap away.",
            )
        } else {
            // Resolve all favorite IDs into unified entries; drop any that can't be resolved.
            val resolvedFavorites = remember(favorites) {
                favorites.mapNotNull { FavoriteSpotResolver.resolve(it) }
            }
            val sortedFavorites = remember(resolvedFavorites) {
                resolvedFavorites.sortedBy { it.name.lowercase() }
            }
            Column(modifier = Modifier.fillMaxSize()) {
                GlobalSearchSection(
                    navController = navController,
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search your favorite spots, dig sites, BLM areas…",
                )
                SponsoredSpecimenPrompt(
                    isPayingUser = isPayingUser,
                    userLat = current.first,
                    userLng = current.second,
                    wishlistIds = wishlistIds,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 4.dp),
                )
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                item {
                    Text(
                        "Dig sites, state parks, BLM areas, trailheads, and campgrounds you've saved. Tap any to open its detail page.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMid,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                    Text(
                        "${sortedFavorites.size} spots saved for your next trip.",
                        style = MaterialTheme.typography.labelMedium,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
                items(sortedFavorites.size) { index ->
                    val entry = sortedFavorites[index]
                    val miles = AppRepository.distanceMiles(current.first, current.second, entry.latitude, entry.longitude)
                    DarkCard(
                        accent = entry.accent,
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate(entry.route) },
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SpecimenGlyph(emoji = entry.emoji, accent = entry.accent, size = 50)
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(entry.name, style = MaterialTheme.typography.titleLarge, color = Color.White)
                                Text(entry.region, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid)
                                Spacer(Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (entry.latitude != 0.0 && entry.longitude != 0.0) {
                                        Icon(Icons.Filled.NearMe, contentDescription = null, tint = Citrine, modifier = Modifier.size(13.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("${miles.toInt()} mi", style = MaterialTheme.typography.labelMedium, color = Citrine)
                                        Spacer(Modifier.width(10.dp))
                                    }
                                    TagChip(entry.typeLabel.uppercase(), color = entry.accent)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                SculptedIconButton(
                                    icon = Icons.Filled.Share,
                                    contentDescription = "Share spot",
                                    onClick = {
                                        scope.launch {
                                            ShareCardImage.share(
                                                context = context,
                                                title = entry.name,
                                                subtitle = entry.region + "  •  " + entry.typeLabel,
                                                body = entry.region,
                                                accentHex = 0xFF2C6F9B,
                                                fileName = "rockscout_spot_${entry.id}",
                                            )
                                        }
                                    },
                                    accent = Citrine,
                                    iconTint = TextLow,
                                    size = 36.dp,
                                )
                                SculptedIconButton(
                                    icon = Icons.Filled.PersonAdd,
                                    contentDescription = "Share to Profile",
                                    onClick = { shareToProfileLocId = entry.id },
                                    accent = Citrine,
                                    iconTint = Citrine,
                                    size = 36.dp,
                                )
                                SculptedIconButton(
                                    icon = Icons.Filled.Bookmark,
                                    contentDescription = "Remove bookmark",
                                    onClick = { pendingUnbookmarkLocId = entry.id },
                                    accent = Citrine,
                                    iconTint = Citrine,
                                    size = 36.dp,
                                )
                            }
                        }
                    }
                }
            }
            }
        }
    }

    shareToProfileLocId?.let { favId ->
        val entry = FavoriteSpotResolver.resolve(favId)
        if (entry != null) {
            ShareToProfileComposer(
                sourceType = "favoritespot",
                title = entry.name,
                tagline = entry.region + "  •  " + entry.typeLabel,
                imageUri = null,
                locationText = entry.region,
                onDismiss = { shareToProfileLocId = null },
            )
        }
    }

    pendingUnbookmarkLocId?.let { favId ->
        val entry = FavoriteSpotResolver.resolve(favId)
        if (entry != null) {
            DeleteConfirmDialog(
                title = "Remove bookmark?",
                message = "Remove ${entry.name} from your favorite spots?",
                onConfirm = {
                    val wasFav = repo.isFavoriteSpot(favId)
                    repo.toggleFavoriteSpot(favId)
                    if (!wasFav) AchievementsRepository.award(XpSource.DIG_SITE_CHECKIN)
                    pendingUnbookmarkLocId = null
                },
                onDismiss = { pendingUnbookmarkLocId = null },
            )
        }
    }
}

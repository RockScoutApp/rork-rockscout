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
    val featureLocked = remember(isPremium) {
        accessManager.isFeatureLocked(isPremium)
    }
    val isPayingUser = isPremium
    val wishlistIds by repo.wishlist.collectAsStateWithLifecycle()
    var shareToProfileLocId by remember { mutableStateOf<String?>(null) }
    var pendingUnbookmarkLocId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    ScreenScaffold(title = "My Favorite Spots", onBack = { navController.popBackStack() }) {
        if (featureLocked) {
            LockedFeatureBanner(
                onSubscribe = { navController.navigate(Routes.PAYWALL) },
                message = "Your 1-week trial has ended. Subscribe or donate to save and visit your favorite spots.",
                modifier = Modifier.padding(20.dp),
            )
        } else if (favorites.isEmpty()) {
            EmptyState(
                emoji = "\uD83D\uDCCD",
                title = "No saved spots yet",
                message = "Bookmark mines and dig sites from their detail page to keep them one tap away.",
            )
        } else {
            val sortedFavorites = remember(favorites) {
                favorites.sortedBy { SeedData.locationById(it)?.name?.lowercase() ?: it }
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
                        "Dig sites you've saved for quick access. Tap any to open the map and head out.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMid,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                    Text(
                        "${favorites.size} spots saved for your next trip.",
                        style = MaterialTheme.typography.labelMedium,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
                items(sortedFavorites.size) { index ->
                    val loc = SeedData.locationById(sortedFavorites[index]) ?: return@items
                    val miles = AppRepository.distanceMiles(current.first, current.second, loc.latitude, loc.longitude)
                    DarkCard(
                        accent = Aqua,
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Routes.location(loc.id)) },
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SpecimenGlyph(emoji = loc.type.emoji, accent = Aqua, size = 50)
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(loc.name, style = MaterialTheme.typography.titleLarge, color = Color.White)
                                Text(loc.region, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid)
                                Spacer(Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.NearMe, contentDescription = null, tint = Citrine, modifier = Modifier.size(13.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("${miles.toInt()} mi", style = MaterialTheme.typography.labelMedium, color = Citrine)
                                    Spacer(Modifier.width(10.dp))
                                    if (loc.publicAccess) TagChip("PUBLIC", color = Success)
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
                                                title = loc.name,
                                                subtitle = loc.region + "  •  " + loc.type.label,
                                                body = loc.summary,
                                                accentHex = 0xFF2C6F9B,
                                                fileName = "rockscout_spot_${loc.id}",
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
                                    onClick = { shareToProfileLocId = loc.id },
                                    accent = Citrine,
                                    iconTint = Citrine,
                                    size = 36.dp,
                                )
                                SculptedIconButton(
                                    icon = Icons.Filled.Bookmark,
                                    contentDescription = "Remove bookmark",
                                    onClick = { pendingUnbookmarkLocId = loc.id },
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

    shareToProfileLocId?.let { locId ->
        val loc = SeedData.locationById(locId)
        if (loc != null) {
            ShareToProfileComposer(
                sourceType = "favoritespot",
                title = loc.name,
                tagline = loc.region + "  •  " + loc.type.label,
                imageUri = null,
                locationText = loc.region,
                onDismiss = { shareToProfileLocId = null },
            )
        }
    }

    pendingUnbookmarkLocId?.let { locId ->
        val loc = SeedData.locationById(locId)
        if (loc != null) {
            DeleteConfirmDialog(
                title = "Remove bookmark?",
                message = "Remove ${loc.name} from your favorite spots?",
                onConfirm = {
                    val wasFav = repo.isFavoriteSpot(loc.id)
                    repo.toggleFavoriteSpot(loc.id)
                    if (!wasFav) AchievementsRepository.award(XpSource.DIG_SITE_CHECKIN)
                    pendingUnbookmarkLocId = null
                },
                onDismiss = { pendingUnbookmarkLocId = null },
            )
        }
    }
}

package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.PersonAdd
import com.rork.rockscout.ui.components.YooperliteHeart
import com.rork.rockscout.ui.components.ShareCardImage
import com.rork.rockscout.ui.components.ShareToProfileComposer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.Specimen
import com.rork.rockscout.data.GearGuide
import com.rork.rockscout.data.SpecimenImages
import com.rork.rockscout.ui.components.StandaloneZoomableImageViewer
import com.rork.rockscout.ui.components.LongPressableImage
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SpecimenLikeAddShare
import com.rork.rockscout.ui.components.StatRow
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.GearLinksCard
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.RarityIndicator
import com.rork.rockscout.ui.components.rockClassColor
import com.rork.rockscout.ui.components.categoryColor
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.CitrineSoft
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpecimenDetailScreen(
    navController: NavController,
    specimenId: String,
    /** Latitude of the specimen marker pin the user tapped to reach this
     *  screen. When non-null (along with [spotLongitude]), a "Share a Spot"
     *  button is shown so the user can share this specific hunting spot. */
    spotLatitude: Double? = null,
    /** Longitude of the specimen marker pin. See [spotLatitude]. */
    spotLongitude: Double? = null,
) {
    val spec = SeedData.specimenById(specimenId)
    val repo = AppRepository.instance
    val collection by repo.collection.collectAsStateWithLifecycle()
    val wishlist by repo.wishlist.collectAsStateWithLifecycle()
    val likedSpecimens by repo.likedSpecimens.collectAsStateWithLifecycle()
    val favoriteSpots by repo.favoriteSpots.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var shareToProfileSpec by remember { mutableStateOf<Specimen?>(null) }

    // Favorite-spot ID for this specimen marker (only when opened from a pin).
    val spotFavId = if (spotLatitude != null && spotLongitude != null)
        com.rork.rockscout.data.FavoriteSpotResolver.pinId(spotLatitude, spotLongitude, spec?.name ?: "Specimen Spot")
    else null
    val isSpotFavorited = spotFavId != null && favoriteSpots.contains(spotFavId)

    if (spec == null) {
        RockBackground {
            Box(Modifier.fillMaxWidth().padding(40.dp)) { Text("Specimen not found.", color = TextMid) }
        }
        return
    }

    val accent = rockClassColor(spec.rockClass)
    val collected = collection.any { it.specimenId == spec.id }
    val wishlisted = wishlist.contains(spec.id)
    val isLiked = likedSpecimens.contains(spec.id)
    val imageUrls = SpecimenImages.urls[spec.id] ?: emptyList()
    var viewerOpen by remember { mutableStateOf(false) }
    var viewerPage by remember { mutableIntStateOf(0) }

    // Prefetch all gallery images for this specimen so the full-screen viewer
    // and remaining thumbnails load instantly when tapped.
    androidx.compose.runtime.LaunchedEffect(spec.id, imageUrls) {
        if (imageUrls.size > 1) {
            com.rork.rockscout.data.ImagePrefetcher.prefetch(context, imageUrls)
        }
    }

    if (viewerOpen && imageUrls.isNotEmpty()) {
        StandaloneZoomableImageViewer(imageUrl = imageUrls[viewerPage.coerceIn(0, imageUrls.lastIndex)], onDismiss = { viewerOpen = false })
    }

    RockBackground {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { SpecimenHeader(
                spec, accent, isLiked, collected, wishlisted,
                onLikeToggle = { repo.toggleLike(spec.id) },
                onCollectionToggle = { repo.toggleCollection(spec.id) },
                onWishlistToggle = { repo.toggleWishlist(spec.id) },
                likeCount = repo.specimenLikeCount(spec.id),
                onBack = { navController.popBackStack() },
                onShare = {
                    scope.launch {
                        val photo: android.graphics.Bitmap? =
                            imageUrls.firstOrNull()?.let { url ->
                                ShareCardImage.loadDownsampled(context, android.net.Uri.parse(url))
                            }
                        ShareCardImage.share(
                            context = context,
                            title = spec.name,
                            subtitle = spec.category + "  •  " + spec.rarity,
                            body = spec.tagline,
                            accentHex = spec.colorHex,
                            photoBitmap = photo,
                            fileName = "rockscout_specimen_${spec.id}",
                        )
                    }
                },
                onShareToProfile = { shareToProfileSpec = spec },
            ) }
            if (imageUrls.isNotEmpty()) {
                val labels = when (spec.id) {
                    "amazing-fluorescent-hackmanite" -> hackmanitePhotoLabels
                    "amazing-squid-game-calcite", "calcite-squid-game" -> squidGameCalciteLabels
                    "amethyst" -> amethystPhotoLabels
                    in fluorescentSpecimenIds -> fluorescentPhotoLabels
                    else -> photoLabels
                }
                item { ThumbnailGallery(imageUrls = imageUrls, labels = labels, accent = accent, onTap = { idx -> viewerPage = idx; viewerOpen = true }) }
            }
            item {
                SectionCard("Overview", accent = accent) {
                    Text(spec.description, style = MaterialTheme.typography.bodyLarge, color = DarkTextMid)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TagChip(spec.category, color = categoryColor(spec.category))
                        RarityIndicator(spec.rarity, accent = accent)
                    }
                }
            }
            item {
                SectionCard("Properties", accent = accent) {
                    StatRow("Chemical formula", spec.chemicalFormula, accent = accent)
                    StatRow("Hardness", spec.hardness, accent = accent)
                    StatRow("Luster", spec.luster, accent = accent)
                    StatRow("Streak", spec.streak, accent = accent)
                    StatRow("Crystal system", spec.crystalSystem, accent = accent)
                    StatRow("Category", spec.category, accent = accent)
                    StatRow("Rarity", spec.rarity, accent = accent, showDivider = false)
                }
            }
            item {
                SectionCard("How it forms", accent = accent) {
                    Text(spec.formation, style = MaterialTheme.typography.bodyLarge, color = DarkTextMid)
                }
            }
            item {
                SectionCard("Common colors", accent = accent) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        spec.commonColors.forEach { TagChip(it, color = accent) }
                    }
                }
            }
            item {
                SectionCard("Where it's found", accent = accent) {
                    spec.whereFound.forEachIndexed { index, place ->
                        LocationRow(place, accent, isLast = index == spec.whereFound.lastIndex)
                    }
                }
            }
            item {
                SectionCard("Good to know", accent = accent) {
                    spec.funFacts.forEachIndexed { index, fact ->
                        FactRow(fact, accent, isLast = index == spec.funFacts.lastIndex)
                    }
                    Spacer(Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(accent.copy(alpha = 0.10f))
                            .glowingBorder(2.dp, accent.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                    ) {
                        Column {
                            Text("USES", style = MaterialTheme.typography.labelMedium, color = Aqua, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text(spec.uses, style = MaterialTheme.typography.bodyMedium, color = DarkTextHigh)
                        }
                    }
                }
            }
            item {
                GearLinksCard(
                    sectionTitle = "Gear to find this",
                    items = GearGuide.gearForRockClass(spec.rockClass),
                    accent = accent,
                )
            }
            // Share-a-Spot + Add to Favorite Spots card — only shown when the
            // user arrived from a specimen marker pin tap (i.e. we have the
            // pin's coordinates).
            if (spotLatitude != null && spotLongitude != null) {
                item { ShareASpotCard(
                    specimenName = spec.name,
                    latitude = spotLatitude,
                    longitude = spotLongitude,
                    accent = accent,
                ) }
                item {
                    val favAccent = if (isSpotFavorited) Success else Citrine
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Obsidian.copy(alpha = 0.7f))
                            .glowingBorder(1.5.dp, favAccent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .clickable {
                                spotFavId?.let { repo.toggleFavoriteSpot(it) }
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            if (isSpotFavorited) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Add to Favorite Spots",
                            tint = favAccent,
                            modifier = Modifier.size(22.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isSpotFavorited) "Saved to Favorite Spots" else "Add to Favorite Spots",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "Bookmark this specimen hunting spot for quick access from My Favorite Spots.",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextMid,
                            )
                        }
                    }
                }
            }
        }
    }

    shareToProfileSpec?.let { s ->
        ShareToProfileComposer(
            sourceType = "specimen",
            title = s.name,
            tagline = s.category + "  •  " + s.rarity,
            imageUri = (SpecimenImages.urls[s.id] ?: emptyList()).firstOrNull(),
            locationText = s.whereFound.firstOrNull() ?: "",
            onDismiss = { shareToProfileSpec = null },
        )
    }
}

@Composable
private fun SpecimenHeader(
    spec: Specimen,
    accent: Color,
    isLiked: Boolean,
    collected: Boolean,
    wishlisted: Boolean,
    onLikeToggle: () -> Unit,
    onCollectionToggle: () -> Unit,
    onWishlistToggle: () -> Unit,
    onBack: () -> Unit,
    onShare: () -> Unit = {},
    onShareToProfile: () -> Unit = {},
    likeCount: Int = 0,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(
                Brush.verticalGradient(listOf(accent.copy(alpha = 0.40f), Slate900))
            ),
    ) {
        // Back button — top left
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 48.dp)
                .size(44.dp)
                .sculpted(
                    shape = RoundedCornerShape(22.dp),
                    accent = accent,
                    shadowElevation = 5.dp,
                    circular = true,
                    onClick = onBack,
                )
                .clip(RoundedCornerShape(22.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        // Like heart + Add/Share dropdown — top right
        SpecimenLikeAddShare(
            liked = isLiked,
            onLikeToggle = onLikeToggle,
            collected = collected,
            wishlisted = wishlisted,
            onCollectionToggle = onCollectionToggle,
            onWishlistToggle = onWishlistToggle,
            onShareToProfile = onShareToProfile,
            onShareToSocial = onShare,
            accent = accent,
            heartSize = 44.dp,
            likeCount = likeCount,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 12.dp, top = 48.dp),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp),
        ) {
            TagChip(spec.rockClass.label.uppercase(), color = accent, filled = true)
            Spacer(Modifier.height(8.dp))
            Text(
                spec.name,
                style = MaterialTheme.typography.displayMedium,
                color = TextHigh,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
            Text(
                spec.tagline,
                style = MaterialTheme.typography.bodyLarge,
                color = com.rork.rockscout.ui.components.brightenForText(accent, amount = 0.5f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SectionCard(title: String, accent: Color = Citrine, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, 18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Aqua,
                fontWeight = FontWeight.Bold,
            )
        }
        DarkCard(modifier = Modifier.fillMaxWidth(), accent = accent) { content() }
    }
}

/** Clean location row with a map-pin-style accent dot and subtle divider. */
@Composable
private fun LocationRow(place: String, accent: Color, isLast: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.85f))
                    .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape),
            )
            Spacer(Modifier.width(12.dp))
            Text(
                place,
                style = MaterialTheme.typography.bodyLarge,
                color = DarkTextMid,
                modifier = Modifier.weight(1f),
            )
        }
        if (!isLast) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.06f)),
            )
        }
    }
}

/** Fun-fact row with a lightbulb icon and subtle divider. */
@Composable
private fun FactRow(fact: String, accent: Color, isLast: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accent.copy(alpha = 0.15f))
                    .glowingBorder(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = accent, modifier = Modifier.size(14.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text(
                fact,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextHigh,
                modifier = Modifier.weight(1f),
            )
        }
        if (!isLast) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.06f)),
            )
        }
    }
}

/** Standard photo labels for specimens with 4-image sets (rough, wild, museum, cabochon). */
private val photoLabels = listOf(
    "Rough Material",
    "In the Wild",
    "Museum Quality",
    "Polished Cabochon",
)

/** Source-specific labels for the general Amethyst card to highlight origin and accuracy. */
private val amethystPhotoLabels = listOf(
    "Rough Material",
    "Brazilian Amethyst",
    "Uruguay Amethyst",
    "Polished Cabochon",
)

/** Specimens whose photos are UV-wavelength sets rather than rough/wild/museum/cabochon. */
private val fluorescentSpecimenIds = setOf(
    "amazing-fluorescent-adamite",
    "amazing-fluorescent-autunite",
    "amazing-fluorescent-fluorite",
    "amazing-fluorescent-scapolite",
    "amazing-fluorescent-scheelite",
    "amazing-fluorescent-sphalerite",
    "amazing-fluorescent-willemite",
    "amazing-phosphorescent-calcite",
    "amazing-fluorescent-hackmanite",
    "amazing-fluorescent-syenite-yooperlite",
    "amazing-squid-game-calcite",
    "calcite-squid-game",
    "syenite-fluorescent",
)

/** UV-wavelength labels for fluorescent mineral photo sets. */
private val fluorescentPhotoLabels = listOf(
    "Natural Light",
    "Longwave UV",
    "Midwave UV",
    "Shortwave UV",
)

/** Labels for Squid Game Calcite — shortwave-focused gallery. */
private val squidGameCalciteLabels = listOf(
    "Shortwave UV",
    "Shortwave UV",
)

/** Labels for hackmanite that emphasize its tenebrescent color-change sequence. */
private val hackmanitePhotoLabels = listOf(
    "Before UV",
    "Longwave UV",
    "After UV",
    "Shortwave UV",
    "Cabochon Before Sun",
    "Cabochon After Sun",
)

@Composable
private fun ThumbnailGallery(imageUrls: List<String>, labels: List<String>, accent: Color, onTap: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Text(
            "Photos".uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = Aqua,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(0.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(imageUrls.size, key = { it }) { index ->
                val url = imageUrls[index]
                // Only show real text labels. Empty strings and bare numbers (e.g. "5")
                // are treated as "no label" so the thumbnail never shows a numbered fallback.
                val rawLabel = labels.getOrNull(index) ?: ""
                val label = rawLabel.takeIf { it.isNotBlank() && !it.trim().matches(Regex("^\\d+$")) } ?: ""
                Column(
                    modifier = Modifier.width(120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .sculpted(shape = RoundedCornerShape(14.dp), accent = accent, shadowElevation = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Slate800)
                            .glowingBorder(4.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        LongPressableImage(
                            model = url,
                            contentDescription = if (label.isBlank()) "Specimen photo" else "$label photo",
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            contentScale = ContentScale.Crop,
                            onClick = { onTap(index) },
                        )
                    }
                    if (label.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = Aqua,
                            fontWeight = if (index < 4) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

/**
 * "Share a Spot" card — shown on the specimen detail screen when the user
 * reached it by tapping a specimen marker pin on the map. Generates a
 * `rockscout://spot/<lat>,<lng>?name=<specimen>` deep link so the user can
 * share this specific rock hunting spot with friends via the system share
 * sheet. Friends who tap the link open the same spot in RockScout.
 */
@Composable
private fun ShareASpotCard(
    specimenName: String,
    latitude: Double,
    longitude: Double,
    accent: Color,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, 18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "SHARE THIS SPOT".uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Aqua,
                fontWeight = FontWeight.Bold,
            )
        }
        DarkCard(modifier = Modifier.fillMaxWidth(), accent = accent) {
            Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Filled.Share,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Found a great spot for $specimenName?",
                            style = MaterialTheme.typography.bodyLarge,
                            color = DarkTextHigh,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Share this exact location with friends so they can hunt here too. " +
                                "They'll get a deep link that opens the spot on the map in RockScout.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextMid,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "\uD83D\uDCCD " + String.format(java.util.Locale.US, "%.5f, %.5f", latitude, longitude),
                            style = MaterialTheme.typography.labelMedium,
                            color = accent,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .weight(1f)
                            .sculpted(
                                shape = RoundedCornerShape(12.dp),
                                accent = accent,
                                shadowElevation = 4.dp,
                                onClick = {
                                    val deepLink = buildSpotDeepLink(latitude, longitude, specimenName)
                                    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(android.content.Intent.EXTRA_SUBJECT, "Rock hunting spot: $specimenName")
                                        putExtra(
                                            android.content.Intent.EXTRA_TEXT,
                                            "$specimenName — a rock hunting spot shared with you via RockScout.\n\n" +
                                                "Open in RockScout: $deepLink\n\n" +
                                                "Coordinates: " + String.format(java.util.Locale.US, "%.5f, %.5f", latitude, longitude),
                                        )
                                    }
                                    com.rork.rockscout.data.SafeLinkOpener.openShareChooser(
                                        context = context,
                                        shareIntent = shareIntent,
                                        title = "Share this rock hunting spot",
                                    )
                                },
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .background(accent.copy(alpha = 0.12f))
                        .glowingBorder(1.5.dp, accent.copy(alpha = 0.40f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "Share a Spot",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = accent,
                        )
                    }
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .weight(1f)
                            .sculpted(
                                shape = RoundedCornerShape(12.dp),
                                accent = Aqua,
                                shadowElevation = 4.dp,
                                onClick = {
                                    com.rork.rockscout.data.SafeLinkOpener.openMaps(
                                        context = context,
                                        googleMapsUri = "google.navigation:q=$latitude,$longitude",
                                        fallbackGeoUri = "geo:$latitude,$longitude?q=$latitude,$longitude",
                                    )
                                },
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .background(Aqua.copy(alpha = 0.12f))
                        .glowingBorder(1.5.dp, Aqua.copy(alpha = 0.40f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "Get Directions",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Aqua,
                        )
                    }
                }
            }
        }
    }
}

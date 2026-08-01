package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.EmojiObjects
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.net.Uri
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.GearGuide
import com.rork.rockscout.data.GearKit
import com.rork.rockscout.data.RocksAreAmazingSpecimens
import com.rork.rockscout.data.ScreenPdfExporter
import com.rork.rockscout.data.ScreenPdfItem
import com.rork.rockscout.data.Specimen
import com.rork.rockscout.data.SpecimenImages
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.GearLinksCard
import com.rork.rockscout.ui.components.GlobalSearchSection
import com.rork.rockscout.ui.components.FullScreenImageViewer
import com.rork.rockscout.ui.components.InterstitialAdTrigger
import com.rork.rockscout.ui.components.LongPressableImage
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.CompactTagChip
import com.rork.rockscout.ui.components.SpecimenThumbnail
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.RarityIndicator
import com.rork.rockscout.ui.components.brightenForText
import com.rork.rockscout.ui.components.ShareCardImage
import com.rork.rockscout.ui.components.shortCategoryLabel
import com.rork.rockscout.ui.components.SpecimenListItem
import com.rork.rockscout.ui.components.CompactAddShareButton
import com.rork.rockscout.ui.components.CardHeart
import com.rork.rockscout.ui.components.SpecimenAddShare
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.YooperliteHeart
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.CitrineSoft
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.components.glowingBorder

private data class AmazingSection(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color,
    val specimens: List<Specimen>,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RocksAreAmazingScreen(navController: NavController) {
    var viewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerInitialPage by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var shareToProfileSpec by remember { mutableStateOf<Specimen?>(null) }

    InterstitialAdTrigger(screenKey = "rocks_are_amazing") {
        navController.navigate(Routes.PAYWALL)
    }

    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var showSubmitDialog by remember { mutableStateOf(false) }
    var isExportingPdf by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val customRaaSpecimens by com.rork.rockscout.data.CustomSpecimenStore.raaSpecimens.collectAsStateWithLifecycle()
    val sections = remember {
        listOf(
            AmazingSection(
                title = "Enhydros & Fluid Inclusions",
                subtitle = "Ancient water trapped in crystal for millions of years",
                icon = Icons.Filled.WaterDrop,
                accent = Color(0xFF5BBAE8),
                specimens = RocksAreAmazingSpecimens.enhydros,
            ),
            AmazingSection(
                title = "Pseudomorphs — Nature's Impostors",
                subtitle = "Minerals that have perfectly replaced others while keeping their shape",
                icon = Icons.Filled.Nature,
                accent = Color(0xFF8CC84B),
                specimens = RocksAreAmazingSpecimens.pseudomorphs,
            ),
            AmazingSection(
                title = "Petroleum & Hydrocarbon Inclusions",
                subtitle = "Fossil fuels trapped inside crystals — oil frozen in time",
                icon = Icons.Filled.LocalFireDepartment,
                accent = Color(0xFFDD9933),
                specimens = RocksAreAmazingSpecimens.petroleumInclusions,
            ),
            AmazingSection(
                title = "Mineral Inclusions in Crystals",
                subtitle = "Green clouds, red fire, blue fibers — other minerals frozen inside quartz",
                icon = Icons.Filled.Diamond,
                accent = Color(0xFF9B59B6),
                specimens = RocksAreAmazingSpecimens.mineralInclusions,
            ),
            AmazingSection(
                title = "Fluorescent & Phosphorescent Minerals",
                subtitle = "Rocks that explode with color under ultraviolet light",
                icon = Icons.Filled.EmojiObjects,
                accent = Color(0xFF00DD88),
                specimens = RocksAreAmazingSpecimens.fluorescent,
            ),
            AmazingSection(
                title = "Optical Phenomena",
                subtitle = "Cat's eyes, floating stars, and rainbow flashes — light bending in stone",
                icon = Icons.Filled.Visibility,
                accent = Color(0xFFEE5577),
                specimens = RocksAreAmazingSpecimens.opticalPhenomena,
            ),
            AmazingSection(
                title = "Other Amazing Formations",
                subtitle = "Lightning frozen in glass, color-changing stones, and nature's sculptures",
                icon = Icons.Filled.AutoAwesome,
                accent = Color(0xFF44AACC),
                specimens = RocksAreAmazingSpecimens.otherAmazing,
            ),
            AmazingSection(
                title = "Industrial Slag & Slag Glass",
                subtitle = "Beautiful stones born from fire and industry — smelting's accidental gems",
                icon = Icons.Filled.LocalFireDepartment,
                accent = Color(0xFFDD7733),
                specimens = RocksAreAmazingSpecimens.industrialSlag,
            ),
            AmazingSection(
                title = "Coprolites & Trace Fossils",
                subtitle = "Fossilized prehistoric dung — each specimen comes with an image of the animal it came from",
                icon = Icons.Filled.Nature,
                accent = Color(0xFF8B6D4B),
                specimens = RocksAreAmazingSpecimens.coprolites,
            ),
            AmazingSection(
                title = "Copper-Inclusion Agates",
                subtitle = "Native copper and silver trapped inside chalcedony — the legendary agates of Michigan's Keweenaw Peninsula",
                icon = Icons.Filled.Diamond,
                accent = Color(0xFFB87333),
                specimens = RocksAreAmazingSpecimens.copperInclusionAgates,
            ),
            AmazingSection(
                title = "Mineral Assemblages",
                subtitle = "Nature's iconic mineral combinations — two or more species crystallized together in a single specimen",
                icon = Icons.Filled.Diamond,
                accent = Color(0xFFCC8844),
                specimens = RocksAreAmazingSpecimens.mineralAssemblages,
            ),
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Rocks Are Amazing",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                },
                navigationIcon = {
                    SculptedIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = { navController.popBackStack() },
                        accent = Citrine,
                        iconTint = Citrine,
                    )
                },
                actions = {
                    SculptedIconButton(
                        icon = Icons.Filled.PictureAsPdf,
                        contentDescription = "Export PDF",
                        onClick = {
                            if (isExportingPdf) return@SculptedIconButton
                            isExportingPdf = true
                            scope.launch {
                                val items = sections.flatMap { section ->
                                    section.specimens.map { spec ->
                                        ScreenPdfItem(
                                            title = spec.name,
                                            subtitle = "${section.title}  •  ${spec.rockClass.label}  •  ${spec.category}",
                                            accentRgb = spec.colorHex.toInt(),
                                            imageUrl = (SpecimenImages.urls[spec.id] ?: spec.imageUrls).firstOrNull(),
                                            fields = listOf(
                                                "Section" to section.title,
                                                "Class" to spec.rockClass.label,
                                                "Category" to spec.category,
                                                "Hardness" to spec.hardness,
                                                "Luster" to spec.luster,
                                                "Crystal System" to spec.crystalSystem,
                                                "Chemical Formula" to spec.chemicalFormula,
                                                "Common Colors" to spec.commonColors.joinToString(", "),
                                                "Rarity" to spec.rarity,
                                            ).filter { it.second.isNotBlank() },
                                            description = spec.description,
                                        )
                                    }
                                }
                                ScreenPdfExporter.export(
                                    context = context,
                                    docTitle = "Rocks Are Amazing",
                                    fileName = "RockScout_RocksAreAmazing",
                                    items = items,
                                )
                                isExportingPdf = false
                            }
                        },
                        accent = Citrine,
                        iconTint = Citrine,
                        size = 40.dp,
                        shadowElevation = 4.dp,
                        enabled = !isExportingPdf,
                    )
                    SculptedIconButton(
                        icon = Icons.Filled.CloudUpload,
                        contentDescription = "Submit specimen",
                        onClick = { showSubmitDialog = true },
                        accent = Citrine,
                        iconTint = Citrine,
                        size = 40.dp,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
        containerColor = Color.Transparent,
    ) { innerPadding ->
        RockBackground {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    scope.launch {
                        kotlinx.coroutines.delay(800)
                        isRefreshing = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp, top = innerPadding.calculateTopPadding() + 8.dp, bottom = 40.dp
                ),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                item {
                    GlobalSearchSection(
                        navController = navController,
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        placeholder = "Search amazing rocks, specimens, guides, locations…",
                    )
                    Spacer(Modifier.height(8.dp))
                }

                // Hero header
                item {
                    AmazingHero()
                    Spacer(Modifier.height(8.dp))
                }

                // Render each section
                sections.forEach { section ->
                    item {
                        Spacer(Modifier.height(12.dp))
                        SectionHeader(
                            title = section.title,
                            subtitle = section.subtitle,
                            icon = section.icon,
                            accent = section.accent,
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    items(section.specimens) { specimen ->
                        AmazingSpecimenCard(
                            specimen = specimen,
                            onClick = { navController.navigate(Routes.specimen(specimen.id)) },
                            onPhotoClick = { urls, page ->
                                viewerUrls = urls
                                viewerInitialPage = page
                            },
                            onShareToProfile = { shareToProfileSpec = specimen },
                        )
                    }
                }

                // User-submitted specimens approved for RAA
                if (customRaaSpecimens.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(12.dp))
                        SectionHeader(
                            title = "User Submissions",
                            subtitle = "Specimens submitted by the RockScout community",
                            icon = Icons.Filled.CloudUpload,
                            accent = Color(0xFF6FA8C7),
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    items(customRaaSpecimens, key = { it.id }) { specimen ->
                        AmazingSpecimenCard(
                            specimen = specimen,
                            onClick = { navController.navigate(Routes.specimen(specimen.id)) },
                            onPhotoClick = { urls, page ->
                                viewerUrls = urls
                                viewerInitialPage = page
                            },
                            onShareToProfile = { shareToProfileSpec = specimen },
                        )
                    }
                }

            }

            if (viewerUrls.isNotEmpty()) {
                FullScreenImageViewer(
                    imageUrls = viewerUrls,
                    initialPage = viewerInitialPage,
                    onDismiss = { viewerUrls = emptyList() },
                )
            }
            if (showSubmitDialog) {
                SubmitSpecimenDialog(onDismiss = { showSubmitDialog = false })
            }
            }
        }
    }

    shareToProfileSpec?.let { s ->
        ShareToProfileComposer(
            sourceType = "rocks_are_amazing",
            title = s.name,
            tagline = s.category + "  •  " + s.rarity,
            imageUri = (SpecimenImages.urls[s.id] ?: s.imageUrls).firstOrNull(),
            locationText = s.whereFound.firstOrNull() ?: "",
            onDismiss = { shareToProfileSpec = null },
        )
    }
}

@Composable
private fun AmazingHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF2D2638),
                        Color(0xFF1E1C25),
                        Color(0xFF2A2230),
                    )
                )
            )
            .padding(24.dp),
    ) {
        Column {
            Text(
                text = "\u2728 The Earth is a museum",
                style = MaterialTheme.typography.headlineMedium,
                color = TextHigh,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "From water trapped for 100 million years, to crystals that change color in sunlight, to lightning turned to glass — these are the specimens that make geology truly mind-blowing.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextMid,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TagChip("70+ wonders", color = CitrineSoft, filled = true, textColor = Ink)
                TagChip("11 categories", color = Aqua, filled = true, textColor = Ink)
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(accent.copy(alpha = 0.18f))
                .glowingBorder(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .padding(10.dp),
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = accent,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Aqua,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Aqua,
            )
        }
    }
}

/** Full-width Gear Guide title tile — replaces the old SectionHeader that sat above
 *  the kit cards. Sits after the last kit card and before the "All gear" links card
 *  (master build task N). */
@Composable
private fun GearGuideTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        accent.copy(alpha = 0.18f),
                        Color(0xFF120F08).copy(alpha = 0.55f),
                    )
                )
            )
            .glowingBorder(2.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(accent.copy(alpha = 0.20f))
                .glowingBorder(1.dp, accent.copy(alpha = 0.55f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Aqua,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Aqua,
            )
        }
    }
}

@Composable
private fun AmazingSpecimenCard(
    specimen: Specimen,
    onClick: () -> Unit,
    onPhotoClick: (List<String>, Int) -> Unit = { _, _ -> },
    onShareToProfile: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = AppRepository.instance
    val likedSpecimens by repo.likedSpecimens.collectAsStateWithLifecycle()
    val collection by repo.collection.collectAsStateWithLifecycle()
    val wishlist by repo.wishlist.collectAsStateWithLifecycle()
    val isLiked = likedSpecimens.contains(specimen.id)
    val isCollected = collection.any { it.specimenId == specimen.id }
    val isWishlisted = wishlist.contains(specimen.id)

    val imageUrls = SpecimenImages.urls[specimen.id] ?: specimen.imageUrls
    val accent = Color(specimen.colorHex)
    val isFluorescent = specimen.category == "Fluorescent & Phosphorescent Minerals"
    val thumbnailUrl = if (isFluorescent) {
        imageUrls.drop(1).firstOrNull() ?: imageUrls.firstOrNull()
    } else {
        imageUrls.firstOrNull()
    }

    SpecimenListItem(
        specimen = specimen,
        accent = accent,
        onClick = onClick,
        onImageClick = { onPhotoClick(imageUrls, 0) },
        imageSize = 113.dp,
        thumbnailUrl = thumbnailUrl,
        showCategory = true,
        categoryLabel = specimen.rockClass.label,
        subtitle = {
            if (specimen.hardness != "—") {
                Text(
                    text = "Mohs ${specimen.hardness}",
                    style = MaterialTheme.typography.labelMedium,
                    color = DarkTextMid,
                    maxLines = 1,
                )
            }
        },
        addShare = {
            CompactAddShareButton(
                collected = isCollected,
                wishlisted = isWishlisted,
                onCollectionToggle = { repo.toggleCollection(specimen.id) },
                onWishlistToggle = { repo.toggleWishlist(specimen.id) },
                onShareToProfile = onShareToProfile,
                onShareToSocial = {
                    scope.launch {
                        val photo: android.graphics.Bitmap? =
                            imageUrls.firstOrNull()?.let { url ->
                                ShareCardImage.loadDownsampled(context, Uri.parse(url))
                            }
                        ShareCardImage.share(
                            context = context,
                            title = specimen.name,
                            subtitle = specimen.category + "  •  " + specimen.rarity,
                            body = specimen.tagline,
                            accentHex = specimen.colorHex,
                            photoBitmap = photo,
                            fileName = "rockscout_raa_${specimen.id}",
                        )
                    }
                },
                accent = accent,
                size = 44.dp,
            )
        },
        heart = {
            CardHeart(
                active = isLiked,
                onToggle = { repo.toggleLike(specimen.id) },
                accent = accent,
                size = 44.dp,
                likeCount = repo.specimenLikeCount(specimen.id),
            )
        },
    )
}

@Composable
private fun GearKitCard(kit: GearKit) {
    DarkCard(
        accent = Citrine,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Citrine.copy(alpha = 0.22f))
                    .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(kit.emoji, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = kit.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = kit.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${kit.items.size} items",
                    style = MaterialTheme.typography.labelSmall,
                    color = Citrine,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        kit.items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(item.emoji, style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = item.priceBand,
                    style = MaterialTheme.typography.labelSmall,
                    color = Citrine,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Links will open separately in your browser",
            style = MaterialTheme.typography.labelSmall,
            color = DarkTextMid,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}


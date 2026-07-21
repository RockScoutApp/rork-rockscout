package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.AdditionalSpecimens
import com.rork.rockscout.data.AssemblageSpecimens
import com.rork.rockscout.data.ExpandedSpecimens
import com.rork.rockscout.data.ExpandedVarieties
import com.rork.rockscout.data.ImpactGlassSpecimens
import com.rork.rockscout.data.FossilSpecimens
import com.rork.rockscout.data.ImagePrefetcher
import com.rork.rockscout.data.PersistenceManager
import com.rork.rockscout.data.ExpansionGarnets
import com.rork.rockscout.data.ExpansionGemstones
import com.rork.rockscout.data.ExpansionMinerals
import com.rork.rockscout.data.ExpansionSilicates
import com.rork.rockscout.data.ExpansionTourmalines
import com.rork.rockscout.data.JasperSpecimens
import com.rork.rockscout.data.MassiveExpansion
import com.rork.rockscout.data.RocksAreAmazingSpecimens
import com.rork.rockscout.data.MeteoriteSpecimens
import com.rork.rockscout.data.RockClass
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.Specimen
import com.rork.rockscout.data.SpecimenImages
import com.rork.rockscout.ui.components.AlphabetIndex
import com.rork.rockscout.ui.components.BulkDownloadCard
import com.rork.rockscout.ui.components.CategoryFilterRow
import com.rork.rockscout.ui.components.FullScreenImageViewer
import com.rork.rockscout.ui.components.ListCategoryFilter
import com.rork.rockscout.ui.components.filterSpecimensByCategory
import com.rork.rockscout.ui.components.InterstitialAdTrigger
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.CardHeart
import com.rork.rockscout.ui.components.SpecimenAddShare
import com.rork.rockscout.ui.components.SpecimenListItem
import com.rork.rockscout.ui.components.CompactAddShareButton
import com.rork.rockscout.ui.components.ShareCardImage
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.RarityIndicator
import com.rork.rockscout.ui.components.rockClassColor
import com.rork.rockscout.ui.components.shortCategoryLabel
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.rork.rockscout.ui.components.noAutoFocus
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import com.rork.rockscout.ui.components.glowingBorder



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecimenListScreen(navController: NavController) {
    val customSpecimens by com.rork.rockscout.data.CustomSpecimenStore.specimens.collectAsStateWithLifecycle()
    val allSpecimens = remember(customSpecimens) {
        (SeedData.allSpecimens + customSpecimens).distinctBy { it.id }.sortedBy { it.name.lowercase() }
    }
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<ListCategoryFilter?>(null) }

    // Full-screen viewer state
    var viewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerInitialPage by remember { mutableIntStateOf(0) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var showSubmitDialog by remember { mutableStateOf(false) }
    var shareToProfileSpec by remember { mutableStateOf<Specimen?>(null) }
    val repo = com.rork.rockscout.data.AppRepository.instance
    val context = LocalContext.current

    // Bulk-download sheet state ("Download all images for offline").
    var showBulkSheet by remember { mutableStateOf(false) }
    val cacheMode by remember { mutableStateOf(PersistenceManager.loadCacheSizeMode()) }
    val isMaxCache = cacheMode == "max"
    // Offline-ready badge: shows once a bulk download has completed. Re-read on
    // recomposition so it appears immediately after the sheet's run finishes.
    val bulkState by remember { mutableStateOf(PersistenceManager.loadBulkDownloadState() ?: PersistenceManager.BulkDownloadState()) }
    val isOfflineReady = bulkState.done && bulkState.total > 0 && bulkState.finished >= bulkState.total

    InterstitialAdTrigger(screenKey = "specimen_list") {
        navController.navigate(Routes.PAYWALL)
    }

    val selected = selectedFilter
    val filtered = remember(allSpecimens, query, selectedFilter) {
        val byCategory = filterSpecimensByCategory(allSpecimens, selected)

        if (query.isBlank()) byCategory
        else byCategory.filter { spec ->
            spec.name.contains(query, ignoreCase = true) ||
            spec.category.contains(query, ignoreCase = true) ||
            spec.chemicalFormula.contains(query, ignoreCase = true) ||
            spec.whereFound.any { it.contains(query, ignoreCase = true) } ||
            spec.commonColors.any { it.contains(query, ignoreCase = true) } ||
            spec.tagline.contains(query, ignoreCase = true) ||
            spec.rockClass.label.contains(query, ignoreCase = true)
        }
    }

    // Prefetch images for the visible list window plus the next few rows, so
    // thumbnails appear quickly as the user scrolls — even on a weak connection.
    LaunchedEffect(filtered, listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.layoutInfo.visibleItemsInfo.size }
            .collect { (firstVisible, visibleCount) ->
                val window = filtered.drop(firstVisible).take((visibleCount + 12).coerceAtLeast(1))
                val urls = window.mapNotNull { specimen ->
                    (SpecimenImages.urls[specimen.id] ?: specimen.imageUrls).firstOrNull()
                }
                ImagePrefetcher.prefetch(context, urls)
            }
    }

    RockBackground {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                scope.launch {
                    delay(800)
                    isRefreshing = false
                }
            },
            modifier = Modifier.fillMaxSize(),
        ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 16.dp, top = 52.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Specimen Database",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f),
                )
                TagChip(
                    "${filtered.size} specimens",
                    color = Citrine,
                    modifier = Modifier.padding(end = 4.dp),
                )
                Spacer(Modifier.width(6.dp))
                // Offline-ready badge: shown once the bulk download completes, so
                // the user can see at a glance that the catalog is fully cached.
                if (isOfflineReady) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .glowingBorder(
                                1.dp,
                                Color(0xFF5CC98C).copy(alpha = 0.55f),
                                RoundedCornerShape(14.dp),
                            )
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF5CC98C).copy(alpha = 0.18f),
                                        Color(0xFF5CC98C).copy(alpha = 0.08f),
                                    )
                                )
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Filled.CloudDone,
                            contentDescription = "Offline ready",
                            tint = Color(0xFF5CC98C),
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Offline ready",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF5CC98C),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                }
                // Bulk-download pill — opens the detail bottom sheet.
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .glowingBorder(
                            1.dp,
                            Citrine.copy(alpha = 0.55f),
                            RoundedCornerShape(14.dp),
                        )
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Citrine.copy(alpha = 0.18f),
                                    Citrine.copy(alpha = 0.08f),
                                )
                            )
                        )
                        .clickable { showBulkSheet = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Download,
                            contentDescription = "Download all images",
                            tint = Citrine,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Offline",
                            style = MaterialTheme.typography.labelMedium,
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                SculptedIconButton(
                    icon = Icons.Filled.CloudUpload,
                    contentDescription = "Submit specimen",
                    onClick = { showSubmitDialog = true },
                    accent = Citrine,
                    iconTint = Citrine,
                    size = 40.dp,
                )
            }
            Text(
                "Browse every rock, mineral, crystal, and fossil in the database — filter by class or search by name. Tap any specimen for full details, photos, and where to find it.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
            )

            // Category filter tabs
            CategoryFilterRow(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                showNavigationEntry = true,
                onNavigate = { navController.navigate(Routes.ROCKS_ARE_AMAZING) },
            )

            // Search bar
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Slate800.copy(alpha = 0.6f))
                    .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(16.dp)).noAutoFocus(),
                placeholder = {
                    Text(
                        "Search by name, category, color, location…",
                        color = TextLow,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Search", tint = TextLow)
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        Icon(
                            Icons.Filled.Clear,
                            contentDescription = "Clear",
                            tint = TextLow,
                            modifier = Modifier.clickable { query = "" },
                        )
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = Citrine,
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
            )

            // Specimen list
            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "No specimens match \"$query\"",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextMid,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Try a different search term or category.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMid,
                        )
                    }
                }
            } else {
                Row(modifier = Modifier.fillMaxSize()) {
                    AlphabetIndex(
                        names = filtered.map { it.name },
                        onLetterClick = { letter ->
                            val index = filtered.indexOfFirst {
                                it.name.startsWith(letter.toString(), ignoreCase = true)
                            }
                            if (index != -1) {
                                scope.launch { listState.scrollToItem(index) }
                            }
                        },
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .align(Alignment.CenterVertically),
                    )
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .navigationBarsPadding(),
                        contentPadding = PaddingValues(
                            start = 8.dp, end = 16.dp, top = 4.dp, bottom = 40.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        items(filtered, key = { it.id }) { specimen ->
                            SpecimenListCard(
                                specimen = specimen,
                                repo = repo,
                                onClick = { navController.navigate(Routes.specimen(specimen.id)) },
                                onPhotoClick = { photoUrls, pageIdx ->
                                    viewerUrls = photoUrls
                                    viewerInitialPage = pageIdx
                                },
                                onShareToProfile = { shareToProfileSpec = specimen },
                            )
                        }
                    }
                }
            }
        }

        // Full-screen image viewer overlay
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

        // Bulk-download detail sheet
        if (showBulkSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showBulkSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF1E1C16),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 28.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Text(
                        "Download all images for offline",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Caches every specimen photo, guide illustration, and hero image on your device so the whole app works offline. " +
                            "Reopens instantly with zero signal — perfect for off-grid field trips.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMid,
                    )
                    Spacer(Modifier.height(16.dp))
                    BulkDownloadCard(
                        isMaxCache = isMaxCache,
                        onEnableMaxCache = {
                            // Close this sheet and route the user to Social Settings
                            // where the 2 GB toggle lives.
                            showBulkSheet = false
                            navController.navigate(Routes.SOCIAL_SETTINGS)
                        },
                        compact = true,
                    )
                }
            }
        }
        }
    }

    shareToProfileSpec?.let { spec ->
        val imageUrls = SpecimenImages.urls[spec.id] ?: spec.imageUrls
        ShareToProfileComposer(
            sourceType = "database",
            title = spec.name,
            tagline = spec.category + "  •  " + spec.rarity,
            imageUri = imageUrls.firstOrNull(),
            locationText = spec.whereFound.firstOrNull() ?: "",
            onDismiss = { shareToProfileSpec = null },
        )
    }
}


@Composable
private fun SpecimenListCard(
    specimen: Specimen,
    repo: com.rork.rockscout.data.AppRepository,
    onClick: () -> Unit,
    onPhotoClick: (List<String>, Int) -> Unit,
    onShareToProfile: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val accent = rockClassColor(specimen.rockClass)
    val imageUrls = SpecimenImages.urls[specimen.id] ?: specimen.imageUrls
    val collection by repo.collection.collectAsStateWithLifecycle()
    val wishlist by repo.wishlist.collectAsStateWithLifecycle()
    val likedSpecimens by repo.likedSpecimens.collectAsStateWithLifecycle()
    val collected = collection.any { it.specimenId == specimen.id }
    val wishlisted = wishlist.contains(specimen.id)
    val isLiked = likedSpecimens.contains(specimen.id)

    SpecimenListItem(
        specimen = specimen,
        accent = accent,
        onClick = onClick,
        onImageClick = { onPhotoClick(imageUrls, 0) },
        imageSize = 113.dp,
        addShare = {
            CompactAddShareButton(
                collected = collected,
                wishlisted = wishlisted,
                onCollectionToggle = { repo.toggleCollection(specimen.id) },
                onWishlistToggle = { repo.toggleWishlist(specimen.id) },
                onShareToProfile = onShareToProfile,
                onShareToSocial = {
                    scope.launch {
                        val photo: android.graphics.Bitmap? =
                            imageUrls.firstOrNull()?.let { url ->
                                ShareCardImage.loadDownsampled(context, android.net.Uri.parse(url))
                            }
                        ShareCardImage.share(
                            context = context,
                            title = specimen.name,
                            subtitle = specimen.category + "  •  " + specimen.rarity,
                            body = specimen.tagline,
                            accentHex = specimen.colorHex,
                            photoBitmap = photo,
                            fileName = "rockscout_specimen_${specimen.id}",
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
        showCategory = true,
        categoryLabel = shortCategoryLabel(specimen.category),
    )
}

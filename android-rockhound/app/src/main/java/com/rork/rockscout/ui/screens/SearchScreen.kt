package com.rork.rockscout.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.data.AdditionalSpecimens
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AssemblageSpecimens
import com.rork.rockscout.data.DigLocation
import com.rork.rockscout.data.LocationType
import com.rork.rockscout.data.ExpandedSpecimens
import com.rork.rockscout.data.ExpandedVarieties
import com.rork.rockscout.data.ImpactGlassSpecimens
import com.rork.rockscout.data.FossilSpecimens
import com.rork.rockscout.data.ExpansionGarnets
import com.rork.rockscout.data.ExpansionGemstones
import com.rork.rockscout.data.ExpansionMinerals
import com.rork.rockscout.data.ExpansionSilicates
import com.rork.rockscout.data.ExpansionTourmalines
import com.rork.rockscout.data.JasperSpecimens
import com.rork.rockscout.data.MassiveExpansion
import com.rork.rockscout.data.RocksAreAmazingSpecimens
import com.rork.rockscout.data.MeteoriteSpecimens
import com.rork.rockscout.data.PeriodicTableElements
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.Specimen
import com.rork.rockscout.data.SpecimenImages
import com.rork.rockscout.data.RockGuide
import com.rork.rockscout.data.FossilPeriod
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.InterstitialAdTrigger
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.ShareCardImage
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.SpecimenGlyph
import com.rork.rockscout.ui.components.CardHeart
import com.rork.rockscout.ui.components.SpecimenAddShare
import com.rork.rockscout.ui.components.SpecimenListItem
import com.rork.rockscout.ui.components.CompactAddShareButton
import com.rork.rockscout.ui.components.RarityIndicator
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.rockClassColor
import com.rork.rockscout.ui.components.shortCategoryLabel
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Fossil
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.components.glowingBorder
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch

private sealed class SearchResult {
    data class SpecimenResult(val spec: Specimen) : SearchResult()
    data class LocationResult(val loc: DigLocation) : SearchResult()
    data class GuideResult(val guide: RockGuide) : SearchResult()
    data class PeriodResult(val period: FossilPeriod) : SearchResult()
    data class ElementResult(val element: PeriodicTableElements.Element) : SearchResult()
}

@Composable
fun SearchScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(true) }
    var isLocationSearching by remember { mutableStateOf(false) }
    var shareToProfileSpec by remember { mutableStateOf<Specimen?>(null) }

    val repo = com.rork.rockscout.data.AppRepository.instance
    val current by repo.currentLocation.collectAsStateWithLifecycle()

    InterstitialAdTrigger(screenKey = "search") {
        navController.navigate(Routes.PAYWALL)
    }

    // Detect "near me" queries and pull location once
    val isNearMeQuery = remember(query) {
        val q = query.lowercase().trim()
        q.contains("near me") || q.contains("nearby") || q.contains("rock shops near") || q.contains("digs near") || q.contains("mines near")
    }

    LaunchedEffect(isNearMeQuery) {
        if (isNearMeQuery) {
            isLocationSearching = true
            // Simulate a one-time location pull
            kotlinx.coroutines.delay(800)
            isLocationSearching = false
        }
    }

    val results: List<SearchResult> = remember(query, current, isLocationSearching) {
        if (query.isBlank() || isLocationSearching) emptyList()
        else performSearch(query, current, isNearMeQuery)
    }

    val sections = remember(results) {
        val specimens = results.filterIsInstance<SearchResult.SpecimenResult>()
        val locations = results.filterIsInstance<SearchResult.LocationResult>()
        val guides = results.filterIsInstance<SearchResult.GuideResult>()
        val periods = results.filterIsInstance<SearchResult.PeriodResult>()
        val elements = results.filterIsInstance<SearchResult.ElementResult>()
        listOfNotNull(
            if (specimens.isNotEmpty()) "Specimens" to specimens.sortedBy { it.spec.name } else null,
            if (locations.isNotEmpty()) "Dig Sites, Shops & Metaphysical" to locations.sortedBy { it.loc.name } else null,
            if (guides.isNotEmpty()) "Geology Guides" to guides.sortedBy { it.guide.title } else null,
            if (periods.isNotEmpty()) "Prehistoric Periods" to periods.sortedBy { it.period.name } else null,
            if (elements.isNotEmpty()) "Periodic Table Elements" to elements.sortedBy { it.element.atomicNumber } else null,
        )
    }

    RockBackground {
        Column(modifier = Modifier.fillMaxSize()) {
        // Top bar with back button
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
                text = "Search",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f),
            )
        }
        // Search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Slate800)
                .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(16.dp)),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = null,
                    tint = if (isFocused) Citrine else TextLow,
                    modifier = Modifier.size(22.dp),
                )
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f).noAutoFocus(),
                    textStyle = TextStyle(
                        color = TextHigh,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (query.isEmpty()) {
                                Text(
                                    "Search specimens, fossils, dig sites, shops…",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextMid,
                                )
                            }
                            innerTextField()
                        }
                    },
                    singleLine = true,
                )
                if (query.isNotEmpty()) {
                    Icon(
                        Icons.Filled.Clear,
                        contentDescription = "Clear",
                        tint = TextLow,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { query = "" },
                    )
                }
            }
        }

        // Results
        if (query.isBlank()) {
            // Empty state suggestions
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Spacer(Modifier.height(32.dp))
                Text(
                    "Try searching for…",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextMid,
                    fontWeight = FontWeight.Bold,
                )
                listOf(
                    "\"quartz\" or \"ammonite\"",
                    "\"Crater of Diamonds\"",
                    "\"Igneous\" or \"Jurassic\"",
                    "\"rock shops near me\"",
                    "\"digs near me\"",
                ).forEach { suggestion ->
                    Text(
                        suggestion,
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkTextMid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E1C16))
                            .glowingBorder(1.dp, Color(0xFF1E1C16).copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .clickable { query = suggestion.trim('"') }
                            .padding(14.dp),
                    )
                }
            }
        } else if (isLocationSearching) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = Citrine,
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Finding locations near you…", color = TextMid, style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else if (results.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No results for \"$query\"", color = TextMid, style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp, end = 20.dp, bottom = 40.dp
                ),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                sections.forEach { (label, items) ->
                    item {
                        Text(
                            "${label} (${items.size})",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextMid,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                    items(items.size) { idx ->
                        val result = items[idx]
                        when (result) {
                            is SearchResult.SpecimenResult -> {
                                SpecimenRow(
                                    result.spec,
                                    repo = repo,
                                    onClick = {
                                        navController.navigate(Routes.specimen(result.spec.id))
                                    },
                                    onShareToProfile = { shareToProfileSpec = result.spec },
                                )
                            }
                            is SearchResult.LocationResult -> {
                                LocationRow(result.loc) {
                                    navController.navigate(Routes.location(result.loc.id))
                                }
                            }
                            is SearchResult.GuideResult -> {
                                GuideRow(result.guide) {
                                    navController.navigate(Routes.guide(result.guide.id))
                                }
                            }
                            is SearchResult.PeriodResult -> {
                                PeriodRow(result.period) {
                                    navController.navigate(Routes.period(result.period.id))
                                }
                            }
                            is SearchResult.ElementResult -> {
                                ElementRow(result.element) {
                                    navController.navigate(Routes.element(result.element.atomicNumber))
                                }
                            }
                        }
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
}

private fun performSearch(
    query: String,
    currentLocation: Pair<Double, Double> = Pair(34.5037, -93.6321),
    isNearMe: Boolean = false,
): List<SearchResult> {
    val q = query.lowercase().trim()
    val results = mutableListOf<SearchResult>()

    // Handle "near me" location-based queries
    if (isNearMe) {
        // Determine what type of place the user is looking for
        val lookingForRockShops = q.contains("rock shop") || q.contains("shop")
        val lookingForDigs = q.contains("dig") || q.contains("mine") || q.contains("quarry")
        val lookingForBeaches = q.contains("beach") || q.contains("shore")
        val lookingForClubs = q.contains("club") || q.contains("lapidary")
        val lookingForAll = !lookingForRockShops && !lookingForDigs && !lookingForBeaches && !lookingForClubs

        for (loc in SeedData.allLocations) {
            val miles = AppRepository.distanceMiles(
                currentLocation.first, currentLocation.second,
                loc.latitude, loc.longitude,
            )
            if (miles <= 50.0) {
                val matches = when {
                    lookingForRockShops -> loc.type == LocationType.ROCK_SHOP
                    lookingForDigs -> loc.type == LocationType.PUBLIC_DIG ||
                        loc.type == LocationType.MINE ||
                        loc.type == LocationType.QUARRY
                    lookingForBeaches -> loc.type == LocationType.BEACH
                    lookingForClubs -> loc.type == LocationType.LAPIDARY_CLUB
                    else -> true
                }
                if (matches) {
                    results.add(SearchResult.LocationResult(loc))
                }
            }
        }
        return results.sortedBy {
            AppRepository.distanceMiles(
                currentLocation.first, currentLocation.second,
                (it as SearchResult.LocationResult).loc.latitude,
                it.loc.longitude,
            )
        }
    }

    // Standard search — specimens
    val allSpecimens = SeedData.allSpecimens
    for (spec in allSpecimens) {
        if (q in spec.name.lowercase() ||
            q in spec.category.lowercase() ||
            q in spec.tagline.lowercase() ||
            q in spec.description.lowercase() ||
            spec.commonColors.any { q in it.lowercase() } ||
            spec.whereFound.any { q in it.lowercase() }
        ) {
            results.add(SearchResult.SpecimenResult(spec))
        }
    }

    // Search locations
    for (loc in SeedData.allLocations) {
        if (q in loc.name.lowercase() ||
            q in loc.region.lowercase() ||
            q in loc.summary.lowercase() ||
            q in loc.type.label.lowercase() ||
            loc.mineralTags.any { q in it.lowercase() }
        ) {
            results.add(SearchResult.LocationResult(loc))
        }
    }

    // Search guides
    for (guide in SeedData.guides) {
        if (q in guide.title.lowercase() ||
            q in guide.intro.lowercase() ||
            q in guide.rockClass.label.lowercase()
        ) {
            results.add(SearchResult.GuideResult(guide))
        }
    }

    // Search fossil periods
    for (period in SeedData.fossilPeriods) {
        if (q in period.name.lowercase() ||
            q in period.era.lowercase() ||
            q in period.climate.lowercase() ||
            period.majorEvents.any { q in it.lowercase() } ||
            period.keyOrganisms.any { q in it.name.lowercase() || q in it.type.lowercase() }
        ) {
            results.add(SearchResult.PeriodResult(period))
        }
    }

    // Search periodic table elements
    for (element in PeriodicTableElements.elements) {
        if (q == element.symbol.lowercase() ||
            q in element.name.lowercase() ||
            element.atomicNumber.toString() == q ||
            q in element.category.label.lowercase() ||
            q in element.inRocks.lowercase() ||
            q in element.summary.lowercase()
        ) {
            results.add(SearchResult.ElementResult(element))
        }
    }

    return results
}

@Composable
private fun SpecimenRow(
    spec: Specimen,
    repo: com.rork.rockscout.data.AppRepository,
    onClick: () -> Unit,
    onShareToProfile: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val accent = rockClassColor(spec.rockClass)
    val collection by repo.collection.collectAsStateWithLifecycle()
    val wishlist by repo.wishlist.collectAsStateWithLifecycle()
    val likedSpecimens by repo.likedSpecimens.collectAsStateWithLifecycle()
    val collected = collection.any { it.specimenId == spec.id }
    val wishlisted = wishlist.contains(spec.id)
    val isLiked = likedSpecimens.contains(spec.id)
    val imageUrls = SpecimenImages.urls[spec.id] ?: spec.imageUrls
    SpecimenListItem(
        specimen = spec,
        accent = accent,
        onClick = onClick,
        imageSize = 113.dp,
        addShare = {
            CompactAddShareButton(
                collected = collected,
                wishlisted = wishlisted,
                onCollectionToggle = { repo.toggleCollection(spec.id) },
                onWishlistToggle = { repo.toggleWishlist(spec.id) },
                onShareToProfile = onShareToProfile,
                onShareToSocial = {
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
                            fileName = "rockscout_search_${spec.id}",
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
                onToggle = { repo.toggleLike(spec.id) },
                accent = accent,
                size = 44.dp,
                likeCount = repo.specimenLikeCount(spec.id),
            )
        },
        showCategory = true,
        categoryLabel = shortCategoryLabel(spec.category),
    )
}

@Composable
private fun LocationRow(loc: DigLocation, onClick: () -> Unit) {
    DarkCard(
        accent = Success,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SpecimenGlyph(emoji = loc.type.emoji, accent = Success, size = 46)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(loc.name, style = MaterialTheme.typography.titleMedium, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(loc.region, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                    Spacer(Modifier.width(8.dp))
                    TagChip(loc.type.label, color = Success)
                }
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = DarkTextMid)
        }
    }
}

@Composable
private fun GuideRow(guide: RockGuide, onClick: () -> Unit) {
    val accent = rockClassColor(guide.rockClass)
    DarkCard(
        accent = accent,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SpecimenGlyph(emoji = guide.emoji, accent = accent, size = 46)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(guide.title, style = MaterialTheme.typography.titleMedium, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(guide.intro, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = DarkTextMid)
        }
    }
}

@Composable
private fun PeriodRow(period: FossilPeriod, onClick: () -> Unit) {
    DarkCard(
        accent = Fossil,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SpecimenGlyph(emoji = period.emoji, accent = Fossil, size = 46)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(period.name, style = MaterialTheme.typography.titleMedium, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(period.timeRange, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = DarkTextMid)
        }
    }
}

@Composable
private fun ElementRow(element: PeriodicTableElements.Element, onClick: () -> Unit) {
    val accent = Color(element.category.colorHex)
    DarkCard(
        accent = accent,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.18f))
                    .glowingBorder(2.dp, accent.copy(alpha = 0.55f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    element.symbol,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(element.name, style = MaterialTheme.typography.titleMedium, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    "${element.category.label} · Atomic #${element.atomicNumber}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                    maxLines = 1,
                )
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = DarkTextMid)
        }
    }
}

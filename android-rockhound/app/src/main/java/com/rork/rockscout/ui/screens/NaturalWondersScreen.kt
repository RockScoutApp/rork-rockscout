package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.FavoriteSpotResolver
import com.rork.rockscout.data.NaturalWonder
import com.rork.rockscout.data.NaturalWondersData
import com.rork.rockscout.data.UsRegion
import com.rork.rockscout.data.WonderType
import com.rork.rockscout.data.usRegion
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.FullScreenImageViewer
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NaturalWondersScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<WonderType?>(null) }
    var selectedRegion by remember { mutableStateOf<UsRegion?>(null) }
    var viewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerInitialPage by remember { mutableIntStateOf(0) }
    var shareWonder by remember { mutableStateOf<NaturalWonder?>(null) }
    val repo = AppRepository.instance
    val favorites by repo.favoriteSpots.collectAsStateWithLifecycle()

    val allWonders = remember { NaturalWondersData.allWonders }
    val filteredWonders = remember(allWonders, searchQuery, selectedType, selectedRegion) {
        var list = allWonders
        if (selectedType != null) {
            list = list.filter { it.type == selectedType }
        }
        if (selectedRegion != null) {
            list = list.filter { it.usRegion() == selectedRegion }
        }
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.lowercase().trim()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                    it.location.lowercase().contains(q) ||
                    it.description.lowercase().contains(q) ||
                    it.rocksToFind.any { rock -> rock.lowercase().contains(q) }
            }
        }
        list
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Natural Wonders",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
        containerColor = Color.Transparent,
    ) { innerPadding ->
        RockBackground {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp,
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = 40.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Hero
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(26.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF1B3A4B),
                                        Color(0xFF0D1F2D),
                                        Color(0xFF162A38),
                                    )
                                )
                            )
                            .padding(24.dp),
                    ) {
                        Column {
                            Text(
                                "🌍 Natural Wonders of the Earth",
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextHigh,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "World-famous geological sites — how they formed, and what rocks & minerals you can find there. " +
                                    "From the Grand Canyon to Giant's Causeway, these are the places where geology comes alive.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMid,
                            )
                        }
                    }
                }

                // Search bar
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E1C16))
                            .glowingBorder(1.dp, Color(0xFF1E1C16).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = TextLow, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        androidx.compose.material3.OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search wonders, locations, rocks…", style = MaterialTheme.typography.bodyMedium, color = TextLow) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextHigh,
                                unfocusedTextColor = TextHigh,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                // Type filter chips
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        WonderFilterChip("All Types", selectedType == null) { selectedType = null }
                        WonderType.entries.forEach { type ->
                            WonderFilterChip(type.label, selectedType == type) { selectedType = type }
                        }
                    }
                }

                // US region filter chips
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        WonderFilterChip("All Regions", selectedRegion == null, selectedColor = Citrine) { selectedRegion = null }
                        UsRegion.entries.forEach { region ->
                            WonderFilterChip(region.label, selectedRegion == region, selectedColor = Citrine) { selectedRegion = region }
                        }
                    }
                }

                // Result count
                item {
                    Text(
                        "${filteredWonders.size} wonder${if (filteredWonders.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextLow,
                    )
                }

                // Wonder cards
                items(filteredWonders, key = { it.id }) { wonder ->
                    val favId = FavoriteSpotResolver.wonderId(wonder.id)
                    WonderCard(
                        wonder = wonder,
                        isFavorited = favorites.contains(favId),
                        onToggleFavorite = { repo.toggleFavoriteSpot(favId) },
                        onPhotoClick = { urls, page ->
                            viewerUrls = urls
                            viewerInitialPage = page
                        },
                        onShare = { shareWonder = wonder },
                    )
                }

                if (filteredWonders.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(40.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "No wonders found. Try a different search or filter.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextLow,
                            )
                        }
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
        }
    }

    shareWonder?.let { wonder ->
        ShareToProfileComposer(
            sourceType = "natural_wonder",
            title = wonder.name,
            tagline = wonder.location + "  •  " + wonder.type.label,
            imageUri = wonder.imageUrl,
            locationText = wonder.location,
            onDismiss = { shareWonder = null },
        )
    }
}

@Composable
private fun WonderFilterChip(
    label: String,
    isSelected: Boolean,
    selectedColor: Color = Aqua,
    onClick: () -> Unit,
) {
    val accent = if (isSelected) selectedColor else Color(0xFF2A2820)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(accent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) Color.Black else TextMid,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

@Composable
private fun WonderCard(
    wonder: NaturalWonder,
    isFavorited: Boolean,
    onToggleFavorite: () -> Unit,
    onPhotoClick: (List<String>, Int) -> Unit,
    onShare: () -> Unit,
) {
    val typeColor = wonder.type.accentColor()
    DarkCard(
        accent = typeColor,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
    ) {
        // Hero image — uses 16:9 aspect ratio + Crop so the photo fills the frame
        // without letterboxing, while still showing the full scenic composition.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF1A1812))
                .clickable { onPhotoClick(listOf(wonder.imageUrl), 0) },
        ) {
            AsyncImage(
                model = wonder.imageUrl,
                contentDescription = wonder.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            // Gradient overlay for text legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    ),
            )
            // Type badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(typeColor.copy(alpha = 0.85f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    wonder.type.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                )
            }
            // Location
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = typeColor, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    wonder.location,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            // Favorite + Share buttons
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .sculpted(
                            shape = RoundedCornerShape(8.dp),
                            accent = if (isFavorited) Citrine else typeColor,
                            shadowElevation = 2.dp,
                            onClick = onToggleFavorite,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        if (isFavorited) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = if (isFavorited) "Remove from Favorite Spots" else "Add to Favorite Spots",
                        tint = if (isFavorited) Citrine else Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .sculpted(
                            shape = RoundedCornerShape(8.dp),
                            accent = typeColor,
                            shadowElevation = 2.dp,
                            onClick = onShare,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Share, contentDescription = "Share", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Title
        Text(
            wonder.name,
            style = MaterialTheme.typography.titleLarge,
            color = DarkTextHigh,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(8.dp))

        // Description
        Text(
            wonder.description,
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )

        Spacer(Modifier.height(10.dp))

        // Formation section
        Text(
            "How It Formed",
            style = MaterialTheme.typography.titleSmall,
            color = typeColor,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            wonder.formation,
            style = MaterialTheme.typography.bodySmall,
            color = TextMid,
        )

        Spacer(Modifier.height(10.dp))

        // Rocks to find
        Text(
            "What to Find There",
            style = MaterialTheme.typography.titleSmall,
            color = Citrine,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            wonder.rocksToFind.forEach { rock ->
                TagChip(rock, color = Citrine)
            }
        }

        Spacer(Modifier.height(10.dp))

        // Fun facts
        Text(
            "Did You Know?",
            style = MaterialTheme.typography.titleSmall,
            color = Aqua,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        wonder.funFacts.forEach { fact ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                Text("•", style = MaterialTheme.typography.bodySmall, color = Aqua, modifier = Modifier.padding(end = 6.dp))
                Text(fact, style = MaterialTheme.typography.bodySmall, color = TextMid, maxLines = 4, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

/** Accent color per wonder type for visual variety. */
private fun WonderType.accentColor(): Color = when (this) {
    WonderType.VOLCANIC -> Color(0xFFE2574C)
    WonderType.SEDIMENTARY -> Color(0xFFD9B26A)
    WonderType.METAMORPHIC -> Color(0xFF9B7BD8)
    WonderType.IMPACT -> Color(0xFFC0C0C0)
    WonderType.KARST -> Color(0xFF5CC98C)
    WonderType.COASTAL -> Color(0xFF44AACC)
    WonderType.DESERT -> Color(0xFFE8A33D)
    WonderType.MOUNTAIN -> Color(0xFF6FA8C7)
}

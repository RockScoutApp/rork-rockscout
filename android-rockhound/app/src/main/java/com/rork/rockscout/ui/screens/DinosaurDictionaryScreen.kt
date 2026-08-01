package com.rork.rockscout.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.data.DinoDictionary
import com.rork.rockscout.data.DinoDiet
import com.rork.rockscout.data.DinoEntry
import com.rork.rockscout.data.DinoEra
import com.rork.rockscout.data.DinoImageMap
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.DinoSilhouette
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.delay

/** Era accent colors — each era gets a vibrant, distinct color theme. */
private val eraColors: Map<DinoEra, Color> = mapOf(
    DinoEra.TRIASSIC to Color(0xFFE8A33D),
    DinoEra.JURASSIC to Color(0xFF8BBF6A),
    DinoEra.CRETACEOUS to Color(0xFF6FBF8A),
    DinoEra.PALEOGENE to Color(0xFF9B7BD8),
    DinoEra.NEOGENE to Color(0xFFE2574C),
    DinoEra.QUATERNARY to Color(0xFF5090B0),
    DinoEra.OTHER to Color(0xFF7CB5EC),
)

/** Diet colors — smaller accent tags for diet classification. */
private val dietColors: Map<DinoDiet, Color> = mapOf(
    DinoDiet.CARNIVORE to Color(0xFFE2574C),
    DinoDiet.HERBIVORE to Color(0xFF8BBF6A),
    DinoDiet.OMNIVORE to Color(0xFFE8A33D),
    DinoDiet.PISCIVORE to Color(0xFF5090B0),
    DinoDiet.FILTER_FEEDER to Color(0xFF6FBF8A),
    DinoDiet.INSECTIVORE to Color(0xFF9B7BD8),
    DinoDiet.SCAVENGER to Color(0xFF8B7A60),
)

private val eraOrder = listOf(
    DinoEra.TRIASSIC, DinoEra.JURASSIC, DinoEra.CRETACEOUS,
    DinoEra.PALEOGENE, DinoEra.NEOGENE, DinoEra.QUATERNARY, DinoEra.OTHER,
)

@Composable
fun DinosaurDictionaryScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }
    var selectedDiet by remember { mutableStateOf<DinoDiet?>(null) }
    var expandedEras by remember { mutableStateOf(setOf<DinoEra>()) }
    var selectedEntry by remember { mutableStateOf<DinoEntry?>(null) }

    // Debounce search — wait 200ms after last keystroke
    LaunchedEffect(searchQuery) {
        snapshotFlow { searchQuery }
            .collect { query ->
                delay(200)
                debouncedQuery = query.trim().lowercase()
            }
    }

    val filtered = remember(debouncedQuery, selectedDiet) {
        DinoDictionary.all.filter { entry ->
            val matchesSearch = debouncedQuery.isBlank() ||
                entry.name.lowercase().contains(debouncedQuery) ||
                entry.period.lowercase().contains(debouncedQuery) ||
                entry.description.lowercase().contains(debouncedQuery) ||
                entry.habitat.lowercase().contains(debouncedQuery) ||
                entry.foundIn.any { it.lowercase().contains(debouncedQuery) }
            val matchesDiet = selectedDiet == null || entry.diet == selectedDiet
            matchesSearch && matchesDiet
        }
    }

    val groupedByEra = remember(filtered) {
        filtered.groupBy { it.era }
    }

    ScreenScaffold(title = "Dinosaur Dictionary", onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Hero intro card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1A2818), Color(0xFF142012), Color(0xFF0E1A0C))
                            )
                        )
                        .padding(20.dp),
                ) {
                    Column {
                        Text(
                            "Explore ${DinoDictionary.count}+ prehistoric animals from 500 million years of Earth history — from the first dinosaurs to the Ice Age giants. Every era, every diet, every body plan.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextMid,
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TagChip("${DinoDictionary.count} animals", color = Color(0xFF8BBF6A))
                            TagChip("7 eras", color = Color(0xFF6FBF8A))
                            TagChip("All continents", color = Color(0xFF5090B0))
                        }
                    }
                }
            }

            // Search bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search dinosaurs, eras, locations…", color = DarkTextLow) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Aqua) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Close, contentDescription = "Clear", tint = Aqua)
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                )
            }

            // Diet filter chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        DietFilterChip(
                            label = "All",
                            selected = selectedDiet == null,
                            color = Aqua,
                            onClick = { selectedDiet = null },
                        )
                    }
                    items(DinoDiet.entries.toList()) { diet ->
                        DietFilterChip(
                            label = diet.label,
                            selected = selectedDiet == diet,
                            color = dietColors[diet] ?: Aqua,
                            onClick = {
                                selectedDiet = if (selectedDiet == diet) null else diet
                            },
                        )
                    }
                }
            }

            // Results count
            item {
                Text(
                    text = if (debouncedQuery.isBlank() && selectedDiet == null) {
                        "Browse all ${filtered.size} animals by era"
                    } else {
                        "${filtered.size} result${if (filtered.size != 1) "s" else ""}"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMid,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }

            // Era sections
            eraOrder.forEach { era ->
                val entries = groupedByEra[era] ?: return@forEach
                val eraColor = eraColors[era] ?: Aqua
                val isExpanded = expandedEras.contains(era) || debouncedQuery.isNotBlank()

                item {
                    EraSectionHeader(
                        era = era,
                        count = entries.size,
                        accent = eraColor,
                        isExpanded = isExpanded,
                        onToggle = {
                            expandedEras = if (isExpanded) {
                                expandedEras - era
                            } else {
                                expandedEras + era
                            }
                        },
                    )
                }

                if (isExpanded) {
                    items(entries, key = { it.id }) { entry ->
                        DinoCard(
                            entry = entry,
                            eraColor = eraColor,
                            onClick = { selectedEntry = entry },
                        )
                    }
                }
            }

            // Empty state
            if (filtered.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            DinoSilhouette(
                                bodyPlan = com.rork.rockscout.ui.components.DinoBodyPlan.THEROPOD_LARGE,
                                color = DarkTextLow,
                                modifier = Modifier.size(80.dp),
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "No dinosaurs found",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextMid,
                            )
                            Text(
                                "Try a different search or filter",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextLow,
                            )
                        }
                    }
                }
            }
        }
    }

    // Full-page detail popup
    selectedEntry?.let { entry ->
        DinoDetailSheet(
            entry = entry,
            onDismiss = { selectedEntry = null },
        )
    }
}

@Composable
private fun DietFilterChip(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
) {
    val bgAlpha by animateFloatAsState(if (selected) 0.25f else 0.08f, label = "chip")
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = bgAlpha))
            .glowingBorder(1.dp, color.copy(alpha = if (selected) 0.5f else 0.2f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) color else DarkTextMid,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
private fun EraSectionHeader(
    era: DinoEra,
    count: Int,
    accent: Color,
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(accent.copy(alpha = 0.08f))
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(accent)
                .glowingBorder(1.dp, accent.copy(alpha = 0.4f), CircleShape),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = era.label.uppercase(),
                style = MaterialTheme.typography.titleSmall,
                color = accent,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = era.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = DarkTextLow,
            )
        }
        Text(
            text = "$count",
            style = MaterialTheme.typography.labelLarge,
            color = accent,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.width(8.dp))
        Icon(
            imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = accent,
        )
    }
}

@Composable
private fun DinoCard(
    entry: DinoEntry,
    eraColor: Color,
    onClick: () -> Unit,
) {
    val silhouetteColor = Color(entry.accentColor)
    val dietColor = dietColors[entry.diet] ?: eraColor
    val imageUrl = DinoImageMap.imageUrl(entry)

    DarkCard(
        accent = eraColor,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
    ) {
        Column(
            modifier = Modifier.clickable(onClick = onClick),
        ) {
            // Paleoart image hero area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2.2f)
                    .background(
                        Brush.radialGradient(
                            listOf(silhouetteColor.copy(alpha = 0.22f), Color(0xFF1A1812)),
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "${entry.name} illustration",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    // Fallback to silhouette if no image
                    DinoSilhouette(
                        bodyPlan = entry.bodyPlan,
                        color = silhouetteColor.copy(alpha = 0.85f),
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .aspectRatio(1.8f),
                    )
                }
                // Gradient overlay for text readability at bottom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                0.6f to Color.Transparent,
                                1f to Color.Black.copy(alpha = 0.4f),
                            )
                        )
                )
                // Diet badge in corner
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(dietColor.copy(alpha = 0.75f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = entry.diet.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                    )
                }
            }

            // Info section
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = entry.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = entry.tagline().ifBlank { entry.description },
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    TagChip(entry.period, color = eraColor)
                    TagChip(entry.length, color = silhouetteColor)
                }
            }
        }
    }
}

/** Extract a short tagline from the description or fun facts. */
private fun DinoEntry.tagline(): String {
    return description.take(80).let { if (description.length > 80) "$it…" else it }
}

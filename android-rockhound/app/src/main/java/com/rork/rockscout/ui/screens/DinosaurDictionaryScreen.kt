package com.rork.rockscout.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PictureAsPdf
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
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
import com.rork.rockscout.data.ScreenPdfExporter
import com.rork.rockscout.data.ScreenPdfItem
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.DinoBodyPlan
import com.rork.rockscout.ui.components.DinoSilhouette
import com.rork.rockscout.ui.components.MetricText
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

/** Body plan filter group labels — group related body plans for easier filtering. */
private data class BodyTypeFilter(
    val label: String,
    val color: Color,
    val bodyPlans: Set<DinoBodyPlan>,
)

private val bodyTypeFilters: List<BodyTypeFilter> = listOf(
    BodyTypeFilter("Theropods", Color(0xFFE2574C), setOf(
        DinoBodyPlan.THEROPOD_LARGE, DinoBodyPlan.THEROPOD_SMALL,
    )),
    BodyTypeFilter("Sauropods", Color(0xFF8BBF6A), setOf(
        DinoBodyPlan.SAUROPOD,
    )),
    BodyTypeFilter("Ceratopsians", Color(0xFF6FBF8A), setOf(
        DinoBodyPlan.CERATOPSIAN,
    )),
    BodyTypeFilter("Armored", Color(0xFF9B7BD8), setOf(
        DinoBodyPlan.STEGOSAUR, DinoBodyPlan.ANKYLOSAUR,
    )),
    BodyTypeFilter("Ornithopods", Color(0xFFE8A33D), setOf(
        DinoBodyPlan.ORNITHOPOD, DinoBodyPlan.THERIZINOSAUR,
    )),
    BodyTypeFilter("Marine Reptiles", Color(0xFF5090B0), setOf(
        DinoBodyPlan.PLESIOSAUR, DinoBodyPlan.ICHTHYOSAUR,
        DinoBodyPlan.MOSASAUR, DinoBodyPlan.ELASMOSAUR,
        DinoBodyPlan.SHARK_GIANT, DinoBodyPlan.CROCODILIAN,
    )),
    BodyTypeFilter("Flyers", Color(0xFF7CB5EC), setOf(
        DinoBodyPlan.PTEROSAUR, DinoBodyPlan.BIRD_PREHISTORIC,
    )),
    BodyTypeFilter("Ice Age Mammals", Color(0xFFB0D0E0), setOf(
        DinoBodyPlan.MAMMOTH, DinoBodyPlan.SABERTOOTH, DinoBodyPlan.RHINO_GIANT,
        DinoBodyPlan.SLOTH_GIANT, DinoBodyPlan.BEAR_GIANT,
        DinoBodyPlan.WOLF_PREHISTORIC, DinoBodyPlan.SYNAPSID,
    )),
)

/** Height range filter buckets in feet. */
private data class HeightRange(
    val label: String,
    val minFeet: Float,
    val maxFeet: Float,
    val color: Color,
)

private val heightRanges: List<HeightRange> = listOf(
    HeightRange("Tiny (< 3 ft)", 0f, 3f, Color(0xFF6FBF8A)),
    HeightRange("Small (3–10 ft)", 3f, 10f, Color(0xFFE8A33D)),
    HeightRange("Medium (10–25 ft)", 10f, 25f, Color(0xFF9B7BD8)),
    HeightRange("Large (25–50 ft)", 25f, 50f, Color(0xFF5090B0)),
    HeightRange("Giant (50+ ft)", 50f, Float.MAX_VALUE, Color(0xFFE2574C)),
)

/** Sort options for the dictionary. */
private enum class SortMode(val label: String) {
    BY_ERA("By Era"),
    BY_SIZE("Largest First"),
    BY_NAME("A–Z"),
    BY_NAME_DESC("Z–A"),
}

/**
 * Extract the length in feet from strings like "40 ft", "10–20 ft", "23 ft wingspan".
 * Returns the max value for ranges.
 */
private fun estimateLengthFeet(length: String): Float {
    val rangeMatch = Regex("""([\d.]+)\s*[–-]\s*([\d.]+)\s*ft""").find(length)
    if (rangeMatch != null) {
        return rangeMatch.groupValues[2].toFloatOrNull() ?: 16f
    }
    val feetMatch = Regex("""([\d.]+)\s*ft""").find(length)
    if (feetMatch != null) {
        return feetMatch.groupValues[1].toFloatOrNull() ?: 16f
    }
    val inchMatch = Regex("""([\d.]+)\s*in""").find(length)
    if (inchMatch != null) {
        val inches = inchMatch.groupValues[1].toFloatOrNull() ?: 200f
        return inches / 12f
    }
    return 16f
}

@Composable
fun DinosaurDictionaryScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }
    var selectedDiet by remember { mutableStateOf<DinoDiet?>(null) }
    var selectedEra by remember { mutableStateOf<DinoEra?>(null) }
    var selectedBodyType by remember { mutableStateOf<BodyTypeFilter?>(null) }
    var selectedHeightRange by remember { mutableStateOf<HeightRange?>(null) }
    var sortMode by remember { mutableStateOf(SortMode.BY_ERA) }
    var showFilters by remember { mutableStateOf(false) }
    var expandedEras by remember { mutableStateOf(setOf<DinoEra>()) }
    var selectedEntry by remember { mutableStateOf<DinoEntry?>(null) }
    var isExportingPdf by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val pdfScope = rememberCoroutineScope()

    // Debounce search — wait 200ms after last keystroke
    LaunchedEffect(searchQuery) {
        snapshotFlow { searchQuery }
            .collect { query ->
                delay(200)
                debouncedQuery = query.trim().lowercase()
            }
    }

    // Check if any filters are active
    val hasActiveFilters = selectedDiet != null || selectedEra != null ||
        selectedBodyType != null || selectedHeightRange != null ||
        sortMode != SortMode.BY_ERA

    val filtered = remember(
        debouncedQuery, selectedDiet, selectedEra,
        selectedBodyType, selectedHeightRange, sortMode,
    ) {
        DinoDictionary.all
            .filter { entry ->
                val matchesSearch = debouncedQuery.isBlank() ||
                    entry.name.lowercase().contains(debouncedQuery) ||
                    entry.period.lowercase().contains(debouncedQuery) ||
                    entry.description.lowercase().contains(debouncedQuery) ||
                    entry.habitat.lowercase().contains(debouncedQuery) ||
                    entry.foundIn.any { it.lowercase().contains(debouncedQuery) }
                val matchesDiet = selectedDiet == null || entry.diet == selectedDiet
                val matchesEra = selectedEra == null || entry.era == selectedEra
                val matchesBodyType = selectedBodyType == null ||
                    entry.bodyPlan in selectedBodyType!!.bodyPlans
                val matchesHeight = selectedHeightRange == null || run {
                    val range = selectedHeightRange!!
                    val feet = estimateLengthFeet(entry.length)
                    feet >= range.minFeet && feet < range.maxFeet
                }
                matchesSearch && matchesDiet && matchesEra &&
                    matchesBodyType && matchesHeight
            }
            .let { list ->
                when (sortMode) {
                    SortMode.BY_ERA -> list
                    SortMode.BY_SIZE -> list.sortedByDescending { estimateLengthFeet(it.length) }
                    SortMode.BY_NAME -> list.sortedBy { it.name.lowercase() }
                    SortMode.BY_NAME_DESC -> list.sortedByDescending { it.name.lowercase() }
                }
            }
    }

    // Group by era only when sorting by era; otherwise flat list
    val groupedByEra = remember(filtered, sortMode) {
        if (sortMode == SortMode.BY_ERA) {
            filtered.groupBy { it.era }
        } else {
            emptyMap()
        }
    }

    ScreenScaffold(
        title = "Dinosaur Dictionary",
        onBack = { navController.popBackStack() },
        actions = {
            SculptedIconButton(
                icon = Icons.Filled.PictureAsPdf,
                contentDescription = "Export PDF",
                onClick = {
                    if (isExportingPdf) return@SculptedIconButton
                    isExportingPdf = true
                    pdfScope.launch {
                        val items = filtered.map { entry ->
                            ScreenPdfItem(
                                title = entry.name,
                                subtitle = "${entry.era.label}  •  ${entry.diet.label}  •  ${entry.period}",
                                accentRgb = entry.accentColor.toInt(),
                                imageUrl = DinoImageMap.imageUri(entry.id),
                                fields = listOf(
                                    "Era" to entry.era.label,
                                    "Period" to entry.period,
                                    "Age" to entry.age,
                                    "Diet" to entry.diet.label,
                                    "Length" to entry.length,
                                    "Weight" to entry.weight,
                                    "Habitat" to entry.habitat,
                                    "Found In" to entry.foundIn.joinToString(", "),
                                ).filter { it.second.isNotBlank() },
                                description = entry.description,
                            )
                        }
                        ScreenPdfExporter.export(
                            context = context,
                            docTitle = "Dinosaur Dictionary",
                            fileName = "RockScout_DinoDictionary",
                            items = items,
                        )
                        isExportingPdf = false
                    }
                },
                accent = Aqua,
                iconTint = DarkTextMid,
                size = 36.dp,
                shadowElevation = 3.dp,
                enabled = !isExportingPdf && filtered.isNotEmpty(),
            )
        },
    ) {
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

            // Search bar with filter toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
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
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                    )
                    FilterToggleButton(
                        active = showFilters || hasActiveFilters,
                        onClick = { showFilters = !showFilters },
                    )
                }
            }

            // Expandable filter panel
            item {
                AnimatedVisibility(
                    visible = showFilters,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    FilterPanel(
                        selectedDiet = selectedDiet,
                        onDietChange = { selectedDiet = it },
                        selectedEra = selectedEra,
                        onEraChange = { selectedEra = it },
                        selectedBodyType = selectedBodyType,
                        onBodyTypeChange = { selectedBodyType = it },
                        selectedHeightRange = selectedHeightRange,
                        onHeightRangeChange = { selectedHeightRange = it },
                        sortMode = sortMode,
                        onSortModeChange = { sortMode = it },
                        onClearAll = {
                            selectedDiet = null
                            selectedEra = null
                            selectedBodyType = null
                            selectedHeightRange = null
                            sortMode = SortMode.BY_ERA
                        },
                    )
                }
            }

            // Results count
            item {
                Text(
                    text = if (!hasActiveFilters && debouncedQuery.isBlank()) {
                        "Browse all ${filtered.size} animals by era"
                    } else {
                        "${filtered.size} result${if (filtered.size != 1) "s" else ""}" +
                            if (hasActiveFilters) " • tap filter icon to adjust" else ""
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMid,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }

            if (sortMode == SortMode.BY_ERA) {
                // Era sections
                eraOrder.forEach { era ->
                    val entries = groupedByEra[era] ?: return@forEach
                    val eraColor = eraColors[era] ?: Aqua
                    val isExpanded = expandedEras.contains(era) ||
                        debouncedQuery.isNotBlank() || hasActiveFilters

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
            } else {
                // Flat list when sorted by size/name
                items(filtered, key = { it.id }) { entry ->
                    val eraColor = eraColors[entry.era] ?: Aqua
                    DinoCard(
                        entry = entry,
                        eraColor = eraColor,
                        onClick = { selectedEntry = entry },
                    )
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
                                bodyPlan = DinoBodyPlan.THEROPOD_LARGE,
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

/**
 * Filter toggle button — circular icon button that highlights when filters are active.
 */
@Composable
private fun FilterToggleButton(
    active: Boolean,
    onClick: () -> Unit,
) {
    val bgColor by animateColorAsState(
        if (active) Aqua.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f),
        label = "filterBg",
    )
    val iconColor by animateColorAsState(
        if (active) Aqua else DarkTextMid,
        label = "filterIcon",
    )
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .glowingBorder(
                1.dp,
                if (active) Aqua.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(16.dp),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.FilterList,
            contentDescription = "Filters",
            tint = iconColor,
        )
    }
}

/**
 * Full filter panel with diet, era, body type, height range, and sort options.
 */
@Composable
private fun FilterPanel(
    selectedDiet: DinoDiet?,
    onDietChange: (DinoDiet?) -> Unit,
    selectedEra: DinoEra?,
    onEraChange: (DinoEra?) -> Unit,
    selectedBodyType: BodyTypeFilter?,
    onBodyTypeChange: (BodyTypeFilter?) -> Unit,
    selectedHeightRange: HeightRange?,
    onHeightRangeChange: (HeightRange?) -> Unit,
    sortMode: SortMode,
    onSortModeChange: (SortMode) -> Unit,
    onClearAll: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF142012))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Filters & Sort",
                style = MaterialTheme.typography.titleMedium,
                color = Aqua,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "Clear all",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFE2574C),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(onClick = onClearAll),
            )
        }

        // Sort mode
        FilterSectionLabel("Sort By")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(SortMode.entries.toList()) { mode ->
                FilterChip(
                    label = mode.label,
                    selected = sortMode == mode,
                    color = Aqua,
                    onClick = { onSortModeChange(mode) },
                )
            }
        }

        // Era filter
        FilterSectionLabel("Era")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    label = "All Eras",
                    selected = selectedEra == null,
                    color = Aqua,
                    onClick = { onEraChange(null) },
                )
            }
            items(eraOrder) { era ->
                FilterChip(
                    label = era.label,
                    selected = selectedEra == era,
                    color = eraColors[era] ?: Aqua,
                    onClick = { onEraChange(if (selectedEra == era) null else era) },
                )
            }
        }

        // Diet filter
        FilterSectionLabel("Diet")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    label = "All Diets",
                    selected = selectedDiet == null,
                    color = Aqua,
                    onClick = { onDietChange(null) },
                )
            }
            items(DinoDiet.entries.toList()) { diet ->
                FilterChip(
                    label = diet.label,
                    selected = selectedDiet == diet,
                    color = dietColors[diet] ?: Aqua,
                    onClick = { onDietChange(if (selectedDiet == diet) null else diet) },
                )
            }
        }

        // Body type filter
        FilterSectionLabel("Body Type")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    label = "All Types",
                    selected = selectedBodyType == null,
                    color = Aqua,
                    onClick = { onBodyTypeChange(null) },
                )
            }
            items(bodyTypeFilters) { bodyType ->
                FilterChip(
                    label = bodyType.label,
                    selected = selectedBodyType == bodyType,
                    color = bodyType.color,
                    onClick = {
                        onBodyTypeChange(if (selectedBodyType == bodyType) null else bodyType)
                    },
                )
            }
        }

        // Height range filter
        FilterSectionLabel("Size Range")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    label = "Any Size",
                    selected = selectedHeightRange == null,
                    color = Aqua,
                    onClick = { onHeightRangeChange(null) },
                )
            }
            items(heightRanges) { range ->
                FilterChip(
                    label = range.label,
                    selected = selectedHeightRange == range,
                    color = range.color,
                    onClick = {
                        onHeightRangeChange(if (selectedHeightRange == range) null else range)
                    },
                )
            }
        }
    }
}

@Composable
private fun FilterSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = DarkTextMid,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
    )
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
) {
    val bgAlpha by animateFloatAsState(if (selected) 0.25f else 0.08f, label = "fchip")
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
    val imageUrl = DinoImageMap.imageUri(entry)

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
                    // MetricText for the length tag — long-press to see metric
                    MetricText(
                        text = entry.length,
                        color = silhouetteColor,
                        fontSize = 11,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier,
                    )
                }
            }
        }
    }
}

/** Extract a short tagline from the description or fun facts. */
private fun DinoEntry.tagline(): String {
    return description.take(80).let { if (description.length > 80) "$it…" else it }
}

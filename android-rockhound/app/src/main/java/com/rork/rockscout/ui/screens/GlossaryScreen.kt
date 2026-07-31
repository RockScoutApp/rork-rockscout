package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.GLOSSARY_ENTRIES
import com.rork.rockscout.data.GLOSSARY_LETTERS
import com.rork.rockscout.data.GlossaryCategory
import com.rork.rockscout.data.GlossaryEntry
import com.rork.rockscout.ui.components.AlphabetIndex
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.InterstitialAdTrigger
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink

/** Accent color used for the search bar and letter index. */
private val GlossaryAccent = Aqua

@Composable
fun GlossaryScreen(navController: NavController) {
    InterstitialAdTrigger(screenKey = "glossary") {
        navController.navigate(Routes.PAYWALL)
    }
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<GlossaryCategory?>(null) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Filtered entries: match search text (term + definition) and category filter.
    val filtered = remember(query, selectedCategory) {
        val q = query.trim().lowercase()
        GLOSSARY_ENTRIES.filter { entry ->
            (selectedCategory == null || entry.category == selectedCategory) &&
                (q.isBlank() ||
                    entry.term.lowercase().contains(q) ||
                    entry.definition.lowercase().contains(q) ||
                    entry.category.label.lowercase().contains(q))
        }
    }

    // Group the filtered entries by first letter for section headers.
    val grouped = remember(filtered) {
        filtered.groupBy { it.term.first().uppercaseChar() }
            .toSortedMap()
    }

    ScreenScaffold(
        title = "Glossary",
        onBack = { navController.popBackStack() },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search bar — rounded pill matching the app's existing search inputs.
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = {
                    Text(
                        "Search terms, abbreviations, definitions…",
                        color = DarkTextMid,
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = null,
                        tint = GlossaryAccent,
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .sculpted(
                                    shape = CircleShape,
                                    accent = DarkTextMid,
                                    shadowElevation = 2.dp,
                                    circular = true,
                                    onClick = { query = "" },
                                )
                                .background(Color.Black),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = "Clear search",
                                tint = DarkTextHigh,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DarkTextHigh,
                    unfocusedTextColor = DarkTextHigh,
                    cursorColor = GlossaryAccent,
                    focusedBorderColor = GlossaryAccent,
                    unfocusedBorderColor = GlossaryAccent.copy(alpha = 0.4f),
                    focusedContainerColor = Color(0xFF1E1C16),
                    unfocusedContainerColor = Color(0xFF1E1C16),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            )

            // Category filter chips — horizontally scrollable so all chips fit on any screen.
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    CategoryChip(
                        label = "All",
                        accent = GlossaryAccent,
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                    )
                }
                items(GlossaryCategory.entries.toList()) { cat ->
                    CategoryChip(
                        label = cat.label,
                        accent = cat.accent,
                        selected = selectedCategory == cat,
                        onClick = {
                            selectedCategory = if (selectedCategory == cat) null else cat
                        },
                    )
                }
            }

            // Results count + entries list with a vertical A–Z jump index on the right edge.
            Row(
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 20.dp, end = 8.dp, bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                // Intro card.
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                                )
                            )
                            .padding(18.dp),
                    ) {
                        Text(
                            "Every scientific abbreviation, uncommon word, and phrase used across RockScout — explained in plain language. Search above, filter by category, or tap a letter to jump.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextMid,
                        )
                    }
                }

                if (filtered.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "📖",
                                style = MaterialTheme.typography.displayMedium,
                            )
                        }
                    }
                    item {
                        Text(
                            "No matching terms found",
                            style = MaterialTheme.typography.titleMedium,
                            color = DarkTextHigh,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    item {
                        Text(
                            "Try a different search word or clear the category filter.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextMid,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                // Results count (inline at top of the list).
                item {
                    Text(
                        text = "${filtered.size} ${if (filtered.size == 1) "term" else "terms"}" +
                            if (query.isNotBlank()) " matching \"$query\"" else "",
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkTextMid,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }

                grouped.forEach { (letter, entries) ->
                    item(key = "header_$letter") {
                        Text(
                            letter.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = GlossaryAccent,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
                        )
                    }
                    items(entries, key = { it.term }) { entry ->
                        GlossaryEntryCard(entry)
                    }
                }
                } // close LazyColumn

                // Vertical A–Z jump index on the right edge.
                AlphabetIndex(
                    names = filtered.map { it.term },
                    onLetterClick = { letter ->
                        val index = grouped.keys.indexOf(letter)
                        if (index >= 0) {
                            // Offset: 1 (results count) + accumulated items up to this letter.
                            var offset = 1
                            grouped.keys.take(index).forEach { key ->
                                offset += 1 + (grouped[key]?.size ?: 0)
                            }
                            scope.launch {
                                listState.animateScrollToItem(offset)
                            }
                        }
                    },
                    modifier = Modifier,
                )
            } // close Row
        } // close Column
    } // close ScreenScaffold
}

@Composable
private fun CategoryChip(
    label: String,
    accent: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(if (selected) accent.copy(alpha = 0.25f) else accent.copy(alpha = 0.10f))
            .glowingBorder(
                1.5.dp,
                if (selected) accent.copy(alpha = 0.8f) else accent.copy(alpha = 0.3f),
                shape,
            )
            .sculpted(
                shape = shape,
                accent = accent,
                shadowElevation = if (selected) 3.dp else 1.dp,
                onClick = onClick,
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) accent else DarkTextHigh,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun GlossaryEntryCard(entry: GlossaryEntry) {
    DarkCard(
        accent = entry.category.accent,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                entry.term,
                style = MaterialTheme.typography.titleMedium,
                color = entry.category.accent,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            TagChip(
                text = entry.category.label,
                color = entry.category.accent,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            entry.definition,
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
    }
}

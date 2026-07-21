package com.rork.rockscout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.SearchMatch
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.data.performGlobalSearch
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.components.glowingBorder

/**
 * Reusable global search bar styled after the Specimen Database search bar.
 */
@Composable
fun GlobalSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search every part of RockScout…",
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Slate800.copy(alpha = 0.6f))
            .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(16.dp))
            .noAutoFocus(),
        placeholder = {
            Text(
                placeholder,
                color = TextLow,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                contentDescription = "Search",
                tint = Citrine,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = "Clear",
                    tint = TextLow,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onQueryChange("") },
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
}

/**
 * Displays the top [maxResults] most relevant global search matches for [query].
 * Tapping a row navigates to the matching detail screen; tapping the footer
 * opens the full Search screen.
 */
@Composable
fun GlobalSearchResults(
    query: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    repo: AppRepository = AppRepository.instance,
    friends: List<SocialRepository.HunterProfile> = emptyList(),
    hunters: List<SocialRepository.HunterProfile> = emptyList(),
    maxResults: Int = 5,
) {
    val current by repo.currentLocation.collectAsStateWithLifecycle()

    val matches: List<SearchMatch> = remember(query, current, friends, hunters, maxResults) {
        performGlobalSearch(
            query = query,
            repo = repo,
            currentLocation = current,
            friends = friends,
            hunters = hunters,
            limit = maxResults,
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "Top ${matches.size} result${if (matches.size == 1) "" else "s"}",
                style = MaterialTheme.typography.labelMedium,
                color = TextMid,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "Searching across the whole app",
                style = MaterialTheme.typography.labelSmall,
                color = DarkTextMid,
            )
        }

        if (matches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Slate800.copy(alpha = 0.6f))
                    .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(16.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "No matches for \"$query\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMid,
                )
            }
        } else {
            matches.forEach { match ->
                SearchMatchRow(match = match) {
                    navController.navigate(match.route)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Citrine.copy(alpha = 0.15f))
                .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .clickable { navController.navigate(Routes.SEARCH) }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "Open full Search",
                style = MaterialTheme.typography.labelLarge,
                color = Citrine,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SearchMatchRow(
    match: SearchMatch,
    onClick: () -> Unit,
) {
    DarkCard(
        accent = match.accent,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        contentPadding = PaddingValues(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(match.accent.copy(alpha = 0.18f))
                    .glowingBorder(1.dp, match.accent.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    match.emoji,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    match.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    match.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.width(4.dp))
                TagChip(
                    text = match.typeLabel,
                    color = match.accent,
                    filled = false,
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = DarkTextMid,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/**
 * Convenience wrapper that adds a global search bar and, when the user types,
 * shows the top 5 relevant results. Designed for screens that already use a
 * [LazyColumn] — the caller provides the rest of the list content.
 */
@Composable
fun GlobalSearchSection(
    navController: NavController,
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search every part of RockScout…",
    friends: List<SocialRepository.HunterProfile> = emptyList(),
    hunters: List<SocialRepository.HunterProfile> = emptyList(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        GlobalSearchBar(
            query = query,
            onQueryChange = onQueryChange,
            placeholder = placeholder,
        )
        if (query.isNotBlank()) {
            GlobalSearchResults(
                query = query,
                navController = navController,
                friends = friends,
                hunters = hunters,
            )
        }
    }
}

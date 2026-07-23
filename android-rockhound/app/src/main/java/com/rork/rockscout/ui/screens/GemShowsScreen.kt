package com.rork.rockscout.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.CustomGemShowStore
import com.rork.rockscout.data.GemShow
import com.rork.rockscout.data.GemShowData
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.components.glowingBorder

/**
 * Shows the upcoming gem, mineral & fossil shows across the US,
 * grouped by month. Tapping a show opens its website in the browser.
 */
@Composable
fun GemShowsScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(GemShowData.PREFS_NAME, android.content.Context.MODE_PRIVATE) }
    val lastRefreshMs = remember { prefs.getLong(GemShowData.KEY_LAST_REFRESH_MS, 0L) }
    val currentMonth1 = remember { Calendar.getInstance().get(Calendar.MONTH) + 1 }

    // Initialize the custom show store so user-submitted shows load.
    LaunchedEffect(Unit) { CustomGemShowStore.initialize() }

    val customShows by CustomGemShowStore.shows.collectAsStateWithLifecycle()
    // Use the enriched, upcoming-sorted list (auto-refreshed monthly by the worker)
    val enriched = remember(customShows, currentMonth1) { GemShowData.upcomingShows(currentMonth1) }
    val grouped = remember(enriched) { groupByMonthOrdered(enriched) }
    var query by remember { mutableStateOf("") }
    var showSubmitDialog by remember { mutableStateOf(false) }

    val filtered = remember(query, grouped) {
        if (query.isBlank()) grouped
        else grouped.map { (month, shows) ->
            month to shows.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.city.contains(query, ignoreCase = true) ||
                    it.state.contains(query, ignoreCase = true)
            }
        }.filter { it.second.isNotEmpty() }
    }

    val lastUpdatedText = remember(lastRefreshMs) {
        if (lastRefreshMs > 0L) {
            "Updated " + DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(lastRefreshMs))
        } else {
            "Auto-updates at the end of each month"
        }
    }

    ScreenScaffold(title = "Gem & Mineral Shows", onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Explore,
                        contentDescription = null,
                        tint = Amethyst,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Upcoming US Shows",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "${GemShowData.totalShowCount()} recurring gem, mineral & fossil shows across the US. Dates reflect typical annual scheduling — confirm details on the show's website before traveling.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMid,
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        tint = Citrine,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        lastUpdatedText,
                        style = MaterialTheme.typography.labelMedium,
                        color = Citrine,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Slate800)
                        .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = null,
                        tint = Citrine,
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
                                        "Search by show or state…",
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
                Spacer(Modifier.height(12.dp))
                SculptedOutlinedButton(
                    text = "Submit a Show",
                    onClick = { showSubmitDialog = true },
                    accent = Citrine,
                    textColor = Citrine,
                    icon = Icons.Filled.Add,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            filtered.forEach { (month, shows) ->
                item {
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = Citrine,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            month,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                items(shows) { show ->
                    GemShowCard(show = show, onOpenWebsite = { url ->
                        SafeLinkOpener.openUrl(context, url)
                    })
                }
            }

            if (filtered.isEmpty()) {
                item {
                    Text(
                        "No shows match \"$query\".",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextMid,
                        modifier = Modifier.padding(top = 24.dp),
                    )
                }
            }
        }
    }

    if (showSubmitDialog) {
        SubmitShowDialog(
            onDismiss = { showSubmitDialog = false },
            onSubmitted = { approved ->
                showSubmitDialog = false
                if (approved) {
                    // Force a refresh of the custom show store so the new show appears.
                    CustomGemShowStore.initialize()
                }
            },
        )
    }
}

@Composable
private fun GemShowCard(show: GemShow, onOpenWebsite: (String) -> Unit) {
    DarkCard(
        accent = Amethyst,
        modifier = Modifier.fillMaxWidth().clickable { onOpenWebsite(show.website) },
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(show.name, style = MaterialTheme.typography.titleLarge, color = androidx.compose.ui.graphics.Color.White)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Aqua, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${show.city}, ${show.state}", style = MaterialTheme.typography.bodyMedium, color = DarkTextMid)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = Citrine, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(show.dateRange, style = MaterialTheme.typography.labelLarge, color = Citrine)
                }
                Spacer(Modifier.height(8.dp))
                Text(show.description, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TagChip("VENUE", color = Amethyst)
                    Spacer(Modifier.width(8.dp))
                    Text(show.venue, style = MaterialTheme.typography.labelMedium, color = DarkTextMid)
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocalActivity, contentDescription = null, tint = Aqua, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(show.entryFee, style = MaterialTheme.typography.labelMedium, color = Aqua)
                }
            }
            Icon(
                Icons.Filled.OpenInNew,
                contentDescription = "Open website",
                tint = DarkTextMid,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/**
 * Groups shows by month, ordered starting from the current month and wrapping
 * around the calendar year. "Various" shows appear last.
 */
private fun groupByMonthOrdered(shows: List<GemShow>): List<Pair<String, List<GemShow>>> {
    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December",
    )
    val currentMonth1 = Calendar.getInstance().get(Calendar.MONTH) + 1
    // Order: current month first, then each successive month, then "Various".
    val orderedMonths = (0 until 12).map { offset ->
        monthNames[((currentMonth1 - 1 + offset) % 12)]
    } + "Various"
    return orderedMonths.mapNotNull { month ->
        val showsInMonth = shows.filter { it.monthLabel == month }
        if (showsInMonth.isNotEmpty()) month to showsInMonth else null
    }
}

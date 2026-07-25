package com.rork.rockscout.ui.screens

import android.content.ClipData
import androidx.activity.compose.BackHandler
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.ui.components.FullScreenImageViewer
import com.rork.rockscout.ui.components.DevUserPopup
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import com.rork.rockscout.data.AdAnalyticsTracker
import com.rork.rockscout.data.BugLogger
import com.rork.rockscout.data.CustomDigLocationStore
import com.rork.rockscout.data.CustomGemShowStore
import com.rork.rockscout.data.LocationSubmissionStore
import com.rork.rockscout.data.GemShowSubmissionStore
import com.rork.rockscout.data.DigLocation
import com.rork.rockscout.data.DigSiteDiscoveryStore
import com.rork.rockscout.data.ImageReviewRepository
import com.rork.rockscout.data.MockDataSeeder
import com.rork.rockscout.data.MockDataSeeder.LocalAppeal
import com.rork.rockscout.data.MockDataSeeder.LocalDeletedAccountLog
import com.rork.rockscout.data.MockDataSeeder.LocalUserReport
import com.rork.rockscout.data.AppealRepository
import com.rork.rockscout.data.ReportRepository
import com.rork.rockscout.data.SpecimenSubmissionStore
import com.rork.rockscout.data.CustomSpecimenStore
import com.rork.rockscout.data.UserPinSubmissionStore
import com.rork.rockscout.data.SubscriptionAdminManager
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.LocalDataStore
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedDialogButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.Warning
import com.rork.rockscout.ui.theme.textOnDark
import com.rork.rockscout.ui.theme.textOnDarkMuted
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.components.glowingBorder

/**
 * Hidden Developer Console — Analytics, Moderation, Subscriptions, and Bug Log.
 *
 * Accessed only via 5 taps on the version text on the Home screen + PIN 081311.
 */
@Composable
fun DeveloperConsoleScreen(navController: NavController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        DevTab("Analytics", Icons.Filled.QueryStats),
        DevTab("Users", Icons.Filled.PersonSearch),
        DevTab("Moderation", Icons.Filled.Flag),
        DevTab("Bugs", Icons.Filled.BugReport),
        DevTab("Submit", Icons.Filled.AddLocation),
    )

    RockBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar with subtle title accent
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 16.dp, top = 52.dp, bottom = 8.dp),
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
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Developer Console",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Citrine),
                    )
                }
            }

            // Custom segmented tab bar — clean, pill-style, stacked icon + label
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                tabs.forEachIndexed { index, tab ->
                    val selected = selectedTab == index
                    val backgroundColor by animateDpAsState(
                        targetValue = if (selected) 1.dp else 0.dp,
                        animationSpec = tween(200),
                        label = "tab-elevation",
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(
                                elevation = if (selected) 6.dp else 0.dp,
                                shape = RoundedCornerShape(16.dp),
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (selected) {
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF2A2820), Color(0xFF1E1C16))
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        listOf(Slate800.copy(alpha = 0.5f), Slate800.copy(alpha = 0.3f))
                                    )
                                }
                            )
                            .glowingBorder(
                                1.5.dp,
                                if (selected) Citrine.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.08f),
                                RoundedCornerShape(16.dp),
                            )
                            .clickable { selectedTab = index }
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            tab.icon,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = if (selected) Citrine else DarkTextMid,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            tab.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) DarkTextHigh else DarkTextMid,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        )
                        if (selected) {
                            Spacer(Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Citrine),
                            )
                        }
                    }
                }
            }

            // Tab content with a soft top divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.08f)),
            )

            when (selectedTab) {
                0 -> AnalyticsTab()
                1 -> SubscriptionsTab(navController)
                2 -> ModerationTab()
                3 -> BugLogTab()
                4 -> SubmissionsTab()
            }
        }
    }
}

private data class DevTab(val label: String, val icon: ImageVector)

// ── Analytics Tab ──────────────────────────────────────────────────────────

@Composable
private fun AnalyticsTab() {
    val context = LocalContext.current
    val state by AdAnalyticsTracker.state.collectAsState()
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.fillMaxSize().navigationBarsPadding().imePadding(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Page title
        item {
            PageTitle("Ad Performance", "Live analytics for banner and interstitial ads")
        }

        // Stat cards grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Banner Impressions",
                        value = state.bannerImpressions.toString(),
                        accent = Aqua,
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        title = "Banner Clicks",
                        value = state.bannerClicks.toString(),
                        accent = Amethyst,
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Interstitial Impr.",
                        value = state.interstitialImpressions.toString(),
                        accent = Citrine,
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        title = "Interstitial Clicks",
                        value = state.interstitialClicks.toString(),
                        accent = Success,
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Est. Revenue",
                        value = AdAnalyticsTracker.estimatedRevenueFormatted(),
                        accent = Citrine,
                        modifier = Modifier.weight(1f),
                        large = true,
                    )
                    StatCard(
                        title = "eCPM",
                        value = AdAnalyticsTracker.combinedEcpmFormatted(),
                        accent = Aqua,
                        modifier = Modifier.weight(1f),
                        large = true,
                    )
                }
            }
        }

        // Ads enable/disable toggle
        item {
            DevCard(accent = if (state.adsEnabled) Success else Danger) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Ads Enabled",
                            style = MaterialTheme.typography.titleMedium,
                            color = DarkTextHigh,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            if (state.adsEnabled) "Real ads are serving to non-premium users"
                            else "All ads are suppressed globally",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextMid,
                        )
                    }
                    Switch(
                        checked = state.adsEnabled,
                        onCheckedChange = { AdAnalyticsTracker.setAdsEnabled(context, it) },
                    )
                }
            }
        }

        // Cooldown control
        item {
            DevCard(accent = Citrine) {
                Column {
                    Text(
                        "Interstitial Cooldown",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Current: ${state.interstitialCooldownMs / 1000}s",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(15_000L, 30_000L, 45_000L, 60_000L, 120_000L).forEach { ms ->
                            val selected = state.interstitialCooldownMs == ms
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (selected) Citrine.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.06f)
                                    )
                                    .glowingBorder(
                                        1.5.dp,
                                        if (selected) Citrine.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.12f),
                                        RoundedCornerShape(10.dp),
                                    )
                                    .clickable { AdAnalyticsTracker.setInterstitialCooldown(context, ms) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                            ) {
                                Text(
                                    "${ms / 1000}s",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (selected) Citrine else DarkTextMid,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
        }

        // Reset button
        item {
            var confirmReset by remember { mutableStateOf(false) }
            SculptedButton(
                text = "Reset Analytics Stats",
                onClick = { confirmReset = true },
                accent = Danger,
                containerColor = Danger.copy(alpha = 0.15f),
                textColor = Danger,
                icon = Icons.Filled.Delete,
                modifier = Modifier.fillMaxWidth(),
            )
            if (confirmReset) {
                AlertDialog(
                    onDismissRequest = { confirmReset = false },
                    title = { Text("Reset stats?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
                    text = { Text("This clears all impression/click/revenue counters. Settings are kept.", color = DarkTextMid) },
                    confirmButton = {
                        SculptedDialogButton(
                            text = "Reset",
                            onClick = {
                                AdAnalyticsTracker.reset(context)
                                confirmReset = false
                            },
                            accent = Danger,
                        )
                    },
                    dismissButton = {
                        SculptedTextButton(text = "Cancel", onClick = { confirmReset = false }, accent = Danger, textColor = Danger)
                    },
                    containerColor = Color(0xFF1E1C16),
                )
            }
        }
    }
}

@Composable
private fun PageTitle(title: String, subtitle: String) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            color = textOnDark,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = textOnDarkMuted,
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
    large: Boolean = false,
) {
    Box(
        modifier = modifier
            .sculpted(shape = RoundedCornerShape(18.dp), accent = accent, shadowElevation = 5.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .glowingBorder(3.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
            .padding(14.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accent)
                        .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTextMid,
                    fontWeight = FontWeight.Medium,
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                value,
                style = if (large) MaterialTheme.typography.headlineSmall
                else MaterialTheme.typography.titleLarge,
                color = accent,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

// ── Subscriptions Tab ─────────────────────────────────────────────────────

@Composable
private fun SubscriptionsTab(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var analytics by remember { mutableStateOf<SubscriptionAdminManager.SubAnalytics?>(null) }
    var results by remember { mutableStateOf<List<SubscriptionAdminManager.AdminUser>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var searched by remember { mutableStateOf(false) }
    var popupUserId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        analytics = SubscriptionAdminManager.computeAnalytics()
        results = SubscriptionAdminManager.searchUsers("")
        loading = false
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().navigationBarsPadding().imePadding(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { PageTitle("User Subscriptions", "Search users and manage subscription overrides") }

        // Analytics stat cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                analytics?.let { a ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(
                            title = "Total Users",
                            value = a.totalUsers.toString(),
                            accent = Aqua,
                            modifier = Modifier.weight(1f),
                        )
                        StatCard(
                            title = "Premium",
                            value = a.premiumUsers.toString(),
                            accent = Citrine,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(
                            title = "Free Users",
                            value = a.freeUsers.toString(),
                            accent = Amethyst,
                            modifier = Modifier.weight(1f),
                        )
                        StatCard(
                            title = "Conversion",
                            value = "%.1f%%".format(a.conversionRate * 100),
                            accent = Success,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(
                            title = "Est. MRR",
                            value = "$%,.2f".format(a.estimatedMrr()),
                            accent = Citrine,
                            modifier = Modifier.weight(1f),
                            large = true,
                        )
                        StatCard(
                            title = "Admin Overrides",
                            value = a.totalOverrides.toString(),
                            accent = Warning,
                            modifier = Modifier.weight(1f),
                            large = true,
                        )
                    }
                }
            }
        }

        // Search bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    searched = false
                },
                modifier = Modifier.fillMaxWidth().noAutoFocus(),
                placeholder = { Text("Search by name, email, or user ID…", color = DarkTextMid) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = DarkTextMid) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        SculptedTextButton(
                            text = "Clear",
                            onClick = {
                                searchQuery = ""
                                searched = false
                            },
                            accent = Aqua,
                            textColor = DarkTextMid,
                        )
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2A2820),
                    unfocusedContainerColor = Color(0xFF2A2820),
                    focusedTextColor = DarkTextHigh,
                    unfocusedTextColor = DarkTextHigh,
                    focusedIndicatorColor = Citrine,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Citrine,
                ),
                shape = RoundedCornerShape(14.dp),
            )
        }

        // Search button
        item {
            SculptedButton(
                text = "Search Users",
                onClick = {
                    scope.launch {
                        loading = true
                        results = SubscriptionAdminManager.searchUsers(searchQuery)
                        analytics = SubscriptionAdminManager.computeAnalytics()
                        loading = false
                        searched = true
                    }
                },
                accent = Citrine,
                containerColor = Citrine.copy(alpha = 0.18f),
                textColor = Citrine,
                icon = Icons.Filled.PersonSearch,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Loading / empty / results
        if (loading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Citrine.copy(alpha = 0.12f))
                                .glowingBorder(3.dp, Citrine.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Filled.PersonSearch,
                                contentDescription = null,
                                tint = Citrine,
                                modifier = Modifier.size(28.dp),
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Loading users…", color = DarkTextMid, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        } else if (results.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(DarkTextMid.copy(alpha = 0.10f))
                                .glowingBorder(3.dp, DarkTextMid.copy(alpha = 0.25f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Filled.PersonSearch,
                                contentDescription = null,
                                tint = DarkTextMid,
                                modifier = Modifier.size(32.dp),
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            if (searched) "No users match \"$searchQuery\"" else "No users found",
                            color = DarkTextMid,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        } else {
            item {
                Text(
                    "${results.size} user${if (results.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                    fontWeight = FontWeight.Medium,
                )
            }
            // Compact 2-column spreadsheet-style name grid
            item {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    gridItems(results, key = { it.id }) { user ->
                        DevUserNameCell(
                            name = user.display_name.ifBlank { "(no name)" },
                            avatar = user.avatar_emoji,
                            isPremium = user.is_premium || user.premium_badge,
                            isDeleted = false,
                            onClick = { popupUserId = user.id },
                        )
                    }
                }
            }
        }
    }

    popupUserId?.let { uid ->
        DevUserPopup(
            userId = uid,
            onDismiss = { popupUserId = null },
        )
    }
}

/** Compact spreadsheet-style name cell for the Users tab grid.
 *  Shows avatar + name (truncated) + small status dot. Clicking opens DevUserPopup. */
@Composable
private fun DevUserNameCell(
    name: String,
    avatar: String,
    isPremium: Boolean,
    isDeleted: Boolean,
    onClick: () -> Unit,
) {
    val accent = when {
        isDeleted -> Danger
        isPremium -> Citrine
        else -> DarkTextMid
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16))
                )
            )
            .glowingBorder(2.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(avatar, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.width(6.dp))
            Text(
                name,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextHigh,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            Spacer(Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(accent)
                    .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape),
            )
        }
    }
}

// ── Moderation Tab ─────────────────────────────────────────────────────────

@Composable
private fun ModerationTab() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var groups by remember { mutableStateOf<List<ReportRepository.ModerationGroup>>(emptyList()) }
    var imageReviews by remember { mutableStateOf<List<MockDataSeeder.LocalImageReview>>(emptyList()) }
    var appeals by remember { mutableStateOf<List<LocalAppeal>>(emptyList()) }
    var allReports by remember { mutableStateOf<List<LocalUserReport>>(emptyList()) }
    var deletedLogs by remember { mutableStateOf<List<LocalDeletedAccountLog>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var reinstating by remember { mutableStateOf<ReportRepository.ModerationGroup?>(null) }
    var screenshotViewerPath by remember { mutableStateOf<String?>(null) }
    var modSearchQuery by remember { mutableStateOf("") }
    var popupUserId by remember { mutableStateOf<String?>(null) }
    var pendingDeleteReviewId by remember { mutableStateOf<String?>(null) }
    var pendingRemoveReportId by remember { mutableStateOf<String?>(null) }

    suspend fun reloadAll() {
        groups = ReportRepository.instance.getAllModerationGroups()
        imageReviews = ImageReviewRepository.instance.getPendingReviews()
        appeals = AppealRepository.instance.getAllAppeals()
        allReports = ReportRepository.instance.getAllReports()
        deletedLogs = LocalDataStore.getTable<LocalDeletedAccountLog>(LocalDataStore.KEY_DELETED_ACCOUNT_LOGS)
            .sortedByDescending { it.deleted_at }
        loading = false
    }

    LaunchedEffect(Unit) { reloadAll() }

    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            EmptyState(
                icon = Icons.Filled.Flag,
                title = "Loading reports…",
                tint = Citrine,
            )
        }
        return
    }

    if (groups.isEmpty() && imageReviews.isEmpty() && appeals.isEmpty() && allReports.isEmpty() && deletedLogs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            EmptyState(
                icon = Icons.Filled.Flag,
                title = "No reports filed",
                subtitle = "The community is behaving itself. Nice.",
            )
        }
        return
    }

    val oneReport = groups.filter { it.reportCount == 1 }
    val twoReports = groups.filter { it.reportCount == 2 }
    val banned = groups.filter { it.reportCount >= 3 }

    // Filter reported users by search query (username or email)
    val modSearchLower = modSearchQuery.trim().lowercase()
    val allReportedGroups = (banned + twoReports + oneReport)
    val filteredGroups = if (modSearchLower.isBlank()) allReportedGroups
        else allReportedGroups.filter {
            it.reportedName.lowercase().contains(modSearchLower) ||
            run {
                val user = LocalDataStore.getTable<com.rork.rockscout.data.LocalUser>(LocalDataStore.KEY_USERS)
                    .firstOrNull { u -> u.id == it.reportedUserId }
                user?.email?.lowercase()?.contains(modSearchLower) == true
            }
        }

    LazyColumn(
        modifier = Modifier.fillMaxSize().navigationBarsPadding().imePadding(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { PageTitle("Moderation Queue", "Review reported users and image submissions") }

               // ── Search bar for reported users ──
        if (allReportedGroups.isNotEmpty()) {
            item {
                OutlinedTextField(
                    value = modSearchQuery,
                    onValueChange = { modSearchQuery = it },
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                    placeholder = { Text("Search reported users by name or email…", color = DarkTextMid) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = DarkTextMid) },
                    trailingIcon = {
                        if (modSearchQuery.isNotEmpty()) {
                            SculptedTextButton(
                                text = "Clear",
                                onClick = { modSearchQuery = "" },
                                accent = Aqua,
                                textColor = DarkTextMid,
                            )
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2A2820),
                        unfocusedContainerColor = Color(0xFF2A2820),
                        focusedTextColor = DarkTextHigh,
                        unfocusedTextColor = DarkTextHigh,
                        focusedIndicatorColor = Citrine,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Citrine,
                    ),
                    shape = RoundedCornerShape(14.dp),
                )
            }
        }

        // ── Reported users compact grid ──
        if (filteredGroups.isNotEmpty()) {
            item { SectionHeader("Reported Users", Warning, filteredGroups.size) }
            item {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    gridItems(filteredGroups, key = { it.reportedUserId }) { group ->
                        val reportAccent = when {
                            group.reportCount >= 3 -> Danger
                            group.reportCount == 2 -> Warning
                            else -> Citrine
                        }
                        DevUserNameCell(
                            name = group.reportedName,
                            avatar = group.reportedAvatar,
                            isPremium = false,
                            isDeleted = false,
                            onClick = { popupUserId = group.reportedUserId },
                        )
                    }
                }
            }
        }

        // ── Image Reviews section ──
        if (imageReviews.isNotEmpty()) {
            item { SectionHeader("Image Reviews", Aqua, imageReviews.size) }
            items(imageReviews, key = { it.id }) { review ->
                ImageReviewCard(
                    review = review,
                    onApprove = {
                        scope.launch {
                            val approved = ImageReviewRepository.instance.approveReview(review.id)
                            if (approved != null) {
                                // Apply the approved image
                                if (approved.type == "profile_background") {
                                    AppRepository.instance.setBackgroundImagePath(approved.image_uri)
                                } else if (approved.type == "field_capture" && approved.capture_id != null) {
                                    AppRepository.instance.addImageToCapture(approved.capture_id, approved.image_uri)
                                    AppRepository.instance.setCapturePendingImage(approved.capture_id, null)
                                }
                            }
                            reloadAll()
                        }
                    },
                    onDelete = {
                        pendingDeleteReviewId = review.id
                    },
                )
            }
        }

        // ── Appeals section ──
        if (appeals.isNotEmpty()) {
            item { SectionHeader("Appeals", Citrine, appeals.size) }
            items(appeals, key = { it.id }) { appeal ->
                AppealCard(
                    appeal = appeal,
                    onApprove = {
                        scope.launch {
                            AppealRepository.instance.resolveAppeal(appeal.id, true)
                            reloadAll()
                        }
                    },
                    onDeny = {
                        scope.launch {
                            AppealRepository.instance.resolveAppeal(appeal.id, false)
                            reloadAll()
                        }
                    },
                )
            }
        }

        // ── Deleted Accounts log (spreadsheet-style) ──
        if (deletedLogs.isNotEmpty()) {
            item { SectionHeader("Deleted Accounts Log", Danger, deletedLogs.size) }
            item { DeletedAccountsLogHeader() }
            items(deletedLogs, key = { it.id }) { log ->
                DeletedAccountLogRow(log = log)
            }
        }

        // ── Report log (spreadsheet-style, flat) ──
        if (allReports.isNotEmpty()) {
            item { SectionHeader("Report Log", Warning, allReports.size) }
            item { ReportLogHeader() }
            items(allReports, key = { it.id }) { report ->
                ReportLogRow(
                    report = report,
                    onScreenshotClick = { path -> screenshotViewerPath = path },
                )
            }
        }
    }

    reinstating?.let { group ->
        AlertDialog(
            onDismissRequest = { reinstating = null },
            title = { Text("Reinstate ${group.reportedName}?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This clears all ${group.reportCount} reports against this user and restores their social access. This cannot be undone.",
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                SculptedDialogButton(
                    text = "Reinstate",
                    onClick = {
                        scope.launch {
                            ReportRepository.instance.reinstateUser(group.reportedUserId)
                            reloadAll()
                            reinstating = null
                        }
                    },
                    accent = Success,
                )
            },
            dismissButton = {
                SculptedTextButton(text = "Cancel", onClick = { reinstating = null }, accent = Citrine, textColor = DarkTextMid)
            },
            containerColor = Color(0xFF1E1C16),
        )
    }

    // ── Image review delete confirmation ──
    pendingDeleteReviewId?.let { reviewId ->
        AlertDialog(
            onDismissRequest = { pendingDeleteReviewId = null },
            title = { Text("Delete this image review?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "The image will be rejected and a rejection email will be sent to the user. This cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                SculptedDialogButton(
                    text = "Delete",
                    onClick = {
                        val id = reviewId
                        pendingDeleteReviewId = null
                        scope.launch {
                            val deleted = ImageReviewRepository.instance.deleteReview(id)
                            if (deleted != null) {
                                if (deleted.type == "profile_background") {
                                    AppRepository.instance.setPendingBackgroundPath(null)
                                } else if (deleted.type == "field_capture" && deleted.capture_id != null) {
                                    AppRepository.instance.setCapturePendingImage(deleted.capture_id, null)
                                }
                                val userEmail = AuthRepository.instance.currentUserEmail
                                if (!userEmail.isNullOrBlank()) {
                                    sendImageRejectionEmail(userEmail, deleted.type, deleted.user_name)
                                }
                            }
                            reloadAll()
                        }
                    },
                    accent = Danger,
                )
            },
            dismissButton = { SculptedTextButton(text = "Cancel", onClick = { pendingDeleteReviewId = null }, accent = Danger, textColor = DarkTextMid) },
            containerColor = Color(0xFF1E1C16),
        )
    }

    // ── Per-report remove confirmation ──
    pendingRemoveReportId?.let { reportId ->
        AlertDialog(
            onDismissRequest = { pendingRemoveReportId = null },
            title = { Text("Remove this report?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This will dismiss the selected report. Other reports for this user will remain.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                SculptedDialogButton(
                    text = "Remove",
                    onClick = {
                        val id = reportId
                        pendingRemoveReportId = null
                        scope.launch {
                            ReportRepository.instance.removeReport(id)
                            reloadAll()
                        }
                    },
                    accent = Danger,
                )
            },
            dismissButton = { SculptedTextButton(text = "Cancel", onClick = { pendingRemoveReportId = null }, accent = Danger, textColor = DarkTextMid) },
            containerColor = Color(0xFF1E1C16),
        )
    }

    // Screenshot viewer for report log images
    screenshotViewerPath?.let { path ->
        val file = File(path)
        if (file.exists()) {
            FullScreenImageViewer(
                imageUrls = listOf(path),
                initialPage = 0,
                onDismiss = { screenshotViewerPath = null },
            )
        } else {
            // File may have been cleaned up — just clear state
            LaunchedEffect(Unit) { screenshotViewerPath = null }
        }
    }

    // Dev user popup overlay
    popupUserId?.let { uid ->
        DevUserPopup(
            userId = uid,
            onDismiss = { popupUserId = null },
        )
    }
}

/** Sends an image rejection email by calling the Cloudflare function. */
private suspend fun sendImageRejectionEmail(email: String, type: String, userName: String?) {
    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        runCatching {
            val allVals = com.rork.rockscout.Config.allValues
            val functionsUrl = allVals["EXPO_PUBLIC_RORK_FUNCTIONS_URL"] ?: return@withContext
            val payload = kotlinx.serialization.json.buildJsonObject {
                put("email", email)
                put("type", type)
                put("displayName", userName ?: "Rock Scout")
            }.toString()
            val client = io.ktor.client.HttpClient {
                install(io.ktor.client.plugins.HttpTimeout) {
                    requestTimeoutMillis = 15_000
                    connectTimeoutMillis = 10_000
                }
            }
            client.post("$functionsUrl/image-rejection-email") {
                contentType(io.ktor.http.ContentType.Application.Json)
                setBody(payload)
            }
            client.close()
        }
    }
}

@Composable
private fun ImageReviewCard(
    review: MockDataSeeder.LocalImageReview,
    onApprove: () -> Unit,
    onDelete: () -> Unit,
) {
    val accent = Aqua
    val time = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US).format(Date(review.created_at))
    val typeLabel = when (review.type) {
        "profile_background" -> "Profile Background"
        "field_capture" -> "Field Capture"
        else -> review.type
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = RoundedCornerShape(18.dp), accent = accent, shadowElevation = 5.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .glowingBorder(3.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
            .padding(16.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Thumbnail
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1A1812))
                        .glowingBorder(2.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        model = review.image_uri,
                        contentDescription = "Pending image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        review.user_name ?: "Unknown user",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                    Text(
                        "$typeLabel · $time",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                    )
                    Text(
                        "AI: ${review.reason}",
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Success.copy(alpha = 0.15f))
                        .glowingBorder(2.dp, Success.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                        .clickable { onApprove() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = Success, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Approve", style = MaterialTheme.typography.labelMedium, color = Success, fontWeight = FontWeight.Bold)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Danger.copy(alpha = 0.12f))
                        .glowingBorder(2.dp, Danger.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable { onDelete() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Delete, contentDescription = null, tint = Danger, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Delete", style = MaterialTheme.typography.labelMedium, color = Danger, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    tint: Color = DarkTextMid,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.10f))
                .glowingBorder(3.dp, tint.copy(alpha = 0.25f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(34.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            title,
            color = DarkTextMid,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        if (subtitle != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                subtitle,
                color = DarkTextMid.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun SectionHeader(text: String, color: Color, count: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
                .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), CircleShape),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.titleSmall,
            color = color,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(color.copy(alpha = 0.15f))
                .glowingBorder(2.dp, color.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                .padding(horizontal = 7.dp, vertical = 2.dp),
        ) {
            Text(
                count.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun ReportGroupCard(
    group: ReportRepository.ModerationGroup,
    isBanned: Boolean,
    onReinstate: (() -> Unit)?,
    onRemoveReport: (String) -> Unit = {},
) {
    val accent = if (isBanned) Danger else if (group.reportCount == 2) Warning else Citrine
    var expanded by remember { mutableStateOf(false) }

    BackHandler(enabled = expanded) { expanded = false }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = RoundedCornerShape(18.dp), accent = accent, shadowElevation = 5.dp, onClick = { expanded = !expanded })
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .glowingBorder(3.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
            .padding(16.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.15f))
                        .glowingBorder(3.dp, accent.copy(alpha = 0.45f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(group.reportedAvatar, style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        group.reportedName,
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "${group.reportCount} report${if (group.reportCount != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = accent,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
            // Reinstate button on its own row so it doesn't squeeze the text above
            if (isBanned && onReinstate != null) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Success.copy(alpha = 0.15f))
                            .glowingBorder(2.dp, Success.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                            .clickable { onReinstate() }
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                    ) {
                        Text(
                            "Reinstate",
                            style = MaterialTheme.typography.labelMedium,
                            color = Success,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.08f)),
                    )
                    Spacer(Modifier.height(12.dp))
                    group.reports.forEach { report ->
                        val time = SimpleDateFormat("MM/dd HH:mm", Locale.US)
                            .format(Date(report.created_at))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(accent)
                                    .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape),
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    report.reason ?: "(no reason)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkTextMid,
                                    fontWeight = FontWeight.Medium,
                                )
                                Text(
                                    "by ${report.reporter_name ?: "Unknown"} · $time",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkTextMid.copy(alpha = 0.7f),
                                )
                            }
                            report.screenshotPath?.let { path ->
                                val file = File(path)
                                if (file.exists()) {
                                    AsyncImage(
                                        model = file,
                                        contentDescription = "Screenshot",
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .glowingBorder(2.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                                    )
                                }
                            }
                            // Per-report remove button
                            Box(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Danger.copy(alpha = 0.12f))
                                    .glowingBorder(1.5.dp, Danger.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                    .clickable { onRemoveReport(report.id) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Text(
                                    "Remove",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Danger,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Bug Log Tab ────────────────────────────────────────────────────────────

@Composable
private fun BugLogTab() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val entries by BugLogger.entries.collectAsState()
    val discoveredSites by DigSiteDiscoveryStore.sites.collectAsState()
    val customCount by CustomDigLocationStore.locations.collectAsState()
    var expandedId by remember { mutableStateOf<String?>(null) }
    var confirmClear by remember { mutableStateOf(false) }

    BackHandler(enabled = expandedId != null) { expandedId = null }
    var confirmClearDiscovered by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var acceptMessage by remember { mutableStateOf<String?>(null) }
    var pendingDenySites by remember { mutableStateOf<List<com.rork.rockscout.data.DigSiteDiscoveryStore.DiscoveredSite>?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().navigationBarsPadding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Bug Log",
                        style = MaterialTheme.typography.headlineSmall,
                        color = textOnDark,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "${entries.size} entr${if (entries.size == 1) "y" else "ies"} captured",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textOnDarkMuted,
                    )
                }
                if (entries.isNotEmpty()) {
                    // Filled with a dark surface so the danger text stays legible
                    // over the agate background.
                    OutlinedButton(
                        onClick = { confirmClear = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .sculpted(shape = RoundedCornerShape(12.dp), accent = Danger, shadowElevation = 5.dp)
                            .background(Slate800, RoundedCornerShape(12.dp)),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Danger.copy(alpha = 0.8f), Danger.copy(alpha = 0.5f)))
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Danger),
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Clear all", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        if (entries.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    EmptyState(
                        icon = Icons.Filled.BugReport,
                        title = "No bugs logged",
                        subtitle = "Everything looks healthy for now.",
                    )
                }
            }
        } else {
            items(entries, key = { it.id }) { entry ->
                val accent = if (entry.isFatal) Danger else Citrine
                val isExpanded = expandedId == entry.id
                val time = SimpleDateFormat("MM/dd HH:mm:ss", Locale.US)
                    .format(Date(entry.timestamp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF2A2820), Color(0xFF1E1C16))
                            )
                        )
                        .glowingBorder(3.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .clickable { expandedId = if (isExpanded) null else entry.id }
                        .padding(14.dp),
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(accent)
                                    .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape),
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${entry.exceptionType}: ${entry.message.take(80)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DarkTextHigh,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = if (isExpanded) 10 else 2,
                                )
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    "${entry.screen} · $time · v${entry.appVersion}${if (entry.isFatal) " · FATAL" else ""}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkTextMid.copy(alpha = 0.7f),
                                )
                            }
                            if (isExpanded) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), CircleShape)
                                        .clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE)
                                                as ClipboardManager
                                            clipboard.setPrimaryClip(
                                                ClipData.newPlainText("bug", BugLogger.entryToText(entry))
                                            )
                                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                        },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        Icons.Filled.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = DarkTextMid,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }
                        AnimatedVisibility(visible = isExpanded) {
                            Column {
                                Spacer(Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color.White.copy(alpha = 0.08f)),
                                )
                                Spacer(Modifier.height(10.dp))
                                if (entry.stackTrace.isNotBlank()) {
                                    Surface(
                                        color = Color(0xFF100E08),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Text(
                                            entry.stackTrace.take(2000),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                            ),
                                            color = DarkTextMid,
                                            modifier = Modifier.padding(12.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Discovered Locations section ──
        item {
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.08f)),
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Discovered Locations",
                        style = MaterialTheme.typography.headlineSmall,
                        color = textOnDark,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "${discoveredSites.size} site${if (discoveredSites.size == 1) "" else "s"} found via web search" +
                            if (customCount.isNotEmpty()) " · ${customCount.size} accepted" else "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textOnDarkMuted,
                    )
                    if (selectedIds.isNotEmpty()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "${selectedIds.size} selected",
                            style = MaterialTheme.typography.labelMedium,
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                if (discoveredSites.isNotEmpty()) {
                    // Filled with a dark surface so the aqua text stays legible
                    // over the agate background.
                    OutlinedButton(
                        onClick = { confirmClearDiscovered = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .sculpted(shape = RoundedCornerShape(12.dp), accent = Aqua, shadowElevation = 5.dp)
                            .background(Slate800, RoundedCornerShape(12.dp)),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Aqua.copy(alpha = 0.8f), Aqua.copy(alpha = 0.5f)))
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Aqua),
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Clear", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        // Accept / Deny pill buttons — shown when sites exist
        if (discoveredSites.isNotEmpty()) {
            item {
                acceptMessage?.let { msg ->
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Success.copy(alpha = 0.12f))
                            .glowingBorder(2.dp, Success.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Text(
                            msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = Success,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // Select All / Deselect All toggle
                    val allSelected = discoveredSites.all { it.id in selectedIds }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White.copy(alpha = 0.06f))
                            .glowingBorder(2.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                            .clickable {
                                selectedIds = if (allSelected) emptySet() else discoveredSites.map { it.id }.toSet()
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            if (allSelected) "Deselect all" else "Select all",
                            style = MaterialTheme.typography.labelMedium,
                            color = DarkTextHigh,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    // Accept pill
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                if (selectedIds.isNotEmpty()) Success.copy(alpha = 0.18f)
                                else Color.White.copy(alpha = 0.04f)
                            )
                            .glowingBorder(
                                2.dp,
                                if (selectedIds.isNotEmpty()) Success.copy(alpha = 0.55f)
                                else Color.White.copy(alpha = 0.12f),
                                RoundedCornerShape(24.dp),
                            )
                            .clickable(enabled = selectedIds.isNotEmpty()) {
                                val toAccept = discoveredSites.filter { it.id in selectedIds }
                                val customLocs = toAccept.map { CustomDigLocationStore.fromDiscovered(it) }
                                CustomDigLocationStore.addAll(customLocs)
                                toAccept.forEach { DigSiteDiscoveryStore.remove(it.id) }
                                acceptMessage = "${toAccept.size} site${if (toAccept.size == 1) "" else "s"} added to Dig Sites tab permanently."
                                selectedIds = emptySet()
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (selectedIds.isNotEmpty()) Success else DarkTextMid,
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Accept (${selectedIds.size})",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selectedIds.isNotEmpty()) Success else DarkTextMid,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    // Deny pill
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                if (selectedIds.isNotEmpty()) Danger.copy(alpha = 0.15f)
                                else Color.White.copy(alpha = 0.04f)
                            )
                            .glowingBorder(
                                2.dp,
                                if (selectedIds.isNotEmpty()) Danger.copy(alpha = 0.5f)
                                else Color.White.copy(alpha = 0.12f),
                                RoundedCornerShape(24.dp),
                            )
                            .clickable(enabled = selectedIds.isNotEmpty()) {
                                val toDeny = discoveredSites.filter { it.id in selectedIds }
                                pendingDenySites = toDeny
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (selectedIds.isNotEmpty()) Danger else DarkTextMid,
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Deny (${selectedIds.size})",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selectedIds.isNotEmpty()) Danger else DarkTextMid,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }

        if (discoveredSites.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "No discovered locations yet. They appear here when a location search returns fewer than 3 built-in dig sites.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textOnDarkMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
        } else {
            items(discoveredSites, key = { it.id }) { site ->
                val siteTime = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(Date(site.discoveredAt))
                val isSelected = site.id in selectedIds
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF2A2820), Color(0xFF1E1C16))
                            )
                        )
                        .glowingBorder(
                            3.dp,
                            if (isSelected) Citrine.copy(alpha = 0.6f) else Aqua.copy(alpha = 0.4f),
                            RoundedCornerShape(14.dp),
                        )
                        .clickable {
                            selectedIds = if (isSelected) selectedIds - site.id else selectedIds + site.id
                        }
                        .padding(14.dp),
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        // Checkbox on the left
                        Icon(
                            if (isSelected) Icons.Filled.CheckBox else Icons.Outlined.CheckBoxOutlineBlank,
                            contentDescription = if (isSelected) "Selected" else "Not selected",
                            tint = if (isSelected) Citrine else DarkTextMid,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(22.dp)
                                .clickable {
                                    selectedIds = if (isSelected) selectedIds - site.id else selectedIds + site.id
                                },
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                site.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextHigh,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                "${site.type} · ${site.region} · found $siteTime",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextMid.copy(alpha = 0.7f),
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                site.url,
                                style = MaterialTheme.typography.labelSmall,
                                color = Aqua,
                                maxLines = 2,
                            )
                            if (site.description.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    site.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkTextMid,
                                    maxLines = 4,
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                                .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), CircleShape)
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE)
                                        as ClipboardManager
                                    clipboard.setPrimaryClip(
                                        ClipData.newPlainText(
                                            "site",
                                            "${site.name}\n${site.type}\n${site.region}\n${site.url}\n${site.description}",
                                        )
                                    )
                                    Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Filled.ContentCopy,
                                contentDescription = "Copy",
                                tint = DarkTextMid,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Clear bug log?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = { Text("Removes all ${entries.size} entries. This cannot be undone.", color = DarkTextMid) },
            confirmButton = {
                SculptedDialogButton(
                    text = "Clear",
                    onClick = {
                        BugLogger.clear(context)
                        confirmClear = false
                    },
                    accent = Danger,
                )
            },
            dismissButton = { SculptedTextButton(text = "Cancel", onClick = { confirmClear = false }, accent = Danger, textColor = DarkTextMid) },
            containerColor = Color(0xFF1E1C16),
        )
    }

    if (confirmClearDiscovered) {
        AlertDialog(
            onDismissRequest = { confirmClearDiscovered = false },
            title = { Text("Clear discovered locations?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = { Text("Removes all ${discoveredSites.size} discovered sites. This cannot be undone.", color = DarkTextMid) },
            confirmButton = {
                SculptedDialogButton(
                    text = "Clear",
                    onClick = {
                        DigSiteDiscoveryStore.clear()
                        selectedIds = emptySet()
                        confirmClearDiscovered = false
                    },
                    accent = Aqua,
                )
            },
            dismissButton = { SculptedTextButton(text = "Cancel", onClick = { confirmClearDiscovered = false }, accent = Aqua, textColor = DarkTextMid) },
            containerColor = Color(0xFF1E1C16),
        )
    }

    // ── Deny selected discovered sites confirmation ──
    pendingDenySites?.let { sites ->
        AlertDialog(
            onDismissRequest = { pendingDenySites = null },
            title = { Text("Dismiss ${sites.size} site${if (sites.size == 1) "" else "s"}?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This will permanently dismiss the selected discovered location${if (sites.size == 1) "" else "s"}. This cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                SculptedDialogButton(
                    text = "Dismiss",
                    onClick = {
                        sites.forEach { DigSiteDiscoveryStore.remove(it.id) }
                        acceptMessage = "${sites.size} site${if (sites.size == 1) "" else "s"} dismissed."
                        selectedIds = emptySet()
                        pendingDenySites = null
                    },
                    accent = Danger,
                )
            },
            dismissButton = { SculptedTextButton(text = "Cancel", onClick = { pendingDenySites = null }, accent = Danger, textColor = DarkTextMid) },
            containerColor = Color(0xFF1E1C16),
        )
    }

}

@Composable
private fun DevCard(
    accent: Color,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = RoundedCornerShape(18.dp), accent = accent, shadowElevation = 5.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .glowingBorder(3.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
            .padding(16.dp),
    ) {
        content()
    }
}

// ── Submissions Tab ─────────────────────────────────────────────────────────

@Composable
private fun SubmissionsTab() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val pinSubmissions by UserPinSubmissionStore.submissions.collectAsState()
    val specimenSubmissions by SpecimenSubmissionStore.submissions.collectAsState()
    val discoveredSites by DigSiteDiscoveryStore.sites.collectAsState()
    val locationSubmissions by LocationSubmissionStore.submissions.collectAsState()
    val showSubmissions by GemShowSubmissionStore.submissions.collectAsState()
    var selectedPinIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var actionMessage by remember { mutableStateOf<String?>(null) }
    var specimenViewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var specimenViewerPage by remember { mutableIntStateOf(0) }
    var pendingDenySite by remember { mutableStateOf<com.rork.rockscout.data.DigSiteDiscoveryStore.DiscoveredSite?>(null) }

    Box(modifier = Modifier.fillMaxSize().navigationBarsPadding().imePadding()) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { PageTitle("Submissions", "User pin submissions & web-discovered locations") }

        // ── User Pin Submissions ──
        item { SectionHeader("User Pin Submissions", Citrine, pinSubmissions.filter { it.status == "pending" }.size) }

        if (pinSubmissions.none { it.status == "pending" }) {
            item {
                Text(
                    "No pending pin submissions. Users can flag custom trip pins as rock locations for your review.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textOnDarkMuted,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        } else {
            items(pinSubmissions.filter { it.status == "pending" }, key = { it.id }) { sub ->
                val isSelected = sub.id in selectedPinIds
                val subTime = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(Date(sub.submittedAt))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF2A2820), Color(0xFF1E1C16))
                            )
                        )
                        .glowingBorder(
                            3.dp,
                            if (isSelected) Citrine.copy(alpha = 0.6f) else Aqua.copy(alpha = 0.4f),
                            RoundedCornerShape(14.dp),
                        )
                        .clickable {
                            selectedPinIds = if (isSelected) selectedPinIds - sub.id else selectedPinIds + sub.id
                        }
                        .padding(14.dp),
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            if (isSelected) Icons.Filled.CheckBox else Icons.Outlined.CheckBoxOutlineBlank,
                            contentDescription = if (isSelected) "Selected" else "Not selected",
                            tint = if (isSelected) Citrine else DarkTextMid,
                            modifier = Modifier.padding(top = 2.dp).size(22.dp).clickable {
                                selectedPinIds = if (isSelected) selectedPinIds - sub.id else selectedPinIds + sub.id
                            },
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                sub.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextHigh,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                "by ${sub.submitterName} · $subTime",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextMid.copy(alpha = 0.7f),
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "%.4f, %.4f".format(sub.latitude, sub.longitude),
                                style = MaterialTheme.typography.labelSmall,
                                color = Aqua,
                            )
                            if (sub.webSnippet.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    sub.webSnippet,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkTextMid,
                                    maxLines = 3,
                                )
                            }
                            if (sub.manuallyFlagged) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Manually flagged by user",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Citrine,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                        // Per-submission actions
                        Column {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Success.copy(alpha = 0.15f))
                                    .glowingBorder(1.5.dp, Success.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        scope.launch {
                                            // Convert to permanent DigLocation
                                            val loc = DigLocation(
                                                id = "custom-${sub.id}",
                                                name = sub.name,
                                                type = com.rork.rockscout.data.LocationType.PUBLIC_DIG,
                                                region = "",
                                                latitude = sub.latitude,
                                                longitude = sub.longitude,
                                                summary = if (sub.webSnippet.isNotBlank()) sub.webSnippet else "User-submitted location.",
                                                knownFor = emptyList(),
                                                mineralTags = emptyList(),
                                                feeInfo = "",
                                                hours = "",
                                                website = sub.webUrl.ifBlank { null },
                                                phone = null,
                                                difficulty = "Unknown",
                                                publicAccess = true,
                                                tips = "Submitted by ${sub.submitterName}.",
                                            )
                                            CustomDigLocationStore.addAll(listOf(loc))
                                            UserPinSubmissionStore.approve(sub.id)
                                            actionMessage = "${sub.name} approved and added to Dig Sites."
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Text("Approve", style = MaterialTheme.typography.labelSmall, color = Success, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Danger.copy(alpha = 0.12f))
                                    .glowingBorder(1.5.dp, Danger.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        UserPinSubmissionStore.deny(sub.id)
                                        actionMessage = "${sub.name} denied."
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Text("Deny", style = MaterialTheme.typography.labelSmall, color = Danger, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // ── Specimen Submissions ──
        item { SectionHeader("Specimen Submissions", Citrine, specimenSubmissions.filter { it.status == "pending" }.size) }

        if (specimenSubmissions.none { it.status == "pending" }) {
            item {
                Text(
                    "No specimen submissions yet. Users can submit specimens from the Specimen Database or Rocks Are Amazing pages.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textOnDarkMuted,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        } else {
            items(specimenSubmissions.filter { it.status == "pending" }, key = { it.id }) { sub ->
                SpecimenSubmissionReviewCard(
                    submission = sub,
                    onApproveDatabase = {
                        val specimen = CustomSpecimenStore.fromSubmission(sub, "database")
                        CustomSpecimenStore.add(specimen)
                        SpecimenSubmissionStore.approve(sub.id, "database")
                        actionMessage = "${specimen.name} added to database."
                    },
                    onApproveRaa = {
                        val specimen = CustomSpecimenStore.fromSubmission(sub, "raa")
                        CustomSpecimenStore.addToRaa(specimen)
                        SpecimenSubmissionStore.approve(sub.id, "raa")
                        actionMessage = "${specimen.name} added to RAA."
                    },
                    onDeny = {
                        SpecimenSubmissionStore.deny(sub.id)
                        actionMessage = "Submission denied."
                    },
                    onUpdateInfoText = { newText ->
                        SpecimenSubmissionStore.updateInfoText(sub.id, newText)
                    },
                    onPhotoClick = { urls, page ->
                        specimenViewerUrls = urls
                        specimenViewerPage = page
                    },
                )
            }
        }

        // ── Location Submissions ──
        item { SectionHeader("Location Submissions", Aqua, locationSubmissions.filter { it.status == "pending" }.size) }

        if (locationSubmissions.none { it.status == "pending" }) {
            item {
                Text(
                    "No pending location submissions. Users can upload full dig site locations via the Upload New Location form.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textOnDarkMuted,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        } else {
            items(locationSubmissions.filter { it.status == "pending" }, key = { it.id }) { sub ->
                val subTime = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(Date(sub.submittedAt))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF2A2820), Color(0xFF1E1C16))
                            )
                        )
                        .glowingBorder(2.dp, Aqua.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                sub.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextHigh,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                "by ${sub.submitterName} · $subTime",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextMid.copy(alpha = 0.7f),
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${sub.type} · %.4f, %.4f".format(sub.latitude, sub.longitude),
                                style = MaterialTheme.typography.labelSmall,
                                color = Aqua,
                            )
                            if (sub.address.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    sub.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkTextMid,
                                    maxLines = 2,
                                )
                            }
                            if (sub.comments.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    sub.comments,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkTextMid,
                                    maxLines = 3,
                                )
                            }
                            if (sub.webVerified) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "\u2713 Web-verified",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Success,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            // Photo thumbnails
                            if (sub.photoUris.isNotEmpty()) {
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    sub.photoUris.take(4).forEach { uri ->
                                        coil3.compose.AsyncImage(
                                            model = uri,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                        )
                                    }
                                }
                            }
                        }
                        // Per-submission actions
                        Column {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Success.copy(alpha = 0.15f))
                                    .glowingBorder(1.5.dp, Success.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        scope.launch {
                                            CustomDigLocationStore.addApprovedLocation(sub)
                                            LocationSubmissionStore.approve(sub.id)
                                            actionMessage = "${sub.name} approved and added to Dig Sites."
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Text("Approve", style = MaterialTheme.typography.labelSmall, color = Success, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Danger.copy(alpha = 0.12f))
                                    .glowingBorder(1.5.dp, Danger.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        LocationSubmissionStore.deny(sub.id)
                                        actionMessage = "${sub.name} denied."
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Text("Deny", style = MaterialTheme.typography.labelSmall, color = Danger, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // ── Show Submissions ──
        item { SectionHeader("Show Submissions", Citrine, showSubmissions.filter { it.status == "pending" }.size) }

        if (showSubmissions.none { it.status == "pending" }) {
            item {
                Text(
                    "No pending show submissions. Users can submit gem, mineral, and fossil shows from the Gem Shows screen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textOnDarkMuted,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        } else {
            items(showSubmissions.filter { it.status == "pending" }, key = { it.id }) { sub ->
                val subTime = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(Date(sub.submittedAt))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF2A2820), Color(0xFF1E1C16))
                            )
                        )
                        .glowingBorder(2.dp, Citrine.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                sub.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextHigh,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                "by ${sub.submitterName} · $subTime",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextMid.copy(alpha = 0.7f),
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${sub.city}, ${sub.state} · ${sub.month}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Citrine,
                            )
                            if (sub.dateRange.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    sub.dateRange,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkTextMid,
                                )
                            }
                            if (sub.description.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    sub.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkTextMid,
                                    maxLines = 3,
                                )
                            }
                            if (sub.webUrl.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    sub.webUrl,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Aqua,
                                    maxLines = 1,
                                )
                            }
                            if (sub.webVerified) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "\u2713 Web-verified",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Success,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        // Per-submission actions
                        Column {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Success.copy(alpha = 0.15f))
                                    .glowingBorder(1.5.dp, Success.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        CustomGemShowStore.addApprovedShow(sub)
                                        GemShowSubmissionStore.approve(sub.id)
                                        actionMessage = "${sub.name} approved and added to Gem Shows."
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Text("Approve", style = MaterialTheme.typography.labelSmall, color = Success, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Danger.copy(alpha = 0.12f))
                                    .glowingBorder(1.5.dp, Danger.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        GemShowSubmissionStore.deny(sub.id)
                                        actionMessage = "${sub.name} denied."
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Text("Deny", style = MaterialTheme.typography.labelSmall, color = Danger, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // ── Web-Discovered Locations (moved from Bugs tab) ──
        item { SectionHeader("Web-Discovered Locations", Aqua, discoveredSites.size) }

        if (discoveredSites.isEmpty()) {
            item {
                Text(
                    "No discovered locations yet. They appear here when a location search returns fewer than 3 built-in dig sites.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textOnDarkMuted,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        } else {
            items(discoveredSites, key = { it.id }) { site ->
                val siteTime = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(Date(site.discoveredAt))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF2A2820), Color(0xFF1E1C16))
                            )
                        )
                        .glowingBorder(3.dp, Aqua.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                ) {
                    Column {
                        Text(
                            site.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextHigh,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            "${site.type} · ${site.region} · found $siteTime",
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTextMid.copy(alpha = 0.7f),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(site.url, style = MaterialTheme.typography.labelSmall, color = Aqua, maxLines = 2)
                        if (site.description.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(site.description, style = MaterialTheme.typography.bodySmall, color = DarkTextMid, maxLines = 4)
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Success.copy(alpha = 0.15f))
                                    .glowingBorder(1.5.dp, Success.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        val loc = CustomDigLocationStore.fromDiscovered(site)
                                        CustomDigLocationStore.addAll(listOf(loc))
                                        DigSiteDiscoveryStore.remove(site.id)
                                        actionMessage = "${site.name} approved and added to Dig Sites."
                                    }
                                    .padding(horizontal = 12.dp, vertical = 7.dp),
                            ) { Text("Approve", style = MaterialTheme.typography.labelSmall, color = Success, fontWeight = FontWeight.Bold) }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Danger.copy(alpha = 0.12f))
                                    .glowingBorder(1.5.dp, Danger.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        pendingDenySite = site
                                    }
                                    .padding(horizontal = 12.dp, vertical = 7.dp),
                            ) { Text("Deny", style = MaterialTheme.typography.labelSmall, color = Danger, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }
        }

        if (actionMessage != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Success.copy(alpha = 0.12f))
                        .glowingBorder(2.dp, Success.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = Success, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(actionMessage ?: "", style = MaterialTheme.typography.bodyMedium, color = Success, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

    }

    if (specimenViewerUrls.isNotEmpty()) {
        FullScreenImageViewer(
            imageUrls = specimenViewerUrls,
            initialPage = specimenViewerPage,
            onDismiss = { specimenViewerUrls = emptyList() },
        )
    }

    // ── Deny individual discovered site confirmation ──
    pendingDenySite?.let { site ->
        AlertDialog(
            onDismissRequest = { pendingDenySite = null },
            title = { Text("Dismiss \"${site.name}\"?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This will permanently dismiss this discovered location. This cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                SculptedDialogButton(
                    text = "Dismiss",
                    onClick = {
                        DigSiteDiscoveryStore.remove(site.id)
                        actionMessage = "${site.name} dismissed."
                        pendingDenySite = null
                    },
                    accent = Danger,
                )
            },
            dismissButton = { SculptedTextButton(text = "Cancel", onClick = { pendingDenySite = null }, accent = Danger, textColor = DarkTextMid) },
            containerColor = Color(0xFF1E1C16),
        )
    }
    }
}

@Composable
private fun SpecimenSubmissionReviewCard(
    submission: SpecimenSubmissionStore.SpecimenSubmission,
    onApproveDatabase: () -> Unit,
    onApproveRaa: () -> Unit,
    onDeny: () -> Unit,
    onUpdateInfoText: (String) -> Unit,
    onPhotoClick: (List<String>, Int) -> Unit,
) {
    val subTime = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(Date(submission.submittedAt))
    var editableInfoText by remember(submission.id) { mutableStateOf(submission.infoText) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16))
                )
            )
            .glowingBorder(3.dp, Citrine.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(14.dp),
    ) {
        Column {
            // Submitter info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    submission.submitterAvatar,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        submission.submitterName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        "${submission.location.ifBlank { "Location not shown" }} · $subTime",
                        style = MaterialTheme.typography.labelSmall,
                        color = textOnDarkMuted,
                    )
                }
            }
            Spacer(Modifier.height(10.dp))

            // Image thumbnails
            if (submission.imageUris.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    submission.imageUris.forEachIndexed { idx, uri ->
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1A1812))
                                .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                .clickable { onPhotoClick(submission.imageUris, idx) },
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Submission photo ${idx + 1}",
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            // Editable info text
            Text(
                "Specimen info (editable):",
                style = MaterialTheme.typography.labelMedium,
                color = Citrine,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = editableInfoText,
                onValueChange = {
                    editableInfoText = it
                    onUpdateInfoText(it)
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )
            Spacer(Modifier.height(10.dp))

            // Action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Success.copy(alpha = 0.12f))
                        .glowingBorder(1.5.dp, Success.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .clickable { onApproveDatabase() }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Add to Database", style = MaterialTheme.typography.labelSmall, color = Success, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Aqua.copy(alpha = 0.12f))
                        .glowingBorder(1.5.dp, Aqua.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .clickable { onApproveRaa() }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Add to RAA", style = MaterialTheme.typography.labelSmall, color = Aqua, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Danger.copy(alpha = 0.12f))
                        .glowingBorder(1.5.dp, Danger.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .clickable { onDeny() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Deny", style = MaterialTheme.typography.labelSmall, color = Danger, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ── Moderation: Appeals Card ───────────────────────────────────────────────

@Composable
private fun AppealCard(
    appeal: LocalAppeal,
    onApprove: () -> Unit,
    onDeny: () -> Unit,
) {
    val isPending = appeal.status == "pending"
    val accent = when (appeal.status) {
        "approved" -> Success
        "denied" -> Danger
        else -> Citrine
    }
    val typeLabel = when (appeal.type) {
        "account_deletion" -> "Account Deletion"
        "image_rejected" -> "Image Rejection"
        "report_ban" -> "Report/Ban"
        else -> appeal.type
    }
    val time = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US).format(Date(appeal.created_at))
    var showImage by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = RoundedCornerShape(18.dp), accent = accent, shadowElevation = 5.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .glowingBorder(3.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
            .padding(16.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.15f))
                        .glowingBorder(2.dp, accent.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Gavel,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        typeLabel,
                        style = MaterialTheme.typography.titleSmall,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "User: ${appeal.user_id.take(20)}\u2026 · $time",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextMid.copy(alpha = 0.7f),
                    )
                }
                // Status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accent.copy(alpha = 0.15f))
                        .glowingBorder(2.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        appeal.status.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            // Appeal text
            Text(
                appeal.reason,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
            )
            // Attached image
            appeal.image_uri?.let { uri ->
                val file = File(uri)
                if (file.exists()) {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1A1812))
                            .glowingBorder(2.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .clickable { showImage = true },
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Evidence",
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            }
            // Approve/Deny buttons (only for pending appeals)
            if (isPending) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Success.copy(alpha = 0.15f))
                            .glowingBorder(2.dp, Success.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                            .clickable { onApprove() }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = Success, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Approve", style = MaterialTheme.typography.labelMedium, color = Success, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Danger.copy(alpha = 0.12f))
                            .glowingBorder(2.dp, Danger.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .clickable { onDeny() }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Close, contentDescription = null, tint = Danger, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Deny", style = MaterialTheme.typography.labelMedium, color = Danger, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showImage && appeal.image_uri != null) {
        FullScreenImageViewer(
            imageUrls = listOf(appeal.image_uri),
            initialPage = 0,
            onDismiss = { showImage = false },
        )
    }
}

// ── Moderation: Deleted Accounts Log (spreadsheet-style) ───────────────────

@Composable
private fun DeletedAccountsLogHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
            .background(Color(0xFF2A2820))
            .glowingBorder(1.dp, Danger.copy(alpha = 0.3f), RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Username", style = MaterialTheme.typography.labelMedium, color = Danger, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
        Text("Email", style = MaterialTheme.typography.labelMedium, color = Danger, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
        Text("Reason", style = MaterialTheme.typography.labelMedium, color = Danger, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
        Text("Date", style = MaterialTheme.typography.labelMedium, color = Danger, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text("Status", style = MaterialTheme.typography.labelMedium, color = Danger, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
    }
}

@Composable
private fun DeletedAccountLogRow(log: LocalDeletedAccountLog) {
    val isRestored = log.restored_at != null
    val rowBg = if (isRestored) Color(0xFF1A2A1A) else Color(0xFF1E1C16)
    val date = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(Date(log.deleted_at))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(0.dp))
            .background(rowBg)
            .glowingBorder(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(0.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            log.username,
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextHigh,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.2f),
        )
        Text(
            log.email,
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextMid,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.3f),
        )
        Text(
            log.reason,
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextMid,
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.5f),
        )
        Text(
            date,
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextMid.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f),
        )
        if (isRestored) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Restore, contentDescription = null, tint = Success, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    "Restored",
                    style = MaterialTheme.typography.labelSmall,
                    color = Success,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.8f),
                )
            }
        } else {
            Text(
                "Deleted",
                style = MaterialTheme.typography.labelSmall,
                color = Danger,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.8f),
            )
        }
    }
}

// ── Moderation: Report Log (spreadsheet-style, flat) ───────────────────────

@Composable
private fun ReportLogHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
            .background(Color(0xFF2A2820))
            .glowingBorder(1.dp, Warning.copy(alpha = 0.3f), RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Reported User", style = MaterialTheme.typography.labelMedium, color = Warning, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
        Text("Reporter", style = MaterialTheme.typography.labelMedium, color = Warning, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text("Reason", style = MaterialTheme.typography.labelMedium, color = Warning, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
        Text("Date", style = MaterialTheme.typography.labelMedium, color = Warning, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text("Screenshot", style = MaterialTheme.typography.labelMedium, color = Warning, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f))
    }
}

@Composable
private fun ReportLogRow(
    report: LocalUserReport,
    onScreenshotClick: (String) -> Unit,
) {
    val date = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(Date(report.created_at))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1C16))
            .glowingBorder(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(0.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            report.reported_name ?: "Unknown",
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextHigh,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.2f),
        )
        Text(
            report.reporter_name ?: "Unknown",
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextMid,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            report.reason ?: "(no reason)",
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextMid,
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.5f),
        )
        Text(
            date,
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextMid.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f),
        )
        // Screenshot icon
        Box(
            modifier = Modifier.weight(0.6f),
            contentAlignment = Alignment.Center,
        ) {
            report.screenshotPath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Warning.copy(alpha = 0.12f))
                            .glowingBorder(1.5.dp, Warning.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { onScreenshotClick(path) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Image,
                            contentDescription = "View screenshot",
                            tint = Warning,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}

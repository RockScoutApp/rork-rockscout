package com.rork.rockscout.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.R
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.ListingType
import com.rork.rockscout.data.TradeInterestRepository
import com.rork.rockscout.data.TradeListing
import com.rork.rockscout.data.CapturedPhoto
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.FullScreenImageViewer
import com.rork.rockscout.ui.components.LongPressableImage
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.StoneLine
import com.rork.rockscout.ui.theme.TextLow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class MyTradesTab(val label: String) {
    POSTED("Posted"),
    INTERESTED("Interested"),
    COMPLETED("Completed"),
    EXPIRED("Expired"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTradesScreen(navController: NavController) {
    val repo = AppRepository.instance
    val listings by repo.tradeListings.collectAsStateWithLifecycle()
    val interestRepo = TradeInterestRepository.instance
    val myInterests by interestRepo.myInterests.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var tab by remember { mutableStateOf(MyTradesTab.POSTED) }
    // Per-tab sort direction: true = newest first (default), false = oldest first
    val sortDirections = remember {
        mutableStateOf(mapOf(
            MyTradesTab.POSTED to true,
            MyTradesTab.INTERESTED to true,
            MyTradesTab.COMPLETED to true,
            MyTradesTab.EXPIRED to true,
        ))
    }
    val me = AuthRepository.instance.currentUserId
    val captures by repo.captures.collectAsStateWithLifecycle()
    val collection by repo.collection.collectAsStateWithLifecycle()
    val wishlist by repo.wishlist.collectAsStateWithLifecycle()
    var viewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerInitialPage by remember { mutableStateOf(0) }
    var editingListing by remember { mutableStateOf<TradeListing?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        repo.expireStaleListings()
        interestRepo.loadMyInterests()
    }

    ScreenScaffold(
        title = "My Trades",
        onBack = { navController.popBackStack() },
        background = { innerContent ->
            Box(modifier = Modifier.fillMaxSize()) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.trade_board_floor),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.42f),
                                    Color.Black.copy(alpha = 0.36f),
                                    Color.Black.copy(alpha = 0.40f),
                                    Color.Black.copy(alpha = 0.48f),
                                )
                            )
                        )
                )
                innerContent()
            }
        },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // "Enter the Trading Floor" shortcut pill
            SculptedButton(
                text = "Enter the Trading Floor",
                onClick = { navController.navigate(Routes.TRADING_FLOOR) },
                accent = Citrine,
                containerColor = Color(0xFF1E1C16).copy(alpha = 0.92f),
                textColor = Citrine,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            )
            Spacer(Modifier.height(8.dp))
            // Tab chips
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MyTradesTab.values().forEach { t ->
                    FilterChip(
                        selected = tab == t,
                        onClick = { tab = t },
                        label = {
                            Text(
                                t.label,
                                fontWeight = if (tab == t) FontWeight.Bold else FontWeight.Medium,
                            )
                        },
                        leadingIcon = if (tab == t) ({ Icon(Icons.Filled.Check, null, modifier = Modifier.size(16.dp)) }) else null,
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFF1E1C16).copy(alpha = 0.92f),
                            labelColor = TextLow,
                            iconColor = TextLow,
                            selectedContainerColor = Citrine,
                            selectedLabelColor = Ink,
                            selectedLeadingIconColor = Ink,
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (tab == t) Citrine else StoneLine.copy(alpha = 0.6f),
                        ),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            // Sort toggle pill
            val isNewestFirst = sortDirections.value[tab] ?: true
            SortTogglePill(
                isNewestFirst = isNewestFirst,
                onToggle = {
                    sortDirections.value = sortDirections.value.toMap() + (tab to !isNewestFirst)
                },
            )
            Spacer(Modifier.height(8.dp))

            when (tab) {
                MyTradesTab.POSTED -> {
                    val posted = remember(listings, me, isNewestFirst) {
                        val filtered = listings.filter { it.ownerUserId == me && it.status == "active" }
                        if (isNewestFirst) filtered.sortedByDescending { it.createdAt }
                        else filtered.sortedBy { it.createdAt }
                    }
                    if (posted.isEmpty()) {
                        MyTradesEmptyState(
                            emoji = "\uD83D\uDCBC",
                            title = "No posted trades",
                            message = "Listings you post to the Trade Board will appear here.",
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            itemsIndexed(posted) { _, listing ->
                                MyTradeCard(
                                    listing = listing,
                                    navController = navController,
                                    actionLabel = "Edit",
                                    onAction = {
                                        editingListing = listing
                                        showEditor = true
                                    },
                                    onPhotoClick = { urls, page ->
                                        viewerUrls = urls
                                        viewerInitialPage = page
                                    },
                                )
                            }
                        }
                    }
                }
                MyTradesTab.INTERESTED -> {
                    // Exclude listings already marked traded — those appear in Completed instead.
                    val interested = remember(listings, myInterests, isNewestFirst) {
                        val pairs = myInterests.mapNotNull { interest ->
                            listings.firstOrNull { it.id == interest.listing_id && it.status != "traded" }
                                ?.let { it to interest }
                        }
                        if (isNewestFirst) pairs.sortedByDescending { it.second.created_at }
                        else pairs.sortedBy { it.second.created_at }
                    }
                    if (interested.isEmpty()) {
                        MyTradesEmptyState(
                            emoji = "\uD83D\uDD0D",
                            title = "No interests yet",
                            message = "Tap \"Interested!\" on a Trade Board listing to track it here.",
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            itemsIndexed(interested) { _, pair ->
                                val (listing, interest) = pair
                                MyTradeCard(
                                    listing = listing,
                                    navController = navController,
                                    actionLabel = "Message",
                                    onAction = {
                                        listing.ownerUserId?.let { ownerId ->
                                            navController.navigate(Routes.messengerThread(ownerId))
                                        }
                                    },
                                    meta = "You expressed interest ${formatRelative(interest.created_at)}",
                                    onPhotoClick = { urls, page ->
                                        viewerUrls = urls
                                        viewerInitialPage = page
                                    },
                                )
                            }
                        }
                    }
                }
                MyTradesTab.COMPLETED -> {
                    // Show listings the user owns that are traded, PLUS listings the user
                    // expressed interest in that have been marked traded by the owner.
                    val interestedListingIds = remember(myInterests) {
                        myInterests.map { it.listing_id }.toSet()
                    }
                    val completed = remember(listings, me, interestedListingIds, isNewestFirst) {
                        val filtered = listings.filter {
                            it.status == "traded" && (
                                it.ownerUserId == me || it.id in interestedListingIds
                            )
                        }
                        if (isNewestFirst) filtered.sortedByDescending { it.createdAt }
                        else filtered.sortedBy { it.createdAt }
                    }
                    if (completed.isEmpty()) {
                        MyTradesEmptyState(
                            emoji = "\u2705",
                            title = "No completed trades",
                            message = "When you mark your listings as traded, or a listing you're interested in gets traded, it shows up here.",
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            itemsIndexed(completed) { _, listing ->
                                MyTradeCard(
                                    listing = listing,
                                    navController = navController,
                                    actionLabel = "View",
                                    onAction = {
                                        editingListing = listing
                                        showEditor = true
                                    },
                                    onPhotoClick = { urls, page ->
                                        viewerUrls = urls
                                        viewerInitialPage = page
                                    },
                                )
                            }
                        }
                    }
                }
                MyTradesTab.EXPIRED -> {
                    val expired = remember(listings, me, isNewestFirst) {
                        val filtered = listings.filter { it.ownerUserId == me && it.status == "expired" }
                        if (isNewestFirst) filtered.sortedByDescending { it.createdAt }
                        else filtered.sortedBy { it.createdAt }
                    }
                    if (expired.isEmpty()) {
                        MyTradesEmptyState(
                            emoji = "\u23F0",
                            title = "No expired listings",
                            message = "Listings past their 14-day limit will show up here. You can re-list them with a fresh 14-day clock.",
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            itemsIndexed(expired) { _, listing ->
                                MyTradeCard(
                                    listing = listing,
                                    navController = navController,
                                    actionLabel = "Edit",
                                    onAction = {
                                        editingListing = listing
                                        showEditor = true
                                    },
                                    meta = "Expired — edit and re-list to put it back on the Trading Floor",
                                    onPhotoClick = { urls, page ->
                                        viewerUrls = urls
                                        viewerInitialPage = page
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditor) {
        ListingEditorDialog(
            initial = editingListing,
            initialType = editingListing?.type ?: ListingType.HAVE,
            availableCaptures = captures,
            collectionEntries = collection,
            wishlistIds = wishlist,
            onDismiss = { showEditor = false; editingListing = null },
            onSave = { saved ->
                repo.saveTradeListing(saved)
                showEditor = false
                editingListing = null
            },
        )
    }

    if (viewerUrls.isNotEmpty()) {
        FullScreenImageViewer(
            imageUrls = viewerUrls,
            initialPage = viewerInitialPage,
            onDismiss = { viewerUrls = emptyList() },
        )
    }
}

@Composable
private fun MyTradeCard(
    listing: TradeListing,
    navController: NavController,
    actionLabel: String,
    onAction: () -> Unit,
    meta: String? = null,
    onPhotoClick: (List<String>, Int) -> Unit = { _, _ -> },
) {
    val accent = if (listing.type == ListingType.HAVE) Citrine else Aqua
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    DarkCard(
        accent = accent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAction),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1A1812))
                    .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (listing.photoUri != null) {
                    LongPressableImage(
                        model = listing.photoUri,
                        contentDescription = listing.specimenName,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop,
                        onClick = { onPhotoClick(listOf(listing.photoUri), 0) },
                    )
                } else {
                    Text(
                        if (listing.type == ListingType.HAVE) "\uD83D\uDC8E" else "\uD83D\uDD0D",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(if (listing.type == ListingType.HAVE) accent else Color.Transparent)
                            .glowingBorder(
                                if (listing.type == ListingType.WANT) 1.dp else 0.dp,
                                accent,
                                RoundedCornerShape(5.dp),
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            listing.type.label.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (listing.type == ListingType.HAVE) Color.Black else accent,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        dateFormat.format(Date(listing.createdAt)),
                        style = MaterialTheme.typography.labelSmall,
                        color = Aqua,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    listing.specimenName,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
                if (listing.status == "traded") {
                    Text(
                        "Status: Completed",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9DE89D),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                    )
                }
                if (!meta.isNullOrBlank()) {
                    Text(
                        meta,
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextMid,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            SculptedButton(
                text = actionLabel,
                onClick = onAction,
                accent = if (actionLabel == "Message") Aqua else Citrine,
                containerColor = if (actionLabel == "Message") Aqua else Citrine,
                textColor = Ink,
                icon = if (actionLabel == "Message") Icons.Filled.Message else null,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun MyTradesEmptyState(
    emoji: String,
    title: String,
    message: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 220.dp)
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(emoji, style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(12.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            color = DarkTextHigh,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
    }
}

@Composable
private fun SortTogglePill(
    isNewestFirst: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        SculptedButton(
            text = if (isNewestFirst) "Newest first" else "Oldest first",
            onClick = onToggle,
            accent = Aqua,
            containerColor = Color(0xFF1E1C16).copy(alpha = 0.92f),
            textColor = Aqua,
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
        )
    }
}

private fun formatRelative(createdAt: String): String {
    return runCatching {
        val instant = java.time.OffsetDateTime.parse(createdAt).toInstant()
        val then = instant.toEpochMilli()
        val now = System.currentTimeMillis()
        val diff = now - then
        when {
            diff < 60_000 -> "just now"
            diff < 60 * 60_000 -> "${diff / 60_000}m ago"
            diff < 24 * 60 * 60_000 -> "${diff / (60 * 60_000)}h ago"
            diff < 7 * 24 * 60 * 60_000 -> "${diff / (24 * 60 * 60_000)}d ago"
            else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(then))
        }
    }.getOrDefault("recently")
}

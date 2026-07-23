package com.rork.rockscout.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.R
import com.rork.rockscout.data.AchievementsRepository
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.FriendRepository
import com.rork.rockscout.data.ListingMode
import com.rork.rockscout.data.ListingType
import com.rork.rockscout.data.NotificationHelper
import com.rork.rockscout.data.ProfanityFilter
import com.rork.rockscout.data.SessionStatus
import com.rork.rockscout.data.TradeInterestRepository
import com.rork.rockscout.data.TradeListing
import com.rork.rockscout.data.XpSource
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.FullScreenImageViewer
import com.rork.rockscout.ui.components.LongPressableImage
import com.rork.rockscout.ui.components.ShareCardImage
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.components.DeleteConfirmDialog
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.StoneLine
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.ui.platform.LocalView
import com.rork.rockscout.data.ReportRepository
import com.rork.rockscout.data.ReportScreenshotHelper
import com.rork.rockscout.ui.components.ReportSubmittedDialog
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Danger

private enum class FloorFilter(val label: String) {
    ALL("All"), HAVE("Have"), WANT("Want")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradingFloorScreen(navController: NavController) {
    val repo = AppRepository.instance
    val listings by repo.tradeListings.collectAsStateWithLifecycle()
    val captures by repo.captures.collectAsStateWithLifecycle()
    val collection by repo.collection.collectAsStateWithLifecycle()
    val wishlist by repo.wishlist.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var editingListing by remember { mutableStateOf<TradeListing?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var initialEditorType by remember { mutableStateOf(ListingType.HAVE) }
    var filter by remember { mutableStateOf(FloorFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    val auth = AuthRepository.instance
    val friendRepo = FriendRepository.instance
    val sessionStatus by auth.sessionStatus.collectAsStateWithLifecycle()
    val isSignedIn = sessionStatus is SessionStatus.Authenticated
    val friends by friendRepo.friends.collectAsStateWithLifecycle()
    val friendIds = remember(friends) { friends.map { it.id }.toSet() }
    val interestRepo = TradeInterestRepository.instance
    val interestedListingIds by interestRepo.interestedListingIds.collectAsStateWithLifecycle()
    var shareToProfileListing by remember { mutableStateOf<TradeListing?>(null) }
    var pendingDeleteListing by remember { mutableStateOf<TradeListing?>(null) }
    var selectedListing by remember { mutableStateOf<TradeListing?>(null) }
    var viewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerInitialPage by remember { mutableStateOf(0) }
    var reportTargetListing by remember { mutableStateOf<TradeListing?>(null) }
    var showReportConfirm by remember { mutableStateOf(false) }
    var reportSubmitted by remember { mutableStateOf(false) }
    val myProfile by repo.profile.collectAsStateWithLifecycle()
    val rootView = LocalView.current
    var safetyCardVisible by remember { mutableStateOf(true) }
    var warningCardVisible by remember { mutableStateOf(true) }

    LaunchedEffect(isSignedIn) {
        if (isSignedIn) {
            interestRepo.loadMyInterests()
        }
    }

    LaunchedEffect(Unit) {
        repo.expireStaleListings()
    }

    val filtered = remember(listings, filter, searchQuery) {
        val byFilter = when (filter) {
            FloorFilter.ALL -> listings.filter { it.status == "active" }
            FloorFilter.HAVE -> listings.filter { it.type == ListingType.HAVE && it.status == "active" }
            FloorFilter.WANT -> listings.filter { it.type == ListingType.WANT && it.status == "active" }
        }
        if (searchQuery.isBlank()) {
            byFilter
        } else {
            val q = searchQuery.trim().lowercase()
            byFilter.filter { listing ->
                listing.specimenName.lowercase().contains(q) ||
                    listing.description.lowercase().contains(q) ||
                    listing.tags.any { it.lowercase().contains(q) } ||
                    listing.condition.lowercase().contains(q) ||
                    listing.price.lowercase().contains(q) ||
                    listing.wantInReturn.lowercase().contains(q) ||
                    (listing.ownerUsername?.lowercase()?.contains(q) ?: false)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.trade_board_floor),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.42f),
                            Color.Black.copy(alpha = 0.36f),
                            Color.Black.copy(alpha = 0.40f),
                            Color.Black.copy(alpha = 0.48f),
                        )
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            // Top row: title + "Leave the Trading Floor" pill
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Trading Floor",
                    style = MaterialTheme.typography.headlineSmall,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                )
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E1C16).copy(alpha = 0.92f),
                        contentColor = Aqua,
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    border = BorderStroke(1.dp, Aqua),
                ) {
                    Icon(Icons.Filled.ExitToApp, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Leave the Trading Floor", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }
            }

            // Search bar — styled identically to the home page MiniSearchBar
            // but with a functioning text input that filters listings in real time.
            FloorSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            )

            // Filter pills + Post a listing — single row with even spacing so all 4
            // buttons (All, Have, Want, Post) share the same width and align cleanly.
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FloorFilter.values().forEach { f ->
                    FilterChip(
                        selected = filter == f,
                        onClick = { filter = f },
                        label = {
                            Text(
                                f.label,
                                fontWeight = if (filter == f) FontWeight.Bold else FontWeight.Medium,
                            )
                        },
                        leadingIcon = if (filter == f) ({ Icon(Icons.Filled.Check, null, modifier = Modifier.size(16.dp)) }) else null,
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
                            if (filter == f) Citrine else StoneLine.copy(alpha = 0.6f),
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }
                SculptedButton(
                    onClick = { editingListing = null; initialEditorType = ListingType.HAVE; showEditor = true },
                    accent = Citrine,
                    containerColor = Citrine,
                    textColor = Ink,
                    icon = Icons.Filled.Add,
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f),
                )
            }

            // Safety note — closeable card that resets each time the user visits.
            if (safetyCardVisible) {
                DarkCard(
                    accent = Citrine,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = Aqua,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Trade safely: meet in a public, well-lit place, bring a friend when possible, and never ship first or send money before inspecting the specimen. Insist on a neutral location like a rock shop or library parking lot, and take clear photos of the trade as a record. Trust your gut — if a deal feels off, walk away. Scammers are out there; no legitimate trader will pressure you to pay before you can see what you're getting. All listings expire after 14 days to keep the board fresh.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Aqua,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            lineHeight = MaterialTheme.typography.labelSmall.lineHeight,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(
                            onClick = { safetyCardVisible = false },
                            modifier = Modifier.size(28.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Close",
                                tint = TextLow,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }

            // Anti-theft warning — closeable card with red danger accent.
            if (warningCardVisible) {
                DarkCard(
                    accent = Danger,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.GppBad,
                            contentDescription = null,
                            tint = Danger,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Theft or scamming is not tolerated AT ALL. If confirmed, your account will be immediately deleted. Appeals can be made, but don't get your hopes up. I review these personally.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Danger,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            lineHeight = MaterialTheme.typography.labelSmall.lineHeight,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(
                            onClick = { warningCardVisible = false },
                            modifier = Modifier.size(28.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Close",
                                tint = TextLow,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }

            // Full-width listings in a vertical scroll
            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("\uD83D\uDD04", style = MaterialTheme.typography.displayLarge)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (searchQuery.isNotBlank()) "No matching listings" else "No listings yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = Aqua,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            if (searchQuery.isNotBlank()) "Try a different search term."
                            else "Post a specimen to trade, or check back later.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextMid,
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    items(filtered, key = { it.id }) { listing ->
                        FloorListingCard(
                            listing = listing,
                            navController = navController,
                            isFriend = listing.ownerUserId != null && friendIds.contains(listing.ownerUserId),
                            alreadyInterested = interestedListingIds.contains(listing.id),
                            onPhotoClick = { urls, page ->
                                viewerUrls = urls
                                viewerInitialPage = page
                            },
                            onClick = { selectedListing = listing },
                            onEdit = { editingListing = listing; initialEditorType = listing.type; showEditor = true },
                            onDelete = { pendingDeleteListing = listing },
                            onMarkTraded = {
                                repo.markTradeListingTraded(listing.id)
                                AchievementsRepository.award(XpSource.TRADE)
                            },
                            onShare = {
                                scope.launch {
                                    val photo: android.graphics.Bitmap? = listing.photoUri?.let { uriStr ->
                                        ShareCardImage.loadDownsampled(context, android.net.Uri.parse(uriStr))
                                    }
                                    val sub = (if (listing.type == ListingType.HAVE) "HAVE" else "WANT") +
                                        "  \u2022  " + SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(listing.createdAt))
                                    ShareCardImage.share(
                                        context = context,
                                        title = listing.specimenName,
                                        subtitle = sub,
                                        body = listing.description.ifBlank { null },
                                        accentHex = if (listing.type == ListingType.HAVE) 0xFFE8A33D else 0xFF6FA8C7,
                                        photoBitmap = photo,
                                        caption = "Posted from RockScout Trade Board",
                                        fileName = "rockscout_trade_${listing.id}",
                                    )
                                }
                            },
                            onShareToProfile = { shareToProfileListing = listing },
                            onInterested = {
                                scope.launch {
                                    val ownerId = listing.ownerUserId ?: return@launch
                                    if (isSignedIn) {
                                        val defaultMsg = "Hi! I'm interested in your ${listing.specimenName}. Is it still available?"
                                        val filteredMsg = ProfanityFilter.filter(defaultMsg)
                                        interestRepo.expressInterest(listing, filteredMsg)
                                        FriendRepository.instance.sendFriendRequest(ownerId)
                                        android.widget.Toast.makeText(
                                            context,
                                            "Interest sent! Opening conversation…",
                                            android.widget.Toast.LENGTH_SHORT,
                                        ).show()
                                    }
                                    navController.navigate(Routes.messengerThread(ownerId))
                                }
                            },
                            onReport = { reportTargetListing = listing; showReportConfirm = true },
                        )
                    }
                }
            }
        }
    }

    if (showEditor) {
        ListingEditorDialog(
            initial = editingListing,
            initialType = initialEditorType,
            availableCaptures = captures,
            collectionEntries = collection,
            wishlistIds = wishlist,
            onDismiss = { showEditor = false },
            onSave = { saved -> repo.saveTradeListing(saved); showEditor = false },
        )
    }

    selectedListing?.let { listing ->
        ListingDetailDialog(
            listing = listing,
            navController = navController,
            isFriend = listing.ownerUserId != null && friendIds.contains(listing.ownerUserId),
            alreadyInterested = interestedListingIds.contains(listing.id),
            onEdit = { editingListing = listing; initialEditorType = listing.type; showEditor = true; selectedListing = null },
            onDelete = { pendingDeleteListing = listing; selectedListing = null },
            onMarkTraded = {
                repo.markTradeListingTraded(listing.id)
                AchievementsRepository.award(XpSource.TRADE)
                selectedListing = null
            },
            onInterested = {
                selectedListing = null
                scope.launch {
                    val ownerId = listing.ownerUserId ?: return@launch
                    if (isSignedIn) {
                        val defaultMsg = "Hi! I'm interested in your ${listing.specimenName}. Is it still available?"
                        val filteredMsg = ProfanityFilter.filter(defaultMsg)
                        interestRepo.expressInterest(listing, filteredMsg)
                        FriendRepository.instance.sendFriendRequest(ownerId)
                        android.widget.Toast.makeText(
                            context,
                            "Interest sent! Opening conversation…",
                            android.widget.Toast.LENGTH_SHORT,
                        ).show()
                    }
                    navController.navigate(Routes.messengerThread(ownerId))
                }
            },
            onShare = {
                scope.launch {
                    val photo: android.graphics.Bitmap? = listing.photoUri?.let { uriStr ->
                        ShareCardImage.loadDownsampled(context, android.net.Uri.parse(uriStr))
                    }
                    val sub = (if (listing.type == ListingType.HAVE) "HAVE" else "WANT") +
                        "  \u2022  " + SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(listing.createdAt))
                    ShareCardImage.share(
                        context = context,
                        title = listing.specimenName,
                        subtitle = sub,
                        body = listing.description.ifBlank { null },
                        accentHex = if (listing.type == ListingType.HAVE) 0xFFE8A33D else 0xFF6FA8C7,
                        photoBitmap = photo,
                        caption = "Posted from RockScout Trade Board",
                        fileName = "rockscout_trade_${listing.id}",
                    )
                }
            },
            onShareToProfile = { shareToProfileListing = listing; selectedListing = null },
            onReport = { reportTargetListing = listing; showReportConfirm = true; selectedListing = null },
            onDismiss = { selectedListing = null },
            onPhotoClick = { urls, page ->
                viewerUrls = urls
                viewerInitialPage = page
            },
        )
    }

    shareToProfileListing?.let { listing ->
        ShareToProfileComposer(
            sourceType = "trade",
            title = "${listing.type.label}: ${listing.specimenName}",
            tagline = "${listing.listingMode.label}  \u2022  " +
                SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(listing.createdAt)),
            imageUri = listing.photoUri,
            locationText = "",
            onDismiss = { shareToProfileListing = null },
        )
    }

    if (viewerUrls.isNotEmpty()) {
        FullScreenImageViewer(
            imageUrls = viewerUrls,
            initialPage = viewerInitialPage,
            onDismiss = { viewerUrls = emptyList() },
        )
    }

    pendingDeleteListing?.let { listing ->
        DeleteConfirmDialog(
            title = "Delete listing?",
            message = "Delete \"${listing.specimenName}\"? This action cannot be undone.",
            onConfirm = {
                repo.deleteTradeListing(listing.id)
                pendingDeleteListing = null
            },
            onDismiss = { pendingDeleteListing = null },
        )
    }

    // Report confirmation — prevents accidental taps from triggering screenshot capture.
    if (showReportConfirm && reportTargetListing != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showReportConfirm = false; reportTargetListing = null },
            title = { Text("Report this listing?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "A screenshot will be captured and you'll be asked to send a report email about this trade listing. Only continue if you believe it violates our community guidelines.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                SculptedButton(
                    text = "Continue",
                    onClick = { showReportConfirm = false },
                    accent = Danger,
                    containerColor = Danger,
                    textColor = Color.White,
                )
            },
            dismissButton = {
                SculptedTextButton(text = "Cancel", onClick = { showReportConfirm = false; reportTargetListing = null }, accent = DarkTextMid, textColor = DarkTextMid)
            },
            containerColor = Color(0xFF1E1C16),
            titleContentColor = DarkTextHigh,
            textContentColor = DarkTextMid,
        )
    }

    // Report dialog — captures screenshot, records the report, and launches email composer.
    reportTargetListing?.let { listing ->
        if (!showReportConfirm) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { reportTargetListing = null },
                title = { Text("Report ${listing.ownerUsername ?: "this listing"}?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Help keep RockScout safe and family-friendly. Report this trade listing for inappropriate content, spam, or behavior that violates our community guidelines.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                    )
                },
                confirmButton = {
                    SculptedButton(
                        text = "Report",
                        onClick = {
                            scope.launch {
                                val ownerId = listing.ownerUserId ?: return@launch
                                val reportId = "report-${System.currentTimeMillis()}"
                                val screenshotPath = ReportScreenshotHelper.captureScreenshot(
                                    context, rootView, reportId,
                                )
                                ReportRepository.instance.reportUser(
                                    reportedUserId = ownerId,
                                    reason = "Inappropriate trade listing: ${listing.specimenName}",
                                    screenshotPath = screenshotPath,
                                    reporterName = myProfile.name,
                                    reportedName = listing.ownerUsername,
                                    reportedAvatar = null,
                                ).onSuccess {
                                    reportSubmitted = true
                                    val intent = ReportScreenshotHelper.buildEmailIntent(
                                        context = context,
                                        reportedUserName = listing.ownerUsername ?: "Unknown user",
                                        reporterUserName = myProfile.name,
                                        reason = "Inappropriate trade listing: ${listing.specimenName}",
                                        timestamp = System.currentTimeMillis(),
                                        screenshotPath = screenshotPath,
                                    )
                                    ReportScreenshotHelper.launchEmailComposer(context, intent)
                                }
                            }
                            reportTargetListing = null
                        },
                        accent = Danger,
                        containerColor = Danger,
                        textColor = Color.White,
                    )
                },
                dismissButton = {
                    SculptedTextButton(text = "Cancel", onClick = { reportTargetListing = null }, accent = DarkTextMid, textColor = DarkTextMid)
                },
                containerColor = Color(0xFF1E1C16),
                titleContentColor = DarkTextHigh,
                textContentColor = DarkTextMid,
            )
        }
    }

    if (reportSubmitted) {
        ReportSubmittedDialog(onDismiss = { reportSubmitted = false })
    }
}

/**
 * Functioning search bar styled identically to the home page's MiniSearchBar
 * (same pill shape, Citrine border, shadow, dark gradient background) but with
 * a real text input that filters listings in real time.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FloorSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(23.dp),
                ambientColor = Citrine.copy(alpha = 0.25f),
                spotColor = Citrine,
            )
            .clip(RoundedCornerShape(23.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Obsidian.copy(alpha = 0.85f),
                        Slate900.copy(alpha = 0.9f),
                    )
                )
            )
            .noAutoFocus(),
        placeholder = {
            Text(
                "Search listings\u2026",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextMid.copy(alpha = 0.6f),
                ),
            )
        },
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                contentDescription = "Search",
                tint = Aqua,
                modifier = Modifier.size(22.dp),
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
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = DarkTextHigh,
            unfocusedTextColor = DarkTextHigh,
            cursorColor = Citrine,
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.SemiBold,
        ),
        shape = RoundedCornerShape(23.dp),
    )
}

/**
 * Uniform trading-floor listing tile. Every card has the same fixed structure:
 * photo on the left, then exactly three horizontal rows — badges + icon actions,
 * specimen name, and meta + owner + single CTA — so all tiles are the same height.
 * Tags, Message, and Mark Traded are available in the detail dialog.
 */
@Composable
private fun FloorListingCard(
    listing: TradeListing,
    navController: NavController,
    isFriend: Boolean,
    alreadyInterested: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMarkTraded: () -> Unit,
    onShare: () -> Unit,
    onShareToProfile: () -> Unit,
    onInterested: () -> Unit,
    onReport: () -> Unit = {},
    onClick: () -> Unit,
    onPhotoClick: (List<String>, Int) -> Unit = { _, _ -> },
) {
    val accent = if (listing.type == ListingType.HAVE) Citrine else Aqua
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val ownerId = listing.ownerUserId
    val ownerName = listing.ownerUsername
    val isMine = ownerId == AuthRepository.instance.currentUserId

    // Single-line meta: condition, price, want, or description snippet
    val metaText = when {
        listing.condition.isNotBlank() && listing.type == ListingType.HAVE -> "Condition: ${listing.condition}"
        listing.listingMode == ListingMode.SELLING && listing.price.isNotBlank() -> "Asking: ${listing.price}"
        listing.listingMode == ListingMode.BUYING && listing.price.isNotBlank() -> "Budget: ${listing.price}"
        listing.type == ListingType.HAVE && listing.listingMode == ListingMode.SWAP && listing.wantInReturn.isNotBlank() -> "Want: ${listing.wantInReturn}"
        listing.description.isNotBlank() -> listing.description
        else -> ""
    }
    val metaColor = when {
        listing.condition.isNotBlank() && listing.type == ListingType.HAVE -> Aqua
        listing.listingMode == ListingMode.SELLING && listing.price.isNotBlank() -> Color(0xFF9DE89D)
        listing.listingMode == ListingMode.BUYING && listing.price.isNotBlank() -> Aqua
        listing.type == ListingType.HAVE && listing.listingMode == ListingMode.SWAP && listing.wantInReturn.isNotBlank() -> accent
        else -> DarkTextHigh
    }

    DarkCard(
        accent = accent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentPadding = PaddingValues(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Photo — fixed 56dp, same as specimen cards
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1A1812))
                    .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (listing.photoUri != null) {
                    LongPressableImage(
                        model = listing.photoUri,
                        contentDescription = listing.specimenName,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop,
                        onClick = { onPhotoClick(listOf(listing.photoUri), 0) },
                    )
                } else {
                    Text(
                        if (listing.type == ListingType.HAVE) "\uD83D\uDC8E" else "\uD83D\uDD0D",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
                // Friend dot — overlaid on photo top-right corner
                if (isFriend) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(Success)
                            .glowingBorder(2.dp, Color(0xFF1A1812), CircleShape),
                    )
                }
            }
            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Row 1: badges + date (left) … icon actions (right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        // Type badge
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
                        // Mode badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    when (listing.listingMode) {
                                        ListingMode.SELLING -> Color(0xFF7FD07F).copy(alpha = 0.22f)
                                        ListingMode.BUYING -> Color(0xFF6FA8C7).copy(alpha = 0.22f)
                                        else -> Color(0xFFE8A33D).copy(alpha = 0.18f)
                                    }
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Text(
                                listing.listingMode.label.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = when (listing.listingMode) {
                                    ListingMode.SELLING -> Color(0xFF9DE89D)
                                    ListingMode.BUYING -> Aqua
                                    else -> Citrine
                                },
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                            )
                        }
                        Text(
                            dateFormat.format(Date(listing.createdAt)),
                            style = MaterialTheme.typography.labelSmall,
                            color = Aqua,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        if (isMine) {
                            SculptedIconButton(icon = Icons.Filled.Edit, contentDescription = "Edit", onClick = onEdit, accent = Aqua, iconTint = Aqua, size = 24.dp, shadowElevation = 3.dp)
                            SculptedIconButton(icon = Icons.Filled.Share, contentDescription = "Share", onClick = onShare, accent = Aqua, iconTint = Aqua, size = 24.dp, shadowElevation = 3.dp)
                            SculptedIconButton(icon = Icons.Filled.PersonAdd, contentDescription = "Share to Profile", onClick = onShareToProfile, accent = Citrine, iconTint = Citrine, size = 24.dp, shadowElevation = 3.dp)
                            SculptedIconButton(icon = Icons.Filled.Delete, contentDescription = "Delete", onClick = onDelete, accent = Aqua, iconTint = Aqua, size = 24.dp, shadowElevation = 3.dp)
                        }
                        if (!isMine && ownerId != null) {
                            SculptedIconButton(icon = Icons.Filled.Flag, contentDescription = "Report", onClick = onReport, accent = Danger, iconTint = Danger, size = 24.dp, shadowElevation = 3.dp)
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                // Row 2: specimen name — single line, ellipsis
                Text(
                    listing.specimenName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                // Row 3: meta + owner (left, weight 1) … single CTA (right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        if (metaText.isNotBlank()) {
                            Text(
                                metaText,
                                style = MaterialTheme.typography.labelMedium,
                                color = metaColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false),
                            )
                        }
                        if (!ownerName.isNullOrBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f, fill = false),
                            ) {
                                Icon(
                                    Icons.Filled.AccountCircle,
                                    contentDescription = "View profile",
                                    tint = Aqua,
                                    modifier = Modifier.size(14.dp),
                                )
                                Spacer(Modifier.width(3.dp))
                                Text(
                                    ownerName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Aqua,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.clickable(enabled = ownerId != null) {
                                        ownerId?.let { navController.navigate(Routes.userProfile(it)) }
                                    },
                                )
                            }
                        }
                    }
                    // Single CTA — same slot for every card, uniform height
                    if (listing.status == "active") {
                        if (listing.type == ListingType.HAVE) {
                            SculptedButton(
                                text = if (alreadyInterested) "Interested" else "Interested!",
                                onClick = onInterested,
                                enabled = !alreadyInterested,
                                accent = Citrine,
                                containerColor = if (alreadyInterested) Color(0xFF3A3830) else Citrine,
                                textColor = if (alreadyInterested) DarkTextMid else Ink,
                                icon = Icons.Filled.Send,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            )
                        } else {
                            SculptedButton(
                                text = if (alreadyInterested) "Interested" else "I have this!",
                                onClick = onInterested,
                                enabled = !alreadyInterested,
                                accent = Aqua,
                                containerColor = if (alreadyInterested) Color(0xFF3A3830) else Aqua,
                                textColor = if (alreadyInterested) DarkTextMid else Ink,
                                icon = Icons.Filled.Send,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Full-screen dialog that shows the complete listing details and every action
 *  available for that listing. Opened by tapping a compact Trading Floor tile. */
@Composable
private fun ListingDetailDialog(
    listing: TradeListing,
    navController: NavController,
    isFriend: Boolean,
    alreadyInterested: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMarkTraded: () -> Unit,
    onInterested: () -> Unit,
    onShare: () -> Unit,
    onShareToProfile: () -> Unit,
    onReport: () -> Unit = {},
    onDismiss: () -> Unit,
    onPhotoClick: (List<String>, Int) -> Unit = { _, _ -> },
) {
    val accent = if (listing.type == ListingType.HAVE) Citrine else Aqua
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val ownerId = listing.ownerUserId
    val ownerName = listing.ownerUsername
    val isMine = ownerId == AuthRepository.instance.currentUserId

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
    ) {
        DarkCard(
            accent = accent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            contentPadding = PaddingValues(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (listing.type == ListingType.HAVE) accent else Color.Transparent)
                                .glowingBorder(
                                    if (listing.type == ListingType.WANT) 1.5.dp else 0.dp,
                                    accent,
                                    RoundedCornerShape(8.dp),
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                        ) {
                            Text(
                                listing.type.label.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (listing.type == ListingType.HAVE) Color.Black else accent,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when (listing.listingMode) {
                                        ListingMode.SELLING -> Color(0xFF7FD07F).copy(alpha = 0.22f)
                                        ListingMode.BUYING -> Color(0xFF6FA8C7).copy(alpha = 0.22f)
                                        else -> Color(0xFFE8A33D).copy(alpha = 0.18f)
                                    }
                                )
                                .glowingBorder(1.dp, Color(0xFF7FD07F).copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                        ) {
                            Text(
                                listing.listingMode.label.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = when (listing.listingMode) {
                                    ListingMode.SELLING -> Color(0xFF9DE89D)
                                    ListingMode.BUYING -> Aqua
                                    else -> Citrine
                                },
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    Text(
                        dateFormat.format(Date(listing.createdAt)),
                        style = MaterialTheme.typography.labelMedium,
                        color = Aqua,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1A1812))
                        .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (listing.photoUri != null) {
                        LongPressableImage(
                            model = listing.photoUri,
                            contentDescription = listing.specimenName,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onPhotoClick(listOf(listing.photoUri), 0) },
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Text(
                            if (listing.type == ListingType.HAVE) "\uD83D\uDC8E" else "\uD83D\uDD0D",
                            style = MaterialTheme.typography.displayMedium,
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    listing.specimenName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                if (listing.condition.isNotBlank() && listing.type == ListingType.HAVE) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Condition: ${listing.condition}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Aqua,
                    )
                }
                if (listing.listingMode == ListingMode.SELLING && listing.price.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Asking: ${listing.price}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF9DE89D),
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (listing.listingMode == ListingMode.BUYING && listing.price.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Budget: ${listing.price}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Aqua,
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (listing.type == ListingType.HAVE && listing.listingMode == ListingMode.SWAP && listing.wantInReturn.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Looking for: ${listing.wantInReturn}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = accent,
                        fontWeight = FontWeight.Medium,
                    )
                }
                if (listing.description.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        listing.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextHigh,
                        textAlign = TextAlign.Center,
                    )
                }
                if (listing.tags.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        listing.tags.forEach { tag ->
                            CompactListingTag(tag, accent)
                        }
                    }
                }
                if (isFriend) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Success.copy(alpha = 0.16f))
                            .glowingBorder(1.dp, Success.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Success)
                                .glowingBorder(1.dp, Success.copy(alpha = 0.35f), CircleShape),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "RockScout Friend",
                            style = MaterialTheme.typography.labelMedium,
                            color = Success,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                if (!ownerName.isNullOrBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Aqua.copy(alpha = 0.12f))
                            .glowingBorder(1.5.dp, Aqua.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .clickable(enabled = ownerId != null) {
                                ownerId?.let { navController.navigate(Routes.userProfile(it)) }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Icon(
                            Icons.Filled.AccountCircle,
                            contentDescription = "View profile",
                            tint = Aqua,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            ownerName,
                            style = MaterialTheme.typography.labelLarge,
                            color = Aqua,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                ) {
                    if (isMine) {
                        SculptedIconButton(icon = Icons.Filled.Edit, contentDescription = "Edit", onClick = onEdit, accent = Aqua, iconTint = Aqua, size = 40.dp, shadowElevation = 3.dp)
                        SculptedIconButton(icon = Icons.Filled.Share, contentDescription = "Share", onClick = onShare, accent = Aqua, iconTint = Aqua, size = 40.dp, shadowElevation = 3.dp)
                        SculptedIconButton(icon = Icons.Filled.PersonAdd, contentDescription = "Share to Profile", onClick = onShareToProfile, accent = Citrine, iconTint = Citrine, size = 40.dp, shadowElevation = 3.dp)
                        SculptedIconButton(icon = Icons.Filled.Delete, contentDescription = "Delete", onClick = onDelete, accent = Aqua, iconTint = Aqua, size = 40.dp, shadowElevation = 3.dp)
                    }
                    if (!isMine && ownerId != null) {
                        SculptedIconButton(icon = Icons.Filled.Flag, contentDescription = "Report", onClick = onReport, accent = Danger, iconTint = Danger, size = 40.dp, shadowElevation = 3.dp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                if (listing.status == "active") {
                    if (listing.type == ListingType.HAVE) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            if (isMine) {
                                SculptedOutlinedButton(
                                    text = "Mark Traded",
                                    onClick = onMarkTraded,
                                    accent = Aqua,
                                    textColor = Aqua,
                                    icon = Icons.Filled.Check,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            SculptedButton(
                                text = if (alreadyInterested) "Interested" else "Interested!",
                                onClick = onInterested,
                                enabled = !alreadyInterested,
                                accent = Citrine,
                                containerColor = if (alreadyInterested) Color(0xFF3A3830) else Citrine,
                                textColor = if (alreadyInterested) DarkTextMid else Ink,
                                icon = Icons.Filled.Send,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    } else {
                        SculptedButton(
                            text = if (alreadyInterested) "Interested" else "I have this!",
                            onClick = onInterested,
                            enabled = !alreadyInterested,
                            accent = Aqua,
                            containerColor = if (alreadyInterested) Color(0xFF3A3830) else Aqua,
                            textColor = if (alreadyInterested) DarkTextMid else Ink,
                            icon = Icons.Filled.Send,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                SculptedTextButton(
                    text = "Close",
                    onClick = onDismiss,
                    accent = StoneLine,
                    textColor = DarkTextMid,
                )
            }
        }
    }
}

@Composable
private fun CompactListingTag(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.14f))
            .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(6.dp))
            .padding(horizontal = 5.dp, vertical = 1.dp),
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

package com.rork.rockscout.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.CapturedPhoto
import com.rork.rockscout.data.ListingMode
import com.rork.rockscout.data.ListingType
import com.rork.rockscout.data.ProfanityFilter
import com.rork.rockscout.data.ImageModerator
import com.rork.rockscout.data.ImageUtils
import com.rork.rockscout.data.ModerationTriState
import com.rork.rockscout.data.ImageReviewRepository
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.SpecimenImages
import com.rork.rockscout.data.TradeListing
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.data.FriendRepository
import com.rork.rockscout.data.NotificationRepository
import com.rork.rockscout.data.TradeInterestRepository
import com.rork.rockscout.data.NotificationHelper
import com.rork.rockscout.data.SessionStatus
import com.rork.rockscout.R
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.FullScreenImageViewer
import com.rork.rockscout.ui.components.LongPressableImage
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.SavedImagesPickerDialog
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.processSavedImage
import com.rork.rockscout.ui.components.ShareCardImage
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import androidx.compose.material.icons.filled.Flag
import androidx.compose.ui.platform.LocalView
import com.rork.rockscout.data.ReportRepository
import com.rork.rockscout.data.ReportScreenshotHelper
import com.rork.rockscout.ui.components.ReportSubmittedDialog
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Danger

private enum class ListingFilter(val label: String) {
    ALL("All"), HAVE("Have"), WANT("Want")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeBoardScreen(navController: NavController) {
    // ─── Premium gating: Trade Board is a social feature ───
    val accessManager = com.rork.rockscout.data.IdentifyAccessManager.instance
    val purchaseManager = com.rork.rockscout.data.PurchaseManager.instance
    val isPremium by purchaseManager.isPremium.collectAsStateWithLifecycle()
    val clubLocked = remember(isPremium) { accessManager.isSocialLocked(isPremium) }
    if (clubLocked) {
        com.rork.rockscout.ui.components.ClubLockedState(
            emoji = "\uD83D\uDD12",
            title = "Unlock the Trade Board",
            message = "Your 1-week free trial has ended. Subscribe or donate to post and browse trade listings.",
            buttonLabel = "Subscribe or donate",
            onButton = { navController.navigate(Routes.PAYWALL) },
        )
        return
    }

    val repo = AppRepository.instance
    val listings by repo.tradeListings.collectAsStateWithLifecycle()
    val captures by repo.captures.collectAsStateWithLifecycle()
    val collection by repo.collection.collectAsStateWithLifecycle()
    val wishlist by repo.wishlist.collectAsStateWithLifecycle()
    var editingListing by remember { mutableStateOf<TradeListing?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var initialEditorType by remember { mutableStateOf(ListingType.HAVE) }
    var viewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerInitialPage by remember { mutableStateOf(0) }
    var reportTargetListing by remember { mutableStateOf<TradeListing?>(null) }
    var showReportConfirm by remember { mutableStateOf(false) }
    var reportSubmitted by remember { mutableStateOf(false) }
    val myProfile by repo.profile.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val rootView = LocalView.current

    // Auto-expire stale listings on entry and seed sample data on first run.
    LaunchedEffect(Unit) {
        repo.expireStaleListings()
        if (listings.isEmpty()) {
            val me = AuthRepository.instance.currentUserId
            val myName = repo.profile.value.name.takeIf { it.isNotBlank() }
                ?: AuthRepository.instance.currentUserEmail?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
                ?: "Rock Scout"
            val sample = TradeListing(
                id = UUID.randomUUID().toString(),
                type = ListingType.HAVE,
                listingMode = ListingMode.SWAP,
                ownerUserId = me,
                ownerUsername = myName,
                specimenName = "Lake Superior Agate",
                condition = "Tumbled",
                description = "Beautiful banded Lake Superior agate, ~2.5 inches. Found last fall on the North Shore and tumbled it myself over 4 weeks.",
                wantInReturn = "Looking for a nice thomsonite or a Charlevoix stone — or a clean quartz point from Mount Ida.",
                tags = listOf("agate", "lake-superior", "tumbled"),
            )
            repo.saveTradeListing(sample)
            val sellSample = TradeListing(
                id = UUID.randomUUID().toString(),
                type = ListingType.HAVE,
                listingMode = ListingMode.SELLING,
                price = "$35",
                ownerUserId = me,
                ownerUsername = myName,
                specimenName = "Polished Quartz Point",
                condition = "Polished",
                description = "Clear quartz point from Mount Ida, Arkansas. About 3 inches, polished to a glass finish.",
                tags = listOf("quartz", "crystal", "arkansas"),
            )
            repo.saveTradeListing(sellSample)
            val wantSample = TradeListing(
                id = UUID.randomUUID().toString(),
                type = ListingType.WANT,
                listingMode = ListingMode.BUYING,
                ownerUserId = me,
                ownerUsername = myName,
                specimenName = "Chlorastrolite (Lake Superior Greenstone)",
                description = "On the hunt for a decent chlorastrolite — Michigan's state gem. Doesn't have to be huge, just a clean star pattern.",
                tags = listOf("chlorastrolite", "greenstone", "michigan"),
                sourceWishlistSpecimenId = null,
            )
            repo.saveTradeListing(wantSample)
        }
    }

    ScreenScaffold(
        title = "Trade Board",
        onBack = { navController.popBackStack() },
        background = { innerContent ->
            Box(modifier = Modifier.fillMaxSize()) {
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
                innerContent()
            }
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Enlarged info card — describes the Trade Board features, safety rules,
            // and the 14-day auto-expiry policy.
            DarkCard(
                accent = Citrine,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                contentPadding = PaddingValues(16.dp),
            ) {
                Column {
                    Text(
                        "\uD83D\uDC8E Trade Board",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Post specimens you have to swap, sell, or trade. Browse WANT listings from other hunters. Message owners, share listings, view profiles, and mark deals as done when the trade is complete.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextHigh,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "\u26A0\uFE0F Trade safely",
                        style = MaterialTheme.typography.titleSmall,
                        color = Aqua,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Meet in a public spot, never ship first to a stranger, and handle exchange details in private messages. No addresses or payment info in-app.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Aqua,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "\u23F0 Listings expire after 14 days",
                        style = MaterialTheme.typography.titleSmall,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Your posts automatically expire after 14 days to keep the board fresh. Expired listings can be re-listed from My Trades.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Main CTA — opens the full-screen Trading Floor.
            SculptedButton(
                text = "Enter the Trading Floor",
                onClick = { navController.navigate(Routes.TRADING_FLOOR) },
                accent = Citrine,
                containerColor = Citrine,
                textColor = Ink,
                icon = Icons.Filled.Send,
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            )

            Spacer(Modifier.height(10.dp))

            // Secondary action — post a listing without entering the Trading Floor.
            // Styled identically to the main CTA so all three top actions read as one
            // cohesive, tappable 3D row of buttons.
            SculptedButton(
                text = "Post a listing",
                onClick = { editingListing = null; initialEditorType = ListingType.HAVE; showEditor = true },
                accent = Citrine,
                containerColor = Citrine,
                textColor = Ink,
                icon = Icons.Filled.Add,
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            )

            Spacer(Modifier.height(10.dp))

            // My Trades shortcut — now matches the full Citrine 3D slab style so the
            // button is unmistakable against the trade-floor background image.
            SculptedButton(
                text = "My Trades",
                onClick = { navController.navigate(Routes.MY_TRADES) },
                accent = Citrine,
                containerColor = Citrine,
                textColor = Ink,
                icon = Icons.Filled.Bookmarks,
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            )
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

    if (viewerUrls.isNotEmpty()) {
        FullScreenImageViewer(
            imageUrls = viewerUrls,
            initialPage = viewerInitialPage,
            onDismiss = { viewerUrls = emptyList() },
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

@Composable
internal fun ListingCard(
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
    onPhotoClick: (List<String>, Int) -> Unit = { _, _ -> },
) {
    val accent = if (listing.type == ListingType.HAVE) Citrine else Aqua
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val ownerId = listing.ownerUserId
    val ownerName = listing.ownerUsername
    val isMine = ownerId == AuthRepository.instance.currentUserId
    var showDeleteConfirm by remember { mutableStateOf(false) }
    DarkCard(
        accent = accent,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            // Green dot prefix for trader friends — signifies the listing
            // owner is already in the user's RockScout Friends list.
            if (isFriend) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Success)
                        .glowingBorder(1.dp, Success.copy(alpha = 0.35f), CircleShape)
                        .align(Alignment.Top),
                )
                Spacer(Modifier.width(6.dp))
            }
            // Photo or type glyph
            Box(
                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(12.dp))
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
                        style = MaterialTheme.typography.displaySmall,
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (listing.type == ListingType.HAVE) accent else Color.Transparent)
                                .glowingBorder(
                                    if (listing.type == ListingType.WANT) 1.2.dp else 0.dp,
                                    accent,
                                    RoundedCornerShape(6.dp),
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                        ) {
                            Text(
                                listing.type.label.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (listing.type == ListingType.HAVE) Color.Black else accent,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when (listing.listingMode) {
                                        ListingMode.SELLING -> Color(0xFF7FD07F).copy(alpha = 0.22f)
                                        ListingMode.BUYING -> Color(0xFF6FA8C7).copy(alpha = 0.22f)
                                        else -> Color(0xFFE8A33D).copy(alpha = 0.18f)
                                    }
                                )
                                .glowingBorder(1.dp, Color(0xFF7FD07F).copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
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
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            dateFormat.format(Date(listing.createdAt)),
                            style = MaterialTheme.typography.labelSmall,
                            color = Aqua,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    listing.specimenName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                if (listing.condition.isNotBlank() && listing.type == ListingType.HAVE) {
                    Text(
                        "Condition: ${listing.condition}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Aqua,
                    )
                }
                // Owner username and message button are now shown in the top-right of the card header.
            }
        }
        Spacer(Modifier.height(6.dp))
        // Action icons on their own row so they don't squeeze the text above
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isMine) {
                SculptedIconButton(icon = Icons.Filled.Edit, contentDescription = "Edit", onClick = onEdit, accent = Aqua, iconTint = Aqua, size = 34.dp, shadowElevation = 3.dp)
                SculptedIconButton(icon = Icons.Filled.Share, contentDescription = "Share", onClick = onShare, accent = Aqua, iconTint = Aqua, size = 34.dp, shadowElevation = 3.dp)
                SculptedIconButton(icon = Icons.Filled.PersonAdd, contentDescription = "Share to Profile", onClick = onShareToProfile, accent = Citrine, iconTint = Citrine, size = 34.dp, shadowElevation = 3.dp)
                SculptedIconButton(icon = Icons.Filled.Delete, contentDescription = "Delete", onClick = onDelete, accent = Aqua, iconTint = Aqua, size = 34.dp, shadowElevation = 3.dp)
            }
            if (!isMine && ownerId != null) {
                SculptedIconButton(icon = Icons.Filled.Flag, contentDescription = "Report", onClick = onReport, accent = Danger, iconTint = Danger, size = 34.dp, shadowElevation = 3.dp)
            }
        }
        if (listing.description.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                listing.description,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextHigh,
                maxLines = 4,
            )
        }
        if (listing.type == ListingType.HAVE && listing.listingMode == ListingMode.SWAP && listing.wantInReturn.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                "Looking for: ${listing.wantInReturn}",
                style = MaterialTheme.typography.bodyMedium,
                color = accent,
                fontWeight = FontWeight.Medium,
            )
        }
        if (listing.listingMode == ListingMode.SELLING && listing.price.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                "Asking: ${listing.price}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF9DE89D),
                fontWeight = FontWeight.Bold,
            )
        }
        if (listing.listingMode == ListingMode.BUYING && listing.price.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                "Budget: ${listing.price}",
                style = MaterialTheme.typography.bodyLarge,
                color = Aqua,
                fontWeight = FontWeight.Bold,
            )
        }
        if (listing.tags.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                listing.tags.take(8).forEach { tag ->
                    TagChip(tag, color = accent)
                }
            }
        }
        if (listing.type == ListingType.HAVE && listing.status == "active") {
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
        } else if (listing.type == ListingType.WANT && listing.status == "active") {
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
        if (!ownerName.isNullOrBlank()) {
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Aqua.copy(alpha = 0.12f))
                        .glowingBorder(2.dp, Aqua.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
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
                        style = MaterialTheme.typography.labelMedium,
                        color = Aqua,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
    if (showDeleteConfirm) {
        com.rork.rockscout.ui.components.DeleteConfirmDialog(
            title = "Delete listing?",
            message = "Delete \"${listing.specimenName}\"? This action cannot be undone.",
            onConfirm = { showDeleteConfirm = false; onDelete() },
            onDismiss = { showDeleteConfirm = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ListingEditorDialog(
    initial: TradeListing?,
    initialType: ListingType,
    availableCaptures: List<CapturedPhoto>,
    collectionEntries: List<com.rork.rockscout.data.CollectionEntry>,
    wishlistIds: List<String>,
    onDismiss: () -> Unit,
    onSave: (TradeListing) -> Unit,
) {
    val isEdit = initial != null
    var type by remember { mutableStateOf(initial?.type ?: initialType) }
    var listingMode by remember { mutableStateOf(initial?.listingMode ?: ListingMode.SWAP) }
    var price by remember { mutableStateOf(initial?.price ?: "") }
    var specimenName by remember { mutableStateOf(initial?.specimenName ?: "") }
    var condition by remember { mutableStateOf(initial?.condition ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var wantInReturn by remember { mutableStateOf(initial?.wantInReturn ?: "") }
    val tags = remember { mutableStateListOf<String>().apply { initial?.tags?.let { addAll(it) } } }
    var newTag by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf(initial?.photoUri) }
    var sourceCaptureId by remember { mutableStateOf(initial?.sourceCaptureId) }
    var sourceCollectionSpecimenId by remember { mutableStateOf(initial?.sourceCollectionSpecimenId) }
    var sourceWishlistSpecimenId by remember { mutableStateOf(initial?.sourceWishlistSpecimenId) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showCapturePicker by remember { mutableStateOf(false) }
    var showCollectionPicker by remember { mutableStateOf(false) }
    var showWishlistPicker by remember { mutableStateOf(false) }
    var showSavedImagePicker by remember { mutableStateOf(false) }
    var showDatabasePicker by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri -> if (uri != null) {
        // Reject files larger than 5 MB before any pipeline work.
        if (ImageUtils.isOverUploadLimit(context, uri)) {
            android.widget.Toast.makeText(
                context,
                "That image is over 5 MB. Please choose a smaller photo.",
                android.widget.Toast.LENGTH_LONG,
            ).show()
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            val base64 = ImageUtils.uriToModerationBase64(context, uri)
            if (base64 == null) {
                return@launch
            }
            val verdict = ImageModerator.scan(base64, "image/jpeg")
            when (verdict.triState) {
                ModerationTriState.CLEAN -> {
                    val persistentPath = ImageUtils.copyUriToInternalStorage(
                        context, uri, "trade_listings",
                    )
                    photoUri = persistentPath ?: uri.toString()
                    sourceCaptureId = null; sourceCollectionSpecimenId = null
                }
                ModerationTriState.EXPLICIT -> {
                    android.widget.Toast.makeText(
                        context,
                        verdict.reason.ifBlank { "This image can't be used because it violates our family-friendly policies." },
                        android.widget.Toast.LENGTH_LONG,
                    ).show()
                }
                ModerationTriState.QUESTIONABLE -> {
                    val persistentPath = ImageUtils.copyUriToInternalStorage(
                        context, uri, "trade_listings",
                    )
                    val userId = AuthRepository.instance.currentUserId
                    val userName = AppRepository.instance.profile.value.name
                    val avatar = AppRepository.instance.profile.value.avatarEmoji
                    ImageReviewRepository.instance.submitReview(
                        userId = userId ?: "unknown",
                        userName = userName,
                        userAvatar = avatar,
                        imageUri = persistentPath ?: uri.toString(),
                        type = "trade_listing",
                        reason = verdict.reason,
                    )
                    photoUri = persistentPath ?: uri.toString()
                    sourceCaptureId = null; sourceCollectionSpecimenId = null
                    android.widget.Toast.makeText(
                        context,
                        "Image submitted for review. It'll be visible once approved.",
                        android.widget.Toast.LENGTH_LONG,
                    ).show()
                }
            }
        }
    } }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
    ) { _ -> /* preview-only placeholder; full camera capture wired when on device */ }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                if (isEdit) "Edit Listing" else if (type == ListingType.HAVE) "New Have Listing" else "New Want Listing",
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).imePadding()) {
                // Type toggle
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = type == ListingType.HAVE,
                        onClick = { type = ListingType.HAVE },
                        label = { Text("Have") },
                    )
                    FilterChip(
                        selected = type == ListingType.WANT,
                        onClick = { type = ListingType.WANT },
                        label = { Text("Want") },
                    )
                }
                Spacer(Modifier.height(12.dp))

                // Listing mode chips — swap / selling / buying for HAVE, swap / buying for WANT.
                Text(
                    "Listing type",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = listingMode == ListingMode.SWAP,
                        onClick = { listingMode = ListingMode.SWAP; price = "" },
                        label = { Text("Swap") },
                    )
                    FilterChip(
                        selected = listingMode == ListingMode.SELLING,
                        onClick = { listingMode = ListingMode.SELLING },
                        label = { Text("Selling") },
                    )
                    if (type == ListingType.HAVE) {
                        FilterChip(
                            selected = listingMode == ListingMode.BUYING,
                            onClick = { listingMode = ListingMode.BUYING },
                            label = { Text("Buying") },
                        )
                    }
                }
                if (listingMode == ListingMode.SELLING || listingMode == ListingMode.BUYING) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = {
                            Text(
                                if (type == ListingType.HAVE) "Asking price (e.g. $40, $15 + shipping)"
                                else "Budget (e.g. $40, $15 + shipping)"
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None, imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth().noAutoFocus(),
                    )
                }
                Spacer(Modifier.height(12.dp))

                // Source picker
                Text(
                    if (type == ListingType.HAVE) "Source specimen" else "Source specimen",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(6.dp))
                if (type == ListingType.HAVE) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        SourceButton(
                            label = "Field",
                            icon = Icons.Filled.PhotoLibrary,
                            onClick = { showCapturePicker = true },
                            modifier = Modifier.weight(1f),
                        )
                        SourceButton(
                            label = "My Rocks",
                            icon = Icons.Filled.Collections,
                            onClick = { showCollectionPicker = true },
                            modifier = Modifier.weight(1f),
                        )
                        SourceButton(
                            label = "Saved",
                            icon = Icons.Filled.Download,
                            onClick = { showSavedImagePicker = true },
                            modifier = Modifier.weight(1f),
                        )
                        SourceButton(
                            label = "Database",
                            icon = Icons.Filled.Collections,
                            onClick = { showDatabasePicker = true },
                            modifier = Modifier.weight(1f),
                        )
                        SourceButton(
                            label = "Gallery",
                            icon = Icons.Filled.Bookmarks,
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f),
                        )
                        SourceButton(
                            label = "Camera",
                            icon = Icons.Filled.CameraAlt,
                            onClick = { cameraLauncher.launch(null) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = { showWishlistPicker = true },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Filled.Bookmarks, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Pick from Wishlist")
                    }
                }
                // Current source attribution
                if (sourceCaptureId != null) {
                    val cap = availableCaptures.firstOrNull { it.id == sourceCaptureId }
                    if (cap != null) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "From Field Capture: " + cap.customName.ifBlank { SeedData.specimenById(cap.specimenId)?.name ?: "Unknown" },
                            style = MaterialTheme.typography.labelSmall,
                            color = Citrine,
                        )
                    }
                }
                if (sourceCollectionSpecimenId != null) {
                    val spec = SeedData.specimenById(sourceCollectionSpecimenId!!)
                    if (spec != null) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "From My Rocks: ${spec.name}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Citrine,
                        )
                    }
                }
                if (sourceWishlistSpecimenId != null) {
                    val spec = SeedData.specimenById(sourceWishlistSpecimenId!!)
                    if (spec != null) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "From Wishlist: ${spec.name}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Aqua,
                        )
                    }
                }

                // Photo preview
                if (photoUri != null) {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier.size(96.dp).clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1A1812))
                            .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                    ) {
                        AsyncImage(
                            model = photoUri,
                            contentDescription = "Listing photo",
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop,
                        )
                        Box(
                            modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                                .sculpted(
                                    shape = CircleShape,
                                    accent = Citrine,
                                    shadowElevation = 3.dp,
                                    circular = true,
                                    onClick = { photoUri = null },
                                )
                                .clip(CircleShape)
                                .background(Color.Black),
                            contentAlignment = Alignment.Center,
                        ) { Icon(Icons.Filled.Close, "Remove", tint = Color.White, modifier = Modifier.size(14.dp)) }
                    }
                }

                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = specimenName,
                    onValueChange = { specimenName = it },
                    label = { Text("Specimen name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                )

                if (type == ListingType.HAVE) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = condition,
                        onValueChange = { condition = it },
                        label = { Text("Condition (rough / tumbled / cut / polished / slab)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth().noAutoFocus(),
                    )
                }

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(if (type == ListingType.HAVE) "What you're offering" else "What you're looking for") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp).noAutoFocus(),
                    minLines = 3,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Default),
                )

                if (type == ListingType.HAVE && listingMode == ListingMode.SWAP) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = wantInReturn,
                        onValueChange = { wantInReturn = it },
                        label = { Text("What you want in return") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth().noAutoFocus(),
                    )
                }

                // Ensure WANT listings never stay in SELLING mode.
                LaunchedEffect(type) {
                    if (type == ListingType.WANT && listingMode == ListingMode.SELLING) {
                        listingMode = ListingMode.SWAP
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text("Tags", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Citrine.copy(alpha = 0.14f))
                                .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                .clickable { tags.remove(tag) }
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(tag, style = MaterialTheme.typography.labelMedium, color = Citrine, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.width(4.dp))
                                Icon(Icons.Filled.Close, "Remove", tint = Citrine, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newTag,
                        onValueChange = { newTag = it },
                        label = { Text("Add tag") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                        modifier = Modifier.weight(1f).noAutoFocus(),
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = { if (newTag.isNotBlank()) { tags.add(newTag.trim().lowercase()); newTag = "" } },
                    ) { Icon(Icons.Filled.Add, "Add", tint = Citrine) }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val listing = TradeListing(
                        id = initial?.id ?: UUID.randomUUID().toString(),
                        type = type,
                        listingMode = listingMode,
                        price = if (listingMode == ListingMode.SELLING || listingMode == ListingMode.BUYING) ProfanityFilter.filter(price.trim()) else "",
                        specimenName = ProfanityFilter.filter(specimenName.ifBlank { "Untitled specimen" }),
                        condition = ProfanityFilter.filter(condition.trim()),
                        description = ProfanityFilter.filter(description.trim()),
                        wantInReturn = if (type == ListingType.HAVE && listingMode == ListingMode.SWAP) ProfanityFilter.filter(wantInReturn.trim()) else "",
                        photoUri = photoUri,
                        tags = tags.map { ProfanityFilter.filter(it) },
                        sourceCaptureId = sourceCaptureId,
                        sourceCollectionSpecimenId = sourceCollectionSpecimenId,
                        sourceWishlistSpecimenId = sourceWishlistSpecimenId,
                        status = initial?.status ?: "active",
                        createdAt = initial?.createdAt ?: System.currentTimeMillis(),
                        expiresAt = initial?.expiresAt ?: (System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000),
                    )
                    onSave(listing)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (type == ListingType.HAVE) Citrine else Aqua,
                    contentColor = Color.Black,
                ),
                enabled = specimenName.isNotBlank(),
            ) { Text(if (isEdit) "Save Changes" else "Post Listing") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )

    if (showCapturePicker) {
        CapturePickerSheet(
            available = availableCaptures,
            onDismiss = { showCapturePicker = false },
            onPick = { cap ->
                sourceCaptureId = cap.id
                sourceCollectionSpecimenId = null
                sourceWishlistSpecimenId = null
                if (specimenName.isBlank()) {
                    specimenName = cap.customName.ifBlank { SeedData.specimenById(cap.specimenId)?.name ?: "" }
                }
                if (description.isBlank()) description = cap.generalInfo
                if (cap.imageUris.isNotEmpty()) photoUri = cap.imageUris.first()
                showCapturePicker = false
            },
        )
    }
    if (showCollectionPicker) {
        CollectionPickerSheet(
            collectionEntries = collectionEntries,
            onDismiss = { showCollectionPicker = false },
            onPick = { specId ->
                sourceCollectionSpecimenId = specId
                sourceCaptureId = null
                sourceWishlistSpecimenId = null
                val spec = SeedData.specimenById(specId)
                if (spec != null) {
                    if (specimenName.isBlank()) specimenName = spec.name
                    if (description.isBlank()) description = spec.tagline
                    val entry = collectionEntries.firstOrNull { it.specimenId == specId }
                    if (entry != null && entry.note.isNotBlank()) {
                        if (description.isBlank()) description = entry.note
                    }
                }
                showCollectionPicker = false
            },
        )
    }
    if (showWishlistPicker) {
        WishlistPickerSheet(
            wishlistIds = wishlistIds,
            onDismiss = { showWishlistPicker = false },
            onPick = { specId ->
                sourceWishlistSpecimenId = specId
                sourceCaptureId = null
                sourceCollectionSpecimenId = null
                val spec = SeedData.specimenById(specId)
                if (spec != null) {
                    specimenName = spec.name
                    if (description.isBlank()) description = "Looking for ${spec.name}."
                    if (tags.isEmpty()) tags.addAll(listOf(spec.id, spec.rockClass.label.lowercase()))
                }
                showWishlistPicker = false
            },
        )
    }
    if (showSavedImagePicker) {
        SavedImagesPickerDialog(
            onDismiss = { showSavedImagePicker = false },
            onImageSelected = { image ->
                showSavedImagePicker = false
                scope.launch {
                    val path = processSavedImage(context, image, "trade_listings", "trade_listing")
                    if (path != null) {
                        photoUri = path
                        sourceCaptureId = null
                        sourceCollectionSpecimenId = null
                        sourceWishlistSpecimenId = null
                    }
                }
            },
        )
    }
    if (showDatabasePicker) {
        DatabasePickerSheet(
            onDismiss = { showDatabasePicker = false },
            onPick = { specId ->
                sourceCollectionSpecimenId = null
                sourceCaptureId = null
                sourceWishlistSpecimenId = null
                val spec = SeedData.specimenById(specId)
                if (spec != null) {
                    if (specimenName.isBlank()) specimenName = spec.name
                    if (description.isBlank()) description = spec.tagline
                    val firstImg = SpecimenImages.urls[spec.id]?.firstOrNull()
                    if (firstImg != null && photoUri == null) photoUri = firstImg
                    if (tags.isEmpty()) tags.addAll(listOf(spec.id, spec.rockClass.label.lowercase()))
                }
                showDatabasePicker = false
            },
        )
    }
}

@Composable
private fun DatabasePickerSheet(
    onDismiss: () -> Unit,
    onPick: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Pick from Specimen Database", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    items(SeedData.allSpecimens, key = { it.id }) { spec ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().sculpted(shape = RoundedCornerShape(10.dp), accent = Citrine, shadowElevation = 3.dp, onClick = { onPick(spec.id) })
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .glowingBorder(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                .padding(10.dp),
                        ) {
                            Text(spec.emoji, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(spec.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                Text(spec.tagline, style = MaterialTheme.typography.labelSmall, color = TextLow, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun SourceButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(38.dp),
        border = BorderStroke(1.dp, Aqua),
        shape = RoundedCornerShape(50.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
    ) {
        Icon(icon, null, modifier = Modifier.size(13.dp), tint = Aqua)
        Spacer(Modifier.width(3.dp))
        Text(
            label,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 12.sp),
            color = Aqua,
        )
    }
}

@Composable
private fun CapturePickerSheet(
    available: List<CapturedPhoto>,
    onDismiss: () -> Unit,
    onPick: (CapturedPhoto) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Pick a Field Capture", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                if (available.isEmpty()) {
                    Text("No field captures yet. Run an identification through the camera first, then you can list it here.",
                        style = MaterialTheme.typography.bodyMedium, color = TextLow)
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        items(available, key = { it.id }) { cap ->
                            val name = cap.customName.ifBlank { SeedData.specimenById(cap.specimenId)?.name ?: "Unknown" }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().sculpted(shape = RoundedCornerShape(10.dp), accent = Citrine, shadowElevation = 3.dp, onClick = { onPick(cap) })
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .glowingBorder(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                    .padding(10.dp),
                            ) {
                                Box(
                                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1A1812))
                                        .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (cap.imageUris.isNotEmpty()) {
                                        AsyncImage(
                                            model = cap.imageUris.first(),
                                            contentDescription = name,
                                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop,
                                        )
                                    } else {
                                        Text(cap.specimenEmoji, style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(cap.timestamp)),
                                        style = MaterialTheme.typography.labelSmall, color = TextLow,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun CollectionPickerSheet(
    collectionEntries: List<com.rork.rockscout.data.CollectionEntry>,
    onDismiss: () -> Unit,
    onPick: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Pick from My Rocks", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                if (collectionEntries.isEmpty()) {
                    Text("Your collection is empty. Add a specimen to My Rocks first, then list it here.",
                        style = MaterialTheme.typography.bodyMedium, color = TextLow)
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        items(collectionEntries, key = { it.specimenId }) { entry ->
                            val spec = SeedData.specimenById(entry.specimenId) ?: return@items
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().sculpted(shape = RoundedCornerShape(10.dp), accent = Citrine, shadowElevation = 3.dp, onClick = { onPick(spec.id) })
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .glowingBorder(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                    .padding(10.dp),
                            ) {
                                Text(spec.emoji, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(spec.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                    if (entry.foundAt.isNotBlank()) {
                                        Text(entry.foundAt, style = MaterialTheme.typography.labelSmall, color = TextLow)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun WishlistPickerSheet(
    wishlistIds: List<String>,
    onDismiss: () -> Unit,
    onPick: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Pick from Wishlist", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                if (wishlistIds.isEmpty()) {
                    Text("Your wishlist is empty. Add a specimen to your Wishlist first, then post it as a Want here.",
                        style = MaterialTheme.typography.bodyMedium, color = TextLow)
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        items(wishlistIds, key = { it }) { specId ->
                            val spec = SeedData.specimenById(specId) ?: return@items
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().sculpted(shape = RoundedCornerShape(10.dp), accent = Citrine, shadowElevation = 3.dp, onClick = { onPick(spec.id) })
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .glowingBorder(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                    .padding(10.dp),
                            ) {
                                Text(spec.emoji, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(spec.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                    Text(spec.tagline, style = MaterialTheme.typography.labelSmall, color = TextLow, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

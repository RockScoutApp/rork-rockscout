package com.rork.rockscout.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.CapturedPhoto
import com.rork.rockscout.data.IdentifyAccessManager
import com.rork.rockscout.data.PurchaseManager
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.ui.components.FieldCaptureCard
import com.rork.rockscout.ui.components.GlobalSearchSection
import com.rork.rockscout.ui.components.LockedFeatureBanner
import com.rork.rockscout.ui.components.MergeableCapture
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.ShareCardImage
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.SpecimenMarkerMap
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.UploadSpecimenPill
import com.rork.rockscout.ui.components.rockClassColor
import com.rork.rockscout.ui.components.DeleteConfirmDialog
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextLow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun CapturesScreen(navController: NavController) {
    val repo = AppRepository.instance
    val captures by repo.captures.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val accessManager = IdentifyAccessManager.instance
    val purchaseManager = PurchaseManager.instance
    val isPremium by purchaseManager.isPremium.collectAsStateWithLifecycle()
    val featureLocked = remember(isPremium) {
        accessManager.isFeatureLocked(isPremium)
    }
    var shareToProfileCapture by remember { mutableStateOf<CapturedPhoto?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var pendingDeleteCapture by remember { mutableStateOf<CapturedPhoto?>(null) }
    var showUploadSpecimenDialog by remember { mutableStateOf(false) }

    // Selection mode state
    var selectionMode by remember { mutableStateOf(false) }
    val selectedIds = remember { mutableStateSetOf<String>() }
    var showBatchDeleteDialog by remember { mutableStateOf(false) }

    // Pager state — 2 pages: Captures (0) and Specimen Map (1)
    val pagerState = rememberPagerState(initialPage = 0) { 2 }
    val pagerScope = rememberCoroutineScope()

    BackHandler(enabled = selectionMode) {
        selectedIds.clear()
        selectionMode = false
    }

    val selectedCount = selectedIds.size
    val allSelected = captures.isNotEmpty() && selectedIds.size == captures.size

    // Batch delete confirmation
    if (showBatchDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showBatchDeleteDialog = false },
            title = { Text("Delete $selectedCount capture${if (selectedCount != 1) "s" else ""}?") },
            text = {
                Text(
                    "Are you sure you want to delete $selectedCount field capture${if (selectedCount != 1) "s" else ""}? This cannot be undone.",
                    color = TextLow,
                )
            },
            confirmButton = {
                SculptedTextButton(
                    text = "Delete All",
                    onClick = {
                        repo.removeCaptures(selectedIds.toSet())
                        selectedIds.clear()
                        selectionMode = false
                        showBatchDeleteDialog = false
                    },
                    accent = Danger,
                    textColor = Danger,
                )
            },
            dismissButton = {
                SculptedTextButton(
                    text = "Cancel",
                    onClick = { showBatchDeleteDialog = false },
                    accent = Citrine,
                    textColor = TextLow,
                )
            },
            containerColor = Color(0xFF2A2820),
            titleContentColor = Color.White,
            textContentColor = TextLow,
        )
    }

    ScreenScaffold(
        title = "Field Captures",
        onBack = { navController.popBackStack() },
        actions = {
            if (!featureLocked && captures.isNotEmpty() && pagerState.currentPage == 0) {
                SculptedIconButton(
                    icon = if (selectionMode) Icons.Filled.Close else Icons.Filled.CheckCircle,
                    contentDescription = if (selectionMode) "Exit selection" else "Select captures",
                    onClick = {
                        if (selectionMode) {
                            selectedIds.clear()
                        }
                        selectionMode = !selectionMode
                    },
                    accent = Citrine,
                    iconTint = if (selectionMode) Citrine else TextLow,
                )
            }
        },
    ) {
        if (featureLocked) {
            LockedFeatureBanner(
                onSubscribe = { navController.navigate(Routes.PAYWALL) },
                message = "Your 1-week trial has ended. Subscribe or donate to log and edit field captures.",
                modifier = Modifier.padding(20.dp),
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Pill switcher — Captures / Specimen Map
                CapturesPagePillSwitcher(
                    currentPage = pagerState.currentPage,
                    onPageSelected = { page ->
                        pagerScope.launch { pagerState.animateScrollToPage(page) }
                    },
                    onUploadClick = { showUploadSpecimenDialog = true },
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    pageSpacing = 0.dp,
                ) { page ->
                    when (page) {
                        0 -> CapturesListPage(
                            captures = captures,
                            selectionMode = selectionMode,
                            selectedIds = selectedIds,
                            allSelected = allSelected,
                            selectedCount = selectedCount,
                            searchQuery = searchQuery,
                            onQueryChange = { searchQuery = it },
                            navController = navController,
                            onToggleSelection = { id ->
                                if (id in selectedIds) selectedIds.remove(id) else selectedIds.add(id)
                            },
                            onSelectAllToggle = {
                                if (allSelected) selectedIds.clear()
                                else { selectedIds.clear(); captures.forEach { selectedIds.add(it.id) } }
                            },
                            onToggleCollection = { id -> repo.toggleCaptureInCollection(id) },
                            onToggleWishlist = { id -> repo.toggleCaptureInWishlist(id) },
                            onSaveFields = { id, name, location, info ->
                                repo.updateCaptureFields(id, name, location, info)
                            },
                            onAddImage = { id, uri -> repo.addImageToCapture(id, uri) },
                            onMergeCapture = { target, other -> repo.mergeCaptures(target, other) },
                            onRemoveImage = { id, idx -> repo.removeImageFromCapture(id, idx) },
                            onDelete = { capture -> pendingDeleteCapture = capture },
                            onShare = { capture ->
                                scope.launch {
                                    val spec = SeedData.specimenById(capture.specimenId)
                                    val displayName = capture.displayName(spec?.name ?: "Unknown specimen")
                                    val dateFormat = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
                                    val photo: android.graphics.Bitmap? =
                                        capture.imageUris.firstOrNull()?.let { uriStr ->
                                            ShareCardImage.loadDownsampled(context, Uri.parse(uriStr))
                                        }
                                    ShareCardImage.share(
                                        context = context,
                                        title = displayName,
                                        subtitle = (spec?.category ?: "Field capture") + "  •  " +
                                            dateFormat.format(Date(capture.timestamp)),
                                        body = capture.generalInfo.ifBlank { null },
                                        accentHex = if (spec != null) spec.colorHex else 0xFF2C6F9B,
                                        photoBitmap = photo,
                                        caption = "Posted from RockScout",
                                        fileName = "rockscout_capture_${capture.id}",
                                    )
                                }
                            },
                            onShareToProfile = { capture -> shareToProfileCapture = capture },
                        )
                        1 -> SpecimenMarkerMap(
                            captures = captures,
                            modifier = Modifier.fillMaxSize(),
                            onPinTapped = { capture ->
                                navController.navigate(Routes.specimen(capture.specimenId))
                            },
                        )
                    }
                }
            }
        }
    }

    // Bottom mass-action bar when in selection mode and items are selected
    if (selectionMode && selectedCount > 0 && !featureLocked) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF2A2820), Color(0xFF1E1C16))
                            )
                        )
                        .glowingBorder(2.dp, Citrine.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Move to My Rocks
                    MassActionButton(
                        icon = Icons.Filled.Inventory2,
                        label = "Move to\nMy Rocks",
                        color = Success,
                        onClick = {
                            repo.moveCapturesToCollection(selectedIds.toSet())
                            selectedIds.clear()
                            selectionMode = false
                        },
                    )
                    // Move to Wishlist
                    MassActionButton(
                        icon = Icons.Filled.Bookmark,
                        label = "Move to\nWishlist",
                        color = Citrine,
                        onClick = {
                            repo.moveCapturesToWishlist(selectedIds.toSet())
                            selectedIds.clear()
                            selectionMode = false
                        },
                    )
                    // Delete
                    MassActionButton(
                        icon = Icons.Filled.Delete,
                        label = "Delete\n($selectedCount)",
                        color = Danger,
                        onClick = { showBatchDeleteDialog = true },
                    )
                }
            }
        }
    }

    shareToProfileCapture?.let { cap ->
        val spec = SeedData.specimenById(cap.specimenId)
        val specimenName = spec?.name ?: "Unknown specimen"
        ShareToProfileComposer(
            sourceType = "capture",
            title = cap.displayName(specimenName),
            tagline = (spec?.category ?: "Field capture") + "  •  " +
                SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(cap.timestamp)),
            imageUri = cap.imageUris.firstOrNull(),
            locationText = cap.customLocation,
            onDismiss = { shareToProfileCapture = null },
        )
    }

    pendingDeleteCapture?.let { cap ->
        DeleteConfirmDialog(
            title = "Delete capture?",
            message = "Delete this field capture? This action cannot be undone.",
            onConfirm = {
                repo.removeCapture(cap.id)
                pendingDeleteCapture = null
            },
            onDismiss = { pendingDeleteCapture = null },
        )
    }

    if (showUploadSpecimenDialog) {
        SubmitSpecimenDialog(onDismiss = { showUploadSpecimenDialog = false })
    }
}

/* ── Pill switcher — Captures / Specimen Map ─────────────────────────────── */

@Composable
private fun CapturesPagePillSwitcher(
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
    onUploadClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val pills = listOf("Captures" to 0, "Specimen Map" to 1)
        pills.forEach { (label, page) ->
            val isActive = currentPage == page
            val accent = if (page == 0) Citrine else Amethyst
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (isActive) accent.copy(alpha = 0.18f) else Color.Transparent
                    )
                    .glowingBorder(
                        1.5.dp,
                        if (isActive) accent else Color(0x33FFFFFF),
                        RoundedCornerShape(24.dp),
                    )
                    .clickable { onPageSelected(page) }
                    .padding(horizontal = 24.dp, vertical = 8.dp),
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isActive) accent else DarkTextMid,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                )
            }
            if (page < 1) {
                Spacer(Modifier.width(10.dp))
            }
        }
        Spacer(Modifier.width(10.dp))
        UploadSpecimenPill(onClick = onUploadClick)
    }
}

/* ── Page 1: Captures list (extracted from original CapturesScreen) ──────── */

@Composable
private fun CapturesListPage(
    captures: List<CapturedPhoto>,
    selectionMode: Boolean,
    selectedIds: androidx.compose.runtime.snapshots.SnapshotStateSet<String>,
    allSelected: Boolean,
    selectedCount: Int,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    navController: NavController,
    onToggleSelection: (String) -> Unit,
    onSelectAllToggle: () -> Unit,
    onToggleCollection: (String) -> Unit,
    onToggleWishlist: (String) -> Unit,
    onSaveFields: (String, String, String, String) -> Unit,
    onAddImage: (String, String) -> Unit,
    onMergeCapture: (String, String) -> Unit,
    onRemoveImage: (String, Int) -> Unit,
    onDelete: (CapturedPhoto) -> Unit,
    onShare: (CapturedPhoto) -> Unit,
    onShareToProfile: (CapturedPhoto) -> Unit,
) {
    if (captures.isEmpty()) {
        EmptyState(
            emoji = "\uD83D\uDCF7",
            title = "No captures yet",
            message = "Photos you snap through the identify camera or the Field Camera tile will appear here as your field journal.",
        )
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp, end = 20.dp,
            top = 8.dp,
            bottom = if (selectionMode) 88.dp else 40.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            GlobalSearchSection(
                navController = navController,
                query = searchQuery,
                onQueryChange = onQueryChange,
                placeholder = "Search your captures, rocks, dig sites, hunters…",
            )
        }
        // Select All / Deselect bar when in selection mode
        if (selectionMode) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Citrine.copy(alpha = 0.12f))
                        .glowingBorder(1.dp, Citrine.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .clickable { onSelectAllToggle() }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        if (allSelected) Icons.Filled.Deselect else Icons.Filled.SelectAll,
                        contentDescription = null,
                        tint = Citrine,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        if (allSelected) "Deselect all" else "Select all (${captures.size})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Citrine,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        "$selectedCount selected",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextLow,
                    )
                }
            }
        } else {
            item {
                Text(
                    "${captures.size} field capture${if (captures.size != 1) "s" else ""} in your journal. Tap the select icon to batch-manage your captures.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Aqua,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
        }
        items(captures, key = { it.id }) { capture ->
            val spec = SeedData.specimenById(capture.specimenId)
            val accent = if (spec != null) rockClassColor(spec.rockClass) else Citrine
            val specimenName = spec?.name ?: "Unknown specimen"
            val displayName = capture.displayName(specimenName)
            val dateFormat = remember { SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault()) }
            val others = remember(captures, capture.id) {
                captures.filter { it.id != capture.id }.map {
                    MergeableCapture(
                        id = it.id,
                        displayName = it.displayName(SeedData.specimenById(it.specimenId)?.name ?: "Unknown specimen"),
                        thumbnailUri = it.imageUris.firstOrNull(),
                        emoji = it.specimenEmoji,
                        photoCount = it.imageUris.size,
                    )
                }
            }
            FieldCaptureCard(
                capture = capture,
                specimenName = specimenName,
                accent = accent,
                otherCaptures = others,
                inCollection = capture.inCollection,
                inWishlist = capture.inWishlist,
                selectionMode = selectionMode,
                isSelected = capture.id in selectedIds,
                onToggleSelection = { onToggleSelection(capture.id) },
                onToggleCollection = { onToggleCollection(capture.id) },
                onToggleWishlist = { onToggleWishlist(capture.id) },
                onSaveFields = { name, location, info ->
                    onSaveFields(capture.id, name, location, info)
                },
                onAddImage = { uriStr -> onAddImage(capture.id, uriStr) },
                onMergeCapture = { otherId -> onMergeCapture(capture.id, otherId) },
                onRemoveImage = { index -> onRemoveImage(capture.id, index) },
                onDelete = { onDelete(capture) },
                onShare = { onShare(capture) },
                onShareToProfile = { onShareToProfile(capture) },
            )
        }
    }
}

@Composable
private fun MassActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = TextLow,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        )
    }
}

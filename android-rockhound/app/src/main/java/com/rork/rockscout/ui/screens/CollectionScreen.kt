package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.CollectionCsvExporter
import com.rork.rockscout.data.CollectionPdfExporter
import com.rork.rockscout.data.CollectionZipExporter
import com.rork.rockscout.data.IdentifyAccessManager
import com.rork.rockscout.data.PurchaseManager
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.SpecimenImages
import com.rork.rockscout.ui.components.LockedFeatureBanner
import com.rork.rockscout.ui.components.CategoryFilterRow
import com.rork.rockscout.ui.components.ListCategoryFilter
import com.rork.rockscout.ui.components.filterSpecimensByCategory
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.FieldCaptureCard
import com.rork.rockscout.ui.components.FullScreenImageViewer
import com.rork.rockscout.ui.components.MergeableCapture
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.CardHeart
import com.rork.rockscout.ui.components.SpecimenAddShare
import com.rork.rockscout.ui.components.SpecimenListItem
import com.rork.rockscout.ui.components.CompactAddShareButton
import com.rork.rockscout.ui.components.ShareCardImage
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.GlobalSearchSection
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.rockClassColor
import com.rork.rockscout.ui.components.shortCategoryLabel
import com.rork.rockscout.ui.components.LongPressableImage
import com.rork.rockscout.ui.components.DeleteConfirmDialog
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.theme.TextLow
import android.net.Uri
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(navController: NavController) {
    val repo = AppRepository.instance
    val collection by repo.collection.collectAsStateWithLifecycle()
    val captures by repo.captures.collectAsStateWithLifecycle()
    val wishlist by repo.wishlist.collectAsStateWithLifecycle()
    val likedSpecimens by repo.likedSpecimens.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val accessManager = IdentifyAccessManager.instance
    val purchaseManager = PurchaseManager.instance
    val isPremium by purchaseManager.isPremium.collectAsStateWithLifecycle()
    val featureLocked = remember(isPremium) {
        accessManager.isFeatureLocked(isPremium)
    }
    var shareToProfileCapture by remember { mutableStateOf<com.rork.rockscout.data.CapturedPhoto?>(null) }
    var shareToProfileSpec by remember { mutableStateOf<com.rork.rockscout.data.Specimen?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var pendingDeleteCapture by remember { mutableStateOf<com.rork.rockscout.data.CapturedPhoto?>(null) }
    var selectedFilter by remember { mutableStateOf<ListCategoryFilter?>(null) }
    var viewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerInitialPage by remember { mutableIntStateOf(0) }
    var isExportingZip by remember { mutableStateOf(false) }
    var zipExportError by remember { mutableStateOf<String?>(null) }
    var isExportingPdf by remember { mutableStateOf(false) }
    var pdfExportError by remember { mutableStateOf<String?>(null) }
    val profile by repo.profile.collectAsStateWithLifecycle()

    // Date-range filter for the ZIP export. Null means "no filter" (export
    // the entire collection). Stored as epoch UTC millis; the picker produces
    // start-of-day values, and the exporter treats end as end-of-day.
    var rangeStartMillis by remember { mutableStateOf<Long?>(null) }
    var rangeEndMillis by remember { mutableStateOf<Long?>(null) }
    var showRangePicker by remember { mutableStateOf(false) }

    // Mass-delete selection mode. When active, each card shows a checkbox and
    // tapping a card toggles selection instead of opening the specimen.
    var selectionMode by remember { mutableStateOf(false) }
    val selectedSpecimenIds = remember { mutableStateOf<Set<String>>(emptySet()) }
    val selectedCaptureIds = remember { mutableStateOf<Set<String>>(emptySet()) }
    var showBulkDeleteDialog by remember { mutableStateOf(false) }

    // Field captures promoted to My Rocks
    val captureInCollection = remember(captures) {
        captures.filter { it.inCollection }
    }

    // Helper — clear all selection state and exit selection mode.
    fun clearSelection() {
        selectionMode = false
        selectedSpecimenIds.value = emptySet()
        selectedCaptureIds.value = emptySet()
    }

    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    ScreenScaffold(title = "My Rocks", onBack = { navController.popBackStack() }) {
        if (featureLocked) {
            LockedFeatureBanner(
                onSubscribe = { navController.navigate(Routes.PAYWALL) },
                message = "Your 1-week trial has ended. Subscribe or donate to add and edit your collection.",
                modifier = Modifier.padding(20.dp),
            )
        } else if (collection.isEmpty() && captureInCollection.isEmpty()) {
            EmptyState(
                emoji = "\uD83E\uDEA8",
                title = "No specimens yet",
                message = "Identify or browse rocks and tap \"Add to Collection\" to start your cabinet.",
            )
        } else {
            val sortedCollection = remember(collection) {
                collection.sortedBy { SeedData.specimenById(it.specimenId)?.name?.lowercase() ?: it.specimenId }
            }
            // Apply category filter to database specimens
            val filteredCollection = remember(sortedCollection, selectedFilter) {
                if (selectedFilter == null) sortedCollection
                else sortedCollection.filter { entry ->
                    SeedData.specimenById(entry.specimenId)?.let { spec ->
                        filterSpecimensByCategory(listOf(spec), selectedFilter).isNotEmpty()
                    } ?: false
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                item {
                    GlobalSearchSection(
                        navController = navController,
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        placeholder = "Search your rocks, captures, locations, friends…",
                    )
                }
                item {
                    CategoryFilterRow(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it },
                    )
                }
                item {
                    val total = filteredCollection.size + captureInCollection.size
                    // ── Master build task M: action buttons moved to a full-width row at the
                    // very top of the list, above the description and count text, so they
                    // never collapse into vertical lettering on narrow screens. ──
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Enter / exit mass-delete selection mode.
                        SculptedOutlinedButton(
                            text = if (selectionMode) "Done" else "Select",
                            onClick = {
                                if (selectionMode) clearSelection()
                                else selectionMode = true
                            },
                            accent = Citrine,
                            textColor = Citrine,
                            icon = if (selectionMode) Icons.Filled.CheckCircle else Icons.Filled.CheckCircle,
                            enabled = total > 0,
                            modifier = Modifier.weight(1f),
                        )
                        SculptedOutlinedButton(
                            text = if (isExportingZip) "Exporting…" else "Export ZIP",
                            onClick = {
                                if (isExportingZip) return@SculptedOutlinedButton
                                isExportingZip = true
                                zipExportError = null
                                scope.launch {
                                    val ok = CollectionZipExporter.exportAndShare(
                                        context = context,
                                        collection = collection,
                                        captures = captures,
                                        startMillis = rangeStartMillis,
                                        endMillis = rangeEndMillis,
                                    )
                                    isExportingZip = false
                                    if (!ok) {
                                        zipExportError = if (rangeStartMillis != null || rangeEndMillis != null) {
                                            "No specimens in that date range."
                                        } else {
                                            "Could not export ZIP. Try again."
                                        }
                                    }
                                }
                            },
                            accent = Aqua,
                            textColor = Aqua,
                            icon = Icons.Filled.FolderZip,
                            enabled = !isExportingZip,
                            modifier = Modifier.weight(1f),
                        )
                        SculptedOutlinedButton(
                            text = if (isExportingPdf) "Generating…" else "Export PDF",
                            onClick = {
                                if (isExportingPdf) return@SculptedOutlinedButton
                                isExportingPdf = true
                                pdfExportError = null
                                scope.launch {
                                    val ok = CollectionPdfExporter.export(
                                        context = context,
                                        hunterName = profile.name.ifBlank { "Rock Scout" },
                                        collection = collection,
                                        captures = captures,
                                        startMillis = rangeStartMillis,
                                        endMillis = rangeEndMillis,
                                    )
                                    isExportingPdf = false
                                    if (ok == null) {
                                        pdfExportError = if (rangeStartMillis != null || rangeEndMillis != null) {
                                            "No specimens in that date range."
                                        } else {
                                            "Could not generate PDF. Try again."
                                        }
                                    }
                                }
                            },
                            accent = Citrine,
                            textColor = Citrine,
                            icon = Icons.Filled.PictureAsPdf,
                            enabled = !isExportingPdf && total > 0,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    // Count text and description now live on their own lines below the
                    // action row so the count can never collapse into vertical lettering.
                    Text(
                        "$total specimens in your cabinet.",
                        style = MaterialTheme.typography.labelMedium,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 2.dp),
                    )
                    Text(
                        "Specimens you've identified or bought and kept. Tap one to add notes, mark where you found it, or share it.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMid,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                    zipExportError?.let { err ->
                        Text(
                            err,
                            style = MaterialTheme.typography.labelMedium,
                            color = Citrine,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }
                    pdfExportError?.let { err ->
                        Text(
                            err,
                            style = MaterialTheme.typography.labelMedium,
                            color = Citrine,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }

                    // ── Date-range filter row for the ZIP export ──
                    // A pill-shaped control that opens the Material 3 date-range
                    // picker. Shows the current selection or a hint, with a clear
                    // button once a range is set.
                    val hasRange = rangeStartMillis != null || rangeEndMillis != null
                    val rangeLabel = when {
                        rangeStartMillis != null && rangeEndMillis != null ->
                            "${dateFormat.format(Date(rangeStartMillis!!))} – ${dateFormat.format(Date(rangeEndMillis!!))}"
                        rangeStartMillis != null ->
                            "From ${dateFormat.format(Date(rangeStartMillis!!))}"
                        rangeEndMillis != null ->
                            "Up to ${dateFormat.format(Date(rangeEndMillis!!))}"
                        else -> "All dates"
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SculptedOutlinedButton(
                            text = rangeLabel,
                            onClick = { showRangePicker = true },
                            accent = Aqua,
                            textColor = Aqua,
                            icon = Icons.Filled.CalendarMonth,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 14.dp, vertical = 8.dp,
                            ),
                        )
                        if (hasRange) {
                            SculptedOutlinedButton(
                                text = "Clear",
                                onClick = {
                                    rangeStartMillis = null
                                    rangeEndMillis = null
                                    zipExportError = null
                                },
                                accent = DarkTextMid,
                                textColor = DarkTextMid,
                                icon = Icons.Filled.Close,
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    horizontal = 14.dp, vertical = 8.dp,
                                ),
                            )
                        }
                        Text(
                            "ZIP & PDF exports use this range.",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextLow,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                    }

                    // ── Mass-delete action bar (only in selection mode) ──
                    if (selectionMode) {
                        val selectedCount = selectedSpecimenIds.value.size + selectedCaptureIds.value.size
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                "$selectedCount selected",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selectedCount > 0) Danger else TextMid,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f, fill = false),
                            )
                            SculptedOutlinedButton(
                                text = "All",
                                onClick = {
                                    selectedSpecimenIds.value = filteredCollection.map { it.specimenId }.toSet()
                                    selectedCaptureIds.value = captureInCollection.map { it.id }.toSet()
                                },
                                accent = Citrine,
                                textColor = Citrine,
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    horizontal = 14.dp, vertical = 8.dp,
                                ),
                            )
                            SculptedOutlinedButton(
                                text = "None",
                                onClick = {
                                    selectedSpecimenIds.value = emptySet()
                                    selectedCaptureIds.value = emptySet()
                                },
                                accent = DarkTextMid,
                                textColor = DarkTextMid,
                                enabled = selectedCount > 0,
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    horizontal = 14.dp, vertical = 8.dp,
                                ),
                            )
                            SculptedButton(
                                text = "Delete",
                                onClick = { showBulkDeleteDialog = true },
                                accent = Danger,
                                containerColor = Danger,
                                textColor = Color.White,
                                enabled = selectedCount > 0,
                                icon = Icons.Filled.DeleteSweep,
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    horizontal = 16.dp, vertical = 10.dp,
                                ),
                            )
                        }
                    }
                }

                // Field capture cards
                if (captureInCollection.isNotEmpty()) {
                    item {
                        Text(
                            "Field Captures",
                            style = MaterialTheme.typography.titleSmall,
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
                        )
                    }
                    items(captureInCollection.size) { index ->
                        val capture = captureInCollection[index]
                        val spec = SeedData.specimenById(capture.specimenId)
                        val accent = if (spec != null) rockClassColor(spec.rockClass) else Citrine
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
                            specimenName = spec?.name ?: "Unknown specimen",
                            accent = accent,
                            otherCaptures = others,
                            inCollection = true,
                            inWishlist = capture.inWishlist,
                            onToggleCollection = { repo.toggleCaptureInCollection(capture.id) },
                            onToggleWishlist = { repo.toggleCaptureInWishlist(capture.id) },
                            onSaveFields = { name, location, info ->
                                repo.updateCaptureFields(capture.id, name, location, info)
                            },
                            onAddImage = { uriStr -> repo.addImageToCapture(capture.id, uriStr) },
                            onMergeCapture = { otherId -> repo.mergeCaptures(capture.id, otherId) },
                            onRemoveImage = { idx -> repo.removeImageFromCapture(capture.id, idx) },
                            onDelete = { pendingDeleteCapture = capture },
                            onShare = {
                                scope.launch {
                                    val photo: android.graphics.Bitmap? =
                                        capture.imageUris.firstOrNull()?.let { uriStr ->
                                            ShareCardImage.loadDownsampled(context, Uri.parse(uriStr))
                                        }
                                    ShareCardImage.share(
                                        context = context,
                                        title = capture.displayName(spec?.name ?: "Unknown specimen"),
                                        subtitle = (spec?.category ?: "Field capture") + "  •  In my collection",
                                        body = capture.generalInfo.ifBlank { null },
                                        accentHex = if (spec != null) spec.colorHex else 0xFF2C6F9B,
                                        photoBitmap = photo,
                                        fileName = "rockscout_collection_capture_${capture.id}",
                                    )
                                }
                            },
                            onShareToProfile = { shareToProfileCapture = capture },
                            selectionMode = selectionMode,
                            isSelected = capture.id in selectedCaptureIds.value,
                            onToggleSelection = {
                                selectedCaptureIds.value = selectedCaptureIds.value.toMutableSet().apply {
                                    if (contains(capture.id)) remove(capture.id) else add(capture.id)
                                }.toSet()
                            },
                        )
                    }
                }

                // Database specimen cards
                if (filteredCollection.isNotEmpty()) {
                    if (captureInCollection.isNotEmpty()) {
                        item {
                            Text(
                                "Database Specimens",
                                style = MaterialTheme.typography.titleSmall,
                                color = Citrine,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
                            )
                        }
                    }
                    items(filteredCollection.size) { index ->
                        val entry = filteredCollection[index]
                        val spec = SeedData.specimenById(entry.specimenId) ?: return@items
                        val accent = rockClassColor(spec.rockClass)
                        val fieldCaptures = captures.filter { it.specimenId == entry.specimenId }
                        val isWishlisted = wishlist.contains(spec.id)
                        val isLiked = likedSpecimens.contains(spec.id)
                        val imageUrls = SpecimenImages.urls[spec.id] ?: spec.imageUrls
                        SpecimenListItem(
                            specimen = spec,
                            accent = accent,
                            onClick = { navController.navigate(Routes.specimen(spec.id)) },
                            onImageClick = {
                                viewerUrls = imageUrls
                                viewerInitialPage = 0
                            },
                            imageSize = 113.dp,
                            selectionMode = selectionMode,
                            isSelected = spec.id in selectedSpecimenIds.value,
                            onToggleSelection = {
                                selectedSpecimenIds.value = selectedSpecimenIds.value.toMutableSet().apply {
                                    if (contains(spec.id)) remove(spec.id) else add(spec.id)
                                }.toSet()
                            },
                            subtitle = {
                                if (entry.foundAt.isNotBlank()) {
                                    Icon(
                                        Icons.Filled.Place,
                                        contentDescription = null,
                                        tint = Aqua,
                                        modifier = Modifier.size(16.dp),
                                    )
                                    Text(
                                        entry.foundAt,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Aqua,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false),
                                    )
                                }
                                if (entry.note.isNotBlank()) {
                                    if (entry.foundAt.isNotBlank()) {
                                        Text("\u00b7 ", style = MaterialTheme.typography.bodyMedium, color = DarkTextMid)
                                    }
                                    Text(
                                        entry.note,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = DarkTextMid,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false),
                                    )
                                }
                                if (fieldCaptures.isNotEmpty()) {
                                    if (entry.foundAt.isNotBlank() || entry.note.isNotBlank()) {
                                        Text("\u00b7 ", style = MaterialTheme.typography.bodyMedium, color = DarkTextMid)
                                    }
                                    Text(
                                        "${fieldCaptures.size} capture${if (fieldCaptures.size > 1) "s" else ""}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Aqua,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            },
                            addShare = {
                                CompactAddShareButton(
                                    collected = true,
                                    wishlisted = isWishlisted,
                                    onCollectionToggle = { repo.toggleCollection(spec.id) },
                                    onWishlistToggle = { repo.toggleWishlist(spec.id) },
                                    onShareToProfile = { shareToProfileSpec = spec },
                                    onShareToSocial = {
                                        scope.launch {
                                            val photo: android.graphics.Bitmap? =
                                                imageUrls.firstOrNull()?.let { url ->
                                                    ShareCardImage.loadDownsampled(context, Uri.parse(url))
                                                }
                                            ShareCardImage.share(
                                                context = context,
                                                title = spec.name,
                                                subtitle = spec.category + "  •  " + spec.rarity + "  •  In my collection",
                                                body = entry.note.ifBlank { spec.tagline },
                                                accentHex = spec.colorHex,
                                                photoBitmap = photo,
                                                fileName = "rockscout_collection_${spec.id}",
                                            )
                                        }
                                    },
                                    accent = accent,
                                    size = 44.dp,
                                )
                            },
                            heart = {
                                CardHeart(
                                    active = isLiked,
                                    onToggle = { repo.toggleLike(spec.id) },
                                    accent = accent,
                                    size = 44.dp,
                                    likeCount = repo.specimenLikeCount(spec.id),
                                )
                            },
                            showCategory = true,
                            categoryLabel = shortCategoryLabel(spec.category),
                        )
                    }
                }
            }
        }
    }

    shareToProfileCapture?.let { cap ->
        val spec = SeedData.specimenById(cap.specimenId)
        ShareToProfileComposer(
            sourceType = "collection",
            title = cap.displayName(spec?.name ?: "Unknown specimen"),
            tagline = (spec?.category ?: "Field capture") + "  •  In my collection",
            imageUri = cap.imageUris.firstOrNull(),
            locationText = cap.customLocation,
            onDismiss = { shareToProfileCapture = null },
        )
    }

    shareToProfileSpec?.let { spec ->
        val imageUrls = SpecimenImages.urls[spec.id] ?: spec.imageUrls
        ShareToProfileComposer(
            sourceType = "collection",
            title = spec.name,
            tagline = spec.category + "  •  " + spec.rarity + "  •  In my collection",
            imageUri = imageUrls.firstOrNull(),
            locationText = spec.whereFound.firstOrNull() ?: "",
            onDismiss = { shareToProfileSpec = null },
        )
    }

    pendingDeleteCapture?.let { cap ->
        DeleteConfirmDialog(
            title = "Remove from collection?",
            message = "Remove this specimen from your collection? This action cannot be undone.",
            onConfirm = {
                repo.removeCapture(cap.id)
                pendingDeleteCapture = null
            },
            onDismiss = { pendingDeleteCapture = null },
        )
    }

    // Bulk-delete confirmation for the mass-selection flow.
    if (showBulkDeleteDialog) {
        val selectedCount = selectedSpecimenIds.value.size + selectedCaptureIds.value.size
        DeleteConfirmDialog(
            title = "Delete $selectedCount ${if (selectedCount == 1) "specimen" else "specimens"}?",
            message = "Remove the selected ${if (selectedCount == 1) "specimen" else "specimens"} from your collection? This action cannot be undone.",
            onConfirm = {
                repo.removeCollectionEntries(selectedSpecimenIds.value)
                repo.removeCaptures(selectedCaptureIds.value)
                clearSelection()
                showBulkDeleteDialog = false
            },
            onDismiss = { showBulkDeleteDialog = false },
        )
    }

    // Material 3 date-range picker dialog for the ZIP export filter.
    if (showRangePicker) {
        val rangeState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = rangeStartMillis,
            initialSelectedEndDateMillis = rangeEndMillis,
        )
        DatePickerDialog(
            onDismissRequest = { showRangePicker = false },
            colors = DatePickerDefaults.colors(
                containerColor = Color(0xFF1E1C16),
                titleContentColor = Citrine,
                headlineContentColor = Aqua,
                weekdayContentColor = Aqua,
                subheadContentColor = Citrine,
                yearContentColor = Aqua,
                currentYearContentColor = Citrine,
                selectedDayContainerColor = Citrine,
                selectedDayContentColor = Color(0xFF1C1A14),
                todayContentColor = Citrine,
                dayContentColor = DarkTextMid,
            ),
            confirmButton = {
                TextButton(
                    onClick = {
                        rangeStartMillis = rangeState.selectedStartDateMillis
                        rangeEndMillis = rangeState.selectedEndDateMillis
                        zipExportError = null
                        showRangePicker = false
                    },
                ) { Text("Apply", color = Citrine, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showRangePicker = false }) {
                    Text("Cancel", color = DarkTextMid)
                }
            },
        ) {
            DateRangePicker(
                state = rangeState,
                title = { Text("Filter exports by date", color = Citrine) },
                showModeToggle = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(560.dp)
                    .padding(16.dp),
            )
        }
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
fun EmptyState(emoji: String, title: String, message: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(96.dp).clip(CircleShape).background(Citrine.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) { Text(emoji, style = MaterialTheme.typography.displayMedium) }
        Spacer(Modifier.height(20.dp))
        Text(title, style = MaterialTheme.typography.headlineMedium, color = Aqua)
        Spacer(Modifier.height(8.dp))
        Text(message, style = MaterialTheme.typography.bodyLarge, color = Aqua, modifier = Modifier.padding(horizontal = 16.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

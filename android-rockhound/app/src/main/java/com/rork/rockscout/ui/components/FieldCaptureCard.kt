package com.rork.rockscout.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.rork.rockscout.ui.components.glowingBorder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.rork.rockscout.data.CapturedPhoto
import com.rork.rockscout.data.ImageModerator
import com.rork.rockscout.data.ModerationTriState
import com.rork.rockscout.data.ImageUtils
import com.rork.rockscout.data.ImageReviewRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.ProfanityFilter
import com.rork.rockscout.ui.components.SavedImagesPickerDialog
import com.rork.rockscout.ui.components.processSavedImage
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextLow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.components.sculpted

/**
 * Editable field capture card with name, location, general info fields,
 * multi-image support via "Add to Specimen" button, and collection/wishlist toggles.
 * Used in Captures, My Rocks, and Wishlist screens.
 */
/**
 * Other field captures available for merging into this card.
 * Only captures not already merged into this one are passed in.
 */
data class MergeableCapture(
    val id: String,
    val displayName: String,
    val thumbnailUri: String?,
    val emoji: String,
    val photoCount: Int,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldCaptureCard(
    capture: CapturedPhoto,
    specimenName: String,
    accent: Color,
    showCollectionToggle: Boolean = true,
    showWishlistToggle: Boolean = true,
    inCollection: Boolean = capture.inCollection,
    inWishlist: Boolean = capture.inWishlist,
    otherCaptures: List<MergeableCapture> = emptyList(),
    onToggleCollection: () -> Unit,
    onToggleWishlist: () -> Unit,
    onSaveFields: (name: String, location: String, info: String) -> Unit,
    onAddImage: (uriString: String) -> Unit,
    onMergeCapture: (otherId: String) -> Unit,
    onRemoveImage: (index: Int) -> Unit,
    onDelete: () -> Unit,
    onShare: (() -> Unit)? = null,
    onShareToProfile: (() -> Unit)? = null,
    selectionMode: Boolean = false,
    isSelected: Boolean = false,
    onToggleSelection: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault()) }
    var isEditing by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var editName by remember(capture.id) { mutableStateOf(capture.customName) }
    var editLocation by remember(capture.id) { mutableStateOf(capture.customLocation) }
    var editInfo by remember(capture.id) { mutableStateOf(capture.generalInfo) }

    // Full-screen image viewer state for the capture's photo gallery.
    var viewerOpen by remember { mutableStateOf(false) }
    var viewerPage by remember { mutableIntStateOf(0) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var imageModerating by remember { mutableStateOf(false) }
    var moderationRejected by remember { mutableStateOf<String?>(null) }
    var showSavedImagePicker by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Reject files larger than 5 MB before moderation to prevent
            // base64-encoding OOMs and failed uploads.
            if (ImageUtils.isOverUploadLimit(context, uri)) {
                moderationRejected = "That image is over 5 MB. Please choose a smaller photo."
                return@rememberLauncherForActivityResult
            }
            imageModerating = true
            moderationRejected = null
            coroutineScope.launch {
                val base64 = ImageUtils.uriToModerationBase64(context, uri)
                if (base64 == null) {
                    imageModerating = false
                    return@launch
                }
                val verdict = ImageModerator.scan(base64, "image/jpeg")
                imageModerating = false
                when (verdict.triState) {
                    ModerationTriState.CLEAN -> {
                        val persistentPath = ImageUtils.copyUriToInternalStorage(context, uri, "capture_images")
                        onAddImage(persistentPath ?: uri.toString())
                    }
                    ModerationTriState.EXPLICIT -> {
                        moderationRejected = verdict.reason.ifBlank {
                            "This photo can't be used because it contains content that violates our family-friendly policy."
                        }
                    }
                    ModerationTriState.QUESTIONABLE -> {
                        // Hold in pending state and submit to moderation queue
                        AppRepository.instance.setCapturePendingImage(capture.id, uri.toString())
                        val userId = AuthRepository.instance.currentUserId
                        val userName = AppRepository.instance.profile.value.name
                        val avatar = AppRepository.instance.profile.value.avatarEmoji
                        ImageReviewRepository.instance.submitReview(
                            userId = userId ?: "unknown",
                            userName = userName,
                            userAvatar = avatar,
                            imageUri = uri.toString(),
                            type = "field_capture",
                            captureId = capture.id,
                            reason = verdict.reason,
                        )
                        moderationRejected = "Your image is pending review. It'll be added once approved."
                    }
                }
            }
        }
    }

    var showMergePicker by remember { mutableStateOf(false) }
    val mergeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var pendingRemoveImageIdx by remember { mutableStateOf<Int?>(null) }

    val displayName = capture.displayName(specimenName)
    val displayLocation = capture.customLocation.ifBlank { "" }

    // Photo-removal confirmation dialog
    pendingRemoveImageIdx?.let { idx ->
        DeleteConfirmDialog(
            title = "Remove photo?",
            message = "Remove this photo from the capture? This action cannot be undone.",
            onConfirm = {
                onRemoveImage(idx)
                pendingRemoveImageIdx = null
            },
            onDismiss = { pendingRemoveImageIdx = null },
        )
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete capture?") },
            text = {
                Text(
                    "Are you sure you want to delete \"$displayName\"? " +
                        "This cannot be undone.",
                    color = TextLow,
                )
            },
            confirmButton = {
                SculptedTextButton(
                    text = "Delete",
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    accent = Danger,
                    textColor = Danger,
                    fontWeight = FontWeight.Bold,
                )
            },
            dismissButton = {
                SculptedTextButton(
                    text = "Cancel",
                    onClick = { showDeleteDialog = false },
                    accent = Aqua,
                    textColor = TextLow,
                )
            },
            containerColor = Color(0xFF2A2820),
            titleContentColor = Color.White,
            textContentColor = TextLow,
        )
    }

    DarkCard(
        accent = accent,
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (selectionMode && onToggleSelection != null) {
                    Modifier.clickable { onToggleSelection() }
                } else {
                    Modifier
                }
            ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
    ) {
        // Top row: checkbox (in selection mode) + thumbnail + info + action icons
        Row(verticalAlignment = Alignment.Top) {
            // Selection checkbox on the left when in selection mode
            if (selectionMode && onToggleSelection != null) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelection() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Citrine,
                        uncheckedColor = DarkTextLow,
                        checkmarkColor = Color(0xFF1C1A14),
                    ),
                    modifier = Modifier.size(32.dp),
                )
                Spacer(Modifier.width(8.dp))
            }
            Box(
                modifier = Modifier
                    .size(78.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(accent.copy(alpha = 0.35f), Color(0xFF1A1812))
                        )
                    )
                    .then(
                        if (capture.imageUris.isNotEmpty()) {
                            Modifier.clickable {
                                viewerPage = 0
                                viewerOpen = true
                            }
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (capture.imageUris.isNotEmpty()) {
                    AsyncImage(
                        model = capture.imageUris.first(),
                        contentDescription = displayName,
                        modifier = Modifier.size(78.dp),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Text(capture.specimenEmoji, style = MaterialTheme.typography.headlineMedium)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        displayName,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Spacer(Modifier.width(8.dp))
                    TagChip(
                        "${capture.confidence}% match",
                        color = if (capture.confidence >= 85) Success else Citrine,
                    )
                }
                Spacer(Modifier.height(4.dp))
                if (displayLocation.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = Aqua,
                            modifier = Modifier.size(13.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            displayLocation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Aqua,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                }
                if (capture.generalInfo.isNotBlank()) {
                    Text(
                        capture.generalInfo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(2.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        tint = DarkTextLow,
                        modifier = Modifier.size(13.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        dateFormat.format(Date(capture.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextLow,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (capture.imageUris.size > 1) {
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "${capture.imageUris.size} photos",
                            style = MaterialTheme.typography.labelSmall,
                            color = Aqua,
                        )
                    }
                }
            }
            // Action icons
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (showCollectionToggle) {
                    SculptedIconButton(
                        icon = Icons.Filled.Inventory2,
                        contentDescription = "Add to My Rocks",
                        onClick = onToggleCollection,
                        modifier = Modifier.size(36.dp),
                        accent = Citrine,
                        iconTint = if (inCollection) Success else DarkTextLow,
                        size = 36.dp,
                        shadowElevation = 3.dp,
                    )
                }
                if (showWishlistToggle) {
                    SculptedIconButton(
                        icon = if (inWishlist) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Add to Wishlist",
                        onClick = onToggleWishlist,
                        modifier = Modifier.size(36.dp),
                        accent = Aqua,
                        iconTint = if (inWishlist) Citrine else DarkTextLow,
                        size = 36.dp,
                        shadowElevation = 3.dp,
                    )
                }
                SculptedIconButton(
                    icon = Icons.Filled.Edit,
                    contentDescription = "Edit capture",
                    onClick = { isEditing = !isEditing },
                    modifier = Modifier.size(36.dp),
                    accent = Citrine,
                    iconTint = if (isEditing) accent else DarkTextLow,
                    size = 36.dp,
                    shadowElevation = 3.dp,
                )
                if (onShare != null) {
                    SculptedIconButton(
                        icon = Icons.Filled.Share,
                        contentDescription = "Share capture",
                        onClick = onShare,
                        modifier = Modifier.size(36.dp),
                        accent = Citrine,
                        iconTint = DarkTextLow,
                        size = 36.dp,
                        shadowElevation = 3.dp,
                    )
                }
                if (onShareToProfile != null) {
                    SculptedIconButton(
                        icon = Icons.Filled.PersonAdd,
                        contentDescription = "Share to Profile",
                        onClick = onShareToProfile,
                        modifier = Modifier.size(36.dp),
                        accent = Citrine,
                        iconTint = Citrine,
                        size = 36.dp,
                        shadowElevation = 3.dp,
                    )
                }
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Delete capture",
                    tint = DarkTextLow,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(onClick = { showDeleteDialog = true }),
                )
            }
        }

        // Image gallery row
        if (capture.imageUris.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                capture.imageUris.forEachIndexed { index, uriStr ->
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1A1812))
                            .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .then(
                                if (!isEditing) {
                                    Modifier.clickable {
                                        viewerPage = index
                                        viewerOpen = true
                                    }
                                } else {
                                    Modifier
                                }
                            ),
                    ) {
                        AsyncImage(
                            model = uriStr,
                            contentDescription = "Capture photo ${index + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                        if (isEditing) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(2.dp)
                                    .size(20.dp)
                                    .sculpted(
                                        shape = CircleShape,
                                        accent = Citrine,
                                        shadowElevation = 2.dp,
                                        circular = true,
                                        onClick = { pendingRemoveImageIdx = index },
                                    )
                                    .clip(CircleShape)
                                    .background(Color.Black),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "Remove photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        // Editable section
        AnimatedVisibility(
            visible = isEditing,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                EditableCaptureField(
                    label = "Specimen Name",
                    value = editName,
                    placeholder = specimenName,
                    onValueChange = { editName = ProfanityFilter.filter(it) },
                )
                Spacer(Modifier.height(8.dp))
                EditableCaptureField(
                    label = "Location",
                    value = editLocation,
                    placeholder = "e.g. Crater of Diamonds, AR",
                    onValueChange = { editLocation = ProfanityFilter.filter(it) },
                )
                Spacer(Modifier.height(8.dp))
                EditableCaptureField(
                    label = "General Info",
                    value = editInfo,
                    placeholder = "Notes about this find...",
                    onValueChange = { editInfo = ProfanityFilter.filter(it) },
                    singleLine = false,
                    minLines = 2,
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SculptedTextButton(
                        text = "Add to Specimen",
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f),
                        accent = accent,
                        textColor = accent,
                    )
                    SculptedTextButton(
                        text = "Save",
                        onClick = {
                            onSaveFields(editName, editLocation, editInfo)
                            isEditing = false
                        },
                        accent = Success,
                        textColor = Success,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        // Moderation loading / rejection feedback
        if (imageModerating) {
            Spacer(Modifier.height(8.dp))
            val modShape = RoundedCornerShape(10.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(modShape)
                    .background(accent.copy(alpha = 0.10f))
                    .glowingBorder(1.dp, accent.copy(alpha = 0.30f), modShape)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Checking image…",
                    style = MaterialTheme.typography.labelLarge,
                    color = accent,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        moderationRejected?.let { msg ->
            Spacer(Modifier.height(6.dp))
            val rejShape = RoundedCornerShape(10.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(rejShape)
                    .background(if (msg.contains("pending", ignoreCase = true)) Aqua.copy(alpha = 0.12f) else Danger.copy(alpha = 0.12f))
                    .glowingBorder(1.dp, (if (msg.contains("pending", ignoreCase = true)) Aqua else Danger).copy(alpha = 0.35f), rejShape)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    msg,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (msg.contains("pending", ignoreCase = true)) Aqua else Danger,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
        // Pending image hint on the capture
        if (capture.pendingImageUri != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                "\u23F3 1 image pending review",
                style = MaterialTheme.typography.labelSmall,
                color = Aqua,
                fontWeight = FontWeight.SemiBold,
            )
        }

        // Full-screen viewer for this capture's photos (pinch-to-zoom + pan enabled).
        if (viewerOpen && capture.imageUris.isNotEmpty()) {
            FullScreenImageViewer(
                imageUrls = capture.imageUris,
                initialPage = viewerPage,
                onDismiss = { viewerOpen = false },
            )
        }

        // "Add to Specimen" buttons when not editing
        if (!isEditing) {
            Spacer(Modifier.height(8.dp))
            val addShape = RoundedCornerShape(10.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(addShape)
                        .background(accent.copy(alpha = 0.10f))
                        .glowingBorder(1.dp, accent.copy(alpha = 0.30f), addShape)
                        .clickable(enabled = !imageModerating) { galleryLauncher.launch("image/*") }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Gallery",
                        style = MaterialTheme.typography.labelLarge,
                        color = accent,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(addShape)
                        .background(accent.copy(alpha = 0.10f))
                        .glowingBorder(1.dp, accent.copy(alpha = 0.30f), addShape)
                        .clickable(enabled = !imageModerating) { showSavedImagePicker = true }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Download, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Saved Images",
                        style = MaterialTheme.typography.labelLarge,
                        color = accent,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }

    if (showSavedImagePicker) {
        SavedImagesPickerDialog(
            onDismiss = { showSavedImagePicker = false },
            onImageSelected = { image ->
                showSavedImagePicker = false
                coroutineScope.launch {
                    imageModerating = true
                    moderationRejected = null
                    val path = processSavedImage(context, image, "capture_images", "field_capture")
                    imageModerating = false
                    if (path != null) onAddImage(path)
                }
            },
        )
    }
}

@Composable
fun EditableCaptureField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodySmall) },
        singleLine = singleLine,
        minLines = minLines,
        modifier = Modifier.fillMaxWidth().noAutoFocus(),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0xFF1A1812),
            unfocusedContainerColor = Color(0xFF1A1812),
            focusedIndicatorColor = Citrine,
            unfocusedIndicatorColor = Color(0xFF3A3830),
            focusedLabelColor = Citrine,
            unfocusedLabelColor = TextLow,
            cursorColor = Citrine,
        ),
        shape = RoundedCornerShape(10.dp),
    )
}

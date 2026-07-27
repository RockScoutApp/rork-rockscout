package com.rork.rockscout.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.rork.rockscout.data.CommunityRepository
import com.rork.rockscout.data.ImageModerator
import com.rork.rockscout.data.ImageUtils
import com.rork.rockscout.data.ModerationTriState
import com.rork.rockscout.data.ProfanityFilter
import com.rork.rockscout.data.ImageReviewRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.components.SavedImagesPickerDialog
import com.rork.rockscout.ui.components.processSavedImage
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Success
import kotlinx.coroutines.launch

/**
 * Bottom sheet composer for creating a community post. Lets the user attach
 * a photo from their gallery, write a title, tagline, description, and
 * optional location. On confirm, creates a post via [CommunityRepository]
 * with a 14-day expiry.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityPostComposer(
    onDismiss: () -> Unit,
    onPosted: () -> Unit,
    initialPhotoUri: String? = null,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var tagline by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var locationText by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf(initialPhotoUri) }
    var posting by remember { mutableStateOf(false) }
    var posted by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var imageModerating by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(CommunityRepository.Category.GENERAL) }
    var showSavedImagePicker by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri != null) {
            // Reject files larger than 5 MB before moderation to prevent
            // base64-encoding OOMs and failed uploads.
            if (ImageUtils.isOverUploadLimit(context, uri)) {
                error = "That image is over 5 MB. Please choose a smaller photo."
                return@rememberLauncherForActivityResult
            }
            imageModerating = true
            error = null
            scope.launch {
                val base64 = ImageUtils.uriToModerationBase64(context, uri)
                if (base64 == null) {
                    imageModerating = false
                    error = "Could not load image."
                    return@launch
                }
                val verdict = ImageModerator.scan(base64, "image/jpeg")
                imageModerating = false
                when (verdict.triState) {
                    ModerationTriState.CLEAN -> {
                        val persistentPath = ImageUtils.copyUriToInternalStorage(
                            context, uri, "community_posts",
                        )
                        photoUri = persistentPath ?: uri.toString()
                        error = null
                    }
                    ModerationTriState.EXPLICIT -> {
                        error = verdict.reason.ifBlank {
                            "This image can't be used because it violates our family-friendly policies."
                        }
                    }
                    ModerationTriState.QUESTIONABLE -> {
                        val persistentPath = ImageUtils.copyUriToInternalStorage(
                            context, uri, "community_posts",
                        )
                        val userId = AuthRepository.instance.currentUserId
                        val userName = AppRepository.instance.profile.value.name
                        val avatar = AppRepository.instance.profile.value.avatarEmoji
                        ImageReviewRepository.instance.submitReview(
                            userId = userId ?: "unknown",
                            userName = userName,
                            userAvatar = avatar,
                            imageUri = persistentPath ?: uri.toString(),
                            type = "community_post",
                            reason = verdict.reason,
                        )
                        photoUri = persistentPath ?: uri.toString()
                        error = null
                    }
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1E1C16),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
        ) {
            Text(
                "Create a Community Post",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Share a question, a find, or start a discussion with the RockScout community. Posts expire after 2 weeks.",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
            )
            Spacer(Modifier.height(16.dp))

            // Photo attach row
            Text(
                "Photo (optional)",
                style = MaterialTheme.typography.labelLarge,
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            if (photoUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1A1812))
                        .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                ) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Attached photo",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit,
                    )
                    // Remove photo button
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2A2820))
                            .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape)
                            .clickable { photoUri = null },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Remove photo",
                            tint = DarkTextHigh,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    // Gallery button
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2A2820))
                            .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .clickable { galleryLauncher.launch("image/*") }
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Filled.PhotoLibrary,
                            contentDescription = "Choose from gallery",
                            tint = Citrine,
                            modifier = Modifier.size(28.dp),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Gallery",
                            style = MaterialTheme.typography.labelMedium,
                            color = DarkTextHigh,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    // Saved images button
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2A2820))
                            .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .clickable { showSavedImagePicker = true }
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Filled.Download,
                            contentDescription = "Choose from saved images",
                            tint = Citrine,
                            modifier = Modifier.size(28.dp),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Saved Images",
                            style = MaterialTheme.typography.labelMedium,
                            color = DarkTextHigh,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Title field
            OutlinedTextField(
                value = title,
                onValueChange = { title = ProfanityFilter.filter(it) },
                modifier = Modifier.fillMaxWidth().noAutoFocus(),
                label = { Text("Title *", color = DarkTextMid) },
                placeholder = { Text("Ask a question or describe your find…", color = DarkTextMid) },
                singleLine = true,
                colors = communityTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
            )
            Spacer(Modifier.height(10.dp))

            // Tagline field
            OutlinedTextField(
                value = tagline,
                onValueChange = { tagline = ProfanityFilter.filter(it) },
                modifier = Modifier.fillMaxWidth().noAutoFocus(),
                label = { Text("Tagline (optional)", color = DarkTextMid) },
                placeholder = { Text("A short subtitle…", color = DarkTextMid) },
                singleLine = true,
                colors = communityTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
            )
            Spacer(Modifier.height(10.dp))

            // Description field
            OutlinedTextField(
                value = description,
                onValueChange = { description = ProfanityFilter.filter(it) },
                modifier = Modifier.fillMaxWidth().noAutoFocus(),
                label = { Text("Description *", color = DarkTextMid) },
                placeholder = { Text("Describe your issue, question, or find in detail…", color = DarkTextMid) },
                minLines = 3,
                maxLines = 6,
                colors = communityTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
            )
            Spacer(Modifier.height(10.dp))

            // Location field
            OutlinedTextField(
                value = locationText,
                onValueChange = { locationText = ProfanityFilter.filter(it) },
                modifier = Modifier.fillMaxWidth().noAutoFocus(),
                label = { Text("Location (optional)", color = DarkTextMid) },
                placeholder = { Text("Where was this? e.g. Mount Ida, AR", color = DarkTextMid) },
                singleLine = true,
                colors = communityTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
            )

            Spacer(Modifier.height(16.dp))

            // Category picker
            Text(
                "Category",
                style = MaterialTheme.typography.labelLarge,
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CommunityRepository.Category.entries.forEach { category ->
                    val isSelected = selectedCategory == category
                    val accent = if (isSelected) Citrine else DarkTextMid
                    val bg = if (isSelected) Citrine.copy(alpha = 0.22f) else Color(0xFF2A2820)
                    val borderAlpha = if (isSelected) 0.85f else 0.35f
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bg)
                            .glowingBorder(1.dp, Citrine.copy(alpha = borderAlpha), RoundedCornerShape(12.dp))
                            .clickable { selectedCategory = category }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            category.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = accent,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            // Image moderation loading indicator
            if (imageModerating) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Citrine,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Scanning image…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                    )
                }
            }

            // Error message
            if (error != null) {
                Spacer(Modifier.height(10.dp))
                Text(
                    error ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFE2574C),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Success message
            if (posted) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "Posted to the community board!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Success,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                if (posted) {
                    SculptedButton(
                        text = "Done",
                        onClick = onDismiss,
                        accent = Citrine,
                        containerColor = Citrine,
                        textColor = Ink,
                    )
                } else {
                    SculptedTextButton(
                        text = "Cancel",
                        onClick = {
                            scope.launch { sheetState.hide() }
                            onDismiss()
                        },
                        accent = Citrine,
                        textColor = DarkTextMid,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.width(8.dp))
                    SculptedButton(
                        text = if (posting) "Posting…" else "Post",
                        onClick = {
                            if (title.isBlank() || description.isBlank()) {
                                error = "Title and description are required."
                                return@SculptedButton
                            }
                            error = null
                            posting = true
                            scope.launch {
                                CommunityRepository.instance.createPost(
                                    title = title,
                                    tagline = tagline,
                                    imageUri = photoUri,
                                    description = description,
                                    locationText = locationText,
                                    category = selectedCategory,
                                ).onSuccess {
                                    posted = true
                                    onPosted()
                                }.onFailure {
                                    error = "Failed to post. Please try again."
                                }
                                posting = false
                            }
                        },
                        enabled = !posting,
                        accent = Citrine,
                        containerColor = Citrine,
                        textColor = Ink,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    if (showSavedImagePicker) {
        SavedImagesPickerDialog(
            onDismiss = { showSavedImagePicker = false },
            onImageSelected = { image ->
                showSavedImagePicker = false
                scope.launch {
                    imageModerating = true
                    error = null
                    val path = processSavedImage(context, image, "community_posts", "community_post")
                    imageModerating = false
                    if (path != null) {
                        photoUri = path
                        error = null
                    }
                }
            },
        )
    }
}

@Composable
private fun communityTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color(0xFF3A3830),
    unfocusedContainerColor = Color(0xFF3A3830),
    focusedTextColor = DarkTextHigh,
    unfocusedTextColor = DarkTextHigh,
    focusedIndicatorColor = Citrine,
    unfocusedIndicatorColor = Color(0xFF5A554A),
    cursorColor = Citrine,
)

package com.rork.rockscout.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.ImageModerator
import com.rork.rockscout.data.ImageUtils
import com.rork.rockscout.data.ModerationResult
import com.rork.rockscout.data.ModerationTriState
import com.rork.rockscout.data.ProfanityFilter
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.SpecimenSubmissionStore
import com.rork.rockscout.ui.components.SavedImagesPickerDialog
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.components.processSavedImage
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.TextLow
import java.util.UUID

/**
 * Full-screen dialog for submitting a specimen to the developer for review.
 * Users can upload up to 4 images, enter info text, and select a location.
 * The submission is saved to [SpecimenSubmissionStore] for dev review.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitSpecimenDialog(
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val repo = AppRepository.instance
    val profile = repo.profile.value

    val imageUris = remember { mutableStateListOf<String>() }
    var infoText by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf("") }
    var locationDropdownExpanded by remember { mutableStateOf(false) }
    var customLocation by remember { mutableStateOf("") }
    var useCustomLocation by remember { mutableStateOf(false) }
    var isModerating by remember { mutableStateOf(false) }
    var showSavedImagePicker by remember { mutableStateOf(false) }
    var pendingRemovePhotoIdx by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri == null || imageUris.size >= 4) return@rememberLauncherForActivityResult

        scope.launch {
            isModerating = true
            val base64 = ImageUtils.uriToModerationBase64(context, uri)
            val verdict = if (base64 != null) {
                ImageModerator.scan(base64)
            } else {
                ModerationResult(allowed = true)
            }
            isModerating = false

            if (verdict.triState == ModerationTriState.EXPLICIT) {
                Toast.makeText(context, verdict.reason, Toast.LENGTH_LONG).show()
                return@launch
            }

            val persistentPath = ImageUtils.copyUriToInternalStorage(
                context, uri, "specimen_submissions",
            )
            if (persistentPath != null) {
                imageUris.add(persistentPath)
                if (verdict.triState == ModerationTriState.QUESTIONABLE) {
                    val userId = com.rork.rockscout.data.AuthRepository.instance.currentUserId
                    val userName = com.rork.rockscout.data.AppRepository.instance.profile.value.name
                    val avatar = com.rork.rockscout.data.AppRepository.instance.profile.value.avatarEmoji
                    com.rork.rockscout.data.ImageReviewRepository.instance.submitReview(
                        userId = userId ?: "unknown",
                        userName = userName,
                        userAvatar = avatar,
                        imageUri = persistentPath,
                        type = "specimen_submission",
                        reason = verdict.reason,
                    )
                }
            } else {
                Toast.makeText(context, "Could not save image. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Unique US state/region names from SeedData locations
    val locations = remember {
        SeedData.allLocations.map { it.region }.distinct().sorted()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate900),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Header row with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Submit a Specimen",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Aqua,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    SculptedIconButton(
                        icon = Icons.Filled.Close,
                        contentDescription = "Close",
                        onClick = onDismiss,
                        accent = Aqua,
                        iconTint = DarkTextMid,
                        size = 40.dp,
                    )
                }

                // Helper text
                Text(
                    text = "Make sure the image is as clear and precise as possible, and include any information you have about it.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )

                // Upload area
                Text(
                    text = "Photos (up to 4)",
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    imageUris.forEachIndexed { idx, uri ->
                        Box(
                            modifier = Modifier.size(72.dp).clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1A1812))
                                .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Photo ${idx + 1}",
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop,
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black)
                                    .clickable { pendingRemovePhotoIdx = idx },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Filled.Close, "Remove", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                    if (imageUris.size < 4) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Aqua.copy(alpha = 0.12f))
                                    .glowingBorder(2.dp, Aqua.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                    .clickable { galleryLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Filled.Add, "Add photo", tint = Aqua, modifier = Modifier.size(24.dp))
                                    Text(
                                        "${imageUris.size}/4",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Aqua,
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Citrine.copy(alpha = 0.12f))
                                    .glowingBorder(2.dp, Citrine.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                    .clickable { showSavedImagePicker = true },
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Filled.Download, "Saved images", tint = Citrine, modifier = Modifier.size(24.dp))
                                    Text(
                                        "Saved",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Citrine,
                                    )
                                }
                            }
                        }
                    }
                }

                // Info text box
                OutlinedTextField(
                    value = infoText,
                    onValueChange = { infoText = ProfanityFilter.filter(it) },
                    label = { Text("Specimen information") },
                    placeholder = { Text("Name, description, what you know about it…") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp).noAutoFocus(),
                    minLines = 3,
                )

                // Location dropdown
                Text(
                    text = "Location",
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF2A2820))
                            .glowingBorder(1.dp, Aqua.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .clickable { locationDropdownExpanded = true }
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocationOn, null, tint = Aqua, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (useCustomLocation) {
                                    customLocation.ifBlank { "Enter location…" }
                                } else {
                                    selectedLocation.ifBlank { "Select a location…" }
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selectedLocation.isNotBlank() || customLocation.isNotBlank()) DarkTextHigh else DarkTextMid,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = locationDropdownExpanded,
                        onDismissRequest = { locationDropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(Color(0xFF1E1C16), RoundedCornerShape(10.dp)),
                    ) {
                        locations.forEach { region ->
                            DropdownMenuItem(
                                text = { Text(region, color = DarkTextHigh) },
                                onClick = {
                                    selectedLocation = region
                                    useCustomLocation = false
                                    locationDropdownExpanded = false
                                },
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Location not shown", color = Aqua, fontWeight = FontWeight.SemiBold) },
                            onClick = {
                                useCustomLocation = true
                                selectedLocation = ""
                                locationDropdownExpanded = false
                            },
                        )
                    }
                }
                if (useCustomLocation) {
                    OutlinedTextField(
                        value = customLocation,
                        onValueChange = { customLocation = ProfanityFilter.filter(it) },
                        label = { Text("Enter location") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().noAutoFocus(),
                    )
                }

                Spacer(Modifier.weight(1f))

                // Submit button — auth-gated (must be signed in to submit) and
                // requires a non-empty location whether picked or custom.
                val isSignedIn = com.rork.rockscout.data.AuthRepository.instance.currentUserId != null
                val locationValid = if (useCustomLocation) {
                    customLocation.trim().isNotBlank()
                } else {
                    selectedLocation.isNotBlank()
                }
                val canSubmit = imageUris.isNotEmpty() &&
                    infoText.isNotBlank() &&
                    locationValid &&
                    isSignedIn
                val finalLocation = if (useCustomLocation) customLocation.trim() else selectedLocation
                SculptedButton(
                    text = "Submit Specimen",
                    onClick = {
                        val submission = SpecimenSubmissionStore.SpecimenSubmission(
                            id = UUID.randomUUID().toString(),
                            submitterName = profile.name.ifBlank { "Anonymous" },
                            submitterId = com.rork.rockscout.data.AuthRepository.instance.currentUserId,
                            submitterAvatar = profile.avatarEmoji,
                            imageUris = imageUris.toList(),
                            infoText = ProfanityFilter.filter(infoText.trim()),
                            location = finalLocation,
                            submittedAt = System.currentTimeMillis(),
                        )
                        val result = SpecimenSubmissionStore.add(submission)
                        if (result.isSuccess) {
                            Toast.makeText(context, "Specimen submitted! Thank you.", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        } else {
                            Toast.makeText(
                                context,
                                "Could not save submission. Please try again.",
                                Toast.LENGTH_LONG,
                            ).show()
                        }
                    },
                    accent = Citrine,
                    containerColor = Citrine,
                    textColor = Ink,
                    icon = Icons.Filled.CloudUpload,
                    enabled = canSubmit,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                )
            }
        }
    }

    if (showSavedImagePicker) {
        SavedImagesPickerDialog(
            onDismiss = { showSavedImagePicker = false },
            onImageSelected = { image ->
                showSavedImagePicker = false
                if (imageUris.size < 4) {
                    scope.launch {
                        isModerating = true
                        val path = processSavedImage(context, image, "specimen_submissions", "specimen_submission")
                        isModerating = false
                        if (path != null) imageUris.add(path)
                    }
                }
            },
        )
    }

    pendingRemovePhotoIdx?.let { idx ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { pendingRemovePhotoIdx = null },
            title = { Text("Remove photo?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = { Text("Remove this photo from the submission?", color = DarkTextMid) },
            confirmButton = {
                SculptedTextButton(
                    text = "Remove",
                    onClick = {
                        if (idx in imageUris.indices) imageUris.removeAt(idx)
                        pendingRemovePhotoIdx = null
                    },
                    accent = Danger,
                    textColor = Danger,
                    fontWeight = FontWeight.Bold,
                )
            },
            dismissButton = {
                SculptedTextButton(
                    text = "Cancel",
                    onClick = { pendingRemovePhotoIdx = null },
                    accent = Citrine,
                    textColor = TextLow,
                )
            },
            containerColor = Color(0xFF1E1C16),
            titleContentColor = DarkTextHigh,
            textContentColor = DarkTextMid,
        )
    }
}

package com.rork.rockscout.ui.screens

import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.CustomDigLocationStore
import com.rork.rockscout.data.DigSiteSearchService
import com.rork.rockscout.data.ImageModerator
import com.rork.rockscout.data.ImageReviewRepository
import com.rork.rockscout.data.LocationSubmissionStore
import com.rork.rockscout.data.LocationType
import com.rork.rockscout.data.ProfanityFilter
import com.rork.rockscout.data.TripStopType
import com.rork.rockscout.ui.components.AddLocationSheet
import com.rork.rockscout.ui.components.SavedImagesPickerDialog
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.processSavedImage
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.TextLow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

private val locationTypes = listOf(
    "Public Dig Site" to LocationType.PUBLIC_DIG,
    "Mine" to LocationType.MINE,
    "Quarry" to LocationType.QUARRY,
    "Beach / Shore" to LocationType.BEACH,
    "River / Creek" to LocationType.RIVER,
    "Desert / Field" to LocationType.DESERT,
    "Rock Shop" to LocationType.ROCK_SHOP,
    "Metaphysical Shop" to LocationType.METAPHYSICAL,
    "Lapidary Club" to LocationType.LAPIDARY_CLUB,
)

private val campgroundTypes = listOf(
    "BLM Campground" to LocationType.PUBLIC_DIG,
    "State Park Campground" to LocationType.PUBLIC_DIG,
    "Private Campground" to LocationType.PUBLIC_DIG,
    "Dispersed Camping" to LocationType.PUBLIC_DIG,
    "National Forest Campground" to LocationType.PUBLIC_DIG,
    "RV Park" to LocationType.PUBLIC_DIG,
)

private val trailheadTypes = listOf(
    "BLM Trailhead" to LocationType.PUBLIC_DIG,
    "Forest Service Trailhead" to LocationType.PUBLIC_DIG,
    "Park Trailhead" to LocationType.PUBLIC_DIG,
    "Other Access Point" to LocationType.PUBLIC_DIG,
)

/**
 * Full-screen location submission form with photo capture, type dropdown,
 * address, comments, map pin-drop, web verification, and auto-approval.
 *
 * Replaces the simple pin-drop [AddLocationSheet] for the "Upload New Location"
 * action on the Dig Sites screen. The pin-drop map is still used to set
 * coordinates — it's embedded as a step within this form.
 *
 * When [submissionMode] is set to "campground" or "trailhead", the title,
 * subtitle, type dropdown options, and web verification call change to
 * match the appropriate location category.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLocationDialog(
    onDismiss: () -> Unit,
    onSubmitted: (approved: Boolean) -> Unit,
    preFilledCoords: Pair<Double, Double>? = null,
    submissionMode: String = "dig_site",
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = AppRepository.instance
    val profile by repo.profile.collectAsStateWithLifecycle()
    val auth = AuthRepository.instance
    val current by repo.currentLocation.collectAsStateWithLifecycle()

    val effectiveTypes = when (submissionMode) {
        "campground" -> campgroundTypes
        "trailhead" -> trailheadTypes
        else -> locationTypes
    }
    val dialogTitle = when (submissionMode) {
        "campground" -> "Upload New Campground"
        "trailhead" -> "Upload New Trailhead"
        else -> "Upload New Location"
    }
    val dialogSubtitle = when (submissionMode) {
        "campground" -> "Upload a campground for the community trip planner."
        "trailhead" -> "Upload a trailhead or access point for the community trip planner."
        else -> "Upload a dig site, mine, quarry, or rock shop for the community."
    }

    var name by remember { mutableStateOf("") }
    var selectedTypeIndex by remember { mutableStateOf(0) }
    var typeMenuExpanded by remember { mutableStateOf(false) }
    var address by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf("") }
    var pinLocation by remember { mutableStateOf<Pair<Double, Double>?>(preFilledCoords) }
    var showPinPicker by remember { mutableStateOf(false) }

    val photoUris = remember { mutableStateListOf<Uri>() }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var submitStatus by remember { mutableStateOf<String?>(null) }
    var moderationError by remember { mutableStateOf<String?>(null) }
    var showSavedImagePicker by remember { mutableStateOf(false) }
    var pendingRemovePhoto by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success && cameraUri != null) {
            val uri = cameraUri
            if (uri != null && photoUris.size < 4) {
                photoUris.add(uri)
            }
        }
        cameraUri = null
    }

    // Gallery picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri != null && photoUris.size < 4) {
            photoUris.add(uri)
        }
    }

    fun startCamera() {
        val photoFile = File(context.cacheDir, "photos/${UUID.randomUUID()}.jpg")
        photoFile.parentFile?.mkdirs()
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
        cameraUri = uri
        cameraLauncher.launch(uri)
    }

    ScreenScaffold(
        title = dialogTitle,
        onBack = { if (!isSubmitting) onDismiss() },
        actions = {
            SculptedIconButton(
                icon = Icons.Filled.Close,
                contentDescription = "Close",
                onClick = { if (!isSubmitting) onDismiss() },
                accent = Citrine,
                iconTint = Color.White,
                backgroundColor = Slate800,
                size = 40.dp,
                shadowElevation = 3.dp,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
            ) {
                Text(
                    dialogSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextLow,
                )
                Spacer(Modifier.height(16.dp))
                // ── Location name ──
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = ProfanityFilter.filter(it) },
                    label = { Text("Location name *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                )
                Spacer(Modifier.height(12.dp))

                // ── Location type dropdown ──
                ExposedDropdownMenuBox(
                    expanded = typeMenuExpanded,
                    onExpandedChange = { typeMenuExpanded = it },
                ) {
                    OutlinedTextField(
                        value = effectiveTypes[selectedTypeIndex].first,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor().noAutoFocus(),
                        singleLine = true,
                        label = { Text("Location type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded) },
                        shape = RoundedCornerShape(12.dp),
                    )
                    ExposedDropdownMenu(
                        expanded = typeMenuExpanded,
                        onDismissRequest = { typeMenuExpanded = false },
                    ) {
                        effectiveTypes.forEachIndexed { idx, (label, _) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedTypeIndex = idx
                                    typeMenuExpanded = false
                                },
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))

                // ── Address ──
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = ProfanityFilter.filter(it) },
                    label = { Text("Address or approximate location") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                )
                Spacer(Modifier.height(12.dp))

                // ── Comments ──
                OutlinedTextField(
                    value = comments,
                    onValueChange = { comments = ProfanityFilter.filter(it) },
                    label = { Text("Comments / additional info") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp)
                        .noAutoFocus(),
                    minLines = 3,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Default,
                    ),
                )
                Spacer(Modifier.height(12.dp))

                // ── Map pin-drop ──
                Text("Pin Location on Map", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                if (pinLocation != null) {
                    val loc = pinLocation!!
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Aqua, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "%.4f, %.4f".format(loc.first, loc.second),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Aqua,
                            fontWeight = FontWeight.Medium,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Tap to adjust",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextLow,
                            modifier = Modifier.clickable { showPinPicker = true },
                        )
                    }
                } else {
                    SculptedOutlinedButton(
                        text = "Drop a pin on the map",
                        onClick = { showPinPicker = true },
                        accent = Aqua,
                        textColor = Aqua,
                        icon = Icons.Filled.AddLocation,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(Modifier.height(12.dp))

                // ── Photos ──
                Text("Photos (up to 4)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(photoUris) { uri ->
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Slate800)
                                .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(10.dp)),
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(22.dp)
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .glowingBorder(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(11.dp))
                                    .clickable { pendingRemovePhoto = uri },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Filled.Close, contentDescription = "Remove photo", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                    if (photoUris.size < 4) {
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Column(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Slate800.copy(alpha = 0.5f))
                                        .glowingBorder(1.5.dp, Aqua.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .clickable { startCamera() },
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Icon(Icons.Filled.PhotoCamera, contentDescription = "Camera", tint = Aqua, modifier = Modifier.size(24.dp))
                                    Spacer(Modifier.height(2.dp))
                                    Text("Camera", style = MaterialTheme.typography.labelSmall, color = Aqua, fontWeight = FontWeight.Medium)
                                }
                                Column(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Slate800.copy(alpha = 0.5f))
                                        .glowingBorder(1.5.dp, Citrine.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .clickable { galleryLauncher.launch("image/*") },
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = "Gallery", tint = Citrine, modifier = Modifier.size(24.dp))
                                    Spacer(Modifier.height(2.dp))
                                    Text("Gallery", style = MaterialTheme.typography.labelSmall, color = Citrine, fontWeight = FontWeight.Medium)
                                }
                                Column(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Slate800.copy(alpha = 0.5f))
                                        .glowingBorder(1.5.dp, Citrine.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .clickable { showSavedImagePicker = true },
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Icon(Icons.Filled.Download, contentDescription = "Saved Images", tint = Citrine, modifier = Modifier.size(24.dp))
                                    Spacer(Modifier.height(2.dp))
                                    Text("Saved", style = MaterialTheme.typography.labelSmall, color = Citrine, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }

                // ── Status messages ──
                moderationError?.let { msg ->
                    Spacer(Modifier.height(10.dp))
                    Text(
                        msg,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF6B3D),
                        fontWeight = FontWeight.Medium,
                    )
                }
                submitStatus?.let { status ->
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CloudDone, contentDescription = null, tint = Aqua, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(status, style = MaterialTheme.typography.bodySmall, color = Aqua, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Bottom action bar — Cancel + Upload
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SculptedTextButton(
                    text = "Cancel",
                    onClick = { if (!isSubmitting) onDismiss() },
                    accent = Citrine,
                    textColor = Citrine,
                    modifier = Modifier.weight(1f),
                )
                SculptedButton(
                    text = if (isSubmitting) "Uploading…" else "Upload New Location",
                    onClick = {
                        if (isSubmitting) return@SculptedButton
                        if (name.isBlank()) return@SculptedButton
                        val coords = pinLocation
                        if (coords == null) return@SculptedButton

                        isSubmitting = true
                        moderationError = null
                        submitStatus = null
                        scope.launch {
                            // 1. Moderate photos if any
                            if (photoUris.isNotEmpty()) {
                                val moderationOk = withContext(Dispatchers.IO) {
                                    photoUris.all { uri ->
                                        runCatching {
                                            val bitmap = com.rork.rockscout.data.ImageUtils.decodeSampledBitmap(
                                                context, uri,
                                            ) ?: return@runCatching true
                                            val baos = java.io.ByteArrayOutputStream()
                                            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, baos)
                                            val base64 = android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.NO_WRAP)
                                            val result = ImageModerator.scan(base64, "image/jpeg")
                                            if (result.triState == com.rork.rockscout.data.ModerationTriState.EXPLICIT) {
                                                false
                                            } else {
                                                if (result.triState == com.rork.rockscout.data.ModerationTriState.QUESTIONABLE) {
                                                    // Allow but submit to the review queue
                                                    val persistentPath = runCatching {
                                                        com.rork.rockscout.data.ImageUtils.copyUriToInternalStorage(
                                                            context, uri, "location_submissions",
                                                        )
                                                    }.getOrNull()?.toString() ?: uri.toString()
                                                    ImageReviewRepository.instance.submitReview(
                                                        userId = auth.currentUserId ?: "unknown",
                                                        userName = profile.name.ifBlank { "Anonymous" },
                                                        userAvatar = profile.avatarEmoji,
                                                        imageUri = persistentPath,
                                                        type = "location_submission",
                                                        reason = result.reason,
                                                    )
                                                }
                                                true
                                            }
                                        }.getOrDefault(true)
                                    }
                                }
                                if (!moderationOk) {
                                    moderationError = "One or more photos were flagged by moderation and cannot be used. Please remove them and try again."
                                    isSubmitting = false
                                    return@launch
                                }
                            }

                            // 2. Copy photos to internal storage for persistence
                            val persistentUris = withContext(Dispatchers.IO) {
                                photoUris.map { uri ->
                                    runCatching {
                                        com.rork.rockscout.data.ImageUtils.copyUriToInternalStorage(
                                            context, uri, "location_submissions",
                                        )
                                    }.getOrNull()?.toString() ?: uri.toString()
                                }
                            }

                            // 3. Web verification (type-aware)
                            var webVerified = false
                            var webSnippet = ""
                            var webUrl = ""
                            withContext(Dispatchers.IO) {
                                webVerified = runCatching {
                                    when (submissionMode) {
                                        "campground" -> DigSiteSearchService.verifyCampground(name, coords.first, coords.second) { snippet, url ->
                                            webSnippet = snippet
                                            webUrl = url
                                        }
                                        "trailhead" -> DigSiteSearchService.verifyTrailhead(name, coords.first, coords.second) { snippet, url ->
                                            webSnippet = snippet
                                            webUrl = url
                                        }
                                        else -> DigSiteSearchService.verifyLocation(name, coords.first, coords.second) { snippet, url ->
                                            webSnippet = snippet
                                            webUrl = url
                                        }
                                    }
                                }.getOrDefault(false)
                            }

                            // 4. Create submission
                            val submission = LocationSubmissionStore.LocationSubmission(
                                id = "loc-sub-${UUID.randomUUID()}",
                                name = name.trim(),
                                type = effectiveTypes[selectedTypeIndex].first,
                                address = address.trim(),
                                comments = comments.trim(),
                                latitude = coords.first,
                                longitude = coords.second,
                                photoUris = persistentUris,
                                submitterName = profile.name.ifBlank { "Anonymous" },
                                submitterId = auth.currentUserId,
                                submittedAt = System.currentTimeMillis(),
                                webVerified = webVerified,
                                webSnippet = webSnippet,
                                webUrl = webUrl,
                                locationCategory = submissionMode,
                            )

                            runCatching {
                                if (webVerified) {
                                    // Auto-approve: add to CustomDigLocationStore immediately
                                    LocationSubmissionStore.add(submission.copy(status = "approved"))
                                    CustomDigLocationStore.addApprovedLocation(submission)
                                    submitStatus = "Web-verified and auto-approved! It's now on the map."
                                } else {
                                    // Send to Developer Console for manual review
                                    LocationSubmissionStore.add(submission)
                                    submitStatus = "Submitted for review. A developer will verify it shortly."
                                }
                            }.onFailure {
                                submitStatus = "Something went wrong saving your submission. Please try again."
                            }

                            isSubmitting = false
                            // Wait a moment to show the status, then close
                            kotlinx.coroutines.delay(1500)
                            onSubmitted(webVerified)
                        }
                    },
                    accent = Citrine,
                    containerColor = Citrine,
                    textColor = Color.Black,
                    modifier = Modifier.weight(1.5f),
                    enabled = !isSubmitting && name.isNotBlank() && pinLocation != null,
                )
            }
        }
    }

    // Pin picker map sheet
    if (showPinPicker) {
        AddLocationSheet(
            onDismiss = { showPinPicker = false },
            onPick = { pinName, lat, lng, _ ->
                if (name.isBlank()) name = pinName
                pinLocation = Pair(lat, lng)
                showPinPicker = false
            },
            defaultSubmitAsRockLocation = false,
        )
    }

    if (showSavedImagePicker) {
        SavedImagesPickerDialog(
            onDismiss = { showSavedImagePicker = false },
            onImageSelected = { image ->
                showSavedImagePicker = false
                scope.launch {
                    val path = processSavedImage(context, image, "location_submissions", "location_submission")
                    if (path != null) photoUris.add(Uri.parse(path))
                }
            },
        )
    }

    pendingRemovePhoto?.let { uri ->
        AlertDialog(
            onDismissRequest = { pendingRemovePhoto = null },
            title = { Text("Remove photo?", color = MaterialTheme.colorScheme.onSurface) },
            text = { Text("Remove this photo from the submission?", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                SculptedTextButton(
                    text = "Remove",
                    onClick = {
                        photoUris.remove(uri)
                        pendingRemovePhoto = null
                    },
                    accent = Danger,
                    textColor = Danger,
                    fontWeight = FontWeight.Bold,
                )
            },
            dismissButton = {
                SculptedTextButton(
                    text = "Cancel",
                    onClick = { pendingRemovePhoto = null },
                    accent = Citrine,
                    textColor = TextLow,
                )
            },
        )
    }
}



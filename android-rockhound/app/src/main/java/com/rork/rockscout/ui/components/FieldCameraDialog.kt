package com.rork.rockscout.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.CapturedPhoto
import com.rork.rockscout.data.GallerySaver
import com.rork.rockscout.data.IdentifyAccessManager
import com.rork.rockscout.data.ImageModerator
import com.rork.rockscout.data.ImageUtils
import com.rork.rockscout.data.ModerationTriState
import com.rork.rockscout.data.PurchaseManager
import com.rork.rockscout.data.ProfanityFilter
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.TextLow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * Save destination for a field camera photo.
 * Each destination routes the photo to the appropriate existing repository method,
 * and all destinations also create a field capture card so the photo appears on
 * the specimen marker map.
 */
enum class SaveDestination(val label: String) {
    FIELD_CAPTURES("Save to Field Captures"),
    SAVED_IMAGES("Save to My Saved Images"),
    MY_ROCKS("Save to My Rocks"),
    MY_WISHLIST("Save to My Wishlist"),
    FIELD_JOURNAL("Attach to Field Journal Entry"),
    SHARE_PROFILE("Share to Profile"),
    PROFILE_BACKGROUND("Change Profile Background"),
    SUBMIT_SPECIMEN("Submit a Specimen"),
}

/**
 * Full-page dialog that handles:
 * 1. Launching the device camera (no ID tool).
 * 2. Showing a photo preview with a "Save to…" dropdown.
 * 3. A full-page capture card form with Name, Location, Description inputs.
 * 4. A tap-to-pin map for dropping a pin at the collection/find spot.
 * 5. Routing the photo to the selected destination + creating a field capture card.
 *
 * Tapping the Field Camera tile on the Home screen shows this dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldCameraDialog(
    onDismiss: () -> Unit,
    onShareToProfile: (imageUri: String, name: String, location: String) -> Unit = { _, _, _ -> },
    onSubmitSpecimen: (imageUri: String) -> Unit = {},
    onOpenFieldJournal: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = AppRepository.instance
    val purchaseManager = PurchaseManager.instance
    val isPremium by purchaseManager.effectiveIsPremium.collectAsStateWithLifecycle()
    val accessManager = IdentifyAccessManager.instance
    val trialExpired by accessManager.trialExpired.collectAsStateWithLifecycle()
    val hasLocationUnlock by accessManager.hasLocationUnlock.collectAsStateWithLifecycle()
    val hasAdFreeUnlock by accessManager.hasAdFreeUnlock.collectAsStateWithLifecycle()
    // Free-tier camera: after the trial expires, only Saved Images is available
    // unless the user has a donated unlock or is Premium.
    val paidFeaturesUnlocked = isPremium || hasLocationUnlock || hasAdFreeUnlock
    val freeDestinationOnly = trialExpired && !paidFeaturesUnlocked

    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    var capturedUri by remember { mutableStateOf<Uri?>(null) }
    var capturedPath by remember { mutableStateOf<String?>(null) }
    var isModerating by remember { mutableStateOf(false) }
    var moderationError by remember { mutableStateOf<String?>(null) }

    // Form fields
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var pinLat by remember { mutableStateOf<Double?>(null) }
    var pinLng by remember { mutableStateOf<Double?>(null) }

    // Save dropdown
    var dropdownExpanded by remember { mutableStateOf(false) }
    var selectedDestination by remember { mutableStateOf<SaveDestination?>(null) }
    var showSaveForm by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var saveSuccess by remember { mutableStateOf(false) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraUri != null) {
            val uri = cameraUri!!
            scope.launch {
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        com.rork.rockscout.data.ImageUtils.decodeSampledBitmap(context, uri)
                    }
                    if (bitmap != null) {
                        // Save to gallery for persistent URI
                        val galleryUri = withContext(Dispatchers.IO) {
                            GallerySaver.saveBitmap(context.contentResolver, bitmap)
                        }
                        // Copy to internal storage for the field capture card
                        val persistentPath = withContext(Dispatchers.IO) {
                            ImageUtils.copyUriToInternalStorage(context, galleryUri ?: uri, "capture_images")
                        }
                        capturedUri = galleryUri ?: uri
                        capturedPath = persistentPath ?: (galleryUri ?: uri).toString()
                    }
                } catch (_: Exception) {
                    moderationError = "Failed to load the captured photo. Please try again."
                }
            }
        }
    }

    fun startCamera() {
        val photoFile = File(context.cacheDir, "photos/${UUID.randomUUID()}.jpg")
        photoFile.parentFile?.mkdirs()
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
        cameraUri = uri
        cameraLauncher.launch(uri)
    }

    // Auto-launch camera on first show
    DisposableEffect(Unit) {
        if (capturedUri == null) {
            startCamera()
        }
        onDispose { cameraUri = null }
    }

    // Moderation check before saving
    suspend fun moderateImage(uri: Uri): Boolean {
        val base64 = withContext(Dispatchers.IO) {
            ImageUtils.uriToModerationBase64(context, uri)
        } ?: return false
        val verdict = ImageModerator.scan(base64, "image/jpeg")
        return when (verdict.triState) {
            ModerationTriState.CLEAN -> true
            ModerationTriState.QUESTIONABLE -> {
                // Allow but submit to the review queue
                val persistentPath = capturedPath ?: uri.toString()
                val userId = com.rork.rockscout.data.AuthRepository.instance.currentUserId
                val userName = repo.profile.value.name
                val avatar = repo.profile.value.avatarEmoji
                com.rork.rockscout.data.ImageReviewRepository.instance.submitReview(
                    userId = userId ?: "unknown",
                    userName = userName,
                    userAvatar = avatar,
                    imageUri = persistentPath,
                    type = "field_camera",
                    reason = verdict.reason,
                )
                true
            }
            ModerationTriState.EXPLICIT -> {
                moderationError = "This photo can't be used because it contains content that violates our family-friendly policy."
                false
            }
        }
    }

    fun saveCapture() {
        val dest = selectedDestination ?: return
        val imageUri = capturedPath ?: capturedUri?.toString() ?: return
        isSaving = true
        scope.launch {
            // Moderate the image first
            val clean = moderateImage(capturedUri ?: Uri.parse(imageUri))
            if (!clean) {
                isSaving = false
                return@launch
            }

            // Always create a field capture card (so it appears on the specimen marker map)
            val captureId = UUID.randomUUID().toString()
            val capture = CapturedPhoto(
                id = captureId,
                specimenId = "field-camera",
                specimenEmoji = "\uD83D\uDCF7",
                confidence = 100,
                timestamp = System.currentTimeMillis(),
                customName = name,
                customLocation = location,
                generalInfo = description,
                imageUris = listOf(imageUri),
                inCollection = dest == SaveDestination.MY_ROCKS,
                inWishlist = dest == SaveDestination.MY_WISHLIST,
                latitude = pinLat,
                longitude = pinLng,
            )
            repo.addCapture(capture)

            // Route to the specific destination
            when (dest) {
                SaveDestination.FIELD_CAPTURES -> { /* already created above */ }
                SaveDestination.SAVED_IMAGES -> {
                    repo.addSavedImage(imageUri, imageUri)
                }
                SaveDestination.MY_ROCKS -> {
                    // The capture's inCollection flag is already set above
                }
                SaveDestination.MY_WISHLIST -> {
                    // The capture's inWishlist flag is already set above
                }
                SaveDestination.FIELD_JOURNAL -> {
                    onOpenFieldJournal()
                }
                SaveDestination.SHARE_PROFILE -> {
                    onShareToProfile(imageUri, name, location)
                }
                SaveDestination.PROFILE_BACKGROUND -> {
                    repo.setBackgroundImagePath(imageUri)
                }
                SaveDestination.SUBMIT_SPECIMEN -> {
                    onSubmitSpecimen(imageUri)
                }
            }

            isSaving = false
            saveSuccess = true
            // Auto-dismiss after a brief success state
            kotlinx.coroutines.delay(800)
            onDismiss()
        }
    }

    if (saveSuccess) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Color(0xFF1E1C16),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Citrine, modifier = Modifier.padding(end = 8.dp))
                    Text("Saved!", color = DarkTextHigh, fontWeight = FontWeight.Bold)
                }
            },
            text = { Text("Your field capture has been saved.", color = DarkTextMid) },
            confirmButton = {
                SculptedTextButton(text = "OK", onClick = onDismiss, accent = Citrine, textColor = Citrine)
            },
        )
        return
    }

    if (moderationError != null) {
        AlertDialog(
            onDismissRequest = { moderationError = null; if (capturedUri == null) onDismiss() },
            containerColor = Color(0xFF1E1C16),
            title = { Text("Photo Rejected", color = Danger, fontWeight = FontWeight.Bold) },
            text = { Text(moderationError ?: "", color = DarkTextMid) },
            confirmButton = {
                SculptedTextButton(
                    text = "OK",
                    onClick = {
                        moderationError = null
                        if (capturedUri == null) onDismiss() else startCamera()
                    },
                    accent = Citrine,
                    textColor = Citrine,
                )
            },
        )
        return
    }

    // Camera preview + Save-to dropdown (before the form opens)
    if (!showSaveForm && capturedUri != null) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(16.dp),
            ) {
                // Top bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Field Camera",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    SculptedIconButton(
                        icon = Icons.Filled.Close,
                        contentDescription = "Close",
                        onClick = onDismiss,
                        accent = Danger,
                        iconTint = Color.White,
                        backgroundColor = Slate800,
                        size = 40.dp,
                    )
                }
                Spacer(Modifier.height(12.dp))

                // Photo preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1A1812))
                        .glowingBorder(2.dp, Citrine.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                ) {
                    AsyncImage(
                        model = capturedUri,
                        contentDescription = "Captured photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                }
                Spacer(Modifier.height(12.dp))

                // Retake button
                SculptedOutlinedButton(
                    text = "Retake Photo",
                    onClick = { startCamera() },
                    accent = Aqua,
                    textColor = Aqua,
                    icon = Icons.Filled.PhotoCamera,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))

                // Save-to dropdown
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .sculpted(
                                shape = RoundedCornerShape(12.dp),
                                accent = Citrine,
                                shadowElevation = 5.dp,
                                onClick = { dropdownExpanded = true },
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate800)
                            .glowingBorder(2.dp, Citrine, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = Citrine, modifier = Modifier.padding(end = 8.dp))
                        Text(
                            "Save to…",
                            style = MaterialTheme.typography.titleMedium,
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                        )
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = Citrine)
                    }
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                    ) {
                        SaveDestination.entries.forEach { dest ->
                            // After the trial expires, free users can only save to
                            // Saved Images. Other destinations are paid.
                            val isPaidDestination = dest != SaveDestination.SAVED_IMAGES
                            val disabledForFreeUser = freeDestinationOnly && isPaidDestination
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(dest.label)
                                        if (disabledForFreeUser) {
                                            Spacer(Modifier.width(6.dp))
                                            Icon(
                                                Icons.Filled.Lock,
                                                contentDescription = null,
                                                tint = TextLow,
                                                modifier = Modifier.size(14.dp),
                                            )
                                        }
                                    }
                                },
                                enabled = !disabledForFreeUser,
                                onClick = {
                                    dropdownExpanded = false
                                    selectedDestination = dest
                                    showSaveForm = true
                                },
                            )
                        }
                    }
                }
            }
        }
        return
    }

    // Full-page save form
    if (showSaveForm && capturedUri != null && selectedDestination != null) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF12110D))
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(16.dp),
            ) {
                // Top bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SculptedIconButton(
                        icon = Icons.Filled.Close,
                        contentDescription = "Close",
                        onClick = onDismiss,
                        accent = Danger,
                        iconTint = Color.White,
                        backgroundColor = Slate800,
                        size = 36.dp,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        selectedDestination!!.label,
                        style = MaterialTheme.typography.titleLarge,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(12.dp))

                // Photo preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1A1812))
                        .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                ) {
                    AsyncImage(
                        model = capturedUri,
                        contentDescription = "Captured photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
                Spacer(Modifier.height(16.dp))

                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = ProfanityFilter.filter(it) },
                    label = { Text("Name") },
                    placeholder = { Text("e.g. Clear quartz point") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                )
                Spacer(Modifier.height(10.dp))

                // Location field
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = ProfanityFilter.filter(it) },
                    label = { Text("Location") },
                    placeholder = { Text("e.g. Crater of Diamonds, AR") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                )
                Spacer(Modifier.height(10.dp))

                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = ProfanityFilter.filter(it) },
                    label = { Text("Description") },
                    placeholder = { Text("Notes about this find…") },
                    minLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp)
                        .noAutoFocus(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Default,
                    ),
                )
                Spacer(Modifier.height(16.dp))

                // Pin-drop map
                Text(
                    "Drop a pin where you found this",
                    style = MaterialTheme.typography.titleSmall,
                    color = Aqua,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(8.dp))
                PinDropMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    initialZoom = 10.0,
                    accent = Citrine,
                    pinLabel = "Specimen pin",
                    onPinSet = { lat, lng ->
                        pinLat = lat
                        pinLng = lng
                    },
                    onPinRemoved = {
                        pinLat = null
                        pinLng = null
                    },
                )
                Spacer(Modifier.height(16.dp))

                // Save button
                SculptedButton(
                    text = if (isSaving) "Saving…" else "Save to ${selectedDestination!!.label.removePrefix("Save to ")}",
                    onClick = { if (!isSaving) saveCapture() },
                    accent = Citrine,
                    containerColor = Citrine,
                    textColor = Color.Black,
                    icon = Icons.Filled.Check,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving,
                )
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

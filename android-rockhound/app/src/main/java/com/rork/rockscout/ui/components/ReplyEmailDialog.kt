package com.rork.rockscout.ui.components

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.ImageModerator
import com.rork.rockscout.data.ImageUtils
import com.rork.rockscout.data.ModerationTriState
import coil3.compose.AsyncImage
import com.rork.rockscout.data.Museum
import com.rork.rockscout.data.SavedImage
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Danger
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

/**
 * Small dialog that lets the user enter or edit their reply-to email
 * address and optionally attach additional photos before launching the
 * pre-filled email draft to a museum.
 *
 * The original identification photo (if any) is included as the first
 * attachment. The user can upload additional images (up to 4 total) from
 * their gallery or Saved Images — useful when they've taken multiple
 * photos of the find from different angles.
 *
 * Pre-fills with the user's RockScout login email, falling back to
 * the last-used reply email persisted in SharedPreferences, or empty.
 * The user can change it freely — it's included in the email body so
 * the museum knows where to respond, and is independent of the email
 * account they'll send from.
 *
 * On "Continue", launches an [Intent.ACTION_SEND] (single attachment) or
 * [Intent.ACTION_SEND_MULTIPLE] (multiple attachments) with the pre-filled
 * subject, body, and photo attachment(s) via FileProvider.
 *
 * @param museum the museum to email
 * @param onDismiss called when the dialog is dismissed
 * @param artifactMatchNames top artifact match name(s) for the email body
 * @param artifactConfidences confidence percentage(s) for the email body
 * @param aiSummary the AI analysis summary for the email body
 * @param capturedBitmap the user's original ID photo to attach (optional)
 * @param userLocationText approximate location text (city/region, not exact coords)
 */
@Composable
fun ReplyEmailDialog(
    museum: Museum,
    onDismiss: () -> Unit,
    artifactMatchNames: List<String> = emptyList(),
    artifactConfidences: List<Int> = emptyList(),
    aiSummary: String = "",
    capturedBitmap: Bitmap? = null,
    userLocationText: String = "",
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val loginEmail = AuthRepository.instance.currentUserEmail ?: ""
    val prefs = context.getSharedPreferences("rockscout_prefs", 0)
    val lastUsedEmail = prefs.getString("last_reply_email", null) ?: loginEmail
    var replyEmail by remember { mutableStateOf(lastUsedEmail) }

    // --- Multi-photo attachment state ---
    val maxImages = 4
    val extraImageUris = remember { mutableStateListOf<Uri>() }
    var includeCapturedPhoto by remember { mutableStateOf(capturedBitmap != null) }
    var imageModerating by remember { mutableStateOf(false) }
    var moderationRejected by remember { mutableStateOf<String?>(null) }
    var showSavedImagePicker by remember { mutableStateOf(false) }

    val capturedCount = if (includeCapturedPhoto && capturedBitmap != null) 1 else 0
    val totalPhotos = capturedCount + extraImageUris.size
    val canAddMore = totalPhotos < maxImages

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        if (!canAddMore) return@rememberLauncherForActivityResult

        if (ImageUtils.isOverUploadLimit(context, uri)) {
            moderationRejected = "That image is over 5 MB. Please choose a smaller photo."
            return@rememberLauncherForActivityResult
        }

        imageModerating = true
        moderationRejected = null
        scope.launch {
            val base64 = ImageUtils.uriToModerationBase64(context, uri)
            if (base64 == null) {
                imageModerating = false
                return@launch
            }
            val verdict = ImageModerator.scan(base64)
            imageModerating = false

            if (verdict.triState == ModerationTriState.EXPLICIT) {
                moderationRejected = verdict.reason.ifBlank {
                    "This photo can't be used because it contains content that violates our family-friendly policy."
                }
                return@launch
            }

            val contentUri = copyToPhotosCache(context, uri)
            if (contentUri != null) {
                extraImageUris.add(contentUri)
            } else {
                Toast.makeText(context, "Could not save image. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (showSavedImagePicker) {
        SavedImagesPickerDialog(
            onDismiss = { showSavedImagePicker = false },
            onImageSelected = { savedImage ->
                showSavedImagePicker = false
                if (!canAddMore) return@SavedImagesPickerDialog

                imageModerating = true
                moderationRejected = null
                scope.launch {
                    val sourceUri = if (savedImage.localUri != null) {
                        Uri.parse(savedImage.localUri)
                    } else {
                        Uri.parse(savedImage.url)
                    }
                    val base64 = ImageUtils.uriToModerationBase64(context, sourceUri)
                    if (base64 == null) {
                        imageModerating = false
                        return@launch
                    }
                    val verdict = ImageModerator.scan(base64)
                    imageModerating = false

                    if (verdict.triState == ModerationTriState.EXPLICIT) {
                        moderationRejected = verdict.reason.ifBlank {
                            "This photo can't be used because it contains content that violates our family-friendly policy."
                        }
                        return@launch
                    }

                    val contentUri = copyToPhotosCache(context, sourceUri)
                    if (contentUri != null) {
                        extraImageUris.add(contentUri)
                    } else {
                        Toast.makeText(context, "Could not save image. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1C16),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Email,
                    contentDescription = null,
                    tint = Citrine,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text(
                    "Reply Email",
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        text = {
            Column {
                Text(
                    "So the museum can reply to you. You can change this to any email address.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = replyEmail,
                    onValueChange = { replyEmail = it },
                    label = { Text("Your reply-to email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                // --- Photo attachments section ---
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Photos to attach (${totalPhotos}/${maxImages})",
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "The original ID photo is included. Add more angles or detail shots — museums often need multiple views.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                )
                Spacer(Modifier.height(8.dp))

                // Thumbnail row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Original captured photo thumbnail
                    if (includeCapturedPhoto && capturedBitmap != null) {
                        AttachmentThumbnail(
                            modifier = Modifier.size(56.dp),
                            onRemove = { includeCapturedPhoto = false },
                        ) {
                            Image(
                                bitmap = capturedBitmap.asImageBitmap(),
                                contentDescription = "Original ID photo",
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }

                    // Extra uploaded photos (loaded async via Coil)
                    extraImageUris.forEachIndexed { idx, uri ->
                        AttachmentThumbnail(
                            modifier = Modifier.size(56.dp),
                            onRemove = { extraImageUris.removeAt(idx) },
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Photo ${idx + 2}",
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }

                    // Add-from-gallery + Saved-images buttons
                    if (canAddMore && !imageModerating) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Aqua.copy(alpha = 0.12f))
                                .glowingBorder(2.dp, Aqua.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .clickable { galleryLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Add, "Add photo", tint = Aqua, modifier = Modifier.size(20.dp))
                        }
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Citrine.copy(alpha = 0.12f))
                                .glowingBorder(2.dp, Citrine.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .clickable { showSavedImagePicker = true },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Download, "Saved images", tint = Citrine, modifier = Modifier.size(20.dp))
                        }
                    }

                    if (imageModerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 2.dp,
                            color = Aqua,
                        )
                    }
                }

                // Moderation error
                moderationRejected?.let { msg ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.bodySmall,
                        color = Danger,
                        textAlign = TextAlign.Start,
                    )
                }
            }
        },
        confirmButton = {
            SculptedTextButton(
                text = "Continue",
                onClick = {
                    prefs.edit().putString("last_reply_email", replyEmail).apply()

                    // Build the full attachment list
                    val attachmentUris = mutableListOf<Uri>()
                    if (includeCapturedPhoto && capturedBitmap != null) {
                        writeBitmapToPhotosCache(context, capturedBitmap)?.let { attachmentUris.add(it) }
                    }
                    attachmentUris.addAll(extraImageUris)

                    launchEmailDraft(
                        context = context,
                        museum = museum,
                        replyEmail = replyEmail,
                        artifactMatchNames = artifactMatchNames,
                        artifactConfidences = artifactConfidences,
                        aiSummary = aiSummary,
                        attachmentUris = attachmentUris,
                        userLocationText = userLocationText,
                    )
                    onDismiss()
                },
                accent = Citrine,
                textColor = Citrine,
            )
        },
        dismissButton = {
            SculptedTextButton(
                text = "Cancel",
                onClick = onDismiss,
                accent = DarkTextMid,
                textColor = DarkTextMid,
            )
        },
    )
}

/**
 * A single attachment thumbnail with a remove (×) button overlay.
 */
@Composable
private fun AttachmentThumbnail(
    modifier: Modifier = Modifier,
    onRemove: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1A1812)),
    ) {
        content()
        // Remove button
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(18.dp)
                .clip(CircleShape)
                .background(Color.Black)
                .clickable { onRemove() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Close, "Remove", tint = Color.White, modifier = Modifier.size(12.dp))
        }
    }
}

/**
 * Copies a content/file/http [uri] into the cache `photos/` directory
 * (configured in file_paths.xml as the `captured_photos` FileProvider path)
 * and returns the shareable [Uri] via FileProvider.
 *
 * Returns null on any I/O or FileProvider failure.
 */
private fun copyToPhotosCache(context: android.content.Context, uri: Uri): Uri? {
    return try {
        val scheme = uri.scheme
        val inputStream: java.io.InputStream? = if (scheme == "http" || scheme == "https") {
            java.net.URL(uri.toString()).openStream()
        } else {
            context.contentResolver.openInputStream(uri)
        }
        if (inputStream == null) return null

        val dir = File(context.cacheDir, "photos").apply { mkdirs() }
        val file = File(dir, "expert_extra_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            inputStream.use { it.copyTo(out) }
        }
        if (file.length() == 0L) return null
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
    } catch (_: Throwable) {
        null
    }
}

/**
 * Writes a [Bitmap] to the cache `photos/` directory and returns the
 * FileProvider [Uri] for sharing.
 */
private fun writeBitmapToPhotosCache(context: android.content.Context, bitmap: Bitmap): Uri? {
    return try {
        val dir = File(context.cacheDir, "photos").apply { mkdirs() }
        val file = File(dir, "expert_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
    } catch (_: Throwable) {
        null
    }
}

/**
 * Builds and launches the pre-filled email draft via Intent.ACTION_SEND
 * (single attachment) or ACTION_SEND_MULTIPLE (multiple attachments).
 *
 * The email includes:
 * - A note that the user used RockScout's AI identification tool
 * - The top artifact match name(s) and confidence percentage(s)
 * - The AI analysis summary
 * - A note about the attached photo(s)
 * - The user's reply-to email address
 * - The user's approximate location (privacy: city/region only)
 *
 * Photos are written to the `photos/` cache dir (matching file_paths.xml)
 * and shared via FileProvider.
 */
private fun launchEmailDraft(
    context: android.content.Context,
    museum: Museum,
    replyEmail: String,
    artifactMatchNames: List<String>,
    artifactConfidences: List<Int>,
    aiSummary: String,
    attachmentUris: List<Uri>,
    userLocationText: String,
) {
    val subject = "RockScout — Artifact Identification Assistance"
    val photoCount = attachmentUris.size

    val body = buildString {
        appendLine("Hello,")
        appendLine()
        appendLine("I used RockScout's AI artifact identification tool and the result was uncertain. I'm hoping you can help confirm what I've found.")
        appendLine()
        if (artifactMatchNames.isNotEmpty()) {
            appendLine("Top AI match(es):")
            artifactMatchNames.forEachIndexed { i, name ->
                val conf = artifactConfidences.getOrNull(i)
                appendLine("  • $name${if (conf != null) " — ${conf}% confidence" else ""}")
            }
            appendLine()
        }
        if (aiSummary.isNotBlank()) {
            appendLine("AI analysis summary:")
            appendLine(aiSummary)
            appendLine()
        }
        when {
            photoCount > 1 -> appendLine("I've attached $photoCount photos for your reference — different angles and detail shots of the find.")
            photoCount == 1 -> appendLine("I've attached the original photo for your reference.")
            else -> appendLine("Unfortunately I don't have a photo available at the moment, but I can provide more details if needed.")
        }
        appendLine()
        appendLine("You can reply to me at: $replyEmail")
        if (userLocationText.isNotBlank()) {
            appendLine("Approximate location: $userLocationText")
        }
        appendLine()
        appendLine("Thank you for your time and expertise.")
        appendLine()
        appendLine("— Sent from the RockScout app")
    }

    val hasAttachments = attachmentUris.isNotEmpty()

    val intent = if (hasAttachments && attachmentUris.size > 1) {
        // Multiple photos — use ACTION_SEND_MULTIPLE
        Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(attachmentUris))
        }
    } else {
        // Single or no attachment — use ACTION_SEND
        Intent(Intent.ACTION_SEND).apply {
            type = if (hasAttachments) "image/jpeg" else "message/rfc822"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (hasAttachments) {
                putExtra(Intent.EXTRA_STREAM, attachmentUris.first())
            }
        }
    }

    // Add recipient if museum has an email
    if (!museum.email.isNullOrBlank()) {
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(museum.email))
    }

    val chooser = Intent.createChooser(intent, "Send to museum expert").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching {
        context.startActivity(chooser)
    }.onFailure {
        Toast.makeText(
            context,
            "No email app found. Please send manually to ${museum.email ?: "the museum"}.",
            Toast.LENGTH_LONG,
        ).show()
    }
}

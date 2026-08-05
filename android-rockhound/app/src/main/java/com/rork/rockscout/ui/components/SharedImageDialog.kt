package com.rork.rockscout.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.ImageModerator
import com.rork.rockscout.data.ImageUtils
import com.rork.rockscout.data.ModerationTriState
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Confirmation dialog shown when an image is shared to RockScout from an
 * external app (email, Facebook, Messages, etc.). Shows a preview thumbnail,
 * runs image moderation for sexually explicit content, and lets the user
 * save the image to My Saved Images or cancel.
 */
@Composable
fun SharedImageDialog(
    imageUri: Uri,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var isSaving by remember { mutableStateOf(false) }
    var isModerating by remember { mutableStateOf(true) }
    var moderationRejected by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Run image moderation when the dialog opens
    LaunchedEffect(imageUri) {
        isModerating = true
        moderationRejected = null
        val base64 = withContext(Dispatchers.IO) {
            ImageUtils.uriToModerationBase64(context, imageUri)
        }
        val verdict = if (base64 != null) {
            ImageModerator.scan(base64)
        } else {
            com.rork.rockscout.data.ModerationResult(allowed = true)
        }
        isModerating = false
        if (verdict.triState == ModerationTriState.EXPLICIT) {
            moderationRejected = verdict.reason.ifBlank {
                "This image can't be saved because it contains content that violates our family-friendly policy."
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Save to My Saved Images?",
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E1C16)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isModerating) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Scanning image...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Citrine,
                            )
                        }
                    } else if (moderationRejected == null) {
                        AsyncImage(
                            model = imageUri.toString(),
                            contentDescription = "Shared image preview",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Fit,
                        )
                    } else {
                        Text(
                            text = moderationRejected ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFE2574C),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                if (moderationRejected == null) {
                    Text(
                        text = "This image will be saved to your Saved Images collection in RockScout.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        },
        confirmButton = {
            if (moderationRejected == null && !isModerating) {
                SculptedButton(
                    text = if (isSaving) "Saving..." else "Save",
                    onClick = {
                        if (isSaving) return@SculptedButton
                        isSaving = true
                        val repo = AppRepository.instance
                        scope.launch {
                            val persistentUrl = withContext(Dispatchers.IO) {
                                ImageUtils.copyUriToInternalStorage(context, imageUri, "shared_images")
                            }
                            if (persistentUrl != null) {
                                repo.addSavedImage(persistentUrl)
                            }
                            isSaving = false
                            onDismiss()
                        }
                    },
                    accent = Citrine,
                    containerColor = Citrine,
                    textColor = Color(0xFF1C1A14),
                    enabled = !isSaving,
                )
            }
        },
        dismissButton = {
            SculptedTextButton(
                text = if (moderationRejected != null) "Close" else "Cancel",
                onClick = onDismiss,
                accent = DarkTextMid,
                textColor = DarkTextMid,
            )
        },
        containerColor = Color(0xFF1E1C16),
        titleContentColor = DarkTextHigh,
        textContentColor = DarkTextMid,
    )
}

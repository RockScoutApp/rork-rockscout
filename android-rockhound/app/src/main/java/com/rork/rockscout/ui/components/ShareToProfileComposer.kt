package com.rork.rockscout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.rork.rockscout.data.PostRepository
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Success
import kotlinx.coroutines.launch
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.components.glowingBorder

/**
 * Composer dialog for sharing a card to the user's profile feed. Shows a
 * preview of the card snapshot + an optional caption field. On confirm,
 * creates a post via [PostRepository] (max 10 posts; oldest is archived).
 *
 * @param sourceType One of: capture, collection, wishlist, digsite, raa,
 * favoritespot, trip, journal.
 * @param title The specimen/item name.
 * @param tagline A short subtitle (specimen tagline, location name, etc).
 * @param imageUri Optional photo URI.
 * @param locationText Optional location label (redacted for non-friends).
 * @param onDismiss Called when the dialog is closed.
 */
/** Lightweight overload for sharing a standalone image (e.g. from a long-press)
 *  without any specimen or item context. */
@Composable
fun ShareToProfileComposer(
    imageUri: String?,
    onDismiss: () -> Unit,
) {
    ShareToProfileComposer(
        sourceType = "photo",
        title = "Shared Photo",
        tagline = "",
        imageUri = imageUri,
        locationText = "",
        onDismiss = onDismiss,
    )
}

@Composable
fun ShareToProfileComposer(
    sourceType: String,
    title: String,
    tagline: String,
    imageUri: String?,
    locationText: String,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var caption by remember { mutableStateOf("") }
    var posting by remember { mutableStateOf(false) }
    var posted by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = Color(0xFF1E1C16),
        titleContentColor = DarkTextHigh,
        textContentColor = DarkTextHigh,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Share to Profile", style = MaterialTheme.typography.headlineSmall, color = DarkTextHigh, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).imePadding()) {
                Text(
                    "This will appear in your profile feed. You can have up to 10 posts — the oldest is archived when you add a new one.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
                Spacer(Modifier.height(12.dp))
                // Preview card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF3A3830))
                        .glowingBorder(1.dp, Color(0xFF3A3830).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (imageUri != null) {
                        Box(
                            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1A1812))
                                .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                        ) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = title,
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop,
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, style = MaterialTheme.typography.titleSmall, color = DarkTextHigh, fontWeight = FontWeight.Bold, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                        if (tagline.isNotBlank()) {
                            Text(tagline, style = MaterialTheme.typography.labelSmall, color = DarkTextMid, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                        }
                        if (locationText.isNotBlank()) {
                            Text(locationText, style = MaterialTheme.typography.labelSmall, color = Aqua, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                    label = { Text("Caption (optional)", color = DarkTextMid) },
                    placeholder = { Text("Add a note…", color = DarkTextMid) },
                    maxLines = 3,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF3A3830),
                        unfocusedContainerColor = Color(0xFF3A3830),
                        focusedTextColor = DarkTextHigh,
                        unfocusedTextColor = DarkTextHigh,
                        focusedIndicatorColor = Citrine,
                        unfocusedIndicatorColor = Color(0xFF5A554A),
                        cursorColor = Citrine,
                    ),
                    shape = RoundedCornerShape(12.dp),
                )
                if (posted) {
                    Spacer(Modifier.height(10.dp))
                    Text("Posted to your profile!", style = MaterialTheme.typography.bodyMedium, color = Success, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            if (posted) {
                SculptedButton(
                    text = "Done",
                    onClick = onDismiss,
                    accent = Citrine,
                    containerColor = Citrine,
                    textColor = Ink,
                )
            } else {
                SculptedButton(
                    text = if (posting) "Posting…" else "Post to Profile",
                    onClick = {
                        posting = true
                        scope.launch {
                            PostRepository.instance.createPost(
                                sourceType = sourceType,
                                sourceRefId = null,
                                title = title,
                                tagline = tagline,
                                imageUri = imageUri,
                                caption = caption,
                                locationText = locationText,
                            ).onSuccess { posted = true }
                            posting = false
                        }
                    },
                    enabled = !posting,
                    accent = Citrine,
                    containerColor = Citrine,
                    textColor = Ink,
                )
            }
        },
        dismissButton = {
            if (!posted) {
                SculptedTextButton(
                    text = "Cancel",
                    onClick = onDismiss,
                    accent = Citrine,
                    textColor = DarkTextMid,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
    )
}

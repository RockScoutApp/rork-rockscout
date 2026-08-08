package com.rork.rockscout.ui.screens

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.IdentifyMatch
import com.rork.rockscout.data.SpecimenSubmissionStore
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate800
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

/** Data passed to the ReportIncorrectMatchDialog. */
data class ReportMatchInfo(
    val topMatchName: String,
    val topMatchConfidence: Int,
    val topMatchReasoning: String,
    val allMatchNames: List<String>,
    val allMatchConfidences: List<Int>,
    val isArtifact: Boolean,
)

/** Dialog for reporting an incorrect ID. Shows the AI's match info (read-only),
 *  an optional "I think this is actually" field, optional notes, and a thumbnail
 *  strip of the user's captured photos. Submits directly into the existing
 *  specimen submission pipeline via SpecimenSubmissionStore. */
@Composable
fun ReportIncorrectMatchDialog(
    context: Context,
    matchInfo: ReportMatchInfo,
    capturedBitmaps: List<Bitmap>,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var correctedName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    if (showSuccess) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(),
        ) {
            DarkCard(
                modifier = Modifier.fillMaxWidth(),
                accent = Citrine,
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "We're sorry we couldn't pin down the ID, but we'll do our best to try and figure it out! Thank you for helping to expand and improve RockScout!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                    Spacer(Modifier.height(20.dp))
                    SculptedButton(
                        text = "Close",
                        onClick = onDismiss,
                        accent = Citrine,
                        containerColor = Citrine,
                        textColor = Color.Black,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
        return
    }

    Dialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        DarkCard(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            accent = Color(0xFFE8A33D),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Report,
                        contentDescription = null,
                        tint = Citrine,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Report incorrect ID",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }

                // AI match info (read-only)
                DarkCard(
                    modifier = Modifier.fillMaxWidth(),
                    accent = Aqua.copy(alpha = 0.3f),
                    solidBackground = true,
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "AI suggested: ${matchInfo.topMatchName} (${matchInfo.topMatchConfidence}%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextMid,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Reasoning: ${matchInfo.topMatchReasoning}",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextLow,
                        )
                        if (matchInfo.allMatchNames.size > 1) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "All matches: " + matchInfo.allMatchNames.zip(matchInfo.allMatchConfidences).joinToString(", ") { (name, conf) -> "$name ($conf%)" },
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextLow,
                            )
                        }
                    }
                }

                // Photo thumbnail strip
                if (capturedBitmaps.isNotEmpty()) {
                    Text(
                        "Your photos (included in report):",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextLow,
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(capturedBitmaps.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Slate800)
                                    .glowingBorder(1.dp, Aqua.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                            ) {
                                Icon(
                                    Icons.Filled.Report,
                                    contentDescription = null,
                                    tint = Aqua.copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp).align(Alignment.Center),
                                )
                            }
                        }
                    }
                }

                // Corrected name field
                OutlinedTextField(
                    value = correctedName,
                    onValueChange = { correctedName = it },
                    label = { Text("I think this is actually:") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Citrine,
                        unfocusedBorderColor = Slate800,
                        focusedLabelColor = Citrine,
                        unfocusedLabelColor = DarkTextLow,
                        cursorColor = Citrine,
                    ),
                )
                Text(
                    "If you know what this is, tell us here! We review all submissions and may add it to the database for everyone.",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTextLow,
                )

                // Notes field
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Additional notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Citrine,
                        unfocusedBorderColor = Slate800,
                        focusedLabelColor = Citrine,
                        unfocusedLabelColor = DarkTextLow,
                        cursorColor = Citrine,
                    ),
                )

                // Submit button
                if (isSubmitting) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator(color = Citrine, modifier = Modifier.size(24.dp))
                    }
                } else {
                    SculptedButton(
                        text = "Submit Report",
                        onClick = {
                            isSubmitting = true
                            scope.launch {
                                val imagePaths = capturedBitmaps.mapNotNull { bitmap ->
                                    withContext(Dispatchers.IO) {
                                        ImageUtils.saveBitmapToInternal(context, bitmap)
                                    }
                                }

                                val profile = AppRepository.instance.profile.value
                                val userId = AuthRepository.instance.currentUserId
                                val submitterName = profile.name
                                val submitterAvatar = profile.avatarImagePath ?: ""

                                val effectiveName = correctedName.trim().ifBlank {
                                    "Unknown — AI suggested ${matchInfo.topMatchName}"
                                }

                                val infoText = buildString {
                                    append("AI suggested: ${matchInfo.topMatchName} (${matchInfo.topMatchConfidence}%). ")
                                    append("Reasoning: ${matchInfo.topMatchReasoning}. ")
                                    append("All matches: ${matchInfo.allMatchNames.zip(matchInfo.allMatchConfidences).joinToString(", ") { (n, c) -> "$n ($c%)" }}. ")
                                    if (notes.isNotBlank()) append("User notes: ${notes.trim()}.")
                                    append(" Report type: ${if (matchInfo.isArtifact) "Artifact" else "Rock"} ID.")
                                }

                                val submission = SpecimenSubmissionStore.SpecimenSubmission(
                                    id = UUID.randomUUID().toString(),
                                    submitterName = submitterName,
                                    submitterId = userId,
                                    submitterAvatar = submitterAvatar,
                                    imageUris = imagePaths,
                                    name = effectiveName,
                                    infoText = infoText,
                                    location = "",
                                    submittedAt = System.currentTimeMillis(),
                                    status = "pending",
                                )

                                SpecimenSubmissionStore.add(submission)
                                isSubmitting = false
                                showSuccess = true
                            }
                        },
                        accent = Citrine,
                        containerColor = Citrine,
                        textColor = Color.Black,
                        icon = Icons.Filled.Report,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // Cancel button
                if (!isSubmitting) {
                    SculptedButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        accent = Aqua,
                        containerColor = Slate800,
                        textColor = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}


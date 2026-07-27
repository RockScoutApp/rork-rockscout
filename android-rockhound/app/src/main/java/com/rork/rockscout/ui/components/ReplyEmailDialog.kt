package com.rork.rockscout.ui.components

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.Museum
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import java.io.File
import java.io.FileOutputStream

/**
 * Small dialog that lets the user enter or edit their reply-to email
 * address before launching the pre-filled email draft to a museum.
 *
 * Pre-fills with the user's RockScout login email, falling back to
 * the last-used reply email persisted in SharedPreferences, or empty.
 * The user can change it freely — it's included in the email body so
 * the museum knows where to respond, and is independent of the email
 * account they'll send from.
 *
 * On "Continue", launches an [Intent.ACTION_SEND] with the pre-filled
 * subject, body, and photo attachment (via FileProvider).
 *
 * @param museum the museum to email
 * @param onDismiss called when the dialog is dismissed
 * @param artifactMatchNames top artifact match name(s) for the email body
 * @param artifactConfidences confidence percentage(s) for the email body
 * @param aiSummary the AI analysis summary for the email body
 * @param capturedBitmap the user's photo to attach (optional)
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
    val loginEmail = AuthRepository.instance.currentUserEmail ?: ""
    val prefs = context.getSharedPreferences("rockscout_prefs", 0)
    val lastUsedEmail = prefs.getString("last_reply_email", null) ?: loginEmail
    var replyEmail by remember { mutableStateOf(lastUsedEmail) }

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
            }
        },
        confirmButton = {
            SculptedTextButton(
                text = "Continue",
                onClick = {
                    // Save the reply email for next time
                    prefs.edit().putString("last_reply_email", replyEmail).apply()

                    // Build the email draft
                    launchEmailDraft(
                        context = context,
                        museum = museum,
                        replyEmail = replyEmail,
                        artifactMatchNames = artifactMatchNames,
                        artifactConfidences = artifactConfidences,
                        aiSummary = aiSummary,
                        capturedBitmap = capturedBitmap,
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
 * Builds and launches the pre-filled email draft via Intent.ACTION_SEND.
 *
 * The email includes:
 * - A note that the user used RockScout's AI identification tool
 * - The top artifact match name(s) and confidence percentage(s)
 * - The AI analysis summary
 * - A note that the original photo is attached
 * - The user's reply-to email address
 * - The user's approximate location (privacy: city/region only)
 *
 * The photo is written to the captured_photos cache dir and shared
 * via FileProvider (same pattern as ReportScreenshotHelper).
 */
private fun launchEmailDraft(
    context: android.content.Context,
    museum: Museum,
    replyEmail: String,
    artifactMatchNames: List<String>,
    artifactConfidences: List<Int>,
    aiSummary: String,
    capturedBitmap: Bitmap?,
    userLocationText: String,
) {
    val subject = "RockScout — Artifact Identification Assistance"

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
        appendLine("I've attached the original photo for your reference.")
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

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "message/rfc822"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    // Add recipient if museum has an email
    if (!museum.email.isNullOrBlank()) {
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(museum.email))
    }

    // Attach photo if available
    if (capturedBitmap != null) {
        runCatching {
            val dir = File(context.cacheDir, "captured_photos").apply { mkdirs() }
            val file = File(dir, "expert_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file,
            )
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.type = "image/jpeg"
        }
    }

    val chooser = Intent.createChooser(intent, "Send to museum expert").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(chooser)
}

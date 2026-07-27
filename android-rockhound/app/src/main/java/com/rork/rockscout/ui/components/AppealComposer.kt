package com.rork.rockscout.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppealRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.ImageUtils
import com.rork.rockscout.data.ProfanityFilter
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate900
import kotlinx.coroutines.launch

private const val APPEAL_EMAIL = "RockScoutApp2026@yahoo.com"

/**
 * Full-screen modal composer for submitting an appeal (report/ban or account deletion).
 *
 * Users can write an explanation, attach an optional screenshot/image, and submit.
 * The appeal is saved via [AppealRepository] and a pre-filled email is launched.
 *
 * @param appealType   The type of appeal — e.g. "report_ban" or "account_deletion".
 * @param refId        Optional reference ID (e.g. report ID or ban ID).
 * @param title        Title displayed in the composer header.
 * @param onDismiss    Called when the composer is closed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppealComposer(
    appealType: String = "report_ban",
    refId: String? = null,
    title: String = "Appeal a Report or Ban",
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var appealText by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        // Reject files larger than 5 MB before copying to internal storage.
        if (ImageUtils.isOverUploadLimit(context, uri)) {
            Toast.makeText(
                context,
                "That image is over 5 MB. Please choose a smaller photo.",
                Toast.LENGTH_LONG,
            ).show()
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            val persistentPath = ImageUtils.copyUriToInternalStorage(
                context, uri, "appeal_images",
            )
            if (persistentPath != null) {
                imageUri = persistentPath
            } else {
                Toast.makeText(context, "Could not save image. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Gavel,
                            contentDescription = null,
                            tint = Danger,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Danger,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    SculptedIconButton(
                        icon = Icons.Filled.Close,
                        contentDescription = "Close",
                        onClick = onDismiss,
                        accent = Danger,
                        iconTint = DarkTextMid,
                        size = 40.dp,
                    )
                }

                // Helper text
                Text(
                    text = "Explain why you believe this report or ban is unjustified. Be specific and include any relevant context. You can attach a screenshot as evidence.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )

                // Appeal text input
                OutlinedTextField(
                    value = appealText,
                    onValueChange = { appealText = ProfanityFilter.filter(it) },
                    label = { Text("Your appeal") },
                    placeholder = { Text("Explain your side of the story…") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp)
                        .noAutoFocus(),
                    minLines = 4,
                )

                // Image attachment section
                Text(
                    text = "Evidence (optional)",
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (imageUri != null) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1A1812))
                                .glowingBorder(1.dp, Danger.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                        ) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Evidence",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop,
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black)
                                    .clickable { imageUri = null },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    "Remove",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp),
                                )
                            }
                        }
                    }
                    if (imageUri == null) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Danger.copy(alpha = 0.12f))
                                .glowingBorder(2.dp, Danger.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .clickable { galleryLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Filled.Add,
                                    "Attach image",
                                    tint = Danger,
                                    modifier = Modifier.size(24.dp),
                                )
                                Text(
                                    "Add",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Danger,
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                // Submit button — disabled until text is non-empty
                val canSubmit = appealText.trim().isNotBlank() && !isSubmitting
                SculptedButton(
                    text = if (isSubmitting) "Submitting…" else "Submit Appeal",
                    onClick = {
                        if (isSubmitting) return@SculptedButton
                        isSubmitting = true
                        scope.launch {
                            val userId = AuthRepository.instance.currentUserId ?: "unknown"
                            AppealRepository.instance.submitAppeal(
                                userId = userId,
                                type = appealType,
                                refId = refId,
                                reason = appealText.trim(),
                                imageUri = imageUri,
                            )

                            // Launch pre-filled email
                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$APPEAL_EMAIL")
                                putExtra(Intent.EXTRA_SUBJECT, "RockScout Appeal - $title")
                                putExtra(Intent.EXTRA_TEXT, appealText.trim())
                            }
                            SafeLinkOpener.openShareChooser(context, emailIntent, "Send appeal email")

                            Toast.makeText(
                                context,
                                "Appeal submitted. Thank you.",
                                Toast.LENGTH_SHORT,
                            ).show()
                            isSubmitting = false
                            onDismiss()
                        }
                    },
                    accent = Danger,
                    containerColor = Danger,
                    textColor = Color.White,
                    icon = Icons.Filled.CloudUpload,
                    enabled = canSubmit,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                )
            }
        }
    }
}

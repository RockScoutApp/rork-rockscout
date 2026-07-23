package com.rork.rockscout.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalActivity
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.CustomGemShowStore
import com.rork.rockscout.data.GemShow
import com.rork.rockscout.data.GemShowSearchService
import com.rork.rockscout.data.GemShowSubmissionStore
import com.rork.rockscout.data.ProfanityFilter
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.TextLow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

private val states = listOf(
    "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY",
)

private val months = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December", "Various",
)

/**
 * Full-screen dialog for users to submit a gem, mineral, or fossil show.
 *
 * The submission is web-verified via [GemShowSearchService]. If verification
 * succeeds, the show is immediately added to the public list through
 * [CustomGemShowStore]. If it fails, the submission is queued in
 * [GemShowSubmissionStore] for developer review.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitShowDialog(
    onDismiss: () -> Unit,
    onSubmitted: (approved: Boolean) -> Unit,
) {
    val repo = AppRepository.instance
    val profile by repo.profile.collectAsStateWithLifecycle()
    val auth = AuthRepository.instance
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var stateIndex by remember { mutableStateOf(0) }
    var stateMenuExpanded by remember { mutableStateOf(false) }
    var monthIndex by remember { mutableStateOf(0) }
    var monthMenuExpanded by remember { mutableStateOf(false) }
    var website by remember { mutableStateOf("") }
    var dateRange by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var isSubmitting by remember { mutableStateOf(false) }
    var submitStatus by remember { mutableStateOf<String?>(null) }
    var submitError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnBackPress = !isSubmitting,
            dismissOnClickOutside = false,
        ),
        title = {
            Column {
                Text("Submit a Show", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(2.dp))
                Text(
                    "Add a gem, mineral, or fossil show to the list. We'll verify it online before publishing.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextLow,
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
            ) {
                // Show name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = ProfanityFilter.filter(it) },
                    label = { Text("Show name *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                )
                Spacer(Modifier.height(12.dp))

                // City
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = ProfanityFilter.filter(it) },
                    label = { Text("City *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                )
                Spacer(Modifier.height(12.dp))

                // State dropdown
                ExposedDropdownMenuBox(
                    expanded = stateMenuExpanded,
                    onExpandedChange = { stateMenuExpanded = it },
                ) {
                    OutlinedTextField(
                        value = states[stateIndex],
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor().noAutoFocus(),
                        singleLine = true,
                        label = { Text("State *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateMenuExpanded) },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    )
                    ExposedDropdownMenu(
                        expanded = stateMenuExpanded,
                        onDismissRequest = { stateMenuExpanded = false },
                    ) {
                        states.forEachIndexed { idx, label ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    stateIndex = idx
                                    stateMenuExpanded = false
                                },
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))

                // Month dropdown
                ExposedDropdownMenuBox(
                    expanded = monthMenuExpanded,
                    onExpandedChange = { monthMenuExpanded = it },
                ) {
                    OutlinedTextField(
                        value = months[monthIndex],
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor().noAutoFocus(),
                        singleLine = true,
                        label = { Text("Month *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthMenuExpanded) },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    )
                    ExposedDropdownMenu(
                        expanded = monthMenuExpanded,
                        onDismissRequest = { monthMenuExpanded = false },
                    ) {
                        months.forEachIndexed { idx, label ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    monthIndex = idx
                                    monthMenuExpanded = false
                                },
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))

                // Website / Facebook URL
                OutlinedTextField(
                    value = website,
                    onValueChange = { website = ProfanityFilter.filter(it) },
                    label = { Text("Website or Facebook URL *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                )
                Spacer(Modifier.height(12.dp))

                // Date range
                OutlinedTextField(
                    value = dateRange,
                    onValueChange = { dateRange = ProfanityFilter.filter(it) },
                    label = { Text("Approximate date range") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                )
                Spacer(Modifier.height(12.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = ProfanityFilter.filter(it) },
                    label = { Text("Description / what to expect") },
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                    minLines = 3,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Default,
                    ),
                )

                // Status messages
                submitError?.let { msg ->
                    Spacer(Modifier.height(10.dp))
                    Text(msg, style = MaterialTheme.typography.bodySmall, color = Color(0xFFFF6B3D), fontWeight = FontWeight.Medium)
                }
                submitStatus?.let { status ->
                    Spacer(Modifier.height(10.dp))
                    Text(status, style = MaterialTheme.typography.bodySmall, color = Citrine, fontWeight = FontWeight.Medium)
                }
            }
        },
        confirmButton = {
            SculptedButton(
                text = if (isSubmitting) "Verifying…" else "Submit Show",
                onClick = {
                    if (isSubmitting) return@SculptedButton
                    if (name.isBlank() || city.isBlank() || website.isBlank()) {
                        submitError = "Show name, city, and website are required."
                        return@SculptedButton
                    }

                    isSubmitting = true
                    submitError = null
                    submitStatus = null

                    scope.launch {
                        var webVerified = false
                        var webSnippet = ""
                        var webUrl = ""

                        withContext(Dispatchers.IO) {
                            webVerified = runCatching {
                                GemShowSearchService.verifyShow(
                                    name = name.trim(),
                                    city = city.trim(),
                                    state = states[stateIndex],
                                    website = website.trim(),
                                ) { snippet, url ->
                                    webSnippet = snippet
                                    webUrl = url
                                }
                            }.getOrDefault(false)
                        }

                        val submission = GemShowSubmissionStore.GemShowSubmission(
                            id = "show-sub-${UUID.randomUUID()}",
                            name = name.trim(),
                            city = city.trim(),
                            state = states[stateIndex],
                            month = months[monthIndex],
                            website = website.trim(),
                            dateRange = dateRange.trim(),
                            description = description.trim(),
                            submitterName = profile.name.ifBlank { "Anonymous" },
                            submitterId = auth.currentUserId,
                            submittedAt = System.currentTimeMillis(),
                            status = if (webVerified) "approved" else "pending",
                            webVerified = webVerified,
                            webSnippet = webSnippet,
                            webUrl = webUrl,
                        )

                        if (webVerified) {
                            GemShowSubmissionStore.add(submission)
                            CustomGemShowStore.addApprovedShow(submission)
                            submitStatus = "Verified and added! The show is now in the list."
                        } else {
                            GemShowSubmissionStore.add(submission)
                            submitStatus = "Submitted for review. A developer will verify it shortly."
                        }

                        isSubmitting = false
                        kotlinx.coroutines.delay(1500)
                        onSubmitted(webVerified)
                    }
                },
                accent = Citrine,
                containerColor = Citrine,
                textColor = Color.Black,
                enabled = !isSubmitting && name.isNotBlank() && city.isNotBlank() && website.isNotBlank(),
            )
        },
        dismissButton = {
            SculptedTextButton(
                text = "Cancel",
                onClick = { if (!isSubmitting) onDismiss() },
                accent = Citrine,
                textColor = Citrine,
            )
        },
    )
}

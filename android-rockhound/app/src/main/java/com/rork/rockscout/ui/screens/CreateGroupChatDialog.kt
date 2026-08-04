package com.rork.rockscout.ui.screens

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.rockscout.data.SupabaseMessagingRepository
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.Slate900
import kotlinx.coroutines.launch

/**
 * Full-screen dialog for creating a new group chat.
 *
 * Fields: name (required), subject, max members (optional cap),
 * profanity filter level (normal/strict), header image URL (optional),
 * scroll speed setting (normal/half/stop).
 */
@Composable
fun CreateGroupChatDialog(
    onDismiss: () -> Unit,
    onCreated: (String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var maxMembersStr by remember { mutableStateOf("") }
    var profanityLevel by remember { mutableStateOf("normal") }
    var headerImageUrl by remember { mutableStateOf("") }
    var scrollSpeed by remember { mutableStateOf("normal") }
    var isCreating by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val canSubmit = name.isNotBlank() && !isCreating

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(20.dp),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Start a New Group Chat",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3A3830))
                        .glowingBorder(1.dp, Color(0xFF3A3830).copy(alpha = 0.35f), CircleShape)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = DarkTextHigh, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(20.dp))

            // Name field (required)
            Text("Group Chat Name *", style = MaterialTheme.typography.labelLarge, color = Citrine, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Pacific Northwest Rockhounds", color = DarkTextMid) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = inputColors(),
            )

            Spacer(Modifier.height(16.dp))

            // Subject field
            Text("Subject Matter", style = MaterialTheme.typography.labelLarge, color = Citrine, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Agate hunting, gem cutting, field trips", color = DarkTextMid) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = inputColors(),
            )

            Spacer(Modifier.height(16.dp))

            // Max members
            Text("Max Users Allowed", style = MaterialTheme.typography.labelLarge, color = Citrine, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Leave empty for no cap on total members.", style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = maxMembersStr,
                onValueChange = { maxMembersStr = it.filter { c -> c.isDigit() } },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. 50 (0 or empty = no cap)", color = DarkTextMid) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = inputColors(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                ),
            )

            Spacer(Modifier.height(20.dp))

            // Profanity filter level
            Text("Profanity Filter", style = MaterialTheme.typography.labelLarge, color = Citrine, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FilterOption(
                    label = "Normal",
                    description = "Catches explicit words only",
                    selected = profanityLevel == "normal",
                    onSelect = { profanityLevel = "normal" },
                    accent = Citrine,
                )
                FilterOption(
                    label = "Extra Strict",
                    description = "Also catches mild profanity",
                    selected = profanityLevel == "strict",
                    onSelect = { profanityLevel = "strict" },
                    accent = Danger,
                )
            }

            Spacer(Modifier.height(20.dp))

            // Scroll speed control
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Speed, contentDescription = null, tint = Aqua, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Scroll Speed Control", style = MaterialTheme.typography.labelLarge, color = Aqua, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Text("Sets the default. Each user can override on their own screen.", style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ScrollOption(
                    label = "Normal",
                    selected = scrollSpeed == "normal",
                    onSelect = { scrollSpeed = "normal" },
                )
                ScrollOption(
                    label = "Half Speed",
                    selected = scrollSpeed == "half",
                    onSelect = { scrollSpeed = "half" },
                )
                ScrollOption(
                    label = "Stop Scroll",
                    selected = scrollSpeed == "stop",
                    onSelect = { scrollSpeed = "stop" },
                )
            }

            Spacer(Modifier.height(20.dp))

            // Header image URL
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Image, contentDescription = null, tint = Aqua, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Header Image URL (optional)", style = MaterialTheme.typography.labelLarge, color = Aqua, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = headerImageUrl,
                onValueChange = { headerImageUrl = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://… (image shown at top of chat)", color = DarkTextMid) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = inputColors(),
            )

            Spacer(Modifier.height(24.dp))

            // Error
            error?.let { err ->
                Text(err, style = MaterialTheme.typography.bodyMedium, color = Danger, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
            }

            // Create button
            SculptedButton(
                text = if (isCreating) "Creating…" else "Create Group Chat",
                onClick = {
                    if (!canSubmit) return@SculptedButton
                    isCreating = true
                    error = null
                    val maxMembers = maxMembersStr.trim().toIntOrNull()?.takeIf { it > 0 }
                    scope.launch {
                        SupabaseMessagingRepository.createGroupChat(
                            name = name.trim(),
                            subject = subject.trim(),
                            maxMembers = maxMembers,
                            profanityFilterLevel = profanityLevel,
                            headerImageUrl = headerImageUrl.trim().ifBlank { null },
                            scrollSpeedSetting = scrollSpeed,
                        ).onSuccess { chatId ->
                            onCreated(chatId)
                        }.onFailure { e ->
                            error = e.message ?: "Failed to create group chat"
                            isCreating = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                accent = Aqua,
                containerColor = Aqua,
                textColor = Ink,
            )
            Spacer(Modifier.height(12.dp))
            SculptedTextButton(
                text = "Cancel",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                accent = DarkTextMid,
                textColor = DarkTextMid,
            )
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun FilterOption(
    label: String,
    description: String,
    selected: Boolean,
    onSelect: () -> Unit,
    accent: Color,
) {
    DarkCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        accent = if (selected) accent else Color(0x33FFFFFF),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selected,
                    onClick = onSelect,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = accent,
                        unselectedColor = DarkTextMid,
                    ),
                )
                Spacer(Modifier.width(4.dp))
                Text(label, style = MaterialTheme.typography.titleSmall, color = if (selected) accent else DarkTextHigh, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(2.dp))
            Text(description, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
        }
    }
}

@Composable
private fun ScrollOption(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    val accent = if (selected) Aqua else DarkTextMid
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Aqua.copy(alpha = 0.18f) else Color(0xFF2A2820))
            .glowingBorder(1.dp, if (selected) Aqua.copy(alpha = 0.7f) else Color(0x33FFFFFF), RoundedCornerShape(16.dp))
            .clickable(onClick = onSelect)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = accent,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
private fun inputColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Slate900.copy(alpha = 0.9f),
    unfocusedContainerColor = Obsidian.copy(alpha = 0.85f),
    focusedBorderColor = Aqua.copy(alpha = 0.6f),
    unfocusedBorderColor = Color(0x33FFFFFF),
    focusedTextColor = DarkTextHigh,
    unfocusedTextColor = DarkTextHigh,
    cursorColor = Aqua,
)

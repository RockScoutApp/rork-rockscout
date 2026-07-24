package com.rork.rockscout.ui.components

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppealRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.MockDataSeeder.LocalDeletedAccountLog
import com.rork.rockscout.data.ReportRepository
import com.rork.rockscout.data.SubscriptionAdminManager
import com.rork.rockscout.data.LocalDataStore
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.Warning
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Full-screen dark-themed overlay popup for the Developer Console.
 * Shows a compact profile card with admin/moderator buttons only (no social
 * actions), plus a clickable offenses list and appeals section.
 */
@Composable
fun DevUserPopup(
    userId: String,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf<SubscriptionAdminManager.AdminUser?>(null) }
    var reports by remember { mutableStateOf<List<com.rork.rockscout.data.MockDataSeeder.LocalUserReport>>(emptyList()) }
    var appeals by remember { mutableStateOf<List<com.rork.rockscout.data.MockDataSeeder.LocalAppeal>>(emptyList()) }
    var deletedLogs by remember { mutableStateOf<List<LocalDeletedAccountLog>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var screenshotViewerPath by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteReason by remember { mutableStateOf("") }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var actionMessage by remember { mutableStateOf<String?>(null) }

    suspend fun reload() {
        user = SubscriptionAdminManager.fetchAdminUser(userId)
        val allReports = ReportRepository.instance.getAllReports()
        reports = allReports.filter { it.reported_user_id == userId }
        appeals = AppealRepository.instance.getAllAppeals().filter { it.user_id == userId }
        deletedLogs = LocalDataStore.getTable<LocalDeletedAccountLog>(LocalDataStore.KEY_DELETED_ACCOUNT_LOGS)
            .filter { it.user_id == userId }
            .sortedByDescending { it.deleted_at }
        loading = false
    }

    LaunchedEffect(userId) { reload() }

    BackHandler { onDismiss() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A1812), Color(0xFF12100B), Color(0xFF0A0907))
                )
            )
            .navigationBarsPadding()
            .imePadding(),
    ) {
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading…", color = DarkTextMid, style = MaterialTheme.typography.titleMedium)
            }
        } else if (user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("User not found", color = DarkTextMid, style = MaterialTheme.typography.titleMedium)
            }
        } else {
            val u = user!!
            val isDeleted = deletedLogs.any { it.restored_at == null }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // ── Top bar with close button ──
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "User Profile",
                            style = MaterialTheme.typography.headlineSmall,
                            color = DarkTextHigh,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                        )
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                                .glowingBorder(2.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Close", tint = DarkTextMid, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                // ── Compact profile card ──
                item {
                    DevPopupCard(accent = if (u.is_premium) Citrine else DarkTextMid) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background((if (u.is_premium) Citrine else DarkTextMid).copy(alpha = 0.15f))
                                    .glowingBorder(3.dp, (if (u.is_premium) Citrine else DarkTextMid).copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(u.avatar_emoji, style = MaterialTheme.typography.titleMedium)
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        u.display_name.ifBlank { "(no name)" },
                                        style = MaterialTheme.typography.titleMedium,
                                        color = DarkTextHigh,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false),
                                    )
                                    if (u.is_premium || u.premium_badge) {
                                        Spacer(Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Citrine.copy(alpha = 0.22f))
                                                .glowingBorder(2.dp, Citrine.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 7.dp, vertical = 3.dp),
                                        ) {
                                            Text("PREMIUM", style = MaterialTheme.typography.labelSmall, color = Citrine, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    if (isDeleted) {
                                        Spacer(Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Danger.copy(alpha = 0.22f))
                                                .glowingBorder(2.dp, Danger.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 7.dp, vertical = 3.dp),
                                        ) {
                                            Text("DELETED", style = MaterialTheme.typography.labelSmall, color = Danger, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    u.email.ifBlank { "no email" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkTextMid,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    "Lvl ${u.level} · ${u.status.replace("-", " ")}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkTextMid.copy(alpha = 0.7f),
                                )
                            }
                        }
                        if (isDeleted) {
                            val delLog = deletedLogs.firstOrNull { it.restored_at == null }
                            if (delLog != null) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Reason: ${delLog.reason}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Danger.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                    }
                }

                // ── Action message ──
                actionMessage?.let { msg ->
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Success.copy(alpha = 0.12f))
                                .glowingBorder(2.dp, Success.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                        ) {
                            Text(msg, style = MaterialTheme.typography.bodySmall, color = Success, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // ── Admin buttons ──
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Delete / Restore account
                        if (isDeleted) {
                            DevActionButton(
                                text = "Restore Account",
                                icon = Icons.Filled.Restore,
                                accent = Success,
                                onClick = { showRestoreDialog = true },
                            )
                        } else {
                            DevActionButton(
                                text = "Delete Account",
                                icon = Icons.Filled.NoAccounts,
                                accent = Danger,
                                onClick = { showDeleteDialog = true },
                            )
                        }

                        // Clear all reports / Reinstate
                        if (reports.isNotEmpty()) {
                            DevActionButton(
                                text = "Clear All Reports (${reports.size})",
                                icon = Icons.Filled.Delete,
                                accent = Warning,
                                onClick = {
                                    scope.launch {
                                        ReportRepository.instance.reinstateUser(userId)
                                        actionMessage = "All reports cleared for ${u.display_name}."
                                        reload()
                                    }
                                },
                            )
                        }

                        // Toggle Premium
                        if (u.is_premium) {
                            DevActionButton(
                                text = "Revoke Premium",
                                icon = Icons.Filled.Star,
                                accent = Danger,
                                onClick = {
                                    scope.launch {
                                        SubscriptionAdminManager.cancelSubscription(context, userId)
                                        actionMessage = "Premium revoked for ${u.display_name}."
                                        reload()
                                    }
                                },
                            )
                        } else {
                            DevActionButton(
                                text = "Grant Premium",
                                icon = Icons.Filled.Star,
                                accent = Citrine,
                                onClick = {
                                    scope.launch {
                                        SubscriptionAdminManager.renewSubscription(context, userId)
                                        actionMessage = "Premium granted to ${u.display_name}."
                                        reload()
                                    }
                                },
                            )
                        }
                    }
                }

                // ── Offenses section ──
                if (reports.isNotEmpty()) {
                    item {
                        DevPopupSectionHeader("Offenses", Warning, reports.size)
                    }
                    items(reports, key = { it.id }) { report ->
                        val time = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US).format(Date(report.created_at))
                        DevPopupCard(accent = Warning) {
                            Row(verticalAlignment = Alignment.Top) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        report.reason ?: "(no reason)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = DarkTextHigh,
                                        fontWeight = FontWeight.Medium,
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "by ${report.reporter_name ?: "Unknown"} · $time",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkTextMid.copy(alpha = 0.7f),
                                    )
                                    report.screenshotPath?.let { path ->
                                        val file = File(path)
                                        if (file.exists()) {
                                            Spacer(Modifier.height(8.dp))
                                            AsyncImage(
                                                model = file,
                                                contentDescription = "Screenshot",
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .glowingBorder(2.dp, Warning.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                                    .clickable { screenshotViewerPath = path },
                                                contentScale = ContentScale.Crop,
                                            )
                                        }
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                                // Per-report remove button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Danger.copy(alpha = 0.12f))
                                        .glowingBorder(1.5.dp, Danger.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            scope.launch {
                                                ReportRepository.instance.removeReport(report.id)
                                                actionMessage = "Report removed."
                                                reload()
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                ) {
                                    Text("Remove", style = MaterialTheme.typography.labelSmall, color = Danger, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // ── Appeals section ──
                if (appeals.isNotEmpty()) {
                    item {
                        DevPopupSectionHeader("Appeals", Citrine, appeals.size)
                    }
                    items(appeals, key = { it.id }) { appeal ->
                        val time = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US).format(Date(appeal.created_at))
                        val isPending = appeal.status == "pending"
                        val accent = when (appeal.status) {
                            "approved" -> Success
                            "denied" -> Danger
                            else -> Citrine
                        }
                        DevPopupCard(accent = accent) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(accent.copy(alpha = 0.15f))
                                        .glowingBorder(2.dp, accent.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(Icons.Filled.Gavel, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        appeal.type.replace("_", " ").replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.titleSmall,
                                        color = DarkTextHigh,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text("$time · ${appeal.status.uppercase()}", style = MaterialTheme.typography.labelSmall, color = DarkTextMid.copy(alpha = 0.7f))
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(appeal.reason, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid)
                            if (isPending) {
                                Spacer(Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Success.copy(alpha = 0.15f))
                                            .glowingBorder(2.dp, Success.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                                            .clickable {
                                                scope.launch {
                                                    AppealRepository.instance.resolveAppeal(appeal.id, true)
                                                    actionMessage = "Appeal approved."
                                                    reload()
                                                }
                                            }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text("Approve", style = MaterialTheme.typography.labelMedium, color = Success, fontWeight = FontWeight.Bold)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Danger.copy(alpha = 0.12f))
                                            .glowingBorder(2.dp, Danger.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                            .clickable {
                                                scope.launch {
                                                    AppealRepository.instance.resolveAppeal(appeal.id, false)
                                                    actionMessage = "Appeal denied."
                                                    reload()
                                                }
                                            }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text("Deny", style = MaterialTheme.typography.labelMedium, color = Danger, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom padding
                item { Spacer(Modifier.height(40.dp)) }
            }
        }
    }

    // ── Delete account dialog ──
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; deleteReason = "" },
            title = { Text("Delete ${user?.display_name ?: "user"}'s account?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "The user will see a blocking popup on next sign-in and cannot access the app until restored.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = deleteReason,
                        onValueChange = { deleteReason = com.rork.rockscout.data.ProfanityFilter.filter(it) },
                        label = { Text("Reason for deletion") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                SculptedButton(
                    text = "Delete",
                    onClick = {
                        val reason = deleteReason.trim().ifBlank { "Violation of community guidelines" }
                        showDeleteDialog = false
                        deleteReason = ""
                        scope.launch {
                            val result = AuthRepository.instance.adminDeleteAccount(userId, reason)
                            if (result.isSuccess) {
                                actionMessage = "Account deleted. User will see blocking popup on next sign-in."
                            } else {
                                actionMessage = "Failed to delete: ${result.exceptionOrNull()?.message}"
                            }
                            reload()
                        }
                    },
                    accent = Danger,
                    containerColor = Danger,
                    textColor = Color.White,
                    enabled = deleteReason.trim().isNotBlank(),
                )
            },
            dismissButton = {
                SculptedTextButton(text = "Cancel", onClick = { showDeleteDialog = false; deleteReason = "" }, accent = DarkTextMid, textColor = DarkTextMid)
            },
            containerColor = Color(0xFF1E1C16),
            titleContentColor = DarkTextHigh,
            textContentColor = DarkTextMid,
        )
    }

    // ── Restore account dialog ──
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Restore ${user?.display_name ?: "user"}'s account?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This will un-delete the account and allow the user to sign in normally again.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                SculptedButton(
                    text = "Restore",
                    onClick = {
                        showRestoreDialog = false
                        scope.launch {
                            val result = AuthRepository.instance.adminRestoreAccount(userId)
                            if (result.isSuccess) {
                                actionMessage = "Account restored. User can sign in normally."
                            } else {
                                actionMessage = "Failed to restore: ${result.exceptionOrNull()?.message}"
                            }
                            reload()
                        }
                    },
                    accent = Success,
                    containerColor = Success,
                    textColor = Ink,
                )
            },
            dismissButton = {
                SculptedTextButton(text = "Cancel", onClick = { showRestoreDialog = false }, accent = DarkTextMid, textColor = DarkTextMid)
            },
            containerColor = Color(0xFF1E1C16),
            titleContentColor = DarkTextHigh,
            textContentColor = DarkTextMid,
        )
    }

    // Screenshot viewer
    screenshotViewerPath?.let { path ->
        val file = File(path)
        if (file.exists()) {
            FullScreenImageViewer(
                imageUrls = listOf(path),
                initialPage = 0,
                onDismiss = { screenshotViewerPath = null },
            )
        } else {
            LaunchedEffect(Unit) { screenshotViewerPath = null }
        }
    }
}

@Composable
private fun DevPopupCard(
    accent: Color,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .glowingBorder(2.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(14.dp),
    ) {
        content()
    }
}

@Composable
private fun DevPopupSectionHeader(text: String, color: Color, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
                .glowingBorder(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
        )
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.titleSmall, color = color, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(color.copy(alpha = 0.15f))
                .glowingBorder(2.dp, color.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                .padding(horizontal = 7.dp, vertical = 2.dp),
        ) {
            Text(count.toString(), style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DevActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accent.copy(alpha = 0.12f))
            .glowingBorder(2.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(text, style = MaterialTheme.typography.labelLarge, color = accent, fontWeight = FontWeight.Bold)
        }
    }
}

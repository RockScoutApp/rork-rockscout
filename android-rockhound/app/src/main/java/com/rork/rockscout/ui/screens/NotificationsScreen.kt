package com.rork.rockscout.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.data.AppealRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.FriendRepository
import com.rork.rockscout.data.NotificationRepository
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Full-screen notification feed with RockBackground.
 * Shows all notifications newest-first with type icons, timestamps,
 * selection mode, clear, delete selected, swipe-to-delete with undo,
 * and a friend-request summary tile at the top.
 */
@Composable
fun NotificationsScreen(
    navController: NavController,
) {
    val notifRepo = remember { NotificationRepository.instance }
    val notifications by notifRepo.notifications.collectAsStateWithLifecycle()
    val unreadCount by notifRepo.unreadCount.collectAsStateWithLifecycle()
    val friendRequestCount by notifRepo.friendRequestCount.collectAsStateWithLifecycle()
    val undoStack by notifRepo.undoStack.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val me = AuthRepository.instance.currentUserId

    val friendRepo = remember { FriendRepository.instance }
    val incomingFriendRequests by friendRepo.incomingFriendRequests.collectAsStateWithLifecycle()
    var friendRequestSenderNames by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        friendRepo.loadFriendRequests()
    }

    // Fetch sender display names for grouped friend-request display
    LaunchedEffect(incomingFriendRequests) {
        val senderIds = incomingFriendRequests.map { it.sender_id }
        if (senderIds.isNotEmpty()) {
            val profiles = SocialRepository.instance.fetchProfiles(senderIds)
            friendRequestSenderNames = profiles.map { it.display_name }
        } else {
            friendRequestSenderNames = emptyList()
        }
    }

    var selectionMode by remember { mutableStateOf(false) }
    val selectedIds = remember { mutableStateOf<Set<String>>(emptySet()) }
    var showUndoPill by remember { mutableStateOf(false) }
    var showDeleteSelectedConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        notifRepo.loadNotifications()
    }

    // Auto-dismiss undo pill after 5 seconds
    LaunchedEffect(undoStack.size) {
        if (undoStack.isNotEmpty()) {
            showUndoPill = true
            delay(5000)
            notifRepo.clearUndoStack()
            showUndoPill = false
        }
    }

    BackHandler(enabled = selectionMode) {
        selectionMode = false
        selectedIds.value = emptySet()
    }

    RockBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().navigationBarsPadding(),
            ) {
                // ── Top bar ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .sculpted(
                                    shape = CircleShape,
                                    accent = Aqua,
                                    shadowElevation = 4.dp,
                                    circular = true,
                                    onClick = { navController.popBackStack() },
                                )
                                .clip(CircleShape)
                                .background(Obsidian.copy(alpha = 0.88f))
                                .glowingBorder(2.dp, Aqua.copy(alpha = 0.55f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Close",
                                tint = Aqua,
                                modifier = Modifier.size(22.dp),
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selectionMode) Citrine.copy(alpha = 0.18f) else Obsidian.copy(alpha = 0.6f)
                                )
                                .glowingBorder(
                                    2.dp,
                                    if (selectionMode) Citrine.copy(alpha = 0.6f) else TextLow.copy(alpha = 0.3f),
                                    RoundedCornerShape(10.dp),
                                )
                                .clickable {
                                    if (selectionMode) {
                                        selectedIds.value = emptySet()
                                    }
                                    selectionMode = !selectionMode
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(
                                        if (selectionMode) Citrine else Color.Transparent
                                    )
                                    .glowingBorder(
                                        2.dp,
                                        if (selectionMode) Citrine else TextLow.copy(alpha = 0.5f),
                                        RoundedCornerShape(5.dp),
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (selectionMode) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Ink,
                                        modifier = Modifier.size(14.dp),
                                    )
                                }
                            }
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Select",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selectionMode) Citrine else TextMid,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SculptedButton(
                            text = "Clear",
                            onClick = {
                                scope.launch { notifRepo.markAllRead() }
                            },
                            accent = Aqua,
                            containerColor = Obsidian.copy(alpha = 0.88f),
                            textColor = Aqua,
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 14.dp, vertical = 6.dp,
                            ),
                        )

                        val hasSelection = selectedIds.value.isNotEmpty()
                        SculptedButton(
                            text = "Delete Selected",
                            onClick = {
                                if (hasSelection) {
                                    showDeleteSelectedConfirm = true
                                }
                            },
                            accent = if (hasSelection) Citrine else TextLow,
                            containerColor = if (hasSelection) Citrine.copy(alpha = 0.15f) else Obsidian.copy(alpha = 0.5f),
                            textColor = if (hasSelection) Citrine else TextLow.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 14.dp, vertical = 6.dp,
                            ),
                        )
                    }
                }

                // ── Notification list ──
                if (notifications.isEmpty() && friendRequestCount == 0) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "\uD83E\uDEA8",
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp),
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "No notifications yet",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextHigh,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "When fellow RockScouts interact with you, you'll see it here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMid,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = 16.dp, end = 16.dp, bottom = 80.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        // ── Friend request summary tile ──
                        if (friendRequestCount > 0) {
                            item {
                                val groupedText = formatFriendRequestSummary(friendRequestSenderNames, friendRequestCount)
                                SummaryTileRow(
                                    icon = Icons.Filled.Group,
                                    accentColor = Aqua,
                                    text = groupedText,
                                    onClick = { navController.navigate(Routes.friends(showFR = true)) },
                                )
                                Spacer(Modifier.height(4.dp))
                                androidx.compose.material3.HorizontalDivider(
                                    color = Color(0x22FFFFFF),
                                    thickness = 1.dp,
                                )
                                Spacer(Modifier.height(4.dp))
                            }
                        }

                        // ── Regular notifications ──
                        items(notifications) { notif ->
                            SwipeableNotificationRow(
                                notif = notif,
                                selectionMode = selectionMode,
                                isSelected = notif.id in selectedIds.value,
                                onToggleSelect = { id ->
                                    selectedIds.value = if (id in selectedIds.value) {
                                        selectedIds.value - id
                                    } else {
                                        selectedIds.value + id
                                    }
                                },
                                onTap = {
                                    scope.launch {
                                        notifRepo.markRead(notif.id)
                                    }
                                    val target = notif.deep_link_target
                                    if (target != null) {
                                        navigateToDeepLink(navController, target)
                                    }
                                },
                                onAppeal = {
                                    scope.launch {
                                        AppealRepository.instance.submitAppeal(
                                            userId = me ?: "",
                                            type = notif.type,
                                            refId = notif.ref_id,
                                            reason = "I disagree with this moderation decision.",
                                        )
                                    }
                                },
                                onSwipeDelete = {
                                    scope.launch { notifRepo.swipeDelete(notif.id) }
                                },
                            )
                        }
                    }
                }

                // ── Undo pill ──
                if (showUndoPill && undoStack.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        SculptedButton(
                            text = if (undoStack.size == 1) "Undo" else "Undo (${undoStack.size})",
                            onClick = {
                                scope.launch { notifRepo.undoDelete() }
                            },
                            accent = Aqua,
                            containerColor = Obsidian.copy(alpha = 0.92f),
                            textColor = Aqua,
                            shape = RoundedCornerShape(24.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 24.dp, vertical = 8.dp,
                            ),
                        )
                    }
                }

                // ── Bottom close button ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    SculptedButton(
                        text = "Close",
                        onClick = { navController.popBackStack() },
                        accent = Aqua,
                        containerColor = Obsidian.copy(alpha = 0.92f),
                        textColor = Aqua,
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 32.dp, vertical = 10.dp,
                        ),
                    )
                }
            }
        }
    }

    // Delete selected notifications confirmation
    if (showDeleteSelectedConfirm) {
        val count = selectedIds.value.size
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteSelectedConfirm = false },
            containerColor = Color(0xFF1E1C16),
            titleContentColor = TextHigh,
            textContentColor = TextMid,
            title = { Text("Delete $count notification${if (count != 1) "s" else ""}?", color = TextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to delete $count notification${if (count != 1) "s" else ""}? This cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMid,
                )
            },
            confirmButton = {
                SculptedButton(
                    text = "Delete",
                    onClick = {
                        showDeleteSelectedConfirm = false
                        scope.launch {
                            notifRepo.deleteNotifications(selectedIds.value)
                            selectedIds.value = emptySet()
                            selectionMode = false
                        }
                    },
                    accent = Danger,
                    containerColor = Danger,
                    textColor = Color.White,
                )
            },
            dismissButton = {
                SculptedTextButton(
                    text = "Cancel",
                    onClick = { showDeleteSelectedConfirm = false },
                    accent = Aqua,
                    textColor = TextMid,
                )
            },
        )
    }
}

/* ── Summary tile row (friend requests) ──────────────────────────────────── */

@Composable
private fun SummaryTileRow(
    icon: ImageVector,
    accentColor: Color,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(accentColor.copy(alpha = 0.18f), Obsidian.copy(alpha = 0.55f))
                )
            )
            .glowingBorder(1.5.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accentColor.copy(alpha = 0.22f))
                .glowingBorder(1.5.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextHigh,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(22.dp),
        )
    }
}

/* ── Swipeable notification row ──────────────────────────────────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableNotificationRow(
    notif: NotificationRepository.NotificationRow,
    selectionMode: Boolean,
    isSelected: Boolean,
    onToggleSelect: (String) -> Unit,
    onTap: () -> Unit,
    onAppeal: () -> Unit,
    onSwipeDelete: () -> Unit,
) {
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onSwipeDelete()
                true
            } else false
        },
    )

    SwipeToDismissBox(
        state = swipeState,
        modifier = Modifier.fillMaxWidth(),
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE2574C).copy(alpha = 0.85f))
                    .glowingBorder(1.dp, Color(0xFFE2574C).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        enableDismissFromStartToEnd = false,
    ) {
        NotificationRowItem(
            notif = notif,
            selectionMode = selectionMode,
            isSelected = isSelected,
            onToggleSelect = onToggleSelect,
            onTap = onTap,
            onAppeal = onAppeal,
        )
    }
}

/* ── Notification row item ────────────────────────────────────────────────── */

@Composable
private fun NotificationRowItem(
    notif: NotificationRepository.NotificationRow,
    selectionMode: Boolean,
    isSelected: Boolean,
    onToggleSelect: (String) -> Unit,
    onTap: () -> Unit,
    onAppeal: () -> Unit,
) {
    val isUnread = notif.read_at == null
    val typeIcon = typeIconForType(notif.type)
    val typeColor = typeColorForType(notif.type)
    val dateFormat = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }
    val formattedDate = remember(notif.created_at) {
        runCatching {
            val instant = java.time.OffsetDateTime.parse(notif.created_at).toInstant()
            dateFormat.format(Date.from(instant))
        }.getOrDefault("")
    }

    val rowBackground = if (isUnread) {
        Brush.horizontalGradient(
            listOf(typeColor.copy(alpha = 0.12f), Obsidian.copy(alpha = 0.55f))
        )
    } else {
        Brush.horizontalGradient(
            listOf(Obsidian.copy(alpha = 0.6f), Obsidian.copy(alpha = 0.45f))
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(rowBackground)
            .glowingBorder(
                1.dp,
                if (isUnread) typeColor.copy(alpha = 0.35f) else TextLow.copy(alpha = 0.12f),
                RoundedCornerShape(12.dp),
            )
            .clickable { onTap() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        if (selectionMode) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .padding(top = 2.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(if (isSelected) Citrine else Color.Transparent)
                    .glowingBorder(
                        2.dp,
                        if (isSelected) Citrine else TextLow.copy(alpha = 0.5f),
                        RoundedCornerShape(5.dp),
                    )
                    .clickable { onToggleSelect(notif.id) },
                contentAlignment = Alignment.Center,
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = Ink,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(typeColor.copy(alpha = 0.18f))
                .glowingBorder(1.5.dp, typeColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                typeIcon,
                contentDescription = null,
                tint = typeColor,
                modifier = Modifier.size(20.dp),
            )
        }

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notif.body,
                style = MaterialTheme.typography.bodyMedium,
                color = TextHigh,
                fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Medium,
                maxLines = 4,
                overflow = TextOverflow.Visible,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelSmall,
                color = TextLow,
                fontWeight = FontWeight.SemiBold,
            )

            if (notif.type == NotificationRepository.TYPE_IMAGE_REJECTED ||
                notif.type == NotificationRepository.TYPE_REPORT_BAN
            ) {
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Citrine.copy(alpha = 0.15f))
                        .glowingBorder(1.5.dp, Citrine.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .clickable { onAppeal() }
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        tint = Citrine,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Appeal",
                        style = MaterialTheme.typography.labelMedium,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

/* ── Helpers ──────────────────────────────────────────────────────────────── */

private fun typeIconForType(type: String): ImageVector = when (type) {
    NotificationRepository.TYPE_FRIEND_REQUEST -> Icons.Filled.Group
    NotificationRepository.TYPE_MESSAGE -> Icons.Filled.Message
    NotificationRepository.TYPE_NEW_POST -> Icons.Filled.Notifications
    NotificationRepository.TYPE_POST_LIKE -> Icons.Filled.Favorite
    NotificationRepository.TYPE_POST_COMMENT -> Icons.Filled.Message
    NotificationRepository.TYPE_POST_REPLY -> Icons.Filled.Message
    NotificationRepository.TYPE_TRADE_INTEREST -> Icons.Filled.SwapHoriz
    NotificationRepository.TYPE_MARKED_TRADED -> Icons.Filled.CheckCircle
    NotificationRepository.TYPE_LOCATION_APPROVED -> Icons.Filled.LocationOn
    NotificationRepository.TYPE_IMAGE_REJECTED -> Icons.Filled.Warning
    NotificationRepository.TYPE_REPORT_BAN -> Icons.Filled.Warning
    else -> Icons.Filled.Notifications
}

private fun typeColorForType(type: String): Color = when (type) {
    NotificationRepository.TYPE_FRIEND_REQUEST -> Aqua
    NotificationRepository.TYPE_MESSAGE -> Aqua
    NotificationRepository.TYPE_NEW_POST -> Citrine
    NotificationRepository.TYPE_POST_LIKE -> Color(0xFFE2574C)
    NotificationRepository.TYPE_POST_COMMENT -> Color(0xFF7CB5EC)
    NotificationRepository.TYPE_POST_REPLY -> Color(0xFF7CB5EC)
    NotificationRepository.TYPE_TRADE_INTEREST -> Citrine
    NotificationRepository.TYPE_MARKED_TRADED -> Color(0xFF5CC98C)
    NotificationRepository.TYPE_LOCATION_APPROVED -> Color(0xFF5CC98C)
    NotificationRepository.TYPE_IMAGE_REJECTED -> Color(0xFFFF6B3D)
    NotificationRepository.TYPE_REPORT_BAN -> Color(0xFFFF3D3D)
    else -> Aqua
}

/** Format a grouped friend-request summary like "Alice, Bob, and 3 others have sent you friend requests." */
private fun formatFriendRequestSummary(names: List<String>, totalCount: Int): String {
    if (totalCount <= 0) return "You have no friend requests waiting"
    val knownNames = names.take(2)
    val extraCount = totalCount - knownNames.size
    return when {
        knownNames.isEmpty() && extraCount > 0 -> "You have $totalCount friend request${if (totalCount > 1) "s" else ""} waiting"
        knownNames.size == 1 && extraCount == 0 -> "${knownNames[0]} sent you a friend request"
        knownNames.size == 1 && extraCount == 1 -> "${knownNames[0]} and 1 other have sent you friend requests"
        knownNames.size == 1 && extraCount > 1 -> "${knownNames[0]} and $extraCount others have sent you friend requests"
        knownNames.size == 2 && extraCount == 0 -> "${knownNames[0]}, ${knownNames[1]} have sent you friend requests"
        knownNames.size >= 2 && extraCount > 0 -> "${knownNames[0]}, ${knownNames[1]}, and $extraCount other${if (extraCount > 1) "s" else ""} have sent you friend requests"
        else -> "You have $totalCount friend request${if (totalCount > 1) "s" else ""} waiting"
    }
}

private fun navigateToDeepLink(navController: NavController, target: String) {
    runCatching {
        if (target.startsWith("user_profile/")) {
            val userId = target.removePrefix("user_profile/")
            navController.navigate(Routes.userProfile(userId))
        } else if (target.startsWith("location/")) {
            val locId = target.removePrefix("location/")
            navController.navigate(Routes.location(locId))
        } else when (target) {
            "my_trades" -> navController.navigate(Routes.MY_TRADES)
            "profile" -> navController.navigate(Routes.PROFILE)
            "contact_us" -> navController.navigate(Routes.CONTACT_US)
            "friends" -> navController.navigate(Routes.friends(showFR = true))
            "notifications" -> { /* already here */ }
            else -> { /* unknown — stay */ }
        }
    }
}

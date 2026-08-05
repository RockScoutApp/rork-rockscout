package com.rork.rockscout.ui.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.NotificationRepository
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import kotlinx.coroutines.launch

/**
 * Full-screen conversation picker for sharing the user's current ping.
 * Shows all existing threads and friend/message-request senders so the user
 * can tap a tile to send the ping deep link and jump straight into the chat.
 */
@Composable
fun PingConversationPicker(
    navController: NavController,
    ping: PingToShare,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val social = SocialRepository.instance
    val auth = AuthRepository.instance
    val threads by social.threads.collectAsStateWithLifecycle()
    val incomingRequests by social.incomingRequests.collectAsStateWithLifecycle()
    var hunterCache by remember { mutableStateOf<Map<String, SocialRepository.HunterProfile>>(emptyMap()) }
    var sending by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        social.loadThreads()
        social.loadRequests()
        social.loadConnections()
    }

    LaunchedEffect(threads, incomingRequests) {
        val ids = buildSet {
            threads.forEach {
                add(if (it.user_a == auth.currentUserId) it.user_b else it.user_a)
            }
            incomingRequests.forEach { add(it.sender_id) }
        }.toList()
        if (ids.isNotEmpty()) {
            val fetched = mutableMapOf<String, SocialRepository.HunterProfile>()
            ids.chunked(50).forEach { chunk ->
                social.fetchProfiles(chunk).forEach { fetched[it.id] = it }
            }
            hunterCache = fetched
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                    )
                ),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 16.dp, top = 52.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DarkTextHigh,
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Share ping with",
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    if (sending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Citrine,
                            strokeWidth = 2.dp,
                        )
                    }
                }
                HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)

                val meId = auth.currentUserId
                val items = remember(threads, incomingRequests, hunterCache) {
                    val threadItems = threads.map { thread ->
                        val otherId = if (thread.user_a == meId) thread.user_b else thread.user_a
                        ConversationItem.Thread(
                            thread = thread,
                            otherId = otherId,
                            profile = hunterCache[otherId],
                        )
                    }
                    val requestItems = incomingRequests.map { req ->
                        ConversationItem.Request(
                            request = req,
                            otherId = req.sender_id,
                            profile = hunterCache[req.sender_id],
                        )
                    }
                    // De-duplicate: if a request sender also has a thread, show only the thread.
                    val threadIds = threadItems.map { it.otherId }.toSet()
                    threadItems + requestItems.filter { it.request.sender_id !in threadIds }
                }

                if (items.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "No conversations yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = DarkTextHigh,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Start a conversation from RockScout Friends first.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextMid,
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(items, key = { it.id }) { item ->
                            ConversationTile(
                                item = item,
                                onClick = {
                                    if (sending) return@ConversationTile
                                    val otherId = item.otherId
                                    val otherName = item.profile?.display_name ?: "RockScout"
                                    scope.launch {
                                        sending = true
                                        error = null
                                        val result = sharePingToConversation(
                                            social = social,
                                            auth = auth,
                                            item = item,
                                            ping = ping,
                                            context = context,
                                        )
                                        sending = false
                                        if (result.isSuccess) {
                                            onDismiss()
                                            navController.navigate(Routes.messengerThread(otherId))
                                        } else {
                                            error = result.exceptionOrNull()?.message ?: "Could not send ping."
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
            }

            error?.let { err ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2A2820))
                        .padding(12.dp),
                ) {
                    Text(err, color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/** Data passed from the map describing the ping to share. */
data class PingToShare(
    val lat: Double,
    val lng: Double,
    val label: String,
    val senderId: String,
    val senderName: String,
)

private sealed class ConversationItem {
    abstract val id: String
    abstract val otherId: String
    abstract val profile: SocialRepository.HunterProfile?

    data class Thread(
        val thread: SocialRepository.ThreadRow,
        override val otherId: String,
        override val profile: SocialRepository.HunterProfile?,
    ) : ConversationItem() {
        override val id: String = thread.id
    }

    data class Request(
        val request: SocialRepository.MessageRequestRow,
        override val otherId: String,
        override val profile: SocialRepository.HunterProfile?,
    ) : ConversationItem() {
        override val id: String = request.id
    }
}

@Composable
private fun ConversationTile(
    item: ConversationItem,
    onClick: () -> Unit,
) {
    val emoji = item.profile?.avatar_emoji ?: "\u26CF\uFE0F"
    val name = item.profile?.display_name ?: "RockScout"
    val subtitle = when (item) {
        is ConversationItem.Thread -> "Tap to send your ping"
        is ConversationItem.Request -> "Message request · tap to send your ping"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2A2820))
            .glowingBorder(1.dp, Aqua.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Citrine.copy(alpha = 0.45f), Aqua.copy(alpha = 0.25f)))),
            contentAlignment = Alignment.Center,
        ) {
            Text(emoji, style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                name,
                style = MaterialTheme.typography.titleMedium,
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextMid,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(8.dp))
        Icon(
            Icons.Filled.Share,
            contentDescription = "Share",
            tint = Citrine,
            modifier = Modifier.size(22.dp),
        )
    }
}

private suspend fun sharePingToConversation(
    social: SocialRepository,
    auth: AuthRepository,
    item: ConversationItem,
    ping: PingToShare,
    context: android.content.Context,
): Result<Unit> {
    val me = auth.currentUserId ?: return Result.failure(IllegalStateException("Not signed in"))
    val otherId = item.otherId

    return runCatching {
        // Ensure a thread exists for this recipient.
        val threadId = when (item) {
            is ConversationItem.Thread -> item.thread.id
            is ConversationItem.Request -> social.ensureThread(otherId)
                ?: throw IllegalStateException("Could not create thread")
        }

        // Build a deep link the recipient can tap to add the shared ping to their map.
        val deepLink = Uri.parse(
            "rockscout://ping/${ping.lat},${ping.lng}?" +
                "label=${Uri.encode(ping.label)}&" +
                "from=${Uri.encode(ping.senderName)}&" +
                "fromId=${Uri.encode(ping.senderId)}",
        ).toString()
        val messageBody = "I'm sharing my RockScout ping with you: $deepLink"

        val result = social.sendMessage(
            threadId = threadId,
            body = messageBody,
        )
        if (result.isFailure) throw result.exceptionOrNull() ?: IllegalStateException("Send failed")

        // For the sender, their own ping is already visible on their own map.
        // For the recipient, the deep link will add a shared ping when they tap it.

        // Notify the recipient so they see the new message even if they don't open the chat.
        NotificationRepository.instance.createNotification(
            userId = otherId,
            type = NotificationRepository.TYPE_MESSAGE,
            actorId = me,
            refId = threadId,
            body = "${ping.senderName} shared a ping location with you.",
        )

        Unit
    }.onFailure { Log.w("PingConversationPicker", "sharePing failed", it) }
}

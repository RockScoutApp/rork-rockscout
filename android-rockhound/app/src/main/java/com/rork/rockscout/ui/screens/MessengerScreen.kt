package com.rork.rockscout.ui.screens

import android.content.Intent
import android.net.Uri
import com.rork.rockscout.data.SafeLinkOpener
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import com.rork.rockscout.ui.components.glowingBorder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.ImageModerator
import com.rork.rockscout.data.ImageUtils
import com.rork.rockscout.data.ModerationTriState
import com.rork.rockscout.data.ImageReviewRepository
import com.rork.rockscout.data.ProfanityFilter
import com.rork.rockscout.data.ReportRepository
import com.rork.rockscout.data.ReportScreenshotHelper
import com.rork.rockscout.data.SessionStatus
import com.rork.rockscout.data.ChatDraftStore
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.data.SupabaseMessagingRepository
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.ReportSubmittedDialog
import com.rork.rockscout.ui.components.ImageSourcePickerDialog
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.processImageUri
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.MyBubbleBg
import com.rork.rockscout.ui.theme.OtherBubbleBg
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.data.OfflineMessageQueue
import com.rork.rockscout.data.PendingMessage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Mail
import okhttp3.MediaType.Companion.toMediaTypeOrNull

/**
 * Messenger screen — used for deep-linking into specific conversation threads
 * and message request chat views. The inbox list has been moved to the unified
 * FriendsScreen.
 *
 * Entry points:
 *  - MESSENGER_THREAD route: opens a specific thread with another user.
 *  - MESSENGER_REQUEST route: opens a specific message request chat view.
 *  - MESSENGER route (no args): redirects to the unified FriendsScreen.
 */
@Composable
fun MessengerScreen(
    navController: NavController,
    openThreadWith: String? = null,
    openRequestId: String? = null,
) {
    val auth = AuthRepository.instance
    val social = SocialRepository.instance
    val repo = AppRepository.instance
    val profile by repo.profile.collectAsStateWithLifecycle()
    val sessionStatus by auth.sessionStatus.collectAsStateWithLifecycle()
    val isSignedIn = sessionStatus is SessionStatus.Authenticated

    val accessManager = com.rork.rockscout.data.IdentifyAccessManager.instance
    val purchaseManager = com.rork.rockscout.data.PurchaseManager.instance
    val isPremium by purchaseManager.effectiveIsPremium.collectAsStateWithLifecycle()
    val clubLocked = remember(isPremium) {
        accessManager.isSocialLocked(isPremium)
    }

    val incomingRequests by social.incomingRequests.collectAsStateWithLifecycle()
    val threads by social.threads.collectAsStateWithLifecycle()
    val messages by social.messages.collectAsStateWithLifecycle()
    val connections by social.connections.collectAsStateWithLifecycle()
    val friendRepo = com.rork.rockscout.data.FriendRepository.instance
    val friendsList by friendRepo.friends.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    var activeThreadId by remember { mutableStateOf<String?>(null) }
    var activeOtherId by remember { mutableStateOf<String?>(null) }
    var activeOtherName by remember { mutableStateOf<String?>(null) }
    var activeOtherEmoji by remember { mutableStateOf<String?>(null) }
    var replyBody by remember { mutableStateOf("") }
    var hunterCache by remember { mutableStateOf<Map<String, SocialRepository.HunterProfile>>(emptyMap()) }
    var activeRequestId by remember { mutableStateOf<String?>(openRequestId) }
    var showProfanityWarning by remember { mutableStateOf(false) }
    var pendingFilteredText by remember { mutableStateOf("") }
    var showSelfHarmWarning by remember { mutableStateOf(false) }
    var selfHarmFilteredText by remember { mutableStateOf("") }
    var selfHarmOffenseCount by remember { mutableStateOf(0) }
    var activeGroupChatId by remember { mutableStateOf<String?>(null) }
    var activeGroupChatName by remember { mutableStateOf<String?>(null) }
    var replyToMessageId by remember { mutableStateOf<String?>(null) }
    var replyToSenderName by remember { mutableStateOf<String?>(null) }
    var replyToBody by remember { mutableStateOf<String?>(null) }

    // Message request dialog state
    var showRequestDialog by remember { mutableStateOf(false) }
    var requestTarget by remember { mutableStateOf<SocialRepository.HunterProfile?>(null) }
    var requestBody by remember { mutableStateOf("") }
    var requestSent by remember { mutableStateOf<String?>(null) }
    var requestError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isSignedIn, profile.clubEnabled) {
        if (isSignedIn && profile.clubEnabled) {
            social.loadRequests()
            social.loadThreads()
            social.loadConnections()
        }
    }

    // If opened with a group chat target (prefix "group:"), load group chat
    LaunchedEffect(openThreadWith) {
        if (openThreadWith != null && openThreadWith.startsWith("group:")) {
            val gcId = openThreadWith.removePrefix("group:")
            activeGroupChatId = gcId
            scope.launch {
                SupabaseMessagingRepository.loadGroupMessages(gcId)
                SupabaseMessagingRepository.saveGroupLastRead(gcId)
                SupabaseMessagingRepository.refreshGroupChatUnreadCounts()
                // Find group chat name
                val gc = SupabaseMessagingRepository.groupChats.value.firstOrNull { it.id == gcId }
                activeGroupChatName = gc?.name ?: "Group Chat"
            }
        }
    }

    // If opened with a target user, open the thread directly (if connected)
    // or show a message request dialog (if not connected).
    LaunchedEffect(openThreadWith, threads, connections) {
        if (openThreadWith != null && !openThreadWith.startsWith("group:") && activeThreadId == null && !showRequestDialog) {
            val thread = threads.firstOrNull {
                val other = if (it.user_a == auth.currentUserId) it.user_b else it.user_a
                other == openThreadWith
            }
            if (thread != null) {
                activeThreadId = thread.id
                activeOtherId = openThreadWith
            } else if (connections.contains(openThreadWith)) {
                scope.launch {
                    val tid = social.ensureThread(openThreadWith)
                    if (tid != null) {
                        activeThreadId = tid
                        activeOtherId = openThreadWith
                    }
                }
            } else {
                if (requestTarget == null) {
                    scope.launch {
                        val profiles = social.fetchProfiles(listOf(openThreadWith))
                        requestTarget = profiles.firstOrNull()
                        showRequestDialog = true
                    }
                }
            }
        }
    }

    // Fetch hunter profiles for thread partners + request senders.
    LaunchedEffect(threads, incomingRequests, activeOtherId) {
        val ids = buildSet {
            threads.forEach { add(if (it.user_a == auth.currentUserId) it.user_b else it.user_a) }
            incomingRequests.forEach { add(it.sender_id) }
            activeOtherId?.let { add(it) }
        }.toList()
        if (ids.isNotEmpty()) {
            val fetched = mutableMapOf<String, SocialRepository.HunterProfile>()
            ids.chunked(50).forEach { chunk ->
                social.fetchProfiles(chunk).forEach { fetched[it.id] = it }
            }
            hunterCache = fetched
        }
    }

    LaunchedEffect(activeThreadId) {
        val tid = activeThreadId
        if (tid != null) {
            social.loadMessages(tid)
        }
    }

    LaunchedEffect(activeOtherId, hunterCache) {
        val h = activeOtherId?.let { hunterCache[it] }
        activeOtherName = h?.display_name ?: "RockScout"
        activeOtherEmoji = h?.avatar_emoji ?: "\u26CF\uFE0F"
    }

    if (!isSignedIn) {
        MessengerLockedState(
            emoji = "\uD83D\uDD11",
            title = "Sign in to message",
            message = "You need a RockScout account to connect and chat with other hunters.",
            buttonLabel = "Sign in",
            onButton = { navController.navigate(Routes.SIGN_IN) },
        )
        return
    }
    if (clubLocked) {
        MessengerLockedState(
            emoji = "\uD83D\uDD12",
            title = "Unlock RockScout Friends",
            message = "Your 1-week free trial has ended. Subscribe or donate to keep messaging and connecting with other hunters.",
            buttonLabel = "Subscribe or donate",
            onButton = { navController.navigate(Routes.PAYWALL) },
        )
        return
    }
    if (!profile.clubEnabled) {
        MessengerLockedState(
            emoji = "\uD83E\uDD1D",
            title = "Turn on RockScout Friends",
            message = "Enable the RockScout Friends toggle in your Profile to send and receive messages.",
            buttonLabel = "Open Profile",
            onButton = { navController.navigate(Routes.PROFILE) },
        )
        return
    }

    // If no specific thread or request was requested, redirect to FriendsScreen.
    if (openThreadWith == null && openRequestId == null && activeThreadId == null && activeRequestId == null) {
        LaunchedEffect(Unit) {
            navController.navigate(Routes.friends()) {
                popUpTo(Routes.FRIENDS) { inclusive = true }
            }
        }
        return
    }

    // Full-screen chat view for a message request.
    val activeReq = incomingRequests.firstOrNull { it.id == activeRequestId }
    if (activeReq != null) {
        val sender = hunterCache[activeReq.sender_id]
        BackHandler { activeRequestId = null }
        RequestChatView(
            senderName = sender?.display_name ?: "RockScout",
            senderEmoji = sender?.avatar_emoji ?: "\u26CF\uFE0F",
            body = activeReq.body,
            timestamp = formatRelative(activeReq.created_at),
            onBack = { activeRequestId = null },
            senderId = activeReq.sender_id,
            onOpenUserProfile = { uid -> navController.navigate(Routes.userProfile(uid)) },
            onAccept = {
                scope.launch {
                    social.acceptRequest(activeReq)
                    social.loadRequests()
                    social.loadThreads()
                    social.loadConnections()
                    activeRequestId = null
                }
            },
            onDeny = {
                scope.launch {
                    social.denyRequest(activeReq)
                    social.loadRequests()
                    activeRequestId = null
                }
            },
            onBlock = {
                scope.launch {
                    social.denyAndBlockRequest(activeReq)
                    social.loadRequests()
                    activeRequestId = null
                }
            },
            navController = navController,
        )
        return
    }

    // Group chat view
    if (activeGroupChatId != null) {
        val gcId = activeGroupChatId
        val groupMessages by SupabaseMessagingRepository.groupMessages.collectAsStateWithLifecycle()
        BackHandler {
            if (replyBody.isNotBlank()) {
                ChatDraftStore.saveDraft(gcId!!, activeGroupChatName ?: "Group Chat", replyBody, isGroup = true)
            }
            gcId?.let { scope.launch { SupabaseMessagingRepository.clearTypingStatus(it) } }
            SupabaseMessagingRepository.saveGroupLastRead(gcId!!)
            scope.launch { SupabaseMessagingRepository.refreshGroupChatUnreadCounts() }
            activeGroupChatId = null
            activeGroupChatName = null
            replyBody = ""
            replyToMessageId = null
            replyToSenderName = null
            replyToBody = null
        }
        ThreadView(
            otherName = activeGroupChatName ?: "Group Chat",
            otherEmoji = "\uD83D\uDC65",
            otherUserId = null,
            messages = groupMessages.map { gm ->
                SocialRepository.MessageRow(
                    id = gm.id,
                    thread_id = gcId ?: "",
                    sender_id = gm.sender_id,
                    body = gm.body,
                    image_uri = gm.image_url,
                    read_at = null,
                    created_at = gm.created_at,
                    reply_to_message_id = gm.reply_to_message_id,
                    tagged_user_ids = gm.tagged_user_ids,
                )
            },
            myUserId = auth.currentUserId,
            replyBody = replyBody,
            onReplyChange = { replyBody = it },
            chatId = gcId ?: "",
            onOpenUserProfile = { uid -> navController.navigate(Routes.userProfile(uid)) },
            onSend = {
                // Use the group's profanity filter level
                val gc = SupabaseMessagingRepository.groupChats.value.firstOrNull { it.id == gcId }
                val strict = gc?.profanity_filter_level == "strict"
                // Step 1: Check for self-harm phrases FIRST
                val selfHarmResult = ProfanityFilter.filterSelfHarm(replyBody)
                if (selfHarmResult.hasSelfHarm) {
                    selfHarmOffenseCount += 1
                    selfHarmFilteredText = selfHarmResult.filteredText
                    if (selfHarmOffenseCount >= 2 && auth.currentUserId != null) {
                        // Auto-file a report on 2nd offense
                        scope.launch {
                            ReportRepository.instance.reportUser(
                                reportedUserId = auth.currentUserId!!,
                                reason = "Self-harm language: ${selfHarmResult.matchedPhrases.joinToString(", ")}",
                                screenshotPath = null,
                                reporterName = null,
                                reportedName = profile.name,
                                reportedAvatar = profile.avatarEmoji,
                            )
                        }
                    }
                    showSelfHarmWarning = true
                    return@ThreadView
                }
                // Step 2: Regular profanity filter on the (possibly self-harm-asterisked) text
                val result = ProfanityFilter.filterWithWarning(selfHarmResult.filteredText, groupStrict = strict)
                val filtered = result.filteredText
                if (result.hasExplicitContent) {
                    pendingFilteredText = filtered
                    showProfanityWarning = true
                    // Record warning server-side via Cloudflare Worker
                    val uid = auth.currentUserId
                    if (uid != null) {
                        scope.launch {
                            try {
                                val apiUrl = com.rork.rockscout.data.BuildSecrets.resolve("EXPO_PUBLIC_RORK_FUNCTIONS_URL", com.rork.rockscout.data.BuildSecrets.RORK_FUNCTIONS_URL)
                                val appKey = com.rork.rockscout.data.BuildSecrets.resolve("EXPO_PUBLIC_RORK_APP_KEY", com.rork.rockscout.data.BuildSecrets.RORK_APP_KEY)
                                val client = okhttp3.OkHttpClient()
                                val json = org.json.JSONObject().apply {
                                    put("userId", uid)
                                    put("reason", "Explicit language in group chat")
                                    put("source", "group_chat")
                                    put("sourceId", gcId)
                                }
                                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                                val req = okhttp3.Request.Builder()
                                    .url("$apiUrl/profanity-warning")
                                    .post(okhttp3.RequestBody.create(mediaType, json.toString()))
                                    .addHeader("Content-Type", "application/json")
                                    .addHeader("Authorization", "Bearer $appKey")
                                    .build()
                                client.newCall(req).execute()
                            } catch (_: Exception) { /* best-effort */ }
                        }
                    }
                } else if (filtered.isNotBlank() && gcId != null) {
                    // Parse @username tags from the input text
                    val memberIds = SupabaseMessagingRepository.groupChatMemberIds(gcId)
                    val taggedIds = parseTaggedUserIds(replyBody, memberIds, hunterCache)
                    scope.launch {
                        SupabaseMessagingRepository.sendGroupMessage(gcId, filtered, null, replyToMessageId, taggedIds)
                        replyBody = ""
                        replyToMessageId = null
                        replyToSenderName = null
                        replyToBody = null
                        ChatDraftStore.deleteDraft(gcId)
                    }
                }
            },
            onSendImage = { imageUri ->
                if (gcId != null) {
                    scope.launch { SupabaseMessagingRepository.sendGroupMessage(gcId, "", imageUri, replyToMessageId) }
                }
            },
            onBack = {
                if (replyBody.isNotBlank()) {
                    ChatDraftStore.saveDraft(gcId!!, activeGroupChatName ?: "Group Chat", replyBody, isGroup = true)
                }
                gcId?.let { scope.launch { SupabaseMessagingRepository.clearTypingStatus(it) } }
                SupabaseMessagingRepository.saveGroupLastRead(gcId!!)
                scope.launch { SupabaseMessagingRepository.refreshGroupChatUnreadCounts() }
                activeGroupChatId = null
                activeGroupChatName = null
                replyBody = ""
                replyToMessageId = null
                replyToSenderName = null
                replyToBody = null
            },
            isGroupChat = true,
            replyToSenderName = replyToSenderName,
            replyToBody = replyToBody,
            onCancelReply = {
                replyToMessageId = null
                replyToSenderName = null
                replyToBody = null
            },
            onLongPressMessage = { msg ->
                replyToMessageId = msg.id
                val senderName = hunterCache[msg.sender_id]?.display_name ?: "RockScout"
                replyToSenderName = senderName
                replyToBody = msg.body.ifBlank { "[image]" }
                // Insert @username into the input box
                if (replyBody.isBlank() || !replyBody.contains("@$senderName")) {
                    replyBody = "@$senderName " + replyBody
                }
            },
            hunterCache = hunterCache,
        )
        if (showSelfHarmWarning) {
            SelfHarmWarningDialog(
                offenseCount = selfHarmOffenseCount,
                onConfirm = {
                    showSelfHarmWarning = false
                    val text = selfHarmFilteredText
                    if (text.isNotBlank() && gcId != null) {
                        val memberIds = SupabaseMessagingRepository.groupChatMemberIds(gcId)
                        val taggedIds = parseTaggedUserIds(replyBody, memberIds, hunterCache)
                        val replyToSnapshot = replyToMessageId
                        scope.launch {
                            SupabaseMessagingRepository.sendGroupMessage(gcId, text, null, replyToSnapshot, taggedIds)
                            replyBody = ""
                            replyToMessageId = null
                            replyToSenderName = null
                            replyToBody = null
                            ChatDraftStore.deleteDraft(gcId)
                        }
                    }
                },
            )
        }
        if (showProfanityWarning) {
            ProfanityWarningDialog(
                onConfirm = {
                    showProfanityWarning = false
                    val text = pendingFilteredText
                    if (text.isNotBlank() && gcId != null) {
                        val memberIds = SupabaseMessagingRepository.groupChatMemberIds(gcId)
                        val taggedIds = parseTaggedUserIds(replyBody, memberIds, hunterCache)
                        val replyToSnapshot = replyToMessageId
                        scope.launch {
                            val result = SupabaseMessagingRepository.sendGroupMessage(gcId, text, null, replyToSnapshot, taggedIds)
                            if (result.isFailure) {
                                OfflineMessageQueue.enqueue(PendingMessage(
                                    id = "pending-" + java.util.UUID.randomUUID(),
                                    chatId = gcId,
                                    body = text,
                                    replyToMessageId = replyToSnapshot,
                                    taggedUserIds = taggedIds,
                                    isGroup = true,
                                ))
                            }
                            replyBody = ""
                            replyToMessageId = null
                            replyToSenderName = null
                            replyToBody = null
                            ChatDraftStore.deleteDraft(gcId)
                        }
                    }
                },
            )
        }
        return
    }

    // Thread view (when a thread is open).
    if (activeThreadId != null && activeOtherId != null) {
        val threadId = activeThreadId
        BackHandler {
            if (replyBody.isNotBlank() && threadId != null) {
                ChatDraftStore.saveDraft(threadId, activeOtherName ?: "RockScout", replyBody)
            }
            threadId?.let { scope.launch { SupabaseMessagingRepository.clearTypingStatus(it) } }
            activeThreadId = null
            activeOtherId = null
            replyBody = ""
            replyToMessageId = null
            replyToSenderName = null
            replyToBody = null
            scope.launch {
                threadId?.let { social.markThreadRead(it) }
                social.loadThreads()
            }
        }
        ThreadView(
            otherName = activeOtherName ?: "RockScout",
            otherEmoji = activeOtherEmoji ?: "\u26CF\uFE0F",
            otherUserId = activeOtherId,
            messages = messages,
            myUserId = auth.currentUserId,
            replyBody = replyBody,
            onReplyChange = { replyBody = it },
            chatId = threadId ?: "",
            onOpenUserProfile = { uid -> navController.navigate(Routes.userProfile(uid)) },
            friendsList = friendsList,
            onAddFriendToChat = { friend ->
                scope.launch {
                    val tid = social.ensureThread(friend.id)
                    if (tid != null) {
                        social.loadThreads()
                        // Navigate to the new thread
                        activeThreadId = tid
                        activeOtherId = friend.id
                        activeOtherName = friend.display_name
                        activeOtherEmoji = friend.avatar_emoji
                    }
                }
            },
            onSend = {
                // Step 1: Check for self-harm phrases FIRST
                val selfHarmResult = ProfanityFilter.filterSelfHarm(replyBody)
                if (selfHarmResult.hasSelfHarm) {
                    selfHarmOffenseCount += 1
                    selfHarmFilteredText = selfHarmResult.filteredText
                    if (selfHarmOffenseCount >= 2 && auth.currentUserId != null) {
                        scope.launch {
                            ReportRepository.instance.reportUser(
                                reportedUserId = auth.currentUserId!!,
                                reason = "Self-harm language: ${selfHarmResult.matchedPhrases.joinToString(", ")}",
                                screenshotPath = null,
                                reporterName = null,
                                reportedName = profile.name,
                                reportedAvatar = profile.avatarEmoji,
                            )
                        }
                    }
                    showSelfHarmWarning = true
                    return@ThreadView
                }
                // Step 2: Regular profanity filter
                val result = ProfanityFilter.filterWithWarning(selfHarmResult.filteredText, groupStrict = false)
                val filtered = result.filteredText
                if (result.hasExplicitContent) {
                    pendingFilteredText = filtered
                    showProfanityWarning = true
                    val uid = auth.currentUserId
                    if (uid != null) {
                        scope.launch {
                            try {
                                val apiUrl = com.rork.rockscout.data.BuildSecrets.resolve("EXPO_PUBLIC_RORK_FUNCTIONS_URL", com.rork.rockscout.data.BuildSecrets.RORK_FUNCTIONS_URL)
                                val appKey = com.rork.rockscout.data.BuildSecrets.resolve("EXPO_PUBLIC_RORK_APP_KEY", com.rork.rockscout.data.BuildSecrets.RORK_APP_KEY)
                                val client = okhttp3.OkHttpClient()
                                val json = org.json.JSONObject().apply {
                                    put("userId", uid)
                                    put("reason", "Explicit language in private chat")
                                    put("source", "chat")
                                    put("sourceId", threadId)
                                }
                                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                                val req = okhttp3.Request.Builder()
                                    .url("$apiUrl/profanity-warning")
                                    .post(okhttp3.RequestBody.create(mediaType, json.toString()))
                                    .addHeader("Content-Type", "application/json")
                                    .addHeader("Authorization", "Bearer $appKey")
                                    .build()
                                client.newCall(req).execute()
                            } catch (_: Exception) { }
                        }
                    }
                } else if (filtered.isNotBlank() && threadId != null) {
                    // For private chats, tag the other user if their name appears
                    val taggedIds = activeOtherId?.let { oid ->
                        val otherName = activeOtherName ?: ""
                        if (replyBody.contains("@$otherName", ignoreCase = true)) listOf(oid) else emptyList()
                    } ?: emptyList()
                    scope.launch {
                        social.sendMessage(threadId, filtered, null, replyToMessageId, taggedIds)
                        replyBody = ""
                        replyToMessageId = null
                        replyToSenderName = null
                        replyToBody = null
                        ChatDraftStore.deleteDraft(threadId)
                    }
                }
            },
            onSendImage = { imageUri ->
                if (threadId != null) {
                    scope.launch { social.sendMessage(threadId, "", imageUri, replyToMessageId) }
                }
            },
            onBack = {
                if (replyBody.isNotBlank() && threadId != null) {
                    ChatDraftStore.saveDraft(threadId, activeOtherName ?: "RockScout", replyBody)
                }
                threadId?.let { scope.launch { SupabaseMessagingRepository.clearTypingStatus(it) } }
                activeThreadId = null
                activeOtherId = null
                replyBody = ""
                replyToMessageId = null
                replyToSenderName = null
                replyToBody = null
                scope.launch {
                    threadId?.let { social.markThreadRead(it) }
                    social.loadThreads()
                }
            },
            isGroupChat = false,
            replyToSenderName = replyToSenderName,
            replyToBody = replyToBody,
            onCancelReply = {
                replyToMessageId = null
                replyToSenderName = null
                replyToBody = null
            },
            onLongPressMessage = { msg ->
                replyToMessageId = msg.id
                val senderName = if (msg.sender_id == auth.currentUserId) {
                    AppRepository.instance.profile.value.name
                } else {
                    hunterCache[msg.sender_id]?.display_name ?: activeOtherName ?: "RockScout"
                }
                replyToSenderName = senderName
                replyToBody = msg.body.ifBlank { "[image]" }
                // Insert @username into the input box
                if (replyBody.isBlank() || !replyBody.contains("@$senderName")) {
                    replyBody = "@$senderName " + replyBody
                }
            },
            hunterCache = hunterCache,
        )
        if (showSelfHarmWarning) {
            SelfHarmWarningDialog(
                offenseCount = selfHarmOffenseCount,
                onConfirm = {
                    showSelfHarmWarning = false
                    val text = selfHarmFilteredText
                    if (text.isNotBlank() && threadId != null) {
                        val taggedIds = activeOtherId?.let { oid ->
                            val otherName = activeOtherName ?: ""
                            if (replyBody.contains("@$otherName", ignoreCase = true)) listOf(oid) else emptyList()
                        } ?: emptyList()
                        scope.launch {
                            social.sendMessage(threadId, text, null, replyToMessageId, taggedIds)
                            replyBody = ""
                            replyToMessageId = null
                            replyToSenderName = null
                            replyToBody = null
                            ChatDraftStore.deleteDraft(threadId)
                        }
                    }
                },
            )
        }
        if (showProfanityWarning) {
            ProfanityWarningDialog(
                onConfirm = {
                    showProfanityWarning = false
                    val text = pendingFilteredText
                    if (text.isNotBlank() && threadId != null) {
                        val taggedIds = activeOtherId?.let { oid ->
                            val otherName = activeOtherName ?: ""
                            if (replyBody.contains("@$otherName", ignoreCase = true)) listOf(oid) else emptyList()
                        } ?: emptyList()
                        scope.launch {
                            social.sendMessage(threadId, text, null, replyToMessageId, taggedIds)
                            replyBody = ""
                            replyToMessageId = null
                            replyToSenderName = null
                            replyToBody = null
                            ChatDraftStore.deleteDraft(threadId)
                        }
                    }
                },
            )
        }
        return
    }

    // Message request dialog — shown when a user taps "Message" on a
    // non-connected user's profile/card.
    requestTarget?.let { target ->
        if (showRequestDialog) {
            MessageRequestComposeDialog(
                target = target,
                body = requestBody,
                onBodyChange = { requestBody = it },
                sentFlag = requestSent,
                error = requestError,
                onSend = {
                    scope.launch {
                        social.sendRequest(
                            target.id,
                            requestBody.ifBlank { "Hi! Fellow RockScout here \u2014 want to connect?" },
                        ).onSuccess {
                            requestSent = target.id
                            requestError = null
                        }.onFailure {
                            requestError = it.message ?: "Could not send request."
                        }
                    }
                },
                onDismiss = {
                    showRequestDialog = false
                    requestTarget = null
                    requestBody = ""
                    requestSent = null
                    requestError = null
                    if (openThreadWith != null) {
                        navController.popBackStack()
                    }
                },
            )
        }
    }
}

/* ── ThreadView ──────────────────────────────────────────────────────────── */

@Composable
private fun ThreadView(
    otherName: String,
    otherEmoji: String,
    otherUserId: String?,
    messages: List<SocialRepository.MessageRow>,
    myUserId: String?,
    replyBody: String,
    onReplyChange: (String) -> Unit,
    onSend: () -> Unit,
    onSendImage: (String) -> Unit,
    onBack: () -> Unit,
    isGroupChat: Boolean = false,
    replyToSenderName: String? = null,
    replyToBody: String? = null,
    onCancelReply: () -> Unit = {},
    onLongPressMessage: (SocialRepository.MessageRow) -> Unit = {},
    hunterCache: Map<String, SocialRepository.HunterProfile> = emptyMap(),
    chatId: String = "",
    onOpenUserProfile: (String) -> Unit = {},
    friendsList: List<SocialRepository.HunterProfile> = emptyList(),
    onAddFriendToChat: (SocialRepository.HunterProfile) -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var showPreview by remember { mutableStateOf(false) }
    var imageModerating by remember { mutableStateOf(false) }
    var moderationError by remember { mutableStateOf<String?>(null) }
    var showImageSourcePicker by remember { mutableStateOf(false) }

    var scrollSpeed by remember { mutableStateOf("normal") } // normal, half, stop
    var isUserScrolling by remember { mutableStateOf(false) }
    var lastAutoScrollMs by remember { mutableStateOf(0L) }

    // ── Typing indicator state ──
    var typingUsers by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var lastTypingSent by remember { mutableStateOf(0L) }
    var wasTyping by remember { mutableStateOf(false) }

    // Poll typing status every 2.5 seconds while chat is open
    LaunchedEffect(chatId) {
        if (chatId.isNotBlank()) {
            while (true) {
                kotlinx.coroutines.delay(2500L)
                val result = SupabaseMessagingRepository.pollTypingStatus(chatId)
                if (result.isSuccess) {
                    val typingIds = result.getOrDefault(emptyMap()).keys.toList()
                    if (typingIds.isNotEmpty()) {
                        val names = SupabaseMessagingRepository.fetchProfileNames(typingIds)
                        typingUsers = names.filterKeys { it in typingIds }
                    } else {
                        typingUsers = emptyMap()
                    }
                }
            }
        }
    }

    // Send typing status when replyBody changes
    LaunchedEffect(replyBody) {
        if (chatId.isBlank()) return@LaunchedEffect
        val now = System.currentTimeMillis()
        if (replyBody.isNotBlank()) {
            if (!wasTyping || now - lastTypingSent > 3000L) {
                wasTyping = true
                lastTypingSent = now
                scope.launch { SupabaseMessagingRepository.setTypingStatus(chatId, true) }
            }
        } else if (wasTyping) {
            wasTyping = false
            scope.launch { SupabaseMessagingRepository.setTypingStatus(chatId, false) }
        }
    }

    // Clear typing status when leaving chat
    LaunchedEffect(Unit) {
        kotlinx.coroutines.awaitCancellation()
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            isUserScrolling = true
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri != null) {
            // Reject files larger than 5 MB before moderation to prevent
            // base64-encoding OOMs and failed uploads.
            if (ImageUtils.isOverUploadLimit(context, uri)) {
                moderationError = "That image is over 5 MB. Please choose a smaller photo."
                return@rememberLauncherForActivityResult
            }
            imageModerating = true
            moderationError = null
            scope.launch {
                val base64 = ImageUtils.uriToModerationBase64(context, uri)
                if (base64 == null) {
                    imageModerating = false
                    moderationError = "Could not load image."
                    return@launch
                }
                val verdict = ImageModerator.scan(base64, "image/jpeg")
                imageModerating = false
                when (verdict.triState) {
                    ModerationTriState.CLEAN -> {
                        val persistent = ImageUtils.copyUriToInternalStorage(context, uri, "message_images")
                        if (persistent != null) {
                            onSendImage(persistent)
                            moderationError = null
                        } else {
                            moderationError = "Could not save image."
                        }
                    }
                    ModerationTriState.EXPLICIT -> {
                        moderationError = verdict.reason.ifBlank { "This image can't be sent because it violates our family-friendly policies." }
                    }
                    ModerationTriState.QUESTIONABLE -> {
                        val persistent = ImageUtils.copyUriToInternalStorage(context, uri, "message_images")
                        val userId = AuthRepository.instance.currentUserId
                        val userName = AppRepository.instance.profile.value.name
                        val avatar = AppRepository.instance.profile.value.avatarEmoji
                        ImageReviewRepository.instance.submitReview(
                            userId = userId ?: "unknown",
                            userName = userName,
                            userAvatar = avatar,
                            imageUri = persistent ?: uri.toString(),
                            type = "message_image",
                            reason = verdict.reason,
                        )
                        if (persistent != null) {
                            onSendImage(persistent)
                            moderationError = null
                        } else {
                            moderationError = "Could not save image."
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty() && scrollSpeed != "stop" && !isUserScrolling) {
            val now = System.currentTimeMillis()
            val delayMs = if (scrollSpeed == "half") 4000L else 0L
            if (now - lastAutoScrollMs >= delayMs) {
                listState.animateScrollToItem(messages.lastIndex)
                lastAutoScrollMs = now
            }
        }
    }


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
            // Header with close button (X) in top-right
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
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = DarkTextHigh)
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Citrine.copy(alpha = 0.45f), Aqua.copy(alpha = 0.25f)))),
                    contentAlignment = Alignment.Center,
                ) { Text(otherEmoji, style = MaterialTheme.typography.titleMedium) }
                Spacer(Modifier.width(10.dp))
                Text(
                    otherName,
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            enabled = !isGroupChat && otherUserId != null,
                            onClick = { otherUserId?.let { onOpenUserProfile(it) } },
                        ),
                )
                Spacer(Modifier.width(8.dp))
                SculptedOutlinedButton(
                    text = "Preview",
                    onClick = { showPreview = true },
                    accent = Aqua,
                    textColor = Aqua,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                )
                // Add User button (private chats only, max 10 users)
                if (!isGroupChat && otherUserId != null) {
                    var showFriendPicker by remember { mutableStateOf(false) }
                    SculptedIconButton(
                        icon = Icons.Filled.PersonAdd,
                        contentDescription = "Add user to chat",
                        onClick = { showFriendPicker = true },
                        accent = Aqua,
                        iconTint = Aqua,
                        size = 40.dp,
                        shadowElevation = 3.dp,
                    )
                    Spacer(Modifier.width(6.dp))
                    if (showFriendPicker) {
                        FriendPickerDialog(
                            friends = friendsList.filter { it.id != otherUserId && it.id != myUserId },
                            onDismiss = { showFriendPicker = false },
                            onSelect = { friend ->
                                showFriendPicker = false
                                onAddFriendToChat(friend)
                            },
                        )
                    }
                }
                if (otherUserId != null) {
                    var showReportConfirm by remember { mutableStateOf(false) }
                    var showReportDialog by remember { mutableStateOf(false) }
                    var reportSubmitted by remember { mutableStateOf(false) }
                    val reportScope = rememberCoroutineScope()
                    val reportContext = LocalContext.current
                    val reportView = androidx.compose.ui.platform.LocalView.current
                    val reportRepo = AppRepository.instance
                    val myProf by reportRepo.profile.collectAsStateWithLifecycle()
                    SculptedIconButton(icon = Icons.Filled.Flag, contentDescription = "Report", onClick = { showReportConfirm = true }, accent = Danger, iconTint = DarkTextMid, size = 40.dp, shadowElevation = 3.dp)
                    if (showReportConfirm) {
                        androidx.compose.material3.AlertDialog(
                            onDismissRequest = { showReportConfirm = false },
                            title = { Text("Report this user?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
                            text = {
                                Text(
                                    "A screenshot will be captured and you'll be asked to send a report email. Only continue if you believe this user violated our community guidelines.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DarkTextMid,
                                )
                            },
                            confirmButton = {
                                SculptedButton(
                                    text = "Continue",
                                    onClick = {
                                        showReportConfirm = false
                                        showReportDialog = true
                                    },
                                    accent = Danger,
                                    containerColor = Danger,
                                    textColor = Color.White,
                                )
                            },
                            dismissButton = {
                                SculptedTextButton(text = "Cancel", onClick = { showReportConfirm = false }, accent = DarkTextMid, textColor = DarkTextMid)
                            },
                            containerColor = Color(0xFF1E1C16),
                            titleContentColor = DarkTextHigh,
                            textContentColor = DarkTextMid,
                        )
                    }
                    if (showReportDialog) {
                        androidx.compose.material3.AlertDialog(
                            onDismissRequest = { showReportDialog = false },
                            title = { Text("Report $otherName?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
                            text = {
                                Text(
                                    "Help keep RockScout safe and family-friendly. Report this user for inappropriate behavior, profanity, or content that violates our community guidelines.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DarkTextMid,
                                )
                            },
                            confirmButton = {
                                SculptedButton(
                                    text = "Report",
                                    onClick = {
                                        reportScope.launch {
                                            val reportId = "report-${System.currentTimeMillis()}"
                                            val screenshotPath = ReportScreenshotHelper.captureScreenshot(
                                                reportContext, reportView, reportId,
                                            )
                                            ReportRepository.instance.reportUser(
                                                reportedUserId = otherUserId,
                                                reason = "Inappropriate behavior",
                                                screenshotPath = screenshotPath,
                                                reporterName = myProf.name,
                                                reportedName = otherName,
                                                reportedAvatar = null,
                                            ).onSuccess {
                                                reportSubmitted = true
                                                val intent = ReportScreenshotHelper.buildEmailIntent(
                                                    context = reportContext,
                                                    reportedUserName = otherName,
                                                    reporterUserName = myProf.name,
                                                    reason = "Inappropriate behavior",
                                                    timestamp = System.currentTimeMillis(),
                                                    screenshotPath = screenshotPath,
                                                )
                                                ReportScreenshotHelper.launchEmailComposer(reportContext, intent)
                                            }
                                        }
                                        showReportDialog = false
                                    },
                                    accent = Danger,
                                    containerColor = Danger,
                                    textColor = Color.White,
                                )
                            },
                            dismissButton = {
                                SculptedTextButton(text = "Cancel", onClick = { showReportDialog = false }, accent = DarkTextMid, textColor = DarkTextMid)
                            },
                            containerColor = Color(0xFF1E1C16),
                            titleContentColor = DarkTextHigh,
                            textContentColor = DarkTextMid,
                        )
                    }
                    if (reportSubmitted) {
                        ReportSubmittedDialog(onDismiss = { reportSubmitted = false })
                    }
                }
            }
            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)

            // Messages
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (messages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "Say hi! This is the start of your conversation.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextMid,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
                items(messages, key = { it.id }) { msg ->
                    val isMe = msg.sender_id == myUserId
                    // Resolve reply-to sender name for display
                    val replySenderName = if (msg.reply_to_message_id != null) {
                        val repliedMsg = messages.firstOrNull { it.id == msg.reply_to_message_id }
                        repliedMsg?.sender_id?.let { rid ->
                            if (rid == myUserId) {
                                AppRepository.instance.profile.value.name
                            } else {
                                hunterCache[rid]?.display_name ?: otherName
                            }
                        }
                    } else null
                    val replyBodyText = if (msg.reply_to_message_id != null) {
                        messages.firstOrNull { it.id == msg.reply_to_message_id }?.body?.ifBlank { "[image]" }
                    } else null
                    // Resolve tagged user names from IDs
                    val taggedNames = msg.tagged_user_ids.mapNotNull { tid ->
                        if (tid == myUserId) {
                            AppRepository.instance.profile.value.name
                        } else {
                            hunterCache[tid]?.display_name
                        }
                    }
                    // Sender display name for long-press profile
                    val senderDisplayName = if (isMe) {
                        AppRepository.instance.profile.value.name
                    } else {
                        hunterCache[msg.sender_id]?.display_name ?: otherName
                    }
                    Box(
                        modifier = Modifier.combinedClickable(
                            onClick = {},
                            onLongClick = {
                                // Long-press opens user profile (not self)
                                if (!isMe && msg.sender_id.isNotBlank()) {
                                    onOpenUserProfile(msg.sender_id)
                                } else {
                                    onLongPressMessage(msg)
                                }
                            },
                        ),
                    ) {
                        ChatBubble(
                            text = msg.body,
                            timestamp = formatTime(msg.created_at),
                            isMe = isMe,
                            imageUri = msg.image_uri,
                            isRead = msg.read_at != null,
                            replyToSenderName = replySenderName,
                            replyToBody = replyBodyText,
                            taggedUserNames = taggedNames,
                            senderName = if (isGroupChat && !isMe) senderDisplayName else null,
                            senderEmoji = if (isGroupChat && !isMe) hunterCache[msg.sender_id]?.avatar_emoji else null,
                        )
                    }
                }
                // Typing indicator at the bottom of the message list
                if (typingUsers.isNotEmpty()) {
                    item {
                        TypingIndicatorRow(typingNames = typingUsers.values.toList())
                    }
                }
            }

            // Reply bar
            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)
            // Reply-to preview bar
            if (replyToSenderName != null && replyToBody != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2A2820))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Replying to $replyToSenderName",
                            style = MaterialTheme.typography.labelMedium,
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            replyToBody.take(60) + if (replyToBody.length > 60) "\u2026" else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextMid,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Cancel reply",
                        tint = DarkTextMid,
                        modifier = Modifier.size(18.dp).clickable(onClick = onCancelReply),
                    )
                }
            }
            // Scroll speed controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // Speed control icons
                SpeedControlButton(
                    label = "Normal",
                    isActive = scrollSpeed == "normal",
                    onClick = { scrollSpeed = "normal"; isUserScrolling = false },
                )
                SpeedControlButton(
                    label = "Half",
                    isActive = scrollSpeed == "half",
                    onClick = { scrollSpeed = "half"; isUserScrolling = false },
                )
                SpeedControlButton(
                    label = "Stop",
                    isActive = scrollSpeed == "stop",
                    onClick = { scrollSpeed = "stop" },
                )
                Spacer(Modifier.weight(1f))
                // Current button — jump to newest messages
                if (isUserScrolling || scrollSpeed == "stop") {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Aqua.copy(alpha = 0.18f))
                            .glowingBorder(1.dp, Aqua.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .clickable {
                                isUserScrolling = false
                                if (messages.isNotEmpty()) {
                                    scope.launch { listState.animateScrollToItem(messages.lastIndex) }
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.FastForward, contentDescription = "Jump to latest", tint = Aqua, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Current", style = MaterialTheme.typography.labelSmall, color = Aqua, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3A3830))
                            .glowingBorder(1.dp, Color(0xFF3A3830).copy(alpha = 0.35f), CircleShape)
                            .clickable(enabled = !imageModerating) { galleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Image,
                            contentDescription = "Send image",
                            tint = if (!imageModerating) Citrine else DarkTextMid,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3A3830))
                            .glowingBorder(1.dp, Color(0xFF3A3830).copy(alpha = 0.35f), CircleShape)
                            .clickable(enabled = !imageModerating) { showImageSourcePicker = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Download,
                            contentDescription = "Send saved image",
                            tint = if (!imageModerating) Citrine else DarkTextMid,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = replyBody,
                        onValueChange = onReplyChange,
                        modifier = Modifier.weight(1f).noAutoFocus(),
                        placeholder = { Text("Message\u2026", color = DarkTextMid) },
                        maxLines = 4,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF3A3830),
                            unfocusedContainerColor = Color(0xFF3A3830),
                            focusedTextColor = DarkTextHigh,
                            unfocusedTextColor = DarkTextHigh,
                            focusedIndicatorColor = Citrine,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Citrine,
                        ),
                        shape = RoundedCornerShape(16.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (replyBody.isNotBlank()) Citrine else Color(0xFF3A3830))
                            .clickable(enabled = replyBody.isNotBlank(), onClick = onSend),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (replyBody.isNotBlank()) Ink else DarkTextMid,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
                if (imageModerating) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Aqua,
                            strokeWidth = 2.dp,
                        )
                        Text(
                            "Checking image\u2026",
                            style = MaterialTheme.typography.bodySmall,
                            color = Aqua,
                        )
                    }
                }
                moderationError?.let { error ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        error,
                        style = MaterialTheme.typography.bodySmall,
                        color = Danger,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
    if (showPreview) {
        ThreadPreviewOverlay(
            otherName = otherName,
            otherEmoji = otherEmoji,
            messages = messages,
            myUserId = myUserId,
            onClose = { showPreview = false },
        )
    }
    if (showImageSourcePicker) {
        ImageSourcePickerDialog(
            onDismiss = { showImageSourcePicker = false },
            onImageSelected = { uri ->
                showImageSourcePicker = false
                scope.launch {
                    imageModerating = true
                    val path = processImageUri(context, uri, "message_images", "message_image")
                    imageModerating = false
                    if (path != null) onSendImage(path)
                }
            },
        )
    }
}

/* ── Scroll speed control button ──────────────────────────────────────────── */

@Composable
private fun SpeedControlButton(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    val accent = if (isActive) Aqua else DarkTextMid
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isActive) Aqua.copy(alpha = 0.15f) else Color.Transparent)
            .glowingBorder(1.dp, if (isActive) Aqua.copy(alpha = 0.5f) else Color.Transparent, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

/* ── Profanity warning dialog ─────────────────────────────────────────────── */

@Composable
private fun ProfanityWarningDialog(
    onConfirm: () -> Unit,
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onConfirm,
        containerColor = Color(0xFF1E1C16),
        titleContentColor = Danger,
        textContentColor = DarkTextMid,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = Danger, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text("Content Warning", color = Danger, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Text(
                "Your message contains language that was censored. If you believe we're censoring a word by mistake, email support@rockscout.net to get it cleared up.",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
            )
        },
        confirmButton = {
            com.rork.rockscout.ui.components.SculptedButton(
                text = "OK",
                onClick = onConfirm,
                accent = Danger,
                containerColor = Danger,
                textColor = Color.White,
            )
        },
    )
}

/* ── Self-harm warning dialog ────────────────────────────────────────────── */

@Composable
private fun SelfHarmWarningDialog(
    offenseCount: Int,
    onConfirm: () -> Unit,
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onConfirm,
        containerColor = Color(0xFF1E1C16),
        titleContentColor = Danger,
        textContentColor = DarkTextMid,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = Danger, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text("Self-Harm Language Detected", color = Danger, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    "Your message contains language related to self-harm. This is a serious violation of our community guidelines. The phrase has been censored.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
                if (offenseCount >= 2) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "This is your ${offenseCount}th offense. An automatic report has been filed and you will be notified via email and notifications.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Danger,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "If you or someone you know is struggling, please contact the 988 Suicide & Crisis Lifeline by dialing 988.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                )
            }
        },
        confirmButton = {
            com.rork.rockscout.ui.components.SculptedButton(
                text = "I Understand",
                onClick = onConfirm,
                accent = Danger,
                containerColor = Danger,
                textColor = Color.White,
            )
        },
    )
}

/* ── Thread preview overlay ──────────────────────────────────────────────── */

@Composable
private fun ThreadPreviewOverlay(
    otherName: String,
    otherEmoji: String,
    messages: List<SocialRepository.MessageRow>,
    myUserId: String?,
    onClose: () -> Unit,
) {
    BackHandler { onClose() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xE6000000)),
    ) {
        Column(Modifier.fillMaxSize()) {
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
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close preview", tint = DarkTextHigh)
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Citrine.copy(alpha = 0.45f), Aqua.copy(alpha = 0.25f)))),
                    contentAlignment = Alignment.Center,
                ) { Text(otherEmoji, style = MaterialTheme.typography.titleMedium) }
                Spacer(Modifier.width(10.dp))
                Text(
                    otherName,
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "Preview",
                    style = MaterialTheme.typography.labelMedium,
                    color = Aqua,
                    fontWeight = FontWeight.Bold,
                )
            }
            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(messages, key = { it.id }) { msg ->
                    val isMe = msg.sender_id == myUserId
                    ChatBubble(
                        text = msg.body,
                        timestamp = formatTime(msg.created_at),
                        isMe = isMe,
                        imageUri = msg.image_uri,
                        isRead = msg.read_at != null,
                    )
                }
            }
        }
    }
}

/* ── RequestChatView (full-screen message request chat) ──────────────────── */

@Composable
fun RequestChatView(
    senderName: String,
    senderEmoji: String,
    body: String,
    timestamp: String,
    onBack: () -> Unit,
    onAccept: () -> Unit,
    onDeny: () -> Unit,
    onBlock: () -> Unit,
    senderId: String = "",
    onOpenUserProfile: ((String) -> Unit)? = null,
    navController: NavController? = null,
) {
    var showBlockConfirm by remember { mutableStateOf(false) }
    var showReportConfirm by remember { mutableStateOf(false) }
    var reportSubmitted by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val view = androidx.compose.ui.platform.LocalView.current
    val reportScope = rememberCoroutineScope()
    val reportRepo = AppRepository.instance
    val myProf by reportRepo.profile.collectAsStateWithLifecycle()
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
            // Header with back arrow + sender name
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
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkTextHigh)
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Citrine.copy(alpha = 0.45f), Aqua.copy(alpha = 0.25f)))),
                    contentAlignment = Alignment.Center,
                ) { Text(senderEmoji, style = MaterialTheme.typography.titleMedium) }
                Spacer(Modifier.width(10.dp))
                Text(
                    senderName,
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            enabled = senderId.isNotBlank() && onOpenUserProfile != null,
                            onClick = { if (senderId.isNotBlank()) onOpenUserProfile?.invoke(senderId) },
                        ),
                )
            }
            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)

            // Messages area
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "\u2709\uFE0F Message Request",
                            style = MaterialTheme.typography.labelMedium,
                            color = Aqua,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                item {
                    ChatBubble(
                        text = body.ifBlank { "Hi! Fellow RockScout here \u2014 want to connect?" },
                        timestamp = timestamp,
                        isMe = false,
                    )
                }
            }

            // Action pills at the bottom
            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SculptedOutlinedButton(
                    text = "Deny",
                    onClick = onDeny,
                    modifier = Modifier.weight(1f),
                    accent = Danger,
                    textColor = DarkTextMid,
                    icon = Icons.Filled.Close,
                )
                SculptedOutlinedButton(
                    text = "Block",
                    onClick = { showBlockConfirm = true },
                    modifier = Modifier.weight(1f),
                    accent = Danger,
                    textColor = Danger,
                    icon = Icons.Filled.Block,
                )
                SculptedOutlinedButton(
                    text = "Report",
                    onClick = { showReportConfirm = true },
                    modifier = Modifier.weight(1f),
                    accent = Danger,
                    textColor = Danger,
                    icon = Icons.Filled.Flag,
                )
                SculptedButton(
                    text = "Accept",
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    accent = Success,
                    containerColor = Success,
                    textColor = Ink,
                    icon = Icons.Filled.Check,
                )
            }
        }
    }
    if (showBlockConfirm) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showBlockConfirm = false },
            title = { Text("Block $senderName?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Blocking prevents them from sending you message or friend requests. This cannot be undone from their side.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                SculptedButton(
                    text = "Confirm Block",
                    onClick = {
                        showBlockConfirm = false
                        onBlock()
                    },
                    accent = Danger,
                    containerColor = Danger,
                    textColor = Color.White,
                    icon = Icons.Filled.Block,
                )
            },
            dismissButton = {
                SculptedTextButton(text = "Cancel", onClick = { showBlockConfirm = false }, accent = DarkTextMid, textColor = DarkTextMid)
            },
            containerColor = Color(0xFF1E1C16),
            titleContentColor = DarkTextHigh,
            textContentColor = DarkTextMid,
        )
    }
    if (showReportConfirm) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showReportConfirm = false },
            title = { Text("Report $senderName?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "A screenshot will be captured and you'll be asked to send a report email. Only continue if you believe this user violated our community guidelines.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                SculptedButton(
                    text = "Continue",
                    onClick = {
                        showReportConfirm = false
                        reportScope.launch {
                            val reportId = "report-${System.currentTimeMillis()}"
                            val screenshotPath = ReportScreenshotHelper.captureScreenshot(
                                context, view, reportId,
                            )
                            ReportRepository.instance.reportUser(
                                reportedUserId = senderId,
                                reason = "Inappropriate behavior",
                                screenshotPath = screenshotPath,
                                reporterName = myProf.name,
                                reportedName = senderName,
                                reportedAvatar = null,
                            ).onSuccess {
                                reportSubmitted = true
                                val intent = ReportScreenshotHelper.buildEmailIntent(
                                    context = context,
                                    reportedUserName = senderName,
                                    reporterUserName = myProf.name,
                                    reason = "Inappropriate behavior",
                                    timestamp = System.currentTimeMillis(),
                                    screenshotPath = screenshotPath,
                                )
                                ReportScreenshotHelper.launchEmailComposer(context, intent)
                            }
                        }
                    },
                    accent = Danger,
                    containerColor = Danger,
                    textColor = Color.White,
                    icon = Icons.Filled.Flag,
                )
            },
            dismissButton = {
                SculptedTextButton(text = "Cancel", onClick = { showReportConfirm = false }, accent = DarkTextMid, textColor = DarkTextMid)
            },
            containerColor = Color(0xFF1E1C16),
            titleContentColor = DarkTextHigh,
            textContentColor = DarkTextMid,
        )
    }
    if (reportSubmitted) {
        ReportSubmittedDialog(onDismiss = { reportSubmitted = false })
    }
}

/* ── ChatBubble ──────────────────────────────────────────────────────────── */

@Composable
fun ChatBubble(
    text: String,
    timestamp: String,
    isMe: Boolean,
    imageUri: String? = null,
    isRead: Boolean = false,
    replyToSenderName: String? = null,
    replyToBody: String? = null,
    taggedUserNames: List<String> = emptyList(),
    senderName: String? = null,
    senderEmoji: String? = null,
    navController: NavController? = null,
) {
    val bubbleBg = if (isMe) MyBubbleBg else OtherBubbleBg
    val bubbleShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomEnd = if (isMe) 4.dp else 16.dp,
        bottomStart = if (isMe) 16.dp else 4.dp,
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(bubbleShape)
                .background(bubbleBg)
                .glowingBorder(1.dp, (if (isMe) Citrine else Aqua).copy(alpha = 0.35f), bubbleShape)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Column {
                // Sender name (group chats only, for other users' messages)
                if (senderName != null && senderEmoji != null) {
                    Text(
                        "$senderEmoji $senderName",
                        style = MaterialTheme.typography.labelSmall,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                }
                // Reply threading — show original comment above the reply
                if (replyToSenderName != null && replyToBody != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1A1812))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Text(
                            "$replyToSenderName replied to your comment",
                            style = MaterialTheme.typography.labelSmall,
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            replyToBody.take(80) + if (replyToBody.length > 80) "\u2026" else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextMid,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                }
                if (!imageUri.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Shared image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    if (text.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                    }
                }
                if (text.isNotBlank()) {
                    // Render text with tagged user pills and clickable deep links inline
                    val ctx = LocalContext.current
                    val nav = navController
                    val linkScope = rememberCoroutineScope()
                    ClickableMessageText(
                        text = text,
                        taggedUserNames = taggedUserNames,
                        onDeepLinkClick = { deepLinkUri ->
                            nav?.let { handlePingDeepLink(deepLinkUri, ctx, it, linkScope) }
                        },
                    )
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = DarkTextMid,
            )
            if (isMe) {
                Spacer(Modifier.width(4.dp))
                if (isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Success)
                            .glowingBorder(1.dp, Success.copy(alpha = 0.35f), CircleShape),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Read",
                        style = MaterialTheme.typography.labelSmall,
                        color = Success,
                        fontWeight = FontWeight.Bold,
                    )
                } else {
                    Text(
                        "Sent",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextMid,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

/* ── Typing indicator row ─────────────────────────────────────────────────── */

@Composable
private fun TypingIndicatorRow(typingNames: List<String>) {
    val label = when {
        typingNames.size == 1 -> "${typingNames[0]} is typing…"
        typingNames.size == 2 -> "${typingNames[0]} and ${typingNames[1]} are typing…"
        typingNames.size > 2 -> "${typingNames[0]} and ${typingNames.size - 1} others are typing…"
        else -> "Someone is typing…"
    }
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "dot_alpha",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Pulsing dots
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            repeat(3) { i ->
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(Aqua.copy(alpha = dotAlpha)),
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Aqua,
            fontWeight = FontWeight.Medium,
        )
    }
}

/* ── Locked state ────────────────────────────────────────────────────────── */

@Composable
private fun MessengerLockedState(
    emoji: String,
    title: String,
    message: String,
    buttonLabel: String,
    onButton: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(emoji, style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleLarge, color = DarkTextHigh, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid, textAlign = TextAlign.Center)
            Spacer(Modifier.height(20.dp))
            SculptedButton(
                text = buttonLabel,
                onClick = onButton,
                accent = Citrine,
                containerColor = Citrine,
                textColor = Ink,
            )
        }
    }
}

/* ── Message request compose dialog ──────────────────────────────────────── */

@Composable
private fun MessageRequestComposeDialog(
    target: SocialRepository.HunterProfile,
    body: String,
    onBodyChange: (String) -> Unit,
    sentFlag: String?,
    error: String?,
    onSend: () -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        DarkCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clickable(enabled = false) {},
            accent = Citrine,
        ) {
            Column(modifier = Modifier.fillMaxWidth().imePadding()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(target.avatar_emoji, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Message ${target.display_name}", style = MaterialTheme.typography.titleMedium, color = DarkTextHigh, fontWeight = FontWeight.Bold)
                        Text("They'll get a request to confirm. If they accept, you two can chat.", style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .sculpted(
                                shape = CircleShape,
                                accent = Citrine,
                                shadowElevation = 3.dp,
                                circular = true,
                                onClick = onDismiss,
                            )
                            .clip(CircleShape)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.height(14.dp))
                if (sentFlag == target.id) {
                    Text(
                        "Request sent! They'll get a popup to confirm, deny, or block. If they accept, you'll be connected and can chat.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Success,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(12.dp))
                    SculptedButton(
                        text = "Done",
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        accent = Citrine,
                        containerColor = Citrine,
                        textColor = Ink,
                    )
                } else {
                    OutlinedTextField(
                        value = body,
                        onValueChange = onBodyChange,
                        modifier = Modifier.fillMaxWidth().noAutoFocus(),
                        placeholder = { Text("Hi! Fellow RockScout here \u2014 want to connect?") },
                        minLines = 3,
                        maxLines = 5,
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
                    if (error != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            error,
                            style = MaterialTheme.typography.bodySmall,
                            color = Danger,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    SculptedButton(
                        text = "Send message request",
                        onClick = onSend,
                        modifier = Modifier.fillMaxWidth(),
                        accent = Aqua,
                        containerColor = Aqua,
                        textColor = Ink,
                        icon = Icons.AutoMirrored.Filled.Send,
                    )
                }
            }
        }
    }
}

/* ── Tagged text renderer ───────────────────────────────────────────────── */

/**
 * Renders chat text with tagged usernames wrapped in bright Citrine pills.
 * When a tagged name is found in the text, it's rendered as a pill with
 * the username in dark text on a Citrine background.
 */
@Composable
private fun ClickableMessageText(
    text: String,
    taggedUserNames: List<String>,
    onDeepLinkClick: (android.net.Uri) -> Unit,
) {
    // Find all tagged names and deep links in the text and split into segments.
    // Each segment is (content, type) where type is "plain", "tag", or "link".
    val segments = mutableListOf<Pair<String, String>>()
    var remaining = text
    while (remaining.isNotEmpty()) {
        var earliestMatch: Triple<String, Int, String>? = null

        // Check for tagged names
        for (name in taggedUserNames) {
            val idx = remaining.indexOf(name, ignoreCase = true)
            if (idx >= 0 && (earliestMatch == null || idx < earliestMatch.second)) {
                earliestMatch = Triple(name, idx, "tag")
            }
        }
        // Check for deep links (rockscout:// or https://)
        val deepLinkRegex = Regex("(rockscout://[^\\s]+|https?://[^\\s]+)")
        deepLinkRegex.find(remaining)?.let { match ->
            val idx = match.range.first
            if (earliestMatch == null || idx < earliestMatch.second) {
                earliestMatch = Triple(match.value, idx, "link")
            }
        }

        if (earliestMatch == null) {
            segments.add(remaining to "plain")
            break
        }
        val (content, idx, type) = earliestMatch
        if (idx > 0) segments.add(remaining.substring(0, idx) to "plain")
        segments.add(remaining.substring(idx, idx + content.length) to type)
        remaining = remaining.substring(idx + content.length)
    }
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        for ((segment, type) in segments) {
            when (type) {
                "tag" -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Citrine)
                            .padding(horizontal = 6.dp, vertical = 1.dp),
                    ) {
                        Text(
                            segment,
                            style = MaterialTheme.typography.bodySmall,
                            color = Ink,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                "link" -> {
                    val uri = runCatching { android.net.Uri.parse(segment) }.getOrNull()
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Aqua.copy(alpha = 0.18f))
                            .glowingBorder(1.dp, Aqua.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .then(
                                if (uri != null) Modifier.clickable { onDeepLinkClick(uri) } else Modifier
                            )
                            .padding(horizontal = 6.dp, vertical = 1.dp),
                    ) {
                        Text(
                            segment,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Aqua,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                else -> Text(segment, style = MaterialTheme.typography.bodyMedium, color = DarkTextHigh)
            }
        }
    }
}

/** Handle a rockscout://ping deep link tapped inside a chat bubble. */
private fun handlePingDeepLink(
    uri: android.net.Uri,
    context: android.content.Context,
    navController: NavController,
    scope: kotlinx.coroutines.CoroutineScope,
) {
    if (uri.host != "ping") return
    val coordSegment = uri.pathSegments.firstOrNull()
    val parts = coordSegment?.split(',')
    if (parts?.size == 2) {
        val lat = parts[0].toDoubleOrNull()
        val lng = parts[1].toDoubleOrNull()
        if (lat != null && lng != null) {
            val label = uri.getQueryParameter("label") ?: "Shared ping"
            val from = uri.getQueryParameter("from") ?: "A fellow hunter"
            val fromId = uri.getQueryParameter("fromId")
            scope.launch {
                SocialRepository.instance.addSharedPing(lat, lng, label, fromId, from)
            }
            navController.navigate(Routes.ROCKSCOUTS_MAP)
            return
        }
    }
    // Fallback for generic web links
    SafeLinkOpener.launch(context, android.content.Intent(Intent.ACTION_VIEW, uri), "No app can open this link.")
}

/* ── Helpers ──────────────────────────────────────────────────────────────── */

private val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

private fun parseIso(ts: String): Long = runCatching { isoFormatter.parse(ts)?.time ?: 0L }.getOrDefault(0L)

private fun formatRelative(ts: String): String {
    val t = parseIso(ts)
    if (t == 0L) return ""
    val diff = System.currentTimeMillis() - t
    val mins = diff / 60_000
    return when {
        mins < 1 -> "just now"
        mins < 60 -> "${mins}m ago"
        mins < 60 * 24 -> "${mins / 60}h ago"
        mins < 60 * 24 * 7 -> "${mins / (60 * 24)}d ago"
        else -> SimpleDateFormat("MMM d", Locale.US).format(Date(t))
    }
}

private fun formatTime(ts: String): String {
    val t = parseIso(ts)
    if (t == 0L) return ""
    return SimpleDateFormat("h:mm a", Locale.US).format(Date(t))
}

/**
 * Parse @username patterns from the input text and match them against known
 * member user IDs. Returns the list of user IDs that were tagged.
 */
private fun parseTaggedUserIds(
    text: String,
    memberIds: List<String>,
    hunterCache: Map<String, SocialRepository.HunterProfile>,
): List<String> {
    if (memberIds.isEmpty() || text.isBlank()) return emptyList()
    val tagged = mutableListOf<String>()
    for (userId in memberIds) {
        val name = hunterCache[userId]?.display_name ?: continue
        val pattern = "@$name"
        if (text.contains(pattern, ignoreCase = true)) {
            tagged.add(userId)
        }
    }
    return tagged.distinct()
}

/**
 * Friend picker dialog — shows a searchable list of friends to invite into
 * a private chat. Tapping a friend opens a new thread with them.
 */
@Composable
private fun FriendPickerDialog(
    friends: List<SocialRepository.HunterProfile>,
    onDismiss: () -> Unit,
    onSelect: (SocialRepository.HunterProfile) -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(friends, searchQuery) {
        if (searchQuery.isBlank()) friends
        else friends.filter { it.display_name.contains(searchQuery, ignoreCase = true) }
    }
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add a friend to chat", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                if (friends.isEmpty()) {
                    Text(
                        "You don't have any friends yet. Send a friend request from a hunter's profile to start connecting!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                    )
                } else {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search friends...", color = TextMid) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = DarkTextHigh,
                            unfocusedTextColor = DarkTextHigh,
                            cursorColor = Aqua,
                            focusedIndicatorColor = Aqua,
                            unfocusedIndicatorColor = Color(0x33FFFFFF),
                        ),
                    )
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(filtered, key = { it.id }) { friend ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSelect(friend) }
                                    .padding(horizontal = 8.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(Citrine.copy(alpha = 0.3f), Aqua.copy(alpha = 0.2f)))),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(friend.avatar_emoji, style = MaterialTheme.typography.titleSmall)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        friend.display_name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = DarkTextHigh,
                                        fontWeight = FontWeight.Medium,
                                    )
                                    if (friend.status.isNotBlank() && friend.status != "off") {
                                        Text(
                                            friend.status.replace("-", " ").replaceFirstChar { it.uppercase() },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMid,
                                        )
                                    }
                                }
                                Icon(
                                    Icons.Filled.PersonAdd,
                                    contentDescription = null,
                                    tint = Aqua,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            SculptedTextButton(text = "Cancel", onClick = onDismiss, accent = DarkTextMid, textColor = DarkTextMid)
        },
        containerColor = Color(0xFF1E1C16),
        titleContentColor = DarkTextHigh,
        textContentColor = DarkTextMid,
    )
}

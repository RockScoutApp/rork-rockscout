package com.rork.rockscout.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.ReportSubmittedDialog
import com.rork.rockscout.ui.components.SculptedButton
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Mail

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
    val isPremium by purchaseManager.isPremium.collectAsStateWithLifecycle()
    val clubLocked = remember(isPremium) {
        accessManager.isFeatureLocked(isPremium)
    }

    val incomingRequests by social.incomingRequests.collectAsStateWithLifecycle()
    val threads by social.threads.collectAsStateWithLifecycle()
    val messages by social.messages.collectAsStateWithLifecycle()
    val connections by social.connections.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    var activeThreadId by remember { mutableStateOf<String?>(null) }
    var activeOtherId by remember { mutableStateOf<String?>(null) }
    var activeOtherName by remember { mutableStateOf<String?>(null) }
    var activeOtherEmoji by remember { mutableStateOf<String?>(null) }
    var replyBody by remember { mutableStateOf("") }
    var hunterCache by remember { mutableStateOf<Map<String, SocialRepository.HunterProfile>>(emptyMap()) }
    var activeRequestId by remember { mutableStateOf<String?>(openRequestId) }

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

    // If opened with a target user, open the thread directly (if connected)
    // or show a message request dialog (if not connected).
    LaunchedEffect(openThreadWith, threads, connections) {
        if (openThreadWith != null && activeThreadId == null && !showRequestDialog) {
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
                popUpTo(Routes.MESSENGER) { inclusive = true }
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
        )
        return
    }

    // Thread view (when a thread is open).
    if (activeThreadId != null && activeOtherId != null) {
        val threadId = activeThreadId
        BackHandler {
            activeThreadId = null
            activeOtherId = null
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
            onSend = {
                val filtered = ProfanityFilter.filter(replyBody)
                val tid = activeThreadId
                if (filtered.isNotBlank() && tid != null) {
                    scope.launch {
                        social.sendMessage(tid, filtered)
                        replyBody = ""
                    }
                }
            },
            onSendImage = { imageUri ->
                val tid = activeThreadId
                if (tid != null) {
                    scope.launch { social.sendMessage(tid, "", imageUri) }
                }
            },
            onBack = {
                activeThreadId = null
                activeOtherId = null
                scope.launch {
                    threadId?.let { social.markThreadRead(it) }
                    social.loadThreads()
                }
            },
        )
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
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var showPreview by remember { mutableStateOf(false) }
    var imageModerating by remember { mutableStateOf(false) }
    var moderationError by remember { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri != null) {
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
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
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
            // Header
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
                ) { Text(otherEmoji, style = MaterialTheme.typography.titleMedium) }
                Spacer(Modifier.width(10.dp))
                Text(
                    otherName,
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                SculptedOutlinedButton(
                    text = "Preview",
                    onClick = { showPreview = true },
                    accent = Aqua,
                    textColor = Aqua,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                )
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
                    ChatBubble(
                        text = msg.body,
                        timestamp = formatTime(msg.created_at),
                        isMe = isMe,
                        imageUri = msg.image_uri,
                        isRead = msg.read_at != null,
                    )
                }
            }

            // Reply bar
            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)
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
                    modifier = Modifier.weight(1f),
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
                    Text(
                        text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextHigh,
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

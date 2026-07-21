package com.rork.rockscout.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.CommunityRepository
import com.rork.rockscout.data.ReportRepository
import com.rork.rockscout.data.ReportScreenshotHelper
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.ui.components.CommunityPostCard
import com.rork.rockscout.ui.components.CommunityPostComposer
import com.rork.rockscout.ui.components.ExpiredCommunityPostsPopup
import com.rork.rockscout.ui.components.ReportSubmittedDialog
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.Ink
import kotlinx.coroutines.launch

/**
 * Community Q&A board — an app-wide feed where any signed-in user can post,
 * comment, reply, love, and repost. Posts auto-expire after 14 days.
 */
@Composable
fun CommunityScreen(navController: NavController) {
    val repo = CommunityRepository.instance
    val social = SocialRepository.instance
    val auth = AuthRepository.instance
    val myUserId = auth.currentUserId
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val rootView = LocalView.current

    val feedPosts by repo.feedPosts.collectAsStateWithLifecycle()
    val expiredPosts by repo.expiredPosts.collectAsStateWithLifecycle()
    val postLikes by repo.postLikes.collectAsStateWithLifecycle()
    val postComments by repo.postComments.collectAsStateWithLifecycle()
    val commentLikes by repo.commentLikes.collectAsStateWithLifecycle()
    val likedPostIds by repo.likedPostIds.collectAsStateWithLifecycle()
    val likedCommentIds by repo.likedCommentIds.collectAsStateWithLifecycle()
    val authors by repo.authors.collectAsStateWithLifecycle()
    val connections by social.connections.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        repo.loadFeed()
        social.loadConnections()
    }

    // ─── UI state ───
    var sortMode by remember { mutableStateOf(CommunityRepository.SortMode.Newest) }
    var selectedCategory by remember { mutableStateOf<CommunityRepository.Category?>(null) }
    var showComposer by remember { mutableStateOf(false) }
    var showExpiredPopup by remember { mutableStateOf(false) }
    var pendingDeletePostId by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var reportTargetPost by remember { mutableStateOf<CommunityRepository.PostRow?>(null) }
    var showReportConfirm by remember { mutableStateOf(false) }
    var reportSubmitted by remember { mutableStateOf(false) }
    var repostTarget by remember { mutableStateOf<CommunityRepository.PostRow?>(null) }

    // Per-post comment bodies (text being typed)
    val commentBodies = remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    // Reply bodies keyed by comment ID
    val replyBodies = remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var replyingTo by remember { mutableStateOf<String?>(null) }
    // Comment/reply image attachment state
    val commentImageUris = remember { mutableStateOf<Map<String, String?>>(emptyMap()) }
    val replyImageUris = remember { mutableStateOf<Map<String, String?>>(emptyMap()) }
    val commentImageErrors = remember { mutableStateOf<Map<String, String?>>(emptyMap()) }
    val replyImageErrors = remember { mutableStateOf<Map<String, String?>>(emptyMap()) }

    BackHandler(enabled = showComposer) { showComposer = false }
    BackHandler(enabled = showExpiredPopup) { showExpiredPopup = false }
    BackHandler(enabled = showDeleteConfirm) { showDeleteConfirm = false }
    BackHandler(enabled = reportTargetPost != null && !showReportConfirm) {
        reportTargetPost = null
    }

    val sortedFeed = remember(feedPosts, sortMode, postLikes, postComments, selectedCategory) {
        repo.sortedFeed(sortMode, selectedCategory)
    }

    ScreenScaffold(
        title = "Community",
        onBack = { navController.popBackStack() },
        actions = {
            // Archived Posts button — top-right corner
            SculptedIconButton(
                icon = Icons.Filled.Bookmarks,
                contentDescription = "Archived Posts",
                onClick = {
                    scope.launch { repo.loadExpiredPosts() }
                    showExpiredPopup = true
                },
                modifier = Modifier.size(40.dp),
                accent = Citrine,
                iconTint = Citrine,
                backgroundColor = Color(0xFF2A2820),
                size = 40.dp,
                shadowElevation = 3.dp,
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            // Create Post button + sort row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // Create Post pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(Citrine.copy(alpha = 0.18f))
                            .glowingBorder(2.dp, Citrine.copy(alpha = 0.85f), RoundedCornerShape(24.dp))
                            .clickable { showComposer = true }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                            tint = Citrine,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Create Post",
                            style = MaterialTheme.typography.labelLarge,
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    // Sort dropdown
                    SortDropdown(
                        currentMode = sortMode,
                        onModeSelected = { sortMode = it },
                    )
                }
            }

            // Feed count
            item {
                Text(
                    "${sortedFeed.size} post${if (sortedFeed.size != 1) "s" else ""} in the community",
                    style = MaterialTheme.typography.labelMedium,
                    color = DarkTextMid,
                )
            }

            // Category filter chips
            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    CategoryChip(
                        label = "All",
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                    )
                    CommunityRepository.Category.entries.forEach { category ->
                        CategoryChip(
                            label = category.label,
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                        )
                    }
                }
            }

            // Empty state
            if (sortedFeed.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.Forum,
                                contentDescription = null,
                                tint = Citrine.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp),
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                if (feedPosts.isEmpty())
                                    "No community posts yet.\nBe the first to post!"
                                else
                                    "No posts match this filter.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = DarkTextMid,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

            // Post cards
            items(sortedFeed, key = { it.id }) { post ->
                val author = authors[post.user_id]
                val isFriend = connections.contains(post.user_id)
                val hoursLeft = repo.hoursUntilExpiry(post)
                CommunityPostCard(
                    post = post,
                    authorName = author?.displayName ?: "Unknown",
                    authorAvatar = author?.avatarEmoji ?: "⛏️",
                    isMe = post.user_id == myUserId,
                    isLiked = likedPostIds.contains(post.id),
                    likeCount = postLikes[post.id]?.size ?: 0,
                    comments = postComments[post.id] ?: emptyList(),
                    commentLikes = commentLikes,
                    likedCommentIds = likedCommentIds,
                    myUserId = myUserId,
                    hoursUntilExpiry = hoursLeft,
                    commentBody = commentBodies.value[post.id] ?: "",
                    replyingToCommentId = replyingTo,
                    replyBody = if (replyingTo != null) replyBodies.value[replyingTo] ?: "" else "",
                    commentImageUri = commentImageUris.value[post.id],
                    replyImageUri = replyingTo?.let { replyImageUris.value[it] },
                    commentImageModerating = commentImageUris.value[post.id] == "__loading__",
                    replyImageModerating = replyingTo != null && replyImageUris.value[replyingTo] == "__loading__",
                    commentImageError = commentImageErrors.value[post.id],
                    replyImageError = replyingTo?.let { replyImageErrors.value[it] },
                    onLike = { scope.launch { repo.toggleLike(post.id) } },
                    onCommentChange = { body ->
                        commentBodies.value = commentBodies.value + (post.id to body)
                    },
                    onComment = {
                        val body = commentBodies.value[post.id] ?: ""
                        val imgUri = commentImageUris.value[post.id]
                        val cleanImg = if (imgUri != null && !imgUri.startsWith("__") && imgUri != "__loading__") imgUri else null
                        if (body.isNotBlank() || cleanImg != null) {
                            scope.launch {
                                repo.addComment(post.id, body, null, cleanImg)
                                commentBodies.value = commentBodies.value - post.id
                                commentImageUris.value = commentImageUris.value - post.id
                                commentImageErrors.value = commentImageErrors.value - post.id
                            }
                        }
                    },
                    onReplyStart = { commentId -> replyingTo = commentId },
                    onReplyBodyChange = { body ->
                        val key = replyingTo
                        if (key != null) {
                            replyBodies.value = replyBodies.value + (key to body)
                        }
                    },
                    onReplySubmit = {
                        val parentId = replyingTo
                        val body = if (parentId != null) replyBodies.value[parentId] ?: "" else ""
                        val imgUri = parentId?.let { replyImageUris.value[it] }
                        val cleanImg = if (imgUri != null && !imgUri.startsWith("__") && imgUri != "__loading__") imgUri else null
                        if (parentId != null && (body.isNotBlank() || cleanImg != null)) {
                            scope.launch {
                                repo.addComment(post.id, body, parentId, cleanImg)
                                replyBodies.value = replyBodies.value - parentId
                                replyImageUris.value = replyImageUris.value - parentId
                                replyImageErrors.value = replyImageErrors.value - parentId
                                replyingTo = null
                            }
                        }
                    },
                    onCommentLike = { commentId ->
                        scope.launch { repo.toggleCommentLike(commentId) }
                    },
                    onCommentImagePicked = { uri ->
                        if (uri == null) {
                            commentImageUris.value = commentImageUris.value - post.id
                            commentImageErrors.value = commentImageErrors.value - post.id
                        } else if (uri.startsWith("__error:")) {
                            commentImageErrors.value = commentImageErrors.value + (post.id to uri.substring(7))
                            commentImageUris.value = commentImageUris.value - post.id
                        } else {
                            commentImageUris.value = commentImageUris.value + (post.id to uri)
                            commentImageErrors.value = commentImageErrors.value - post.id
                        }
                    },
                    onReplyImagePicked = { uri ->
                        val key = replyingTo ?: return@CommunityPostCard
                        if (uri == null) {
                            replyImageUris.value = replyImageUris.value - key
                            replyImageErrors.value = replyImageErrors.value - key
                        } else if (uri.startsWith("__error:")) {
                            replyImageErrors.value = replyImageErrors.value + (key to uri.substring(7))
                            replyImageUris.value = replyImageUris.value - key
                        } else {
                            replyImageUris.value = replyImageUris.value + (key to uri)
                            replyImageErrors.value = replyImageErrors.value - key
                        }
                    },
                    onCommentImageRemove = {
                        commentImageUris.value = commentImageUris.value - post.id
                        commentImageErrors.value = commentImageErrors.value - post.id
                    },
                    onReplyImageRemove = {
                        replyingTo?.let {
                            replyImageUris.value = replyImageUris.value - it
                            replyImageErrors.value = replyImageErrors.value - it
                        }
                    },
                    onRepost = if (!isFriend && post.user_id != myUserId) {
                        null
                    } else if (post.user_id != myUserId) {
                        { repostTarget = post }
                    } else null,
                    onDelete = if (post.user_id == myUserId) {
                        {
                            pendingDeletePostId = post.id
                            showDeleteConfirm = true
                        }
                    } else null,
                    onImageClick = null,
                )
            }
            // Bottom spacing
            item { Spacer(Modifier.height(20.dp)) }
        }
    }

    // ─── Create Post composer ───
    if (showComposer) {
        CommunityPostComposer(
            onDismiss = { showComposer = false },
            onPosted = {
                scope.launch { repo.loadFeed() }
            },
        )
    }

    // ─── Expired posts popup ───
    if (showExpiredPopup) {
        ExpiredCommunityPostsPopup(
            posts = expiredPosts,
            isMe = true,
            postLikes = postLikes,
            postComments = postComments,
            commentLikes = commentLikes,
            likedPostIds = likedPostIds,
            likedCommentIds = likedCommentIds,
            myUserId = myUserId,
            authors = authors,
            commentBodies = commentBodies.value,
            replyBodies = replyBodies.value,
            replyingTo = replyingTo,
            commentImageUris = commentImageUris.value,
            replyImageUris = replyImageUris.value,
            commentImageErrors = commentImageErrors.value,
            replyImageErrors = replyImageErrors.value,
            onLike = { postId -> scope.launch { repo.toggleLike(postId) } },
            onCommentChange = { postId, body ->
                commentBodies.value = commentBodies.value + (postId to body)
            },
            onComment = { postId ->
                val body = commentBodies.value[postId] ?: ""
                val imgUri = commentImageUris.value[postId]
                val cleanImg = if (imgUri != null && !imgUri.startsWith("__") && imgUri != "__loading__") imgUri else null
                if (body.isNotBlank() || cleanImg != null) {
                    scope.launch {
                        repo.addComment(postId, body, null, cleanImg)
                        commentBodies.value = commentBodies.value - postId
                        commentImageUris.value = commentImageUris.value - postId
                        commentImageErrors.value = commentImageErrors.value - postId
                    }
                }
            },
            onReplyStart = { commentId -> replyingTo = commentId },
            onReplyBodyChange = { body ->
                val key = replyingTo
                if (key != null) {
                    replyBodies.value = replyBodies.value + (key to body)
                }
            },
            onReplySubmit = {
                val parentId = replyingTo
                val body = if (parentId != null) replyBodies.value[parentId] ?: "" else ""
                val imgUri = parentId?.let { replyImageUris.value[it] }
                val cleanImg = if (imgUri != null && !imgUri.startsWith("__") && imgUri != "__loading__") imgUri else null
                if (parentId != null && (body.isNotBlank() || cleanImg != null)) {
                    val postId = expiredPosts.firstOrNull { p ->
                        postComments[p.id]?.any { it.id == parentId } == true
                    }?.id
                    if (postId != null) {
                        scope.launch {
                            repo.addComment(postId, body, parentId, cleanImg)
                            replyBodies.value = replyBodies.value - parentId
                            replyImageUris.value = replyImageUris.value - parentId
                            replyImageErrors.value = replyImageErrors.value - parentId
                            replyingTo = null
                        }
                    }
                }
            },
            onCommentLike = { commentId ->
                scope.launch { repo.toggleCommentLike(commentId) }
            },
            onCommentImagePicked = { postId, uri ->
                if (uri == null) {
                    commentImageUris.value = commentImageUris.value - postId
                    commentImageErrors.value = commentImageErrors.value - postId
                } else if (uri.startsWith("__error:")) {
                    commentImageErrors.value = commentImageErrors.value + (postId to uri.substring(7))
                    commentImageUris.value = commentImageUris.value - postId
                } else {
                    commentImageUris.value = commentImageUris.value + (postId to uri)
                    commentImageErrors.value = commentImageErrors.value - postId
                }
            },
            onReplyImagePicked = { uri ->
                val key = replyingTo
                if (key != null) {
                    if (uri == null) {
                        replyImageUris.value = replyImageUris.value - key
                        replyImageErrors.value = replyImageErrors.value - key
                    } else if (uri.startsWith("__error:")) {
                        replyImageErrors.value = replyImageErrors.value + (key to uri.substring(7))
                        replyImageUris.value = replyImageUris.value - key
                    } else {
                        replyImageUris.value = replyImageUris.value + (key to uri)
                        replyImageErrors.value = replyImageErrors.value - key
                    }
                }
            },
            onCommentImageRemove = { postId ->
                commentImageUris.value = commentImageUris.value - postId
                commentImageErrors.value = commentImageErrors.value - postId
            },
            onReplyImageRemove = {
                replyingTo?.let {
                    replyImageUris.value = replyImageUris.value - it
                    replyImageErrors.value = replyImageErrors.value - it
                }
            },
            onRestore = { postId ->
                scope.launch {
                    repo.restorePost(postId)
                    repo.loadExpiredPosts()
                }
            },
            onDelete = { postId ->
                scope.launch {
                    repo.deletePost(postId)
                    repo.loadExpiredPosts()
                }
            },
            onDismiss = { showExpiredPopup = false },
        )
    }

    // ─── Delete confirmation ───
    if (showDeleteConfirm && pendingDeletePostId != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirm = false
                pendingDeletePostId = null
            },
            containerColor = Color(0xFF1E1C16),
            titleContentColor = DarkTextHigh,
            textContentColor = DarkTextMid,
            title = { Text("Delete post?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Delete this community post? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                SculptedButton(
                    text = "Delete",
                    onClick = {
                        val postId = pendingDeletePostId
                        showDeleteConfirm = false
                        pendingDeletePostId = null
                        if (postId != null) {
                            scope.launch { repo.deletePost(postId) }
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
                    onClick = {
                        showDeleteConfirm = false
                        pendingDeletePostId = null
                    },
                    accent = DarkTextMid,
                    textColor = DarkTextMid,
                )
            },
        )
    }

    // ─── Report confirmation ───
    if (showReportConfirm && reportTargetPost != null) {
        val post = reportTargetPost
        AlertDialog(
            onDismissRequest = {
                showReportConfirm = false
                reportTargetPost = null
            },
            containerColor = Color(0xFF1E1C16),
            titleContentColor = DarkTextHigh,
            textContentColor = DarkTextMid,
            title = { Text("Report this post?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "A screenshot will be captured and you'll be asked to send a report email about this community post. Only continue if you believe it violates our community guidelines.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                SculptedButton(
                    text = "Report",
                    onClick = {
                        val target = reportTargetPost
                        showReportConfirm = false
                        reportTargetPost = null
                        if (target != null) {
                            scope.launch {
                                val reportId = "report-${System.currentTimeMillis()}"
                                val screenshotPath = ReportScreenshotHelper.captureScreenshot(
                                    context, rootView, reportId,
                                )
                                val myProfile = AppRepository.instance.profile.value
                                val author = authors[target.user_id]
                                ReportRepository.instance.reportUser(
                                    reportedUserId = target.user_id,
                                    reason = "Inappropriate community post: ${target.title}",
                                    screenshotPath = screenshotPath,
                                    reporterName = myProfile.name,
                                    reportedName = author?.displayName,
                                    reportedAvatar = author?.avatarEmoji,
                                ).onSuccess {
                                    reportSubmitted = true
                                    val intent = ReportScreenshotHelper.buildEmailIntent(
                                        context = context,
                                        reportedUserName = author?.displayName ?: "Unknown user",
                                        reporterUserName = myProfile.name,
                                        reason = "Inappropriate community post: ${target.title}",
                                        timestamp = System.currentTimeMillis(),
                                        screenshotPath = screenshotPath,
                                    )
                                    ReportScreenshotHelper.launchEmailComposer(context, intent)
                                }
                            }
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
                    onClick = {
                        showReportConfirm = false
                        reportTargetPost = null
                    },
                    accent = DarkTextMid,
                    textColor = DarkTextMid,
                )
            },
        )
    }

    // ─── Report submitted dialog ───
    if (reportSubmitted) {
        ReportSubmittedDialog(onDismiss = { reportSubmitted = false })
    }

    // ─── Repost composer (share to profile) ───
    repostTarget?.let { post ->
        val author = authors[post.user_id]
        ShareToProfileComposer(
            sourceType = "community_repost",
            title = post.title,
            tagline = if (post.tagline.isNotBlank()) {
                "Reposted from ${author?.displayName ?: "community"}: ${post.tagline}"
            } else {
                "Reposted from ${author?.displayName ?: "community"}"
            },
            imageUri = post.image_uri,
            locationText = post.location_text,
            onDismiss = { repostTarget = null },
        )
    }
}

@Composable
private fun SortDropdown(
    currentMode: CommunityRepository.SortMode,
    onModeSelected: (CommunityRepository.SortMode) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF2A2820))
                .glowingBorder(1.dp, Citrine.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                currentMode.label,
                style = MaterialTheme.typography.labelMedium,
                color = Citrine,
                fontWeight = FontWeight.Bold,
            )
        }
        androidx.compose.material3.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            CommunityRepository.SortMode.entries.forEach { mode ->
                androidx.compose.material3.DropdownMenuItem(
                    text = {
                        Text(
                            mode.label,
                            color = if (mode == currentMode) Citrine else DarkTextHigh,
                            fontWeight = if (mode == currentMode) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                    onClick = {
                        onModeSelected(mode)
                        expanded = false
                    },
                )
            }
        }
    }
}

/**
 * Pill-style filter chip used to toggle between community board categories.
 * Selected chip is filled with the category accent; unselected chips are dimmed.
 */
@Composable
private fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val accent = if (selected) Citrine else DarkTextMid
    val bg = if (selected) Citrine.copy(alpha = 0.22f) else Color(0xFF2A2820)
    val borderAlpha = if (selected) 0.85f else 0.35f
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .glowingBorder(1.dp, Citrine.copy(alpha = borderAlpha), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = accent,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

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
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
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
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.YooperliteHeart
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.components.CommunityPostComposer
import com.rork.rockscout.ui.components.ExpiredCommunityPostsPopup
import com.rork.rockscout.ui.components.ReportSubmittedDialog
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.CompactSearchPill
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.data.SupabaseMessagingRepository
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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

    // ─── Premium gating: Community is a social feature ───
    val accessManager = com.rork.rockscout.data.IdentifyAccessManager.instance
    val purchaseManager = com.rork.rockscout.data.PurchaseManager.instance
    val isPremium by purchaseManager.isPremium.collectAsStateWithLifecycle()
    val clubLocked = remember(isPremium) { accessManager.isSocialLocked(isPremium) }
    if (clubLocked) {
        com.rork.rockscout.ui.components.ClubLockedState(
            emoji = "\uD83D\uDD12",
            title = "Unlock the Community Board",
            message = "Your 1-week free trial has ended. Subscribe or donate to join the community discussion.",
            buttonLabel = "Subscribe or donate",
            onButton = { navController.navigate(Routes.PAYWALL) },
        )
        return
    }

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
    var selectedTab by remember { mutableStateOf(0) } // 0=Posts, 1=Group Chats
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

    // Pending comment/reply image removal (requires confirmation)
    var pendingCommentImageRemovePostId by remember { mutableStateOf<String?>(null) }
    var pendingReplyImageRemove by remember { mutableStateOf(false) }

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
    BackHandler(enabled = selectedTab == 1) { selectedTab = 0 }
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
            // Archived Posts button — top-right corner (only on Posts tab)
            if (selectedTab == 0) {
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
            }
        },
    ) {
        // ── Tab switcher: Posts | Group Chats ──
        CommunityTabSwitcher(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
        )

        if (selectedTab == 1) {
            // Group Chats tab
            GroupChatsTabContent(navController = navController)
            return@ScreenScaffold
        }

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

            // Compact post cards — tap opens the full post detail page
            items(sortedFeed, key = { it.id }) { post ->
                val author = authors[post.user_id]
                val hoursLeft = repo.hoursUntilExpiry(post)
                CompactCommunityCard(
                    post = post,
                    authorName = author?.displayName ?: "Unknown",
                    authorAvatar = author?.avatarEmoji ?: "⛏️",
                    isMe = post.user_id == myUserId,
                    isLiked = likedPostIds.contains(post.id),
                    likeCount = postLikes[post.id]?.size ?: 0,
                    commentCount = (postComments[post.id] ?: emptyList()).size,
                    hoursUntilExpiry = hoursLeft,
                    onTap = { navController.navigate(Routes.communityPostDetail(post.id)) },
                    onLike = { scope.launch { repo.toggleLike(post.id) } },
                    onRepost = if (post.user_id != myUserId) {
                        { repostTarget = post }
                    } else null,
                    onDelete = if (post.user_id == myUserId) {
                        {
                            pendingDeletePostId = post.id
                            showDeleteConfirm = true
                        }
                    } else null,
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
                pendingCommentImageRemovePostId = postId
            },
            onReplyImageRemove = {
                pendingReplyImageRemove = true
            },
            onRestore = { postId ->
                scope.launch {
                    repo.restorePost(postId)
                    repo.loadExpiredPosts()
                }
            },
            onDelete = { postId ->
                showExpiredPopup = false
                pendingDeletePostId = postId
                showDeleteConfirm = true
            },
            onDeleteComment = { commentId ->
                scope.launch { repo.deleteComment(commentId) }
            },
            onEditComment = { commentId, newBody ->
                scope.launch { repo.editComment(commentId, newBody) }
            },
            onEditPost = { postId, newDescription ->
                scope.launch { repo.editPostDescription(postId, newDescription) }
            },
            onDismiss = { showExpiredPopup = false },
        )
    }

    // ─── Comment image removal confirmation ───
    pendingCommentImageRemovePostId?.let { postId ->
        AlertDialog(
            onDismissRequest = { pendingCommentImageRemovePostId = null },
            containerColor = Color(0xFF1E1C16),
            titleContentColor = DarkTextHigh,
            textContentColor = DarkTextMid,
            title = { Text("Remove photo?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Remove this photo from your comment?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                SculptedTextButton(
                    text = "Remove",
                    onClick = {
                        commentImageUris.value = commentImageUris.value - postId
                        commentImageErrors.value = commentImageErrors.value - postId
                        pendingCommentImageRemovePostId = null
                    },
                    accent = Danger,
                    textColor = Danger,
                    fontWeight = FontWeight.Bold,
                )
            },
            dismissButton = {
                SculptedTextButton(
                    text = "Cancel",
                    onClick = { pendingCommentImageRemovePostId = null },
                    accent = Citrine,
                    textColor = DarkTextMid,
                )
            },
        )
    }

    // ─── Reply image removal confirmation ───
    if (pendingReplyImageRemove) {
        AlertDialog(
            onDismissRequest = { pendingReplyImageRemove = false },
            containerColor = Color(0xFF1E1C16),
            titleContentColor = DarkTextHigh,
            textContentColor = DarkTextMid,
            title = { Text("Remove photo?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Remove this photo from your reply?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                SculptedTextButton(
                    text = "Remove",
                    onClick = {
                        replyingTo?.let {
                            replyImageUris.value = replyImageUris.value - it
                            replyImageErrors.value = replyImageErrors.value - it
                        }
                        pendingReplyImageRemove = false
                    },
                    accent = Danger,
                    textColor = Danger,
                    fontWeight = FontWeight.Bold,
                )
            },
            dismissButton = {
                SculptedTextButton(
                    text = "Cancel",
                    onClick = { pendingReplyImageRemove = false },
                    accent = Citrine,
                    textColor = DarkTextMid,
                )
            },
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
                            scope.launch {
                                repo.deletePost(postId)
                                repo.loadExpiredPosts()
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

/**
 * Compact card for the community feed. Shows a small photo, short tagline,
 * username, post type badge, love + comment counts, and an optional delete.
 * Tapping the card opens the full post detail page. No comments or reply
 * boxes inline — everything is in the detail screen.
 */
@Composable
private fun CompactCommunityCard(
    post: CommunityRepository.PostRow,
    authorName: String,
    authorAvatar: String,
    isMe: Boolean,
    isLiked: Boolean,
    likeCount: Int,
    commentCount: Int,
    hoursUntilExpiry: Long,
    onTap: () -> Unit,
    onLike: () -> Unit,
    onRepost: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
) {
    val category = CommunityRepository.resolveCategory(post.category)
    val categoryAccent = when (category) {
        CommunityRepository.Category.IDENTIFICATION -> Citrine
        CommunityRepository.Category.LOCATION_TIPS -> Aqua
        CommunityRepository.Category.GENERAL -> Color(0xFFB8A0FF)
    }
    DarkCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTap),
        accent = Citrine,
    ) {
        Row(verticalAlignment = Alignment.Top) {
            // Thumbnail (if image present)
            if (post.image_uri != null) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1A1812))
                        .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        model = post.image_uri,
                        contentDescription = post.title,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop,
                    )
                }
                Spacer(Modifier.width(10.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                // Author row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Citrine.copy(alpha = 0.18f))
                            .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(authorAvatar, style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        authorName,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isMe) Citrine else DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    if (onDelete != null) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2A2820))
                                .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape)
                                .clickable(onClick = onDelete),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Delete post",
                                tint = TextLow,
                                modifier = Modifier.size(12.dp),
                            )
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                // Title
                Text(
                    post.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                // Tagline
                if (post.tagline.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        post.tagline,
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkTextMid,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.height(6.dp))
                // Category badge + location + expiry
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(categoryAccent.copy(alpha = 0.18f))
                            .glowingBorder(1.dp, categoryAccent.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            category.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = categoryAccent,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    if (post.location_text.isNotBlank()) {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "📍 ${post.location_text}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Aqua,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                    if (hoursUntilExpiry in 0..48) {
                        val hoursLeft = (hoursUntilExpiry % 24).toInt()
                        val daysLeft = (hoursUntilExpiry / 24).toInt()
                        val expiryText = if (daysLeft > 0) "${daysLeft}d ${hoursLeft}h" else "${hoursLeft}h"
                        Text(
                            "⏳ $expiryText",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFE8A33D),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                // Love + comment count row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isLiked) Color(0xFF3A2818).copy(alpha = 0.85f) else Color.Transparent)
                            .clickable(onClick = onLike)
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            YooperliteHeart(active = isLiked, contentDescription = if (isLiked) "Loved" else "Love", size = 14.dp)
                            if (likeCount > 0) {
                                Spacer(Modifier.width(3.dp))
                                Text(
                                    likeCount.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isLiked) Color(0xFFFF8C2A) else DarkTextMid,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "$commentCount 💬",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextMid,
                    )
                    Spacer(Modifier.weight(1f))
                    if (onRepost != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Transparent)
                                .clickable(onClick = onRepost)
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Repeat,
                                    contentDescription = "Repost",
                                    tint = Aqua,
                                    modifier = Modifier.size(14.dp),
                                )
                                Spacer(Modifier.width(3.dp))
                                Text(
                                    "Repost",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Aqua,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        "Tap to view ›",
                        style = MaterialTheme.typography.labelSmall,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

/* ── Community tab switcher: Posts | Group Chats ─────────────────────────── */

@Composable
private fun CommunityTabSwitcher(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val pills = listOf("Posts" to 0, "Group Chats" to 1)
        pills.forEach { (label, tab) ->
            val isActive = selectedTab == tab
            val accent = if (tab == 0) Citrine else Aqua
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (isActive) accent.copy(alpha = 0.18f) else Color.Transparent)
                    .glowingBorder(1.5.dp, if (isActive) accent else Color(0x33FFFFFF), RoundedCornerShape(24.dp))
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (tab == 0) Icons.Filled.Forum else Icons.Filled.Group,
                        contentDescription = null,
                        tint = if (isActive) accent else DarkTextMid,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        label,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isActive) accent else DarkTextMid,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                    )
                }
            }
            if (tab < 1) Spacer(Modifier.width(12.dp))
        }
    }
}

/* ── Group chats tab content ─────────────────────────────────────────────── */

@Composable
private fun GroupChatsTabContent(
    navController: NavController,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val groupChats by SupabaseMessagingRepository.groupChats.collectAsStateWithLifecycle()
    val groupChatMembers by SupabaseMessagingRepository.groupChatMembers.collectAsStateWithLifecycle()
    val pendingInvites by SupabaseMessagingRepository.pendingGroupInvites.collectAsStateWithLifecycle()
    val unreadCounts by SupabaseMessagingRepository.groupChatUnreadCounts.collectAsStateWithLifecycle()
    val creatorNames by SupabaseMessagingRepository.creatorNames.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        SupabaseMessagingRepository.loadGroupChats()
        SupabaseMessagingRepository.loadPendingInvites()
        SupabaseMessagingRepository.refreshGroupChatUnreadCounts()
        SupabaseMessagingRepository.fetchCreatorNames()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Search bar + Create button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CompactSearchPill(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search group chats…",
                    accent = Aqua,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(23.dp))
                        .background(Aqua.copy(alpha = 0.18f))
                        .glowingBorder(2.dp, Aqua.copy(alpha = 0.85f), RoundedCornerShape(23.dp))
                        .clickable { showCreateDialog = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Create", tint = Aqua, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("New Group", style = MaterialTheme.typography.labelLarge, color = Aqua, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Pending invites
        if (pendingInvites.isNotEmpty()) {
            item {
                Text(
                    "Pending Invites (${pendingInvites.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = Citrine,
                    fontWeight = FontWeight.Bold,
                )
            }
            items(pendingInvites, key = { it.id }) { invite ->
                val chat = groupChats.firstOrNull { it.id == invite.group_chat_id }
                DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
                    Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                        Text(
                            chat?.name ?: "Group Chat",
                            style = MaterialTheme.typography.titleSmall,
                            color = DarkTextHigh,
                            fontWeight = FontWeight.Bold,
                        )
                        if (chat != null && chat.subject.isNotBlank()) {
                            Text(chat.subject, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SculptedButton(
                                text = "Accept",
                                onClick = {
                                    scope.launch {
                                        SupabaseMessagingRepository.acceptGroupInvite(invite.id, invite.group_chat_id)
                                        SupabaseMessagingRepository.loadGroupChats()
                                        SupabaseMessagingRepository.loadPendingInvites()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                accent = com.rork.rockscout.ui.theme.Success,
                                containerColor = com.rork.rockscout.ui.theme.Success,
                                textColor = Ink,
                            )
                            SculptedOutlinedButton(
                                text = "Decline",
                                onClick = {
                                    scope.launch {
                                        SupabaseMessagingRepository.declineGroupInvite(invite.id)
                                        SupabaseMessagingRepository.loadPendingInvites()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                accent = Danger,
                                textColor = DarkTextMid,
                            )
                        }
                    }
                }
            }
        }

        // Group chat count
        item {
            Text(
                "${groupChats.size} group chat${if (groupChats.size != 1) "s" else ""}",
                style = MaterialTheme.typography.labelMedium,
                color = DarkTextMid,
            )
        }

        // Group chat cards — sized to match trade listing entries
        if (groupChats.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Group, contentDescription = null, tint = Aqua.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No group chats yet.", style = MaterialTheme.typography.bodyLarge, color = DarkTextMid, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(4.dp))
                        Text("Start one to bring hunters together!", style = MaterialTheme.typography.bodyMedium, color = DarkTextMid, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            val filteredChats = if (searchQuery.isBlank()) groupChats else groupChats.filter {
                it.name.contains(searchQuery, ignoreCase = true) || it.subject.contains(searchQuery, ignoreCase = true)
            }
            items(filteredChats, key = { it.id }) { chat ->
                GroupChatListingCard(
                    chat = chat,
                    creatorName = creatorNames[chat.creator_id] ?: "Unknown",
                    memberCount = SupabaseMessagingRepository.groupChatMemberCount(chat.id),
                    unreadCount = unreadCounts[chat.id] ?: 0,
                    onTap = {
                        navController.navigate(Routes.messengerThread("group:${chat.id}"))
                    },
                    onShare = {
                        val deepLink = "rockscout://group_chat/${chat.id}"
                        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "Join my group chat: ${chat.name}")
                            putExtra(android.content.Intent.EXTRA_TEXT, "Join our group chat \"${chat.name}\" on RockScout!\n\n$deepLink")
                        }
                        com.rork.rockscout.data.SafeLinkOpener.openShareChooser(
                            context,
                            shareIntent,
                            "Share Group Chat",
                        )
                    },
                )
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }

    // Create group chat dialog
    if (showCreateDialog) {
        CreateGroupChatDialog(
            onDismiss = { showCreateDialog = false },
            onCreated = { chatId ->
                showCreateDialog = false
                scope.launch {
                    SupabaseMessagingRepository.loadGroupChats()
                }
                navController.navigate(Routes.messengerThread("group:$chatId"))
            },
        )
    }
}

/**
 * Trade-listing-sized card for group chat entries in the Community board.
 * Shows creator name, header image (if applicable), title, and subject.
 * Tapping the card opens the full group chat screen.
 */
@Composable
private fun GroupChatListingCard(
    chat: SupabaseMessagingRepository.GroupChatDto,
    creatorName: String,
    memberCount: Int,
    unreadCount: Int,
    onTap: () -> Unit,
    onShare: () -> Unit,
) {
    DarkCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTap),
        accent = Aqua,
        contentPadding = PaddingValues(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Header image or icon — fixed 56dp, same as trade listing cards
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1A1812))
                    .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (chat.header_image_url != null) {
                    AsyncImage(
                        model = chat.header_image_url,
                        contentDescription = chat.name,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Icon(Icons.Filled.Group, contentDescription = null, tint = Aqua, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Row 1: creator name + unread badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Aqua.copy(alpha = 0.18f))
                            .glowingBorder(1.dp, Aqua.copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("⛏️", style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        creatorName,
                        style = MaterialTheme.typography.labelMedium,
                        color = Aqua,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    if (unreadCount > 0) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Citrine)
                                .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape)
                                .size(20.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                if (unreadCount > 99) "99+" else unreadCount.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Ink,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                // Row 2: group chat name (title)
                Text(
                    chat.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                // Row 3: subject + member count + badges
                if (chat.subject.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        chat.subject,
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkTextMid,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "$memberCount member${if (memberCount != 1) "s" else ""}${if (chat.max_members != null) " / ${chat.max_members} max" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Aqua,
                    )
                    Spacer(Modifier.weight(1f))
                    // Profanity filter badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (chat.profanity_filter_level == "strict") Danger.copy(alpha = 0.18f) else Citrine.copy(alpha = 0.18f))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            if (chat.profanity_filter_level == "strict") "Strict" else "Normal",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (chat.profanity_filter_level == "strict") Danger else Citrine,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    // Share button
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2A2820))
                            .glowingBorder(1.dp, Aqua.copy(alpha = 0.35f), CircleShape)
                            .clickable(onClick = onShare),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Share,
                            contentDescription = "Share group chat link",
                            tint = Aqua,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
            }
        }
    }
}

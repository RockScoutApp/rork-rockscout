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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.data.CommunityRepository
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.FullScreenImageViewer
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.YooperliteHeart
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

/** Sort mode for detail screen comments. */
private enum class DetailCommentSortMode { Popular, Newest, Oldest }

/**
 * Full-screen community post detail page. Shows the complete post body,
 * image, like/repost actions, and a scrollable comments section where
 * each comment can be expanded to reveal its replies individually.
 *
 * Top-level comments show the comment + a reply button. When a comment
 * has more than one reply, an "View all N replies" button expands just
 * that comment's replies inline.
 */
@Composable
fun CommunityPostDetailScreen(
    navController: NavController,
    postId: String,
) {
    val repo = CommunityRepository.instance
    val social = SocialRepository.instance
    val feedPosts by repo.feedPosts.collectAsStateWithLifecycle()
    val expiredPosts by repo.expiredPosts.collectAsStateWithLifecycle()
    val postLikes by repo.postLikes.collectAsStateWithLifecycle()
    val postComments by repo.postComments.collectAsStateWithLifecycle()
    val commentLikes by repo.commentLikes.collectAsStateWithLifecycle()
    val likedPostIds by repo.likedPostIds.collectAsStateWithLifecycle()
    val likedCommentIds by repo.likedCommentIds.collectAsStateWithLifecycle()
    val authors by repo.authors.collectAsStateWithLifecycle()
    val connections by social.connections.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val post = remember(feedPosts, expiredPosts, postId) {
        feedPosts.firstOrNull { it.id == postId }
            ?: expiredPosts.firstOrNull { it.id == postId }
    }

    if (post == null) {
        Box(
            Modifier.fillMaxSize().background(Obsidian).padding(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("Post not found.", color = TextMid)
        }
        return
    }

    val author = authors[post.user_id]
    val authorName = author?.displayName ?: "Unknown"
    val authorAvatar = author?.avatarEmoji ?: "⛏️"
    val isLiked = likedPostIds.contains(post.id)
    val likeCount = postLikes[post.id]?.size ?: 0
    val comments = postComments[post.id] ?: emptyList()
    val isFriend = connections.contains(post.user_id)
    val myUserId = com.rork.rockscout.data.AuthRepository.instance.currentUserId
    val isMe = post.user_id == myUserId

    // Per-comment expanded replies state (which comments have replies visible)
    val expandedReplies = remember { mutableStateOf<Set<String>>(emptySet()) }
    // Which comment is currently being replied to
    var replyingToCommentId by remember { mutableStateOf<String?>(null) }
    val replyBodies = remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var viewerImageUrl by remember { mutableStateOf<String?>(null) }
    var repostTarget by remember { mutableStateOf<CommunityRepository.PostRow?>(null) }
    var commentSortMode by remember { mutableStateOf(DetailCommentSortMode.Popular) }
    var filterMenuExpanded by remember { mutableStateOf(false) }

    // Pre-compute comment structure outside LazyListScope so remember{} is valid
    val topLevelComments = remember(comments) { comments.filter { it.parent_comment_id == null } }
    val sortedTopLevel = remember(comments, commentLikes, commentSortMode) {
        when (commentSortMode) {
            DetailCommentSortMode.Popular -> topLevelComments.sortedByDescending { commentLikes[it.id]?.size ?: 0 }
            DetailCommentSortMode.Newest -> topLevelComments.sortedByDescending { it.created_at }
            DetailCommentSortMode.Oldest -> topLevelComments.sortedBy { it.created_at }
        }
    }
    val repliesByComment = remember(comments) {
        comments.groupBy { it.parent_comment_id }
    }
    val sortedRepliesByComment = remember(comments, commentLikes, commentSortMode) {
        topLevelComments.associateWith { parent ->
            val replies = repliesByComment[parent.id] ?: emptyList()
            when (commentSortMode) {
                DetailCommentSortMode.Popular -> replies.sortedByDescending { commentLikes[it.id]?.size ?: 0 }
                DetailCommentSortMode.Newest -> replies.sortedByDescending { it.created_at }
                DetailCommentSortMode.Oldest -> replies.sortedBy { it.created_at }
            }
        }
    }

    BackHandler { navController.popBackStack() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Obsidian)
            .navigationBarsPadding()
            .imePadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Back button
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .sculpted(
                            shape = CircleShape,
                            accent = Citrine,
                            shadowElevation = 5.dp,
                            circular = true,
                            onClick = { navController.popBackStack() },
                        )
                        .clip(CircleShape)
                        .background(Color(0xCC0C0F14))
                        .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }
        }

        // Post header: avatar + author + category
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Citrine.copy(alpha = 0.18f))
                        .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(authorAvatar, style = MaterialTheme.typography.titleSmall)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        authorName,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isMe) Citrine else DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val category = CommunityRepository.resolveCategory(post.category)
                    val categoryAccent = when (category) {
                        CommunityRepository.Category.IDENTIFICATION -> Citrine
                        CommunityRepository.Category.LOCATION_TIPS -> Aqua
                        CommunityRepository.Category.GENERAL -> Color(0xFFB8A0FF)
                    }
                    Text(
                        category.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = categoryAccent,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        // Post title + tagline + description
        item {
            Column {
                Text(post.title, style = MaterialTheme.typography.headlineSmall, color = DarkTextHigh, fontWeight = FontWeight.Bold)
                if (post.tagline.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(post.tagline, style = MaterialTheme.typography.bodyLarge, color = DarkTextMid)
                }
                if (post.location_text.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text("📍 ${post.location_text}", style = MaterialTheme.typography.labelMedium, color = Aqua)
                }
                if (post.description.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(post.description, style = MaterialTheme.typography.bodyMedium, color = DarkTextHigh)
                }
            }
        }

        // Post image (full size)
        if (post.image_uri != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1A1812))
                        .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .clickable { viewerImageUrl = post.image_uri },
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        model = post.image_uri,
                        contentDescription = post.title,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }

        // Action row: Love + Repost + Comment count
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Love button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isLiked) Color(0xFF3A2818).copy(alpha = 0.85f) else Color(0xFF3A3830))
                        .glowingBorder(1.dp, Color(0xFF3A2818).copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .clickable { scope.launch { repo.toggleLike(post.id) } }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        YooperliteHeart(active = isLiked, contentDescription = if (isLiked) "Loved" else "Love", size = 20.dp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            if (likeCount > 0) likeCount.toString() else "Love",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isLiked) Color(0xFFFF8C2A) else DarkTextMid,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                if (!isMe && isFriend) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF3A3830))
                            .glowingBorder(1.dp, Aqua.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .clickable { repostTarget = post }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Repeat, contentDescription = "Repost", tint = Aqua, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Repost", style = MaterialTheme.typography.labelLarge, color = Aqua, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    "${comments.size} comment${if (comments.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.labelLarge,
                    color = DarkTextMid,
                )
            }
        }

        // Comments section header with filter button
        item {
            if (topLevelComments.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "COMMENTS",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMid,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f).padding(top = 4.dp, bottom = 4.dp),
                    )
                    if (topLevelComments.size > 1) {
                        Box {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF2A2820))
                                    .glowingBorder(1.dp, Citrine.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                                    .clickable { filterMenuExpanded = true }
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(Icons.Filled.FilterList, contentDescription = "Sort comments", tint = Citrine, modifier = Modifier.size(14.dp))
                                Text(
                                    when (commentSortMode) {
                                        DetailCommentSortMode.Popular -> "Most Popular"
                                        DetailCommentSortMode.Newest -> "Most Recent"
                                        DetailCommentSortMode.Oldest -> "Oldest"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Citrine,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            DropdownMenu(
                                expanded = filterMenuExpanded,
                                onDismissRequest = { filterMenuExpanded = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Most Popular", color = if (commentSortMode == DetailCommentSortMode.Popular) Citrine else DarkTextHigh, fontWeight = if (commentSortMode == DetailCommentSortMode.Popular) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = { commentSortMode = DetailCommentSortMode.Popular; filterMenuExpanded = false },
                                )
                                DropdownMenuItem(
                                    text = { Text("Most Recent", color = if (commentSortMode == DetailCommentSortMode.Newest) Citrine else DarkTextHigh, fontWeight = if (commentSortMode == DetailCommentSortMode.Newest) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = { commentSortMode = DetailCommentSortMode.Newest; filterMenuExpanded = false },
                                )
                                DropdownMenuItem(
                                    text = { Text("Oldest", color = if (commentSortMode == DetailCommentSortMode.Oldest) Citrine else DarkTextHigh, fontWeight = if (commentSortMode == DetailCommentSortMode.Oldest) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = { commentSortMode = DetailCommentSortMode.Oldest; filterMenuExpanded = false },
                                )
                            }
                        }
                    }
                }
            }
        }

        // Individual top-level comments with per-comment reply expansion
        sortedTopLevel.forEach { comment ->
            val sortedReplies = sortedRepliesByComment[comment] ?: emptyList()
            val isExpanded = expandedReplies.value.contains(comment.id)

            item(key = "comment_${comment.id}") {
                DetailCommentRow(
                    comment = comment,
                    isMine = comment.user_id == myUserId,
                    likeCount = commentLikes[comment.id]?.size ?: 0,
                    isLiked = likedCommentIds.contains(comment.id),
                    onLike = { scope.launch { repo.toggleCommentLike(comment.id) } },
                    onReply = { replyingToCommentId = comment.id },
                    isReplying = replyingToCommentId == comment.id,
                    onImageClick = { url -> viewerImageUrl = url },
                )
            }

            // Show first reply inline (always visible) — indented under its parent
            if (sortedReplies.isNotEmpty()) {
                item(key = "first_reply_${comment.id}") {
                    val firstReply = sortedReplies.first()
                    DetailCommentRow(
                        comment = firstReply,
                        isMine = firstReply.user_id == myUserId,
                        likeCount = commentLikes[firstReply.id]?.size ?: 0,
                        isLiked = likedCommentIds.contains(firstReply.id),
                        onLike = { scope.launch { repo.toggleCommentLike(firstReply.id) } },
                        onReply = {},
                        isReplying = false,
                        isReply = true,
                        parentCommentBody = comment.body,
                        onImageClick = { url -> viewerImageUrl = url },
                    )
                }

                // "View all N replies" / "Collapse" button when there are more than 1 reply
                if (sortedReplies.size > 1) {
                    item(key = "expand_replies_${comment.id}") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    expandedReplies.value = if (isExpanded) {
                                        expandedReplies.value - comment.id
                                    } else {
                                        expandedReplies.value + comment.id
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = null,
                                tint = Citrine,
                                modifier = Modifier.size(14.dp),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                if (isExpanded) "Collapse" else "View all ${sortedReplies.size} replies",
                                style = MaterialTheme.typography.labelSmall,
                                color = Citrine,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    // Expanded replies (all except the first one already shown) — indented under their parent
                    if (isExpanded) {
                        item(key = "expanded_replies_${comment.id}") {
                            Column {
                                sortedReplies.drop(1).forEach { reply ->
                                    DetailCommentRow(
                                        comment = reply,
                                        isMine = reply.user_id == myUserId,
                                        likeCount = commentLikes[reply.id]?.size ?: 0,
                                        isLiked = likedCommentIds.contains(reply.id),
                                        onLike = { scope.launch { repo.toggleCommentLike(reply.id) } },
                                        onReply = {},
                                        isReplying = false,
                                        isReply = true,
                                        parentCommentBody = comment.body,
                                        onImageClick = { url -> viewerImageUrl = url },
                                    )
                                    Spacer(Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Reply composer for this comment — indented under the parent comment
            if (replyingToCommentId == comment.id) {
                item(key = "reply_composer_${comment.id}") {
                    Column(modifier = Modifier.padding(start = 20.dp, top = 6.dp)) {
                        DetailReplyComposer(
                            body = replyBodies.value[comment.id] ?: "",
                            onBodyChange = { body ->
                                replyBodies.value = replyBodies.value + (comment.id to body)
                            },
                            onSubmit = {
                                val body = replyBodies.value[comment.id] ?: ""
                                if (body.isNotBlank()) {
                                    scope.launch {
                                        repo.addComment(post.id, body, comment.id, null)
                                        replyBodies.value = replyBodies.value - comment.id
                                        replyingToCommentId = null
                                    }
                                }
                            },
                            onCancel = { replyingToCommentId = null },
                        )
                    }
                }
            }
        }
    }

    // Full-screen image viewer
    viewerImageUrl?.let { url ->
        FullScreenImageViewer(
            imageUrls = listOf(url),
            initialPage = 0,
            onDismiss = { viewerImageUrl = null },
        )
    }

    // Repost composer
    repostTarget?.let { p ->
        val repAuthor = authors[p.user_id]
        ShareToProfileComposer(
            sourceType = "community_repost",
            title = p.title,
            tagline = if (p.tagline.isNotBlank()) {
                "Reposted from ${repAuthor?.displayName ?: "community"}: ${p.tagline}"
            } else {
                "Reposted from ${repAuthor?.displayName ?: "community"}"
            },
            imageUri = p.image_uri,
            locationText = p.location_text,
            onDismiss = { repostTarget = null },
        )
    }
}

@Composable
private fun DetailCommentRow(
    comment: CommunityRepository.CommentRow,
    isMine: Boolean,
    likeCount: Int,
    isLiked: Boolean,
    onLike: () -> Unit,
    onReply: () -> Unit,
    isReplying: Boolean,
    isReply: Boolean = false,
    parentCommentBody: String? = null,
    onImageClick: ((String) -> Unit)? = null,
) {
    val accent = if (isMine) Citrine else Aqua
    val commentShape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = if (isReply) 20.dp else 0.dp)
            .clip(commentShape)
            .background(if (isReply) Color(0xFF2E2C26) else Color(0xFF3A3830))
            .glowingBorder(1.dp, accent.copy(alpha = 0.35f), commentShape)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.25f))
                    .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(if (isMine) "🫵" else "⛏️", style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isLiked) Color(0xFF3A2818).copy(alpha = 0.85f) else Color.Transparent)
                    .clickable(onClick = onLike)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                YooperliteHeart(
                    active = isLiked,
                    contentDescription = if (isLiked) "Loved" else "Love this comment",
                    size = 16.dp,
                )
                if (likeCount > 0) {
                    Spacer(Modifier.width(3.dp))
                    Text(
                        likeCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isLiked) Color(0xFFFF8C2A) else TextMid,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
        }
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            if (isReply && parentCommentBody != null) {
                Text(
                    "\u21b3 replying to \"${parentCommentBody.take(40)}${if (parentCommentBody.length > 40) "\u2026" else ""}\"",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMid,
                    fontWeight = FontWeight.Normal,
                )
                Spacer(Modifier.height(4.dp))
            }
            Text(comment.body, style = MaterialTheme.typography.bodyMedium, color = DarkTextHigh)
            if (comment.image_uri != null) {
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 180.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1A1812))
                        .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .clickable { onImageClick?.invoke(comment.image_uri) },
                ) {
                    AsyncImage(
                        model = comment.image_uri,
                        contentDescription = "Comment image",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            if (!isReply) {
                Text(
                    "Reply",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isReplying) Citrine else TextMid,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onReply() },
                )
            }
        }
    }
}

@Composable
private fun DetailReplyComposer(
    body: String,
    onBodyChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
) {
    Column {
        androidx.compose.material3.OutlinedTextField(
            value = body,
            onValueChange = onBodyChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Write a reply…", color = DarkTextMid) },
            maxLines = 3,
            colors = androidx.compose.material3.TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF3A3830),
                unfocusedContainerColor = Color(0xFF3A3830),
                focusedTextColor = DarkTextHigh,
                unfocusedTextColor = DarkTextHigh,
                focusedIndicatorColor = Citrine,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Citrine,
            ),
            shape = RoundedCornerShape(12.dp),
        )
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SculptedButton(
                text = "Cancel",
                onClick = onCancel,
                accent = DarkTextMid,
                textColor = DarkTextMid,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            )
            val canSubmit = body.isNotBlank()
            SculptedButton(
                text = "Reply",
                onClick = { if (canSubmit) onSubmit() },
                accent = Citrine,
                containerColor = if (canSubmit) Citrine else Color(0xFF3A3830),
                textColor = if (canSubmit) Ink else DarkTextMid,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                enabled = canSubmit,
            )
        }
    }
}

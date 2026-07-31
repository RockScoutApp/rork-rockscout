package com.rork.rockscout.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.CommunityRepository
import com.rork.rockscout.data.ImageModerator
import com.rork.rockscout.data.ImageReviewRepository
import com.rork.rockscout.data.ImageUtils
import com.rork.rockscout.data.ModerationTriState
import com.rork.rockscout.data.SavedImage
import com.rork.rockscout.ui.components.SavedImagesPickerDialog
import com.rork.rockscout.ui.components.processSavedImage
import kotlinx.coroutines.launch
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid


/** Sort mode for community post comments. */
private enum class CommunityCommentSortMode { Popular, Newest, Oldest }

/**
 * Compact card for the community feed. Shows author, title, tagline,
 * description preview, optional image, love button, comment count,
 * repost button (inside the card), delete (author only), and expandable
 * comments. Comments are open to all signed-in users.
 *
 * @param onRepost Called when the repost button is tapped. Only shown when
 *   the viewer is not the post author.
 * @param onDelete Called when the delete button is tapped. Only shown when
 *   the viewer is the post author.
 * @param onRestore Optional restore callback for expired posts.
 */
@Composable
fun CommunityPostCard(
    post: CommunityRepository.PostRow,
    authorName: String,
    authorAvatar: String,
    isMe: Boolean,
    isLiked: Boolean,
    likeCount: Int,
    comments: List<CommunityRepository.CommentRow>,
    commentLikes: Map<String, List<CommunityRepository.CommentLikeRow>>,
    likedCommentIds: Set<String>,
    myUserId: String?,
    hoursUntilExpiry: Long,
    commentBody: String,
    replyingToCommentId: String?,
    replyBody: String,
    commentImageUri: String?,
    replyImageUri: String?,
    commentImageModerating: Boolean,
    replyImageModerating: Boolean,
    commentImageError: String?,
    replyImageError: String?,
    onLike: () -> Unit,
    onCommentChange: (String) -> Unit,
    onComment: () -> Unit,
    onReplyStart: (commentId: String) -> Unit,
    onReplyBodyChange: (String) -> Unit,
    onReplySubmit: () -> Unit,
    onCommentLike: (commentId: String) -> Unit,
    onCommentImagePicked: ((String?) -> Unit)? = null,
    onReplyImagePicked: ((String?) -> Unit)? = null,
    onCommentImageRemove: (() -> Unit)? = null,
    onReplyImageRemove: (() -> Unit)? = null,
    onRepost: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onRestore: (() -> Unit)? = null,
    onDeleteComment: ((commentId: String) -> Unit)? = null,
    onImageClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var viewerImageUrl by remember { mutableStateOf<String?>(null) }
    var isExpanded by remember(post.id) { mutableStateOf(false) }

    DarkCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        accent = Citrine,
    ) {
        // ─── Author row ───
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Citrine.copy(alpha = 0.18f))
                    .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(authorAvatar, style = MaterialTheme.typography.labelMedium)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    authorName,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isMe) Citrine else DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                // Expiry badge — amber when < 48 hours
                if (hoursUntilExpiry in 0..48) {
                    val daysLeft = (hoursUntilExpiry / 24).toInt()
                    val hoursLeft = (hoursUntilExpiry % 24).toInt()
                    val expiryText = if (daysLeft > 0) "${daysLeft}d ${hoursLeft}h left" else "${hoursLeft}h left"
                    Text(
                        "⏳ $expiryText",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFE8A33D),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            if (onRestore != null) {
                SculptedOutlinedButton(
                    text = "Restore",
                    onClick = onRestore,
                    accent = Citrine,
                    icon = Icons.Filled.Unarchive,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                )
                Spacer(Modifier.width(6.dp))
            }
            if (onDelete != null) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
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
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // ─── Comment expand toggle + title row ───
        var commentsExpanded by remember(post.id) { mutableStateOf(false) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    post.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                )
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
            }
        }

        // Category badge + location
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Compact-only preview thumbnail shown inline next to the metadata when collapsed
            if (!isExpanded && post.image_uri != null) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1A1812))
                        .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .clickable {
                            if (onImageClick != null) onImageClick()
                            else viewerImageUrl = post.image_uri
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        model = post.image_uri,
                        contentDescription = post.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
                Spacer(Modifier.width(8.dp))
            }
            val category = CommunityRepository.resolveCategory(post.category)
            val categoryAccent = when (category) {
                CommunityRepository.Category.IDENTIFICATION -> Citrine
                CommunityRepository.Category.LOCATION_TIPS -> Aqua
                CommunityRepository.Category.GENERAL -> Color(0xFFB8A0FF)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(categoryAccent.copy(alpha = 0.18f))
                    .glowingBorder(1.dp, categoryAccent.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    category.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = categoryAccent,
                    fontWeight = FontWeight.Bold,
                )
            }
            if (post.location_text.isNotBlank()) {
                Text(
                    "📍 ${post.location_text}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Aqua,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // ─── Image (optional, full size only when expanded) ───
        AnimatedVisibility(visible = isExpanded && post.image_uri != null) {
            Column {
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1A1812))
                        .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .clickable {
                            if (onImageClick != null) onImageClick()
                            else viewerImageUrl = post.image_uri
                        },
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

        // ─── Description ───
        if (post.description.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                post.description,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextHigh,
                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
            )
            if (!isExpanded && post.description.length > 100) {
                Spacer(Modifier.height(2.dp))
                Text(
                    "Tap to read more",
                    style = MaterialTheme.typography.labelSmall,
                    color = Citrine,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // ─── Expand indicator (compact only) ───
        if (!isExpanded) {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = "Expand post",
                    tint = Citrine.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        // ─── Action row: Love · Repost · Comment count ───
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
                    .clickable(onClick = onLike)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .sculpted(
                                shape = RoundedCornerShape(15.dp),
                                accent = Citrine,
                                shadowElevation = 3.dp,
                                circular = true,
                            )
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center,
                    ) {
                        YooperliteHeart(
                            active = isLiked,
                            contentDescription = if (isLiked) "Loved" else "Love this post",
                            size = 18.dp,
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (likeCount > 0) likeCount.toString() else "Love",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isLiked) Color(0xFFFF8C2A) else DarkTextMid,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            // Repost button — only for other users' posts
            if (onRepost != null && !isMe) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF3A3830))
                        .glowingBorder(1.dp, Aqua.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .clickable(onClick = onRepost)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Repeat,
                            contentDescription = "Repost to profile",
                            tint = Aqua,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Repost",
                            style = MaterialTheme.typography.labelMedium,
                            color = Aqua,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
            }
            Text(
                "${comments.size} comment${if (comments.size != 1) "s" else ""}",
                style = MaterialTheme.typography.labelMedium,
                color = DarkTextMid,
            )
            if (!isExpanded && comments.isNotEmpty()) {
                Spacer(Modifier.width(6.dp))
                Text(
                    "·",
                    style = MaterialTheme.typography.labelMedium,
                    color = DarkTextMid,
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "tap to view",
                    style = MaterialTheme.typography.labelMedium,
                    color = Citrine,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // ─── Comments (most popular comment + reply when collapsed, all when expanded) ───
        AnimatedVisibility(visible = isExpanded && comments.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            val topLevel = comments.filter { it.parent_comment_id == null }
            val rankedTopLevel = remember(comments, commentLikes) {
                topLevel.sortedByDescending { commentLikes[it.id]?.size ?: 0 }
            }
            val topComment = rankedTopLevel.firstOrNull()
            val topReply = remember(comments, commentLikes, topComment) {
                topComment?.let { tc ->
                    comments.filter { it.parent_comment_id == tc.id }
                        .sortedByDescending { commentLikes[it.id]?.size ?: 0 }
                        .firstOrNull()
                }
            }
            val hasMore = topLevel.size > 1 || (topComment?.let { c -> comments.count { it.parent_comment_id == c.id } } ?: 0) > 1

            // Comment sort filter dropdown
            var commentSortMode by remember(post.id) { mutableStateOf(CommunityCommentSortMode.Popular) }
            var filterMenuExpanded by remember(post.id) { mutableStateOf(false) }
            val sortedTopLevel: List<CommunityRepository.CommentRow> = when (commentSortMode) {
                CommunityCommentSortMode.Popular -> rankedTopLevel
                CommunityCommentSortMode.Newest -> topLevel.sortedByDescending { it.created_at }
                CommunityCommentSortMode.Oldest -> topLevel.sortedBy { it.created_at }
            }

            val visibleTopLevel: List<CommunityRepository.CommentRow> =
                if (commentsExpanded) sortedTopLevel else rankedTopLevel.take(1)

            // Filter button row
            if (commentsExpanded && topLevel.size > 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
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
                                    CommunityCommentSortMode.Popular -> "Most Popular"
                                    CommunityCommentSortMode.Newest -> "Most Recent"
                                    CommunityCommentSortMode.Oldest -> "Oldest"
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
                                text = { Text("Most Popular", color = if (commentSortMode == CommunityCommentSortMode.Popular) Citrine else DarkTextHigh, fontWeight = if (commentSortMode == CommunityCommentSortMode.Popular) FontWeight.Bold else FontWeight.Normal) },
                                onClick = { commentSortMode = CommunityCommentSortMode.Popular; filterMenuExpanded = false },
                            )
                            DropdownMenuItem(
                                text = { Text("Most Recent", color = if (commentSortMode == CommunityCommentSortMode.Newest) Citrine else DarkTextHigh, fontWeight = if (commentSortMode == CommunityCommentSortMode.Newest) FontWeight.Bold else FontWeight.Normal) },
                                onClick = { commentSortMode = CommunityCommentSortMode.Newest; filterMenuExpanded = false },
                            )
                            DropdownMenuItem(
                                text = { Text("Oldest", color = if (commentSortMode == CommunityCommentSortMode.Oldest) Citrine else DarkTextHigh, fontWeight = if (commentSortMode == CommunityCommentSortMode.Oldest) FontWeight.Bold else FontWeight.Normal) },
                                onClick = { commentSortMode = CommunityCommentSortMode.Oldest; filterMenuExpanded = false },
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            visibleTopLevel.forEach { comment ->
                val allReplies = comments.filter { it.parent_comment_id == comment.id }
                val sortedReplies: List<CommunityRepository.CommentRow> = when (commentSortMode) {
                    CommunityCommentSortMode.Popular -> allReplies.sortedByDescending { commentLikes[it.id]?.size ?: 0 }
                    CommunityCommentSortMode.Newest -> allReplies.sortedByDescending { it.created_at }
                    CommunityCommentSortMode.Oldest -> allReplies.sortedBy { it.created_at }
                }
                val visibleReplies: List<CommunityRepository.CommentRow> =
                    if (commentsExpanded) sortedReplies else {
                        // Collapsed: show top reply only if this is the top comment
                        if (comment == topComment && topReply != null) listOf(topReply) else emptyList()
                    }

                CommunityCommentRow(
                    comment = comment,
                    isMine = comment.user_id == myUserId,
                    likeCount = commentLikes[comment.id]?.size ?: 0,
                    isLiked = likedCommentIds.contains(comment.id),
                    onLike = { onCommentLike(comment.id) },
                    onReply = { onReplyStart(comment.id) },
                    isReplying = replyingToCommentId == comment.id,
                    canDelete = comment.user_id == myUserId,
                    onDelete = onDeleteComment?.let { cb -> { cb(comment.id) } },
                    onImageClick = { url -> viewerImageUrl = url },
                )
                if (replyingToCommentId == comment.id) {
                    CommunityReplyComposer(
                        body = replyBody,
                        onBodyChange = onReplyBodyChange,
                        onSubmit = onReplySubmit,
                        imageUri = replyImageUri,
                        imageModerating = replyImageModerating,
                        imageError = replyImageError,
                        onImagePicked = onReplyImagePicked,
                        onImageRemove = onReplyImageRemove,
                        modifier = Modifier.padding(start = 20.dp, top = 6.dp),
                    )
                }
                if (visibleReplies.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    visibleReplies.forEach { reply ->
                        CommunityCommentRow(
                            comment = reply,
                            isMine = reply.user_id == myUserId,
                            likeCount = commentLikes[reply.id]?.size ?: 0,
                            isLiked = likedCommentIds.contains(reply.id),
                            onLike = { onCommentLike(reply.id) },
                            onReply = { onReplyStart(reply.id) },
                            isReplying = replyingToCommentId == reply.id,
                            isReply = true,
                            parentCommentBody = comment.body,
                            canDelete = reply.user_id == myUserId,
                            onDelete = onDeleteComment?.let { cb -> { cb(reply.id) } },
                            onImageClick = { url -> viewerImageUrl = url },
                        )
                        if (replyingToCommentId == reply.id) {
                            CommunityReplyComposer(
                                body = replyBody,
                                onBodyChange = onReplyBodyChange,
                                onSubmit = onReplySubmit,
                                imageUri = replyImageUri,
                                imageModerating = replyImageModerating,
                                imageError = replyImageError,
                                onImagePicked = onReplyImagePicked,
                                onImageRemove = onReplyImageRemove,
                                modifier = Modifier.padding(start = 40.dp, top = 6.dp),
                            )
                        }
                        if (reply != visibleReplies.last()) Spacer(Modifier.height(4.dp))
                    }
                }
                if (comment != visibleTopLevel.last()) Spacer(Modifier.height(6.dp))
            }

            // Expand / collapse button when there are more comments/replies to show
            if (hasMore) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (commentsExpanded) Citrine.copy(alpha = 0.20f) else Color(0xFF2A2820))
                            .glowingBorder(1.dp, Citrine.copy(alpha = 0.55f), RoundedCornerShape(20.dp))
                            .clickable { commentsExpanded = !commentsExpanded }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = if (commentsExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = if (commentsExpanded) "Collapse comments" else "Expand comments",
                                tint = Citrine,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                if (commentsExpanded) "Collapse comments" else "${comments.size} comment${if (comments.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Citrine,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }

        // ─── Comment input (expanded only) ───
        AnimatedVisibility(visible = isExpanded) {
            Column {
                Spacer(Modifier.height(10.dp))
                CommunityCommentInputRow(
                    body = commentBody,
                    onBodyChange = onCommentChange,
                    onSubmit = onComment,
                    imageUri = commentImageUri,
                    imageModerating = commentImageModerating,
                    imageError = commentImageError,
                    onImagePicked = onCommentImagePicked,
                    onImageRemove = onCommentImageRemove,
                    placeholder = "Add a comment…",
                )
            }
        }
    }

    // ─── Full-screen image viewer ───
    viewerImageUrl?.let { url ->
        FullScreenImageViewer(
            imageUrls = listOf(url),
            initialPage = 0,
            onDismiss = { viewerImageUrl = null },
        )
    }
}

@Composable
private fun CommunityCommentRow(
    comment: CommunityRepository.CommentRow,
    isMine: Boolean,
    likeCount: Int,
    isLiked: Boolean,
    onLike: () -> Unit,
    onReply: () -> Unit,
    isReplying: Boolean,
    isReply: Boolean = false,
    parentCommentBody: String? = null,
    canDelete: Boolean = false,
    onDelete: (() -> Unit)? = null,
    onImageClick: ((String) -> Unit)? = null,
) {
    val accent = if (isMine) Citrine else Aqua
    val commentShape = RoundedCornerShape(10.dp)
    var showDeleteConfirm by remember { mutableStateOf(false) }
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
                    color = TextLow,
                    fontWeight = FontWeight.Normal,
                )
                Spacer(Modifier.height(4.dp))
            }
            Text(
                comment.body,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextHigh,
            )
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
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Reply",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isReplying) Citrine else TextMid,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onReply() },
                )
                if (canDelete && onDelete != null) {
                    Text(
                        "Delete",
                        style = MaterialTheme.typography.labelSmall,
                        color = Danger,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showDeleteConfirm = true },
                    )
                }
            }
        }
    }

    // Comment deletion confirmation
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = Color(0xFF2A2820),
            titleContentColor = DarkTextHigh,
            textContentColor = TextLow,
            title = { Text("Delete comment?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    if (isReply) "Delete this reply? This action cannot be undone."
                    else "Delete this comment and all replies to it? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextLow,
                )
            },
            confirmButton = {
                SculptedTextButton(
                    text = "Delete",
                    onClick = { showDeleteConfirm = false; onDelete?.invoke() },
                    accent = Danger,
                    textColor = Danger,
                    fontWeight = FontWeight.Bold,
                )
            },
            dismissButton = {
                SculptedTextButton(
                    text = "Cancel",
                    onClick = { showDeleteConfirm = false },
                    accent = Aqua,
                    textColor = TextLow,
                )
            },
        )
    }
}

@Composable
fun CommunityReplyComposer(
    body: String,
    onBodyChange: (String) -> Unit,
    onSubmit: () -> Unit,
    imageUri: String? = null,
    imageModerating: Boolean = false,
    imageError: String? = null,
    onImagePicked: ((String?) -> Unit)? = null,
    onImageRemove: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        CommunityCommentInputRow(
            body = body,
            onBodyChange = onBodyChange,
            onSubmit = onSubmit,
            imageUri = imageUri,
            imageModerating = imageModerating,
            imageError = imageError,
            onImagePicked = onImagePicked,
            onImageRemove = onImageRemove,
            placeholder = "Write a reply…",
            modifier = Modifier.fillMaxWidth(),
        )
        if (onCancel != null) {
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SculptedButton(
                    text = "Cancel",
                    onClick = onCancel,
                    accent = DarkTextMid,
                    textColor = DarkTextMid,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                )
                val canSubmit = body.isNotBlank() && !imageModerating &&
                    (imageUri == null || (!imageUri.startsWith("__") && imageUri != "__loading__"))
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
}

/** Reusable comment/reply input row with optional image attachment and moderation. */
@Composable
fun CommunityCommentInputRow(
    body: String,
    onBodyChange: (String) -> Unit,
    onSubmit: () -> Unit,
    imageUri: String? = null,
    imageModerating: Boolean = false,
    imageError: String? = null,
    onImagePicked: ((String?) -> Unit)? = null,
    onImageRemove: (() -> Unit)? = null,
    placeholder: String = "Add a comment…",
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri != null && onImagePicked != null) {
            // Reject files larger than 5 MB before any pipeline work.
            if (ImageUtils.isOverUploadLimit(context, uri)) {
                onImagePicked("__error:That image is over 5 MB. Please choose a smaller photo.")
                return@rememberLauncherForActivityResult
            }
            onImagePicked("__loading__")
            scope.launch {
                val base64 = ImageUtils.uriToModerationBase64(context, uri)
                if (base64 == null) {
                    onImagePicked(null)
                    return@launch
                }
                val verdict = ImageModerator.scan(base64, "image/jpeg")
                when (verdict.triState) {
                    ModerationTriState.CLEAN -> {
                        val persistentPath = ImageUtils.copyUriToInternalStorage(
                            context, uri, "comment_images",
                        )
                        onImagePicked(persistentPath ?: uri.toString())
                    }
                    ModerationTriState.EXPLICIT -> {
                        onImagePicked("__error:${verdict.reason.ifBlank { "This image can't be used because it violates our content policies." }}")
                    }
                    ModerationTriState.QUESTIONABLE -> {
                        val persistentPath = ImageUtils.copyUriToInternalStorage(
                            context, uri, "comment_images",
                        )
                        val userId = AuthRepository.instance.currentUserId
                        val userName = AppRepository.instance.profile.value.name
                        val avatar = AppRepository.instance.profile.value.avatarEmoji
                        ImageReviewRepository.instance.submitReview(
                            userId = userId ?: "unknown",
                            userName = userName,
                            userAvatar = avatar,
                            imageUri = persistentPath ?: uri.toString(),
                            type = "comment_image",
                            reason = verdict.reason,
                        )
                        onImagePicked(persistentPath ?: uri.toString())
                    }
                }
            }
        }
    }

    var showSavedImagePicker by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Image preview
        if (imageUri != null && imageUri != "__loading__" && !imageUri.startsWith("__error:")) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 140.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1A1812))
                    .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
            ) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Attached image",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit,
                )
                if (onImageRemove != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2A2820))
                            .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape)
                            .clickable { onImageRemove() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Remove image",
                            tint = DarkTextHigh,
                            modifier = Modifier.size(12.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
        }
        if (imageModerating) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    strokeWidth = 2.dp,
                    color = Citrine,
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Scanning image…",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTextMid,
                )
            }
            Spacer(Modifier.height(4.dp))
        }
        if (imageError != null) {
            Text(
                imageError,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFE2574C),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(4.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Image attach button
            if (onImagePicked != null && (imageUri == null || imageUri == "__loading__" || imageUri.startsWith("__error:"))) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3A3830))
                        .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape)
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.PhotoLibrary,
                        contentDescription = "Attach image",
                        tint = Citrine,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3A3830))
                        .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape)
                        .clickable { showSavedImagePicker = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Download,
                        contentDescription = "Attach saved image",
                        tint = Citrine,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(8.dp))
            }
            OutlinedTextField(
                value = body,
                onValueChange = onBodyChange,
                modifier = Modifier.weight(1f).noAutoFocus(),
                placeholder = { Text(placeholder, color = DarkTextMid) },
                maxLines = 3,
                colors = TextFieldDefaults.colors(
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
            Spacer(Modifier.width(8.dp))
            val canSubmit = body.isNotBlank() && !imageModerating &&
                (imageUri == null || (!imageUri.startsWith("__") && imageUri != "__loading__"))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (canSubmit) Citrine else Color(0xFF3A3830))
                    .clickable(enabled = canSubmit, onClick = onSubmit),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Send,
                    contentDescription = "Send",
                    tint = if (canSubmit) Ink else DarkTextMid,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }

    if (showSavedImagePicker) {
        SavedImagesPickerDialog(
            onDismiss = { showSavedImagePicker = false },
            onImageSelected = { image: SavedImage ->
                showSavedImagePicker = false
                if (onImagePicked != null) {
                    onImagePicked("__loading__")
                    scope.launch {
                        val path = processSavedImage(context, image, "comment_images", "comment_image")
                        onImagePicked(path ?: "__error:Could not use saved image.")
                    }
                }
            },
        )
    }
}

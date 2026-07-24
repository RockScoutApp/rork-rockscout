package com.rork.rockscout.ui.components

import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.rork.rockscout.R
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.CapturedPhoto
import com.rork.rockscout.data.CollectionEntry
import com.rork.rockscout.data.ImageModerator
import com.rork.rockscout.data.ImageReviewRepository
import com.rork.rockscout.data.ImageUtils
import com.rork.rockscout.data.JournalEntry
import com.rork.rockscout.data.ModerationTriState
import com.rork.rockscout.data.PostRepository
import com.rork.rockscout.data.SavedImage
import com.rork.rockscout.data.ProfanityFilter
import com.rork.rockscout.data.RocksAreAmazingSpecimens
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.data.SpecimenImages
import com.rork.rockscout.data.TradeListing
import com.rork.rockscout.data.Trip
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.components.SavedImagesPickerDialog
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.YooperliteHeart
import com.rork.rockscout.ui.components.processSavedImage
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.components.glowingBorder

/** A clickable empty-post box shown when the user has no posts yet.
 *  Sized to match the full-width ID tile on the home screen. */
@Composable
fun EmptyPostBox(
    isMe: Boolean,
    onCreatePost: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .sculpted(shape = shape, accent = Citrine, shadowElevation = 6.dp, enabled = isMe, onClick = onCreatePost)
            .clip(shape)
            .glowingBorder(3.dp, Citrine.copy(alpha = 0.50f), shape),
        contentAlignment = Alignment.Center,
    ) {
        // Rock-texture background fills the empty-post box.
        Image(
            painter = painterResource(id = R.drawable.level_tile_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        // Dark scrim keeps the text and icon perfectly legible over the busy rock image.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.35f), Color.Black.copy(alpha = 0.55f), Color.Black.copy(alpha = 0.72f))
                    )
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(90.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Citrine.copy(alpha = 0.18f), Color.Transparent)
                    )
                ),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Citrine.copy(alpha = 0.18f))
                    .glowingBorder(3.dp, Citrine.copy(alpha = 0.55f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null,
                    tint = Citrine,
                    modifier = Modifier.size(28.dp),
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (isMe) "Post Something!" else "No Posts Available",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            if (isMe) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Share a specimen, trip, or field find to your profile.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/** Pill-shaped button used to open the create-post sheet. */
@Composable
fun CreatePostButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Citrine.copy(alpha = 0.18f))
            .glowingBorder(2.dp, Citrine.copy(alpha = 0.85f), RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
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
            text = "Create Post",
            style = MaterialTheme.typography.labelLarge,
            color = Citrine,
            fontWeight = FontWeight.Bold,
        )
    }
}

/** Sort mode for the comment section of a profile post. */
private enum class CommentSortMode { Collapsed, Popular, Newest }

/** A single profile post card. Supports like, comment, reply, comment loves, image attachments on comments, and optional delete. */
@Composable
fun PostCard(
    post: PostRepository.PostRow,
    isFriend: Boolean,
    isMe: Boolean,
    isLiked: Boolean,
    likeCount: Int,
    comments: List<PostRepository.CommentRow>,
    commentLikes: Map<String, List<PostRepository.CommentLikeRow>>,
    likedCommentIds: Set<String>,
    myUserId: String?,
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
    onImageClick: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onRestore: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val canComment = isMe || isFriend
    // Journal and trip posts are friends-only — non-friends see a locked placeholder
    val isLockedPost = (post.source_type == "journal" || post.source_type == "trip") && !isFriend && !isMe
    DarkCard(modifier = modifier.fillMaxWidth(), accent = Citrine) {
        if (isLockedPost) {
            // Locked overlay for journal/trip posts visible to non-friends
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF2A2820), Color(0xFF1E1C16))
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = null,
                        tint = Citrine.copy(alpha = 0.6f),
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (post.source_type == "journal") "Field journal entries are visible to friends only"
                        else "Planned trips are visible to friends only",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
        } else if (post.image_uri != null) {
            // Image resizes to fit the post instead of cropping
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1A1812))
                    .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .then(if (onImageClick != null) Modifier.clickable(onClick = onImageClick) else Modifier),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = post.image_uri,
                    contentDescription = post.title,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit,
                )
            }
            Spacer(Modifier.height(10.dp))
        }
        // ─── Comment sort mode (shared between toggle pill and comments block) ───
        var commentSortMode by remember(post.id) {
            mutableStateOf(CommentSortMode.Collapsed)
        }

        // ─── Expand/collapse toggle pill + title row ───
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Toggle pill — top-left, opens dropdown for comment sort options
            if (comments.isNotEmpty()) {
                var pillMenuExpanded by remember(post.id) { mutableStateOf(false) }
                Box {
                    Row(
                        modifier = Modifier
                            .height(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (commentSortMode != CommentSortMode.Collapsed)
                                    Citrine.copy(alpha = 0.20f)
                                else Color(0xFF2A2820)
                            )
                            .glowingBorder(
                                1.dp,
                                Citrine.copy(
                                    alpha = if (commentSortMode != CommentSortMode.Collapsed) 0.85f else 0.45f
                                ),
                                RoundedCornerShape(16.dp),
                            )
                            .clickable { pillMenuExpanded = true }
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = if (commentSortMode != CommentSortMode.Collapsed)
                                Icons.Filled.ExpandLess
                            else Icons.Filled.ExpandMore,
                            contentDescription = "Toggle comments",
                            tint = Citrine,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = comments.size.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    DropdownMenu(
                        expanded = pillMenuExpanded,
                        onDismissRequest = { pillMenuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Most popular comments", color = DarkTextHigh) },
                            onClick = {
                                commentSortMode = CommentSortMode.Popular
                                pillMenuExpanded = false
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Newest comments", color = DarkTextHigh) },
                            onClick = {
                                commentSortMode = CommentSortMode.Newest
                                pillMenuExpanded = false
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Collapse", color = DarkTextHigh) },
                            onClick = {
                                commentSortMode = CommentSortMode.Collapsed
                                pillMenuExpanded = false
                            },
                        )
                    }
                }
            }
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
                    )
                }
            }
            if (onRestore != null) {
                SculptedOutlinedButton(
                    text = "Restore",
                    onClick = onRestore,
                    accent = Citrine,
                    icon = Icons.Filled.Unarchive,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                )
                Spacer(Modifier.width(8.dp))
            }
            if (onDelete != null) {
                SculptedIconButton(
                    icon = Icons.Filled.Close,
                    contentDescription = "Delete post",
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp),
                    accent = Citrine,
                    iconTint = TextLow,
                    size = 36.dp,
                    shadowElevation = 3.dp,
                )
            }
        }
        if (post.location_text.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text("📍 ${post.location_text}", style = MaterialTheme.typography.labelMedium, color = Aqua)
        }
        if (post.caption.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(post.caption, style = MaterialTheme.typography.bodyMedium, color = DarkTextHigh)
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
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
                            .size(34.dp)
                            .sculpted(
                                shape = RoundedCornerShape(17.dp),
                                accent = Citrine,
                                shadowElevation = 3.dp,
                                circular = true,
                            )
                            .clip(RoundedCornerShape(17.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center,
                    ) {
                        YooperliteHeart(
                            active = isLiked,
                            contentDescription = if (isLiked) "Loved" else "Love this post",
                            size = 20.dp,
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
            Text(
                "${comments.size} comment${if (comments.size != 1) "s" else ""}",
                style = MaterialTheme.typography.labelMedium,
                color = DarkTextMid,
            )
        }
        if (comments.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            val topLevel = comments.filter { it.parent_comment_id == null }
            val rankedTopLevel = remember(comments, commentLikes) {
                topLevel.sortedByDescending { commentLikes[it.id]?.size ?: 0 }
            }
            val newestTopLevel = remember(comments) {
                topLevel.sortedByDescending { it.created_at }
            }
            val topComment = rankedTopLevel.firstOrNull()
            val topCommentReplyCount = topComment?.let { c ->
                comments.count { it.parent_comment_id == c.id }
            } ?: 0
            val collapsible = rankedTopLevel.size > 1 || topCommentReplyCount > 1

            val visibleTopLevel: List<PostRepository.CommentRow> = when (commentSortMode) {
                CommentSortMode.Collapsed -> rankedTopLevel.take(1)
                CommentSortMode.Popular -> rankedTopLevel
                CommentSortMode.Newest -> newestTopLevel
            }

            visibleTopLevel.forEach { comment ->
                val allReplies = comments.filter { it.parent_comment_id == comment.id }
                val rankedReplies = remember(comments, commentLikes, comment.id) {
                    allReplies.sortedByDescending { commentLikes[it.id]?.size ?: 0 }
                }
                val newestReplies = remember(comments, comment.id) {
                    allReplies.sortedByDescending { it.created_at }
                }
                val visibleReplies: List<PostRepository.CommentRow> = when (commentSortMode) {
                    CommentSortMode.Collapsed -> rankedReplies.take(1)
                    CommentSortMode.Popular -> rankedReplies
                    CommentSortMode.Newest -> newestReplies
                }

                CommentRow(
                    comment = comment,
                    isMine = comment.user_id == myUserId,
                    likeCount = commentLikes[comment.id]?.size ?: 0,
                    isLiked = likedCommentIds.contains(comment.id),
                    onLike = { onCommentLike(comment.id) },
                    onReply = { onReplyStart(comment.id) },
                    isReplying = replyingToCommentId == comment.id,
                )
                if (visibleReplies.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    visibleReplies.forEach { reply ->
                        CommentRow(
                            comment = reply,
                            isMine = reply.user_id == myUserId,
                            likeCount = commentLikes[reply.id]?.size ?: 0,
                            isLiked = likedCommentIds.contains(reply.id),
                            onLike = { onCommentLike(reply.id) },
                            onReply = { onReplyStart(reply.id) },
                            isReplying = replyingToCommentId == reply.id,
                            isReply = true,
                            parentCommentBody = comment.body,
                        )
                        if (reply != visibleReplies.last()) Spacer(Modifier.height(4.dp))
                    }
                }
                if (replyingToCommentId == comment.id) {
                    ReplyComposer(
                        body = replyBody,
                        onBodyChange = onReplyBodyChange,
                        onSubmit = onReplySubmit,
                        imageUri = replyImageUri,
                        imageModerating = replyImageModerating,
                        imageError = replyImageError,
                        onImagePicked = onReplyImagePicked,
                        onImageRemove = onReplyImageRemove,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
                if (comment != visibleTopLevel.last()) Spacer(Modifier.height(6.dp))
            }

            // Bottom comment-sort pills removed — replaced by the top-left
            // toggle pill with dropdown menu above.
        }
        if (canComment) {
            Spacer(Modifier.height(10.dp))
            CommentInputRow(
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
        } else if (comments.isEmpty()) {
            Spacer(Modifier.height(6.dp))
            Text(
                "Only RockScout Friends can comment.",
                style = MaterialTheme.typography.labelSmall,
                color = TextLow,
            )
        }
    }
}

@Composable
private fun CommentRow(
    comment: PostRepository.CommentRow,
    isMine: Boolean,
    likeCount: Int,
    isLiked: Boolean,
    onLike: () -> Unit,
    onReply: () -> Unit,
    isReplying: Boolean,
    isReply: Boolean = false,
    parentCommentBody: String? = null,
) {
    val accent = if (isMine) Citrine else Aqua
    val commentShape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(commentShape)
            .background(if (isReply) Color(0xFF2E2C26) else Color(0xFF3A3830))
            .glowingBorder(1.dp, accent.copy(alpha = 0.35f), commentShape)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.width(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.25f))
                    .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(if (isMine) "🫵" else "⛏️", style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
                    size = 18.dp,
                )
                if (likeCount > 0) {
                    Spacer(Modifier.width(3.dp))
                    Text(
                        likeCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isLiked) Color(0xFFFF8C2A) else TextMid,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
        }
        Spacer(Modifier.width(12.dp))
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
                        .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
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
            Row(verticalAlignment = Alignment.CenterVertically) {
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
private fun CommentSortPill(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = if (isActive) Ink else Citrine,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isActive) Citrine else Color(0xFF2A2820))
            .glowingBorder(
                1.dp,
                Citrine.copy(alpha = if (isActive) 1f else 0.45f),
                RoundedCornerShape(14.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
    )
}

@Composable
private fun ReplyComposer(
    body: String,
    onBodyChange: (String) -> Unit,
    onSubmit: () -> Unit,
    imageUri: String? = null,
    imageModerating: Boolean = false,
    imageError: String? = null,
    onImagePicked: ((String?) -> Unit)? = null,
    onImageRemove: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    CommentInputRow(
        body = body,
        onBodyChange = onBodyChange,
        onSubmit = onSubmit,
        imageUri = imageUri,
        imageModerating = imageModerating,
        imageError = imageError,
        onImagePicked = onImagePicked,
        onImageRemove = onImageRemove,
        placeholder = "Write a reply…",
        modifier = modifier,
    )
}

/** Reusable comment/reply input row with optional image attachment and moderation. */
@Composable
private fun CommentInputRow(
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

/** Bottom sheet that lets the user pick a source tab and share an item to their profile. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostSheet(
    onDismiss: () -> Unit,
    onPosted: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var phase by remember { mutableStateOf(CreatePostPhase.Categories) }
    var selectedCategory by remember { mutableStateOf<SourceCategory?>(null) }
    var selectedItem by remember { mutableStateOf<ShareableItem?>(null) }
    var caption by remember { mutableStateOf("") }
    // Text-only post fields
    var textTitle by remember { mutableStateOf("") }
    var textTagline by remember { mutableStateOf("") }
    var textLocation by remember { mutableStateOf("") }
    var textPhotoUri by remember { mutableStateOf<String?>(null) }
    val itemsByCategory = rememberShareableItemsByCategory()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1E1C16),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
        ) {
            Text(
                "Create a Post",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                when (phase) {
                    CreatePostPhase.Categories -> "Choose a tab to share something from, or write a text-only post."
                    CreatePostPhase.Items -> "Pick something from ${selectedCategory?.label ?: ""}."
                    CreatePostPhase.Compose -> "Add a caption and share it to your profile."
                    CreatePostPhase.TextCompose -> "Write a post and share it to your profile."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
            )
            Spacer(Modifier.height(16.dp))

            when (phase) {
                CreatePostPhase.Categories -> {
                    // Text-only post card at the top
                    TextPostCard(
                        onClick = {
                            textTitle = ""
                            textTagline = ""
                            caption = ""
                            textLocation = ""
                            textPhotoUri = null
                            phase = CreatePostPhase.TextCompose
                        },
                    )
                    Spacer(Modifier.height(10.dp))
                    CategoryGrid(
                        itemsByCategory = itemsByCategory,
                        onCategoryClick = { category ->
                            selectedCategory = category
                            phase = CreatePostPhase.Items
                        },
                    )
                }
                CreatePostPhase.Items -> {
                    val category = selectedCategory ?: return@Column
                    val items = itemsByCategory[category.sourceType].orEmpty()
                    ItemList(
                        items = items,
                        emptyLabel = "Nothing to share from ${category.label} yet.",
                        onBack = { phase = CreatePostPhase.Categories },
                        onItemClick = { item ->
                            selectedItem = item
                            caption = ""
                            phase = CreatePostPhase.Compose
                        },
                    )
                }
                CreatePostPhase.Compose -> {
                    val item = selectedItem ?: return@Column
                    ComposePost(
                        item = item,
                        caption = caption,
                        onCaptionChange = { caption = it },
                        onBack = { phase = CreatePostPhase.Items },
                        onPost = {
                            scope.launch {
                                PostRepository.instance.createPost(
                                    sourceType = item.sourceType,
                                    sourceRefId = item.sourceRefId,
                                    title = item.title,
                                    tagline = item.subtitle,
                                    imageUri = item.imageUri,
                                    caption = caption,
                                    locationText = item.locationText,
                                )
                                onPosted()
                                sheetState.hide()
                                onDismiss()
                            }
                        },
                    )
                }
                CreatePostPhase.TextCompose -> {
                    ComposeTextPost(
                        title = textTitle,
                        onTitleChange = { textTitle = it },
                        tagline = textTagline,
                        onTaglineChange = { textTagline = it },
                        caption = caption,
                        onCaptionChange = { caption = it },
                        locationText = textLocation,
                        onLocationChange = { textLocation = it },
                        photoUri = textPhotoUri,
                        onPhotoChange = { textPhotoUri = it },
                        onBack = { phase = CreatePostPhase.Categories },
                        onPost = {
                            scope.launch {
                                PostRepository.instance.createPost(
                                    sourceType = "text",
                                    sourceRefId = null,
                                    title = textTitle,
                                    tagline = textTagline,
                                    imageUri = textPhotoUri,
                                    caption = caption,
                                    locationText = textLocation,
                                )
                                onPosted()
                                sheetState.hide()
                                onDismiss()
                            }
                        },
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

private enum class CreatePostPhase { Categories, Items, Compose, TextCompose }

private data class SourceCategory(
    val sourceType: String,
    val label: String,
    val icon: ImageVector,
    val accent: Color,
)

private val sourceCategories = listOf(
    SourceCategory("collection", "My Rocks", Icons.Filled.Inventory2, Aqua),
    SourceCategory("wishlist", "Wish List", Icons.Filled.PlaylistAdd, Color(0xFF9B7BD8)),
    SourceCategory("capture", "Field Captures", Icons.Filled.CameraAlt, Color(0xFF5CC98C)),
    SourceCategory("spot", "Favorite Spots", Icons.Filled.Place, Color(0xFFE2574C)),
    SourceCategory("trip", "Trips", Icons.Filled.Map, Color(0xFFE8A33D)),
    SourceCategory("journal", "Field Journal", Icons.Filled.MenuBook, Color(0xFF6FA8C7)),
    SourceCategory("trade", "Trade Board", Icons.Filled.SwapHoriz, Color(0xFFE8A33D)),
    SourceCategory("rocks_are_amazing", "Rocks Are Amazing", Icons.Filled.CollectionsBookmark, Color(0xFF44AACC)),
    SourceCategory("database", "Database", Icons.Filled.Search, Color(0xFF66AADD)),
)

@Composable
private fun CategoryGrid(
    itemsByCategory: Map<String, List<ShareableItem>>,
    onCategoryClick: (SourceCategory) -> Unit,
) {
    if (itemsByCategory.values.all { it.isEmpty() }) {
        Box(
            modifier = Modifier.fillMaxWidth().height(160.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "Nothing to share yet. Add a specimen, capture, trip, or journal entry first!",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
                textAlign = TextAlign.Center,
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth().height(360.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        val rows = sourceCategories.chunked(2)
        items(rows, key = { it.joinToString { c -> c.sourceType } }) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                row.forEach { category ->
                    val count = itemsByCategory[category.sourceType]?.size ?: 0
                    CategoryCard(
                        category = category,
                        count = count,
                        modifier = Modifier.weight(1f),
                        onClick = { onCategoryClick(category) },
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: SourceCategory,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .sculpted(shape = RoundedCornerShape(16.dp), accent = category.accent, shadowElevation = 5.dp, enabled = count > 0, onClick = onClick)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2A2820))
            .glowingBorder(2.dp, category.accent.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(category.accent.copy(alpha = 0.18f))
                .glowingBorder(2.dp, category.accent.copy(alpha = 0.50f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                category.icon,
                contentDescription = null,
                tint = category.accent,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            category.label,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            "$count to share",
            style = MaterialTheme.typography.bodySmall,
            color = if (count > 0) DarkTextMid else DarkTextMid.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ItemList(
    items: List<ShareableItem>,
    emptyLabel: String,
    onBack: () -> Unit,
    onItemClick: (ShareableItem) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        BackButton(onBack)
        Spacer(Modifier.height(10.dp))
        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    emptyLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(items, key = { it.id }) { item ->
                    ShareableItemRow(item = item, onClick = { onItemClick(item) })
                }
            }
        }
    }
}

@Composable
private fun BackButton(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onBack)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Citrine,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            "Back",
            style = MaterialTheme.typography.labelLarge,
            color = Citrine,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ComposePost(
    item: ShareableItem,
    caption: String,
    onCaptionChange: (String) -> Unit,
    onBack: () -> Unit,
    onPost: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        BackButton(onBack)
        Spacer(Modifier.height(10.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A1812))
                    .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
            ) {
                if (item.imageUri != null) {
                    AsyncImage(
                        model = item.imageUri,
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                item.title,
                style = MaterialTheme.typography.titleMedium,
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
            )
            Text(
                item.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = caption,
                onValueChange = onCaptionChange,
                modifier = Modifier.fillMaxWidth().noAutoFocus(),
                placeholder = { Text("Add a caption…", color = DarkTextMid) },
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
                shape = RoundedCornerShape(12.dp),
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SculptedButton(
                    text = "Back",
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    accent = Citrine,
                    containerColor = Color(0xFF3A3830),
                    textColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                )
                SculptedButton(
                    text = "Post",
                    onClick = onPost,
                    modifier = Modifier.weight(1f),
                    accent = Citrine,
                    containerColor = Citrine,
                    textColor = Ink,
                    shape = RoundedCornerShape(12.dp),
                )
            }
        }
    }
}

/** Full-width card that launches the text-only post composer. */
@Composable
private fun TextPostCard(
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = RoundedCornerShape(16.dp), accent = Citrine, shadowElevation = 5.dp, enabled = true, onClick = onClick)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2A2820))
            .glowingBorder(2.dp, Citrine.copy(alpha = 0.50f), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Citrine.copy(alpha = 0.18f))
                .glowingBorder(2.dp, Citrine.copy(alpha = 0.50f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.TextFields,
                contentDescription = null,
                tint = Citrine,
                modifier = Modifier.size(26.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Text Post",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "Share a thought, question, or story — no image required.",
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextMid,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            Icons.Filled.Add,
            contentDescription = "Create text post",
            tint = Citrine,
            modifier = Modifier.size(24.dp),
        )
    }
}

/** Composer for a text-only profile post (title, tagline, caption, location, optional photo). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComposeTextPost(
    title: String,
    onTitleChange: (String) -> Unit,
    tagline: String,
    onTaglineChange: (String) -> Unit,
    caption: String,
    onCaptionChange: (String) -> Unit,
    locationText: String,
    onLocationChange: (String) -> Unit,
    photoUri: String?,
    onPhotoChange: (String?) -> Unit,
    onBack: () -> Unit,
    onPost: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var imageModerating by remember { mutableStateOf(false) }
    var imageError by remember { mutableStateOf<String?>(null) }
    var showSavedImagePicker by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri != null) {
            imageModerating = true
            imageError = null
            scope.launch {
                val base64 = ImageUtils.uriToModerationBase64(context, uri)
                if (base64 == null) {
                    imageModerating = false
                    imageError = "Could not load image."
                    return@launch
                }
                val verdict = ImageModerator.scan(base64, "image/jpeg")
                imageModerating = false
                when (verdict.triState) {
                    ModerationTriState.CLEAN -> {
                        val persistentPath = ImageUtils.copyUriToInternalStorage(
                            context, uri, "profile_posts",
                        )
                        onPhotoChange(persistentPath ?: uri.toString())
                        imageError = null
                    }
                    ModerationTriState.EXPLICIT -> {
                        imageError = verdict.reason.ifBlank {
                            "This image can't be used because it violates our content policies."
                        }
                    }
                    ModerationTriState.QUESTIONABLE -> {
                        val persistentPath = ImageUtils.copyUriToInternalStorage(
                            context, uri, "profile_posts",
                        )
                        val userId = AuthRepository.instance.currentUserId
                        val userName = AppRepository.instance.profile.value.name
                        val avatar = AppRepository.instance.profile.value.avatarEmoji
                        ImageReviewRepository.instance.submitReview(
                            userId = userId ?: "unknown",
                            userName = userName,
                            userAvatar = avatar,
                            imageUri = persistentPath ?: uri.toString(),
                            type = "profile_post",
                            reason = verdict.reason,
                        )
                        onPhotoChange(persistentPath ?: uri.toString())
                        imageError = null
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        BackButton(onBack)
        Spacer(Modifier.height(10.dp))

        // Optional photo attach
        Text(
            "Photo (optional)",
            style = MaterialTheme.typography.labelLarge,
            color = DarkTextHigh,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        if (photoUri != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1A1812))
                    .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
            ) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Attached photo",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit,
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2A2820))
                        .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape)
                        .clickable { onPhotoChange(null) },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Remove photo",
                        tint = DarkTextHigh,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2A2820))
                        .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .clickable { galleryLauncher.launch("image/*") }
                        .padding(vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        Icons.Filled.PhotoLibrary,
                        contentDescription = "Choose from gallery",
                        tint = Citrine,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Gallery",
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2A2820))
                        .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .clickable { showSavedImagePicker = true }
                        .padding(vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        Icons.Filled.Download,
                        contentDescription = "Choose from saved images",
                        tint = Citrine,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Saved Images",
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        if (imageModerating) {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = Citrine,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Scanning image…",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                )
            }
        }
        if (imageError != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                imageError ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFE2574C),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(14.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { onTitleChange(ProfanityFilter.filter(it)) },
            modifier = Modifier.fillMaxWidth().noAutoFocus(),
            label = { Text("Title *", color = DarkTextMid) },
            placeholder = { Text("Give your post a title…", color = DarkTextMid) },
            singleLine = true,
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
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = tagline,
            onValueChange = { onTaglineChange(ProfanityFilter.filter(it)) },
            modifier = Modifier.fillMaxWidth().noAutoFocus(),
            label = { Text("Tagline (optional)", color = DarkTextMid) },
            placeholder = { Text("A short subtitle…", color = DarkTextMid) },
            singleLine = true,
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
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = caption,
            onValueChange = { onCaptionChange(ProfanityFilter.filter(it)) },
            modifier = Modifier.fillMaxWidth().noAutoFocus(),
            label = { Text("Description *", color = DarkTextMid) },
            placeholder = { Text("Write your post…", color = DarkTextMid) },
            minLines = 3,
            maxLines = 8,
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
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = locationText,
            onValueChange = { onLocationChange(ProfanityFilter.filter(it)) },
            modifier = Modifier.fillMaxWidth().noAutoFocus(),
            label = { Text("Location (optional)", color = DarkTextMid) },
            placeholder = { Text("Where was this? e.g. Mount Ida, AR", color = DarkTextMid) },
            singleLine = true,
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
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SculptedButton(
                text = "Back",
                onClick = onBack,
                modifier = Modifier.weight(1f),
                accent = Citrine,
                containerColor = Color(0xFF3A3830),
                textColor = Color.White,
                shape = RoundedCornerShape(12.dp),
            )
            SculptedButton(
                text = "Post",
                onClick = onPost,
                modifier = Modifier.weight(1f),
                enabled = title.isNotBlank() && caption.isNotBlank() && !imageModerating,
                accent = Citrine,
                containerColor = Citrine,
                textColor = Ink,
                shape = RoundedCornerShape(12.dp),
            )
        }
    }

    if (showSavedImagePicker) {
        SavedImagesPickerDialog(
            onDismiss = { showSavedImagePicker = false },
            onImageSelected = { image ->
                showSavedImagePicker = false
                scope.launch {
                    imageModerating = true
                    imageError = null
                    val path = processSavedImage(context, image, "profile_posts", "profile_post")
                    imageModerating = false
                    if (path != null) {
                        onPhotoChange(path)
                        imageError = null
                    }
                }
            },
        )
    }
}

private data class ShareableItem(
    val id: String,
    val sourceType: String,
    val sourceRefId: String?,
    val title: String,
    val subtitle: String,
    val imageUri: String?,
    val locationText: String,
    val icon: ImageVector,
    val accent: Color,
)

@Composable
private fun rememberShareableItemsByCategory(): Map<String, List<ShareableItem>> {
    val repo = AppRepository.instance
    val collection by repo.collection.collectAsStateWithLifecycle()
    val wishlist by repo.wishlist.collectAsStateWithLifecycle()
    val captures by repo.captures.collectAsStateWithLifecycle()
    val favorites by repo.favoriteSpots.collectAsStateWithLifecycle()
    val trips by repo.trips.collectAsStateWithLifecycle()
    val journalEntries by repo.journalEntries.collectAsStateWithLifecycle()
    val tradeListings by repo.tradeListings.collectAsStateWithLifecycle()

    return remember(
        collection, wishlist, captures, favorites, trips, journalEntries, tradeListings,
    ) {
        val map = mutableMapOf<String, MutableList<ShareableItem>>()
        sourceCategories.forEach { map[it.sourceType] = mutableListOf() }

        collection.forEach { entry ->
            val specimen = SeedData.specimenById(entry.specimenId)
            if (specimen != null) {
                map.getOrPut("collection") { mutableListOf() }.add(
                    ShareableItem(
                        id = "col-${entry.specimenId}",
                        sourceType = "collection",
                        sourceRefId = entry.specimenId,
                        title = specimen.name,
                        subtitle = "From my collection",
                        imageUri = SpecimenImages.urls[entry.specimenId]?.firstOrNull(),
                        locationText = entry.foundAt,
                        icon = Icons.Filled.Inventory2,
                        accent = Aqua,
                    )
                )
            }
        }
        wishlist.forEach { id ->
            val specimen = SeedData.specimenById(id)
            if (specimen != null) {
                map.getOrPut("wishlist") { mutableListOf() }.add(
                    ShareableItem(
                        id = "wish-$id",
                        sourceType = "wishlist",
                        sourceRefId = id,
                        title = specimen.name,
                        subtitle = "On my wishlist",
                        imageUri = SpecimenImages.urls[id]?.firstOrNull(),
                        locationText = "",
                        icon = Icons.Filled.PlaylistAdd,
                        accent = Color(0xFF9B7BD8),
                    )
                )
            }
        }
        captures.forEach { capture ->
            map.getOrPut("capture") { mutableListOf() }.add(
                ShareableItem(
                    id = "cap-${capture.id}",
                    sourceType = "capture",
                    sourceRefId = capture.id,
                    title = capture.displayName(SeedData.specimenById(capture.specimenId)?.name ?: "Rock"),
                    subtitle = "Field capture",
                    imageUri = capture.imageUris.firstOrNull(),
                    locationText = capture.customLocation,
                    icon = Icons.Filled.CameraAlt,
                    accent = Color(0xFF5CC98C),
                )
            )
        }
        favorites.forEach { id ->
            val location = SeedData.locationById(id)
            if (location != null) {
                map.getOrPut("spot") { mutableListOf() }.add(
                    ShareableItem(
                        id = "spot-$id",
                        sourceType = "spot",
                        sourceRefId = id,
                        title = location.name,
                        subtitle = "Favorite spot",
                        imageUri = SpecimenImages.urls[id]?.firstOrNull(),
                        locationText = location.region,
                        icon = Icons.Filled.Place,
                        accent = Color(0xFFE2574C),
                    )
                )
            }
        }
        trips.forEach { trip ->
            map.getOrPut("trip") { mutableListOf() }.add(
                ShareableItem(
                    id = "trip-${trip.id}",
                    sourceType = "trip",
                    sourceRefId = trip.id,
                    title = trip.name,
                    subtitle = "Planned trip · ${formatDate(trip.date)}",
                    imageUri = trip.stops.firstOrNull()?.let { stop ->
                        SpecimenImages.urls[stop.locationId]?.firstOrNull()
                    },
                    locationText = trip.stops.joinToString(" → ") { it.locationName },
                    icon = Icons.Filled.Map,
                    accent = Color(0xFFE8A33D),
                )
            )
        }
        journalEntries.forEach { entry ->
            map.getOrPut("journal") { mutableListOf() }.add(
                ShareableItem(
                    id = "journal-${entry.id}",
                    sourceType = "journal",
                    sourceRefId = entry.id,
                    title = "Journal · ${formatDate(entry.date)}",
                    subtitle = entry.location.ifBlank { "Field journal entry" },
                    imageUri = entry.photoUris.firstOrNull(),
                    locationText = entry.location,
                    icon = Icons.Filled.MenuBook,
                    accent = Color(0xFF6FA8C7),
                )
            )
        }
        tradeListings.filter { it.status == "active" }.forEach { listing ->
            map.getOrPut("trade") { mutableListOf() }.add(
                ShareableItem(
                    id = "trade-${listing.id}",
                    sourceType = "trade",
                    sourceRefId = listing.id,
                    title = "${listing.type.label}: ${listing.specimenName}",
                    subtitle = "${listing.listingMode.label} · ${listing.description.take(40)}",
                    imageUri = listing.photoUri,
                    locationText = "",
                    icon = Icons.Filled.SwapHoriz,
                    accent = Color(0xFFE8A33D),
                )
            )
        }
        RocksAreAmazingSpecimens.allAmazing.forEach { specimen ->
            map.getOrPut("rocks_are_amazing") { mutableListOf() }.add(
                ShareableItem(
                    id = "raa-${specimen.id}",
                    sourceType = "rocks_are_amazing",
                    sourceRefId = specimen.id,
                    title = specimen.name,
                    subtitle = "Rocks Are Amazing",
                    imageUri = SpecimenImages.urls[specimen.id]?.firstOrNull(),
                    locationText = "",
                    icon = Icons.Filled.CollectionsBookmark,
                    accent = Color(0xFF44AACC),
                )
            )
        }
        SeedData.allSpecimens.forEach { specimen ->
            map.getOrPut("database") { mutableListOf() }.add(
                ShareableItem(
                    id = "db-${specimen.id}",
                    sourceType = "database",
                    sourceRefId = specimen.id,
                    title = specimen.name,
                    subtitle = specimen.tagline,
                    imageUri = SpecimenImages.urls[specimen.id]?.firstOrNull(),
                    locationText = "",
                    icon = Icons.Filled.Search,
                    accent = Color(0xFF66AADD),
                )
            )
        }

        map.mapValues { (_, list) -> list.sortedBy { it.title } }
    }
}

@Composable
private fun ShareableItemRow(
    item: ShareableItem,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = RoundedCornerShape(14.dp), accent = item.accent, shadowElevation = 4.dp, onClick = onClick)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF2A2820))
            .glowingBorder(2.dp, item.accent.copy(alpha = 0.40f), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(item.accent.copy(alpha = 0.18f))
                .glowingBorder(2.dp, item.accent.copy(alpha = 0.50f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(item.icon, contentDescription = null, tint = item.accent, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.title,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextMid,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            Icons.Filled.Add,
            contentDescription = "Select",
            tint = item.accent,
            modifier = Modifier.size(24.dp),
        )
    }
}

/**
 * Full-screen archived posts viewer. Shows all archived posts sorted newest-first.
 * Same PostCard component and friend/non-friend privilege rules as the active feed.
 * Close button in the top-left corner.
 */
@Composable
fun ArchivedPostsPopup(
    posts: List<PostRepository.PostRow>,
    isMe: Boolean,
    isFriend: Boolean,
    postLikes: Map<String, List<PostRepository.LikeRow>>,
    postComments: Map<String, List<PostRepository.CommentRow>>,
    commentLikes: Map<String, List<PostRepository.CommentLikeRow>>,
    likedPostIds: Set<String>,
    likedCommentIds: Set<String>,
    myUserId: String?,
    commentBodies: Map<String, String>,
    replyBodies: Map<String, String>,
    replyingTo: String?,
    commentImageUris: Map<String, String?> = emptyMap(),
    replyImageUris: Map<String, String?> = emptyMap(),
    commentImageErrors: Map<String, String?> = emptyMap(),
    replyImageErrors: Map<String, String?> = emptyMap(),
    onLike: (postId: String) -> Unit,
    onCommentChange: (postId: String, body: String) -> Unit,
    onComment: (postId: String) -> Unit,
    onReplyStart: (commentId: String) -> Unit,
    onReplyBodyChange: (body: String) -> Unit,
    onReplySubmit: () -> Unit,
    onCommentLike: (commentId: String) -> Unit,
    onCommentImagePicked: ((postId: String, uri: String?) -> Unit)? = null,
    onReplyImagePicked: ((uri: String?) -> Unit)? = null,
    onCommentImageRemove: ((postId: String) -> Unit)? = null,
    onReplyImageRemove: (() -> Unit)? = null,
    onRestore: ((postId: String) -> Unit)? = null,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF16140F)),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar with close button + title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SculptedIconButton(
                        icon = Icons.Filled.Close,
                        contentDescription = "Close",
                        onClick = onDismiss,
                        modifier = Modifier.size(40.dp),
                        accent = Citrine,
                        iconTint = Color.White,
                        backgroundColor = Color(0xFF2A2820),
                        size = 40.dp,
                        shadowElevation = 3.dp,
                    )
                    Text(
                        text = "Archived Posts (${posts.size})",
                        style = MaterialTheme.typography.titleLarge,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                }
                // Archived posts list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    items(posts, key = { it.id }) { post ->
                        PostCard(
                            post = post,
                            isFriend = isFriend,
                            isMe = isMe,
                            isLiked = likedPostIds.contains(post.id),
                            likeCount = postLikes[post.id]?.size ?: 0,
                            comments = postComments[post.id] ?: emptyList(),
                            commentLikes = commentLikes,
                            likedCommentIds = likedCommentIds,
                            myUserId = myUserId,
                            commentBody = commentBodies[post.id] ?: "",
                            replyingToCommentId = replyingTo,
                            replyBody = if (replyingTo != null) replyBodies[replyingTo] ?: "" else "",
                            commentImageUri = commentImageUris[post.id],
                            replyImageUri = replyingTo?.let { replyImageUris[it] },
                            commentImageModerating = commentImageUris[post.id] == "__loading__",
                            replyImageModerating = replyingTo != null && replyImageUris[replyingTo] == "__loading__",
                            commentImageError = commentImageErrors[post.id],
                            replyImageError = replyingTo?.let { replyImageErrors[it] },
                            onLike = { onLike(post.id) },
                            onCommentChange = { body -> onCommentChange(post.id, body) },
                            onComment = { onComment(post.id) },
                            onReplyStart = onReplyStart,
                            onReplyBodyChange = onReplyBodyChange,
                            onReplySubmit = onReplySubmit,
                            onCommentLike = onCommentLike,
                            onCommentImagePicked = { uri -> onCommentImagePicked?.invoke(post.id, uri) },
                            onReplyImagePicked = onReplyImagePicked,
                            onCommentImageRemove = { onCommentImageRemove?.invoke(post.id) },
                            onReplyImageRemove = onReplyImageRemove,
                            onRestore = onRestore?.let { cb -> { cb(post.id) } },
                        )
                    }
                }
            }
        }
    }
}

private fun formatDate(epochMillis: Long): String {
    return runCatching {
        DateTimeFormatter.ofPattern("MMM d, yyyy")
            .format(Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate())
    }.getOrDefault("")
}

/**
 * Returns a click handler that navigates to the source card for a profile post.
 *
 * - Specimen-based sources (collection, wishlist, database, rocks_are_amazing)
 *   navigate to the specimen detail screen.
 * - Spot posts navigate to the location detail screen.
 * - Trip posts navigate to the Trip Planner.
 * - Journal posts navigate to the Field Journal.
 * - Trade posts navigate to the Trade Board.
 * - Capture posts navigate to Captures.
 *
 * Journal and trip posts are only tappable by the post owner or friends;
 * the PostCard already hides the image for non-friends, so this handler
 * is only invoked when appropriate.
 */
fun postImageNavigation(
    post: PostRepository.PostRow,
    navController: androidx.navigation.NavController,
): (() -> Unit)? {
    val refId = post.source_ref_id ?: return null
    val route = when (post.source_type) {
        "collection", "wishlist", "database", "rocks_are_amazing" ->
            com.rork.rockscout.ui.navigation.Routes.specimen(refId)
        "spot" ->
            com.rork.rockscout.ui.navigation.Routes.location(refId)
        "trip" ->
            com.rork.rockscout.ui.navigation.Routes.TRIP_PLANNER
        "journal" ->
            com.rork.rockscout.ui.navigation.Routes.FIELD_JOURNAL
        "trade" ->
            com.rork.rockscout.ui.navigation.Routes.TRADE_BOARD
        "capture" ->
            com.rork.rockscout.ui.navigation.Routes.CAPTURES
        else -> return null
    }
    return { navController.navigate(route) }
}


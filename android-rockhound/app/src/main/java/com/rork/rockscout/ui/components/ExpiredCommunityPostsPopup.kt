package com.rork.rockscout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rork.rockscout.data.CommunityRepository
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid

/**
 * Full-screen popup showing the current user's expired community posts.
 * Matches the ArchivedPostsPopup pattern. Shows expired posts newest-first
 * with a restore button on each.
 *
 * @param onRestore Called with the post ID when the restore button is tapped.
 *   If null, no restore button is shown.
 */
@Composable
fun ExpiredCommunityPostsPopup(
    posts: List<CommunityRepository.PostRow>,
    isMe: Boolean,
    postLikes: Map<String, List<CommunityRepository.LikeRow>>,
    postComments: Map<String, List<CommunityRepository.CommentRow>>,
    commentLikes: Map<String, List<CommunityRepository.CommentLikeRow>>,
    likedPostIds: Set<String>,
    likedCommentIds: Set<String>,
    myUserId: String?,
    authors: Map<String, CommunityRepository.AuthorInfo>,
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
    onDelete: ((postId: String) -> Unit)? = null,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF16140F)),
        ) {
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
            if (posts.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        "No expired community posts yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkTextMid,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    items(posts, key = { it.id }) { post ->
                        val author = authors[post.user_id]
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
                            hoursUntilExpiry = 0,
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
                            onCommentImagePicked = onCommentImagePicked?.let { cb -> { uri -> cb(post.id, uri) } },
                            onReplyImagePicked = onReplyImagePicked,
                            onCommentImageRemove = onCommentImageRemove?.let { cb -> { cb(post.id) } },
                            onReplyImageRemove = onReplyImageRemove,
                            onRepost = null,
                            onDelete = onDelete?.let { cb -> { cb(post.id) } },
                            onRestore = onRestore?.let { cb -> { cb(post.id) } },
                        )
                    }
                }
            }
        }
    }
}

package com.rork.rockscout.data

import android.util.Log
import com.rork.rockscout.data.MockDataSeeder.LocalPost
import com.rork.rockscout.data.MockDataSeeder.LocalPostLike
import com.rork.rockscout.data.MockDataSeeder.LocalPostComment
import com.rork.rockscout.data.MockDataSeeder.LocalPostCommentLike
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Profile posts — a 10-post feed per user. Posts can be sourced from field
 * captures, my rocks, wishlist, dig sites, RAA, favorite spots, trips, and
 * journal entries. Any user can "love" a post; only RockScout Friends can
 * comment. Adding an 11th post archives the oldest (kept in storage, visible
 * via the Archived Posts viewer).
 *
 * Fully self-contained: all data stored in [LocalDataStore]. No Supabase.
 */
class PostRepository private constructor() {

    @Serializable
    data class PostRow(
        val id: String,
        val user_id: String,
        val source_type: String,
        val source_ref_id: String? = null,
        val title: String,
        val tagline: String = "",
        val image_uri: String? = null,
        val caption: String = "",
        val location_text: String = "",
        val created_at: String,
    )

    @Serializable
    data class LikeRow(
        val post_id: String,
        val user_id: String,
        val created_at: String,
    )

    @Serializable
    data class CommentRow(
        val id: String,
        val post_id: String,
        val user_id: String,
        val body: String,
        val parent_comment_id: String? = null,
        val created_at: String,
        val image_uri: String? = null,
    )

    @Serializable
    data class CommentLikeRow(
        val comment_id: String,
        val user_id: String,
        val created_at: String,
    )

    private val _myPosts = MutableStateFlow<List<PostRow>>(emptyList())
    val myPosts: StateFlow<List<PostRow>> = _myPosts.asStateFlow()

    private val _viewedPosts = MutableStateFlow<List<PostRow>>(emptyList())
    val viewedPosts: StateFlow<List<PostRow>> = _viewedPosts.asStateFlow()

    private val _archivedPosts = MutableStateFlow<List<PostRow>>(emptyList())
    val archivedPosts: StateFlow<List<PostRow>> = _archivedPosts.asStateFlow()

    private val _postLikes = MutableStateFlow<Map<String, List<LikeRow>>>(emptyMap())
    val postLikes: StateFlow<Map<String, List<LikeRow>>> = _postLikes.asStateFlow()

    private val _postComments = MutableStateFlow<Map<String, List<CommentRow>>>(emptyMap())
    val postComments: StateFlow<Map<String, List<CommentRow>>> = _postComments.asStateFlow()

    private val _commentLikes = MutableStateFlow<Map<String, List<CommentLikeRow>>>(emptyMap())
    val commentLikes: StateFlow<Map<String, List<CommentLikeRow>>> = _commentLikes.asStateFlow()

    private val _likedPostIds = MutableStateFlow<Set<String>>(emptySet())
    val likedPostIds: StateFlow<Set<String>> = _likedPostIds.asStateFlow()

    private val _likedCommentIds = MutableStateFlow<Set<String>>(emptySet())
    val likedCommentIds: StateFlow<Set<String>> = _likedCommentIds.asStateFlow()

    private fun currentUserId(): String? = AuthRepository.instance.currentUserId

    /** Create a new post. If the user already has 10, the oldest is archived. */
    suspend fun createPost(
        sourceType: String,
        sourceRefId: String?,
        title: String,
        tagline: String,
        imageUri: String?,
        caption: String,
        locationText: String,
    ): Result<Unit> {
        val me = currentUserId() ?: return Result.failure(IllegalStateException("Not signed in"))
        return runCatching {
            val filteredCaption = ProfanityFilter.filter(caption)
            val filteredTitle = ProfanityFilter.filter(title)
            val filteredTagline = ProfanityFilter.filter(tagline)
            val filteredLocation = ProfanityFilter.filter(locationText)
            val now = java.time.OffsetDateTime.now().toString()

            val posts = LocalDataStore.getTable<LocalPost>(LocalDataStore.KEY_POSTS).toMutableList()
            val myPosts = posts.filter { it.user_id == me }.sortedBy { it.created_at }

            val newPost = LocalPost(
                id = "post-" + UUID.randomUUID(),
                user_id = me,
                source_type = sourceType,
                source_ref_id = sourceRefId,
                title = filteredTitle,
                tagline = filteredTagline,
                image_uri = imageUri,
                caption = filteredCaption,
                location_text = filteredLocation,
                created_at = now,
            )
            posts.add(newPost)

            // If over 10, archive the oldest instead of deleting it.
            // Archived posts keep their likes/comments and are visible via
            // the Archived Posts viewer on both the owner's profile and other
            // users' profiles (with friend/non-friend privileges enforced).
            if (myPosts.size >= 10) {
                val oldest = myPosts.first()
                val idx = posts.indexOfFirst { it.id == oldest.id }
                if (idx >= 0) {
                    posts[idx] = posts[idx].copy(archived = true)
                }
            }

            LocalDataStore.setTable(LocalDataStore.KEY_POSTS, posts)

            // Notify friends about the new post.
            notifyFriendsOfNewPost(me, filteredTitle)

            loadMyPosts()
            Unit
        }.onFailure { Log.w("PostRepository", "createPost failed", it) }
    }

    /**
     * Restore an archived post back to the active profile feed.
     *
     * If the owner already has 10 active posts, the oldest active post is
     * bumped into the archive first — the same overflow logic used by
     * [createPost] when an 11th post is added.
     */
    suspend fun restorePost(postId: String): Result<Unit> {
        val me = currentUserId() ?: return Result.failure(IllegalStateException("Not signed in"))
        return runCatching {
            val posts = LocalDataStore.getTable<LocalPost>(LocalDataStore.KEY_POSTS).toMutableList()
            val targetIdx = posts.indexOfFirst { it.id == postId && it.archived }
            if (targetIdx < 0) return@runCatching
            val ownerId = posts[targetIdx].user_id

            // If the owner already has 10 active posts, archive the oldest.
            val activeMine = posts
                .filter { it.user_id == ownerId && !it.archived }
                .sortedBy { it.created_at }
            if (activeMine.size >= 10) {
                val oldest = activeMine.first()
                val oldestIdx = posts.indexOfFirst { it.id == oldest.id }
                if (oldestIdx >= 0) {
                    posts[oldestIdx] = posts[oldestIdx].copy(archived = true)
                }
            }

            posts[targetIdx] = posts[targetIdx].copy(archived = false)
            LocalDataStore.setTable(LocalDataStore.KEY_POSTS, posts)

            loadMyPosts()
            loadArchivedPosts(ownerId)
            Unit
        }.onFailure { Log.w("PostRepository", "restorePost failed", it) }
    }

    /** Delete a post by id (removes it entirely, including from archive). */
    suspend fun deletePost(postId: String): Result<Unit> {
        return runCatching {
            val posts = LocalDataStore.getTable<LocalPost>(LocalDataStore.KEY_POSTS)
                .filterNot { it.id == postId }
            LocalDataStore.setTable(LocalDataStore.KEY_POSTS, posts)
            deleteLikesAndCommentsForPost(postId)
            loadMyPosts()
            Unit
        }.onFailure { Log.w("PostRepository", "deletePost failed", it) }
    }

    private fun deleteLikesAndCommentsForPost(postId: String) {
        val likes = LocalDataStore.getTable<LocalPostLike>(LocalDataStore.KEY_POST_LIKES)
            .filterNot { it.post_id == postId }
        LocalDataStore.setTable(LocalDataStore.KEY_POST_LIKES, likes)
        val comments = LocalDataStore.getTable<LocalPostComment>(LocalDataStore.KEY_POST_COMMENTS)
            .filterNot { it.post_id == postId }
        LocalDataStore.setTable(LocalDataStore.KEY_POST_COMMENTS, comments)
    }

    /** Notify all friends that the user posted something new. */
    private suspend fun notifyFriendsOfNewPost(userId: String, title: String) {
        val social = SocialRepository.instance
        social.loadConnections()
        val friendIds = social.connections.value
        for (fid in friendIds) {
            NotificationRepository.instance.createNotification(
                userId = fid,
                type = NotificationRepository.TYPE_NEW_POST,
                actorId = userId,
                refId = null,
                body = "New post: $title",
            )
        }
    }

    /** Load the current user's active (non-archived) posts. */
    suspend fun loadMyPosts() {
        val me = currentUserId() ?: return
        runCatching {
            val posts = LocalDataStore.getTable<LocalPost>(LocalDataStore.KEY_POSTS)
            _myPosts.value = posts
                .filter { it.user_id == me && !it.archived }
                .map { PostRow(it.id, it.user_id, it.source_type, it.source_ref_id, it.title, it.tagline, it.image_uri, it.caption, it.location_text, it.created_at) }
                .sortedByDescending { it.created_at }
            loadLikesAndComments(_myPosts.value)
        }.onFailure { Log.w("PostRepository", "loadMyPosts failed", it) }
    }

    /** Load another user's active (non-archived) posts for viewing their profile. */
    suspend fun loadUserPosts(userId: String) {
        runCatching {
            val posts = LocalDataStore.getTable<LocalPost>(LocalDataStore.KEY_POSTS)
            _viewedPosts.value = posts
                .filter { it.user_id == userId && !it.archived }
                .map { PostRow(it.id, it.user_id, it.source_type, it.source_ref_id, it.title, it.tagline, it.image_uri, it.caption, it.location_text, it.created_at) }
                .sortedByDescending { it.created_at }
            loadLikesAndComments(_viewedPosts.value)
        }.onFailure { Log.w("PostRepository", "loadUserPosts failed", it) }
    }

    /** Load any user's archived posts, sorted newest-first (descending by created_at). */
    suspend fun loadArchivedPosts(userId: String) {
        runCatching {
            val posts = LocalDataStore.getTable<LocalPost>(LocalDataStore.KEY_POSTS)
            _archivedPosts.value = posts
                .filter { it.user_id == userId && it.archived }
                .map { PostRow(it.id, it.user_id, it.source_type, it.source_ref_id, it.title, it.tagline, it.image_uri, it.caption, it.location_text, it.created_at) }
                .sortedByDescending { it.created_at }
            loadLikesAndComments(_archivedPosts.value)
        }.onFailure { Log.w("PostRepository", "loadArchivedPosts failed", it) }
    }

    /** Load likes + comments + comment likes for a set of posts. */
    private suspend fun loadLikesAndComments(posts: List<PostRow>) {
        val me = currentUserId()
        val postIds = posts.map { it.id }
        if (postIds.isEmpty()) return
        runCatching {
            val allLikes = LocalDataStore.getTable<LocalPostLike>(LocalDataStore.KEY_POST_LIKES)
            val likes = allLikes.filter { it.post_id in postIds }
            val likesMap = likes.groupBy { it.post_id }
                .mapValues { (_, rows) ->
                    rows.map { LikeRow(it.post_id, it.user_id, it.created_at) }
                }
            _postLikes.value = likesMap

            val allComments = LocalDataStore.getTable<LocalPostComment>(LocalDataStore.KEY_POST_COMMENTS)
            val comments = allComments.filter { it.post_id in postIds }
            _postComments.value = comments.groupBy { it.post_id }
                .mapValues { (_, rows) ->
                    rows.map { CommentRow(it.id, it.post_id, it.user_id, it.body, it.parent_comment_id, it.created_at, it.image_uri) }
                }

            val commentIds = comments.map { it.id }
            val allCommentLikes = LocalDataStore.getTable<LocalPostCommentLike>(LocalDataStore.KEY_POST_COMMENT_LIKES)
            val commentLikes = allCommentLikes.filter { it.comment_id in commentIds }
            _commentLikes.value = commentLikes.groupBy { it.comment_id }
                .mapValues { (_, rows) -> rows.map { CommentLikeRow(it.comment_id, it.user_id, it.created_at) } }

            if (me != null) {
                _likedPostIds.value = likes.filter { it.user_id == me }.map { it.post_id }.toSet()
                _likedCommentIds.value = commentLikes.filter { it.user_id == me }.map { it.comment_id }.toSet()
            }
        }.onFailure { Log.w("PostRepository", "loadLikesAndComments failed", it) }
    }

    /** Toggle like on a post. */
    suspend fun toggleLike(postId: String): Result<Unit> {
        val me = currentUserId() ?: return Result.failure(IllegalStateException("Not signed in"))
        return runCatching {
            val likes = LocalDataStore.getTable<LocalPostLike>(LocalDataStore.KEY_POST_LIKES).toMutableList()
            val now = java.time.OffsetDateTime.now().toString()
            val existing = likes.indexOfFirst { it.post_id == postId && it.user_id == me }
            if (existing >= 0) {
                likes.removeAt(existing)
                _likedPostIds.value = _likedPostIds.value - postId
            } else {
                likes.add(LocalPostLike(post_id = postId, user_id = me, created_at = now))
                _likedPostIds.value = _likedPostIds.value + postId
                // Notify the post owner (if it's not our own post).
                val posts = LocalDataStore.getTable<LocalPost>(LocalDataStore.KEY_POSTS)
                val post = posts.firstOrNull { it.id == postId }
                if (post != null && post.user_id != me) {
                    val myName = AppRepository.instance.profile.value.name.ifBlank { "A RockScout" }
                    NotificationRepository.instance.createNotification(
                        userId = post.user_id,
                        type = NotificationRepository.TYPE_POST_LIKE,
                        actorId = me,
                        refId = postId,
                        body = "$myName liked your post!",
                        deepLinkTarget = "user_profile/$me",
                    )
                }
            }
            LocalDataStore.setTable(LocalDataStore.KEY_POST_LIKES, likes)
            loadLikesAndComments(_viewedPosts.value.ifEmpty { _myPosts.value })
            Unit
        }.onFailure { Log.w("PostRepository", "toggleLike failed", it) }
    }

    /** Add a comment (or reply) to a post. Optionally includes an image URI. */
    suspend fun addComment(postId: String, body: String, parentCommentId: String? = null, imageUri: String? = null): Result<Unit> {
        val me = currentUserId() ?: return Result.failure(IllegalStateException("Not signed in"))
        return runCatching {
            val filtered = ProfanityFilter.filter(body)
            if (filtered.isBlank() && imageUri == null) return@runCatching
            val comments = LocalDataStore.getTable<LocalPostComment>(LocalDataStore.KEY_POST_COMMENTS).toMutableList()
            comments.add(LocalPostComment(
                id = "cmt-" + UUID.randomUUID(),
                post_id = postId,
                user_id = me,
                body = filtered,
                parent_comment_id = parentCommentId,
                created_at = java.time.OffsetDateTime.now().toString(),
                image_uri = imageUri,
            ))
            LocalDataStore.setTable(LocalDataStore.KEY_POST_COMMENTS, comments)
            // Notify the post owner.
            val posts = LocalDataStore.getTable<LocalPost>(LocalDataStore.KEY_POSTS)
            val post = posts.firstOrNull { it.id == postId }
            if (post != null && post.user_id != me) {
                val myName = AppRepository.instance.profile.value.name.ifBlank { "A RockScout" }
                val notifType = if (parentCommentId != null) {
                    NotificationRepository.TYPE_POST_REPLY
                } else {
                    NotificationRepository.TYPE_POST_COMMENT
                }
                val action = if (parentCommentId != null) "replied to" else "commented on"
                NotificationRepository.instance.createNotification(
                    userId = post.user_id,
                    type = notifType,
                    actorId = me,
                    refId = postId,
                    body = "$myName $action your post!",
                    deepLinkTarget = "user_profile/$me",
                )
            }
            loadLikesAndComments(_viewedPosts.value.ifEmpty { _myPosts.value })
            Unit
        }.onFailure { Log.w("PostRepository", "addComment failed", it) }
    }

    /** Toggle like on a comment. */
    suspend fun toggleCommentLike(commentId: String): Result<Unit> {
        val me = currentUserId() ?: return Result.failure(IllegalStateException("Not signed in"))
        return runCatching {
            val likes = LocalDataStore.getTable<LocalPostCommentLike>(LocalDataStore.KEY_POST_COMMENT_LIKES).toMutableList()
            val now = java.time.OffsetDateTime.now().toString()
            val existing = likes.indexOfFirst { it.comment_id == commentId && it.user_id == me }
            if (existing >= 0) {
                likes.removeAt(existing)
                _likedCommentIds.value = _likedCommentIds.value - commentId
            } else {
                likes.add(LocalPostCommentLike(comment_id = commentId, user_id = me, created_at = now))
                _likedCommentIds.value = _likedCommentIds.value + commentId
            }
            LocalDataStore.setTable(LocalDataStore.KEY_POST_COMMENT_LIKES, likes)
            loadLikesAndComments(_viewedPosts.value.ifEmpty { _myPosts.value })
            Unit
        }.onFailure { Log.w("PostRepository", "toggleCommentLike failed", it) }
    }

    companion object {
        val instance: PostRepository by lazy { PostRepository() }
    }
}

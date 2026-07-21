package com.rork.rockscout.data

import android.util.Log
import com.rork.rockscout.data.MockDataSeeder.LocalCommunityPost
import com.rork.rockscout.data.MockDataSeeder.LocalCommunityPostLike
import com.rork.rockscout.data.MockDataSeeder.LocalCommunityPostComment
import com.rork.rockscout.data.MockDataSeeder.LocalCommunityPostCommentLike
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.UUID

/**
 * Community board — an app-wide feed where any signed-in user can post a
 * question or find (photo + title + description). Any user can love, comment,
 * and reply (open community, NOT friends-only). Posts auto-expire after
 * [EXPIRY_DAYS] days; expired posts move to an author-only archive with a
 * restore option.
 *
 * Fully self-contained: all data stored in [LocalDataStore]. No Supabase.
 */
class CommunityRepository private constructor() {

    @Serializable
    data class PostRow(
        val id: String,
        val user_id: String,
        val title: String,
        val tagline: String = "",
        val image_uri: String? = null,
        val description: String = "",
        val location_text: String = "",
        val created_at: String,
        val expires_at: String,
        val expired: Boolean = false,
        val category: String = Category.GENERAL.id,
    )

    /** Community post categories for the Q&A board filter. */
    enum class Category(val id: String, val label: String) {
        IDENTIFICATION("identification", "Identification"),
        LOCATION_TIPS("location_tips", "Location Tips"),
        GENERAL("general", "General Discussion"),
    }

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

    /** Compact author info resolved from the local users table. */
    data class AuthorInfo(
        val userId: String,
        val displayName: String,
        val avatarEmoji: String,
    )

    private val _feedPosts = MutableStateFlow<List<PostRow>>(emptyList())
    val feedPosts: StateFlow<List<PostRow>> = _feedPosts.asStateFlow()

    private val _expiredPosts = MutableStateFlow<List<PostRow>>(emptyList())
    val expiredPosts: StateFlow<List<PostRow>> = _expiredPosts.asStateFlow()

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

    private val _authors = MutableStateFlow<Map<String, AuthorInfo>>(emptyMap())
    val authors: StateFlow<Map<String, AuthorInfo>> = _authors.asStateFlow()

    private fun currentUserId(): String? = AuthRepository.instance.currentUserId

    /** Create a new community post. Expiry is set to [EXPIRY_DAYS] from now. */
    suspend fun createPost(
        title: String,
        tagline: String,
        imageUri: String?,
        description: String,
        locationText: String,
        category: Category = Category.GENERAL,
    ): Result<Unit> {
        val me = currentUserId() ?: return Result.failure(IllegalStateException("Not signed in"))
        return runCatching {
            val filteredTitle = ProfanityFilter.filter(title)
            val filteredTagline = ProfanityFilter.filter(tagline)
            val filteredDescription = ProfanityFilter.filter(description)
            val filteredLocation = ProfanityFilter.filter(locationText)
            val now = OffsetDateTime.now()
            val expires = now.plusDays(EXPIRY_DAYS.toLong())

            val posts = LocalDataStore.getTable<LocalCommunityPost>(LocalDataStore.KEY_COMMUNITY_POSTS).toMutableList()
            val newPost = LocalCommunityPost(
                id = "cpost-" + UUID.randomUUID(),
                user_id = me,
                title = filteredTitle,
                tagline = filteredTagline,
                image_uri = imageUri,
                description = filteredDescription,
                location_text = filteredLocation,
                created_at = now.toString(),
                expires_at = expires.toString(),
                expired = false,
                category = category.id,
            )
            posts.add(newPost)
            LocalDataStore.setTable(LocalDataStore.KEY_COMMUNITY_POSTS, posts)

            loadFeed()
            Unit
        }.onFailure { Log.w("CommunityRepository", "createPost failed", it) }
    }

    /** Restore an expired post back to the community feed (resets the expiry clock). */
    suspend fun restorePost(postId: String): Result<Unit> {
        val me = currentUserId() ?: return Result.failure(IllegalStateException("Not signed in"))
        return runCatching {
            val posts = LocalDataStore.getTable<LocalCommunityPost>(LocalDataStore.KEY_COMMUNITY_POSTS).toMutableList()
            val idx = posts.indexOfFirst { it.id == postId && it.user_id == me && it.expired }
            if (idx < 0) return@runCatching
            val now = OffsetDateTime.now()
            val newExpiry = now.plusDays(EXPIRY_DAYS.toLong())
            posts[idx] = posts[idx].copy(
                expired = false,
                expires_at = newExpiry.toString(),
                created_at = now.toString(),
            )
            LocalDataStore.setTable(LocalDataStore.KEY_COMMUNITY_POSTS, posts)

            loadFeed()
            loadExpiredPosts()
            Unit
        }.onFailure { Log.w("CommunityRepository", "restorePost failed", it) }
    }

    /** Delete a community post by id (removes it entirely). */
    suspend fun deletePost(postId: String): Result<Unit> {
        return runCatching {
            val posts = LocalDataStore.getTable<LocalCommunityPost>(LocalDataStore.KEY_COMMUNITY_POSTS)
                .filterNot { it.id == postId }
            LocalDataStore.setTable(LocalDataStore.KEY_COMMUNITY_POSTS, posts)
            deleteLikesAndCommentsForPost(postId)
            loadFeed()
            loadExpiredPosts()
            Unit
        }.onFailure { Log.w("CommunityRepository", "deletePost failed", it) }
    }

    private fun deleteLikesAndCommentsForPost(postId: String) {
        val likes = LocalDataStore.getTable<LocalCommunityPostLike>(LocalDataStore.KEY_COMMUNITY_POST_LIKES)
            .filterNot { it.post_id == postId }
        LocalDataStore.setTable(LocalDataStore.KEY_COMMUNITY_POST_LIKES, likes)
        val comments = LocalDataStore.getTable<LocalCommunityPostComment>(LocalDataStore.KEY_COMMUNITY_POST_COMMENTS)
            .filterNot { it.post_id == postId }
        LocalDataStore.setTable(LocalDataStore.KEY_COMMUNITY_POST_COMMENTS, comments)
    }

    /**
     * Sweep expired posts: any non-expired post whose `expires_at` has passed
     * is marked as expired. Called on every feed load.
     */
    private suspend fun sweepExpired() {
        val now = OffsetDateTime.now()
        val posts = LocalDataStore.getTable<LocalCommunityPost>(LocalDataStore.KEY_COMMUNITY_POSTS).toMutableList()
        var changed = false
        for (i in posts.indices) {
            if (!posts[i].expired) {
                val expiresAt = runCatching { OffsetDateTime.parse(posts[i].expires_at) }.getOrNull()
                if (expiresAt != null && expiresAt.isBefore(now)) {
                    posts[i] = posts[i].copy(expired = true)
                    changed = true
                }
            }
        }
        if (changed) {
            LocalDataStore.setTable(LocalDataStore.KEY_COMMUNITY_POSTS, posts)
        }
    }

    /** Load the active (non-expired) community feed from all users. */
    suspend fun loadFeed() {
        runCatching {
            sweepExpired()
            val posts = LocalDataStore.getTable<LocalCommunityPost>(LocalDataStore.KEY_COMMUNITY_POSTS)
            _feedPosts.value = posts
                .filter { !it.expired }
                .map { it.toPostRow() }
                .sortedByDescending { it.created_at }
            loadLikesAndComments(_feedPosts.value)
            loadAuthors(_feedPosts.value)
        }.onFailure { Log.w("CommunityRepository", "loadFeed failed", it) }
    }

    /** Load the current user's expired community posts (author-only archive). */
    suspend fun loadExpiredPosts() {
        val me = currentUserId() ?: return
        runCatching {
            sweepExpired()
            val posts = LocalDataStore.getTable<LocalCommunityPost>(LocalDataStore.KEY_COMMUNITY_POSTS)
            _expiredPosts.value = posts
                .filter { it.user_id == me && it.expired }
                .map { it.toPostRow() }
                .sortedByDescending { it.created_at }
            loadLikesAndComments(_expiredPosts.value)
        }.onFailure { Log.w("CommunityRepository", "loadExpiredPosts failed", it) }
    }

    /** Resolve author display info for a set of posts from the local users table. */
    private suspend fun loadAuthors(posts: List<PostRow>) {
        runCatching {
            val userIds = posts.map { it.user_id }.distinct()
            if (userIds.isEmpty()) {
                _authors.value = emptyMap()
                return@runCatching
            }
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
            val authorMap = mutableMapOf<String, AuthorInfo>()
            for (uid in userIds) {
                val user = users.firstOrNull { it.id == uid }
                if (user != null) {
                    authorMap[uid] = AuthorInfo(
                        userId = uid,
                        displayName = user.display_name,
                        avatarEmoji = user.avatar_emoji,
                    )
                } else {
                    // Fallback for the current user if not yet in the users table
                    if (uid == AuthRepository.instance.currentUserId) {
                        val profile = AppRepository.instance.profile.value
                        authorMap[uid] = AuthorInfo(
                            userId = uid,
                            displayName = profile.name.ifBlank { "You" },
                            avatarEmoji = profile.avatarEmoji,
                        )
                    }
                }
            }
            _authors.value = authorMap
        }.onFailure { Log.w("CommunityRepository", "loadAuthors failed", it) }
    }

    /** Load likes + comments + comment likes for a set of posts. */
    private suspend fun loadLikesAndComments(posts: List<PostRow>) {
        val me = currentUserId()
        val postIds = posts.map { it.id }
        if (postIds.isEmpty()) return
        runCatching {
            val allLikes = LocalDataStore.getTable<LocalCommunityPostLike>(LocalDataStore.KEY_COMMUNITY_POST_LIKES)
            val likes = allLikes.filter { it.post_id in postIds }
            _postLikes.value = likes.groupBy { it.post_id }
                .mapValues { (_, rows) ->
                    rows.map { LikeRow(it.post_id, it.user_id, it.created_at) }
                }

            val allComments = LocalDataStore.getTable<LocalCommunityPostComment>(LocalDataStore.KEY_COMMUNITY_POST_COMMENTS)
            val comments = allComments.filter { it.post_id in postIds }
            _postComments.value = comments.groupBy { it.post_id }
                .mapValues { (_, rows) ->
                    rows.map { CommentRow(it.id, it.post_id, it.user_id, it.body, it.parent_comment_id, it.created_at, it.image_uri) }
                }

            val commentIds = comments.map { it.id }
            val allCommentLikes = LocalDataStore.getTable<LocalCommunityPostCommentLike>(LocalDataStore.KEY_COMMUNITY_POST_COMMENT_LIKES)
            val commentLikes = allCommentLikes.filter { it.comment_id in commentIds }
            _commentLikes.value = commentLikes.groupBy { it.comment_id }
                .mapValues { (_, rows) -> rows.map { CommentLikeRow(it.comment_id, it.user_id, it.created_at) } }

            if (me != null) {
                _likedPostIds.value = likes.filter { it.user_id == me }.map { it.post_id }.toSet()
                _likedCommentIds.value = commentLikes.filter { it.user_id == me }.map { it.comment_id }.toSet()
            }
        }.onFailure { Log.w("CommunityRepository", "loadLikesAndComments failed", it) }
    }

    /** Toggle like on a community post. */
    suspend fun toggleLike(postId: String): Result<Unit> {
        val me = currentUserId() ?: return Result.failure(IllegalStateException("Not signed in"))
        return runCatching {
            val likes = LocalDataStore.getTable<LocalCommunityPostLike>(LocalDataStore.KEY_COMMUNITY_POST_LIKES).toMutableList()
            val now = OffsetDateTime.now().toString()
            val existing = likes.indexOfFirst { it.post_id == postId && it.user_id == me }
            if (existing >= 0) {
                likes.removeAt(existing)
                _likedPostIds.value = _likedPostIds.value - postId
            } else {
                likes.add(LocalCommunityPostLike(post_id = postId, user_id = me, created_at = now))
                _likedPostIds.value = _likedPostIds.value + postId
                // Notify the post owner (if it's not our own post).
                val posts = LocalDataStore.getTable<LocalCommunityPost>(LocalDataStore.KEY_COMMUNITY_POSTS)
                val post = posts.firstOrNull { it.id == postId }
                if (post != null && post.user_id != me) {
                    val myName = AppRepository.instance.profile.value.name.ifBlank { "A RockScout" }
                    NotificationRepository.instance.createNotification(
                        userId = post.user_id,
                        type = NotificationRepository.TYPE_POST_LIKE,
                        actorId = me,
                        refId = postId,
                        body = "$myName loved your community post!",
                        deepLinkTarget = "user_profile/$me",
                    )
                }
            }
            LocalDataStore.setTable(LocalDataStore.KEY_COMMUNITY_POST_LIKES, likes)
            loadLikesAndComments(_feedPosts.value.ifEmpty { _expiredPosts.value })
            Unit
        }.onFailure { Log.w("CommunityRepository", "toggleLike failed", it) }
    }

    /** Add a comment (or reply) to a community post. Optionally includes an image URI. */
    suspend fun addComment(postId: String, body: String, parentCommentId: String? = null, imageUri: String? = null): Result<Unit> {
        val me = currentUserId() ?: return Result.failure(IllegalStateException("Not signed in"))
        return runCatching {
            val filtered = ProfanityFilter.filter(body)
            if (filtered.isBlank() && imageUri == null) return@runCatching
            val comments = LocalDataStore.getTable<LocalCommunityPostComment>(LocalDataStore.KEY_COMMUNITY_POST_COMMENTS).toMutableList()
            comments.add(LocalCommunityPostComment(
                id = "ccmt-" + UUID.randomUUID(),
                post_id = postId,
                user_id = me,
                body = filtered,
                parent_comment_id = parentCommentId,
                created_at = OffsetDateTime.now().toString(),
                image_uri = imageUri,
            ))
            LocalDataStore.setTable(LocalDataStore.KEY_COMMUNITY_POST_COMMENTS, comments)
            // Notify the post owner.
            val posts = LocalDataStore.getTable<LocalCommunityPost>(LocalDataStore.KEY_COMMUNITY_POSTS)
            val post = posts.firstOrNull { it.id == postId }
            if (post != null && post.user_id != me) {
                val myName = AppRepository.instance.profile.value.name.ifBlank { "A RockScout" }
                val action = if (parentCommentId != null) "replied to" else "commented on"
                NotificationRepository.instance.createNotification(
                    userId = post.user_id,
                    type = NotificationRepository.TYPE_POST_COMMENT,
                    actorId = me,
                    refId = postId,
                    body = "$myName $action your community post!",
                    deepLinkTarget = "user_profile/$me",
                )
            }
            loadLikesAndComments(_feedPosts.value.ifEmpty { _expiredPosts.value })
            Unit
        }.onFailure { Log.w("CommunityRepository", "addComment failed", it) }
    }

    /** Toggle like on a comment. */
    suspend fun toggleCommentLike(commentId: String): Result<Unit> {
        val me = currentUserId() ?: return Result.failure(IllegalStateException("Not signed in"))
        return runCatching {
            val likes = LocalDataStore.getTable<LocalCommunityPostCommentLike>(LocalDataStore.KEY_COMMUNITY_POST_COMMENT_LIKES).toMutableList()
            val now = OffsetDateTime.now().toString()
            val existing = likes.indexOfFirst { it.comment_id == commentId && it.user_id == me }
            if (existing >= 0) {
                likes.removeAt(existing)
                _likedCommentIds.value = _likedCommentIds.value - commentId
            } else {
                likes.add(LocalCommunityPostCommentLike(comment_id = commentId, user_id = me, created_at = now))
                _likedCommentIds.value = _likedCommentIds.value + commentId
            }
            LocalDataStore.setTable(LocalDataStore.KEY_COMMUNITY_POST_COMMENT_LIKES, likes)
            loadLikesAndComments(_feedPosts.value.ifEmpty { _expiredPosts.value })
            Unit
        }.onFailure { Log.w("CommunityRepository", "toggleCommentLike failed", it) }
    }

    /** Sort the feed by the given mode, optionally filtered to a [category]. */
    fun sortedFeed(mode: SortMode, category: Category? = null): List<PostRow> {
        val posts = if (category == null) _feedPosts.value else _feedPosts.value.filter { it.category == category.id }
        val likes = _postLikes.value
        val comments = _postComments.value
        return when (mode) {
            SortMode.Newest -> posts.sortedByDescending { it.created_at }
            SortMode.MostLoved -> posts.sortedByDescending { likes[it.id]?.size ?: 0 }
            SortMode.MostCommented -> posts.sortedByDescending { comments[it.id]?.size ?: 0 }
            SortMode.ExpiringSoon -> {
                val now = OffsetDateTime.now()
                posts.mapNotNull { p ->
                    val expiresAt = runCatching { OffsetDateTime.parse(p.expires_at) }.getOrNull()
                        ?: return@mapNotNull null
                    val hoursLeft = Duration.between(now, expiresAt).toHours()
                    if (hoursLeft in 0..48) p to hoursLeft else null
                }.sortedBy { it.second }.map { it.first }
            }
        }
    }

    /** Get a post by ID (from the current in-memory feed or expired list). */
    fun getPostById(postId: String): PostRow? {
        return _feedPosts.value.firstOrNull { it.id == postId }
            ?: _expiredPosts.value.firstOrNull { it.id == postId }
    }

    private fun LocalCommunityPost.toPostRow(): PostRow = PostRow(
        id = id,
        user_id = user_id,
        title = title,
        tagline = tagline,
        image_uri = image_uri,
        description = description,
        location_text = location_text,
        created_at = created_at,
        expires_at = expires_at,
        expired = expired,
        category = category,
    )

    /** Hours remaining until a post expires (0 if already expired). */
    fun hoursUntilExpiry(post: PostRow): Long {
        return runCatching {
            val expiresAt = OffsetDateTime.parse(post.expires_at)
            val now = OffsetDateTime.now()
            Duration.between(now, expiresAt).toHours().coerceAtLeast(0)
        }.getOrDefault(0)
    }

    enum class SortMode(val label: String) {
        Newest("Newest"),
        MostLoved("Most Loved"),
        MostCommented("Most Commented"),
        ExpiringSoon("Expiring Soon"),
    }

    companion object {
        private const val EXPIRY_DAYS = 14
        val instance: CommunityRepository by lazy { CommunityRepository() }

        /** Resolve a raw category string (from storage) into a [Category], defaulting to General. */
        fun resolveCategory(raw: String?): Category =
            Category.entries.firstOrNull { it.id == raw } ?: Category.GENERAL
    }
}

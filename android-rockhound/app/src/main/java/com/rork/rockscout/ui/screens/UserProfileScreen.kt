package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.FriendRepository
import com.rork.rockscout.data.HunterStatus
import com.rork.rockscout.data.NotificationRepository
import com.rork.rockscout.data.PostRepository
import com.rork.rockscout.data.ProfanityFilter
import com.rork.rockscout.data.ReportRepository
import com.rork.rockscout.data.ReportScreenshotHelper
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.EmptyPostBox
import com.rork.rockscout.ui.components.HunterStatusIcon
import com.rork.rockscout.ui.components.PostCard
import com.rork.rockscout.ui.components.ArchivedPostsPopup
import com.rork.rockscout.ui.components.ReportSubmittedDialog
import com.rork.rockscout.ui.components.postImageNavigation
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.StatTile
import com.rork.rockscout.ui.components.YooperliteHeart
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.components.profileBorderColor
import com.rork.rockscout.ui.components.statusAccent
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.launch

/**
 * User profile view — shows another RockScout's profile card, their 5-post
 * feed, and action buttons (Send message, RockScout request, Report).
 *
 * Non-friends see posts but locations are redacted ("Shared with friends only").
 * Friends see the unredacted profile and can comment on posts.
 * Any user (friend or not) can "love" a post with the Yooperlite heart icon.
 */
@Composable
fun UserProfileScreen(
    navController: NavController,
    userId: String,
    isAdminView: Boolean = false,
) {
    val auth = AuthRepository.instance
    val social = SocialRepository.instance
    val friendRepo = FriendRepository.instance
    val postRepo = PostRepository.instance
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val rootView = androidx.compose.ui.platform.LocalView.current
    val myProfile by com.rork.rockscout.data.AppRepository.instance.profile.collectAsStateWithLifecycle()

    // ─── Premium gating: User Profile is a social feature ───
    val accessManager = com.rork.rockscout.data.IdentifyAccessManager.instance
    val purchaseManager = com.rork.rockscout.data.PurchaseManager.instance
    val isPremium by purchaseManager.isPremium.collectAsStateWithLifecycle()
    val clubLocked = remember(isPremium) { accessManager.isSocialLocked(isPremium) }
    if (clubLocked) {
        com.rork.rockscout.ui.components.ClubLockedState(
            emoji = "\uD83D\uDD12",
            title = "Unlock Hunter Profiles",
            message = "Your 1-week free trial has ended. Subscribe or donate to view and connect with other hunters.",
            buttonLabel = "Subscribe or donate",
            onButton = { navController.navigate(Routes.PAYWALL) },
        )
        return
    }

    val connections by social.connections.collectAsStateWithLifecycle()
    val viewedPosts by postRepo.viewedPosts.collectAsStateWithLifecycle()
    val postLikes by postRepo.postLikes.collectAsStateWithLifecycle()
    val postComments by postRepo.postComments.collectAsStateWithLifecycle()
    val commentLikes by postRepo.commentLikes.collectAsStateWithLifecycle()
    val likedPostIds by postRepo.likedPostIds.collectAsStateWithLifecycle()
    val likedCommentIds by postRepo.likedCommentIds.collectAsStateWithLifecycle()

    var hunter by remember { mutableStateOf<SocialRepository.HunterProfile?>(null) }
    var fullUser by remember { mutableStateOf<com.rork.rockscout.data.LocalUser?>(null) }
    var isFriend by remember { mutableStateOf(false) }
    var friendRequestSent by remember { mutableStateOf(false) }
    var showReportConfirm by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var reportSubmitted by remember { mutableStateOf(false) }
    var showBlockConfirm by remember { mutableStateOf(false) }
    var showRemoveConfirm by remember { mutableStateOf(false) }
    var isBlocked by remember { mutableStateOf(false) }
    var commentBodies by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var replyBodies by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var replyingTo by remember { mutableStateOf<String?>(null) }
    var commentImageUris by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }
    var replyImageUris by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }
    var commentImageErrors by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }
    var replyImageErrors by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }
    var showArchivedPosts by remember { mutableStateOf(false) }
    val archivedPosts by postRepo.archivedPosts.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var deleteReason by remember { mutableStateOf("") }
    var adminActionMessage by remember { mutableStateOf<String?>(null) }
    var viewedUserDeleted by remember { mutableStateOf(false) }

    // Load the user's profile + posts + friendship status.
    LaunchedEffect(userId) {
        runCatching {
            val me = auth.currentUserId
            if (me != null) {
                isFriend = connections.contains(userId) || social.isConnected(userId)
                friendRepo.loadBlocks()
            }
            // Fetch the hunter profile.
            val profiles = social.fetchProfiles(listOf(userId))
            hunter = profiles.firstOrNull()
            // Fetch full profile (bio, home_region, level, xp) for the detailed view.
            fullUser = social.fetchUserProfile(userId)
            viewedUserDeleted = fullUser?.account_deleted == true
            // Load their posts.
            postRepo.loadUserPosts(userId)
            postRepo.loadArchivedPosts(userId)
            // Check if we already have a pending outgoing friend request.
            friendRepo.loadFriendRequests()
            friendRequestSent = friendRepo.outgoingFriendRequests.value.any {
                it.recipient_id == userId && it.status == "pending"
            }
            // Check if this user is already blocked.
            isBlocked = friendRepo.isBlocked(userId)
            // Mark new-post notifications from this user as read (clears
            // only this friend's badge — not everyone's).
            if (isFriend) {
                NotificationRepository.instance.markReadFromActor(userId)
            }
        }
    }

    val isMe = auth.currentUserId == userId

    val h = hunter
    if (h == null) {
        ScreenScaffold(title = "Profile", onBack = { navController.popBackStack() }) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("Loading profile…", style = MaterialTheme.typography.bodyMedium, color = TextMid)
            }
        }
        return
    }

    val status = when (h.status) {
        "on-the-hunt" -> HunterStatus.ON_THE_HUNT
        "wishing" -> HunterStatus.WISHING
        "looking-for-trades" -> HunterStatus.LOOKING_FOR_TRADES
        else -> HunterStatus.OFF_GRID
    }
    val statusLabel = status.label
    val statusAccent = statusAccent(status)

    ScreenScaffold(title = h.display_name, onBack = { navController.popBackStack() }, actions = {
        if (!isMe && !isAdminView) {
            // Block button — next to the report button
            SculptedIconButton(
                icon = Icons.Filled.Block,
                contentDescription = "Block",
                onClick = { showBlockConfirm = true },
                accent = Danger,
                iconTint = if (isBlocked) Danger else DarkTextMid,
                size = 40.dp,
                shadowElevation = 3.dp,
            )
            // Report button
            SculptedIconButton(
                icon = Icons.Filled.Flag,
                contentDescription = "Report",
                onClick = { showReportConfirm = true },
                accent = Danger,
                iconTint = DarkTextMid,
                size = 40.dp,
                shadowElevation = 3.dp,
            )
        }
    }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Profile header card
            item {
                DarkCard(modifier = Modifier.fillMaxWidth(), accent = statusAccent) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(statusAccent.copy(alpha = 0.5f), Aqua.copy(alpha = 0.3f))))
                                .glowingBorder(3.dp, profileBorderColor(status), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) { Text(h.avatar_emoji, style = MaterialTheme.typography.displaySmall) }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                                Text(
                                    h.display_name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f),
                                )
                                if (h.premium_badge) {
                                    Spacer(Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Citrine.copy(alpha = 0.30f))
                                            .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                    ) {
                                        Text("PREMIUM", style = MaterialTheme.typography.labelSmall, color = Citrine, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            // Tappable level pill — opens UserAchievementsScreen
                            val levelTier = com.rork.rockscout.data.LevelTier.forLevel(h.level)
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Citrine.copy(alpha = 0.18f))
                                    .glowingBorder(2.dp, Citrine.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                                    .clickable { navController.navigate(Routes.userAchievements(userId)) }
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Lvl ${h.level}", style = MaterialTheme.typography.labelMedium, color = Citrine, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.width(6.dp))
                                    Text(levelTier.emoji, style = MaterialTheme.typography.labelMedium)
                                    Spacer(Modifier.width(4.dp))
                                    Text(levelTier.displayName, style = MaterialTheme.typography.labelMedium, color = Citrine, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            // Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                HunterStatusIcon(status = status, size = 20.dp)
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    statusLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Aqua,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        // Send message button
                        if (isFriend) {
                            SculptedButton(
                                text = "Message",
                                onClick = { navController.navigate(Routes.messengerThread(userId)) },
                                modifier = Modifier.weight(1f),
                                accent = Aqua,
                                containerColor = Aqua,
                                textColor = Ink,
                                icon = Icons.Filled.Send,
                            )
                        } else {
                            SculptedButton(
                                text = "Send message",
                                onClick = {
                                    navController.navigate(Routes.messengerThread(userId))
                                },
                                modifier = Modifier.weight(1f),
                                accent = Aqua,
                                containerColor = Aqua,
                                textColor = Ink,
                                icon = Icons.Filled.Send,
                            )
                        }
                        // RockScout request button (friend request)
                        if (!isFriend) {
                            if (friendRequestSent) {
                                SculptedOutlinedButton(
                                    text = "Requested",
                                    onClick = {},
                                    enabled = false,
                                    modifier = Modifier.weight(1f),
                                    accent = Success,
                                    textColor = Success,
                                )
                            } else {
                                SculptedButton(
                                    text = "Add Friend",
                                    onClick = {
                                        scope.launch {
                                            runCatching {
                                                friendRepo.sendFriendRequest(userId)
                                                friendRequestSent = true
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    accent = Success,
                                    containerColor = Success,
                                    textColor = Ink,
                                    icon = Icons.Filled.PersonAdd,
                                )
                            }
                        }
                        // Remove friend button (only shown for existing friends)
                        if (isFriend) {
                            SculptedOutlinedButton(
                                text = "Remove",
                                onClick = { showRemoveConfirm = true },
                                modifier = Modifier.weight(1f),
                                accent = Danger,
                                textColor = Danger,
                                icon = Icons.Filled.Close,
                            )
                        }
                    }
                    // Public stats — collection, wishlist, and favorite spots
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatTile(
                            label = "Collected",
                            value = h.collection_count.toString(),
                            icon = Icons.Filled.Inventory2,
                            accent = Aqua,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Routes.userCollection(userId)) },
                        )
                        StatTile(
                            label = "Wishlist",
                            value = h.wishlist_count.toString(),
                            icon = Icons.Filled.PlaylistAdd,
                            accent = Color(0xFF9B7BD8),
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Routes.userWishlist(userId)) },
                        )
                        StatTile(
                            label = "Spots",
                            value = h.favorite_spots_count.toString(),
                            icon = Icons.Filled.Place,
                            accent = Color(0xFFE2574C),
                            modifier = Modifier.weight(1f),
                            onClick = if (isMe) {{ navController.navigate(Routes.FAVORITES) }} else null,
                        )
                    }

                    // Bio + home region section
                    fullUser?.let { fu ->
                        Spacer(Modifier.height(14.dp))
                        HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp)
                        Spacer(Modifier.height(12.dp))
                        // Bio
                        if (!fu.bio.isNullOrBlank()) {
                            Text(
                                fu.bio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextHigh,
                            )
                        } else {
                            Text(
                                "No bio yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextLow,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        // Home region — only for friends (location privacy)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = if (isFriend && !fu.home_region.isNullOrBlank()) Aqua else DarkTextLow,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                if (isFriend && !fu.home_region.isNullOrBlank()) fu.home_region else "Location shared with friends only",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isFriend && !fu.home_region.isNullOrBlank()) Aqua else DarkTextLow,
                                fontStyle = if (isFriend && !fu.home_region.isNullOrBlank()) null else androidx.compose.ui.text.font.FontStyle.Italic,
                            )
                        }

                    }
                }
            }
            // ── Admin delete/restore section (only visible from dev console) ──
            if (isAdminView) {
                item {
                    adminActionMessage?.let { msg ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Success.copy(alpha = 0.12f))
                                .glowingBorder(2.dp, Success.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Text(
                                msg,
                                style = MaterialTheme.typography.bodySmall,
                                color = Success,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    if (viewedUserDeleted) {
                        // Restore button
                        SculptedButton(
                            text = "Restore Account",
                            onClick = { showRestoreDialog = true },
                            accent = Success,
                            containerColor = Success,
                            textColor = Ink,
                            icon = Icons.Filled.Shield,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Danger.copy(alpha = 0.12f))
                                .glowingBorder(2.dp, Danger.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Text(
                                "This account is currently DELETED. Reason: ${fullUser?.deletion_reason ?: "Unknown"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Danger,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    } else {
                        // Delete button
                        SculptedButton(
                            text = "Delete Account",
                            onClick = { showDeleteDialog = true },
                            accent = Danger,
                            containerColor = Danger,
                            textColor = Color.White,
                            icon = Icons.Filled.Block,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            item {
                Text(
                    "POSTS",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                )
            }

            if (viewedPosts.isEmpty()) {
                item {
                    EmptyPostBox(
                        isMe = false,
                        onCreatePost = { },
                    )
                }
            } else {
                items(viewedPosts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        isFriend = isFriend,
                        isMe = isMe,
                        isLiked = likedPostIds.contains(post.id),
                        likeCount = postLikes[post.id]?.size ?: 0,
                        comments = postComments[post.id] ?: emptyList(),
                        commentLikes = commentLikes,
                        likedCommentIds = likedCommentIds,
                        myUserId = auth.currentUserId,
                        commentBody = commentBodies[post.id] ?: "",
                        replyingToCommentId = replyingTo,
                        replyBody = if (replyingTo != null) replyBodies[replyingTo] ?: "" else "",
                        commentImageUri = commentImageUris[post.id],
                        replyImageUri = replyingTo?.let { replyImageUris[it] },
                        commentImageModerating = commentImageUris[post.id] == "__loading__",
                        replyImageModerating = replyingTo != null && replyImageUris[replyingTo] == "__loading__",
                        commentImageError = commentImageErrors[post.id],
                        replyImageError = replyingTo?.let { replyImageErrors[it] },
                        onLike = {
                            scope.launch { runCatching { postRepo.toggleLike(post.id) } }
                        },
                        onCommentChange = { body ->
                            commentBodies = commentBodies + (post.id to body)
                        },
                        onComment = {
                            val body = commentBodies[post.id] ?: ""
                            val imgUri = commentImageUris[post.id]
                            val cleanImg = if (imgUri != null && !imgUri.startsWith("__") && imgUri != "__loading__") imgUri else null
                            if (body.isNotBlank() || cleanImg != null) {
                                scope.launch {
                                    runCatching {
                                        postRepo.addComment(post.id, body, null, cleanImg)
                                        commentBodies = commentBodies + (post.id to "")
                                        commentImageUris = commentImageUris - post.id
                                        commentImageErrors = commentImageErrors - post.id
                                    }
                                }
                            }
                        },
                        onReplyStart = { commentId ->
                            replyingTo = if (replyingTo == commentId) null else commentId
                        },
                        onReplyBodyChange = { body ->
                            replyingTo?.let { replyBodies = replyBodies + (it to body) }
                        },
                        onReplySubmit = {
                            val parentId = replyingTo
                            val body = parentId?.let { replyBodies[it] } ?: ""
                            val imgUri = parentId?.let { replyImageUris[it] }
                            val cleanImg = if (imgUri != null && !imgUri.startsWith("__") && imgUri != "__loading__") imgUri else null
                            if (parentId != null && (body.isNotBlank() || cleanImg != null)) {
                                scope.launch {
                                    runCatching {
                                        postRepo.addComment(post.id, body, parentId, cleanImg)
                                        replyBodies = replyBodies + (parentId to "")
                                        replyImageUris = replyImageUris - parentId
                                        replyImageErrors = replyImageErrors - parentId
                                        replyingTo = null
                                    }
                                }
                            }
                        },
                        onCommentLike = { commentId ->
                            scope.launch { runCatching { postRepo.toggleCommentLike(commentId) } }
                        },
                        onCommentImagePicked = { uri ->
                            if (uri == null) {
                                commentImageUris = commentImageUris - post.id
                                commentImageErrors = commentImageErrors - post.id
                            } else if (uri.startsWith("__error:")) {
                                commentImageErrors = commentImageErrors + (post.id to uri.substring(7))
                                commentImageUris = commentImageUris - post.id
                            } else {
                                commentImageUris = commentImageUris + (post.id to uri)
                                commentImageErrors = commentImageErrors - post.id
                            }
                        },
                        onReplyImagePicked = { uri ->
                            val key = replyingTo ?: return@PostCard
                            if (uri == null) {
                                replyImageUris = replyImageUris - key
                                replyImageErrors = replyImageErrors - key
                            } else if (uri.startsWith("__error:")) {
                                replyImageErrors = replyImageErrors + (key to uri.substring(7))
                                replyImageUris = replyImageUris - key
                            } else {
                                replyImageUris = replyImageUris + (key to uri)
                                replyImageErrors = replyImageErrors - key
                            }
                        },
                        onCommentImageRemove = {
                            commentImageUris = commentImageUris - post.id
                            commentImageErrors = commentImageErrors - post.id
                        },
                        onReplyImageRemove = {
                            replyingTo?.let {
                                replyImageUris = replyImageUris - it
                                replyImageErrors = replyImageErrors - it
                            }
                        },
                        onImageClick = postImageNavigation(post, navController),
                        onDeleteComment = { commentId ->
                            scope.launch { runCatching { postRepo.deleteComment(commentId) } }
                        },
                    )
                }
            }

            // ─── Archived Posts button (only if archived posts exist) ───
            if (archivedPosts.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(16.dp))
                    SculptedOutlinedButton(
                        text = "Archived Posts (${archivedPosts.size})",
                        onClick = { showArchivedPosts = true },
                        modifier = Modifier.fillMaxWidth(),
                        accent = Citrine,
                        icon = Icons.Filled.Archive,
                    )
                }
            }
        }
    }

    // ─── Archived Posts full-screen popup ───
    if (showArchivedPosts) {
        ArchivedPostsPopup(
            posts = archivedPosts,
            isMe = isMe,
            isFriend = isFriend,
            postLikes = postLikes,
            postComments = postComments,
            commentLikes = commentLikes,
            likedPostIds = likedPostIds,
            likedCommentIds = likedCommentIds,
            myUserId = auth.currentUserId,
            commentBodies = commentBodies,
            replyBodies = replyBodies,
            replyingTo = replyingTo,
            commentImageUris = commentImageUris,
            replyImageUris = replyImageUris,
            commentImageErrors = commentImageErrors,
            replyImageErrors = replyImageErrors,
            onLike = { postId -> scope.launch { runCatching { postRepo.toggleLike(postId) } } },
            onCommentChange = { postId, body -> commentBodies = commentBodies + (postId to body) },
            onComment = { postId ->
                val body = commentBodies[postId] ?: ""
                val imgUri = commentImageUris[postId]
                val cleanImg = if (imgUri != null && !imgUri.startsWith("__") && imgUri != "__loading__") imgUri else null
                if (body.isNotBlank() || cleanImg != null) {
                    scope.launch {
                        runCatching {
                            postRepo.addComment(postId, body, null, cleanImg)
                            commentBodies = commentBodies + (postId to "")
                            commentImageUris = commentImageUris - postId
                            commentImageErrors = commentImageErrors - postId
                        }
                    }
                }
            },
            onReplyStart = { commentId -> replyingTo = if (replyingTo == commentId) null else commentId },
            onReplyBodyChange = { body -> replyingTo?.let { replyBodies = replyBodies + (it to body) } },
            onReplySubmit = {
                val parentId = replyingTo
                val body = parentId?.let { replyBodies[it] } ?: ""
                val imgUri = parentId?.let { replyImageUris[it] }
                val cleanImg = if (imgUri != null && !imgUri.startsWith("__") && imgUri != "__loading__") imgUri else null
                if (parentId != null && (body.isNotBlank() || cleanImg != null)) {
                    scope.launch {
                        runCatching {
                            val postId = archivedPosts.firstOrNull { post ->
                                postComments[post.id]?.any { it.id == parentId } == true
                            }?.id
                            if (postId != null) {
                                postRepo.addComment(postId, body, parentId, cleanImg)
                                replyBodies = replyBodies + (parentId to "")
                                replyImageUris = replyImageUris - parentId
                                replyImageErrors = replyImageErrors - parentId
                                replyingTo = null
                            }
                        }
                    }
                }
            },
            onCommentLike = { commentId -> scope.launch { runCatching { postRepo.toggleCommentLike(commentId) } } },
            onCommentImagePicked = { postId, uri ->
                if (uri == null) {
                    commentImageUris = commentImageUris - postId
                    commentImageErrors = commentImageErrors - postId
                } else if (uri.startsWith("__error:")) {
                    commentImageErrors = commentImageErrors + (postId to uri.substring(7))
                    commentImageUris = commentImageUris - postId
                } else {
                    commentImageUris = commentImageUris + (postId to uri)
                    commentImageErrors = commentImageErrors - postId
                }
            },
            onDeleteComment = { commentId ->
                scope.launch { runCatching { postRepo.deleteComment(commentId) } }
            },
            onReplyImagePicked = { uri ->
                val key = replyingTo
                if (key != null) {
                    if (uri == null) {
                        replyImageUris = replyImageUris - key
                        replyImageErrors = replyImageErrors - key
                    } else if (uri.startsWith("__error:")) {
                        replyImageErrors = replyImageErrors + (key to uri.substring(7))
                        replyImageUris = replyImageUris - key
                    } else {
                        replyImageUris = replyImageUris + (key to uri)
                        replyImageErrors = replyImageErrors - key
                    }
                }
            },
            onCommentImageRemove = { postId ->
                commentImageUris = commentImageUris - postId
                commentImageErrors = commentImageErrors - postId
            },
            onReplyImageRemove = {
                replyingTo?.let {
                    replyImageUris = replyImageUris - it
                    replyImageErrors = replyImageErrors - it
                }
            },
            onDismiss = { showArchivedPosts = false },
        )
    }

    // Report confirmation popup — prevents accidental taps from triggering
    // screenshot capture + email composer. Only if user confirms does the
    // full report dialog open.
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

    // Report dialog
    if (showReportDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report ${h.display_name}?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
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
                        scope.launch {
                            runCatching {
                                val reportId = "report-${System.currentTimeMillis()}"
                                val screenshotPath = ReportScreenshotHelper.captureScreenshot(
                                    context, rootView, reportId,
                                )
                                ReportRepository.instance.reportUser(
                                    reportedUserId = userId,
                                    reason = "Inappropriate behavior",
                                    screenshotPath = screenshotPath,
                                    reporterName = myProfile.name,
                                    reportedName = h.display_name,
                                    reportedAvatar = h.avatar_emoji,
                                ).onSuccess {
                                    reportSubmitted = true
                                    val intent = ReportScreenshotHelper.buildEmailIntent(
                                        context = context,
                                        reportedUserName = h.display_name,
                                        reporterUserName = myProfile.name,
                                        reason = "Inappropriate behavior",
                                        timestamp = System.currentTimeMillis(),
                                        screenshotPath = screenshotPath,
                                    )
                                    ReportScreenshotHelper.launchEmailComposer(context, intent)
                                }
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

    // Block confirmation popup — user must confirm before the block takes effect.
    if (showBlockConfirm) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showBlockConfirm = false },
            title = {
                Text(
                    if (isBlocked) "Unblock ${h.display_name}?" else "Block ${h.display_name}?",
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    if (isBlocked)
                        "This will allow ${h.display_name} to send you messages and friend requests again."
                    else
                        "${h.display_name} will no longer be able to send you messages or friend requests, and they will be hidden from your RockScouts Map.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                SculptedButton(
                    text = if (isBlocked) "Confirm Unblock" else "Confirm Block",
                    onClick = {
                        showBlockConfirm = false
                        scope.launch {
                            runCatching {
                                if (isBlocked) {
                                    // Unblock: remove the block row
                                    val me = auth.currentUserId ?: return@runCatching
                                    val blocks = com.rork.rockscout.data.LocalDataStore.getTable<com.rork.rockscout.data.MockDataSeeder.LocalBlock>(com.rork.rockscout.data.LocalDataStore.KEY_BLOCKS)
                                    val filtered = blocks.filterNot { it.blocker_id == me && it.blocked_id == userId }
                                    com.rork.rockscout.data.LocalDataStore.setTable(com.rork.rockscout.data.LocalDataStore.KEY_BLOCKS, filtered)
                                    friendRepo.loadBlocks()
                                    isBlocked = false
                                } else {
                                    friendRepo.blockUser(userId)
                                    friendRepo.loadBlocks()
                                    isBlocked = true
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
                SculptedTextButton(text = "Cancel", onClick = { showBlockConfirm = false }, accent = DarkTextMid, textColor = DarkTextMid)
            },
            containerColor = Color(0xFF1E1C16),
            titleContentColor = DarkTextHigh,
            textContentColor = DarkTextMid,
        )
    }

    // Remove friend confirmation popup — only shown for existing friends.
    if (showRemoveConfirm) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showRemoveConfirm = false },
            title = { Text("Remove ${h.display_name}?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Remove ${h.display_name} from your friends? You can always send a new friend request later.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                SculptedButton(
                    text = "Confirm Remove",
                    onClick = {
                        showRemoveConfirm = false
                        scope.launch {
                            runCatching {
                                friendRepo.unfriend(userId)
                                isFriend = false
                            }
                        }
                    },
                    accent = Danger,
                    containerColor = Danger,
                    textColor = Color.White,
                    icon = Icons.Filled.Close,
                )
            },
            dismissButton = {
                SculptedTextButton(text = "Cancel", onClick = { showRemoveConfirm = false }, accent = DarkTextMid, textColor = DarkTextMid)
            },
            containerColor = Color(0xFF1E1C16),
            titleContentColor = DarkTextHigh,
            textContentColor = DarkTextMid,
        )
    }

    // ── Admin: Delete account dialog (with reason input) ──
    if (showDeleteDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteDialog = false; deleteReason = "" },
            title = { Text("Delete ${h.display_name}'s account?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "This will mark the account as deleted. The user will see a blocking popup on next sign-in and cannot access the app. You can restore the account later.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                    )
                    Spacer(Modifier.height(12.dp))
                    androidx.compose.material3.OutlinedTextField(
                        value = deleteReason,
                        onValueChange = { deleteReason = com.rork.rockscout.data.ProfanityFilter.filter(it) },
                        label = { Text("Reason for deletion") },
                        placeholder = { Text("e.g. Confirmed scamming on trade board") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF2A2820),
                            unfocusedContainerColor = Color(0xFF2A2820),
                            focusedTextColor = DarkTextHigh,
                            unfocusedTextColor = DarkTextHigh,
                            focusedIndicatorColor = Danger,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Danger,
                        ),
                        shape = RoundedCornerShape(12.dp),
                    )
                }
            },
            confirmButton = {
                SculptedButton(
                    text = "Delete Account",
                    onClick = {
                        val reason = deleteReason.trim().ifBlank { "Violation of community guidelines" }
                        showDeleteDialog = false
                        deleteReason = ""
                        scope.launch {
                            val result = auth.adminDeleteAccount(userId, reason)
                            if (result.isSuccess) {
                                viewedUserDeleted = true
                                adminActionMessage = "Account deleted. The user will see the blocking popup on next sign-in."
                            } else {
                                adminActionMessage = "Failed to delete account: ${result.exceptionOrNull()?.message}"
                            }
                        }
                    },
                    accent = Danger,
                    containerColor = Danger,
                    textColor = Color.White,
                    enabled = deleteReason.trim().isNotBlank(),
                )
            },
            dismissButton = {
                SculptedTextButton(text = "Cancel", onClick = { showDeleteDialog = false; deleteReason = "" }, accent = DarkTextMid, textColor = DarkTextMid)
            },
            containerColor = Color(0xFF1E1C16),
            titleContentColor = DarkTextHigh,
            textContentColor = DarkTextMid,
        )
    }

    // ── Admin: Restore account dialog ──
    if (showRestoreDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Restore ${h.display_name}'s account?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This will un-delete the account and allow the user to sign in normally again.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                SculptedButton(
                    text = "Restore Account",
                    onClick = {
                        showRestoreDialog = false
                        scope.launch {
                            val result = auth.adminRestoreAccount(userId)
                            if (result.isSuccess) {
                                viewedUserDeleted = false
                                adminActionMessage = "Account restored. The user can sign in normally."
                            } else {
                                adminActionMessage = "Failed to restore account: ${result.exceptionOrNull()?.message}"
                            }
                        }
                    },
                    accent = Success,
                    containerColor = Success,
                    textColor = Ink,
                )
            },
            dismissButton = {
                SculptedTextButton(text = "Cancel", onClick = { showRestoreDialog = false }, accent = DarkTextMid, textColor = DarkTextMid)
            },
            containerColor = Color(0xFF1E1C16),
            titleContentColor = DarkTextHigh,
            textContentColor = DarkTextMid,
        )
    }

}

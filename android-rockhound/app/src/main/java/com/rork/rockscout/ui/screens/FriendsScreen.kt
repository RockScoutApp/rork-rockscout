package com.rork.rockscout.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.FriendRepository
import com.rork.rockscout.data.HunterStatus
import com.rork.rockscout.data.NotificationRepository
import com.rork.rockscout.data.ReportRepository
import com.rork.rockscout.data.ReportScreenshotHelper
import com.rork.rockscout.data.SessionStatus
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.GlobalSearchBar
import com.rork.rockscout.ui.components.GlobalSearchSection
import com.rork.rockscout.ui.components.HunterStatusIcon
import com.rork.rockscout.ui.components.ProfileStatBar
import com.rork.rockscout.ui.components.ReportSubmittedDialog
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.profileBorderColor
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.launch

/* ── Main screen ─────────────────────────────────────────────────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    navController: NavController,
    initialTabIndex: Int = 0,
    @Suppress("UNUSED_PARAMETER") showFriendRequests: Boolean = true,
) {
    val auth = AuthRepository.instance
    val social = SocialRepository.instance
    val friendRepo = FriendRepository.instance
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

    val friends by friendRepo.friends.collectAsStateWithLifecycle()
    val incomingFriendRequests by friendRepo.incomingFriendRequests.collectAsStateWithLifecycle()
    val incomingMessageRequests by social.incomingRequests.collectAsStateWithLifecycle()
    val threads by social.threads.collectAsStateWithLifecycle()
    val messagingCount by social.totalMessagingCount.collectAsStateWithLifecycle()
    val notifications by NotificationRepository.instance.notifications.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    var hunterCache by remember { mutableStateOf<Map<String, SocialRepository.HunterProfile>>(emptyMap()) }

    LaunchedEffect(isSignedIn, profile.clubEnabled) {
        if (isSignedIn && profile.clubEnabled) {
            friendRepo.loadFriends()
            friendRepo.loadFriendRequests()
            friendRepo.loadBlocks()
            social.loadRequests()
            social.loadThreads()
            social.loadConnections()
            NotificationRepository.instance.loadNotifications()
        }
    }

    LaunchedEffect(friends, incomingFriendRequests, incomingMessageRequests, threads) {
        val ids = buildSet {
            friends.forEach { add(it.id) }
            incomingFriendRequests.forEach { add(it.sender_id) }
            incomingMessageRequests.forEach { add(it.sender_id) }
            threads.forEach {
                add(if (it.user_a == auth.currentUserId) it.user_b else it.user_a)
            }
        }.toList()
        if (ids.isNotEmpty()) {
            val fetched = mutableMapOf<String, SocialRepository.HunterProfile>()
            ids.chunked(50).forEach { chunk ->
                social.fetchProfiles(chunk).forEach { fetched[it.id] = it }
            }
            hunterCache = fetched
        }
    }

    if (!isSignedIn) {
        FriendsLockedState(
            emoji = "\uD83D\uDD11",
            title = "Sign in to RockScout Friends",
            message = "You need a RockScout account to connect with other hunters, share posts, and trade.",
            buttonLabel = "Sign in",
            onButton = { navController.navigate(Routes.SIGN_IN) },
        )
        return
    }
    if (clubLocked) {
        FriendsLockedState(
            emoji = "\uD83D\uDD12",
            title = "Unlock RockScout Friends",
            message = "Your 1-week free trial has ended. Subscribe or donate to keep connecting with other hunters.",
            buttonLabel = "Subscribe or donate",
            onButton = { navController.navigate(Routes.PAYWALL) },
        )
        return
    }
    if (!profile.clubEnabled) {
        FriendsLockedState(
            emoji = "\uD83E\uDD1D",
            title = "Turn on RockScout Friends",
            message = "Enable the RockScout Friends toggle in your Profile to connect with other hunters.",
            buttonLabel = "Open Profile",
            onButton = { navController.navigate(Routes.PROFILE) },
        )
        return
    }

    val traderIds = remember(friends) {
        friends.filter { it.status == "looking-for-trades" }.map { it.id }.toSet()
    }
    val sortedFriends = remember(friends, traderIds) {
        val traders = friends.filter { it.id in traderIds }
            .sortedBy { it.display_name.lowercase() }
        val hunters = friends.filter { it.id !in traderIds }
            .sortedBy { it.display_name.lowercase() }
        traders + hunters
    }

    val friendRequests = remember(incomingFriendRequests) {
        incomingFriendRequests
            .sortedByDescending { it.created_at }
    }
    val messageRequests = remember(incomingMessageRequests) {
        incomingMessageRequests
            .sortedByDescending { it.created_at }
    }

    // Pager state — 2 pages: Friends (0) and Messages (1)
    val pagerState = rememberPagerState(initialPage = initialTabIndex.coerceIn(0, 1)) { 2 }
    val pagerScope = rememberCoroutineScope()

    val screenTitle = when (pagerState.currentPage) {
        1 -> "RockScout Messages"
        else -> "RockScout Friends"
    }

    ScreenScaffold(title = screenTitle, onBack = { navController.popBackStack() }) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Pill switcher ──
            PagePillSwitcher(
                currentPage = pagerState.currentPage,
                onPageSelected = { page ->
                    pagerScope.launch { pagerState.animateScrollToPage(page) }
                },
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 0.dp,
            ) { page ->
                when (page) {
                    0 -> FriendsPage(
                        navController = navController,
                        friends = sortedFriends,
                        traderIds = traderIds,
                        friendRequests = friendRequests,
                        hunterCache = hunterCache,
                        notifications = notifications,
                        scope = scope,
                        friendRepo = friendRepo,
                    )
                    1 -> MessagesPage(
                        navController = navController,
                        threads = threads,
                        messageRequests = messageRequests,
                        hunterCache = hunterCache,
                        social = social,
                        auth = auth,
                        messagingCount = messagingCount,
                        scope = scope,
                    )
                }
            }
        }
    }
}

/* ── Pill switcher ────────────────────────────────────────────────────────── */

@Composable
private fun PagePillSwitcher(
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val pills = listOf("Friends" to 0, "Messages" to 1)
        pills.forEach { (label, page) ->
            val isActive = currentPage == page
            val accent = if (page == 0) Citrine else Aqua
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (isActive) accent.copy(alpha = 0.18f) else Color.Transparent
                    )
                    .glowingBorder(
                        1.5.dp,
                        if (isActive) accent else Color(0x33FFFFFF),
                        RoundedCornerShape(24.dp),
                    )
                    .clickable { onPageSelected(page) }
                    .padding(horizontal = 28.dp, vertical = 8.dp),
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isActive) accent else DarkTextMid,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                )
            }
            if (page < 1) {
                Spacer(Modifier.width(12.dp))
            }
        }
    }
}

/* ── Page 1: Friends ──────────────────────────────────────────────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendsPage(
    navController: NavController,
    friends: List<SocialRepository.HunterProfile>,
    traderIds: Set<String>,
    friendRequests: List<FriendRepository.FriendRequestRow>,
    hunterCache: Map<String, SocialRepository.HunterProfile>,
    notifications: List<NotificationRepository.NotificationRow>,
    scope: kotlinx.coroutines.CoroutineScope,
    friendRepo: FriendRepository,
) {
    var searchQuery by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // ── Search bar ──
        item {
            GlobalSearchSection(
                navController = navController,
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search friends, hunters, rocks, dig sites, BLM\u2026",
                friends = friends,
            )
        }

        // ── Friend Requests section (expandable) ──
        if (friendRequests.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.PersonAdd, contentDescription = null, tint = Citrine, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "FRIEND REQUESTS (${friendRequests.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            items(friendRequests, key = { it.id }) { friendReq ->
                val sender = hunterCache[friendReq.sender_id]
                ExpandableRequestTile(
                    name = sender?.display_name ?: "RockScout",
                    emoji = sender?.avatar_emoji ?: "\u26CF\uFE0F",
                    subtitle = "wants to be your friend",
                    body = null,
                    senderId = friendReq.sender_id,
                    timestamp = formatRelativeTime(friendReq.created_at),
                    onAccept = { scope.launch { runCatching { friendRepo.acceptFriendRequest(friendReq); NotificationRepository.instance.loadNotifications() } } },
                    onDeny = { scope.launch { runCatching { friendRepo.denyFriendRequest(friendReq); NotificationRepository.instance.loadNotifications() } } },
                    onBlock = { scope.launch { runCatching { friendRepo.denyAndBlockFriendRequest(friendReq); NotificationRepository.instance.loadNotifications() } } },
                    onPreview = { navController.navigate(Routes.userProfile(friendReq.sender_id)) },
                )
            }
            item { HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp)) }
        }

        // ── Friends section ──
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Group, contentDescription = null, tint = Aqua, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "FRIENDS (${friends.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = Aqua,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        if (friends.isEmpty()) {
            item {
                DarkCard(modifier = Modifier.fillMaxWidth(), accent = TextMid) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("\uD83E\uDD1D", style = MaterialTheme.typography.displayMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "No friends yet.",
                            style = MaterialTheme.typography.titleMedium,
                            color = DarkTextHigh,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Scan for RockScouts and send a friend request to start building your Friends list.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextMid,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        } else {
            val traders = friends.filter { it.id in traderIds }
            val huntersList = friends.filter { it.id !in traderIds }
            if (traders.isNotEmpty()) {
                item {
                    Text(
                        "TRADERS",
                        style = MaterialTheme.typography.labelMedium,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                    )
                }
                items(traders, key = { it.id }) { friend ->
                    FriendCard(
                        friend = friend,
                        isTrader = true,
                        hasNewPosts = notifications.any {
                            it.read_at == null && it.type == NotificationRepository.TYPE_NEW_POST && it.actor_id == friend.id
                        },
                        onTap = { navController.navigate(Routes.userProfile(friend.id)) },
                        onViewCollection = { navController.navigate(Routes.userCollection(friend.id)) },
                        onViewWishlist = { navController.navigate(Routes.userWishlist(friend.id)) },
                    )
                }
            }
            if (huntersList.isNotEmpty()) {
                item {
                    Text(
                        "HUNTERS",
                        style = MaterialTheme.typography.labelMedium,
                        color = Aqua,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                    )
                }
                items(huntersList, key = { it.id }) { friend ->
                    FriendCard(
                        friend = friend,
                        isTrader = false,
                        hasNewPosts = notifications.any {
                            it.read_at == null && it.type == NotificationRepository.TYPE_NEW_POST && it.actor_id == friend.id
                        },
                        onTap = { navController.navigate(Routes.userProfile(friend.id)) },
                        onViewCollection = { navController.navigate(Routes.userCollection(friend.id)) },
                        onViewWishlist = { navController.navigate(Routes.userWishlist(friend.id)) },
                    )
                }
            }
        }
    }
}

/* ── Page 2: Messages ─────────────────────────────────────────────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessagesPage(
    navController: NavController,
    threads: List<SocialRepository.ThreadRow>,
    messageRequests: List<SocialRepository.MessageRequestRow>,
    hunterCache: Map<String, SocialRepository.HunterProfile>,
    social: SocialRepository,
    auth: AuthRepository,
    messagingCount: Int,
    scope: kotlinx.coroutines.CoroutineScope,
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredThreads = remember(threads, searchQuery, hunterCache) {
        if (searchQuery.isBlank()) threads
        else {
            threads.filter { thread ->
                val otherId = if (thread.user_a == auth.currentUserId) thread.user_b else thread.user_a
                val name = hunterCache[otherId]?.display_name ?: ""
                name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // ── Search bar ──
        item {
            GlobalSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search conversations\u2026",
            )
        }

        // ── Message Requests section (expandable) ──
        if (messageRequests.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Mail, contentDescription = null, tint = Citrine, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "MESSAGE REQUESTS (${messageRequests.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            items(messageRequests, key = { it.id }) { msgReq ->
                val sender = hunterCache[msgReq.sender_id]
                ExpandableRequestTile(
                    name = sender?.display_name ?: "RockScout",
                    emoji = sender?.avatar_emoji ?: "\u26CF\uFE0F",
                    subtitle = "wants to message you",
                    body = msgReq.body,
                    senderId = msgReq.sender_id,
                    timestamp = formatRelativeTime(msgReq.created_at),
                    onAccept = {
                        scope.launch {
                            runCatching {
                                social.acceptRequest(msgReq)
                                social.loadRequests()
                                social.loadThreads()
                                social.loadConnections()
                                NotificationRepository.instance.loadNotifications()
                            }
                        }
                    },
                    onDeny = {
                        scope.launch { runCatching { social.denyRequest(msgReq); social.loadRequests(); NotificationRepository.instance.loadNotifications() } }
                    },
                    onBlock = {
                        scope.launch { runCatching { social.denyAndBlockRequest(msgReq); social.loadRequests(); NotificationRepository.instance.loadNotifications() } }
                    },
                    onPreview = { navController.navigate(Routes.messengerRequest(msgReq.id)) },
                )
            }
            item { HorizontalDivider(color = Color(0x22FFFFFF), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp)) }
        }

        // ── Conversations section ──
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Mail, contentDescription = null, tint = Aqua, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "CONVERSATIONS",
                    style = MaterialTheme.typography.labelMedium,
                    color = Aqua,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        if (filteredThreads.isEmpty()) {
            item {
                DarkCard(modifier = Modifier.fillMaxWidth(), accent = TextMid) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("\uD83D\uDC8C", style = MaterialTheme.typography.displayMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (searchQuery.isNotBlank()) "No conversations match your search." else "No conversations yet.",
                            style = MaterialTheme.typography.titleMedium,
                            color = DarkTextHigh,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                        if (searchQuery.isBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Send a message request from a user's profile or scan to start a conversation.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextMid,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        } else {
            items(filteredThreads, key = { it.id }) { thread ->
                val otherId = if (thread.user_a == auth.currentUserId) thread.user_b else thread.user_a
                val other = hunterCache[otherId]
                val lastMessage = remember(thread.id) { social.getLastMessageForThread(thread.id) }
                val unreadCount = remember(thread.id, messagingCount) { social.unreadCountForThread(thread.id) }
                SwipeableConversationTile(
                    name = other?.display_name ?: "RockScout",
                    emoji = other?.avatar_emoji ?: "\u26CF\uFE0F",
                    status = other?.status,
                    preview = lastMessage?.body ?: "Tap to open conversation",
                    timestamp = lastMessage?.created_at?.let { formatRelativeTime(it) }
                        ?: formatRelativeTime(thread.last_message_at),
                    unreadCount = unreadCount,
                    onClick = { navController.navigate(Routes.messengerThread(otherId)) },
                    onAvatarClick = { navController.navigate(Routes.userProfile(otherId)) },
                    onDelete = {
                        scope.launch { runCatching { social.deleteThread(thread.id) } }
                    },
                )
            }
        }
    }
}

/* ── Expandable request tile (friend + message requests) ──────────────────── */

@Composable
private fun ExpandableRequestTile(
    name: String,
    emoji: String,
    subtitle: String,
    body: String?,
    senderId: String,
    timestamp: String,
    onAccept: () -> Unit,
    onDeny: () -> Unit,
    onBlock: () -> Unit,
    onPreview: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var showBlockConfirm by remember { mutableStateOf(false) }
    var showReportConfirm by remember { mutableStateOf(false) }
    var reportSubmitted by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val view = LocalView.current
    val reportScope = rememberCoroutineScope()
    val reportRepo = AppRepository.instance
    val myProf by reportRepo.profile.collectAsStateWithLifecycle()

    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
        Column(modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Citrine.copy(alpha = 0.45f), Aqua.copy(alpha = 0.25f)))),
                    contentAlignment = Alignment.Center,
                ) { Text(emoji, style = MaterialTheme.typography.titleMedium) }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.titleMedium, color = DarkTextHigh, fontWeight = FontWeight.Bold)
                    Text(subtitle, style = MaterialTheme.typography.labelMedium, color = DarkTextMid)
                    if (body != null && body.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            body,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextMid,
                            maxLines = if (expanded) Int.MAX_VALUE else 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(timestamp, style = MaterialTheme.typography.labelSmall, color = DarkTextMid)
                }
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = DarkTextMid,
                    modifier = Modifier.size(24.dp),
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SculptedOutlinedButton(
                            text = "Preview",
                            onClick = onPreview,
                            modifier = Modifier.weight(1f),
                            accent = Aqua,
                            textColor = Aqua,
                            icon = Icons.Filled.Visibility,
                        )
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
        }
    }

    if (showBlockConfirm) {
        AlertDialog(
            onDismissRequest = { showBlockConfirm = false },
            title = { Text("Block $name?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
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
                    onClick = { showBlockConfirm = false; onBlock() },
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
        AlertDialog(
            onDismissRequest = { showReportConfirm = false },
            title = { Text("Report $name?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
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
                            val screenshotPath = ReportScreenshotHelper.captureScreenshot(context, view, reportId)
                            ReportRepository.instance.reportUser(
                                reportedUserId = senderId,
                                reason = "Inappropriate behavior",
                                screenshotPath = screenshotPath,
                                reporterName = myProf.name,
                                reportedName = name,
                                reportedAvatar = null,
                            ).onSuccess {
                                reportSubmitted = true
                                val intent = ReportScreenshotHelper.buildEmailIntent(
                                    context = context,
                                    reportedUserName = name,
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

/* ── Swipeable conversation tile ──────────────────────────────────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableConversationTile(
    name: String,
    emoji: String,
    status: String?,
    preview: String,
    timestamp: String,
    unreadCount: Int,
    onClick: () -> Unit,
    onAvatarClick: () -> Unit,
    onDelete: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart && !showDeleteDialog) {
                showDeleteDialog = true
            }
            false
        },
    )

    SwipeToDismissBox(
        state = swipeState,
        modifier = Modifier.fillMaxWidth(),
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Danger.copy(alpha = 0.85f))
                    .glowingBorder(1.dp, Danger.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        enableDismissFromStartToEnd = false,
    ) {
        ConversationTile(
            name = name,
            emoji = emoji,
            status = status,
            preview = preview,
            timestamp = timestamp,
            unreadCount = unreadCount,
            onClick = onClick,
            onAvatarClick = onAvatarClick,
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete conversation?", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Delete this conversation with $name? This will remove all messages.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                SculptedButton(
                    text = "Confirm",
                    onClick = { showDeleteDialog = false; onDelete() },
                    accent = Danger,
                    containerColor = Danger,
                    textColor = Color.White,
                    icon = Icons.Filled.Delete,
                )
            },
            dismissButton = {
                SculptedTextButton(text = "Cancel", onClick = { showDeleteDialog = false }, accent = DarkTextMid, textColor = DarkTextMid)
            },
            containerColor = Color(0xFF1E1C16),
            titleContentColor = DarkTextHigh,
            textContentColor = DarkTextMid,
        )
    }
}

@Composable
private fun ConversationTile(
    name: String,
    emoji: String,
    status: String?,
    preview: String,
    timestamp: String,
    unreadCount: Int,
    onClick: () -> Unit,
    onAvatarClick: () -> Unit,
) {
    val statusColor = when (status) {
        "on-the-hunt" -> Success
        "wishing" -> Citrine
        "looking-for-trades" -> Aqua
        else -> TextMid
    }
    DarkCard(
        accent = Aqua,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar with status dot
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Aqua.copy(alpha = 0.40f), Citrine.copy(alpha = 0.20f))))
                        .clickable(onClick = onAvatarClick),
                    contentAlignment = Alignment.Center,
                ) { Text(emoji, style = MaterialTheme.typography.titleMedium) }
                // Status indicator dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                        .glowingBorder(1.5.dp, Color(0xFF1E1C16), CircleShape),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        name,
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                    )
                    if (timestamp.isNotBlank()) {
                        Text(
                            timestamp,
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTextMid,
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                    if (unreadCount > 0) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Citrine)
                                .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape)
                                .size(24.dp),
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
                Spacer(Modifier.height(2.dp))
                Text(
                    preview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/* ── Friend card (existing design, preserved) ────────────────────────────── */

@Composable
private fun FriendCard(
    friend: SocialRepository.HunterProfile,
    isTrader: Boolean,
    hasNewPosts: Boolean,
    onTap: () -> Unit,
    onViewCollection: () -> Unit,
    onViewWishlist: () -> Unit,
) {
    val status = when (friend.status) {
        "on-the-hunt" -> HunterStatus.ON_THE_HUNT
        "wishing" -> HunterStatus.WISHING
        "looking-for-trades" -> HunterStatus.LOOKING_FOR_TRADES
        else -> HunterStatus.OFF_GRID
    }
    val statusLabel = status.label
    val labelColor = if (isTrader) Citrine else Aqua
    DarkCard(
        accent = labelColor,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onTap),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isTrader) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Success)
                            .glowingBorder(1.dp, Success.copy(alpha = 0.35f), CircleShape),
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(labelColor.copy(alpha = 0.40f), Aqua.copy(alpha = 0.20f))))
                        .glowingBorder(2.dp, profileBorderColor(status), CircleShape),
                    contentAlignment = Alignment.Center,
                ) { Text(friend.avatar_emoji, style = MaterialTheme.typography.titleMedium) }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        friend.display_name,
                        style = MaterialTheme.typography.titleSmall,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    )
                    if (friend.premium_badge) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Citrine.copy(alpha = 0.30f))
                                .padding(horizontal = 4.dp, vertical = 1.dp),
                        ) {
                            Text("PREMIUM", style = MaterialTheme.typography.labelSmall, color = Citrine, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    HunterStatusIcon(status = status, size = 16.dp)
                    Text(
                        statusLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = Aqua,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                    )
                }
                Text(
                    if (isTrader) "Trader" else "Hunter",
                    style = MaterialTheme.typography.labelSmall,
                    color = labelColor,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
                Spacer(Modifier.height(6.dp))
                ProfileStatBar(
                    collectionCount = friend.collection_count,
                    wishlistCount = friend.wishlist_count,
                    onCollectionClick = onViewCollection,
                    onWishlistClick = onViewWishlist,
                )
            }

            if (hasNewPosts) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Citrine.copy(alpha = 0.25f))
                        .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        "new posts!",
                        style = MaterialTheme.typography.labelSmall,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

/* ── Locked state (existing, preserved) ──────────────────────────────────── */

@Composable
private fun FriendsLockedState(
    emoji: String,
    title: String,
    message: String,
    buttonLabel: String,
    onButton: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(emoji, style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium, color = TextMid, textAlign = TextAlign.Center)
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

/* ── Helpers ──────────────────────────────────────────────────────────────── */

private fun formatRelativeTime(ts: String): String {
    val t = runCatching { java.time.OffsetDateTime.parse(ts).toInstant().toEpochMilli() }.getOrDefault(0L)
    if (t == 0L) return ""
    val diff = System.currentTimeMillis() - t
    val mins = diff / 60_000
    return when {
        mins < 1 -> "just now"
        mins < 60 -> "${mins}m ago"
        mins < 60 * 24 -> "${mins / 60}h ago"
        mins < 60 * 24 * 7 -> "${mins / (60 * 24)}d ago"
        else -> java.text.SimpleDateFormat("MMM d", java.util.Locale.US).format(java.util.Date(t))
    }
}

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.HunterStatus
import com.rork.rockscout.data.SessionStatus
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.GlobalSearchSection
import com.rork.rockscout.ui.components.HunterStatusIcon
import com.rork.rockscout.ui.components.profileBorderColor
import com.rork.rockscout.ui.components.ProfileStatBar
import com.rork.rockscout.ui.components.ProBadge
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.components.glowingBorder

/**
 * Browse + search screen for discovering other RockScout hunters.
 *
 * Shows every discoverable hunter (those opted into the RockScout Friends
 * club) in an alphabetical list, with a live search bar that filters by
 * display name. Tapping a hunter opens their public profile.
 */
@Composable
fun DiscoverHuntersScreen(navController: NavController) {
    val auth = AuthRepository.instance
    val social = SocialRepository.instance
    val sessionStatus by auth.sessionStatus.collectAsStateWithLifecycle()
    val isSignedIn = sessionStatus is SessionStatus.Authenticated
    val connections by social.connections.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current

    // ─── Premium gating: Discover Hunters is a social feature ───
    val accessManager = com.rork.rockscout.data.IdentifyAccessManager.instance
    val purchaseManager = com.rork.rockscout.data.PurchaseManager.instance
    val isPremium by purchaseManager.effectiveIsPremium.collectAsStateWithLifecycle()
    val clubLocked = remember(isPremium) { accessManager.isSocialLocked(isPremium) }
    if (clubLocked) {
        com.rork.rockscout.ui.components.ClubLockedState(
            emoji = "\uD83D\uDD12",
            title = "Unlock Discover Hunters",
            message = "Your 1-week free trial has ended. Subscribe or donate to find and connect with other hunters.",
            buttonLabel = "Subscribe or donate",
            onButton = { navController.navigate(Routes.PAYWALL) },
        )
        return
    }

    var query by remember { mutableStateOf("") }
    var hunters by remember { mutableStateOf<List<SocialRepository.HunterProfile>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchJob by remember { mutableStateOf<Job?>(null) }
    var globalSearchQuery by remember { mutableStateOf("") }

    // Initial load — list every discoverable hunter.
    LaunchedEffect(isSignedIn) {
        if (isSignedIn) {
            social.loadConnections()
            hunters = social.searchHunters("")
        }
    }

    // Debounced live search.
    LaunchedEffect(query) {
        if (!isSignedIn) return@LaunchedEffect
        searchJob?.cancel()
        searchJob = scope.launch {
            isSearching = true
            delay(220) // debounce
            hunters = social.searchHunters(query.trim())
            isSearching = false
        }
    }

    ScreenScaffold(title = "Discover Hunters", onBack = { navController.popBackStack() }) {
        if (!isSignedIn) {
            Box(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("\uD83D\uDD11", style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Sign in to browse hunters",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Sign in to discover and connect with fellow RockScouts.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Citrine)
                            .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .clickable { navController.navigate(Routes.SIGN_IN) }
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                    ) {
                        Text("Sign in", color = Ink, fontWeight = FontWeight.Bold)
                    }
                }
            }
            return@ScreenScaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().imePadding(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Global search bar
            item {
                GlobalSearchSection(
                    navController = navController,
                    query = globalSearchQuery,
                    onQueryChange = { globalSearchQuery = it },
                    placeholder = "Search every part of RockScout…",
                    hunters = hunters,
                )
            }

            // Hunter search bar
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                    placeholder = { Text("Search by name or email…", color = DarkTextMid) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextMid) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF3A3830))
                                    .glowingBorder(1.dp, Color(0xFF3A3830).copy(alpha = 0.35f), CircleShape)
                                    .clickable { query = "" },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("✕", style = MaterialTheme.typography.labelMedium, color = TextMid)
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2A2820),
                        unfocusedContainerColor = Color(0xFF2A2820),
                        focusedTextColor = DarkTextHigh,
                        unfocusedTextColor = DarkTextHigh,
                        focusedIndicatorColor = Citrine,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Citrine,
                    ),
                    shape = RoundedCornerShape(14.dp),
                )
            }

            // Result count
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.PersonSearch,
                        contentDescription = null,
                        tint = Citrine,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (isSearching) "Searching…"
                        else if (query.isNotBlank()) "${hunters.size} hunter${if (hunters.size != 1) "s" else ""} matching \"$query\""
                        else "${hunters.size} discoverable hunter${if (hunters.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMid,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Empty state
            if (hunters.isEmpty() && !isSearching) {
                item {
                    DarkCard(modifier = Modifier.fillMaxWidth(), accent = TextMid) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text("\uD83D\uDD0D", style = MaterialTheme.typography.displayMedium)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                if (query.isBlank()) "No discoverable hunters yet."
                                else "No hunters match \"$query\".",
                                style = MaterialTheme.typography.titleMedium,
                                color = DarkTextHigh,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                if (query.isBlank())
                                    "When hunters turn on RockScout Friends, they'll show up here."
                                else "Try a different name or check the spelling.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextMid,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

            // Hunter list
            items(hunters, key = { it.id }) { hunter ->
                DiscoverableHunterRow(
                    hunter = hunter,
                    isFriend = connections.contains(hunter.id),
                    onTap = { navController.navigate(Routes.userProfile(hunter.id)) },
                    onViewCollection = { navController.navigate(Routes.userCollection(hunter.id)) },
                    onViewWishlist = { navController.navigate(Routes.userWishlist(hunter.id)) },
                )
            }
        }
    }
}

@Composable
private fun DiscoverableHunterRow(
    hunter: SocialRepository.HunterProfile,
    isFriend: Boolean,
    onTap: () -> Unit,
    onViewCollection: () -> Unit,
    onViewWishlist: () -> Unit,
) {
    val statusAccent = when (hunter.status) {
        "on-the-hunt" -> Success
        "wishing" -> Citrine
        "looking-for-trades" -> Aqua
        else -> TextMid
    }
    val status = when (hunter.status) {
        "on-the-hunt" -> HunterStatus.ON_THE_HUNT
        "wishing" -> HunterStatus.WISHING
        "looking-for-trades" -> HunterStatus.LOOKING_FOR_TRADES
        else -> HunterStatus.OFF_GRID
    }
    val statusLabel = status.label
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = statusAccent) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onTap)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(statusAccent.copy(alpha = 0.45f), Aqua.copy(alpha = 0.20f))
                        )
                    )
                    .glowingBorder(2.dp, profileBorderColor(status), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(hunter.avatar_emoji, style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        hunter.display_name,
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                    if (hunter.premium_badge) {
                        Spacer(Modifier.width(6.dp))
                        ProBadge()
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HunterStatusIcon(status = status, size = 16.dp)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        statusLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = Aqua,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                    )
                }
                Text(
                    "Lvl ${hunter.level}" +
                        if (isFriend) " · RockScout Friend" else " · Tap to view profile",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTextMid,
                )
                Spacer(Modifier.height(6.dp))
                ProfileStatBar(
                    collectionCount = hunter.collection_count,
                    wishlistCount = hunter.wishlist_count,
                    onCollectionClick = onViewCollection,
                    onWishlistClick = onViewWishlist,
                )
            }
            // Friend badge
            if (isFriend) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Success.copy(alpha = 0.18f))
                        .glowingBorder(1.dp, Success.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        "FRIEND",
                        style = MaterialTheme.typography.labelSmall,
                        color = Success,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

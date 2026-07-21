package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.data.SpecimenImages
import com.rork.rockscout.ui.components.CardHeart
import com.rork.rockscout.ui.components.CategoryFilterRow
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.FullScreenImageViewer
import com.rork.rockscout.ui.components.ListCategoryFilter
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.SpecimenAddShare
import com.rork.rockscout.ui.components.SpecimenListItem
import com.rork.rockscout.ui.components.CompactAddShareButton
import com.rork.rockscout.ui.components.RarityIndicator
import com.rork.rockscout.ui.components.filterSpecimensByCategory
import com.rork.rockscout.ui.components.rockClassColor
import com.rork.rockscout.ui.components.shortCategoryLabel
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.launch

enum class UserCollectionMode {
    COLLECTION,
    WISHLIST,
}

/**
 * Read-only collection / wishlist viewer for any RockScout user.
 *
 * For the signed-in user it reads from [AppRepository] so the data is live.
 * For other users it reads the public collection/wishlist IDs stored in the
 * local users table.
 *
 * Other users can like entries in another user's collection or wishlist using
 * the heart icon on each card.
 */
@Composable
fun UserCollectionScreen(
    navController: NavController,
    userId: String,
    mode: UserCollectionMode,
) {
    val auth = AuthRepository.instance
    val repo = AppRepository.instance
    val social = SocialRepository.instance
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val isMe = auth.currentUserId == userId
    val collection by repo.collection.collectAsStateWithLifecycle()
    val wishlist by repo.wishlist.collectAsStateWithLifecycle()
    val likedSpecimens by repo.likedSpecimens.collectAsStateWithLifecycle()
    val listLikes by social.listLikes.collectAsStateWithLifecycle()
    val likedListEntries by social.likedListEntries.collectAsStateWithLifecycle()

    var userName by remember { mutableStateOf<String?>(null) }
    var specimenIds by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerInitialPage by remember { mutableStateOf(0) }
    var selectedFilter by remember { mutableStateOf<ListCategoryFilter?>(null) }

    val listType = when (mode) {
        UserCollectionMode.COLLECTION -> "collection"
        UserCollectionMode.WISHLIST -> "wishlist"
    }

    LaunchedEffect(userId, mode) {
        if (isMe) {
            userName = repo.profile.value.name
            specimenIds = when (mode) {
                UserCollectionMode.COLLECTION -> repo.collection.value.map { it.specimenId }
                UserCollectionMode.WISHLIST -> repo.wishlist.value
            }
        } else {
            val profile = social.fetchUserProfile(userId)
            userName = profile?.display_name
            specimenIds = when (mode) {
                UserCollectionMode.COLLECTION -> social.fetchUserCollection(userId)
                UserCollectionMode.WISHLIST -> social.fetchUserWishlist(userId)
            }
        }
        // Load list likes so heart states and counts are current
        social.loadListLikes()
    }

    // Apply category filter
    val filteredSpecimenIds = remember(specimenIds, selectedFilter) {
        if (selectedFilter == null) specimenIds
        else specimenIds.filter { id ->
            SeedData.specimenById(id)?.let { spec ->
                filterSpecimensByCategory(listOf(spec), selectedFilter).isNotEmpty()
            } ?: false
        }
    }

    val titlePrefix = when (mode) {
        UserCollectionMode.COLLECTION -> "Rocks"
        UserCollectionMode.WISHLIST -> "Wishlist"
    }
    val title = "${userName ?: "RockScout"}'s $titlePrefix"
    val emptyEmoji = if (mode == UserCollectionMode.COLLECTION) "\uD83E\uDEA8" else "\uD83D\uDD16"
    val emptyTitle = if (mode == UserCollectionMode.COLLECTION) "No rocks yet" else "No wishlist items"
    val emptyMessage = if (mode == UserCollectionMode.COLLECTION) {
        "${userName ?: "This hunter"} hasn't added any specimens to their collection yet."
    } else {
        "${userName ?: "This hunter"} hasn't added any specimens to their wishlist yet."
    }

    ScreenScaffold(title = title, onBack = { navController.popBackStack() }) {
        if (specimenIds.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier.size(96.dp).clip(RoundedCornerShape(48.dp)).background(Citrine.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) { Text(emptyEmoji, style = MaterialTheme.typography.displayMedium) }
                Spacer(Modifier.height(20.dp))
                Text(emptyTitle, style = MaterialTheme.typography.headlineMedium, color = Aqua)
                Spacer(Modifier.height(8.dp))
                Text(
                    emptyMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Aqua,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                item {
                    Text(
                        "${filteredSpecimenIds.size} specimen${if (filteredSpecimenIds.size != 1) "s" else ""}" +
                            if (selectedFilter != null && specimenIds.size != filteredSpecimenIds.size) " of ${specimenIds.size}" else "",
                        style = MaterialTheme.typography.labelMedium,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
                item {
                    CategoryFilterRow(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it },
                    )
                }
                items(filteredSpecimenIds) { id ->
                    val spec = SeedData.specimenById(id)
                    if (spec != null) {
                        val accent = rockClassColor(spec.rockClass)
                        val imgUrls = SpecimenImages.urls[spec.id] ?: spec.imageUrls
                        val likeCount = social.listLikeCount(userId, spec.id, listType)
                        SpecimenListItem(
                            specimen = spec,
                            accent = accent,
                            onClick = { navController.navigate(Routes.specimen(spec.id)) },
                            onImageClick = {
                                viewerUrls = imgUrls
                                viewerInitialPage = 0
                            },
                            imageSize = 113.dp,
                            subtitle = {
                                if (!isMe && likeCount > 0) {
                                    Text(
                                        "♥ $likeCount",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Citrine,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                    )
                                }
                            },
                            addShare = if (isMe) {
                                {
                                    val collected = collection.any { it.specimenId == spec.id }
                                    val wishlisted = wishlist.contains(spec.id)
                                    CompactAddShareButton(
                                        collected = collected,
                                        wishlisted = wishlisted,
                                        onCollectionToggle = { repo.toggleCollection(spec.id) },
                                        onWishlistToggle = { repo.toggleWishlist(spec.id) },
                                        accent = accent,
                                        size = 44.dp,
                                    )
                                }
                            } else {
                                null
                            },
                            heart = {
                                if (isMe) {
                                    val isLiked = likedSpecimens.contains(spec.id)
                                    CardHeart(
                                        active = isLiked,
                                        onToggle = { repo.toggleLike(spec.id) },
                                        accent = accent,
                                        size = 44.dp,
                                        likeCount = repo.specimenLikeCount(spec.id),
                                    )
                                } else {
                                    val isLiked = social.isListEntryLiked(userId, spec.id, listType)
                                    CardHeart(
                                        active = isLiked,
                                        onToggle = {
                                            scope.launch {
                                                social.toggleListLike(userId, spec.id, listType)
                                            }
                                        },
                                        accent = accent,
                                        size = 44.dp,
                                        likeCount = likeCount,
                                    )
                                }
                            },
                            showCategory = true,
                            categoryLabel = shortCategoryLabel(spec.category),
                        )
                    }
                }
            }
        }
    }

    if (viewerUrls.isNotEmpty()) {
        FullScreenImageViewer(
            imageUrls = viewerUrls,
            initialPage = viewerInitialPage,
            onDismiss = { viewerUrls = emptyList() },
        )
    }
}

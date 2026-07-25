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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AchievementsRepository
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.IdentifyAccessManager
import com.rork.rockscout.data.PurchaseManager
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.SpecimenImages
import com.rork.rockscout.data.XpSource
import com.rork.rockscout.ui.components.GlobalSearchSection
import com.rork.rockscout.ui.components.LockedFeatureBanner
import com.rork.rockscout.ui.components.CategoryFilterRow
import com.rork.rockscout.ui.components.ListCategoryFilter
import com.rork.rockscout.ui.components.filterSpecimensByCategory
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.FieldCaptureCard
import com.rork.rockscout.ui.components.LongPressableImage
import com.rork.rockscout.ui.components.MergeableCapture
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.ShareCardImage
import com.rork.rockscout.ui.components.CardHeart
import com.rork.rockscout.ui.components.SpecimenAddShare
import com.rork.rockscout.ui.components.SpecimenListItem
import com.rork.rockscout.ui.components.CompactAddShareButton
import com.rork.rockscout.ui.components.YooperliteHeart
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.RarityIndicator
import com.rork.rockscout.ui.components.rockClassColor
import com.rork.rockscout.ui.components.shortCategoryLabel
import com.rork.rockscout.ui.components.SculptedIconButton

import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextLow
import android.net.Uri
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.rork.rockscout.ui.components.DeleteConfirmDialog

@Composable
fun WishlistScreen(navController: NavController) {
    val repo = AppRepository.instance
    val wishlist by repo.wishlist.collectAsStateWithLifecycle()
    val collection by repo.collection.collectAsStateWithLifecycle()
    val captures by repo.captures.collectAsStateWithLifecycle()
    val likedSpecimens by repo.likedSpecimens.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val accessManager = IdentifyAccessManager.instance
    val purchaseManager = PurchaseManager.instance
    val isPremium by purchaseManager.isPremium.collectAsStateWithLifecycle()
    var shareToProfileCapture by remember { mutableStateOf<com.rork.rockscout.data.CapturedPhoto?>(null) }
    var shareToProfileSpec by remember { mutableStateOf<com.rork.rockscout.data.Specimen?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var pendingDeleteCapture by remember { mutableStateOf<com.rork.rockscout.data.CapturedPhoto?>(null) }
    var selectedFilter by remember { mutableStateOf<ListCategoryFilter?>(null) }
    var viewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerInitialPage by remember { mutableIntStateOf(0) }

    // Field captures promoted to Wishlist
    val captureInWishlist = remember(captures) {
        captures.filter { it.inWishlist }
    }

    ScreenScaffold(title = "Wishlist", onBack = { navController.popBackStack() }) {
        if (wishlist.isEmpty() && captureInWishlist.isEmpty()) {
            EmptyState(
                emoji = "\uD83D\uDD16",
                title = "Your wishlist is empty",
                message = "Bookmark the rocks, minerals, and crystals you're hunting for and they'll appear here.",
            )
        } else {
            val sortedWishlist = remember(wishlist) {
                wishlist.sortedBy { SeedData.specimenById(it)?.name?.lowercase() ?: it }
            }
            // Apply category filter
            val filteredWishlist = remember(sortedWishlist, selectedFilter) {
                if (selectedFilter == null) sortedWishlist
                else sortedWishlist.filter { id ->
                    SeedData.specimenById(id)?.let { spec ->
                        filterSpecimensByCategory(listOf(spec), selectedFilter).isNotEmpty()
                    } ?: false
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                item {
                    GlobalSearchSection(
                        navController = navController,
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        placeholder = "Search wishlist, specimens, dig sites, hunters…",
                    )
                }
                item {
                    CategoryFilterRow(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it },
                    )
                }
                item {
                    val total = filteredWishlist.size + captureInWishlist.size
                    Text(
                        "Specimens you're hunting for. We'll flag them when you log a matching capture, and you can post one as a Want on the Trade Board.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMid,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                    Text(
                        "$total specimens you're chasing.",
                        style = MaterialTheme.typography.labelMedium,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }

                // Field capture cards
                if (captureInWishlist.isNotEmpty()) {
                    item {
                        Text(
                            "Field Captures",
                            style = MaterialTheme.typography.titleSmall,
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
                        )
                    }
                    items(captureInWishlist.size) { index ->
                        val capture = captureInWishlist[index]
                        val spec = SeedData.specimenById(capture.specimenId)
                        val accent = if (spec != null) rockClassColor(spec.rockClass) else Citrine
                        val others = remember(captures, capture.id) {
                            captures.filter { it.id != capture.id }.map {
                                MergeableCapture(
                                    id = it.id,
                                    displayName = it.displayName(SeedData.specimenById(it.specimenId)?.name ?: "Unknown specimen"),
                                    thumbnailUri = it.imageUris.firstOrNull(),
                                    emoji = it.specimenEmoji,
                                    photoCount = it.imageUris.size,
                                )
                            }
                        }
                        FieldCaptureCard(
                            capture = capture,
                            specimenName = spec?.name ?: "Unknown specimen",
                            accent = accent,
                            otherCaptures = others,
                            inCollection = capture.inCollection,
                            inWishlist = true,
                            onToggleCollection = { repo.toggleCaptureInCollection(capture.id) },
                            onToggleWishlist = { repo.toggleCaptureInWishlist(capture.id) },
                            onSaveFields = { name, location, info ->
                                repo.updateCaptureFields(capture.id, name, location, info)
                            },
                            onAddImage = { uriStr -> repo.addImageToCapture(capture.id, uriStr) },
                            onMergeCapture = { otherId -> repo.mergeCaptures(capture.id, otherId) },
                            onRemoveImage = { idx -> repo.removeImageFromCapture(capture.id, idx) },
                            onDelete = { pendingDeleteCapture = capture },
                            onShare = {
                                scope.launch {
                                    val photo: android.graphics.Bitmap? =
                                        capture.imageUris.firstOrNull()?.let { uriStr ->
                                            ShareCardImage.loadDownsampled(context, Uri.parse(uriStr))
                                        }
                                    ShareCardImage.share(
                                        context = context,
                                        title = capture.displayName(spec?.name ?: "Unknown specimen"),
                                        subtitle = (spec?.category ?: "Field capture") + "  •  " +
                                            SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                                                .format(Date(capture.timestamp)),
                                        body = capture.generalInfo.ifBlank { null },
                                        accentHex = if (spec != null) spec.colorHex else 0xFF2C6F9B,
                                        photoBitmap = photo,
                                        fileName = "rockscout_wishlist_capture_${capture.id}",
                                    )
                                }
                            },
                            onShareToProfile = { shareToProfileCapture = capture },
                        )
                    }
                }

                // Database specimen cards
                if (filteredWishlist.isNotEmpty()) {
                    if (captureInWishlist.isNotEmpty()) {
                        item {
                            Text(
                                "Database Specimens",
                                style = MaterialTheme.typography.titleSmall,
                                color = Aqua,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
                            )
                        }
                    }
                    items(filteredWishlist.size) { index ->
                        val spec = SeedData.specimenById(filteredWishlist[index]) ?: return@items
                        val accent = rockClassColor(spec.rockClass)
                        val isInCollection = collection.any { it.specimenId == spec.id }
                        val isLiked = likedSpecimens.contains(spec.id)
                        val imageUrls = SpecimenImages.urls[spec.id] ?: spec.imageUrls
                        SpecimenListItem(
                            specimen = spec,
                            accent = accent,
                            onClick = { navController.navigate(Routes.specimen(spec.id)) },
                            onImageClick = {
                                viewerUrls = imageUrls
                                viewerInitialPage = 0
                            },
                            imageSize = 113.dp,
                            addShare = {
                                CompactAddShareButton(
                                    collected = isInCollection,
                                    wishlisted = true,
                                    onCollectionToggle = {
                                        val wasInWishlist = repo.isWishlisted(spec.id)
                                        repo.toggleCollection(spec.id)
                                        repo.toggleWishlist(spec.id)
                                        if (wasInWishlist) {
                                            AchievementsRepository.award(XpSource.WISHLIST_HIT, familyTag = spec.id)
                                        }
                                    },
                                    onWishlistToggle = { repo.toggleWishlist(spec.id) },
                                    onShareToProfile = { shareToProfileSpec = spec },
                                    onShareToSocial = {
                                        scope.launch {
                                            val photo: android.graphics.Bitmap? =
                                                imageUrls.firstOrNull()?.let { url ->
                                                    ShareCardImage.loadDownsampled(context, Uri.parse(url))
                                                }
                                            ShareCardImage.share(
                                                context = context,
                                                title = spec.name,
                                                subtitle = spec.category + "  •  " + spec.rarity + "  •  On my wishlist",
                                                body = spec.tagline,
                                                accentHex = spec.colorHex,
                                                photoBitmap = photo,
                                                fileName = "rockscout_wishlist_${spec.id}",
                                            )
                                        }
                                    },
                                    accent = accent,
                                    size = 44.dp,
                                )
                            },
                            heart = {
                                CardHeart(
                                    active = isLiked,
                                    onToggle = { repo.toggleLike(spec.id) },
                                    accent = accent,
                                    size = 44.dp,
                                    likeCount = repo.specimenLikeCount(spec.id),
                                )
                            },
                            showCategory = true,
                            categoryLabel = shortCategoryLabel(spec.category),
                        )
                    }
                }
            }
        }
    }

    shareToProfileCapture?.let { cap ->
        val spec = SeedData.specimenById(cap.specimenId)
        ShareToProfileComposer(
            sourceType = "wishlist",
            title = cap.displayName(spec?.name ?: "Unknown specimen"),
            tagline = (spec?.category ?: "Field capture") + "  •  On my wishlist",
            imageUri = cap.imageUris.firstOrNull(),
            locationText = cap.customLocation,
            onDismiss = { shareToProfileCapture = null },
        )
    }

    shareToProfileSpec?.let { spec ->
        val imageUrls = SpecimenImages.urls[spec.id] ?: spec.imageUrls
        ShareToProfileComposer(
            sourceType = "wishlist",
            title = spec.name,
            tagline = spec.category + "  •  " + spec.rarity + "  •  On my wishlist",
            imageUri = imageUrls.firstOrNull(),
            locationText = spec.whereFound.firstOrNull() ?: "",
            onDismiss = { shareToProfileSpec = null },
        )
    }

    pendingDeleteCapture?.let { cap ->
        DeleteConfirmDialog(
            title = "Remove from wishlist?",
            message = "Remove this specimen from your wishlist? This action cannot be undone.",
            onConfirm = {
                repo.removeCapture(cap.id)
                pendingDeleteCapture = null
            },
            onDismiss = { pendingDeleteCapture = null },
        )
    }
}

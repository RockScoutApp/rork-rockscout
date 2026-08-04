package com.rork.rockscout.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.ImageEnhancerService
import com.rork.rockscout.data.ImageUtils
import com.rork.rockscout.data.SavedImage
import com.rork.rockscout.data.ScreenPdfExporter
import com.rork.rockscout.data.ScreenPdfItem
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.components.FullScreenImageViewer
import com.rork.rockscout.ui.components.LongPressableImage
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.ShareCardImage
import com.rork.rockscout.ui.components.DeleteConfirmDialog
import com.rork.rockscout.ui.components.CardHeart
import com.rork.rockscout.ui.components.CardPlusDropdown
import com.rork.rockscout.ui.components.CardAction
import com.rork.rockscout.ui.components.YooperliteHeart
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.rork.rockscout.ui.components.glowingBorder

@Composable
fun SavedImagesScreen(navController: NavController) {
    val repo = AppRepository.instance
    val savedImages by repo.savedImages.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var viewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerInitialPage by remember { mutableIntStateOf(0) }
    var pendingDeleteImage by remember { mutableStateOf<SavedImage?>(null) }
    var isExportingPdf by remember { mutableStateOf(false) }

    // Per-image enhancement state — tracks which image is being enhanced and its progress
    var enhancingImageId by remember { mutableStateOf<String?>(null) }
    var enhanceProgress by remember { mutableFloatStateOf(0f) }
    var enhanceTileText by remember { mutableStateOf("") }

    fun runEnhanceOnImage(image: SavedImage) {
        if (enhancingImageId != null) return
        enhancingImageId = image.id
        enhanceProgress = 0f
        enhanceTileText = "Preparing..."
        val originalUrl = image.url
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                ImageEnhancerService.enhance(context, Uri.parse(originalUrl)) { current, total ->
                    enhanceProgress = current.toFloat() / total.toFloat()
                    enhanceTileText = "Processing tile $current of $total"
                }
            }
            if (result != null) {
                // Store the original URL for undo, then update the image URL to the enhanced version
                repo.setEnhancedUrl(image.id, result.enhancedUrl, originalUrl)
                Toast.makeText(context, "Image enhanced 4x!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Enhancement failed. Try a different image.", Toast.LENGTH_SHORT).show()
            }
            enhancingImageId = null
        }
    }

    RockBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 16.dp, top = 52.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SculptedIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    onClick = { navController.popBackStack() },
                    accent = Aqua,
                    iconTint = Aqua,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "My Saved Images",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Aqua,
                    modifier = Modifier.weight(1f),
                )
                // Enhance pill button — opens the full enhancer screen
                EnhancePillButton(
                    onClick = { navController.navigate(Routes.IMAGE_ENHANCER) },
                    enabled = enhancingImageId == null,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${savedImages.size}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Citrine,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp),
                )
                SculptedIconButton(
                    icon = Icons.Filled.PictureAsPdf,
                    contentDescription = "Export PDF",
                    onClick = {
                        if (isExportingPdf) return@SculptedIconButton
                        isExportingPdf = true
                        scope.launch {
                            val items = savedImages.map { img ->
                                ScreenPdfItem(
                                    title = "Saved Image",
                                    subtitle = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(img.savedAt)),
                                    accentRgb = 0xFF2C6F9B.toInt(),
                                    imageUrl = img.url,
                                    fields = listOf(
                                        "Saved On" to SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault()).format(Date(img.savedAt)),
                                        "Source URL" to img.url.take(80),
                                    ).filter { it.second.isNotBlank() },
                                    description = if (img.liked) "★ Liked" else "",
                                )
                            }
                            ScreenPdfExporter.export(
                                context = context,
                                docTitle = "My Saved Images",
                                fileName = "RockScout_SavedImages",
                                items = items,
                            )
                            isExportingPdf = false
                        }
                    },
                    accent = Aqua,
                    iconTint = Aqua,
                    size = 36.dp,
                    shadowElevation = 3.dp,
                    enabled = !isExportingPdf && savedImages.isNotEmpty(),
                )
            }

            if (savedImages.isEmpty()) {
                EmptySavedImagesState(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(savedImages, key = { it.id }) { image ->
                        SavedImageCard(
                            image = image,
                            isEnhancing = enhancingImageId == image.id,
                            enhanceProgress = enhanceProgress,
                            enhanceTileText = enhanceTileText,
                            onClick = {
                                viewerUrls = savedImages.map { it.url }
                                viewerInitialPage = savedImages.indexOf(it)
                            },
                            onDelete = { pendingDeleteImage = it },
                            onShare = { url ->
                                scope.launch {
                                    val bitmap = ShareCardImage.loadDownsampled(
                                        context,
                                        Uri.parse(url),
                                    )
                                    ShareCardImage.share(
                                        context = context,
                                        title = "Saved Rock Specimen",
                                        subtitle = "Found with RockScout",
                                        accentHex = 0xFF2C6F9B,
                                        photoBitmap = bitmap,
                                        caption = "Check out this rock specimen I found on RockScout!",
                                    )
                                }
                            },
                            onEnhance = { runEnhanceOnImage(it) },
                            onUndo = {
                                if (repo.undoEnhancement(image.id)) {
                                    Toast.makeText(context, "Reverted to original", Toast.LENGTH_SHORT).show()
                                }
                            },
                        )
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

        pendingDeleteImage?.let { img ->
            DeleteConfirmDialog(
                title = "Delete saved image?",
                message = "Delete this saved image? This action cannot be undone.",
                onConfirm = {
                    repo.removeSavedImage(img.id)
                    pendingDeleteImage = null
                },
                onDismiss = { pendingDeleteImage = null },
            )
        }
    }
}

/**
 * Compact pill button with a sparkles icon and "Enhance" text.
 * Uses Citrine accent to stand out as a distinct AI action.
 */
@Composable
private fun EnhancePillButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (enabled) Citrine.copy(alpha = 0.15f) else Citrine.copy(alpha = 0.05f)
            )
            .glowingBorder(
                1.5.dp,
                Citrine.copy(alpha = if (enabled) 0.6f else 0.2f),
                RoundedCornerShape(20.dp),
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = "Enhance",
                tint = if (enabled) Citrine else Citrine.copy(alpha = 0.3f),
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Enhance",
                style = MaterialTheme.typography.labelMedium,
                color = if (enabled) Citrine else Citrine.copy(alpha = 0.3f),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun SavedImageCard(
    image: SavedImage,
    isEnhancing: Boolean = false,
    enhanceProgress: Float = 0f,
    enhanceTileText: String = "",
    onClick: (SavedImage) -> Unit,
    onDelete: (SavedImage) -> Unit,
    onShare: (String) -> Unit = {},
    onEnhance: (SavedImage) -> Unit = {},
    onUndo: () -> Unit = {},
) {
    val formatter = remember { SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault()) }
    val repo = AppRepository.instance
    val savedImages by repo.savedImages.collectAsStateWithLifecycle()
    val isLiked = savedImages.any { it.id == image.id && it.liked }
    val canUndo = image.originalUrl != null

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Slate800.copy(alpha = 0.75f))
            .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(18.dp))
            .clickable(enabled = !isEnhancing) { onClick(image) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Thumbnail with progress overlay when enhancing
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF1E1C16))
                .glowingBorder(1.dp, Color(0xFF1E1C16).copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            LongPressableImage(
                model = image.url,
                contentDescription = "Saved image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                allowSave = false,
            )

            // Progress bar overlay on the thumbnail during enhancement
            if (isEnhancing) {
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.80f))
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = enhanceTileText,
                            style = MaterialTheme.typography.labelSmall,
                            color = Citrine,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(2.dp))
                        val animatedProgress by animateFloatAsState(
                            targetValue = enhanceProgress,
                            animationSpec = tween(300),
                            label = "thumbProgress",
                        )
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .clip(RoundedCornerShape(1.5.dp)),
                            color = Citrine,
                            trackColor = Color.White.copy(alpha = 0.15f),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (canUndo) "Enhanced image" else "Saved image",
                style = MaterialTheme.typography.titleMedium,
                color = if (canUndo) Citrine else Color.White,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = formatter.format(Date(image.savedAt)),
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextMid,
            )
            // Undo button — only visible if the image has been enhanced
            if (canUndo && !isEnhancing) {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Aqua.copy(alpha = 0.12f))
                        .clickable { onUndo() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo enhancement",
                        tint = Aqua,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Undo",
                        style = MaterialTheme.typography.labelSmall,
                        color = Aqua,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
        CardHeart(
            active = isLiked,
            onToggle = { repo.toggleSavedImageLike(image.id) },
            accent = Aqua,
            size = 36.dp,
            likeCount = repo.savedImageLikeCount(image.id),
        )
        Spacer(Modifier.width(8.dp))
        CardPlusDropdown(
            actions = buildList {
                add(CardAction.Item(
                    label = "Enhance 4x",
                    icon = Icons.Filled.AutoAwesome,
                    iconTint = Citrine,
                    onClick = { onEnhance(image) },
                ))
                add(CardAction.Item(
                    label = "Share",
                    icon = Icons.Filled.Share,
                    iconTint = Aqua,
                    onClick = { onShare(image.url) },
                ))
                if (canUndo) {
                    add(CardAction.Item(
                        label = "Undo Enhance",
                        icon = Icons.AutoMirrored.Filled.Undo,
                        iconTint = Aqua,
                        onClick = { onUndo() },
                    ))
                }
                add(CardAction.Item(
                    label = "Delete",
                    icon = Icons.Filled.Delete,
                    iconTint = Color(0xFFE2574C),
                    onClick = { onDelete(image) },
                ))
            },
            accent = Aqua,
            size = 36.dp,
        )
    }
}

@Composable
private fun EmptySavedImagesState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.radialGradient(
                        listOf(Aqua.copy(alpha = 0.25f), Color.Transparent)
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Download,
                contentDescription = null,
                tint = Aqua.copy(alpha = 0.6f),
                modifier = Modifier.size(48.dp),
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = "No saved images yet",
            style = MaterialTheme.typography.titleLarge,
            color = TextHigh,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Long-press any photo in the app and tap Save photo to add it here.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
            textAlign = TextAlign.Center,
        )
    }
}

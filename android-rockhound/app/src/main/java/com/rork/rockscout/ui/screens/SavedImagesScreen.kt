package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
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
import kotlinx.coroutines.launch
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.SavedImage
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
                Text(
                    text = "${savedImages.size}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Citrine,
                    fontWeight = FontWeight.Bold,
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
                            onClick = {
                                viewerUrls = savedImages.map { it.url }
                                viewerInitialPage = savedImages.indexOf(it)
                            },
                            onDelete = { pendingDeleteImage = it },
                            onShare = { url ->
                                scope.launch {
                                    val bitmap = ShareCardImage.loadDownsampled(
                                        context,
                                        android.net.Uri.parse(url),
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

@Composable
private fun SavedImageCard(
    image: SavedImage,
    onClick: (SavedImage) -> Unit,
    onDelete: (SavedImage) -> Unit,
    onShare: (String) -> Unit = {},
) {
    val formatter = remember { SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault()) }
    val repo = AppRepository.instance
    val savedImages by repo.savedImages.collectAsStateWithLifecycle()
    val isLiked = savedImages.any { it.id == image.id && it.liked }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Slate800.copy(alpha = 0.75f))
            .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(18.dp))
            .clickable { onClick(image) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Saved image",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
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
            actions = listOf(
                CardAction.Item(
                    label = "Share",
                    icon = Icons.Filled.Share,
                    iconTint = Aqua,
                    onClick = { onShare(image.url) },
                ),
                CardAction.Item(
                    label = "Delete",
                    icon = Icons.Filled.Delete,
                    iconTint = Color(0xFFE2574C),
                    onClick = { onDelete(image) },
                ),
            ),
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

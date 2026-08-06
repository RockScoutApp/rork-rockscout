package com.rork.rockscout.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.DarkTextMid

/**
 * Image loader that survives navigation and transient network failures.
 *
 * Builds an explicit [ImageRequest] with disk+memory caching enabled, retries
 * automatically up to 3 times, and shows a shimmer while loading. Use anywhere
 * a plain `AsyncImage` is currently dropping images on navigation back.
 *
 * @param model image URL, URI, or resource to display
 * @param contentDescription accessibility label for the image
 * @param modifier layout modifier applied to the outer Box
 * @param contentScale how the image fills its bounds
 * @param placeholderIcon icon shown behind/while the image loads (optional)
 * @param placeholderIconTint color for the placeholder icon
 * @param fullResolution when true, requests the original image size instead of
 *   a sampled size; useful for zoom/lightbox views
 */
@Composable
fun PersistentImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderIcon: ImageVector? = null,
    placeholderIconTint: Color = Aqua,
    fullResolution: Boolean = false,
) {
    val context = LocalContext.current

    // Retry counter — bumped automatically when the painter enters the Error
    // state, which forces Coil to re-fetch (up to 3 attempts). This handles
    // transient network failures where the image otherwise stays blank after
    // navigation back.
    var retryCount by remember(model) { mutableIntStateOf(0) }
    val request = remember(model, retryCount) {
        ImageRequest.Builder(context)
            .data(model)
            .crossfade(true)
            .apply { if (fullResolution) size(Size.ORIGINAL) }
            .diskCacheKey(model?.toString())
            .memoryCacheKey(model?.toString())
            .build()
    }
    val painter = rememberAsyncImagePainter(request)
    val state = painter.state

    LaunchedEffect(state) {
        if (state is AsyncImagePainter.State.Error && retryCount < 3) {
            kotlinx.coroutines.delay(500L * (retryCount + 1))
            retryCount++
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale,
        )
        when (state) {
            is AsyncImagePainter.State.Loading -> {
                ShimmerPlaceholder(modifier = Modifier.fillMaxSize())
            }
            is AsyncImagePainter.State.Error -> {
                Icon(
                    imageVector = placeholderIcon ?: Icons.Default.Image,
                    contentDescription = "Image failed to load",
                    tint = placeholderIconTint.copy(alpha = 0.5f),
                    modifier = Modifier.size(40.dp),
                )
            }
            else -> Unit
        }
        if (placeholderIcon != null && state !is AsyncImagePainter.State.Loading) {
            Icon(
                imageVector = placeholderIcon,
                contentDescription = null,
                tint = placeholderIconTint.copy(alpha = 0.35f),
                modifier = Modifier.size(40.dp),
            )
        }
    }
}

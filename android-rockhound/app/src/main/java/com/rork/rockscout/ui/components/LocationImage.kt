package com.rork.rockscout.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.rork.rockscout.data.LocationImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Location hero image with three-tier caching:
 *
 * 1. **URL resolution cache** — [LocationImageRepository] resolves the
 *    Wikimedia Commons URL once per location name (in-memory + persisted),
 *    so repeated card renders never re-fetch the backend endpoint.
 * 2. **Coil bitmap cache** — once the URL is resolved, Coil's disk + memory
 *    cache loads the actual image bytes instantly on repeat views, even
 *    offline. Cache keys are explicit so navigation back never re-downloads.
 * 3. **Graceful fallback** — if no Commons photo exists (or the network
 *    fails), the [fallback] composable is shown instead. A sentinel is
 *    cached so the fallback is instant on subsequent renders.
 *
 * Use on location list cards (thumbnail), LocationDetailScreen (hero),
 * and MuseumCard (thumbnail).
 *
 * @param name the location or museum name to search Commons for
 * @param region optional region context to improve search quality
 * @param modifier layout modifier
 * @param contentScale how the image fills its bounds
 * @param fallback composable shown when no image is available or loading
 * @param fullResolution when true, requests the original image size
 */
@Composable
fun LocationImage(
    name: String,
    region: String? = null,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    fallback: @Composable () -> Unit = { DefaultLocationFallback() },
    fullResolution: Boolean = false,
) {
    val context = LocalContext.current

    // Phase 1: resolve the Commons URL (cached in-memory / persisted)
    var resolvedUrl by remember(name, region) { mutableStateOf<String?>(null) }
    var resolutionComplete by remember(name, region) { mutableStateOf(false) }

    LaunchedEffect(name, region) {
        resolvedUrl = withContext(Dispatchers.IO) {
            LocationImageRepository.resolveImageUrl(name, region)
        }
        resolutionComplete = true
    }

    // Phase 2: load the bitmap via Coil (cached on disk + in memory)
    val painter = rememberAsyncImagePainter(
        model = resolvedUrl?.let { url ->
            ImageRequest.Builder(context)
                .data(url)
                .crossfade(true)
                .apply { if (fullResolution) size(Size.ORIGINAL) }
                .diskCacheKey(url)
                .memoryCacheKey(url)
                .build()
        },
    )
    val painterState = painter.state

    Crossfade(
        targetState = when {
            !resolutionComplete -> LoadState.Resolving
            resolvedUrl == null -> LoadState.NoPhoto
            painterState is AsyncImagePainter.State.Loading -> LoadState.Loading
            painterState is AsyncImagePainter.State.Error -> LoadState.Error
            painterState is AsyncImagePainter.State.Success -> LoadState.Success
            else -> LoadState.Loading
        },
        modifier = modifier,
        label = "location-image",
    ) { state ->
        when (state) {
            LoadState.Resolving, LoadState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    fallback()
                }
            }
            LoadState.Success -> {
                Image(
                    painter = painter,
                    contentDescription = name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                )
            }
            LoadState.NoPhoto, LoadState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    fallback()
                }
            }
        }
    }
}

private enum class LoadState {
    Resolving, Loading, Success, NoPhoto, Error
}

/** Default fallback: a muted broken-image icon. Override with [fallback]. */
@Composable
private fun DefaultLocationFallback() {
    Icon(
        imageVector = Icons.Default.BrokenImage,
        contentDescription = null,
        tint = Color.White.copy(alpha = 0.3f),
        modifier = Modifier.size(28.dp),
    )
}

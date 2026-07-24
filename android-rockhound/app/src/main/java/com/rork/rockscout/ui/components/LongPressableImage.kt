package com.rork.rockscout.ui.components

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.GallerySaver
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.TextHigh
import java.io.IOException
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Wraps an image so a long-press (hard press) reveals a "Save photo" action.
 * Saving stores the image in the device gallery and also adds it to the
 * in-app "My Saved Images" collection so it shows on the home tile.
 *
 * Short taps can optionally trigger [onClick] (e.g., tap to enlarge). When
 * [onClick] is null, short taps pass through untouched.
 *
 * @param overlay optional visual content drawn on top of the image. It does not
 *                intercept pointer input — the long-press still works through it.
 */
@Composable
fun LongPressableImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderIcon: ImageVector? = null,
    placeholderIconTint: Color = Aqua,
    allowSave: Boolean = true,
    onClick: (() -> Unit)? = null,
    overlay: @Composable () -> Unit = {},
    fullResolution: Boolean = false,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showSaveDialog by remember { mutableStateOf(false) }
    var saveMessage by remember { mutableStateOf<String?>(null) }
    var showToast by remember { mutableStateOf(false) }

    // Retry counter — bumped automatically when the painter enters the Error
    // state, which forces Coil to re-fetch (up to 3 attempts). This handles
    // transient network failures where the thumbnail otherwise stays blank.
    var retryCount by remember(model) { mutableIntStateOf(0) }
    val request = remember(model, retryCount) {
        ImageRequest.Builder(context)
            .data(model)
            .crossfade(true)
            .apply { if (fullResolution) size(Size.ORIGINAL) }
            .build()
    }
    val painter = rememberAsyncImagePainter(request)
    val state = painter.state

    // Auto-retry on error (up to 3 times with a short delay)
    LaunchedEffect(state) {
        if (state is AsyncImagePainter.State.Error && retryCount < 3) {
            kotlinx.coroutines.delay(500L * (retryCount + 1))
            retryCount++
        }
    }

    Box(
        modifier = modifier
            .then(
                if (allowSave || onClick != null) {
                    Modifier.pointerInput(model, onClick) {
                        detectTapGestures(
                            onTap = { onClick?.invoke() },
                            onLongPress = { showSaveDialog = true }
                        )
                    }
                } else Modifier
            ),
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.matchParentSize(),
            contentScale = contentScale,
        )
        when (state) {
            is AsyncImagePainter.State.Loading -> {
                ShimmerPlaceholder(modifier = Modifier.matchParentSize())
            }
            is AsyncImagePainter.State.Error -> {
                Box(
                    modifier = Modifier.matchParentSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = placeholderIcon ?: Icons.Default.Image,
                        contentDescription = "Image failed to load",
                        tint = placeholderIconTint.copy(alpha = 0.5f),
                        modifier = Modifier.size(40.dp),
                    )
                }
            }
            else -> Unit
        }
        overlay()
        if (placeholderIcon != null && state !is AsyncImagePainter.State.Loading) {
            Box(
                modifier = Modifier.matchParentSize(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = placeholderIcon,
                    contentDescription = null,
                    tint = placeholderIconTint.copy(alpha = 0.35f),
                    modifier = Modifier.size(40.dp),
                )
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Text(
                    text = "Save photo?",
                    color = Aqua,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "This will save the image to your gallery and add it to My Saved Images.",
                    color = TextHigh,
                )
            },
            confirmButton = {
                SculptedButton(
                    text = "Save photo",
                    onClick = {
                        showSaveDialog = false
                        scope.launch {
                            val result = saveImageToGalleryAndCollection(context, model)
                            saveMessage = result
                            showToast = true
                        }
                    },
                    accent = Citrine,
                    containerColor = Citrine,
                    textColor = Ink,
                    shape = RoundedCornerShape(12.dp),
                    icon = Icons.Filled.Download,
                )
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel", color = DarkTextMid)
                }
            },
            containerColor = Slate900,
        )
    }

    if (showToast) {
        AlertDialog(
            onDismissRequest = { showToast = false },
            title = null,
            text = {
                Text(
                    text = saveMessage ?: "Saved",
                    color = TextHigh,
                )
            },
            confirmButton = {
                TextButton(onClick = { showToast = false }) {
                    Text("OK", color = Aqua)
                }
            },
            containerColor = Slate900,
        )
    }
}

/**
 * Downloads [imageModel] (currently supports http/https URLs), saves it to the
 * RockScout Captures gallery album, and records it in the app's saved images list.
 *
 * Uses Coil's disk/memory cache first — if the image was already loaded on screen,
 * it's decoded from cache instantly with zero network traffic. Falls back to a
 * raw HTTP download only if the image isn't in Coil's cache (e.g. user saved an
 * image they haven't viewed yet, which shouldn't normally happen since they
 * long-pressed a visible image).
 *
 * Returns a user-facing message.
 */
suspend fun saveImageToGalleryAndCollection(context: android.content.Context, imageModel: Any?): String {
    val urlString = imageModel as? String ?: return "Could not save this image."

    return withContext(Dispatchers.IO) {
        try {
            val bitmap: Bitmap? = if (urlString.startsWith("http://") || urlString.startsWith("https://")) {
                // Try Coil's disk cache first — the image is likely already there
                // from being displayed on screen. This avoids a redundant network
                // download on low-signal connections.
                val loader = coil3.SingletonImageLoader.get(context)
                try {
                    val request = coil3.request.ImageRequest.Builder(context)
                        .data(urlString)
                        .build()
                    val result = loader.execute(request)
                    (result.image as? coil3.BitmapImage)?.bitmap
                } catch (_: Exception) { null }
                    ?: downloadBitmap(urlString)
            } else {
                // Local file:// or content:// URI — decode directly from disk
                val uri = Uri.parse(urlString)
                val resolver = context.contentResolver
                try {
                    val input = resolver.openInputStream(uri)
                    input?.use { BitmapFactory.decodeStream(it) }
                } catch (_: Exception) { null }
            }
            if (bitmap == null) return@withContext "Could not load this image."
            val title = "RockScout_${System.currentTimeMillis()}"
            val uri = GallerySaver.saveBitmap(context.contentResolver, bitmap, title)
                ?: return@withContext "Could not save to gallery."
            AppRepository.instance.addSavedImage(urlString, uri.toString())
            "Saved to gallery and My Saved Images"
        } catch (e: IOException) {
            "Could not save image: ${e.message}"
        } catch (e: Exception) {
            "Could not save image."
        }
    }
}

/** Animated gradient placeholder that indicates an image is loading.
 *  Draws a subtle dark band that sweeps across the image area so the user
 *  always sees feedback instead of a blank box. */
@Composable
internal fun ShimmerPlaceholder(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer",
    )
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1A1812),
                    Color(0xFF2C2720),
                    Color(0xFF1A1812),
                ),
                start = Offset(translate * 400f - 400f, 0f),
                end = Offset(translate * 400f, 0f),
            )
        )
    )
}

private fun downloadBitmap(urlString: String): Bitmap? {
    return try {
        val conn = (URL(urlString).openConnection() as java.net.HttpURLConnection).apply {
            connectTimeout = 10_000
            readTimeout = 15_000
            instanceFollowRedirects = true
        }
        conn.inputStream.use { stream ->
            // Downsample so a huge source image can't OOM the save flow.
            com.rork.rockscout.data.ImageUtils.decodeSampledBitmap(stream)
        }
    } catch (_: Exception) {
        null
    }
}

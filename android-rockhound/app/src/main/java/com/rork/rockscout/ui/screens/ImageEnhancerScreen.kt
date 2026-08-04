package com.rork.rockscout.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.ImageEnhancerService
import com.rork.rockscout.data.ImageUtils
import com.rork.rockscout.ui.components.ShareCardImage
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Cyan
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.TextHigh
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Full-screen AI image enhancer tool. Lets the user pick an image from gallery
 * or camera, runs the on-device Real-ESRGAN model to upscale 4x with reconstructed
 * detail, shows a before/after comparison, and lets the user save or share the result.
 *
 * When [initialImageUri] is provided (e.g. from a Saved Images card action), the
 * enhancer skips the picker and starts with that image loaded.
 */
@Composable
fun ImageEnhancerScreen(
    navController: NavController,
    initialImageUri: String? = null,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var sourceUri by remember { mutableStateOf<Uri?>(initialImageUri?.let { Uri.parse(it) }) }
    var sourceBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var enhancedResult by remember { mutableStateOf<ImageEnhancerService.EnhanceResult?>(null) }
    var isEnhancing by remember { mutableStateOf(false) }
    var enhanceProgress by remember { mutableFloatStateOf(0f) }
    var tileProgressText by remember { mutableStateOf("") }
    var isSaved by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Camera launcher
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success && cameraUri != null) {
            sourceUri = cameraUri
            enhancedResult = null
            isSaved = false
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri != null) {
            sourceUri = uri
            enhancedResult = null
            isSaved = false
        }
    }

    // Load bitmap when sourceUri changes
    LaunchedEffect(sourceUri) {
        if (sourceUri != null) {
            sourceBitmap = withContext(Dispatchers.IO) {
                ImageUtils.decodeSampledBitmap(context, sourceUri!!, 640)
            }
        } else {
            sourceBitmap = null
        }
    }

    // Release the interpreter when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            ImageEnhancerService.release()
        }
    }

    fun startCamera() {
        val photoFile = File(context.cacheDir, "photos/${System.currentTimeMillis()}.jpg")
        photoFile.parentFile?.mkdirs()
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
        cameraUri = uri
        cameraLauncher.launch(uri)
    }

    fun startGallery() {
        galleryLauncher.launch("image/*")
    }

    fun runEnhance() {
        val uri = sourceUri ?: return
        isEnhancing = true
        enhanceProgress = 0f
        tileProgressText = "Preparing..."
        errorMessage = null
        enhancedResult = null
        isSaved = false

        scope.launch {
            val result = withContext(Dispatchers.IO) {
                ImageEnhancerService.enhance(context, uri) { current, total ->
                    enhanceProgress = current.toFloat() / total.toFloat()
                    tileProgressText = "Processing tile $current of $total"
                }
            }
            isEnhancing = false
            if (result != null) {
                enhancedResult = result
            } else {
                errorMessage = "Enhancement failed. The image may be too large or in an unsupported format."
            }
        }
    }

    fun saveResult() {
        val result = enhancedResult ?: return
        val repo = AppRepository.instance
        // Save the enhanced image as a new saved image, with originalUrl for undo
        val originalUrl = sourceUri?.toString() ?: ""
        // Copy the original to internal storage if it's a content URI
        val persistentOriginalUrl = if (originalUrl.startsWith("content:")) {
            ImageUtils.copyUriToInternalStorage(context, Uri.parse(originalUrl), "enhanced_originals")
                ?: originalUrl
        } else {
            originalUrl
        }
        repo.addSavedImage(result.enhancedUrl)
        // Set the originalUrl on the newly added image for undo
        val added = repo.savedImages.value.firstOrNull { it.url == result.enhancedUrl }
        if (added != null) {
            repo.setEnhancedUrl(added.id, result.enhancedUrl, persistentOriginalUrl)
        }
        isSaved = true
        Toast.makeText(context, "Saved to My Saved Images", Toast.LENGTH_SHORT).show()
    }

    fun shareResult() {
        val result = enhancedResult ?: return
        scope.launch {
            val bitmap = result.enhancedBitmap
            ShareCardImage.share(
                context = context,
                title = "Enhanced Image",
                subtitle = "AI-upscaled 4x with RockScout",
                accentHex = 0xFFE8A33D,
                photoBitmap = bitmap,
                caption = "Check out this AI-enhanced image from RockScout!",
            )
        }
    }

    RockBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
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
                    text = "AI Image Enhancer",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Aqua,
                    modifier = Modifier.weight(1f),
                )
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .navigationBarsPadding(),
            ) {
                if (sourceBitmap == null && sourceUri == null) {
                    // No image selected — show picker options
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "Select an image to enhance",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextHigh,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "The AI model runs entirely on your device — no internet needed, no cloud credits.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(32.dp))
                    // Picker buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        SculptedOutlinedButton(
                            text = "Gallery",
                            icon = Icons.Filled.PhotoLibrary,
                            onClick = { startGallery() },
                            accent = Aqua,
                            textColor = Aqua,
                            modifier = Modifier.weight(1f),
                        )
                        SculptedOutlinedButton(
                            text = "Camera",
                            icon = Icons.Filled.CameraAlt,
                            onClick = { startCamera() },
                            accent = Aqua,
                            textColor = Aqua,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(Modifier.weight(1f))
                } else {
                    // Image preview area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Slate800.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (enhancedResult != null) {
                            // Show enhanced result
                            Image(
                                bitmap = enhancedResult!!.enhancedBitmap.asImageBitmap(),
                                contentDescription = "Enhanced image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit,
                            )
                        } else if (sourceBitmap != null) {
                            // Show original
                            Image(
                                bitmap = sourceBitmap!!.asImageBitmap(),
                                contentDescription = "Original image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit,
                            )
                        }

                        // Progress bar overlay
                        if (isEnhancing) {
                            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Black.copy(alpha = 0.75f))
                                        .padding(16.dp),
                                ) {
                                    Text(
                                        text = tileProgressText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Citrine,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    val animatedProgress by animateFloatAsState(
                                        targetValue = enhanceProgress,
                                        animationSpec = tween(300),
                                        label = "enhanceProgress",
                                    )
                                    LinearProgressIndicator(
                                        progress = { animatedProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = Citrine,
                                        trackColor = Color.White.copy(alpha = 0.15f),
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Dimension info
                    if (enhancedResult != null) {
                        val r = enhancedResult!!
                        Text(
                            text = "${r.originalWidth}x${r.originalHeight} → ${r.enhancedWidth}x${r.enhancedHeight}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextMid,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    // Action buttons
                    if (enhancedResult != null) {
                        // Result actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            if (isSaved) {
                                SculptedOutlinedButton(
                                    text = "Saved",
                                    icon = Icons.Filled.Save,
                                    onClick = { },
                                    accent = Cyan,
                                    textColor = Cyan,
                                    enabled = false,
                                    modifier = Modifier.weight(1f),
                                )
                            } else {
                                SculptedButton(
                                    text = "Save",
                                    icon = Icons.Filled.Save,
                                    onClick = { saveResult() },
                                    accent = Citrine,
                                    containerColor = Citrine,
                                    textColor = Color(0xFF1C1A14),
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            SculptedOutlinedButton(
                                text = "Share",
                                icon = Icons.Filled.Share,
                                onClick = { shareResult() },
                                accent = Aqua,
                                textColor = Aqua,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        SculptedOutlinedButton(
                            text = "Enhance Another",
                            icon = Icons.Filled.PhotoLibrary,
                            onClick = {
                                sourceUri = null
                                sourceBitmap = null
                                enhancedResult = null
                                isSaved = false
                            },
                            accent = DarkTextMid,
                            textColor = DarkTextMid,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else if (!isEnhancing) {
                        // Pre-enhance actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            SculptedOutlinedButton(
                                text = "New Image",
                                icon = Icons.Filled.PhotoLibrary,
                                onClick = {
                                    sourceUri = null
                                    sourceBitmap = null
                                },
                                accent = DarkTextMid,
                                textColor = DarkTextMid,
                                modifier = Modifier.weight(1f),
                            )
                            SculptedButton(
                                text = "Enhance 4x",
                                icon = Icons.Filled.AutoAwesome,
                                onClick = { runEnhance() },
                                accent = Citrine,
                                containerColor = Citrine,
                                textColor = Color(0xFF1C1A14),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }

                // Error message
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE2574C),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    )
                }
            }
        }
    }
}

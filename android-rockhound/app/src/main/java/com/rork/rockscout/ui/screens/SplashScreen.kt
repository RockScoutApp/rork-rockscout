package com.rork.rockscout.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.rork.rockscout.ui.theme.Obsidian
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MS = 3000
/** Splash icon URL. Public so the offline bulk-download registry can include it. */
const val SPLASH_ICON_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/dc6abf00-a66e-4a19-b4a1-ded86a993b3c.png"

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(800))
        delay(SPLASH_DURATION_MS.toLong())
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A1812), Obsidian, Color(0xFF0F0E0A))
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        // Full-bleed high-res splash image — fills the entire screen edge-to-edge.
        // No text overlay; the image itself contains the branding.
        AsyncImage(
            model = SPLASH_ICON_URL,
            contentDescription = "RockScout splash",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha.value),
        )
    }
}

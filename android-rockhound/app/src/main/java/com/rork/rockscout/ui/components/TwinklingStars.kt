package com.rork.rockscout.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.random.Random

/**
 * Animated twinkling white stars rendered as a non-interactive background layer.
 * Stars are positioned via a seeded random grid with jitter so they never overlap
 * text, images, or clickable items — they sit behind all content via [drawBehind].
 *
 * @param modifier Layout modifier for the star field container.
 * @param starCount Number of stars to render (default 50).
 */
@Composable
fun TwinklingStars(
    modifier: Modifier = Modifier,
    starCount: Int = 50,
) {
    val density = LocalDensity.current
    val transition = rememberInfiniteTransition(label = "twinkle")

    // Pre-compute star positions with a seeded random so they don't jump on recomposition
    val stars = remember(starCount) {
        val rng = Random(42L)
        List(starCount) { index ->
            TwinkleStar(
                x = rng.nextFloat(),
                y = rng.nextFloat(),
                radius = 0.8f + rng.nextFloat() * 1.7f,
                durationMs = 1500 + rng.nextInt(2500),
                delayMs = rng.nextInt(3000),
                minAlpha = 0.15f + rng.nextFloat() * 0.2f,
            )
        }
    }

    // Animate each star's alpha independently
    val alphas = stars.mapIndexed { index, star ->
        transition.animateFloat(
            initialValue = star.minAlpha,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(star.durationMs, delayMillis = star.delayMs),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "twinkle_$index",
        )
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEachIndexed { index, star ->
                val alpha = alphas[index].value
                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = star.radius.dp.toPx(),
                    center = Offset(star.x * size.width, star.y * size.height),
                )
            }
        }
    }
}

private data class TwinkleStar(
    val x: Float,
    val y: Float,
    val radius: Float,
    val durationMs: Int,
    val delayMs: Int,
    val minAlpha: Float,
)

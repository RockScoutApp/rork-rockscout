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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Animated twinkling starbursts rendered as a non-interactive background layer.
 * Each sparkle is a 4-point starburst (two crossed diamonds) with a soft halo,
 * pulsing in and out. Stars are positioned via a seeded random grid with jitter
 * so they never overlap text, images, or clickable items — they sit behind all
 * content via [drawBehind].
 *
 * @param modifier Layout modifier for the star field container.
 * @param starCount Number of stars to render (default 50).
 * @param maxAlpha Maximum alpha a star reaches at peak twinkle (default 1.0).
 *                 Lower this (e.g. 0.55f) for subtle ambient sparkles over a
 *                 textured background.
 */
@Composable
fun TwinklingStars(
    modifier: Modifier = Modifier,
    starCount: Int = 50,
    maxAlpha: Float = 1.0f,
) {
    val density = LocalDensity.current
    val transition = rememberInfiniteTransition(label = "twinkle")

    // Pre-compute star positions with a seeded random so they don't jump on recomposition.
    // Frequency roughly doubled: duration range 800–2200ms (was 1500–4000ms).
    val stars = remember(starCount) {
        val rng = Random(42L)
        List(starCount) { index ->
            TwinkleStar(
                x = rng.nextFloat(),
                y = rng.nextFloat(),
                radius = 0.9f + rng.nextFloat() * 1.8f,
                durationMs = 800 + rng.nextInt(1400),
                delayMs = rng.nextInt(2200),
                minAlpha = 0.10f + rng.nextFloat() * 0.18f,
                rotationDeg = rng.nextFloat() * 90f,
            )
        }
    }

    // Animate each star's alpha independently.
    val alphas = stars.mapIndexed { index, star ->
        transition.animateFloat(
            initialValue = star.minAlpha * maxAlpha,
            targetValue = maxAlpha,
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
                val center = Offset(star.x * size.width, star.y * size.height)
                val r = star.radius.dp.toPx()
                val haloColor = Color.White.copy(alpha = alpha * 0.28f)
                val burstColor = Color.White.copy(alpha = alpha)

                // Soft halo behind the starburst for a glow effect.
                drawCircle(
                    color = haloColor,
                    radius = r * 2.2f,
                    center = center,
                )

                // 4-point starburst: two crossed diamonds forming a sparkle cross.
                val starPath = starburstPath(center, r, star.rotationDeg)
                drawPath(
                    path = starPath,
                    color = burstColor,
                )
            }
        }
    }
}

/**
 * Builds a 4-point starburst [Path] centered at [center] with half-diagonal [r].
 * The vertical and horizontal spikes extend to [r], while the diagonal waists
 * pinch in to ~[r] * 0.32, creating a classic sparkle silhouette. Rotated by
 * [rotationDeg] so each starburst sits at a slightly different angle.
 */
private fun starburstPath(center: Offset, r: Float, rotationDeg: Float): Path {
    val waist = r * 0.32f
    val rad = Math.toRadians(rotationDeg.toDouble()).toFloat()
    val cosR = cos(rad)
    val sinR = sin(rad)
    // Rotate a point around the center by the rotation angle.
    fun rot(x: Float, y: Float): Offset {
        val dx = x - center.x
        val dy = y - center.y
        return Offset(center.x + dx * cosR - dy * sinR, center.y + dx * sinR + dy * cosR)
    }
    val top = rot(center.x, center.y - r)
    val right = rot(center.x + r, center.y)
    val bottom = rot(center.x, center.y + r)
    val left = rot(center.x - r, center.y)
    val tr = rot(center.x + waist, center.y - waist)
    val br = rot(center.x + waist, center.y + waist)
    val bl = rot(center.x - waist, center.y + waist)
    val tl = rot(center.x - waist, center.y - waist)
    return Path().apply {
        moveTo(top.x, top.y)
        lineTo(tr.x, tr.y)
        lineTo(right.x, right.y)
        lineTo(br.x, br.y)
        lineTo(bottom.x, bottom.y)
        lineTo(bl.x, bl.y)
        lineTo(left.x, left.y)
        lineTo(tl.x, tl.y)
        close()
    }
}

private data class TwinkleStar(
    val x: Float,
    val y: Float,
    val radius: Float,
    val durationMs: Int,
    val delayMs: Int,
    val minAlpha: Float,
    val rotationDeg: Float,
)

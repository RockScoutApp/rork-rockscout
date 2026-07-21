package com.rork.rockscout.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A heart icon styled like a UV-glowing Yooperlite — the fluorescent sodalite
 * syenite from Lake Superior that glows fiery orange under UV light.
 *
 * When [active] the heart gets an orange glow halo, a subtle pulsing animation,
 * and a warm radial gradient behind it. When inactive it shows a dim outline.
 *
 * @param active whether the heart is in the "loved"/"wishlisted" state
 * @param contentDescription accessibility description
 * @param size icon size in dp (default 22)
 * @param modifier additional modifier
 * @param activeTint color used when the heart is active (defaults to glowing orange)
 * @param inactiveTint color used when the heart is inactive (defaults to dim stone)
 * @param unclippedGlow when true, the UV glow halo is drawn in a layer that
 *   overflows the parent button's clipped circle so the glow is visible around
 *   the icon instead of being clipped away. Use this when the heart sits inside
 *   a clipped circular button frame.
 */
@Composable
fun YooperliteHeart(
    active: Boolean,
    contentDescription: String,
    size: Dp = 22.dp,
    modifier: Modifier = Modifier,
    activeTint: Color = Color(0xFFFF7A1A),
    inactiveTint: Color = Color(0xFFC9C2B0),
    unclippedGlow: Boolean = false,
) {
    if (active) {
        // Pulsing glow animation — mimics UV fluorescence shimmer
        val transition = rememberInfiniteTransition(label = "yooperlite-glow")
        val glowAlpha by transition.animateFloat(
            initialValue = 0.55f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(1400),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "glow-alpha",
        )
        val pulseScale by transition.animateFloat(
            initialValue = 0.92f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(1400),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "pulse-scale",
        )

        if (unclippedGlow) {
            // Unclipped mode: render the heart at the parent's size, then draw
            // the UV glow halo as an overflowing radial gradient + pulsing ring
            // in a drawWithContent layer that is NOT clipped to the button.
            Box(
                modifier = modifier
                    .size(size)
                    .drawWithContent {
                        // Draw the glow halo first so the icon sits on top of it.
                        val glowRadius = size.toPx() * 1.6f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF8C2A).copy(alpha = glowAlpha * 0.55f),
                                    Color(0xFFFF6B0A).copy(alpha = glowAlpha * 0.28f),
                                    Color.Transparent,
                                ),
                                center = Offset(size.toPx() / 2f, size.toPx() / 2f),
                                radius = glowRadius,
                            ),
                            radius = glowRadius,
                            center = Offset(size.toPx() / 2f, size.toPx() / 2f),
                        )
                        drawContent()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(size + 4.dp)
                        .scale(pulseScale)
                        .glowingBorder(3.dp, Color(0xFFFF7A1A).copy(alpha = glowAlpha * 0.65f), CircleShape),
                )
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = contentDescription,
                    tint = activeTint,
                    modifier = Modifier
                        .size(size)
                        .graphicsLayer { shadowElevation = 4f },
                )
            }
        } else {
            Box(
                modifier = modifier
                    .size(size + 8.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF8C2A).copy(alpha = glowAlpha * 0.45f),
                                Color(0xFFFF6B0A).copy(alpha = glowAlpha * 0.20f),
                                Color.Transparent,
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(size + 4.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .glowingBorder(3.dp, Color(0xFFFF7A1A).copy(alpha = glowAlpha * 0.5f), CircleShape),
                )
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = contentDescription,
                    tint = activeTint,
                    modifier = Modifier
                        .size(size)
                        .graphicsLayer { shadowElevation = 4f },
                )
            }
        }
    } else {
        // Inactive — dim outline heart, no glow
        Icon(
            imageVector = Icons.Filled.FavoriteBorder,
            contentDescription = contentDescription,
            tint = inactiveTint,
            modifier = modifier.size(size),
        )
    }
}

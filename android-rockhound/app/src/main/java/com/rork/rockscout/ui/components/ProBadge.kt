package com.rork.rockscout.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rork.rockscout.ui.theme.Ink

/** Gold gradient colors for the Pro badge. */
private val GoldLight = Color(0xFFFFD700)
private val GoldMid = Color(0xFFFFC107)
private val GoldDeep = Color(0xFFB8860B)
private val GoldShimmer = Color(0xFFFFF8DC)

/**
 * A bold, shiny gold "PRO" badge that stands out from everything else.
 * Features an animated shimmer sweep across a gold gradient pill.
 * Shows a star icon + "PRO" text.
 */
@Composable
fun ProBadge(
    modifier: Modifier = Modifier,
    label: String = "PRO",
) {
    val transition = rememberInfiniteTransition(label = "proShimmer")
    val shimmerOffset by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerSweep",
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    listOf(GoldDeep, GoldMid, GoldLight, GoldMid, GoldDeep),
                )
            )
            .drawWithCache {
                val sweepWidth = size.width
                val sweep = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        GoldShimmer.copy(alpha = 0.45f),
                        Color.Transparent,
                    ),
                    start = Offset(shimmerOffset * sweepWidth, 0f),
                    end = Offset(shimmerOffset * sweepWidth + sweepWidth * 0.5f, size.height),
                )
                onDrawWithContent {
                    drawContent()
                    drawRect(sweep)
                }
            }
            .padding(horizontal = 8.dp, vertical = 1.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = Ink,
                modifier = Modifier.size(12.dp),
            )
            Spacer(Modifier.width(3.dp))
            Text(
                text = label,
                color = Ink,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
            )
        }
    }
}

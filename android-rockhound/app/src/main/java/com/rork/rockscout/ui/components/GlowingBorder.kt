package com.rork.rockscout.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A drop-in replacement for [Modifier.border] that adds a soft crystalline
 * inner glow in the border's own color — the same luminous look the
 * achievement tiles have.
 *
 * Draws a faint radial gradient (brightest near edges, fading to center)
 * plus 2 wider, lower-alpha border strokes to create a fuzzy glow halo,
 * then the actual border at its original color/width on top.
 *
 * Static (no animation) — safe for 280+ simultaneous uses.
 */
fun Modifier.glowingBorder(
    border: BorderStroke,
    shape: Shape,
): Modifier {
    val color = (border.brush as? SolidColor)?.value ?: Color.White
    return glowingBorderImpl(border.width, color, shape, border.brush)
}

/** Convenience overload matching the most common `.border(width, color, shape)` call site. */
fun Modifier.glowingBorder(
    width: Dp,
    color: Color,
    shape: Shape,
): Modifier = glowingBorderImpl(width, color, shape, SolidColor(color))

private fun Modifier.glowingBorderImpl(
    width: Dp,
    color: Color,
    shape: Shape,
    actualBrush: Brush,
): Modifier =
    this
        // 1 — Inner radial gradient glow (brightest at edges, fading to center)
        .drawWithContent {
            drawContent()
            val cx = size.width / 2f
            val cy = size.height / 2f
            val maxDim = maxOf(size.width, size.height)
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        color.copy(alpha = color.alpha * 0.06f),
                        color.copy(alpha = color.alpha * 0.18f),
                    ),
                    center = Offset(cx, cy),
                    radius = maxDim * 0.72f,
                ),
            )
        }
        // 2 — Fuzzy glow halo: 2 wider, lower-alpha border strokes
        .border(width + 3.dp, color.copy(alpha = color.alpha * 0.08f), shape)
        .border(width + 1.5.dp, color.copy(alpha = color.alpha * 0.16f), shape)
        // 3 — The actual border at original width and brush
        .border(BorderStroke(width, actualBrush), shape)

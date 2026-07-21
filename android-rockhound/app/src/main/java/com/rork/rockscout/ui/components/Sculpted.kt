package com.rork.rockscout.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rork.rockscout.ui.theme.Citrine

/**
 * Sculpted 3D surface modifier — adds carved-stone bevels, tinted outer
 * shadows, and a press-down sink animation to any composable.
 *
 * - **Raised state:** light highlight along top/left edges, dark shadow
 *   along bottom/right edges, and a soft outer drop shadow — the element
 *   appears to float above the surface like a raised stone slab.
 * - **Pressed state:** bevels swap (dark top, light bottom), the element
 *   scales down ~4% and the shadow shrinks — feels like a physical
 *   button being depressed into the surface.
 *
 * Apply this modifier **before** `.clip()`, `.background()`, `.border()`
 * and content so the shadow wraps outward and the bevel draws on top.
 *
 * @param shape clip shape for the shadow and bevel.
 * @param accent tint colour for the outer drop shadow.
 * @param shadowElevation outer drop-shadow strength (dp).
 * @param circular when true, uses a radial-gradient bevel suitable for
 *   circular icon buttons instead of the linear top/bottom gradient.
 * @param enabled whether the press animation and click are active.
 * @param onClick if non-null, the element becomes clickable with the
 *   full press-down animation. If null, only the raised bevel + shadow
 *   are applied (for non-interactive sculpted surfaces).
 */
fun Modifier.sculpted(
    shape: Shape = RoundedCornerShape(20.dp),
    accent: Color = Citrine,
    shadowElevation: Dp = 10.dp,
    circular: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val isActive = pressed && onClick != null && enabled
    val scale by animateFloatAsState(
        targetValue = if (isActive) 0.96f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "sculptedScale",
    )
    val elevation = if (isActive) shadowElevation * 0.3f else shadowElevation

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        // Layer 1 — darker ambient base shadow: diffuse, dark, gives real depth
        // against the dark slate backgrounds so elements feel raised off the surface.
        .shadow(
            elevation = elevation * 1.4f,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.40f),
            spotColor = Color.Black.copy(alpha = 0.30f),
        )
        // Layer 2 — accent-tinted key shadow: closer, colored, adds the mineral glow.
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = accent.copy(alpha = 0.38f),
            spotColor = accent.copy(alpha = 0.65f),
        )
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick,
                )
            } else {
                Modifier
            }
        )
        .drawWithContent {
            drawContent()
            val h = size.height
            val w = size.width
            if (circular) {
                // Radial-gradient bevel for circular icon buttons.
                // Light originates from upper-left (raised) or lower-right (pressed).
                val topColor = if (!isActive) Color.White.copy(alpha = 0.22f) else Color.Black.copy(alpha = 0.35f)
                val bottomColor = if (!isActive) Color.Black.copy(alpha = 0.38f) else Color.White.copy(alpha = 0.10f)
                val centerY = if (!isActive) h * 0.32f else h * 0.68f
                val centerX = if (!isActive) w * 0.32f else w * 0.68f
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(topColor, Color.Transparent, bottomColor),
                        center = Offset(centerX, centerY),
                        radius = w * 0.65f,
                    ),
                )
            } else {
                // Linear-gradient bevel for rectangular / rounded-rect elements.
                val bevelH = (h * 0.45f).coerceAtLeast(1f)
                val bevelW = (w * 0.12f).coerceAtLeast(1f)
                val topColor = if (!isActive) Color.White.copy(alpha = 0.14f) else Color.Black.copy(alpha = 0.28f)
                val bottomColor = if (!isActive) Color.Black.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.07f)

                // Top edge highlight (or shadow when pressed)
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(topColor, Color.Transparent),
                        startY = 0f,
                        endY = bevelH,
                    ),
                )
                // Bottom edge shadow (or highlight when pressed)
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, bottomColor),
                        startY = h - bevelH,
                        endY = h,
                    ),
                )
                // Left edge highlight
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(topColor.copy(alpha = topColor.alpha * 0.5f), Color.Transparent),
                        startX = 0f,
                        endX = bevelW,
                    ),
                )
                // Right edge shadow
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, bottomColor.copy(alpha = bottomColor.alpha * 0.5f)),
                        startX = w - bevelW,
                        endX = w,
                    ),
                )
            }
        }
}

package com.rork.rockscout.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
// Brush still used by SculptedDialogButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.TextHigh

/**
 * A fully 3D sculpted solid-fill button — raised stone-slab bevel,
 * accent-tinted drop shadow, and press-down sink animation.
 *
 * @param text    label shown inside the button.
 * @param onClick click handler.
 * @param accent  tint colour for the shadow and border glow.
 * @param modifier optional modifier.
 * @param enabled when false, the button is dimmed and not clickable.
 * @param icon    optional leading icon.
 * @param shape   clip shape (default rounded rect).
 * @param shadowElevation outer drop-shadow strength.
 * @param contentPadding internal padding.
 */
@Composable
fun SculptedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = Citrine,
    containerColor: Color = Slate800,
    textColor: Color = TextHigh,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    shadowElevation: Dp = 8.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
) {
    Box(
        modifier = modifier
            .sculpted(
                shape = shape,
                accent = accent,
                shadowElevation = shadowElevation,
                enabled = enabled,
                onClick = onClick,
            )
            .clip(shape)
            .background(containerColor.copy(alpha = if (enabled) 1f else 0.5f))
            .glowingBorder(
                BorderStroke(2.dp, accent.copy(alpha = if (enabled) 0.55f else 0.2f)),
                shape,
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) textColor else textColor.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (enabled) textColor else textColor.copy(alpha = 0.4f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * A 3D sculpted outlined button — same raised bevel and press animation but
 * with a visible accent border and translucent fill. Used for secondary
 * actions (Mark Traded, Add Stop, toggles, etc.).
 */
@Composable
fun SculptedOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = Citrine,
    textColor: Color = accent,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    shadowElevation: Dp = 6.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
) {
    Box(
        modifier = modifier
            .sculpted(
                shape = shape,
                accent = accent,
                shadowElevation = shadowElevation,
                enabled = enabled,
                onClick = onClick,
            )
            .clip(shape)
            .background(
                if (enabled) accent.copy(alpha = 0.12f) else accent.copy(alpha = 0.05f)
            )
            .glowingBorder(
                BorderStroke(2.dp, accent.copy(alpha = if (enabled) 0.65f else 0.25f)),
                shape,
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) textColor else textColor.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (enabled) textColor else textColor.copy(alpha = 0.4f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * A 3D sculpted circular icon button — radial bevel, press-down sink.
 * Used for back, close, edit, share, delete, zoom, add, remove, etc.
 *
 * @param icon      the vector icon to display.
 * @param contentDescription accessibility label.
 * @param onClick   click handler.
 * @param accent    tint colour for the shadow/bevel.
 * @param size      diameter of the button in dp.
 */
@Composable
fun SculptedIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = Citrine,
    iconTint: Color = TextHigh,
    backgroundColor: Color = Slate800,
    size: Dp = 44.dp,
    shadowElevation: Dp = 5.dp,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .size(size)
            .sculpted(
                shape = CircleShape,
                accent = accent,
                shadowElevation = shadowElevation,
                circular = true,
                enabled = enabled,
                onClick = onClick,
            )
            .clip(CircleShape)
            .background(backgroundColor.copy(alpha = if (enabled) 1f else 0.5f))
            .glowingBorder(
                BorderStroke(2.dp, accent.copy(alpha = if (enabled) 0.50f else 0.2f)),
                CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) iconTint else iconTint.copy(alpha = 0.4f),
            modifier = Modifier.size(size * 0.5f),
        )
    }
}

/**
 * A lighter 3D sculpted text button for dialogs — subtle raised surface,
 * press-down sink, but lighter shadow so it doesn't look too heavy on
 * an already-elevated dialog surface.
 */
@Composable
fun SculptedTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = Citrine,
    textColor: Color = accent,
    enabled: Boolean = true,
    fontWeight: FontWeight = FontWeight.SemiBold,
    shape: Shape = RoundedCornerShape(12.dp),
    shadowElevation: Dp = 4.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
) {
    Box(
        modifier = modifier
            .sculpted(
                shape = shape,
                accent = accent,
                shadowElevation = shadowElevation,
                enabled = enabled,
                onClick = onClick,
            )
            .clip(shape)
            .background(accent.copy(alpha = if (enabled) 0.10f else 0.04f))
            .glowingBorder(
                BorderStroke(1.5.dp, accent.copy(alpha = if (enabled) 0.40f else 0.15f)),
                shape,
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = fontWeight,
            color = if (enabled) textColor else textColor.copy(alpha = 0.4f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * A 3D sculpted filled-tonal button for dialog confirm actions.
 * Slightly lighter than [SculptedButton] — uses a filled accent surface
 * with dark text, suited for dialog "OK" / "Save" / "Confirm" actions.
 */
@Composable
fun SculptedDialogButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = Citrine,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(14.dp),
    shadowElevation: Dp = 5.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
) {
    Box(
        modifier = modifier
            .sculpted(
                shape = shape,
                accent = accent,
                shadowElevation = shadowElevation,
                enabled = enabled,
                onClick = onClick,
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        accent.copy(alpha = if (enabled) 0.90f else 0.4f),
                        accent.copy(alpha = if (enabled) 0.70f else 0.3f),
                    )
                )
            )
            .glowingBorder(
                BorderStroke(2.dp, accent.copy(alpha = if (enabled) 0.80f else 0.3f)),
                shape,
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (enabled) Ink else Ink.copy(alpha = 0.4f),
        )
    }
}

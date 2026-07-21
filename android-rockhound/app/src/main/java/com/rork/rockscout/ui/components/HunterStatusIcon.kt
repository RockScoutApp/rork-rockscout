package com.rork.rockscout.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rork.rockscout.data.HunterStatus
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextMid

/** Accent color associated with a hunter status. */
fun statusAccent(status: HunterStatus): Color = when (status) {
    HunterStatus.OFF_GRID -> TextMid
    HunterStatus.ON_THE_HUNT -> Success
    HunterStatus.WISHING -> Citrine
    HunterStatus.LOOKING_FOR_TRADES -> Aqua
}

/** Border color for profile avatars based on hunter status.
 *  Off-grid = red (Danger), any other status = bright green (Success).
 *  Visible to all users viewing the profile. */
fun profileBorderColor(status: HunterStatus): Color = when (status) {
    HunterStatus.OFF_GRID -> Danger
    else -> Success
}

/** Material icon that represents each hunter status. */
fun statusIcon(status: HunterStatus): ImageVector = when (status) {
    HunterStatus.OFF_GRID -> Icons.Filled.Lock
    HunterStatus.ON_THE_HUNT -> Icons.Filled.Explore
    HunterStatus.WISHING -> Icons.Filled.Diamond
    HunterStatus.LOOKING_FOR_TRADES -> Icons.Filled.SwapHoriz
}

/** A polished, glowing status badge for hunter statuses.
 *  Replaces the plain emoji with a circular mineral-like disc so the icons feel
 *  less cartoony and more like app badges. */
@Composable
fun HunterStatusIcon(
    status: HunterStatus,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    selected: Boolean = false,
) {
    val accent = statusAccent(status)
    val transition = rememberInfiniteTransition(label = "statusIconGlow")
    val glow by transition.animateFloat(
        initialValue = 0.22f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "statusGlowAlpha",
    )
    val animatedBorder by animateColorAsState(
        targetValue = if (selected) accent else accent.copy(alpha = 0.55f),
        animationSpec = tween(250),
        label = "statusBorderColor",
    )

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = if (selected) 10.dp else 6.dp,
                shape = CircleShape,
                ambientColor = accent.copy(alpha = if (selected) 0.55f else 0.35f),
                spotColor = accent,
            )
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        accent.copy(alpha = if (selected) glow + 0.25f else glow),
                        accent.copy(alpha = 0.18f),
                        Color(0xFF1A1812).copy(alpha = 0.85f),
                    )
                )
            )
            .glowingBorder(
                width = if (selected) 2.dp else 1.5.dp,
                color = animatedBorder,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = statusIcon(status),
            contentDescription = status.label,
            tint = Color.White.copy(alpha = 0.92f),
            modifier = Modifier.size(size * 0.55f),
        )
    }
}

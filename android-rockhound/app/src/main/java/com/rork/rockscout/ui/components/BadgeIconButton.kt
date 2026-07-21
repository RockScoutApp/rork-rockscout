package com.rork.rockscout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Cyan
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.Slate900

/**
 * Circular icon button with a top-right count badge and a "New!" label below.
 * Used for the notification bell and message-request icon so they share a
 * uniform shape, size, and badge style.
 */
@Composable
fun BadgeIconButton(
    icon: ImageVector,
    contentDescription: String,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = Aqua,
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(size)
                    .sculpted(
                        shape = CircleShape,
                        accent = accent,
                        shadowElevation = 6.dp,
                        circular = true,
                        onClick = onClick,
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Slate900.copy(alpha = 0.92f), Obsidian.copy(alpha = 0.88f))
                        )
                    )
                    .glowingBorder(3.dp, accent.copy(alpha = 0.65f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = contentDescription,
                    tint = accent,
                    modifier = Modifier.size(iconSize),
                )
            }
            if (count > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-2).dp, y = (-2).dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Cyan)
                        .glowingBorder(1.5.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 5.dp, vertical = 1.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (count > 99) "99+" else count.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Ink,
                            fontSize = 9.sp,
                        ),
                    )
                }
            }
        }
        if (count > 0) {
            Text(
                text = "New!",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Cyan,
                    fontSize = 9.sp,
                ),
            )
        }
    }
}

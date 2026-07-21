package com.rork.rockscout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.components.glowingBorder

/**
 * Compact, tappable collection + wishlist count pills used on profile cards
 * (friend rows, scan cards, discover rows, etc.). Tapping opens the user's
 * public collection or wishlist.
 */
@Composable
fun ProfileStatBar(
    collectionCount: Int,
    wishlistCount: Int,
    onCollectionClick: () -> Unit,
    onWishlistClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CountPill(
            icon = Icons.Filled.Inventory2,
            count = collectionCount,
            label = "Collected",
            accent = Aqua,
            onClick = onCollectionClick,
            modifier = Modifier.weight(1f),
        )
        CountPill(
            icon = Icons.Filled.PlaylistAdd,
            count = wishlistCount,
            label = "Wishlist",
            accent = Color(0xFF9B7BD8),
            onClick = onWishlistClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CountPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    label: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = modifier
            .sculpted(shape = shape, accent = accent, shadowElevation = 4.dp, onClick = onClick)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF3A3830), Color(0xFF2E2C26))
                )
            )
            .glowingBorder(2.dp, accent.copy(alpha = 0.55f), shape)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(accent.copy(alpha = 0.22f))
                .glowingBorder(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(14.dp))
        }
        Spacer(Modifier.width(6.dp))
        Text(
            count.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = DarkTextHigh,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.width(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

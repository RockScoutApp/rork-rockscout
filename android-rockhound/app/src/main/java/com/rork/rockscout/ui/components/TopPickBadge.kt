package com.rork.rockscout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rork.rockscout.ui.theme.Citrine

/**
 * A compact, always-horizontal "Top Pick" badge used in gear rows.
 *
 * The badge is intentionally constrained to a single line and uses only its
 * intrinsic width, so it can never be squeezed into a vertical or clipped layout.
 */
@Composable
fun TopPickBadge(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Citrine.copy(alpha = 0.25f),
    borderColor: Color = Citrine.copy(alpha = 0.5f),
    contentColor: Color = Citrine,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .glowingBorder(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.width(10.dp),
        )
        Spacer(Modifier.width(3.dp))
        Text(
            text = "Top Pick",
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )
    }
}

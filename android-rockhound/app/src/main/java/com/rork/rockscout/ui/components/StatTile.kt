package com.rork.rockscout.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.rockscout.R

/**
 * A flashy, tappable stat tile used for the Collected / Wishlist / Spots cards
 * on profile screens. Includes a rock-texture background, strong top accent glow,
 * glossy highlight, and a radial icon background so the numbers pop against the dark theme.
 */
@Composable
fun StatTile(
    label: String,
    value: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = modifier
            .aspectRatio(0.82f)
            .sculpted(shape = shape, accent = accent, shadowElevation = 6.dp, onClick = onClick)
            .clip(shape)
            .glowingBorder(3.dp, accent.copy(alpha = 0.75f), shape),
        contentAlignment = Alignment.Center,
    ) {
        // Rock-texture background — fills the tile, then a dark scrim keeps the icon/text legible.
        Image(
            painter = painterResource(id = R.drawable.level_tile_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.35f), Color.Black.copy(alpha = 0.55f), Color.Black.copy(alpha = 0.72f))
                    )
                ),
        )
        // Mineral vein texture tinted by the accent color.
        GamerRockTexture(
            modifier = Modifier.fillMaxSize(),
            speckleTint = accent.copy(alpha = 0.55f),
            veinColors = listOf(accent.copy(alpha = 0.32f), Color.White.copy(alpha = 0.10f)),
        )
        // Subtle dark wash so the texture doesn't overwhelm the text.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.10f), Color.Black.copy(alpha = 0.25f), Color.Black.copy(alpha = 0.40f))
                    )
                ),
        )
        // Strong accent glow at the top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(64.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(accent.copy(alpha = 0.40f), Color.Transparent)
                    )
                ),
        )
        // Glossy highlight strip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(26.dp)
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.14f),
                            Color.White.copy(alpha = 0.05f),
                            Color.Transparent,
                        )
                    )
                ),
        )
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(accent.copy(alpha = 0.55f), accent.copy(alpha = 0.15f))
                        )
                    )
                    .glowingBorder(3.dp, accent.copy(alpha = 0.85f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

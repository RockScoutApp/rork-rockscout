package com.rork.rockscout.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.CitrineDeep
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.components.sculpted

/**
 * A shiny golden camera token "bank" shown next to the profile icon on the Home screen.
 * Displays the user's total available identifies (trial + purchased tokens combined),
 * or ∞ when unlocked via subscription or donation. Visible for all users. Tapping it
 * navigates to the token info screen.
 *
 * @param tokenBalance the total available identifies (trial + purchased tokens combined).
 * @param isUnlocked whether the user has unlimited access (subscription or donation unlock).
 *   When true, the count chip shows the infinity symbol instead of a number.
 * @param onClick invoked when the token bank is tapped.
 */
@Composable
fun TokenBank(
    tokenBalance: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isUnlocked: Boolean = false,
) {
    val transition = rememberInfiniteTransition(label = "tokenShine")
    val shine by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Reverse),
        label = "tokenShineAlpha",
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .sculpted(
                shape = RoundedCornerShape(20.dp),
                accent = Citrine,
                shadowElevation = 6.dp,
                onClick = onClick,
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF3A2E12).copy(alpha = 0.78f),
                        Color(0xFF2A2820).copy(alpha = 0.68f),
                    )
                )
            )
            .glowingBorder(3.dp, Citrine.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        // Shiny golden camera token
        Box(
            modifier = Modifier
                .size(34.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = CircleShape,
                    ambientColor = Citrine.copy(alpha = 0.45f),
                    spotColor = Citrine,
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0xFFFFE9A8),
                            Citrine,
                            CitrineDeep,
                        )
                    )
                )
                .glowingBorder(3.dp, Color(0xFFFFF2C8).copy(alpha = 0.85f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            // Sheen overlay
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color.White.copy(alpha = shine * 0.55f),
                                Color.Transparent,
                            )
                        )
                    ),
            )
            Icon(
                Icons.Filled.CameraAlt,
                contentDescription = "Identifier tokens",
                tint = Ink,
                modifier = Modifier.size(18.dp),
            )
        }
        // Count chip
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Citrine.copy(alpha = 0.18f))
                .glowingBorder(2.dp, Citrine.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                .padding(horizontal = 7.dp, vertical = 2.dp),
        ) {
            Text(
                text = if (isUnlocked) "\u221E" else tokenBalance.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

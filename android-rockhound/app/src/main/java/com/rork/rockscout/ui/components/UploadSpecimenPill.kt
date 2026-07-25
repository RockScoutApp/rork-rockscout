package com.rork.rockscout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.rockscout.R
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Ink

/**
 * A single-row pill button used for the specimen upload action.
 * Shows a rock/crystal icon on the left and the word "Upload" on the right,
 * styled with the same sculpted citrine accent used throughout the app.
 */
@Composable
fun UploadSpecimenPill(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val alpha = if (enabled) 1f else 0.5f
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .glowingBorder(1.5.dp, Citrine.copy(alpha = 0.55f), RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Citrine.copy(alpha = 0.22f * alpha),
                        Citrine.copy(alpha = 0.10f * alpha),
                    )
                )
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Citrine.copy(alpha = 0.85f * alpha)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_upload_rock),
                contentDescription = null,
                tint = Ink,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Upload",
            style = MaterialTheme.typography.labelLarge,
            color = Citrine.copy(alpha = alpha),
            fontWeight = FontWeight.Bold,
        )
    }
}

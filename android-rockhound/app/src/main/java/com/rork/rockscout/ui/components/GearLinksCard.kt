package com.rork.rockscout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.rork.rockscout.ui.components.glowingBorder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.rockscout.data.GearItem
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink

/**
 * A contextual "Gear" card showing affiliate gear recommendations.
 *
 * Renders a section title (caller-supplied) and a list of [GearItem]s, each
 * tappable to open the affiliate URL in the system browser. Useful content,
 * not an ad — stays visible to every user (Premium and Pro included).
 *
 * @param sectionTitle e.g. "Gear to find this", "Recommended gear for this trip"
 * @param items the gear recommendations to display
 * @param accent the accent color for the section
 */
@Composable
fun GearLinksCard(
    sectionTitle: String,
    items: List<GearItem>,
    accent: Color = Citrine,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Text(
            sectionTitle.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = Aqua,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
        )
        DarkCard(modifier = Modifier.fillMaxWidth(), accent = accent) {
            items.forEach { item ->
                GearItemRow(item = item, accent = accent, onClick = {
                    SafeLinkOpener.openUrl(context, item.url)
                })
                if (item != items.last()) {
                    Spacer(Modifier.height(10.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Links will open separately in your browser",
                style = MaterialTheme.typography.labelSmall,
                color = Aqua,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

@Composable
private fun GearItemRow(item: GearItem, accent: Color, onClick: () -> Unit) {
    val rowShape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(rowShape)
            .background(Color(0xFF3A3830))
            .glowingBorder(1.dp, accent.copy(alpha = 0.35f), rowShape)
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.22f))
                .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(item.emoji, style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.name,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                item.priceBand,
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Icon(
            Icons.Filled.OpenInNew,
            contentDescription = "Open ${item.name}",
            tint = accent,
            modifier = Modifier.size(18.dp),
        )
    }
}

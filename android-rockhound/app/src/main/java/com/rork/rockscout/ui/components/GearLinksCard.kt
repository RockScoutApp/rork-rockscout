package com.rork.rockscout.ui.components

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.rork.rockscout.ui.components.glowingBorder
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rork.rockscout.data.AffiliateClickTracker
import com.rork.rockscout.data.GearItem
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.data.TopPickManager
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import androidx.compose.foundation.shape.RoundedCornerShape
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
                    AffiliateClickTracker.recordClick(context, item.id, item.name)
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

/**
 * A compact, fixed-height scrollable gear box — the same visual pattern as the
 * home screen's gear tile. Shows a section header and a scrollable list of affiliate
 * gear items inside a dark, rounded container. Designed for placement inside detail
 * screen LazyColumns (campground, trailhead, state park, etc.).
 *
 * @param sectionTitle e.g. "Camping Gear", "Hiking Gear"
 * @param items the gear recommendations to display
 * @param accent the accent color for the section
 * @param boxHeight the fixed height of the scrollable area (default 200dp)
 */
@Composable
fun ScrollingGearBox(
    sectionTitle: String,
    items: List<GearItem>,
    accent: Color = Citrine,
    boxHeight: androidx.compose.ui.unit.Dp = 200.dp,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return
    val context = LocalContext.current
    val topPickIds by TopPickManager.topPickIds.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Text(
            sectionTitle.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = Aqua,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
        )
        val boxShape = RoundedCornerShape(18.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(boxHeight)
                .clip(boxShape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                    )
                )
                .glowingBorder(2.dp, accent.copy(alpha = 0.45f), boxShape),
        ) {
            // Accent glow at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(accent.copy(alpha = 0.18f), Color.Transparent)
                        )
                    ),
            )
            // Scrollable gear list — pointerInput consumes taps so the parent
            // LazyColumn doesn't intercept scroll gestures inside the box.
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 6.dp, bottom = 6.dp, start = 10.dp, end = 10.dp)
                    .pointerInput(Unit) { detectTapGestures { } },
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
            ) {
                items(items, key = { it.id }) { item ->
                    CompactGearItemRow(
                        item = item,
                        accent = accent,
                        isTopPick = topPickIds.contains(item.id),
                        onClick = {
                            AffiliateClickTracker.recordClick(context, item.id, item.name)
                            SafeLinkOpener.openUrl(context, item.url)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactGearItemRow(
    item: GearItem,
    accent: Color,
    isTopPick: Boolean,
    onClick: () -> Unit,
) {
    val rowShape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(rowShape)
            .background(if (isTopPick) Color(0xFF3D3826) else Color(0xFF3A3830).copy(alpha = 0.92f))
            .glowingBorder(
                1.dp,
                if (isTopPick) Citrine.copy(alpha = 0.6f) else accent.copy(alpha = 0.35f),
                rowShape,
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.22f))
                .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(item.emoji, style = MaterialTheme.typography.titleSmall)
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    item.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (isTopPick) {
                    Spacer(Modifier.width(6.dp))
                    TopPickBadge()
                }
            }
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
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun GearItemRow(item: GearItem, accent: Color, onClick: () -> Unit) {
    val topPickIds by TopPickManager.topPickIds.collectAsStateWithLifecycle()
    val isTopPick = topPickIds.contains(item.id)
    val rowShape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(rowShape)
            .background(if (isTopPick) Color(0xFF3D3826) else Color(0xFF3A3830))
            .glowingBorder(
                1.dp,
                if (isTopPick) Citrine.copy(alpha = 0.6f) else accent.copy(alpha = 0.35f),
                rowShape,
            )
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    item.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (isTopPick) {
                    Spacer(Modifier.width(6.dp))
                    TopPickBadge()
                }
            }
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

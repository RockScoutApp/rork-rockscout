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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.HunterStatus
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Ink

/**
 * Reusable hunter-status dropdown with a full-width trigger and a compact trigger.
 * The full variant is used on the home screen in the blue “status update” box; the
 * compact variant is used under the Social Settings pill on the profile screen.
 */
@Composable
fun HunterStatusDropdown(
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    hunterStatus: HunterStatus,
    isPremium: Boolean,
    isSignedIn: Boolean = true,
    onCooldown: (minutes: Int) -> Unit = {},
) {
    if (!isSignedIn) return

    val repo = remember { AppRepository.instance }
    var expanded by remember { mutableStateOf(false) }
    val accentColor = statusAccent(hunterStatus)
    val shape = RoundedCornerShape(12.dp)

    val iconSize = if (isCompact) 16.dp else 22.dp
    val chevronSize = if (isCompact) 14.dp else 18.dp
    val textStyle = if (isCompact) {
        MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
    } else {
        MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
    }
    val horizontalPadding = if (isCompact) 8.dp else 12.dp
    val verticalPadding = if (isCompact) 4.dp else 8.dp

    Box(modifier = modifier) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Text(
                "Your status",
                style = MaterialTheme.typography.labelMedium,
                color = Aqua,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            )
            HunterStatus.entries.forEach { status ->
                val selected = status == hunterStatus
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            HunterStatusIcon(
                                status = status,
                                size = 34.dp,
                                selected = selected,
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                status.label,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Aqua,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            )
                        }
                    },
                    trailingIcon = {
                        if (selected) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = Citrine,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    },
                    onClick = {
                        if (repo.canChangeStatus(isPremium)) {
                            repo.setHunterStatus(status)
                        } else {
                            onCooldown(repo.statusCooldownMinutesRemaining(isPremium))
                        }
                        expanded = false
                    },
                )
            }
            HorizontalDivider()
            Text(
                "Visible to other RockScouts in scans and on your pings.",
                style = MaterialTheme.typography.labelSmall,
                color = Aqua,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .matchParentSize()
                .sculpted(
                    shape = shape,
                    accent = accentColor,
                    shadowElevation = if (isCompact) 3.dp else 5.dp,
                    onClick = { expanded = true },
                )
                .clip(shape)
                .background(accentColor.copy(alpha = 0.22f))
                .glowingBorder(2.dp, accentColor.copy(alpha = 0.7f), shape)
                .clickable { expanded = true }
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        ) {
            HunterStatusIcon(
                status = hunterStatus,
                size = iconSize,
            )
            Text(
                text = hunterStatus.label,
                style = textStyle,
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(chevronSize),
            )
        }
    }
}

/** A small, standalone "Status" label row used in the compact profile variant. */
@Composable
fun HunterStatusCompactTrigger(
    modifier: Modifier = Modifier,
    hunterStatus: HunterStatus,
) {
    val accentColor = statusAccent(hunterStatus)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier,
    ) {
        Text(
            "Status",
            style = MaterialTheme.typography.labelSmall,
            color = accentColor,
            fontWeight = FontWeight.Bold,
        )
        Icon(
            Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(12.dp),
        )
    }
}

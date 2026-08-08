package com.rork.rockscout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Whatshot
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Success

// ── Badge flags ───────────────────────────────────────────────────────────

/** Flags indicating which badges a user has earned. */
data class BadgeFlags(
    val topContributor: Boolean = false,
    val avidTrader: Boolean = false,
    val specimenContributor: Boolean = false,
    val expert: Boolean = false,
    /** When true, the Expert badge shows a small green checkmark (AI-auto-verified).
     *  When false (manually approved), no checkmark is shown. */
    val expertAutoVerified: Boolean = false,
)

// ── Avatar size tiers ──────────────────────────────────────────────────────

/** Size tiers for UserAvatar with proportional badge scaling. */
enum class AvatarSize(val diameter: Dp, val badgeSize: Dp, val nameSpacing: Dp) {
    LARGE(72.dp, 5.dp, 8.dp),
    MEDIUM(48.dp, 3.5.dp, 8.dp),
    SMALL(32.dp, 2.5.dp, 6.dp),
    THUMBNAIL(24.dp, 2.dp, 4.dp),
}

// ── Badge colors ────────────────────────────────────────────────────────────

private val BadgeAmber = Color(0xFFE8A33D)
private val BadgeGreen = Color(0xFF5CC98C)
private val BadgeBlue = Color(0xFF6FA8C7)
private val BadgePurple = Color(0xFF9B7BD8)
private val BadgeBg = Color(0xFF1E1C16)

// ── BadgeOverlay ────────────────────────────────────────────────────────────

/** Overlays earned badge icons on the four corners of an avatar.
 *  Badges are small circular icons with a semi-transparent dark background
 *  and a colored icon. All earned badges are always shown, scaled to the
 *  avatar size.
 *
 *  - Top-left: Top Contributor (amber, Whatshot icon)
 *  - Top-right: Avid Trader (green, CompareArrows icon)
 *  - Bottom-left: Specimen Contributor (blue, VolunteerActivism icon)
 *  - Bottom-right: Expert (purple, Biotech icon, with green check if auto-verified)
 */
@Composable
fun BoxScope.BadgeOverlay(
    badgeFlags: BadgeFlags,
    size: AvatarSize,
) {
    val badgeDp = size.badgeSize
    val iconSp = (badgeDp.value * 0.7f).sp

    // Top-left: Top Contributor
    if (badgeFlags.topContributor) {
        BadgeIcon(
            icon = Icons.Filled.Whatshot,
            color = BadgeAmber,
            size = badgeDp,
            iconSp = iconSp,
            modifier = Modifier.align(Alignment.TopStart),
        )
    }

    // Top-right: Avid Trader
    if (badgeFlags.avidTrader) {
        BadgeIcon(
            icon = Icons.Filled.CompareArrows,
            color = BadgeGreen,
            size = badgeDp,
            iconSp = iconSp,
            modifier = Modifier.align(Alignment.TopEnd),
        )
    }

    // Bottom-left: Specimen Contributor
    if (badgeFlags.specimenContributor) {
        BadgeIcon(
            icon = Icons.Filled.VolunteerActivism,
            color = BadgeBlue,
            size = badgeDp,
            iconSp = iconSp,
            modifier = Modifier.align(Alignment.BottomStart),
        )
    }

    // Bottom-right: Expert (with optional green checkmark)
    if (badgeFlags.expert) {
        Box(modifier = Modifier.align(Alignment.BottomEnd)) {
            BadgeIcon(
                icon = Icons.Filled.Biotech,
                color = BadgePurple,
                size = badgeDp,
                iconSp = iconSp,
                modifier = Modifier,
            )
            // Green checkmark overlay for auto-verified
            if (badgeFlags.expertAutoVerified) {
                val checkDp = badgeDp * 0.6f
                Box(
                    modifier = Modifier
                        .size(checkDp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(Success),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size((checkDp.value * 0.7f).dp),
                    )
                }
            }
        }
    }
}

/** A single badge icon — small circular dark background with colored icon. */
@Composable
private fun BadgeIcon(
    icon: ImageVector,
    color: Color,
    size: Dp,
    iconSp: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(BadgeBg.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center,
    ) {
        if (size.value >= 3f) {
            // Icon visible at larger sizes
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size((size.value * 0.65f).dp),
            )
        } else {
            // At very small sizes (2dp), just show a colored dot
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}

// ── UserAvatar ──────────────────────────────────────────────────────────────

/** Reusable avatar component that renders a profile photo or "?" placeholder,
 *  with optional badge overlays, and the display name to the right.
 *
 *  - If imagePath is not blank, renders the photo with ContentScale.Crop.
 *  - If no photo, renders a large "?" centered in the circle.
 *  - Badge overlays are built in — automatically rendered at the correct scale.
 *  - Display name is rendered to the RIGHT of the avatar in a Row.
 *
 *  @param imagePath Optional path/URL to the profile photo.
 *  @param displayName The user's display name, shown to the right.
 *  @param size The avatar size tier (LARGE, MEDIUM, SMALL, THUMBNAIL).
 *  @param badgeFlags Optional badge flags — null means no badges.
 *  @param modifier Modifier for the outer Row.
 *  @param onClick Optional click handler for the avatar circle.
 *  @param showName Whether to show the display name (false for avatar-only contexts).
 *  @param nameStyle Optional text style override for the name.
 *  @param nameColor Optional color override for the name.
 *  @param borderColor Optional border color (defaults to Citrine/Aqua gradient).
 */
@Composable
fun UserAvatar(
    imagePath: String?,
    displayName: String,
    size: AvatarSize,
    badgeFlags: BadgeFlags? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    showName: Boolean = true,
    nameStyle: androidx.compose.ui.text.TextStyle? = null,
    nameColor: Color? = null,
    borderColor: Color? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(size.nameSpacing),
        modifier = modifier,
    ) {
        // Avatar circle with optional badge overlay
        Box(
            modifier = Modifier.size(size.diameter),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(size.diameter)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                (borderColor ?: Citrine).copy(alpha = 0.5f),
                                Aqua.copy(alpha = 0.3f),
                            ),
                        ),
                    )
                    .glowingBorder(
                        2.dp,
                        borderColor ?: Citrine.copy(alpha = 0.5f),
                        CircleShape,
                    )
                    .let { mod -> if (onClick != null) mod.clickable(onClick = onClick) else mod },
                contentAlignment = Alignment.Center,
            ) {
                if (!imagePath.isNullOrBlank()) {
                    AsyncImage(
                        model = imagePath,
                        contentDescription = "Profile picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    // "?" placeholder — large bold question mark
                    val questionSp = (size.diameter.value * 0.6f).sp
                    Text(
                        text = "?",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = questionSp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = (borderColor ?: Citrine).copy(alpha = 0.7f),
                    )
                }
            }

            // Badge overlay on top of avatar
            if (badgeFlags != null) {
                BadgeOverlay(
                    badgeFlags = badgeFlags,
                    size = size,
                )
            }
        }

        // Display name to the right
        if (showName) {
            Text(
                text = displayName,
                style = nameStyle ?: when (size) {
                    AvatarSize.LARGE -> MaterialTheme.typography.titleMedium
                    AvatarSize.MEDIUM -> MaterialTheme.typography.titleSmall
                    AvatarSize.SMALL -> MaterialTheme.typography.bodyMedium
                    AvatarSize.THUMBNAIL -> MaterialTheme.typography.labelMedium
                },
                color = nameColor ?: Color.White,
                maxLines = 1,
            )
        }
    }
}

// ── BadgeRow ──────────────────────────────────────────────────────────────

/** Horizontal wrapable row showing each earned badge as [icon] [name]
 *  with a subtle pill background. Only earned badges are shown. */
@Composable
fun BadgeRow(
    badgeFlags: BadgeFlags,
    modifier: Modifier = Modifier,
) {
    data class BadgeEntry(val name: String, val icon: ImageVector, val color: Color)
    val badges = buildList {
        if (badgeFlags.topContributor) add(BadgeEntry("Top Contributor", Icons.Filled.Whatshot, BadgeAmber))
        if (badgeFlags.avidTrader) add(BadgeEntry("Avid Trader", Icons.Filled.CompareArrows, BadgeGreen))
        if (badgeFlags.specimenContributor) add(BadgeEntry("Specimen Contributor", Icons.Filled.VolunteerActivism, BadgeBlue))
        if (badgeFlags.expert) add(BadgeEntry("Expert", Icons.Filled.Biotech, BadgePurple))
    }

    if (badges.isEmpty()) return

    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        badges.forEach { badge ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(BadgeBg.copy(alpha = 0.6f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Icon(
                    imageVector = badge.icon,
                    contentDescription = null,
                    tint = badge.color,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = badge.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = badge.color,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

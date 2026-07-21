package com.rork.rockscout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextHigh

/**
 * A compact heart + plus dropdown combo for specimen cards.
 *
 * The heart toggles wishlist status with the YooperliteHeart glow animation.
 * The plus button opens a dropdown with "Add to Collection" and "Add to Wishlist" options.
 *
 * @param collected whether the specimen is already in the user's collection
 * @param wishlisted whether the specimen is already on the user's wishlist
 * @param onCollectionToggle callback when collection toggle is tapped
 * @param onWishlistToggle callback when wishlist toggle is tapped
 * @param accent color accent for the card
 * @param heartSize size of the heart button
 * @param plusSize size of the plus button
 */
@Composable
fun SpecimenHeartPlus(
    collected: Boolean,
    wishlisted: Boolean,
    onCollectionToggle: () -> Unit,
    onWishlistToggle: () -> Unit,
    accent: Color = Citrine,
    heartSize: Dp = 36.dp,
    plusSize: Dp = 36.dp,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Heart button — toggles wishlist
        Box(
            modifier = Modifier
                .size(heartSize)
                .sculpted(
                    shape = RoundedCornerShape(heartSize / 2),
                    accent = accent,
                    shadowElevation = 3.dp,
                    circular = true,
                    onClick = onWishlistToggle,
                )
                .clip(RoundedCornerShape(heartSize / 2))
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            YooperliteHeart(
                active = wishlisted,
                contentDescription = if (wishlisted) "Remove from wishlist" else "Add to wishlist",
                size = (heartSize * 0.55f),
                unclippedGlow = true,
            )
        }

        // Plus dropdown — add to collection or wishlist
        var showAddMenu by remember { mutableStateOf(false) }
        Box {
            Box(
                modifier = Modifier
                    .size(plusSize)
                    .sculpted(
                        shape = RoundedCornerShape(plusSize / 2),
                        accent = accent,
                        shadowElevation = 3.dp,
                        circular = true,
                        onClick = { showAddMenu = true },
                    )
                    .clip(RoundedCornerShape(plusSize / 2))
                    .background(Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (collected && wishlisted) Icons.Filled.Check else Icons.Filled.Add,
                    contentDescription = "Add to list",
                    tint = Color.White,
                    modifier = Modifier.size((plusSize * 0.5f)),
                )
            }
            DropdownMenu(
                expanded = showAddMenu,
                onDismissRequest = { showAddMenu = false },
            ) {
                DropdownMenuItem(
                    text = { Text(if (collected) "✓ In Collection" else "Add to Collection") },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = if (collected) Success else Color.White,
                        )
                    },
                    onClick = {
                        onCollectionToggle()
                        showAddMenu = false
                    },
                )
                DropdownMenuItem(
                    text = { Text(if (wishlisted) "♥ On Wishlist" else "Add to Wishlist") },
                    leadingIcon = {
                        YooperliteHeart(
                            active = wishlisted,
                            contentDescription = "",
                            size = 20.dp,
                            inactiveTint = Color.White,
                        )
                    },
                    onClick = {
                        onWishlistToggle()
                        showAddMenu = false
                    },
                )
            }
        }
    }
}

/**
 * A generic plus dropdown for non-specimen content cards (posts, comments, locations, saved images).
 *
 * Shows a plus button that opens a dropdown with context-appropriate actions.
 *
 * @param actions list of [CardAction] items to show in the dropdown
 * @param accent color accent for the button
 * @param size diameter of the plus button
 */
@Composable
fun CardPlusDropdown(
    actions: List<CardAction>,
    accent: Color = Citrine,
    size: Dp = 36.dp,
    allDone: Boolean = false,
    modifier: Modifier = Modifier,
) {
    if (actions.isEmpty()) return

    var showMenu by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(size)
                .sculpted(
                    shape = RoundedCornerShape(size / 2),
                    accent = accent,
                    shadowElevation = 3.dp,
                    circular = true,
                    onClick = { showMenu = true },
                )
                .clip(RoundedCornerShape(size / 2))
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (allDone) Icons.Filled.Check else Icons.Filled.Add,
                contentDescription = "More actions",
                tint = Color.White,
                modifier = Modifier.size(size * 0.5f),
            )
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
        ) {
            actions.forEachIndexed { index, action ->
                if (action is CardAction.Divider) {
                    if (index > 0) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                } else {
                    val item = action as CardAction.Item
                    DropdownMenuItem(
                        text = { Text(item.label) },
                        leadingIcon = {
                            item.icon?.let { icon ->
                                Icon(
                                    icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = item.iconTint ?: Color.White,
                                )
                            }
                        },
                        onClick = {
                            item.onClick()
                            showMenu = false
                        },
                    )
                }
            }
        }
    }
}

/**
 * Sealed interface for dropdown action items.
 */
sealed interface CardAction {
    /**
     * A clickable action item in the dropdown.
     */
    data class Item(
        val label: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
        val iconTint: Color? = null,
        val onClick: () -> Unit,
    ) : CardAction

    /**
     * A divider between groups of actions.
     */
    data object Divider : CardAction
}

/**
 * Format a like count for compact display under the heart icon.
 * Numbers under 10,000 are shown as-is; larger values use K/M suffixes.
 */
private fun formatLikeCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 10_000 -> "${count / 1000}K"
        else -> count.toString()
    }
}

/**
 * A compact heart button for non-specimen cards (posts, comments, saved images).
 * Uses the YooperliteHeart glow animation.
 *
 * When [likeCount] is greater than zero, a small bold number is rendered directly
 * below the heart with its top slightly overlapping the heart's bottom edge.
 * The number is not clickable — it moves as one with the heart icon.
 *
 * @param active whether the heart is in the "liked" state
 * @param onToggle callback when the heart is tapped
 * @param accent color accent for the button
 * @param size diameter of the button
 * @param likeCount optional like count to display below the heart (0 = hidden)
 */
@Composable
fun CardHeart(
    active: Boolean,
    onToggle: () -> Unit,
    accent: Color = Citrine,
    size: Dp = 36.dp,
    contentDescription: String = if (active) "Unlike" else "Like",
    likeCount: Int = 0,
    modifier: Modifier = Modifier,
) {
    if (likeCount <= 0) {
        // The sculpted button frame is drawn first (clipped, black bg, bevel
        // overlay). The YooperliteHeart — including its UV glow halo — is then
        // rendered in an unclipped Box on top so the glow is visible around the
        // icon instead of being covered by the sculpted bevel / clipped by the
        // circular frame.
        Box(
            modifier = modifier
                .size(size)
                .sculpted(
                    shape = RoundedCornerShape(size / 2),
                    accent = accent,
                    shadowElevation = 3.dp,
                    circular = true,
                    onClick = onToggle,
                )
                .clip(RoundedCornerShape(size / 2))
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            YooperliteHeart(
                active = active,
                contentDescription = contentDescription,
                size = size * 0.55f,
                // Allow the glow halo to render outside the clipped button frame.
                unclippedGlow = true,
            )
        }
    } else {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(size)
                    .sculpted(
                        shape = RoundedCornerShape(size / 2),
                        accent = accent,
                        shadowElevation = 3.dp,
                        circular = true,
                        onClick = onToggle,
                    )
                    .clip(RoundedCornerShape(size / 2))
                    .background(Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                YooperliteHeart(
                    active = active,
                    contentDescription = contentDescription,
                    size = size * 0.55f,
                    unclippedGlow = true,
                )
            }
            Text(
                text = formatLikeCount(likeCount),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(y = (-5).dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 3.dp, vertical = 1.dp),
            )
        }
    }
}

/**
 * A text-based "Add/Share" dropdown button styled to match the app's dialog buttons.
 *
 * Opens a clean dropdown menu with the supplied [CardAction] items. Use this for
 * non-specimen content (journal entries, trips, locations, etc.) that needs a
 * single compact action button instead of multiple standalone share buttons.
 *
 * @param actions list of [CardAction] items to show in the dropdown
 * @param accent color accent for the button and shadow
 * @param textColor color used for the button text
 * @param modifier optional layout modifier
 */
@Composable
fun AddShareDropdown(
    actions: List<CardAction>,
    accent: Color = Citrine,
    textColor: Color = accent,
    modifier: Modifier = Modifier,
) {
    if (actions.isEmpty()) return

    var showMenu by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        SculptedOutlinedButton(
            text = "Add/Share",
            onClick = { showMenu = true },
            accent = accent,
            textColor = textColor,
            icon = Icons.Filled.ArrowDropDown,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            shadowElevation = 4.dp,
        )
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
        ) {
            actions.forEachIndexed { index, action ->
                if (action is CardAction.Divider) {
                    if (index > 0) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                } else {
                    val item = action as CardAction.Item
                    DropdownMenuItem(
                        text = {
                            Text(
                                item.label,
                                fontWeight = FontWeight.Medium,
                            )
                        },
                        leadingIcon = {
                            item.icon?.let { icon ->
                                Icon(
                                    icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = item.iconTint ?: Color.White,
                                )
                            }
                        },
                        modifier = Modifier.height(52.dp),
                        onClick = {
                            item.onClick()
                            showMenu = false
                        },
                    )
                }
            }
        }
    }
}

/**
 * A standalone "Add/Share" dropdown button for specimen cards.
 *
 * Opens a clean dropdown with up to 4 options: Add to Collection, Add to Wishlist,
 * Share to Profile, and Share to Social. Each item has generous padding and a
 * leading icon for easy reading and tapping.
 *
 * @param collected whether the specimen is already in the user's collection
 * @param wishlisted whether the specimen is already on the user's wishlist
 * @param onCollectionToggle callback for Add to Collection
 * @param onWishlistToggle callback for Add to Wishlist
 * @param onShareToProfile callback for Share to Profile (null = hide option)
 * @param onShareToSocial callback for Share to Social (null = hide option)
 * @param accent color accent for the card
 * @param height height of the button row
 * @param modifier optional layout modifier
 */
@Composable
fun SpecimenAddShare(
    collected: Boolean,
    wishlisted: Boolean,
    onCollectionToggle: () -> Unit,
    onWishlistToggle: () -> Unit,
    onShareToProfile: (() -> Unit)? = null,
    onShareToSocial: (() -> Unit)? = null,
    accent: Color = Citrine,
    height: Dp = 36.dp,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .height(height)
                .sculpted(
                    shape = RoundedCornerShape(height / 2),
                    accent = accent,
                    shadowElevation = 3.dp,
                    onClick = { showMenu = true },
                )
                .clip(RoundedCornerShape(height / 2))
                .background(Color.Black)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = "Add/Share",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Open menu",
                tint = accent,
                modifier = Modifier.size(16.dp),
            )
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
        ) {
            // Add to Collection
            DropdownMenuItem(
                text = {
                    Text(
                        if (collected) "\u2713 In Collection" else "Add to Collection",
                        fontWeight = FontWeight.Medium,
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Inventory2,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (collected) Success else Color.White,
                    )
                },
                modifier = Modifier.height(52.dp),
                onClick = {
                    onCollectionToggle()
                    showMenu = false
                },
            )
            // Add to Wishlist
            DropdownMenuItem(
                text = {
                    Text(
                        if (wishlisted) "\u2665 On Wishlist" else "Add to Wishlist",
                        fontWeight = FontWeight.Medium,
                    )
                },
                leadingIcon = {
                    YooperliteHeart(
                        active = wishlisted,
                        contentDescription = "",
                        size = 20.dp,
                        inactiveTint = Color.White,
                    )
                },
                modifier = Modifier.height(52.dp),
                onClick = {
                    onWishlistToggle()
                    showMenu = false
                },
            )
            // Share to Profile
            if (onShareToProfile != null) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                DropdownMenuItem(
                    text = {
                        Text(
                            "Share to Profile",
                            fontWeight = FontWeight.Medium,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Aqua,
                        )
                    },
                    modifier = Modifier.height(52.dp),
                    onClick = {
                        onShareToProfile()
                        showMenu = false
                    },
                )
            }
            // Share to Social
            if (onShareToSocial != null) {
                DropdownMenuItem(
                    text = {
                        Text(
                            "Share to Social",
                            fontWeight = FontWeight.Medium,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Share,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Citrine,
                        )
                    },
                    modifier = Modifier.height(52.dp),
                    onClick = {
                        onShareToSocial()
                        showMenu = false
                    },
                )
            }
        }
    }
}

/**
 * Compact circular Add/Share button for the redesigned specimen cards.
 *
 * A small accent-colored circular button with a "+" icon that opens the same
 * dropdown as SpecimenAddShare (Add to Collection, Add to Wishlist, Share to
 * Profile, Share to Social). Designed to sit in the bottom-right corner of
 * the card, stacked above the heart.
 *
 * @param collected whether the specimen is already in the user's collection
 * @param wishlisted whether the specimen is already on the user's wishlist
 * @param onCollectionToggle callback for Add to Collection
 * @param onWishlistToggle callback for Add to Wishlist
 * @param onShareToProfile callback for Share to Profile (null = hide option)
 * @param onShareToSocial callback for Share to Social (null = hide option)
 * @param accent color accent for the button
 * @param size diameter of the circular button
 * @param modifier optional layout modifier
 */
@Composable
fun CompactAddShareButton(
    collected: Boolean,
    wishlisted: Boolean,
    onCollectionToggle: () -> Unit,
    onWishlistToggle: () -> Unit,
    onShareToProfile: (() -> Unit)? = null,
    onShareToSocial: (() -> Unit)? = null,
    accent: Color = Citrine,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(size)
                .sculpted(
                    shape = RoundedCornerShape(size / 2),
                    accent = accent,
                    shadowElevation = 3.dp,
                    circular = true,
                    onClick = { showMenu = true },
                )
                .clip(RoundedCornerShape(size / 2))
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add/Share",
                tint = accent,
                modifier = Modifier.size(size * 0.45f),
            )
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        if (collected) "\u2713 In Collection" else "Add to Collection",
                        fontWeight = FontWeight.Medium,
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Inventory2,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (collected) Success else Color.White,
                    )
                },
                modifier = Modifier.height(52.dp),
                onClick = {
                    onCollectionToggle()
                    showMenu = false
                },
            )
            DropdownMenuItem(
                text = {
                    Text(
                        if (wishlisted) "\u2665 On Wishlist" else "Add to Wishlist",
                        fontWeight = FontWeight.Medium,
                    )
                },
                leadingIcon = {
                    YooperliteHeart(
                        active = wishlisted,
                        contentDescription = "",
                        size = 20.dp,
                        inactiveTint = Color.White,
                    )
                },
                modifier = Modifier.height(52.dp),
                onClick = {
                    onWishlistToggle()
                    showMenu = false
                },
            )
            if (onShareToProfile != null) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                DropdownMenuItem(
                    text = {
                        Text("Share to Profile", fontWeight = FontWeight.Medium)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Aqua,
                        )
                    },
                    modifier = Modifier.height(52.dp),
                    onClick = {
                        onShareToProfile()
                        showMenu = false
                    },
                )
            }
            if (onShareToSocial != null) {
                DropdownMenuItem(
                    text = {
                        Text("Share to Social", fontWeight = FontWeight.Medium)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Share,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Citrine,
                        )
                    },
                    modifier = Modifier.height(52.dp),
                    onClick = {
                        onShareToSocial()
                        showMenu = false
                    },
                )
            }
        }
    }
}

/**
 * A compact "Like" heart + "Add/Share" dropdown combo for specimen cards.
 *
 * The heart toggles a lightweight "like" (personal favorite) with the
 * YooperliteHeart glow animation. The "Add/Share" button opens a clean
 * dropdown with up to 4 options: Add to Collection, Add to Wishlist,
 * Share to Profile, and Share to Social.
 *
 * @param liked     whether the specimen is currently "liked" by the user
 * @param onLikeToggle callback when the heart is tapped
 * @param collected whether the specimen is already in the user's collection
 * @param wishlisted whether the specimen is already on the user's wishlist
 * @param onCollectionToggle callback for Add to Collection
 * @param onWishlistToggle callback for Add to Wishlist
 * @param onShareToProfile callback for Share to Profile (null = hide option)
 * @param onShareToSocial callback for Share to Social (null = hide option)
 * @param accent color accent for the card
 * @param heartSize diameter of the heart button
 * @param modifier optional layout modifier
 */
@Composable
fun SpecimenLikeAddShare(
    liked: Boolean,
    onLikeToggle: () -> Unit,
    collected: Boolean,
    wishlisted: Boolean,
    onCollectionToggle: () -> Unit,
    onWishlistToggle: () -> Unit,
    onShareToProfile: (() -> Unit)? = null,
    onShareToSocial: (() -> Unit)? = null,
    accent: Color = Citrine,
    heartSize: Dp = 36.dp,
    likeCount: Int = 0,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CardHeart(
            active = liked,
            onToggle = onLikeToggle,
            accent = accent,
            size = heartSize,
            contentDescription = if (liked) "Unlike" else "Like",
            likeCount = likeCount,
        )
        SpecimenAddShare(
            collected = collected,
            wishlisted = wishlisted,
            onCollectionToggle = onCollectionToggle,
            onWishlistToggle = onWishlistToggle,
            onShareToProfile = onShareToProfile,
            onShareToSocial = onShareToSocial,
            accent = accent,
            height = heartSize,
        )
    }
}


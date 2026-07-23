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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rork.rockscout.data.Badge
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.StoneLine
import coil3.compose.AsyncImage

/**
 * Gamer-rock themed stat pill used under the level tile on the Achievements screen.
 * Combines a dark mineral texture background, a colored accent border, a glowing
 * icon circle, and bold white text so the numbers pop.
 */
@Composable
fun GamerStatPill(
    label: String,
    value: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    backgroundUrl: String? = null,
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .sculpted(shape = shape, accent = accent, shadowElevation = 5.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF3A3530), Color(0xFF2A2820), Color(0xFF1E1C16))
                )
            )
            .glowingBorder(2.dp, accent.copy(alpha = 0.85f), shape),
        contentAlignment = Alignment.Center,
    ) {
        // Generated mineral background image — similar to the achievement tiles.
        if (!backgroundUrl.isNullOrBlank()) {
            AsyncImage(
                model = backgroundUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.55f), Color.Black.copy(alpha = 0.72f))
                        )
                    ),
            )
        }
        // Mineral texture overlay behind the content.
        GamerRockTexture(
            modifier = Modifier.fillMaxSize(),
            speckleTint = accent.copy(alpha = 0.35f),
            veinColors = listOf(
                accent.copy(alpha = 0.22f),
                Color(0xFFD8C8A8).copy(alpha = 0.12f),
            ),
        )
        // Dark scrim so text stays legible over the busy texture.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.10f),
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.55f),
                        )
                    )
                ),
        )
        // Large radial accent glow — fills the tile like the badge tiles.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(accent.copy(alpha = 0.35f), accent.copy(alpha = 0.12f), Color.Transparent)
                    )
                ),
        )
        // Glossy highlight strip at the top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(22.dp)
                .background(
                    Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.14f), Color.White.copy(alpha = 0.05f), Color.Transparent)
                    )
                ),
        )
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(accent.copy(alpha = 0.55f), accent.copy(alpha = 0.10f))
                        )
                    )
                    .glowingBorder(2.dp, accent.copy(alpha = 0.90f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * Gamer-rock themed daily-streak card. Puts the fire icon and streak text over a
 * dark mineral texture with a pulsing aqua border when a streak is active.
 */
@Composable
fun GamerStreakCard(
    currentStreak: Int,
    longestStreak: Int,
    modifier: Modifier = Modifier,
    backgroundUrl: String? = null,
) {
    val shape = RoundedCornerShape(20.dp)
    val active = currentStreak > 0
    val transition = rememberInfiniteTransition(label = "streakPulse")
    val pulse by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "streakBorderPulse",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(116.dp)
            .sculpted(shape = shape, accent = if (active) Aqua else StoneLine, shadowElevation = 6.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF3A3530), Color(0xFF2A2820), Color(0xFF1E1C16))
                )
            )
            .glowingBorder(
                3.dp,
                if (active) Aqua.copy(alpha = pulse) else StoneLine.copy(alpha = 0.75f),
                shape,
            ),
    ) {
        // Generated mineral background image — similar to the achievement tiles.
        if (!backgroundUrl.isNullOrBlank()) {
            AsyncImage(
                model = backgroundUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.55f), Color.Black.copy(alpha = 0.72f))
                        )
                    ),
            )
        }
        GamerRockTexture(
            modifier = Modifier.fillMaxSize(),
            speckleTint = if (active) Aqua.copy(alpha = 0.35f) else StoneLine.copy(alpha = 0.25f),
            veinColors = listOf(
                if (active) Aqua.copy(alpha = 0.22f) else StoneLine.copy(alpha = 0.12f),
                Citrine.copy(alpha = 0.10f),
            ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.20f),
                            Color.Black.copy(alpha = 0.40f),
                            Color.Black.copy(alpha = 0.55f),
                        )
                    )
                ),
        )
        // Glossy highlight strip at the top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(26.dp)
                .background(
                    Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.12f), Color.White.copy(alpha = 0.04f), Color.Transparent)
                    )
                ),
        )
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                if (active) Aqua.copy(alpha = 0.50f) else StoneLine.copy(alpha = 0.18f),
                                Color.Transparent,
                            )
                        )
                    )
                    .glowingBorder(
                        3.dp,
                        if (active) Aqua.copy(alpha = 0.85f) else StoneLine.copy(alpha = 0.45f),
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = if (active) Aqua else Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(30.dp),
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "DAILY STREAK",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (active) Aqua else Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                )
                Text(
                    if (active) "$currentStreak day${if (currentStreak > 1) "s" else ""} in a row"
                    else "No active streak — identify or capture a rock today!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "Longest streak: $longestStreak days",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
            }
        }
    }
}

/** Per-badge color recipe used on the catalog tile and the detail popup. */
data class BadgePalette(
    val gradient: List<Color>,
    val accent: Color,
    val secondary: Color,
    val ring: Color,
)

/**
 * Returns the unique color palette for a badge. The same palette is used on
 * the catalog tile and the popup so every badge has a consistent, recognizable
 * visual identity.
 */
fun badgePalette(badgeId: String): BadgePalette = when (badgeId) {
    // Identification milestones — trophy / medal / set square / star
    "first_10" -> BadgePalette(
        gradient = listOf(Color(0xFF3D2E10), Color(0xFF2A1F0A), Color(0xFF1A1306)),
        accent = Color(0xFFD4AF37),
        secondary = Color(0xFFE8A33D),
        ring = Color(0xFFD4AF37),
    )
    "century_club" -> BadgePalette(
        gradient = listOf(Color(0xFF3A3020), Color(0xFF282015), Color(0xFF18100A)),
        accent = Color(0xFFE8C547),
        secondary = Color(0xFFB87333),
        ring = Color(0xFFFFD700),
    )
    "rock_sage" -> BadgePalette(
        gradient = listOf(Color(0xFF0D2538), Color(0xFF081A28), Color(0xFF051018)),
        accent = Color(0xFF7CB5EC),
        secondary = Color(0xFF5CC9D8),
        ring = Color(0xFF90CAF9),
    )
    "grandmaster_hound" -> BadgePalette(
        gradient = listOf(Color(0xFF251038), Color(0xFF180A24), Color(0xFF0F0514)),
        accent = Color(0xFF9B7BD8),
        secondary = Color(0xFFD4AF37),
        ring = Color(0xFFCE93D8),
    )

    // Field capture milestones — camera / campsite / mountain
    "shutterbug" -> BadgePalette(
        gradient = listOf(Color(0xFF0D2E2A), Color(0xFF081E1C), Color(0xFF051210)),
        accent = Color(0xFF5CC9D8),
        secondary = Color(0xFF7CB5EC),
        ring = Color(0xFF80DEEA),
    )
    "field_season" -> BadgePalette(
        gradient = listOf(Color(0xFF0D2A14), Color(0xFF081A0D), Color(0xFF051207)),
        accent = Color(0xFF5CC98C),
        secondary = Color(0xFF4CAF50),
        ring = Color(0xFF81C784),
    )
    "seasoned_hunter" -> BadgePalette(
        gradient = listOf(Color(0xFF1A2A0D), Color(0xFF101A08), Color(0xFF081005)),
        accent = Color(0xFF8BC34A),
        secondary = Color(0xFF8D6E63),
        ring = Color(0xFFAED581),
    )

    // Wishlist milestones — heart / star
    "wishlist_rookie" -> BadgePalette(
        gradient = listOf(Color(0xFF2A0D16), Color(0xFF1A0A0E), Color(0xFF110609)),
        accent = Color(0xFFF48FB1),
        secondary = Color(0xFFCE93D8),
        ring = Color(0xFFF8BBD0),
    )
    "wishlist_master" -> BadgePalette(
        gradient = listOf(Color(0xFF250D2A), Color(0xFF160A1A), Color(0xFF0D0610)),
        accent = Color(0xFFCE93D8),
        secondary = Color(0xFFF48FB1),
        ring = Color(0xFFE1BEE7),
    )

    // Streak — fire
    "streak_7" -> BadgePalette(
        gradient = listOf(Color(0xFF2A1208), Color(0xFF1A0A05), Color(0xFF100603)),
        accent = Color(0xFFFF6B35),
        secondary = Color(0xFFFF8C42),
        ring = Color(0xFFFFAB91),
    )
    "streak_30" -> BadgePalette(
        gradient = listOf(Color(0xFF2A0D08), Color(0xFF1A0705), Color(0xFF100403)),
        accent = Color(0xFFFF8C42),
        secondary = Color(0xFFFFD54F),
        ring = Color(0xFFFFCC80),
    )
    "streak_100" -> BadgePalette(
        gradient = listOf(Color(0xFF2A1A08), Color(0xFF1A0F05), Color(0xFF100803)),
        accent = Color(0xFFFFD54F),
        secondary = Color(0xFFFF6B35),
        ring = Color(0xFFFFECB3),
    )

    // Dig site check-ins — map / pickaxe
    "site_hopper" -> BadgePalette(
        gradient = listOf(Color(0xFF0D2538), Color(0xFF081A28), Color(0xFF051018)),
        accent = Color(0xFF7CB5EC),
        secondary = Color(0xFF5CC9D8),
        ring = Color(0xFF90CAF9),
    )
    "dig_veteran" -> BadgePalette(
        gradient = listOf(Color(0xFF2A1F0D), Color(0xFF1A1508), Color(0xFF0F0C04)),
        accent = Color(0xFFA1887F),
        secondary = Color(0xFFFF8C42),
        ring = Color(0xFFBCAAA4),
    )

    // Journal entries — book / quill
    "journal_keeper" -> BadgePalette(
        gradient = listOf(Color(0xFF0D2A3A), Color(0xFF081C28), Color(0xFF051218)),
        accent = Color(0xFF64B5F6),
        secondary = Color(0xFF90CAF9),
        ring = Color(0xFFBBDEFB),
    )
    "chronicler" -> BadgePalette(
        gradient = listOf(Color(0xFF2A250D), Color(0xFF1A1708), Color(0xFF100D04)),
        accent = Color(0xFFFFE082),
        secondary = Color(0xFFD4AF37),
        ring = Color(0xFFFFECB3),
    )

    // Trades — swap arrows
    "trader" -> BadgePalette(
        gradient = listOf(Color(0xFF0D2A25), Color(0xFF081A16), Color(0xFF051210)),
        accent = Color(0xFF4DB6AC),
        secondary = Color(0xFF81C784),
        ring = Color(0xFFB2DFDB),
    )
    "swap_meet" -> BadgePalette(
        gradient = listOf(Color(0xFF0D2A14), Color(0xFF081A0D), Color(0xFF051207)),
        accent = Color(0xFF81C784),
        secondary = Color(0xFF4DB6AC),
        ring = Color(0xFFC8E6C9),
    )
    "baron_barter" -> BadgePalette(
        gradient = listOf(Color(0xFF2A1F0D), Color(0xFF1A1508), Color(0xFF0F0C04)),
        accent = Color(0xFFD4AF37),
        secondary = Color(0xFF81C784),
        ring = Color(0xFFFFECB3),
    )

    // Referrals — star / people / star
    "recruiter" -> BadgePalette(
        gradient = listOf(Color(0xFF1F0D2A), Color(0xFF140A1A), Color(0xFF0C0510)),
        accent = Color(0xFF9B7BD8),
        secondary = Color(0xFFD4AF37),
        ring = Color(0xFFD1C4E9),
    )
    "squad_leader" -> BadgePalette(
        gradient = listOf(Color(0xFF150D2A), Color(0xFF0D0A1A), Color(0xFF070510)),
        accent = Color(0xFF7CB5EC),
        secondary = Color(0xFF9B7BD8),
        ring = Color(0xFFC5CAE9),
    )
    "rockscout_legion" -> BadgePalette(
        gradient = listOf(Color(0xFF1F0D2A), Color(0xFF140A1A), Color(0xFF0C0510)),
        accent = Color(0xFF9B7BD8),
        secondary = Color(0xFFFFD54F),
        ring = Color(0xFFE1BEE7),
    )

    // Collection size — shop / museum
    "curator" -> BadgePalette(
        gradient = listOf(Color(0xFF2A0D16), Color(0xFF1A0A0E), Color(0xFF110609)),
        accent = Color(0xFFF48FB1),
        secondary = Color(0xFFCE93D8),
        ring = Color(0xFFF8BBD0),
    )
    "museum_keeper" -> BadgePalette(
        gradient = listOf(Color(0xFF1F0D2A), Color(0xFF140A1A), Color(0xFF0C0510)),
        accent = Color(0xFFCE93D8),
        secondary = Color(0xFFD4AF37),
        ring = Color(0xFFE1BEE7),
    )

    // Level milestones — runner / stone
    "rising_hunter" -> BadgePalette(
        gradient = listOf(Color(0xFF0D2520), Color(0xFF081612), Color(0xFF050D0A)),
        accent = Color(0xFF4DB6AC),
        secondary = Color(0xFF81C784),
        ring = Color(0xFFB2DFDB),
    )
    "senior_scout" -> BadgePalette(
        gradient = listOf(Color(0xFF1F0D2A), Color(0xFF140A1A), Color(0xFF0C0510)),
        accent = Color(0xFF9B7BD8),
        secondary = Citrine,
        ring = Color(0xFFCE93D8),
    )

    // Fossil & family — bone / quartz / agate / trophy
    "first_fossil" -> BadgePalette(
        gradient = listOf(Color(0xFF2A1F0D), Color(0xFF1A1508), Color(0xFF0F0C04)),
        accent = Color(0xFFA1887F),
        secondary = Color(0xFFFFD54F),
        ring = Color(0xFFD7CCC8),
    )
    "quartz_family" -> BadgePalette(
        gradient = listOf(Color(0xFF250D2A), Color(0xFF160A1A), Color(0xFF0D0610)),
        accent = Color(0xFFF48FB1),
        secondary = Color(0xFFCE93D8),
        ring = Color(0xFFF8BBD0),
    )
    "agate_hunter" -> BadgePalette(
        gradient = listOf(Color(0xFF2A0D10), Color(0xFF1A0709), Color(0xFF100405)),
        accent = Color(0xFFFF6B35),
        secondary = Color(0xFF9B7BD8),
        ring = Color(0xFFFFAB91),
    )
    "completionist" -> BadgePalette(
        gradient = listOf(Color(0xFF2A1F0D), Color(0xFF1A1508), Color(0xFF0F0C04)),
        accent = Color(0xFFD4AF37),
        secondary = Color(0xFFFF6B35),
        ring = Color(0xFFFFECB3),
    )

    else -> BadgePalette(
        gradient = listOf(Color(0xFF2A2418), Color(0xFF1E1A12), Color(0xFF16130C)),
        accent = Citrine,
        secondary = Color(0xFFE8A33D),
        ring = Color(0xFFD4AF37),
    )
}

/**
 * Gamer-rock themed badge tile for the Achievements badge catalog. Each badge
 * uses its own unique gradient palette so the catalog feels like a collection of
 * distinct trophies. The same palette is reused in the badge detail popup and the
 * badge-earned celebration. A strong dark scrim and text backing keep the emoji
 * and badge name readable for both earned and locked states.
 */
@Composable
fun GamerBadgeTile(
    badge: Badge,
    earned: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(14.dp)
    val transition = rememberInfiniteTransition(label = "badgeGlow")
    val glow by transition.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.30f,
        animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Reverse),
        label = "badgeGlowAlpha",
    )

    val palette = badgePalette(badge.id)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .sculpted(shape = shape, accent = if (earned) palette.ring else StoneLine, shadowElevation = 5.dp, onClick = onClick)
            .clip(shape)
            .background(
                Brush.verticalGradient(palette.gradient)
            )
            .glowingBorder(
                2.dp,
                if (earned) palette.ring.copy(alpha = 0.85f) else StoneLine.copy(alpha = 0.45f),
                shape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        // Badge-specific radial glow — large and vivid so the palette is unmistakable.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(
                            palette.accent.copy(alpha = if (earned) 0.62f + glow else 0.28f),
                            palette.secondary.copy(alpha = if (earned) 0.38f else 0.16f),
                            palette.gradient.last().copy(alpha = 0.95f),
                        )
                    )
                ),
        )
        // Mineral texture tinted by the badge palette so the tile feels like
        // a real stone slab rather than a flat color swatch.
        GamerRockTexture(
            modifier = Modifier.fillMaxSize(),
            speckleTint = palette.accent.copy(alpha = 0.55f),
            veinColors = listOf(
                palette.accent.copy(alpha = 0.32f),
                palette.secondary.copy(alpha = 0.26f),
                palette.ring.copy(alpha = 0.20f),
            ),
        )
        // Locked badges are slightly muted so earned ones still pop.
        if (!earned) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF2A2820).copy(alpha = 0.35f)),
            )
        }
        // Light scrim only at the bottom so the text pill stays legible without
        // washing out the badge color across the whole tile.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.00f),
                            Color.Black.copy(alpha = 0.12f),
                            Color.Black.copy(alpha = 0.30f),
                        )
                    )
                ),
        )
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Glowing mineral core behind the emoji — stronger glow and border so the
        // badge icon stands out clearly even on vivid palette backgrounds.
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                palette.accent.copy(alpha = if (earned) 0.80f else 0.45f),
                                palette.secondary.copy(alpha = if (earned) 0.40f else 0.18f),
                                Color.Transparent,
                            )
                        )
                    )
                    .glowingBorder(
                        2.5.dp,
                        palette.ring.copy(alpha = if (earned) 0.95f else 0.60f),
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    badge.emoji,
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (earned) Color.White else Color.White.copy(alpha = 0.85f),
                )
            }
            Spacer(Modifier.height(6.dp))
            // Text sits on a dark, opaque pill with a bright accent border so the badge
            // name is always readable regardless of the tile background behind it.
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xE6000000))
                    .glowingBorder(1.5.dp, palette.ring.copy(alpha = if (earned) 0.85f else 0.50f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    badge.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
            }
        }
    }
}

/** Icon presets for the four stat pills on the Achievements screen. */
object GamerStatIcons {
    val Identified: ImageVector = Icons.Filled.Search
    val Captured: ImageVector = Icons.Filled.CameraAlt
    val Badges: ImageVector = Icons.Filled.EmojiEvents
    val Streak: ImageVector = Icons.Filled.LocalFireDepartment
    val Xp: ImageVector = Icons.Filled.Star
}

/** Accent colors for the four stat pills. */
object GamerStatAccents {
    val Identified = Color(0xFF7CB5EC)
    val Captured = Color(0xFF5CC98C)
    val Badges = Citrine
    val Streak = Aqua
    val Xp = Color(0xFF9B7BD8)
}

/**
 * Unlimited XP bar with a full-width progress bar underneath. The progress bar
 * always reads full (animates to 100% on each new upload) to visually confirm
 * the XP was earned. Shows a counter of completed actions.
 */
@Composable
fun UnlimitedXpBarWithProgress(
    title: String,
    subtitle: String,
    count: Int,
    emoji: String,
    accent: Color,
    modifier: Modifier = Modifier,
    backgroundUrl: String? = null,
) {
    val shape = RoundedCornerShape(16.dp)
    val transition = rememberInfiniteTransition(label = "unlimitedBarPulse")
    val pulse by transition.animateFloat(
        initialValue = 0.40f,
        targetValue = 0.80f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse),
        label = "unlimitedBarPulseAlpha",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .sculpted(shape = shape, accent = accent, shadowElevation = 5.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF3A3530), Color(0xFF2A2820), Color(0xFF1E1C16))
                )
            )
            .glowingBorder(2.dp, accent.copy(alpha = pulse), shape),
    ) {
        // Generated mineral background image — similar to the achievement tiles.
        if (!backgroundUrl.isNullOrBlank()) {
            AsyncImage(
                model = backgroundUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.60f), Color.Black.copy(alpha = 0.78f))
                        )
                    ),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(accent.copy(alpha = 0.50f), accent.copy(alpha = 0.10f))
                        )
                    )
                    .glowingBorder(2.dp, accent.copy(alpha = 0.85f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(emoji, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = accent,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xCC1E1C16))
                    .glowingBorder(1.5.dp, accent.copy(alpha = 0.70f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    "$count done",
                    style = MaterialTheme.typography.labelMedium,
                    color = accent,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
        // Full progress bar — always reads full since every action earns XP
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0x22FFFFFF)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(listOf(accent, accent.copy(alpha = 0.7f)))
                    ),
            )
        }
    }
}
}

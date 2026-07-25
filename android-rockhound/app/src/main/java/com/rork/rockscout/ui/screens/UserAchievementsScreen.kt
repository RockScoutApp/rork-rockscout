package com.rork.rockscout.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rork.rockscout.data.AchievementsRepository
import com.rork.rockscout.data.AchievementsState
import com.rork.rockscout.data.Badge
import com.rork.rockscout.data.LevelTier
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.R
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.GamerBadgeTile
import com.rork.rockscout.ui.components.GamerRockTexture
import com.rork.rockscout.ui.components.badgePalette
import com.rork.rockscout.ui.components.GamerStatAccents
import com.rork.rockscout.ui.components.GamerStatIcons
import com.rork.rockscout.ui.components.GamerStatPill
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.Slate700
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.StoneLine
import com.rork.rockscout.ui.theme.TextHigh

/**
 * Read-only achievements view for another user. Shows their level, tier, XP,
 * and badge catalog with approximate earned state. No mutations or persistence.
 */
@Composable
fun UserAchievementsScreen(
    navController: NavController,
    userId: String,
) {
    val social = SocialRepository.instance
    var user by remember { mutableStateOf<SocialRepository.HunterProfile?>(null) }
    var synthesizedState by remember { mutableStateOf<AchievementsState?>(null) }
    var selectedBadge by remember { mutableStateOf<Badge?>(null) }

    BackHandler(enabled = selectedBadge != null) { selectedBadge = null }

    LaunchedEffect(userId) {
        val profiles = social.fetchProfiles(listOf(userId))
        val profile = profiles.firstOrNull()
        user = profile
        if (profile != null) {
            val fullUser = social.fetchUserProfile(userId)
            val xp = fullUser?.xp ?: 0
            val level = fullUser?.level ?: profile.level
            synthesizedState = AchievementsRepository.synthesizeStateForUser(level, xp)
        }
    }

    val h = user
    val state = synthesizedState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A1812), Obsidian, Color(0xFF0F0E0A))
                )
            ),
    ) {
        // Gamer-style mineral texture overlay — speckles, glowing flecks, veins
        GamerRockTexture(modifier = Modifier.fillMaxSize())
        // Subtle starburst sparkles layered over the gamer texture.
        com.rork.rockscout.ui.components.TwinklingStars(
            starCount = 20,
            maxAlpha = 0.4f,
        )

        if (h == null || state == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("Loading…", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.6f))
            }
            return
        }

        val level = AchievementsRepository.levelForXp(state.totalXp)
        val tier = LevelTier.forLevel(level)

        LazyColumn(
            modifier = Modifier.fillMaxSize().navigationBarsPadding(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp, end = 20.dp, top = 56.dp, bottom = 80.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SculptedIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = { navController.popBackStack() },
                        accent = Citrine,
                        iconTint = Color.White,
                    )
                    Text(
                        text = "${h.display_name}'s Achievements",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    )
                }
            }

            item {
                UserGamerHeader(
                    avatarEmoji = h.avatar_emoji,
                    name = h.display_name,
                    level = level,
                    tier = tier,
                    totalXp = state.totalXp,
                    progress = AchievementsRepository.levelProgress(state.totalXp),
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GamerStatPill("Identified", state.identifyCount.toString(), GamerStatIcons.Identified, GamerStatAccents.Identified, Modifier.weight(1f))
                    GamerStatPill("Captured", state.captureCount.toString(), GamerStatIcons.Captured, GamerStatAccents.Captured, Modifier.weight(1f))
                    GamerStatPill("Badges", state.earnedBadgeIds.size.toString(), GamerStatIcons.Badges, GamerStatAccents.Badges, Modifier.weight(1f))
                    GamerStatPill("XP", state.totalXp.toString(), GamerStatIcons.Xp, GamerStatAccents.Xp, Modifier.weight(1f))
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xCC000000))
                        .glowingBorder(2.dp, Citrine.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        "BADGE CATALOG",
                        style = MaterialTheme.typography.labelLarge,
                        color = Citrine,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Badge progress is approximate — shown from public level data.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.80f),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }

            item {
                UserBadgeGrid(
                    earnedIds = state.earnedBadgeIds,
                    onBadgeTap = { selectedBadge = it },
                )
            }
        }
    }

    selectedBadge?.let { badge ->
        UserBadgeDetailOverlay(
            badge = badge,
            earned = badge.id in (synthesizedState?.earnedBadgeIds ?: emptySet()),
            onDismiss = { selectedBadge = null },
        )
    }
}


@Composable
private fun UserGamerHeader(
    avatarEmoji: String,
    name: String,
    level: Int,
    tier: LevelTier,
    totalXp: Int,
    progress: Float,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = RoundedCornerShape(20.dp), accent = Citrine, shadowElevation = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .glowingBorder(3.dp, Citrine.copy(alpha = 0.55f), RoundedCornerShape(20.dp)),
    ) {
        // High-res colorful agate slice background — fills the level tile, cropped to the card shape
        Image(
            painter = painterResource(id = R.drawable.level_tile_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        // Dark scrim so the white text, Citrine/Aqua accents, and XP bar stay legible
        // over the colorful stone background. The gradient darkens toward the bottom where the
        // XP bar and row labels sit, while still letting the agate colors show through.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.42f),
                            Color.Black.copy(alpha = 0.50f),
                            Color.Black.copy(alpha = 0.60f),
                        )
                    )
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Citrine.copy(alpha = 0.18f), Citrine.copy(alpha = 0.04f), Color.Transparent)
                    )
                ),
        )
        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Citrine.copy(alpha = 0.5f), Aqua.copy(alpha = 0.3f))))
                        .glowingBorder(4.dp, Citrine.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(avatarEmoji, style = MaterialTheme.typography.displaySmall)
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "LV",
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkTextMid,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        level.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        color = Citrine,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 48.sp,
                        lineHeight = 48.sp,
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            val nextLevelXp = AchievementsRepository.xpForLevel(level + 1)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Citrine.copy(alpha = 0.25f))
                        .glowingBorder(2.dp, Citrine.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(tier.emoji, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            tier.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xCC1E1C16))
                        .glowingBorder(2.dp, Aqua.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Text(
                        "$totalXp XP",
                        style = MaterialTheme.typography.titleMedium,
                        color = Aqua,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0x22FFFFFF)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Brush.horizontalGradient(listOf(Citrine, Aqua))),
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "${AchievementsRepository.xpForLevel(level)} XP at Level $level",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTextLow,
                    fontWeight = FontWeight.Bold,
                )
                // XP-to-level chip removed per plan — total XP is shown above
            }
        }
    }
}

@Composable
private fun UserBadgeGrid(
    earnedIds: Set<String>,
    onBadgeTap: (Badge) -> Unit,
) {
    val catalog = AchievementsRepository.badgeCatalog
    val columns = 3
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        catalog.chunked(columns).forEach { rowBadges ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowBadges.forEach { badge ->
                    val earned = badge.id in earnedIds
                    GamerBadgeTile(
                        badge = badge,
                        earned = earned,
                        onClick = { onBadgeTap(badge) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columns - rowBadges.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun UserBadgeDetailOverlay(badge: Badge, earned: Boolean, onDismiss: () -> Unit) {
    val palette = badgePalette(badge.id)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .sculpted(shape = RoundedCornerShape(20.dp), accent = if (earned) palette.ring else Color(0xFF6A6258), shadowElevation = 6.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(palette.gradient)
                )
                .glowingBorder(
                    1.5.dp,
                    if (earned) palette.ring.copy(alpha = 0.6f) else Color(0xFF6A6258),
                    RoundedCornerShape(20.dp),
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(badge.emoji, style = MaterialTheme.typography.displaySmall)
            Spacer(Modifier.height(10.dp))
            Text(
                badge.name,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (earned) palette.accent.copy(alpha = 0.20f) else Color(0x22FFFFFF),
                    )
                    .glowingBorder(1.dp, palette.accent.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Text(
                    if (earned) "EARNED" else "LOCKED",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (earned) palette.accent else DarkTextMid,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                badge.description,
                style = MaterialTheme.typography.bodyLarge,
                color = DarkTextHigh,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Tap anywhere to close",
                style = MaterialTheme.typography.labelSmall,
                color = DarkTextLow,
            )
        }
    }
}

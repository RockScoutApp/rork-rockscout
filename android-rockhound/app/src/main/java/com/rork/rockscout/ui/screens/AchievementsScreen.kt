package com.rork.rockscout.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalFireDepartment
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.data.AchievementBackgrounds
import com.rork.rockscout.data.AchievementsRepository
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.Badge
import com.rork.rockscout.data.LevelTier
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.R
import com.rork.rockscout.ui.components.GamerBadgeTile
import com.rork.rockscout.ui.components.GamerRockTexture
import com.rork.rockscout.ui.components.GamerStatAccents
import com.rork.rockscout.ui.components.GamerStatIcons
import com.rork.rockscout.ui.components.GamerStatPill
import com.rork.rockscout.ui.components.GamerStreakCard
import com.rork.rockscout.ui.components.UnlimitedXpBarWithProgress
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.badgePalette
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate700
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.StoneLine
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextLow

@Composable
fun AchievementsScreen(
    navController: NavController,
    scrollToBadges: Boolean = false,
) {
    val state by AchievementsRepository.state.collectAsStateWithLifecycle()
    val level by AchievementsRepository.level.collectAsStateWithLifecycle()
    val tier by AchievementsRepository.tier.collectAsStateWithLifecycle()
    val profile by AppRepository.instance.profile.collectAsStateWithLifecycle()
    var selectedBadge by remember { mutableStateOf<Badge?>(null) }
    val listState = rememberLazyListState()

    // The badge catalog header item index — used for scrollToBadges navigation.
    // Items: 0 = top bar, 1 = GamerHeader, 2 = Unlimited Field Capture XP bar,
    // 3 = Unlimited Premium Referral XP bar, 4 = stat pills, 5 = streak card,
    // 6 = badge catalog header.
    val badgeHeaderIndex = 6

    BackHandler(enabled = selectedBadge != null) { selectedBadge = null }

    androidx.compose.runtime.LaunchedEffect(scrollToBadges) {
        if (scrollToBadges) {
            listState.animateScrollToItem(badgeHeaderIndex)
        }
    }

    // Full-screen dark rock-strata background with custom top bar.
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

        LazyColumn(
            state = listState,
            // Keep the scrollable area above the gesture navigation bar so
            // scrolling to the bottom doesn't accidentally trigger the system
            // home gesture / app minimize.
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp, end = 20.dp, top = 56.dp, bottom = 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Top bar row
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
                        text = "Player Card",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Gamer-style header banner: level image + centered name/location + tier + XP bar
            item {
                GamerHeader(
                    avatarEmoji = profile.avatarEmoji,
                    name = profile.name,
                    homeRegion = profile.homeRegion,
                    level = level,
                    tier = tier,
                    totalXp = state.totalXp,
                    progress = AchievementsRepository.levelProgress(state.totalXp),
                    earnedBadges = AchievementsRepository.badgeCatalog.filter { it.id in state.earnedBadgeIds },
                    onViewAllAchievements = { navController.navigate(Routes.allAchievements()) },
                )
            }

            // Unlimited Field Capture XP bar — always at the top
            item {
                UnlimitedXpBarWithProgress(
                    title = "Unlimited Field Capture XP",
                    subtitle = "25 XP per upload",
                    count = state.captureCount,
                    emoji = "\uD83D\uDCF7",
                    accent = Color(0xFF5CC98C),
                    backgroundUrl = AchievementBackgrounds.profileTileUrls.fieldCaptureXp,
                )
            }

            // Unlimited Premium Referral XP bar — right below the field capture bar
            item {
                UnlimitedXpBarWithProgress(
                    title = "Unlimited Premium Referral XP",
                    subtitle = "500 XP per completed referral",
                    count = state.premiumReferralCount,
                    emoji = "\uD83D\uDCB0",
                    accent = Color(0xFFD4AF37),
                    backgroundUrl = AchievementBackgrounds.profileTileUrls.premiumReferralXp,
                )
            }

            // Gamer-rock stats row — Identified, Captured, Badges, Streak
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GamerStatPill("Identified", state.identifyCount.toString(), GamerStatIcons.Identified, GamerStatAccents.Identified, Modifier.weight(1f), backgroundUrl = AchievementBackgrounds.profileTileUrls.identifiedStat)
                    GamerStatPill("Captured", state.captureCount.toString(), GamerStatIcons.Captured, GamerStatAccents.Captured, Modifier.weight(1f), backgroundUrl = AchievementBackgrounds.profileTileUrls.capturedStat)
                    GamerStatPill("Badges", state.earnedBadgeIds.size.toString(), GamerStatIcons.Badges, GamerStatAccents.Badges, Modifier.weight(1f), backgroundUrl = AchievementBackgrounds.profileTileUrls.badgesStat)
                    GamerStatPill("Streak", state.currentStreak.toString(), GamerStatIcons.Streak, GamerStatAccents.Streak, Modifier.weight(1f), backgroundUrl = AchievementBackgrounds.profileTileUrls.streakStat)
                }
            }

            // Gamer-rock streak card
            item {
                GamerStreakCard(
                    currentStreak = state.currentStreak,
                    longestStreak = state.longestStreak,
                    backgroundUrl = AchievementBackgrounds.profileTileUrls.dailyStreak,
                )
            }

            // Badge catalog header
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
            }

            // Gamer-rock badge grid — non-lazy Column of Rows (fixes nested scroll bug)
            item {
                NonLazyBadgeGrid(
                    earnedIds = state.earnedBadgeIds,
                    onBadgeTap = { selectedBadge = it },
                )
            }
        }
    }

    // Badge detail dialog
    selectedBadge?.let { badge ->
        BadgeDetailOverlay(
            badge = badge,
            earned = badge.id in state.earnedBadgeIds,
            onDismiss = { selectedBadge = null },
        )
    }
}

@Composable
private fun GamerHeader(
    avatarEmoji: String,
    name: String,
    homeRegion: String,
    level: Int,
    tier: LevelTier,
    totalXp: Int,
    progress: Float,
    earnedBadges: List<Badge>,
    onViewAllAchievements: () -> Unit,
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
        // Strong dark scrim so the text, badges, and XP labels stay legible over the
        // colorful stone background. The gradient is heavier than before because the
        // circled labels (region, tier, XP endpoints) were getting lost in the agate.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.55f),
                            Color.Black.copy(alpha = 0.62f),
                            Color.Black.copy(alpha = 0.70f),
                            Color.Black.copy(alpha = 0.78f),
                        )
                    )
                ),
        )
        // Glow overlay
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
            // ─── Top row: large Lvl image on the left, centered name/location, avatar on the right ───
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Large flashy "Lvl xx" level image — no border, gradient glow behind the number.
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Citrine.copy(alpha = 0.55f),
                                    Citrine.copy(alpha = 0.15f),
                                    Color.Transparent,
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Lvl",
                            style = MaterialTheme.typography.labelMedium,
                            color = Citrine,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                        )
                        Text(
                            level.toString(),
                            style = MaterialTheme.typography.displayLarge,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 42.sp,
                            lineHeight = 40.sp,
                        )
                    }
                }
                // Centered username + location
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                    // Region chip with a dark backing so it survives the busy background.
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xCC1E1C16))
                            .glowingBorder(1.5.dp, Citrine.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            homeRegion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                // Small avatar on the right
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Citrine.copy(alpha = 0.5f), Aqua.copy(alpha = 0.3f))))
                        .glowingBorder(3.dp, Citrine.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(avatarEmoji, style = MaterialTheme.typography.headlineSmall)
                }
            }
            // ─── Earned badges strip ───
            if (earnedBadges.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    earnedBadges.take(12).forEach { badge ->
                        val palette = badgePalette(badge.id)
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.verticalGradient(palette.gradient))
                                .glowingBorder(2.dp, palette.ring.copy(alpha = 0.85f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            listOf(palette.accent.copy(alpha = 0.50f), Color.Transparent)
                                        )
                                    ),
                            )
                            Text(
                                badge.emoji,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            val nextLevelXp = AchievementsRepository.xpForLevel(level + 1)
            // Tier name + emoji
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Tier badge with a solid dark backing so the name and emoji stay readable
                // against the colorful agate background.
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xDD1E1C16))
                        .glowingBorder(2.dp, Citrine.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
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
                        .background(Color(0xDD1E1C16))
                        .glowingBorder(2.dp, Aqua.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
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
            // XP progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0x22FFFFFF))
                    .glowingBorder(1.5.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(5.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xFFFFD54F)),
                )
            }
            Spacer(Modifier.height(10.dp))
            // Bottom row: View all achievements (left) + XP-to-next-level counter (right)
            val currentLevelXp = AchievementsRepository.xpForLevel(level)
            val xpIntoLevel = totalXp - currentLevelXp
            val xpNeeded = nextLevelXp - currentLevelXp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xE61E1C16))
                        .glowingBorder(2.dp, Citrine.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                        .clickable(onClick = onViewAllAchievements)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        "View all achievements",
                        style = MaterialTheme.typography.labelMedium,
                        color = Citrine,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xE61E1C16))
                        .glowingBorder(2.dp, Aqua.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        "$xpIntoLevel / $xpNeeded XP to Lvl ${level + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Aqua,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }
    }
}


@Composable
private fun NonLazyBadgeGrid(
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
                // Fill empty slots to keep grid aligned
                repeat(columns - rowBadges.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun BadgeDetailOverlay(badge: Badge, earned: Boolean, onDismiss: () -> Unit) {
    val palette = badgePalette(badge.id)
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + scaleIn(initialScale = 0.9f),
        exit = fadeOut() + scaleOut(targetScale = 0.9f),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6000000))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            DarkCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                accent = if (earned) palette.ring else Color(0xFF6A6258),
            ) {
                // Replace the generic dark card background with the badge-specific gradient
                // while keeping the sculpted border from DarkCard.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(palette.gradient)),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
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
        }
    }
}

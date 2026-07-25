package com.rork.rockscout.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.R
import com.rork.rockscout.data.Achievement
import com.rork.rockscout.data.AchievementBackgrounds
import com.rork.rockscout.data.AchievementCatalog
import com.rork.rockscout.data.AchievementsRepository
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.GamerRockTexture
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.StoneLine
import coil3.compose.AsyncImage

/**
 * Full-screen scrollable page that lists every achievement in the catalog
 * with its progress bar and earned/locked state. This is a separate route
 * from the Player Card (level/badge screen) so the two are independent.
 *
 * Has a close button at the top and a close button at the bottom.
 */
@Composable
fun AllAchievementsScreen(
    navController: NavController,
) {
    val state by AchievementsRepository.state.collectAsStateWithLifecycle()
    val level by AchievementsRepository.level.collectAsStateWithLifecycle()

    BackHandler(enabled = true) { navController.popBackStack() }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Bright, celebratory full-page rock background.
        Image(
            painter = painterResource(id = R.drawable.agate_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.42f), Color.Black.copy(alpha = 0.68f), Color.Black.copy(alpha = 0.85f))
                    )
                ),
        )
        // Subtle mineral texture overlay for extra depth.
        GamerRockTexture(
            modifier = Modifier.fillMaxSize(),
            speckleTint = Citrine.copy(alpha = 0.18f),
            veinColors = listOf(Aqua.copy(alpha = 0.08f), Citrine.copy(alpha = 0.06f)),
        )
        // Subtle starburst sparkles over the celebratory background.
        com.rork.rockscout.ui.components.TwinklingStars(
            starCount = 20,
            maxAlpha = 0.4f,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
        ) {
            // ─── Top bar with close button ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2A2820))
                        .glowingBorder(2.dp, Citrine.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "All Achievements",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }

            // ─── Scrollable achievement list ───
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(AchievementCatalog.all, key = { it.id }) { achievement ->
                    val progress = AchievementCatalog.progressFor(achievement, state, level)
                    val earned = AchievementCatalog.isEarned(achievement, state, level)
                    AllAchievementsItem(
                        achievement = achievement,
                        progress = progress,
                        earned = earned,
                    )
                }
            }

            // ─── Bottom close button ───
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                SculptedButton(
                    text = "Close",
                    onClick = { navController.popBackStack() },
                    accent = Citrine,
                    containerColor = Color(0xFF2A2820),
                    textColor = Color.White,
                    icon = Icons.Filled.Close,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun AllAchievementsItem(
    achievement: Achievement,
    progress: Int,
    earned: Boolean,
) {
    val accent = if (earned) Citrine else StoneLine
    val transition = rememberInfiniteTransition(label = "achievementGlow")
    val glow by transition.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "achievementGlowAlpha",
    )
    // Bright, saturated tile gradient: earned tiles blaze golden-brown, locked tiles stay cool stone.
    val tileGradient = if (earned) {
        Brush.horizontalGradient(
            listOf(Color(0xFF5C3D0B).copy(alpha = 0.98f), Color(0xFF3D2E10).copy(alpha = 0.98f), Color(0xFF2A1F0A).copy(alpha = 0.98f), Color(0xFF1A1306).copy(alpha = 0.98f))
        )
    } else {
        Brush.horizontalGradient(
            listOf(Color(0xFF3D3A30).copy(alpha = 0.92f), Color(0xFF2A2820).copy(alpha = 0.92f), Color(0xFF1E1C16).copy(alpha = 0.92f))
        )
    }
    val bgUrl = AchievementBackgrounds.urlFor(achievement)

    DarkCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        accent = accent,
        contentPadding = PaddingValues(0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(tileGradient),
        ) {
            // Generated blue-outline mineral background image when available.
            if (!bgUrl.isNullOrBlank()) {
                AsyncImage(
                    model = bgUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (earned) {
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF5C3D0B).copy(alpha = 0.82f), Color(0xFF3D2E10).copy(alpha = 0.85f), Color(0xFF1A1306).copy(alpha = 0.90f))
                                )
                            } else {
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF3D3A30).copy(alpha = 0.82f), Color(0xFF2A2820).copy(alpha = 0.86f), Color(0xFF1E1C16).copy(alpha = 0.92f))
                                )
                            }
                        ),
                )
            }
            // Mineral texture overlay — vivid for earned, subtle for locked.
            GamerRockTexture(
                modifier = Modifier.fillMaxSize(),
                speckleTint = if (earned) Citrine.copy(alpha = 0.75f) else StoneLine.copy(alpha = 0.45f),
                veinColors = if (earned) {
                    listOf(Citrine.copy(alpha = 0.45f), Color(0xFFFFD54F).copy(alpha = 0.35f), Color(0xFFFFA000).copy(alpha = 0.22f))
                } else {
                    listOf(Color(0xFF7CB5EC).copy(alpha = 0.28f), StoneLine.copy(alpha = 0.22f), Color(0xFF5A7A9A).copy(alpha = 0.15f))
                },
            )
            // Pulsing radial glow — golden for earned, cool blue for locked.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            if (earned) {
                                listOf(Citrine.copy(alpha = 0.45f + glow), Color(0xFFFFD54F).copy(alpha = 0.20f + glow * 0.4f), Color(0xFFFFA000).copy(alpha = 0.08f), Color.Transparent)
                            } else {
                                listOf(Color(0xFF7CB5EC).copy(alpha = 0.22f + glow), StoneLine.copy(alpha = 0.12f + glow * 0.5f), Color.Transparent)
                            }
                        )
                    ),
            )
            // Locked overlay is lighter so the texture still shows through.
            if (!earned) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF2A2820).copy(alpha = 0.18f)),
                )
            }
            // Glossy highlight strip at the top.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(Color.White.copy(alpha = 0.16f), Color.White.copy(alpha = 0.06f), Color.Transparent)
                        )
                    ),
            )
            // Sparkle / shimmer sweep across the tile surface.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.08f + glow * 0.15f),
                                Color.White.copy(alpha = 0.16f + glow * 0.25f),
                                Color.White.copy(alpha = 0.08f + glow * 0.15f),
                                Color.Transparent,
                            )
                        )
                    ),
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (earned) {
                                Brush.radialGradient(
                                    listOf(Citrine.copy(alpha = 0.55f), Citrine.copy(alpha = 0.15f), Color.Transparent)
                                )
                            } else {
                                Brush.radialGradient(
                                    listOf(StoneLine.copy(alpha = 0.25f), Color.Transparent)
                                )
                            }
                        )
                        .glowingBorder(2.5.dp, accent, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(achievement.emoji, style = MaterialTheme.typography.titleLarge)
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            achievement.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            if (earned) "+${achievement.rewardXp} XP" else "${achievement.rewardXp} XP",
                            style = MaterialTheme.typography.labelSmall,
                            color = accent,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.height(1.dp))
                    Text(
                        achievement.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (earned) Color.White.copy(alpha = 0.85f) else DarkTextMid,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0x22FFFFFF)),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth((progress.toFloat() / achievement.threshold.toFloat()).coerceIn(0f, 1f))
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(accent),
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            "$progress / ${achievement.threshold}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (earned) accent else DarkTextMid,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            if (earned) "EARNED" else "LOCKED",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (earned) accent else DarkTextMid,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

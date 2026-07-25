package com.rork.rockscout.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rork.rockscout.data.AchievementCelebrationData
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Ink
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * Full-screen celebratory overlay for achievement completions.
 *
 * Mirrors the [LevelUpCelebration] visual language but uses amethyst/purple
 * accents to distinguish achievement unlocks from level-ups. Shows a geode
 * burst with faceted crystal shards, a glowing mineral core, the achievement
 * name + description, and the bonus XP awarded. Auto-dismisses after 5 seconds.
 */
@Composable
fun AchievementCelebration(
    data: AchievementCelebrationData,
    onShare: () -> Unit,
    onShareToProfile: () -> Unit = {},
    onDismiss: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    var burst by remember { mutableStateOf(false) }
    var shardProgress by remember { mutableStateOf(0f) }

    // Play burst once on entry — no auto-dismiss; stays until user closes
    LaunchedEffect(Unit) {
        visible = true
        delay(150)
        burst = true
        val steps = 24
        for (i in 0..steps) {
            shardProgress = i / steps.toFloat()
            delay(50)
        }
    }

    val accent = Amethyst
    val secondary = Citrine
    val tertiary = Aqua

    val transition = rememberInfiniteTransition(label = "achCelebrationGlow")
    val glow by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "achGlowAlpha",
    )
    val corePulse by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "achCorePulse",
    )
    val shimmerRotate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Restart),
        label = "achShimmerRotate",
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(200)) + scaleIn(initialScale = 0.7f, animationSpec = tween(400)),
        exit = fadeOut(tween(300)) + scaleOut(targetScale = 0.85f),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6000000))
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center,
        ) {
            // ─── Looping fireworks behind the content ───
            FireworksOverlay(
                colors = listOf(accent, secondary, tertiary),
                modifier = Modifier,
            )

            // ─── Radiating crystal shard burst ───
            if (burst) {
                val shardCount = 14
                for (i in 0 until shardCount) {
                    val angleDeg = (360f / shardCount) * i
                    val angleRad = Math.toRadians(angleDeg.toDouble())
                    val distance = 120f + 180f * shardProgress
                    val dx = (cos(angleRad) * distance).toFloat()
                    val dy = (sin(angleRad) * distance).toFloat()
                    val shardColor = when (i % 3) {
                        0 -> accent
                        1 -> secondary
                        else -> tertiary
                    }
                    Box(
                        modifier = Modifier
                            .offset(x = dx.dp, y = dy.dp)
                            .size((14f + 10f * (1f - shardProgress)).dp)
                            .rotate(angleDeg + shardProgress * 180f)
                            .scale(1f - shardProgress * 0.4f)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        shardColor.copy(alpha = (1f - shardProgress * 0.7f)),
                                        shardColor.copy(alpha = 0.1f),
                                    ),
                                ),
                            ),
                    )
                }
            }

            // ─── Outer geode glow rings ───
            if (burst) {
                Box(
                    modifier = Modifier
                        .size((280 * corePulse).dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    accent.copy(alpha = glow * 0.6f),
                                    secondary.copy(alpha = glow * 0.2f),
                                    Color.Transparent,
                                ),
                            ),
                        ),
                ) {}
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .rotate(shimmerRotate)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color.Transparent,
                                    accent.copy(alpha = 0.3f),
                                    Color.Transparent,
                                    secondary.copy(alpha = 0.3f),
                                    Color.Transparent,
                                    tertiary.copy(alpha = 0.25f),
                                    Color.Transparent,
                                ),
                            ),
                        ),
                ) {}
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .heightIn(min = 300.dp, max = 460.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF241628), Color(0xFF1A121E), Color(0xFF130E16)),
                        ),
                    )
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            ) {
                // "ACHIEVEMENT UNLOCKED" label with sparkle icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "ACHIEVEMENT UNLOCKED",
                        style = MaterialTheme.typography.labelLarge,
                        color = accent,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(16.dp),
                    )
                }

                Spacer(Modifier.height(14.dp))

                // Faceted mineral core badge with achievement emoji
                Box(
                    modifier = Modifier
                        .size((84 * corePulse).dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    accent.copy(alpha = 0.6f),
                                    secondary.copy(alpha = 0.3f),
                                ),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .rotate(shimmerRotate)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.4f),
                                        Color.Transparent,
                                    ),
                                ),
                            ),
                    )
                    Text(data.emoji, style = MaterialTheme.typography.headlineLarge)
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = data.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )

                Text(
                    text = data.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )

                // XP reward badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(accent.copy(alpha = 0.3f), secondary.copy(alpha = 0.2f)),
                            ),
                        )
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "+${data.rewardXp} XP",
                        style = MaterialTheme.typography.labelLarge,
                        color = secondary,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SculptedButton(
                        text = "Share",
                        onClick = {
                            visible = false
                            onShare()
                        },
                        modifier = Modifier.height(44.dp),
                        accent = accent,
                        containerColor = accent,
                        textColor = Color.White,
                        icon = Icons.Filled.Share,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        textStyle = MaterialTheme.typography.labelMedium,
                    )
                    SculptedButton(
                        text = "Add to Profile",
                        onClick = {
                            visible = false
                            onShareToProfile()
                        },
                        modifier = Modifier.height(44.dp),
                        accent = Citrine,
                        containerColor = Citrine,
                        textColor = Ink,
                        icon = Icons.Filled.PersonAdd,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        textStyle = MaterialTheme.typography.labelMedium,
                    )
                    SculptedButton(
                        text = "Close",
                        onClick = {
                            visible = false
                            onDismiss()
                        },
                        modifier = Modifier.height(44.dp),
                        accent = accent,
                        containerColor = Color.Black,
                        textColor = Color.White,
                        icon = Icons.Filled.Close,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        textStyle = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

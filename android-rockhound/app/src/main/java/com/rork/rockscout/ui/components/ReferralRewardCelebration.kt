package com.rork.rockscout.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.unit.sp
import com.rork.rockscout.data.ReferralRepository
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.CitrineDeep
import com.rork.rockscout.ui.theme.Cyan
import com.rork.rockscout.ui.theme.Ink
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * Full-screen celebratory overlay for referral reward events.
 *
 * Two modes:
 *  - **New user (referred)**: shown when a new user enters a referral code on the
 *    sign-in screen and their welcome gifts are confirmed. Uses aqua/cyan accents
 *    to signal "welcome to the community."
 *  - **Sender (referrer)**: shown when a sender's referral completion is credited
 *    while they were away. Uses citrine/gold accents to signal "reward earned."
 *
 * Both modes feature a radiating crystal shard burst, a glowing mineral core,
 * reward details, and a dismiss button. Auto-dismisses after 6 seconds.
 */
@Composable
fun ReferralRewardCelebration(
    data: ReferralRepository.NewUserRewardData,
    onDismiss: () -> Unit,
) {
    ReferralCelebrationContent(
        accent = Cyan,
        secondary = Aqua,
        tertiary = Amethyst,
        iconVector = Icons.Filled.CardGiftcard,
        bannerText = "WELCOME GIFT",
        headline = "You're in!",
        subtitle = "Your referral rewards are confirmed.",
        rewardLines = listOf(
            "+${ReferralRepository.TOKEN_REWARD} ID tokens",
            "+${ReferralRepository.UNLOCK_DAYS_REWARD} day full-feature unlock",
            "RockScout Friends connection created",
        ),
        onDismiss = onDismiss,
    )
}

/**
 * Full-screen celebratory overlay for the sender's referral reward.
 * Fires when a sender opens the app and backend completions are credited.
 */
@Composable
fun ReferralSenderRewardCelebration(
    data: ReferralRepository.SenderRewardData,
    onDismiss: () -> Unit,
) {
    val rewardLines: List<String> = if (data.isPremium) {
        listOf("+${ReferralRepository.PREMIUM_XP_REWARD} XP per referral")
    } else {
        listOf(
            "+${ReferralRepository.TOKEN_REWARD} ID tokens",
            "+${ReferralRepository.UNLOCK_DAYS_REWARD} day full-feature unlock",
        )
    }
    val headline = if (data.count == 1) "A friend joined!" else "${data.count} friends joined!"
    val subtitle = if (data.isPremium) {
        "Your referral XP rewards have been credited."
    } else {
        "Your referral rewards have been credited."
    }

    ReferralCelebrationContent(
        accent = Citrine,
        secondary = Color(0xFFE8A33D),
        tertiary = Color(0xFFD4AF37),
        iconVector = Icons.Filled.PersonAdd,
        bannerText = "REFERRAL REWARD",
        headline = headline,
        subtitle = subtitle,
        rewardLines = rewardLines,
        onDismiss = onDismiss,
    )
}

@Composable
private fun ReferralCelebrationContent(
    accent: Color,
    secondary: Color,
    tertiary: Color,
    iconVector: androidx.compose.ui.graphics.vector.ImageVector,
    bannerText: String,
    headline: String,
    subtitle: String,
    rewardLines: List<String>,
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
        val steps = 20
        for (i in 0..steps) {
            shardProgress = i / steps.toFloat()
            delay(45)
        }
    }

    val transition = rememberInfiniteTransition(label = "referralCelebrationGlow")
    val glow by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.80f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "referralGlowAlpha",
    )
    val corePulse by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "referralCorePulse",
    )
    val shimmerRotate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Restart),
        label = "referralShimmerRotate",
    )
    val burstScale by animateFloatAsState(
        targetValue = if (burst) 1f else 0f,
        animationSpec = tween(500),
        label = "referralBurstScale",
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

            // Radiating crystal shard burst
            if (burst) {
                val shardCount = 12
                for (i in 0 until shardCount) {
                    val angleDeg = (360f / shardCount) * i
                    val angleRad = Math.toRadians(angleDeg.toDouble())
                    val distance = 100f + 160f * shardProgress
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
                            .size((12f + 8f * (1f - shardProgress)).dp)
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

            // Outer glow rings
            if (burst) {
                Box(
                    modifier = Modifier
                        .size((260 * corePulse).dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    accent.copy(alpha = glow * 0.5f),
                                    secondary.copy(alpha = glow * 0.2f),
                                    Color.Transparent,
                                ),
                            ),
                        ),
                ) {}
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .rotate(shimmerRotate)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color.Transparent,
                                    accent.copy(alpha = 0.3f),
                                    Color.Transparent,
                                    secondary.copy(alpha = 0.25f),
                                    Color.Transparent,
                                    tertiary.copy(alpha = 0.2f),
                                    Color.Transparent,
                                ),
                            ),
                        ),
                ) {}
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .heightIn(min = 320.dp, max = 480.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1E2228), Color(0xFF161A1E), Color(0xFF101316)),
                        ),
                    )
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            ) {
                // Banner with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        Icons.Filled.Diamond,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = bannerText,
                        style = MaterialTheme.typography.labelLarge,
                        color = accent,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        letterSpacing = 1.5.sp,
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        Icons.Filled.Diamond,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(16.dp),
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Glowing mineral core with icon
                Box(
                    modifier = Modifier
                        .size((80 * corePulse).dp)
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
                            .size(48.dp)
                            .rotate(shimmerRotate)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.35f),
                                        Color.Transparent,
                                    ),
                                ),
                            ),
                    )
                    Icon(
                        iconVector,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp),
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = headline,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(10.dp))

                // Reward lines
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    rewardLines.forEach { line ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(14.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = line,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextRewardColor,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Action buttons — horizontally scrollable for full text
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SculptedButton(
                        text = "Awesome!",
                        onClick = {
                            visible = false
                            onDismiss()
                        },
                        modifier = Modifier.height(48.dp),
                        accent = accent,
                        containerColor = accent,
                        textColor = Ink,
                        shape = RoundedCornerShape(12.dp),
                    )
                    SculptedButton(
                        text = "Close",
                        onClick = {
                            visible = false
                            onDismiss()
                        },
                        modifier = Modifier.height(48.dp),
                        accent = accent,
                        containerColor = Color.Black,
                        textColor = Color.White,
                        icon = Icons.Filled.Close,
                        shape = RoundedCornerShape(12.dp),
                    )
                }
            }
        }
    }
}

private val TextRewardColor = Color(0xFFF5F2EA)

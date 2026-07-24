package com.rork.rockscout.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.CitrineDeep
import com.rork.rockscout.ui.theme.CitrineSoft
import kotlinx.coroutines.delay

/**
 * Celebration intensity level based on donation amount tier.
 * Higher tiers produce more particles, brighter glows, and bigger text.
 */
enum class CelebrationLevel(val amount: Double) {
    BASIC(1.0),
    SUPPORTER(4.99),
    ENTHUSIAST(9.99),
    PATRON(19.99),
    CHAMPION(49.99);

    companion object {
        fun forAmount(amount: Double): CelebrationLevel = when {
            amount >= 35.0 -> CHAMPION
            amount >= 15.0 -> PATRON
            amount >= 7.5 -> ENTHUSIAST
            amount >= 2.5 -> SUPPORTER
            else -> BASIC
        }
    }
}

/**
 * A full-screen rock-themed "Thank you for your support" celebration overlay.
 * Auto-dismisses after 5.5 seconds. Intensity scales with the donation amount —
 * higher tiers get more crystals, brighter bursts, and a more triumphant layout.
 */
@Composable
fun ThankYouCelebration(
    level: CelebrationLevel,
    onDismiss: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    var burst by remember { mutableStateOf(false) }

    // Intercept back press so it dismisses the celebration instead of
    // popping the underlying screen from the navigation stack.
    BackHandler { onDismiss() }

    // Play burst once on entry — no auto-dismiss; stays until user closes
    LaunchedEffect(Unit) {
        visible = true
        delay(300)
        burst = true
    }

    val baseGlow = when (level) {
        CelebrationLevel.BASIC -> 0.45f
        CelebrationLevel.SUPPORTER -> 0.55f
        CelebrationLevel.ENTHUSIAST -> 0.65f
        CelebrationLevel.PATRON -> 0.75f
        CelebrationLevel.CHAMPION -> 0.90f
    }

    val transition = rememberInfiniteTransition(label = "celebrationGlow")
    val pulse by transition.animateFloat(
        initialValue = baseGlow,
        targetValue = baseGlow + 0.20f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "pulseAlpha",
    )
    val rotate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(9000), RepeatMode.Restart),
        label = "rotateAngle",
    )

    val burstScale by animateFloatAsState(
        targetValue = if (burst) 1f else 0f,
        animationSpec = tween(600, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "burstScale",
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(250)),
        exit = fadeOut(tween(250)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1A1812).copy(alpha = 0.92f),
                            Color(0xFF0B0A07).copy(alpha = 0.97f),
                        ),
                        radius = 1200f,
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            // ─── Looping fireworks behind the content ───
            val fwColors = when (level) {
                CelebrationLevel.BASIC -> listOf(Citrine, CitrineSoft)
                CelebrationLevel.SUPPORTER -> listOf(Citrine, CitrineSoft, Aqua)
                CelebrationLevel.ENTHUSIAST -> listOf(Citrine, CitrineSoft, Aqua, Amethyst)
                CelebrationLevel.PATRON -> listOf(Citrine, Color(0xFFFFE9A8), Aqua, Amethyst)
                CelebrationLevel.CHAMPION -> listOf(Citrine, Color(0xFFFFE9A8), Aqua, Amethyst, Color(0xFFFFF3C8))
            }
            FireworksOverlay(colors = fwColors, modifier = Modifier)

            // Ambient rock-vein glow behind everything
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Citrine.copy(alpha = pulse * 0.30f),
                                Amethyst.copy(alpha = pulse * 0.18f),
                                Color.Transparent,
                            ),
                            radius = 900f,
                        )
                    )
            )

            // Rotating crystal burst ring (intensity scales with level)
            CrystalBurstRing(
                level = level,
                rotate = rotate,
                burstScale = burstScale,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Hero crystal icon
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .scale(burstScale.coerceAtLeast(0.1f))
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Citrine.copy(alpha = pulse), CitrineDeep.copy(alpha = 0.45f))
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Diamond,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp),
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Headline — scales with tier
                val headline = when (level) {
                    CelebrationLevel.BASIC -> "Thank you!"
                    CelebrationLevel.SUPPORTER -> "Thank you, supporter!"
                    CelebrationLevel.ENTHUSIAST -> "Thank you, enthusiast!"
                    CelebrationLevel.PATRON -> "Thank you, patron!"
                    CelebrationLevel.CHAMPION -> "Thank you, champion!"
                }
                val headlineStyle = when (level) {
                    CelebrationLevel.BASIC -> MaterialTheme.typography.headlineSmall
                    CelebrationLevel.SUPPORTER -> MaterialTheme.typography.headlineMedium
                    CelebrationLevel.ENTHUSIAST -> MaterialTheme.typography.headlineMedium
                    CelebrationLevel.PATRON -> MaterialTheme.typography.headlineLarge
                    CelebrationLevel.CHAMPION -> MaterialTheme.typography.headlineLarge
                }
                val headlineColor = when (level) {
                    CelebrationLevel.BASIC -> Citrine
                    CelebrationLevel.SUPPORTER -> Citrine
                    CelebrationLevel.ENTHUSIAST -> CitrineSoft
                    CelebrationLevel.PATRON -> Color(0xFFFFE9A8)
                    CelebrationLevel.CHAMPION -> Color(0xFFFFF3C8)
                }
                Text(
                    text = headline,
                    style = headlineStyle,
                    color = headlineColor,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(10.dp))

                // Subtitle
                val subtitle = when (level) {
                    CelebrationLevel.BASIC -> "Your support keeps RockScout growing."
                    CelebrationLevel.SUPPORTER -> "Your tokens and ad-free week are unlocked."
                    CelebrationLevel.ENTHUSIAST -> "Tokens, location monitoring & ad-free — unlocked!"
                    CelebrationLevel.PATRON -> "A generous gift. 3 weeks of full access unlocked!"
                    CelebrationLevel.CHAMPION -> "A rockhound champion! A full month of premium-style access is yours!"
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFD8D2C3),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                )

                // Extra flourish for higher tiers — a row of accent gems
                if (level.ordinal >= CelebrationLevel.ENTHUSIAST.ordinal) {
                    Spacer(Modifier.height(18.dp))
                    AccentGemRow(level = level, burstScale = burstScale)
                }

                // Champion-only banner
                if (level == CelebrationLevel.CHAMPION) {
                    Spacer(Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .scale(burstScale.coerceAtLeast(0.1f))
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Citrine.copy(alpha = 0.85f), Color(0xFFFFE9A8).copy(alpha = 0.85f))
                                )
                            )
                            .padding(horizontal = 18.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = "CHAMPION ROCKHOUND",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF1A1812),
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp,
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Close button — stays until user dismisses
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SculptedButton(
                        text = "Close",
                        onClick = {
                            visible = false
                            onDismiss()
                        },
                        modifier = Modifier.height(48.dp),
                        accent = Citrine,
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

/**
 * Rotating ring of crystal shards radiating outward. The number of shards and
 * their color variety scale with the celebration level.
 */
@Composable
private fun CrystalBurstRing(
    level: CelebrationLevel,
    rotate: Float,
    burstScale: Float,
) {
    val shardCount = when (level) {
        CelebrationLevel.BASIC -> 6
        CelebrationLevel.SUPPORTER -> 10
        CelebrationLevel.ENTHUSIAST -> 14
        CelebrationLevel.PATRON -> 18
        CelebrationLevel.CHAMPION -> 24
    }
    val palette = when (level) {
        CelebrationLevel.BASIC -> listOf(Citrine)
        CelebrationLevel.SUPPORTER -> listOf(Citrine, CitrineSoft)
        CelebrationLevel.ENTHUSIAST -> listOf(Citrine, CitrineSoft, Aqua)
        CelebrationLevel.PATRON -> listOf(Citrine, CitrineSoft, Aqua, Amethyst)
        CelebrationLevel.CHAMPION -> listOf(Citrine, Color(0xFFFFE9A8), Aqua, Amethyst, Color(0xFFFFF3C8))
    }
    val radius = when (level) {
        CelebrationLevel.BASIC -> 150f
        CelebrationLevel.SUPPORTER -> 175f
        CelebrationLevel.ENTHUSIAST -> 200f
        CelebrationLevel.PATRON -> 225f
        CelebrationLevel.CHAMPION -> 250f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .rotate(rotate)
            .scale(burstScale.coerceAtLeast(0.05f)),
        contentAlignment = Alignment.Center,
    ) {
        for (i in 0 until shardCount) {
            val angle = (360f / shardCount) * i
            val color = palette[i % palette.size]
            val shardSize = (10 + (i % 3) * 4).dp
            Box(
                modifier = Modifier
                    .offset(
                        x = with(androidx.compose.ui.platform.LocalDensity.current) {
                            (radius * kotlin.math.cos(Math.toRadians(angle.toDouble()))).toFloat().toDp()
                        },
                        y = with(androidx.compose.ui.platform.LocalDensity.current) {
                            (radius * kotlin.math.sin(Math.toRadians(angle.toDouble()))).toFloat().toDp()
                        },
                    )
                    .size(shardSize)
                    .rotate(angle)
                    .alpha(0.85f)
                    .background(
                        Brush.radialGradient(listOf(color, color.copy(alpha = 0.0f)))
                    ),
            )
        }
    }
}

/**
 * A small row of accent gems shown for mid-to-top tiers, bouncing in with the burst.
 */
@Composable
private fun AccentGemRow(level: CelebrationLevel, burstScale: Float) {
    val gems = when (level) {
        CelebrationLevel.ENTHUSIAST -> listOf(Citrine, Aqua, CitrineSoft)
        CelebrationLevel.PATRON -> listOf(Citrine, Aqua, Amethyst, CitrineSoft)
        CelebrationLevel.CHAMPION -> listOf(Citrine, Color(0xFFFFE9A8), Aqua, Amethyst, Color(0xFFFFF3C8))
        else -> emptyList()
    }
    val transition = rememberInfiniteTransition(label = "gemBounce")
    val bounce by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1200, easing = LinearEasing),
            RepeatMode.Reverse,
        ),
        label = "bounceT",
    )

    Row(
        modifier = Modifier
            .scale(burstScale.coerceAtLeast(0.1f))
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        gems.forEachIndexed { index, color ->
            val offset = (bounce * 6f * if (index % 2 == 0) 1f else -1f).dp
            Box(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .offset(y = offset)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(color, color.copy(alpha = 0.25f)))
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(12.dp),
                )
            }
        }
    }
}

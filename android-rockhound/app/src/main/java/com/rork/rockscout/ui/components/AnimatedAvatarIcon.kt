package com.rork.rockscout.ui.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/** Total loop duration for every avatar micro-animation. */
private const val AVATAR_LOOP_MS = 500

/**
 * Reusable animated avatar icon — replaces static emoji [Text] on the Home and
 * Profile screens. Each emoji gets a signature micro-action that plays on a loop
 * with a rest phase, so the avatar "does the thing" for ~250ms then idles ~250ms.
 *
 * Animation groups (determined by [avatarAnimationFor]):
 * - Hammer/pick: tilt ±20° in quick bursts
 * - Volcano: lava particle eruption upward
 * - Gems/crystals: shimmer sweep + sparkle particles
 * - Water creatures: horizontal sine swim-wiggle
 * - Dinos/walkers: heavy vertical stomp bob
 * - Climbers/magic: slow climb-up-then-rest cycle
 * - Spinners: slow continuous rotation
 * - Fire: flicker scale + hue
 * - Sway: gentle side-to-side rotation
 * - Default: subtle float bob
 */
@Composable
fun AnimatedAvatarIcon(
    emoji: String,
    size: Dp,
    style: TextStyle,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
) {
    val animation = avatarAnimationFor(emoji)
    val transition = rememberInfiniteTransition(label = "avatarAnim")

    when (animation) {
        AvatarAnimation.HAMMER -> {
            val tilt by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    keyframes {
                        durationMillis = AVATAR_LOOP_MS
                        0f at 0
                        -20f at 60 with FastOutLinearInEasing
                        15f at 110 with FastOutLinearInEasing
                        0f at 160
                        0f at AVATAR_LOOP_MS
                    },
                    RepeatMode.Restart,
                ),
                label = "hammerTilt",
            )
            Box(modifier = modifier.size(size).rotate(tilt), contentAlignment = contentAlignment) {
                Text(emoji, style = style, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }

        AvatarAnimation.VOLCANO -> {
            val bob by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    keyframes {
                        durationMillis = AVATAR_LOOP_MS
                        0f at 0
                        -4f at 40 with FastOutLinearInEasing
                        0f at 100
                        0f at AVATAR_LOOP_MS
                    },
                    RepeatMode.Restart,
                ),
                label = "volcanoBob",
            )
            val particles = remember { mutableStateListOfVolcanoParticles() }
            VolcanoParticleSpawner(particles)
            Box(modifier = modifier.size(size), contentAlignment = contentAlignment) {
                Canvas(modifier = Modifier.size(size)) {
                    drawVolcanoParticles(particles)
                }
                Text(
                    emoji,
                    style = style,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(1f + bob / 100f),
                )
            }
        }

        AvatarAnimation.GEM -> {
            val shimmer by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(AVATAR_LOOP_MS, easing = LinearEasing), RepeatMode.Restart),
                label = "gemShimmer",
            )
            val sparkleScale by transition.animateFloat(
                initialValue = 0.95f,
                targetValue = 1.08f,
                animationSpec = infiniteRepeatable(tween(250), RepeatMode.Reverse),
                label = "gemScale",
            )
            Box(modifier = modifier.size(size), contentAlignment = contentAlignment) {
                Text(
                    emoji,
                    style = style,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().scale(sparkleScale).rotate(shimmer * 5f),
                )
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawGemSparkles(shimmer)
                }
            }
        }

        AvatarAnimation.SWIM -> {
            val wiggle by transition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(tween(AVATAR_LOOP_MS, easing = LinearEasing), RepeatMode.Restart),
                label = "swimWiggle",
            )
            val offsetX = cos(Math.toRadians(wiggle.toDouble())).toFloat() * 3f
            Box(modifier = modifier.size(size), contentAlignment = contentAlignment) {
                Text(
                    emoji,
                    style = style,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .rotate(sin(Math.toRadians(wiggle.toDouble())).toFloat() * 5f),
                )
            }
        }

        AvatarAnimation.STOMP -> {
            val stomp by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    keyframes {
                        durationMillis = AVATAR_LOOP_MS
                        0f at 0
                        6f at 90 with FastOutLinearInEasing
                        0f at 160
                        0f at AVATAR_LOOP_MS
                    },
                    RepeatMode.Restart,
                ),
                label = "stompBob",
            )
            Box(modifier = modifier.size(size), contentAlignment = contentAlignment) {
                Text(
                    emoji,
                    style = style,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(1f - stomp / 80f)
                        .rotate(stomp * -3f),
                )
            }
        }

        AvatarAnimation.CLIMB -> {
            val climb by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    keyframes {
                        durationMillis = AVATAR_LOOP_MS
                        0f at 0
                        -6f at 200 with FastOutLinearInEasing
                        -6f at 300
                        0f at 400
                        0f at AVATAR_LOOP_MS
                    },
                    RepeatMode.Restart,
                ),
                label = "climbOffset",
            )
            Box(modifier = modifier.size(size), contentAlignment = contentAlignment) {
                Text(
                    emoji,
                    style = style,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(1f + climb / 200f),
                )
            }
        }

        AvatarAnimation.SPINNER -> {
            val spin by transition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(tween(AVATAR_LOOP_MS, easing = LinearEasing), RepeatMode.Restart),
                label = "spinnerRotate",
            )
            Box(modifier = modifier.size(size).rotate(spin), contentAlignment = contentAlignment) {
                Text(emoji, style = style, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }

        AvatarAnimation.FIRE -> {
            val flicker by transition.animateFloat(
                initialValue = 0.9f,
                targetValue = 1.12f,
                animationSpec = infiniteRepeatable(tween(250), RepeatMode.Reverse),
                label = "fireFlicker",
            )
            Box(modifier = modifier.size(size), contentAlignment = contentAlignment) {
                Text(
                    emoji,
                    style = style,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().scale(flicker),
                )
            }
        }

        AvatarAnimation.SWAY -> {
            val sway by transition.animateFloat(
                initialValue = -8f,
                targetValue = 8f,
                animationSpec = infiniteRepeatable(tween(AVATAR_LOOP_MS), RepeatMode.Reverse),
                label = "swayRotate",
            )
            Box(modifier = modifier.size(size).rotate(sway), contentAlignment = contentAlignment) {
                Text(emoji, style = style, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }

        AvatarAnimation.FLOAT -> {
            val float by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    keyframes {
                        durationMillis = AVATAR_LOOP_MS
                        0f at 0
                        -5f at 250 with FastOutLinearInEasing
                        0f at AVATAR_LOOP_MS
                    },
                    RepeatMode.Restart,
                ),
                label = "floatBob",
            )
            Box(modifier = modifier.size(size), contentAlignment = contentAlignment) {
                Text(
                    emoji,
                    style = style,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().scale(1f + float / 300f),
                )
            }
        }
    }
}

// ─── Animation type enum ───

private enum class AvatarAnimation {
    HAMMER, VOLCANO, GEM, SWIM, STOMP, CLIMB, SPINNER, FIRE, SWAY, FLOAT,
}

private fun avatarAnimationFor(emoji: String): AvatarAnimation {
    return when (emoji) {
        "⛏️", "⚒️", "🛠️" -> AvatarAnimation.HAMMER
        "🌋" -> AvatarAnimation.VOLCANO
        "💎", "💠", "✨", "🌟", "🔮" -> AvatarAnimation.GEM
        "🐟", "🐠", "🐡", "🐬", "🐳", "🐙", "🦑", "🦞", "🦀", "🐢", "🐊", "🦈" -> AvatarAnimation.SWIM
        "🦖", "🦕", "🐘" -> AvatarAnimation.STOMP
        "🧗", "🧙", "🧝" -> AvatarAnimation.CLIMB
        "❄️", "🧭", "☄️", "🪐" -> AvatarAnimation.SPINNER
        "🔥", "🧯" -> AvatarAnimation.FIRE
        "🪸", "🌴", "🌵", "🪴", "🌿" -> AvatarAnimation.SWAY
        else -> AvatarAnimation.FLOAT
    }
}

// ─── Volcano particle helpers ───

private class VolcanoParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
)

private fun mutableStateListOfVolcanoParticles() = androidx.compose.runtime.mutableStateListOf<VolcanoParticle>()

@Composable
private fun VolcanoParticleSpawner(particles: androidx.compose.runtime.snapshots.SnapshotStateList<VolcanoParticle>) {
    val density = LocalDensity.current
    androidx.compose.runtime.LaunchedEffect(Unit) {
        var spawnTimer = 0L
        while (true) {
            kotlinx.coroutines.delay(50)
            spawnTimer += 50
            if (spawnTimer >= AVATAR_LOOP_MS) {
                spawnTimer = 0
                val sizePx = with(density) { 50.dp.toPx() }
                repeat(4) { i ->
                    val angle = -90f + (i - 1.5f) * 25f
                    val speed = Random.nextFloat() * 20f + 30f
                    particles.add(
                        VolcanoParticle(
                            x = sizePx / 2f,
                            y = sizePx * 0.7f,
                            vx = cos(Math.toRadians(angle.toDouble())).toFloat() * speed,
                            vy = sin(Math.toRadians(angle.toDouble())).toFloat() * speed,
                            life = 1f,
                        )
                    )
                }
            }
            particles.toList().forEach { p ->
                p.x += p.vx * 0.05f * 3f
                p.y += p.vy * 0.05f * 3f
                p.vy += 60f * 0.05f
                p.life -= 0.05f / 1.5f
                if (p.life <= 0f) particles.remove(p)
            }
        }
    }
}

private fun DrawScope.drawVolcanoParticles(particles: List<VolcanoParticle>) {
    val lavaColor = Color(0xFFFF6B2C)
    particles.forEach { p ->
        val alpha = p.life.coerceIn(0f, 1f)
        val radius = (3f * alpha + 1f) * density
        drawCircle(
            color = lavaColor.copy(alpha = alpha * 0.85f),
            radius = radius,
            center = androidx.compose.ui.geometry.Offset(p.x, p.y),
        )
    }
}

private fun DrawScope.drawGemSparkles(progress: Float) {
    val sparkleColor = Color.White.copy(alpha = 0.7f)
    // 3 sparkles that sweep diagonally
    for (i in 0 until 3) {
        val phase = (progress + i * 0.33f) % 1f
        if (phase < 0.3f) {
            val t = phase / 0.3f
            val x = size.width * (0.2f + 0.6f * t)
            val y = size.height * (0.8f - 0.6f * t)
            val r = (4f * (1f - t) + 1f) * density
            drawCircle(
                color = sparkleColor.copy(alpha = (1f - t) * 0.8f),
                radius = r,
                center = androidx.compose.ui.geometry.Offset(x, y),
            )
        }
    }
}

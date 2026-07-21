package com.rork.rockscout.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Looping firework particle system rendered behind celebration popups.
 *
 * Rockets launch from random positions along the bottom of the screen, arc
 * upward with gravity, then explode into cascading spark particles that fade.
 * New rockets fire every 800–1200ms while the overlay is visible.
 *
 * @param colors accent palette for the firework bursts.
 * @param modifier layout modifier (should fill the parent [Box]).
 */
@Composable
fun FireworksOverlay(
    colors: List<Color>,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val rockets = remember { mutableStateListOf<RocketState>() }
    val sparks = remember { mutableStateListOf<SparkState>() }

    // Spawn rockets on a loop
    LaunchedEffect(colors) {
        while (true) {
            delay(Random.nextLong(800, 1200))
            if (rockets.size < 6) {
                val wPx = density.density * 360f // approximate screen width in px
                val hPx = density.density * 720f
                rockets.add(
                    RocketState(
                        startX = Random.nextFloat() * wPx * 0.8f + wPx * 0.1f,
                        startY = hPx * 0.95f,
                        targetY = hPx * (Random.nextFloat() * 0.25f + 0.2f),
                        color = colors.random(),
                    )
                )
            }
        }
    }

    // Animate rockets and sparks
    LaunchedEffect(colors) {
        while (true) {
            val dt = 16f // ~60fps
            // Update rockets
            val toExplode = mutableListOf<RocketState>()
            rockets.forEach { rocket ->
                rocket.progress += dt / 600f
                if (rocket.progress >= 1f) {
                    toExplode.add(rocket)
                }
            }
            toExplode.forEach { rocket ->
                rockets.remove(rocket)
                // Explode into sparks
                val burstCount = Random.nextInt(14, 20)
                val burstColor = rocket.color
                val altColor = colors.random()
                repeat(burstCount) { i ->
                    val angle = (360f / burstCount) * i + (Random.nextFloat() * 30f - 15f)
                    val speed = Random.nextFloat() * 50f + 40f
                    sparks.add(
                        SparkState(
                            x = rocket.startX,
                            y = rocket.targetY,
                            vx = cos(Math.toRadians(angle.toDouble())).toFloat() * speed,
                            vy = sin(Math.toRadians(angle.toDouble())).toFloat() * speed,
                            color = if (i % 3 == 0) altColor else burstColor,
                            life = 1f,
                        )
                    )
                }
            }
            // Update sparks
            sparks.toList().forEach { spark ->
                spark.x += spark.vx * dt / 1000f * 60f
                spark.y += spark.vy * dt / 1000f * 60f
                spark.vy += 80f * dt / 1000f * 60f // gravity
                spark.life -= dt / 1400f
                if (spark.life <= 0f) {
                    sparks.remove(spark)
                }
            }
            delay(16)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        drawFireworks(rockets, sparks)
    }
}

/**
 * Draws all active rockets (as glowing trails) and sparks (as fading circles).
 */
private fun DrawScope.drawFireworks(
    rockets: SnapshotStateList<RocketState>,
    sparks: SnapshotStateList<SparkState>,
) {
    // Draw rockets as glowing ascending dots
    rockets.forEach { rocket ->
        val t = rocket.progress.coerceIn(0f, 1f)
        val y = rocket.startY + (rocket.targetY - rocket.startY) * t
        val x = rocket.startX
        val radius = (3f + 2f * (1f - t)) * density
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(rocket.color, rocket.color.copy(alpha = 0.0f)),
                center = Offset(x, y),
                radius = radius * 3f,
            ),
            radius = radius * 3f,
            center = Offset(x, y),
        )
        // Trail
        drawCircle(
            color = rocket.color.copy(alpha = 0.6f),
            radius = radius,
            center = Offset(x, y),
        )
    }

    // Draw sparks as fading glowing particles
    sparks.forEach { spark ->
        val alpha = spark.life.coerceIn(0f, 1f)
        val radius = (4f * alpha + 1.5f) * density
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    spark.color.copy(alpha = alpha * 0.9f),
                    spark.color.copy(alpha = 0.0f),
                ),
                center = Offset(spark.x, spark.y),
                radius = radius * 2.5f,
            ),
            radius = radius * 2.5f,
            center = Offset(spark.x, spark.y),
        )
        drawCircle(
            color = spark.color.copy(alpha = alpha * 0.8f),
            radius = radius,
            center = Offset(spark.x, spark.y),
        )
    }
}

/** State for a single ascending firework rocket. */
private class RocketState(
    val startX: Float,
    val startY: Float,
    val targetY: Float,
    val color: Color,
    var progress: Float = 0f,
)

/** State for a single explosion spark particle. */
private class SparkState(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    var life: Float,
)

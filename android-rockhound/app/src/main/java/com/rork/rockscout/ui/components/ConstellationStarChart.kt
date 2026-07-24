package com.rork.rockscout.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * A star in a constellation star chart with normalized 0-1 coordinates.
 * @param x Normalized horizontal position (0 = left, 1 = right).
 * @param y Normalized vertical position (0 = top, 1 = bottom).
 * @param magnitude Apparent magnitude — lower = brighter. Used for dot size.
 * @param name Star name (optional, for labels).
 */
data class ChartStar(
    val x: Float,
    val y: Float,
    val magnitude: Float,
    val name: String = "",
)

/**
 * A line connecting two stars in a constellation star chart.
 * Indices refer to positions in the [stars] list.
 */
data class StarLine(val fromIndex: Int, val toIndex: Int)

/**
 * Programmatic Canvas-based star chart that plots a constellation's major stars
 * as white dots connected by faint lines. Star sizes vary by magnitude.
 *
 * @param stars List of stars with normalized positions and magnitudes.
 * @param lines List of star connections (indices into [stars]).
 * @param modifier Layout modifier.
 * @param lineColor Color of the constellation lines (default faint white).
 */
@Composable
fun ConstellationStarChart(
    stars: List<ChartStar>,
    lines: List<StarLine>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.White.copy(alpha = 0.3f),
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0D0C08)),
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            val w = size.width
            val h = size.height
            val padding = 20f

            // Draw connecting lines first (behind stars)
            lines.forEach { line ->
                val from = stars.getOrNull(line.fromIndex) ?: return@forEach
                val to = stars.getOrNull(line.toIndex) ?: return@forEach
                val path = Path().apply {
                    moveTo(padding + from.x * (w - 2 * padding), padding + from.y * (h - 2 * padding))
                    lineTo(padding + to.x * (w - 2 * padding), padding + to.y * (h - 2 * padding))
                }
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 1.5f, cap = StrokeCap.Round),
                )
            }

            // Draw stars on top
            stars.forEach { star ->
                val cx = padding + star.x * (w - 2 * padding)
                val cy = padding + star.y * (h - 2 * padding)
                // Brighter stars (lower magnitude) are bigger
                val radius = (4f - star.magnitude.coerceIn(-1f, 5f)) * 1.5f
                drawCircle(
                    color = Color.White,
                    radius = radius.coerceAtLeast(2f),
                    center = Offset(cx, cy),
                )
                // Glow for bright stars
                if (star.magnitude < 1.5f) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.15f),
                        radius = radius * 2.5f,
                        center = Offset(cx, cy),
                    )
                }
            }
        }
    }
}

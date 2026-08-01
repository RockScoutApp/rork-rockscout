package com.rork.rockscout.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rork.rockscout.data.DinoEntry
import kotlin.math.sqrt

/**
 * Size comparison visualization showing the dinosaur silhouette next to a 1.7m (5'7") human.
 * Silhouettes are scaled relative to each other using sqrt scaling so huge animals
 * don't completely dwarf small ones.
 *
 * @param entry The dinosaur entry to compare
 * @param accentColor The accent color for the dinosaur silhouette
 * @param modifier Layout modifier
 */
@Composable
fun DinoSizeComparison(
    entry: DinoEntry,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    val dinoMeters = estimateLengthMeters(entry.length)
    val humanMeters = 1.7f
    val dinoVisual = scaleForDisplay(dinoMeters)
    val humanVisual = scaleForDisplay(humanMeters)
    val maxVisual = maxOf(dinoVisual, humanVisual)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
        ) {
            val w = size.width
            val h = size.height
            val groundY = h * 0.88f

            // Ground line
            drawLine(
                color = Color.White.copy(alpha = 0.12f),
                start = Offset(0f, groundY),
                end = Offset(w, groundY),
                strokeWidth = 2f,
            )

            // --- Human silhouette (left, ~20% width) ---
            val humanPixelHeight = (humanVisual / maxVisual) * (h * 0.72f)
            drawHumanFigure(
                centerX = w * 0.18f,
                groundY = groundY,
                height = humanPixelHeight,
                color = Color(0xFF8899AA),
            )

            // --- Dinosaur silhouette (right, ~60% area) ---
            val dinoPixelHeight = (dinoVisual / maxVisual) * (h * 0.78f)
            val dinoScale = dinoPixelHeight / 100f
            val dinoPath = Path().apply { fillType = PathFillType.EvenOdd }
            buildDinoPath(entry.bodyPlan, dinoPath)

            val dinoFeetY = 92f
            val dinoOffsetX = w * 0.38f
            val dinoOffsetY = groundY - dinoFeetY * dinoScale

            inset(dinoOffsetX, dinoOffsetY, 0f, 0f) {
                scale(dinoScale, dinoScale, pivot = Offset.Zero) {
                    drawPath(
                        path = dinoPath,
                        color = accentColor.copy(alpha = 0.72f),
                    )
                }
            }
        }

        // Labels at the bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Human\n1.7 m",
                color = Color(0xFF8899AA),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 12.sp,
            )
            Text(
                text = "${entry.name}\n${entry.length}",
                color = accentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 12.sp,
                textAlign = TextAlign.End,
            )
        }
    }
}

/**
 * Draws a simple human figure using basic shapes.
 */
private fun DrawScope.drawHumanFigure(
    centerX: Float,
    groundY: Float,
    height: Float,
    color: Color,
) {
    val s = height / 100f
    // Head
    drawCircle(
        color = color,
        radius = 5f * s,
        center = Offset(centerX, groundY - height + 5f * s),
    )
    // Body
    drawRect(
        color = color,
        topLeft = Offset(centerX - 7f * s, groundY - height + 12f * s),
        size = Size(14f * s, 32f * s),
    )
    // Arms
    drawRect(
        color = color,
        topLeft = Offset(centerX - 12f * s, groundY - height + 14f * s),
        size = Size(5f * s, 28f * s),
    )
    drawRect(
        color = color,
        topLeft = Offset(centerX + 7f * s, groundY - height + 14f * s),
        size = Size(5f * s, 28f * s),
    )
    // Legs
    drawRect(
        color = color,
        topLeft = Offset(centerX - 7f * s, groundY - height + 44f * s),
        size = Size(6f * s, 56f * s),
    )
    drawRect(
        color = color,
        topLeft = Offset(centerX + 1f * s, groundY - height + 44f * s),
        size = Size(6f * s, 56f * s),
    )
}

/**
 * Build the dino silhouette path for the given body plan.
 * Delegates to the path builder functions in DinoSilhouette.kt.
 */
private fun buildDinoPath(bodyPlan: DinoBodyPlan, p: Path) {
    when (bodyPlan) {
        DinoBodyPlan.THEROPOD_LARGE -> buildTheropodLarge(p)
        DinoBodyPlan.THEROPOD_SMALL -> buildTheropodSmall(p)
        DinoBodyPlan.SAUROPOD -> buildSauropod(p)
        DinoBodyPlan.CERATOPSIAN -> buildCeratopsian(p)
        DinoBodyPlan.STEGOSAUR -> buildStegosaur(p)
        DinoBodyPlan.ANKYLOSAUR -> buildAnkylosaur(p)
        DinoBodyPlan.ORNITHOPOD -> buildOrnithopod(p)
        DinoBodyPlan.PTEROSAUR -> buildPterosaur(p)
        DinoBodyPlan.PLESIOSAUR -> buildPlesiosaur(p)
        DinoBodyPlan.ICHTHYOSAUR -> buildIchthyosaur(p)
        DinoBodyPlan.MOSASAUR -> buildMosasaur(p)
        DinoBodyPlan.THERIZINOSAUR -> buildTherizinosaur(p)
        DinoBodyPlan.SYNAPSID -> buildSynapsid(p)
        DinoBodyPlan.MAMMOTH -> buildMammoth(p)
        DinoBodyPlan.SABERTOOTH -> buildSaberTooth(p)
        DinoBodyPlan.RHINO_GIANT -> buildRhinoGiant(p)
        DinoBodyPlan.BIRD_PREHISTORIC -> buildBirdPrehistoric(p)
        DinoBodyPlan.CROCODILIAN -> buildCrocodilian(p)
        DinoBodyPlan.SHARK_GIANT -> buildSharkGiant(p)
        DinoBodyPlan.SLOTH_GIANT -> buildSlothGiant(p)
        DinoBodyPlan.BEAR_GIANT -> buildBearGiant(p)
        DinoBodyPlan.WOLF_PREHISTORIC -> buildWolfPrehistoric(p)
        DinoBodyPlan.ELASMOSAUR -> buildElasmosaur(p)
    }
}

private fun estimateLengthMeters(length: String): Float {
    val meterMatch = Regex("""\(([\d.]+)\s*m""").find(length)
    if (meterMatch != null) {
        return meterMatch.groupValues[1].toFloatOrNull() ?: 5f
    }
    val feetMatch = Regex("""([\d.]+)\s*ft""").find(length)
    if (feetMatch != null) {
        val feet = feetMatch.groupValues[1].toFloatOrNull() ?: 16f
        return feet * 0.3048f
    }
    return 5f
}

private fun scaleForDisplay(meters: Float): Float {
    return sqrt(meters.coerceIn(0.5f, 50f))
}

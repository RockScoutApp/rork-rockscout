package com.rork.rockscout.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.rork.rockscout.data.DinoEntry
import com.rork.rockscout.data.DinoLifeImageMap
import kotlin.math.sqrt

/**
 * Size comparison visualization showing the dinosaur paleoart image next to a 5'7" human.
 * The image is scaled relative to the human using sqrt scaling so huge animals
 * don't completely dwarf small ones.
 *
 * When the [entry] changes, the silhouette and labels smoothly animate — the dino
 * figure grows or shrinks with a spring transition, the image cross-fades, and the
 * label slides in from the side, making scale changes feel dynamic.
 *
 * @param entry The dinosaur entry to compare
 * @param accentColor The accent color for labels
 * @param modifier Layout modifier
 */
@Composable
fun DinoSizeComparison(
    entry: DinoEntry,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    val dinoFeet = estimateLengthFeet(entry.length)
    val humanFeet = 5.6f
    val dinoVisual = scaleForDisplay(dinoFeet)
    val humanVisual = scaleForDisplay(humanFeet)
    val maxVisual = maxOf(dinoVisual, humanVisual)

    // Animate the visual scale smoothly when switching dinos
    val animatedDinoVisual by animateFloatAsState(
        targetValue = dinoVisual,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "dinoScale",
    )
    val animatedHumanVisual by animateFloatAsState(
        targetValue = humanVisual,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "humanScale",
    )
    val animatedMaxVisual by animateFloatAsState(
        targetValue = maxVisual,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "maxScale",
    )

    // Animate the accent color transition
    val animatedAccent by animateColorAsState(
        targetValue = accentColor,
        animationSpec = tween(durationMillis = 400),
        label = "accentColor",
    )

    // Prefer the life reconstruction so the scale chart shows a living animal.
    val imageUrl = DinoLifeImageMap.imageUri(entry)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
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

            // --- Human silhouette (left, ~22% width) ---
            val humanPixelHeight = (animatedHumanVisual / animatedMaxVisual) * (h * 0.72f)
            drawHumanFigure(
                centerX = w * 0.18f,
                groundY = groundY,
                height = humanPixelHeight,
                color = Color(0xFF8899AA),
            )

            // --- Dinosaur silhouette outline (right, behind the image) ---
            val dinoPixelHeight = (animatedDinoVisual / animatedMaxVisual) * (h * 0.78f)
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
                        color = animatedAccent.copy(alpha = 0.3f),
                    )
                }
            }
        }

        // Paleoart image overlaid on the right side with cross-fade transition
        AnimatedContent(
            targetState = imageUrl,
            transitionSpec = {
                (fadeIn(animationSpec = tween(400)) +
                    slideInHorizontally(animationSpec = tween(400)) { it / 4 }) togetherWith
                    (fadeOut(animationSpec = tween(300)) +
                    slideOutHorizontally(animationSpec = tween(300)) { -it / 4 })
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxWidth(0.62f)
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp)),
            label = "dinoImage",
        ) { currentImageUrl ->
            if (currentImageUrl != null) {
                AsyncImage(
                    model = currentImageUrl,
                    contentDescription = "${entry.name} size comparison",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            }
        }

        // Labels at the bottom with slide-in animation on dino change
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Human\n5'7\"",
                color = Color(0xFF8899AA),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 12.sp,
            )
            AnimatedContent(
                targetState = entry to animatedAccent,
                transitionSpec = {
                    (fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 3 }) togetherWith
                        (fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 3 })
                },
                modifier = Modifier,
                label = "dinoLabel",
            ) { (currentEntry, color) ->
                Text(
                    text = "${currentEntry.name}\n${currentEntry.length}",
                    color = color,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 12.sp,
                    textAlign = TextAlign.End,
                )
            }
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

/**
 * Extract the length in feet from strings like "40 ft", "10–20 ft", "23 ft wingspan".
 */
private fun estimateLengthFeet(length: String): Float {
    val feetMatch = Regex("""([\d.]+)\s*ft""").find(length)
    if (feetMatch != null) {
        return feetMatch.groupValues[1].toFloatOrNull() ?: 16f
    }
    val inchMatch = Regex("""([\d.]+)\s*in""").find(length)
    if (inchMatch != null) {
        val inches = inchMatch.groupValues[1].toFloatOrNull() ?: 200f
        return inches / 12f
    }
    return 16f
}

private fun scaleForDisplay(feet: Float): Float {
    return sqrt(feet.coerceIn(0.5f, 200f))
}

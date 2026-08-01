package com.rork.rockscout.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Body plan categories for prehistoric animals.
 * Each maps to a unique silhouette drawn via Compose Canvas.
 * Silhouettes are bundled in code — no external links that could disappear.
 */
enum class DinoBodyPlan(val label: String) {
    THEROPOD_LARGE("Large Theropod"),
    THEROPOD_SMALL("Small Theropod"),
    SAUROPOD("Sauropod"),
    CERATOPSIAN("Ceratopsian"),
    STEGOSAUR("Stegosaur"),
    ANKYLOSAUR("Ankylosaur"),
    ORNITHOPOD("Ornithopod"),
    PTEROSAUR("Pterosaur"),
    PLESIOSAUR("Plesiosaur"),
    ICHTHYOSAUR("Ichthyosaur"),
    MOSASAUR("Mosasaur"),
    THERIZINOSAUR("Therizinosaur"),
    SYNAPSID("Synapsid"),
    MAMMOTH("Mammoth"),
    SABERTOOTH("Saber-toothed Cat"),
    RHINO_GIANT("Giant Rhinoceros"),
    BIRD_PREHISTORIC("Prehistoric Bird"),
    CROCODILIAN("Crocodilian"),
    SHARK_GIANT("Giant Shark"),
    SLOTH_GIANT("Giant Sloth"),
    BEAR_GIANT("Giant Bear"),
    WOLF_PREHISTORIC("Dire Wolf"),
    ELASMOSAUR("Elasmosaur"),
}

/**
 * Draws a colorful dinosaur silhouette on a Canvas.
 * The silhouette is drawn in a 100x100 coordinate space and scaled to fit.
 * Each body plan gets a unique, recognizable side-profile shape.
 *
 * @param bodyPlan The silhouette type to draw
 * @param color The fill color for the silhouette
 * @param modifier Modifier for sizing
 */
@Composable
fun DinoSilhouette(
    bodyPlan: DinoBodyPlan,
    color: Color,
    modifier: Modifier = Modifier,
    backgroundGradient: List<Color>? = null,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val scale = minOf(w / 100f, h / 100f)
        val offsetX = (w - 100f * scale) / 2f
        val offsetY = (h - 100f * scale) / 2f

        // Draw background gradient if provided
        if (backgroundGradient != null) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = backgroundGradient,
                    center = Offset(w * 0.5f, h * 0.4f),
                    radius = w * 0.7f,
                ),
                size = size,
            )
        }

        val path = Path().apply {
            fillType = PathFillType.EvenOdd
        }

        when (bodyPlan) {
            DinoBodyPlan.THEROPOD_LARGE -> buildTheropodLarge(path)
            DinoBodyPlan.THEROPOD_SMALL -> buildTheropodSmall(path)
            DinoBodyPlan.SAUROPOD -> buildSauropod(path)
            DinoBodyPlan.CERATOPSIAN -> buildCeratopsian(path)
            DinoBodyPlan.STEGOSAUR -> buildStegosaur(path)
            DinoBodyPlan.ANKYLOSAUR -> buildAnkylosaur(path)
            DinoBodyPlan.ORNITHOPOD -> buildOrnithopod(path)
            DinoBodyPlan.PTEROSAUR -> buildPterosaur(path)
            DinoBodyPlan.PLESIOSAUR -> buildPlesiosaur(path)
            DinoBodyPlan.ICHTHYOSAUR -> buildIchthyosaur(path)
            DinoBodyPlan.MOSASAUR -> buildMosasaur(path)
            DinoBodyPlan.THERIZINOSAUR -> buildTherizinosaur(path)
            DinoBodyPlan.SYNAPSID -> buildSynapsid(path)
            DinoBodyPlan.MAMMOTH -> buildMammoth(path)
            DinoBodyPlan.SABERTOOTH -> buildSaberTooth(path)
            DinoBodyPlan.RHINO_GIANT -> buildRhinoGiant(path)
            DinoBodyPlan.BIRD_PREHISTORIC -> buildBirdPrehistoric(path)
            DinoBodyPlan.CROCODILIAN -> buildCrocodilian(path)
            DinoBodyPlan.SHARK_GIANT -> buildSharkGiant(path)
            DinoBodyPlan.SLOTH_GIANT -> buildSlothGiant(path)
            DinoBodyPlan.BEAR_GIANT -> buildBearGiant(path)
            DinoBodyPlan.WOLF_PREHISTORIC -> buildWolfPrehistoric(path)
            DinoBodyPlan.ELASMOSAUR -> buildElasmosaur(path)
        }

        drawPath(
            path = path,
            color = color,
        )
    }
}

// All paths in 100x100 coordinate space, side profile facing right.

/** T. rex, Giganotosaurus, Allosaurus — massive bipedal carnivore */
private fun buildTheropodLarge(p: Path) {
    p.moveTo(8f, 52f)   // tail tip
    p.cubicTo(20f, 46f, 30f, 42f, 38f, 40f)   // tail top
    p.cubicTo(42f, 30f, 50f, 26f, 58f, 25f)   // back rising
    p.cubicTo(62f, 22f, 66f, 20f, 70f, 18f)   // neck/head top
    p.cubicTo(76f, 16f, 82f, 16f, 86f, 18f)   // head top
    p.lineTo(92f, 24f)   // snout tip top
    p.lineTo(90f, 30f)   // snout tip
    p.lineTo(82f, 30f)   // jaw front
    p.cubicTo(80f, 32f, 78f, 33f, 76f, 35f)   // jaw bottom
    p.cubicTo(74f, 38f, 73f, 40f, 72f, 44f)   // throat
    // tiny arm
    p.lineTo(74f, 46f)
    p.lineTo(72f, 52f)
    p.lineTo(70f, 50f)
    p.lineTo(70f, 44f)
    p.cubicTo(68f, 50f, 62f, 56f, 56f, 60f)   // belly
    // leg
    p.lineTo(54f, 64f)
    p.lineTo(50f, 78f)
    p.lineTo(48f, 88f)
    p.lineTo(54f, 90f)
    p.lineTo(58f, 88f)
    p.lineTo(60f, 78f)
    p.lineTo(62f, 64f)   // back of leg
    p.lineTo(66f, 58f)   // hip
    p.cubicTo(55f, 52f, 40f, 50f, 25f, 52f)   // tail bottom
    p.lineTo(8f, 52f)
    p.close()
}

/** Velociraptor, Compsognathus — slim running predator */
private fun buildTheropodSmall(p: Path) {
    p.moveTo(6f, 48f)   // tail tip
    p.cubicTo(18f, 44f, 30f, 40f, 38f, 38f)   // tail
    p.cubicTo(44f, 32f, 50f, 28f, 56f, 26f)   // back
    p.cubicTo(60f, 24f, 64f, 22f, 68f, 20f)   // neck
    p.cubicTo(74f, 18f, 80f, 18f, 84f, 20f)   // head
    p.lineTo(90f, 26f)   // snout
    p.lineTo(88f, 30f)
    p.lineTo(82f, 30f)
    p.cubicTo(78f, 34f, 74f, 38f, 72f, 44f)   // throat
    p.lineTo(74f, 46f)   // tiny arm
    p.lineTo(72f, 50f)
    p.lineTo(70f, 48f)
    p.lineTo(68f, 42f)
    p.cubicTo(64f, 50f, 58f, 54f, 52f, 56f)   // belly
    // leg
    p.lineTo(50f, 62f)
    p.lineTo(46f, 80f)
    p.lineTo(44f, 90f)
    p.lineTo(50f, 92f)
    p.lineTo(54f, 88f)
    p.lineTo(56f, 78f)
    p.lineTo(58f, 60f)
    p.lineTo(62f, 54f)
    p.cubicTo(48f, 50f, 30f, 50f, 15f, 48f)
    p.lineTo(6f, 48f)
    p.close()
}

/** Brachiosaurus, Diplodocus — long-necked giant */
private fun buildSauropod(p: Path) {
    // tail
    p.moveTo(4f, 55f)
    p.cubicTo(12f, 52f, 20f, 50f, 26f, 48f)
    // body
    p.cubicTo(30f, 42f, 36f, 38f, 42f, 36f)
    // neck rising
    p.cubicTo(46f, 30f, 50f, 20f, 56f, 12f)
    p.cubicTo(60f, 8f, 64f, 6f, 68f, 6f)    // head top
    p.lineTo(72f, 8f)    // head
    p.lineTo(76f, 12f)   // snout
    p.lineTo(74f, 16f)
    p.lineTo(68f, 16f)   // jaw
    p.cubicTo(66f, 20f, 62f, 26f, 58f, 34f)  // neck front
    p.cubicTo(54f, 40f, 50f, 44f, 48f, 48f)  // chest
    // front leg
    p.lineTo(46f, 62f)
    p.lineTo(44f, 82f)
    p.lineTo(42f, 90f)
    p.lineTo(48f, 92f)
    p.lineTo(52f, 90f)
    p.lineTo(54f, 82f)
    p.lineTo(56f, 62f)
    // belly
    p.lineTo(60f, 56f)
    p.lineTo(72f, 54f)
    // back leg
    p.lineTo(74f, 62f)
    p.lineTo(76f, 82f)
    p.lineTo(78f, 90f)
    p.lineTo(84f, 92f)
    p.lineTo(86f, 88f)
    p.lineTo(82f, 78f)
    p.lineTo(80f, 62f)
    p.lineTo(78f, 52f)
    // back to tail
    p.cubicTo(60f, 50f, 40f, 52f, 20f, 55f)
    p.lineTo(4f, 55f)
    p.close()
}

/** Triceratops, Styracosaurus — horned frilled quadruped */
private fun buildCeratopsian(p: Path) {
    // tail
    p.moveTo(8f, 55f)
    p.cubicTo(16f, 52f, 22f, 50f, 28f, 48f)
    // body
    p.cubicTo(32f, 44f, 36f, 42f, 40f, 42f)
    // back leg
    p.lineTo(42f, 56f)
    p.lineTo(44f, 72f)
    p.lineTo(42f, 84f)
    p.lineTo(48f, 86f)
    p.lineTo(52f, 82f)
    p.lineTo(50f, 70f)
    p.lineTo(48f, 56f)
    // belly
    p.lineTo(54f, 52f)
    p.lineTo(64f, 52f)
    // front leg
    p.lineTo(66f, 64f)
    p.lineTo(64f, 80f)
    p.lineTo(62f, 88f)
    p.lineTo(68f, 90f)
    p.lineTo(72f, 86f)
    p.lineTo(70f, 74f)
    p.lineTo(68f, 60f)
    // chest rising to frill
    p.cubicTo(72f, 46f, 76f, 36f, 78f, 26f)   // frill back
    p.cubicTo(82f, 18f, 86f, 14f, 88f, 16f)   // frill top
    p.cubicTo(90f, 22f, 92f, 30f, 90f, 36f)   // frill front
    p.lineTo(84f, 30f)   // brow horn
    p.lineTo(82f, 22f)
    p.lineTo(86f, 20f)
    p.lineTo(88f, 28f)
    p.lineTo(92f, 36f)
    p.lineTo(90f, 42f)   // nose horn area
    p.lineTo(86f, 44f)
    p.lineTo(84f, 40f)   // beak
    p.lineTo(82f, 44f)
    p.cubicTo(76f, 46f, 68f, 48f, 58f, 48f)   // chest
    p.cubicTo(48f, 48f, 30f, 50f, 15f, 55f)   // tail underside
    p.lineTo(8f, 55f)
    p.close()
}

/** Stegosaurus — plates along back, spiked tail */
private fun buildStegosaur(p: Path) {
    // tail with spikes (thagomizer)
    p.moveTo(4f, 50f)
    p.lineTo(8f, 44f)   // tail spike
    p.lineTo(10f, 48f)
    p.lineTo(12f, 42f)  // spike 2
    p.lineTo(14f, 48f)
    p.lineTo(16f, 44f)  // spike 3
    p.lineTo(18f, 48f)
    p.cubicTo(22f, 44f, 26f, 40f, 30f, 38f)   // tail to body
    // arched back with plates
    p.lineTo(32f, 28f)  // plate 1
    p.lineTo(34f, 36f)
    p.lineTo(38f, 24f)  // plate 2
    p.lineTo(40f, 36f)
    p.lineTo(44f, 22f)  // plate 3
    p.lineTo(46f, 34f)
    p.lineTo(50f, 20f)  // plate 4
    p.lineTo(52f, 34f)
    p.lineTo(56f, 24f)  // plate 5
    p.lineTo(58f, 36f)
    p.lineTo(62f, 28f)  // plate 6
    p.lineTo(64f, 38f)
    // small head
    p.cubicTo(66f, 34f, 70f, 32f, 74f, 32f)   // head
    p.lineTo(80f, 34f)
    p.lineTo(82f, 38f)   // beak
    p.lineTo(78f, 40f)
    p.cubicTo(74f, 42f, 70f, 44f, 66f, 46f)   // throat
    // front leg
    p.lineTo(64f, 58f)
    p.lineTo(62f, 76f)
    p.lineTo(60f, 86f)
    p.lineTo(66f, 88f)
    p.lineTo(70f, 84f)
    p.lineTo(68f, 72f)
    p.lineTo(66f, 58f)
    // belly
    p.lineTo(54f, 52f)
    p.lineTo(42f, 50f)
    // back leg
    p.lineTo(40f, 60f)
    p.lineTo(38f, 78f)
    p.lineTo(36f, 88f)
    p.lineTo(42f, 90f)
    p.lineTo(46f, 86f)
    p.lineTo(44f, 74f)
    p.lineTo(42f, 58f)
    // tail underside
    p.cubicTo(30f, 52f, 20f, 52f, 10f, 52f)
    p.lineTo(4f, 50f)
    p.close()
}

/** Ankylosaurus — armored low body, tail club */
private fun buildAnkylosaur(p: Path) {
    // tail club
    p.moveTo(4f, 55f)
    p.cubicTo(8f, 50f, 14f, 48f, 16f, 52f)   // club
    p.cubicTo(14f, 56f, 10f, 58f, 8f, 58f)
    p.lineTo(4f, 55f)
    // tail to body
    p.cubicTo(20f, 50f, 28f, 44f, 36f, 38f)
    // armored back (domed)
    p.cubicTo(44f, 30f, 52f, 26f, 60f, 26f)
    p.cubicTo(68f, 26f, 74f, 28f, 78f, 32f)
    // head (low, wide)
    p.lineTo(82f, 38f)
    p.lineTo(88f, 40f)   // beak
    p.lineTo(86f, 44f)
    p.lineTo(80f, 46f)
    p.cubicTo(76f, 48f, 72f, 50f, 68f, 52f)   // throat
    // belly
    p.lineTo(56f, 54f)
    p.lineTo(44f, 54f)
    p.lineTo(32f, 52f)
    // legs (short, stocky)
    p.lineTo(30f, 64f)
    p.lineTo(28f, 80f)
    p.lineTo(26f, 88f)
    p.lineTo(32f, 90f)
    p.lineTo(36f, 86f)
    p.lineTo(34f, 76f)
    p.lineTo(32f, 62f)
    p.lineTo(58f, 58f)
    p.lineTo(62f, 66f)
    p.lineTo(60f, 80f)
    p.lineTo(58f, 88f)
    p.lineTo(64f, 90f)
    p.lineTo(68f, 86f)
    p.lineTo(66f, 76f)
    p.lineTo(64f, 60f)
    // tail bottom
    p.cubicTo(24f, 56f, 14f, 56f, 6f, 56f)
    p.close()
}

/** Parasaurolophus, Iguanodon — bipedal herbivore */
private fun buildOrnithopod(p: Path) {
    p.moveTo(6f, 52f)   // tail
    p.cubicTo(18f, 46f, 28f, 42f, 36f, 40f)
    p.cubicTo(42f, 34f, 48f, 30f, 54f, 28f)   // back
    // crest (for hadrosaurs)
    p.lineTo(56f, 18f)  // crest back
    p.lineTo(62f, 16f)
    p.lineTo(60f, 26f)  // crest front
    p.cubicTo(64f, 24f, 68f, 24f, 72f, 26f)   // head
    p.lineTo(80f, 28f)  // snout
    p.lineTo(82f, 34f)
    p.lineTo(78f, 36f)
    p.cubicTo(74f, 38f, 70f, 40f, 68f, 44f)   // jaw
    p.cubicTo(66f, 48f, 64f, 52f, 62f, 54f)   // chest
    // arm (smaller)
    p.lineTo(64f, 56f)
    p.lineTo(62f, 62f)
    p.lineTo(60f, 58f)
    p.lineTo(58f, 52f)
    // leg
    p.lineTo(56f, 64f)
    p.lineTo(52f, 82f)
    p.lineTo(50f, 92f)
    p.lineTo(56f, 94f)
    p.lineTo(60f, 90f)
    p.lineTo(62f, 80f)
    p.lineTo(64f, 62f)
    p.lineTo(68f, 54f)
    p.cubicTo(54f, 52f, 34f, 52f, 18f, 54f)
    p.lineTo(6f, 52f)
    p.close()
}

/** Pteranodon, Quetzalcoatlus — flying reptile */
private fun buildPterosaur(p: Path) {
    // wings spread (facing right)
    p.moveTo(50f, 45f)   // body center
    // left wing
    p.cubicTo(30f, 38f, 12f, 30f, 4f, 28f)
    p.cubicTo(8f, 32f, 16f, 38f, 28f, 44f)
    p.lineTo(50f, 48f)
    // right wing
    p.cubicTo(68f, 42f, 82f, 34f, 96f, 28f)
    p.cubicTo(90f, 36f, 80f, 42f, 66f, 46f)
    p.lineTo(50f, 48f)
    p.close()
    // body + head
    p.moveTo(48f, 42f)
    p.cubicTo(50f, 36f, 54f, 30f, 58f, 24f)   // neck
    p.lineTo(62f, 16f)   // crest back
    p.lineTo(70f, 14f)
    p.lineTo(66f, 22f)   // crest front
    p.lineTo(72f, 18f)   // head top
    p.lineTo(86f, 24f)   // beak tip
    p.lineTo(84f, 28f)
    p.lineTo(72f, 28f)   // jaw
    p.cubicTo(68f, 30f, 62f, 34f, 56f, 40f)   // throat
    // legs
    p.lineTo(54f, 52f)
    p.lineTo(52f, 62f)
    p.lineTo(48f, 62f)
    p.lineTo(46f, 52f)
    p.lineTo(48f, 42f)
    p.close()
}

/** Plesiosaurus — long-necked marine reptile */
private fun buildPlesiosaur(p: Path) {
    // body (flattened oval)
    p.moveTo(20f, 50f)
    p.cubicTo(24f, 40f, 32f, 36f, 40f, 36f)   // back
    // neck rising
    p.cubicTo(44f, 30f, 48f, 22f, 52f, 14f)
    p.cubicTo(54f, 10f, 56f, 8f, 58f, 8f)     // head top
    p.lineTo(64f, 10f)   // head
    p.lineTo(68f, 16f)   // snout
    p.lineTo(64f, 20f)
    p.lineTo(58f, 20f)   // jaw
    p.cubicTo(56f, 24f, 54f, 28f, 50f, 36f)   // neck front
    p.cubicTo(46f, 42f, 44f, 46f, 42f, 48f)   // chest
    // front flipper
    p.lineTo(34f, 54f)
    p.lineTo(28f, 58f)
    p.lineTo(26f, 56f)
    p.lineTo(32f, 52f)
    // belly
    p.lineTo(44f, 56f)
    // back flipper
    p.lineTo(50f, 62f)
    p.lineTo(48f, 66f)
    p.lineTo(44f, 62f)
    p.lineTo(38f, 56f)
    // tail
    p.cubicTo(36f, 54f, 30f, 52f, 24f, 52f)
    p.lineTo(20f, 50f)
    p.close()
}

/** Elasmosaur — extreme long-necked plesiosaur */
private fun buildElasmosaur(p: Path) {
    p.moveTo(16f, 52f)
    p.cubicTo(22f, 44f, 30f, 40f, 38f, 38f)   // back
    // very long neck curving
    p.cubicTo(42f, 34f, 46f, 26f, 48f, 18f)
    p.cubicTo(50f, 12f, 52f, 8f, 54f, 6f)
    p.lineTo(58f, 4f)
    p.cubicTo(62f, 4f, 64f, 6f, 66f, 8f)    // head
    p.lineTo(72f, 12f)
    p.lineTo(70f, 16f)
    p.lineTo(64f, 16f)
    p.cubicTo(62f, 18f, 60f, 22f, 58f, 28f)
    p.cubicTo(56f, 34f, 54f, 38f, 50f, 44f)   // neck
    p.cubicTo(46f, 48f, 42f, 50f, 40f, 52f)   // chest
    // flippers
    p.lineTo(32f, 56f)
    p.lineTo(26f, 60f)
    p.lineTo(24f, 58f)
    p.lineTo(30f, 54f)
    p.lineTo(42f, 58f)
    p.lineTo(48f, 64f)
    p.lineTo(46f, 68f)
    p.lineTo(40f, 64f)
    p.lineTo(34f, 58f)
    p.cubicTo(30f, 56f, 24f, 54f, 18f, 54f)
    p.lineTo(16f, 52f)
    p.close()
}

/** Ichthyosaur — dolphin-like marine reptile */
private fun buildIchthyosaur(p: Path) {
    // body (torpedo shape)
    p.moveTo(6f, 50f)   // tail
    p.lineTo(10f, 42f)  // tail fin top
    p.lineTo(14f, 46f)
    p.cubicTo(20f, 40f, 30f, 36f, 42f, 34f)   // back
    p.cubicTo(50f, 32f, 58f, 32f, 64f, 34f)   // dorsal fin area
    p.lineTo(66f, 28f)   // dorsal fin
    p.lineTo(70f, 34f)
    // head
    p.cubicTo(74f, 34f, 80f, 34f, 86f, 36f)   // snout top
    p.lineTo(92f, 42f)   // snout tip
    p.lineTo(88f, 46f)
    p.lineTo(82f, 44f)
    p.cubicTo(78f, 46f, 74f, 48f, 70f, 48f)   // jaw
    p.cubicTo(62f, 50f, 52f, 52f, 42f, 52f)   // belly
    // flipper
    p.lineTo(38f, 58f)
    p.lineTo(34f, 62f)
    p.lineTo(32f, 60f)
    p.lineTo(36f, 56f)
    p.lineTo(42f, 52f)
    p.cubicTo(30f, 52f, 20f, 52f, 12f, 52f)   // tail bottom
    p.lineTo(8f, 54f)   // tail fin bottom
    p.lineTo(6f, 50f)
    p.close()
}

/** Mosasaur — elongated sea lizard */
private fun buildMosasaur(p: Path) {
    p.moveTo(4f, 48f)   // tail tip
    p.cubicTo(10f, 42f, 16f, 40f, 20f, 42f)   // tail
    p.lineTo(24f, 36f)  // tail fin
    p.lineTo(28f, 42f)
    p.cubicTo(36f, 36f, 44f, 34f, 52f, 34f)   // back
    p.cubicTo(60f, 34f, 66f, 36f, 72f, 38f)   // head
    p.lineTo(82f, 40f)  // snout top
    p.lineTo(90f, 44f)  // snout tip
    p.lineTo(86f, 48f)
    p.lineTo(78f, 48f)  // jaw (with teeth)
    p.lineTo(74f, 52f)
    p.cubicTo(68f, 52f, 60f, 52f, 52f, 52f)   // belly
    // flipper
    p.lineTo(46f, 58f)
    p.lineTo(42f, 62f)
    p.lineTo(40f, 60f)
    p.lineTo(44f, 56f)
    p.lineTo(50f, 52f)
    p.cubicTo(38f, 54f, 26f, 54f, 16f, 52f)   // tail underside
    p.lineTo(4f, 48f)
    p.close()
}

/** Therizinosaurus — pot belly, long neck, huge claws */
private fun buildTherizinosaur(p: Path) {
    p.moveTo(8f, 54f)   // tail
    p.cubicTo(16f, 50f, 24f, 48f, 30f, 46f)
    // pot belly body
    p.cubicTo(36f, 38f, 40f, 34f, 44f, 32f)   // back
    // long neck
    p.cubicTo(48f, 24f, 52f, 16f, 56f, 10f)
    p.lineTo(60f, 6f)   // head
    p.lineTo(66f, 8f)
    p.lineTo(64f, 14f)
    p.lineTo(58f, 14f)  // jaw
    p.cubicTo(54f, 20f, 50f, 28f, 46f, 36f)   // neck front
    // huge claws
    p.lineTo(50f, 40f)
    p.lineTo(56f, 36f)  // claw 1
    p.lineTo(54f, 44f)
    p.lineTo(58f, 38f)  // claw 2
    p.lineTo(56f, 46f)
    p.lineTo(60f, 40f)  // claw 3
    p.lineTo(58f, 48f)
    // big belly
    p.cubicTo(52f, 54f, 44f, 56f, 36f, 56f)
    // legs (thick)
    p.lineTo(34f, 68f)
    p.lineTo(32f, 84f)
    p.lineTo(30f, 92f)
    p.lineTo(38f, 94f)
    p.lineTo(42f, 88f)
    p.lineTo(40f, 76f)
    p.lineTo(38f, 62f)
    p.lineTo(44f, 56f)
    p.lineTo(52f, 58f)
    p.lineTo(54f, 70f)
    p.lineTo(52f, 86f)
    p.lineTo(50f, 92f)
    p.lineTo(58f, 94f)
    p.lineTo(62f, 88f)
    p.lineTo(58f, 76f)
    p.lineTo(56f, 60f)
    p.cubicTo(44f, 56f, 28f, 56f, 14f, 56f)
    p.lineTo(8f, 54f)
    p.close()
}

/** Dimetrodon — sail-backed synapsid */
private fun buildSynapsid(p: Path) {
    p.moveTo(8f, 52f)   // tail
    p.cubicTo(18f, 48f, 26f, 46f, 32f, 44f)
    // sail
    p.lineTo(34f, 30f)  // sail front
    p.cubicTo(38f, 16f, 44f, 8f, 48f, 6f)    // sail top
    p.cubicTo(54f, 8f, 56f, 16f, 58f, 28f)   // sail back
    p.lineTo(62f, 40f)
    // head
    p.cubicTo(66f, 38f, 72f, 38f, 78f, 40f)
    p.lineTo(86f, 44f)  // snout
    p.lineTo(88f, 48f)
    p.lineTo(82f, 48f)
    p.cubicTo(78f, 50f, 72f, 52f, 66f, 52f)  // jaw
    // belly
    p.cubicTo(56f, 54f, 44f, 54f, 34f, 54f)
    // legs
    p.lineTo(32f, 64f)
    p.lineTo(30f, 80f)
    p.lineTo(28f, 88f)
    p.lineTo(34f, 90f)
    p.lineTo(38f, 84f)
    p.lineTo(36f, 72f)
    p.lineTo(34f, 58f)
    p.lineTo(50f, 56f)
    p.lineTo(54f, 66f)
    p.lineTo(52f, 82f)
    p.lineTo(50f, 88f)
    p.lineTo(56f, 90f)
    p.lineTo(60f, 84f)
    p.lineTo(56f, 72f)
    p.lineTo(54f, 58f)
    p.cubicTo(42f, 56f, 26f, 54f, 12f, 54f)
    p.lineTo(8f, 52f)
    p.close()
}

/** Woolly Mammoth — trunk, tusks, domed head */
private fun buildMammoth(p: Path) {
    // body
    p.moveTo(20f, 50f)
    p.cubicTo(24f, 38f, 32f, 32f, 42f, 30f)   // back
    p.cubicTo(50f, 28f, 58f, 28f, 62f, 30f)   // domed head top
    p.lineTo(60f, 20f)  // dome
    p.lineTo(66f, 22f)
    p.lineTo(64f, 30f)
    // trunk
    p.cubicTo(66f, 36f, 70f, 48f, 72f, 60f)
    p.lineTo(76f, 62f)
    p.lineTo(74f, 66f)
    p.lineTo(70f, 60f)
    p.cubicTo(68f, 50f, 66f, 40f, 64f, 36f)
    // tusk
    p.lineTo(68f, 44f)
    p.lineTo(74f, 54f)
    p.lineTo(76f, 52f)
    p.lineTo(70f, 42f)
    p.lineTo(66f, 34f)
    p.cubicTo(62f, 38f, 56f, 42f, 48f, 44f)   // throat/chest
    // legs (thick, stocky)
    p.lineTo(46f, 58f)
    p.lineTo(44f, 80f)
    p.lineTo(42f, 90f)
    p.lineTo(50f, 92f)
    p.lineTo(54f, 86f)
    p.lineTo(52f, 74f)
    p.lineTo(50f, 58f)
    p.lineTo(56f, 52f)
    p.lineTo(64f, 52f)
    p.lineTo(68f, 60f)
    p.lineTo(66f, 80f)
    p.lineTo(64f, 90f)
    p.lineTo(72f, 92f)
    p.lineTo(76f, 86f)
    p.lineTo(72f, 74f)
    p.lineTo(70f, 58f)
    p.cubicTo(58f, 52f, 40f, 52f, 26f, 54f)
    p.lineTo(20f, 50f)
    p.close()
}

/** Smilodon — saber-toothed cat */
private fun buildSaberTooth(p: Path) {
    // body (cat silhouette)
    p.moveTo(20f, 50f)
    p.cubicTo(24f, 40f, 30f, 34f, 38f, 32f)   // back
    p.cubicTo(44f, 30f, 50f, 30f, 56f, 32f)   // neck
    // head
    p.cubicTo(60f, 28f, 64f, 28f, 68f, 30f)
    p.lineTo(76f, 34f)  // forehead
    p.lineTo(80f, 40f)  // saber teeth
    p.lineTo(82f, 46f)
    p.lineTo(78f, 44f)  // chin
    p.cubicTo(74f, 46f, 70f, 48f, 66f, 50f)   // jaw
    p.cubicTo(62f, 52f, 58f, 54f, 52f, 54f)   // chest
    // front leg
    p.lineTo(50f, 64f)
    p.lineTo(48f, 80f)
    p.lineTo(46f, 88f)
    p.lineTo(52f, 90f)
    p.lineTo(56f, 86f)
    p.lineTo(54f, 76f)
    p.lineTo(52f, 64f)
    // belly
    p.lineTo(40f, 56f)
    // back leg
    p.lineTo(38f, 68f)
    p.lineTo(34f, 82f)
    p.lineTo(32f, 90f)
    p.lineTo(38f, 92f)
    p.lineTo(42f, 86f)
    p.lineTo(40f, 76f)
    p.lineTo(38f, 60f)
    // tail
    p.cubicTo(30f, 48f, 22f, 48f, 16f, 50f)
    p.lineTo(10f, 46f)
    p.lineTo(8f, 50f)
    p.lineTo(14f, 52f)
    p.lineTo(20f, 50f)
    p.close()
}

/** Paraceratherium — giant hornless rhinoceros */
private fun buildRhinoGiant(p: Path) {
    p.moveTo(12f, 52f)   // tail
    p.cubicTo(20f, 46f, 28f, 42f, 36f, 40f)   // back
    p.cubicTo(44f, 36f, 52f, 34f, 58f, 34f)   // neck/chest
    // long head
    p.cubicTo(64f, 32f, 72f, 32f, 80f, 36f)
    p.lineTo(90f, 42f)  // snout
    p.lineTo(92f, 46f)
    p.lineTo(86f, 46f)
    p.cubicTo(82f, 48f, 76f, 50f, 70f, 52f)   // jaw
    p.cubicTo(64f, 54f, 56f, 56f, 48f, 56f)   // chest
    // long legs
    p.lineTo(46f, 70f)
    p.lineTo(44f, 86f)
    p.lineTo(42f, 92f)
    p.lineTo(48f, 94f)
    p.lineTo(52f, 90f)
    p.lineTo(50f, 78f)
    p.lineTo(48f, 66f)
    p.lineTo(62f, 58f)
    p.lineTo(64f, 72f)
    p.lineTo(62f, 86f)
    p.lineTo(60f, 92f)
    p.lineTo(66f, 94f)
    p.lineTo(70f, 90f)
    p.lineTo(66f, 78f)
    p.lineTo(64f, 64f)
    p.lineTo(68f, 58f)
    p.cubicTo(52f, 56f, 34f, 56f, 18f, 56f)
    p.lineTo(12f, 52f)
    p.close()
}

/** Archaeopteryx — feathered bird with teeth and bony tail */
private fun buildBirdPrehistoric(p: Path) {
    // body
    p.moveTo(40f, 48f)
    p.cubicTo(44f, 40f, 48f, 36f, 52f, 34f)   // back
    p.cubicTo(56f, 30f, 60f, 28f, 64f, 28f)   // head
    p.lineTo(72f, 30f)  // snout (with teeth)
    p.lineTo(70f, 34f)
    p.lineTo(62f, 34f)
    p.cubicTo(58f, 36f, 54f, 40f, 50f, 44f)   // throat
    // wing
    p.lineTo(48f, 40f)
    p.cubicTo(40f, 32f, 28f, 26f, 16f, 24f)   // wing top
    p.lineTo(14f, 28f)
    p.cubicTo(24f, 34f, 34f, 40f, 42f, 46f)   // wing bottom
    p.lineTo(46f, 48f)
    // legs
    p.lineTo(48f, 56f)
    p.lineTo(46f, 66f)
    p.lineTo(44f, 72f)
    p.lineTo(48f, 74f)
    p.lineTo(50f, 70f)
    p.lineTo(52f, 62f)
    p.lineTo(54f, 52f)
    // long bony tail with feathers
    p.cubicTo(44f, 52f, 36f, 54f, 28f, 56f)
    p.lineTo(24f, 60f)  // feather 1
    p.lineTo(20f, 58f)
    p.lineTo(16f, 62f)  // feather 2
    p.lineTo(12f, 58f)
    p.lineTo(8f, 60f)   // feather 3
    p.lineTo(6f, 56f)
    p.lineTo(10f, 54f)
    p.lineTo(14f, 56f)
    p.lineTo(18f, 52f)
    p.lineTo(24f, 54f)
    p.lineTo(30f, 50f)
    p.lineTo(40f, 48f)
    p.close()
}

/** Sarcosuchus, Deinosuchus — giant crocodilian */
private fun buildCrocodilian(p: Path) {
    // long low body
    p.moveTo(4f, 52f)   // tail tip
    p.cubicTo(12f, 46f, 20f, 44f, 28f, 42f)   // tail top
    p.cubicTo(38f, 38f, 48f, 36f, 56f, 36f)   // back
    // head with long snout
    p.cubicTo(62f, 34f, 70f, 34f, 80f, 36f)
    p.lineTo(92f, 40f)  // snout tip
    p.lineTo(94f, 44f)
    p.lineTo(88f, 44f)
    p.lineTo(78f, 42f)  // jaw
    p.cubicTo(70f, 44f, 60f, 46f, 52f, 48f)   // throat
    // legs (short)
    p.lineTo(48f, 54f)
    p.lineTo(44f, 62f)
    p.lineTo(42f, 70f)
    p.lineTo(48f, 72f)
    p.lineTo(52f, 66f)
    p.lineTo(52f, 58f)
    p.lineTo(58f, 54f)
    p.lineTo(64f, 56f)
    p.lineTo(60f, 64f)
    p.lineTo(58f, 72f)
    p.lineTo(64f, 74f)
    p.lineTo(68f, 68f)
    p.lineTo(66f, 58f)
    // belly/tail underside
    p.cubicTo(54f, 54f, 38f, 54f, 22f, 54f)
    p.lineTo(10f, 56f)
    p.lineTo(6f, 54f)
    p.lineTo(4f, 52f)
    p.close()
}

/** Megalodon — giant shark */
private fun buildSharkGiant(p: Path) {
    p.moveTo(4f, 50f)   // tail
    p.lineTo(8f, 38f)   // tail fin top
    p.lineTo(14f, 44f)
    p.cubicTo(22f, 36f, 32f, 32f, 42f, 30f)   // back
    p.lineTo(46f, 22f)  // dorsal fin
    p.lineTo(54f, 30f)
    p.cubicTo(60f, 30f, 68f, 32f, 76f, 36f)   // head
    p.lineTo(90f, 44f)  // snout
    p.lineTo(92f, 48f)
    p.lineTo(84f, 48f)  // mouth
    p.lineTo(78f, 52f)  // jaw line
    p.cubicTo(68f, 54f, 56f, 56f, 44f, 56f)   // belly
    // pectoral fin
    p.lineTo(38f, 62f)
    p.lineTo(34f, 68f)
    p.lineTo(32f, 66f)
    p.lineTo(36f, 60f)
    p.lineTo(40f, 54f)
    p.cubicTo(30f, 54f, 20f, 54f, 12f, 54f)   // tail bottom
    p.lineTo(8f, 58f)   // tail fin bottom
    p.lineTo(4f, 50f)
    p.close()
}

/** Giant Ground Sloth — large round body, long claws */
private fun buildSlothGiant(p: Path) {
    // round body
    p.moveTo(20f, 50f)
    p.cubicTo(24f, 36f, 34f, 28f, 46f, 26f)   // back (domed)
    p.cubicTo(54f, 24f, 60f, 26f, 64f, 30f)   // head
    p.lineTo(72f, 32f)  // snout
    p.lineTo(74f, 36f)
    p.lineTo(68f, 38f)
    p.cubicTo(64f, 40f, 58f, 42f, 52f, 44f)   // jaw
    // long claw arm
    p.lineTo(50f, 50f)
    p.lineTo(46f, 56f)
    p.lineTo(44f, 64f)  // claw
    p.lineTo(48f, 66f)
    p.lineTo(52f, 58f)
    p.lineTo(56f, 52f)
    // big belly
    p.cubicTo(48f, 56f, 38f, 58f, 30f, 56f)
    // legs
    p.lineTo(28f, 70f)
    p.lineTo(26f, 84f)
    p.lineTo(24f, 92f)
    p.lineTo(32f, 94f)
    p.lineTo(36f, 88f)
    p.lineTo(34f, 76f)
    p.lineTo(32f, 64f)
    p.lineTo(44f, 60f)
    p.lineTo(48f, 70f)
    p.lineTo(46f, 84f)
    p.lineTo(44f, 92f)
    p.lineTo(52f, 94f)
    p.lineTo(56f, 88f)
    p.lineTo(52f, 76f)
    p.lineTo(50f, 62f)
    p.lineTo(44f, 56f)
    // tail stub
    p.cubicTo(30f, 52f, 24f, 52f, 18f, 52f)
    p.lineTo(20f, 50f)
    p.close()
}

/** Giant Short-Faced Bear — large bear silhouette */
private fun buildBearGiant(p: Path) {
    // body
    p.moveTo(18f, 50f)
    p.cubicTo(22f, 38f, 30f, 32f, 40f, 30f)   // back
    p.cubicTo(46f, 28f, 52f, 28f, 58f, 30f)   // shoulders
    // head
    p.cubicTo(62f, 26f, 66f, 26f, 70f, 28f)
    p.lineTo(76f, 32f)  // forehead
    p.lineTo(82f, 36f)  // snout
    p.lineTo(80f, 42f)
    p.lineTo(74f, 42f)
    p.cubicTo(70f, 44f, 66f, 46f, 62f, 48f)   // jaw
    p.cubicTo(56f, 50f, 48f, 52f, 42f, 52f)   // chest
    // front leg
    p.lineTo(40f, 64f)
    p.lineTo(38f, 82f)
    p.lineTo(36f, 90f)
    p.lineTo(44f, 92f)
    p.lineTo(48f, 86f)
    p.lineTo(46f, 74f)
    p.lineTo(44f, 62f)
    // belly
    p.lineTo(34f, 56f)
    // back leg
    p.lineTo(30f, 68f)
    p.lineTo(26f, 82f)
    p.lineTo(24f, 90f)
    p.lineTo(30f, 92f)
    p.lineTo(34f, 86f)
    p.lineTo(32f, 74f)
    p.lineTo(30f, 60f)
    p.cubicTo(26f, 54f, 20f, 54f, 14f, 52f)
    p.lineTo(18f, 50f)
    p.close()
}

/** Dire Wolf — large wolf silhouette */
private fun buildWolfPrehistoric(p: Path) {
    // body
    p.moveTo(14f, 48f)
    p.cubicTo(20f, 38f, 28f, 32f, 36f, 30f)   // back
    p.cubicTo(42f, 28f, 48f, 28f, 54f, 30f)   // neck
    // head
    p.cubicTo(58f, 26f, 62f, 26f, 66f, 28f)
    p.lineTo(74f, 30f)  // ears/forehead
    p.lineTo(72f, 26f)
    p.lineTo(76f, 28f)
    p.lineTo(80f, 34f)  // snout
    p.lineTo(82f, 40f)
    p.lineTo(76f, 40f)
    p.cubicTo(72f, 42f, 68f, 44f, 64f, 46f)   // jaw
    p.cubicTo(58f, 48f, 52f, 50f, 46f, 50f)   // chest
    // front leg
    p.lineTo(44f, 62f)
    p.lineTo(42f, 80f)
    p.lineTo(40f, 88f)
    p.lineTo(46f, 90f)
    p.lineTo(50f, 86f)
    p.lineTo(48f, 74f)
    p.lineTo(46f, 62f)
    // belly
    p.lineTo(34f, 52f)
    // back leg
    p.lineTo(32f, 66f)
    p.lineTo(28f, 82f)
    p.lineTo(26f, 88f)
    p.lineTo(32f, 90f)
    p.lineTo(36f, 84f)
    p.lineTo(34f, 72f)
    p.lineTo(32f, 58f)
    // tail
    p.cubicTo(26f, 48f, 20f, 46f, 14f, 46f)
    p.lineTo(10f, 44f)
    p.lineTo(8f, 48f)
    p.lineTo(12f, 50f)
    p.lineTo(14f, 48f)
    p.close()
}

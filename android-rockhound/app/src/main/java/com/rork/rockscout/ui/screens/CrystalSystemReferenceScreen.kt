package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.InterstitialAdTrigger
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid

private data class CrystalSystem(
    val name: String,
    val axes: String,
    val angles: String,
    val symmetry: String,
    val description: String,
    val commonShapes: String,
    val examples: String,
    val accent: Color,
    val emoji: String,
)

private val crystalSystems = listOf(
    CrystalSystem(
        name = "Cubic (Isometric)",
        axes = "a₁ = a₂ = a₃",
        angles = "α = β = γ = 90°",
        symmetry = "Highest — 4-fold, 3-fold, 2-fold axes",
        description = "The most symmetrical crystal system. All three axes are equal length and meet at 90°. Crystals can form cubes, octahedrons, dodecahedrons, and tetrahedrons. Minerals in this system often form perfect geometric shapes that are instantly recognizable.",
        commonShapes = "Cubes, octahedrons, dodecahedrons, tetrahedrons, trapezohedrons",
        examples = "Pyrite, galena, diamond, fluorite, halite, gold, silver, copper, magnetite, spinel, garnet",
        accent = Color(0xFFE5683C),
        emoji = "🎲",
    ),
    CrystalSystem(
        name = "Hexagonal",
        axes = "a₁ = a₂ = a₃ ≠ c",
        angles = "α = β = 90°, γ = 120°",
        symmetry = "6-fold vertical axis",
        description = "Three equal horizontal axes at 120° to each other, plus a vertical axis of different length. Produces the classic hexagonal prism shape — six-sided columns with a flat top and bottom. Many important gemstone minerals crystallize in this system.",
        commonShapes = "Hexagonal prisms, hexagonal bipyramids, hexagonal pyramids",
        examples = "Quartz, beryl (emerald/aquamarine), apatite, nepheline, vanadinite, mimetite",
        accent = Color(0xFF6FA8C7),
        emoji = "⬡",
    ),
    CrystalSystem(
        name = "Tetragonal",
        axes = "a₁ = a₂ ≠ c",
        angles = "α = β = γ = 90°",
        symmetry = "4-fold vertical axis",
        description = "Like cubic but with one axis stretched or compressed. Two equal horizontal axes at 90°, plus a vertical axis of different length. Creates rectangular prisms that look like stretched cubes. Less common than cubic or hexagonal but includes several important ore minerals.",
        commonShapes = "Tetragonal prisms, dipyramids, pinacoids",
        examples = "Zircon, rutile, cassiterite, vesuvianite, scapolite, wulfenite, scheelite",
        accent = Color(0xFFD9B26A),
        emoji = "▭",
    ),
    CrystalSystem(
        name = "Orthorhombic",
        axes = "a ≠ b ≠ c",
        angles = "α = β = γ = 90°",
        symmetry = "Three 2-fold axes, no higher",
        description = "Three unequal axes all meeting at 90°. The least symmetrical of the systems where all angles are right angles. Produces rhombic prisms and dipyramids — diamond-shaped cross sections. Many common minerals belong here.",
        commonShapes = "Rhombic prisms, dipyramids, pinacoids, tabular forms",
        examples = "Olivine, topaz, sulfur, barite, celestine, aragonite, stibnite, cerussite, danburite",
        accent = Color(0xFF8BBF6A),
        emoji = "◆",
    ),
    CrystalSystem(
        name = "Monoclinic",
        axes = "a ≠ b ≠ c",
        angles = "α = γ = 90°, β ≠ 90°",
        symmetry = "One 2-fold axis, one mirror plane",
        description = "Three unequal axes with two at 90° and one oblique (not 90°). The name means 'one inclined.' Creates prisms with tilted faces — crystals that look like they're leaning. The largest crystal system by number of mineral species.",
        commonShapes = "Prisms with inclined faces, basal cleavage sheets, bladed crystals",
        examples = "Gypsum, orthoclase, muscovite, augite, azurite, epidote, sphene, talc, clinopyroxenes",
        accent = Color(0xFFC97070),
        emoji = "▱",
    ),
    CrystalSystem(
        name = "Triclinic",
        axes = "a ≠ b ≠ c",
        angles = "α ≠ β ≠ γ ≠ 90°",
        symmetry = "Lowest — only 1-fold (identity)",
        description = "The least symmetrical crystal system. Three unequal axes, none at 90° to each other. Crystals tend to be flattened, tabular, or highly distorted. Despite the low symmetry, several important rock-forming minerals are triclinic.",
        commonShapes = "Pinacoids, tabular forms, distorted prisms",
        examples = "Plagioclase feldspar (albite, labradorite), kyanite, turquoise, ulexite, rhodonite, axinite",
        accent = Color(0xFF70A0C9),
        emoji = "▱",
    ),
    CrystalSystem(
        name = "Trigonal (Rhombohedral)",
        axes = "a₁ = a₂ = a₃",
        angles = "α = β = γ ≠ 90°",
        symmetry = "3-fold axis (subset of hexagonal)",
        description = "Sometimes classified as a subdivision of hexagonal. Three equal axes that meet at angles other than 90°. Produces rhombohedrons and scalenohedrons — the classic 'dog-tooth' calcite shape. Calcite is the most important trigonal mineral.",
        commonShapes = "Rhombohedrons, scalenohedrons, trigonal prisms",
        examples = "Calcite, dolomite, quartz (some classify as hexagonal), cinnabar, hematite, magnetite (some), tourmaline",
        accent = Color(0xFFB0A0C0),
        emoji = "🔺",
    ),
)

@Composable
fun CrystalSystemReferenceScreen(navController: NavController) {
    InterstitialAdTrigger(screenKey = "crystal_systems") {
        navController.navigate(Routes.PAYWALL)
    }
    ScreenScaffold(title = "Crystal System Reference", onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                            )
                        )
                        .padding(20.dp),
                ) {
                    Text(
                        "Every mineral on Earth crystallizes in one of seven geometric systems. The system is determined by the internal arrangement of atoms — the crystal lattice — and it dictates the external shapes a crystal can grow into. Learn to recognize these systems and you can narrow down any unknown mineral by its crystal shape alone.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkTextMid,
                    )
                }
            }

            item { CrystalSectionHeader("THE SEVEN CRYSTAL SYSTEMS") }
            items(crystalSystems.size) { index ->
                CrystalSystemCard(crystalSystems[index])
            }

            item { CrystalSectionHeader("IDENTIFYING CRYSTAL SYSTEMS IN THE FIELD") }
            item { FieldIdentificationCard() }
        }
    }
}

@Composable
private fun CrystalSectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = Aqua,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
    )
}

@Composable
private fun CrystalSystemCard(system: CrystalSystem) {
    DarkCard(
        accent = system.accent,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(system.accent.copy(alpha = 0.2f))
                    .glowingBorder(2.dp, system.accent.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(system.emoji, style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    system.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    system.axes + "  ·  " + system.angles,
                    style = MaterialTheme.typography.labelMedium,
                    color = system.accent,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            system.description,
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        // Properties grid
        val properties = listOf(
            "Symmetry" to system.symmetry,
            "Common shapes" to system.commonShapes,
        )
        properties.forEach { (label, value) ->
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    "$label: ",
                    style = MaterialTheme.typography.bodySmall,
                    color = system.accent,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    value,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(system.accent.copy(alpha = 0.08f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                "Examples: ",
                style = MaterialTheme.typography.bodySmall,
                color = system.accent,
                fontWeight = FontWeight.Bold,
            )
            Text(
                system.examples,
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextMid,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun FieldIdentificationCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
        val tips = listOf(
            "Look for cubes and octahedrons first" to "If the crystal has equal sides and 90° corners, it's likely cubic — pyrite, galena, or fluorite are the most common cube-forming minerals.",
            "Count the sides of prismatic crystals" to "Six-sided prisms → hexagonal (quartz, beryl). Four-sided → tetragonal (zircon, rutile) or orthorhombic (topaz, barite). Two sides flattened → monoclinic or triclinic.",
            "Check for tilted or leaning faces" to "If crystal faces don't meet at 90°, it's monoclinic (one tilted axis) or triclinic (all axes tilted). These are the most common systems for fine-grained minerals.",
            "Rhombohedrons look like sheared cubes" to "A rhombohedron looks like a cube pushed sideways — the angles aren't 90°. This is the signature of trigonal minerals, especially calcite.",
            "Some minerals look the same but belong to different systems" to "Quartz is technically trigonal (or hexagonal depending on classification). Aragonite is orthorhombic but looks like hexagonal calcite. Always verify with other tests.",
            "Crystal habit ≠ crystal system" to "A mineral's crystal SYSTEM is fixed by its chemistry. Its crystal HABIT (the shape it actually grows into) can vary — pyrite can form cubes, octahedrons, or pyritohedrons, all within the cubic system.",
        )
        tips.forEach { (title, desc) ->
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = Citrine,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
            )
            Text(
                desc,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
            )
        }
    }
}

package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.BookStyleImage
import com.rork.rockscout.ui.components.GEO_IMG_CRYSTAL_HABIT
import com.rork.rockscout.ui.components.GEO_IMG_MOHS_SCRATCH
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.TextLow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

@Composable
fun CrystalHardnessScreen(navController: NavController) {
    ScreenScaffold(title = "Crystal Systems & Hardness", onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { CrystalSystemsCard() }
            item { MohsScaleCard() }
        }
    }
}

@Composable
private fun CrystalSystemsCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFF9B7BD8)) {
        Text(
            "The Seven Crystal Systems",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Every mineral crystallizes in one of seven geometric systems, determined by the arrangement of atoms in its crystal lattice. The system dictates the shapes crystals can grow into.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.Top) {
            Text(
                "The seven systems below define every possible crystal shape.",
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextLow,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.weight(1f).padding(end = 10.dp),
            )
            BookStyleImage(
                imageUrl = GEO_IMG_CRYSTAL_HABIT,
                contentDescription = "Hexagonal quartz crystal cluster showing crystal habit",
            )
        }
        Spacer(Modifier.height(10.dp))

        val systems = listOf(
            Triple("Cubic (Isometric)", "Equal axes at 90°. Forms cubes, octahedrons, dodecahedrons.", "Pyrite, galena, diamond, fluorite, halite, gold") to Color(0xFFE5683C),
            Triple("Hexagonal", "Three equal axes at 120° plus a vertical axis. Forms hexagonal prisms and pyramids.", "Quartz, beryl, apatite, calcite, nepheline") to Color(0xFF6FA8C7),
            Triple("Tetragonal", "Two equal horizontal axes at 90°, one different vertical axis. Forms rectangular prisms.", "Zircon, rutile, cassiterite, vesuvianite, scapolite") to Color(0xFFD9B26A),
            Triple("Orthorhombic", "Three unequal axes at 90°. Forms rhombic prisms and dipyramids.", "Olivine, topaz, sulfur, barite, celestine, aragonite") to Color(0xFF8BBF6A),
            Triple("Monoclinic", "Three unequal axes, two at 90° and one oblique. Forms prisms with tilted faces.", "Gypsum, orthoclase, muscovite, augite, azurite, epidote") to Color(0xFFC97070),
            Triple("Triclinic", "Three unequal axes, none at 90°. The least symmetrical system.", "Plagioclase feldspar, kyanite, albite, labradorite, turquoise") to Color(0xFF70A0C9),
            Triple("Trigonal (Rhombohedral)", "Like hexagonal but with 3-fold symmetry. Forms rhombohedrons and scalenohedrons.", "Calcite, dolomite, quartz (some classify as hexagonal), cinnabar, hematite") to Color(0xFFB0A0C0),
        )
        systems.forEach { (data, color) ->
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                        .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), CircleShape)
                        .padding(top = 6.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(data.first, style = MaterialTheme.typography.titleSmall, color = color, fontWeight = FontWeight.Bold)
                    Text(data.second, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                    Text("Examples: ${data.third}", style = MaterialTheme.typography.labelSmall, color = DarkTextLow, modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
    }
}

@Composable
private fun MohsScaleCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFFD9B26A)) {
        val accentColor = Color(0xFFD9B26A)
        Text("Mohs Hardness Scale", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Friedrich Mohs created this scale in 1812. It's a relative scale — each mineral scratches all minerals below it. It's not linear: the jump from corundum (9) to diamond (10) is far greater than from 1 to 9 combined.",
            style = MaterialTheme.typography.bodyMedium, color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        val scale = listOf(
            Triple(1, "Talc", "Scratched by fingernail. Used in baby powder.") to Color(0xFFAAAAAA),
            Triple(2, "Gypsum", "Scratched by fingernail (~2.5). Forms desert roses.") to Color(0xFFBBBBBB),
            Triple(3, "Calcite", "Scratched by copper penny (~3.5). Fizzes in acid.") to Color(0xFFCCCCCC),
            Triple(4, "Fluorite", "Scratched by steel knife (~5.5). Fluoresces under UV.") to Color(0xFF6FA8C7),
            Triple(5, "Apatite", "Scratched by steel knife. In your teeth and bones.") to Color(0xFF4ECDC4),
            Triple(6, "Orthoclase", "Scratches glass (~5.5). A major component of granite.") to Color(0xFFCD919E),
            Triple(7, "Quartz", "Scratches glass easily. The most common durable mineral.") to Color(0xFFD4A017),
            Triple(8, "Topaz", "Scratches quartz. One of the hardest gemstones.") to Color(0xFFE8B84B),
            Triple(9, "Corundum", "Scratches topaz. Ruby and sapphire are corundum.") to Color(0xFFD64545),
            Triple(10, "Diamond", "Scratches everything. The hardest natural material.") to Color(0xFFBEE3F8),
        )
        scale.forEach { (data, color) ->
            if (data.first == 7) {
                Row(verticalAlignment = Alignment.Top) {
                    BookStyleImage(imageUrl = GEO_IMG_MOHS_SCRATCH, contentDescription = "Scratching a crystal with a steel knife for hardness test")
                    Spacer(Modifier.width(10.dp))
                    Text("\u2192 A knife blade (H\u22485.5) can't scratch quartz \u2014 that's how you know it's 7+.", style = MaterialTheme.typography.bodySmall, color = DarkTextLow, fontStyle = FontStyle.Italic, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
            }
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.2f)).glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Text(data.first.toString(), style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(data.second, style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text(data.third, style = MaterialTheme.typography.labelSmall, color = DarkTextMid, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TagChip("Fingernail ≈ 2.5", color = accentColor)
            TagChip("Penny ≈ 3.5", color = accentColor)
            TagChip("Knife ≈ 5.5", color = accentColor)
            TagChip("Glass ≈ 5.5", color = accentColor)
        }
    }
}

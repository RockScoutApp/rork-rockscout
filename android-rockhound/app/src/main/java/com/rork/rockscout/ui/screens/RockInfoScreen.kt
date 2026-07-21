package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.navigation.NavController
import com.rork.rockscout.data.RockGuide
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.ui.components.InterstitialAdTrigger
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.rockClassColor
import com.rork.rockscout.ui.components.BookStyleImage
import com.rork.rockscout.ui.components.GEO_IMG_STREAK_TEST
import com.rork.rockscout.ui.components.GEO_IMG_CRYSTAL_HABIT
import com.rork.rockscout.ui.components.GEO_IMG_MOHS_SCRATCH
import com.rork.rockscout.ui.components.GEO_IMG_ACID_TEST
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.components.glowingBorder

@Composable
fun RockInfoScreen(navController: NavController) {
    InterstitialAdTrigger(screenKey = "rockinfo") {
        navController.navigate(Routes.PAYWALL)
    }
    ScreenScaffold(title = "Exploring Geology", onBack = { navController.popBackStack() }) {
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
                        "From molten magma to polished gem, every rock and mineral tells a story. Explore the five great categories of geology below — plus the tools and techniques geologists use to identify what you find in the field.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextMid,
                    )
                }
            }

            // === ROCK TYPE GUIDES ===
            item { SectionHeader("ROCK TYPES") }
            items(SeedData.guides.size) { index ->
                val guide = SeedData.guides[index]
                GuideCard(guide) { navController.navigate(Routes.guide(guide.id)) }
            }

            // === MINERAL IDENTIFICATION ===
            item { SectionHeader("MINERAL IDENTIFICATION") }
            item { MineralIDCard() }

            // === CRYSTAL SYSTEMS ===
            item { SectionHeader("CRYSTAL SYSTEMS") }
            item { CrystalSystemsCard() }

            // === MOHS HARDNESS SCALE ===
            item { SectionHeader("HARDNESS SCALE") }
            item { MohsScaleCard() }

            // === ROCK CYCLE ===
            item { SectionHeader("THE ROCK CYCLE") }
            item { RockCycleCard() }

            // === STREAK, LUSTER, CLEAVAGE ===
            item { SectionHeader("KEY PROPERTIES") }
            item { KeyPropertiesCard() }

            // === FIELD TOOLS ===
            item { SectionHeader("FIELD TOOLS") }
            item { FieldToolsCard() }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = Aqua,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
    )
}

@Composable
private fun GuideCard(guide: RockGuide, onClick: () -> Unit) {
    val accent = rockClassColor(guide.rockClass)
    DarkCard(
        accent = accent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape).background(accent.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center,
            ) { Text(guide.emoji, style = MaterialTheme.typography.headlineMedium) }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    guide.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    guide.rockClass.label.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = accent,
                    fontWeight = FontWeight.Bold,
                )
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = DarkTextMid)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            guide.intro,
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextMid,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun MineralIDCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
        Text(
            "How to identify a mineral in the field",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Geologists use a step-by-step process to identify minerals. Follow these tests in order — each one narrows down the possibilities.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        val steps = listOf(
            "1. Streak Test" to "Rub the mineral across an unglazed porcelain plate (streak plate). The powder color left behind is the streak — it's more reliable than the surface color because it's consistent regardless of impurities.",
            "2. Hardness Test" to "Try to scratch the mineral with your fingernail (H~2.5), a copper penny (H~3.5), a steel knife (H~5.5), and quartz (H~7). This narrows down the Mohs hardness range.",
            "3. Luster" to "How does it reflect light? Metallic (like gold, galena), vitreous/glassy (like quartz), pearly (like talc), adamantine (like diamond), or earthy/dull?",
            "4. Cleavage & Fracture" to "How does it break? Cleavage is flat, smooth breaks along crystal planes (like mica's perfect sheets). Fracture is irregular — conchoidal fracture makes curved, shell-like breaks (like quartz and obsidian).",
            "5. Crystal Form" to "If crystals are visible, note their shape. Cubic (pyrite, galena), hexagonal (quartz, beryl), octahedral (diamond, fluorite), or prismatic (tourmaline, epidote)?",
            "6. Reaction to Acid" to "Place a drop of dilute HCl (or household vinegar) on the specimen. Calcite and other carbonates fizz vigorously as CO₂ is released. This is one of the most definitive field tests.",
            "7. Magnetism & Density" to "Is it attracted to a magnet? (Magnetite, pyrrhotite.) How heavy does it feel for its size? Barite and galena are surprisingly dense — a key clue.",
            "8. Special Properties" to "Some minerals have unique traits: fluorite fluoresces under UV light, calcite double-refracts (text appears doubled through clear crystals), magnetite is magnetic, and halite tastes salty.",
        )
        steps.forEach { (title, desc) ->
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
            // Book-style illustration: streak test image after step 1
            if (title == "1. Streak Test") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        "\u2192 The streak color is often the most diagnostic single test.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextLow,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.weight(1f).padding(end = 10.dp),
                    )
                    BookStyleImage(
                        imageUrl = GEO_IMG_STREAK_TEST,
                        contentDescription = "Streak test on porcelain plate",
                    )
                }
            }
            // Book-style illustration: acid test image after step 6
            if (title == "6. Reaction to Acid") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    BookStyleImage(
                        imageUrl = GEO_IMG_ACID_TEST,
                        contentDescription = "Acid drop fizzing on calcite",
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "\u2192 The fizz is CO\u2082 escaping \u2014 only carbonates react this strongly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextLow,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun CrystalSystemsCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFF9B7BD8)) {
        val accentColor = Color(0xFF9B7BD8)
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

        // Book-style illustration: crystal habit image in upper right
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
                    Text(
                        data.first,
                        style = MaterialTheme.typography.titleSmall,
                        color = color,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        data.second,
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                    )
                    Text(
                        "Examples: ${data.third}",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextLow,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun MohsScaleCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFFD9B26A)) {
        val accentColor = Color(0xFFD9B26A)
        Text(
            "Mohs Hardness Scale",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Friedrich Mohs created this scale in 1812. It's a relative scale — each mineral scratches all minerals below it. It's not linear: the jump from corundum (9) to diamond (10) is far greater than from 1 to 9 combined.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
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
            // Book-style illustration: hardness scratch test after quartz (H=7)
            if (data.first == 7) {
                Row(verticalAlignment = Alignment.Top) {
                    BookStyleImage(
                        imageUrl = GEO_IMG_MOHS_SCRATCH,
                        contentDescription = "Scratching a crystal with a steel knife for hardness test",
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "\u2192 A knife blade (H\u22485.5) can't scratch quartz \u2014 that's how you know it's 7+.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextLow,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.2f))
                        .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        data.first.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = color,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        data.second,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        data.third,
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextMid,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
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

private data class RockCycleStage(
    val label: String,
    val description: String,
    val imageUrl: String,
    val accent: Color,
)

private val rockCycleStages = listOf(
    RockCycleStage(
        label = "Magma",
        description = "Molten rock below the Earth's surface. When it cools, it crystallizes into igneous rock.",
        imageUrl = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/1377a35e-b615-40f7-90d1-15b9310b1af8.png",
        accent = Color(0xFFE5683C),
    ),
    RockCycleStage(
        label = "Igneous Rock",
        description = "Formed when magma or lava cools and solidifies. Granite, basalt, and obsidian start here.",
        imageUrl = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/53df6c87-ab76-4fdd-b590-a792920dc46a.png",
        accent = Color(0xFFE5683C),
    ),
    RockCycleStage(
        label = "Sediment",
        description = "Rock broken down by weathering and erosion. Layers compact and cement into sedimentary rock.",
        imageUrl = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/6876799c-c3c0-4c3f-9587-d861bafc0c72.png",
        accent = Color(0xFFD9B26A),
    ),
    RockCycleStage(
        label = "Metamorphic Rock",
        description = "Igneous or sedimentary rock transformed by intense heat and pressure deep underground.",
        imageUrl = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/d58a650b-99fa-4e8a-86bc-d458f5e430c0.png",
        accent = Color(0xFF6FA8C7),
    ),
)

@Composable
private fun RockCycleCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
        Text("THE ROCK CYCLE", style = MaterialTheme.typography.labelMedium, color = Citrine, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Text(
            "Rocks are never permanent. Heat and pressure, weathering, melting, and time constantly recycle one type into another. The cycle has no true beginning or end — any rock can become any other rock given enough time and the right conditions.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(16.dp))
        // 2×2 grid of stage images with labels and descriptions
        rockCycleStages.forEachIndexed { index, stage ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(stage.accent.copy(alpha = 0.12f))
                        .glowingBorder(1.dp, stage.accent.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        model = stage.imageUrl,
                        contentDescription = stage.label,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stage.label,
                        style = MaterialTheme.typography.titleSmall,
                        color = stage.accent,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        stage.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (index < rockCycleStages.lastIndex) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 36.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        Icons.Filled.ArrowForward,
                        contentDescription = null,
                        tint = TextLow,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(
            "Melting: Metamorphic or sedimentary rock can melt into magma, restarting the cycle.",
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Weathering: Any rock can be weathered into sediment, beginning the sedimentary path.",
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Heat & Pressure: Igneous and sedimentary rock can be buried and transformed into metamorphic rock.",
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextMid,
        )
    }
}

@Composable
private fun KeyPropertiesCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFF5BBAE8)) {
        val accentColor = Color(0xFF5BBAE8)
        Text(
            "Key Mineral Properties",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))

        val properties = listOf(
            "Streak" to "The color of a mineral's powder when scratched on a porcelain plate. More reliable than surface color. Hematite always has a red-brown streak even when it looks silver or black. Pyrite's streak is greenish-black, unlike gold's yellow streak.",
            "Luster" to "How light reflects off the surface. Metallic (galena, pyrite), vitreous/glassy (quartz, feldspar), pearly (talc, muscovite), adamantine (diamond, cerussite), resinous (sphalerite), silky (gypsum, asbestos), or earthy/dull (kaolinite, bauxite).",
            "Cleavage" to "The tendency to break along flat planes of weak atomic bonds. Described by quality (perfect, good, poor) and direction. Mica has one perfect direction (peels in sheets). Calcite has three perfect directions (rhombohedrons). Galena has three perfect cubic directions.",
            "Fracture" to "How a mineral breaks when not along cleavage planes. Conchoidal fracture (curved, shell-like) is classic in quartz and obsidian. Uneven fracture is common in many minerals. Splintery or fibrous fracture is seen in chrysotile and actinolite.",
            "Specific Gravity" to "How dense a mineral is compared to water. Most silicate minerals are 2.5–3.5. Metallic minerals are much heavier — galena is 7.5, gold is 19.3. Hefting a specimen (feeling its weight) is a quick field test for density.",
            "Tenacity" to "How a mineral resists breaking. Brittle (quartz, pyrite — snaps), sectile (can be cut with a knife), malleable (gold, copper — can be hammered), flexible (chlorite — bends but doesn't spring back), elastic (mica — bends and springs back).",
            "Diaphaneity" to "How light passes through. Transparent (clear quartz, calcite), translucent (jade, chalcedony), or opaque (pyrite, hematite). This helps narrow down mineral groups quickly.",
            "Color" to "The most obvious but least reliable property. Many minerals come in many colors (quartz can be clear, purple, pink, brown, black). But some minerals have diagnostic colors — malachite is always green, azurite is always blue, sulfur is always yellow.",
        )
        properties.forEach { (title, desc) ->
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = accentColor,
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

@Composable
private fun FieldToolsCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFF8BBF6A)) {
        val accentColor = Color(0xFF8BBF6A)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Science, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                "Essential Field Kit",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "You don't need a lab to identify most rocks and minerals. Here's what every rockhound should carry:",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(12.dp))

        val tools = listOf(
            "Streak Plate" to "Unglazed porcelain tile (~$2). The single most useful field tool — reveals streak color, which is often different from surface color.",
            "Hand Lens (10x–20x)" to "A jeweler's loupe for examining crystal shapes, cleavage surfaces, and small inclusions. Essential for identifying fine-grained rocks.",
            "Steel Knife or Nail" to "For hardness testing. A pocket knife blade is ~5.5 on the Mohs scale, so it scratches anything softer and is scratched by anything harder.",
            "Magnet" to "A small neodymium magnet detects magnetic minerals (magnetite, pyrrhotite, native iron). Some minerals are only weakly magnetic — drag the magnet through sand.",
            "Dilute HCl or Vinegar" to "A small squeeze bottle of weak acid. Calcite, aragonite, and dolomite all fizz — the strength of the fizz helps distinguish them.",
            "Notebook & Camera" to "Record where you found each specimen, what rock it was in, and associated minerals. Context is critical for identification.",
            "GPS or Phone" to "Mark coordinates of your find. Location data helps identify what minerals are known from that area.",
            "Reference Guide" to "A field guide or app (like RockScout!) to cross-reference properties and confirm identifications.",
        )
        tools.forEach { (name, desc) ->
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        name,
                        style = MaterialTheme.typography.titleSmall,
                        color = accentColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                    )
                }
            }
        }
    }
}

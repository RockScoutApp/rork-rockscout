package com.rork.rockscout.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.BookStyleImage
import com.rork.rockscout.ui.components.GEO_IMG_STREAK_TEST
import com.rork.rockscout.ui.components.GEO_IMG_ACID_TEST
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid

@Composable
fun MineralIdScreen(navController: NavController) {
    ScreenScaffold(title = "Mineral Identification", onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { MineralIDCard() }
            item { KeyPropertiesCard() }
        }
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
            if (title == "6. Reaction to Acid") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    BookStyleImage(
                        imageUrl = GEO_IMG_ACID_TEST,
                        contentDescription = "Acid drop fizzing on calcite",
                    )
                    Spacer(Modifier.height(10.dp))
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

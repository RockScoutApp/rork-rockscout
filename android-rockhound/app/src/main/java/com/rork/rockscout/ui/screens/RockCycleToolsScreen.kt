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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.TextLow

@Composable
fun RockCycleToolsScreen(navController: NavController) {
    ScreenScaffold(title = "Rock Cycle & Field Tools", onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { RockCycleCard() }
            item { FieldToolsCard() }
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
    RockCycleStage("Magma", "Molten rock below the Earth's surface. When it cools, it crystallizes into igneous rock.", "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/1377a35e-b615-40f7-90d1-15b9310b1af8.png", Color(0xFFE5683C)),
    RockCycleStage("Igneous Rock", "Formed when magma or lava cools and solidifies. Granite, basalt, and obsidian start here.", "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/53df6c87-ab76-4fdd-b590-a792920dc46a.png", Color(0xFFE5683C)),
    RockCycleStage("Sediment", "Rock broken down by weathering and erosion. Layers compact and cement into sedimentary rock.", "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/6876799c-c3c0-4c3f-9587-d861bafc0c72.png", Color(0xFFD9B26A)),
    RockCycleStage("Metamorphic Rock", "Igneous or sedimentary rock transformed by intense heat and pressure deep underground.", "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/d58a650b-99fa-4e8a-86bc-d458f5e430c0.png", Color(0xFF6FA8C7)),
)

@Composable
private fun RockCycleCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFF8BBF6A)) {
        Text("THE ROCK CYCLE", style = MaterialTheme.typography.labelMedium, color = Color(0xFF8BBF6A), fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Text(
            "Rocks are never permanent. Heat and pressure, weathering, melting, and time constantly recycle one type into another. The cycle has no true beginning or end — any rock can become any other rock given enough time and the right conditions.",
            style = MaterialTheme.typography.bodyMedium, color = DarkTextMid,
        )
        Spacer(Modifier.height(16.dp))
        rockCycleStages.forEachIndexed { index, stage ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(72.dp).clip(RoundedCornerShape(12.dp)).background(stage.accent.copy(alpha = 0.12f)).glowingBorder(1.dp, stage.accent.copy(alpha = 0.35f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    AsyncImage(model = stage.imageUrl, contentDescription = stage.label, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(stage.label, style = MaterialTheme.typography.titleSmall, color = stage.accent, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(2.dp))
                    Text(stage.description, style = MaterialTheme.typography.bodySmall, color = DarkTextMid, maxLines = 3, overflow = TextOverflow.Ellipsis)
                }
            }
            if (index < rockCycleStages.lastIndex) {
                Row(modifier = Modifier.fillMaxWidth().padding(start = 36.dp), horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = TextLow, modifier = Modifier.size(16.dp))
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Text("Melting: Metamorphic or sedimentary rock can melt into magma, restarting the cycle.", style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
        Spacer(Modifier.height(4.dp))
        Text("Weathering: Any rock can be weathered into sediment, beginning the sedimentary path.", style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
        Spacer(Modifier.height(4.dp))
        Text("Heat & Pressure: Igneous and sedimentary rock can be buried and transformed into metamorphic rock.", style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
    }
}

@Composable
private fun FieldToolsCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFF8BBF6A)) {
        val accentColor = Color(0xFF8BBF6A)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Science, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(10.dp))
            Text("Essential Field Kit", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        Text("You don't need a lab to identify most rocks and minerals. Here's what every rockhound should carry:", style = MaterialTheme.typography.bodyMedium, color = DarkTextMid)
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
            Row(modifier = Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.Top) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.titleSmall, color = accentColor, fontWeight = FontWeight.SemiBold)
                    Text(desc, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                }
            }
        }
    }
}

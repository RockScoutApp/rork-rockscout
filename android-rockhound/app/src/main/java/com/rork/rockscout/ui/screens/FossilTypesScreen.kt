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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import com.rork.rockscout.ui.components.PALEO_IMG_TRILOBITE
import com.rork.rockscout.ui.components.PALEO_IMG_AMMONITE
import com.rork.rockscout.ui.components.PALEO_IMG_DINO_TRACK
import com.rork.rockscout.ui.components.PALEO_IMG_PETRIFIED_WOOD
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid

@Composable
fun FossilTypesScreen(navController: NavController) {
    ScreenScaffold(title = "Fossilization & Types", onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { FossilizationCard() }
            item { FossilTypesCard() }
        }
    }
}

@Composable
private fun FossilizationCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFF6FA8C7)) {
        val accentColor = Color(0xFF6FA8C7)
        Text("How Fossils Form", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Fossilization is extraordinarily rare. An organism must die in specific conditions, be buried quickly, and survive millions of years of geological processes. Only a tiny fraction of all life that ever existed has fossilized.", style = MaterialTheme.typography.bodyMedium, color = DarkTextMid)
        Spacer(Modifier.height(14.dp))

        val steps = listOf(
            "1. Death & Decay" to "The organism dies and soft tissues usually decay or are scavenged. Only hard parts — bones, teeth, shells, wood — typically survive. Rapid burial is essential.",
            "2. Rapid Burial" to "The remains must be buried quickly by sediment (mud, sand, volcanic ash) before scavengers, weather, or currents destroy them. This seals them from oxygen and slows decomposition.",
            "3. Permineralization" to "Groundwater seeps through the sediment and deposits minerals (usually silica or calcite) into the empty spaces within bone or wood. Over thousands of years, the mineral fills every pore.",
            "4. Replacement" to "The original organic material is gradually replaced molecule-by-molecule with minerals. Petrified wood is quartz replacing wood cells. Pyritized fossils have pyrite replacing soft tissue.",
            "5. Lithification" to "The surrounding sediment compacts and cements into sedimentary rock over millions of years. The fossil is now encased in stone.",
            "6. Exposure" to "Millions of years later, erosion, tectonic uplift, or human excavation exposes the fossil at the surface. This is the moment a paleontologist (or you!) can find it.",
        )
        steps.forEach { (title, desc) ->
            Text(title, style = MaterialTheme.typography.titleSmall, color = accentColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
            Text(desc, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid)
            if (title == "4. Replacement") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Text("\u2192 Petrified wood is the most famous example \u2014 quartz replacing ancient tree cells.", style = MaterialTheme.typography.bodySmall, color = DarkTextLow, fontStyle = FontStyle.Italic, modifier = Modifier.weight(1f).padding(end = 10.dp))
                    BookStyleImage(imageUrl = PALEO_IMG_PETRIFIED_WOOD, contentDescription = "Polished petrified wood slab showing quartz replacing wood grain")
                }
            }
        }
    }
}

@Composable
private fun FossilTypesCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFF9B7BD8)) {
        val accentColor = Color(0xFF9B7BD8)
        Text("Types of Fossils", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        val types = listOf(
            "Body Fossils" to "Actual preserved parts of the organism — bones, teeth, shells, wood. Most common type. Includes permineralized, replaced, and carbonized remains.",
            "Trace Fossils (Ichnofossils)" to "Evidence of activity rather than the organism itself. Includes footprints, trackways, burrows, boreholes, coprolites (fossilized dung), and gastroliths (stomach stones). Trace fossils reveal behavior.",
            "Molds & Casts" to "A mold forms when an organism dissolves away, leaving a cavity in the exact shape of its surface. A cast forms when new minerals fill that mold, creating a replica.",
            "Carbonization (Carbon Films)" to "Soft-bodied organisms like leaves, jellyfish, and worms leave a thin film of carbon as they decay under pressure. Graptolites and Burgess Shale organisms are preserved this way.",
            "Amber Preservation" to "Insects, spiders, and small organisms trapped in tree resin that hardens into amber. The finest soft-tissue preservation known — even feathers and DNA fragments survive.",
            "Frozen & Mummified" to "Rare cases where entire organisms are preserved with soft tissue. Woolly mammoths in Siberian permafrost, mummified dinosaurs in sandstone. The most complete preservation possible.",
            "Live Fossils (Living Fossils)" to "Organisms virtually unchanged from their fossil ancestors — ginkgo trees, horseshoe crabs, coelacanths, nautilus. Not fossils themselves, but they show evolution can sometimes stand still.",
        )
        types.forEach { (title, desc) ->
            Text(title, style = MaterialTheme.typography.titleSmall, color = accentColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp, bottom = 4.dp))
            Text(desc, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid)
            if (title == "Body Fossils") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    BookStyleImage(imageUrl = PALEO_IMG_TRILOBITE, contentDescription = "Detailed trilobite fossil in shale")
                    Spacer(Modifier.width(10.dp))
                    Text("\u2192 Trilobites are among the most iconic body fossils \u2014 500+ million years old.", style = MaterialTheme.typography.bodySmall, color = DarkTextLow, fontStyle = FontStyle.Italic, modifier = Modifier.weight(1f))
                }
            }
            if (title == "Trace Fossils (Ichnofossils)") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Text("\u2192 Trackways reveal how fast dinosaurs moved and whether they traveled in herds.", style = MaterialTheme.typography.bodySmall, color = DarkTextLow, fontStyle = FontStyle.Italic, modifier = Modifier.weight(1f).padding(end = 10.dp))
                    BookStyleImage(imageUrl = PALEO_IMG_DINO_TRACK, contentDescription = "Dinosaur footprint fossil in sandstone")
                }
            }
            if (title == "Molds & Casts") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    BookStyleImage(imageUrl = PALEO_IMG_AMMONITE, contentDescription = "Polished ammonite fossil showing golden calcite chambers")
                    Spacer(Modifier.width(10.dp))
                    Text("\u2192 Ammonite chambers often fill with calcite \u2014 a perfect natural cast.", style = MaterialTheme.typography.bodySmall, color = DarkTextLow, fontStyle = FontStyle.Italic, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

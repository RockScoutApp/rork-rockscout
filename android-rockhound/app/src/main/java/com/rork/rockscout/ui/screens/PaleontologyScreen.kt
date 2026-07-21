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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Public
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.ui.components.InterstitialAdTrigger
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.BookStyleImage
import com.rork.rockscout.ui.components.PALEO_IMG_TRILOBITE
import com.rork.rockscout.ui.components.PALEO_IMG_AMMONITE
import com.rork.rockscout.ui.components.PALEO_IMG_DINO_TRACK
import com.rork.rockscout.ui.components.PALEO_IMG_PETRIFIED_WOOD
import com.rork.rockscout.ui.components.PALEO_IMG_STROMATOLITE
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Fossil
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.components.glowingBorder

@Composable
fun PaleontologyScreen(navController: NavController) {
    val periods = SeedData.fossilPeriods

    InterstitialAdTrigger(screenKey = "paleontology") {
        navController.navigate(Routes.PAYWALL)
    }

    ScreenScaffold(title = "Exploring Paleontology", onBack = { navController.popBackStack() }) {
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
                        "Explore the history of life on Earth, from the Cambrian explosion to the last Ice Age. Each period tells the story of life evolving through deep time — over 3.7 billion years of evolution, extinction, and renewal.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextMid,
                    )
                }
            }

            // === GEOLOGIC TIME SCALE ===
            item { SectionHeader("GEOLOGIC TIME SCALE") }
            item { GeologicTimeScaleCard() }

            // === MASS EXTINCTIONS ===
            item { SectionHeader("MASS EXTINCTIONS") }
            item { MassExtinctionsCard() }

            // === FOSSILIZATION ===
            item { SectionHeader("HOW FOSSILS FORM") }
            item { FossilizationCard() }

            // === FOSSIL TYPES ===
            item { SectionHeader("TYPES OF FOSSILS") }
            item { FossilTypesCard() }

            // === PERIODS BY ERA ===
            item { SectionHeader("GEOLOGIC PERIODS") }

            item { EraHeader("PALEOZOIC ERA", "541 – 252 mya · ~289 million years") }
            items(periods.filter { it.era == "Paleozoic" }.size) { idx ->
                val period = periods.filter { it.era == "Paleozoic" }[idx]
                PeriodCard(period) { navController.navigate(Routes.period(period.id)) }
            }
            item {
                EraHeader("MESOZOIC ERA", "252 – 66 mya · ~186 million years")
            }
            items(periods.filter { it.era == "Mesozoic" }.size) { idx ->
                val period = periods.filter { it.era == "Mesozoic" }[idx]
                PeriodCard(period) { navController.navigate(Routes.period(period.id)) }
            }
            item {
                EraHeader("CENOZOIC ERA", "66 mya – Present · ~66 million years")
            }
            items(periods.filter { it.era == "Cenozoic" }.size) { idx ->
                val period = periods.filter { it.era == "Cenozoic" }[idx]
                PeriodCard(period) { navController.navigate(Routes.period(period.id)) }
            }
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
private fun GeologicTimeScaleCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Fossil) {
        Text(
            "The Geologic Time Scale",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Earth's 4.6-billion-year history is divided into eons, eras, periods, and epochs. The divisions are based on major changes in the fossil record — often mass extinctions that mark boundaries between periods.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        // Book-style illustration: stromatolite image (oldest fossils)
        Row(verticalAlignment = Alignment.Top) {
            BookStyleImage(
                imageUrl = PALEO_IMG_STROMATOLITE,
                contentDescription = "Ancient stromatolite fossils — some of the oldest life on Earth",
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "\u2192 Stromatolites from the Archean are the oldest fossils known \u2014 3.5 billion years old.",
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextLow,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(12.dp))

        val eons = listOf(
            Triple("Hadean", "4,600 – 4,000 mya", "Earth forms. Molten surface, no life. Atmosphere is toxic.") to Color(0xFF8B4513),
            Triple("Archean", "4,000 – 2,500 mya", "First single-celled life appears. Stromatolites form. Oxygen begins accumulating.") to Color(0xFF708090),
            Triple("Proterozoic", "2,500 – 541 mya", "Oxygen crisis (Great Oxidation Event). First eukaryotes and multicellular life. Snowball Earth.") to Color(0xFF5070A0),
            Triple("Phanerozoic", "541 mya – Present", "Visible life explodes. Divided into Paleozoic, Mesozoic, and Cenozoic eras.") to Color(0xFF8BBF6A),
        )
        eons.forEach { (data, color) ->
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                        .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), CircleShape),
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row {
                        Text(
                            data.first,
                            style = MaterialTheme.typography.titleSmall,
                            color = color,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            data.second,
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTextLow,
                        )
                    }
                    Text(
                        data.third,
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "Note: The first three eons (Hadean, Archean, Proterozoic) are sometimes grouped as the Precambrian — a vast span covering nearly 90% of Earth's history.",
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextMid,
        )
    }
}

@Composable
private fun MassExtinctionsCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFFE2574C)) {
        val accentColor = Color(0xFFE2574C)
        Text(
            "The Big Five Mass Extinctions",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Earth has experienced five mass extinctions where more than 50% of species vanished. Each reshaped the course of evolution, clearing the way for new groups to rise.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        val extinctions = listOf(
            "End-Ordovician (~444 mya)" to "85% of marine species lost. Caused by a sudden ice age as Gondwana drifted over the South Pole, dropping sea levels and freezing shallow marine habitats.",
            "Late Devonian (~375 mya)" to "75% of species lost. A series of extinction pulses over ~20 million years. Anoxic oceans and possible asteroid impacts killed reef-building organisms and armored fish.",
            "End-Permian (~252 mya)" to "96% of all species lost — the 'Great Dying.' The deadliest extinction ever. Caused by the Siberian Traps volcanic eruption lasting a million years, triggering ocean acidification and anoxia.",
            "End-Triassic (~201 mya)" to "80% of species lost. Massive volcanic eruptions from the Central Atlantic Magmatic Province as Pangaea split apart. Cleared niches for dinosaurs to dominate.",
            "End-Cretaceous (~66 mya)" to "76% of species lost including all non-avian dinosaurs. The Chicxulub asteroid struck Mexico with the force of 10 billion Hiroshima bombs, plunging Earth into an impact winter.",
        )
        extinctions.forEach { (title, desc) ->
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
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.Top) {
            Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = Citrine, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                "Many scientists argue we're now in the Sixth Mass Extinction — the Holocene/Anthropocene extinction — caused by human activity, habitat destruction, and climate change.",
                style = MaterialTheme.typography.bodySmall,
                color = Citrine,
            )
        }
    }
}

@Composable
private fun FossilizationCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFF6FA8C7)) {
        val accentColor = Color(0xFF6FA8C7)
        Text(
            "How Fossils Form",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Fossilization is extraordinarily rare. An organism must die in specific conditions, be buried quickly, and survive millions of years of geological processes. Only a tiny fraction of all life that ever existed has fossilized.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
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
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            )
            Text(
                desc,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
            )
            // Book-style illustration: petrified wood after replacement step
            if (title == "4. Replacement") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        "\u2192 Petrified wood is the most famous example \u2014 quartz replacing ancient tree cells.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextLow,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.weight(1f).padding(end = 10.dp),
                    )
                    BookStyleImage(
                        imageUrl = PALEO_IMG_PETRIFIED_WOOD,
                        contentDescription = "Polished petrified wood slab showing quartz replacing wood grain",
                    )
                }
            }
        }
    }
}

@Composable
private fun FossilTypesCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFF9B7BD8)) {
        val accentColor = Color(0xFF9B7BD8)
        Text(
            "Types of Fossils",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
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
            // Book-style illustration: trilobite after Body Fossils
            if (title == "Body Fossils") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    BookStyleImage(
                        imageUrl = PALEO_IMG_TRILOBITE,
                        contentDescription = "Detailed trilobite fossil in shale",
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "\u2192 Trilobites are among the most iconic body fossils \u2014 500+ million years old.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextLow,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            // Book-style illustration: dinosaur footprint after Trace Fossils
            if (title == "Trace Fossils (Ichnofossils)") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        "\u2192 Trackways reveal how fast dinosaurs moved and whether they traveled in herds.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextLow,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.weight(1f).padding(end = 10.dp),
                    )
                    BookStyleImage(
                        imageUrl = PALEO_IMG_DINO_TRACK,
                        contentDescription = "Dinosaur footprint fossil in sandstone",
                    )
                }
            }
            // Book-style illustration: ammonite after Molds & Casts
            if (title == "Molds & Casts") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    BookStyleImage(
                        imageUrl = PALEO_IMG_AMMONITE,
                        contentDescription = "Polished ammonite fossil showing golden calcite chambers",
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "\u2192 Ammonite chambers often fill with calcite \u2014 a perfect natural cast.",
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
private fun EraHeader(era: String, timeRange: String) {
    Column(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
        Text(
            text = era,
            style = MaterialTheme.typography.labelLarge,
            color = Fossil,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            text = timeRange,
            style = MaterialTheme.typography.labelSmall,
            color = TextMid,
        )
    }
}

@Composable
private fun PeriodCard(
    period: com.rork.rockscout.data.FossilPeriod,
    onClick: () -> Unit,
) {
    DarkCard(
        accent = Fossil,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Fossil.copy(alpha = 0.25f))
                    .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(period.emoji, style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    period.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    period.timeRange,
                    style = MaterialTheme.typography.labelMedium,
                    color = Fossil,
                    fontWeight = FontWeight.Bold,
                )
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = DarkTextMid)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            period.majorEvents.firstOrNull() ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextMid,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TagChip(period.duration, color = Fossil)
        }
    }
}

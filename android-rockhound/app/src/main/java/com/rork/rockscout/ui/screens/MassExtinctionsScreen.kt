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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid

@Composable
fun MassExtinctionsScreen(navController: NavController) {
    ScreenScaffold(title = "Mass Extinctions", onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { MassExtinctionsCard() }
        }
    }
}

@Composable
private fun MassExtinctionsCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFFE2574C)) {
        val accentColor = Color(0xFFE2574C)
        Text("The Big Five Mass Extinctions", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Earth has experienced five mass extinctions where more than 50% of species vanished. Each reshaped the course of evolution, clearing the way for new groups to rise.", style = MaterialTheme.typography.bodyMedium, color = DarkTextMid)
        Spacer(Modifier.height(14.dp))

        val extinctions = listOf(
            "End-Ordovician (~444 mya)" to "85% of marine species lost. Caused by a sudden ice age as Gondwana drifted over the South Pole, dropping sea levels and freezing shallow marine habitats.",
            "Late Devonian (~375 mya)" to "75% of species lost. A series of extinction pulses over ~20 million years. Anoxic oceans and possible asteroid impacts killed reef-building organisms and armored fish.",
            "End-Permian (~252 mya)" to "96% of all species lost — the 'Great Dying.' The deadliest extinction ever. Caused by the Siberian Traps volcanic eruption lasting a million years, triggering ocean acidification and anoxia.",
            "End-Triassic (~201 mya)" to "80% of species lost. Massive volcanic eruptions from the Central Atlantic Magmatic Province as Pangaea split apart. Cleared niches for dinosaurs to dominate.",
            "End-Cretaceous (~66 mya)" to "76% of species lost including all non-avian dinosaurs. The Chicxulub asteroid struck Mexico with the force of 10 billion Hiroshima bombs, plunging Earth into an impact winter.",
        )
        extinctions.forEach { (title, desc) ->
            Text(title, style = MaterialTheme.typography.titleSmall, color = accentColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp, bottom = 4.dp))
            Text(desc, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid)
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.Top) {
            Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = Citrine, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Many scientists argue we're now in the Sixth Mass Extinction — the Holocene/Anthropocene extinction — caused by human activity, habitat destruction, and climate change.", style = MaterialTheme.typography.bodySmall, color = Citrine)
        }
    }
}

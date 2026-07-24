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
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.BookStyleImage
import com.rork.rockscout.ui.components.PALEO_IMG_STROMATOLITE
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Fossil

@Composable
fun GeoTimeScaleScreen(navController: NavController) {
    ScreenScaffold(title = "Geologic Time Scale", onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { GeologicTimeScaleCard() }
        }
    }
}

@Composable
private fun GeologicTimeScaleCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Fossil) {
        Text("The Geologic Time Scale", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Earth's 4.6-billion-year history is divided into eons, eras, periods, and epochs. The divisions are based on major changes in the fossil record — often mass extinctions that mark boundaries between periods.",
            style = MaterialTheme.typography.bodyMedium, color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.Top) {
            BookStyleImage(imageUrl = PALEO_IMG_STROMATOLITE, contentDescription = "Ancient stromatolite fossils — some of the oldest life on Earth")
            Spacer(Modifier.width(10.dp))
            Text("\u2192 Stromatolites from the Archean are the oldest fossils known \u2014 3.5 billion years old.", style = MaterialTheme.typography.bodySmall, color = DarkTextLow, fontStyle = FontStyle.Italic, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))

        val eons = listOf(
            Triple("Hadean", "4,600 – 4,000 mya", "Earth forms. Molten surface, no life. Atmosphere is toxic.") to Color(0xFF8B4513),
            Triple("Archean", "4,000 – 2,500 mya", "First single-celled life appears. Stromatolites form. Oxygen begins accumulating.") to Color(0xFF708090),
            Triple("Proterozoic", "2,500 – 541 mya", "Oxygen crisis (Great Oxidation Event). First eukaryotes and multicellular life. Snowball Earth.") to Color(0xFF5070A0),
            Triple("Phanerozoic", "541 mya – Present", "Visible life explodes. Divided into Paleozoic, Mesozoic, and Cenozoic eras.") to Color(0xFF8BBF6A),
        )
        eons.forEach { (data, color) ->
            Row(modifier = Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color).glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), CircleShape))
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row {
                        Text(data.first, style = MaterialTheme.typography.titleSmall, color = color, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Text(data.second, style = MaterialTheme.typography.labelSmall, color = DarkTextLow)
                    }
                    Text(data.third, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text("Note: The first three eons (Hadean, Archean, Proterozoic) are sometimes grouped as the Precambrian — a vast span covering nearly 90% of Earth's history.", style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
    }
}

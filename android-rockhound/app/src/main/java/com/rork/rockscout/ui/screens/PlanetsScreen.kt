package com.rork.rockscout.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.rork.rockscout.data.PlanetData
import kotlin.math.roundToInt
import com.rork.rockscout.data.PlanetEntry
import com.rork.rockscout.ui.components.TwinklingStars
import com.rork.rockscout.ui.components.ScreenScaffold

private val AuroraGreen = Color(0xFF00E5C9)
private val AuroraPurple = Color(0xFF9B7BD8)
private val AuroraBlue = Color(0xFF5CC98C)
private val PlanetBg = Color(0xFF0D0C08)
private val TextHighW = Color.White
private val TextMidW = Color.White.copy(alpha = 0.7f)
private val TextLowW = Color.White.copy(alpha = 0.45f)

@Composable
fun PlanetsScreen(navController: NavController) {
    var selectedPlanet by remember { mutableStateOf<PlanetEntry?>(null) }

    ScreenScaffold(title = "Planets", onBack = { navController.popBackStack() }) {
        Box(modifier = Modifier.fillMaxSize()) {
            TwinklingStars(modifier = Modifier.fillMaxSize(), starCount = 50)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(PlanetData.allPlanets) { planet ->
                    PlanetRow(planet) { selectedPlanet = it }
                }
            }
        }
    }

    selectedPlanet?.let { planet ->
        PlanetDetailDialog(planet) { selectedPlanet = null }
    }
}

@Composable
private fun PlanetRow(planet: PlanetEntry, onTap: (PlanetEntry) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PlanetBg.copy(alpha = 0.9f))
            .clickable { onTap(planet) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Planet image thumbnail
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(28.dp)),
        ) {
            AsyncImage(
                model = planet.heroImageUrl,
                contentDescription = planet.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
        Spacer(Modifier.padding(end = 12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = planet.name,
                style = MaterialTheme.typography.titleSmall,
                color = TextHighW,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "${planet.type} · ${planet.moons} moon${if (planet.moons == 1) "" else "s"}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMidW,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${planet.distanceFromSunAu} AU · ${String.format("%.0f", planet.orbitalPeriodDays)} day orbit",
                style = MaterialTheme.typography.labelSmall,
                color = TextLowW,
            )
        }
    }
}

@Composable
private fun PlanetDetailDialog(planet: PlanetEntry, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        BackHandler { onDismiss() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PlanetBg)
                .clickable { },
        ) {
        TwinklingStars(modifier = Modifier.fillMaxWidth().height(60.dp), starCount = 20)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .statusBarsPadding(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = planet.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = AuroraGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable { onDismiss() }
                        .padding(8.dp),
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextHighW, modifier = Modifier.size(20.dp))
                }
            }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = planet.type,
                    style = MaterialTheme.typography.bodySmall,
                    color = AuroraPurple,
                )
                Spacer(Modifier.height(12.dp))

                // Planet hero image — full image, not cropped.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black),
                ) {
                    AsyncImage(
                        model = planet.heroImageUrl,
                        contentDescription = planet.name,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit,
                    )
                }
                Spacer(Modifier.height(12.dp))

                Text(
                    text = planet.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMidW,
                )
                Spacer(Modifier.height(12.dp))

                PlanetProperty("Diameter", "${"%,d".format((planet.diameterKm * 0.621371).roundToInt())} miles")
                PlanetProperty("Distance from Sun", "${planet.distanceFromSunAu} AU")
                PlanetProperty("Orbital Period", "${String.format("%.1f", planet.orbitalPeriodDays)} days")
                PlanetProperty("Moons", planet.moons.toString())
                PlanetProperty("Mass", "${planet.massEarth}× Earth")
                PlanetProperty("Gravity", "${"%.1f".format(planet.gravityMs2 * 3.28084)} ft/s²")
                PlanetProperty("Temperature", planet.tempRangeC + " °C")
                PlanetProperty("Atmosphere", planet.atmosphere)
                PlanetProperty("Best Viewing", planet.bestViewing)
                PlanetProperty("Magnitude", planet.apparentMagnitude)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Notable Features",
                    style = MaterialTheme.typography.titleSmall,
                    color = AuroraGreen,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = planet.notableFeatures,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMidW,
                )
        }
    }
    }
}

@Composable
private fun PlanetProperty(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextLowW)
        Text(text = value, style = MaterialTheme.typography.bodySmall, color = TextHighW, fontWeight = FontWeight.SemiBold)
    }
}

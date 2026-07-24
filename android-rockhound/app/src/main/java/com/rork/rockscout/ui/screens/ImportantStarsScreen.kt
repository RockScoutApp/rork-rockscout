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
import com.rork.rockscout.data.StarData
import com.rork.rockscout.data.StarEntry
import com.rork.rockscout.ui.components.TwinklingStars
import com.rork.rockscout.ui.components.ScreenScaffold

private val AuroraGreen = Color(0xFF00E5C9)
private val AuroraPurple = Color(0xFF9B7BD8)
private val StarBg = Color(0xFF0D0C08)
private val TextHighW = Color.White
private val TextMidW = Color.White.copy(alpha = 0.7f)
private val TextLowW = Color.White.copy(alpha = 0.45f)

@Composable
fun ImportantStarsScreen(navController: NavController) {
    var selectedStar by remember { mutableStateOf<StarEntry?>(null) }

    ScreenScaffold(title = "Important Stars", onBack = { navController.popBackStack() }) {
        Box(modifier = Modifier.fillMaxSize()) {
            TwinklingStars(modifier = Modifier.fillMaxSize(), starCount = 50)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(StarData.allStars) { star ->
                    StarRow(star) { selectedStar = it }
                }
            }
        }
    }

    selectedStar?.let { star ->
        StarDetailDialog(star) { selectedStar = null }
    }
}

@Composable
private fun StarRow(star: StarEntry, onTap: (StarEntry) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(StarBg.copy(alpha = 0.9f))
            .clickable { onTap(star) }
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Spectral color dot
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .background(Color(star.spectralColor))
                    .padding(10.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = star.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextHighW,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "${star.constellation} · Mag ${star.apparentMagnitude} · ${star.distanceLy} ly",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMidW,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = star.spectralClass,
                style = MaterialTheme.typography.labelSmall,
                color = AuroraGreen.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun StarDetailDialog(star: StarEntry, onDismiss: () -> Unit) {
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
                .background(StarBg)
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
                    text = star.name,
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
                    text = "${star.constellation} · ${star.hemisphere} hemisphere",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMidW,
                )
                Spacer(Modifier.height(12.dp))

                star.heroImageUrl?.let { url ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp)),
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = star.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                Text(
                    text = star.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMidW,
                )
                Spacer(Modifier.height(12.dp))

                // Properties grid
                StarProperty("Spectral Class", star.spectralClass)
                StarProperty("Apparent Magnitude", star.apparentMagnitude.toString())
                StarProperty("Absolute Magnitude", star.absoluteMagnitude.toString())
                StarProperty("Distance", "${star.distanceLy} light-years")
                StarProperty("Temperature", "${star.temperatureK} K")
                StarProperty("Luminosity", "${star.luminositySolar}× Sun")
                StarProperty("Best Viewing", star.bestViewingMonth)
        }
    }
    }
}

@Composable
private fun StarProperty(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextLowW)
        Text(text = value, style = MaterialTheme.typography.bodySmall, color = TextHighW, fontWeight = FontWeight.SemiBold)
    }
}

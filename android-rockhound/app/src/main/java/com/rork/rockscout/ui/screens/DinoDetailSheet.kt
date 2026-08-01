package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.rork.rockscout.data.DinoDiet
import com.rork.rockscout.data.DinoEntry
import com.rork.rockscout.data.DinoEra
import com.rork.rockscout.data.DinoImageMap
import com.rork.rockscout.ui.components.DinoSizeComparison
import com.rork.rockscout.ui.components.MetricText
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.TextMid

/** Era accent colors */
private val eraColors: Map<DinoEra, Color> = mapOf(
    DinoEra.TRIASSIC to Color(0xFFE8A33D),
    DinoEra.JURASSIC to Color(0xFF8BBF6A),
    DinoEra.CRETACEOUS to Color(0xFF6FBF8A),
    DinoEra.PALEOGENE to Color(0xFF9B7BD8),
    DinoEra.NEOGENE to Color(0xFFE2574C),
    DinoEra.QUATERNARY to Color(0xFF5090B0),
    DinoEra.OTHER to Color(0xFF7CB5EC),
)

private val dietColors: Map<DinoDiet, Color> = mapOf(
    DinoDiet.CARNIVORE to Color(0xFFE2574C),
    DinoDiet.HERBIVORE to Color(0xFF8BBF6A),
    DinoDiet.OMNIVORE to Color(0xFFE8A33D),
    DinoDiet.PISCIVORE to Color(0xFF5090B0),
    DinoDiet.FILTER_FEEDER to Color(0xFF6FBF8A),
    DinoDiet.INSECTIVORE to Color(0xFF9B7BD8),
    DinoDiet.SCAVENGER to Color(0xFF8B7A60),
)

/**
 * Full-page detail popup for a dinosaur entry.
 * Shows the paleoart illustration, size comparison, and all information.
 *
 * @param entry The dinosaur entry to display
 * @param onDismiss Callback when the popup is closed
 */
@Composable
fun DinoDetailSheet(
    entry: DinoEntry,
    onDismiss: () -> Unit,
) {
    val eraColor = eraColors[entry.era] ?: Color(0xFF7CB5EC)
    val dietColor = dietColors[entry.diet] ?: eraColor
    val silhouetteColor = Color(entry.accentColor)
    val imageUrl = DinoImageMap.imageUri(entry)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        eraColor.copy(alpha = 0.15f),
                        Color(0xFF121512),
                        Color(0xFF0E120C),
                    )
                )
            ),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            // Hero image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.5f),
                ) {
                    if (imageUrl != null) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "${entry.name} illustration",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    // Gradient overlay for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    0f to Color.Transparent,
                                    0.6f to Color.Transparent,
                                    1f to Color(0xFF121512),
                                )
                            )
                    )
                    // Close button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f)),
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                        )
                    }
                    // Name + era badge over the image
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp),
                    ) {
                        Text(
                            text = entry.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TagChip(entry.era.label, color = eraColor)
                            TagChip(entry.diet.label, color = dietColor)
                        }
                    }
                }
            }

            // Quick stats row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    MetricStatBlock("Length", entry.length, eraColor)
                    MetricStatBlock("Weight", entry.weight, silhouetteColor)
                    StatBlock("Lived", entry.age, dietColor)
                }
            }

            // Paleoart image label
            item {
                Text(
                    text = "Artist's Rendition",
                    style = MaterialTheme.typography.titleSmall,
                    color = eraColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                )
            }

            // Full-size paleoart image (also shown in hero, but this one is labeled)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp)),
                ) {
                    if (imageUrl != null) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "${entry.name} paleoart illustration",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.5f),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Size comparison section
            item {
                Text(
                    text = "Size Comparison",
                    style = MaterialTheme.typography.titleSmall,
                    color = eraColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1A1812)),
                ) {
                    DinoSizeComparison(
                        entry = entry,
                        accentColor = silhouetteColor,
                    )
                }
                Spacer(Modifier.height(20.dp))
            }

            // Description
            item {
                InfoSection("About", entry.description, eraColor)
            }

            // Habitat
            item {
                InfoSection("Habitat", entry.habitat, eraColor)
            }

            // Fun facts
            if (entry.funFacts.isNotEmpty()) {
                item {
                    Text(
                        text = "Fun Facts",
                        style = MaterialTheme.typography.titleSmall,
                        color = eraColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                    )
                }
                items(entry.funFacts) { fact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(eraColor)
                                .glowingBorder(1.dp, eraColor.copy(alpha = 0.3f), CircleShape),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = fact,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMid,
                        )
                    }
                }
                item { Spacer(Modifier.height(12.dp)) }
            }

            // Fossil locations
            if (entry.foundIn.isNotEmpty()) {
                item {
                    Text(
                        text = "Fossils Found In",
                        style = MaterialTheme.typography.titleSmall,
                        color = eraColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                    )
                }
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        entry.foundIn.forEach { location ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(dietColor.copy(alpha = 0.7f)),
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = location,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkTextMid,
                                )
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(20.dp)) }
            }

            // Period detail
            item {
                InfoSection("Period", "${entry.period} • ${entry.age}", eraColor)
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun StatBlock(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = DarkTextLow,
        )
    }
}

/**
 * Stat block that shows an imperial measurement with long-press metric conversion popup.
 * Long-press the value to see the metric equivalent (meters, tonnes, kg, etc.).
 */
@Composable
private fun MetricStatBlock(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        MetricText(
            text = value,
            color = color,
            fontSize = 16,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = DarkTextLow,
        )
    }
}

@Composable
private fun InfoSection(title: String, body: String, accent: Color) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = accent,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMid,
        )
    }
}

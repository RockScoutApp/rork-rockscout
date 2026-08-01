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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.data.StarData
import com.rork.rockscout.data.StarEntry
import com.rork.rockscout.data.ScreenPdfExporter
import com.rork.rockscout.data.ScreenPdfItem
import com.rork.rockscout.ui.components.TwinklingStars
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.SculptedIconButton
import kotlinx.coroutines.launch

private val AuroraGreen = Color(0xFF00E5C9)
private val AuroraPurple = Color(0xFF9B7BD8)
private val StarBg = Color(0xFF0D0C08)
private val TextHighW = Color.White
private val TextMidW = Color.White.copy(alpha = 0.7f)
private val TextLowW = Color.White.copy(alpha = 0.45f)

@Composable
fun ImportantStarsScreen(navController: NavController) {
    var selectedStar by remember { mutableStateOf<StarEntry?>(null) }
    var isExportingPdf by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val pdfScope = rememberCoroutineScope()

    ScreenScaffold(
        title = "Important Stars",
        onBack = { navController.popBackStack() },
        actions = {
            SculptedIconButton(
                icon = Icons.Filled.PictureAsPdf,
                contentDescription = "Export PDF",
                onClick = {
                    if (isExportingPdf) return@SculptedIconButton
                    isExportingPdf = true
                    pdfScope.launch {
                        val items = StarData.allStars.map { star ->
                            ScreenPdfItem(
                                title = star.name,
                                subtitle = "${star.constellation}  •  Mag ${star.apparentMagnitude}  •  ${star.spectralClass}",
                                accentRgb = AuroraGreen.toArgb(),
                                imageUrl = star.heroImageUrl,
                                fields = listOf(
                                    "Constellation" to star.constellation,
                                    "Apparent Magnitude" to star.apparentMagnitude.toString(),
                                    "Absolute Magnitude" to star.absoluteMagnitude.toString(),
                                    "Distance" to "${star.distanceLy} light-years",
                                    "Spectral Class" to star.spectralClass,
                                    "Temperature" to "${star.temperatureK} K",
                                    "Luminosity" to "${star.luminositySolar}× Sun",
                                    "Best Viewing" to star.bestViewingMonth,
                                    "Hemisphere" to star.hemisphere,
                                ),
                                description = star.description,
                            )
                        }
                        ScreenPdfExporter.export(
                            context = context,
                            docTitle = "Important Stars",
                            fileName = "RockScout_ImportantStars",
                            items = items,
                        )
                        isExportingPdf = false
                    }
                },
                accent = AuroraGreen,
                iconTint = TextHighW,
                size = 36.dp,
                shadowElevation = 3.dp,
                enabled = !isExportingPdf,
            )
        },
    ) {
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
            .padding(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Thumbnail image — all stars have heroImageUrl.
            star.heroImageUrl?.let { url ->
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black),
                ) {
                    AsyncImage(
                        model = url,
                        contentDescription = star.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
                Spacer(Modifier.width(10.dp))
            }
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
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black),
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = star.name,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Fit,
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

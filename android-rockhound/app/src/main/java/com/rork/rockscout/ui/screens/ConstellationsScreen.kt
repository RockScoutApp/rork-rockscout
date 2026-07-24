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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.rork.rockscout.data.ConstellationData
import com.rork.rockscout.data.ConstellationEntry
import com.rork.rockscout.ui.components.ConstellationStarChart
import com.rork.rockscout.ui.components.TwinklingStars
import com.rork.rockscout.ui.components.ScreenScaffold

private val AuroraGreen = Color(0xFF00E5C9)
private val AuroraPurple = Color(0xFF9B7BD8)
private val AuroraBlue = Color(0xFF5CC98C)
private val StarBg = Color(0xFF0D0C08)
private val TextHighW = Color.White
private val TextMidW = Color.White.copy(alpha = 0.7f)
private val TextLowW = Color.White.copy(alpha = 0.45f)

@Composable
fun ConstellationsScreen(navController: NavController) {
    var selectedConstellation by remember { mutableStateOf<ConstellationEntry?>(null) }

    ScreenScaffold(title = "Constellations", onBack = { navController.popBackStack() }) {
        Box(modifier = Modifier.fillMaxSize()) {
            TwinklingStars(modifier = Modifier.fillMaxSize(), starCount = 50)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Group by hemisphere
                val grouped = ConstellationData.allConstellations.distinctBy { it.name }
                    .groupBy { it.hemisphere }
                grouped.forEach { (hemisphere, constellations) ->
                    item {
                        Text(
                            text = hemisphere,
                            style = MaterialTheme.typography.titleMedium,
                            color = AuroraGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                        )
                    }
                    items(constellations) { con ->
                        ConstellationRow(con) { selectedConstellation = it }
                    }
                }
            }
        }
    }

    // Detail dialog
    selectedConstellation?.let { con ->
        ConstellationDetailDialog(con) { selectedConstellation = null }
    }
}

@Composable
private fun ConstellationRow(con: ConstellationEntry, onTap: (ConstellationEntry) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(StarBg.copy(alpha = 0.9f))
            .clickable { onTap(con) }
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = con.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextHighW,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "${con.abbr} · Brightest: ${con.brightestStar}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMidW,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = con.bestSeason.ifBlank { "—" },
                style = MaterialTheme.typography.labelSmall,
                color = AuroraGreen.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun ConstellationDetailDialog(con: ConstellationEntry, onDismiss: () -> Unit) {
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
                .clickable { /* swallow */ },
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
                    text = con.name,
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
                    text = "${con.abbr} · ${con.hemisphere} · ${con.bestSeason}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMidW,
                )
                Spacer(Modifier.height(12.dp))

                // Hero image for famous constellations
                con.heroImageUrl?.let { url ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp)),
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = con.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Star chart
                if (con.starChartStars.isNotEmpty()) {
                    ConstellationStarChart(
                        stars = con.starChartStars,
                        lines = con.starChartLines,
                    )
                    Spacer(Modifier.height(12.dp))
                }

                Text(
                    text = con.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMidW,
                )

                if (con.mythology.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Mythology",
                        style = MaterialTheme.typography.titleSmall,
                        color = AuroraPurple,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = con.mythology,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMidW,
                    )
                }

                if (con.rightAscension.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "RA: ${con.rightAscension} · Dec: ${con.declination}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextLowW,
                    )
                }
        }
    }
    }
}

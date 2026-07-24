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
import com.rork.rockscout.data.DeepSkyObjectData
import com.rork.rockscout.data.DeepSkyObject
import com.rork.rockscout.ui.components.TwinklingStars
import com.rork.rockscout.ui.components.ScreenScaffold

private val AuroraGreen = Color(0xFF00E5C9)
private val AuroraPurple = Color(0xFF9B7BD8)
private val Citrine = Color(0xFFD4C84A)
private val DsoBg = Color(0xFF0D0C08)
private val TextHighW = Color.White
private val TextMidW = Color.White.copy(alpha = 0.7f)
private val TextLowW = Color.White.copy(alpha = 0.45f)

@Composable
fun DeepSkyObjectsScreen(navController: NavController) {
    var selectedDso by remember { mutableStateOf<DeepSkyObject?>(null) }

    ScreenScaffold(title = "Deep Sky Objects", onBack = { navController.popBackStack() }) {
        Box(modifier = Modifier.fillMaxSize()) {
            TwinklingStars(modifier = Modifier.fillMaxSize(), starCount = 50)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val grouped = DeepSkyObjectData.allObjects.groupBy { it.type }
                grouped.forEach { (type, objects) ->
                    item {
                        Text(
                            text = type,
                            style = MaterialTheme.typography.titleMedium,
                            color = AuroraGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                        )
                    }
                    items(objects) { dso ->
                        DsoRow(dso) { selectedDso = it }
                    }
                }
            }
        }
    }

    selectedDso?.let { dso ->
        DsoDetailDialog(dso) { selectedDso = null }
    }
}

@Composable
private fun DsoRow(dso: DeepSkyObject, onTap: (DeepSkyObject) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DsoBg.copy(alpha = 0.9f))
            .clickable { onTap(dso) }
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${dso.catalog} — ${dso.commonName}",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextHighW,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "${dso.constellation} · ${dso.distance} · Mag ${dso.magnitude}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMidW,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = dso.equipment,
                style = MaterialTheme.typography.labelSmall,
                color = Citrine.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun DsoDetailDialog(dso: DeepSkyObject, onDismiss: () -> Unit) {
    BackHandler { onDismiss() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DsoBg)
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dso.commonName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = AuroraGreen,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = dso.catalog,
                        style = MaterialTheme.typography.bodySmall,
                        color = AuroraPurple,
                    )
                }
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
                Spacer(Modifier.height(12.dp))

                dso.heroImageUrl?.let { url ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp)),
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = dso.commonName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                Text(
                    text = dso.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMidW,
                )
                Spacer(Modifier.height(12.dp))

                DsoProperty("Type", dso.type)
                DsoProperty("Constellation", dso.constellation)
                DsoProperty("Distance", dso.distance)
                DsoProperty("Angular Size", dso.angularSize)
                DsoProperty("Magnitude", dso.magnitude)
                DsoProperty("Discovery", "${dso.discoveryYear} — ${dso.discoverer}")
                DsoProperty("Best Season", dso.bestSeason)
                DsoProperty("Hemisphere", dso.hemisphere)
                DsoProperty("Equipment Needed", dso.equipment)
        }
    }
}

@Composable
private fun DsoProperty(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextLowW)
        Text(text = value, style = MaterialTheme.typography.bodySmall, color = TextHighW, fontWeight = FontWeight.SemiBold)
    }
}

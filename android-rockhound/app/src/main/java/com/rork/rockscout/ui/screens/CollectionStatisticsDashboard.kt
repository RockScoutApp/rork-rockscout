package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rork.rockscout.data.CollectionEntry
import com.rork.rockscout.data.RockClass
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.Specimen
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.Igneous
import com.rork.rockscout.ui.theme.Metamorphic
import com.rork.rockscout.ui.theme.Sedimentary
import com.rork.rockscout.ui.theme.Fossil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CollectionStatisticsDashboard(
    collection: List<CollectionEntry>,
) {
    val specimens = remember(collection) {
        collection.mapNotNull { entry ->
            SeedData.specimenById(entry.specimenId)
        }
    }
    val total = specimens.size + collection.count { entry ->
        SeedData.specimenById(entry.specimenId) == null
    }

    if (total == 0) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "Add specimens to your collection to see statistics.",
                style = MaterialTheme.typography.bodyLarge,
                color = DarkTextMid,
            )
        }
        return
    }

    // By rock class
    val byClass = remember(specimens) {
        specimens.groupingBy { it.rockClass }.eachCount()
    }

    // By rarity
    val byRarity = remember(specimens) {
        specimens.groupingBy { it.rarity.ifBlank { "Unknown" } }.eachCount()
            .toList().sortedByDescending { it.second }
    }

    // By color
    val byColor = remember(specimens) {
        specimens.flatMap { it.commonColors }
            .groupingBy { it.lowercase() }
            .eachCount()
            .toList().sortedByDescending { it.second }.take(8)
    }

    // By location found
    val byLocation = remember(collection) {
        collection.map { it.foundAt }
            .filter { it.isNotBlank() }
            .groupingBy { it }
            .eachCount()
            .toList().sortedByDescending { it.second }.take(6)
    }

    // By date added (by month)
    val byMonth = remember(collection) {
        val sdf = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        collection.map { sdf.format(Date(it.addedAt)) }
            .groupingBy { it }
            .eachCount()
            .toList().sortedBy { it.first }.takeLast(6)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Total count hero card
        DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Citrine.copy(alpha = 0.15f))
                        .glowingBorder(2.dp, Citrine.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        total.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        "Total specimens",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "in your collection cabinet",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                    )
                }
            }
        }

        // By rock class
        if (byClass.isNotEmpty()) {
            StatSectionHeader("BY TYPE")
            DarkCard(modifier = Modifier.fillMaxWidth(), accent = Aqua) {
                val maxCount = byClass.values.maxOrNull() ?: 1
                byClass.entries.sortedByDescending { it.value }.forEach { (rockClass, count) ->
                    val color = when (rockClass) {
                        RockClass.IGNEOUS -> Igneous
                        RockClass.SEDIMENTARY -> Sedimentary
                        RockClass.METAMORPHIC -> Metamorphic
                        RockClass.MINERAL -> Citrine
                        RockClass.CRYSTAL -> Amethyst
                        RockClass.FOSSIL -> Fossil
                    }
                    val pct = count.toFloat() / maxCount
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            rockClass.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = color,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.width(120.dp),
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(20.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color.copy(alpha = 0.12f)),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(pct)
                                    .height(20.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(color.copy(alpha = 0.5f)),
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            count.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = color,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(32.dp),
                        )
                    }
                }
            }
        }

        // By rarity
        if (byRarity.isNotEmpty()) {
            StatSectionHeader("BY RARITY")
            DarkCard(modifier = Modifier.fillMaxWidth(), accent = Amethyst) {
                val maxCount = byRarity.maxOfOrNull { it.second } ?: 1
                byRarity.forEach { (rarity, count) ->
                    val pct = count.toFloat() / maxCount
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            rarity,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.width(100.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(18.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(Amethyst.copy(alpha = 0.12f)),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(pct)
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(Amethyst.copy(alpha = 0.5f)),
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            count.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = Amethyst,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(32.dp),
                        )
                    }
                }
            }
        }

        // By color
        if (byColor.isNotEmpty()) {
            StatSectionHeader("BY COLOR")
            DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
                val maxCount = byColor.maxOfOrNull { it.second } ?: 1
                byColor.forEach { (color, count) ->
                    val pct = count.toFloat() / maxCount
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            color.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.width(100.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(18.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(Citrine.copy(alpha = 0.12f)),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(pct)
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(Citrine.copy(alpha = 0.5f)),
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            count.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(32.dp),
                        )
                    }
                }
            }
        }

        // By location found
        if (byLocation.isNotEmpty()) {
            StatSectionHeader("BY LOCATION FOUND")
            DarkCard(modifier = Modifier.fillMaxWidth(), accent = Aqua) {
                val maxCount = byLocation.maxOfOrNull { it.second } ?: 1
                byLocation.forEach { (location, count) ->
                    val pct = count.toFloat() / maxCount
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            location,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Aqua.copy(alpha = 0.12f)),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(pct)
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Aqua.copy(alpha = 0.5f)),
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            count.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = Aqua,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        // By date added
        if (byMonth.isNotEmpty()) {
            StatSectionHeader("ADDED OVER TIME")
            DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
                val maxCount = byMonth.maxOfOrNull { it.second } ?: 1
                byMonth.forEach { (month, count) ->
                    val pct = count.toFloat() / maxCount
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            month,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f),
                        )
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Citrine.copy(alpha = 0.12f)),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(pct)
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Citrine.copy(alpha = 0.5f)),
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            count.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatSectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = Aqua,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
    )
}

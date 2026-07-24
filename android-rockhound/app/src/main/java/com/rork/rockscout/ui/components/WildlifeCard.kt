package com.rork.rockscout.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.rockscout.data.WildlifeEntry
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid

/**
 * Reusable "Common Wildlife" card showing wildlife species organized by category.
 * Uses the DarkCard component with a nature-green (Success) accent.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WildlifeCard(wildlife: WildlifeEntry, modifier: Modifier = Modifier) {
    DarkCard(accent = Success, modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Pets,
                contentDescription = null,
                tint = Success,
                modifier = Modifier.padding(end = 10.dp),
            )
            Text(
                text = "Common Wildlife in This Area",
                style = MaterialTheme.typography.titleSmall,
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(10.dp))

        if (wildlife.mammals.isNotEmpty()) {
            Text(
                text = "Mammals",
                style = MaterialTheme.typography.labelMedium,
                color = Success,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                wildlife.mammals.forEach { animal ->
                    TagChip(text = animal, color = Success)
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (wildlife.birds.isNotEmpty()) {
            Text(
                text = "Birds",
                style = MaterialTheme.typography.labelMedium,
                color = Success,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                wildlife.birds.forEach { bird ->
                    TagChip(text = bird, color = Success)
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (wildlife.reptiles.isNotEmpty()) {
            Text(
                text = "Reptiles & Amphibians",
                style = MaterialTheme.typography.labelMedium,
                color = Success,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                wildlife.reptiles.forEach { reptile ->
                    TagChip(text = reptile, color = Success)
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (wildlife.marine.isNotEmpty()) {
            Text(
                text = "Marine Life",
                style = MaterialTheme.typography.labelMedium,
                color = Success,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                wildlife.marine.forEach { marine ->
                    TagChip(text = marine, color = Success)
                }
            }
        }
    }
}

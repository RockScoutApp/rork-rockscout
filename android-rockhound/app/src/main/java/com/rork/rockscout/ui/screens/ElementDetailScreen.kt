package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rork.rockscout.data.PeriodicTableElements
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.BlackRockBackground
import com.rork.rockscout.ui.components.ELEMENT_CATEGORY_BG
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow

@Composable
fun ElementDetailScreen(
    navController: NavController,
    atomicNumber: Int,
) {
    val element = PeriodicTableElements.byNumber(atomicNumber) ?: run {
        ElementNotFound(navController)
        return
    }
    val color = Color(element.category.colorHex)

    BlackRockBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 16.dp, top = 52.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SculptedIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = { navController.popBackStack() },
                accent = Citrine,
                iconTint = TextHigh,
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "${element.name} (${element.symbol})",
                style = MaterialTheme.typography.headlineMedium,
                color = TextHigh,
                modifier = Modifier.weight(1f),
            )
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            // Hero tile with element-category background texture
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .sculpted(shape = RoundedCornerShape(20.dp), accent = color, shadowElevation = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(color.copy(alpha = 0.15f))
                    .glowingBorder(3.dp, color.copy(alpha = 0.55f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center,
            ) {
                // Element-category background texture
                val bgUrl = ELEMENT_CATEGORY_BG[element.category.label]
                if (bgUrl != null) {
                    coil3.compose.AsyncImage(
                        model = bgUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                    // Color tint overlay for text legibility
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(color.copy(alpha = 0.45f), color.copy(alpha = 0.65f), Color.Black.copy(alpha = 0.4f))
                                )
                            )
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp),
                ) {
                    Text(
                        text = element.symbol,
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 72.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = element.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Atomic number ${element.atomicNumber} · ${element.category.label}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            DataRow(label = "Atomic mass", value = element.atomicMass)
            DataRow(label = "Period", value = element.period.toString())
            DataRow(label = "Group", value = element.group.toString())
            DataRow(label = "Electron configuration", value = element.electronConfiguration)

            Spacer(Modifier.height(16.dp))

            DarkCard(modifier = Modifier.fillMaxWidth(), accent = color) {
                Column {
                    Text(
                        "About",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        element.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            DarkCard(modifier = Modifier.fillMaxWidth(), accent = color) {
                Column {
                    Text(
                        "In rocks, minerals & gems",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        element.inRocks,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                    )
                }
            }
        }
    }
}
}

@Composable
private fun DataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMid,
            fontWeight = FontWeight.Medium,
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextHigh,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ElementNotFound(navController: NavController) {
    BlackRockBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Element not found",
                style = MaterialTheme.typography.headlineMedium,
                color = TextHigh,
            )
            Spacer(Modifier.height(16.dp))
            SculptedIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = { navController.popBackStack() },
                accent = Citrine,
                iconTint = TextHigh,
            )
        }
    }
}

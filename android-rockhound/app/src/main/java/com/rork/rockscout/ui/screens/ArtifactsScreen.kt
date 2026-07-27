package com.rork.rockscout.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.ArtifactSpecimens
import com.rork.rockscout.ui.components.ArtifactListItem
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextMid

@Composable
fun ArtifactsScreen(navController: NavController) {
    val accent = Color(0xFFB87333) // warm clay/ochre artifact accent

    // Explicitly wire the system back button to the NavController so the
    // hardware/gesture back reliably pops the back stack instead of closing
    // the app when the stack is non-empty.
    BackHandler(enabled = true) { navController.popBackStack() }

    ScreenScaffold(
        title = "Artifacts",
        onBack = { navController.popBackStack() },
        background = { RockBackground(it) },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp,
                top = 8.dp,
                bottom = 40.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            // Hero header
            item {
                ArtifactsHero(accent = accent)
                Spacer(Modifier.height(8.dp))
            }

            // Render each section
            ArtifactSpecimens.sections.forEach { section ->
                item(key = "header-${section.title}") {
                    Spacer(Modifier.height(12.dp))
                    ArtifactSectionHeader(
                        title = section.title,
                        subtitle = section.subtitle,
                        accent = accent,
                    )
                    Spacer(Modifier.height(8.dp))
                }
                item(key = "header-label-${section.title}") {
                    Text(
                        text = "${section.artifacts.size} ${if (section.artifacts.size == 1) "entry" else "entries"}",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMid,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                    )
                }
                items(section.artifacts.size, key = { section.artifacts[it].id }) { idx ->
                    val artifact = section.artifacts[idx]
                    ArtifactListItem(
                        artifact = artifact,
                        accent = Color(artifact.accentHex),
                        onClick = {
                            navController.navigate(Routes.artifactDetail(artifact.id))
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtifactsHero(accent: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF3A2A1E),
                        Color(0xFF22160C),
                        Color(0xFF2A1E12),
                    )
                )
            )
            .padding(24.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(accent.copy(alpha = 0.40f), accent.copy(alpha = 0.12f))
                            )
                        )
                        .glowingBorder(2.dp, accent.copy(alpha = 0.60f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.AccountBalance,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "🗿 The Earth keeps their tools",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextHigh,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Arrowheads, hand axes, beads, effigies, and pipes — prehistoric tools and ornaments made of stone, shell, wood, and ceramic. Each artifact tells the story of the people who shaped it and the land they lived on.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextMid,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ArtifactHeroChip("${ArtifactSpecimens.allArtifacts.size} artifacts", accent)
                ArtifactHeroChip("11 families", Color(0xFFDC9A6E))
            }
        }
    }
}

@Composable
private fun ArtifactHeroChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.18f))
            .glowingBorder(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ArtifactSectionHeader(
    title: String,
    subtitle: String,
    accent: Color,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(accent.copy(alpha = 0.18f))
                .glowingBorder(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .padding(10.dp),
        ) {
            Icon(
                Icons.Filled.AccountBalance,
                contentDescription = null,
                tint = accent,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = TextHigh,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
            )
        }
    }
}

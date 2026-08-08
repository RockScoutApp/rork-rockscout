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
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.ArtifactSpecimens
import com.rork.rockscout.data.ScreenPdfExporter
import com.rork.rockscout.data.ScreenPdfItem
import com.rork.rockscout.data.WarRelicSpecimens
import com.rork.rockscout.ui.components.ArtifactListItem
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.launch

@Composable
fun ArtifactsScreen(navController: NavController) {
    val accent = Color(0xFFB87333) // warm clay/ochre artifact accent
    val warRelicAccent = Color(0xFF4A6B7B) // steel-blue war relic accent
    var selectedTab by remember { mutableStateOf(0) } // 0=Artifacts, 1=War Relics
    var showRecentlyAddedOnly by remember { mutableStateOf(false) }
    var isExportingPdf by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val pdfScope = rememberCoroutineScope()

    BackHandler(enabled = true) { navController.popBackStack() }

    val screenTitle = if (selectedTab == 1) "War Relics" else "Artifacts"
    val currentAccent = if (selectedTab == 1) warRelicAccent else accent

    ScreenScaffold(
        title = screenTitle,
        onBack = { navController.popBackStack() },
        background = { RockBackground(it) },
        actions = {
            SculptedIconButton(
                icon = Icons.Filled.PictureAsPdf,
                contentDescription = "Export PDF",
                onClick = {
                    if (isExportingPdf) return@SculptedIconButton
                    isExportingPdf = true
                    pdfScope.launch {
                        val sourceList = if (selectedTab == 1) {
                            WarRelicSpecimens.allWarRelics
                        } else {
                            ArtifactSpecimens.allArtifacts
                        }
                        val items = sourceList.map { art ->
                            ScreenPdfItem(
                                title = art.name,
                                subtitle = "${art.family}  •  ${art.subFamily}  •  ${art.timePeriod}",
                                accentRgb = art.accentHex.toInt(),
                                imageUrl = art.imageUrl,
                                fields = listOf(
                                    "Family" to art.family,
                                    "Sub-Family" to art.subFamily,
                                    (if (selectedTab == 1) "Origin/Side" else "Tribe") to art.tribe,
                                    "Time Period" to art.timePeriod,
                                    "Where Found" to art.whereFound.joinToString(", "),
                                    "How Made" to art.howMade,
                                ).filter { it.second.isNotBlank() },
                                description = art.description,
                            )
                        }
                        ScreenPdfExporter.export(
                            context = context,
                            docTitle = if (selectedTab == 1) "War Relics Catalog" else "Artifacts Catalog",
                            fileName = if (selectedTab == 1) "RockScout_WarRelics" else "RockScout_Artifacts",
                            items = items,
                        )
                        isExportingPdf = false
                    }
                },
                accent = currentAccent,
                iconTint = currentAccent,
                size = 36.dp,
                shadowElevation = 3.dp,
                enabled = !isExportingPdf,
            )
        },
    ) {
        // ── Pill switcher — Artifacts / War Relics ──
        ArtifactPillSwitcher(
            selectedTab = selectedTab,
            onTabSelected = {
                selectedTab = it
                showRecentlyAddedOnly = false
            },
            artifactsAccent = accent,
            warRelicAccent = warRelicAccent,
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp,
                top = 4.dp,
                bottom = 40.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            // Hero header
            item {
                if (selectedTab == 1) {
                    WarRelicsHero(accent = warRelicAccent)
                } else {
                    ArtifactsHero(accent = accent)
                }
                Spacer(Modifier.height(8.dp))
            }

            // Recently Added filter chip
            item {
                val toggleShape = RoundedCornerShape(12.dp)
                val toggleAccent = Citrine
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(toggleShape)
                            .background(if (showRecentlyAddedOnly) toggleAccent else toggleAccent.copy(alpha = 0.12f))
                            .glowingBorder(1.dp, toggleAccent.copy(alpha = if (showRecentlyAddedOnly) 0.9f else 0.45f), toggleShape)
                            .clickable { showRecentlyAddedOnly = !showRecentlyAddedOnly }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = "Recently Added",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (showRecentlyAddedOnly) Color(0xFF1A1306) else toggleAccent,
                            fontWeight = if (showRecentlyAddedOnly) FontWeight.Bold else FontWeight.Medium,
                        )
                    }
                }
            }

            // Render each section — filtered when Recently Added is on
            val activeSections = if (selectedTab == 1) {
                WarRelicSpecimens.sections
            } else {
                ArtifactSpecimens.sections
            }
            activeSections.forEach { section ->
                val filteredArtifacts = if (showRecentlyAddedOnly) {
                    section.artifacts.filter { it.isNew() }
                } else {
                    section.artifacts
                }
                if (filteredArtifacts.isNotEmpty()) {
                    item(key = "header-${section.title}") {
                        Spacer(Modifier.height(12.dp))
                        ArtifactSectionHeader(
                            title = section.title,
                            subtitle = section.subtitle,
                            accent = currentAccent,
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    item(key = "header-label-${section.title}") {
                        Text(
                            text = "${filteredArtifacts.size} ${if (filteredArtifacts.size == 1) "entry" else "entries"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextMid,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                        )
                    }
                    items(filteredArtifacts.size, key = { filteredArtifacts[it].id }) { idx ->
                        val artifact = filteredArtifacts[idx]
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

            // Empty state when Recently Added filter is on and nothing matches
            if (showRecentlyAddedOnly && activeSections.all { section -> section.artifacts.none { it.isNew() } }) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "No ${if (selectedTab == 1) "relics" else "artifacts"} added in the last 7 days. Check back soon!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMid,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

/* ── Pill Switcher ────────────────────────────────────────────────────────── */

@Composable
private fun ArtifactPillSwitcher(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    artifactsAccent: Color,
    warRelicAccent: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val pills = listOf("Artifacts" to 0, "War Relics" to 1)
        pills.forEach { (label, tab) ->
            val isActive = selectedTab == tab
            val pillAccent = if (tab == 0) artifactsAccent else warRelicAccent
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (isActive) pillAccent.copy(alpha = 0.18f) else Color.Transparent
                    )
                    .glowingBorder(
                        1.5.dp,
                        if (isActive) pillAccent else Color(0x33FFFFFF),
                        RoundedCornerShape(24.dp),
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 28.dp, vertical = 8.dp),
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isActive) pillAccent else TextMid,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                )
            }
            if (tab < 1) {
                Spacer(Modifier.width(12.dp))
            }
        }
    }
}

/* ── Hero Headers ─────────────────────────────────────────────────────────── */

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
private fun WarRelicsHero(accent: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF1E2A35),
                        Color(0xFF0C1620),
                        Color(0xFF121E2A),
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
                        Icons.Filled.MilitaryTech,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "🎖️ Relics of the battlefields",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextHigh,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Bullets, buttons, buckles, bayonets, and camp-life items from the Civil War and Revolutionary War. Each relic connects you to the soldiers who carried it, the battles they fought, and the nation they shaped.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextMid,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ArtifactHeroChip("${WarRelicSpecimens.allWarRelics.size} relics", accent)
                ArtifactHeroChip("8 families", Color(0xFF6B8B9B))
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

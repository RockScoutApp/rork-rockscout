package com.rork.rockscout.ui.screens

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.GearItem
import com.rork.rockscout.data.GearKit
import com.rork.rockscout.data.GearGuide
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid

/**
 * Dedicated Gear Guide screen — shows only the clickable gear items
 * (curated kits + the flat all-items catalog). Every row opens its
 * affiliate URL in the system browser. Reached from the Home screen's
 * full-width Gear Guide tile.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GearGuideScreen(navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Gear Guide",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                },
                navigationIcon = {
                    SculptedIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = { navController.popBackStack() },
                        accent = Citrine,
                        iconTint = Citrine,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
        containerColor = Color.Transparent,
    ) { innerPadding ->
        RockBackground {
            LazyColumn(
                modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp,
                    top = innerPadding.calculateTopPadding() + 8.dp, bottom = 40.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Hero header
                item { GearGuideHero() }

                // Curated kits
                item { GearGuideSectionLabel("Curated kits") }
                items(GearGuide.kits, key = { it.id }) { kit ->
                    GearKitCard(kit = kit)
                }

                // All individual gear
                item { Spacer(Modifier.height(8.dp)) }
                item { GearGuideSectionLabel("All gear") }
                item {
                    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Aqua) {
                        GearGuide.allItems.forEachIndexed { index, item ->
                            GearItemRow(item = item, accent = Aqua, onClick = {
                                SafeLinkOpener.openUrl(context, item.url)
                            })
                            if (index != GearGuide.allItems.lastIndex) {
                                Spacer(Modifier.height(10.dp))
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Links will open separately in your browser",
                            style = MaterialTheme.typography.labelSmall,
                            color = Aqua,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GearGuideHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF2D2638),
                        Color(0xFF1E1C25),
                        Color(0xFF2A2230),
                    )
                )
            )
            .padding(24.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Citrine.copy(alpha = 0.22f))
                        .glowingBorder(1.dp, Citrine.copy(alpha = 0.55f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Diamond,
                        contentDescription = null,
                        tint = Citrine,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Spacer(Modifier.width(14.dp))
                Text(
                    text = "Field-ready gear for every rockhound",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Citrine,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Curated kits and individual tools trusted by working geologists and collectors. Tap any item to open it in your browser and shop directly.",
                style = MaterialTheme.typography.bodyLarge,
                color = DarkTextMid,
            )
        }
    }
}

@Composable
private fun GearGuideSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = Aqua,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 2.dp),
    )
}

@Composable
private fun GearKitCard(kit: GearKit) {
    val context = LocalContext.current
    DarkCard(
        accent = Citrine,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Citrine.copy(alpha = 0.22f))
                    .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(kit.emoji, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = kit.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = kit.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${kit.items.size} items",
                    style = MaterialTheme.typography.labelSmall,
                    color = Citrine,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        kit.items.forEach { item ->
            GearItemRow(
                item = item,
                accent = Citrine,
                onClick = { SafeLinkOpener.openUrl(context, item.url) },
            )
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Links will open separately in your browser",
            style = MaterialTheme.typography.labelSmall,
            color = DarkTextMid,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun GearItemRow(item: GearItem, accent: Color, onClick: () -> Unit) {
    val rowShape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(rowShape)
            .background(Color(0xFF3A3830))
            .glowingBorder(1.dp, accent.copy(alpha = 0.35f), rowShape)
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.22f))
                .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(item.emoji, style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.name,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                item.priceBand,
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Icon(
            Icons.Filled.OpenInNew,
            contentDescription = "Open ${item.name}",
            tint = accent,
            modifier = Modifier.size(18.dp),
        )
    }
}

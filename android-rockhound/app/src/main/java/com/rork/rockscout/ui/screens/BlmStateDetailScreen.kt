package com.rork.rockscout.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.BlmData
import com.rork.rockscout.data.BlmDigSite
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid

@Composable
fun BlmStateDetailScreen(
    navController: NavController,
    stateCode: String,
) {
    val state = remember(stateCode) {
        BlmData.allStates.firstOrNull { it.code == stateCode }
    }

    if (state == null) {
        ScreenScaffold(title = "State Guide", onBack = { navController.popBackStack() }) {
            Box(Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
                Text("State information not found.", color = TextMid)
            }
        }
        return
    }

    val accent = Color(state.accentHex)
    val context = LocalContext.current

    ScreenScaffold(
        title = state.name,
        onBack = { navController.popBackStack() },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp, end = 20.dp, bottom = 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // State header card
            item {
                DarkCard(accent = accent, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(accent.copy(alpha = 0.18f))
                                .glowingBorder(2.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(state.silhouetteEmoji, style = MaterialTheme.typography.headlineSmall)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                state.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = DarkTextHigh,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                "BLM: ${state.blmAcreage}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = accent,
                            )
                        }
                    }
                }
            }

            // What you can collect
            item {
                SectionCard(
                    title = "What You Can Collect",
                    icon = "⛏️",
                    accent = Success,
                    content = state.whatYouCanCollect,
                )
            }

            // Quantity limits
            item {
                SectionCard(
                    title = "Quantity Limits",
                    icon = "⚖️",
                    accent = Citrine,
                    content = state.quantityLimits,
                )
            }

            // Tool restrictions
            item {
                SectionCard(
                    title = "Tool Restrictions",
                    icon = "🔧",
                    accent = accent,
                    content = state.toolRestrictions,
                )
            }

            // Permit notes
            item {
                SectionCard(
                    title = "Permits & Authorization",
                    icon = "📋",
                    accent = accent,
                    content = state.permitNotes,
                )
            }

            // Special notes
            item {
                SectionCard(
                    title = "Important Notes",
                    icon = "📌",
                    accent = accent,
                    content = state.specialNotes,
                )
            }

            // BLM dig sites — only from BLM.gov
            if (state.blmDigSites.isNotEmpty()) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "BLM Collecting Sites",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextHigh,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Official BLM.gov-listed public collecting locations in ${state.name}.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMid,
                    )
                }
                items(state.blmDigSites, key = { "${state.code}_${it.name}" }) { site ->
                    BlmDigSiteCard(site = site, accent = accent)
                }
            } else {
                item {
                    DarkCard(accent = accent, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "No specific BLM-designated collecting areas listed for ${state.name}. General BLM rules still apply on public lands — contact the local BLM field office for area recommendations.",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextMid,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            // Verify with BLM office disclaimer
            item {
                DarkCard(accent = Citrine, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            "⚠️ Rules can change",
                            style = MaterialTheme.typography.titleSmall,
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "This information is curated from BLM.gov for general reference. Always verify current rules, area closures, and permit requirements with the local BLM field office before collecting.",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextMid,
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Citrine.copy(alpha = 0.15f))
                                .glowingBorder(2.dp, Citrine.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .clickable {
                                    SafeLinkOpener.openUrl(context, "https://www.blm.gov/programs/recreation/rockhounding")
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = Citrine, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Visit BLM.gov",
                                style = MaterialTheme.typography.labelLarge,
                                color = Citrine,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: String,
    accent: Color,
    content: String,
) {
    DarkCard(accent = accent, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(10.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            content,
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextMid,
        )
    }
}

@Composable
private fun BlmDigSiteCard(site: BlmDigSite, accent: Color) {
    val context = LocalContext.current
    DarkCard(accent = accent, modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.16f))
                        .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    site.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(site.region, style = MaterialTheme.typography.bodySmall, color = accent)
            Spacer(Modifier.height(6.dp))
            Text("What to find: ${site.whatToFind}", style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
            Spacer(Modifier.height(4.dp))
            Text("Directions: ${site.directions}", style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
            Spacer(Modifier.height(4.dp))
            Text("Facilities: ${site.facilities}", style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TagChip(text = site.feeInfo, color = Success)
                TagChip(text = site.difficulty, color = accent)
            }
            if (site.website != null) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accent.copy(alpha = 0.12f))
                        .glowingBorder(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .clickable {
                            SafeLinkOpener.openUrl(context, site.website)
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "View on BLM.gov",
                        style = MaterialTheme.typography.labelMedium,
                        color = accent,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

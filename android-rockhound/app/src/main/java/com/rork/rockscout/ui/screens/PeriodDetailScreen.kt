package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.FossilPeriod
import com.rork.rockscout.data.PeriodOrganism
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Fossil
import com.rork.rockscout.ui.theme.Slate900
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.components.glowingBorder

@Composable
fun PeriodDetailScreen(navController: NavController, periodId: String) {
    val period = SeedData.periodById(periodId)

    if (period == null) {
        RockBackground {
            Box(Modifier.fillMaxSize().padding(40.dp)) {
                Text("Period not found.", color = TextMid)
            }
        }
        return
    }

    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    RockBackground {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                scope.launch {
                    delay(800)
                    isRefreshing = false
                }
            },
            modifier = Modifier.fillMaxSize(),
        ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().navigationBarsPadding(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { PeriodHeader(period) { navController.popBackStack() } }
            item {
                SectionCard("Climate", Fossil) {
                    Text(period.climate, style = MaterialTheme.typography.bodyLarge, color = DarkTextMid)
                }
            }
            item {
                SectionCard("Continents", Fossil) {
                    Text(period.continents, style = MaterialTheme.typography.bodyLarge, color = DarkTextMid)
                }
            }
            item {
                SectionCard("Major Events", Fossil) {
                    period.majorEvents.forEach { event ->
                        Row(
                            modifier = Modifier.padding(vertical = 5.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Icon(
                                Icons.Filled.Timeline,
                                contentDescription = null,
                                tint = Fossil,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(event, style = MaterialTheme.typography.bodyLarge, color = DarkTextMid)
                        }
                    }
                }
            }
            item {
                SectionCard("Key Organisms", Fossil) {
                    period.keyOrganisms.forEach { org ->
                        OrganismCard(org)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
            item {
                SectionCard("Did you know?", Citrine) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Filled.Lightbulb,
                            contentDescription = null,
                            tint = Citrine,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(period.funFact, style = MaterialTheme.typography.bodyLarge, color = DarkTextMid)
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun PeriodHeader(period: FossilPeriod, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(
                Brush.verticalGradient(
                    listOf(Fossil.copy(alpha = 0.40f), Slate900)
                )
            ),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 48.dp)
                .size(44.dp)
                .sculpted(
                    shape = RoundedCornerShape(22.dp),
                    accent = Fossil,
                    shadowElevation = 5.dp,
                    circular = true,
                    onClick = onBack,
                )
                .clip(RoundedCornerShape(22.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp),
        ) {
            Text(text = period.emoji, style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.height(6.dp))
            TagChip(period.era.uppercase(), color = Fossil, filled = true)
            Spacer(Modifier.height(8.dp))
            Text(period.name, style = MaterialTheme.typography.displayMedium, color = TextHigh)
            Text(period.timeRange, style = MaterialTheme.typography.bodyLarge, color = TextMid)
            Spacer(Modifier.height(4.dp))
            Text(
                "Duration: ${period.duration}",
                style = MaterialTheme.typography.labelMedium,
                color = Fossil,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun OrganismCard(org: PeriodOrganism) {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Fossil.copy(alpha = 0.20f))
                    .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(org.emoji, style = MaterialTheme.typography.headlineSmall)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    org.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkTextHigh,
                )
                Text(
                    org.type,
                    style = MaterialTheme.typography.labelMedium,
                    color = Fossil,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    org.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, accent: Color, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, 18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Aqua,
                fontWeight = FontWeight.Bold,
            )
        }
        DarkCard(modifier = Modifier.fillMaxWidth(), accent = accent) { content() }
    }
}

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Fossil
import com.rork.rockscout.ui.theme.TextMid

@Composable
fun GeologicPeriodsScreen(navController: NavController) {
    val periods = SeedData.fossilPeriods
    val paleozoic = remember(periods) { periods.filter { it.era == "Paleozoic" } }
    val mesozoic = remember(periods) { periods.filter { it.era == "Mesozoic" } }
    val cenozoic = remember(periods) { periods.filter { it.era == "Cenozoic" } }
    ScreenScaffold(title = "Geologic Periods", onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    "Each period tells the story of life evolving through deep time. Tap a period to learn more.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMid,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
            item { EraHeader("PALEOZOIC ERA", "541 – 252 mya · ~289 million years") }
            items(paleozoic, key = { it.id }) { period ->
                PeriodCard(period) { navController.navigate(Routes.period(period.id)) }
            }
            item { EraHeader("MESOZOIC ERA", "252 – 66 mya · ~186 million years") }
            items(mesozoic, key = { it.id }) { period ->
                PeriodCard(period) { navController.navigate(Routes.period(period.id)) }
            }
            item { EraHeader("CENOZOIC ERA", "66 mya – Present · ~66 million years") }
            items(cenozoic, key = { it.id }) { period ->
                PeriodCard(period) { navController.navigate(Routes.period(period.id)) }
            }
        }
    }
}

@Composable
private fun EraHeader(era: String, timeRange: String) {
    Column(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
        Text(text = era, style = MaterialTheme.typography.labelLarge, color = Fossil, fontWeight = FontWeight.ExtraBold)
        Text(text = timeRange, style = MaterialTheme.typography.labelSmall, color = TextMid)
    }
}

@Composable
private fun PeriodCard(
    period: com.rork.rockscout.data.FossilPeriod,
    onClick: () -> Unit,
) {
    DarkCard(
        accent = Fossil,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        contentPadding = PaddingValues(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape).background(Fossil.copy(alpha = 0.25f)).glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(period.emoji, style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(period.name, style = MaterialTheme.typography.titleLarge, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(period.timeRange, style = MaterialTheme.typography.labelMedium, color = Fossil, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = DarkTextMid)
        }
        Spacer(Modifier.height(10.dp))
        Text(period.majorEvents.firstOrNull() ?: "", style = MaterialTheme.typography.bodySmall, color = DarkTextMid, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TagChip(period.duration, color = Fossil)
        }
    }
}

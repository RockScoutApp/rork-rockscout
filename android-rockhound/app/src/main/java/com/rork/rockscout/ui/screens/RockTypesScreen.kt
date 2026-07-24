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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.RockGuide
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.rockClassColor
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.DarkTextMid

@Composable
fun RockTypesScreen(navController: NavController) {
    ScreenScaffold(title = "Rock Types", onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    "Every rock falls into one of three major categories based on how it formed. Tap a category to learn more.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
            items(SeedData.guides.size) { index ->
                val guide = SeedData.guides[index]
                GuideCard(guide) { navController.navigate(Routes.guide(guide.id)) }
            }
        }
    }
}

@Composable
private fun GuideCard(guide: RockGuide, onClick: () -> Unit) {
    val accent = rockClassColor(guide.rockClass)
    DarkCard(
        accent = accent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        contentPadding = PaddingValues(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape).background(accent.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center,
            ) { Text(guide.emoji, style = MaterialTheme.typography.headlineMedium) }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    guide.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    guide.rockClass.label.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = accent,
                    fontWeight = FontWeight.Bold,
                )
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = DarkTextMid)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            guide.intro,
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextMid,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

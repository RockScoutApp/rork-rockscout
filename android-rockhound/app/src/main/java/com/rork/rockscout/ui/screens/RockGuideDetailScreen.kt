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
import androidx.compose.material.icons.filled.Check
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
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SpecimenGlyph
import com.rork.rockscout.ui.components.rockClassColor
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.navigation.Routes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid

@Composable
fun RockGuideDetailScreen(navController: NavController, guideId: String) {
    val guide = SeedData.guideById(guideId)
    if (guide == null) {
        RockBackground { Box(Modifier.fillMaxSize().padding(40.dp)) { Text("Guide not found.", color = TextMid) } }
        return
    }
    val accent = rockClassColor(guide.rockClass)
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
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(Brush.verticalGradient(listOf(accent.copy(alpha = 0.40f), Slate900))),
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 12.dp, top = 48.dp)
                            .size(44.dp)
                            .sculpted(
                                shape = CircleShape,
                                accent = accent,
                                shadowElevation = 5.dp,
                                circular = true,
                                onClick = { navController.popBackStack() },
                            )
                            .clip(CircleShape)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center,
                    ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White) }
                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                        Text(guide.emoji, style = MaterialTheme.typography.displayLarge)
                        Text(guide.title, style = MaterialTheme.typography.displayMedium, color = TextHigh)
                    }
                }
            }
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(guide.intro, style = MaterialTheme.typography.bodyLarge, color = TextMid)
                }
            }
            item {
                Section("How it forms", accent) {
                    Text(guide.howItForms, style = MaterialTheme.typography.bodyLarge, color = Color.White)
                }
            }
            item {
                Section("How to spot it", accent) {
                    guide.keyTraits.forEach { trait ->
                        Row(modifier = Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(trait, style = MaterialTheme.typography.bodyLarge, color = Color.White)
                        }
                    }
                }
            }
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text("EXAMPLES", style = MaterialTheme.typography.labelMedium, color = Aqua, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
                    guide.examples.mapNotNull { SeedData.specimenById(it) }.forEach { spec ->
                        DarkCard(
                            accent = accent,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp).clickable { navController.navigate(Routes.specimen(spec.id)) },
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SpecimenGlyph(emoji = spec.emoji, accent = accent, size = 46)
                                Spacer(Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(spec.name, style = MaterialTheme.typography.titleLarge, color = Color.White)
                                    Text(
                                        spec.tagline,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = com.rork.rockscout.ui.components.brightenForText(accent, amount = 0.5f),
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        maxLines = 2,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    )
                                }
                                Text("View ›", color = accent, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
                        Text("ROCK CYCLE", style = MaterialTheme.typography.labelMedium, color = Aqua, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(guide.cycleNote, style = MaterialTheme.typography.bodyLarge, color = Color.White)
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun Section(title: String, accent: Color, content: @Composable () -> Unit) {
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

package com.rork.rockscout.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.StormChaserChannel
import com.rork.rockscout.data.StormChaserData
import com.rork.rockscout.data.WorkScheduler
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.YouTubePlayer
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid

private val StormOrange = Color(0xFFFF6B3D)
private val StormRed = Color(0xFFFF3B30)

@Composable
fun SevereWeatherScreen(navController: NavController) {
    val repo = AppRepository.instance
    val profile by repo.profile.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedChannel by remember { mutableStateOf<StormChaserChannel?>(null) }
    var pendingNotifToggle by remember { mutableStateOf(false) }

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        val notifGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            results[Manifest.permission.POST_NOTIFICATIONS] == true
        if (pendingNotifToggle && notifGranted) {
            repo.setWeatherAlertsEnabled(true)
            WorkScheduler.scheduleWeatherChain(context.applicationContext)
            WorkScheduler.runWeatherCheckNow(context.applicationContext)
        }
        pendingNotifToggle = false
    }

    // Full-screen YouTube player overlay
    if (selectedChannel != null) {
        YouTubePlayer(
            channelUrl = selectedChannel!!.channelUrl,
            onClose = { selectedChannel = null },
        )
        return
    }

    RockBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding(),
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 16.dp, top = 52.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextHigh)
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Severe Weather",
                    style = MaterialTheme.typography.headlineMedium,
                    color = StormOrange,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }

            // ─── Storm Chaser YouTube Channels ───
            Text(
                text = "Storm Chaser Live Coverage",
                style = MaterialTheme.typography.titleLarge,
                color = StormOrange,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Live YouTube streams from storm chasers across the country. Tap to watch in-app.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(12.dp))

            // National channels
            Text(
                text = "NATIONAL COVERAGE",
                style = MaterialTheme.typography.titleSmall,
                color = Citrine,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            )
            StormChaserData.national.forEach { channel ->
                StormChaserCard(channel = channel, onClick = { selectedChannel = channel })
            }

            Spacer(Modifier.height(12.dp))

            // State-specific channels
            Text(
                text = "BY STATE",
                style = MaterialTheme.typography.titleSmall,
                color = Citrine,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            )
            StormChaserData.byState.sortedBy { it.stateOrRegion }.forEach { channel ->
                StormChaserCard(channel = channel, onClick = { selectedChannel = channel })
            }

            // States without dedicated chasers note
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1A1812).copy(alpha = 0.6f))
                    .glowingBorder(1.dp, TextLow.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Text(
                    text = "States without a dedicated chaser (${StormChaserData.statesWithoutDedicatedChaser.size}): " +
                        StormChaserData.statesWithoutDedicatedChaser.joinToString(", ") +
                        " — use the national channels above for coverage.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextLow,
                )
            }

            Spacer(Modifier.height(12.dp))

            // Disclaimer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1A1812).copy(alpha = 0.6f))
                    .glowingBorder(1.dp, StormOrange.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = StormOrange, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Stream availability depends on each chaser being actively live. Channels may show recorded content or be offline between storms.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMid,
                    )
                }
            }

            // ─── NWS Severe Weather Alerts Toggle (moved to bottom) ───
            val weatherAccent = if (profile.weatherAlertsEnabled) StormOrange else TextLow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1A1812).copy(alpha = 0.92f), Color(0xFF120F0A).copy(alpha = 0.88f))
                        )
                    )
                    .glowingBorder(1.dp, weatherAccent.copy(alpha = 0.35f), RoundedCornerShape(20.dp)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .animateContentSize(),
                ) {
                    Text(
                        text = "Severe Weather Alerts",
                        style = MaterialTheme.typography.titleMedium,
                        color = StormOrange,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.radialGradient(
                                        listOf(weatherAccent.copy(alpha = 0.35f), weatherAccent.copy(alpha = 0.10f))
                                    )
                                )
                                .glowingBorder(2.dp, weatherAccent.copy(alpha = 0.60f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Warning, contentDescription = null, tint = weatherAccent)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF0D0C08).copy(alpha = 0.72f),
                                            Color(0xFF0D0C08).copy(alpha = 0.52f),
                                            Color(0xFF0D0C08).copy(alpha = 0.28f),
                                        )
                                    )
                                )
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                        ) {
                            Text(
                                text = if (profile.weatherAlertsEnabled) "Severe weather alerts ON" else "Severe weather alerts OFF",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    shadow = Shadow(color = Color.Black.copy(alpha = 0.8f), offset = Offset(0f, 1f), blurRadius = 4f),
                                ),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = if (profile.weatherAlertsEnabled)
                                    "Instant NWS warnings for your area. Monitors your location independently."
                                else "Turn on for instant severe weather alerts in your area.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), offset = Offset(0f, 1f), blurRadius = 3f),
                                ),
                                color = Color(0xFFF5F0E6),
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Switch(
                            checked = profile.weatherAlertsEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    val perms = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        perms.add(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    pendingNotifToggle = true
                                    permissionsLauncher.launch(perms.toTypedArray())
                                } else {
                                    repo.setWeatherAlertsEnabled(false)
                                    WorkScheduler.cancelWeatherChain(context.applicationContext)
                                }
                            },
                        )
                    }

                    // Severe weather alert type list (shown when enabled)
                    if (profile.weatherAlertsEnabled) {
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0x22FFFFFF))
                        Spacer(Modifier.height(8.dp))
                        SevereWeatherAlertTypes()
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun StormChaserCard(
    channel: StormChaserChannel,
    onClick: () -> Unit,
) {
    val accent = StormOrange
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A1812).copy(alpha = 0.90f), Color(0xFF120F0A).copy(alpha = 0.85f))
                )
            )
            .glowingBorder(1.dp, accent.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Play icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(accent.copy(alpha = 0.25f), accent.copy(alpha = 0.08f))
                        )
                    )
                    .glowingBorder(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.PlayCircle,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(28.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = channel.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextHigh,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = channel.handle,
                    style = MaterialTheme.typography.labelMedium,
                    color = accent.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = channel.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMid,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            // State badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0D0C08).copy(alpha = 0.6f))
                    .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = channel.stateOrRegion,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFFD9B0),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun SevereWeatherAlertTypes() {
    val leftColumn = listOf(
        "Severe Thunderstorm",
        "Tornado",
        "Flash Flood",
        "Hurricane",
        "Tropical Storm",
        "Tsunami",
        "Blizzard",
        "Winter Storm",
    )
    val rightColumn = listOf(
        "Ice Storm",
        "Extreme Heat/Cold",
        "High Wind",
        "Dust Storm",
        "Fire Weather",
        "Red Flag",
        "Smoke & Air Quality",
    )
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                leftColumn.forEach { alert ->
                    Text(
                        text = "\u2022 $alert",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFF5F0E6),
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                rightColumn.forEach { alert ->
                    Text(
                        text = "\u2022 $alert",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFF5F0E6),
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Monitors your location independently.",
            style = MaterialTheme.typography.labelSmall,
            color = TextLow,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

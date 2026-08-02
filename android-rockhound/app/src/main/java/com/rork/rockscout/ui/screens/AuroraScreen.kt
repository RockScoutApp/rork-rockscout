package com.rork.rockscout.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import kotlinx.coroutines.launch
import com.rork.rockscout.data.AuroraRepository
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.FlareClass
import com.rork.rockscout.data.LocationFetcher
import com.rork.rockscout.data.PersistenceManager
import com.rork.rockscout.data.SunspotHistoryTracker
import com.rork.rockscout.data.SolarRegion
import androidx.compose.material.icons.filled.Close
import com.rork.rockscout.data.WorkScheduler
import com.rork.rockscout.ui.components.AuroraSavedSpotsMap
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset as CanvasOffset
import androidx.compose.ui.graphics.Path as CanvasPath
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.roundToInt

/** Aurora tile background image URL. */
const val AURORA_TILE_BG_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/030eff91-2010-41f3-bea6-3188baec1a38.png"

/** Aurora header hero image URL. */
const val AURORA_HEADER_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/0e103349-b2b5-444f-916b-d44b5cfc9d39.png"

/** Aurora subtle sky background texture URL. */
const val AURORA_SKY_BG_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/42afaafc-19ca-41b0-9562-5571e38fbd75.png"

// Aurora accent colors
private val AuroraGreen = Color(0xFF00E5C9)
private val AuroraPurple = Color(0xFF9B7BD8)
private val AuroraBlue = Color(0xFF5CC98C)
private val KpGreen = Color(0xFF30D158)
private val KpYellow = Color(0xFFFFCC00)
private val KpOrange = Color(0xFFFF9500)
private val KpRed = Color(0xFFFF3B30)

private val SDO_WAVELENGTHS = listOf(
    "171" to "Quiet corona",
    "193" to "Corona & flares",
    "304" to "Chromosphere",
    "211" to "Active regions",
    "131" to "Flare plasma",
)

@Composable
fun AuroraScreen(navController: NavController) {
    val repo = AppRepository.instance
    val profile by repo.profile.collectAsStateWithLifecycle()
    val currentLocation by repo.currentLocation.collectAsStateWithLifecycle()
    val auroraData by AuroraRepository.auroraData.collectAsState()
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    var isLoading by remember { mutableStateOf(true) }
    var selectedWavelength by remember { mutableStateOf("304") }
    var showEducation by remember { mutableStateOf(false) }
    var auroraAlertsEnabled by remember { mutableStateOf(PersistenceManager.isAuroraAlertsEnabled()) }
    val auroraSavedSpots by repo.auroraSavedSpots.collectAsStateWithLifecycle()
    var selectedSunspotRegion by remember { mutableStateOf<SolarRegion?>(null) }

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        val notifGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            results[Manifest.permission.POST_NOTIFICATIONS] == true
        if (notifGranted) {
            auroraAlertsEnabled = true
            repo.setAuroraAlertsEnabled(true)
            WorkScheduler.scheduleAuroraChain(context.applicationContext)
            WorkScheduler.runAuroraCheckNow(context.applicationContext)
        }
    }

    // Fetch aurora data on load
    LaunchedEffect(currentLocation) {
        isLoading = true
        AuroraRepository.fetchAll(currentLocation.first, currentLocation.second)
        isLoading = false
    }

    RockBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding(),
        ) {
            // Header with back button
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
                    text = "Aurora Forecaster",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AuroraGreen,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }

            // Hero header image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp)),
            ) {
                AsyncImage(
                    model = AURORA_HEADER_URL,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xFF0D0C08).copy(alpha = 0.7f))
                            )
                        ),
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                ) {
                    Text(
                        text = "Northern Lights Forecast",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    val moon = auroraData.moonPhase
                    if (moon != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${moon.emoji} ${moon.phaseName} · ${moon.illumination.roundToInt()}% illuminated",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = AuroraGreen)
                }
            }

            // ─── Current Conditions Card ───
            AuroraCard(
                title = "Current Conditions",
                accent = AuroraGreen,
            ) {
                val kp = auroraData.currentKp
                val stormScale = AuroraRepository.stormScaleLabel(kp)
                val stormColor = Color(AuroraRepository.stormScaleColor(kp))
                val threshold = auroraData.visibilityThreshold
                val isVisible = auroraData.isAuroraVisible
                val isKpLoading = isLoading && kp <= 0.0

                // Kp gauge bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Column {
                            if (isKpLoading) {
                                Text(
                                    text = "Loading...",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = TextMid,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = "Fetching Kp data",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextLow,
                                )
                            } else {
                                Text(
                                    text = "Kp ${String.format("%.1f", kp)}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = stormColor,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = stormScale,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = stormColor.copy(alpha = 0.8f),
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            if (isKpLoading) {
                                CircularProgressIndicator(
                                    color = AuroraGreen,
                                    modifier = Modifier.size(20.dp),
                                )
                            } else {
                                Text(
                                    text = if (isVisible) "Aurora visible tonight" else "Aurora unlikely",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isVisible) AuroraGreen else TextMid,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = "Threshold: Kp ${String.format("%.1f", threshold)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextLow,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    // Kp gauge bar (0-9)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1C1A14)),
                    ) {
                        val kpFraction = (kp / 9.0).toFloat().coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(kpFraction)
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(KpGreen, KpYellow, KpOrange, KpRed)
                                    )
                                ),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("0", style = MaterialTheme.typography.labelSmall, color = TextLow)
                        Text("5", style = MaterialTheme.typography.labelSmall, color = TextLow)
                        Text("9", style = MaterialTheme.typography.labelSmall, color = TextLow)
                    }
                }

                // Cause chain
                if (auroraData.causeChain.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0x22FFFFFF))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = auroraData.causeChain,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMid,
                    )
                }

                // Moon phase badge
                auroraData.moonPhase?.let { moon ->
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Brightness3, contentDescription = null, tint = AuroraPurple, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${moon.phaseName} · ${moon.illumination.roundToInt()}% illuminated",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMid,
                        )
                        if (moon.illumination > 60) {
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "(bright moon reduces visibility)",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextLow,
                            )
                        }
                    }
                }
            }

            // ─── Solar Flare Card ───
            AuroraCard(title = "Solar Flare Activity", accent = KpOrange) {
                val flares = auroraData.recentFlares
                if (flares.isEmpty()) {
                    Text(
                        text = "No significant flares (M-class or above) detected in the past 6 hours.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMid,
                    )
                } else {
                    val latest = flares.first()
                    val cls = latest.flareClass
                    val clsColor = Color(cls.color)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(clsColor.copy(alpha = 0.2f))
                                .border(1.dp, clsColor, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = AuroraRepository.flareClassLabel(latest.flux),
                                style = MaterialTheme.typography.titleLarge,
                                color = clsColor,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Latest flare",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextLow,
                            )
                            Text(
                                text = latest.timeTag.take(16).replace("T", " ") + " UTC",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMid,
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = flareClassExplanation(cls),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextLow,
                    )
                    if (flares.size > 1) {
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0x22FFFFFF))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Recent flares (${flares.size}):",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextHigh,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(4.dp))
                        flares.take(5).forEach { flare ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                val fc = Color(flare.flareClass.color)
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(fc),
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = AuroraRepository.flareClassLabel(flare.flux),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = fc,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = flare.timeTag.take(16).replace("T", " ") + " UTC",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextLow,
                                )
                            }
                        }
                    }
                }
            }

            // ─── Solar Wind & IMF Card ───
            AuroraCard(title = "Solar Wind & IMF", accent = AuroraBlue) {
                val wind = auroraData.solarWind
                val imf = auroraData.imf
                if (wind == null && imf == null) {
                    Text("Loading solar wind data...", style = MaterialTheme.typography.bodyMedium, color = TextLow)
                } else {
                    wind?.let { w ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text("Wind Speed", style = MaterialTheme.typography.labelMedium, color = TextLow)
                                Text(
                                    text = "${(w.speedKms * 0.621371).roundToInt()} mi/s",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = speedColor(w.speedKms),
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Density", style = MaterialTheme.typography.labelMedium, color = TextLow)
                                Text(
                                    text = "${w.density.roundToInt()} p/in³",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextHigh,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        // Speed gauge
                        val speedFraction = (w.speedKms / 900.0).toFloat().coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF1C1A14)),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(speedFraction)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(speedColor(w.speedKms)),
                            )
                        }
                        Text(
                            text = speedLabel(w.speedKms),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextLow,
                        )
                    }
                    imf?.let { b ->
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0x22FFFFFF))
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text("IMF Bz", style = MaterialTheme.typography.labelMedium, color = TextLow)
                                Text(
                                    text = "${b.bz.roundToInt()} nT",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = if (b.bz < 0) KpRed else KpGreen,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Direction", style = MaterialTheme.typography.labelMedium, color = TextLow)
                                Text(
                                    text = if (b.bz < 0) "South ↓" else "North ↑",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (b.bz < 0) KpRed else KpGreen,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = if (b.bz < 0)
                                "Bz points south — solar wind magnetic field connects with Earth's, allowing energy to pour in and drive aurora."
                            else
                                "Bz points north — limits aurora activity. Aurora becomes more likely if Bz turns south.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMid,
                        )
                    }
                }
            }

            // ─── Active Sunspot Regions Card ───
            if (auroraData.solarRegions.isNotEmpty()) {
                AuroraCard(title = "Active Sunspot Regions", accent = Citrine) {
                    val regions = auroraData.solarRegions
                    Text(
                        text = "${regions.size} active region${if (regions.size > 1) "s" else ""} on the Earth-facing side of the Sun. Tap a region for details.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMid,
                    )
                    Spacer(Modifier.height(12.dp))
                    regions.forEach { region ->
                        val highM = region.mClassProb > 15
                        val highX = region.xClassProb > 1
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedSunspotRegion = region }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (highM || highX) KpOrange.copy(alpha = 0.2f) else Color(0xFF1C1A14)
                                    )
                                    .border(
                                        1.dp,
                                        if (highM || highX) KpOrange.copy(alpha = 0.5f) else Color(0x33FFFFFF),
                                        RoundedCornerShape(10.dp),
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "#${region.number}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (highM || highX) KpOrange else TextMid,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Region ${region.number} · ${region.location}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextHigh,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = "Magnetic: ${region.magneticClass} · Spots: ${region.spotCount}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextLow,
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Row {
                                    ProbBadge("C", region.cClassProb, KpGreen)
                                    Spacer(Modifier.width(4.dp))
                                    ProbBadge("M", region.mClassProb, KpOrange)
                                    Spacer(Modifier.width(4.dp))
                                    ProbBadge("X", region.xClassProb, KpRed)
                                }
                            }
                        }
                    }
                }
            }

            // ─── 24-Hour Trends Card ───
            if (auroraData.kpHistory.isNotEmpty() || auroraData.f107History.isNotEmpty()) {
                AuroraCard(title = "24-Hour Trends", accent = AuroraGreen) {
                    // Kp 24h line chart
                    if (auroraData.kpHistory.isNotEmpty()) {
                        Text(
                            text = "Kp Index — Last 24 Hours",
                            style = MaterialTheme.typography.titleSmall,
                            color = AuroraGreen,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(8.dp))
                        val kpData = auroraData.kpHistory.takeLast(60) // last ~60 readings
                        val threshold = auroraData.visibilityThreshold
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                        ) {
                            val w = size.width
                            val h = size.height
                            val padding = 8f
                            if (kpData.size < 2) return@Canvas
                            // Threshold line (dashed)
                            val thresholdY = h - (threshold.toFloat() / 9f) * (h - 2 * padding) - padding
                            val dashPath = CanvasPath()
                            var x = padding
                            while (x < w) {
                                dashPath.moveTo(x, thresholdY)
                                dashPath.lineTo(x + 6f, thresholdY)
                                x += 12f
                            }
                            drawPath(dashPath, AuroraGreen.copy(alpha = 0.4f), style = Stroke(width = 1.5f))
                            // Kp line
                            val path = CanvasPath()
                            kpData.forEachIndexed { i, entry ->
                                val px = padding + (i.toFloat() / (kpData.size - 1)) * (w - 2 * padding)
                                val py = h - (entry.kp.toFloat() / 9f) * (h - 2 * padding) - padding
                                if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                            }
                            drawPath(path, AuroraGreen, style = Stroke(width = 2f))
                            // Dots above threshold
                            kpData.forEachIndexed { i, entry ->
                                if (entry.kp >= threshold) {
                                    val px = padding + (i.toFloat() / (kpData.size - 1)) * (w - 2 * padding)
                                    val py = h - (entry.kp.toFloat() / 9f) * (h - 2 * padding) - padding
                                    drawCircle(AuroraGreen, 3f, CanvasOffset(px, py))
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("Kp 0", style = MaterialTheme.typography.labelSmall, color = TextLow)
                            Text("Threshold ${String.format("%.1f", threshold)}", style = MaterialTheme.typography.labelSmall, color = AuroraGreen.copy(alpha = 0.6f))
                            Text("Kp 9", style = MaterialTheme.typography.labelSmall, color = TextLow)
                        }
                    }
                    // F10.7 daily chart
                    if (auroraData.f107History.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "F10.7 Radio Flux — 7-Day Daily Values",
                            style = MaterialTheme.typography.titleSmall,
                            color = AuroraPurple,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(8.dp))
                        val fluxData = auroraData.f107History
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                        ) {
                            val w = size.width
                            val h = size.height
                            val padding = 8f
                            if (fluxData.size < 2) return@Canvas
                            val maxFlux = (fluxData.maxOf { it.f107 } * 1.1).toFloat()
                            val minFlux = (fluxData.minOf { it.f107 } * 0.9).toFloat().coerceAtLeast(0f)
                            val range = (maxFlux - minFlux).coerceAtLeast(1f)
                            val path = CanvasPath()
                            fluxData.forEachIndexed { i, entry ->
                                val px = padding + (i.toFloat() / (fluxData.size - 1)) * (w - 2 * padding)
                                val py = h - ((entry.f107.toFloat() - minFlux) / range) * (h - 2 * padding) - padding
                                if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                            }
                            drawPath(path, AuroraPurple, style = Stroke(width = 2f))
                            // Dots at each data point
                            fluxData.forEachIndexed { i, entry ->
                                val px = padding + (i.toFloat() / (fluxData.size - 1)) * (w - 2 * padding)
                                val py = h - ((entry.f107.toFloat() - minFlux) / range) * (h - 2 * padding) - padding
                                drawCircle(AuroraPurple, 3f, CanvasOffset(px, py))
                            }
                        }
                        Text(
                            text = "Range: ${fluxData.minOf { it.f107 }.roundToInt()}–${fluxData.maxOf { it.f107 }.roundToInt()} sfu",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextLow,
                        )
                    }
                }
            }

            // ─── Solar Activity Index Card ───
            auroraData.solarFlux?.let { flux ->
                AuroraCard(title = "Solar Activity Index", accent = AuroraPurple) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text("F10.7 Flux", style = MaterialTheme.typography.labelMedium, color = TextLow)
                            Text(
                                text = "${flux.f107.roundToInt()} sfu",
                                style = MaterialTheme.typography.titleLarge,
                                color = fluxColor(flux.f107),
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        auroraData.probabilities?.let { probs ->
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Flare Probabilities", style = MaterialTheme.typography.labelMedium, color = TextLow)
                                Text(
                                    text = "C: ${probs.cClassProb.roundToInt()}%  M: ${probs.mClassProb.roundToInt()}%  X: ${probs.xClassProb.roundToInt()}%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMid,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = fluxContext(flux.f107),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextLow,
                    )
                }
            }

            // ─── 3-Day Forecast Card ───
            if (auroraData.kpForecast.isNotEmpty()) {
                AuroraCard(title = "3-Day Kp Forecast", accent = AuroraGreen) {
                    val forecast = auroraData.kpForecast.filter { !it.observed }.take(72)
                    val maxKp = forecast.maxOfOrNull { it.kp } ?: 0.0
                    val threshold = auroraData.visibilityThreshold
                    val aboveThreshold = forecast.filter { it.kp >= threshold }
                    if (aboveThreshold.isNotEmpty()) {
                        val peak = aboveThreshold.maxByOrNull { it.kp }
                        Text(
                            text = "Kp is forecast to reach ${String.format("%.1f", peak?.kp ?: 0.0)} around ${peak?.timeTag?.take(13)?.replace("T", " ") ?: ""}. Aurora may be visible from your location.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AuroraGreen,
                            fontWeight = FontWeight.SemiBold,
                        )
                    } else {
                        Text(
                            text = "Kp is not forecast to reach your visibility threshold (${String.format("%.1f", threshold)}) in the next 72 hours.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMid,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    // Simple bar chart
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        items(forecast.take(48)) { entry ->
                            val barHeight = ((entry.kp / 9.0) * 80).toInt().coerceAtLeast(2)
                            val isAbove = entry.kp >= threshold
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .height(barHeight.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (isAbove) AuroraGreen.copy(alpha = 0.8f) else AuroraGreen.copy(alpha = 0.25f)
                                    ),
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Green bars = above your visibility threshold",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextLow,
                    )
                }
            }

            // ─── Live NASA Sun Feed Card ───
            AuroraCard(title = "Live NASA Sun Feed", accent = KpOrange) {
                // Wavelength selector
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(SDO_WAVELENGTHS) { (wl, label) ->
                        val isSelected = selectedWavelength == wl
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) AuroraGreen.copy(alpha = 0.2f) else Color(0xFF1C1A14)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) AuroraGreen else Color(0x33FFFFFF),
                                    RoundedCornerShape(8.dp),
                                )
                                .clickable { selectedWavelength = wl }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = "$wl Å",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) AuroraGreen else TextMid,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                val wlLabel = SDO_WAVELENGTHS.firstOrNull { it.first == selectedWavelength }?.second ?: ""
                Text(
                    text = "$selectedWavelength Å — $wlLabel",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextLow,
                )
                Spacer(Modifier.height(12.dp))
                // SDO image with loading/error states
                val refreshKey = AuroraRepository.sdoRefreshKey()
                var sdoLoadState by remember(selectedWavelength, refreshKey) { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0D0C08)),
                ) {
                    AsyncImage(
                        model = "${AuroraRepository.sdoImageUrl(selectedWavelength)}?t=$refreshKey",
                        contentDescription = "SDO AIA $selectedWavelength Å",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        onState = { state -> sdoLoadState = state },
                    )
                    when (sdoLoadState) {
                        is AsyncImagePainter.State.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(color = AuroraGreen, modifier = Modifier.size(32.dp))
                            }
                        }
                        is AsyncImagePainter.State.Error -> {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Icon(Icons.Filled.WbSunny, contentDescription = null, tint = TextLow, modifier = Modifier.size(32.dp))
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Could not load NASA SDO image",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMid,
                                    textAlign = TextAlign.Center,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "The solar telescope feed may be offline. Try again later.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextLow,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                        else -> {}
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0D0C08).copy(alpha = 0.7f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = "NASA SDO · updates every 15 min",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextLow,
                        )
                    }
                }
            }

            // ─── Viewing Conditions Card ───
            AuroraCard(title = "Viewing Conditions", accent = AuroraBlue) {
                val lat = currentLocation.first
                val threshold = AuroraRepository.kpThresholdForLatitude(lat)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.NightsStay, contentDescription = null, tint = AuroraGreen, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Your latitude: ${String.format("%.1f", lat)}° — aurora visible at Kp ≥ ${String.format("%.1f", threshold)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMid,
                    )
                }
                Spacer(Modifier.height(12.dp))
                auroraData.moonPhase?.let { moon ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Brightness3, contentDescription = null, tint = AuroraPurple, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${moon.emoji} ${moon.phaseName} — ${moon.illumination.roundToInt()}% illuminated",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (moon.illumination < 30) AuroraGreen else TextMid,
                        )
                    }
                    if (moon.illumination < 30) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Dark skies — excellent for aurora viewing",
                            style = MaterialTheme.typography.labelSmall,
                            color = AuroraGreen,
                        )
                    } else if (moon.illumination > 70) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Bright moon may wash out faint aurora",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextLow,
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color(0x22FFFFFF))
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Tips for aurora viewing:",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextHigh,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                listOf(
                    "Find a dark sky away from city lights",
                    "Look north toward the horizon",
                    "Best viewing is between 10 PM and 2 AM",
                    "Clear skies are essential — check cloud cover",
                    "Aurora can appear suddenly and fade quickly",
                ).forEach { tip ->
                    Text(
                        text = "• $tip",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMid,
                        modifier = Modifier.padding(vertical = 2.dp),
                    )
                }
            }

            // ─── Aurora Alerts Toggle ───
            AuroraCard(title = "Aurora Notifications", accent = AuroraGreen) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (auroraAlertsEnabled) "Aurora alerts ON" else "Aurora alerts OFF",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (auroraAlertsEnabled) AuroraGreen else TextLow,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = if (auroraAlertsEnabled)
                                "You'll get a push notification when aurora is likely visible from your location (nighttime only)."
                            else
                                "Turn on to get notified when the northern lights are likely visible from your area.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMid,
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Switch(
                        checked = auroraAlertsEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled) {
                                val perms = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    perms.add(Manifest.permission.POST_NOTIFICATIONS)
                                }
                                permissionsLauncher.launch(perms.toTypedArray())
                            } else {
                                auroraAlertsEnabled = false
                                repo.setAuroraAlertsEnabled(false)
                                WorkScheduler.cancelAuroraChain(context.applicationContext)
                            }
                        },
                    )
                }
            }

            // ─── Kp Alert Threshold ───
            AuroraKpThresholdSlider(
                repo = repo,
                auroraAlertsEnabled = auroraAlertsEnabled,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
            )

            // ─── Education Card (collapsible) ───
            AuroraCard(title = "How Aurora Works", accent = AuroraPurple) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEducation = !showEducation }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (showEducation) "Tap to collapse" else "Tap to learn about space weather",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AuroraPurple,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = AuroraPurple,
                        modifier = Modifier.size(20.dp),
                    )
                }
                if (showEducation) {
                    Spacer(Modifier.height(12.dp))
                    EducationText(
                        "The Chain: Sun → Earth",
                        "The Sun constantly sends charged particles (solar wind) toward Earth. When solar activity increases — from solar flares or coronal holes — more energy reaches Earth's magnetic field, causing aurora.",
                    )
                    Spacer(Modifier.height(8.dp))
                    EducationText(
                        "What is Kp?",
                        "Kp (planetary K-index) measures geomagnetic activity on a 0-9 scale. Higher Kp means aurora is visible farther south. Kp 5 = G1 storm (visible from mid-latitudes), Kp 7+ = G3 storm (visible from much of the US).",
                    )
                    Spacer(Modifier.height(8.dp))
                    EducationText(
                        "Flare Classes (A/B/C/M/X)",
                        "Solar flares are ranked by X-ray brightness: A (background), B (minor), C (small, no aurora impact), M (medium, can cause storms), X (extreme, major aurora). Each class is 10× stronger than the previous.",
                    )
                    Spacer(Modifier.height(8.dp))
                    EducationText(
                        "Why Bz Matters",
                        "Bz is the north-south direction of the solar wind's magnetic field. When Bz points south (negative), it connects with Earth's magnetic field and pumps energy in — driving aurora. North Bz blocks this connection.",
                    )
                    Spacer(Modifier.height(8.dp))
                    EducationText(
                        "Coronal Holes",
                        "Dark areas on the Sun where magnetic field lines open into space. They send fast solar wind that can cause aurora even without a solar flare — often the source of Kp 4-5 storms.",
                    )
                }
            }

            // ─── Saved Spots Section ───
            AuroraSavedSpotsMap(
                spots = auroraSavedSpots,
                onAddSpot = { name, lat, lng ->
                    // Guard against malformed coordinates before persisting.
                    if (lat in -90.0..90.0 && lng in -180.0..180.0) {
                        repo.addAuroraSavedSpot(name, lat, lng)
                    }
                },
                onRemoveSpot = { id -> repo.removeAuroraSavedSpot(id) },
                currentKp = auroraData.currentKp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                onHeaderClick = { /* Opens the full-screen Saved Spots map from inside the component. */ },
            )

            Spacer(Modifier.height(40.dp))
        }
    }

    // ─── Sunspot Region Detail Dialog ───
    selectedSunspotRegion?.let { region ->
        SunspotRegionDetailDialog(region) { selectedSunspotRegion = null }
    }
}

// ─── Helper composables ───

@Composable
private fun AuroraCard(
    title: String,
    accent: Color,
    content: @Composable () -> Unit,
) {
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
            .glowingBorder(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(20.dp)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = accent,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun ProbBadge(label: String, prob: Double, color: Color) {
    val pct = prob.roundToInt()
    Text(
        text = "$label $pct%",
        style = MaterialTheme.typography.labelSmall,
        color = if (pct > 0) color else TextLow,
        fontWeight = if (pct > 0) FontWeight.Bold else FontWeight.Normal,
    )
}

@Composable
private fun EducationText(title: String, body: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = TextHigh,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMid,
        )
    }
}

@Composable
private fun AuroraKpThresholdSlider(
    repo: AppRepository,
    auroraAlertsEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val currentLocation by repo.currentLocation.collectAsStateWithLifecycle()
    val defaultThreshold = AuroraRepository.kpThresholdForLatitude(currentLocation.first)
    val customThreshold = repo.getAuroraKpThreshold()
    val storedValue = customThreshold ?: defaultThreshold.toFloat()

    // Local thumb position makes dragging smooth; repo is only updated on release.
    var sliderPosition by remember(storedValue) { mutableStateOf(storedValue) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A1812).copy(alpha = 0.92f), Color(0xFF120F0A).copy(alpha = 0.88f))
                )
            )
            .glowingBorder(1.dp, AuroraGreen.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(16.dp),
    ) {
        Text(
            text = "Notify me when Kp reaches: ${String.format("%.1f", sliderPosition)}",
            style = MaterialTheme.typography.titleMedium,
            color = if (auroraAlertsEnabled) AuroraGreen else TextLow,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (customThreshold != null)
                "Custom threshold set. Default for your latitude: Kp ${String.format("%.1f", defaultThreshold)}"
            else
                "Default: based on your latitude (Kp ${String.format("%.1f", defaultThreshold)}). Set a custom level to get alerts at your preferred Kp threshold.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMid,
        )
        Spacer(Modifier.height(12.dp))
        Slider(
            value = sliderPosition,
            onValueChange = { newVal ->
                sliderPosition = (newVal * 2).roundToInt() / 2f
            },
            onValueChangeFinished = {
                repo.setAuroraKpThreshold(sliderPosition)
            },
            valueRange = 0f..9f,
            steps = 17,
            enabled = auroraAlertsEnabled,
            colors = SliderDefaults.colors(
                thumbColor = AuroraGreen,
                activeTrackColor = AuroraGreen.copy(alpha = 0.7f),
                inactiveTrackColor = AuroraGreen.copy(alpha = 0.2f),
                disabledThumbColor = TextLow,
                disabledActiveTrackColor = TextLow.copy(alpha = 0.3f),
                disabledInactiveTrackColor = TextLow.copy(alpha = 0.1f),
            ),
        )
        if (customThreshold != null) {
            Spacer(Modifier.height(4.dp))
            SculptedOutlinedButton(
                text = "Use Default (Kp ${String.format("%.1f", defaultThreshold)})",
                onClick = {
                    repo.setAuroraKpThreshold(null)
                    sliderPosition = defaultThreshold.toFloat()
                },
                modifier = Modifier.fillMaxWidth(),
                accent = AuroraGreen,
                icon = Icons.Filled.Bedtime,
            )
        }
        if (!auroraAlertsEnabled) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Enable Aurora alerts above to activate this threshold.",
                style = MaterialTheme.typography.labelSmall,
                color = TextLow,
            )
        }
    }
}

// ─── Helper functions ───

private fun speedColor(speed: Double): Color = when {
    speed > 800 -> KpRed
    speed > 600 -> KpOrange
    speed > 400 -> KpYellow
    else -> KpGreen
}

private fun speedLabel(speed: Double): String = when {
    speed > 800 -> "Extreme"
    speed > 600 -> "Storm levels"
    speed > 400 -> "Elevated"
    else -> "Calm"
}

private fun fluxColor(f107: Double): Color = when {
    f107 > 150 -> KpOrange
    f107 > 70 -> KpYellow
    else -> KpGreen
}

private fun fluxContext(f107: Double): String = when {
    f107 > 150 -> "High activity — consistent with solar maximum conditions."
    f107 > 70 -> "Moderate activity — typical of the rising or declining solar cycle."
    else -> "Low activity — solar minimum conditions."
}

private fun flareClassExplanation(cls: FlareClass): String = when (cls) {
    FlareClass.A -> "A-class flares are background activity — no aurora impact."
    FlareClass.B -> "B-class flares are minor — no aurora impact."
    FlareClass.C -> "C-class flares are small — common, rarely affect aurora."
    FlareClass.M -> "M-class flares are medium — can cause minor to moderate geomagnetic storms and enhance aurora in 1-3 days."
    FlareClass.X -> "X-class flares are extreme — major aurora storms likely within 1-3 days."
}

@Composable
private fun SunspotRegionDetailDialog(region: SolarRegion, onDismiss: () -> Unit) {
    val history = remember(region.number) {
        SunspotHistoryTracker.getRegionHistory(region.number)
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        BackHandler { onDismiss() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D0C08))
                .clickable { },
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Region ${region.number}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Citrine,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable { onDismiss() }
                        .padding(8.dp),
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextHigh, modifier = Modifier.size(20.dp))
                }
            }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${region.location} · Magnetic: ${region.magneticClass} · Spots: ${region.spotCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMid,
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProbBadge("C", region.cClassProb, KpGreen)
                    ProbBadge("M", region.mClassProb, KpOrange)
                    ProbBadge("X", region.xClassProb, KpRed)
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Magnetic Class: ${region.magneticClass}",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(SunspotHistoryTracker.magneticClassColor(region.magneticClass)),
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = SunspotHistoryTracker.magneticClassDescription(region.magneticClass),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMid,
                )
                if (history.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Magnetic Evolution History",
                        style = MaterialTheme.typography.titleSmall,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    history.forEach { snap ->
                        val magColor = Color(SunspotHistoryTracker.magneticClassColor(snap.magneticClass))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier.size(8.dp)
                                    .clip(CircleShape)
                                    .background(magColor),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = snap.date.take(10),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextLow,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = snap.magneticClass,
                                style = MaterialTheme.typography.bodySmall,
                                color = magColor,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "${snap.spotCount} spots",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMid,
                            )
                        }
                    }
                } else {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "No history yet — snapshots are recorded daily when this region appears on the Earth-facing side of the Sun.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextLow,
                    )
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color(0x22FFFFFF))
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Magnetic class evolution typically follows: Alpha → Beta → Beta-Gamma → Beta-Delta as a region grows and becomes more flare-productive.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextLow,
                )
        }
    }
    }
}

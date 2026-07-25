package com.rork.rockscout.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.Success

/** Difficulty tiers for tips. */
enum class TipLevel { BEGINNER, INTERMEDIATE, ADVANCED, EXPERT }

private fun tipLevelLabel(level: TipLevel): String = when (level) {
    TipLevel.BEGINNER -> "Beginner"
    TipLevel.INTERMEDIATE -> "Intermediate"
    TipLevel.ADVANCED -> "Advanced"
    TipLevel.EXPERT -> "Expert"
}

private fun tipLevelColor(level: TipLevel): Color = when (level) {
    TipLevel.BEGINNER -> Success
    TipLevel.INTERMEDIATE -> Citrine
    TipLevel.ADVANCED -> Color(0xFFE89A4F)
    TipLevel.EXPERT -> Color(0xFFE2574C)
}

/** A single tip with its level, body, and associated illustration URL. */
data class FieldTip(
    val title: String,
    val body: String,
    val level: TipLevel,
    val imageUrl: String,
    val icon: ImageVector,
)

// ── Generated illustration URLs (filled in after image generation completes) ──
internal const val IMG_CAMP_TENT_SITE = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/73602a30-dd33-4902-afd2-839de825d33f.png"
internal const val IMG_CAMP_FIRE_SAFETY = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/fad108c5-55ad-493b-a806-2a7cc393c2ca.png"
internal const val IMG_CAMP_FOOD_STORAGE = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/41b65430-a6fd-4579-8bd8-ae12fa416b45.png"
internal const val IMG_CAMP_WEATHER_SHELTER = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/89ca57e4-e8f1-4649-bd3e-50ff72c00f17.png"
internal const val IMG_CAMP_WATER_PURIFY = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/1ad0c761-2e28-4a3c-ae7a-2740dfa9a9f3.png"
internal const val IMG_CAMP_EXTREME_CAMP = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/4cb2630b-5ce6-4c63-9104-f46f106c1711.png"

internal const val IMG_HIKE_FOOTWEAR = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/2052af03-2fb8-4ac6-957d-89f8d9cdfc53.png"
internal const val IMG_HIKE_SUN_PROTECTION = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/15c0d9b9-e9e7-4b43-a382-fa2d4dcabd4c.png"
internal const val IMG_HIKE_NAVIGATION = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/44475a0b-88f1-4914-bca1-973bd35f2a0f.png"
internal const val IMG_HIKE_LAYERING = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/fe9fd35b-05d2-478a-8a70-19cfbcbc9032.png"
internal const val IMG_HIKE_CREEK_CROSSING = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/b3ee765b-c443-474a-8bb2-af503f20b381.png"
internal const val IMG_HIKE_RIDGE_EXPOSURE = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/422ea70b-8969-4670-85e9-867fe8874543.png"

/** Camping tips — beginner to expert. */
val campingTips: List<FieldTip> = listOf(
    FieldTip(
        title = "Pitch on flat, well-drained ground",
        body = "Choose a flat spot free of rocks and roots. Avoid low depressions and dry washes — flash floods can fill them in minutes, even if it's raining miles away. Look for a slight crown so water sheds away from your tent, and clear debris before staking.",
        level = TipLevel.BEGINNER,
        imageUrl = IMG_CAMP_TENT_SITE,
        icon = Icons.Filled.Terrain,
    ),
    FieldTip(
        title = "Contain your fire in a ring",
        body = "Use an existing fire ring when possible. Keep fires small, never leave one unattended, and drown it with water until the ashes are cold to the touch. On BLM land, check current fire restrictions before lighting anything — red flag warnings mean no open flames, including stoves without a shut-off valve.",
        level = TipLevel.BEGINNER,
        imageUrl = IMG_CAMP_FIRE_SAFETY,
        icon = Icons.Filled.LocalFireDepartment,
    ),
    FieldTip(
        title = "Store food and scents properly",
        body = "In bear country, use a bear box, canister, or hang food at least 12 feet up and 6 feet from the trunk. In desert BLM land, locked hard-sided coolers deter rodents and ravens — never leave food in a tent. Pack out all scraps and wrappers; even orange peels attract wildlife.",
        level = TipLevel.INTERMEDIATE,
        imageUrl = IMG_CAMP_FOOD_STORAGE,
        icon = Icons.Filled.Star,
    ),
    FieldTip(
        title = "Read the weather and rig a shelter",
        body = "Watch for cumulonimbus buildup and shifting winds. Pitch your tarp or rain fly before you need it — a mid-storm tarp fight is how poles snap. Orient the low end into the prevailing wind, and guy out every point. In desert wind, weigh stakes with rocks; sand stakes pull out fast.",
        level = TipLevel.INTERMEDIATE,
        imageUrl = IMG_CAMP_WEATHER_SHELTER,
        icon = Icons.Filled.Nightlight,
    ),
    FieldTip(
        title = "Know your water source and treat it",
        body = "Never assume backcountry water is safe. Filter with a pump or squeeze, then chemically treat or UV-treat as a backup. Camel up at reliable sources and carry enough between fills. In arid regions, plan water down to the liter — spring flow reports are often outdated, and a dry spring can end a trip.",
        level = TipLevel.ADVANCED,
        imageUrl = IMG_CAMP_WATER_PURIFY,
        icon = Icons.Filled.WaterDrop,
    ),
    FieldTip(
        title = "Cold-weather and high-altitude bivouac",
        body = "Snow-camping in the high desert demands a 4-season shelter, a closed-cell foam pad under an inflatable, and a water bottle filled with hot water in your bag. Vent the tent to stop condensation freeze. Melt snow for water rather than carrying it. If the forecast drops below your bag's rating, build a snow trench or dig in — exposure kills faster than cold.",
        level = TipLevel.EXPERT,
        imageUrl = IMG_CAMP_EXTREME_CAMP,
        icon = Icons.Filled.Bolt,
    ),
)

/** Hiking tips — beginner to expert. */
val hikingTips: List<FieldTip> = listOf(
    FieldTip(
        title = "Wear broken-in, supportive footwear",
        body = "Trail runners or hiking boots with grippy soles beat sneakers on loose rock. Break them in on short walks before a full day — blisters end trips. Pair with merino socks that dry fast; carry a spare pair and leukotape for hotspots the moment you feel them.",
        level = TipLevel.BEGINNER,
        imageUrl = IMG_HIKE_FOOTWEAR,
        icon = Icons.Filled.Terrain,
    ),
    FieldTip(
        title = "Sun protection is gear, not an afterthought",
        body = "A wide-brim hat, UV sunglasses, and SPF 30+ sunscreen are mandatory in the desert. Reapply every two hours and after sweat. Light-colored long sleeves actually keep you cooler than bare skin and cut UV exposure. Plan the sun-exposed section for early morning or late afternoon.",
        level = TipLevel.BEGINNER,
        imageUrl = IMG_HIKE_SUN_PROTECTION,
        icon = Icons.Filled.Star,
    ),
    FieldTip(
        title = "Carry navigation and know how to use it",
        body = "A phone GPS dies or loses signal. Carry a paper topo map and compass, and know how to take a bearing. Mark waypoints at trailheads and junctions before you lose bars. On BLM land, stay on designated routes — cutting across is illegal and damages the landscape. Tell someone your planned route and return time.",
        level = TipLevel.INTERMEDIATE,
        imageUrl = IMG_HIKE_NAVIGATION,
        icon = Icons.Filled.Terrain,
    ),
    FieldTip(
        title = "Layer for shifting conditions",
        body = "A base layer, a fleece mid, and a wind/rain shell cover 90% of conditions. Avoid cotton — it stays wet and chills you. Start cold: you'll warm up in 10 minutes. Carry the shell even on dry days; desert thunderstorms arrive fast and drop temps 30°F in minutes.",
        level = TipLevel.INTERMEDIATE,
        imageUrl = IMG_HIKE_LAYERING,
        icon = Icons.Filled.Nightlight,
    ),
    FieldTip(
        title = "Cross creeks and loose terrain with intent",
        body = "Unbuckle your pack hip belt before any creek crossing so you can ditch it if you fall. Use trekking poles upstream, face into the current, and shuffle — don't cross your feet. On loose scree, kick a platform with the uphill foot and keep three points of contact. Turn around if the water is above your knees or moving fast.",
        level = TipLevel.ADVANCED,
        imageUrl = IMG_HIKE_CREEK_CROSSING,
        icon = Icons.Filled.WaterDrop,
    ),
    FieldTip(
        title = "Exposed ridge travel and route-finding",
        body = "Ridge travel demands a helmet, a wind shell, and a turnaround time set in advance — not a turnaround point. If you can't see the next cairn or the weather shifts, descend to a saddle and wait it out. Route-find by reading the terrain ahead, not by your phone. Solo exposed routes in storm season are how experienced hikers die; carry a PLB or satellite messenger.",
        level = TipLevel.EXPERT,
        imageUrl = IMG_HIKE_RIDGE_EXPOSURE,
        icon = Icons.Filled.Bolt,
    ),
)

/**
 * Full-page popup showing a list of camping or hiking tips, grouped
 * beginner → expert, each with an illustrated image card.
 *
 * @param mode "Camping Tips" or "Hiking Tips".
 * @param onDismiss Called when the user closes the popup.
 */
@Composable
fun CampingHikingTipsSheet(
    mode: String,
    onDismiss: () -> Unit,
) {
    val tips = remember(mode) {
        when (mode) {
            "Hiking Tips" -> hikingTips
            else -> campingTips
        }
    }
    val accent = remember(mode) {
        when (mode) {
            "Hiking Tips" -> Success
            else -> Citrine
        }
    }
    val sortedTips = remember(tips) { tips.sortedBy { it.level.ordinal } }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF120F0A))) {
            // Ambient dark background gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1B1610),
                                Color(0xFF120F0A),
                                Color(0xFF0C0906),
                            )
                        )
                    ),
            )

            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 16.dp, top = 48.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Slate800)
                            .sculpted(
                                shape = RoundedCornerShape(22.dp),
                                accent = accent,
                                shadowElevation = 4.dp,
                                onClick = onDismiss,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Close tips",
                            tint = DarkTextHigh,
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = mode,
                        style = MaterialTheme.typography.headlineMedium,
                        color = accent,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.weight(1f),
                    )
                }

                // Intro line
                Text(
                    text = "Beginner to expert tips — scroll through every card.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    items(sortedTips, key = { it.title }) { tip ->
                        TipCard(tip = tip, accent = accent)
                    }
                    // Closing note
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Slate900.copy(alpha = 0.7f))
                                .padding(16.dp),
                        ) {
                            Text(
                                text = "Always verify local conditions with the BLM field office before you go. Leave No Trace.",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextMid,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TipCard(
    tip: FieldTip,
    accent: Color,
) {
    val levelColor = tipLevelColor(tip.level)
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = shape, accent = accent, shadowElevation = 6.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .glowingBorder(3.dp, accent.copy(alpha = 0.45f), shape),
    ) {
        // Accent glow overlay at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(accent.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )
        Column(modifier = Modifier.padding(0.dp)) {
            // Image
            TipImage(url = tip.imageUrl, description = tip.title)

            Column(modifier = Modifier.padding(16.dp)) {
                // Level pill + icon row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(levelColor.copy(alpha = 0.16f))
                            .glowingBorder(1.dp, levelColor.copy(alpha = 0.55f), RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = tipLevelLabel(tip.level),
                            style = MaterialTheme.typography.labelMedium,
                            color = levelColor,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(
                        tip.icon,
                        contentDescription = null,
                        tint = accent.copy(alpha = 0.75f),
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = tip.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                    lineHeight = 20.sp,
                )
            }
        }
    }
}

@Composable
private fun TipImage(url: String, description: String) {
    // Animated loading shimmer for when the image is still loading.
    val transition = rememberInfiniteTransition(label = "tip_img_shimmer")
    val shimmer by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "shimmer_alpha",
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(Color(0xFF1A1812).copy(alpha = 0.5f + 0.15f * shimmer)),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = url,
            contentDescription = description,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        // Bottom gradient for legibility of any caption overlap
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Transparent, Color(0xE616140F))
                    )
                )
        )
    }
}

/**
 * Small pill button used to open the tips sheet. Matches the app's pill style.
 */
@Composable
fun TipsPillButton(
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val pillShape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .sculpted(
                shape = pillShape,
                accent = accent,
                shadowElevation = 5.dp,
                onClick = onClick,
            )
            .clip(pillShape)
            .background(Slate800)
            .glowingBorder(2.dp, accent.copy(alpha = 0.6f), pillShape)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = accent,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

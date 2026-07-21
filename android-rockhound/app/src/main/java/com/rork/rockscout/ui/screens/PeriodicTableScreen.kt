package com.rork.rockscout.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rork.rockscout.R
import com.rork.rockscout.data.PeriodicTableElements
import com.rork.rockscout.ui.components.BlackRockBackground
import com.rork.rockscout.ui.components.ELEMENT_CATEGORY_BG
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextMid

@Composable
fun PeriodicTableScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = remember { context.findActivity() }

    DisposableEffect(Unit) {
        val previous = activity?.requestedOrientation
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity?.requestedOrientation =
                previous ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    val close: () -> Unit = { navController.popBackStack() }
    BackHandler(onBack = close)

    val pagerState = rememberPagerState(pageCount = { 4 })
    val elements = remember { PeriodicTableElements.elements }
    val liquids = remember { elements.filter { PeriodicTableElements.stateFor(it.atomicNumber) == PeriodicTableElements.ElementState.LIQUID } }
    val gases = remember { elements.filter { PeriodicTableElements.stateFor(it.atomicNumber) == PeriodicTableElements.ElementState.GAS } }
    val solids = remember { elements.filter { PeriodicTableElements.stateFor(it.atomicNumber).let { s -> s != PeriodicTableElements.ElementState.LIQUID && s != PeriodicTableElements.ElementState.GAS } } }

    val pageTitles = listOf("Periodic Table", "Liquids", "Solids", "Gases")
    val pageAccent = listOf(
        Citrine,
        Color(0xFF4FC3F7),
        Color(0xFFFFD93D),
        Color(0xFF81C784),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Obsidian),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true,
        ) { page ->
            when (page) {
                0 -> PeriodicTableImage()
                1 -> StateListPage(
                    title = "Liquids",
                    subtitle = "Elements that are liquid at room temperature",
                    accent = pageAccent[1],
                    stateIconTint = Color(0xFF4FC3F7),
                    elements = liquids,
                )
                2 -> StateListPage(
                    title = "Solids",
                    subtitle = "Elements that are solid at room temperature",
                    accent = pageAccent[2],
                    stateIconTint = Color(0xFFFFD93D),
                    elements = solids,
                )
                3 -> StateListPage(
                    title = "Gases",
                    subtitle = "Elements that are gaseous at room temperature",
                    accent = pageAccent[3],
                    stateIconTint = Color(0xFF81C784),
                    elements = gases,
                )
            }
        }

        // Persistent top-left close button
        Box(
            modifier = Modifier
                .padding(start = 12.dp, top = 8.dp)
                .size(44.dp)
                .sculpted(
                    shape = CircleShape,
                    accent = Citrine,
                    shadowElevation = 5.dp,
                    circular = true,
                    onClick = close,
                )
                .clip(CircleShape)
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Close",
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }

        // Page title + indicator dots (top-center)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = pageTitles[pagerState.currentPage],
                fontSize = 15.sp,
                color = TextHigh,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(4) { i ->
                    val selected = pagerState.currentPage == i
                    Box(
                        modifier = Modifier
                            .size(if (selected) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) pageAccent[i]
                                else TextHigh.copy(alpha = 0.35f)
                            )
                            .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), CircleShape),
                    )
                }
            }
        }


    }
}

@Composable
private fun PeriodicTableImage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp, bottom = 4.dp, start = 4.dp, end = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFDFD5BC))
            .glowingBorder(1.dp, Color(0xFFDFD5BC).copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.periodic_table),
            contentDescription = "Periodic table of elements",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun StateListPage(
    title: String,
    subtitle: String,
    accent: Color,
    stateIconTint: Color,
    elements: List<PeriodicTableElements.Element>,
) {
    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    BackHandler(enabled = expandedIndex != null) { expandedIndex = null }

    BlackRockBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp, bottom = 12.dp, start = 56.dp, end = 56.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(stateIconTint)
                        .glowingBorder(2.dp, TextHigh.copy(alpha = 0.4f), CircleShape),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "$title  ·  ${elements.size}",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextHigh,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextMid,
                modifier = Modifier.padding(start = 18.dp, top = 2.dp),
            )
            Spacer(Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                itemsIndexed(elements, key = { _, e -> e.atomicNumber }) { index, element ->
                    StateListCard(
                        element = element,
                        accent = accent,
                        stateIconTint = stateIconTint,
                        expanded = expandedIndex == index,
                        onToggle = { expandedIndex = if (expandedIndex == index) null else index },
                    )
                }
            }
        }
    }
}

@Composable
private fun StateListCard(
    element: PeriodicTableElements.Element,
    accent: Color,
    stateIconTint: Color,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    val categoryColor = Color(element.category.colorHex)
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(200),
        label = "arrow",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = RoundedCornerShape(14.dp), accent = categoryColor, shadowElevation = 5.dp, onClick = onToggle)
            .clip(RoundedCornerShape(14.dp))
            .background(Slate900.copy(alpha = 0.88f))
            .glowingBorder(3.dp, categoryColor.copy(alpha = 0.75f), RoundedCornerShape(14.dp)),
    ) {
        // Vibrant category-colored top accent stripe
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(categoryColor.copy(alpha = 0.95f), categoryColor.copy(alpha = 0.25f))
                    )
                ),
        )

        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Symbol tile with element-category background texture
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(categoryColor.copy(alpha = 0.3f))
                        .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    val bgUrl = ELEMENT_CATEGORY_BG[element.category.label]
                    if (bgUrl != null) {
                        coil3.compose.AsyncImage(
                            model = bgUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    // Category color tint overlay for text legibility
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(categoryColor.copy(alpha = 0.55f), categoryColor.copy(alpha = 0.35f))
                                )
                            )
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = element.symbol,
                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = element.atomicNumber.toString(),
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                // Name + mass
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = element.name,
                        fontSize = 16.sp,
                        color = TextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${element.atomicMass} u  ·  ${element.category.label}",
                        fontSize = 11.sp,
                        color = TextMid,
                        fontWeight = FontWeight.Medium,
                    )
                }
                // State dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(stateIconTint)
                        .glowingBorder(2.dp, TextHigh.copy(alpha = 0.4f), CircleShape),
                )
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
                    else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = categoryColor,
                    modifier = Modifier
                        .size(26.dp)
                        .graphicsLayer(rotationZ = arrowRotation),
                )
            }

            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Slate800.copy(alpha = 0.75f))
                        .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    InfoLine("State", PeriodicTableElements.stateFor(element.atomicNumber).label, accent)
                    InfoLine("Atomic #", element.atomicNumber.toString(), accent)
                    InfoLine("Mass", "${element.atomicMass} u", accent)
                    InfoLine("Category", element.category.label, accent)
                    InfoLine("Electron config", element.electronConfiguration, accent)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = element.summary,
                        fontSize = 12.sp,
                        color = TextHigh,
                        lineHeight = 17.sp,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "In rocks & minerals",
                        fontSize = 11.sp,
                        color = accent,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = element.inRocks,
                        fontSize = 12.sp,
                        color = TextMid,
                        lineHeight = 17.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String, accent: Color) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label: ",
            fontSize = 12.sp,
            color = accent,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = TextHigh,
        )
    }
}

private fun android.content.Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is android.content.ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

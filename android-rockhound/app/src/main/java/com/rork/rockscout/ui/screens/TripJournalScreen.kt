package com.rork.rockscout.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.navigation.safePopBackStack
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import kotlinx.coroutines.launch

/**
 * Merged Trip Planner & Field Journal screen. The pill switcher lets the user
 * flip between Trip Planner and Field Journal. To avoid the native crashes seen
 * with two OSMDroid MapViews alive inside a HorizontalPager, the content uses
 * AnimatedContent so only one tab is composed at a time while still keeping a
 * smooth horizontal slide feel.
 */
@Composable
fun TripJournalScreen(
    navController: NavController,
    initialTabIndex: Int = 0,
) {
    var currentPage by remember { mutableIntStateOf(initialTabIndex.coerceIn(0, 1)) }
    val scope = rememberCoroutineScope()

    val screenTitle = when (currentPage) {
        1 -> "Field Journal"
        else -> "Trip Planner"
    }

    // System-back handler: if the user is on this combined screen, a back press
    // should leave the combined screen, not fall through to the activity.
    // The debounced safe pop helper prevents crashes from rapid presses.
    BackHandler { navController.safePopBackStack() }

    ScreenScaffold(
        title = screenTitle,
        onBack = { navController.safePopBackStack() },
        background = { RockBackground(it) },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Pill switcher ──
            TripJournalPillSwitcher(
                currentPage = currentPage,
                onPageSelected = { page -> currentPage = page },
            )

            AnimatedContent<Int>(
                targetState = currentPage,
                modifier = Modifier.fillMaxSize(),
                transitionSpec = {
                    val direction = if (targetState > initialState) 1 else -1
                    (slideInHorizontally { width -> width * direction } + fadeIn())
                        .togetherWith(
                            slideOutHorizontally { width -> -width * direction } + fadeOut()
                        )
                },
                label = "tripJournalTab",
            ) { page ->
                // Only the active page is composed, so only one MapView can be
                // alive at a time. This eliminates the freeze/close on tab switch.
                when (page) {
                    0 -> TripPlannerScreen(navController = navController, embedded = true)
                    1 -> FieldJournalScreen(navController = navController, embedded = true)
                }
            }
        }
    }
}

@Composable
private fun TripJournalPillSwitcher(
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val pills = listOf("Trip Planner" to 0, "Field Journal" to 1)
        pills.forEach { (label, page) ->
            val isActive = currentPage == page
            val accent = if (page == 0) Citrine else Aqua
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (isActive) accent.copy(alpha = 0.18f) else Color.Transparent
                    )
                    .glowingBorder(
                        1.5.dp,
                        if (isActive) accent else Color(0x33FFFFFF),
                        RoundedCornerShape(24.dp),
                    )
                    .clickable { onPageSelected(page) }
                    .padding(horizontal = 28.dp, vertical = 8.dp),
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isActive) accent else DarkTextMid,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                )
            }
            if (page < 1) {
                Spacer(Modifier.width(12.dp))
            }
        }
    }
}

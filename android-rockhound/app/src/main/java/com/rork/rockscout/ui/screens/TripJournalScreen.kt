package com.rork.rockscout.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import kotlinx.coroutines.launch

/**
 * Merged Trip Planner & Field Journal screen — a single tappable tile on the
 * home screen opens this swipeable two-tab page (Trip Planner | Field Journal)
 * using the same pill-switcher + HorizontalPager pattern as the Friends &
 * Messages screen. Both tab pages reuse the existing TripPlannerScreen and
 * FieldJournalScreen composables in embedded mode (no inner scaffold), so all
 * current functionality (add, edit, archive, share, detail sheets, etc.)
 * works exactly the same.
 */
@Composable
fun TripJournalScreen(
    navController: NavController,
    initialTabIndex: Int = 0,
) {
    val pagerState = rememberPagerState(initialPage = initialTabIndex.coerceIn(0, 1)) { 2 }
    val scope = rememberCoroutineScope()

    val screenTitle = when (pagerState.currentPage) {
        1 -> "Field Journal"
        else -> "Trip Planner"
    }

    ScreenScaffold(
        title = screenTitle,
        onBack = { navController.popBackStack() },
        background = { RockBackground(it) },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Pill switcher ──
            TripJournalPillSwitcher(
                currentPage = pagerState.currentPage,
                onPageSelected = { page ->
                    scope.launch { pagerState.animateScrollToPage(page) }
                },
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 0.dp,
            ) { page ->
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

package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.Trip
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import androidx.compose.ui.unit.sp
import com.rork.rockscout.ui.theme.TextMid
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TripCalendarScreen(navController: NavController) {
    val repo = AppRepository.instance
    val trips by repo.trips.collectAsStateWithLifecycle()
    val archivedTrips by repo.archivedTrips.collectAsStateWithLifecycle()

    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }

    val allTrips = trips + archivedTrips
    val monthLabel = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonth.time)

    // Build calendar grid
    val firstDay = currentMonth.clone() as Calendar
    firstDay.set(Calendar.DAY_OF_MONTH, 1)
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = firstDay.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday

    // Trip date map
    val tripsByDate = remember(allTrips) {
        allTrips.groupBy { trip ->
            val cal = Calendar.getInstance().apply { timeInMillis = trip.date }
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }
    }

    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    // Upcoming and past trips
    val upcomingTrips = allTrips.filter { it.date >= today }.sortedBy { it.date }
    val pastTrips = allTrips.filter { it.date < today }.sortedByDescending { it.date }

    ScreenScaffold(title = "Trip Calendar", onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Month navigation
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Citrine.copy(alpha = 0.15f))
                            .clickable {
                                currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous month", tint = Citrine)
                    }
                    Text(
                        text = monthLabel,
                        style = MaterialTheme.typography.titleLarge,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Citrine.copy(alpha = 0.15f))
                            .clickable {
                                currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.ChevronRight, contentDescription = "Next month", tint = Citrine)
                    }
                }
            }

            // Day headers
            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                    dayNames.forEach { day ->
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextLow,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // Calendar grid
            item {
                Column {
                    // Empty cells before first day
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (i in 0 until firstDayOfWeek) {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        }
                        for (day in 1..daysInMonth) {
                            val dateCal = (currentMonth.clone() as Calendar).apply {
                                set(Calendar.DAY_OF_MONTH, day)
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            val dateMillis = dateCal.timeInMillis
                            val isToday = dateMillis == today
                            val dayTrips = tripsByDate[dateMillis] ?: emptyList()

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isToday) Citrine.copy(alpha = 0.2f)
                                        else if (dayTrips.isNotEmpty()) Color(0xFF1A1812)
                                        else Color.Transparent
                                    )
                                    .clickable {
                                        selectedDate = if (dayTrips.isNotEmpty()) dateMillis else null
                                    }
                                    .padding(2.dp),
                            ) {
                                Column {
                                    Text(
                                        text = day.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isToday) Citrine else TextMid,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                    )
                                    if (dayTrips.isNotEmpty()) {
                                        dayTrips.take(2).forEach { trip ->
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .clip(CircleShape)
                                                        .background(if (trip.isArchived) TextLow else Citrine),
                                                )
                                                Spacer(Modifier.width(2.dp))
                                                Text(
                                                    text = trip.name.take(8),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (trip.isArchived) TextLow else TextHigh,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    fontSize = 8.sp,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Selected date trips or upcoming trips
            val displayTrips = if (selectedDate != null) {
                tripsByDate[selectedDate] ?: emptyList()
            } else {
                upcomingTrips
            }

            item {
                Text(
                    text = if (selectedDate != null)
                        "Trips on ${SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(selectedDate!!))}"
                    else "Upcoming Trips",
                    style = MaterialTheme.typography.titleMedium,
                    color = Citrine,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            items(displayTrips) { trip ->
                CalendarTripRow(trip, navController)
            }


            if (selectedDate == null && pastTrips.isNotEmpty()) {
                item {
                    Text(
                        text = "Past Trips",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextLow,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                    )
                }
                items(pastTrips.take(10)) { trip ->
                    CalendarTripRow(trip, navController, isPast = true)
                }
            }
        }
    }
}

@Composable
private fun CalendarTripRow(trip: Trip, navController: NavController, isPast: Boolean = false) {
    val dateText = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date(trip.date))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1812))
            .clickable { navController.navigate(com.rork.rockscout.ui.navigation.Routes.TRIP_PLANNER) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = trip.name,
                style = MaterialTheme.typography.titleSmall,
                color = if (isPast) TextLow else TextHigh,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "$dateText · ${trip.stops.size} stop${if (trip.stops.size == 1) "" else "s"}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMid,
            )
            if (trip.stops.isNotEmpty()) {
                Text(
                    text = trip.stops.take(3).joinToString(" → ") { it.locationName },
                    style = MaterialTheme.typography.labelSmall,
                    color = TextLow,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (!isPast) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (trip.isArchived) TextLow else Aqua),
            )
        }
    }
}

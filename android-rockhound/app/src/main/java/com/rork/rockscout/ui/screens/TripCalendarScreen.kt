package com.rork.rockscout.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.R
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.PersistenceManager
import com.rork.rockscout.data.Trip
import com.rork.rockscout.ui.components.DeleteConfirmDialog
import com.rork.rockscout.ui.components.MapTileCacheManager
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.ShareCardImage
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
fun TripCalendarScreen(navController: NavController) {
    val repo = AppRepository.instance
    val trips by repo.trips.collectAsStateWithLifecycle()
    val archivedTrips by repo.archivedTrips.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }

    // Editing / detail state
    var editingTrip by remember { mutableStateOf<Trip?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var detailTrip by remember { mutableStateOf<Trip?>(null) }
    var shareToProfileTrip by remember { mutableStateOf<Trip?>(null) }
    var pendingDeleteTrip by remember { mutableStateOf<Trip?>(null) }
    var pendingArchiveTrip by remember { mutableStateOf<Trip?>(null) }

    // Drag-and-drop reschedule state
    var draggingTrip by remember { mutableStateOf<Trip?>(null) }
    var dropTargetDate by remember { mutableStateOf<Long?>(null) }
    val dayCellRects = remember { mutableStateMapOf<Long, Rect>() }
    val haptic = LocalHapticFeedback.current
    val listState = rememberLazyListState()

    val onDragStart: (Trip) -> Unit = { t ->
        draggingTrip = t
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        scope.launch { listState.scrollToItem(2) }
    }
    val onDragMove: (Offset) -> Unit = { finger ->
        dropTargetDate = dayCellRects.entries
            .firstOrNull { it.value.contains(finger) }
            ?.key
    }
    val onDragEnd: () -> Unit = {
        dropTargetDate?.let { newDate ->
            draggingTrip?.let { t ->
                repo.saveTrip(t.copy(date = newDate))
                currentMonth = Calendar.getInstance().apply { timeInMillis = newDate }
            }
        }
        draggingTrip = null
        dropTargetDate = null
    }
    val onDragCancel: () -> Unit = {
        draggingTrip = null
        dropTargetDate = null
    }

    BackHandler(
        enabled = showEditor || detailTrip != null || shareToProfileTrip != null ||
            pendingDeleteTrip != null || pendingArchiveTrip != null || draggingTrip != null,
    ) {
        when {
            showEditor -> { editingTrip = null; showEditor = false }
            detailTrip != null -> detailTrip = null
            shareToProfileTrip != null -> shareToProfileTrip = null
            pendingDeleteTrip != null -> pendingDeleteTrip = null
            pendingArchiveTrip != null -> pendingArchiveTrip = null
            draggingTrip != null -> { draggingTrip = null; dropTargetDate = null }
        }
    }

    val allTrips = trips + archivedTrips
    val monthLabel = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonth.time)

    val firstDay = currentMonth.clone() as Calendar
    firstDay.set(Calendar.DAY_OF_MONTH, 1)
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = firstDay.get(Calendar.DAY_OF_WEEK) - 1

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

    val upcomingTrips = allTrips.filter { it.date >= today }.sortedBy { it.date }
    val pastTrips = allTrips.filter { it.date < today }.sortedByDescending { it.date }

    val displayTrips = if (selectedDate != null) {
        tripsByDate[selectedDate] ?: emptyList()
    } else {
        upcomingTrips
    }

    ScreenScaffold(
        title = "Calendar",
        onBack = { navController.popBackStack() },
        background = { innerContent ->
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.trip_planner_background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                AsyncImage(
                    model = "https://r2-pub.rork.com/attachments/78k8yy4tgahby3o9opb6j.png",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.42f),
                                    Color.Black.copy(alpha = 0.52f),
                                    Color.Black.copy(alpha = 0.62f),
                                    Color.Black.copy(alpha = 0.72f),
                                )
                            )
                        )
                )
                innerContent()
            }
        },
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header row: Plan New Trip button
            item {
                val pillShape = RoundedCornerShape(50.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Trip Calendar",
                        style = MaterialTheme.typography.titleLarge,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                    Box(
                        modifier = Modifier
                            .sculpted(
                                shape = pillShape,
                                accent = Citrine,
                                shadowElevation = 6.dp,
                                onClick = { editingTrip = null; showEditor = true },
                            )
                            .clip(pillShape)
                            .background(Slate800)
                            .glowingBorder(2.dp, Citrine, pillShape)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Explore,
                                contentDescription = null,
                                tint = Citrine,
                                modifier = Modifier.size(14.dp),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Plan New Trip",
                                style = MaterialTheme.typography.labelSmall,
                                color = Citrine,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                fontSize = 11.sp,
                                lineHeight = 13.sp,
                            )
                        }
                    }
                }
            }

            // Month navigation
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1A1812).copy(alpha = 0.85f))
                        .border(1.dp, Color(0xFF7CB5EC).copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF7CB5EC).copy(alpha = 0.15f))
                            .clickable {
                                currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous month", tint = Color(0xFF7CB5EC))
                    }
                    Text(
                        text = monthLabel,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF7CB5EC),
                        fontWeight = FontWeight.Bold,
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF7CB5EC).copy(alpha = 0.15f))
                            .clickable {
                                currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.ChevronRight, contentDescription = "Next month", tint = Color(0xFF7CB5EC))
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF141210).copy(alpha = 0.80f))
                        .border(1.dp, Color(0xFF7CB5EC).copy(alpha = 0.20f), RoundedCornerShape(14.dp))
                        .padding(6.dp),
                ) {
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
                            val isDropTarget = dropTargetDate == dateMillis

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .onGloballyPositioned { coords ->
                                    val pos = coords.localToRoot(Offset.Zero)
                                    val s = coords.size
                                    dayCellRects[dateMillis] = Rect(pos.x, pos.y, pos.x + s.width, pos.y + s.height)
                                }
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when {
                                            isDropTarget -> Citrine.copy(alpha = 0.35f)
                                            isToday -> Color(0xFF7CB5EC).copy(alpha = 0.22f)
                                            dayTrips.isNotEmpty() -> Color(0xFF1A1812)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .then(
                                        if (isDropTarget) Modifier.border(2.dp, Citrine, RoundedCornerShape(6.dp))
                                        else Modifier
                                    )
                                    .clickable {
                                        selectedDate = if (selectedDate == dateMillis) null
                                        else if (dayTrips.isNotEmpty() || true) dateMillis
                                        else null
                                    }
                                    .padding(2.dp),
                            ) {
                                Column {
                                    Text(
                                        text = day.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isToday) Color(0xFF7CB5EC) else TextMid,
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

            // Drag-and-drop hint
            item {
                Text(
                    text = "Long-press a trip card and drag it onto a date to reschedule",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextLow.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                )
            }

            // Selected date or upcoming trips header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (selectedDate != null)
                            "Trips on ${SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(selectedDate!!))}"
                        else "Upcoming Trips",
                        style = MaterialTheme.typography.titleMedium,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    if (selectedDate != null) {
                        Text(
                            text = "Show all",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF7CB5EC),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedDate = null }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
            }

            if (displayTrips.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1A1812).copy(alpha = 0.70f))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "🕐",
                                fontSize = 32.sp,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = if (selectedDate != null) "No trips on this date"
                                else "No upcoming trips — tap \"Plan New Trip\" to start",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMid,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            } else {
                items(displayTrips, key = { it.id }) { trip ->
                    CalendarTripCard(
                        trip = trip,
                        navController = navController,
                        onOpen = { detailTrip = trip },
                        onEdit = { editingTrip = trip; showEditor = true },
                        onDelete = { pendingDeleteTrip = trip },
                        onArchive = { pendingArchiveTrip = trip },
                        onShare = {
                            scope.launch {
                                ShareCardImage.share(
                                    context = context,
                                    title = trip.name,
                                    subtitle = "Trip on " + SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(trip.date)) +
                                        "  \u2022  ${trip.stops.size} stop${if (trip.stops.size != 1) "s" else ""}",
                                    body = trip.stops.joinToString(" \u2192 ") { it.locationName } +
                                        if (trip.targetSpecimens.isNotEmpty()) "\nHunting: " + trip.targetSpecimens.joinToString(", ") else "",
                                    accentHex = 0xFFE8A33D,
                                    photoBitmap = null,
                                    caption = "Planned with RockScout",
                                    fileName = "rockscout_trip_${trip.id}",
                                )
                            }
                        },
                        onShareToProfile = { shareToProfileTrip = trip },
                        isDragging = draggingTrip?.id == trip.id,
                        onDragStart = onDragStart,
                        onDragMove = onDragMove,
                        onDragEnd = onDragEnd,
                        onDragCancel = onDragCancel,
                    )
                }
            }

            // Past trips section
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
                items(pastTrips.take(10), key = { it.id }) { trip ->
                    CalendarTripCard(
                        trip = trip,
                        navController = navController,
                        isPast = true,
                        onOpen = { detailTrip = trip },
                        onEdit = { editingTrip = trip; showEditor = true },
                        onDelete = { pendingDeleteTrip = trip },
                        onArchive = { pendingArchiveTrip = trip },
                        onShare = {
                            scope.launch {
                                ShareCardImage.share(
                                    context = context,
                                    title = trip.name,
                                    subtitle = "Trip on " + SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(trip.date)) +
                                        "  \u2022  ${trip.stops.size} stop${if (trip.stops.size != 1) "s" else ""}",
                                    body = trip.stops.joinToString(" \u2192 ") { it.locationName },
                                    accentHex = 0xFFE8A33D,
                                    photoBitmap = null,
                                    caption = "Planned with RockScout",
                                    fileName = "rockscout_trip_${trip.id}",
                                )
                            }
                        },
                        onShareToProfile = { shareToProfileTrip = trip },
                        isDragging = draggingTrip?.id == trip.id,
                        onDragStart = onDragStart,
                        onDragMove = onDragMove,
                        onDragEnd = onDragEnd,
                        onDragCancel = onDragCancel,
                    )
                }
            }

            // Trip Planner link
            item {
                val pillShape = RoundedCornerShape(50.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .sculpted(
                                shape = pillShape,
                                accent = Color(0xFF7CB5EC),
                                shadowElevation = 4.dp,
                                onClick = { navController.navigate(Routes.TRIP_PLANNER) },
                            )
                            .clip(pillShape)
                            .background(Slate800)
                            .glowingBorder(2.dp, Color(0xFF7CB5EC).copy(alpha = 0.5f), pillShape)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Map,
                                contentDescription = null,
                                tint = Color(0xFF7CB5EC),
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Open Trip Planner",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF7CB5EC),
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }

    // ─── Overlays (editor, detail, share, delete, archive) ───

    if (showEditor) {
        TripEditorDialog(
            initial = editingTrip,
            onDismiss = { showEditor = false },
            onSave = { saved ->
                repo.saveTrip(saved)
                showEditor = false
                // Navigate the calendar to the trip's month
                val tripCal = Calendar.getInstance().apply { timeInMillis = saved.date }
                currentMonth = tripCal
            },
            onOpenLocation = { locId -> navController.navigate(Routes.location(locId)) },
        )
    }

    detailTrip?.let { trip ->
        TripDetailSheet(
            trip = trip,
            onDismiss = { detailTrip = null },
            onEdit = {
                detailTrip = null
                editingTrip = trip
                showEditor = true
            },
            onOpenLocation = { locId ->
                detailTrip = null
                navController.navigate(Routes.location(locId))
            },
            onShare = {
                scope.launch {
                    ShareCardImage.share(
                        context = context,
                        title = trip.name,
                        subtitle = "Trip on " + SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(trip.date)) +
                            "  \u2022  ${trip.stops.size} stop${if (trip.stops.size != 1) "s" else ""}",
                        body = trip.stops.joinToString(" \u2192 ") { it.locationName } +
                            if (trip.targetSpecimens.isNotEmpty()) "\nHunting: " + trip.targetSpecimens.joinToString(", ") else "",
                        accentHex = 0xFFE8A33D,
                        photoBitmap = null,
                        caption = "Planned with RockScout",
                        fileName = "rockscout_trip_${trip.id}",
                    )
                }
            },
            onShareToProfile = { shareToProfileTrip = trip },
        )
    }

    shareToProfileTrip?.let { trip ->
        ShareToProfileComposer(
            sourceType = "trip",
            title = trip.name,
            tagline = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(trip.date)) +
                "  \u2022  ${trip.stops.size} stop${if (trip.stops.size != 1) "s" else ""}",
            imageUri = null,
            locationText = trip.stops.joinToString(" \u2192 ") { it.locationName }.ifBlank { "" },
            onDismiss = { shareToProfileTrip = null },
        )
    }

    pendingDeleteTrip?.let { trip ->
        DeleteConfirmDialog(
            title = "Delete trip?",
            message = "Delete \"${trip.name}\"? This action cannot be undone.",
            onConfirm = {
                scope.launch {
                    withContext(Dispatchers.IO) {
                        runCatching {
                            MapTileCacheManager.deleteTripAreaTiles(
                                context = context,
                                trip = trip,
                                radiusMiles = 3.0,
                            )
                        }
                        PersistenceManager.removeCachedTripId(trip.id)
                    }
                    repo.deleteTrip(trip.id)
                }
                pendingDeleteTrip = null
            },
            onDismiss = { pendingDeleteTrip = null },
        )
    }

    pendingArchiveTrip?.let { trip ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { pendingArchiveTrip = null },
            title = { Text("Trip completed?", style = MaterialTheme.typography.headlineSmall) },
            text = {
                Text(
                    "Archive \"${trip.name}\"? You can restore it anytime from the Archived tab in Trip Planner.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        repo.archiveTrip(trip.id)
                        pendingArchiveTrip = null
                    },
                ) { Text("Archive", color = Aqua, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { pendingArchiveTrip = null },
                ) { Text("Cancel", color = TextMid) }
            },
        )
    }
}

@Composable
private fun CalendarTripCard(
    trip: Trip,
    navController: NavController,
    isPast: Boolean = false,
    isDragging: Boolean = false,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onArchive: () -> Unit,
    onShare: () -> Unit,
    onShareToProfile: () -> Unit,
    onDragStart: (Trip) -> Unit = {},
    onDragMove: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {},
) {
    val dateText = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date(trip.date))
    val cardShape = RoundedCornerShape(16.dp)
    var cardCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var fingerStartGlobal by remember { mutableStateOf(Offset.Zero) }
    var dragAccumulator by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = cardShape, accent = Citrine, shadowElevation = 4.dp, onClick = onOpen, enabled = !isDragging)
            .onGloballyPositioned { cardCoords = it }
            .pointerInput(trip.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { startOffset ->
                        val global = cardCoords?.localToRoot(startOffset)
                        if (global != null) {
                            fingerStartGlobal = global
                            dragAccumulator = Offset.Zero
                            onDragStart(trip)
                        }
                    },
                    onDrag = { _, delta ->
                        dragAccumulator += delta
                        onDragMove(
                            Offset(
                                fingerStartGlobal.x + dragAccumulator.x,
                                fingerStartGlobal.y + dragAccumulator.y,
                            )
                        )
                    },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragCancel() },
                )
            }
            .clip(cardShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.60f),
                        Color.Black.copy(alpha = 0.72f),
                    )
                )
            )
            .glowingBorder(
                2.dp,
                if (isDragging) Citrine else Citrine.copy(alpha = 0.35f),
                cardShape,
            )
            .then(if (isDragging) Modifier.graphicsLayer { alpha = 0.35f } else Modifier),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isPast) TextLow else Citrine,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = trip.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isPast) TextLow else DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                    if (trip.stops.isNotEmpty()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "${trip.stops.size} stop${if (trip.stops.size == 1) "" else "s"} \u00b7 " +
                                trip.stops.take(3).joinToString(" \u2192 ") { it.locationName },
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextMid,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.DragHandle,
                        contentDescription = "Drag to reschedule",
                        tint = DarkTextMid.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp),
                    )
                    SculptedIconButton(icon = Icons.Filled.Edit, contentDescription = "Edit", onClick = onEdit, accent = Citrine, iconTint = DarkTextMid, size = 32.dp, shadowElevation = 2.dp)
                    SculptedIconButton(icon = Icons.Filled.Share, contentDescription = "Share", onClick = onShare, accent = Citrine, iconTint = DarkTextMid, size = 32.dp, shadowElevation = 2.dp)
                    SculptedIconButton(icon = Icons.Filled.PersonAdd, contentDescription = "Share to Profile", onClick = onShareToProfile, accent = Citrine, iconTint = Citrine, size = 32.dp, shadowElevation = 2.dp)
                    if (!isPast) {
                        SculptedIconButton(icon = Icons.Filled.CheckCircle, contentDescription = "Archive", onClick = onArchive, accent = Citrine, iconTint = Aqua, size = 32.dp, shadowElevation = 2.dp)
                    }
                    SculptedIconButton(icon = Icons.Filled.Delete, contentDescription = "Delete", onClick = onDelete, accent = Citrine, iconTint = DarkTextMid, size = 32.dp, shadowElevation = 2.dp)
                }
            }
        }
    }
}

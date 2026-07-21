package com.rork.rockscout.ui.screens

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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Undo
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.R
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.PersistenceManager
import com.rork.rockscout.data.Trip
import com.rork.rockscout.ui.components.DeleteConfirmDialog
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.TripRouteMap
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate800
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ArchivedTripsScreen(navController: NavController) {
    val repo = AppRepository.instance
    val archivedTrips by repo.archivedTrips.collectAsStateWithLifecycle()
    var detailTrip by remember { mutableStateOf<Trip?>(null) }
    var pendingDeleteTrip by remember { mutableStateOf<Trip?>(null) }

    ScreenScaffold(
        title = "Archived Trips",
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
        if (archivedTrips.isEmpty()) {
            EmptyState(
                emoji = "\uD83D\uDDC3\uFE0F",
                title = "No archived trips yet",
                message = "When you complete a trip, it'll show up here. Tap the checkmark on a trip card to archive it.",
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E1C16).copy(alpha = 0.82f))
                            .glowingBorder(1.dp, Color(0xFF1E1C16).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                    ) {
                        Column {
                            Text(
                                "Completed trips are stored here. Restore a trip to move it back to your active planner, or delete it permanently.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Aqua,
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "When you mark a trip as completed, the cached satellite tiles for that trip's route are automatically deleted to free up storage on your phone. If you restore a trip, you can re-cache the area before your next visit.",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextMid,
                            )
                        }
                    }
                }
                items(archivedTrips, key = { it.id }) { trip ->
                    ArchivedTripCard(
                        trip = trip,
                        navController = navController,
                        onOpen = { detailTrip = trip },
                        onRestore = { repo.restoreTrip(trip.id) },
                        onDelete = { pendingDeleteTrip = trip },
                    )
                }
            }
        }
    }

    detailTrip?.let { trip ->
        TripDetailSheet(
            trip = trip,
            onDismiss = { detailTrip = null },
            onEdit = {
                detailTrip = null
                navController.popBackStack()
                navController.navigate(Routes.TRIP_PLANNER)
            },
            onOpenLocation = { locId ->
                detailTrip = null
                navController.navigate(Routes.location(locId))
            },
            onShare = {},
            onShareToProfile = {},
        )
    }

    pendingDeleteTrip?.let { trip ->
        DeleteConfirmDialog(
            title = "Delete archived trip?",
            message = "Permanently delete \"${trip.name}\"? This action cannot be undone.",
            onConfirm = {
                PersistenceManager.removeCachedTripId(trip.id)
                repo.deleteArchivedTrip(trip.id)
                pendingDeleteTrip = null
            },
            onDismiss = { pendingDeleteTrip = null },
        )
    }
}

@Composable
private fun ArchivedTripCard(
    trip: Trip,
    navController: NavController,
    onOpen: () -> Unit,
    onRestore: () -> Unit,
    onDelete: () -> Unit,
) {
    val dateFormat = remember { SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()) }
    val completedFormat = remember { SimpleDateFormat("Completed MMM d, yyyy", Locale.getDefault()) }
    val cardShape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = cardShape, accent = Aqua, shadowElevation = 4.dp, onClick = onOpen)
            .clip(cardShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.62f),
                        Color.Black.copy(alpha = 0.72f),
                        Color.Black.copy(alpha = 0.82f),
                    )
                )
            )
            .glowingBorder(3.dp, Aqua.copy(alpha = 0.40f), cardShape),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(80.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Aqua.copy(alpha = 0.10f), Color.Transparent)
                    )
                )
        )
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Archive,
                            contentDescription = null,
                            tint = Aqua,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "ARCHIVED",
                            style = MaterialTheme.typography.labelSmall,
                            color = Aqua,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        trip.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        trip.completedAt?.let { completedFormat.format(Date(it)) }
                            ?: dateFormat.format(Date(trip.date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    SculptedIconButton(
                        icon = Icons.Filled.Undo,
                        contentDescription = "Restore trip",
                        onClick = onRestore,
                        accent = Aqua,
                        iconTint = Aqua,
                        size = 34.dp,
                        shadowElevation = 3.dp,
                    )
                    SculptedIconButton(
                        icon = Icons.Filled.Delete,
                        contentDescription = "Delete permanently",
                        onClick = onDelete,
                        accent = Aqua,
                        iconTint = DarkTextMid,
                        size = 34.dp,
                        shadowElevation = 3.dp,
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            trip.stops.take(3).forEachIndexed { idx, stop ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                    Box(
                        modifier = Modifier.size(20.dp).clip(RoundedCornerShape(5.dp))
                            .background(Aqua.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center,
                    ) { Text("${idx + 1}", style = MaterialTheme.typography.labelSmall, color = Aqua, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(8.dp))
                    Text(stop.locationName, style = MaterialTheme.typography.bodySmall, color = DarkTextHigh, maxLines = 1)
                }
            }
            if (trip.stops.size > 3) {
                Text(
                    "+${trip.stops.size - 3} more",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTextMid,
                    modifier = Modifier.padding(start = 28.dp, top = 1.dp),
                )
            }
            if (trip.targetSpecimens.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    trip.targetSpecimens.take(3).forEach { tag ->
                        TagChip(tag, color = Aqua, modifier = Modifier)
                    }
                }
            }
            if (trip.stops.size > 1) {
                Spacer(Modifier.height(8.dp))
                TripRouteMap(
                    trip = trip,
                    onStopTap = { stop -> navController.navigate(Routes.location(stop.locationId)) },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().height(135.dp),
                )
            }
        }
    }
}

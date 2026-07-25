package com.rork.rockscout.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import coil3.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.DigLocation
import com.rork.rockscout.data.PersistenceManager
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.data.OfflineMapExporter
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.Trip
import com.rork.rockscout.data.TripStop
import com.rork.rockscout.data.TripPdfExporter
import com.rork.rockscout.data.SpecimenMarker
import com.rork.rockscout.data.UserPinSubmissionStore
import com.rork.rockscout.data.ProfanityFilter
import com.rork.rockscout.data.WeatherRepository
import com.rork.rockscout.data.WeatherSnapshot
import com.rork.rockscout.ui.components.AnimatedAvatarIcon
import com.rork.rockscout.ui.components.FireworksOverlay
import com.rork.rockscout.ui.components.MapLayerStyle
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.createRockScoutMapView
import com.rork.rockscout.ui.components.MapZoomControls
import com.rork.rockscout.ui.components.MapExpandButton
import com.rork.rockscout.ui.components.FullscreenMapOverlay
import com.rork.rockscout.ui.components.MapViewLifecycleEffect
import com.rork.rockscout.ui.components.toggleSatelliteView
import com.rork.rockscout.ui.components.MapOfflineNotice
import com.rork.rockscout.ui.components.MapTileCacheManager
import com.rork.rockscout.ui.components.ShareCardImage
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.TripRouteMap
import com.rork.rockscout.ui.components.MultiPinDropMap
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.screens.AddLocationDialog
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.R
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextLow
import androidx.activity.compose.BackHandler
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.components.DeleteConfirmDialog
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.data.DigSiteSearchService
import com.rork.rockscout.data.DigSiteDiscoveryStore
import com.rork.rockscout.data.BlmData
import com.rork.rockscout.data.BlmTrailhead
import com.rork.rockscout.data.BlmCampground
import com.rork.rockscout.data.BlmDigSite
import com.rork.rockscout.data.StatePark
import com.rork.rockscout.data.StateParkData
import com.rork.rockscout.data.TripStopType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPlannerScreen(navController: NavController) {
    // ─── Premium gating: Trip Planner is a premium feature ───
    val accessManager = com.rork.rockscout.data.IdentifyAccessManager.instance
    val purchaseManager = com.rork.rockscout.data.PurchaseManager.instance
    val isPremium by purchaseManager.isPremium.collectAsStateWithLifecycle()
    val clubLocked = remember(isPremium) { accessManager.isFeatureLocked(isPremium) }
    if (clubLocked) {
        com.rork.rockscout.ui.components.ClubLockedState(
            emoji = "\uD83D\uDD12",
            title = "Unlock Trip Planner",
            message = "Your 1-week free trial has ended. Subscribe or donate to plan and save multi-stop rockhounding trips.",
            buttonLabel = "Subscribe or donate",
            onButton = { navController.navigate(Routes.PAYWALL) },
        )
        return
    }

    val repo = AppRepository.instance
    val trips by repo.trips.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var editingTrip by remember { mutableStateOf<Trip?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var detailTrip by remember { mutableStateOf<Trip?>(null) }
    var shareToProfileTrip by remember { mutableStateOf<Trip?>(null) }
    var pendingDeleteTrip by remember { mutableStateOf<Trip?>(null) }
    var pendingArchiveTrip by remember { mutableStateOf<Trip?>(null) }
    var showAddLocationDialog by remember { mutableStateOf(false) }
    var pendingLocationCoords by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    LaunchedEffect(Unit) {
        // Seed one demo trip the first time the user opens the planner so the
        // screen isn't empty on first run and the value of the feature is clear.
        if (trips.isEmpty()) {
            val demoStops = listOf(
                SeedData.allLocations.firstOrNull { it.id == "crater-of-diamonds" },
                SeedData.allLocations.firstOrNull { it.id == "wegner-quartz" },
            ).filterNotNull().mapIndexed { idx, loc ->
                TripStop(locationId = loc.id, locationName = loc.name, order = idx)
            }
            if (demoStops.isNotEmpty()) {
                val demo = Trip(
                    id = UUID.randomUUID().toString(),
                    name = "Arkansas Crystal Country Weekend",
                    date = System.currentTimeMillis() + 6L * 24 * 60 * 60 * 1000,
                    stops = demoStops,
                    targetSpecimens = listOf("Clear quartz points", "Herkimer-like double terminations", "Phantom quartz"),
                    gearChecklist = listOf("Rock hammer", "Bucket", "Sifter", "Sun hat", "Plenty of water", "Gloves"),
                    notes = "Arrive at Crater of Diamonds at opening. Bring sifter — start at the east drain. Wegner in the afternoon for the pay-dig crystal beds.",
                )
                repo.saveTrip(demo)
            }
        }
    }

    // Intercept system back presses while any overlay is open so the user
    // returns to the Trip Planner list instead of popping the entire screen.
    BackHandler(
        enabled = showEditor || detailTrip != null || shareToProfileTrip != null ||
            pendingDeleteTrip != null || pendingArchiveTrip != null || showAddLocationDialog,
    ) {
        when {
            showEditor -> { editingTrip = null; showEditor = false }
            detailTrip != null -> detailTrip = null
            shareToProfileTrip != null -> shareToProfileTrip = null
            pendingDeleteTrip != null -> pendingDeleteTrip = null
            pendingArchiveTrip != null -> pendingArchiveTrip = null
            showAddLocationDialog -> { showAddLocationDialog = false; pendingLocationCoords = null }
        }
    }

    ScreenScaffold(
        title = "Trip Planner",
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
                // Darker scrim so the busy photo is visible but every tile stays legible.
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
        Column(modifier = Modifier.fillMaxSize()) {
            val pillShape = RoundedCornerShape(50.dp)
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Archived Trips button — secondary outlined pill
                val archivedTrips by repo.archivedTrips.collectAsStateWithLifecycle()
                Box(
                    modifier = Modifier
                        .sculpted(
                            shape = pillShape,
                            accent = Aqua,
                            shadowElevation = 4.dp,
                            onClick = { navController.navigate(Routes.ARCHIVED_TRIPS) },
                        )
                        .clip(pillShape)
                        .background(Slate800)
                        .glowingBorder(2.dp, Aqua.copy(alpha = 0.6f), pillShape)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Archive,
                            contentDescription = null,
                            tint = Aqua,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            if (archivedTrips.isNotEmpty()) "Archived (${archivedTrips.size})" else "Archived",
                            style = MaterialTheme.typography.labelSmall,
                            color = Aqua,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                        )
                    }
                }
                // Calendar button — secondary outlined pill
                Box(
                    modifier = Modifier
                        .sculpted(
                            shape = pillShape,
                            accent = Color(0xFF7CB5EC),
                            shadowElevation = 4.dp,
                            onClick = { navController.navigate(Routes.TRIP_CALENDAR) },
                        )
                        .clip(pillShape)
                        .background(Slate800)
                        .glowingBorder(2.dp, Color(0xFF7CB5EC).copy(alpha = 0.5f), pillShape)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Map,
                            contentDescription = null,
                            tint = Color(0xFF7CB5EC),
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Calendar",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF7CB5EC),
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                        )
                    }
                }
                // Plan My Adventure button — primary pill
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
                            "Plan My Adventure",
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
            if (trips.isEmpty()) {
                EmptyState(
                    emoji = "\uD83D\uDDFA\uFE0F",
                    title = "No trips planned yet",
                    message = "Build a multi-stop rockhounding trip with weather, daylight windows, and a gear checklist. Tap the + above to start.",
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
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
                                "Build a multi-stop hunt and share it with your RockScout Friends. Tap to open, hold the pencil to edit.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Aqua,
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Tap the checkmark on a trip card when your hunt is done — it archives the trip and frees the cached satellite tiles so they don't clutter your phone. Archived trips are kept in the Archived tab so you can revisit or restore them anytime.",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextMid,
                            )
                        }
                    }
                }
                items(trips, key = { it.id }) { trip ->
                    TripCard(
                        trip = trip,
                        navController = navController,
                        onOpen = { detailTrip = trip },
                        onEdit = { editingTrip = trip; showEditor = true },
                        onDelete = { pendingDeleteTrip = trip },
                        onArchive = { pendingArchiveTrip = trip },
                        onShare = {
                            scope.launch {
                                runCatching {
                                    ShareCardImage.share(
                                        context = context,
                                        title = trip.name,
                                        subtitle = "Trip on " + SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(trip.date)) +
                                            "  •  ${trip.stops.size} stop${if (trip.stops.size != 1) "s" else ""}",
                                        body = trip.stops.joinToString(" → ") { it.locationName } +
                                            if (trip.targetSpecimens.isNotEmpty()) "\nHunting: " + trip.targetSpecimens.joinToString(", ") else "",
                                        accentHex = 0xFFE8A33D,
                                        photoBitmap = null,
                                        caption = "Planned with RockScout",
                                        fileName = "rockscout_trip_${trip.id}",
                                    )
                                }
                            }
                        },
                        onShareToProfile = { shareToProfileTrip = trip },
                        onAddStop = { name, lat, lng, locationId ->
                            val stop = TripStop(
                                locationId = locationId ?: "custom-pin-${UUID.randomUUID()}",
                                locationName = name,
                                order = trip.stops.size,
                                latitude = lat,
                                longitude = lng,
                                isCustomPin = locationId == null,
                            )
                            repo.addTripStop(trip.id, stop)
                            android.widget.Toast.makeText(
                                context,
                                "Added \"$name\" to the route",
                                android.widget.Toast.LENGTH_SHORT,
                            ).show()
                        },
                        onSubmitLocation = { lat, lng ->
                            pendingLocationCoords = Pair(lat, lng)
                            showAddLocationDialog = true
                        },
                    )
                }
            }
        }
    }

    if (showEditor) {
        TripEditorDialog(
            initial = editingTrip,
            onDismiss = { showEditor = false },
            onSave = { saved ->
                repo.saveTrip(saved)
                showEditor = false
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
                    runCatching {
                        ShareCardImage.share(
                            context = context,
                            title = trip.name,
                            subtitle = "Trip on " + SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(trip.date)) +
                                "  •  ${trip.stops.size} stop${if (trip.stops.size != 1) "s" else ""}",
                            body = trip.stops.joinToString(" → ") { it.locationName } +
                                if (trip.targetSpecimens.isNotEmpty()) "\nHunting: " + trip.targetSpecimens.joinToString(", ") else "",
                            accentHex = 0xFFE8A33D,
                            photoBitmap = null,
                            caption = "Planned with RockScout",
                            fileName = "rockscout_trip_${trip.id}",
                        )
                    }
                }
            },
            onShareToProfile = { shareToProfileTrip = trip },
            onAddStop = { name, lat, lng, locationId ->
                val stop = TripStop(
                    locationId = locationId ?: "custom-pin-${UUID.randomUUID()}",
                    locationName = name,
                    order = trip.stops.size,
                    latitude = lat,
                    longitude = lng,
                    isCustomPin = locationId == null,
                )
                repo.addTripStop(trip.id, stop)
                android.widget.Toast.makeText(
                    context,
                    "Added \"$name\" to the route",
                    android.widget.Toast.LENGTH_SHORT,
                ).show()
            },
            onSubmitLocation = { lat, lng ->
                detailTrip = null
                pendingLocationCoords = Pair(lat, lng)
                showAddLocationDialog = true
            },
        )
    }

    shareToProfileTrip?.let { trip ->
        ShareToProfileComposer(
            sourceType = "trip",
            title = trip.name,
            tagline = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(trip.date)) +
                "  •  ${trip.stops.size} stop${if (trip.stops.size != 1) "s" else ""}",
            imageUri = null,
            locationText = trip.stops.joinToString(" → ") { it.locationName }.ifBlank { "" },
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
                    "Mark \"${trip.name}\" as completed? Cached map tiles for this trip's route and specimen markers will be deleted to free up storage. The trip will be moved to your Archived Trips.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                SculptedButton(
                    text = "Complete & Archive",
                    onClick = {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                runCatching {
                                    MapTileCacheManager.deleteTripAreaTiles(
                                        context = context,
                                        trip = trip,
                                        radiusMiles = 3.0,
                                    )
                                }
                            }
                            repo.archiveTrip(trip.id)
                            android.widget.Toast.makeText(
                                context,
                                "Trip archived. Cached tiles freed.",
                                android.widget.Toast.LENGTH_LONG,
                            ).show()
                        }
                        pendingArchiveTrip = null
                    },
                    accent = Citrine,
                    containerColor = Citrine,
                    textColor = Color.Black,
                )
            },
            dismissButton = {
                SculptedTextButton(text = "Cancel", onClick = { pendingArchiveTrip = null }, accent = Citrine, textColor = Citrine)
            },
        )
    }

    // Location submission dialog — launched from the fullscreen route map's "Submit a New Location" button.
    if (showAddLocationDialog) {
        AddLocationDialog(
            onDismiss = { showAddLocationDialog = false; pendingLocationCoords = null },
            onSubmitted = { approved ->
                showAddLocationDialog = false
                pendingLocationCoords = null
                android.widget.Toast.makeText(
                    context,
                    if (approved) "Location submitted and auto-approved!" else "Location submitted for review.",
                    android.widget.Toast.LENGTH_LONG,
                ).show()
            },
            preFilledCoords = pendingLocationCoords,
        )
    }
}
}

@Composable
private fun TripCard(
    trip: Trip,
    navController: NavController,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onArchive: () -> Unit,
    onShare: () -> Unit,
    onShareToProfile: () -> Unit,
    onAddStop: ((name: String, lat: Double, lng: Double, locationId: String?) -> Unit)? = null,
    onSubmitLocation: ((lat: Double, lng: Double) -> Unit)? = null,
) {
    val dateFormat = remember { SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var cacheProgress by remember(trip.id) { mutableStateOf<Int?>(null) }
    // Initialize from persisted state so the button reflects the real on-disk
    // cache state (and the timestamp the indicator shows) across app restarts.
    var tripCacheTimestamp by remember(trip.id) {
        mutableStateOf(PersistenceManager.loadCachedTripTimestamps()[trip.id])
    }
    var areaCached by remember(trip.id) { mutableStateOf(tripCacheTimestamp != null) }
    val cardShape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = cardShape, accent = Citrine, shadowElevation = 6.dp, onClick = onOpen)
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
            .glowingBorder(3.dp, Citrine.copy(alpha = 0.50f), cardShape),
    ) {
        // Accent glow overlay at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(100.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Citrine.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "TRIP",
                        style = MaterialTheme.typography.labelMedium,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        trip.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        dateFormat.format(Date(trip.date)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    SculptedIconButton(icon = Icons.Filled.Edit, contentDescription = "Edit", onClick = onEdit, accent = Citrine, iconTint = DarkTextMid, size = 36.dp, shadowElevation = 3.dp)
                    SculptedIconButton(icon = Icons.Filled.Share, contentDescription = "Share", onClick = onShare, accent = Citrine, iconTint = DarkTextMid, size = 36.dp, shadowElevation = 3.dp)
                    SculptedIconButton(icon = Icons.Filled.PersonAdd, contentDescription = "Share to Profile", onClick = onShareToProfile, accent = Citrine, iconTint = Citrine, size = 36.dp, shadowElevation = 3.dp)
                    SculptedIconButton(icon = Icons.Filled.CheckCircle, contentDescription = "Trip completed", onClick = onArchive, accent = Citrine, iconTint = Aqua, size = 36.dp, shadowElevation = 3.dp)
                    SculptedIconButton(icon = Icons.Filled.Delete, contentDescription = "Delete", onClick = onDelete, accent = Citrine, iconTint = DarkTextMid, size = 36.dp, shadowElevation = 3.dp)
                }
            }
            Spacer(Modifier.height(10.dp))
            trip.stops.take(4).forEachIndexed { idx, stop ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
                    Box(
                        modifier = Modifier.size(22.dp).clip(RoundedCornerShape(6.dp))
                            .background(Citrine.copy(alpha = 0.20f))
                            .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center,
                    ) { Text("${idx + 1}", style = MaterialTheme.typography.labelSmall, color = Citrine, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(10.dp))
                    Text(stop.locationName, style = MaterialTheme.typography.bodyMedium, color = DarkTextHigh, maxLines = 1)
                }
            }
            if (trip.stops.size > 4) {
                Text(
                    "+${trip.stops.size - 4} more",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTextMid,
                    modifier = Modifier.padding(start = 32.dp, top = 2.dp),
                )
            }
            if (trip.targetSpecimens.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    trip.targetSpecimens.take(3).forEach { tag ->
                        TagChip(tag, color = Aqua, modifier = Modifier)
                    }
                }
            }
            if (trip.stops.size > 1) {
                Spacer(Modifier.height(10.dp))
                TripRouteMap(
                    trip = trip,
                    onStopTap = { stop -> navController.navigate(Routes.location(stop.locationId)) },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().height(195.dp),
                    onAddStop = onAddStop,
                    onSubmitLocation = onSubmitLocation,
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Citrine.copy(alpha = 0.12f))
                        .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .clickable(onClick = onOpen)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Map, contentDescription = null, tint = Citrine, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("View route map", style = MaterialTheme.typography.labelLarge, color = Citrine, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Citrine, modifier = Modifier.size(18.dp))
                }
            }
            // Cache Trip Area button — downloads satellite/street/label tiles for a 3-mile radius
            // around every stop so the user can navigate the full route with zero signal.
            Spacer(Modifier.height(10.dp))
            SculptedOutlinedButton(
                text = when {
                    areaCached -> "Trip Area Cached"
                    cacheProgress != null -> "Caching… (${cacheProgress}%)"
                    else -> "Cache Trip Area"
                },
                onClick = {
                    if (cacheProgress != null) return@SculptedOutlinedButton
                    cacheProgress = 0
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            runCatching {
                                MapTileCacheManager.prefetchTripArea(
                                    context = context,
                                    trip = trip,
                                    radiusMiles = 3.0,
                                    onProgress = { pct -> cacheProgress = pct },
                                )
                            }
                        }
                        cacheProgress = null
                        areaCached = true
                        tripCacheTimestamp = PersistenceManager.loadCachedTripTimestamps()[trip.id]
                        // Persist the cached-trip id so the button state survives
                        // an app restart (matches the TripDetailSheet behavior).
                        PersistenceManager.addCachedTripId(trip.id)
                        android.widget.Toast.makeText(
                            context,
                            "Trip area cached — satellite maps available offline for your route.",
                            android.widget.Toast.LENGTH_LONG,
                        ).show()
                    }
                },
                accent = if (areaCached) Aqua else Citrine,
                textColor = if (areaCached) Aqua else Citrine,
                icon = if (areaCached) Icons.Filled.CloudDone else Icons.Filled.Download,
                modifier = Modifier.fillMaxWidth(),
                enabled = cacheProgress == null,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TripEditorDialog(
    initial: Trip?,
    onDismiss: () -> Unit,
    onSave: (Trip) -> Unit,
    onOpenLocation: (String) -> Unit,
) {
    val isEdit = initial != null
    val context = LocalContext.current
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var dateMillis by remember { mutableStateOf(initial?.date ?: System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000) }
    val stops = remember { mutableStateListOf<TripStop>().apply { initial?.stops?.let { addAll(it) } } }
    val targetSpecimens = remember { mutableStateListOf<String>().apply { initial?.targetSpecimens?.let { addAll(it) } } }
    val gearChecklist = remember { mutableStateListOf<String>().apply { initial?.gearChecklist?.let { addAll(it) } } }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }
    // Specimen markers — available for both new and edit trips so the user can
    // drop pins while planning a brand-new adventure, not just when revising one.
    val specimenMarkers = remember { mutableStateListOf<SpecimenMarker>().apply { initial?.specimenMarkers?.let { addAll(it) } } }
    var pendingPinLat by remember { mutableStateOf<Double?>(null) }
    var pendingPinLng by remember { mutableStateOf<Double?>(null) }
    var newMarkerName by remember { mutableStateOf("") }
    var newMarkerDesc by remember { mutableStateOf("") }
    var newMarkerCategory by remember { mutableStateOf("Other") }
    var pendingRemoveMarker by remember { mutableStateOf<SpecimenMarker?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showLocationPicker by remember { mutableStateOf(false) }
    var showCustomPinPicker by remember { mutableStateOf(false) }
    var showTrailheadPicker by remember { mutableStateOf(false) }
    var showCampgroundPicker by remember { mutableStateOf(false) }
    var showBlmDigSitePicker by remember { mutableStateOf(false) }
    var showStateParkPicker by remember { mutableStateOf(false) }
    var showAddLocationDialog by remember { mutableStateOf(false) }
    var addLocationMode by remember { mutableStateOf("dig_site") }
    var addStopMenuExpanded by remember { mutableStateOf(false) }
    var newSpecimen by remember { mutableStateOf("") }
    var newGear by remember { mutableStateOf("") }
    var pendingRemoveStop by remember { mutableStateOf<Int?>(null) }
    var pendingRemoveTarget by remember { mutableStateOf<Int?>(null) }
    var pendingRemoveGear by remember { mutableStateOf<Int?>(null) }
    // Specimen marker filter (new + edit trips)
    var selectedFilter by remember { mutableStateOf("All") }
    // Auto-save draft state
    var draftSavedAt by remember { mutableStateOf<Long?>(null) }
    var showRestoreDraft by remember { mutableStateOf(false) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    val draftKey = initial?.id ?: "new"

    // Build a Trip snapshot for the route map. We now synthesize a transient
    // id for brand-new trips so the route map renders as soon as two stops are
    // added — no need to wait for a save anymore.
    val tripForMap = remember(initial, stops, specimenMarkers) {
        (initial ?: Trip(
            id = draftKey,
            name = name.ifBlank { "New Trip" },
            date = dateMillis,
            stops = emptyList(),
            targetSpecimens = emptyList(),
            gearChecklist = emptyList(),
            notes = "",
            createdAt = System.currentTimeMillis(),
            specimenMarkers = emptyList(),
        )).copy(stops = stops.toList(), specimenMarkers = specimenMarkers.toList())
    }

    // Filtered specimen markers for display on map and list
    val filteredMarkers = remember(specimenMarkers, selectedFilter) {
        if (selectedFilter == "All") specimenMarkers.toList()
        else specimenMarkers.filter { it.category == selectedFilter }
    }

    // Calculate total route distance
    val totalDistance = remember(stops) {
        val points = stops.mapNotNull { stop ->
            if (stop.isCustomPin && stop.latitude != null && stop.longitude != null) {
                Pair(stop.latitude, stop.longitude)
            } else {
                SeedData.locationById(stop.locationId)?.let { Pair(it.latitude, it.longitude) }
            }
        }
        var total = 0.0
        for (i in 1 until points.size) {
            total += AppRepository.distanceMiles(
                points[i - 1].first, points[i - 1].second,
                points[i].first, points[i].second,
            )
        }
        total
    }

    // Calculate per-segment travel durations (45 mph average for mixed driving)
    val segmentDurations = remember(stops) {
        val points = stops.mapNotNull { stop ->
            if (stop.isCustomPin && stop.latitude != null && stop.longitude != null) {
                Pair(stop.latitude, stop.longitude)
            } else {
                SeedData.locationById(stop.locationId)?.let { Pair(it.latitude, it.longitude) }
            }
        }
        val durations = mutableListOf<Int>() // minutes
        for (i in 1 until points.size) {
            val miles = AppRepository.distanceMiles(
                points[i - 1].first, points[i - 1].second,
                points[i].first, points[i].second,
            )
            durations.add((miles / 45.0 * 60).toInt().coerceAtLeast(1))
        }
        durations
    }

    val totalTravelMinutes = remember(segmentDurations) { segmentDurations.sum() }

    fun clearDraft() {
        runCatching {
            java.io.File(context.filesDir, "trip_draft_$draftKey.json").delete()
        }
        draftSavedAt = null
    }

    // ── Auto-save draft logic ──
    LaunchedEffect(name, dateMillis, stops.toList(), targetSpecimens.toList(), gearChecklist.toList(), notes, specimenMarkers.toList()) {
        val hasContent = name.isNotBlank() ||
            stops.isNotEmpty() ||
            targetSpecimens.isNotEmpty() ||
            gearChecklist.isNotEmpty() ||
            notes.isNotBlank() ||
            specimenMarkers.isNotEmpty()
        if (!hasContent) {
            // No meaningful content yet — delete any stale draft so an empty form
            // never prompts for restore on the next visit.
            clearDraft()
            return@LaunchedEffect
        }
        kotlinx.coroutines.delay(3000)
        runCatching {
            val draftJson = kotlinx.serialization.json.Json.encodeToString(
                Trip.serializer(),
                Trip(
                    id = draftKey,
                    name = name,
                    date = dateMillis,
                    stops = stops.toList(),
                    targetSpecimens = targetSpecimens.toList(),
                    gearChecklist = gearChecklist.toList(),
                    notes = notes,
                    specimenMarkers = specimenMarkers.toList(),
                ),
            )
            val draftFile = java.io.File(context.filesDir, "trip_draft_$draftKey.json")
            draftFile.writeText(draftJson)
            draftSavedAt = System.currentTimeMillis()
        }
    }

    // Check for existing draft on entry
    LaunchedEffect(draftKey) {
        kotlinx.coroutines.delay(100)
        val draftFile = java.io.File(context.filesDir, "trip_draft_$draftKey.json")
        if (draftFile.exists()) {
            val ageMs = System.currentTimeMillis() - draftFile.lastModified()
            if (ageMs < 7L * 24 * 60 * 60 * 1000) {
                showRestoreDraft = true
            } else {
                draftFile.delete()
            }
        }
    }

    ScreenScaffold(
        title = if (isEdit) "Edit Trip" else "New Trip",
        onBack = { clearDraft(); onDismiss() },
        actions = {
            // Auto-save indicator
            if (draftSavedAt != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(end = 4.dp),
                ) {
                    Icon(
                        Icons.Filled.CloudDone,
                        contentDescription = null,
                        tint = Citrine,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        "Saved",
                        style = MaterialTheme.typography.labelSmall,
                        color = Citrine,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            // Share Trip text summary button
            SculptedIconButton(
                icon = Icons.Filled.Share,
                contentDescription = "Share trip text summary",
                onClick = {
                        val finalName = name.ifBlank { "Untitled Trip" }
                        val routeText = stops.joinToString(" → ") { it.locationName }.ifBlank { "No stops yet" }
                        val targetText = if (targetSpecimens.isNotEmpty())
                            "\nHunting: ${targetSpecimens.joinToString(", ")}" else ""
                        val markerCount = "\nSpecimen markers: ${specimenMarkers.size}"
                        val summary = """${finalName}
${SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(dateMillis))}
${stops.size} stop${if (stops.size != 1) "s" else ""}: $routeText$targetText$markerCount

Planned with RockScout""".trimIndent()
                        val sendIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_SUBJECT, finalName)
                            putExtra(android.content.Intent.EXTRA_TEXT, summary)
                        }
                        SafeLinkOpener.openShareChooser(context, sendIntent, "Share Trip")
                    },
                    accent = Aqua,
                    iconTint = Aqua,
                    backgroundColor = Slate800,
                    size = 40.dp,
                    shadowElevation = 3.dp,
                )
        },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                // ── Route map (shown for both new and edit trips once there are 2+ stops) ──
                if (tripForMap.stops.size > 1) {
                    Text(
                        "Route Map",
                        style = MaterialTheme.typography.titleMedium,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                    // Distance label beneath header
                    if (totalDistance > 0) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Citrine.copy(alpha = 0.12f))
                                .glowingBorder(1.dp, Citrine.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                Icons.Filled.Straighten,
                                contentDescription = null,
                                tint = Citrine,
                                modifier = Modifier.size(14.dp),
                            )
                            Text(
                                "Estimated route distance: ${"%.1f".format(totalDistance)} miles · Est. ${if (totalTravelMinutes >= 60) "${totalTravelMinutes / 60}h ${totalTravelMinutes % 60}m" else "$totalTravelMinutes min"} travel",
                                style = MaterialTheme.typography.labelMedium,
                                color = Citrine,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    TripRouteMap(
                        trip = tripForMap,
                        onStopTap = { stop -> onOpenLocation(stop.locationId) },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                    )
                    Spacer(Modifier.height(20.dp))
                }

                // ── Form fields ──
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = ProfanityFilter.filter(it) },
                    label = { Text("Trip name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                )
                Spacer(Modifier.height(12.dp))
                SculptedOutlinedButton(
                    text = "Trip date: ${SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(Date(dateMillis))}",
                    onClick = { showDatePicker = true },
                    accent = Citrine,
                    textColor = Citrine,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                Text("Stops (long-press to drag & reorder)", style = MaterialTheme.typography.titleSmall, color = DarkTextHigh, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                stops.forEachIndexed { idx, stop ->
                    // Travel duration badge between stops
                    if (idx > 0 && idx - 1 < segmentDurations.size) {
                        val mins = segmentDurations[idx - 1]
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(modifier = Modifier.width(1.dp).height(8.dp).background(Citrine.copy(alpha = 0.3f)))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (mins >= 60) "${mins / 60}h ${mins % 60}m" else "$mins min",
                                style = MaterialTheme.typography.labelSmall,
                                color = Citrine,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Citrine.copy(alpha = 0.12f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Box(modifier = Modifier.width(1.dp).height(8.dp).background(Citrine.copy(alpha = 0.3f)))
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    ) {
                        Box(
                            modifier = Modifier.size(26.dp).clip(RoundedCornerShape(6.dp))
                                .background(Citrine.copy(alpha = 0.18f))
                                .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center,
                        ) { Text("${idx + 1}", style = MaterialTheme.typography.labelSmall, color = Citrine, fontWeight = FontWeight.Bold) }
                        Spacer(Modifier.width(10.dp))
                        Text(stop.locationName, style = MaterialTheme.typography.bodyMedium, color = DarkTextHigh, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                        // Move up/down buttons for reordering
                        if (idx > 0) {
                            SculptedIconButton(
                                icon = Icons.Filled.Undo,
                                contentDescription = "Move up",
                                onClick = {
                                    if (idx > 0) {
                                        val item = stops.removeAt(idx)
                                        stops.add(idx - 1, item)
                                    }
                                },
                                accent = Citrine,
                                iconTint = DarkTextMid,
                                size = 28.dp,
                                shadowElevation = 2.dp,
                            )
                            Spacer(Modifier.width(4.dp))
                        }
                        SculptedIconButton(icon = Icons.Filled.Delete, contentDescription = "Remove stop", onClick = { pendingRemoveStop = idx }, accent = Citrine, iconTint = TextLow, size = 32.dp, shadowElevation = 3.dp)
                    }
                }
                // ── Add a stop dropdown ──
                Box(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    SculptedOutlinedButton(
                        text = "+ Add a stop",
                        onClick = { addStopMenuExpanded = true },
                        accent = Citrine,
                        textColor = Citrine,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    DropdownMenu(
                        expanded = addStopMenuExpanded,
                        onDismissRequest = { addStopMenuExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.88f),
                    ) {
                        DropdownMenuItem(
                            text = { Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Explore, contentDescription = null, tint = Citrine, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("Dig Sites")
                            } },
                            onClick = { addStopMenuExpanded = false; showLocationPicker = true },
                        )
                        DropdownMenuItem(
                            text = { Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Hiking, contentDescription = null, tint = Success, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("Trailheads")
                            } },
                            onClick = { addStopMenuExpanded = false; showTrailheadPicker = true },
                        )
                        DropdownMenuItem(
                            text = { Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Terrain, contentDescription = null, tint = Citrine, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("Campgrounds")
                            } },
                            onClick = { addStopMenuExpanded = false; showCampgroundPicker = true },
                        )
                        DropdownMenuItem(
                            text = { Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Landscape, contentDescription = null, tint = Color(0xFFC97B4A), modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("BLM Dig Sites")
                            } },
                            onClick = { addStopMenuExpanded = false; showBlmDigSitePicker = true },
                        )
                        DropdownMenuItem(
                            text = { Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Park, contentDescription = null, tint = Success, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("State Parks")
                            } },
                            onClick = { addStopMenuExpanded = false; showStateParkPicker = true },
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                SculptedOutlinedButton(
                    text = "+ Drop a custom pin",
                    onClick = { showCustomPinPicker = true },
                    accent = Aqua,
                    textColor = Aqua,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(12.dp))
                Text("Target specimens", style = MaterialTheme.typography.titleSmall, color = DarkTextHigh, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                targetSpecimens.forEachIndexed { idx, tag ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Aqua.copy(alpha = 0.14f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        ) { Text(tag, style = MaterialTheme.typography.labelMedium, color = Aqua, fontWeight = FontWeight.SemiBold) }
                        Spacer(Modifier.width(8.dp))
                        SculptedIconButton(icon = Icons.Filled.Delete, contentDescription = "Remove", onClick = { pendingRemoveTarget = idx }, accent = Aqua, iconTint = TextLow, size = 32.dp, shadowElevation = 3.dp)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newSpecimen,
                        onValueChange = { newSpecimen = ProfanityFilter.filter(it) },
                        label = { Text("Add a target") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                        modifier = Modifier.weight(1f).noAutoFocus(),
                    )
                    Spacer(Modifier.width(8.dp))
                    SculptedIconButton(
                        icon = Icons.Filled.Add,
                        contentDescription = "Add",
                        onClick = {
                            if (newSpecimen.isNotBlank()) { targetSpecimens.add(newSpecimen.trim()); newSpecimen = "" }
                        },
                        accent = Citrine,
                        iconTint = Citrine,
                        size = 44.dp,
                    )
                }

                Spacer(Modifier.height(12.dp))
                Text("Gear checklist", style = MaterialTheme.typography.titleSmall, color = DarkTextHigh, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                gearChecklist.forEachIndexed { idx, item ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Slate800)
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        ) { Text(item, style = MaterialTheme.typography.labelMedium, color = DarkTextHigh) }
                        Spacer(Modifier.width(8.dp))
                        SculptedIconButton(icon = Icons.Filled.Delete, contentDescription = "Remove", onClick = { pendingRemoveGear = idx }, accent = Citrine, iconTint = TextLow, size = 32.dp, shadowElevation = 3.dp)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newGear,
                        onValueChange = { newGear = ProfanityFilter.filter(it) },
                        label = { Text("Add gear item") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done),
                        modifier = Modifier.weight(1f).noAutoFocus(),
                    )
                    Spacer(Modifier.width(8.dp))
                    SculptedIconButton(
                        icon = Icons.Filled.Add,
                        contentDescription = "Add",
                        onClick = {
                            if (newGear.isNotBlank()) { gearChecklist.add(newGear.trim()); newGear = "" }
                        },
                        accent = Citrine,
                        iconTint = Citrine,
                        size = 44.dp,
                    )
                }

                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = ProfanityFilter.filter(it) },
                    label = { Text("Trip notes") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 110.dp).noAutoFocus(),
                    minLines = 3,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Default),
                )

                // ── Specimen Markers section (new + edit trips, below route map) ──
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Aqua.copy(alpha = 0.10f))
                        .glowingBorder(1.dp, Aqua.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Aqua, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Specimen Markers",
                        style = MaterialTheme.typography.titleMedium,
                        color = Aqua,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        "${specimenMarkers.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Aqua,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(8.dp))

                // ── Filter chips row ──
                val filterCategories = listOf("All", "Crystal", "Mineral", "Fossil", "Gemstone", "Quartz", "Other")
                val filterColors = mapOf(
                    "All" to Citrine,
                    "Crystal" to Aqua,
                    "Mineral" to Citrine,
                    "Fossil" to Color(0xFFB8860B),
                    "Gemstone" to Amethyst,
                    "Quartz" to Color(0xFFE8D33D),
                    "Other" to Color(0xFF808890),
                )
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    filterCategories.forEach { category ->
                        val isSelected = selectedFilter == category
                        val chipColor = filterColors[category] ?: Citrine
                        Box(
                            modifier = Modifier
                                .height(36.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isSelected) chipColor.copy(alpha = 0.25f) else Slate800)
                                .glowingBorder(
                                    1.5.dp,
                                    if (isSelected) chipColor else chipColor.copy(alpha = 0.3f),
                                    RoundedCornerShape(18.dp),
                                )
                                .clickable { selectedFilter = category }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                category,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) chipColor else DarkTextMid,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                            )
                        }
                    }
                }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Tap the map to drop a pin, then tap Set Pin to save it.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                    )
                    Spacer(Modifier.height(8.dp))

                    // Pin-drop map for specimen markers (with layer toggle)
                    MultiPinDropMap(
                        pins = specimenMarkers,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        initialZoom = 10.0,
                        accent = Aqua,
                        pinLabel = "Specimen marker",
                        showLayerToggle = true,
                        onPinSet = { lat, lng ->
                            pendingPinLat = lat
                            pendingPinLng = lng
                        },
                        onPinRemoved = { id ->
                            specimenMarkers.removeAll { it.id == id }
                            if (pendingRemoveMarker?.id == id) pendingRemoveMarker = null
                        },
                    )

                    // Name + description inputs for the new marker
                    if (pendingPinLat != null && pendingPinLng != null) {
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = newMarkerName,
                            onValueChange = { newMarkerName = ProfanityFilter.filter(it) },
                            label = { Text("Marker name (e.g. Amethyst pocket)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next,
                            ),
                            modifier = Modifier.fillMaxWidth().noAutoFocus(),
                        )
                        Spacer(Modifier.height(8.dp))
                        // Category dropdown
                        Box(modifier = Modifier.fillMaxWidth()) {
                            SculptedOutlinedButton(
                                text = "Category: $newMarkerCategory",
                                onClick = { categoryMenuExpanded = true },
                                accent = Aqua,
                                textColor = Aqua,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            DropdownMenu(
                                expanded = categoryMenuExpanded,
                                onDismissRequest = { categoryMenuExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.88f),
                            ) {
                                listOf("Crystal", "Mineral", "Fossil", "Gemstone", "Quartz", "Other").forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            newMarkerCategory = cat
                                            categoryMenuExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newMarkerDesc,
                            onValueChange = { newMarkerDesc = ProfanityFilter.filter(it) },
                            label = { Text("Description (optional)") },
                            minLines = 2,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Default,
                            ),
                            modifier = Modifier.fillMaxWidth().noAutoFocus(),
                        )
                        Spacer(Modifier.height(10.dp))
                        SculptedButton(
                            text = "Add Specimen Marker",
                            onClick = {
                                val marker = SpecimenMarker(
                                    id = UUID.randomUUID().toString(),
                                    name = newMarkerName.ifBlank { "Specimen #${specimenMarkers.size + 1}" },
                                    latitude = pendingPinLat!!,
                                    longitude = pendingPinLng!!,
                                    description = newMarkerDesc,
                                    timestamp = System.currentTimeMillis(),
                                    category = newMarkerCategory,
                                )
                                specimenMarkers.add(marker)
                                newMarkerName = ""
                                newMarkerDesc = ""
                                newMarkerCategory = "Other"
                                pendingPinLat = null
                                pendingPinLng = null
                            },
                            accent = Aqua,
                            containerColor = Aqua,
                            textColor = Color.Black,
                            icon = Icons.Filled.Add,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    // Existing specimen markers list (filtered by selected category)
                    if (filteredMarkers.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        if (selectedFilter != "All") {
                            Text(
                                "Showing ${filteredMarkers.size} of ${specimenMarkers.size} ($selectedFilter)",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextMid,
                                modifier = Modifier.padding(bottom = 4.dp),
                            )
                        }
                        filteredMarkers.forEach { marker ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Slate800)
                                    .glowingBorder(1.dp, Aqua.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                            ) {
                                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Aqua, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        marker.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = DarkTextHigh,
                                        fontWeight = FontWeight.Medium,
                                    )
                                    // Category badge
                                    Text(
                                        marker.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Aqua,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    if (marker.description.isNotBlank()) {
                                        Text(
                                            marker.description,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DarkTextMid,
                                            maxLines = 2,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                        )
                                    }
                                    Text(
                                        "%.4f, %.4f".format(marker.latitude, marker.longitude),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextLow,
                                    )
                                }
                                SculptedIconButton(
                                    icon = Icons.Filled.Delete,
                                    contentDescription = "Remove marker",
                                    onClick = { pendingRemoveMarker = marker },
                                    accent = Citrine,
                                    iconTint = TextLow,
                                    size = 32.dp,
                                    shadowElevation = 3.dp,
                                )
                            }
                        }
                    } else if (specimenMarkers.isNotEmpty() && selectedFilter != "All") {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "No $selectedFilter markers. Tap \"All\" to see all ${specimenMarkers.size} markers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextMid,
                        )
                    }

                // Bottom padding for scroll
                Spacer(Modifier.height(40.dp))
            }
            // Bottom action bar — Cancel + Save/Create (matches JournalEditorScreen)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SculptedTextButton(
                    text = "Cancel",
                    onClick = { clearDraft(); onDismiss() },
                    accent = Citrine,
                    textColor = Citrine,
                    modifier = Modifier.weight(1f),
                )
                SculptedButton(
                    text = if (isEdit) "Save" else "Create",
                    onClick = {
                        val finalName = name.ifBlank { "Untitled Trip" }
                        val trip = Trip(
                            id = initial?.id ?: UUID.randomUUID().toString(),
                            name = finalName,
                            date = dateMillis,
                            stops = stops.mapIndexed { idx, s -> s.copy(order = idx) },
                            targetSpecimens = targetSpecimens.toList(),
                            gearChecklist = gearChecklist.toList(),
                            notes = notes,
                            createdAt = initial?.createdAt ?: System.currentTimeMillis(),
                            specimenMarkers = specimenMarkers.toList(),
                        )
                        clearDraft()
                        onSave(trip)
                    },
                    accent = Citrine,
                    containerColor = Citrine,
                    textColor = Color.Black,
                    modifier = Modifier.weight(1.5f),
                    enabled = name.isNotBlank(),
                )
            }
        }
    }

    // Restore unsaved draft prompt
    if (showRestoreDraft) {
        AlertDialog(
            onDismissRequest = { showRestoreDraft = false },
            containerColor = Color(0xFF1E1C16),
            title = { Text("Restore unsaved draft?", color = Citrine, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "A previous editing session was not saved. Restore your work, or discard the draft and start fresh?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SculptedButton(
                        text = "Restore",
                        onClick = {
                            runCatching {
                                val draftFile = java.io.File(context.filesDir, "trip_draft_$draftKey.json")
                                if (draftFile.exists()) {
                                    val restored = kotlinx.serialization.json.Json.decodeFromString(
                                        Trip.serializer(),
                                        draftFile.readText(),
                                    )
                                    name = restored.name
                                    dateMillis = restored.date
                                    stops.clear(); stops.addAll(restored.stops)
                                    targetSpecimens.clear(); targetSpecimens.addAll(restored.targetSpecimens)
                                    gearChecklist.clear(); gearChecklist.addAll(restored.gearChecklist)
                                    notes = restored.notes
                                    specimenMarkers.clear(); specimenMarkers.addAll(restored.specimenMarkers)
                                }
                            }
                            showRestoreDraft = false
                        },
                        accent = Citrine,
                        containerColor = Citrine,
                        textColor = Color.Black,
                        modifier = Modifier.weight(1f),
                    )
                    SculptedTextButton(
                        text = "Discard",
                        onClick = {
                            clearDraft()
                            // Reset the form to defaults so the old draft isn't re-saved
                            // and the "Saved" indicator does not appear.
                            name = ""
                            dateMillis = System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000
                            stops.clear()
                            targetSpecimens.clear()
                            gearChecklist.clear()
                            notes = ""
                            specimenMarkers.clear()
                            newMarkerName = ""
                            newMarkerDesc = ""
                            newMarkerCategory = "Other"
                            newSpecimen = ""
                            newGear = ""
                            showRestoreDraft = false
                        },
                        accent = DarkTextMid,
                        textColor = DarkTextMid,
                        modifier = Modifier.weight(1f),
                    )
                }
            },
        )
    }

    if (showDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let { dateMillis = it }
                        showDatePicker = false
                    },
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } },
        ) { DatePicker(state = state) }
    }

    if (showLocationPicker) {
        LocationPickerSheet(
            onDismiss = { showLocationPicker = false },
            onPick = { loc ->
                stops.add(TripStop(locationId = loc.id, locationName = loc.name, order = stops.size))
                showLocationPicker = false
            },
        )
    }

    if (showCustomPinPicker) {
        com.rork.rockscout.ui.components.AddLocationSheet(
            onDismiss = { showCustomPinPicker = false },
            onPick = { name, lat, lng, submitAsRockLocation ->
                val pinId = "custom-pin-${UUID.randomUUID()}"
                stops.add(TripStop(
                    locationId = pinId,
                    locationName = name,
                    order = stops.size,
                    latitude = lat,
                    longitude = lng,
                    isCustomPin = true,
                    stopType = TripStopType.CUSTOM_PIN.label.lowercase().replace(" ", "_"),
                ))
                showCustomPinPicker = false
            },
            defaultSubmitAsRockLocation = false,
        )
    }

    if (showTrailheadPicker) {
        TrailheadPickerSheet(
            onDismiss = { showTrailheadPicker = false },
            onPick = { trailhead ->
                stops.add(TripStop(
                    locationId = "trailhead-${trailhead.name.hashCode()}-${trailhead.state}",
                    locationName = trailhead.name,
                    order = stops.size,
                    latitude = trailhead.latitude,
                    longitude = trailhead.longitude,
                    stopType = "trailhead",
                ))
                showTrailheadPicker = false
            },
            onAddNew = {
                showTrailheadPicker = false
                addLocationMode = "trailhead"
                showAddLocationDialog = true
            },
        )
    }

    if (showCampgroundPicker) {
        CampgroundPickerSheet(
            onDismiss = { showCampgroundPicker = false },
            onPick = { campground ->
                stops.add(TripStop(
                    locationId = "campground-${campground.name.hashCode()}-${campground.state}",
                    locationName = campground.name,
                    order = stops.size,
                    latitude = campground.latitude,
                    longitude = campground.longitude,
                    stopType = "campground",
                ))
                showCampgroundPicker = false
            },
            onAddNew = {
                showCampgroundPicker = false
                addLocationMode = "campground"
                showAddLocationDialog = true
            },
        )
    }

    if (showBlmDigSitePicker) {
        BlmDigSitePickerSheet(
            onDismiss = { showBlmDigSitePicker = false },
            onPick = { site ->
                stops.add(TripStop(
                    locationId = "blm-dig-${site.name.hashCode()}-${site.region.hashCode()}",
                    locationName = site.name,
                    order = stops.size,
                    latitude = site.latitude,
                    longitude = site.longitude,
                    stopType = "blm_dig_site",
                ))
                showBlmDigSitePicker = false
            },
        )
    }

    if (showStateParkPicker) {
        StateParkPickerSheet(
            onDismiss = { showStateParkPicker = false },
            onPick = { park ->
                stops.add(TripStop(
                    locationId = "state-park-${park.id}",
                    locationName = park.name,
                    order = stops.size,
                    latitude = park.latitude,
                    longitude = park.longitude,
                    stopType = "state_park",
                ))
                showStateParkPicker = false
            },
        )
    }

    if (showAddLocationDialog) {
        AddLocationDialog(
            onDismiss = { showAddLocationDialog = false },
            onSubmitted = { approved ->
                showAddLocationDialog = false
                android.widget.Toast.makeText(
                    context,
                    if (approved) "Location submitted and auto-approved!" else "Location submitted for review.",
                    android.widget.Toast.LENGTH_LONG,
                ).show()
            },
            submissionMode = addLocationMode,
        )
    }

    pendingRemoveStop?.let { idx ->
        DeleteConfirmDialog(
            title = "Remove stop?",
            message = "Remove \"${stops.getOrNull(idx)?.locationName ?: "this stop"}\" from the trip?",
            onConfirm = { if (idx < stops.size) stops.removeAt(idx); pendingRemoveStop = null },
            onDismiss = { pendingRemoveStop = null },
        )
    }
    pendingRemoveTarget?.let { idx ->
        DeleteConfirmDialog(
            title = "Remove target specimen?",
            message = "Remove \"${targetSpecimens.getOrNull(idx) ?: "this target"}\" from the trip?",
            onConfirm = { if (idx < targetSpecimens.size) targetSpecimens.removeAt(idx); pendingRemoveTarget = null },
            onDismiss = { pendingRemoveTarget = null },
        )
    }
    pendingRemoveGear?.let { idx ->
        DeleteConfirmDialog(
            title = "Remove gear item?",
            message = "Remove \"${gearChecklist.getOrNull(idx) ?: "this item"}\" from the checklist?",
            onConfirm = { if (idx < gearChecklist.size) gearChecklist.removeAt(idx); pendingRemoveGear = null },
            onDismiss = { pendingRemoveGear = null },
        )
    }
    pendingRemoveMarker?.let { marker ->
        DeleteConfirmDialog(
            title = "Remove specimen marker?",
            message = "Remove \"${marker.name}\" from this trip? This cannot be undone.",
            onConfirm = { specimenMarkers.removeAll { it.id == marker.id }; pendingRemoveMarker = null },
            onDismiss = { pendingRemoveMarker = null },
        )
    }
}

private enum class LocationFilterMode { Name, State, Country }

/** Parse "City, State/Province, Country" into the state/province token. */
private val DigLocation.stateOrProvince: String?
    get() {
        val parts = region.split(",").map { it.trim() }.filter { it.isNotBlank() }
        return if (parts.size >= 3) parts[parts.size - 2] else null
    }

/** Parse "City, State/Province, Country" into the country token. */
private val DigLocation.country: String
    get() = region.split(",").map { it.trim() }.filter { it.isNotBlank() }.lastOrNull() ?: region

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationPickerSheet(
    onDismiss: () -> Unit,
    onPick: (DigLocation) -> Unit,
) {
    val allDigSites = remember {
        SeedData.allLocations.filter {
            it.type != com.rork.rockscout.data.LocationType.ROCK_SHOP &&
                it.type != com.rork.rockscout.data.LocationType.METAPHYSICAL
        }
    }
    val states = remember(allDigSites) {
        allDigSites.mapNotNull { it.stateOrProvince }.distinct().sortedBy { it }
    }
    val countries = remember(allDigSites) {
        allDigSites.map { it.country }.distinct().sortedBy { it }
    }

    var mode by remember { mutableStateOf(LocationFilterMode.Name) }
    var query by remember { mutableStateOf("") }
    var selectedState by remember { mutableStateOf<String?>(null) }
    var selectedCountry by remember { mutableStateOf<String?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val currentLocation by AppRepository.instance.currentLocation.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var isSearchingWeb by remember { mutableStateOf(false) }
    var webSearchDone by remember { mutableStateOf(false) }
    var webSearchArea by remember { mutableStateOf("") }

    val filteredLocations = remember(mode, query, selectedState, selectedCountry, currentLocation) {
        when (mode) {
            LocationFilterMode.Name -> {
                if (query.isBlank()) allDigSites
                else allDigSites.filter {
                    it.name.contains(query, ignoreCase = true) ||
                        it.region.contains(query, ignoreCase = true)
                }
            }
            LocationFilterMode.State -> {
                selectedState?.let { state ->
                    allDigSites.filter { it.stateOrProvince?.equals(state, ignoreCase = true) == true }
                } ?: allDigSites
            }
            LocationFilterMode.Country -> {
                selectedCountry?.let { country ->
                    allDigSites.filter {
                        it.country.equals(country, ignoreCase = true) &&
                            AppRepository.distanceMiles(
                                currentLocation.first,
                                currentLocation.second,
                                it.latitude,
                                it.longitude,
                            ) <= 200.0
                    }
                } ?: allDigSites
            }
        }
    }

    // Trigger a background web search when fewer than 3 built-in dig sites
    // match the user's location filter. Results are saved to the Dev Console.
    LaunchedEffect(filteredLocations.size, mode, selectedState, selectedCountry) {
        val searchArea = when (mode) {
            LocationFilterMode.State -> selectedState
            LocationFilterMode.Country -> selectedCountry
            else -> null
        }
        if (searchArea != null && filteredLocations.size < 3 && !isSearchingWeb && !webSearchDone) {
            isSearchingWeb = true
            webSearchArea = searchArea
            scope.launch {
                val results = DigSiteSearchService.searchRockLocations(searchArea)
                if (results.isNotEmpty()) {
                    DigSiteDiscoveryStore.addAll(results)
                }
                isSearchingWeb = false
                webSearchDone = true
            }
        }
        if (searchArea == null) {
            webSearchDone = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Add a stop", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(460.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    LocationFilterMode.entries.forEach { entry ->
                        val label = when (entry) {
                            LocationFilterMode.Name -> "Search"
                            LocationFilterMode.State -> "State"
                            LocationFilterMode.Country -> "Country"
                        }
                        val selected = mode == entry
                        SculptedOutlinedButton(
                            text = label,
                            onClick = {
                                mode = entry
                                query = ""
                                selectedState = null
                                selectedCountry = null
                            },
                            accent = Citrine,
                            textColor = if (selected) Citrine else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                when (mode) {
                    LocationFilterMode.Name -> {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            label = { Text("Search by name or location") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().noAutoFocus(),
                        )
                    }
                    LocationFilterMode.State -> {
                        LocationDropdown(
                            label = "State / Province",
                            options = states,
                            selected = selectedState,
                            onSelect = { selectedState = it },
                            expanded = dropdownExpanded,
                            onExpandedChange = { dropdownExpanded = it },
                        )
                    }
                    LocationFilterMode.Country -> {
                        LocationDropdown(
                            label = "Country",
                            options = countries,
                            selected = selectedCountry,
                            onSelect = { selectedCountry = it },
                            expanded = dropdownExpanded,
                            onExpandedChange = { dropdownExpanded = it },
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "${filteredLocations.size} dig site${if (filteredLocations.size == 1) "" else "s"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextLow,
                        modifier = Modifier.weight(1f),
                    )
                    if (isSearchingWeb) {
                        Text(
                            "Searching for new locations…",
                            style = MaterialTheme.typography.labelSmall,
                            color = Aqua,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                if (isSearchingWeb) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Looking for rock shops & dig sites near \"$webSearchArea\" to add to our database.",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextLow,
                    )
                } else if (webSearchDone && filteredLocations.size < 3) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "We searched the web for additional rock hunting locations in \"$webSearchArea\" — results saved to Developer Console for review.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Aqua.copy(alpha = 0.8f),
                    )
                }
                Spacer(Modifier.height(6.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(filteredLocations.take(80), key = { it.id }) { loc ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .sculpted(shape = RoundedCornerShape(10.dp), accent = Citrine, shadowElevation = 3.dp, onClick = { onPick(loc) })
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .glowingBorder(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(loc.type.emoji, style = MaterialTheme.typography.titleMedium)
                                    Spacer(Modifier.width(8.dp))
                                    Text(loc.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                }
                                Spacer(Modifier.height(2.dp))
                                Text(loc.region, style = MaterialTheme.typography.labelSmall, color = TextLow)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { SculptedTextButton(text = "Cancel", onClick = onDismiss, accent = Citrine, textColor = Citrine) },
    )
}

@Composable
private fun LocationDropdown(
    label: String,
    options: List<String>,
    selected: String?,
    onSelect: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        SculptedOutlinedButton(
            text = selected ?: "Choose $label",
            onClick = { onExpandedChange(true) },
            accent = Citrine,
            textColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.fillMaxWidth(0.88f),
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        onExpandedChange(false)
                    },
                )
            }
        }
    }
}

/** Picker sheet for BLM trailheads — searchable, state-grouped list with same
 *  card layout as the BLM Guide's Trailheads tab. Tapping a trailhead adds it
 *  as a trip stop. An "Add a trailhead" button at the bottom opens the
 *  submission form in trailhead mode. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrailheadPickerSheet(
    onDismiss: () -> Unit,
    onPick: (BlmTrailhead) -> Unit,
    onAddNew: () -> Unit,
) {
    val allTrailheads = remember { BlmData.trailheads }
    var query by remember { mutableStateOf("") }

    val filtered = remember(query) {
        if (query.isBlank()) allTrailheads
        else allTrailheads.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.region.contains(query, ignoreCase = true) ||
                it.state.contains(query, ignoreCase = true)
        }
    }
    val grouped = remember(filtered) {
        filtered.groupBy { it.state }.toList().sortedBy { it.first }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Add a trailhead", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(460.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search by name or region") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "${filtered.size} trailhead${if (filtered.size == 1) "" else "s"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextLow,
                )
                Spacer(Modifier.height(6.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    grouped.forEach { (state, trailheads) ->
                        item {
                            Text(
                                state,
                                style = MaterialTheme.typography.labelMedium,
                                color = Citrine,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 4.dp),
                            )
                        }
                        items(trailheads, key = { "${it.name}-${it.state}" }) { trailhead ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .sculpted(shape = RoundedCornerShape(10.dp), accent = Success, shadowElevation = 3.dp, onClick = { onPick(trailhead) })
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .glowingBorder(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Hiking, contentDescription = null, tint = Success, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(trailhead.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                    }
                                    Spacer(Modifier.height(2.dp))
                                    Text(trailhead.region, style = MaterialTheme.typography.labelSmall, color = TextLow)
                                    if (trailhead.description.isNotBlank()) {
                                        Spacer(Modifier.height(4.dp))
                                        Text(trailhead.description, style = MaterialTheme.typography.labelSmall, color = DarkTextMid, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                SculptedOutlinedButton(
                    text = "+ Add a new trailhead",
                    onClick = onAddNew,
                    accent = Success,
                    textColor = Success,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {},
        dismissButton = { SculptedTextButton(text = "Cancel", onClick = onDismiss, accent = Citrine, textColor = Citrine) },
    )
}

/** Picker sheet for BLM campgrounds — searchable, state-grouped list with same
 *  card layout as the BLM Guide's Campgrounds tab. Tapping a campground adds it
 *  as a trip stop. An "Add a campground" button at the bottom opens the
 *  submission form in campground mode. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampgroundPickerSheet(
    onDismiss: () -> Unit,
    onPick: (BlmCampground) -> Unit,
    onAddNew: () -> Unit,
) {
    val allCampgrounds = remember { BlmData.campgrounds }
    var query by remember { mutableStateOf("") }

    val filtered = remember(query) {
        if (query.isBlank()) allCampgrounds
        else allCampgrounds.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.region.contains(query, ignoreCase = true) ||
                it.state.contains(query, ignoreCase = true)
        }
    }
    val grouped = remember(filtered) {
        filtered.groupBy { it.state }.toList().sortedBy { it.first }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Add a campground", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(460.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search by name or region") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "${filtered.size} campground${if (filtered.size == 1) "" else "s"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextLow,
                )
                Spacer(Modifier.height(6.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    grouped.forEach { (state, campgrounds) ->
                        item {
                            Text(
                                state,
                                style = MaterialTheme.typography.labelMedium,
                                color = Citrine,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 4.dp),
                            )
                        }
                        items(campgrounds, key = { "${it.name}-${it.state}" }) { campground ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .sculpted(shape = RoundedCornerShape(10.dp), accent = Citrine, shadowElevation = 3.dp, onClick = { onPick(campground) })
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .glowingBorder(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Terrain, contentDescription = null, tint = Citrine, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(campground.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                    }
                                    Spacer(Modifier.height(2.dp))
                                    Text(campground.region, style = MaterialTheme.typography.labelSmall, color = TextLow)
                                    if (campground.feeInfo.isNotBlank()) {
                                        Spacer(Modifier.height(2.dp))
                                        Text(campground.feeInfo, style = MaterialTheme.typography.labelSmall, color = Aqua, fontWeight = FontWeight.Medium)
                                    }
                                    if (campground.description.isNotBlank()) {
                                        Spacer(Modifier.height(4.dp))
                                        Text(campground.description, style = MaterialTheme.typography.labelSmall, color = DarkTextMid, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                SculptedOutlinedButton(
                    text = "+ Add a new campground",
                    onClick = onAddNew,
                    accent = Citrine,
                    textColor = Citrine,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {},
        dismissButton = { SculptedTextButton(text = "Cancel", onClick = onDismiss, accent = Citrine, textColor = Citrine) },
    )
}

/** Picker sheet for BLM dig sites — searchable, state-grouped list. Tapping a
 *  site adds it as a trip stop with coordinates and the "blm_dig_site" stopType. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BlmDigSitePickerSheet(
    onDismiss: () -> Unit,
    onPick: (BlmDigSite) -> Unit,
) {
    val allSites = remember { BlmData.allBlmDigSites }
    var query by remember { mutableStateOf("") }

    val filtered = remember(query) {
        if (query.isBlank()) allSites
        else allSites.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.region.contains(query, ignoreCase = true) ||
                it.whatToFind.contains(query, ignoreCase = true)
        }
    }
    val grouped = remember(filtered) {
        filtered.groupBy { site ->
            BlmData.allStates.firstOrNull { it.code == site.region.substringAfterLast(", ").take(2).uppercase() }?.name
                ?: "BLM"
        }.toList().sortedBy { it.first }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Add a BLM dig site", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(460.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search by name, region, or mineral") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "${filtered.size} BLM dig site${if (filtered.size == 1) "" else "s"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextLow,
                )
                Spacer(Modifier.height(6.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    grouped.forEach { (regionLabel, sites) ->
                        item {
                            Text(
                                regionLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFC97B4A),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 4.dp),
                            )
                        }
                        items(sites, key = { "${it.name}-${it.region}" }) { site ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .sculpted(shape = RoundedCornerShape(10.dp), accent = Color(0xFFC97B4A), shadowElevation = 3.dp, onClick = { onPick(site) })
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .glowingBorder(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Landscape, contentDescription = null, tint = Color(0xFFC97B4A), modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(site.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                    }
                                    Spacer(Modifier.height(2.dp))
                                    Text(site.region, style = MaterialTheme.typography.labelSmall, color = TextLow)
                                    if (site.whatToFind.isNotBlank()) {
                                        Spacer(Modifier.height(4.dp))
                                        Text(site.whatToFind, style = MaterialTheme.typography.labelSmall, color = DarkTextMid, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { SculptedTextButton(text = "Cancel", onClick = onDismiss, accent = Citrine, textColor = Citrine) },
    )
}

/** Picker sheet for State Parks — searchable, state-grouped list. Tapping a
 *  park adds it as a trip stop with coordinates and the "state_park" stopType. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StateParkPickerSheet(
    onDismiss: () -> Unit,
    onPick: (StatePark) -> Unit,
) {
    val allParks = remember { StateParkData.allParks }
    var query by remember { mutableStateOf("") }

    val filtered = remember(query) {
        if (query.isBlank()) allParks
        else allParks.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.region.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
        }
    }
    val grouped = remember(filtered) {
        filtered.groupBy { it.state }.toList().sortedBy { it.first }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Add a state park", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(460.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search by name or region") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "${filtered.size} state park${if (filtered.size == 1) "" else "s"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextLow,
                )
                Spacer(Modifier.height(6.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    grouped.forEach { (stateCode, parks) ->
                        item {
                            Text(
                                stateCode,
                                style = MaterialTheme.typography.labelMedium,
                                color = Success,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 4.dp),
                            )
                        }
                        items(parks, key = { it.id }) { park ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .sculpted(shape = RoundedCornerShape(10.dp), accent = Success, shadowElevation = 3.dp, onClick = { onPick(park) })
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .glowingBorder(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Park, contentDescription = null, tint = Success, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(park.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                    }
                                    Spacer(Modifier.height(2.dp))
                                    Text(park.region, style = MaterialTheme.typography.labelSmall, color = TextLow)
                                    if (park.description.isNotBlank()) {
                                        Spacer(Modifier.height(4.dp))
                                        Text(park.description, style = MaterialTheme.typography.labelSmall, color = DarkTextMid, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { SculptedTextButton(text = "Cancel", onClick = onDismiss, accent = Citrine, textColor = Citrine) },
    )
}

@Composable
internal fun TripDetailSheet(
    trip: Trip,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onOpenLocation: (String) -> Unit,
    onShare: () -> Unit,
    onShareToProfile: () -> Unit = {},
    onAddStop: ((name: String, lat: Double, lng: Double, locationId: String?) -> Unit)? = null,
    onSubmitLocation: ((lat: Double, lng: Double) -> Unit)? = null,
) {
    val dateFormat = remember { SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault()) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isDownloadingMaps by remember { mutableStateOf(false) }
    var mapsDownloaded by remember { mutableStateOf(PersistenceManager.loadCachedTripIds().contains(trip.id)) }
    var downloadProgress by remember { mutableStateOf(0) }
    var downloadStopIndex by remember { mutableStateOf(0) }
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
        title = {
            Column {
                Text("TRIP", style = MaterialTheme.typography.labelMedium, color = Citrine, fontWeight = FontWeight.Bold)
                Text(trip.name, style = MaterialTheme.typography.headlineSmall)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                Text(dateFormat.format(Date(trip.date)), style = MaterialTheme.typography.bodyMedium, color = TextLow)
                Spacer(Modifier.height(12.dp))
                Text("Route", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                trip.stops.forEachIndexed { idx, stop ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onOpenLocation(stop.locationId) }
                        .padding(vertical = 6.dp)) {
                        Box(
                            modifier = Modifier.size(26.dp).clip(RoundedCornerShape(6.dp)).background(Citrine.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center,
                        ) { Text("${idx + 1}", style = MaterialTheme.typography.labelSmall, color = Citrine, fontWeight = FontWeight.Bold) }
                        Spacer(Modifier.width(10.dp))
                        Text(stop.locationName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    }
                }
                if (trip.stops.size > 1) {
                    Spacer(Modifier.height(12.dp))
                    // Distance label beneath route map header
                    val detailDistance = remember(trip.stops) {
                        val pts = trip.stops.mapNotNull { stop ->
                            if (stop.isCustomPin && stop.latitude != null && stop.longitude != null) {
                                Pair(stop.latitude, stop.longitude)
                            } else {
                                SeedData.locationById(stop.locationId)?.let { Pair(it.latitude, it.longitude) }
                            }
                        }
                        var total = 0.0
                        for (i in 1 until pts.size) {
                            total += AppRepository.distanceMiles(pts[i - 1].first, pts[i - 1].second, pts[i].first, pts[i].second)
                        }
                        total
                    }
                    if (detailDistance > 0) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Citrine.copy(alpha = 0.12f))
                                .glowingBorder(1.dp, Citrine.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(Icons.Filled.Straighten, contentDescription = null, tint = Citrine, modifier = Modifier.size(14.dp))
                            val totalTravelMins = remember(detailDistance) { (detailDistance / 45.0 * 60).toInt().coerceAtLeast(1) }
                            Text(
                                "Estimated route distance: ${"%.1f".format(detailDistance)} miles · Est. ${if (totalTravelMins >= 60) "${totalTravelMins / 60}h ${totalTravelMins % 60}m" else "$totalTravelMins min"} travel",
                                style = MaterialTheme.typography.labelMedium,
                                color = Citrine,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    TripRouteMap(
                        trip = trip,
                        onStopTap = { stop -> onOpenLocation(stop.locationId) },
                        modifier = Modifier.fillMaxWidth().height(215.dp),
                        onAddStop = onAddStop,
                        onSubmitLocation = onSubmitLocation,
                    )
                    Spacer(Modifier.height(8.dp))
                    SculptedOutlinedButton(
                        text = when {
                            isDownloadingMaps -> "Downloading stop ${downloadStopIndex} of ${trip.stops.size}… (${downloadProgress}%)"
                            mapsDownloaded -> "All stop maps downloaded"
                            else -> "Download Maps for All Stops"
                        },
                        onClick = {
                            if (isDownloadingMaps) return@SculptedOutlinedButton
                            isDownloadingMaps = true
                            downloadProgress = 0
                            downloadStopIndex = 0
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    runCatching {
                                        com.rork.rockscout.ui.components.MapTileCacheManager.prefetchTripArea(
                                            context = context,
                                            trip = trip,
                                            radiusMiles = 3.0,
                                            onProgress = { pct ->
                                                downloadProgress = pct
                                                downloadStopIndex = ((pct / 100.0) * trip.stops.size).toInt().coerceAtLeast(1)
                                            },
                                        )
                                    }
                                }
                                isDownloadingMaps = false
                                mapsDownloaded = true
                                PersistenceManager.addCachedTripId(trip.id)
                                android.widget.Toast.makeText(
                                    context,
                                    "All stop maps downloaded — you can navigate this trip and specimen markers offline.",
                                    android.widget.Toast.LENGTH_LONG,
                                ).show()
                            }
                        },
                        accent = if (mapsDownloaded) Aqua else Citrine,
                        textColor = if (mapsDownloaded) Aqua else Citrine,
                        icon = if (mapsDownloaded) Icons.Filled.CloudDone else Icons.Filled.Download,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isDownloadingMaps,
                    )
                    if (isDownloadingMaps) {
                        Spacer(Modifier.height(6.dp))
                        androidx.compose.material3.LinearProgressIndicator(
                            progress = { downloadProgress / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = Citrine,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Caches satellite, street, and label tiles at zoom 10–19 for a 3-mile radius around every stop, plus a 1-mile radius around every pinned specimen marker, so the full route and find spots work with zero signal.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                    )
                    Spacer(Modifier.height(10.dp))
                    var isGeneratingMapImage by remember { mutableStateOf(false) }
                    SculptedOutlinedButton(
                        text = if (isGeneratingMapImage) "Generating map image…" else "Save ultra high-res map image",
                        onClick = {
                            if (isGeneratingMapImage) return@SculptedOutlinedButton
                            isGeneratingMapImage = true
                            scope.launch {
                                val firstStop = trip.stops.firstOrNull()
                                val lat = if (firstStop?.isCustomPin == true && firstStop.latitude != null) firstStop.latitude
                                    else SeedData.locationById(firstStop?.locationId ?: "")?.latitude ?: 0.0
                                val lng = if (firstStop?.isCustomPin == true && firstStop.longitude != null) firstStop.longitude
                                    else SeedData.locationById(firstStop?.locationId ?: "")?.longitude ?: 0.0
                                if (lat != 0.0 || lng != 0.0) {
                                    OfflineMapExporter.saveOfflineMapImage(context, lat, lng)
                                }
                                isGeneratingMapImage = false
                            }
                        },
                        accent = Citrine,
                        textColor = Citrine,
                        icon = Icons.Filled.PhotoCamera,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isGeneratingMapImage,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Stitches a single ultra high-resolution satellite image (3-mile radius, zoom 15) of the first stop and saves it to your gallery's Offline Maps folder. Pinch to zoom in deep — trails, terrain, and landmarks stay crisp even with no signal. Great for finding your way back to your vehicle when you're off-grid.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                    )
                    Spacer(Modifier.height(8.dp))
                    SculptedButton(
                        text = "Get Directions",
                        onClick = {
                            val dirStops = trip.stops.mapNotNull { stop ->
                                if (stop.isCustomPin && stop.latitude != null && stop.longitude != null) {
                                    Pair(stop.latitude, stop.longitude)
                                } else {
                                    SeedData.locationById(stop.locationId)?.let { Pair(it.latitude, it.longitude) }
                                }
                            }
                            SafeLinkOpener.openMultiStopDirections(context, dirStops)
                        },
                        accent = Citrine,
                        containerColor = Citrine,
                        textColor = Color.Black,
                        icon = Icons.Filled.Directions,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else if (trip.stops.size == 1) {
                    Spacer(Modifier.height(8.dp))
                    SculptedButton(
                        text = "Get Directions",
                        onClick = {
                            val stop = trip.stops[0]
                            if (stop.isCustomPin && stop.latitude != null && stop.longitude != null) {
                                SafeLinkOpener.openMultiStopDirections(context, listOf(Pair(stop.latitude, stop.longitude)))
                            } else {
                                SeedData.locationById(stop.locationId)?.let { loc ->
                                    SafeLinkOpener.openMultiStopDirections(context, listOf(Pair(loc.latitude, loc.longitude)))
                                }
                            }
                        },
                        accent = Citrine,
                        containerColor = Citrine,
                        textColor = Color.Black,
                        icon = Icons.Filled.Directions,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (trip.targetSpecimens.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text("Hunting", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        trip.targetSpecimens.forEach { TagChip(it, color = Aqua) }
                    }
                }
                // ── Weather forecast for trip stops ──
                if (trip.stops.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    TripWeatherSection(trip = trip)
                }
                if (trip.gearChecklist.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text("Gear", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    trip.gearChecklist.forEach { item ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
                            Box(modifier = Modifier.size(14.dp).clip(RoundedCornerShape(3.dp))
                                .glowingBorder(2.4.dp, TextLow, RoundedCornerShape(3.dp)))
                            Spacer(Modifier.width(8.dp))
                            Text(item, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                if (trip.notes.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Text("Notes", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(trip.notes, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        },
        confirmButton = {
            var isExportingPdf by remember { mutableStateOf(false) }
            Column(modifier = Modifier.fillMaxWidth()) {
                // Row 1: Share Trip text + Export PDF
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SculptedOutlinedButton(
                        text = if (isExportingPdf) "Generating…" else "Share Trip",
                        onClick = {
                            val routeText = trip.stops.joinToString(" → ") { it.locationName }.ifBlank { "No stops yet" }
                            val targetText = if (trip.targetSpecimens.isNotEmpty())
                                "\nHunting: ${trip.targetSpecimens.joinToString(", ")}" else ""
                            val markerCount = if (trip.specimenMarkers.isNotEmpty())
                                "\nSpecimen markers: ${trip.specimenMarkers.size}" else ""
                            val summary = """${trip.name}
${SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(trip.date))}
${trip.stops.size} stop${if (trip.stops.size != 1) "s" else ""}: $routeText$targetText$markerCount

Planned with RockScout""".trimIndent()
                            val sendIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_SUBJECT, trip.name)
                                putExtra(android.content.Intent.EXTRA_TEXT, summary)
                            }
                            SafeLinkOpener.openShareChooser(context, sendIntent, "Share Trip")
                        },
                        accent = Aqua,
                        textColor = Aqua,
                        icon = Icons.Filled.Share,
                        modifier = Modifier.weight(1f),
                    )
                    SculptedOutlinedButton(
                        text = if (isExportingPdf) "Generating…" else "Export PDF",
                        onClick = {
                            if (isExportingPdf) return@SculptedOutlinedButton
                            isExportingPdf = true
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    runCatching {
                                        TripPdfExporter.exportTripPdf(context, trip, mapBitmap = null)
                                    }
                                }
                                isExportingPdf = false
                            }
                        },
                        accent = Citrine,
                        textColor = Citrine,
                        icon = Icons.Filled.FileDownload,
                        enabled = !isExportingPdf,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(8.dp))
                // Row 2: Edit + Share Image + Close. Share Image is the widest
                // label, so it gets a larger weight so it never wraps or clip.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SculptedTextButton(
                        text = "Edit",
                        onClick = onEdit,
                        accent = Citrine,
                        textColor = Citrine,
                        modifier = Modifier.weight(1f),
                    )
                    SculptedButton(
                        text = "Share Image",
                        onClick = onShare,
                        accent = Citrine,
                        containerColor = Citrine,
                        textColor = Color.Black,
                        modifier = Modifier.weight(1.5f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                    )
                    SculptedOutlinedButton(
                        text = "Close",
                        onClick = onDismiss,
                        accent = Citrine,
                        textColor = Citrine,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        },
        dismissButton = {},
    )
}

/**
 * Full-screen map dialog for dropping a custom trip pin. User taps the map to
 * place a pin, types a name (e.g. "Gas station", "Crystal Creek crossing"),
 * and optionally toggles "Submit as rock location" for admin review.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomPinPickerSheet(
    onDismiss: () -> Unit,
    onPick: (name: String, lat: Double, lng: Double, submitAsRockLocation: Boolean) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = AppRepository.instance
    val current by repo.currentLocation.collectAsStateWithLifecycle()
    val auth = AuthRepository.instance
    val profile by repo.profile.collectAsStateWithLifecycle()
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var pinLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var pinName by remember { mutableStateOf("") }
    var submitAsRockLocation by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    var showDownloadSheet by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Drop a custom pin", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).imePadding()) {
                Text(
                    "Tap the map to place a pin for any stop on your route — gas stations, food stops, creek crossings, or unnamed collecting spots.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextLow,
                )
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(295.dp)
                        .clip(RoundedCornerShape(20.dp)),
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            createRockScoutMapView(ctx).apply {
                                controller.setZoom(10.0)
                                controller.setCenter(GeoPoint(current.first, current.second))
                                overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
                                overlays.add(CompassOverlay(ctx, this).apply { enableCompass() })

                                overlays.add(object : org.osmdroid.views.overlay.Overlay() {
                                    override fun onSingleTapConfirmed(e: android.view.MotionEvent?, view: MapView?): Boolean {
                                        if (e == null || view == null) return false
                                        val proj = view.projection
                                        val point = proj.fromPixels(e.x.toInt(), e.y.toInt())
                                        pinLocation = Pair(point.latitude, point.longitude)
                                        view.overlays.removeAll { it is Marker && it.id == "custom_pin_preview" }
                                        val marker = Marker(view).apply {
                                            id = "custom_pin_preview"
                                            position = GeoPoint(point.latitude, point.longitude)
                                            title = "Pin location"
                                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        }
                                        view.overlays.add(marker)
                                        view.invalidate()
                                        return true
                                    }
                                })

                                mapView = this
                            }
                        },
                        update = { /* no-op */ },
                    )
                    MapZoomControls(
                        onZoomIn = { mapView?.let { it.controller.zoomIn() } },
                        onZoomOut = { mapView?.let { it.controller.zoomOut() } },
                        onRecenter = {
                            val mv = mapView ?: return@MapZoomControls
                            pinLocation?.let { mv.controller.animateTo(GeoPoint(it.first, it.second)) }
                                ?: mv.controller.animateTo(GeoPoint(current.first, current.second))
                        },
                        showUser = false,
                        onSatellite = { toggleSatelliteView(mapView) },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                    )
                    MapExpandButton(
                        onClick = { isFullscreen = true },
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                    )
                    MapOfflineNotice(
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
                    )
                }
                // Download map at pin location button
                if (pinLocation != null) {
                    Spacer(Modifier.height(10.dp))
                    SculptedOutlinedButton(
                        text = "Download map",
                        onClick = { showDownloadSheet = true },
                        accent = Aqua,
                        textColor = Aqua,
                        icon = Icons.Filled.Download,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        "at pin location",
                        style = MaterialTheme.typography.labelSmall,
                        color = Aqua,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp),
                    )
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = pinName,
                    onValueChange = { pinName = ProfanityFilter.filter(it) },
                    label = { Text("Pin name (e.g. \"Gas station\", \"Crystal Creek\")") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                )
                if (pinLocation != null) {
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Aqua, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "%.4f, %.4f".format(pinLocation!!.first, pinLocation!!.second),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextLow,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Aqua.copy(alpha = 0.08f))
                            .glowingBorder(1.dp, Aqua.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .clickable { submitAsRockLocation = !submitAsRockLocation }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (submitAsRockLocation) Aqua else Color.Transparent)
                                .glowingBorder(2.dp, Aqua, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (submitAsRockLocation) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Submit as rock location for review",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextHigh,
                        )
                    }
                }
            }
        },
        confirmButton = {
            SculptedButton(
                text = "Add pin",
                onClick = {
                    val loc = pinLocation ?: return@SculptedButton
                    val name = pinName.ifBlank { "Custom stop" }
                    val shouldSubmit = submitAsRockLocation
                    if (shouldSubmit) {
                        scope.launch {
                            val submitterName = profile.name.ifBlank { "Anonymous" }
                            val submitterId = auth.currentUserId
                            UserPinSubmissionStore.add(
                                UserPinSubmissionStore.PinSubmission(
                                    id = "pin-sub-${UUID.randomUUID()}",
                                    name = name,
                                    latitude = loc.first,
                                    longitude = loc.second,
                                    submitterName = submitterName,
                                    submitterId = submitterId,
                                    submittedAt = System.currentTimeMillis(),
                                    webSnippet = "",
                                    webUrl = "",
                                    manuallyFlagged = true,
                                )
                            )
                        }
                    }
                    onPick(name, loc.first, loc.second, shouldSubmit)
                },
                accent = Citrine,
                containerColor = Citrine,
                textColor = Color.Black,
                enabled = pinLocation != null,
            )
        },
        dismissButton = { SculptedTextButton(text = "Cancel", onClick = onDismiss, accent = Citrine, textColor = Citrine) },
    )

    if (isFullscreen) {
        FullscreenMapOverlay(
            onDismiss = { isFullscreen = false },
            initialCenter = mapView?.let {
                GeoPoint(it.mapCenter.latitude, it.mapCenter.longitude)
            } ?: GeoPoint(current.first, current.second),
            initialZoom = mapView?.zoomLevelDouble ?: 10.0,
            onMapReady = { fsMv ->
                fsMv.overlays.add(object : org.osmdroid.views.overlay.Overlay() {
                    override fun onSingleTapConfirmed(e: android.view.MotionEvent?, view: MapView?): Boolean {
                        if (e == null || view == null) return false
                        val proj = view.projection
                        val point = proj.fromPixels(e.x.toInt(), e.y.toInt())
                        pinLocation = Pair(point.latitude, point.longitude)
                        view.overlays.removeAll { it is Marker && it.id == "custom_pin_preview_fs" }
                        val marker = Marker(view).apply {
                            id = "custom_pin_preview_fs"
                            position = GeoPoint(point.latitude, point.longitude)
                            title = "Pin location"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        view.overlays.add(marker)
                        view.invalidate()
                        return true
                    }
                })
                fsMv.invalidate()
            },
        )
    }

    MapViewLifecycleEffect(mapView)
}

/**
 * Weather forecast section for trip stops. Fetches current conditions for each
 * stop with coordinates and displays them in a compact card.
 */
@Composable
private fun TripWeatherSection(trip: Trip) {
    val scope = rememberCoroutineScope()
    val weatherState = remember(trip.id) { mutableStateOf<Map<String, WeatherSnapshot>>(emptyMap()) }
    var isRefreshing by remember(trip.id) { mutableStateOf(false) }

    // Collect stops with coordinates so we can fetch weather for each
    val stopsWithCoords = remember(trip.stops) {
        trip.stops.mapNotNull { stop ->
            val coords = if (stop.isCustomPin && stop.latitude != null && stop.longitude != null) {
                Pair(stop.latitude, stop.longitude)
            } else {
                SeedData.locationById(stop.locationId)?.let { Pair(it.latitude, it.longitude) }
            }
            coords?.let { Triple(stop.locationId, stop.locationName, it) }
        }
    }

    LaunchedEffect(trip.id, stopsWithCoords.size) {
        if (stopsWithCoords.isEmpty()) return@LaunchedEffect
        isRefreshing = true
        scope.launch {
            val results = mutableMapOf<String, WeatherSnapshot>()
            stopsWithCoords.forEach { (id, _, coords) ->
                val snap = WeatherRepository.fetch("trip_${trip.id}_$id", coords.first, coords.second)
                if (snap != null) results[id] = snap
            }
            weatherState.value = results
            isRefreshing = false
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                Icons.Filled.CloudDone,
                contentDescription = null,
                tint = Color(0xFF6FA8C7),
                modifier = Modifier.size(18.dp),
            )
            Text(
                "Weather at your stops",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6FA8C7),
            )
            if (isRefreshing) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF6FA8C7),
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        if (stopsWithCoords.isEmpty()) {
            Text(
                "Add stops with locations to see weather forecasts.",
                style = MaterialTheme.typography.bodySmall,
                color = TextLow,
            )
        } else {
            stopsWithCoords.forEach { (stopId, name, coords) ->
                val snap = weatherState.value[stopId]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF6FA8C7).copy(alpha = 0.08f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        if (snap != null) snap.conditionEmoji else "⏳",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (snap != null) {
                            Text(
                                "${snap.temperatureF}°F · ${snap.conditionLabel} · ${snap.precipProbability}% rain · ${snap.windMph} mph wind",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextLow,
                            )
                        } else if (!isRefreshing) {
                            Text(
                                "Weather unavailable — check connection",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextLow,
                            )
                        }
                    }
                    if (snap != null) {
                        Text(
                            "${snap.temperatureF}°",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF6FA8C7),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Forecast from Open-Meteo. Updated every 30 minutes. Check conditions before you drive out.",
                style = MaterialTheme.typography.labelSmall,
                color = TextLow,
            )
        }
    }
}

package com.rork.rockscout.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AchievementsRepository
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.JournalEntry
import com.rork.rockscout.data.JournalPdfExporter
import com.rork.rockscout.data.ProfanityFilter
import com.rork.rockscout.data.ImageModerator
import com.rork.rockscout.data.ImageUtils
import com.rork.rockscout.data.ModerationTriState
import com.rork.rockscout.data.ImageReviewRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.SpecimenMarker
import com.rork.rockscout.data.Trip
import com.rork.rockscout.data.XpSource
import com.rork.rockscout.ui.components.AddShareDropdown
import com.rork.rockscout.ui.components.CardAction
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.FullScreenImageViewer
import com.rork.rockscout.ui.components.LongPressableImage
import com.rork.rockscout.ui.components.ImageSourcePickerDialog
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.processImageUri
import com.rork.rockscout.ui.components.MultiPinDropMap
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.ShareCardImage
import com.rork.rockscout.ui.components.SpecimenMarkerMap
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.R
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.TextLow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.DeleteConfirmDialog
import com.rork.rockscout.ui.components.glowingBorder

@Composable
fun FieldJournalScreen(navController: NavController, embedded: Boolean = false) {
    val repo = AppRepository.instance
    // Field Journal is a personal tool — always free, never locked.
    val entries by repo.journalEntries.collectAsStateWithLifecycle()
    val captures by repo.captures.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var editingEntry by remember { mutableStateOf<JournalEntry?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var detailEntry by remember { mutableStateOf<JournalEntry?>(null) }
    var shareToProfileEntry by remember { mutableStateOf<JournalEntry?>(null) }
    var viewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerInitialPage by remember { mutableStateOf(0) }
    var pendingDeleteEntry by remember { mutableStateOf<JournalEntry?>(null) }

    LaunchedEffect(Unit) {
        // Seed a demo journal entry the first time so the screen isn't empty.
        if (entries.isEmpty()) {
            val sample = JournalEntry(
                id = UUID.randomUUID().toString(),
                date = System.currentTimeMillis() - 2L * 24 * 60 * 60 * 1000,
                location = "Crater of Diamonds State Park, AR",
                digSiteId = "crater-of-diamonds",
                weatherSummary = "82°F • Sunny • 10% rain • Sunrise 6:14 AM • Sunset 8:02 PM",
                notes = "Arrived at opening and started on the east drain. Soil was wet from yesterday's rain — perfect for spotting diamonds. Found a small white plume crystal by mid-morning, then a 4-point clear quartz in the gravel bar. The sifter earned its keep today. Took a break at noon for shade and water, then hunted the west field until 3 PM. No diamonds this trip, but the day was a great one out in the dirt.",
                photoUris = emptyList(),
                attachedCaptureIds = captures.take(2).map { it.id },
            )
            repo.saveJournalEntry(sample)
        }
    }

    val journalContent: @Composable () -> Unit = {
        if (entries.isEmpty()) {
            EmptyState(
                emoji = "\uD83D\uDCD6",
                title = "No journal entries yet",
                message = "The story of a day in the field — weather, notes, photos, the company you kept. Tap the + above to start your first entry.",
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
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
                        Text(
                            "Your per-trip stories: weather, notes, and photos from a day in the field. Tap to read or add an entry.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Aqua,
                        )
                    }
                }
                items(entries, key = { it.id }) { entry ->
                    JournalCard(
                        entry = entry,
                        attachedCaptureCount = captures.count { entry.attachedCaptureIds.contains(it.id) },
                        onOpen = { detailEntry = entry },
                        onEdit = { editingEntry = entry; showEditor = true },
                        onDelete = { pendingDeleteEntry = entry },
                        onPhotoClick = { urls, page ->
                            viewerUrls = urls
                            viewerInitialPage = page
                        },
                        onShare = {
                            scope.launch {
                                runCatching {
                                    ShareCardImage.share(
                                        context = context,
                                        title = entry.location.ifBlank { "Field Journal" } +
                                            "  •  " + SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(entry.date)),
                                        subtitle = entry.weatherSummary.ifBlank { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(entry.date)) },
                                        body = entry.notes.ifBlank { null },
                                        accentHex = 0xFF6FA8C7,
                                        photoBitmap = entry.photoUris.firstOrNull()?.let { uriStr ->
                                            ShareCardImage.loadDownsampled(context, android.net.Uri.parse(uriStr))
                                        },
                                        caption = "Logged with RockScout",
                                        fileName = "rockscout_journal_${entry.id}",
                                    )
                                }
                            }
                        },
                    )
                }
            }
        }
    }

    if (embedded) {
        // Embedded mode: render without the ScreenScaffold wrapper so the
        // merged Trip Planner & Field Journal screen can host this as a tab
        // page inside its own scaffold + pill switcher.
        Box(modifier = Modifier.fillMaxSize()) {
            journalContent()
            // Floating add button for embedded mode (no top-bar action).
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 24.dp)
                    .size(52.dp)
                    .sculpted(
                        shape = CircleShape,
                        accent = Aqua,
                        shadowElevation = 8.dp,
                        circular = true,
                        onClick = { editingEntry = null; showEditor = true },
                    )
                    .clip(CircleShape)
                    .background(Aqua.copy(alpha = 0.18f))
                    .glowingBorder(2.dp, Aqua, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "New entry", tint = Aqua, modifier = Modifier.size(26.dp))
            }
        }
    } else {
        ScreenScaffold(
            title = "Field Journal",
            onBack = { navController.popBackStack() },
            actions = {
                IconButton(onClick = { editingEntry = null; showEditor = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "New entry", tint = MaterialTheme.colorScheme.onBackground)
                }
            },
            background = { innerContent ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.field_journal_background),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                    AsyncImage(
                        model = "https://r2-pub.rork.com/attachments/abxrhqw66vap6ksxlr670.png",
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                    // Darker scrim so the field-journal photo stays visible while
                    // every tile and line of text remains clearly legible.
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.40f),
                                        Color.Black.copy(alpha = 0.52f),
                                        Color.Black.copy(alpha = 0.64f),
                                        Color.Black.copy(alpha = 0.76f),
                                    )
                                )
                            )
                    )
                    innerContent()
                }
            },
        ) {
            journalContent()
        }
    }

    // Intercept system back when the editor is open so it dismisses the
    // editor instead of popping the entire Field Journal screen.
    BackHandler(enabled = showEditor) { showEditor = false; editingEntry = null }

    if (showEditor) {
        JournalEditorScreen(
            initial = editingEntry,
            availableCaptures = captures,
            availableTrips = repo.trips.value,
            onDismiss = { showEditor = false; editingEntry = null },
            onSave = { saved ->
                val isNew = !repo.journalEntries.value.any { it.id == saved.id }
                repo.saveJournalEntry(saved)
                if (isNew) AchievementsRepository.award(XpSource.JOURNAL_ENTRY)
                showEditor = false
                editingEntry = null
            },
        )
        return
    }

    detailEntry?.let { entry ->
        JournalDetailSheet(
            entry = entry,
            attachedCaptures = captures.filter { entry.attachedCaptureIds.contains(it.id) },
            onDismiss = { detailEntry = null },
            onEdit = { detailEntry = null; editingEntry = entry; showEditor = true },
            onPhotoClick = { urls, page ->
                viewerUrls = urls
                viewerInitialPage = page
            },
            onOpenCapture = { capId ->
                // Jump to the field capture via the captures list — simplest is to pop back to Field tab.
                detailEntry = null
                navController.navigate(Routes.CAPTURES)
            },
            onShare = {
                scope.launch {
                    runCatching {
                        ShareCardImage.share(
                            context = context,
                            title = entry.location.ifBlank { "Field Journal" } +
                                "  •  " + SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(entry.date)),
                            subtitle = entry.weatherSummary.ifBlank { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(entry.date)) },
                            body = entry.notes.ifBlank { null },
                            accentHex = 0xFF6FA8C7,
                            photoBitmap = entry.photoUris.firstOrNull()?.let { uriStr ->
                                ShareCardImage.loadDownsampled(context, android.net.Uri.parse(uriStr))
                            },
                            caption = "Logged with RockScout",
                            fileName = "rockscout_journal_${entry.id}",
                        )
                    }
                }
            },
            onShareToProfile = { shareToProfileEntry = entry },
            onExportPdf = {
                scope.launch {
                    withContext(Dispatchers.IO) {
                        val captureNames = entry.attachedCaptureIds.mapNotNull { capId ->
                            captures.find { it.id == capId }?.let { cap ->
                                cap.displayName(SeedData.specimenById(cap.specimenId)?.name ?: "Unknown specimen")
                            }
                        }
                        JournalPdfExporter.exportJournalPdf(context, entry, captureNames)
                    }
                }
            },
        )
    }

    shareToProfileEntry?.let { entry ->
        ShareToProfileComposer(
            sourceType = "journal",
            title = entry.location.ifBlank { "Field Journal" },
            tagline = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(entry.date)) +
                if (entry.weatherSummary.isNotBlank()) "  •  ${entry.weatherSummary}" else "",
            imageUri = entry.photoUris.firstOrNull(),
            locationText = entry.location,
            onDismiss = { shareToProfileEntry = null },
        )
    }

    if (viewerUrls.isNotEmpty()) {
        FullScreenImageViewer(
            imageUrls = viewerUrls,
            initialPage = viewerInitialPage,
            onDismiss = { viewerUrls = emptyList() },
        )
    }

    pendingDeleteEntry?.let { entry ->
        DeleteConfirmDialog(
            title = "Delete journal entry?",
            message = "Delete the entry from ${entry.location.ifBlank { "this location" }}? This action cannot be undone.",
            onConfirm = {
                repo.deleteJournalEntry(entry.id)
                pendingDeleteEntry = null
            },
            onDismiss = { pendingDeleteEntry = null },
        )
    }
}

@Composable
private fun JournalCard(
    entry: JournalEntry,
    attachedCaptureCount: Int,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onExportPdf: () -> Unit = {},
    onPhotoClick: (List<String>, Int) -> Unit = { _, _ -> },
) {
    val dateFormat = remember { SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()) }
    val accent = Aqua
    val cardShape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = cardShape, accent = accent, shadowElevation = 6.dp, onClick = onOpen)
            .clip(cardShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.60f),
                        Color.Black.copy(alpha = 0.72f),
                        Color.Black.copy(alpha = 0.84f),
                    )
                )
            )
            .glowingBorder(3.dp, accent.copy(alpha = 0.50f), cardShape),
    ) {
        // Accent glow overlay at top so the journal photo peeks through.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(100.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(accent.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "JOURNAL",
                        style = MaterialTheme.typography.labelMedium,
                        color = accent,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        entry.location.ifBlank { "Untitled location" },
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(dateFormat.format(Date(entry.date)), style = MaterialTheme.typography.bodyMedium, color = DarkTextMid)
                    if (entry.weatherSummary.isNotBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Text(entry.weatherSummary, style = MaterialTheme.typography.labelSmall, color = DarkTextMid, maxLines = 1)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    SculptedIconButton(icon = Icons.Filled.Edit, contentDescription = "Edit", onClick = onEdit, accent = Aqua, iconTint = DarkTextMid, size = 36.dp, shadowElevation = 3.dp)
                    SculptedIconButton(icon = Icons.Filled.PictureAsPdf, contentDescription = "Export PDF", onClick = onExportPdf, accent = Aqua, iconTint = DarkTextMid, size = 36.dp, shadowElevation = 3.dp)
                    SculptedIconButton(icon = Icons.Filled.Share, contentDescription = "Share", onClick = onShare, accent = Aqua, iconTint = DarkTextMid, size = 36.dp, shadowElevation = 3.dp)
                    SculptedIconButton(icon = Icons.Filled.Delete, contentDescription = "Delete", onClick = onDelete, accent = Aqua, iconTint = DarkTextMid, size = 36.dp, shadowElevation = 3.dp)
                }
            }
            if (entry.notes.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    entry.notes.take(180) + if (entry.notes.length > 180) "…" else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextHigh,
                    maxLines = 4,
                )
            }
            if (entry.photoUris.isNotEmpty() || attachedCaptureCount > 0) {
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    entry.photoUris.take(4).forEachIndexed { idx, uri ->
                        Box(
                            modifier = Modifier.size(46.dp).clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1A1812))
                                .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                        ) {
                            LongPressableImage(
                                model = uri,
                                contentDescription = "Journal photo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop,
                                onClick = { onPhotoClick(entry.photoUris, idx) },
                            )
                        }
                    }
                    if (attachedCaptureCount > 0) {
                        TagChip("$attachedCaptureCount capture${if (attachedCaptureCount != 1) "s" else ""}", color = Citrine)
                    }
                    if (entry.specimenMarkers.isNotEmpty()) {
                        TagChip("${entry.specimenMarkers.size} pin${if (entry.specimenMarkers.size != 1) "s" else ""}", color = Aqua)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JournalEditorScreen(
    initial: JournalEntry?,
    availableCaptures: List<com.rork.rockscout.data.CapturedPhoto>,
    availableTrips: List<Trip>,
    onDismiss: () -> Unit,
    onSave: (JournalEntry) -> Unit,
) {
    val isEdit = initial != null
    var dateMillis by remember { mutableStateOf(initial?.date ?: System.currentTimeMillis()) }
    var location by remember { mutableStateOf(initial?.location ?: "") }
    var selectedDigSiteId by remember { mutableStateOf(initial?.digSiteId) }
    var selectedTripId by remember { mutableStateOf(initial?.tripId) }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }
    val attachedCaptureIds = remember { mutableStateListOf<String>().apply { initial?.attachedCaptureIds?.let { addAll(it) } } }
    val photoUris = remember { mutableStateListOf<String>().apply { initial?.photoUris?.let { addAll(it) } } }
    val specimenMarkers = remember { mutableStateListOf<SpecimenMarker>().apply { initial?.specimenMarkers?.let { addAll(it) } } }
    var pendingPinLat by remember { mutableStateOf<Double?>(null) }
    var pendingPinLng by remember { mutableStateOf<Double?>(null) }
    var newMarkerName by remember { mutableStateOf("") }
    var newMarkerDesc by remember { mutableStateOf("") }
    var newMarkerCategory by remember { mutableStateOf("Other") }
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var showLocationPicker by remember { mutableStateOf(false) }
    var showUploadLocationDialog by remember { mutableStateOf(false) }
    var uploadLocationMessage by remember { mutableStateOf<String?>(null) }
    var showCapturePicker by remember { mutableStateOf(false) }
    var showTripPicker by remember { mutableStateOf(false) }
    var showImageSourcePicker by remember { mutableStateOf(false) }
    var weatherSummary by remember { mutableStateOf(initial?.weatherSummary ?: "") }
    var pendingPhotoDeleteIdx by remember { mutableStateOf<Int?>(null) }
    var pendingDetachCaptureIdx by remember { mutableStateOf<Int?>(null) }
    var pendingRemoveMarkerIdx by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri -> if (uri != null) {
        if (photoUris.size >= 50) return@rememberLauncherForActivityResult
        // Reject files larger than 5 MB before any pipeline work.
        if (ImageUtils.isOverUploadLimit(context, uri)) {
            android.widget.Toast.makeText(
                context,
                "That image is over 5 MB. Please choose a smaller photo.",
                android.widget.Toast.LENGTH_LONG,
            ).show()
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            val base64 = ImageUtils.uriToModerationBase64(context, uri)
            if (base64 == null) return@launch
            val verdict = ImageModerator.scan(base64, "image/jpeg")
            when (verdict.triState) {
                ModerationTriState.CLEAN -> {
                    val persistentPath = ImageUtils.copyUriToInternalStorage(
                        context, uri, "journal_photos",
                    )
                    photoUris.add(persistentPath ?: uri.toString())
                }
                ModerationTriState.EXPLICIT -> {
                    android.widget.Toast.makeText(
                        context,
                        verdict.reason.ifBlank { "This image can't be used because it violates our family-friendly policies." },
                        android.widget.Toast.LENGTH_LONG,
                    ).show()
                }
                ModerationTriState.QUESTIONABLE -> {
                    val persistentPath = ImageUtils.copyUriToInternalStorage(
                        context, uri, "journal_photos",
                    )
                    val userId = AuthRepository.instance.currentUserId
                    val userName = AppRepository.instance.profile.value.name
                    val avatar = AppRepository.instance.profile.value.avatarEmoji
                    ImageReviewRepository.instance.submitReview(
                        userId = userId ?: "unknown",
                        userName = userName,
                        userAvatar = avatar,
                        imageUri = persistentPath ?: uri.toString(),
                        type = "journal_photo",
                        reason = verdict.reason,
                    )
                    photoUris.add(persistentPath ?: uri.toString())
                    android.widget.Toast.makeText(
                        context,
                        "Image submitted for review. It'll be visible once approved.",
                        android.widget.Toast.LENGTH_LONG,
                    ).show()
                }
            }
        }
    } }

    ScreenScaffold(
        title = if (isEdit) "Edit Entry" else "New Journal Entry",
        onBack = onDismiss,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = ProfanityFilter.filter(it); selectedDigSiteId = null },
                        label = { Text("Location") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                        modifier = Modifier.weight(1f).noAutoFocus(),
                    )
                    Spacer(Modifier.width(8.dp))
                    SculptedOutlinedButton(
                        text = "Dig site",
                        icon = Icons.Filled.Add,
                        onClick = { showLocationPicker = true },
                        accent = Citrine,
                        textColor = Citrine,
                    )
                }
                Spacer(Modifier.height(8.dp))
                if (selectedDigSiteId != null) {
                    val site = SeedData.allLocations.firstOrNull { it.id == selectedDigSiteId }
                    if (site != null) {
                        Text("Linked dig site: ${site.name}", style = MaterialTheme.typography.labelMedium, color = Citrine)
                    }
                }
                Spacer(Modifier.height(8.dp))
                SculptedOutlinedButton(
                    text = "Upload New Location",
                    icon = Icons.Filled.AddLocationAlt,
                    onClick = { showUploadLocationDialog = true },
                    accent = Aqua,
                    textColor = Aqua,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Trip: ", style = MaterialTheme.typography.bodyMedium)
                    val trip = availableTrips.firstOrNull { it.id == selectedTripId }
                    Text(
                        trip?.name ?: "—",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (trip != null) Aqua else TextLow,
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    SculptedOutlinedButton(
                        text = if (trip != null) "Change" else "Link a trip",
                        onClick = { showTripPicker = true },
                        accent = Citrine,
                        textColor = Citrine,
                    )
                    if (trip != null) {
                        Spacer(Modifier.width(6.dp))
                        SculptedIconButton(icon = Icons.Filled.Close, contentDescription = "Unlink trip", onClick = { selectedTripId = null }, accent = Citrine, iconTint = TextLow, size = 36.dp, shadowElevation = 3.dp)
                    }
                }

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = weatherSummary,
                    onValueChange = { weatherSummary = ProfanityFilter.filter(it) },
                    label = { Text("Weather summary") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                )

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = ProfanityFilter.filter(it) },
                    label = { Text("Notes — the story of the day") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp).noAutoFocus(),
                    minLines = 4,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Default),
                )

                Spacer(Modifier.height(12.dp))
                Text("Photos", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    photoUris.forEachIndexed { idx, uri ->
                        Box(
                            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1A1812))
                                .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Photo ${idx + 1}",
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop,
                            )
                            Box(
                                modifier = Modifier.align(Alignment.TopEnd).size(22.dp)
                                    .sculpted(
                                        shape = CircleShape,
                                        accent = Citrine,
                                        shadowElevation = 2.dp,
                                        circular = true,
                                        onClick = { pendingPhotoDeleteIdx = idx },
                                    )
                                    .clip(CircleShape)
                                    .background(Color.Black),
                                contentAlignment = Alignment.Center,
                            ) { Icon(Icons.Filled.Close, "Remove", tint = Color.White, modifier = Modifier.size(14.dp)) }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SculptedOutlinedButton(
                            text = "Add photo",
                            icon = Icons.Filled.Add,
                            onClick = { galleryLauncher.launch("image/*") },
                            accent = Citrine,
                            textColor = Citrine,
                        )
                        SculptedOutlinedButton(
                            text = "Saved images",
                            icon = Icons.Filled.Download,
                            onClick = { showImageSourcePicker = true },
                            accent = Citrine,
                            textColor = Citrine,
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        "Attached field captures (${attachedCaptureIds.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    SculptedOutlinedButton(
                        text = "Attach",
                        onClick = { showCapturePicker = true },
                        accent = Citrine,
                        textColor = Citrine,
                    )
                }
                if (attachedCaptureIds.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    attachedCaptureIds.forEachIndexed { idx, capId ->
                        val cap = availableCaptures.firstOrNull { it.id == capId }
                        if (cap != null) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                                Box(
                                    modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1A1812))
                                        .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (cap.imageUris.isNotEmpty()) {
                                        AsyncImage(
                                            model = cap.imageUris.first(),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop,
                                        )
                                    } else {
                                        Text(cap.specimenEmoji, style = MaterialTheme.typography.titleSmall)
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    cap.customName.ifBlank { SeedData.specimenById(cap.specimenId)?.name ?: "Unknown" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f),
                                )
                                SculptedIconButton(icon = Icons.Filled.Delete, contentDescription = "Detach", onClick = { pendingDetachCaptureIdx = idx }, accent = Aqua, iconTint = TextLow, size = 32.dp, shadowElevation = 3.dp)
                            }
                        }
                    }
                }

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
                        "Found Specimen Map",
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
                Text(
                    "Tap the map to drop a pin for each rock you found, then name it and add it.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextLow,
                )
                Spacer(Modifier.height(8.dp))
                MultiPinDropMap(
                    pins = specimenMarkers,
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    initialZoom = 10.0,
                    accent = Aqua,
                    pinLabel = "Found specimen",
                    showLayerToggle = true,
                    onPinSet = { lat, lng ->
                        pendingPinLat = lat
                        pendingPinLng = lng
                    },
                    onPinRemoved = { id ->
                        specimenMarkers.removeAll { it.id == id }
                    },
                )
                if (pendingPinLat != null && pendingPinLng != null) {
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newMarkerName,
                        onValueChange = { newMarkerName = ProfanityFilter.filter(it) },
                        label = { Text("Specimen name (e.g. Arkansas quartz)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next,
                        ),
                        modifier = Modifier.fillMaxWidth().noAutoFocus(),
                    )
                    Spacer(Modifier.height(8.dp))
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
                        text = "Add Found Specimen",
                        onClick = {
                            val lat = pendingPinLat
                            val lng = pendingPinLng
                            if (lat == null || lng == null) return@SculptedButton
                            val marker = SpecimenMarker(
                                id = UUID.randomUUID().toString(),
                                name = newMarkerName.ifBlank { "Specimen #${specimenMarkers.size + 1}" },
                                latitude = lat,
                                longitude = lng,
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
                        enabled = newMarkerName.isNotBlank(),
                    )
                }
                if (specimenMarkers.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    specimenMarkers.forEachIndexed { idx, marker ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Aqua.copy(alpha = 0.18f))
                                    .glowingBorder(1.dp, Aqua.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(marker.category.take(1), style = MaterialTheme.typography.labelMedium, color = Aqua, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(marker.name, style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(
                                    "%.4f, %.4f".format(marker.latitude, marker.longitude),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextLow,
                                )
                            }
                            SculptedIconButton(
                                icon = Icons.Filled.Delete,
                                contentDescription = "Remove marker",
                                onClick = { pendingRemoveMarkerIdx = idx },
                                accent = Aqua,
                                iconTint = TextLow,
                                size = 32.dp,
                                shadowElevation = 3.dp,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SculptedTextButton(
                    text = "Cancel",
                    onClick = onDismiss,
                    accent = Citrine,
                    textColor = Citrine,
                    modifier = Modifier.weight(1f),
                )
                SculptedButton(
                    text = if (isEdit) "Save Changes" else "Save Entry",
                    onClick = {
                        val entry = JournalEntry(
                            id = initial?.id ?: UUID.randomUUID().toString(),
                            date = dateMillis,
                            location = ProfanityFilter.filter(location.trim()),
                            digSiteId = selectedDigSiteId,
                            tripId = selectedTripId,
                            weatherSummary = ProfanityFilter.filter(weatherSummary.trim()),
                            notes = ProfanityFilter.filter(notes.trim()),
                            attachedCaptureIds = attachedCaptureIds.toList(),
                            photoUris = photoUris.toList(),
                            specimenMarkers = specimenMarkers.toList(),
                            createdAt = initial?.createdAt ?: System.currentTimeMillis(),
                        )
                        onSave(entry)
                    },
                    accent = Aqua,
                    containerColor = Aqua,
                    textColor = Color.Black,
                    modifier = Modifier.weight(1.5f),
                    enabled = location.isNotBlank() || notes.isNotBlank(),
                )
            }
        }
    }

    if (showLocationPicker) {
        SimpleLocationPicker(
            onDismiss = { showLocationPicker = false },
            onPick = { loc ->
                selectedDigSiteId = loc.id
                location = loc.name
                showLocationPicker = false
            },
        )
    }

    if (showUploadLocationDialog) {
        AddLocationDialog(
            onDismiss = { showUploadLocationDialog = false },
            onSubmitted = { approved ->
                uploadLocationMessage = if (approved) {
                    "Location web-verified and added to the map!"
                } else {
                    "Location submitted for review!"
                }
                showUploadLocationDialog = false
            },
        )
    }

    uploadLocationMessage?.let { msg ->
        androidx.compose.material3.SnackbarHost(
            hostState = remember { androidx.compose.material3.SnackbarHostState() }.also {
                androidx.compose.runtime.LaunchedEffect(msg) {
                    it.showSnackbar(msg)
                    uploadLocationMessage = null
                }
            },
        )
    }
    if (showCapturePicker) {
        CapturePickerSheet(
            available = availableCaptures.filter { it.id !in attachedCaptureIds },
            onDismiss = { showCapturePicker = false },
            onPick = { cap ->
                attachedCaptureIds.add(cap.id)
                showCapturePicker = false
            },
        )
    }
    if (showTripPicker) {
        TripPickerSheet(
            available = availableTrips,
            onDismiss = { showTripPicker = false },
            onPick = { trip ->
                selectedTripId = trip.id
                if (location.isBlank()) {
                    // Guard against empty stops — some legacy trips may have none.
                    location = trip.stops.firstOrNull()?.locationName ?: trip.name
                }
                showTripPicker = false
            },
        )
    }
    pendingRemoveMarkerIdx?.let { idx ->
        if (idx in specimenMarkers.indices) {
            DeleteConfirmDialog(
                title = "Remove specimen pin?",
                message = "Remove this specimen marker from the journal entry? You'll need to re-drop it on the map if you change your mind.",
                onConfirm = {
                    specimenMarkers.removeAt(idx)
                    pendingRemoveMarkerIdx = null
                },
                onDismiss = { pendingRemoveMarkerIdx = null },
            )
        } else {
            pendingRemoveMarkerIdx = null
        }
    }
    pendingPhotoDeleteIdx?.let { idx ->
        if (idx in photoUris.indices) {
            DeleteConfirmDialog(
                title = "Remove photo?",
                message = "Remove this photo from the journal entry? You'll need to re-add it if you change your mind.",
                onConfirm = {
                    photoUris.removeAt(idx)
                    pendingPhotoDeleteIdx = null
                },
                onDismiss = { pendingPhotoDeleteIdx = null },
            )
        } else {
            pendingPhotoDeleteIdx = null
        }
    }
    pendingDetachCaptureIdx?.let { idx ->
        if (idx in attachedCaptureIds.indices) {
            DeleteConfirmDialog(
                title = "Detach capture?",
                message = "Detach this field capture from the journal entry? The capture itself won't be deleted — you can reattach it later.",
                onConfirm = {
                    attachedCaptureIds.removeAt(idx)
                    pendingDetachCaptureIdx = null
                },
                onDismiss = { pendingDetachCaptureIdx = null },
            )
        } else {
            pendingDetachCaptureIdx = null
        }
    }

    if (showImageSourcePicker) {
        ImageSourcePickerDialog(
            onDismiss = { showImageSourcePicker = false },
            onImageSelected = { uri ->
                showImageSourcePicker = false
                scope.launch {
                    val path = processImageUri(context, uri, "journal_photos", "journal_photo")
                    if (path != null) photoUris.add(path)
                }
            },
        )
    }
}

@Composable
private fun SimpleLocationPicker(
    onDismiss: () -> Unit,
    onPick: (com.rork.rockscout.data.DigLocation) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Link a dig site", style = MaterialTheme.typography.headlineSmall) },
        text = {
            val query = remember { mutableStateOf("") }
            val locations = remember(query.value) {
                val all = SeedData.allLocations
                if (query.value.isBlank()) all else all.filter {
                    it.name.contains(query.value, ignoreCase = true) ||
                        it.region.contains(query.value, ignoreCase = true)
                }
            }
            Column(modifier = Modifier.fillMaxWidth().height(420.dp).imePadding()) {
                OutlinedTextField(
                    value = query.value,
                    onValueChange = { query.value = it },
                    label = { Text("Search dig sites") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                )
                Spacer(Modifier.height(10.dp))
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(locations.take(60), key = { it.id }) { loc ->
                        Box(
                            modifier = Modifier.fillMaxWidth().sculpted(shape = RoundedCornerShape(10.dp), accent = Citrine, shadowElevation = 3.dp, onClick = { onPick(loc) })
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
private fun CapturePickerSheet(
    available: List<com.rork.rockscout.data.CapturedPhoto>,
    onDismiss: () -> Unit,
    onPick: (com.rork.rockscout.data.CapturedPhoto) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Attach a field capture", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                if (available.isEmpty()) {
                    Text("No unattached captures available. Run an identification in the field first, then attach it here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextLow)
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(available, key = { it.id }) { cap ->
                            val name = cap.customName.ifBlank { SeedData.specimenById(cap.specimenId)?.name ?: "Unknown" }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().sculpted(shape = RoundedCornerShape(10.dp), accent = Citrine, shadowElevation = 3.dp, onClick = { onPick(cap) })
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .glowingBorder(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                    .padding(10.dp),
                            ) {
                                Box(
                                    modifier = Modifier.size(42.dp).clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1A1812))
                                        .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (cap.imageUris.isNotEmpty()) {
                                        AsyncImage(
                                            model = cap.imageUris.first(),
                                            contentDescription = name,
                                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop,
                                        )
                                    } else {
                                        Text(cap.specimenEmoji, style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(cap.timestamp)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextLow,
                                    )
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
private fun TripPickerSheet(
    available: List<Trip>,
    onDismiss: () -> Unit,
    onPick: (Trip) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Link a trip", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(360.dp)) {
                if (available.isEmpty()) {
                    Text("No trips planned yet. Plan one in the Trip Planner first, then link it here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextLow)
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(available, key = { it.id }) { trip ->
                            Box(
                                modifier = Modifier.fillMaxWidth().sculpted(shape = RoundedCornerShape(10.dp), accent = Citrine, shadowElevation = 3.dp, onClick = { onPick(trip) })
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .glowingBorder(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                            ) {
                                Column {
                                    Text(trip.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(trip.date)) +
                                            "  •  ${trip.stops.size} stop${if (trip.stops.size != 1) "s" else ""}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextLow,
                                    )
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
private fun JournalDetailSheet(
    entry: JournalEntry,
    attachedCaptures: List<com.rork.rockscout.data.CapturedPhoto>,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onOpenCapture: (String) -> Unit,
    onShare: () -> Unit,
    onExportPdf: () -> Unit = {},
    onShareToProfile: () -> Unit = {},
    onPhotoClick: (List<String>, Int) -> Unit = { _, _ -> },
) {
    val dateFormat = remember { SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Column {
                Text("JOURNAL", style = MaterialTheme.typography.labelMedium, color = Aqua, fontWeight = FontWeight.Bold)
                Text(entry.location.ifBlank { "Untitled location" }, style = MaterialTheme.typography.headlineSmall)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                Text(dateFormat.format(Date(entry.date)), style = MaterialTheme.typography.bodyMedium, color = TextLow)
                if (entry.weatherSummary.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(entry.weatherSummary, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
                if (entry.notes.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Text(entry.notes, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
                if (entry.photoUris.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text("Photos", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        entry.photoUris.take(4).forEachIndexed { idx, uri ->
                            Box(modifier = Modifier.size(72.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF1A1812))) {
                                LongPressableImage(
                                    model = uri,
                                    contentDescription = "Photo",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop,
                                    onClick = { onPhotoClick(entry.photoUris, idx) },
                                )
                            }
                        }
                    }
                }
                if (attachedCaptures.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text("Attached captures", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    attachedCaptures.forEach { cap ->
                        val name = cap.customName.ifBlank { SeedData.specimenById(cap.specimenId)?.name ?: "Unknown" }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                                .clickable { onOpenCapture(cap.id) }
                                .padding(vertical = 6.dp),
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF1A1812)),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (cap.imageUris.isNotEmpty()) {
                                    AsyncImage(
                                        model = cap.imageUris.first(),
                                        contentDescription = name,
                                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop,
                                    )
                                } else {
                                    Text(cap.specimenEmoji, style = MaterialTheme.typography.titleSmall)
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                if (entry.specimenMarkers.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text("Found Specimen Map", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Aqua)
                    Spacer(Modifier.height(6.dp))
                    SpecimenMarkerMap(
                        markers = entry.specimenMarkers,
                        modifier = Modifier.fillMaxWidth().height(220.dp),
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "${entry.specimenMarkers.size} pin${if (entry.specimenMarkers.size != 1) "s" else ""} dropped",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextLow,
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SculptedTextButton(
                    text = "Export PDF",
                    onClick = onExportPdf,
                    accent = Aqua,
                    textColor = Aqua,
                    modifier = Modifier.weight(1f),
                )
                SculptedTextButton(
                    text = "Cancel",
                    onClick = onDismiss,
                    accent = Citrine,
                    textColor = Citrine,
                    modifier = Modifier.weight(1f),
                )
            }
        },
    )
}

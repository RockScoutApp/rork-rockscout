package com.rork.rockscout.ui.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.rork.rockscout.data.LocationFetcher
import com.rork.rockscout.data.Museum
import com.rork.rockscout.data.MuseumRepository
import com.rork.rockscout.data.MuseumResponse
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Warning
import kotlinx.coroutines.launch

/**
 * Bottom sheet that displays nearby artifact-relevant museums.
 *
 * On open, checks location permission. If not granted, requests it.
 * Fetches the user's location, calls the /museums backend endpoint,
 * and displays a scrollable list of museum cards sorted by distance.
 *
 * Each card shows the museum name, type badge, address, distance,
 * phone (tappable), website (tappable), and an "Email Expert" button
 * that opens the [ReplyEmailDialog] before launching the email draft.
 *
 * @param onDismiss called when the sheet is dismissed
 * @param onEmailExpert called when the user taps "Email Expert" on a museum card;
 *   receives the museum so the caller can show the reply-email dialog and
 *   build the pre-filled email draft with the photo attachment.
 * @param artifactMatchNames the top artifact match name(s) for the email body
 * @param artifactConfidences the confidence percentage(s) for the email body
 * @param aiSummary the AI analysis summary for the email body
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuseumFinderSheet(
    onDismiss: () -> Unit,
    onEmailExpert: (Museum) -> Unit,
    onEmailExperts: (List<Museum>) -> Unit = {},
    artifactMatchNames: List<String> = emptyList(),
    artifactConfidences: List<Int> = emptyList(),
    aiSummary: String = "",
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isLoading by remember { mutableStateOf(true) }
    var museums by remember { mutableStateOf<List<Museum>>(emptyList()) }
    var expandedRadius by remember { mutableStateOf(false) }
    val selectedMuseums = remember { mutableStateListOf<Museum>() }
    var searchRadius by remember { mutableStateOf(50) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED,
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasLocationPermission = granted
        if (!granted) {
            isLoading = false
            errorMessage = "Location permission is needed to find nearby museums. Tap the button below to grant it."
        }
    }

    // Request permission on first show if not granted
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Fetch museums when permission is granted
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission && museums.isEmpty() && isLoading) {
            scope.launch {
                val location = LocationFetcher.fetch(context)
                if (location == null) {
                    errorMessage = "Could not determine your location. Please try again."
                    isLoading = false
                    return@launch
                }

                val result = MuseumRepository.fetchMuseums(
                    lat = location.latitude,
                    lon = location.longitude,
                )

                result
                    .onSuccess { response: MuseumResponse ->
                        museums = response.museums
                        expandedRadius = response.expandedRadius
                        searchRadius = response.searchRadiusMiles
                        isLoading = false
                    }
                    .onFailure { e ->
                        errorMessage = e.message ?: "Failed to find museums. Please try again."
                        isLoading = false
                    }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1E1C16),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Ask an Expert",
                    style = MaterialTheme.typography.titleLarge,
                    color = Citrine,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                SculptedIconButton(
                    icon = Icons.Filled.Close,
                    contentDescription = "Close",
                    onClick = onDismiss,
                    accent = Slate800,
                    iconTint = DarkTextMid,
                    backgroundColor = Slate800,
                    size = 36.dp,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Nearby museums and cultural centers that can help identify your artifact.",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
            )
            Spacer(Modifier.height(16.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            CircularProgressIndicator(
                                color = Citrine,
                                modifier = Modifier.size(40.dp),
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Finding museums near you...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextMid,
                            )
                        }
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = Warning,
                                modifier = Modifier.size(32.dp),
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                errorMessage ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextMid,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                            if (!hasLocationPermission) {
                                Spacer(Modifier.height(16.dp))
                                SculptedButton(
                                    text = "Grant Location Permission",
                                    onClick = {
                                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                    },
                                    accent = Citrine,
                                    containerColor = Citrine,
                                    textColor = Color.Black,
                                    icon = Icons.Filled.LocationOn,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }

                museums.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                "No museums found nearby.",
                                style = MaterialTheme.typography.titleMedium,
                                color = DarkTextHigh,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Try searching online for \"archaeologist near me\" or contact a regional university archaeology department.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextMid,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                    }
                }

                else -> {
                    // Expanded radius note
                    if (expandedRadius) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Warning.copy(alpha = 0.15f))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = Warning,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "No museums within 50 miles — showing the nearest options up to $searchRadius miles away.",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextMid,
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    // Museum cards
                    museums.forEach { museum ->
                        val isSelected = selectedMuseums.any { it.name == museum.name && it.address == museum.address }
                        MuseumCard(
                            museum = museum,
                            isSelected = isSelected,
                            onToggleSelect = {
                                if (isSelected) {
                                    selectedMuseums.removeAll { it.name == museum.name && it.address == museum.address }
                                } else {
                                    selectedMuseums.add(museum)
                                }
                            },
                            onEmailExpert = { onEmailExpert(museum) },
                        )
                        Spacer(Modifier.height(10.dp))
                    }

                    // Compose Email button — appears when museums are selected
                    if (selectedMuseums.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        SculptedButton(
                            text = "Compose Email (${selectedMuseums.size})",
                            onClick = {
                                onEmailExperts(selectedMuseums.toList())
                            },
                            accent = Citrine,
                            containerColor = Citrine,
                            textColor = Color.Black,
                            icon = Icons.Filled.Email,
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MuseumCard(
    museum: Museum,
    isSelected: Boolean = false,
    onToggleSelect: () -> Unit = {},
    onEmailExpert: () -> Unit = {},
) {
    val context = LocalContext.current
    DarkCard(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = onToggleSelect)
            .then(
                if (isSelected) Modifier.border(2.dp, Citrine, RoundedCornerShape(16.dp))
                else Modifier
            ),
        accent = if (isSelected) Citrine else Citrine.copy(alpha = 0.3f),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
    ) {
        // Name + type badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                museum.name,
                style = MaterialTheme.typography.titleSmall,
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Citrine.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    museum.type,
                    style = MaterialTheme.typography.labelSmall,
                    color = Citrine,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        // Address
        if (museum.address.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                museum.address,
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextMid,
            )
        }

        // Distance
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.AutoMirrored.Filled.DirectionsWalk,
                contentDescription = null,
                tint = Aqua,
                modifier = Modifier.size(14.dp),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                "${"%.1f".format(museum.distanceMiles)} miles away",
                style = MaterialTheme.typography.bodySmall,
                color = Aqua,
                fontWeight = FontWeight.Medium,
            )
        }

        // Action buttons
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Email Expert — primary action
            SculptedButton(
                text = "Email Expert",
                onClick = onEmailExpert,
                accent = Citrine,
                containerColor = Citrine,
                textColor = Color.Black,
                icon = Icons.Filled.Email,
                modifier = Modifier.weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            )

            // Directions
            SculptedIconButton(
                icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                contentDescription = "Directions",
                onClick = {
                    val uri = Uri.parse("google.navigation:q=${museum.lat},${museum.lon}")
                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                },
                accent = Aqua,
                iconTint = Aqua,
                backgroundColor = Slate800,
                size = 40.dp,
            )

            // Call (if phone available)
            if (museum.phone != null) {
                SculptedIconButton(
                    icon = Icons.Filled.Call,
                    contentDescription = "Call",
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${museum.phone}"))
                        context.startActivity(intent)
                    },
                    accent = Aqua,
                    iconTint = Aqua,
                    backgroundColor = Slate800,
                    size = 40.dp,
                )
            }

            // Website (if available)
            if (museum.website != null) {
                SculptedIconButton(
                    icon = Icons.Filled.Public,
                    contentDescription = "Website",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(museum.website))
                        context.startActivity(intent)
                    },
                    accent = Aqua,
                    iconTint = Aqua,
                    backgroundColor = Slate800,
                    size = 40.dp,
                )
            }
        }
    }
}

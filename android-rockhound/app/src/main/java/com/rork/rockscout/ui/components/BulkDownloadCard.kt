package com.rork.rockscout.ui.components

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.rockscout.RockScoutApplication
import com.rork.rockscout.data.ImagePrefetcher
import com.rork.rockscout.data.PersistenceManager
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Estimated download size shown to the user in all copy. */
private const val ESTIMATED_SIZE_GB = 3.5

/**
 * Reusable bulk-download control card for the "Download all specimen images
 * for offline" action. Shows:
 *  - The estimated size (3.5 GB) and current cache-mode gate.
 *  - A determinate progress bar with "X / Y images" + percentage while running.
 *  - A cellular warning confirmation dialog before starting on mobile data.
 *  - A resume/continue state after a cancel or app restart.
 *  - A completion banner with the actual bytes cached.
 *
 * Shared by the Specimen Database header pill sheet and the Storage settings
 * card so both surfaces stay in sync.
 *
 * @param isMaxCache        Whether the 2 GB Maximum cache toggle is on. If false,
 *                          the card shows an "Enable 2 GB cache first" prompt
 *                          and the [onEnableMaxCache] action is offered.
 * @param onEnableMaxCache  Callback invoked when the user taps the "Enable 2 GB
 *                          cache first" button.
 * @param modifier          Optional layout modifier.
 * @param compact           When true, renders a slimmer card suitable for a
 *                          bottom sheet body. Defaults to false (full card for
 *                          the settings screen).
 */
@Composable
fun BulkDownloadCard(
    isMaxCache: Boolean,
    onEnableMaxCache: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val scope = rememberCoroutineScope()

    // Re-load persisted state on every recomposition trigger (we bump this key
    // after each progress update so the UI reflects the latest counts).
    var state by remember {
        mutableStateOf(PersistenceManager.loadBulkDownloadState() ?: PersistenceManager.BulkDownloadState())
    }
    var refreshKey by remember { mutableStateOf(0) }
    LaunchedEffect(refreshKey) {
        if (refreshKey > 0) {
            state = PersistenceManager.loadBulkDownloadState() ?: PersistenceManager.BulkDownloadState()
        }
    }

    var job by remember { mutableStateOf<Job?>(null) }
    var cancelled by remember { mutableStateOf(state.cancelled) }
    val isRunning = state.running && !state.done && !cancelled
    val isComplete = state.done && state.total > 0 && state.finished >= state.total

    // Cellular warning dialog gate.
    var showCellularConfirm by remember { mutableStateOf(false) }

    // Completion snackbar state — auto-dismisses after a few seconds.
    var showCompletionBanner by remember { mutableStateOf(isComplete) }
    LaunchedEffect(isComplete) {
        if (isComplete) showCompletionBanner = true
    }

    val progressFraction = if (state.total > 0) {
        (state.finished.toFloat() / state.total.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val progressPercent = (progressFraction * 100f).toInt()

    fun startDownload() {
        val isWifi = appContext.isWifiConnected()
        if (!isWifi) {
            // Cellular or unknown: confirm before starting.
            showCellularConfirm = true
            return
        }
        beginRun(appContext, scope, state, job, shouldPause = { cancelled },
            onProgress = { finished, total, bytes ->
                val allDone = finished >= total && total > 0
                val updated = state.copy(
                    total = total,
                    finished = finished,
                    bytes = bytes,
                    running = !cancelled && !allDone,
                    done = allDone,
                    cancelled = cancelled && !allDone,
                )
                state = updated
                PersistenceManager.saveBulkDownloadState(updated)
            }, onComplete = { finished, total, bytes ->
                val wasPaused = cancelled
                val allDone = finished >= total && total > 0
                val finalState = state.copy(
                    total = total,
                    finished = finished,
                    bytes = bytes,
                    running = false,
                    done = allDone,
                    cancelled = wasPaused && !allDone,
                    finishedAt = System.currentTimeMillis(),
                )
                state = finalState
                PersistenceManager.saveBulkDownloadState(finalState)
                job = null
            }, onJobCreated = { j -> job = j })
    }

    fun pauseDownload() {
        // Cooperative pause: set the flag so shouldCancel() returns true.
        // In-flight downloads finish cleanly (no partial writes orphaned in the
        // disk cache); no new URLs are enqueued. The job ends naturally and
        // onComplete finalizes the persisted state as paused/resumable.
        cancelled = true
        val pausedState = state.copy(
            running = false,
            cancelled = true,
        )
        state = pausedState
        PersistenceManager.saveBulkDownloadState(pausedState)
    }

    fun resetAndDismissCompletion() {
        showCompletionBanner = false
        // Keep the persisted state so a re-open still shows "All images saved
        // offline" until the user taps dismiss; only clear when they dismiss.
        PersistenceManager.clearBulkDownloadState()
        state = PersistenceManager.BulkDownloadState()
        cancelled = false
    }

    if (showCellularConfirm) {
        CellularConfirmDialog(
            onConfirm = {
                showCellularConfirm = false
                beginRun(appContext, scope, state, job, shouldPause = { cancelled },
                    onProgress = { finished, total, bytes ->
                        val allDone = finished >= total && total > 0
                        val updated = state.copy(
                            total = total,
                            finished = finished,
                            bytes = bytes,
                            running = !cancelled && !allDone,
                            done = allDone,
                            cancelled = cancelled && !allDone,
                        )
                        state = updated
                        PersistenceManager.saveBulkDownloadState(updated)
                    },
                    onComplete = { finished, total, bytes ->
                        val wasPaused = cancelled
                        val allDone = finished >= total && total > 0
                        val finalState = state.copy(
                            total = total,
                            finished = finished,
                            bytes = bytes,
                            running = false,
                            done = allDone,
                            cancelled = wasPaused && !allDone,
                            finishedAt = System.currentTimeMillis(),
                        )
                        state = finalState
                        PersistenceManager.saveBulkDownloadState(finalState)
                        job = null
                    },
                    onJobCreated = { j -> job = j },
                )
            },
            onDismiss = { showCellularConfirm = false },
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(if (compact) 14.dp else 16.dp))
            .glowingBorder(
                1.dp,
                Citrine.copy(alpha = if (isRunning) 0.55f else 0.35f),
                RoundedCornerShape(if (compact) 14.dp else 16.dp),
            )
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF1A1812).copy(alpha = 0.75f),
                        Color(0xFF120F08).copy(alpha = 0.55f),
                    )
                )
            )
            .padding(horizontal = if (compact) 16.dp else 18.dp, vertical = if (compact) 14.dp else 18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Header row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(if (compact) 36.dp else 42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Citrine.copy(alpha = 0.18f))
                    .glowingBorder(1.dp, Citrine.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    when {
                        isComplete -> Icons.Filled.CloudDone
                        isRunning -> Icons.Filled.Cloud
                        !isMaxCache -> Icons.Filled.Storage
                        cancelled -> Icons.Filled.PauseCircle
                        else -> Icons.Filled.Download
                    },
                    contentDescription = null,
                    tint = Citrine,
                    modifier = Modifier.size(if (compact) 20.dp else 22.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when {
                        isComplete -> "All images saved offline"
                        isRunning -> "Downloading all images…"
                        cancelled && state.finished > 0 -> "Download paused"
                        !isMaxCache -> "Download all images for offline"
                        else -> "Download all images for offline"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.8f),
                            offset = Offset(0f, 1f),
                            blurRadius = 4f,
                        ),
                    ),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = when {
                        isComplete -> "${formatBytes(state.bytes)} cached · ${state.total} images available offline"
                        isRunning -> "${state.finished} / ${state.total} images · $progressPercent% · ~${formatBytes(state.bytes)} so far"
                        cancelled && state.finished > 0 -> "${state.finished} / ${state.total} images · ${progressPercent}% complete · tap to resume"
                        !isMaxCache -> "Requires Maximum (2 GB) cache — enable it to download ~$ESTIMATED_SIZE_GB GB of images for offline use."
                        state.total > 0 -> "Caches every specimen photo plus all educational/guide illustrations on-device. Estimated size: ~$ESTIMATED_SIZE_GB GB."
                        else -> "Caches every specimen photo, guide illustration, and hero image on-device so the whole app works fully offline. Estimated size: ~$ESTIMATED_SIZE_GB GB."
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.7f),
                            offset = Offset(0f, 1f),
                            blurRadius = 3f,
                        ),
                    ),
                    color = TextMid,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        // Live progress bar (running or partial/resumable)
        if (isRunning || (state.finished > 0 && !isComplete)) {
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Citrine,
                trackColor = Color.White.copy(alpha = 0.12f),
            )
        }

        // Cache-mode gate
        if (!isMaxCache && !isComplete) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Aqua.copy(alpha = 0.10f))
                    .glowingBorder(1.dp, Aqua.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .clickable(onClick = onEnableMaxCache)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Filled.Storage,
                    contentDescription = null,
                    tint = Aqua,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Enable Maximum (2 GB) cache first",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        "The standard 150 MB cache can't hold the full set.",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMid,
                    )
                }
            }
        }

        // Action buttons
        if (isComplete) {
            // Completion banner + dismiss
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF5CC98C),
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    "Every image is now on your device — specimens, guides, geology articles, and hero art. The whole app loads instantly with no signal.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMid,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Medium,
                )
            }
            Spacer(Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = { resetAndDismissCompletion() }) {
                    Text("Dismiss", color = TextLow, fontWeight = FontWeight.Bold)
                }
            }
        } else if (!isMaxCache) {
            // Cache gate blocks the start button
            OutlinedButton(
                onClick = onEnableMaxCache,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Citrine,
                ),
            ) {
                Icon(Icons.Filled.Storage, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Enable 2 GB cache", fontWeight = FontWeight.Bold)
            }
        } else if (isRunning) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = { pauseDownload() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFB347)),
                ) {
                    Icon(Icons.Filled.Pause, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Pause", fontWeight = FontWeight.Bold)
                }
            }
        } else if (cancelled && state.finished > 0 && state.finished < state.total) {
            // Resume
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Button(
                    onClick = { cancelled = false; startDownload() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Citrine),
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp), tint = Ink)
                    Spacer(Modifier.width(8.dp))
                    Text("Resume download", color = Ink, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // Fresh start
            Button(
                onClick = { cancelled = false; startDownload() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Citrine),
            ) {
                Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(18.dp), tint = Ink)
                Spacer(Modifier.width(8.dp))
                Text("Download all images (~$ESTIMATED_SIZE_GB GB)", color = Ink, fontWeight = FontWeight.Bold)
            }
        }

        // WiFi status hint
        if (!isComplete && isMaxCache) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val isWifi = appContext.isWifiConnected()
                val hintColor = if (isWifi) Color(0xFF5CC98C) else Color(0xFFFFB347)
                val hintIcon: ImageVector = if (isWifi) Icons.Filled.Wifi else Icons.Filled.SignalCellularConnectedNoInternet0Bar
                Icon(hintIcon, contentDescription = null, tint = hintColor, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (isWifi)
                        "On WiFi — fastest download."
                    else "On cellular — you'll be asked to confirm before downloading.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextLow,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

/**
 * Cellular confirmation dialog shown before starting a bulk download when the
 * device is not on WiFi.
 */
@Composable
private fun CellularConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Download over cellular?", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Text(
                "You're not on WiFi. Downloading every image (specimens + guides + hero art) uses roughly $ESTIMATED_SIZE_GB GB of mobile data and may take a while. Continue?",
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Citrine),
            ) {
                Text("Download now", color = Ink, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextLow)
            }
        },
    )
}

/**
 * Tiny helper that kicks off a [ImagePrefetcher.bulkPrefetchAll] run on the IO
 * dispatcher and forwards progress to the caller. Centralizes the plumbing so
 * the cellular and WiFi entry points share one path.
 */
private fun beginRun(
    appContext: Context,
    scope: kotlinx.coroutines.CoroutineScope,
    currentState: PersistenceManager.BulkDownloadState,
    currentJob: Job?,
    shouldPause: () -> Boolean,
    onProgress: (finished: Int, total: Int, bytes: Long) -> Unit,
    onComplete: (finished: Int, total: Int, bytes: Long) -> Unit,
    onJobCreated: (Job) -> Unit,
) {
    // Cancel any lingering job (e.g. a paused run whose in-flight downloads
    // haven't finished yet) before starting fresh. The new run filters out
    // URLs already in the disk cache, so nothing is re-downloaded.
    currentJob?.cancel()
    val startedState = currentState.copy(
        running = true,
        cancelled = false,
        done = false,
        startedAt = System.currentTimeMillis(),
    )
    PersistenceManager.saveBulkDownloadState(startedState)

    var lastFinished = 0
    var lastTotal = 0
    var lastBytes = 0L
    val newJob = scope.launch {
        val job = ImagePrefetcher.bulkPrefetchAll(
            context = appContext,
            onProgress = { finished, total, bytes ->
                lastFinished = finished
                lastTotal = total
                lastBytes = bytes
                onProgress(finished, total, bytes)
            },
            // Cooperative pause: when the user taps Pause, shouldPause() returns
            // true. No new URLs are enqueued; in-flight downloads finish cleanly
            // so no partial writes are orphaned in the disk cache.
            shouldCancel = shouldPause,
        )
        job.join()
        withContext(Dispatchers.Main) {
            onComplete(lastFinished, lastTotal, lastBytes)
        }
    }
    onJobCreated(newJob)
}

/** Formats a byte count as a human-readable string (e.g. "24.3 MB" / "1.8 GB"). */
private fun formatBytes(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    return when {
        gb >= 1.0 -> String.format("%.1f GB", gb)
        mb >= 1.0 -> String.format("%.0f MB", mb)
        kb >= 1.0 -> String.format("%.0f KB", kb)
        else -> "$bytes B"
    }
}

/** True if the device is currently connected over WiFi. */
private fun Context.isWifiConnected(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    val network = cm.activeNetwork ?: return false
    val caps = cm.getNetworkCapabilities(network) ?: return false
    return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
}

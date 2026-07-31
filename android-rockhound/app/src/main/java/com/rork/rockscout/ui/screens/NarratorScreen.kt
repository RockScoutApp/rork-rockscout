package com.rork.rockscout.ui.screens

import android.content.Context
import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.NarratorChapter
import com.rork.rockscout.data.NarratorChapters
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.Success
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val PREFS_NAME = "narrator_prefs"
private const val KEY_CHAPTER = "narrator_chapter"
private const val KEY_POSITION_MS = "narrator_position_ms"

/** Format milliseconds as m:ss. */
private fun formatTime(ms: Int): String {
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%d:%02d".format(min, sec)
}

@Composable
fun NarratorScreen(navController: NavController) {
    val context = LocalContext.current
    val chapters = NarratorChapters.chapters
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Restore last chapter + position from SharedPreferences
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    val savedChapter = remember { prefs.getInt(KEY_CHAPTER, 0) }
    val savedPosition = remember { prefs.getInt(KEY_POSITION_MS, 0) }

    var currentChapter by remember { mutableIntStateOf(savedChapter.coerceIn(0, chapters.lastIndex)) }
    var isPlaying by remember { mutableStateOf(false) }
    var positionMs by remember { mutableIntStateOf(0) }
    var durationMs by remember { mutableIntStateOf(0) }
    var isSeeking by remember { mutableStateOf(false) }

    // MediaPlayer — released on dispose
    val mediaPlayer = remember { MediaPlayer() }

    DisposableEffect(Unit) {
        onDispose {
            try {
                if (mediaPlayer.isPlaying) mediaPlayer.stop()
                mediaPlayer.release()
            } catch (_: Exception) {
            }
        }
    }

    // Load chapter into MediaPlayer
    fun loadChapter(index: Int, startPositionMs: Int = 0) {
        try {
            mediaPlayer.reset()
            val resId = context.resources.getIdentifier(
                chapters[index].rawResName, "raw", context.packageName
            )
            if (resId == 0) return
            val afd = context.resources.openRawResourceFd(resId)
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mediaPlayer.setOnPreparedListener { mp ->
                durationMs = mp.duration
                if (startPositionMs > 0 && startPositionMs < mp.duration) {
                    mp.seekTo(startPositionMs)
                    positionMs = startPositionMs
                } else {
                    positionMs = 0
                }
                mp.start()
                isPlaying = true
            }
            mediaPlayer.setOnCompletionListener {
                isPlaying = false
                positionMs = durationMs
                // Auto-queue next chapter
                if (currentChapter < chapters.lastIndex) {
                    currentChapter++
                    loadChapter(currentChapter)
                }
            }
            mediaPlayer.prepareAsync()
        } catch (_: Exception) {
        }
    }

    // Load the saved chapter on first launch
    LaunchedEffect(Unit) {
        loadChapter(currentChapter, savedPosition)
        // Scroll to the current chapter
        listState.animateScrollToItem(currentChapter)
    }

    // Poll position while playing
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            try {
                if (!isSeeking) positionMs = mediaPlayer.currentPosition
            } catch (_: Exception) {
            }
            delay(200)
        }
    }

    // Save position periodically
    LaunchedEffect(positionMs, currentChapter) {
        prefs.edit()
            .putInt(KEY_CHAPTER, currentChapter)
            .putInt(KEY_POSITION_MS, positionMs)
            .apply()
    }

    BackHandler {
        try {
            if (mediaPlayer.isPlaying) mediaPlayer.pause()
            isPlaying = false
        } catch (_: Exception) {
        }
        navController.popBackStack()
    }

    // Calculate total running time (sum of all durations — use actual once loaded, estimate otherwise)
    // Once the first chapter loads, we get real duration. For the rest we estimate.
    val totalEstimatedSec = chapters.size * 35 // rough avg 35s per chapter
    val totalRunningTime = formatTime(totalEstimatedSec * 1000)

    RockBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 16.dp, top = 52.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable {
                            try {
                                if (mediaPlayer.isPlaying) mediaPlayer.pause()
                                isPlaying = false
                            } catch (_: Exception) {
                            }
                            navController.popBackStack()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Narrator",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f),
                )
            }

            // Total running time banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Citrine.copy(alpha = 0.15f), Aqua.copy(alpha = 0.10f))
                        )
                    )
                    .glowingBorder(1.dp, Citrine.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "16 Chapters",
                        style = MaterialTheme.typography.titleSmall,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "Total: ~$totalRunningTime",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextHigh,
                    )
                }
            }

            // Player controls
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.verticalGradient(listOf(Slate800, Slate900)))
                    .glowingBorder(2.dp, Citrine.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                    .padding(16.dp),
            ) {
                Column {
                    // Current chapter title
                    Text(
                        text = "${chapters[currentChapter].index}. ${chapters[currentChapter].title}",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(8.dp))

                    // Seek slider
                    Slider(
                        value = positionMs.toFloat(),
                        onValueChange = { value ->
                            isSeeking = true
                            positionMs = value.toInt()
                        },
                        onValueChangeFinished = {
                            try {
                                mediaPlayer.seekTo(positionMs)
                            } catch (_: Exception) {
                            }
                            isSeeking = false
                        },
                        valueRange = 0f..(durationMs.toFloat().coerceAtLeast(1f)),
                        colors = SliderDefaults.colors(
                            thumbColor = Citrine,
                            activeTrackColor = Citrine,
                            inactiveTrackColor = Citrine.copy(alpha = 0.25f),
                        ),
                    )

                    // Elapsed / remaining time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = formatTime(positionMs),
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTextMid,
                        )
                        Text(
                            text = formatTime(durationMs),
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTextMid,
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Playback controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Previous
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .sculpted(
                                    shape = CircleShape,
                                    accent = Aqua,
                                    shadowElevation = 4.dp,
                                    circular = true,
                                    enabled = currentChapter > 0,
                                    onClick = {
                                        if (currentChapter > 0) {
                                            currentChapter--
                                            loadChapter(currentChapter)
                                        }
                                    },
                                )
                                .clip(CircleShape)
                                .background(Aqua.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Filled.SkipPrevious,
                                contentDescription = "Previous chapter",
                                tint = if (currentChapter > 0) Aqua else DarkTextLow,
                                modifier = Modifier.size(28.dp),
                            )
                        }
                        Spacer(Modifier.width(24.dp))
                        // Play/Pause
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .sculpted(
                                    shape = CircleShape,
                                    accent = Citrine,
                                    shadowElevation = 8.dp,
                                    circular = true,
                                    onClick = {
                                        try {
                                            if (isPlaying) {
                                                mediaPlayer.pause()
                                                isPlaying = false
                                            } else {
                                                if (positionMs >= durationMs && durationMs > 0) {
                                                    loadChapter(currentChapter)
                                                } else {
                                                    mediaPlayer.start()
                                                    isPlaying = true
                                                }
                                            }
                                        } catch (_: Exception) {
                                        }
                                    },
                                )
                                .clip(CircleShape)
                                .background(Citrine),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Ink,
                                modifier = Modifier.size(36.dp),
                            )
                        }
                        Spacer(Modifier.width(24.dp))
                        // Next
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .sculpted(
                                    shape = CircleShape,
                                    accent = Aqua,
                                    shadowElevation = 4.dp,
                                    circular = true,
                                    enabled = currentChapter < chapters.lastIndex,
                                    onClick = {
                                        if (currentChapter < chapters.lastIndex) {
                                            currentChapter++
                                            loadChapter(currentChapter)
                                        }
                                    },
                                )
                                .clip(CircleShape)
                                .background(Aqua.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Filled.SkipNext,
                                contentDescription = "Next chapter",
                                tint = if (currentChapter < chapters.lastIndex) Aqua else DarkTextLow,
                                modifier = Modifier.size(28.dp),
                            )
                        }
                    }
                }
            }

            // Chapter list
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(chapters) { chapter ->
                    ChapterRow(
                        chapter = chapter,
                        isCurrent = chapter.index - 1 == currentChapter,
                        isPlaying = isPlaying && chapter.index - 1 == currentChapter,
                        onClick = {
                            currentChapter = chapter.index - 1
                            loadChapter(currentChapter)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ChapterRow(
    chapter: NarratorChapter,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
) {
    val accent = if (isCurrent) Citrine else Aqua
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isCurrent) Citrine.copy(alpha = 0.12f) else Slate800.copy(alpha = 0.5f)
            )
            .glowingBorder(
                1.dp,
                if (isCurrent) Citrine.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.06f),
                RoundedCornerShape(14.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Chapter number / playing indicator
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                if (isPlaying) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(18.dp),
                    )
                } else {
                    Text(
                        text = "${chapter.index}",
                        style = MaterialTheme.typography.labelMedium,
                        color = accent,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isCurrent) DarkTextHigh else DarkTextMid,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = chapter.scriptPreview,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextLow,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

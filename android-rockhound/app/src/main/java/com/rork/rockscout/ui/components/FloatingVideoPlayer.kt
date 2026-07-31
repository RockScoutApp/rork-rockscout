package com.rork.rockscout.ui.components

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.ui.PlayerView
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextMid
import java.util.Locale
import kotlin.math.roundToInt

/** Full list of supported audio/subtitle languages in the MKV. */
private val SUPPORTED_LANGUAGES: List<Pair<String, String>> = listOf(
    "eng" to "English",
    "spa" to "Español",
    "fra" to "Français",
    "deu" to "Deutsch",
    "por" to "Português",
    "chi" to "中文",
    "ara" to "العربية",
    "hin" to "हिन्दी",
    "ind" to "Bahasa Indonesia",
    "jpn" to "日本語",
    "fil" to "Filipino",
    "vie" to "Tiếng Việt",
    "rus" to "Русский",
    "pol" to "Polski",
    "ita" to "Italiano",
)

/**
 * The floating tutorial video player overlay.
 * Renders on top of the entire app at the activity level.
 * Shows fullscreen or minimized depending on [state].
 */
@Composable
fun FloatingVideoPlayer(
    state: VideoPlayerState,
    modifier: Modifier = Modifier,
) {
    if (!state.isVisible) return

    val context = LocalContext.current
    val player = remember(state.videoUrl) {
        state.clearError()
        val cacheDataSourceFactory = VideoCacheManager.buildCacheDataSourceFactory(context)
        val dataSourceFactory = DefaultDataSource.Factory(context, cacheDataSourceFactory)
        val mediaSourceFactory: MediaSource.Factory = DefaultMediaSourceFactory(dataSourceFactory)
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build().apply {
                setMediaItem(MediaItem.fromUri(state.videoUrl))
                prepare()
                playWhenReady = true
                addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    state.updatePlaying(isPlaying)
                }
                override fun onPlaybackStateChanged(playbackState: Int) {
                    state.updateBuffering(playbackState == Player.STATE_BUFFERING)
                    if (playbackState == Player.STATE_READY) {
                        state.clearError()
                    }
                }
                override fun onPlayerError(error: PlaybackException) {
                    state.updateError(error.localizedMessage ?: "Unable to load the tutorial video.")
                }
                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int,
                ) {
                    state.updatePosition(newPosition.positionMs)
                }
            })
        }
    }

    // Poll position for the progress bar
    LaunchedEffect(player) {
        while (true) {
            if (player.duration > 0) {
                state.updateDuration(player.duration)
                state.updatePosition(player.currentPosition)
            }
            kotlinx.coroutines.delay(500)
        }
    }

    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }

    // Reset drag offset when entering fullscreen or hiding
    LaunchedEffect(state.displayMode) {
        if (state.displayMode != VideoDisplayMode.MINIMIZED) {
            state.resetDragOffset()
        }
    }

    when (state.displayMode) {
        VideoDisplayMode.FULLSCREEN -> FullscreenPlayer(
            player = player,
            state = state,
            onMinimize = { state.minimize() },
            onClose = {
                player.stop()
                player.release()
                state.hide()
            },
            modifier = modifier,
        )
        VideoDisplayMode.MINIMIZED -> MinimizedPlayer(
            player = player,
            state = state,
            onMaximize = { state.maximize() },
            onClose = {
                player.stop()
                player.release()
                state.hide()
            },
            modifier = modifier,
        )
        VideoDisplayMode.HIDDEN -> {}
    }
}

@Composable
private fun FullscreenPlayer(
    player: ExoPlayer,
    state: VideoPlayerState,
    onMinimize: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var showControls by remember { mutableStateOf(true) }
    var audioMenuExpanded by remember { mutableStateOf(false) }
    var subtitleMenuExpanded by remember { mutableStateOf(false) }
    var currentAudioLangIndex by remember { mutableIntStateOf(0) }
    var currentSubtitleLangIndex by remember { mutableIntStateOf(0) }

    // Discover available audio and text tracks from the player
    val audioTrackGroups = remember(player) {
        val groups = mutableListOf<Int>()
        player.currentTracks.groups.forEachIndexed { index, group ->
            if (group.type == androidx.media3.common.C.TRACK_TYPE_AUDIO) {
                groups.add(index)
            }
        }
        groups
    }
    val textTrackGroups = remember(player) {
        val groups = mutableListOf<Int>()
        player.currentTracks.groups.forEachIndexed { index, group ->
            if (group.type == androidx.media3.common.C.TRACK_TYPE_TEXT) {
                groups.add(index)
            }
        }
        groups
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        // Video surface
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false
                    setShutterBackgroundColor(android.graphics.Color.BLACK)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .clickable { showControls = !showControls },
        )

        // Buffering spinner
        if (state.isBuffering && state.errorMessage == null) {
            CircularProgressIndicator(
                color = Citrine,
                strokeWidth = 3.dp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp),
            )
        }

        // Error overlay with retry button
        if (state.errorMessage != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = state.errorMessage!!,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    onClick = {
                        state.clearError()
                        player.prepare()
                        player.playWhenReady = true
                    },
                    color = Citrine,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = "Retry",
                        color = Slate900,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                    )
                }
            }
        }

        // Controls overlay
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Black.copy(alpha = 0.6f),
                            0.15f to Color.Transparent,
                            0.85f to Color.Transparent,
                            1f to Color.Black.copy(alpha = 0.8f),
                        ),
                    ),
            ) {
                // Top bar: close, title, minimize, audio, subtitle, download
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Close",
                            tint = Color.White,
                        )
                    }
                    Text(
                        text = "RockScout Tutorial",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Row {
                        // Audio language dropdown
                        Box {
                            IconButton(onClick = { audioMenuExpanded = true }) {
                                Icon(
                                    Icons.Filled.Audiotrack,
                                    contentDescription = "Audio Language",
                                    tint = if (currentAudioLangIndex > 0) Citrine else Color.White,
                                )
                            }
                            DropdownMenu(
                                expanded = audioMenuExpanded,
                                onDismissRequest = { audioMenuExpanded = false },
                            ) {
                                SUPPORTED_LANGUAGES.forEachIndexed { index, (code, name) ->
                                    DropdownMenuItem(
                                        text = { Text(name) },
                                        onClick = {
                                            currentAudioLangIndex = index
                                            audioMenuExpanded = false
                                            setAudioTrack(player, index)
                                        },
                                        leadingIcon = if (index == currentAudioLangIndex) {
                                            { Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Citrine) }
                                        } else null,
                                    )
                                }
                            }
                        }
                        // Subtitle dropdown
                        Box {
                            IconButton(onClick = { subtitleMenuExpanded = true }) {
                                Icon(
                                    Icons.Filled.ClosedCaption,
                                    contentDescription = "Subtitles",
                                    tint = if (currentSubtitleLangIndex >= 0) Citrine else Color.White,
                                )
                            }
                            DropdownMenu(
                                expanded = subtitleMenuExpanded,
                                onDismissRequest = { subtitleMenuExpanded = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Off") },
                                    onClick = {
                                        currentSubtitleLangIndex = -1
                                        subtitleMenuExpanded = false
                                        setSubtitleTrack(player, -1)
                                    },
                                    leadingIcon = if (currentSubtitleLangIndex == -1) {
                                        { Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Citrine) }
                                    } else null,
                                )
                                SUPPORTED_LANGUAGES.forEachIndexed { index, (_, name) ->
                                    DropdownMenuItem(
                                        text = { Text(name) },
                                        onClick = {
                                            currentSubtitleLangIndex = index
                                            subtitleMenuExpanded = false
                                            setSubtitleTrack(player, index)
                                        },
                                        leadingIcon = if (index == currentSubtitleLangIndex) {
                                            { Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Citrine) }
                                        } else null,
                                    )
                                }
                            }
                        }
                        // Download
                        IconButton(onClick = {
                            downloadVideo(context, state.videoUrl)
                        }) {
                            Icon(
                                Icons.Filled.Download,
                                contentDescription = "Download",
                                tint = Color.White,
                            )
                        }
                        // Minimize
                        IconButton(onClick = onMinimize) {
                            Icon(
                                Icons.Filled.PictureInPicture,
                                contentDescription = "Minimize",
                                tint = Color.White,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Current chapter label
                val currentChapter = remember(state.positionMs) {
                    TUTORIAL_CHAPTERS.lastOrNull { state.positionMs >= it.startTimeMs }
                }
                currentChapter?.let { chapter ->
                    Text(
                        text = chapter.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Citrine,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        textAlign = TextAlign.Center,
                    )
                }

                // Chapter progress bar
                ChapterProgressBar(
                    positionMs = state.positionMs,
                    durationMs = state.durationMs,
                    chapters = TUTORIAL_CHAPTERS,
                    onSeek = { ms -> player.seekTo(ms) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )

                // Playback controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = {
                        val prevChapter = TUTORIAL_CHAPTERS.lastOrNull {
                            it.startTimeMs < state.positionMs - 2000
                        }
                        player.seekTo(prevChapter?.startTimeMs ?: 0L)
                    }) {
                        Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous Chapter", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = {
                        if (player.isPlaying) player.pause() else player.play()
                    }) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Citrine)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                if (player.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Slate900,
                                modifier = Modifier.size(28.dp),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = {
                        val nextChapter = TUTORIAL_CHAPTERS.firstOrNull {
                            it.startTimeMs > state.positionMs + 2000
                        }
                        if (nextChapter != null) player.seekTo(nextChapter.startTimeMs)
                    }) {
                        Icon(Icons.Filled.SkipNext, contentDescription = "Next Chapter", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MinimizedPlayer(
    player: ExoPlayer,
    state: VideoPlayerState,
    onMaximize: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Track accumulated drag within the current gesture (added to the
    // persisted offset from VideoPlayerState). On release the persisted
    // offset is updated so the position survives recomposition.
    var gestureOffsetX by remember { mutableFloatStateOf(0f) }
    var gestureOffsetY by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val totalOffsetX = state.dragOffsetX + gestureOffsetX
    val totalOffsetY = state.dragOffsetY + gestureOffsetY

    Box(
        modifier = modifier
            .padding(8.dp)
            .offset { IntOffset(totalOffsetX.roundToInt(), totalOffsetY.roundToInt()) }
            .size(width = 160.dp, height = 284.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
            .border(
                1.dp,
                if (isDragging) Citrine else Citrine.copy(alpha = 0.3f),
                RoundedCornerShape(12.dp),
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onMaximize() },
                )
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        isDragging = true
                        gestureOffsetX = 0f
                        gestureOffsetY = 0f
                    },
                    onDragEnd = {
                        isDragging = false
                        state.updateDragOffset(totalOffsetX, totalOffsetY)
                        gestureOffsetX = 0f
                        gestureOffsetY = 0f
                    },
                    onDragCancel = {
                        isDragging = false
                        gestureOffsetX = 0f
                        gestureOffsetY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        gestureOffsetX += dragAmount.x
                        gestureOffsetY += dragAmount.y
                    },
                )
            },
    ) {
        // Video surface
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false
                    setShutterBackgroundColor(android.graphics.Color.BLACK)
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        // Buffering spinner (minimized)
        if (state.isBuffering && state.errorMessage == null) {
            CircularProgressIndicator(
                color = Citrine,
                strokeWidth = 2.dp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp),
            )
        }

        // Error overlay (minimized)
        if (state.errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Tap to retry",
                    color = Color.White,
                    fontSize = 11.sp,
                    modifier = Modifier.clickable {
                        state.clearError()
                        player.prepare()
                        player.playWhenReady = true
                    },
                )
            }
        }

        // Drag hint when not dragging
        if (!isDragging) {
            Text(
                text = "Hold to drag",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                ),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp),
            )
        }

        // Control buttons overlay at bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    if (player.isPlaying) player.pause() else player.play()
                },
                modifier = Modifier.size(28.dp),
            ) {
                Icon(
                    if (player.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
            IconButton(
                onClick = onMaximize,
                modifier = Modifier.size(28.dp),
            ) {
                Icon(
                    Icons.Filled.Fullscreen,
                    contentDescription = "Maximize",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp),
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

/**
 * Custom progress bar with clickable chapter tick marks.
 */
@Composable
private fun ChapterProgressBar(
    positionMs: Long,
    durationMs: Long,
    chapters: List<TutorialChapter>,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragValue by remember { mutableFloatStateOf(0f) }

    val progress = if (durationMs > 0) {
        if (isDragging) dragValue else (positionMs.toFloat() / durationMs.toFloat())
    } else 0f

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
        ) {
            Slider(
                value = progress,
                onValueChange = { value ->
                    isDragging = true
                    dragValue = value
                },
                onValueChangeFinished = {
                    isDragging = false
                    val targetMs = (dragValue * durationMs).toLong()
                    onSeek(targetMs)
                },
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth(),
            )

            // Chapter tick marks
            if (durationMs > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    chapters.forEach { chapter ->
                        val fraction = chapter.startTimeMs.toFloat() / durationMs.toFloat()
                        Box(
                            modifier = Modifier
                                .padding(start = (fraction * 360).dp)
                                .size(width = 2.dp, height = 12.dp)
                                .background(Citrine.copy(alpha = 0.7f)),
                        )
                    }
                }
            }
        }
        // Time display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = formatTime(positionMs),
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f)),
            )
            Text(
                text = formatTime(durationMs),
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f)),
            )
        }
    }
}

/** Select an audio track by language index using preferred language codes. */
private fun setAudioTrack(player: ExoPlayer, langIndex: Int) {
    val langCodes = listOf("eng", "spa", "fra", "deu", "por", "cmn", "ara", "hin", "ind", "jpn", "fil", "vie", "rus", "pol", "ita")
    if (langIndex < langCodes.size) {
        player.trackSelectionParameters = player.trackSelectionParameters
            .buildUpon()
            .setPreferredAudioLanguage(langCodes[langIndex])
            .build()
    }
}

/** Select a subtitle track by language index, or -1 to disable. */
private fun setSubtitleTrack(player: ExoPlayer, langIndex: Int) {
    val langCodes = listOf("eng", "spa", "fra", "deu", "por", "cmn", "ara", "hin", "ind", "jpn", "fil", "vie", "rus", "pol", "ita")
    val builder = player.trackSelectionParameters.buildUpon()
    if (langIndex < 0 || langIndex >= langCodes.size) {
        builder.setTrackTypeDisabled(
            androidx.media3.common.C.TRACK_TYPE_TEXT,
            true,
        )
    } else {
        builder.setTrackTypeDisabled(
            androidx.media3.common.C.TRACK_TYPE_TEXT,
            false,
        )
        builder.setPreferredTextLanguage(langCodes[langIndex])
        builder.setPreferredTextRoleFlags(androidx.media3.common.C.ROLE_FLAG_SUBTITLE)
    }
    player.trackSelectionParameters = builder.build()
}

/** Download the tutorial video via DownloadManager. */
private fun downloadVideo(context: Context, url: String) {
    try {
        val request = android.app.DownloadManager.Request(Uri.parse(url))
        request.setTitle("RockScout Tutorial Video")
        request.setDescription("Downloading tutorial video with all language tracks")
        request.setDestinationInExternalPublicDir(
            android.os.Environment.DIRECTORY_DOWNLOADS,
            "RockScout_Tutorial.mkv",
        )
        request.setNotificationVisibility(
            android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED,
        )
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as android.app.DownloadManager
        dm.enqueue(request)
        Toast.makeText(context, "Downloading tutorial video...", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

/** Format milliseconds as M:SS. */
private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

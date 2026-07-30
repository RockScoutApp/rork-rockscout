package com.rork.rockscout.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

/** Display mode for the floating tutorial video player. */
enum class VideoDisplayMode {
    HIDDEN,
    FULLSCREEN,
    MINIMIZED,
}

/** Chapter marker for the tutorial video progress bar. */
data class TutorialChapter(
    val title: String,
    val startTimeMs: Long,
)

/** Activity-scoped state for the tutorial video player overlay. */
class VideoPlayerState {
    var displayMode by mutableStateOf(VideoDisplayMode.HIDDEN)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var positionMs by mutableLongStateOf(0L)
        private set

    var durationMs by mutableLongStateOf(0L)
        private set

    /** X offset (px) of the minimized player from its default bottom-end position. */
    var dragOffsetX by mutableFloatStateOf(0f)
        private set

    /** Y offset (px) of the minimized player from its default bottom-end position. */
    var dragOffsetY by mutableFloatStateOf(0f)
        private set

    /** The URL will be set once the MKV is uploaded. */
    var videoUrl: String = "https://litter.catbox.moe/k6t1s8.mkv"

    fun showFullscreen() {
        displayMode = VideoDisplayMode.FULLSCREEN
    }

    fun minimize() {
        if (displayMode == VideoDisplayMode.FULLSCREEN) {
            displayMode = VideoDisplayMode.MINIMIZED
        }
    }

    fun maximize() {
        if (displayMode == VideoDisplayMode.MINIMIZED) {
            displayMode = VideoDisplayMode.FULLSCREEN
        }
    }

    fun hide() {
        displayMode = VideoDisplayMode.HIDDEN
        isPlaying = false
        positionMs = 0L
    }

    fun updatePlaying(playing: Boolean) {
        isPlaying = playing
    }

    fun updatePosition(ms: Long) {
        positionMs = ms
    }

    fun updateDuration(ms: Long) {
        durationMs = ms
    }

    fun updateDragOffset(x: Float, y: Float) {
        dragOffsetX = x
        dragOffsetY = y
    }

    fun resetDragOffset() {
        dragOffsetX = 0f
        dragOffsetY = 0f
    }

    /** Whether the player overlay should be visible at all. */
    val isVisible: Boolean
        get() = displayMode != VideoDisplayMode.HIDDEN

    /** Whether the player is actively playing video (for PiP decisions). */
    val isVideoActive: Boolean
        get() = isVisible && isPlaying
}

/** CompositionLocal providing the activity-scoped VideoPlayerState to all composables. */
val LocalVideoPlayerState = androidx.compose.runtime.staticCompositionLocalOf<VideoPlayerState?> {
    null
}

/** Chapter timestamps for the tutorial video.
 * Updated after ffmpeg compositing determines actual segment durations. */
val TUTORIAL_CHAPTERS: List<TutorialChapter> = listOf(
    TutorialChapter("Welcome", 0L),
    TutorialChapter("AI Rock ID", 32_862L),
    TutorialChapter("Your Collection", 167_053L),
    TutorialChapter("Field Tools", 235_912L),
    TutorialChapter("Dig Sites & Gem Shows", 316_343L),
    TutorialChapter("Trip Planning", 366_994L),
    TutorialChapter("Trading & Community", 464_979L),
    TutorialChapter("Social", 546_194L),
    TutorialChapter("Aurora & Night Sky", 613_250L),
    TutorialChapter("Your Profile", 695_301L),
    TutorialChapter("Reference Library", 745_299L),
    TutorialChapter("Artifacts & Wonders", 808_437L),
    TutorialChapter("Field Kit", 871_444L),
    TutorialChapter("Learn & Explore", 969_612L),
    TutorialChapter("Premium & Free Tier", 1_075_434L),
    TutorialChapter("Outro", 1_187_865L),
)

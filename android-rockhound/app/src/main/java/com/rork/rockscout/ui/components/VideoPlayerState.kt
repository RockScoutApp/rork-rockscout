package com.rork.rockscout.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

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

    /** The URL will be set once the MKV is uploaded. */
    var videoUrl: String = "https://litter.catbox.moe/s33dfo.mkv"

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
    TutorialChapter("AI Rock ID", 27_611L),
    TutorialChapter("Your Collection", 137_978L),
    TutorialChapter("Field Tools", 193_044L),
    TutorialChapter("Dig Sites & Gem Shows", 257_175L),
    TutorialChapter("Trip Planning", 296_359L),
    TutorialChapter("Trading & Community", 378_122L),
    TutorialChapter("Social", 442_383L),
    TutorialChapter("Aurora & Night Sky", 497_501L),
    TutorialChapter("Your Profile", 568_685L),
    TutorialChapter("Reference Library", 609_253L),
    TutorialChapter("Artifacts & Wonders", 660_897L),
    TutorialChapter("Field Kit", 715_049L),
    TutorialChapter("Learn & Explore", 794_592L),
    TutorialChapter("Premium & Free Tier", 880_352L),
    TutorialChapter("Outro", 971_598L),
)

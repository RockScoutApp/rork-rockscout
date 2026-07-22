package com.rork.rockscout.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rork.rockscout.data.NightModeManager

/**
 * Full-screen red overlay for night UV collecting. When [NightModeManager] is
 * enabled, a deep red translucent layer covers the entire app to preserve
 * dark-adapted vision. Taps pass through to the UI beneath.
 *
 * The overlay sits at the top of the z-stack in the root Box so it covers
 * every screen, sheet, and dialog.
 */
@Composable
fun NightModeOverlay() {
    val enabled by NightModeManager.enabled.collectAsStateWithLifecycle()
    AnimatedVisibility(
        visible = enabled,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x8C000000)),
        )
    }
}

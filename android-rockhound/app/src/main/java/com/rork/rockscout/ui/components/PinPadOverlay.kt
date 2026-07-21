package com.rork.rockscout.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink

/**
 * Fullscreen PIN pad overlay revealed after 5 taps on the version text.
 *
 * Enter [correctPin] (6 digits). On correct entry [onUnlock] is called.
 * On wrong entry the dots shake and clear. [onDismiss] cancels the overlay.
 */
@Composable
fun PinPadOverlay(
    correctPin: String,
    onUnlock: () -> Unit,
    onDismiss: () -> Unit,
) {
    var entered by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    fun press(digit: String) {
        if (entered.length >= 6) return
        val next = entered + digit
        entered = next
        error = false
        if (next.length == 6) {
            if (next == correctPin) {
                onUnlock()
            } else {
                error = true
                // Clear after brief shake
                entered = ""
            }
        }
    }

    fun backspace() {
        if (entered.isNotEmpty()) entered = entered.dropLast(1)
        error = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.92f))
            .clickable(
                enabled = false,
                onClick = {},
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp),
        ) {
            // Close button row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .sculpted(
                            shape = CircleShape,
                            accent = Citrine,
                            shadowElevation = 4.dp,
                            circular = true,
                            onClick = { onDismiss() },
                        )
                        .clip(CircleShape)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Lock icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(Citrine.copy(alpha = 0.30f), Citrine.copy(alpha = 0.08f))
                        )
                    )
                    .glowingBorder(2.dp, Citrine.copy(alpha = 0.55f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = null,
                    tint = Citrine,
                    modifier = Modifier.size(28.dp),
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "Developer Access",
                style = MaterialTheme.typography.titleLarge,
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Enter PIN",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
            )

            Spacer(Modifier.height(24.dp))

            // PIN dots
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                for (i in 0 until 6) {
                    val filled = i < entered.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (filled) {
                                    if (error) Color(0xFFE2574C) else Citrine
                                } else {
                                    Color.White.copy(alpha = 0.15f)
                                }
                            )
                            .glowingBorder(
                                1.dp,
                                if (error) Color(0xFFE2574C) else Citrine.copy(alpha = 0.5f),
                                CircleShape,
                            ),
                    )
                }
            }

            AnimatedVisibility(visible = error, enter = fadeIn(), exit = fadeOut()) {
                Text(
                    text = "Incorrect PIN",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFE2574C),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }

            Spacer(Modifier.height(32.dp))

            // Number pad (3x4 grid)
            val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "⌫")
            keys.chunked(3).forEach { rowKeys ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    rowKeys.forEach { key ->
                        when (key) {
                            "" -> Box(modifier = Modifier.size(72.dp))
                            "⌫" -> Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .sculpted(
                                        shape = CircleShape,
                                        accent = Citrine,
                                        shadowElevation = 4.dp,
                                        circular = true,
                                        onClick = { backspace() },
                                    )
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .glowingBorder(2.dp, Color.White.copy(alpha = 0.18f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    Icons.Filled.Backspace,
                                    contentDescription = "Backspace",
                                    tint = DarkTextMid,
                                    modifier = Modifier.size(26.dp),
                                )
                            }
                            else -> Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .sculpted(
                                        shape = CircleShape,
                                        accent = Citrine,
                                        shadowElevation = 5.dp,
                                        circular = true,
                                        onClick = { press(key) },
                                    )
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.10f))
                                    .glowingBorder(2.dp, Color.White.copy(alpha = 0.22f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = DarkTextHigh,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

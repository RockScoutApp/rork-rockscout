package com.rork.rockscout.ui.components

import android.app.Activity
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rork.rockscout.data.PurchaseManager
import com.rork.rockscout.data.ReviewManager
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.Ink
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * "Review Us" card displayed at the bottom of the home screen, below the
 * donation card. Always visible regardless of premium status. Tapping it
 * triggers the Google Play in-app review flow (with Play Store fallback).
 */
@Composable
fun ReviewUsCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "reviewGlow")
    val glow by transition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.40f,
        animationSpec = infiniteRepeatable(tween(2600), RepeatMode.Reverse),
        label = "reviewGlowAlpha",
    )
    val accent = Citrine
    Box(
        modifier = modifier
            .fillMaxWidth()
            .sculpted(shape = RoundedCornerShape(20.dp), accent = accent, shadowElevation = 6.dp, onClick = onClick)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .glowingBorder(3.dp, accent.copy(alpha = 0.55f), RoundedCornerShape(20.dp)),
    ) {
        // Glow overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(100.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(accent.copy(alpha = 0.20f + glow), accent.copy(alpha = 0.03f), Color.Transparent)
                    )
                ),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Star icon in a glowing circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(accent.copy(alpha = 0.35f), accent.copy(alpha = 0.08f))
                        )
                    )
                    .glowingBorder(2.dp, accent.copy(alpha = 0.60f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.StarRate,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(28.dp),
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Review Us on Google Play",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Enjoying RockScout? Tap here to leave a quick review — it really helps!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFC9C2B0),
                )
            }
        }
    }
}

/**
 * Random in-app review popup dialog. Shows a friendly prompt asking the user
 * to rate the app. If they tap "Rate Now", the Google Play in-app review flow
 * is triggered. If they tap "Maybe Later", the popup is dismissed and the
 * cooldown timer is reset.
 *
 * This popup is only shown to non-premium users (callers must check
 * [PurchaseManager.isPremium] before setting [show] to true).
 */
@Composable
fun ReviewPopupDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onRateNow: () -> Unit,
) {
    if (!show) return

    val transition = rememberInfiniteTransition(label = "popupGlow")
    val glow by transition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.50f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "popupGlowAlpha",
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                        )
                    )
                    .glowingBorder(4.dp, Citrine.copy(alpha = 0.50f + glow), RoundedCornerShape(28.dp)),
            ) {
                // Glow overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .height(140.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Citrine.copy(alpha = 0.20f + glow), Citrine.copy(alpha = 0.02f), Color.Transparent)
                            )
                        ),
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .sculpted(
                                    shape = CircleShape,
                                    accent = Citrine,
                                    shadowElevation = 3.dp,
                                    circular = true,
                                    onClick = onDismiss,
                                )
                                .clip(CircleShape)
                                .background(Color.Black),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }

                    // Stars row
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(5) { i ->
                            val starAlpha by transition.animateFloat(
                                initialValue = 0.35f + i * 0.05f,
                                targetValue = 0.80f + i * 0.04f,
                                animationSpec = infiniteRepeatable(
                                    tween(1800 + i * 200),
                                    RepeatMode.Reverse,
                                ),
                                label = "starGlow_$i",
                            )
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = Citrine.copy(alpha = starAlpha),
                                modifier = Modifier.size(36.dp),
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = "Enjoying RockScout?",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Your review helps other rockhounders discover the app and keeps us motivated to add more specimens!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFC9C2B0),
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(24.dp))

                    // Rate Now button
                    SculptedButton(
                        text = "Rate Now",
                        onClick = onRateNow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        accent = Citrine,
                        containerColor = Citrine,
                        textColor = Ink,
                        icon = Icons.Filled.Star,
                        shape = RoundedCornerShape(14.dp),
                    )

                    Spacer(Modifier.height(10.dp))

                    // Maybe Later button
                    SculptedOutlinedButton(
                        text = "Maybe Later",
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        accent = Aqua,
                        textColor = Color(0xFFC9C2B0),
                        shape = RoundedCornerShape(14.dp),
                    )
                }
            }
        }
    }
}

/**
 * Random review popup controller. Manages the logic for randomly showing
 * the review prompt during app usage. Only triggers for non-premium users
 * and respects a cooldown period.
 *
 * @param isPremium whether the user has purchased ad-free (premium).
 * @param sessionKey a unique key for the current screen/session so the popup
 *   doesn't trigger multiple times in the same view.
 */
@Composable
fun RandomReviewPopup(
    isPremium: Boolean,
    sessionKey: String,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showPopup by remember { mutableStateOf(false) }
    val reviewManager = remember { ReviewManager(context) }

    LaunchedEffect(isPremium, sessionKey) {
        if (isPremium) return@LaunchedEffect
        if (!reviewManager.shouldShowRandomPrompt()) return@LaunchedEffect

        // Random delay between 15-45 seconds of screen usage
        val delayMs = (15_000L..45_000L).random()
        delay(delayMs)

        if (!showPopup) {
            showPopup = true
        }
    }

    ReviewPopupDialog(
        show = showPopup,
        onDismiss = {
            showPopup = false
            reviewManager.markRandomPromptShown()
        },
        onRateNow = {
            showPopup = false
            val activity = context as? Activity
            if (activity != null) {
                scope.launch {
                    reviewManager.requestReview(activity)
                }
            }
        },
    )
}

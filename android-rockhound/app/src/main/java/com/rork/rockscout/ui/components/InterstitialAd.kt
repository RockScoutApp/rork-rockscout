package com.rork.rockscout.ui.components

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
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rork.rockscout.data.AdAnalyticsTracker
import com.rork.rockscout.data.AdManager
import com.rork.rockscout.data.IdentifyAccessManager
import com.rork.rockscout.data.PurchaseManager
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine

/** Simulated interstitial ad content cycled for variety. */
private data class AdContent(
    val headline: String,
    val body: String,
    val cta: String,
    val accent: Color,
)

private val AD_ROTATIONS = listOf(
    AdContent(
        headline = "RockScout Premium",
        body = "Go Premium to remove all ads, unlock unlimited identifications, and support development.",
        cta = "Go Premium · $9.99/mo",
        accent = Amethyst,
    ),
    AdContent(
        headline = "Unlimited Identifies",
        body = "Subscribe to Premium for unlimited AI identifies, ad-free, and live location alerts.",
        cta = "Subscribe Now",
        accent = Citrine,
    ),
    AdContent(
        headline = "Support RockScout",
        body = "RockScout is built by a rockhounder, for rockhounders. Go premium and help us grow the database.",
        cta = "Become a Supporter",
        accent = Aqua,
    ),
)

/**
 * A closeable interstitial popup ad shown to non-premium users when they
 * navigate into key screens. Respects a per-session cooldown and global
 * minimum interval so it's not too aggressive, and never appears for
 * premium users.
 *
 * Place at the top level of a screen composable — it self-manages visibility.
 */
@Composable
fun InterstitialAdTrigger(
    screenKey: String,
    onGoPremium: () -> Unit,
) {
    val purchaseManager = PurchaseManager.instance
    val isPremium by purchaseManager.isPremium.collectAsState()
    val accessManager = IdentifyAccessManager.instance
    val hasAdFreeUnlock by accessManager.hasAdFreeUnlock.collectAsState()
    val analyticsState by AdAnalyticsTracker.state.collectAsState()
    val showAds = !isPremium && !hasAdFreeUnlock && analyticsState.adsEnabled

    var visible by remember { mutableStateOf(false) }
    var ad by remember { mutableStateOf(AD_ROTATIONS[0]) }
    val context = androidx.compose.ui.platform.LocalContext.current

    // Evaluate on entry — only show if ads aren't suppressed, not dismissed, and cooldown elapsed
    LaunchedEffect(screenKey, showAds) {
        if (showAds && AdManager.instance.shouldShowAd(screenKey)) {
            ad = AD_ROTATIONS[AdManager.instance.rotationIndex % AD_ROTATIONS.size]
            AdManager.instance.markShown(screenKey)
            // Small delay so the screen renders before the popup appears
            kotlinx.coroutines.delay(400L)
            visible = true
            AdAnalyticsTracker.recordInterstitialImpression(context)
        }
    }

    if (!visible) return

    val transition = rememberInfiniteTransition(label = "interstitialGlow")
    val glow by transition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.22f,
        animationSpec = infiniteRepeatable(tween(2800), RepeatMode.Reverse),
        label = "interstitialGlowAlpha",
    )

    val dismiss = {
        AdManager.instance.dismissAd(screenKey)
        visible = false
    }

    Dialog(
        onDismissRequest = dismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(onClick = dismiss, enabled = false),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                        )
                    )
                    .glowingBorder(4.dp, ad.accent.copy(alpha = 0.55f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center,
            ) {
                // Accent glow overlay at top
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .height(120.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    ad.accent.copy(alpha = 0.20f + glow),
                                    ad.accent.copy(alpha = 0.05f),
                                    Color.Transparent,
                                )
                            )
                        ),
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Close button row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .sculpted(
                                    shape = CircleShape,
                                    accent = Citrine,
                                    shadowElevation = 3.dp,
                                    circular = true,
                                    onClick = { dismiss() },
                                )
                                .clip(CircleShape)
                                .background(Color.Black),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Close ad",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // Sponsored label
                    Text(
                        text = "Sponsored",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium,
                    )

                    Spacer(Modifier.height(16.dp))

                    // Icon box
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.radialGradient(
                                    listOf(ad.accent.copy(alpha = 0.35f), ad.accent.copy(alpha = 0.08f))
                                )
                            )
                            .glowingBorder(3.dp, ad.accent.copy(alpha = 0.60f), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Diamond,
                            contentDescription = null,
                            tint = ad.accent,
                            modifier = Modifier.size(36.dp),
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = ad.headline,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = ad.body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFC8C2B0),
                        textAlign = TextAlign.Center,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                    )

                    Spacer(Modifier.height(22.dp))

                    // CTA button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(ad.accent, ad.accent.copy(alpha = 0.80f))
                                )
                            )
                            .clickable {
                                dismiss()
                                AdAnalyticsTracker.recordInterstitialClick(context)
                                onGoPremium()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = ad.cta,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Secondary dismiss
                    Text(
                        text = "No thanks, continue with ads",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.65f),
                        modifier = Modifier
                            .clickable { dismiss() }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }
        }
    }
}

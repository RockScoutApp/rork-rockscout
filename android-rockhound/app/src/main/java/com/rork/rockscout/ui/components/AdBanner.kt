package com.rork.rockscout.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.rockscout.data.AdAnalyticsTracker
import com.rork.rockscout.data.IdentifyAccessManager
import com.rork.rockscout.data.PurchaseManager
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Citrine

/**
 * Ad banner shown to non-premium users.
 *
 * Tries to load a real Google AdMob banner (test unit ID) when ads are
 * enabled via [AdAnalyticsTracker]. Falls back to the simulated upgrade
 * promo card when real ads can't load (cloud emulator / no network) or
 * when the Developer Console has ads disabled.
 */
@Composable
fun AdBanner(
    onRemoveAds: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val purchaseManager = PurchaseManager.instance
    val isPremium by purchaseManager.effectiveIsPremium.collectAsState()
    val accessManager = IdentifyAccessManager.instance
    val hasAdFreeUnlock by accessManager.hasAdFreeUnlock.collectAsState()
    val analyticsState by AdAnalyticsTracker.state.collectAsState()
    val showAds = !isPremium && !hasAdFreeUnlock && analyticsState.adsEnabled

    val transition = rememberInfiniteTransition(label = "adGlow")
    val glow by transition.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(tween(2600), RepeatMode.Reverse),
        label = "adGlowAlpha",
    )

    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(showAds) {
        if (showAds) {
            AdAnalyticsTracker.recordBannerImpression(context)
        }
    }

    AnimatedVisibility(
        visible = showAds,
        enter = fadeIn(tween(300)) + slideInVertically(tween(300)),
        exit = fadeOut(tween(200)) + slideOutVertically(tween(200)),
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .sculpted(shape = RoundedCornerShape(20.dp), accent = Amethyst, shadowElevation = 6.dp, onClick = {
                    AdAnalyticsTracker.recordBannerClick(context)
                    onRemoveAds()
                })
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                    )
                )
                .glowingBorder(3.dp, Amethyst.copy(alpha = 0.55f), RoundedCornerShape(20.dp)),
        ) {
            // Accent glow overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .height(50.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Amethyst.copy(alpha = 0.22f + glow), Amethyst.copy(alpha = 0.04f), Color.Transparent)
                        )
                    ),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.radialGradient(
                                    listOf(Amethyst.copy(alpha = 0.35f), Amethyst.copy(alpha = 0.10f))
                                )
                            )
                            .glowingBorder(2.dp, Amethyst.copy(alpha = 0.60f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Diamond,
                            contentDescription = "Go Premium",
                            tint = Amethyst,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Ad-Supported",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Go Premium — $9.99/mo",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFC8C2B0),
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Diamond,
                        contentDescription = null,
                        tint = Citrine,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Go Premium",
                        style = MaterialTheme.typography.labelLarge,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

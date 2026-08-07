package com.rork.rockscout.ui.screens

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import android.app.Activity
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.IdentifyAccessManager
import com.rork.rockscout.data.IapConfig
import com.rork.rockscout.data.PurchaseManager
import com.rork.rockscout.data.PurchaseResult
import com.rork.rockscout.ui.components.CelebrationLevel
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.ThankYouCelebration
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.CitrineDeep
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.launch

/**
 * Explains the identifier-token system. Free and donated unlocks cover the rest of the app
 * (location monitoring, ad-free access, etc.); identifications still consume tokens unless
 * the user is a Premium subscriber. Provides a donation card with a working dropdown so
 * users can purchase more tokens directly. Reached by tapping the token bank icon on the Home header.
 */
@Composable
fun TokenInfoScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val accessManager = IdentifyAccessManager.instance
    val purchaseManager = PurchaseManager.instance

    val tokenBalance by accessManager.tokenBalance.collectAsStateWithLifecycle()
    val trialActive by accessManager.trialActive.collectAsStateWithLifecycle()
    val trialUsesRemaining by accessManager.trialUsesRemaining.collectAsStateWithLifecycle()
    val trialExpired by accessManager.trialExpired.collectAsStateWithLifecycle()
    val hasLocationUnlock by accessManager.hasLocationUnlock.collectAsStateWithLifecycle()
    val hasAdFreeUnlock by accessManager.hasAdFreeUnlock.collectAsStateWithLifecycle()
    val isPremium by purchaseManager.effectiveIsPremium.collectAsStateWithLifecycle()
    val isPurchasing by purchaseManager.isPurchasing.collectAsStateWithLifecycle()

    var celebrationLevel by remember { mutableStateOf<CelebrationLevel?>(null) }

    RockBackground {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp, top = 56.dp, bottom = 40.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Top bar with back button
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SculptedIconButton(icon = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", onClick = { navController.popBackStack() }, accent = Citrine, iconTint = TextMid, size = 44.dp, shadowElevation = 3.dp)
                    Text(
                        text = "Identifier Tokens",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Premium users — tokens aren't used
            if (isPremium) {
                item {
                    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Success) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Success.copy(alpha = 0.18f))
                                    .glowingBorder(1.dp, Success.copy(alpha = 0.35f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Success,
                                    modifier = Modifier.size(36.dp),
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = "You're Premium!",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Success,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Premium subscribers get unlimited identifies — no tokens needed.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = DarkTextMid,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            } else {
                // Token balance hero — show combined trial + purchased token count
                val effectiveTokens = (if (trialActive) trialUsesRemaining else 0) + tokenBalance
                item { TokenBalanceHero(effectiveTokens = effectiveTokens) }

                // How tokens work
                item { HowTokensWorkCard() }

                // Free trial status
                item {
                    FreeTrialStatusCard(
                        trialActive = trialActive,
                        trialUsesRemaining = trialUsesRemaining,
                        trialExpired = trialExpired,
                    )
                }

                // Active unlocks (location + ad-free) if any
                if (hasLocationUnlock || hasAdFreeUnlock) {
                    item {
                        ActiveUnlocksCard(
                            hasLocationUnlock = hasLocationUnlock,
                            hasAdFreeUnlock = hasAdFreeUnlock,
                        )
                    }
                }

                // Donation card with working dropdown
                item {
                    Text(
                        text = "GET MORE TOKENS",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextMid,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                    )
                }
                // Donation card — hidden for Premium subscribers
                if (!isPremium) {
                    item {
                        DonationCard(
                            purchaseManager = purchaseManager,
                            isPurchasing = isPurchasing,
                            onDonate = { packageId ->
                                val activity = context as? Activity
                                if (activity != null) {
                                    scope.launch {
                                        val result = purchaseManager.purchaseDonation(activity, packageId)
                                        if (result is PurchaseResult.Success) {
                                            val amount = IapConfig.PRESET_DONATIONS.firstOrNull {
                                                it.packageId == packageId
                                            }?.displayAmount?.toDouble() ?: 1.0
                                            celebrationLevel = CelebrationLevel.forAmount(amount)
                                            val tokens = IapConfig.DONATION_TOKEN_GRANT[packageId] ?: 0
                                            val days = IapConfig.donationLocationDays(packageId)
                                            kotlinx.coroutines.delay(2000)
                                            celebrationLevel = null
                                            navController.navigate(Routes.thankYou(tokens, days))
                                        }
                                    }
                                }
                            },
                        )
                    }
                }

                // Premium upsell
                item {
                    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Citrine.copy(alpha = 0.18f))
                                    .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Citrine,
                                    modifier = Modifier.size(26.dp),
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Go Premium",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = DarkTextHigh,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "$5.99/mo — unlock ad-free access, location alerts, and unlimited identifies.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DarkTextMid,
                                )
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            SculptedOutlinedButton(
                                text = "Go Premium",
                                onClick = { navController.navigate(Routes.PAYWALL) },
                                accent = Citrine,
                                textColor = Citrine,
                            )
                        }
                    }
                }
            }

            // Footer note
            item {
                Text(
                    text = "Tokens are single-use and consumed each time you identify a specimen. Donations also unlock location monitoring, ad-free access, and other features for a limited time.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMid,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                )
            }
        }
    }

    // Thank-you celebration overlay (after a successful donation)
    celebrationLevel?.let { level ->
        ThankYouCelebration(
            level = level,
            onDismiss = { celebrationLevel = null },
        )
    }
}

/**
 * Big shiny token balance display with the same gold-coin aesthetic as the TokenBank.
 * Shows the combined trial + purchased token count. Identifies always consume tokens
 * unless the user is a Premium subscriber.
 */
@Composable
private fun TokenBalanceHero(effectiveTokens: Int) {
    val transition = rememberInfiniteTransition(label = "tokenHeroShine")
    val shine by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Reverse),
        label = "tokenHeroShineAlpha",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = RoundedCornerShape(24.dp), accent = Citrine, shadowElevation = 7.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F)),
                ),
            )
            .glowingBorder(2.dp, Citrine.copy(alpha = 0.40f), RoundedCornerShape(24.dp)),
    ) {
        // Glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Citrine.copy(alpha = shine * 0.5f), Color.Transparent),
                        radius = 320f,
                    ),
                ),
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Big token coin
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = CircleShape,
                        ambientColor = Citrine.copy(alpha = 0.45f),
                        spotColor = Citrine,
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFFFE9A8), Citrine, CitrineDeep),
                        ),
                    )
                    .glowingBorder(4.dp, Color(0xFFFFF2C8).copy(alpha = 0.85f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color.White.copy(alpha = shine * 0.55f), Color.Transparent),
                            ),
                        ),
                )
                Icon(
                    Icons.Filled.CameraAlt,
                    contentDescription = "Identifier tokens",
                    tint = Ink,
                    modifier = Modifier.size(38.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = effectiveTokens.toString(),
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "identif${if (effectiveTokens == 1) "y" else "ies"} available",
                style = MaterialTheme.typography.titleMedium,
                color = Citrine,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (effectiveTokens > 0)
                    "Each identify uses 1 token (trial or purchased)"
                else
                    "Donate below to stock up on tokens",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
            )
        }
    }
}

/**
 * Explains how the token system works — trial, tokens, premium.
 */
@Composable
private fun HowTokensWorkCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Citrine.copy(alpha = 0.18f))
                    .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = Citrine,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "How Identifies Work",
                style = MaterialTheme.typography.titleLarge,
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(16.dp))

        val steps = listOf(
            Triple("1", "Free Trial", "New users get 5 free identifies over a 1-week trial. Trial uses + purchased tokens are combined in your token bank."),
            Triple("2", "Tokens", "After the trial, each identify costs 1 token. Donate to fill your token bank — donations also unlock location monitoring, ad-free access, and the rest of the app for a limited time."),
            Triple("3", "Go Premium", "Go Premium for $5.99/mo to unlock ad-free access, location alerts, and unlimited identifies."),
        )
        steps.forEach { (num, title, body) ->
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Citrine.copy(alpha = 0.15f))
                        .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        num,
                        style = MaterialTheme.typography.labelMedium,
                        color = Citrine,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleSmall,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                        lineHeight = androidx.compose.ui.unit.TextUnit(20f, androidx.compose.ui.unit.TextUnitType.Sp),
                    )
                }
            }
        }
    }
}

/**
 * Shows the current free-trial status.
 */
@Composable
private fun FreeTrialStatusCard(
    trialActive: Boolean,
    trialUsesRemaining: Int,
    trialExpired: Boolean,
) {
    val accent = if (trialActive && trialUsesRemaining > 0) Success else Citrine
    val statusText = when {
        trialActive && trialUsesRemaining > 0 ->
            "Free trial active — $trialUsesRemaining of ${IdentifyAccessManager.MAX_TRIAL_USES_PER_WEEK_PUBLIC} identifies left this week"
        trialActive ->
            "Free identifies used up this week — resets Monday 12:01 AM EST"
        trialExpired ->
            "Free trial ended — tokens, a donation, or Premium required to identify"
        else -> "Free trial unavailable"
    }

    DarkCard(modifier = Modifier.fillMaxWidth(), accent = accent) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.18f))
                    .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (trialActive && trialUsesRemaining > 0) Icons.Filled.CameraAlt
                    else Icons.Filled.Lock,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Free Trial",
                    style = MaterialTheme.typography.labelMedium,
                    color = accent,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

/**
 * Shows active donated unlocks (location monitoring + ad-free).
 */
@Composable
private fun ActiveUnlocksCard(
    hasLocationUnlock: Boolean,
    hasAdFreeUnlock: Boolean,
) {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Success) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Success.copy(alpha = 0.18f))
                    .glowingBorder(1.dp, Success.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = Success,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Active Donor Perks",
                style = MaterialTheme.typography.titleMedium,
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(10.dp))
        // Identifies still consume tokens even while other features are unlocked
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.CameraAlt,
                contentDescription = null,
                tint = Success,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Identifies still use 1 token each",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextHigh,
            )
        }
        if (hasLocationUnlock) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = Success,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Location monitoring unlocked",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextHigh,
                )
            }
        }
        if (hasAdFreeUnlock) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null,
                    tint = Success,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Ad-free experience unlocked",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextHigh,
                )
            }
        }
    }
}

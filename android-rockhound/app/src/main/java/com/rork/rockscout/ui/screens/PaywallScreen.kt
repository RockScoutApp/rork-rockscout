package com.rork.rockscout.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.content.Intent
import android.net.Uri
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.IapConfig
import com.rork.rockscout.data.PurchaseManager
import com.rork.rockscout.data.PurchaseResult
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.CitrineSoft
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.launch

@Composable
fun PaywallScreen(navController: NavController) {
    val purchaseManager = PurchaseManager.instance
    val isPremium by purchaseManager.isPremium.collectAsState()
    val isLoading by purchaseManager.isLoading.collectAsState()
    val isPurchasing by purchaseManager.isPurchasing.collectAsState()
    val message by purchaseManager.purchaseMessage.collectAsState()
    val currentOffering by purchaseManager.currentOffering.collectAsState()

    val auth = AuthRepository.instance
    val sessionStatus by auth.sessionStatus.collectAsState()
    val isSignedIn = sessionStatus is com.rork.rockscout.data.SessionStatus.Authenticated
    val signedInEmail = (sessionStatus as? com.rork.rockscout.data.SessionStatus.Authenticated)?.session?.user?.email

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showSuccessDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = showSuccessDialog) {
        showSuccessDialog = false
    }

    LaunchedEffect(isPremium) {
        if (isPremium) {
            showSuccessDialog = true
        }
    }

    LaunchedEffect(message) {
        if (message != null && !isPurchasing) {
            kotlinx.coroutines.delay(4000)
            purchaseManager.clearMessage()
        }
    }

    RockBackground {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp, top = 56.dp, bottom = 40.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Top bar with back button
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SculptedIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = { navController.popBackStack() },
                        accent = Aqua,
                        iconTint = TextMid,
                        backgroundColor = Slate800,
                    )
                    Text(
                        text = "Choose Your Plan",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Subtitle — Flintstones vs Star Trek tier comparison
            item {
                Text(
                    text = "Free tier is the Flintstones car with a map carved on a stone tablet — 1 AI model — you'll get there eventually. Premium is the transporter from Star Trek — 3 AI models, web search, and database cross checks — you'll get where you want to go faster than you can say “Beam me up Scotty.”",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMid,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                )
            }

            // Signed-in reassurance banner
            if (isSignedIn && signedInEmail != null) {
                item {
                    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Aqua) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = Aqua,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Signed in as $signedInEmail — your profile, collections, and friends carry over to Premium.",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextMid,
                            )
                        }
                    }
                }
            }

            // Already premium banner
            if (isPremium) {
                item {
                    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Success.copy(alpha = 0.18f))
                                    .glowingBorder(1.dp, Success.copy(alpha = 0.35f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    Icons.Filled.Verified,
                                    contentDescription = null,
                                    tint = Success,
                                    modifier = Modifier.size(32.dp),
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "You're on Premium!",
                                style = MaterialTheme.typography.titleLarge,
                                color = Success,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Thanks for supporting an indie rockhounder-made app. Ads are removed and every feature is unlocked.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextMid,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(Modifier.height(12.dp))
                            // Manage Subscription link (Part 6)
                            SculptedOutlinedButton(
                                text = "Manage Subscription",
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/account/subscriptions"))
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    runCatching { context.startActivity(intent) }
                                },
                                accent = Aqua,
                                textColor = Aqua,
                                icon = Icons.Filled.Restore,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "You can cancel anytime from Google Play.",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextMid,
                            )
                        }
                    }
                }
            }

            // Free tier card — trial + donation system
            item { FreeTierCard() }

            // Offering load error fallback (Part 8)
            if (currentOffering == null && !isLoading) {
                item {
                    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFFE2574C)) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                Icons.Filled.Block,
                                contentDescription = null,
                                tint = Color(0xFFE2574C),
                                modifier = Modifier.size(32.dp),
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Plans temporarily unavailable — check your connection and try again.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextHigh,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(Modifier.height(12.dp))
                            SculptedButton(
                                text = "Retry",
                                onClick = { purchaseManager.fetchOfferings() },
                                accent = Aqua,
                                containerColor = Aqua,
                                textColor = Ink,
                                icon = Icons.Filled.Restore,
                            )
                        }
                    }
                }
            }

            // Premium tier card — all 3 AI models + all features unlocked
            item {
                PremiumTierCard(
                    isPurchasing = isPurchasing,
                    isCurrentPlan = isPremium,
                    onPurchase = {
                        val activity = context as? Activity
                        if (activity != null) {
                            scope.launch {
                                val result = purchaseManager.purchaseMonthlySubscription(activity)
                                if (result is PurchaseResult.Success) {
                                    showSuccessDialog = true
                                }
                            }
                        }
                    },
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
                                        val tokens = com.rork.rockscout.data.IapConfig.DONATION_TOKEN_GRANT[packageId] ?: 0
                                        val days = com.rork.rockscout.data.IapConfig.donationLocationDays(packageId)
                                        navController.navigate(Routes.thankYou(tokens, days))
                                    }
                                }
                            }
                        },
                    )
                }
            }

            // Restore purchases button — shorter, centered, wrap-to-content width.
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    SculptedOutlinedButton(
                        text = "Already purchased? Restore",
                        onClick = {
                            scope.launch {
                                purchaseManager.restorePurchases()
                            }
                        },
                        accent = Aqua,
                        textColor = Aqua,
                        icon = Icons.Filled.Restore,
                        enabled = !isLoading,
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    )
                }
            }

            // Message feedback
            if (message != null) {
                item {
                    val isError = !isPremium && !message!!.contains("successful", ignoreCase = true) && !message!!.contains("Thank you", ignoreCase = true)
                    DarkCard(
                        modifier = Modifier.fillMaxWidth(),
                        accent = Citrine,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isError) Color(0xFFE2574C).copy(alpha = 0.15f)
                                        else Success.copy(alpha = 0.15f)
                                    )
                                    .glowingBorder(1.dp, Success.copy(alpha = 0.35f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    if (isError) Icons.Filled.Block else Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = if (isError) Color(0xFFE2574C) else Success,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = message!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isError) Color(0xFFE2574C) else Success,
                            )
                        }
                    }
                }
            }

            // Footer note
            item {
                Text(
                    text = "Made by a rockhounder, for rockhounders. Your support keeps RockScout growing.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMid,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                )
            }
        }
    }

    // Success dialog
    if (showSuccessDialog && isPremium) {
        SuccessDialog(onDismiss = {
            showSuccessDialog = false
            navController.popBackStack()
        })
    }
}

/** Accuracy ladder chip row — shows which AI models each tier uses. */
@Composable
private fun AccuracyLadder(models: List<Pair<String, Color>>, accent: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        models.forEach { (label, color) ->
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.16f))
                    .glowingBorder(2.dp, color.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                    .padding(vertical = 8.dp, horizontal = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Filled.Science,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = color,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

/** Single feature row with an icon + check. */
@Composable
private fun FeatureRow(icon: ImageVector, text: String, accent: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.16f))
                .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(14.dp),
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextHigh,
            modifier = Modifier.weight(1f),
        )
    }
}

/** Free tier card — explains the 1-week trial and donation system. */
@Composable
private fun FreeTierCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Aqua) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Aqua.copy(alpha = 0.20f))
                        .glowingBorder(1.dp, Aqua.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Spa,
                        contentDescription = null,
                        tint = Aqua,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Free",
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "$0 · 1-week trial, then free tier",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Aqua,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                TagChip("TRIAL", color = Aqua, filled = true, textColor = Ink)
            }
            Spacer(Modifier.height(14.dp))

            // Trial explanation
            Text(
                text = "Try everything free for 1 week.",
                style = MaterialTheme.typography.titleMedium,
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "You get 5 identification tokens to spend at your own pace over 7 days. Full app access — AI identifier, dig sites, field captures, wishlist, favorite spots, and RockScout Friends. Banner and interstitial ads run throughout.",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
            )
            Spacer(Modifier.height(14.dp))

            // After trial explanation
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = Success, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "After the trial — what stays free",
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(8.dp))
            FeatureRow(Icons.Filled.Spa, "Browse the full specimen database & geology guides", Success)
            Spacer(Modifier.height(8.dp))
            FeatureRow(Icons.Filled.CloudDownload, "Bulk-download all specimen photos for offline use (~4 GB)", Success)
            Spacer(Modifier.height(8.dp))
            FeatureRow(Icons.Filled.PhotoCamera, "Field camera — snap photos and save to Saved Images", Success)
            Spacer(Modifier.height(8.dp))
            FeatureRow(Icons.Filled.Notifications, "NWS severe weather alerts", Success)
            Spacer(Modifier.height(8.dp))
            FeatureRow(Icons.Filled.Map, "Browse dig sites & offline maps", Success)
            Spacer(Modifier.height(14.dp))

            // What locks after trial
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = Aqua, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "What requires Premium or a donation",
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(8.dp))
            FeatureRow(Icons.Filled.Bolt, "AI identification & ID tokens", Aqua)
            Spacer(Modifier.height(8.dp))
            FeatureRow(Icons.Filled.Spa, "RockScout Friends, Messenger & Community", Aqua)
            Spacer(Modifier.height(8.dp))
            FeatureRow(Icons.Filled.Send, "Trade Board, Trading Floor & Discover Hunters", Aqua)
            Spacer(Modifier.height(8.dp))
            FeatureRow(Icons.Filled.GpsFixed, "My Rocks, Wishlist, Field Captures & Favorite Spots", Aqua)
            Spacer(Modifier.height(8.dp))
            FeatureRow(Icons.Filled.Map, "Trip Planner, Field Journal & submitting specimens", Aqua)
            Spacer(Modifier.height(14.dp))

            // How to keep going — donation system
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color(0xFFE2574C), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Two ways to keep identifying",
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(8.dp))
            FeatureRow(Icons.Filled.Bolt, "Watch 2 short rock-related videos = 1 ID token (no weekly cap)", Aqua)
            Spacer(Modifier.height(8.dp))
            FeatureRow(Icons.Filled.Favorite, "Donate any amount — get tokens + a temporary full-feature unlock (2 days up to 1 month)", Color(0xFFE2574C))
            Spacer(Modifier.height(12.dp))

            // Accuracy
            Text(
                text = "ID accuracy",
                style = MaterialTheme.typography.labelLarge,
                color = DarkTextMid,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            AccuracyLadder(
                models = listOf("Haiku" to Aqua),
                accent = Aqua,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Single-model AI identification. Best for clear, typical specimens.",
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextLow,
            )
        }
    }
}

/** Premium tier card — emphasizes ID accuracy lift and unlocked features. */
@Composable
private fun PremiumTierCard(
    isPurchasing: Boolean,
    isCurrentPlan: Boolean,
    onPurchase: () -> Unit,
) {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Citrine.copy(alpha = 0.22f))
                        .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = Citrine,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Premium",
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "$9.99/mo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Citrine,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                if (isCurrentPlan) {
                    TagChip("CURRENT", color = Success, filled = true, textColor = Ink)
                } else {
                    TagChip("POPULAR", color = CitrineSoft, filled = true, textColor = Ink)
                }
            }
            Spacer(Modifier.height(14.dp))

            // Accuracy callout — the headline upgrade reason
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Citrine.copy(alpha = 0.12f))
                    .glowingBorder(2.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Citrine, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "The highest accuracy we offer",
                            style = MaterialTheme.typography.titleSmall,
                            color = DarkTextHigh,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Three AI models vote on the hardest IDs.",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextMid,
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            Text(
                text = "ID accuracy",
                style = MaterialTheme.typography.labelLarge,
                color = DarkTextMid,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            AccuracyLadder(
                models = listOf("Haiku" to Citrine, "+ Sonnet" to CitrineSoft, "+ Gemini" to Amethyst),
                accent = Citrine,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Haiku + Sonnet work every call. When both disagree or return low confidence (the hardest ~5%), Gemini 2.5 Pro casts the deciding vote. Three independent models agreeing is the strongest ID signal available.",
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextLow,
            )
            Spacer(Modifier.height(14.dp))

            // Unlocked features
            Text(
                text = "Everything unlocked",
                style = MaterialTheme.typography.labelLarge,
                color = DarkTextMid,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            FeatureRow(Icons.Filled.Check, "Unlimited AI identifies — no tokens, no caps", Citrine)
            Spacer(Modifier.height(8.dp))
            FeatureRow(Icons.Filled.Block, "Ad-free — banner & interstitial ads removed", Citrine)
            Spacer(Modifier.height(8.dp))
            FeatureRow(Icons.Filled.GpsFixed, "Location alerts near dig sites", Citrine)
            Spacer(Modifier.height(8.dp))
            FeatureRow(Icons.Filled.Map, "Field captures, wishlist, my rocks, favorite spots", Citrine)
            Spacer(Modifier.height(8.dp))
            FeatureRow(Icons.Filled.Spa, "RockScout Friends — scan, message, and ping (100 mi)", Citrine)
            Spacer(Modifier.height(10.dp))
            FeatureRow(Icons.Filled.Public, "250 mi scan radius — cast the widest net", Citrine)
            Spacer(Modifier.height(10.dp))
            FeatureRow(Icons.Filled.GpsFixed, "24-hour pings (2× the standard window)", Citrine)
            Spacer(Modifier.height(10.dp))
            FeatureRow(Icons.Filled.Verified, "Premium gem badge on your profile, cards & pins", Citrine)
            Spacer(Modifier.height(10.dp))
            FeatureRow(Icons.Filled.RocketLaunch, "Early access to new features (a week ahead)", Citrine)
            Spacer(Modifier.height(14.dp))

            // CTA button
            if (isCurrentPlan) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Success.copy(alpha = 0.18f))
                        .glowingBorder(2.dp, Success.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = Success, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Current Plan", color = Success, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                }
            } else if (isPurchasing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator(color = Citrine, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    SculptedButton(
                        text = "Go Premium · $9.99/mo",
                        onClick = onPurchase,
                        accent = Citrine,
                        containerColor = Citrine,
                        textColor = Ink,
                        icon = Icons.Filled.Star,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun DonationCard(
    purchaseManager: PurchaseManager,
    isPurchasing: Boolean,
    onDonate: (String) -> Unit,
) {
    val allDonations = purchaseManager.allDonationPackages
    val presets = IapConfig.PRESET_DONATIONS
    var selectedPresetIndex by remember { mutableStateOf(0) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val selectedPreset = presets[selectedPresetIndex]
    val selectedPkg = allDonations[selectedPreset.packageId]
    val donateButtonLabel = "$${selectedPreset.displayAmount}"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = RoundedCornerShape(24.dp), accent = Color(0xFFE2574C), shadowElevation = 6.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF2A2820),
                        Color(0xFF1E1C16),
                        Color(0xFF16140F),
                    )
                )
            )
            .glowingBorder(3.dp, Color(0xFFE2574C).copy(alpha = 0.25f), RoundedCornerShape(24.dp))
            .padding(20.dp),
    ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFE2574C).copy(alpha = 0.35f), Color(0xFFE2574C).copy(alpha = 0.10f))
                            )
                        )
                        .glowingBorder(2.dp, Color(0xFFE2574C).copy(alpha = 0.30f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFE2574C),
                        modifier = Modifier.size(28.dp),
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Support RockScout",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Choose an amount to support the developer. Each donation grants identifier tokens and a temporary full-feature unlock — from 2 days up to 1 week.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFF5F2EA),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            // Dropdown — using Box + DropdownMenu for reliable touch handling inside LazyColumn
            Box(modifier = Modifier.fillMaxWidth()) {
                // Trigger button — looks like a text field but is a clickable Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .glowingBorder(
                            1.dp,
                            if (dropdownExpanded) Color(0xFFE2574C)
                            else Color(0xFFE2574C).copy(alpha = 0.40f),
                            RoundedCornerShape(12.dp),
                        )
                        .background(Color(0xFF2A2820).copy(alpha = 0.6f))
                        .clickable { dropdownExpanded = !dropdownExpanded }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "\$${selectedPreset.displayAmount} · ${selectedPreset.tokenGrant} tokens",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                            val locLabel = IapConfig.donationLocationLabel(selectedPreset.packageId)
                            if (locLabel.isNotEmpty()) {
                                Text(
                                    text = locLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Citrine,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            contentDescription = if (dropdownExpanded) "Collapse" else "Expand",
                            tint = Color(0xFFE2574C),
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { dropdownExpanded = !dropdownExpanded },
                        )
                    }
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(
                            Color(0xFF1E1C16),
                            RoundedCornerShape(12.dp),
                        )
                        .glowingBorder(2.dp, Color(0xFFE2574C).copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                ) {
                    presets.forEachIndexed { index, preset ->
                        val pkg = allDonations[preset.packageId]
                        val displayPrice = pkg?.product?.price?.formatted ?: preset.fallbackPrice
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(
                                        text = "\$${preset.displayAmount}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (index == selectedPresetIndex) Color(0xFFE2574C) else Color.White,
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        text = "${preset.tokenGrant} tokens",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFFFE9A8),
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    val locLabel = IapConfig.donationLocationLabel(preset.packageId)
                                    if (locLabel.isNotEmpty()) {
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = locLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Citrine,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }
                                    Spacer(Modifier.weight(1f))
                                    if (index == selectedPresetIndex) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Color(0xFFE2574C),
                                            modifier = Modifier.size(18.dp),
                                        )
                                    }
                                }
                            },
                            onClick = {
                                selectedPresetIndex = index
                                dropdownExpanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = Color.White,
                                leadingIconColor = Color.White,
                                trailingIconColor = Color.White,
                                disabledTextColor = Color(0xFFC9C2B0),
                                disabledLeadingIconColor = Color(0xFFC9C2B0),
                                disabledTrailingIconColor = Color(0xFFC9C2B0),
                            ),
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            if (isPurchasing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFE2574C),
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp),
                    )
                }
            } else {
                SculptedButton(
                    text = "Donate $donateButtonLabel",
                    onClick = {
                        onDonate(presets[selectedPresetIndex].packageId)
                    },
                    accent = Color(0xFFE2574C),
                    containerColor = Color(0xFFE2574C),
                    textColor = Color.White,
                    icon = Icons.Filled.Favorite,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Your account info carries over — donate from any device.",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFC8C2B0),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }

@Composable
private fun SuccessDialog(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Success.copy(alpha = 0.18f), Slate900)
                )
            )
            .padding(24.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Success.copy(alpha = 0.2f))
                    .glowingBorder(1.dp, Success.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = Success,
                    modifier = Modifier.size(36.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Thank You!",
                style = MaterialTheme.typography.headlineSmall,
                color = Ink,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Ads have been removed. Enjoy RockScout ad-free!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(20.dp))
            SculptedButton(
                text = "Continue",
                onClick = onDismiss,
                accent = Success,
                containerColor = Success,
                textColor = Ink,
                shape = RoundedCornerShape(12.dp),
            )
        }
    }
}

package com.rork.rockscout.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rork.rockscout.data.AchievementsRepository
import com.rork.rockscout.data.PurchaseManager
import com.rork.rockscout.data.ReferralApi
import com.rork.rockscout.data.ReferralRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.data.XpSource
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.ShareToProfileComposer
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.theme.Warning
import kotlinx.coroutines.launch
import com.rork.rockscout.ui.components.glowingBorder

/**
 * "Enlist a RockScout" referral screen.
 *
 * Shows the user's personal referral code, shareable text, pending/completed
 * enlistment counts, and a categorized reward explainer (free vs premium).
 * Includes an email input + send button so the user can send their code
 * directly to another person. After a new user signs up with the code, both
 * users automatically become RockScout Friends and the sender receives rewards.
 */
@Composable
fun ReferralScreen(navController: NavController) {
    val context = LocalContext.current
    val referralCode by ReferralRepository.referralCode.collectAsStateWithLifecycle()
    val pendingCount by ReferralRepository.pendingCount.collectAsStateWithLifecycle()
    val completedCount by ReferralRepository.completedCount.collectAsStateWithLifecycle()
    val rewardsCredited by ReferralRepository.rewardsCredited.collectAsStateWithLifecycle()
    val achievementsState by AchievementsRepository.state.collectAsStateWithLifecycle()
    val purchaseManager = PurchaseManager.instance
    val isPremium by purchaseManager.hasPaidAccess.collectAsStateWithLifecycle()
    val auth = AuthRepository.instance
    val signedInEmail = auth.currentUserEmail ?: ""
    val scope = rememberCoroutineScope()

    val recruiterBadgeEarned = "recruiter" in achievementsState.earnedBadgeIds
    val weeklyCapReached = ReferralRepository.isFreeWeeklyCapReached()
    val weeklyCount = ReferralRepository.weeklyRewardedCount()
    var showShareToProfile by remember { mutableStateOf(false) }

    var recipientEmail by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var sendResult by remember { mutableStateOf<String?>(null) }

    ScreenScaffold(title = "Enlist a RockScout", onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 20.dp, end = 20.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    text = if (isPremium)
                        "Premium users earn 500 XP for every friend who joins RockScout with their code. No referral limit."
                    else
                        "Free users earn 4 ID tokens + a 1-day full-feature unlock for every friend who joins. Limit 3 rewarded referrals per week. Premium users earn 500 XP per referral with no weekly limit.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Aqua,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }

            // Hero referral-code card
            item {
                DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Citrine.copy(alpha = 0.5f), Aqua.copy(alpha = 0.3f)))),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "+${ReferralRepository.PREMIUM_XP_REWARD}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                )
                                Text(
                                    "XP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Citrine,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                )
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Your referral code", style = MaterialTheme.typography.labelMedium, color = Citrine, fontWeight = FontWeight.Bold)
                            Text(
                                referralCode,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3A3830))
                                .glowingBorder(1.dp, Color(0xFF3A3830).copy(alpha = 0.35f), CircleShape)
                                .clickable {
                                    val clipboard = context.getSystemService(android.content.ClipboardManager::class.java)
                                    clipboard?.setPrimaryClip(
                                        android.content.ClipData.newPlainText("RockScout referral code", referralCode)
                                    )
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy code", tint = Citrine, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    SculptedButton(
                        text = "Share your code",
                        onClick = {
                            // Register the code on the backend so it's verifiable
                            // when a recipient enters it, even via share-sheet.
                            val code = ReferralRepository.referralCode.value
                            val senderEmail = AuthRepository.instance.currentUserEmail ?: ""
                            val senderName = com.rork.rockscout.data.AppRepository.instance.profile.value.name
                            if (senderEmail.isNotBlank()) {
                                scope.launch {
                                    ReferralApi.registerCode(code, senderEmail, senderName)
                                    ReferralRepository.incrementPending()
                                }
                            }
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Join me on RockScout")
                                putExtra(Intent.EXTRA_TEXT, ReferralRepository.shareText())
                            }
                            SafeLinkOpener.openShareChooser(context, shareIntent, "Share your code")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        accent = Citrine,
                        containerColor = Citrine,
                        textColor = Ink,
                        icon = Icons.Filled.Share,
                    )
                    Spacer(Modifier.height(8.dp))
                    SculptedOutlinedButton(
                        text = "Share to Profile",
                        onClick = { showShareToProfile = true },
                        modifier = Modifier.fillMaxWidth(),
                        accent = Citrine,
                        textColor = Citrine,
                        icon = Icons.Filled.PersonAdd,
                    )
                }
            }

            // Email referral section
            item {
                DarkCard(modifier = Modifier.fillMaxWidth(), accent = Aqua) {
                    Text(
                        "Send a referral by email",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Enter a friend's email and we'll send them your code. When they sign up and enter it, you both get rewards and automatically become RockScout Friends.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = recipientEmail,
                        onValueChange = { recipientEmail = it },
                        label = { Text("Friend's email") },
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = Aqua) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth().noAutoFocus(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Slate800,
                            unfocusedContainerColor = Slate800,
                            focusedIndicatorColor = Aqua,
                            unfocusedIndicatorColor = Color(0xFF5A5750),
                            focusedTextColor = TextHigh,
                            unfocusedTextColor = TextHigh,
                            focusedLabelColor = Aqua,
                            unfocusedLabelColor = TextMid,
                        ),
                    )
                    Spacer(Modifier.height(10.dp))
                    SculptedButton(
                        text = if (isSending) "Sending…" else "Send referral",
                        onClick = {
                            if (recipientEmail.isBlank() || !recipientEmail.contains('@')) {
                                sendResult = "Please enter a valid email address."
                                return@SculptedButton
                            }
                            if (signedInEmail.isBlank()) {
                                sendResult = "Sign in to send referral emails."
                                return@SculptedButton
                            }
                            isSending = true
                            sendResult = null
                            scope.launch {
                                val success = ReferralApi.sendReferralEmail(
                                    code = referralCode,
                                    senderEmail = signedInEmail,
                                    recipientEmail = recipientEmail.trim(),
                                    senderName = AuthRepository.instance.currentUserId?.let { id ->
                                        SocialRepository.instance.fetchUserProfile(id)?.display_name
                                    } ?: "A RockScout friend",
                                )
                                isSending = false
                                sendResult = if (success) {
                                    ReferralRepository.incrementPending()
                                    "Invitation sent! When they sign up with your code, you'll get rewards."
                                } else {
                                    "Couldn't send the email. Try sharing your code directly instead."
                                }
                            }
                        },
                        enabled = !isSending,
                        modifier = Modifier.fillMaxWidth(),
                        accent = Aqua,
                        containerColor = Aqua,
                        textColor = Ink,
                        icon = if (isSending) null else Icons.Filled.Email,
                    )
                    sendResult?.let { msg ->
                        Spacer(Modifier.height(8.dp))
                        Text(
                            msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (msg.startsWith("Invitation")) Success else Warning,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            // Stats row — pending + completed
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ReferralStatCard(
                        label = "Pending",
                        value = pendingCount.toString(),
                        accent = Warning,
                        modifier = Modifier.weight(1f),
                    )
                    ReferralStatCard(
                        label = "Completed",
                        value = completedCount.toString(),
                        accent = Success,
                        modifier = Modifier.weight(1f),
                    )
                    ReferralStatCard(
                        label = if (isPremium) "Rewards" else "This week",
                        value = if (isPremium) rewardsCredited.toString() else "$weeklyCount/${ReferralRepository.FREE_USER_WEEKLY_CAP}",
                        accent = Citrine,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Reward explainer
            item {
                DarkCard(modifier = Modifier.fillMaxWidth(), accent = Aqua) {
                    Text("How rewards work", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    RewardRow(
                        icon = Icons.Filled.CheckCircle,
                        title = "Friend signs in AND enters your code",
                        detail = "A referral is only complete when your friend downloads RockScout, creates an account, signs in, and enters your referral code. Rewards are credited to both of you automatically — sign-up alone is not enough.",
                    )
                    Spacer(Modifier.height(10.dp))
                    if (isPremium) {
                        RewardRow(
                            icon = Icons.Filled.Star,
                            title = "You earn ${ReferralRepository.PREMIUM_XP_REWARD} XP",
                            detail = "Premium users receive ${ReferralRepository.PREMIUM_XP_REWARD} XP per completed referral with no weekly limit.",
                        )
                    } else {
                        RewardRow(
                            icon = Icons.Filled.Star,
                            title = "You earn ${ReferralRepository.TOKEN_REWARD} ID tokens",
                            detail = "Free users get ${ReferralRepository.TOKEN_REWARD} identifier tokens per completed referral — up to ${ReferralRepository.FREE_USER_WEEKLY_CAP} rewarded referrals per week.",
                        )
                        Spacer(Modifier.height(10.dp))
                        RewardRow(
                            icon = Icons.Filled.Group,
                            title = "+${ReferralRepository.UNLOCK_DAYS_REWARD} day full-feature unlock",
                            detail = "Free users also get a ${ReferralRepository.UNLOCK_DAYS_REWARD}-day unlock for location monitoring, ad-free use, and RockScout Friends.",
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    RewardRow(
                        icon = Icons.Filled.PersonAdd,
                        title = "You become RockScout Friends automatically",
                        detail = "After the new user signs in with your code, a friend request is sent both ways so you can find each other easily.",
                    )
                }
            }

            // Recruiter badge status
            item {
                DarkCard(modifier = Modifier.fillMaxWidth(), accent = if (recruiterBadgeEarned) Success else Color(0xFF5A5750)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background((if (recruiterBadgeEarned) Success else Color(0xFF5A5750)).copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center,
                        ) { Text("🌟", style = MaterialTheme.typography.titleLarge) }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                if (recruiterBadgeEarned) "Recruiter badge earned" else "Recruiter badge — locked",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                if (recruiterBadgeEarned)
                                    "You enlisted your first RockScout. Keep going to climb the levels."
                                else
                                    "Enlist your first RockScout to earn the Recruiter badge + 50 XP.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextMid,
                            )
                        }
                    }
                }
            }

            // Cap notice for free users
            if (!isPremium && weeklyCapReached) {
                item {
                    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Warning) {
                        Text(
                            "Weekly cap reached — $weeklyCount/${ReferralRepository.FREE_USER_WEEKLY_CAP}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "You've hit the free-user referral reward cap for this week. Enlistments still track, and rewards resume next Monday. Premium users have no referral limit.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextMid,
                        )
                    }
                }
            }

            // Safety / anti-farming note
            item {
                DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFF5A5750)) {
                    Text("Fair play", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Rewards only trigger when a referred friend completes both sign-in AND enters your referral code — not on sign-up alone. This ensures both parties receive their rewards only after the referral is fully completed, so fake accounts can't farm tokens or XP.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextMid,
                    )
                }
            }
        }
    }

    if (showShareToProfile) {
        ShareToProfileComposer(
            sourceType = "referral",
            title = "Enlist a RockScout!",
            tagline = "Code: $referralCode",
            imageUri = null,
            locationText = "",
            onDismiss = { showShareToProfile = false },
        )
    }
}

@Composable
private fun ReferralStatCard(label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(listOf(accent.copy(alpha = 0.28f), Slate800)))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.30f))
                .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(value, style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextHigh, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun RewardRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, detail: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Aqua.copy(alpha = 0.22f))
                .glowingBorder(1.dp, Aqua.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = Aqua, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text(detail, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid)
        }
    }
}

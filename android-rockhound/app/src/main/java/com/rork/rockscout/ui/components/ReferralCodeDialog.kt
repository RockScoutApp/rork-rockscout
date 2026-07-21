package com.rork.rockscout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.ReferralApi
import com.rork.rockscout.data.ReferralRepository
import com.rork.rockscout.data.SocialRepository
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.theme.Warning
import kotlinx.coroutines.launch
import com.rork.rockscout.ui.components.glowingBorder

/**
 * Deprecated — referral code entry is now handled inline on the SignInScreen before
 * sign-in. This dialog is no longer invoked anywhere in the app. Kept for reference only.
 *
 * The code was previously verified against the Cloudflare backend after sign-in, and if
 * valid the new user received free gifts (ID tokens + a full-feature unlock) and a
 * reciprocal RockScout Friends connection was created with the referrer.
 */
@Composable
fun ReferralCodeDialog(onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    var code by remember { mutableStateOf("") }
    var isVerifying by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { if (!isVerifying) onDismiss() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1E1C16))
                .glowingBorder(1.dp, Color(0xFF1E1C16).copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                .padding(24.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Filled.CardGiftcard,
                contentDescription = null,
                tint = Citrine,
                modifier = Modifier.size(48.dp),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Have a referral code?",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "If a friend invited you to RockScout, enter their code here. " +
                    "You'll get free ID tokens + a full-feature unlock, and you'll automatically become RockScout Friends.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = code,
                onValueChange = { code = it.uppercase().filter { c -> c.isLetterOrDigit() || c == '-' } },
                label = { Text("Referral code") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth().noAutoFocus(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Slate800,
                    unfocusedContainerColor = Slate800,
                    focusedIndicatorColor = Citrine,
                    unfocusedIndicatorColor = Color(0xFF5A5750),
                    focusedTextColor = TextHigh,
                    unfocusedTextColor = TextHigh,
                    focusedLabelColor = Citrine,
                    unfocusedLabelColor = TextMid,
                ),
            )
            result?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Text(
                    msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (success) Aqua else Warning,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.height(16.dp))
            SculptedButton(
                text = if (isVerifying) "Verifying…" else "Apply code",
                onClick = {
                    val trimmed = code.trim()
                    if (trimmed.isBlank() || !trimmed.startsWith("ROCK-")) {
                        result = "Please enter a valid ROCK- referral code."
                        success = false
                        return@SculptedButton
                    }
                    val email = AuthRepository.instance.currentUserEmail
                    if (email.isNullOrBlank()) {
                        result = "Sign in required to apply a referral code."
                        success = false
                        return@SculptedButton
                    }
                    isVerifying = true
                    result = null
                    success = false
                    scope.launch {
                        val senderEmail = ReferralApi.verifyReferralCode(trimmed, email)
                        if (senderEmail != null) {
                            ReferralRepository.applyReferralCode(senderEmail)
                            SocialRepository.instance.createConnectionFromReferral(senderEmail)
                            ReferralApi.completeReferral(trimmed, email)
                            ReferralRepository.markReferralPromptShown()
                            result = "Welcome gift applied! You and your friend are now connected."
                            success = true
                        } else {
                            result = "That code wasn't found. Double-check it with your friend."
                            success = false
                        }
                        isVerifying = false
                    }
                },
                enabled = !isVerifying,
                modifier = Modifier.fillMaxWidth(),
                accent = Citrine,
                containerColor = Citrine,
                textColor = Ink,
                shape = RoundedCornerShape(14.dp),
            )
            Spacer(Modifier.height(8.dp))
            SculptedTextButton(
                text = "Skip",
                onClick = { onDismiss() },
                accent = Citrine,
                textColor = Aqua,
                enabled = !isVerifying,
                fontWeight = FontWeight.SemiBold,
            )
            if (success) {
                Spacer(Modifier.height(8.dp))
                SculptedTextButton(
                    text = "Continue",
                    onClick = { onDismiss() },
                    accent = Citrine,
                    textColor = Citrine,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.ReferralRepository
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.CitrineDeep
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.StoneLine
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.data.SessionStatus
import kotlinx.coroutines.launch
import com.rork.rockscout.ui.components.noAutoFocus
import com.rork.rockscout.ui.components.SculptedIconButton
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Mail
import com.rork.rockscout.ui.theme.Danger
import kotlinx.coroutines.delay

/**
 * Self-contained RockScout sign-in / create-account screen. Primary path is
 * email + password. When [isGate] is true the screen acts as a mandatory
 * blocking gate — no back button, defaults to sign-up mode, and the user
 * cannot dismiss it without authenticating. When [isGate] is false the screen
 * is purely additive (e.g. opened from the Profile tab).
 */
@Composable
fun SignInScreen(
    onSignedIn: () -> Unit,
    onBack: () -> Unit,
    isGate: Boolean = false,
) {
    // In gate mode there is no previous screen to return to — swallow the
    // system back gesture so it doesn't kick the user out of the app before
    // they've authenticated. Mirrors the gate pattern in DisclaimerScreen.
    BackHandler(enabled = isGate) { /* no-op */ }

    val auth = AuthRepository.instance
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val sessionStatus by auth.sessionStatus.collectAsStateWithLifecycle()
    val isLoading by auth.isLoading.collectAsStateWithLifecycle()
    val error by auth.error.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(isGate) }
    var showPassword by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }
    var infoMessage by remember { mutableStateOf<String?>(null) }

    // ─── Email verification state ───
    var verificationCode by remember { mutableStateOf("") }
    var resendCooldown by remember { mutableStateOf(0) }
    var isVerifying by remember { mutableStateOf(false) }
    var isResending by remember { mutableStateOf(false) }
    var verifyError by remember { mutableStateOf<String?>(null) }
    var verifyInfo by remember { mutableStateOf<String?>(null) }
    val pendingEmail = auth.pendingVerificationEmail
    val isPendingVerification = sessionStatus is SessionStatus.PendingVerification

    // Referral code entry — shown as a collapsible field on the sign-in screen.
    // The user enters the code BEFORE signing in; it is stored locally and
    // processed after sign-in completes (verified against the backend, free
    // gifts applied, RockScout Friends connection created, and a confirmation
    // popup shown on the home screen).
    val referralCodeApplied by ReferralRepository.referralCodeApplied.collectAsStateWithLifecycle()
    var showReferralField by remember { mutableStateOf(false) }
    var referralCode by remember { mutableStateOf("") }

    // Cooldown timer for resend button — ticks down every second.
    LaunchedEffect(resendCooldown) {
        if (resendCooldown > 0) {
            delay(1000)
            resendCooldown = resendCooldown - 1
        }
    }

    // Auto-dismiss on a successful authenticated session.
    // Before dismissing, process any pending referral code.
    LaunchedEffect(sessionStatus) {
        if (sessionStatus is SessionStatus.Authenticated) {
            // If the user entered a referral code, store it so it gets
            // processed by the AppNavigation LaunchedEffect after navigation.
            val trimmed = referralCode.trim()
            if (trimmed.isNotBlank() && trimmed.startsWith("ROCK-") && !referralCodeApplied) {
                ReferralRepository.setPendingReferralCode(trimmed)
            }
            onSignedIn()
        }
        // Reset verification state when leaving pending state.
        if (sessionStatus !is SessionStatus.PendingVerification) {
            verificationCode = ""
            verifyError = null
            verifyInfo = null
        }
    }

    fun submit() {
        localError = null
        infoMessage = null
        if (email.isBlank() || !email.contains('@')) {
            localError = "Please enter a valid email address."
            return
        }
        if (password.length < 6) {
            localError = "Password must be at least 6 characters."
            return
        }
        if (isSignUp && password != confirmPassword) {
            localError = "Passwords do not match."
            return
        }
        scope.launch {
            if (isSignUp) {
                auth.signUp(email.trim(), password).onSuccess {
                    infoMessage = "Welcome to RockScout! Your account is ready."
                    isSignUp = false
                }
            } else {
                auth.signIn(email.trim(), password)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Slate900,
                        0.6f to Slate800,
                        1.0f to Obsidian,
                    ),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Brand mark
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Citrine, CitrineDeep),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "⛏️",
                    fontSize = 34.sp,
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "RockScout",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextHigh,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (isSignUp) "Create your RockScout account" else "Sign in to RockScout",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(28.dp))

            if (!isPendingVerification) {
            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Filled.Mail, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth().noAutoFocus(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Slate800,
                    unfocusedContainerColor = Slate800,
                    focusedIndicatorColor = Citrine,
                    unfocusedIndicatorColor = StoneLine,
                    focusedLabelColor = CitrineDeep,
                    unfocusedLabelColor = TextMid,
                ),
            )
            Spacer(Modifier.height(12.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                trailingIcon = {
                    SculptedIconButton(
                        icon = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (showPassword) "Hide password" else "Show password",
                        onClick = { showPassword = !showPassword },
                        accent = Citrine,
                        iconTint = TextMid,
                        backgroundColor = Slate800,
                        size = 40.dp,
                    )
                },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().noAutoFocus(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Slate800,
                    unfocusedContainerColor = Slate800,
                    focusedIndicatorColor = Citrine,
                    unfocusedIndicatorColor = StoneLine,
                    focusedLabelColor = CitrineDeep,
                    unfocusedLabelColor = TextMid,
                ),
            )
            if (isSignUp) {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm password") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Slate800,
                        unfocusedContainerColor = Slate800,
                        focusedIndicatorColor = Citrine,
                        unfocusedIndicatorColor = StoneLine,
                        focusedLabelColor = CitrineDeep,
                        unfocusedLabelColor = TextMid,
                    ),
                )
            }

            Spacer(Modifier.height(8.dp))
            val shownError = localError ?: error
            if (shownError != null) {
                Text(
                    text = shownError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            val infoMsg = infoMessage
            if (infoMsg != null) {
                Text(
                    text = infoMsg,
                    color = Aqua,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { submit() },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Citrine,
                    contentColor = Ink,
                ),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = Ink,
                    )
                } else {
                    Text(
                        text = if (isSignUp) "Create account" else "Sign in with RockScout",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            // Toggle sign-up / sign-in
            TextButton(
                onClick = {
                    isSignUp = !isSignUp
                    localError = null
                    infoMessage = null
                },
            ) {
                Text(
                    text = if (isSignUp) "Already have an account? Sign in" else "New to RockScout? Create an account",
                    color = CitrineDeep,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            // ─── Referral code entry ───
            // Collapsible field so a referred user can enter their code before
            // signing in. The code is stored locally and processed after the
            // session is authenticated.
            if (!referralCodeApplied) {
                Spacer(Modifier.height(16.dp))
                TextButton(
                    onClick = { showReferralField = !showReferralField },
                ) {
                    Icon(
                        Icons.Filled.CardGiftcard,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Aqua,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (showReferralField) "Hide referral code" else "Have a referral code?",
                        color = Aqua,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                if (showReferralField) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = referralCode,
                        onValueChange = {
                            referralCode = it.uppercase().filter { c -> c.isLetterOrDigit() || c == '-' }
                        },
                        label = { Text("Referral code (ROCK-XXXXXX)") },
                        leadingIcon = { Icon(Icons.Filled.CardGiftcard, contentDescription = null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth().noAutoFocus(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Slate800,
                            unfocusedContainerColor = Slate800,
                            focusedIndicatorColor = Aqua,
                            unfocusedIndicatorColor = StoneLine,
                            focusedLabelColor = Aqua,
                            unfocusedLabelColor = TextMid,
                            focusedTextColor = TextHigh,
                            unfocusedTextColor = TextHigh,
                        ),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Enter your friend's code, then sign in. You'll get free ID tokens + a full-feature unlock!",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextLow,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            } // end if (!isPendingVerification) — form fields + referral

            // Google (secondary) — native Supabase Auth doesn't ship OAuth in-app
            // without a browser deep-link yet, so this is shown as a disabled
            // "coming soon" path. Email/password is the primary supported flow.
            if (!isPendingVerification) {
                Spacer(Modifier.height(28.dp))

                // Divider
                Text(
                    text = "or",
                    color = TextMid,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { /* TODO: wire Google OAuth via deep-link */ },
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextMid,
                    ),
                ) {
                    Text("Sign in with Google (coming soon)")
                }

                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Made by a rockhounder, for rockhounders",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMid,
                    textAlign = TextAlign.Center,
                )
                // Back button only shown in non-gate mode — in gate mode there's
                // nothing to go back to.
                if (!isGate) {
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onBack) {
                        Text(
                            text = "Back",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CitrineDeep,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            } else {
                // ─── Email Verification Panel ───
                // Shown when sessionStatus is PendingVerification. The user
                // must enter the 6-digit code emailed to them before they can
                // access the app.
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Verify your email",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextHigh,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "We sent a verification email to",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMid,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = pendingEmail ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Citrine,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Tap \u201cVerify My Email\u201d in the email, or enter the code below:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMid,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(16.dp))

                // Code input — single styled field, numeric keyboard, 6 digits
                OutlinedTextField(
                    value = verificationCode,
                    onValueChange = {
                        val filtered = it.filter { c -> c.isDigit() }.take(6)
                        verificationCode = filtered
                        verifyError = null
                    },
                    label = { Text("6-digit code") },
                    leadingIcon = { Icon(Icons.Filled.Mail, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Slate800,
                        unfocusedContainerColor = Slate800,
                        focusedIndicatorColor = Citrine,
                        unfocusedIndicatorColor = StoneLine,
                        focusedLabelColor = CitrineDeep,
                        unfocusedLabelColor = TextMid,
                        focusedTextColor = TextHigh,
                        unfocusedTextColor = TextHigh,
                    ),
                )

                Spacer(Modifier.height(8.dp))
                val vErr = verifyError
                if (vErr != null) {
                    Text(
                        text = vErr,
                        color = Danger,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                val vInfo = verifyInfo
                if (vInfo != null) {
                    Text(
                        text = vInfo,
                        color = Aqua,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(Modifier.height(16.dp))
                // Verify button
                Button(
                    onClick = {
                        if (verificationCode.length != 6) {
                            verifyError = "Please enter the full 6-digit code."
                            return@Button
                        }
                        isVerifying = true
                        verifyError = null
                        verifyInfo = null
                        scope.launch {
                            auth.completeVerification(verificationCode)
                                .onSuccess {
                                    // LaunchedEffect(sessionStatus) will pick up
                                    // Authenticated and dismiss the screen.
                                }
                                .onFailure { e ->
                                    verifyError = e.message ?: "Verification failed"
                                }
                            isVerifying = false
                        }
                    },
                    enabled = !isVerifying && verificationCode.length == 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Citrine,
                        contentColor = Ink,
                    ),
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = Ink,
                        )
                    } else {
                        Text(
                            text = "Verify email",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                // Resend code button with cooldown
                TextButton(
                    onClick = {
                        if (resendCooldown == 0 && !isResending) {
                            isResending = true
                            verifyError = null
                            verifyInfo = null
                            scope.launch {
                                auth.resendVerificationCode()
                                    .onSuccess {
                                        verifyInfo = "New email sent! Check your inbox."
                                        resendCooldown = 60
                                    }
                                    .onFailure { e ->
                                        verifyError = e.message ?: "Failed to resend code"
                                    }
                                isResending = false
                            }
                        }
                    },
                    enabled = resendCooldown == 0 && !isResending,
                ) {
                    if (isResending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = CitrineDeep,
                        )
                    } else if (resendCooldown > 0) {
                        Text(
                            text = "Resend email in ${resendCooldown}s",
                            color = TextLow,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    } else {
                        Text(
                            text = "Resend email",
                            color = CitrineDeep,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                // Use a different email — cancels the pending verification
                // and removes the unverified account.
                TextButton(
                    onClick = {
                        scope.launch {
                            auth.cancelPendingVerification()
                        }
                        verificationCode = ""
                        verifyError = null
                        verifyInfo = null
                        password = ""
                        confirmPassword = ""
                        isSignUp = true
                    },
                ) {
                    Text(
                        text = "Use a different email",
                        color = TextMid,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Made by a rockhounder, for rockhounders",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMid,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

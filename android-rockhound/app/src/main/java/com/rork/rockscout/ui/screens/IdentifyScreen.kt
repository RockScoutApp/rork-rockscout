// RockScout — offline-aware rock & mineral identifier
package com.rork.rockscout.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.SignalCellularOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.CapturedPhoto
import com.rork.rockscout.data.GallerySaver
import com.rork.rockscout.data.ClarificationQuestion
import com.rork.rockscout.data.IdentifyAccess
import com.rork.rockscout.data.IdentifyAccessManager
import com.rork.rockscout.data.ConsumedCredit
import com.rork.rockscout.data.IdentifyApi
import com.rork.rockscout.data.IdentifyCache
import com.rork.rockscout.data.AchievementsRepository
import com.rork.rockscout.data.XpSource
import com.rork.rockscout.data.ImageModerator
import com.rork.rockscout.data.ModerationResult
import com.rork.rockscout.data.IdentifyMatch
import com.rork.rockscout.data.IdentifyResponse
import com.rork.rockscout.data.IapConfig
import com.rork.rockscout.data.PurchaseManager
import com.rork.rockscout.data.PurchaseResult
import com.rork.rockscout.data.Artifact
import com.rork.rockscout.data.ArtifactSpecimens
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.Specimen
import com.rork.rockscout.data.GearGuide
import com.rork.rockscout.data.SpecimenImages
import com.rork.rockscout.data.AssemblageResult
import com.rork.rockscout.data.WebReference
import com.rork.rockscout.data.SpecimenReportPdfExporter
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.GearLinksCard
import com.rork.rockscout.ui.components.FullScreenImageViewer
import com.rork.rockscout.ui.components.InterstitialAdTrigger
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.rememberNetworkOnline
import com.rork.rockscout.ui.theme.Warning
import com.rork.rockscout.ui.components.CelebrationLevel
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.ThankYouCelebration
import com.rork.rockscout.ui.components.rockClassColor
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.MuseumFinderSheet
import com.rork.rockscout.ui.components.ReplyEmailDialog
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.SculptedTextButton
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.Slate700
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID

/** Identify screen background URL. Public so the offline bulk-download registry can include it. */
const val IDENTIFY_BACKGROUND_URL = "https://r2-pub.rork.com/attachments/t5vh4q8xpxmg46mq3j955.jpg"

private enum class ScanState {
    IDLE,
    CAPTURED,
    MODERATING,
    ARTIFACT_CONFIRM, // Artifact-detection pre-pass suspects an artifact
    SCANNING,
    CLARIFY_QUESTIONS,
    CLARIFYING,
    RESULTS,
    ERROR,
    LOCKED,
    REJECTED,
}

@Composable
fun IdentifyScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val purchaseManager = PurchaseManager.instance
    val isPremium by purchaseManager.isPremium.collectAsState()
    val isPurchasing by purchaseManager.isPurchasing.collectAsState()
    val accessManager = IdentifyAccessManager.instance
    val trialUsesRemaining by accessManager.trialUsesRemaining.collectAsState()
    val trialActive by accessManager.trialActive.collectAsState()
    val trialExpired by accessManager.trialExpired.collectAsState()
    val tokenBalance by accessManager.tokenBalance.collectAsState()
    val hasLocationUnlock by accessManager.hasLocationUnlock.collectAsState()

    var state by remember { mutableStateOf(ScanState.IDLE) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var capturedUri by remember { mutableStateOf<Uri?>(null) }
    var capturedBase64 by remember { mutableStateOf("") }
    var matches by remember { mutableStateOf<List<Pair<Specimen, IdentifyMatch>>>(emptyList()) }
    // Parallel artifact match list — populated only when the user confirmed
    // an artifact and the backend ran in "artifacts" search mode. Kept fully
    // separate from the specimen `matches` list so the specimen flow is
    // untouched. When `artifactMatches` is non-empty, the results screen
    // renders ArtifactMatchRow instead of MatchRow and shows the "Identify
    // the rock material too" secondary button.
    var artifactMatches by remember { mutableStateOf<List<Pair<Artifact, IdentifyMatch>>>(emptyList()) }
    var aiSummary by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var clarificationQuestions by remember { mutableStateOf<List<ClarificationQuestion>>(emptyList()) }
    var webReferences by remember { mutableStateOf<List<WebReference>>(emptyList()) }
    var assemblageResult by remember { mutableStateOf<AssemblageResult?>(null) }
    var uncertainArtifact by remember { mutableStateOf(false) }
    var preliminaryMatches by remember { mutableStateOf<List<IdentifyMatch>>(emptyList()) }
    var preliminarySummary by remember { mutableStateOf("") }
    val answers = remember { mutableStateMapOf<String, String>() }
    val customAnswers = remember { mutableStateMapOf<String, String>() }

    // Full-screen viewer state
    var viewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerInitialPage by remember { mutableIntStateOf(0) }

    // PDF report generation — tracks which match index is currently generating
    var generatingReportIndex by remember { mutableIntStateOf(-1) }

    // Temp file URI for camera capture
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    // Celebration overlay — shown after a successful donation
    var celebrationLevel by remember { mutableStateOf<CelebrationLevel?>(null) }

    // Ask an Expert — museum finder + reply email dialog (uncertainty card)
    var showMuseumFinder by remember { mutableStateOf(false) }
    var emailTargetMuseum by remember { mutableStateOf<com.rork.rockscout.data.Museum?>(null) }
    var emailTargetMuseums by remember { mutableStateOf<List<com.rork.rockscout.data.Museum>>(emptyList()) }

    // Rewarded video flow — 2 videos = 1 bonus ID token for free users
    var showRewardedVideo by remember { mutableStateOf(false) }
    var rewardedVideoIndex by remember { mutableStateOf(0) } // 0 = first video, 1 = second
    var rewardedVideoProgress by remember { mutableFloatStateOf(0f) }
    var rewardedVideoComplete by remember { mutableStateOf(false) }

    InterstitialAdTrigger(screenKey = "identify") {
        navController.navigate(Routes.PAYWALL)
    }

    // Compute access state — lock the identifier if no credits are available.
    val accessState = remember(isPremium, trialUsesRemaining, trialActive, tokenBalance) {
        accessManager.accessState(isPremium)
    }

    var moderationReason by remember { mutableStateOf("") }
    var pendingSearchMode by remember { mutableStateOf("rocks") }

    // Determinate staged progress for the identify flow — mirrors the cloud
    // backup bar: a track that fills up with contextual stage text so the
    // user sees tangible progress through moderation, AI analysis, and
    // result-building phases.
    var identifyProgress by remember { mutableFloatStateOf(0f) }
    var identifyStage by remember { mutableStateOf("") }

    // Live network connectivity — the AI identifier requires a signal to reach
    // the Cloudflare Worker backend. When offline, show a notice so the user
    // knows the identifier is unavailable but they can still browse the on-device
    // specimen database (which is fully cached after a bulk download).
    val isOnline by rememberNetworkOnline()

    fun resetAll() {
        state = ScanState.IDLE
        capturedBitmap = null
        capturedUri = null
        capturedBase64 = ""
        matches = emptyList()
        artifactMatches = emptyList()
        aiSummary = ""
        errorMessage = ""
        moderationReason = ""
        clarificationQuestions = emptyList()
        webReferences = emptyList()
        assemblageResult = null
        uncertainArtifact = false
        preliminaryMatches = emptyList()
        preliminarySummary = ""
        answers.clear()
        customAnswers.clear()
    }

    fun startIdentification(searchMode: String = "rocks") {
        val bitmap = capturedBitmap ?: return
        // Gate: consume a credit before firing the AI call.
        val consumedCredit = accessManager.consumeIdentify(isPremium)
        if (consumedCredit == ConsumedCredit.NONE) {
            state = ScanState.LOCKED
            return
        }
        state = ScanState.SCANNING
        errorMessage = ""
        pendingSearchMode = searchMode
        identifyProgress = 0.30f
        identifyStage = "Analyzing with AI vision…"

        scope.launch {
            try {
                val base64 = withContext(Dispatchers.IO) {
                    val resized = resizeBitmap(bitmap, 1536)
                    val baos = ByteArrayOutputStream()
                    resized.compress(Bitmap.CompressFormat.JPEG, 88, baos)
                    val bytes = baos.toByteArray()
                    android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                }
                capturedBase64 = base64
                identifyProgress = 0.40f

                // On-device exact-image cache: if the user is re-identifying the
                // exact same photo (accidental re-submit, re-upload after closing
                // the result), return the cached result instantly with zero AI
                // cost and zero accuracy loss. The cache key is the SHA-256 of the
                // normalized image bytes — a different specimen photo produces a
                // different hash and always falls through to a fresh AI call.
                val imageBytes = android.util.Base64.decode(base64, android.util.Base64.NO_WRAP)
                val imageHash = IdentifyCache.hash(imageBytes)
                val cached = IdentifyCache.get(imageHash)
                val entitlement = when {
                    isPremium -> "premium"
                    else -> "free"
                }
                identifyStage = "Comparing against ${SeedData.allSpecimens.size} known specimens…"
                identifyProgress = 0.55f
                val response = cached ?: IdentifyApi.identify(base64, "image/jpeg", entitlement, pendingSearchMode).also {
                    // Cache only successful, non-error first-pass results. Errors
                    // and clarification re-rank calls are never cached (re-rank
                    // depends on user answers, which are unique each time).
                    if (it.error == null) IdentifyCache.put(imageHash, it)
                }
                identifyProgress = 0.85f
                identifyStage = "Building your results…"
                if (cached != null) {
                    android.util.Log.d("IdentifyScreen", "Identify cache hit — saved an AI call")
                }

                if (response.error != null) {
                    // Refund the consumed credit — the AI call failed, user should not be charged.
                    accessManager.refundIdentify(consumedCredit)
                    errorMessage = response.error
                    state = ScanState.ERROR
                    return@launch
                }

                // ── Artifact vs specimen match resolution ──────────────────────
                // In artifact search mode, matches come back with artifact IDs
                // ("art-...") that exist in ArtifactSpecimens, not SeedData. We
                // resolve them into a parallel `artifactMatches` list and render
                // ArtifactMatchRow instead of MatchRow. The specimen flow is
                // untouched.
                val isArtifactSearch = pendingSearchMode == "artifacts"
                if (isArtifactSearch) {
                    val artifactMap = ArtifactSpecimens.allArtifacts.associateBy { it.id }
                    val matchedArtifacts = response.matches.mapNotNull { match ->
                        artifactMap[match.id]?.let { it to match }
                    }
                    if (matchedArtifacts.isEmpty()) {
                        // Refund — no valid artifact matches returned.
                        accessManager.refundIdentify(consumedCredit)
                        errorMessage = "No matching artifacts found. Try a clearer photo showing the artifact's shape and flaking pattern."
                        state = ScanState.ERROR
                        return@launch
                    }
                    // Save top artifact match as field capture
                    val topArtifact = matchedArtifacts.first()
                    val imageUriStr = capturedUri?.toString() ?: ""
                    AppRepository.instance.addCapture(
                        CapturedPhoto(
                            id = UUID.randomUUID().toString(),
                            specimenId = topArtifact.first.id,
                            specimenEmoji = topArtifact.first.emoji,
                            confidence = topArtifact.second.confidence,
                            timestamp = System.currentTimeMillis(),
                            imageUris = if (imageUriStr.isNotBlank()) listOf(imageUriStr) else emptyList(),
                        )
                    )
                    AchievementsRepository.award(XpSource.IDENTIFY, familyTag = topArtifact.first.id)
                    AchievementsRepository.award(XpSource.CAPTURE)

                    // Artifact mode skips clarification (the backend doesn't
                    // generate clarification questions for artifacts).
                    artifactMatches = matchedArtifacts
                    matches = emptyList()
                    aiSummary = response.summary
                    webReferences = response.webReferences
                    assemblageResult = response.assemblage
                    uncertainArtifact = response.uncertainArtifact
                    state = ScanState.RESULTS
                    return@launch
                }

                val allSpecs = SeedData.allSpecimens
                val specimenMap = allSpecs.associateBy { it.id }
                val matchedList = response.matches.mapNotNull { match ->
                    specimenMap[match.id]?.let { it to match }
                }

                if (matchedList.isEmpty()) {
                    // Refund — no valid matches returned, user should not be charged.
                    accessManager.refundIdentify(consumedCredit)
                    errorMessage = "No matching specimens found. Try a clearer photo showing the specimen's texture and color."
                    state = ScanState.ERROR
                    return@launch
                }

                // Save top match as field capture
                val topMatch = matchedList.first()
                val imageUriStr = capturedUri?.toString() ?: ""
                AppRepository.instance.addCapture(
                    CapturedPhoto(
                        id = UUID.randomUUID().toString(),
                        specimenId = topMatch.first.id,
                        specimenEmoji = topMatch.first.emoji,
                        confidence = topMatch.second.confidence,
                        timestamp = System.currentTimeMillis(),
                        imageUris = if (imageUriStr.isNotBlank()) listOf(imageUriStr) else emptyList(),
                    )
                )

                // Award XP for the identification + field capture.
                // Identify (cached or fresh) earns +10 XP; the capture earns +15 XP.
                // The streak bonus is applied inside AchievementsRepository.
                AchievementsRepository.award(XpSource.IDENTIFY, familyTag = topMatch.first.id)
                AchievementsRepository.award(XpSource.CAPTURE)

                if (response.needsClarification && response.clarificationQuestions.isNotEmpty()) {
                    // Show clarification questions
                    matches = matchedList
                    aiSummary = response.summary
                    clarificationQuestions = response.clarificationQuestions
                    webReferences = response.webReferences
                    assemblageResult = response.assemblage
                    preliminaryMatches = response.matches
                    preliminarySummary = response.summary
                    answers.clear()
                    identifyProgress = 1f
                    state = ScanState.CLARIFY_QUESTIONS
                } else {
                    // Confidence >= 85% or no questions — show results directly
                    matches = matchedList
                    aiSummary = response.summary
                    webReferences = response.webReferences
                    assemblageResult = response.assemblage
                    identifyProgress = 1f
                    state = ScanState.RESULTS
                }
            } catch (e: Exception) {
                // Refund the consumed credit on any failure (timeout, network error, etc.).
                accessManager.refundIdentify(consumedCredit)
                val isTimeout = e is io.ktor.client.plugins.HttpRequestTimeoutException
                errorMessage = if (isTimeout) {
                    "The identification took too long and timed out. Your token has been refunded — please try again."
                } else {
                    "Identification failed: ${e.message ?: "Unknown error"}. Your token has been refunded — check your connection and try again."
                }
                state = ScanState.ERROR
            }
        }
    }

    /**
     * Run the image content moderator before identification. If the image is
     * sexually explicit or contains profanity (other than hell/damn), the
     * capture is rejected with a friendly message and the credit is preserved.
     */
    fun moderateAndIdentify() {
        val bitmap = capturedBitmap ?: return
        // Hard-block the identify flow when the device is offline — the backend
        // AI pipeline can't be reached without a connection. Show a clear error
        // (no token consumed) so the user knows to reconnect and try again.
        if (!isOnline) {
            errorMessage = "No internet connection. The AI identifier needs a signal to reach the rock database and analysis models. " +
                "Your token was not used. Reconnect and try again, or browse the on-device specimen database offline."
            state = ScanState.ERROR
            return
        }
        state = ScanState.MODERATING
        identifyProgress = 0.05f
        identifyStage = "Checking photo…"
        scope.launch {
            try {
                val base64 = withContext(Dispatchers.IO) {
                    val resized = resizeBitmap(bitmap, 1024)
                    val baos = ByteArrayOutputStream()
                    resized.compress(Bitmap.CompressFormat.JPEG, 80, baos)
                    android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.NO_WRAP)
                }
                val verdict = ImageModerator.scan(base64, "image/jpeg")
                identifyProgress = 0.15f
                if (!verdict.allowed) {
                    moderationReason = verdict.reason.ifBlank {
                        "This photo can't be used because it contains content that violates our family-friendly policy."
                    }
                    state = ScanState.REJECTED
                    return@launch
                }
                // Passed moderation — run the artifact-detection pre-pass
                // (Haiku-only, fast, cheap, no credit consumed). If it suspects
                // an artifact, show the confirmation popup before proceeding.
                val detectBase64 = withContext(Dispatchers.IO) {
                    val resized = resizeBitmap(bitmap, 1024)
                    val baos = ByteArrayOutputStream()
                    resized.compress(Bitmap.CompressFormat.JPEG, 80, baos)
                    android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.NO_WRAP)
                }
                val detection = IdentifyApi.detectArtifact(detectBase64, "image/jpeg")
                identifyProgress = 0.25f
                if (detection.isArtifact && detection.confidence >= 70) {
                    // Suspected artifact — ask the user to confirm before routing
                    state = ScanState.ARTIFACT_CONFIRM
                } else {
                    // Not an artifact (or detection failed) — normal rock-ID flow
                    startIdentification()
               }
            } catch (e: Exception) {
                // Moderation hiccup — fail open and proceed to identification.
                startIdentification()
            }
        }
    }

    fun submitClarification() {
        if (capturedBase64.isEmpty()) return
        state = ScanState.CLARIFYING
        identifyProgress = 0.30f
        identifyStage = "Refining identification…"

        scope.launch {
            try {
                val finalAnswers = answers.mapValues { (id, value) ->
                    if (value == "Other") {
                        customAnswers[id]?.takeIf { it.isNotBlank() } ?: "Other"
                    } else {
                        value
                    }
                }
                identifyProgress = 0.50f
                identifyStage = "Cross-referencing your answers…"
                val response = IdentifyApi.clarify(
                    imageBase64 = capturedBase64,
                    mimeType = "image/jpeg",
                    answers = finalAnswers,
                    preliminaryMatches = preliminaryMatches,
                    summary = preliminarySummary,
                )
                identifyProgress = 0.85f
                identifyStage = "Building your results…"

                if (response.error != null) {
                    // If clarification fails, fall back to the preliminary results
                    errorMessage = response.error
                    state = ScanState.RESULTS
                    return@launch
                }

                val allSpecs = SeedData.allSpecimens
                val specimenMap = allSpecs.associateBy { it.id }
                val matchedList = response.matches.mapNotNull { match ->
                    specimenMap[match.id]?.let { it to match }
                }

                if (matchedList.isEmpty()) {
                    // Fall back to preliminary results
                    state = ScanState.RESULTS
                    return@launch
                }

                matches = matchedList
                aiSummary = response.summary
                webReferences = response.webReferences

                // Update the saved capture with the refined top match
                val topMatch = matchedList.first()
                val imageUriStr = capturedUri?.toString() ?: ""
                AppRepository.instance.addCapture(
                    CapturedPhoto(
                        id = UUID.randomUUID().toString(),
                        specimenId = topMatch.first.id,
                        specimenEmoji = topMatch.first.emoji,
                        confidence = topMatch.second.confidence,
                        timestamp = System.currentTimeMillis(),
                        imageUris = if (imageUriStr.isNotBlank()) listOf(imageUriStr) else emptyList(),
                    )
                )

                identifyProgress = 1f
                state = ScanState.RESULTS
            } catch (e: Exception) {
                // Fall back to showing preliminary results
                identifyProgress = 1f
                state = ScanState.RESULTS
            }
        }
    }

    // Camera launcher — auto-runs identification immediately after capture.
    // The captured photo is persisted to the device gallery under a dedicated
    // "RockScout Captures" album, and that persistent gallery URI is used as
    // the field-capture image so it survives across launches and is available
    // in the user's photo library.
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraUri != null) {
            val capturedCameraUri: Uri = cameraUri!!
            scope.launch {
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        com.rork.rockscout.data.ImageUtils.decodeSampledBitmap(context, capturedCameraUri)
                    }
                    if (bitmap != null) {
                        capturedBitmap = bitmap
                        // Save the capture to the device gallery's "RockScout Captures"
                        // album so the photo is available in the user's photo library and
                        // as a stable URI for the field capture card.
                        val galleryUri = withContext(Dispatchers.IO) {
                            GallerySaver.saveBitmap(context.contentResolver, bitmap)
                        }
                        capturedUri = galleryUri ?: cameraUri
                        // Auto-run moderation then identification — camera is already closed at this point
                        moderateAndIdentify()
                    }
                } catch (_: Exception) {
                    errorMessage = "Failed to load the captured photo. Please try again."
                    state = ScanState.ERROR
                }
            }
        }
    }

    // Gallery picker launcher — also auto-runs for a streamlined flow
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Reject files larger than 5 MB before decoding to prevent OOMs
            // and failed identification requests on slow connections.
            if (com.rork.rockscout.data.ImageUtils.isOverUploadLimit(context, uri)) {
                errorMessage = "That image is over 5 MB. Please choose a smaller photo."
                state = ScanState.ERROR
                return@rememberLauncherForActivityResult
            }
            scope.launch {
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        com.rork.rockscout.data.ImageUtils.decodeSampledBitmap(context, uri)
                    }
                    if (bitmap != null) {
                        capturedBitmap = bitmap
                        // Copy to internal storage so the capture image survives restart
                        val persistentUri = withContext(Dispatchers.IO) {
                            com.rork.rockscout.data.ImageUtils.copyUriToInternalStorage(
                                context, uri, "capture_images",
                            )
                        }
                        capturedUri = persistentUri?.let { Uri.parse(it) } ?: uri
                        moderateAndIdentify()
                    }
                } catch (_: Exception) {
                    errorMessage = "Failed to load the selected photo. Please try again."
                    state = ScanState.ERROR
                }
            }
        }
    }

    fun startCamera() {
        val photoFile = File(context.cacheDir, "photos/${UUID.randomUUID()}.jpg")
        photoFile.parentFile?.mkdirs()
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
        cameraUri = uri
        cameraLauncher.launch(uri)
    }

    // Clean up camera file reference
    DisposableEffect(Unit) {
        onDispose { cameraUri = null }
    }

    ScreenScaffold(
        title = "Identify a Specimen",
        onBack = { navController.popBackStack() },
        actions = {
            // Signal-strength indicator — gives instant visual feedback on
            // whether the AI identifier can reach the backend. Green signal bars
            // when online; red crossed-out icon when offline. Updates live as
            // connectivity changes (wifi/cellular drop or reconnect).
            val signalColor = if (isOnline) Success else Warning
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .border(1.dp, signalColor.copy(alpha = 0.45f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (isOnline) Icons.Filled.SignalCellularAlt else Icons.Filled.SignalCellularOff,
                    contentDescription = if (isOnline) "Signal: online" else "Signal: offline",
                    tint = signalColor,
                    modifier = Modifier.size(20.dp),
                )
            }
        },
        background = { innerContent ->
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = IDENTIFY_BACKGROUND_URL,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.42f),
                                    Color.Black.copy(alpha = 0.52f),
                                    Color.Black.copy(alpha = 0.62f),
                                    Color.Black.copy(alpha = 0.72f),
                                )
                            )
                        )
                )
                innerContent()
            }
        },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Offline notice — the AI identifier can't run without a signal.
            // Shown in IDLE state so users see it before they try to capture.
            if (state == ScanState.IDLE && !isOnline) {
                item {
                    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Warning) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.HelpOutline,
                                contentDescription = null,
                                tint = Warning,
                                modifier = Modifier.size(22.dp),
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    "No signal — identifier offline",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    "The AI identifier needs an internet connection to analyze photos. " +
                                        "You can still browse the on-device specimen database, geology guides, and offline maps while offline. Reconnect to identify rocks.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMid,
                                    lineHeight = 18.sp,
                                )
                            }
                        }
                    }
                }
            }

            // Photo preview area
            item {
                PhotoPreview(
                    bitmap = capturedBitmap,
                    state = state,
                    onRetake = { resetAll() },
                    onCamera = { startCamera() },
                )
            }

            // Action buttons
            item {
                when (state) {
                    ScanState.IDLE -> CaptureButtons(
                        onCamera = { startCamera() },
                        onGallery = { galleryLauncher.launch("image/*") },
                    )
                    ScanState.CAPTURED -> Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // Filled dark surface so the discard label stays legible over the photo background.
                        OutlinedButton(
                            onClick = { resetAll() },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .sculpted(shape = RoundedCornerShape(14.dp), accent = Aqua, shadowElevation = 6.dp)
                                .background(Slate800, RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, Aqua),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Aqua),
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Discard", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        SculptedButton(
                            text = "Identify",
                            onClick = { moderateAndIdentify() },
                            accent = Citrine,
                            containerColor = Citrine,
                            textColor = Ink,
                            icon = Icons.Filled.AutoAwesome,
                            modifier = Modifier.weight(1f).height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                        )
                    }
                    ScanState.SCANNING -> Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            identifyStage.ifBlank { "Analyzing with AI vision…" },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { identifyProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Citrine,
                            trackColor = Citrine.copy(alpha = 0.2f),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Comparing against ${SeedData.allSpecimens.size} known specimens — this might take a minute.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextLow,
                            textAlign = TextAlign.Center,
                        )
                    }
                    ScanState.MODERATING -> Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            identifyStage.ifBlank { "Checking photo…" },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { identifyProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Citrine,
                            trackColor = Citrine.copy(alpha = 0.2f),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Scanning for inappropriate content before identifying",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextLow,
                            textAlign = TextAlign.Center,
                        )
                    }
                    ScanState.CLARIFYING -> Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            identifyStage.ifBlank { "Refining identification…" },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { identifyProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Citrine,
                            trackColor = Citrine.copy(alpha = 0.2f),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Cross-referencing your answers with the database",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextLow,
                            textAlign = TextAlign.Center,
                        )
                        }
                    }
                    ScanState.CLARIFY_QUESTIONS -> Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick = { state = ScanState.RESULTS },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .sculpted(shape = RoundedCornerShape(14.dp), accent = Aqua, shadowElevation = 6.dp)
                                .background(Slate800, RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, Aqua),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Aqua),
                        ) {
                            Text("Skip", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        SculptedButton(
                            text = "Refine",
                            onClick = { submitClarification() },
                            accent = Citrine,
                            containerColor = Citrine,
                            textColor = Ink,
                            icon = Icons.Filled.Search,
                            modifier = Modifier.weight(1f).height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            enabled = answers.isNotEmpty() && answers.entries.none { (id, ans) ->
                                ans == "Other" && customAnswers[id].isNullOrBlank()
                            },
                        )
                    }
                    ScanState.RESULTS -> SculptedButton(
                        text = "Identify another specimen",
                        onClick = { resetAll() },
                        accent = Citrine,
                        containerColor = Slate800,
                        textColor = Citrine,
                        icon = Icons.Filled.Refresh,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                    )
                    ScanState.ERROR -> Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick = { resetAll() },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .sculpted(shape = RoundedCornerShape(14.dp), accent = Aqua, shadowElevation = 6.dp)
                                .background(Slate800, RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, Aqua),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Aqua),
                        ) {
                            Text("Start over", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        SculptedButton(
                            text = "Retry",
                            onClick = { moderateAndIdentify() },
                            accent = Citrine,
                            containerColor = Citrine,
                            textColor = Ink,
                            modifier = Modifier.weight(1f).height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                        )
                    }
                    ScanState.REJECTED -> Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick = { resetAll() },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .sculpted(shape = RoundedCornerShape(14.dp), accent = Aqua, shadowElevation = 6.dp)
                                .background(Slate800, RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, Aqua),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Aqua),
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Discard", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        SculptedButton(
                            text = "New photo",
                            onClick = { startCamera() },
                            accent = Citrine,
                            containerColor = Citrine,
                            textColor = Ink,
                            icon = Icons.Filled.CameraAlt,
                            modifier = Modifier.weight(1f).height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                        )
                    }
                    ScanState.ARTIFACT_CONFIRM -> Spacer(Modifier.height(0.dp))
                    ScanState.LOCKED -> Spacer(Modifier.height(0.dp))
                }
            }

            // Artifact confirmation popup — 3 vertically stacked, centered pill buttons
            if (state == ScanState.ARTIFACT_CONFIRM) {
                item {
                    ArtifactConfirmPopup(
                        onYes = { startIdentification(searchMode = "artifacts") },
                        onMaybe = { startIdentification(searchMode = "artifacts") },
                        onNo = { startIdentification(searchMode = "rocks") },
                    )
                }
            }

            // Error message
            if (state == ScanState.ERROR && errorMessage.isNotEmpty()) {
                item {
                    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFE2574C),
                        )
                    }
                }
            }

            // Moderation rejection message — photo blocked by content policy
            if (state == ScanState.REJECTED && moderationReason.isNotEmpty()) {
                item {
                    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFFE2574C)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = null,
                                tint = Color(0xFFE2574C),
                                modifier = Modifier.size(22.dp),
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = moderationReason,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFE2574C),
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }

            // Lock screen — no credits left
            if (state == ScanState.LOCKED) {
                item {
                    IdentifyLockCard(
                        trialActive = trialActive,
                        trialUsesRemaining = trialUsesRemaining,
                        tokenBalance = tokenBalance,
                        isPremium = isPremium,
                        hasLocationUnlock = hasLocationUnlock,
                        onGoPremium = { navController.navigate(Routes.PAYWALL) },
                        onWatchAdsForToken = { showRewardedVideo = true; rewardedVideoIndex = 0; rewardedVideoProgress = 0f; rewardedVideoComplete = false },
                        onPurchaseTokens = { packageId ->
                            val activity = context as? android.app.Activity
                            if (activity != null) {
                                scope.launch {
                                    val result = purchaseManager.purchaseDonation(activity, packageId)
                                    if (result is PurchaseResult.Success) {
                                        val amount = IapConfig.PRESET_DONATIONS.firstOrNull {
                                            it.packageId == packageId
                                        }?.displayAmount?.toDouble() ?: 1.0
                                        celebrationLevel = CelebrationLevel.forAmount(amount)
                                        state = ScanState.IDLE
                                    }
                                }
                            }
                        },
                        isPurchasing = isPurchasing,
                    )
                }
            }

            // Trial status banner (visible in IDLE state for free users)
            if (state == ScanState.IDLE && !isPremium) {
                item {
                    TrialStatusCard(
                        trialActive = trialActive,
                        trialUsesRemaining = trialUsesRemaining,
                        tokenBalance = tokenBalance,
                    )
                }
                // Rewarded-video button is NOT shown in IDLE state.
                // It only appears inside the LOCKED card (see below) when the
                // user has exhausted their trial IDs and has no tokens — and
                // only when no donated unlock is active.
            }

            // Clarification questions
            if (state == ScanState.CLARIFY_QUESTIONS && clarificationQuestions.isNotEmpty()) {
                item {
                    ClarificationIntroCard(topConfidence = matches.firstOrNull()?.second?.confidence ?: 0)
                }
                itemsIndexed(clarificationQuestions) { index, question ->
                    ClarificationQuestionCard(
                        question = question,
                        questionNumber = index + 1,
                        selectedAnswer = answers[question.id],
                        customAnswer = customAnswers[question.id],
                        onSelect = { answer -> answers[question.id] = answer },
                        onCustomAnswerChange = { text -> customAnswers[question.id] = text },
                    )
                }
            }

            // Preliminary results shown during clarification
            if (state == ScanState.CLARIFY_QUESTIONS && matches.isNotEmpty()) {
                item {
                    Text(
                        "PRELIMINARY MATCHES",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMid,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                itemsIndexed(matches) { index, (spec, match) ->
                    AnimatedVisibility(visible = true, enter = fadeIn()) {
                        MatchRow(
                            spec = spec,
                            match = match,
                            isTop = index == 0,
                            onClick = { navController.navigate(Routes.specimen(spec.id)) },
                            onPhotoClick = { urls, page ->
                                viewerUrls = urls
                                viewerInitialPage = page
                            },
                        )
                    }
                }
            }

            // AI summary
            if (state == ScanState.RESULTS && aiSummary.isNotEmpty()) {
                item {
                    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = Citrine,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "AI ANALYSIS",
                                style = MaterialTheme.typography.labelMedium,
                                color = Citrine,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = aiSummary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextMid,
                            lineHeight = 22.sp,
                        )
                    }
                }
            }

            // Assemblage analysis card
            if (state == ScanState.RESULTS && assemblageResult != null && assemblageResult!!.components.isNotEmpty()) {
                item {
                    AssemblageCard(assemblage = assemblageResult!!)
                }
            }

            // Web references
            if (state == ScanState.RESULTS && webReferences.isNotEmpty()) {
                item {
                    WebReferencesCard(references = webReferences)
                }
            }

            // ── Artifact match results ──────────────────────────────────────
            // Rendered when the user confirmed an artifact and the backend ran
            // in "artifacts" search mode. Fully separate from the specimen
            // results block below — the two are mutually exclusive.
            if (state == ScanState.RESULTS && artifactMatches.isNotEmpty()) {
                // Uncertainty notification — only shown when the ENTIRE pipeline
                // (database + Haiku + Sonnet + Gemini + web search) couldn't
                // produce a reasonably confident match. Lets the user know the
                // object could not be fully distinguished between an actual
                // artifact and a similar-shaped natural rock.
                if (uncertainArtifact) {
                    item {
                        DarkCard(modifier = Modifier.fillMaxWidth(), accent = Warning) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.HelpOutline,
                                    contentDescription = null,
                                    tint = Warning,
                                    modifier = Modifier.size(22.dp),
                                )
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        "Hard to distinguish",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        "Our AI ran the full analysis pipeline and couldn't confidently tell whether this is an actual artifact or a similarly shaped natural rock. The matches below are our best guesses — consider consulting a local archaeologist for confirmation.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DarkTextLow,
                                        lineHeight = 18.sp,
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    SculptedButton(
                                        text = "Ask an Expert",
                                        onClick = { showMuseumFinder = true },
                                        accent = Warning,
                                        containerColor = Warning,
                                        textColor = Color.Black,
                                        icon = Icons.Filled.HelpOutline,
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    Text(
                        "BEST ARTIFACT MATCHES",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMid,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                itemsIndexed(artifactMatches) { index, (artifact, match) ->
                    AnimatedVisibility(visible = true, enter = fadeIn()) {
                        ArtifactMatchRow(
                            artifact = artifact,
                            match = match,
                            isTop = index == 0,
                            onClick = { navController.navigate(Routes.artifactDetail(artifact.id)) },
                            onPhotoClick = { urls, page ->
                                viewerUrls = urls
                                viewerInitialPage = page
                            },
                            onGenerateReport = {
                                generatingReportIndex = index
                                scope.launch {
                                    val reportData = SpecimenReportPdfExporter.ReportData(
                                        capturedBitmap = capturedBitmap,
                                        capturedUri = capturedUri,
                                        matches = artifactMatches.map { (a, m) ->
                                            SpecimenReportPdfExporter.MatchEntry(a.name, m.confidence, m.reasoning)
                                        },
                                        aiSummary = aiSummary,
                                        assemblage = assemblageResult,
                                        webReferences = webReferences,
                                        locationText = "",
                                        isArtifact = true,
                                    )
                                    val result = SpecimenReportPdfExporter.export(context, reportData)
                                    generatingReportIndex = -1
                                    if (result == null) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Could not generate the report. Try again.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            isGeneratingReport = generatingReportIndex == index,
                        )
                    }
                }
                // Secondary pass — identify the rock material the artifact is
                // made from (chert, obsidian, flint, etc.). Reuses the captured
                // photo and runs the normal rock-ID flow without consuming a
                // second credit from the user.
                item {
                    Spacer(Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = {
                            // Run a rock-material pass on the same photo.
                            // Reset artifact matches and run the normal flow.
                            artifactMatches = emptyList()
                            startIdentification(searchMode = "rocks")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Citrine,
                        ),
                    ) {
                        Icon(
                            Icons.Filled.Diamond,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Identify the rock material too",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                item {
                    GearLinksCard(
                        sectionTitle = "Tools to confirm this ID at home",
                        items = GearGuide.confirmIdGear,
                        accent = Citrine,
                    )
                }
            }

            // Match results
            if (state == ScanState.RESULTS && matches.isNotEmpty()) {
                item {
                    Text(
                        "BEST MATCHES",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMid,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                itemsIndexed(matches) { index, (spec, match) ->
                    AnimatedVisibility(visible = true, enter = fadeIn()) {
                        MatchRow(
                            spec = spec,
                            match = match,
                            isTop = index == 0,
                            onClick = { navController.navigate(Routes.specimen(spec.id)) },
                            onPhotoClick = { urls, page ->
                                viewerUrls = urls
                                viewerInitialPage = page
                            },
                            onGenerateReport = {
                                generatingReportIndex = index
                                scope.launch {
                                    val reportData = SpecimenReportPdfExporter.ReportData(
                                        capturedBitmap = capturedBitmap,
                                        capturedUri = capturedUri,
                                        matches = matches.map { (s, m) ->
                                            SpecimenReportPdfExporter.MatchEntry(s.name, m.confidence, m.reasoning)
                                        },
                                        aiSummary = aiSummary,
                                        assemblage = assemblageResult,
                                        webReferences = webReferences,
                                        locationText = "",
                                        isArtifact = false,
                                    )
                                    val result = SpecimenReportPdfExporter.export(context, reportData)
                                    generatingReportIndex = -1
                                    if (result == null) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Could not generate the report. Try again.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            isGeneratingReport = generatingReportIndex == index,
                        )
                    }
                }
                item {
                    GearLinksCard(
                        sectionTitle = "Tools to confirm this ID at home",
                        items = GearGuide.confirmIdGear,
                        accent = Citrine,
                    )
                }
            }
        }

        // Full-screen image viewer overlay
        if (viewerUrls.isNotEmpty()) {
            FullScreenImageViewer(
                imageUrls = viewerUrls,
                initialPage = viewerInitialPage,
                onDismiss = { viewerUrls = emptyList() },
            )
        }

        // Thank-you celebration overlay (after a successful donation)
        celebrationLevel?.let { level ->
            ThankYouCelebration(
                level = level,
                onDismiss = { celebrationLevel = null },
            )
        }

        // Ask an Expert — museum finder bottom sheet (shown from uncertainty card)
        if (showMuseumFinder) {
            MuseumFinderSheet(
                onDismiss = { showMuseumFinder = false },
                onEmailExpert = { museum ->
                    emailTargetMuseum = museum
                    emailTargetMuseums = listOf(museum)
                    showMuseumFinder = false
                },
                onEmailExperts = { museums ->
                    emailTargetMuseums = museums
                    emailTargetMuseum = museums.firstOrNull()
                    showMuseumFinder = false
                },
                artifactMatchNames = artifactMatches.map { it.first.name },
                artifactConfidences = artifactMatches.map { it.second.confidence },
                aiSummary = aiSummary,
            )
        }

        // Reply email dialog — shown after user picks a museum to email
        emailTargetMuseum?.let { museum ->
            ReplyEmailDialog(
                museum = museum,
                museums = emailTargetMuseums,
                onDismiss = { emailTargetMuseum = null },
                artifactMatchNames = artifactMatches.map { it.first.name },
                artifactConfidences = artifactMatches.map { it.second.confidence },
                aiSummary = aiSummary,
                capturedBitmap = capturedBitmap,
            )
        }

        // Rewarded video overlay — plays 2 simulated ads, grants 1 token on completion
        if (showRewardedVideo) {
            RewardedVideoOverlay(
                videoIndex = rewardedVideoIndex,
                progress = rewardedVideoProgress,
                onComplete = {
                    if (rewardedVideoIndex == 0) {
                        rewardedVideoIndex = 1
                        rewardedVideoProgress = 0f
                    } else {
                        accessManager.addTokens(1)
                        rewardedVideoComplete = true
                        showRewardedVideo = false
                        rewardedVideoIndex = 0
                        rewardedVideoProgress = 0f
                        state = ScanState.IDLE
                    }
                },
                onDismiss = {
                    showRewardedVideo = false
                    rewardedVideoIndex = 0
                    rewardedVideoProgress = 0f
                },
            )
        }
        } // close Box
    }
}

@Composable
private fun PhotoPreview(bitmap: Bitmap?, state: ScanState, onRetake: () -> Unit, onCamera: () -> Unit) {
    val showBitmap = state == ScanState.CAPTURED ||
        state == ScanState.MODERATING ||
        state == ScanState.ARTIFACT_CONFIRM ||
        state == ScanState.SCANNING ||
        state == ScanState.CLARIFY_QUESTIONS ||
        state == ScanState.CLARIFYING ||
        state == ScanState.RESULTS ||
        state == ScanState.ERROR ||
        state == ScanState.REJECTED

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(listOf(Slate800, Slate900))
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null && showBitmap) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Captured specimen photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            // Retake button overlay
            if (state == ScanState.CAPTURED) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .sculpted(
                            shape = CircleShape,
                            accent = Citrine,
                            shadowElevation = 4.dp,
                            circular = true,
                            onClick = onRetake,
                        )
                        .clip(CircleShape)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Remove photo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            // Moderating overlay — scanning for inappropriate content
            if (state == ScanState.MODERATING) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.40f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 32.dp),
                    ) {
                        Text(
                            identifyStage.ifBlank { "Checking photo…" },
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { identifyProgress },
                            modifier = Modifier
                                .fillMaxWidth(0.70f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Citrine,
                            trackColor = Color(0x33FFFFFF),
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Scanning for inappropriate content before identifying",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            // Rejected overlay — content policy violation blocked the photo
            if (state == ScanState.REJECTED) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.70f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "\u26d4",
                            style = MaterialTheme.typography.displayMedium,
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Photo blocked",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            // Scanning overlay
            if (state == ScanState.SCANNING) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 32.dp),
                    ) {
                        Text(
                            identifyStage.ifBlank { "Scanning specimen…" },
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { identifyProgress },
                            modifier = Modifier
                                .fillMaxWidth(0.70f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Citrine,
                            trackColor = Color(0x33FFFFFF),
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "This might take a minute. The search is extensive.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            // Clarifying overlay
            if (state == ScanState.CLARIFYING) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 32.dp),
                    ) {
                        Text(
                            identifyStage.ifBlank { "Refining results…" },
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { identifyProgress },
                            modifier = Modifier
                                .fillMaxWidth(0.70f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Citrine,
                            trackColor = Color(0x33FFFFFF),
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Cross-referencing your answers with the database",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        } else {
            // Empty state
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(Citrine.copy(alpha = 0.18f))
                        .glowingBorder(3.dp, Citrine, CircleShape)
                        .clickable(enabled = state == ScanState.IDLE, onClick = onCamera),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.CameraAlt,
                        contentDescription = "Take photo",
                        tint = Citrine,
                        modifier = Modifier.size(44.dp),
                    )
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Tap the camera to take a photo, or choose one from your gallery",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextMid,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "For best results, place the specimen on a neutral background in good natural light. Make sure texture and crystal structure are visible.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMid,
                    textAlign = TextAlign.Center,
                )
            }
        }
        // AI Vision badge
        if (state != ScanState.SCANNING && state != ScanState.CLARIFYING) {
            TagChip(
                "AI VISION",
                color = Citrine,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
            )
        }
    }
}

@Composable
private fun CaptureButtons(onCamera: () -> Unit, onGallery: () -> Unit) {
    // Camera button lives inside the identifier tile; the row below now only
    // hosts the Gallery button, centered in the same row.
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        SculptedButton(
            text = "Gallery",
            onClick = onGallery,
            accent = Citrine,
            containerColor = Citrine,
            textColor = Ink,
            icon = Icons.Filled.PhotoLibrary,
            modifier = Modifier.fillMaxWidth(0.6f).height(54.dp),
            shape = RoundedCornerShape(14.dp),
        )
    }
}

/**
 * Reusable "Watch 2 short ads for 1 more ID" button.
 * Shown below the ID tile (IDLE state) and inside the lock card (LOCKED state)
 * for free users — premium/pro never see it.
 */
@Composable
private fun RewardedVideoButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .sculpted(shape = RoundedCornerShape(14.dp), accent = Amethyst, shadowElevation = 5.dp, onClick = onClick)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Amethyst.copy(alpha = 0.25f), Citrine.copy(alpha = 0.15f))
                )
            )
            .glowingBorder(3.dp, Amethyst.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Amethyst.copy(alpha = 0.35f))
                    .glowingBorder(2.dp, Amethyst.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.PlayCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Watch 2 short ads for 1 more ID",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Free · no purchase needed",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFD8D2C3),
                )
            }
        }
    }
}

@Composable
private fun ClarificationIntroCard(topConfidence: Int) {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.HelpOutline,
                contentDescription = null,
                tint = Citrine,
                modifier = Modifier.size(22.dp),
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    "Help us narrow it down",
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "Top match is ${topConfidence}% confident. Answer a few quick questions to improve accuracy.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextLow,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ClarificationQuestionCard(
    question: ClarificationQuestion,
    questionNumber: Int,
    selectedAnswer: String?,
    customAnswer: String?,
    onSelect: (String) -> Unit,
    onCustomAnswerChange: (String) -> Unit,
) {
    val options = remember(question.options) { question.options + "Other" }
    val isOtherSelected = selectedAnswer == "Other"
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Citrine.copy(alpha = 0.15f))
                    .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "$questionNumber",
                    style = MaterialTheme.typography.labelMedium,
                    color = Citrine,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                question.question,
                style = MaterialTheme.typography.titleSmall,
                color = DarkTextHigh,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(12.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            options.forEach { option ->
                val isSelected = selectedAnswer == option
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) Citrine.copy(alpha = 0.25f)
                            else Slate700
                        )
                        .glowingBorder(
                            width = if (isSelected) 1.5.dp else 0.dp,
                            color = if (isSelected) Citrine else Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onSelect(option) }
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                ) {
                    Text(
                        option,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextHigh,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    )
                }
            }
        }
        if (isOtherSelected) {
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = customAnswer ?: "",
                onValueChange = onCustomAnswerChange,
                label = { Text("Your answer") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = false,
                minLines = 2,
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextHigh,
                    unfocusedTextColor = TextHigh,
                    focusedContainerColor = Slate800,
                    unfocusedContainerColor = Slate800,
                    focusedBorderColor = Citrine,
                    unfocusedBorderColor = Slate700,
                    focusedLabelColor = Citrine,
                    unfocusedLabelColor = TextMid,
                    cursorColor = Citrine,
                ),
            )
        }
    }
}

@Composable
private fun WebReferencesCard(references: List<WebReference>) {
    val uriHandler = LocalUriHandler.current

    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Language,
                contentDescription = null,
                tint = Citrine,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "WEB CROSS-REFERENCE",
                style = MaterialTheme.typography.labelMedium,
                color = Citrine,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "Verified against internet mineralogy sources",
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextLow,
        )
        Spacer(Modifier.height(12.dp))
        references.forEach { ref ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (ref.url.isNotEmpty()) {
                            try {
                                uriHandler.openUri(ref.url)
                            } catch (_: Exception) {
                                // Ignore navigation errors
                            }
                        }
                    }
                    .padding(vertical = 6.dp),
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            ref.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextHigh,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                        )
                        if (ref.source.isNotEmpty()) {
                            Spacer(Modifier.width(8.dp))
                            TagChip(ref.source, color = Aqua, filled = true, textColor = Ink, modifier = Modifier)
                        }
                    }
                    if (ref.snippet.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            ref.snippet,
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextLow,
                            maxLines = 3,
                            lineHeight = 18.sp,
                        )
                    }
                }
            }
            if (ref != references.last()) {
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFE0DCD0)),
                )
            }
        }
    }
}

@Composable
private fun MatchRow(
    spec: Specimen,
    match: IdentifyMatch,
    isTop: Boolean,
    onClick: () -> Unit,
    onPhotoClick: (List<String>, Int) -> Unit = { _, _ -> },
    onGenerateReport: () -> Unit = {},
    isGeneratingReport: Boolean = false,
) {
    val accent = rockClassColor(spec.rockClass)
    val imageUrls = SpecimenImages.urls[spec.id] ?: spec.imageUrls

    DarkCard(
        accent = accent,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        contentPadding = PaddingValues(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Specimen photo (or emoji fallback)
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.radialGradient(listOf(accent.copy(alpha = 0.32f), Color(0xFF1A1812)))
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (imageUrls.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrls.first(),
                        contentDescription = "Photo of ${spec.name}",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onPhotoClick(imageUrls, 0) },
                        contentScale = ContentScale.Crop,
                    )
                    // Photo count badge
                    if (imageUrls.size > 1) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(3.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color.Black.copy(alpha = 0.55f))
                                .padding(horizontal = 5.dp, vertical = 2.dp),
                        ) {
                            Text(
                                "${imageUrls.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                            )
                        }
                    }
                } else {
                    Text(text = spec.emoji, style = MaterialTheme.typography.headlineSmall)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Name is weighted + ellipsised so a long specimen name can
                    // never push the confidence pill or the top-match sparkle off
                    // the right edge of the row.
                    Text(
                        spec.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (isTop) {
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = Citrine,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    // Confidence pill — shows how certain the AI is of this match,
                    // right next to the specimen name so users see it at a glance.
                    // Color-coded: green >=80, amber >=60, red otherwise.
                    val confColor = when {
                        match.confidence >= 80 -> Success
                        match.confidence >= 60 -> Citrine
                        else -> Warning
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(confColor.copy(alpha = 0.18f))
                            .border(1.dp, confColor.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                    ) {
                        Text(
                            "${match.confidence}% match",
                            style = MaterialTheme.typography.labelSmall,
                            color = confColor,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Text(spec.category, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                Spacer(Modifier.height(6.dp))
                if (match.reasoning.isNotEmpty()) {
                    Text(
                        match.reasoning,
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                        maxLines = 2,
                        minLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        lineHeight = 18.sp,
                    )
                } else {
                    Text(
                        "",
                        style = MaterialTheme.typography.bodySmall,
                        minLines = 2,
                    )
                }
                Spacer(Modifier.height(6.dp))
                ConfidenceBar(match.confidence, accent)
            }
            Spacer(Modifier.width(10.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${match.confidence}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isTop) Success else DarkTextMid,
                )
                if (isGeneratingReport) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp).padding(top = 4.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        Icons.Filled.Description,
                        contentDescription = "Generate PDF report",
                        tint = accent,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable(onClick = onGenerateReport)
                            .padding(2.dp),
                    )
                }
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = DarkTextMid)
            }
        }
    }
}

/** Artifact match row — visually parallel to [MatchRow] but reads from the
 *  [Artifact] model. Renders the artifact image, name, family/sub-family,
 *  confidence pill, reasoning, and confidence bar. Tapping navigates to the
 *  artifact detail screen.
 *
 *  Kept self-contained so the specimen [MatchRow] and its call sites are
 *  completely untouched. */
@Composable
private fun ArtifactMatchRow(
    artifact: Artifact,
    match: IdentifyMatch,
    isTop: Boolean,
    onClick: () -> Unit,
    onPhotoClick: (List<String>, Int) -> Unit = { _, _ -> },
    onGenerateReport: () -> Unit = {},
    isGeneratingReport: Boolean = false,
) {
    val accent = Color(artifact.accentHex)
    val imageUrls = listOf(artifact.imageUrl)

    DarkCard(
        accent = accent,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        contentPadding = PaddingValues(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Artifact image
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.radialGradient(listOf(accent.copy(alpha = 0.32f), Color(0xFF1A1812)))
                    ),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = artifact.imageUrl,
                    contentDescription = "Image of ${artifact.name}",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onPhotoClick(imageUrls, 0) },
                    contentScale = ContentScale.Crop,
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        artifact.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (isTop) {
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = Citrine,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    val confColor = when {
                        match.confidence >= 80 -> Success
                        match.confidence >= 60 -> Citrine
                        else -> Warning
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(confColor.copy(alpha = 0.18f))
                            .border(1.dp, confColor.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                    ) {
                        Text(
                            "${match.confidence}% match",
                            style = MaterialTheme.typography.labelSmall,
                            color = confColor,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Text(
                    "${artifact.family} \u00b7 ${artifact.subFamily}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(6.dp))
                if (match.reasoning.isNotEmpty()) {
                    Text(
                        match.reasoning,
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                        maxLines = 2,
                        minLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        lineHeight = 18.sp,
                    )
                } else {
                    Text("", style = MaterialTheme.typography.bodySmall, minLines = 2)
                }
                Spacer(Modifier.height(6.dp))
                ConfidenceBar(match.confidence, accent)
            }
            Spacer(Modifier.width(10.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${match.confidence}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isTop) Success else DarkTextMid,
                )
                if (isGeneratingReport) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp).padding(top = 4.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        Icons.Filled.Description,
                        contentDescription = "Generate PDF report",
                        tint = accent,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable(onClick = onGenerateReport)
                            .padding(2.dp),
                    )
                }
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = DarkTextMid)
            }
        }
    }
}

@Composable
private fun ConfidenceBar(confidence: Int, accent: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color(0xFF2A313C)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(confidence / 100f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(accent),
        )
    }
}

/** Resize a bitmap to fit within maxDimension while maintaining aspect ratio. */
private fun resizeBitmap(bitmap: Bitmap, maxDimension: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    if (width <= maxDimension && height <= maxDimension) return bitmap

    val ratio = minOf(maxDimension.toFloat() / width, maxDimension.toFloat() / height)
    val newWidth = (width * ratio).toInt()
    val newHeight = (height * ratio).toInt()
    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IdentifyLockCard(
    trialActive: Boolean,
    trialUsesRemaining: Int,
    tokenBalance: Int,
    isPremium: Boolean,
    hasLocationUnlock: Boolean,
    onGoPremium: () -> Unit,
    onWatchAdsForToken: () -> Unit,
    onPurchaseTokens: (String) -> Unit,
    isPurchasing: Boolean,
) {
    val donationPresets = IapConfig.PRESET_DONATIONS
    val allDonationPackages = PurchaseManager.instance.allDonationPackages
    var dropdownExpanded by remember { mutableStateOf(false) }
    var selectedPresetIndex by remember { mutableStateOf(0) }

    val transition = rememberInfiniteTransition(label = "lockGlow")
    val glow by transition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(tween(2400), RepeatMode.Reverse),
        label = "lockGlowAlpha",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = RoundedCornerShape(24.dp), accent = Citrine, shadowElevation = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .glowingBorder(3.dp, Citrine.copy(alpha = 0.55f), RoundedCornerShape(24.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Citrine.copy(alpha = 0.22f + glow), Citrine.copy(alpha = 0.05f), Color.Transparent)
                    )
                ),
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Lock icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Citrine.copy(alpha = 0.35f), Citrine.copy(alpha = 0.10f))
                        )
                    )
                    .glowingBorder(3.dp, Citrine.copy(alpha = 0.60f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = null,
                    tint = Citrine,
                    modifier = Modifier.size(34.dp),
                )
            }
            Spacer(Modifier.height(18.dp))
            Text(
                text = if (trialActive && trialUsesRemaining == 0)
                    "Free identifies used up"
                else
                    "Free trial ended",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = if (trialActive && trialUsesRemaining == 0)
                    "You’ve used all 5 free identifies from your trial week. Watch 2 short ads for another ID, donate for more tokens, or go Premium for unlimited access."
                else
                    "Your 1-week free trial of 5 identifies is over. Watch 2 short ads for another ID, donate for tokens, or subscribe for unlimited identifies + ad-free access.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFF5F2EA),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
            )

            if (tokenBalance > 0) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "You have $tokenBalance token${if (tokenBalance > 1) "s" else ""} available — tap Identify to use one.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Success,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
            }

            // Rewarded video button — watch 2 short ads for 1 bonus ID.
            // Hidden during donated unlock periods (the user already has tokens
            // and full-feature access from their donation).
            if (!hasLocationUnlock) {
                Spacer(Modifier.height(18.dp))
                RewardedVideoButton(onClick = onWatchAdsForToken)
                Spacer(Modifier.height(22.dp))
            } else {
                Spacer(Modifier.height(22.dp))
            }

            // Donation / token-grant dropdown
            Text(
                text = "DONATE FOR IDENTIFIER TOKENS",
                style = MaterialTheme.typography.labelMedium,
                color = Citrine,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                val selected = donationPresets[selectedPresetIndex]
                val pkg = allDonationPackages[selected.packageId]
                val priceLabel = "$${selected.displayAmount}"
                val locLabel = com.rork.rockscout.data.IapConfig.donationLocationLabel(selected.packageId)
                val locTag = if (locLabel.isNotEmpty()) " · $locLabel" else ""
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .glowingBorder(
                            1.dp,
                            if (dropdownExpanded) Citrine
                            else Citrine.copy(alpha = 0.40f),
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
                                text = "${selected.tokenGrant} tokens · $priceLabel$locTag",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "Choose a donation tier",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFD8D2C3),
                            )
                        }
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            contentDescription = if (dropdownExpanded) "Collapse" else "Expand",
                            tint = Citrine,
                            modifier = Modifier.size(28.dp),
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
                        .glowingBorder(2.dp, Citrine.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                ) {
                    donationPresets.forEachIndexed { index, option ->
                        val pkgItem = allDonationPackages[option.packageId]
                        val displayPrice = "$${option.displayAmount}"
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(
                                        text = "${option.tokenGrant} tokens",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (index == selectedPresetIndex) Citrine else Color.White,
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        text = displayPrice,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFB5AE9E),
                                    )
                                    val locLabel = com.rork.rockscout.data.IapConfig.donationLocationLabel(option.packageId)
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
                                            tint = Citrine,
                                            modifier = Modifier.size(18.dp),
                                        )
                                    }
                                }
                            },
                            onClick = {
                                selectedPresetIndex = index
                                dropdownExpanded = false
                            },
                            colors = androidx.compose.material3.MenuDefaults.itemColors(
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
            Spacer(Modifier.height(14.dp))
            if (isPurchasing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator(
                        color = Citrine,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp),
                    )
                }
            } else {
                SculptedButton(
                    text = "Donate for Extra Identifier Tokens and More!",
                    onClick = {
                        onPurchaseTokens(donationPresets[selectedPresetIndex].packageId)
                    },
                    accent = Citrine,
                    containerColor = Citrine,
                    textColor = Ink,
                    icon = Icons.Filled.Favorite,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                )
            }

            Spacer(Modifier.height(14.dp))
            // Go Premium CTA
            SculptedOutlinedButton(
                text = "Go Premium · $5.99/mo",
                onClick = onGoPremium,
                accent = Citrine,
                textColor = Citrine,
                icon = Icons.Filled.Star,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
            )
        }
    }
}

@Composable
private fun TrialStatusCard(
    trialActive: Boolean,
    trialUsesRemaining: Int,
    tokenBalance: Int,
) {
    val accent = if (trialActive && trialUsesRemaining > 0) Success else Citrine
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = RoundedCornerShape(18.dp), accent = accent, shadowElevation = 5.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16))
                )
            )
            .glowingBorder(2.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
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
                    Icons.Filled.CameraAlt,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (trialActive && trialUsesRemaining > 0)
                        "Free trial: $trialUsesRemaining of ${com.rork.rockscout.data.IdentifyAccessManager.Companion.MAX_TRIAL_USES_PER_WEEK_PUBLIC} identifies left this week"
                    else if (trialActive)
                        "Free identifies used up this week — resets Monday 12:01 AM EST"
                    else
                        "Free trial ended — tokens, a donation, or Premium required to identify",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
                if (tokenBalance > 0) {
                    Text(
                        text = "$tokenBalance token${if (tokenBalance > 1) "s" else ""} in your bank",
                        style = MaterialTheme.typography.labelMedium,
                        color = Citrine,
                    )
                }
            }
        }
    }
}

/**
 * Simulated rewarded-video overlay (cloud-emulator placeholder).
 *
 * In production this would be a Google AdMob RewardedAd. In the cloud
 * emulator there's no real ad SDK, so we show a placeholder ad card with a
 * progress bar that auto-completes after a few seconds. The user must watch
 * the full duration to get credit — they can skip via the dismiss button,
 * but skipping cancels the reward.
 */
@Composable
private fun RewardedVideoOverlay(
    videoIndex: Int,
    progress: Float,
    onComplete: () -> Unit,
    onDismiss: () -> Unit,
) {
    // Intercept back press so it dismisses the rewarded video overlay
    // instead of popping the entire Identify screen from the nav stack.
    BackHandler { onDismiss() }

    val adContents = remember {
        listOf(
            "Premium Rockhound Gear — shop handpicked field kits online.",
            "Free shipping on rock hammers and loupes this week only.",
        )
    }
    val adText = adContents.getOrElse(videoIndex) { "Your ad here." }
    val transition = rememberInfiniteTransition(label = "rewardedGlow")
    val glow by transition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.50f,
        animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Reverse),
        label = "rewardedGlowAlpha",
    )
    var currentProgress by remember(videoIndex) { mutableFloatStateOf(0f) }
    LaunchedEffect(videoIndex) {
        currentProgress = 0f
        val durationFrames = 80
        for (i in 1..durationFrames) {
            kotlinx.coroutines.delay(50)
            currentProgress = i.toFloat() / durationFrames
        }
        onComplete()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.88f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .sculpted(shape = RoundedCornerShape(20.dp), accent = Amethyst, shadowElevation = 6.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                    )
                )
                .glowingBorder(3.dp, Amethyst.copy(alpha = 0.55f + glow * 0.4f), RoundedCornerShape(20.dp))
                .padding(20.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "AD · ${videoIndex + 1} of 2",
                    style = MaterialTheme.typography.labelSmall,
                    color = Amethyst,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Amethyst.copy(alpha = 0.20f), Aqua.copy(alpha = 0.15f))
                            )
                        )
                        .glowingBorder(2.dp, Amethyst.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = adText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                }
                Spacer(Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF3A3830)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(currentProgress)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Amethyst),
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Watch the full ad to earn 1 ID token",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFD8D2C3),
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(12.dp))
                SculptedTextButton(
                    text = "Skip (no reward)",
                    onClick = onDismiss,
                    accent = Color(0xFF5A5850),
                    textColor = Color(0xFFB5AFA4),
                    shape = RoundedCornerShape(10.dp),
                )
            }
        }
    }
}

/** Assemblage analysis card — shows host rock + mineral component breakdown
 *  with colored percentage bars when the specimen is a multi-mineral assemblage. */
@Composable
private fun AssemblageCard(assemblage: AssemblageResult) {
    val componentColors = listOf(
        Color(0xFF6FA8C7), Color(0xFFE8A33D), Color(0xFF5CC98C),
        Color(0xFF9B7BD8), Color(0xFFE2574C), Color(0xFF44AACC),
    )
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFF9B7BD8)) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Diamond,
                    contentDescription = null,
                    tint = Color(0xFF9B7BD8),
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "ASSEMBLAGE ANALYSIS",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF9B7BD8),
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Host rock: ${assemblage.hostRock}",
                style = MaterialTheme.typography.titleSmall,
                color = DarkTextHigh,
                fontWeight = FontWeight.SemiBold,
            )
            if (assemblage.summary.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    assemblage.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                )
            }
            Spacer(Modifier.height(12.dp))
            assemblage.components.forEachIndexed { idx, component ->
                val color = componentColors[idx % componentColors.size]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                ) {
                    Text(
                        component.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        "${component.percentage}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = color,
                        fontWeight = FontWeight.Bold,
                    )
                }
                // Percentage bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.08f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(component.percentage / 100f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(color),
                    )
                }
                if (component.evidence.isNotBlank()) {
                    Text(
                        component.evidence,
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextMid,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                if (idx < assemblage.components.size - 1) {
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

/**
 * Artifact confirmation popup — 3 vertically stacked, centered pill buttons.
 * Shown when the AI artifact-detection pre-pass suspects the photo contains
 * an artifact (knapped stone tool, point, bead, etc.). The user confirms
 * before the full identify runs.
 *
 * - Yes (warm clay accent, primary) → searches artifacts first
 * - Maybe? (clay accent, secondary) → searches artifacts first
 * - No (neutral / Aqua accent) → normal rock-ID flow
 */
@Composable
private fun ArtifactConfirmPopup(
    onYes: () -> Unit,
    onMaybe: () -> Unit,
    onNo: () -> Unit,
) {
    val clayAccent = Color(0xFFB87333)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Question header
        DarkCard(
            modifier = Modifier.fillMaxWidth(),
            accent = clayAccent,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    Icons.Filled.HelpOutline,
                    contentDescription = null,
                    tint = clayAccent,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        "Is this an artifact?",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "Our AI thinks this might be a knapped stone tool, point, or other artifact. Help us search the right database.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMid,
                    )
                }
            }
        }

        // Yes — warm clay accent, primary
        SculptedButton(
            text = "Yes",
            onClick = onYes,
            accent = clayAccent,
            containerColor = clayAccent,
            textColor = Ink,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp),
        )

        // Maybe? — clay accent, secondary (outlined)
        OutlinedButton(
            onClick = onMaybe,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .sculpted(shape = RoundedCornerShape(14.dp), accent = clayAccent, shadowElevation = 6.dp)
                .background(Slate800, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.5.dp, clayAccent),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = clayAccent),
        ) {
            Text("Maybe?", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        // No — neutral / Aqua accent (matches the existing Discard pill aesthetic)
        OutlinedButton(
            onClick = onNo,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .sculpted(shape = RoundedCornerShape(14.dp), accent = Aqua, shadowElevation = 6.dp)
                .background(Slate800, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.5.dp, Aqua),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Aqua),
        ) {
            Text("No", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

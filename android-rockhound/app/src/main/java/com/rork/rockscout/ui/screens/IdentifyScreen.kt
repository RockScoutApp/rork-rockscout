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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AngleImage
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
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.StandaloneZoomableImageViewer
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
    MODERATING,
    SCANNING,
    ARTIFACT_CONFIRM,
    CLARIFY_QUESTIONS,
    CLARIFYING,
    RESULTS,
    ERROR,
    LOCKED,
    REJECTED,
}

/** Data class for a single angle capture in the 3-angle capture flow.
 * The user captures up to 3 photos (top, side, bottom) with optional
 * per-angle descriptions. The description is stored independently of the
 * bitmap/uri so deleting a photo does not clear the description. */
private data class AngleCapture(
    val angle: String, // "top", "side", "bottom"
    val bitmap: Bitmap? = null,
    val uri: Uri? = null,
    val description: String = "",
)

@Composable
fun IdentifyScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val purchaseManager = PurchaseManager.instance
    val isPremium by purchaseManager.effectiveIsPremium.collectAsState()
    val isPurchasing by purchaseManager.isPurchasing.collectAsState()
    val accessManager = IdentifyAccessManager.instance
    val trialUsesRemaining by accessManager.trialUsesRemaining.collectAsState()
    val trialActive by accessManager.trialActive.collectAsState()
    val trialExpired by accessManager.trialExpired.collectAsState()
    val tokenBalance by accessManager.tokenBalance.collectAsState()
    val hasLocationUnlock by accessManager.hasLocationUnlock.collectAsState()

    var state by remember { mutableStateOf(ScanState.IDLE) }
    // 3-angle capture state — each slot has its own bitmap, uri, and
    // description. The description is independent of the photo state so
    // deleting a photo preserves the typed text.
    val angleLabels = remember { listOf("top", "side", "bottom") }
    var angleCaptures by remember {
        mutableStateOf(List(3) { i -> AngleCapture(angle = angleLabels[i]) })
    }
    var currentAngleIndex by remember { mutableIntStateOf(0) }
    var showDeleteConfirmFor by remember { mutableIntStateOf(-1) }
    var rejectedAngle by remember { mutableStateOf("") }
    // Stored angle images (with base64) for the clarify call — populated at
    // identify time so the clarify re-rank can re-examine all viewpoints.
    var storedAngleImages by remember { mutableStateOf<List<AngleImage>>(emptyList()) }

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
    var modelsUsed by remember { mutableStateOf<List<String>>(emptyList()) }
    var uncertainArtifact by remember { mutableStateOf(false) }
    var artifactDetectConfidence by remember { mutableIntStateOf(0) }
    var preliminaryMatches by remember { mutableStateOf<List<IdentifyMatch>>(emptyList()) }
    var preliminarySummary by remember { mutableStateOf("") }
    val answers = remember { mutableStateMapOf<String, String>() }
    val customAnswers = remember { mutableStateMapOf<String, String>() }

    // Full-screen viewer state
    var viewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerInitialPage by remember { mutableIntStateOf(0) }

    // Comparison sheet state
    var showComparisonSheet by remember { mutableStateOf(false) }
    var comparisonMatch by remember { mutableStateOf<Pair<Specimen, IdentifyMatch>?>(null) }

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
        angleCaptures = List(3) { i -> AngleCapture(angle = angleLabels[i]) }
        currentAngleIndex = 0
        showDeleteConfirmFor = -1
        rejectedAngle = ""
        storedAngleImages = emptyList()
        matches = emptyList()
        artifactMatches = emptyList()
        aiSummary = ""
        errorMessage = ""
        moderationReason = ""
        clarificationQuestions = emptyList()
        webReferences = emptyList()
        assemblageResult = null
        modelsUsed = emptyList()
        uncertainArtifact = false
        artifactDetectConfidence = 0
        preliminaryMatches = emptyList()
        preliminarySummary = ""
        answers.clear()
        customAnswers.clear()
        showComparisonSheet = false
        comparisonMatch = null
    }

    fun startIdentification(searchMode: String = "rocks", skipArtifactDetect: Boolean = false) {
        val captures = angleCaptures.filter { it.bitmap != null }
        if (captures.isEmpty()) return
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
                // Encode all captured angle photos to base64
                val angleImages = withContext(Dispatchers.IO) {
                    captures.map { capture ->
                        val resized = resizeBitmap(capture.bitmap!!, 1536)
                        val baos = ByteArrayOutputStream()
                        resized.compress(Bitmap.CompressFormat.JPEG, 88, baos)
                        val bytes = baos.toByteArray()
                        val base64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                        AngleImage(
                            imageBase64 = base64,
                            mimeType = "image/jpeg",
                            angle = capture.angle,
                            description = capture.description.take(500),
                        )
                    }
                }
                storedAngleImages = angleImages
                identifyProgress = 0.40f

                // On-device exact-image cache: keyed on combined SHA-256 of all
                // submitted image bytes. Same set of photos = cache hit.
                val combinedHash = withContext(Dispatchers.IO) {
                    val allBytes = angleImages.flatMap { img ->
                        android.util.Base64.decode(img.imageBase64, android.util.Base64.NO_WRAP).toList()
                    }.toByteArray()
                    IdentifyCache.hash(allBytes)
                }
                val cached = IdentifyCache.get(combinedHash)
                val entitlement = when {
                    isPremium -> "premium"
                    else -> "free"
                }
                identifyStage = "Comparing against ${SeedData.allSpecimens.size} known specimens…"
                identifyProgress = 0.55f
                val response = cached ?: IdentifyApi.identifyMultiAngle(angleImages, entitlement, pendingSearchMode, skipArtifactDetect).also {
                    if (it.error == null && !it.artifactDetected) IdentifyCache.put(combinedHash, it)
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

                // ── Artifact detection confirmation ──────────────────────────
                // The backend detected a possible artifact (>= 70% confidence).
                // Show the yes/maybe/no popup so the user can confirm before
                // routing to the artifact pipeline. The consumed credit is
                // preserved — the re-call (artifact or rock) does not consume
                // a second credit because we refund here and re-consume on the
                // next call.
                if (response.artifactDetected && !skipArtifactDetect) {
                    accessManager.refundIdentify(consumedCredit)
                    artifactDetectConfidence = response.artifactConfidence
                    state = ScanState.ARTIFACT_CONFIRM
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
                    // Save top artifact match as field capture with all angle URIs
                    val topArtifact = matchedArtifacts.first()
                    val allUris = angleCaptures.mapNotNull { it.uri?.toString() }
                    AppRepository.instance.addCapture(
                        CapturedPhoto(
                            id = UUID.randomUUID().toString(),
                            specimenId = topArtifact.first.id,
                            specimenEmoji = topArtifact.first.emoji,
                            confidence = topArtifact.second.confidence,
                            timestamp = System.currentTimeMillis(),
                            imageUris = allUris,
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
                    modelsUsed = response.modelsUsed
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

                // Save top match as field capture with all angle URIs
                val topMatch = matchedList.first()
                val allUris = angleCaptures.mapNotNull { it.uri?.toString() }
                AppRepository.instance.addCapture(
                    CapturedPhoto(
                        id = UUID.randomUUID().toString(),
                        specimenId = topMatch.first.id,
                        specimenEmoji = topMatch.first.emoji,
                        confidence = topMatch.second.confidence,
                        timestamp = System.currentTimeMillis(),
                        imageUris = allUris,
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
                    modelsUsed = response.modelsUsed
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
                    modelsUsed = response.modelsUsed
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
        val captures = angleCaptures.filter { it.bitmap != null }
        if (captures.isEmpty()) return
        // Hard-block the identify flow when the device is offline.
        if (!isOnline) {
            errorMessage = "No internet connection. The AI identifier needs a signal to reach the rock database and analysis models. " +
                "Your token was not used. Reconnect and try again, or browse the on-device specimen database offline."
            state = ScanState.ERROR
            return
        }
        state = ScanState.MODERATING
        identifyProgress = 0.05f
        identifyStage = "Checking photos…"
        scope.launch {
            try {
                // Moderate all captured photos — if any fails, reject with
                // a message identifying which angle was blocked.
                for ((index, capture) in captures.withIndex()) {
                    val base64 = withContext(Dispatchers.IO) {
                        val resized = resizeBitmap(capture.bitmap!!, 1024)
                        val baos = ByteArrayOutputStream()
                        resized.compress(Bitmap.CompressFormat.JPEG, 80, baos)
                        android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.NO_WRAP)
                    }
                    val verdict = ImageModerator.scan(base64, "image/jpeg")
                    if (!verdict.allowed) {
                        moderationReason = verdict.reason.ifBlank {
                            "This photo can't be used because it contains content that violates our family-friendly policy."
                        }
                        rejectedAngle = capture.angle.replaceFirstChar { it.uppercase() }
                        state = ScanState.REJECTED
                        return@launch
                    }
                    identifyProgress = 0.05f + (0.20f * (index + 1) / captures.size)
                }
                identifyProgress = 0.25f
                // All photos passed moderation — proceed to identification.
                // Artifact detection is now handled by the backend in the
                // combined describe+detect Haiku call. No client-side pre-pass.
                startIdentification()
            } catch (e: Exception) {
                // Moderation hiccup — fail open and proceed to identification.
                startIdentification()
            }
        }
    }

    fun submitClarification() {
        if (storedAngleImages.isEmpty()) return
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
                    angleImages = storedAngleImages,
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
                modelsUsed = response.modelsUsed

                // Update the saved capture with the refined top match
                val topMatch = matchedList.first()
                val allUris = angleCaptures.mapNotNull { it.uri?.toString() }
                AppRepository.instance.addCapture(
                    CapturedPhoto(
                        id = UUID.randomUUID().toString(),
                        specimenId = topMatch.first.id,
                        specimenEmoji = topMatch.first.emoji,
                        confidence = topMatch.second.confidence,
                        timestamp = System.currentTimeMillis(),
                        imageUris = allUris,
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

    // Camera launcher — fills the current angle slot. Does NOT auto-run
    // identification; the user captures 1-3 photos at their own pace, then
    // taps "Identify".
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraUri != null) {
            val capturedCameraUri: Uri = cameraUri!!
            val slotIndex = currentAngleIndex
            scope.launch {
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        com.rork.rockscout.data.ImageUtils.decodeSampledBitmap(context, capturedCameraUri)
                    }
                    if (bitmap != null) {
                        val galleryUri = withContext(Dispatchers.IO) {
                            GallerySaver.saveBitmap(context.contentResolver, bitmap)
                        }
                        val uri = galleryUri ?: capturedCameraUri
                        // Update the angle slot, preserving the description text
                        angleCaptures = angleCaptures.toMutableList().also {
                            val existing = it[slotIndex]
                            it[slotIndex] = existing.copy(bitmap = bitmap, uri = uri)
                        }
                    }
                } catch (_: Exception) {
                    errorMessage = "Failed to load the captured photo. Please try again."
                    state = ScanState.ERROR
                }
            }
        }
    }

    // Gallery picker launcher — fills the current angle slot from gallery.
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            if (com.rork.rockscout.data.ImageUtils.isOverUploadLimit(context, uri)) {
                errorMessage = "That image is over 5 MB. Please choose a smaller photo."
                state = ScanState.ERROR
                return@rememberLauncherForActivityResult
            }
            val slotIndex = currentAngleIndex
            scope.launch {
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        com.rork.rockscout.data.ImageUtils.decodeSampledBitmap(context, uri)
                    }
                    if (bitmap != null) {
                        val persistentUri = withContext(Dispatchers.IO) {
                            com.rork.rockscout.data.ImageUtils.copyUriToInternalStorage(
                                context, uri, "capture_images",
                            )
                        }
                        val finalUri = persistentUri?.let { Uri.parse(it) } ?: uri
                        angleCaptures = angleCaptures.toMutableList().also {
                            val existing = it[slotIndex]
                            it[slotIndex] = existing.copy(bitmap = bitmap, uri = finalUri)
                        }
                    }
                } catch (_: Exception) {
                    errorMessage = "Failed to load the selected photo. Please try again."
                    state = ScanState.ERROR
                }
            }
        }
    }

    fun startCameraForAngle(slotIndex: Int) {
        currentAngleIndex = slotIndex
        val photoFile = File(context.cacheDir, "photos/${UUID.randomUUID()}.jpg")
        photoFile.parentFile?.mkdirs()
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
        cameraUri = uri
        cameraLauncher.launch(uri)
    }

    fun startGalleryForAngle(slotIndex: Int) {
        currentAngleIndex = slotIndex
        galleryLauncher.launch("image/*")
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

            // 3-angle capture area
            item {
                MultiAngleCapture(
                    angleCaptures = angleCaptures,
                    state = state,
                    identifyProgress = identifyProgress,
                    identifyStage = identifyStage,
                    onCapture = { idx -> startCameraForAngle(idx) },
                    onGallery = { idx -> startGalleryForAngle(idx) },
                    onDelete = { idx -> showDeleteConfirmFor = idx },
                    onDescriptionChange = { idx, text ->
                        angleCaptures = angleCaptures.toMutableList().also {
                            it[idx] = it[idx].copy(description = text.take(500))
                        }
                    },
                    onReset = { resetAll() },
                )
            }

            // Action buttons
            item {
                when (state) {
                    ScanState.IDLE -> {
                        val capturedCount = angleCaptures.count { it.bitmap != null }
                        if (capturedCount > 0) {
                            Row(
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
                                    Text("Clear", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                            if (capturedCount < 3) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "$capturedCount of 3 photos taken — add more angles for best accuracy",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMid,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        } else {
                            CaptureButtons(
                                onCamera = { startCameraForAngle(0) },
                                onGallery = { startGalleryForAngle(0) },
                            )
                        }
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
                            text = "Retake",
                            onClick = { state = ScanState.IDLE },
                            accent = Citrine,
                            containerColor = Citrine,
                            textColor = Ink,
                            icon = Icons.Filled.CameraAlt,
                            modifier = Modifier.weight(1f).height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                        )
                    }
                    ScanState.ARTIFACT_CONFIRM -> Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        SculptedButton(
                            text = "No",
                            onClick = { startIdentification(skipArtifactDetect = true) },
                            accent = Aqua,
                            containerColor = Slate800,
                            textColor = Aqua,
                            modifier = Modifier.weight(1f).height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                        )
                        SculptedButton(
                            text = "Maybe",
                            onClick = { startIdentification(searchMode = "artifacts") },
                            accent = Warning,
                            containerColor = Warning,
                            textColor = Color.Black,
                            modifier = Modifier.weight(1f).height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                        )
                        SculptedButton(
                            text = "Yes",
                            onClick = { startIdentification(searchMode = "artifacts") },
                            accent = Citrine,
                            containerColor = Citrine,
                            textColor = Ink,
                            icon = Icons.Filled.Check,
                            modifier = Modifier.weight(1f).height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                        )
                    }
                    ScanState.LOCKED -> Spacer(Modifier.height(0.dp))
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

            // Artifact detection confirmation — the AI detected a possible
            // prehistoric artifact. Show a card explaining what was detected
            // with yes/maybe/no buttons (the buttons are in the action area above).
            if (state == ScanState.ARTIFACT_CONFIRM) {
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
                                    "Is this a prehistoric artifact?",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    "Our AI thinks this might be a prehistoric artifact (${artifactDetectConfidence}% confidence) — like an arrowhead, tool, bead, or pottery — rather than a natural rock or mineral.\n\n" +
                                        "• \u2705 Yes — search the artifact database\n" +
                                        "• \u2753 Maybe — try the artifact database\n" +
                                        "• \u274c No — continue with rock & mineral ID",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkTextMid,
                                    lineHeight = 18.sp,
                                )
                            }
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
                            onCompare = {
                                val userBitmap = angleCaptures.firstNotNullOfOrNull { it.bitmap }
                                if (userBitmap != null) {
                                    comparisonMatch = null
                                    showComparisonSheet = true
                                } else {
                                    Toast.makeText(context, "No captured photo to compare", Toast.LENGTH_SHORT).show()
                                }
                            },
                        )
                    }
                }
            }

            // Agate uncertainty disclaimer — shown when the top match is an agate and confidence < 85%.
            if (state == ScanState.RESULTS) {
                val topAgate = matches.firstOrNull()?.let { (spec, match) ->
                    if (spec.name.contains("agate", ignoreCase = true) && match.confidence < 85) spec to match else null
                }
                if (topAgate != null) {
                    item {
                        AgateUncertaintyCard(
                            onCompare = {
                                val userBitmap = angleCaptures.firstNotNullOfOrNull { it.bitmap }
                                if (userBitmap != null) {
                                    comparisonMatch = null
                                    showComparisonSheet = true
                                } else {
                                    Toast.makeText(context, "No captured photo to compare", Toast.LENGTH_SHORT).show()
                                }
                            },
                        )
                    }
                }
            }

            // AI summary + model attribution chips
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
                        if (modelsUsed.isNotEmpty()) {
                            Spacer(Modifier.height(10.dp))
                            ModelsAttributionRow(modelsUsed)
                        }
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
                                        capturedBitmap = angleCaptures.firstOrNull { it.bitmap != null }?.bitmap,
                                        capturedUri = angleCaptures.firstOrNull { it.uri != null }?.uri,
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
                            // skipArtifactDetect=true so the backend doesn't
                            // re-detect the artifact and show the popup again.
                            artifactMatches = emptyList()
                            startIdentification(searchMode = "rocks", skipArtifactDetect = true)
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
                // Always-available Ask an Expert card for artifact IDs — lets
                // the user contact a museum even when the AI is confident, in
                // case they have something rare or historically significant.
                item {
                    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Museum,
                                contentDescription = null,
                                tint = Citrine,
                                modifier = Modifier.size(22.dp),
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    "Want to confirm with a museum?",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    "If you think you may have something rare or historically significant, reach out to a nearby museum or cultural center. They can help verify your find with expert eyes.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkTextLow,
                                    lineHeight = 18.sp,
                                )
                                Spacer(Modifier.height(10.dp))
                                SculptedButton(
                                    text = "Ask an Expert",
                                    onClick = { showMuseumFinder = true },
                                    accent = Citrine,
                                    containerColor = Citrine,
                                    textColor = Color.Black,
                                    icon = Icons.Filled.Museum,
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                                )
                            }
                        }
                    }
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
                                        capturedBitmap = angleCaptures.firstOrNull { it.bitmap != null }?.bitmap,
                                        capturedUri = angleCaptures.firstOrNull { it.uri != null }?.uri,
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
                            onCompare = {
                                val userBitmap = angleCaptures.firstNotNullOfOrNull { it.bitmap }
                                if (userBitmap != null) {
                                    comparisonMatch = null
                                    showComparisonSheet = true
                                } else {
                                    Toast.makeText(context, "No captured photo to compare", Toast.LENGTH_SHORT).show()
                                }
                            },
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

        // Side-by-side comparison sheet
        if (showComparisonSheet) {
            val userBitmap = remember(angleCaptures) {
                angleCaptures.firstNotNullOfOrNull { it.bitmap }
            }
            if (userBitmap != null) {
                SpecimenComparisonSheet(
                    matches = matches,
                    userBitmap = userBitmap,
                    onDismiss = { showComparisonSheet = false },
                    onViewDetails = { spec ->
                        showComparisonSheet = false
                        navController.navigate(Routes.specimen(spec.id))
                    },
                )
            }
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
            // Collect current GPS coordinates and reverse-geocode them so
            // the expert email includes both a readable location and raw coords.
            val currentCoords = AppRepository.instance.currentLocation.value
            val profile = AppRepository.instance.profile.value
            var expertLocationText by remember { mutableStateOf("") }
            LaunchedEffect(museum) {
                expertLocationText = com.rork.rockscout.data.GeoUtils.emailLocationString(
                    context = context,
                    lat = currentCoords.first,
                    lng = currentCoords.second,
                    homeRegion = profile.homeRegion,
                )
            }
            ReplyEmailDialog(
                museum = museum,
                museums = emailTargetMuseums,
                onDismiss = { emailTargetMuseum = null },
                artifactMatchNames = artifactMatches.map { it.first.name },
                artifactConfidences = artifactMatches.map { it.second.confidence },
                aiSummary = aiSummary,
                capturedBitmap = angleCaptures.firstOrNull { it.bitmap != null }?.bitmap,
                userLocationText = expertLocationText,
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
        // Delete confirmation dialog for angle photos
        if (showDeleteConfirmFor >= 0) {
            val slotIndex = showDeleteConfirmFor
            val angleName = angleCaptures[slotIndex].angle.replaceFirstChar { it.uppercase() }
            AlertDialog(
                onDismissRequest = { showDeleteConfirmFor = -1 },
                title = { Text("Delete this photo?") },
                text = { Text("Remove the $angleName view photo? Your description text will be kept so you can retake the photo without losing your notes.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            angleCaptures = angleCaptures.toMutableList().also {
                                val existing = it[slotIndex]
                                it[slotIndex] = existing.copy(bitmap = null, uri = null)
                            }
                            showDeleteConfirmFor = -1
                        },
                    ) { Text("Delete", color = Color(0xFFE2574C), fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmFor = -1 }) {
                        Text("Cancel", color = Citrine)
                    }
                },
                containerColor = Slate800,
                titleContentColor = TextHigh,
                textContentColor = TextMid,
            )
        }
        } // close Box
    }
}

/** 3-angle capture composable — shows 3 labeled slots (top, side, bottom),
 *  each with a thumbnail, description field, and delete/retake/gallery options.
 *  Replaces the old single-photo PhotoPreview. During scanning/moderating/
 *  clarifying states, an overlay with progress is shown on top of the
 *  first captured photo. */
@Composable
private fun MultiAngleCapture(
    angleCaptures: List<AngleCapture>,
    state: ScanState,
    identifyProgress: Float,
    identifyStage: String,
    onCapture: (Int) -> Unit,
    onGallery: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onDescriptionChange: (Int, String) -> Unit,
    onReset: () -> Unit,
) {
    val angleLabels = listOf("Top view" to "top", "Side view" to "side", "Bottom view" to "bottom")
    val angleIcons = listOf(Icons.Filled.CameraAlt, Icons.Filled.CameraAlt, Icons.Filled.CameraAlt)
    val isInteractive = state == ScanState.IDLE
    val showOverlay = state == ScanState.MODERATING ||
        state == ScanState.SCANNING ||
        state == ScanState.CLARIFYING ||
        state == ScanState.REJECTED

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(Slate800, Slate900)))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Header — explanatory text
        Row(verticalAlignment = Alignment.CenterVertically) {
            TagChip(
                "AI VISION",
                color = Citrine,
                modifier = Modifier,
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "3 photos = best accuracy",
                style = MaterialTheme.typography.labelMedium,
                color = Citrine,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            "For the most accurate ID, take 3 photos from different angles — top, side, and bottom. One at a time. You can also describe what you see from each angle.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMid,
            lineHeight = 18.sp,
        )

        // 3 angle slots
        angleCaptures.forEachIndexed { index, capture ->
            AngleSlot(
                label = angleLabels[index].first,
                angleKey = angleLabels[index].second,
                capture = capture,
                isInteractive = isInteractive,
                onCapture = { onCapture(index) },
                onGallery = { onGallery(index) },
                onDelete = { onDelete(index) },
                onDescriptionChange = { text -> onDescriptionChange(index, text) },
            )
        }

        // Progress overlay — shown during scanning/moderating/clarifying/rejected
        if (showOverlay) {
            val firstBitmap = angleCaptures.firstOrNull { it.bitmap != null }?.bitmap
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Slate900),
                contentAlignment = Alignment.Center,
            ) {
                if (firstBitmap != null) {
                    Image(
                        bitmap = firstBitmap.asImageBitmap(),
                        contentDescription = "Captured specimen",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(
                            alpha = if (state == ScanState.REJECTED) 0.70f else 0.40f
                        )),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 32.dp),
                    ) {
                        if (state == ScanState.REJECTED) {
                            Text("\u26d4", style = MaterialTheme.typography.displayMedium)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Photo blocked",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                        } else {
                            Text(
                                identifyStage.ifBlank {
                                    when (state) {
                                        ScanState.MODERATING -> "Checking photos…"
                                        ScanState.CLARIFYING -> "Refining results…"
                                        else -> "Scanning specimen…"
                                    }
                                },
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
                                when (state) {
                                    ScanState.MODERATING -> "Scanning for inappropriate content before identifying"
                                    ScanState.CLARIFYING -> "Cross-referencing your answers with the database"
                                    else -> "This might take a minute. The search is extensive."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Single angle slot — shows label, thumbnail (or empty placeholder with
 *  camera/gallery buttons), and a 500-char description field. */
@Composable
private fun AngleSlot(
    label: String,
    angleKey: String,
    capture: AngleCapture,
    isInteractive: Boolean,
    onCapture: () -> Unit,
    onGallery: () -> Unit,
    onDelete: () -> Unit,
    onDescriptionChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Label row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Citrine.copy(alpha = 0.15f))
                    .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.CameraAlt,
                    contentDescription = null,
                    tint = Citrine,
                    modifier = Modifier.size(16.dp),
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                label,
                style = MaterialTheme.typography.titleSmall,
                color = TextHigh,
                fontWeight = FontWeight.Bold,
            )
        }

        // Description field — always visible, independent of photo state
        OutlinedTextField(
            value = capture.description,
            onValueChange = { text -> onDescriptionChange(text.take(500)) },
            label = { Text("Describe this angle (optional)") },
            supportingText = {
                Text(
                    "Describe what you see — luster, small crystals, texture, grain size, transparency, or other details the camera might not pick up.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextLow,
                    lineHeight = 16.sp,
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = false,
            minLines = 1,
            maxLines = 3,
            enabled = isInteractive,
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
        // Character counter
        if (capture.description.isNotEmpty()) {
            Text(
                "${capture.description.length} / 500",
                style = MaterialTheme.typography.labelSmall,
                color = TextLow,
                modifier = Modifier.padding(start = 4.dp),
            )
        }

        // Thumbnail (if photo captured) or empty placeholder (camera/gallery buttons)
        if (capture.bitmap != null) {
            // Thumbnail with delete button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate700),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        bitmap = capture.bitmap.asImageBitmap(),
                        contentDescription = "$label photo",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop,
                    )
                }
                // Retake button
                OutlinedButton(
                    onClick = onCapture,
                    enabled = isInteractive,
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp),
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Retake", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                // Delete button
                OutlinedButton(
                    onClick = onDelete,
                    enabled = isInteractive,
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFE2574C),
                    ),
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete photo", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Delete", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        } else {
            // Empty placeholder — camera + gallery buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedButton(
                    onClick = onCapture,
                    enabled = isInteractive,
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Citrine.copy(alpha = 0.4f)),
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Camera", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(
                    onClick = onGallery,
                    enabled = isInteractive,
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Aqua.copy(alpha = 0.4f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Aqua),
                ) {
                    Icon(Icons.Filled.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Gallery", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
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

/**
 * Simple pinch-to-zoom viewer for a captured [Bitmap]. Mirrors the behavior of
 * [ZoomablePhoto] in Components.kt but accepts an in-memory bitmap instead of a URL.
 */
@Composable
private fun ZoomableBitmap(bitmap: Bitmap, contentDescription: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (scale > 1f) {
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                        } else {
                            onDismiss()
                        }
                    },
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(1f, 6f)
                    scale = newScale
                    if (newScale > 1f) {
                        offsetX += pan.x
                        offsetY += pan.y
                    } else {
                        offsetX = 0f
                        offsetY = 0f
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY,
                ),
            contentScale = ContentScale.Fit,
        )
    }
}

/**
 * Informational amber card shown when the top match is an agate and confidence is below 85%.
 * It explains that agate varieties are hard to distinguish and suggests comparing with reference photos.
 */
@Composable
private fun AgateUncertaintyCard(onCompare: () -> Unit, modifier: Modifier = Modifier) {
    DarkCard(modifier = modifier.fillMaxWidth(), accent = Warning) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.HelpOutline,
                contentDescription = null,
                tint = Warning,
                modifier = Modifier.size(22.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "Agate identification",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Agates are among the hardest minerals to identify down to their specific variety — many share similar banding patterns and colors. The database images and your local gem & mineral resources can help you confirm the exact type. Tap any match above to compare your specimen side-by-side with reference photos.",
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextLow,
            lineHeight = 18.sp,
        )
        Spacer(Modifier.height(10.dp))
        SculptedButton(
            text = "Compare reference photos",
            onClick = onCompare,
            accent = Warning,
            containerColor = Warning,
            textColor = Color.Black,
            icon = Icons.Filled.PhotoLibrary,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        )
    }
}

/**
 * Full-screen comparison dialog. Shows up to 5 rows, each pairing the user's best
 * captured photo (left) with a match's primary reference image (right). The user's
 * photo is the same in every row; the match reference image changes per row.
 * Each image is independently tap-to-zoom. Tapping "View details" navigates to the
 * specimen detail page and dismisses the dialog.
 */
@Composable
private fun SpecimenComparisonSheet(
    matches: List<Pair<Specimen, IdentifyMatch>>,
    userBitmap: Bitmap,
    onDismiss: () -> Unit,
    onViewDetails: (Specimen) -> Unit,
) {
    val context = LocalContext.current
    var zoomedMatchUrl by remember { mutableStateOf<String?>(null) }
    var zoomedUserBitmap by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF2000000)),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Compare your specimen",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    SculptedIconButton(
                        icon = Icons.Filled.Close,
                        contentDescription = "Close comparison",
                        onClick = onDismiss,
                        accent = Color.White,
                        iconTint = Color.White,
                        backgroundColor = Slate800,
                        size = 40.dp,
                        shadowElevation = 4.dp,
                    )
                }
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                ) {
                    itemsIndexed(matches.take(5)) { index, (spec, match) ->
                        val imageUrls = SpecimenImages.urls[spec.id] ?: spec.imageUrls
                        val refUrl = imageUrls.firstOrNull() ?: ""
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Match ${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextLow,
                                modifier = Modifier.padding(bottom = 6.dp),
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().height(180.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                // User photo
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF1C1A14))
                                        .clickable { zoomedUserBitmap = true },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Image(
                                        bitmap = userBitmap.asImageBitmap(),
                                        contentDescription = "Your photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                    )
                                }
                                // Match reference image
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF1C1A14))
                                        .clickable(enabled = refUrl.isNotBlank()) { zoomedMatchUrl = refUrl },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (refUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = refUrl,
                                            contentDescription = spec.name,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop,
                                        )
                                    } else {
                                        Text(
                                            "No reference image",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextLow,
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        spec.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        "${match.confidence}% match",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = rockClassColor(spec.rockClass),
                                    )
                                }
                                TextButton(
                                    onClick = { onViewDetails(spec) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                ) {
                                    Text(
                                        "View details",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Citrine,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            if (index < matches.size.coerceAtMost(5) - 1) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color.White.copy(alpha = 0.12f)),
                                )
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }

            // Zoomed overlays
            if (zoomedUserBitmap) {
                ZoomableBitmap(
                    bitmap = userBitmap,
                    contentDescription = "Your photo",
                    onDismiss = { zoomedUserBitmap = false },
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.95f)),
                )
            }
            zoomedMatchUrl?.let { url ->
                StandaloneZoomableImageViewer(
                    imageUrl = url,
                    onDismiss = { zoomedMatchUrl = null },
                )
            }
        }
    }
}

/**
 * Small chip row showing which AI pipeline steps ran for this identification.
 * Models are mapped to user-friendly labels and only unique, non-empty values are shown.
 */
@Composable
private fun ModelsAttributionRow(models: List<String>, modifier: Modifier = Modifier) {
    val labels = remember(models) {
        models.distinct().map { raw ->
            when (raw.lowercase()) {
                "visual_db_match", "visual reference comparison", "visual_reference_comparison" -> "Visual DB match"
                "web_cross_check", "web cross-check", "web_search" -> "Web cross-check"
                "sonnet_rerank", "sonnet re-rank", "claude_sonnet" -> "Sonnet re-rank"
                "gemini_2nd_opinion", "gemini 2nd opinion", "gemini" -> "Gemini 2nd opinion"
                "haiku_describe", "haiku description", "haiku" -> "Haiku describe"
                "artifact_detect" -> "Artifact check"
                "assemblage_analysis" -> "Assemblage analysis"
                else -> raw.replace("_", " ").replaceFirstChar { it.uppercaseChar() }
            }
        }.filter { it.isNotBlank() }
    }
    if (labels.isEmpty()) return
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        labels.forEach { label ->
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2A2720))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = Success,
                    modifier = Modifier.size(12.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTextHigh,
                    fontWeight = FontWeight.Medium,
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
    onCompare: () -> Unit = {},
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
                Icon(
                    Icons.Filled.PhotoLibrary,
                    contentDescription = "Compare with reference",
                    tint = Citrine,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable(onClick = onCompare)
                        .padding(2.dp),
                )
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

// ArtifactConfirmPopup removed — artifact detection is now handled by the
// backend in the combined describe+detect Haiku call. No client-side pre-pass.

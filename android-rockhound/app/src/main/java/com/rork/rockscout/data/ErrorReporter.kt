package com.rork.rockscout.data

import android.content.Context
import android.os.Build
import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.security.MessageDigest

/**
 * Central error reporter that sends runtime exceptions to the Supabase
 * `rockscout_error_logs` table via the `/error-report` Cloudflare Worker.
 *
 * Works alongside [BugLogger] — BugLogger persists errors locally for the
 * Developer Console, while ErrorReporter additionally ships them to the
 * central service for cross-platform diagnostics.
 *
 * Self-healing: for known recoverable error patterns (cache corruption,
 * OOM, network state), the reporter can automatically apply a fix before
 * the error propagates. See [SelfHealer].
 *
 * All network calls are fire-and-forget on a background coroutine — they
 * never block the calling thread or crash the app if the upload fails.
 */
object ErrorReporter {

    private const val TAG = "ErrorReporter"
    private const val MAX_STACK_TRACE = 8000
    private const val MAX_SCREEN = 200
    private const val MAX_MESSAGE = 2000
    private const val MAX_BREADCRUMB = 500

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val reportMutex = Mutex()

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    private val client = NetworkClient.client

    @Serializable
    private data class ErrorPayload(
        val platform: String,
        val appVersion: String?,
        val osVersion: String?,
        val deviceModel: String?,
        val userId: String?,
        val errorType: String,
        val errorMessage: String,
        val stackTrace: String?,
        val isFatal: Boolean,
        val screen: String?,
        val breadcrumb: String?,
        val autoHealed: Boolean,
        val healAction: String?,
        val fingerprint: String,
    )

    @Serializable
    private data class ErrorResponse(val ok: Boolean = false, val deduplicated: Boolean = false)

    private var appVersionName: String = "unknown"
    private var currentUserId: String? = null
    private var lastBreadcrumb: String? = null
    private var initialized = false
    private var appContext: Context? = null

    /** Must be called once from Application.onCreate. */
    fun initialize(context: Context) {
        if (initialized) return
        initialized = true
        appContext = context
        appVersionName = runCatching {
            context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionName ?: "unknown"
        }.getOrDefault("unknown")
    }

    /** Set the current user ID so errors can be attributed post-auth. */
    fun setUserId(userId: String?) {
        currentUserId = userId
        // Keep BugLogger in sync so local bug entries also carry the user ID
        BugLogger.setUserId(userId)
    }

    /** Set the last user action for breadcrumb tracing. */
    fun setBreadcrumb(action: String) {
        lastBreadcrumb = action.take(MAX_BREADCRUMB)
    }

    /**
     * Report an error to the central service. Fire-and-forget.
     *
     * @param context Android context (used for version info if not initialized)
     * @param screen  Screen name or route where the error occurred
     * @param throwable The exception
     * @param isFatal Whether this is a fatal crash
     * @param attemptSelfHeal If true, try to auto-fix known recoverable patterns
     */
    fun report(
        context: Context? = null,
        screen: String,
        throwable: Throwable,
        isFatal: Boolean = false,
        attemptSelfHeal: Boolean = true,
    ) {
        val version = if (initialized) appVersionName else context?.let {
            runCatching {
                it.packageManager.getPackageInfo(it.packageName, 0).versionName ?: "unknown"
            }.getOrDefault("unknown")
        } ?: "unknown"

        var healAction: String? = null
        var autoHealed = false

        if (attemptSelfHeal && !isFatal) {
            val healResult = SelfHealer.attemptHeal(context, screen, throwable)
            if (healResult != null) {
                autoHealed = true
                healAction = healResult
                Log.i(TAG, "Auto-healed error on $screen: $healAction")
            }
        }

        val errorType = throwable.javaClass.simpleName.ifBlank { throwable.javaClass.name }
        val message = (throwable.message ?: "(no message)").take(MAX_MESSAGE)
        val stack = runCatching {
            Log.getStackTraceString(throwable).take(MAX_STACK_TRACE)
        }.getOrDefault("")

        val fingerprint = fingerprint(errorType, message, screen)

        val payload = ErrorPayload(
            platform = "android",
            appVersion = version,
            osVersion = Build.VERSION.SDK_INT.toString(),
            deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}".take(300),
            userId = currentUserId,
            errorType = errorType.take(300),
            errorMessage = message,
            stackTrace = stack.ifBlank { null },
            isFatal = isFatal,
            screen = screen.take(MAX_SCREEN).ifBlank { null },
            breadcrumb = lastBreadcrumb,
            autoHealed = autoHealed,
            healAction = healAction,
            fingerprint = fingerprint,
        )

        // Also log locally to BugLogger so it shows up in the dev tools bug section
        if (autoHealed) {
            BugLogger.logMessage(
                context = context,
                screen = screen,
                message = "[Auto-healed: $healAction] $message",
                isFatal = isFatal,
            )
        } else {
            BugLogger.log(
                context = context,
                screen = screen,
                throwable = throwable,
                isFatal = isFatal,
            )
        }

        scope.launch {
            reportMutex.withLock {
                runCatching { upload(payload) }
            }
        }
    }

    /**
     * Report a non-throwable error message (e.g. a failed assertion or
     * an API error that doesn't have an exception object).
     */
    fun reportMessage(
        screen: String,
        message: String,
        isFatal: Boolean = false,
        context: Context? = null,
    ) {
        val fingerprint = fingerprint("Message", message, screen)

        val payload = ErrorPayload(
            platform = "android",
            appVersion = appVersionName,
            osVersion = Build.VERSION.SDK_INT.toString(),
            deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}".take(300),
            userId = currentUserId,
            errorType = "Message",
            errorMessage = message.take(MAX_MESSAGE),
            stackTrace = null,
            isFatal = isFatal,
            screen = screen.take(MAX_SCREEN).ifBlank { null },
            breadcrumb = lastBreadcrumb,
            autoHealed = false,
            healAction = null,
            fingerprint = fingerprint,
        )

        // Also log locally to BugLogger so it shows up in the dev tools bug section
        BugLogger.logMessage(
            context = context ?: appContext,
            screen = screen,
            message = message,
            isFatal = isFatal,
        )

        scope.launch {
            reportMutex.withLock {
                runCatching { upload(payload) }
            }
        }
    }

    private suspend fun upload(payload: ErrorPayload) {
        val baseUrl = BuildSecrets.resolve("EXPO_PUBLIC_RORK_FUNCTIONS_URL", BuildSecrets.RORK_FUNCTIONS_URL)
            .ifBlank { null } ?: return
        val appKey = BuildSecrets.resolve("EXPO_PUBLIC_RORK_APP_KEY", BuildSecrets.RORK_APP_KEY)

        val response = client.post("$baseUrl/error-report") {
            contentType(ContentType.Application.Json)
            if (appKey.isNotBlank()) header("X-App-Key", appKey)
            setBody(json.encodeToString(ErrorPayload.serializer(), payload))
        }

        val body = response.body<String>()
        val parsed = json.decodeFromString(ErrorResponse.serializer(), body)
        if (parsed.ok) {
            Log.d(TAG, "Error reported: ${payload.errorType} on ${payload.screen}" +
                if (parsed.deduplicated) " (deduplicated)" else "")
        } else {
            Log.w(TAG, "Error report failed: $body")
        }
    }

    /** SHA-256 of (errorType + message + screen), first 16 hex chars. */
    private fun fingerprint(type: String, message: String, screen: String): String {
        val input = "$type|$message|$screen"
        val digest = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }.take(16)
    }
}

package com.rork.rockscout.data

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.FileProvider
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.io.readByteArray
import java.io.File

/**
 * Downloads an update APK and installs it over the existing app.
 *
 * The install path is the [PackageInstaller] session API, which reports the
 * *real* failure reason (incompatible signature, insufficient storage, aborted
 * by the user, …) through [InstallResultReceiver] instead of leaving the user
 * staring at the system's generic "App not installed" dialog. If the session
 * API is unavailable for any reason, it falls back to the classic
 * `ACTION_VIEW` + FileProvider sideload intent.
 *
 * The full pipeline, in order:
 * 1. Ensure "install unknown apps" is granted — if not, park in
 *    [Status.NEEDS_INSTALL_PERMISSION] and resume automatically once the user
 *    comes back from Settings ([resumeIfReady]).
 * 2. Stream the APK to `cache/updates/` (never buffered whole in memory, so a
 *    60 MB+ APK can't OOM a low-memory device).
 * 3. Validate it: magic bytes, parseable package, matching package name, not a
 *    downgrade, matching signing certificate.
 * 4. Commit a PackageInstaller session and report the outcome.
 *
 * Every stage publishes to [status] / [progress] / [error] so the UI can show
 * exactly where things are, and [retry] re-runs the last attempt.
 */
object ApkInstaller {

    private const val TAG = "ApkInstaller"
    private const val INSTALLER_REQUEST_CODE = 77001
    private const val DOWNLOAD_BUFFER = 64L * 1024L

    enum class Status {
        IDLE,
        NEEDS_INSTALL_PERMISSION,
        DOWNLOADING,
        VERIFYING,
        INSTALLING,
        DONE,
        FAILED,
        SIGNING_CONFLICT,
    }

    /** Thrown when the downloaded APK is signed with a different key than the
     *  installed app. Surfaced to the UI as a friendly dialog offering to
     *  uninstall the old version before reinstalling. */
    private class SigningConflictException(message: String) : Exception(message)

    private val _status = MutableStateFlow(Status.IDLE)
    val status: StateFlow<Status> = _status.asStateFlow()

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** Human-readable size of the download, e.g. "52.4 MB" — empty while unknown. */
    private val _downloadSize = MutableStateFlow("")
    val downloadSize: StateFlow<String> = _downloadSize.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** Remembered so the flow can be resumed after the user grants the
     *  "install unknown apps" permission, or retried after a failure. */
    @Volatile private var pendingApkUrl: String? = null
    @Volatile private var pendingStoreUrl: String = ""

    /** Reset back to IDLE so the dialog can be dismissed cleanly. */
    fun reset() {
        _status.value = Status.IDLE
        _progress.value = 0
        _error.value = null
        _downloadSize.value = ""
    }

    /**
     * Entry point. Downloads [apkUrl] and installs it.
     *
     * Safe to call repeatedly — a run already in flight is ignored so a
     * double-tap can't kick off two downloads.
     */
    fun start(context: Context, apkUrl: String, fallbackStoreUrl: String = "") {
        val current = _status.value
        if (current == Status.DOWNLOADING || current == Status.VERIFYING || current == Status.INSTALLING) {
            Log.d(TAG, "Update already in progress (status=$current) — ignoring duplicate start")
            return
        }
        pendingApkUrl = apkUrl
        pendingStoreUrl = fallbackStoreUrl
        val appContext = context.applicationContext
        scope.launch { run(appContext) }
    }

    /** Legacy suspend entry point kept for existing callers. */
    suspend fun downloadAndInstall(context: Context, apkUrl: String, fallbackStoreUrl: String = "") {
        pendingApkUrl = apkUrl
        pendingStoreUrl = fallbackStoreUrl
        run(context.applicationContext)
    }

    /** Re-runs the last attempt (after a failure, or after granting permission). */
    fun retry(context: Context) {
        val url = pendingApkUrl ?: return
        reset()
        start(context, url, pendingStoreUrl)
    }

    /**
     * Called from the Activity's `onResume`. If we were waiting on the
     * "install unknown apps" permission and the user has now granted it, the
     * update resumes on its own — no second tap required.
     */
    fun resumeIfReady(context: Context) {
        if (_status.value != Status.NEEDS_INSTALL_PERMISSION) return
        if (!canInstallPackages(context)) return
        Log.d(TAG, "Install permission granted — resuming update")
        retry(context)
    }

    /** True when the OS will let this app install an APK. */
    fun canInstallPackages(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return true
        return runCatching { context.packageManager.canRequestPackageInstalls() }.getOrDefault(false)
    }

    /**
     * Opens the system screen where the user enables "Install unknown apps"
     * for RockScout. Falls back to the app's own settings page, then to the
     * global security settings, so this always lands somewhere useful.
     */
    fun openInstallPermissionSettings(context: Context) {
        val intents = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                add(
                    Intent(
                        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:${context.packageName}"),
                    ),
                )
                add(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES))
            }
            add(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:${context.packageName}"),
                ),
            )
        }
        for (intent in intents) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (SafeLinkOpener.canHandle(context, intent)) {
                SafeLinkOpener.launch(context, intent, "Unable to open the install permission screen.")
                return
            }
        }
        SafeLinkOpener.launch(
            context,
            Intent(Settings.ACTION_SECURITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            "Unable to open Settings. Please enable 'Install unknown apps' for RockScout manually.",
        )
    }

    /** Launch the system uninstall flow for RockScout so the user can remove
     *  the old (differently-signed) build before installing the new one.
     *
     *  Before launching the uninstall, backs up the user's full SharedPreferences
     *  to the cloud (keyed by their user ID) so it can be restored after reinstall.
     *  The backup is fire-and-forget — if it fails, the uninstall still proceeds. */
    fun launchUninstall(context: Context) {
        val userId = AuthRepository.instance.currentUserId
        if (userId != null) {
            try {
                val settingsJson = PersistenceManager.exportAllSettingsAsJson()
                scope.launch {
                    SettingsBackupApi.backupSettings(userId, settingsJson)
                        .onFailure { Log.w(TAG, "Settings cloud backup failed: ${it.message}") }
                        .onSuccess { Log.d(TAG, "Settings cloud backup saved for user $userId") }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Could not export settings for backup: ${e.message}")
            }
        }

        val intent = Intent(Intent.ACTION_DELETE, Uri.parse("package:${context.packageName}")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        SafeLinkOpener.launch(context, intent, "Unable to open the uninstall screen.")
    }

    // ---------------------------------------------------------------- pipeline

    private suspend fun run(context: Context) {
        val apkUrl = pendingApkUrl ?: return
        _error.value = null
        _progress.value = 0

        // Ask for the install permission BEFORE downloading anything. Nothing
        // is more frustrating than waiting for a 60 MB download only to be told
        // the app isn't allowed to install it.
        if (!canInstallPackages(context)) {
            Log.d(TAG, "Missing REQUEST_INSTALL_PACKAGES consent — prompting user")
            _status.value = Status.NEEDS_INSTALL_PERMISSION
            return
        }

        _status.value = Status.DOWNLOADING
        try {
            val apkFile = downloadApk(context, apkUrl)
            _status.value = Status.VERIFYING
            validateApk(context, apkFile)
            _status.value = Status.INSTALLING
            install(context, apkFile)
        } catch (e: SigningConflictException) {
            Log.w(TAG, "Signing conflict: ${e.message}")
            _error.value = e.message
            _status.value = Status.SIGNING_CONFLICT
        } catch (e: CancellationException) {
            _status.value = Status.IDLE
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Self-update failed", e)
            _error.value = e.message ?: "The update could not be downloaded. Please check your connection and try again."
            _status.value = Status.FAILED
        }
    }

    /** Streams the APK to cache/updates/rockscout-update.apk with progress updates. */
    private suspend fun downloadApk(context: Context, apkUrl: String): File = withContext(Dispatchers.IO) {
        val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
        // Clear stale downloads so a half-finished file can never be installed.
        updatesDir.listFiles()?.forEach { runCatching { it.delete() } }
        val apkFile = File(updatesDir, "rockscout-update.apk")

        val client = HttpClient {
            install(io.ktor.client.plugins.HttpTimeout) {
                connectTimeoutMillis = 20_000
                requestTimeoutMillis = 15 * 60_000
                socketTimeoutMillis = 90_000
            }
            followRedirects = true
        }

        try {
            val response = client.get(apkUrl)
            if (!response.status.isSuccess()) {
                throw RuntimeException("Download failed (HTTP ${response.status.value}). The update file may have moved.")
            }
            val total = response.contentLength() ?: -1L
            _downloadSize.value = if (total > 0) formatBytes(total) else ""

            // Fail fast when there isn't enough free space — otherwise the
            // install aborts halfway with an opaque error.
            if (total > 0) {
                val free = updatesDir.usableSpace
                // Need room for the download AND the installed copy.
                if (free in 1 until (total * 2)) {
                    throw RuntimeException(
                        "Not enough free space to install the update. Please free up about ${formatBytes(total * 2)} and try again.",
                    )
                }
            }

            var written = 0L
            val channel = response.bodyAsChannel()
            apkFile.outputStream().buffered().use { out ->
                while (!channel.isClosedForRead) {
                    val packet = channel.readRemaining(DOWNLOAD_BUFFER)
                    while (!packet.exhausted()) {
                        val bytes = packet.readByteArray()
                        out.write(bytes)
                        written += bytes.size
                        if (total > 0) {
                            _progress.value = ((written * 100) / total).toInt().coerceIn(0, 100)
                        }
                    }
                }
                out.flush()
            }
            _progress.value = 100
            Log.d(TAG, "Downloaded $written bytes to ${apkFile.absolutePath}")

            if (total > 0 && written < total) {
                throw RuntimeException("The download was interrupted. Please try again.")
            }
            apkFile
        } finally {
            client.close()
        }
    }

    /**
     * Installs [apkFile] using the PackageInstaller session API, falling back
     * to the FileProvider sideload intent if the session can't be created.
     */
    private fun install(context: Context, apkFile: File) {
        val sessionResult = runCatching { installViaSession(context, apkFile) }
        if (sessionResult.isSuccess) return

        Log.w(TAG, "Session install failed, falling back to intent", sessionResult.exceptionOrNull())
        installViaIntent(context, apkFile)
    }

    private fun installViaSession(context: Context, apkFile: File) {
        val installer = context.packageManager.packageInstaller
        val params = PackageInstaller.SessionParams(
            PackageInstaller.SessionParams.MODE_FULL_INSTALL,
        ).apply {
            setAppPackageName(context.packageName)
            runCatching { setSize(apkFile.length()) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                runCatching { setInstallReason(PackageManager.INSTALL_REASON_USER) }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                runCatching {
                    setRequireUserAction(PackageInstaller.SessionParams.USER_ACTION_NOT_REQUIRED)
                }
            }
        }

        val sessionId = installer.createSession(params)
        installer.openSession(sessionId).use { session ->
            session.openWrite("rockscout_update", 0, apkFile.length()).use { out ->
                apkFile.inputStream().use { input -> input.copyTo(out, 128 * 1024) }
                session.fsync(out)
            }

            val intent = Intent(context, InstallResultReceiver::class.java).apply {
                action = InstallResultReceiver.ACTION_INSTALL_RESULT
                setPackage(context.packageName)
            }
            var flags = PendingIntent.FLAG_UPDATE_CURRENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Must be mutable: the system fills in EXTRA_STATUS / EXTRA_INTENT.
                flags = flags or PendingIntent.FLAG_MUTABLE
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                INSTALLER_REQUEST_CODE,
                intent,
                flags,
            )
            session.commit(pendingIntent.intentSender)
        }
        Log.d(TAG, "Install session $sessionId committed")
    }

    /**
     * Classic sideload path — fires the system "Install APK" intent via
     * FileProvider. Used only when the session API isn't usable.
     */
    private fun installViaIntent(context: Context, apkFile: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile,
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (!SafeLinkOpener.canHandle(context, intent)) {
            throw IllegalStateException("No package installer is available on this device.")
        }
        context.startActivity(intent)
        // The intent path gives no completion callback — treat launching the
        // system installer as done from our side.
        _status.value = Status.DONE
    }

    // -------------------------------------------------------------- callbacks

    /** Called by [InstallResultReceiver] when the session finishes cleanly. */
    fun onInstallSucceeded() {
        _status.value = Status.DONE
        _error.value = null
        pendingApkUrl = null
    }

    /** Called by [InstallResultReceiver] with the real failure reason. */
    fun onInstallFailed(status: Int, message: String?) {
        val incompatible = status == PackageInstaller.STATUS_FAILURE_CONFLICT ||
            message?.contains("INSTALL_FAILED_UPDATE_INCOMPATIBLE", ignoreCase = true) == true ||
            message?.contains("signatures do not match", ignoreCase = true) == true
        if (incompatible) {
            _error.value = "This update is signed with a different key than the version installed on your device."
            _status.value = Status.SIGNING_CONFLICT
            return
        }
        _error.value = friendlyInstallError(status, message)
        _status.value = if (status == PackageInstaller.STATUS_FAILURE_ABORTED) Status.IDLE else Status.FAILED
    }

    private fun friendlyInstallError(status: Int, message: String?): String = when (status) {
        PackageInstaller.STATUS_FAILURE_ABORTED ->
            "Install cancelled."
        PackageInstaller.STATUS_FAILURE_BLOCKED ->
            "The install was blocked by your device. Check Play Protect or your security settings, then try again."
        PackageInstaller.STATUS_FAILURE_INCOMPATIBLE ->
            "This update isn't compatible with your device."
        PackageInstaller.STATUS_FAILURE_INVALID ->
            "The update file was damaged in transit. Please try again."
        PackageInstaller.STATUS_FAILURE_STORAGE ->
            "There isn't enough free space to install the update. Free up some space and try again."
        else -> message?.takeIf { it.isNotBlank() } ?: "The update couldn't be installed. Please try again."
    }

    // ------------------------------------------------------------- validation

    /**
     * Validates the downloaded APK before the installer is ever asked to handle
     * it. Checks, in order: non-empty file, ZIP magic bytes, parseable package,
     * matching package name, not a downgrade, matching signing certificate.
     */
    private fun validateApk(context: Context, apkFile: File) {
        if (!apkFile.exists() || apkFile.length() <= 0) {
            throw IllegalStateException("The downloaded update is missing or empty.")
        }

        // APKs are ZIP archives — the first two bytes must be "PK".
        val magic = apkFile.inputStream().use { it.readNBytes(2) }
        if (magic.size < 2 || magic[0] != 'P'.code.toByte() || magic[1] != 'K'.code.toByte()) {
            throw IllegalStateException("The downloaded file isn't a valid app package. The download link may be broken.")
        }

        val packageManager = context.packageManager
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PackageManager.GET_SIGNING_CERTIFICATES
        } else {
            @Suppress("DEPRECATION")
            PackageManager.GET_SIGNATURES
        }

        val archiveInfo = packageManager.getPackageArchiveInfo(apkFile.absolutePath, flags)
            ?: throw IllegalStateException("The downloaded update couldn't be read as an Android app.")

        val installedInfo = runCatching {
            packageManager.getPackageInfo(context.packageName, flags)
        }.getOrNull()
            ?: throw IllegalStateException("Could not read the installed app information.")

        if (archiveInfo.packageName != context.packageName) {
            throw IllegalStateException("The downloaded update isn't RockScout (package mismatch).")
        }

        val installedVersion = versionCodeOf(installedInfo)
        val apkVersion = versionCodeOf(archiveInfo)
        // Only a genuine downgrade is rejected — Android refuses those outright.
        // An equal version is allowed so a user can always repair/reinstall.
        if (apkVersion < installedVersion) {
            throw IllegalStateException(
                "The downloaded update is older than the version you already have installed.",
            )
        }

        if (!signaturesMatch(installedInfo, archiveInfo)) {
            throw SigningConflictException(
                "This update is signed with a different key than the version installed on your device.",
            )
        }
    }

    private fun versionCodeOf(info: PackageInfo): Long =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            info.versionCode.toLong()
        }

    /**
     * Compares the signing certificate(s) of the installed app and the
     * downloaded APK. Android requires them to match when installing an update
     * over an existing app.
     */
    private fun signaturesMatch(installedInfo: PackageInfo, archiveInfo: PackageInfo): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val installedSigners = installedInfo.signingInfo?.apkContentsSigners
            val archiveSigners = archiveInfo.signingInfo?.apkContentsSigners
            if (installedSigners.isNullOrEmpty() || archiveSigners.isNullOrEmpty()) return false
            installedSigners.size == archiveSigners.size &&
                installedSigners.zip(archiveSigners).all { (a, b) ->
                    a.toByteArray().contentEquals(b.toByteArray())
                }
        } else {
            @Suppress("DEPRECATION")
            val installedSignatures = installedInfo.signatures
            @Suppress("DEPRECATION")
            val archiveSignatures = archiveInfo.signatures
            if (installedSignatures.isNullOrEmpty() || archiveSignatures.isNullOrEmpty()) {
                return false
            }
            installedSignatures.size == archiveSignatures.size &&
                installedSignatures.zip(archiveSignatures).all { (a, b) ->
                    a.toByteArray().contentEquals(b.toByteArray())
                }
        }
    }

    private fun formatBytes(bytes: Long): String {
        val mb = bytes / (1024.0 * 1024.0)
        return if (mb >= 1) String.format("%.1f MB", mb) else "${bytes / 1024} KB"
    }
}

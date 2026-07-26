package com.rork.rockscout.data

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import io.ktor.client.HttpClient
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Downloads an update APK and launches the system package installer so the new
 * build installs over the existing one — no uninstall required, as long as the
 * signing key matches.
 *
 * Progress is published to [progress] (0–100) and the high-level status to
 * [status] so the UI can show a download + install dialog. Cancelling the
 * collecting coroutine cancels the download.
 */
object ApkInstaller {

    private const val TAG = "ApkInstaller"
    private const val INSTALLER_REQUEST_CODE = 77001
    private const val SESSION_ID_KEY = "com.rork.rockscout.update.session_id"

    enum class Status { IDLE, DOWNLOADING, INSTALLING, DONE, FAILED, SIGNING_CONFLICT }

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

    /** Reset back to IDLE so the dialog can be dismissed cleanly. */
    fun reset() {
        _status.value = Status.IDLE
        _progress.value = 0
        _error.value = null
    }

    /** Launch the system uninstall flow for RockScout so the user can remove
     *  the old (differently-signed) build before installing the new one.
     *  Uses a safe launch so a missing handler never crashes the app. */
    fun launchUninstall(context: Context) {
        val intent = Intent(Intent.ACTION_DELETE, Uri.parse("package:${context.packageName}")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        SafeLinkOpener.launch(context, intent, "Unable to open the uninstall screen.")
    }

    /**
     * Downloads [apkUrl] into the app cache, then triggers the system installer.
     * Throws on failure so callers can surface a message; also publishes to [error].
     *
     * @param fallbackStoreUrl  When the APK cannot be installed (e.g. the file
     *        is corrupt, signatures differ, or no package installer is available),
     *        the user is redirected to this store URL instead of seeing the system
     *        "App not installed" dialog.
     */
    suspend fun downloadAndInstall(context: Context, apkUrl: String, fallbackStoreUrl: String = "") {
        _error.value = null
        _progress.value = 0
        _status.value = Status.DOWNLOADING
        try {
            val apkFile = downloadApk(context, apkUrl)
            _status.value = Status.INSTALLING
            launchSystemInstaller(context, apkFile, fallbackStoreUrl)
            _status.value = Status.DONE
        } catch (e: SigningConflictException) {
            // Signing conflict — do NOT auto-redirect to the store. Surface the
            // friendly dialog so the user can uninstall the old build first.
            Log.w(TAG, "Signing conflict: ${e.message}")
            _error.value = e.message
            _status.value = Status.SIGNING_CONFLICT
        } catch (e: CancellationException) {
            _status.value = Status.IDLE
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Self-update failed", e)
            _error.value = e.message ?: "Download failed"
            _status.value = Status.FAILED
            if (fallbackStoreUrl.isNotBlank()) {
                SafeLinkOpener.openUrl(context, fallbackStoreUrl)
            }
        }
    }

    /** Streams the APK to cache/updates/rockscout-update.apk with progress updates. */
    private suspend fun downloadApk(context: Context, apkUrl: String): File = withContext(Dispatchers.IO) {
        val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
        val apkFile = File(updatesDir, "rockscout-update.apk")
        if (apkFile.exists()) apkFile.delete()

        // Dedicated client with a long socket timeout for large APK downloads.
        val client = HttpClient {
            install(io.ktor.client.plugins.HttpTimeout) {
                connectTimeoutMillis = 15_000
                requestTimeoutMillis = 10 * 60_000
                socketTimeoutMillis = 60_000
            }
        }

        try {
            val response = client.get(apkUrl) {
                onDownload { bytesSentTotal, contentLength ->
                    if (contentLength != null && contentLength > 0) {
                        val pct = ((bytesSentTotal * 100) / contentLength).toInt().coerceIn(0, 100)
                        _progress.value = pct
                    }
                }
            }
            if (!response.status.isSuccess()) {
                throw RuntimeException("Download failed: HTTP ${response.status.value}")
            }
            val bytes = response.readBytes()
            apkFile.writeBytes(bytes)
            _progress.value = 100
            Log.d(TAG, "Downloaded ${bytes.size} bytes to ${apkFile.absolutePath}")
            apkFile
        } finally {
            client.close()
        }
    }

    /**
     * Fires the system "Install APK" intent via FileProvider. The user still has
     * to tap Install in the system dialog — Android never lets apps silently
     * install another APK.
     *
     * Guards against the system "App not installed" dialog by fully validating the
     * APK before launch: it must be a non-empty ZIP/APK, parseable by the package
     * manager, match the current package name, have a higher version code, and be
     * signed with the same certificate as the installed app. If anything is wrong,
     * it falls back to the Play Store listing instead of asking the system to
     * install a broken or incompatible APK.
     */
    private fun launchSystemInstaller(context: Context, apkFile: File, fallbackStoreUrl: String) {
        validateApk(context, apkFile)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile,
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            // Clear any prior dec-extras so old grants don't trip up the installer.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }

        if (!SafeLinkOpener.canHandle(context, intent)) {
            Log.w(TAG, "No package installer available for APK; falling back to store")
            if (fallbackStoreUrl.isNotBlank()) {
                SafeLinkOpener.openUrl(context, fallbackStoreUrl)
            }
            throw IllegalStateException("No package installer available for this APK.")
        }

        runCatching { context.startActivity(intent) }
            .onFailure { e ->
                Log.e(TAG, "Could not launch installer", e)
                if (fallbackStoreUrl.isNotBlank()) {
                    SafeLinkOpener.openUrl(context, fallbackStoreUrl)
                }
                throw e
            }
    }

    /**
     * Validates the downloaded APK before the system installer is ever asked to
     * handle it. This prevents the system "App not installed" dialog that appears
     * when the installer receives a corrupted, mismatched, or downgraded APK.
     *
     * Checks, in order:
     * 1. File exists and is non-empty.
     * 2. File starts with the ZIP/APK magic bytes ("PK").
     * 3. PackageManager can parse the archive and read its package info.
     * 4. APK package name matches the currently installed app.
     * 5. APK version code is strictly greater than the installed version code.
     * 6. APK signing certificate(s) match the installed app.
     * 7. On Android 8+, the app is allowed to request package installs.
     *
     * Throws [IllegalStateException] with a human-readable message if any check fails.
     */
    private fun validateApk(context: Context, apkFile: File) {
        if (!apkFile.exists() || apkFile.length() <= 0) {
            throw IllegalStateException("Downloaded update is missing or empty.")
        }

        // APKs are ZIP archives — the first two bytes must be "PK".
        val magic = apkFile.inputStream().use { it.readNBytes(2) }
        if (magic.size < 2 || magic[0] != 'P'.code.toByte() || magic[1] != 'K'.code.toByte()) {
            throw IllegalStateException("Downloaded update is not a valid APK file.")
        }

        val packageManager = context.packageManager
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PackageManager.GET_SIGNING_CERTIFICATES
        } else {
            @Suppress("DEPRECATION")
            PackageManager.GET_SIGNATURES
        }

        val archiveInfo = packageManager.getPackageArchiveInfo(apkFile.absolutePath, flags)
            ?: throw IllegalStateException("Downloaded update is not a valid Android package.")

        val installedInfo = runCatching {
            packageManager.getPackageInfo(context.packageName, flags)
        }.getOrNull()
            ?: throw IllegalStateException("Could not read the installed app information.")

        if (archiveInfo.packageName != context.packageName) {
            throw IllegalStateException(
                "Downloaded update does not match RockScout (package mismatch).",
            )
        }

        val installedVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            installedInfo.longVersionCode.toInt()
        } else {
            @Suppress("DEPRECATION")
            installedInfo.versionCode
        }
        val apkVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            archiveInfo.longVersionCode.toInt()
        } else {
            @Suppress("DEPRECATION")
            archiveInfo.versionCode
        }
        if (apkVersion <= installedVersion) {
            throw IllegalStateException(
                "Downloaded update is not newer than the installed version.",
            )
        }

        if (!signaturesMatch(installedInfo, archiveInfo)) {
            throw SigningConflictException(
                "Downloaded update was signed with a different key. Please install from the Play Store instead.",
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            !packageManager.canRequestPackageInstalls()
        ) {
            throw IllegalStateException(
                "This app isn't allowed to install updates. Please enable 'Install unknown apps' for RockScout in Settings, or update from the Play Store.",
            )
        }
    }

    /**
     * Compares the signing certificate(s) of the installed app and the downloaded
     * APK. Android requires the certificates to match exactly when installing an
     * update over an existing app; otherwise the install fails with the system
     * "App not installed" dialog.
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
}

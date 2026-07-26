package com.rork.rockscout.data

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
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

    enum class Status { IDLE, DOWNLOADING, INSTALLING, DONE, FAILED }

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
     * Guards against the "App not installed" dialog by verifying the APK file
     * is non-empty and that the system has a package installer that can handle it.
     * If not, it falls back to the Play Store listing.
     */
    private fun launchSystemInstaller(context: Context, apkFile: File, fallbackStoreUrl: String) {
        if (!apkFile.exists() || apkFile.length() <= 0) {
            throw IllegalStateException("Downloaded APK is missing or empty.")
        }

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
}

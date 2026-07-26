package com.rork.rockscout.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

/**
 * Observable update state for the app.
 *
 * Holds the latest [AppUpdateInfo] when a newer version is available on the
 * store, or `null` when the app is up to date / the check has not yet run.
 *
 * The [UpdateCheckWorker] and the in-app foreground check both feed into this
 * singleton so any observing UI (the "Update Now" button on the home header)
 * reacts instantly when an update is detected.
 */
object UpdateManager {

    private const val TAG = "UpdateManager"
    private const val BASE_URL = "https://rockscout-finder-backend.rork.app"
    private const val DEFAULT_PLAY_STORE_URL =
        "https://play.google.com/store/apps/details?id=com.rork.rockscout"

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _updateInfo = MutableStateFlow<AppUpdateInfo?>(null)
    val updateInfo: StateFlow<AppUpdateInfo?> = _updateInfo.asStateFlow()

    /** True when a newer version is available — convenience for UI. */
    val updateAvailable: StateFlow<Boolean> = MutableStateFlow(false).also { mutable ->
        scope.launch {
            _updateInfo.collect { mutable.value = it != null }
        }
    }

    /**
     * Fetches the latest version from the backend and, if newer than the
     * installed versionCode, publishes it to [updateInfo] and fires a push
     * notification. Safe to call from the foreground (onResume) or from the
     * [UpdateCheckWorker]. Coalesces rapid successive calls — only the last
     * result wins.
     */
    fun checkForUpdate(context: Context) {
        scope.launch {
            try {
                val installedVersionCode = installedVersionCode(context)
                val response = NetworkClient.client.get("$BASE_URL/app-version")
                    val body: String = response.body()
                    val info = json.decodeFromString(AppUpdateInfo.serializer(), body)

                    if (info.latestVersionCode > installedVersionCode) {
                        Log.d(
                            TAG,
                            "Update available: ${info.latestVersionName} (code ${info.latestVersionCode}) > installed $installedVersionCode",
                        )
                        val resolved = info.copy(
                            storeUrl = info.storeUrl.ifEmpty { DEFAULT_PLAY_STORE_URL },
                            changelog = info.changelog.ifEmpty { "A new version is available — tap to update." },
                        )
                        _updateInfo.value = resolved
                        NotificationHelper.showUpdateNotification(
                            context = context,
                            newVersionName = resolved.latestVersionName,
                            storeUrl = resolved.storeUrl,
                            changelog = resolved.changelog,
                        )
                    } else {
                        Log.d(TAG, "App is up to date (installed=$installedVersionCode, latest=${info.latestVersionCode})")
                        _updateInfo.value = null
                    }
            } catch (e: Exception) {
                Log.w(TAG, "Version check failed: ${e.message}")
            }
        }
    }

    /** Opens the user's appropriate app store listing for the update. */
    fun openStore(context: Context) {
        val url = _updateInfo.value?.storeUrl ?: DEFAULT_PLAY_STORE_URL
        SafeLinkOpener.openUrl(context, url)
    }

    /**
     * Attempts an in-app self-update when a direct APK URL is available from the
     * backend; otherwise falls back to the app store listing.
     *
     * When [AppUpdateInfo.apkUrl] is non-empty, the APK is downloaded and the
     * system package installer is launched. ApkInstaller fully validates the
     * download first (magic bytes, package name, version code, signing
     * certificate) so a corrupted or mismatched APK never reaches the installer.
     *
     * If a signing conflict is detected (the downloaded APK is signed with a
     * different key than the installed build), ApkInstaller surfaces a
     * [ApkInstaller.Status.SIGNING_CONFLICT] state instead of triggering the
     * system "App not installed" dialog. The UI observes this and shows a
     * friendly dialog offering to uninstall the old version first.
     *
     * Returns true when an update flow was started (direct APK or store).
     */
    fun downloadAndInstall(context: Context): Boolean {
        val info = _updateInfo.value
        val apkUrl = info?.apkUrl?.ifBlank { null }
        if (apkUrl != null) {
            val storeUrl = info?.storeUrl?.ifBlank { DEFAULT_PLAY_STORE_URL } ?: DEFAULT_PLAY_STORE_URL
            scope.launch {
                ApkInstaller.downloadAndInstall(context, apkUrl, fallbackStoreUrl = storeUrl)
            }
            return true
        }
        openStore(context)
        return true
    }

    private fun installedVersionCode(context: Context): Int {
        return context.packageManager
            .getPackageInfo(context.packageName, 0)
            .let {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    it.longVersionCode.toInt()
                } else {
                    @Suppress("DEPRECATION")
                    it.versionCode
                }
            }
    }
}

/**
 * Response from the /app-version Cloudflare Worker endpoint.
 * Mirrors [AppVersionInfo] from the worker package but lives here so the
 * UI layer can observe it without depending on WorkManager internals.
 */
@Serializable
data class AppUpdateInfo(
    val latestVersionCode: Int = 0,
    val latestVersionName: String = "",
    val storeUrl: String = "",
    val changelog: String = "",
    /** Direct APK download URL — when non-empty, the app can self-update by
     * downloading and launching the system installer instead of going to the
     * Play Store. Empty means fall back to [storeUrl]. */
    val apkUrl: String = "",
)

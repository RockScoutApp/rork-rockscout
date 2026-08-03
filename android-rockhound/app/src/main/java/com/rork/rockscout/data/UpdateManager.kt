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
     * Routes the user down the right update path for how they installed the app.
     *
     * - **Installed from Google Play** → open the Play listing (the Play in-app
     *   update flow is started separately from the Activity on resume, so by the
     *   time this button is tapped the store page is the reliable action).
     * - **Sideloaded APK** → download the direct APK from [AppUpdateInfo.apkUrl]
     *   and install it over the existing app via [ApkInstaller], which validates
     *   the package and reports real failure reasons instead of the system's
     *   generic "App not installed" dialog.
     * - **No direct APK configured** → fall back to the store listing.
     *
     * Returns true when an update flow was started.
     */
    fun downloadAndInstall(context: Context): Boolean {
        val info = _updateInfo.value
        val storeUrl = info?.storeUrl?.ifBlank { DEFAULT_PLAY_STORE_URL } ?: DEFAULT_PLAY_STORE_URL

        // Pro-flavor builds must use the premium APK so a self-update never
        // downgrades a premium user to the trial build. Free-flavor builds
        // use the trial APK.
        val isProFlavor = com.rork.rockscout.BuildConfig.FORCE_PREMIUM
        val apkUrl = (if (isProFlavor) info?.premiumApkUrl else info?.apkUrl)?.ifBlank { null }

        // Play installs must update through Play — sideloading over a Play
        // build fails on signature mismatch (Play re-signs with its own key).
        if (PlayUpdateManager.isPlayInstall(context)) {
            openStore(context)
            return true
        }

        if (apkUrl != null) {
            ApkInstaller.start(context, apkUrl, fallbackStoreUrl = storeUrl)
            return true
        }
        openStore(context)
        return true
    }

    /** True when the backend published a direct APK for this release. */
    fun hasDirectApk(): Boolean {
        val info = _updateInfo.value ?: return false
        return if (com.rork.rockscout.BuildConfig.FORCE_PREMIUM) {
            info.premiumApkUrl.isNotBlank()
        } else {
            info.apkUrl.isNotBlank()
        }
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
    /** Direct APK download URL for the **free/trial** flavor — when non-empty,
     * the app can self-update by downloading and launching the system installer
     * instead of going to the Play Store. Empty means fall back to [storeUrl]. */
    val apkUrl: String = "",
    /** Direct APK download URL for the **premium/pro** flavor. Free-flavor
     * builds ignore this; pro-flavor builds use it instead of [apkUrl] so a
     * self-update never downgrades a premium user to the trial build. */
    val premiumApkUrl: String = "",
)

package com.rork.rockscout.data

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Google Play in-app updates.
 *
 * When the app was installed from the Play Store, Play can deliver a new
 * version without the user ever leaving RockScout. We use the FLEXIBLE flow:
 * the update downloads in the background while the app stays usable, then the
 * user is prompted to restart and apply it.
 *
 * When the app was NOT installed from Play (a sideloaded APK, or a device with
 * no Play Services), Play's API fails fast and we fall back to
 * [UpdateManager]'s direct-APK self-update path — so both distribution
 * channels stay covered with no user-visible difference.
 */
object PlayUpdateManager {

    private const val TAG = "PlayUpdateManager"

    /** True once a Play update has finished downloading and only needs a restart. */
    private val _readyToInstall = MutableStateFlow(false)
    val readyToInstall: StateFlow<Boolean> = _readyToInstall.asStateFlow()

    /** True when Play reports an update is available and the flow was started. */
    private val _playFlowActive = MutableStateFlow(false)
    val playFlowActive: StateFlow<Boolean> = _playFlowActive.asStateFlow()

    private var manager: AppUpdateManager? = null
    private var listener: InstallStateUpdatedListener? = null

    /**
     * True when the app came from the Play Store. Sideloaded builds return
     * false and should use the direct-APK updater instead.
     */
    fun isPlayInstall(context: Context): Boolean {
        return runCatching {
            val pm = context.packageManager
            val installer = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                pm.getInstallSourceInfo(context.packageName).installingPackageName
            } else {
                @Suppress("DEPRECATION")
                pm.getInstallerPackageName(context.packageName)
            }
            installer == "com.android.vending"
        }.getOrDefault(false)
    }

    /**
     * Checks Play for a newer build and starts the flexible update flow.
     * Safe to call on every `onResume` — Play de-duplicates internally.
     *
     * @param onUnavailable invoked when Play cannot serve an update (not a Play
     *        install, no Play Services, or already up to date) so the caller can
     *        fall back to the direct-APK updater.
     */
    fun checkAndStart(
        activity: Activity,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        onUnavailable: () -> Unit,
    ) {
        if (!isPlayInstall(activity)) {
            onUnavailable()
            return
        }

        val appUpdateManager = manager ?: runCatching {
            AppUpdateManagerFactory.create(activity.applicationContext)
        }.getOrNull()
        if (appUpdateManager == null) {
            Log.w(TAG, "Play update manager unavailable")
            onUnavailable()
            return
        }
        manager = appUpdateManager
        registerListener(appUpdateManager)

        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { info ->
                when {
                    // A previously started immediate update was interrupted —
                    // resume it so the app is never left half-updated.
                    info.updateAvailability() ==
                        UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                        startFlow(appUpdateManager, info, launcher, AppUpdateType.IMMEDIATE, onUnavailable)
                    }
                    // Already downloaded in a previous session — just complete it.
                    info.installStatus() == InstallStatus.DOWNLOADED -> {
                        _readyToInstall.value = true
                    }
                    info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE -> {
                        val type = if (info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                            AppUpdateType.FLEXIBLE
                        } else {
                            AppUpdateType.IMMEDIATE
                        }
                        startFlow(appUpdateManager, info, launcher, type, onUnavailable)
                    }
                    else -> {
                        Log.d(TAG, "Play reports app is up to date")
                        _playFlowActive.value = false
                        onUnavailable()
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Play update check failed: ${e.message}")
                _playFlowActive.value = false
                onUnavailable()
            }
    }

    private fun startFlow(
        appUpdateManager: AppUpdateManager,
        info: com.google.android.play.core.appupdate.AppUpdateInfo,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        type: Int,
        onUnavailable: () -> Unit,
    ) {
        val started = runCatching {
            appUpdateManager.startUpdateFlowForResult(
                info,
                launcher,
                AppUpdateOptions.newBuilder(type).build(),
            )
        }.isSuccess
        if (started) {
            _playFlowActive.value = true
            Log.d(TAG, "Started Play update flow (type=$type)")
        } else {
            Log.w(TAG, "Could not start Play update flow")
            _playFlowActive.value = false
            onUnavailable()
        }
    }

    private fun registerListener(appUpdateManager: AppUpdateManager) {
        if (listener != null) return
        val l = InstallStateUpdatedListener { state ->
            when (state.installStatus()) {
                InstallStatus.DOWNLOADED -> {
                    Log.d(TAG, "Play update downloaded — awaiting restart")
                    _readyToInstall.value = true
                }
                InstallStatus.INSTALLED, InstallStatus.CANCELED, InstallStatus.FAILED -> {
                    _readyToInstall.value = false
                    _playFlowActive.value = false
                }
                else -> Unit
            }
        }
        appUpdateManager.registerListener(l)
        listener = l
    }

    /** Applies a downloaded Play update — restarts the app into the new build. */
    fun completeUpdate() {
        val appUpdateManager = manager ?: return
        runCatching { appUpdateManager.completeUpdate() }
            .onFailure { Log.w(TAG, "completeUpdate failed: ${it.message}") }
    }

    /** Detach the listener when the hosting Activity is destroyed. */
    fun release() {
        val appUpdateManager = manager
        val l = listener
        if (appUpdateManager != null && l != null) {
            runCatching { appUpdateManager.unregisterListener(l) }
        }
        listener = null
    }
}

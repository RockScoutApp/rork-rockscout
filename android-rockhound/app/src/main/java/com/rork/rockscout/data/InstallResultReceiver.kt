package com.rork.rockscout.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import android.util.Log

/**
 * Receives the outcome of a [PackageInstaller] session started by [ApkInstaller].
 *
 * The session API is asynchronous: committing a session immediately returns and
 * the real result — including the confirmation prompt the user must approve —
 * arrives here as a broadcast. Handling it properly is what lets us surface the
 * *actual* reason an update failed instead of the system's generic
 * "App not installed" dialog.
 */
class InstallResultReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_INSTALL_RESULT) return

        val status = intent.getIntExtra(
            PackageInstaller.EXTRA_STATUS,
            PackageInstaller.STATUS_FAILURE,
        )
        val message = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)

        when (status) {
            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                // The system needs the user to confirm. Launch the confirmation
                // activity it handed us.
                val confirmIntent = extractConfirmIntent(intent)
                if (confirmIntent == null) {
                    Log.w(TAG, "Pending user action with no confirmation intent")
                    ApkInstaller.onInstallFailed(status, "Could not open the install confirmation.")
                    return
                }
                confirmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                runCatching { context.startActivity(confirmIntent) }
                    .onFailure { e ->
                        Log.e(TAG, "Failed to launch install confirmation", e)
                        ApkInstaller.onInstallFailed(status, "Could not open the install confirmation.")
                    }
            }

            PackageInstaller.STATUS_SUCCESS -> {
                Log.d(TAG, "Update installed successfully")
                ApkInstaller.onInstallSucceeded()
            }

            else -> {
                Log.w(TAG, "Install failed: status=$status message=$message")
                ApkInstaller.onInstallFailed(status, message)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun extractConfirmIntent(intent: Intent): Intent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Intent.EXTRA_INTENT, Intent::class.java)
        } else {
            intent.getParcelableExtra(Intent.EXTRA_INTENT)
        }
    }

    companion object {
        private const val TAG = "InstallResultReceiver"

        /** Explicit action used for the session-commit PendingIntent. */
        const val ACTION_INSTALL_RESULT = "com.rork.rockscout.action.INSTALL_RESULT"
    }
}

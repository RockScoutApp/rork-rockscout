// RockScout entry point
package com.rork.rockscout

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.activity.result.IntentSenderRequest
import com.rork.rockscout.data.ApkInstaller
import com.rork.rockscout.data.LocationRefresher
import com.rork.rockscout.data.PlayUpdateManager
import com.rork.rockscout.data.UpdateManager
import com.rork.rockscout.data.WorkScheduler
import com.rork.rockscout.ui.navigation.AppNavigation
import com.rork.rockscout.ui.screens.SplashScreen
import com.rork.rockscout.ui.theme.AppTheme
import androidx.compose.runtime.LaunchedEffect

class MainActivity : ComponentActivity() {

    private val deepLinkState = mutableStateOf<Uri?>(null)
    private val sharedImageState = mutableStateOf<Uri?>(null)

    /**
     * Android 13+ drops every notification the app posts until POST_NOTIFICATIONS
     * is granted. It used to only be requested from a handful of feature screens,
     * so update, proximity, social, trade, moderation and developer notifications
     * were silently discarded for anyone who never opened those screens. Ask once
     * at launch so every notification channel can actually deliver.
     */
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

    /**
     * Result channel for Google Play's in-app update flow. Play hands back a
     * confirmation UI through an IntentSender; a cancelled or failed flow just
     * leaves the app on the current version, and the direct-APK path stays
     * available as a fallback.
     */
    private val playUpdateLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                // User declined or Play failed — make sure the in-app
                // "Update Now" button still has fresh info to act on.
                UpdateManager.checkForUpdate(this)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()
        deepLinkState.value = intent?.data
        handleSharedImageIntent(intent)
        setContent {
            AppTheme {
                val showSplash = rememberSaveable { mutableStateOf(true) }
                if (showSplash.value) {
                    SplashScreen(
                        onFinished = { showSplash.value = false },
                    )
                } else {
                    AppNavigation(
                        deepLinkUri = deepLinkState.value,
                        sharedImageUri = sharedImageState.value,
                        onDeepLinkConsumed = { deepLinkState.value = null },
                        onSharedImageConsumed = { sharedImageState.value = null },
                    )
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private var lastOnResumeMs: Long = 0L

    override fun onResume() {
        super.onResume()
        // Throttle location + update network work so rapid back-navigation
        // doesn't re-fire both on every screen return (caused ANRs on heavy
        // screens). Minimum 4-second gap between refreshes.
        val now = System.currentTimeMillis()
        if (now - lastOnResumeMs < 4000L) return
        lastOnResumeMs = now
        LocationRefresher.refresh(this)

        // If the user just came back from granting "install unknown apps",
        // pick the interrupted APK update back up automatically.
        ApkInstaller.resumeIfReady(this)

        // Play-installed builds get Google's seamless in-app update flow;
        // everything else falls back to the direct-APK / store check.
        PlayUpdateManager.checkAndStart(
            activity = this,
            launcher = playUpdateLauncher,
            onUnavailable = { UpdateManager.checkForUpdate(this) },
        )
    }

    override fun onDestroy() {
        PlayUpdateManager.release()
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Update the deep-link state in place — do NOT call setContent again.
        // Re-calling setContent destroys and recreates the entire Compose tree
        // (including NavController state), which causes the app to freeze
        // when returning from an external browser/app via the back button.
        intent.data?.let { deepLinkState.value = it }
        handleSharedImageIntent(intent)
    }

    /** Extract an image URI from an ACTION_SEND intent (share-to-app from
     *  external apps like email, Facebook, Messages, etc.). */
    private fun handleSharedImageIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND) {
            val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            if (uri != null) {
                // Grant persistent read permission so we can copy the file later
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION,
                    )
                } catch (_: SecurityException) {
                    // Some providers don't support persistable permissions —
                    // the temporary grant from the share sheet is sufficient.
                }
                sharedImageState.value = uri
            }
        }
    }
}

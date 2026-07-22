// RockScout entry point
package com.rork.rockscout

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.rork.rockscout.data.LocationRefresher
import com.rork.rockscout.data.UpdateManager
import com.rork.rockscout.data.WorkScheduler
import com.rork.rockscout.ui.navigation.AppNavigation
import com.rork.rockscout.ui.screens.SplashScreen
import com.rork.rockscout.ui.theme.AppTheme
import androidx.compose.runtime.LaunchedEffect

class MainActivity : ComponentActivity() {

    private val deepLinkState = mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        deepLinkState.value = intent?.data
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
                        onDeepLinkConsumed = { deepLinkState.value = null },
                    )
                }
            }
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
        UpdateManager.checkForUpdate(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Update the deep-link state in place — do NOT call setContent again.
        // Re-calling setContent destroys and recreates the entire Compose tree
        // (including NavController state), which causes the app to freeze
        // when returning from an external browser/app via the back button.
        intent.data?.let { deepLinkState.value = it }
    }
}

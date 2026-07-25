package com.rork.rockscout.ui.components

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Embedded YouTube player using a WebView with the YouTube IFrame API.
 * Loads the channel page so the user can watch live streams or latest videos
 * without leaving the app.
 *
 * @param channelUrl The YouTube channel URL to load.
 * @param onClose Called when the user dismisses the player.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubePlayer(
    channelUrl: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler { onClose() }

    var webView by remember { mutableStateOf<WebView?>(null) }

    // Destroy the WebView when the composable leaves composition to prevent
    // memory leaks (WebView holds JS engine, DOM, and media resources).
    DisposableEffect(Unit) {
        onDispose {
            webView?.let { wv ->
                runCatching {
                    wv.stopLoading()
                    wv.webChromeClient = null
                    wv.webViewClient = WebViewClient()
                    wv.destroy()
                }
            }
            webView = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowFileAccess = false
                    settings.mediaPlaybackRequiresUserGesture = false
                    webViewClient = WebViewClient()
                    // WebChromeClient is required for YouTube IFrame API
                    // fullscreen, JS dialogs, and video lifecycle.
                    webChromeClient = WebChromeClient()
                    loadUrl(channelUrl)
                    webView = this
                }
            },
            update = { wv ->
                // Only reload if the URL actually changed AND isn't a redirect
                // from the channel page (avoids reload loops on YouTube
                // channel URL → watch page redirects).
                if (wv.url != channelUrl && !wv.url.isNullOrEmpty()) {
                    // Don't reload — let YouTube's internal navigation work
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1C1A14).copy(alpha = 0.85f))
                .clickable { onClose() }
                .padding(8.dp),
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Close player",
                tint = Color.White,
                modifier = Modifier.padding(2.dp),
            )
        }
    }
}

package com.rork.rockscout.data

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import android.util.Log

/**
 * Safe wrapper for launching external intents (browser, maps, email, share, etc.).
 *
 * Every [Context.startActivity] call in the app should go through here so that
 * a missing handler app never crashes or freezes the UI. When no app can handle
 * the intent, a user-friendly toast is shown instead.
 */
object SafeLinkOpener {

    private const val TAG = "SafeLinkOpener"

    /**
     * Open a URL in the system browser (or app that handles the URL scheme).
     * Shows a toast if no browser is available.
     */
    fun openUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        launch(context, intent, "No app available to open this link.")
    }

    /**
     * Open a geo: URI in a maps app, falling back to a generic geo intent
     * if Google Maps isn't installed.
     */
    fun openMaps(context: Context, googleMapsUri: String, fallbackGeoUri: String) {
        val primary = Intent(Intent.ACTION_VIEW, Uri.parse(googleMapsUri)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            setPackage("com.google.android.apps.maps")
        }
        val fallback = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackGeoUri)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(primary) }
            .onFailure {
                Log.w(TAG, "Google Maps not available, trying generic geo intent: ${it.message}")
                launch(context, fallback, "No maps app found.")
            }
    }

    /**
     * Open multi-stop directions in Google Maps (or fallback navigation app).
     * Constructs a Google Maps directions URL with waypoints for all stops.
     * Fallback chain: Google Maps app → cross-platform maps URL (browser) →
     * generic geo: intent → toast. Never forces Google Maps — works with Waze,
     * OsmAnd, or any installed navigation app.
     *
     * @param stops List of (lat, lng) pairs in route order. First = origin,
     *        last = destination, middle = waypoints.
     */
    fun openMultiStopDirections(context: Context, stops: List<Pair<Double, Double>>) {
        if (stops.isEmpty()) return
        if (stops.size == 1) {
            val (lat, lng) = stops[0]
            openMaps(
                context,
                "google.navigation:q=$lat,$lng",
                "geo:$lat,$lng?q=$lat,$lng",
            )
            return
        }
        val destination = stops.last()
        val waypoints = stops.dropLast(1).drop(1) // exclude origin
        val waypointStr = waypoints.joinToString("|") { "${it.first},${it.second}" }
        val mapsUrl = if (waypointStr.isEmpty()) {
            "https://www.google.com/maps/dir/?api=1&destination=${destination.first},${destination.second}&travelmode=driving"
        } else {
            "https://www.google.com/maps/dir/?api=1&destination=${destination.first},${destination.second}&waypoints=$waypointStr&travelmode=driving"
        }
        // Try Google Maps app first
        val gmapsIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            setPackage("com.google.android.apps.maps")
        }
        // Fallback: open in browser (redirects to whatever nav app is installed)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(gmapsIntent) }
            .onFailure {
                Log.w(TAG, "Google Maps not available, trying browser URL: ${it.message}")
                launch(context, browserIntent, "No maps app found. Install Google Maps or another navigation app for directions.")
            }
    }

    /**
     * Open point-to-point directions from origin to destination.
     * Uses Google Maps URL with explicit origin and destination.
     */
    fun openPointToPointDirections(context: Context, origin: Pair<Double, Double>, destination: Pair<Double, Double>) {
        val mapsUrl = "https://www.google.com/maps/dir/?api=1&origin=${origin.first},${origin.second}&destination=${destination.first},${destination.second}&travelmode=driving"
        val gmapsIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            setPackage("com.google.android.apps.maps")
        }
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(gmapsIntent) }
            .onFailure {
                Log.w(TAG, "Google Maps not available, trying browser URL: ${it.message}")
                launch(context, browserIntent, "No maps app found. Install Google Maps or another navigation app for directions.")
            }
    }

    /**
     * Open a plain geo: URI in any available maps app.
     */
    fun openGeo(context: Context, geoUri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        launch(context, intent, "No maps app found.")
    }

    /**
     * Launch a share intent via a chooser dialog.
     * Shows a toast if no sharing app is available.
     */
    fun openShareChooser(context: Context, shareIntent: Intent, title: String) {
        val chooser = Intent.createChooser(shareIntent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        launch(context, chooser, "No app available to share.")
    }

    /**
     * Launch an arbitrary intent via a chooser dialog.
     * Shows a toast if no handler is available.
     */
    fun openChooser(context: Context, intent: Intent, title: String) {
        val chooser = Intent.createChooser(intent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        launch(context, chooser, "No app available to handle this.")
    }

    /**
     * Core launch helper — catches [ActivityNotFoundException] and any other
     * exception, showing a toast instead of letting the app crash or freeze.
     */
    fun launch(context: Context, intent: Intent, errorMessage: String) {
        runCatching {
            context.startActivity(intent)
        }.onFailure { e ->
            Log.w(TAG, "startActivity failed: ${e.message}")
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}

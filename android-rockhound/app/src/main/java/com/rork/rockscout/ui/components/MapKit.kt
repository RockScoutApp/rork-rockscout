package com.rork.rockscout.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CompletableDeferred
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import android.view.MotionEvent
import com.rork.rockscout.data.DigLocation
import com.rork.rockscout.data.LocationType
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.Trip
import com.rork.rockscout.data.TripStop
import com.rork.rockscout.data.DigSiteSearchService
import com.rork.rockscout.data.CustomDigLocationStore
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.Warning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.modules.INetworkAvailablityCheck
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver
import org.osmdroid.util.MapTileIndex
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlin.math.roundToInt
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.tan
import kotlin.math.log
import kotlin.math.floor
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import com.rork.rockscout.data.OfflineMapExporter
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.PersistenceManager
import com.rork.rockscout.data.UserPinSubmissionStore
import com.rork.rockscout.data.ProfanityFilter
import com.rork.rockscout.data.SpecimenMarker
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.ImeAction
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.DarkTextHigh
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rork.rockscout.ui.components.glowingBorder

/**
 * Tracks whether the device currently has an active internet connection.
 * Used by map screens to show an offline notice when tiles can't load.
 */
@Composable
fun rememberNetworkOnline(): State<Boolean> {
    val context = LocalContext.current
    return produceState(initialValue = isOnline(context)) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (cm != null) {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: android.net.Network) { value = true }
                override fun onLost(network: android.net.Network) { value = isOnline(context) }
            }
            cm.registerDefaultNetworkCallback(callback)
            awaitDispose { cm.unregisterNetworkCallback(callback) }
        }
    }
}

/** Synchronous connectivity check — true if any network has internet capability. */
private fun isOnline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return true
    // Modern API (API 23+).
    val nw = cm.activeNetwork
    if (nw != null) {
        val caps = cm.getNetworkCapabilities(nw)
        if (caps != null) {
            return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN))
        }
    }
    // Fallback for older devices or emulators where the modern API returns null.
    @Suppress("DEPRECATION")
    val activeInfo = cm.activeNetworkInfo
    return activeInfo?.isConnected ?: true
}

/**
 * Semi-transparent banner shown on top of the map when the device is offline.
 * Lets the user know why no map tiles are loading.
 */
@Composable
fun MapOfflineNotice(modifier: Modifier = Modifier) {
    val isOnline by rememberNetworkOnline()
    if (isOnline) return
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xE6000000))
            .glowingBorder(2.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            Icons.Filled.LocationDisabled,
            contentDescription = null,
            tint = Warning,
            modifier = Modifier.size(16.dp),
        )
        Text(
            "Offline — showing cached tiles",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Sync status for a map's offline cache. Driven by the timestamps recorded by
 * [MapTileCacheManager] in [PersistenceManager]. The indicator tells the user
 * at a glance whether the visible area is cached, how fresh the cache is, and
 * whether a refresh is recommended (tiles older than [STALE_AFTER_MS]).
 *
 * States:
 * - [Mode.UNKNOWN]      — no cache record for this area yet (shows a subtle "Not cached" pill).
 * - [Mode.FRESH]        — cached < 24h ago (green CloudDone, "Cached Xm / Xh ago").
 * - [Mode.CACHED]       — cached < 7 days ago (aqua CloudSync, "Cached Xd ago").
 * - [Mode.STALE]        — cached >= 7 days ago (warning CloudSync + Refresh hint,
 *                         "Cached Xd ago • Tap refresh").
 * - [Mode.OFFLINE_FRESH]— device offline AND tiles are cached (green, "Offline • tiles fresh").
 * - [Mode.OFFLINE_STALE]— device offline AND tiles are stale or unknown (warning,
 *                         "Offline • tiles may be stale").
 */
enum class CacheSyncState { UNKNOWN, FRESH, CACHED, STALE, OFFLINE_FRESH, OFFLINE_STALE }

/** Tiles older than this are considered stale (7 days). */
private const val CACHE_STALE_AFTER_MS = 7L * 24 * 60 * 60 * 1000

/** Tiles younger than this are "fresh" (24h). */
private const val CACHE_FRESH_AFTER_MS = 24L * 60 * 60 * 1000

/**
 * Resolves the [CacheSyncState] for a cached-at timestamp, taking the device's
 * online state into account. Returns [CacheSyncState.UNKNOWN] when
 * [cachedAtMillis] is null.
 */
fun resolveCacheSyncState(cachedAtMillis: Long?, isOnline: Boolean): CacheSyncState {
    if (cachedAtMillis == null) {
        return if (isOnline) CacheSyncState.UNKNOWN else CacheSyncState.OFFLINE_STALE
    }
    val age = System.currentTimeMillis() - cachedAtMillis
    val isStale = age >= CACHE_STALE_AFTER_MS
    val isFresh = age < CACHE_FRESH_AFTER_MS
    return when {
        !isOnline && isStale -> CacheSyncState.OFFLINE_STALE
        !isOnline -> CacheSyncState.OFFLINE_FRESH
        isStale -> CacheSyncState.STALE
        isFresh -> CacheSyncState.FRESH
        else -> CacheSyncState.CACHED
    }
}

/** Formats a cached-at millis as a human-readable "Xm / Xh / Xd ago" string. */
fun formatCacheAge(cachedAtMillis: Long?): String {
    if (cachedAtMillis == null) return "Not cached"
    val ageMs = System.currentTimeMillis() - cachedAtMillis
    if (ageMs < 0) return "just now"
    val minutes = ageMs / 60_000
    val hours = ageMs / 3_600_000
    val days = ageMs / 86_400_000
    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 30 -> "${days}d ago"
        else -> "${days / 30}mo ago"
    }
}

/**
 * Pill-shaped indicator that shows the current sync status of the cached map
 * tiles for the visible area. Tapping it triggers [onRefresh] (typically a
 * re-prefetch of the visible area's tiles). When the device is offline, the
 * pill is non-interactive and only reports staleness.
 */
@Composable
fun MapCacheStatusIndicator(
    cachedAtMillis: Long?,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Offline maps",
) {
    val isOnline by rememberNetworkOnline()
    val state = remember(cachedAtMillis, isOnline) {
        resolveCacheSyncState(cachedAtMillis, isOnline)
    }
    val ageText = remember(cachedAtMillis) { formatCacheAge(cachedAtMillis) }

    val (icon, tint, bg, border, subtitle, clickable) = when (state) {
        CacheSyncState.UNKNOWN -> Six(Icons.Filled.CloudOff, DarkTextMid,
            Color(0xE6000000), Color.White.copy(alpha = 0.15f),
            "tap to cache", isOnline)
        CacheSyncState.FRESH -> Six(Icons.Filled.CloudDone, Success,
            Color(0xE6000000), Success.copy(alpha = 0.45f),
            "Up to date", true)
        CacheSyncState.CACHED -> Six(Icons.Filled.CloudDone, Aqua,
            Color(0xE6000000), Aqua.copy(alpha = 0.45f),
            "Cached", true)
        CacheSyncState.STALE -> Six(Icons.Filled.CloudSync, Warning,
            Color(0xE6000000), Warning.copy(alpha = 0.55f),
            "Stale • tap to refresh", true)
        CacheSyncState.OFFLINE_FRESH -> Six(Icons.Filled.CloudDone, Success,
            Color(0xE6000000), Success.copy(alpha = 0.45f),
            "Offline • tiles fresh", false)
        CacheSyncState.OFFLINE_STALE -> Six(Icons.Filled.CloudOff, Warning,
            Color(0xE6000000), Warning.copy(alpha = 0.55f),
            "Offline • may be stale", false)
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .glowingBorder(2.dp, border, RoundedCornerShape(10.dp))
            .let { if (clickable) it.clickable(onClick = onRefresh) else it }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
        Column {
            Text(
                label,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "$ageText • $subtitle",
                color = Color.White.copy(alpha = 0.78f),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** Tiny tuple helper so we can return 6 values from a `when` without a data class. */
private class Six<A, B, C, D, E, F>(
    val a: A, val b: B, val c: C, val d: D, val e: E, val f: F,
)

private operator fun <A, B, C, D, E, F> Six<A, B, C, D, E, F>.component1() = a
private operator fun <A, B, C, D, E, F> Six<A, B, C, D, E, F>.component2() = b
private operator fun <A, B, C, D, E, F> Six<A, B, C, D, E, F>.component3() = c
private operator fun <A, B, C, D, E, F> Six<A, B, C, D, E, F>.component4() = d
private operator fun <A, B, C, D, E, F> Six<A, B, C, D, E, F>.component5() = e
private operator fun <A, B, C, D, E, F> Six<A, B, C, D, E, F>.component6() = f

/**
 * Modern network availability check for osmdroid.
 *
 * osmdroid 6.1.20's built-in NetworkAvailablityCheck uses the deprecated
 * ConnectivityManager.getActiveNetworkInfo() which returns null on Android 10+
 * (API 29+), causing osmdroid to think the device is offline even with perfect
 * internet — so no map tiles ever download. This replacement uses the modern
 * NetworkCapabilities API to correctly detect connectivity on all Android versions.
 */
private class ModernNetworkAvailabilityCheck(
    private val context: Context,
) : INetworkAvailablityCheck {

    private val connectivityManager: ConnectivityManager? =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    override fun getNetworkAvailable(): Boolean {
        val cm = connectivityManager ?: return true
        val network = cm.activeNetwork
        if (network != null) {
            val caps = cm.getNetworkCapabilities(network)
            if (caps != null) {
                return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                        caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN))
            }
        }
        // Fallback to the deprecated API so emulators and older devices still load tiles.
        @Suppress("DEPRECATION")
        val activeInfo = cm.activeNetworkInfo
        return activeInfo?.isConnected ?: true
    }

    override fun getWiFiNetworkAvailable(): Boolean = getNetworkAvailable()

    override fun getCellularDataNetworkAvailable(): Boolean = getNetworkAvailable()

    override fun getRouteToPathExists(road: Int): Boolean = getNetworkAvailable()
}

/**
 * Shared single-thread executor for MapView detach work. Detaching an osmdroid
 * MapView does disk/tile-cache cleanup that can take a few hundred ms, so it
 * must not run on the main thread. Routing all detaches through one daemon
 * thread avoids spawning an unbounded number of bare `Thread`s when several
 * map screens are entered and left in quick succession (each spawned thread
 * would otherwise remain non-daemon and block process teardown until it
 * finishes). The executor is process-scoped and never needs explicit shutdown.
 */
private val mapDetachExecutor: java.util.concurrent.ExecutorService =
    java.util.concurrent.Executors.newSingleThreadExecutor { r ->
        Thread(r, "RockScout-MapDetach").apply { isDaemon = true }
    }

/**
 * Wires an osmdroid [MapView] to the host composable's lifecycle so it receives
 * onResume/onPause and is detached when the composable leaves. Without this,
 * MapViews inside Compose never get resumed and tiles stay blank.
 */
@Composable
fun MapViewLifecycleEffect(mapView: MapView?) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(mapView, lifecycle) {
        var wasResumed = false
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    mapView?.onResume()
                    wasResumed = true
                }
                Lifecycle.Event.ON_PAUSE -> {
                    mapView?.onPause()
                    wasResumed = false
                }
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            mapView?.onResume()
            wasResumed = true
        }
        onDispose {
            lifecycle.removeObserver(observer)
            if (wasResumed) {
                runCatching { mapView?.onPause() }
            }
            // Detach on the shared background executor to avoid blocking the
            // UI. runCatching guards against the MapView being concurrently
            // accessed by coroutines that haven't fully cancelled yet.
            mapView?.let { mv ->
                mapDetachExecutor.execute { runCatching { mv.onDetach() } }
            }
        }
    }
}

/**
 * Creates a [MapView] with a [MapTileProviderBasic] that uses [ModernNetworkAvailabilityCheck]
 * instead of osmdroid's broken built-in check. This ensures tiles download correctly on
 * Android 10+ (API 29+) where the deprecated getActiveNetworkInfo() returns null.
 *
 * The returned MapView has the CartoDbVoyager tile source pre-applied and standard settings
 * (multi-touch, no built-in zoom controls, no map repetition, data connection enabled).
 * Callers should add their own overlays, compass, markers, etc.
 */
/** Zoom level at which the map switches from street tiles to satellite + labels. */
private const val SATELLITE_ZOOM_THRESHOLD = 16.0

/** Max zoom for the OpenTopoMap tile source. Topo tiles are blank above this. */
private const val TOPO_MAX_ZOOM = 17.0

/** Default max zoom for street/satellite sources. */
private const val DEFAULT_MAX_ZOOM = 19.0

/** Tracks which maps have a manually-selected layer override. Auto-hybrid switching is disabled for these maps. */
private val manualLayerOverrides = java.util.WeakHashMap<MapView, MapLayerStyle>()

private fun setManualLayer(mapView: MapView, style: MapLayerStyle?) {
    if (style == null || style == MapLayerStyle.STREET) {
        manualLayerOverrides.remove(mapView)
    } else {
        manualLayerOverrides[mapView] = style
    }
}

private fun getManualLayer(mapView: MapView): MapLayerStyle? = manualLayerOverrides[mapView]

fun createRockScoutMapView(context: Context, readOnly: Boolean = false): MapView {
    // The global osmdroid user agent is already configured in RockScoutApplication.
    // Make sure the tile cache directory exists on disk before the provider tries
    // to open its SqlTileWriter database.
    org.osmdroid.config.Configuration.getInstance().osmdroidTileCache?.mkdirs()

    val registerReceiver = SimpleRegisterReceiver(context)
    val networkCheck = ModernNetworkAvailabilityCheck(context)
    // Pass null for the cache writer so osmdroid creates its own SqlTileWriter
    // using the configured cache path. This avoids multiple custom SqlTileWriter
    // instances contending for the same static database and failing to load tiles.
    val tileProvider = MapTileProviderBasic(
        registerReceiver,
        networkCheck,
        CartoDbVoyager,
        context,
        null,
    )
    val labelProvider = MapTileProviderBasic(
        SimpleRegisterReceiver(context),
        networkCheck,
        CartoDbVoyagerLabels,
        context,
        null,
    )
    val labelOverlay = HybridLabelOverlay(labelProvider, context).apply {
        loadingBackgroundColor = android.graphics.Color.TRANSPARENT
        loadingLineColor = android.graphics.Color.TRANSPARENT
    }
    val view = object : MapView(context, tileProvider) {
        override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
            // Prevent scrollable Compose parents (LazyColumn, verticalScroll Column)
            // from stealing the drag events while the user is panning or pinching
            // the map. Without this, the map feels jittery because the parent
            // scrollable intercepts part of the gesture and competes for frames.
            val parentView = parent
            if (parentView != null && ev != null) {
                when (ev.actionMasked) {
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_MOVE,
                    MotionEvent.ACTION_POINTER_DOWN,
                    -> parentView.requestDisallowInterceptTouchEvent(true)
                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL,
                    -> parentView.requestDisallowInterceptTouchEvent(false)
                }
            }
            return super.dispatchTouchEvent(ev)
        }
    }
    return view.apply {
        // Render the map in software so panning/zooming is smooth and free of the
        // tile-tearing / stutter artifacts that osmdroid often shows under hardware
        // acceleration inside a Compose host.
        setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
        setMultiTouchControls(!readOnly)
        setBuiltInZoomControls(false)
        minZoomLevel = 2.0
        maxZoomLevel = 19.0  // Esri World Imagery supports zoom 19 for maximum satellite detail
        isHorizontalMapRepetitionEnabled = false
        isVerticalMapRepetitionEnabled = false
        setUseDataConnection(true)
        setTilesScaledToDpi(true)

        addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?) = false
            override fun onZoom(event: ZoomEvent?): Boolean {
                // Don't override a manually-selected layer (Satellite / Topo) when the user zooms.
                if (getManualLayer(this@apply) == null) {
                    applyAutoHybridTiles(this@apply, labelOverlay)
                }
                return false
            }
        })
        applyAutoHybridTiles(this, labelOverlay)
    }
}

/**
 * Switches the map's tile source between street and satellite based on the
 * current zoom level. At [SATELLITE_ZOOM_THRESHOLD] and above, the base layer
 * becomes Esri World Imagery and a transparent CartoDB label overlay is added
 * for a Google-Maps hybrid look. Below the threshold, the clean street map
 * tiles are used.
 */
private fun applyAutoHybridTiles(mapView: MapView, labelOverlay: HybridLabelOverlay) {
    // Never override a manually-selected layer (Satellite / Topo) with the auto-hybrid switch.
    if (getManualLayer(mapView) != null) return

    val zoom = mapView.zoomLevelDouble
    val isClose = zoom >= SATELLITE_ZOOM_THRESHOLD
    val currentSource = mapView.tileProvider?.tileSource
    val targetSource = if (isClose) EsriWorldImagery else CartoDbVoyager

    if (currentSource !== targetSource) {
        // Clear the in-memory tile cache so stale tiles from the previous
        // source don't bleed into the new source's render (fixes the
        // half-street / half-satellite tile mixing bug).
        runCatching { mapView.tileProvider?.clearTileCache() }
        mapView.setTileSource(targetSource)
    }
    if (isClose) {
        if (mapView.overlays.none { it === labelOverlay }) {
            mapView.overlays.add(labelOverlay)
        }
    } else {
        mapView.overlays.remove(labelOverlay)
    }
    mapView.invalidate()
}

/** Zoom level for the first satellite toggle tap — satellite imagery appears here. */
private const val SATELLITE_JUMP_ZOOM = 16.0

/** Max zoom for the second satellite toggle tap — deepest satellite detail. */
private const val SATELLITE_MAX_ZOOM = 19.0

/** Street-level overview zoom used when toggling satellite off (3rd tap). */
private const val SATELLITE_OVERVIEW_ZOOM = 12.5

/**
 * Per-MapView state for the 3-state satellite toggle cycle.
 * Tracks which stage of the cycle the user is on and the original zoom
 * to restore on the 3rd tap.
 */
private class SatelliteToggleState {
    var stage: Int = 0  // 0 = street, 1 = zoom 16, 2 = zoom 19
    var originalZoom: Double = 0.0
}

/** Weak map so state is cleaned up when MapView instances are GC'd. */
private val satelliteToggleStates = java.util.WeakHashMap<MapView, SatelliteToggleState>()

/**
 * Three-state satellite toggle:
 * - 1st tap: zoom to [SATELLITE_JUMP_ZOOM] (16) — satellite imagery appears.
 * - 2nd tap: zoom to [SATELLITE_MAX_ZOOM] (19) — deepest satellite detail.
 * - 3rd tap: return to the original zoom the map was at before the first tap.
 *
 * The zoom listener in [createRockScoutMapView] handles switching the tile
 * source to Esri World Imagery with label overlays automatically when
 * crossing [SATELLITE_ZOOM_THRESHOLD]. Manual pinch-zoom still works at
 * all times.
 */
fun toggleSatelliteView(mapView: MapView?) {
    val mv = mapView ?: return
    val state = satelliteToggleStates.getOrPut(mv) { SatelliteToggleState() }

    when (state.stage) {
        0 -> {
            // Save original zoom before jumping to satellite
            state.originalZoom = mv.zoomLevelDouble
            mv.controller.setZoom(SATELLITE_JUMP_ZOOM)
            state.stage = 1
        }
        1 -> {
            // Zoom in deeper to max satellite detail
            mv.controller.setZoom(SATELLITE_MAX_ZOOM)
            state.stage = 2
        }
        else -> {
            // Return to original view
            mv.controller.setZoom(state.originalZoom)
            state.stage = 0
            state.originalZoom = 0.0
        }
    }
    mv.invalidate()
}

/**
 * Clean, Google-Maps-like base map from CartoDB Voyager (OpenStreetMap data).
 */
val CartoDbVoyager: OnlineTileSourceBase = object : OnlineTileSourceBase(
    "CartoDB Voyager",
    0,
    19,
    256,
    ".png",
    arrayOf("https://basemaps.cartocdn.com/rastertiles/voyager/"),
    "© OpenStreetMap contributors, © CartoDB",
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        val z = MapTileIndex.getZoom(pMapTileIndex)
        val x = MapTileIndex.getX(pMapTileIndex)
        val y = MapTileIndex.getY(pMapTileIndex)
        return "${getBaseUrl()}$z/$x/$y.png"
    }
}

/**
 * Global Esri World Imagery satellite tiles. Switched on automatically at
 * zoom 16+ so users can see terrain, parking lots, trailheads, and exact dig
 * spots. Supports zoom up to 19 for maximum detail.
 */
val EsriWorldImagery: OnlineTileSourceBase = object : OnlineTileSourceBase(
    "ESRI World Imagery",
    0,
    19,  // Esri native max zoom
    256,
    ".png",
    arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/"),
    "© Esri",
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        val z = MapTileIndex.getZoom(pMapTileIndex)
        val x = MapTileIndex.getX(pMapTileIndex)
        val y = MapTileIndex.getY(pMapTileIndex)
        return "${getBaseUrl()}$z/$y/$x.png"
    }
}

/**
 * Transparent OpenStreetMap label tiles (streets, place names, boundaries).
 * Overlaid on satellite imagery for a Google Maps hybrid look. Uses CartoDB
 * Voyager "only labels" raster tiles — free, no API key.
 */
val CartoDbVoyagerLabels: OnlineTileSourceBase = object : OnlineTileSourceBase(
    "CartoDB Voyager Labels",
    0,
    19,
    256,
    ".png",
    arrayOf("https://basemaps.cartocdn.com/rastertiles/voyager_only_labels/"),
    "© OpenStreetMap contributors, © CartoDB",
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        val z = MapTileIndex.getZoom(pMapTileIndex)
        val x = MapTileIndex.getX(pMapTileIndex)
        val y = MapTileIndex.getY(pMapTileIndex)
        return "${getBaseUrl()}$z/$x/$y.png"
    }
}

/**
 * OpenTopoMap topographic tile source — contour lines, terrain shading, and trail
 * detail. Free, no API key. Max zoom 17. Great for terrain planning when the user
 * wants to see elevation contours and topographic features.
 */
val OpenTopoMap: OnlineTileSourceBase = object : OnlineTileSourceBase(
    "OpenTopoMap",
    0,
    17,
    256,
    ".png",
    arrayOf(
        "https://a.tile.opentopomap.org/",
        "https://b.tile.opentopomap.org/",
        "https://c.tile.opentopomap.org/",
    ),
    "© OpenStreetMap contributors, © OpenTopoMap",
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        val z = MapTileIndex.getZoom(pMapTileIndex)
        val x = MapTileIndex.getX(pMapTileIndex)
        val y = MapTileIndex.getY(pMapTileIndex)
        return "${getBaseUrl()}$z/$x/$y.png"
    }
}

/** Map layer styles for the 3-way layer toggle. */
enum class MapLayerStyle(val label: String) {
    STREET("Street"),
    SATELLITE("Sat"),
    TOPO("Topo"),
}

/**
 * Cycles the map's tile source through Street → Satellite → Topo.
 * Clears the in-memory tile cache so stale tiles don't bleed between sources.
 * Records a manual layer override so the auto-hybrid listener doesn't switch
 * the source back to street/satellite as the user zooms. Topo mode caps the
 * max zoom to [TOPO_MAX_ZOOM] so tiles always render and the map stays fully
 * zoomable/pannable within the supported range.
 *
 * Returns the next layer style in the cycle.
 */
fun toggleMapLayer(mapView: MapView?, currentLayer: MapLayerStyle): MapLayerStyle {
    val mv = mapView ?: return currentLayer
    val next = when (currentLayer) {
        MapLayerStyle.STREET -> MapLayerStyle.SATELLITE
        MapLayerStyle.SATELLITE -> MapLayerStyle.TOPO
        MapLayerStyle.TOPO -> MapLayerStyle.STREET
    }
    val targetSource = when (next) {
        MapLayerStyle.STREET -> CartoDbVoyager
        MapLayerStyle.SATELLITE -> EsriWorldImagery
        MapLayerStyle.TOPO -> OpenTopoMap
    }

    // Remember the user's choice so auto-hybrid doesn't override it.
    setManualLayer(mv, if (next == MapLayerStyle.STREET) null else next)

    // Cap zoom and max zoom for the Topo source so tiles never go blank.
    if (next == MapLayerStyle.TOPO) {
        mv.maxZoomLevel = TOPO_MAX_ZOOM
        if (mv.zoomLevelDouble > TOPO_MAX_ZOOM) {
            mv.controller.setZoom(TOPO_MAX_ZOOM)
        }
    } else {
        mv.maxZoomLevel = DEFAULT_MAX_ZOOM
    }

    runCatching { mv.tileProvider?.clearTileCache() }
    mv.setTileSource(targetSource)
    mv.invalidate()
    return next
}

/**
 * Creates a styled dashed route polyline with a dark outline shadow for contrast.
 * Used by both [TripRouteMap] and [FullscreenRouteMapOverlay].
 */
private fun createRoutePolyline(context: Context, points: List<GeoPoint>): Polyline {
    val density = context.resources.displayMetrics.density
    return Polyline().apply {
        setPoints(points)
        // Outline shadow — dark, thick, drawn behind the main line
        outlinePaint.strokeWidth = 10f * density
        outlinePaint.color = android.graphics.Color.argb(120, 0, 0, 0)
        outlinePaint.isAntiAlias = true
        outlinePaint.style = Paint.Style.STROKE
        outlinePaint.strokeCap = Paint.Cap.ROUND
    }
}

/**
 * Applies the dashed citrine pattern + dark outline to a [Polyline].
 * The outline is a separate Polyline drawn behind the main dashed line.
 */
private fun styleRoutePolyline(polyline: Polyline, density: Float) {
    polyline.outlinePaint.strokeWidth = 8f * density
    polyline.outlinePaint.color = Citrine.toArgb()
    polyline.outlinePaint.isAntiAlias = true
    polyline.outlinePaint.style = Paint.Style.STROKE
    polyline.outlinePaint.strokeCap = Paint.Cap.ROUND
    polyline.outlinePaint.pathEffect = DashPathEffect(floatArrayOf(16f * density, 8f * density), 0f)
}

/**
 * Creates a small directional arrow [Marker] at the midpoint of a route segment,
 * rotated to match the bearing from [start] to [end].
 */
private fun createDirectionalArrow(
    mapView: MapView,
    start: GeoPoint,
    end: GeoPoint,
): Marker {
    val midLat = (start.latitude + end.latitude) / 2.0
    val midLng = (start.longitude + end.longitude) / 2.0
    val bearing = Math.toDegrees(
        kotlin.math.atan2(
            end.longitude - start.longitude,
            end.latitude - start.latitude,
        ).toDouble()
    ).toFloat()
    return Marker(mapView).apply {
        id = "route_arrow_${midLat}_${midLng}"
        position = GeoPoint(midLat, midLng)
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        icon = createArrowDrawable(mapView.context, bearing)
    }
}

/** Creates a small triangular arrow Drawable rotated to [bearing] degrees. */
private fun createArrowDrawable(
    context: Context,
    bearing: Float,
): Drawable {
    val density = context.resources.displayMetrics.density
    val sizePx = (24 * density).toInt()
    val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val cx = sizePx / 2f
    val cy = sizePx / 2f
    val r = sizePx / 3f

    canvas.save()
    canvas.rotate(bearing, cx, cy)

    // White circle background for visibility
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(cx, cy, r, paint)

    // Citrine arrow triangle pointing up (north)
    paint.color = Citrine.toArgb()
    val path = Path().apply {
        moveTo(cx, cy - r * 0.6f)
        lineTo(cx - r * 0.45f, cy + r * 0.3f)
        lineTo(cx + r * 0.45f, cy + r * 0.3f)
        close()
    }
    canvas.drawPath(path, paint)

    // White ring
    paint.color = android.graphics.Color.WHITE
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 1.5f * density
    canvas.drawCircle(cx, cy, r, paint)

    canvas.restore()
    return BitmapDrawable(context.resources, bmp)
}

/** Tag interface so we can find/remove the hybrid label overlay from the overlay list. */
private class HybridLabelOverlay(
    provider: org.osmdroid.tileprovider.MapTileProviderBase,
    context: Context,
) : TilesOverlay(provider, context)

/**
 * Configures a MapView with a reliable, Google-Maps-like street/terrain base
 * layer. Uses CartoDB Voyager (OpenStreetMap data) which is free, requires no
 * API key, and loads reliably on devices and in test environments. The map
 * still auto-switches to Esri satellite + labels at close zoom via the
 * listener installed in [createRockScoutMapView]. Tiles are cached by osmdroid
 * for offline use.
 */
fun applyHybridTiles(context: Context, mapView: MapView) {
    // Remove any legacy hybrid label overlays from previous versions.
    mapView.overlays.removeAll { it is HybridLabelOverlay }
    // Base: clean OSM street/terrain tiles with built-in labels.
    mapView.setTileSource(CartoDbVoyager)
}

/**
 * Reusable, polished RockScout map. Looks like Google Maps, supports pinch/pan
 * and zoom buttons up to zoom 19, and auto-switches to satellite tiles at close
 * zoom. Tiles are cached by osmdroid automatically as the user pans/zooms.
 */
@Composable
fun RockScoutMap(
    modifier: Modifier = Modifier,
    initialCenter: Pair<Double, Double>? = null,
    initialZoom: Double = 4.0,
    showUserLocation: Boolean = false,
    onMapReady: (MapView) -> Unit = {},
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var isFullscreen by remember { mutableStateOf(false) }
    var fullscreenCenter by remember { mutableStateOf(GeoPoint(39.5, -98.0)) }
    var fullscreenZoom by remember { mutableStateOf(initialZoom) }

    LaunchedEffect(showUserLocation, mapView) {
        val mv = mapView ?: return@LaunchedEffect
        var locOverlay = mv.overlays.firstOrNull { it is MyLocationNewOverlay } as? MyLocationNewOverlay
        if (showUserLocation && locOverlay == null) {
            locOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mv)
            locOverlay.enableMyLocation()
            mv.overlays.add(locOverlay)
        } else if (!showUserLocation && locOverlay != null) {
            locOverlay.disableMyLocation()
            mv.overlays.remove(locOverlay)
        }
        mv.invalidate()
    }

    LaunchedEffect(mapView) {
        val mv = mapView ?: return@LaunchedEffect
        val center = initialCenter?.let { GeoPoint(it.first, it.second) } ?: GeoPoint(39.5, -98.0)
        mv.controller.setCenter(center)
        mv.controller.setZoom(initialZoom)
        onMapReady(mv)
        // NOTE: Automatic tile prefetching removed — it was causing the app to
        // freeze on entry (same root cause as the LocationDetailScreen freeze).
        // prefetchUserArea opens the osmdroid SQLite writer and downloads
        // hundreds of tiles across 3 tile sources × zoom 12-19 on screen load,
        // contending with the visible map's own tile loads on the shared
        // SqlTileWriter lock. The map loads tiles on demand for visible areas;
        // users can manually download offline tiles from the download button.
    }

    Box(modifier = modifier.clip(RoundedCornerShape(20.dp))) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                createRockScoutMapView(ctx).apply {
                    controller.setZoom(initialZoom)
                    controller.setCenter(initialCenter?.let { GeoPoint(it.first, it.second) } ?: GeoPoint(39.5, -98.0))

                    overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
                    overlays.add(CompassOverlay(ctx, this).apply { enableCompass() })

                    mapView = this
                }
            },
            update = { /* state changes handled via LaunchedEffects */ },
        )

        MapZoomControls(
            onZoomIn = { mapView?.controller?.zoomIn() },
            onZoomOut = { mapView?.controller?.zoomOut() },
            onRecenter = {
                mapView?.let { mv ->
                    val center = initialCenter?.let { GeoPoint(it.first, it.second) } ?: GeoPoint(39.5, -98.0)
                    mv.controller.animateTo(center)
                }
            },
            showUser = showUserLocation,
            onSatellite = { toggleSatelliteView(mapView) },
            compact = true,
            modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
        )

        MapOfflineNotice(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
        )

        MapExpandButton(
            onClick = {
                mapView?.let {
                    fullscreenCenter = GeoPoint(it.mapCenter.latitude, it.mapCenter.longitude)
                    fullscreenZoom = it.zoomLevelDouble
                }
                isFullscreen = true
            },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
        )
    }

    if (isFullscreen) {
        FullscreenMapOverlay(
            onDismiss = { isFullscreen = false },
            initialCenter = fullscreenCenter,
            initialZoom = fullscreenZoom,
            showUserLocation = showUserLocation,
        )
    }

    MapViewLifecycleEffect(mapView)
}

/**
 * Trip route map: shows the driving/collecting route between stops with numbered
 * pings. Tapping a stop opens its location detail. The bounding box is zoomed to
 * fit all stops. Tiles for the route corridor are pre-cached for offline use.
 */
@Composable
fun TripRouteMap(
    trip: Trip,
    onStopTap: (TripStop) -> Unit = {},
    readOnly: Boolean = false,
    modifier: Modifier = Modifier,
    onAddStop: ((name: String, lat: Double, lng: Double, locationId: String?) -> Unit)? = null,
    onSubmitLocation: ((lat: Double, lng: Double) -> Unit)? = null,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var isFullscreen by remember { mutableStateOf(false) }
    var fullscreenCenter by remember { mutableStateOf(GeoPoint(39.5, -98.0)) }
    var fullscreenZoom by remember { mutableStateOf(4.0) }
    // Live cache timestamp for this trip's tiles. Re-read on every recomposition
    // of the trip card / detail sheet so a refresh from the Cache Trip Area
    // button is reflected immediately.
    var tripCacheTimestamp by remember(trip.id) {
        mutableStateOf(PersistenceManager.loadCachedTripTimestamps()[trip.id])
    }

    val stopsWithLocations = remember(trip.stops) {
        trip.stops.mapNotNull { stop ->
            if (stop.isCustomPin && stop.latitude != null && stop.longitude != null) {
                stop to DigLocation(
                    id = stop.locationId,
                    name = stop.locationName,
                    type = LocationType.PUBLIC_DIG,
                    region = "",
                    latitude = stop.latitude,
                    longitude = stop.longitude,
                    summary = "",
                    knownFor = emptyList(),
                    mineralTags = emptyList(),
                    feeInfo = "",
                    hours = "",
                    website = null,
                    phone = null,
                    difficulty = "",
                    publicAccess = true,
                    tips = "",
                )
            } else {
                SeedData.locationById(stop.locationId)?.let { stop to it }
            }
        }
    }

    // Add markers + polyline once the map is ready.
    LaunchedEffect(mapView, stopsWithLocations) {
        val mv = mapView ?: return@LaunchedEffect
        if (stopsWithLocations.isEmpty()) return@LaunchedEffect

        // Build overlays off the main thread, but mutate mv.overlays on the
        // main thread. osmdroid's overlay list is a plain (non-synchronized)
        // ArrayList, so mutating it from a background coroutine while the
        // MapView is being detached (when the edit dialog is dismissed) races
        // and throws ConcurrentModificationException / NPE, crashing the app.
        // See FullscreenRouteMapOverlay below for the same correct pattern.
        scope.launch(Dispatchers.IO) {
            val routePoints = stopsWithLocations.map { (_, loc) -> GeoPoint(loc.latitude, loc.longitude) }
            val density = context.resources.displayMetrics.density
            // Outline shadow polyline (dark, thick, behind)
            val outlinePolyline = Polyline().apply {
                id = "trip_route_outline"
                setPoints(routePoints)
                outlinePaint.strokeWidth = 10f * density
                outlinePaint.color = android.graphics.Color.argb(120, 0, 0, 0)
                outlinePaint.isAntiAlias = true
                outlinePaint.style = Paint.Style.STROKE
                outlinePaint.strokeCap = Paint.Cap.ROUND
            }
            // Main dashed citrine polyline
            val polyline = Polyline().apply {
                id = "trip_route"
                setPoints(routePoints)
                styleRoutePolyline(this, density)
            }
            // Directional arrow markers at midpoint of each segment
            val arrows = (1 until routePoints.size).map { i ->
                createDirectionalArrow(mv, routePoints[i - 1], routePoints[i])
            }
            val markers = stopsWithLocations.mapIndexed { index, (stop, loc) ->
                TripStopMarker(mv, index + 1, loc, stop, stop.isCustomPin) {
                    onStopTap(stop)
                }.apply { id = "trip_stop_${stop.locationId}" }
            }

            withContext(Dispatchers.Main) {
                // Clear old route markers/polylines and add new ones on the main
                // thread to avoid concurrent modification of mv.overlays.
                mv.overlays.removeAll { it is Marker && it.id?.startsWith("trip_stop_") == true }
                mv.overlays.removeAll { it is Polyline && (it.id == "trip_route" || it.id == "trip_route_outline") }
                mv.overlays.add(0, outlinePolyline)
                mv.overlays.add(1, polyline)
                arrows.forEach { mv.overlays.add(it) }
                markers.forEach { mv.overlays.add(it) }
                val box = BoundingBox.fromGeoPoints(routePoints)
                mv.zoomToBoundingBox(box, false, 48)
                mv.invalidate()
            }

            // NOTE: Automatic tile prefetching removed — it was causing the app to
            // force-close. The prefetch queued thousands of tiles across 3 sources
            // every time a trip detail opened, causing SQLite DB contention and OOM.
            // The map loads tiles on demand (lazy loading) for normal viewing.
            // Users can manually cache tiles via the "Cache Trip Area" button.
        }
    }

    Box(modifier = modifier.clip(RoundedCornerShape(20.dp))) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                createRockScoutMapView(ctx, readOnly = readOnly).apply {
                    if (!readOnly) {
                        overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
                    }
                    mapView = this
                }
            },
            update = { /* no-op */ },
        )

        MapZoomControls(
            onZoomIn = { mapView?.let { it.controller.zoomIn() } },
            onZoomOut = { mapView?.let { it.controller.zoomOut() } },
            onRecenter = {
                val mv = mapView ?: return@MapZoomControls
                val points = stopsWithLocations.map { (_, loc) -> GeoPoint(loc.latitude, loc.longitude) }
                if (points.isNotEmpty()) {
                    val box = BoundingBox.fromGeoPoints(points)
                    mv.controller.animateTo(GeoPoint(box.centerLatitude, box.centerLongitude))
                }
            },
            showUser = false,
            onSatellite = { toggleSatelliteView(mapView) },
            compact = true,
            modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
        )

        MapOfflineNotice(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
        )

        MapExpandButton(
            onClick = {
                mapView?.let {
                    fullscreenCenter = GeoPoint(it.mapCenter.latitude, it.mapCenter.longitude)
                    fullscreenZoom = it.zoomLevelDouble
                }
                isFullscreen = true
            },
            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
        )
    }

    if (isFullscreen) {
        FullscreenRouteMapOverlay(
            onDismiss = { isFullscreen = false },
            initialCenter = fullscreenCenter,
            initialZoom = fullscreenZoom,
            trip = trip,
            onStopTap = onStopTap,
            onAddStop = onAddStop,
            onSubmitLocation = onSubmitLocation,
            cacheTimestamp = tripCacheTimestamp,
            onCacheUpdated = { tripCacheTimestamp = it },
        )
    }

    MapViewLifecycleEffect(mapView)
}

/**
 * Specialized fullscreen map overlay for the trip route map.
 *
 * Includes:
 * - The existing route markers and polyline so the user sees their current route.
 * - Tap-to-drop-pin overlay.
 * - Two pill buttons at the bottom: "Add to Route" (citrine) and "Upload New Location" (aqua).
 * - Location scan logic using [DigSiteSearchService.verifyLocation] when Add to Route
 *   is tapped on an unlisted location.
 * - Auto-approve to [CustomDigLocationStore] if the scan finds rock-related results.
 * - An OutlinedTextField popup for naming the route stop if the scan returns no rock results.
 * - Full zoom controls, satellite toggle, rotation, compass, and offline notice.
 */
@Composable
fun FullscreenRouteMapOverlay(
    onDismiss: () -> Unit,
    initialCenter: GeoPoint,
    initialZoom: Double,
    trip: Trip,
    onStopTap: (TripStop) -> Unit = {},
    onAddStop: ((name: String, lat: Double, lng: Double, locationId: String?) -> Unit)? = null,
    onSubmitLocation: ((lat: Double, lng: Double) -> Unit)? = null,
    cacheTimestamp: Long? = null,
    onCacheUpdated: ((Long?) -> Unit)? = null,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var fsMapView by remember { mutableStateOf<MapView?>(null) }
    var pinLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var showNameInput by remember { mutableStateOf(false) }
    var customStopName by remember { mutableStateOf("") }
    var scanResultMessage by remember { mutableStateOf<String?>(null) }
    var tripCacheTimestamp by remember(trip.id) { mutableStateOf(cacheTimestamp) }

    val stopsWithLocations = remember(trip.stops) {
        trip.stops.mapNotNull { stop ->
            if (stop.isCustomPin && stop.latitude != null && stop.longitude != null) {
                stop to DigLocation(
                    id = stop.locationId,
                    name = stop.locationName,
                    type = LocationType.PUBLIC_DIG,
                    region = "",
                    latitude = stop.latitude,
                    longitude = stop.longitude,
                    summary = "",
                    knownFor = emptyList(),
                    mineralTags = emptyList(),
                    feeInfo = "",
                    hours = "",
                    website = null,
                    phone = null,
                    difficulty = "",
                    publicAccess = true,
                    tips = "",
                )
            } else {
                SeedData.locationById(stop.locationId)?.let { stop to it }
            }
        }
    }

    // Build route markers and polyline once the fullscreen map is ready
    LaunchedEffect(fsMapView, stopsWithLocations) {
        val mv = fsMapView ?: return@LaunchedEffect
        if (stopsWithLocations.isEmpty()) return@LaunchedEffect
        withContext(Dispatchers.IO) {
            val routePoints = stopsWithLocations.map { (_, loc) -> GeoPoint(loc.latitude, loc.longitude) }
            val density = context.resources.displayMetrics.density
            // Outline shadow polyline
            val outlinePolyline = Polyline().apply {
                id = "trip_route_fs_outline"
                setPoints(routePoints)
                outlinePaint.strokeWidth = 10f * density
                outlinePaint.color = android.graphics.Color.argb(120, 0, 0, 0)
                outlinePaint.isAntiAlias = true
                outlinePaint.style = Paint.Style.STROKE
                outlinePaint.strokeCap = Paint.Cap.ROUND
            }
            // Main dashed citrine polyline
            val polyline = Polyline().apply {
                id = "trip_route_fs"
                setPoints(routePoints)
                styleRoutePolyline(this, density)
            }
            val arrows = (1 until routePoints.size).map { i ->
                createDirectionalArrow(mv, routePoints[i - 1], routePoints[i])
            }
            val markers = stopsWithLocations.mapIndexed { index, (stop, loc) ->
                TripStopMarker(mv, index + 1, loc, stop, stop.isCustomPin) {
                    onStopTap(stop)
                }.apply {
                    id = "trip_stop_fs_${stop.locationId}"
                }
            }
            withContext(Dispatchers.Main) {
                mv.overlays.add(0, outlinePolyline)
                mv.overlays.add(1, polyline)
                arrows.forEach { mv.overlays.add(it) }
                markers.forEach { mv.overlays.add(it) }
                mv.invalidate()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    createRockScoutMapView(ctx).apply {
                        controller.setCenter(initialCenter)
                        controller.setZoom(initialZoom)
                        overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
                        overlays.add(CompassOverlay(ctx, this).apply { enableCompass() })

                        // Tap-to-drop-pin overlay
                        overlays.add(object : Overlay() {
                            override fun onSingleTapConfirmed(e: MotionEvent?, view: MapView?): Boolean {
                                if (e == null || view == null) return false
                                val proj = view.projection
                                val point = proj.fromPixels(e.x.toInt(), e.y.toInt())
                                pinLocation = Pair(point.latitude, point.longitude)
                                showNameInput = false
                                scanResultMessage = null
                                view.overlays.removeAll { it is Marker && it.id == "route_pin_preview" }
                                val marker = Marker(view).apply {
                                    id = "route_pin_preview"
                                    position = GeoPoint(point.latitude, point.longitude)
                                    title = "New stop"
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                }
                                view.overlays.add(marker)
                                view.invalidate()
                                return true
                            }
                        })
                        fsMapView = this
                    }
                },
                update = { /* no-op */ },
            )

            // Close button
            SculptedIconButton(
                icon = Icons.Filled.Close,
                contentDescription = "Close full screen map",
                onClick = onDismiss,
                accent = Citrine,
                iconTint = Color.White,
                backgroundColor = Slate800,
                size = 44.dp,
                shadowElevation = 5.dp,
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
            )

            // Zoom controls
            MapZoomControls(
                onZoomIn = { fsMapView?.controller?.zoomIn() },
                onZoomOut = { fsMapView?.controller?.zoomOut() },
                onRecenter = {
                    fsMapView?.let { mv ->
                        val pts = stopsWithLocations.map { (_, loc) -> GeoPoint(loc.latitude, loc.longitude) }
                        if (pts.isNotEmpty()) {
                            val box = BoundingBox.fromGeoPoints(pts)
                            mv.controller.animateTo(GeoPoint(box.centerLatitude, box.centerLongitude))
                        } else {
                            mv.controller.animateTo(initialCenter)
                        }
                    }
                },
                showUser = false,
                onSatellite = { toggleSatelliteView(fsMapView) },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            )

            MapOfflineNotice(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 64.dp),
            )

            MapCacheStatusIndicator(
                cachedAtMillis = tripCacheTimestamp,
                onRefresh = {
                    val mv = fsMapView ?: return@MapCacheStatusIndicator
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            runCatching {
                                MapTileCacheManager.prefetchTripArea(
                                    context = context,
                                    trip = trip,
                                    radiusMiles = 3.0,
                                )
                            }
                        }
                        val updated = PersistenceManager.loadCachedTripTimestamps()[trip.id]
                        tripCacheTimestamp = updated
                        onCacheUpdated?.invoke(updated)
                        android.widget.Toast.makeText(
                            context,
                            "Offline tiles refreshed.",
                            android.widget.Toast.LENGTH_SHORT,
                        ).show()
                    }
                },
                label = "Trip tiles",
                modifier = Modifier.align(Alignment.TopStart).padding(top = 72.dp, start = 16.dp),
            )

            // Pin-drop hint
            if (pinLocation == null && !isScanning) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 110.dp, start = 16.dp, end = 16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xE6000000))
                        .glowingBorder(2.dp, Warning.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Warning, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Tap the map to drop a pin, then use the buttons below.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                    )
                }
            }

            // Scanning indicator
            if (isScanning) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 110.dp, start = 16.dp, end = 16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xE6000000))
                        .glowingBorder(2.dp, Aqua.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Aqua)
                    Spacer(Modifier.width(8.dp))
                    Text("Scanning for rock locations…", style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
            }

            // Scan result message
            scanResultMessage?.let { msg ->
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 110.dp, start = 16.dp, end = 16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xE6000000))
                        .glowingBorder(2.dp, Citrine.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(msg, style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
            }

            // Bottom pill buttons — Add to Route + Upload New Location
            if (pinLocation != null && !isScanning && !showNameInput) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 72.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Add to Route
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .sculpted(
                                shape = RoundedCornerShape(24.dp),
                                accent = Citrine,
                                shadowElevation = 6.dp,
                                onClick = {
                                    val (lat, lng) = pinLocation ?: return@sculpted
                                    if (onAddStop != null) {
                                        isScanning = true
                                        scope.launch {
                                            // Check if the location is already known
                                            val knownLoc = SeedData.allLocations.firstOrNull {
                                                AppRepository.distanceMiles(it.latitude, it.longitude, lat, lng) < 0.5
                                            }
                                            if (knownLoc != null) {
                                                // Already a known location — add directly
                                                onAddStop(knownLoc.name, lat, lng, knownLoc.id)
                                                isScanning = false
                                                scanResultMessage = "Added: ${knownLoc.name}"
                                                pinLocation = null
                                                fsMapView?.overlays?.removeAll { it is Marker && it.id == "route_pin_preview" }
                                                fsMapView?.invalidate()
                                            } else {
                                                // Run location scan
                                                val tempName = "%.4f, %.4f".format(lat, lng)
                                                val verified = DigSiteSearchService.verifyLocation(tempName, lat, lng) { snippet, url ->
                                                    // Auto-approve: we could add to CustomDigLocationStore here
                                                    // but the verifyLocation callback gives us the snippet/url
                                                }
                                                isScanning = false
                                                if (verified) {
                                                    // Rock-related location found — add as a known stop
                                                    onAddStop("Rock Location", lat, lng, null)
                                                    scanResultMessage = "Rock location verified and added to route!"
                                                    pinLocation = null
                                                    fsMapView?.overlays?.removeAll { it is Marker && it.id == "route_pin_preview" }
                                                    fsMapView?.invalidate()
                                                } else {
                                                    // Unknown location — show name input
                                                    showNameInput = true
                                                }
                                            }
                                        }
                                    }
                                },
                            )
                            .clip(RoundedCornerShape(24.dp))
                            .background(Slate800)
                            .glowingBorder(2.dp, Citrine, RoundedCornerShape(24.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = Citrine, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Add to Route", style = MaterialTheme.typography.labelLarge, color = Citrine, fontWeight = FontWeight.Bold)
                    }

                    // Upload New Location
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .sculpted(
                                shape = RoundedCornerShape(24.dp),
                                accent = Aqua,
                                shadowElevation = 6.dp,
                                onClick = {
                                    val (lat, lng) = pinLocation ?: return@sculpted
                                    onSubmitLocation?.invoke(lat, lng)
                                    onDismiss()
                                },
                            )
                            .clip(RoundedCornerShape(24.dp))
                            .background(Slate800)
                            .glowingBorder(2.dp, Aqua, RoundedCornerShape(24.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.AddLocationAlt, contentDescription = null, tint = Aqua, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Upload New Location", style = MaterialTheme.typography.labelLarge, color = Aqua, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
            }

            // Name input popup for unknown locations
            if (showNameInput && pinLocation != null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 72.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Slate800)
                        .glowingBorder(2.dp, Aqua, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                ) {
                    Text(
                        "No rock location found here.",
                        style = MaterialTheme.typography.titleSmall,
                        color = Aqua,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Name this route stop (e.g. Gas station, Creek crossing):",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customStopName,
                        onValueChange = { customStopName = ProfanityFilter.filter(it) },
                        label = { Text("Stop name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done,
                        ),
                        modifier = Modifier.fillMaxWidth().noAutoFocus(),
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SculptedTextButton(
                            text = "Cancel",
                            onClick = {
                                showNameInput = false
                                customStopName = ""
                                pinLocation = null
                                fsMapView?.overlays?.removeAll { it is Marker && it.id == "route_pin_preview" }
                                fsMapView?.invalidate()
                            },
                            accent = DarkTextMid,
                            textColor = DarkTextMid,
                            modifier = Modifier.weight(1f),
                        )
                        SculptedButton(
                            text = "Add Stop",
                            onClick = {
                                val (lat, lng) = pinLocation ?: return@SculptedButton
                                val name = customStopName.ifBlank { "Custom Stop" }
                                onAddStop?.invoke(name, lat, lng, null)
                                showNameInput = false
                                customStopName = ""
                                scanResultMessage = "Added: $name"
                                pinLocation = null
                                fsMapView?.overlays?.removeAll { it is Marker && it.id == "route_pin_preview" }
                                fsMapView?.invalidate()
                            },
                            accent = Aqua,
                            containerColor = Aqua,
                            textColor = Color.Black,
                            icon = Icons.Filled.Check,
                            modifier = Modifier.weight(1f),
                            enabled = customStopName.isNotBlank(),
                        )
                    }
                }
            }
        }
        MapViewLifecycleEffect(fsMapView)
    }
}

/** Marker showing a numbered trip-stop ping. */
private class TripStopMarker(
    mapView: MapView,
    number: Int,
    loc: DigLocation,
    private val stop: TripStop,
    private val isCustomPin: Boolean,
    private val onTap: () -> Unit,
) : Marker(mapView) {
    init {
        position = GeoPoint(loc.latitude, loc.longitude)
        title = if (isCustomPin) "$number. \uD83D\uDDCC ${loc.name}" else "$number. ${loc.name}"
        snippet = if (isCustomPin) "Custom stop" else loc.region
        setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM)
        icon = createNumberedPinIcon(mapView.context, number, if (isCustomPin) Aqua else Citrine)
        setOnMarkerClickListener { _, _ ->
            onTap()
            true
        }
    }
}

/** Zoom / recenter controls used by all RockScout maps. */
@Composable
fun MapZoomControls(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onRecenter: () -> Unit,
    showUser: Boolean,
    modifier: Modifier = Modifier,
    onSatellite: () -> Unit = {},
    compact: Boolean = false,
    parkingState: ParkingButtonState = ParkingButtonState.Hidden,
    onParkHere: () -> Unit = {},
    onNavigateToVehicle: () -> Unit = {},
    onClearParking: () -> Unit = {},
    showLayerToggle: Boolean = false,
    currentLayer: MapLayerStyle = MapLayerStyle.STREET,
    onLayerToggle: () -> Unit = {},
) {
    val buttonSize = if (compact) 32.dp else 40.dp
    val spacerHeight = if (compact) 0.5.dp else 1.dp
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Slate800.copy(alpha = 0.92f))
            .navigationBarsPadding(),
    ) {
        SculptedIconButton(
            icon = Icons.Filled.Add,
            contentDescription = "Zoom in",
            onClick = onZoomIn,
            accent = Citrine,
            iconTint = Color.White,
            size = buttonSize,
            shadowElevation = if (compact) 2.dp else 3.dp,
        )
        Spacer(Modifier.height(spacerHeight).width(buttonSize).background(Color.White.copy(alpha = 0.15f)))
        SculptedIconButton(
            icon = Icons.Filled.Remove,
            contentDescription = "Zoom out",
            onClick = onZoomOut,
            accent = Citrine,
            iconTint = Color.White,
            size = buttonSize,
            shadowElevation = if (compact) 2.dp else 3.dp,
        )
        Spacer(Modifier.height(spacerHeight).width(buttonSize).background(Color.White.copy(alpha = 0.15f)))
        SculptedIconButton(
            icon = if (showUser) Icons.Filled.MyLocation else Icons.Filled.LocationDisabled,
            contentDescription = "Recenter",
            onClick = onRecenter,
            accent = Citrine,
            iconTint = if (showUser) Citrine else Color.White,
            size = buttonSize,
            shadowElevation = if (compact) 2.dp else 3.dp,
        )
        Spacer(Modifier.height(spacerHeight).width(buttonSize).background(Color.White.copy(alpha = 0.15f)))
        SculptedIconButton(
            icon = Icons.Filled.SatelliteAlt,
            contentDescription = "Jump to satellite view",
            onClick = onSatellite,
            accent = Citrine,
            iconTint = Aqua,
            size = buttonSize,
            shadowElevation = if (compact) 2.dp else 3.dp,
        )
        if (showLayerToggle) {
            Spacer(Modifier.height(spacerHeight).width(buttonSize).background(Color.White.copy(alpha = 0.15f)))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                SculptedIconButton(
                    icon = Icons.Filled.Layers,
                    contentDescription = "Toggle map layer: ${currentLayer.label}",
                    onClick = onLayerToggle,
                    accent = Citrine,
                    iconTint = if (currentLayer == MapLayerStyle.TOPO) Aqua else Color.White,
                    size = buttonSize,
                    shadowElevation = if (compact) 2.dp else 3.dp,
                )
                Text(
                    currentLayer.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 8.sp,
                    maxLines = 1,
                )
            }
        }
        if (parkingState != ParkingButtonState.Hidden) {
            Spacer(Modifier.height(spacerHeight).width(buttonSize).background(Color.White.copy(alpha = 0.15f)))
            val parkingIcon = when (parkingState) {
                ParkingButtonState.NoSpot -> Icons.Filled.LocalParking
                ParkingButtonState.HasSpot -> Icons.Filled.DirectionsCar
                else -> Icons.Filled.LocalParking
            }
            val parkingTint = when (parkingState) {
                ParkingButtonState.HasSpot -> Citrine
                else -> Color.White
            }
            val parkingDesc = when (parkingState) {
                ParkingButtonState.HasSpot -> "Tap to clear saved parking spot"
                else -> "Park here — save your parking spot"
            }
            SculptedIconButton(
                icon = parkingIcon,
                contentDescription = parkingDesc,
                onClick = {
                    if (parkingState == ParkingButtonState.HasSpot) onClearParking() else onParkHere()
                },
                accent = Citrine,
                iconTint = parkingTint,
                size = buttonSize,
                shadowElevation = if (compact) 2.dp else 3.dp,
            )
        }
    }
}

/** State of the Park Here button in [MapZoomControls]. */
enum class ParkingButtonState {
    Hidden,
    NoSpot,
    HasSpot,
}

/**
 * Standalone satellite jump button for maps that don't use [MapZoomControls].
 * Tapping cycles through the 3-state satellite toggle: zoom 16, zoom 19,
 * back to original view.
 */
@Composable
fun SatelliteJumpButton(
    mapView: MapView?,
    modifier: Modifier = Modifier,
) {
    SculptedIconButton(
        icon = Icons.Filled.SatelliteAlt,
        contentDescription = "Toggle satellite view",
        onClick = { toggleSatelliteView(mapView) },
        accent = Citrine,
        iconTint = Aqua,
        backgroundColor = Slate800,
        size = 44.dp,
        shadowElevation = 5.dp,
        modifier = modifier,
    )
}

/**
 * Expand-to-fullscreen button shown on embedded maps. Tapping it opens the
 * map in a full-screen overlay for easier scrolling and zooming.
 */
@Composable
fun MapExpandButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SculptedIconButton(
        icon = Icons.Filled.Fullscreen,
        contentDescription = "Expand map to full screen",
        onClick = onClick,
        accent = Citrine,
        iconTint = Aqua,
        backgroundColor = Slate800,
        size = 44.dp,
        shadowElevation = 5.dp,
        modifier = modifier,
    )
}

/**
 * Full-screen map overlay shown when the user taps the expand button on an
 * embedded map. Creates a fresh MapView at the same center/zoom as the
 * original, with zoom controls, satellite toggle, and a close button in the
 * top-left corner. Callers can use [onMapReady] to add custom overlays
 * (markers, polylines, etc.) to the fullscreen map.
 */
@Composable
fun FullscreenMapOverlay(
    onDismiss: () -> Unit,
    initialCenter: GeoPoint,
    initialZoom: Double,
    showUserLocation: Boolean = false,
    onMapReady: suspend (MapView) -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var fsMapView by remember { mutableStateOf<MapView?>(null) }
    var pinLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var downloadCoords by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var showDownloadSheet by remember { mutableStateOf(false) }
    // Live cache timestamp for the area around the fullscreen map's center.
    // Re-read after a refresh so the indicator flips to "fresh" immediately.
    var areaCacheTimestamp by remember(initialCenter) {
        mutableStateOf(PersistenceManager.areaCacheTime(initialCenter.latitude, initialCenter.longitude))
    }

    // Run the caller's map setup (creating bitmaps, markers, polylines, and
    // calling zoomToBoundingBox) after the view has been laid out and on a
    // background dispatcher. Doing this synchronously inside the AndroidView
    // factory blocked the main thread and caused ANRs when the expand button
    // was tapped.
    LaunchedEffect(fsMapView) {
        val mv = fsMapView ?: return@LaunchedEffect
        val layoutReady = CompletableDeferred<Unit>()
        mv.post { layoutReady.complete(Unit) }
        layoutReady.await()
        withContext(Dispatchers.IO) {
            runCatching { onMapReady(mv) }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    createRockScoutMapView(ctx).apply {
                        controller.setCenter(initialCenter)
                        controller.setZoom(initialZoom)
                        overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
                        overlays.add(CompassOverlay(ctx, this).apply { enableCompass() })
                        if (showUserLocation) {
                            val locOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                            locOverlay.enableMyLocation()
                            overlays.add(locOverlay)
                        }
                        // Tap-to-drop-pin overlay — tapping the map drops a pin
                        // that the user can then download offline maps for.
                        overlays.add(object : Overlay() {
                            override fun onSingleTapConfirmed(e: MotionEvent?, view: MapView?): Boolean {
                                if (e == null || view == null) return false
                                val proj = view.projection
                                val point = proj.fromPixels(e.x.toInt(), e.y.toInt())
                                pinLocation = Pair(point.latitude, point.longitude)
                                view.overlays.removeAll { it is Marker && it.id == "fs_pin_preview" }
                                val marker = Marker(view).apply {
                                    id = "fs_pin_preview"
                                    position = GeoPoint(point.latitude, point.longitude)
                                    title = "Pin location"
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                }
                                view.overlays.add(marker)
                                view.invalidate()
                                return true
                            }
                        })
                        fsMapView = this
                    }
                },
            )

            // Close button — top-left corner, only closes the fullscreen overlay
            SculptedIconButton(
                icon = Icons.Filled.Close,
                contentDescription = "Close full screen map",
                onClick = onDismiss,
                accent = Citrine,
                iconTint = Color.White,
                backgroundColor = Slate800,
                size = 44.dp,
                shadowElevation = 5.dp,
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
            )

            // Zoom controls — bottom-right
            MapZoomControls(
                onZoomIn = { fsMapView?.controller?.zoomIn() },
                onZoomOut = { fsMapView?.controller?.zoomOut() },
                onRecenter = { fsMapView?.controller?.animateTo(initialCenter) },
                showUser = showUserLocation,
                onSatellite = { toggleSatelliteView(fsMapView) },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            )

            MapOfflineNotice(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 64.dp),
            )

            MapCacheStatusIndicator(
                cachedAtMillis = areaCacheTimestamp,
                onRefresh = {
                    val mv = fsMapView ?: return@MapCacheStatusIndicator
                    val center = mv.mapCenter
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            runCatching {
                                MapTileCacheManager.prefetchUserArea(
                                    context = context,
                                    lat = center.latitude,
                                    lng = center.longitude,
                                )
                            }
                        }
                        areaCacheTimestamp = PersistenceManager.areaCacheTime(center.latitude, center.longitude)
                        android.widget.Toast.makeText(
                            context,
                            "Offline tiles refreshed.",
                            android.widget.Toast.LENGTH_SHORT,
                        ).show()
                    }
                },
                label = "Area tiles",
                modifier = Modifier.align(Alignment.TopStart).padding(top = 72.dp, start = 16.dp),
            )

            // Pin-drop hint banner — shows until the user drops a pin
            if (pinLocation == null) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 110.dp, start = 16.dp, end = 16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xE6000000))
                        .glowingBorder(2.dp, Warning.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = Warning,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Tap the map to drop a pin, then download offline maps for that area.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                    )
                }
            }

            // Floating download button — bottom-left corner (zoom controls are bottom-right)
            SculptedIconButton(
                icon = Icons.Filled.Download,
                contentDescription = "Download offline maps",
                onClick = {
                    val coords = pinLocation ?: fsMapView?.let {
                        Pair(it.mapCenter.latitude, it.mapCenter.longitude)
                    } ?: Pair(initialCenter.latitude, initialCenter.longitude)
                    downloadCoords = coords
                    showDownloadSheet = true
                },
                accent = Citrine,
                iconTint = Aqua,
                backgroundColor = Slate800,
                size = 44.dp,
                shadowElevation = 5.dp,
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
            )
        }

        if (showDownloadSheet && downloadCoords != null) {
            MapDownloadSheet(
                lat = downloadCoords!!.first,
                lng = downloadCoords!!.second,
                onDismiss = { showDownloadSheet = false },
            )
        }

        MapViewLifecycleEffect(fsMapView)
    }
}

/**
 * Reusable download sheet — takes a lat/lng and offers two full-width buttons:
 * (1) Download offline map tiles at pin location (caches satellite/street/label
 * tiles at zoom 10-19 for a 3-mile radius).
 * (2) Save ultra high-res map image at pin location (stitches a zoom-15 satellite
 * image, 3-mile radius, saves to gallery "Offline Maps" folder).
 *
 * "Download ahead of time" helper text at the top reminds users to cache areas
 * before they lose signal en route to a destination.
 */
@Composable
fun MapDownloadSheet(
    lat: Double,
    lng: Double,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isDownloadingTiles by remember { mutableStateOf(false) }
    var isGeneratingImage by remember { mutableStateOf(false) }
    var tilesDone by remember { mutableStateOf(false) }
    var imageDone by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .clip(RoundedCornerShape(20.dp))
                .background(Slate800)
                .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(20.dp))
                .padding(20.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Download,
                    contentDescription = null,
                    tint = Citrine,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "Download Offline Maps",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .sculpted(
                            shape = CircleShape,
                            accent = Citrine,
                            shadowElevation = 3.dp,
                            circular = true,
                            onClick = onDismiss,
                        )
                        .clip(CircleShape)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Spacer(Modifier.height(6.dp))
            Text(
                "Lat: %.4f  \u00b7  Lng: %.4f".format(lat, lng),
                style = MaterialTheme.typography.labelSmall,
                color = DarkTextMid,
            )

            Spacer(Modifier.height(14.dp))

            // "Download ahead of time" tip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Warning.copy(alpha = 0.10f))
                    .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Icon(
                    Icons.Filled.Lightbulb,
                    contentDescription = null,
                    tint = Warning,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Download ahead of time \u2014 before you lose signal on your way to this area. Drop a pin on any map to cache that spot for offline use.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                )
            }

            Spacer(Modifier.height(16.dp))

            // Button 1: Download offline map tiles at pin location
            MapDownloadButton(
                title = "Download offline map tiles",
                subtitle = "at pin location",
                helperText = "Caches satellite, street, and label tiles at zoom 10\u201319 for a 3-mile radius. The map works with zero signal.",
                isLoading = isDownloadingTiles,
                isDone = tilesDone,
                accent = Aqua,
                icon = Icons.Filled.Download,
                onClick = {
                    if (isDownloadingTiles) return@MapDownloadButton
                    isDownloadingTiles = true
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            runCatching {
                                MapTileCacheManager.prefetchPoint(context, lat, lng)
                            }
                        }
                        isDownloadingTiles = false
                        tilesDone = true
                    }
                },
            )

            Spacer(Modifier.height(14.dp))

            // Button 2: Save ultra high-res map image at pin location
            MapDownloadButton(
                title = "Save ultra high-res map image",
                subtitle = "at pin location",
                helperText = "Stitches a zoom-15 satellite image (3-mile radius) and saves it to your gallery's Offline Maps folder. Pinch to zoom in deep \u2014 trails and terrain stay crisp with no signal.",
                isLoading = isGeneratingImage,
                isDone = imageDone,
                accent = Citrine,
                icon = Icons.Filled.PhotoCamera,
                onClick = {
                    if (isGeneratingImage) return@MapDownloadButton
                    isGeneratingImage = true
                    scope.launch {
                        OfflineMapExporter.saveOfflineMapImage(context, lat, lng)
                        isGeneratingImage = false
                        imageDone = true
                    }
                },
            )
        }
    }
}

@Composable
private fun MapDownloadButton(
    title: String,
    subtitle: String,
    helperText: String,
    isLoading: Boolean,
    isDone: Boolean,
    accent: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .sculpted(
                    shape = RoundedCornerShape(14.dp),
                    accent = accent,
                    shadowElevation = 5.dp,
                    onClick = if (isLoading) ({}) else onClick,
                )
                .clip(RoundedCornerShape(14.dp))
                .background(Slate800)
                .glowingBorder(2.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.18f))
                        .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = accent,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(
                            if (isDone) Icons.Filled.Check else icon,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        if (isLoading) "$title\u2026" else if (isDone) "$title \u2713" else title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = accent,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            helperText,
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextMid,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
    }
}

/** Bitmap marker with a bold number inside a colored teardrop. */
private fun createNumberedPinIcon(context: Context, number: Int, color: Color): Drawable {
    val density = context.resources.displayMetrics.density
    val sizePx = (32 * density).toInt().coerceAtLeast(32)
    val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val cx = sizePx / 2f
    val cy = sizePx / 2f
    val r = sizePx / 3f

    // Teardrop.
    paint.color = color.toArgb()
    val path = Path().apply {
        moveTo(cx, sizePx.toFloat())
        lineTo(cx - r * 0.9f, cy + r * 0.4f)
        lineTo(cx + r * 0.9f, cy + r * 0.4f)
        close()
    }
    canvas.drawPath(path, paint)
    canvas.drawCircle(cx, cy, r, paint)

    // White ring.
    paint.color = android.graphics.Color.WHITE
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 2.5f * density
    canvas.drawCircle(cx, cy, r, paint)

    // Number.
    paint.style = Paint.Style.FILL
    paint.textSize = r * 1.2f
    paint.textAlign = Paint.Align.CENTER
    val fm = paint.fontMetrics
    val textY = cy - (fm.ascent + fm.descent) / 2
    canvas.drawText(number.toString(), cx, textY, paint)

    return BitmapDrawable(context.resources, bmp)
}

/**
 * Read-only map that displays a set of [SpecimenMarker] pins. Used by the field
 * journal detail sheet to show where the user found rocks during a trip/day.
 * Centers/zooms to fit all markers, with a fallback to the first marker if only
 * one exists. Tapping a marker opens an info window with its name/description.
 */
@Composable
fun SpecimenMarkerMap(
    markers: List<SpecimenMarker>,
    modifier: Modifier = Modifier,
    initialCenter: Pair<Double, Double>? = null,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var isFullscreen by remember { mutableStateOf(false) }
    var fullscreenCenter by remember { mutableStateOf(GeoPoint(39.5, -98.0)) }
    var fullscreenZoom by remember { mutableStateOf(4.0) }

    LaunchedEffect(mapView, markers) {
        val mv = mapView ?: return@LaunchedEffect
        mv.overlays.removeAll { it is Marker && it.id?.startsWith("journal_marker_") == true }
        val density = context.resources.displayMetrics.density
        markers.forEach { marker ->
            val m = Marker(mv).apply {
                id = "journal_marker_${marker.id}"
                position = GeoPoint(marker.latitude, marker.longitude)
                title = marker.name
                snippet = marker.description
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = createJournalPinIcon(mv.context, Aqua, density)
            }
            mv.overlays.add(m)
        }
        if (markers.isNotEmpty()) {
            val pts = markers.map { GeoPoint(it.latitude, it.longitude) }
            val box = BoundingBox.fromGeoPoints(pts)
            mv.zoomToBoundingBox(box, false, 48)
        }
        mv.invalidate()
    }

    Box(modifier = modifier.clip(RoundedCornerShape(16.dp))) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                createRockScoutMapView(ctx, readOnly = true).apply {
                    val center = initialCenter?.let { GeoPoint(it.first, it.second) }
                        ?: markers.firstOrNull()?.let { GeoPoint(it.latitude, it.longitude) }
                        ?: GeoPoint(39.5, -98.0)
                    controller.setCenter(center)
                    controller.setZoom(if (markers.size > 1) 10.0 else 13.0)
                    mapView = this
                }
            },
            update = { /* no-op */ },
        )

        MapZoomControls(
            onZoomIn = { mapView?.let { it.controller.zoomIn() } },
            onZoomOut = { mapView?.let { it.controller.zoomOut() } },
            onRecenter = {
                val mv = mapView ?: return@MapZoomControls
                val pts = markers.map { GeoPoint(it.latitude, it.longitude) }
                if (pts.isNotEmpty()) {
                    val box = BoundingBox.fromGeoPoints(pts)
                    mv.controller.animateTo(GeoPoint(box.centerLatitude, box.centerLongitude))
                }
            },
            showUser = false,
            onSatellite = { toggleSatelliteView(mapView) },
            compact = true,
            modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
        )

        MapOfflineNotice(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
        )

        MapExpandButton(
            onClick = {
                mapView?.let {
                    fullscreenCenter = GeoPoint(it.mapCenter.latitude, it.mapCenter.longitude)
                    fullscreenZoom = it.zoomLevelDouble
                }
                isFullscreen = true
            },
            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
        )
    }

    if (isFullscreen) {
        FullscreenMapOverlay(
            onDismiss = { isFullscreen = false },
            initialCenter = fullscreenCenter,
            initialZoom = fullscreenZoom,
        ) { mv ->
            markers.forEach { marker ->
                val m = Marker(mv).apply {
                    id = "journal_marker_fs_${marker.id}"
                    position = GeoPoint(marker.latitude, marker.longitude)
                    title = marker.name
                    snippet = marker.description
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = createJournalPinIcon(mv.context, Aqua, mv.context.resources.displayMetrics.density)
                }
                mv.overlays.add(m)
            }
            if (markers.size > 1) {
                val pts = markers.map { GeoPoint(it.latitude, it.longitude) }
                val box = BoundingBox.fromGeoPoints(pts)
                mv.zoomToBoundingBox(box, false, 48)
            }
            mv.invalidate()
        }
    }

    MapViewLifecycleEffect(mapView)
}

/** Small teardrop pin icon for journal specimen markers, matching the trip marker style. */
private fun createJournalPinIcon(context: Context, color: Color, density: Float): Drawable {
    val sizePx = (28 * density).toInt().coerceAtLeast(32)
    val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val cx = sizePx / 2f
    val cy = sizePx / 2f
    val r = sizePx / 3f
    paint.color = color.toArgb()
    val path = Path().apply {
        moveTo(cx, sizePx.toFloat())
        lineTo(cx - r * 0.9f, cy + r * 0.4f)
        lineTo(cx + r * 0.9f, cy + r * 0.4f)
        close()
    }
    canvas.drawPath(path, paint)
    canvas.drawCircle(cx, cy, r, paint)
    paint.color = android.graphics.Color.WHITE
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 2.5f * density
    canvas.drawCircle(cx, cy, r, paint)
    return BitmapDrawable(context.resources, bmp)
}

/**
 * Offline tile cache helper. Uses osmdroid's CacheManager to pre-download map
 * tiles for all three tile sources (street, satellite, labels) so the map still
 * works without a signal — including the satellite view at max zoom.
 *
 * Every prefetch downloads tiles for **all** tile sources, not just the one
 * currently displayed, so that when the user zooms in and the map auto-switches
 * to satellite those tiles are already on disk.
 */
object MapTileCacheManager {

    /** Tile sources that should be cached for offline use. */
    private val offlineTileSources: List<OnlineTileSourceBase>
        get() = listOf(CartoDbVoyager, EsriWorldImagery, CartoDbVoyagerLabels)

    /**
     * Download tiles for a trip's route corridor. Fetches street + satellite +
     * label tiles at zoom 14-18 for the route overview, and zoom 18-19 for each
     * stop so the exact dig sites are crisp in satellite view when offline.
     *
     * Handles custom pins (stops with their own lat/lng) as well as stops
     * referencing seed-data locations. Uses per-stop 3-mile-radius areas
     * instead of one giant bounding box across all stops (which could span
     * hundreds of miles and generate tens of thousands of tiles, causing OOM
     * and SQLite contention crashes).
     *
     * Also caches a 1-mile radius around every pinned specimen marker so the
     * user's find spots work offline even when they're away from a route stop.
     */
    fun prefetchTrip(context: Context, trip: Trip) {
        val points = trip.stops.mapNotNull { stop ->
            if (stop.isCustomPin && stop.latitude != null && stop.longitude != null) {
                GeoPoint(stop.latitude, stop.longitude)
            } else {
                SeedData.locationById(stop.locationId)?.let { GeoPoint(it.latitude, it.longitude) }
            }
        }
        // Specimen markers pinned by the user on the trip's marker map.
        val markerPoints = trip.specimenMarkers.map { GeoPoint(it.latitude, it.longitude) }
        if (points.isEmpty() && markerPoints.isEmpty()) return

        // Download a 3-mile radius around each stop at zoom 10-19.
        // This covers the route without creating a giant bounding box that
        // could span entire states and queue 50,000+ tile downloads.
        points.forEach { pt ->
            val box = radiusBoundingBox(pt, 3.0)
            prefetchArea(context, box, zoomMin = 10, zoomMax = 19)
        }
        // Cache a tighter 1-mile radius around each specimen marker so the
        // user's pinned find spots are available offline without prefetching
        // huge areas for markers that may be scattered across miles of terrain.
        markerPoints.forEach { pt ->
            val box = radiusBoundingBox(pt, 1.0)
            prefetchArea(context, box, zoomMin = 14, zoomMax = 19)
        }
    }

    /**
     * Download tiles for a 3-mile radius around every stop on a trip (including
     * custom pins), plus a 1-mile radius around every pinned specimen marker.
     * Fetches all three tile sources (street, satellite, labels) at zoom 10–19
     * so the user can navigate the full trip route and visit pinned find spots
     * with zero signal, including satellite zoom at max detail.
     *
     * Reports progress via [onProgress] as a percentage (0–100). Progress is
     * weighted across stops and markers so the bar reflects real work done.
     */
    fun prefetchTripArea(
        context: Context,
        trip: Trip,
        radiusMiles: Double = 3.0,
        onProgress: ((Int) -> Unit)? = null,
    ) {
        val stopPoints = trip.stops.mapNotNull { stop ->
            if (stop.isCustomPin && stop.latitude != null && stop.longitude != null) {
                GeoPoint(stop.latitude, stop.longitude)
            } else {
                SeedData.locationById(stop.locationId)?.let { GeoPoint(it.latitude, it.longitude) }
            }
        }
        val markerPoints = trip.specimenMarkers.map { GeoPoint(it.latitude, it.longitude) }
        if (stopPoints.isEmpty() && markerPoints.isEmpty()) return

        // Stops get the full radius; specimen markers get a tighter 1-mile radius.
        val total = stopPoints.size + markerPoints.size
        var done = 0
        stopPoints.forEach { pt ->
            val box = radiusBoundingBox(pt, radiusMiles)
            prefetchArea(context, box, zoomMin = 10, zoomMax = 19)
            done++
            onProgress?.invoke((done * 100) / total)
        }
        markerPoints.forEach { pt ->
            val box = radiusBoundingBox(pt, 1.0)
            prefetchArea(context, box, zoomMin = 14, zoomMax = 19)
            done++
            onProgress?.invoke((done * 100) / total)
        }
        // Record the refresh time so the map sync-status indicator can show
        // "Cached just now" and later suggest a refresh when tiles go stale.
        runCatching { PersistenceManager.recordTripCacheTime(trip.id, System.currentTimeMillis()) }
    }

    /**
     * Download tiles for the user's current area. Called on the ping map so
     * the satellite view is cached if the user loses signal while in the field.
     */
    fun prefetchUserArea(context: Context, lat: Double, lng: Double) {
        if (lat == 0.0 && lng == 0.0) return
        val pt = GeoPoint(lat, lng)
        prefetchArea(context, listOf(pt), zoomMin = 12, zoomMax = 19)
        runCatching { PersistenceManager.recordAreaCacheTime(lat, lng) }
    }

    /**
     * Deletes cached map tiles for a 3-mile radius around every stop on a trip,
     * plus the 1-mile radius around every pinned specimen marker. Opens the
     * osmdroid SQLite tile cache database directly and runs targeted DELETE
     * queries for each point's tile bounding box at each zoom level (10–19 for
     * stops, 14–19 for markers), for each of the 3 tile sources. Only tiles
     * within the radii are removed — the rest of the cache is untouched.
     * Best-effort: wrapped in try/catch, never crashes the app.
     */
    fun deleteTripAreaTiles(
        context: Context,
        trip: Trip,
        radiusMiles: Double = 3.0,
    ) {
        val stopPoints = trip.stops.mapNotNull { stop ->
            if (stop.isCustomPin && stop.latitude != null && stop.longitude != null) {
                GeoPoint(stop.latitude, stop.longitude)
            } else {
                SeedData.locationById(stop.locationId)?.let { GeoPoint(it.latitude, it.longitude) }
            }
        }
        val markerPoints = trip.specimenMarkers.map { GeoPoint(it.latitude, it.longitude) }
        if (stopPoints.isEmpty() && markerPoints.isEmpty()) return

        val cacheDir = org.osmdroid.config.Configuration.getInstance().osmdroidTileCache ?: return
        val dbFile = java.io.File(cacheDir, "cache.db")
        if (!dbFile.exists()) return

        var db: android.database.sqlite.SQLiteDatabase? = null
        try {
            db = android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(dbFile, null)
            db.execSQL("PRAGMA busy_timeout = 5000;")

            offlineTileSources.forEach { tileSource ->
                val providerName = tileSource.name()
                // Stop tiles: 3-mile radius, zoom 10–19.
                stopPoints.forEach { pt ->
                    deleteTilesAround(db, providerName, pt, radiusMiles, zoomMin = 10, zoomMax = 19)
                }
                // Specimen marker tiles: 1-mile radius, zoom 14–19.
                markerPoints.forEach { pt ->
                    deleteTilesAround(db, providerName, pt, 1.0, zoomMin = 14, zoomMax = 19)
                }
            }
        } catch (_: Throwable) {
            // Best-effort: never crash the app on tile deletion.
        } finally {
            runCatching { db?.close() }
        }
    }

    /**
     * Deletes cached tiles within [radiusMiles] of [pt] for the given tile
     * provider, across zoom levels [zoomMin]..[zoomMax]. Best-effort.
     */
    private fun deleteTilesAround(
        db: android.database.sqlite.SQLiteDatabase,
        providerName: String,
        pt: GeoPoint,
        radiusMiles: Double,
        zoomMin: Int,
        zoomMax: Int,
    ) {
        val latRad = Math.toRadians(pt.latitude)
        val latOffset = radiusMiles / 69.0
        val lngOffset = radiusMiles / (69.0 * cos(latRad))
        val north = pt.latitude + latOffset
        val south = pt.latitude - latOffset
        val east = pt.longitude + lngOffset
        val west = pt.longitude - lngOffset

        for (zoom in zoomMin..zoomMax) {
            val minTileX = lonToTileX(west, zoom)
            val maxTileX = lonToTileX(east, zoom)
            val minTileY = latToTileY(north, zoom)  // North has smaller Y
            val maxTileY = latToTileY(south, zoom)  // South has larger Y

            for (tileX in minTileX..maxTileX) {
                val minKey = MapTileIndex.getTileIndex(zoom, tileX, minTileY)
                val maxKey = MapTileIndex.getTileIndex(zoom, tileX, maxTileY)
                db.delete(
                    "tiles",
                    "provider = ? AND key >= ? AND key <= ?",
                    arrayOf(providerName, minKey.toString(), maxKey.toString()),
                )
            }
        }
    }

    /** Computes the 4 corner GeoPoints of a square bounding box around [center]
     *  with the given [radiusMiles] radius. */
    private fun radiusBoundingBox(center: GeoPoint, radiusMiles: Double): List<GeoPoint> {
        val latRad = Math.toRadians(center.latitude)
        val latOffset = radiusMiles / 69.0
        val lngOffset = radiusMiles / (69.0 * cos(latRad))
        val north = center.latitude + latOffset
        val south = center.latitude - latOffset
        val east = center.longitude + lngOffset
        val west = center.longitude - lngOffset
        return listOf(
            GeoPoint(north, west),
            GeoPoint(north, east),
            GeoPoint(south, east),
            GeoPoint(south, west),
        )
    }

    /** Converts a longitude to tile X coordinate at the given zoom level. */
    private fun lonToTileX(lon: Double, zoom: Int): Int {
        val n = (1 shl zoom).toDouble()
        return floor((lon + 180.0) / 360.0 * n).toInt().coerceIn(0, (1 shl zoom) - 1)
    }

    /** Converts a latitude to tile Y coordinate at the given zoom level (Web Mercator). */
    private fun latToTileY(lat: Double, zoom: Int): Int {
        val latRad = Math.toRadians(lat)
        val n = (1 shl zoom).toDouble()
        return floor((1.0 - kotlin.math.ln(tan(latRad) + 1.0 / cos(latRad)) / PI) / 2.0 * n).toInt().coerceIn(0, (1 shl zoom) - 1)
    }

    /**
     * Download tiles for a single location (e.g., the dig site the user is
     * currently viewing or traveling to). Fetches street + satellite + label
     * tiles at zoom 16-19 so the satellite view works offline at max zoom.
     */
    fun prefetchLocation(context: Context, loc: DigLocation) {
        val pt = GeoPoint(loc.latitude, loc.longitude)
        prefetchArea(context, listOf(pt), zoomMin = 10, zoomMax = 19)
        runCatching { PersistenceManager.recordPointCacheTime(loc.latitude, loc.longitude) }
    }

    /**
     * Download tiles for a 3-mile radius around a single point (lat/lng).
     * Fetches all three tile sources (street, satellite, labels) at zoom 10–19
     * so the map works offline at all zoom levels.
     */
    fun prefetchPoint(context: Context, lat: Double, lng: Double, radiusMiles: Double = 3.0) {
        val pt = GeoPoint(lat, lng)
        val box = radiusBoundingBox(pt, radiusMiles)
        prefetchArea(context, box, zoomMin = 10, zoomMax = 19)
        runCatching { PersistenceManager.recordPointCacheTime(lat, lng) }
    }

    /**
     * Download tiles for a 3-mile radius around the user's saved parking spot.
     * Fetches all three tile sources (street, satellite, labels) at zoom 10–19
     * so the user can visually navigate back to their vehicle with zero signal.
     */
    fun prefetchParkingSpot(context: Context, lat: Double, lng: Double) {
        if (lat == 0.0 && lng == 0.0) return
        prefetchPoint(context, lat, lng, radiusMiles = 3.0)
    }

    /** Maximum tiles to download per tile source per prefetch call. */
    private const val MAX_TILES_PER_SOURCE = 4_000

    /** Latch timeout — don't block forever if the async callback hangs. */
    private const val LATCH_TIMEOUT_SECONDS = 120L

    /**
     * Download tiles for all three tile sources (street, satellite, labels)
     * covering the bounding box around the supplied points. Uses
     * [downloadAreaAsyncNoUI] to avoid the osmdroid progress dialog and
     * "Loading completed with X errors" toast.
     *
     * Safety measures to prevent crashes:
     * - Cap at [MAX_TILES_PER_SOURCE] per source — skips the download if the
     *   area would generate too many tiles (prevents OOM and SQLite contention).
     * - Latch timeout of [LATCH_TIMEOUT_SECONDS] — if the async callback never
     *   fires, the latch unblocks instead of hanging the IO thread forever.
     */
    fun prefetchArea(
        context: Context,
        points: List<GeoPoint>,
        zoomMin: Int = 12,
        zoomMax: Int = 19,
    ) {
        if (points.isEmpty()) return
        val pts = ArrayList(points)

        // Estimate the total tile count for the bounding box.
        // If it exceeds our safety cap, skip — downloading 50,000 tiles would
        // crash the app with OOM or SQLite lock contention.
        val estimatedTiles = estimateTileCount(pts, zoomMin, zoomMax)
        if (estimatedTiles > MAX_TILES_PER_SOURCE) return

        // Download tile sources sequentially rather than launching all three
        // concurrently. Each SqlTileWriter opens the same SQLite database file;
        // concurrent writers contending for the same DB can corrupt it (the
        // root cause of the cache error dialog after a crash). We chain the
        // async downloads so each source finishes before the next begins.
        offlineTileSources.forEach { tileSource ->
            try {
                val writer = org.osmdroid.tileprovider.modules.SqlTileWriter()
                val manager = org.osmdroid.tileprovider.cachemanager.CacheManager(
                    tileSource,
                    writer,
                    zoomMin,
                    zoomMax,
                )
                val latch = java.util.concurrent.CountDownLatch(1)
                val callback = object : org.osmdroid.tileprovider.cachemanager.CacheManager.CacheManagerCallback {
                    override fun onTaskComplete() { latch.countDown() }
                    override fun onTaskFailed(error: Int) { latch.countDown() }
                    override fun updateProgress(progress: Int, currentZoomLevel: Int, zoomMin: Int, zoomMax: Int) {}
                    override fun downloadStarted() {}
                    override fun setPossibleTilesInArea(total: Int) {}
                }
                manager.downloadAreaAsyncNoUI(context, pts, zoomMin, zoomMax, callback)
                // Block until this source finishes or the timeout expires.
                // prefetchArea is always called on Dispatchers.IO, so blocking is safe.
                latch.await(LATCH_TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
                runCatching { writer.onDetach() }
            } catch (_: Throwable) {
                // Best-effort: if one source fails the others may still succeed.
            }
        }
    }

    /**
     * Estimates the total number of tiles across all zoom levels for the
     * bounding box of the given points. Used to skip prefetches that would
     * generate too many tiles and crash the app.
     */
    private fun estimateTileCount(points: List<GeoPoint>, zoomMin: Int, zoomMax: Int): Int {
        if (points.isEmpty()) return 0
        val lats = points.map { it.latitude }
        val lngs = points.map { it.longitude }
        val north = lats.max()
        val south = lats.min()
        val east = lngs.max()
        val west = lngs.min()
        var total = 0
        for (z in zoomMin..zoomMax) {
            val minX = lonToTileX(west, z)
            val maxX = lonToTileX(east, z)
            val minY = latToTileY(north, z)
            val maxY = latToTileY(south, z)
            total += (maxX - minX + 1) * (maxY - minY + 1)
            if (total > MAX_TILES_PER_SOURCE) return total
        }
        return total
    }
}

/**
 * Shared add-location sheet — tap-to-place pin on an interactive map with a name
 * field, coordinate display, and optional "Submit as rock location" toggle.
 *
 * Used by both the Trip Planner (general-purpose stops) and the Dig Sites tab
 * (submitting new dig site locations for admin review).
 *
 * @param onDismiss called when the sheet is cancelled.
 * @param onPick called with (name, lat, lng, submitAsRockLocation) when the user confirms.
 * @param defaultSubmitAsRockLocation if true, the submit toggle defaults to checked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLocationSheet(
    onDismiss: () -> Unit,
    onPick: (name: String, lat: Double, lng: Double, submitAsRockLocation: Boolean) -> Unit,
    defaultSubmitAsRockLocation: Boolean = false,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = AppRepository.instance
    val current by repo.currentLocation.collectAsStateWithLifecycle()
    val auth = AuthRepository.instance
    val profile by repo.profile.collectAsStateWithLifecycle()
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var pinLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var pinName by remember { mutableStateOf("") }
    var submitAsRockLocation by remember { mutableStateOf(defaultSubmitAsRockLocation) }
    var isFullscreen by remember { mutableStateOf(false) }
    var showDownloadSheet by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Drop a custom pin", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).imePadding()) {
                Text(
                    "Tap the map to place a pin for any stop on your route — gas stations, food stops, creek crossings, or unnamed collecting spots.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextLow,
                )
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(295.dp)
                        .clip(RoundedCornerShape(20.dp)),
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            createRockScoutMapView(ctx).apply {
                                controller.setZoom(10.0)
                                controller.setCenter(GeoPoint(current.first, current.second))
                                overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
                                overlays.add(CompassOverlay(ctx, this).apply { enableCompass() })

                                overlays.add(object : Overlay() {
                                    override fun onSingleTapConfirmed(e: MotionEvent?, view: MapView?): Boolean {
                                        if (e == null || view == null) return false
                                        val proj = view.projection
                                        val point = proj.fromPixels(e.x.toInt(), e.y.toInt())
                                        pinLocation = Pair(point.latitude, point.longitude)
                                        view.overlays.removeAll { it is Marker && it.id == "custom_pin_preview" }
                                        val marker = Marker(view).apply {
                                            id = "custom_pin_preview"
                                            position = GeoPoint(point.latitude, point.longitude)
                                            title = "Pin location"
                                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        }
                                        view.overlays.add(marker)
                                        view.invalidate()
                                        return true
                                    }
                                })

                                mapView = this
                            }
                        },
                        update = { /* no-op */ },
                    )
                    MapZoomControls(
                        onZoomIn = { mapView?.let { it.controller.zoomIn() } },
                        onZoomOut = { mapView?.let { it.controller.zoomOut() } },
                        onRecenter = {
                            val mv = mapView ?: return@MapZoomControls
                            pinLocation?.let { mv.controller.animateTo(GeoPoint(it.first, it.second)) }
                                ?: mv.controller.animateTo(GeoPoint(current.first, current.second))
                        },
                        showUser = false,
                        onSatellite = { toggleSatelliteView(mapView) },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                    )
                    MapExpandButton(
                        onClick = { isFullscreen = true },
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                    )
                    MapOfflineNotice(
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
                    )
                }
                if (pinLocation != null) {
                    Spacer(Modifier.height(10.dp))
                    SculptedOutlinedButton(
                        text = "Download map",
                        onClick = { showDownloadSheet = true },
                        accent = Aqua,
                        textColor = Aqua,
                        icon = Icons.Filled.Download,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        "at pin location",
                        style = MaterialTheme.typography.labelSmall,
                        color = Aqua,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp),
                    )
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = pinName,
                    onValueChange = { pinName = ProfanityFilter.filter(it) },
                    label = { Text("Pin name (e.g. \"Gas station\", \"Crystal Creek\")") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth().noAutoFocus(),
                )
                if (pinLocation != null) {
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Aqua, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "%.4f, %.4f".format(pinLocation!!.first, pinLocation!!.second),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextLow,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Aqua.copy(alpha = 0.08f))
                            .glowingBorder(1.dp, Aqua.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .clickable { submitAsRockLocation = !submitAsRockLocation }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (submitAsRockLocation) Aqua else Color.Transparent)
                                .glowingBorder(2.dp, Aqua, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (submitAsRockLocation) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Submit as rock location for review",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextHigh,
                        )
                    }
                }
            }
        },
        confirmButton = {
            SculptedButton(
                text = "Add pin",
                onClick = {
                    val loc = pinLocation ?: return@SculptedButton
                    val name = pinName.ifBlank { "Custom stop" }
                    val shouldSubmit = submitAsRockLocation
                    if (shouldSubmit) {
                        scope.launch {
                            val submitterName = profile.name.ifBlank { "Anonymous" }
                            val submitterId = auth.currentUserId
                            UserPinSubmissionStore.add(
                                UserPinSubmissionStore.PinSubmission(
                                    id = "pin-sub-${java.util.UUID.randomUUID()}",
                                    name = name,
                                    latitude = loc.first,
                                    longitude = loc.second,
                                    submitterName = submitterName,
                                    submitterId = submitterId,
                                    submittedAt = System.currentTimeMillis(),
                                    webSnippet = "",
                                    webUrl = "",
                                    manuallyFlagged = true,
                                )
                            )
                        }
                    }
                    onPick(name, loc.first, loc.second, shouldSubmit)
                },
                accent = Citrine,
                containerColor = Citrine,
                textColor = Color.Black,
                enabled = pinLocation != null,
            )
        },
        dismissButton = { SculptedTextButton(text = "Cancel", onClick = onDismiss, accent = Citrine, textColor = Citrine) },
    )

    if (isFullscreen) {
        FullscreenMapOverlay(
            onDismiss = { isFullscreen = false },
            initialCenter = mapView?.let {
                GeoPoint(it.mapCenter.latitude, it.mapCenter.longitude)
            } ?: GeoPoint(current.first, current.second),
            initialZoom = mapView?.zoomLevelDouble ?: 10.0,
            onMapReady = { fsMv ->
                fsMv.overlays.add(object : Overlay() {
                    override fun onSingleTapConfirmed(e: MotionEvent?, view: MapView?): Boolean {
                        if (e == null || view == null) return false
                        val proj = view.projection
                        val point = proj.fromPixels(e.x.toInt(), e.y.toInt())
                        pinLocation = Pair(point.latitude, point.longitude)
                        view.overlays.removeAll { it is Marker && it.id == "custom_pin_preview_fs" }
                        val marker = Marker(view).apply {
                            id = "custom_pin_preview_fs"
                            position = GeoPoint(point.latitude, point.longitude)
                            title = "Pin location"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        view.overlays.add(marker)
                        view.invalidate()
                        return true
                    }
                })
                fsMv.invalidate()
            },
        )
    }

    if (showDownloadSheet && pinLocation != null) {
        MapDownloadSheet(
            lat = pinLocation!!.first,
            lng = pinLocation!!.second,
            onDismiss = { showDownloadSheet = false },
        )
    }

    MapViewLifecycleEffect(mapView)
}

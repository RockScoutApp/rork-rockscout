package com.rork.rockscout.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.view.MotionEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.Projection
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Radius-based marker clustering overlay for specimen discovery pins.
 *
 * Groups [Marker]s whose projected screen positions fall within [clusterRadiusPx]
 * of each other into a single cluster badge showing the count. When the map is
 * zoomed in past [maxClusteringZoomLevel], every marker renders individually.
 *
 * Tapping a single marker forwards the tap to that marker's own click listener
 * (preserving the existing open-popup behavior). Tapping a cluster badge zooms
 * the map in toward that cluster's centroid so the individual pins separate.
 *
 * This is a self-contained implementation that depends only on osmdroid-android
 * (no OSMBonusPack dependency required).
 */
class SpecimenClusterOverlay(
    private val mapView: MapView,
    private val clusterRadiusPx: Int = 80,
    private val maxClusteringZoomLevel: Int = 18,
    private val onClusterTapped: ((GeoPoint, Int) -> Unit)? = null,
) : Overlay() {

    /** A group of one or more markers rendered as a single pin or badge. */
    private data class Cluster(
        val markers: List<Marker>,
        val center: GeoPoint,
    )

    private val items = mutableListOf<Marker>()
    private var lastZoom: Int = Int.MIN_VALUE
    private var lastWidth: Int = 0
    private var lastHeight: Int = 0
    private val clusters = mutableListOf<Cluster>()

    /** Replaces the clustered markers and forces a recluster on the next draw. */
    fun setItems(newItems: List<Marker>) {
        items.clear()
        items.addAll(newItems)
        lastZoom = Int.MIN_VALUE
        mapView.postInvalidate()
    }

    /** Removes all clustered markers. */
    fun clear() {
        items.clear()
        clusters.clear()
        lastZoom = Int.MIN_VALUE
        mapView.postInvalidate()
    }

    private fun rebuild(projection: Projection) {
        clusters.clear()
        if (items.isEmpty()) return

        val zoom = projection.zoomLevel.toInt()
        val screen = projection.intrinsicScreenRect
        val pointOut = android.graphics.Point()

        // Pre-compute pixel positions for every marker relative to the current projection.
        data class Item(val marker: Marker, val px: Float, val py: Float)
        val projected = ArrayList<Item>(items.size)
        for (m in items) {
            projection.toPixels(m.position, pointOut)
            // Skip markers far outside the viewport — they cannot be hit or seen,
            // and excluding them keeps clustering cheap on large datasets.
            val margin = clusterRadiusPx * 2
            if (pointOut.x < screen.left - margin || pointOut.x > screen.right + margin ||
                pointOut.y < screen.top - margin || pointOut.y > screen.bottom + margin
            ) {
                continue
            }
            projected.add(Item(m, pointOut.x.toFloat(), pointOut.y.toFloat()))
        }

        // Above the max clustering zoom, every marker renders on its own.
        if (zoom > maxClusteringZoomLevel) {
            for (it in projected) {
                clusters.add(Cluster(listOf(it.marker), it.marker.position))
            }
            return
        }

        val r = clusterRadiusPx.toFloat()
        val r2 = r * r
        val assigned = BooleanArray(projected.size)

        // Greedy radius-based grouping: take the next unassigned marker, then pull
        // in every remaining marker whose screen pixel distance is within r.
        for (i in projected.indices) {
            if (assigned[i]) continue
            assigned[i] = true
            val group = mutableListOf(projected[i].marker)
            for (j in (i + 1) until projected.size) {
                if (assigned[j]) continue
                val dx = projected[j].px - projected[i].px
                val dy = projected[j].py - projected[i].py
                if (dx * dx + dy * dy <= r2) {
                    assigned[j] = true
                    group.add(projected[j].marker)
                }
            }
            clusters.add(Cluster(group, centroid(group)))
        }
    }

    /** Geographic centroid (lat/lng average) of a marker group. */
    private fun centroid(markers: List<Marker>): GeoPoint {
        if (markers.size == 1) return markers.first().position
        // Average lat/lng directly — fine for the regional groupings we cluster.
        var lat = 0.0
        var lng = 0.0
        for (m in markers) {
            lat += m.position.latitude
            lng += m.position.longitude
        }
        return GeoPoint(lat / markers.size, lng / markers.size)
    }

    override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
        if (shadow) return
        val projection = mapView.projection
        val zoom = projection.zoomLevel.toInt()
        val screen = projection.intrinsicScreenRect
        // Recluster when zoom changes or the viewport size changes (rotation, etc.).
        if (zoom != lastZoom || screen.width() != lastWidth || screen.height() != lastHeight) {
            rebuild(projection)
            lastZoom = zoom
            lastWidth = screen.width()
            lastHeight = screen.height()
        }
        val pointOut = android.graphics.Point()
        for (cluster in clusters) {
            if (cluster.markers.size == 1) {
                cluster.markers.first().draw(canvas, projection)
            } else {
                projection.toPixels(cluster.center, pointOut)
                drawClusterBadge(canvas, pointOut.x.toFloat(), pointOut.y.toFloat(), cluster.markers.size)
            }
        }
    }

    override fun onSingleTapConfirmed(e: MotionEvent, mapView: MapView): Boolean {
        if (clusters.isEmpty()) return false
        val projection = mapView.projection
        val tapX = e.x
        val tapY = e.y
        val pointOut = android.graphics.Point()

        // Check clusters first (they render on top of individual markers).
        // Iterate in reverse so top-drawn clusters win when badges overlap.
        for (i in clusters.indices.reversed()) {
            val cluster = clusters[i]
            if (cluster.markers.size == 1) {
                val single = cluster.markers.first()
                if (single.hitTest(e, mapView)) {
                    single.showInfoWindow()
                    return true
                }
            } else {
                projection.toPixels(cluster.center, pointOut)
                val dx = tapX - pointOut.x.toFloat()
                val dy = tapY - pointOut.y.toFloat()
                val radius = badgeRadiusPx(cluster.markers.size)
                if (dx * dx + dy * dy <= radius * radius) {
                    onClusterTapped?.invoke(cluster.center, cluster.markers.size)
                    // Default behavior: zoom in toward the cluster centroid so the
                    // individual pins separate. Callers can override via the callback.
                    if (onClusterTapped == null) {
                        zoomIntoCluster(mapView, cluster.center)
                    }
                    return true
                }
            }
        }
        return false
    }

    private fun zoomIntoCluster(mapView: MapView, center: GeoPoint) {
        val current = mapView.zoomLevelDouble
        val target = (current + 2).coerceAtMost(mapView.maxZoomLevel)
        mapView.controller.animateTo(GeoPoint(center.latitude, center.longitude), target, 400L)
    }

    private fun badgeRadiusPx(count: Int): Float {
        val density = mapView.context.resources.displayMetrics.density
        // 2 markers ≈ 18dp, scaling up slowly with count.
        val baseDp = 18f + minOf(8f, count.toFloat() / 6f)
        return baseDp * density
    }

    private var badgePaint: Paint? = null
    private var strokePaint: Paint? = null
    private var textPaint: Paint? = null

    private fun ensurePaints() {
        if (badgePaint != null) return
        val density = mapView.context.resources.displayMetrics.density
        badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Citrine.toArgb()
            style = Paint.Style.FILL
        }
        strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFFFFF.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 2.5f * density
        }
        textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF1A1812.toInt()
            textAlign = Paint.Align.CENTER
            textSize = 12f * density
            isFakeBoldText = true
        }
    }

    private fun drawClusterBadge(canvas: Canvas, cx: Float, cy: Float, count: Int) {
        ensurePaints()
        val r = badgeRadiusPx(count)
        // Soft halo so the badge reads against any map background.
        val halo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Citrine.toArgb()
            alpha = 60
            style = Paint.Style.FILL
        }
        canvas.drawCircle(cx, cy, r + 6f, halo)
        canvas.drawCircle(cx, cy, r, badgePaint!!)
        canvas.drawCircle(cx, cy, r, strokePaint!!)
        val tp = textPaint!!
        val fm = tp.fontMetrics
        val textY = cy - (fm.ascent + fm.descent) / 2f
        canvas.drawText(count.toString(), cx, textY, tp)
    }
}

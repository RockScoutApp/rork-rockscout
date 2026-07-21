package com.rork.rockscout.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

/**
 * Fetches satellite tiles for a bounding box around a given lat/lng and stitches
 * them into a single high-resolution bitmap, then saves it to the "Offline Maps"
 * gallery album so the user can zoom in to see trails and landmarks clearly
 * even when the app isn't working.
 *
 * Uses the Esri World Imagery tile server (same as the map view). Tiles are 256px
 * each. At zoom 15, a 3-mile radius covers roughly 7x7 tiles (1792x1792px).
 * The bitmap is capped at 4096x4096px for memory safety.
 */
object OfflineMapExporter {

    private const val TILE_SIZE = 256
    private const val MAX_BITMAP_DIM = 3072
    private const val ESRI_BASE_URL = "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile"

    /** Skip tile download if the stitched bitmap would exceed this many bytes. */
    private const val MAX_BITMAP_BYTES = 36L * 1024 * 1024

    /**
     * Generates and saves an offline map image for the area around [lat]/[lng].
     *
     * @param context for accessing the content resolver and showing toast
     * @param lat center latitude
     * @param lng center longitude
     * @param zoom tile zoom level (default 15 — ~1.2 km per tile at equator)
     * @param radiusMiles radius to cover (default 3 miles)
     * @return true if the image was saved successfully
     */
    suspend fun saveOfflineMapImage(
        context: Context,
        lat: Double,
        lng: Double,
        zoom: Int = 15,
        radiusMiles: Double = 3.0,
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // Convert radius to degrees
            val milesPerDegreeLat = 69.0
            val latDelta = radiusMiles / milesPerDegreeLat
            val lngDelta = radiusMiles / (milesPerDegreeLat * Math.cos(Math.toRadians(lat)))

            // Calculate tile range for the bounding box
            val minTileX = lonToTileX(lng - lngDelta, zoom)
            val maxTileX = lonToTileX(lng + lngDelta, zoom)
            val minTileY = latToTileY(lat + latDelta, zoom)
            val maxTileY = latToTileY(lat - latDelta, zoom)

            val tilesWide = maxTileX - minTileX + 1
            val tilesHigh = maxTileY - minTileY + 1

            // Cap the bitmap size for memory safety
            var bitmapWidth = (tilesWide * TILE_SIZE).coerceAtMost(MAX_BITMAP_DIM)
            var bitmapHeight = (tilesHigh * TILE_SIZE).coerceAtMost(MAX_BITMAP_DIM)

            // Check estimated memory before allocating. ARGB_8888 = 4 bytes/pixel.
            // If it's too large, reduce zoom to shrink the tile count.
            var effectiveZoom = zoom
            while (bitmapWidth.toLong() * bitmapHeight.toLong() * 4 > MAX_BITMAP_BYTES && effectiveZoom > 10) {
                effectiveZoom--
                val newMinX = lonToTileX(lng - lngDelta, effectiveZoom)
                val newMaxX = lonToTileX(lng + lngDelta, effectiveZoom)
                val newMinY = latToTileY(lat + latDelta, effectiveZoom)
                val newMaxY = latToTileY(lat - latDelta, effectiveZoom)
                bitmapWidth = ((newMaxX - newMinX + 1) * TILE_SIZE).coerceAtMost(MAX_BITMAP_DIM)
                bitmapHeight = ((newMaxY - newMinY + 1) * TILE_SIZE).coerceAtMost(MAX_BITMAP_DIM)
            }
            // Final safety: if still too large, scale down proportionally
            val estBytes = bitmapWidth.toLong() * bitmapHeight.toLong() * 4
            if (estBytes > MAX_BITMAP_BYTES) {
                val scale = Math.sqrt(MAX_BITMAP_BYTES.toDouble() / estBytes)
                bitmapWidth = (bitmapWidth * scale).toInt().coerceAtLeast(256)
                bitmapHeight = (bitmapHeight * scale).toInt().coerceAtLeast(256)
            }

            // Recompute tile range with the effective zoom
            val effMinTileX = lonToTileX(lng - lngDelta, effectiveZoom)
            val effMaxTileX = lonToTileX(lng + lngDelta, effectiveZoom)
            val effMinTileY = latToTileY(lat + latDelta, effectiveZoom)
            val effMaxTileY = latToTileY(lat - latDelta, effectiveZoom)
            val effTilesWide = effMaxTileX - effMinTileX + 1
            val effTilesHigh = effMaxTileY - effMinTileY + 1

            // Create the stitched bitmap
            val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.BLACK)

            val paint = Paint().apply { isFilterBitmap = true }

            // Fetch and draw each tile using the effective zoom level
            val scaleX = bitmapWidth.toFloat() / (effTilesWide * TILE_SIZE)
            val scaleY = bitmapHeight.toFloat() / (effTilesHigh * TILE_SIZE)
            for (tileX in effMinTileX..effMaxTileX) {
                for (tileY in effMinTileY..effMaxTileY) {
                    val tileBitmap = fetchTile(effectiveZoom, tileX, tileY)
                    if (tileBitmap != null) {
                        val destX = ((tileX - effMinTileX) * TILE_SIZE * scaleX).toInt()
                        val destY = ((tileY - effMinTileY) * TILE_SIZE * scaleY).toInt()
                        val destW = (TILE_SIZE * scaleX).toInt().coerceAtLeast(1)
                        val destH = (TILE_SIZE * scaleY).toInt().coerceAtLeast(1)
                        val srcRect = android.graphics.Rect(0, 0, tileBitmap.width, tileBitmap.height)
                        val destRect = android.graphics.Rect(
                            destX, destY,
                            (destX + destW).coerceAtMost(bitmapWidth),
                            (destY + destH).coerceAtMost(bitmapHeight),
                        )
                        canvas.drawBitmap(tileBitmap, srcRect, destRect, paint)
                        tileBitmap.recycle()
                    }
                }
            }

            // Add a small label with coordinates
            val labelPaint = Paint().apply {
                color = Color.WHITE
                textSize = 28f
                setShadowLayer(4f, 2f, 2f, Color.BLACK)
            }
            val label = String.format("RockScout Offline Map · %.4f, %.4f · Z%d", lat, lng, effectiveZoom)
            canvas.drawText(label, 16f, bitmapHeight - 16f, labelPaint)

            // Save to "Offline Maps" gallery album
            val title = "RockScout_Map_${System.currentTimeMillis()}"
            val uri = GallerySaver.saveBitmapToAlbum(
                resolver = context.contentResolver,
                bitmap = bitmap,
                title = title,
                albumName = "Offline Maps",
            )

            bitmap.recycle()

            if (uri != null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Saved to gallery → Offline Maps folder", Toast.LENGTH_LONG).show()
                }
                true
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Could not save map image to gallery.", Toast.LENGTH_SHORT).show()
                }
                false
            }
        } catch (e: OutOfMemoryError) {
            // Retry with smaller area if we hit memory limits
            if (radiusMiles > 1.0) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Reducing map area for memory safety…", Toast.LENGTH_SHORT).show()
                }
                return@withContext saveOfflineMapImage(context, lat, lng, zoom.coerceAtMost(14), radiusMiles * 0.5)
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Could not generate map image — area too large for device memory.", Toast.LENGTH_LONG).show()
            }
            false
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to generate map image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            false
        }
    }

    /** Converts longitude to tile X coordinate at the given zoom level. */
    private fun lonToTileX(lon: Double, zoom: Int): Int {
        return Math.floor((lon + 180) / 360 * (1 shl zoom)).toInt()
    }

    /** Converts latitude to tile Y coordinate at the given zoom level. */
    private fun latToTileY(lat: Double, zoom: Int): Int {
        val latRad = Math.toRadians(lat)
        return Math.floor((1 - Math.log(Math.tan(latRad) + 1 / Math.cos(latRad)) / Math.PI) / 2 * (1 shl zoom)).toInt()
    }

    /** Fetches a single tile from the Esri World Imagery server. */
    private fun fetchTile(zoom: Int, tileX: Int, tileY: Int): Bitmap? {
        return try {
            // Esri URL format: {base}/{z}/{y}/{x}.png
            val url = "$ESRI_BASE_URL/$zoom/$tileY/$tileX.png"
            val conn = URL(url).openConnection().apply {
                connectTimeout = 10_000
                readTimeout = 15_000
                setRequestProperty("User-Agent", "RockScout/1.0")
            }
            conn.getInputStream().use { stream ->
                // Tiles are 256×256 so this never downsamples, but routing
                // through decodeSampledBitmap bounds memory if a server ever
                // returns an oversized tile.
                com.rork.rockscout.data.ImageUtils.decodeSampledBitmap(
                    stream, maxDimension = 512,
                )
            }
        } catch (_: Exception) {
            null
        }
    }
}

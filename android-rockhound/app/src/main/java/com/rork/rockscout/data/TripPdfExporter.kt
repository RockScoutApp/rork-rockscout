package com.rork.rockscout.data

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Generates a simple PDF summary of a trip using Android's built-in [PdfDocument].
 *
 * The PDF contains: trip name + date header, the route stops list with numbering,
 * total specimen count, target specimens, gear checklist, and an optional route
 * map screenshot. Saved to the Downloads folder via [MediaStore] (API 29+) or
 * direct file write (older APIs).
 */
object TripPdfExporter {

    /**
     * Generates and saves a PDF for [trip]. If [mapBitmap] is non-null, it's
     * rendered as a route map image at the bottom of the document.
     *
     * @return the saved file [Uri], or null on failure.
     */
    fun exportTripPdf(context: Context, trip: Trip, mapBitmap: Bitmap? = null): Uri? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val density = context.resources.displayMetrics.density

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 24f * density
            isAntiAlias = true
            isFakeBoldText = true
        }
        val headerPaint = Paint().apply {
            color = Color.parseColor("#E8A33D")
            textSize = 16f * density
            isAntiAlias = true
            isFakeBoldText = true
        }
        val bodyPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 13f * density
            isAntiAlias = true
        }
        val footerPaint = Paint().apply {
            color = Color.GRAY
            textSize = 10f * density
            isAntiAlias = true
            textSkewX = -0.3f
        }

        val margin = 32f * density
        var y = margin

        // Title
        canvas.drawText(trip.name, margin, y, titlePaint)
        y += titlePaint.descent() - titlePaint.ascent() + 8f * density

        // Date
        val dateFormat = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault())
        canvas.drawText(dateFormat.format(Date(trip.date)), margin, y, bodyPaint)
        y += bodyPaint.descent() - bodyPaint.ascent() + 16f * density

        // Route section
        canvas.drawText("Route (${trip.stops.size} stops)", margin, y, headerPaint)
        y += headerPaint.descent() - headerPaint.ascent() + 6f * density
        trip.stops.forEachIndexed { idx, stop ->
            canvas.drawText("${idx + 1}. ${stop.locationName}", margin + 12f * density, y, bodyPaint)
            y += bodyPaint.descent() - bodyPaint.ascent() + 2f * density
        }
        y += 12f * density

        // Total distance
        val totalDistance = calculateRouteDistance(trip)
        if (totalDistance > 0) {
            canvas.drawText(
                "Estimated route distance: ${"%.1f".format(totalDistance)} miles",
                margin, y, bodyPaint,
            )
            y += bodyPaint.descent() - bodyPaint.ascent() + 12f * density
        }

        // Specimen markers count
        if (trip.specimenMarkers.isNotEmpty()) {
            canvas.drawText(
                "Specimen markers recorded: ${trip.specimenMarkers.size}",
                margin, y, bodyPaint,
            )
            y += bodyPaint.descent() - bodyPaint.ascent() + 12f * density
        }

        // Target specimens
        if (trip.targetSpecimens.isNotEmpty()) {
            canvas.drawText("Hunting for:", margin, y, headerPaint)
            y += headerPaint.descent() - headerPaint.ascent() + 6f * density
            trip.targetSpecimens.forEach { spec ->
                canvas.drawText("• $spec", margin + 12f * density, y, bodyPaint)
                y += bodyPaint.descent() - bodyPaint.ascent() + 2f * density
            }
            y += 12f * density
        }

        // Gear checklist
        if (trip.gearChecklist.isNotEmpty()) {
            canvas.drawText("Gear checklist:", margin, y, headerPaint)
            y += headerPaint.descent() - headerPaint.ascent() + 6f * density
            trip.gearChecklist.forEach { item ->
                canvas.drawText("☐ $item", margin + 12f * density, y, bodyPaint)
                y += bodyPaint.descent() - bodyPaint.ascent() + 2f * density
            }
            y += 12f * density
        }

        // Notes
        if (trip.notes.isNotBlank()) {
            canvas.drawText("Notes:", margin, y, headerPaint)
            y += headerPaint.descent() - headerPaint.ascent() + 6f * density
            // Simple word-wrap for notes
            val maxWidth = (595 - 64) * density
            val words = trip.notes.split(" ")
            val line = StringBuilder()
            for (word in words) {
                val testLine = if (line.isEmpty()) word else "$line $word"
                if (bodyPaint.measureText(testLine) > maxWidth) {
                    canvas.drawText(line.toString(), margin + 12f * density, y, bodyPaint)
                    y += bodyPaint.descent() - bodyPaint.ascent() + 2f * density
                    line.clear()
                    line.append(word)
                } else {
                    line.clear()
                    line.append(testLine)
                }
            }
            if (line.isNotEmpty()) {
                canvas.drawText(line.toString(), margin + 12f * density, y, bodyPaint)
                y += bodyPaint.descent() - bodyPaint.ascent() + 12f * density
            }
        }

        // Map screenshot
        if (mapBitmap != null) {
            y += 8f * density
            val mapHeaderPaint = Paint().apply {
                color = Color.parseColor("#E8A33D")
                textSize = 16f * density
                isAntiAlias = true
                isFakeBoldText = true
            }
            canvas.drawText("Route Map", margin, y, mapHeaderPaint)
            y += mapHeaderPaint.descent() - mapHeaderPaint.ascent() + 6f * density
            val availWidth = 595f - 64f * density
            val scaledHeight = mapBitmap.height.toFloat() * (availWidth / mapBitmap.width.toFloat())
            val scaled = Bitmap.createScaledBitmap(
                mapBitmap,
                availWidth.toInt().coerceAtLeast(100),
                scaledHeight.toInt().coerceAtLeast(100),
                true,
            )
            canvas.drawBitmap(scaled, margin, y, null)
            y += scaledHeight + 16f * density
        }

        // Footer
        canvas.drawText("Planned with RockScout", margin, 842f - margin, footerPaint)

        document.finishPage(page)

        // Save to Downloads
        val fileName = "RockScout_Trip_${trip.name.replace(Regex("[^A-Za-z0-9]"), "_")}.pdf"
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveViaMediaStore(context, fileName, document)
            } else {
                saveViaLegacyFile(context, fileName, document)
            }
        } catch (e: Exception) {
            document.close()
            null
        } finally {
            document.close()
        }
    }

    private fun saveViaMediaStore(context: Context, fileName: String, document: PdfDocument): Uri? {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), values) ?: return null
        resolver.openOutputStream(uri)?.use { out ->
            document.writeTo(out)
            out.flush()
        }
        Toast.makeText(context, "PDF saved to Downloads: $fileName", Toast.LENGTH_LONG).show()
        return uri
    }

    private fun saveViaLegacyFile(context: Context, fileName: String, document: PdfDocument): Uri? {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        FileOutputStream(file).use { out ->
            document.writeTo(out)
            out.flush()
        }
        Toast.makeText(context, "PDF saved to Downloads: $fileName", Toast.LENGTH_LONG).show()
        return Uri.fromFile(file)
    }

    /** Calculates total route distance in miles across consecutive stops with coordinates. */
    private fun calculateRouteDistance(trip: Trip): Double {
        val points = trip.stops.mapNotNull { stop ->
            if (stop.isCustomPin && stop.latitude != null && stop.longitude != null) {
                Pair(stop.latitude, stop.longitude)
            } else {
                SeedData.locationById(stop.locationId)?.let { Pair(it.latitude, it.longitude) }
            }
        }
        var total = 0.0
        for (i in 1 until points.size) {
            total += AppRepository.distanceMiles(
                points[i - 1].first, points[i - 1].second,
                points[i].first, points[i].second,
            )
        }
        return total
    }
}

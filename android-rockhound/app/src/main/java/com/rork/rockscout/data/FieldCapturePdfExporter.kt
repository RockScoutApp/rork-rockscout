package com.rork.rockscout.data

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Generates a one-page PDF summary of a user-made field capture / specimen card.
 *
 * The PDF contains: the capture photo (first image), custom or specimen name,
 * location, date, general info, confidence, optional coordinates, and a
 * "Logged with RockScout" footer. Saved to Downloads via MediaStore (API 29+)
 * or direct file write (older APIs), then surfaced through the system share sheet.
 */
object FieldCapturePdfExporter {

    private const val PAGE_W = 595
    private const val PAGE_H = 842
    private const val MARGIN = 36f
    private const val ACCENT = 0xFFE8A33D.toInt()

    /** Generate and save a PDF for [capture]. [specimenName] is the fallback
     *  display name when the capture has no custom name. */
    fun exportCapturePdf(context: Context, capture: CapturedPhoto, specimenName: String = "Field Capture"): Uri? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, 1).create()
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
            color = ACCENT
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
        val labelPaint = Paint().apply {
            color = Color.parseColor("#2C6F9B")
            textSize = 12f * density
            isAntiAlias = true
            isFakeBoldText = true
        }

        val margin = 32f * density
        var y = margin

        val displayName = capture.displayName(specimenName)

        // Title
        canvas.drawText(displayName, margin, y, titlePaint)
        y += titlePaint.descent() - titlePaint.ascent() + 8f * density

        // Date + confidence line
        val dateFormat = SimpleDateFormat("EEEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault())
        canvas.drawText(
            "${dateFormat.format(Date(capture.timestamp))}  ·  ${capture.confidence}% match",
            margin, y, bodyPaint,
        )
        y += bodyPaint.descent() - bodyPaint.ascent() + 16f * density

        // Location
        if (capture.customLocation.isNotBlank()) {
            canvas.drawText("Location", margin, y, headerPaint)
            y += headerPaint.descent() - headerPaint.ascent() + 4f * density
            canvas.drawText(capture.customLocation, margin + 12f * density, y, bodyPaint)
            y += bodyPaint.descent() - bodyPaint.ascent() + 12f * density
        }

        // Coordinates
        if (capture.latitude != null && capture.longitude != null) {
            canvas.drawText("Coordinates", margin, y, headerPaint)
            y += headerPaint.descent() - headerPaint.ascent() + 4f * density
            canvas.drawText(
                "%.6f, %.6f".format(Locale.US, capture.latitude, capture.longitude),
                margin + 12f * density, y, bodyPaint,
            )
            y += bodyPaint.descent() - bodyPaint.ascent() + 12f * density
        }

        // General info
        if (capture.generalInfo.isNotBlank()) {
            canvas.drawText("Notes", margin, y, headerPaint)
            y += headerPaint.descent() - headerPaint.ascent() + 6f * density
            val maxWidth = (PAGE_W - 64) * density
            val words = capture.generalInfo.split(" ")
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

        // Photo
        if (capture.imageUris.isNotEmpty()) {
            val photoBitmap = loadHighResBitmap(context, capture.imageUris.first())
            if (photoBitmap != null) {
                y += 8f * density
                canvas.drawText("Photo", margin, y, headerPaint)
                y += headerPaint.descent() - headerPaint.ascent() + 6f * density
                val availWidth = PAGE_W - 64f * density
                val scaledHeight = photoBitmap.height.toFloat() * (availWidth / photoBitmap.width.toFloat())
                val maxPhotoHeight = 260f * density
                val finalHeight = scaledHeight.coerceAtMost(maxPhotoHeight)
                val finalWidth = if (scaledHeight > maxPhotoHeight) {
                    photoBitmap.width.toFloat() * (maxPhotoHeight / photoBitmap.height.toFloat())
                } else {
                    availWidth
                }
                val scaled = Bitmap.createScaledBitmap(
                    photoBitmap,
                    finalWidth.toInt().coerceAtLeast(100),
                    finalHeight.toInt().coerceAtLeast(100),
                    true,
                )
                canvas.drawBitmap(scaled, margin, y, null)
                y += scaled.height + 16f * density
            }
        }

        // Footer
        canvas.drawText("Logged with RockScout", margin, 842f - margin, footerPaint)

        document.finishPage(page)

        val safeName = displayName.replace(Regex("[^A-Za-z0-9]"), "_").take(40)
        val fileName = "RockScout_Capture_${safeName}_${stamp()}.pdf"
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

    private fun loadHighResBitmap(context: Context, url: String?): Bitmap? = try {
        if (url.isNullOrBlank()) return null
        val uri = Uri.parse(url)
        val scheme = uri.scheme
        val openStream: () -> java.io.InputStream? = {
            when (scheme) {
                "http", "https" -> URL(url).openStream()
                else -> context.contentResolver.openInputStream(uri)
            }
        }
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        openStream()?.use { BitmapFactory.decodeStream(it, null, opts) }
        if (opts.outWidth <= 0 || opts.outHeight <= 0) return null
        val srcW = opts.outWidth
        val srcH = opts.outHeight
        val longer = maxOf(srcW, srcH)
        var sample = 1
        while (longer / sample > 2048) sample *= 2
        val decodeOpts = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        openStream()?.use { BitmapFactory.decodeStream(it, null, decodeOpts) }
    } catch (_: Throwable) {
        null
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
        launchShare(context, uri, fileName)
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
        val uri = Uri.fromFile(file)
        launchShare(context, uri, fileName)
        return uri
    }

    private fun launchShare(context: Context, uri: Uri, fileName: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "RockScout Field Capture: $fileName")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        SafeLinkOpener.openShareChooser(context, intent, "Share capture PDF")
    }

    private fun stamp(): String =
        SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
}

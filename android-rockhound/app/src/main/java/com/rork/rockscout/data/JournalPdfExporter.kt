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
 * Generates a one-page PDF summary of a Field Journal entry.
 *
 * The PDF contains: location, date, weather summary, notes, attached field
 * capture names, embedded photos, and a "Logged with RockScout" footer. Saved
 * to Downloads via MediaStore (API 29+) or direct file write (older APIs), then
 * surfaced through the system share sheet.
 */
object JournalPdfExporter {

    private const val PAGE_W = 595
    private const val PAGE_H = 842
    private const val MARGIN = 36f
    private const val ACCENT = 0xFFE8A33D.toInt()
    private const val AQUA = 0xFF6FA8C7.toInt()

    /** Generate and save a PDF for [entry]. [attachedCaptures] supplies the
     *  display names for captures linked to this entry. */
    fun exportJournalPdf(
        context: Context,
        entry: JournalEntry,
        attachedCaptures: List<String> = emptyList(),
    ): Uri? {
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
        val aquaHeaderPaint = Paint().apply {
            color = AQUA
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

        val dateFormat = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault())
        val locationName = entry.location.ifBlank { "Field Journal Entry" }

        // Title
        canvas.drawText(locationName, margin, y, titlePaint)
        y += titlePaint.descent() - titlePaint.ascent() + 8f * density

        // Date
        canvas.drawText(dateFormat.format(Date(entry.date)), margin, y, bodyPaint)
        y += bodyPaint.descent() - bodyPaint.ascent() + 16f * density

        // Weather
        if (entry.weatherSummary.isNotBlank()) {
            canvas.drawText("Weather", margin, y, aquaHeaderPaint)
            y += aquaHeaderPaint.descent() - aquaHeaderPaint.ascent() + 4f * density
            canvas.drawText(entry.weatherSummary, margin + 12f * density, y, bodyPaint)
            y += bodyPaint.descent() - bodyPaint.ascent() + 12f * density
        }

        // Notes
        if (entry.notes.isNotBlank()) {
            canvas.drawText("Notes", margin, y, headerPaint)
            y += headerPaint.descent() - headerPaint.ascent() + 6f * density
            val notePaint = TextPaint().apply {
                color = Color.DKGRAY
                textSize = 13f * density
                isAntiAlias = true
            }
            val maxWidth = (PAGE_W - 64) * density
            val layout = wrapLayout(entry.notes, notePaint, maxWidth.toInt())
            canvas.translate(margin, y)
            layout.draw(canvas)
            canvas.translate(-margin, -y)
            y += layout.height + 16f * density
        }

        // Attached captures
        if (attachedCaptures.isNotEmpty()) {
            canvas.drawText("Attached captures", margin, y, headerPaint)
            y += headerPaint.descent() - headerPaint.ascent() + 6f * density
            attachedCaptures.forEach { name ->
                canvas.drawText("• $name", margin + 12f * density, y, bodyPaint)
                y += bodyPaint.descent() - bodyPaint.ascent() + 2f * density
            }
            y += 12f * density
        }

        // Specimen markers
        if (entry.specimenMarkers.isNotEmpty()) {
            canvas.drawText("Specimen markers", margin, y, aquaHeaderPaint)
            y += aquaHeaderPaint.descent() - aquaHeaderPaint.ascent() + 6f * density
            entry.specimenMarkers.forEach { marker ->
                canvas.drawText(
                    "• ${marker.name}  (${"%.5f".format(Locale.US, marker.latitude)}, ${"%.5f".format(Locale.US, marker.longitude)})",
                    margin + 12f * density, y, bodyPaint,
                )
                y += bodyPaint.descent() - bodyPaint.ascent() + 2f * density
            }
            y += 12f * density
        }

        // Photos
        if (entry.photoUris.isNotEmpty()) {
            val photoBitmap = loadHighResBitmap(context, entry.photoUris.first())
            if (photoBitmap != null) {
                y += 8f * density
                canvas.drawText("Photo", margin, y, headerPaint)
                y += headerPaint.descent() - headerPaint.ascent() + 6f * density
                val availWidth = PAGE_W - 64f * density
                val scaledHeight = photoBitmap.height.toFloat() * (availWidth / photoBitmap.width.toFloat())
                val maxPhotoHeight = 220f * density
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

        val safeName = locationName.replace(Regex("[^A-Za-z0-9]"), "_").take(40)
        val fileName = "RockScout_Journal_${safeName}_${stamp()}.pdf"
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

    private fun wrapLayout(text: String, paint: TextPaint, maxWidth: Int): StaticLayout =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder
                .obtain(text, 0, text.length, paint, maxWidth)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1.15f)
                .setIncludePad(false)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(text, paint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.15f, 0f, false)
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
            putExtra(Intent.EXTRA_SUBJECT, "RockScout Field Journal: $fileName")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        SafeLinkOpener.openShareChooser(context, intent, "Share journal PDF")
    }

    private fun stamp(): String =
        SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
}

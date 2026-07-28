package com.rork.rockscout.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Generates a 1-2 page PDF identification report from an AI scan result.
 *
 * Page 1 contains the captured photo, AI analysis summary, and the top
 * specimen/artifact matches with confidence scores and reasoning.
 *
 * Page 2 (only when relevant content exists) contains the assemblage
 * breakdown, web references, and a field-capture footer note.
 *
 * The PDF uses the same visual style as [CollectionPdfExporter]: Citrine
 * #E8A33D accent, StaticLayout text wrapping, and high-resolution bitmap
 * loading. The file is saved to the public Downloads folder and optionally
 * surfaced through the system share sheet.
 */
object SpecimenReportPdfExporter {

    private const val PAGE_W = 595
    private const val PAGE_H = 842
    private const val MARGIN = 36f
    private const val ACCENT = 0xFFE8A33D.toInt()

    /** Data bundle for a single identification report. */
    data class MatchEntry(
        val name: String,
        val confidence: Int,
        val reasoning: String,
    )

    data class ReportData(
        val capturedBitmap: Bitmap?,
        val capturedUri: Uri?,
        val matches: List<MatchEntry>,
        val aiSummary: String,
        val assemblage: AssemblageResult?,
        val webReferences: List<WebReference>,
        val locationText: String,
        val isArtifact: Boolean,
    )

    /** Build and save the report PDF, then open the share sheet.
     * @return the saved file [Uri], or null on failure. */
    suspend fun export(context: Context, data: ReportData): Uri? = withContext(Dispatchers.IO) {
        if (data.matches.isEmpty() && data.aiSummary.isBlank()) return@withContext null

        val document = PdfDocument()
        try {
            drawReportPage1(context, document, data)
            val hasPage2 = data.assemblage?.components?.isNotEmpty() == true ||
                data.webReferences.isNotEmpty()
            if (hasPage2) drawReportPage2(document, data)

            val fileName = "RockScout_Report_${stamp()}.pdf"
            val uri = save(context, fileName, document) ?: return@withContext null
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Report saved to Downloads: $fileName", Toast.LENGTH_LONG).show()
                launchShare(context, uri)
            }
            uri
        } catch (_: Throwable) {
            null
        } finally {
            document.close()
        }
    }

    // ── Page 1 ────────────────────────────────────────────────────────────

    private fun drawReportPage1(context: Context, document: PdfDocument, data: ReportData) {
        val page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, 1).create())
        val canvas = page.canvas

        // Top accent band
        canvas.drawRect(0f, 0f, PAGE_W.toFloat(), 12f, Paint().apply { color = ACCENT })

        // Title
        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 26f
            isAntiAlias = true
            isFakeBoldText = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("RockScout Identification Report", MARGIN, 56f, titlePaint)

        // Date
        val datePaint = Paint().apply {
            color = ACCENT
            textSize = 12f
            isAntiAlias = true
            isFakeBoldText = true
        }
        val dateStr = SimpleDateFormat("EEEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(Date())
        canvas.drawText("Generated $dateStr", MARGIN, 78f, datePaint)

        var y = 100f

        // Captured photo
        val imgMaxW = PAGE_W - MARGIN * 2
        val imgMaxH = 300f
        val bitmap = loadBitmap(context, data)
        if (bitmap != null) {
            val scale = minOf(imgMaxW / bitmap.width, imgMaxH / bitmap.height)
            val drawW = bitmap.width * scale
            val drawH = bitmap.height * scale
            val left = MARGIN + (imgMaxW - drawW) / 2f
            canvas.drawBitmap(bitmap, left, y, null)
            val framePaint = Paint().apply {
                color = Color.LTGRAY
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }
            canvas.drawRect(left, y, left + drawW, y + drawH, framePaint)
            y = y + drawH + 16f
        } else {
            val placePaint = Paint().apply { color = 0xFFEFEBE3.toInt() }
            canvas.drawRect(MARGIN, y, MARGIN + imgMaxW, y + 100f, placePaint)
            val capPaint = Paint().apply {
                color = Color.GRAY
                textSize = 11f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("Captured photo unavailable", PAGE_W / 2f, y + 55f, capPaint)
            y += 116f
        }

        // AI Analysis section
        if (data.aiSummary.isNotBlank()) {
            y = drawSectionHeader(canvas, "AI Analysis", y)
            val bodyPaint = TextPaint().apply {
                color = Color.DKGRAY
                textSize = 10f
                isAntiAlias = true
            }
            val laid = wrapLayout(data.aiSummary, bodyPaint, imgMaxW.toInt())
            canvas.translate(MARGIN, y)
            laid.draw(canvas)
            canvas.translate(-MARGIN, -y)
            y += laid.height + 14f
        }

        // Top Matches section
        y = drawSectionHeader(canvas, "Top Matches", y)
        val namePaint = Paint().apply {
            color = Color.BLACK
            textSize = 11f
            isAntiAlias = true
            isFakeBoldText = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val confPaint = Paint().apply {
            color = ACCENT
            textSize = 10f
            isAntiAlias = true
            isFakeBoldText = true
        }
        val reasonPaint = TextPaint().apply {
            color = Color.DKGRAY
            textSize = 9f
            isAntiAlias = true
        }
        data.matches.take(5).forEachIndexed { idx, match ->
            if (y > PAGE_H - MARGIN - 40f) return@forEachIndexed

            // Confidence color
            val confColor = when {
                match.confidence >= 80 -> 0xFF2E7D32.toInt()
                match.confidence >= 60 -> ACCENT
                else -> 0xFFC62828.toInt()
            }
            val numStr = "${idx + 1}."
            canvas.drawText(numStr, MARGIN, y + 10f, namePaint)
            canvas.drawText(match.name, MARGIN + 22f, y + 10f, namePaint)

            val confText = "${match.confidence}% match"
            confPaint.color = confColor
            val confX = PAGE_W - MARGIN - confPaint.measureText(confText)
            canvas.drawText(confText, confX, y + 10f, confPaint)
            y += 16f

            if (match.reasoning.isNotBlank()) {
                val laid = wrapLayout(match.reasoning, reasonPaint, (imgMaxW - 22f).toInt())
                canvas.translate(MARGIN + 22f, y)
                laid.draw(canvas)
                canvas.translate(-(MARGIN + 22f), -y)
                y += laid.height + 4f
            }
            y += 6f
        }

        // Footer
        drawFooter(canvas, "RockScout AI Identification Report")
        document.finishPage(page)
    }

    // ── Page 2 ────────────────────────────────────────────────────────────

    private fun drawReportPage2(document: PdfDocument, data: ReportData) {
        val page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, 2).create())
        val canvas = page.canvas
        val imgMaxW = PAGE_W - MARGIN * 2
        var y = 56f

        // Accent band
        canvas.drawRect(0f, 0f, PAGE_W.toFloat(), 6f, Paint().apply { color = ACCENT })

        // Assemblage Analysis
        val assemblage = data.assemblage
        if (assemblage != null && assemblage.components.isNotEmpty()) {
            y = drawSectionHeader(canvas, "Assemblage Analysis", y)

            val labelPaint = Paint().apply {
                color = Color.DKGRAY
                textSize = 10f
                isAntiAlias = true
                isFakeBoldText = true
            }
            val valuePaint = TextPaint().apply {
                color = Color.BLACK
                textSize = 10f
                isAntiAlias = true
            }

            if (assemblage.hostRock.isNotBlank()) {
                canvas.drawText("Host Rock:", MARGIN, y + 10f, labelPaint)
                canvas.drawText(assemblage.hostRock, MARGIN + 90f, y + 10f, valuePaint)
                y += 20f
            }
            if (assemblage.summary.isNotBlank()) {
                val laid = wrapLayout(assemblage.summary, valuePaint, imgMaxW.toInt())
                canvas.translate(MARGIN, y)
                laid.draw(canvas)
                canvas.translate(-MARGIN, -y)
                y += laid.height + 10f
            }
            assemblage.components.forEach { comp ->
                if (y > PAGE_H - MARGIN - 60f) return@forEach
                canvas.drawText(
                    "${comp.name} — ${comp.percentage}%",
                    MARGIN, y + 10f, labelPaint,
                )
                y += 16f
                if (comp.evidence.isNotBlank()) {
                    val laid = wrapLayout(comp.evidence, valuePaint, imgMaxW.toInt())
                    canvas.translate(MARGIN, y)
                    laid.draw(canvas)
                    canvas.translate(-MARGIN, -y)
                    y += laid.height + 6f
                }
            }
            y += 10f
        }

        // Web References
        if (data.webReferences.isNotEmpty()) {
            y = drawSectionHeader(canvas, "Web References", y)
            val refPaint = TextPaint().apply {
                color = Color.DKGRAY
                textSize = 9f
                isAntiAlias = true
            }
            val urlPaint = Paint().apply {
                color = ACCENT
                textSize = 8f
                isAntiAlias = true
            }
            data.webReferences.take(6).forEach { ref ->
                if (y > PAGE_H - MARGIN - 60f) return@forEach
                if (ref.title.isNotBlank()) {
                    val titlePaint = Paint().apply {
                        color = Color.BLACK
                        textSize = 10f
                        isAntiAlias = true
                        isFakeBoldText = true
                    }
                    canvas.drawText(ellipsize(ref.title, titlePaint, imgMaxW), MARGIN, y + 10f, titlePaint)
                    y += 16f
                }
                if (ref.snippet.isNotBlank()) {
                    val laid = wrapLayout(ref.snippet, refPaint, imgMaxW.toInt())
                    canvas.translate(MARGIN, y)
                    laid.draw(canvas)
                    canvas.translate(-MARGIN, -y)
                    y += laid.height + 4f
                }
                if (ref.url.isNotBlank()) {
                    canvas.drawText(ellipsize(ref.url, urlPaint, imgMaxW), MARGIN, y + 8f, urlPaint)
                    y += 14f
                }
                y += 6f
            }
        }

        // Field Capture Note
        if (y < PAGE_H - MARGIN - 40f) {
            y += 10f
            val notePaint = TextPaint().apply {
                color = Color.GRAY
                textSize = 9f
                isAntiAlias = true
            }
            val generatedDate = SimpleDateFormat("EEEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(Date())
            val noteText = buildString {
                append("Specimen ${if (data.isArtifact) "artifact" else "rock"} was field-captured via RockScout AI vision on $generatedDate.")
                if (data.locationText.isNotBlank()) {
                    append(" Approximate location: ${data.locationText} (city/region only for privacy).")
                }
            }
            val laid = wrapLayout(noteText, notePaint, imgMaxW.toInt())
            canvas.translate(MARGIN, y)
            laid.draw(canvas)
            canvas.translate(-MARGIN, -y)
        }

        drawFooter(canvas, "RockScout AI Identification Report — Page 2")
        document.finishPage(page)
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private fun drawSectionHeader(canvas: android.graphics.Canvas, title: String, y: Float): Float {
        val headerPaint = Paint().apply {
            color = ACCENT
            textSize = 14f
            isAntiAlias = true
            isFakeBoldText = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(title, MARGIN, y + 14f, headerPaint)
        val linePaint = Paint().apply { color = Color.LTGRAY }
        canvas.drawLine(MARGIN, y + 20f, PAGE_W - MARGIN, y + 20f, linePaint)
        return y + 30f
    }

    private fun drawFooter(canvas: android.graphics.Canvas, text: String) {
        val footerPaint = Paint().apply {
            color = Color.GRAY
            textSize = 9f
            isAntiAlias = true
            textSkewX = -0.3f
        }
        canvas.drawText(text, MARGIN, PAGE_H - MARGIN + 6f, footerPaint)
    }

    private fun loadBitmap(context: Context, data: ReportData): Bitmap? {
        // Prefer in-memory bitmap (always available from camera capture)
        data.capturedBitmap?.let { return it }
        // Fall back to URI-based loading
        data.capturedUri?.let { uri -> return loadHighResBitmap(context, uri) }
        return null
    }

    private const val MAX_IMAGE_EDGE = 2048

    private fun loadHighResBitmap(context: Context, uri: Uri): Bitmap? = try {
        val openStream: () -> java.io.InputStream? = {
            when (uri.scheme) {
                "http", "https" -> URL(uri.toString()).openStream()
                else -> context.contentResolver.openInputStream(uri)
            }
        }
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        openStream()?.use { BitmapFactory.decodeStream(it, null, opts) }
        if (opts.outWidth <= 0 || opts.outHeight <= 0) return null
        val longer = maxOf(opts.outWidth, opts.outHeight)
        var sample = 1
        while (longer / sample > MAX_IMAGE_EDGE) sample *= 2
        val decodeOpts = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        openStream()?.use { BitmapFactory.decodeStream(it, null, decodeOpts) }
    } catch (_: Throwable) {
        null
    }

    private fun ellipsize(text: String, paint: Paint, maxWidth: Float): String {
        if (paint.measureText(text) <= maxWidth) return text
        val ellipsis = "…"
        var cut = text.length - 1
        while (cut > 0 && paint.measureText(text.substring(0, cut) + ellipsis) > maxWidth) cut--
        return if (cut <= 0) ellipsis else text.substring(0, cut) + ellipsis
    }

    private fun wrapLayout(text: String, paint: TextPaint, maxWidth: Int): StaticLayout =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder
                .obtain(text, 0, text.length, paint, maxWidth)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1.1f)
                .setIncludePad(false)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(text, paint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.1f, 0f, false)
        }

    private fun stamp(): String =
        SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())

    // ── Saving + sharing ───────────────────────────────────────────────────

    private fun save(context: Context, fileName: String, document: PdfDocument): Uri? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveViaMediaStore(context, fileName, document)
        } else {
            saveViaLegacyFile(context, fileName, document)
        }

    private fun saveViaMediaStore(context: Context, fileName: String, document: PdfDocument): Uri? {
        val resolver = context.contentResolver
        val values = android.content.ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), values) ?: return null
        resolver.openOutputStream(uri)?.use { out ->
            document.writeTo(out)
            out.flush()
        }
        return uri
    }

    private fun saveViaLegacyFile(context: Context, fileName: String, document: PdfDocument): Uri? {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        FileOutputStream(file).use { out ->
            document.writeTo(out)
            out.flush()
        }
        return Uri.fromFile(file)
    }

    private fun launchShare(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "RockScout Identification Report")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        SafeLinkOpener.openShareChooser(context, intent, "Share identification report")
    }
}

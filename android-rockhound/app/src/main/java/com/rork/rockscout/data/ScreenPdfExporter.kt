package com.rork.rockscout.data

import android.content.ContentValues
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
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A single renderable item in a generic screen PDF export.
 */
data class ScreenPdfItem(
    val title: String,
    val subtitle: String = "",
    val accentRgb: Int = 0xFFE8A33D.toInt(),
    val imageUrl: String? = null,
    val fields: List<Pair<String, String>> = emptyList(),
    val description: String = "",
)

/**
 * Generic PDF exporter for reference-data screens (dino dictionary, prehistoric
 * organisms, constellations, specimen database, wishlist, RAA, saved images,
 * completed trades, natural wonders, artifacts).
 *
 * Produces a cover page with title + count + contents, then one detail page per
 * item with image, fields, and description. Saved to Downloads via MediaStore
 * (API 29+) or direct file write (older APIs), then opened via share sheet.
 */
object ScreenPdfExporter {

    private const val PAGE_W = 595
    private const val PAGE_H = 842
    private const val MARGIN = 36f
    private const val MAX_IMAGE_EDGE = 4096

    /**
     * Build and save a PDF catalog of [items].
     *
     * @param docTitle  the cover-page title (e.g. "Dinosaur Dictionary").
     * @param fileName  base file name (without extension).
     * @return the saved file [Uri], or null on failure / empty list.
     */
    suspend fun export(
        context: Context,
        docTitle: String,
        fileName: String,
        items: List<ScreenPdfItem>,
        shareAfterSave: Boolean = true,
    ): Uri? {
        if (items.isEmpty()) return null
        val document = PdfDocument()
        return try {
            drawCover(document, docTitle, items)
            items.forEachIndexed { index, item ->
                drawItemPage(context, document, item, index + 1, items.size, docTitle)
            }
            val fullFileName = "${fileName}_${stamp()}.pdf"
            val uri = save(context, fullFileName, document) ?: return null
            if (shareAfterSave) {
                Toast.makeText(
                    context,
                    "PDF saved to Downloads: $fullFileName",
                    Toast.LENGTH_LONG,
                ).show()
                launchShare(context, uri, docTitle)
            }
            uri
        } catch (t: Throwable) {
            null
        } finally {
            document.close()
        }
    }

    // ── Cover page ──────────────────────────────────────────────────────

    private fun drawCover(document: PdfDocument, docTitle: String, items: List<ScreenPdfItem>) {
        val page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, 1).create())
        val canvas = page.canvas
        val accent = items.firstOrNull()?.accentRgb ?: 0xFFE8A33D.toInt()

        // Top accent band
        canvas.drawRect(0f, 0f, PAGE_W.toFloat(), 12f, Paint().apply { color = accent })

        // Title
        val titlePaint = Paint().apply {
            color = Color.BLACK; textSize = 30f; isAntiAlias = true
            isFakeBoldText = true; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(docTitle, MARGIN, 110f, titlePaint)

        // Subtitle
        val subPaint = Paint().apply {
            color = accent; textSize = 16f; isAntiAlias = true; isFakeBoldText = true
        }
        canvas.drawText("RockScout Reference Catalog", MARGIN, 140f, subPaint)

        // Date + count
        val bodyPaint = Paint().apply { color = Color.DKGRAY; textSize = 12f; isAntiAlias = true }
        val dateStr = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault()).format(Date())
        canvas.drawText("Generated $dateStr", MARGIN, 168f, bodyPaint)
        canvas.drawText(
            "${items.size} ${if (items.size == 1) "entry" else "entries"} in this catalog",
            MARGIN, 186f, bodyPaint,
        )

        // Contents list
        val headerPaint = Paint().apply {
            color = accent; textSize = 13f; isAntiAlias = true; isFakeBoldText = true
        }
        val contentsY = 240f
        canvas.drawText("Contents", MARGIN, contentsY, headerPaint)
        canvas.drawLine(MARGIN, contentsY + 6f, PAGE_W - MARGIN, contentsY + 6f,
            Paint().apply { color = Color.LTGRAY })

        val entryPaint = Paint().apply { color = Color.DKGRAY; textSize = 11f; isAntiAlias = true }
        val nameMaxW = PAGE_W - MARGIN * 2 - 28f
        var y = contentsY + 26f
        val maxContentsY = PAGE_H - MARGIN
        items.takeWhile { y < maxContentsY }.forEachIndexed { idx, item ->
            canvas.drawText("${idx + 1}.", MARGIN, y, entryPaint)
            canvas.drawText(ellipsize(item.title, entryPaint, nameMaxW), MARGIN + 24f, y, entryPaint)
            y += 16f
        }

        // Footer
        val footerPaint = Paint().apply {
            color = Color.GRAY; textSize = 9f; isAntiAlias = true; textSkewX = -0.3f
        }
        canvas.drawText("Catalog compiled with RockScout", MARGIN, PAGE_H - MARGIN + 6f, footerPaint)

        document.finishPage(page)
    }

    // ── Item page ───────────────────────────────────────────────────────

    private fun drawItemPage(
        context: Context,
        document: PdfDocument,
        item: ScreenPdfItem,
        index: Int,
        total: Int,
        docTitle: String,
    ) {
        val page = document.startPage(
            PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, index + 1).create()
        )
        val canvas = page.canvas

        // Accent header bar
        canvas.drawRect(0f, 0f, PAGE_W.toFloat(), 6f, Paint().apply { color = item.accentRgb })

        // Title
        val titlePaint = Paint().apply {
            color = Color.BLACK; textSize = 22f; isAntiAlias = true
            isFakeBoldText = true; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(ellipsize(item.title, titlePaint, PAGE_W - MARGIN * 2 - 40f), MARGIN, 48f, titlePaint)

        // Subtitle
        if (item.subtitle.isNotBlank()) {
            val subPaint = Paint().apply {
                color = item.accentRgb; textSize = 12f; isAntiAlias = true; isFakeBoldText = true
            }
            canvas.drawText(ellipsize(item.subtitle, subPaint, PAGE_W - MARGIN * 2), MARGIN, 68f, subPaint)
        }

        // Page counter
        val counterPaint = Paint().apply { color = Color.GRAY; textSize = 9f; isAntiAlias = true }
        val counterText = "$index / $total"
        canvas.drawText(counterText, PAGE_W - MARGIN - counterPaint.measureText(counterText), 68f, counterPaint)

        var y = 90f

        // Image
        val imgMaxW = PAGE_W - MARGIN * 2
        val imgMaxH = 280f
        val bitmap = loadHighResBitmap(context, item.imageUrl)
        if (bitmap != null) {
            val scale = minOf(imgMaxW / bitmap.width, imgMaxH / bitmap.height)
            val drawW = bitmap.width * scale
            val drawH = bitmap.height * scale
            val left = MARGIN + (imgMaxW - drawW) / 2f
            canvas.drawBitmap(bitmap, left, y, null)
            canvas.drawRect(left, y, left + drawW, y + drawH, Paint().apply {
                color = Color.LTGRAY; style = Paint.Style.STROKE; strokeWidth = 1f
            })
            y = y + drawH + 14f
        } else {
            y += 10f
        }

        // Fields
        val labelPaint = Paint().apply {
            color = Color.DKGRAY; textSize = 10f; isAntiAlias = true; isFakeBoldText = true
        }
        val valuePaint = TextPaint().apply { color = Color.BLACK; textSize = 10f; isAntiAlias = true }
        val valueX = MARGIN + 110f
        val valueMaxW = (PAGE_W - valueX - MARGIN).toInt()

        item.fields.forEach { (label, value) ->
            if (value.isBlank()) return@forEach
            val laid = wrapLayout(value, valuePaint, valueMaxW)
            if (y + laid.height > PAGE_H - MARGIN - 20f) return@forEach
            canvas.drawText(label, MARGIN, y + 10f, labelPaint)
            canvas.translate(valueX, y)
            laid.draw(canvas)
            canvas.translate(-valueX, -y)
            y += laid.height + 6f
        }

        // Description
        if (item.description.isNotBlank()) {
            y += 6f
            val descHeaderPaint = Paint().apply {
                color = item.accentRgb; textSize = 11f; isAntiAlias = true; isFakeBoldText = true
            }
            canvas.drawText("Description", MARGIN, y, descHeaderPaint)
            y += 14f
            val descPaint = TextPaint().apply { color = Color.DKGRAY; textSize = 10f; isAntiAlias = true }
            val laid = wrapLayout(item.description, descPaint, imgMaxW.toInt())
            // Truncate overflow to fit remaining space
            if (y + laid.height <= PAGE_H - MARGIN - 20f) {
                canvas.translate(MARGIN, y)
                laid.draw(canvas)
                canvas.translate(-MARGIN, -y)
                y += laid.height + 6f
            } else {
                // Draw what fits
                val availH = (PAGE_H - MARGIN - 20f - y).toInt()
                if (availH > 20) {
                    val partial = wrapLayout(item.description.take(availH * 3), descPaint, imgMaxW.toInt())
                    canvas.translate(MARGIN, y)
                    partial.draw(canvas)
                    canvas.translate(-MARGIN, -y)
                }
            }
        }

        // Footer
        val footerPaint = Paint().apply {
            color = Color.GRAY; textSize = 9f; isAntiAlias = true; textSkewX = -0.3f
        }
        canvas.drawText(
            "RockScout  •  $docTitle  •  $index/$total",
            MARGIN, PAGE_H - MARGIN + 6f, footerPaint,
        )

        document.finishPage(page)
    }

    // ── Bitmap loading ──────────────────────────────────────────────────

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
        val longer = maxOf(opts.outWidth, opts.outHeight)
        var sample = 1
        while (longer / sample > MAX_IMAGE_EDGE) sample *= 2
        openStream()?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply {
                inSampleSize = sample
                inPreferredConfig = Bitmap.Config.ARGB_8888
            })
        }
    } catch (_: Throwable) { null }

    // ── Text helpers ────────────────────────────────────────────────────

    private fun ellipsize(text: String, paint: Paint, maxWidth: Float): String {
        if (paint.measureText(text) <= maxWidth) return text
        val ellipsis = "…"
        var cut = text.length - 1
        while (cut > 0 && paint.measureText(text.substring(0, cut) + ellipsis) > maxWidth) cut--
        return if (cut <= 0) ellipsis else text.substring(0, cut) + ellipsis
    }

    private fun wrapLayout(text: String, paint: TextPaint, maxWidth: Int): StaticLayout =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(text, 0, text.length, paint, maxWidth)
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

    // ── Saving + sharing ────────────────────────────────────────────────

    private fun save(context: Context, fileName: String, document: PdfDocument): Uri? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveViaMediaStore(context, fileName, document)
        } else {
            saveViaLegacyFile(context, fileName, document)
        }

    private fun saveViaMediaStore(context: Context, fileName: String, document: PdfDocument): Uri? {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), values) ?: return null
        resolver.openOutputStream(uri)?.use { document.writeTo(it); it.flush() }
        return uri
    }

    private fun saveViaLegacyFile(context: Context, fileName: String, document: PdfDocument): Uri? {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(dir, fileName)
        FileOutputStream(file).use { document.writeTo(it); it.flush() }
        return Uri.fromFile(file)
    }

    private fun launchShare(context: Context, uri: Uri, docTitle: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, docTitle)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        SafeLinkOpener.openShareChooser(context, intent, "Share $docTitle PDF")
    }
}

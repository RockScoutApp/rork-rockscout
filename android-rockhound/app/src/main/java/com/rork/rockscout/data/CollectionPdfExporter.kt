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
 * Exports the user's entire rock collection as a formatted PDF document.
 *
 * The PDF is laid out in portrait A4 (595 x 842 pt) and contains:
 *  - A cover page with the hunter's name, generation date, an optional date-
 *    range filter summary, and a per-specimen contents list with dates.
 *  - One detail page per collected specimen, with a high-resolution
 *    representative photograph (up to 4K on the longest edge, when available)
 *    and the full reference details (class, category, tagline, hardness,
 *    luster, streak, crystal system, formula, common colors, typical
 *    localities, rarity, the date added/captured, GPS latitude / longitude
 *    (field captures with coordinates, formatted to 6 decimal places for
 *    ~10 cm field-reporting accuracy), plus the user's note and "found at"
 *    location).
 *
 * Sources mirror [CollectionCsvExporter]: both [CollectionEntry] database
 * specimens and [CapturedPhoto] field captures promoted to My Rocks are
 * included. Images are fetched at full resolution off the main thread; the
 * whole export runs on [Dispatchers.IO] and the resulting file is saved to
 * the public Downloads folder (via [MediaStore] on API 29+, direct file
 * write on older APIs) and optionally surfaced through the system share
 * sheet.
 *
 * An optional date range (`startMillis`..`endMillis`, inclusive, in epoch
 * UTC) may be supplied to restrict the export to specimens whose capture /
 * add timestamp falls within that window. Field captures use
 * [CapturedPhoto.timestamp]; database specimens use [CollectionEntry.addedAt]
 * (entries with `addedAt == 0` — created before this field existed — are
 * treated as "date unknown" and only included when no filter is applied).
 * When both bounds are null the entire collection is exported.
 */
object CollectionPdfExporter {

    private const val PAGE_W = 595
    private const val PAGE_H = 842
    private const val MARGIN = 36f

    /** A single renderable item in the PDF — resolved from either a
     *  [CollectionEntry] (database specimen) or a [CapturedPhoto] (field capture). */
    private data class PdfItem(
        val title: String,
        val subtitle: String,
        val accentRgb: Int,
        val imageUrl: String?,
        val fields: List<Pair<String, String>>,
        val note: String,
        val foundAt: String,
        val dateLabel: String?,        // formatted "Date Added" / "Date Captured", null when unknown
    )

    /**
     * Build and save the collection PDF. Safe to call from a coroutine — all
     * bitmap and file I/O happens on [Dispatchers.IO].
     *
     * @param hunterName display name for the cover page header.
     * @return the saved file [Uri], or null on failure.
     */
    suspend fun export(
        context: Context,
        hunterName: String,
        collection: List<CollectionEntry>,
        captures: List<CapturedPhoto>,
        shareAfterSave: Boolean = true,
        startMillis: Long? = null,
        endMillis: Long? = null,
    ): Uri? = withContext(Dispatchers.IO) {
        val (filteredCollection, filteredCaptures) = applyDateFilter(
            collection, captures, startMillis, endMillis,
        )
        val items = buildItems(filteredCollection, filteredCaptures)
        if (items.isEmpty()) return@withContext null

        val document = PdfDocument()
        try {
            drawCover(document, hunterName, items, startMillis, endMillis)
            items.forEachIndexed { index, item ->
                drawItemPage(context, document, item, index + 1, items.size)
            }

            val fileName = "RockScout_Collection_${stamp()}.pdf"
            val uri = save(context, fileName, document) ?: return@withContext null
            if (shareAfterSave) {
                withContext(Dispatchers.Main) {
                    // Tell the user the file was saved, then immediately open
                    // the system share sheet so they can send it onward. The
                    // toast must run on the main thread (Toast.show() requires
                    // a Looper thread); running it on Dispatchers.IO used to
                    // throw and silently swallow the share intent.
                    Toast.makeText(
                        context,
                        "PDF saved to Downloads: $fileName",
                        Toast.LENGTH_LONG,
                    ).show()
                    launchShare(context, uri, hunterName)
                }
            }
            uri
        } catch (t: Throwable) {
            null
        } finally {
            document.close()
        }
    }

    // ── Date-range filtering ────────────────────────────────────────────

    /** Mirrors [CollectionZipExporter.applyDateFilter]: entries with a zero /
     *  missing timestamp are excluded when a filter is set so the user gets a
     *  predictable, honest date slice; when no filter is set everything is
     *  included. */
    private fun applyDateFilter(
        collection: List<CollectionEntry>,
        captures: List<CapturedPhoto>,
        startMillis: Long?,
        endMillis: Long?,
    ): Pair<List<CollectionEntry>, List<CapturedPhoto>> {
        if (startMillis == null && endMillis == null) {
            return collection to captures.filter { it.inCollection }
        }
        val start = startMillis ?: 0L
        val endRaw = endMillis ?: Long.MAX_VALUE
        val end = if (endRaw < Long.MAX_VALUE) endRaw + 24 * 60 * 60 * 1000L - 1 else endRaw
        val inCollection = collection.filter { it.addedAt != 0L && it.addedAt in start..end }
        val inCaptures = captures.filter { it.inCollection && it.timestamp != 0L && it.timestamp in start..end }
        return inCollection to inCaptures
    }

    // ── Content resolution ───────────────────────────────────────────────

    private fun buildItems(
        collection: List<CollectionEntry>,
        captures: List<CapturedPhoto>,
    ): List<PdfItem> {
        val items = mutableListOf<PdfItem>()

        collection.forEach { entry ->
            val spec = SeedData.specimenById(entry.specimenId) ?: return@forEach
            val imageUrls = SpecimenImages.urls[spec.id] ?: spec.imageUrls
            items += PdfItem(
                title = spec.name,
                subtitle = "${spec.rockClass.label}  •  ${spec.category}  •  ${spec.rarity}",
                accentRgb = accentForClass(spec.rockClass),
                imageUrl = imageUrls.firstOrNull(),
                fields = listOf(
                    "Hardness" to spec.hardness,
                    "Luster" to spec.luster,
                    "Streak" to spec.streak,
                    "Crystal System" to spec.crystalSystem,
                    "Chemical Formula" to spec.chemicalFormula,
                    "Common Colors" to spec.commonColors.joinToString(", "),
                    "Typical Localities" to spec.whereFound.joinToString("; "),
                ).filter { it.second.isNotBlank() },
                note = entry.note,
                foundAt = entry.foundAt,
                dateLabel = if (entry.addedAt != 0L)
                    SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date(entry.addedAt)) else null,
            )
        }

        captures.filter { it.inCollection }.forEach { cap ->
            val spec = SeedData.specimenById(cap.specimenId)
            val imageUri = cap.imageUris.firstOrNull()
            items += PdfItem(
                title = cap.displayName(spec?.name ?: "Unknown specimen"),
                subtitle = "${spec?.rockClass?.label ?: "Field Capture"}  •  ${spec?.category ?: cap.specimenEmoji}  •  Field Capture",
                accentRgb = if (spec != null) accentForClass(spec.rockClass) else 0xFFE8A33D.toInt(),
                imageUrl = imageUri,
                fields = listOfNotNull(
                    if (cap.customLocation.isNotBlank()) "Found At" to cap.customLocation else null,
                    cap.latitude?.let { "Latitude" to "%.6f".format(it) },
                    cap.longitude?.let { "Longitude" to "%.6f".format(it) },
                    spec?.let { "Reference Class" to "${it.rockClass.label} — ${it.category}" },
                    spec?.takeIf { it.hardness.isNotBlank() }?.let { "Hardness" to it.hardness },
                    spec?.takeIf { it.chemicalFormula.isNotBlank() }?.let { "Chemical Formula" to it.chemicalFormula },
                    spec?.takeIf { it.whereFound.isNotEmpty() }
                        ?.let { "Typical Localities" to it.whereFound.joinToString("; ") },
                ),
                note = cap.generalInfo,
                foundAt = cap.customLocation,
                dateLabel = if (cap.timestamp > 0)
                    SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date(cap.timestamp)) else null,
            )
        }

        return items
    }

    private fun accentForClass(rockClass: RockClass): Int = when (rockClass) {
        RockClass.IGNEOUS -> 0xFFE5683C.toInt()
        RockClass.SEDIMENTARY -> 0xFFD9B26A.toInt()
        RockClass.METAMORPHIC -> 0xFF6FA8C7.toInt()
        RockClass.MINERAL -> 0xFFE8A33D.toInt()
        RockClass.CRYSTAL -> 0xFF9B7BD8.toInt()
        RockClass.FOSSIL -> 0xFFC9A87C.toInt()
    }

    // ── Drawing ──────────────────────────────────────────────────────────

    private fun drawCover(
        document: PdfDocument,
        hunterName: String,
        items: List<PdfItem>,
        startMillis: Long?,
        endMillis: Long?,
    ) {
        val page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, 1).create())
        val canvas = page.canvas
        val accent = 0xFFE8A33D.toInt()

        // Top accent band
        val bandPaint = Paint().apply { color = accent }
        canvas.drawRect(0f, 0f, PAGE_W.toFloat(), 12f, bandPaint)

        // Title
        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 30f
            isAntiAlias = true
            isFakeBoldText = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val title = "My Rock Collection"
        canvas.drawText(title, MARGIN, 110f, titlePaint)

        // Hunter name
        val subPaint = Paint().apply {
            color = accent
            textSize = 16f
            isAntiAlias = true
            isFakeBoldText = true
        }
        canvas.drawText(hunterName, MARGIN, 140f, subPaint)

        // Date + count
        val bodyPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 12f
            isAntiAlias = true
        }
        val dateStr = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault()).format(Date())
        canvas.drawText("Generated $dateStr", MARGIN, 168f, bodyPaint)
        canvas.drawText("${items.size} specimen${if (items.size > 1) "s" else ""} in this catalog", MARGIN, 186f, bodyPaint)

        // Optional date-range filter summary line.
        var rangeY = 204f
        if (startMillis != null || endMillis != null) {
            val df = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            val rangeText = when {
                startMillis != null && endMillis != null ->
                    "Date range: ${df.format(Date(startMillis))} – ${df.format(Date(endMillis))}"
                startMillis != null -> "Date range: from ${df.format(Date(startMillis))}"
                else -> "Date range: up to ${df.format(Date(endMillis!!))}"
            }
            val rangePaint = Paint().apply {
                color = accent
                textSize = 11f
                isAntiAlias = true
                isFakeBoldText = true
            }
            canvas.drawText(rangeText, MARGIN, rangeY, rangePaint)
            rangeY += 18f
        }

        // Contents list
        val headerPaint = Paint().apply {
            color = accent
            textSize = 13f
            isAntiAlias = true
            isFakeBoldText = true
        }
        val contentsHeaderY = (rangeY + 26f).coerceAtLeast(240f)
        canvas.drawText("Contents", MARGIN, contentsHeaderY, headerPaint)
        val linePaint = Paint().apply { color = Color.LTGRAY }
        canvas.drawLine(MARGIN, contentsHeaderY + 6f, PAGE_W - MARGIN, contentsHeaderY + 6f, linePaint)

        val entryPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 11f
            isAntiAlias = true
        }
        val dateColPaint = Paint().apply {
            color = Color.GRAY
            textSize = 9f
            isAntiAlias = true
        }
        var y = contentsHeaderY + 26f
        val maxContentsY = PAGE_H - MARGIN
        val nameMaxW = PAGE_W - MARGIN * 2 - 28f - 90f
        items.takeWhile { y < maxContentsY }.forEachIndexed { idx, item ->
            val numStr = "${idx + 1}."
            canvas.drawText(numStr, MARGIN, y, entryPaint)
            val name = ellipsize(item.title, entryPaint, nameMaxW)
            canvas.drawText(name, MARGIN + 24f, y, entryPaint)
            item.dateLabel?.let { d ->
                val dateStr = ellipsize(d, dateColPaint, 86f)
                canvas.drawText(dateStr, PAGE_W - MARGIN - dateColPaint.measureText(dateStr), y, dateColPaint)
            }
            y += 16f
        }

        // Footer
        val footerPaint = Paint().apply {
            color = Color.GRAY
            textSize = 9f
            isAntiAlias = true
            textSkewX = -0.3f
        }
        canvas.drawText("Catalog compiled with RockScout", MARGIN, PAGE_H - MARGIN + 6f, footerPaint)

        document.finishPage(page)
    }

    private fun drawItemPage(
        context: Context,
        document: PdfDocument,
        item: PdfItem,
        index: Int,
        total: Int,
    ) {
        val page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, index + 1).create())
        val canvas = page.canvas

        // Accent header bar
        val barPaint = Paint().apply { color = item.accentRgb }
        canvas.drawRect(0f, 0f, PAGE_W.toFloat(), 6f, barPaint)

        // Title
        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 22f
            isAntiAlias = true
            isFakeBoldText = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val title = ellipsize(item.title, titlePaint, PAGE_W - MARGIN * 2 - 40f)
        canvas.drawText(title, MARGIN, 48f, titlePaint)

        // Subtitle (class • category • rarity)
        val subPaint = Paint().apply {
            color = item.accentRgb
            textSize = 12f
            isAntiAlias = true
            isFakeBoldText = true
        }
        canvas.drawText(ellipsize(item.subtitle, subPaint, PAGE_W - MARGIN * 2), MARGIN, 68f, subPaint)

        // Page counter (top-right) — also shows the specimen's date when known.
        val counterPaint = Paint().apply {
            color = Color.GRAY
            textSize = 9f
            isAntiAlias = true
        }
        val counterText = if (item.dateLabel != null) "$index / $total  •  ${item.dateLabel}" else "$index / $total"
        canvas.drawText(counterText, PAGE_W - MARGIN - counterPaint.measureText(counterText), 68f, counterPaint)

        var y = 90f

        // Representative image (top portion of page) — loaded at high
        // resolution (up to 4K on the longest edge) so the embedded photo
        // stays crisp even when printed or zoomed. The drawn size is still
        // bounded by the page area below.
        val imgMaxW = PAGE_W - MARGIN * 2
        val imgMaxH = 280f
        val bitmap = loadHighResBitmap(context, item.imageUrl)
        if (bitmap != null) {
            val scale = minOf(imgMaxW / bitmap.width, imgMaxH / bitmap.height)
            val drawW = bitmap.width * scale
            val drawH = bitmap.height * scale
            val left = MARGIN + (imgMaxW - drawW) / 2f
            val top = y
            canvas.drawBitmap(bitmap, left, top, null)
            // Subtle frame
            val framePaint = Paint().apply {
                color = Color.LTGRAY
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }
            canvas.drawRect(left, top, left + drawW, top + drawH, framePaint)
            y = top + drawH + 14f
        } else {
            // Placeholder box
            val placePaint = Paint().apply { color = 0xFFEFEBE3.toInt() }
            canvas.drawRect(MARGIN, y, MARGIN + imgMaxW, y + 120f, placePaint)
            val capPaint = Paint().apply {
                color = Color.GRAY
                textSize = 11f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("No photo available", PAGE_W / 2f, y + 66f, capPaint)
            y += 134f
        }

        // Fields (two-column key / value list)
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
        val labelX = MARGIN
        val valueX = MARGIN + 110f
        val valueMaxW = PAGE_W - valueX - MARGIN

        item.fields.forEach { (label, value) ->
            val laid = wrapLayout(value, valuePaint, valueMaxW.toInt())
            val blockHeight = laid.height.toFloat()
            if (y + blockHeight > PAGE_H - MARGIN - 20f) return@forEach  // skip overflow silently
            canvas.drawText(label, labelX, y + 10f, labelPaint)
            canvas.translate(valueX, y)
            laid.draw(canvas)
            canvas.translate(-valueX, -y)
            y += blockHeight + 6f
        }

        // Note (user's personal note)
        if (item.note.isNotBlank()) {
            y += 6f
            val noteHeaderPaint = Paint().apply {
                color = item.accentRgb
                textSize = 11f
                isAntiAlias = true
                isFakeBoldText = true
            }
            canvas.drawText("Note", MARGIN, y, noteHeaderPaint)
            y += 14f
            val notePaint = TextPaint().apply {
                color = Color.DKGRAY
                textSize = 10f
                isAntiAlias = true
            }
            val laid = wrapLayout(item.note, notePaint, imgMaxW.toInt())
            canvas.translate(MARGIN, y)
            laid.draw(canvas)
            canvas.translate(-MARGIN, -y)
            y += laid.height + 6f
        }

        // Footer
        val footerPaint = Paint().apply {
            color = Color.GRAY
            textSize = 9f
            isAntiAlias = true
            textSkewX = -0.3f
        }
        canvas.drawText("RockScout Collection  •  $title", MARGIN, PAGE_H - MARGIN + 6f, footerPaint)

        document.finishPage(page)
    }

    // ── Bitmap loading ───────────────────────────────────────────────────

    /** Maximum edge length (px) for an embedded photograph. Anything larger
     *  than this is downsampled to keep memory bounded; anything smaller is
     *  embedded at native resolution. 4096 = "4K". */
    private const val MAX_IMAGE_EDGE = 4096

    /** Load a high-resolution bitmap (up to [MAX_IMAGE_EDGE] px on the
     *  longest edge) from a remote URL or content/file URI. The returned
     *  bitmap is the full-resolution source — it is only downsampled when the
     *  original exceeds 4K, so the PDF embeds a genuine 4K photograph. */
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
        // Only downsample if the source exceeds 4K; this keeps sub-4K photos
        // at full resolution and caps huge images at 4K to bound memory.
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

    // ── Text helpers ─────────────────────────────────────────────────────

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

    // ── Saving + sharing ─────────────────────────────────────────────────

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

    private fun launchShare(context: Context, uri: Uri, hunterName: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "$hunterName's Rock Collection")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        SafeLinkOpener.openShareChooser(context, intent, "Share collection PDF")
    }
}

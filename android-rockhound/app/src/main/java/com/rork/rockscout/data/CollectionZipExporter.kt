package com.rork.rockscout.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStreamWriter
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Exports the user's entire rock collection as a single ZIP archive containing
 * both the data (CSV) and the full-resolution 4K specimen photographs.
 *
 * The archive contains one top-level folder `RockScout_Collection_<timestamp>/`
 * with:
 *  - `collection.csv` — the same RFC 4180 + UTF-8 BOM CSV produced by
 *    [CollectionCsvExporter.buildCsv], with one extra trailing column
 *    `Image File` holding the relative path to the specimen's photo inside the
 *    archive (e.g. `images/Amethyst.png`), or blank when no image is available.
 *  - `images/` — the original 4K source photo for every specimen that has one,
 *    named `<sanitized-specimen-name>.<ext>` with a numeric suffix on collision.
 *
 * Both [CollectionEntry] database specimens (R2 CDN URLs) and [CapturedPhoto]
 * field captures promoted to My Rocks (content/file URIs) are included. All
 * network and file I/O runs on [Dispatchers.IO]; the resulting zip is shared
 * through [FileProvider] via the system share sheet.
 *
 * An optional date range (`startMillis`..`endMillis`, inclusive, in epoch UTC)
 * may be supplied to restrict the export to specimens whose capture / add
 * timestamp falls within that window. Field captures use [CapturedPhoto.timestamp];
 * database specimens use [CollectionEntry.addedAt] (entries with `addedAt == 0`
 * — created before this field existed — are treated as "date unknown" and only
 * included when no filter is applied). When both bounds are null the entire
 * collection is exported, matching the previous behaviour.
 */
object CollectionZipExporter {

    /** Trailing column appended to the CSV produced by [CollectionCsvExporter.buildCsv]. */
    private const val IMAGE_FILE_COLUMN = "Image File"

    /**
     * Build the zip, write it to the app's cache dir, and launch the system
     * share sheet. Safe to call from a coroutine — all I/O is on
     * [Dispatchers.IO].
     *
     * @param startMillis optional inclusive lower bound (epoch UTC millis).
     *   Pass `null` for no lower bound.
     * @param endMillis optional inclusive upper bound (epoch UTC millis). The
     *   upper bound is treated as end-of-day so a same-day range includes the
     *   whole day. Pass `null` for no upper bound.
     * @return true if the share sheet was launched; false on fatal failure or
     *   when the filtered set is empty.
     */
    suspend fun exportAndShare(
        context: Context,
        collection: List<CollectionEntry>,
        captures: List<CapturedPhoto>,
        startMillis: Long? = null,
        endMillis: Long? = null,
    ): Boolean = withContext(Dispatchers.IO) {
        val stamp = stamp()
        val folderName = "RockScout_Collection_$stamp"
        val stagingRoot = File(context.cacheDir, "zip_staging").apply { mkdirs() }
        val folder = File(stagingRoot, folderName)
        val imagesDir = File(folder, "images")

        try {
            if (!imagesDir.exists() && !imagesDir.mkdirs()) return@withContext false

            // 0. Apply the optional date-range filter (inclusive on both ends).
            val (filteredCollection, filteredCaptures) = applyDateFilter(
                collection, captures, startMillis, endMillis,
            )
            if (filteredCollection.isEmpty() && filteredCaptures.isEmpty()) {
                return@withContext false
            }

            // 1. Resolve image entries (one per specimen, full-resolution source).
            val entries = resolveEntries(filteredCollection, filteredCaptures)

            // 2. Download / copy each image into images/, tracking the relative path
            //    that will be written into the new `Image File` column of the CSV.
            val usedNames = mutableSetOf<String>()
            val imagePaths = entries.map { item ->
                try {
                    val safeBase = sanitizeFileName(item.displayName)
                    val ext = guessExtension(item.imageUrl)
                    var name = "$safeBase.$ext"
                    var n = 1
                    while (!usedNames.add(name)) {
                        name = "${safeBase}_${n}.${ext}"
                        n++
                    }
                    val target = File(imagesDir, name)
                    if (copyFullResolution(context, item.imageUrl, target)) "images/$name" else ""
                } catch (_: Throwable) {
                    ""
                }
            }

            // 3. Build the CSV (base rows from the existing exporter, + Image File column).
            val csv = appendImageColumn(
                CollectionCsvExporter.buildCsv(filteredCollection, filteredCaptures),
                imagePaths,
            )
            val csvFile = File(folder, "collection.csv")
            OutputStreamWriter(FileOutputStream(csvFile), Charsets.UTF_8).use { it.write(csv) }

            // 4. Zip the folder into cacheDir/zip_exports/RockScout_Collection_<stamp>.zip.
            val zipDir = File(context.cacheDir, "zip_exports").apply { mkdirs() }
            val zipFile = File(zipDir, "$folderName.zip")
            if (!writeZip(zipFile, folder, folderName)) return@withContext false

            val uri = uriFor(context, zipFile) ?: return@withContext false
            withContext(Dispatchers.Main) {
                // Let the user know the archive is ready, then immediately
                // open the system share sheet so they can send it to another
                // app. The toast runs on the main thread (Toast.show() needs
                // a Looper thread); the share chooser opens right after.
                android.widget.Toast.makeText(
                    context,
                    "Collection ZIP ready — choose where to send it.",
                    android.widget.Toast.LENGTH_SHORT,
                ).show()
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/zip"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "My RockScout Collection")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                SafeLinkOpener.openShareChooser(context, shareIntent, "Export collection ZIP")
            }
            true
        } catch (_: Throwable) {
            false
        } finally {
            // Clean up staging regardless of outcome; the zip itself lives until share.
            stagingRoot.deleteRecursively()
        }
    }

    // ── Date-range filtering ────────────────────────────────────────────

    /** Returns the subset of the collection + captures whose timestamp falls
     *  within `[start, end]` inclusive. Entries with a zero/missing timestamp
     *  (pre-existing rows created before `addedAt` shipped) are excluded when a
     *  filter is set so the user gets a predictable, honest date slice. */
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
        // Treat end as end-of-day (23:59:59.999) so a same-day range still covers
        // captures taken later that day.
        val endRaw = endMillis ?: Long.MAX_VALUE
        val end = if (endRaw < Long.MAX_VALUE) endRaw + 24 * 60 * 60 * 1000L - 1 else endRaw
        val inCollection = collection.filter { entry ->
            entry.addedAt != 0L && entry.addedAt in start..end
        }
        val inCaptures = captures.filter { cap ->
            cap.inCollection && cap.timestamp != 0L && cap.timestamp in start..end
        }
        return inCollection to inCaptures
    }

    // ── Entry resolution ─────────────────────────────────────────────────

    /** One renderable image per specimen — mirrors the PDF exporter's source logic. */
    private data class ZipEntryItem(
        val displayName: String,
        val imageUrl: String?,
    )

    private fun resolveEntries(
        collection: List<CollectionEntry>,
        captures: List<CapturedPhoto>,
    ): List<ZipEntryItem> {
        val items = mutableListOf<ZipEntryItem>()
        collection.forEach { entry ->
            val spec = SeedData.specimenById(entry.specimenId) ?: return@forEach
            val imageUrls = SpecimenImages.urls[spec.id] ?: spec.imageUrls
            items += ZipEntryItem(
                displayName = spec.name,
                imageUrl = imageUrls.firstOrNull(),
            )
        }
        captures.filter { it.inCollection }.forEach { cap ->
            val spec = SeedData.specimenById(cap.specimenId)
            items += ZipEntryItem(
                displayName = cap.displayName(spec?.name ?: "Unknown specimen"),
                imageUrl = cap.imageUris.firstOrNull(),
            )
        }
        return items
    }

    // ── CSV column appending ─────────────────────────────────────────────

    /**
     * The base CSV already ends with a trailing comma per row? No — it has a
     * fixed column count. We split rows on newlines, append a comma + escaped
     * image path to each row (header + data), and return the new CSV text.
     * Blank image paths become empty trailing fields so column count stays
     * consistent.
     */
    private fun appendImageColumn(baseCsv: String, imagePaths: List<String>): String {
        val lines = baseCsv.split("\r\n").toMutableList()
        // Drop trailing empty line(s) produced by the trailing CRLF.
        while (lines.isNotEmpty() && lines.last().isEmpty()) lines.removeAt(lines.lastIndex)

        val out = StringBuilder()
        // Header
        out.append(lines.first()).append(",").append(escape(IMAGE_FILE_COLUMN)).append("\r\n")
        // Data rows
        for (i in 1 until lines.size) {
            val path = imagePaths.getOrNull(i - 1) ?: ""
            out.append(lines[i]).append(",").append(escape(path)).append("\r\n")
        }
        return out.toString()
    }

    /** RFC 4180 field escaping — mirrors [CollectionCsvExporter] semantics. */
    private fun escape(value: String): String {
        if (value.isEmpty()) return ""
        val needsQuoting = value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        return if (needsQuoting) "\"" + value.replace("\"", "\"\"") + "\"" else value
    }

    // ── Image copy (full resolution, no downsampling) ────────────────────

    private fun copyFullResolution(context: Context, url: String?, target: File): Boolean {
        if (url.isNullOrBlank()) return false
        val uri = Uri.parse(url)
        val scheme = uri.scheme
        val openStream: () -> InputStream? = {
            when (scheme) {
                "http", "https" -> URL(url).openStream()
                else -> context.contentResolver.openInputStream(uri)
            }
        }
        return try {
            openStream()?.use { input ->
                FileOutputStream(target).use { output ->
                    input.copyTo(output, bufferSize = 64 * 1024)
                    output.flush()
                }
            }
            target.exists() && target.length() > 0
        } catch (_: Throwable) {
            // Clean up a partial file so a failed download doesn't leave a corrupt entry.
            target.delete()
            false
        }
    }

    // ── Zip writing ──────────────────────────────────────────────────────

    /** Stream every file under [root] into [zipFile], preserving the [folderName]
     *  as the top-level directory entry inside the archive. */
    private fun writeZip(zipFile: File, root: File, folderName: String): Boolean = try {
        FileOutputStream(zipFile).use { fos ->
            ZipOutputStream(fos).use { zos ->
                root.walkTopDown().forEach { file ->
                    if (file == root) return@forEach
                    val rel = file.relativeTo(root).path.replace(File.separatorChar, '/')
                    val entryName = "$folderName/$rel"
                    if (file.isDirectory) {
                        zos.putNextEntry(ZipEntry("$entryName/"))
                        zos.closeEntry()
                    } else {
                        zos.putNextEntry(ZipEntry(entryName))
                        file.inputStream().use { it.copyTo(zos, bufferSize = 64 * 1024) }
                        zos.closeEntry()
                    }
                }
            }
        }
        true
    } catch (_: Throwable) {
        false
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private fun sanitizeFileName(name: String): String {
        val trimmed = name.trim().ifBlank { "specimen" }
        val cleaned = trimmed.replace(Regex("[^A-Za-z0-9._-]"), "_")
        // Collapse repeated underscores and strip leading/trailing dots/underscores.
        return cleaned.replace(Regex("_+"), "_").trim('_', '.').ifBlank { "specimen" }
    }

    private fun guessExtension(url: String?): String {
        if (url.isNullOrBlank()) return "png"
        val lower = url.substringBefore('?').lowercase(Locale.US)
        return when {
            lower.endsWith(".jpg") || lower.endsWith(".jpeg") -> "jpg"
            lower.endsWith(".png") -> "png"
            lower.endsWith(".webp") -> "webp"
            lower.endsWith(".gif") -> "gif"
            else -> "png"
        }
    }

    private fun stamp(): String =
        SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())

    private fun uriFor(context: Context, file: File): Uri? = try {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
    } catch (_: Throwable) {
        null
    }
}

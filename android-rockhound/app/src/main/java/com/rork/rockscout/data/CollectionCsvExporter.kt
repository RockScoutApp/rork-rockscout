package com.rork.rockscout.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.rork.rockscout.data.SafeLinkOpener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Exports the user's collected rocks (both [CollectionEntry] database specimens and
 * [CapturedPhoto] field captures promoted to My Rocks) as a CSV file for backup and
 * external analysis.
 *
 * The file is written to the app's cache dir and shared via [FileProvider] through
 * [Intent.ACTION_SEND], so the user can save it to disk, email it to themselves,
 * or open it in any spreadsheet app.
 *
 * CSV follows RFC 4180: fields containing commas, quotes, or newlines are wrapped in
 * double quotes and embedded quotes are doubled. A UTF-8 BOM is prepended so Excel
 * on Windows opens the file with the correct character encoding.
 */
object CollectionCsvExporter {

    /** Header row — column order is stable so external analysis scripts can rely on it. */
    private val HEADER = listOf(
        "Specimen ID",
        "Name",
        "Rock Class",
        "Category",
        "Rarity",
        "Hardness (Mohs)",
        "Luster",
        "Streak",
        "Crystal System",
        "Chemical Formula",
        "Common Colors",
        "Typical Localities",
        "Note",
        "Found At",
        "Source",
        "Date Captured",
        "Latitude",
        "Longitude",
    )

    /**
     * Build the CSV text from the given collection entries and field captures that
     * have been promoted to My Rocks. Pure function — no I/O — so it can be unit
     * tested and run on a background dispatcher.
     *
     * @param collection database specimens the user has collected.
     * @param captures all field captures; only those with [CapturedPhoto.inCollection]
     *   true are exported.
     */
    fun buildCsv(
        collection: List<CollectionEntry>,
        captures: List<CapturedPhoto>,
    ): String {
        val sb = StringBuilder()
        // UTF-8 BOM — Excel on Windows mis-detects encoding without it.
        sb.append("\uFEFF")
        sb.append(HEADER.joinToString(",") { escape(it) })
        sb.append("\r\n")

        // Database specimens — one row per collected entry.
        collection.forEach { entry ->
            val spec = SeedData.specimenById(entry.specimenId)
            val row = listOf(
                entry.specimenId,
                spec?.name ?: entry.specimenId,
                spec?.rockClass?.label ?: "",
                spec?.category ?: "",
                spec?.rarity ?: "",
                spec?.hardness ?: "",
                spec?.luster ?: "",
                spec?.streak ?: "",
                spec?.crystalSystem ?: "",
                spec?.chemicalFormula ?: "",
                spec?.commonColors?.joinToString("; ") ?: "",
                spec?.whereFound?.joinToString("; ") ?: "",
                entry.note,
                entry.foundAt,
                "Database",
                "",
                "",
                "",
            )
            sb.append(row.joinToString(",") { escape(it) })
            sb.append("\r\n")
        }

        // Field captures promoted to My Rocks — one row each.
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        captures.filter { it.inCollection }.forEach { cap ->
            val spec = SeedData.specimenById(cap.specimenId)
            val row = listOf(
                cap.specimenId,
                cap.displayName(spec?.name ?: "Unknown specimen"),
                spec?.rockClass?.label ?: "Field Capture",
                spec?.category ?: "",
                spec?.rarity ?: "",
                spec?.hardness ?: "",
                spec?.luster ?: "",
                spec?.streak ?: "",
                spec?.crystalSystem ?: "",
                spec?.chemicalFormula ?: "",
                spec?.commonColors?.joinToString("; ") ?: "",
                spec?.whereFound?.joinToString("; ") ?: "",
                cap.generalInfo,
                cap.customLocation,
                "Field Capture",
                if (cap.timestamp > 0) df.format(Date(cap.timestamp)) else "",
                cap.latitude?.let { "%.6f".format(it) } ?: "",
                cap.longitude?.let { "%.6f".format(it) } ?: "",
            )
            sb.append(row.joinToString(",") { escape(it) })
            sb.append("\r\n")
        }

        return sb.toString()
    }

    /**
     * Build the CSV, write it to the app's cache dir, and launch the system share
     * sheet so the user can save or send the file. Safe to call from a coroutine —
     * all file I/O is dispatched to [Dispatchers.IO].
     *
     * @return true if the share sheet was launched; false if writing failed.
     */
    suspend fun exportAndShare(
        context: Context,
        collection: List<CollectionEntry>,
        captures: List<CapturedPhoto>,
    ): Boolean = withContext(Dispatchers.IO) {
        val csv = buildCsv(collection, captures)
        val fileName = "rockscout_collection_${System.currentTimeMillis()}.csv"
        val file = writeCacheFile(context, csv, fileName) ?: return@withContext false
        val uri = uriFor(context, file) ?: return@withContext false
        withContext(Dispatchers.Main) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "My RockScout Collection")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            SafeLinkOpener.openShareChooser(context, shareIntent, "Export collection CSV")
        }
        true
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    /** RFC 4180 field escaping — wrap in quotes if the value contains comma, quote,
     *  newline, or carriage return, and double any embedded quotes. */
    private fun escape(value: String): String {
        if (value.isEmpty()) return ""
        val needsQuoting = value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        return if (needsQuoting) "\"" + value.replace("\"", "\"\"") + "\"" else value
    }

    private fun writeCacheFile(context: Context, content: String, fileName: String): File? {
        return try {
            val dir = File(context.cacheDir, "csv_exports").apply { mkdirs() }
            val file = File(dir, fileName)
            OutputStreamWriter(FileOutputStream(file), Charsets.UTF_8).use { writer ->
                writer.write(content)
            }
            file
        } catch (_: Throwable) {
            null
        }
    }

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

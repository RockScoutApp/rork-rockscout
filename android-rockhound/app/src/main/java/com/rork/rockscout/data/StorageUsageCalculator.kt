package com.rork.rockscout.data

import android.content.Context
import org.osmdroid.config.Configuration
import java.io.File

/**
 * Computes on-device storage used by RockScout's persisted media:
 * cached map tiles (osmdroid SQLite + loose tile files) and user-uploaded
 * rock / specimen / field-capture / community images stored under the app's
 * internal files directory.
 *
 * All work is best-effort: missing directories contribute 0 bytes and any
 * I/O error is swallowed, so this never crashes the caller.
 */
object StorageUsageCalculator {

    /**
     * Subdirectories under [Context.filesDir] that hold user-uploaded images.
     * Each represents a distinct upload surface in the app:
     *  - specimen_submissions: specimen submission form photos
     *  - capture_images: identify-camera captures and "Add to Specimen" picks
     *  - journal_photos: field journal entry photos
     *  - trade_listings: trade board listing photos
     *  - community_posts: community post photos
     *  - comment_images: images attached to comments / replies
     *  - message_images: private message attachments
     *  - location_submissions: dig site / location submission photos
     *  - backgrounds: user-chosen profile background images
     */
    private val USER_IMAGE_SUBDIRS = listOf(
        "specimen_submissions",
        "capture_images",
        "journal_photos",
        "trade_listings",
        "community_posts",
        "comment_images",
        "message_images",
        "location_submissions",
        "backgrounds",
    )

    /** Result of a storage usage scan. All sizes are in bytes. */
    data class Usage(
        val mapTilesBytes: Long,
        val uploadedImagesBytes: Long,
    ) {
        /** Total on-device storage used by the tracked caches. */
        val totalBytes: Long get() = mapTilesBytes + uploadedImagesBytes
    }

    /**
     * Synchronously walk the cache directories and sum file sizes.
     * Call from a background thread — directory walks may be slow when the
     * tile cache or image folders contain many files.
     */
    fun compute(context: Context): Usage {
        val mapTiles = computeMapTileBytes(context)
        val uploadedImages = computeUploadedImageBytes(context)
        return Usage(mapTilesBytes = mapTiles, uploadedImagesBytes = uploadedImages)
    }

    /**
     * Size in bytes of the osmdroid tile cache: the SQLite cache database
     * (cache.sqlite / cache.db), its -wal / -shm sidecars, and any loose
     * tile files under the configured tile cache directory.
     */
    private fun computeMapTileBytes(context: Context): Long {
        var total = 0L
        runCatching {
            val tileCacheDir: File? = Configuration.getInstance().osmdroidTileCache
                ?: context.filesDir.resolve("osmdroid/tiles")
            if (tileCacheDir != null && tileCacheDir.exists()) {
                tileCacheDir.walkTopDown().forEach { f ->
                    if (f.isFile) total += f.length()
                }
            }
        }
        return total
    }

    /**
     * Size in bytes of every user-uploaded image stored under the app's
     * internal files directory (across all known upload subdirectories).
     */
    private fun computeUploadedImageBytes(context: Context): Long {
        var total = 0L
        val filesDir = context.filesDir
        USER_IMAGE_SUBDIRS.forEach { sub ->
            runCatching {
                val dir = File(filesDir, sub)
                if (dir.exists()) {
                    dir.walkTopDown().forEach { f ->
                        if (f.isFile) total += f.length()
                    }
                }
            }
        }
        return total
    }

    /**
     * Deletes every cached map tile (osmdroid SQLite cache + loose tile files)
     * and returns the number of bytes freed. Best-effort: never throws.
     */
    fun clearMapTiles(context: Context): Long {
        var freed = 0L
        runCatching {
            val tileCacheDir: File? = Configuration.getInstance().osmdroidTileCache
                ?: context.filesDir.resolve("osmdroid/tiles")
            if (tileCacheDir != null && tileCacheDir.exists()) {
                // SQLite cache: open and drop the tiles table so any active
                // SqlTileWriter releases its lock before we delete the file.
                val dbFile = File(tileCacheDir, "cache.db")
                if (dbFile.exists()) {
                    freed += dbFile.length()
                    runCatching {
                        val db = android.database.sqlite.SQLiteDatabase
                            .openOrCreateDatabase(dbFile, null)
                        db.execSQL("DELETE FROM tiles;")
                        db.execSQL("VACUUM;")
                        db.close()
                    }
                    runCatching { dbFile.delete() }
                }
                runCatching { File(tileCacheDir, "cache.db-wal").delete() }
                runCatching { File(tileCacheDir, "cache.db-shm").delete() }
                // Loose tile files + any archives subdirectory.
                runCatching {
                    File(tileCacheDir, "tiles").takeIf { it.exists() }?.let { dir ->
                        dir.walkBottomUp().forEach { f ->
                            if (f.isFile) freed += f.length()
                            f.delete()
                        }
                    }
                }
                // Also sweep any other loose tile files directly in the cache dir.
                runCatching {
                    tileCacheDir.listFiles()?.forEach { f ->
                        if (f.isFile && f.name != "cache.db" &&
                            f.name != "cache.db-wal" && f.name != "cache.db-shm"
                        ) {
                            freed += f.length()
                            f.delete()
                        }
                    }
                }
            }
        }
        return freed
    }

    /**
     * Deletes every user-uploaded rock / specimen / field-capture / community
     * image stored under the app's internal files directory (across all known
     * upload subdirectories) and returns the number of bytes freed.
     *
     * NOTE: This removes the locally-persisted originals. Saved specimens,
     * journal entries, and trade listings themselves are unaffected — only
     * the image files. They will re-download from remote storage if the
     * corresponding record still references a remote URL.
     */
    fun clearUploadedImages(context: Context): Long {
        var freed = 0L
        val filesDir = context.filesDir
        USER_IMAGE_SUBDIRS.forEach { sub ->
            runCatching {
                val dir = File(filesDir, sub)
                if (dir.exists()) {
                    dir.walkBottomUp().forEach { f ->
                        if (f.isFile) {
                            freed += f.length()
                            f.delete()
                        }
                    }
                    // Recreate the empty directory so future saves keep working.
                    runCatching { dir.mkdirs() }
                }
            }
        }
        return freed
    }

    /** Formats a byte count as a human-readable string (e.g. "24.3 MB", "1.2 GB"). */
    fun formatBytes(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1.0 -> String.format("%.2f GB", gb)
            mb >= 1.0 -> String.format("%.1f MB", mb)
            kb >= 1.0 -> String.format("%.0f KB", kb)
            else -> "$bytes B"
        }
    }
}

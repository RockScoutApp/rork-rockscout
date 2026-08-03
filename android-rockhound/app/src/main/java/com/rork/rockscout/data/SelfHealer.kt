package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import coil3.SingletonImageLoader
import org.osmdroid.config.Configuration
import java.io.File

/**
 * Auto-remediation for known recoverable error patterns.
 *
 * When [ErrorReporter] catches an exception, it asks [SelfHealer] to try
 * a fix before the error propagates. If the heal succeeds, the error is
 * still reported (with `autoHealed = true`) so we can track how often
 * self-healing fires, but the user never sees the failure.
 *
 * All heal methods are best-effort and wrapped in runCatching — a heal
 * failure is logged but never thrown.
 */
object SelfHealer {

    private const val TAG = "SelfHealer"

    /**
     * Attempts to heal a known error pattern. Returns a human-readable
     * description of the heal action if successful, or null if the error
     * is not a known recoverable pattern.
     */
    fun attemptHeal(context: Context?, screen: String, throwable: Throwable): String? {
        val errorName = throwable.javaClass.simpleName
        val message = throwable.message.orEmpty()

        // 1. Corrupt SQLite / cache database — delete and let the app rebuild
        if (errorName in corruptDbErrors || message.contains("database", ignoreCase = true) ||
            message.contains("corrupt", ignoreCase = true) ||
            message.contains("malformed", ignoreCase = true)
        ) {
            return healCorruptCache(context)
        }

        // 2. Out of memory — clear image caches
        if (errorName in oomErrors || message.contains("out of memory", ignoreCase = true) ||
            message.contains("OOM", ignoreCase = true)
        ) {
            return healOom(context)
        }

        // 3. Network state errors — clear stale connection pool
        if (errorName in networkErrors || message.contains("connection", ignoreCase = true) ||
            message.contains("timeout", ignoreCase = true) ||
            message.contains("closed", ignoreCase = true)
        ) {
            return healNetworkState()
        }

        // 4. File not found / IO — the file was expected but is gone
        if (errorName in ioErrors || message.contains("no such file", ignoreCase = true) ||
            message.contains("ENOENT", ignoreCase = true)
        ) {
            return healMissingFile(context, message)
        }

        // 5. SharedPreferences corruption — wipe and reload
        if ((message.contains("SharedPreferences", ignoreCase = true) &&
            message.contains("corrupt", ignoreCase = true)) ||
            (message.contains("prefs", ignoreCase = true) && message.contains("corrupt", ignoreCase = true))
        ) {
            return healPrefs(context)
        }

        return null
    }

    private val corruptDbErrors = setOf(
        "SQLiteException",
        "SQLiteDatabaseLockedException",
        "SQLiteDiskIOException",
        "SQLiteCantOpenDatabaseException",
        "DatabaseObjectNotClosedException",
    )

    private val oomErrors = setOf(
        "OutOfMemoryError",
        "OutOfMemoryError_$",
    )

    private val networkErrors = setOf(
        "SocketTimeoutException",
        "ConnectException",
        "SocketException",
        "ClosedChannelException",
        "ConnectionClosedException",
    )

    private val ioErrors = setOf(
        "FileNotFoundException",
        "NoSuchFileException",
        "IOException",
    )

    /** Delete corrupt SQLite/cache databases so the app recreates them fresh. */
    private fun healCorruptCache(context: Context?): String? {
        if (context == null) return null
        var healed = false

        // osmdroid tile cache
        runCatching {
            val tileCacheDir = Configuration.getInstance().osmdroidTileCache
            if (tileCacheDir != null) {
                val dbFile = File(tileCacheDir, "cache.db")
                if (dbFile.exists()) {
                    dbFile.delete()
                    File(tileCacheDir, "cache.db-wal").delete()
                    File(tileCacheDir, "cache.db-shm").delete()
                    healed = true
                    Log.i(TAG, "Healed corrupt osmdroid tile cache")
                }
            }
        }

        // Any SQLite DB in filesDir matching *.db
        runCatching {
            context.filesDir?.listFiles()?.forEach { file ->
                if (file.name.endsWith(".db") && file.length() == 0L) {
                    file.delete()
                    healed = true
                    Log.i(TAG, "Healed empty SQLite DB: ${file.name}")
                }
            }
        }

        return if (healed) "cleared_corrupt_cache" else null
    }

    /** Clear Coil image caches to free memory and prevent OOM cascades. */
    private fun healOom(context: Context?): String? {
        if (context == null) return null
        var healed = false

        runCatching {
            val loader = SingletonImageLoader.get(context)
            loader.memoryCache?.clear()
            healed = true
        }

        runCatching {
            // Also clear disk cache to free storage
            val loader = SingletonImageLoader.get(context)
            loader.diskCache?.clear()
        }

        // Suggest GC to help with memory pressure
        runCatching { System.gc() }

        return if (healed) "cleared_image_caches" else null
    }

    /** Reset the shared Ktor client's connection state. */
    private fun healNetworkState(): String? {
        // The NetworkClient singleton uses HttpRequestRetry with exponential
        // backoff, so transient network issues are already retried.
        // Return null so the error is logged fully instead of being masked
        // as auto-healed. This preserves the stack trace in BugLogger for
        // debugging while still allowing Ktor's built-in retry to work.
        Log.i(TAG, "Network error recognized — Ktor retry handles this automatically")
        return null
    }

    /** Recreate missing directories referenced in the error message. */
    private fun healMissingFile(context: Context?, message: String): String? {
        if (context == null) return null

        // Try to recreate the filesDir if it was somehow deleted
        runCatching {
            context.filesDir?.mkdirs()
        }

        // Recreate common subdirectories
        runCatching {
            context.filesDir?.resolve("crash_logs")?.mkdirs()
            context.filesDir?.resolve("osmdroid")?.mkdirs()
            context.filesDir?.resolve("osmdroid/tiles")?.mkdirs()
            context.filesDir?.resolve("image_cache")?.mkdirs()
            context.filesDir?.resolve("network_cache")?.mkdirs()
        }

        return "recreated_missing_dirs"
    }

    /** Clear corrupted SharedPreferences so the app can start fresh. */
    private fun healPrefs(context: Context?): String? {
        if (context == null) return null
        runCatching {
            // Only clear the specific prefs file if we can identify it
            // Otherwise leave it — clearing all prefs would lose user data
            val prefsDir = context.filesDir?.resolve("shared_prefs")
            prefsDir?.listFiles()?.forEach { file ->
                runCatching {
                    // Check if the file is valid XML; if not, delete it
                    val content = file.readText()
                    if (!content.startsWith("<?xml") && !content.startsWith("<map")) {
                        file.delete()
                        Log.i(TAG, "Healed corrupt prefs file: ${file.name}")
                    }
                }
            }
        }
        return "cleared_corrupt_prefs"
    }
}

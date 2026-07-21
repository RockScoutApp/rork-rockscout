package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Captures runtime errors (crashes + non-fatal) into a persistent local log
 * viewable from the hidden Developer Console.
 *
 * A global uncaught-exception handler is installed at app startup from
 * [RockScoutApplication]. Existing catch blocks can also call [log] to record
 * non-fatal errors. The log is capped at [MAX_ENTRIES] entries (FIFO).
 */
object BugLogger {

    private const val TAG = "BugLogger"
    private const val MAX_ENTRIES = 500
    private const val PREFS_KEY = "bug_log_entries"

    @Serializable
    data class BugEntry(
        val id: String,
        val timestamp: Long,
        val exceptionType: String,
        val message: String,
        val stackTrace: String,
        val screen: String,
        val appVersion: String,
        val isFatal: Boolean,
    )

    private val _entries = MutableStateFlow<List<BugEntry>>(emptyList())
    val entries: StateFlow<List<BugEntry>> = _entries.asStateFlow()

    private var initialized = false
    private var appVersionName: String = "unknown"
    private lateinit var appContext: Context

    /** Must be called once from Application.onCreate. */
    fun initialize(context: Context) {
        if (initialized) return
        initialized = true
        appVersionName = runCatching {
            context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionName ?: "unknown"
        }.getOrDefault("unknown")

        appContext = context.applicationContext
        loadFromDisk(context)
        installGlobalExceptionHandler()
    }

    /** Log a non-fatal error from a catch block or recovery path. */
    fun log(
        context: Context?,
        screen: String,
        throwable: Throwable,
        isFatal: Boolean = false,
    ) {
        addEntry(context, screen, throwable, isFatal)
    }

    /** Convenience for logging a simple message without a throwable. */
    fun logMessage(
        context: Context?,
        screen: String,
        message: String,
        isFatal: Boolean = false,
    ) {
        val entry = BugEntry(
            id = "bug-${System.currentTimeMillis()}-${(1..9999).random()}",
            timestamp = System.currentTimeMillis(),
            exceptionType = "Message",
            message = message,
            stackTrace = "",
            screen = screen,
            appVersion = appVersionName,
            isFatal = isFatal,
        )
        prependEntry(context, entry)
    }

    /** Clear all logged bugs. */
    fun clear(context: Context?) {
        _entries.value = emptyList()
        context?.let { persist(it) }
    }

    /** Copy a single entry to the clipboard-friendly text. */
    fun entryToText(entry: BugEntry): String {
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(entry.timestamp))
        return buildString {
            appendLine("RockScout Bug Report")
            appendLine("Time: $time")
            appendLine("Screen: ${entry.screen}")
            appendLine("Version: ${entry.appVersion}")
            appendLine("Fatal: ${entry.isFatal}")
            appendLine("Type: ${entry.exceptionType}")
            appendLine("Message: ${entry.message}")
            if (entry.stackTrace.isNotBlank()) {
                appendLine("Stack trace:")
                appendLine(entry.stackTrace)
            }
        }
    }

    // ---- Internal --------------------------------------------------------

    private fun installGlobalExceptionHandler() {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                addEntry(
                    context = appContext,
                    screen = "Global/${thread.name}",
                    throwable = throwable,
                    isFatal = true,
                )
            } catch (_: Throwable) {
                // Never let logging itself crash harder.
            }
            previous?.uncaughtException(thread, throwable)
        }
    }

    private fun addEntry(
        context: Context?,
        screen: String,
        throwable: Throwable,
        isFatal: Boolean,
    ) {
        val entry = BugEntry(
            id = "bug-${System.currentTimeMillis()}-${(1..9999).random()}",
            timestamp = System.currentTimeMillis(),
            exceptionType = throwable.javaClass.simpleName.ifBlank { "Error" },
            message = throwable.message ?: "(no message)",
            stackTrace = runCatching {
                android.util.Log.getStackTraceString(throwable).take(4000)
            }.getOrDefault(""),
            screen = screen,
            appVersion = appVersionName,
            isFatal = isFatal,
        )
        prependEntry(context, entry)
    }

    private fun prependEntry(context: Context?, entry: BugEntry) {
        val updated = (listOf(entry) + _entries.value).take(MAX_ENTRIES)
        _entries.value = updated
        context?.let { persist(it) }
        if (entry.isFatal) {
            Log.e(TAG, "[${entry.screen}] ${entry.exceptionType}: ${entry.message}")
        }
    }

    private fun loadFromDisk(context: Context) {
        runCatching {
            val raw = LocalDataStore.getString(PREFS_KEY) ?: return
            val list = LocalDataStore.json.decodeFromString<List<BugEntry>>(raw)
            _entries.value = list
        }.onFailure { /* start fresh */ }
    }

    private fun persist(context: Context) {
        runCatching {
            LocalDataStore.setString(
                PREFS_KEY,
                LocalDataStore.json.encodeToString(_entries.value),
            )
        }
    }
}

package com.rork.rockscout.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Captures a screenshot of the current [View] hierarchy and saves it to
 * app-private storage, returning a file path that can be stored in a report
 * row and attached to the moderation email.
 *
 * Also builds the pre-filled email [Intent] to RockScoutApp2026@yahoo.com with
 * the screenshot attached via [FileProvider].
 */
object ReportScreenshotHelper {

    private const val TAG = "ReportScreenshot"
    const val REPORT_EMAIL = "RockScoutApp2026@yahoo.com"

    /**
     * Capture [rootView] into a PNG under `reports/` in the cache dir.
     * Returns the absolute file path, or null on failure.
     */
    suspend fun captureScreenshot(
        context: Context,
        rootView: View,
        reportId: String,
    ): String? = withContext(Dispatchers.IO) {
        runCatching {
            val bitmap = captureViewBitmap(rootView)
            val dir = File(context.cacheDir, "reports").apply { mkdirs() }
            val file = File(dir, "report_$reportId.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            }
            bitmap.recycle()
            file.absolutePath
        }.onFailure { Log.w(TAG, "Screenshot capture failed: ${it.message}") }
            .getOrNull()
    }

    /** Build the email composer intent pre-filled to RockScoutApp2026@yahoo.com. */
    fun buildEmailIntent(
        context: Context,
        reportedUserName: String,
        reporterUserName: String,
        reason: String,
        timestamp: Long,
        screenshotPath: String?,
    ): Intent {
        val subject = "RockScout Report — $reportedUserName"
        val time = java.text.SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss z",
            java.util.Locale.US,
        ).format(java.util.Date(timestamp))
        val body = buildString {
            appendLine("RockScout user report")
            appendLine()
            appendLine("Reported user: $reportedUserName")
            appendLine("Reporter: $reporterUserName")
            appendLine("Reason: $reason")
            appendLine("Timestamp: $time")
            appendLine()
            appendLine("— Sent from the RockScout app")
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(REPORT_EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Attach screenshot if it exists
        screenshotPath?.let { path ->
            runCatching {
                val file = File(path)
                if (file.exists()) {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file,
                    )
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    intent.type = "image/png"
                }
            }
        }
        return intent
    }

    /** Launch the email composer; show a toast if no email app is available. */
    fun launchEmailComposer(context: Context, intent: Intent) {
        runCatching {
            val chooser = Intent.createChooser(intent, "Send report email").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        }.onFailure {
            Toast.makeText(
                context,
                "No email app found — report still saved in Developer Tools.",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    private fun captureViewBitmap(view: View): Bitmap {
        val width = view.width.coerceAtLeast(1)
        val height = view.height.coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}

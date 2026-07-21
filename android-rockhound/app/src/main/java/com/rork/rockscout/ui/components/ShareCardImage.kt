package com.rork.rockscout.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.rork.rockscout.data.SafeLinkOpener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Builds a clean, branded share-card [Bitmap] from the given inputs and launches
 * the system Android share sheet with the image plus the "Posted from RockScout"
 * caption.
 *
 * Drawing is done programmatically on an Android [Canvas] (not by capturing a
 * rendered Composable) so the output is:
 *  - deterministic — same card regardless of screen size, theme, or scroll state,
 *  - location-safe — only the fields passed in are drawn, so Field Captures can
 *    trivially strip GPS/location by simply not passing it,
 *  - cleanly branded — a consistent RockScout footer appears on every shared
 *    card, which drives free user acquisition on social media.
 *
 * The card is a 1080×1350 (4:5 portrait, Instagram-friendly) PNG written to the
 * app's cache dir and shared via [FileProvider] through [Intent.ACTION_SEND].
 */
object ShareCardImage {

    /** Card dimensions in pixels — 4:5 portrait, social-media friendly. */
    private const val CARD_W = 1080
    private const val CARD_H = 1350

    /** Caption attached to every shared card. */
    const val DEFAULT_CAPTION = "Posted from RockScout"

    /**
     * Build a share-card bitmap and launch the system share sheet.
     *
     * @param context any context — used for resources + launching the share intent.
     * @param title large headline text (e.g. specimen name or dig site name).
     * @param subtitle smaller line under the title (e.g. category, region, or date).
     * @param body optional longer text (notes, summary, tagline). Word-wrapped.
     * @param accentHex accent color (long ARGB) used for the top band + title.
     * @param photoBitmap optional specimen photo drawn behind the title area. If
     *   null, a gradient accent panel is drawn instead.
     * @param caption share-sheet text caption (defaults to [DEFAULT_CAPTION]).
     * @param fileName cache file name (without extension).
     */
    suspend fun share(
        context: Context,
        title: String,
        subtitle: String,
        body: String? = null,
        accentHex: Long = 0xFF2C6F9B,
        photoBitmap: Bitmap? = null,
        caption: String = DEFAULT_CAPTION,
        fileName: String = "rockscout_share_${System.currentTimeMillis()}",
    ) {
        val bitmap = withContext(Dispatchers.Default) {
            buildCard(title, subtitle, body, accentHex, photoBitmap)
        }
        val uri = withContext(Dispatchers.IO) {
            writeCache(context, bitmap, fileName)
        } ?: return
        launchShareIntent(context, uri, caption)
    }

    /** Renders the 1080×1350 card bitmap. Public so previews/tests can use it. */
    fun buildCard(
        title: String,
        subtitle: String,
        body: String?,
        accentHex: Long,
        photo: Bitmap?,
    ): Bitmap {
        val bmp = Bitmap.createBitmap(CARD_W, CARD_H, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val accent = accentHex.toInt()

        // ── Background: dark stone gradient ──────────────────────────────
        val bg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, 0f, CARD_H.toFloat(),
                Color.rgb(18, 22, 28),
                Color.rgb(28, 32, 40),
                Shader.TileMode.CLAMP,
            )
        }
        canvas.drawRect(0f, 0f, CARD_W.toFloat(), CARD_H.toFloat(), bg)

        // ── Top: photo or accent panel (clipped to top region) ───────────
        val photoTop = 0
        val photoBottom = 760
        val photoRect = Rect(0, photoTop, CARD_W, photoBottom)
        if (photo != null) {
            // Fill-clamp the photo into the top region (center-crop).
            val src = centerCropSrc(photo.width, photo.height, CARD_W, photoBottom - photoTop)
            canvas.drawBitmap(photo, src, photoRect, Paint(Paint.FILTER_BITMAP_FLAG))
            // Dark scrim gradient at the bottom of the photo for legibility.
            val scrim = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    0f, (photoBottom - 280).toFloat(), 0f, photoBottom.toFloat(),
                    0x00000000, 0xEE111418, Shader.TileMode.CLAMP,
                )
            }
            canvas.drawRect(0f, (photoBottom - 280).toFloat(), CARD_W.toFloat(), photoBottom.toFloat(), scrim)
        } else {
            // Accent gradient panel with subtle diagonal sheen.
            val panel = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    0f, 0f, CARD_W.toFloat(), photoBottom.toFloat(),
                    accent, darken(accent, 0.45f),
                    Shader.TileMode.CLAMP,
                )
            }
            canvas.drawRect(photoRect, panel)
        }

        // ── Accent band under the photo/panel ────────────────────────────
        val band = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accent }
        canvas.drawRect(0f, photoBottom.toFloat(), CARD_W.toFloat(), (photoBottom + 8).toFloat(), band)

        // ── Title (large, white, bold) ───────────────────────────────────
        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 64f
        }
        val titleX = 64f
        val titleY = (photoBottom + 130).toFloat()
        drawWrappedText(
            canvas, titlePaint, title,
            x = titleX, y = titleY,
            maxWidth = CARD_W - 128,
            maxLines = 2,
            lineHeight = 72f,
        )

        // ── Subtitle (medium, accent-tinted) ─────────────────────────────
        val subPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = lighten(accent, 0.5f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textSize = 38f
        }
        val subY = titleY + 72f * minOf(2, countLines(title, titlePaint, CARD_W - 128))
        drawWrappedText(
            canvas, subPaint, subtitle,
            x = titleX, y = subY,
            maxWidth = CARD_W - 128,
            maxLines = 2,
            lineHeight = 46f,
        )

        // ── Body (regular, light gray) ───────────────────────────────────
        var cursorY = subY + 46f * minOf(2, countLines(subtitle, subPaint, CARD_W - 128)) + 24f
        if (!body.isNullOrBlank()) {
            val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(210, 216, 224)
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textSize = 34f
            }
            cursorY = drawWrappedText(
                canvas, bodyPaint, body,
                x = titleX, y = cursorY,
                maxWidth = CARD_W - 128,
                maxLines = 8,
                lineHeight = 44f,
            )
        }

        // ── Footer: RockScout branding ───────────────────────────────────
        val footerY = CARD_H - 90f
        val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(150, 162, 178)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            textSize = 32f
        }
        val footer = "Posted from RockScout"
        val footerWidth = footerPaint.measureText(footer)
        canvas.drawText(footer, (CARD_W - footerWidth) / 2f, footerY, footerPaint)

        // Small accent dot next to the footer.
        val dot = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accent }
        canvas.drawCircle((CARD_W / 2f) - footerWidth / 2f - 28f, footerY - 12f, 8f, dot)

        return bmp
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private fun writeCache(context: Context, bitmap: Bitmap, fileName: String): Uri? {
        return try {
            val cacheDir = File(context.cacheDir, "shares").apply { mkdirs() }
            val file = File(cacheDir, "$fileName.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(CompressFormat.PNG, 100, out)
            }
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file,
            )
        } catch (_: Throwable) {
            null
        }
    }

    private fun launchShareIntent(context: Context, uri: Uri, caption: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, caption)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        SafeLinkOpener.openShareChooser(context, shareIntent, "Share to")
    }

    /** Center-crop source rect for filling [dstW]×[dstH] from a [srcW]×[srcH] bitmap. */
    private fun centerCropSrc(srcW: Int, srcH: Int, dstW: Int, dstH: Int): Rect {
        val srcRatio = srcW.toFloat() / srcH.toFloat()
        val dstRatio = dstW.toFloat() / dstH.toFloat()
        return if (srcRatio > dstRatio) {
            // Source is wider — crop sides.
            val newW = (srcH * dstRatio).toInt()
            val offsetX = (srcW - newW) / 2
            Rect(offsetX, 0, offsetX + newW, srcH)
        } else {
            // Source is taller — crop top/bottom.
            val newH = (srcW / dstRatio).toInt()
            val offsetY = (srcH - newH) / 2
            Rect(0, offsetY, srcW, offsetY + newH)
        }
    }

    private fun darken(color: Int, factor: Float): Int {
        val r = (Color.red(color) * factor).toInt().coerceIn(0, 255)
        val g = (Color.green(color) * factor).toInt().coerceIn(0, 255)
        val b = (Color.blue(color) * factor).toInt().coerceIn(0, 255)
        return Color.rgb(r, g, b)
    }

    private fun lighten(color: Int, factor: Float): Int {
        val r = (Color.red(color) + (255 - Color.red(color)) * factor).toInt().coerceIn(0, 255)
        val g = (Color.green(color) + (255 - Color.green(color)) * factor).toInt().coerceIn(0, 255)
        val b = (Color.blue(color) + (255 - Color.blue(color)) * factor).toInt().coerceIn(0, 255)
        return Color.rgb(r, g, b)
    }

    /** Word-wrap + draw, returns the y position after the last drawn line. */
    private fun drawWrappedText(
        canvas: Canvas,
        paint: TextPaint,
        text: String,
        x: Float,
        y: Float,
        maxWidth: Int,
        maxLines: Int,
        lineHeight: Float,
    ): Float {
        val words = text.split(' ')
        var line = StringBuilder()
        var linesDrawn = 0
        var currentY = y
        for (word in words) {
            val trial = if (line.isEmpty()) word else "$line $word"
            if (paint.measureText(trial) <= maxWidth) {
                line = StringBuilder(trial)
            } else {
                if (linesDrawn == maxLines - 1) {
                    // Last line — append ellipsis if more text remains.
                    var trimmed = line.toString()
                    while (paint.measureText("$trimmed…") > maxWidth && trimmed.isNotEmpty()) {
                        trimmed = trimmed.dropLast(1)
                    }
                    canvas.drawText("$trimmed…", x, currentY, paint)
                    return currentY + lineHeight
                }
                canvas.drawText(line.toString(), x, currentY, paint)
                currentY += lineHeight
                linesDrawn++
                line = StringBuilder(word)
            }
        }
        if (line.isNotEmpty() && linesDrawn < maxLines) {
            canvas.drawText(line.toString(), x, currentY, paint)
            currentY += lineHeight
        }
        return currentY
    }

    /** Counts how many lines [text] would wrap to within [maxWidth]. */
    private fun countLines(text: String, paint: TextPaint, maxWidth: Int): Int {
        val words = text.split(' ')
        var line = StringBuilder()
        var lines = 1
        for (word in words) {
            val trial = if (line.isEmpty()) word else "$line $word"
            if (paint.measureText(trial) <= maxWidth) {
                line = StringBuilder(trial)
            } else {
                lines++
                line = StringBuilder(word)
            }
        }
        return lines
    }

    /** Load a bitmap from a content [Uri] or remote URL, downsampled. */
    suspend fun loadDownsampled(context: Context, uri: Uri, maxDim: Int = 1080): Bitmap? =
        withContext(Dispatchers.IO) {
            try {
                val scheme = uri.scheme
                val openStream: () -> java.io.InputStream? = {
                    if (scheme == "http" || scheme == "https") {
                        java.net.URL(uri.toString()).openStream()
                    } else {
                        context.contentResolver.openInputStream(uri)
                    }
                }
                val opts = android.graphics.BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                openStream()?.use {
                    android.graphics.BitmapFactory.decodeStream(it, null, opts)
                }
                val srcW = opts.outWidth.coerceAtLeast(1)
                val srcH = opts.outHeight.coerceAtLeast(1)
                val sample = (((srcW.coerceAtLeast(srcH)) / maxDim).coerceAtLeast(1))
                    .let { Integer.highestOneBit(it).coerceAtLeast(1) }
                val decodeOpts = android.graphics.BitmapFactory.Options().apply { inSampleSize = sample }
                openStream()?.use {
                    android.graphics.BitmapFactory.decodeStream(it, null, decodeOpts)
                }
            } catch (_: Throwable) {
                null
            }
        }
}

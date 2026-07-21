package com.rork.rockscout.data

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Shared image utility for converting content URIs to base64 for AI moderation,
 * and for resizing bitmaps. Used by both the profile background picker and the
 * field-capture "Add to Specimen" picker.
 */
object ImageUtils {

    /** Maximum dimension for moderation images (keeps the base64 payload small). */
    private const val MAX_DIMENSION = 1024

    /** Maximum dimension for full-resolution user captures (camera/gallery).
     *  2048px is a safe upper bound — it bounds a single decode to ~16MB
     *  (ARGB_8888) regardless of the source image's native megapixel count,
     *  which prevents OOM on capture from high-MP phone cameras (e.g. 200MP
     *  sensors produce ~30k×30k bitmaps that would otherwise allocate ~3GB). */
    private const val MAX_CAPTURE_DIMENSION = 2048

    /**
     * Load a content URI as a base64-encoded JPEG string suitable for [ImageModerator.scan].
     * Returns null if the URI cannot be read.
     */
    fun uriToModerationBase64(context: Context, uri: Uri): String? {
        return try {
            val bitmap = decodeSampledBitmap(context, uri) ?: return null
            val resized = resizeBitmap(bitmap, MAX_DIMENSION)
            val baos = ByteArrayOutputStream()
            resized.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
        } catch (_: Exception) {
            null
        }
    }

    /** Resize a bitmap to fit within [maxDimension] while maintaining aspect ratio. */
    fun resizeBitmap(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) return bitmap
        val ratio = minOf(maxDimension.toFloat() / width, maxDimension.toFloat() / height)
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * Decode a content/file/http [uri] to a Bitmap, downsampled so the longest
     * edge is at most [maxDimension] px. This bounds memory regardless of the
     * source's native resolution — critical for camera captures and gallery
     * picks, where the source can be tens of megapixels.
     *
     * Uses a two-pass decode: first reads just the bounds (cheap), computes
     * an [BitmapFactory.Options.inSampleSize] that is a power of two and
     * brings the longest edge to <= [maxDimension], then decodes the real
     * bitmap at that sample size. The returned bitmap is always non-null on a
     * successfully decodable source, and null on any I/O or decode failure.
     */
    fun decodeSampledBitmap(
        context: Context,
        uri: Uri,
        maxDimension: Int = MAX_CAPTURE_DIMENSION,
    ): Bitmap? = try {
        val openStream: () -> java.io.InputStream? = {
            when (uri.scheme) {
                "http", "https" -> java.net.URL(uri.toString()).openStream()
                else -> context.contentResolver.openInputStream(uri)
            }
        }
        val boundsOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        openStream()?.use { BitmapFactory.decodeStream(it, null, boundsOpts) }
        val srcW = boundsOpts.outWidth
        val srcH = boundsOpts.outHeight
        if (srcW <= 0 || srcH <= 0) return null
        val longest = maxOf(srcW, srcH)
        var sample = 1
        while (longest / sample > maxDimension) sample *= 2
        val decodeOpts = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        openStream()?.use { BitmapFactory.decodeStream(it, null, decodeOpts) }
    } catch (_: Throwable) {
        null
    }

    /**
     * Decode an arbitrary [inputStream] to a Bitmap, downsampled so the longest
     * edge is at most [maxDimension] px. The stream is consumed and closed.
     *
     * Unlike the URI overload, this requires the caller to be able to re-open
     * the stream for the second decode pass — so this variant reads the entire
     * stream into a byte[] first, then decodes bounds + real from the in-memory
     * buffer. Use the URI overload when you have a re-openable URI; use this
     * only for one-shot streams (e.g. a network response you can't rewind).
     */
    fun decodeSampledBitmap(
        inputStream: java.io.InputStream?,
        maxDimension: Int = MAX_CAPTURE_DIMENSION,
    ): Bitmap? = try {
        if (inputStream == null) return null
        val bytes = inputStream.use { it.readBytes() }
        val boundsOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, boundsOpts)
        val srcW = boundsOpts.outWidth
        val srcH = boundsOpts.outHeight
        if (srcW <= 0 || srcH <= 0) return null
        val longest = maxOf(srcW, srcH)
        var sample = 1
        while (longest / sample > maxDimension) sample *= 2
        val decodeOpts = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, decodeOpts)
    } catch (_: Throwable) {
        null
    }

    /**
     * Copies a content URI (from GetContent/OpenDocument picker) to internal storage
     * so the image survives app restarts. Temporary URI permissions from GetContent
     * expire when the activity is destroyed, so persisting the raw content:// URI
     * string breaks image loading after restart.
     *
     * @param context any context
     * @param uri the content URI from the image picker
     * @param subdir subdirectory under app files dir (e.g. "backgrounds", "specimens")
     * @return a stable file:// URI string, or null on failure
     */
    fun copyUriToInternalStorage(context: Context, uri: Uri, subdir: String = "images"): String? {
        return try {
            val scheme = uri.scheme
            val inputStream: InputStream? = if (scheme == "http" || scheme == "https") {
                java.net.URL(uri.toString()).openStream()
            } else {
                context.contentResolver.openInputStream(uri)
            }
            if (inputStream == null) return null

            val dir = File(context.filesDir, subdir).apply { mkdirs() }
            val fileName = "img_${System.currentTimeMillis()}.jpg"
            val destFile = File(dir, fileName)

            FileOutputStream(destFile).use { out ->
                inputStream.use { input ->
                    input.copyTo(out)
                }
            }
            "file://${destFile.absolutePath}"
        } catch (_: Throwable) {
            null
        }
    }
}

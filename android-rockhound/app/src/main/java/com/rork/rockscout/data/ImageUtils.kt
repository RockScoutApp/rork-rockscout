package com.rork.rockscout.data

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayInputStream
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

    /** Maximum allowed size for a user-selected upload image (5 MB).
     *  Picks larger than this are rejected before any moderation / upload
     *  pipeline runs, preventing base64-encoding OOMs and failed uploads
     *  on slow connections. */
    const val MAX_UPLOAD_BYTES = 5L * 1024L * 1024L

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

    /** Returns the byte size of the content at [uri], or null if it can't be
     *  determined (e.g. the provider doesn't expose a length). Uses the
     *  AssetFileDescriptor length — a fast metadata query safe to call on
     *  the main thread — so callers can gate uploads without a full file read. */
    fun uriSizeBytes(context: Context, uri: Uri): Long? = try {
        context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { afd ->
            afd.length.takeIf { it >= 0 }
        }
    } catch (_: Throwable) {
        null
    }

    /** Returns true when the content at [uri] exceeds [MAX_UPLOAD_BYTES].
     *  Returns false when the size is unknown so uploads are not blocked
     *  on providers that don't report a length (graceful degradation). */
    fun isOverUploadLimit(context: Context, uri: Uri): Boolean {
        val bytes = uriSizeBytes(context, uri) ?: return false
        return bytes > MAX_UPLOAD_BYTES
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
     * Read the EXIF orientation tag from a content/file/http [uri].
     * Returns [ExifInterface.ORIENTATION_NORMAL] on any failure.
     */
    fun readExifOrientation(context: Context, uri: Uri): Int = try {
        val stream: InputStream? = when (uri.scheme) {
            "http", "https" -> java.net.URL(uri.toString()).openStream()
            else -> context.contentResolver.openInputStream(uri)
        }
        stream?.use { ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL) }
            ?: ExifInterface.ORIENTATION_NORMAL
    } catch (_: Throwable) {
        ExifInterface.ORIENTATION_NORMAL
    }

    /**
     * Apply an EXIF orientation value to a [bitmap], returning a new bitmap
     * rotated/flipped to match. Returns the original bitmap for
     * [ExifInterface.ORIENTATION_NORMAL].
     */
    fun applyExifOrientation(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.postRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.postRotate(270f)
                matrix.postScale(-1f, 1f)
            }
            else -> return bitmap
        }
        return try {
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (_: Throwable) {
            bitmap
        }
    }

    /**
     * Copy EXIF metadata (GPS, timestamp, camera, exposure) from [srcUri] to
     * [destFile]. Sets TAG_ORIENTATION to NORMAL (pixels are already upright).
     * Best-effort — wraps in try/catch so EXIF preservation never fails the
     * caller's operation.
     */
    fun copyExifMetadata(context: Context, srcUri: Uri, destFile: File) {
        try {
            val srcStream: InputStream? = when (srcUri.scheme) {
                "http", "https" -> java.net.URL(srcUri.toString()).openStream()
                else -> context.contentResolver.openInputStream(srcUri)
            }
            if (srcStream == null) return
            val srcExif = srcStream.use { ExifInterface(it) }
            val destExif = ExifInterface(destFile.absolutePath)
            val tagsToCopy = listOf(
                ExifInterface.TAG_GPS_LATITUDE,
                ExifInterface.TAG_GPS_LATITUDE_REF,
                ExifInterface.TAG_GPS_LONGITUDE,
                ExifInterface.TAG_GPS_LONGITUDE_REF,
                ExifInterface.TAG_GPS_ALTITUDE,
                ExifInterface.TAG_GPS_ALTITUDE_REF,
                ExifInterface.TAG_GPS_TIMESTAMP,
                ExifInterface.TAG_GPS_DATESTAMP,
                ExifInterface.TAG_DATETIME,
                ExifInterface.TAG_DATETIME_ORIGINAL,
                ExifInterface.TAG_DATETIME_DIGITIZED,
                ExifInterface.TAG_MAKE,
                ExifInterface.TAG_MODEL,
                ExifInterface.TAG_F_NUMBER,
                ExifInterface.TAG_EXPOSURE_TIME,
                ExifInterface.TAG_FOCAL_LENGTH,
                ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY,
            )
            for (tag in tagsToCopy) {
                srcExif.getAttribute(tag)?.let { destExif.setAttribute(tag, it) }
            }
            destExif.setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL.toString())
            destExif.saveAttributes()
        } catch (_: Throwable) {
            // Best-effort — never fail the caller's operation
        }
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
     *
     * After decoding, the bitmap is rotated to match the source JPEG's EXIF
     * orientation tag so the result is always upright.
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
        val decoded = openStream()?.use { BitmapFactory.decodeStream(it, null, decodeOpts) }
            ?: return null
        val orientation = readExifOrientation(context, uri)
        applyExifOrientation(decoded, orientation)
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
        val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, decodeOpts)
            ?: return null
        val orientation = try {
            ExifInterface(ByteArrayInputStream(bytes)).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL,
            )
        } catch (_: Throwable) {
            ExifInterface.ORIENTATION_NORMAL
        }
        applyExifOrientation(decoded, orientation)
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
    /**
     * Crop a square avatar region from a bitmap based on the user's pan/zoom
     * state in the profile editor preview box.
     *
     * The preview renders the image with ContentScale.Crop (equivalent to
     * object-fit: cover on web), then applies the user's pinch-zoom scale
     * and pan offset via GraphicsLayer.
     *
     * @param bitmap     The source bitmap
     * @param userScale  User's pinch-zoom scale (clamped to >= 1f)
     * @param offsetX    User's pan offset X in preview-box pixels
     * @param offsetY    User's pan offset Y in preview-box pixels
     * @param boxSizePx  The preview box size in pixels
     * @return A square-cropped bitmap suitable for avatar use
     */
    fun cropAvatarSquare(
        bitmap: Bitmap,
        userScale: Float,
        offsetX: Float,
        offsetY: Float,
        boxSizePx: Int,
    ): Bitmap {
        val bmpW = bitmap.width
        val bmpH = bitmap.height
        val cropScale = maxOf(boxSizePx.toFloat() / bmpW, boxSizePx.toFloat() / bmpH)
        val imgLeft = (boxSizePx - bmpW * cropScale) / 2f
        val imgTop = (boxSizePx - bmpH * cropScale) / 2f
        val us = userScale.coerceAtLeast(1f)
        val visSize = boxSizePx / (cropScale * us)
        val cx = (boxSizePx / 2f - imgLeft - offsetX / us) / cropScale
        val cy = (boxSizePx / 2f - imgTop - offsetY / us) / cropScale
        val half = visSize / 2f
        val left = (cx - half).coerceIn(0f, (bmpW - visSize).coerceAtLeast(0f))
        val top = (cy - half).coerceIn(0f, (bmpH - visSize).coerceAtLeast(0f))
        val size = visSize.toInt().coerceIn(1, minOf(bmpW, bmpH))
        return Bitmap.createBitmap(
            bitmap,
            left.toInt().coerceIn(0, (bmpW - size).coerceAtLeast(0)),
            top.toInt().coerceIn(0, (bmpH - size).coerceAtLeast(0)),
            size,
            size,
        )
    }

    /**
     * Decode a URI to a bitmap, crop a square avatar region based on the
     * user's pan/zoom state, resize to 300px, and save the result to internal
     * storage as a JPEG.
     *
     * @return A stable file:// URI string, or null on failure
     */
    fun cropAndSaveAvatar(
        context: Context,
        uri: Uri,
        userScale: Float,
        offsetX: Float,
        offsetY: Float,
        boxSizePx: Int,
    ): String? {
        return try {
            val bitmap = decodeSampledBitmap(context, uri, 1024) ?: return null
            val cropped = cropAvatarSquare(bitmap, userScale, offsetX, offsetY, boxSizePx)
            // Force output to 300x300, upscaling low-resolution crops with bilinear
            // filtering so profile pictures always render crisp and clear.
            val resized = Bitmap.createScaledBitmap(cropped, 300, 300, true)
            val dir = File(context.filesDir, "avatars").apply { mkdirs() }
            val fileName = "avatar_${System.currentTimeMillis()}.jpg"
            val destFile = File(dir, fileName)
            FileOutputStream(destFile).use { out ->
                resized.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            "file://${destFile.absolutePath}"
        } catch (_: Throwable) {
            null
        }
    }

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

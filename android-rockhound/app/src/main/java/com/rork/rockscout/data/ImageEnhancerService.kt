package com.rork.rockscout.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileOutputStream
import java.nio.FloatBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.ceil

/**
 * On-device AI image enhancer using the quantized Real-ESRGAN x4plus (w8a8)
 * TFLite model bundled in assets. Runs entirely on-device via LiteRT —
 * no network calls, no cloud credits.
 *
 * The model takes a 128x128x3 RGB tile (normalized to [0,1] float32) and
 * outputs a 512x512x3 enhanced tile (4x upscale). For full images, the
 * bitmap is split into overlapping 128x128 tiles, each is run through
 * inference, and the 512x512 outputs are stitched back together.
 *
 * Progress is reported via [onProgress] so the UI can show a progress bar
 * overlay with tile-by-tile status.
 */
object ImageEnhancerService {

    private const val TAG = "ImageEnhancerService"

    /** Model input tile size (128x128 pixels). */
    private const val TILE_SIZE = 128

    /** Model output tile size (512x512 pixels = 4x upscale). */
    private const val OUTPUT_TILE_SIZE = 512

    /** Scale factor of the model. */
    const val SCALE = 4

    /** Overlap between adjacent tiles in pixels (to avoid seam artifacts). */
    private const val OVERLAP = 8

    /** Max input dimension before downsampling (keeps processing time reasonable). */
    private const val MAX_INPUT_DIMENSION = 640

    /** Asset path for the TFLite model. */
    private const val MODEL_ASSET_PATH = "models/Real-ESRGAN-x4plus_w8a8.tflite"

    private var interpreter: Interpreter? = null

    /** Load (or return cached) the LiteRT interpreter for the ESRGAN model. */
    @Synchronized
    private fun getInterpreter(context: Context): Interpreter? {
        interpreter?.let { return it }
        return try {
            val assetManager = context.assets
            val modelBytes = assetManager.open(MODEL_ASSET_PATH).use { it.readBytes() }
            val buffer = ByteBuffer.allocateDirect(modelBytes.size)
                .order(ByteOrder.nativeOrder())
                .put(modelBytes)
            buffer.rewind()
            val options = Interpreter.Options()
            // CPU-only for broad device compatibility — the w8a8 quantized model
            // runs efficiently on CPU with very good quality.
            interpreter = Interpreter(buffer, options)
            interpreter
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to load ESRGAN model", e)
            null
        }
    }

    /** Result of an enhancement operation. */
    data class EnhanceResult(
        val enhancedBitmap: Bitmap,
        val enhancedUrl: String,
        val originalWidth: Int,
        val originalHeight: Int,
        val enhancedWidth: Int,
        val enhancedHeight: Int,
    )

    /** Progress callback for tile-by-tile processing. */
    fun interface ProgressCallback {
        fun onProgress(currentTile: Int, totalTiles: Int)
    }

    /**
     * Enhance an image from a [uri] (content://, file://, or http(s)://).
     * Splits into overlapping tiles, runs inference per tile, stitches the
     * results, saves the enhanced bitmap to internal storage, and returns
     * the result with the file:// URL.
     *
     * Returns null on any failure (model load, decode, OOM, etc.).
     */
    suspend fun enhance(
        context: Context,
        uri: Uri,
        onProgress: ProgressCallback,
    ): EnhanceResult? {
        val interp = getInterpreter(context) ?: return null

        // Load and optionally downsample the source image
        val sourceBitmap = ImageUtils.decodeSampledBitmap(context, uri, MAX_INPUT_DIMENSION)
            ?: return null

        return enhanceBitmap(context, sourceBitmap, interp, onProgress)
    }

    /**
     * Enhance an already-decoded [bitmap]. This is the core processing path —
     * tile splitting, inference, stitching, and saving.
     */
    private fun enhanceBitmap(
        context: Context,
        sourceBitmap: Bitmap,
        interp: Interpreter,
        onProgress: ProgressCallback,
    ): EnhanceResult? {
        val srcW = sourceBitmap.width
        val srcH = sourceBitmap.height
        if (srcW <= 0 || srcH <= 0) return null

        // Pad to multiples of TILE_SIZE - OVERLAP so tiles cover the full image
        val stride = TILE_SIZE - OVERLAP
        val tilesX = if (srcW <= TILE_SIZE) 1 else ceil((srcW - OVERLAP).toFloat() / stride).toInt()
        val tilesY = if (srcH <= TILE_SIZE) 1 else ceil((srcH - OVERLAP).toFloat() / stride).toInt()
        val totalTiles = tilesX * tilesY

        val outW = srcW * SCALE
        val outH = srcH * SCALE
        val outputBitmap = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)

        var currentTile = 0

        for (ty in 0 until tilesY) {
            for (tx in 0 until tilesX) {
                // Source tile coordinates
                val srcX = (tx * stride).coerceAtMost(srcW - TILE_SIZE)
                val srcY = (ty * stride).coerceAtMost(srcH - TILE_SIZE)
                val safeSrcX = srcX.coerceAtLeast(0)
                val safeSrcY = srcY.coerceAtLeast(0)

                // Extract or pad the source tile
                val tileBitmap = extractOrPadTile(sourceBitmap, safeSrcX, safeSrcY, TILE_SIZE)

                // Run inference
                val outputTile = runInference(interp, tileBitmap) ?: continue

                // Calculate destination position in the output bitmap
                val dstX = safeSrcX * SCALE
                val dstY = safeSrcY * SCALE

                // Draw the output tile, cropping the overlap region for non-edge tiles
                val srcOutX = if (tx > 0) OVERLAP * SCALE / 2 else 0
                val srcOutY = if (ty > 0) OVERLAP * SCALE / 2 else 0
                val effectiveOutX = dstX + (if (tx > 0) OVERLAP * SCALE / 2 else 0)
                val effectiveOutY = dstY + (if (ty > 0) OVERLAP * SCALE / 2 else 0)
                val drawW = (TILE_SIZE * SCALE - srcOutX).coerceAtMost(outW - effectiveOutX)
                val drawH = (OUTPUT_TILE_SIZE - srcOutY).coerceAtMost(outH - effectiveOutY)

                if (drawW > 0 && drawH > 0) {
                    val srcRect = Rect(srcOutX, srcOutY, srcOutX + drawW, srcOutY + drawH)
                    val dstRect = Rect(effectiveOutX, effectiveOutY, effectiveOutX + drawW, effectiveOutY + drawH)
                    canvas.drawBitmap(outputTile, srcRect, dstRect, null)
                }

                if (tileBitmap !== sourceBitmap) tileBitmap.recycle()
                outputTile.recycle()

                currentTile++
                onProgress.onProgress(currentTile, totalTiles)
            }
        }

        // Save enhanced bitmap to internal storage
        val savedUrl = saveBitmap(context, outputBitmap)
            ?: run {
                outputBitmap.recycle()
                return null
            }

        val result = EnhanceResult(
            enhancedBitmap = outputBitmap,
            enhancedUrl = savedUrl,
            originalWidth = srcW,
            originalHeight = srcH,
            enhancedWidth = outW,
            enhancedHeight = outH,
        )
        return result
    }

    /** Extract a TILE_SIZE x TILE_SIZE region from [bitmap] at (x, y).
     *  If the region extends beyond the bitmap, the extracted tile is padded
     *  with edge pixels (clamp) so it is always exactly TILE_SIZE x TILE_SIZE. */
    private fun extractOrPadTile(bitmap: Bitmap, x: Int, y: Int, size: Int): Bitmap {
        val bw = bitmap.width
        val bh = bitmap.height
        if (x + size <= bw && y + size <= bh) {
            return Bitmap.createBitmap(bitmap, x, y, size, size)
        }
        // Need to pad — create a TILE_SIZE square and draw the available pixels
        val tile = Bitmap.createBitmap(size, size, bitmap.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(tile)
        canvas.drawColor(Color.BLACK)
        val srcW = minOf(size, bw - x)
        val srcH = minOf(size, bh - y)
        if (srcW > 0 && srcH > 0) {
            val srcRect = Rect(x, y, x + srcW, y + srcH)
            val dstRect = Rect(0, 0, srcW, srcH)
            canvas.drawBitmap(bitmap, srcRect, dstRect, null)
        }
        return tile
    }

    /** Run ESRGAN inference on a single 128x128 tile.
     *  Input: [1, 128, 128, 3] float32 normalized to [0, 1].
     *  Output: [1, 512, 512, 3] float32. */
    private fun runInference(interp: Interpreter, tileBitmap: Bitmap): Bitmap? {
        val inputSize = TILE_SIZE * TILE_SIZE * 3
        val outputSize = OUTPUT_TILE_SIZE * OUTPUT_TILE_SIZE * 3

        val inputBuffer = FloatBuffer.allocate(inputSize)
        val outputBuffer = FloatBuffer.allocate(outputSize)

        // Convert bitmap pixels to normalized float array [0,1]
        val pixels = IntArray(TILE_SIZE * TILE_SIZE)
        tileBitmap.getPixels(pixels, 0, TILE_SIZE, 0, 0, TILE_SIZE, TILE_SIZE)
        for (pixel in pixels) {
            inputBuffer.put(((pixel shr 16) and 0xFF) / 255.0f) // R
            inputBuffer.put(((pixel shr 8) and 0xFF) / 255.0f)  // G
            inputBuffer.put((pixel and 0xFF) / 255.0f)          // B
        }
        inputBuffer.rewind()
        outputBuffer.rewind()

        val inputArray = arrayOf(inputBuffer)
        val outputArray = arrayOf(outputBuffer)

        return try {
            interp.runForMultipleInputsOutputs(inputArray, mapOf(0 to outputArray))
            outputBuffer.rewind()

            // Convert output float array back to bitmap
            val outputPixels = IntArray(OUTPUT_TILE_SIZE * OUTPUT_TILE_SIZE)
            for (i in 0 until outputPixels.size) {
                val r = (outputBuffer.get() * 255f).coerceIn(0f, 255f).toInt()
                val g = (outputBuffer.get() * 255f).coerceIn(0f, 255f).toInt()
                val b = (outputBuffer.get() * 255f).coerceIn(0f, 255f).toInt()
                outputPixels[i] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
            }
            Bitmap.createBitmap(outputPixels, OUTPUT_TILE_SIZE, OUTPUT_TILE_SIZE, Bitmap.Config.ARGB_8888)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Inference failed for tile", e)
            null
        }
    }

    /** Save a bitmap to internal storage as a high-quality JPEG and return the file:// URL. */
    private fun saveBitmap(context: Context, bitmap: Bitmap): String? {
        return try {
            val dir = File(context.filesDir, "enhanced").apply { mkdirs() }
            val fileName = "enhanced_${System.currentTimeMillis()}.jpg"
            val destFile = File(dir, fileName)
            FileOutputStream(destFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
            }
            "file://${destFile.absolutePath}"
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to save enhanced bitmap", e)
            null
        }
    }

    /** Release the interpreter to free native memory. Call when the enhancer
     *  screen is disposed or the app goes to background. */
    @Synchronized
    fun release() {
        interpreter?.close()
        interpreter = null
    }
}

package com.rork.rockscout.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.rork.rockscout.data.LocalDataStore
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/** Utility for saving bitmaps and content URIs to internal storage. */
object ImageUtils {

    /** Save a bitmap to internal storage under [subdir] and return the absolute file path,
     *  or null on failure. */
    fun saveBitmapToInternal(context: Context, bitmap: Bitmap, subdir: String = "report_images"): String? {
        return try {
            val dir = File(context.filesDir, subdir)
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "img_${UUID.randomUUID()}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            android.util.Log.w("ImageUtils", "saveBitmapToInternal failed", e)
            null
        }
    }

    /** Save a content URI (e.g. gallery photo) to internal storage and return the path. */
    fun saveUriToInternal(context: Context, uri: Uri, subdir: String = "report_images"): String? {
        return try {
            val input = context.contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(input)
            input.close()
            saveBitmapToInternal(context, bitmap, subdir)
        } catch (e: Exception) {
            android.util.Log.w("ImageUtils", "saveUriToInternal failed", e)
            null
        }
    }
}

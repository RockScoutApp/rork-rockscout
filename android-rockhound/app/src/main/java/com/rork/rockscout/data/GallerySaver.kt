package com.rork.rockscout.data

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.OutputStream

/**
 * Saves captured identification photos into a dedicated "RockScout Captures"
 * album in the device gallery via [MediaStore]. Returns the persistent
 * gallery [Uri] for the saved image, or null on failure.
 *
 * On API 29+ (Q) the scoped-storage MediaStore path is used with
 * [MediaStore.Images.Media.RELATIVE_PATH] pointing at
 * `Pictures/RockScout Captures`. On older devices the deprecated
 * [MediaStore.Images.Media.insertImage] helper is used instead, which
 * drops the file into the top-level Pictures directory.
 */
object GallerySaver {

    /** Display name of the gallery album the app writes captures into. */
    const val ALBUM_NAME: String = "RockScout Captures"

    private const val MIME_JPEG: String = "image/jpeg"

    /**
     * Saves [bitmap] to the "RockScout Captures" gallery album.
     *
     * @param resolver a [ContentResolver] from the calling context.
     * @param bitmap the photo to persist.
     * @param title file title (without extension) — defaults to a timestamped name.
     * @return the persistent gallery [Uri] for the saved image, or null on failure.
     */
    fun saveBitmap(
        resolver: ContentResolver,
        bitmap: Bitmap,
        title: String = "RockScout_${System.currentTimeMillis()}",
    ): Uri? = saveBitmapToAlbum(resolver, bitmap, title, ALBUM_NAME)

    /**
     * Saves [bitmap] to a custom gallery album (e.g. "Offline Maps").
     *
     * @param albumName the gallery album/folder name under Pictures/.
     * @return the persistent gallery [Uri] for the saved image, or null on failure.
     */
    fun saveBitmapToAlbum(
        resolver: ContentResolver,
        bitmap: Bitmap,
        title: String = "RockScout_${System.currentTimeMillis()}",
        albumName: String = ALBUM_NAME,
    ): Uri? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveQ(resolver, bitmap, title, albumName)
            } else {
                saveLegacy(resolver, bitmap, title)
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun saveQ(resolver: ContentResolver, bitmap: Bitmap, title: String, albumName: String = ALBUM_NAME): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$title.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, MIME_JPEG)
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$albumName")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = resolver.insert(collection, values) ?: return null
        if (!writeBitmap(resolver.openOutputStream(uri), bitmap)) {
            resolver.delete(uri, null, null)
            return null
        }
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        return uri
    }

    @Suppress("DEPRECATION")
    private fun saveLegacy(resolver: ContentResolver, bitmap: Bitmap, title: String): Uri? {
        val description = "Captured with RockScout"
        val url = MediaStore.Images.Media.insertImage(resolver, bitmap, title, description)
        return url?.let { Uri.parse(it) }
    }

    private fun writeBitmap(stream: OutputStream?, bitmap: Bitmap): Boolean {
        if (stream == null) return false
        return try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
            stream.close()
            true
        } catch (_: Exception) {
            false
        }
    }
}

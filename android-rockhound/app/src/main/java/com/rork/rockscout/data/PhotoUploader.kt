package com.rork.rockscout.data

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import java.io.File
import java.util.UUID

/**
 * Uploads local user photos (field captures, journal photos, trip photos,
 * saved images) to the Supabase Storage bucket `user-photos`.
 *
 * Files are stored at `user-photos/{userId}/{uuid}.{ext}` and the public URL
 * is returned so it can replace the local `file://` URI in database rows
 * during sync.
 *
 * Uses the Supabase REST Storage API (not the JS SDK) via [NetworkClient].
 * The bucket is public-read, so the returned URL can be used directly in
 * image views without signed URLs.
 */
object PhotoUploader {

    private const val TAG = "PhotoUploader"
    private const val BUCKET = "user-photos"

    private val client = NetworkClient.client

    private fun baseUrl(): String =
        BuildSecrets.resolve("EXPO_PUBLIC_SUPABASE_URL", BuildSecrets.SUPABASE_URL)

    private fun anonKey(): String =
        BuildSecrets.resolve("EXPO_PUBLIC_SUPABASE_ANON_KEY", BuildSecrets.SUPABASE_ANON_KEY)

    private fun accessToken(): String? =
        LocalDataStore.getString(LocalDataStore.KEY_SUPABASE_ACCESS_TOKEN)

    /**
     * Uploads a single local file to the user's storage folder.
     *
     * @param localUri A `file://` URI or a plain file path on internal storage.
     * @param userId   The Supabase auth user ID (used as the storage folder name).
     * @return The public URL of the uploaded file, or null on failure.
     */
    suspend fun upload(localUri: String, userId: String): String? {
        val token = accessToken() ?: run {
            Log.w(TAG, "Cannot upload — not authenticated")
            return null
        }

        // Resolve the file from the URI
        val file = resolveFile(localUri) ?: run {
            Log.w(TAG, "Cannot resolve file: $localUri")
            return null
        }

        if (!file.exists() || file.length() == 0L) {
            Log.w(TAG, "File does not exist or is empty: ${file.absolutePath}")
            return null
        }

        // Skip if already a remote URL (http/https)
        if (localUri.startsWith("http")) return localUri

        val ext = file.extension.lowercase().let {
            if (it.isBlank() || it !in listOf("jpg", "jpeg", "png", "webp", "heic", "heif")) "jpg" else it
        }
        val remotePath = "$userId/${UUID.randomUUID()}.$ext"
        val uploadUrl = "${baseUrl()}/storage/v1/object/$BUCKET/$remotePath"

        return try {
            val bytes = file.readBytes()
            val contentType = when (ext) {
                "png" -> ContentType.Image.PNG
                "webp" -> ContentType.parse("image/webp")
                "heic" -> ContentType.parse("image/heic")
                "heif" -> ContentType.parse("image/heif")
                else -> ContentType.Image.JPEG
            }

            val response: HttpResponse = client.put(uploadUrl) {
                header("apikey", anonKey())
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(contentType)
                header("x-upsert", "true")
                setBody(bytes)
            }

            if (!response.status.isSuccess()) {
                val body = response.body<String>()
                Log.w(TAG, "Upload failed: ${response.status} — $body")
                return null
            }

            // Construct the public URL for the uploaded object
            val publicUrl = "${baseUrl()}/storage/v1/object/public/$BUCKET/$remotePath"
            Log.d(TAG, "Uploaded ${file.name} → $publicUrl (${bytes.size} bytes)")
            publicUrl
        } catch (e: Exception) {
            Log.w(TAG, "Upload exception: ${e.message}", e)
            null
        }
    }

    /**
     * Uploads multiple local files and returns a map of original URI → remote URL.
     * Files that fail to upload are omitted from the result map (caller can
     * decide whether to retry or skip).
     */
    suspend fun uploadAll(uris: List<String>, userId: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        for (uri in uris) {
            if (uri.startsWith("http")) {
                // Already a remote URL — no upload needed
                result[uri] = uri
                continue
            }
            val remoteUrl = upload(uri, userId)
            if (remoteUrl != null) {
                result[uri] = remoteUrl
            }
        }
        return result
    }

    /**
     * Resolves a URI string to a [File] on the local filesystem.
     * Handles `file://` URIs, plain paths, and content:// URIs that were
     * previously copied to internal storage by [ImageUtils.copyUriToInternalStorage].
     */
    private fun resolveFile(uriString: String): File? {
        return try {
            when {
                uriString.startsWith("file://") -> File(android.net.Uri.parse(uriString).path ?: return null)
                uriString.startsWith("/") -> File(uriString)
                uriString.startsWith("content://") -> {
                    // Content URIs can't be read as File directly — skip.
                    // These should have been copied to internal storage already.
                    null
                }
                else -> File(uriString)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to resolve file: $uriString — ${e.message}")
            null
        }
    }
}

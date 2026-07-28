package com.rork.rockscout.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream

/**
 * Persisted email-composer draft for the Ask an Expert flow.
 *
 * Survives app close so the user's recipient selections, photo attachments,
 * and reply email are not lost if the app is killed mid-compose. Stored in
 * [Context.filesDir] (NOT cacheDir — the OS can purge cache) as a single
 * JSON file plus a photo directory.
 *
 * A draft older than 7 days is silently deleted on load (see [load]).
 */
@Serializable
data class EmailComposerDraft(
    val recipients: List<Museum> = emptyList(),
    val replyEmail: String = "",
    val includeCapturedPhoto: Boolean = false,
    val capturedPhotoPath: String? = null,
    val extraPhotoPaths: List<String> = emptyList(),
    val savedAtMs: Long = 0L,
)

object EmailComposerDraftStore {

    private const val DRAFT_FILE = "email_composer_draft.json"
    private const val PHOTO_DIR = "email_draft_photos"
    private const val SEVEN_DAYS_MS = 7L * 24 * 60 * 60 * 1000

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    /** Save the current draft to disk. Photos must already be copied to the
     *  photo dir; only their file paths are stored in the JSON. */
    fun save(draft: EmailComposerDraft) {
        if (!::appContext.isInitialized) return
        try {
            val file = File(appContext.filesDir, DRAFT_FILE)
            file.writeText(json.encodeToString(EmailComposerDraft.serializer(), draft))
        } catch (_: Throwable) {
            // Best-effort — never fail the compose flow
        }
    }

    /** Load and return the persisted draft, or null if none exists or it's
     *  stale (>7 days). Stale drafts and their photos are silently deleted. */
    fun load(): EmailComposerDraft? {
        if (!::appContext.isInitialized) return null
        return try {
            val file = File(appContext.filesDir, DRAFT_FILE)
            if (!file.exists()) return null
            val draft = json.decodeFromString(EmailComposerDraft.serializer(), file.readText())
            // Stale check — silently delete drafts older than 7 days
            val age = System.currentTimeMillis() - draft.savedAtMs
            if (age > SEVEN_DAYS_MS) {
                delete()
                return null
            }
            draft
        } catch (_: Throwable) {
            null
        }
    }

    /** Delete the draft file and all stored photos. */
    fun delete() {
        if (!::appContext.isInitialized) return
        try {
            File(appContext.filesDir, DRAFT_FILE).delete()
            val photoDir = File(appContext.filesDir, PHOTO_DIR)
            if (photoDir.exists()) {
                photoDir.listFiles()?.forEach { it.delete() }
                photoDir.delete()
            }
        } catch (_: Throwable) {
            // Best-effort
        }
    }

    /** Check if a draft exists without loading it. */
    fun exists(): Boolean {
        if (!::appContext.isInitialized) return false
        return File(appContext.filesDir, DRAFT_FILE).exists()
    }

    /** Copy a bitmap to the draft photo dir and return its file path. */
    fun saveCapturedPhoto(bitmap: Bitmap): String? {
        if (!::appContext.isInitialized) return null
        return try {
            val photoDir = File(appContext.filesDir, PHOTO_DIR).apply { mkdirs() }
            val file = File(photoDir, "captured_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            file.absolutePath
        } catch (_: Throwable) {
            null
        }
    }

    /** Copy a file-based photo URI to the draft photo dir and return its path. */
    fun copyPhotoToDraftDir(sourcePath: String): String? {
        if (!::appContext.isInitialized) return null
        return try {
            val sourceFile = File(sourcePath)
            if (!sourceFile.exists()) return null
            val photoDir = File(appContext.filesDir, PHOTO_DIR).apply { mkdirs() }
            val destFile = File(photoDir, "extra_${System.currentTimeMillis()}_${sourceFile.name}")
            sourceFile.copyTo(destFile, overwrite = true)
            destFile.absolutePath
        } catch (_: Throwable) {
            null
        }
    }

    /** Load a stored photo as a Bitmap (for the captured photo thumbnail). */
    fun loadBitmap(path: String): Bitmap? {
        return try {
            BitmapFactory.decodeFile(path)
        } catch (_: Throwable) {
            null
        }
    }

    /** Check if a stored photo file still exists on disk. */
    fun photoExists(path: String): Boolean {
        return try {
            File(path).exists()
        } catch (_: Throwable) {
            false
        }
    }
}

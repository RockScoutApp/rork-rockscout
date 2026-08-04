package com.rork.rockscout.data

import kotlinx.serialization.Serializable

/** A photo the user has saved from inside the app.
 *  Keeps the original remote URL, an optional local gallery URI, and a timestamp.
 *
 *  [originalUrl] stores the pre-enhancement URL when the image has been AI-enhanced,
 *  enabling the undo button on the Saved Images screen to revert an enhancement. */
@Serializable
data class SavedImage(
    val id: String,
    val url: String,
    val localUri: String? = null,
    val savedAt: Long = System.currentTimeMillis(),
    val liked: Boolean = false,
    /** When non-null, this image is the result of an AI enhancement and the
     *  original (pre-enhancement) image URL is stored here for undo. */
    val originalUrl: String? = null,
)

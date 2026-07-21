package com.rork.rockscout.data

import kotlinx.serialization.Serializable

/** A photo the user has saved from inside the app.
 *  Keeps the original remote URL, an optional local gallery URI, and a timestamp. */
@Serializable
data class SavedImage(
    val id: String,
    val url: String,
    val localUri: String? = null,
    val savedAt: Long = System.currentTimeMillis(),
    val liked: Boolean = false,
)

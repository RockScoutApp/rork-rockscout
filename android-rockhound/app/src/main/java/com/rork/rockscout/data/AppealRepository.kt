package com.rork.rockscout.data

import com.rork.rockscout.data.MockDataSeeder.LocalAppeal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Stores moderation appeals submitted from notification rows.
 * Appeals appear in the Developer Console moderation tab so the developer
 * can review and reverse decisions (reinstate user, approve rejected image).
 *
 * Fully self-contained: all data stored in [LocalDataStore].
 */
class AppealRepository private constructor() {

    companion object {
        val instance: AppealRepository by lazy { AppealRepository() }
    }

    /** Submit an appeal for an image rejection or report/ban. */
    suspend fun submitAppeal(
        userId: String,
        type: String,
        refId: String?,
        reason: String,
        imageUri: String? = null,
    ): String = withContext(Dispatchers.IO) {
        val id = "appeal-" + UUID.randomUUID()
        val appeal = LocalAppeal(
            id = id,
            user_id = userId,
            type = type,
            ref_id = refId,
            reason = ProfanityFilter.filter(reason),
            image_uri = imageUri,
            status = "pending",
            created_at = System.currentTimeMillis(),
        )
        LocalDataStore.updateTable(LocalDataStore.KEY_APPEALS) { rows ->
            listOf(appeal) + rows
        }
        id
    }

    /** Get all pending appeals, newest first. */
    suspend fun getPendingAppeals(): List<LocalAppeal> = withContext(Dispatchers.IO) {
        LocalDataStore.getTable<LocalAppeal>(LocalDataStore.KEY_APPEALS)
            .filter { it.status == "pending" }
            .sortedByDescending { it.created_at }
    }

    /** Get all appeals (including resolved), newest first. */
    suspend fun getAllAppeals(): List<LocalAppeal> = withContext(Dispatchers.IO) {
        LocalDataStore.getTable<LocalAppeal>(LocalDataStore.KEY_APPEALS)
            .sortedByDescending { it.created_at }
    }

    /** Resolve an appeal — mark it approved or denied. */
    suspend fun resolveAppeal(appealId: String, approved: Boolean) = withContext(Dispatchers.IO) {
        LocalDataStore.updateTable<LocalAppeal>(LocalDataStore.KEY_APPEALS) { rows ->
            rows.map { row ->
                if (row.id == appealId) row.copy(status = if (approved) "approved" else "denied") else row
            }
        }
    }
}

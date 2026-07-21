package com.rork.rockscout.data

import com.rork.rockscout.data.MockDataSeeder.LocalImageReview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Manages the local image review queue for profile background and field-capture
 * images flagged as "questionable" by [ImageModerator].
 *
 * Stores reviews in [LocalDataStore] under [LocalDataStore.KEY_IMAGE_REVIEWS].
 * The Developer Console Moderation tab reads pending reviews and can approve
 * or delete them. Deleting a review triggers a rejection email to the user.
 */
class ImageReviewRepository private constructor() {

    companion object {
        val instance: ImageReviewRepository by lazy { ImageReviewRepository() }
    }

    /** Add a new image review entry to the queue. */
    suspend fun submitReview(
        userId: String,
        userName: String?,
        userAvatar: String?,
        imageUri: String,
        type: String,
        captureId: String? = null,
        reason: String,
    ): String = withContext(Dispatchers.IO) {
        val id = "img-review-" + UUID.randomUUID()
        val review = LocalImageReview(
            id = id,
            user_id = userId,
            user_name = userName,
            user_avatar = userAvatar,
            image_uri = imageUri,
            type = type,
            capture_id = captureId,
            reason = reason,
            status = "pending",
            created_at = System.currentTimeMillis(),
        )
        LocalDataStore.updateTable(LocalDataStore.KEY_IMAGE_REVIEWS) { rows ->
            listOf(review) + rows
        }
        id
    }

    /** Get all pending image reviews, newest first. */
    suspend fun getPendingReviews(): List<LocalImageReview> = withContext(Dispatchers.IO) {
        LocalDataStore.getTable<LocalImageReview>(LocalDataStore.KEY_IMAGE_REVIEWS)
            .filter { it.status == "pending" }
            .sortedByDescending { it.created_at }
    }

    /** Approve a review — marks it approved and returns the review so the caller
     *  can apply the image (set profile background or add to capture). */
    suspend fun approveReview(reviewId: String): LocalImageReview? = withContext(Dispatchers.IO) {
        LocalDataStore.updateTable<LocalImageReview>(LocalDataStore.KEY_IMAGE_REVIEWS) { rows ->
            rows.map { row ->
                if (row.id == reviewId) row.copy(status = "approved") else row
            }
        }
        // Re-read to get the approved row
        LocalDataStore.getTable<LocalImageReview>(LocalDataStore.KEY_IMAGE_REVIEWS)
            .firstOrNull { it.id == reviewId }
    }

    /** Delete a review — marks it deleted and returns the review so the caller
     *  can send a rejection email and clear the pending state on the model.
     *  Also fires an instant image-rejected notification to the user (no toggle). */
    suspend fun deleteReview(reviewId: String): LocalImageReview? = withContext(Dispatchers.IO) {
        // Capture the review before mutating
        val target = LocalDataStore.getTable<LocalImageReview>(LocalDataStore.KEY_IMAGE_REVIEWS)
            .firstOrNull { it.id == reviewId }
        if (target != null) {
            LocalDataStore.updateTable<LocalImageReview>(LocalDataStore.KEY_IMAGE_REVIEWS) { rows ->
                rows.map { row ->
                    if (row.id == reviewId) row.copy(status = "deleted") else row
                }
            }
            // Fire an instant image-rejected notification to the user.
            // No toggle — always on.
            val body = if (target.type == "profile_background") {
                "Your profile image was rejected. Tap to appeal."
            } else {
                "Your field capture image was rejected. Tap to appeal."
            }
            NotificationRepository.instance.createNotification(
                userId = target.user_id,
                type = NotificationRepository.TYPE_IMAGE_REJECTED,
                actorId = null,
                refId = reviewId,
                body = body,
                deepLinkTarget = "profile",
            )
            target.copy(status = "deleted")
        } else {
            null
        }
    }
}

package com.rork.rockscout.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rork.rockscout.data.DigLocation
import com.rork.rockscout.data.LocationType

/**
 * Room entity for [DigLocation]. Stores both US and international locations
 * in the same table, distinguished by [isInternational].
 */
@Entity(tableName = "dig_locations")
data class DigLocationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val region: String,
    val latitude: Double,
    val longitude: Double,
    val summary: String,
    val knownFor: List<String>,
    val mineralTags: List<String>,
    val feeInfo: String,
    val hours: String,
    val website: String?,
    val phone: String?,
    val difficulty: String,
    val publicAccess: Boolean,
    val tips: String,
    val submitterName: String?,
    val submittedPhotoUris: List<String>,
    val addedAtMs: Long,
    val imageUrl: String?,
    val address: String?,
    val isInternational: Boolean,
)

/** Maps this domain model to its Room entity. */
fun DigLocation.toEntity(isInternational: Boolean): DigLocationEntity =
    DigLocationEntity(
        id = id,
        name = name,
        type = type.name,
        region = region,
        latitude = latitude,
        longitude = longitude,
        summary = summary,
        knownFor = knownFor,
        mineralTags = mineralTags,
        feeInfo = feeInfo,
        hours = hours,
        website = website,
        phone = phone,
        difficulty = difficulty,
        publicAccess = publicAccess,
        tips = tips,
        submitterName = submitterName,
        submittedPhotoUris = submittedPhotoUris,
        addedAtMs = addedAtMs,
        imageUrl = imageUrl,
        address = address,
        isInternational = isInternational,
    )

/** Maps this Room entity back to the domain model. */
fun DigLocationEntity.toDigLocation(): DigLocation =
    DigLocation(
        id = id,
        name = name,
        type = runCatching { LocationType.valueOf(type) }.getOrDefault(LocationType.PUBLIC_DIG),
        region = region,
        latitude = latitude,
        longitude = longitude,
        summary = summary,
        knownFor = knownFor,
        mineralTags = mineralTags,
        feeInfo = feeInfo,
        hours = hours,
        website = website,
        phone = phone,
        difficulty = difficulty,
        publicAccess = publicAccess,
        tips = tips,
        submitterName = submitterName,
        submittedPhotoUris = submittedPhotoUris,
        addedAtMs = addedAtMs,
        imageUrl = imageUrl,
        address = address,
    )

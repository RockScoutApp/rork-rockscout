package com.rork.rockscout.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rork.rockscout.data.MuseumEntry

/**
 * Room entity for [MuseumEntry]. Curated museums use a composite key
 * ("name|city|state"); user-added museums use their [UserMuseum] id.
 */
@Entity(tableName = "museums")
data class MuseumEntity(
    @PrimaryKey val id: String,
    val name: String,
    val city: String,
    val state: String,
    val website: String,
    val lat: Double,
    val lng: Double,
    val imageUrl: String?,
    val address: String?,
    val isUserAdded: Boolean,
)

/** Maps a curated [MuseumEntry] to its Room entity. */
fun MuseumEntry.toEntity(): MuseumEntity =
    MuseumEntity(
        id = "$name|$city|$state",
        name = name,
        city = city,
        state = state,
        website = website,
        lat = lat,
        lng = lng,
        imageUrl = imageUrl,
        address = address,
        isUserAdded = false,
    )

/** Maps this Room entity back to the domain model. */
fun MuseumEntity.toMuseumEntry(): MuseumEntry =
    MuseumEntry(
        name = name,
        city = city,
        state = state,
        website = website,
        lat = lat,
        lng = lng,
        imageUrl = imageUrl,
        address = address,
    )

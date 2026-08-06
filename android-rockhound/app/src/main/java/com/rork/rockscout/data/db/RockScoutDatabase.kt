package com.rork.rockscout.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Main Room database for RockScout offline data.
 *
 * Stores dig locations (US + international) and museums (curated + user-added)
 * so users can browse all content without an active internet connection.
 *
 * Seeded from hardcoded Kotlin objects on first launch via
 * [com.rork.rockscout.data.LocationCacheRepository].
 */
@Database(
    entities = [DigLocationEntity::class, MuseumEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class RockScoutDatabase : RoomDatabase() {

    abstract fun digLocationDao(): DigLocationDao
    abstract fun museumDao(): MuseumDao

    companion object {
        @Volatile
        private var INSTANCE: RockScoutDatabase? = null

        fun getInstance(context: Context): RockScoutDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    RockScoutDatabase::class.java,
                    "rockscout.db",
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}

package com.rork.rockscout.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DigLocationDao {

    @Query("SELECT * FROM dig_locations ORDER BY name COLLATE NOCASE")
    fun getAll(): Flow<List<DigLocationEntity>>

    @Query("SELECT * FROM dig_locations WHERE isInternational = 0 ORDER BY name COLLATE NOCASE")
    fun getUsLocations(): Flow<List<DigLocationEntity>>

    @Query("SELECT * FROM dig_locations WHERE isInternational = 1 ORDER BY name COLLATE NOCASE")
    fun getInternationalLocations(): Flow<List<DigLocationEntity>>

    @Query("SELECT * FROM dig_locations WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): DigLocationEntity?

    @Query(
        "SELECT * FROM dig_locations WHERE " +
            "name LIKE '%' || :query || '%' OR " +
            "region LIKE '%' || :query || '%' OR " +
            "address LIKE '%' || :query || '%' " +
            "ORDER BY name COLLATE NOCASE"
    )
    fun search(query: String): Flow<List<DigLocationEntity>>

    @Query("SELECT COUNT(*) FROM dig_locations")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(locations: List<DigLocationEntity>)

    @Query("DELETE FROM dig_locations")
    suspend fun deleteAll()
}

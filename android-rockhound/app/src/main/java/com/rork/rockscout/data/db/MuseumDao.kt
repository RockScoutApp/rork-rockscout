package com.rork.rockscout.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MuseumDao {

    @Query("SELECT * FROM museums WHERE isUserAdded = 0 ORDER BY state COLLATE NOCASE, name COLLATE NOCASE")
    fun getCuratedMuseums(): Flow<List<MuseumEntity>>

    @Query("SELECT * FROM museums ORDER BY state COLLATE NOCASE, name COLLATE NOCASE")
    fun getAll(): Flow<List<MuseumEntity>>

    @Query("SELECT * FROM museums WHERE isUserAdded = 1 ORDER BY name COLLATE NOCASE")
    fun getUserAdded(): Flow<List<MuseumEntity>>

    @Query("SELECT COUNT(*) FROM museums WHERE isUserAdded = 0")
    suspend fun curatedCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(museums: List<MuseumEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(museum: MuseumEntity)

    @Query("DELETE FROM museums WHERE isUserAdded = 0")
    suspend fun deleteCurated()

    @Query("DELETE FROM museums WHERE id = :id")
    suspend fun deleteById(id: String)
}

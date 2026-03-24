package com.kefdev.zentrack.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kefdev.zentrack.data.local.entity.MeditationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeditationDao {
    @Query("SELECT * FROM meditations ORDER BY completed_at DESC")
    fun getAllMeditations(): Flow<List<MeditationEntity>>

    @Query("SELECT COALESCE(SUM(duration_minutes), 0) FROM meditations")
    fun getTotalMinutes(): Flow<Int>

    @Insert
    suspend fun insert(entity: MeditationEntity)

    @Delete
    suspend fun delete(entity: MeditationEntity)
}

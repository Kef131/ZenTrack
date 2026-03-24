package com.kefdev.zentrack.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kefdev.zentrack.data.local.entity.MeditationEntity

@Database(entities = [MeditationEntity::class], version = 1, exportSchema = false)
abstract class ZenTrackDatabase : RoomDatabase() {
    abstract fun meditationDao(): MeditationDao
}

package com.kefdev.zentrack.di

import android.content.Context
import androidx.room.Room
import com.kefdev.zentrack.data.local.MeditationDao
import com.kefdev.zentrack.data.local.ZenTrackDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideZenTrackDatabase(@ApplicationContext context: Context): ZenTrackDatabase =
        Room.databaseBuilder(context, ZenTrackDatabase::class.java, "zentrack_database").build()

    @Provides
    @Singleton
    fun provideMeditationDao(database: ZenTrackDatabase): MeditationDao = database.meditationDao()
}

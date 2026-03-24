package com.kefdev.zentrack.di

import com.kefdev.zentrack.data.repository.MeditationRepository
import com.kefdev.zentrack.data.repository.MeditationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMeditationRepository(
        impl: MeditationRepositoryImpl
    ): MeditationRepository
}

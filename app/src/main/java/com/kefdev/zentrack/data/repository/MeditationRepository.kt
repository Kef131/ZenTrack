package com.kefdev.zentrack.data.repository

import com.kefdev.zentrack.domain.model.Meditation
import kotlinx.coroutines.flow.Flow

interface MeditationRepository {
    fun getAllMeditations(): Flow<List<Meditation>>
    fun getTotalMinutes(): Flow<Int>
    suspend fun insertMeditation(meditation: Meditation)
    suspend fun deleteMeditation(meditation: Meditation)
}

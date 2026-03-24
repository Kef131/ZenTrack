package com.kefdev.zentrack.data.repository

import com.kefdev.zentrack.data.local.MeditationDao
import com.kefdev.zentrack.domain.model.Meditation
import com.kefdev.zentrack.domain.model.toDomain
import com.kefdev.zentrack.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MeditationRepositoryImpl @Inject constructor(
    private val dao: MeditationDao
) : MeditationRepository {

    override fun getAllMeditations(): Flow<List<Meditation>> =
        dao.getAllMeditations().map { list -> list.map { it.toDomain() } }

    override fun getTotalMinutes(): Flow<Int> = dao.getTotalMinutes()

    override suspend fun insertMeditation(meditation: Meditation) =
        dao.insert(meditation.toEntity())

    override suspend fun deleteMeditation(meditation: Meditation) =
        dao.delete(meditation.toEntity())
}

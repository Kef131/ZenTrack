package com.kefdev.zentrack.data.repository

import com.kefdev.zentrack.domain.model.Meditation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeMeditationRepository : MeditationRepository {

    private val _meditations = MutableStateFlow<List<Meditation>>(emptyList())

    override fun getAllMeditations(): Flow<List<Meditation>> = _meditations.asStateFlow()

    override fun getTotalMinutes(): Flow<Int> =
        _meditations.map { list -> list.sumOf { it.durationMinutes } }

    override suspend fun insertMeditation(meditation: Meditation) {
        _meditations.update { current ->
            current + meditation.copy(id = current.size.toLong() + 1)
        }
    }

    override suspend fun deleteMeditation(meditation: Meditation) {
        _meditations.update { current -> current.filter { it.id != meditation.id } }
    }
}

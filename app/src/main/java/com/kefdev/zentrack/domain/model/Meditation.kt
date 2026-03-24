package com.kefdev.zentrack.domain.model

import com.kefdev.zentrack.data.local.entity.MeditationEntity

data class Meditation(
    val id: Long,
    val durationMinutes: Int,
    val completedAt: Long
)

fun MeditationEntity.toDomain(): Meditation = Meditation(id, durationMinutes, completedAt)
fun Meditation.toEntity(): MeditationEntity = MeditationEntity(id, durationMinutes, completedAt)

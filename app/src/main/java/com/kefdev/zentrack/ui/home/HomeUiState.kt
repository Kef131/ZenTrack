package com.kefdev.zentrack.ui.home

import com.kefdev.zentrack.domain.model.Meditation

data class HomeUiState(
    val totalMinutes: Int = 0,
    val totalSessions: Int = 0,
    val recentMeditations: List<Meditation> = emptyList(),
    val isLoading: Boolean = true
)

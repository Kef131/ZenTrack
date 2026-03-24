package com.kefdev.zentrack.ui.history

import com.kefdev.zentrack.domain.model.Meditation

data class HistoryUiState(
    val meditations: List<Meditation> = emptyList(),
    val totalMinutes: Int = 0,
    val totalSessions: Int = 0,
    val isLoading: Boolean = true
)

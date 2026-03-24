package com.kefdev.zentrack.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kefdev.zentrack.data.repository.MeditationRepository
import com.kefdev.zentrack.domain.model.Meditation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: MeditationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HistoryUiEvent>(replay = 0, extraBufferCapacity = 1)
    val events: SharedFlow<HistoryUiEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getAllMeditations(),
                repository.getTotalMinutes()
            ) { meditations, totalMinutes ->
                HistoryUiState(
                    meditations = meditations,
                    totalMinutes = totalMinutes,
                    totalSessions = meditations.size,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.update { state }
            }
        }
    }

    fun onDeleteMeditation(meditation: Meditation) {
        viewModelScope.launch {
            repository.deleteMeditation(meditation)
            _events.tryEmit(HistoryUiEvent.ShowMessage("Sesión eliminada"))
        }
    }
}

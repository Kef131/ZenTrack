package com.kefdev.zentrack.ui.home

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
class HomeViewModel @Inject constructor(
    private val repository: MeditationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeUiEvent>(replay = 0, extraBufferCapacity = 1)
    val events: SharedFlow<HomeUiEvent> = _events.asSharedFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                repository.getAllMeditations(),
                repository.getTotalMinutes()
            ) { meditations, totalMinutes ->
                HomeUiState(
                    totalMinutes = totalMinutes,
                    totalSessions = meditations.size,
                    recentMeditations = meditations,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.update { state }
            }
        }
    }

    fun onStartMeditation(minutes: Int) {
        if (minutes <= 0) {
            _events.tryEmit(HomeUiEvent.ShowMessage("Selecciona una duración válida"))
            return
        }
        _events.tryEmit(HomeUiEvent.NavigateToTimer(minutes))
    }

    fun onDeleteMeditation(meditation: Meditation) {
        viewModelScope.launch {
            repository.deleteMeditation(meditation)
            _events.tryEmit(HomeUiEvent.ShowMessage("Sesión eliminada"))
        }
    }
}

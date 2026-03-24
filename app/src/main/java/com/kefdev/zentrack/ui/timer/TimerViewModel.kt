package com.kefdev.zentrack.ui.timer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kefdev.zentrack.data.repository.MeditationRepository
import com.kefdev.zentrack.domain.model.Meditation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val repository: MeditationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val presetMinutes: Int = savedStateHandle.get<Int>("minutes") ?: 5
    private val initialSeconds: Int = presetMinutes * 60

    private val _uiState = MutableStateFlow(
        TimerUiState(totalSeconds = initialSeconds, remainingSeconds = initialSeconds)
    )
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TimerUiEvent>(replay = 0, extraBufferCapacity = 1)
    val events: SharedFlow<TimerUiEvent> = _events.asSharedFlow()

    private var timerJob: Job? = null

    fun startTimer() {
        if (timerJob?.isActive == true) return
        timerJob = viewModelScope.launch {
            _uiState.update { it.copy(isRunning = true) }
            while (_uiState.value.remainingSeconds > 0) {
                delay(1_000L)
                _uiState.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
            }
            _uiState.update { it.copy(isRunning = false, isFinished = true) }
            saveMeditation()
            _events.tryEmit(TimerUiEvent.MeditationCompleted)
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false) }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _uiState.update {
            TimerUiState(totalSeconds = initialSeconds, remainingSeconds = initialSeconds)
        }
    }

    private suspend fun saveMeditation() {
        repository.insertMeditation(
            Meditation(
                id = 0L,
                durationMinutes = presetMinutes,
                completedAt = System.currentTimeMillis()
            )
        )
    }
}

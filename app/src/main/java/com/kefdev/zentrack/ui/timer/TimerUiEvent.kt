package com.kefdev.zentrack.ui.timer

sealed interface TimerUiEvent {
    data object MeditationCompleted : TimerUiEvent
}

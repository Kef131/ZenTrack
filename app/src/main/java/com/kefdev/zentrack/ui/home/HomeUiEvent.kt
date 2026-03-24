package com.kefdev.zentrack.ui.home

sealed interface HomeUiEvent {
    data class ShowMessage(val message: String) : HomeUiEvent
    data class NavigateToTimer(val presetMinutes: Int) : HomeUiEvent
}

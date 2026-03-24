package com.kefdev.zentrack.ui.history

sealed interface HistoryUiEvent {
    data class ShowMessage(val message: String) : HistoryUiEvent
}

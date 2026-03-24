package com.kefdev.zentrack.ui.timer

data class TimerUiState(
    val totalSeconds: Int,
    val remainingSeconds: Int,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false
) {
    val progress: Float
        get() = if (totalSeconds == 0) 0f else remainingSeconds / totalSeconds.toFloat()

    val displayTime: String
        get() {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            return "%02d:%02d".format(minutes, seconds)
        }
}

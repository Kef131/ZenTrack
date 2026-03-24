package com.kefdev.zentrack.ui.timer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kefdev.zentrack.ui.theme.ZenTrackTheme

@Composable
fun TimerScreen(
    onNavigateBack: () -> Unit,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                TimerUiEvent.MeditationCompleted -> Unit
            }
        }
    }

    TimerContent(
        uiState = uiState,
        onStart = viewModel::startTimer,
        onPause = viewModel::pauseTimer,
        onReset = viewModel::resetTimer,
        onBack = onNavigateBack,
        onRepeat = viewModel::resetTimer
    )
}

@Composable
private fun TimerContent(
    uiState: TimerUiState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
    onRepeat: () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = uiState.progress,
        animationSpec = tween(durationMillis = 600),
        label = "timerProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Circular timer
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(240.dp)
            ) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 10.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surface
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.displayTime,
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (uiState.isFinished) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "¡Completada!",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(56.dp))

            // Controls
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                when {
                    uiState.isFinished -> {
                        OutlinedButton(onClick = onBack) { Text("Volver") }
                        Button(onClick = onRepeat) { Text("Repetir") }
                    }
                    uiState.isRunning -> {
                        Button(onClick = onPause) { Text("Pausar") }
                    }
                    else -> {
                        val isResuming = uiState.remainingSeconds < uiState.totalSeconds
                        Button(onClick = onStart) {
                            Text(if (isResuming) "Reanudar" else "Iniciar")
                        }
                        if (isResuming) {
                            OutlinedButton(onClick = onReset) { Text("Reiniciar") }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerContentRunningPreview() {
    ZenTrackTheme {
        TimerContent(
            uiState = TimerUiState(totalSeconds = 600, remainingSeconds = 423, isRunning = true),
            onStart = {}, onPause = {}, onReset = {}, onBack = {}, onRepeat = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerContentFinishedPreview() {
    ZenTrackTheme {
        TimerContent(
            uiState = TimerUiState(totalSeconds = 600, remainingSeconds = 0, isFinished = true),
            onStart = {}, onPause = {}, onReset = {}, onBack = {}, onRepeat = {}
        )
    }
}

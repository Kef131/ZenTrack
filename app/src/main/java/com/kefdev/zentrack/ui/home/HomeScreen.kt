package com.kefdev.zentrack.ui.home

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kefdev.zentrack.domain.model.Meditation
import com.kefdev.zentrack.ui.theme.ZenTrackTheme

private val DURATION_PRESETS = listOf(5, 10, 15, 20, 30)

@Composable
fun HomeScreen(
    onNavigateToTimer: (Int) -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeUiEvent.ShowMessage ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                is HomeUiEvent.NavigateToTimer ->
                    onNavigateToTimer(event.presetMinutes)
            }
        }
    }

    HomeContent(
        uiState = uiState,
        onStartMeditation = viewModel::onStartMeditation,
        onNavigateToHistory = onNavigateToHistory
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onStartMeditation: (Int) -> Unit,
    onNavigateToHistory: () -> Unit
) {
    var selectedMinutes by rememberSaveable { mutableIntStateOf(10) }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Zen icon
        Text(
            text = "🧘",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "ZenTrack",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Stats card with animated content size
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(animationSpec = tween(durationMillis = 400))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "${uiState.totalMinutes} min",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "tiempo total meditado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${uiState.totalSessions} sesiones completadas",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "¿Cuántos minutos?",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(DURATION_PRESETS) { minutes ->
                FilterChip(
                    selected = selectedMinutes == minutes,
                    onClick = { selectedMinutes = minutes },
                    label = { Text("$minutes min") }
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = { onStartMeditation(selectedMinutes) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Comenzar Meditación")
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = onNavigateToHistory,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Ver historial completo")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    ZenTrackTheme {
        HomeContent(
            uiState = HomeUiState(
                totalMinutes = 120,
                totalSessions = 8,
                recentMeditations = listOf(
                    Meditation(1L, 15, System.currentTimeMillis()),
                    Meditation(2L, 10, System.currentTimeMillis())
                ),
                isLoading = false
            ),
            onStartMeditation = {},
            onNavigateToHistory = {}
        )
    }
}

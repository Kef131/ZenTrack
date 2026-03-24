package com.kefdev.zentrack.ui.home

import app.cash.turbine.test
import com.kefdev.zentrack.MainDispatcherRule
import com.kefdev.zentrack.data.repository.FakeMeditationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createViewModel() = HomeViewModel(FakeMeditationRepository())

    @Test
    fun `initial state has isLoading true then transitions to false once data loads`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertTrue("Expected initial state to have isLoading = true", awaitItem().isLoading)
            val loaded = awaitItem()
            assertFalse("Expected loaded state to have isLoading = false", loaded.isLoading)
            assertEquals(0, loaded.totalMinutes)
            assertEquals(0, loaded.totalSessions)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onStartMeditation with 0 minutes emits ShowMessage event`() = runTest {
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.onStartMeditation(0)
            val event = awaitItem()
            assertTrue(
                "Expected ShowMessage event, got $event",
                event is HomeUiEvent.ShowMessage
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onStartMeditation with negative minutes emits ShowMessage event`() = runTest {
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.onStartMeditation(-5)
            val event = awaitItem()
            assertTrue(event is HomeUiEvent.ShowMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onStartMeditation with 10 minutes emits NavigateToTimer with correct minutes`() = runTest {
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.onStartMeditation(10)
            val event = awaitItem()
            assertTrue(
                "Expected NavigateToTimer event, got $event",
                event is HomeUiEvent.NavigateToTimer
            )
            assertEquals(10, (event as HomeUiEvent.NavigateToTimer).presetMinutes)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

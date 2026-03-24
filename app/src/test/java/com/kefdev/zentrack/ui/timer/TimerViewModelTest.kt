package com.kefdev.zentrack.ui.timer

import androidx.lifecycle.SavedStateHandle
import com.kefdev.zentrack.MainDispatcherRule
import com.kefdev.zentrack.data.repository.FakeMeditationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createViewModel(minutes: Int = 5): TimerViewModel =
        TimerViewModel(
            repository = FakeMeditationRepository(),
            savedStateHandle = SavedStateHandle(mapOf("minutes" to minutes))
        )

    @Test
    fun `initial remainingSeconds equals presetMinutes times 60`() {
        val viewModel = createViewModel(minutes = 5)
        assertEquals(300, viewModel.uiState.value.remainingSeconds)
        assertEquals(300, viewModel.uiState.value.totalSeconds)
    }

    @Test
    fun `initial remainingSeconds for 10 minutes equals 600`() {
        val viewModel = createViewModel(minutes = 10)
        assertEquals(600, viewModel.uiState.value.remainingSeconds)
    }

    @Test
    fun `displayTime formats correctly as MM colon SS for 5 minutes`() {
        val viewModel = createViewModel(minutes = 5)
        assertEquals("05:00", viewModel.uiState.value.displayTime)
    }

    @Test
    fun `displayTime formats correctly for 10 minutes`() {
        val viewModel = createViewModel(minutes = 10)
        assertEquals("10:00", viewModel.uiState.value.displayTime)
    }

    @Test
    fun `initial isRunning is false`() {
        val viewModel = createViewModel()
        assertFalse(viewModel.uiState.value.isRunning)
    }

    @Test
    fun `initial isFinished is false`() {
        val viewModel = createViewModel()
        assertFalse(viewModel.uiState.value.isFinished)
    }

    @Test
    fun `startTimer sets isRunning to true`() = runTest {
        val viewModel = createViewModel(minutes = 1)
        viewModel.startTimer()
        advanceTimeBy(1L) // just enough to start the coroutine
        assertTrue(viewModel.uiState.value.isRunning)
    }

    @Test
    fun `remainingSeconds decrements by 1 after one second`() = runTest {
        val viewModel = createViewModel(minutes = 1)
        viewModel.startTimer()
        advanceTimeBy(1_001L)
        assertEquals(59, viewModel.uiState.value.remainingSeconds)
    }

    @Test
    fun `remainingSeconds decrements by 3 after three seconds`() = runTest {
        val viewModel = createViewModel(minutes = 1)
        viewModel.startTimer()
        advanceTimeBy(3_001L)
        assertEquals(57, viewModel.uiState.value.remainingSeconds)
    }

    @Test
    fun `pauseTimer stops the countdown`() = runTest {
        val viewModel = createViewModel(minutes = 1)
        viewModel.startTimer()
        advanceTimeBy(2_001L)
        viewModel.pauseTimer()
        val secondsAfterPause = viewModel.uiState.value.remainingSeconds
        advanceTimeBy(3_000L) // time passes but timer is paused
        assertEquals(secondsAfterPause, viewModel.uiState.value.remainingSeconds)
    }

    @Test
    fun `resetTimer restores full duration`() = runTest {
        val viewModel = createViewModel(minutes = 1)
        viewModel.startTimer()
        advanceTimeBy(5_001L)
        viewModel.resetTimer()
        assertEquals(60, viewModel.uiState.value.remainingSeconds)
        assertFalse(viewModel.uiState.value.isRunning)
    }
}

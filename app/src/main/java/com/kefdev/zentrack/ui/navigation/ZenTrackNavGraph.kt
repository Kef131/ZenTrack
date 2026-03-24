package com.kefdev.zentrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kefdev.zentrack.ui.history.HistoryScreen
import com.kefdev.zentrack.ui.home.HomeScreen
import com.kefdev.zentrack.ui.timer.TimerScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data class TimerRoute(val minutes: Int)

@Serializable
data object HistoryRoute

@Composable
fun ZenTrackNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HomeRoute) {

        composable<HomeRoute> {
            HomeScreen(
                onNavigateToTimer = { minutes -> navController.navigate(TimerRoute(minutes)) },
                onNavigateToHistory = { navController.navigate(HistoryRoute) }
            )
        }

        composable<TimerRoute> {
            TimerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable<HistoryRoute> {
            HistoryScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}

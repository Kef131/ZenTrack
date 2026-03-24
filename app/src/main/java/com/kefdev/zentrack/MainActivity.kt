package com.kefdev.zentrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kefdev.zentrack.ui.navigation.ZenTrackNavGraph
import com.kefdev.zentrack.ui.theme.ZenTrackTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZenTrackTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ZenTrackNavGraph()
                }
            }
        }
    }
}
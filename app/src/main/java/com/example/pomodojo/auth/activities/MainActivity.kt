package com.example.pomodojo.auth.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodojo.auth.screens.MainScreen
import com.example.pomodojo.auth.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    MainScreenWithViewModel()
                }
            }
        }
    }

    @Composable
    fun MainScreenWithViewModel(viewModel: MainViewModel = viewModel()) {
        val navigateToFaceScan = viewModel.navigateToFaceScan.observeAsState()
        val navigateToSpotify = viewModel.navigateToSpotify.observeAsState()
        val navigateToPomodoro = viewModel.navigateToPomodoro.observeAsState()

        navigateToFaceScan.value?.let {
           //startActivity(Intent(this@MainActivity, FaceScan::class.java))
            finish()
            viewModel.resetNavigation()
        }

        navigateToSpotify.value?.let {
            // startActivity(Intent(this@MainActivity, Spotify::class.java))
            finish()
            viewModel.resetNavigation()
        }

        navigateToPomodoro.value?.let {
            //startActivity(Intent(this@MainActivity, Pomodoro::class.java))
            finish()
            viewModel.resetNavigation()
        }

        MainScreen(viewModel = viewModel)
    }
}

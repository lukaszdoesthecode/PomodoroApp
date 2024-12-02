package com.example.pomodojo.functionality.dashboard.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodojo.functionality.pomodoro.ui.WorkTimeActivity
import com.example.pomodojo.functionality.dashboard.screens.MainScreen
import com.example.pomodojo.functionality.dashboard.viewmodel.HomeViewModel

class HomeActivity : ComponentActivity() {

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
    fun MainScreenWithViewModel(viewModel: HomeViewModel = viewModel()) {
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
            val intent = Intent(this, WorkTimeActivity::class.java)
            startActivity(intent)
            viewModel.resetNavigation()
        }

        MainScreen(viewModel = viewModel)
    }
}

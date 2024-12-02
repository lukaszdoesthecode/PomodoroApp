package com.example.pomodojo.functionality.dashboard.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodojo.core.utils.ErrorSnackBar
import com.example.pomodojo.functionality.pomodoro.ui.WorkTimeActivity
import com.example.pomodojo.functionality.dashboard.screens.MainScreen
import com.example.pomodojo.functionality.dashboard.viewmodel.HomeViewModel

/**
 * Activity that hosts the Home screen of the application.
 */
class HomeActivity : ComponentActivity() {

    /**
     * Called when the activity is starting. This is where most initialization should go.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
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

    /**
     * Composable function that observes navigation events and displays the main screen with the provided ViewModel.
     *
     * @param viewModel The ViewModel that handles the logic for the Home screen.
     */
    @Composable
    fun MainScreenWithViewModel(viewModel: HomeViewModel = viewModel()) {
        val navigateToFaceScan = viewModel.navigateToFaceScan.observeAsState()
        val navigateToSpotify = viewModel.navigateToSpotify.observeAsState()
        val navigateToPomodoro = viewModel.navigateToPomodoro.observeAsState()
        val errorMessage = viewModel.errorMessage.observeAsState()

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

        errorMessage.value?.let { (mainMessage, subMessage) ->
            ErrorSnackBar(mainMessage = mainMessage, subMessage = subMessage)
        }
    }
}
package com.example.pomodojo.functionality.dashboard.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodojo.core.utils.ErrorSnackBar
import com.example.pomodojo.functionality.pomodoro.ui.WorkTimeActivity
import com.example.pomodojo.functionality.dashboard.screens.MainScreen
import com.example.pomodojo.functionality.dashboard.viewmodel.HomeViewModel
import com.example.pomodojo.functionality.facescan.FaceScan

/**
 * Activity that hosts the Home screen of the application.
 */
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

    /**
     * Composable function that observes navigation events and displays the main screen with the provided ViewModel.
     *
     * @param viewModel The ViewModel that handles the logic for the Home screen.
     */
    @Composable
    fun MainScreenWithViewModel(viewModel: HomeViewModel = viewModel()) {
        val navigateToFaceScan = viewModel.navigateToFaceScan.observeAsState()
        val navigateToDataAnalysis = viewModel.navigateToDataAnalysis.observeAsState()
        val navigateToPomodoro = viewModel.navigateToPomodoro.observeAsState()
        val errorMessage = viewModel.errorMessage.observeAsState()

        navigateToDataAnalysis.value?.let {
            startActivity(Intent(this, FaceScan::class.java))
            finish()
            viewModel.resetNavigation()
        }

        navigateToFaceScan.value?.let {
            startActivity(Intent(this, FaceScan::class.java))
            finish()
            viewModel.resetNavigation()
        }


        navigateToPomodoro.value?.let {
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
package com.example.pomodojo.functionality.auth.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodojo.functionality.auth.screens.FollowUpScreen
import com.example.pomodojo.functionality.auth.viewmodel.FollowUpViewModel
import com.example.pomodojo.functionality.dashboard.ui.HomeActivity

/**
 * Activity that displays the Follow-Up screen for completing user account information.
 */
class FollowUpActivity : ComponentActivity() {
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
                    FollowUpScreenWithViewModel()
                }
            }
        }
    }

    /**
     * Composable function that displays the Follow-Up screen with the provided ViewModel.
     *
     * @param viewModel The ViewModel that handles the logic for the Follow-Up screen.
     */
    @Composable
    fun FollowUpScreenWithViewModel(viewModel: FollowUpViewModel = viewModel()) {
        viewModel.navigateToHome.observe(this@FollowUpActivity) {
            startActivity(Intent(this@FollowUpActivity, HomeActivity::class.java))
            finish()
        }

        FollowUpScreen(viewModel)
    }
}
package com.example.pomodojo.functionality.auth.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodojo.functionality.auth.screens.SignUpScreen
import com.example.pomodojo.functionality.auth.viewmodel.SignUpViewModel

/**
 * Activity that displays the Sign-Up screen and handles the sign-up logic.
 */
class SignUpActivity : ComponentActivity() {
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
                    SignUpScreenWithViewModel()
                }
            }
        }
    }

    /**
     * Composable function that displays the Sign-Up screen with the provided ViewModel.
     *
     * @param viewModel The ViewModel that handles the logic for the Sign-Up screen.
     */
    @Composable
    fun SignUpScreenWithViewModel(viewModel: SignUpViewModel = viewModel()) {
        viewModel.navigateToLogIn.observe(this@SignUpActivity) {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            finish()
        }

        SignUpScreen(viewModel)
    }
}
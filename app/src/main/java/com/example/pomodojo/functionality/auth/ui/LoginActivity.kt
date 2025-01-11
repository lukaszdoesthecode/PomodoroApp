package com.example.pomodojo.functionality.auth.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodojo.functionality.auth.screens.LoginScreen
import com.example.pomodojo.functionality.auth.viewmodel.LoginViewModel
import com.example.pomodojo.functionality.dashboard.ui.HomeActivity


/**
 * Activity that displays the Login screen and handles authentication logic.
 */
class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by lazy {
        androidx.lifecycle.ViewModelProvider(this)[LoginViewModel::class.java]
    }

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
                    LoginScreenWithViewModel(viewModel)
                }
            }
        }

    }

    /**
     * Composable function that displays the Login screen with the provided ViewModel.
     *
     * @param viewModel The ViewModel that handles the logic for the Login screen.
     */
    @Composable
    fun LoginScreenWithViewModel(viewModel: LoginViewModel = viewModel()) {
        viewModel.navigateToSignUp.observe(this@LoginActivity) {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            finish()
        }

        viewModel.navigateToHome.observe(this@LoginActivity) {
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
        }

        LoginScreen(viewModel)
    }
}
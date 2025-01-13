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
 * Activity that displays the Login screen and manages the authentication workflow.
 *
 * This activity initializes the [LoginViewModel] and observes navigation events
 * to transition between screens such as SignUp and Home.
 */
class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by lazy {
        androidx.lifecycle.ViewModelProvider(this)[LoginViewModel::class.java]
    }

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
     * This function observes navigation events from the [LoginViewModel] to handle
     * transitions to the SignUp screen or the Home screen based on user actions.
     *
     * @param viewModel The [LoginViewModel] that provides login logic and state.
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

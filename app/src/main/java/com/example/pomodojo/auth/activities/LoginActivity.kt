package com.example.pomodojo.auth.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodojo.activities.SignUpActivity
import com.example.pomodojo.auth.screens.LoginScreen
import com.example.pomodojo.auth.viewmodels.LoginViewModel

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    LoginScreenWithViewModel()
                }
            }
        }
    }

    @Composable
    fun LoginScreenWithViewModel(viewModel: LoginViewModel = viewModel()) {
        viewModel.navigateToSignUp.observe(this@LoginActivity) {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            finish()
        }

        viewModel.navigateToHome.observe(this@LoginActivity) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }

        LoginScreen(viewModel)
    }
}
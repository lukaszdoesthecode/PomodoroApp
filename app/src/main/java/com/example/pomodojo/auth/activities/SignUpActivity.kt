package com.example.pomodojo.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodojo.auth.activities.LoginActivity
import com.example.pomodojo.auth.screens.SignUpScreen
import com.example.pomodojo.auth.viewmodels.SignUpViewModel

class SignUpActivity : ComponentActivity() {
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

    @Composable
    fun SignUpScreenWithViewModel(viewModel: SignUpViewModel = viewModel()) {
        viewModel.navigateToLogIn.observe(this@SignUpActivity) {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))

            finish()
        }


        SignUpScreen(viewModel)
    }
}
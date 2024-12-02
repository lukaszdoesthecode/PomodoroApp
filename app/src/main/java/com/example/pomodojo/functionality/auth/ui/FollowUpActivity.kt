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


class FollowUpActivity : ComponentActivity() {
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

    @Composable
    fun FollowUpScreenWithViewModel(viewModel: FollowUpViewModel = viewModel()) {
        viewModel.navigateToHome.observe(this@FollowUpActivity) {
            startActivity(Intent(this@FollowUpActivity, HomeActivity::class.java))
            finish()
        }


        FollowUpScreen(viewModel)
    }
}

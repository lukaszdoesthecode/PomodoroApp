package com.example.pomodojo.functionality.pomodoro.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.pomodojo.ui.theme.PomodojoTheme
import com.example.pomodojo.functionality.pomodoro.screens.ShortBreakScreen

class ShortBreakActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomodojoTheme {
                ShortBreakScreen()
            }
        }
    }
}


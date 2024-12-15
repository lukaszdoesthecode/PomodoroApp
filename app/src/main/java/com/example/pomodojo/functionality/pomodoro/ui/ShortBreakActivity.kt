package com.example.pomodojo.functionality.pomodoro.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.pomodojo.ui.theme.PomodojoTheme

class ShortBreakActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomodojoTheme {
                com.example.pomodojo.functionality.pomodoro.screens.ShortBreakScreen()
            }
        }
    }
}

@Composable
fun ShortBreakScreen() {
    Text(
        text = "Short Break",
        modifier = Modifier.fillMaxSize()
    )
}
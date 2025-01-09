package com.example.pomodojo.functionality.pomodoro.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.pomodojo.ui.theme.PomodojoTheme
import com.example.pomodojo.functionality.pomodoro.screens.ShortBreakScreen

/**
 * Activity for the short break screen.
 */
class ShortBreakActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. This is where most initialization should go.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomodojoTheme {
                ShortBreakScreen(context = this)
            }
        }
    }
}
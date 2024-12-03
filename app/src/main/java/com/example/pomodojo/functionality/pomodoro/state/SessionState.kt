package com.example.pomodojo.functionality.pomodoro.state

import androidx.compose.runtime.Composable
import com.example.pomodojo.core.utils.ErrorSnackBar

enum class SessionState {
    WORK,
    SHORT_BREAK,
    LONG_BREAK,
    WAITING_FOR_USER_INPUT;

    companion object {
        @Composable
        fun getSessionStateString(sessionState: Enum<SessionState>): String {
            return when (sessionState) {
                WORK -> "Focus"
                SHORT_BREAK -> "Short Break"
                LONG_BREAK -> "Long Break"
                else -> {
                    ShowUnknownStateError()
                    "Unknown"
                }
            }
        }

        @Composable
        private fun ShowUnknownStateError() {
            ErrorSnackBar(mainMessage = "Error", subMessage = "Unknown session state encountered")
        }
    }
}
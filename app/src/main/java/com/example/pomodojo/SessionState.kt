package com.example.pomodojo

enum class SessionState {
    WORK,
    SHORT_BREAK,
    LONG_BREAK;

    companion object {
        fun getSessionStateString(sessionState: Enum<SessionState>): String {
            return when(sessionState) {
                WORK -> "Focus"
                SHORT_BREAK -> "Short Break"
                LONG_BREAK -> "Long Break"
                else -> "Unknown"
            }
        }
    }

}
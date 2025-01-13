package com.example.pomodojo.pomodoro

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pomodojo.functionality.pomodoro.state.SessionState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for the SessionState enum and its companion object methods.
 */
@RunWith(AndroidJUnit4::class)
class SessionStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Tests that the getSessionStateString method returns the correct string for each SessionState.
     */
    @Test
    fun getSessionStateString_returnsCorrectString() {
        composeTestRule.setContent {
            assertEquals("Focus", SessionState.getSessionStateString(SessionState.WORK))
            assertEquals("Short Break", SessionState.getSessionStateString(SessionState.SHORT_BREAK))
            assertEquals("Long Break", SessionState.getSessionStateString(SessionState.LONG_BREAK))
            assertEquals("Unknown", SessionState.getSessionStateString(SessionState.WAITING_FOR_USER_INPUT))
        }
    }
}
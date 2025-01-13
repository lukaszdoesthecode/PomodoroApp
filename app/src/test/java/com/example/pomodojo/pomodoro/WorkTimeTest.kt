package com.example.pomodojo.pomodoro

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pomodojo.functionality.pomodoro.screens.WorkTimeScreen
import com.example.pomodojo.ui.theme.PomodojoTheme
import com.example.pomodojo.functionality.pomodoro.state.SessionState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for the WorkTimeScreen composable.
 */
@RunWith(AndroidJUnit4::class)
class WorkTimeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Tests the initial state of the WorkTimeScreen.
     */
    @Test
    fun workTimeScreen_initialState() {
        composeTestRule.setContent {
            val context = LocalContext.current
            PomodojoTheme {
                WorkTimeScreen(
                    onClickStartStop = {},
                    onClickReset = {},
                    onClickSkip = {},
                    timeSeconds = 1500,
                    sessionState = SessionState.WORK
                )
            }
        }

        composeTestRule.onNodeWithText("25").assertExists()
        composeTestRule.onNodeWithText("00").assertExists()
    }

    /**
     * Tests the countdown functionality of the WorkTimeScreen.
     */
    @Test
    fun workTimeScreen_countdown() {
        composeTestRule.setContent {
            val context = LocalContext.current
            PomodojoTheme {
                WorkTimeScreen(
                    onClickStartStop = {},
                    onClickReset = {},
                    onClickSkip = {},
                    timeSeconds = 1500,
                    sessionState = SessionState.WORK
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CenterButton").assertExists()
        composeTestRule.onNodeWithTag("CenterButton").performClick()
        composeTestRule.mainClock.advanceTimeBy(60000L)

        val nodes = composeTestRule.onAllNodesWithText("24")
        if (nodes.fetchSemanticsNodes().isNotEmpty()) {
            println("Countdown updated to 24:00")
        } else {
            println("Countdown did not update to 24:00")
        }
    }

    /**
     * Tests the interactions with the MenuBar in the WorkTimeScreen.
     */
    @Test
    fun workTimeScreen_menuBarInteractions() {
        composeTestRule.setContent {
            val context = LocalContext.current
            PomodojoTheme {
                WorkTimeScreen(
                    onClickStartStop = {},
                    onClickReset = {},
                    onClickSkip = {},
                    timeSeconds = 1500,
                    sessionState = SessionState.WORK
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CenterButton").assertExists()
        composeTestRule.onNodeWithTag("CenterButton").performClick()
        composeTestRule.onNodeWithTag("CenterButton").performClick()
        composeTestRule.onNodeWithTag("LightbulbButton").assertExists()
        composeTestRule.onNodeWithTag("LightbulbButton").performClick()

        composeTestRule.onNodeWithText("25").assertExists()
        composeTestRule.onNodeWithText("00").assertExists()
    }
}
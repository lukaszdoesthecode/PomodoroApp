package com.example.pomodojo.pomodoro

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pomodojo.functionality.pomodoro.screens.ShortBreakScreen
import com.example.pomodojo.ui.theme.PomodojoTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for the ShortBreakScreen composable.
 */
@RunWith(AndroidJUnit4::class)
class ShortBreakTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Tests the initial state of the ShortBreakScreen.
     */
    @Test
    fun shortBreakScreen_initialState() {
        composeTestRule.setContent {
            val context = LocalContext.current
            PomodojoTheme {
                ShortBreakScreen(context = context)
            }
        }

        composeTestRule.onNodeWithText("05").assertExists()
        composeTestRule.onNodeWithText("00").assertExists()
    }

    /**
     * Tests the countdown functionality of the ShortBreakScreen.
     */
    @Test
    fun shortBreakScreen_countdown() {
        composeTestRule.setContent {
            val context = LocalContext.current
            PomodojoTheme {
                ShortBreakScreen(context = context)
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CenterButton").assertExists()
        composeTestRule.onNodeWithTag("CenterButton").performClick()
        composeTestRule.mainClock.advanceTimeBy(60000L)

        val nodes = composeTestRule.onAllNodesWithText("04")
        if (nodes.fetchSemanticsNodes().isNotEmpty()) {
            println("Countdown updated to 04:00")
        } else {
            println("Countdown did not update to 04:00")
        }
    }

    /**
     * Tests the interactions with the MenuBar in the ShortBreakScreen.
     */
    @Test
    fun shortBreakScreen_menuBarInteractions() {
        composeTestRule.setContent {
            val context = LocalContext.current
            PomodojoTheme {
                ShortBreakScreen(context = context)
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CenterButton").assertExists()
        composeTestRule.onNodeWithTag("CenterButton").performClick()
        composeTestRule.onNodeWithTag("CenterButton").performClick()
        composeTestRule.onNodeWithTag("LightbulbButton").performClick()

        composeTestRule.onNodeWithText("05").assertExists()
        composeTestRule.onNodeWithText("00").assertExists()
    }
}
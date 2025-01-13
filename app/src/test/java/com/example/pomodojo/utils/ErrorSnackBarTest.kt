package com.example.pomodojo.utils

import android.os.Build
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import com.example.pomodojo.core.utils.ErrorSnackBar
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for the ErrorSnackBar composable function.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ErrorSnackBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Sets up the test environment before each test.
     */
    @Before
    fun setUp() {
        val buildClass = Build::class.java
        val field = buildClass.getDeclaredField("FINGERPRINT")
        field.isAccessible = true
        field.set(null, "test_fingerprint")
    }

    /**
     * Tests that the ErrorSnackBar displays the correct messages.
     */
    @Test
    fun errorSnackBar_DisplaysCorrectMessages() {
        val mainMessage = "Error occurred"
        val subMessage = "Please try again"

        composeTestRule.setContent {
            ErrorSnackBar(mainMessage = mainMessage, subMessage = subMessage)
        }

        composeTestRule.onNodeWithText(mainMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText(subMessage).assertIsDisplayed()
    }

    /**
     * Tests that the ErrorSnackBar handles empty messages.
     */
    @Test
    fun errorSnackBar_HandlesEmptyMessages() {
        val mainMessage = ""
        val subMessage = ""

        composeTestRule.setContent {
            ErrorSnackBar(mainMessage = mainMessage, subMessage = subMessage)
        }

        composeTestRule.onNodeWithText(mainMessage).assertDoesNotExist()
        composeTestRule.onNodeWithText(subMessage).assertDoesNotExist()
    }

    /**
     * Tests that the ErrorSnackBar displays long messages correctly.
     */
    @Test
    fun errorSnackBar_DisplaysLongMessages() {
        val mainMessage = "This is a very long error message that should be displayed correctly without any issues."
        val subMessage = "This is a very long sub-message that provides additional context about the error and should also be displayed correctly."

        composeTestRule.setContent {
            ErrorSnackBar(mainMessage = mainMessage, subMessage = subMessage)
        }

        composeTestRule.onNodeWithText(mainMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText(subMessage).assertIsDisplayed()
    }

    /**
     * Tests that the ErrorSnackBar handles special characters in messages.
     */
    @Test
    fun errorSnackBar_HandlesSpecialCharacters() {
        val mainMessage = "Error: Something went wrong! @#\$%^&*()"
        val subMessage = "Please try again later. ~`<>?/|\\"

        composeTestRule.setContent {
            ErrorSnackBar(mainMessage = mainMessage, subMessage = subMessage)
        }

        composeTestRule.onNodeWithText(mainMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText(subMessage).assertIsDisplayed()
    }
}
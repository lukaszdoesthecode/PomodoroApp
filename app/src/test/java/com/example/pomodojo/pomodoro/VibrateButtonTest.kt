package com.example.pomodojo.pomodoro

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pomodojo.functionality.pomodoro.components.VibrateButton
import com.example.pomodojo.ui.theme.PomodojoTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

/**
 * Unit tests for the VibrateButton composable.
 */
@RunWith(AndroidJUnit4::class)
class VibrateButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Tests that the VibrateButton exists in the UI.
     */
    @Test
    fun vibrateButton_exists() {
        composeTestRule.setContent {
            PomodojoTheme {
                VibrateButton()
            }
        }

        composeTestRule.onNodeWithText("Test Vibration").assertExists()
    }
}
// CustomSnackBarTest.kt
package com.example.pomodojo.utils

import android.os.Build
import android.view.View
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import com.example.pomodojo.core.utils.CustomSnackBar
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Unit tests for the CustomSnackBar composable function.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class CustomSnackBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var rootView: View

    /**
     * Sets up the test environment before each test.
     */
    @Before
    fun setUp() {
        val buildClass = Build::class.java
        val field = buildClass.getDeclaredField("FINGERPRINT")
        field.isAccessible = true
        field.set(null, "test_fingerprint")

        val activity = Robolectric.buildActivity(TestActivity::class.java).setup().get()
        rootView = activity.findViewById(android.R.id.content)
    }

    /**
     * Tests that the CustomSnackBar handles empty messages.
     */
    @Test
    fun customSnackBar_HandlesEmptyMessages() {
        val mainMessage = ""
        val subMessage = ""

        composeTestRule.setContent {
            CustomSnackBar.showSnackBar(rootView, mainMessage, subMessage)
        }

        composeTestRule.onNodeWithText(mainMessage).assertDoesNotExist()
        composeTestRule.onNodeWithText(subMessage).assertDoesNotExist()
    }
}

/**
 * A test activity used for unit testing.
 */
class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(android.R.layout.simple_list_item_1)
    }
}
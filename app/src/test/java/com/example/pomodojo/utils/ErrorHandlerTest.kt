package com.example.pomodojo.utils

import android.view.View
import android.widget.FrameLayout
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.pomodojo.R
import com.example.pomodojo.core.utils.ErrorHandler
import com.google.android.material.textfield.TextInputLayout
import io.mockk.*
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for the ErrorHandler class.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = android.app.Application::class)
class ErrorHandlerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var rootLayout: FrameLayout
    private lateinit var realView: View
    private lateinit var realNameLayout: TextInputLayout
    private lateinit var realDobLayout: TextInputLayout
    private lateinit var realEmailLayout: TextInputLayout
    private lateinit var realPasswordLayout: TextInputLayout
    private lateinit var realRepeatLayout: TextInputLayout

    /**
     * Sets up the test environment before each test.
     */
    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        context.setTheme(R.style.AppTheme)

        rootLayout = FrameLayout(context)
        realView = View(rootLayout.context)
        rootLayout.addView(realView)

        realNameLayout = TextInputLayout(rootLayout.context)
        realDobLayout = TextInputLayout(rootLayout.context)
        realEmailLayout = TextInputLayout(rootLayout.context)
        realPasswordLayout = TextInputLayout(rootLayout.context)
        realRepeatLayout = TextInputLayout(rootLayout.context)

        mockkObject(ErrorHandler)
    }

    /**
     * Cleans up the test environment after each test.
     */
    @After
    fun tearDown() {
        unmockkAll()
    }

    /**
     * Tests that the resetErrorStates function resets the error states correctly.
     */
    @Test
    fun testResetErrorStates() {
        ErrorHandler.resetErrorStates(realNameLayout, realDobLayout)

        Assert.assertEquals(
            realView.context.getColor(R.color.white),
            realNameLayout.boxStrokeColor
        )
        Assert.assertEquals(
            realView.context.getColor(R.color.white),
            realDobLayout.boxStrokeColor
        )
    }

    /**
     * Tests that the validateSignUpFields function validates the sign-up fields correctly.
     */
    @Test
    fun testValidateSignUpFields() {
        composeTestRule.setContent {
            val result = ErrorHandler.validateSignUpFields(
                rootLayout, "John Doe", "1990/01/01", "test@example.com", "Password123!", "Password123!",
                realNameLayout, realDobLayout, realEmailLayout, realPasswordLayout, realRepeatLayout
            )
            Assert.assertTrue(result)
        }
    }

    /**
     * Tests that the validateDateOfBirth function validates the date of birth correctly.
     */
    @Test
    fun testValidateDateOfBirth() {
        composeTestRule.setContent {
            ErrorHandler.validateDateOfBirth(rootLayout, "1990/01/01", realDobLayout)
            Assert.assertNotEquals(
                realView.context.getColor(R.color.error),
                realDobLayout.boxStrokeColor
            )
        }
    }

    /**
     * Tests that the validateFields function validates the email and password fields correctly.
     */
    @Test
    fun testValidateFields() {
        composeTestRule.setContent {
            val result = ErrorHandler.validateFields(rootLayout, realEmailLayout, realPasswordLayout, "test@example.com", "Password123!")
            Assert.assertTrue(result)
        }
    }

    /**
     * Tests that the showSuccessMessage function displays a success message correctly.
     */
    @Test
    fun testShowSuccessMessage() {
        composeTestRule.setContent {
            ErrorHandler.showSuccessMessage(rootLayout, "Success", "Account created successfully")
        }
    }

    /**
     * Tests that the showPasswordResetSuccess function displays a password reset success message correctly.
     */
    @Test
    fun testShowPasswordResetSuccess() {
        composeTestRule.setContent {
            ErrorHandler.showPasswordResetSuccess(rootLayout)
        }
    }

    /**
     * Tests that the showPasswordResetFailedError function displays a password reset failed error message correctly.
     */
    @Test
    fun testShowPasswordResetFailedError() {
        composeTestRule.setContent {
            ErrorHandler.showPasswordResetFailedError(rootLayout)
        }
    }

    /**
     * Tests that the showGoogleSignInFailedError function displays a Google sign-in failed error message correctly.
     */
    @Test
    fun testShowGoogleSignInFailedError() {
        composeTestRule.setContent {
            ErrorHandler.showGoogleSignInFailedError(rootLayout)
        }
    }

    /**
     * Tests that the showGoogleAuthenticationFailedError function displays a Google authentication failed error message correctly.
     */
    @Test
    fun testShowGoogleAuthenticationFailedError() {
        composeTestRule.setContent {
            ErrorHandler.showGoogleAuthenticationFailedError(rootLayout)
        }
    }

    /**
     * Tests that the showAuthenticationFailedError function displays an authentication failed error message correctly.
     */
    @Test
    fun testShowAuthenticationFailedError() {
        composeTestRule.setContent {
            ErrorHandler.showAuthenticationFailedError(rootLayout)
        }
    }

    /**
     * Tests that the showInitializationError function displays an initialization error message correctly.
     */
    @Test
    fun testShowInitializationError() {
        composeTestRule.setContent {
            ErrorHandler.showInitializationError(rootLayout, "Initialization Error", "Failed to initialize Firebase")
        }
    }

    /**
     * Tests that the showMissingFieldError function displays a missing field error message correctly.
     */
    @Test
    fun testShowMissingFieldError() {
        composeTestRule.setContent {
            ErrorHandler.showMissingFieldError(rootLayout, "email", realEmailLayout)
        }
    }

    /**
     * Tests that the showGeneralError function displays a general error message correctly.
     */
    @Test
    fun testShowGeneralError() {
        composeTestRule.setContent {
            ErrorHandler.showGeneralError(rootLayout, "General Error", "An unexpected error occurred")
        }
    }
}
package com.example.pomodojo.utils

import android.view.View
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.example.pomodojo.R
import com.example.pomodojo.core.utils.ErrorHandler
import com.example.pomodojo.core.utils.ErrorSnackBar
import com.google.android.material.textfield.TextInputLayout
import io.mockk.*
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Test class for ErrorHandler.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = android.app.Application::class)
class ErrorHandlerTest {

    private lateinit var rootLayout: FrameLayout
    private lateinit var realView: View
    private lateinit var realNameLayout: TextInputLayout
    private lateinit var realDobLayout: TextInputLayout
    private lateinit var realEmailLayout: TextInputLayout
    private lateinit var realPasswordLayout: TextInputLayout
    private lateinit var realRepeatLayout: TextInputLayout

    /**
     * Sets up the test environment.
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

        mockkObject(ErrorSnackBar)
    }

    /**
     * Tears down the test environment.
     */
    @After
    fun tearDown() {
        unmockkAll()
    }

    /**
     * Tests the resetErrorStates function.
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
     * Tests the validateSignUpFields function.
     */
    @Test
    fun testValidateSignUpFields() {
        val result = ErrorHandler.validateSignUpFields(
            rootLayout, "John Doe", "1990/01/01", "test@example.com", "Password123!", "Password123!",
            realNameLayout, realDobLayout, realEmailLayout, realPasswordLayout, realRepeatLayout
        )
        Assert.assertTrue(result)
    }

    /**
     * Tests the validateDateOfBirth function.
     */
    @Test
    fun testValidateDateOfBirth() {
        ErrorHandler.validateDateOfBirth(rootLayout, "1990/01/01", realDobLayout)
        Assert.assertNotEquals(
            realView.context.getColor(R.color.error),
            realDobLayout.boxStrokeColor
        )
    }

    /**
     * Tests the validateFields function.
     */
    @Test
    fun testValidateFields() {
        val result = ErrorHandler.validateFields(rootLayout, realEmailLayout, realPasswordLayout, "test@example.com", "Password123!")
        Assert.assertTrue(result)
    }

    /**
     * Tests the showSuccessMessage function.
     */
    @Test
    fun testShowSuccessMessage() {
        every { ErrorSnackBar.showErrorSnackBar(any(), any(), any()) } just Runs

        ErrorHandler.showSuccessMessage(rootLayout, "Success", "Account created successfully")

        verify { ErrorSnackBar.showErrorSnackBar(rootLayout, "Success", "Account created successfully") }
    }

    /**
     * Tests the showPasswordResetSuccess function.
     */
    @Test
    fun testShowPasswordResetSuccess() {
        every { ErrorSnackBar.showErrorSnackBar(any(), any(), any()) } just Runs

        ErrorHandler.showPasswordResetSuccess(rootLayout)

        verify { ErrorSnackBar.showErrorSnackBar(rootLayout, "Password Reset Email Sent", "Check your email to reset your password") }
    }

    /**
     * Tests the showPasswordResetFailedError function.
     */
    @Test
    fun testShowPasswordResetFailedError() {
        every { ErrorSnackBar.showErrorSnackBar(any(), any(), any()) } just Runs

        ErrorHandler.showPasswordResetFailedError(rootLayout)

        verify { ErrorSnackBar.showErrorSnackBar(rootLayout, "Reset Email Failed", "There was an error sending the reset email. Please try again.") }
    }

    /**
     * Tests the showGoogleSignInFailedError function.
     */
    @Test
    fun testShowGoogleSignInFailedError() {
        every { ErrorSnackBar.showErrorSnackBar(any(), any(), any()) } just Runs

        ErrorHandler.showGoogleSignInFailedError(rootLayout)

        verify { ErrorSnackBar.showErrorSnackBar(rootLayout, "Google Sign-In Failed", "There was an error during Google sign-in. Please try again.") }
    }

    /**
     * Tests the showGoogleAuthenticationFailedError function.
     */
    @Test
    fun testShowGoogleAuthenticationFailedError() {
        every { ErrorSnackBar.showErrorSnackBar(any(), any(), any()) } just Runs

        ErrorHandler.showGoogleAuthenticationFailedError(rootLayout)

        verify { ErrorSnackBar.showErrorSnackBar(rootLayout, "Google Authentication Failed", "Failed to authenticate with Google. Please try again.") }
    }

    /**
     * Tests the showAuthenticationFailedError function.
     */
    @Test
    fun testShowAuthenticationFailedError() {
        every { ErrorSnackBar.showErrorSnackBar(any(), any(), any()) } just Runs

        ErrorHandler.showAuthenticationFailedError(rootLayout)

        verify { ErrorSnackBar.showErrorSnackBar(rootLayout, "Authentication Failed", "Please check your credentials and try again") }
    }

    /**
     * Tests the showInitializationError function.
     */
    @Test
    fun testShowInitializationError() {
        every { ErrorSnackBar.showErrorSnackBar(any(), any(), any()) } just Runs

        ErrorHandler.showInitializationError(rootLayout, "Initialization Error", "Failed to initialize Firebase")

        verify { ErrorSnackBar.showErrorSnackBar(rootLayout, "Initialization Error", "Failed to initialize Firebase") }
    }

    /**
     * Tests the showMissingFieldError function.
     */
    @Test
    fun testShowMissingFieldError() {
        every { ErrorSnackBar.showErrorSnackBar(any(), any(), any()) } just Runs

        ErrorHandler.showMissingFieldError(rootLayout, "email", realEmailLayout)

        verify { ErrorSnackBar.showErrorSnackBar(rootLayout, "Missing Information", "Please fill out your email information") }
        Assert.assertEquals(
            realView.context.getColor(R.color.error),
            realEmailLayout.boxStrokeColor
        )
    }

    /**
     * Tests the showGeneralError function.
     */
    @Test
    fun testShowGeneralError() {
        every { ErrorSnackBar.showErrorSnackBar(any(), any(), any()) } just Runs

        ErrorHandler.showGeneralError(rootLayout, "General Error", "An unexpected error occurred")

        verify { ErrorSnackBar.showErrorSnackBar(rootLayout, "General Error", "An unexpected error occurred") }
    }
}
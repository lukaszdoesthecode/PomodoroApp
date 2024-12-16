@file:Suppress("DEPRECATION")

package com.example.pomodojo.core.utils

import android.annotation.SuppressLint
import android.view.View
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import com.example.pomodojo.R
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * ErrorHandler class centralizes error handling, including displaying error messages
 * and validation logic for input fields in the app.
 */
object ErrorHandler {

    /**
     * Resets error states for the provided TextInputLayouts.
     */
    fun resetErrorStates(vararg layouts: TextInputLayout) {
        layouts.forEach { layout ->
            layout.boxStrokeColor = layout.context.resources.getColor(R.color.white)
        }
    }

    /**
     * Validates the input fields for the SignUp activity, setting errors where necessary.
     *
     * @return true if all fields pass validation, false otherwise.
     */
    @Composable
    fun validateSignUpFields(
        view: View, name: String, dob: String, email: String, password: String, repeatPassword: String,
        nameLayout: TextInputLayout, dobLayout: TextInputLayout, emailLayout: TextInputLayout,
        passwordLayout: TextInputLayout, repeatLayout: TextInputLayout
    ): Boolean {
        return when {
            name.isBlank() -> {
                showError(view, nameLayout, "Fill out your name information")
                false
            }
            dob.isBlank() -> {
                showError(view, dobLayout, "Fill out your date of birth information")
                false
            }
            email.isBlank() -> {
                showError(view, emailLayout, "Fill out your email information")
                false
            }
            password.isBlank() -> {
                showError(view, passwordLayout, "Fill out your password information")
                false
            }
            repeatPassword.isBlank() -> {
                showError(view, repeatLayout, "Fill out your password confirmation")
                false
            }
            password.length < 6 -> {
                showError(view, passwordLayout, "Password must be at least 6 characters long")
                false
            }
            !password.any { it.isUpperCase() } -> {
                showError(view, passwordLayout, "Password must contain an uppercase letter")
                false
            }
            !password.any { it.isDigit() } -> {
                showError(view, passwordLayout, "Password must contain a number")
                false
            }
            password.none { !it.isLetterOrDigit() } -> {
                showError(view, passwordLayout, "Password must contain a special character")
                false
            }
            password != repeatPassword -> {
                showError(view, repeatLayout, "Passwords must match")
                false
            }
            else -> true
        }
    }

    /**
     * Validates the date of birth input, showing errors if the date is in the future
     * or in an incorrect format.
     */
    @Composable
    fun validateDateOfBirth(view: View, date: String, dobLayout: TextInputLayout) {
        val parts = date.split("/")
        if (parts.size != 3) {
            showError(view, dobLayout, "Invalid date format (YYYY/MM/DD required)")
            return
        }

        val (year, month, day) = try {
            Triple(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        } catch (e: NumberFormatException) {
            showError(view, dobLayout, "Date contains invalid numbers")
            return
        }

        if (month !in 1..12) {
            showError(view, dobLayout, "Month must be between 1 and 12")
            return
        }

        val calendar = Calendar.getInstance().apply { set(year, month - 1, day) }
        if (calendar.get(Calendar.YEAR) != year || calendar.get(Calendar.MONTH) != month - 1 || calendar.get(Calendar.DAY_OF_MONTH) != day) {
            showError(view, dobLayout, "Invalid date values")
            return
        }

        if (calendar.after(Calendar.getInstance())) {
            showError(view, dobLayout, "Date cannot be in the future")
        }
    }

    /**
     * Validates the email and password fields for the LogIn activity.
     *
     * @return true if both fields pass validation, false otherwise.
     */
    @Composable
    fun validateFields(view: View, emailLayout: TextInputLayout, passwordLayout: TextInputLayout, email: String, password: String): Boolean {
        resetErrorStates(emailLayout, passwordLayout)

        if (email.isEmpty()) {
            showMissingFieldError(view, "email", emailLayout)
            return false
        } else if (password.isEmpty()) {
            showMissingFieldError(view, "password", passwordLayout)
            return false
        }
        return true
    }

    /**
     * Shows a success message for successful actions such as account creation.
     *
     * @param view The view to anchor the ErrorSnackBar.
     * @param mainMessage The main success message.
     * @param subMessage The secondary message providing additional context.
     */
    @Composable
    fun showSuccessMessage(view: View, mainMessage: String, subMessage: String) {
        showSnackbar(view, mainMessage, subMessage)
    }

    /**
     * Shows a password reset success message, indicating the email was sent successfully.
     *
     * @param view The view to anchor the ErrorSnackBar.
     */
    @Composable
    fun showPasswordResetSuccess(view: View) {
        showSnackbar(view, "Password Reset Email Sent", "Check your email to reset your password")
    }

    /**
     * Shows a password reset failure message when the email fails to send.
     *
     * @param view The view to anchor the ErrorSnackBar.
     */
    @Composable
    fun showPasswordResetFailedError(view: View) {
        showSnackbar(view, "Reset Email Failed", "There was an error sending the reset email. Please try again.")
    }

    /**
     * Shows a Google Sign-In failure message when the sign-in process encounters an error.
     *
     * @param view The view to anchor the ErrorSnackBar.
     */
    @Composable
    fun showGoogleSignInFailedError(view: View) {
        showSnackbar(view, "Google Sign-In Failed", "There was an error during Google sign-in. Please try again.")
    }

    /**
     * Shows a Google authentication failure message after signing in with Google credentials.
     *
     * @param view The view to anchor the ErrorSnackBar.
     */
    @Composable
    fun showGoogleAuthenticationFailedError(view: View) {
        showSnackbar(view, "Google Authentication Failed", "Failed to authenticate with Google. Please try again.")
    }

    /**
     * Shows an authentication failure message, used when Firebase authentication fails.
     *
     * @param view The view to anchor the ErrorSnackBar.
     */
    @Composable
    fun showAuthenticationFailedError(view: View) {
        showSnackbar(view, "Authentication Failed", "Please check your credentials and try again")
    }

    /**
     * Shows an initialization error message, such as when loading Firebase or other services.
     *
     * @param view The view to anchor the ErrorSnackBar.
     * @param mainMessage The primary error message.
     * @param subMessage Additional information about the initialization error.
     */
    @Composable
    fun showInitializationError(view: View, mainMessage: String, subMessage: String) {
        showSnackbar(view, mainMessage, subMessage)
    }

    /**
     * Shows a missing field error with a specific message for required fields.
     *
     * @param view The view to anchor the ErrorSnackBar.
     * @param fieldName The name of the missing field.
     * @param layout The TextInputLayout to highlight as an error.
     */
    @Composable
    fun showMissingFieldError(view: View, fieldName: String, layout: TextInputLayout) {
        showSnackbar(view, "Missing Information", "Please fill out your $fieldName information")
        layout.boxStrokeColor = view.context.resources.getColor(R.color.error)
    }

    /**
     * Handles other general errors by showing an ErrorSnackBar with the provided messages.
     *
     * @param view The view to anchor the ErrorSnackBar.
     * @param mainMessage The primary message describing the error.
     * @param subMessage The additional message describing further details.
     */
    @Composable
    fun showGeneralError(view: View, mainMessage: String, subMessage: String) {
        showSnackbar(view, mainMessage, subMessage)
    }

    /**
     * Displays an error message using the ErrorSnackBar and updates the box stroke color to red.
     *
     * @param view The view to anchor the ErrorSnackBar.
     * @param layout The TextInputLayout to display the error.
     * @param message The error message to display.
     */
    @Composable
    private fun showError(view: View, layout: TextInputLayout, message: String) {
        showSnackbar(view, "Input Error", message)
        layout.boxStrokeColor = layout.context.resources.getColor(R.color.error)
    }

    @Composable
    fun showErrorMessage(view: View, mainMessage: String, subMessage: String) {
        val parentView = view.rootView.findViewById<View>(android.R.id.content)
        showSnackbar(parentView, mainMessage, subMessage)
    }

    /**
     * Displays a Snackbar using Compose.
     *
     * @param view The view to anchor the Snackbar.
     * @param mainMessage The primary message describing the error.
     * @param subMessage The additional message describing further details.
     */
    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    private fun showSnackbar(view: View, mainMessage: String, subMessage: String) {
        val snackbarHostState = remember { SnackbarHostState() }
        (view as? ComposeView)?.setContent {
            SnackbarHost(hostState = snackbarHostState)
        }
        (view.context as? androidx.fragment.app.FragmentActivity)?.lifecycleScope?.launch {
            snackbarHostState.showSnackbar("$mainMessage: $subMessage")
        }
    }
}
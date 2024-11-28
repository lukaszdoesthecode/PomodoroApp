package com.example.pomodojo.auth

import android.content.Intent
import android.widget.EditText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pomodojo.utils.ErrorHandler
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import org.mockito.kotlin.anyOrNull
import com.example.pomodojo.R

/**
 * Unit tests for the LogIn activity.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
class LogInTest {

    private lateinit var logInActivity: LogIn
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText

    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockGoogleSignInClient: GoogleSignInClient
    private lateinit var mockErrorHandler: ErrorHandler

    /**
     * Sets up the test environment before each test.
     */
    @Before
    fun setUp() {
        mockAuth = mock(FirebaseAuth::class.java)
        mockGoogleSignInClient = mock(GoogleSignInClient::class.java)
        mockErrorHandler = mock(ErrorHandler::class.java)

        logInActivity = Robolectric.buildActivity(LogIn::class.java).create().get()
        logInActivity.auth = mockAuth
        logInActivity.google = mockGoogleSignInClient

        emailField = EditText(logInActivity)
        passwordField = EditText(logInActivity)

        logInActivity.email = emailField
        logInActivity.password = passwordField
    }

    /**
     * Sets the text of an EditText.
     *
     * @param editText The EditText to set the text for.
     * @param text The text to set.
     */
    private fun setEditText(editText: EditText, text: String) {
        editText.setText(text)
    }

    /**
     * Tests that validate() returns true when email and password fields are filled.
     */
    @Test
    fun validate_shouldReturnTrue_whenEmailAndPasswordAreFilled() {
        setEditText(emailField, "test@example.com")
        setEditText(passwordField, "password123")
        Assert.assertTrue(logInActivity.validate())
    }

    /**
     * Tests that validate() returns false when the password field is empty.
     */
    @Test
    fun validate_shouldReturnFalse_whenPasswordIsEmpty() {
        setEditText(emailField, "test@example.com")
        setEditText(passwordField, "")
        Assert.assertFalse(logInActivity.validate())
    }

    /**
     * Tests that loginUser() authenticates with Firebase when email and password are correct.
     */
    @Test
    fun loginUser_shouldAuthenticate_whenEmailAndPasswordAreCorrect() {
        setEditText(emailField, "test@example.com")
        setEditText(passwordField, "password123")

        `when`(mockAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(mock(Task::class.java) as Task<AuthResult>)
        logInActivity.loginUser()
        verify(mockAuth).signInWithEmailAndPassword("test@example.com", "password123")
    }

    /**
     * Tests that forgotPassword() sends a password reset email when the email is valid.
     */
    @Test
    fun forgotPassword_shouldSendPasswordResetEmail_whenEmailIsValid() {
        setEditText(emailField, "test@example.com")

        `when`(mockAuth.sendPasswordResetEmail(anyString())).thenReturn(mock(Task::class.java) as Task<Void>)
        logInActivity.forgotPassword()
        verify(mockAuth).sendPasswordResetEmail("test@example.com")
    }
}
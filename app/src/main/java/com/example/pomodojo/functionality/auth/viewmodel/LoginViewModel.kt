package com.example.pomodojo.functionality.auth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

/**
 * ViewModel for handling the logic of the Login screen.
 *
 * This ViewModel manages user authentication using FirebaseAuth, provides navigation events
 * for transitioning to different screens, and handles error reporting.
 *
 * @param application The application context.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _navigateToHome = MutableLiveData<Unit>()
    val navigateToHome: LiveData<Unit> = _navigateToHome

    private val _navigateToSignUp = MutableLiveData<Unit>()
    val navigateToSignUp: LiveData<Unit> = _navigateToSignUp
    private val _errorMessage = MutableLiveData<Pair<String, String>>()

    /**
     * Navigates to the Main Page screen.
     */
    fun navigateToHome() {
        _navigateToHome.postValue(Unit)
    }

    /**
     * Sends a password reset email to the provided email address.
     * If the email address is empty, an error message is posted.
     *
     * @param email The email address to send the password reset email to.
     */
    fun forgotPassword(email: String) {
        if (email.isEmpty()) {
            _errorMessage.postValue(Pair("Error", "Please enter your email address."))
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { sent ->
                if (sent.isSuccessful) {
                    _errorMessage.postValue(Pair("Success", "Password reset email sent."))
                } else {
                    _errorMessage.postValue(Pair("Error", "Error in sending reset email."))
                }
            }
    }

    /**
     * Logs in the user with the provided email and password.
     *
     * If the email or password is empty, an error message is posted. Otherwise, it attempts
     * to sign in the user using FirebaseAuth. On successful login, a navigation set to go
     * to the Home screen. On failure, an error message is posted.
     *
     * @param email The email address of the user.
     * @param password The password of the user.
     */
    fun loginUser(email: String, password: String) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || password.isBlank()) {
            _errorMessage.postValue(Pair("Error", "Email and password cannot be empty."))
            return
        }

        auth.signInWithEmailAndPassword(trimmedEmail, password)
            .addOnCompleteListener { login ->
                if (login.isSuccessful) {
                    _navigateToHome.postValue(Unit)
                } else {
                    _errorMessage.postValue(Pair("Error", "Authentication failed: ${login.exception?.localizedMessage}"))
                }
            }
    }

    /**
     * Navigates to the SignUp screen.
     */
    fun navigateToSignUp() {
        _navigateToSignUp.postValue(Unit)
    }
}

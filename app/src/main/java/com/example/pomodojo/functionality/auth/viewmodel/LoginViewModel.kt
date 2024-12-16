package com.example.pomodojo.functionality.auth.viewmodel

import android.app.Application
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pomodojo.R
import com.example.pomodojo.core.utils.ErrorSnackBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn

/**
 * ViewModel for handling the logic of the Login screen.
 *
 * @param application The application context.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _navigateToHome = MutableLiveData<Unit>()
    val navigateToHome: LiveData<Unit> = _navigateToHome

    private val _navigateToSignUp = MutableLiveData<Unit>()
    val navigateToSignUp: LiveData<Unit> = _navigateToSignUp

    private val _navigateToFollowUp = MutableLiveData<Unit>()
    val navigateToFollowUp: LiveData<Unit> = _navigateToFollowUp

    private val _googleSignInIntent = MutableLiveData<Intent>()
    val googleSignInIntent: LiveData<Intent> = _googleSignInIntent

    private val _errorMessage = MutableLiveData<Pair<String, String>>()
    val errorMessage: LiveData<Pair<String, String>> = _errorMessage

    /**
     * Initiates the Google login process by creating a sign-in intent.
     */
    fun googleLogin() {
        val context = getApplication<Application>()
        val clientId = context.getString(R.string.default_web_client_id)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        val signInIntent = googleSignInClient.signInIntent

        _googleSignInIntent.postValue(signInIntent)
    }


    /**
     * Navigates to the Home screen without logging in. Only to be used for testing purposes.
     */
    fun navigateToHome() {
    _navigateToHome.postValue(Unit)
}

    /**
     * Handles the result of the Google sign-in process.
     *
     * @param account The GoogleSignInAccount obtained from the sign-in process.
     */
    fun handleGoogleSignInResult(account: GoogleSignInAccount?) {
        if (account == null) {
            _errorMessage.postValue(Pair("Error", "Google sign-in failed."))
            return
        }

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: false

                    if (isNewUser) {
                        _navigateToFollowUp.postValue(Unit)
                    } else {
                        _navigateToHome.postValue(Unit)
                    }
                } else {
                    _errorMessage.postValue(Pair("Error", task.exception?.localizedMessage ?: "Unknown error"))
                }
            }
    }

    /**
     * Sends a password reset email to the provided email address.
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
     * Navigates to the Sign-Up screen.
     */
    fun navigateToSignUp() {
        _navigateToSignUp.postValue(Unit)
    }
}


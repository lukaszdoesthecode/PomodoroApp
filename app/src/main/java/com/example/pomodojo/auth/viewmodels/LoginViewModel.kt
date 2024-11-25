@file:Suppress("DEPRECATION")

package com.example.pomodojo.auth.viewmodels

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pomodojo.R
import com.example.pomodojo.utils.ErrorSnackBar
import com.example.pomodojo.utils.SnackBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn

@Suppress("DEPRECATION", "INFERRED_TYPE_VARIABLE_INTO_EMPTY_INTERSECTION_WARNING")
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


    fun handleGoogleSignInResult(account: GoogleSignInAccount?) {
        if (account == null) {
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
                    ErrorSnackBar.showErrorSnackBar(getApplication(), "Error",task.exception?.localizedMessage ?: "Unknown error")
                }
            }
    }

    fun forgotPassword(email: String) {
        if (email.isEmpty()) {
            ErrorSnackBar.showErrorSnackBar(getApplication(), "Error","Please enter your email address.")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { sent ->
                if (sent.isSuccessful) {
                      SnackBar.showSnackBar(getApplication(), "Success","Password reset email sent.")
                } else {
                    ErrorSnackBar.showErrorSnackBar(getApplication(), "Error","Error in sending reset email.")
                }
            }
    }
    fun loginUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            ErrorSnackBar.showErrorSnackBar(getApplication(), "Error.","Email and password cannot be empty.")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { login ->
                if (login.isSuccessful) {
                    _navigateToHome.postValue(Unit)
                } else {
                    ErrorSnackBar.showErrorSnackBar(getApplication(), "Error","Authentication failed: ${login.exception?.localizedMessage}")
                }
            }
    }

    fun navigateToSignUp() {
        _navigateToSignUp.postValue(Unit)
    }

}

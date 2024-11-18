package com.example.pomodojo.auth.viewmodels

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _navigateToHome = MutableLiveData<Unit>()
    val navigateToHome: LiveData<Unit> = _navigateToHome

    private val _navigateToSignUp = MutableLiveData<Unit>()
    val navigateToSignUp: LiveData<Unit> = _navigateToSignUp

    private val _navigateToFaceScan = MutableLiveData<Unit>()
    val navigateToFaceScan: LiveData<Unit> = _navigateToFaceScan

    private val _googleSignInIntent = MutableLiveData<Intent>()
    val googleSignInIntent: LiveData<Intent> = _googleSignInIntent

    fun googleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("YOUR_WEB_CLIENT_ID")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(getApplication(), gso)
        val signInIntent = googleSignInClient.signInIntent

        _googleSignInIntent.postValue(signInIntent)
    }

    fun handleGoogleSignInResult(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    _navigateToHome.postValue(Unit)
                } else {
                   // SnackBar.showSnackBar(getApplication(), "Google sign-in failed: ${task.exception?.localizedMessage}")
                }
            }
    }
    fun forgotPassword(email: String) {
        if (email.isEmpty()) {
            //SnackBar.showSnackBar(getApplication(), "Please enter your email address.")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { sent ->
                if (sent.isSuccessful) {
                 //   SnackBar.showSnackBar(getApplication(), "Password reset email sent.")
                } else {
                 //   SnackBar.showSnackBar(getApplication(), "Error in sending reset email.")
                }
            }
    }
    fun loginUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
           // SnackBar.showSnackBar(getApplication(), "Email and password cannot be empty.")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { login ->
                if (login.isSuccessful) {
                    _navigateToHome.postValue(Unit)
                } else {
                 //   SnackBar.showSnackBar(getApplication(), "Authentication failed: ${login.exception?.localizedMessage}")
                }
            }
    }

    fun navigateToSignUp() {
        _navigateToSignUp.postValue(Unit)
    }

}

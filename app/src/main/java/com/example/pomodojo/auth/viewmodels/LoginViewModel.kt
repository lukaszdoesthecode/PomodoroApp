@file:Suppress("DEPRECATION")

package com.example.pomodojo.auth.viewmodels

import android.app.Application
import android.content.Intent
import android.view.View
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
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


    fun handleGoogleSignInResult(account: GoogleSignInAccount?, view: View) {
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
                    showError(view, task.exception?.localizedMessage ?: "Unknown error")

                }
            }
    }

    fun forgotPassword(email: String, view: View) {
        if (email.isEmpty()) {
            showError(view, "Please enter your email address.")

            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                showSuccess(view, "Password reset email sent.")
                            } else {
                                showError(view, "Error in sending reset email. Please try again.")
                            }
                        }
                } else {
                    showError(view, "No account found with this email address.")
                }
            }
            .addOnFailureListener { exception ->
                showError(view, "Failed to check email existence: ${exception.message}")
            }
    }
    fun loginUser(email: String, password: String, view: View) {
        if (email.isBlank() || password.isBlank()) {
            showError(view, "Email and password cannot be empty.")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { login ->
                if (login.isSuccessful) {
                    _navigateToHome.postValue(Unit)
                } else {
                    showError(view, "Authentication failed: ${login.exception?.localizedMessage}")
                }
            }
    }

    private fun showError(view: View, message: String) {
        ErrorSnackBar.showErrorSnackBar(view, "Error", message)
    }

    private fun showSuccess(view: View, message: String) {
        SnackBar.showSnackBar(view, "Success", message)
    }

    fun navigateToSignUp() {
        _navigateToSignUp.postValue(Unit)
    }

}

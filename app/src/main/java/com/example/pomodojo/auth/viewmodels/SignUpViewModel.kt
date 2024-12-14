package com.example.pomodojo.auth.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pomodojo.utils.ErrorSnackBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class SignUpViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _navigateToLogIn = MutableLiveData<Unit>()
    val navigateToLogIn: LiveData<Unit> = _navigateToLogIn

    fun createAnAccount(fullName: String, dob: String, email: String, password: String, view: View) {
        val (name, surname) = splitName(fullName)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        saveUserToFirestore(userId, name, surname, dob, email, view)
                    } else {
                        showError(view, "Error while creating account.")
                    }
                } else {
                    handleAuthError(task.exception, view)
                }
            }
    }

    private fun splitName(fullName: String): Pair<String, String> {
        val nameParts = fullName.trim().split(" ")
        val name = nameParts.first()
        val surname = nameParts.drop(1).joinToString(" ")
        return Pair(name, surname)
    }

    private fun saveUserToFirestore(
        userId: String,
        name: String,
        surname: String,
        dob: String,
        email: String,
        view: View
    ) {
        val user = mapOf("name" to name, "surname" to surname, "dob" to dob, "email" to email)

        firestore.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                _navigateToLogIn.postValue(Unit)
            }
            .addOnFailureListener {
                showError(view, "Error while saving user data.")
            }
    }

    private fun handleAuthError(exception: Exception?, view: View) {
        if (exception is FirebaseAuthException) {
            when (exception.errorCode) {
                "ERROR_EMAIL_ALREADY_IN_USE" -> showError(view, "Email is already in use.")
                "ERROR_WEAK_PASSWORD" -> showError(view, "Weak password. Please use a stronger password.")
                else -> showError(view, exception.localizedMessage ?: "Authentication failed.")
            }
        } else {
            showError(view, "Error while authenticating.")
        }
    }

    private fun showError(view: View, message: String) {
        ErrorSnackBar.showErrorSnackBar(view, "Error", message)
    }

    fun navigateToLogIn() {
        _navigateToLogIn.postValue(Unit)
    }
}

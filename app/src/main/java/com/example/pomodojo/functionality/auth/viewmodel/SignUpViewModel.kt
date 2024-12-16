package com.example.pomodojo.functionality.auth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * ViewModel for handling the logic of the Sign-Up screen.
 *
 * @param application The application context.
 */
class SignUpViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _navigateToLogIn = MutableLiveData<Unit>()
    val navigateToLogIn: LiveData<Unit> = _navigateToLogIn

    private val _errorMessage = MutableLiveData<Pair<String, String>>()
    val errorMessage: LiveData<Pair<String, String>> = _errorMessage

    /**
     * Creates a new user account with the provided information and saves it to Firestore.
     *
     * @param fullName The full name of the user.
     * @param dob The date of birth of the user.
     * @param email The email address of the user.
     * @param password The password for the new account.
     */
    fun createAnAccount(fullName: String, dob: String, email: String, password: String) {
        val nameParts = fullName.trim().split(" ")
        val name = nameParts.first()
        val surname = nameParts.drop(1).joinToString(" ")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val user = mapOf(
                            "name" to name,
                            "surname" to surname,
                            "dob" to dob,
                            "email" to email
                        )

                        firestore.collection("users").document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                _navigateToLogIn.postValue(Unit)
                            }
                            .addOnFailureListener { _ ->
                                _errorMessage.postValue(Pair("Error", "Error while saving user information."))
                            }
                    } else {
                        _errorMessage.postValue(Pair("Error", "Error while creating account."))
                    }
                } else {
                    _errorMessage.postValue(Pair("Error", "Error while authenticating."))
                }
            }
    }

    /**
     * Navigates to the Login screen.
     */
    fun navigateToLogIn() {
        _navigateToLogIn.postValue(Unit)
    }
}
package com.example.pomodojo.functionality.auth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pomodojo.core.utils.ErrorSnackBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("INFERRED_TYPE_VARIABLE_INTO_EMPTY_INTERSECTION_WARNING")
class SignUpViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _navigateToLogIn = MutableLiveData<Unit>()
    val navigateToLogIn: LiveData<Unit> = _navigateToLogIn

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
                            }
                    } else {
                        ErrorSnackBar.showErrorSnackBar(getApplication(), "Error","Error while creating account.")

                    }
                } else {
                    ErrorSnackBar.showErrorSnackBar(getApplication(), "Error","Error while authenticating.")

                }
            }
    }

    fun navigateToLogIn() {
        _navigateToLogIn.postValue(Unit)
    }
}

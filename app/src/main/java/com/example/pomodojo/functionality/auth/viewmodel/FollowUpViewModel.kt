package com.example.pomodojo.functionality.auth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pomodojo.core.utils.ErrorSnackBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * ViewModel for handling the logic of the Follow-Up screen.
 *
 * @param application The application context.
 */
class FollowUpViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _navigateToHome = MutableLiveData<Unit>()
    val navigateToHome: LiveData<Unit> = _navigateToHome

    /**
     * Completes the user information by saving it to Firestore.
     *
     * @param fullName The full name of the user.
     * @param dob The date of birth of the user.
     * @param email The email address of the user.
     */
    fun completeInformation(fullName: String, dob: String, email: String) {
        val nameParts = fullName.trim().split(" ")
        val name = nameParts.first()
        val surname = nameParts.drop(1).joinToString(" ")

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
                    _navigateToHome.postValue(Unit)
                }
                .addOnFailureListener { _ ->
                    ErrorSnackBar.showErrorSnackBar(getApplication(), "Error", "Error while saving information.")
                }
        }
    }
}
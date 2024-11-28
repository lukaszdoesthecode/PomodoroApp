package com.example.pomodojo.auth.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pomodojo.utils.ErrorSnackBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("INFERRED_TYPE_VARIABLE_INTO_EMPTY_INTERSECTION_WARNING")
class FollowUpViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _navigateToHome = MutableLiveData<Unit>()
    val navigateToHome: LiveData<Unit> = _navigateToHome

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
                    ErrorSnackBar.showErrorSnackBar(getApplication(), "Error","Error while saving information.")
                }
        }
    }

}

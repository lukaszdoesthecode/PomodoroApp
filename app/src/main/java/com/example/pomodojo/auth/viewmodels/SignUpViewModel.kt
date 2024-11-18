package com.example.pomodojo.auth.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
//import com.example.pomodojo.SnackBar
import com.google.firebase.auth.FirebaseAuth

class SignUpViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _navigateToLogIn = MutableLiveData<Unit>()
    val navigateToLogIn: LiveData<Unit> = _navigateToLogIn

    fun createAnAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                   // SnackBar.showSnackBar(getApplication(), "Account created successfully.")
                    _navigateToLogIn.postValue(Unit)
                } else {
                   // SnackBar.showSnackBar(getApplication(), task.exception?.message ?: "Sign-up failed.")
                }
            }
    }

    fun navigateToLogIn() {
        _navigateToLogIn.postValue(Unit)
    }
}

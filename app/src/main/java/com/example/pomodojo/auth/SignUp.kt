package com.example.pomodojo.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.pomodojo.R
import com.example.pomodojo.utils.ErrorHandler
import com.example.pomodojo.databinding.ActivitySignUpBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Calendar

/**
 * SignUp class handles user registration functionality including
 * input validation and creating a new account using Firebase.
 */
@Suppress("DEPRECATION")
class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivitySignUpBinding
    private var name: String = ""
    private var dob: String = ""
    private var email: String = ""
    private var password: String = ""
    private var repeat: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.signIn.setOnClickListener {
            goLogIn()
        }


        binding.dob.addTextChangedListener(DobTextWatcher(binding.dob))
        binding.dob.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.dob.text!!.isEmpty()) {
                binding.dobLayout.hint = "YYYY/MM/DD"
            } else if (!hasFocus && binding.dob.text!!.isEmpty()) {
                binding.dobLayout.hint = "Date of Birth"
            } else if (!hasFocus) {
                validateDate(binding.dob.text.toString())
            }
        }

        binding.save.setOnClickListener {
            if (validate()) {
                createAccount()
            }
        }
    }

    fun setFirebaseAuth(auth: FirebaseAuth) {
        this.auth = auth
    }

    /**
     * Creates a new user account with the provided email and password after validating
     * input.
     */

fun createAccount() {
    val email = binding.email.text.toString().trim()
    val password = binding.password.text.toString().trim()

    if (email.isNotEmpty() && password.isNotEmpty()) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { create ->
                if (create.isSuccessful) {
                    ErrorHandler.showSuccessMessage(binding.root, "Registration Successful", "Your account has been created.")
                    goLogIn()
                } else {
                    ErrorHandler.showAuthenticationFailedError(binding.root)
                }
            }
    } else {
        // Handle empty email or password
        ErrorHandler.showErrorMessage(binding.root, "Error", "Email and password must not be empty.")
    }
}

    /**
     * Validates user input for the sign-up including name, date of birth, email, and passwords.
     * The fields cannot be empty, dob cannot be set in the future, and passwords have to match and
     * contain at least 6 characters, 1 number, 1 uppercase letter, and 1 special character.
     *
     * @return true if all fields are valid. When false, the outline of the particular box is set to red.
     */
    fun validate(): Boolean {
        name = binding.username.text.toString().trim()
        dob = binding.dob.text.toString().trim()
        email = binding.email.text.toString().trim()
        password = binding.password.text.toString().trim()
        repeat = binding.repeat.text.toString().trim()

        val nameLayout = binding.nameLayout
        val dobLayout = binding.dobLayout
        val emailLayout = binding.emailLayout
        val passwordLayout = binding.passwordLayout
        val repeatLayout = binding.repeatLayout

        ErrorHandler.resetErrorStates(nameLayout, dobLayout, emailLayout, passwordLayout, repeatLayout)

        return ErrorHandler.validateSignUpFields(
            binding.root, name, dob, email, password, repeat,
            nameLayout, dobLayout, emailLayout, passwordLayout, repeatLayout
        )
    }

    /**
     * Validates the user's date of birth.
     *
     * @param date the date input as a string.
     */
    private fun validateDate(date: String) {
        ErrorHandler.validateDateOfBirth(binding.root, date, binding.dobLayout)
    }

    /**
     * Navigates to the Log In activity.
     */
    fun goLogIn() {
        val intent = Intent(this, LogIn::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Tracks the changes done in the date of birth EditText to ensure the correct format.
     */
    class DobTextWatcher(private val dobField: EditText) : TextWatcher {
        private var isUpdating = false

        override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(editable: Editable?) {
            if (isUpdating) return

            val input = editable.toString().replace("/", "")
            val formatted = StringBuilder()

            if (input.length > 4) {
                formatted.append(input.substring(0, 4)).append("/")
                if (input.length >= 6) {
                    formatted.append(input.substring(4, 6)).append("/")
                    if (input.length > 6) {
                        formatted.append(input.substring(6))
                    }
                } else {
                    formatted.append(input.substring(4))
                }
            } else {
                formatted.append(input)
            }

            isUpdating = true
            dobField.setText(formatted.toString())
            dobField.setSelection(formatted.length)
            isUpdating = false
        }
    }
}

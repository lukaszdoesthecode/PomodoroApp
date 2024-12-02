package com.example.pomodojo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.pomodojo.databinding.ActivitySignUpBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.Calendar

/**
 * SignUp class handles user registration functionality including
 * input validation and creating a new account using Firebase
 */
@Suppress("DEPRECATION")
class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding
    private var name: String = ""
    private var dob: String = ""
    private var email: String = ""
    private var password: String = ""
    private var repeat: String = ""

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.signIn.setOnClickListener{
            goLogIn()
        }
        binding.dob.addTextChangedListener(DobTextWatcher(binding.dob))
        binding.dob.setOnFocusChangeListener{_, hasFocus ->
        if(hasFocus && binding.dob.text!!.isEmpty()){
            binding.dobLayout.hint = "YYYY/MM/DD"
        }else if(!hasFocus && binding.dob.text!!.isEmpty()){
            binding.dobLayout.hint = "Date of Birth"
        }else if (!hasFocus){
            validateDate(binding.dob.text.toString())
        }
        }


        binding.save.setOnClickListener{
                if(validate()){
                createAccount()}
            }
    }

    /**
     * Creates a new user account with the provided email and password after validating
     * input.
     */
    private fun createAccount(){
                   auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this){create ->
                        if(create.isSuccessful) {
                            SnackBar.showSnackBar(binding.root, "Authentication Succeeded")
                            goLogIn()
                        }else{
                            SnackBar.showSnackBar(binding.root, "Authentication Failed")
                        }
                    }
    }

    /**
     * Validates user input for the sign up including ame, date of birth, email, passwords.
     * The fields cannot be empty, dob cannot be set in future, and passwords have to match and
     * contain at least 6 characters, 1 number, 1 upper case letter and 1 special sign.
     *
     * @return true if all fields are valid. When false changes the particular box outline to red.
     */
    private fun validate(): Boolean{

            name = findViewById<EditText>(R.id.username).text.toString().trim()
            dob = findViewById<EditText>(R.id.dob).text.toString().trim()
            email = findViewById<EditText>(R.id.email).text.toString().trim()
            password = findViewById<EditText>(R.id.password).text.toString().trim()
            repeat = findViewById<EditText>(R.id.repeat).text.toString().trim()

            val nameLayout: TextInputLayout = findViewById(R.id.name_layout)
            val dobLayout: TextInputLayout = findViewById(R.id.dob_layout)
            val emailLayout: TextInputLayout = findViewById(R.id.email_layout)
            val passwordLayout: TextInputLayout = findViewById(R.id.password_layout)
            val repeatLayout: TextInputLayout = findViewById(R.id.repeat_layout)

            resetErrorStates(nameLayout, dobLayout, emailLayout, passwordLayout, repeatLayout)


            return when {
                name.isBlank() -> {
                    showError(nameLayout, "Fill out your information")
                    false
                }

                dob.isBlank() -> {
                    showError(dobLayout, "Fill out your information")
                        false
                    }

                email.isBlank() -> {
                    showError(emailLayout, "Fill out your information")
                        false
                    }

                password.isBlank() -> {
                    showError(passwordLayout, "Fill out your information")
                        false
                    }

                repeat.isBlank() -> {
                    showError(repeatLayout, "Fill out your information")
                        false
                    }

                password.length < 6 -> {
                    showError(passwordLayout, "Password must be at least 6 characters long")
                        false
                    }

                !password.any{it.isUpperCase()} -> {
                    showError(passwordLayout, "Password has to have a upper case letter")
                        false
                    }

                !password.any{it.isDigit()} -> {
                    showError(passwordLayout, "Password has to have a number")
                        false
                    }

                !password.any{it.isLetterOrDigit()} -> {
                    showError(passwordLayout, "Password has to have special sign")
                        false
                    }

                password != repeat -> {
                    showError(passwordLayout, "Passwords must match")
                        false
                    }

                else -> true
            }

        }

    /**
     * Validates the user's date of birth
     *
     * @return true if the date is not set in the future or if the
     * months and days are correctly inputted
     */
    private fun validateDate(date : String){
        val parts = date.split("/")
        if(parts.size != 3){
            SnackBar.showSnackBar(binding.root, "Invalid data format")
            return
        }

        val year: Int
        val month: Int
        val day: Int

        try{
            year = parts[0].toInt()
            month = parts[1].toInt()
            day = parts[2].toInt()
        }catch(e: NumberFormatException){
            SnackBar.showSnackBar(binding.root, "Invalid data values")
            return
        }

        if(month < 1 || month > 12){
            SnackBar.showSnackBar(binding.root, "Month has to be between 1 and 12")
            return
        }

        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day)
        if(calendar.get(Calendar.YEAR) != year || calendar.get(Calendar.MONTH) != month || calendar.get(Calendar.DAY_OF_MONTH) != day){
            SnackBar.showSnackBar(binding.root, "Invalid data values")
        }

        val currentDate = Calendar.getInstance()
        if(calendar.after(currentDate)){
            SnackBar.showSnackBar(binding.root, "Date cannot be in the future")
        }

    }

    /**
     * Resets error states for the TextInputLayouts.
     */
    private fun resetErrorStates(vararg layouts: TextInputLayout){
        for(layout in layouts){
            layout.boxStrokeColor = resources.getColor(R.color.white)
        }

    }
    /**
     * Shows error message and sets teh outline color to red.
     */
    private fun showError(layout: TextInputLayout, message: String){
     SnackBar.showSnackBar(binding.root, message)
     layout.boxStrokeColor = resources.getColor(R.color.error)
    }

    /**
     * Navigates to the Log In activity.
     */
    private fun goLogIn(){
        val intent = Intent(this, LogIn::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Tracks the changes done in the date of birth editview to ensure the correct format
     */
    class DobTextWatcher(private val dobField: EditText): TextWatcher{
        private var isUpdating = false
        private var current = ""

        override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int){}
        override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(editable: Editable?){

            if(isUpdating) return

            val input = editable.toString().replace("/", "")
            val formatted = StringBuilder()

            if(input.length > 4){
                formatted.append(input.substring(0,4)).append("/")
                if(input.length >= 6){
                    formatted.append(input.substring(4,6)).append("/")
                    if(input.length > 6){
                        formatted.append(input.substring(6))
                    }
                }else{
                    formatted.append(input.substring(4))
                }
            }else{
                formatted.append(input)
            }


            isUpdating = true
            dobField.setText(formatted.toString())
            dobField.setSelection(formatted.length)
            isUpdating = false
            current = formatted.toString()

        }
    }
}
@file:Suppress("DEPRECATION")

package com.example.pomodojo

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pomodojo.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout

/**
 * LogIn class handles the user login functionality, including email/password
 * authentication and Google sign in.
 */
@Suppress("DEPRECATION")
class LogIn : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var google: GoogleSignInClient
    private var email: EditText? = null
    private var password: EditText? = null

    /**
     * onCreate function is called when the activity is starting.
     * It sets the UI components and initializes the Firebase
     * authentication and handles sign in logic.
     */

    companion object{
        private const val RC_SIGN_IN = 9001 // Request code for Google sign in
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        email = binding.email
        password = binding.password

        val signup: TextView = findViewById(R.id.sign_up)
        signup.setOnClickListener{goSignUp()}

        val forgotPassword: TextView = findViewById(R.id.forgot_password)
        forgotPassword.setOnClickListener{forgotPassword()}

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        google = GoogleSignIn.getClient(this, gso)

        binding.google.setOnClickListener{
            googleLogin()
        }


        binding.save.setOnClickListener{
            if(validate()){
                loginUser()
            }
        }
    }
    /**
     * Validate function checks if the email and password fields are empty
     *
     * @return true if both fields are filled.
     */
    private fun validate(): Boolean{
        val emailLayout: TextInputLayout = findViewById(R.id.email_layout)
        val passwordLayout: TextInputLayout = findViewById(R.id.password_layout)

        emailLayout.boxStrokeColor = resources.getColor(R.color.white)
        passwordLayout.boxStrokeColor = resources.getColor(R.color.white)

        if(email?.text.toString().isEmpty()){
            SnackBar.showSnackBar(binding.root, "Fill Out Your Information")
            emailLayout.boxStrokeColor = resources.getColor(R.color.error)

            return false
        }else if(password?.text.toString().isEmpty()){
            SnackBar.showSnackBar(binding.root, "Fill Out Your Information")
            passwordLayout.boxStrokeColor = resources.getColor(R.color.error)
            return false

        }
        return true
    }

    /**
     * login in the user using the email and password authentication
     * on success navigates to the home activity
     */
    private fun loginUser(){
        auth.signInWithEmailAndPassword(email?.text.toString(), password?.text.toString())
            .addOnCompleteListener(this){login ->
                if(login.isSuccessful){
                    goHome()
                }else{
                    SnackBar.showSnackBar(binding.root, "Authentication failed.")
                }
            }
    }

    /**
     * Initiates the password reset process by sending a reset email.
     */
    private fun forgotPassword(){
        if(email?.text.toString().isEmpty()){
            SnackBar.showSnackBar(binding.root, "Please enter your email address.")
            return
        }

        auth.sendPasswordResetEmail(email?.text.toString())
            .addOnCompleteListener{ sent ->
                if(sent.isSuccessful){
                    SnackBar.showSnackBar(binding.root, "Password reset email sent.")
                }else{
                    SnackBar.showSnackBar(binding.root, "Error in sending reset email.")
                }
            }
    }

    /**
     * Starts the Google sign in process
     */
    private fun googleLogin(){
        val signInIntent = google.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /**
     * Handles the result of the google sign in intent
     *
     * @param requestCode The request code passed in startActivityForResult
     * @param resultCode The result code returned by the child activity
     * @param data The intent data returned by the child activity
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)
                if (account != null){
                    googleAuthentication(account)
                }
            }catch(e: ApiException){
                SnackBar.showSnackBar(binding.root, "Google sign in failed")
            }
        }
    }

    /**
     * Authenticates the user with Firebase using Google credentials
     *
     * @param account The GoogleSignInAccount containing the user's information.
     */
    private fun googleAuthentication(account: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){ auth ->
                if(auth.isSuccessful){
                    goHome()
                } else{
                    SnackBar.showSnackBar(binding.root,"Google Authentication failed")
                }
            }
    }

    /**
     * Navigates to the Sign Up activity.
     */
    private fun goSignUp(){
        val intent = Intent(this, SignUp::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Navigates to the Sign Up activity.
     */
    private fun goHome(){
        val intent = Intent(this, Main::class.java)
        startActivity(intent)
        finish()
    }

}
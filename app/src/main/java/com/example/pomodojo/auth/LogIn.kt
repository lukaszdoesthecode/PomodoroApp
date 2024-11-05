@file:Suppress("DEPRECATION")

package com.example.pomodojo.auth

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pomodojo.Main
import com.example.pomodojo.R
import com.example.pomodojo.utils.ErrorHandler
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
 * authentication and Google sign-in.
 */
@Suppress("DEPRECATION")
class LogIn : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding
    lateinit var auth: FirebaseAuth
    lateinit var google: GoogleSignInClient
    var email: EditText? = null
    var password: EditText? = null

    /**
     * onCreate function is called when the activity is starting.
     * It sets the UI components and initializes the Firebase
     * authentication and handles sign-in logic.
     */
    companion object {
        const val RC_SIGN_IN = 9001 // Request code for Google sign-in
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Authentication
        try {
            auth = FirebaseAuth.getInstance()
        } catch (e: Exception) {
            ErrorHandler.showInitializationError(binding.root, "Initialization Failed", "Unable to initialize Firebase Authentication.")
            return
        }

        email = binding.email
        password = binding.password

        val signup: TextView = findViewById(R.id.sign_up)
        signup.setOnClickListener { goSignUp() }

        val forgotPassword: TextView = findViewById(R.id.forgot_password)
        forgotPassword.setOnClickListener { forgotPassword() }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Initialize Google Sign-In Client
        try {
            google = GoogleSignIn.getClient(this, gso)
        } catch (e: Exception) {
            ErrorHandler.showInitializationError(binding.root, "Google Sign-In Failed", "Unable to initialize Google Sign-In.")
            return
        }

        binding.google.setOnClickListener {
            googleLogin()
        }

        binding.save.setOnClickListener {
            if (validate()) {
                loginUser()
            }
        }
    }

    /**
     * Validate function checks if the email and password fields are empty
     *
     * @return true if both fields are filled.
     */
    fun validate(): Boolean {
        return ErrorHandler.validateFields(
            binding.root,
            binding.emailLayout,
            binding.passwordLayout,
            email?.text.toString(),
            password?.text.toString()
        )
    }

    /**
     * Logs in the user using email and password authentication.
     * On success, navigates to the home activity.
     */
    fun loginUser() {
        auth.signInWithEmailAndPassword(email?.text.toString(), password?.text.toString())
            .addOnCompleteListener(this) { login ->
                if (login.isSuccessful) {
                    goHome()
                } else {
                    ErrorHandler.showAuthenticationFailedError(binding.root)
                }
            }
    }

    /**
     * Initiates the password reset process by sending a reset email.
     */
    fun forgotPassword() {
        if (email?.text.toString().isEmpty()) {
            ErrorHandler.showMissingFieldError(binding.root, "email", binding.emailLayout)
            return
        }

        auth.sendPasswordResetEmail(email?.text.toString())
            .addOnCompleteListener { sent ->
                if (sent.isSuccessful) {
                    ErrorHandler.showPasswordResetSuccess(binding.root)
                } else {
                    ErrorHandler.showPasswordResetFailedError(binding.root)
                }
            }
    }

    /**
     * Starts the Google sign-in process.
     */
    fun googleLogin() {
        val signInIntent = google.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /**
     * Handles the result of the Google sign-in intent.
     *
     * @param requestCode The request code passed in startActivityForResult.
     * @param resultCode The result code returned by the child activity.
     * @param data The intent data returned by the child activity.
     */
    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    googleAuthentication(account)
                }
            } catch (e: ApiException) {
                ErrorHandler.showGoogleSignInFailedError(binding.root)
            }
        }
    }

    /**
     * Authenticates the user with Firebase using Google credentials.
     *
     * @param account The GoogleSignInAccount containing the user's information.
     */
    fun googleAuthentication(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    goHome()
                } else {
                    ErrorHandler.showGoogleAuthenticationFailedError(binding.root)
                }
            }
    }

    /**
     * Navigates to the Sign-Up activity.
     */
    fun goSignUp() {
        val intent = Intent(this, SignUp::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Navigates to the Home activity.
     */
    fun goHome() {
        val intent = Intent(this, Main::class.java)
        startActivity(intent)
        finish()
    }
}

package com.example.pomodojo.auth

import android.widget.EditText
import android.widget.LinearLayout
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pomodojo.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/**
 * Unit tests for the SignUp activity.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
class SignUpTest {

    private lateinit var signUpActivity: SignUp
    private lateinit var mockAuth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding

    /**
     * Sets up the test environment before each test.
     */
    @Before
    fun setUp() {
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        mockAuth = mock(FirebaseAuth::class.java)
        signUpActivity = Robolectric.buildActivity(SignUp::class.java).create().get()
        binding = ActivitySignUpBinding.inflate(signUpActivity.layoutInflater)
        signUpActivity.setFirebaseAuth(mockAuth)
        signUpActivity.binding = binding

        val parentLayout = LinearLayout(signUpActivity)
        parentLayout.orientation = LinearLayout.VERTICAL
        signUpActivity.setContentView(parentLayout)

        val usernameField = TextInputEditText(signUpActivity)
        val dobField = TextInputEditText(signUpActivity)
        val emailField = TextInputEditText(signUpActivity)
        val passwordField = TextInputEditText(signUpActivity)
        val repeatField = TextInputEditText(signUpActivity)

        parentLayout.addView(usernameField)
        parentLayout.addView(dobField)
        parentLayout.addView(emailField)
        parentLayout.addView(passwordField)
        parentLayout.addView(repeatField)

        val usernameBindingField = binding::class.java.getDeclaredField("username")
        usernameBindingField.isAccessible = true
        usernameBindingField.set(binding, usernameField)

        val dobBindingField = binding::class.java.getDeclaredField("dob")
        dobBindingField.isAccessible = true
        dobBindingField.set(binding, dobField)

        val emailBindingField = binding::class.java.getDeclaredField("email")
        emailBindingField.isAccessible = true
        emailBindingField.set(binding, emailField)

        val passwordBindingField = binding::class.java.getDeclaredField("password")
        passwordBindingField.isAccessible = true
        passwordBindingField.set(binding, passwordField)

        val repeatBindingField = binding::class.java.getDeclaredField("repeat")
        repeatBindingField.isAccessible = true
        repeatBindingField.set(binding, repeatField)
    }

    /**
     * Sets the text of an EditText.
     *
     * @param editText The EditText to set the text for.
     * @param text The text to set.
     */
    private fun setEditText(editText: EditText, text: String) {
        editText.setText(text)
    }

    /**
     * Tests that validate() returns true when all fields are correctly filled.
     */
    @Test
    fun validate_shouldReturnTrue_whenAllFieldsAreCorrectlyFilled() {
        setEditText(binding.username, "John Doe")
        setEditText(binding.dob, "1990/01/01")
        setEditText(binding.email, "test@example.com")
        setEditText(binding.password, "Password123!")
        setEditText(binding.repeat, "Password123!")
        Assert.assertTrue(signUpActivity.validate())
    }

    /**
     * Tests that validate() returns false when any field is empty.
     */
    @Test
    fun validate_shouldReturnFalse_whenAnyFieldIsEmpty() {
        val rootLayout = LinearLayout(signUpActivity)
        rootLayout.orientation = LinearLayout.VERTICAL
        signUpActivity.setContentView(rootLayout)
        rootLayout.addView(binding.root)

        setEditText(binding.username, "John Doe")
        setEditText(binding.dob, "1990/01/01")
        setEditText(binding.email, "test@example.com")
        setEditText(binding.password, "Password123!")
        setEditText(binding.repeat, "Password123!")

        setEditText(binding.username, "")
        Assert.assertFalse("Validation failed when username is empty", signUpActivity.validate())

        setEditText(binding.username, "John Doe")
        setEditText(binding.dob, "")
        Assert.assertFalse("Validation failed when dob is empty", signUpActivity.validate())

        setEditText(binding.dob, "1990/01/01")
        setEditText(binding.email, "")
        Assert.assertFalse("Validation failed when email is empty", signUpActivity.validate())

        setEditText(binding.email, "test@example.com")
        setEditText(binding.password, "")
        Assert.assertFalse("Validation failed when password is empty", signUpActivity.validate())

        setEditText(binding.password, "Password123!")
        setEditText(binding.repeat, "")
        Assert.assertFalse("Validation failed when repeat password is empty", signUpActivity.validate())
    }

    /**
     * Tests that createAccount() calls createUserWithEmailAndPassword with the correct email and password.
     */
    @Test
    fun createAccount_shouldCallCreateUserWithEmailAndPassword_withCorrectEmailAndPassword() {
        binding.email.setText("test@example.com")
        binding.password.setText("Password123!")

        `when`(mockAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(
            mock(Task::class.java) as Task<AuthResult>
        )

        signUpActivity.createAccount()

        verify(mockAuth).createUserWithEmailAndPassword("test@example.com", "Password123!")
    }

    /**
     * Tests that goLogIn() starts the LogIn activity.
     */
    @Test
    fun goLogIn_shouldStartLogInActivity() {
        signUpActivity.goLogIn()
        val startedIntent = shadowOf(signUpActivity).nextStartedActivity
        Assert.assertEquals(LogIn::class.java.name, startedIntent?.component?.className)
    }
}
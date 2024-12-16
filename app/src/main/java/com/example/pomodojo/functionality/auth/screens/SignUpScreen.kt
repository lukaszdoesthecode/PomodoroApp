package com.example.pomodojo.functionality.auth.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodojo.R
import com.example.pomodojo.functionality.auth.viewmodel.SignUpViewModel
import com.example.pomodojo.ui.theme.Error
import com.example.pomodojo.ui.theme.PomodojoTheme
import com.example.pomodojo.ui.theme.Primary
import com.example.pomodojo.ui.theme.ShadowD
import com.example.pomodojo.ui.theme.ShadowL
import com.example.pomodojo.ui.theme.White
import java.text.SimpleDateFormat
import java.util.*

/**
 * Composable function that displays the Sign-Up screen.
 *
 * @param viewModel The ViewModel that handles the logic for the Sign-Up screen.
 */
@Composable
fun SignUpScreen(viewModel: SignUpViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeat by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var dobError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var repeatPasswordError by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }
    var repeatPasswordVisible by remember { mutableStateOf(false) }

    /**
     * Validates the name input. Sets the nameError flag if the input does not contain at least two words.
     *
     * @param input The name input to validate.
     */
    val validateName = { input: String ->
        nameError = input.trim().split(" ").size < 2
    }

    /**
     * Validates the date of birth input. Sets the dobError flag if the input is not a valid date
     * or if it is not within the acceptable range (01/01/1900 to the current date).
     *
     * @param date The date of birth input to validate.
     */
    val validateDob = { date: String ->
        dobError = false
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        formatter.isLenient = false
        try {
            val parsedDate = formatter.parse(date)
            val minDate = Calendar.getInstance().apply { set(1900, 0, 1) }.time
            val maxDate = Date()
            dobError = parsedDate == null || parsedDate !in minDate..maxDate
        } catch (e: Exception) {
            dobError = true
        }
    }

    /**
     * Validates the password input. Sets the passwordError flag if the input does not meet the required criteria.
     *
     * @param pass The password input to validate.
     */
    val validatePassword = { pass: String ->
        val passwordRegex = Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{6,}\$")
        passwordError = !passwordRegex.matches(pass)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(color = Primary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ninja_backdrop),
            contentDescription = "Ninja",
            modifier = Modifier
                .fillMaxWidth()
                .height(245.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Sign In",
                color = White,
                fontSize = 22.sp,
                modifier = Modifier.clickable {
                    viewModel.navigateToLogIn()
                }
            )
            Text(
                text = "Sign Up",
                color = White,
                fontSize = 22.sp,
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .padding(end = 16.dp),
                color = Primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .padding(start = 16.dp),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        InputField(
            label = "Full Name",
            value = name,
            onValueChange = {
                name = it
                validateName(name)
            },
            isError = nameError,
            errorMessage = if (nameError) "Please enter at least a first and last name" else null
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputField(
            label = "Date of Birth (dd/MM/yyyy)",
            value = dob,
            onValueChange = {
                dob = it
                validateDob(dob)
            },
            isError = dobError,
            errorMessage = "Invalid Date of Birth"
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputField("Email", email, onValueChange = { email = it })

        Spacer(modifier = Modifier.height(16.dp))

        InputFieldWithVisibility(
            label = "Password",
            value = password,
            onValueChange = {
                password = it
                validatePassword(password)
            },
            isError = passwordError,
            errorMessage = "Password must have 6+ chars, 1 uppercase, 1 number, and 1 special character",
            passwordVisible = passwordVisible,
            onPasswordVisibilityChange = { passwordVisible = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputFieldWithVisibility(
            label = "Repeat Password",
            value = repeat,
            onValueChange = {
                repeat = it
                repeatPasswordError = password != repeat
            },
            isError = repeatPasswordError,
            errorMessage = "Passwords do not match",
            passwordVisible = repeatPasswordVisible,
            onPasswordVisibilityChange = { repeatPasswordVisible = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isNotBlank() && email.isNotBlank() && !dobError && !passwordError && !repeatPasswordError) {
                    viewModel.createAnAccount(name, dob, email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Error)
        ) {
            Text("Sign Up", fontSize = 20.sp, color = White)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Composable function that displays an input field.
 *
 * @param label The label for the input field.
 * @param value The current value of the input field.
 * @param onValueChange Callback to handle changes in the input field value.
 * @param isError Indicates if there is an error in the input field.
 * @param errorMessage The error message to display if there is an error.
 */
@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                cursorColor = Primary,
                focusedTextColor = Primary,
                unfocusedTextColor = ShadowD,
                focusedLabelColor = ShadowD,
                unfocusedLabelColor = ShadowL,
                focusedIndicatorColor = Primary,
                unfocusedIndicatorColor = Primary
            ),
            isError = isError
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

/**
 * Composable function that displays an input field with password visibility toggle.
 *
 * @param label The label for the input field.
 * @param value The current value of the input field.
 * @param onValueChange Callback to handle changes in the input field value.
 * @param isError Indicates if there is an error in the input field.
 * @param errorMessage The error message to display if there is an error.
 * @param passwordVisible Indicates if the password is visible.
 * @param onPasswordVisibilityChange Callback to handle changes in the password visibility.
 */
@Composable
fun InputFieldWithVisibility(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit
) {
    Column {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
                        ),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                cursorColor = Primary,
                focusedTextColor = Primary,
                unfocusedTextColor = ShadowD,
                focusedLabelColor = ShadowD,
                unfocusedLabelColor = ShadowL,
                focusedIndicatorColor = Primary,
                unfocusedIndicatorColor = Primary
            ),
            isError = isError
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

/**
 * Preview function for the SignUpScreen composable.
 */
@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    PomodojoTheme {
        SignUpScreen()
    }
}
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodojo.R
import com.example.pomodojo.functionality.auth.viewmodel.LoginViewModel
import com.example.pomodojo.ui.theme.Error
import com.example.pomodojo.ui.theme.Primary
import com.example.pomodojo.ui.theme.ShadowD
import com.example.pomodojo.ui.theme.ShadowL
import com.example.pomodojo.ui.theme.White

/**
 * Composable function that displays the Login screen.
 *
 * @param viewModel The ViewModel that handles the logic for the Login screen.
 */
@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

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
            )
            Text(
                text = "Sign Up",
                color = White,
                fontSize = 22.sp,
                modifier = Modifier.clickable {
                    viewModel.navigateToSignUp()
                }
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
                    .padding(start = 16.dp),
                color = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .padding(end = 16.dp),
                color = Primary
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        InputField("Email", email, onValueChange = { email = it })

        Spacer(modifier = Modifier.height(16.dp))

        InputFieldWithVisibilityLogIn(
            label = "Password",
            value = password,
            isError = passwordError,
            errorMessage = if (passwordError) "Password cannot be empty" else null,
            passwordVisible = passwordVisible,
            onValueChange = { password = it },
            onPasswordVisibilityChange = { passwordVisible = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.forgot_the_password),
            color = colorResource(R.color.accentL),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End)
                .clickable(onClick = { viewModel.forgotPassword(email) }),
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                passwordError = password.isBlank()
                if (email.isNotBlank() && password.isNotBlank()) {
                    viewModel.loginUser(email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Error)
        ) {
            Text("Log In", fontSize = 20.sp, color = White)
        }

        Spacer(modifier = Modifier.height(16.dp))



        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.navigateToHome() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Skip", fontSize = 20.sp, color = White)
        }
    }
}
/**
 * Composable function that displays an input field with password visibility toggle for the Login screen.
 *
 * @param label The label for the input field.
 * @param value The current value of the input field.
 * @param isError Indicates if there is an error in the input field.
 * @param errorMessage The error message to display if there is an error.
 * @param passwordVisible Indicates if the password is visible.
 * @param onValueChange Callback to handle changes in the input field value.
 * @param onPasswordVisibilityChange Callback to handle changes in the password visibility.
 */
@Composable
fun InputFieldWithVisibilityLogIn(
    label: String,
    value: String,
    isError: Boolean = false,
    errorMessage: String? = null,
    passwordVisible: Boolean,
    onValueChange: (String) -> Unit,
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
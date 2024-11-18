package com.example.pomodojo.auth.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomodojo.R
import com.example.pomodojo.auth.viewmodels.LoginViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodojo.auth.ui.theme.*

@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel()) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


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
            Divider(
                color = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Divider(
                color = Primary,
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .padding(end = 16.dp)

            )
        }


        Spacer(modifier = Modifier.height(48.dp))

        InputField("Email", email, onValueChange = { email = it })

        Spacer(modifier = Modifier.height(16.dp))

        InputField(
            label = "Password",
            value = password,
            onValueChange = {
                password = it
            })

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.forgot_the_password),
            color = colorResource(R.color.accentL),
            modifier = Modifier.padding(16.dp).align(Alignment.End).clickable(onClick = { viewModel.forgotPassword(email) }),
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
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

        Text(
            text = "Or",
            color = colorResource(R.color.accentL),
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.End
        )

        Button(
            onClick = { viewModel.googleLogin() },
            modifier = Modifier
                .height(60.dp)
                .width(60.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(2.dp)
                ),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Image(
                painter = painterResource(R.drawable.google_logo),
                contentDescription = stringResource(R.string.sign_in_with_google),
                modifier = Modifier.size(40.dp)
            )
        }


    }


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
}
@Preview(showBackground = true)
@Composable
fun LogInScreenPreview() {
    PomodojoTheme {
        LoginScreen()
    }
}

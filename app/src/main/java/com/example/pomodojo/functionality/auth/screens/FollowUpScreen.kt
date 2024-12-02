package com.example.pomodojo.functionality.auth.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodojo.functionality.auth.viewmodel.FollowUpViewModel
import com.example.pomodojo.ui.theme.Error
import com.example.pomodojo.ui.theme.PomodojoTheme
import com.example.pomodojo.ui.theme.Primary
import com.example.pomodojo.ui.theme.White
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FollowUpScreen(viewModel: FollowUpViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    val email = FirebaseAuth.getInstance().currentUser?.email ?: ""

    var nameError by remember { mutableStateOf(false) }
    var dobError by remember { mutableStateOf(false) }


    val validateName = { input: String ->
        nameError = input.trim().split(" ").size < 2
    }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(color = Primary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Help Us Complete Your Account!",
            color = White,
            fontSize = 22.sp,
        )
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .height(2.dp)
                .padding(start = 16.dp),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(48.dp))

        InputField(
            label = "Full Name",
            value = name,
            onValueChange = {
                name = it
                validateName(name)
            },
            isError = nameError,
            errorMessage = if (nameError) "Please enter first and last name" else null
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


        Button(
            onClick = {
                if (name.isNotBlank() && email.isNotBlank() && !dobError) {
                    viewModel.completeInformation(name, dob, email)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Error)
        ) {
            Text("Continue", fontSize = 20.sp, color = White)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }}


@Preview(showBackground = true)
@Composable
fun FollowUpScreenPreview() {
    PomodojoTheme {
        FollowUpScreen()
    }
}


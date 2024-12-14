package com.example.pomodojo.auth.screens

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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodojo.auth.ui.theme.*
import com.example.pomodojo.auth.viewmodels.FollowUpViewModel
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

    val currentView = LocalView.current

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
        verticalArrangement = Arrangement.SpaceEvenly, // Distribute items evenly
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Help Us Complete Your Account!",
            color = White,
            fontSize = 22.sp,
            modifier = Modifier.padding(top = 32.dp) // Add padding to move it lower
        )

        HorizontalDivider(
            modifier = Modifier
                .height(2.dp)
                .padding(start = 16.dp, end = 16.dp),
            color = Color.White
        )

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

        Button(
            onClick = {
                if (name.isNotBlank() && email.isNotBlank() && !dobError) {
                    viewModel.completeInformation(name, dob, email, currentView)
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f) // Slightly narrow width
                .height(48.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Error)
        ) {
            Text("Continue", fontSize = 20.sp, color = White)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun FollowUpScreenPreview() {
    PomodojoTheme {
        FollowUpScreen()
    }
}

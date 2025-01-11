package com.example.pomodojo.functionality.dashboard.screens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodojo.R
import com.example.pomodojo.functionality.dashboard.viewmodel.HomeViewModel
import com.example.pomodojo.ui.theme.PomodojoTheme
import com.example.pomodojo.core.utils.ErrorSnackBar
import com.example.pomodojo.functionality.facescan.FaceScan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.airbnb.lottie.compose.*
import com.example.pomodojo.functionality.dashboard.Config
import com.example.pomodojo.functionality.pomodoro.ui.WorkTimeActivity

/**
 * Composable function that displays the main screen of the application.
 *
 * @param viewModel The ViewModel that handles the logic for the Home screen.
 * @param backgroundColor The background color of the screen.
 */
@Composable
fun MainScreen(
    viewModel: HomeViewModel = viewModel(),
    backgroundColor: Color = colorResource(R.color.accentL)
) {
    val errorMessage by viewModel.errorMessage.observeAsState()
    val context = LocalContext.current

    var config by remember { mutableStateOf(Config()) }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Welcome!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.primary),
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        viewModel.navigateToFaceScan()
                        context.startActivity(Intent(context, FaceScan::class.java))
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = colorResource(id = R.color.primary),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.data),
                        contentDescription = "Data Icon",
                        tint = colorResource(id = R.color.accentL),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                IconButton(
                    onClick = {
                        viewModel.navigateToFaceScan()
                        context.startActivity(Intent(context, FaceScan::class.java))
                    },
                    modifier = Modifier
                        .padding(12.dp)
                        .size(56.dp)
                        .background(
                            color = colorResource(id = R.color.primary),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.face),
                        contentDescription = "Face Scan",
                        tint = colorResource(id = R.color.accentL),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        Log.d("PomodoroConfig", "Config: $config")
                        val intent = Intent(context, WorkTimeActivity::class.java).apply {
                            putExtra("config", config)
                        }
                        context.startActivity(intent)
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.primary))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.size(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (composition != null) {
                                LottieAnimation(
                                    composition = composition,
                                    progress = { progress },
                                )
                            } else {
                                Text(
                                    text = "Failed to load animation",
                                    color = Color.Red,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Start Pomodoro Session",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.accentL),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Customize your sessions:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.primary),
                textAlign = TextAlign.Start,
                modifier = Modifier.align(Alignment.Start).padding(16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                val cardColors = listOf(
                    colorResource(id = R.color.accentD),
                    colorResource(id = R.color.shadowL),
                    colorResource(id = R.color.shadowD),
                    colorResource(id = R.color.primary)
                )
                val cardText = listOf("Short Break", "Focus Time", "Long Break", "Iterations")

                cardColors.zip(cardText).forEach { (color, text) ->
                    CustomizeSessions(
                        color = color,
                        text = text,
                        initialNumbers = when (text) {
                            "Short Break" -> config.shortBreak
                            "Focus Time" -> config.focusTime
                            "Long Break" -> config.longBreak
                            "Iterations" -> config.iterations
                            else -> 0
                        }
                    ) { updatedNumber ->
                        when (text) {
                            "Short Break" -> config = config.copy(shortBreak = updatedNumber)
                            "Focus Time" -> config = config.copy(focusTime = updatedNumber)
                            "Long Break" -> config = config.copy(longBreak = updatedNumber)
                            "Iterations" -> config = config.copy(iterations = updatedNumber)
                        }
                    }
                }
            }
        }

        errorMessage?.let { (mainMessage, subMessage) ->
            ErrorSnackBar(mainMessage = mainMessage, subMessage = subMessage)
        }
    }
}

@Composable
fun CustomizeSessions(color: Color, text: String, initialNumbers: Int, onValueChange: (Int) -> Unit) {
    var numbers by remember { mutableIntStateOf(initialNumbers) }

    Card(
        modifier = Modifier
            .width(120.dp)
            .height(250.dp)
            .padding(6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    numbers++
                    onValueChange(numbers)
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_up),
                    contentDescription = "Increase",
                    tint = colorResource(id = R.color.accentL),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(
                text = numbers.toString(),
                fontSize = 26.sp,
                color = colorResource(id = R.color.accentL),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Decrease button
            IconButton(
                onClick = {
                    if (numbers > 0) {
                        numbers--
                        onValueChange(numbers) // Update parent state
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_down),
                    contentDescription = "Decrease",
                    tint = colorResource(id = R.color.accentL),
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Label for the card
            Text(
                text = text,
                fontSize = 18.sp,
                color = colorResource(id = R.color.accentL),
                modifier = Modifier
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Composable function that previews the main screen.
 */
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PomodojoTheme {
        MainScreen()
    }
}
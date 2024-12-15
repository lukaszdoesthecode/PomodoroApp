package com.example.pomodojo.functionality.pomodoro.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.pomodojo.R
import com.example.pomodojo.functionality.pomodoro.service.AudioPlayerService
import com.example.pomodojo.functionality.pomodoro.components.MenuBar
import com.example.pomodojo.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ShortBreakScreen(context: Context) {
    var timeLeft by remember { mutableStateOf(5 * 60) } // 5 minutes in seconds
    var isPaused by remember { mutableStateOf(true) } // Timer is initially paused
    val audioPlayer = remember { AudioPlayerService(context) }

    // Play audio when the screen is displayed
    LaunchedEffect(Unit) {
        audioPlayer.playAudio(R.raw.vo_intro)
    }

    // Stop audio when the composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.stopAudio()
        }
    }

    // Launch a coroutine for countdown
    LaunchedEffect(isPaused) {
        while (timeLeft > 0 && !isPaused) {
            delay(1000L) // Wait for 1 second
            timeLeft -= 1
        }
    }

    val minutes = (timeLeft / 60).toString().padStart(2, '0')
    val seconds = (timeLeft % 60).toString().padStart(2, '0')

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Top Image
            Image(
                painter = painterResource(id = R.drawable.ic_short_break),
                contentDescription = "Short Break",
                modifier = Modifier
                    .size(100.dp)
                    .padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Timer
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = minutes,
                    fontSize = 80.sp,
                    color = White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = seconds,
                    fontSize = 80.sp,
                    color = White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // MenuBar: fixed size, aligned at the bottom of its section
            Box(
                modifier = Modifier
                    .width(350.dp)
                    .height(55.dp), // Fixed height for MenuBar
                contentAlignment = Alignment.Center
            ) {
                MenuBar(
                    onLeftClick = {},
                    onCenterClick = {
                        isPaused = it
                        if (!it) {
                            audioPlayer.playAudio(R.raw.vo_guide_2) // Play guide audio on center button click
                        }
                    }, // Update the timer state
                    onRightClick = {
                        if (audioPlayer.isPlaying()) {
                            audioPlayer.stopAudio()
                        } else {
                            audioPlayer.restartAudio() // Restart audio on right button click
                        }
                    },
                    buttonHeight = 54.dp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Square Breathing Visual
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f) // Remaining space
                    .aspectRatio(1f)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                SquareBreathingVisual(isPaused = isPaused)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShortBreakScreenPreview() {
    PomodojoTheme {
        ShortBreakScreen(context = LocalContext.current)
    }
}
package com.example.pomodojo.functionality.pomodoro.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
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
import com.example.pomodojo.core.utils.getConfigFromPreferences
import com.example.pomodojo.functionality.pomodoro.service.AudioPlayerService
import com.example.pomodojo.functionality.pomodoro.components.MenuBar
import com.example.pomodojo.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Composable function for the ShortBreakScreen.
 *
 * @param context The context used to initialize the AudioPlayerService.
 */
@Composable
fun ShortBreakScreen(context: Context) {
    var timeLeft by remember { mutableStateOf(5 * 60) }
    var isPaused by remember { mutableStateOf(true) }
    val audioPlayer = remember { AudioPlayerService(context) }
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    timeLeft = getConfigFromPreferences(context).shortBreak * 60

    LaunchedEffect(Unit) {
        audioPlayer.playAudio(R.raw.vo_intro)
    }

    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.stopAudio()
        }
    }

    LaunchedEffect(isPaused) {
        while (timeLeft > 0 && !isPaused) {
            delay(1000L)
            timeLeft -= 1

            if (timeLeft <= 5) {
                vibrator?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        it.vibrate(500)
                    }
                }
            }
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
            Image(
                painter = painterResource(id = R.drawable.ic_short_break),
                contentDescription = "Short Break",
                modifier = Modifier
                    .size(100.dp)
                    .padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

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

            Box(
                modifier = Modifier
                    .width(350.dp)
                    .height(55.dp),
                contentAlignment = Alignment.Center
            ) {
                MenuBar(
                    onLeftClick = {},
                    onCenterClick = {
                        isPaused = it
                        if (!it) {
                            audioPlayer.playAudio(R.raw.vo_guide_2)
                        }
                    },
                    onRightClick = {
                        if (audioPlayer.isPlaying()) {
                            audioPlayer.stopAudio()
                        } else {
                            audioPlayer.restartAudio()
                        }
                    },
                    buttonHeight = 54.dp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .aspectRatio(1f)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                SquareBreathingVisual(isPaused = isPaused)
            }
        }
    }
}

/**
 * Preview function for the ShortBreakScreen composable.
 */
@Preview(showBackground = true)
@Composable
fun ShortBreakScreenPreview() {
    PomodojoTheme {
        ShortBreakScreen(context = LocalContext.current)
    }
}
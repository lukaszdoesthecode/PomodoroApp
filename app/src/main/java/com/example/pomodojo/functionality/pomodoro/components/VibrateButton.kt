package com.example.pomodojo.functionality.pomodoro.components

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.pomodojo.ui.theme.PomodojoTheme

/**
 * Activity that sets the content to display the VibrateButton composable.
 */
class VibrateButtonActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomodojoTheme {
                VibrateButton()
            }
        }
    }
}

/**
 * Composable function that displays a button which triggers a vibration when clicked.
 */
@Composable
fun VibrateButton() {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    Button(onClick = {
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(500)
            }
        }
    }) {
        Text("Test Vibration")
    }
}package com.example.pomodojo.functionality.pomodoro.components

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.pomodojo.ui.theme.PomodojoTheme

/**
 * Activity that sets the content to display the VibrateButton composable.
 */
class VibrateButtonActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomodojoTheme {
                VibrateButton()
            }
        }
    }
}

/**
 * Composable function that displays a button which triggers a vibration when clicked.
 */
@Composable
fun VibrateButton() {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    Button(onClick = {
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(500)
            }
        }
    }) {
        Text("Test Vibration")
    }
}
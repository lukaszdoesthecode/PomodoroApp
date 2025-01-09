package com.example.pomodojo.functionality.pomodoro.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomodojo.ui.theme.PomodojoTheme
import com.example.pomodojo.ui.theme.Primary
import com.example.pomodojo.ui.theme.White
import com.example.pomodojo.ui.theme.AccentL
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Composable function for the SquareBreathingVisual.
 *
 * @param isPaused Boolean indicating whether the animation is paused.
 */
@Composable
fun SquareBreathingVisual(isPaused: Boolean) {
    var progress by remember { mutableStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition()

    LaunchedEffect(isPaused) {
        if (!isPaused) {
            progress = 0f
        }
    }

    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    if (!isPaused) {
        progress = animatedProgress
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary),
        contentAlignment = Alignment.Center
    ) {
        BreathingSquare(progress = progress)
        CenteredHoldCircle(progress = (progress + 0.25f) % 1f)
    }
}

/**
 * Composable function for the BreathingSquare.
 *
 * @param progress Float value representing the animation progress.
 */
@Composable
fun BreathingSquare(progress: Float) {
    val squareSize = 250.dp
    val density = LocalDensity.current
    val buttonSize = 64.dp

    val context = LocalContext.current
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    val coroutineScope = rememberCoroutineScope()
    var isButtonReleased by remember { mutableStateOf(false) }
    var vibrationJob by remember { mutableStateOf<Job?>(null) }

    Box(
        modifier = Modifier
            .size(squareSize)
            .background(Color.Transparent),
        contentAlignment = Alignment.TopStart
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = with(density) { 4.dp.toPx() }

            drawRoundRect(
                color = White,
                size = size,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(with(density) { 16.dp.toPx() }),
                style = Stroke(width = strokeWidth, pathEffect = PathEffect.cornerPathEffect(with(density) { 16.dp.toPx() }))
            )
        }

        val pathLength = with(density) { squareSize.toPx() * 4 }
        val perimeter = pathLength
        val currentPosition = progress * perimeter

        val dotPosition = when {
            currentPosition <= with(density) { squareSize.toPx() } -> Offset(currentPosition, 0f)
            currentPosition <= with(density) { squareSize.toPx() * 2 } -> Offset(
                with(density) { squareSize.toPx() },
                currentPosition - with(density) { squareSize.toPx() }
            )
            currentPosition <= with(density) { squareSize.toPx() * 3 } -> Offset(
                with(density) { squareSize.toPx() } - (currentPosition - with(density) { squareSize.toPx() * 2 }),
                with(density) { squareSize.toPx() }
            )
            else -> Offset(
                0f,
                with(density) { squareSize.toPx() } - (currentPosition - with(density) { squareSize.toPx() * 3 })
            )
        }

        val offsetModifier = Modifier.offset(
            x = with(density) { (dotPosition.x - buttonSize.toPx() / 2).toDp() },
            y = with(density) { (dotPosition.y - buttonSize.toPx() / 2).toDp() }
        )

        Box(
            modifier = offsetModifier.size(buttonSize),
            contentAlignment = Alignment.Center
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            LaunchedEffect(isPressed) {
                if (isPressed) {
                    vibrationJob?.cancel()
                    vibrationJob = null
                    isButtonReleased = false
                } else {
                    if (!isButtonReleased) {
                        isButtonReleased = true
                        vibrationJob = coroutineScope.launch {
                            delay(5000)
                            if (isButtonReleased) {
                                vibrator?.let {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        it.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                                    } else {
                                        @Suppress("DEPRECATION")
                                        it.vibrate(200)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { /* Handle button click */ },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (isPressed) AccentL else White
                ),
                interactionSource = interactionSource,
                modifier = Modifier.fillMaxSize()
            ) {}
        }
    }
}

/**
 * Composable function for the CenteredHoldCircle.
 *
 * @param progress Float value representing the animation progress.
 */
@Composable
fun CenteredHoldCircle(progress: Float) {
    fun lerpDp(start: Dp, stop: Dp, fraction: Float): Dp {
        return start + (stop - start) * fraction
    }

    val (circleSize, text) = remember(progress) {
        when {
            progress <= 0.25f -> lerpDp(80.dp, 130.dp, progress / 0.25f) to "inhale"
            progress <= 0.5f -> 130.dp to "hold"
            progress <= 0.75f -> lerpDp(130.dp, 80.dp, (progress - 0.5f) / 0.25f) to "exhale"
            else -> 80.dp to "hold"
        }
    }

    Surface(
        shape = CircleShape,
        color = Color(0xFFEDEDED),
        modifier = Modifier.size(circleSize)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = Color.Black,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Preview function for the SquareBreathingVisual composable.
 */
@Preview(showBackground = true)
@Composable
fun SquareBreathingVisualPreview() {
    PomodojoTheme {
        SquareBreathingVisual(isPaused = true)
    }
}
package com.example.pomodojo.functionality.pomodoro.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomodojo.ui.theme.PomodojoTheme
import com.example.pomodojo.ui.theme.Primary

@Composable
fun SquareBreathingVisual(isPaused: Boolean) {
    // Animation progress along the square's path (0f to 1f)
    var progress by remember { mutableStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition()

    LaunchedEffect(isPaused) {
        if (!isPaused) {
            progress = 0f // Reset progress when animation starts
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
            .background(Primary), // Use Primary as the background color
        contentAlignment = Alignment.Center
    ) {
        // Square breathing visualization
        BreathingSquare(progress = progress)
        // Centered circle with animation (delayed by 4 seconds)
        CenteredHoldCircle(progress = (progress + 0.25f) % 1f)
    }
}

@Composable
fun BreathingSquare(progress: Float) {
    val squareSize = 250.dp

    Canvas(modifier = Modifier.size(squareSize)) {
        val pathLength = size.width * 4  // Total path around the square
        val strokeWidth = 4.dp.toPx()

        // Square with rounded corners
        drawRoundRect(
            color = Color.White,
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
            style = Stroke(width = strokeWidth, pathEffect = PathEffect.cornerPathEffect(16.dp.toPx()))
        )

        // Dot movement calculation
        val perimeter = pathLength
        val currentPosition = progress * perimeter

        val dotPosition = when {
            currentPosition <= size.width -> Offset(currentPosition, 0f) // Top side
            currentPosition <= size.width * 2 -> Offset(size.width, currentPosition - size.width) // Right side
            currentPosition <= size.width * 3 -> Offset(size.width - (currentPosition - size.width * 2), size.height) // Bottom side
            else -> Offset(0f, size.height - (currentPosition - size.width * 3)) // Left side
        }

        // Moving dot
        drawCircle(
            color = Color.White,
            radius = 8.dp.toPx(),
            center = dotPosition
        )
    }
}

@Composable
fun CenteredHoldCircle(progress: Float) {
    // Custom lerp function for Dp interpolation
    fun lerpDp(start: Dp, stop: Dp, fraction: Float): Dp {
        return start + (stop - start) * fraction
    }

    // Calculate circle size and text based on delayed progress
    val (circleSize, text) = remember(progress) {
        when {
            progress <= 0.25f -> lerpDp(80.dp, 130.dp, progress / 0.25f) to "inhale" // Expanding (0 - 25%)
            progress <= 0.5f -> 130.dp to "hold" // Hold at maximum size (25% - 50%)
            progress <= 0.75f -> lerpDp(130.dp, 80.dp, (progress - 0.5f) / 0.25f) to "exhale" // Contracting (50% - 75%)
            else -> 80.dp to "hold" // Hold at minimum size (75% - 100%)
        }
    }

    Surface(
        shape = CircleShape,
        color = Color(0xFFEDEDED), // Light circle color
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

@Preview(showBackground = true)
@Composable
fun SquareBreathingVisualPreview() {
    PomodojoTheme {
        SquareBreathingVisual(isPaused = true)
    }
}
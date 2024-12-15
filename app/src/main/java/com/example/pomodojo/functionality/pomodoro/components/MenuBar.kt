package com.example.pomodojo.functionality.pomodoro.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pomodojo.ui.theme.*

@Composable
fun MenuBar(
    onLeftClick: () -> Unit = {},
    onCenterClick: (Boolean) -> Unit = {}, // Callback for Pause/Play
    onRightClick: (Boolean) -> Unit = {}, // Callback for Lightbulb ON/OFF
    buttonHeight: androidx.compose.ui.unit.Dp = 56.dp // Default height
) {
    // State for toggling Pause/Play and Lightbulb ON/OFF
    var isPaused by remember { mutableStateOf(true) }
    var isLightOn by remember { mutableStateOf(true) }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        // Left Button - Small, White
        MenuButton(
            iconId = com.example.pomodojo.R.drawable.ic_options,
            backgroundColor = ShadowD,
            height = buttonHeight,
            onClick = onLeftClick
        )

        // Center Button - Toggle Pause/Play
        MenuButton(
            iconId = if (isPaused) com.example.pomodojo.R.drawable.ic_play else com.example.pomodojo.R.drawable.ic_pause,
            backgroundColor = ShadowL,
            height = buttonHeight * 1.3f, // Slightly larger center button
            onClick = {
                isPaused = !isPaused
                onCenterClick(isPaused) // Notify parent of state change
            }
        )

        // Right Button - Toggle Lightbulb ON/OFF
        MenuButton(
            iconId = if (isLightOn) com.example.pomodojo.R.drawable.ic_lightbulb_on else com.example.pomodojo.R.drawable.ic_lightbulb_off,
            backgroundColor = ShadowD,
            height = buttonHeight,
            onClick = {
                isLightOn = !isLightOn
                onRightClick(isLightOn) // Notify parent of state change
            }
        )
    }
}

@Composable
fun MenuButton(
    iconId: Int,
    backgroundColor: Color,
    height: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val width = height * 1.5f // 3:2 width-to-height ratio

    Surface(
        shape = RoundedCornerShape(8.dp), // Rounded rectangle shape
        color = backgroundColor,
        shadowElevation = 4.dp,
        modifier = Modifier
            .width(width)
            .height(height)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier.size(height / 2) // Icon size proportional to the button height
            )
        }
    }
}
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

/**
 * Composable function for the MenuBar with three buttons: left, center, and right.
 *
 * @param onLeftClick Callback for the left button click.
 * @param onCenterClick Callback for the center button click, toggles Pause/Play.
 * @param onRightClick Callback for the right button click, toggles Lightbulb ON/OFF.
 * @param buttonHeight Height of the buttons in the MenuBar.
 */
@Composable
fun MenuBar(
    onLeftClick: () -> Unit = {},
    onCenterClick: (Boolean) -> Unit = {},
    onRightClick: (Boolean) -> Unit = {},
    buttonHeight: androidx.compose.ui.unit.Dp = 56.dp
) {
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
            height = buttonHeight * 1.3f,
            onClick = {
                isPaused = !isPaused
                onCenterClick(isPaused)
            }
        )

        // Right Button - Toggle Lightbulb ON/OFF
        MenuButton(
            iconId = if (isLightOn) com.example.pomodojo.R.drawable.ic_lightbulb_on else com.example.pomodojo.R.drawable.ic_lightbulb_off,
            backgroundColor = ShadowD,
            height = buttonHeight,
            onClick = {
                isLightOn = !isLightOn
                onRightClick(isLightOn)
            }
        )
    }
}

/**
 * Composable function for a MenuButton with an icon.
 *
 * @param iconId Resource ID of the icon to be displayed on the button.
 * @param backgroundColor Background color of the button.
 * @param height Height of the button.
 * @param onClick Callback for the button click.
 */
@Composable
fun MenuButton(
    iconId: Int,
    backgroundColor: Color,
    height: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val width = height * 1.5f

    Surface(
        shape = RoundedCornerShape(8.dp),
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
                modifier = Modifier.size(height / 2)
            )
        }
    }
}
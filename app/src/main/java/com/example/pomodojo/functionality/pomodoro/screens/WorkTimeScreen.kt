package com.example.pomodojo.functionality.pomodoro.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomodojo.R
import com.example.pomodojo.functionality.pomodoro.state.SessionState
import com.example.pomodojo.ui.theme.PomodojoTheme
import com.example.pomodojo.ui.theme.Primary
import com.example.pomodojo.ui.theme.ShadowD
import com.example.pomodojo.ui.theme.ShadowL
import com.example.pomodojo.ui.theme.White


@Composable
fun WorkTimeScreen(
    modifier: Modifier = Modifier,
    onClickStartStop: () -> Unit,
    onClickReset: () -> Unit,
    onClickSkip: () -> Unit,
    timeSeconds: Int,
    sessionState: Enum<SessionState>
) {
    val (mins, secs) = getMinsSecs(timeSeconds)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary)
            .padding(0.dp, 50.dp, 0.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,

        ) {
        //text in a box with rounded corners
        Box(
            modifier = Modifier
                .background(
                    Color.LightGray,
                    shape = RoundedCornerShape(20.dp)
                )
                .border(BorderStroke(2.dp, Color.Black), shape = RoundedCornerShape(20.dp))
                .padding(8.dp)
        ) {
            Text(
                text = getSessionStateString(sessionState),
                modifier = Modifier,
                color = Color.Black,
                fontWeight = Bold
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = mins,
                    modifier = Modifier.padding(0.dp),
                    color = White,
                    style = TextStyle(
                        fontSize = 200.sp,
                        fontWeight = FontWeight.ExtraBold,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                )
                Text(
                    text = secs,
                    modifier = Modifier.padding(0.dp),
                    color = White,
                    style = TextStyle(
                        fontSize = 200.sp,
                        fontWeight = FontWeight.ExtraBold,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClickReset,
                    modifier = Modifier
                        .background(ShadowD, shape = RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Pause Icon",
                        tint = White
                    )
                }
                IconButton(
                    onClick = onClickStartStop,
                    modifier = Modifier
                        .background(ShadowD, shape = RoundedCornerShape(16.dp))
                        .size(75.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Pause Icon",
                        tint = White
                    )
                }
                IconButton(
                    onClick = onClickSkip,
                    modifier = Modifier
                        .background(ShadowD, shape = RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Pause Icon",
                        tint = White
                    )
                }
            }
        }
    }
}

fun getMinsSecs(time: Int): Pair<String, String> {
    val mins = time / 60
    val secs = time % 60
    return Pair(mins.toString().padStart(2, '0'), secs.toString().padStart(2, '0'))
}

fun getSessionStateString(sessionState: Enum<SessionState>): String {
    return when (sessionState) {
        SessionState.WORK -> "Focus"
        SessionState.SHORT_BREAK -> "Short Break"
        SessionState.LONG_BREAK -> "Long Break"
        else -> "Unknown"
    }
}

@Preview(showBackground = true)
@Composable
fun TimerPreview() {
    PomodojoTheme {
        WorkTimeScreen(
            onClickStartStop = {},
            onClickReset = {},
            onClickSkip = {},
            timeSeconds = 200,
            sessionState = SessionState.WORK
        )
    }
}
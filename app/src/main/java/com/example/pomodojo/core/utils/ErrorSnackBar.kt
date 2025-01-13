package com.example.pomodojo.core.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pomodojo.R
import com.example.pomodojo.ui.theme.Error
import com.example.pomodojo.ui.theme.Primary

/**
 * A composable function that displays an error snackbar with a main message and a sub-message.
 *
 * @param mainMessage The main error message to display.
 * @param subMessage The sub-message providing additional context about the error.
 */
@Composable
fun ErrorSnackBar(mainMessage: String, subMessage: String) {
    if (mainMessage.isNotEmpty() || subMessage.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 48.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(start = 40.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (mainMessage.isNotEmpty()) {
                        Text(
                            text = mainMessage,
                            color = Error,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                        )
                    }
                    if (subMessage.isNotEmpty()) {
                        Text(
                            text = subMessage,
                            color = Primary,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            Image(
                painter = painterResource(id = R.drawable.ic_error),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.CenterStart)
                    .offset(x = (-32).dp)
            )
        }
    }
}

/**
 * A preview composable function to display the ErrorSnackBar in the Android Studio preview.
 */
@Preview(showBackground = true)
@Composable
fun PreviewErrorSnackBar() {
    ErrorSnackBar(mainMessage = "Error occurred", subMessage = "Please try again")
}
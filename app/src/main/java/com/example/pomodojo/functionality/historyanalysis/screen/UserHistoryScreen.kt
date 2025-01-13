package com.example.pomodojo.functionality.historyanalysis.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomodojo.functionality.historyanalysis.viewmodel.UserHistoryViewModel
import com.example.pomodojo.functionality.historyanalysis.viewmodel.generateAdvice
import com.example.pomodojo.ui.theme.Primary
import com.example.pomodojo.ui.theme.White

/**
 * Composable function that displays the user's Pomodoro history and advice.
 *
 * @param viewModel The ViewModel providing user history data.
 *
 * This function observes the user's total and weekly Pomodoro, long breathing exercises,
 * and short breathing exercises counts, displaying them on the screen. It also provides
 * personalized advice based on the user's activity.
 */


@Composable
fun UserHistoryScreen(viewModel: UserHistoryViewModel) {

    val pomodoros by viewModel.totalPomodoros.collectAsState()
    val longExercises by viewModel.totalLongExercises.collectAsState()
    val shortExercises by viewModel.totalShortExercises.collectAsState()

    val weeklyPomodoros by viewModel.weeklyPomodoros.collectAsState()
    val weeklyLongExercises by viewModel.weeklyLongExercises.collectAsState()
    val weeklyShortExercises by viewModel.weeklyShortExercises.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF2F3E46)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "Analyze your history!",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFCAD2C5)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pomodoros in total: $pomodoros",
                fontSize = 20.sp,
                color = Color(0xFFCAD2C5)
            )
            Text(
                text = "Long breathing exercises in total: $longExercises",
                fontSize = 20.sp,
                color = Color(0xFFCAD2C5)
            )
            Text(
                text = "Short breathing exercises in total: $shortExercises",
                fontSize = 20.sp,
                color = Color(0xFFCAD2C5)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "This week's history",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFCAD2C5)
            )

            Text(
                text = "Pomodoros this week: $weeklyPomodoros",
                fontSize = 20.sp,
                color = Color(0xFFCAD2C5)
            )
            Text(
                text = "Long breathing exercises this week: $weeklyLongExercises",
                fontSize = 20.sp,
                color = Color(0xFFCAD2C5)
            )
            Text(
                text = "Short breathing exercises this week: $weeklyShortExercises",
                fontSize = 20.sp,
                color = Color(0xFFCAD2C5)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Advice Section
            Text(
                text = "Main advice to you:",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFCAD2C5)
            )
            val advice = generateAdvice(pomodoros, longExercises, shortExercises, weeklyPomodoros, weeklyLongExercises, weeklyShortExercises)
            advice.forEach { text ->
                Text(
                    text = text,
                    fontSize = 18.sp,
                    color = Color(0xFFCAD2C5)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.navigateToHome() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(containerColor = White)
            ) {
                Text("Back Home", fontSize = 20.sp, color = Primary)
            }
        }
    }
}

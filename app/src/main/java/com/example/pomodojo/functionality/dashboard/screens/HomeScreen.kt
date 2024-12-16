package com.example.pomodojo.functionality.dashboard.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodojo.R
import com.example.pomodojo.functionality.dashboard.viewmodel.HomeViewModel
import com.example.pomodojo.ui.theme.PomodojoTheme
import com.example.pomodojo.core.utils.ErrorSnackBar
import com.example.pomodojo.functionality.facescan.FaceScan

/**
 * Composable function that displays the main screen of the application.
 *
 * @param viewModel The ViewModel that handles the logic for the Home screen.
 * @param backgroundColor The background color of the screen.
 */
@Composable
fun MainScreen(
    viewModel: HomeViewModel = viewModel(),
    backgroundColor: Color = colorResource(R.color.primary)
) {
    val errorMessage by viewModel.errorMessage.observeAsState()
    val context = LocalContext.current

    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome, User!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Welcome message",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                TimeOption("Short time", "5")
                TimeOption("Focus time", "25")
                TimeOption("Long break", "15")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Music genre",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Music genre",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "How's Your Mood Today?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                MoodIcon("😊")
                MoodIcon("😐")
                MoodIcon("😴")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.navigateToPomodoro() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Start New Session", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "View Your Past Sessions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Start,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(10) {
                    PastSessionCard(
                        date = "Date $it",
                        timeSpent = "${10 + it} mins",
                        cycles = "${1 + it} cycles"
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Navigate to more sessions */ },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("See more", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.navigateToFaceScan()
                    context.startActivity(Intent(context, FaceScan::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("FaceScan", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        errorMessage?.let { (mainMessage, subMessage) ->
            ErrorSnackBar(mainMessage = mainMessage, subMessage = subMessage)
        }
    }
}

/**
 * Composable function that displays the bottom navigation bar.
 */
@Composable
fun BottomNavigationBar() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        Pair("Ranking", painterResource(id = R.drawable.ic_trophy)),
        Pair("Home", painterResource(id = R.drawable.ic_house)),
        Pair("Settings", painterResource(id = R.drawable.ic_settings))
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.second, contentDescription = item.first) },
                label = { Text(item.first) },
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )
        }
    }
}

/**
 * Composable function that displays a card for a past session.
 *
 * @param date The date of the past session.
 * @param timeSpent The time spent in the past session.
 * @param cycles The number of cycles completed in the past session.
 */
@Composable
fun PastSessionCard(date: String, timeSpent: String, cycles: String) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(200.dp)
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Text(text = date, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(text = timeSpent, fontSize = 14.sp)
        Text(text = cycles, fontSize = 14.sp)
    }
}

/**
 * Composable function that displays a time option.
 *
 * @param title The title of the time option.
 * @param time The time value of the time option.
 */
@Composable
fun TimeOption(title: String, time: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = time,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
    }
}

/**
 * Composable function that displays a mood icon.
 *
 * @param emoji The emoji representing the mood.
 */
@Composable
fun MoodIcon(emoji: String) {
    Text(
        text = emoji,
        fontSize = 32.sp,
        textAlign = TextAlign.Center
    )
}

/**
 * Composable function that previews the main screen.
 */
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PomodojoTheme {
        MainScreen()
    }
}
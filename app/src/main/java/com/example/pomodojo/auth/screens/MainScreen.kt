package com.example.pomodojo.auth.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodojo.auth.viewmodels.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = { viewModel.navigateToFaceScan() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Face Recognition")
        }

        Button(
            onClick = { viewModel.navigateToSpotify() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Spotify")
        }

        Button(
            onClick = { viewModel.navigateToPomodoro() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pomodoro Timer")
        }
    }
}

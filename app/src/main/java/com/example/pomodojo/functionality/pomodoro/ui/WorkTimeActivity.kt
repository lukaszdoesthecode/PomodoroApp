package com.example.pomodojo.functionality.pomodoro.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.pomodojo.functionality.pomodoro.state.SessionState
import com.example.pomodojo.functionality.pomodoro.service.TimerService
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.pomodojo.ui.theme.PomodojoTheme

class WorkTimeActivity : ComponentActivity() {

    private var timerService: TimerService? = null
    private var timerRunning by mutableStateOf(false)

    private var serviceBoundState by mutableStateOf(false)
    private var time by mutableStateOf(5)
    private var sessionState by mutableStateOf(SessionState.WORK)

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerService.LocalBinder
            timerService = binder.getService()

            val combinedFlow = combine(
                timerService?.timeFlow ?: flowOf(),
                timerService?.sessionStatusFlow ?: flowOf(),
                timerService?.timerRunningFlow ?: flowOf()
            ) { time, sessionStatus, timerRunning ->
                Triple(time, sessionStatus, timerRunning)
            }

            lifecycleScope.launch {
                combinedFlow.collect { (newTime, newSessionStatus, newTimerRunning) ->
                    time = newTime
                    sessionState = newSessionStatus
                    timerRunning = newTimerRunning
                }
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            serviceBoundState = false
            timerService = null
        }
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createSharedPrefs()
        enableEdgeToEdge()

        checkAndRequestNotificationPermission()
        tryToBindToServiceIfRunning()
        if (!restoreSessionStateSharedPrefs()) {
            // Optional error handling can be added here if needed
        }

        Log.d(TAG, "onCreate WorkTimeActivity")

        setContent {
            PomodojoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TimerView(
                        modifier = Modifier.padding(innerPadding),
                        onClickStartStop = ::startStopService,
                        onClickReset = ::resetAll,
                        onClickSkip = ::navigateToShortBreak,
                        timeSeconds = time,
                        sessionState = sessionState
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (timerService != null) {
            //unbindService(connection)
        }
    }

    private fun tryToBindToServiceIfRunning() {
        Intent(this, TimerService::class.java).also { intent ->
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
    }

    private fun runMyService() {
        val intent = Intent(this, TimerService::class.java)
        intent.putExtra("time", time)
        intent.putExtra("sessionState", sessionState)
        startForegroundService(intent)
        tryToBindToServiceIfRunning()
        timerRunning = true
    }

    private fun stopService() {
        timerService?.stopForegroundService()
        unbindService(connection)
        timerRunning = false
    }

    private fun resetAll() {
        val sharedPref = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.remove("current_session")
        editor.remove("remaining_time")
        editor.remove("current_state_index")
        editor.apply()
        if (timerRunning) stopService()
        if (!restoreSessionStateSharedPrefs()) {
            Toast.makeText(this, "Reset", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startStopService() {
        if (timerRunning) {
            stopService()
        } else {
            runMyService()
        }
    }

    private fun createSharedPrefs() {
        val sharedPref = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("short_break_duration", 5)
        editor.putInt("long_break_duration", 15)
        editor.putInt("work_duration", 25)
        editor.apply()
    }

    private fun restoreSessionStateSharedPrefs(): Boolean {
        val sharedPref = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val storedCurrentSession = sharedPref.getInt("current_session", -1)
        val storedTime = sharedPref.getInt("remaining_time", -1)

        return if (storedCurrentSession != -1 && storedTime != -1) {
            time = storedTime
            sessionState = SessionState.entries[storedCurrentSession]
            true
        } else {
            time = sharedPref.getInt("work_duration", 25)
            sessionState = SessionState.WORK
            false
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            when (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            )) {
                android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                }

                else -> {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun navigateToShortBreak() {
        val intent = Intent(this, ShortBreakActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

@Composable
fun TimerView(
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
            .padding(0.dp, 50.dp, 0.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
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
                fontWeight = FontWeight.Bold
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
                    text = "$mins:$secs",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = onClickStartStop) {
                    Text("Start/Stop")
                }
                Button(onClick = onClickReset) {
                    Text("Reset")
                }
                Button(onClick = onClickSkip) {
                    Text("Skip")
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
        TimerView(
            onClickStartStop = {},
            onClickReset = {},
            onClickSkip = {},
            timeSeconds = 200,
            sessionState = SessionState.WORK
        )
    }
}

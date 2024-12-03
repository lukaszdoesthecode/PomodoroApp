package com.example.pomodojo.functionality.pomodoro.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.pomodojo.ui.theme.PomodojoTheme
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.pomodojo.functionality.pomodoro.state.SessionState
import com.example.pomodojo.functionality.pomodoro.service.TimerService
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import android.view.View
import androidx.compose.ui.platform.ComposeView
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup

//todo: find out how to make the persistent notification

//todo: try to fix the background service so that it reliably runs
// when the app is closed

class WorkTimeActivity : ComponentActivity() {

    private var timerService: TimerService? = null
    private var timerRunning by mutableStateOf(false)

    private var serviceBoundState by mutableStateOf(false)
    private var time by mutableStateOf(5)
    private var sessionState by mutableStateOf(SessionState.WORK)

    // needed to communicate with the service.
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // we've bound to ExampleLocationForegroundService, cast the IBinder and get ExampleLocationForegroundService instance.
            Log.d(TAG, "onServiceConnected")

            val binder = service as TimerService.LocalBinder
            timerService = binder.getService()

            val combinedFlow = combine(
                timerService?.timeFlow ?: flowOf(),
                timerService?.sessionStatusFlow ?: flowOf()
            ) { time, sessionStatus ->
                Pair(time, sessionStatus)
            }

            // Collect the combined flow
            lifecycleScope.launch {
                combinedFlow.collect { (newTime, newSessionStatus) ->
                    time = newTime
                    sessionState = newSessionStatus
                }
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            // This is called when the connection with the service has been disconnected. Clean up.
            Log.d(TAG, "onServiceDisconnected")
            serviceBoundState = false
            timerService = null
            //showGeneralError("Service Disconnected", "The connection to the timer service was lost.")
        }
    }

    // we need notification permission to be able to display a notification for the foreground service
    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            // if permission was denied, the service can still run only the notification won't be visible
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createSharedPrefs()
        enableEdgeToEdge()
        setContent {
            PomodojoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TimerView(
                        modifier = Modifier.padding(innerPadding),
                        onClickStartStop = ::startStopService,
                        onClickReset = ::resetAll,
                        timeSeconds = time,
                        sessionState = sessionState
                    )
                }
            }
        }
        Log.d(TAG, "onCreate WorkTimeActivity")
        checkAndRequestNotificationPermission()
        if (!restoreSessionStateSharedPrefs()) {
            //showGeneralError("Error", "Failed to restore session state.")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (timerService != null) {
            unbindService(connection)
        }
        //unbindService(connection)
    }

    private fun tryToBindToServiceIfRunning() {
        Intent(this, TimerService::class.java).also { intent ->
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
    }

    private fun runMyService() {
        Log.d(TAG, "runMyService")
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
            //showGeneralError("Error", "Failed to restore session state.")
        } else {
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
            Toast.makeText(this, "Restored session state", Toast.LENGTH_SHORT).show()
            true
        } else {
            time = sharedPref.getInt("work_duration", 25)
            sessionState = SessionState.WORK
            false
        }
    }

    /**
     * Check for notification permission before starting the service so that the notification is visible
     */
    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            )) {
                android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "Notification permission already granted")
                }

                else -> {
                    Log.d(TAG, "Launching notification permission request")
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
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

@Composable
fun TimerView(
    modifier: Modifier = Modifier,
    onClickStartStop: () -> Unit,
    onClickReset: () -> Unit,
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
                    onClick = {},
                    modifier = Modifier
                        .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More"
                    )
                }
                IconButton(
                    onClick = onClickStartStop,
                    modifier = Modifier
                        .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                        .size(75.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Pause Icon"
                    )
                }
                IconButton(
                    onClick = onClickReset,
                    modifier = Modifier
                        .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Pause Icon"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimerPreview() {
    PomodojoTheme {
        TimerView(
            onClickStartStop = {},
            onClickReset = {},
            timeSeconds = 200,
            sessionState = SessionState.WORK
        )
    }
}
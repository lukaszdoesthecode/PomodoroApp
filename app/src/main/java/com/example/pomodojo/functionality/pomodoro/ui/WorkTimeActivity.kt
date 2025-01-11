package com.example.pomodojo.functionality.pomodoro.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pomodojo.functionality.pomodoro.screens.WorkTimeScreen
import com.example.pomodojo.ui.theme.PomodojoTheme

class WorkTimeActivity : ComponentActivity() {

    private var timerService: TimerService? = null
    private var timerRunning by mutableStateOf(false)
    private var timerPaused by mutableStateOf(false)

    private var serviceBoundState by mutableStateOf(false)
    private var time by mutableStateOf(5)
    private var sessionState by mutableStateOf(SessionState.WORK)

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerService.LocalBinder
            timerService = binder.getService()
            timerService?.onFinishSession = ::finishSessionAction

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

    fun finishSessionAction(info: SessionState){
        Log.d("testing-3", "Finish action")
        resetAll()
        navigateToShortBreak()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createSharedPrefs()
        enableEdgeToEdge()

        checkAndRequestNotificationPermission()
        tryToBindToServiceIfRunning()



        Log.d(TAG, "onCreate WorkTimeActivity")

        setContent {
            PomodojoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WorkTimeScreen(
                        modifier = Modifier.padding(innerPadding),
                        onClickStartStop = ::startStopService,
                        onClickReset = ::resetAll,
                        onClickSkip = { navigateToShortBreak()
                                        stopService()
                                        resetAll()
                                      },
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



    private fun runService() {
        val intent = Intent(this, TimerService::class.java)

        //intent.putExtra("time", time)
       // intent.putExtra("sessionState", sessionState)
        startForegroundService(intent)
        tryToBindToServiceIfRunning()
        timerRunning = true
        timerPaused = false
    }

    private fun resumeService(){
        val intent = Intent(this, TimerService::class.java)
        intent.putExtra("action", "resume")
        startForegroundService(intent)
        tryToBindToServiceIfRunning()
        timerRunning = true
        timerPaused = false
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
        //if (!restoreSessionStateSharedPrefs()) {
         //   Toast.makeText(this, "Reset", Toast.LENGTH_SHORT).show()
       // }
        time = sharedPref.getInt("work_duration", 25)
        sessionState = SessionState.WORK
    }

    private fun startStopService() {
        if (timerRunning) {
            stopService()
            timerPaused = true
        } else if(timerPaused) {
            resumeService()
        }else{
            runService()
            timerPaused = false
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







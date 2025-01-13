package com.example.pomodojo.functionality.pomodoro.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ServiceCompat
import android.os.Binder
import androidx.compose.runtime.Composable
import com.example.pomodojo.core.utils.ErrorSnackBar
import com.example.pomodojo.core.utils.NotificationsHelper
import com.example.pomodojo.functionality.auth.screens.LoginScreen
import com.example.pomodojo.functionality.pomodoro.state.SessionState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object NotificationUtils {

}


//todo: use shared preferences to store the session duration and break duration


class TimerService : Service() {
    private val binder = LocalBinder()
    val _timeFlow = MutableStateFlow(1500)
    val timeFlow: StateFlow<Int> get() = _timeFlow
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    val _sessionStatusFlow = MutableStateFlow(SessionState.WORK)
    val sessionStatusFlow: StateFlow<SessionState> get() = _sessionStatusFlow
    val _timerRunning = MutableStateFlow(false)
    val timerRunningFlow: StateFlow<Boolean> get() = _timerRunning
    var onFinishSession: (SessionState) -> Unit = {}

    private var performMainWorkJob: Job? = null
    lateinit var sharedPref: SharedPreferences

    var sessionTimeMap = mapOf(
        SessionState.WORK to 1500,
        SessionState.SHORT_BREAK to 300,
        SessionState.LONG_BREAK to 900
    )
    var currentSessionIndex = 0

    private val sessionSequence = listOf(
        SessionState.WORK,
        //SessionState.SHORT_BREAK,
       // SessionState.WORK,
        //SessionState.SHORT_BREAK,
       // SessionState.WORK,
       // SessionState.SHORT_BREAK,
       // SessionState.WORK,
       // SessionState.LONG_BREAK
    )
    // Other service code...

    companion object {
        private const val TAG = "TimerService"
    }

    fun updateTime(newTime: Int) {
        _timeFlow.value = newTime
    }

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "onBind service")
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        sharedPref = getSharedPreferences("PomodojoPrefs", MODE_PRIVATE)
        initSessionState()
    }

    private suspend fun makeToastAsync(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(
                applicationContext, message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun formatTime(time: Int): String {
        val minutes = time / 60
        val seconds = time % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

//    private suspend fun performMainWork() {
//        withContext(Dispatchers.Main) {
//            performMainWorkLogic(
//                timeFlow = timeFlow,
//                sessionSequence = sessionSequence,
//                currentSessionIndex = currentSessionIndex,
//                sessionStatusFlow = sessionStatusFlow,
//                updateTime = { newTime -> updateTime(newTime) },
//                updateNotification = { title, text -> updateNotification(title, text) },
//                changeSessionState = { changeSessionStateNonComposable() },
//                makeToastAsync = { message -> makeToastAsync(message) },
//                stopForeground = { stopForeground(STOP_FOREGROUND_REMOVE) },
//                stopSelf = { stopSelf() }
//            )
//        }
//    }

    private suspend fun performMainWorkLogic(
        timeFlow: StateFlow<Int>,
        sessionSequence: List<SessionState>,
        currentSessionIndex: Int,
        sessionStatusFlow: StateFlow<SessionState>,
        updateTime: (Int) -> Unit,
        updateNotification: (String, String) -> Unit,
        changeSessionState: () -> Unit,
        makeToastAsync: suspend (String) -> Unit,
        stop: () -> Unit
    ) {
        //CoroutineScope(Dispatchers.Main).launch {
            _timerRunning.value = true
            while (timeFlow.value > 0) {
                delay(1000)
                updateTime(timeFlow.value - 1)
                val currentSessionString = getSessionStateStringNonComposable(sessionSequence[currentSessionIndex])
                updateNotification(
                    "Pomodojo",
                    "$currentSessionString: ${formatTime(timeFlow.value)}"
                )

                if (timeFlow.value == 0) {
                    onFinishSession(sessionSequence[currentSessionIndex])
                    stopForegroundService()
                    /*changeSessionState()
                    makeToastAsync(
                        "Session starting: ${
                            getSessionStateStringNonComposable(
                                sessionStatusFlow.value
                            )
                        }"
                    )*/
           //     }
                }
          }
        stop()
    }

    private fun getSessionStateStringNonComposable(sessionState: SessionState): String {
        return when (sessionState) {
            SessionState.WORK -> "Focus"
            SessionState.SHORT_BREAK -> "Short Break"
            SessionState.LONG_BREAK -> "Long Break"
            else -> "Unknown"
        }
    }

    private fun changeSessionStateNonComposable() {
        currentSessionIndex = (currentSessionIndex + 1) % sessionSequence.size
        val nextSession = sessionSequence[currentSessionIndex]
        _sessionStatusFlow.value = nextSession
        updateTime(sessionTimeMap[nextSession] ?: 1500)
        Log.d("testing-1", sessionTimeMap.toString())
        Log.d(
            "Session State",
            "Starting Session: ${getSessionStateStringNonComposable(nextSession)}"
        )
    }

    fun updateNotification(title: String, text: String) {
        val notification = NotificationsHelper.buildNotification(this, title, text)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        sessionTimeMap = mapOf(
//            SessionState.WORK to sharedPref.getInt("work_duration", 1500),
//            SessionState.SHORT_BREAK to sharedPref.getInt("short_break_duration", 300),
//            SessionState.LONG_BREAK to sharedPref.getInt("long_break_duration", 900)
//        )
        //initSessionState()
        if(intent?.getStringExtra("action") == "resume"){
            restoreSessionStateSharedPrefs()
        }

        performMainWorkJob = serviceScope.launch {
            performMainWorkLogic(
                timeFlow = timeFlow,
                sessionSequence = sessionSequence,
                currentSessionIndex = currentSessionIndex,
                sessionStatusFlow = sessionStatusFlow,
                updateTime = { newTime -> updateTime(newTime) },
                updateNotification = { title, text -> updateNotification(title, text) },
                changeSessionState = { changeSessionStateNonComposable() },
                makeToastAsync = { message -> makeToastAsync(message) },
                stop = { stopForegroundService()},
            )
        }

        // create the notification channel


        NotificationsHelper.createNotificationChannel(this)

        // promote service to foreground service
        ServiceCompat.startForeground(
            this,
            1,
            NotificationsHelper.buildNotification(this, "Pomodojo", "Session starting"),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            } else {
                0
            }
        )

        return START_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        Log.d("Stopping", "Stopping Service")

        return super.stopService(name)
    }

    fun stopForegroundService() {
        _timerRunning.value = false
        performMainWorkJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    fun initSessionState() {
        sessionTimeMap = mapOf(
            SessionState.WORK to sharedPref.getInt("focusTime", 25) * 60,
            SessionState.SHORT_BREAK to sharedPref.getInt("shortBreak", 5) * 60,
            SessionState.LONG_BREAK to sharedPref.getInt("longBreak", 20) * 60
        )
       // val restored = restoreSessionStateSharedPrefs()
        Log.d("testing-2", "initSessionState()")
        Log.d("testing-2", sessionTimeMap.toString())
        Log.d("testing-2", (sessionTimeMap[sessionSequence[0]] ?: 1500).toString())
        //if (!restored) {
            _timeFlow.value = sessionTimeMap[sessionSequence[0]] ?: 1500
            _sessionStatusFlow.value = SessionState.WORK
       // }

        Log.d("testing-2", _timeFlow.value.toString() )
    }

    private fun restoreSessionStateSharedPrefs(): Boolean {
        val sharedPref = getSharedPreferences("PomodojoPrefs", MODE_PRIVATE)
        val storedCurrentState = sharedPref.getInt("current_state_index", -1)
        val storedTime = sharedPref.getInt("remaining_time", -1)

        return if (storedCurrentState != -1 && storedTime != -1) {
            _timeFlow.value = storedTime
            currentSessionIndex = storedCurrentState
            _sessionStatusFlow.value = sessionSequence[currentSessionIndex]
            true
        } else {
            _timeFlow.value = sessionTimeMap[SessionState.WORK] ?: 1500
            _sessionStatusFlow.value = SessionState.WORK
            true
        }
}

    fun storeSessionStateSharedPrefs() {
        val sharedPref = getSharedPreferences("PomodojoPrefs", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("current_state_index", currentSessionIndex)
        editor.putInt("remaining_time", timeFlow.value)
        editor.putInt("current_session", sessionStatusFlow.value.ordinal)
        editor.apply()
        Log.d("Stored", "Stored session state")
    }

    override fun onDestroy() {
        performMainWorkJob?.cancel()
        storeSessionStateSharedPrefs()
        Log.d("Stopped", "Service Stopped")
        super.onDestroy()
    }

    @Composable
    private fun showGeneralErrorComposable() {
        ErrorSnackBar(mainMessage = "Service Stopped", subMessage = "Service execution completed")
    }

    @Composable
    fun OnDestroyComposable() {
        showGeneralErrorComposable()
    }
}
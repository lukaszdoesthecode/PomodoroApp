package com.example.pomodojo

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object NotificationUtils {

}


//todo: use shared preferences to store the session duration and break duration


class TimerService: Service() {
    private val binder = LocalBinder()
    private val _timeFlow = MutableStateFlow(1500)
    val timeFlow: StateFlow<Int> get() = _timeFlow
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private val _sessionStatusFlow = MutableStateFlow(SessionState.WORK)
    val sessionStatusFlow: StateFlow<SessionState> get() = _sessionStatusFlow
    private var performMainWorkJob: Job? = null
    private lateinit var sharedPref: SharedPreferences

    private var sessionTimeMap = mapOf(
        SessionState.WORK to 1500,
        SessionState.SHORT_BREAK to 300,
        SessionState.LONG_BREAK to 900
    )
    private var currentSessionIndex = 0

    private val sessionSequence = listOf(
        SessionState.WORK,
        SessionState.SHORT_BREAK,
        SessionState.WORK,
        SessionState.SHORT_BREAK,
        SessionState.WORK,
        SessionState.SHORT_BREAK,
        SessionState.WORK,
        SessionState.LONG_BREAK
    )

    // Other service code...

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
        sharedPref = getSharedPreferences("myPrefs", MODE_PRIVATE)
        sessionTimeMap = mapOf(
            SessionState.WORK to sharedPref.getInt("work_duration", 1500),
            SessionState.SHORT_BREAK to sharedPref.getInt("short_break_duration", 300),
            SessionState.LONG_BREAK to sharedPref.getInt("long_break_duration", 900)
        )
    }

    private fun changeSessionState() {


        currentSessionIndex = (currentSessionIndex + 1) % sessionSequence.size
        val nextSession = sessionSequence[currentSessionIndex]
        _sessionStatusFlow.value = nextSession
        updateTime(sessionTimeMap[nextSession] ?: 1500)
        Log.d("Session State", "Starting Session: ${SessionState.getSessionStateString(nextSession)}")

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

    private suspend fun performMainWork() {
        Log.d("Service Status", "Starting Service")

        makeToastAsync("Service has started running in the background")

        while (timeFlow.value > 0) {
            delay(1000)
            updateTime(timeFlow.value - 1)
            var currentSessionString = SessionState.getSessionStateString(sessionSequence[currentSessionIndex])
            updateNotification("Pomodojo", "${currentSessionString}: ${formatTime(timeFlow.value)}")
            Log.d("Status", "Time ${timeFlow.value}")

            if(timeFlow.value == 0){
                changeSessionState()
                makeToastAsync("Session starting: ${SessionState.getSessionStateString(sessionStatusFlow.value)}")
            }
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()

    }

    private fun updateNotification(title: String, text: String) {
        val notification = NotificationsHelper.buildNotification(this, title, text)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initSessionState()

        performMainWorkJob = serviceScope.launch {
            performMainWork()
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
        Log.d("Stopping","Stopping Service")

        return super.stopService(name)
    }

    fun stopForegroundService() {
        performMainWorkJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    fun initSessionState(){
        val restored = restoreSessionStateSharedPrefs()

        if(!restored){
            _timeFlow.value = sessionTimeMap[sessionSequence[0]] ?: 1500
            _sessionStatusFlow.value = SessionState.WORK
        }
    }

    fun restoreSessionStateSharedPrefs(): Boolean{
        val sharedPref = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val storedCurrentState = sharedPref.getInt("current_state_index", -1)
        val storedTime = sharedPref.getInt("remaining_time",  -1)

        if(storedCurrentState != -1 && storedTime != -1){
            _timeFlow.value = storedTime
            currentSessionIndex = storedCurrentState
            _sessionStatusFlow.value = sessionSequence[currentSessionIndex]
            Log.d("Restored", "Restored session state")
            return true
        }
        else{
            return false
        }
    }

    fun storeSessionStateSharedPrefs() {
        val sharedPref = getSharedPreferences("myPrefs", MODE_PRIVATE)
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
        Toast.makeText(
            applicationContext, "Service execution completed",
            Toast.LENGTH_SHORT
        ).show()
        Log.d("Stopped","Service Stopped")
        super.onDestroy()
    }

    companion object {
        private const val TAG = "ExampleForegroundService"
    }

}
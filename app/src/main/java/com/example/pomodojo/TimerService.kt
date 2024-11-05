package com.example.pomodojo

import android.app.Service
import android.content.Intent
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

class TimerService: Service() {
    private val binder = LocalBinder()
    private val _timeFlow = MutableStateFlow(1500)
    val timeFlow: StateFlow<Int> get() = _timeFlow
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private val _sessionStatusFlow = MutableStateFlow(SessionState.WORK)
    val sessionStatusFlow: StateFlow<SessionState> get() = _sessionStatusFlow
    private var performMainWorkJob: Job? = null

    private val sessionTimeMap = mapOf(
        SessionState.WORK to 2,
        SessionState.SHORT_BREAK to 5,
        SessionState.LONG_BREAK to 5
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

    private suspend fun performMainWork(intent: Intent?) {
        val name = intent?.getStringExtra("name")
        Log.d("Service Status", "Starting Service")

        makeToastAsync("Service has started running in the background")

        while (timeFlow.value > 0) {
            delay(1000)
            updateTime(timeFlow.value - 1)
            Log.d("Status", "Time ${timeFlow.value}")

            if(timeFlow.value == 0){
                changeSessionState()
                makeToastAsync("Session starting: ${SessionState.getSessionStateString(sessionStatusFlow.value)}")
            }
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // create the notification channel
        val initialTime = intent?.getIntExtra("time", 10)
        _timeFlow.value = sessionTimeMap[sessionSequence[0]] ?: 1500
        _sessionStatusFlow.value = SessionState.WORK

        NotificationsHelper.createNotificationChannel(this)

        // promote service to foreground service
        ServiceCompat.startForeground(
            this,
            1,
            NotificationsHelper.buildNotification(this),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            } else {
                0
            }
        )

        performMainWorkJob = serviceScope.launch {
            performMainWork(intent)
        }

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


    override fun onDestroy() {
        performMainWorkJob?.cancel()
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
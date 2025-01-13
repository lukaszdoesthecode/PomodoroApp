package com.example.pomodojo.pomodoro

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.example.pomodojo.functionality.pomodoro.service.TimerService
import com.example.pomodojo.functionality.pomodoro.state.SessionState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for the TimerService class.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class TimerServiceTest {

    private lateinit var context: Context
    private lateinit var timerService: TimerService
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        sharedPreferences = context.getSharedPreferences("PomodojoPrefs", Context.MODE_PRIVATE)
        timerService = TimerService()
        timerService.sharedPref = sharedPreferences
    }

    /**
     * Tests that the service is properly bound.
     */
    @Test
    fun onBind_returnsBinder() {
        val intent = Intent(context, TimerService::class.java)
        val binder = timerService.onBind(intent)
        assertNotNull(binder)
        assertTrue(binder is TimerService.LocalBinder)
    }

    /**
     * Tests that the initial session state is set correctly.
     */
    @Test
    fun initSessionState_setsInitialState() {
        timerService.initSessionState()
        assertEquals(SessionState.WORK, timerService.sessionStatusFlow.value)
        assertEquals(1500, timerService.timeFlow.value)
    }

    /**
     * Tests that the updateTime method updates the time correctly.
     */
    @Test
    fun updateTime_updatesTime() {
        timerService.updateTime(1200)
        assertEquals(1200, timerService.timeFlow.value)
    }

    /**
     * Tests that the stopForegroundService method stops the service correctly.
     */
    @Test
    fun stopForegroundService_stopsService() {
        timerService.stopForegroundService()
        assertFalse(timerService.timerRunningFlow.value)
    }
}
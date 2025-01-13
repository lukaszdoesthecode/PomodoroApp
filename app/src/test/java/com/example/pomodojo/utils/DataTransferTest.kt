package com.example.pomodojo.core.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.example.pomodojo.functionality.dashboard.Config
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config as RoboConfig

/**
 * Unit tests for the DataTransfer functions.
 */
@RunWith(RobolectricTestRunner::class)
@RoboConfig(sdk = [28])
class DataTransferTest {

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences

    /**
     * Sets up the test environment before each test.
     */
    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

    /**
     * Tests that the saveConfigToPreferences function saves the configuration correctly.
     */
    @Test
    fun saveConfigToPreferences_savesConfigCorrectly() {
        val shortBreak = 10
        val focusTime = 30
        val longBreak = 15
        val iterations = 3

        saveConfigToPreferences(context, shortBreak, focusTime, longBreak, iterations)

        val savedConfig = getConfigFromPreferences(context)
        assertEquals(shortBreak, savedConfig.shortBreak)
        assertEquals(focusTime, savedConfig.focusTime)
        assertEquals(longBreak, savedConfig.longBreak)
        assertEquals(iterations, savedConfig.iterations)
    }

    /**
     * Tests that the getConfigFromPreferences function retrieves the default configuration when no data is saved.
     */
    @Test
    fun getConfigFromPreferences_retrievesDefaultConfig() {
        val defaultConfig = Config(shortBreak = 5, focusTime = 25, longBreak = 20, iterations = 2)
        val retrievedConfig = getConfigFromPreferences(context)

        assertEquals(defaultConfig.shortBreak, retrievedConfig.shortBreak)
        assertEquals(defaultConfig.focusTime, retrievedConfig.focusTime)
        assertEquals(defaultConfig.longBreak, retrievedConfig.longBreak)
        assertEquals(defaultConfig.iterations, retrievedConfig.iterations)
    }
}
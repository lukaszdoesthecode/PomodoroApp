package com.example.pomodojo.core.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.pomodojo.functionality.dashboard.Config

const val PREFS_NAME = "PomodojoPrefs"
private const val KEY_SHORT_BREAK = "shortBreak"
private const val KEY_FOCUS_TIME = "focusTime"
private const val KEY_LONG_BREAK = "longBreak"
private const val KEY_ITERATIONS = "iterations"

/**
 * Saves the configuration to SharedPreferences.
 */
fun saveConfigToPreferences(context: Context, shortBreak: Int, focusTime: Int, longBreak: Int, iterations: Int) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    sharedPreferences.edit().apply {
        putInt(KEY_SHORT_BREAK, shortBreak)
        putInt(KEY_FOCUS_TIME, focusTime)
        putInt(KEY_LONG_BREAK, longBreak)
        putInt(KEY_ITERATIONS, iterations)
        apply()
    }
}

/**
 * Retrieves the configuration from SharedPreferences.
 */
fun getConfigFromPreferences(context: Context): Config {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return Config(
        shortBreak = sharedPreferences.getInt(KEY_SHORT_BREAK, 5),
        focusTime = sharedPreferences.getInt(KEY_FOCUS_TIME, 25),
        longBreak = sharedPreferences.getInt(KEY_LONG_BREAK, 20),
        iterations = sharedPreferences.getInt(KEY_ITERATIONS, 2)
    )
}

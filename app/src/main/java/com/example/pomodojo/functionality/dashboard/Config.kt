package com.example.pomodojo.functionality.dashboard

import java.io.Serializable

/**
 * Represents the configuration settings for the Pomodoro timer and Breathing Exercises functionality in the application.
 *
 * @property shortBreak The duration (in minutes) of a short break between focus sessions. Default is 5 minutes.
 * @property focusTime The duration (in minutes) of a single focus session. Default is 25 minutes.
 * @property longBreak The duration (in minutes) of a long break after a series of focus sessions. Default is 20 minutes.
 * @property iterations The number of focus sessions before a long break. Default is 2.
 *
 * Implements the [Serializable] interface to allow the configuration to be serialized for data transfer or storage.
 */

data class Config(
    var shortBreak: Int = 5,
    var focusTime: Int = 25,
    var longBreak: Int = 20,
    var iterations: Int = 2
) : Serializable

package com.example.pomodojo.functionality.dashboard

import java.io.Serializable

data class Config(
    var shortBreak: Int = 5,
    var focusTime: Int = 25,
    var longBreak: Int = 20,
    var iterations: Int = 2
) : Serializable

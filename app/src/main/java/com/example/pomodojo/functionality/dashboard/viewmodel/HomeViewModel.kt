package com.example.pomodojo.functionality.dashboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen.
 */
class HomeViewModel : ViewModel() {

    private val _navigateToFaceScan = MutableLiveData<Boolean?>(null)
    val navigateToFaceScan: LiveData<Boolean?> = _navigateToFaceScan

    private val _navigateToConfig = MutableLiveData<Boolean?>(null)
    val navigateToConfig: LiveData<Boolean?> = _navigateToConfig

    private val _navigateToPomodoro = MutableLiveData<Boolean?>(null)
    val navigateToPomodoro: LiveData<Boolean?> = _navigateToPomodoro

    private val _errorMessage = MutableLiveData<Pair<String, String>?>(null)
    val errorMessage: LiveData<Pair<String, String>?> = _errorMessage




    /**
     * Sends configuration data and navigates to the Config screen.
     */
    fun saveConfiguration(shortBreak: String, focusTime: String, longBreak: String, repetitions: String) {
        viewModelScope.launch {
            try {
                val configData = mapOf(
                    "shortBreak" to shortBreak,
                    "focusTime" to focusTime,
                    "longBreak" to longBreak,
                    "repetitions" to repetitions
                )
                //  _navigateToConfig.value = configData
            } catch (e: Exception) {
                _errorMessage.value = Pair("Save Configuration Error", e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Navigates to the FaceScan screen.
     */
    fun navigateToFaceScan() {
        viewModelScope.launch {
            try {
                _navigateToFaceScan.value = true
            } catch (e: Exception) {
                _errorMessage.value = Pair("Navigation Error", e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Navigates to the Pomodoro screen.
     */
    fun navigateToPomodoro() {
        viewModelScope.launch {
            try {
                _navigateToPomodoro.value = true
            } catch (e: Exception) {
                _errorMessage.value = Pair("Navigation Error", e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Resets the navigation state.
     */
    fun resetNavigation() {
        viewModelScope.launch {
            try {
                _navigateToFaceScan.value = null
                _navigateToPomodoro.value = null
            } catch (e: Exception) {
                _errorMessage.value = Pair("Reset Error", e.message ?: "Unknown error")
            }
        }
    }
}
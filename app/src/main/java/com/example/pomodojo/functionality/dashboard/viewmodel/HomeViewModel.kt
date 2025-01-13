package com.example.pomodojo.functionality.dashboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * ViewModel for managing navigation and error states in the Home screen.
 *
 * This ViewModel is responsible for handling navigation to the Data Analysis, FaceScan and Pomodoro screens,
 * as well as managing error messages and resetting navigation states.
 */
class HomeViewModel : ViewModel() {

    private val _navigateToFaceScan = MutableLiveData<Boolean?>(null)
    val navigateToFaceScan: LiveData<Boolean?> = _navigateToFaceScan

    private val _navigateToDataAnalysis = MutableLiveData<Boolean?>(null)
    val navigateToDataAnalysis: LiveData<Boolean?> = _navigateToDataAnalysis

    private val _navigateToPomodoro = MutableLiveData<Boolean?>(null)
    val navigateToPomodoro: LiveData<Boolean?> = _navigateToPomodoro

    private val _errorMessage = MutableLiveData<Pair<String, String>?>(null)
    val errorMessage: LiveData<Pair<String, String>?> = _errorMessage

    /**
     * Navigates to the Face Scan screen.
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
     * Navigates to the Data Analysis screen.
     */

    fun navigateToDataAnalysis() {
        viewModelScope.launch {
            try {
                _navigateToDataAnalysis.value = true
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
     *Reset the Navigation.
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

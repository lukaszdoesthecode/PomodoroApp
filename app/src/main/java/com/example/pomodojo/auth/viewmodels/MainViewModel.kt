package com.example.pomodojo.auth.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _navigateToFaceScan = MutableLiveData<Boolean?>(null)
    val navigateToFaceScan: LiveData<Boolean?> = _navigateToFaceScan

    private val _navigateToSpotify = MutableLiveData<Boolean?>(null)
    val navigateToSpotify: LiveData<Boolean?> = _navigateToSpotify

    private val _navigateToPomodoro = MutableLiveData<Boolean?>(null)
    val navigateToPomodoro: LiveData<Boolean?> = _navigateToPomodoro


    fun navigateToFaceScan() {
        _navigateToFaceScan.value = true
    }

    fun navigateToSpotify() {
        _navigateToSpotify.value = true
    }

    fun navigateToPomodoro() {
        _navigateToPomodoro.value = true
    }

    fun resetNavigation() {
        _navigateToFaceScan.value = null
        _navigateToSpotify.value = null
        _navigateToPomodoro.value = null
    }
}

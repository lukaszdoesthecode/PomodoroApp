package com.example.pomodojo.pomodoro

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.pomodojo.R
import com.example.pomodojo.functionality.pomodoro.service.AudioPlayerService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

/**
 * Unit tests for the AudioPlayerService class.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AudioPlayerServiceTest {

    private lateinit var context: Context
    private lateinit var audioPlayerService: AudioPlayerService

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        audioPlayerService = AudioPlayerService(context)
    }

    /**
     * Tests that the stopAudio method stops the MediaPlayer.
     */
    @Test
    fun stopAudio_stopsMediaPlayer() {
        val resId = R.raw.vo_intro
        audioPlayerService.playAudio(resId)
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        audioPlayerService.stopAudio()
        assertFalse(audioPlayerService.mediaPlayer?.isPlaying == true)
    }

    /**
     * Tests that the isPlaying method returns false when the MediaPlayer is not playing.
     */
    @Test
    fun isPlaying_returnsFalseWhenMediaPlayerIsNotPlaying() {
        assertFalse(audioPlayerService.isPlaying())
    }
}
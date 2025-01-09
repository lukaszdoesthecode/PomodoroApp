package com.example.pomodojo.functionality.pomodoro.service

import android.content.Context
import android.media.MediaPlayer
import com.example.pomodojo.R

/**
 * Service class for handling audio playback.
 *
 * @property context The context used to create the MediaPlayer.
 */
class AudioPlayerService(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var lastResId: Int? = null

    /**
     * Plays the audio resource specified by [resId].
     *
     * @param resId The resource ID of the audio to be played.
     */
    fun playAudio(resId: Int) {
        stopAudio() // Ensure no other audio is playing
        lastResId = resId
        mediaPlayer = MediaPlayer.create(context, resId)
        mediaPlayer?.setOnCompletionListener {
            if (resId == R.raw.vo_intro) {
                playAudio(R.raw.vo_intro_2)
            }
        }
        mediaPlayer?.start()
    }

    /**
     * Stops the currently playing audio, if any.
     */
    fun stopAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /**
     * Restarts the last played audio.
     */
    fun restartAudio() {
        lastResId?.let {
            playAudio(it)
        }
    }

    /**
     * Checks if any audio is currently playing.
     *
     * @return True if audio is playing, false otherwise.
     */
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }
}
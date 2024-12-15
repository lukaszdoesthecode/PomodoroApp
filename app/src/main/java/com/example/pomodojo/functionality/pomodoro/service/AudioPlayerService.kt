package com.example.pomodojo.functionality.pomodoro.service

import android.content.Context
import android.media.MediaPlayer
import com.example.pomodojo.R

class AudioPlayerService(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var lastResId: Int? = null

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

    fun stopAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun restartAudio() {
        lastResId?.let {
            playAudio(it)
        }
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }
}
package com.example.callwall

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService

class Audio (audioManager: AudioManager){
    var audioManager : AudioManager = audioManager
    var volume : Int = 5
    var muted : Boolean ?= null

    fun mute() {
        this.audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0)
        println("muted")
    }

    fun unmute() {
        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0)
    }

    fun getCurVolume() {
        volume = audioManager.getStreamVolume(AudioManager.STREAM_RING)
    }

    fun revertVolume() {
        this.audioManager.adjustStreamVolume(AudioManager.STREAM_RING, volume, 0)
    }

    fun revertMute() {
        println("Mute status $muted")
        if (muted == false) unmute()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun saveMuteStatus() {
        muted = audioManager.isStreamMute(AudioManager.STREAM_RING)
        println("Mute status $muted")
    }
}
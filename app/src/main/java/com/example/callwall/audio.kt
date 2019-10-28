package com.example.callwall


import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi

class Audio (var audioManager: AudioManager){
    var volume : Int = 5
    var muted : Boolean ?= null
    var lastRingerMode: Int = AudioManager.RINGER_MODE_NORMAL

    fun mute() {
        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
        //audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0)
        println("muted")
    }

    fun unmute() {
        audioManager.ringerMode = lastRingerMode
        //audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0)
    }

    fun getCurVolume() {
        volume = audioManager.getStreamVolume(AudioManager.STREAM_RING)
    }

    fun revertVolume() {
        this.audioManager.adjustStreamVolume(AudioManager.STREAM_RING, volume, 0)
    }

    fun revertMute() {
        unmute()
        println("Mute status $muted")
        //if (muted == false) unmute()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun saveMuteStatus() {
        lastRingerMode = audioManager.ringerMode
        muted = audioManager.isStreamMute(AudioManager.STREAM_RING)
        println("Mute status $muted")
    }
}
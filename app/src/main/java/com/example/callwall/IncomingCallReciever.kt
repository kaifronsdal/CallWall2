package com.example.callwall

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.net.URL
import android.telephony.TelephonyManager
import android.widget.TextView
import android.content.SharedPreferences
import android.R.id.edit






class IncomingCallReceiver : BroadcastReceiver() {
    companion object {
        //var callsChecked: TextView? = null
        var sharedpreferences: SharedPreferences? = null
        var checkCall: Boolean = true

        fun toggleCheck() {
            checkCall = !checkCall
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (!checkCall) {
            return
        }

        when (intent.getStringExtra(TelephonyManager.EXTRA_STATE)) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                //MainActivity.incrementCallList()
                println("ringing")//onrecieve
                CheckValidTask.interrupted = false
                println(intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER))
                CheckValidTask().execute(intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER))
            }
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                println("offhook")
            }
            TelephonyManager.EXTRA_STATE_IDLE -> {
                println("idle")//onfinish
                CheckValidTask.interrupted = true
                endPopupProccess()
            }
        }

        println("-----------------------------recieved")

        var number: Int = 16505461126.toInt()

        println("Done")
    }
}
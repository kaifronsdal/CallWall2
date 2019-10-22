package com.example.callwall

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.WindowManager


class IncomingCallReceiver : BroadcastReceiver() {
    companion object {
        var checkCall: Boolean = true

        fun toggleCheck() {
            checkCall = !checkCall
        }
    }

    var number: String? = ""
    private var windowManager: WindowManager? = null
    private var layoutInflater: LayoutInflater? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (!checkCall) {
            return
        }

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        layoutInflater = LayoutInflater.from(context)

        when (intent.getStringExtra(TelephonyManager.EXTRA_STATE)) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                println("ringing")//onrecieve
                CheckValidTask.interrupted = false
                number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                println(number)
                buildPopup(R.layout.popup_searching, windowManager!!, layoutInflater!!)
                CheckValidTask().execute(this)
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
    }

    fun onPreExecute() {

    }

    fun onPostExecute(result: Boolean?) {
        if (result == null) {
            buildPopup(R.layout.popup_undetermined, windowManager!!, layoutInflater!!)
        } else if (!result) {
            buildPopup(R.layout.popup_found, windowManager!!, layoutInflater!!)
        } else {
            buildPopup(R.layout.popup_not_found, windowManager!!, layoutInflater!!)
        }
    }
}
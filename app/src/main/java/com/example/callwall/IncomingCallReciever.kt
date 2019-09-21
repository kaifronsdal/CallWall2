package com.example.callwall

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import java.net.URL



class IncomingCallReceiver: BroadcastReceiver() {
    companion object {
        var thisActivity: Activity? = null
    }
//
//    fun getActivity(): Activity? {
//        if (thisActivity == null) {
//            thisActivity = MainActivity.getInstance()
//        }
//        return thisActivity
//    }

    fun checkBusyValid(number: Number): Boolean {
        //var response = URL("http://10.7.65.105:3000/number/$number").readText()
        //var response = URL("http://dlongo.pythonanywhere.com/?phone_number=+$number").readText()
        var response = URL("http://dlongo.pythonanywhere.com/?phone_number=+16505461126").readText()
        if (response == "not busy") {
            return false
        }
        if (response == "busy") {
            return true
        }
        println("invalid reply $response")
        return true
    }

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(
            thisActivity,
            "Recieve",
            Toast.LENGTH_LONG
        ).show()
        Log.v("asdf", "--------------------------------------------------------------")
        println("Hello World!")
        var number: Int = 16505461126.toInt()
        CheckValidTask.thisActivity = thisActivity
        CheckValidTask().execute(number)
        //println(checkBusyValid(16505461126))
        println("Done")
    }
}
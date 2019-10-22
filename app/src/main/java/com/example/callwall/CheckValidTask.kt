package com.example.callwall

import android.os.AsyncTask
import android.util.Log
import java.net.URL
import android.view.LayoutInflater

class CheckValidTask : AsyncTask<IncomingCallReceiver?, Void, Boolean>() {
    companion object {
        var interrupted: Boolean = false
    }
    private var incomingCallReceiver: IncomingCallReceiver? = null

    override fun doInBackground(vararg params: IncomingCallReceiver?): Boolean? {
        val receiver = params[0]
        incomingCallReceiver = receiver
        val param = receiver?.number
        println("------------------back")
        //var response = URL("http://dlongo.pythonanywhere.com/?phone_number=+1$param").readText()

        if (param == null) {
            //return null
        }
        try {
            val response: String? = URL("http://dlongo.pythonanywhere.com/?phone_number=+1" + param).readText()
            println("http://dlongo.pythonanywhere.com/?phone_number=+1" + param)
            //response = URL("http://dlongo.pythonanywhere.com/?phone_number=+16505461126").readText()
            if (response == "not busy") {
                return false
            }
            if (response == "busy") {
                return true
            }
            println("invalid reply $response")
            return true
        } catch (e: Exception) {
            println(e)
            Log.e("---------", e.toString())
            return null
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        interrupted = false
        incomingCallReceiver?.onPreExecute()
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        if (interrupted) {
            return
        }
        incomingCallReceiver?.onPostExecute(result)
//        if (result == null) {
//            buildPopup(R.layout.popup_undetermined, windowManager!!, layoutInflater!!)
//        } else if (!result) {
//            buildPopup(R.layout.popup_found, windowManager!!, layoutInflater!!)
//        } else {
//            buildPopup(R.layout.popup_not_found, windowManager!!, layoutInflater!!)
//        }
    }
}
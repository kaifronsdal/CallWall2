package com.example.callwall

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.view.*
import java.net.URL
import android.view.WindowManager


class CheckValidTask : AsyncTask<String, Void, Boolean>() {
    companion object {
        var layoutInflater: LayoutInflater? = null
        var windowManager: WindowManager? = null
        var interrupted: Boolean = false
    }

    override fun doInBackground(vararg params: String?): Boolean? {
        var param = params[0]
        println("------------------back")
        //var response = URL("http://dlongo.pythonanywhere.com/?phone_number=+1$param").readText()
        var response: String?
        if (param == null) {
            //return null
        }
        try {
            //response = URL("http://dlongo.pythonanywhere.com/?phone_number=+1" + param).readText()
            response = URL("http://dlongo.pythonanywhere.com/?phone_number=+16505461126").readText()
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
            return null
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        interrupted = false
        buildPopup(R.layout.popup_searching, windowManager!!, layoutInflater!!)
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        if (interrupted) {
            return
        }
        if (result == null) {
            buildPopup(R.layout.popup_undetermined, windowManager!!, layoutInflater!!)
        } else if (!result) {
            buildPopup(R.layout.popup_found, windowManager!!, layoutInflater!!)
        } else {
            buildPopup(R.layout.popup_not_found, windowManager!!, layoutInflater!!)
        }
    }
}
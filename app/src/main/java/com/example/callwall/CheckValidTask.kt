package com.example.callwall

import android.app.Activity
import android.os.AsyncTask
import android.view.*
import java.net.URL
import android.view.WindowManager


class CheckValidTask : AsyncTask<Int, Void, Boolean>() {
    companion object {
        var thisActivity: Activity? = null
        var layoutInflater: LayoutInflater? = null
        var windowManager: WindowManager? = null
    }

    override fun doInBackground(vararg params: Int?): Boolean? {
        var param = params[0]
        //var response = URL("http://dlongo.pythonanywhere.com/?phone_number=+1$param").readText()
        var response: String?
        try {
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
        buildPopup(R.layout.popup_searching, thisActivity!!, windowManager!!, layoutInflater!!)
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)

        if (result != null && !result) {
            buildPopup(R.layout.popup_found, thisActivity!!, windowManager!!, layoutInflater!!)
        } else {
            buildPopup(R.layout.popup_not_found, thisActivity!!, windowManager!!, layoutInflater!!)
        }
    }
}
package com.example.callwall

import android.os.AsyncTask
import android.widget.Toast
import java.net.URL


class CheckValidTask() : AsyncTask<Int, Void, Boolean>() {
    override fun doInBackground(vararg params: Int?): Boolean? {
        var param = params[0]
        //var response = URL("http://dlongo.pythonanywhere.com/?phone_number=+1$param").readText()
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

    override fun onPreExecute() {
        super.onPreExecute()

    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        Toast.makeText(
            IncomingCallReceiver.thisActivity,
            "Done!!!",
            Toast.LENGTH_LONG
        ).show()
        println("onPostExecute")
        println(result)
        println("-------------------------------")
    }
}
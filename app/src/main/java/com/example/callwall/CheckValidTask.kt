package com.example.callwall

import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.os.AsyncTask
import android.os.Build
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import java.net.URL


class CheckValidTask : AsyncTask<Int, Void, Boolean>() {
    companion object {
        var thisActivity: Activity? = null
        var wm: WindowManager? = null
                //= getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var inflater: LayoutInflater? = null
                        //= getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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
        if (result != null && !result) {
            val params = getParams()
            params.x = 50
            params.y = 100


            val myView = inflater?.inflate(R.layout.popup, null)
            myView?.setOnTouchListener { _, _ ->
                Toast.makeText(
                    thisActivity,
                    "touch",
                    Toast.LENGTH_LONG
                ).show()
                wm?.removeView(myView)
                true
            }

            // Add layout to window manager
            wm?.addView(myView, params)
        }
    }

    private fun getParams(): WindowManager.LayoutParams {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            )
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            )
        }
    }
}
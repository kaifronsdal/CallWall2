package com.example.callwall

import android.app.Activity
import android.graphics.PixelFormat
import android.os.AsyncTask
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import java.net.URL
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import android.view.WindowManager
import android.view.MotionEvent


class CheckValidTask : AsyncTask<Int, Void, Boolean>() {
    companion object {
        var thisActivity: Activity? = null
        var wm: WindowManager? = null
        //= getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var inflater: LayoutInflater? = null
        var windowManager: WindowManager? = null
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
        val params = getParams()
        val displayMetrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        params.y = displayMetrics.heightPixels / 2 - params.height
        params.width = displayMetrics.widthPixels


        val myView = inflater?.inflate(R.layout.popup_searching, null)


        //var movingView = findViewById<View>(R.id.popup_found_layout)
//        myView?.getViewTreeObserver()
//            ?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//                @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
//                override fun onGlobalLayout() {
//                    var xAnimation = SpringAnimation(
//                        myView, SpringAnimation.X
//                    )
//                    myView?.getViewTreeObserver()?.removeOnGlobalLayoutListener(this)
//                }
//            })
//        val animX = myView.also { view ->
//            SpringAnimation(view, SpringAnimation.TRANSLATION_X).apply {
//                //getSpring()
//                spring.dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
//                spring.stiffness = SpringForce.STIFFNESS_MEDIUM
//            }
//        }
        val animX = SpringAnimation(myView, SpringAnimation.TRANSLATION_X)
        animX.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY)
        animX.getSpring().setStiffness(SpringForce.STIFFNESS_MEDIUM)

        var dX = 0f
        var dY = 0f

        myView?.setOnTouchListener { view, event: MotionEvent ->
            //            println(event.y)
//            if (event.y > 0) {
//                wm?.removeView(myView)
//            }
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY

                    animX.cancel()
                }
                MotionEvent.ACTION_MOVE -> myView.animate()
                    .x(event.rawX + dX)
                    .y(event.rawY + dY)
                    .setDuration(0)
                    .start()
                MotionEvent.ACTION_UP -> {
                    animX.start()
                }
            }
            true
        }

        // Add layout to window manager
        wm?.addView(myView, params)
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        println("onPostExecute")
        println(result)
        println("-------------------------------")
        val params = getParams()
        val displayMetrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        params.y = displayMetrics.heightPixels / 2 - params.height
        params.width = displayMetrics.widthPixels

        val myView: View? = if (result != null && !result) {
            inflater?.inflate(R.layout.popup_found, null)
        } else {
            inflater?.inflate(R.layout.popup_not_found, null)
        }
        myView?.setOnTouchListener { _, event: MotionEvent ->
            if (event.y > 0) {
                wm?.removeView(myView)
            }
            true
        }

        // Add layout to window manager
        wm?.addView(myView, params)

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
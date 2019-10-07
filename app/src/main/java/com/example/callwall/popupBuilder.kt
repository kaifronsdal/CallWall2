package com.example.callwall

import android.app.Activity
import android.graphics.PixelFormat
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import java.util.*
import kotlin.math.sign


public fun buildPopup(
    layout: Int,
    activity: Activity,
    windowManager: WindowManager,
    layoutInflater: LayoutInflater
): View {
    val params = getParams()
    val displayMetrics = DisplayMetrics()

    windowManager.defaultDisplay?.getMetrics(displayMetrics)
    params.y = displayMetrics.heightPixels / 2 - params.height
    params.width = displayMetrics.widthPixels


    val popupView = layoutInflater.inflate(layout, null)

    windowManager.addView(popupView, params)

    val animSpring = SpringAnimation(popupView, SpringAnimation.TRANSLATION_X)
    animSpring.spring = SpringForce(0f)
        .setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY)
        .setStiffness(SpringForce.STIFFNESS_MEDIUM)
    var animSwipe: ViewPropertyAnimator? = null

    val startX = popupView.x
    var dX = 0f

    popupView?.setOnTouchListener { view: View, event: MotionEvent ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                dX = view.x - event.rawX
                animSpring.cancel()
                if (animSwipe != null) {
                    animSwipe?.cancel()
                }
            }
            MotionEvent.ACTION_MOVE -> popupView.animate()
                .x(event.rawX + dX)
                .setDuration(0)
                .start()
            MotionEvent.ACTION_UP -> {
                if (Math.abs(startX - view.x) > displayMetrics.widthPixels / 3) {
                    val duration: Long = 400

                    animSwipe = popupView.animate()
                        .x(-sign(startX - view.x) * displayMetrics.widthPixels * 2)
                        .setDuration(duration)
                    animSwipe?.start()

                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            activity.runOnUiThread {
                                popupView.visibility = View.GONE
                            }
                        }
                    }, duration - 50)

                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            windowManager.removeView(popupView)
                        }
                    }, duration)
                } else {
                    animSpring.start()
                }
            }
        }
        true
    }
    return popupView
}

private fun getParams(): WindowManager.LayoutParams {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )
    } else {
        WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )
    }
}

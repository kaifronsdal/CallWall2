package com.example.callwall

import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.util.DisplayMetrics
import android.view.*
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import java.util.*
import kotlin.math.sign
import android.view.WindowManager


var currentPopup: View? = null
var windowManagerStatic: WindowManager? = null

public fun endPopupProccess() {
    if (currentPopup != null) {
        currentPopup?.visibility = View.GONE
        if (windowManagerStatic != null) {
            windowManagerStatic?.removeView(currentPopup)
        }
    }
    currentPopup = null
}

public fun buildPopup(
    layout: Int,
    windowManager: WindowManager,
    layoutInflater: LayoutInflater
): View {
    windowManagerStatic = windowManager
    if (currentPopup != null) {
        windowManager.removeView(currentPopup)
        currentPopup = null
    }

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
                            Handler(popupView.context.mainLooper).post {
                                if (currentPopup != null) {
                                    popupView.visibility = View.GONE
                                }
                            }
                        }
                    }, duration - 50)

                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            if (currentPopup != null) {
                                windowManager.removeView(popupView)
                                currentPopup = null
                            }
                        }
                    }, duration)
                } else {
                    animSpring.start()
                }
            }
        }
        true
    }
    currentPopup = popupView
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
                    or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        )
    } else {
        WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        )
    }
}

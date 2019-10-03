package com.example.callwall

import android.Manifest
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.provider.Settings.canDrawOverlays
import android.os.Build
import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.view.WindowManager
import android.content.pm.PackageManager
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import java.lang.Math.abs
import java.util.*
import kotlin.math.sign


const val OVERLAY_PERMISSION_REQ_CODE: Int = 200
const val REGULAR_PERMISSION_REQ_CODE: Int = 100

class MainActivity : Activity() {
    var checkingForPermissions: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        checkPermissionOverlay()
        IncomingCallReceiver.thisActivity = this
        CheckValidTask.windowManager = windowManager

        findViewById<Button>(R.id.toggle).setOnClickListener {
            IncomingCallReceiver.toggleCheck()
            println("toggle: " + IncomingCallReceiver.checkCall)
        }

        if (checkPermissionOverlay()) {
            onRecieveOverlay()
        }
    }

    private fun onRecieveOverlay() {
        requestNeededPermissions(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.INTERNET
        )

        test()
    }

    private fun requestNeededPermissions(vararg permissions: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        val neededPermissions = mutableListOf<String>()

        for (s: String in permissions) {
            if (checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(s)
            }
        }

        requestPermissions(neededPermissions.toTypedArray(), REGULAR_PERMISSION_REQ_CODE)
    }

    private fun checkPermissionOverlay(): Boolean {
        if (checkingForPermissions) {
            return false
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false
        }
        if (!canDrawOverlays(this)) {
            checkingForPermissions = true
            val intentSettings = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + packageName)
            )
            startActivityForResult(intentSettings, OVERLAY_PERMISSION_REQ_CODE)
            return false
        } else {
            checkingForPermissions = false
            return true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (canDrawOverlays(this)) {
                checkingForPermissions = false
                onRecieveOverlay()
            } else {
                checkPermissionOverlay()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        run()
    }

    private fun run() {
    }

    private fun test() {
        val params = getParams()
        val displayMetrics = DisplayMetrics()
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        CheckValidTask.wm = wm
        CheckValidTask.inflater = inflater

        wm.defaultDisplay?.getMetrics(displayMetrics)
        params.y = displayMetrics.heightPixels / 2 - params.height
        params.width = displayMetrics.widthPixels


        val myView = inflater.inflate(R.layout.popup_searching, null)

        wm.addView(myView, params)

        val animX = SpringAnimation(myView, SpringAnimation.TRANSLATION_X)
        animX.spring = SpringForce(0f)
            .setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY)
            .setStiffness(SpringForce.STIFFNESS_MEDIUM)
        val startX = myView.x
        var dX = 0f
        println("-------------------")
        myView?.setOnTouchListener { view: View, event: MotionEvent ->

            println("-------------------")
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    animX.cancel()
                }
                MotionEvent.ACTION_MOVE -> myView.animate()
                    .x(event.rawX + dX)
                    .setDuration(0)
                    .start()
                MotionEvent.ACTION_UP -> {
                    //if (view.x)
                    Log.e("TEST", "offest: " + (startX - view.x) + "     speed: " + dX)
                    Log.e("HI", "-------------"+displayMetrics.widthPixels)
                    println("offest: " + (startX - view.x) + "     speed: " + dX)
                    if (abs(startX - view.x) > displayMetrics.widthPixels/3) {
                        val duration: Long = 400

                        myView.animate()
                            .x(-sign(startX-view.x)*displayMetrics.widthPixels*2)
                            .setDuration(duration)
                            .start()
                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                myView.alpha = 0f
                                wm.removeView(myView)
                            }
                        }, duration - 50)
                    } else {
                        animX.start()
                    }
                }
            }
            true
        }
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
}

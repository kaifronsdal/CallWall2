package com.example.callwall

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.view.LayoutInflater
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import android.provider.Settings.canDrawOverlays
import android.os.Build
import android.annotation.TargetApi
import android.graphics.PixelFormat
import android.util.Log
import android.view.WindowManager
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

const val OVERLAY_PERMISSION_REQ_CODE: Int = 200

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissionOverlay()
        IncomingCallReceiver.thisActivity = this
        Log.v("aaaa", "--------------------------------------------------------------")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                val permissions = arrayOf<String>(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.INTERNET
                )
                requestPermissions(permissions, 100)
            }

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.INTERNET
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.INTERNET),
                    100
                )
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun checkPermissionOverlay() {
        if (!canDrawOverlays(this)) {
            Toast.makeText(
                this@MainActivity,
                "Can Use Overylay?",
                Toast.LENGTH_LONG
            ).show()
            val intentSettings = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivityForResult(intentSettings, OVERLAY_PERMISSION_REQ_CODE)
        } else {
            run()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Toast.makeText(
            this@MainActivity,
            "finished",
            Toast.LENGTH_LONG
        ).show()

        run()
    }

    private fun run() {
        Toast.makeText(
            this@MainActivity,
            "run",
            Toast.LENGTH_LONG
        ).show()

        val params = getParams()
        params.x = 50
        params.y = 100

        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        CheckValidTask.wm = wm
        CheckValidTask.inflater = inflater
        val myView = inflater.inflate(R.layout.popup, null)
        myView.setOnTouchListener { _, _ ->
            Toast.makeText(
                this@MainActivity,
                "touch",
                Toast.LENGTH_LONG
            ).show()
            wm.removeView(myView)
            true
        }

        // Add layout to window manager
        wm.addView(myView, params)
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

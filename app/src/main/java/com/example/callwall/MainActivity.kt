package com.example.callwall

import android.Manifest
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.provider.Settings.canDrawOverlays
import android.os.Build
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Button
import android.widget.TextView
import androidx.core.content.PermissionChecker
import java.nio.charset.Charset


const val OVERLAY_PERMISSION_REQ_CODE: Int = 200
const val REGULAR_PERMISSION_REQ_CODE: Int = 100

class MainActivity : Activity() {
    var checkingForPermissions: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        checkPermissionOverlay()
        //IncomingCallReceiver.thisActivity = this
        //IncomingCallReceiver.callsChecked = findViewById<TextView>(R.id.verifiedCount)
        CheckValidTask.windowManager = windowManager
        CheckValidTask.layoutInflater = layoutInflater

        findViewById<Button>(R.id.toggle).setOnClickListener {
            IncomingCallReceiver.toggleCheck()
            println("toggle: " + IncomingCallReceiver.checkCall)
        }

        if (checkPermissionOverlay()) {
            onRecieveOverlay()
        }
    }

    public fun incrementCallList() {
//        val filename = "TotalCalls"
//        val fileContents = 0
//        openFileOutput(filename, Context.MODE_PRIVATE).use {
//            it.write(fileContents)
//        }
//        var content: Int?
//
//        openFileInput(filename).use {
//            content = it.readBytes().toString(Charset.defaultCharset()).toIntOrNull()
//            if (content == null) {
//                content = 0
//            }
//            findViewById<TextView>(R.id.verifiedCount).text = content.toString()
//        }




//        val sharedpreferences = getSharedPreferences("CallWall", Context.MODE_PRIVATE)
//        IncomingCallReceiver.sharedpreferences = sharedpreferences
//        val editor = sharedpreferences.edit()
//        if (!sharedpreferences.contains("totalChecked")) {
//            editor.putInt("totalChecked", 1)
//            editor.apply()
//        } else {
//            editor.putInt("totalChecked", sharedpreferences.getInt("totalChecked", -1) + 1)
//            editor.apply()
//        }
//
//        println("---------------------" + sharedpreferences.getInt("totalChecked", -1))
        //findViewById<TextView>(R.id.verifiedCount).text = sharedpreferences.getInt("totalChecked", -1).toString()
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
        if (requestCode == REGULAR_PERMISSION_REQ_CODE) {
            var notGranted = false
            grantResults.forEach {
                if (it == PermissionChecker.PERMISSION_GRANTED) {
                    notGranted = true
                }
            }
            if (notGranted) {
                requestNeededPermissions(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.INTERNET
                )
            } else {
                run()
            }
        }
    }

    private fun run() {
    }

    private fun test() {
        //buildPopup(R.layout.popup_searching, this, windowManager, layoutInflater)
    }
}

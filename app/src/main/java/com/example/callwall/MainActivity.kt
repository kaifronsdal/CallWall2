package com.example.callwall

import android.Manifest
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.provider.Settings.canDrawOverlays
import android.os.Build
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Button
import androidx.core.content.PermissionChecker
import java.util.*


const val NOTIFICATION_PERMISSION_REQ_CODE: Int = 0x1
const val OVERLAY_PERMISSION_REQ_CODE: Int = 0x2
const val REGULAR_PERMISSION_REQ_CODE: Int = 0x4

class MainActivity : Activity() {
    var checkingForPermissions: Boolean = false
    var notificationManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//        requestBasicPermissions()
//        Timer().schedule(object : TimerTask() {
//            override fun run() {
//                checkPermissionOverlay()
//            }
//        }, 1000)

        findViewById<Button>(R.id.toggle).setOnClickListener {
            IncomingCallReceiver.toggleCheck()
            println("toggle: " + IncomingCallReceiver.checkCall)
        }

        var done = false
        Timer().schedule(object : TimerTask() {
            override fun run() {
                println("----------ucuebciecbeu")
                while (!done) {
                    println(done)
                    done = true
                    if (checkPermissionNotification()) {
                        done = false
                    } else if (checkPermissionOverlay()) {
                        done = false
                    } else if(requestBasicPermissions()) {
                        done = false
                    }
                    Thread.sleep(2000)
                }
            }
        }, 1000)
    }

    private fun requestBasicPermissions(): Boolean {
        return requestNeededPermissions(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.INTERNET
        )
    }

    private fun requestNeededPermissions(vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        val neededPermissions = mutableListOf<String>()

        var alreadyHave = true
        for (s: String in permissions) {
            if (checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(s)
                alreadyHave = false
            }
        }

        requestPermissions(neededPermissions.toTypedArray(), REGULAR_PERMISSION_REQ_CODE)
        print (alreadyHave)
        return alreadyHave
    }

    private fun checkPermissionOverlay(): Boolean {
        if (checkingForPermissions) {
            //return false
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false
        }
        if (!canDrawOverlays(this)) {
            checkingForPermissions = true
            val intentSettings = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intentSettings, OVERLAY_PERMISSION_REQ_CODE)
            return false
        } else {
            checkingForPermissions = false
            return true
        }
    }

    private fun checkPermissionNotification(): Boolean {
        if (checkingForPermissions) {
            //return false
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false
        }
        if (!notificationManager?.isNotificationPolicyAccessGranted!!) {
            checkingForPermissions = true
            val intentSettings =
                Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivityForResult(intentSettings, NOTIFICATION_PERMISSION_REQ_CODE)
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
        if (requestCode == NOTIFICATION_PERMISSION_REQ_CODE) {
            if (notificationManager?.isNotificationPolicyAccessGranted!!) {
                checkingForPermissions = false
                checkPermissionOverlay()
                requestBasicPermissions()
            } else {
                checkPermissionNotification()
            }
        } else if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (canDrawOverlays(this)) {
                println("---------aksjdhflkajsdh")
                checkingForPermissions = false
                checkPermissionNotification()
                requestBasicPermissions()
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

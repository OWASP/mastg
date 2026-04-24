package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast

// SUMMARY: This sample demonstrates an attacker app that uses the SYSTEM_ALERT_WINDOW permission
// to draw a visible overlay over other apps. It is used to demonstrate how overlay attacks work
// against apps that lack overlay protections (see MASTG-DEMO-0x01).

class MastgTest(private val context: Context) {

    val shouldRunInMainThread = true

    companion object {
        private var overlayView: View? = null
    }

    fun mastgTest(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Toast.makeText(
                context,
                "Grant overlay permission, then press Start again",
                Toast.LENGTH_LONG
            ).show()
            return "Overlay permission required"
        }

        return if (overlayView == null) {
            showOverlay()
            "Overlay shown"
        } else {
            hideOverlay()
            "Overlay hidden"
        }
    }

    private fun showOverlay() {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val overlay = View(context).apply {
            setBackgroundColor(Color.argb(140, 255, 0, 0))
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            800,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        windowManager.addView(overlay, params)
        overlayView = overlay

        Toast.makeText(context, "Overlay shown", Toast.LENGTH_SHORT).show()
    }

    private fun hideOverlay() {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
        Toast.makeText(context, "Overlay hidden", Toast.LENGTH_SHORT).show()
    }
}
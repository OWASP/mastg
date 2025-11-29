package org.owasp.mastestapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.WindowManager.LayoutParams

class MastgTest (private val context: Context){

    var shouldRunInMainThread: Boolean = true

    fun mastgTest(): String {
        if (context is Activity) {
            context.window.addFlags(LayoutParams.FLAG_SECURE)

            // Activity window
            context.window.setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE)
            context.window.setFlags(0, LayoutParams.FLAG_SECURE)

            // Standard dialog
            val dialog = AlertDialog.Builder(context)
                .setTitle("Secure dialog")
                .setMessage("FLAG_SECURE is applied to this dialog.")
                .setPositiveButton("OK", null)
                .create()
            dialog.show()
            dialog.window?.setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE)
            dialog.window?.clearFlags(LayoutParams.FLAG_SECURE)


            return "SUCCESS!!\n\nFLAG_SECURE has been set for the Activity window"
        } else {
            return "ERROR: Context is not an Activity"
        }

    }
}

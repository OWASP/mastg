package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.os.Build

class MastgTest (private val context: Context){

    var shouldRunInMainThread: Boolean = true

    fun mastgTest(): String {
        if (context is Activity) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                context.setRecentsScreenshotEnabled(true)
            }
            else {
                return "ERROR: The setRecentsScreenshotEnabled() method is not available on Android versions below 34."
            }

            return "SUCCESS!!\n\nThe setRecentsScreenshotEnabled() method has been set to true."
        } else {
            return "ERROR: Context is not an Activity"
        }

    }
}

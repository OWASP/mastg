package org.owasp.mastestapp

import android.content.Context
import android.content.Intent

class MastgTest (private val context: Context){

    fun mastgTest(): String {
        val r = DemoResults("0x03")

        // SUMMARY The static issue lives in AndroidManifest.xml: InternalSettingsActivity is declared
        // with android:exported="true" and an <intent-filter>, so any installed app can launch
        // it. At runtime we launch it the proper way — explicitly — to make the activity
        // reachable for manual inspection during the demo.
        try {
            val explicitIntent = Intent(context, InternalSettingsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(explicitIntent)
            r.add(Status.FAIL, "[MASTG-TEST-0x03] InternalSettingsActivity is exported via an <intent-filter>; any app on the device can launch it.")
        } catch (e: Exception) {
            r.add(Status.ERROR, e.toString())
        }
        return r.toJson()
    }

}

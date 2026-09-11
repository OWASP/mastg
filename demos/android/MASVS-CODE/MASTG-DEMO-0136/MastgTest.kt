package org.owasp.mastestapp

import android.content.Context
import android.content.Intent

// SUMMARY: This sample demonstrates the insecure use of implicit intents for internal communication.
class MastgTest (private val context: Context){

    fun mastgTest(): String {
        val r = DemoResults("0x01")

        // FAIL: [MASTG-TEST-0372] The app uses an implicit intent to start an internal activity.
        val implicitIntent = Intent().apply {
            action = "org.owasp.mastestapp.INTERNAL_ACTION"
            putExtra("user_id", "12345")
            putExtra("session_token", "abcde-fghij-12345")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(implicitIntent)
            r.add(Status.FAIL, "Launched internal activity via implicit intent")
        } catch (e: Exception) {
            r.add(Status.ERROR, e.toString())
        }

        /*
        // PASS: [MASTG-TEST-0372] The app uses an explicit intent for internal communication.
        val explicitIntent = Intent(context, InternalActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(explicitIntent)
            r.add(Status.PASS, "Launched internal activity via explicit intent")
        } catch (e: Exception) {
            r.add(Status.ERROR, e.toString())
        }
        */

        return r.toJson()
    }
}

class InternalActivity : android.app.Activity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = android.widget.TextView(this)
        textView.text = "Internal Activity"
        textView.textSize = 24f
        textView.gravity = android.view.Gravity.CENTER
        setContentView(textView)
    }
}

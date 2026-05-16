package org.owasp.mastestapp

import android.content.Context
import android.content.Intent

class MastgTest (private val context: Context){

    fun mastgTest(): String {
        val r = DemoResults("0x01")

        // FAIL: [MASTG-TEST-0x01] The app sends an implicit intent to one of its own components without restricting the target component.
        val vulnerableIntent = Intent().apply {
            action = "org.owasp.mastestapp.PROCESS_SENSITIVE_DATA"
            putExtra("sensitive_token", "auth_token_12345")
            putExtra("user_credentials", "admin:password123")
            putExtra("api_key", "sk-1234567890abcdef")
        }

        try {
            context.startActivity(vulnerableIntent)
            r.add(Status.FAIL, "Hijackable implicit intent launched")
        } catch (e: Exception) {
            r.add(Status.ERROR, e.toString())
        }

        return r.toJson()
    }
}

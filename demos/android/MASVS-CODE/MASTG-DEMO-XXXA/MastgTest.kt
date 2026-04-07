package org.owasp.mastestapp

import android.util.Log
import android.content.Context
import android.content.Intent

class MastgTest (private val context: Context){

    fun mastgTest(): String {
        val r = DemoResults("XXXA")

        // FAIL: [MASTG-TEST-XXXA] The app insecurely sends an implicit intent carrying sensitive without restricting the target component.
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

        
        val secureIntent = Intent().apply {
            action = "org.owasp.mastestapp.PROCESS_SENSITIVE_DATA"
            setPackage("org.owasp.mastestapp")
            putExtra("sensitive_token", "auth_token_12345")
            putExtra("user_credentials", "admin:password123")
            putExtra("api_key", "sk-1234567890abcdef")
        }

        
        try {
            context.startActivity(secureIntent)
            r.add(Status.PASS, "Implicit intent only for the current app launched")
        } catch (e: Exception) {
            r.add(Status.ERROR, e.toString())
        }

        return r.toJson()
    }
}

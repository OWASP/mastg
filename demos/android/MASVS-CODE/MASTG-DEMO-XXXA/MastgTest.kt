package org.owasp.mastestapp

import android.util.Log
import android.content.Context
import android.content.Intent

class MastgTest (private val context: Context){

    fun mastgTest(): String {
        val r = DemoResults("XXXA")

        // Vulnerable: Using implicit intent with sensitive data
        val vulnerableIntent = Intent().apply {
            action = "org.owasp.mastestapp.PROCESS_SENSITIVE_DATA"
            putExtra("sensitive_token", "auth_token_12345")
            putExtra("user_credentials", "admin:password123")
            putExtra("api_key", "sk-1234567890abcdef")
        }

        // Launch implicit intent - any app can intercept this
        try {
            context.startActivity(vulnerableIntent)
            r.add(Status.FAIL, "Hijackable implicit intent launched")
        } catch (e: Exception) {
            r.add(Status.ERROR, e.toString())
        }

        // Secure: Using implicit intent with sensitive data only accessible by the known target application
        val secureIntent = Intent().apply {
            action = "org.owasp.mastestapp.PROCESS_SENSITIVE_DATA"
            setPackage("org.owasp.mastestapp")
            putExtra("sensitive_token", "auth_token_12345")
            putExtra("user_credentials", "admin:password123")
            putExtra("api_key", "sk-1234567890abcdef")
        }

        // Launch implicit intent - any app can intercept this
        try {
            context.startActivity(secureIntent)
            r.add(Status.PASS, "Implicit intent only for the current app launched")
        } catch (e: Exception) {
            r.add(Status.ERROR, e.toString())
        }

        return r.toJson()
    }
}

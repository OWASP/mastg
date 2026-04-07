package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class MastgTest(private val context: Context) {

    val shouldRunInMainThread = true

    companion object {
        const val REQUEST_FILE = 1001
    }


    fun writeSensitiveData() {
        val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("auth_token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoiYWRtaW4ifQ.secret")
            .putString("refresh_token", "rt_8f14e45f-ceea-367f-a27f-abc123def456")
            .putString("user_email", "admin@example.com")
            .putString("api_key", "sk-live-1234567890abcdef")
            .putString("session_id", "sess_a1b2c3d4e5f6")
            .putString("credit_card_last4", "4242")
            .apply()

        Log.d("MASTG-DEMO", "Dummy sensitive data written to SharedPreferences")
    }

    fun mastgTest(): String {
        val r = DemoResults("0060")


        val intent = Intent().apply {
            action = "org.owasp.mastestapp.REQUEST_FILE"
        }

        try {
            (context as Activity).startActivityForResult(intent, REQUEST_FILE)
            r.add(Status.FAIL, "Implicit intent launched with action REQUEST_FILE — any app can intercept")
        } catch (e: Exception) {
            r.add(Status.ERROR, e.toString())
        }

        return r.toJson()
    }

    fun handleResult(activity: Activity, uri: Uri): String {
        val r = DemoResults("0060")
        try {
            val file = File(activity.externalCacheDir, "tmp")
            file.createNewFile()
            activity.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            r.add(Status.FAIL, "File copied to world-readable location: ${file.absolutePath}")
            Log.d("MASTG-DEMO", "Copied URI content to: ${file.absolutePath}")

            // Log what was copied
            val content = file.readText()
            Log.d("MASTG-DEMO", "Stolen file content:\n$content")
        } catch (e: Exception) {
            r.add(Status.ERROR, "Failed to copy: ${e.message}")
        }
        return r.toJson()
    }
}

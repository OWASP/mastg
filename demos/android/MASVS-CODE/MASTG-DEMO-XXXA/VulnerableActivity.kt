package org.owasp.mastestapp

import android.app.Activity
import android.os.Bundle
import android.util.Log

class VulnerableActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        
        intent?.extras?.keySet()?.forEach { key ->
            val value = intent.getStringExtra(key)
            Log.w("VULNERABLE-APP", "$key = $value")
        }

        Log.d("VULNERABLE-APP", "VulnerableActivity processed data")

        this.finish()
    }
}

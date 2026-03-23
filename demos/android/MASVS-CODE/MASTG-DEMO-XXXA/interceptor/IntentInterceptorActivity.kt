package org.owasp.masattackerapp

import android.app.Activity
import android.os.Bundle
import android.util.Log

class IntentInterceptorActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = StringBuilder("Intercepted Data:\n")

        intent?.extras?.keySet()?.forEach { key ->
            val value = intent.getStringExtra(key)
            data.append("$key: $value\n")
            Log.w("INTERCEPTOR", "$key = $value")
        }

        this.finish()
    }
}

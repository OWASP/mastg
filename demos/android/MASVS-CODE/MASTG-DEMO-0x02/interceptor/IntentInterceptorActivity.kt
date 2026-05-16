package org.owasp.masattackerapp

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView

class IntentInterceptorActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = StringBuilder("Intercepted Data:\n")
        intent?.extras?.keySet()?.forEach { key ->
            val value = intent.getStringExtra(key)
            data.append("$key: $value\n")
        }

        val textView = TextView(this).apply {
            text = data.toString()
            textSize = 16f
            gravity = Gravity.START
            setPadding(48, 48, 48, 48)
        }
        setContentView(textView)
    }
}

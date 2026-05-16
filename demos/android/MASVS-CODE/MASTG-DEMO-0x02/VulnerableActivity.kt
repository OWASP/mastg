package org.owasp.mastestapp

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView

class VulnerableActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val received = StringBuilder("VulnerableActivity received:\n")
        intent?.extras?.keySet()?.forEach { key ->
            val value = intent.getStringExtra(key)
            received.append("$key = $value\n")
        }

        val textView = TextView(this).apply {
            text = received.toString()
            textSize = 16f
            gravity = Gravity.START
            setPadding(48, 48, 48, 48)
        }
        setContentView(textView)
    }
}

package org.owasp.mastestapp

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView

class InternalSettingsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this).apply {
            text = "Internal settings screen"
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
        }
        setContentView(textView)
    }
}

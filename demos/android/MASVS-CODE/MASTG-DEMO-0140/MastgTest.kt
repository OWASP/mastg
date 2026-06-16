package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView

// SUMMARY: This sample demonstrates the risk of processing unsanitized data from implicit intent results.
class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        val r = DemoResults("0x05")
        r.add(Status.FAIL, "This app should be invoked by an implicit intents from MASTG-DEMO-0136")
        return r.toJson()
    }
}

class AttackerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent.action
        val textToShow = if (action == android.content.Intent.ACTION_MAIN) {
            "Hello in the attacker Demo app"
        } else {
            val extras = intent.extras
            val args = extras?.keySet()?.joinToString { key -> "$key=${extras.get(key)}" } ?: "None"
            "Intercepted intent! \nArguments: $args"
        }

        val textView = TextView(this).apply {
            text = textToShow
            textSize = 20f
            gravity = Gravity.CENTER
            setTextColor(android.graphics.Color.BLACK)
        }
        setContentView(textView)
    }
}

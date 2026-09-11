package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TextView

// SUMMARY: This sample demonstrates an attacker app that handles an internal implicit intent.
class MastgTest(private val context: Context) {

    companion object {
        const val TAG = "INTENT_ATTACK"
    }

    fun mastgTest(): String {
        return "Install this app with MASTG-DEMO-0136 and select it when Android resolves INTERNAL_ACTION."
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
            val args = extras?.keySet()?.sorted()?.joinToString { key -> "$key=${extras.get(key)}" } ?: "None"
            Log.e(MastgTest.TAG, "Intercepted action=$action extras=$args")
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

package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

// SUMMARY: This sample demonstrates a visually masked password whose plaintext
// is exposed through accessibility metadata.

class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        context.startActivity(
            Intent(context, SensitiveInputActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
        return "Enter MASTG-SECRET-1234 in the password field."
    }
}

class SensitiveInputActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        container.addView(TextView(this).apply {
            text = "Password"
            textSize = 20f
            setTypeface(typeface, Typeface.BOLD)
        })

        val passwordField = EditText(this).apply {
            hint = "Enter password"
            inputType =
                InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_PASSWORD

            // FAIL: [MASTG-TEST-0x01] The password is visually masked,
            // but its plaintext is copied into accessibility metadata.
            accessibilityDelegate = object : View.AccessibilityDelegate() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View,
                    info: AccessibilityNodeInfo
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info.contentDescription = text?.toString().orEmpty()
                }
            }
        }

        container.addView(passwordField)
        setContentView(container)
    }
}

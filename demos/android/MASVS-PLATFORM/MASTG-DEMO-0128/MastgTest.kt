// SUMMARY: This sample shows a two-activity login flow where PinEntryActivity
// enforces a PIN (4321) before launching SecretActivity. Because SecretActivity
// is exported with no android:permission, an attacker can start it directly with
// adb and bypass the PIN check entirely.

package org.owasp.mastestapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        // Launch PinEntryActivity - the intended path that enforces the PIN gate.
        // SecretActivity is also exported, so an attacker can bypass PinEntryActivity
        // entirely by starting it directly.
        context.startActivity(
            Intent(context, PinEntryActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
        return "Launching PIN entry screen..."
    }

    // Legitimate entry point: enforces PIN 4321 before launching SecretActivity.
    // Not exported - only reachable through the app's own flow.
    class PinEntryActivity : Activity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            actionBar?.hide()

            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(64, 120, 64, 64)
            }

            val title = TextView(this).apply {
                text = "MASTestApp - Secure Area"
                textSize = 22f
            }

            val subtitle = TextView(this).apply {
                text = "Enter your PIN to access the secret screen."
                textSize = 16f
                setPadding(0, 24, 0, 48)
            }

            val pinInput = EditText(this).apply {
                hint = "PIN"
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            }

            val button = Button(this).apply {
                text = "Start"
                setOnClickListener {
                    if (pinInput.text.toString() == "4321") {
                        startActivity(Intent(this@PinEntryActivity, SecretActivity::class.java))
                    } else {
                        AlertDialog.Builder(this@PinEntryActivity)
                            .setTitle("Wrong PIN")
                            .setMessage("Incorrect PIN. Try again.")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
            }

            layout.addView(title)
            layout.addView(subtitle)
            layout.addView(pinInput)
            layout.addView(button)
            setContentView(layout)
        }
    }

    // FAIL: [MASTG-TEST-0364] SecretActivity is exported and does not need any special permissions.
    // External callers, like other apps including adb, can start it directly, bypassing PinEntryActivity.
    class SecretActivity : Activity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            actionBar?.hide()

            val secret = buildString {
                append("SECRET SCREEN (reached without authentication)\n\n")
                append("Account: 1234-5678-9012-3456\n")
                append("Balance: 10,000\n")
                append("Recovery PIN: 4321")
            }

            val scrollView = ScrollView(this)
            val view = TextView(this).apply {
                text = secret
                textSize = 28f
                setPadding(48, 48, 48, 48)
            }
            scrollView.addView(view)
            setContentView(scrollView)
        }
    }
}

package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast

private const val PREFS_NAME = "mastg_demo_prefs"
private const val KEY_SENSITIVE_SWITCH = "sensitive_switch"
private const val KEY_SET_BY = "sensitive_set_by"

/**
 * Vulnerable deep-link Activity that triggers a sensitive action (toggle on/off)
 * without any user confirmation or authentication.
 *
 * Deep link examples:
 *  - mastestapp://toggle?state=on
 *  - mastestapp://toggle?state=off
 */
class DeepLinkActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val data: Uri? = intent?.data
            val stateParam = data?.getQueryParameter("state")?.lowercase()
            val newState = when (stateParam) {
                "on", "1", "true", "enable" -> true
                "off", "0", "false", "disable" -> false
                else -> null
            }

            if (newState != null) {
                val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit()
                    .putBoolean(KEY_SENSITIVE_SWITCH, newState)
                    .putString(KEY_SET_BY, "deeplink")
                    .apply()

                Toast.makeText(
                    this,
                    "Sensitive switch set ${if (newState) "ON" else "OFF"} via deep link",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Missing or invalid 'state' parameter (use on/off)",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Log.e("MASTG-DEMO", "DeepLinkActivity error: ${e.message}", e)
        }

        // Optionally bring user to the main screen so they can inspect the result.
        try {
            startActivity(Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        } catch (_: Exception) { }

        finish()
    }
}

class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        val r = DemoResults("DEEPLINK-01")

        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val isOn = prefs.getBoolean(KEY_SENSITIVE_SWITCH, false)
            val setBy = prefs.getString(KEY_SET_BY, "unknown")

            r.add(
                Status.PASS,
                "A vulnerable custom deep link is registered: mastestapp://toggle?state=on|off. Opening it toggles a sensitive switch with no validation."
            )

            if (setBy == "deeplink") {
                r.add(
                    Status.FAIL,
                    "Sensitive action was triggered via deep link. Switch is ${if (isOn) "ON" else "OFF"}."
                )
            } else {
                r.add(
                    Status.PASS,
                    "Current switch state: ${if (isOn) "ON" else "OFF"}. It has not been changed via deep link yet."
                )
            }
        } catch (e: Exception) {
            r.add(Status.ERROR, e.toString())
        }
        return r.toJson()
    }
}

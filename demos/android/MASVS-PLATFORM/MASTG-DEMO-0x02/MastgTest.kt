package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast

private const val PREFS_NAME = "mastg_demo_prefs"
private const val KEY_TWO_FACTOR_ENABLED = "two_factor_enabled"
private const val KEY_SET_BY = "two_factor_set_by"

/**
 * Vulnerable App Link Activity.
 *
 * The manifest registers the http(s)://deeplink.example.com App Link WITHOUT
 * android:autoVerify="true", so Android never verifies domain ownership against
 * the site's /.well-known/assetlinks.json. Because the link is unverified, any
 * other app can declare the same intent filter and be chosen to handle (hijack)
 * the link. Opening it performs a sensitive action — disabling two-factor
 * authentication — with no verification or user confirmation.
 *
 * Deep link examples:
 *  - https://deeplink.example.com/security?twofa=off
 *  - https://deeplink.example.com/security?twofa=on
 */
class DeepLinkActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val data: Uri? = intent?.data
            val twofaParam = data?.getQueryParameter("twofa")?.lowercase()
            val enable = when (twofaParam) {
                "on", "1", "true", "enable" -> true
                "off", "0", "false", "disable" -> false
                else -> null
            }

            if (enable != null) {
                val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit()
                    .putBoolean(KEY_TWO_FACTOR_ENABLED, enable)
                    .putString(KEY_SET_BY, "deeplink")
                    .apply()

                Toast.makeText(
                    this,
                    "Two-factor authentication turned ${if (enable) "ON" else "OFF"} via deep link",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Missing or invalid 'twofa' parameter (use on/off)",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Log.e("MASTG-DEMO", "DeepLinkActivity error: ${e.message}", e)
        }

        // Bring the user to the main screen so the result can be inspected.
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
        val r = DemoResults("AUTOVERIFY-01")

        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val twoFactorEnabled = prefs.getBoolean(KEY_TWO_FACTOR_ENABLED, true)
            val setBy = prefs.getString(KEY_SET_BY, "unknown")

            r.add(
                Status.PASS,
                "An App Link (https://deeplink.example.com) is registered WITHOUT android:autoVerify=\"true\". Because the link is unverified, any app can claim it, and opening it changes a sensitive security setting with no verification."
            )

            if (setBy == "deeplink") {
                r.add(
                    Status.FAIL,
                    "A sensitive security setting was changed via an unverified deep link. Two-factor authentication is now ${if (twoFactorEnabled) "ON" else "OFF"}."
                )
            } else {
                r.add(
                    Status.PASS,
                    "Current two-factor authentication state: ${if (twoFactorEnabled) "ON" else "OFF"}. It has not been changed via deep link yet."
                )
            }
        } catch (e: Exception) {
            r.add(Status.ERROR, e.toString())
        }
        return r.toJson()
    }
}

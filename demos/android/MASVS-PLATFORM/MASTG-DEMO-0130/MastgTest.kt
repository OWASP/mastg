// SUMMARY: This sample implements a small password vault. Tapping Start opens VaultActivity,
// which shows the password currently stored in the app. The app also declares
// PasswordResetReceiver, an exported broadcast receiver that changes the stored password from
// an unvalidated intent extra and logs the old password. Any app can send the broadcast to
// reset the password; tapping Refresh in VaultActivity then shows the new value.
// Inspired by the receiver in the "Android Insecure Bank" app.

package org.owasp.mastestapp

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MastgTest(private val context: Context) {

    companion object {
        const val PREFS = "secure_prefs"
        const val KEY_PASSWORD_STORE = "vault_password"
        const val DEFAULT_PASSWORD = "originalPass123"
    }

    fun mastgTest(): String {
        // Seed the stored password the first time the demo runs.
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.contains(KEY_PASSWORD_STORE)) {
            prefs.edit().putString(KEY_PASSWORD_STORE, DEFAULT_PASSWORD).apply()
        }

        // Open the legitimate vault screen, which displays the stored password.
        context.startActivity(
            Intent(context, VaultActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
        return "Opening the password vault…"
    }

    // Legitimate UI: shows the password currently stored in the vault.
    // Not exported. Tap Refresh after running the attack to see the value change.
    class VaultActivity : Activity() {

        private lateinit var status: TextView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            actionBar?.hide()

            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(64, 120, 64, 64)
            }

            val title = TextView(this).apply {
                text = "MASTestApp – Password Vault"
                textSize = 22f
            }

            status = TextView(this).apply {
                textSize = 18f
                setPadding(0, 48, 0, 48)
            }

            val refresh = Button(this).apply {
                text = "Refresh"
                setOnClickListener { showPassword() }
            }

            layout.addView(title)
            layout.addView(status)
            layout.addView(refresh)
            setContentView(layout)

            showPassword()
        }

        override fun onResume() {
            super.onResume()
            showPassword()
        }

        private fun showPassword() {
            val pwd = getSharedPreferences(MastgTest.PREFS, Context.MODE_PRIVATE)
                .getString(MastgTest.KEY_PASSWORD_STORE, "")
            status.text = "Current vault password:\n\n$pwd"
        }
    }

    // FAIL: [MASTG-TEST-0366] PasswordResetReceiver is exported and does not need any special permissions.
    // External callers, like other apps including adb, can send the broadcast and reset the password.
    class PasswordResetReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val newPassword = intent.getStringExtra("newpass") ?: return
            val prefs = context.getSharedPreferences(MastgTest.PREFS, Context.MODE_PRIVATE)
            val oldPassword = prefs.getString(MastgTest.KEY_PASSWORD_STORE, "")
            Log.d("MASTG-DEMO", "Password changed from $oldPassword to $newPassword")
            prefs.edit().putString(MastgTest.KEY_PASSWORD_STORE, newPassword).apply()
        }
    }
}

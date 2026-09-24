// SUMMARY: This sample implements a small password vault. Tapping Start opens VaultActivity,
// which shows the password currently stored in the app. Instead of declaring its receivers in
// the manifest, the app registers them at runtime (context-registered). VaultActivity registers
// PasswordResetReceiver with RECEIVER_EXPORTED, so any app can send the broadcast and reset the
// password, and VaultRefreshReceiver with RECEIVER_NOT_EXPORTED, which only the app itself can
// trigger. The in-app-only AdminActivity registers AdminCommandReceiver, which can wipe the
// vault, with RECEIVER_EXPORTED but restricted with a signature-level broadcastPermission, so
// only same-signer apps can reach it. Tapping Refresh in VaultActivity then shows the new value.

package org.owasp.mastestapp

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

class MastgTest(private val context: Context) {

    companion object {
        const val PREFS = "secure_prefs"
        const val KEY_PASSWORD_STORE = "vault_password"
        const val DEFAULT_PASSWORD = "originalPass123"
        const val ACTION_RESET_PASSWORD = "org.owasp.mastestapp.RESET_PASSWORD"
        const val ACTION_VAULT_UPDATED = "org.owasp.mastestapp.VAULT_UPDATED"
        const val ACTION_ADMIN_COMMAND = "org.owasp.mastestapp.ADMIN_COMMAND"
        const val PERMISSION_ADMIN_COMMAND = "org.owasp.mastestapp.ADMIN_COMMAND_PERMISSION"
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

    // Legitimate UI: shows the password currently stored in the vault. It registers its broadcast
    // receivers at runtime instead of declaring them in the manifest, so they don't appear there.
    class VaultActivity : Activity() {

        private lateinit var status: TextView
        private val passwordResetReceiver = PasswordResetReceiver()
        private val vaultRefreshReceiver = VaultRefreshReceiver()

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
            val admin = Button(this).apply {
                text = "Admin"
                setOnClickListener {
                    startActivity(Intent(this@VaultActivity, AdminActivity::class.java))
                }
            }
            layout.addView(title)
            layout.addView(status)
            layout.addView(refresh)
            layout.addView(admin)
            setContentView(layout)

            // FAIL: [MASTG-TEST-0366] Registered as exported, so any app on the device can deliver
            // the RESET_PASSWORD broadcast and reset the vault password.
            ContextCompat.registerReceiver(
                this,
                passwordResetReceiver,
                IntentFilter(ACTION_RESET_PASSWORD),
                ContextCompat.RECEIVER_EXPORTED
            )

            // PASS: Registered as not exported, so only broadcasts the app sends to itself can
            // reach it. It is not part of the external attack surface.
            ContextCompat.registerReceiver(
                this,
                vaultRefreshReceiver,
                IntentFilter(ACTION_VAULT_UPDATED),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )

            showPassword()
        }

        override fun onDestroy() {
            super.onDestroy()
            unregisterReceiver(passwordResetReceiver)
            unregisterReceiver(vaultRefreshReceiver)
        }

        private fun showPassword() {
            val pwd = getSharedPreferences(MastgTest.PREFS, Context.MODE_PRIVATE)
                .getString(MastgTest.KEY_PASSWORD_STORE, "")
            status.text = "Current vault password:\n\n$pwd"
        }
    }

    // Legitimate in-app admin screen, reachable only from within the app (not exported). It shows
    // sensitive data (the vault recovery key) and registers a context-registered receiver that can
    // wipe the vault.
    class AdminActivity : Activity() {

        private val adminCommandReceiver = AdminCommandReceiver()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            actionBar?.hide()

            val recoveryKey = getSharedPreferences(MastgTest.PREFS, Context.MODE_PRIVATE)
                .getString(MastgTest.KEY_PASSWORD_STORE, "")

            val view = TextView(this).apply {
                text = "ADMIN CONSOLE\n\nVault recovery key: $recoveryKey"
                textSize = 18f
                setPadding(64, 120, 64, 64)
            }
            setContentView(view)

            // PASS: Registered as exported, but restricted with a signature-level broadcastPermission,
            // so only apps signed with the same certificate can deliver the ADMIN_COMMAND broadcast.
            ContextCompat.registerReceiver(
                this,
                adminCommandReceiver,
                IntentFilter(ACTION_ADMIN_COMMAND),
                PERMISSION_ADMIN_COMMAND,
                null,
                ContextCompat.RECEIVER_EXPORTED
            )
        }

        override fun onDestroy() {
            super.onDestroy()
            unregisterReceiver(adminCommandReceiver)
        }
    }

    // FAIL: [MASTG-TEST-0366] Exposes a sensitive action (changing the stored password) and is
    // registered as exported, so external callers can trigger it and disclose the old password.
    class PasswordResetReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val newPassword = intent.getStringExtra("newpass") ?: return
            val prefs = context.getSharedPreferences(MastgTest.PREFS, Context.MODE_PRIVATE)
            val oldPassword = prefs.getString(MastgTest.KEY_PASSWORD_STORE, "")
            Log.d("MASTG-DEMO", "Password changed from $oldPassword to $newPassword")
            prefs.edit().putString(MastgTest.KEY_PASSWORD_STORE, newPassword).apply()
        }
    }

    // PASS: Registered as not exported and only refreshes the UI, exposing no sensitive action.
    class VaultRefreshReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("MASTG-DEMO", "Vault updated event received, refreshing UI")
        }
    }

    // PASS: Performs a sensitive action (wiping the vault) but is registered with a signature-level
    // broadcastPermission, so untrusted apps cannot deliver the broadcast that triggers it.
    class AdminCommandReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Logged on every delivery. If this line never appears after sending the broadcast,
            // the OS rejected the sender before onReceive ran (it lacked the broadcastPermission).
            Log.d("MASTG-DEMO", "AdminCommandReceiver received broadcast: ${intent.action}")
            if (intent.getStringExtra("command") != "wipe") return
            context.getSharedPreferences(MastgTest.PREFS, Context.MODE_PRIVATE)
                .edit().remove(MastgTest.KEY_PASSWORD_STORE).apply()
            Log.d("MASTG-DEMO", "Vault wiped by admin command")
        }
    }
}

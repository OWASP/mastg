package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle

// SUMMARY: This sample performs a sensitive action (a transfer) through an
// http/https App Link deep link. The input IS validated correctly, so the
// weakness is NOT input validation (that is covered by a separate test). The
// weakness is that the App Link is not verified: the manifest is missing
// android:autoVerify="true", so the OS never confirms that this app owns the
// deeplink.example.com domain. Another app can register the same domain to
// hijack the link (and any sensitive data it carries), or invoke this exported
// handler directly, to reach the sensitive action.

class DeepLinkActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lastDeepLink = intent?.data
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        })
        finish()
    }

    companion object {
        // Holds the URI that launched the app via the App Link.
        var lastDeepLink: Uri? = null
    }
}

class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        val data: Uri = DeepLinkActivity.lastDeepLink
            ?: return """
                No deep link processed yet.

                Trigger the unverified App Link with:
                adb shell am start -n org.owasp.mastestapp/.DeepLinkActivity -a android.intent.action.VIEW -d "https://deeplink.example.com/transfer?amount=100"

                Then press Start again to see the result.
            """.trimIndent()

        val amountParam = data.getQueryParameter("amount")

        // The input IS validated correctly: it must be a positive number within
        // an allowed range. This keeps the focus on the App Link verification
        // weakness, not on input validation.
        val amount = amountParam?.toLongOrNull()
        if (amount == null || amount <= 0 || amount > 10_000) {
            return "Rejected invalid amount: $amountParam"
        }

        return processTransfer(amount)
    }

    private fun processTransfer(amount: Long): String {
        // Sensitive action. Even though the amount is validated, this action is
        // reached through an unverified App Link and can be hijacked or
        // triggered by another app on the device.
        return "Transferred $amount units to the linked account"
    }
}

package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle

// SUMMARY: This sample demonstrates a custom URL scheme handler that uses a URL
// query parameter directly without validating it (no numeric type conversion
// and no bounds checking).

/**
 * Receives the custom URL scheme `mastestapp://transfer`. Any app on the device
 * can launch it, for example with:
 *
 *   adb shell am start -a android.intent.action.VIEW -d "mastestapp://transfer?amount=9999999"
 *
 * It remembers the launching URI and routes the user to MainActivity so the
 * deep link can be processed by tapping the Start button.
 */
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
        // Holds the URI that launched the app via the custom URL scheme.
        var lastDeepLink: Uri? = null
    }
}

class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        val data: Uri = DeepLinkActivity.lastDeepLink
            ?: return """
                No deep link processed yet.

                Trigger the vulnerable custom URL scheme handler with:
                adb shell am start -a android.intent.action.VIEW -d "mastestapp://transfer?amount=9999999"

                Then press Start again to see the result.
            """.trimIndent()

        val amount = data.getQueryParameter("amount")
            ?: return "Missing 'amount' parameter"

        // FAIL: [MASTG-TEST-0394] The handler uses the "amount" query parameter
        // directly without converting it to a numeric type (e.g. toLong()) or
        // checking it against any bounds. Any app can open
        // mastestapp://transfer?amount=9999999 or amount=-1 to bypass the
        // expected business logic constraints.
        return processTransfer(amount)
    }

    private fun processTransfer(amount: String): String {
        return "Transferring $amount units"
    }
}

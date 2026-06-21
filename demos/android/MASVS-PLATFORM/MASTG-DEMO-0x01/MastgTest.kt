package org.owasp.mastestapp

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity

/**
 * Vulnerable deep-link entry point.
 *
 * The app registers the `vulnerable-app://deeplink` scheme in the manifest and routes
 * it to this exported Activity. The Activity reads the attacker-controlled `url` query
 * parameter and loads it directly into a WebView with no validation.
 *
 * Trigger it with:
 *   adb shell am start -n org.owasp.mastestapp/.DeepLinkActivity \
 *       -a android.intent.action.VIEW \
 *       -d "vulnerable-app://deeplink?url=https://example.com"
 */
class DeepLinkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The deep link URI arrives as the Intent data. Hand it to the vulnerable sink.
        MastgTest(this).processDeepLinkAndLoad(intent?.data)
    }
}

class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        return """
            This app is vulnerable to deep link attacks.

            Test with:
            adb shell am start -n org.owasp.mastestapp/.DeepLinkActivity -a android.intent.action.VIEW -d "vulnerable-app://deeplink?url=https://example.com"
        """.trimIndent()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun processDeepLinkAndLoad(uri: Uri?) {
        if (uri == null) return

        val url = uri.getQueryParameter("url")
        if (url != null) {
            val webView = WebView(context)
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(url)
            (context as ComponentActivity).setContentView(webView)
        }
    }
}

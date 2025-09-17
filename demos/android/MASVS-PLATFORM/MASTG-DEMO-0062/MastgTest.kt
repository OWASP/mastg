package org.owasp.mastestapp

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.webkit.WebView
import androidx.activity.ComponentActivity

class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        return """
            This app is vulnerable to deep link attacks.

            Test with:
            adb shell am start -a android.intent.action.VIEW -d "vulnerable-app://deeplink?url=https://example.com"
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

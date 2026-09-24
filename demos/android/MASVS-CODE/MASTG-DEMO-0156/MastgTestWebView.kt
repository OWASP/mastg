package org.owasp.mastestapp

import android.content.Context
import android.os.Build
import android.util.Log
import android.webkit.SafeBrowsingResponse
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

// SUMMARY: This sample demonstrates an app that disables WebView SafeBrowsing at runtime, which takes precedence over the AndroidManifest setting.

class MastgTestWebView(private val context: Context) {

    fun mastgTest(webView: WebView) {
        webView.apply {

            // FAIL: [MASTG-TEST-0399] SafeBrowsing is disabled in code, overriding the manifest and removing protection against known malicious URLs.
            settings.safeBrowsingEnabled = false

            webViewClient = object : WebViewClient() {
                override fun onSafeBrowsingHit(
                    view: WebView,
                    request: WebResourceRequest,
                    threatType: Int,
                    callback: SafeBrowsingResponse
                ) {
                    Log.d("SafeBrowsing", "Blocked URL, ${request.url}, threatType, $threatType")

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                        callback.backToSafety(true)
                    } else {
                        super.onSafeBrowsingHit(view, request, threatType, callback)
                    }
                }
            }

            loadUrl("chrome://safe-browsing/match?type=phishing")
        }
    }
}

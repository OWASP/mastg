package org.owasp.mastestapp

import android.content.Context
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

// SUMMARY: This sample demonstrates a WebView with a custom WebViewClient that intercepts URL loading without proper validation.

class MastgTestWebView(private val context: Context) {

    fun mastgTest(webView: WebView): String {
        // Configure WebView settings
        webView.settings.apply {
            javaScriptEnabled = true
        }

        // FAIL: [MASTG-TEST-0398] Custom WebViewClient intercepts URL loading without proper validation
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString()
                // No URL validation is performed - any URL will be loaded
                Log.d("MastgTest", "Loading URL: $url")
                return false // Allow the WebView to load the URL
            }

            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): android.webkit.WebResourceResponse? {
                val url = request?.url?.toString()
                Log.d("MastgTest", "Intercepting request: $url")
                // No validation - allow all requests
                return super.shouldInterceptRequest(view, request)
            }
        }

        // Load a trusted page initially
        webView.loadUrl("https://mas.owasp.org/")

        return "WebView configured with custom URL handling"
    }
}

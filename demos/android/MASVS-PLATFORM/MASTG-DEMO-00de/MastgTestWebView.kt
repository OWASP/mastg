package org.owasp.mastestapp

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface

class MastgTestWebView (private val context: Context){

    // Insecure demo JS bridge for testing (do NOT use in production)
    inner class Bridge {

        //Affects i.e. confidentiality
        @JavascriptInterface
        fun getName(): String {
            return "John Doe"
        }

        //Affects i.e. confidentiality and/or integrity
        @JavascriptInterface
        fun getJwt(): String {
            return "header.payload.signature" // Dummy JWT for demo purposes
        }

        //Affects i.e. integrity
        @JavascriptInterface
        fun changeConfiguration(config: String) {
            // write to app configuration or disk
        }
    }

    fun mastgTest(webView: WebView) {
        // Intentionally insecure WebView demo
        val demoHtml = """
            <html>
            <body>
                <h1>Insecure WebView Demo</h1>
                <button onclick="showName()">Get Name</button>
                <button onclick="showJwt()">Get JWT</button>
                <button onclick="changeConfig()">Change Config</button>
                <p id="output"></p>
                
                <script>
                    function showName() {
                        var name = MASBridge.getName();
                        document.getElementById("output").innerText = "Name: " + name;
                    }
                    
                    function showJwt() {
                        var jwt = MASBridge.getJwt();
                        document.getElementById("output").innerText = "JWT: " + jwt;
                    }
                    
                    function changeConfig() {
                        MASBridge.changeConfiguration("newConfigValue");
                        document.getElementById("output").innerText = "Configuration Changed";
                    }
                </script>
            </body>
            </html>
            
        """.trimIndent()

        webView.apply {
            // Enable JavaScript (part of the failing conditions for the test)
            settings.javaScriptEnabled = true

            // Add an exposed JS interface (part of the failing conditions for the test)
            addJavascriptInterface(Bridge(), "MASBridge")

            // Basic client to keep navigation inside the WebView
            webViewClient = object : WebViewClient() {}

            // Load HTML under a cleartext base URL to emulate mixed conditions (requires usesCleartextTraffic=true to fetch network, but base URL is enough for origin in this inline demo)
            loadDataWithBaseURL(
                "http://insecure.example/", // intentional cleartext origin for the demo
                demoHtml,
                "text/html",
                "utf-8",
                null
            )
        }
    }

}

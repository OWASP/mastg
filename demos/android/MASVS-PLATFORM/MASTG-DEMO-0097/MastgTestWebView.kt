package org.owasp.mastestapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import org.json.JSONObject
import androidx.core.content.edit

class MastgTestWebView(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("support_demo_prefs", Context.MODE_PRIVATE)

    // Intentionally insecure demo bridge, do not use in production.
    inner class SupportBridge {

        @JavascriptInterface
        fun getUserProfileJson(): String {
            val name = prefs.getString("name", "") ?: ""
            val email = prefs.getString("email", "") ?: ""
            val jwt = prefs.getString("jwt", "") ?: ""

            return JSONObject().apply {
                put("name", name)
                put("email", email)
                put("jwt", jwt)
            }.toString()
        }

        @JavascriptInterface
        fun submitSupportMessage(email: String, message: String): String {
            return JSONObject().apply {
                put("status", "ok")
                put("email", email)
                put("message", message)
            }.toString()
        }

        @JavascriptInterface
        fun updateSupportPreference(value: String) {
            prefs.edit { putString("support_preference", value) }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun mastgTest(webView: WebView) {
        seedDemoSessionData()

        val demoHtml = """
            <!doctype html>
            <html>
            <head>
                <meta charset="utf-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1" />
                <title>Contact Support</title>
                <style>
                    body {
                        font-family: sans-serif;
                        margin: 0;
                        padding: 16px;
                        background: #ff444444;
                    }
                    .card {
                        background: white;
                        border: 1px solid #d9d9d9;
                        border-radius: 10px;
                        padding: 16px;
                        max-width: 500px;
                        margin: 0 auto;
                    }
                    h1 {
                        margin-top: 0;
                        font-size: 22px;
                    }
                    label {
                        display: block;
                        margin-top: 12px;
                        margin-bottom: 6px;
                        font-weight: 600;
                    }
                    input, textarea {
                        width: 100%;
                        box-sizing: border-box;
                        padding: 10px;
                        border: 1px solid #bbb;
                        border-radius: 8px;
                        font-size: 14px;
                    }
                    textarea {
                        min-height: 160px;
                        resize: vertical;
                    }
                    .row {
                        display: flex;
                        gap: 8px;
                        margin-top: 12px;
                    }
                    button {
                        padding: 10px 14px;
                        border: 1px solid #999;
                        border-radius: 8px;
                        background: #fff;
                        cursor: pointer;
                    }
                    .hint {
                        color: #666;
                        font-size: 13px;
                        margin-top: 8px;
                    }
                    .hidden {
                        display: none;
                    }
                    .messageBox {
                        width: 100%;
                        box-sizing: border-box;
                        min-height: 180px;
                        padding: 10px;
                        border: 1px solid #bbb;
                        border-radius: 8px;
                        font-size: 14px;
                        background: white;
                        white-space: pre-wrap;
                        word-break: break-word;
                    }
                </style>
            </head>
            <body>
                <div class="card">
                    <div id="formView">
                        <h1>Contact Support</h1>
                        <p class="hint">Send a message to the support team.</p>

                        <label for="email">Email</label>
                        <input id="email" type="text" value="john.doe@example.com" />

                        <label for="message">Message</label>
                        <textarea id="message" placeholder="Describe your issue here"></textarea>

                        <div class="row">
                            <button onclick="sendMessage()">Send</button>
                        </div>
                    </div>

                    <div id="resultView" class="hidden">
                        <h1>Support</h1>
                        <div id="resultText" class="messageBox"></div>
                        <div class="row">
                            <button onclick="showForm()">Send another message</button>
                        </div>
                    </div>
                </div>

                <script>
                    function showForm() {
                        document.getElementById("resultView").classList.add("hidden");
                        document.getElementById("formView").classList.remove("hidden");
                        document.getElementById("email").value = "john.doe@example.com";
                        document.getElementById("message").value = "";
                        document.getElementById("resultText").innerHTML = "";
                    }

                    function sendMessage() {
                        const email = document.getElementById("email").value;
                        const message = document.getElementById("message").value;

                        MASBridge.submitSupportMessage(email, message);

                        document.getElementById("formView").classList.add("hidden");
                        document.getElementById("resultView").classList.remove("hidden");

                        // Intentionally insecure.
                        document.getElementById("resultText").innerHTML =
                            "Message sent, you'll soon hear from us in " + email;
                    }
                </script>
            </body>
            </html>
        """.trimIndent()

        webView.apply {
            settings.javaScriptEnabled = true
            addJavascriptInterface(SupportBridge(), "MASBridge")
            webViewClient = object : WebViewClient() {}
            loadDataWithBaseURL(
                "https://appassets.androidplatform.net/assets/support/",
                demoHtml,
                "text/html",
                "utf-8",
                null
            )
        }
    }

    private fun seedDemoSessionData() {
        prefs.edit {
            putString("name", "John Doe")
                .putString("email", "john.doe@example.com")
                .putString(
                    "jwt",
                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiZW1haWwiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsInJvbGUiOiJ1c2VyIiwiaWF0IjoxNzE2MjM5MDIyLCJleHAiOjE5OTk5OTk5OTksImF1ZCI6Im1hc3Rlc3RhcHAiLCJpc3MiOiJodHRwczovL2F1dGguZXhhbXBsZS5jb20ifQ.s5vXz8Q1w4m0l9B2x3G7g8J9k1N2p3Q4r5S6t7U8v9w"
                )
        }
    }
}
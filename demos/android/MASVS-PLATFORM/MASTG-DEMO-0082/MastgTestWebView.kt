package org.owasp.mastestapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient

class MastgTestWebView(private val context: Context) {

    @SuppressLint("SetJavaScriptEnabled")
    fun mastgTest(webView: WebView) {
        webView.apply {
            // Enable JS and DOM storage so the page can use localStorage
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
            }

            // Bridge object exposed to JS as "Android" so the page can request the app to close
            addJavascriptInterface(AndroidBridge(), "Android")

            // Inline HTML which writes sensitive data into localStorage, shows a countdown,
            // and calls Android.closeApp() when finished.
            val html = """
                <!doctype html>
                <html>
                <head>
                  <meta name="viewport" content="width=device-width,initial-scale=1.0" />
                  <style>
                    body{
                      background:#111;
                      color:#fff;
                      font-family:sans-serif;
                      margin:0;
                      padding:0;
                      text-align:center;
                    }
                    .msg{
                      font-size:20px;
                      margin:12px;
                      display:block;
                    }
                    .count{
                      font-size:30px;
                      margin-top:8px;
                      display:inline-block;
                    }
                  </style>                
                </head>
                <body>
                 <div class="msg">Stored a token and user's driving licence ID in Local Storage.</div>
                 <br />
                 <div class="msg">The app will now close in : <span id="count" class="count"></span></div>
                 <script>
                    // Store sensitive data in localStorage
                    try {
                      localStorage.setItem('sensitive_token', 'SECRET_TOKEN_123456');
                      localStorage.setItem('driving_license_id', 'DL-987654321');
                    } catch(e) { /* ignore */ }

                    var n = 10;
                    var el = document.getElementById('count');
                    if(el) el.innerText = n;
                    var t = setInterval(function(){
                      n--;
                      if(n<=0){
                        clearInterval(t);
                        if(el) el.innerText = 'Closing...';
                        try { Android.closeApp(); } catch(e) { /* ignore */ }
                      } else {
                        if(el) el.innerText = n;
                      }
                    }, 1000);
                  </script>
                </body>
                </html>
            """.trimIndent()

            // Load the HTML into the WebView. Use a base URL so localStorage origin is defined.
            loadDataWithBaseURL("https://mas.owasp.org/", html, "text/html", "utf-8", null)
        }
    }

    private inner class AndroidBridge {
        @Suppress("unused") // called from JS
        @JavascriptInterface
        fun closeApp() {
            val activity = context as? Activity ?: return
            activity.runOnUiThread {
                activity.finish()
            }
        }
    }
}

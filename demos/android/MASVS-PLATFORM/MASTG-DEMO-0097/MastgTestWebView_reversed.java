package org.owasp.mastestapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.autofill.HintConstants;
import androidx.core.app.NotificationCompat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: MastgTestWebView.kt */
@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001:\u0001\rB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0007J\b\u0010\f\u001a\u00020\tH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lorg/owasp/mastestapp/MastgTestWebView;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "prefs", "Landroid/content/SharedPreferences;", "mastgTest", "", "webView", "Landroid/webkit/WebView;", "seedDemoSessionData", "SupportBridge", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTestWebView {
    public static final int $stable = 8;
    private final Context context;
    private final SharedPreferences prefs;

    public MastgTestWebView(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        SharedPreferences sharedPreferences = this.context.getSharedPreferences("support_demo_prefs", 0);
        Intrinsics.checkNotNullExpressionValue(sharedPreferences, "getSharedPreferences(...)");
        this.prefs = sharedPreferences;
    }

    /* compiled from: MastgTestWebView.kt */
    @Metadata(d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0007J\u0018\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\u0005H\u0007J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0005H\u0007¨\u0006\f"}, d2 = {"Lorg/owasp/mastestapp/MastgTestWebView$SupportBridge;", "", "<init>", "(Lorg/owasp/mastestapp/MastgTestWebView;)V", "getUserProfileJson", "", "submitSupportMessage", NotificationCompat.CATEGORY_EMAIL, "message", "updateSupportPreference", "", "value", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public final class SupportBridge {
        public SupportBridge() {
        }

        @JavascriptInterface
        public final String getUserProfileJson() throws JSONException {
            String name = MastgTestWebView.this.prefs.getString(HintConstants.AUTOFILL_HINT_NAME, "");
            if (name == null) {
                name = "";
            }
            String email = MastgTestWebView.this.prefs.getString(NotificationCompat.CATEGORY_EMAIL, "");
            if (email == null) {
                email = "";
            }
            String string = MastgTestWebView.this.prefs.getString("jwt", "");
            String jwt = string != null ? string : "";
            JSONObject $this$getUserProfileJson_u24lambda_u240 = new JSONObject();
            $this$getUserProfileJson_u24lambda_u240.put(HintConstants.AUTOFILL_HINT_NAME, name);
            $this$getUserProfileJson_u24lambda_u240.put(NotificationCompat.CATEGORY_EMAIL, email);
            $this$getUserProfileJson_u24lambda_u240.put("jwt", jwt);
            String string2 = $this$getUserProfileJson_u24lambda_u240.toString();
            Intrinsics.checkNotNullExpressionValue(string2, "toString(...)");
            return string2;
        }

        @JavascriptInterface
        public final String submitSupportMessage(String email, String message) throws JSONException {
            Intrinsics.checkNotNullParameter(email, "email");
            Intrinsics.checkNotNullParameter(message, "message");
            JSONObject $this$submitSupportMessage_u24lambda_u241 = new JSONObject();
            $this$submitSupportMessage_u24lambda_u241.put(NotificationCompat.CATEGORY_STATUS, "ok");
            $this$submitSupportMessage_u24lambda_u241.put(NotificationCompat.CATEGORY_EMAIL, email);
            $this$submitSupportMessage_u24lambda_u241.put("message", message);
            String string = $this$submitSupportMessage_u24lambda_u241.toString();
            Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
            return string;
        }

        @JavascriptInterface
        public final void updateSupportPreference(String value) {
            Intrinsics.checkNotNullParameter(value, "value");
            SharedPreferences $this$edit_u24default$iv = MastgTestWebView.this.prefs;
            SharedPreferences.Editor editor$iv = $this$edit_u24default$iv.edit();
            editor$iv.putString("support_preference", value);
            editor$iv.apply();
        }
    }

    public final void mastgTest(WebView webView) {
        Intrinsics.checkNotNullParameter(webView, "webView");
        seedDemoSessionData();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new SupportBridge(), "MASBridge");
        webView.setWebViewClient(new WebViewClient() { // from class: org.owasp.mastestapp.MastgTestWebView$mastgTest$1$1
        });
        webView.loadDataWithBaseURL("https://appassets.androidplatform.net/assets/support/", "<!doctype html>\n<html>\n<head>\n    <meta charset=\"utf-8\" />\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n    <title>Contact Support</title>\n    <style>\n        body {\n            font-family: sans-serif;\n            margin: 0;\n            padding: 16px;\n            background: #ff444444;\n        }\n        .card {\n            background: white;\n            border: 1px solid #d9d9d9;\n            border-radius: 10px;\n            padding: 16px;\n            max-width: 500px;\n            margin: 0 auto;\n        }\n        h1 {\n            margin-top: 0;\n            font-size: 22px;\n        }\n        label {\n            display: block;\n            margin-top: 12px;\n            margin-bottom: 6px;\n            font-weight: 600;\n        }\n        input, textarea {\n            width: 100%;\n            box-sizing: border-box;\n            padding: 10px;\n            border: 1px solid #bbb;\n            border-radius: 8px;\n            font-size: 14px;\n        }\n        textarea {\n            min-height: 160px;\n            resize: vertical;\n        }\n        .row {\n            display: flex;\n            gap: 8px;\n            margin-top: 12px;\n        }\n        button {\n            padding: 10px 14px;\n            border: 1px solid #999;\n            border-radius: 8px;\n            background: #fff;\n            cursor: pointer;\n        }\n        .hint {\n            color: #666;\n            font-size: 13px;\n            margin-top: 8px;\n        }\n        .hidden {\n            display: none;\n        }\n        .messageBox {\n            width: 100%;\n            box-sizing: border-box;\n            min-height: 180px;\n            padding: 10px;\n            border: 1px solid #bbb;\n            border-radius: 8px;\n            font-size: 14px;\n            background: white;\n            white-space: pre-wrap;\n            word-break: break-word;\n        }\n    </style>\n</head>\n<body>\n    <div class=\"card\">\n        <div id=\"formView\">\n            <h1>Contact Support</h1>\n            <p class=\"hint\">Send a message to the support team.</p>\n\n            <label for=\"email\">Email</label>\n            <input id=\"email\" type=\"text\" value=\"john.doe@example.com\" />\n\n            <label for=\"message\">Message</label>\n            <textarea id=\"message\" placeholder=\"Describe your issue here\"></textarea>\n\n            <div class=\"row\">\n                <button onclick=\"sendMessage()\">Send</button>\n            </div>\n        </div>\n\n        <div id=\"resultView\" class=\"hidden\">\n            <h1>Support</h1>\n            <div id=\"resultText\" class=\"messageBox\"></div>\n            <div class=\"row\">\n                <button onclick=\"showForm()\">Send another message</button>\n            </div>\n        </div>\n    </div>\n\n    <script>\n        function showForm() {\n            document.getElementById(\"resultView\").classList.add(\"hidden\");\n            document.getElementById(\"formView\").classList.remove(\"hidden\");\n            document.getElementById(\"email\").value = \"john.doe@example.com\";\n            document.getElementById(\"message\").value = \"\";\n            document.getElementById(\"resultText\").innerHTML = \"\";\n        }\n\n        function sendMessage() {\n            const email = document.getElementById(\"email\").value;\n            const message = document.getElementById(\"message\").value;\n\n            MASBridge.submitSupportMessage(email, message);\n\n            document.getElementById(\"formView\").classList.add(\"hidden\");\n            document.getElementById(\"resultView\").classList.remove(\"hidden\");\n\n            // Intentionally insecure.\n            document.getElementById(\"resultText\").innerHTML =\n                \"Message sent, you'll soon hear from us in \" + email;\n        }\n    </script>\n</body>\n</html>", "text/html", "utf-8", null);
    }

    private final void seedDemoSessionData() {
        SharedPreferences $this$edit_u24default$iv = this.prefs;
        SharedPreferences.Editor editor$iv = $this$edit_u24default$iv.edit();
        editor$iv.putString(HintConstants.AUTOFILL_HINT_NAME, "John Doe").putString(NotificationCompat.CATEGORY_EMAIL, "john.doe@example.com").putString("jwt", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiZW1haWwiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsInJvbGUiOiJ1c2VyIiwiaWF0IjoxNzE2MjM5MDIyLCJleHAiOjE5OTk5OTk5OTksImF1ZCI6Im1hc3Rlc3RhcHAiLCJpc3MiOiJodHRwczovL2F1dGguZXhhbXBsZS5jb20ifQ.s5vXz8Q1w4m0l9B2x3G7g8J9k1N2p3Q4r5S6t7U8v9w");
        editor$iv.apply();
    }
}

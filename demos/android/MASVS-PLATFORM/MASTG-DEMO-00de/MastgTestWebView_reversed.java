package org.owasp.mastestapp;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MastgTestWebView.kt */
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001:\u0001\nB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000b"}, d2 = {"Lorg/owasp/mastestapp/MastgTestWebView;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "webView", "Landroid/webkit/WebView;", "Bridge", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTestWebView {
    public static final int $stable = 8;
    private final Context context;

    public MastgTestWebView(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    /* compiled from: MastgTestWebView.kt */
    @Metadata(d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0007J\b\u0010\u0006\u001a\u00020\u0005H\u0007J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0005H\u0007¨\u0006\n"}, d2 = {"Lorg/owasp/mastestapp/MastgTestWebView$Bridge;", "", "<init>", "(Lorg/owasp/mastestapp/MastgTestWebView;)V", "getName", "", "getJwt", "changeConfiguration", "", "config", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public final class Bridge {
        public Bridge() {
        }

        @JavascriptInterface
        public final String getName() {
            return "John Doe";
        }

        @JavascriptInterface
        public final String getJwt() {
            return "header.payload.signature";
        }

        @JavascriptInterface
        public final void changeConfiguration(String config) {
            Intrinsics.checkNotNullParameter(config, "config");
        }
    }

    public final void mastgTest(WebView webView) {
        Intrinsics.checkNotNullParameter(webView, "webView");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new Bridge(), "MASBridge");
        webView.setWebViewClient(new WebViewClient() { // from class: org.owasp.mastestapp.MastgTestWebView$mastgTest$1$1
        });
        webView.loadDataWithBaseURL("http://insecure.example/", "<html>\n<body>\n    <h1>Insecure WebView Demo</h1>\n    <button onclick=\"showName()\">Get Name</button>\n    <button onclick=\"showJwt()\">Get JWT</button>\n    <button onclick=\"changeConfig()\">Change Config</button>\n    <p id=\"output\"></p>\n    \n    <script>\n        function showName() {\n            var name = MASBridge.getName();\n            document.getElementById(\"output\").innerText = \"Name: \" + name;\n        }\n        \n        function showJwt() {\n            var jwt = MASBridge.getJwt();\n            document.getElementById(\"output\").innerText = \"JWT: \" + jwt;\n        }\n        \n        function changeConfig() {\n            MASBridge.changeConfiguration(\"newConfigValue\");\n            document.getElementById(\"output\").innerText = \"Configuration Changed\";\n        }\n    </script>\n</body>\n</html>\n", "text/html", "utf-8", null);
    }
}

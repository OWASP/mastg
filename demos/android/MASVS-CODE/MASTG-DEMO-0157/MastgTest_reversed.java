package org.owasp.mastestapp;

import android.content.Context;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\t"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "mastgTest", "", "webView", "Landroid/webkit/WebView;", "app_debug"}, k = 1, mv = {1, 9, 0}, xi = 48)
/* loaded from: classes4.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest(WebView webView) {
        Intrinsics.checkNotNullParameter(webView, "webView");
        
        // Configure WebView settings
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        // FAIL: [MASTG-TEST-0398] Custom WebViewClient intercepts URL loading without proper validation
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request != null ? String.valueOf(request.getUrl()) : null;
                // No URL validation is performed - any URL will be loaded
                Log.d("MastgTest", "Loading URL: " + url);
                return false; // Allow the WebView to load the URL
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request != null ? String.valueOf(request.getUrl()) : null;
                Log.d("MastgTest", "Intercepting request: " + url);
                // No validation - allow all requests
                return super.shouldInterceptRequest(view, request);
            }
        });

        // Load a trusted page initially
        webView.loadUrl("https://mas.owasp.org/");

        return "WebView configured with custom URL handling";
    }
}

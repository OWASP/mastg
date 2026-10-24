package org.owasp.mastestapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: MastgTestWebView.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\n"}, d2 = {"Lorg/owasp/mastestapp/MastgTestWebView;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "webView", "Landroid/webkit/WebView;", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class MastgTestWebView {
    public static final int $stable = 8;
    private final Context context;

    public MastgTestWebView(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest(WebView webView) {
        Intrinsics.checkNotNullParameter(webView, "webView");
        WebSettings $this$mastgTest_u24lambda_u240 = webView.getSettings();
        $this$mastgTest_u24lambda_u240.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() { // from class: org.owasp.mastestapp.MastgTestWebView.mastgTest.2
            @Override // android.webkit.WebViewClient
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri url;
                String url2 = (request == null || (url = request.getUrl()) == null) ? null : url.toString();
                Log.d("MastgTest", "Loading URL: " + url2);
                return false;
            }

            @Override // android.webkit.WebViewClient
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Uri url;
                String url2 = (request == null || (url = request.getUrl()) == null) ? null : url.toString();
                Log.d("MastgTest", "Intercepting request: " + url2);
                return super.shouldInterceptRequest(view, request);
            }
        });
        webView.loadUrl("https://mas.owasp.org/");
        return "WebView configured with custom URL handling";
    }
}

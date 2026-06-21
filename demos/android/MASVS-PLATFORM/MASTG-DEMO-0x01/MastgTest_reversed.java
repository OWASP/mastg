package org.owasp.mastestapp;

import android.content.Context;
import android.net.Uri;
import android.webkit.WebView;
import androidx.activity.ComponentActivity;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J\u0012\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\f"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "processDeepLinkAndLoad", "", "uri", "Landroid/net/Uri;", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() {
        return "This app is vulnerable to deep link attacks.\n\nTest with:\nadb shell am start -n org.owasp.mastestapp/.DeepLinkActivity -a android.intent.action.VIEW -d \"vulnerable-app://deeplink?url=https://example.com\"";
    }

    public final void processDeepLinkAndLoad(Uri uri) {
        String url;
        if (uri != null && (url = uri.getQueryParameter("url")) != null) {
            WebView webView = new WebView(this.context);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(url);
            Context context = this.context;
            Intrinsics.checkNotNull(context, "null cannot be cast to non-null type androidx.activity.ComponentActivity");
            ((ComponentActivity) context).setContentView(webView);
        }
    }
}

package org.owasp.mastestapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

/* JADX INFO: compiled from: MastgTest.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u0000 \b2\u00020\u0001:\u0001\bB\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0014¨\u0006\t"}, d2 = {"Lorg/owasp/mastestapp/DeepLinkActivity;", "Landroid/app/Activity;", "<init>", "()V", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "Companion", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class DeepLinkActivity extends Activity {
    public static final int $stable = 0;

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static Uri lastDeepLink;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        lastDeepLink = intent != null ? intent.getData() : null;
        Intent $this$onCreate_u24lambda_u240 = new Intent(this, (Class<?>) MainActivity.class);
        $this$onCreate_u24lambda_u240.addFlags(335544320);
        startActivity($this$onCreate_u24lambda_u240);
        finish();
    }

    /* JADX INFO: compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\t¨\u0006\n"}, d2 = {"Lorg/owasp/mastestapp/DeepLinkActivity$Companion;", "", "<init>", "()V", "lastDeepLink", "Landroid/net/Uri;", "getLastDeepLink", "()Landroid/net/Uri;", "setLastDeepLink", "(Landroid/net/Uri;)V", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final Uri getLastDeepLink() {
            return DeepLinkActivity.lastDeepLink;
        }

        public final void setLastDeepLink(Uri uri) {
            DeepLinkActivity.lastDeepLink = uri;
        }
    }
}
